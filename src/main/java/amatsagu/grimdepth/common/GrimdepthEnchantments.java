package amatsagu.grimdepth.common;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public final class GrimdepthEnchantments {
	public static final ResourceKey<Enchantment> ARMOR_PIERCER = ResourceKey.create(
			Registries.ENCHANTMENT,
			Identifier.fromNamespaceAndPath("grimdepth", "armor_piercer")
	);

	public static final ResourceKey<Enchantment> BACKSTEP = ResourceKey.create(
			Registries.ENCHANTMENT,
			Identifier.fromNamespaceAndPath("grimdepth", "backstep")
	);

	public static int getArmorPiercerLevel(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments == null || enchantments.isEmpty()) {
			enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
			if (enchantments == null || enchantments.isEmpty()) {
				return 0;
			}
		}
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.is(ARMOR_PIERCER)) {
				return enchantments.getLevel(holder);
			}
		}
		return 0;
	}

	public static int getArmorPiercerLevel(Level level, ItemStack stack) {
		return getArmorPiercerLevel(stack);
	}

	public static int getBackstepLevel(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
		if (enchantments == null || enchantments.isEmpty()) {
			enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
			if (enchantments == null || enchantments.isEmpty()) {
				return 0;
			}
		}
		for (Holder<Enchantment> holder : enchantments.keySet()) {
			if (holder.is(BACKSTEP)) {
				return enchantments.getLevel(holder);
			}
		}
		return 0;
	}

	public static int getBackstepLevel(Level level, ItemStack stack) {
		return getBackstepLevel(stack);
	}
}
