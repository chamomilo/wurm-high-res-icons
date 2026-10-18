WURM HIGH-RES ICONS 0.2.13
By Chamomilo

Detailed 32 x 32 item icons, material colours and animated rarity effects
for the Wurm Unlimited client. No server installation is required.

Illustrated guide and support:
https://github.com/chamomilo/wurm-high-res-icons
Release:
https://github.com/chamomilo/wurm-high-res-icons/releases/tag/v0.2.13
Issues:
https://github.com/chamomilo/wurm-high-res-icons/issues

INSTALLATION

Requires Wurm Unlimited and Ago's Client ModLauncher:
https://github.com/ago1024/WurmClientModLauncher/releases

1. Close the game client.
2. Extract this ZIP into your WurmLauncher folder. Preserve the mods structure.
3. When updating, remove the previous version's two wurm-highres JARs from
   mods/wurm-highres. Replace the descriptor or merge your custom settings
   into the new file, keeping its new version and JAR paths.
4. Start the client through Client ModLauncher.

ZIP CONTENTS

README.txt
mods/wurm-highres.properties
mods/wurm-highres/wurm-highres-0.2.13.jar
mods/wurm-highres/wurm-highres-resources-0.2.13.jar

All required runtime images, fonts and component license notices are inside
the two JARs. Documentation images are in the illustrated online guide.

FEATURES AND THIS RELEASE

- Replacement artwork in Inventory, Toolbelt, Equipment and Creation/Build.
- Separate fishing rods, hooks, lines, net, reel parts and assembled reels.
- Matching finished tools and parts, a separate leatherworking knife,
  stone-cutting tool, six-tooth rake head and revised curved sickle.
- Separate axe heads, small/medium mauls and heads, training equipment,
  short sword, short-bow variants, war arrow, arrow head and spear tip.
- Material colours preserve wood, leather, feathers and decorative inlays.
- Distinct drake-hide and dragon-scale colours.
- Rare, supreme and fantastic highlights with blob or star animation.
- Rainbow shimmer on magic water skins and Santa sacks.

Includes the complete current Chamomilo updater 1.5.0 with its pinned
Chamomilo UI 0.4.11. DOWNLOAD in the shared Mods Registry opens the release
page. Install updates manually with the client closed.

The release build verifies all source images against the packaged JARs,
mapped image paths, image dimensions and complete embedded updater contents.

SETTINGS

After the HUD appears, open Mod updates -> High Res Icons -> Settings.
Hover over a setting for help. Valid changes save automatically; supported
live settings apply during play. Languages: English, Brazilian Portuguese,
German and Russian.

replaceIcons=true       Replacement artwork.
magicShimmer=true       Magic water skin and Santa sack shimmer.
rarityGlow=true         Rare-item glow.
rarityBackground=false Coloured background beneath rare items.
rarityAnimation=blob   Choose blob or star.
pulseGlow=true          Animate the glow.
glowRadius=3           Glow width: 1 to 4 pixels.
glowAlpha=0.48          Glow opacity: 0 to 1.
backgroundAlpha=0.16   Background opacity: 0 to 1.
rareColor=66,153,224
supremeColor=0,255,255
fantasticColor=255,0,255

Colours accept RGB channels or #RRGGBB. The resourcePack path must match
the installed resource JAR. Changing it requires a restart; the editor
offers Wurm's normal quit confirmation after saving valid restart changes.

Saved editor settings live in your user profile:
.chamomilo/mod-settings/wurm-highres.properties
They survive mod updates. Unedited settings use the installed defaults.
You can also edit mods/wurm-highres.properties with the client closed.
Remove a saved editor override to let the descriptor control that setting.

COMPATIBILITY

Unsupported or ambiguous shared items retain their existing image. Exact
names, models or standard inventory weights distinguish supported variants.
Renamed shared items and generic recipe names may keep the original icon.

Both short-bow artworks are included. The two game templates can have the
same name, icon and weight; a distinct strung/unstrung name or model marker
is required to select the corresponding replacement.

LICENSE

GNU GPL 3.0 or later. The GPL text, earlier LGPL notice and embedded
components' license notices are included inside the mod JAR.
