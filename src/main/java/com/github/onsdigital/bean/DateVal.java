package com.github.onsdigital.bean;


import org.apache.commons.lang.StringUtils;

/**
 * 
 */
public class DateVal {
	public int year;
	public String quarter; //Q1,Q2,Q3 or Q4
	public String month; //Jan,Feb .....


	@Override
	public String toString() {
		String date = String.valueOf(year);
		if (StringUtils.isNotBlank(month)) {
			date += " " + month;
		}
		if (StringUtils.isNotBlank(quarter)) {
			date += " " + quarter;
		}
		return date;
	}
}
