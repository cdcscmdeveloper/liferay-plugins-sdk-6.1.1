package my.ep.web.managedbean.sm.applicationView;

import java.util.Date;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "viewApplHistoryMB")
@SessionScoped
public class ViewApplHistoryManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	public static Logger log = Logger.getLogger(ViewApplHistoryManagedBean.class);//to be included for #25792
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
	
	//28771
	private String SearchDocumentMode;
	
	private Boolean displayResubmitButton; //#31248

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
			
			//28771 - start
			reqDTO.setsupportingDocMode(getSupportingDocMode());
			//28771-end
			
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
				log.error("File: ViewApplHistoryManagedBean Method: pageChangeListener Message: "+ex.getMessage()); //to be included for #25792
				return new SearchResultBean();
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
				log.error("File: ViewApplHistoryManagedBean Method: getApplicationHistoryResultBean Message: "+e.getMessage());//25792 - for log
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
		
	private String getWorkflowStatus(Long applId, String statusOri) {
		SmWorkflowStatus st = SmCommonUtil
				.getCurrentActiveWokFlowStatus(applId);
		String retVal = statusOri;
		if (st.getPmStatus() != null
				&& st.getPmStatus().getStatusId() != null) {
			PmStatusDesc sd = SmCommonUtil.getStatusDescByStatusId(st
					.getPmStatus().getStatusId());
			if (sd != null) {
				retVal = sd.getStatusName();
			}
		}
		return retVal;
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

	public void setOverlapCategoryVOList(List<SmBulkApplOverlapCatVO> overlapCategoryVOList) {
		this.overlapCategoryVOList = overlapCategoryVOList;
	}

	public List<SmBulkApplicationVO> getSmBulkApplicationVO() {
		return smBulkApplicationVO;
	}

	public void setSmBulkApplicationVO(List<SmBulkApplicationVO> smBulkApplicationVO) {
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
	
	//28771 - start
	public String getSearchDocumentMode(){
		return SearchDocumentMode;
	}
	
	public void setSearchDocumentMode(String SearchDocumentMode){
		this.SearchDocumentMode = SearchDocumentMode;
	}
	//28771 - end
	
	public Boolean getDisplayResubmitButton() {
		return displayResubmitButton;
	}
	public void setDisplayResubmitButton(Boolean displayResubmitButton) {
		this.displayResubmitButton = displayResubmitButton;
	}
}
