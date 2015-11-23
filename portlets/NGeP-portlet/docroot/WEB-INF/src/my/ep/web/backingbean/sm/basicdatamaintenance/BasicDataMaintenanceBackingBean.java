package my.ep.web.backingbean.sm.basicdatamaintenance;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.basicdatamaintenance.BasicDataMaintenanceManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "basicDataMaintenanceBackingBean")
@RequestScoped
public class BasicDataMaintenanceBackingBean extends
		BaseBackingBean<BasicDataMaintenanceManagedBean> {

	/**
	 * Pre-render and redirect to Basic Account Maintenance screen
	 */
	public void preRenderBasicDataMaintenance() {
		renewSesionObject();
		resetSearchCriteria();
		SmCommonUtil.setMDCUserId();
		SmCommonUtil.redirectToPage("searchBasicDataMaintenance.xhtml", "");
	}

	public void viewSupplier(SmSupplier supplier) {
		getManagedBean().setSelectedSupplier(supplier);
		getManagedBean().setOldBusinessType(supplier.getBusinessType());
		MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
		mofRegistrationReqDTO.setSupplierId(getManagedBean()
				.getSelectedSupplier().getSupplierId());
		SmAppl activeAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveActiveSmApplBySupp(mofRegistrationReqDTO).getSmAppl();
		if (activeAppl != null) {
			throw new EpException(MessageConst.ERROR_722618);
		}
		getManagedBean().setBusinessTypeItems(
				SmCommonUtil.getInstance().getBusinessTypeItems(
						getManagedBean().getSelectedSupplier()
								.getBusinessType(),
						getManagedBean().getSelectedSupplier().getSmAppl()
								.getSupplierType(),
						getManagedBean().getSelectedSupplier().getSmAppl()
								.getApplType()));
		SmCommonUtil.redirectToPage("basicDataMaintenance.xhtml", "");
	}

	public void search() {
		getManagedBean().searchSupplier();
	}

	public void reset() {
		resetSearchCriteria();
	}

	public void back() {
		SmCommonUtil.redirectToPage("searchBasicDataMaintenance.xhtml", "");
	}

	public void onChangeSearchBy(AjaxBehaviorEvent e) {
		getManagedBean().setSearchSupplier(new SmSupplier());
	}

	public void submit() {
		if (getManagedBean().getSelectedSupplier() != null) {
			SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
			reqDTO.setSupplier(getManagedBean().getSelectedSupplier());
			reqDTO.setRemark(getManagedBean().getRemarks());
			reqDTO.setDocType(ParameterConst.SM_BASIC_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
			reqDTO.setOldBusinessType(getBusinessTypeLabel(getManagedBean()
					.getOldBusinessType()));
			reqDTO.setNewBusinessType(getBusinessTypeLabel(getManagedBean()
					.getSelectedSupplier().getBusinessType()));
			SmServiceLookup.getSupplierDataMaintenanceSO()
					.updateBasicSupplierInfo(reqDTO);
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721221);
		} else {
			throw new EpException("Failed to update supplier info!!");
		}
	}

	private String getBusinessTypeLabel(String businessType) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean()
				.getBusinessTypeItems())) {
			for (SelectItem item : getManagedBean().getBusinessTypeItems()) {
				if (item.getValue() != null
						&& item.getValue().equals(businessType)) {
					retVal = item.getLabel();
					break;
				}
			}
		}
		return retVal;
	}

	private void resetSearchCriteria() {
		getManagedBean().setSearchMode("E");
		getManagedBean().setSearchSupplier(new SmSupplier());
	}

}
