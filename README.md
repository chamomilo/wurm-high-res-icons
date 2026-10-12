# WURM High-Res Icons

Detailed item icons and animated rarity effects for the **Wurm Unlimited client**, by **Chamomilo**.

![Original and replacement icons for carving knife, stone chisel, leather and hide](docs/WURM-High-Res-Icons-before-after.png)

[Download the latest release](https://github.com/chamomilo/wurm-high-res-icons/releases/latest) · [Release history](CHANGELOG.md) · [Report an issue](https://github.com/chamomilo/wurm-high-res-icons/issues)

## What it changes

- Detailed 32 × 32 item artwork in Inventory, Toolbelt, Equipment and Creation/Build windows.
- Separate icons for supported items that share an original image: carving knife and stone chisel, hide and leather, anvil sizes, barrel types, torch and light token, and four lamp types.
- Material colours for supported tools, shields and other objects. Metal parts follow the item's material; wooden handles, glass and flames keep their own colours.
- Individual colours for black, blue, green, red and white drake hides and dragon scales.
- Animated highlights for rare, supreme and fantastic items, with a pulsing blob or rotating star.
- Rainbow shimmer for magic water skins and Santa sacks.

The resource pack loads at runtime. Game archives and server item data remain unchanged.

## Tools and metals

Seryll tools use a consistent material colour across ordinary and rare items, including recognized tools with server-specific icon IDs. The palette distinguishes white zinc, slightly greyer silver, light steel, dark iron, yellow gold, reddish copper, warm brass, cool tin, dark lead and yellow-green electrum. Adamantine and seryll use the game's colours.

![Six tools in twelve metal colours, including lead and electrum](docs/images/tool-metals.png)

These are the mod's 32 × 32 runtime icons, enlarged for comparison.

## Lamps and lighting

Candelabra, campfire, torch, candle and light token have redesigned icons. Oil lamp, lamp, hanging lamp, torch lamp and imperial street lamp use close-up compositions with visible lamp heads. The light token and the four lamp types have separate artwork.

![Ten lighting icons from the current mod](docs/images/lighting.png)

## Installation

Requires the Wurm Unlimited client with [Ago's Client ModLauncher](https://github.com/ago1024/WurmClientModLauncher/releases).

1. Close the game client.
2. Download `WURM-High-Res-Icons-0.1.39.zip` from [Releases](https://github.com/chamomilo/wurm-high-res-icons/releases/latest).
3. Extract the ZIP into your `WurmLauncher` folder, keeping the `mods` directory structure. Allow replacement of the existing mod configuration when updating, or merge your custom settings manually.
4. Start the client through Client ModLauncher.

The installation ZIP contains exactly these files:

```text
README.txt
mods/
  wurm-highres.properties
  wurm-highres/
    wurm-highres-0.1.39.jar
    wurm-highres-resources-0.1.39.jar
```

## Settings

Edit `mods/wurm-highres.properties` while the client is closed. Restart the client after changing settings.

| Setting | Default | Effect |
| --- | --- | --- |
| `replaceIcons` | `true` | Enable the replacement artwork. |
| `rarityGlow` | `true` | Highlight rare, supreme and fantastic items. |
| `rarityBackground` | `false` | Add a coloured background behind rare items. |
| `rarityAnimation` | `blob` | Choose `blob` or `star`. |
| `pulseGlow` | `true` | Enable the pulsing white shimmer. |
| `magicShimmer` | `true` | Enable the magic water skin and Santa sack effect. |

Rarity colours accept comma-separated RGB values or hex colours. Defaults are `66,153,224` for rare, `0,255,255` for supreme and `255,0,255` for fantastic. Glow size and opacity can be adjusted with `glowRadius`, `glowAlpha` and `backgroundAlpha`.

## Updates

Version **0.1.39** includes the shared **Chamomilo updater 1.4.2** and **Chamomilo UI 0.4.11**. The native **Mod updates** menu opens the shared update window after the HUD starts. **DOWNLOAD** opens the release page; install the downloaded ZIP manually with the client closed. The updater also provides a shared language selector for participating mods.

## Building from source

Use Java 8, the Gradle wrapper and local Wurm client libraries. Copy `local.properties.example` to `local.properties` and set `wurmClientLibDir` to the folder containing `client-patched.jar`, `common.jar`, `javassist.jar` and `modlauncher.jar`.

The build embeds the complete canonical Chamomilo updater from an external reference checkout. Set `CHAMOMILO_UPDATER_REFERENCE` or pass `-PchamomiloUpdaterReference=<reference-directory>`. The reference also needs the client JavaFX JAR, supplied as `-PwurmJavaFxJar=<path-to-jfxrt.jar>`.

```text
gradlew.bat clean test publicationZip -PchamomiloUpdaterReference=<reference-directory> -PwurmJavaFxJar=<path-to-jfxrt.jar>
```

The release ZIP is written to `build/publication`. The build verifies the updater's version, complete entry set and bytes, its startup bootstrap, the resource pack and the four-file distribution layout.

## License

GNU GPL 3.0 or later: see [LICENSE](LICENSE). The earlier LGPL notice is retained in [lgpl-3.0.txt](lgpl-3.0.txt). Both texts are included inside the mod JAR under `META-INF/licenses/wurm-highres/`; embedded components retain their own notices.
