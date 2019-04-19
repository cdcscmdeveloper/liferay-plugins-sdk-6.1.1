package my.ep.web.backingbean.rpt;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;

import org.apache.commons.collections.CollectionUtils;

public class ReportUtil {
	
	String strLanguage;

	public ReportUtil(){
	
	}
	
	public ReportUtil(String strLanguage){
		this.strLanguage = strLanguage;
	}
	
	public IRPTRequestSO getRptRequestSO(){
		return EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
	}
	
	public IPMReportSO getPMReportSO(){
		return EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
	}

	public boolean isEnglish(){
		return strLanguage.equalsIgnoreCase(ReportConst.ENG_LANG);
	}
	
	public boolean isMalay(){
		return strLanguage.equalsIgnoreCase(ReportConst.MALAY_LANG);
	}
	
	public List<SelectItem> assignListToSelectItemValueLabel(List<Object[]> list){

		List<SelectItem> selectItemList = new ArrayList<SelectItem>();
		
		if (CollectionUtils.isNotEmpty(list)) {
			
			for (Object[] object : list) {
			
				SelectItem selectItem = new SelectItem();
			
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				
				selectItemList.add(selectItem);
				
			}
		}
		
		return selectItemList;
	}	
	
	public List<SelectItem> assignListToSelectItemLabelValue(List<Object[]> list){

		List<SelectItem> selectItemList = new ArrayList<SelectItem>();
		
		if (CollectionUtils.isNotEmpty(list)) {
			
			for (Object[] object : list) {
			
				SelectItem selectItem = new SelectItem();
			
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				
				selectItemList.add(selectItem);
				
			}
		}
		
		return selectItemList;
	}
	
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
	
	protected boolean isEmpty(List<?> list){
		return list == null || list.isEmpty();
	}
	
	protected boolean isEmpty(HashMap map){
		return map == null || map.isEmpty();
	}
	
	protected boolean isEmpty(String str){
		return str == null || str.isEmpty();
	}
	
	protected boolean isEmpty(Object obj){
		return obj == null;
	}
}