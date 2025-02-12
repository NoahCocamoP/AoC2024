package Week1;

import java.io.*;
import java.util.*;

public class GuardPath {
	public static final String inputPath = "InputText.txt";
	public static final File input = new File(inputPath);
	public static final int[] rDirs = new int[] {-1, 0, 1, 0};
	public static final int[] cDirs = new int[] {0, 1, 0, -1};
	public static void main(String[] args) {
		List<char[]> mat = readInInput();
		part1(mat);

	}
	
	public static void part1(List<char[]> mat) {
		int[] pos = {-1, -1};
		int dir = -1;
		int m = mat.size(), n = mat.get(0).length;
		
		for (int i = 0; i < m && dir == -1; i++) {
			char[] currRow = mat.get(i);
			for (int j = 0; j < n && dir == -1; j++) {
				
				if (currRow[j] != '.' && currRow[j] != '#') {
					pos = new int[] {i, j};
					dir = getDirInd(currRow[j]);
				}
			}
		}
		
		mat.get(pos[0])[pos[1]] = '.';
		boolean[][] beenTo = new boolean[m][n];
		beenTo[pos[0]][pos[1]] = true;
		long totalVisited = 1;
		long part2Count = 0;
		
		while (nextMoveInBounds(pos, dir, m, n)) {
			if (nextMoveBlocked(pos, dir, mat)) {
				dir = (dir + 1) % rDirs.length;
				continue;
			}
			int[] nextPos = makeMove(pos, dir);;
			if (!beenTo[nextPos[0]][nextPos[1]]) {
				totalVisited++;
				if (isLooping(pos, nextPos, mat, dir)) part2Count++;
			}
			pos = nextPos;
			beenTo[pos[0]][pos[1]] = true;
		}
		
		System.out.println("Finished!");
		System.out.println("Answer to part 1: " + totalVisited);
		System.out.println("Answer to part 2: " + part2Count);
	}
	
	private static void displayBoard(List<char[]> mat) {
		for (var row : mat) System.out.println(Arrays.toString(row));
	}
	
	private static boolean isLooping(int[] pos, int[] nextPos, List<char[]> mat, int dir) {
		int m = mat.size(), n = mat.get(0).length;
		int originalR = nextPos[0], originalC = nextPos[1];
		int[] orP = new int[] {pos[0], pos[1]};
		mat.get(orP[0])[orP[1]] = getCharRep(dir);
		mat.get(nextPos[0])[nextPos[1]] = '#';
		
		boolean[][][] beenTo = new boolean[m][n][4];
		
		int tableInd = getTable(dir);
		beenTo[pos[0]][pos[1]][tableInd] = true;
		
		boolean finished = false;
		
		while (nextMoveInBounds(pos, dir, m, n) && !finished) {
			if (nextMoveBlocked(pos, dir, mat)) {
				dir = (dir + 1) % 4;
				tableInd = getTable(dir);
				continue;
			}
			nextPos = makeMove(pos, dir);
			
			if (beenTo[nextPos[0]][nextPos[1]][tableInd]) {
				finished = true;
			}
			else {
				beenTo[nextPos[0]][nextPos[1]][tableInd] = true;
				pos = nextPos;
			}
		}
		
		mat.get(orP[0])[orP[1]] = '.';
		mat.get(originalR)[originalC] = '.';
		
		return finished;
	}
	
	private static int[] makeMove(int[] pos, int dirInd) {
		int r = pos[0] + rDirs[dirInd], c = pos[1] + cDirs[dirInd];
		return new int[] {r, c};
	}
	
	private static boolean nextMoveBlocked(int[] pos, int dirInd, List<char[]> mat) {
		int r = pos[0] + rDirs[dirInd], c = pos[1] + cDirs[dirInd];
		
		return mat.get(r)[c] == '#';
	}
	
	private static boolean nextMoveInBounds(int[] pos, int dirInd, int m, int n) {
		int r = pos[0] + rDirs[dirInd];
		int c = pos[1] + cDirs[dirInd];
		
		return inBounds(r, c, m, n);
	}
	
	private static boolean inBounds(int r, int c, int m, int n) {
		return r >= 0 &&
			   r <  m &&
			   c >= 0 &&
			   c <  n;
	}
	
	public static int getDirInd(char c) {
		if (c == '^') return 0;
		else if (c == '>') return 1;
		else if (c == 'v') return 2;
		else return 3;
	}
	
	public static char getCharRep(int dir) {
		if (dir == 0) return '^';
		else if (dir == 1) return '>';
		else if (dir == 2) return 'v';
		else return '<';
	}
	
	private static int getTable(int dirInd) {
		return dirInd;
	}
	
	private static List<char[]> readInInput() {
		List<char[]> mat = new ArrayList<>();
		
		try(Scanner scan = new Scanner(input)){
			while (scan.hasNext()) mat.add(scan.nextLine().toCharArray());
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return mat;
	}

}
