package Week3;

import java.util.*;
import java.io.*;

record dpState(int dInd1, int dInd2, int nInd, int sInd) {}
public class Day21 {
	public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
   	public static final int[] directional = new int[] {-1, 0, Integer.MAX_VALUE, 1, 2, 3};
   	public static final char[] numerical = new char[] {'7', '8', '9', '4', '5', '6', '1', '2', '3', '.', '0', 'A'};
   	public static final int dLen = directional.length;
   	public static final int nLen = numerical.length;
   	public static Map<dpState, Long> totalPresses;
   	public static int[] dirs = new int[]{-3, -1, 3, 1};
    
    
	public static void main(String[] args) {
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
		
		System.out.println(total);
	}
	
	private static dpState calculatePress(dpState curr, int level, char[] toMatch) {
		if (level == 1 && directional[curr.dInd2()] == Integer.MAX_VALUE) {
			//System.out.println("Character to match: " + toMatch[curr.sInd()]);
			//System.out.println("Character at numpad: " + numerical[curr.nInd()]);
			if (toMatch[curr.sInd()] == numerical[curr.nInd()]) {
				//System.out.println("FOUND MATCH: " + curr);
				return new dpState(curr.dInd1(), curr.dInd2(), curr.nInd(), curr.sInd() + 1);
			}
			else return null;
		}
		else if (level == 1) {
			
			int nInd = curr.nInd() + dirs[directional[curr.dInd2()]];
			int dir = dirs[directional[curr.dInd2()]];
			if (nInd < 0 || nInd >= nLen || numerical[nInd] == '.') return null;
			if (dir == 1 && (nInd == 3 || nInd == 6)) return null;
			if (dir == -1 && (nInd == 2 || nInd == 5)) return null;
			dpState res = new dpState(curr.dInd1(), curr.dInd2(), nInd, curr.sInd());
			//System.out.println("Moving num pad from " + curr.nInd() + " to " + nInd);
			
			return res;
		}
		else {
			
			if (directional[curr.dInd1()] == Integer.MAX_VALUE) {
				
				return calculatePress(curr, level + 1, toMatch);
			}
			
			int d2Ind = curr.dInd2() + dirs[directional[curr.dInd1()]];
			int dir = dirs[directional[curr.dInd1()]];
			if (d2Ind < 0 || d2Ind >= dLen || directional[d2Ind] == -1) return null;
			if (dir == 1 && d2Ind == 3) return null;
			if (dir == -1 && d2Ind == 2) return null;
			
			return new dpState(curr.dInd1(), d2Ind, curr.nInd(), curr.sInd());
		}
		
	}
	
	
	private static long doDP(String goalCode) {
		totalPresses = new HashMap<>();
		int strLen = goalCode.length();
		char[] toMatch = goalCode.toCharArray();
		long pressCount = -1;
		
		List<dpState> starts = new ArrayList<>();
		
		starts.add(new dpState(2, 2, nLen - 1, 0));
		
		
		Queue<dpState> q = new ArrayDeque<>();
		
		for (var s : starts) {
			q.offer(s);
		}
		
		boolean foundEnd = false;
		while (q.size() != 0 && !foundEnd) {
			
			pressCount++;
			
			System.out.println("total presses now " + pressCount);
			int count = q.size();
			
			while (count != 0 && !foundEnd) {
				dpState curr = q.poll();
				count--;
				
				//System.out.println("trying state " + curr);
				if (curr.sInd() == toMatch.length) {
					foundEnd = true;
					break;
				}
				
				for (var dir : dirs) {
					
					int nextDInd1 = curr.dInd1() + dir;
					
					if (nextDInd1 < 0 || nextDInd1 >= dLen || directional[nextDInd1] == -1) continue;
					if (dir == 1 && nextDInd1 == 3) continue;
					if (dir == -1 && nextDInd1 == 2) continue;
					
					dpState next = new dpState(nextDInd1, curr.dInd2(), curr.nInd(), curr.sInd());
					
					if (totalPresses.containsKey(next)) continue;
					
					//System.out.println("Moving d ind from " + curr.dInd1() + " to " + nextDInd1);
					totalPresses.put(next,  pressCount + 1);
					q.offer(next);
				}
				
				dpState press = calculatePress(curr, 0, toMatch);
				if (press == null) continue;
				if (totalPresses.containsKey(press)) continue;
				totalPresses.put(press, pressCount + 1);
				q.offer(press);
			}
		}
		
		long numericPortion = 0;
		
		int ind = 0;
		while (ind < strLen && Character.isDigit(toMatch[ind])) {
			numericPortion *= 10;
			numericPortion += toMatch[ind++] - '0';
		}
		
		long res = numericPortion * pressCount;
		
		System.out.println("For " + goalCode + " result was " + res + " with total presses == " + pressCount + " and numeric portion == " + numericPortion);
		return res;
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
