package com.zm.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class KNN {
	MyMatrix m;
	String[] labels;
	int K;
	public KNN(MyMatrix m,String[] labels,int K) {
		this.m=m;
		this.labels=labels;
		this.K=K;
	}
	
	public KNN(MyMatrix m,List<String> labels,int K) {
		this.m=m;
		String[] ll=new String[labels.size()];
		labels.toArray(ll);
		this.labels=ll;
		this.K=K;
	}
	String[] predict(MyMatrix ma) {
		return knn(m.matrix,labels,ma.matrix,K);
	}
	class KSam {
		double dis;
		String label;

		public double getDis() {
			return dis;
		}

		public void setDis(double dis) {
			this.dis = dis;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}
	public String[] knn(double[][] train_data, String[] labels, double[][] test_data, int k) {
		String[] result = new String[test_data.length];
		for (int i = 0; i < test_data.length; i++) {
			ArrayList<KSam> kss = new ArrayList<KSam>();
			for (int j = 0; j < train_data.length; j++) {
				double dis = com_dis(test_data[i], train_data[j]);
				KSam ks = new KSam();
				ks.setDis(dis);
				ks.setLabel(labels[j]);
				kss.add(ks);
			}
			Collections.sort(kss, new Comparator<KSam>() {
				@Override
				public int compare(KSam a, KSam b) {
					if (a.getDis() > b.getDis()) {
						return 1;
					} else if (a.getDis() < b.getDis()) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			HashMap<String,Double> hash=new HashMap<String,Double>();
			for (int j = 0; j < k; j++) {
				KSam ks = kss.get(j);
				double s=ks.getDis()==0? (Double.MAX_VALUE / k/2):(1/ks.getDis());
				if(hash.containsKey(ks.getLabel())) {
					hash.put(ks.getLabel(), hash.get(ks.getLabel()).doubleValue()+s);
				}else {
					hash.put(ks.getLabel(), s);
				}
			}
			double vmax=-1;
			String max="";
			for(String label:hash.keySet()) {
				double hd=hash.get(label).doubleValue();
				if(hd >vmax) {
					vmax=hd;
					max=label;
				}
			}
			result[i] = max;
		}
		return result;
	}
	double com_dis(double[] a, double[] b) {
		double d = 0;
		for (int i = 0; i < a.length; i++) {
			d += (a[i] - b[i]) * (a[i] - b[i]);
		}
		return Math.pow(d, 0.5);
	}
}
