package Week3;

import java.io.*;
import java.util.*;
record posAndDir(int r, int c, int dir) {}
public class Day16 {
	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	public static int[][] directions = {
		    { 0,  1},  // East
		    {-1,  1},  // Northeast
		    {-1,  0},  // North
		    {-1, -1},  // Northwest
		    { 0, -1},  // West
		    { 1, -1},  // Southwest
		    { 1,  0},  // South
		    { 1,  1}   // Southeast
		};
	public static final int TOTAL_DIRS = directions.length;
	public static final int DIR_INCREMENT = 2;
	public static final long rotateCost = 1000;
	public static HashMap<posAndDir, Long> minCost;
	public static Set<posAndDir> countedSide = new HashSet<posAndDir>();
	public static Set<posAndDir> beenTo = new HashSet<posAndDir>();

	public static void main(String[] args) {
		double start = System.currentTimeMillis();
		char[][] mat = readInFullInput();
		minCost = new HashMap<>();
		int endR = -1, endC = -1;
		int startR = -1, startC = -1;
		
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				
				if (mat[i][j] == 'S') {
					startR = i;
					startC = j;
				}
				else if (mat[i][j] == 'E') {
					endR = i;
					endC = j;
				}
			}
		}
		
		long minPath = minPath(mat, startR, startC, endR, endC);
		recurseAllPaths(mat, endR, endC, minPath);
		double end = System.currentTimeMillis();
		
		System.out.println("Took " + (end - start) + " ms");
		System.out.println("Min possible path costs " + minPath);
		System.out.println("Count of all sides of paths included is " + countedSide.size());

	}
	
	private static void recurseAllPaths(char[][] mat, int currR, int currC, long cost) {
		for (int i = 0; i < TOTAL_DIRS; i += 2) {
			int dir = i;
			posAndDir pos = new posAndDir(currR, currC, dir);
			recursePath(pos, cost);
		}
	}
	
	private static void recursePath(posAndDir pos, long cost) {
		if (beenTo.contains(pos)) {
			System.out.println("Already been to: " + pos);
			return;
		}
		
		posAndDir cell = new posAndDir(pos.r(), pos.c(), 0);
		countedSide.add(cell);
		
		posAndDir back = backStep(pos);
		
		if (minCost.containsKey(back) && minCost.get(back) == cost - 1) recursePath(back, cost - 1);
		
		posAndDir rotateLeft = rotate(pos, DIR_INCREMENT);
		posAndDir rotateRight = rotate(pos, -DIR_INCREMENT);
		
		if (minCost.containsKey(rotateLeft) && minCost.get(rotateLeft) == cost - rotateCost) recursePath(rotateLeft, cost - rotateCost);
		if (minCost.containsKey(rotateRight) && minCost.get(rotateRight) == cost - rotateCost) recursePath(rotateRight, cost - rotateCost);
	}
	
	private static long minPath(char[][] mat, int r, int c, int endR, int endC) {
		int m = mat.length, n = mat[0].length;
		
		posAndDir start = new posAndDir(r, c, 0);
		minCost.put(start,  0L);
		
		PriorityQueue<posAndDir> pq = new PriorityQueue<>((a, b) -> {
			return minCost.get(a).compareTo(minCost.get(b));
		});
		
		pq.offer(start);
		
		while (pq.size() != 0) {
			
			posAndDir curr = pq.poll();
			
			if (curr.r() == endR && curr.c() == endC) return minCost.get(curr);
			
			long currScore = minCost.get(curr);
			
			posAndDir stepInDirection = takeStep(curr);
			posAndDir rotateLeft = rotate(curr, -DIR_INCREMENT);
			posAndDir rotateRight = rotate(curr, DIR_INCREMENT);
			
			long stepDirScore = currScore + 1;
			long rotateScore = currScore + rotateCost;
			
			tryAdd(pq, stepInDirection, mat, stepDirScore);
			tryAdd(pq, rotateLeft, mat, rotateScore);
			tryAdd(pq, rotateRight, mat, rotateScore);
		}
		
		return Long.MIN_VALUE;
	}
	
	private static void tryAdd(PriorityQueue<posAndDir> pq, posAndDir pos, char[][] mat, long score) {
		if (mat[pos.r()][pos.c()] == '#') return;
		if (minCost.containsKey(pos) && minCost.get(pos) <= score) return;
		
		minCost.put(pos, score);
		pq.offer(pos);
	}
	
	private static posAndDir rotate(posAndDir curr, int rotateDirection) {
		int dir = curr.dir();
		
		dir = (dir + rotateDirection + TOTAL_DIRS) % TOTAL_DIRS;
		
		return new posAndDir(curr.r(), curr.c(), dir);
	}
	
	private static posAndDir takeStep(posAndDir curr) {
		int r = curr.r(), c = curr.c();
		
		int[] dir = directions[curr.dir()];
		
		r += dir[0];
		c += dir[1];
		
		return new posAndDir(r, c, curr.dir());
	}
	
	private static posAndDir backStep(posAndDir curr) {
		int r = curr.r(), c = curr.c();
		
		int[] dir = directions[curr.dir()];
		
		r -= dir[0];
		c -= dir[1];
		
		return new posAndDir(r, c, curr.dir());
	}
	
	public static char[][] readInFullInput(){
		List<char[]> primMat = new ArrayList<>();
		
		try(Scanner scan = new Scanner(input)){
			
			while (scan.hasNext()) primMat.add(scan.nextLine().toCharArray());
		}
		catch(FileNotFoundException e) {
			System.err.println("File not found at given path - that's not good!");
			e.printStackTrace();
			System.exit(1);
		}
		
		int m = primMat.size(), n = primMat.get(0).length;
		char[][] res = new char[m][n];
		
		for (int i = 0; i < m; i++) {
			res[i] = primMat.get(i);
		}
		
		
		return res;
	}

}
