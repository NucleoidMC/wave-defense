package supercoder79.wavedefense.entity.monster.classes;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.random.Random;
import supercoder79.wavedefense.entity.EquipmentHelper;
import supercoder79.wavedefense.entity.MonsterModifier;

public final class MonsterClasses {
	public static final MonsterClass DEFAULT = new MonsterClass() {
		private int iron;

		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
			iron = 1;

			if (random.nextInt(100) >= Math.min(-3 + waveOrdinal, 33))
				return;

			iron = EquipmentHelper.equipBoots(waveOrdinal, random, iron, entity, -2);
			if (!random.nextBoolean())
				return;

			iron = EquipmentHelper.equipLeggings(waveOrdinal, random, iron, entity, -2);
			if (!random.nextBoolean())
				return;

			iron = EquipmentHelper.equipChestplate(waveOrdinal, random, iron, entity, -2);
			if (!random.nextBoolean())
				return;

			iron = EquipmentHelper.equipHelmet(waveOrdinal, random, iron, entity, -2);
		}

		@Override
		public int ironCount(Random random) {
			return iron;
		}

		@Override
		public int goldCount(Random random) {
			return 0;
		}

		@Override
		public int monsterPoints() {
			return 2;
		}

		@Override
		public String name() {
			return "Zombie";
		}
	};



	public static final MonsterClass KNIGHT = new MonsterClass() {
		private int iron;

		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
			iron = 4;

			iron = EquipmentHelper.equipSword(waveOrdinal, random, iron, entity, 7);
			iron = EquipmentHelper.equipHelmet(waveOrdinal, random, iron, entity, 4);
			iron = EquipmentHelper.equipChestplate(waveOrdinal, random, iron, entity, 4);
			iron = EquipmentHelper.equipLeggings(waveOrdinal, random, iron, entity, 4);
			iron = EquipmentHelper.equipBoots(waveOrdinal, random, iron, entity, 4);
		}

		@Override
		public double maxHealth() {
			return 22.0;
		}

		@Override
		public double speed() {
			return 1.05;
		}

		@Override
		public String name() {
			return "Knight";
		}

		@Override
		public int ironCount(Random random) {
			return iron;
		}

		@Override
		public int goldCount(Random random) {
			return random.nextInt(12) == 0 ? 1 : 0;
		}

		@Override
		public int monsterPoints() {
			return 3;
		}
	};

	public static final MonsterClass TANK = new MonsterClass() {
		private int iron;

		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
			iron = 7;
			iron = EquipmentHelper.equipSword(waveOrdinal, random, iron, entity, 4);
			iron = EquipmentHelper.equipHelmet(waveOrdinal, random, iron, entity, 8);
			iron = EquipmentHelper.equipChestplate(waveOrdinal, random, iron, entity, 8);
			iron = EquipmentHelper.equipLeggings(waveOrdinal, random, iron, entity, 8);
			iron = EquipmentHelper.equipBoots(waveOrdinal, random, iron, entity, 8);
		}

		@Override
		public double maxHealth() {
			return 35.0;
		}

		@Override
		public double speed() {
			return 0.75;
		}

		@Override
		public String name() {
			return "Tank";
		}

		@Override
		public int ironCount(Random random) {
			return iron;
		}

		@Override
		public int goldCount(Random random) {
			return random.nextInt(4) == 0 ? 1 : 0;
		}

		@Override
		public int monsterPoints() {
			return 4;
		}
	};

	public static final MonsterClass SCOUT = new MonsterClass() {
		private int iron;
		
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
			iron = 4;
			iron = EquipmentHelper.equipHelmet(waveOrdinal, random, iron, entity, 1);
			iron = EquipmentHelper.equipChestplate(waveOrdinal, random, iron, entity, 1);
			iron = EquipmentHelper.equipLeggings(waveOrdinal, random, iron, entity, 1);
		}

		@Override
		public double maxHealth() {
			return 18.0;
		}

		@Override
		public double speed() {
			return 1.3;
		}

		@Override
		public String name() {
			return "Scout";
		}

		@Override
		public int ironCount(Random random) {
			return iron;
		}

		@Override
		public int goldCount(Random random) {
			return random.nextInt(8) == 0 ? 1 : 0;
		}

		@Override
		public int monsterPoints() {
			return 3;
		}
	};

	public static final MonsterClass RUNNER = new MonsterClass() {
		private int iron;
		
		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
			iron = 2;
			iron = EquipmentHelper.equipSword(waveOrdinal, random, iron, entity, -1);
		}

		@Override
		public double maxHealth() {
			return 15.0;
		}

		@Override
		public double speed() {
			return 1.35;
		}

		@Override
		public String name() {
			return "Runner";
		}

		@Override
		public int ironCount(Random random) {
			return iron;
		}

		@Override
		public int goldCount(Random random) {
			return 0;
		}

		@Override
		public int monsterPoints() {
			return 3;
		}
	};

	public static final MonsterClass FIGHTER = new MonsterClass() {
		private int iron;

		@Override
		public void apply(MobEntity entity, MonsterModifier mod, Random random, int waveOrdinal) {
			iron = 2;

			iron = EquipmentHelper.equipSword(waveOrdinal, random, iron, entity, 1 + waveOrdinal / 4);

			if (random.nextBoolean()) {
				iron = EquipmentHelper.equipHelmet(waveOrdinal, random, iron, entity, 1);
			}
			if (random.nextBoolean()) {
				iron = EquipmentHelper.equipChestplate(waveOrdinal, random, iron, entity, 1);
			}
			if (random.nextBoolean()) {
				iron = EquipmentHelper.equipLeggings(waveOrdinal, random, iron, entity, 1);
			}
			if (random.nextBoolean()) {
				iron = EquipmentHelper.equipBoots(waveOrdinal, random, iron, entity, 1);
			}
		}

		@Override
		public double maxHealth() {
			return 24.0;
		}

		@Override
		public double speed() {
			return 1.0;
		}

		@Override
		public String name() {
			return "Fighter";
		}

		@Override
		public int ironCount(Random random) {
			return iron;
		}

		@Override
		public int goldCount(Random random) {
			return 0;
		}

		@Override
		public int monsterPoints() {
			return 3;
		}
	};
}
