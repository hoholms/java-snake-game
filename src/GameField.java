import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.Random;

public class GameField extends JPanel implements ActionListener {
    private Image dot;
    private Image apple;
    private int appleX;
    private int appleY;
    private final int[] x = new int[Config.ALL_DOTS];
    private final int[] y = new int[Config.ALL_DOTS];
    private int dots;
    private Timer timer;
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;
    private boolean changedDirection = false;
    private String sharpTurn = "";
    private boolean alreadyMoved = false;
    private boolean inGame = true;
    private int delay = Config.START_DELAY;
    private int score;

    public GameField() {
        setBackground(Color.GRAY);
        setPreferredSize(new Dimension(Config.WIDTH, Config.HEIGHT));
        loadImages();
        initGame();
        addKeyListener(new MyKeyListener());
        setFocusable(true);
    }

    public void initGame() {
        score = 0;
        dots = 3;
        for (int i = 0; i < dots; ++i) {
            x[i] = 48 - i * Config.DOT_SIZE;
            y[i] = 48;
        }
        timer = new Timer(Config.START_DELAY, this);
        timer.start();
        inGame = true;
        createApple();
    }

    public void createApple() {
        boolean check = false;
        while (!check) {
            check = true;
            appleX = new Random().nextInt(Config.WIDTH / Config.DOT_SIZE) * Config.DOT_SIZE;
            appleY = new Random().nextInt(Config.HEIGHT / Config.DOT_SIZE) * Config.DOT_SIZE;
            for (int i = dots; i > 0; --i) {
                if (appleX == x[i] && appleY == y[i]) {
                    check = false;
                    break;
                }
            }
        }
    }

    public void loadImages() {
        try {
            ImageIcon imgApple = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/apple.png")));
            apple = imgApple.getImage();
            ImageIcon imgDot = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/dot.png")));
            dot = imgDot.getImage();
        }
        catch (Exception ex) {
            System.out.println("Images cannot be loaded!");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font scoreFont = new Font("Calibri", Font.BOLD, 24);
        g.setColor(Color.BLACK);
        g.setFont(scoreFont);
        g.drawString("Score: " + score, 650, 24);
        if (inGame) {
            g.drawImage(apple, appleX, appleY, this);
            for (int i = 0; i < dots; ++i) {
                g.drawImage(dot, x[i], y[i], this);
            }
        } else {
            String gameOver = "Game Over!";
            String pressSpace = "(Press Spacebar to restart)";
            Font font = new Font("Calibri", Font.BOLD, 64);
            FontMetrics metricsGameOver = g.getFontMetrics(font);
            FontMetrics metricsSpacebar = g.getFontMetrics(scoreFont);
            g.setColor(Color.BLACK);
            g.setFont(font);
            g.drawString(gameOver, (Config.WIDTH - metricsGameOver.stringWidth(gameOver)) / 2,
                    ((Config.HEIGHT - metricsGameOver.getHeight()) / 2) + metricsGameOver.getAscent());

            g.setFont(scoreFont);
            g.drawString(pressSpace, (Config.WIDTH - metricsSpacebar.stringWidth(pressSpace)) / 2,
                    ((Config.HEIGHT - metricsSpacebar.getHeight()) / 2) + metricsSpacebar.getAscent() + 34);
        }
    }

    public void move() {
        for (int i = dots; i > 0; --i) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if (left) {
            x[0] -= Config.DOT_SIZE;
        }
        if (right) {
            x[0] += Config.DOT_SIZE;
        }
        if (up) {
            y[0] -= Config.DOT_SIZE;
        }
        if (down) {
            y[0] += Config.DOT_SIZE;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            ++dots;
            ++score;
            if (delay > Config.MIN_DELAY) {
                delay -= 10;
                timer.setDelay(delay);
            }
            createApple();
        }
    }

    public void checkCollisions() {
        for (int i = dots; i > 0; --i) {
            if (x[0] == x[i] && y[0] == y[i]) {
                inGame = false;
                break;
            }
        }

        if (x[0] >= Config.WIDTH)
            x[0] = 0;
        else if (x[0] < 0)
            x[0] = Config.WIDTH - Config.DOT_SIZE;

        if (y[0] >= Config.HEIGHT)
            y[0] = 0;
        else if (y[0] < 0)
            y[0] = Config.HEIGHT - Config.DOT_SIZE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollisions();
            if (changedDirection && !Objects.equals(sharpTurn, "")) {
                move();
                alreadyMoved = true;
                switch (sharpTurn) {
                    case ("left") -> {
                        if (up) up = false;
                        if (down) down = false;
                        left = true;
                    }
                    case ("right") -> {
                        if (up) up = false;
                        if (down) down = false;
                        right = true;
                    }
                    case ("up") -> {
                        if (left) left = false;
                        if (right) right = false;
                        up = true;
                    }
                    case ("down") -> {
                        if (left) left = false;
                        if (right) right = false;
                        down = true;
                    }
                }
                sharpTurn = "";
            }
            if (changedDirection) {
                changedDirection = false;
            }
            if (!alreadyMoved) move();
            if (alreadyMoved) alreadyMoved = false;
            checkApple();
            checkCollisions();
        }
        updateUI();
    }

    class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();

            if (!inGame && key == KeyEvent.VK_SPACE && !changedDirection) {
                timer.stop();
                initGame();
            }

            if (key == KeyEvent.VK_LEFT && !right && !changedDirection) {
                up = false;
                down = false;
                left = true;
                changedDirection = true;
            }
            else if (key == KeyEvent.VK_RIGHT && !left && !changedDirection) {
                up = false;
                down = false;
                right = true;
                changedDirection = true;
            }

            else if (key == KeyEvent.VK_UP && !down && !changedDirection) {
                left = false;
                right = false;
                up = true;
                changedDirection = true;
            }
            else if (key == KeyEvent.VK_DOWN && !up && !changedDirection) {
                left = false;
                right = false;
                down = true;
                changedDirection = true;
            }

            if ((left || right) && changedDirection && key == KeyEvent.VK_UP)
                sharpTurn = "up";
            if ((left || right) && changedDirection && key == KeyEvent.VK_DOWN)
                sharpTurn = "down";

            if ((up || down) && changedDirection && key == KeyEvent.VK_LEFT)
                sharpTurn = "left";
            if ((up || down) && changedDirection && key == KeyEvent.VK_RIGHT)
                sharpTurn = "right";
        }
    }

}
