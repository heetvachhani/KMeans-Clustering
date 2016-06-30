import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class kmeans {

	public static void main(String args[]) {
		int nc = Integer.parseInt(args[0]);
		String line[];
		File file = new File(args[1]);
		points[] p = new points[150];
		HashMap<Integer, ArrayList<Integer>> tmpcluster = new HashMap<Integer, ArrayList<Integer>>();
		try {
			int count = 0;
			@SuppressWarnings("resource")
			Scanner input = new Scanner(file);
			PrintStream out = new PrintStream(new FileOutputStream(args[2]));
			System.setOut(out);
			input.nextLine();
			while (input.hasNextLine()) {
				line = input.nextLine().split("\t");
				p[count] = new points();
				p[count].setId(Integer.parseInt(line[0]));
				p[count].setX(Double.parseDouble(line[1]));
				p[count].setY(Double.parseDouble(line[2]));
				count++;
			}
			int length = count;
			// initially selecting random center
			Integer[] arr = new Integer[count];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = i;
			}
			Collections.shuffle(Arrays.asList(arr));

			for (int i = 1; i <= nc; i++) {
				p[count] = new points();
				p[count].setId(i);
				p[count].setX(p[arr[i]].getX());
				p[count].setY(p[arr[i]].getY());
				count++;
			}

			// printing initial center
		//	printCenteroids(p, nc, length);

			int itr = 1;
			HashMap<Integer, ArrayList<Integer>> cluster ;
			// calculating distance matrix and changing centroids until all
			// points converged
			while (true) {
				double x, y;
				double mat[][] = new double[nc][length];
				for (int i = 0; i < nc; i++) {
					for (int j = 0; j < length; j++) {
						x = Math.abs(p[j].getX() - p[length + i].getX());
						y = Math.abs(p[j].getY() - p[length + i].getY());
						mat[i][j] = Math.sqrt((x * x) + (y * y));
					}
				}
				// printing matrix
			//	printDistanceMatrix(mat, nc, length);

				cluster= new HashMap<Integer, ArrayList<Integer>>();
				for (int j = 0; j < length; j++) {
					double min = 1000;
					int cindex = 0;
					for (int i = 0; i < nc; i++) {
						if (min > mat[i][j]) {
							min = mat[i][j];
							cindex = i;
						}
					}
					if (cluster.containsKey(new Integer(cindex))) {
						ArrayList<Integer> pts2 = cluster.get(new Integer(cindex));
						pts2.add(p[j].getId());
						cluster.put(cindex, pts2);
					} else {
						ArrayList<Integer> pts = new ArrayList<Integer>();
						pts.add(p[j].getId());
						cluster.put(cindex, pts);
					}
				}
				
				if (tmpcluster.equals(cluster)) {
					System.out.println("Result for final Cluster:");
					System.out.println("Cluster Id:\tPoints ID");
					
					for (int i = 0; i < cluster.keySet().size(); i++) {
						System.out.println((i + 1) + "\t\t" + cluster.get(i));
					}
					System.out.print("SSE value : ");
					calculateSSE(p, length, nc, cluster);
					System.exit(0);
				}
				tmpcluster = cluster;
				itr++;
				
				// Calculate new center
				int id = 0;
				for (int i = 0; i < cluster.keySet().size(); i++) {
					double sumX = 0, sumY = 0;

					for (int j = 0; j < cluster.get(i).size(); j++) {
						id = cluster.get(i).get(j);
						sumX = sumX + p[id - 1].getX();
						sumY = sumY + p[id - 1].getY();
					}
					p[length + i].setId(i + 1);
					p[length + i].setX((sumX / cluster.get(i).size()));
					p[length + i].setY((sumY / cluster.get(i).size()));

				}
				// printing new centroids
			//	printCenteroids(p, nc, length);

			}
			
		} catch (FileNotFoundException e) {

			System.out.println("File not found:" + e);

		}

	}

	private static void printDistanceMatrix(double[][] mat, int nc, int length) {
		for (int i = 0; i < nc; i++) {
			for (int j = 0; j < length; j++) {
				System.out.print(mat[i][j] + "\t");
			}
			System.out.println("");
		}
	}

	private static void printCenteroids(points[] p, int nc, int length) {
		for (int i = 0; i < nc; i++) {
			System.out.println(p[length + i].getId());
			System.out.println(p[length + i].getX());
			System.out.println(p[length + i].getY());
		}
	}

	private static void calculateSSE(points[] p, int length, int nc, HashMap<Integer, ArrayList<Integer>> cluster) {
		int id = 0;
		double x, y = 0;
		double sse = 0, res = 0;

		for (int i = 0; i < nc; i++) {
			res=0;
			for (int j = 0; j < cluster.get(i).size(); j++) {
				
				id = cluster.get(i).get(j);
				x = Math.abs(p[id - 1].getX() - p[length + i].getX());
				y = Math.abs(p[id - 1].getY() - p[length + i].getY());
				res = res + ((x * x) + (y * y));
			}
			sse = res + sse;
		}
		System.out.println(sse);
	}

}