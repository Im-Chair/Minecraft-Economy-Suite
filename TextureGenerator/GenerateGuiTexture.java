import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GenerateGuiTexture {

    // 顏色定義
    private static final Color BG_COLOR = new Color(198, 198, 198);
    private static final Color FRAME_DARK = new Color(55, 55, 55);
    private static final Color FRAME_LIGHT = new Color(255, 255, 255);
    private static final Color SLOT_BG = new Color(139, 139, 139);

    public static void main(String[] args) {
        System.out.println("開始生成所有 GUI 貼圖...");
        try {
            generateScrapYardGui();
            generateArtisanGui();
            generateSteleGui();
            generateBountyBoardGui(); // 【新增】呼叫懸賞板 GUI 生成方法
            System.out.println("成功！所有 GUI 貼圖已生成。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateScrapYardGui() throws IOException {
        int width = 256;
        int height = 256;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        drawBeveledFrame(g2d, 0, 0, 176, 166);
        drawSlot(g2d, 80, 36);
        drawPlayerInventory(g2d, 8, 84);
        g2d.dispose();
        ImageIO.write(image, "png", new File("scrap_yard_gui.png"));
    }

    private static void generateArtisanGui() throws IOException {
        int width = 256;
        int height = 256;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        drawBeveledFrame(g2d, 0, 0, 176, 222);
        g2d.setColor(SLOT_BG);
        g2d.fillRect(8, 16, 160, 115);
        drawPlayerInventory(g2d, 8, 139);
        g2d.dispose();
        ImageIO.write(image, "png", new File("artisan_gui.png"));
    }

    private static void generateSteleGui() throws IOException {
        int width = 256;
        int height = 256;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        drawBeveledFrame(g2d, 0, 0, 176, 166);
        drawPlayerInventory(g2d, 8, 84);
        g2d.dispose();
        ImageIO.write(image, "png", new File("stele_gui.png"));
    }
    
    // 【新增】生成懸賞板 GUI 的方法
    private static void generateBountyBoardGui() throws IOException {
        int width = 256;
        int height = 256;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // 我們為懸賞板設計的 GUI 高度是 222
        int guiWidth = 220;
        int guiHeight = 222;

        drawBeveledFrame(g2d, 0, 0, guiWidth, guiHeight);
        // 為未來的任務列表預留一個背景區域
        g2d.setColor(SLOT_BG);
        g2d.fillRect(8, 16, 204, 115);
        
        // 根據新寬度，重新計算玩家物品欄的置中起始位置
        int playerInvX = (guiWidth - 162) / 2; // 162 是 9 個 18px 格子的總寬度
        drawPlayerInventory(g2d, playerInvX, 139);

        g2d.dispose();
        ImageIO.write(image, "png", new File("bounty_board_gui.png"));
    }

    // --- 輔助繪圖方法 ---
    private static void drawBeveledFrame(Graphics2D g, int x, int y, int width, int height) { g.setColor(BG_COLOR); g.fillRect(x, y, width, height); g.setColor(FRAME_LIGHT); g.drawLine(x, y, x + width - 2, y); g.drawLine(x, y, x, y + height - 2); g.setColor(FRAME_DARK); g.drawLine(x + width - 1, y, x + width - 1, y + height - 1); g.drawLine(x, y + height - 1, x + width - 1, y + height - 1); g.setColor(SLOT_BG); g.drawRect(x + 1, y + 1, width - 4, height - 4); }
    private static void drawSlot(Graphics2D g, int x, int y) { g.setColor(FRAME_DARK); g.drawRect(x - 1, y - 1, 17, 17); g.setColor(BG_COLOR); g.drawRect(x, y, 15, 15); }
    private static void drawPlayerInventory(Graphics2D g, int startX, int startY) { for (int row = 0; row < 3; ++row) { for (int col = 0; col < 9; ++col) { drawSlot(g, startX + col * 18, startY + row * 18); } } for (int col = 0; col < 9; ++col) { drawSlot(g, startX + col * 18, startY + 58); } }
    private static void addNoise(BufferedImage image, int amount, int width, int height) { /* 省略，保持不變 */ }
}