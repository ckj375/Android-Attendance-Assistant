package com.yr.worktime.util;

import java.util.Calendar;
import java.util.Date;

public class ComFunction {

    public static boolean isEmpty(String str)
    {
        return (str != null && str.length() > 0) ? false : true;
    }

    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }
    

    public static String myDate(int year,int month,int day)
    {
        return year
        + "/" + (month > 9 ? month : "0" + month)
        + "/" + (day > 9 ? day : "0" + day);
    }

    public static String myDate(String year,String month,String day)
    {
        return year
        + "/" + (Integer.parseInt(month) > 9 ? month : "0" + month)
        + "/" + (Integer.parseInt(day) > 9 ? day : "0" + day);
    }
    
    public static String myTime(int hour,int minute)
    {
        return (hour > 9 ? hour : "0" + hour)
        + ":" + (minute > 9 ? minute : "0" + minute);
    }
    
    public static String myTime(String hour,String minute)
    {
        return (Integer.parseInt(hour) > 9 ? hour : "0" + hour)
        + ":" + (Integer.parseInt(minute) > 9 ? minute : "0" + minute);
    }

	public static int getGapCount(String strdateBegin, String strdateEnd) {
		
		@SuppressWarnings("deprecation")
		Date startDate = new Date(Integer.parseInt(strdateBegin.substring(0,4))
				,Integer.parseInt(strdateBegin.substring(5,7))
				,Integer.parseInt(strdateBegin.substring(8)));

		@SuppressWarnings("deprecation")
		Date endDate = new Date(Integer.parseInt(strdateEnd.substring(0,4))
				,Integer.parseInt(strdateEnd.substring(5,7))
				,Integer.parseInt(strdateEnd.substring(8)));

		Calendar fromCalendar = Calendar.getInstance(); 
		fromCalendar.setTime(startDate);    
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);    
		fromCalendar.set(Calendar.MINUTE, 0);    
		fromCalendar.set(Calendar.SECOND, 0);    
		fromCalendar.set(Calendar.MILLISECOND, 0);    
   
		Calendar toCalendar = Calendar.getInstance();    
		toCalendar.setTime(endDate);    
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);    
		toCalendar.set(Calendar.MINUTE, 0);    
		toCalendar.set(Calendar.SECOND, 0);    
		toCalendar.set(Calendar.MILLISECOND, 0);    
   
		return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));  
	     
	}
}
