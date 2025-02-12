package Week2;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

class fileValue{
	
	public boolean isEmpty;
	public Long fId;
	
	public fileValue() {
		isEmpty = true;
	}
	
	public fileValue(long fId) {
		this.fId = fId;
		isEmpty = false;
	}
}

class memSpan {
	int start;
	int end;
	int numSpots;
	
	fileValue val;
	
	public memSpan(int s, int e, int nS, fileValue v) {
		start = s;
		end = e;
		numSpots = nS;
		val = v;
	}
}

public class FileFormatting {

	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	
	public static void main(String[] args) {
		String denseFile = readInInput();
		findSolution(denseFile.toCharArray());
	}
	
	private static fileValue[] decompress(char[] s) {
		List<fileValue> fValues = new ArrayList<>();
		
		long fId = 0;
		int ind = 0, len = s.length;
		
		while (ind < len) {
			int fileSpots = s[ind++] - '0';
			
			for (int i = 0; i < fileSpots; i++) fValues.add(new fileValue(fId));
			
			fId++;
			
			if (ind == len) break;
			
			int freeSpots = s[ind++] - '0';
			int start = fValues.size();
			int end = start + freeSpots - 1;
			
			for (int i = 0; i < freeSpots; i++) fValues.add(new fileValue());
		}
		
		fileValue[] outp = new fileValue[fValues.size()];
		
		for (int i = 0; i < fValues.size(); i++) {
			outp[i] = fValues.get(i);
		}
		return outp;
	}
	
	private static void swap(int l, int r, fileValue[] s) {
		var temp = s[l];
		s[l] = s[r];
		s[r] = temp;
	}
	
	private static boolean canFit(int[] window, int size) {
		return size <= (window[1] - window[0]);
	}
	
	private static int[] nextWindow(int l, int r, fileValue[] arr) {
		while (l < r && !arr[l].isEmpty) l++;
		
		if (l >= r) return new int[] {-1, -1};
		
		int endL = l;
		
		while (endL < r && arr[endL].isEmpty) endL++;
		
		return new int[] {l, endL};
	}
	
	private static void findSolution(char[] s) {
		fileValue[] converted = decompress(s);
		
		int len = converted.length;
		int r = len - 1;
		while (r > -1) {
			
			while (r > -1 && converted[r].isEmpty) r--;
			
			int endR = r;
			while (endR > -1 && !converted[endR].isEmpty && converted[endR].fId.equals(converted[r].fId)) endR--;
			
			int windowSize = r - endR;
			
			int l = 0;
			int[] window = nextWindow(l, endR + 1, converted);
			
			while (window[0] != -1 && !canFit(window, windowSize)) {
				l = window[1] + 1;
				window = nextWindow(l, endR + 1, converted);
			}
			
			if (window[0] != -1) {
				
				int wP = window[0];
				int rP = endR + 1;
				
				while (rP <= r) {
					swap(wP++, rP++, converted);
				}
			}
			r = endR;
		}
		
		BigInteger total = BigInteger.ZERO;
		
		for (int i = 0; i < len; i++) {
			if (converted[i].isEmpty) continue;
			total = total.add(BigInteger.valueOf((converted[i].fId) * (long)i));
		}
		
		System.out.println("Answer for part 2 is " + total);
	}
	
	private static String readInInput() {
		try (Scanner scan = new Scanner(input)) {return scan.nextLine();}
		catch (FileNotFoundException e) {
			System.err.println("File not found for the given path. Exiting now.");
			System.exit(1);
		}
		
		return "";
	}

}
