package supercoder79.wavedefense.entity.monster.waveentity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.level.Level;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.CaveSpiderClasses;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public class WaveCaveSpiderEntity extends CaveSpider implements WaveEntity {
    private final WdActive game;
    private MonsterModifier mod;

    public WaveCaveSpiderEntity(Level world, WdActive game, MonsterClass monsterClass) {
        super(EntityTypes.CAVE_SPIDER, world);

        this.game = game;
        this.setMod(mod);

        this.goalSelector.addGoal(2, new MoveTowardGameCenterGoal<>(this));

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8);
        this.setHealth((float) this.getMonsterClass().maxHealth());
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        boolean didAttack = super.doHurtTarget(world, target);

        if (didAttack) {
            if (target instanceof LivingEntity && getMod().effect != null) {
                ((LivingEntity)target).addEffect(getMod().effect.get());
            }
        }

        return didAttack;
    }

    @Override
    public int ironCount(RandomSource random) {
        return this.getMonsterClass().ironCount(random) + this.getMod().ironBonus;
    }

    @Override
    public int goldCount(RandomSource random) {
        return this.getMonsterClass().goldCount(random);
    }

    @Override
    public int monsterScore() {
        return this.getMonsterClass().monsterPoints();
    }

    @Override
    public MonsterClass getMonsterClass() {
        return CaveSpiderClasses.DEFAULT;
    }

    @Override
    public WdActive getGame() {
        return game;
    }

    public MonsterModifier getMod() {
        return mod;
    }

    @Override
    public void setMod(MonsterModifier mod) {
        this.mod = mod;
    }
}
