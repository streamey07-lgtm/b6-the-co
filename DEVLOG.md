# B6: THE CORE - Development Log

## Target
- Minecraft Java 1.20.1, **Forge 47.3.0** (switched from Fabric because the creator asked for Forge 1.20.1 and a .jar).
- Java 17 (required by 1.20.1). Gradle with ForgeGradle 6, official Mojang mappings.

## Phase 1 - Project skeleton (written, NOT yet built)
Done (written, not compiled):
- build.gradle, settings.gradle, gradle.properties
- mods.toml, pack.mcmeta
- B6Mod main class
- B6Config with: encounter frequency, B6 difficulty, story stage, debug mode, overlay intensity, sound volume

NOT verified:
- Compilation. The workspace I used blocks Forge, Mojang and Gradle download servers, so nothing has been built or launched.
- No .jar exists yet.

## Safety rules (apply to every phase)
- Never delete, edit or corrupt world saves.
- Never really block Save and Quit. The day-six sequence is visual only.

## Canon notes
- Lore follows the creator's prompt exactly. Nothing invented.
- Undecided: name/look of Alex's gift item (will be a labelled placeholder), glow colour, sounds.

## Build instructions (on your PC)
1. Install Java 17 (Temurin 17 is fine).
2. Download the official Forge 1.20.1 MDK from files.minecraftforge.net and copy its `gradlew`, `gradlew.bat` and `gradle/` folder into this project (the MDK's wrapper is what makes `gradlew` work).
3. In this folder run: `gradlew build` (Windows: `gradlew.bat build`).
4. The jar appears in `build/libs/b6core-0.1.0.jar`. Put it in your `.minecraft/mods` folder with Forge 1.20.1 installed.
5. To test without installing: `gradlew runClient`.
6. If anything fails, send me the full error text.

## Phase 2 - B6 entity (written, NOT yet built or run)
Written:
- B6Entity: floating hostile mob (100 HP, flying movement, hunts nearest player, no fall damage, fire immune)
- B6Renderer: normal player model + black-and-white 8px checker texture, always fully lit (glow)
- ModEntities / ModItems / ModEvents / ClientEvents registration
- B6 spawn egg (creative tab: Spawn Eggs), en_us.json, generated texture textures/entity/b6.png

Not verified: compiling, the entity appearing, flying/attacking behaviour, the texture mapping on the model.
Known limits: the texture is a simple checker over the whole skin sheet. The "half shadow / half white" look and real glow come in Phase 3. B6 does not spawn naturally yet.

How to test (after it builds): `gradlew runClient`, create a creative world, run `/summon b6core:b6` or use the B6 Spawn Egg.

## Main menu (written, NOT yet built or run)
Written:
- Panorama: assets/minecraft/textures/gui/title/background/panorama_0..5.png replaced with a dark black-and-white checker world (overrides vanilla because mod assets load above vanilla).
- Vanilla title logo (minecraft.png) and edition badge (edition.png) replaced with transparent images.
- MainMenuGlitch.java draws our own black-and-white checker "MINECRAFT" logo (textures/gui/logo.png) on the title screen: constant vibration, ghost copies, horizontal tearing bursts and rare white flashes. Strength follows the overlayIntensity setting (0 = static logo).

Not verified: compiling, that the vanilla logo is really hidden, how the shake/glitch looks, panorama appearance.
Notes: another resource pack placed above this mod would override the panorama/logo. The splash text and buttons are unchanged.

## Main menu: Quit Game trap (written, NOT yet built or run)
Written:
- QuitTrapButton replaces the Quit Game button (the real one is hidden). Clicks 1-2: nothing happens (button shakes, click sound gets lower). Click 3: corruption starts.
- Sequence (~10.5 s): cracks + black-and-white missing texture spread (3 s) -> button melts and drips (6 s) -> remains fall off screen (1.4 s) -> line fades in: "You thought you could leave that easily? Just use your Alt + F4."
- Placeholder vanilla sounds (enderman scream, slime, anvil, warden heartbeat), scaled by soundVolume.
- Config: client `menuQuitPrank` (true by default). Set false to disable.

SAFETY: visual only. The button never quits and never touches files. Alt+F4 and the window close button always work. State is per game session and resets on next launch.
The original text typo "taught" was written as "thought" in the line. Say if you want it changed back.

Not verified: compiling; that Forge's ScreenEvent.Init.Post adds the custom button correctly; how the melt/fall look; message position; sounds.

## Cloud build option (no local setup)
.github/workflows/build.yml builds the jar on GitHub with Java 17 + Gradle 8.1.1 and uploads it as the "b6core-jar" artifact. Not yet run.

## Phase 3 - Portal block, basic Core dimension, B6 forms, encounters, jumpscare (written, NOT yet built or run)
Written:
- Stage 1: `missing_block` (black-and-white checker, slight glow). Staring at it ~2 s plays a jumpscare and teleports you to the Core. Config `lookTeleport` (default true) turns this off.
- Basic Core dimension `b6core:core`: flat world, dark/bright checker ground (`core_stone`, `core_surface`), permanent night, no mobs. This is only a placeholder world; atmosphere, effects and the boss arena come in Phase 10.
- CoreTravel remembers where you came from; `/b6core leave` brings you back (works for anyone, so you can never get stuck). Only your position changes - no world files are touched.
- B6 forms in one entity: glitch (stage 2, harmless, stares, vanishes with a scare when stared at/approached/hit), stone (stage 3, 150 HP, slow, hostile), main (stage 4, half shadow / half white skin, hostile).
- Skins generated: b6.png (checker), b6_stone.png (dark with glowing cracks), b6_main.png (shadow side / white side, glowing eyes).
- Random encounters: checked every second; ~1 per 4 min at default, 3x in the Nether, 2-minute cooldown, never on Peaceful or in the Core. Forms unlock by `storyStage` (glitch always, stone from 3, main from 6). Sometimes a missing-texture block appears nearby.
- Jumpscare overlay: fast black-and-white flash with a huge "B6"; follows `overlayIntensity`. Sent from server via a network packet.
- Commands: `/b6core spawn <glitch|stone|main>`, `/b6core core`, `/b6core scare` (op only), `/b6core leave` (anyone).
- B6 difficulty now scales his damage (0.6x / 1x / 1.5x / 2x).

Not verified: compiling; dimension JSON accepted by Minecraft 1.20.1; look-trigger; stare detection; skin mapping on the model; encounter spawn spots; overlay look; network packet.
Placeholders: all sounds are vanilla (Enderman scream, Warden heartbeat, end portal sound).

How to test (after it builds): `gradlew runClient`, new creative world.
1. `/b6core spawn glitch`, then stare at it - it should vanish with a flash.
2. `/b6core spawn stone` and `/b6core spawn main` to see the other forms.
3. Get the Missing Texture block from the Building Blocks tab, place it, look at it for 2 s - you should land in the Core. `/b6core leave` to return.

## Phase 4 - B6 communication and screen effects (written, NOT yet built or run)
Written:
- B6Whispers: random chat lines in the form `<B6> ...` (every ~11 min on average, 90 s cooldown, never in the Core or on Peaceful). Chat is server-wide, so B6 can message you wherever you are. Each message also flickers the screen.
- Editable messages: config/b6core-messages.txt is created on first run. One message per line; lines starting with # are ignored. If the file is empty or broken, built-in lines are used.
- Encounters: 50% chance of a chat line, 60% chance of a static flash when a figure spawns. About half of the figures get a name tag (e.g. "I can see you"), shown always.
- ScreenFx (client): static, corruption (black-and-white cells spreading) and glitch bars, all scaled by overlayIntensity. Sent by packet FxMessage (channel index 1).

NOT done yet (still in Phase 4 scope):
- B6 writing on signs.
- The sun turning into words.
- Gameplay-linked overlays during chases (only random flashes exist so far).

Not verified: compiling; the chat format; that the messages file is created in the config folder; the name tags; the screen effects' look and strength.

How to test (after it builds):
1. Run `/b6core spawn glitch` - you should get a chat line and possibly a static flash.
2. Edit config/b6core-messages.txt, add a line, restart the game, and wait for a message or spawn.
3. Set overlayIntensity to 0 in b6core-client.toml and confirm the static stops.

## Phase 4 addendum - B6 signs and sun words (written, NOT yet built or run)
- B6Signs: random oak signs appear 12-24 blocks from a player (about one every 8 minutes, 5-minute cooldown, never on Peaceful or in the Core). Text is red, from the PHRASES list in B6Signs.java (placeholders, edit freely). Test command: `/b6core sign` (op).
- SunWords (client): from day 2 to day 6 the sun shows a word from the WORDS list in SunWords.java, drawn in red on the sun's own plane right after the sky. Overworld only.

NOT done:
- Black-and-white sign texture. Signs are still vanilla oak. Doing that means registering a custom wood type with its own sign texture, which I can't test from here. It's a separate step.
- Sun words may appear mirrored or sideways. The rotation copies vanilla's sun, but I couldn't test it. Adjust in SunWords.java once you've seen it.

Not verified: compiling; SignBlockEntity.setMessage works as written in 1.20.1; sun word placement, orientation and size; sign spawn spots.
