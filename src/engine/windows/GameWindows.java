package engine.windows;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import engine.windows.Bullet;
import engine.windows.Plane;

public class GameWindows extends Frame implements Runnable, KeyListener {
    private Game gameG;
    private BufferedImage bufferImg;
    private Graphics bufferG;
    private ScheduledExecutorService executorService;
    private int spaceX = 20;

    public GameWindows() {
        super();
        this.setTitle("Mao phắc");
        this.setSize(480, 800);
        this.setResizable(false);
        this.addKeyListener(this);
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });
        this.bufferImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.bufferG = bufferImg.getGraphics();
        this.gameG = new Game(bufferG);
        this.addMouseListener(new MyMouseListener());
    }

    public class MyMouseListener extends MouseAdapter {

    public void mouseClicked(MouseEvent e) {
        Point point = e.getPoint();
        if(gameG.plane != null) {
            gameG.plane.moveTo(point);
        }
        System.out.println("Mouse clicked at x: " + point.x + " y: " + point.y);
    }
}

    @Override
    public void update(Graphics g) {
        super.update(g);
        //paint(g);
    }

    @Override
    public void paint(Graphics g) {
        try {
            this.gameG.run();
        }catch(Exception ie){

        }
        g.drawImage(bufferImg, 0, 0, null);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(!this.gameG.isStarted() && keyCode != KeyEvent.VK_UP) return;
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    if(!this.gameG.isStarted()) {
                        this.gameG.setStarted();
                        System.out.println("Game started");
                        break;
                    }
                    this.gameG.plane.MoveY(-this.spaceX);
                    break;

                case KeyEvent.VK_DOWN:
                    this.gameG.plane.MoveY(this.spaceX);
                    break;
                case KeyEvent.VK_LEFT:
                    this.gameG.plane.MoveX(-this.spaceX);
                    break;
                case KeyEvent.VK_RIGHT:
                    this.gameG.plane.MoveX(this.spaceX);
                    break;

                case KeyEvent.VK_S:
                    this.gameG.plane.speedUp(1);
                    break;

                case KeyEvent.VK_SPACE:
                    this.gameG.plane.shoot();
                    for(int i = 0; i < this.gameG.enemiesPlane.size(); i++){
                        Plane ePlane = this.gameG.enemiesPlane.get(i);
                        if(ePlane.isAlive()) ePlane.shoot();
                    }
                    break;
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
       // System.out.println("Key typed: " + keyChar);
    }

    @Override
    public void run() {
        while (true) {
            updateGame();
            repaint();
        }
    }

    private long lastBulletMoveTime = System.currentTimeMillis();

    private void updateGame() {
        if(this.gameG.isStarted()) this.gameG.makeRandomEnemy();
        this.gameG.updateBullet();
    }


    public void start() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleAtFixedRate(this, 0, 16, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        GameWindows game = new GameWindows();
        game.setVisible(true);
        game.start();
    }
}
