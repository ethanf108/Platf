package launch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class staticImages {
    public static void init(){
        try {
            Character = ImageIO.read(staticImages.class.getResource("Sprite.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        fly = new BufferedImage(100,20,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) fly.getGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 100, 20);
    }
    public static BufferedImage Character = null;
    public static BufferedImage fly = null;
    public static Rectangle collision;
    public static boolean detectCollision(Rectangle spiderBounds, Rectangle flyBounds) {
            collision = null;
            // Check if the boundires intersect
            if (spiderBounds.intersects(flyBounds)) {
                // Calculate the collision overlay
                Rectangle bounds = getCollision(spiderBounds, flyBounds);
                if (!bounds.isEmpty()) {
                    // Check all the pixels in the collision overlay to determine
                    // if there are any non-alpha pixel collisions...
                    for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
                        for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
                            if (collision(x, y,spiderBounds,flyBounds)) {
                                collision = bounds;
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        public static Rectangle getCollision(Rectangle rect1, Rectangle rect2) {
            Area a1 = new Area(rect1);
            Area a2 = new Area(rect2);
            a1.intersect(a2);
            return a1.getBounds();
        }

        /**
         * Test if a given x/y position of the images contains transparent
         * pixels or not...
         * @param x
         * @param y
         * @return 
         */
        public static boolean collision(int x, int y,Rectangle spiderBounds, Rectangle flyBounds) {
            boolean collision = false;
            int spiderPixel = Character.getRGB(x - spiderBounds.x, y - spiderBounds.y);
            int flyPixel = fly.getRGB(x - flyBounds.x, y - flyBounds.y);
            // 255 is completely transparent, you might consider using something
            // a little less absolute, like 225, to give you a sligtly
            // higher hit right, for example...
            if (((spiderPixel >> 24) & 0xFF) < 255 && ((flyPixel >> 24) & 0xFF) < 255) {
                collision = true;
            }
            return collision;
        }
    
}
