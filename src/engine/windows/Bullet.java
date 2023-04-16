package engine.windows;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.*;
import java.io.IOException;
import java.awt.image.BufferedImage;
import engine.windows.Tools;

public class Bullet{
    public int x;
    public int y;
    private Graphics g;
    private int speed;
    public boolean isAlive;
    public BufferedImage bulletImage;
    public long lastBulletMoveTime = System.currentTimeMillis();
    public Bullet(int x, int y, int speed, String path, Graphics g) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.isAlive = true;
        try {
            this.bulletImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        g.drawImage(this.bulletImage, this.x, this.y, null);
    }


    public void MoveY(int y) {
        this.y -= y;
    }

    public int getSpeed(){
        return this.speed;
    }

    public void disappear(){
        this.isAlive = false;
    }

}
