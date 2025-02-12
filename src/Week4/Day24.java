package Week4;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

record targAndOp(String targ, int op) {
	
}

record inputSide(String a, String b, int op) {
	
}
public class Day24 {
	public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    
    public static HashMap<String, Integer> vals;
    public static HashMap<String, Set<targAndOp>> adjMap; 
    
    
    public static HashMap<String, inputSide> reverseDir;
    
    public static long goalVal = 0;

	public static void main(String[] args) {
		part1();

	}
	
	public static void part1() {
		Scanner scan = null;
		vals = new HashMap<>();
		adjMap = new HashMap<>();
		reverseDir = new HashMap<>();
		
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			System.err.println("File not found at specified path. Exiting now.");
			System.exit(1);
		}
		
		String line = "";
		
		while (!(line = scan.nextLine()).isEmpty()) {
			setValue(line);
		}
		
		while (scan.hasNext()) {
			line = scan.nextLine();
			processEdge(line);
		}
		
		
		if (passesTestCases()) {
			System.out.println("Passed all testcases. Exiting now.");
			System.exit(0);
		}
		
		List<Entry<String, inputSide>> seen = new ArrayList<>();
		List<Entry<String, inputSide>> reverseDirLis = new ArrayList<>();
		
		for (var e : reverseDir.entrySet()) reverseDirLis.add(e);
		trySwaps(seen, 1, reverseDirLis, 0);
	}
	
	private static boolean passesTestCases() {
		for (int shift = 25; shift < 44; shift++) {
			setVals(1L << shift, 1L);
			long xVal = 0, yVal = 0;
			for (int i = 44; i > -1; i--) {
				String keyX = "x" + (i < 10 ? "0" : "") +  i;
				String keyY = "y" +(i < 10 ? "0" : "") + i;
				
				if (!vals.containsKey(keyX)) continue;
				xVal *= 2;
				xVal += vals.get(keyX);
				yVal *= 2;
				yVal += vals.get(keyY);
				if (!vals.containsKey(keyX) || !vals.containsKey(keyY)) System.out.println("Missing key.");
			}
			
			
			
			long targetZVal = xVal + yVal;
			
			goalVal = targetZVal;
			
			
			if (!computeAnswer(null)) {
				System.out.println("Failed on shift " + shift + " with a sum of " + targetZVal);
				return false;
			}
		}
		
		return true;
		
	}
	
	private static void setVals(long x, long y) {
		for (int i = 0; i < 45; i++) {
			String key = "x" + (i < 10 ? "0" : "") + i;
			int val = (x & (1L << i)) > 0 ? 1 : 0;
			vals.put(key, val);
		}
		for (int i = 0; i < 45; i++) {
			String key = "y" + (i < 10 ? "0" : "") + i;
			int val = (y & (1L << i)) > 0 ? 1 : 0;
			vals.put(key, val);
		}
	}
	
	private static boolean trySwaps(List<Entry<String, inputSide>> seen, int remaining, List<Entry<String, inputSide>> reverseDirLis, int ind) {
		if (remaining == 0) {
			return computeAnswer(seen);
		}
		else if (ind == reverseDirLis.size()) return false;
		int badInRow = 0;
		for (int aInd = ind; aInd < reverseDirLis.size(); aInd++) {
			Entry<String, inputSide> a = reverseDirLis.get(aInd);
			if (seen.contains(a)) continue;
			seen.add(a);
			String aKey = a.getKey();
			inputSide aVal = a.getValue();
			for (int bInd = aInd + 1; bInd < reverseDirLis.size() && badInRow < 10; bInd++) {
				Entry<String, inputSide> b = reverseDirLis.get(bInd);
				String bKey = b.getKey();
				inputSide bVal = b.getValue();
				if (a.equals(b) || seen.contains(b)) continue;
				if (aKey.equals(bVal.a()) ||
					aKey.equals(bVal.b()) ||
					bKey.equals(aVal.a()) ||
					bKey.equals(aVal.b())) continue;
				if (aVal.a().equals(bVal.a()) ||
					aVal.a().equals(bVal.b()) ||
					bVal.b().equals(aVal.b())) continue;
				seen.add(b);
				swap(a.getKey(), a.getValue(), b.getKey(), b.getValue(), aVal.op(), bVal.op());
				if (!computeAnswer(seen)) {
					swap(b.getKey(), a.getValue(), a.getKey(), b.getValue(), bVal.op(), aVal.op());
					seen.remove(b);
					badInRow++;
					continue;
				}
				if (!trySwaps(seen, remaining - 1, reverseDirLis, bInd + 1)) {
					badInRow++;
				}
				else badInRow = 0;
				swap(b.getKey(), a.getValue(), a.getKey(), b.getValue(), bVal.op(), aVal.op());
				seen.remove(b);
				
				if (badInRow == 10) break;
			}
			badInRow = 0;
			seen.remove(a);
		}
		
		return false;
	}
	private static void removeAndAdd(String inp, targAndOp toRemove, targAndOp toAdd) {
		//System.out.println("TO REMOVE: " + toRemove);
		//System.out.println("TO ADD: " + toAdd);
		if (!adjMap.get(inp).remove(toRemove)) {
			System.err.println("Adj map didn't contain element we were trying to remove. Exiting now.");
			System.out.println(inp);
			System.out.println(toRemove);
			System.exit(3);
		}
		if (!adjMap.get(inp).add(toAdd)) {
			System.err.println("Adj map already contained element we were trying to add. Exiting now.");
			System.out.println(toAdd);
			System.out.println(inp);
			System.exit(4);
		}
		
	}
	
	private static void swap(String outp1, inputSide inp1, String outp2, inputSide inp2, int op1, int op2) {
		//System.out.println("Trying to swap: " + inp1 + " and " + inp2);
		targAndOp key1 = new targAndOp(outp1, op1);
		targAndOp key2 = new targAndOp(outp2, op2);
		
		removeAndAdd(inp1.a(), key1, key2);
		removeAndAdd(inp1.b(), key1, key2);
		removeAndAdd(inp2.a(), key2, key1);
		removeAndAdd(inp2.b(), key2, key1);
	}
	
	private static boolean computeAnswer(List<Entry<String, inputSide>> swaps) {
		HashMap<String, Integer> newVals = new HashMap<>();
		
		for (var val : vals.entrySet()) {
			newVals.put(val.getKey(), val.getValue());
		}
		
		if (tSort(newVals, adjMap)) {
			long res = part1Res(newVals, swaps);
			if (swaps == null) System.out.println("Result of this was " + res);
			if (swaps == null) return res == goalVal ? true : false;
			else			   return res != -1 ? true : false;
		}
		else return false;
	}
	
	private static Long part1Res(HashMap<String, Integer> vals, List<Entry<String, inputSide>> swaps) {
		String key = "z";
		
		long sum = 0;
		int maxI = 65;
		for (int i = 64; i > -1; i--) {
			String thisKey = key;
			if (i < 10) thisKey = thisKey + "0";
			thisKey = thisKey + i;
			maxI = i;
			if (vals.containsKey(thisKey)) break;
			
		}
		String binaryRep = "";
		try {
			for (int i = maxI; i > -1; i--) {
				String thisKey = key;
				if (i < 10) thisKey = thisKey + "0";
				thisKey = thisKey + i;
				sum *= 2;
				sum += vals.get(thisKey);
				binaryRep = binaryRep + vals.get(thisKey);
			}
		}
		catch(Exception e) {
			//System.out.println("Something went wrong here. Exiting this one now.");
			return -1L;
		}
		if (sum == goalVal) {
			boolean foundVertex = true;
			if (swaps == null) swaps = new ArrayList<>();
			for (var s : swaps) {
				if (s.getKey().equals("khg")) foundVertex = true;
			}
			
			if (!foundVertex) return sum;
			System.out.println("FOUND SOLUTION WITH SWAPS: ");
			
			
			for (var s : swaps) {
				System.out.println(s);
			}
			
		}
		else return sum;
		
		return sum;
	}
	
	// comb numb : 51401618891888
	
	private static boolean tSort(HashMap<String, Integer> vals, HashMap<String, Set<targAndOp>> adjMap) {
		
		Queue<String> evaluated = new ArrayDeque<>();
		Set<String> seenBefore = new HashSet<>();
		
		for (var num : vals.entrySet()) {
			evaluated.offer(num.getKey());
			seenBefore.add(num.getKey());
		}
		
		int round = 0;
		while (evaluated.size() != 0) {
			
			int count = evaluated.size();
			round++;
			//System.out.println("For round " + round + " there are " + count + " upcoming elements.");
			while (count != 0) {
				if ((round % 101) == 0) {
					System.out.println("Excessive rounds detected. Returning now.");
					return false;
				}
				String curr = evaluated.poll();
		
				//System.out.println(curr + " on round " + round);
	
				count--;
				
				int val = vals.get(curr);
				if (!adjMap.containsKey(curr)) {
					continue;
				}
				
				for (var e : adjMap.get(curr)) {
					
					String targ = e.targ();
					int op = e.op();
					
					if (!vals.containsKey(targ)) {
						vals.put(targ, val);
						continue;
					}
					else if (!seenBefore.contains(targ)){
						seenBefore.add(targ);
						int thatVal = vals.get(targ);
						int resOp = performOp(thatVal, val, op);
						vals.put(targ, resOp);
						evaluated.offer(targ);
					}
					else {
						System.out.println("Somehow contributing to " + targ + " vertex for a 3rd time - exiting now.");
						return false;
					}
				}
			}
			
		}
		
		return true;
	}
	
	private static int performOp(int thatVal, int thisVal, int op) {
		if (op == 0) return thatVal & thisVal;
		else if (op == 1) return thatVal | thisVal;
		else 				return thatVal ^ thisVal;
	}
	private static int getOp(String inp) {
		if (inp.equals("AND")) return 0;
		else if (inp.equals("OR")) return 1;
		else if (inp.equals("XOR")) return 2;
		
		System.err.println("No valid op found for input " + inp + ". Exiting now.");
		System.exit(2);
		
		return -1;
	}
	
	private static void processEdge(String inp) {
		//System.out.println("Received edge " + inp);
		String[] parsed = inp.split(" ");
		int op = getOp(parsed[1]);
		String target = parsed[4];
		
		targAndOp edgeTo = new targAndOp(target, op);
		
		String from1 = parsed[0];
		String from2 = parsed[2];
		
		inputSide inpSide = new inputSide(from1, from2, op);
		
		adjMap.computeIfAbsent(from1, (a) -> new HashSet<>()).add(edgeTo);
		adjMap.computeIfAbsent(from2, (a) -> new HashSet<>()).add(edgeTo);
		reverseDir.put(target, inpSide);
	}
	
	private static void setValue(String inp) {
		String[] parsed = inp.split(": ");
		
		vals.put(parsed[0], Integer.parseInt(parsed[1]));
	}

}
