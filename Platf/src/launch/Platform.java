package launch;

import java.awt.Rectangle;

public class Platform extends Rectangle{
    String Props;
    boolean isLeft=false,isMiddleX=false,isTop=false,isMiddleY=false;
    public Platform(Rectangle z,String pro){
        x=z.x;
        y=z.y;
        width=z.width;
        height=z.height;
        Props=pro;
    }
}