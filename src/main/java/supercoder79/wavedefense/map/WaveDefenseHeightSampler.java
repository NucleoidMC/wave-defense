package supercoder79.wavedefense.map;

import kdotjpg.opensimplex.OpenSimplexNoise;

import java.util.Random;

public final class WaveDefenseHeightSampler {
    private final OpenSimplexNoise ridgeNoise;
    private final OpenSimplexNoise baseNoise;
    private final OpenSimplexNoise detailNoise;

    private final WaveDefensePath path;

    public WaveDefenseHeightSampler(WaveDefensePath path, long seed) {
        this.path = path;

        Random random = new Random(seed);
        this.ridgeNoise = new OpenSimplexNoise(random.nextLong());
        this.baseNoise = new OpenSimplexNoise(random.nextLong());
        this.detailNoise = new OpenSimplexNoise(random.nextLong());
    }

    public double sampleHeight(int x, int z) {
        int distanceToPath2 = this.path.distanceToPath2(x, z);
        double distanceToPath = Math.sqrt(distanceToPath2);

        // Create base terrain
        double noise = this.baseNoise.eval(x / 256.0, z / 256.0);
        noise *= noise > 0 ? 14 : 12;

        // Add small details to make the terrain less rounded
        noise += this.detailNoise.eval(x / 20.0, z / 20.0) * 3.25;

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
