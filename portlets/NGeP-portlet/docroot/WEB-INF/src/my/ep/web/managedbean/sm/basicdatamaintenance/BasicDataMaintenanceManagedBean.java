package my.ep.web.managedbean.sm.basicdatamaintenance;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "basicDataMaintenanceMB")
@SessionScoped
public class BasicDataMaintenanceManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8162503608718241997L;
	private String oldBusinessType;
	private String remarks;
	private String searchMode;
	private SmSupplier searchSupplier;
	private SmSupplier selectedSupplier;
	private List<SelectItem> businessTypeItems;
	private IPagingDataModel<SmSupplier> smSupplierDataModel = new SmSupplierPagingDataModel();

	private class SmSupplierPagingDataModel extends PagingDataModel<SmSupplier> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("basicSupplierDtId");
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSupplierSearchBean(searchBean);

			try {
				if (!isSearchCriteriaEmpty()) {
					return getBasicSupplierResultBean(reqDTO);
				} else {
					return new SearchResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<SmSupplier> getBasicSupplierResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveBasicSupplierBySearchCriteria(reqDTO)
					.getSupplierResultBean();
		}
	}

	private boolean isSearchCriteriaEmpty() {
		boolean retVal = true;
		if (getSearchSupplier() != null
				&& (StringUtils
						.isNotBlank(getSearchSupplier().getCompanyName())
						|| StringUtils.isNotBlank(getSearchSupplier()
								.getRegNo()) || StringUtils
							.isNotBlank(getSearchSupplier().getEpNo()))) {
			retVal = false;
		}
		return retVal;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(String searchMode) {
		this.searchMode = searchMode;
	}

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

	public SmSupplier getSearchSupplier() {
		return searchSupplier;
	}

	public void setSearchSupplier(SmSupplier searchSupplier) {
		this.searchSupplier = searchSupplier;
	}

	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

	public List<SelectItem> getBusinessTypeItems() {
		return businessTypeItems;
	}

	public void setBusinessTypeItems(List<SelectItem> businessTypeItems) {
		this.businessTypeItems = businessTypeItems;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getOldBusinessType() {
		return oldBusinessType;
	}

	public void setOldBusinessType(String oldBusinessType) {
		this.oldBusinessType = oldBusinessType;
	}

}
