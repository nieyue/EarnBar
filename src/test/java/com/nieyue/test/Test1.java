package com.nieyue.test;

import java.util.Calendar;
import java.util.Date;

public class Test1 {
 public static void main(String[] args) {
	System.out.println(Calendar.ALL_STYLES);
	System.out.println(Calendar.AM);
	System.out.println(Calendar.DATE);
	System.out.println(Calendar.DAY_OF_MONTH);
	System.out.println(Calendar.getInstance().getTimeInMillis());
	System.out.println(new Date("1970/1/1 8:0:1").getTime());
	System.out.println(new Double(2.3)+3.0);
}
}
