package launch;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class GameCharacter extends Platform implements KeyEventDispatcher {

    private boolean SpacePressed;
    private boolean RightKeyPressed;
    private boolean LeftKeyPressed;
    double gmx, gmy, gx, gy;
    boolean canJump, canWallJump;
    public int keyL=KeyEvent.VK_LEFT,keyS=KeyEvent.VK_UP,keyR=KeyEvent.VK_RIGHT;
    boolean isActive;
    private final Thread GamePhysThread;
    private int ScreenX;
    private int ScreenY;
    private final World world;
    private boolean FacingLeft;
    public boolean isFacingLeft(){
        return FacingLeft;
    }
    public void start() {
        isActive = true;
        GamePhysThread.start();
    }
    public void stop(){
        isActive=false;
    }
    public GameCharacter(Rectangle r,World w) {
        super(r,"p");
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        this.ScreenX=w.Levels.get(w.Level).xs;
        this.ScreenY=w.Levels.get(w.Level).ys;
        this.world = w;
        gy=r.y;
        gx=r.x;
        GamePhysThread = new Thread(() -> {
            while (isActive) {
                update();
            }
        }
        );
        GamePhysThread.setDaemon(true);
    }
    public void update() {
        ScreenX=world.Levels.get(world.Level).xs;
        ScreenY=world.Levels.get(world.Level).ys;
        if (gy >= ScreenY - (height + 10)) {
            gmy = 0;
            canJump = true;
            gy = ScreenY - (height + 10);
        } else if (gy < 0) {
            gmy = 0;
            gy = 0;
        } else {
            gmy += 2;
        }
        if (gx + width > ScreenX) {
            gx = ScreenX - width;
            gmx = 0;
        } else if (gx < 0) {
            gx = 0;
            gmx = 0;
        } else if (RightKeyPressed) {
            gmx += 0.07;
        } else if (LeftKeyPressed) {
            gmx -= 0.07;
        }
        if (SpacePressed && (canJump || canWallJump)) {
            if (canWallJump && !canJump) {
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
        world.collision(this);
        gx += gmx;
        gy += gmy / 70;
        x = (int) gx;
        y = (int) gy;
        try {
            Thread.sleep(world.getSpeed());
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
                FacingLeft=false;
            }
            if (e.getKeyCode() == keyL) {
                FacingLeft=true;
                LeftKeyPressed = true;
            }
        }
        return false;
    }

}
