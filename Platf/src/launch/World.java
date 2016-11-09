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
    ArrayList<Level> Levels = new ArrayList<>();
    private boolean stopped = false;
    private char CollisionDelay = 5;
    public final char FastMode = 3;
    public final char SlowMode = 7;
    public final char RegularMode = 5;
    /*
    private Thread CollisionThread;
//DEPRECATED

    void collisionCheck() {
        for (GameCharacter gc : Characters) {
            gc.canJump = false;
            gc.canWallJump = false;
        }
        for (Platform tmprect : Levels.get(Level)) {
            Rectangle g = ((Rectangle) tmprect.clone());
            // g.grow(-29,-92);
            for (GameCharacter gc : Characters) {
                //if (gc.intersects(tmprect)) {  
                if (staticImages.detectCollision(gc, g)) {
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
                        }//MLC
                         if (gc.isLeft) {
                         gc.gmx = 0;
                         gc.gx -= 1;
                         } else {
                         gc.gmx = 0;
                         gc.gx += 1;
                         }//END MLC

                    } else if (gc.isMiddleY) {
                        if (gc.isLeft) {
                            if (gc.gmx > 0) {
                                gc.gx -= 1;
                                gc.gmx = 0;
                            }
                        } else if (gc.gmx < 0) {
                            gc.gmx = 0;
                            gc.gx += 1;
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
                    if (tmprect.Props.contains("w") && gc.isMiddleY) {
                        gc.canWallJump = !gc.isMiddleX;
                    }
                }
            }
        }
        try {
            Thread.sleep(CollisionDelay - 1);
        } catch (InterruptedException ex) {
            System.err.println("ERROR");
        }
    }
*/
    void collision(GameCharacter gc) {
        gc.canJump = false;
        gc.canWallJump = false;
        for (GameObject g : Levels.get(Level)) {
            Rectangle tmprect = ((Rectangle) g.clone());
            tmprect.grow(RectScale, RectScale);
            if (g.isEndLevel) {
                if (gc.intersects(tmprect)) {
                    this.Level += 1;
                    gc.x = 0;
                    gc.y = ScreenY - 120;
                    gc.gx = 0;
                    gc.gy = ScreenY - 120;
                    gc.gmx = 0;
                    gc.gmy = 0;
                }
                continue;
            }
            //   if(staticImages.detectCollision(gc, g)){  
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
                        gc.gy = g.getMinY() - g.height;
                    } else {
                        gc.gmy = 0;
                        gc.gy = g.getMaxY();
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
                            gc.gy = g.getMinY() - gc.height;
                        }
                    } else if (gc.gmy < 0) {
                        gc.gmy = 0;
                        gc.gy = g.getMaxY();
                    }
                }
                gc.onHighJump=false;
                if (gc.isTop && gc.isMiddleX) {
                    gc.onHighJump=g.isHighJump;
                    gc.canJump = true;
                }
                if (g.isAbleWallJump && gc.isMiddleY) {
                    gc.canWallJump = !gc.isMiddleX;
                    gc.onHighJump=g.isHighJump;
                }
            }
        }

    }

    public void setSpeed(char mode) {
        CollisionDelay = mode;
    }

    public char getSpeed() {
        return CollisionDelay;
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
