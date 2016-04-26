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
import java.awt.geom.Path2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javax.swing.JPanel;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyEventDispatcher {

    public final byte TimeSlow = 1;
    public static double SCALE = 45;
    public static boolean MouseDown = false;
    public static double X = 0;
    public static double Y = 0;
    public static boolean isPaused = false;
    public static final double NANO_TO_BASE = 1.0e9;
    private final int ScreenY;
    private final int ScreenX;
    Body MoveGuy = null;

    public double Scalar(double s) {
        return s / SCALE;
    }

    public void CreateObs() {
        Rectangle floorRect = new Rectangle(Scalar(ScreenX), Scalar(100.0));
        GameObject floor = new GameObject();
        floor.addFixture(new BodyFixture(floorRect));
        floor.setMass(MassType.INFINITE);
        floor.translate(Scalar(ScreenX / 2.0), Scalar(ScreenY + 40.0));
        floor.color = Color.GREEN;
        this.world.addBody(floor);
        Rectangle Guy = new Rectangle(Scalar(50), Scalar(100));
        GameObject GuyOb = null;
        try {
            GuyOb = new GameObject(ImageIO.read(this.getClass().getResourceAsStream("mario.jpg")));
        } catch (IOException ex) {
        }
        GuyOb.color = Color.RED;
        BodyFixture fix = new BodyFixture(Guy);
        GuyOb.addFixture(fix);
        GuyOb.setMass(MassType.NORMAL);
        GuyOb.translate(Scalar(100), Scalar(100));
        MoveGuy = GuyOb;
        this.world.addBody(GuyOb);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.exit(0);
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
    double force = 0;
    byte whichKey;

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            whichKey = 0;
        } else {
            whichKey = 1;
        }
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
        boolean isImage;
        BufferedImage im = null;

        public static Color colorGen() {
            return new Color(
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f,
                    (float) Math.random() * 0.5f + 0.5f);

        }

        public GameObject(BufferedImage g) {
            im = g;
        }

        public GameObject() {
            this.color = colorGen();
            isImage = false;
        }

        public final void render(Graphics2D g, Polygon polygon, double scale, Color color, BufferedImage im) {
               Vector2[] vertices = polygon.getVertices();
                int l = vertices.length;

                // create the awt polygon
                Path2D.Double p = new Path2D.Double();
                p.moveTo(vertices[0].x * scale, vertices[0].y * scale);
                for (int i = 1; i < l; i++) {
                    p.lineTo(vertices[i].x * scale, vertices[i].y * scale);
                }
                p.closePath();

                // fill the shape
                g.setColor(color);
                g.fill(p);
                // draw the outline
                g.setColor(getOutlineColor(color));
                g.draw(p);

            if (im != null) {
                g.drawImage(im, null,this.getFixture(0));
            }
        }

        private static final Color getOutlineColor(Color color) {
            Color oc = color.darker();
            return new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), color.getAlpha());
        }

        public void render(Graphics2D g) {
            AffineTransform ot = g.getTransform();
            AffineTransform lt = new AffineTransform();
            lt.translate(this.transform.getTranslationX() * SCALE, this.transform.getTranslationY() * SCALE);
            lt.rotate(this.transform.getRotation());
            g.transform(lt);
            for (BodyFixture fixture : this.fixtures) {
                Convex convex = fixture.getShape();
                render(g, (Polygon) convex, SCALE, color, im);
                g.rotate(0 - transform.getRotation());
                g.setColor(Color.BLACK);
                g.setTransform(ot);
            }
        }
    }
    protected Canvas canvas;
    protected World world;
    protected boolean stopped;
    protected long last;

    public GamePanel() {
        super();
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        setBounds(0, 0, getToolkit().getScreenSize().width,
                getToolkit().getScreenSize().height);
        this.add(canvas);
        initializeWorld();
        this.stopped = false;
        world.setGravity(new Vector2(0, 9.8));
    }

    protected final void initializeWorld() {
        this.world = new World();
        this.canvas.addMouseListener(this);
        this.canvas.addMouseMotionListener(this);
        CreateObs();
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

        GameRenderThread.setDaemon(true);
        GameRenderThread.start();
    }

    protected void gameLoop() {
        MoveGuy.setAsleep(false);
        Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy().getDrawGraphics();
        this.render(g);
        g.dispose();
        BufferStrategy strategy = this.canvas.getBufferStrategy();
        MoveGuy.setLinearVelocity(new Vector2(force, MoveGuy.getLinearVelocity().y));
        if (whichKey == 1) {
            force += 1.0;
        }
        force *= 0.9;
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
        g.setColor(Color.RED);
        g.fillRect(0, 0, ScreenX, ScreenY);
        for (int i = 0; i < this.world.getBodyCount(); i++) {
            GameObject go = (GameObject) this.world.getBody(i);
            go.render(g);
        }
    }

    public synchronized boolean isStopped() {
        return this.stopped;
    }

}
