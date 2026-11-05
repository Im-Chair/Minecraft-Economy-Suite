"""Generate 128x128 placeholder PNGs for poker and mahjong map art cards."""
import os
from PIL import Image, ImageDraw, ImageFont

BASE = os.path.join(os.path.dirname(__file__),
                    "src", "main", "resources", "assets", "economycore",
                    "textures", "map_art")

def ensure(path):
    os.makedirs(path, exist_ok=True)

def make_card(path, bg, border, symbol, rank, suit_color, rank_color=None):
    img = Image.new("RGBA", (128, 128), bg)
    d = ImageDraw.Draw(img)
    # border
    d.rectangle([0, 0, 127, 127], outline=border, width=4)
    # inner rounded feel
    d.rectangle([6, 6, 121, 121], outline=border, width=1)
    # suit symbol (large, center)
    try:
        font_big  = ImageFont.truetype("C:/Windows/Fonts/msyh.ttc", 56)
        font_med  = ImageFont.truetype("C:/Windows/Fonts/msyh.ttc", 28)
        font_small= ImageFont.truetype("C:/Windows/Fonts/msyh.ttc", 20)
    except Exception:
        font_big = font_med = font_small = ImageFont.load_default()

    # Large suit symbol
    bb = d.textbbox((0, 0), symbol, font=font_big)
    sw, sh = bb[2]-bb[0], bb[3]-bb[1]
    d.text((64 - sw//2, 68 - sh//2), symbol, font=font_big, fill=suit_color)

    # Rank top-left
    rc = rank_color or suit_color
    d.text((10, 8), rank, font=font_med, fill=rc)
    # Rank bottom-right (flipped)
    bb2 = d.textbbox((0, 0), rank, font=font_small)
    rw, rh = bb2[2]-bb2[0], bb2[3]-bb2[1]
    d.text((118 - rw, 100), rank, font=font_small, fill=rc)

    img.save(path, "PNG")

def make_tile(path, bg, border, text, text_color):
    img = Image.new("RGBA", (128, 128), bg)
    d = ImageDraw.Draw(img)
    # border
    d.rectangle([0, 0, 127, 127], outline=border, width=5)
    d.rectangle([7, 7, 120, 120], outline=border, width=2)
    try:
        font_big = ImageFont.truetype("C:/Windows/Fonts/msyh.ttc", 60)
    except Exception:
        font_big = ImageFont.load_default()
    bb = d.textbbox((0, 0), text, font=font_big)
    sw, sh = bb[2]-bb[0], bb[3]-bb[1]
    d.text((64 - sw//2, 64 - sh//2), text, font=font_big, fill=text_color)
    img.save(path, "PNG")

# ── Poker ─────────────────────────────────────────────────────────────────────
poker_dir = os.path.join(BASE, "poker")
ensure(poker_dir)

suits = [
    ("spades",   "♠", (30,  30,  30),  (255, 255, 255), (30,  30,  30)),
    ("hearts",   "♥", (200, 220, 200),  (200,  40,  40), (200,  40,  40)),
    ("diamonds", "♦", (210, 230, 255),  ( 30,  80, 200), ( 30,  80, 200)),
    ("clubs",    "♣", (220, 245, 220),  ( 30, 140,  50), ( 30, 140,  50)),
]
ranks = [("a","A"),("2","2"),("3","3"),("4","4"),("5","5"),("6","6"),
         ("7","7"),("8","8"),("9","9"),("10","10"),("j","J"),("q","Q"),("k","K")]

for suit_id, symbol, bg, suit_color, rank_color in suits:
    for rank_id, rank_label in ranks:
        fname = f"{suit_id}_{rank_id}.png"
        make_card(os.path.join(poker_dir, fname),
                  bg, (80, 80, 80), symbol, rank_label, suit_color, rank_color)

# Jokers
make_card(os.path.join(poker_dir, "joker_big.png"),
          (30, 0, 60), (200, 160, 0), "★", "大", (255, 215, 0), (255, 215, 0))
make_card(os.path.join(poker_dir, "joker_small.png"),
          (200, 200, 220), (100, 100, 140), "☆", "小", (80, 80, 120), (80, 80, 120))

print(f"Poker: generated {len(ranks)*4 + 2} cards")

# ── Mahjong ───────────────────────────────────────────────────────────────────
mahjong_dir = os.path.join(BASE, "mahjong")
ensure(mahjong_dir)

# 萬子 (characters): ivory bg, red text
for i in range(1, 10):
    make_tile(os.path.join(mahjong_dir, f"wan_{i}.png"),
              (240, 225, 185), (160, 100, 40), str(i) + "萬", (180, 30, 30))

# 筒子 (circles): light blue bg, dark blue text
for i in range(1, 10):
    make_tile(os.path.join(mahjong_dir, f"tong_{i}.png"),
              (200, 225, 245), (40, 80, 160), str(i) + "筒", (20, 60, 160))

# 索子 (bamboo): light green bg, dark green text
for i in range(1, 10):
    make_tile(os.path.join(mahjong_dir, f"suo_{i}.png"),
              (200, 235, 205), (30, 120, 50), str(i) + "索", (20, 100, 40))

# 字牌 (honor tiles)
honors = [
    ("dong",  "東", (240, 210, 170), (140, 60, 10),  (180, 80, 20)),
    ("nan",   "南", (240, 210, 170), (140, 60, 10),  (180, 80, 20)),
    ("xi",    "西", (240, 210, 170), (140, 60, 10),  (180, 80, 20)),
    ("bei",   "北", (240, 210, 170), (140, 60, 10),  (180, 80, 20)),
    ("zhong", "中", (250, 230, 230), (160, 20, 20),  (200, 20, 20)),
    ("fa",    "發", (220, 245, 220), (20,  120, 40), (20,  150, 50)),
    ("bai",   "白", (245, 245, 245), (160, 160, 160),(160, 160, 160)),
]
for hid, char, bg, border, text_color in honors:
    make_tile(os.path.join(mahjong_dir, f"{hid}.png"), bg, border, char, text_color)

print(f"Mahjong: generated {9*3 + 7} tiles")
print("Done!")
