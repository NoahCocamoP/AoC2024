package Week3;


import java.io.*;
import java.util.*;

/* Already defined
class Box{
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
		}
		else {
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

*/
public class OriginalDay15 {
	public static final String inputPath = "InputText.txt";
	public static final File input = new File(inputPath);
	public static HashMap<Integer, Box> boxMap;

	public static void main(String[] args) {
		Scanner scan = null;
		boxMap = new HashMap<>();
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			System.err.println("File not found at path, exiting now.");
			System.exit(1);
		}
		
		String line = null;
		List<char[]> primMat = new ArrayList<>();
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
		
		System.out.println("Max iterations == " + (Math.max(primMat.size(), primMat.get(0).length) * (long)moveSeq.length()));
		
		int[][] mat = new int[m][n * 2];
		
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
				}
				else if (id == 'O') {
					boxID++;
					
					Box toAdd = new Box(i, c, boxID);
					mat[i][c] = boxID;
					mat[i][c2] = boxID;
					
					boxMap.put(boxID, toAdd);
				}
				else if (id == '.') {
					mat[i][c] = 0;
					mat[i][c2] = 0;
				}
				else {
					mat[i][c] = -2;
					mat[i][c2] = 0;
				}
			}
		}
		
		performMoves(mat, moveSeq.toString().toCharArray());
		
		long countScore = getScore(mat);
		
		System.out.println("Ending score is " + countScore);
	}
	
	private static long getScore(int[][] mat) {
		int m = mat.length, n = mat[0].length;
		long total = 0;
		for (var entry : boxMap.entrySet()) {
			
			Box b = entry.getValue();
			
			total += (long)b.r * 100;
			total += b.c;
			
		}
		
		return total;
	}
	
	private static void performMoves(int[][] mat, char[] moves) {
		
		int m = mat.length, n = mat[0].length, mLen = moves.length;
		
		int r = -1, c = -1;
		
		for (int i = 0; i < m && r == -1; i++) {
			
			for (int j = 0; j < n && r == -1; j++) {
				
				if (mat[i][j] == -2) {
					r = i;
					c = j;
				}
			}
		}
		
		for (var move : moves) {
			//printArr(mat, move);
			int[] dir = getDir(move);
			int nextR = r + dir[0], nextC = c + dir[1];
			
			if (!inBounds(nextR, nextC, m, n) || mat[nextR][nextC] == -1) continue;
			Stack<Box> toMove = tryMove(nextR, nextC, dir, mat);
			if (toMove == null) continue;
			
			while (toMove.size() != 0) {
				Box moveThis = toMove.pop();
				moveThis.moveDir(dir, mat);
			}
			
			swap(mat, r, c, nextR, nextC);
			r = nextR;
			c = nextC;
		}
		
		//printArr(mat, 'x');
	}
	
	private static void printArr(int[][] mat, char c) {
		
		System.out.println();
		System.out.println("ARRAY AFTER MOVE: " + c);
		
		for (var row : mat) {
			System.out.println(Arrays.toString(row));
		}
	}
	
	private static int[] getDir(char c) {
		if (c == '^') return new int[] {-1, 0};
		else if (c == 'v') return new int[] {1, 0};
		else if (c == '>' || c == '[') return new int[] {0, 1};
		else 			   return new int[] {0, -1};
	}
	
	private static boolean inBounds(int r, int c, int m, int n) {
		return r >= 0 &&
				r < m &&
				c >= 0 &&
				c < n;
	}
	
	private static Stack<Box> tryMove(int r, int c, int[] dir, int[][] mat) {
		Stack<Box> res = new Stack<Box>();
		Queue<Box> bfs = new ArrayDeque<Box>();
		Set<Box> addedAlready = new HashSet<>();
		int m = mat.length, n = mat[0].length;
		
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
	
	private static void swap(int[][] mat, int r, int c, int x, int y) {
		var temp = mat[r][c];
		mat[r][c] = mat[x][y];
		mat[x][y] = temp;
	}

}