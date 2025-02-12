package Week4;

import java.util.*;
import java.io.*;

class Trie{
	public Trie[] nextPins;
	public long keysHere;
	
	public Trie() {
		nextPins = new Trie[10];
		keysHere = 0;
	}
	
	public long countValidKeys(int[] lock, int ind) {
		if (ind == lock.length) return 1;
		
		int maxOther = 7 - lock[ind];
		long res = 0;
		
		for (int i = 0; i <= maxOther; i++) {
			if (nextPins[i] != null) {
				res += nextPins[i].countValidKeys(lock, ind + 1);
			}
		}
		
		return res;
	}
	
	public void addKey(int[] key, int ind) {
		if (ind == key.length) {
			this.keysHere++;
			
			if (this.keysHere > 1) {
				System.out.println("Identical key added");
			}
			return;
		}
		
		if (nextPins[key[ind]] == null) nextPins[key[ind]] = new Trie();
		nextPins[key[ind]].addKey(key, ind + 1);
		
	}
}

record lockID(int a, int b, int c, int d, int e) {
	
	public int[] getArr() {
		return new int[] {a, b, c, d, e};
	}
}
public class Day25 {
	
	public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    
    public static Set<lockID> locks;
    public static Trie keys;
    
    public static int totalKeys = 0;
    
	public static void main(String[] args) {
		Scanner scan = null;
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		locks = new HashSet<>();
		keys = new Trie();
		
		while (scan.hasNext()) {
			
			String inp = "";
			List<char[]> mat = new ArrayList<>();
			while (scan.hasNext() && !(inp = scan.nextLine()).isEmpty()) {
				mat.add(inp.toCharArray());
			}
			
			boolean isLock = true;
			int len = mat.get(0).length;
			
			for (int i = 0; i < len; i++) {
				if (mat.get(0)[i] == '.') {
					isLock = false;
					break;
				}
			}
			
		
			int[] res = new int[len];
			
			for (int i = 0; i < len; i++) {
				int numThisCol = 0;
				
				for (int j = 0; j < mat.size(); j++) {
					if (mat.get(j)[i] == '#') numThisCol++;
				}
				
				res[i] = numThisCol;
			}
			
			if (isLock) {
				lockID id = new lockID(res[0], res[1], res[2], res[3], res[4]);
				if (locks.contains(id)) System.out.println("Identical lock added");
				locks.add(id);
			}
			else {
				keys.addKey(res, 0);
				totalKeys++;
			}
		}
		
		long total = 0;
		for (var lock : locks) {
			System.out.println("Trying lock " + lock);
			long res = keys.countValidKeys(lock.getArr(), 0);
			total += res;
			System.out.println("Found " + res + " keys that fit.");
		}
		
		System.out.println(total);
		System.out.println(totalKeys + " keys and " + locks.size() + " locks");

	}

}
