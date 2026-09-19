package amatsagu.grimdepth.compat.penchant;

import amatsagu.grimdepth.common.Grimdepth;
import amatsagu.grimdepth.common.GrimdepthEnchantments;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

/**
 * Optional integration for the Penchant mod (specifically targeting 0.5.5+mc26.3 or higher).
 * <p>
 * If Penchant is detected at 0.5.5+mc26.3 or higher:
 * - Backstep has instantly max level (like Infinity), no level progression.
 * - Armor Piercing levels up in the same way as Sharpness (+25% more progress required).
 * If a higher version of Penchant is detected and fails compatibility verification,
 * an {@link IncompatiblePenchantVersionException} is thrown.
 */
public class PenchantCompat {
    public static final String PENCHANT_MOD_ID = "penchant";
    public static final String TARGET_PENCHANT_VERSION = "0.5.5+mc26.3";

    private static boolean initialized = false;
    private static boolean integrationActive = false;

    public static boolean isIntegrationActive() {
        return integrationActive;
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        Optional<ModContainer> penchantContainer = FabricLoader.getInstance().getModContainer(PENCHANT_MOD_ID);
        if (penchantContainer.isEmpty()) {
            Grimdepth.LOGGER.info("Penchant mod not detected. Skipping Penchant integration.");
            return;
        }

        ModContainer container = penchantContainer.get();
        Version detectedVersion = container.getMetadata().getVersion();
        Grimdepth.LOGGER.info("Detected Penchant mod version: {}", detectedVersion.getFriendlyString());

        Version targetVersion;
        try {
            targetVersion = Version.parse(TARGET_PENCHANT_VERSION);
        } catch (VersionParsingException e) {
            Grimdepth.LOGGER.error("Failed to parse target Penchant version '{}': {}", TARGET_PENCHANT_VERSION, e.getMessage());
            return;
        }

        int cmp = detectedVersion.compareTo(targetVersion);
        if (cmp < 0) {
            Grimdepth.LOGGER.warn("Installed Penchant version ({}) is older than supported minimum ({}). Skipping integration.",
                    detectedVersion.getFriendlyString(), TARGET_PENCHANT_VERSION);
            return;
        }

        // Test compatibility for exact version and higher versions
        boolean isHigherVersion = cmp > 0;
        try {
            verifyCompatibility(detectedVersion, isHigherVersion);
        } catch (IncompatiblePenchantVersionException e) {
            Grimdepth.LOGGER.error("================================================================================");
            Grimdepth.LOGGER.error("FATAL: Incompatible Penchant mod version detected!");
            Grimdepth.LOGGER.error("{}", e.getMessage());
            Grimdepth.LOGGER.error("================================================================================");
            throw e;
        }

        integrationActive = true;
        Grimdepth.LOGGER.info("Penchant mod integration initialized successfully for version {}.", detectedVersion.getFriendlyString());

        ServerLifecycleEvents.SERVER_STARTED.register(PenchantCompat::onServerStarted);
    }

    /**
     * Verifies that the detected Penchant version contains all expected classes, methods, and fields.
     * Throws {@link IncompatiblePenchantVersionException} if any check fails.
     */
    public static void verifyCompatibility(Version version, boolean isHigherVersion) {
        Grimdepth.LOGGER.info("Verifying Penchant compatibility (detected: {}, target: {}, higher: {})...",
                version.getFriendlyString(), TARGET_PENCHANT_VERSION, isHigherVersion);

        checkClass("archives.tater.penchant.Penchant", version);

        Class<?> defClass = checkClass("archives.tater.penchant.PenchantmentDefinition", version);
        checkConstructor(defClass, version, int.class, int.class, Enchantment.Cost.class);
        checkMethod(defClass, "getDefinition", version, Holder.class);
        checkMethod(defClass, "keyOf", version, ResourceKey.class);
        checkMethod(defClass, "getProgressCostFactor", version, int.class);
        checkField(defClass, "CACHE", version, Map.class);

        Class<?> regClass = checkClass("archives.tater.penchant.registry.PenchantRegistries", version);
        checkField(regClass, "PENCHANTMENT_DEFINITION", version, ResourceKey.class);

        Class<?> tagsClass = checkClass("archives.tater.penchant.registry.PenchantEnchantmentTags", version);
        checkField(tagsClass, "NO_LEVELING", version, TagKey.class);
        checkField(tagsClass, "RARE", version, TagKey.class);

        Class<?> progressClass = checkClass("archives.tater.penchant.component.EnchantmentProgress", version);
        checkMethod(progressClass, "onDurabilityDamage", version, ItemStack.class, LivingEntity.class);
        checkMethod(progressClass, "addToProgress", version, ItemStack.class, int.class, LivingEntity.class);
        checkMethod(progressClass, "getMaxProgress", version, Holder.class, int.class, DataComponentGetter.class);

        Class<?> helperClass = checkClass("archives.tater.penchant.util.PenchantmentHelper", version);
        checkMethod(helperClass, "getProgressCostFactor", version, Holder.class, int.class);
        checkMethod(helperClass, "getBookRequirement", version, Holder.class);
        checkMethod(helperClass, "getXpLevelCost", version, Holder.class);

        Grimdepth.LOGGER.info("Penchant compatibility verified successfully.");
    }

    private static Class<?> checkClass(String className, Version version) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            String msg = "Penchant version " + version.getFriendlyString()
                    + " is missing required class '" + className + "'.";
            throw new IncompatiblePenchantVersionException(msg, e);
        }
    }

    private static void checkConstructor(Class<?> clazz, Version version, Class<?>... parameterTypes) {
        try {
            clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            String msg = "Penchant version " + version.getFriendlyString()
                    + " has incompatible constructor signature in class '" + clazz.getName() + "'.";
            throw new IncompatiblePenchantVersionException(msg, e);
        }
    }

    private static void checkMethod(Class<?> clazz, String methodName, Version version, Class<?>... parameterTypes) {
        try {
            clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            String msg = "Penchant version " + version.getFriendlyString()
                    + " is missing required method '" + methodName + "' in class '" + clazz.getName() + "'.";
            throw new IncompatiblePenchantVersionException(msg, e);
        }
    }

    private static void checkField(Class<?> clazz, String fieldName, Version version, Class<?> expectedType) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (!expectedType.isAssignableFrom(field.getType())) {
                String msg = "Penchant version " + version.getFriendlyString()
                        + " field '" + fieldName + "' in class '" + clazz.getName()
                        + "' has unexpected type " + field.getType().getName()
                        + " (expected assignable to " + expectedType.getName() + ").";
                throw new IncompatiblePenchantVersionException(msg);
            }
        } catch (NoSuchFieldException e) {
            String msg = "Penchant version " + version.getFriendlyString()
                    + " is missing required field '" + fieldName + "' in class '" + clazz.getName() + "'.";
            throw new IncompatiblePenchantVersionException(msg, e);
        }
    }

    private static void onServerStarted(MinecraftServer server) {
        if (!integrationActive) {
            return;
        }
        injectDefinitions(server.registryAccess(), "Server");
    }

    /**
     * Injects the calculated PenchantmentDefinition entries for Armor Piercer and Backstep into
     * Penchant's runtime cache.
     *
     * Values:
     * - Armor Piercer:
     *   Sharpness fallback in Penchant: expCost=1, bookReq=0, Cost(21, 11) -> L2=32 uses, L3=43 uses
     *   Armor Piercer (+25% progress): expCost=1, bookReq=0, Cost(26, 14) -> L2=40 uses (32 * 1.25), L3=54 uses (43 * 1.25 ~ 53.75)
     * - Backstep:
     *   Instantly max level (like Infinity), no level progression.
     *   Tagged in #penchant:enchantment/no_leveling and #penchant:enchantment/rare.
     *   Definition: expCost=4 (Fortune rarity/anvil cost), bookReq=25, Cost(65, 0)
     */
    public static void injectDefinitions(HolderLookup.Provider registryAccess, String contextLabel) {
        if (!integrationActive) {
            return;
        }

        try {
            var enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);
            var armorPiercerHolder = enchantmentRegistry.get(GrimdepthEnchantments.ARMOR_PIERCER);
            var backstepHolder = enchantmentRegistry.get(GrimdepthEnchantments.BACKSTEP);

            Class<?> defClass = Class.forName("archives.tater.penchant.PenchantmentDefinition");
            Constructor<?> defConstructor = defClass.getConstructor(int.class, int.class, Enchantment.Cost.class);

            Object armorPiercerDef = defConstructor.newInstance(1, 0, new Enchantment.Cost(26, 14));
            Object backstepDef = defConstructor.newInstance(4, 25, new Enchantment.Cost(65, 0));

            Field cacheField = defClass.getDeclaredField("CACHE");
            cacheField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Holder<Enchantment>, Object> cache = (Map<Holder<Enchantment>, Object>) cacheField.get(null);

            if (armorPiercerHolder.isPresent()) {
                cache.put(armorPiercerHolder.get(), armorPiercerDef);
                Grimdepth.LOGGER.info("[{}] Injected Penchant definition for Armor Piercer: expCost=1, bookReq=0, Cost(26, 14)", contextLabel);
            } else {
                Grimdepth.LOGGER.warn("[{}] Armor Piercer enchantment holder not found in registry access.", contextLabel);
            }

            if (backstepHolder.isPresent()) {
                cache.put(backstepHolder.get(), backstepDef);
                Grimdepth.LOGGER.info("[{}] Injected Penchant definition for Backstep: expCost=4, bookReq=25, Cost(65, 0)", contextLabel);
            } else {
                Grimdepth.LOGGER.warn("[{}] Backstep enchantment holder not found in registry access.", contextLabel);
            }
        } catch (Throwable t) {
            Grimdepth.LOGGER.error("[{}] Failed to inject Penchant enchantment definitions: {}", contextLabel, t.getMessage(), t);
        }
    }
}
