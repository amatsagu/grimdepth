package amatsagu.grimdepth.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.entity.Mob;

public final class BackstepHelper {

	public static void tryTrigger(ServerLevel level, LivingEntity shooter, ItemStack weapon) {
		LivingEntity target = (shooter instanceof Mob mob) ? mob.getTarget() : null;
		tryTrigger(level, shooter, weapon, target);
	}

	public static void tryTrigger(ServerLevel level, LivingEntity shooter, ItemStack weapon, LivingEntity target) {
		GrimdepthConfig.BackstepConfig cfg = GrimdepthConfig.INSTANCE.backstep;
		if (!cfg.enabled || shooter == null || weapon == null || weapon.isEmpty()) {
			return;
		}
		if (shooter.isPassenger() || shooter.isFallFlying()) {
			return;
		}

		int levelNum = GrimdepthEnchantments.getBackstepLevel(level, weapon);
		if (levelNum <= 0) {
			return;
		}

		// Calculate horizontal opposite direction of aim
		double lookX, lookZ;
		if (target != null) {
			lookX = target.getX() - shooter.getX();
			lookZ = target.getZ() - shooter.getZ();
		} else {
			Vec3 look = shooter.getLookAngle();
			lookX = look.x;
			lookZ = look.z;
		}
		double horizDist = Math.sqrt(lookX * lookX + lookZ * lookZ);
		if (horizDist < 1e-4) {
			float yawRad = (float) Math.toRadians(shooter.getYRot());
			lookX = -Math.sin(yawRad);
			lookZ = Math.cos(yawRad);
			horizDist = Math.sqrt(lookX * lookX + lookZ * lookZ);
		}
		double dirX = -lookX / horizDist;
		double dirZ = -lookZ / horizDist;

		double pushBlocks = cfg.pushDistanceBlocks;
		if (cfg.scaleWithLevel && levelNum > 1) {
			pushBlocks += (levelNum - 1) * cfg.pushDistancePerLevel;
		}

		// Check safe floor if enabled
		if (cfg.requireSafeFloor && !isFloorSafe(level, shooter, dirX, dirZ, pushBlocks, cfg.maxSafeDropDistance)) {
			return;
		}

		// Gentle smooth recoil impulse:
		// Ground friction decelerates delta movement to ~2.2x the initial velocity.
		double impulse = pushBlocks * 0.42;
		double impulseY = shooter.onGround() ? 0.08 : 0.0;

		shooter.push(dirX * impulse, impulseY, dirZ * impulse);
		shooter.syncVelocity = true;
		shooter.needsSync = true;
		level.getChunkSource().sendToTrackingPlayersAndSelf(shooter, new ClientboundSetEntityMotionPacket(shooter));
		if (shooter instanceof ServerPlayer serverPlayer) {
			serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
		}

		// Grant 5s of movement speed effect with barely visible (ambient) particles
		shooter.addEffect(new MobEffectInstance(
				MobEffects.SPEED,
				cfg.speedDurationTicks,
				cfg.speedAmplifier,
				true,  // ambient = true: faint translucent swirls
				true,  // visible
				true   // showIcon
		));

		// Small smoke poof near their feet
		level.sendParticles(
				ParticleTypes.POOF,
				shooter.getX(),
				shooter.getY() + 0.05,
				shooter.getZ(),
				3,
				0.2, 0.05, 0.2,
				0.02
		);
		level.sendParticles(
				ParticleTypes.SMOKE,
				shooter.getX(),
				shooter.getY() + 0.05,
				shooter.getZ(),
				4,
				0.2, 0.05, 0.2,
				0.01
		);
	}

	private static boolean isFloorSafe(ServerLevel level, LivingEntity entity, double dirX, double dirZ, double distance, double maxDrop) {
		double px = entity.getX();
		double py = entity.getY();
		double pz = entity.getZ();

		// Sample halfway and at full distance
		double[] samples = { distance * 0.5, distance };
		for (double d : samples) {
			double sx = px + dirX * d;
			double sz = pz + dirZ * d;
			BlockPos samplePos = BlockPos.containing(sx, py, sz);

			// Check body and head space for deadly hazards
			if (isHazard(level.getBlockState(samplePos), level.getFluidState(samplePos))) {
				return false;
			}
			if (isHazard(level.getBlockState(samplePos.above()), level.getFluidState(samplePos.above()))) {
				return false;
			}

			// Check downward from feet for solid floor or water within maxDrop
			boolean floorFound = false;
			int maxDown = Math.max(1, (int) Math.ceil(maxDrop));
			for (int dy = 0; dy <= maxDown; dy++) {
				BlockPos checkPos = samplePos.below(dy);
				if (checkPos.getY() < level.getMinY()) {
					return false;
				}
				BlockState state = level.getBlockState(checkPos);
				FluidState fluid = level.getFluidState(checkPos);

				if (isHazard(state, fluid)) {
					return false;
				}

				if (!state.getCollisionShape(level, checkPos).isEmpty() || fluid.is(FluidTags.WATER)) {
					floorFound = true;
					break;
				}
			}

			if (!floorFound) {
				return false; // Cliff / sheer drop / abyss
			}
		}

		return true;
	}

	private static boolean isHazard(BlockState state, FluidState fluid) {
		if (fluid.is(FluidTags.LAVA)) {
			return true;
		}
		if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
			return true;
		}
		if (state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.CACTUS) || state.is(Blocks.SWEET_BERRY_BUSH) || state.is(Blocks.WITHER_ROSE)) {
			return true;
		}
		return state.getBlock() instanceof CampfireBlock;
	}
}
