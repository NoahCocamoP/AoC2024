package Week1;

import java.io.*;
import java.util.*;

public class FindingChristmas {
	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	public static final int[] rDirs = new int[] {0, 0, 1, -1, 1, 1, -1, -1};
	public static final int[] cDirs = new int[] {1, -1, 0, 0, 1, -1, 1, -1};
	public static final int NUM_DIRS = 8;
	public static final int DIAG_START = 4;
	public static char[] TO_MATCH = "MAS".toCharArray();
	public static void main(String[] args) {
		List<char[]> wordMat = readInput();
		//long res = countAllDirs(wordMat);
		int[][] diagPairs = new int[4][2];
		diagPairs[0] = new int[] {4, 5};
		diagPairs[1] = new int[] {5, 7};
		diagPairs[2] = new int[] {6, 7};
		diagPairs[3] = new int[] {4, 6};
		long res = countAllSquare(wordMat, diagPairs);
		
		System.out.println("There are " + res);
		

	}
	
	private static boolean isMatch(int r, int c, int rAdd, int cAdd, List<char[]> mat) {
		int m = mat.size(), n = mat.get(0).length;
		
		int ind = 0, mLen = TO_MATCH.length;
		while (ind < mLen && inBounds(r, c, m, n) && mat.get(r)[c] == TO_MATCH[ind]) {
			ind++;
			r += rAdd;
			c += cAdd;
		}
		
		return ind == mLen;
	}
	
	private static boolean foundMatch(int r, int c, List<char[]> mat, int[][] dirPairs) {
		for (var p : dirPairs) {
			int rAdd1 = rDirs[p[0]], cAdd1 = cDirs[p[0]];
			int rAdd2 = rDirs[p[1]], cAdd2 = cDirs[p[1]];
			
			int sR1 = r - rAdd1, sC1 = c - cAdd1;
			int sR2 = r - rAdd2, sC2 = c - cAdd2;
			
			char c1 = mat.get(sR1)[sC1];
			char c2 = mat.get(sR2)[sC2];
			
			if (c1 == c2 && c1 == TO_MATCH[0]) {
				if (isMatch(sR1, sC1, rAdd1, cAdd1, mat) &&
					isMatch(sR2, sC2, rAdd2, cAdd2, mat)) return true;
			}
		}
		
		return false;
	}
	
	private static long countAllSquare(List<char[]> wordMat, int[][] diagDirPairs) {
		long res = 0;
		
		int m = wordMat.size(), n = wordMat.get(0).length;
		
		for (int i = 1; i < m - 1; i++) {
			for (int j = 1; j < n - 1; j++) {
				
				if (foundMatch(i, j, wordMat, diagDirPairs)) res++;
			}
		}
		
		return res;
	}
	
	public static boolean inBounds(int r, int c, int m, int n) {
		return r >= 0 &&
			   r < m  &&
			   c >= 0 &&
			   c < n;
	}
	
	public static long countWithDir(List<char[]> mat, int rAdd, int cAdd) {
		int m = mat.size();
		int n = mat.get(0).length;
		
		long totalMatch = 0;
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				
				int r = i, c = j;
				
				int ind = 0, gLen = TO_MATCH.length;
				
				while (ind < gLen && inBounds(r, c, m, n) && mat.get(r)[c] == TO_MATCH[ind]) {
					ind++;
					r += rAdd;
					c += cAdd;
				}
				
				if (ind == gLen) totalMatch++;
			}
		}
		
		return totalMatch;
		
	}
	
	public static long countAllDirs(List<char[]> mat) {
		long res = 0;
		for (int dir = 0; dir < NUM_DIRS; dir++) {
			int rIncr = rDirs[dir];
			int cIncr = cDirs[dir];
			
			res += countWithDir(mat, rIncr, cIncr);
		}
		
		return res;
	}
	
	public static List<char[]> readInput(){
		List<char[]> outp = new ArrayList<char[]>();
		
		try(Scanner scan = new Scanner(input)){
			
			while (scan.hasNext()) outp.add(scan.nextLine().toCharArray());
		}
		catch(FileNotFoundException e) {
			System.err.println("Something went very wrong! Input File not able to be found at specified path!");
			System.exit(1);
		}
		
		return outp;
	}

}
