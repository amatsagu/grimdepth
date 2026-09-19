package amatsagu.grimdepth.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GrimdepthConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static File getConfigFile() {
		try {
			var loader = FabricLoader.getInstance();
			if (loader != null && loader.getConfigDir() != null) {
				return loader.getConfigDir().resolve("grimdepth.json").toFile();
			}
		} catch (Throwable ignored) {
		}
		return new File("config/grimdepth.json");
	}

	public static GrimdepthConfig INSTANCE = new GrimdepthConfig();

	public GeneralConfig general = new GeneralConfig();
	public ArmorPiercerConfig armorPiercer = new ArmorPiercerConfig();
	public SkeletonsConfig skeletons = new SkeletonsConfig();
	public ZombiesConfig zombies = new ZombiesConfig();
	public CreepersConfig creepers = new CreepersConfig();
	public SpidersConfig spiders = new SpidersConfig();
	public BatsConfig bats = new BatsConfig();
	public BackstepConfig backstep = new BackstepConfig();
	public LightingConfig lighting = new LightingConfig();
	public NightmareAwarenessConfig nightmareAwareness = new NightmareAwarenessConfig();
	public DungeonLootConfig dungeonLoot = new DungeonLootConfig();

	public static class DungeonLootConfig {
		public int emptyWeight = 18;
		public int armorPiercerWeight = 1;
		public int backstepWeight = 1;
	}

	public static class NightmareAwarenessConfig {
		public int maxTriggerYLevel = 0;
		public double defaultOreChance = 0.10;
		public double darkOreChance = 0.35;
		public double highRiskOreChance = 0.75;
		public List<String> highRiskOres = new ArrayList<>(List.of(
				"minecraft:deepslate_diamond_ore",
				"minecraft:deepslate_emerald_ore"
		));
		public int darkLightLevelThreshold = 0;
		public int durationTicks = 600;
		public double aggroRangeBonusPerLevel = 0.25;
		public double maxAggroRangeBonus = 2.50;
		public int maxLevel = 10;

		public double getAggroMultiplier(int amplifier) {
			int level = Math.max(1, amplifier + 1);
			double bonus = Math.min(this.maxAggroRangeBonus, level * this.aggroRangeBonusPerLevel);
			return 1.0 + bonus;
		}
	}

	public static class LightingConfig {
		public double defaultGamma = 0.75;
		public double brightnessDarkeningScale = 2.0 / 3.0;
		public double nightVisionBaseBoost = 0.25;
		public double nightVisionPerLevelBoost = 0.10;
		public boolean disableNightVisionFlashingShader = true;
		public boolean gammaMigratedTo75 = false;
		public double maxBrightnessCap = 2.0;
		public double antiCheatMaxGamma = 1.0;
		public double antiCheatResetGamma = 0.75;
	}

	public static class GeneralConfig {
		public int undergroundYLevel = 64;
		public int deepslateYLevel = 0;
		public int bedrockYLevel = -64;
	}

	public static class ArmorPiercerConfig {
		public List<Double> armorPiercingPerLevel = new ArrayList<>(List.of(0.10, 0.20, 0.30));
		public boolean spawnBlueSkullParticles = true;
		public boolean projectileTrailParticles = true;
	}

	public static class SkeletonsConfig {
		public double minUndergroundEnchantedBowChance = 0.05;
		public double deepslateEnchantedBowChance = 0.25;
		public double bedrockEnchantedBowChance = 0.50;
		public int maxEnchantLevel = 3;
	}

	public static class ZombiesConfig {
		public double minUndergroundToolChance = 0.05;
		public double deepslateToolChance = 0.25;
		public double bedrockToolChance = 0.50;
		public double pickaxeChance = 0.70;
		public double shovelChance = 0.30;
		public double upperLevelsPrimitiveToolChance = 0.80;
		public double deepslateAdvancedToolChance = 0.80;
		public List<String> primitiveTools = new ArrayList<>(List.of("minecraft:stone_pickaxe", "minecraft:stone_shovel"));
		public List<String> advancedTools = new ArrayList<>(List.of("minecraft:iron_pickaxe", "minecraft:iron_shovel"));
		public LeaderConfig leaders = new LeaderConfig();

		// Legacy aliases for config backwards compatibility
		public List<String> stoneTools;
		public List<String> ironTools;
		public Double upperLevelsStoneToolChance;
		public Double deepslateIronToolChance;
	}

	public static class LeaderConfig {
		public double deepslateLeaderChance = 0.35;
		public int maxNearbyLeaders = 2;
		public double nearbyLeaderCheckRadius = 64.0;
		public double bonusMovementSpeed = 0.10;
		public double stepHeight = 1.5;
		public double particleProximityRadius = 24.0;
		public List<String> leaderArmorPool = new ArrayList<>(List.of(
				"minecraft:iron_helmet",
				"minecraft:iron_chestplate",
				"minecraft:chainmail_leggings",
				"minecraft:chainmail_boots"
		));
		public int armorPiecesCount = 2;
		public String weapon = "minecraft:iron_sword";
		public double bothEnchantmentsChance = 0.30;
	}

	public static class CreepersConfig {
		public int undergroundMinFuseTicks = 20;
		public int undergroundMaxFuseTicks = 30;
		public boolean huntLightSources = true;
		public int lightSearchHorizontalRange = 12;
		public int lightSearchVerticalRange = 4;
	}

	public static class SpidersConfig {
		public double deepslateCaveSpiderChance = 0.10;
	}

	public static class BatsConfig {
		public double deepslateVexChance = 0.10;
	}

	public static class BackstepConfig {
		public double pushDistanceBlocks = 1.0;
		public boolean scaleWithLevel = false;
		public double pushDistancePerLevel = 0.5;
		public int speedDurationTicks = 100;
		public int speedAmplifier = 0;
		public boolean requireSafeFloor = true;
		public double maxSafeDropDistance = 2.0;
	}

	public static void load() {
		File file = getConfigFile();
		if (!file.exists()) {
			save();
			return;
		}
		try (FileReader reader = new FileReader(file)) {
			GrimdepthConfig loaded = GSON.fromJson(reader, GrimdepthConfig.class);
			if (loaded != null) {
				INSTANCE = loaded;
				if (INSTANCE.general == null) INSTANCE.general = new GeneralConfig();
				if (INSTANCE.armorPiercer == null) INSTANCE.armorPiercer = new ArmorPiercerConfig();
				if (INSTANCE.skeletons == null) INSTANCE.skeletons = new SkeletonsConfig();
				if (INSTANCE.zombies == null) INSTANCE.zombies = new ZombiesConfig();
				if (INSTANCE.creepers == null) INSTANCE.creepers = new CreepersConfig();
				if (INSTANCE.spiders == null) INSTANCE.spiders = new SpidersConfig();
				if (INSTANCE.bats == null) INSTANCE.bats = new BatsConfig();
				if (INSTANCE.backstep == null) INSTANCE.backstep = new BackstepConfig();
				if (INSTANCE.lighting == null) INSTANCE.lighting = new LightingConfig();
				if (INSTANCE.nightmareAwareness == null) INSTANCE.nightmareAwareness = new NightmareAwarenessConfig();
				if (INSTANCE.dungeonLoot == null) INSTANCE.dungeonLoot = new DungeonLootConfig();

				// Migrate legacy zombies config keys if present
				if (INSTANCE.zombies.stoneTools != null && !INSTANCE.zombies.stoneTools.isEmpty()
						&& (INSTANCE.zombies.primitiveTools == null || INSTANCE.zombies.primitiveTools.isEmpty())) {
					INSTANCE.zombies.primitiveTools = new ArrayList<>(INSTANCE.zombies.stoneTools);
				}
				if (INSTANCE.zombies.ironTools != null && !INSTANCE.zombies.ironTools.isEmpty()
						&& (INSTANCE.zombies.advancedTools == null || INSTANCE.zombies.advancedTools.isEmpty())) {
					INSTANCE.zombies.advancedTools = new ArrayList<>(INSTANCE.zombies.ironTools);
				}
				if (INSTANCE.zombies.upperLevelsStoneToolChance != null) {
					INSTANCE.zombies.upperLevelsPrimitiveToolChance = INSTANCE.zombies.upperLevelsStoneToolChance;
				}
				if (INSTANCE.zombies.deepslateIronToolChance != null) {
					INSTANCE.zombies.deepslateAdvancedToolChance = INSTANCE.zombies.deepslateIronToolChance;
				}
				INSTANCE.zombies.stoneTools = null;
				INSTANCE.zombies.ironTools = null;
				INSTANCE.zombies.upperLevelsStoneToolChance = null;
				INSTANCE.zombies.deepslateIronToolChance = null;
			}
		} catch (Exception e) {
			save();
		}
	}

	public static void save() {
		try {
			File file = getConfigFile();
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			try (FileWriter writer = new FileWriter(file)) {
				GSON.toJson(INSTANCE, writer);
			}
		} catch (IOException ignored) {
		}
	}
}
