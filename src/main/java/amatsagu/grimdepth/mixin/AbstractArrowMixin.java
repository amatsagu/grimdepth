package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEnchantments;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

	@Shadow protected abstract boolean isInGround();

	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
			)
	)
	private void grimdepth$replaceArrowCritParticle(Level level, ParticleOptions particle, double x, double y, double z, double dx, double dy, double dz) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (particle.getType() == ParticleTypes.CRIT && GrimdepthConfig.INSTANCE.armorPiercer.projectileTrailParticles) {
			if (GrimdepthEnchantments.getArmorPiercerLevel(self.getWeaponItem()) > 0) {
				level.addParticle(ParticleTypes.TRIAL_OMEN, x, y, z, 0.0, 0.0, 0.0);
				return;
			}
		}
		level.addParticle(particle, x, y, z, dx, dy, dz);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void grimdepth$spawnTridentTrail(CallbackInfo ci) {
		AbstractArrow self = (AbstractArrow) (Object) this;
		if (!self.level().isClientSide() || this.isInGround()) {
			return;
		}
		if (!((Object) this instanceof ThrownTrident)) {
			return;
		}
		if (!GrimdepthConfig.INSTANCE.armorPiercer.projectileTrailParticles) {
			return;
		}
		if (GrimdepthEnchantments.getArmorPiercerLevel(self.getWeaponItem()) <= 0) {
			return;
		}

		Vec3 delta = self.getDeltaMovement();
		if (delta.lengthSqr() <= 0.0025) {
			return;
		}
		self.level().addParticle(
				ParticleTypes.TRIAL_OMEN,
				self.getX(), self.getY(), self.getZ(),
				0.0, 0.0, 0.0
		);
	}
}
