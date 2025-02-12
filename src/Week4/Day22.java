package Week4;

import java.util.*;
import java.io.*;

record FourDiff(int Diff4, int Diff3, int Diff2, int Diff1) {
	
	public FourDiff addDiff(int diff) {
		return new FourDiff(this.Diff3(), this.Diff2(), this.Diff1(), diff);
	}
}
public class Day22 {
	public static final long mod = 16777216;
	public static final int log2 = 24;
	public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    public static int LIFTNUM = 2000;
    
    // for part1 - bit lifting for some reason
    public static HashMap<Integer, Integer> nextIn = new HashMap<>();
    public static int[][] bitLift;
    
    // for part2 - part 1 approach tried to be too cute.
    public static List<HashMap<FourDiff, Integer>> secCombs;
    public static HashMap<FourDiff, Long> cumSum;
    
    
	public static void main(String[] args) {
		//part1();
		part2();
		
		
	}
	
	private static void processCombs(long start) {
		FourDiff curr = new FourDiff(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		int prevVal = (int) (start % 10);
		long currReal = start;
		
		Set<FourDiff> seen = new HashSet<FourDiff>();
		
		for (int i = 0; i < LIFTNUM; i++) {
			long nextReal = getNext(currReal);
			int thisVal = (int)(nextReal % 10);
			int thisDiff = thisVal - prevVal;
			
			prevVal = thisVal;
			currReal = nextReal;
			
			curr = curr.addDiff(thisDiff);
			if (curr.Diff4() == Integer.MAX_VALUE ||
				seen.contains(curr)) continue;
			
			seen.add(curr);
			cumSum.put(curr, cumSum.getOrDefault(curr, 0L) + thisVal);
		}
	}
	
	private static void part2() {
		
		Scanner scan = null;
		secCombs = new ArrayList<>();
		cumSum = new HashMap<>();
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			System.err.println("File not found at specified path. Exiting now.");
			System.exit(1);
		}
		
		while (scan.hasNext()) {
			processCombs(Long.parseLong(scan.nextLine()));
		}
		
		long maxRes = Long.MIN_VALUE;
		
		for (var e : cumSum.entrySet()) {
			maxRes = Math.max(maxRes, e.getValue());
		}
		
		System.out.println("The maximum sum is " + maxRes);
		
	}
	
	private static void part1() {
		double start = System.currentTimeMillis();
		for (int i = 0; i < mod; i++) {
			nextIn.put(i, getNext(i));
		}
		bitLift = new int[(int)mod][log2];
		
		for (int i = 0; i < mod; i++) {
			
			bitLift[i][0] = nextIn.get(i);
		}
		
		for (int i = 1; i < log2; i++) {
			
			for (int num = 0; num < mod; num++) {
				int prev = bitLift[num][i - 1];
				bitLift[num][i] = bitLift[prev][i - 1];
			}
		}
		double end = System.currentTimeMillis() - start;
		
		System.out.println("Took " + end + " milliseconds or about " + (end / 1000) + " seconds");
		
		
		Scanner scan = null;
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			System.err.println("File not found at specified path. Exiting now.");
		}
		long total = 0;
		
		
		while (scan.hasNext()) {
			total += findSecret(Long.parseLong(scan.nextLine()));
		}
		
		System.out.println("Total secret sum is " + total);
	}
	
	private static int findSecret(long num) {
		
		int lifting = LIFTNUM - 1;
		
		num = getNext(num);
		
		int numInd = (int)num;
		
		for (int i = log2 - 1; i > -1; i--) {
			if ((lifting & (1 << i)) > 0) {
				lifting -= 1 << i;
				numInd = bitLift[numInd][i];
			}
		}
		
		System.out.println("After " + LIFTNUM + " shifts, " + num + " now equals " + numInd);
		return numInd;
	}
	
	private static int getNext(long i) {
		i = mix(i, i * 64L);
		i = prune(i);
		
		i = mix(i, i / 32);
		i = prune(i);
		
		i = mix (i, i * 2048L);
		i = prune(i);
		
		return (int)i;
	}
	
	private static long mix(long a, long b) {
		return a ^ b;
	}
	
	private static long prune(long a) {
		return a % mod;
	}

}
