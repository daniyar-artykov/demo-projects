package com.sleekbill.report;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CustomDecimalFormat {

	private String pattern;
	private char decimalSeparator;
	private char groupingSeparator;
	private String language;
	private String country;

	public CustomDecimalFormat(String pattern, char decimalSeparator,
			char groupingSeparator) {
		this.pattern = pattern;
		this.decimalSeparator = decimalSeparator;
		this.groupingSeparator = groupingSeparator;
		// default values
		this.language = "en";
		this.country = "UK";
	}

	public CustomDecimalFormat(String pattern, char decimalSeparator,
			char groupingSeparator, String language, String country) {
		this.pattern = pattern;
		this.decimalSeparator = decimalSeparator;
		this.groupingSeparator = groupingSeparator;
		this.language = language;
		this.country = country;
	}

	public String format(Double value) {
		Locale locale = new Locale(language, country);
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(locale);
		otherSymbols.setDecimalSeparator(decimalSeparator);
		otherSymbols.setGroupingSeparator(groupingSeparator);
		DecimalFormat df = new DecimalFormat(pattern, otherSymbols);

		return df.format(value); 
	}
	
	public String format(BigDecimal value) {
		Locale locale = new Locale(language, country);
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(locale);
		otherSymbols.setDecimalSeparator(decimalSeparator);
		otherSymbols.setGroupingSeparator(groupingSeparator);
		DecimalFormat df = new DecimalFormat(pattern, otherSymbols);

		return df.format(value); 
	}
}
