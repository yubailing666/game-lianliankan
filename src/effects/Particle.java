package effects;

import java.awt.*;

public class Particle {
    public float x, y;
    public float vx, vy;
    public float life;
    public Color color;
    public float size;

    public void update(){
        x += vx;
        y += vy;
        life -= 0.02f;
    }

    public void draw(Graphics2D g){
        if(life <=  0) return;
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(255 * life))) ;
    }
}
