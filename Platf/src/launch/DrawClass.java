package launch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

public class DrawClass extends Canvas implements MouseListener {

    private final int ScreenX, ScreenY;
    private final SoftReference<World> sworld;
    public boolean stopped = true;
    public int testrate = 0;
    int oldtest = 0;
    long lasttime = 0;

    public DrawClass(int x, int y, World w) {
        ScreenX = x;
        ScreenY = y;
        sworld = new SoftReference<>(w);
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor"));
    }

    public void gameLoop() {
        if (System.currentTimeMillis() - lasttime > 1000) {
            lasttime = System.currentTimeMillis();
            testrate = oldtest;
            oldtest = 0;
        } else {
            oldtest += 1;
        }
        Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        this.render(g);
        g.dispose();
        BufferStrategy strategy = getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }
        Toolkit.getDefaultToolkit().sync();
    }
    int x, ix, y, iy;

    private BufferedImage createFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public void render(Graphics2D g) {
        World world = sworld.get();
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, world.Levels.get(world.Level).xs, world.Levels.get(world.Level).ys);
        g.setColor(Color.BLACK);
        g.drawString("" + testrate, 100, 100);
        g.setColor(Color.BLUE);
        Rectangle tr = (Rectangle) world.Characters.get(0).clone();
        if (tr.x < ScreenX / 2) {
            x = tr.x;
            ix = 0;
        } else if (tr.x > world.Levels.get(world.Level).xs - (ScreenX / 2)) {
            ix = ScreenX - world.Levels.get(world.Level).xs;
            x = (ScreenX) - (world.Levels.get(world.Level).xs - (tr.x));
        } else {
            ix = (ScreenX / 2) - tr.x;
            x = ScreenX / 2;
        }
        if (tr.y < ScreenY / 2) {
            y = tr.y;
            iy = 0;
        } else if (tr.y > world.Levels.get(world.Level).ys - (ScreenY / 2)) {
            iy = ScreenY - world.Levels.get(world.Level).ys;
            y = (ScreenY) - (world.Levels.get(world.Level).ys - (tr.y));
        } else {
            iy = (ScreenY / 2) - tr.y;
            y = ScreenY / 2;
        }
        if (((GameCharacter) tr).isActive) {
            BufferedImage tb = ((world.Characters.get(0).isFacingLeft() ? createFlipped(staticImages.Character) : staticImages.Character));
            g.drawImage(tb.getSubimage(0, 0,
                    tb.getWidth(), tb.getHeight() - (((GameCharacter) tr).isCrouching ? ((GameCharacter) tr).CrouchDecrease : 0)), x, y, null);
        }
        g.translate(ix, iy);
        g.fillRect(0, world.Levels.get(world.Level).ys - 12, world.Levels.get(world.Level).xs, 12);
        for (Rectangle r : world.Levels.get(world.Level)) {
            g.setColor(((GameObject) r).isEndLevel ? Color.RED : Color.BLUE);
            Rectangle trh = (Rectangle) r.clone();
            tr.grow(2, 2);
            g.fillRect(trh.x, trh.y, trh.width, trh.height);
        }
        g.setColor(Color.BLACK);
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void start() {
        setBounds(0, 0, ScreenX, ScreenY);
        addMouseListener(this);
        setIgnoreRepaint(true);
        createBufferStrategy(2);
        Thread GameRenderThread = new Thread(() -> {
            lasttime = System.currentTimeMillis();
            while (stopped) {
                gameLoop();
            }
        });
        GameRenderThread.setDaemon(true);
        GameRenderThread.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        System.exit(0);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
