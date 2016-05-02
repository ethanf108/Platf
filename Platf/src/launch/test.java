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

public class test extends Canvas implements MouseListener, KeyEventDispatcher {

    public int ScreenX, ScreenY;
    boolean stop = false;
    double gx = 0, gy = 0, gmx = 0, gmy = 0;
    final int gxs = 40, gys = 80;
    private boolean SpacePressed;
    private boolean RightKeyPressed;
    private boolean canJump = true;
    private boolean LeftKeyPressed;
    private boolean canWallJump;
    final int ADD = 2;
    boolean WallJumpLeft = false, ableWallJump = false;

    {
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
    }
    Rectangle Guy = new Rectangle(0, 0, gxs, gys);

    Rectangle NR(int x, int y, int xs, int ys) {
        Rectangle tmp = new Rectangle(x, y, xs, ys);
        tmp.grow(-2, -2);
        return tmp;
    }
    ArrayList<RectWithProps> Rects = new ArrayList<RectWithProps>() {
        {
            add(new RectWithProps(Guy, "p"));
            add(new RectWithProps(NR(0, ScreenY - 10, ScreenX, 10), ""));//floor
            add(new RectWithProps(NR(ScreenX, 0, 10, ScreenY), ""));//right wall
            add(new RectWithProps(NR(-10, 0, 10, ScreenY), ""));//left wall
            add(new RectWithProps(NR(0, -10, ScreenX, 10), ""));//ceil
            add(new RectWithProps(NR(1100, ScreenY - 500, 100, 20), "w"));//rand platf
            add(new RectWithProps(NR(500, ScreenY - 300, 100, 20), "w"));//rand platf
            add(new RectWithProps(NR(800, ScreenY - 300, 100, 20), "w"));//rand platf
            add(new RectWithProps(NR(1100, ScreenY - 50, 100, 20), "w"));//rand platf
        }
    };
    Color ct = Color.BLUE;

    void collisionCheck() {
        for (RectWithProps g : Rects) {
            if (Guy != g.rect) {
                Rectangle tmprect = ((Rectangle) g.rect.clone());
                tmprect.grow(ADD, ADD);
                if (g.rect.x + ADD > Guy.x + Guy.width) {
                    g.isLeft = true;
                    g.isMiddleX = false;
                } else if (g.rect.x + g.rect.width < Guy.x + ADD) {
                    g.isLeft = false;
                    g.isMiddleX = false;
                } else {
                    g.isMiddleX = true;
                }
                if (g.rect.y + ADD > Guy.y + Guy.height) {
                    g.isTop = true;
                    g.isMiddleY = false;
                } else if (g.rect.y + g.rect.height < Guy.y + ADD) {
                    g.isTop = false;
                    g.isMiddleY = false;
                } else {
                    g.isMiddleY = true;
                }
                WallJumpLeft = g.isLeft;
                ableWallJump = !g.isMiddleX;
                if (Guy.intersects(tmprect)) {
                    if (g.isMiddleY) {
                        if (g.isLeft) {
                            if (gmx > 0) {
                                gmx = 0;
                            }
                        } else if (gmx < 0) {
                            gmx = 0;
                        }
                    } else if (g.isMiddleX) {
                        if (g.isTop) {
                            if (gmy > 0) {
                                gmy = 0;
                            }
                        } else if (gmy < 0) {
                            gmy = 0;
                        }
                    }
                    if (g.isTop && g.isMiddleX) {
                        canJump = true;
                    } else {
                        canJump = false;
                    }
                    if (g.Props.contains("w") && g.isMiddleY) {
                        canWallJump = true;
                    } else {
                        canWallJump = false;
                    }
                    ct=Color.GREEN;
                } else {
                    canJump = false;ct=Color.WHITE;
                    canWallJump = false;
                }
            }
        }
    }

    public void GamePhysThread() {
        if (gy >= ScreenY - (gys + 10)) {
            gmy = 0;
            canJump = true;
            gy = ScreenY - (gys + 10);
        } else {
            gmy += 2;
        }
        if (RightKeyPressed) {
            gmx += 0.07;
        } else if (LeftKeyPressed) {
            gmx -= 0.07;
        }
        if (SpacePressed && (canJump || canWallJump)) {
            canJump = false;
            gmy = -220.0;
            if (canWallJump && ableWallJump) {
                gmx = (WallJumpLeft ? 1 : -1) * -8;
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

    public test() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        setBounds(0, 0, getToolkit().getScreenSize().width,
                getToolkit().getScreenSize().height);
        Thread t = new Thread(() -> {
            while (true) {
                GamePhysThread();
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
            Rectangle tr = (Rectangle) r.rect.clone();
            tr.grow(ADD, ADD);
            g.setColor(ct);
            g.fillRect(tr.x, tr.y, tr.width, tr.height);
        }
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
