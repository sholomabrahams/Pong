package spring2020.mcon364.pong;

import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.EOFException;
import java.io.IOException;
import java.util.Random;

public class PongWindow extends JFrame {
    private final Dimension WINDOW_SIZE = new Dimension(600, 375);
    private final int BALL_DIAMETER = 50, PADDLE_WIDTH = 20, PADDLE_HEIGHT = 100, PADDLE_MARGIN = 5;
    private final int BOTTOM = WINDOW_SIZE.height - PADDLE_HEIGHT;
    private Point ball = new Point(WINDOW_SIZE.width / 2 - BALL_DIAMETER / 2,WINDOW_SIZE.height / 2 - BALL_DIAMETER / 2),
            paddleLeft = new Point(PADDLE_MARGIN, BOTTOM / 2),
            paddleRight = new Point(WINDOW_SIZE.width - PADDLE_WIDTH - PADDLE_MARGIN,BOTTOM / 2);
    private int ball_dx, ball_dy = 2;
    private Timer ballUpdater;
    private final PongConnection CONNECTION;
    private final Random RANDOM = new Random();
    private final boolean SERVER;

    //Constructor for server window
    public PongWindow(int port) {
        SERVER = true;
        CONNECTION = new Server(port);
        initConnection();
    }

    //Constructor for client window
    public PongWindow(String host, int port) {
        SERVER = false;
        CONNECTION = new Client(host, port);
        initConnection();
    }

    private void startGame() {
        ball_dx = RANDOM.nextBoolean() ? 3 : -3;
        //The server determines the initial direction of the ball
        if (SERVER) {
            CONNECTION.sendData(new Payload(Codes.REVERSE, new Point(ball_dx, ball_dy)));
        }
        ballUpdater = new Timer(35, actionEvent -> {
            ball.translate(ball_dx, ball_dy);
            if (!SERVER && ball.x >= WINDOW_SIZE.width - BALL_DIAMETER) { //If the right player loses
                repaint();
                reverseBall();
                CONNECTION.sendData(new Payload(Codes.GAME_OVER));
                gameOver(false);
            } else if (SERVER && ball.x <= 0) { //If the left player loses
                repaint();
                reverseBall();
                CONNECTION.sendData(new Payload(Codes.GAME_OVER));
                gameOver(true);
            } else if ((ball.x <= PADDLE_MARGIN + PADDLE_WIDTH && ball.y + BALL_DIAMETER >= paddleLeft.y && ball.y <= paddleLeft.y + PADDLE_HEIGHT) ||
                    (ball.x + BALL_DIAMETER >= WINDOW_SIZE.width - PADDLE_MARGIN - PADDLE_WIDTH && ball.y + BALL_DIAMETER >= paddleRight.y && ball.y <= paddleRight.y + PADDLE_HEIGHT)) {
                //If ball hits paddle
                reverseBall();
            }
            if (ball.y >= WINDOW_SIZE.height - BALL_DIAMETER || ball.y <= 0) { //If ball hits top or bottom
                ball_dy = -ball_dy;
            }
            repaint();
        });
        ballUpdater.start();
        GamePanel gamePanel = new GamePanel();
        setContentPane(gamePanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }

    private void initConnection() {
        while (true) {
            try {
                CONNECTION.connect();
                CONNECTION.getStreams();
                startGame();
                processConnection();
            } catch (EOFException e) {
                System.exit(-1);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                CONNECTION.closeConnection();
            }
        }
    }

    private void processConnection() throws IOException, ClassNotFoundException {
        Payload data;
        while (true) {
            data = CONNECTION.getInput();
            processInput(data);
        }
    }

    private void processInput(Payload input) {
        switch (input.CODE) {
            case GAME_OVER:
                gameOver(!SERVER);
                break;
            case BALL:
                ball = input.DATA;
                break;
            case PADDLE:
                if (SERVER) {
                    paddleRight = input.DATA;
                } else {
                    paddleLeft = input.DATA;
                }
                break;
            case REVERSE:
                ball_dx = input.DATA.x;
                ball_dy = input.DATA.y;
                break;
        }
    }

    private void reverseBall() {
        int y = RANDOM.nextInt(3) + 4;
        ball_dy = ball_dy > 1 ? y: -y;
        ball_dx = -ball_dx;
        CONNECTION.sendData(new Payload(Codes.REVERSE, new Point(ball_dx, ball_dy)));
        CONNECTION.sendData(new Payload(Codes.BALL, new Point(ball)));
    }

    private void gameOver(boolean side) {
        ballUpdater.stop();
        StringBuilder str = new StringBuilder("Thanks for playing!\nThe ");
        str.append(side ? "right" : "left");
        str.append(" player won.");
        JOptionPane.showMessageDialog(
                this,
                str,
                "Game Over",
                JOptionPane.PLAIN_MESSAGE
        );
        CONNECTION.closeConnection();
    }

    private class GamePanel extends JPanel {
        {
            setPreferredSize(WINDOW_SIZE);
            setFocusable(true);
            setBackground(new Color(0x3279a0));
            if (SERVER) {
                addMouseWheelListener(mouseWheelEvent -> {
                    movePaddle(paddleLeft, 5 * mouseWheelEvent.getWheelRotation());
                    CONNECTION.sendData(new Payload(Codes.PADDLE, new Point(paddleLeft)));
                });
            } else {
                addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent keyEvent) {
                    }

                    @Override
                    public void keyReleased(KeyEvent keyEvent) {
                    }

                    @Override
                    public void keyPressed(KeyEvent keyEvent) {
                        if (keyEvent.getKeyCode() == KeyEvent.VK_UP) { //Up arrow key
                            movePaddle(paddleRight, -7);
                        } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) { //Down arrow key
                            movePaddle(paddleRight, 7);
                        }
                        CONNECTION.sendData(new Payload(Codes.PADDLE, new Point(paddleRight)));
                    }
                });
            }
        }

        private void movePaddle(Point paddle, int dy) {
            int newY = paddle.y + dy;
            if (newY <= 0) { //Prevent paddle from leaving the top of the window
                paddle.y = 0;
            } else if (newY >= BOTTOM) { //Prevent paddle from leaving the bottom of the window
                paddle.y = BOTTOM;
            } else {
                paddle.translate(0, dy);
            }
            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.fillOval(ball.x,ball.y, BALL_DIAMETER, BALL_DIAMETER);
            g.setColor(new Color(0xeeeeee));
            g.fillRect(paddleLeft.x, paddleLeft.y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.fillRect(paddleRight.x, paddleRight.y, PADDLE_WIDTH, PADDLE_HEIGHT);
        }
    }
}