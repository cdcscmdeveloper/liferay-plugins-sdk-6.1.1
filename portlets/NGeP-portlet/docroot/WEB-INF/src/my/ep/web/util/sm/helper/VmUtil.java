package my.ep.web.util.sm.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;

public class VmUtil {
	
	private static VmUtil instance;
	
	/**
	 * Instance of VmUtil
	 * @return VmUtil
	 */
	public static VmUtil getInstance(){
		if (instance == null){
			instance = new VmUtil();
		}
		return instance;
	}
	
	/*
	 * for Site Visit VM purpose
	 */
	public static String getParameterDesc(PmParameter parameter){
		return ParameterHelper.getInstance().getParameterDesc(
				parameter.getParameterType(), parameter.getParameterCode());
	}
	
	public static String formatDate(Date date){
		if(date != null){
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String cdate = df.format(date);
			return cdate;
		}
		
		return null;
	}
	
	/**
	 * Format to two decimal point.
	 * 
	 * @param amount
	 *            the amount
	 * @return the string
	 */
	public static String formatToTwoDecimalPoint(BigDecimal amount) {
		DecimalFormat formatter = new DecimalFormat("###,##0.00");
		return formatter.format(amount);
	}
	
	/**
	 * Get Label
	 * @param String label
	 * @return String 
	 */
	public String getDescByLabel(String label){
		Locale locale = new FacesScopes().getUserLocale();
		String desc = ResourceBundleUtils.getMessage(
				label,ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		
		return desc;
	}
}
