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
import java.util.ArrayList;

public class World extends Canvas implements MouseListener {

    private static final long serialVersionUID = 1L;
    int Level = 0;
    final int RectScale = 2;
    final int ScreenX, ScreenY;
    Color DebugColor = Color.BLUE;
    ArrayList<GameCharacter> Characters = new ArrayList<>();
    ArrayList<ArrayList<RectWithProps>> Levels = new ArrayList<>();
    boolean stopped = false;
    private Thread CollisionThread;

    {
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
    }

    void collisionCheck() {
        for (GameCharacter gc : Characters) {
            gc.canJump = false;
            gc.canWallJump = false;
        }
        for (RectWithProps g : Levels.get(Level)) {

            Rectangle tmprect = ((Rectangle) g.rect.clone());
            tmprect.grow(RectScale, RectScale);
            for (GameCharacter gc : Characters) {
                if (gc.intersects(tmprect)) {
                    if (g.rect.x + RectScale > gc.gx + gc.gxs) {
                        gc.isLeft = true;
                        gc.isMiddleX = false;
                    } else if (g.rect.x + g.rect.width < gc.gx + RectScale) {
                        gc.isLeft = false;
                        gc.isMiddleX = false;
                    } else {
                        gc.isMiddleX = true;
                    }
                    if (g.rect.y + RectScale > gc.gy + gc.gys) {
                        gc.isTop = true;
                        gc.isMiddleY = false;
                    } else if (g.rect.y + g.rect.height < gc.gy + RectScale) {
                        gc.isTop = false;
                        gc.isMiddleY = false;
                    } else {
                        gc.isMiddleY = true;
                    }
                    gc.ableWallJump = !gc.isMiddleX;
                    if (gc.isMiddleX && gc.isMiddleY) {
                        if (gc.isTop) {
                            gc.gmy = 0;
                            gc.gy -= 1;
                        } else {
                            gc.gmy = 0;
                            gc.gy += 1;
                        }
                        if (gc.isLeft) {
                            gc.gmx = 0;
                            gc.gx -= 1;
                        } else {
                            gc.gmx = 0;
                            gc.gx += 1;
                        }
                    } else if (gc.isMiddleY) {
                        if (gc.isLeft) {
                            if (gc.gmx > 0) {
                                gc.gmx = 0;
                            }
                        } else if (gc.gmx < 0) {
                            gc.gmx = 0;
                        }
                    } else if (gc.isMiddleX) {
                        if (gc.isTop) {
                            if (gc.gmy > 0) {
                                gc.gmy = 0;
                            }
                        } else if (gc.gmy < 0) {
                            gc.gmy = 0;
                        }
                    }
                    if (gc.isTop && gc.isMiddleX) {
                        gc.canJump = true;
                    }
                    if (g.Props.contains("w") && gc.isMiddleY) {
                        gc.canWallJump = true;
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
        setBounds(0, 0, ScreenX, ScreenY);
        addMouseListener(this);
        CollisionThread = new Thread(() -> {
            while (true) {
                collisionCheck();
            }
        }
        );
        CollisionThread.setDaemon(true);
    }

    public World() {
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
        }
        for (GameCharacter gc : Characters) {
            if (gc.isActive) {
                g.setColor(DebugColor);
                g.fillRect(gc.x, gc.y, gc.width, gc.height);
            }
        }
        g.setColor(DebugColor);
        g.fillRect(0, ScreenY - 12, ScreenX, 12);
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
        CollisionThread.start();
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

    
}
