package engine.windows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import engine.windows.Bullet;
import java.util.ArrayList;

public class Plane {
    public String path;
    Graphics g;
    public int x;
    public boolean isEnemy =  false;
    private ArrayList<Bullet> bullets = new ArrayList<>();

    public boolean needToUpdate = false;
    public int y;
    private int bulletSpeed = 5;
    private BufferedImage planeImage;
    private boolean isAlive = true;

    public Plane(String path, Graphics g, int x, int y,boolean isEnemy ) {
        this.path = path;
        this.g = g;
        this.x = x;
        this.y = y;
        this.isEnemy = isEnemy;
    }

    public void drawPlane() {
        if (g != null) {
            if(this.planeImage == null) {
                try {
                    this.planeImage = ImageIO.read(new File(path));
                    if(this.isEnemy) this.planeImage = Tools.rotateImage(this.planeImage,180);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            g.drawImage(this.planeImage, this.x, this.y, null);
        }
    }

    public void moveTo(Point point) {
        this.x = point.x;
        this.y = point.y;
        this.needToUpdate = true;
    }

    public void makePlane() {
        if(this.isAlive) this.drawPlane();
        this.needToUpdate = false;
    }

    public void MoveX(int x) {
        this.x += x;
        if(this.x >= 400)
            this.x = 400;
        if(this.x<=10) this.x = 10;
        this.needToUpdate = true;
    }

    public void MoveY(int y) {
        this.y += y;
        if(this.y >= 700)
            this.y = 700;
        if(this.y<=10) this.y = 10;
        this.needToUpdate = true;
    }

    public void shoot(){
      //  System.out.println("Bắn tùm lum ! at x: " + this.x + " y: " + this.y);
        Bullet bullet = null;
        if(!this.isEnemy)
             bullet = new Bullet(this.x + 30, this.y - 40, bulletSpeed, "Resources/BULLET.png", this.g);
        else
             bullet = new Bullet(this.x + 30, this.y + 40, bulletSpeed, "Resources/BULLET.png", this.g);

        this.bullets.add(bullet);
    }

    public void enemyShoot(){

    }

    public void speedUp(int x){
        this.bulletSpeed += x;
        System.out.println("Speed up: " + this.bulletSpeed);
    }

    public Point getPlanePoint(){
        return new Point(this.x, this.y);
    }

    public void disappear(){
        this.isAlive = false;
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    public void removeBullet(int i){
        this.bullets.remove(i);
    }
    public ArrayList<Bullet> getBullets() {
        return this.bullets;
    }

    public void removeBullets(int i){
        this.bullets.remove(i);
    }
}
