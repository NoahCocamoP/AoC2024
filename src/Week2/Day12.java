package Week2;

import java.io.*;
import java.util.*;

public class Day12 {
	
	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	public static final int[] rDirs = new int[] {1, 0, -1, 0};
	public static final int[] cDirs = new int[] {0, -1, 0, 1};
	public static final int NUM_DIRS = 4;
	public static final int DOWN = 0, UP = 2, RIGHT = 3, LEFT = 1;
	public static final int BOTTOM = 0, TOP = 2, LEFT_SIDE = 1, RIGHT_SIDE = 3;
	
	public static int[] checkDiagAt(int dir) {
		if (dir == DOWN) return new int[] {1, -1, RIGHT};
		else if (dir == LEFT) return new int[] {1, -1, DOWN};
		else if (dir == UP) return new int[] {-1, -1, LEFT};
		else 				return new int[]{-1, 1, UP};
	}
	
	private static int[] diagOnSide(int dir, int side) {
		if (dir == DOWN) {
			
			if (side == LEFT_SIDE) return new int[] {1, 1};
			else return new int[] {1, -1};
		}
		else if (dir == UP) {
			
			if (side == LEFT_SIDE) return new int[] {-1, 1};
			else				   return new int[] {-1, -1};
		}
		else if (dir == LEFT) {
			
			if (side == TOP) return new int[] {-1, -1};
			else 			 return new int[] {1, -1};
		}
		else {
			
			if (side == TOP) return new int[] {-1, 1};
			else		     return new int[] {1, 1};
		}
	}

	public static void main(String[] args) {
		char[][] mat = readInFullInput();
		solve(mat);
		
		

	}
	
	private static void dfs(long[] aAndP, int r, int c, char ID, char[][] mat, int[][] rIDS, int rID) {
		if (rIDS[r][c] != -1) return;
		rIDS[r][c] = rID;
		aAndP[0]++;
		
		for (int dir = 0; dir < NUM_DIRS; dir++) {
			int nR = r + rDirs[dir], nC = c + cDirs[dir];
			
			if (!inBounds(nR, nC, mat.length, mat[0].length) || mat[nR][nC] != ID) {
				continue;
			}
			dfs(aAndP, nR, nC, ID, mat, rIDS, rID);
			
		}
	}
	
	private static boolean inBounds(int r, int c, int m, int n) {
		return r >= 0 &&
			   r < m &&
			   c >= 0 &&
			   c < n;
	}
	
	private static void solve(char[][] mat) {
		
		int m = mat.length, n = mat[0].length;
		
		boolean[][][] beenToFull = new boolean[m][n][4];
		int[][] regionID = new int[m][n];
		HashMap<Integer, long[]> mpToArr = new HashMap<>();
		
		for (int i = 0; i < m; i++) Arrays.fill(regionID[i], -1);
		long total = 0;
		int regionNum = 0;
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				
				if (regionID[i][j] == -1) {
					regionNum++;
					
					long[] arrAndP = new long[2];
					mpToArr.put(regionNum, arrAndP);
					dfs(arrAndP, i, j, mat[i][j], mat, regionID, regionNum);
					
					System.out.println("Found an area of " + arrAndP[0]);
				}
			}
		}
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				
				int rID = regionID[i][j];
				long[] arr = mpToArr.get(rID);
				char ID = mat[i][j];
				
				for (int dir = 0; dir < 4; dir++) {
					
					
					int r = i + rDirs[dir], c = j + cDirs[dir];
					
					
					if (!inBounds(r, c, m, n) || mat[r][c] != ID) {
						
						int[] dirs = dir % 2 == 0 ? new int[]{LEFT, RIGHT} : new int[] {UP, DOWN};
					
						int side = getSide(dir);
						
						if (beenToFull[i][j][side]) continue;
						arr[1]++;
						System.out.println("Initial side checked before call is " + dir);
						markSides(i, j, side, mat, beenToFull, dirs);
					}
				}
			}
		}
		
		for (var entry : mpToArr.entrySet()) {
			long[] arr = entry.getValue();
			
			System.out.println("Found " + arr[1] + " sides on grouping " + entry.getKey());
			total += arr[0] * arr[1];
		}
		
		System.out.println(total);
	}
	
	private static void markSides(int i, int j, int side, char[][] mat, boolean[][][] beenTo, int[] dirs) {
		
		int r = i, c = j;
		int m = mat.length, n = mat[0].length;
		char ID = mat[i][j];
		
		int[] toAdd = new int[] {rDirs[dirs[0]], cDirs[dirs[0]]};
		
		System.out.println("These dirs are " + Arrays.toString(dirs));
		int minR = i, minC = j;
		int maxR = i, maxC = j;
		
		while (inBounds(r, c, m, n)) {
			beenTo[r][c][side] = true;
			int nR = r + toAdd[0], nC = c + toAdd[1];
			
			if (!inBounds(nR, nC, m, n) || mat[nR][nC] != ID) {
				System.out.println("Breaking on condition 1");
				break;
			}
			
			int[] diag = diagOnSide(dirs[0], side);
			
			nR = r + diag[0];
			nC = c + diag[1];
			
			if (inBounds(nR, nC, m, n) && mat[nR][nC] == ID) {
				System.out.println("Breaking on condition 2");
				break;
			}
			
			r += toAdd[0];
			c += toAdd[1];
			
			minR = Math.min(minR, r);
			maxR = Math.max(maxR, r);
			minC = Math.min(minC, c);
			maxC = Math.max(maxC, c);
			
		}
		
		System.out.println("Went to " + r + " - " + c);
		
		r = i;
		c = j;
		toAdd = new int[] {rDirs[dirs[1]], cDirs[dirs[1]]};
		
		
		
		while (inBounds(r, c, m, n)) {
			beenTo[r][c][side] = true;
			int nR = r + toAdd[0], nC = c + toAdd[1];
			
			if (!inBounds(nR, nC, m, n) || mat[nR][nC] != ID) {
				System.out.println("Breaking on condition 1");
				break;
			}
			
			int[] diag = diagOnSide(dirs[1], side);
			
			nR = r + diag[0];
			nC = c + diag[1];
			
			if (inBounds(nR, nC, m, n) && mat[nR][nC] == ID)  {
				System.out.println("Breaking on condition 2");
				break;
			}
			
			r += toAdd[0];
			c += toAdd[1];
			
		}
		
		System.out.println("Then went to " + r + " - " + c);
		
		int[] from = new int[] {maxR, maxC};
		int[] to = new int[] {minR, minC};
		
		System.out.println("Drew a side from " + Arrays.toString(from) + " to  " + Arrays.toString(to) + " on side " + side);
 	}
	
	private static int getSide(int dir) {
		if (dir == DOWN) return BOTTOM;
		else if (dir == LEFT) return RIGHT_SIDE;
		else if (dir == RIGHT) return LEFT_SIDE;
		return TOP;
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
