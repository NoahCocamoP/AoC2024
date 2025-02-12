package Week2;

import java.io.*;
import java.util.*;

public class TrackAntinodes {
	

	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	
	public static void main(String[] args) {
		char[][] grid = readInInput();
		solveInput(grid);
	}
	
	public static int[] stepForward(int[] pos, int[] dir) {
		return new int[] {pos[0] + dir[0],
						  pos[1] + dir[1]
					     };
	}
	
	public static int[] stepBackwards(int[] pos, int[] dir) {
		return new int[] {pos[0] - dir[0],
						 pos[1] - dir[1]
						};
	}
	
	public static boolean tryAddNode(int[] pos, boolean[][] mat) {
		int m = mat.length, n = mat[0].length;
		int r = pos[0], c = pos[1];
		
		if (!(
			r >= 0 &&
			r < m &&
			c >= 0 &&
			c < n)) return false;
		mat[r][c] = true;
		
		return true;
	}
	public static void markAntiNodes(boolean[][] hasAnti, List<int[]> positions) {
		int m = hasAnti.length, n = hasAnti[0].length;
		int len = positions.size();
		
		for (int i = 0; i < len; i++) {
			int[] pos1 = positions.get(i);
			for (int j = i + 1; j < len; j++) {
				int[] pos2 = positions.get(j);
				
				int[] dir = new int[] {pos1[0] - pos2[0], pos1[1] - pos2[1]};
				
				int[] antiNode1 = stepForward(pos1,dir);
				int[] antiNode2 = stepBackwards(pos2,dir);
				
				/*
				 * System.out.println("For the pairing " + Arrays.toString(pos1) + " and " + Arrays.toString(pos2));
				 * System.out.println("There are antiNodes are positions " + Arrays.toString(antiNode1) + " - " + Arrays.toString(antiNode2));
				 */
				
				
				tryAddNodeForward(pos1, dir, hasAnti);
				tryAddNodeBackward(pos2, dir, hasAnti);
			}
		}
	}
	
	private static void tryAddNodeForward(int[] pos, int[] dir, boolean[][] hasAnti) {
		while (tryAddNode(pos, hasAnti)) {
			pos = stepForward(pos, dir);
		}
	}
	
	private static void tryAddNodeBackward(int[] pos, int[] dir, boolean[][] hasAnti) {
		while (tryAddNode(pos, hasAnti)) {
			pos = stepBackwards(pos, dir);
		}
	}
	
	private static void solveInput(char[][] grid) {
		
		int m = grid.length, n = grid[0].length;
		
		HashMap<Character, List<int[]>> posMap = new HashMap<>();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n ; j++) {
				
				char c = grid[i][j];
				if (!Character.isLetterOrDigit(c)) continue;
				int[] pos = new int[] {i, j};
				if (!posMap.containsKey(c)) posMap.put(c, new ArrayList<>());
				posMap.get(c).add(pos);
			}
		}
		
		boolean[][] hasAntiNode = new boolean[m][n];
		
		for (var entry : posMap.entrySet()) {
			if (entry.getValue().size() < 2) continue;
			
			markAntiNodes(hasAntiNode, entry.getValue());
		}
		
		long total = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				
				total += hasAntiNode[i][j] ? 1 : 0;
			}
		}
		
		System.out.println(total + " unique spots have anti nodes in the graph");
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
