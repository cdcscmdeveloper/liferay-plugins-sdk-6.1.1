package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ProfileOfRegCompanyRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "profileOfRegCompanyRptBB")
@ViewScoped
public class ProfileOfRegCompanyRptBackingBean extends
		SmReportBackingBean<ProfileOfRegCompanyRptManagedBean> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(ProfileOfRegCompanyRptBackingBean.class);

	public void generateReport() {
		// #27397 - start
		validateMofRegNo(getManagedBean().getMofNo());
		// #27397 - end
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		params.put(SmReportConst.PARAM_ZONE,
				getManagedBean().getZone() == null ? StringUtils.EMPTY
						: getManagedBean().getZone());
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		
		for (Map.Entry<String, String> entry : params.entrySet()) {
			log.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}

		generateSmReport(params);
	}

	public void resetSearch() {
		this.getManagedBean().setZone(null);
		this.getManagedBean().setSupplierType(null);
		this.getManagedBean().setMofNo(null);
	}
	
	public void populateMofNo(BaseEntity entity) {
		SmSupplier selectedSupplier = (SmSupplier)entity;
		this.getManagedBean().setMofNo(selectedSupplier.getMofNo());
	}
	
	public String getMofNoCriteria() {
		String mofCriteria = "";
		if(this.getManagedBean().getSupplierType() != null) {
			mofCriteria += "|smMofAccounts.supplierType^" + this.getManagedBean().getSupplierType();
		}
		if(this.getManagedBean().getZone() != null) {
			mofCriteria += "|smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.pmZoneStates.pmZone.zoneName^" + this.getManagedBean().getZone().toUpperCase();
		}
		return mofCriteria;
	}
}
