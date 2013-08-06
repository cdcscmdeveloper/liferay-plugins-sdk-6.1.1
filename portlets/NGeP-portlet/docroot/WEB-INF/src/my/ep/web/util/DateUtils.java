package my.ep.web.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	public Date convertStringToDate(String argPattern, String argValue) throws Exception{
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
	public Timestamp convertStringToTimestamp(String argPattern, String argValue) throws Exception{
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
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.add(Calendar.DATE, day);
		
		return c1.getTime();
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
	
	
}
