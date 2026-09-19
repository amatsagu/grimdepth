package amatsagu.grimdepth.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GrimdepthSpawner {

	public static boolean isUnderground(ServerLevelAccessor level, BlockPos pos) {
		return pos.getY() < GrimdepthConfig.INSTANCE.general.undergroundYLevel && !level.canSeeSky(pos);
	}

	public static float getDepthFactor(int y) {
		int top = GrimdepthConfig.INSTANCE.general.undergroundYLevel;
		int bottom = GrimdepthConfig.INSTANCE.general.bedrockYLevel;
		if (top <= bottom) {
			return 0.0f;
		}
		return Mth.clamp((top - y) / (float) (top - bottom), 0.0f, 1.0f);
	}

	public static double getDepthChance(float t, double minChance, double deepslateChance, double bedrockChance) {
		double c = 2.0 * (bedrockChance - 2.0 * deepslateChance + minChance);
		double b = bedrockChance - minChance - c;
		double a = minChance;
		return Mth.clamp(a + b * t + c * t * t, 0.0, 1.0);
	}

	public static void applySkeletonSpawn(AbstractSkeleton skeleton, ServerLevelAccessor level) {
		BlockPos pos = skeleton.blockPosition();
		if (!isUnderground(level, pos)) {
			return;
		}

		float t = getDepthFactor(pos.getY());
		GrimdepthConfig.SkeletonsConfig cfg = GrimdepthConfig.INSTANCE.skeletons;
		double chance = getDepthChance(t, cfg.minUndergroundEnchantedBowChance, cfg.deepslateEnchantedBowChance, cfg.bedrockEnchantedBowChance);
		RandomSource random = skeleton.getRandom();
		if (random.nextDouble() >= chance) {
			return;
		}

		if (!skeleton.getMainHandItem().is(Items.BOW)) {
			skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
		}
		final ItemStack bow = skeleton.getMainHandItem();

		boolean isDeepslate = pos.getY() <= GrimdepthConfig.INSTANCE.general.deepslateYLevel;
		var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		var powerHolder = registry.get(Enchantments.POWER);
		var armorPiercerHolder = registry.get(GrimdepthEnchantments.ARMOR_PIERCER);
		var backstepHolder = registry.get(GrimdepthEnchantments.BACKSTEP);

		int maxLvl = cfg.maxEnchantLevel;
		int lvl = isDeepslate ? (1 + random.nextInt(maxLvl)) : (1 + random.nextInt(Math.max(1, maxLvl - 1)));
		boolean pickPower = random.nextBoolean();
		if (pickPower && powerHolder.isPresent()) {
			bow.enchant(powerHolder.get(), lvl);
		} else if (armorPiercerHolder.isPresent()) {
			bow.enchant(armorPiercerHolder.get(), lvl);
		}

		if (isDeepslate) {
			backstepHolder.ifPresent(h -> bow.enchant(h, 1));
		}
	}

	public static void applyZombieSpawn(Zombie zombie, ServerLevelAccessor level) {
		applyZombieSpawn(zombie, level, EntitySpawnReason.NATURAL);
	}

	public static void applyZombieSpawn(Zombie zombie, ServerLevelAccessor level, EntitySpawnReason spawnReason) {
		BlockPos pos = zombie.blockPosition();
		if (!isUnderground(level, pos)) {
			return;
		}

		RandomSource random = zombie.getRandom();
		boolean isDeepslate = pos.getY() <= GrimdepthConfig.INSTANCE.general.deepslateYLevel;

		if (isDeepslate && spawnReason != EntitySpawnReason.REINFORCEMENT) {
			GrimdepthConfig.LeaderConfig leaderCfg = GrimdepthConfig.INSTANCE.zombies.leaders;
			if (random.nextDouble() < leaderCfg.deepslateLeaderChance) {
				AABB checkArea = zombie.getBoundingBox().inflate(leaderCfg.nearbyLeaderCheckRadius);
				List<Zombie> nearbyLeaders = level.getEntitiesOfClass(
						Zombie.class,
						checkArea,
						other -> other.entityTags().contains("grimdepth:zombie_leader")
				);
				if (nearbyLeaders.size() < leaderCfg.maxNearbyLeaders) {
					promoteToLeader(zombie, level, leaderCfg);
					return;
				}
			}
		}

		float t = getDepthFactor(pos.getY());
		GrimdepthConfig.ZombiesConfig cfg = GrimdepthConfig.INSTANCE.zombies;
		double chance = getDepthChance(t, cfg.minUndergroundToolChance, cfg.deepslateToolChance, cfg.bedrockToolChance);
		if (random.nextDouble() >= chance) {
			return;
		}

		boolean pickaxe = random.nextDouble() < cfg.pickaxeChance;
		boolean advanced = isDeepslate
				? random.nextDouble() < cfg.deepslateAdvancedToolChance
				: random.nextDouble() >= cfg.upperLevelsPrimitiveToolChance;

		List<String> pool = advanced ? cfg.advancedTools : cfg.primitiveTools;
		Item toolItem = getItemFromPool(pool, pickaxe ? "pickaxe" : "shovel");
		if (toolItem != null) {
			zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(toolItem));
		}
	}

	private static void promoteToLeader(Zombie zombie, ServerLevelAccessor level, GrimdepthConfig.LeaderConfig cfg) {
		zombie.addTag("grimdepth:zombie_leader");
		zombie.setCanBreakDoors(true);

		RandomSource random = zombie.getRandom();
		Identifier leaderBonusId = Identifier.withDefaultNamespace("leader_zombie_bonus");

		AttributeInstance reinforcementAttr = zombie.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
		if (reinforcementAttr != null) {
			reinforcementAttr.addOrReplacePermanentModifier(new AttributeModifier(
					leaderBonusId,
					random.nextDouble() * 0.25 + 0.5,
					AttributeModifier.Operation.ADD_VALUE
			));
		}

		AttributeInstance healthAttr = zombie.getAttribute(Attributes.MAX_HEALTH);
		if (healthAttr != null) {
			healthAttr.addOrReplacePermanentModifier(new AttributeModifier(
					leaderBonusId,
					random.nextDouble() * 3.0 + 1.0,
					AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
			));
			zombie.setHealth(zombie.getMaxHealth());
		}

		AttributeInstance speedAttr = zombie.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speedAttr != null) {
			speedAttr.addOrReplacePermanentModifier(new AttributeModifier(
					Identifier.fromNamespaceAndPath("grimdepth", "leader_speed"),
					cfg.bonusMovementSpeed,
					AttributeModifier.Operation.ADD_MULTIPLIED_BASE
			));
		}

		AttributeInstance stepAttr = zombie.getAttribute(Attributes.STEP_HEIGHT);
		if (stepAttr != null) {
			stepAttr.setBaseValue(cfg.stepHeight);
		}
		zombie.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, -1, 0, false, false));

		Item weaponItem = getItemById(cfg.weapon, Items.IRON_SWORD);
		ItemStack weaponStack = new ItemStack(weaponItem);
		var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		var sharpnessHolder = registry.get(Enchantments.SHARPNESS);
		var armorPiercerHolder = registry.get(GrimdepthEnchantments.ARMOR_PIERCER);

		boolean both = random.nextDouble() < cfg.bothEnchantmentsChance;
		if (both) {
			sharpnessHolder.ifPresent(h -> weaponStack.enchant(h, 1));
			armorPiercerHolder.ifPresent(h -> weaponStack.enchant(h, 1));
		} else if (random.nextBoolean()) {
			sharpnessHolder.ifPresent(h -> weaponStack.enchant(h, 1));
		} else {
			armorPiercerHolder.ifPresent(h -> weaponStack.enchant(h, 1));
		}
		zombie.setItemSlot(EquipmentSlot.MAINHAND, weaponStack);

		List<String> pool = new ArrayList<>(cfg.leaderArmorPool);
		Collections.shuffle(pool);
		java.util.Set<EquipmentSlot> equippedSlots = new java.util.HashSet<>();
		for (String id : pool) {
			if (equippedSlots.size() >= cfg.armorPiecesCount) {
				break;
			}
			Item armorItem = getItemById(id, null);
			if (armorItem == null) {
				continue;
			}
			ItemStack armorStack = new ItemStack(armorItem);
			Equippable equippable = armorStack.get(DataComponents.EQUIPPABLE);
			if (equippable != null && equippedSlots.add(equippable.slot())) {
				zombie.setItemSlot(equippable.slot(), armorStack);
			}
		}
	}

	public static boolean applySpiderSpawn(Spider spider, ServerLevelAccessor level) {
		if (spider.getClass() != Spider.class) {
			return false;
		}
		BlockPos pos = spider.blockPosition();
		if (!isUnderground(level, pos) || pos.getY() > GrimdepthConfig.INSTANCE.general.deepslateYLevel) {
			return false;
		}
		if (spider.getRandom().nextDouble() >= GrimdepthConfig.INSTANCE.spiders.deepslateCaveSpiderChance) {
			return false;
		}
		if (!(level instanceof ServerLevel serverLevel)) {
			return false;
		}
		CaveSpider caveSpider = EntityTypes.CAVE_SPIDER.create(serverLevel, EntitySpawnReason.NATURAL);
		if (caveSpider == null) {
			return false;
		}
		caveSpider.copyPosition(spider);
		caveSpider.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), EntitySpawnReason.NATURAL, null);
		serverLevel.addFreshEntity(caveSpider);
		spider.discard();
		return true;
	}

	public static boolean applyBatSpawn(Bat bat, ServerLevelAccessor level) {
		BlockPos pos = bat.blockPosition();
		if (!isUnderground(level, pos) || pos.getY() > GrimdepthConfig.INSTANCE.general.deepslateYLevel) {
			return false;
		}
		if (bat.getRandom().nextDouble() >= GrimdepthConfig.INSTANCE.bats.deepslateVexChance) {
			return false;
		}
		if (!(level instanceof ServerLevel serverLevel)) {
			return false;
		}
		Vex vex = EntityTypes.VEX.create(serverLevel, EntitySpawnReason.NATURAL);
		if (vex == null) {
			return false;
		}
		vex.copyPosition(bat);
		vex.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), EntitySpawnReason.NATURAL, null);
		serverLevel.addFreshEntity(vex);
		bat.discard();
		return true;
	}

	private static Item getItemFromPool(List<String> pool, String keyword) {
		for (String id : pool) {
			if (id.contains(keyword)) {
				return getItemById(id, null);
			}
		}
		return pool.isEmpty() ? null : getItemById(pool.getFirst(), null);
	}

	private static Item getItemById(String id, Item fallback) {
		try {
			var parsed = Identifier.parse(id);
			return BuiltInRegistries.ITEM.getOptional(parsed).orElse(fallback);
		} catch (Exception e) {
			return fallback;
		}
	}
}
