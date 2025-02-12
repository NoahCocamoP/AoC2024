package Week3;

import java.util.*;
import java.io.*;

record path(char from, char to) {}
record pathWithDepth(path p, int d) {}
public class Day21Memo {
	public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    public static final char[][] numerical = new char[][] {
    	{'7','8','9'},
    	{'4','5','6'},
    	{'1','2','3'},
    	{'X','0','A'}
    };
    public static final char[][] directional = new char[][] {
    	{'X','^','A'},
    	{'<','v','>'}
    };
    public static final int[] rDirs = new int[] {-1, 1, 0, 0};
    public static final int[] cDirs = new int[] {0, 0, -1, 1};
    public static final char[] dirChars = new char[] {'^','v','<','>'};
    public static final int NUM_DIRS = 4;
    public static final int STARTING_DEPTH = 25;
    public static Map<path, String> numPaths;
    public static Map<path, String> dirPaths;
    public static Map<pathWithDepth, Long> memo;
    
   
	public static void main(String[] args) {
		
		double start = System.currentTimeMillis();
		numPaths = new HashMap<>();
		dirPaths = new HashMap<>();
		memo = new HashMap<>();
		for (int i = 0; i < numerical.length; i++) {
			for (int j = 0; j < numerical[0].length; j++) {
				if (numerical[i][j] == 'X') continue;
				findPaths(numPaths, numerical, i, j);
			}
		}
		
		
		for (int i = 0; i < directional.length; i++) {
			for (int j = 0; j < directional[0].length; j++) {
				if (directional[i][j] == 'X') continue;
				findPaths(dirPaths, directional, i, j);
			}
		}
		long total = 0;
		
		Scanner scan = null;
		try {
			scan = new Scanner(input);
		}
		catch(Exception e) {
			System.err.println("File not found at path. Exiting now.");
			System.exit(1);
		}
		
		while (scan.hasNext()) {
			total += doDP(scan.nextLine());
		}
		System.out.println("Program ran in " + (System.currentTimeMillis() - start));
		System.out.println(total);

	}
	
	private static void findPaths(Map<path, String> pathsFromTo, char[][] mat, int sR, int sC) {
		int m = mat.length, n = mat[0].length;
		
		Map<Character, pos> cPositions = new HashMap<>();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				cPositions.put(mat[i][j], new pos(i, j));
			}
		}
		char start = mat[sR][sC];
		path st = new path(start, start);
		//System.out.println("Starting at " + st);
		pathsFromTo.put(st, "");
		
		Queue<path> q = new ArrayDeque<>();
		q.offer(st);
		while (q.size() != 0) {
			
			path curr = q.poll();
			pos p = cPositions.get(curr.to());
			String prevPath = pathsFromTo.get(curr);
			
			for (int dir = 0; dir < NUM_DIRS; dir++) {
				int nextR = p.r() + rDirs[dir], nextC = p.c() + cDirs[dir];
				
				if (nextR < 0 || nextR >= m || nextC < 0 || nextC >= n || mat[nextR][nextC] == 'X') continue;
				char nextTo = mat[nextR][nextC];
				path nextPath = new path(curr.from(), nextTo);
				
				if (pathsFromTo.containsKey(nextPath)) continue;
				char dirChar = dirChars[dir];
				String thisPath = prevPath + dirChar;
				//System.out.println("Going from " + curr + " to " + nextPath + " with path " + thisPath);
				pathsFromTo.put(nextPath, thisPath);
				q.offer(nextPath);
			}
		}
		
		
	}
	private static List<path> getRealSeedList(String initial) {
		int len = initial.length();
		List<path> lis = new ArrayList<>();
		
		for (int i = 0; i < len - 1; i++) {
			char from = initial.charAt(i);
			char to = initial.charAt(i + 1);
			path p = new path(from, to);
			lis.add(p);
		}
		
		return lis;
	}
	
	private static void buildPerms(Map<Character, Integer> mp, int remaining, String curr, List<String> lis) {
		if (remaining == 0) {
			lis.add(curr);
			return;
		}
		
		for (char c : dirChars) {
			
			if (mp.containsKey(c) && mp.get(c) != 0) {
				
				mp.put(c, mp.get(c) - 1);
				buildPerms(mp, remaining - 1, curr + c, lis);
				mp.put(c, mp.get(c) + 1);
			}
		}
	}
	
	private static List<String> getAllPermutations(String initial){
		Map<Character, Integer> counts = new HashMap<>();
		
		for (char c : initial.toCharArray()) {
			counts.put(c, counts.getOrDefault(c, 0) + 1);
		}
		List<String> lis = new ArrayList<>();
		buildPerms(counts, initial.length(), "", lis);
		
		
		
		return lis;
	}
	
	private static long robotFilter(path prev, String c, int depth) {
		if (depth == 0) {
			return c.length() + 1;
		}
		List<String> permutations = getAllPermutations(c);
		long minRes = Long.MAX_VALUE;
		
		for (var cmb : permutations) {
			
			if (!isGoodPath(cmb, prev, directional)) continue;
			String comp = 'A' + cmb + 'A';
			
			long res = 0;
			int len = comp.length();
			
			
			for (int i = 0; i < len - 1; i++) {
				char from = comp.charAt(i);
				char to = comp.charAt(i + 1);
				path p = new path(from, to);
				pathWithDepth key = new pathWithDepth(p, depth);
				
				if (memo.containsKey(key)) {
					res += memo.get(key);
					continue;
				}
			
				
				long value = robotFilter(p, dirPaths.get(p), depth - 1);
				memo.put(key, value);
				res += value;
			}
			if (res < minRes) {
				minRes = res;
			}
		}
		
		
		
		return minRes;
	}
	
	private static long doDP(String seed) {
		
		String startAt = 'A' + seed;
		List<path> realSeedSections = getRealSeedList(startAt);
		long totalMemo = 0;
		
		String res = "";
		String numericPath = "";
		
		
		for (var pathSec : realSeedSections) {
			String sec = numPaths.get(pathSec);
			List<String> perms = getAllPermutations(sec);
			long minSection = Long.MAX_VALUE;
			String minComb = "";
			String minSecPerm = "";
			
			for (String rS : perms) {
				long thisSection = 0;
				String realSeed = 'A' + rS + 'A';
				if (!isGoodPath(rS, pathSec, numerical)) {
					continue;
				}
				
				String combThis = "";
				for (int i = 0; i < realSeed.length() - 1; i++) {
					char from = realSeed.charAt(i);
					char to = realSeed.charAt(i + 1);
					
					path p = new path(from, to);
					long resFilter = robotFilter(p, dirPaths.get(p), STARTING_DEPTH - 1);
					thisSection += resFilter;
				}
				
				if (minSection > thisSection) {
					minSection = thisSection;
					minComb = combThis;
					minSecPerm = rS;
				}
			}
			
			numericPath = numericPath + minSecPerm + 'A';
			
			totalMemo += minSection;
			res = res + minComb;
		}
		
		
		char[] toMatch = seed.toCharArray();
		
		long numericPortion = 0;
		
		int ind = 0;
		while (ind < toMatch.length && Character.isDigit(toMatch[ind])) {
			numericPortion *= 10;
			numericPortion += toMatch[ind++] - '0';
		}
		

	
		return totalMemo * numericPortion;
	}
	
	private static boolean isGoodPath(String moves, path p, char[][] mat) {
		
		int m = mat.length, n = mat[0].length;
		
		int r = -1, c = -1;
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				
				if (mat[i][j] == p.from()) {
					r = i;
					c = j;
				}
			}
		}
		
		for (var move : moves.toCharArray()) {
			
			for (int dir = 0; dir < NUM_DIRS; dir++) {
				if (move == dirChars[dir]) {
					
					r += rDirs[dir];
					c += cDirs[dir];
					
					if (mat[r][c] == 'X') return false;
				}
			}
		}
		
		return true;
	}
	
	

}
