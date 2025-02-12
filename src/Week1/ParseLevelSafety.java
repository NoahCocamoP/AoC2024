package Week1;

import java.io.*;
import java.util.*;

public class ParseLevelSafety {
	
	public static final String filePath = "InputText.txt";
	public static final File inputData = new File(filePath);
	public static final long MAX_DIFF = 3;
	public static final long MIN_DIFF = 1;
	public static void main(String[] args) throws FileNotFoundException {
		List<List<Long>> parsed = parseData();
		long totalSafeRows = findSafeRows(parsed);
		System.out.println(totalSafeRows);
	}
	
	private static boolean isSafeRow(List<Long> row, int skips) {
		int len = row.size();
		
		if (len - skips <= 1) return true;
		
		int[][][] canMakeSafe = new int[len][len][skips + 1];
		
		for (int i = len - 2; i > -1; i--) {
			
			for (int j = i + 1; j < len && (j - i - 1) <= skips; j++) {
				long currNum = row.get(i);
				long nextNum = row.get(j);
				long diff = currNum - nextNum;
				int resWouldBe = diff < 0 ? -1 : 1;
				if (Math.abs(diff) > MAX_DIFF || Math.abs(diff) < MIN_DIFF) {
					continue;
				}
				
				for (int skipsLeft = skips - (j - i - 1); skipsLeft > -1; skipsLeft--) {
					
					if (j + skipsLeft + 1 >= len) {
						canMakeSafe[i][j][skipsLeft] = resWouldBe;
						continue;
					}
					
					for (int k = j + 1, thisSkip = skipsLeft; thisSkip > -1 && k < len; thisSkip--, k++) {
						if (resWouldBe == canMakeSafe[j][k][thisSkip]) {
							canMakeSafe[i][j][skipsLeft] = resWouldBe;
							break;
						}
					}
				}
			}
		}
		
		for (int skipsInd = skips; skipsInd > -1 && skips - skipsInd < len; skipsInd--) {
			int startInd = (skips - skipsInd);
			
			for (int remSkips = skipsInd; remSkips > -1; remSkips--) {
				
				int otherInd = startInd + 1 + (skipsInd - remSkips);
				int res = canMakeSafe[startInd][otherInd][remSkips];
				if (res == 1 ||
					res == -1) return true;
			}
		}
		
		return false;
	}
	
	private static long findSafeRows(List<List<Long>> data) {
		long safeRows = 0;
		
		for (var row : data) {
			safeRows += isSafeRow(row, 1) ? 1 : 0; 
		}
		
		return safeRows;
	}
	
	private static List<List<Long>> parseData() throws FileNotFoundException{
		List<List<Long>> outp = new ArrayList<>();
		
		try(Scanner stream = new Scanner(inputData)){
			while (stream.hasNext()) {
				String unparsed = stream.nextLine();
				String[] nums = unparsed.split(" +");
				List<Long> report = new ArrayList<>();
				
				for (var num : nums) report.add(Long.parseLong(num));
				
				outp.add(report);
			}
		}
		catch(FileNotFoundException e) {
			throw e;
		}
		
		return outp;
	}

}
