package my.ep.web.backingbean.sm.applicationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
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
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "viewSuppProfileBackingBean")
@RequestScoped
public class ViewSuppProfileBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> implements Serializable {
	private static Logger logger = Logger.getLogger(ViewSuppProfileBackingBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 377766773512626830L;

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
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
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
	
	/**
	 * @param smSupplier
	 */
	public void onViewDisciplinaryAction(SmSupplier smSupplier) {
		
		//27876: Disciplinary Action - start - <27876>
		smSupplier.setFlagSupplier(true);
		//27876: Disciplinary Action - start - <27876>
		
		List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(smSupplier);
		
		/*
		 * 32560 - Added Checking Active DA
		 */
		findActiveDA(voList);	

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
	
	//31033 start
	public void onViewActiveDisciplinaryAction(SmSupplier smSupplier) {

		// 27876: Disciplinary Action - start - <27876>
		smSupplier.setFlagSupplier(true);
		// 27876: Disciplinary Action - start - <27876>

		List<DisciplinaryActionVO> voList = getActiveDisciplinaryActionVO(smSupplier);
		/*
		 * 32560 - Added Checking Active DA
		 */
		findActiveDA(voList);	
		
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
}
