package Week2;

import java.io.*;
import java.util.*;

public class Day11 {
	
	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	public static HashMap<state, Long> memo;
	
	public static void main(String[] args){
		memo = new HashMap<>();
		List<String> stones = readIn();
		calculateTotal(stones, 75);
	}
	
	private static String[] splitInTwo(String val) {
		StringBuilder sb = new StringBuilder();
		String[] twoParts = new String[2];
		
		int len = val.length();
		int half = len / 2;
		
		for (int i = 0; i < half; i++) sb.append(val.charAt(i));
		
		twoParts[0] = Long.toString(Long.parseLong(sb.toString()));
		
		sb = new StringBuilder();
		
		for (int i = half; i < len; i++) {
			sb.append(val.charAt(i));
		}
		twoParts[1] = Long.toString(Long.parseLong(sb.toString()));
		
		return twoParts;
	}
	
	private static long countTotal(String stoneVal, int blinks) {
		if (blinks == 0) return 1;
		state key = new state(stoneVal, blinks);
		
		if (memo.containsKey(key)) return memo.get(key);
		long res = 0;
		if (stoneVal.equals("0")) {
			res = countTotal("1", blinks - 1);
		}
		else if ((stoneVal.length() % 2) == 0) {
			
			String[] twoParts = splitInTwo(stoneVal);
			res = countTotal(twoParts[0], blinks - 1) + countTotal(twoParts[1], blinks - 1);
		}
		else {
			String newVal = Long.toString(Long.parseLong(stoneVal) * 2024);
			res = countTotal(newVal, blinks - 1);
		}
		
		memo.put(key, res);
		return res;
	}
	
	private static void calculateTotal(List<String> stones, int times) {
		long totalStones = 0;
		
		for (var stone : stones) {
			totalStones += countTotal(stone, times);
		}
		
		System.out.println("After " + times + " blinks, there are " + totalStones + " stones");
	}
	private static List<String> readIn(){
		List<String> outp = new ArrayList<String>();
		try(Scanner scan = new Scanner(input)){
			String stoneLine = scan.nextLine();
			String[] stones = stoneLine.split(" ");
			
			for (var stone : stones) {
				outp.add(stone);
			}
			
		}
		catch(FileNotFoundException e) {
			System.err.println("Something went wrong, file not found at path.");
			System.exit(1);
		}
		
		return outp;
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
