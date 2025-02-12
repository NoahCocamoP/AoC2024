package Week2;

import java.util.HashSet;

public class TestOutp {
	
	public static void main(String[] args) {
		
		int mod = 94, add = 22;
		
		HashSet<Integer> seen = new HashSet<Integer>();
		
		int total = add, modTotal = add % mod;
		int iteration = 0;
		
		while (!seen.contains(modTotal)) {
			seen.add(modTotal);
			iteration++;
			System.out.println(modTotal + " on the " + iteration + " iteration. Total is " + total);
			total += add;
			modTotal = total % mod;
		}
		
		System.out.println("Encountered " + modTotal + " before. Stopping now.");
		System.out.println("Total == " + total);
		
	}
}
