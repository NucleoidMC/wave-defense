package supercoder79.wavedefense.map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class WaveDefensePath {
    private final List<BlockPos> points;

    WaveDefensePath(List<BlockPos> points) {
        this.points = points;
    }

    public static WaveDefensePath generate(int totalLength, int segmentLength) {
        Random random = new Random();

        BlockPos point = BlockPos.ORIGIN;

        List<BlockPos> points = new ArrayList<>();
        points.add(point);

        int currentLength = 0;
        int maxDeltaX = segmentLength / 2;

        while (currentLength < totalLength) {
            int deltaX = MathHelper.nextInt(random, -maxDeltaX, maxDeltaX);
            int deltaZ = MathHelper.floor(Math.sqrt(segmentLength * segmentLength - deltaX * deltaX));

            point = point.add(deltaX, 0, deltaZ);
            points.add(point);

            currentLength += MathHelper.floor(Math.sqrt(deltaX * deltaX + deltaZ * deltaZ));
        }

        return new WaveDefensePath(points);
    }

    public int distanceToPath2(BlockPos pos) {
        int minDistance2 = Integer.MAX_VALUE;

        for (int i = 0; i < this.points.size() - 1; i++) {
            BlockPos start = this.points.get(i);
            BlockPos end = this.points.get(i + 1);

            int distance2 = distanceToSegment2(pos, start, end);
            if (distance2 < minDistance2) {
                minDistance2 = distance2;
            }
        }

        return minDistance2;
    }

    private static int distanceToSegment2(BlockPos pos, BlockPos a, BlockPos b) {
        int abx = b.getX() - a.getX();
        int abz = b.getZ() - a.getZ();

        int pax = a.getX() - pos.getX();
        int paz = a.getZ() - pos.getZ();

        int c = abx * pax + abz * paz;
        if (c > 0) {
            // we are closest to a
            return pax * pax + paz * paz;
        }

        int pbx = pos.getX() - b.getX();
        int pbz = pos.getZ() - b.getZ();

        if (abx * pbx + abz * pbz > 0) {
            // we are closest to b
            return pbx * pbx + pbz * pbz;
        }

        int l2 = abx * abx + abz * abz;
        int dx = pax - ((abx * c) / l2);
        int dz = paz - ((abz * c) / l2);

        return dx * dx + dz * dz;
    }

    public List<BlockPos> getPoints() {
        return this.points;
    }
}
