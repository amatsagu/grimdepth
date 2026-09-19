<div align="center">
  <h1>
    <img src="src/main/resources/assets/grimdepth/icon.png" alt="Grimdepth Logo" width="48" height="48" align="middle" />
    <sub>Grimdepth</sub>
  </h1>
  <h3>A modern Fabric mod that makes deep underground just bit more scary.</h3>
</div>

## About this mod
This mod was originally a datapack that I've created to slightly boost monsters deep underground, make them just a bit more scary to deal with - so even player in end game gear can find them challenging at times.

**Grimdepth** works mostly at `Y < 0` in overworld and attempts to blend well with Vanilla. It only adds 2 new enchants, everything else is still from base game. Almost everything can be configured in `grimdepth.json` that is generated in game files after first launch.

## List of features
- Adds 2 new enchants to make up for the increased difficulty:
  * `Armor Piercer`: Makes weapons ignore 10%/20%/30% of enemy's armor. It's a rare enchantment found in dungeons (mob spawner dungeon, trial chambers, underwater ruins/pyramids, desert pyramids, jungle temples) and very rarely traded by villagers (librarians). Works with any swords, axes, bows, crossbows, tridents, spears & maces. Attacks and projectiles also leave an ominous blue skull particle trail.
  * `Backstep`: Similarly to Armor Piercer, it's a rare enchant found in same dungeons and very rarely appears in village trades. It allows players to better avoid danger by gently pushing them backward and granting a short burst of movement speed after successfully releasing an arrow from a bow, firing from a crossbow, or throwing a trident. It includes safe-floor checks so you won't accidentally recoil into lava, fire, or off cliffs.
- Zombie Leaders are now more special. Grimdepth takes existing Vanilla mechanic and extends it:
  * All Zombie Leaders are now marked with death particles, making them easier to spot in the dark.
  * Zombie Leaders receive a slight bonus to movement speed & jump boost and are always granted an iron sword + 2 random pieces of iron/chainmail equipment.
  * They are more likely to spawn closer to bedrock layer in the overworld, but there's a limit of max 2 near the player in combat.
- In deep underground caves (starting from `Y < 0`), Skeletons and Zombies scale in danger the deeper you explore:
  * Skeletons will always use bows, but have an increasing chance to spawn with enchanted bows (Power or Armor Piercer). In deepslate caverns, enchanted skeletons also receive the Backstep enchantment — when they shoot their bow, they will actively leap backward away from the player if safe ground is available behind them.
  * Zombies may be found with random pieces of equipment they had before turning into a monster (shovels, pickaxes, or swords), favoring primitive stone tools higher up and advanced iron tools deep down.
- Underground Creepers are noticeably more aggressive and unpredictable:
  * Their fuses can detonate faster when underground, giving players less time to react.
  * Deep underground creepers will actively seek out and blow up placed light sources (torches, lanterns, glowstone, copper bulbs) to plunge caves back into darkness. When fighting a player, combat takes priority, but unlit corridors will quickly return to the shadows.
- Spiders in deepslate layers (`Y < 0`) have adapted to narrow cave environments:
  * All deepslate spiders are smaller than usual, making them nimbler and harder to hit in tight passages.
  * When defeated, they trigger the Weaving effect, leaving a patch of cobwebs scattered around their death point.
  * Breaking any of these naturally spawned cobwebs in deepslate caves can disturb another small, angry spider lurking in the shadows, triggering an immediate ambush.
- Cave lighting and the Night Vision effect have been overhauled for better cave atmosphere:
  * Deep caves feel naturally darker and more atmospheric, with richer shadows underground while keeping default game brightness comfortable.
  * Night Vision no longer gives a flat artificial fullbright wash or that jarring flashing screen near the end. Instead, it naturally brightens up your vision in an immersive way that scales with effect level.
- Nightmare Awareness is a new harmful status effect marked by ominous red skull particles:
  * Mining naturally generated deepslate ores deep underground has a chance to inflict this effect. The chance grows higher when mining in pitch darkness and is highest when mining rare veins like diamond and emerald ores (player-placed ores are tracked and will never trigger it).
  * While active, the darkness around you stirs. Hostile monsters will periodically manifest from nearby unlit dark corners and immediately hunt down the player who has the effect.
  * Gaining the effect again while it is still active refreshes the timer and raises its intensity, causing increasingly dangerous monster varieties to emerge from the darkness.
- Recommended companion mods that pair well with Grimdepth:
  * [Penchant](https://github.com/ThePotatoArchivist/Penchant): Grimdepth has built-in optional support for Penchant. When installed, Armor Piercer levels up through weapon durability use, and Backstep works as an instant max-level rare enchantment.
  * [Let Me Despawn](https://modrinth.com/mod/lmd): Highly recommended for server performance, allowing underground mobs that spawn with tools or pick up items to despawn naturally and drop their gear so caves never get overcrowded.

---

## Technical Details & Configuration
For technical architecture, mixin details, and the full `grimdepth.json` configuration reference, see [tech.md](tech.md).