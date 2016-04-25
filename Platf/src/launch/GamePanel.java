package launch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.BoxLayout;

import javax.swing.JPanel;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyEventDispatcher {

    public final byte TimeSlow = 1;
    public static double SCALE = 45.0;
    public static boolean MouseDown = false;
    public static double X = 0;
    public static double Y = 0;
    public static boolean isPaused = false;
    public static final double NANO_TO_BASE = 1.0e9;


    
    public void CreateFloor() {
        Rectangle floorRect = new Rectangle(15.0, 1.0);
        GameObject floor = new GameObject();
        floor.addFixture(new BodyFixture(floorRect));
        floor.setMass(MassType.INFINITE);
        floor.translate(0.0, -4.0);
        this.world.addBody(floor);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        MouseDown = true;
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        MouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        return false;
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
    protected Canvas canvas;
    protected World world;
    protected boolean stopped;
    protected long last;

    public WindowManager() {
        super();
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        customLayoutManager = new LayoutManager(this);
        JPanel before = new JPanel();
        before.setLayout(new BoxLayout(before, BoxLayout.Y_AXIS));
        add(before);
        Dimension size = new Dimension(800, 600);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(size);
        this.canvas.setMinimumSize(size);
        this.canvas.setMaximumSize(size);

        setBounds(0, 0, getToolkit().getScreenSize().width,
                getToolkit().getScreenSize().height);
        this.add(canvas);
        initializeWorld();
        this.stopped = false;
        world.setGravity(new Vector2(0, -9.8));
        before.add(customLayoutManager.layoutSettings());
    }

    protected final void initializeWorld() {
        this.world = new World();
        this.canvas.addMouseListener(this);
        this.canvas.addMouseMotionListener(this);
        CreateFloor();
    }

    public void start() {
        this.last = System.nanoTime();
        this.canvas.setIgnoreRepaint(true);
        this.canvas.createBufferStrategy(2);
        Thread GameRenderThread = new Thread() {
            @Override
            public void run() {
                while (!isStopped()) {
                    gameLoop();

                }
            }
        };
        Thread ApplyForceThread = new Thread() {
            @Override
            public void run() {
                while (!isStopped()) {
                    try {
                        ApplyForceThread();
                    } catch (InterruptedException ex) {
                        new Popup(ex);
                        MainWindow.MAIN.dispose();
                    }
                }
            }
        };
        GameRenderThread.setDaemon(true);
        GameRenderThread.start();
        ApplyForceThread.setDaemon(true);
        ApplyForceThread.start();
    }

    protected void gameLoop() {
        Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy().getDrawGraphics();
        AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
        AffineTransform move = AffineTransform.getTranslateInstance(400, -300);
        g.transform(yFlip);
        g.transform(move);
        this.render(g);
        g.dispose();
        BufferStrategy strategy = this.canvas.getBufferStrategy();
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

    protected void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(-400, -300, 800, 600);
        g.translate(0.0, -1.0 * SCALE);
        for (int i = 0; i < this.world.getBodyCount(); i++) {
            if (this.world.getBodyCount() == 0) {
                break;
            }
            if (this.world.getBody(i).getTransform().getTranslationY() < -7.0) {
                this.world.removeBody(this.world.getBody(i));
                break;
            }
            GameObject go = (GameObject) this.world.getBody(i);
            go.setLinearDamping(AirRes);
            go.setAngularDamping(AirRes);
            go.render(g);
        }
    }

    public synchronized void stop() {
        this.stopped = true;
    }

    public synchronized boolean isStopped() {
        return this.stopped;
    }

}
