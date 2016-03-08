package com.sleekbill.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

	public static void main(String args[]) throws ParseException {
		SimpleDateFormat TO_DATE = new SimpleDateFormat("yyyy-mm-dd");
		Date date = TO_DATE.parse("2014-07-18");
		
		SimpleDateFormat TO_STRING = new SimpleDateFormat("MMM d, yyyy");
		
		System.out.println(TO_STRING.format(date));
	}
}
