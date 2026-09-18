package amatsagu.grimdepth.common;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Optional;

public final class GrimdepthEnchantments {
	public static final ResourceKey<Enchantment> ARMOR_PIERCER = ResourceKey.create(
			Registries.ENCHANTMENT,
			Identifier.fromNamespaceAndPath("grimdepth", "armor_piercer")
	);

	public static int getArmorPiercerLevel(ServerLevel level, ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		Optional<Holder.Reference<Enchantment>> opt = level.registryAccess()
				.lookupOrThrow(Registries.ENCHANTMENT)
				.get(ARMOR_PIERCER);
		return opt.map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, stack)).orElse(0);
	}
}
