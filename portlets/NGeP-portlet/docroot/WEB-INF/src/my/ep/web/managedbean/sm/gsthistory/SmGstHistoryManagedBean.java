package my.ep.web.managedbean.sm.gsthistory;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "gstHistoryMB")
@SessionScoped
public class SmGstHistoryManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6747919477188951717L;

	private IPagingDataModel<SmGstSupplier> smGstDataModel = new SmGstPagingDataModel();
	private SmSupplier searchSupplier;
	private String searchMofNo;
	private String searchSuppType;
	private Long searchPmZoneId;
	private List<PmZone> zoneList;

	private class SmGstPagingDataModel extends PagingDataModel<SmGstSupplier> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmGstPagingDataModel() {
			super("supplierGstHistoryDtId");
		}

		@Override
		protected ISearchResultBean<SmGstSupplier> pageChangeListener(
				ISearchBean<SmGstSupplier> searchBean) {
			SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
			reqDTO.setGstSearchBean(searchBean);
			try {
				if (!isSearchCriteriaEmpty()) {
					return getGstHistoryResultBean(reqDTO);
				} else {
					return new SearchResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				return null;
			}
		}

		private ISearchResultBean<SmGstSupplier> getGstHistoryResultBean(
				SupplierGstReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			reqDTO.setMofAccountNo(getSearchMofNo());
			reqDTO.setSupplierType(getSearchSuppType());
			reqDTO.setPmZoneId(getSearchPmZoneId());
			return SmServiceLookup.getSupplierGstSO()
					.retrieveGstHistoryBySupplierSearchCriteria(reqDTO)
					.getGstHistoryResultBean();
		}
	}

	private boolean isSearchCriteriaEmpty() {
		boolean retVal = true;
		if (StringUtils.isNotBlank(getSearchMofNo())
				|| StringUtils.isNotBlank(getSearchSuppType())
				|| (getSearchSupplier() != null && (StringUtils
						.isNotBlank(getSearchSupplier().getCompanyName()) || StringUtils
						.isNotBlank(getSearchSupplier().getRegNo())))) {
			retVal = false;
		}
		return retVal;
	}

	public void searchGstHistory() {
		smGstDataModel.reloadData();
	}

	public SmSupplier getSearchSupplier() {
		return searchSupplier;
	}

	public void setSearchSupplier(SmSupplier searchSupplier) {
		this.searchSupplier = searchSupplier;
	}

	public String getSearchMofNo() {
		return searchMofNo;
	}

	public void setSearchMofNo(String searchMofNo) {
		this.searchMofNo = searchMofNo;
	}

	public String getSearchSuppType() {
		return searchSuppType;
	}

	public void setSearchSuppType(String searchSuppType) {
		this.searchSuppType = searchSuppType;
	}

	public Long getSearchPmZoneId() {
		return searchPmZoneId;
	}

	public void setSearchPmZoneId(Long searchPmZoneId) {
		this.searchPmZoneId = searchPmZoneId;
	}

	public List<PmZone> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<PmZone> zoneList) {
		this.zoneList = zoneList;
	}

}
