package launch;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class GameCharacter extends Rectangle implements KeyEventDispatcher {

    private boolean SpacePressed;
    private boolean RightKeyPressed;
    private boolean LeftKeyPressed;
    double gmx, gmy, gx, gy, gys, gxs;
    private static final int ScreenX, ScreenY;
    boolean canJump, canWallJump, ableWallJump, isLeft,isMiddleX,isMiddleY,isTop;
    private GameRenderer World;

    static{
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
    }

    public GameCharacter(int x, int y, int xs, int ys,GameRenderer world) {
        this.x = x;
        this.y = y;
        this.width = xs;
        this.height = ys;
        this.gxs=xs;
        this.gys=ys;
        this.World=world;
        Thread GamePhysThread = new Thread(() -> {
            while (true) {
                update();
            }
        }
        );
        GamePhysThread.setDaemon(true);
        GamePhysThread.start();
    }

    public void update() {
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
                if (isLeft && RightKeyPressed) {
                    gmx = -6;
                    gmy = -220.0;
                } else if (!isLeft && LeftKeyPressed) {
                    gmx = 6;
                    gmy = -220.0;
                }
            } else {
                gmy = -220.0;
            }
        }
        gmx *= 0.97;
        gx += gmx;
        gy += gmy / 70;
        x = (int) gx;
        y = (int) gy;
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
        }
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
