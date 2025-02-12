package Week1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;;

public class FindLocationSum {
	
	final static String filePath = "InputText.txt";
	final static File input = new File(filePath);

	public static void main(String[] args) throws Exception {
		
		List<List<Long>> lists = new ArrayList<List<Long>>();
		
		Map<Long, Integer> occurs = new HashMap<>();
		
		try(Scanner inputReader = new Scanner(input)){
			while (inputReader.hasNext()) {
				String line = inputReader.nextLine();
				String[] nums = line.split(" +");
				for (int ind = 0; ind < nums.length; ind++) {
					
					if (lists.size() == ind) lists.add(new ArrayList<Long>());
					lists.get(ind).add(Long.parseLong(nums[ind]));
					if (ind == 1) addNumToMap(Long.parseLong(nums[ind]), occurs);
				}
			}
		}
		catch(Exception e) {
			System.out.println("Something went wrong!");
			e.printStackTrace();
			System.exit(1);
		}
		
		long totalAmount = 0;
		int len = lists.get(0).size();
		List<Long> a = lists.get(0);
		List<Long> b = lists.get(1);
		for (int i = 0; i < len; i++) {
			totalAmount += a.get(i) * occurs.getOrDefault(a.get(i), 0);
		}
		
		System.out.println(totalAmount);
	}
	
	private static void addNumToMap(Long num, Map<Long, Integer> map) {
		map.put(num, map.getOrDefault(num,  0) + 1);
	}

}
