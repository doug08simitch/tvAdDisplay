import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;
import javax.imageio.ImageIO;

/**
 * ParadiseTV — Broadcast Overlay System
 * Transparent ticker bar at bottom of screen.
 * Side ads slide in every 5 minutes.
 * Reads content from ParadiseTV.txt on Desktop.
 */
public class ParadiseTV {

    public static void main(String[] args) {

        // Auto-detect flash drive by scanning all drive letters
        String configPath = null;
        String[] driveLetters = {"D","E","F","G","H","I","J"};

        for (String drive : driveLetters) {
            File testFile = new File(drive + ":\\ParadiseTV.txt");
            if (testFile.exists()) {
                configPath = drive + ":\\ParadiseTV.txt";
                System.out.println("Flash drive found at: " + drive + ":\\");
                break;
            }
        }

// Fall back to Desktop if no flash drive found
        if (configPath == null) {
            System.out.println("No flash drive found.");
            JOptionPane.showMessageDialog(
                    null,
                    "Please insert a flash drive containing ParadiseTV.txt",
                    "ParadiseTV",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String desktopPath = configPath;

        List<String>  tickerMessages = new ArrayList<>();
        List<AdEntry> adList         = new ArrayList<>();

        File configFile = new File(desktopPath);
        System.out.println("=================================");
        System.out.println("ParadiseTV Starting...");
        System.out.println("Config: " + configFile.getAbsolutePath());
        System.out.println("Found:  " + configFile.exists());
        System.out.println("=================================");

        // Create default file if missing


        // Read config file
        if (configFile.exists()) {
            try (BufferedReader br =
                         new BufferedReader(new FileReader(configFile))) {
                String line;
                String section = "";
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    if (line.startsWith("[") && line.endsWith("]")) {
                        section = line.substring(1, line.length() - 1)
                                .toLowerCase();
                        continue;
                    }
                    if (section.equals("ticker")) {
                        tickerMessages.add(line);
                        System.out.println("TICKER >> " + line);
                    }
                    if (section.equals("ads")) {
                        String[] p = line.split("\\|");
                        if (p.length >= 3) {
                            String imgPath = "";

                            if (p.length > 3) {
                                imgPath = new File(
                                        configFile.getParentFile(),
                                        p[3].trim()
                                ).getAbsolutePath();
                            }

                            adList.add(new AdEntry(
                                    p[0].trim(),
                                    p[1].trim(),
                                    p[2].trim(),
                                    imgPath));
                            System.out.println("AD >> " + p[0].trim());
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Read error: " + e.getMessage());
            }
        }
        if (tickerMessages.isEmpty() && adList.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "No content found in ParadiseTV.txt"
            );
            return;
        }

       // Fallbacks



        System.out.println("Loaded " + tickerMessages.size()
                + " messages and " + adList.size() + " ads");

        final List<String>  ft = tickerMessages;
        final List<AdEntry> fa = adList;

// GET SCREEN SIZE HERE
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();

        Rectangle screen = ge.getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getBounds();

        SwingUtilities.invokeLater(() -> {
            ParadiseTickerPanel tickerPanel =
                    new ParadiseTickerPanel(ft, screen.width);

            new ParadiseTickerBar(tickerPanel).show();

            ParadiseAdManager adManager =
                    new ParadiseAdManager(fa);

            adManager.start();

            new ContentWatcher(tickerPanel, adManager).start();
        });
    }
}

// ═══════════════════════════════════════════════════════
//  TICKER BAR WINDOW
// ═══════════════════════════════════════════════════════
class ParadiseTickerBar {
    private final JWindow window;

    public ParadiseTickerBar(ParadiseTickerPanel panel) {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle screen = ge.getDefaultScreenDevice()
                .getDefaultConfiguration().getBounds();

        window = new JWindow();
        window.setAlwaysOnTop(true);
        window.setBackground(new Color(0, 0, 0, 0));
        window.setBounds(
                screen.x,
                screen.y + screen.height - 52,
                screen.width,
                52
        );
        window.add(panel);
    }

    public void show() { window.setVisible(true); }
}




// ═══════════════════════════════════════════════════════
//  TICKER PANEL
// ═══════════════════════════════════════════════════════
class ParadiseTickerPanel extends JPanel {

    private float          scrollX;
    private volatile int      textWidth;
    private final int      loopWidth;
    private final int      loopGap;
    private volatile String   fullText;
    private final Font     tickerFont;
    private final Font     clockFont;
    private final Font     dateFont;
    private volatile String clockText = "";
    private volatile String dateText  = "";
    private float          pulse      = 0f;
    private boolean        pulseUp    = true;

    private static final int BADGE_W = 120;
    private static final int CLOCK_W = 230;

    public void updateMessages(List<String> messages) {
        StringBuilder sb = new StringBuilder();
        for (String m : messages) {
            sb.append(m.toUpperCase());
            sb.append("            \u25C6            ");
        }
        // Update fullText — need to make fullText non-final
        // Change: private final String fullText;
        // To:     private volatile String fullText;
        fullText = sb.toString();

        // Remeasure text width
        BufferedImage tmp =
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tmp.createGraphics();
        g.setFont(tickerFont);
        textWidth = g.getFontMetrics().stringWidth(fullText);
        g.dispose();

        System.out.println("Ticker updated — new width: " + textWidth);
    }

    public ParadiseTickerPanel(List<String> messages, int screenWidth) {
        setOpaque(false);

        tickerFont = new Font("SansSerif", Font.BOLD, 24);
        clockFont  = new Font("Monospaced", Font.BOLD, 26);
        dateFont   = new Font("SansSerif", Font.PLAIN, 14);

        // ── BUILD FULL TEXT
        StringBuilder sb = new StringBuilder("    ");

        for (String m : messages) {
            sb.append(m.toUpperCase());
            sb.append("    ◆    ");
        }
        sb.append("      ");
        fullText = sb.toString();

// Create graphics context for accurate measurements
        BufferedImage tmp =
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        Graphics2D gm = tmp.createGraphics();

        gm.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        gm.setFont(tickerFont);

        FontMetrics fm = gm.getFontMetrics();

// Actual rendered text width
        textWidth = fm.stringWidth(fullText);

// EXTRA SAFE PIXEL GAP BETWEEN LOOPS
// This is the key fix.
        loopGap = 120;

// Total loop width
        loopWidth = textWidth + loopGap;

        gm.dispose();

// Start fully offscreen
        scrollX = screenWidth;

        // ── SCROLL ANIMATION
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            public void run() {
                scrollX -= 1.5f;

                // When first copy has fully exited left edge,
                // shift scrollX forward by exactly textWidth.
                // This makes second copy become the new first copy
                // at the exact same position — perfectly seamless.
                if (scrollX <= -(textWidth + 250)) {
                    scrollX += (textWidth + 250);
                }

                // Pulse glow
                if (pulseUp) {
                    pulse += 0.03f;
                    if (pulse >= 1f) pulseUp = false;
                } else {
                    pulse -= 0.03f;
                    if (pulse <= 0f) pulseUp = true;
                }

                SwingUtilities.invokeLater(() -> repaint());
            }
        }, 0, 16);

        // ── CLOCK TIMER
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Date now = new Date();
                clockText = new SimpleDateFormat("HH:mm:ss").format(now);
                dateText  = new SimpleDateFormat("EEE dd MMM yyyy")
                        .format(now).toUpperCase();
            }
        }, 0, 1000);

        // Initialise clock immediately so it shows on first paint
        Date now = new Date();
        clockText = new SimpleDateFormat("HH:mm:ss").format(now);
        dateText  = new SimpleDateFormat("EEE dd MMM yyyy")
                .format(now).toUpperCase();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();

        // Background
        g2.setPaint(new GradientPaint(
                0, 0, new Color(200, 18, 18),
                0, H, new Color(130,  8,  8)));
        g2.fillRect(0, 0, W, H);

        // Gold top border
        g2.setColor(new Color(255, 215, 0));
        g2.fillRect(0, 0, W, 3);

        // Left badge
        drawLiveBadge(g2, H);

        // Right clock
        drawClockSection(g2, W, H);

        // Dividers
        g2.setColor(new Color(255, 255, 255, 50));
        g2.fillRect(BADGE_W,     3, 1, H - 3);
        g2.fillRect(W - CLOCK_W, 3, 1, H - 3);

        // ── SCROLLING TEXT
        // Clip strictly between badge and clock panel
        int clipX = BADGE_W + 6;
        int clipW = W - CLOCK_W - BADGE_W - 12;
        Shape oldClip = g2.getClip();
        g2.clipRect(clipX, 0, clipW, H);

        g2.setFont(tickerFont);
        g2.setColor(Color.WHITE);
        int textY = H / 2 + 7;
        int drawX = Math.round(scrollX);
// FIRST COPY
        g2.drawString(fullText, drawX, textY);

// SECOND COPY
// Add REAL EMPTY PIXEL SPACE between loops
        int secondX = drawX + textWidth + 250;

        g2.drawString(fullText, secondX, textY);
        g2.setClip(oldClip);
    }

    private void drawLiveBadge(Graphics2D g2, int H) {
        g2.setPaint(new GradientPaint(
                0, 0, new Color(255, 220, 0),
                0, H, new Color(220, 170, 0)));
        g2.fillRect(0, 2, BADGE_W, H - 2);

        // Pulsing dot — alpha clamped to valid 0-255 range
        int dotAlpha = Math.max(0, Math.min(255,
                (int)(120 + pulse * 125)));
        int dotR = 5, dotX = 14, dotY = H / 2;
        g2.setColor(new Color(220, 20, 20, dotAlpha));
        g2.fillOval(dotX - dotR - 2, dotY - dotR - 2,
                (dotR + 2) * 2, (dotR + 2) * 2);
        g2.setColor(new Color(220, 20, 20));
        g2.fillOval(dotX - dotR, dotY - dotR,
                dotR * 2, dotR * 2);

        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.setColor(new Color(15, 15, 15));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("LIVE", dotX + dotR + 6,
                H / 2 + fm.getAscent() / 2 - 2);
    }

    private void drawClockSection(Graphics2D g2, int W, int H) {
        int startX = W - CLOCK_W;
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRect(startX + 1, 2, CLOCK_W - 1, H - 2);

        // Date
        g2.setFont(dateFont);
        g2.setColor(new Color(255, 215, 0, 200));
        FontMetrics dfm = g2.getFontMetrics();
        g2.drawString(dateText,
                startX + (CLOCK_W - dfm.stringWidth(dateText)) / 2,
                H / 2 - 2);

        // Clock
        g2.setFont(clockFont);
        g2.setColor(Color.WHITE);
        FontMetrics cfm = g2.getFontMetrics();
        g2.drawString(clockText,
                startX + (CLOCK_W - cfm.stringWidth(clockText)) / 2,
                H / 2 + cfm.getAscent() - 2);
    }
}

// ═══════════════════════════════════════════════════════
//  AD MANAGER
// ═══════════════════════════════════════════════════════
class ParadiseAdManager {

    private List<AdEntry> ads;
    private int adIndex = 0;
    private static final long FIRST_DELAY   = 15;
    private static final long INTERVAL_SECS = 300;
    private static final long DISPLAY_MS    = 25_000;

    public void updateAds(List<AdEntry> newAds) {
        this.ads.clear();
        this.ads.addAll(newAds);
        this.adIndex = 0;
        System.out.println("Ads updated: " + newAds.size());
    }

    public ParadiseAdManager(List<AdEntry> ads) { this.ads = ads; }

    public void start() {
        ScheduledExecutorService exec =
                Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "ParadiseAdThread");
                    t.setDaemon(true);
                    return t;
                });
        exec.scheduleAtFixedRate(this::showNext,
                FIRST_DELAY, INTERVAL_SECS, TimeUnit.SECONDS);
    }

    private void showNext() {
        if (ads.isEmpty()) return;
        AdEntry ad = ads.get(adIndex % ads.size());
        adIndex++;
        System.out.println("Showing ad: " + ad.getBrand());
        SwingUtilities.invokeLater(() ->
                new ParadiseAdWindow(ad, DISPLAY_MS).show());
    }
}
class ContentWatcher {

    private final ParadiseTickerPanel tickerPanel;
    private final ParadiseAdManager   adManager;
    private long   lastModified = 0;
    private String lastPath     = "";

    public ContentWatcher(ParadiseTickerPanel tickerPanel,
                          ParadiseAdManager adManager) {
        this.tickerPanel = tickerPanel;
        this.adManager   = adManager;
    }

    public void start() {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            public void run() {
                checkForChanges();
            }
        }, 5000, 5000); // check every 5 seconds
    }

    private void checkForChanges() {

        // Scan for flash drive first
        String foundPath = null;
        String[] drives = {"D","E","F","G","H","I","J"};
        for (String d : drives) {
            File f = new File(d + ":\\ParadiseTV.txt");
            if (f.exists()) {
                foundPath = d + ":\\ParadiseTV.txt";
                break;
            }
        }

        // Fall back to Desktop
        if (foundPath == null) {
            System.out.println("Flash drive removed.");
            return;
        }

        File configFile = new File(foundPath);

        // Check if path changed (flash drive plugged/unplugged)
        // or file was modified
        boolean pathChanged     = !foundPath.equals(lastPath);
        boolean fileChanged     = configFile.exists()
                && configFile.lastModified() != lastModified;
        boolean driveRemoved    = !configFile.exists()
                && !lastPath.isEmpty();

        if (pathChanged || fileChanged || driveRemoved) {
            System.out.println("Change detected — reloading content...");
            lastPath     = foundPath;
            lastModified = configFile.exists()
                    ? configFile.lastModified() : 0;
            reloadContent(configFile);
        }
    }

    private void reloadContent(File configFile) {
        List<String>  newTicker = new ArrayList<>();
        List<AdEntry> newAds    = new ArrayList<>();

        if (configFile.exists()) {
            try (BufferedReader br =
                         new BufferedReader(new FileReader(configFile))) {
                String line;
                String section = "";
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    if (line.startsWith("[") && line.endsWith("]")) {
                        section = line.substring(1,
                                line.length() - 1).toLowerCase();
                        continue;
                    }
                    if (section.equals("ticker"))
                        newTicker.add(line);
                    if (section.equals("ads")) {
                        String[] p = line.split("\\|");
                        if (p.length >= 3) {
                            String imgPath = "";

                            if (p.length > 3) {
                                imgPath = new File(
                                        configFile.getParentFile(),
                                        p[3].trim()
                                ).getAbsolutePath();
                            }

                            newAds.add(new AdEntry(
                                    p[0].trim(),
                                    p[1].trim(),
                                    p[2].trim(),
                                    imgPath));
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Reload error: " + e.getMessage());
            }
        }

        // Fallbacks if file empty or missing



        final List<String>  ft = newTicker;
        final List<AdEntry> fa = newAds;

        SwingUtilities.invokeLater(() -> {
            tickerPanel.updateMessages(ft);
            adManager.updateAds(fa);
            System.out.println("Content reloaded — "
                    + ft.size() + " messages, "
                    + fa.size() + " ads");
        });
    }
}
// ═══════════════════════════════════════════════════════
//  AD WINDOW
// ═══════════════════════════════════════════════════════
class ParadiseAdWindow {

    private static final int AD_W = 420;
    private static final int AD_H = 340;

    private final JWindow        window;
    private final ParadiseAdPanel panel;
    private final long           displayMs;
    private int                  currentX;
    private final int            targetX;
    private final int            exitX;
    private final int            adY;
    private float                alpha = 0f;
    private Timer                slideTimer;

    public ParadiseAdWindow(AdEntry ad, long displayMs) {
        this.displayMs = displayMs;
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle screen = ge.getDefaultScreenDevice()
                .getDefaultConfiguration().getBounds();

        currentX = screen.x + screen.width + 10;
        targetX  = screen.x + screen.width - AD_W - 20;
        exitX    = screen.x + screen.width + AD_W + 20;
        adY      = screen.y + 70;

        window = new JWindow();
        window.setAlwaysOnTop(true);
        window.setBackground(new Color(0, 0, 0, 0));
        window.setBounds(currentX, adY, AD_W, AD_H);
        panel = new ParadiseAdPanel(ad);
        window.add(panel);
    }

    public void show() { window.setVisible(true); slideIn(); }

    private void slideIn() {
        slideTimer = new Timer(true);
        slideTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                currentX -= 5;
                alpha = Math.min(1f, alpha + 0.02f);
                if (currentX <= targetX) {
                    currentX = targetX;
                    alpha    = 1f;
                    slideTimer.cancel();
                    new Timer(true).schedule(new TimerTask() {
                        public void run() { slideOut(); }
                    }, displayMs);
                }
                update();
            }
        }, 0, 16);
    }

    private void slideOut() {
        slideTimer = new Timer(true);
        slideTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                currentX += 5;
                alpha = Math.max(0f, alpha - 0.02f);
                if (currentX >= exitX) {
                    slideTimer.cancel();
                    SwingUtilities.invokeLater(() -> {
                        window.setVisible(false);
                        window.dispose();
                    });
                    return;
                }
                update();
            }
        }, 0, 16);
    }

    private void update() {
        final int x = currentX; final float a = alpha;
        SwingUtilities.invokeLater(() -> {
            window.setLocation(x, adY);
            panel.setAlpha(a);
            panel.repaint();
        });
    }
}

// ═══════════════════════════════════════════════════════
//  AD PANEL
// ═══════════════════════════════════════════════════════
class ParadiseAdPanel extends JPanel {

    private final AdEntry ad;
    private float   alpha       = 0f;
    private float   borderPulse = 0f;
    private boolean bpUp        = true;

    public ParadiseAdPanel(AdEntry ad) {
        this.ad = ad;
        setOpaque(false);
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (bpUp) {
                    borderPulse += 0.04f;
                    if (borderPulse >= 1f) bpUp = false;
                } else {
                    borderPulse -= 0.04f;
                    if (borderPulse <= 0f) bpUp = true;
                }
                SwingUtilities.invokeLater(() -> repaint());
            }
        }, 0, 30);
    }

    public void setAlpha(float a) { this.alpha = a; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (alpha <= 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha));

        int W = getWidth(), H = getHeight();

        // Shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(8, 8, W - 6, H - 6, 20, 20);

        // Background
        g2.setPaint(new GradientPaint(
                0, 0, new Color(15, 20, 55),
                0, H, new Color( 5, 10, 30)));
        g2.fillRoundRect(0, 0, W - 8, H - 8, 18, 18);

        // Animated border — alpha clamped 0-255
        int ba = Math.max(0, Math.min(255,
                (int)(150 + borderPulse * 105)));
        g2.setColor(new Color(255, 200, 0, ba));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(1, 1, W - 10, H - 10, 18, 18);

        // Red left bar
        g2.setColor(new Color(210, 20, 20));
        g2.fillRoundRect(0, 0, 7, H - 8, 6, 6);

        // Watermark
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.setColor(new Color(255, 215, 0, 180));
        g2.drawString("PARADISE TV", 14, 16);

        // AD badge
        g2.setColor(new Color(210, 20, 20));
        g2.fillRoundRect(W - 50, 6, 40, 18, 6, 6);
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("AD",
                W - 50 + (40 - fm.stringWidth("AD")) / 2, 19);

        // Image area
        int iL = 12, iT = 24, iW = W - 22, iH = 180;
        BufferedImage img = ad.getImage();
        if (img != null) {
            Shape oc = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(
                    iL, iT, iW, iH, 10, 10));
            g2.drawImage(img, iL, iT, iW, iH, null);
            g2.setClip(oc);
        } else {
            g2.setPaint(new GradientPaint(
                    iL, iT,      new Color(0, 100, 210),
                    iL + iW, iT + iH, new Color(0, 50, 140)));
            g2.fillRoundRect(iL, iT, iW, iH, 10, 10);
            g2.setPaint(new GradientPaint(
                    iL, iT, new Color(255, 255, 255, 30),
                    iL, iT + iH / 2, new Color(255, 255, 255, 0)));
            g2.fillRoundRect(iL, iT, iW, iH / 2, 10, 10);
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillOval(W / 2 - 34, iT + 16, 68, 68);
            g2.setColor(new Color(255, 255, 255, 210));
            g2.setFont(new Font("SansSerif", Font.BOLD, 28));
            fm = g2.getFontMetrics();
            String ini = ad.getBrand()
                    .substring(0, Math.min(2, ad.getBrand().length()))
                    .toUpperCase();
            g2.drawString(ini,
                    W / 2 - fm.stringWidth(ini) / 2, iT + 62);
        }

        // Countdown bar
        int barY = iT + iH + 6;
        g2.setColor(new Color(255, 255, 255, 20));
        g2.fillRoundRect(iL, barY, iW, 4, 2, 2);
        g2.setColor(new Color(255, 215, 0, 180));
        g2.fillRoundRect(iL, barY,
                Math.max(0, (int)(iW * (1f - alpha * 0.1f))), 4, 2, 2);

        // Text
        int tt = barY + 18;
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.setColor(new Color(255, 210, 0));
        g2.drawString(ad.getBrand(), 16, tt);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.setColor(Color.WHITE);
        drawWrapped(g2, ad.getHeadline(), tt + 20, W - 26, 16);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(160, 205, 255));
        drawWrapped(g2, ad.getTagline(), tt + 40, W - 26, 14);

        g2.dispose();
    }

    private void drawWrapped(Graphics2D g2, String text,
                             int y, int maxW, int lineH) {
        if (text == null || text.isEmpty()) return;
        FontMetrics fm = g2.getFontMetrics();
        StringBuilder line = new StringBuilder();
        int ly = y;
        for (String word : text.split(" ")) {
            String test = line
                    + (line.length() > 0 ? " " : "") + word;
            if (fm.stringWidth(test) > maxW && line.length() > 0) {
                g2.drawString(line.toString(), 16, ly);
                line = new StringBuilder(word);
                ly  += lineH;
                if (ly > getHeight() - 6) break;
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }
        if (line.length() > 0 && ly <= getHeight() - 6)
            g2.drawString(line.toString(), 16, ly);
    }
}

// ═══════════════════════════════════════════════════════
//  AD ENTRY
// ═══════════════════════════════════════════════════════
class AdEntry {

    private final String        brand;
    private final String        headline;
    private final String        tagline;
    private       BufferedImage image;

    public AdEntry(String brand, String headline,
                   String tagline, String imagePath) {
        this.brand    = brand;
        this.headline = headline;
        this.tagline  = tagline;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File f = new File(imagePath);
                if (f.exists()) {
                    image = ImageIO.read(f);
                    System.out.println("Image loaded: " + imagePath);
                } else {
                    System.out.println("Image not found: " + imagePath);
                }
            } catch (IOException e) {
                System.out.println("Image error: " + e.getMessage());
            }
        }
    }

    public String        getBrand()    { return brand;    }
    public String        getHeadline() { return headline; }
    public String        getTagline()  { return tagline;  }
    public BufferedImage getImage()    { return image;    }
}