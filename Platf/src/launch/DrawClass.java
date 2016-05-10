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
import java.awt.image.BufferedImage;

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
    int oldLevel = 0;
    BufferedImage LevelBackdrop = null;

    public void setBackdrop() {
        LevelBackdrop = new BufferedImage(world.Levels.get(world.Level).xs, world.Levels.get(world.Level).ys, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = LevelBackdrop.createGraphics();
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, world.Levels.get(world.Level).xs, world.Levels.get(world.Level).ys);
        g.setColor(Color.BLUE);
        for (Rectangle r : world.Levels.get(world.Level)) {
            Rectangle tr = (Rectangle) r.clone();
            tr.grow(2, 2);
            g.fillRect(tr.x, tr.y, tr.width, tr.height);
        }
        g.setColor(Color.BLUE);
        g.fillRect(0, world.Levels.get(world.Level).ys - 12, world.Levels.get(world.Level).xs, 12);
    }

    public void render(Graphics2D g) {
        if (oldLevel != world.Level) {
            oldLevel = world.Level;
            setBackdrop();
        }
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, ScreenX-world.Levels.get(world.Level).xs, ScreenX-world.Levels.get(world.Level).ys);
        g.setColor(Color.BLUE);
        Rectangle tr = (Rectangle) world.Characters.get(0).clone();
        tr.grow(2, 2);
        int x,ix,y,iy;
        if (tr.x < ScreenX/2) {
            x=tr.x;
            ix=0;
        } else if(tr.x>world.Levels.get(world.Level).xs-(ScreenX/2)){
            ix=ScreenX-world.Levels.get(world.Level).xs;
            x=(ScreenX)-(world.Levels.get(world.Level).xs-(tr.x));
        }else{
            ix=(ScreenX/2)-tr.x;
            x=ScreenX/2;
        }
        if (tr.y < ScreenY/2) {
            y=tr.y;
            iy=0;
        } else if(tr.y>world.Levels.get(world.Level).ys-(ScreenY/2)){
            iy=ScreenY-world.Levels.get(world.Level).ys;
            y=(ScreenY)-(world.Levels.get(world.Level).ys-(tr.y));
        }else{
            iy=(ScreenY/2)-tr.y;
            y=ScreenY/2;
        }
        g.drawImage(LevelBackdrop, null, ix, iy);
        g.fillRect(x, y, tr.width, tr.height);
    }

    public void start() {
        oldLevel = world.Level;
        LevelBackdrop = new BufferedImage(ScreenX*2, ScreenY, BufferedImage.TYPE_INT_RGB);
        setBackdrop();
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
