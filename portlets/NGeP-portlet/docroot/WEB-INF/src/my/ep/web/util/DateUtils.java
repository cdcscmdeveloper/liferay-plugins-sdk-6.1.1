package my.ep.web.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import my.ep.constants.CommonConstants;

import org.apache.log4j.Logger;

/**
 * DateUtils is a utility class for Date. 
 */
public class DateUtils {

	private static Logger log = Logger.getLogger(DateUtils.class);
	
	
	/**
	 * Getting a Timestamp Object Using System.currentTimeMillis()
	 * 
	 * @return Timestamp Object
	 */
	public static Timestamp getCurrentDate() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * 
	 * @return
	 */
	public	static	String	getLast2DigitOfYear(){
		Calendar c1 = Calendar.getInstance();
		String	year	=	String.valueOf(c1.get(Calendar.YEAR));
		return year.substring(2);
	}
	
	/**
	 * @ param Date date
	 * @return days of month
	 */
	public	static	String	getDays(Date date){
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		String	days	=	String.valueOf(c1.get(Calendar.DAY_OF_MONTH));
		return days;
	}
	
	/*
	 * Getting a java.util.Date Object without time
	 */
	public static Date getCurrentDateWithoutTime() {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH),0,0,0);
		c2.set(Calendar.MILLISECOND, 0);
		
		return c2.getTime();
	}
	
	/**
	 * Retrieve date without time
	 * @param argValue  date object
	 */
	public static Date getDateWithoutTime(Date argValue) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(argValue);
		Calendar c2 = Calendar.getInstance();
		c2.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
				c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		c2.set(Calendar.MILLISECOND, 0);

		return c2.getTime();
	}
	
	
	/**
	 * Convert date to a string.
	 * 
	 * @param argPattern the arg pattern
	 * @param argValue the arg value
	 * 
	 * @return the string
	 */
	public static String dateToString(String argPattern, Date argValue) {
		String str = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(argPattern);
			str = sdf.format(argValue);
		} catch (Exception e) {
			str = "";
		}
		return str;
	}	
	
	/**
	 * Checks if is date greater or equal.
	 * 
	 * @param d1 the d1
	 * @param d2 the d2
	 * 
	 * @return true, if is date greater or equal
	 */
	public static boolean isDateGreaterOrEqual(Date d1, Date d2) {
		if ((d1 == null) || (d2 == null)) {
			return false;
		}

		try {
			if (d1.compareTo(d2) >= 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
		}

		return false;
	}
	
		
	/**
	 * Calculate the number of days between date1 and date2.
	 * 
	 * @param date1 the date1
	 * @param date2 the date2
	 * 
	 * @return long
	 */
	public static long getDaysDifference(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
	    Calendar calendar2 = Calendar.getInstance();	    
	    calendar1.setTime(date1);
	    calendar2.setTime(date2);	    
	    long milliseconds1 = calendar1.getTimeInMillis();
	    long milliseconds2 = calendar2.getTimeInMillis();
	    long diff = milliseconds2 - milliseconds1;	    	        
	    return (diff / (24 * 60 * 60 * 1000));
	}
	
	/**
	 * 
	 * Calculate the diffrence between date1 and date2.
	 * 
	 * @param date1 the date1
	 * @param date2 the date2
	 * 
	 * @return Date since January 1, 1970, 00:00:00 UTC;
	 */
	public static Date getDateDifference(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(date2.getTime() - date1.getTime() + getCalendarStartDate().getTime());	    	        
	    return c1.getTime();
	}
	
	/**
	 * Convert string to date.
	 * 
	 * @param argPattern the arg pattern
	 * @param argValue the arg value
	 * 
	 * @return the date
	 * 
	 * @throws Exception the exception
	 */
	public static Date convertStringToDate(String argPattern, String argValue) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat(argPattern);
		Date tmpDate = null;
		tmpDate = sdf.parse(argValue);
		return tmpDate;
	}
	
	/**
	 * Convert string to timestamp.
	 * 
	 * @param argPattern the arg pattern
	 * @param argValue the arg value
	 * 
	 * @return the timestamp
	 * 
	 * @throws Exception the exception
	 */
	public static Timestamp convertStringToTimestamp(String argPattern, String argValue) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat(argPattern);
		Date tmpDate = null;
		tmpDate = sdf.parse(argValue);
		return new Timestamp(tmpDate.getTime());
	}
			

	/**
	 * check whether start AND end date overlapped with targetStart AND targetEnd. For Example:
	 * return <b>false</b> when
	 * 
	 * 			  start					end	targetStart			targetEnd
	 * 				|--------------------|		|--------------------|
	 * 										OR
	 * 			targetStart			targetEnd	start				End
	 * 				|--------------------|		  |------------------|
	 * 
	 * otherwise return <b>true</b>
	 * 
	 * @exception 
	 * 		NullPointerException - when start OR end OR targetStart OR targetEnd is null.
	 * 		IllegalArgumentException - when either start >= end OR targetStart >= targetEnd OR
	 * 												start > end OR targetStart > targetEnd
	 * 									depending on isStartEndEqual flag.
	 * @param start
	 * @param end
	 * @param targetStart
	 * @param targetEnd
	 * @param allowedStartEndEqual
	 * @return
	 */
	public static boolean isDateOvarlapping(Date start, Date end, Date targetStart, Date targetEnd, boolean allowedStartEndEqual) {
		// if all date is not empty
		if (start != null && end != null && targetStart != null && targetEnd != null) {
			// date is all valid
			if (isStartEndDateValid(start, end, targetStart, targetEnd, allowedStartEndEqual)) {
				if (!(end.compareTo(targetStart) < 0 
						|| targetEnd.compareTo(start) < 0)) {
					return true;
				}
			} else {
				throw new IllegalArgumentException("EITHER start : " + start
						+ " is greater or equal than end : " + end + " OR"
						+ " targetStart : " + targetStart
						+ " is greater or equal than targetEnd : " + targetEnd);
			}
		} else {
			throw new NullPointerException(
					"One of the arguments is null. [" + "start : " + start
							+ ", end : " + end + ", targetStart : "
							+ targetStart + ", targetEnd : " + targetEnd + "]");
		}

		return false;
	}
	
	/**
	 * if allowedStartEndEqual flag is true
	 * 		start <= end AND targetStart <= targetEnd
	 * otherwise
	 * 		start < end AND targetStart < targetEnd
	 * 
	 * @param start
	 * @param end
	 * @param targetStart
	 * @param targetEnd
	 * @param allowedStartEndEqual
	 * @return
	 */
	private static boolean isStartEndDateValid(Date start, Date end,
			Date targetStart, Date targetEnd, boolean allowedStartEndEqual) {
		if (allowedStartEndEqual) {
			return (start.compareTo(end) <= 0 && targetStart.compareTo(targetEnd) <= 0);
		} else{
			return (start.compareTo(end) < 0 && targetStart.compareTo(targetEnd) < 0);
		}
	}

	/**
	 * check whether start AND end date overlapped with targetStart AND targetEnd. For Example:
	 * return <b>false</b> when
	 * 
	 * 			  start					end	targetStart			targetEnd
	 * 				|--------------------|		|--------------------|
	 * 										OR
	 * 			targetStart			targetEnd	start				End
	 * 				|--------------------|		  |------------------|
	 * 
	 * otherwise return <b>true</b>
	 * 
	 * @exception 
	 * 		NullPointerException - when start OR end OR targetStart OR targetEnd is null.
	 * 		IllegalArgumentException - when start >= end OR targetStart >= targetEnd.
	 * @param start
	 * @param end
	 * @param targetStart
	 * @param targetEnd
	 * @return
	 */
	public static boolean isDateOvarlapping(Date start, Date end, Date targetStart, Date targetEnd) {
		return isDateOvarlapping(start, end, targetStart, targetEnd, false);
	}
	
		
	
	
	/**
	 * Add number of days to the date instance.
	 * 
	 * @param date A Date instance
	 * @param day Number of days to add
	 * @return A date instance after number of days added
	 */
	public static Date addDays(Date date, int day){
		
		Date time = null;
		if(date != null){
			Calendar c1 = Calendar.getInstance();
			c1.setTime(date);
			c1.add(Calendar.DATE, day);
			time =  c1.getTime();
		}
		return time;
	}
	
	/**
	 * Minus number of days to the date instance.
	 * 
	 * @param date A Date instance
	 * @param day Number of days to minus
	 * @return A date instance after number of days minus
	 */
	public static Date minusDays(Date date, int day){
		int currDay = day * -1;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.add(Calendar.DATE, currDay);
		
		return c1.getTime();
	}
	
	/**
	 * Get the date difference in milliseconds.
	 * 
	 * @param startDateUtc Start Date
	 * @param endDateUtc End Date
	 * @return difference between dates in milliseconds
	 */
	public static long getDateDiffInMillis(Date startDateUtc, Date endDateUtc){
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(startDateUtc);
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(endDateUtc);
		
		long milliseconds1 = calendar1.getTimeInMillis();
	    long milliseconds2 = calendar2.getTimeInMillis();
	    long diff = milliseconds2 - milliseconds1;
	    
	    return diff;
	}	
	
	/**
	 * Getting a Calendar Starting Date. January 1, 1970, 00:00:00 UTC;
	 * 
	 * @return Date Object 
	 */
	public static Date getCalendarStartDate() {
		Calendar c1 = Calendar.getInstance();	    
	    c1.setTimeInMillis(0);
	    c1.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);	    
	    return c1.getTime();
	}
	
	/**
	 * Calculate the number of hours between date1 and date2.
	 * 
	 * @param date1 the date1
	 * @param date2 the date2
	 * 
	 * @return long
	 */
	public static long getHoursDifference(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
	    Calendar calendar2 = Calendar.getInstance();	    
	    calendar1.setTime(date1);
	    calendar2.setTime(date2);	    
	    long milliseconds1 = calendar1.getTimeInMillis();
	    long milliseconds2 = calendar2.getTimeInMillis();
	    long diff = milliseconds2 - milliseconds1;	    	        
	    return (diff / (24 * 60 * 1000));
	}
	


/**
	 * Set the default 12 noon for the date example publish/closing date
	 * 
	 * @param date A Date instance
	 * @return A date instance with time 12 noon
	 */
	public static Date setDefaultDateTime(Date date){
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 12);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    
	    Date newDate = new Timestamp(calendar.getTime().getTime());
		return newDate;
	}
	
	public static String durationFormatter(long durationInMills) {

		long diffMinutes = durationInMills / (60 * 1000) % 60;
		long diffHours = durationInMills / (60 * 60 * 1000) % 24;
		DecimalFormat durationFormat = new DecimalFormat("#00");
		String duration = "";
		if (diffHours <= 24 && diffHours >= 2) {
			duration = durationFormat.format(diffHours) + " Hours  "
					+ durationFormat.format(diffMinutes) + " Minutes";
		}

		if (diffHours < 2 && diffHours >= 1) {
			duration = durationFormat.format(diffHours) + " Hour  "
					+ durationFormat.format(diffMinutes) + " Minutes";
		}
		if (diffHours < 1) {
			duration = durationFormat.format(diffMinutes) + " Minutes";
		}

		return duration;
	}
	
	/**
	 * Localize date.
	 *
	 * @param date the date
	 * @param locale the locale
	 * @return the string
	 */
	public static String getLocalizeDate(Date date, Locale locale) { 
	    SimpleDateFormat localDateFormate = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_WITH_MONTH_NAME, locale);       
	    
	    return localDateFormate.format(date);
	}
	/**
	 * Checks if is date greater or equal.
	 * 
	 * @param d1 the d1
	 * @param d2 the d2
	 * 
	 * @return true, if is date greater or equal
	 */
	public static boolean isDateGreater(Date d1, Date d2) {
		if ((d1 == null) || (d2 == null)) {
			return false;
		}

		try {
			if (d1.compareTo(d2) > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
		}

		return false;
	}
	
	/**
	 * Check whether current date already exceed param date (optional additional
	 * months passed will be added to param date)
	 * 
	 * @param Date
	 *            paramDate
	 * @param int months
	 * @return boolean
	 */
	public static boolean isCurrentDateAlreadyExceedParamDatePlusMonths(
			Date paramDate, Integer additionalMonth) {
		boolean retVal = false;
		Calendar calAfterAddMonths = Calendar.getInstance();
		calAfterAddMonths.setTime(paramDate);
		if (additionalMonth != null) {
			calAfterAddMonths.add(Calendar.MONTH, additionalMonth);
		}

		if (new Date().after(calAfterAddMonths.getTime())) {
			retVal = true;
		}
		return retVal;
	}
	
	
	/**
	 * Gets the months.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return the months
	 */
	public static Integer getMonths(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return 0;
		}
		Calendar startCal = new GregorianCalendar();
		startCal.setTime(startDate);
		Calendar endCal = new GregorianCalendar();
		endCal.setTime(endDate);

		int diffYear = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
		return  diffYear * 12 + endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH)+1;
	}

}
