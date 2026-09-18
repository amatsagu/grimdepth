package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEnchantments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "hurtServer", at = @At("RETURN"))
	private void grimdepth$spawnArmorPiercerParticles(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			return;
		}
		LivingEntity self = (LivingEntity) (Object) this;
		if (self.getArmorValue() <= 0) {
			return;
		}
		ItemStack weapon = damageSource.getWeaponItem();
		if (weapon == null || weapon.isEmpty()) {
			return;
		}
		int apLevel = GrimdepthEnchantments.getArmorPiercerLevel(level, weapon);
		if (apLevel > 0 && GrimdepthConfig.INSTANCE.armorPiercer.spawnBlueSkullParticles) {
			level.sendParticles(
					ParticleTypes.RAID_OMEN,
					self.getX(),
					self.getY() + self.getBbHeight() * 0.5,
					self.getZ(),
					apLevel * 3,
					0.25, 0.25, 0.25,
					0.02
			);
		}
	}
}
