package my.ep.web.managedbean.sm.customerservice;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sm.applicationView.BaseReviewApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "smCustServiceMB")
@SessionScoped
public class SmCustomerServiceManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -659689758644790222L;
	private int targetPage;
	private int noOfCatPublished;
	private int noOfCatExpired;
	private int noOfCatApproved;
	private int noOfCatRejected;
	private String mofRegStatus;
	private SmPersonnel suppAdmin;
	private List<CmRequestCode> requestCodeLs;

	private IPagingDataModel<Object[]> smSupplierDataModel = new SmSupplierPagingDataModel();

	public SmCustomerServiceManagedBean() {

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
				if (!isSearchCriteriaEmpty()) {
					return getApplicationHistoryResultBean(reqDTO);
				} else {
					return new SearchResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<Object[]> getApplicationHistoryResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			reqDTO.setMofAccountNo(getSearchMofNo());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setEpNo(getSearchEpNo());
			reqDTO.setApplicationNo(getSearchApplNo());
			reqDTO.setPmStatusId(getSearchApplStatusId());

			ISearchResultBean<Object[]> retVal = SmServiceLookup
					.getSmApplRevisionSO()
					.retrieveApplicationHistoryUsingNativeQuery(reqDTO)
					.getObjectArrResultBean();

			int i = 0;
			for (Object[] obj : retVal.getResultList()) {
				obj[6] = getWorkflowStatus(Long.parseLong(obj[10] + ""), obj[6]
						+ "");
				retVal.getResultList().set(i, obj);
				i++;
			}
			return retVal;
		}
	}

	private String getWorkflowStatus(Long applId, String statusOri) {
		SmWorkflowStatus st = SmCommonUtil
				.getCurrentActiveWokFlowStatus(applId);
		String retVal = statusOri;
		if (st.getPmStatus() != null && st.getPmStatus().getStatusId() != null) {
			PmStatusDesc sd = SmCommonUtil.getStatusDescByStatusId(st
					.getPmStatus().getStatusId());
			if (sd != null) {
				retVal = sd.getStatusName();
			}
		}
		return retVal;
	}

	public int getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(int targetPage) {
		this.targetPage = targetPage;
	}

	public IPagingDataModel<Object[]> getSmSupplierDataModel() {
		return smSupplierDataModel;
	}

	public void setSmSupplierDataModel(
			IPagingDataModel<Object[]> smSupplierDataModel) {
		this.smSupplierDataModel = smSupplierDataModel;
	}

	public String getMofRegStatus() {
		return mofRegStatus;
	}

	public void setMofRegStatus(String mofRegStatus) {
		this.mofRegStatus = mofRegStatus;
	}

	public SmPersonnel getSuppAdmin() {
		return suppAdmin;
	}

	public void setSuppAdmin(SmPersonnel suppAdmin) {
		this.suppAdmin = suppAdmin;
	}

	public int getNoOfCatPublished() {
		return noOfCatPublished;
	}

	public void setNoOfCatPublished(int noOfCatPublished) {
		this.noOfCatPublished = noOfCatPublished;
	}

	public int getNoOfCatExpired() {
		return noOfCatExpired;
	}

	public void setNoOfCatExpired(int noOfCatExpired) {
		this.noOfCatExpired = noOfCatExpired;
	}

	public int getNoOfCatApproved() {
		return noOfCatApproved;
	}

	public void setNoOfCatApproved(int noOfCatApproved) {
		this.noOfCatApproved = noOfCatApproved;
	}

	public int getNoOfCatRejected() {
		return noOfCatRejected;
	}

	public void setNoOfCatRejected(int noOfCatRejected) {
		this.noOfCatRejected = noOfCatRejected;
	}

	public List<CmRequestCode> getRequestCodeLs() {
		return requestCodeLs;
	}

	public void setRequestCodeLs(List<CmRequestCode> requestCodeLs) {
		this.requestCodeLs = requestCodeLs;
	}

	public boolean isSearchCriteriaEmpty() {
		boolean retVal = false;
		if (StringUtils.isBlank(getSearchMofNo())
				&& StringUtils.isBlank(getSearchEpNo())
				&& StringUtils.isBlank(getSearchSupplier().getCompanyName())
				&& StringUtils.isBlank(getSearchApplNo())
				&& getSearchApplStatusId() == null) {
			retVal = true;
		}
		return retVal;
	}

}
