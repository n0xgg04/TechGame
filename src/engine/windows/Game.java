package engine.windows;

import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import engine.windows.Plane;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Game extends Frame{
    public Graphics g;
    public Plane plane, enemy;
    ArrayList<Plane> enemiesPlane = new ArrayList<>();
    public int x = 190;
    public int y = 700;
    private int userScores = 0;
    private int maxEnemyAccept = 1;
    public boolean bgSetupComplete = false;
    private boolean started = false;

    private boolean isNotifiedNotStarted = false;
    private BufferedImage bgImageBuffered, plane1ImageBuffered;
    private long lastBulletMove2;


    public Game(Graphics g){
        this.g = g;
        System.out.println("Game created");
        //set color to black
        g.setColor(Color.BLACK);
        g.drawString("Press ^ key to start", 150, 420);
    }

    public boolean isStarted() {
        return this.started;
    }
    public void setStarted(){
        this.started = true;
        this.bgSetupComplete = false;
    }


    public void setFrameG(Graphics g){
        this.g = g;
    }

    public void setBackGround(Graphics g,String path){
        if(this.bgImageBuffered == null) {
            try {
                this.bgImageBuffered = ImageIO.read(new File(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        g.drawImage(this.bgImageBuffered, 0, 0, null);
        this.bgSetupComplete = true;
    }

    private long lastBulletMoveTime = System.currentTimeMillis();
    private long lastBulletMoveTime2 = System.currentTimeMillis();
    public void updateBullet(){
        //! Bullet logic
        if (this.plane != null) {
            ArrayList<Bullet> bullets = this.plane.getBullets();
            if (bullets.size() > 0) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastBulletMoveTime >= 10) { // move bullet every 0.5 seconds
                    lastBulletMoveTime = currentTime;
                    for (int i = 0; i < bullets.size(); i++) {
                        bullets.get(i).MoveY(bullets.get(i).getSpeed());
                        this.plane.needToUpdate = true;
                        if (bullets.get(i).y <= 0) {
                            bullets.get(i).disappear();
                            this.plane.removeBullets(i);
                        }
                    }
                }
            }
        }


        if (this.enemiesPlane.size() != 0) {
            for(int j = 0; j < enemiesPlane.size(); j++){
                Plane ePlane = this.enemiesPlane.get(j);
                ArrayList<Bullet> bullets = ePlane.getBullets();
                if (bullets.size() > 0) {
                        for (int i = 0; i < bullets.size(); i++) {
                            long currentTime = System.currentTimeMillis();
                            if(currentTime - bullets.get(i).lastBulletMoveTime >= 10) {
                                this.setCurrentTimeForBullet(j,i,currentTime);
                                bullets.get(i).MoveY(-bullets.get(i).getSpeed());
                                ePlane.needToUpdate = true;
                                if (bullets.get(i).y >= 760) {
                                    bullets.get(i).disappear();
                                    bullets.remove(i);
                                }
                            }
                        }
                    }
                }
            }
        }

     public void setCurrentTimeForBullet(int planeId, int bulletId, long timestamp){
        this.enemiesPlane.get(planeId).getBullets().get(bulletId).lastBulletMoveTime = timestamp;
     }
    public void run(){
        if(!this.started) {
            if (!isNotifiedNotStarted) {
                System.out.println("Hasn't started!! Wait user click to screen");
                isNotifiedNotStarted = true;
            }
        }

        if(!this.started){
            //System.out.println("Hasn't started!! Wait user click to screen");
            //set color for string
            return;
        }
        if(!this.bgSetupComplete) {
            setBackGround(g, "Resources/Background.png");
            System.out.println("Background setted");
        }



        if(this.plane == null){
             this.plane = new Plane("Resources/PLANE1.png", g, this.x, this.y, false);
             this.plane.makePlane();
        }else{
            if(!this.plane.needToUpdate){

            }else {
                setBackGround(g, "Resources/Background.png");
                this.plane.makePlane();
                this.updateBulletPosition(this.plane);
                for(int i = 0; i < enemiesPlane.size(); i++){
                    enemiesPlane.get(i).makePlane();
                    this.updateBulletPosition(enemiesPlane.get(i));
                }

            }
        }
        //!Point
        if(this.isStarted()){
            g.setColor(Color.WHITE);
            g.drawString("Scores : " + this.userScores , 20, 20);
        }
    }

    private void youLose(){
        //fill windows to white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 800, 800);
        g.setColor(Color.RED);
        g.drawString("You lose", 200, 400);
        g.drawString("Press ^ to restart", 160, 420);
        this.started = false;
        this.plane = null;
        this.enemiesPlane = new ArrayList<>();
        //this.enemy = null;
    }

    private void updateBulletPosition(Plane plane){
        ArrayList<Bullet> bulletList = plane.getBullets();
        for (int i = 0; i < bulletList.size(); i++) {
            Bullet bullet = bulletList.get(i);
            if (bullet.isAlive) {
                g.drawImage(bullet.bulletImage, bullet.x, bullet.y, null);
                //!Bắn tùm lum trúng địch
                if(!plane.isEnemy){
                    for(int j = 0; j < enemiesPlane.size(); j++){
                        Plane enemy = enemiesPlane.get(j);
                        Point enermyPoint = enemy.getPlanePoint();
                        if(Math.abs(bullet.x - enermyPoint.x) <= 50 && Math.abs(bullet.y - enermyPoint.y) <= 40){
                             System.out.println("Bắn tùm lum trúng địch thứ " + j);
                            if(enemy.isAlive()) enemy.disappear();
                            if(enemy.isAlive()) bullet.isAlive = false;
                            this.enemiesPlane.remove(j);
                        }
                    }
                }else{
                    Point myplanePoint = this.plane.getPlanePoint();
                    if(Math.abs(bullet.x - myplanePoint.x) <= 50 && Math.abs(bullet.y - myplanePoint.y) <= 40){
                        System.out.println("Thua mẹ rồi");
                        if(this.plane.isAlive()) this.plane.disappear();
                        if(this.plane.isAlive()) bullet.isAlive = false;
                        this.youLose();
                    }
                }
                //System.out.println("Bullet at x: " + bullet.x + " y: " + bullet.y);
            } else {
                plane.removeBullet(i);
            }
        }
    }

    public void makeRandomEnemy(){
        if(!this.isStarted()) return;
        if(this.enemiesPlane.size() <= this.maxEnemyAccept){
            Random rand = new Random();
            int randomX = rand.nextInt(200)+100;
            int randomY = rand.nextInt(200) + 50;
            Random random = new Random();
            int rn = random.nextInt(3)+1;
            String pathEN = "Resources/PLANE"+rn+".png";
            Plane emPlane = new Plane(pathEN, g, randomX, randomY, true);
            emPlane.makePlane();
            enemiesPlane.add(emPlane);
        }
        //get random number from 50 to 700
    }
}