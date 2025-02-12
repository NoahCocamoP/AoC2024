package Week3;

import java.io.*;
import java.util.*;

record state(long val, long days) {}

public class LanternFish {
	public static final String inputPath = "InputText.txt";
	public static final File input = new File(inputPath);
	public static HashMap<state, Long> memo;
	
	public static final long BASE_RESET = 6L;
	public static final long EXTRA = 2L;
	public static final long TOTAL_DAYS = 256L;
	public static void main(String[] args) {
		Scanner scan = null;
		memo = new HashMap<>();
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			System.err.println("Couldn't find the file at that path. Not good, returning now.");
			System.exit(1);
		}
		
		StringBuilder sb = new StringBuilder();
		
		while (scan.hasNext()) sb.append(scan.nextLine());
		String[] vals = sb.toString().split(",");
		
		long total = 0;
		
		for (var val : vals) {
			System.out.println("Found " + val);
			long startVal = Long.parseLong(val);
			state startState = new state(startVal, TOTAL_DAYS);
			total += findDP(startState);
		}
		
		System.out.println("After " + TOTAL_DAYS + " days, there are a sum of " + total + " lanternfish.");
	
	}
	
	private static long findDP(state curr) {
		if (memo.containsKey(curr)) return memo.get(curr);
		else if (curr.days() == 0) return 1L;
		
		long totalFish = 0;
		
		long nextDays = curr.days() - 1;
		
		if (curr.val() == 0) {
			
			state fish1 = new state(BASE_RESET, nextDays);
			totalFish += findDP(fish1);
			state fish2 = new state(BASE_RESET + EXTRA, nextDays);
			totalFish += findDP(fish2);
		}
		else {
			state nextFish = new state(curr.val() - 1, nextDays);
			totalFish += findDP(nextFish);
		}
		
		memo.put(curr, totalFish);
		
		return totalFish;
	}

}
