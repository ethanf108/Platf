package launch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import javax.swing.JPanel;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;

public class GamePanel extends Canvas implements MouseListener, MouseMotionListener, KeyEventDispatcher {

    World world = new World();
    long last;
    boolean notStopped = true;
    public static final double NANO_TO_BASE = 1.0e9;
    public double TimeSlow = 1.0;
    public boolean isPaused = false;
    public static double SCALE = 1;
    boolean isGamePaused = false;
    static int X;
    static int Y;
    static boolean isMouseDown = false;
    final int ScreenX;
    final int ScreenY;
    public static class GameObject extends Body {
        protected Color color;
        public static Color colorGen() {
            return new Color(
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f);

        }

        public GameObject() {
            this.color = colorGen();
        }

        public void render(Graphics2D g) {
            AffineTransform ot = g.getTransform();

            AffineTransform lt = new AffineTransform();
            lt.translate(this.transform.getTranslationX() * SCALE, this.transform.getTranslationY() * SCALE);
            lt.rotate(this.transform.getRotation());
            g.transform(lt);
            for (BodyFixture fixture : this.fixtures) {
                Convex convex = fixture.getShape();
                Graphics2DRenderer.render(g, convex, SCALE, color);
                g.rotate(0 - transform.getRotation());
                g.setColor(Color.BLACK);
                AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
                g.transform(yFlip);
                g.setTransform(ot);
            }
        }
    }

    protected void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, ScreenX,ScreenY);
        g.translate(0.0, -1.0 * SCALE);
        if (isGamePaused) {
            g.setColor(Color.red);
            g.fillRect(X, Y, 10,10);
            g.fillRect(ScreenX-50,0,50,50);
        } else {
            for (int i = 0; i < this.world.getBodyCount(); i++) {
                if (this.world.getBodyCount() == 0) {
                    break;
                }
                if (this.world.getBody(i).getTransform().getTranslationY() < -7.0) {
                    this.world.removeBody(this.world.getBody(i));
                    break;
                }
                GameObject go = (GameObject) this.world.getBody(i);
                go.render(g);
            }
        }
    }

    void gameLoop() {
        Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();
        render(g);
        g.dispose();
        BufferStrategy strategy = getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }
        Toolkit.getDefaultToolkit().sync();
        long time = System.nanoTime();
        long diff = time - this.last;
        this.last = time;
        double elapsedTime = diff / NANO_TO_BASE;
        if (!isPaused) {
            if (this.world.getBodyCount() != 0) {
                this.world.update(elapsedTime / TimeSlow);
            }
        }
    }

    public void start() {
        this.last = System.nanoTime();
        setIgnoreRepaint(true);
        createBufferStrategy(2);
        Thread GameRenderThread = new Thread() {
            @Override
            public void run() {
                while (notStopped) {
                    gameLoop();
                }
            }
        };
        GameRenderThread.setDaemon(true);
        GameRenderThread.start();
    }

    public GamePanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;

    }
//<editor-fold defaultstate="collapsed" desc="Listeners">

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isMouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if(e.getID() != KeyEvent.KEY_RELEASED)return false;
        switch(e.getKeyCode()){
            case KeyEvent.VK_ESCAPE:
                isGamePaused = !isGamePaused;
                return true;
            default:
                return false;
        }
    }
    //</editor-fold>

}
