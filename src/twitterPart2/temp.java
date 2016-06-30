package twitterPart2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;

public class temp {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String type;
		JsonFactory f = new MappingJsonFactory();
		JsonParser jp = f.createJsonParser(new File(args[2]));
		HashMap<Long, String> tweets;
		HashMap<Long, String> center;
		tweets = new HashMap<Long, String>();
		center = new HashMap<Long, String>();
		// List<String> removeList;
		// removeList = Arrays.asList("\"", "\r", "\n", "(", ")", "[", "]", "{",
		// "}", "", ".", " ", ",");
		// tweets[] tw = new tweets[300];
		char[] temp = null;
		int count = 0;
		String te;
		// parse tweets and store it
		while (jp.nextToken() != null) {
			type = jp.getCurrentName();
			// System.out.println(type);
			if (type != null && type.equals("text")) {
				// tw[count]= new tweets();
				jp.nextToken();
				temp = jp.getTextCharacters();
				te = new String(temp);
				te = te.replaceAll("[^a-zA-Z\\s]", "");
				// tw[count].setText(te);
				jp.nextToken();
				jp.nextToken();
				jp.nextToken();
				jp.nextToken();
				jp.nextToken();
				jp.nextToken();
				jp.nextToken();
				jp.nextToken();
				jp.nextToken();
				jp.nextToken();
				// tw[count].setId(jp.getLongValue());
				tweets.put(jp.getLongValue(), te);
				// System.out.println(jp.getLongValue()+" : "+te);
				count++;

			}
		}
		int nc = Integer.parseInt(args[0]);
		Scanner input = new Scanner(new File(args[1]));
		String[] line;
		long id;
		while (input.hasNextLine()) {
			line = input.nextLine().split(",");
			id = Long.parseLong(line[0]);
			center.put(id, tweets.get(id));
			// System.out.println(id+" : "+tweets.get(id));
		}

		int length = count;
		// create similarity matrix using Jaccard Distance
		double mat[][] = new double[nc][length];
		HashMap<Integer, ArrayList<Long>> cluster = new HashMap<Integer, ArrayList<Long>>();
		HashMap<Integer, ArrayList<Long>> tmpcluster = new HashMap<Integer, ArrayList<Long>>();
		HashMap<Integer, Long> imap = new HashMap<Integer, Long>();
		HashMap<Integer, Long> jmap = new HashMap<Integer, Long>();

		int i = 0, j = 0;
		for (Map.Entry<Long, String> cen : center.entrySet()) {
			j = 0;
			imap.put(i, cen.getKey());

			for (Map.Entry<Long, String> alldata : tweets.entrySet()) {
				int intersection = intersection(alldata.getValue(), cen.getValue());
				int union = union(alldata.getValue(), cen.getValue());
				mat[i][j] = distTweet(union, intersection);
				if (i == 0)
					jmap.put(j, alldata.getKey());
				j++;

			}
			i++;
		}
		
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
		//printDistanceMatrix(mat, nc, length);

		for (int k = 0; k < cluster.keySet().size(); k++) {
			System.out.println((k + 1) + "\t\t" + cluster.get(k));
		}
		if (tmpcluster.equals(cluster)) {
			System.exit(0);
		}
		tmpcluster = cluster;

		//calculate new center
		
		
		
		// removing duplicates
		// for (String s : removeList)
		// {
		// distinctTerms.remove(s);
		// }

		int c = 0;
		System.out.println(count);

	}

	private static void printDistanceMatrix(double[][] mat, int nc, int length) {
		// TODO Auto-generated method stub
		for (int i = 0; i < nc; i++) {
			for (int j = 0; j < length; j++) {
				System.out.print(mat[i][j] + "\t");
			}
			System.out.println("");
		}
	}

	// Intersection
	public static int intersection(String s1, String s2) {

		String[] s1Array = s1.toLowerCase().split(" ");
		String[] s2Array = s2.toLowerCase().split(" ");

		Collection<String> l1 = new ArrayList<>(Arrays.asList(s1Array));
		Collection<String> l2 = new ArrayList<>(Arrays.asList(s2Array));

		l1.retainAll(l2);

		return l1.size();

	}

	// Union
	public static int union(String s1, String s2) {
		String[] s1Array = s1.toLowerCase().split(" ");
		String[] s2Array = s2.toLowerCase().split(" ");

		List<String> list1 = new ArrayList<String>(Arrays.asList(s1Array));
		List<String> list2 = new ArrayList<String>(Arrays.asList(s2Array));
		Set<String> set = new HashSet<>();

		set.addAll(list1);
		set.addAll(list2);

		return set.size();
	}

	// Calculate Jaccard Distance
	public static double distTweet(int union, int intersection) {

		return (double) (union - intersection) / union;

	}

}
