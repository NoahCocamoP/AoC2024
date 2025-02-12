package Week4;

import java.io.*;
import java.util.*;

class UnionFind{
	public HashMap<String, String> parentOf;
	public HashMap<String, Integer> size;
	
	public UnionFind() {
		parentOf = new HashMap<>();
		size = new HashMap<>();
	}
	
	public void addFresh(String a) {
		if (parentOf.containsKey(a)) return;
		parentOf.put(a, a);
		size.put(a, 1);
	}
	
	public String getParent(String a) {
		
		if (parentOf.get(a).equals(a)) return a;
		
		return parentOf.put(a, getParent(parentOf.get(a)));
	}
	
	public void Union(String a, String b) {
		addFresh(a);
		addFresh(b);
		
		String pA = getParent(a);
		String pB = getParent(b);
		
		if (pA.equals(pB)) return;
		
		Integer szA = size.get(pA);
		Integer szB = size.get(pB);
		
		if (szA >= szB) {
			parentOf.put(pB, pA);
			size.put(pA, szA + szB);
		}
		else {
			parentOf.put(pA, pB);
			size.put(pB, szB + szA);
		}
	}
}

record triConnection(String a, String b, String c) {
	
	public static triConnection ordinalRep(String a, String b, String c) {
		
		List<String> lis = new ArrayList<>();
		lis.add(a);
		lis.add(b);
		lis.add(c);
		
		Collections.sort(lis);
		
		return new triConnection(lis.get(0), lis.get(1), lis.get(2));
	}
}

public class Day23 {
	public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    
    // For part 1
    public static HashMap<String, Set<String>> adjMap;
    public static Set<triConnection> counted;
    
    // For part 2
    public static UnionFind uf;
    public static Set<String> addedVertices;
    public static List<String> verticesLis;
  
    public static String answer = "";

	public static void main(String[] args) {
		part1();
		//part2();

	}
	
	private static void addUFConnection(String inpStr) {
		String[] vertices = inpStr.split("-");
		uf.Union(vertices[0], vertices[1]);
		addedVertices.add(vertices[0]);
		addedVertices.add(vertices[1]);
	}
	
	private static void addConnection(String inpStr) {
		String[] vertices = inpStr.split("-");
		
		if (!adjMap.containsKey(vertices[0])) adjMap.put(vertices[0], new HashSet<>());
		if (!adjMap.containsKey(vertices[1])) adjMap.put(vertices[1], new HashSet<>());
		
		adjMap.get(vertices[0]).add(vertices[1]);
		adjMap.get(vertices[1]).add(vertices[0]);
		
		if (!addedVertices.contains(vertices[0])) {
			addedVertices.add(vertices[0]);
			verticesLis.add(vertices[0]);
		}
		
		if (!addedVertices.contains(vertices[1])) {
			addedVertices.add(vertices[1]);
			verticesLis.add(vertices[1]);
		}
	}
	
	private static void part2() {
		uf = new UnionFind();
		addedVertices = new HashSet<>();
		
		Scanner scan = null;
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			System.err.println("Couldn't find file at specified file path. Exiting now.");
			System.exit(1);
		}
		while (scan.hasNext()) {
			addUFConnection(scan.nextLine());
		}
		
		String largestParent = "";
		Integer largestSize = 0;
		
		for (var key : addedVertices) {
			String parent = uf.getParent(key);
			
			if (uf.size.get(parent) > largestSize) {
				largestParent = parent;
				largestSize = uf.size.get(parent);
			}
		}
		
		
		List<String> compsInComponent = new ArrayList<String>();
		
		for (var key : addedVertices) {
			String parent = uf.getParent(key);
			
			if (parent.equals(largestParent)) compsInComponent.add(key);
		}
		
		Collections.sort(compsInComponent);
		
		StringBuilder sb = new StringBuilder();
		
		for (var s : compsInComponent) {
			sb.append(s).append(',');
		}
		
		System.out.println(sb.toString());
	}
	
	private static void part1() {
		double start = System.currentTimeMillis();
		Scanner scan = null;
		adjMap = new HashMap<>();
		addedVertices = new HashSet<>();
		verticesLis = new ArrayList<>();
		try {
			scan = new Scanner(input);
		}
		catch(FileNotFoundException e) {
			System.err.println("Couldn't find file at specified file path. Exiting now.");
			System.exit(1);
		}
		
		while (scan.hasNext()) {
			addConnection(scan.nextLine());
		}
		
		int maxConnectivity = (int)Math.sqrt(verticesLis.size()) + 1;
		
		for (int i = 1; i < maxConnectivity; i++) {
			
			List<String> strs = new ArrayList<String>();
			
			if (canMakeConnection(strs, i, 0)) {
				continue;
			}
			else break;
		}
		double end = System.currentTimeMillis() - start;
		System.out.println("Took " + end + " MS");
		System.out.println(answer);
	}
	
	private static boolean isValid(List<String> lis) {
		boolean hasConnectionToEverythingElse = true;
		
		int size = lis.size();
		
		
		for (int i = 0; i < size && hasConnectionToEverythingElse; i++) {
			for (int j = 0; j < size && hasConnectionToEverythingElse; j++) {
				if (i == j) continue;
				
				String keyA = lis.get(i);
				String keyB = lis.get(j);
				
				if (!adjMap.get(keyA).contains(keyB)) {
					hasConnectionToEverythingElse = false;
				}
			}
		}
		
		if (hasConnectionToEverythingElse) {
			System.out.println("CAN MAKE: " + Arrays.toString(lis.toArray()));
		}
		return hasConnectionToEverythingElse;
	}
	
	private static boolean canMakeConnection(List<String> lis, int numAdd, int ind) {
		if (numAdd == 0) {
			Collections.sort(lis);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < lis.size(); i++) {
				sb.append(lis.get(i));
				if (i != lis.size() - 1) sb.append(',');
			}
			answer = sb.toString();
			return true;
		}
		int remaining = verticesLis.size() - ind - 2;
		
		if (remaining >= numAdd) {
			boolean thisOption = canMakeConnection(lis, numAdd, ind + 1);
			if (thisOption == true) return true;
		}
		
		boolean canAdd = true;
		String curr = verticesLis.get(ind);
		for (var s : lis) {
			if (!adjMap.get(s).contains(curr)) {
				canAdd = false;
				break;
			}
		}
		
		if (!canAdd) return false;
		
		lis.add(curr);
		boolean res = canMakeConnection(lis, numAdd - 1, ind + 1);
		lis.remove(lis.size() - 1);
		
		return res;
	}
	

}
