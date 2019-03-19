package com.zm.classifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class PCA {
	MyMatrix m;
	public MyMatrix eigens;
	MyMatrix meanMatrix;

	public PCA(MyMatrix eigens, MyMatrix meanMatrix) {
		this.eigens = eigens;
		this.meanMatrix = meanMatrix;
	}

	public PCA(MyMatrix m) throws Exception {
		this.m = m;
		run();
	}

	public void run() throws Exception {
		if (m == null || m.row == 0 || m.col == 0) {
			return;
		}
		meanMatrix = m.colMeans();
		MyMatrix subMean = m.subByCol(meanMatrix);
		if (m.col <= m.row) {
			MyMatrix covmatrix = subMean.tr().multiple(subMean).multipleByVal(1 / (double) m.row);
			EigenVector e = new EigenVector(covmatrix, m.col);
			eigens = e.eigens;
		} else {
			MyMatrix covmatrix = subMean.multiple(subMean.tr()).multipleByVal(1 / (double) m.col);
			EigenVector e = new EigenVector(covmatrix, m.row);
			eigens = EigenVector.normalized_eigens(m.tr().multiple(e.eigens));
		}
	}

	public MyMatrix projection(MyMatrix ma) throws Exception {
		if (ma.col != meanMatrix.col) {
			System.out.println(ma.col+"\t"+meanMatrix.col);
			throw new Exception("project col number not match!");
		}

		return ma.subByCol(meanMatrix).multiple(eigens);
	}

	public void save(String file) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(eigens.row + "\n");
		bw.write(eigens.col + "\n");
		System.out.println(meanMatrix.col+"");
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

	public static PCA load(String file) throws Exception {
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
		return new PCA( new MyMatrix(ma),new MyMatrix(mm));
	}
}
