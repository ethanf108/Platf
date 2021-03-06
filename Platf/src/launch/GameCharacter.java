package launch;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.lang.ref.SoftReference;

public class GameCharacter extends GameObject implements KeyEventDispatcher {

    private boolean SpacePressed;
    private boolean RightKeyPressed;
    private boolean LeftKeyPressed;
    double gmx, gmy, gx, gy;
    boolean canJump, canWallJump,onHighJump;
    public int keyL=KeyEvent.VK_A,keyS=KeyEvent.VK_SPACE,keyR=KeyEvent.VK_D,keyC=KeyEvent.VK_S;
    boolean isActive;
    private final Thread GamePhysThread;
    private int ScreenX;
    private int ScreenY;
    private final SoftReference<World> SoftWorld;
    private boolean FacingLeft;
    public boolean isCrouching = false;
    public int CrouchDecrease = 10;
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
        this.SoftWorld = new SoftReference<>(w);
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
    public double getJumpHeight(){
        return !onHighJump?(isCrouching?-140:-220.0):(isCrouching?-220:-280.0);
    }
    public void update() {
        World world = this.SoftWorld.get();
        ScreenX=world.Levels.get(world.Level).xs;
        ScreenY=world.Levels.get(world.Level).ys;
        if (gy >= ScreenY - (height + 10)) {
            gmy = 0;
            canJump = true;
            onHighJump=false;
            gy = ScreenY - (height + 10);
        } else if (gy < 0) {
            gmy = 0;
            gy = 0;
        } else {
            gmy += isCrouching?5:2;
        }
        if (gx + width > ScreenX) {
            gx = ScreenX - width;
            gmx = 0;
        } else if (gx < 0) {
            gx = 0;
            gmx = 0;
        } else if (RightKeyPressed) {
            gmx += isCrouching?0.04:0.07;
        } else if (LeftKeyPressed) {
            gmx -= isCrouching?0.04:0.07;
        }
        if (SpacePressed && (canJump || canWallJump)) {
            if (canWallJump && !canJump) {
                if (isLeft && RightKeyPressed) {
                    gmx = isCrouching?-1:-6;
                    gmy = getJumpHeight();
                } else if (!isLeft && LeftKeyPressed) {
                    gmx = isCrouching?1:6;
                    gmy = getJumpHeight();
                }
            } else {
                gmy = getJumpHeight();
            }
        }
        if(Math.abs(gmy)>280)gmy=280*(gmy/Math.abs(gmy));
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
            if(e.getKeyCode() == keyC){
                if(isCrouching){
                    this.height+=this.CrouchDecrease;
                    this.y-=this.CrouchDecrease;
                    this.gy-=this.CrouchDecrease;
                }
                isCrouching=false;
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
            if(e.getKeyCode() == keyC){
                if(!isCrouching){
                    this.height-=this.CrouchDecrease;
                    this.gy+=this.CrouchDecrease;
                    this.y+=this.CrouchDecrease;
                }
                isCrouching=true;
            }
        }
        return false;
    }

}
