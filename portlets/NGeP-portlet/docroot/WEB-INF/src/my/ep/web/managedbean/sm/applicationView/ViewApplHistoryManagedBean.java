package my.ep.web.managedbean.sm.applicationView;


import java.util.Date;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "viewApplHistoryMB")
@SessionScoped
public class ViewApplHistoryManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1191700333390541593L;
	private SmAppl selectedApplHistory;
	private boolean claimTask;
	private IPagingDataModel<Object[]> smSupplierDataModel = new SmSupplierPagingDataModel();
	private PmTrackingDiaryBackingBean trackingDiary;
	private List<SmBulkApplOverlapCatVO> overlapCategoryVOList;
	private List<SmBulkApplicationVO> smBulkApplicationVO;
	private SmAttachment cancellationAttachment;
	private SmMofAccount currMof;

	//25792 - start
	private Date currentDate = new Date();
	private String searchAccountType;
	//25792 - end

	//26238
	private List<DisciplinaryActionVO> disciplineVOs; 
	private boolean disciplinaryAction;
	
	//28237 - start
	private boolean warningExist;
	private boolean warning;
	private boolean cancelCategoryCode;
	private boolean suspendRegistration;
	private boolean cancelRegistration;
	private boolean blacklistOwnerOrDirector;
	private boolean bumiputeraStatusCancel;
	private boolean manufacturerStatusCancel;
	private List<DisciplinaryActionVO> disciplinaryActionVoList;
	private List<SmPersonnel> ownerAndDirectorList; 
	private List<SmPersonnel> selectedOwnerAndDirectorList; 
	private String applType; 
	private boolean viewFlag; 
	private SmSupplier smSupp; 
	private boolean viewMode; 
	private boolean mofNoExist;
	
	private List<BlacklistOwnerDirectorVO> blacklistownerDirectorList;
	private String startDate;
	private String endDate;
	private String remarks;
	private boolean noDisciplinary;
	private List<DisciplinaryActionCategoryVO> categoryCodeList;	
	//28237 - end


	public ViewApplHistoryManagedBean() {

	}
	
	private class SmSupplierPagingDataModel extends PagingDataModel<Object[]> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("supplierApplHistoryDtId");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setObjArrSearchBean(searchBean);
			reqDTO.setViewApplicationHistory(isViewApplicationHistory());
			
			try {
				if (!isSupplierUser()) {
					if (!isSearchCriteriaEmpty()) {
						return getApplicationHistoryResultBean(reqDTO);
					} else {
						return new SearchResultBean();
					}
				} else {
					return getApplicationHistoryResultBean(reqDTO);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<Object[]> getApplicationHistoryResultBean(
				SmApplRevisionReqDTO reqDTO) {
			//25792 - start
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			if(getAccountType() != null) {
				if(getAccountType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_BASIC)) {
					reqDTO.setEpNo(getSearchEpNo());
					reqDTO.setApplicationType(getSearchApplType());
					reqDTO.setApplicationNo(getSearchApplNo());
					reqDTO.setPmStatusId(getSearchApplStatusId());
				}
				else if(getAccountType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
					reqDTO.setAgencyType(getAgencyType());
					reqDTO.setAgencyName(getAgencyName());
					reqDTO.setEpNo(getSearchEpNo());
				}
				else {					
					reqDTO.setMofAccountNo(getSearchMofNo());
					reqDTO.setPmRegStatusId(getSearchPmRegStatusId());
					reqDTO.setSupplierType(getSearchSuppType());
					reqDTO.setPmZoneId(getSearchPmZoneId());
					reqDTO.setEpNo(getSearchEpNo());
					reqDTO.setApplicationType(getSearchApplType());
					reqDTO.setApplicationNo(getSearchApplNo());
					reqDTO.setPmStatusId(getSearchApplStatusId());
				}
				
				reqDTO.setAccountType(getAccountType());
			} else {
				//#25792 START Changes Fix error Null Pointer
				if(getSearchSupplier().getSmAppl() != null && SupplierManagementConst.SUPP_TYPE_BASIC.equalsIgnoreCase(getSearchSupplier().getSmAppl().getSupplierType())) {
					reqDTO.setAccountType(SupplierManagementConst.SUPP_TYPE_BASIC);
				} else if(getSearchSupplier().getSmAppl() != null && SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT.equalsIgnoreCase(getSearchSupplier().getSmAppl().getSupplierType())) {
					reqDTO.setAccountType(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT);
					
				} else {
					reqDTO.setAccountType("M");
				}
				//#25792 END Changes Fix error Null Pointer
				reqDTO.setEpNo(getSearchSupplier().getEpNo());
			}
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setSmSupplier(getSearchSupplier());
			//25792 - end

			ISearchResultBean<Object[]> retVal = SmServiceLookup
					.getSmApplRevisionSO()
					.retrieveApplicationHistoryUsingNativeQuery(reqDTO)
					.getObjectArrResultBean();
			//Bug #25601 :
			int[] indexToCheck = new int[]{0, 10, 11, 12, 13,1}; 
			List<Object[]> resultListTemp = new ArrayList();
			String previousAppl="applno,";
			try {
				for (Object[] obj : retVal.getResultList()) {
					//Bug #25601 : SM SHOWSTOPPER: Duplicate MOF Account for New Supplier Registration //10 = appl id & 11 = supplier id
				
						boolean removeDuplicate = true;
						for(int v = 0;v < indexToCheck.length;v++){
							if(obj[indexToCheck[v]]==null){
								removeDuplicate = false;
								break;
							}
						}
						if(removeDuplicate){	
							//Bug #25601 : SM SHOWSTOPPER: Duplicate MOF Account for New Supplier Registration 
							/*if(previousAppl.contains(obj[0].toString())){
								continue;
							}else{
								previousAppl = previousAppl+","+obj[0].toString();
							}*/
							//Condition for MOF NO
							if(obj[8]==null || "".equalsIgnoreCase(obj[8].toString())){
								//get MOF NO
								MofAccountReqDTO reqMofDTO = new MofAccountReqDTO();
								reqMofDTO.setSupplierId(Long.parseLong(obj[11].toString()));
								reqMofDTO.setApplId(Long.parseLong(obj[10].toString()));
								MofAccountResDTO resDto = SmCommonUtil.getMofAccountBySuppIdAndApplId(reqMofDTO);
								if(resDto.getMofAccount() != null){
									obj[8] = resDto.getMofAccount().getMofNo();
								}else{
									//2. For inprogress application which is New Supplier Registration/Upgrade MOF Account (application no start from KN-XXXXXX-XXXX / JN-XXXXXX-XXXX), the MOF No will display N/A. If this type of application has been reject, MOF No still display N/A due to the MOF registration not completed yet.
									if(obj[14] != null && (SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PROCESSING_FEE.equalsIgnoreCase(obj[14].toString()) || SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PAYMENT.equalsIgnoreCase(obj[14].toString()) || SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM.equalsIgnoreCase(obj[14].toString()) || SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER.equalsIgnoreCase(obj[14].toString()) || SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER.equalsIgnoreCase(obj[14].toString())) && "N".equalsIgnoreCase(obj[1].toString())){
										obj[8] = SmCommonUtil.getMessage("lbl.sm.no.available", null);
									}
									//1.& 2. (SM_Appl : Is_Active_Appl = 1 and Record_Status = 1) except application no start from (KN-XXXXXX-XXXX / JN-XXXXXX-XXXX) will display based on active MOF Account (Record_Status = 1)
									else if(Integer.parseInt(obj[13].toString())==1 &&  Integer.parseInt(obj[12].toString())==1 && (!obj[0].toString().contains("KN") || !obj[0].toString().contains("JN"))){
										obj[8] = getMofNo(Long.parseLong(obj[11].toString()),Long.parseLong(obj[10].toString()),
												obj[8]==null?StringUtils.EMPTY: obj[8].toString());
									}else if((obj[0].toString().contains("KN") || obj[0].toString().contains("JN"))){
											obj[8] = getMofNo(Long.parseLong(obj[11].toString()),Long.parseLong(obj[10].toString()),
													obj[8]==null?StringUtils.EMPTY: obj[8].toString());
									}
									//3. For other rejected application that not include in scenario No. 2, the MOF No will display the previous application MOF No (registered application before the application has been rejected).
									else if("R".equalsIgnoreCase(obj[1].toString())){
										obj[8] = getMofNoPrevious(Long.parseLong(obj[11].toString()),Long.parseLong(obj[10].toString()),
												obj[8]==null?StringUtils.EMPTY: obj[8].toString(),null);
									}
									//3.1 - for appl record status 9 but next gen ep
									else if(Integer.parseInt(obj[13].toString())==0 && (!obj[0].toString().contains("KN") || !obj[0].toString().contains("JN"))){
											obj[8] = getMofNoPrevious(Long.parseLong(obj[11].toString()),Long.parseLong(obj[10].toString()),
													obj[8]==null?StringUtils.EMPTY: obj[8].toString(),DbValueConst.RECORD_STATUS_EXPIRED); 
										
									}
									//4. For registered application from migrated Supplier (from old eP) that has application no start with K-XXXXXX-XXXX/J-XXXXXX-XXXX, the MOF No will pointing to the old eP MOF No (as per migrated MOF No).
									 //Read record status 9
									else if(!checkNotMigratedAppl(obj[0].toString()) && (obj[0].toString().contains("K") || obj[0].toString().contains("J"))){
										obj[8] = getMofNoPrevious(Long.parseLong(obj[11].toString()),Long.parseLong(obj[10].toString()),
												obj[8]==null?StringUtils.EMPTY: obj[8].toString(),DbValueConst.RECORD_STATUS_DELETED); 
										//IF NULL CHECK MOF STATUS EXPIRED
										if(obj[8] == null || SmCommonUtil.getMessage("lbl.sm.no.available", null).equalsIgnoreCase(obj[8].toString())){
											obj[8] = getMofNoPrevious(Long.parseLong(obj[11].toString()),Long.parseLong(obj[10].toString()),
													obj[8]==null?StringUtils.EMPTY: obj[8].toString(),DbValueConst.RECORD_STATUS_EXPIRED); 
										}
										
									}else{
										obj[8] = getMofNo(Long.parseLong(obj[11].toString()),Long.parseLong(obj[10].toString()),
												obj[8]==null?StringUtils.EMPTY: obj[8].toString());
									}
								}
								
							}
						}
						//Bug #25601 : SM SHOWSTOPPER: Duplicate MOF Account for New Supplier Registration 
						resultListTemp.add(obj);
				}
				//Bug #25601 : SM SHOWSTOPPER: Duplicate MOF Account for New Supplier Registration 
				retVal.getResultList().clear();
				retVal.getResultList().addAll(resultListTemp);
				
			}catch(Exception e) {
				e.printStackTrace();
				throw new EpException(ExceptionUtils.getStackTrace(e));
			}
			return retVal;
		}
	}
	//Bug #25601 : SM SHOWSTOPPER: Duplicate MOF Account for New Supplier Registration
	private String getMofNo(Long supplierId,Long applId, String mofNo) {
		if (StringUtils.isBlank(mofNo)) {
			//SmMofAccount acc = SmCommonUtil.getPreviousMofAccountBySuppIdAndSuppType(
			//		supplierId,applId, null);
			SmMofAccount acc = SmCommonUtil.getMofAccountBySuppIdAndSuppType(
					supplierId,null);
			if (acc != null) {
				mofNo = acc.getMofNo();
			}
		}
		return StringUtils.isBlank(mofNo) ? SmCommonUtil.getMessage(
				"lbl.sm.no.available", null) : mofNo;
	}
	//Bug #25601 : SM SHOWSTOPPER: Duplicate MOF Account for New Supplier Registration
		private String getMofNoPrevious(Long supplierId,Long applId, String mofNo,Integer recordStatus) {
			if (StringUtils.isBlank(mofNo)) {
				SmMofAccount acc = SmCommonUtil.getPreviousMofAccountBySuppIdAndSuppType(
						supplierId,applId, null,recordStatus);
				if (acc != null) {
					mofNo = acc.getMofNo();
				}
			}
			return StringUtils.isBlank(mofNo) ? SmCommonUtil.getMessage(
					"lbl.sm.no.available", null) : mofNo;
		}
		//Bug #25601 : SM SHOWSTOPPER: Duplicate MOF Account for New Supplier Registration
		public boolean checkNotMigratedAppl(String applNo){
			boolean newePAppl = false;
			if(applNo.contains("KN") || applNo.contains("KU") || applNo.contains("KB")|| applNo.contains("KA") || applNo.contains("JN") || applNo.contains("JU") || applNo.contains("JA")){
				newePAppl = true;
			}
			return newePAppl;
		}

	/**
	 * Search supplier application history
	 */
	public void searchSupplier() {
		validateAtLeastOneSearchCriteria();
		smSupplierDataModel.reloadData();
	}

	public IPagingDataModel<Object[]> getSmSupplierDataModel() {
		return smSupplierDataModel;
	}

	public void setSmSupplierDataModel(
			IPagingDataModel<Object[]> smSupplierDataModel) {
		this.smSupplierDataModel = smSupplierDataModel;
	}

	public SmAppl getSelectedApplHistory() {
		return selectedApplHistory;
	}

	public void setSelectedApplHistory(SmAppl selectedApplHistory) {
		this.selectedApplHistory = selectedApplHistory;
	}

	public boolean isClaimTask() {
		return claimTask;
	}

	public void setClaimTask(boolean claimTask) {
		this.claimTask = claimTask;
	}

	public PmTrackingDiaryBackingBean getTrackingDiary() {
		if (trackingDiary == null) {
			return new PmTrackingDiaryBackingBean();
		} else {
			return trackingDiary;
		}
	}

	public void setTrackingDiary(PmTrackingDiaryBackingBean trackingDiary) {
		this.trackingDiary = trackingDiary;
	}

	public List<SmBulkApplOverlapCatVO> getOverlapCategoryVOList() {
		return overlapCategoryVOList;
	}

	public void setOverlapCategoryVOList(
			List<SmBulkApplOverlapCatVO> overlapCategoryVOList) {
		this.overlapCategoryVOList = overlapCategoryVOList;
	}

	public List<SmBulkApplicationVO> getSmBulkApplicationVO() {
		return smBulkApplicationVO;
	}

	public void setSmBulkApplicationVO(
			List<SmBulkApplicationVO> smBulkApplicationVO) {
		this.smBulkApplicationVO = smBulkApplicationVO;
	}

	public SmAttachment getCancellationAttachment() {
		return cancellationAttachment;
	}

	public void setCancellationAttachment(SmAttachment cancellationAttachment) {
		this.cancellationAttachment = cancellationAttachment;
	}

	public SmMofAccount getCurrMof() {
		return currMof;
	}

	public void setCurrMof(SmMofAccount currMof) {
		this.currMof = currMof;
	}
	//25792 - start
	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	
	public String getSearchAccountType() {
		return searchAccountType;
	}
	public void setSearchAccountType(String searchAccountType) {
		this.searchAccountType = searchAccountType;
	}
	//25792 - end
	public List<DisciplinaryActionVO> getDisciplineVOs() {
		return disciplineVOs;
	}
	
	public void setDisciplineVOs(List<DisciplinaryActionVO> disciplineVOs) {
		this.disciplineVOs = disciplineVOs;
	}
	public boolean isDisciplinaryAction() {
		return disciplinaryAction;
	}
	public void setDisciplinaryAction(boolean disciplinaryAction) {
		this.disciplinaryAction = disciplinaryAction;
	}
	
	
	//28237 - start
	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public boolean isCancelCategoryCode() {
		return cancelCategoryCode;
	}

	public void setCancelCategoryCode(boolean cancelCategoryCode) {
		this.cancelCategoryCode = cancelCategoryCode;
	}

	public boolean isSuspendRegistration() {
		return suspendRegistration;
	}

	public void setSuspendRegistration(boolean suspendRegistration) {
		this.suspendRegistration = suspendRegistration;
	}

	public boolean isCancelRegistration() {
		return cancelRegistration;
	}

	public void setCancelRegistration(boolean cancelRegistration) {
		this.cancelRegistration = cancelRegistration;
	}

	public boolean isBlacklistOwnerOrDirector() {
		return blacklistOwnerOrDirector;
	}

	public void setBlacklistOwnerOrDirector(boolean blacklistOwnerOrDirector) {
		this.blacklistOwnerOrDirector = blacklistOwnerOrDirector;
	}

	public boolean isBumiputeraStatusCancel() {
		return bumiputeraStatusCancel;
	}

	public void setBumiputeraStatusCancel(boolean bumiputeraStatusCancel) {
		this.bumiputeraStatusCancel = bumiputeraStatusCancel;
	}

	public boolean isManufacturerStatusCancel() {
		return manufacturerStatusCancel;
	}

	public void setManufacturerStatusCancel(boolean manufacturerStatusCancel) {
		this.manufacturerStatusCancel = manufacturerStatusCancel;
	}

	public List<DisciplinaryActionVO> getDisciplinaryActionVoList() {
		return disciplinaryActionVoList;
	}

	public void setDisciplinaryActionVoList(List<DisciplinaryActionVO> disciplinaryActionVoList) {
		this.disciplinaryActionVoList = disciplinaryActionVoList;
	}

	public List<SmPersonnel> getOwnerAndDirectorList() {
		return ownerAndDirectorList;
	}

	public void setOwnerAndDirectorList(List<SmPersonnel> ownerAndDirectorList) {
		this.ownerAndDirectorList = ownerAndDirectorList;
	}

	public List<SmPersonnel> getSelectedOwnerAndDirectorList() {
		return selectedOwnerAndDirectorList;
	}

	public void setSelectedOwnerAndDirectorList(List<SmPersonnel> selectedOwnerAndDirectorList) {
		this.selectedOwnerAndDirectorList = selectedOwnerAndDirectorList;
	}

	public String getApplType() {
		return applType;
	}

	public void setApplType(String applType) {
		this.applType = applType;
	} 

	public boolean isViewFlag() {
		return viewFlag;
	}

	public void setViewFlag(boolean viewFlag) {
		this.viewFlag = viewFlag;
	}

	public SmSupplier getSmSupp() {
		return smSupp;
	}

	public void setSmSupp(SmSupplier smSupp) {
		this.smSupp = smSupp;
	}

	public boolean isViewMode() {
		return viewMode;
	}

	public void setViewMode(boolean viewMode) {
		this.viewMode = viewMode;
	}
	
	public void setMofNoExist(boolean mofNoExist) {
		this.mofNoExist = mofNoExist;
	}

	public List<BlacklistOwnerDirectorVO> getBlacklistownerDirectorList() {
		return blacklistownerDirectorList;
	}

	public void setBlacklistownerDirectorList(List<BlacklistOwnerDirectorVO> blacklistownerDirectorList) {
		this.blacklistownerDirectorList = blacklistownerDirectorList;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isNoDisciplinary() {
		return noDisciplinary;
	}

	public void setNoDisciplinary(boolean noDisciplinary) {
		this.noDisciplinary = noDisciplinary;
	}

	public List<DisciplinaryActionCategoryVO> getCategoryCodeList() {
		return categoryCodeList;
	}

	public void setCategoryCodeList(List<DisciplinaryActionCategoryVO> categoryCodeList) {
		this.categoryCodeList = categoryCodeList;
	}
	public boolean isWarningExist() {
		return warningExist;
	}
	public void setWarningExist(boolean warningExist) {
		this.warningExist = warningExist;
	}
	
	public boolean isMofNoExist() {
		return mofNoExist;
	}
	//28237 - end
}
