package spring2020.mcon364.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PongWindow extends JFrame {
    private final Dimension WINDOW_SIZE = new Dimension(900, 650);
    private final int BALL_DIAMETER = 50, PADDLE_WIDTH = 20, PADDLE_HEIGHT = 100, PADDLE_MARGIN = 5;
    final int BOTTOM = WINDOW_SIZE.height - PADDLE_HEIGHT;
    private Point ball = new Point(WINDOW_SIZE.width / 2 - BALL_DIAMETER / 2,WINDOW_SIZE.height / 2 - BALL_DIAMETER / 2),
            paddleLeft = new Point(PADDLE_MARGIN, BOTTOM / 2),
            paddleRight = new Point(WINDOW_SIZE.width - PADDLE_WIDTH - PADDLE_MARGIN,BOTTOM / 2);
    private int ball_dx = 7, ball_dy = 2;
    private Timer ballUpdater;

    public PongWindow() {
        ballUpdater = new Timer(35, actionEvent -> {
            ball.translate(ball_dx, ball_dy);
            if (ball.x >= WINDOW_SIZE.width - BALL_DIAMETER || ball.x <= 0) {
                repaint();
                gameOver(ball.x > WINDOW_SIZE.width / 2);
            } else if ((ball.x <= PADDLE_MARGIN + PADDLE_WIDTH && ball.y + BALL_DIAMETER >= paddleLeft.y && ball.y <= paddleLeft.y + PADDLE_HEIGHT) ||
                    (ball.x + BALL_DIAMETER >= WINDOW_SIZE.width - PADDLE_MARGIN - PADDLE_WIDTH && ball.y + BALL_DIAMETER >= paddleRight.y && ball.y <= paddleRight.y + PADDLE_HEIGHT)) reverseBall();
            else if (ball.y >= WINDOW_SIZE.height - BALL_DIAMETER || ball.y <= 0) ball_dy = -ball_dy;
            repaint();
        });
        ballUpdater.start();
        GamePanel gamePanel = new GamePanel();
        setContentPane(gamePanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }

    private void reverseBall() {
        Random rand = new Random();
        int y = rand.nextInt(6) + 4;
        ball_dy = ball_dy > 1 ? y: -y;
        ball_dx = -ball_dx;
    }

    private void gameOver(boolean side) {
        ballUpdater.stop();
        StringBuilder str = new StringBuilder("Thanks for playing!\nThe ");
        str.append(side ? "left" : "right");
        str.append(" player won.");
        JOptionPane.showMessageDialog(
                this,
                str,
                "Game Over",
                JOptionPane.PLAIN_MESSAGE
        );
        System.exit(0);
    }

    private class GamePanel extends JPanel {
        {
            setPreferredSize(WINDOW_SIZE);
            setFocusable(true);
            setBackground(new Color(50, 120, 160));
            addMouseWheelListener(mouseWheelEvent -> movePaddle(paddleLeft, 5 * mouseWheelEvent.getWheelRotation()));
            addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {}
                @Override
                public void keyReleased(KeyEvent keyEvent) {}
                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == 38) movePaddle(paddleRight, -7);
                    else if (keyEvent.getKeyCode() == 40) movePaddle(paddleRight, 7);
                }
            });
        }

        private void movePaddle(Point paddle, int dy) {
            int newY = paddle.y + dy;
            if (newY <= 0) paddle.y = 0;
            else if (newY >= BOTTOM) paddle.y = BOTTOM;
            else paddle.translate(0, dy);
            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.fillOval(ball.x,ball.y, BALL_DIAMETER, BALL_DIAMETER);
            g.setColor(new Color(240, 240, 240));
            g.fillRect(paddleLeft.x, paddleLeft.y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.fillRect(paddleRight.x, paddleRight.y, PADDLE_WIDTH, PADDLE_HEIGHT);
        }
    }
}