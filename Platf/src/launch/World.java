package launch;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

public class World {

    private static final long serialVersionUID = 1L;
    int Level = 0;
    final int RectScale = 2;
    final int ScreenX, ScreenY;
    Color DebugColor = Color.BLUE;
    ArrayList<GameCharacter> Characters = new ArrayList<>();
    ArrayList<ArrayList<Platform>> Levels = new ArrayList<>();
    boolean stopped = false;
    private Thread CollisionThread;

    void collisionCheck() {
        for (GameCharacter gc : Characters) {
            gc.canJump = false;
            gc.canWallJump = false;
        }
        for (Platform g : Levels.get(Level)) {
            Rectangle tmprect = ((Rectangle) g.clone());
            tmprect.grow(RectScale, RectScale);
            for (GameCharacter gc : Characters) {
                if (gc.intersects(tmprect)) {
                    if (g.x + RectScale > gc.gx + gc.width) {
                        gc.isLeft = true;
                        gc.isMiddleX = false;
                    } else if (g.x + g.width < gc.gx + RectScale) {
                        gc.isLeft = false;
                        gc.isMiddleX = false;
                    } else {
                        gc.isMiddleX = true;
                    }
                    if (g.y + RectScale > gc.gy + gc.height) {
                        gc.isTop = true;
                        gc.isMiddleY = false;
                    } else if (g.y + g.height < gc.gy + RectScale) {
                        gc.isTop = false;
                        gc.isMiddleY = false;
                    } else {
                        gc.isMiddleY = true;
                    }
                    if (gc.isMiddleX && gc.isMiddleY) {
                        if (gc.isTop) {
                            gc.gmy = 0;
                            gc.gy -= 1;
                        } else {
                            gc.gmy = 0;
                            gc.gy += 5;
                        }/*
                        if (gc.isLeft) {
                            gc.gmx = 0;
                            gc.gx -= 1;
                        } else {
                            gc.gmx = 0;
                            gc.gx += 1;
                        }*/
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
                        gc.canWallJump = !gc.isMiddleX;
                    }
                }
            }
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException ex) {
            System.err.println("ERROR");
        }
    }

    public void init() {
        CollisionThread = new Thread(() -> {
            while (!stopped) {
                collisionCheck();
            }
        }
        );
        CollisionThread.setDaemon(true);
    }

    public void start() {
        CollisionThread.start();
    }

    public void stop() {
        for (GameCharacter g : Characters) {
            g.stop();
        }
        stopped = true;
    }

    public World(int sx, int sy) {
        super();
        ScreenX = sx;
        ScreenY = sy;
    }

    public ArrayList<Rectangle> getDrawBodies() {
        ArrayList tmp = (ArrayList) Levels.get(Level).clone();
        tmp.addAll((ArrayList) Characters.clone());
        return tmp;
    }

}
