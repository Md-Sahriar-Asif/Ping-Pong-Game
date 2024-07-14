package ping_pong_final;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

abstract class GameObject extends Rectangle {
    int xVelocity;
    int yVelocity;

    GameObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void setXDirection(int xDirection) {
        xVelocity = xDirection;
    }

    public void setYDirection(int yDirection) {
        yVelocity = yDirection;
    }

    public void move() {
        x = x + xVelocity;
        y = y + yVelocity;
    }
}

class Ball extends GameObject {
    Random random;
    int initialSpeed = 4;

    Ball(int x, int y, int width, int height) {
        super(x, y, width, height);
        random = new Random();
        int randomXDirection = random.nextInt(2) * 2 - 1;
        setXDirection(randomXDirection * initialSpeed);

        int randomYDirection = random.nextInt(2) * 2 - 1;
        setYDirection(randomYDirection * initialSpeed);
    }

    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillOval(x, y, width, height);
    }
}


class Paddle extends GameObject {

    int id;
    int yVelocity;
    int speed = 7;

    Paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id) {
        super(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
        this.id = id;
    }

    public void keyPressed(KeyEvent e) {
        switch (id) {
            case 1:
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    setYDirection(-speed);
                    move();
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    setYDirection(speed);
                    move();
                }
                break;
            case 2:
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    setYDirection(-speed);
                    move();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYDirection(speed);
                    move();
                }
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (id) {
            case 1:
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    setYDirection(0);
                    move();
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    setYDirection(0);
                    move();
                }
                break;
            case 2:
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    setYDirection(0);
                    move();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYDirection(0);
                    move();
                }
                break;
        }
    }

    public void setYDirection(int yDirection) {
        yVelocity = yDirection;
    }

    public void move() {
        y = y + yVelocity;
    }

    public void draw(Graphics g) {
        if (id == 1) {
            g.setColor(Color.magenta);
        } else {
            g.setColor(Color.cyan);
        }
        g.fillRect(x, y, width, height);
    }
}

class Score extends GameObject {

    static int GAME_WIDTH;
    static int GAME_HEIGHT;
    int player1;
    int player2;

    String player1Name;
    String player2Name;

    Score(int GAME_WIDTH, int GAME_HEIGHT, String player1Name, String player2Name) {
        super(0,0,0,0);
        Score.GAME_WIDTH = GAME_WIDTH;
        Score.GAME_HEIGHT = GAME_HEIGHT;
        player1 = 0;
        player2 = 0;

        this.player1Name = player1Name + " Score : ";
        this.player2Name = player2Name + " Score : ";

    }

    public void draw(Graphics g) {
        g.setColor(Color.yellow);
        g.setFont(new Font("Consolas", Font.ITALIC, 30));

        g.drawLine(GAME_WIDTH / 2, 0, GAME_WIDTH / 2, GAME_HEIGHT);

        g.drawString(player1Name + String.valueOf(player1 / 10) + String.valueOf(player1 % 10), (GAME_WIDTH / 2) - 400, 45);

        g.drawString(player2Name + String.valueOf(player2 / 10) + String.valueOf(player2 % 10), (GAME_WIDTH / 2) + 100, 45);

    }
}

class GameFrame extends JFrame {  

    GamePanel panel;
    JButton returnButton;

    GameFrame(boolean isComputerMode, String player1, String player2) {
        panel = new GamePanel(isComputerMode, player1, player2);
        
        returnButton = new JButton("Return to Menu");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                createGameMenu(); 
            }

            private void createGameMenu() { }
        });

        this.add(panel);
        this.add(returnButton, BorderLayout.SOUTH); 
        this.setTitle("Pong_Game");
        this.setResizable(false); 
        this.setBackground(Color.black); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        this.pack(); 
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }


    void setPlayerNames(String player1, String player2) {
       
        System.out.println("Player 1 Name: " + player1);
        System.out.println("Player 2 Name: " + player2);
    }

}

class GamePanel extends JPanel implements Runnable {      

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    private String player1Name;
    private String player2Name;

    public boolean isGameOver() {
        return score.player1 + score.player2 == 7;

    }

    private boolean isComputerMode; 

    GamePanel(boolean isComputerMode, String player1, String player2) 
    {
        this.isComputerMode = isComputerMode; 

        newPaddles();
        newBall();
        this.player1Name = player1;
        this.player2Name = player2;
        score = new Score(GAME_WIDTH, GAME_HEIGHT, player1Name, player2Name);
        this.setFocusable(true); 
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();
    }

    void setPlayerNames(String player1, String player2) {

        this.player1Name = player1;
        this.player2Name = player2;
    }

    // Other code...
    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt(GAME_HEIGHT - BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);  
    }

    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
        if (isGameOver()) {
            if (score.player1 > score.player2) {
                g.setFont(new Font("Consolas", Font.BOLD, 65));
                String gameOverText = "Game Over";
                String winnerText = "!!" + player1Name + " WINS!! "; 
                FontMetrics fm = g.getFontMetrics();
                int stringWidth = fm.stringWidth(gameOverText);
                int stringHeight = fm.getAscent();

                int squareWidth = stringWidth + 20;
                int squareHeight = stringHeight + 20;

                int x = (GAME_WIDTH - squareWidth) / 2;
                int y = (GAME_HEIGHT - squareHeight) / 2;

                g.setColor(Color.black);
                g.fillRect(x, y, squareWidth, squareHeight);

                g.setColor(Color.yellow);
                g.drawString(gameOverText, x + (squareWidth - stringWidth) / 2, y + (squareHeight - stringHeight) / 2 + fm.getAscent());
                g.drawString(winnerText, x + (squareWidth - fm.stringWidth(winnerText)) / 2, y + (squareHeight + fm.getAscent()) / 2 + 50); 
            } else if (score.player1 < score.player2) {
                g.setFont(new Font("Consolas", Font.BOLD, 65));
                String gameOverText = "Game Over";
                String winnerText = " !!" + player2Name + " WINS!! "; 
                FontMetrics fm = g.getFontMetrics();
                int stringWidth = fm.stringWidth(gameOverText);
                int stringHeight = fm.getAscent();

                int squareWidth = stringWidth + 20;
                int squareHeight = stringHeight + 20;

                int x = (GAME_WIDTH - squareWidth) / 2;
                int y = (GAME_HEIGHT - squareHeight) / 2;

                g.setColor(Color.black);
                g.fillRect(x, y, squareWidth, squareHeight);

                g.setColor(Color.yellow);
                g.drawString(gameOverText, x + (squareWidth - stringWidth) / 2, y + (squareHeight - stringHeight) / 2 + fm.getAscent());
                g.drawString(winnerText, x + (squareWidth - fm.stringWidth(winnerText)) / 2, y + (squareHeight + fm.getAscent()) / 2 + 50); 
            }

        }
    }

    public void move() {
        paddle1.move();  
        
        if (isComputerMode) {
           

            int ballCenterY = ball.y + ball.height / 2; 
            int paddleCenterY = paddle2.y + paddle2.height / 2; 

            int paddleYDiff = ballCenterY - paddleCenterY; 

           
            if (paddleYDiff > 0) {
                paddle2.setYDirection(Math.min(paddleYDiff, paddle2.speed)); 
            } else if (paddleYDiff < 0) {
                paddle2.setYDirection(Math.max(paddleYDiff, -paddle2.speed)); 
            } else {
                paddle2.setYDirection(0); 
            }

            paddle2.move(); 
        } else {
            paddle2.move(); 
        }
        ball.move();
    }

    public void checkCollision() {
        
        if (ball.y <= 0) {
            ball.setYDirection(-ball.yVelocity);
        }
        if (ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        
        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; 
            if (ball.yVelocity > 0) {
                ball.yVelocity++; 
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if (ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;
            if (ball.yVelocity > 0) {
                ball.yVelocity++; 
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        if (paddle1.y <= 0) {
            paddle1.y = 0;
        }
        if (paddle1.y >= (GAME_HEIGHT - PADDLE_HEIGHT)) {
            paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }
        if (paddle2.y <= 0) {
            paddle2.y = 0;
        }
        if (paddle2.y >= (GAME_HEIGHT - PADDLE_HEIGHT)) {
            paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }

        if (ball.x <= 0) {
            score.player2++;
            newPaddles();
            newBall();
            System.out.println("Player 2: " + score.player2);
        }
        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newPaddles();
            newBall();
            System.out.println("Player 1: " + score.player1);
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }
            if (isGameOver()) {
                System.out.println("Game Over!");
                if (score.player1 > score.player2) {
                    System.out.println("Player 1 wins!");
                } else {
                    System.out.println("Player 2 wins!");
                }
                break;
            }
        }
    }

    public class AL extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
}

public class Ping_pong_final {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGameMenu();
            }
        });
    }

    private static void createGameMenu() {
        JFrame menuFrame = new JFrame("Game Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setLocation(500, 200);
        menuFrame.setSize(500, 400);
        menuFrame.setLayout(new GridLayout(4, 1));

        JButton startButton = new JButton("START GAME");
        JButton aboutButton = new JButton("ABOUT");
        JButton exitButton = new JButton("EXIT GAME");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openStartMenu(menuFrame);
            }
        });

        aboutButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(menuFrame, " !! Welcome To Ping Pong !!\n\n" +
                "GAME OBJECTIVE:\n" +
                "Score points by hitting the ball past your opponent's paddle.\n\n" +
                "CONTROLS:\n" +
                "Player 1 (W/S): Move paddle up with W key and down with S key.\n" +
                "Player 2 (Up/Down arrows): Move paddle up with up arrow and down with down arrow.\n\n" +
                "GAME MECHANICS:\n" +
                "a.The ball bounces off the paddles and the walls.\nb.There are total 7 rounds.\nc.The player who scores more points wins.\n\n" +
                "GAME MODES:\n" +
                "1. Play with Computer: Challenge the computer AI in a single-player match.\n" +
                "2. Two Player Mode: Play against a friend on the same keyboard.\n\n" +
                "CREATORS:\n" +
                "Faiza, Anika, and Reeman\n\n" +
                "We hope you enjoy playing!\nFeel free to send us your feedback for future improvements.Have Fun!\n\n"+
                "For feedback contact us through this mails:\n22201111@uap-bd.edu\n22201115@uap-bd.edu\n22201121@uap-bd.edu\n\n"
                
        );
    }
});


        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuFrame.add(startButton);
        menuFrame.add(aboutButton);
        menuFrame.add(exitButton);

        menuFrame.setVisible(true);
    }

    private static void openStartMenu(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "GAME MENU", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(5, 1));
        dialog.setLocationRelativeTo(parentFrame);

        JTextField player1Field = new JTextField();
        JButton playWithComputerButton = new JButton("Play with Computer");
        JButton twoPlayerModeButton = new JButton("Two Player Mode");
        JButton leaveLobbyButton = new JButton("Leave the Lobby");
        JButton submitButton = new JButton("Submit");

        playWithComputerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.getContentPane().removeAll();
                dialog.add(new JLabel("Enter Player 1 Name:"));
                dialog.add(player1Field);
                dialog.add(submitButton);
                dialog.revalidate();
                dialog.repaint();
                player1Field.requestFocus();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String player1 = player1Field.getText();
                dialog.dispose();
                JOptionPane.showMessageDialog(parentFrame, "\tGAME STARTED !\t");
                playGame(player1, "Computer", true);
            }
        });

        twoPlayerModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false); 
                openEnterNamesDialog("Two Player Mode", dialog);
            }
        });

        leaveLobbyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.add(playWithComputerButton);
        dialog.add(twoPlayerModeButton);
        dialog.add(leaveLobbyButton);
        dialog.setVisible(true);
    }

    private static void openEnterNamesDialog(String mode, JDialog parentDialog) {
        JDialog dialog = new JDialog(parentDialog, mode, true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(3, 1));
        dialog.setLocationRelativeTo(parentDialog);

        JTextField player1Field = new JTextField();
        JTextField player2Field = new JTextField();

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String player1 = player1Field.getText();
                String player2 = player2Field.getText();
                dialog.dispose();
                JOptionPane.showMessageDialog(parentDialog, "\tGAME STARTED !\t");
                playGame(player1, player2, false);
            }
        });

        dialog.add(new JLabel("Enter Player 1 Name:"));
        dialog.add(player1Field);
        dialog.add(new JLabel("Enter Player 2 Name:"));
        dialog.add(player2Field);
        dialog.add(submitButton);
        dialog.setLocationRelativeTo(parentDialog);

        dialog.setVisible(true);
    }

    private static void playGame(String player1, String player2, boolean isComputerMode) {

        GameFrame frame = new GameFrame(isComputerMode, player1, player2);
        frame.setPlayerNames(player1, player2);
    }
}
