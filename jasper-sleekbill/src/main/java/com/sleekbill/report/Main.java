package com.sleekbill.report;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Main {

	public static void main(String args[]) {
		Locale locale = new Locale("en", "UK");

		DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');

		String pattern = "#,##0.##";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);

		String number = decimalFormat.format(6789.15);
		System.out.println(number);
	}
}
