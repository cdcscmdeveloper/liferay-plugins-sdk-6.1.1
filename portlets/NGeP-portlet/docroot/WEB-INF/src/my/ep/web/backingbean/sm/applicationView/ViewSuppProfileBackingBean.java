package my.ep.web.backingbean.sm.applicationView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmRemarkCategory;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

//#28175 start
//#28175 ReOpen
//#28175 end
//#28895_35501 Start - AUDIT TRAIL SM
import com.liferay.compat.portal.util.PortalUtil;
import javax.servlet.http.HttpServletRequest;
//#28895_35501 End

@ManagedBean(name = "viewSuppProfileBackingBean")
@RequestScoped
public class ViewSuppProfileBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 377766773512626830L;

	/**
	 * Pre - render for View Supplier Profile
	 */
	public void preRenderViewSupplierProfile() {
		//LOG : 26549 : SLA Monitoring
		long startTime = System.currentTimeMillis();
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
		if (getManagedBean().isSupplierUser()) {
			SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			MofRegistrationResDTO resDTO = SmServiceLookup
					.getMofRegistrationSO()
					.retrieveLatestSmApplWithChildBySupplierId(reqDTO);
			String redirectToAppl = SmApplicationUtil.getApplicationErrorPage();

			if (resDTO.getSmAppl() != null) {
				String applNo = resDTO.getSmAppl().getApplNo();
				getManagedBean().setApplicationNoParam(applNo + "_APP");
				if (applNo != null && StringUtils.isNotBlank(applNo)) {
					viewApplication(resDTO.getSmAppl());
				} else {
					SmCommonUtil.redirectToPage(redirectToAppl,
							MessageConst.INFO_721212, null, null);
				}
			} else {
				SmCommonUtil.redirectToPage(redirectToAppl,
						MessageConst.INFO_721212, null, null);
			}
			setTabParam();
			SLAUtils.getInstance().logSLAInfo("SM","SP",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
			getManagedBean().setRenderedBackButton(false);
			//26238 - start
			if(supplier != null){
				//28281
				retrieveActiveDisciplinaryAction(supplier);
			}
			
			List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveSmDisciplinaryActionBySupplier(supplier);
			if(resultList != null
					&& resultList.size() > 0){
				for(SmDisciplinaryAction disciplinaryAction : resultList){
					if(disciplinaryAction.isWarning() || disciplinaryAction.isCancelCategory() 
							|| disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration() 
							|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isManufacturerCancel()
							|| disciplinaryAction.isBumiputeraCancel()){
						getManagedBean().setDisciplinaryAction(true);
					}
				}				
			} else {
				getManagedBean().setDisciplinaryAction(false);
			}
			//26238 - end
		} else {
			initLoginUserZone();
			getManagedBean().setRenderedBackButton(true);
			String supplierIdParam = getSupplierIdParam();
			getManagedBean().setRegStatusDescList(
					retrieveRegistrationStatusList());
			getManagedBean().setZoneList(
					SmServiceLookup.getZoneSO().retrieveActiveZoneList()
							.getZoneList());
			if (StringUtils.isNotBlank(supplierIdParam)) {
				getManagedBean().setRenderedBackButton(false);
				viewApplication(SmApplicationUtil
						.getLatestApplBySupplierId(Long
								.parseLong(supplierIdParam)));
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

	public void onViewDisciplinaryAction() {
		SmSupplier currSupp = null;
		if(getManagedBean().isSupplierUser()){
			currSupp = SmApplicationUtil.getCurrentSupplier();
			
			//27876: Disciplinary Action - start - <27876>
			currSupp.setFlagSupplier(false);
			//27876: Disciplinary Action - start - <27876>
		} else {
			//28281
			//currSupp = getManagedBean().getSearchSupplier();
			currSupp = getManagedBean().getSmSupplier();
		}
		log.info(currSupp+"-------");
		log.info(currSupp.getSupplierId()+"-------");
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())){
			getManagedBean().setMofSupplier(true);
		}
		
		// 28342 : Disciplinary Action - start, 28630 : Add additional role - start
		if(getFacesScopes().hasRoleUser(RoleCode.UPS_SDO.value()) || getFacesScopes().hasRoleUser(RoleCode.UPS_PO.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.UPS_PO.value()) || getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.UPS_SVC.value()) || getFacesScopes().hasRoleUser(RoleCode.UPS_SVO.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.UPS_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())){
			// 28630 : Add additional role - end
			getManagedBean().setDisableDownloadButtonDAHistory(true);
		}
		//	28342 : Disciplinary Action - end
		
		List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(currSupp);
		if(!EntityHelper.isCollectionEmpty(voList)
				&& voList.size() > 0){
			getManagedBean().setDisciplineVOs(voList);
		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		RequestContext.getCurrentInstance().execute(
				"disciplinaryActionHistoryDlg.show();");
	} 
	
	// #32560, #26247 -start-
	/**
	 * 32560 - Added Checking to Get Active DA from the list
	 * CHECKING TO SET START / END DATE FOR DISCIPLINARY ACTION POPUP
	 * @return void
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
	// #32560, #26247 -end-
	
	/**
	 * @param smSupplier
	 */
	public void onViewDisciplinaryAction(SmSupplier smSupplier) {
		
		//27876: Disciplinary Action - start - <27876>
		smSupplier.setFlagSupplier(true);
		//27876: Disciplinary Action - start - <27876>
		
		List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(smSupplier);
		
		// 32560, 26247 -start-
		/*
		 * 32560 - Added Checking Active DA
		 */
		findActiveDA(voList);
		// #32560, #26247 -end-

		if(!EntityHelper.isCollectionEmpty(voList)
				&& voList.size() > 0){
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
			ISupplierDataMaintenanceSO suppDataSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
			SupplierDataMaintenanceResDTO resDTO = null;
			
			if (null != suppDataSO) {
				resDTO = suppDataSO.retrieveDisciplinaryActionMsg(reqDTO);
				if(StringUtils.isNotEmpty(resDTO.getErrorMessage())){
					if (resDTO.getErrorMessage().contains("Please click")) {
						resDTO.setErrorMessage(resDTO.getErrorMessage().split("Please click")[0]); 
					}
					if (resDTO.getErrorMessage().contains("Sila semak")) {
						resDTO.setErrorMessage(resDTO.getErrorMessage().split("Sila semak")[0]); 
					}
					this.getManagedBean().setDisciplinaryActionMsg(resDTO.getErrorMessage());
				} else {
					this.getManagedBean().setDisciplinaryActionMsg(StringUtils.EMPTY);
				}
			}
			
		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		RequestContext.getCurrentInstance().execute(
				"disciplinaryActionHistoryDlg.show();");
	} 
	
	//28281
	/*
	public static List<DisciplinaryActionVO> getDisciplinaryActionVO(SmSupplier currSupp) {
		List<DisciplinaryActionVO> voList = new ArrayList<DisciplinaryActionVO>();
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		perReqDTO.setSmSupplierId(currSupp.getSupplierId());
		
		//27915: Disciplinary Action - start - 26238
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelDABySupplierId(perReqDTO);
		//27915: Disciplinary Action - end - 26238
		
		List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveSmDisciplinaryActionBySupplier(currSupp);
		if (resultList != null
				&& resultList.size() > 0) {
			for(SmDisciplinaryAction disciplinaryAction: resultList){
				if(disciplinaryAction.isWarning() || disciplinaryAction.isCancelCategory() 
						|| disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration() 
						|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isManufacturerCancel()
						|| disciplinaryAction.isBumiputeraCancel()){
					
						DisciplinaryActionVO retVal = new DisciplinaryActionVO();
						if(disciplinaryAction.getStartDate() != null)
							retVal.setStartDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getStartDate()));
						if(disciplinaryAction.getEndDate() != null)
							retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getEndDate()));
						if(disciplinaryAction.getRemark() != null)
							retVal.setRemarks(disciplinaryAction.getRemark());
						
						if(disciplinaryAction.isWarning())
							retVal.setWarning(true);
						if(disciplinaryAction.isCancelCategory())
							retVal.setCancelCategory(true);
						if(disciplinaryAction.isSuspendRegistration())
							retVal.setSuspendRegistration(true);
						if(disciplinaryAction.isCancelRegistration())
							retVal.setCancelRegistration(true);
						if(disciplinaryAction.isBumiputeraCancel())
							retVal.setBumiputeraCancel(true);
						if(disciplinaryAction.isManufacturerCancel())
							retVal.setManufacturerCancel(true);
						if(disciplinaryAction.isBlacklistOwner())
							retVal.setBlacklistOwnerDirector(true);
						//27617 : Disciplinary Action
						if(disciplinaryAction.isCancelCategory()  || disciplinaryAction.isManufacturerCancel()){
							List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
							//27617 : Disciplinary Action - 26238
							List<Object[]> catList = SmApplicationUtil.retrieveCancelCategoryByDisciplinaryActionId(disciplinaryAction.getDisciplinaryActionId());
							
							if(catList != null
									&& catList.size() > 0){
								for(Object[] arr : catList){
									//27426 : Disciplinary Action - start
									if(arr[7] != null){
										if(disciplinaryAction.getDisciplinaryActionId().toString()
												.equalsIgnoreCase(arr[7].toString())){
											DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
											String catCode = StringUtils.EMPTY;
											catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
											catVo.setCategoryCode(catCode);	
											catVoList.add(catVo);
										} //27426 : Disciplinary Action - end
									}
								}						
								retVal.setCategoryCodeList(catVoList);
							}
						}
						
						if(disciplinaryAction.isSuspendRegistration()){
							List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();					
							if(personnelResDTO != null 
									&& personnelResDTO.getSmPersonnelList().size() > 0){
								for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
									BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
									if(personnel.isSuspendRegistration()){
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
										blacklistownerDirectorList.add(blacklistOwner);
									}
								}
								retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
							}						
						}
						
						if(disciplinaryAction.isCancelRegistration()){
							List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
							if(personnelResDTO != null 
									&& personnelResDTO.getSmPersonnelList().size() > 0){
								for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
									BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
									if(personnel.isCancelRegistration()){
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
										blacklistownerDirectorList.add(blacklistOwner);
									}
								}
								retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
							}						
						}
						
						if(disciplinaryAction.isBlacklistOwner()){
							List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
							if(personnelResDTO != null 
									&& personnelResDTO.getSmPersonnelList().size() > 0){
								for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
									BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
									if(personnel.isBlacklisted()){
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
										blacklistownerDirectorList.add(blacklistOwner);
									}
								}
								retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
							}
						}
					voList.add(retVal);					
				}
			}
			log.info(voList);
		}
		return voList;
	}


	public void retrieveActiveDisicplinaryAction(SmSupplier supplier){
		List<SmDisciplinaryAction> discList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		perReqDTO.setSmSupplierId(supplier.getSupplierId());
		
		//27915: Disciplinary Action - start - 26238
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelDABySupplierId(perReqDTO);
		//27915: Disciplinary Action - end - 26238
		
		if(discList != null && discList.size() > 0){
			for(SmDisciplinaryAction disc : discList){
				if(disc.isWarning() || disc.isCancelCategory() || disc.isSuspendRegistration() 
						|| disc.isCancelRegistration() || disc.isBlacklistOwner()
						|| disc.isManufacturerCancel() || disc.isBumiputeraCancel()){
					if(disc.isWarning())
						this.getManagedBean().setWarning(true);	
					if(disc.isCancelCategory())
						this.getManagedBean().setCancelCategoryCode(true);
					if(disc.isSuspendRegistration())
						this.getManagedBean().setSuspendRegistration(true);
					if(disc.isCancelRegistration())
						this.getManagedBean().setCancelRegistration(true);
					if(disc.isBlacklistOwner())
						this.getManagedBean().setBlacklistOwnerOrDirector(true);
					if(disc.isBumiputeraCancel())
						this.getManagedBean().setBumiputeraStatusCancel(true);
					if(disc.isManufacturerCancel())
						this.getManagedBean().setManufacturerStatusCancel(true);
					
					if(!disc.isWarning()){
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if(personnelResDTO != null 
								&& personnelResDTO.getSmPersonnelList().size() > 0){
							for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
								if(personnel.isCancelRegistration()
										|| personnel.isSuspendRegistration()
											|| personnel.isBlacklisted()){
									blacklistOwner.setIcNo(personnel.getIdentificationNo());
									blacklistownerDirectorList.add(blacklistOwner);
								}
							}
							this.getManagedBean().setBlacklistownerDirectorList(blacklistownerDirectorList);
						}
					}
					
					if(disc.isCancelCategory() 
						|| disc.isManufacturerCancel() 
							|| disc.isBumiputeraCancel()){
						List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
						List<Object[]> catList = SmApplicationUtil.retrieveCancelCategory();
						
						if(catList != null
								&& catList.size() > 0){
							for(Object[] arr : catList){
								DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
								String catCode = StringUtils.EMPTY;
								catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
								catVo.setCategoryCode(catCode);	
								catVoList.add(catVo);
							}						
							this.getManagedBean().setCategoryCodeList(catVoList);
						}
					}
					this.getManagedBean().setStartDate(DateUtils.dateToString("dd/MM/yyyy",disc.getStartDate()));
					this.getManagedBean().setEndDate(DateUtils.dateToString("dd/MM/yyyy",disc.getEndDate()));
					this.getManagedBean().setRemarks(disc.getRemark());
				}
			}
		} else {
			this.getManagedBean().setNoDisciplinary(true);
		}
	}
	*/
	//26238 - end
	//26238 -  STATR Disciplinary Action - 28111
	public void onViewDisciplinaryActionSupp(SmSupplier currSupp) {		
		log.info(currSupp+"-------");
		log.info(currSupp.getSupplierId()+"-------");
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())){
			getManagedBean().setMofSupplier(true);
		}
		
		// 28342 : Disciplinary Action - start, 28630 : Add additional role
		if(getFacesScopes().hasRoleUser(RoleCode.UPS_SDO.value()) || getFacesScopes().hasRoleUser(RoleCode.UPS_PO.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.UPS_PO.value()) || getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.UPS_SVC.value()) || getFacesScopes().hasRoleUser(RoleCode.UPS_SVO.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.UPS_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())){
			// 28630 : Add additional role - end
			getManagedBean().setDisableDownloadButtonDAHistory(true);
		}
		
		//28637 - start, 28806
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setSupplierId(currSupp.getSupplierId());
		SmAppl smAppl = SmServiceLookup.getSupplierSearchSO().retriveLatestApplBySupplierId(reqDTO).getSmAppl();
		if(smAppl.getSupplierType().equals("K") || getManagedBean().isMofSupplier())
			getManagedBean().setViewCatCode(true);
		//28637 - end, 28806

		//27876: Disciplinary Action - start - <27876>
		currSupp.setFlagSupplier(true);
		//27876: Disciplinary Action - end - <27876>
		
		//30872: Disciplinary Action - start - <30872>
		currSupp.setFlagDASuppProfile(true);
		//30872: Disciplinary Action - end - <30872>
		
		List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(currSupp);
		if(!EntityHelper.isCollectionEmpty(voList)
				&& voList.size() > 0){
			getManagedBean().setDisciplineVOs(voList);
		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		RequestContext.getCurrentInstance().execute(
				"disciplinaryActionHistoryDlg.show();");
	} 
	//26238 -  END Disciplinary Action - 28111
	
	//28281 - start
	public void viewApplication(SmAppl selectedSmAppl){
		super.viewApplication(selectedSmAppl);
		SmSupplier supplier = null;
		if(getManagedBean().isSupplierUser()){
			supplier = SmApplicationUtil.getCurrentSupplier();
		} else {
			supplier = getManagedBean().getSmSupplier();
		}
		List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
		if(!EntityHelper.isCollectionEmpty(resultList)
				&& resultList.size() > 0){
			getManagedBean().setDisciplinaryAction(true);
		} else {
			getManagedBean().setDisciplinaryAction(false);
		}
		retrieveActiveDisciplinaryAction(supplier);
	}
	//28281 - end
	
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
	
	//35336 START
	/**
	 * Check whether the rejected application was expired
	 * compare to no. of days allowed to resubmit after rejected
	 * @param SmAppl appl
	 * @return Boolean
	 */
	public Boolean checkRejectedApplExpired(SmAppl appl){
		Boolean isExpired = false;
		if(appl.getApplId() != null) {
			if(!SmApplicationUtil.isRejectedApplValidForResubmission(appl)) {
				isExpired = true;
			}
		}		
		return isExpired;
	}
	//35336 END
	
	//31033 start
	public void onViewActiveDisciplinaryAction(SmSupplier smSupplier) {

		// 27876: Disciplinary Action - start - <27876>
		smSupplier.setFlagSupplier(true);
		// 27876: Disciplinary Action - start - <27876>

		List<DisciplinaryActionVO> voList = getActiveDisciplinaryActionVO(smSupplier);
		
		// 32560, 26247 -start-
		/*
		 * 32560 - Added Checking Active DA
		 */
		findActiveDA(voList);	
		// #32560, #26247 -end-
		
		if (!EntityHelper.isCollectionEmpty(voList) && voList.size() > 0) {
			getManagedBean().setDisciplineVOs(voList);
			// #-29771 - Start.
			getManagedBean().setSmSupplier(smSupplier);
			// #-29771 - End.
			SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			PmUser sUsers = facesScopes.getUserSessionBean();

			if (sUsers != null && sUsers.getPreferredLanguage() != null) {
				reqDTO.setLanguage(sUsers.getPreferredLanguage());
			}

			reqDTO.setSupplier(smSupplier);
			ISupplierDataMaintenanceSO suppDataSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
			SupplierDataMaintenanceResDTO resDTO = null;

			if (null != suppDataSO) {
				resDTO = suppDataSO.retrieveDisciplinaryActionMsg(reqDTO);
				if (StringUtils.isNotEmpty(resDTO.getErrorMessage())) {
					if (resDTO.getErrorMessage().contains("Please click")) {
						resDTO.setErrorMessage(resDTO.getErrorMessage().split("Please click")[0]);
					}
					if (resDTO.getErrorMessage().contains("Sila semak")) {
						resDTO.setErrorMessage(resDTO.getErrorMessage().split("Sila semak")[0]);
					}
					this.getManagedBean().setDisciplinaryActionMsg(resDTO.getErrorMessage());
				} else {
					this.getManagedBean().setDisciplinaryActionMsg(StringUtils.EMPTY);
				}
			}

		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		RequestContext.getCurrentInstance().execute("disciplinaryActionHistoryDlg.show();");
	} 
	//31033 end
	
	// 26247 - start	
	/**
	 * Evaluate suspension status based on supplier. 
	 * This supposed to be from SM. 
	 * 
	 * @param scQt
	 * @param supplier
	 * @return
	 */
	public String evaluateQTWarningMessage(SmSupplier supplier) {
		if (null == supplier) {
			return null;
		}
		Locale locale = new FacesScopes().getUserLocale();
		List<String> messages = new ArrayList<>();
	
		if (evaluateCancellationBlacklist(supplier)) {
			messages.add(getCancelRegistrationOrBlacklistMessage(supplier, locale));
		}
		List<String> cancelledCategoryCodes = new ArrayList<>();
		cancelledCategoryCodes.addAll(supplier.getDeletedCategoryCodes());

		if (CollectionUtils.isNotEmpty(cancelledCategoryCodes)) {
			messages.add(MessageUtils.getMessage(MessageConst.ERROR_760557,
					new String[] { StringUtils.join(cancelledCategoryCodes.toArray(), ", ") },
					MessageUtils.BUNDLE_COMMON_MESSAGES, locale));
		}

		boolean noBumiAccount = ParameterConst.OPEN_FOR_BUMI.equals(supplier.getScQt().getOpenFor())
				&& !supplier.isBumi() && (!supplier.isDisciplinaryActions() || !evaluateCancelledBumiAccount(supplier));
		if (noBumiAccount) {
			messages.add(MessageUtils.getMessage(MessageConst.ERROR_760556, new String[] {},
					MessageUtils.BUNDLE_COMMON_MESSAGES, locale));
		} 
		return composeWarningMessages(messages);
	}
	// 26247 - end
	
	// 26247 - start
	/**
	 * Retrieve message and set message to QT DA warning dialog
	 * @param messages
	 */
	public void onViewQTAlertMessage(List<String> messages) {
		this.getManagedBean().setDisciplinaryActionMsg(composeWarningMessages(messages));
		RequestContext.getCurrentInstance().execute(
				"$('[id$=\"suspendAlertWidgetId\"]').appendTo('body');suspendAlertWidget.show();");
	}
	// 26247 - end
	
	// 26247 - start
	/**
	 * Evaluate the flag to show warning flag( for yes/no hyperlink) for
	 * non/disciplinary action
	 * 
	 * @param supplier
	 * @return flag to display alert icon
	 */
	public List<String> evaluateWarningTooltip(SmSupplier supplier) {
		List<String> messages = new ArrayList<>();
		if (supplier != null) {
			Locale locale = new FacesScopes().getUserLocale();
			if (evaluateCancellationBlacklist(supplier)) {
				messages.add(getCancelRegistrationOrBlacklistMessage(supplier, locale));
			}

			List<String> cancelledCategoryCodes = new ArrayList<>();
			cancelledCategoryCodes.addAll(supplier.getDeletedCategoryCodes());

			boolean noCancelledCategory = CollectionUtils.isNotEmpty(cancelledCategoryCodes);

			if (noCancelledCategory) {
				messages.add(MessageUtils.getMessage(MessageConst.INFO_760554,
						new String[] { StringUtils.join(cancelledCategoryCodes.toArray(), ", ") },
						MessageUtils.BUNDLE_COMMON_MESSAGES, locale));
			}

			boolean noBumiAccount = ParameterConst.OPEN_FOR_BUMI.equals(supplier.getScQt().getOpenFor())
					&& !supplier.isBumi()
					&& (!supplier.isDisciplinaryActions() || !evaluateCancelledBumiAccount(supplier));
			
			if (noBumiAccount) {
				messages.add(MessageUtils.getMessage(MessageConst.ERROR_760559, new String[] {},
						MessageUtils.BUNDLE_COMMON_MESSAGES, locale));
			}
		}
		return messages;
	}
	// 26247 - end
	
	// 26247 - start
	private String getCancelRegistrationOrBlacklistMessage(SmSupplier supplier, Locale locale) {
		SmDisciplinaryAction disciplinaryAction = supplier.getSmDisciplinaryActions().get(0);
		String statusDescription = ResourceBundleUtils.getMessage("lbl.sm.status.deleted.with.transaction", null,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		String status = null;
		if (disciplinaryAction.isBlacklistOwner() && disciplinaryAction.isPendingBlacklist()) {
			status = ResourceBundleUtils.getMessage("lbl.sm.disciplinary.jktt.blacklist.owner", null,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else if (disciplinaryAction.isCancelRegistration() && disciplinaryAction.isPendingCancel()) {
			status = ResourceBundleUtils.getMessage("lbl.sm.disciplinary.jktt.cancel.registration", null,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		}

		statusDescription = statusDescription.concat(" (" + status + ")");
		return MessageUtils.getMessage(MessageConst.INFO_760553, new String[] { statusDescription },
				MessageUtils.BUNDLE_COMMON_MESSAGES, locale);
	}
	// 26247 - end
	
	// 26247 - start
	private boolean evaluateCancelledBumiAccount(SmSupplier supplier){
		boolean cancelledBumiAccount = false;
		if (null != supplier && CollectionUtils.isNotEmpty(supplier.getSmDisciplinaryActions())) {
			SmDisciplinaryAction disciplinaryAction = supplier.getSmDisciplinaryActions().get(0);
			cancelledBumiAccount = supplier.isDisciplinaryActions() && disciplinaryAction.isBumiputeraCancel();
		}
		return cancelledBumiAccount;
	}
	// 26247 - end
	
	// 26247 - start
	/**
	 * Evaluate for suspend registration disciplinary action
	 * @param supplier
	 * @return
	 */
	public boolean evaluateSuspendRegistration(SmSupplier supplier){
		if (null != supplier && CollectionUtils.isNotEmpty(supplier.getSmDisciplinaryActions())) {
			SmDisciplinaryAction disciplinaryAction = supplier.getSmDisciplinaryActions().get(0);
			return disciplinaryAction.isSuspendRegistration();
		}
		return false;
	}
	// 26247 - end
	
	// 26247 - start
	/**
	 * Evaluate for pending register cancellation or blacklist owner disciplinary action
	 * @param supplier
	 * @return
	 */
	public boolean evaluateCancellationBlacklist(SmSupplier supplier) {
		if (null == supplier) {
			return false;
		}
		boolean suspendedBlacklisted = false;
		boolean suspendedCancelled = false;

		if (CollectionUtils.isNotEmpty(supplier.getSmDisciplinaryActions())) {
			SmDisciplinaryAction disciplinaryAction = supplier.getSmDisciplinaryActions().get(0);
			suspendedBlacklisted = disciplinaryAction.isBlacklistOwner() && disciplinaryAction.isPendingBlacklist();
			suspendedCancelled = disciplinaryAction.isCancelRegistration() && disciplinaryAction.isPendingCancel();
		}
		boolean blacklistedOrCancelled = suspendedBlacklisted || suspendedCancelled;
		return blacklistedOrCancelled && !supplier.isDisciplinaryActions();
	}
	// 26247 - end
	
	// 26247 - start
	private String composeWarningMessages(List<String> messages) {
		StringBuilder messageSb = new StringBuilder();
		int count = 0;
		for (String message : messages) {
			count++;
			messageSb
					.append("<img style='vertical-align: sub;' src='/NGeP-portlet/css/images/design/icon_error.png'>&nbsp;&nbsp;");
			messageSb.append(message);
			if (count < messages.size()) {
				messageSb.append("<br/>");
			}
		}
		return messageSb.toString();
	}
	// 26247 - end

}
