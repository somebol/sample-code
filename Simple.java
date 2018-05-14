package au.gov.ncis.ir.engine.hash;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class MinHash {
	
	public static final int[] DEFAULT_A = {451768075, 1563641239, -1914166896};
	public static final int[] DEFAULT_B = {1152968997, 1227389450, -1047192020};
	
	private final int[] randomA;
	private final int[] randomB;
	private final int shingleGram;
	
	public MinHash(int[] a, int[] b) {
		this(a, b, 3);
	}
	
	public MinHash(int[] a, int[] b, int numShingles) {
		this.randomA = a;
		this.randomB = b;
		this.shingleGram = numShingles;
		
	}

	public MinHash(int numHashes) {
		this(numHashes, 3);
	}
	
	public MinHash(int numHashes, int numShingles) {
		this.randomA = new int[numHashes];
		this.randomB = new int[numHashes];
		this.shingleGram = numShingles;

		generateRandomNums(randomA);
		generateRandomNums(randomB);
	}
	
	public Set<String> generate(Set<String> names) {
		return generate(names, 1);
	}
	
	public Set<String> generate(Set<String> names, int numHashes) {
		Set<String> hashes = new HashSet<>();
		
		Set<String> sortedNames = appendPart(sortNames(names), 2);
		
		for (String name : sortedNames) {
			Set<String> shingles = getShingles(name);
			for (int i = 0; i < numHashes; i++) {
				hashes.add(hash(shingles, randomA[i], randomB[i]));
			}
		}
		
		return hashes;
	}

	public Map<String, String> generateMap(Set<String> names, int numHashes) {
		Map<String, String> hashes = new HashMap<>();
		
		Set<String> sortedNames = appendPart(sortNames(names), 2);
		
		for (String name : sortedNames) {
			Set<String> shingles = getShingles(name);
			for (int i = 0; i < numHashes; i++) {
				String hash = hash(shingles, randomA[i], randomB[i]);
				hashes.put(hash, name + "||" + randomA[i] + "||" + randomB[i] + "||" + hash);
			}
		}
		
		return hashes;
	}
	
	public String hash(String name, int a, int b, String hashToAvoid) {
		int min = Integer.MAX_VALUE;
		for (String shingle : getShingles(name)) {
			int n = (shingle.hashCode() + a) ^ b;
			n = n ^ (n >>> 16);
			if (n < min && !Integer.toHexString(n).equals(hashToAvoid)) {
				min = n;
			}
		}
		return Integer.toHexString(min);
	}
	
	private void generateRandomNums(int[] rArray) {
		Random r = new Random();
		for (int i = 0; i < rArray.length; i++) {
			rArray[i] = r.nextInt();
		}
	}
	
	private Set<String> getShingles(String s) {
		Set<String> shingles = new TreeSet<>();
		for (int i = 0; i <= s.length() - this.shingleGram; i++) {
			shingles.add(s.substring(i, i + this.shingleGram).toLowerCase());
		}
		return shingles;
	}
	
	private String hash(Set<String> shingles, int a, int b) {
		int min = Integer.MAX_VALUE;
		for (String shingle : shingles) {
			int n = (shingle.hashCode() + a) ^ b;
			n = n ^ (n >>> 16);
			if (n < min) {
				min = n;
			}
		}
		return Integer.toHexString(min);
	}
	
	private Set<String> appendPart(Set<String> names, int numChars) {
		Set<String> ret = new TreeSet<>();
		for (String name : names) {
			if (name.length() > numChars) {
				ret.add(name + " " + name.substring(0, 2).trim());
			} else {
				ret.add(name);
			}
		}
		return ret;
	}
	
	private Set<String> sortNames(Set<String> unsortedNames) {
		Set<String> names = new TreeSet<>();
		for (String unsorted : unsortedNames) {
			String[] split = unsorted.split("\\s");
			List<String> orderedList = Arrays.asList(split);
			Collections.sort(orderedList);
			String s = "";
			for (String temp : orderedList) {
				s += temp + " ";
			}
			names.add(s.trim());
		}
		return names;
	}
}
