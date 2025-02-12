package Week2;

import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.io.*;
import java.util.*;

record Spot(long x, long y) {}

class Robot {
    public long r;
    public long c;

    public long rAdd;
    public long cAdd;

    public Robot(long r, long c, long rAdd, long cAdd) {
        this.r = r;
        this.c = c;
        this.rAdd = rAdd;
        this.cAdd = cAdd;
    }

    public void moveForward(long numTimes) {
        for (long i = 0; i < numTimes; i++) {
        	r += rAdd;
            c += cAdd;
            r = ((r % Day14Swing.NUM_ROWS) + Day14Swing.NUM_ROWS) % Day14Swing.NUM_ROWS;
            c = ((c % Day14Swing.NUM_COLS) + Day14Swing.NUM_COLS) % Day14Swing.NUM_COLS;
        }
    }
}

public class Day14Swing {
    public static final String filePath = "InputText.txt";
    public static final File input = new File(filePath);
    public static final int NUM_ROWS = 103;
    public static final int NUM_COLS = 101;
    public static final long NUM_SECONDS = 100;

    public static java.util.List<Robot> bots;
    private static long iteration = 0;
    private static final long FULL_CYCLE = 103 * 101;

    public static void main(String[] args) {
        bots = new ArrayList<>();
        processBots();
        
        for (var bot : bots) {
        	
        	bot.moveForward(0);
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Robot Grid");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            GridPanel gridPanel = new GridPanel();
            frame.add(gridPanel, BorderLayout.CENTER);

            JLabel iterationLabel = new JLabel("Iteration: 0", SwingConstants.CENTER);
            iterationLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            frame.add(iterationLabel, BorderLayout.SOUTH);

            new Timer(1, e -> {
                iteration++;
                for (var bot : bots) bot.moveForward(1);
                iterationLabel.setText("Iteration: " + iteration);
                gridPanel.repaint();
            }).start();

            frame.setVisible(true);
        });
    }

    private static Robot processBot(String line) {
        String[] vectors = line.split(" ");

        String[] position = vectors[0].split("=")[1].split(",");
        long r = Long.parseLong(position[1]);
        long c = Long.parseLong(position[0]);

        String[] directions = vectors[1].split("=")[1].split(",");
        long rAdd = Long.parseLong(directions[1]);
        long cAdd = Long.parseLong(directions[0]);

        return new Robot(r, c, rAdd, cAdd);
    }

    private static void processBots() {
        try (Scanner scan = new Scanner(input)) {
            while (scan.hasNext()) {
                bots.add(processBot(scan.nextLine()));
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found. Exiting now.");
            System.exit(1);
        }
    }

    static class GridPanel extends JPanel {
        private static final int CELL_SIZE = 7;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= NUM_ROWS; i++) {
                g.drawLine(0, i * CELL_SIZE, NUM_COLS * CELL_SIZE, i * CELL_SIZE);
            }
            for (int i = 0; i <= NUM_COLS; i++) {
                g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, NUM_ROWS * CELL_SIZE);
            }

            g.setColor(Color.RED);
            for (Robot bot : bots) {
                int x = (int) bot.c;
                int y = (int) bot.r;
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(NUM_COLS * CELL_SIZE, NUM_ROWS * CELL_SIZE);
        }
    }
}
