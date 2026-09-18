package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEnchantments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

	@Shadow protected abstract boolean isInGround();

	@Inject(method = "tick", at = @At("TAIL"))
	private void grimdepth$spawnArmorPiercerTrail(CallbackInfo ci) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (self.level().isClientSide() || this.isInGround()) {
			return;
		}

		if (!self.entityTags().contains("grimdepth:checked_ap")) {
			self.addTag("grimdepth:checked_ap");
			if (self.level() instanceof ServerLevel serverLevel) {
				if (GrimdepthEnchantments.getArmorPiercerLevel(serverLevel, self.getWeaponItem()) > 0) {
					self.addTag("grimdepth:has_armor_piercer");
				}
			}
		}

		if (!self.entityTags().contains("grimdepth:has_armor_piercer")) {
			return;
		}

		if (!GrimdepthConfig.INSTANCE.armorPiercer.projectileTrailParticles) {
			return;
		}

		if (self.level() instanceof ServerLevel serverLevel) {
			Vec3 delta = self.getDeltaMovement();
			double speed = delta.length();
			if (speed > 0.05) {
				Vec3 currentPos = self.position();
				Vec3 startPos = currentPos.subtract(delta);
				int steps = speed > 1.0 ? 2 : 1;
				for (int i = 0; i < steps; i++) {
					double t = (i + 0.5) / (double) steps;
					double px = startPos.x + delta.x * t;
					double py = startPos.y + delta.y * t;
					double pz = startPos.z + delta.z * t;
					serverLevel.sendParticles(
							ParticleTypes.TRIAL_OMEN,
							px, py, pz,
							1,
							0.0, 0.0, 0.0,
							0.0
					);
				}
			}
		}
	}
}
