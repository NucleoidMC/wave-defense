package supercoder79.wavedefense.map.gen;

import java.util.Random;

import kdotjpg.opensimplex.OpenSimplexNoise;
import supercoder79.wavedefense.map.biome.BiomeGen;
import supercoder79.wavedefense.map.biome.FakeBiomeSource;

public final class WaveDefenseHeightSampler {
    private final OpenSimplexNoise ridgeNoise;
    private final OpenSimplexNoise baseNoise;
    private final OpenSimplexNoise detailNoise;

    private final WaveDefensePath path;
    private final FakeBiomeSource source;

    public WaveDefenseHeightSampler(WaveDefensePath path, FakeBiomeSource source, long seed) {
        this.path = path;
        this.source = source;

        Random random = new Random(seed);
        this.ridgeNoise = new OpenSimplexNoise(random.nextLong());
        this.baseNoise = new OpenSimplexNoise(random.nextLong());
        this.detailNoise = new OpenSimplexNoise(random.nextLong());
    }

    public double sampleHeight(int x, int z) {
        double upperNoiseFactor = 0;
        double lowerNoiseFactor = 0;
        double detailFactor = 0;
        double weight = 0;

        for (int aX = -4; aX <= 4; aX++) {
            for (int aZ = -4; aZ < 4; aZ++) {
                BiomeGen biome = source.getRealBiome(x + aX, z + aZ);
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

        return 55.0 + noise;
    }

    public double sampleSlope(int x, int z) {
        double dx = this.sampleHeight(x - 1, z) - this.sampleHeight(x + 1, z);
        double dz = this.sampleHeight(x, z - 1) - this.sampleHeight(x, z + 1);
        return Math.sqrt(dx * dx + dz * dz);
    }
}
