package launch;

import java.awt.Color;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

public class GameCharacter extends GamePanel.GameObject implements CollisionListener, KeyEventDispatcher {

    boolean canJump = false;
    int jump, left, right;
    boolean sL = false, sR = false, isSP = false;
    double velX = 0;
    boolean colliding = false;

    public GameCharacter(Image g, int x, int y, int jumpKey, int LeftKey, int RightKey, World w) {
        super(g, x / -2, y / -2);
        jump = jumpKey;
        right = RightKey;
        left = LeftKey;
        color = Color.ORANGE;
        Rectangle Guy = new Rectangle(GamePanel.Scalar(x), GamePanel.Scalar(y));
        BodyFixture fix = new BodyFixture(Guy);
        addFixture(fix);
        setMass(MassType.NORMAL);
        setUserData("p");
        translate(GamePanel.Scalar(100), GamePanel.Scalar(100));
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        w.addListener(this);
        Thread charUpdateThread = new Thread(() -> {
            while (true) {
                setLinearVelocity(new Vector2(velX, getLinearVelocity().y));
                if (sR) {
                    velX += 1.0;
                }
                if (sL) {
                    velX -= 1.0;
                }
                velX *= 0.9;
                transform.setRotation(0.0);
                setAngularVelocity(0.0);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {

                }
            }
        });
        charUpdateThread.setDaemon(true);
        charUpdateThread.start();

    }

    public void jump() {
        applyForce(new Vector2(0, -800.0));
        if(sR&&colliding){
            velX=-40;
        }
    }

    @Override
    public boolean collision(Body body, BodyFixture bf, Body body1, BodyFixture bf1) {
        if (body1 == this || body == this) {
            if (((String) body.getUserData()).contains("s") || ((String) body1.getUserData()).contains("s")) {
                canJump = true;
                colliding = false;
            }
            if (((String) body.getUserData()).contains("w") || ((String) body1.getUserData()).contains("w")) {
                colliding=true;
            }
        }
        return true;
    }

    @Override
    public boolean collision(Body body, BodyFixture bf, Body body1, BodyFixture bf1, Penetration pntrtn) {
        return true;
    }

    @Override
    public boolean collision(Body body, BodyFixture bf, Body body1, BodyFixture bf1, Manifold mnfld) {
        return true;
    }

    @Override
    public boolean collision(ContactConstraint cc) {
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            if (e.getKeyCode() == jump) {
                isSP = false;
            }
            if (e.getKeyCode() == left) {
                sL = false;
            }
            if (e.getKeyCode() == right) {
                sR = false;
            }
        } else if (e.getID() == KeyEvent.KEY_PRESSED) {
            if (e.getKeyCode() == jump) {
                if (!isSP && canJump) {
                    isSP = true;
                    jump();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    canJump = false;
                }
            }
            if (e.getKeyCode() == left) {
                sL = true;
            }
            if (e.getKeyCode() == right) {
                sR = true;
            }
        }
        return false;

    }
}
