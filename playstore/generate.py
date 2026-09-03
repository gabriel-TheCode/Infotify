#!/usr/bin/env python3
"""Generate the Play Store asset set for Infotify.

Everything is drawn from the brand tokens the app itself uses, and every device shot is an
unaltered capture: the phone chassis is drawn around the screenshot, never baked into it, so
a capture can be replaced without redrawing a frame.
"""
import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

SRC = os.path.join(os.path.dirname(os.path.abspath(__file__)), "captures")
OUT = os.path.dirname(os.path.abspath(__file__))
FONTS = os.path.join(os.path.dirname(os.path.abspath(__file__)), os.pardir,
                     "app", "src", "main", "res", "font")

# Brand tokens — the same values Color.kt and site/style.css carry.
PAPER = (250, 246, 242)
INK = (19, 17, 16)
INK_DEEP = (13, 11, 9)
EMBER = (216, 80, 11)
EMBER_BRIGHT = (255, 106, 56)
MUTED_ON_PAPER = (107, 98, 91)
MUTED_ON_INK = (162, 152, 144)
PAPER_ON_INK = (246, 241, 236)
CHASSIS = [(42, 39, 36), (22, 19, 15), (13, 11, 9)]


def bricolage(size, weight=800, width=100):
    f = ImageFont.truetype(f"{FONTS}/bricolage_grotesque.ttf", size)
    # PIL orders the axes as declared in the font: opsz, wght, wdth.
    f.set_variation_by_axes([96, weight, width])
    return f


def schibsted(size, weight=400):
    f = ImageFont.truetype(f"{FONTS}/schibsted_grotesk.ttf", size)
    f.set_variation_by_axes([weight])
    return f


def vertical_gradient(size, top, bottom):
    w, h = size
    grad = Image.new("RGB", (1, h))
    px = grad.load()
    for y in range(h):
        t = y / max(h - 1, 1)
        px[0, y] = tuple(round(top[i] + (bottom[i] - top[i]) * t) for i in range(3))
    return grad.resize((w, h), Image.BILINEAR)


def rounded_mask(size, radius, supersample=4):
    """A rounded-rectangle mask drawn oversized and downsampled, so the corners are smooth."""
    w, h = size
    big = Image.new("L", (w * supersample, h * supersample), 0)
    ImageDraw.Draw(big).rounded_rectangle(
        (0, 0, w * supersample - 1, h * supersample - 1),
        radius=radius * supersample,
        fill=255,
    )
    return big.resize((w, h), Image.LANCZOS)


def phone(screenshot, screen_w, bezel=13, radius=54):
    """Wrap a capture in a device chassis. The capture keeps its own aspect ratio."""
    sw, sh = screenshot.size
    screen_h = round(screen_w * sh / sw)
    screen = screenshot.resize((screen_w, screen_h), Image.LANCZOS).convert("RGB")

    inner_radius = radius - bezel
    screen.putalpha(rounded_mask((screen_w, screen_h), inner_radius))

    body_w, body_h = screen_w + bezel * 2, screen_h + bezel * 2
    body = vertical_gradient((body_w, body_h), CHASSIS[0], CHASSIS[2]).convert("RGBA")
    body.putalpha(rounded_mask((body_w, body_h), radius))

    # A hairline highlight along the top edge is what reads as machined metal rather than
    # a flat grey outline.
    edge = Image.new("RGBA", (body_w, body_h), (0, 0, 0, 0))
    ImageDraw.Draw(edge).rounded_rectangle(
        (0, 0, body_w - 1, body_h - 1), radius=radius, outline=(255, 255, 255, 34), width=2
    )
    body.alpha_composite(edge)
    body.alpha_composite(screen, (bezel, bezel))
    return body


def drop_shadow(shape, blur=34, offset=(0, 22), opacity=118):
    pad = blur * 3
    w, h = shape.size
    canvas = Image.new("RGBA", (w + pad * 2, h + pad * 2), (0, 0, 0, 0))
    silhouette = Image.new("RGBA", (w, h), (0, 0, 0, opacity))
    silhouette.putalpha(
        Image.eval(shape.getchannel("A"), lambda a: a * opacity // 255)
    )
    canvas.alpha_composite(silhouette, (pad + offset[0], pad + offset[1]))
    return canvas.filter(ImageFilter.GaussianBlur(blur)), pad


def wrap(draw, text, font, max_width):
    """Honour explicit newlines first; only the remainder is wrapped to the measure."""
    if "\n" in text:
        out = []
        for part in text.split("\n"):
            out.extend(wrap(draw, part, font, max_width))
        return out
    lines, line = [], ""
    for word in text.split():
        probe = f"{line} {word}".strip()
        if draw.textlength(probe, font=font) <= max_width or not line:
            line = probe
        else:
            lines.append(line)
            line = word
    if line:
        lines.append(line)
    return lines


def screenshot_asset(capture, headline, subline, theme, out_name):
    W, H = 1080, 1920
    dark = theme == "dark"
    ember_ground = theme == "ember"

    if ember_ground:
        canvas = vertical_gradient((W, H), EMBER_BRIGHT, EMBER).convert("RGBA")
        title_c, sub_c = (255, 255, 255), (255, 233, 222)
    elif dark:
        # An ink chassis on an ink ground loses its silhouette, and a drop shadow cannot
        # separate black from black. A low ember bloom behind the device does, and it is the
        # only place the accent appears on this tile.
        canvas = vertical_gradient((W, H), (34, 30, 27), INK_DEEP).convert("RGBA")
        bloom = Image.new("RGBA", (W, H), (0, 0, 0, 0))
        ImageDraw.Draw(bloom).ellipse((90, 620, 990, 1700), fill=EMBER + (86,))
        canvas.alpha_composite(bloom.filter(ImageFilter.GaussianBlur(150)))
        title_c, sub_c = PAPER_ON_INK, MUTED_ON_INK
    else:
        canvas = vertical_gradient((W, H), (255, 253, 251), PAPER).convert("RGBA")
        title_c, sub_c = INK, MUTED_ON_PAPER

    draw = ImageDraw.Draw(canvas)
    margin = 84
    text_w = W - margin * 2

    title_font = bricolage(70, weight=800)
    sub_font = schibsted(35, weight=400)

    y = 132
    for line in wrap(draw, headline, title_font, text_w):
        draw.text((margin, y), line, font=title_font, fill=title_c)
        y += 84
    if subline:
        y += 14
        for line in wrap(draw, subline, sub_font, text_w):
            draw.text((margin, y), line, font=sub_font, fill=sub_c)
            y += 48

    # Only the gesture pill is cropped away — 85px, measured, not guessed: it belongs to the
    # system rather than the app. The navigation bar and its labels stay, because they are
    # the app's own structure and the one thing a store visitor uses to read the layout.
    shot = Image.open(capture)
    shot = shot.crop((0, 0, shot.width, shot.height - 85))

    device = phone(shot, screen_w=700)
    shadow, pad = drop_shadow(device)
    dx = (W - device.width) // 2
    dy = max(y + 96, H - device.height + 10)

    canvas.alpha_composite(shadow, (dx - pad, dy - pad))
    canvas.alpha_composite(device, (dx, dy))

    canvas.convert("RGB").save(os.path.join(OUT, out_name), optimize=True)
    print(f"  {out_name}  {W}x{H}")


def mark(height, supersample=4):
    """The Infotify mark on a transparent ground, at any height.

    One source of geometry for every asset: the numbers below are lifted from
    ic_launcher_foreground.xml, so the store icon, the feature graphic and the installed
    launcher icon are the same drawing rather than three that resemble each other.
    Bounding box in viewport units: x 41.5..69.5, y 24.5..82 — 28 wide by 57.5 tall.
    """
    unit = height * supersample / 57.5
    w = round(28 * unit)
    h = round(57.5 * unit)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    ox, oy = 41.5, 24.5  # move the viewport origin to the mark's own top-left
    d.rounded_rectangle(
        ((41.5 - ox) * unit, (52 - oy) * unit, (60.5 - ox) * unit, (82 - oy) * unit),
        radius=2.5 * unit,
        fill=(251, 248, 245, 255),
    )
    d.ellipse(
        ((50.5 - ox) * unit, (24.5 - oy) * unit, (69.5 - ox) * unit, (43.5 - oy) * unit),
        fill=(255, 106, 31, 255),
    )
    return img.resize((round(w / supersample), round(h / supersample)), Image.LANCZOS)


def icon():
    """The 512 store icon: the launcher vector, rendered — not redrawn.

    The previous file was a different drawing (stem 15px off-axis and 50px too tall), so the
    store icon and the installed icon did not match. They do now.
    """
    S = 512
    img = Image.new("RGBA", (S, S), (22, 19, 15, 255))
    m = mark(round(57.5 * S / 108))
    img.alpha_composite(m, ((S - m.width) // 2 + round(3 * S / 108), (S - m.height) // 2))
    img.convert("RGB").save(os.path.join(OUT, "icon-512x512.png"), optimize=True)
    print(f"  icon-512x512.png  {S}x{S}")


def feature_graphic():
    """1024x500. Play crops this banner hard on some surfaces, so the lockup sits inside the
    left two-thirds and the ember disc is decoration that is allowed to be cut.

    No standalone mark here: the wordmark opens on the same "i" the mark is drawn from, so
    setting them side by side reads as the letter twice rather than as a lockup.
    """
    W, H = 1024, 500
    canvas = vertical_gradient((W, H), (28, 25, 22), INK_DEEP).convert("RGBA")

    glow = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(glow).ellipse((854, -128, 1178, 196), fill=EMBER + (255,))
    canvas.alpha_composite(glow.filter(ImageFilter.GaussianBlur(1.5)))

    draw = ImageDraw.Draw(canvas)
    x = 92
    draw.text((x, 150), "Infotify", font=bricolage(112, weight=800), fill=(251, 248, 245))
    draw.text(
        (x + 5, 290),
        "The news that matters to you",
        font=schibsted(40, weight=500),
        fill=(222, 214, 207),
    )
    draw.text(
        (x + 5, 348),
        "Choose your subjects. Read offline. One briefing a day.",
        font=schibsted(29, weight=400),
        fill=(150, 141, 134),
    )
    canvas.convert("RGB").save(
        os.path.join(OUT, "feature-graphic-1024x500.png"), optimize=True
    )
    print(f"  feature-graphic-1024x500.png  {W}x{H}")


SHOTS = [
    ('en_11_explore.png', 'Pick your subjects.\nSkip the rest.',
     'Fifteen topics, five at a time.', 'ember',
     'screenshot-1-explore.png'),
    ('en_04_foryou.png', 'A front page that is\nactually yours',
     'Thousands of publishers, filtered to what you read.', 'light',
     'screenshot-2-for-you.png'),
    ('en_03_briefing.png', 'One briefing a day',
     'A single notification, at a time you choose.', 'light',
     'screenshot-3-briefing.png'),
    ('en_02_interests.png', 'Five subjects,\none tap each',
     'Change them whenever you like.', 'light',
     'screenshot-4-interests.png'),
    ('en_06_search.png', 'Search every\npublisher at once',
     'One query, the whole wire.', 'light',
     'screenshot-5-search.png'),
    ('en_05_saved.png', 'Save now,\nread later',
     'Saved stories open without a connection.', 'light',
     'screenshot-6-saved.png'),
    ('en_10_feed_dark.png', 'Built for reading\nat night',
     'A true dark theme, not a dimmed white page.', 'dark',
     'screenshot-7-dark.png'),
    ('en_07_settings.png', 'No account.\nNo tracking.',
     'Nothing to sign up for, and nothing to opt out of.', 'light',
     'screenshot-8-settings.png'),
]

if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    print("Play Store assets:")
    icon()
    feature_graphic()
    for capture, headline, subline, theme, name in SHOTS:
        screenshot_asset(os.path.join(SRC, capture), headline, subline, theme, name)
