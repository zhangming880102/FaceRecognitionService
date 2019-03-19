package com.zm.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.zm.bean.ImgBean;

public class MyImageUtils {
	public static ArrayList<String> writeImg(ImgBean imgBean, String dir, int user_id) {
		ArrayList<String> f = new ArrayList<String>();
		try {
			ArrayList<byte[]> bytes = imgBean.getBytes();
			for (int i = 0; i < bytes.size(); i++) {
				Mat mat = new Mat(imgBean.getHeight(), imgBean.getWidth(), CvType.CV_8UC1);
				mat.put(0, 0, bytes.get(i));
				Date d = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				String year = c.get(Calendar.YEAR) + "";
				String month = (c.get(Calendar.MONTH)+1) + "";
				String day = c.get(Calendar.DAY_OF_MONTH) + "";
				long mls = System.currentTimeMillis();

				String odir = dir + "//"+user_id+"//" + year + "//" + month + "//" + day;
				new File(odir).mkdirs();
				
				String file=odir+"//"+mls+"_"+i+".jpg";
				Imgcodecs.imwrite(file, mat);
				f.add(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
	
	public static ArrayList<String> writeColorImg(ImgBean imgBean, String dir, int user_id) {
		ArrayList<String> f = new ArrayList<String>();
		try {
			ArrayList<byte[]> bytes = imgBean.getBytes();
			System.out.println(imgBean.getBytes().size()+"\t"+imgBean.getWidth());
			for (int i = 0; i < bytes.size(); i++) {
				Mat mat = new Mat(imgBean.getHeight(), imgBean.getWidth(), CvType.CV_8UC4);
				byte[] bs=bytes.get(i);
				System.out.println(bs.length+"\t"+bs[bs.length-1]);
				mat.put(0, 0, bytes.get(i));
				Date d = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				String year = c.get(Calendar.YEAR) + "";
				String month = (c.get(Calendar.MONTH)+1) + "";
				String day = c.get(Calendar.DAY_OF_MONTH) + "";
				long mls = System.currentTimeMillis();

				String odir = dir + "//" +user_id+"//"+ year + "//" + month + "//" + day;
				new File(odir).mkdirs();
				
				String file=odir+"//"+mls+"_"+i+".jpg";
				Imgcodecs.imwrite(file, mat);
				f.add(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
}
