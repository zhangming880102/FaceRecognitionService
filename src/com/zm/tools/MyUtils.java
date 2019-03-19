package com.zm.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zm.bean.ManagerSignItemBean;
import com.zm.bean.ManagerUserBean;

public class MyUtils {
	static double EARTH_RADIUS = 6378.137;
    static double rad(double lat){
        return lat*Math.PI/180;
    }
    public static double computeDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }
	public static String toDay(int v) {
		if(v<10) {
			return "0"+v;
		}else {
			return ""+v;
		}
	}
	public static int toHour(Date d) {
		return Integer.parseInt(new SimpleDateFormat("HH").format(d));
	}
	public static int toMinute(Date d) {
		return Integer.parseInt(new SimpleDateFormat("mm").format(d));
	}
	public static String compute_delay(int h,int m,int mh,int mm) {
		return ((h-mh)*60+(m-mm))+"分";
	}
	public static String compute_early(int h,int m,int mh,int mm) {
		return ((mh-h)*60+(mm-m))+"分";
	}
	public static String check_slist(List<ManagerSignItemBean> slist, ManagerUserBean user) {
		String result="缺勤";
		if(slist==null ||user==null) {
			return result;
		}
		if(slist.size()==0) {
			return result;
		}
		int firsth=toHour(slist.get(0).getCreate_time());
		int firstm=toMinute(slist.get(0).getCreate_time());
		if((firsth*100+firstm)>(100*user.getMorning_h()+user.getMorning_m())) {
			return "迟到"+compute_delay(firsth,firstm,user.getMorning_h(),user.getMorning_m());
		}
		if(slist.size()==1) {
			return "未签退";
		}
		int lasth=toHour(slist.get(slist.size()-1).getCreate_time());
		int lastm=toMinute(slist.get(slist.size()-1).getCreate_time());
		if(lasth*100+lastm < 100*user.getEvening_h()+user.getEvening_m()) {
			return "早退"+compute_early(lasth,lastm,user.getEvening_h(),user.getEvening_m());
		}
		return "成功";
	}
	
	public static String dateFormatDir() {
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		String year = c.get(Calendar.YEAR) + "";
		String month = (c.get(Calendar.MONTH)+1) + "";
		String day = c.get(Calendar.DAY_OF_MONTH) + "";
		return "/"+year+"/"+month+"/"+day;
	}
}
