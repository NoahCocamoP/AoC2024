package Week1;

import java.io.*;
import java.util.*;

public class FindEquations {
	
	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);

	public static void main(String[] args) {
		long total = 0;
		try(Scanner scan = new Scanner(input)){
			while (scan.hasNext()) total += parseEquation(scan.nextLine());
		}
		catch (FileNotFoundException e) {
			System.err.println("File cannot be found from given path!");
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Total sum of valid equations is " + total);
	}
	
	private static long parseEquation(String eq) {
		System.out.println("Receieved line: " + eq);
		String[] split1 = eq.split(":");
		String[] nums = split1[1].split(" ");
		Long parsedFinal = Long.parseLong(split1[0]);
		Long multSum = 0L;
		Long addSum = 0L;
		List<Long> parsedNums = new ArrayList<>();
		
		for (var num : nums) {
			if (num == null || num.isEmpty()) continue;
			Long parsed = Long.parseLong(num);
			addSum += parsed;
			if (multSum == 0) multSum = parsed;
			else multSum *= parsed;
			
			parsedNums.add(parsed);
		}
		
		//if (multSum < parsedFinal || addSum > parsedFinal) return 0;
		
		if (canMake(parsedFinal, parsedNums)) return parsedFinal;
		else return 0;
	}
	
	private static boolean canMake(long num, List<Long> nums) {
		return tryMake(nums.get(0), 0, num, nums);
	}
	
	private static boolean tryMake(long total, int ind, long toMatch, List<Long> nums) {
		if (ind == nums.size() - 1) return total == toMatch;
		
		return tryMake(total + nums.get(ind + 1), ind + 1, toMatch, nums) ||
			   tryMake(total * nums.get(ind + 1), ind + 1, toMatch, nums) ||
			   tryMake(concatenate(total, nums.get(ind + 1)), ind + 1, toMatch, nums);
	}
	
	private static int countSpots(long num) {
		int spots = 0;
		while (num != 0) {
			spots++;
			num /= 10;
		}
		
		return spots;
	}
	
	private static long concatenate(long a, long b) {
		int numPlaces = countSpots(b);
		
		a *= (long)Math.pow(10, numPlaces);
		a += b;
		
		return a;
	}

}
