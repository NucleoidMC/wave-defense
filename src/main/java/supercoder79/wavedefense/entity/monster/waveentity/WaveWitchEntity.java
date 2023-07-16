package supercoder79.wavedefense.entity.monster.waveentity;

import org.joml.Vector3f;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.MonsterClass;
import supercoder79.wavedefense.game.WdActive;

public final class WaveWitchEntity extends WitchEntity implements WaveEntity {
    private final WdActive game;
    private MonsterClass monsterClass;

    public WaveWitchEntity(World world, WdActive game, MonsterClass monsterClass) {
        super(EntityType.WITCH, world);
        this.game = game;
        this.setMonsterClass(monsterClass);

        this.goalSelector.add(0, new MoveTowardGameCenterGoal<>(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(0, new ActiveTargetGoal<>(this, MobEntity.class, 1, false, false, e -> !(e instanceof WitchEntity)));

        this.setAttributes();
    }

    double particleSpawnY = 0;
    int state = 0;
    int stateTimer = 200;

    @Override
    public void tick() {
        super.tick();

        particleSpawnY += 0.1d;
        if (particleSpawnY > 1.5d)
            particleSpawnY = -0.5d;

        stateTimer--;
        if (stateTimer <= 0) {
            stateTimer = 200;
            state = random.nextInt(4);
        }

        float red = 1.0f;
        float green = 1.0f;
        float blue = 1.0f;
        float scale = 0.9f;

        switch (state) {
            // heal
            case 0:
                red = 1.0f;
                green = 0.5f;
                blue = 0.5f;
                break;

            // speed
            case 1:
                red = 0.5f;
                green = 1f;
                blue = 1f;
                scale = 0.7f;
                break;

            // invisibility
            case 2:
                red = 0.5f;
                green = 0.5f;
                blue = 1f;
                scale = 0.6f;
                break;

            // poison
            case 3:
                red = 0.5f;
                green = 1.0f;
                blue = 0.5f;
                scale = 0.8f;
                break;
        }

        if (particleSpawnY >= 0 && stateTimer % 2 == 0) {
            for (Entity entity : this.getWorld().getOtherEntities(this,
                    new Box(this.getPos().subtract(3, 3, 3),
                            this.getPos().add(3, 3, 3)), e -> !(e.equals(this)))) {

                MobEntity mob;
                PlayerEntity player;

                if (entity instanceof MobEntity) {
                    mob = (MobEntity) entity;

                    switch (state) {
                        case 0:
                            mob.heal(0.15f);
                            break;
                        case 1:
                            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60, 0));
                            break;
                        case 2:
                            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 15, 0));
                            stateTimer--;
                    }
                }
                else if (entity instanceof PlayerEntity) {
                    player = (PlayerEntity) entity;

                    if (state == 3) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 40, 1));
                    }
                }
            }
        }

        ((ServerWorld) this.getWorld()).spawnParticles(
                new DustParticleEffect(new Vector3f(red, green, blue), scale + stateTimer / 150f),
                this.getX(), this.getY() + particleSpawnY + 0.3, this.getZ(),
                2, 0.2, 0.0, 0.2, 0.1
        );
    }

    public void setAttributes() {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.getMonsterClass().maxHealth());
        this.setHealth((float) this.getMonsterClass().maxHealth());

        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
    }

    @Override
    public boolean showHealth() {
        return true;
    }

    @Override
    public int ironCount(Random random) {
        return this.getMonsterClass().ironCount(random) + this.getMod().ironBonus;
    }

    @Override
    public int goldCount(Random random) {
        return this.getMonsterClass().goldCount(random);
    }

    @Override
    public int monsterScore() {
        return this.getMonsterClass().monsterPoints();
    }

    @Override
    public WdActive getGame() {
        return game;
    }

    @Override
    public MonsterClass getMonsterClass() {
        return monsterClass;
    }

    public void setMonsterClass(MonsterClass monsterClass) {
        this.monsterClass = monsterClass;
    }

    public MonsterModifier getMod() {
        return MonsterModifier.NORMAL;
    }

    public void setMod(MonsterModifier mod) {

    }
}
