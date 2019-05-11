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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.web.util.cm.helper.CmApplicationUtil;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;


import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.cm.catalog.CmItemRejectReason;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.ReviewCatalogueManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.dto.cm.catalogue.CmTrackingDairyVO;
import my.ep.exception.EpException;


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


		CatalogueBackingBean.retriveSupplierGstInfomation(catResDTO.getSupplierItem().getSmSupplier());


		//return "editCatalogItem";
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
		
		// [SIR#24977] Khirul added on 2019-05-06 - Start
		String itemStat = "P"; // Item status pending
		Date currDt = new Date();
		Date stdStartDt = bb.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getStartDate();
		Date stdEndDt = bb.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getEndDate();		
		
		log.info("Standard price Current date is: " + currDt.toString());
		log.info("Standard price Start date is: " + stdStartDt.toString());
		log.info("Standard price End date is: " + stdEndDt.toString());				
		
		if(currDt.equals(stdStartDt) || (currDt.after(stdStartDt) && currDt.before(stdEndDt))) {
			log.info("Date range for standard price is valid for Published...");
			itemStat = "U"; // Item status published
		} else {
			log.info("Date range for standard price is not valid for Published...");
			itemStat = "A"; // Item status approved
		}
		// [SIR#24977] Khirul added on 2019-05-06 - End

		// [SIR#24977] Khirul modified on 2019-05-06 - Start
		//bb.getManagedBean().getSelectedMaintainItem().setItemStatus("A");
		bb.getManagedBean().getSelectedMaintainItem().setItemStatus(itemStat);
		// [SIR#24977] Khirul modified on 2019-05-06 - End

		bb.getManagedBean().getSelectedMaintainItem().setReviewRequired(false);

		reqDTO.getSaveSupplierItems().add(
				bb.getManagedBean().getSelectedMaintainItem());		
		log.info("Tracking diary for approveCatalogueItem items");		

		reqDTO.setTrackingStatus(true);
		reqDTO.setTrackDairyVOs(new ArrayList<CmTrackingDairyVO>());

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

		StatusReqDTO statusReqDTO = new StatusReqDTO();
		
		// [SIR#24977] Khirul modified on 2019-05-08 - Start
		//statusReqDTO.setStatusId(CmConst.APPROVED);
		statusReqDTO.setStatusId(itemStat.equals("U") ? CmConst.PUBLISHED : CmConst.APPROVED);
		// [SIR#24977] Khirul modified on 2019-05-08 - End

		PmUser pmUser = new FacesScopes().getUserSessionBean();

		StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
		// [SIR#24977] Khirul modified on 2019-05-08 - Start
		//String actionDesc ="Item "+bb.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName()+" telah diluluskan oleh "+(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
		String actionDesc ="Item "+bb.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName()+" telah " + (itemStat.equals("U") ? "diiklankan" : "diluluskan") + " oleh " + (pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
		// [SIR#24977] Khirul modified on 2019-05-08 - End
		reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(
				bb.getManagedBean().getSelectedMaintainItem(),actionDesc,statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));

		try {
			
			log.info("Invoking updateSupplierItem...");
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

			reqDTO.setPmUser(pmUser);
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
		countApproveList();
		countRejectList();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "supplierDashboard");
		
		RequestContext.getCurrentInstance().execute(
				"refreshId();");

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


	public void countRejectList(){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();


		try {
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

			SmSupplier smSupplier = null;


			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
			supplierReqDTO.setAllStatuses(true);

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);

			if(smResDTO.getResultBean().getResultList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_721017);
			}

			SearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
			smSupplier = smResDTO.getResultBean().getResultList().get(0);

			List<String> itemStatusLs = new ArrayList<String>();

			itemStatusLs.add("R"); ///reject

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("itemStatus",
							SearchOperator.OPERATOR_IN.value(),itemStatusLs ));


			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("smSupplier.supplierId",
							SearchOperator.OPERATOR_EQUALS.value(), smSupplier.getSupplierId()));

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatus",
							SearchOperator.OPERATOR_EQUALS.value(), true));

			reqDTO.setSearchBean(searchBean);
			CatalogueResDTO resDTO = catalogueSO.retrieveSupplierItemDashBoard(reqDTO);
			System.out.println("Rejected:"
					+ resDTO.getSupplierItemList().size());
			this.getManagedBean().setRejectSize(resDTO.getSupplierItemList().size());


		}catch(Exception e){

		}

	}

	public void countApproveList(){


		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();


		try {
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

			SmSupplier smSupplier = null;


			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
			supplierReqDTO.setAllStatuses(true);

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);

			if(smResDTO.getResultBean().getResultList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_721017);
			}

			SearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
			smSupplier = smResDTO.getResultBean().getResultList().get(0);

			List<String> itemStatusLs = new ArrayList<String>();

			itemStatusLs.add("A"); ///approved

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("itemStatus",
							SearchOperator.OPERATOR_IN.value(),itemStatusLs ));


			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("smSupplier.supplierId",
							SearchOperator.OPERATOR_EQUALS.value(), smSupplier.getSupplierId()));

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatus",
							SearchOperator.OPERATOR_EQUALS.value(), true));

			reqDTO.setSearchBean(searchBean);
			CatalogueResDTO resDTO = catalogueSO.retrieveSupplierItemDashBoard(reqDTO);
			System.out.println("Approved:"
					+ resDTO.getSupplierItemList().size());
			this.getManagedBean().setApproveSize(resDTO.getSupplierItemList().size());


		}catch(Exception e){

		}
	}
	
	
	public void navigateToCatalgoueURL() throws IOException,
	PortalException, SystemException {
		
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
				.getRequestScope(WebKeys.THEME_DISPLAY);

		System.out.println("getURLCurrent" + themeDisplay.getURLCurrent());
		String commonUrl = themeDisplay.getURLCurrent().substring(
				0,
				themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())
						+ themeDisplay.getLayout().getGroup().getFriendlyURL()
						.length());
		System.out.println("commonUrl" + commonUrl);

		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.redirect(commonUrl + "/catalogue-maintenance-file");
	}




}
