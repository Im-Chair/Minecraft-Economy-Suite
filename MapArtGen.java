import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class MapArtGen {

    static final int SZ = 128;
    static final String OUT = "src/main/resources/assets/economycore/textures/map_art/";
    static final Color GOLD  = new Color(218, 165, 32);
    static final Color DGOLD = new Color(140, 100, 10);
    static final Color IVORY = new Color(255, 248, 225);
    static final Color NAVY  = new Color(15, 25, 70);

    public static void main(String[] args) throws Exception {
        new File(OUT + "tarot").mkdirs();
        new File(OUT + "poker").mkdirs();
        new File(OUT + "mahjong").mkdirs();
        genPoker();
        genMahjong();
        genTarot();
        System.out.println("Done — 110 images generated.");
    }

    // ── shared helpers ───────────────────────────────────────────────────

    static Graphics2D setup(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        return g;
    }

    static void save(BufferedImage img, String path) throws Exception {
        ImageIO.write(img, "PNG", new File(path));
    }

    static void fillGrad(Graphics2D g, int x, int y, int w, int h, Color a, Color b) {
        for (int i = 0; i < h; i++) {
            float t = h == 1 ? 0 : (float) i / (h - 1);
            g.setColor(blend(a, b, t));
            g.fillRect(x, y + i, w, 1);
        }
    }

    static Color blend(Color a, Color b, float t) {
        return new Color(
            clamp((int)(a.getRed()   + t*(b.getRed()   - a.getRed()))),
            clamp((int)(a.getGreen() + t*(b.getGreen() - a.getGreen()))),
            clamp((int)(a.getBlue()  + t*(b.getBlue()  - a.getBlue()))),
            clamp((int)(a.getAlpha() + t*(b.getAlpha() - a.getAlpha())))
        );
    }

    static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    /** Filled concentric border layers, outermost first. */
    static void solidBorder(Graphics2D g, Color[] cs, int[] ts) {
        int off = 0;
        for (int i = 0; i < cs.length; i++) {
            int t = ts[i];
            g.setColor(cs[i]);
            g.fillRect(off, off, SZ - 2*off, t);
            g.fillRect(off, SZ - off - t, SZ - 2*off, t);
            g.fillRect(off, off + t, t, SZ - 2*off - 2*t);
            g.fillRect(SZ - off - t, off + t, t, SZ - 2*off - 2*t);
            off += t;
        }
    }

    static void star(Graphics2D g, int cx, int cy, int r, int n, Color c) {
        g.setColor(c);
        int[] xs = new int[n*2], ys = new int[n*2];
        for (int i = 0; i < n*2; i++) {
            double a = -Math.PI/2 + i * Math.PI / n;
            double rad = (i % 2 == 0) ? r : r * 0.42;
            xs[i] = cx + (int)(rad * Math.cos(a));
            ys[i] = cy + (int)(rad * Math.sin(a));
        }
        g.fillPolygon(xs, ys, n*2);
    }

    // ── suit shapes ──────────────────────────────────────────────────────

    static void heart(Graphics2D g, int cx, int cy, int sz) {
        g.fillOval(cx - sz, cy - sz*2/3, sz, sz);
        g.fillOval(cx,      cy - sz*2/3, sz, sz);
        g.fillPolygon(
            new int[]{cx - sz, cx + sz, cx},
            new int[]{cy - sz/6, cy - sz/6, cy + sz*4/5},
            3
        );
    }

    static void diamond(Graphics2D g, int cx, int cy, int sz) {
        g.fillPolygon(
            new int[]{cx, cx+sz, cx, cx-sz},
            new int[]{cy-sz, cy, cy+sz, cy},
            4
        );
    }

    static void spade(Graphics2D g, int cx, int cy, int sz) {
        g.fillOval(cx - sz, cy - sz/4, sz, sz);
        g.fillOval(cx,      cy - sz/4, sz, sz);
        g.fillPolygon(
            new int[]{cx - sz, cx + sz, cx},
            new int[]{cy + sz/4, cy + sz/4, cy - sz*4/5},
            3
        );
        g.fillRect(cx - sz/4, cy + sz*3/4, sz/2, sz*2/5);
        g.fillRect(cx - sz*2/3, cy + sz*3/4 + sz*2/5 - sz/5, sz*4/3, sz/5);
    }

    static void club(Graphics2D g, int cx, int cy, int sz) {
        int s = sz * 3 / 4;
        g.fillOval(cx - s/2, cy - s,   s, s);
        g.fillOval(cx - s,   cy - s/3, s, s);
        g.fillOval(cx,       cy - s/3, s, s);
        g.fillRect(cx - s/4, cy + s/5,            s/2, s*2/5);
        g.fillRect(cx - s*2/3, cy + s/5 + s*2/5 - s/5, s*4/3, s/5);
    }

    static void drawSuit(Graphics2D g, String suit, int cx, int cy, int sz) {
        switch (suit) {
            case "hearts":   heart(g, cx, cy, sz);   break;
            case "diamonds": diamond(g, cx, cy, sz); break;
            case "spades":   spade(g, cx, cy, sz);   break;
            case "clubs":    club(g, cx, cy, sz);    break;
        }
    }

    // ── poker ─────────────────────────────────────────────────────────────

    static void genPoker() throws Exception {
        String[] suits  = {"spades","hearts","diamonds","clubs"};
        boolean[] isRed = {false, true, true, false};
        String[]  ranks = {"a","2","3","4","5","6","7","8","9","10","j","q","k"};
        for (int s = 0; s < 4; s++)
            for (String r : ranks)
                pokerCard(r, suits[s], isRed[s]);
        jokerCard(true);
        jokerCard(false);
    }

    // pip grid: cols x=36,64,92  rows y=32,50,68,86,104
    static final int[] PX = {36, 64, 92};
    static final int[] PY = {32, 50, 68, 86, 104};

    // {colIdx, rowIdx, flipY}
    static int[][] pips(int n) {
        switch (n) {
            case 2:  return new int[][]{{1,0,0},{1,4,1}};
            case 3:  return new int[][]{{1,0,0},{1,2,0},{1,4,1}};
            case 4:  return new int[][]{{0,0,0},{2,0,0},{0,4,1},{2,4,1}};
            case 5:  return new int[][]{{0,0,0},{2,0,0},{1,2,0},{0,4,1},{2,4,1}};
            case 6:  return new int[][]{{0,0,0},{2,0,0},{0,2,0},{2,2,0},{0,4,1},{2,4,1}};
            case 7:  return new int[][]{{0,0,0},{2,0,0},{1,1,0},{0,2,0},{2,2,0},{0,4,1},{2,4,1}};
            case 8:  return new int[][]{{0,0,0},{2,0,0},{1,1,0},{0,2,0},{2,2,0},{1,3,1},{0,4,1},{2,4,1}};
            case 9:  return new int[][]{{0,0,0},{2,0,0},{0,1,0},{2,1,0},{1,2,0},{0,3,1},{2,3,1},{0,4,1},{2,4,1}};
            case 10: return new int[][]{{0,0,0},{2,0,0},{0,1,0},{2,1,0},{1,1,0},{0,3,1},{2,3,1},{1,3,1},{0,4,1},{2,4,1}};
            default: return new int[][]{};
        }
    }

    static void drawPips(Graphics2D g, int n, String suit, Color sc) {
        g.setColor(sc);
        for (int[] p : pips(n)) {
            int cx = PX[p[0]], cy = PY[p[1]];
            if (p[2] == 1) {
                AffineTransform saved = g.getTransform();
                g.rotate(Math.PI, cx, cy);
                drawSuit(g, suit, cx, cy, 9);
                g.setTransform(saved);
            } else {
                drawSuit(g, suit, cx, cy, 9);
            }
        }
    }

    static void pokerCard(String rank, String suit, boolean isRed) throws Exception {
        BufferedImage img = new BufferedImage(SZ, SZ, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setup(img);
        Color sc = isRed ? new Color(200, 0, 0) : new Color(15, 15, 15);

        // Background gradient (warm white)
        fillGrad(g, 0, 0, SZ, SZ, new Color(255,253,248), new Color(238,232,218));

        // Triple border: navy / gold / white
        solidBorder(g,
            new Color[]{NAVY, GOLD, new Color(235,230,215)},
            new int[]{2, 1, 1}
        );

        String label = rank.equals("a")?"A": rank.equals("j")?"J": rank.equals("q")?"Q": rank.equals("k")?"K": rank;

        // Top-left corner
        g.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(sc);
        g.drawString(label, 8, 20);
        g.setColor(sc);
        drawSuit(g, suit, 8 + fm.stringWidth(label)/2, 28, 5);

        // Bottom-right (rotated 180°)
        AffineTransform saved = g.getTransform();
        g.rotate(Math.PI, SZ/2.0, SZ/2.0);
        g.setColor(sc);
        g.drawString(label, 8, 20);
        drawSuit(g, suit, 8 + fm.stringWidth(label)/2, 28, 5);
        g.setTransform(saved);

        // Center design
        g.setColor(sc);
        if (rank.equals("a")) {
            drawSuit(g, suit, SZ/2, SZ/2, 28);
            // Gold decoration ring
            g.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 100));
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(SZ/2-36, SZ/2-36, 72, 72);
            Color gs = new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 180);
            for (int i = 0; i < 4; i++) {
                double a = Math.PI/4 + i*Math.PI/2;
                star(g, (int)(SZ/2+44*Math.cos(a)), (int)(SZ/2+44*Math.sin(a)), 5, 5, gs);
            }
        } else if ("jqk".contains(rank)) {
            drawFace(g, rank, suit, sc);
        } else {
            drawPips(g, Integer.parseInt(rank), suit, sc);
        }

        g.dispose();
        save(img, OUT + "poker/" + suit + "_" + rank + ".png");
    }

    static void drawFace(Graphics2D g, String rank, String suit, Color sc) {
        // Checkerboard background
        Color pat = new Color(sc.getRed(), sc.getGreen(), sc.getBlue(), 14);
        for (int y = 4; y < SZ-4; y += 6)
            for (int x = 4; x < SZ-4; x += 6)
                if ((x/6 + y/6) % 2 == 0) { g.setColor(pat); g.fillRect(x, y, 5, 5); }

        // Face panel
        g.setColor(new Color(255,251,242));
        g.fillRoundRect(18, 18, SZ-36, SZ-36, 8, 8);
        g.setColor(sc);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(18, 18, SZ-36, SZ-36, 8, 8);
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(1));
        g.drawRoundRect(21, 21, SZ-42, SZ-42, 6, 6);

        int cx = SZ/2, cy = SZ/2;

        if (rank.equals("k")) {
            // Crown
            g.setColor(GOLD);
            int[] kx = {cx-16,cx-16,cx-10,cx-5,cx,cx+5,cx+10,cx+16,cx+16};
            int[] ky = {cy-25,cy-15,cy-24,cy-15,cy-26,cy-15,cy-24,cy-15,cy-25};
            g.fillPolygon(kx, ky, kx.length);
            g.setColor(new Color(170,120,0));
            g.fillRect(cx-16, cy-15, 32, 7);
            g.setColor(new Color(200,0,50)); g.fillOval(cx-4,cy-13,8,8);
            g.setColor(new Color(0,80,200)); g.fillOval(cx-13,cy-13,6,6); g.fillOval(cx+7,cy-13,6,6);
            // Face
            g.setColor(new Color(255,215,175)); g.fillOval(cx-11,cy-8,22,24);
            g.setColor(new Color(120,80,40));   g.fillRect(cx-11,cy+15,22,5);
            // Body & sword
            g.setColor(sc); g.fillRect(cx-14,cy+20,28,22);
            g.setColor(new Color(180,185,200));
            g.setStroke(new BasicStroke(2.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx+18,cy-5,cx+8,cy+40);
            g.setColor(GOLD); g.setStroke(new BasicStroke(2));
            g.drawLine(cx+12,cy+8,cx+24,cy+8);
        } else if (rank.equals("q")) {
            // Tiara
            g.setColor(GOLD);
            g.fillArc(cx-16,cy-36,32,20,0,180);
            g.fillOval(cx-4,cy-38,8,8);
            g.setColor(new Color(200,0,100)); g.fillOval(cx-3,cy-37,6,6);
            // Hair
            g.setColor(new Color(100,70,20)); g.fillOval(cx-14,cy-26,28,28);
            // Face
            g.setColor(new Color(255,215,175)); g.fillOval(cx-11,cy-24,22,24);
            // Dress
            g.setColor(sc);
            g.fillPolygon(new int[]{cx-10,cx-16,cx+16,cx+10}, new int[]{cy+2,cy+40,cy+40,cy+2}, 4);
            // Collar
            g.setColor(new Color(255,255,220)); g.fillOval(cx-8,cy-2,16,10);
            // Rose
            g.setColor(new Color(200,30,60)); g.fillOval(cx-22,cy+10,10,10);
            g.setColor(new Color(200,30,30));
            for (int i=0;i<5;i++){double a=i*Math.PI*2/5;g.fillOval((int)(cx-17+7*Math.cos(a))-3,(int)(cy+15+7*Math.sin(a))-3,7,7);}
        } else { // jack
            // Jester hat
            g.setColor(sc);
            g.fillPolygon(new int[]{cx-16,cx,cx+16}, new int[]{cy-14,cy-36,cy-14}, 3);
            g.setColor(GOLD); g.fillOval(cx-5,cy-40,10,10);
            g.setColor(sc.darker()); g.fillRect(cx-16,cy-14,32,6);
            // Face
            g.setColor(new Color(255,215,175)); g.fillOval(cx-11,cy-8,22,24);
            // Collar bells
            g.setColor(GOLD);
            g.fillOval(cx-16,cy+16,10,10); g.fillOval(cx+6,cy+16,10,10);
            // Scepter
            g.setColor(new Color(170,130,20));
            g.setStroke(new BasicStroke(2.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx-22,cy,cx-22,cy+40);
            star(g, cx-22, cy, 7, 5, GOLD);
        }

        // Small suit symbols in face panel corners
        g.setColor(sc);
        drawSuit(g, suit, 26, 28, 5);
        AffineTransform s2 = g.getTransform();
        g.rotate(Math.PI, SZ/2.0, SZ/2.0);
        drawSuit(g, suit, 26, 28, 5);
        g.setTransform(s2);
    }

    static void jokerCard(boolean big) throws Exception {
        BufferedImage img = new BufferedImage(SZ, SZ, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setup(img);

        // Rainbow gradient
        Color[] rb = {new Color(220,50,50),new Color(220,140,30),new Color(200,200,30),
                      new Color(30,180,60),new Color(30,100,220),new Color(150,30,220)};
        int bh = SZ / rb.length;
        for (int i = 0; i < rb.length; i++)
            fillGrad(g, 0, i*bh, SZ, bh+1, rb[i], i+1<rb.length?rb[i+1]:rb[i]);

        solidBorder(g, new Color[]{new Color(255,255,255,220), GOLD}, new int[]{2,1});

        // Jester cap
        g.setColor(new Color(220,50,220));
        g.fillPolygon(new int[]{SZ/2,SZ/2-24,SZ/2-12}, new int[]{16,48,48},3);
        g.setColor(new Color(50,180,220));
        g.fillPolygon(new int[]{SZ/2,SZ/2+24,SZ/2+12}, new int[]{16,48,48},3);
        g.setColor(Color.WHITE); g.fillOval(SZ/2-6,10,12,12);

        // Face
        g.setColor(new Color(255,235,195)); g.fillOval(SZ/2-22,34,44,40);
        g.setColor(new Color(200,100,50));
        g.setStroke(new BasicStroke(2f)); g.drawArc(SZ/2-10,54,20,12,200,140);
        g.setColor(Color.BLACK); g.fillOval(SZ/2-10,42,7,7); g.fillOval(SZ/2+3,42,7,7);
        g.setColor(new Color(220,100,100,150));
        g.fillOval(SZ/2-20,52,10,7); g.fillOval(SZ/2+10,52,10,7);

        // Corner stars
        Color ws = new Color(255,255,255,220);
        star(g,12,12,8,5,ws); star(g,SZ-12,12,8,5,ws);
        star(g,12,SZ-12,8,5,ws); star(g,SZ-12,SZ-12,8,5,ws);

        // Text
        String txt = big ? "大JOKER" : "小Joker";
        g.setFont(new Font("Arial", Font.BOLD, big ? 15 : 13));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(0,0,0,120));
        g.drawString(txt, (SZ-fm.stringWidth(txt))/2+1, SZ-8+1);
        g.setColor(Color.WHITE);
        g.drawString(txt, (SZ-fm.stringWidth(txt))/2, SZ-8);

        g.dispose();
        save(img, OUT + "poker/joker_" + (big?"big":"small") + ".png");
    }

    // ── mahjong ───────────────────────────────────────────────────────────

    static void genMahjong() throws Exception {
        String[] nums = {"一","二","三","四","五","六","七","八","九"};
        for (int i = 1; i <= 9; i++) {
            wanTile(i, nums[i-1]);
            tongTile(i);
            suoTile(i);
        }
        windTile("東", new Color(180,80,0),  "dong");
        windTile("南", new Color(180,0,0),   "nan");
        windTile("西", new Color(80,0,180),  "xi");
        windTile("北", new Color(0,100,0),   "bei");
        dragonTile("中", new Color(220,0,0),   new Color(255,252,248), "zhong");
        dragonTile("發", new Color(220,170,0), new Color(0,120,0),     "fa");
        whiteTile();
    }

    static Font cjk(int style, int size) {
        for (String n : new String[]{"Microsoft JhengHei","微軟正黑體","MingLiU","SansSerif"}) {
            Font f = new Font(n, style, size);
            if (f.canDisplay('一')) return f;
        }
        return new Font("SansSerif", style, size);
    }

    static BufferedImage baseTile(Color b1, Color b2) {
        BufferedImage img = new BufferedImage(SZ, SZ, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setup(img);
        fillGrad(g, 0, 0, SZ, SZ, new Color(255,250,230), new Color(242,232,200));
        solidBorder(g, new Color[]{new Color(50,80,20), b1, new Color(240,232,200), b2}, new int[]{2,2,1,1});
        Color gs = new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 180);
        star(g,12,12,4,4,gs); star(g,SZ-12,12,4,4,gs);
        star(g,12,SZ-12,4,4,gs); star(g,SZ-12,SZ-12,4,4,gs);
        g.dispose();
        return img;
    }

    static void wanTile(int n, String numChar) throws Exception {
        BufferedImage img = baseTile(new Color(160,60,0), new Color(200,80,0));
        Graphics2D g = setup(img);
        Color crimson = new Color(185,15,15);
        // Number
        g.setFont(cjk(Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(0,0,0,50));
        g.drawString(numChar, (SZ-fm.stringWidth(numChar))/2+2, 66+2);
        g.setColor(crimson);
        g.drawString(numChar, (SZ-fm.stringWidth(numChar))/2, 66);
        // 萬
        g.setFont(cjk(Font.BOLD, 30));
        fm = g.getFontMetrics();
        g.setColor(new Color(140,10,10));
        g.drawString("萬", (SZ-fm.stringWidth("萬"))/2, 100);
        // Divider
        g.setColor(GOLD); g.setStroke(new BasicStroke(1));
        g.drawLine(18, 72, SZ-18, 72);
        g.dispose();
        save(img, OUT + "mahjong/wan_" + n + ".png");
    }

    static int[][] tongPos(int n) {
        int cx=SZ/2, m=21;
        switch(n){
            case 1: return new int[][]{{cx,64}};
            case 2: return new int[][]{{cx,44},{cx,84}};
            case 3: return new int[][]{{cx,30},{cx,64},{cx,98}};
            case 4: return new int[][]{{cx-m,44},{cx+m,44},{cx-m,84},{cx+m,84}};
            case 5: return new int[][]{{cx-m,40},{cx+m,40},{cx,64},{cx-m,88},{cx+m,88}};
            case 6: return new int[][]{{cx-m,34},{cx+m,34},{cx-m,64},{cx+m,64},{cx-m,94},{cx+m,94}};
            case 7: return new int[][]{{cx-m,30},{cx+m,30},{cx,50},{cx-m,66},{cx+m,66},{cx-m,90},{cx+m,90}};
            case 8: return new int[][]{{cx-m,28},{cx+m,28},{cx-m,50},{cx+m,50},{cx-m,72},{cx+m,72},{cx-m,94},{cx+m,94}};
            case 9: return new int[][]{{cx-m,26},{cx,26},{cx+m,26},{cx-m,52},{cx,52},{cx+m,52},{cx-m,78},{cx,78},{cx+m,78}};
            default: return new int[][]{{cx,64}};
        }
    }

    static void tongTile(int n) throws Exception {
        BufferedImage img = baseTile(new Color(0,60,160), new Color(20,80,200));
        Graphics2D g = setup(img);
        int r = n<=2?16: n<=5?13: n<=7?11: 9;
        for (int[] p : tongPos(n)) {
            int cx=p[0], cy=p[1];
            g.setColor(new Color(20,80,200));  g.fillOval(cx-r,cy-r,r*2,r*2);
            g.setColor(IVORY);                 g.fillOval(cx-r+2,cy-r+2,r*2-4,r*2-4);
            g.setColor(GOLD);                  g.fillOval(cx-r+4,cy-r+4,r*2-8,r*2-8);
            g.setColor(new Color(255,255,255,90)); g.fillOval(cx-r+3,cy-r+3,r*2/3,r*2/3);
        }
        g.dispose();
        save(img, OUT + "mahjong/tong_" + n + ".png");
    }

    static int[][] suoPos(int n) {
        int ty=14,by=114;
        switch(n){
            case 1: return new int[][]{{SZ/2,ty,by}};
            case 2: return new int[][]{{44,ty,by},{84,ty,by}};
            case 3: return new int[][]{{34,ty,by},{64,ty,by},{94,ty,by}};
            case 4: return new int[][]{{34,ty,66},{64,ty,66},{34,66,by},{64,66,by}};
            case 5: return new int[][]{{34,ty,62},{64,ty,62},{94,ty,62},{46,62,by},{80,62,by}};
            case 6: return new int[][]{{34,ty,62},{64,ty,62},{94,ty,62},{34,62,by},{64,62,by},{94,62,by}};
            case 7: return new int[][]{{26,ty,60},{50,ty,60},{74,ty,60},{98,ty,60},{36,60,by},{64,60,by},{92,60,by}};
            case 8: return new int[][]{{26,ty,60},{50,ty,60},{74,ty,60},{98,ty,60},{26,60,by},{50,60,by},{74,60,by},{98,60,by}};
            case 9: return new int[][]{{20,ty,58},{44,ty,58},{68,ty,58},{92,ty,58},{108,ty,58},{30,58,by},{54,58,by},{78,58,by},{102,58,by}};
            default: return new int[][]{{SZ/2,ty,by}};
        }
    }

    static void bamboo(Graphics2D g, int x, int y1, int y2, int pw) {
        int hw = pw/2;
        g.setColor(new Color(30,140,30));
        g.fillRoundRect(x-hw, y1, pw, y2-y1, pw, pw);
        g.setColor(new Color(120,210,80));
        g.fillRect(x-hw+1, y1+2, Math.max(1,pw/3), y2-y1-4);
        g.setColor(new Color(10,80,10));
        for (int ky = y1+10; ky < y2-4; ky+=14)
            g.fillRoundRect(x-hw-1, ky, pw+2, 4, 2, 2);
    }

    static void suoTile(int n) throws Exception {
        BufferedImage img = baseTile(new Color(0,90,10), new Color(20,130,20));
        Graphics2D g = setup(img);
        int pw = n<=4?8: n<=7?7: 6;
        if (n == 1) {
            bamboo(g, SZ/2, 40, 114, 8);
            // Bird
            g.setColor(new Color(30,140,180)); g.fillOval(42,22,36,24);
            g.setColor(new Color(20,100,140)); g.fillOval(44,24,28,18);
            g.setColor(new Color(240,200,0));
            g.fillPolygon(new int[]{78,88,78}, new int[]{29,32,35}, 3);
            g.setColor(Color.BLACK); g.fillOval(72,26,5,5);
        } else {
            for (int[] p : suoPos(n))
                bamboo(g, p[0], p[1], p[2], pw);
        }
        g.dispose();
        save(img, OUT + "mahjong/suo_" + n + ".png");
    }

    static void windTile(String ch, Color cc, String id) throws Exception {
        BufferedImage img = baseTile(new Color(cc.getRed()/2,cc.getGreen()/2,cc.getBlue()/2), cc);
        Graphics2D g = setup(img);
        g.setFont(cjk(Font.BOLD, 66));
        FontMetrics fm = g.getFontMetrics();
        int tx = (SZ-fm.stringWidth(ch))/2;
        g.setColor(new Color(0,0,0,60)); g.drawString(ch, tx+2, 84+2);
        g.setColor(cc);                  g.drawString(ch, tx,   84);
        g.dispose();
        save(img, OUT + "mahjong/" + id + ".png");
    }

    static void dragonTile(String ch, Color cc, Color bg, String id) throws Exception {
        BufferedImage img = new BufferedImage(SZ, SZ, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setup(img);
        fillGrad(g, 0, 0, SZ, SZ, blend(bg,Color.WHITE,0.3f), blend(bg,Color.BLACK,0.2f));
        Color bd = cc.darker();
        solidBorder(g, new Color[]{bd, cc, IVORY, bd}, new int[]{2,2,1,1});
        Color gs = new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),200);
        star(g,12,12,4,4,gs); star(g,SZ-12,12,4,4,gs);
        star(g,12,SZ-12,4,4,gs); star(g,SZ-12,SZ-12,4,4,gs);
        if (!ch.isEmpty()) {
            g.setFont(cjk(Font.BOLD, 70));
            FontMetrics fm = g.getFontMetrics();
            int tx = (SZ-fm.stringWidth(ch))/2;
            g.setColor(new Color(0,0,0,80)); g.drawString(ch, tx+2, 88+2);
            g.setColor(cc);                  g.drawString(ch, tx,   88);
        } else {
            // 白板: nested blue rectangles
            g.setColor(new Color(100,120,220,100));
            g.setStroke(new BasicStroke(1));
            g.drawRect(14,14,SZ-28,SZ-28);
            g.drawRect(20,20,SZ-40,SZ-40);
            g.drawRect(26,26,SZ-52,SZ-52);
        }
        g.dispose();
        save(img, OUT + "mahjong/" + id + ".png");
    }

    static void whiteTile() throws Exception {
        BufferedImage img = new BufferedImage(SZ, SZ, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setup(img);
        fillGrad(g, 0, 0, SZ, SZ, new Color(255,255,255), new Color(228,232,252));
        solidBorder(g,
            new Color[]{new Color(60,60,160), new Color(100,100,220), new Color(230,232,255), new Color(80,80,180)},
            new int[]{2,2,1,1}
        );
        g.setColor(new Color(100,120,220,100));
        g.setStroke(new BasicStroke(1));
        g.drawRect(14,14,SZ-28,SZ-28);
        g.drawRect(20,20,SZ-40,SZ-40);
        g.drawRect(26,26,SZ-52,SZ-52);
        Color bs = new Color(100,120,220,200);
        star(g,12,12,4,4,bs); star(g,SZ-12,12,4,4,bs);
        star(g,12,SZ-12,4,4,bs); star(g,SZ-12,SZ-12,4,4,bs);
        g.dispose();
        save(img, OUT + "mahjong/bai.png");
    }

    // ── tarot ─────────────────────────────────────────────────────────────

    static final String[] ROMAN = {"0","I","II","III","IV","V","VI","VII","VIII","IX","X",
                                    "XI","XII","XIII","XIV","XV","XVI","XVII","XVIII","XIX","XX","XXI"};

    static void genTarot() throws Exception {
        Object[][] cards = {
            {"fool",      "愚者",    0,  new Color(200,180,30),  new Color(80,170,255)},
            {"magician",  "魔術師",  1,  new Color(170,20,20),   GOLD},
            {"priestess", "女祭司",  2,  new Color(15,45,170),   new Color(180,210,255)},
            {"empress",   "皇后",    3,  new Color(25,110,25),   new Color(210,170,50)},
            {"emperor",   "皇帝",    4,  new Color(150,15,15),   new Color(200,155,0)},
            {"hierophant","教皇",    5,  new Color(100,15,170),  new Color(220,200,80)},
            {"lovers",    "戀人",    6,  new Color(200,55,85),   new Color(255,215,75)},
            {"chariot",   "戰車",    7,  new Color(15,50,150),   new Color(175,200,240)},
            {"strength",  "力量",    8,  new Color(190,100,10),  new Color(240,210,50)},
            {"hermit",    "隱者",    9,  new Color(65,65,65),    new Color(210,190,80)},
            {"wheel",     "命運之輪",10, new Color(80,15,150),   GOLD},
            {"justice",   "正義",    11, new Color(180,15,15),   new Color(220,190,60)},
            {"hanged",    "倒吊人",  12, new Color(15,110,110),  new Color(190,200,80)},
            {"death",     "死神",    13, new Color(15,15,15),    new Color(200,200,200)},
            {"temperance","節制",    14, new Color(15,70,170),   new Color(200,230,255)},
            {"devil",     "惡魔",    15, new Color(120,8,8),     new Color(255,120,30)},
            {"tower",     "高塔",    16, new Color(20,20,20),    new Color(220,90,20)},
            {"star",      "星星",    17, new Color(10,25,110),   new Color(220,210,80)},
            {"moon",      "月亮",    18, new Color(15,15,65),    new Color(180,190,230)},
            {"sun",       "太陽",    19, new Color(190,150,0),   new Color(255,120,20)},
            {"judgement", "審判",    20, new Color(15,60,140),   new Color(220,210,80)},
            {"world",     "世界",    21, new Color(60,15,120),   new Color(200,165,80)},
        };
        for (Object[] c : cards)
            tarotCard((String)c[0],(String)c[1],(int)c[2],(Color)c[3],(Color)c[4]);
    }

    static void tarotCard(String id, String name, int num, Color pri, Color acc) throws Exception {
        BufferedImage img = new BufferedImage(SZ, SZ, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setup(img);

        // Gradient background
        fillGrad(g, 0, 0, SZ, SZ, blend(pri,Color.BLACK,0.65f), blend(pri,Color.BLACK,0.15f));

        // Border layers
        solidBorder(g, new Color[]{Color.BLACK, GOLD, acc, DGOLD}, new int[]{2,1,1,1});

        // Subtle diamond pattern
        Color pat = new Color(acc.getRed(),acc.getGreen(),acc.getBlue(),16);
        for (int y=6;y<SZ-6;y+=10)
            for (int x=6;x<SZ-6;x+=10)
                if ((x/10+y/10)%2==0) { g.setColor(pat); diamond(g,x+5,y+5,4); }

        // Roman numeral
        String roman = num < ROMAN.length ? ROMAN[num] : String.valueOf(num);
        g.setFont(new Font("Times New Roman", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(acc);
        g.drawString(roman, (SZ-fm.stringWidth(roman))/2, 17);

        // Symbol
        drawTarotSym(g, id, pri, acc);

        // Name plate
        g.setColor(new Color(0,0,0,160));
        g.fillRoundRect(5, 108, SZ-10, 14, 4, 4);
        g.setColor(GOLD); g.setStroke(new BasicStroke(0.8f));
        g.drawRoundRect(5, 108, SZ-10, 14, 4, 4);
        g.setFont(cjk(Font.BOLD, 10));
        fm = g.getFontMetrics();
        g.setColor(acc);
        g.drawString(name, (SZ-fm.stringWidth(name))/2, 119);

        // Corner stars
        Color cs = new Color(acc.getRed(),acc.getGreen(),acc.getBlue(),180);
        star(g,11,11,5,5,cs); star(g,SZ-11,11,5,5,cs);
        star(g,11,SZ-11,5,5,cs); star(g,SZ-11,SZ-11,5,5,cs);

        g.dispose();
        save(img, OUT + "tarot/" + id + ".png");
    }

    static void drawTarotSym(Graphics2D g, String id, Color pri, Color acc) {
        // Central symbol area: cx=64, cy=58 (between numeral at y=17 and name at y=108)
        int cx = SZ/2, cy = 60;
        g.setColor(acc);
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch(id) {
        case "fool": {
            // Bright sun top-right
            g.setColor(new Color(255,220,50));
            g.fillOval(cx+6,cy-40,26,26);
            for(int i=0;i<8;i++){double a=i*Math.PI/4;
                g.setStroke(new BasicStroke(2));
                g.drawLine((int)(cx+19+13*Math.cos(a)),(int)(cy-27+13*Math.sin(a)),
                           (int)(cx+19+20*Math.cos(a)),(int)(cy-27+20*Math.sin(a)));}
            // Cliff
            g.setColor(new Color(40,160,40)); g.fillOval(cx-30,cy+8,40,28);
            // Figure
            g.setColor(acc); g.fillOval(cx-26,cy-12,13,13);
            g.fillRect(cx-23,cy+1,9,20);
            // Stick+bag
            g.setColor(new Color(180,130,50)); g.setStroke(new BasicStroke(2));
            g.drawLine(cx-18,cy+8,cx-4,cy-14);
            g.setColor(acc); g.fillOval(cx-8,cy-20,10,10);
            break;
        }
        case "magician": {
            // Horizontal figure-8 (lemniscate) above
            g.setColor(acc); g.setStroke(new BasicStroke(2.5f));
            g.drawOval(cx-22,cy-44,20,14); g.drawOval(cx+2,cy-44,20,14);
            // Wand pointing up
            g.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx,cy-32,cx,cy+14);
            g.setColor(new Color(255,255,80)); g.fillOval(cx-5,cy-38,10,10);
            // Table
            g.setColor(acc); g.setStroke(new BasicStroke(1));
            g.fillRect(cx-26,cy+14,52,4);
            // 4 suits on table
            g.setColor(new Color(200,0,0)); heart(g,cx-14,cy+22,5); diamond(g,cx+14,cy+22,5);
            g.setColor(new Color(10,10,10)); spade(g,cx-28,cy+22,5); club(g,cx+28,cy+22,5);
            break;
        }
        case "priestess": {
            // Crescent moon
            g.setColor(new Color(200,210,250)); g.fillOval(cx-18,cy-44,36,36);
            g.setColor(blend(pri,Color.BLACK,0.4f)); g.fillOval(cx-6,cy-46,36,36);
            // Two pillars
            g.setColor(new Color(180,180,220));
            g.fillRect(cx-28,cy-12,8,44); g.fillRect(cx+20,cy-12,8,44);
            // Scroll
            g.setColor(new Color(220,210,180)); g.fillRoundRect(cx-12,cy+4,24,16,4,4);
            g.setColor(new Color(80,60,40)); g.setStroke(new BasicStroke(1));
            g.drawLine(cx-8,cy+10,cx+8,cy+10); g.drawLine(cx-8,cy+14,cx+8,cy+14);
            break;
        }
        case "empress": {
            // Venus symbol
            g.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawOval(cx-18,cy-44,36,36); g.drawLine(cx,cy-8,cx,cy+20); g.drawLine(cx-10,cy+8,cx+10,cy+8);
            // Flower ring around circle
            g.setColor(new Color(50,200,50));
            for(int i=0;i<6;i++){double a=i*Math.PI/3; g.fillOval((int)(cx-4+18*Math.cos(a)),(int)(cy-26+8*Math.sin(a)),8,8);}
            g.setColor(new Color(220,80,100)); g.fillOval(cx-4,cy-46,8,8);
            break;
        }
        case "emperor": {
            // Throne silhouette
            g.setColor(acc); g.fillRect(cx-22,cy-38,44,58);
            g.setColor(blend(pri,Color.BLACK,0.35f)); g.fillRect(cx-16,cy-32,32,44);
            // Ram heads
            g.setColor(acc); g.fillOval(cx-28,cy-40,16,14); g.fillOval(cx+12,cy-40,16,14);
            // Ram horns
            g.setColor(new Color(180,150,80)); g.setStroke(new BasicStroke(2));
            g.drawArc(cx-32,cy-52,14,16,0,270); g.drawArc(cx+18,cy-52,14,16,270,270);
            // Orb + cross
            g.setColor(new Color(200,180,50)); g.fillOval(cx-10,cy-8,20,20);
            g.setColor(acc); g.setStroke(new BasicStroke(2));
            g.drawLine(cx,cy-10,cx,cy+12); g.drawLine(cx-10,cy+2,cx+10,cy+2);
            break;
        }
        case "hierophant": {
            // Triple cross
            g.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx,cy-42,cx,cy+18);
            g.setStroke(new BasicStroke(3));
            g.drawLine(cx-16,cy-34,cx+16,cy-34);
            g.drawLine(cx-13,cy-22,cx+13,cy-22);
            g.drawLine(cx-9,cy-10,cx+9,cy-10);
            // Two kneeling figures
            g.setColor(new Color(acc.getRed(),acc.getGreen(),acc.getBlue(),170));
            g.fillOval(cx-24,cy+12,11,11); g.fillOval(cx+13,cy+12,11,11);
            g.fillRect(cx-23,cy+23,9,14); g.fillRect(cx+14,cy+23,9,14);
            break;
        }
        case "lovers": {
            // Angel
            g.setColor(new Color(255,230,150)); g.fillOval(cx-8,cy-44,16,16);
            g.setColor(Color.WHITE);
            g.fillArc(cx-28,cy-40,22,30,-20,200); g.fillArc(cx+6,cy-40,22,30,-160,200);
            // Light beam
            g.setColor(new Color(255,220,50,80));
            g.fillPolygon(new int[]{cx-4,cx+4,cx+16,cx-16},new int[]{cy-28,cy-28,cy+18,cy+18},4);
            // Two figures
            g.setColor(acc);
            g.fillOval(cx-22,cy-4,12,12); g.fillOval(cx+10,cy-4,12,12);
            g.fillRect(cx-20,cy+8,8,18);  g.fillRect(cx+12,cy+8,8,18);
            // Heart between them
            g.setColor(new Color(220,50,80)); heart(g,cx,cy+5,7);
            break;
        }
        case "chariot": {
            // Chariot
            g.setColor(acc); g.fillRect(cx-24,cy-18,48,30);
            g.setColor(blend(pri,Color.BLACK,0.35f)); g.fillRect(cx-20,cy-14,40,22);
            // Wheels
            g.setColor(acc); g.setStroke(new BasicStroke(2));
            g.drawOval(cx-24,cy+12,16,16); g.drawOval(cx+8,cy+12,16,16);
            // Wheel spokes
            for(int i=0;i<4;i++){double a=i*Math.PI/4;
                g.drawLine((int)(cx-16+8*Math.cos(a)),(int)(cy+20+8*Math.sin(a)),
                           (int)(cx-16+7*Math.cos(a+Math.PI/8)),(int)(cy+20+7*Math.sin(a+Math.PI/8)));}
            // Sphinxes
            g.setColor(new Color(220,200,140));
            g.fillOval(cx-40,cy-28,16,14); g.fillRect(cx-40,cy-14,16,18);
            g.fillOval(cx+24,cy-28,16,14); g.fillRect(cx+24,cy-14,16,18);
            // Star crown
            star(g,cx,cy-26,10,5,new Color(255,220,50));
            for(int i=0;i<6;i++){double a=i*Math.PI/3;
                star(g,(int)(cx+20*Math.cos(a)),(int)(cy-26+8*Math.sin(a)),4,5,acc);}
            break;
        }
        case "strength": {
            // Lemniscate
            g.setStroke(new BasicStroke(2.5f)); g.drawOval(cx-20,cy-44,18,12); g.drawOval(cx+2,cy-44,18,12);
            // Lion
            g.setColor(new Color(200,160,50)); g.fillOval(cx+2,cy-16,28,26);
            g.setColor(new Color(160,110,20));
            for(int i=0;i<8;i++){double a=i*Math.PI/4; g.fillOval((int)(cx+16+18*Math.cos(a))-5,(int)(cy-3+12*Math.sin(a))-5,10,10);}
            // Woman
            g.setColor(new Color(255,210,170)); g.fillOval(cx-28,cy-26,14,14);
            g.setColor(acc); g.fillRect(cx-26,cy-12,10,24);
            // Hands on lion
            g.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx-18,cy-2,cx+2,cy-2);
            break;
        }
        case "hermit": {
            // Mountain
            g.setColor(new Color(120,120,135)); g.fillPolygon(new int[]{cx-32,cx+14,cx-10},new int[]{cy+30,cy+30,cy-8},3);
            g.setColor(new Color(225,240,255)); g.fillPolygon(new int[]{cx-16,cx+4,cx-6},new int[]{cy-1,cy+12,cy-10},3);
            // Staff
            g.setColor(new Color(140,90,40)); g.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx+10,cy-36,cx+10,cy+28);
            // Lantern star
            star(g,cx+10,cy-39,9,5,new Color(255,230,80));
            g.setColor(new Color(255,200,50,110)); g.fillOval(cx+2,cy-50,18,22);
            // Hooded figure
            g.setColor(new Color(100,100,115));
            g.fillOval(cx-14,cy-30,14,14);
            g.fillPolygon(new int[]{cx-18,cx+2,cx-5},new int[]{cy+2,cy+2,cy-30},3);
            break;
        }
        case "wheel": {
            // Wheel
            g.setStroke(new BasicStroke(3)); g.drawOval(cx-28,cy-46,56,56);
            g.setStroke(new BasicStroke(2)); g.drawOval(cx-18,cy-36,36,36);
            for(int i=0;i<8;i++){double a=i*Math.PI/4;
                g.drawLine((int)(cx+12*Math.cos(a)),(int)(cy-18+12*Math.sin(a)),
                           (int)(cx+28*Math.cos(a)),(int)(cy-18+28*Math.sin(a)));}
            g.fillOval(cx-7,cy-25,14,14);
            // T A R O letters
            g.setFont(new Font("Times New Roman",Font.BOLD,9)); g.setColor(acc);
            g.drawString("T",cx-4,cy-44); g.drawString("A",cx+24,cy-16);
            g.drawString("R",cx-5,cy+12); g.drawString("O",cx-30,cy-16);
            // Serpent + anubis at sides
            g.setColor(new Color(acc.getRed(),acc.getGreen(),acc.getBlue(),160));
            g.setFont(new Font("SansSerif",Font.BOLD,10));
            g.drawString("☿",cx-40,cy-22); g.drawString("♃",cx+30,cy-22);
            break;
        }
        case "justice": {
            // Scales beam
            g.setColor(acc); g.setStroke(new BasicStroke(2.5f));
            g.drawLine(cx-24,cy-16,cx+24,cy-16);
            g.drawLine(cx,cy-42,cx,cy-16);
            g.drawOval(cx-28,cy-14,16,10); g.drawOval(cx+12,cy-14,16,10);
            // Chains to pans
            g.setStroke(new BasicStroke(1));
            g.drawLine(cx-20,cy-16,cx-20,cy-10); g.drawLine(cx+20,cy-16,cx+20,cy-10);
            // Sword
            g.setColor(new Color(180,185,210)); g.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx+20,cy-42,cx+20,cy+22);
            g.setColor(GOLD); g.setStroke(new BasicStroke(2));
            g.drawLine(cx+10,cy-24,cx+30,cy-24);
            g.fillOval(cx+16,cy-44,8,8);
            break;
        }
        case "hanged": {
            // T-shaped tree
            g.setColor(new Color(70,100,40)); g.setStroke(new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx-22,cy-38,cx-22,cy+20); g.drawLine(cx+22,cy-38,cx+22,cy+20);
            g.drawLine(cx-22,cy-38,cx+22,cy-38);
            // Rope
            g.setColor(new Color(180,160,120)); g.setStroke(new BasicStroke(1.5f));
            g.drawLine(cx,cy-38,cx,cy-24);
            // Hanging figure (upside down)
            g.setColor(new Color(255,210,170)); g.fillOval(cx-7,cy-22,14,14);
            g.setColor(acc); g.fillRect(cx-5,cy-8,10,22);
            // Leg crossed (T shape)
            g.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx-10,cy+8,cx+10,cy+8); g.drawLine(cx-10,cy+8,cx-10,cy+20);
            // Halo
            g.setColor(new Color(255,215,50,160)); g.setStroke(new BasicStroke(2));
            g.drawOval(cx-11,cy-28,22,22);
            break;
        }
        case "death": {
            // White horse
            g.setColor(new Color(220,220,220));
            g.fillOval(cx-12,cy+6,40,18); g.fillOval(cx+16,cy-8,18,18);
            g.fillRect(cx-18,cy+10,8,22); g.fillRect(cx-2,cy+10,8,22);
            g.fillRect(cx+18,cy+10,8,22); g.fillRect(cx+26,cy+10,8,22);
            // Skeleton rider
            g.setColor(Color.WHITE); g.fillOval(cx-4,cy-38,16,16);
            g.setColor(new Color(50,50,50)); g.fillOval(cx-1,cy-34,5,5); g.fillOval(cx+5,cy-34,5,5);
            g.fillRect(cx,cy-26,8,3);
            // Scythe
            g.setColor(new Color(180,180,200)); g.setStroke(new BasicStroke(2.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx+14,cy-38,cx+4,cy+6);
            g.setStroke(new BasicStroke(2)); g.drawArc(cx+4,cy-48,24,16,180,180);
            // Black flag
            g.setColor(new Color(20,20,20)); g.fillRect(cx-32,cy-42,3,32); g.fillRect(cx-29,cy-42,20,14);
            g.setColor(Color.WHITE); g.fillOval(cx-22,cy-38,8,8);
            break;
        }
        case "temperance": {
            // Wings
            g.setColor(new Color(220,240,255));
            g.fillArc(cx-30,cy-30,22,36,-30,210); g.fillArc(cx+8,cy-30,22,36,-150,210);
            // Halo
            g.setColor(new Color(255,215,50,180)); g.setStroke(new BasicStroke(2.5f));
            g.drawOval(cx-10,cy-44,20,20);
            // Face
            g.setColor(new Color(255,215,175)); g.fillOval(cx-8,cy-42,16,16);
            // Cups
            g.setColor(GOLD); g.fillRect(cx-24,cy-8,12,18); g.fillRect(cx+12,cy-8,12,18);
            // Water arc
            g.setColor(new Color(80,160,240,200)); g.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawArc(cx-18,cy-14,36,22,0,180);
            // Mountains + rising sun
            g.setColor(new Color(80,100,120));
            g.fillPolygon(new int[]{cx-32,cx-16,cx-24},new int[]{cy+30,cy+30,cy+6},3);
            g.fillPolygon(new int[]{cx+8,cx+32,cx+18},new int[]{cy+30,cy+30,cy+8},3);
            g.setColor(new Color(255,195,50,180)); g.fillOval(cx-8,cy+4,16,16);
            break;
        }
        case "devil": {
            // Inverted pentagram
            star(g,cx,cy-32,18,5,new Color(200,0,0,180));
            // Horns
            g.setColor(new Color(80,40,20));
            g.fillPolygon(new int[]{cx-10,cx-22,cx-2},new int[]{cy-18,cy-42,cy-14},3);
            g.fillPolygon(new int[]{cx+10,cx+22,cx+2},new int[]{cy-18,cy-42,cy-14},3);
            // Goat head
            g.fillOval(cx-14,cy-20,28,24);
            // Glowing eyes
            g.setColor(new Color(255,60,0)); g.fillOval(cx-9,cy-14,6,6); g.fillOval(cx+3,cy-14,6,6);
            // Chained figures
            g.setColor(acc); g.fillOval(cx-24,cy+10,11,11); g.fillOval(cx+13,cy+10,11,11);
            g.fillRect(cx-22,cy+21,9,14); g.fillRect(cx+15,cy+21,9,14);
            g.setColor(new Color(150,150,150)); g.setStroke(new BasicStroke(1.5f));
            g.drawLine(cx-18,cy+10,cx-4,cy+4); g.drawLine(cx+18,cy+10,cx+4,cy+4);
            g.fillOval(cx-6,cy-1,12,8);
            break;
        }
        case "tower": {
            // Tower body with stone texture
            g.setColor(new Color(95,95,105)); g.fillRect(cx-16,cy-36,32,58);
            g.setColor(new Color(75,75,85)); g.setStroke(new BasicStroke(0.5f));
            for(int y=cy-34;y<cy+20;y+=8)for(int x=cx-16;x<cx+16;x+=14){
                int off=((y-cy)/8%2)*7; g.drawRect(x+off,y,12,7);}
            // Battlements
            g.setColor(new Color(95,95,105));
            for(int i=0;i<4;i++) g.fillRect(cx-16+i*9,cy-44,7,10);
            // Crown fire
            g.setColor(new Color(255,80,20,200)); g.fillOval(cx-12,cy-50,24,16);
            g.setColor(new Color(255,150,20,160)); g.fillOval(cx-8,cy-54,16,12);
            // Lightning bolt
            g.setColor(new Color(255,220,0));
            g.fillPolygon(new int[]{cx+20,cx+10,cx+18,cx+4,cx+12},
                          new int[]{cy-48,cy-34,cy-28,cy-14,cy-8},5);
            // Falling figures
            g.setColor(acc); g.fillOval(cx-28,cy+12,10,10); g.fillRect(cx-26,cy+22,8,16);
            AffineTransform ft=g.getTransform(); g.rotate(0.4,cx+22,cy+6);
            g.fillOval(cx+18,cy-4,10,10); g.fillRect(cx+20,cy+6,8,16);
            g.setTransform(ft);
            break;
        }
        case "star": {
            // Large 8-pointed star
            star(g,cx,cy-16,22,8,new Color(255,235,80));
            // 7 small stars
            for(int i=0;i<7;i++){double a=i*2*Math.PI/7-Math.PI/2;
                star(g,(int)(cx+36*Math.cos(a)),(int)(cy-16+28*Math.sin(a)),7,5,acc);}
            // Pool
            g.setColor(new Color(50,130,220,180)); g.fillOval(cx-18,cy+12,36,16);
            // Kneeling figure
            g.setColor(new Color(255,210,170)); g.fillOval(cx+12,cy-4,12,12);
            g.setColor(acc); g.fillRect(cx+14,cy+8,8,18);
            // Pouring
            g.setColor(new Color(100,180,255,180)); g.setStroke(new BasicStroke(2));
            g.drawLine(cx+18,cy+8,cx+12,cy+18);
            break;
        }
        case "moon": {
            // Crescent
            g.setColor(new Color(210,215,250)); g.fillOval(cx-22,cy-44,44,44);
            g.setColor(blend(pri,Color.BLACK,0.3f)); g.fillOval(cx-8,cy-46,44,44);
            // Stars
            g.setColor(new Color(220,220,100));
            star(g,cx-16,cy-40,5,5,new Color(220,220,100));
            star(g,cx+4,cy-44,4,5,new Color(220,220,100));
            // Tower silhouettes
            g.setColor(new Color(50,50,80));
            g.fillRect(cx-32,cy+2,8,22); g.fillRect(cx+24,cy+2,8,22);
            // Pool + crayfish
            g.setColor(new Color(30,60,140,180)); g.fillOval(cx-16,cy+8,32,14);
            g.setColor(new Color(180,50,30)); g.fillOval(cx-6,cy+6,12,10);
            // Dogs howling
            g.setColor(new Color(180,140,80));
            g.fillOval(cx-28,cy-4,12,10); g.fillOval(cx+16,cy-4,12,10);
            break;
        }
        case "sun": {
            // Sun rays (alternating long/short)
            g.setColor(new Color(255,210,30));
            for(int i=0;i<16;i++){double a=i*Math.PI/8;
                g.setStroke(new BasicStroke(i%2==0?3:2));
                int r1=i%2==0?22:19, r2=i%2==0?40:33;
                g.drawLine((int)(cx+r1*Math.cos(a)),(int)(cy-22+r1*Math.sin(a)),
                           (int)(cx+r2*Math.cos(a)),(int)(cy-22+r2*Math.sin(a)));}
            // Sun disc
            g.setColor(new Color(255,230,50)); g.fillOval(cx-18,cy-40,36,36);
            g.setColor(new Color(240,175,0)); g.setStroke(new BasicStroke(2));
            g.drawOval(cx-18,cy-40,36,36);
            // Child on white horse
            g.setColor(new Color(200,200,200)); g.fillOval(cx-10,cy+8,30,16); g.fillOval(cx+12,cy-2,16,14);
            g.fillRect(cx-18,cy+12,8,20); g.fillRect(cx+2,cy+12,8,20);
            g.setColor(new Color(255,210,170)); g.fillOval(cx-12,cy-4,14,14);
            // Red banner
            g.setColor(new Color(220,50,50)); g.setStroke(new BasicStroke(1.5f));
            g.drawLine(cx-8,cy-4,cx-8,cy-24); g.fillRect(cx-14,cy-24,20,12);
            // Sunflowers
            g.setColor(new Color(255,200,0)); g.fillOval(cx+24,cy+4,12,12);
            g.setColor(new Color(100,60,0)); g.fillOval(cx+27,cy+7,6,6);
            break;
        }
        case "judgement": {
            // Angel
            g.setColor(new Color(255,230,150)); g.fillOval(cx-8,cy-44,16,16);
            g.setColor(new Color(255,200,0,180)); g.setStroke(new BasicStroke(2));
            g.drawOval(cx-11,cy-47,22,22);
            g.setColor(Color.WHITE);
            g.fillArc(cx-28,cy-40,22,30,-20,200); g.fillArc(cx+6,cy-40,22,30,-160,200);
            // Trumpet
            g.setColor(GOLD); g.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(cx,cy-28,cx+26,cy-14);
            g.fillOval(cx+22,cy-18,12,10);
            // Cross flag
            g.setColor(new Color(200,30,30)); g.fillRect(cx+8,cy-14,12,10);
            g.setColor(Color.WHITE); g.fillRect(cx+12,cy-13,4,8); g.fillRect(cx+9,cy-10,10,3);
            // Three rising figures
            g.setColor(acc);
            for(int i=-1;i<=1;i++){
                g.fillOval(cx+i*20-6,cy+8,12,12); g.fillRect(cx+i*20-4,cy+20,8,16);}
            break;
        }
        case "world": {
            // Oval wreath
            g.setColor(new Color(30,160,30)); g.setStroke(new BasicStroke(6));
            g.drawOval(cx-28,cy-36,56,72);
            // Leaf dots
            g.setColor(new Color(50,200,50));
            for(int i=0;i<16;i++){double a=i*Math.PI/8;
                int lx=(int)(cx+28*Math.cos(a)), ly=(int)(cy+36*Math.sin(a));
                g.fillOval(lx-4,ly-4,8,8);}
            // Dancer
            g.setColor(new Color(255,210,170)); g.fillOval(cx-7,cy-30,14,14);
            g.setColor(new Color(100,200,255)); g.fillRect(cx-5,cy-16,10,30);
            // Wands
            g.setColor(new Color(180,140,50)); g.setStroke(new BasicStroke(2));
            g.drawLine(cx-12,cy-18,cx-20,cy+8); g.drawLine(cx+12,cy-18,cx+20,cy+8);
            // Corner creature symbols
            g.setFont(new Font("SansSerif",Font.BOLD,9));
            g.setColor(new Color(acc.getRed(),acc.getGreen(),acc.getBlue(),200));
            g.drawString("♈",cx-42,cy-28); g.drawString("♉",cx+28,cy-28);
            g.drawString("♌",cx-42,cy+38); g.drawString("♏",cx+28,cy+38);
            break;
        }
        }
    }
}
