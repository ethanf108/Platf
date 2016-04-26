package launch;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;


public final class Graphics2DRenderer {

	public static final void render(Graphics2D g, Polygon polygon, double scale, Color color,BufferedImage im,Transform t) {
            if(im!=null){
               // g.translate(0, -im.getHeight());
                g.drawImage(im, null, 0, 0);
            }{
		Vector2[] vertices = polygon.getVertices();
		int l = vertices.length;
		
		// create the awt polygon
		Path2D.Double p = new Path2D.Double();
		p.moveTo(vertices[0].x * scale, vertices[0].y * scale);
		for (int i = 1; i < l; i++) {
			p.lineTo(vertices[i].x * scale, vertices[i].y * scale);
		}
		p.closePath();
		
		// fill the shape
		g.setColor(color);
		g.fill(p);
		// draw the outline
		g.setColor(getOutlineColor(color));
		g.draw(p);
            }

	}

	private static final Color getOutlineColor(Color color) {
		Color oc = color.darker();
		return new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), color.getAlpha());
	}
}