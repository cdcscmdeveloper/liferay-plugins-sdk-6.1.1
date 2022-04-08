package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.DisciplinaryActionConst;
import my.ep.constants.CommonConstants;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DirOwnCeoOneCompRptManagedBean;
import my.ep.web.managedbean.report.sm.DiscActionExecSummaryRptManagedBean;
import my.ep.web.managedbean.sm.complaint.ComplaintManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "discActionExecSummaryRptBB")
@ViewScoped
public class DiscActionExecSummaryRptBackingBean extends SmReportBackingBean<DiscActionExecSummaryRptManagedBean> {
	private static final long serialVersionUID = 1L;
	private final Long[] discDropDownStatusArr = new Long[]{DisciplinaryActionConst.REVIEW_BY_PTPK,
			DisciplinaryActionConst.REVIEW_SUPP_RESP, DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT};
	public static Logger log = Logger.getLogger(DiscActionExecSummaryRptBackingBean.class);

	public DiscActionExecSummaryRptBackingBean(){
		getDiscZoneByUser();
		/*if(getManagedBean().getDiscActionStatusLs() == null ){
			getManagedBean().setDiscActionStatusLs(new ArrayList<PmStatusDesc>());
			if(getManagedBean().getDiscActionStatusLs().size() < 1){
				List<PmStatusDesc> statusDescLs = SmCommonUtil.
						retrievePmStatusList(ModuleCodeConst.SUPPLIER_MANAGEMENT,DisciplinaryActionConst.DA_STATUS_DOC_TYPE,
								new FacesScopes().getUserLocale().getLanguage());
				
				if(statusDescLs.size() > 0){
					for(PmStatusDesc status : statusDescLs){
						if(Arrays.asList(discDropDownStatusArr).contains(status.getPmStatus().getStatusId())){
							getManagedBean().getDiscActionStatusLs().add(status);
						}
					}
				}
			}
		}*/
	}
	
	public void generateReport() {
		validateMofRegNo();
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_DISC_ZONE,
				getManagedBean().getZone() == null ? StringUtils.EMPTY
						: getManagedBean().getZone());
		if (!StringUtils.isBlank(getManagedBean().getSupplierType())) {
			params.put(SmReportConst.PARAM_DISC_SUPP_TYPE, getManagedBean()
					.getSupplierType());
		}
		
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			log.info("Supplier ID>>>>>>" + getManagedBean()
			.getSelectedOrgProfile().getSmSupplier().getSupplierId());
			
			params.put(SmReportConst.PARAM_DISC_SUPP_ID, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getSupplierId().toString());
		}
/*		if (!StringUtils.isBlank(getManagedBean().getRole())) {
			params.put(SmReportConst.PARAM_ROLE, getManagedBean().getRole());
		}*/
		if (getManagedBean().getDateFrom() != null) {
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		}
		if (getManagedBean().getDateTo() != null) {
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getDiscActionStatus().toString())) {
			params.put(SmReportConst.PARAM_DISC_STATUS, getManagedBean()
					.getDiscActionStatus().toString());
		}

		generateSmReport(params);
	}

	public void resetSearch() {
		//getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType(null);
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setDiscActionStatus(null);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().setSelectedOrgProfile(null);
		}
		//setDefaultDateFromAndDateTo();
	}
	
	public String getDiscZoneByUser(){
		PmZone zone = SmCommonUtil.getInstance().getUserZone();
		
		if(zone != null){
			getManagedBean().setZone(zone.getZoneName().toUpperCase());
		}else{
			getManagedBean().setZone(null);
		}
		
		log.info("User Zone>>>>>" + getManagedBean().getZone());
		return getManagedBean().getZone();
	}
	
	public List<PmStatusDesc> retrieveDiscStatuses(){
		List<PmStatusDesc> statDescs = new ArrayList<PmStatusDesc>();
		List<PmStatusDesc> statusDescLs = SmCommonUtil.
				retrievePmStatusList(ModuleCodeConst.SUPPLIER_MANAGEMENT,DisciplinaryActionConst.DA_STATUS_DOC_TYPE,
						new FacesScopes().getUserLocale().getLanguage());

		if(statusDescLs.size() > 0){
			for(PmStatusDesc status : statusDescLs){
				if(Arrays.asList(discDropDownStatusArr).contains(status.getPmStatus().getStatusId())){
					statDescs.add(status);
				}
			}
		}
		
		Collections.sort(statDescs, new Comparator<PmStatusDesc>() {
		    @Override
		    public int compare(PmStatusDesc d1, PmStatusDesc d2) {
		        return d1.getPmStatus().getStatusId().compareTo(d2.getPmStatus().getStatusId());
		    }
		});

		return statDescs;
	}
}
