package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.util.RandomCollection;

public interface PhantomClass extends MonsterClass {
    void apply(Mob entity, MonsterModifier mod, RandomSource random, int waveOrdinal);

    default int size() {
        return 1;
    }

    static PhantomClass next(int waveOrdinal) {
        RandomCollection<PhantomClass> phantomClasses = new RandomCollection<>();
        phantomClasses
                .add(10, PhantomClasses.DEFAULT)
                .add(Math.min(5, waveOrdinal / 2d - 15), PhantomClasses.LARGE);

        return phantomClasses.next();
    }
}
