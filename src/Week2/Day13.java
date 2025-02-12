package Week2;

import java.io.*;
import java.util.*;

record State(long x, long y) {}
public class Day13 {

	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	public static final int MAX_PRESS = 100;
	
	public static void main(String[] args) {
		double before = System.currentTimeMillis();
		doSomethingWithLine();
		double after = System.currentTimeMillis();
		
		System.out.println("That took " + (after - before) + " ms");

	}
	
	private static long[] parseButton(String button) {
		String[] split = button.split(":");
		
		String coords = split[1];
		//System.out.println("Coords: " + coords);
		
		long x = 0, y = 0;
		int ind = 0, len = coords.length();
		
		while (coords.charAt(ind) != '+' && coords.charAt(ind) != '=') ind++;
		
		ind++;
		
		while (coords.charAt(ind) != ',') {
			x *= 10;
			x += coords.charAt(ind) - '0';
			ind++;
		}
		
		ind++;
		
		while (coords.charAt(ind) != '+' && coords.charAt(ind) != '=') ind++;
		
		ind++;
		
		while (ind < len) {
			y *= 10;
			y += coords.charAt(ind++) - '0';
		}
		
		return new long[] {x, y};
	}
	
	private static HashMap<State, Long> getModVals(long[] mods, long[] adds){
		HashMap<State, Long> mp = new HashMap<>();
		
		Long r = 0L, c = 0L;
		State curr = new State(r, c);
		long add = 0;
		while (!mp.containsKey(curr)) {
			mp.put(curr, add);
			r += adds[0];
			c += adds[1];
			r %= mods[0];
			c %= mods[1];
			curr = new State(r, c);
			add++;
		}
		
		//System.out.println("For mods " + Arrays.toString(mods) + " and adds " + Arrays.toString(adds) + " a total of " + mp.size() + " states were found before a repeat");
		return mp;
	}
	
	private static long bSearch(long baseSecond, long secondIncrement, long[] firstButton, long[] secondButton, long firstCost, long secondCost, long[] dest) {
		
		long lo = 0, hi = (long)Integer.MAX_VALUE * 100;
		
		//System.out.println("Trying a BSearch with a baseSecond of " + baseSecond + " presses, and an increment of " + secondIncrement + " presses.");
		boolean colIncreasing = isIncreasing(baseSecond, secondIncrement, dest, secondButton, firstButton);
		while (lo <= hi) {
			long mid = lo + (hi - lo) / 2;
			
			//System.out.println("Trying mid = " + mid);
			long secondPresses = baseSecond + (secondIncrement * mid);
			long[] firstPresses = pressesAt(mid, baseSecond, secondIncrement, dest, secondButton, firstButton);
			if (firstPresses[0] < 0 || firstPresses[1] < 0) {
				//System.out.println("Too many second button presses");
				hi = mid - 1;
				continue;
			}
			//System.out.println("First press array: " + Arrays.toString(firstPresses));
			if (firstPresses[0] < firstPresses[1]) {
				
				if (colIncreasing) hi = mid - 1;
				else lo = mid + 1;
			}
			else if (firstPresses[0] > firstPresses[1]) {
				if (colIncreasing) lo = mid + 1;
				else 			   hi = mid - 1;
			}
			else {
				//System.out.println("Found something!");
				return (secondPresses * firstCost) +
					   (firstPresses[0] * secondCost);
			}
		}
		//System.out.println("Found nothing.");
		return Long.MAX_VALUE;
	}
	
	private static long bestPressFirst(long[] pressSecond, long[] pressFirst, long secondCost, long firstCost, long[] dest) {
		
		HashMap<State, Long> modValsX = getModVals(pressSecond, pressFirst);
		
		
		long xKey = dest[0] % pressSecond[0], yKey = dest[1] % pressSecond[1];
		
		State key = new State(xKey, yKey);
		
		if (modValsX.containsKey(key)) {
			
			return bSearch(modValsX.get(key), modValsX.size(), pressFirst, pressSecond, firstCost, secondCost, dest);
		}
		//else System.out.println("Found nothing.");
		
		return 0;
		
	}
	
	private static long[] pressesAt(long mid, long baseSecond, long incrementSecond, long[] dest, long[] firstPress, long[] secondPress) {
		long secondPresses = baseSecond + (mid * incrementSecond);
		
		//System.out.println("The press at the second button (" + Arrays.toString(secondPress) + ") pressed a total of " + secondPresses + " times");
		long[] destAfter = new long[] {dest[0] - (secondPress[0] * secondPresses), dest[1] - (secondPress[1] * secondPresses)};
		
		long[] firstPresses = new long[] {destAfter[0] / firstPress[0], destAfter[1] / firstPress[1]};
		
		return firstPresses;
		
	}
	
	private static boolean isIncreasing(long incrementSecond, long amountSecond, long[] dest, long[] firstPress, long[] secondPress) {
		long[] res1 = pressesAt(0, incrementSecond, amountSecond, dest, firstPress, secondPress);
		long[] res2 = pressesAt(1, incrementSecond, amountSecond, dest, firstPress, secondPress);
		boolean returnVal = res1[1] < res2[1];
		
		//System.out.println("Mid = 0 -- " + Arrays.toString(res1));
		//System.out.println("Mid = 1 -- " + Arrays.toString(res2));
		
		//System.out.println("Returning " + returnVal);
		return returnVal;
				
	}

	
	
	public static long doSomething(String aS, String bS, String prize, long aCost, long bCost) {
		long[] buttonA = parseButton(aS);
		long[] buttonB = parseButton(bS);
		
		long[] dest = parseButton(prize);
		
		dest[0] += 10000000000000L;
		dest[1] += 10000000000000L;
		
		
		
		//System.out.println("Button A: " + Arrays.toString(buttonA));
		//System.out.println("Button B: " + Arrays.toString(buttonB));
		//System.out.println("Prize at: " + Arrays.toString(dest));
		
		
		
	
		
		long minCost = Long.MAX_VALUE;
		
		
		minCost = Math.min(minCost,  Math.min(bestPressFirst(buttonA, buttonB, aCost, bCost, dest), bestPressFirst(buttonB, buttonA, bCost, aCost, dest)));
		
		return minCost == Long.MAX_VALUE ? 0 : minCost;	
		
	}
	
	
	public static void doSomethingWithLine() {
		
		long total = 0;
		
		try(Scanner scan = new Scanner(input)){
			
			while (scan.hasNext()) {
				String buttonA = scan.nextLine();
				String buttonB = scan.nextLine();
				String prize = scan.nextLine();
				
				if (scan.hasNext()) scan.nextLine(); // clear whitespace
				
				long resThis = doSomething(buttonA, buttonB, prize, 3, 1);
				//System.out.println("Total score for that is " + resThis);
				total += resThis;
			}
		}
		catch (FileNotFoundException e) {
			System.err.println("That's not good, file not found. Exiting now.");
			System.exit(1);
		}
		
		System.out.println("Total min cost for avail prizes is: " + total);
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
