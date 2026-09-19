package amatsagu.grimdepth.common;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.Set;

public class GrimdepthLoot {
    public static final Set<ResourceKey<LootTable>> TARGET_DUNGEONS = Set.of(
            // Mob spawner dungeon
            BuiltInLootTables.SIMPLE_DUNGEON,
            // Desert pyramid
            BuiltInLootTables.DESERT_PYRAMID,
            // Jungle temple
            BuiltInLootTables.JUNGLE_TEMPLE,
            // Underwater ruins / pyramids
            BuiltInLootTables.UNDERWATER_RUIN_BIG,
            BuiltInLootTables.UNDERWATER_RUIN_SMALL,
            BuiltInLootTables.BURIED_TREASURE,
            // Trial chambers
            BuiltInLootTables.TRIAL_CHAMBERS_REWARD,
            BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE,
            BuiltInLootTables.TRIAL_CHAMBERS_REWARD_UNIQUE,
            BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS,
            BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE,
            BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE,
            BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY,
            BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR,
            BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION,
            BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL
    );

    public static void init() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!TARGET_DUNGEONS.contains(key)) {
                return;
            }

            GrimdepthConfig.DungeonLootConfig config = GrimdepthConfig.INSTANCE.dungeonLoot;
            var enchantmentRegistryOpt = registries.lookup(Registries.ENCHANTMENT);
            if (enchantmentRegistryOpt.isEmpty()) {
                return;
            }

            var enchantmentRegistry = enchantmentRegistryOpt.get();
            var apHolder = enchantmentRegistry.get(GrimdepthEnchantments.ARMOR_PIERCER);
            var bsHolder = enchantmentRegistry.get(GrimdepthEnchantments.BACKSTEP);

            if (apHolder.isEmpty() && bsHolder.isEmpty()) {
                return;
            }

            var pool = LootPool.lootPool();
            if (config.emptyWeight > 0) {
                pool.add(EmptyLootItem.emptyItem().setWeight(config.emptyWeight));
            }

            if (config.armorPiercerWeight > 0 && apHolder.isPresent()) {
                pool.add(LootItem.lootTableItem(Items.BOOK)
                        .setWeight(config.armorPiercerWeight)
                        .apply(new SetEnchantmentsFunction.Builder()
                                .withEnchantment(apHolder.get(), ContextIntProviders.between(1, 3))));
            }

            if (config.backstepWeight > 0 && bsHolder.isPresent()) {
                pool.add(LootItem.lootTableItem(Items.BOOK)
                        .setWeight(config.backstepWeight)
                        .apply(new SetEnchantmentsFunction.Builder()
                                .withEnchantment(bsHolder.get(), ContextIntProviders.exactly(1))));
            }

            tableBuilder.withPool(pool);
        });
    }
}
