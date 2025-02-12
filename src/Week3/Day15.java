package Week3;

import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.io.*;
import java.util.*;

class Box {
    public int boxId;
    public int r;
    public int c;

    public Box(int r, int c, int id) {
        this.r = r;
        this.c = c;
        this.boxId = id;
    }

    public void moveDir(int[] dir, int[][] mat) {
        int nextR = r + dir[0], nextC = c + dir[1];
        int otherC = nextC + 1;

        if (dir[1] == 1) {
            swap(mat, r, c + 1, nextR, otherC);
            swap(mat, r, c, nextR, nextC);
        } else {
            swap(mat, r, c, nextR, nextC);
            swap(mat, r, c + 1, nextR, otherC);
        }

        r = nextR;
        c = nextC;
    }

    private void swap(int[][] mat, int r, int c, int x, int y) {
        var temp = mat[r][c];
        mat[r][c] = mat[x][y];
        mat[x][y] = temp;
    }
}

public class Day15 {
    public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    public static HashMap<Integer, Box> boxMap;
    public static int[][] mat;
    public static int robotR = -1;
    public static int robotC = -1;
    public static Timer t;
    
    public static int movesIterator = 0;

    public static void main(String[] args) {
        Scanner scan = null;
        boxMap = new HashMap<>();
        try {
            scan = new Scanner(input);
        } catch (FileNotFoundException e) {
            System.err.println("File not found at path, exiting now.");
            System.exit(1);
        }

        String line = null;
        java.util.List<char[]> primMat = new ArrayList<>();
        while (!(line = scan.nextLine()).isEmpty()) {
            System.out.println("Reading in " + line);
            primMat.add(line.toCharArray());
        }

        StringBuilder moveSeq = new StringBuilder();
        while (scan.hasNext()) {
            moveSeq.append(scan.nextLine());
        }

        int m = primMat.size();
        int n = primMat.get(0).length;
        System.out.println(primMat.size() + " rows.");
        System.out.println(primMat.get(0).length + " cols");
        System.out.println(moveSeq.length() + " moves.");

        mat = new int[m][n * 2];

        int boxID = 0;
        
        

        for (int i = 0; i < m; i++) {
            char[] curr = primMat.get(i);
            for (int j = 0; j < n; j++) {

                int c = j * 2;
                int c2 = c + 1;
                char id = curr[j];

                if (id == '#') {
                    mat[i][c] = -1;
                    mat[i][c2] = -1;
                } else if (id == 'O') {
                    boxID++;

                    Box toAdd = new Box(i, c, boxID);
                    mat[i][c] = boxID;
                    mat[i][c2] = boxID;

                    boxMap.put(boxID, toAdd);
                } else if (id == '.') {
                    mat[i][c] = 0;
                    mat[i][c2] = 0;
                } else {
                    mat[i][c] = -2;
                    mat[i][c2] = 0;
                }
            }
        }
        for (var row : mat) System.out.println(Arrays.toString(row));
        int r = -1;
		int c = -1;
        n = mat[0].length;
        for (int i = 0; i < m && r == -1; i++) {
        	for (int j = 0; j < n; j++) {
        		
        		if (mat[i][j] == -2) {
        			r = i;
        			c  = j;
        			break;
        		}
        	}
        }
        robotR = r;
        robotC = c;
        GridVisualizer gVis = new GridVisualizer(mat);
        AnimationController controller = new AnimationController(moveSeq.toString(), gVis, 5);
        
        controller.addStateListener((state, error) -> {
            switch (state) {
                case FINISHED:
                    System.out.println("Animation completed successfully");
                    break;
                case ERROR:
                    System.err.println("Animation error: " + error.getMessage());
                    error.printStackTrace();
                    break;
            }
        });
        
        controller.start();
    }
    
    public static boolean advanceOnce(int[][] mat, int r, int c, char move) {
    	int[] dir = getDir(move);
    	System.out.println("Trying to advance with dir == " + Arrays.toString(dir));
    	System.out.println("From r = " + r + " and c = " + c);
    	int nextR = r + dir[0], nextC = c + dir[1];
    	
    	System.out.println("To nextR = " + nextR + " and nextC = " + nextC);
    	
    	var st = tryMove(nextR, nextC, dir, mat);
    	
    	if (st == null) return false;
    	while (st.size() != 0) {
    		st.pop().moveDir(dir, mat);
    	}
    	
    	return true;
    	
    }
    
    public static int[] getDir(char c) {
		if (c == '^') return new int[] {-1, 0};
		else if (c == 'v') return new int[] {1, 0};
		else if (c == '>' || c == '[') return new int[] {0, 1};
		else 			   return new int[] {0, -1};
	}
    
    public static void swap(int[][] mat, int r, int c, int x, int y) {
		var temp = mat[r][c];
		mat[r][c] = mat[x][y];
		mat[x][y] = temp;
	}
    
    public static Stack<Box> tryMove(int r, int c, int[] dir, int[][] mat) {
		Stack<Box> res = new Stack<Box>();
		Queue<Box> bfs = new ArrayDeque<Box>();
		Set<Box> addedAlready = new HashSet<>();
		int m = mat.length, n = mat[0].length;
		
		if (!inBounds(r, c, m, n)) return null;
		if (mat[r][c] == -1) return null;
		if (mat[r][c] == 0) return res;
		
		int boxId = mat[r][c];
		Box start = boxMap.get(boxId);
		
		addedAlready.add(start);
		bfs.offer(start);
		res.push(start);
		
		while (bfs.size() != 0) {
			
			int count = bfs.size();
			
			while (count != 0) {
				count--;
				
				Box curr = bfs.poll();
				
				int nextR = curr.r + dir[0], nextC = curr.c + dir[1];
				int otherC = nextC + 1;
				
				if (!inBounds(nextR, nextC, m, n) || !inBounds(nextR, otherC, m, n)) return null;
				if (mat[nextR][nextC] == -1 || mat[nextR][otherC] == -1) return null;
				
				int id1 = mat[nextR][nextC], id2 = mat[nextR][otherC];
				
				if (id1 > 0) {
					
					Box toAdd = boxMap.get(id1);
					
					if (!addedAlready.contains(toAdd)) {
						addedAlready.add(toAdd);
						bfs.offer(toAdd);
						res.push(toAdd);
					}
				}
				
				if (id2 > 0) {
					
					Box toAdd = boxMap.get(id2);
					
					if (!addedAlready.contains(toAdd)) {
						addedAlready.add(toAdd);
						bfs.offer(toAdd);
						res.push(toAdd);
					}
				}
			}
		}
		
		return res;
	}
    
    public static int calculatePositionScore() {
        int totalScore = 0;
        for (Box box : boxMap.values()) {
            totalScore += (box.r * 100 + box.c);
        }
        return totalScore;
    }
    
    public static boolean inBounds(int r, int c, int m, int n) {
		return r >= 0 &&
				r < m &&
				c >= 0 &&
				c < n;
	}
}


class GridVisualizer extends JFrame {
    private final int[][] grid;
    private final Map<Integer, Color> colorMap = new HashMap<>();
    private static final int SCORE_PANEL_WIDTH = 150; // Width for score display

    public GridVisualizer(int[][] grid) {
        this.grid = grid;
        initializeColorMap();

        setTitle("Grid Visualization");
        setSize(950, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use BorderLayout to manage grid and score panels
        setLayout(new BorderLayout());
        
        // Add grid panel to center
        add(new GridPanel(), BorderLayout.CENTER);
        
        // Add score panel to right
        add(new ScorePanel(), BorderLayout.EAST);
        
        setVisible(true);
    }

    private void initializeColorMap() {
        Random rand = new Random();
        colorMap.put(-1, new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))); // Impassable object
        colorMap.put(0, Color.WHITE); // Open space
    }

    private Color getColorForValue(int value) {
        Random rng = new Random();
        return colorMap.computeIfAbsent(value, v -> 
            new Color(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256)));
    }

    class GridPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cellWidth = getWidth() / grid[0].length;
            int cellHeight = getHeight() / grid.length;

            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[0].length; c++) {
                    g.setColor(getColorForValue(grid[r][c]));
                    g.fillRect(c * cellWidth, r * cellHeight, cellWidth, cellHeight);
                    g.setColor(Color.BLACK);
                    g.drawRect(c * cellWidth, r * cellHeight, cellWidth, cellHeight);
                }
            }
        }
    }

    class ScorePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            
            String scoreText = String.valueOf(Day15.calculatePositionScore());
            
            // Draw "Score:" label
            g.drawString("Score:", 10, 30);
            
            // Draw actual score, breaking it into multiple lines if needed
            FontMetrics fm = g.getFontMetrics();
            int startY = 50;
            int maxWidth = getWidth() - 20; // Leave 10px padding on each side
            
            while (scoreText.length() > 0) {
                String line = scoreText;
                while (fm.stringWidth(line) > maxWidth && line.length() > 1) {
                    line = line.substring(0, line.length() - 1);
                }
                
                g.drawString(line, 10, startY);
                scoreText = scoreText.substring(line.length());
                startY += fm.getHeight();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(SCORE_PANEL_WIDTH, super.getPreferredSize().height);
        }
    }
}

class AnimationController {
    private final Timer timer;
    private final String moveSequence;
    private final GridVisualizer visualizer;
    private int moveIndex = 0;
    private AnimationState state = AnimationState.READY;
    
    public enum AnimationState {
        READY, RUNNING, FINISHED, ERROR
    }
    
    private java.util.List<AnimationStateListener> listeners = new ArrayList<>();
    
    public interface AnimationStateListener {
        void onStateChange(AnimationState newState, Exception error);
    }
    
    public AnimationController(String moveSequence, GridVisualizer visualizer, int delay) {
        this.moveSequence = moveSequence;
        this.visualizer = visualizer;
        
        timer = new Timer(delay, (e) -> update());
    }
    
    public void addStateListener(AnimationStateListener listener) {
        listeners.add(listener);
    }
    
    public void start() {
        if (state == AnimationState.READY) {
            state = AnimationState.RUNNING;
            notifyListeners(null);
            timer.start();
        }
    }
    
    private void update() {
        try {
            if (moveIndex >= moveSequence.length()) {
                state = AnimationState.FINISHED;
                timer.stop();
                notifyListeners(null);
                return;
            }
            
            if (Day15.advanceOnce(Day15.mat, Day15.robotR, Day15.robotC, 
                    moveSequence.charAt(moveIndex++))) {
                
                int[] dir = Day15.getDir(moveSequence.charAt(moveIndex - 1));
                int nextR = Day15.robotR + dir[0], nextC = Day15.robotC + dir[1];
                Day15.swap(Day15.mat, Day15.robotR, Day15.robotC, nextR, nextC);
                Day15.robotR = nextR;
                Day15.robotC = nextC;
            }
            visualizer.repaint();
            
        } catch (Exception e) {
            state = AnimationState.ERROR;
            timer.stop();
            notifyListeners(e);
        }
    }
    
    private void notifyListeners(Exception error) {
        for (AnimationStateListener listener : listeners) {
            listener.onStateChange(state, error);
        }
    }
}