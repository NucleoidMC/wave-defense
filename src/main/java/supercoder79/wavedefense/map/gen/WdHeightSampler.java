package supercoder79.wavedefense.map.gen;

import java.util.Arrays;
import java.util.Random;

import it.unimi.dsi.fastutil.HashCommon;
import kdotjpg.opensimplex.OpenSimplexNoise;
import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.biome.FakeBiomeSource;

import net.minecraft.util.math.ChunkPos;

public final class WdHeightSampler {
    private final ThreadLocal<HeightCache> heightCache;
    private final ThreadLocal<BiomeCache> biomeCache;
    public WdHeightSampler(WdPath path, FakeBiomeSource source, long seed) {
        biomeCache = ThreadLocal.withInitial(() -> new BiomeCache(source));
        heightCache = ThreadLocal.withInitial(() -> new HeightCache(path, biomeCache, seed));
    }

    public double sampleHeight(int x, int z) {
        return heightCache.get().getHeight(x, z);
    }

    public double sampleSlope(int x, int z) {
        HeightCache cache = heightCache.get();

        double dx = cache.getHeight(x - 1, z) - cache.getHeight(x + 1, z);
        double dz = cache.getHeight(x, z - 1) - cache.getHeight(x, z + 1);
        return dx * dx + dz * dz;
    }

    // Gets the height at a position from the cache, if possible.
    // Not thread safe.
    private static final class HeightCache {
        private static final int CACHE_SIZE = 1024, MASK = 1023;
        private final OpenSimplexNoise ridgeNoise;
        private final OpenSimplexNoise baseNoise;
        private final OpenSimplexNoise detailNoise;

        private final WdPath path;
        private final ThreadLocal<BiomeCache> biomeCache;
        private final long seed;

        private final long[] keys;
        private final double[] values;

        public HeightCache(WdPath path, ThreadLocal<BiomeCache> biomeCache, long seed) {
            this.path = path;
            this.biomeCache = biomeCache;
            this.seed = seed;

            Random random = new Random(seed);
            this.ridgeNoise = new OpenSimplexNoise(random.nextLong());
            this.baseNoise = new OpenSimplexNoise(random.nextLong());
            this.detailNoise = new OpenSimplexNoise(random.nextLong());

            this.keys = new long[CACHE_SIZE];
            Arrays.fill(this.keys, Long.MIN_VALUE);
            this.values = new double[CACHE_SIZE];
        }

        public double getHeight(int x, int z) {
            long key = key(x, z);
            int idx = hash(key) & MASK;

            if (this.keys[idx] == key) {
                return this.values[idx];
            }

            double sampled = getHeightAt(x, z);
            this.values[idx] = sampled;
            this.keys[idx] = key;

            return sampled;
        }

        // Internal use only!
        private double getHeightAt(int x, int z) {
            double upperNoiseFactor = 0;
            double lowerNoiseFactor = 0;
            double detailFactor = 0;
            double weight = 0;

            for (int aX = -4; aX <= 4; aX++) {
                for (int aZ = -4; aZ < 4; aZ++) {
                    BiomeGen biome = biomeCache.get().getBiome(x + aX, z + aZ);
                    upperNoiseFactor += biome.upperNoiseFactor();
                    lowerNoiseFactor += biome.lowerNoiseFactor();
                    detailFactor += biome.detailFactor();

                    weight++;
                }
            }

            upperNoiseFactor /= weight;
            lowerNoiseFactor /= weight;
            detailFactor /= weight;

            int distanceToPath2 = this.path.distanceToPath2(x, z);
            double distanceToPath = Math.sqrt(distanceToPath2);

            // Create base terrain
            double noise = this.baseNoise.eval(x / 256.0, z / 256.0);
            noise *= noise > 0 ? upperNoiseFactor : lowerNoiseFactor;

            // Add small details to make the terrain less rounded
            noise += this.detailNoise.eval(x / 20.0, z / 20.0) * detailFactor;

            double ridgeFactor = Math.min(
                    Math.pow(distanceToPath / 64.0, 3.0),
                    1.0
            );

            if (ridgeFactor > 1e-2) {
                double ridgeNoise = this.ridgeNoise.eval(x / 512.0, z / 512.0);
                ridgeNoise = 1.0 - Math.abs(ridgeNoise);
                ridgeNoise = Math.pow(ridgeNoise, 5.0);

                noise += ridgeNoise * 64.0 * ridgeFactor;
            }

            return 54.0 + noise;
        }

        private static int hash(long key) {
            return (int) HashCommon.mix(key);
        }

        private static long key(int x, int z) {
            return ChunkPos.toLong(x, z);
        }
    }

    // Used to get a biome at a position.
    // Not thread safe.
    private static final class BiomeCache {
        private static final int CACHE_SIZE = 1024, MASK = 1023;

        private final FakeBiomeSource source;

        private final long[] keys;
        private final BiomeGen[] values;

        public BiomeCache(FakeBiomeSource source) {
            this.source = source;

            this.keys = new long[CACHE_SIZE];
            Arrays.fill(this.keys, Long.MIN_VALUE);
            this.values = new BiomeGen[CACHE_SIZE];
        }

        public BiomeGen getBiome(int x, int z) {
            long key = key(x, z);
            int idx = hash(key) & MASK;

            if (this.keys[idx] == key) {
                return this.values[idx];
            }

            BiomeGen sampled = source.getRealBiome(x, z);
            this.values[idx] = sampled;
            this.keys[idx] = key;

            return sampled;
        }

        private static int hash(long key) {
            return (int) HashCommon.mix(key);
        }

        private static long key(int x, int z) {
            return ChunkPos.toLong(x, z);
        }
    }
}
