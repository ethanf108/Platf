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

public class GameRenderer extends Canvas implements MouseListener, KeyEventDispatcher {

    private static final long serialVersionUID = 1L;
    private int Level = 0;
    private double gx, gy, gmx = 0, gmy = 0;
    private boolean SpacePressed, RightKeyPressed, canJump = true,
            LeftKeyPressed, canWallJump, WallJumpLeft = false,
            ableWallJump = false, stopped = false;
    private final int RectScale = 2, gxs = 40, gys = 80, ScreenX, ScreenY;
    private Thread GamePhysThread;
    Color DebugColor = Color.BLUE;
    Rectangle Guy = new Rectangle((int) gx, (int) gy, gxs, gys);

    {
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
        gx = 10;
        gy = ScreenY - 150;
    }

    Rectangle RWP(int x, int y, int xs, int ys) {
        Rectangle tmp = new Rectangle(x, y, xs, ys);
        tmp.grow(-2, -2);
        return tmp;
    }
    private volatile ArrayList<ArrayList<RectWithProps>> Rects = new ArrayList<ArrayList<RectWithProps>>() {
        private static final long serialVersionUID = 1L;

        {
            add(
                    new ArrayList<RectWithProps>() {
                        private static final long serialVersionUID = 1L;

                        {
                            add(new RectWithProps(RWP(200, ScreenY - 150, 100, 20), "w"));//rand platf
                            add(new RectWithProps(RWP(500, ScreenY - 250, 100, 20), "w"));//rand platf
                            add(new RectWithProps(RWP(800, ScreenY - 350, 100, 20), "w"));//rand platf
                            add(new RectWithProps(RWP(1100, ScreenY - 450, 100, 20), "w"));//rand platf
                        }
                    }
            );
            add(
                    new ArrayList<RectWithProps>() {
                        private static final long serialVersionUID = 1L;

                        {
                            add(new RectWithProps(RWP(800, 0, 100, 1000), "w"));//rand platf WallDemo
                        }
                    });
        }
    };

    void collisionCheck() {
        canJump = false;
        canWallJump = false;
        for (RectWithProps g : Rects.get(Level)) {
            if (Guy != g.rect) {
                Rectangle tmprect = ((Rectangle) g.rect.clone());
                tmprect.grow(RectScale, RectScale);
                if (g.rect.x + RectScale > Guy.x + Guy.width) {
                    g.isLeft = true;
                    g.isMiddleX = false;
                } else if (g.rect.x + g.rect.width < Guy.x + RectScale) {
                    g.isLeft = false;
                    g.isMiddleX = false;
                } else {
                    g.isMiddleX = true;
                }
                if (g.rect.y + RectScale > Guy.y + Guy.height) {
                    g.isTop = true;
                    g.isMiddleY = false;
                } else if (g.rect.y + g.rect.height < Guy.y + RectScale) {
                    g.isTop = false;
                    g.isMiddleY = false;
                } else {
                    g.isMiddleY = true;
                }
                if (Guy.intersects(tmprect)) {
                    WallJumpLeft = g.isLeft;
                    ableWallJump = !g.isMiddleX;
                    if (g.isMiddleX && g.isMiddleY) {
                        if (g.isTop) {
                            gmy = 0;
                            gy -= 1;
                        } else {
                            gmy = 0;
                            gy += 1;
                        }
                        if (g.isLeft) {
                            gmx = 0;
                            gx -= 1;
                        } else {
                            gmx = 0;
                            gx += 1;
                        }
                    } else if (g.isMiddleY) {
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
                    }
                    if (g.Props.contains("w") && g.isMiddleY) {
                        canWallJump = true;
                    }
                }
            }
        }
    }

    public void GamePhysThread() {
        if (gy >= ScreenY - (gys + 10)) {
            gmy = 0;
            canJump = true;
            gy = ScreenY - (gys + 10);
        } else if (gy < 0) {
            gmy = 0;
            gy = 0;
        } else {
            gmy += 2;
        }
        if (gx + gxs > ScreenX) {
            gx = ScreenX - gxs;
            gmx = 0;
        } else if (gx < 0) {
            gx = 0;
            gmx = 0;
        } else if (RightKeyPressed) {
            gmx += 0.07;
        } else if (LeftKeyPressed) {
            gmx -= 0.07;
        }
        if (SpacePressed && (canJump || (canWallJump && ableWallJump))) {
            if (canWallJump && ableWallJump && !canJump) {
                if (WallJumpLeft && RightKeyPressed) {
                    gmx = -6;
                    gmy = -220.0;
                } else if (!WallJumpLeft && LeftKeyPressed) {
                    gmx = 6;
                    gmy = -220.0;
                }
            } else {
                gmy = -220.0;
            }
        }
        gmx *= 0.97;
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

    public void init() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        setBounds(0, 0, ScreenX, ScreenY);
        addMouseListener(this);
    }

    public GameRenderer() {
        super();
    }

    public void render(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, ScreenX, ScreenY);
        for (RectWithProps r : Rects.get(Level)) {
            Rectangle tr = (Rectangle) r.rect.clone();
            tr.grow(RectScale, RectScale);
            g.setColor(DebugColor);
            g.fillRect(tr.x, tr.y, tr.width, tr.height);
            //g.setColor(Color.BLACK);
            g.drawRect(tr.x, tr.y, tr.width, tr.height);
        }
        g.setColor(DebugColor);
        g.fillRect(Guy.x, Guy.y-2, Guy.width, Guy.height);
        g.fillRect(0, ScreenY - 12, ScreenX, 12);
        //g.setColor(Color.BLACK);
        g.drawRect(Guy.x, Guy.y-2, Guy.width, Guy.height);
        g.drawRect(0, ScreenY - 12, ScreenX, 12);
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
                while (!stopped) {
                    gameLoop();
                }
            }
        };
        GameRenderThread.setDaemon(true);
        GameRenderThread.start();
        GamePhysThread = new Thread(() -> {
            while (true) {
                GamePhysThread();
            }
        }
        );
        GamePhysThread.setDaemon(true);
        GamePhysThread.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        stopped = true;
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
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                SpacePressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                RightKeyPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                LeftKeyPressed = false;
            }
            if (e.getKeyChar() == 'u') {
                Level = (++Level) % 2;
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                SpacePressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                RightKeyPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                LeftKeyPressed = true;
            }
        }
        return false;
    }

}
