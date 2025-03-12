package my.ep.web.backingbean.report.sm;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		// #33741 - start
		resetSearch();
		// #33741 - end
		//getManagedBean().setYearList(getYearList());
		setDefaultDateFromAndDateTo();
		onChangeRegType();
	}

	public void generateReport() {
		// #31893 - start
		Long supplierId = null;
		boolean isSprMv = false;
		// #31893 - end
		
		//#36125 - START
		ValidateSupplierPerformanceReport();
		//#36125 - END
		
		if (getManagedBean().getSelectedSupplierPerformance() == null && getManagedBean().getMofNo().isEmpty() && getManagedBean().getEpNo().isEmpty() ) { //36125 - Change to view table
				throw new EpException(MessageConst.ERROR_REPORT_780044);		
		} 
		if(getManagedBean().getDateFrom() != null && getManagedBean().getDateTo() != null) {
			dateValidation(getManagedBean().getDateFrom(), getManagedBean().getDateTo());
			
		} else if(getManagedBean().getDateFrom() != null && getManagedBean().getDateTo() == null) {
			throw new EpException(MessageConst.ERROR_REPORT_780055);
		} else if(getManagedBean().getDateFrom() == null && getManagedBean().getDateTo() != null) {
			throw new EpException(MessageConst.ERROR_REPORT_780054);
		}
		// #33741 - start
		// reset selectedOrgProfile if user select supplierId = 2
		if (getManagedBean().getSelectedSupplierPerformance() != null //36125 - Change to view table
				&& getManagedBean().getSelectedSupplierPerformance().getSupplierId() == 2) { //36125 - Change to view table
			getManagedBean().setSelectedSupplierPerformance(null); //36125 - Change to view table
		}
		// #33741 - end
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		if (getManagedBean().getSelectedSupplierPerformance() != null) { //36125 - Change to view table
			if (getManagedBean().getSelectedSupplierPerformance().getCompanyName() != null) { //36125 - Change to view table
				params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
						.getSelectedSupplierPerformance().getCompanyName()); //36125 - Change to view table
				supplierSearchReqDTO.setCompanyName(getManagedBean()
						.getSelectedSupplierPerformance().getCompanyName()); //36125 - Change to view table
				// #33741 - start
				// #31893 - start
				/*
				if (supplierSearchReqDTO.getCompanyName().equalsIgnoreCase(SmReportConst.SM_SUPPLIER_PERFORMANCE_COMP_1)
						|| supplierSearchReqDTO.getCompanyName().equalsIgnoreCase(SmReportConst.SM_SUPPLIER_PERFORMANCE_COMP_2)
						|| supplierSearchReqDTO.getCompanyName().equalsIgnoreCase(SmReportConst.SM_SUPPLIER_PERFORMANCE_COMP_2)) {
					getManagedBean().setReportCode(SmReportConst.SM_SUPPLIER_PERFORMANCE_V_RPT_VAL);
				}
				
				params.put(SmReportConst.PARAM_SUPPLIER_ID, String.valueOf(getManagedBean()
						.getSelectedOrgProfile().getSmSupplier().getSupplierId()));
				
				if (getManagedBean().getSelectedOrgProfile().getSmSupplier().isSprMv()) {
					getManagedBean().setReportCode(SmReportConst.SM_SUPPLIER_PERFORMANCE_V_RPT_VAL);
				}
				*/
				// #31893 - end
				
				//36125 - START Change to view table
				supplierId = getManagedBean().getSelectedSupplierPerformance().getSupplierId();
				SupplierSearchResDTO suResDTO = ValidateSupplier(supplierSearchReqDTO);
				isSprMv = suResDTO.getSmSupplier().isSprMv();
				//36125 - END Change to view table
				// #33741 - end
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
			
			// #33741 - start
			supplierId = suResDTO.getSmSupplier().getSupplierId();
			isSprMv = suResDTO.getSmSupplier().isSprMv();
			// #33741 - end
		}
		// #33741 - start		
		params.put(SmReportConst.PARAM_SUPPLIER_ID, String.valueOf(supplierId));
		
		if (isSprMv) {
			getManagedBean().setReportCode(SmReportConst.SM_SUPPLIER_PERFORMANCE_V_RPT_VAL);
		}
		// #33741 - end		
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
//		getManagedBean().setSelectedOrgProfile(null);//24635
		getManagedBean().setSelectedSupplierPerformance(null); //#36125
		getManagedBean().setMofNo(StringUtils.EMPTY);
		getManagedBean().setRegType(StringUtils.EMPTY);
		getManagedBean().setEpNo(StringUtils.EMPTY);
		//#33741 - START
		Date currDate = new Date();
		getManagedBean().setDateTo(currDate);
		getManagedBean().setDateFrom(null);
		//#33741 - END
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

	//#33741 - START
	public void findMinMaxDate(String dateType, Date selectedDate) {
		Calendar addedCal = new GregorianCalendar();
		Date currDate = new Date();
		addedCal.setTime(selectedDate);

		if (dateType.equalsIgnoreCase("from")) {
			addedCal.add(Calendar.MONTH, 12);
			if (1 == currDate.compareTo(addedCal.getTime())) {
				if(getManagedBean().getDateTo().compareTo(addedCal.getTime()) < 0){
					if(getManagedBean().getDateFrom().compareTo(getManagedBean().getDateTo()) > 0){
						getManagedBean().setDateTo(addedCal.getTime());
					}else{
						getManagedBean().setDateTo(getManagedBean().getDateTo());
					}	
				}else{
					getManagedBean().setDateTo(addedCal.getTime());
				}
			} else {
				getManagedBean().setMaxToDate(currDate);
				if(getManagedBean().getDateTo().compareTo(addedCal.getTime()) > 0){
					getManagedBean().setDateTo(currDate);
				}
			}
			getManagedBean().setMinToDate(selectedDate);
		} else if (dateType.equalsIgnoreCase("to")) {
			addedCal.setTime(getManagedBean().getDateFrom());
			addedCal.add(Calendar.MONTH, 12);
			if (selectedDate.compareTo(addedCal.getTime()) > 0) {
				addedCal.setTime(selectedDate);
				addedCal.add(Calendar.MONTH, -12);
				getManagedBean().setDateFrom(addedCal.getTime());
				getManagedBean().setMinToDate(addedCal.getTime());
			}
		}
	}
	//#33741 - END
	//#36125 - START
	/**
     * Validates if the Supplier Performance Report exists.
     * If the report does not exist, an EpException will be thrown.
     */
	public void ValidateSupplierPerformanceReport(){
		SupplierSearchReqDTO ssReqDTO = new SupplierSearchReqDTO();
		ssReqDTO.setMofAccountNo(getManagedBean().getMofNo());
		ssReqDTO.setEpNo(getManagedBean().getEpNo());
		ISupplierSearchSO supplierSearchSO = SmServiceLookup.getSupplierSearchSO();
		Boolean ssResDTO = supplierSearchSO.isSupplierPerformanceReportExist(ssReqDTO);
		if(!ssResDTO){
			throw new EpException(MessageConst.ERROR_REPORT_780056);
		}
	}
	//#36125 - END
}
