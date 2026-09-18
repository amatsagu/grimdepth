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
	private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve("grimdepth.json").toFile();

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

	public static class NightmareAwarenessConfig {
		public boolean enabled = true;
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
		public double upperLevelsStoneToolChance = 0.80;
		public double deepslateIronToolChance = 0.80;
		public List<String> stoneTools = new ArrayList<>(List.of("minecraft:stone_pickaxe", "minecraft:stone_shovel"));
		public List<String> ironTools = new ArrayList<>(List.of("minecraft:iron_pickaxe", "minecraft:iron_shovel"));
		public LeaderConfig leaders = new LeaderConfig();
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
		public boolean enabled = true;
		public double pushDistanceBlocks = 1.0;
		public boolean scaleWithLevel = true;
		public double pushDistancePerLevel = 0.5;
		public int speedDurationTicks = 100;
		public int speedAmplifier = 0;
		public boolean requireSafeFloor = true;
		public double maxSafeDropDistance = 2.0;
	}

	public static void load() {
		if (!FILE.exists()) {
			save();
			return;
		}
		try (FileReader reader = new FileReader(FILE)) {
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
			}
		} catch (Exception e) {
			save();
		}
	}

	public static void save() {
		try {
			File parent = FILE.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			try (FileWriter writer = new FileWriter(FILE)) {
				GSON.toJson(INSTANCE, writer);
			}
		} catch (IOException ignored) {
		}
	}
}
