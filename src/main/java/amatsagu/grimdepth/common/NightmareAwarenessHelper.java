package amatsagu.grimdepth.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NightmareAwarenessHelper {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Type MAP_TYPE = new TypeToken<Map<String, Set<Long>>>() {}.getType();
	private static final Map<String, Set<Long>> PLACED_ORES = new ConcurrentHashMap<>();

	private static String getDimensionKey(Level level) {
		return level.dimension().identifier().toString();
	}

	public static void onBlockPlaced(Level level, BlockPos pos, BlockState state) {
		if (level.isClientSide() || !state.is(GrimdepthTags.DEEPSLATE_ORES)) {
			return;
		}
		String dimKey = getDimensionKey(level);
		PLACED_ORES.computeIfAbsent(dimKey, k -> ConcurrentHashMap.newKeySet()).add(pos.asLong());
	}

	public static void onBlockMined(Level level, Player player, BlockPos pos, BlockState state) {
		if (level.isClientSide() || player == null || player.isSpectator()) {
			return;
		}

		GrimdepthConfig.NightmareAwarenessConfig config = GrimdepthConfig.INSTANCE.nightmareAwareness;

		if (!state.is(GrimdepthTags.DEEPSLATE_ORES)) {
			return;
		}

		// Check if the block was placed by a player
		String dimKey = getDimensionKey(level);
		Set<Long> placed = PLACED_ORES.get(dimKey);
		if (placed != null && placed.remove(pos.asLong())) {
			return;
		}

		// Must be in deepslate level
		if (pos.getY() > config.maxTriggerYLevel) {
			return;
		}

		// Determine if high risk ore
		Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
		boolean isHighRisk = state.is(GrimdepthTags.HIGH_RISK_ORES)
				|| (blockId != null && config.highRiskOres.contains(blockId.toString()));

		// Determine light level where player stands
		int blockLight = level.getBrightness(LightLayer.BLOCK, player.blockPosition());
		boolean isDark = blockLight <= config.darkLightLevelThreshold;

		// Calculate chance
		double chance = config.defaultOreChance;
		if (isDark) {
			chance = Math.max(chance, config.darkOreChance);
		}
		if (isHighRisk) {
			chance = Math.max(chance, config.highRiskOreChance);
		}

		if (player.getRandom().nextDouble() >= chance) {
			return;
		}
		applyNightmareAwareness(player);
	}

	public static void applyNightmareAwareness(Player player) {
		GrimdepthConfig.NightmareAwarenessConfig config = GrimdepthConfig.INSTANCE.nightmareAwareness;
		MobEffectInstance existing = player.getEffect(GrimdepthEffects.NIGHTMARE_AWARENESS);
		int newAmplifier = 0;
		if (existing != null) {
			newAmplifier = Math.min(config.maxLevel - 1, existing.getAmplifier() + 1);
			player.removeEffect(GrimdepthEffects.NIGHTMARE_AWARENESS);
		}
		player.addEffect(new MobEffectInstance(
				GrimdepthEffects.NIGHTMARE_AWARENESS,
				config.durationTicks,
				newAmplifier
		));
	}

	public static void load(Path worldDir) {
		PLACED_ORES.clear();
		Path saveFile = worldDir.resolve("grimdepth_placed_ores.json");
		if (!Files.exists(saveFile)) {
			return;
		}
		try (BufferedReader reader = Files.newBufferedReader(saveFile)) {
			Map<String, Set<Long>> loaded = GSON.fromJson(reader, MAP_TYPE);
			if (loaded != null) {
				for (Map.Entry<String, Set<Long>> entry : loaded.entrySet()) {
					Set<Long> set = ConcurrentHashMap.newKeySet();
					set.addAll(entry.getValue());
					PLACED_ORES.put(entry.getKey(), set);
				}
			}
		} catch (Exception ignored) {
		}
	}

	public static void save(Path worldDir) {
		Path saveFile = worldDir.resolve("grimdepth_placed_ores.json");
		try {
			if (PLACED_ORES.isEmpty()) {
				Files.deleteIfExists(saveFile);
				return;
			}
			try (BufferedWriter writer = Files.newBufferedWriter(saveFile)) {
				GSON.toJson(PLACED_ORES, writer);
			}
		} catch (Exception ignored) {
		}
	}

	public static void clear() {
		PLACED_ORES.clear();
	}
}
