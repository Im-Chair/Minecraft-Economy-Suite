import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class TextureGenerator2 {

    static BufferedImage create() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 16, 16);
        g.dispose();
        return img;
    }

    static void setPixel(BufferedImage img, int x, int y, int argb) {
        if (x >= 0 && x < 16 && y >= 0 && y < 16) img.setRGB(x, y, argb);
    }

    static void fillRect(BufferedImage img, int x, int y, int w, int h, int argb) {
        for (int dy = 0; dy < h; dy++)
            for (int dx = 0; dx < w; dx++)
                setPixel(img, x + dx, y + dy, argb);
    }

    static void fillOval(BufferedImage img, int x, int y, int w, int h, int argb) {
        double cx = x + w / 2.0, cy = y + h / 2.0;
        double rx = w / 2.0, ry = h / 2.0;
        for (int py = y; py < y + h; py++)
            for (int px = x; px < x + w; px++) {
                double dx = (px + 0.5 - cx) / rx, dy = (py + 0.5 - cy) / ry;
                if (dx * dx + dy * dy <= 1.0) setPixel(img, px, py, argb);
            }
    }

    static void save(BufferedImage img, String path) throws Exception {
        File f = new File(path);
        f.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", f);
        System.out.println("已儲存: " + path);
    }

    // ── 掛鎖 ──────────────────────────────────────────────────────────────
    static void genPadlock() throws Exception {
        BufferedImage img = create();
        int SHACKLE  = 0xFFCCCCCC, SHACKLE_D = 0xFF888888;
        int BODY     = 0xFFB8860B, BODY_H    = 0xFFFFD700, BODY_S = 0xFF8B6914;
        int KEYHOLE  = 0xFF1A1000;

        // shackle arc
        fillRect(img, 5, 1, 6, 2, SHACKLE);   // top bar
        fillRect(img, 5, 1, 2, 6, SHACKLE);   // left leg
        fillRect(img, 9, 1, 2, 6, SHACKLE);   // right leg
        fillRect(img, 5, 6, 2, 1, SHACKLE_D); // left leg bottom
        fillRect(img, 9, 6, 2, 1, SHACKLE_D); // right leg bottom

        // body
        fillRect(img, 3, 7, 10, 7, BODY);
        // top highlight strip
        fillRect(img, 4, 7,  8, 1, BODY_H);
        fillRect(img, 4, 8,  1, 5, BODY_H);
        // right+bottom shadow
        fillRect(img,12, 8,  1, 5, BODY_S);
        fillRect(img, 4,13,  9, 1, BODY_S);

        // keyhole circle + slot
        fillOval(img, 6, 9, 4, 3, KEYHOLE);
        fillRect(img, 7,11, 2, 2, KEYHOLE);

        save(img, "src/main/resources/assets/economycore/textures/item/padlock.png");
    }

    // ── 萬能鑰匙 ──────────────────────────────────────────────────────────
    static void genMasterKey() throws Exception {
        BufferedImage img = create();
        int GOLD  = 0xFFFFD700, GOLD_H = 0xFFFFF0A0, GOLD_S = 0xFFC8A000, GOLD_D = 0xFF8B6914;
        int HOLE  = 0xFF1A1000;

        // bow ring
        fillOval(img, 1, 2, 8, 8, GOLD);
        fillOval(img, 3, 4, 4, 4, HOLE);  // hole
        // bow highlight arc (top-left)
        fillRect(img, 2, 3, 4, 1, GOLD_H);
        fillRect(img, 2, 4, 1, 3, GOLD_H);
        // bow shadow (bottom-right)
        fillRect(img, 2, 9, 5, 1, GOLD_S);
        fillRect(img, 8, 5, 1, 4, GOLD_S);

        // shaft
        fillRect(img, 8, 5, 7, 2, GOLD);
        fillRect(img, 8, 5, 7, 1, GOLD_H); // top highlight
        fillRect(img, 8, 6, 7, 1, GOLD_S); // bottom shadow

        // teeth
        fillRect(img, 10, 7, 2, 2, GOLD);
        fillRect(img, 13, 7, 2, 1, GOLD);
        fillRect(img, 10, 8, 2, 1, GOLD_D);
        fillRect(img, 13, 7, 2, 1, GOLD_D);

        save(img, "src/main/resources/assets/economycore/textures/item/master_key.png");
    }

    // ── 次元迴響 ──────────────────────────────────────────────────────────
    static void genDimensionalEcho() throws Exception {
        BufferedImage img = create();
        int VOID_B  = 0xFF200040, PURPLE = 0xFF9B30FF, MID = 0xFFC070FF;
        int GLOW    = 0xFFE0BBFF, WHITE  = 0xFFFFFFFF, CRACK = 0xFF100020;

        // outer shadow ring
        fillOval(img, 1, 1, 14, 14, VOID_B);
        // main purple orb
        fillOval(img, 2, 2, 12, 12, PURPLE);
        // inner bright
        fillOval(img, 4, 3, 8, 8,  MID);
        // core glow
        fillOval(img, 5, 4, 5, 4,  GLOW);

        // rift cracks
        int[][] cracks = {{5,5,3,9},{10,4,12,9},{4,9,10,12}};
        for (int[] c : cracks) {
            int x0=c[0], y0=c[1], x1=c[2], y1=c[3];
            int steps = Math.max(Math.abs(x1-x0), Math.abs(y1-y0));
            for (int s=0; s<=steps; s++) {
                int px = x0 + (x1-x0)*s/steps, py = y0 + (y1-y0)*s/steps;
                setPixel(img, px, py, CRACK);
            }
        }

        // sparkle top-left
        setPixel(img, 5, 3, WHITE);
        setPixel(img, 6, 3, GLOW);
        setPixel(img, 4, 4, GLOW);

        save(img, "src/main/resources/assets/economycore/textures/item/dimensional_echo.png");
    }

    // ── 星界釣竿 ──────────────────────────────────────────────────────────
    static void genAstralFishingRod() throws Exception {
        BufferedImage img = create();
        int ROD_D  = 0xFF3333AA, ROD   = 0xFF5555CC, ROD_L = 0xFF8888FF;
        int CYAN   = 0xFF00AAFF, STAR  = 0xFFCCEEFF, TIP   = 0xFFFFFFAA;

        // rod body (diagonal, bottom-left to top-right)
        int[][] rod = {
            {0,15,ROD_D},{1,14,ROD_D},{2,13,ROD},{3,12,ROD},
            {4,11,ROD},  {5,10,ROD},  {6,9,ROD}, {7,8,ROD_L},
            {8,7,ROD_L}, {9,6,ROD_L}, {10,5,ROD_L},{11,4,ROD_L},
            {12,3,ROD_L},{13,2,ROD_L},{14,1,ROD_L},{15,0,TIP}
        };
        for (int[] p : rod) setPixel(img, p[0], p[1], p[2]);

        // sparkles along the rod
        int[][] sparks = {
            {2,14,CYAN}, {4,12,STAR}, {1,13,STAR},
            {6,10,CYAN}, {8,8,STAR},  {5,11,STAR},
            {10,6,CYAN}, {12,4,STAR}, {11,5,STAR},
            {14,2,STAR}, {13,3,CYAN}
        };
        for (int[] s : sparks) setPixel(img, s[0], s[1], s[2]);

        save(img, "src/main/resources/assets/economycore/textures/item/astral_fishing_rod.png");
    }

    // ── 終界鮭魚 ──────────────────────────────────────────────────────────
    static void genEnderSalmon() throws Exception {
        BufferedImage img = create();
        int BODY  = 0xFF7040A0, BELLY = 0xFF9060C0, DARK = 0xFF402060;
        int FIN   = 0xFF5030A0, EYE   = 0xFF100020, GLOW = 0xFFAA44FF;
        int SHINE = 0xFFC090E0;

        // tail
        fillRect(img, 0, 5, 2, 6, FIN);
        setPixel(img, 1, 4, FIN); setPixel(img, 1, 11, FIN);

        // body outline
        fillRect(img, 2, 4, 10, 8, BODY);
        fillRect(img, 3, 3,  8, 1, BODY);  // top curve
        fillRect(img, 3,12,  8, 1, BODY);  // bottom curve

        // belly
        fillRect(img, 3, 7, 7, 3, BELLY);

        // dorsal fin
        fillRect(img, 5, 2, 4, 2, FIN);
        setPixel(img, 6, 1, FIN); setPixel(img, 7, 1, FIN);

        // head
        fillRect(img,11, 5, 3, 6, DARK);
        setPixel(img,12, 4, DARK); setPixel(img,12,11, DARK);

        // eye
        fillRect(img,12, 7, 2, 2, EYE);
        setPixel(img, 12, 7, GLOW);

        // shimmer highlights
        setPixel(img, 4, 5, SHINE); setPixel(img, 7, 6, SHINE);
        setPixel(img, 5, 9, SHINE); setPixel(img, 9, 8, SHINE);

        save(img, "src/main/resources/assets/economycore/textures/item/ender_salmon.png");
    }

    // ── 熔岩鱈魚 ──────────────────────────────────────────────────────────
    static void genLavaCod() throws Exception {
        BufferedImage img = create();
        int BODY  = 0xFFCC4400, LAVA1 = 0xFFFF6600, LAVA2 = 0xFFFF8800;
        int DARK  = 0xFF882200, FIN   = 0xFFAA3300;
        int EYE   = 0xFF000000, EGLOW = 0xFFFF4400, GLOW  = 0xFFFFCC44;

        // tail
        fillRect(img, 0, 5, 2, 6, DARK);
        setPixel(img, 1, 4, DARK); setPixel(img, 1, 11, DARK);

        // body outline
        fillRect(img, 2, 4, 10, 8, BODY);
        fillRect(img, 3, 3,  8, 1, BODY);
        fillRect(img, 3,12,  8, 1, BODY);

        // lava patches
        fillRect(img, 3, 6, 3, 3, LAVA1);
        fillRect(img, 7, 5, 3, 4, LAVA1);
        fillRect(img, 4, 7, 2, 2, LAVA2);  // hot core
        fillRect(img, 8, 6, 1, 2, LAVA2);
        setPixel(img, 5, 7, GLOW);          // brightest spot
        setPixel(img, 5, 8, GLOW);

        // dorsal fin
        fillRect(img, 5, 2, 4, 2, FIN);
        setPixel(img, 6, 1, FIN); setPixel(img, 7, 1, FIN);

        // head
        fillRect(img,11, 5, 3, 6, DARK);
        setPixel(img,12, 4, DARK); setPixel(img,12,11, DARK);

        // eye
        fillRect(img,12, 7, 2, 2, EYE);
        setPixel(img,12, 7, EGLOW);

        // glow highlights
        setPixel(img, 3, 5, LAVA1); setPixel(img,10, 6, LAVA1);
        setPixel(img, 3,10, LAVA1); setPixel(img,10, 9, LAVA1);

        save(img, "src/main/resources/assets/economycore/textures/item/lava_cod.png");
    }

    public static void main(String[] args) throws Exception {
        genPadlock();
        genMasterKey();
        genDimensionalEcho();
        genAstralFishingRod();
        genEnderSalmon();
        genLavaCod();
    }
}
