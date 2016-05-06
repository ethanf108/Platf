package launch;

import com.sun.glass.events.KeyEvent;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class MainWindow extends JFrame {


        private int ScreenY;
        private Color DebugColor=Color.BLUE;
        private int ScreenX;
    {
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
    }
    private static final long serialVersionUID = 1L;

    World GameRenderPanel = null;

    public void handleExceptionPopup(Throwable e) {
        dispose();
        Popup errorPopup = new Popup(e);
    }

    public static Rectangle RWP(int x, int y, int xs, int ys) {
        Rectangle tmp = new Rectangle(x, y, xs, ys);
        tmp.grow(-2, -2);
        return tmp;
    }
    public class DrawClass extends Canvas implements MouseListener{
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
        for (Rectangle r : GameRenderPanel.getDrawBodies()) {
            Rectangle tr = (Rectangle) r.clone();
            tr.grow(2, 2);
            g.setColor(DebugColor);
            g.fillRect(tr.x, tr.y, tr.width, tr.height);
        }
        g.setColor(DebugColor);
        g.fillRect(0, ScreenY - 12, ScreenX, 12);
    }
    public void start() {

        setBounds(0, 0, ScreenX, ScreenY);
        addMouseListener(this);        setIgnoreRepaint(true);
        createBufferStrategy(2);
        Thread GameRenderThread = new Thread() {
            @Override
            public void run() {
                while (!GameRenderPanel.stopped) {
                    gameLoop();
                }
            }
        };
        GameRenderThread.setDaemon(true);
        GameRenderThread.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GameRenderPanel.stopped = true;
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
    public void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        GameRenderPanel = new World(ScreenX,ScreenY);
        GameRenderPanel.init();
        GameRenderPanel.Levels = new ArrayList<ArrayList<Platform>>() {
            private static final long serialVersionUID = 1L;

            {
                add(
                        new ArrayList<Platform>() {
                    private static final long serialVersionUID = 1L;

                    {
                        add(new Platform(RWP(200, GameRenderPanel.ScreenY - 150, 100, 20), "w"));//rand platf
                        add(new Platform(RWP(500, GameRenderPanel.ScreenY - 250, 100, 20), "w"));//rand platf
                        add(new Platform(RWP(800, GameRenderPanel.ScreenY - 350, 100, 20), "w"));//rand platf
                        add(new Platform(RWP(1100, GameRenderPanel.ScreenY - 450, 100, 20), "w"));//rand platf
                    }
                }
                );
                add(
                        new ArrayList<Platform>() {
                    private static final long serialVersionUID = 1L;

                    {
                        add(new Platform(RWP(800, 0, 100, 1000), "w"));//rand platf WallDemo
                    }
                });
            }
        };
        GameRenderPanel.Characters.add(new GameCharacter(0, 0, 40, 80, GameRenderPanel){{
            start();
        }});
        GameRenderPanel.Characters.add(new GameCharacter(0, 0, 40, 80, GameRenderPanel){{
            start();
            keyL=KeyEvent.VK_A;
            keyS=KeyEvent.VK_SPACE;
            keyR=KeyEvent.VK_D;
        }});DrawClass f = new DrawClass();
        getContentPane().add(f);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        setVisible(true);        f.start();GameRenderPanel.start();

        pack();
        Thread.UncaughtExceptionHandler h = (Thread th, Throwable e) -> {
            handleExceptionPopup(e);
        };
        Thread.setDefaultUncaughtExceptionHandler(h);
    }

    public MainWindow() {
        super("");
        SwingUtilities.invokeLater(() -> {
            init();
            
        });
    }

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
    }

}
