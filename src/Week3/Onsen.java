package Week3;

import java.io.*;
import java.util.*;

public class Onsen {
    public static final String inputPath = "InputText.txt";
    public static final File input = new File(inputPath);
    public static Trie towelTrie = new Trie();

    public static void main(String[] args) {
        Scanner scan = null;
        
        try {
            scan = new Scanner(input);
        }
        catch (FileNotFoundException e) {
            System.err.println("Uh oh, file not found at given file path. Exiting now.");
            System.exit(1);
        }
        
        String readIn = "";
        
        // Build the Trie from input towels
        while (!(readIn = scan.nextLine()).isEmpty()) {
            String[] towelsArr = readIn.split(", ");
            for (var towel : towelsArr) {
                towelTrie.insert(towel);
            }
        }
        
        long total = 0;
        
        while (scan.hasNext()) {
            readIn = scan.nextLine();
            System.out.println("Trying to find matches for " + readIn);
            char[] match = readIn.toCharArray();
            
            onsenDP dp = new onsenDP(towelTrie, match);
            long res = dp.dpRes();
            System.out.println("There are " + res + " ways to make this.");
            total += res;
        }
        
        System.out.println(total);
    }
}

class TrieNode {
    TrieNode[] children;
    boolean isEndOfWord;
    
    public TrieNode() {
        children = new TrieNode[128];  // ASCII
        isEndOfWord = false;
    }
}

class Trie {
    private TrieNode root;
    
    public Trie() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            if (current.children[c] == null) {
                current.children[c] = new TrieNode();
            }
            current = current.children[c];
        }
        current.isEndOfWord = true;
    }
    
    // Returns all valid prefixes starting at the given position
    public List<Integer> findAllValidEndings(char[] str, int start) {
        List<Integer> validEndings = new ArrayList<>();
        TrieNode current = root;
        
        for (int i = start; i < str.length; i++) {
            char c = str[i];
            if (current.children[c] == null) break;
            
            current = current.children[c];
            if (current.isEndOfWord) {
                validEndings.add(i);
            }
        }
        
        return validEndings;
    }
}

class onsenDP {
    public long[] minCost;
    public Trie towelTrie;
    public char[] toMatch;
    
    public onsenDP(Trie inputTrie, char[] match) {
        minCost = new long[match.length];
        Arrays.fill(minCost, Integer.MIN_VALUE);
        
        towelTrie = inputTrie;
        toMatch = match;
    }
    
    public long dpRes() {
        return getDP(0);
    }
    
    private long getDP(int startInd) {
        if (startInd == minCost.length) return 1;
        else if (startInd > minCost.length) return 0;
        else if (minCost[startInd] != Integer.MIN_VALUE) return minCost[startInd];
        
        long total = 0;
        
        // Get all valid endings for strings starting at startInd
        List<Integer> validEndings = towelTrie.findAllValidEndings(toMatch, startInd);
        
        // For each valid ending, recurse on the next position
        for (int endInd : validEndings) {
            total += getDP(endInd + 1);
        }
        
        return minCost[startInd] = total;
    }
}