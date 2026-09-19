package amatsagu.grimdepth.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NightmareAwarenessHelper {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Type MAP_TYPE = new TypeToken<Map<String, Set<Long>>>() {}.getType();
	private static final Map<String, Set<Long>> PLACED_ORES = new ConcurrentHashMap<>();
	private static final Map<UUID, Long> NEXT_SPAWN_TICKS = new ConcurrentHashMap<>();

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

	public static void tickNightmareAwareness(ServerLevel level, Player player, int amplifier) {
		GrimdepthConfig.NightmareAwarenessConfig config = GrimdepthConfig.INSTANCE.nightmareAwareness;
		UUID uuid = player.getUUID();
		long currentTick = level.getGameTime();

		Long nextTick = NEXT_SPAWN_TICKS.get(uuid);
		if (nextTick == null || currentTick > nextTick + config.maxSpawnIntervalTicks || currentTick < nextTick - config.maxSpawnIntervalTicks * 2) {
			NEXT_SPAWN_TICKS.put(uuid, currentTick + getRandomInterval(config, level.getRandom()));
			return;
		}

		if (currentTick < nextTick) {
			return;
		}

		boolean spawned = trySpawnAmbushMob(level, player, amplifier, config);
		if (spawned) {
			NEXT_SPAWN_TICKS.put(uuid, currentTick + getRandomInterval(config, level.getRandom()));
		} else {
			int retryTicks = Math.min(60, getRandomInterval(config, level.getRandom()));
			NEXT_SPAWN_TICKS.put(uuid, currentTick + retryTicks);
		}
	}

	private static int getRandomInterval(GrimdepthConfig.NightmareAwarenessConfig config, RandomSource random) {
		int min = Math.max(20, config.minSpawnIntervalTicks);
		int max = Math.max(min, config.maxSpawnIntervalTicks);
		return min + random.nextInt(max - min + 1);
	}

	public static List<String> getMonsterPool(GrimdepthConfig.NightmareAwarenessConfig config, int amplifier) {
		List<String> pool = new ArrayList<>();
		if (config.level1Monsters != null && !config.level1Monsters.isEmpty()) {
			pool.addAll(config.level1Monsters);
		}
		int level = amplifier + 1;
		if (level >= 2 && config.level2Monsters != null) {
			pool.addAll(config.level2Monsters);
		}
		if (level >= 3 && config.level3Monsters != null) {
			pool.addAll(config.level3Monsters);
		}
		if (pool.isEmpty()) {
			pool.add("minecraft:zombie");
		}
		return pool;
	}

	public static boolean trySpawnAmbushMob(ServerLevel level, Player player, int amplifier, GrimdepthConfig.NightmareAwarenessConfig config) {
		if (level.getDifficulty() == Difficulty.PEACEFUL) {
			return false;
		}

		List<String> pool = getMonsterPool(config, amplifier);
		RandomSource random = level.getRandom();
		double minDist = Math.max(1.0, config.minSpawnDistance);
		double maxDist = Math.max(minDist, config.maxSpawnDistance);
		BlockPos playerPos = player.blockPosition();

		for (int attempt = 0; attempt < 24; attempt++) {
			String mobId = pool.get(random.nextInt(pool.size()));
			Identifier identifier = Identifier.tryParse(mobId);
			if (identifier == null) {
				continue;
			}
			EntityType<?> rawType = BuiltInRegistries.ENTITY_TYPE.getOptional(identifier).orElse(null);
			if (rawType == null) {
				continue;
			}

			double angle = random.nextDouble() * 2.0 * Math.PI;
			double dist = minDist + random.nextDouble() * (maxDist - minDist);
			int x = Mth.floor(player.getX() + dist * Math.cos(angle));
			int z = Mth.floor(player.getZ() + dist * Math.sin(angle));

			for (int dy = 4; dy >= -6; dy--) {
				BlockPos pos = new BlockPos(x, playerPos.getY() + dy, z);
				if (!level.isLoaded(pos)) {
					continue;
				}

				double distSq = player.distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				if (distSq < minDist * minDist || distSq > maxDist * maxDist) {
					continue;
				}

				if (level.getBrightness(LightLayer.BLOCK, pos) > config.darkLightLevelThreshold) {
					continue;
				}

				@SuppressWarnings("unchecked")
				EntityType<? extends Mob> mobType = (EntityType<? extends Mob>) rawType;
				if (!SpawnPlacements.checkSpawnRules(mobType, level, EntitySpawnReason.NATURAL, pos, random)) {
					continue;
				}

				BlockState floor = level.getBlockState(pos.below());
				if (floor.is(Blocks.LAVA) || floor.is(Blocks.FIRE) || floor.is(Blocks.SOUL_FIRE)
						|| floor.is(Blocks.MAGMA_BLOCK) || floor.is(Blocks.CACTUS)
						|| floor.is(Blocks.CAMPFIRE) || floor.is(Blocks.SOUL_CAMPFIRE)) {
					continue;
				}

				Entity entity = rawType.create(level, EntitySpawnReason.NATURAL);
				if (!(entity instanceof Mob mob)) {
					if (entity != null) {
						entity.discard();
					}
					continue;
				}

				mob.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);

				if (!mob.checkSpawnObstruction(level) || !level.noCollision(mob)) {
					mob.discard();
					continue;
				}

				mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.NATURAL, null);

				AttributeInstance followRange = mob.getAttribute(Attributes.FOLLOW_RANGE);
				double requiredRange = Math.max(config.maxSpawnDistance + 16.0, 32.0);
				if (followRange != null && followRange.getBaseValue() < requiredRange) {
					followRange.setBaseValue(requiredRange);
				}

				mob.setTarget(player);
				mob.setLastHurtByMob(player);
				mob.addTag("grimdepth:nightmare_ambush");

				level.addFreshEntity(mob);
				mob.setTarget(player);

				level.sendParticles(
						ParticleTypes.RAID_OMEN,
						pos.getX() + 0.5,
						pos.getY() + 0.5,
						pos.getZ() + 0.5,
						12,
						0.3, 0.5, 0.3,
						0.05
				);
				mob.playAmbientSound();

				return true;
			}
		}

		return false;
	}

	public static void load(Path worldDir) {
		PLACED_ORES.clear();
		NEXT_SPAWN_TICKS.clear();
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
		NEXT_SPAWN_TICKS.clear();
	}
}
