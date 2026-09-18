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
				int steps = Math.max(1, (int) Math.ceil(speed * 3.0));
				for (int i = 0; i < steps; i++) {
					double factor = (double) i / steps;
					double px = self.getX() - delta.x * factor;
					double py = self.getY() + self.getBbHeight() * 0.5 - delta.y * factor;
					double pz = self.getZ() - delta.z * factor;
					serverLevel.sendParticles(
							ParticleTypes.RAID_OMEN,
							px, py, pz,
							1,
							0.01, 0.01, 0.01,
							0.0
					);
				}
			}
		}
	}
}
