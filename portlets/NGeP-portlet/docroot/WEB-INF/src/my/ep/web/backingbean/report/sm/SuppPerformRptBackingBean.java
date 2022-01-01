package my.ep.web.backingbean.report.sm;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.web.constant.report.PmReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SuppPerformRptManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "suppPerformRptBB")
@ViewScoped
public class SuppPerformRptBackingBean extends
		SmReportBackingBean<SuppPerformRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7400800649761552705L;
	private static final Logger log = EpLogger.getLogger(SuppPerformRptBackingBean.class);

	public SuppPerformRptBackingBean() {
		//getManagedBean().setYearList(getYearList());
		setDefaultDateFromAndDateTo();
		onChangeRegType();
	}

	public void generateReport() {
		
		if (getManagedBean().getSelectedOrgProfile() == null && getManagedBean().getMofNo().isEmpty() && getManagedBean().getEpNo().isEmpty() ) {
				throw new EpException(MessageConst.ERROR_REPORT_780044);		
		} 
		if(getManagedBean().getDateFrom() != null && getManagedBean().getDateTo() != null) {
			dateValidation(getManagedBean().getDateFrom(), getManagedBean().getDateTo());
			
		} else if(getManagedBean().getDateFrom() != null && getManagedBean().getDateTo() == null) {
			throw new EpException(MessageConst.ERROR_REPORT_780055);
		} else if(getManagedBean().getDateFrom() == null && getManagedBean().getDateTo() != null) {
			throw new EpException(MessageConst.ERROR_REPORT_780054);
		}
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		if (getManagedBean().getSelectedOrgProfile() != null) {
			if (getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
				params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
						.getSelectedOrgProfile().getSmSupplier().getCompanyName());
				supplierSearchReqDTO.setCompanyName(getManagedBean()
						.getSelectedOrgProfile().getSmSupplier().getCompanyName());
				// #31893 - start
				/*
				if (supplierSearchReqDTO.getCompanyName().equalsIgnoreCase(SmReportConst.SM_SUPPLIER_PERFORMANCE_COMP_1)
						|| supplierSearchReqDTO.getCompanyName().equalsIgnoreCase(SmReportConst.SM_SUPPLIER_PERFORMANCE_COMP_2)
						|| supplierSearchReqDTO.getCompanyName().equalsIgnoreCase(SmReportConst.SM_SUPPLIER_PERFORMANCE_COMP_2)) {
					getManagedBean().setReportCode(SmReportConst.SM_SUPPLIER_PERFORMANCE_V_RPT_VAL);
				}
				*/
				params.put(SmReportConst.PARAM_SUPPLIER_ID, String.valueOf(getManagedBean()
						.getSelectedOrgProfile().getSmSupplier().getSupplierId()));
				
				if (getManagedBean().getSelectedOrgProfile().getSmSupplier().isSprMv()) {
					getManagedBean().setReportCode(SmReportConst.SM_SUPPLIER_PERFORMANCE_V_RPT_VAL);
				}
				// #31893 - end
			}
		}else{
			if(getManagedBean().getMofNo() != null){
				supplierSearchReqDTO.setMofAccountNo(getManagedBean().getMofNo());
			}
			if(getManagedBean().getEpNo() != null){
				supplierSearchReqDTO.setEpNo(getManagedBean().getEpNo());
			}
			SupplierSearchResDTO suResDTO = ValidateSupplier(supplierSearchReqDTO);
			supplierSearchReqDTO.setCompanyName(suResDTO.getSmSupplier().getCompanyName());
			params.put(SmReportConst.PARAM_COMPANY_NAME, suResDTO.getSmSupplier().getCompanyName());
		}
		
		params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		
		
		if (!StringUtils.isBlank(getManagedBean().getRegType())) {
			params.put(SmReportConst.PARAM_REG_TYPE, getManagedBean()
					.getRegType());
		}
		params.put(SmReportConst.PARAM_EP_NO, getManagedBean().getEpNo());
		params.put(PmReportConst.PARAM_PM_FROM_DATE, DateUtils.dateToString(
				CommonConstants.DEFAULT_DATE_FORMAT_WITH_MONTH_NAME_NEW, getManagedBean()
				.getDateFrom()));
		params.put(PmReportConst.PARAM_PM_TO_DATE, DateUtils.dateToString(
				CommonConstants.DEFAULT_DATE_FORMAT_WITH_MONTH_NAME_NEW, getManagedBean()
				.getDateTo()));
		
		//#7754 - Start
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		//#7754 - End

		for (Map.Entry<String, String> entry : params.entrySet()) {
			log.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
		log.info("********Report Code************* after set newCode: " + getManagedBean().getReportCode());
		generateSmReport(params);
	}
	
	public SupplierSearchResDTO ValidateSupplier(SupplierSearchReqDTO supplierSearchReqDTO ){

		ISupplierSearchSO supplierSearchSO = SmServiceLookup.getSupplierSearchSO();
			SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfilesAsCriteria(supplierSearchReqDTO);
			
			if(smResDTO.getSmSupplier() == null){
				throw new EpException(MessageConst.ERROR_REPORT_780056);
			}		
		return smResDTO;
	}

		private void dateValidation(Date dateFrom, Date dateTo){
		
		if (dateFrom == null || dateTo == null) {
			throw new EpException(MessageConst.ERROR_715050);
		}

		if ((dateFrom != null) && (dateTo != null)) {
			if (dateFrom.after(dateTo)) {
				throw new EpException(MessageConst.ERROR_REPORT_780008);
			}
		}
	}

	public void resetSearch() {
/*		if (getManagedBean().getSelectedOrgProfile() != null) {
			if (getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
				getManagedBean().getSelectedOrgProfile().getSmSupplier()
						.setCompanyName(StringUtils.EMPTY);
			}
		}*/
		getManagedBean().setSelectedOrgProfile(null);//24635
		getManagedBean().setMofNo(StringUtils.EMPTY);
		getManagedBean().setRegType(StringUtils.EMPTY);
		getManagedBean().setEpNo(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

	public void onChangeRegType() {
		String label = SmCommonUtil.getMessage("lbl.rpt.mofregistereddatefrom",
				null);

		if (StringUtils.isBlank(getManagedBean().getRegType())) {
			label = SmCommonUtil.getMessage("lbl.rpt.allregistereddatefrom",
					null);
		} else if (getManagedBean().getRegType().equals(
				SmReportConst.SM_REG_TYPE_BASIC)) {
			label = SmCommonUtil.getMessage("lbl.rpt.registereddatefrom", null);
		}
		getManagedBean().setRegDateLabel(label);
	}

}
