package my.ep.web.backingbean.sm.supplierdatamaintenance;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "supplierDataMaintenanceBackingBean")
@RequestScoped
public class SupplierDataMaintenanceBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	public void preRenderSupplierDataMaintenance() {
		renewSesionObject();
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		getManagedBean().setRegStatusDescList(retrieveRegistrationStatusList());
		getManagedBean().setZoneList(
				SmServiceLookup.getZoneSO().retrieveActiveZoneList()
						.getZoneList());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/supplierdatamaintenance/searchSupplierDataMaintenance.xhtml",
						"");
	}

	public void navigateToMaintenance(SmAppl selectedSmAppl) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(selectedSmAppl.getApplNo());
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
		setApplication(getManagedBean().getSmAppl());
		SmApplicationUtil.assignApplActiveAndApprovedCategoryList(
				getManagedBean().getApprovedCategoryList(), getManagedBean()
						.getActiveCategoryList(), getManagedBean().getSmAppl());
		SmApplicationUtil.setOverLappingCategories(
				getManagedBean().getSmAppl(), getManagedBean()
						.getOverLappedCategoryMsg(), getManagedBean()
						.isOverLappedCategoryExists());
		setCategoryTypeConfirm();
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/supplierdatamaintenance/editSupplierDataMaintenance.xhtml",
						"");
	}

	public void search() {
		getManagedBean().searchSupplier();
	}

	public void update() {
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		SupplierDataMaintenanceResDTO resDTO = new SupplierDataMaintenanceResDTO();
		try {
			Long pmStatId = getManagedBean().getSmAppl().getPmStatus()
					.getStatusId();
			for (PmStatusDesc desc : getManagedBean().getRegStatusDescList()) {
				if (pmStatId.equals(desc.getPmStatus().getStatusId())) {
					getManagedBean().getSmAppl()
							.setPmStatus(desc.getPmStatus());
					if (desc.getPmStatus().getPmRegStatus() != null) {
						getManagedBean().getSmAppl().setPmRegStatus(
								desc.getPmStatus().getPmRegStatus());
						getManagedBean()
								.getSmAppl()
								.getSmSupplier()
								.setBumi(
										desc.getPmStatus().getPmRegStatus()
												.isBumiAppl());
						break;
					}
				}
			}
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
					getManagedBean().getSmAppl(),
					SupplierManagementConst.SUPPLIER_MAINTENANCE_REGISTRATION_STATUS);
			reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);

			resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
					.updateSupplierInfo(reqDTO);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmMofAccount(resDTO.getSmMofAccount());

		} catch (OptimisticLockException opt) {
			throw new EpException(opt.getMessage());
		} catch (PersistenceException pers) {
			throw new EpException(pers.getMessage());
		} catch (NullPointerException nex) {
			throw new EpException(nex.getMessage());
		} finally {
			reqDTO = null;
		}
	}

	public String backToSearchSupplierMaintenance() {
		return "searchSupplierDataMaintenance.xhtml";
	}

	/**
	 * Ajax call to set the application category selected.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void applicationCategorySelected(AjaxBehaviorEvent e) {
		boolean isPoOrApproverUser = false;
		if (getManagedBean().isApproverUser() || getManagedBean().isPoUser()) {
			isPoOrApproverUser = true;
		}
		SmApplicationUtil.setCategoryCodeSelected(0, isPoOrApproverUser,
				getManagedBean().getSmAppl(), getManagedBean()
						.getBusinessTypeVal(), getManagedBean()
						.getRegCatCodeConfirmHeader(), getManagedBean()
						.getRegCatCodeConfirmMsg(), getManagedBean()
						.isReviewSiteVisit());
	}

	private SmTrackingDairyVO assignTrackingDairyValues(SmAppl appl,
			String actionDesc) {
		SmTrackingDairyVO trckVO = new SmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getManagedBean().getRoleCode());
		trckVO.setDocId(appl.getApplId());
		trckVO.setDocNo(appl.getApplNo());
		trckVO.setDocType("SR");
		trckVO.setGroupId(null);
		trckVO.setStatus(appl.getPmStatus());
		trckVO.setGroupDocType(null);
		return trckVO;

	}
	
	private void setCategoryTypeConfirm(){
		if (getManagedBean().getSmAppl().getApplCategoryType()
				.equalsIgnoreCase("N")) {
			getManagedBean()
					.setCatTypeConfirm(
							SmCommonUtil
									.getMessage(
											"lbl.sm.categorycode.note.normal",
											null));
		} else {
			getManagedBean().setCatTypeConfirm(
					SmCommonUtil.getMessage(
							"lbl.sm.categorycode.note.special",
							null));
		}
	}

}
