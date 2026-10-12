package org.wurmhighres.client;

import org.chamomilo.wurm.update.ModSetting;
import java.util.*;

/** Product metadata for the shared native Settings editor. */
final class ModSettingsSchema {
    private ModSettingsSchema() { }
    static List<ModSetting> specs() {
        return Arrays.asList(
            ModSetting.bool("Icons", "replaceIcons", "High resolution icons", "Use replacement icons for items. Existing cached icons may refresh when the window is reopened.", true, true),
            ModSetting.bool("Icons", "magicShimmer", "Magic shimmer", "Animate rainbow refraction on magic water skins and Santa sacks.", true, true),
            ModSetting.bool("Rarity", "rarityGlow", "Rarity glow", "Draw glow around rare, supreme and fantastic item icons.", true, true),
            ModSetting.bool("Rarity", "rarityBackground", "Rarity background", "Draw a colored background behind rare item icons.", false, true),
            ModSetting.choice("Rarity", "rarityAnimation", "Rarity animation", "Choose the animated shape beneath rare item icons.", "blob", true, "blob", "star"),
            ModSetting.bool("Rarity", "pulseGlow", "Pulse glow", "Animate the rarity glow rather than showing a constant glow.", true, true),
            ModSetting.number("Rarity", "glowRadius", "Glow radius (px)", "Width of the glow around a rare item icon.", 3, 1, 4, true, true),
            ModSetting.number("Rarity", "glowAlpha", "Glow opacity", "Opacity of the rarity glow; zero makes it invisible.", 0.48, 0, 1, false, true),
            ModSetting.number("Rarity", "backgroundAlpha", "Background opacity", "Opacity of the rare item background.", 0.16, 0, 1, false, true),
            ModSetting.color("Rarity colors", "rareColor", "Rare color", "RGB color for rare items. Use #RRGGBB or three channels from 0 to 255.", "#4299E0"),
            ModSetting.color("Rarity colors", "supremeColor", "Supreme color", "RGB color for supreme items. Use #RRGGBB or three channels from 0 to 255.", "#00FFFF"),
            ModSetting.color("Rarity colors", "fantasticColor", "Fantastic color", "RGB color for fantastic items. Use #RRGGBB or three channels from 0 to 255.", "#FF00FF"),
            ModSetting.text("Resources", "resourcePack", "Icon resource pack", "Replacement resource pack loaded at client startup.", "mods/wurm-highres/wurm-highres-resources-" + WurmHighresMod.VERSION + ".jar", false)
        );
    }
}

