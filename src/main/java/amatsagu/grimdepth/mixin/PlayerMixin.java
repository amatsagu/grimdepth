package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEnchantments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin {

	@Shadow
	private boolean canCriticalAttack(Entity entity) {
		throw new AssertionError();
	}

	@Redirect(method = "attackVisualEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V"))
	private void grimdepth$replaceCritParticles(Player player, Entity target) {
		ItemStack weapon = player.getMainHandItem();
		int apLevel = GrimdepthEnchantments.getArmorPiercerLevel(player.level(), weapon);
		if (apLevel == 0 && !player.getOffhandItem().isEmpty()) {
			apLevel = GrimdepthEnchantments.getArmorPiercerLevel(player.level(), player.getOffhandItem());
		}
		if (apLevel > 0 && GrimdepthConfig.INSTANCE.armorPiercer.spawnBlueSkullParticles) {
			if (player.level() instanceof ServerLevel serverLevel) {
				int count = Math.max(1, 1 + apLevel);
				serverLevel.sendParticles(
						ParticleTypes.TRIAL_OMEN,
						target.getX(),
						target.getY() + target.getBbHeight() * 0.5,
						target.getZ(),
						count,
						0.25, 0.25, 0.25,
						0.02
				);
			}
			return;
		}
		player.crit(target);
	}

	@Redirect(method = "attackVisualEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;magicCrit(Lnet/minecraft/world/entity/Entity;)V"))
	private void grimdepth$replaceMagicCritParticles(Player player, Entity target) {
		ItemStack weapon = player.getMainHandItem();
		int apLevel = GrimdepthEnchantments.getArmorPiercerLevel(player.level(), weapon);
		if (apLevel == 0 && !player.getOffhandItem().isEmpty()) {
			apLevel = GrimdepthEnchantments.getArmorPiercerLevel(player.level(), player.getOffhandItem());
		}
		if (apLevel > 0) {
			return;
		}
		player.magicCrit(target);
	}

	@WrapOperation(method = "stabAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;attackVisualEffects(Lnet/minecraft/world/entity/Entity;ZZZZF)V"))
	private void grimdepth$spearCritVisualEffects(Player instance, Entity target, boolean isCrit, boolean isKnockback, boolean isStrong, boolean isSweep, float enchantedDamage, Operation<Void> original) {
		boolean crit = isStrong && this.canCriticalAttack(target);
		original.call(instance, target, crit, isKnockback, isStrong, isSweep, enchantedDamage);
	}

	@WrapOperation(method = "stabAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private boolean grimdepth$spearCritDamage(Entity target, DamageSource source, float amount, Operation<Boolean> original) {
		if (this.canCriticalAttack(target)) {
			amount *= 1.5F;
		}
		return original.call(target, source, amount);
	}
}
