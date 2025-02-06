package my.ep.web.backingbean.report.fl.cc;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.cc.SumForPoCancellationReasonRptManagedBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Backing Bean for SumForPoCancellationReason.
 *
 * Enhancement 29550 - Ad Hoc Report
 * @author Syafiza
 * @since 14/11/2022
 */
@ManagedBean(name = "ccSumForPoCancelReasonRptBB")
@SessionScoped
public class SumForPoCancellationReasonRptBackingBean extends FlReportBackingBean<SumForPoCancellationReasonRptManagedBean> {
private static final long serialVersionUID = 1L;
	
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(SumForPoCancellationReasonRptBackingBean.class);
	
	public SumForPoCancellationReasonRptBackingBean() {
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
		if (StringUtils.isNotBlank(getManagedBean().getPhysicalContractNo())) {
			params.put(FlReportConst.FL_RPT_PHY_CONTRACT_NO, getManagedBean().getPhysicalContractNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getContractNo())) {
			params.put(FlReportConst.FL_RPT_CONTRACT_NO, getManagedBean().getContractNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getLoaNo())) {
			params.put(FlReportConst.FL_RPT_SST_NO, getManagedBean().getLoaNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPhysicalLoaNo())) {
			params.put(FlReportConst.FL_RPT_PHY_LOA_NO, getManagedBean().getPhysicalLoaNo());
		}
		if(getManagedBean().getSelectedOrgProfile() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSelectedOrgProfile().getSmSupplier().getSupplierId().toString());
		} else if(getManagedBean().getSupplierId() != null){
			params.put(FlReportConst.FL_RPT_SUPPL_NAME, getManagedBean().getSupplierId());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}
		if(StringUtils.isNotBlank(getManagedBean().getGroup())){
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}
		
		generateFlReport(params);
		
	}
	
	/**
	 * reset action
	 */
	public void resetAction(){
		if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())){
			getManagedBean().setMinistry(null);
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
			}else if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
		}
		
		resetSearchCriteriaValues();
	}
	
	public void onChangeFromToDate() {
		if(this.getManagedBean().getFromDate().after(this.getManagedBean().getToDate()) || 
				this.getManagedBean().getToDate().before(this.getManagedBean().getFromDate())){
			throw new EpException(MessageConst.ERROR_REPORT_780071);
		}
		
		getContractDuration(this.getManagedBean().getFromDate(), this.getManagedBean().getToDate());
	}

}
