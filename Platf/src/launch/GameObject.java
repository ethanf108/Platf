package launch;

import java.awt.Rectangle;


public class GameObject extends Rectangle{
    final boolean isPlayer;
    final boolean isAbleWallJump;
    final boolean isEndLevel;
    final boolean isHighJump;
    boolean isLeft=false,isMiddleX=false,isTop=false,isMiddleY=false;
    public GameObject(Rectangle z,String pro){
        super(z);
        isAbleWallJump=pro.contains("w");
        isEndLevel=pro.contains("N");
        isPlayer=pro.contains("p");
        isHighJump=pro.contains("h");
    }
}