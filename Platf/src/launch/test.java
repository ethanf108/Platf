package launch;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class test extends Canvas implements MouseListener, KeyEventDispatcher {

    public int ScreenX, ScreenY;
    boolean stop = false;
    double gx = 0, gy = 0, gmx = 0, gmy = 0;
    int gxs = 50, gys = 100;
    private boolean SpacePressed;
    private boolean RightKeyPressed;
    private boolean canJump = true;
    private boolean LeftKeyPressed;
    private boolean canWallJump;
    private boolean isLeftOfWall;

    {
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
    }
    Rectangle Guy = new Rectangle(0, 0, gxs, gys);
    ArrayList<RectWithProps> Rects = new ArrayList<RectWithProps>() {
        {
            add(new RectWithProps(Guy, "p"));
            add(new RectWithProps(new Rectangle(0, ScreenY - 10, ScreenX, 10), "jy"));
            add(new RectWithProps(new Rectangle(ScreenX, 0, 10, ScreenY), "bw"));
            add(new RectWithProps(new Rectangle(-10, 0, 10, ScreenY), "bw"));
            add(new RectWithProps(new Rectangle(0, -10, ScreenX, 10), "by"));
            add(new RectWithProps(new Rectangle(100,ScreenY-50,100,10),"ywj"));
        }
    };

    void collisionCheck() {
        String allProps = "";
        for (RectWithProps g : Rects) {
            if (Guy != g.rect) {
                Rectangle tmprect = ((Rectangle) g.rect.clone());
                tmprect.grow(1, 1);
                if (Guy.intersects(tmprect)) {
                    if (g.Props.contains("w")) {
                        if (g.rect.x> Guy.x) {
                            if (gmx > 0) {
                                gmx = 0;
                                isLeftOfWall=true;
                            }
                        } else if (gmx < 0) {
                            isLeftOfWall=false;
                            gmx = 0;
                        }
                    }
                    if (g.Props.contains("y")) {
                        if (g.rect.y > Guy.y) {
                            if (gmy > 0) {
                                gmy = 0;
                            }
                        } else if (gmy < 0) {
                            gmy = 0;
                        }
                    }
                    allProps += g.Props;
                }
            }
        }
        if (allProps.contains("w")) {
            canWallJump = true;
        }
        if (allProps.contains("b")) {
            canJump = false;
            canWallJump=false;
        }
        if (allProps.contains("y")) {
            canWallJump=false;
            canJump = false;
        }
        if (allProps.contains("j")) {
            canJump = true;
            canWallJump = false;
        }
        if(!allProps.contains("w")){
            canWallJump=false;
        }
    }

    public test() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        setBounds(0, 0, getToolkit().getScreenSize().width,
                getToolkit().getScreenSize().height);
        Thread t = new Thread(() -> {
            while (true) {
                if (gy >= ScreenY - (gys + 10)) {
                    gmy = 0;
                    gy = ScreenY - (gys + 10);
                } else {
                    gmy += 1;
                }
                if (RightKeyPressed) {
                    gmx += 0.1;
                } else if (LeftKeyPressed) {
                    gmx -= 0.1;
                }
                if (SpacePressed && (canJump||canWallJump)) {
                    canJump = false;
                    gmy = -200.0;
                    if(canWallJump){
                        gmx=-8;
                    }
                }
                gmx *= 0.98;
                collisionCheck();
                gx += gmx;
                gy += gmy / 70;
                Guy.x = (int) gx;
                Guy.y = (int) gy;

                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {

                }
            }
        }
        );
        t.setDaemon(true);
        t.start();
        addMouseListener(this);
    }

    public void render(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(0, 0, ScreenX, ScreenY);
        g.setColor(Color.BLUE);
        for (RectWithProps r : Rects) {
            Rectangle tr = r.rect;
            g.fillRect(tr.x, tr.y, tr.width, tr.height);
        }
        g.setColor(Color.GREEN);
        //g.fillRect((int) gx, (int) gy, gxs, gys);
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

    public void start() {
        setIgnoreRepaint(true);
        createBufferStrategy(2);
        Thread GameRenderThread = new Thread() {
            @Override
            public void run() {
                while (!stop) {
                    gameLoop();
                }
            }
        };

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            if (e.getKeyChar() == ' ') {
                SpacePressed = false;
            }
            if (e.getKeyChar() == 'd') {
                RightKeyPressed = false;
            }
            if (e.getKeyChar() == 'a') {
                LeftKeyPressed = false;
            }
        } else {
            if (e.getKeyChar() == ' ') {
                SpacePressed = true;
            }
            if (e.getKeyChar() == 'd') {
                RightKeyPressed = true;
            }
            if (e.getKeyChar() == 'a') {
                LeftKeyPressed = true;
            }
        }
        return false;
    }
}
