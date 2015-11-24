package my.ep.web.backingbean.sm.basicdatamaintenance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.basicdatamaintenance.BasicDataMaintenanceManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import com.liferay.portal.kernel.util.StringUtil;

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
		setBusinessTypeList();
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

	private void setBusinessTypeList() {
		List<SelectItem> businessTypeItems = new ArrayList<SelectItem>();
		try {
			SelectItem[] items = getDropdownCodeValuesBySearchBean(
					SupplierManagementConst.SUPPLIER_BUSINESS_TYPE, null);
			PmCountry companyCountry = SmCommonUtil.getInstance()
					.getCountryById(
							getManagedBean().getSelectedSupplier().getSmAppl()
									.getSmCompanyBasic()
									.getSmCompanyAddresses().get(0)
									.getSmAddress().getPmCountry()
									.getCountryId());
			for (SelectItem item : items) {
				if (item.getValue() != null) {
					if (companyCountry.getCountryName().equalsIgnoreCase(
							"SABAH")) {
						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {
							businessTypeItems.add(item);
						}
					} else if (companyCountry.getCountryName()
							.equalsIgnoreCase("SARAWAK")) {
						if (item.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
								|| item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
							businessTypeItems.add(item);
						}
					} else {
						if (!item
								.getValue()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
								&& !item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)
								&& !item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
								&& !item.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
							if (StringUtils.isNotBlank(getManagedBean()
									.getSelectedSupplier().getSmAppl()
									.getSupplierType())
									&& getManagedBean()
											.getSelectedSupplier()
											.getSmAppl()
											.getSupplierType()
											.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
								if (!item
										.getValue()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN)
										&& !item.getValue()
												.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI)
										&& !item.getValue()
												.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN)
										&& !item.getValue()
												.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)) {
									businessTypeItems.add(item);
								}
							} else {
								businessTypeItems.add(item);
							}
						}
					}
				}
			}
		} catch (Exception ex) {

		}
		// sort
		if (!SmCommonUtil.isCollectionEmpty(businessTypeItems)) {
			Collections.sort(businessTypeItems, new Comparator<SelectItem>() {
				@Override
				public int compare(final SelectItem s1, final SelectItem s2) {
					return StringUtil.upperCase(s1.getLabel()).compareTo(
							StringUtil.upperCase(s2.getLabel()));
				}
			});
		}
		getManagedBean().setBusinessTypeItems(businessTypeItems);
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
