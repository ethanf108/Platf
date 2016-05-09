package launch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

public class DrawClass extends Canvas implements MouseListener {

    private final int ScreenX, ScreenY;
    private final World world;

    public DrawClass(int x, int y,World w) {
        ScreenX = x;
        ScreenY = y;
        world=w;
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

    public void render(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, ScreenX, ScreenY);
        for (Rectangle r : world.getDrawBodies()) {
            Rectangle tr = (Rectangle) r.clone();
            tr.grow(2, 2);
            g.setColor(Color.BLUE);
            g.fillRect(tr.x, tr.y, tr.width, tr.height);
        }
        g.setColor(Color.BLUE);
        g.fillRect(0, ScreenY - 12, ScreenX, 12);
    }

    public void start() {
        setBounds(0, 0, ScreenX, ScreenY);
        addMouseListener(this);
        setIgnoreRepaint(true);
        createBufferStrategy(2);
        Thread GameRenderThread = new Thread() {
            @Override
            public void run() {
                while (!world.stopped) {
                    gameLoop();
                }
            }
        };
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
