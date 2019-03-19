package com.zm.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EigenVector {
	MyMatrix m;
	public MyMatrix eigens;
	int k;
	
	public EigenVector(MyMatrix m,int k) {
		this.m=m;
		this.k=k;
		if(k<=m.col) {
			comEigen();
		}
	}
	class Eigen {
		double value;
		double vec[];

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public double[] getVec() {
			return vec;
		}

		public void setVec(double[] vec) {
			this.vec = vec;
		}

	}
	public void comEigen() {
		ArrayList<Eigen> result = new ArrayList<Eigen>();
		int dim=m.col;
		double[][] cov=m.matrix;
		double eigen[][] = new double[dim][dim];
		for (int i = 0; i < dim; i++) {
			eigen[i][i] = 1;
		}
		int maxC = dim * dim * 30;
		double pres = 1e-6;
		int count = 0;
		while (true) {
			double mmax = Math.abs(cov[0][1]);
			int rown = 0;
			int coln = 1;
			for (int i = 0; i < dim; i++) {
				for (int j = i; j < dim; j++) {
					if ((i != j) && Math.abs(cov[i][j]) > mmax) {
						mmax = Math.abs(cov[i][j]);
						rown = i;
						coln = j;
					}
				}
			}
			if (mmax < pres) {
				break;
			}
			if (count > maxC) {
				break;
			}
			count++;
			double sij = cov[rown][coln];
			double sii = cov[rown][rown];
			double sjj = cov[coln][coln];

			double ang = 0;
			ang = 0.5 * Math.atan2(2 * sij, sii - sjj);
			double sina = Math.sin(ang);
			double cosa = Math.cos(ang);
			cov[rown][rown] = cosa * cosa * sii + 2 * cosa * sina * sij + sina * sina * sjj;
			cov[coln][coln] = sina * sina * sii - 2 * cosa * sina * sij + cosa * cosa * sjj;
			cov[rown][coln] = cov[coln][rown] = (cosa * cosa - sina * sina) * sij + sina * cosa * (sjj - sii);

			for (int i = 0; i < dim; i++) {
				if (i != rown && i != coln) {
					double arowk = cov[rown][i];
					double acolk = cov[coln][i];
					cov[rown][i] = arowk * cosa + acolk * sina;
					cov[i][rown] = cov[rown][i];
					cov[coln][i] = acolk * cosa - arowk * sina;
					cov[i][coln] = cov[coln][i];
				}
			}
			for (int i = 0; i < dim; i++) {
				double pkr = eigen[i][rown];
				double pkc = eigen[i][coln];
				eigen[i][rown] = pkr * cosa + pkc * sina;
				eigen[i][coln] = pkc * cosa - pkr * sina;
			}
		}
		
		for (int i = 0; i < dim; i++) {
			Eigen e = new Eigen();
			e.setValue(cov[i][i]);
			double[] vec = new double[dim];
			for (int j = 0; j < dim; j++) {
				vec[j] = eigen[j][i];
			}
			e.setVec(vec);
			result.add(e);
		}
		Collections.sort(result, new Comparator<Eigen>() {
			@Override
			public int compare(Eigen a, Eigen b) {
				if (a.getValue() > b.getValue()) {
					return -1;
				} else if (a.getValue() < b.getValue()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		
		double re[][] =new double[dim][k];
		for(int i=0;i<k;i++) {
			for(int j=0;j<dim;j++) {
				re[j][i]=result.get(i).getVec()[j];
			}
		}
		eigens=normalized_eigens(new MyMatrix(re));
	}
	static MyMatrix normalized_eigens(MyMatrix eigens) {
		double[][] em=eigens.matrix;
		for(int i=0;i<eigens.col;i++) {
			double sum=0;
			for(int j=0;j<eigens.row;j++) {
				sum+=em[j][i]*em[j][i];
			}
			sum=Math.pow(sum, 0.5);
			if(sum!=0) {
				for(int j=0;j<eigens.row;j++) {
					em[j][i]/=sum;
				}
			}
		}
		return new MyMatrix(em);
	}
	
}
