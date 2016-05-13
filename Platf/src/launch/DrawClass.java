package launch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DrawClass extends Canvas implements MouseListener {

    private final int ScreenX, ScreenY;
    private final World world;

    public DrawClass(int x, int y, World w) {
        ScreenX = x;
        ScreenY = y;
        world = w;
    }

    public void gameLoop() {
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

    public static Image makeColorTransparent(final BufferedImage im, final Color color) {
        final ImageFilter filter = new RGBImageFilter() {
            public int markerRGB = color.getRGB() | 0xFFFFFFFF;
            @Override
            public final int filterRGB(final int x, final int y, final int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

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
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, world.Levels.get(world.Level).xs, world.Levels.get(world.Level).ys);
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
            g.drawImage(makeColorTransparent(
                    (world.Characters.get(0).isFacingLeft() ? createFlipped(staticImages.Character) : staticImages.Character),
                    Color.WHITE), x, y, null);
        }
        g.translate(ix, iy);
        g.fillRect(0, world.Levels.get(world.Level).ys - 12, world.Levels.get(world.Level).xs, 12);
        for (Rectangle r : world.Levels.get(world.Level)) {
            Rectangle trh = (Rectangle) r.clone();
            tr.grow(2, 2);
            g.fillRect(trh.x, trh.y, trh.width, trh.height);
        }
        g.setColor(Color.BLACK);
        g.drawString(""+world.Characters.get(0).isLeft+" "+world.Characters.get(0).isTop+" "+world.Characters.get(0).isMiddleX+" "+world.Characters.get(0).isMiddleY+" ", 100, 100);
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
            while (!world.isStopped()) {
                gameLoop();
            }
        });
        GameRenderThread.setDaemon(true);
        GameRenderThread.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        world.stop();
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
