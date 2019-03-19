package com.zm.classifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LDA {
	public MyMatrix eigens;
	public MyMatrix meanMatrix;
	MyMatrix m;
	String[] labels;

	public LDA(MyMatrix eigens, MyMatrix meanMatrix) {
		this.eigens = eigens;
		this.meanMatrix = meanMatrix;
	}

	public LDA(MyMatrix ma, String[] labels) throws Exception {
		if (ma.row != labels.length) {
			throw new Exception("label number not equal to matrix row number.");
		}
		this.m = ma;
		this.labels = labels;
		run();
	}

	public LDA(MyMatrix ma, List<String> labels) throws Exception {
		String[] ll = new String[labels.size()];
		labels.toArray(ll);
		if (ma.row != ll.length) {
			throw new Exception("label number not equal to matrix row number.");
		}
		this.m = ma;
		this.labels = ll;
		run();
	}

	void run() throws Exception {
		HashMap<String, ArrayList<Integer>> classes = new HashMap<String, ArrayList<Integer>>();
		for (int i = 0; i < labels.length; i++) {
			if (classes.containsKey(labels[i])) {
				ArrayList<Integer> vals = classes.get(labels[i]);
				vals.add(i);
				classes.put(labels[i], vals);
			} else {
				ArrayList<Integer> vals = new ArrayList<Integer>();
				vals.add(i);
				classes.put(labels[i], vals);
			}
		}
		meanMatrix = m.colMeans();
		MyMatrix sw = new MyMatrix(m.col, m.col);
		MyMatrix sb = new MyMatrix(m.col, m.col);

		if (classes.keySet().size() < 2) {
			throw new Exception("less than 2 classes.");
		}
		for (String key : classes.keySet()) {
			ArrayList<Integer> vals = classes.get(key);
			double[][] d = new double[vals.size()][m.col];
			for (int i = 0; i < vals.size(); i++) {
				int idx = vals.get(i).intValue();
				for (int j = 0; j < m.col; j++) {
					d[i][j] = m.matrix[idx][j];
				}
			}
			MyMatrix dm = new MyMatrix(d);
			MyMatrix colMeans = dm.colMeans();
			for (int i = 0; i < vals.size(); i++) {
				int idx = vals.get(i).intValue();
				MyMatrix row = m.rowAt(idx).sub(colMeans);
				sw.addToSelf(row.tr().multiple(row));
			}
			MyMatrix mm = colMeans.sub(meanMatrix);
			sb.addToSelf(mm.tr().multiple(mm).multipleByVal(vals.size()));
		}
		MyMatrix swb = sw.solve().multiple(sb);
		EigenVector e = new EigenVector(swb, m.col);
		this.eigens = e.eigens;
	}

	public MyMatrix projection(MyMatrix ma) throws Exception {
		if (ma.col != meanMatrix.col) {
			throw new Exception("project col number not match!");
		}

		return ma.subByCol(meanMatrix).multiple(eigens);
	}

	public void save(String file) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(eigens.row + "\n");
		bw.write(eigens.col + "\n");
		for (int i = 0; i < meanMatrix.col; i++) {
			if (i < meanMatrix.col - 1) {
				bw.write(meanMatrix.matrix[0][i] + "\t");
			} else {
				bw.write(meanMatrix.matrix[0][i] + "\n");
			}
		}
		for (int i = 0; i < eigens.row; i++) {
			for (int j = 0; j < eigens.col; j++) {
				if (j < eigens.col - 1) {
					bw.write(eigens.matrix[i][j] + "\t");
				} else {
					bw.write(eigens.matrix[i][j] + "\n");
				}
			}
		}
		bw.close();
	}

	public static LDA load(String file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		int row = 0;
		int col = 0;
		line = br.readLine();
		if (line != null) {
			row = Integer.parseInt(line);
		} else {
			throw new Exception("can't read file " + file);
		}

		line = br.readLine();
		if (line != null) {
			col = Integer.parseInt(line);
		} else {
			throw new Exception("can't read file " + file);
		}

		line = br.readLine();
		if (line == null) {
			throw new Exception("can't read file " + file);
		}
		String[] meanMatrix = line.split("\t");
		double[][] mm = new double[1][meanMatrix.length];
		for (int i = 0; i < meanMatrix.length; i++) {
			mm[0][i] = Double.parseDouble(meanMatrix[i]);
		}

		double[][] ma = new double[row][col];
		int k = 0;
		for (int i = 0; i < row; i++) {
			line = br.readLine();
			if (line == null) {
				throw new Exception("can't read file " + file);
			}
			String[] arr = line.split("\t");
			if (arr.length < col) {
				throw new Exception("load err " + file);
			}
			for (int j = 0; j < col; j++) {
				ma[i][j] = Double.parseDouble(arr[j]);
			}
		}
		return new LDA(new MyMatrix(ma),new MyMatrix(mm));
	}
}
