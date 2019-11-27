package my.ep.web.backingbean.sm.applicationView;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.SLAUtils;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

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
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
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
			SLAUtils.getInstance().logSLAInfo("SM","SR",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
			getManagedBean().setRenderedBackButton(false);
			//26238 - start
			if(supplier != null){
				retrieveActiveDisicplinaryAction(supplier);
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
				SLAUtils.getInstance().logSLAInfo("SM","",
						"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
			} else {
				//LOG : 26549 : SLA Monitoring
				SLAUtils.getInstance().logSLAInfo("SM","VIEW PROFILE",
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
	public void onViewDisciplinaryAction() {
		SmSupplier currSupp = null;
		if(getManagedBean().isSupplierUser()){
			currSupp = SmApplicationUtil.getCurrentSupplier();
		} else {
			currSupp = getManagedBean().getSearchSupplier();
		}
		
		if(getManagedBean().isMofSupplier()){
			getManagedBean().setMofSupplier(true);
		}
		
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
	
	private static List<DisciplinaryActionVO> getDisciplinaryActionVO(SmSupplier currSupp) {
		List<DisciplinaryActionVO> voList = new ArrayList<DisciplinaryActionVO>();
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		perReqDTO.setSmSupplierId(currSupp.getSupplierId());
		
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierId(perReqDTO);
		
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
						
						if(disciplinaryAction.isCancelCategory()){
							List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
							List<Object[]> catList = SmApplicationUtil.retrieveCancelCategory();
							
							if(!EntityHelper.isCollectionEmpty(catList)
									&& catList.size() > 0){
								for(Object[] arr : catList){
									DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
									String catCode = StringUtils.EMPTY;
									catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
									catVo.setCategoryCode(catCode);	
									catVoList.add(catVo);
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
		
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierId(perReqDTO);
		
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
	//26238 - end

}
