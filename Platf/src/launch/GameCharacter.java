package launch;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class GameCharacter extends Rectangle implements KeyEventDispatcher {

    private boolean SpacePressed;
    private boolean RightKeyPressed;
    private boolean LeftKeyPressed;
    double gmx, gmy, gx, gy, gys, gxs;
    boolean canJump, canWallJump, ableWallJump, isLeft, isMiddleX, isMiddleY, isTop;
    private final World World;
    public int keyL=KeyEvent.VK_LEFT,keyS=KeyEvent.VK_UP,keyR=KeyEvent.VK_RIGHT;
    boolean isActive;
    private final Thread GamePhysThread;

    public void start() {
        isActive = true;
        GamePhysThread.start();
    }
    public void stop(){
        isActive=false;
    }

    public GameCharacter(int x, int y, int xs, int ys, World world) {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        this.x = x;
        this.y = y;
        this.width = xs;
        this.height = ys;
        this.gxs = xs;
        this.gys = ys;
        this.World = world;
        GamePhysThread = new Thread(() -> {
            while (isActive) {
                update();
            }
        }
        );
        GamePhysThread.setDaemon(true);
    }

    public void update() {
        if (gy >= World.ScreenY - (gys + 10)) {
            gmy = 0;
            canJump = true;
            gy = World.ScreenY - (gys + 10);
        } else if (gy < 0) {
            gmy = 0;
            gy = 0;
        } else {
            gmy += 2;
        }
        if (gx + gxs > World.ScreenX) {
            gx = World.ScreenX - gxs;
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
            if (e.getKeyCode() == keyS) {
                SpacePressed = false;
            }
            if (e.getKeyCode() == keyR) {
                RightKeyPressed = false;
            }
            if (e.getKeyCode() == keyL) {
                LeftKeyPressed = false;
            }
        } else {
            if (e.getKeyCode() == keyS) {
                SpacePressed = true;
            }
            if (e.getKeyCode() == keyR) {
                RightKeyPressed = true;
            }
            if (e.getKeyCode() == keyL) {
                LeftKeyPressed = true;
            }
        }
        return false;
    }

}
