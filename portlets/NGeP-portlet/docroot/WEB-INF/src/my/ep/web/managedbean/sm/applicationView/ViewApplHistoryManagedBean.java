package my.ep.web.managedbean.sm.applicationView;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
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

//			int i = 0;
//			for (Object[] obj : retVal.getResultList()) {
//				obj[6] = getWorkflowStatus(Long.parseLong(obj[10] + ""), obj[6]
//						+ "");
//				retVal.getResultList().set(i, obj);
//				i++;
//			}
			return retVal;
		}
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
}
