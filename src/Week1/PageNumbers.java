package Week1;

import java.io.*;
import java.util.*;


public class PageNumbers {
	
	public static final String inputPath = "InputText.txt";
	public static final File input = new File(inputPath);
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner scan = new Scanner(input);
		Map<String, Set<String>> edgeTo = new HashMap<>();
		String readIn = "";
		while ((readIn = scan.nextLine()).length() == 5) {
			String[] edge = readIn.split("\\|");
			String from = edge[0], to = edge[1];
			
			if (!edgeTo.containsKey(from)) edgeTo.put(from, new HashSet<>());
			if (!edgeTo.containsKey(to)) edgeTo.put(to,  new HashSet<>());
			
			edgeTo.get(from).add(to);
		}
		
		long sumOfOrderedBad = 0;
		
		while (scan.hasNext()) {
			readIn = scan.nextLine();
			String[] order = readIn.split(",");
			int res = evaluateLine(order, edgeTo);
			if (res == 0) sumOfOrderedBad += orderAndEvaluateLine(order, edgeTo);
		}
		
		System.out.println(sumOfOrderedBad);
	}
	
	private static int orderAndEvaluateLine(String[] unordered, Map<String, Set<String>> edgeTo) {
		Map<String, Integer> inDegree = new HashMap<>();
		
		Set<String> orderingActive = new HashSet<>();
		for (String key : unordered) {
			orderingActive.add(key);
			inDegree.put(key, 0);
		}
		for (String key : unordered) {
			
			for (var node : edgeTo.get(key)) {
				if (orderingActive.contains(node)) inDegree.put(node,  inDegree.get(node) + 1);
			}
		}
		
		List<String> ordered = tSort(inDegree, edgeTo);
		return Integer.parseInt(ordered.get(ordered.size() / 2));
		
	}
	
	private static List<String> tSort(Map<String, Integer> inDegree, Map<String, Set<String>> edgesTo){
		Queue<String> q = new ArrayDeque<String>();
		
		List<String> res = new ArrayList<>();
		for (var num : inDegree.entrySet()) {
			if (num.getValue() == 0) q.offer(num.getKey());
		}
		
		while (q.size() != 0) {
			String curr = q.poll();
			res.add(curr);
			
			for (var node : edgesTo.get(curr)) {
				
				if (inDegree.containsKey(node)) {
					inDegree.put(node, inDegree.get(node) - 1);
					if (inDegree.get(node) == 0) q.offer(node);
				}
			}
		}
		
		return res;
	}
	
	private static int evaluateLine(String[] order, Map<String, Set<String>> edgeTo) {
		
		Map<String, Integer> countOf = new HashMap<>();
		
		for (var key : order) {
			countOf.put(key, countOf.getOrDefault(key, 0) + 1);
		}
		
		for (int i = order.length - 1; i > -1; i--) {
			String toRemove = order[i];
			Set<String> hasAnEdgeTo = edgeTo.get(toRemove);
			
			countOf.put(toRemove, countOf.get(toRemove) - 1);
			if (countOf.get(toRemove) == 0) countOf.remove(toRemove);
			
			for (var p : countOf.entrySet()) {
				if (hasAnEdgeTo.contains(p.getKey())) return 0;
			}
		}
		
		return Integer.parseInt(order[order.length / 2]);
	}

}
