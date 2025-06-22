import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateBlockTexture {

    public static void main(String[] args) {
        System.out.println("開始生成方塊與物品貼圖...");
        try {
            //generateAncientSteleItem();
            //generateBountyBoardBlock();
            generateBoundaryStoneItem(); // 【新增】呼叫界碑石生成方法
            System.out.println("成功！方塊與物品貼圖已生成。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateAncientSteleItem() throws IOException {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(80, 80, 90));
        g2d.fillRect(2, 0, 12, 16);
        g2d.setColor(new Color(100, 100, 110));
        g2d.drawLine(2, 0, 13, 0);
        g2d.drawLine(2, 1, 2, 15);
        g2d.setColor(new Color(60, 60, 70));
        g2d.drawLine(14, 1, 14, 15);
        g2d.setColor(new Color(0, 255, 200));
        g2d.fillRect(7, 4, 2, 8);
        g2d.dispose();
        ImageIO.write(image, "png", new File("ancient_stele.png"));
    }

    private static void generateBountyBoardBlock() throws IOException {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        // 1. 繪製木板背景
        g2d.setColor(new Color(139, 99, 53)); // 深色木頭
        g2d.fillRect(0, 0, size, size);
        // 2. 繪製木板紋理
        g2d.setColor(new Color(160, 114, 61, 150)); // 半透明的淺色紋理
        for (int i = 0; i < size; i += 2) {
            g2d.drawLine(i, 0, i, size);
        }
        // 3. 繪製一張懸賞單 (羊皮紙顏色)
        g2d.setColor(new Color(245, 222, 179));
        g2d.fillRect(3, 3, 10, 10);
        // 4. 繪製懸賞單的邊框
        g2d.setColor(new Color(210, 180, 140));
        g2d.drawRect(3, 3, 9, 9);
        // 5. 繪製圖釘
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(3, 3, 2, 2);
        g2d.fillRect(11, 3, 2, 2);
        // 6. 繪製模擬的文字線條
        g2d.setColor(new Color(50, 50, 50));
        g2d.drawLine(5, 7, 10, 7);
        g2d.drawLine(5, 9, 10, 9);
        g2d.dispose();
        ImageIO.write(image, "png", new File("bounty_board.png"));
    }
    // 【新增】生成界碑石物品貼圖的方法
    private static void generateBoundaryStoneItem() throws IOException {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 1. 繪製石頭底色
        g2d.setColor(new Color(140, 140, 140));
        g2d.fillRect(0, 0, size, size);

        // 2. 繪製一個更深的內邊框
        g2d.setColor(new Color(110, 110, 110));
        g2d.fillRect(1, 1, 14, 14);
        
        // 3. 繪製一個更淺的、有雕刻感的斜面邊框
        g2d.setColor(new Color(160, 160, 160)); // 亮邊
        g2d.fillRect(2, 2, 12, 1);
        g2d.fillRect(2, 2, 1, 12);
        g2d.setColor(new Color(90, 90, 90)); // 暗邊
        g2d.fillRect(2, 13, 12, 1);
        g2d.fillRect(13, 2, 1, 12);
        
        // 4. 繪製中心的最淺色部分
        g2d.setColor(new Color(180, 180, 180));
        g2d.fillRect(3, 3, 10, 10);

        g2d.dispose();
        ImageIO.write(image, "png", new File("boundary_stone.png"));
    }
}