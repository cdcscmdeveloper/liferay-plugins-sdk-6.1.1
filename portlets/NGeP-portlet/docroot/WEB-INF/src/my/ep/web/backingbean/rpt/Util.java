package my.ep.web.backingbean.rpt;

import java.util.Calendar;
import java.util.Date;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;

public class Util {

	public void valiidateDate(Date fromDate, Date toDate) {
		
		Calendar fromCalendar = Calendar.getInstance();
		Calendar toCalendar = Calendar.getInstance();
		
		fromCalendar.setTime(fromDate);
		toCalendar.setTime(toDate);
		
		int fromYear = fromCalendar.get(Calendar.YEAR); 
		int toYear = toCalendar.get(Calendar.YEAR);
		
		int fromMonth = fromCalendar.get(Calendar.MONTH); 
		int toMonth = toCalendar.get(Calendar.MONTH);
		
		boolean sameYear  = fromYear == toYear;
		
		boolean fromMonthGreaterThanToMonth = fromMonth > toMonth;
		
		boolean fromYearGreaterThanToYear = fromYear > toYear;
		boolean fromYearAndToYearAreMoreThanAYear = toYear > fromYear + 1;
		
		if (sameYear) {
			
			if (fromMonthGreaterThanToMonth) {
				throw new EpException(MessageConst.ERROR_REPORT_780014);
			}
			
		} else if (fromYearGreaterThanToYear) {
			
			throw new EpException(MessageConst.ERROR_REPORT_780015);
			
		} else if (fromYearAndToYearAreMoreThanAYear) {
			
			throw new EpException(MessageConst.ERROR_REPORT_780011);
			
		}

	}
}
