package my.ep.web.managedbean.sm.govtsellerregistration;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "govtSellerMB")
@SessionScoped
public class GovtSellerRegistrationManagedBean extends RegistrationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8672731459327980724L;

	private IPagingDataModel<Object[]> orgProfileDataModel = new OrgProfilePagingDataModel();

	public String targetPage;
	public String orgTypeCode;
	public String agencyName;
	public List<SelectItem> agencyTypeItems;
	public List<SelectItem> identificationTypeItems;
	public List<SelectItem> branchCompanyStates;
	public List<SelectItem> branchCompanyDistricts;
	public List<SelectItem> branchCompanyCities;
	public List<SelectItem> salutationItems;
	public List<SmSupplierBranch> selectedBranches;
	public SmSupplierBranch selectedBranch;
	private boolean showParentMsg;
	private boolean valueReqd;
	private boolean showGst;
	private boolean showBranchPointOfContact;

	private class OrgProfilePagingDataModel extends PagingDataModel<Object[]> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public OrgProfilePagingDataModel() {
			super("orgProfileDtId");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setObjArrSearchBean(searchBean);

			try {
				return getOrgProfileResultBean(reqDTO);
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<Object[]> getOrgProfileResultBean(
				RegistrationReqDTO reqDTO) {
			reqDTO.setOrgTypeCode(orgTypeCode);
			reqDTO.setOrgName(agencyName);
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			return SmServiceLookup.getRegistrationSO()
					.retrieveOrgProfileUsingNativeQuery(reqDTO)
					.getObjectArrResultBean();
		}
	}

	public void search() {
		orgProfileDataModel.reloadData();
	}

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

	public String getOrgTypeCode() {
		return orgTypeCode;
	}

	public void setOrgTypeCode(String orgTypeCode) {
		this.orgTypeCode = orgTypeCode;
	}

	public IPagingDataModel<Object[]> getOrgProfileDataModel() {
		return orgProfileDataModel;
	}

	public void setOrgProfileDataModel(
			IPagingDataModel<Object[]> orgProfileDataModel) {
		this.orgProfileDataModel = orgProfileDataModel;
	}

	public List<SelectItem> getAgencyTypeItems() {
		return agencyTypeItems;
	}

	public void setAgencyTypeItems(List<SelectItem> agencyTypeItems) {
		this.agencyTypeItems = agencyTypeItems;
	}

	public List<SelectItem> getIdentificationTypeItems() {
		return identificationTypeItems;
	}

	public void setIdentificationTypeItems(
			List<SelectItem> identificationTypeItems) {
		this.identificationTypeItems = identificationTypeItems;
	}

	public List<SmSupplierBranch> getSelectedBranches() {
		return selectedBranches;
	}

	public void setSelectedBranches(List<SmSupplierBranch> selectedBranches) {
		this.selectedBranches = selectedBranches;
	}

	public SmSupplierBranch getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(SmSupplierBranch selectedBranch) {
		this.selectedBranch = selectedBranch;
	}

	public boolean isValueReqd() {
		return valueReqd;
	}

	public void setValueReqd(boolean valueReqd) {
		this.valueReqd = valueReqd;
	}

	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	public List<SelectItem> getBranchCompanyStates() {
		return branchCompanyStates;
	}

	public void setBranchCompanyStates(List<SelectItem> branchCompanyStates) {
		this.branchCompanyStates = branchCompanyStates;
	}

	public List<SelectItem> getBranchCompanyDistricts() {
		return branchCompanyDistricts;
	}

	public void setBranchCompanyDistricts(
			List<SelectItem> branchCompanyDistricts) {
		this.branchCompanyDistricts = branchCompanyDistricts;
	}

	public List<SelectItem> getBranchCompanyCities() {
		return branchCompanyCities;
	}

	public void setBranchCompanyCities(List<SelectItem> branchCompanyCities) {
		this.branchCompanyCities = branchCompanyCities;
	}

	public boolean isShowGst() {
		return showGst;
	}

	public void setShowGst(boolean showGst) {
		this.showGst = showGst;
	}

	public boolean isShowBranchPointOfContact() {
		return showBranchPointOfContact;
	}

	public void setShowBranchPointOfContact(boolean showBranchPointOfContact) {
		this.showBranchPointOfContact = showBranchPointOfContact;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public List<SelectItem> getSalutationItems() {
		return salutationItems;
	}

	public void setSalutationItems(List<SelectItem> salutationItems) {
		this.salutationItems = salutationItems;
	}

}
