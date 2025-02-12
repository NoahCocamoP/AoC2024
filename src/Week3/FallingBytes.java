package Week3;

import java.util.*;
import java.io.*;

record coord(int y, int x) {}
public class FallingBytes {
	
	public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    public static final int ROWS = 71, COLS = 71;
    
    public static final int READ_BYTES = 3500;
    public static Set<coord> blocked;
    public static Map<coord, Integer> pathScore;
    
    public static final int[] rDirs = new int[] {1, -1, 0, 0};
    public static final int[] cDirs = new int[] {0, 0, 1, -1};
    public static final int NUM_DIRS = 4;
    
    public static void main(String[] args) {
    	List<coord> coords = new ArrayList<>();
    	blocked = new HashSet<>();
    	pathScore = new HashMap<>();
    	Scanner scan = null;
    	try {
    		scan = new Scanner(input);
    	}
    	catch(FileNotFoundException e) {
    		System.err.println("Couldn't find the input file. Returning now.");
    		System.exit(1);
    	}
    	
    	
    	while (scan.hasNext()) {
    		addCoord(coords, scan.nextLine());
    	}
    	
    	double startFirst = System.currentTimeMillis();
    	
    	for (int i = 0; i < coords.size(); i++) {
    		blocked.add(coords.get(i));
    		int minPath = findMinPath();
    		if (minPath == -1) {
    			System.out.println("According to linear-bfs algo, byte that closes end cell off is: " + (i + 1));
    			break;
    		}
    	}
    	double timeElapsedFirst = System.currentTimeMillis() - startFirst;
    	
    	double startSecond = System.currentTimeMillis();
    	for (int i = 0; i < coords.size(); i++) {
			coord c = coords.get(i);
    		if (pathScore.containsKey(c)) continue;
    		pathScore.put(c, i);
    	}
    	
    	for (int i = 0; i < ROWS; i++) {
    		for (int j = 0; j < COLS; j++) {
    			
    			coord c = new coord(i, j);
    			if (pathScore.containsKey(c)) continue;
    			pathScore.put(c, Integer.MAX_VALUE);
    		}
    	}
    	int minPath = findMaxPath();
    	double timeElapsedSecond = System.currentTimeMillis() - startSecond;
    	
    	System.out.println("Time elapsed first method: " + timeElapsedFirst);
    	System.out.println("Time elapsed second method: " + timeElapsedSecond);
    	
    	System.out.println("According to Dijkstras, byte that closes off end cell is " + (minPath + 1));
    }
    private static int findMaxPath() {
    	coord start = new coord(0, 0);
    	coord end = new coord(ROWS - 1, COLS - 1);
    	
    	HashMap<coord, Integer> score = new HashMap<>();
    	PriorityQueue<coord> pq = new PriorityQueue<>((a, b) -> {
    		Integer aScore = score.get(a);
    		Integer bScore = score.get(b);
    		
    		return Integer.compare(bScore, aScore);
    	});
    	
    	score.put(start, pathScore.get(start));
    	pq.offer(start);
    	
    	while (pq.size() != 0) {
    		coord curr = pq.poll();
    		Integer scoreHere = score.get(curr);
    		if (curr.equals(end)) return score.get(curr);
    		
    		for (int dir = 0; dir < NUM_DIRS; dir++) {
				int y = curr.y() + rDirs[dir];
				int x = curr.x() + cDirs[dir];
				coord nextSpot = new coord(y, x);
				
				//System.out.println("Next spot to try is " + nextSpot);
				if (y < 0 || x < 0 || y >= ROWS || x >= COLS) {
					//System.out.println("Not in bounds");
					continue;
				}
				
				
				
				Integer nextScore = Math.min(scoreHere, pathScore.get(nextSpot));
				if (score.containsKey(nextSpot) && score.get(nextSpot) >= nextScore) continue;
				score.put(nextSpot, nextScore);
				pq.offer(nextSpot);
			}
    	}
    	
    	System.err.println("Pq ended without finding an ending somehow. Not good. Exiting now.");
    	System.exit(1);
    	
    	return -1;
    }
    private static int findMinPath() {
    	coord start = new coord(0, 0);
    	coord end = new coord(ROWS - 1, COLS - 1);
    	
    	Set<coord> beenTo = new HashSet<>();
    	
    	for (coord c : blocked) beenTo.add(c);
    	beenTo.add(start);
    	
    	Queue<coord> q = new ArrayDeque<>();
    	q.offer(start);
    	
    	int moves = -1;
    	
    	while (q.size() != 0) {
    		int count = q.size();
    		moves++;
    		
    		while (count != 0) {
    			count--;
    			coord curr = q.poll();
    			//System.out.println("Currently at: " + curr);
    			
    			if (curr.equals(end)) return moves;
    			
    			for (int dir = 0; dir < NUM_DIRS; dir++) {
    				int y = curr.y() + rDirs[dir];
    				int x = curr.x() + cDirs[dir];
    				coord nextSpot = new coord(y, x);
    				
    				//System.out.println("Next spot to try is " + nextSpot);
    				if (y < 0 || x < 0 || y >= ROWS || x >= COLS) {
    					//System.out.println("Not in bounds");
    					continue;
    				}
    				
    				
    				
    				if (blocked.contains(nextSpot)) {
    					//System.out.println("Blocked spot.");
    					continue;
    				}
    				if (beenTo.contains(nextSpot)) {
    					//System.out.println("Already visited");
    					continue;
    				}
    				//System.out.println("Adding to q");
    				beenTo.add(nextSpot);
    				q.offer(nextSpot);
    			}
    		}
    	}
    	
    	//System.err.println("Queue concluded with no min path. Not good. Exiting now.");
    	//System.exit(1);
    	return -1;
    }
    
    private static void addCoord(List<coord> lis, String input) {
    	String[] axis = input.split(",");
    	
    	int y = Integer.parseInt(axis[1]);
    	int x = Integer.parseInt(axis[0]);
    	
    	coord toAdd = new coord(y, x);
    	lis.add(toAdd);
    }

}
