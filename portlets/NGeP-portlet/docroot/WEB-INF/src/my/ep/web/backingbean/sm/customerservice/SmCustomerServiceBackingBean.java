package my.ep.web.backingbean.sm.customerservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.managedbean.sm.customerservice.SmCustomerServiceManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "smCustomerServiceBackingBean")
@RequestScoped
public class SmCustomerServiceBackingBean extends
		BaseReviewApplicationBackingBean<SmCustomerServiceManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2440882995327138193L;

	private static int SEARCH_PAGE = 1;
	private static int DETAILS_PAGE = 2;

	public void preRenderCustomerService() {
		renewSesionObject();
		getManagedBean().setSearchSupplier(new SmSupplier());
		getManagedBean().getSearchSupplier().setCompanyName(StringUtils.EMPTY);
		getManagedBean().getSearchSupplier().setEpNo(StringUtils.EMPTY);
		getManagedBean().getSearchSupplier().setRegNo(StringUtils.EMPTY);
		getManagedBean().setTargetPage(SEARCH_PAGE);
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/customerService/smCustomerServiceBasePage.xhtml",
				"", null);
	}

	/**
	 * Initialized application by application ID
	 * 
	 * @param String
	 *            applNo
	 */
	public void view(Long applId) {
		getManagedBean().setViewSupplierProfile(true);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(applId);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		setApplication(smAppl);

		// set MOF registration status
		String mofStatus = StringUtils.EMPTY;
		if (getManagedBean().getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
			mofStatus = SmCommonUtil.getMessage("lbl.sm.mof.status.deleted",
					null);
		} else {
			Date currDate = new Date();
			if (getManagedBean().getSmMofAccount() != null) {
				if (currDate.after(getManagedBean().getSmMofAccount()
						.getExpDate())) {
					mofStatus = SmCommonUtil.getMessage(
							"lbl.sm.mof.status.expired", null);
				} else {
					mofStatus = SmCommonUtil.getMessage("lbl.txt.active", null);
				}
			}
		}
		getManagedBean().setMofRegStatus(mofStatus);
		getManagedBean().setTargetPage(DETAILS_PAGE);

		List<SmPersonnel> personnelsToRemove = new ArrayList<SmPersonnel>();
		// set supplier admin
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (per.getEpRole() != null
					&& (per.getEpRole().equals(RoleCode.SUPPLIER_TEMP.value())
							|| per.getEpRole().equals(
									RoleCode.BASIC_SUPPLIER_ADMIN.value()) || per
							.getEpRole().equals(
									RoleCode.MOF_SUPPLIER_ADMIN.value()))) {
				getManagedBean().setSuppAdmin(per);
			}

			// filter to display only personnel that had login whether active or
			// deactivated
			if (per.getPmUser() == null) {
				personnelsToRemove.add(per);
			}

			// set personnel softcert expiry date
			if (per.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED) {
				PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
				personnelReqDTO.setSmSupplierId(getManagedBean().getSmAppl()
						.getSmSupplier().getSupplierId());
				personnelReqDTO.setUserId(per.getPmUser().getUserId());
				SmSoftcertRequest softcertRequest = SmServiceLookup
						.getPersonnelSO()
						.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(
								personnelReqDTO).getSoftcertRequest();
				if (softcertRequest != null
						&& softcertRequest.getPmDigiCert() != null
						&& softcertRequest.getPmDigiCert().getValidTo() != null) {
					per.setSoftcertValidityDate(DateUtils.dateToString(
							"dd/MM/yyyy", softcertRequest.getPmDigiCert()
									.getValidTo()));
				}
			}
		}
		getManagedBean().getSmAppl().getSmPersonnels()
				.removeAll(personnelsToRemove);

		// retrieve supplier's catalogues
		retrieveSupplierCatalogueStatusCountBySupplierId(getManagedBean()
				.getSmAppl().getSmSupplier().getSupplierId());

		// retrieve supplier's code request
		retrieveSupplierCatalogueRequestBySupplierId(getManagedBean()
				.getSmAppl().getSmSupplier().getSupplierId());
	}

	/**
	 * Perform search supplier application history
	 */
	public void search() {
		if (getManagedBean().isSearchCriteriaEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_722650,
									new String[] {},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return;
		}
		getManagedBean().getSmSupplierDataModel().reloadData();
	}

	/**
	 * Back to search page
	 */
	public void back() {
		getManagedBean().setTargetPage(SEARCH_PAGE);
	}

	/**
	 * retrieve supplier's catalogues by supplierId
	 * 
	 * @param Long
	 *            supplierId
	 */
	private void retrieveSupplierCatalogueStatusCountBySupplierId(
			Long supplierId) {

		getManagedBean().setNoOfCatApproved(0);
		getManagedBean().setNoOfCatRejected(0);
		getManagedBean().setNoOfCatExpired(0);
		getManagedBean().setNoOfCatPublished(0);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		reqDTO.setSupplierId(supplierId);

		ICatalogueSO reqSO = (ICatalogueSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
						ICatalogueSO.class);
		CatalogueResDTO resDTO = reqSO.retrieveSupplierItemBySupplierId(reqDTO);

		if (resDTO.getSupplierItemList().size() > 0) {
			for (CmSupplierItem cmSuppItem : resDTO.getSupplierItemList()) {
				if (cmSuppItem.getItemStatus().equalsIgnoreCase(
						SupplierManagementConst.CATALOGUE_APPROVED)) {
					getManagedBean().setNoOfCatApproved(
							getManagedBean().getNoOfCatApproved() + 1);
				} else if (cmSuppItem.getItemStatus().equalsIgnoreCase(
						SupplierManagementConst.CATALOGUE_REJECTED)) {
					getManagedBean().setNoOfCatRejected(
							getManagedBean().getNoOfCatRejected() + 1);
				} else if (cmSuppItem.getItemStatus().equalsIgnoreCase(
						SupplierManagementConst.CATALOGUE_EXPIRED)) {
					getManagedBean().setNoOfCatExpired(
							getManagedBean().getNoOfCatExpired() + 1);
				} else if (cmSuppItem.getItemStatus().equalsIgnoreCase(
						SupplierManagementConst.CATALOGUE_PUBLISHED)) {
					getManagedBean().setNoOfCatPublished(
							getManagedBean().getNoOfCatPublished() + 1);
				}
			}
		}
	}

	/**
	 * retrieve supplier's catalogue request by supplierId
	 * 
	 * @param Long
	 *            supplierId
	 */
	private void retrieveSupplierCatalogueRequestBySupplierId(Long supplierId) {
		RequestCodeReqDTO reqDTO = new RequestCodeReqDTO();
		reqDTO.setSupplierId(supplierId);

		IRequestCodeSO reqSO = (IRequestCodeSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
						IRequestCodeSO.class);
		RequestCodeResDTO resDTO = reqSO
				.retrieveRequestCodeBySupplierId(reqDTO);

		getManagedBean().setRequestCodeLs(resDTO.getRequestLs());
	}
}
