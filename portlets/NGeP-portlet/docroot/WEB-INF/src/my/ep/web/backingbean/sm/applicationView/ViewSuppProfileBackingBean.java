package my.ep.web.backingbean.sm.applicationView;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import com.liferay.portal.model.Role;

@ManagedBean(name = "viewSuppProfileBackingBean")
@RequestScoped
public class ViewSuppProfileBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	private static final int SEARCH_BY_COMPANY_NAME = 1;
	private static final int SEARCH_BY_BUSINESS_REG_NO = 2;
	private static final int SEARCH_BY_EP_NO = 3;

	/**
	 * Pre - render for Search Supplier Profile
	 */
	public void preRenderSearchSupplierProfile() {
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/revisionSmApplView/viewSupplierProfile/searchSupplierProfilePage.xhtml",
						"");
	}

	/**
	 * Redirect back to Search Supplier Profile page
	 * 
	 * @return String
	 */
	public String backToSearch() {
		return "searchSupplierProfilePage.xhtml";
	}

	/**
	 * View Supplier Profile
	 */
	public void viewApplication(SmAppl selectedSmAppl) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(selectedSmAppl.getApplNo());
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl();
		setApplication(smAppl);
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/revisionSmApplView/viewSupplierProfile/smViewProfileSideMenu_BasePage.xhtml",
						"");
	}

	/**
	 * Search By on change and reset value
	 * 
	 * @param e
	 */
	public void onChangeSearchBy(AjaxBehaviorEvent e) {
		if (getManagedBean().getSearchBy() == SEARCH_BY_COMPANY_NAME) {
			getManagedBean().getSearchSupplier().setEpNo("");
			getManagedBean().getSearchSupplier().setRegNo("");
		} else if (getManagedBean().getSearchBy() == SEARCH_BY_BUSINESS_REG_NO) {
			getManagedBean().getSearchSupplier().setEpNo("");
			getManagedBean().getSearchSupplier().setCompanyName("");
		} else if (getManagedBean().getSearchBy() == SEARCH_BY_EP_NO) {
			getManagedBean().getSearchSupplier().setCompanyName("");
			getManagedBean().getSearchSupplier().setRegNo("");
		}
	}

	/**
	 * Reset search value
	 */
	public void resetSearch() {
		getManagedBean().getSearchSupplier().setCompanyName("");
		getManagedBean().getSearchSupplier().setEpNo("");
		getManagedBean().getSearchSupplier().setRegNo("");
	}

	/**
	 * Set login role
	 */
	private void setUserLoginRole() {
		FacesScopes sc = new FacesScopes();
		List<Role> loginUserRoles = sc.getUserRoleList();
		List<String> roleNames = new ArrayList<String>();
		for (Role r : loginUserRoles) {
			roleNames.add(r.getName());
		}
		if (sc.hasRoleUser(SupplierManagementConst.PO_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.PO_HQ_TEMP)) {
			getManagedBean().setPoUser(true);
		} else if (sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ_TEMP)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER)) {
			getManagedBean().setApproverUser(true);
		} else if (sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_USER)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_USER)
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_USER)) {
			getManagedBean().setSupplierUser(true);
			if (sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)
					|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_USER)) {
				getManagedBean().setMofSupplier(true);
			}
		}
	}

	/**
	 * Initialized supplier by login role
	 */
	private void initLogin() {
		setUserLoginRole();
		if (getManagedBean().isSupplierUser()) {
			getManagedBean().setSearchSupplier(
					SmApplicationUtil.getCurrentSupplier());
		} else {
			getManagedBean().setSearchSupplier(new SmSupplier());
			getManagedBean().getSearchSupplier().setCompanyName("");
			getManagedBean().getSearchSupplier().setEpNo("");
			getManagedBean().getSearchSupplier().setRegNo("");
		}
	}

}
