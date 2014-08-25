package my.ep.web.backingbean.cm.catalogue;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.cm.catalog.CmItemRejectReason;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.ReviewCatalogueManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "reviewCatBackingBean")
@RequestScoped
public class ReviewCatalogueBackingBean extends
		BaseBackingBean<ReviewCatalogueManagedBean> {

	private SearchFilterBean statusFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());

	public ReviewCatalogueBackingBean() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setItemStatus("P");
		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		this.getManagedBean().setReviewSupplierItems(
				resDTO.getSupplierItemList());

	}

	public void refreshCatalogueList() {
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setItemStatus("P");
		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		this.getManagedBean().setReviewSupplierItems(
				resDTO.getSupplierItemList());

	}

	public String navigateToCatalogDet() {

		System.out.println("************navigateToCatalogDet*************");

		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		StatusReqDTO statusReqDTO = new StatusReqDTO();

		statusReqDTO.setDocType("CMR");
		statusReqDTO.setModuleCode("CM");
		statusReqDTO.setLanguageCode(languageCode);

		StatusResDTO resDTO = statusSO.retrieveStatusDescList(statusReqDTO);

		CatalogueBackingBean bb = new CatalogueBackingBean();
		bb.getManagedBean().setStatusDescList(resDTO.getStatusDescList());

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSupplierId(this.getManagedBean().getSelectedReviewCat()
				.getSmSupplier().getSupplierId());

		CatalogueResDTO catResDTO = catalogueSO
				.retrieveSupplierZoneBySuppId(reqDTO);

		System.out.println("size:" + catResDTO.getSupplierZoneLs().size());

		bb.getManagedBean().setSupplierZoneList(catResDTO.getSupplierZoneLs());

		bb.getManagedBean().setSelectedMaintainItem(
				this.getManagedBean().getSelectedReviewCat());

		for (CmItemRejectReason remark : bb.getManagedBean()
				.getSelectedMaintainItem().getCmItemRejectReasons()) {

			userReqDTO.setUserId(remark.getChangedBy());
			System.out.println("remark.getChangedBy()" + remark.getChangedBy());
			UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
			System.out.println("userResDTO.getPmUser().getUserName():"
					+ userResDTO.getPmUser().getUserName());
			remark.setCreatedUser(userResDTO.getPmUser().getUserName());
		}
		bb.getManagedBean().setRejectReasonIds(new ArrayList<Long>());
		bb.getManagedBean().setDisplayCatalogue(true);
		bb.getManagedBean().setPreviousScreen(2);//pendingtask
		return "addCatalogItem";

	}

	public String approveCatalogueItem() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueBackingBean bb = new CatalogueBackingBean();
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		bb.getManagedBean().getSelectedMaintainItem().setItemStatus("A");

		bb.getManagedBean().getSelectedMaintainItem().setReviewRequired(false);

		reqDTO.getSaveSupplierItems().add(
				bb.getManagedBean().getSelectedMaintainItem());

		try {
			catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		refreshCatalogueList();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO,
				" Item Successfully approved", ""));
		return "/xhtml/cm/catalogue/catalogueTaskList.xhtml";

	}

	public String rejectCatalogueItem() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);

		try {
			CatalogueBackingBean bb = new CatalogueBackingBean();
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			StatusReqDTO statusReqDTO = new StatusReqDTO();
	
			bb.getManagedBean().getSelectedMaintainItem().setItemStatus("R");
			this.getManagedBean().setRejectReason(false);
	
			bb.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
			System.out.println("bb.getManagedBean().getRejectReasonIds(): "
					+ bb.getManagedBean().getRejectReasonIds());
			
			for (int i = 0; i < bb.getManagedBean().getRejectReasonIds().size(); i++) {
				Long id = Long.valueOf(String.valueOf(bb.getManagedBean().getRejectReasonIds().get(i)));
				
				if(id==10010){
					if(bb.getManagedBean().getRejectReason()==null || bb.getManagedBean().getRejectReason().isEmpty()){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Remark is required!", ""));
						return "addCatalogView";
					}
				}
				
			}
			
			
			
	
			for (int i = 0; i < bb.getManagedBean().getRejectReasonIds().size(); i++) {
				Long id = Long.valueOf(String.valueOf(bb.getManagedBean().getRejectReasonIds().get(i)));
	
				CmItemRejectReason reason = new CmItemRejectReason();
				
				statusReqDTO.setStatusId(id);
				StatusResDTO resDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				PmStatus reasonStatus = resDTO.getPmStatus();
	
				reason.setRemark(bb.getManagedBean().getRejectReason());
				reason.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
				reason.setPmStatus(reasonStatus);
				bb.getManagedBean().getSelectedMaintainItem().getCmItemRejectReasons().add(reason);
			}
	
			reqDTO.getSaveSupplierItems().add(
					bb.getManagedBean().getSelectedMaintainItem());
	
			if (bb.getManagedBean().getRejectReasonIds().isEmpty()) {
	
				this.getManagedBean().setCatDetTabActiveIndex(2);
	
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						" Item Reject Reason Required!", ""));
	
				return "addCatalogView";
			}
		
			catalogueSO.updateSupplierItem(reqDTO);
			bb.getManagedBean().setRejectReason(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			catalogueSO = null;
			statusSO = null;
		}
		refreshCatalogueList();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO,
				" Item Successfully rejected", ""));
		return "/xhtml/cm/catalogue/catalogueTaskList.xhtml";

	}

	public String cancelToPendingApprovalItem() {
    
		return "/xhtml/cm/catalogue/catalogueTaskList.xhtml";
	}

}
