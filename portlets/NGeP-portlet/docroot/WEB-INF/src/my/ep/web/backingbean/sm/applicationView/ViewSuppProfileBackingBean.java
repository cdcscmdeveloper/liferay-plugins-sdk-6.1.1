package my.ep.web.backingbean.sm.applicationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.DisciplinaryActionVO;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
//#28175 start
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.persistence.entity.pm.common.PmRemarkCategory;
//#28175 ReOpen
import my.ep.web.constant.sm.SupplierManagementConst;
//#28175 end
//#28895_35501 Start - AUDIT TRAIL SM
import javax.servlet.http.HttpServletRequest;
import com.liferay.compat.portal.util.PortalUtil;
//#28895_35501 End
//#34031 Start
import java.util.HashMap;
import java.util.Map;
import my.ep.biz.service.sm.revisit.ISmRevisitSO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.revisit.SmRevisitReqDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmRevisit;
import my.ep.util.ResourceBundleUtils;
//#34031 End

@ManagedBean(name = "viewSuppProfileBackingBean")
@RequestScoped
//#29759 - Searching application status supporting doc loading
@SessionScoped
public class ViewSuppProfileBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> implements Serializable {
	private static Logger logger = Logger.getLogger(ViewSuppProfileBackingBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 377766773512626830L;
	private static final String SHOW_DA = "disciplinaryActionHistoryDlg.show();";

	/**
	 * Pre - render for View Supplier Profile
	 */
	public void preRenderViewSupplierProfile() {
		 //20211005@fadhli - #31754
		String sessionId = ((PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest()).getRequestedSessionId(); 
		//LOG : 26549 : SLA Monitoring
		long startTime = System.currentTimeMillis();
		long subStartTime = 0L;
		renewSesionObject();
		initLogin();
		// #28895_35501 Start - AUDIT TRAIL SM
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String isAudit = originalServletRequest.getParameter("isAudit");
		if (SmCommonUtil.md5("true").equals(isAudit)) {
			this.getManagedBean().setAuditAccess(true);
		} else if (SmCommonUtil.md5("false").equals(isAudit)) {
			this.getManagedBean().setAuditAccess(false);
		}
		// #28895_35501 End
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		// #28175
		getManagedBean().setHistoryList(new ArrayList<PmRemarkCategory>());
		// #34031
		initSmRvHistory();
		logger.info("[preRenderViewSupplierProfile] #31754 getManagedBean().isSupplierUser(): " + getManagedBean().isSupplierUser());
		if (getManagedBean().isSupplierUser()) {
			SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			subStartTime = System.currentTimeMillis(); //20211005@fadhli - #31754
			MofRegistrationResDTO resDTO = SmServiceLookup
					.getMofRegistrationSO()
					.retrieveLatestSmApplWithChildBySupplierId(reqDTO);
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "retrieveLatestSmApplWithChildBySupplierId", 
					subStartTime); //20211005@fadhli - #31754
			String redirectToAppl = SmApplicationUtil.getApplicationErrorPage();

			logger.info("[preRenderViewSupplierProfile] #31754 resDTO.getSmAppl(): " + resDTO.getSmAppl());
			if (resDTO.getSmAppl() != null) {
				String applNo = resDTO.getSmAppl().getApplNo();
				getManagedBean().setApplicationNoParam(applNo + "_APP");
				logger.info("[preRenderViewSupplierProfile] #31754 applNo: " + applNo);
				if (applNo != null && StringUtils.isNotBlank(applNo)) {
					subStartTime = System.currentTimeMillis();
					viewApplication(resDTO.getSmAppl());
					SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, applNo,
							"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "viewApplication", 
							subStartTime); //20211005@fadhli - #31754
				} else {
					SmCommonUtil.redirectToPage(redirectToAppl,
							MessageConst.INFO_721212, null, null);
				}
			} else {
				SmCommonUtil.redirectToPage(redirectToAppl,
						MessageConst.INFO_721212, null, null);
			}
			subStartTime = System.currentTimeMillis();
			setTabParam();
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "setTabParam", 
					subStartTime); //20211005@fadhli - #31754
			SLAUtils.getInstance().logSLAInfo("SM","SP",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
			getManagedBean().setRenderedBackButton(false);
			//26238 - start
			retrieveActiveDisciplinaryAction(supplier);
			List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveSmDisciplinaryActionBySupplier(supplier);
			getManagedBean().setDisciplinaryAction(!EntityHelper.isCollectionEmpty(resultList));
			//26238 - end
		} else {
			subStartTime = System.currentTimeMillis();
			initLoginUserZone();
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "initLoginUserZone", 
					subStartTime); //20211005@fadhli - #31754
			getManagedBean().setRenderedBackButton(true);
			String supplierIdParam = getSupplierIdParam();
			subStartTime = System.currentTimeMillis();
			getManagedBean().setRegStatusDescList(
					retrieveRegistrationStatusList());
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "retrieveRegistrationStatusList", 
					subStartTime); //20211005@fadhli - #31754
			subStartTime = System.currentTimeMillis();
			getManagedBean().setZoneList(
					SmServiceLookup.getZoneSO().retrieveActiveZoneList()
							.getZoneList());
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "retrieveActiveZoneList", 
					subStartTime); //20211005@fadhli - #31754
			logger.info("[preRenderViewSupplierProfile] #31754 supplierIdParam: " + supplierIdParam);
			if (StringUtils.isNotBlank(supplierIdParam)) {
				getManagedBean().setRenderedBackButton(false);
				subStartTime = System.currentTimeMillis();
				viewApplication(SmApplicationUtil
						.getLatestApplBySupplierId(Long
								.parseLong(supplierIdParam)));
				SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId,
						SmApplicationUtil.getLatestApplBySupplierId(Long.valueOf(supplierIdParam)).getApplNo(),
						"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "viewApplication", 
						subStartTime); //20211005@fadhli - #31754
				//LOG : 26549 : SLA Monitoring
				SLAUtils.getInstance().logSLAInfo("SM","SP",
						"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
			} else {
				//LOG : 26549 : SLA Monitoring
				SLAUtils.getInstance().logSLAInfo("SM","SP",
						"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
				SmCommonUtil
						.redirectToPage(
								"/xhtml/sm/revisionSmApplView/viewSupplierProfile/searchSupplierProfilePage.xhtml",
								"", null, null);
			}
		}
	}

	public void search() {
		validateCompanySearch();
		getManagedBean().searchSupplier();
	}
	
	//26238 - start
	//27437 : Disciplinary action
	public void searchBasicAccount() {
		getManagedBean().searchSupplier();
		getManagedBean().setBasicAccountMaintenance(true);
	}
	
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * View Disciplinary Action
	 */
	public void onViewDisciplinaryAction() {
		SmSupplier currSupp = getManagedBean().getSmSupplier();
		//27876: Disciplinary Action
		currSupp.setFlagSupplier(getManagedBean().isSupplierUser());
		if(getManagedBean().isSupplierUser()){
			currSupp = SmApplicationUtil.getCurrentSupplier();
		}
		log.info("onViewDisciplinaryAction - "+currSupp.getSupplierId());
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())){
			getManagedBean().setMofSupplier(true);
		}
		
		// 28342 : Disciplinary Action - start, 28630 : Add additional role - start
		getManagedBean().setDisableDownloadButtonDAHistory(disableDownloadDA());
		
		List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(currSupp);
		getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		if(!EntityHelper.isCollectionEmpty(voList)){
			getManagedBean().setDisciplineVOs(voList);
		}
		RequestContext.getCurrentInstance().execute(SHOW_DA);
	} 
	
	/**
	 * 32560 - Added Checking to Get Active DA from the list
	 * CHECKING TO SET START / END DATE FOR DISCIPLINARY ACTION POPUP
	 * @param listDA
	 */
	public void findActiveDA(List<DisciplinaryActionVO> listDA) {
			    Iterator<DisciplinaryActionVO> iterator = listDA.iterator();
			    while (iterator.hasNext()) {
			    	DisciplinaryActionVO daVO = iterator.next();
			        if (daVO.isActiveRecordStatus()) {
			            this.getManagedBean().setStartDate(daVO.getStartDate());
			            this.getManagedBean().setEndDate(daVO.getEndDate());
			            this.getManagedBean().setWarning(daVO.isWarning());
			        }
			    }
			}
	
	/**
	 * @param smSupplier
	 */
	public void onViewDisciplinaryAction(SmSupplier smSupplier) {
		displayDisciplinaryAction(smSupplier, false);
	}
	
	private void displayDisciplinaryAction(SmSupplier smSupplier, boolean isActive) {
		getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		//27876: Disciplinary Action - start - <27876>
		smSupplier.setFlagSupplier(true);
		List<DisciplinaryActionVO> voList = isActive ? getActiveDisciplinaryActionVO(smSupplier)
				: getDisciplinaryActionVO(smSupplier);
		//32560 - Added Checking Active DA
		findActiveDA(voList);	

		if(!EntityHelper.isCollectionEmpty(voList)){
			getManagedBean().setDisciplineVOs(voList);
			//#-29771 - Start.
			getManagedBean().setSmSupplier(smSupplier);
			//#-29771 - End.
			SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			PmUser sUsers = facesScopes.getUserSessionBean();
			
			if (sUsers != null && sUsers.getPreferredLanguage() != null){
				reqDTO.setLanguage(sUsers.getPreferredLanguage());
			}
			
			reqDTO.setSupplier(smSupplier);
			SupplierDataMaintenanceResDTO resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
					.retrieveDisciplinaryActionMsg(reqDTO);
			String msg = resDTO.getErrorMessage();
			if (StringUtils.isEmpty(msg)){
				msg = StringUtils.EMPTY;
			} else if (msg.contains("Please click")){
				msg = msg.split("Please click")[0];
			} else if (msg.contains("Sila semak")){
				msg = msg.split("Sila semak")[0];
			}
			this.getManagedBean().setDisciplinaryActionMsg(msg);	
		}
		RequestContext.getCurrentInstance().execute(SHOW_DA);
	} 
	
	//28281
	//26238 - end
	//26238 -  STATR Disciplinary Action - 28111
	/**
	 * View Disciplinary Action Supplier
	 * @param currSupp
	 */
	public void onViewDisciplinaryActionSupp(SmSupplier currSupp) {		
		if (currSupp == null || currSupp.getSupplierId() == null){
			return;
		}
		log.info("onViewDisciplinaryActionSupp - "+currSupp.getSupplierId());
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())){
			getManagedBean().setMofSupplier(true);
		}
		// 28342 : Disciplinary Action - start, 28630 : Add additional role
		getManagedBean().setDisableDownloadButtonDAHistory(disableDownloadDA());
		
		//28637 - start, 28806
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setSupplierId(currSupp.getSupplierId());
		SmAppl smAppl = SmServiceLookup.getSupplierSearchSO().retriveLatestApplBySupplierId(reqDTO).getSmAppl();
		if(SupplierManagementConst.SUPP_TYPE_CONTRACTOR.equals(smAppl.getSupplierType()) 
				|| getManagedBean().isMofSupplier()){
			getManagedBean().setViewCatCode(true);
		}
		//28637 - end, 28806

		//27876: Disciplinary Action - start - <27876>
		currSupp.setFlagSupplier(true);
		//27876: Disciplinary Action - end - <27876>
		
		//30872: Disciplinary Action - start - <30872>
		currSupp.setFlagDASuppProfile(true);
		//30872: Disciplinary Action - end - <30872>
		
		List<DisciplinaryActionVO> voList = getActiveDisciplinaryActionVO(currSupp);
		getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		if(!EntityHelper.isCollectionEmpty(voList)){
			getManagedBean().setDisciplineVOs(voList);
		}
		RequestContext.getCurrentInstance().execute(SHOW_DA);
	} 
	
	private boolean disableDownloadDA(){
		// 28342 : Disciplinary Action - start, 28630 : Add additional role
		List<String> disableRoles = Arrays.asList(RoleCode.UPS_SDO.value(), RoleCode.UPS_PO.value(),
				RoleCode.APPROVER.value(), RoleCode.UPS_SVC.value(), RoleCode.UPS_SVO.value(), 
				RoleCode.UPS_APPROVER.value(), RoleCode.MOF_SUPPLIER_ADMIN.value(), RoleCode.BASIC_SUPPLIER_ADMIN.value());
		for (String role : disableRoles){
			if (getFacesScopes().hasRoleUser(role)){
				return true;
			}
		}
		return false;
	}
	
	//31033 start
	/**
	 * View Active Disciplinary Action
	 * @param smSupplier
	 */
	public void onViewActiveDisciplinaryAction(SmSupplier smSupplier) {
		displayDisciplinaryAction(smSupplier, true);
	} 
	//26238 -  END Disciplinary Action - 28111, //31033 end
	
	//#29482 - Start
	public void assignSelAcadQualify(SmPersonnel smPersonnel) {
		this.getManagedBean().setSmPersonnel(smPersonnel);
		RequestContext.getCurrentInstance().execute("qualifyDialog.show()");
	}
	//#29482 - End
	// #28175 start
	/**
	 * Handles the click event for the category codes history.
	 * 
	 * This method retrieves the category code details based on the provided category code level and ID.
	 * It then updates the managed bean with the retrieved details and shows the remark history popup.
	 * 
	 * @param catCode the category code
	 * @param catCodeLevel the category code level (1, 2, or 3)
	 * @param catCodeID the category code ID
	 */
	public void onClickCatCodesHistory(String catCode, int catCodeLevel, Long catCodeID) {
		ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		CategoryCodeResDTO resDTO = new CategoryCodeResDTO();
		if(catCodeLevel == 1){
			reqDTO.setCategoryL1Id(catCodeID);
			resDTO = categoryCodeSO.retrieveCategoryL1DtlById(reqDTO);
		}else if(catCodeLevel == 2){
			reqDTO.setCategoryL2Id(catCodeID);
			resDTO = categoryCodeSO.retrieveCategoryL2DtlById(reqDTO);
		}else if(catCodeLevel == 3){
			reqDTO.setCategoryL3Id(catCodeID);
			resDTO = categoryCodeSO.retrieveCategoryL3DtlById(reqDTO);
		}
		// #28175 ReOpen - start
		if (hasSupplierRole()) {
			getManagedBean().setSupplierUser(true);
		}
		// #28175 ReOpen - End
		getManagedBean().setHistoryList(updateCreatedUserName(resDTO.getRemarkList()));
		getManagedBean().setCatCode(catCode);
		RequestContext.getCurrentInstance().execute("remarkHisCatCodePopup.show()");
	}
	//28175
	private boolean hasSupplierRole() {
		String[] roles = { SupplierManagementConst.BASIC_SUPPLIER_ADMIN, SupplierManagementConst.BASIC_SUPPLIER_USER,
				SupplierManagementConst.MOF_SUPPLIER_ADMIN, SupplierManagementConst.MOF_SUPPLIER_USER,
				RoleCode.FL_USER.value(), SupplierManagementConst.SUPPLIER_ADMIN,
				SupplierManagementConst.SUPPLIER_USER, RoleCode.SUPPLIER_TEMP.value(),
				RoleCode.G2G_SUPPLIER_ADMIN.value(), RoleCode.G2G_SUPPLIER_USER.value() };

		for (String role : roles) {
			if (getFacesScopes().hasRoleUser(role)) {
				return true;
			}
		}

		return false;
	}
	
	private List<PmRemarkCategory> updateCreatedUserName(List<PmRemarkCategory> remarkList) {
		for(PmRemarkCategory rc: remarkList){
			PmUser user = PmCommonUtil.retrieveUserById(rc.getCreatedBy(),true);
			rc.setCreatedUsername(user.getUserName());
		}
		return remarkList;
	}
	// #28175 end
	// #34031 Start
	/**
	 * Retrieves remarks related to a given revisit.
	 * @param revisit
	 * @return List<String>
	 */
	public List<String> getSupplierRvRemarks(SmRevisit revisit) {
		List<String> remarks = new ArrayList<>();
		if (null != revisit) {
			StringBuilder remarkOne = new StringBuilder();
			remarkOne.append(
					ResourceBundleUtils.getMessage("lbl.sm.rv.supp.remark.one.part.one", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())).append(" ");
			if (null != revisit.getRevisitPrevRemark()) {
				remarkOne.append(revisit.getRevisitPrevRemark());
			} else {
				remarkOne.append(ResourceBundleUtils.getMessage("lbl.txt.na", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
			}
			remarkOne.append(
					ResourceBundleUtils.getMessage("lbl.sm.rv.supp.remark.one.part.two", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())).append(" ");
			if (null != revisit.getRevisitRemark()) {
				remarkOne.append(revisit.getRevisitRemark());
			} else {
				remarkOne.append(ResourceBundleUtils.getMessage("lbl.txt.na", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
			}
			remarks.add(remarkOne.toString());
			if (revisit.getOldPmRegStatus() != null && revisit.getNewPmRegStatus() != null) {
				List<PmStatusDesc> descList;
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
				descList = SmServiceLookup.getStatusSO().retrieveRegStatusList(reqDTO).getStatusDescList();
				StringBuilder remarkTwo = new StringBuilder();
				remarkTwo
						.append(ResourceBundleUtils.getMessage("lbl.sm.rv.supp.remark.two.part.one", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()))
						.append(" ")
						.append(getPmStatusDescs(descList, revisit.getOldPmRegStatus().getPmStatus().getStatusId()))
						.append(" ")
						.append(ResourceBundleUtils.getMessage("lbl.sm.rv.supp.remark.two.part.two", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())).append(" ")
						.append(getPmStatusDescs(descList, revisit.getNewPmRegStatus().getPmStatus().getStatusId()));
				remarks.add(remarkTwo.toString());
			}
			if (null != revisit.getSmRvCatCodes() && !revisit.getSmRvCatCodes().isEmpty()) {
				String remarkThree = ResourceBundleUtils.getMessage("lbl.sm.rv.supp.remark.three", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());
				remarks.add(remarkThree);
			}
		}
		return remarks;
	}

	private String getPmStatusDescs(List<PmStatusDesc> statusList, Long pmStatusId) {
		String statusName = "";

		if (!SmCommonUtil.isCollectionEmpty(statusList) && pmStatusId != null) {
			for (PmStatusDesc status : statusList) {
				if (status.getPmStatus().getStatusId().equals(pmStatusId)) {
					statusName = status.getStatusName();
					break;
				}
			}
		}
		return statusName;
	}
	// #34031 End
}
