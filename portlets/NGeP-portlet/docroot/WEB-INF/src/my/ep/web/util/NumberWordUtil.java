/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util;

import java.text.DecimalFormat;

public class NumberWordUtil {

	private static final String[] tensNames = { "", " SEPULUH", " DUA PULUH",
			" TIGA PULUH", " EMPAT PULUH", " LIMA PULUH", " ENAM PULUH",
			" TUJUH PULUH", " LAPAN PULUH", " SEMBILAN PULUH" };

	private static final String[] numNames = { "", " SATU", " DUA", " TIGA",
			" EMPAT", " LIMA", " ENAM", " TUJUH", " LAPAN", " SEMBILAN",
			" SEPULUH", " SEBELAS", " DUA BELAS", " TIGA BELAS",
			" EMPAT BELAS", " LIMA BELAS", " ENAM BELAS", " TUJUH BELAS",
			" LAPAN BELAS", " SEMBILAN BELAS" };

	private NumberWordUtil() {
	}

	private static String convertLessThanOneThousand(int number) {
		String soFar;

		if (number % 100 < 20) {
			soFar = numNames[number % 100];
			number /= 100;
		} else {
			soFar = numNames[number % 10];
			number /= 10;

			soFar = tensNames[number % 10] + soFar;
			number /= 10;
		}
		if (number == 0)
			return soFar;
		return numNames[number] + " RATUS" + soFar;
	}

	public static String convert(String phrase) {
		long dollars = 0;
		long cent = 0;
		if (phrase.contains(".")) {
			dollars = Long.parseLong(phrase.substring(0,
					phrase.lastIndexOf(".")));
			cent = Long.parseLong(phrase.substring(phrase.lastIndexOf(".") + 1));
		} else {
			dollars = Long.parseLong(phrase);
		}

		if (dollars == 0 && cent != 0) {
			return convert(cent) + " SEN";
		} else if (cent == 0 && dollars != 0) {
			return convert(dollars);
		} else if(cent == 0 && dollars == 0){
			return "";
		} else{
			return convert(dollars) + " DAN " + convert(cent) + " SEN";
		}

	}

	public static String convert(long number) {
		// 0 to 999 999 999 999
		if (number == 0) {
			return "KOSONG";
		}

		String snumber = Long.toString(number);

		// pad with "0"
		String mask = "000000000000";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXnnnnnnnnn
		int billions = (int) Long.parseLong(snumber.substring(0, 3));
		// nnnXXXnnnnnn
		int millions = (int) Long.parseLong(snumber.substring(3, 6));
		// nnnnnnXXXnnn
		int hundredThousands = (int) Long.parseLong(snumber.substring(6, 9));
		// nnnnnnnnnXXX
		int thousands = (int) Long.parseLong(snumber.substring(9, 12));

		String tradBillions;
		switch (billions) {
		case 0:
			tradBillions = "";
			break;
		case 1:
			tradBillions = convertLessThanOneThousand(billions) + " RIBU JUTA ";
			break;
		default:
			tradBillions = convertLessThanOneThousand(billions) + " RIBU JUTA ";
		}
		String result = tradBillions;

		String tradMillions;
		switch (millions) {
		case 0:
			tradMillions = "";
			break;
		case 1:
			tradMillions = convertLessThanOneThousand(millions) + " JUTA ";
			break;
		default:
			tradMillions = convertLessThanOneThousand(millions) + " JUTA ";
		}
		result = result + tradMillions;

		String tradHundredThousands;
		switch (hundredThousands) {
		case 0:
			tradHundredThousands = "";
			break;
		case 1:
			tradHundredThousands = "SATU RIBU ";
			break;
		default:
			tradHundredThousands = convertLessThanOneThousand(hundredThousands)
					+ " RIBU ";
		}
		result = result + tradHundredThousands;

		String tradThousand;
		tradThousand = convertLessThanOneThousand(thousands);
		result = result + tradThousand;

		// remove extra spaces!
		return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
	}

	public static void main(final String[] args) {
		String s = convert("1111111111.67");
		System.out.println(s);
	}
}