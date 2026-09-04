"""Build a nearest-neighbour QA sheet for material masks and sample colours."""

from pathlib import Path

from PIL import Image, ImageDraw

import generate_material_icons as assets


ROOT = Path(__file__).resolve().parents[1]
SCALE = 4
TILE_W = 440
TILE_H = 170
COLS = 3


def tint(base, mask, colour):
    result = Image.new("RGBA", base.size)
    for y in range(base.height):
        for x in range(base.width):
            red, green, blue, alpha = base.getpixel((x, y))
            amount = mask.getpixel((x, y))[0] / 255.0
            output = []
            for value, multiplier in zip((red, green, blue), colour):
                linear = (value / 255.0) ** 2.2
                linear *= 1.0 - amount + amount * multiplier
                output.append(round(max(0.0, min(1.0, linear)) ** (1.0 / 2.2) * 255))
            result.putpixel((x, y), tuple(output) + (alpha,))
    return result


def main():
    modes = {
        slug: mode for slug, (_, _, mode) in assets.ATLAS_ASSETS.items()
    }
    modes.update({slug: mode for slug, (_, mode) in assets.CUSTOM_ASSETS.items()})
    slugs = sorted(modes)
    rows = (len(slugs) + COLS - 1) // COLS
    sheet = Image.new("RGBA", (COLS * TILE_W, rows * TILE_H), (28, 28, 28, 255))
    draw = ImageDraw.Draw(sheet)
    source = ROOT / "resource-pack" / "gui" / "custom" / "materials"
    for index, slug in enumerate(slugs):
        base = Image.open(source / (slug + ".png")).convert("RGBA")
        mask = Image.open(source / (slug + "-mask.png")).convert("RGBA")
        is_wood = modes[slug] == "wood"
        colour = (0.84, 0.67, 0.96) if is_wood else (0.72, 0.45, 0.218)
        sample = tint(base, mask, colour)
        x = (index % COLS) * TILE_W
        y = (index // COLS) * TILE_H
        draw.text((x + 4, y + 4), slug, fill="white")
        for column, icon in enumerate((base, mask, sample)):
            sheet.alpha_composite(
                icon.resize((32 * SCALE, 32 * SCALE), Image.Resampling.NEAREST),
                (x + 4 + column * 142, y + 28),
            )
    output = ROOT / "build" / "qa" / "material-mask-preview.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    sheet.save(output)
    print(output)

    examples = Image.new("RGBA", (560, 660), (28, 28, 28, 255))
    example_draw = ImageDraw.Draw(examples)
    rows = [
        ("hatchet", (("iron", (1.0, 1.0, 1.0)),
                     ("copper", (0.72, 0.45, 0.218)),
                     ("gold", (0.968, 0.721, 0.208)))),
        ("metal-brush", (("steel", (0.57, 0.60, 0.65)),
                         ("copper", (0.72, 0.45, 0.218)),
                         ("gold", (0.968, 0.721, 0.208)))),
        ("small-barrel", (("oak", (0.77, 0.75, 0.68)),
                          ("lavender", (0.84, 0.67, 0.96)),
                          ("rose", (0.98, 0.65, 0.47)))),
        ("mallet", (("oak", (0.77, 0.75, 0.68)),
                    ("lavender", (0.84, 0.67, 0.96)),
                    ("rose", (0.98, 0.65, 0.47)))),
    ]
    for row, (slug, variants) in enumerate(rows):
        base = Image.open(source / (slug + ".png")).convert("RGBA")
        mask = Image.open(source / (slug + "-mask.png")).convert("RGBA")
        y = row * 160
        example_draw.text((4, y + 4), slug, fill="white")
        for column, (label, colour) in enumerate(variants):
            x = 70 + column * 160
            example_draw.text((x + 42, y + 4), label, fill="white")
            examples.alpha_composite(
                tint(base, mask, colour).resize((128, 128), Image.Resampling.NEAREST),
                (x, y + 25),
            )
    examples_output = ROOT / "build" / "qa" / "material-examples.png"
    examples.save(examples_output)
    print(examples_output)


if __name__ == "__main__":
    main()
