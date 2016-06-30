package twitterPart2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class tweetsKMeans {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException, IOException {
		HashMap<Long, String> tweets;
		HashMap<Long, String> center;
		tweets = new HashMap<Long, String>();
		center = new HashMap<Long, String>();
		int count = 0;
		String te;
		long twid = 0;
		FileInputStream fin = new FileInputStream(args[2]);
		BufferedReader input1 = new BufferedReader(new InputStreamReader(fin));
		PrintStream out = new PrintStream(new FileOutputStream(args[3]));
		System.setOut(out);
		String line1;
		// read json data and store it in hashmap of tweets with key as id of
		// tweet and value as tweet text
		while ((line1 = input1.readLine()) != null) {
			String txt[] = line1.split("\"text\":");
			String id[] = line1.split("\"id\":");
			te = txt[1].substring(1, txt[1].indexOf(',')).replaceAll("[^a-zA-Z\\s]", ""); // removing
																							// unwanted
																							// punctuation
			twid = (Long.parseLong(id[1].substring(1, id[1].indexOf(',')).replace("\"", "")));
			tweets.put(twid, te);
			count++;
		}

		int nc = Integer.parseInt(args[0]);
		Scanner input = new Scanner(new File(args[1]));
		String[] line;
		long id;
		// read initial centroid file and store it in center hashmap with format
		// similar as tweets
		while (input.hasNextLine()) {
			line = input.nextLine().split(",");
			id = Long.parseLong(line[0]);
			center.put(id, tweets.get(id));
		}

		// create hashmap of cluster with key as cluster id and values as tweet
		// id's
		HashMap<Integer, ArrayList<Long>> cluster;
		HashMap<Integer, ArrayList<Long>> tmpcluster = new HashMap<Integer, ArrayList<Long>>();
		// imap and jmap use to map key with its centroid id
		HashMap<Integer, Long> imap;
		HashMap<Integer, Long> jmap;
		int itr = 1;
		int length = count;

		while (true) {
			double mat[][] = new double[nc][length];
			cluster = new HashMap<Integer, ArrayList<Long>>();
			imap = new HashMap<Integer, Long>();
			jmap = new HashMap<Integer, Long>();

			int i = 0, j = 0;
			// generate similarity matrix using Jaccard Distance

			for (Map.Entry<Long, String> cen : center.entrySet()) {
				j = 0;
				imap.put(i, cen.getKey());
				for (Map.Entry<Long, String> alldata : tweets.entrySet()) {
					mat[i][j] = distTweet(alldata.getValue(), cen.getValue());
					if (i == 0)
						jmap.put(j, alldata.getKey());
					j++;

				}
				i++;
			}

			// printDistanceMatrix(mat, nc, length);

			// assign tweets into nearest cluster
			for (int col = 0; col < length; col++) {
				double min = 1;
				int cindex = 0;
				for (int row = 0; row < nc; row++) {
					if (min > mat[row][col]) {
						min = mat[row][col];
						cindex = row;
					}
				}
				if (cluster.containsKey(new Integer(cindex))) {
					ArrayList<Long> pts2 = cluster.get(new Integer(cindex));
					pts2.add(jmap.get(col));
					cluster.put(cindex, pts2);
				} else {
					ArrayList<Long> pts = new ArrayList<Long>();
					pts.add(jmap.get(col));
					cluster.put(cindex, pts);
				}
			}

			if (tmpcluster.equals(cluster)) {
				System.out.println("Final Cluster generated at iteration: " + itr);
				System.out.println("\nCluster Id\tList of tweets id's");
				for (int k = 0; k < cluster.keySet().size(); k++) {
					System.out.println((k + 1) + "\t\t" + cluster.get(k));
				}
				calculateSSE(cluster, imap, tweets);

				System.exit(0);
			}
			itr++;

			tmpcluster = cluster;
			long newcenter = 0;
			// calculate new center
			center.clear();
			ArrayList<Long> tid;
			for (int k = 0; k < cluster.keySet().size(); k++) {
				double mindist = Double.MAX_VALUE;
				tid = cluster.get(k);
				for (int l = 0; l < tid.size(); l++) {
					double totaldist = 0;
					for (int m = 0; m < tid.size(); m++) {
						totaldist = totaldist + distTweet(tweets.get(tid.get(l)), tweets.get(tid.get(m)));
					}
					if (mindist > totaldist) {
						mindist = totaldist;
						newcenter = tid.get(l);
					}
				}
				center.put(newcenter, tweets.get(newcenter));
			}

		}
	}

	// private static void printDistanceMatrix(double[][] mat, int nc, int
	// length) {
	// for (int i = 0; i < nc; i++) {
	// for (int j = 0; j < length; j++) {
	// System.out.print(mat[i][j] + "\t");
	// }
	// System.out.println("");
	// }
	// }

	private static void calculateSSE(HashMap<Integer, ArrayList<Long>> cluster, HashMap<Integer, Long> imap,
			HashMap<Long, String> tweets) {
		double sse = 0;
		double res;
		ArrayList<Long> tid;
		for (int k = 0; k < cluster.keySet().size(); k++) {
			res = 0;
			tid = cluster.get(k);
			long cid = imap.get(k);
			for (int m = 0; m < tid.size(); m++) {
				res = res + distTweet(tweets.get(cid), tweets.get(tid.get(m)));
			}
			sse = sse + res;

		}

		System.out.println("\nSSE value: " + sse);
	}

	// Calculate Jaccard Distance
	public static double distTweet(String s1, String s2) {

		String[] s1Array = s1.toLowerCase().split(" ");
		String[] s2Array = s2.toLowerCase().split(" ");

		Collection<String> l1 = new ArrayList<>(Arrays.asList(s1Array));
		Collection<String> l2 = new ArrayList<>(Arrays.asList(s2Array));

		l1.retainAll(l2);

		int intersection = l1.size();
		List<String> list1 = new ArrayList<String>(Arrays.asList(s1Array));
		List<String> list2 = new ArrayList<String>(Arrays.asList(s2Array));
		Set<String> set = new HashSet<>();

		set.addAll(list1);
		set.addAll(list2);

		int union = set.size();
		return (double) (union - intersection) / union;

	}

}
