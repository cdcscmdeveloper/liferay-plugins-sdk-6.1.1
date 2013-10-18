package my.ep.web.util.pp.common;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

import org.apache.log4j.Logger;

public class CommonMethod {
	private static Logger log = Logger.getLogger(CommonMethod.class);
	
	public static PmStatus currentStatus(Long statusId, String docType){
		//call api to get pm status
		
		return new PmStatus();
	}
	

	public static BigDecimal addBigDecimalNullable(BigDecimal amountOne, BigDecimal amountTwo){
		BigDecimal amtOne = amountOne!=null ? amountOne : new BigDecimal(0);
		BigDecimal amtTwo =  amountTwo!=null ? amountTwo : new BigDecimal(0);
		return amtOne.add(amtTwo);
	}

	public static BigDecimal substractBigDecimalNullable(BigDecimal amountOne, BigDecimal amountTwo){
		BigDecimal amtOne = amountOne!=null ? amountOne : new BigDecimal(0);
		BigDecimal amtTwo =  amountTwo!=null ? amountTwo : new BigDecimal(0);
		return amtOne.subtract(amtTwo);
	}
	

	public static String generateHeader(String viewMode, String progType){
		log.info("generateHeader - Start");
		String pageTitle = "";
		String titleLabel = "";
		if(ProcurementPlanConst.MODE_NEW.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.new";
		}else if(ProcurementPlanConst.MODE_LIST.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.list";
		}else if(ProcurementPlanConst.MODE_APPROVAL.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.approval";
		}else if(ProcurementPlanConst.MODE_REVISION.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.revision";
		}else if(ProcurementPlanConst.MODE_VERIFICATION.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.verification";
		}else if(ProcurementPlanConst.MODE_MAINTAIN.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.maintain";
		}else if(ProcurementPlanConst.MODE_NEW_MTO.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.mto.plan";
		}

		titleLabel = getMessageCommon(pageTitle);
		
		log.info("generateHeader - End");
		return titleLabel;
	}

	public static String getMessageCommon(String name){
		log.info("getMessageCommon");
		return ResourceBundleUtils.getMessage(name, ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}

	//temp
	public static final Map<Long, String> approverMap;
    static
    {
    	approverMap = new HashMap<Long, String>();
    	approverMap.put(1L, "lailah");
    	approverMap.put(2L, "khai");
    }
		
}
