package my.ep.web.util.sm.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.primefaces.component.log.Log;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;

public class VmUtil {
	
	private static VmUtil instance;
	
	public static Logger log = Logger.getLogger(VmUtil.class);

	
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
	//
	public String getBusinessNature(SmSupplierBusiness suppBusinessNat){
		RegistrationBackingBean registrationBackingBean = new RegistrationBackingBean();
		return registrationBackingBean.getBusinessNatureSection(suppBusinessNat);
	}
	
	public String getFomatDate(SmPersonnel smpersonnel){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String birthdate = sdf.format(smpersonnel.getBirthDate());
		log.info("THIS IS BIRTHDATE---------------------------------------------" + birthdate);
		return birthdate;
		
	}
	
	public String setStatusMessageSSM(SmCompanyBasic smcompany){
		String statusCode = smcompany.getCompanySsmStatus();
		if("A".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.active", null);
		}else if("E".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.existing", null);
		}else if("L".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.expired", null);
		}else if("T".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.terminated", null);
		}else if("B".equalsIgnoreCase(statusCode)){ 
		//27382
			return SmCommonUtil.getMessage("lbl.sm.status.dissolved", null);
		}else if("W".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.w", null);
		}else if("D".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.d", null);
		}else if("R".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.r", null);
		}else if("C".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.c", null);
		}else if("X".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.x", null);
		}else if("Y".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.y", null);
		}
		//start #29935
		else if("F".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.winding.up.under.cva", null);
		}
		else if("G".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.existing.under.jm.under.cva", null);
		}
		else if("J".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.existing.under.jm", null);
		}
		else if("V".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.existing.under.cva", null);
		}
		//end #29935
		return "";
	}
	
	public String getCompleteAddress(SmAddress smAdd) {
		return AddressUtils.getFullAddress(smAdd);
	}
	
}
