package amatsagu.grimdepth.mixin;

import amatsagu.grimdepth.common.GrimdepthConfig;
import amatsagu.grimdepth.common.GrimdepthEnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(CombatRules.class)
public abstract class CombatRulesMixin {

	@ModifyVariable(method = "getDamageAfterAbsorb", at = @At("HEAD"), argsOnly = true, ordinal = 1)
	private static float grimdepth$applyArmorPiercer(float armor, LivingEntity entity, float damage, DamageSource damageSource) {
		if (armor <= 0.0f || damageSource == null) {
			return armor;
		}

		ItemStack weapon = damageSource.getWeaponItem();
		if (weapon == null || weapon.isEmpty()) {
			return armor;
		}

		int level = GrimdepthEnchantments.getArmorPiercerLevel(weapon);
		if (level <= 0) {
			return armor;
		}

		List<Double> perLevel = GrimdepthConfig.INSTANCE.armorPiercer.armorPiercingPerLevel;
		double reduction = 0.10 * level;
		if (level <= perLevel.size()) {
			reduction = perLevel.get(level - 1);
		} else if (!perLevel.isEmpty()) {
			reduction = perLevel.getLast();
		}
		
		return (float) Math.max(0.0, armor * (1.0 - reduction));
	}
}
