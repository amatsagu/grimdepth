package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEnchantments;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

	private static final int FLAG_ARMOR_PIERCER = 4;

	@Shadow protected abstract boolean isInGround();

	@Inject(method = "tick", at = @At("HEAD"))
	private void grimdepth$syncArmorPiercerFlag(CallbackInfo ci) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (!self.level().isClientSide() && !self.entityTags().contains("grimdepth:checked_ap")) {
			self.addTag("grimdepth:checked_ap");
			if (GrimdepthEnchantments.getArmorPiercerLevel(self.getWeaponItem()) > 0) {
				self.setFlag(FLAG_ARMOR_PIERCER, true);
			}
		}
	}

	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
			)
	)
	private void grimdepth$replaceArrowCritParticle(Level level, ParticleOptions particle, double x, double y, double z, double dx, double dy, double dz) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (particle.getType() == ParticleTypes.CRIT && grimdepth$hasArmorPiercer(self)) {
			// Suppress vanilla crit particles so they do not clash with the TRIAL_OMEN skull trail
			return;
		}
		level.addParticle(particle, x, y, z, dx, dy, dz);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void grimdepth$spawnArmorPiercerTrail(CallbackInfo ci) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (!self.level().isClientSide() || this.isInGround()) {
			return;
		}
		if (!GrimdepthConfig.INSTANCE.armorPiercer.projectileTrailParticles) {
			return;
		}
		if (!grimdepth$hasArmorPiercer(self)) {
			return;
		}

		Vec3 delta = self.getDeltaMovement();
		if (delta.lengthSqr() <= 0.0025) {
			return;
		}
		self.level().addParticle(
				ParticleTypes.TRIAL_OMEN,
				self.getX() - delta.x * 0.25,
				self.getY() - delta.y * 0.25,
				self.getZ() - delta.z * 0.25,
				0.0, 0.0, 0.0
		);
	}

	@Inject(method = "onHitEntity", at = @At("TAIL"))
	private void grimdepth$onHitEntity(EntityHitResult result, CallbackInfo ci) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (self.level() instanceof ServerLevel serverLevel && grimdepth$hasArmorPiercer(self)) {
			Entity target = result.getEntity();
			if (target != null && GrimdepthConfig.INSTANCE.armorPiercer.spawnBlueSkullParticles) {
				serverLevel.sendParticles(
						ParticleTypes.TRIAL_OMEN,
						target.getX(),
						target.getY() + target.getBbHeight() * 0.5,
						target.getZ(),
						3,
						0.2, 0.2, 0.2,
						0.02
				);
			}
		}
	}

	private static boolean grimdepth$hasArmorPiercer(AbstractArrow arrow) {
		return (arrow.getEntityData().get(AbstractArrow.ID_FLAGS) & FLAG_ARMOR_PIERCER) != 0;
	}
}
