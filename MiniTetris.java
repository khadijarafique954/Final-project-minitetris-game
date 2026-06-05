package minitetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MiniTetris extends JFrame {

    final int ROWS = 20;
    final int COLS = 10;
    final int CELL = 25;

    int[][] grid = new int[ROWS][COLS];
    Timer timer;
    int score = 0;

    int currentX = 0;
    int currentY = 4;

    int[][][] shapes = {
            {{1,1,1,1}},          // I
            {{1,1},{1,1}},        // O
            {{0,1,0},{1,1,1}},    // T
            {{1,0,0},{1,1,1}},    // L
            {{0,0,1},{1,1,1}}     // J
    };

    int[][] shape;
    int shapeId;

    public MiniTetris() {

        setTitle("FULL TETRIS GAME");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);

        spawnShape();

        timer = new Timer(400, e -> gameLoop());
        timer.start();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (canMove(currentX, currentY - 1)) currentY--;
                }

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (canMove(currentX, currentY + 1)) currentY++;
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    moveDown();
                }

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    rotateShape();
                }

                repaint();
            }
        });

        setVisible(true);
    }

    void spawnShape() {
        Random r = new Random();
        shapeId = r.nextInt(shapes.length);
        shape = shapes[shapeId];
        currentX = 0;
        currentY = 3;
    }

    boolean canMove(int x, int y) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {

                if (shape[i][j] == 1) {

                    int newX = x + i;
                    int newY = y + j;

                    if (newY < 0 || newY >= COLS || newX >= ROWS)
                        return false;

                    if (newX >= 0 && grid[newX][newY] == 1)
                        return false;
                }
            }
        }
        return true;
    }

    void rotateShape() {

        int rows = shape.length;
        int cols = shape[0].length;

        int[][] rotated = new int[cols][rows];

        // rotate clockwise
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = shape[i][j];
            }
        }

        int oldX = currentX;
        int oldY = currentY;

        shape = rotated;

        if (!canMove(currentX, currentY)) {
            shape = rotateBack(rotated);
            currentX = oldX;
            currentY = oldY;
        }
    }

    int[][] rotateBack(int[][] rotated) {

        int rows = rotated.length;
        int cols = rotated[0].length;

        int[][] original = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                original[cols - 1 - j][i] = rotated[i][j];
            }
        }
        return original;
    }

    void moveDown() {

        if (canMove(currentX + 1, currentY)) {
            currentX++;
        } else {
            fixShape();
            clearLines();
            spawnShape();

            if (!canMove(currentX, currentY)) {
                JOptionPane.showMessageDialog(this, "GAME OVER! Score: " + score);
                System.exit(0);
            }
        }
    }

    void fixShape() {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {

                if (shape[i][j] == 1) {
                    grid[currentX + i][currentY + j] = 1;
                }
            }
        }
    }

    void clearLines() {

        for (int i = 0; i < ROWS; i++) {

            boolean full = true;

            for (int j = 0; j < COLS; j++) {
                if (grid[i][j] == 0) {
                    full = false;
                    break;
                }
            }

            if (full) {
                score += 100;

                for (int k = i; k > 0; k--) {
                    System.arraycopy(grid[k - 1], 0, grid[k], 0, COLS);
                }

                grid[0] = new int[COLS];
            }
        }
    }

    void gameLoop() {
        moveDown();
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {

                if (grid[i][j] == 1) {
                    g.setColor(Color.CYAN);
                    g.fillRect(50 + j * CELL, 70 + i * CELL, CELL, CELL);
                }

                g.setColor(Color.GRAY);
                g.drawRect(50 + j * CELL, 70 + i * CELL, CELL, CELL);
            }
        }

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {

                if (shape[i][j] == 1) {
                    g.setColor(Color.ORANGE);
                    g.fillRect(50 + (currentY + j) * CELL,
                            70 + (currentX + i) * CELL,
                            CELL, CELL);
                }
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 50, 50);
    }

    public static void main(String[] args) {
        new MiniTetris();
    }
}