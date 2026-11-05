"""
生成 皇家交易所 的方塊材質 (16x16) 和 GUI 材質 (256x256)
"""
from PIL import Image, ImageDraw

# ─── 工具函式 ──────────────────────────────────────────────────────────────────

def slot_frame(draw, x, y):
    """畫標準 18x18 物品槽外框（深色外邊 + 亮色內邊 + 灰色底）"""
    draw.rectangle([x, y, x+17, y+17], fill=(139,139,139))   # 底色
    draw.line([(x, y), (x+16, y)], fill=(55,55,55))           # 上外
    draw.line([(x, y), (x, y+16)], fill=(55,55,55))           # 左外
    draw.line([(x+17, y), (x+17, y+17)], fill=(255,255,255))  # 右外
    draw.line([(x, y+17), (x+17, y+17)], fill=(255,255,255))  # 下外
    draw.line([(x+1, y+1), (x+15, y+1)], fill=(55,55,55))     # 上內
    draw.line([(x+1, y+1), (x+1, y+15)], fill=(55,55,55))     # 左內
    draw.line([(x+16, y+1), (x+16, y+16)], fill=(198,198,198))# 右內
    draw.line([(x+1, y+16), (x+16, y+16)], fill=(198,198,198))# 下內

# ─── 方塊材質 16x16 ────────────────────────────────────────────────────────────

block = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
bd = ImageDraw.Draw(block)

# 深金/石磚底色
for y in range(16):
    for x in range(16):
        is_border = (x + y) % 2 == 0
        base = (180, 140, 40) if not is_border else (160, 120, 30)
        block.putpixel((x, y), base + (255,))

# 皇冠外形（5px 寬，4px 高，置中）
crown_x, crown_y = 5, 5
crown_color = (255, 220, 60, 255)
# 底橫線
for x in range(6): block.putpixel((crown_x + x, crown_y + 3), crown_color)
# 三個齒
block.putpixel((crown_x,   crown_y + 1), crown_color)
block.putpixel((crown_x,   crown_y    ), crown_color)
block.putpixel((crown_x+3, crown_y + 1), crown_color)
block.putpixel((crown_x+3, crown_y    ), crown_color)
block.putpixel((crown_x+5, crown_y + 1), crown_color)
block.putpixel((crown_x+5, crown_y    ), crown_color)
block.putpixel((crown_x+1, crown_y + 2), crown_color)
block.putpixel((crown_x+2, crown_y + 2), crown_color)
block.putpixel((crown_x+3, crown_y + 2), crown_color)
block.putpixel((crown_x+4, crown_y + 2), crown_color)
block.putpixel((crown_x+5, crown_y + 2), crown_color)

block.save("src/main/resources/assets/economycore/textures/block/royal_exchange.png")
print("Block texture saved.")

# ─── GUI 材質 256x256 (可見面板 176x240) ─────────────────────────────────────

W, H = 256, 256
gui = Image.new("RGBA", (W, H), (0, 0, 0, 0))
gd = ImageDraw.Draw(gui)

BG      = (198, 198, 198, 255)   # 標準 MC 面板灰
DARK_BG = (40,  40,  40,  255)   # 標題列深色
TITLE_SEP = (80, 80, 80, 255)    # 標題分隔線
SEP     = (100, 100, 100, 255)   # 背包分隔線
ROW_A   = (185, 185, 185, 255)   # 交替列 A
ROW_B   = (175, 175, 175, 255)   # 交替列 B
SELL_BG = (160, 160, 100, 50)    # 出售區域淡黃底（RGBA）

PW, PH = 176, 240   # 面板寬高

# 1. 主面板背景
gd.rectangle([0, 0, PW-1, PH-1], fill=BG)

# 2. 標題深色列 (y=0-13)
gd.rectangle([0, 0, PW-1, 13], fill=DARK_BG)
gd.line([(0, 13), (PW-1, 13)], fill=TITLE_SEP)

# 3. 數量按鈕底色 (y=13-27) — 稍暗，視覺分區
gd.rectangle([0, 13, PW-1, 27], fill=(180, 180, 180, 255))
gd.line([(0, 27), (PW-1, 27)], fill=TITLE_SEP)

# 4. 物品列交替底色 (4 列，各 18px，從 y=27 開始)
FIRST_ROW = 27
for i in range(4):
    color = ROW_A if i % 2 == 0 else ROW_B
    gd.rectangle([0, FIRST_ROW + i*18, PW-1, FIRST_ROW + (i+1)*18 - 1], fill=color)

# 5. 頁面導覽區 (y=99-116)
gd.rectangle([0, 99, PW-1, 116], fill=(188, 188, 188, 255))
gd.line([(0, 99), (PW-1, 99)], fill=TITLE_SEP)
gd.line([(0, 116), (PW-1, 116)], fill=TITLE_SEP)

# 6. 出售區域淡底 (y=117-142)
sell_overlay = Image.new("RGBA", (PW, 26), (200, 200, 120, 60))
gui.paste(sell_overlay, (0, 117), sell_overlay)

# 7. 出售槽框架 (menu slot 8,120 → frame 7,119)
slot_frame(gd, 7, 119)

# 8. 分隔線 y=143
gd.line([(7, 143), (PW-8, 143)], fill=SEP)
gd.line([(7, 144), (PW-8, 144)], fill=(130, 130, 130, 255))

# 9. 玩家背包欄框 (menu y=158 → frame y=157, 3×9)
for i in range(3):
    for j in range(9):
        slot_frame(gd, 7 + j*18, 157 + i*18)

# 10. 快捷欄框 (menu y=216 → frame y=215, 1×9)
for j in range(9):
    slot_frame(gd, 7 + j*18, 215)

# 11. 快捷欄分隔線
gd.line([(7, 211), (PW-8, 211)], fill=SEP)

# 確保面板邊框明顯
gd.rectangle([0, 0, PW-1, PH-1], outline=(0, 0, 0, 200))

gui.save("src/main/resources/assets/economycore/textures/gui/royal_exchange_gui.png")
print("GUI texture saved.")
print("Done!")
