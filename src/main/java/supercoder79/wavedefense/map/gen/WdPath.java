package supercoder79.wavedefense.map.gen;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class WdPath {
    private final List<BlockPos> points;
    private final DoubleList pointDistances;
    private final double length;

    WdPath(List<BlockPos> points, DoubleList pointDistances, double length) {
        this.points = points;
        this.pointDistances = pointDistances;
        this.length = length;
    }

    public static WdPath generate(int totalLength, int segmentLength) {
        Random random = new Random();

        BlockPos point = BlockPos.ORIGIN;

        List<BlockPos> points = new ArrayList<>();
        DoubleList pointDistances = new DoubleArrayList();

        points.add(point);
        pointDistances.add(0.0);

        double currentLength = 0;
        int maxDeltaX = segmentLength / 2;

        while (currentLength < totalLength) {
            int deltaX = MathHelper.nextInt(random, -maxDeltaX, maxDeltaX);
            int deltaZ = MathHelper.floor(Math.sqrt(segmentLength * segmentLength - deltaX * deltaX));
            double length = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            point = point.add(deltaX, 0, deltaZ);
            points.add(point);

            currentLength += length;
            pointDistances.add(currentLength);
        }

        return new WdPath(points, pointDistances, currentLength);
    }

    public int distanceToPath2(int x, int z) {
        int minDistance2 = Integer.MAX_VALUE;

        for (int i = 0; i < this.points.size() - 1; i++) {
            BlockPos start = this.points.get(i);
            BlockPos end = this.points.get(i + 1);

            int distance2 = distanceToSegment2(start, end, x, z);
            if (distance2 < minDistance2) {
                minDistance2 = distance2;
            }
        }

        return minDistance2;
    }

    public double distanceAlongPath(double x, double z) {
        int minDistance2 = Integer.MAX_VALUE;
        int segmentIndex = -1;

        int ix = MathHelper.floor(x);
        int iz = MathHelper.floor(z);

        for (int i = 0; i < this.points.size() - 1; i++) {
            BlockPos start = this.points.get(i);
            BlockPos end = this.points.get(i + 1);

            int distance2 = distanceToSegment2(start, end, ix, iz);
            if (distance2 < minDistance2) {
                minDistance2 = distance2;
                segmentIndex = i;
            }
        }

        BlockPos start = this.points.get(segmentIndex);
        BlockPos end = this.points.get(segmentIndex + 1);
        double distance = this.pointDistances.getDouble(segmentIndex);

        return (distance + distanceAlongSegment(start, end, x, z)) / this.length;
    }

    private static int distanceToSegment2(BlockPos a, BlockPos b, int px, int pz) {
        int mx = b.getX() - a.getX();
        int mz = b.getZ() - a.getZ();

        int vx = px - a.getX();
        int vz = pz - a.getZ();

        // squared length of the line segment
        int m2 = mx * mx + mz * mz;

        // orthogonally project V (p-a) onto M (b-a)

        // squared distance along the line segment
        int t2 = vx * mx + vz * mz;

        if (t2 <= 0) {
            // P is closest to A: return square length of P-A
            return vx * vx + vz * vz;
        }

        if (t2 >= m2) {
            // P is closest to B: return square length of P-B
            int dx = px - b.getX();
            int dz = pz - b.getZ();
            return dx * dx + dz * dz;
        }

        // find the point C along the line that we are closest to
        int cx = a.getX() + (mx * t2) / m2;
        int cz = a.getZ() + (mz * t2) / m2;

        int dx = px - cx;
        int dz = pz - cz;

        return dx * dx + dz * dz;
    }

    private static double distanceAlongSegment(BlockPos a, BlockPos b, double px, double pz) {
        int mx = b.getX() - a.getX();
        int mz = b.getZ() - a.getZ();

        double vx = px - a.getX();
        double vz = pz - a.getZ();

        // squared length of the line segment
        int m2 = mx * mx + mz * mz;

        // distance along line segment in range [0; 1]
        return (vx * mx + vz * mz) / m2;
    }

    public List<BlockPos> getPoints() {
        return this.points;
    }

    public Vec3d getPointAlong(double progress) {
        double x = progress * this.points.size();

        int index = MathHelper.floor(x);
        if (index < 0) {
            return Vec3d.ofCenter(this.points.get(0));
        } else if (index >= this.points.size() - 1) {
            return Vec3d.ofCenter(this.points.get(this.points.size() - 1));
        }

        double mid = x - index;

        BlockPos start = this.points.get(index);
        BlockPos end = this.points.get(index + 1);

        return new Vec3d(
                start.getX() + (end.getX() - start.getX()) * mid,
                0.0,
                start.getZ() + (end.getZ() - start.getZ()) * mid
        );
    }

    public double getLength() {
        return this.length;
    }
}
