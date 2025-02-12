package Week1;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class ParseCorruptedMath {
	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	public static final String regex = "mul\\([0-9]+,[0-9]+\\)|do\\(\\)|don't\\(\\)";
	public static final String DO = "do()";
	public static final String DONT = "don't()";
	public static void main(String[] args) throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		
		try(Scanner scan = new Scanner(input)){
			
			while (scan.hasNext()) sb.append(scan.nextLine());
		}
		
		String inputString = sb.toString();
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(inputString);
		
		long totalSumOfOps = 0;
		boolean addMultis = true;
		while (m.find()) {
			String group = m.group();
			if (group.equals(DO)) addMultis = true;
			else if (group.equals(DONT)) addMultis = false;
			else if (addMultis) totalSumOfOps += performOp(group);
		}
		
		System.out.println(totalSumOfOps);

	}
	
	private static long performOp(String operation) {
		long num1 = 0, num2 = 0;
		
		int startInd = 4, len = operation.length();
		
		while (startInd < len && operation.charAt(startInd) != ',') {
			num1 *= 10;
			num1 += operation.charAt(startInd++) - '0';
		}
		startInd++;
		while (startInd < len && operation.charAt(startInd) != ')') {
			num2 *= 10;
			num2 += operation.charAt(startInd++) - '0';
		}
		
		return num1 * num2;
	}

}
