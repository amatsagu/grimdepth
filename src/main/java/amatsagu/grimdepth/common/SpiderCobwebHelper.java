package amatsagu.grimdepth.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SpiderCobwebHelper {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Type MAP_TYPE = new TypeToken<Map<String, Set<Long>>>() {}.getType();
	private static final Map<String, Set<Long>> SPIDER_COBWEBS = new ConcurrentHashMap<>();

	private static String getDimensionKey(Level level) {
		return level.dimension().identifier().toString();
	}

	public static void trackCobweb(Level level, BlockPos pos) {
		if (level.isClientSide()) {
			return;
		}
		String dimKey = getDimensionKey(level);
		SPIDER_COBWEBS.computeIfAbsent(dimKey, k -> ConcurrentHashMap.newKeySet()).add(pos.asLong());
	}

	public static boolean isSpiderCobweb(Level level, BlockPos pos) {
		String dimKey = getDimensionKey(level);
		Set<Long> set = SPIDER_COBWEBS.get(dimKey);
		return set != null && set.contains(pos.asLong());
	}

	public static boolean removeSpiderCobweb(Level level, BlockPos pos) {
		String dimKey = getDimensionKey(level);
		Set<Long> set = SPIDER_COBWEBS.get(dimKey);
		return set != null && set.remove(pos.asLong());
	}

	public static void onBlockBroken(Level level, Player player, BlockPos pos, BlockState state) {
		if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
			return;
		}
		if (player == null || player.isSpectator()) {
			return;
		}
		if (!state.is(Blocks.COBWEB)) {
			return;
		}
		if (pos.getY() > GrimdepthConfig.INSTANCE.general.deepslateYLevel) {
			return;
		}

		if (!removeSpiderCobweb(level, pos)) {
			return;
		}

		double chance = GrimdepthConfig.INSTANCE.spiders.breakCobwebSpiderSpawnChance;
		if (serverLevel.getRandom().nextDouble() >= chance) {
			return;
		}

		spawnAggroSpider(serverLevel, pos, player);
	}

	private static void spawnAggroSpider(ServerLevel level, BlockPos brokenPos, Player player) {
		BlockPos spawnPos = brokenPos;
		BlockState below = level.getBlockState(brokenPos.below());
		if (!below.isFaceSturdy(level, brokenPos.below(), Direction.UP) && below.getCollisionShape(level, brokenPos.below()).isEmpty()) {
			// Try to find a sturdy nearby block on floor
			for (BlockPos check : BlockPos.betweenClosed(brokenPos.offset(-1, -1, -1), brokenPos.offset(1, 1, 1))) {
				if (level.getBlockState(check).isAir() && level.getBlockState(check.below()).isFaceSturdy(level, check.below(), Direction.UP)) {
					spawnPos = check.immutable();
					break;
				}
			}
		}

		Spider spider = EntityTypes.SPIDER.create(level, EntitySpawnReason.EVENT);
		if (spider == null) {
			return;
		}

		spider.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, level.getRandom().nextFloat() * 360.0F, 0.0F);
		spider.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.EVENT, null);

		// 25% smaller
		AttributeInstance scaleAttr = spider.getAttribute(Attributes.SCALE);
		if (scaleAttr != null) {
			scaleAttr.setBaseValue(GrimdepthConfig.INSTANCE.spiders.deepslateScale);
		}

		// Weaving status effect so it also spawns cobwebs on death
		spider.addEffect(new MobEffectInstance(MobEffects.WEAVING, -1, 0, false, false));
		spider.addTag("grimdepth:deepslate_spider");

		// Aggro immediately on player who broke the cobweb
		spider.setTarget(player);

		level.addFreshEntity(spider);

		// Visual & audio feedback
		level.sendParticles(
				ParticleTypes.ITEM_COBWEB,
				spawnPos.getX() + 0.5,
				spawnPos.getY() + 0.5,
				spawnPos.getZ() + 0.5,
				12,
				0.3,
				0.3,
				0.3,
				0.05
		);
		level.playSound(
				null,
				spawnPos,
				SoundEvents.SPIDER_AMBIENT,
				SoundSource.HOSTILE,
				1.0F,
				1.2F
		);
	}

	public static void load(Path worldRoot) {
		Path file = worldRoot.resolve("data").resolve("grimdepth_spider_cobwebs.json");
		if (!Files.exists(file)) {
			return;
		}
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			Map<String, Set<Long>> data = GSON.fromJson(reader, MAP_TYPE);
			if (data != null) {
				SPIDER_COBWEBS.clear();
				data.forEach((k, v) -> SPIDER_COBWEBS.put(k, ConcurrentHashMap.newKeySet(v.size())));
				data.forEach((k, v) -> SPIDER_COBWEBS.get(k).addAll(v));
			}
		} catch (Exception e) {
			Grimdepth.LOGGER.warn("Failed to load spider cobwebs data: {}", e.getMessage());
		}
	}

	public static void save(Path worldRoot) {
		Path dir = worldRoot.resolve("data");
		Path file = dir.resolve("grimdepth_spider_cobwebs.json");
		try {
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			try (BufferedWriter writer = Files.newBufferedWriter(file)) {
				GSON.toJson(SPIDER_COBWEBS, writer);
			}
		} catch (Exception e) {
			Grimdepth.LOGGER.warn("Failed to save spider cobwebs data: {}", e.getMessage());
		}
	}

	public static void clear() {
		SPIDER_COBWEBS.clear();
	}
}
