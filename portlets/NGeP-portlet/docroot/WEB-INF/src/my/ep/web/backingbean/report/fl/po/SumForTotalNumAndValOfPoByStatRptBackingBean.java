package my.ep.web.backingbean.report.fl.po;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.po.SumForTotalNumAndValOfPoByStatRptManagedBean;

/**
 * Backing Bean for SumForTotalNumAndValOfPoByStat.
 *
 * Enhancement 29550 - Ad Hoc Report
 * @author Syafiza
 * @since 14/11/2022
 */
@ManagedBean(name = "poSumTotalNumValofPoRptBB")
@SessionScoped
public class SumForTotalNumAndValOfPoByStatRptBackingBean extends FlReportBackingBean<SumForTotalNumAndValOfPoByStatRptManagedBean> {

	private static final long serialVersionUID = 1L;
	
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(SumForTotalNumAndValOfPoByStatRptBackingBean.class);
	
	public SumForTotalNumAndValOfPoByStatRptBackingBean() {
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
		if (StringUtils.isNotBlank(getManagedBean().getGroupStatus())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_GROUP_STATUS, getManagedBean().getGroupStatus());
		}
		if(StringUtils.isNotBlank(getManagedBean().getGroup())){
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}
		if(getManagedBean().getPoGenDateFrom() != null){
			params.put(FlReportConst.FL_RPT_PO_GENERATED_FROM_DATE, df.format(getManagedBean().getPoGenDateFrom()));
		}
		if(getManagedBean().getPoGenDateTo() != null){
			params.put(FlReportConst.FL_RPT_PO_GENERATED_TO_DATE, df.format(getManagedBean().getPoGenDateTo()));
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
		getManagedBean().setPoGenDateFrom(null);
		getManagedBean().setPoGenDateTo(null);
	}
	
	public void onChangeFromToDate() {
		if(this.getManagedBean().getFromDate().after(this.getManagedBean().getToDate()) || 
				this.getManagedBean().getToDate().before(this.getManagedBean().getFromDate())){
			throw new EpException(MessageConst.ERROR_REPORT_780071);
		}
		if(this.getManagedBean().getPoGenDateFrom()!= null && this.getManagedBean().getPoGenDateTo()!=null){
			if(this.getManagedBean().getPoGenDateFrom().after(this.getManagedBean().getPoGenDateTo())){
				throw new EpException(MessageConst.ERROR_REPORT_780079);
			}else if(this.getManagedBean().getPoGenDateFrom().before(this.getManagedBean().getFromDate()) ||
					this.getManagedBean().getPoGenDateTo().after(this.getManagedBean().getToDate())){
				throw new EpException(MessageConst.ERROR_REPORT_780078);
			}	
		}else if((this.getManagedBean().getPoGenDateFrom()!= null && this.getManagedBean().getPoGenDateTo()==null) || 
				(this.getManagedBean().getPoGenDateFrom()== null && this.getManagedBean().getPoGenDateTo()!=null)){
			throw new EpException(MessageConst.ERROR_REPORT_780080);
		}
		
		getContractDuration(this.getManagedBean().getFromDate(), this.getManagedBean().getToDate());
	}
}
