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
    int Level = 0;
    boolean SpacePressed, RightKeyPressed,
            LeftKeyPressed;
    final int RectScale = 2, gxs = 40, gys = 80;
    final static int ScreenX, ScreenY;
    Color DebugColor = Color.BLUE;
    GameCharacter Guy = new GameCharacter(0, 0, 40, 80, this);
    ArrayList<GameCharacter> Characters;
    boolean stopped = false;

    static {
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
    }

    static volatile ArrayList<ArrayList<RectWithProps>> Levels;

    void collisionCheck() {
        Guy.canJump = false;
        Guy.canWallJump = false;
        for (RectWithProps g : Levels.get(Level)) {
            if (Guy != g.rect) {

                Rectangle tmprect = ((Rectangle) g.rect.clone());
                tmprect.grow(RectScale, RectScale);
                if (Guy.intersects(tmprect)) {
                    if (g.rect.x + RectScale > Guy.gx + Guy.gxs) {
                        Guy.isLeft = true;
                        Guy.isMiddleX = false;
                    } else if (g.rect.x + g.rect.width < Guy.gx + RectScale) {
                        Guy.isLeft = false;
                        Guy.isMiddleX = false;
                    } else {
                        Guy.isMiddleX = true;
                    }
                    if (g.rect.y + RectScale > Guy.gy + Guy.gys) {
                        Guy.isTop = true;
                        Guy.isMiddleY = false;
                    } else if (g.rect.y + g.rect.height < Guy.gy + RectScale) {
                        Guy.isTop = false;
                        Guy.isMiddleY = false;
                    } else {
                        Guy.isMiddleY = true;
                    }
                    //WallJumpLeft = g.isLeft;
                    Guy.ableWallJump = !Guy.isMiddleX;
                    if (Guy.isMiddleX && Guy.isMiddleY) {
                        if (Guy.isTop) {
                            Guy.gmy = 0;
                            Guy.gy -= 1;
                        } else {
                            Guy.gmy = 0;
                            Guy.gy += 1;
                        }
                        if (Guy.isLeft) {
                            Guy.gmx = 0;
                            Guy.gx -= 1;
                        } else {
                            Guy.gmx = 0;
                            Guy.gx += 1;
                        }
                    } else if (Guy.isMiddleY) {
                        if (Guy.isLeft) {
                            if (Guy.gmx > 0) {
                                Guy.gmx = 0;
                            }
                        } else if (Guy.gmx < 0) {
                            Guy.gmx = 0;
                        }
                    } else if (Guy.isMiddleX) {
                        if (Guy.isTop) {
                            if (Guy.gmy > 0) {
                                Guy.gmy = 0;
                            }
                        } else if (Guy.gmy < 0) {
                            Guy.gmy = 0;
                        }
                    }
                    if (Guy.isTop && Guy.isMiddleX) {
                        Guy.canJump = true;
                    }
                    if (g.Props.contains("w") && Guy.isMiddleY) {
                        Guy.canWallJump = true;
                    }
                }
            }
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void init() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(Guy);
        setBounds(0, 0, ScreenX, ScreenY);
        addMouseListener(this);
        Thread GamePhysThread = new Thread(() -> {
            while (true) {
                collisionCheck();
            }
        }
        );
        GamePhysThread.setDaemon(true);
        GamePhysThread.start();
    }

    public GameRenderer() {
        super();
    }

    public void render(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, ScreenX, ScreenY);
        for (RectWithProps r : Levels.get(Level)) {
            Rectangle tr = (Rectangle) r.rect.clone();
            tr.grow(RectScale, RectScale);
            g.setColor(DebugColor);
            g.fillRect(tr.x, tr.y, tr.width, tr.height);
            //g.setColor(Color.BLACK);
            g.drawRect(tr.x, tr.y, tr.width, tr.height);
        }
        g.setColor(DebugColor);
        g.fillRect(Guy.x, Guy.y - 2, Guy.width, Guy.height);
        g.fillRect(0, ScreenY - 12, ScreenX, 12);
        g.setColor(Color.BLACK);
        g.drawString("" + Guy.x + " " + Guy.y, 100, 100);
    }

    public void gameLoop() {
        Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();
//        collisionCheck();
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
