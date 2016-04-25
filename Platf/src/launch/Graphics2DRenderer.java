package launch;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Vector2;


public final class Graphics2DRenderer {

	public static final void render(Graphics2D g, Shape shape, double scale, Color color) {
		// no-op
		if (shape == null) return;
		
		// just default the color
		if (color == null) color = Color.ORANGE;
		
		if (shape instanceof Circle) {
			Graphics2DRenderer.render(g, (Circle)shape, scale, color);
		} else if (shape instanceof Polygon) {
			Graphics2DRenderer.render(g, (Polygon)shape, scale, color);
		} else if (shape instanceof Segment) {
			Graphics2DRenderer.render(g, (Segment)shape, scale, color);
		} else if (shape instanceof Capsule) {
			Graphics2DRenderer.render(g, (Capsule)shape, scale, color);
		} else if (shape instanceof Ellipse) {
			Graphics2DRenderer.render(g, (Ellipse)shape, scale, color);
		} else if (shape instanceof Slice) {
			Graphics2DRenderer.render(g, (Slice)shape, scale, color);
		} else if (shape instanceof HalfEllipse) {
			Graphics2DRenderer.render(g, (HalfEllipse)shape, scale, color);
		} else {
			// unknown shape
		}
	}

	public static final void render(Graphics2D g, Polygon polygon, double scale, Color color) {
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

	private static final Color getOutlineColor(Color color) {
		Color oc = color.darker();
		return new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), color.getAlpha());
	}
}