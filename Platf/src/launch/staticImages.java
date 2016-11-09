package launch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import javax.imageio.ImageIO;

public class staticImages {

    public static BufferedImage makeColorTransparent(final BufferedImage im, final Color color) {
        final ImageFilter filter = new RGBImageFilter() {
            public int markerRGB = color.getRGB() | 0xFFFFFFFF;

            @Override
            public final int filterRGB(final int x, final int y, final int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        Image image = Toolkit.getDefaultToolkit().createImage(ip);
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        return newImage;
    }

    public static void init() {
        try {
            Character = makeColorTransparent(ImageIO.read(staticImages.class.getResource("Sprite.png")), Color.WHITE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static BufferedImage Character = null;

    public static boolean detectCollision(Rectangle R1, Rectangle R2) {
        Rectangle RET = R1.intersection(R2);
        PixelGrabber pix = new PixelGrabber(Character,RET.x,RET.y,RET.height,RET.width,true);
        try {
            pix.grabPixels();
        ColorModel cm = pix.getColorModel();
        return cm.hasAlpha();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static Rectangle getCollision(Rectangle rect1, Rectangle rect2) {
        Area a1 = new Area(rect1);
        Area a2 = new Area(rect2);
        a1.intersect(a2);
        return a1.getBounds();
    }


}
