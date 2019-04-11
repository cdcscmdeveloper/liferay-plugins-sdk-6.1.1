package my.ep.web.managedbean.sm.gsthistory;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sm.applicationView.BaseReviewApplicationManagedBean;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "gstHistoryMB")
@SessionScoped
public class SmGstHistoryManagedBean extends BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6747919477188951717L;

	private IPagingDataModel<SmSupplier> smGstDataModel = new SmGstPagingDataModel();
	private List<SmGstSupplier> hqGstList;
	private List<SmGstSupplier> branchGstList;
	private SmSupplier selectedSupplier;

	private class SmGstPagingDataModel extends PagingDataModel<SmSupplier> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmGstPagingDataModel() {
			super("searchGstHistoryDtId");
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSupplierSearchBean(searchBean);
			try {
				if (!isGstSearchCriteriaEmpty()) {
					return getSupplierResultBean(reqDTO);
				} else {
					return new SearchResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<SmSupplier> getSupplierResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			reqDTO.setMofAccountNo(getSearchMofNo());
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveActiveSupplierBySearchCriteria(reqDTO)
					.getSupplierResultBean();
		}
	}

	private boolean isGstSearchCriteriaEmpty() {
		boolean retVal = true;
		if (StringUtils.isNotBlank(getSearchMofNo())
				|| (getSearchSupplier() != null && (StringUtils
						.isNotBlank(getSearchSupplier().getCompanyName()) || StringUtils
						.isNotBlank(getSearchSupplier().getEpNo())))) {
			retVal = false;
		}
		return retVal;
	}

	public void searchGstHistory() {
		smGstDataModel.reloadData();
	}

	public IPagingDataModel<SmSupplier> getSmGstDataModel() {
		return smGstDataModel;
	}

	public void setSmGstDataModel(IPagingDataModel<SmSupplier> smGstDataModel) {
		this.smGstDataModel = smGstDataModel;
	}

	public List<SmGstSupplier> getHqGstList() {
		return hqGstList;
	}

	public void setHqGstList(List<SmGstSupplier> hqGstList) {
		this.hqGstList = hqGstList;
	}

	public List<SmGstSupplier> getBranchGstList() {
		return branchGstList;
	}

	public void setBranchGstList(List<SmGstSupplier> branchGstList) {
		this.branchGstList = branchGstList;
	}

	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

}
