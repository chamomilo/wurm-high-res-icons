"""Extract one neutral base and author one semantic material mask per icon.

The game mod combines these assets with the material colour at runtime.  No
per-metal or per-wood-species bitmap variants are generated.
"""

from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
CELL = 32
ATLAS_OFFSETS = {
    "misc": 240,
    "resource": 480,
    "resource2": 1440,
    "tools": 720,
    "weapons": 1200,
}

# slug: (atlas, icon id, mask mode)
ATLAS_ASSETS = {
    "praying-statuette": ("misc", 282, "full"),
    "chain": ("resource", 520, "full"),
    "butchering-knife": ("tools", 755, "metal-tool"),
    "fork": ("tools", 767, "full"),
    "spoon": ("tools", 768, "full"),
    "awl": ("tools", 754, "metal-tool"),
    "clay-shaper": ("tools", 802, "wood"),
    "metal-brush": ("tools", 882, "metal"),
    "grooming-brush": ("tools", 902, "wood"),
    "knife": ("tools", 940, "metal-tool"),
    "stone-chisel": ("weapons", 1201, "metal-tool"),
    "crowbar": ("tools", 738, "full"),
    "mallet": ("tools", 741, "wood"),
    "hammer": ("tools", 742, "metal-tool"),
    "pickaxe": ("tools", 743, "metal-tool"),
    "rake": ("tools", 745, "metal-tool"),
    "shovel": ("tools", 746, "metal-tool"),
    "saw": ("tools", 747, "metal-tool"),
    "scissors": ("tools", 748, "full"),
    "file": ("tools", 749, "metal-tool"),
    "trowel": ("tools", 750, "metal-tool"),
    "sickle": ("tools", 752, "metal-tool"),
    "pliers": ("tools", 780, "full"),
    "spindle": ("tools", 787, "wood"),
    "small-anvil": ("tools", 791, "full"),
    "spatula": ("tools", 808, "wood"),
    "steel-and-flint": ("tools", 783, "metal"),
    "fruit-press": ("misc", 246, "wood"),
    "bee-smoker": ("tools", 736, "bee-smoker-metal"),
    "cheese-drill": ("misc", 266, "wood"),
    "small-bucket": ("misc", 265, "wood"),
    "needle": ("tools", 788, "full"),
    "compass": ("tools", 792, "compass-metal"),
    "lantern": ("tools", 822, "metal"),
    "spyglass": ("tools", 860, "coloured-metal"),
    "rope-tool": ("tools", 880, "wood"),
    "hatchet": ("weapons", 1207, "metal-tool"),
}

# slug: (existing custom filename, mask mode)
CUSTOM_ASSETS = {
    "branding-iron": ("branding-iron.png", "full"),
    "smelting-pot": ("smelting-pot.png", "full"),
    "small-barrel": ("small-barrel.png", "wood"),
    "large-barrel": ("large-barrel.png", "wood"),
    "huge-tub": ("huge-tub.png", "wood"),
    "huge-oil-barrel": ("huge-oil-barrel.png", "wood"),
    "wine-barrel": ("wine-barrel.png", "wood"),
    "press": ("press.png", "wood"),
    "large-anvil": ("large-anvil.png", "large-anvil-metal"),
    "prayer-charm": ("prayer-charm.png", "full"),
    "carving-knife": ("carving-knife.png", "metal-tool"),
}


def cell(image, atlas_name, icon_id):
    local = icon_id - ATLAS_OFFSETS[atlas_name]
    left = (local % 20) * CELL
    top = (local // 20) * CELL
    return image.crop((left, top, left + CELL, top + CELL))


def metal_amount(red, green, blue):
    """Continuous low-chroma selector: metal stays selected through antialiasing."""
    high = max(red, green, blue)
    low = min(red, green, blue)
    if high < 12:
        return 0
    chroma = high - low
    return max(0, min(255, round((92 - chroma) * 255 / 68)))


def wood_amount(red, green, blue):
    """Select warm timber while leaving grey hoops, blades, glass and bristles alone."""
    if max(red, green, blue) < 12:
        return 0
    warmth = red - blue
    green_balance = red - max(0, green - 30)
    return max(0, min(255, round(min(warmth * 3.4, green_balance * 4.0))))


def make_mask(base, mode):
    mask = Image.new("RGBA", base.size, (0, 0, 0, 0))
    for y in range(base.height):
        for x in range(base.width):
            red, green, blue, alpha = base.getpixel((x, y))
            if not alpha:
                continue
            if mode == "full":
                amount = 255
            elif mode == "metal":
                amount = metal_amount(red, green, blue)
            elif mode == "metal-tool":
                amount = metal_amount(red, green, blue) if x >= 11 and y <= 21 else 0
            elif mode == "wood":
                amount = wood_amount(red, green, blue)
            elif mode == "bee-smoker-metal":
                # The leather bellows is on the left; the can and nozzle occupy
                # the upper-right half and contain warm reflected pixels that a
                # colour-only selector would otherwise miss.
                amount = 255 if x >= 16 and y <= 27 else 0
            elif mode == "compass-metal":
                radius_squared = (x - 15.5) ** 2 + (y - 17.0) ** 2
                amount = 255 if radius_squared >= 78 or y <= 7 else 0
            elif mode == "coloured-metal":
                amount = wood_amount(red, green, blue)
            elif mode == "large-anvil-metal":
                amount = metal_amount(red, green, blue) if y <= 15 else 0
            else:
                raise ValueError("Unknown mask mode %s" % mode)
            amount = round(amount * alpha / 255)
            mask.putpixel((x, y), (amount, amount, amount, alpha))
    return mask


def neutral_metal_base(base, mask):
    result = base.copy()
    for y in range(base.height):
        for x in range(base.width):
            red, green, blue, alpha = base.getpixel((x, y))
            amount = mask.getpixel((x, y))[0] / 255.0
            if not alpha or not amount:
                continue
            grey = round(red * 0.2126 + green * 0.7152 + blue * 0.0722)
            result.putpixel((x, y), (
                round(red * (1.0 - amount) + grey * amount),
                round(green * (1.0 - amount) + grey * amount),
                round(blue * (1.0 - amount) + grey * amount),
                alpha,
            ))
    return result


def write_asset(pack, slug, base, mode):
    output = pack / "gui" / "custom" / "materials"
    output.mkdir(parents=True, exist_ok=True)
    mask = make_mask(base, mode)
    # Metal starts from a neutral luminance master, so even iron correctly
    # loses baked-in gold/copper reflections before its material colour is used.
    if mode != "wood":
        base = neutral_metal_base(base, mask)
    base.save(output / (slug + ".png"), optimize=True)
    mask.save(output / (slug + "-mask.png"), optimize=True)


def main():
    for pack_name in ("resource-pack",):
        pack = ROOT / pack_name
        atlases = {
            name: Image.open(pack / "gui" / (name + ".png")).convert("RGBA")
            for name in {asset[0] for asset in ATLAS_ASSETS.values()}
        }
        for slug, (atlas_name, icon_id, mode) in ATLAS_ASSETS.items():
            write_asset(pack, slug, cell(atlases[atlas_name], atlas_name, icon_id), mode)
        for slug, (filename, mode) in CUSTOM_ASSETS.items():
            base = Image.open(pack / "gui" / "custom" / filename).convert("RGBA")
            write_asset(pack, slug, base, mode)
        print("%s: %d material base/mask pairs" % (
            pack_name, len(ATLAS_ASSETS) + len(CUSTOM_ASSETS)))


if __name__ == "__main__":
    main()
