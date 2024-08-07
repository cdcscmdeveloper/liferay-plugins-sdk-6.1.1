package my.ep.web.backingbean.report.fl.po;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.po.SupplierTransactionRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Backing Bean for SupplierTransaction.
 *
 * Enhancement 29550 - Ad Hoc Report
 * @author Syafiza
 * @since 14/11/2022
 */
@ManagedBean(name = "poSuppTransRptBB")
@SessionScoped
public class SupplierTransactionRptBackingBean extends FlReportBackingBean<SupplierTransactionRptManagedBean> {
private static final long serialVersionUID = 1L;
	
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(SupplierTransactionRptBackingBean.class);
	
	public SupplierTransactionRptBackingBean() {
		if(getManagedBean().getMinistry() == null){
			if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()) || getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
				defaultPtjGrpAndPtjValues();
			}else{
				defaultPtjAndMinistryValues();
			}
		}
		
		if(getManagedBean().getFromDate() == null){
			setDefaultDateFromAndDateTo();
		}
	}
	
	/**
	 * generate report
	 */
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		
		retrieveReportValueFromDb();
		onChangeFromToDate();
		
		params.put(FlReportConst.FL_RPT_FROM_DATE, df.format(getManagedBean().getFromDate()));
		params.put(FlReportConst.FL_RPT_TO_DATE, df.format(getManagedBean().getToDate()));
		
		if(getManagedBean().getMinistry() != null){
			params.put(FlReportConst.FL_RPT_MINISTRY, getManagedBean().getMinistry().getOrgProfileId().toString());
		}
		if (getManagedBean().getPtjGroup() != null) {
			params.put(FlReportConst.FL_RPT_KUMPULAN, getManagedBean().getPtjGroup().getOrgProfileId().toString());
		}
		if (getManagedBean().getPtj() != null) {
			params.put(FlReportConst.FL_RPT_PTJ_NAME, getManagedBean().getPtj().getOrgProfileId().toString());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPanelName())) {
			params.put(FlReportConst.FL_RPT_CC_PANEL_NAME, getManagedBean().getPanelName());
		}
		if(getManagedBean().getSelectedOrgProfile() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSelectedOrgProfile().getSmSupplier().getSupplierId().toString());
		} else if(getManagedBean().getSupplierId() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSupplierId());
		}
		if (StringUtils.isNotBlank(getManagedBean().getBusinessRegNo())) {
			params.put(FlReportConst.FL_RPT_BUSINESS_REG_NO, getManagedBean().getBusinessRegNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getMofNo())) {
			params.put(FlReportConst.FL_RPT_MOF_NO, getManagedBean().getMofNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getEpNo())) {
			params.put(FlReportConst.FL_RPT_EP_NO, getManagedBean().getEpNo());
		}
		if(getManagedBean().getFinancialYear() != null){
			params.put(FlReportConst.FL_RPT_FN_YEAR, getManagedBean().getFinancialYear());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderStatus())) {
			params.put(FlReportConst.FL_RPT_PO_STATUS, getManagedBean().getPurchaseOrderStatus());
		}
		if(StringUtils.isNotBlank(getManagedBean().getGroup())){
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}
		
		generateFlReport(params);
		
	}
	
	/**
	 * reset action
	 */
	public void resetAction(){
		if(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())){
			getManagedBean().setMinistry(null);
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
			}else if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
		}
	
		resetSearchCriteriaValues();
		getManagedBean().setPurchaseOrderStatus(null);
		getManagedBean().setBusinessRegNo(null);
		getManagedBean().setMofNo(null);
		getManagedBean().setEpNo(null);
		getManagedBean().setFinancialYear(null);
	}
	
	/**
	 * retrieve financial year options for current year and up to previous 10 years
	 * @return financial year options
	 */
	public static List<SelectItem> getFinancialYearOpt() {
		int years = 0;
		List<SelectItem>financialYearOpt = new ArrayList<SelectItem>();
		for(Integer i = 0;i < 11;i++){
			years = CommonMethod.getYear(-i);
			 financialYearOpt.add(new SelectItem(years, Integer.toString(years)));
		}
		return financialYearOpt;
	}
	
	public void onChangeFromToDate() {
		if(this.getManagedBean().getFromDate().after(this.getManagedBean().getToDate()) || 
				this.getManagedBean().getToDate().before(this.getManagedBean().getFromDate())){
			throw new EpException(MessageConst.ERROR_REPORT_780071);
		}
		
		getContractDuration(this.getManagedBean().getFromDate(), this.getManagedBean().getToDate());
	}
	
	public void autopopSuppData(){
		if(StringUtils.isNotBlank(this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName())){
			log.info("**inside autopopSuppData**");
			retrieveSuppDataByCompName(this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getCompanyName(),
					this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getRegNo());	
		}
	}
	
	/**
	 * Get status select items by module and report category for dropdown.
	 * 
	 * @param moduleCode
	 *            the module code
	 * @param reportCategory
	 *            the report category
	 * @return array of SelectItem
	 */
	public SelectItem[] getPOStatusDropdownValues(String moduleCode, String reportCategory) {
		log.info("**inside getPOStatusDropdownValues**");
		log.info("moduleCode: "+ moduleCode + " , "+ "reportCategory: " + reportCategory);
		int i = 1;
		String newdocType = null;
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			if(reportCategory != null){
				if(("PO").equals(reportCategory)){
					newdocType = "PO";
				}else{
					newdocType = "CO";
				}
			}
			
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance().getStatusDescList(moduleCode, newdocType, null);

			items = new SelectItem[statusDescs.size()];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			
			for (PmStatusDesc statusDesc : statusDescs) {
				if(!("Cancelled").equals(statusDesc.getStatusName())){
					items[i++] = new SelectItem(statusDesc.getPmStatus()
							.getStatusId(), statusDesc.getStatusName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage( null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}

		return items;
	}

}
