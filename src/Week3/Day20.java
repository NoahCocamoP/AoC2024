package Week3;

import java.util.*;
import java.io.*;

record pos(int r, int c) {
	
}

record skipPos(pos a, pos b) {}
public class Day20 {
	
	public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    public static final int[] rDirs = new int[] {1, 0, -1, 0};
	public static final int[] cDirs = new int[] {0, -1, 0, 1};
	public static final int NUM_DIRS = 4;
	public static long MAX_DIFF = 20;
	public static long THRESHOLD = 100L;
	
	public static final HashMap<pos, Long> minDistEnd = new HashMap<>();
	public static final HashMap<pos, Long> minDistStart = new HashMap<>();
	
	public static Long minDistNoCheating = -1L;

	public static void main(String[] args) {
		char[][] mat = readInFullInput();
		BFS(mat);
		int m = mat.length, n = mat[0].length;
		
		long total = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				pos start = new pos(i, j);
				if (mat[i][j] == '#' ||
					!minDistEnd.containsKey(start)) continue;
					
				
				long cost = minDistEnd.get(start);
				
				int startR = (int)Math.max(0, i - MAX_DIFF);
				int endR = (int)Math.min(m - 1, i + MAX_DIFF);
				for (int r = startR; r <= endR; r++) {
					int diffR = Math.abs(r - i);
					int remainingDiff = (int)MAX_DIFF - diffR;
					
					int startC = Math.max(0, j - remainingDiff);
					int endC = Math.min(n - 1, j + remainingDiff);
					
					for (int c = startC; c <= endC; c++) {
						int diffC = Math.abs(j - c);
						pos end = new pos(r, c);
						long totalDiff = diffC + diffR;
						if (!minDistStart.containsKey(end) ||
							(i == r && j == c) ||
							mat[r][c] == '#' ||
							totalDiff > MAX_DIFF) continue;
						
						
						long otherCost = minDistStart.get(end);
						
						long totalCostThis = cost + otherCost;
						long timeSaved = minDistNoCheating - totalCostThis - totalDiff;
						if (timeSaved >= THRESHOLD) {
							//System.out.println("Going from " + end + " to " + start + " saved " + timeSaved);
							//printCheat(mat, end, start);
							total++;
						}
					}
				}
			}
		}
		
		System.out.println(total);

	}
	
	private static void printCheat(char[][] mat, pos start, pos end) {
		for (int row = 0; row < mat.length; row++) {
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < mat[row].length; i++) {
				if (row == start.r() && i == start.c()) {
					sb.append('$');
				}
				else if (row == end.r() && i == end.c()) {
					sb.append('3');
				}
				else sb.append(mat[row][i]);
			}
			
			System.out.println(sb.toString());
		}
	}
	
	private static void startAt(int r, int c, Queue<pos> q, char[][] mat, HashMap<pos, Long> minDist) {
		int m = mat.length, n = mat[0].length;
		
		pos start = new pos(r, c);
		
		long currDist = 0;
		q.offer(start);
		minDist.put(start, currDist);
		
		while (q.size() != 0) {
			
			currDist++;
			int count = q.size();
			while (count != 0) {
				count--;
				pos curr = q.poll();
				
				
				for (int dir = 0; dir < NUM_DIRS; dir++) {
					int nextR = curr.r() + rDirs[dir];
					int nextC = curr.c() + cDirs[dir];
					
					pos next = new pos(nextR, nextC);
					
					if (minDist.containsKey(next) || mat[nextR][nextC] == '#') continue;
					
					minDist.put(next, currDist);
					q.offer(next);
					
				}
			}
		}
	}
	
	private static void BFS(char[][] mat) {
		
		int m = mat.length, n = mat[0].length;
		
		Queue<pos> qEnd = new ArrayDeque<>();
		Queue<pos> qStart = new ArrayDeque<>();
		int r = -1, c = -1;
		
		for (int i = 0 ; i < m; i++) {
			for (int j = 0; j < n; j++) {
				
				if (mat[i][j] == 'E') startAt(i, j, qEnd, mat, minDistEnd);
				else if (mat[i][j] == 'S') {
					startAt(i, j, qStart, mat, minDistStart);
					r = i;
					c = j;
				}
			}
		}
		
		pos p = new pos(r, c);
		minDistNoCheating = minDistEnd.get(p);
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
