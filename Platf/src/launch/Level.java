package launch;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Level extends ArrayList<GameObject>{
    int xs, ys;
    public Level(int xs, int ys){
        super();
        this.xs=xs;
        this.ys=ys;
    }
}
