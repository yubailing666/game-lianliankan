package effects;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ShardParticle extends Particle{
    public float rotation;
    public float rotationSpeed;
    public float gravity;

    @Override
    public void update(){
        x += vx;
        y += vy;
        vy += gravity;
        rotation += rotationSpeed;
        life -= 0.015f;
    }

    @Override
    public void draw(Graphics2D g){
        if(life <=  0) return;
        AffineTransform transform = new AffineTransform();
        transform.translate(x + size / 2, y + size / 2);
        transform.rotate(rotation);
        transform.translate(-size / 2, -size / 2);
        g.setTransform(transform);
        g.setColor(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                (int)(life * 255)
        ));
        int[] xPoints = {0, (int) size, (int)(size / 2)};
        int[] yPoints = {0, 0, (int)size};
        g.fillPolygon(xPoints, yPoints, 3);
    }

}
