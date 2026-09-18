package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEnchantments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin {

	@Redirect(method = "attackVisualEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;crit(Lnet/minecraft/world/entity/Entity;)V"))
	private void grimdepth$replaceCritParticles(Player player, Entity target) {
		ItemStack weapon = player.getMainHandItem();
		int apLevel = GrimdepthEnchantments.getArmorPiercerLevel(player.level(), weapon);
		if (apLevel == 0 && !player.getOffhandItem().isEmpty()) {
			apLevel = GrimdepthEnchantments.getArmorPiercerLevel(player.level(), player.getOffhandItem());
		}
		boolean targetHasArmor = (target instanceof LivingEntity living && living.getArmorValue() > 0);
		if (apLevel > 0 && targetHasArmor && GrimdepthConfig.INSTANCE.armorPiercer.spawnBlueSkullParticles) {
			if (player.level() instanceof ServerLevel serverLevel) {
				int count = Math.max(1, 1 + apLevel);
				serverLevel.sendParticles(
						ParticleTypes.TRIAL_OMEN,
						target.getX(),
						target.getY() + target.getBbHeight() * 0.35,
						target.getZ(),
						count,
						0.25, 0.2, 0.25,
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
		boolean targetHasArmor = (target instanceof LivingEntity living && living.getArmorValue() > 0);
		if (apLevel > 0 && targetHasArmor) {
			return;
		}
		player.magicCrit(target);
	}
}
