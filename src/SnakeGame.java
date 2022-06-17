import javax.swing.*;

public class SnakeGame extends JFrame {

    private SnakeGame() {
        setTitle("Snake!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        GameField gameField = new GameField();
        add(gameField);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String args[]) {
        new SnakeGame();
    }
}