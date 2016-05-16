/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1 . 00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.cm.catalogue;

import java.io.Serializable;
import java.util.ArrayList;
import my.ep.web.util.cm.helper.CmApplicationUtil;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;


import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.cm.catalog.CmItemRejectReason;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.ReviewCatalogueManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.dto.cm.catalogue.CmTrackingDairyVO;


/**
 * Class ReviewCatalogueBackingBean.
 * @author hktan
 */
@ManagedBean(name = "reviewCatBackingBean")
@RequestScoped
public class ReviewCatalogueBackingBean extends
BaseBackingBean<ReviewCatalogueManagedBean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The status filter bean. */
	private SearchFilterBean statusFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());

	/** The log. */
	private static Logger log = Logger.getLogger(ReviewCatalogueBackingBean.class);

	/*	public ReviewCatalogueBackingBean() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setItemStatus("P");
		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		this.getManagedBean().setReviewSupplierItems(
				resDTO.getSupplierItemList());

	}*/

	/**
	 * Refresh catalogue list.
	 */
	public void refreshCatalogueList() {
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		System.out.println("******************refreshCatalogueList ********************");

		this.getManagedBean().getApprovalSearchDataModel().reloadData();

	}

	/**
	 * Navigate to catalog details page.
	 * 
	 * @return string(addCatalogItem)
	 */
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

		bb.getManagedBean().setRejectReason(null);
		bb.getManagedBean().setSupplierZoneList(catResDTO.getSupplierZoneLs());

		reqDTO.setSupplierItemId(this.getManagedBean().getSelectedReviewCat().getSupplierItemId());
		catResDTO = catalogueSO.retrieveSupplierItemById(reqDTO);
		bb.getManagedBean().setSelectedMaintainItem(catResDTO.getSupplierItem());

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

	/**
	 * Approve catalogue item.
	 * 
	 * @return String(/xhtml/cm/catalogue/catalogueTaskList.xhtml)
	 */
	public String approveCatalogueItem() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		log.info("start of approveCatalogueItem ");
		CatalogueBackingBean bb = new CatalogueBackingBean();
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		bb.getManagedBean().getSelectedMaintainItem().setItemStatus("A");

		bb.getManagedBean().getSelectedMaintainItem().setReviewRequired(false);

		reqDTO.getSaveSupplierItems().add(
				bb.getManagedBean().getSelectedMaintainItem());		
		log.info("Tracking diary for approveCatalogueItem items");		

		reqDTO.setTrackingStatus(true);
		reqDTO.setTrackDairyVOs(new ArrayList<CmTrackingDairyVO>());

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

		StatusReqDTO statusReqDTO = new StatusReqDTO();

		statusReqDTO.setStatusId(CmConst.APPROVED);

		PmUser pmUser = new FacesScopes().getUserSessionBean();

		StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
		String actionDesc ="Item "+bb.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName()+" telah diluluskan oleh "+(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
		reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(
				bb.getManagedBean().getSelectedMaintainItem(),actionDesc,statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));

		try {
			catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("error:"+e.getMessage());
			System.out.println("error e:"+e);
			//e.printStackTrace();
		}
		refreshCatalogueList();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO,
				"", MessageUtils.getMessage(MessageConst.ERROR_715055,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale())));

		System.out.println("*************end*******************");
		return "/xhtml/cm/catalogue/catalogueTaskList.xhtml";

	}

	/**
	 * Reject catalogue item.
	 * 
	 * @return String(addCatalogView or /xhtml/cm/catalogue/catalogueTaskList.xhtml )
	 */
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
								"", MessageUtils.getMessage(MessageConst.ERROR_715056,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
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
						"", MessageUtils.getMessage(MessageConst.ERROR_715057,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

				return "addCatalogView";
			}


			log.info("Tracking diary for to reject the catalog items");			
			reqDTO.setTrackDairyVOs(new ArrayList<CmTrackingDairyVO>());
			reqDTO.setTrackingStatus(true);
			statusReqDTO.setStatusId(CmConst.REJECTED);
			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
			System.out.println("statusResDTO.getPmStatus().getStatusId():"+statusResDTO.getPmStatus().getStatusId());
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			String actionDesc ="Item "+bb.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName()+" telah ditolak oleh "+(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
			reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(bb.getManagedBean().getSelectedMaintainItem(),actionDesc,statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));

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
				"", MessageUtils.getMessage(MessageConst.ERROR_715058,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale())));

		System.out.println("*************end*******************");
		return "/xhtml/cm/catalogue/catalogueTaskList.xhtml";


	}

	/**
	 * Cancel to pending approval item.
	 * 
	 * @return String(/xhtml/cm/catalogue/catalogueTaskList.xhtml)
	 */
	public String cancelToPendingApprovalItem() {

		return "/xhtml/cm/catalogue/catalogueTaskList.xhtml";
	}

	/**
	 * Pre render catalogue task list.
	 */
	public void preRenderCatalogueTaskList() {

		renewSesionObject();

		System.out.println("***********preRenderCatalogueTaskList**************");
		this.getManagedBean().getApprovalSearchDataModel().reloadData();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "catalogueTaskList");

	}


	public void preRenderSupplierItemDashboard() {

		renewSesionObject();

		System.out.println("***********preRenderSupplierItemDashboard**************");
		this.getManagedBean().getSuppItemDashboardSearchDataModel().reloadData();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "supplierDashboard");

	}
	
	public String navigateToCatalogDetfromDashboard() {

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

		bb.getManagedBean().setRejectReason(null);
		bb.getManagedBean().setSupplierZoneList(catResDTO.getSupplierZoneLs());

		reqDTO.setSupplierItemId(this.getManagedBean().getSelectedReviewCat().getSupplierItemId());
		catResDTO = catalogueSO.retrieveSupplierItemById(reqDTO);
		bb.getManagedBean().setSelectedMaintainItem(catResDTO.getSupplierItem());

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
		bb.getManagedBean().setPreviousScreen(1);//catalogue maintenance file
		return "addCatalogView";

	}
	
	public String getStatusDesc(String status){
		
		String statusDesc = "";
		
		if(status.equalsIgnoreCase("A")){
			statusDesc = ResourceBundleUtils.getMessage("lbl.cm.approved",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		}else if(status.equalsIgnoreCase("R")){
			statusDesc = ResourceBundleUtils.getMessage("lbl.cm.reject",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		}
		
		return statusDesc;
	}
	
	
	
	


}
