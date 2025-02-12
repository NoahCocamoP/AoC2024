package Week2;

import java.io.*;
import java.util.*;

record Pos(int r, int c) {}

class UnionFind{
	public Pos[][] parent;
	public int[][] score;
	public int[][] size;
	
	public UnionFind(char[][] mat) {
		int m = mat.length, n = mat[0].length;
		parent = new Pos[m][n];
		score = new int[m][n];
		size = new int[m][n];
		
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				
				int thisScore = 0;
				
				if (mat[i][j] == '9') thisScore++;
				
				Pos thisPos = new Pos(i, j);
				
				parent[i][j] = thisPos;
				score[i][j] = thisScore;
				size[i][j] = 1;
			}
		}
	}
	
	public int getSize(Pos a) {
		return size[a.r()][a.c()];
	}
	
	public Pos getParent(Pos curr) {
		Pos parentThis = parent[curr.r()][curr.c()];
		if (!curr.equals(parentThis)) return parent[curr.r()][curr.c()] = getParent(curr);
		else return parentThis;
	}
	
	public int getScore(Pos curr) {
		Pos p = getParent(curr);
		
		return score[p.r()][p.c()];
	}
	
	public void Union(Pos a, Pos b) {
		Pos pA = getParent(a);
		Pos pB = getParent(b);
		
		int sizeA = getSize(pA), sizeB = getSize(pB);
		
		Pos smaller = sizeA >= sizeB ? pB : pA;
		Pos larger = sizeA >= sizeB ? pA : pB;
		
		parent[smaller.r()][smaller.c()] = pA;
		size[larger.r()][larger.c()] += size[smaller.r()][smaller.c()];
		score[larger.r()][larger.c()] += score[smaller.r()][smaller.c()];
		
		
	}
}

public class CountingTrails {
	public static final String inputPath = "InputText.txt";
	public static final File input = new File(inputPath);
	public static final int[] rDirs = new int[] {1, -1, 0, 0};
	public static final int[] cDirs = new int[] {0, 0, 1, -1};
	public static final int NUM_DIRS = 4;

	public static void main(String[] args) {
		char[][] mat = readInInput();
		solveWithDP(mat);

	}
	
	private static void solveWithDP(char[][] mat) {
		int m = mat.length, n = mat[0].length;
		
		long[][] dp = new long[m][n];
		
		for (int i = 0; i < m; i++) Arrays.fill(dp[i], -1);
		
		long res = 0;
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				
				if (mat[i][j] == '0') {
					long outpThat = countDP(dp, mat, i, j);
					
					//System.out.println("At that trailhead, there is a score of " + outpThat);
					res += outpThat;
				}
			}
		}
		
		System.out.println("The score of all trailheads is " + res);
	}
	
	private static boolean inBounds(int r, int c, int m, int n) {
		return r >= 0 &&
			   r < m &&
			   c >= 0 && 
			   c < n;
	}
	
	private static long countDP(long[][] dp, char[][] mat, int i, int j) {
		if (mat[i][j] == '9') return 1;
		else if (dp[i][j] != -1) return dp[i][j];
		
		long res = 0;
		int m = mat.length, n = mat[0].length;
		int thisVal = mat[i][j] - '0';
		for (int dir = 0; dir < NUM_DIRS; dir++) {
			int r = i + rDirs[dir];
			int c = j + cDirs[dir];
			
			if (!inBounds(r, c, m, n)) continue;
			
			int thatVal = mat[r][c] - '0';
			
			if ((thatVal - thisVal) != 1) continue;
			
			res += countDP(dp, mat, r, c);
		}
		
		//System.out.println("At " + (i + 1) + " - " + (j + 1) + " there is a score of " + res);
		
		return res;
		
	}
	
	public static char[][] readInInput(){
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
