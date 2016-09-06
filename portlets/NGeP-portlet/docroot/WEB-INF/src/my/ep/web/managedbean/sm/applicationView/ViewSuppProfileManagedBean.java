package my.ep.web.managedbean.sm.applicationView;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmRegStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.vo.SmSupplierBasicInfoVO;

@ManagedBean(name = "viewSuppProfileMB")
@SessionScoped
public class ViewSuppProfileManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9029253736925390560L;
	private PmUser applApprover;
	private String approvedDate;
	private SmSupplier selectedSupplier;
	private SmSupplierCategory selectedCategory;
	private SmSupplierBasicInfoVO basicInfo;
	private DisciplinaryActionVO disciplineVO;
	private String applnApprovalRemarks;
	private boolean showParentMsg;
	private boolean allowedToDoMaintenance;
	private boolean hasActiveTransaction;
	private boolean editDisciplinaryAction;
	private PmRegStatus existingRegStatus;
	private Date existingMofExpiryDate;
	private List<SmSupplierCategory> selectedApprovedCategories;
	private List<SmSupplierCategory> selectedPendingApprovedCategories;
	private List<SmSupplierCategory> deleteApprovedCategories;
	private IPagingDataModel<SmSupplier> smSupplierDataModel = new SmSupplierPagingDataModel();

	public ViewSuppProfileManagedBean() {

	}

	private class SmSupplierPagingDataModel extends PagingDataModel<SmSupplier> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("supplierApplHistoryDtId");
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSupplierSearchBean(searchBean);
			reqDTO.setViewApplicationHistory(isViewApplicationHistory());

			try {
				if (!isSupplierUser()) {
					if (!isSearchCriteriaEmpty()) {
						return getApplicationHistoryResultBean(reqDTO);
					} else {
						return new SearchResultBean();
					}
				} else {
					return new SearchResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<SmSupplier> getApplicationHistoryResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			reqDTO.setMofAccountNo(getSearchMofNo());
			reqDTO.setEpNo(getSearchEpNo());
			reqDTO.setPmRegStatusId(getSearchPmRegStatusId());
			reqDTO.setSupplierType(getSearchSuppType());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setPmZoneId(getSearchPmZoneId());
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveSupplierBySearchCriteria(reqDTO)
					.getSupplierResultBean();
		}
	}

	/**
	 * Search supplier latest application
	 */
	public void searchSupplier() {
		smSupplierDataModel.reloadData();
	}

	public IPagingDataModel<SmSupplier> getSmSupplierDataModel() {
		return smSupplierDataModel;
	}

	public void setSmSupplierDataModel(
			IPagingDataModel<SmSupplier> smSupplierDataModel) {
		this.smSupplierDataModel = smSupplierDataModel;
	}

	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

	public SmSupplierCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(SmSupplierCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public SmSupplierBasicInfoVO getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(SmSupplierBasicInfoVO basicInfo) {
		this.basicInfo = basicInfo;
	}

	public String getApplnApprovalRemarks() {
		return applnApprovalRemarks;
	}

	public void setApplnApprovalRemarks(String applnApprovalRemarks) {
		this.applnApprovalRemarks = applnApprovalRemarks;
	}

	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	public PmUser getApplApprover() {
		return applApprover;
	}

	public void setApplApprover(PmUser applApprover) {
		this.applApprover = applApprover;
	}

	public String getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}

	public boolean isAllowedToDoMaintenance() {
		return allowedToDoMaintenance;
	}

	public void setAllowedToDoMaintenance(boolean allowedToDoMaintenance) {
		this.allowedToDoMaintenance = allowedToDoMaintenance;
	}

	public PmRegStatus getExistingRegStatus() {
		return existingRegStatus;
	}

	public void setExistingRegStatus(PmRegStatus existingRegStatus) {
		this.existingRegStatus = existingRegStatus;
	}

	public List<SmSupplierCategory> getDeleteApprovedCategories() {
		return deleteApprovedCategories;
	}

	public void setDeleteApprovedCategories(
			List<SmSupplierCategory> deleteApprovedCategories) {
		this.deleteApprovedCategories = deleteApprovedCategories;
	}

	public DisciplinaryActionVO getDisciplineVO() {
		return disciplineVO;
	}

	public void setDisciplineVO(DisciplinaryActionVO disciplineVO) {
		this.disciplineVO = disciplineVO;
	}

	public boolean isEditDisciplinaryAction() {
		return editDisciplinaryAction;
	}

	public void setEditDisciplinaryAction(boolean editDisciplinaryAction) {
		this.editDisciplinaryAction = editDisciplinaryAction;
	}

	public Date getExistingMofExpiryDate() {
		return existingMofExpiryDate;
	}

	public void setExistingMofExpiryDate(Date existingMofExpiryDate) {
		this.existingMofExpiryDate = existingMofExpiryDate;
	}

	public List<SmSupplierCategory> getSelectedApprovedCategories() {
		return selectedApprovedCategories;
	}

	public void setSelectedApprovedCategories(
			List<SmSupplierCategory> selectedApprovedCategories) {
		this.selectedApprovedCategories = selectedApprovedCategories;
	}

	public List<SmSupplierCategory> getSelectedPendingApprovedCategories() {
		return selectedPendingApprovedCategories;
	}

	public void setSelectedPendingApprovedCategories(
			List<SmSupplierCategory> selectedPendingApprovedCategories) {
		this.selectedPendingApprovedCategories = selectedPendingApprovedCategories;
	}

	public boolean isHasActiveTransaction() {
		return hasActiveTransaction;
	}

	public void setHasActiveTransaction(boolean hasActiveTransaction) {
		this.hasActiveTransaction = hasActiveTransaction;
	}

}
