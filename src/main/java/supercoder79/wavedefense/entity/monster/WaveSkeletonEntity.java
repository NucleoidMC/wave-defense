package supercoder79.wavedefense.entity.monster;

import supercoder79.wavedefense.entity.MonsterModifier;
import supercoder79.wavedefense.entity.WaveEntity;
import supercoder79.wavedefense.entity.goal.MoveTowardGameCenterGoal;
import supercoder79.wavedefense.entity.monster.classes.SkeletonClass;
import supercoder79.wavedefense.game.WdActive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WaveSkeletonEntity extends SkeletonEntity implements WaveEntity {
	private final WdActive game;
	private final MonsterModifier mod;
	private final SkeletonClass skeletonClass;

	public WaveSkeletonEntity(World world, WdActive game, MonsterModifier mod, SkeletonClass skeletonClass) {
		super(EntityType.SKELETON, world);
		this.game = game;
		this.mod = mod;
		this.skeletonClass = skeletonClass;

		skeletonClass.apply(this, mod, world.getRandom());

		this.initializeGoals();
		this.setAttributes();
	}

	@Override
	protected void initGoals() {

	}

	protected void initializeGoals() {
		this.goalSelector.add(1, new BowAttackGoal<>(this, this.skeletonClass.speed(), this.skeletonClass.attackInterval(), this.skeletonClass.range()));
		this.goalSelector.add(2, new MoveTowardGameCenterGoal<>(this));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this, WaveZombieEntity.class, WaveSkeletonEntity.class));
		this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
	}

	public void setAttributes() {
		this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(this.skeletonClass.maxHealth());
		this.setHealth((float) this.skeletonClass.maxHealth());
	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		ItemStack itemStack = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
		PersistentProjectileEntity arrowProjectile = this.createArrowProjectile(itemStack, pullProgress);
		arrowProjectile.setDamage(arrowProjectile.getDamage() * this.skeletonClass.damageScale());

		// Add modifier effect
		if (this.mod.effect != null) {
			((ArrowEntity) arrowProjectile).addEffect(this.mod.effect.get());
		}

		double xDist = target.getX() - this.getX();
		double yDist = target.getBodyY(0.3333333333333333D) - arrowProjectile.getY();
		double zDist = target.getZ() - this.getZ();
		double yScale = MathHelper.sqrt(xDist * xDist + zDist * zDist);

		arrowProjectile.setVelocity(xDist, yDist + yScale * 0.20000000298023224D, zDist, this.skeletonClass.arrowSpeed(), this.skeletonClass.arrowDivergence());
		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

		this.world.spawnEntity(arrowProjectile);
	}

	@Override
	public int ironCount() {
		return this.skeletonClass.ironCount() + this.mod.ironBonus;
	}

	@Override
	public WdActive getGame() {
		return game;
	}
}
