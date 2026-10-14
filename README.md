# WURM High-Res Icons

![High-Res Icons](docs/branding/high-res-icons-logo-128.png)

Detailed item artwork, material colours and animated rarity effects for the **Wurm Unlimited client**, by **Chamomilo**.

[Download 0.2.5](https://github.com/chamomilo/wurm-high-res-icons/releases/tag/v0.2.5) · [All releases](https://github.com/chamomilo/wurm-high-res-icons/releases) · [Report an issue](https://github.com/chamomilo/wurm-high-res-icons/issues)

![Original and replacement item icons](docs/WURM-High-Res-Icons-before-after.png)

## What it does

- Replaces supported item icons with detailed 32 × 32 artwork in Inventory, Toolbelt, Equipment and Creation/Build.
- Gives supported items separate images even when the game uses one shared icon.
- Colours metal and wooden parts by the item's material while preserving handles, glass, wax and other details.
- Adds animated rare, supreme and fantastic highlights, with a pulsing blob or rotating star.
- Adds rainbow shimmer to magic water skins and Santa sacks, and individual colours to drake hides and dragon scales.
- Provides an in-game settings editor through the shared Chamomilo Mods Registry.

Version **0.2.5** includes **Chamomilo updater 1.5.0** and **Chamomilo UI 0.4.11**. The updater checks participating mods together and opens their release pages for manual installation.

## Installation

Requires **Wurm Unlimited** with [Ago's Client ModLauncher](https://github.com/ago1024/WurmClientModLauncher/releases).

1. Close the game client.
2. Download **WURM-High-Res-Icons-0.2.5.zip** from the [release page](https://github.com/chamomilo/wurm-high-res-icons/releases/tag/v0.2.5).
3. Extract it into your `WurmLauncher` folder, preserving the `mods` structure.
4. When updating, replace the configuration or merge your custom values into the new file. Remove the previous version's two `wurm-highres` JARs from `mods/wurm-highres`.
5. Start the client through Client ModLauncher.

The ZIP contains exactly four files:

```text
README.txt
mods/wurm-highres.properties
mods/wurm-highres/wurm-highres-0.2.5.jar
mods/wurm-highres/wurm-highres-resources-0.2.5.jar
```

## In-game settings

Open **Mod updates** after the HUD appears, find **High Res Icons**, then choose **Settings**. Each setting has hover help. Valid edits are saved automatically, and live settings apply during play. The settings window follows the shared language selector: English, Brazilian Portuguese, German and Russian.

| Setting | Default | Effect |
| --- | --- | --- |
| High resolution icons | On | Replacement item artwork. |
| Magic shimmer | On | Rainbow refraction on magic water skins and Santa sacks. |
| Rarity glow | On | Highlights for rare, supreme and fantastic items. |
| Rarity background | Off | Coloured background beneath rare items. |
| Rarity animation | Blob | Choose a blob or rotating star. |
| Pulse glow | On | Animate the glow. |
| Glow radius | 3 px | Glow width, from 1 to 4 pixels. |
| Glow opacity | 0.48 | Glow strength, from 0 to 1. |
| Background opacity | 0.16 | Background strength, from 0 to 1. |
| Rare / supreme / fantastic colour | Blue / cyan / magenta | RGB channels or `#RRGGBB`. |
| Icon resource pack | Versioned resource JAR | Resource pack loaded at client startup. |

Changing the resource pack requires a restart. The editor identifies restart settings and offers Wurm's normal quit confirmation after saving valid changes. Saved overrides live in your user profile under `.chamomilo/mod-settings/wurm-highres.properties`; they survive a mod update. Unedited settings retain the new release's defaults. See [settings details](docs/SETTINGS.md) for configuration keys.

## Artwork examples

### Tools and material colours

The palette distinguishes white zinc, slightly greyer silver, light steel, dark iron, yellow gold, reddish copper, warm brass, cool tin, dark lead and yellow-green electrum. Adamantine and seryll follow the game's colours. The same artwork works across supported material-specific server items and rarity levels.

![Tools in twelve metal colours](docs/images/tool-metals.png)

### Containers, lighting and cookware

Gift and gift box, chest, tackle box and compartment have separate designs. The buoy keeps its lantern and iron hoops while its wooden body changes colour. Raw clay is matte grey, and fired pottery is warm brown. Frying pan and sauce pan use distinct shallow and deep profiles.

![Gifts and containers](docs/images/containers.png)

![Lighting icons](docs/images/lighting.png)

![Buoy and clay vessels](docs/images/buoy-pottery.png)

![Pottery flask and metal cookware](docs/images/cookware.png)

### Keys, locks and hardware

Large and small nails retain different proportions. Ordinary key and Key copy share a design with material colours; Key of the heavens has polished gold and three ruby inlays. Door lock, small padlock and boat lock share the small image, while large padlock and gate lock share a larger image. Hooks preserve their curved S-shaped design.

![Key moulds, lock picks, rivets, nail sizes and ordinary key](docs/images/hardware.png)

![Locks, Key of the heavens, key copy, hooks and eyelet](docs/images/locks-hardware.png)

### Fishing equipment

Fish keep net and the wooden spool follow their game models. Basic, light, medium, heavy and braided fishing line share one spool icon. Old fine fishing hook has its own image with an eye, sharp point and barb, shaped after a [Gamakatsu Octopus Light hook](https://gamakatsu.com/product/octopus-hooks-light/). Its metal colour follows the material palette.

![Fish keep net, common line spool and old fine fishing hook](docs/images/fishing.png)

Previews show the actual 32 × 32 icons enlarged. Items outside the supported replacements keep their existing artwork. String of cloth, bow string, yarn, metal fishing hook and old fishing lines retain separate identities. A renamed ground hook keeps its existing image when its shared model does not identify which hook it is.

## Updates and compatibility

Use **DOWNLOAD** in the Mods Registry to open the release page, then install the ZIP with the client closed. The shared updater does not install game files automatically. High-Res Icons is a client mod and needs no server installation.

## Building from source

Use JDK 8, the Gradle wrapper, your Wurm client libraries and the canonical Chamomilo updater checkout:

```sh
./gradlew clean build publicationZip -PwurmClientLibDir=/path/to/client/libs -PchamomiloUpdaterReference=/path/to/updater
```

The client library directory must contain `client-patched.jar`, `common.jar`, `javassist.jar` and `modlauncher.jar`. The updater build also requires its documented test libraries and JavaFX runtime. The build checks the complete embedded updater and produces the installable ZIP in `build/publication/`.

## License

GNU GPL 3.0 or later. See [LICENSE](LICENSE). The mod JAR includes the GPL text, the earlier LGPL notice and embedded components' license notices.
