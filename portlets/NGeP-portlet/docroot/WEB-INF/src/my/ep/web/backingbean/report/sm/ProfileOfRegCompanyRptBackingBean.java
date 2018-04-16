package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

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

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		params.put(SmReportConst.PARAM_ZONE,
				getManagedBean().getZone() == null ? StringUtils.EMPTY
						: getManagedBean().getZone());
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());

		generateSmReport(params);
	}

	public void resetSearch() {

	}
	
	public void populateMofNo(BaseEntity entity) {
		SmSupplier selectedSupplier = (SmSupplier)entity;
		/*SmMofAccount mofAccount = null;
		for(SmMofAccount acc : selectedSupplier.getSmMofAccounts()) {
			if(acc.isRecordStatus()) {
				mofAccount = acc;
				break;
			}
		}*/
		this.getManagedBean().setMofNo(selectedSupplier.getMofNo());
	}
	
	public String constructMofNoCriteria() {
		String mofCriteria = "";
		if(this.getManagedBean().getSupplierType() != null) {
			mofCriteria += "|smMofAccounts.supplierType^" + this.getManagedBean().getSupplierType();
		}
		if(this.getManagedBean().getZone() != null) {
			mofCriteria += "|smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.pmZoneStates.pmZone.zoneName^" + this.getManagedBean().getZone();
		}
		return mofCriteria;
	}
}
