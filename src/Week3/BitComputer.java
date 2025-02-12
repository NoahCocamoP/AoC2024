package Week3;

import java.io.*;
import java.util.*;

record ProgramRes(long origA, String outp, int passScore) {}

public class BitComputer {
	
	public static long A;
	public static long B;
	public static long C;
	public static final String filePath = "InputText.txt";
	public static final File input = new File(filePath);
	public static final File outputFile = new File("OutputText.txt");
	public static Set<Long> usedAlready = new HashSet<>();
	public static List<ProgramRes> greatScores = new ArrayList<ProgramRes>();
	public static boolean inUse = false;
	public static StringBuilder sb = new StringBuilder();
	
	List<Long> workingAVals = new ArrayList<Long>();
	
	public static Integer iPointer;
	// Who knows what these do at this point
	public static int prevLen = 0;
	public static int threadInd = 1;
	public static int MAX_SCORE = -1;
	public static int globalIter = 0;
	
	// Don't worry about it
	public static Thread t;
	
	
	public static Long MIN_VALID = Long.MAX_VALUE;
	public static void main(String[] args) {
		Scanner scan = null;
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			System.err.println("File not found, not good. Exiting now.");
			System.exit(1);
		}
		
		/*
		 * Changing System.out 
		try {
			System.setOut(new PrintStream(outputFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Couldn't set out stream");
			System.exit(1);
		}
		*/
		
		A = parseVal(scan);
		B = parseVal(scan);
		C = parseVal(scan);
		
		scan.nextLine();
	
		char[] program = getProgram(scan);
		
		/* Attempt at brute force with many threads computing chunks
		long start = (long) 216584205979245L;
		long parts = 10000;
		long aPart = 0;
		
		List<Thread> threads = new ArrayList<Thread>();
		for (; threadInd < parts; threadInd++) {
				t = new Thread(() -> {
				for (long ind = (long)Math.pow(8, 14) * threadInd; ind < (long)Math.pow(8,  14) * (threadInd + 1); ind++) {
	 				RESET_PROGRAM(ind);
					ProgramRes res = RUN_PROGRAM(program, ind, 0, 0);
					
					if (res.passScore() > 13) t.setPriority(Thread.MAX_PRIORITY);
					if (res.passScore() == -1) break;
					if (res.passScore() > 15) System.out.println("Novel score " + res);
					if (res.passScore() <  3) {
						ind += new Random().nextInt(100000);
						continue;
					}
					
				}
				int threadVal = threadInd;
				//System.out.println("Finished thread " + threadVal);
			});
			threads.add(t);
			t.start();
		}
		boolean notFinished = true;
		while (notFinished) {
			
			notFinished = false;
			for (var thread : threads) {
				if (thread.isAlive()) {
					notFinished = true;
					break;
				}
			}
		}
		System.exit(1);
		*/
		
		int[] setBits = new int[3 * program.length];
		Arrays.fill(setBits, -1);
		
		findMinCost(setBits, 0, program);
		
		System.out.println("Min cost found: " + MIN_VALID);
		
		for (long start = 202797954918051L; start < 202797954918052L; start++) {
			RESET_PROGRAM(start);
			
			ProgramRes res = RUN_PROGRAM(program, start, 0, 0);
			System.out.println(start + " -- " + res);
		}
		
		iPointer = 0;
		long currAVal = 1L;
		long endVal = currAVal;
		PriorityQueue<ProgramRes> pq = new PriorityQueue<ProgramRes>((a, b) -> {
			return Integer.compare(b.passScore(), a.passScore());
		});
		RESET_PROGRAM(currAVal);
		pq.offer(RUN_PROGRAM(program, currAVal, 0 ,0));
		int interations = 0;
		while (pq.size() != 0) {
			ProgramRes prev = pq.poll();
			interations++;
			for (int i = 0; i < program.length; i++) {
				for (int dir = -1; dir < 2; dir += 2) {
					
					long add = (long)Math.pow(8, i) * dir;
					
					long newA = prev.origA() + add;
					if (newA < 0) continue;
					if (usedAlready.contains(newA)) continue;
					
					usedAlready.add(newA);
					
					RESET_PROGRAM(newA);
					
					ProgramRes res = RUN_PROGRAM(program, newA, 0, 0);
					if (res.passScore() != -1 && MAX_SCORE - res.passScore() <= 3) pq.offer(res);
					MAX_SCORE = Math.max(MAX_SCORE, res.passScore());
				}
			}
		}
		
		System.out.println("Iterated this many times: " + interations);
		 
		greatScores.sort((a, b) -> {
			if (Integer.compare(b.passScore(), a.passScore()) == 0) return Long.compare(a.origA(), b.origA());
			else return Integer.compare(b.passScore(), a.passScore());
		});
		
		for (var score : greatScores) System.out.println(score);
		
		
	}
	
	private static long getCost(int[] setBits) {
		long total = 0;
		
		for (int i = setBits.length - 1; i > -1; i--) {
			total *= 2;
			total += setBits[i];
		}
		
		System.out.println("Found a solution: " + total);
		return total;
	}
	
	private static void findMinCost(int[] setBits, int pInd, char[] program) {
		if (pInd == program.length) {
			MIN_VALID = Math.min(MIN_VALID, getCost(setBits));
			return;
		}
		
		int bInd = pInd * 3;
		for (int c = 0; c < 8; c++) {
			int val = (program[pInd] - '0') ^ c ^ getLiteral(program[7]);
			
			int startCInd = bInd + val;
			int endCInd = startCInd + 3;
			boolean[] setC = new boolean[3];
			boolean matchedAll = true;
			boolean specialCase = false;
			
			if (endCInd >= setBits.length && c != 0) {
				matchedAll = false;
			}
			int bitsNeeded = c;
			
			if (endCInd >= setBits.length && c == 0) {
				specialCase = true;
			}
			for (int ind = startCInd; ind < endCInd && matchedAll && !specialCase; ind++) {
				int setTo = bitsNeeded % 2;
				bitsNeeded /= 2;
				
				if (setBits[ind] != -1 && setBits[ind] != setTo) {
					matchedAll = false;
				}
				else if (setBits[ind] == -1) {
					setBits[ind] = setTo;
					setC[ind - startCInd] = true;
				}
			}
			
			if (matchedAll) {
				int startBInd = bInd;
				int endBInd = startBInd + 3;
				boolean[] setB = new boolean[3];
				boolean matchedAllB = true;
				int bitsNeededB = val ^ getLiteral(program[3]);
				
				for (int ind = startBInd; ind < endBInd && matchedAllB; ind++) {
					int setTo = bitsNeededB % 2;
					bitsNeededB /= 2;
					
					if (setBits[ind] != -1 && setBits[ind] != setTo) {
						matchedAllB = false;
					}
					else if (setBits[ind] == -1) {
						setBits[ind] = setTo;
						setB[ind - startBInd] = true;
					}
				}
				
				if (matchedAllB) {
					findMinCost(setBits, pInd + 1, program);
				}
				
				for (int i = 0; i < 3; i++) {
					if (setB[i]) setBits[i + startBInd] = -1;
				}
			}
			for (int i = 0; i < 3; i++) {
				if (setC[i]) setBits[i + startCInd] = -1;
			}
			
		}
		
	}
	
	private static ProgramRes RUN_PROGRAM(char[] program, long a, long b, long c) {
		
		globalIter++;
		long priorA = a;
		long priorB = b;
		sb.setLength(0);
		
		//System.out.println("A is " + A + ", B is " + B + ", C is " + C);
		
		
		while (iPointer < program.length) {
			char opcode = program[iPointer];
			char operand = program[iPointer + 1];
			processOperation(opcode, operand);
		}
		
		//System.out.println("Found string output: " + sb.toString());
		int passRate = program.length;
		
		if (sb.length() / 2 != program.length) {
			return new ProgramRes(priorA, sb.toString(),  -1);
		}
		
		for (int i = 0; i < sb.length(); i += 2) {
			char ch = sb.charAt(i);
			if (ch != program[i == 0 ? 0 : i / 2]) passRate--;
		}
		
		
		ProgramRes results = new ProgramRes(priorA, sb.toString(), passRate);
		
		if (results.passScore() == program.length) {
			greatScores.add(results);
		}
		
		return results;
	}
	
	private static void RESET_PROGRAM(long aVal) {
		//System.out.println("Trying with A: " + aVal);
		A = aVal;
		B = 0;
		C = 0;
		iPointer = 0;
	}
	
	private static Integer getLiteral(char operand) {
		return operand - '0';
	}
	
	private static long getCombo(char operand) {
		if (operand >= '0' && operand <= '3') return operand - '0';
		if (operand == '4') return A;
		if (operand == '5') return B;
		if (operand == '6') return C;
		
		System.err.println("Unimplemented operand read... shutting down now.");
		System.exit(1);
		
		return -1;
	}
	
	private static Long performDivision(char operand) {
		long iterations = getCombo(operand);
		
		long start = 1;
		
		while (start <= A && iterations != 0) {
			iterations--;
			start *= 2;
		}
		
		if (start > A) return 0L;
		else return A / start;
	}
	
	private static void processOperation(char opCode, char operand) {
		if (opCode == '0') {
			Long divRes = performDivision(operand);
			A = divRes;
			iPointer += 2;
		}
		else if (opCode == '6') {
			Long divRes = performDivision(operand);
			B = divRes;
			iPointer += 2;
		}
		else if (opCode == '7') {
			Long divRes = performDivision(operand);
			C = divRes;
			iPointer += 2;
		}
		else if (opCode == '1') {
			Long xOrRes = B ^ getLiteral(operand);
			B = xOrRes;
			iPointer += 2;
		}
		else if (opCode == '2') {
			B = getCombo(operand) % 8;
			iPointer += 2;
		}
		else if (opCode == '3') {
			if (A == 0) {
				iPointer += 2;
			}
			else {
				iPointer = getLiteral(operand);
				//System.out.println("Moving back to " + iPointer + " with A now == " + A + " and B == " + B);
			}
		}
		else if (opCode == '4') {
			B = B ^ C;
			iPointer += 2;
		}
		else if (opCode == '5') {
			long val = B % 8;
			sb.append(val).append(',');
			iPointer += 2;
		}
	}
	
	private static char[] getProgram(Scanner scan) {
		String clumped = scan.nextLine();
		char[] withCommas = clumped.split(" ")[1].toCharArray();
		
		char[] withoutCommas = new char[(withCommas.length / 2) + 1];
		
		for (int i = 0; i < withCommas.length; i += 2) {
			int ind = i == 0 ? 0 : i / 2;
			
			withoutCommas[ind] = withCommas[i]; 
		}
		
		return withoutCommas;
	}
	
	private static Integer parseVal(Scanner scan) {
		String inputLine = scan.nextLine();
		String[] split = inputLine.split(":");
		
		String val = split[1].substring(1);
		System.out.println("String is " + val);
		
		return Integer.parseInt(val);
	}

}
