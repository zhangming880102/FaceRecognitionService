package com.zm.classifier;

public class MyMatrix {
	public double[][] matrix;
	public int row;
	public int col;

	public MyMatrix(double[][] matrix) {
		this.matrix = matrix;
		row = col = 0;
		if (matrix != null) {
			row = matrix.length;
			if (row != 0) {
				col = matrix[0].length;
			}
		}
	}

	public MyMatrix(int row, int col) {
		matrix = new double[row][col];
		this.row = row;
		this.col = col;
	}
	public void addToSelf(MyMatrix ma) throws Exception{
		if (ma.row != row || ma.col != col) {
			throw new Exception("dim not eqaul.");
		}
		double m2[][] = ma.getMatrix();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				matrix[i][j] += m2[i][j];
			}
		}
	}
	public MyMatrix add(MyMatrix ma) throws Exception {
		if (ma.row != row || ma.col != col) {
			throw new Exception("dim not eqaul.");
		}
		double re[][] = new double[row][col];
		double m2[][] = ma.getMatrix();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				re[i][j] = matrix[i][j] + m2[i][j];
			}
		}
		return new MyMatrix(re);
	}

	public MyMatrix sub(MyMatrix ma) throws Exception {
		if (ma.row != row || ma.col != col) {
			throw new Exception("dim not eqaul.");
		}
		double re[][] = new double[row][col];
		double m2[][] = ma.getMatrix();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				re[i][j] = matrix[i][j] - m2[i][j];
			}
		}
		return new MyMatrix(re);
	}

	public MyMatrix multiple(MyMatrix ma) throws Exception {
		if (col == ma.row) {
			double[][] re = new double[row][ma.col];
			double[][] m2 = ma.getMatrix();
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < ma.col; j++) {
					double sum = 0;
					for (int k = 0; k < col; k++) {
						sum += matrix[i][k] * m2[k][j];
					}
					re[i][j] = sum;
				}
			}
			return new MyMatrix(re);
		} else {
			throw new Exception("dim not eqaul.");
		}
	}

	public MyMatrix tr() {
		double[][] re = new double[col][row];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				re[j][i] = matrix[i][j];
			}
		}
		return new MyMatrix(re);
	}

	public MyMatrix colMeans() {
		double[][] re = new double[1][col];
		for (int i = 0; i < col; i++) {
			double sum = 0;
			for (int j = 0; j < row; j++) {
				sum += matrix[j][i];
			}
			double mean = row == 0 ? 0 : (sum / row);
			re[0][i] = mean;
		}
		return new MyMatrix(re);
	}

	public MyMatrix subByCol(MyMatrix colmeans) throws Exception {
		if (colmeans.col != col) {
			throw new Exception("dim not eqaul.");
		}
		double re[][] = new double[row][col];
		double m2[][] = colmeans.getMatrix();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				re[i][j] = matrix[i][j] - m2[0][j];
			}
		}
		return new MyMatrix(re);
	}

	public MyMatrix multipleByVal(double v) {
		double[][] re = new double[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				re[i][j] = matrix[i][j] * v;
			}
		}
		return new MyMatrix(re);
	}

	public MyMatrix rowAt(int idx) throws Exception {
		if (idx >= row) {
			throw new Exception("idx should less than row");
		}
		double[][] row = new double[1][col];
		for (int i = 0; i < col; i++) {
			row[0][i] = matrix[idx][i];
		}
		return new MyMatrix(row);
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}
	void sumrow(double[] row1,double[] row2) {
		for(int i=0;i<row1.length;i++) {
			row1[i]+=row2[i];
		}
	}
	void sumrow2(double[] row1,double[] row2,double v) {
		for(int i=0;i<row1.length;i++) {
			row1[i]+=row2[i]*v;
		}
	}
	void rowmultiple(double[] row,double v) {
		for(int i=0;i<row.length;i++) {
			row[i]*=v;
		}
	}
	public MyMatrix solve() throws Exception {
		if (row != col) {
			throw new Exception("row should equals to col");
		}
		double[][] r = new double[row][2 * row];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < row; j++) {
				r[i][j] = matrix[i][j];
				r[i][row + j] = i == j ? 1 : 0;
			}
		}
		for (int i = 0; i < row; i++) {
			if (r[i][i] == 0) {
				boolean all0 = true;
				int j=0;
				for (j = row-1; j >=0; j--) {
					if (r[j][i] != 0) {
						all0 = false;
						break;
					}
				}
				if(all0) {
					throw new Exception("err0");
				}
				sumrow2(r[i],r[j],1/r[j][i]);
			}else {
				rowmultiple(r[i],1/r[i][i]);
			}
			
			for(int j=row-1;j>i;j--) {
				sumrow2(r[j],r[i],-r[j][i]);
			}
		}
		for(int i=row-1;i>0;i--) {
			for(int j=0;j<i;j++) {
				sumrow2(r[j],r[i],-r[j][i]);
			}
		}
		
		double[][] re=new double[row][row];
		for(int i=0;i<row;i++) {
			for(int j=0;j<row;j++) {
				re[i][j]=r[i][j+row];
			}
		}
		return new MyMatrix(re);
	}
	
	public void print_matrix() {
		for(int i=0;i<row;i++) {
			for(int j=0;j<col;j++) {
				System.out.print(matrix[i][j]+" ");
			}
			System.out.println();
		}
	}

}
