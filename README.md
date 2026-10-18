# WURM High-Res Icons

![High-Res Icons logo](docs/branding/high-res-icons-logo-128.png)

Detailed item icons, material colours and animated rarity effects for the **Wurm Unlimited client**, by **Chamomilo**.

**[Download 0.2.13](https://github.com/chamomilo/wurm-high-res-icons/releases/tag/v0.2.13)** · [All releases](https://github.com/chamomilo/wurm-high-res-icons/releases) · [Report an issue](https://github.com/chamomilo/wurm-high-res-icons/issues)

![Original icons beside replacement icons, enlarged from actual runtime images](docs/renders/before-after.png)

## Features

- Detailed **32 × 32** item artwork in Inventory, Toolbelt, Equipment and Creation/Build.
- Separate designs for supported items that share an original game icon.
- Metal and wood colours that preserve handles, leather, glass, feathers and other fixed details.
- Animated rare, supreme and fantastic highlights, with a pulsing blob or rotating star.
- Rainbow shimmer for magic water skins and Santa sacks, plus distinct drake-hide and dragon-scale colours.
- In-game settings and the shared Chamomilo mod updater, in English, Brazilian Portuguese, German and Russian.

This release includes the completed fishing collection, matching tool parts, leatherworking knife, stone-cutting tool, training equipment and weapon variants. The sickle and its blade follow the game's curved shape and wrapped grip. See the [changelog](CHANGELOG.md) for the release history.

## Installation

Requires **Wurm Unlimited** with [Ago's Client ModLauncher](https://github.com/ago1024/WurmClientModLauncher/releases). No server installation is required.

1. Close the game client.
2. Download **WURM-High-Res-Icons-0.2.13.zip** from the [release page](https://github.com/chamomilo/wurm-high-res-icons/releases/tag/v0.2.13).
3. Extract it into your **WurmLauncher** folder, preserving the `mods` structure.
4. When updating, remove the previous version's two `wurm-highres` JARs from `mods/wurm-highres`. Replace the descriptor or merge your custom settings into the new descriptor, keeping its new version and JAR paths.
5. Start the client through Client ModLauncher.

The download contains exactly four files. All runtime images, fonts and component license notices are inside the two JARs.

```text
README.txt
mods/wurm-highres.properties
mods/wurm-highres/wurm-highres-0.2.13.jar
mods/wurm-highres/wurm-highres-resources-0.2.13.jar
```

The mod includes **Chamomilo updater 1.5.0** and its pinned **Chamomilo UI 0.4.11**. **DOWNLOAD** in the shared Mods Registry opens the release page. Install updates manually with the client closed.

## Artwork

These previews use the actual **32 × 32 PNGs shipped in the resource JAR**. Enlarged examples use nearest-neighbour scaling; the smaller examples show their native size. Documentation previews are collected in [docs/renders](docs/renders).

### Fishing equipment

Separate old rods distinguish a crafted, good-quality rod from a smooth tapered stick with or without line. Modern rods, hooks, landing net, reel components and assembled reels have their own designs. Material masks preserve line, stone sinkers and leather grips.

![Fishing collection: runtime icons enlarged, with native 32 pixel samples](docs/renders/fishing-equipment.png)

### Tools, parts and weapons

Parts match their completed tools. The rake has six widely spaced teeth; the leatherworking knife and its blade are distinct from the ordinary knife. The stone chisel is a stone-cutting tool. Axe and maul heads follow the corresponding finished weapon, with separate small and medium designs.

![Matching completed tools and separate parts](docs/renders/tools-and-parts.png)

![Training equipment, weapon variants and unfinished parts](docs/renders/workshop.png)

### Material colours

Supported metal artwork uses twelve material colours. Wood also follows the item's material. Recolouring affects the intended surface while preserving fixed fittings and decorative inlays.

![Selected metal colours on tools and weapon parts](docs/renders/material-colours.png)

## Settings

After the HUD appears, open **Mod updates → High Res Icons → Settings**. Hover over a setting for help. Valid edits save automatically; supported live settings apply during play.

| Setting | Default |
| --- | --- |
| Replacement icons | On |
| Magic shimmer | On |
| Rarity glow / coloured background | On / Off |
| Rarity animation | Blob; star also available |
| Pulse glow | On |
| Glow radius / opacity | 3 px / 0.48 |
| Background opacity | 0.16 |
| Rare / supreme / fantastic colours | Blue / cyan / magenta |

The resource pack setting requires a restart. Saved editor settings live in your user profile under `.chamomilo/mod-settings/wurm-highres.properties` and survive updates. Unedited settings use the installed release's defaults. See [all settings and configuration keys](docs/SETTINGS.md).

## Compatibility notes

Only supported items receive replacement artwork. When a shared icon cannot be identified reliably from its name, model or standard weight, the existing image is retained. Renamed shared items and generic recipe labels can therefore keep their original icon.

Both short-bow artworks are included. The two game templates can arrive with identical names, icons and weights, so a distinct strung/unstrung name or model marker is required to select the corresponding replacement.

## Building

Use JDK 8, the Gradle wrapper, Wurm client libraries and the canonical Chamomilo updater checkout:

```sh
./gradlew clean build publicationZip -PwurmClientLibDir=/path/to/client/libs -PchamomiloUpdaterReference=/path/to/updater
```

The client library directory must contain `client-patched.jar`, `common.jar`, `javassist.jar` and `modlauncher.jar`. The updater also needs the test libraries and JavaFX runtime described in its integration documentation. Local paths can be set in `local.properties`; see [local.properties.example](local.properties.example).

The build clean-builds the updater, verifies its complete embedded contents, checks every packaged source resource and mapped image, runs the tests, and produces the installable ZIP in `build/publication/`.

## License

GNU GPL 3.0 or later. See [LICENSE](LICENSE). The mod JAR also retains the earlier LGPL notice and embedded components' license notices.
