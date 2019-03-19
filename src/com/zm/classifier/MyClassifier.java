package com.zm.classifier;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.zm.bean.ClassifierResult;

public class MyClassifier {
	public static final int IMG_WIDTH = 128;
	public static final int IMG_HEIGHT = 128;
	public static final int K = 1;

	double[] load_img(String path) throws Exception{
		Mat mat=Imgcodecs.imread(path,0);
		Mat dst = new Mat();
		Imgproc.resize(mat, dst, new Size(IMG_WIDTH, IMG_HEIGHT));
		byte[] v = new byte[IMG_WIDTH * IMG_HEIGHT];
		dst.get(0, 0, v);
		double[] d = new double[IMG_WIDTH * IMG_HEIGHT];
		for (int i = 0; i < d.length; i++) {
			d[i] = v[i];
		}
		return d;
	}

	MyMatrix load_img_list(List<String> paths)throws Exception {
		double[][] m = new double[paths.size()][IMG_WIDTH * IMG_HEIGHT];
		for (int i = 0; i < paths.size(); i++) {
			m[i] = load_img(paths.get(i));
		}
		return new MyMatrix(m);
	}

	public ClassifierResult check_test_imgs(List<String> paths, String user_id, List<String> trainset,
			List<String> train_user_ids, String pca_path, String lda_path) throws Exception {

		ClassifierResult result = new ClassifierResult();
		MyMatrix train_m = load_img_list(trainset);
		MyMatrix test_m = load_img_list(paths);
		PCA pca = PCA.load(pca_path);
		LDA lda = LDA.load(lda_path);

		MyMatrix pcatrain = pca.projection(train_m);
		MyMatrix pcatest = pca.projection(test_m);

		MyMatrix ldatrain = lda.projection(pcatrain);
		MyMatrix ldatest = lda.projection(pcatest);

		KNN knn = new KNN(ldatrain, train_user_ids, K);
		String[] labels = knn.predict(ldatest);

		boolean re = false;
		ArrayList<Boolean> result_list = new ArrayList<Boolean>();
		for (int i = 0; i < paths.size(); i++) {
			if (labels[i].equals(user_id)) {
				result_list.add(true);
				re = true;
			} else {
				result_list.add(false);
			}
		}
		result.setResult_list(result_list);
		result.setResult(re);
		return result;
	}
	
	public void train(List<String> trainset,List<String> train_user_ids,String pca_path, String lda_path)throws Exception {
		MyMatrix train_m = load_img_list(trainset);
		PCA pca=new PCA(train_m);
		MyMatrix pcam=pca.projection(train_m);
		LDA lda=new LDA(pcam,train_user_ids);
		pca.save(pca_path);
		lda.save(lda_path);
	}
}
