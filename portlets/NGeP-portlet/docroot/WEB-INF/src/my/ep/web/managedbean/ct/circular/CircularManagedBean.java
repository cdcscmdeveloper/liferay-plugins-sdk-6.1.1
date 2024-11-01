/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
//Start - #30866
import my.ep.util.SLAUtils;
import my.ep.persistence.entity.ct.circular.VCcCt;
import my.ep.persistence.entity.ct.circular.VCcDp;
import my.ep.persistence.entity.ct.circular.VCcQt;
//End - #30866

/**
 * Circular Managed Bean
 * 
 * @author kieyun
 * @since 23/12/2014
 */
@ManagedBean
@SessionScoped
public class CircularManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private boolean readOnly = true;
	private boolean trackingDiarySearch;
	private String searchCircularNo;
	private String searchCircularName;
	private String searchPhysicalCircularNo;
	private String searchCircularDocType;
	private String searchStatus;
	private String searchSupplierName;
	private String searchItemName;
	private Date searchStartDate;
	private Date searchEndDate;
	private Long searchStatusId;
	private int searchCircularStatus;
	// Start - #30866
	private String procurementMode;
	private String procurementModeCode;
	private Long pmParameterId;
	private boolean isPqCircularType = false;
	private boolean isContinuityCircularAuditSearch = false;
	private String externalPageUrlForAudit;
	private String circularDocType;
	private CtCircularVer circularCqCentralContractList;
	private CtCircularVer circularPqDirectPurchaseList;
	private CtCircularVer circularPqQtList;
	// End - #30866
	private IPagingDataModel<CtCircularVer> circularListingPagingDataModel = new CircularListingPagingDataModel();
	// Start - #30866
	private transient IPagingDataModel<VCcCt> circularCtListingDataModel = new CircularCtListingPagingDataModel();
	private transient IPagingDataModel<VCcDp> circularDpListingDataModel = new CircularDpListingPagingDataModel();
	private transient IPagingDataModel<VCcQt> circularQtListingDataModel = new CircularQtListingPagingDataModel();

	// End - #30866

	private class CircularListingPagingDataModel extends PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public CircularListingPagingDataModel() {
			super("circularVerDt", isReadOnly());
		}

		@Override
		public ISearchResultBean<CtCircularVer> pageChangeListener(ISearchBean<CtCircularVer> searchBean) {
			try {		
				if(getSearchCircularDocType()!=null) {
					SearchFilterBean filterBean = new SearchFilterBean("docType", SearchOperator.OPERATOR_EQUALS.value(),getSearchCircularDocType());
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchCircularName()!=null && getSearchCircularName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("name", SearchOperator.OPERATOR_LIKE.value(),getSearchCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchCircularNo()!=null && getSearchCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctCircular.circularNo",	SearchOperator.OPERATOR_LIKE.value(),getSearchCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getSearchPhysicalCircularNo()!=null && getSearchPhysicalCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularNo", SearchOperator.OPERATOR_LIKE.value(),getSearchPhysicalCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchStatus()!=null) {
					SearchFilterBean filterBean = new SearchFilterBean("ctCircular.recordStatus", SearchOperator.OPERATOR_EQUALS.value(),getSearchStatus());
					searchBean.getDataFilterBeans().add(filterBean);
				}

				String supplierNameField = null;
				String itemNameField = null;
				if(getSearchCircularDocType()!=null && getSearchCircularDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
					supplierNameField = "ctPanelItems.ctPanelSuppliers.supplier.companyName";
					itemNameField = "ctPanelItems.itemName";
				} else {
					supplierNameField = "ctCircularContracts.ctContract.smSupplier.companyName";
					itemNameField = "ctCircularContracts.ctContract.latestContractVer.ctContractItems.scRequestItem.itemName";
				}
				if(getSearchSupplierName()!=null && getSearchSupplierName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean(supplierNameField, SearchOperator.OPERATOR_LIKE.value(),getSearchSupplierName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchItemName()!=null && getSearchItemName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean(itemNameField, SearchOperator.OPERATOR_LIKE.value(),getSearchItemName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}	

				if(getSearchStartDate()!=null && getSearchEndDate()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getSearchStartDate()));	
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), getSearchEndDate()));		
				}
				if(getSearchStartDate()!=null && getSearchEndDate()==null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_EQUALS.value(), getSearchStartDate()));	
				}
				if(getSearchStartDate()==null && getSearchEndDate()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_EQUALS.value(), getSearchEndDate()));	
				}

				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerSearchBean(searchBean);
				reqDTO.setFilterStatusId(getSearchStatusId());
				reqDTO.setProcModeIdCheck(getPmParameterId()); //Fix - #30866
				reqDTO.setProcurementModeCode(getProcurementModeCode()); //Fix - #30866
				reqDTO.setSearchCircularStatus(getSearchCircularStatus());

				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
				CircularResDTO resDTO = null;
				if(isReadOnly()) {
					resDTO = circularSO.searchLatestCircularVer(reqDTO);
				} else {
					resDTO = circularSO.searchCircularVer(reqDTO);
				}
				if(resDTO.isSuccessful()) {
					return resDTO.getCircularVerResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}
	
	// Start - #30866
	private class CircularCtListingPagingDataModel extends
			PagingDataModel<VCcCt> {
		private static final long serialVersionUID = 1L;

		public CircularCtListingPagingDataModel() {
			super("subTableCcContract", isReadOnly());
		}

		@Override
		public ISearchResultBean<VCcCt> pageChangeListener(ISearchBean<VCcCt> searchBean) {
			try {

				if (getSearchCircularNo() != null
						&& getSearchCircularNo().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("ccr.CIRCULAR_NO",
							SearchOperator.OPERATOR_EQUALS.value(),
							getSearchCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}

				if (getCircularDocType() != null) {
					SearchFilterBean filterBean = new SearchFilterBean("ccws.DOC_TYPE",
							SearchOperator.OPERATOR_EQUALS.value(),
							getCircularDocType());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}

				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCtCircularVerSearchBean(searchBean);
				reqDTO.setFilterStatusId(getSearchStatusId());
				reqDTO.setProcModeIdCheck(getPmParameterId());
				reqDTO.setSearchCircularStatus(getSearchCircularStatus());
				searchBean.setUseEntityClass(true);

				ICircularSO circularSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularSO.class);
				CircularResDTO resDTO = null;
				resDTO = circularSO.getCircularContractSearchAudit(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getCtCircularVerResultBean();
				}
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(
						ModuleCodeConst.CONTRACT_MANAGEMENT,"CircularManagedBean", null,
						"CircularManagedBean.CircularCtListingPagingDataModel",null, e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
			}
			return null;
		}
	}

	// End - #30866
	
	// Start - #30866
	private class CircularDpListingPagingDataModel extends
			PagingDataModel<VCcDp> {
		private static final long serialVersionUID = 1L;

		public CircularDpListingPagingDataModel() {
			super("subTableCcDirectPurchase", isReadOnly());
		}

		@Override
		public ISearchResultBean<VCcDp> pageChangeListener(
				ISearchBean<VCcDp> searchBean) {
			try {

				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setDpCircularVerSearchBean(searchBean);
				reqDTO.setProcModeIdCheck(getPmParameterId());
				reqDTO.setProcurementModeCode(getProcurementMode());
				reqDTO.setCircularNo(getSearchCircularNo());
				searchBean.setUseEntityClass(true);

				ICircularSO circularSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
								ICircularSO.class);
				CircularResDTO resDTO = null;
				resDTO = circularSO.getCircularContractSearchAudit(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getDpCircularVerResultBean();
				}
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						"CircularManagedBean", null,
						"CircularManagedBean.CircularDpListingPagingDataModel",
						null, e);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e
								.toString(), e.getMessage()));
			}
			return null;
		}
	}

	// End - #30866
	
	// Start - #30866
	private class CircularQtListingPagingDataModel extends
			PagingDataModel<VCcQt> {
		private static final long serialVersionUID = 1L;

		public CircularQtListingPagingDataModel() {
			super("subTableCcQt", isReadOnly());
		}

		@Override
		public ISearchResultBean<VCcQt> pageChangeListener(
				ISearchBean<VCcQt> searchBean) {
			try {
				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setQtCircularVerSearchBean(searchBean);
				reqDTO.setProcModeIdCheck(getPmParameterId());
				reqDTO.setProcurementModeCode(getProcurementMode());
				reqDTO.setCircularNo(getSearchCircularNo());
				searchBean.setUseEntityClass(true);

				ICircularSO circularSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
								ICircularSO.class);
				CircularResDTO resDTO = null;
				resDTO = circularSO.getCircularContractSearchAudit(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getQtCircularVerResultBean();
				}
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						"CircularManagedBean", null,
						"CircularManagedBean.CircularDpListingPagingDataModel",
						null, e);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e
								.toString(), e.getMessage()));
			}
			return null;
		}
	}

	// End - #30866

	/**
	 * @return the searchCircularNo
	 */
	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	/**
	 * @param searchCircularNo the searchCircularNo to set
	 */
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}

	/**
	 * @return the searchCircularName
	 */
	public String getSearchCircularName() {
		return searchCircularName;
	}

	/**
	 * @param searchCircularName the searchCircularName to set
	 */
	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}

	/**
	 * @return the searchPhysicalCircularNo
	 */
	public String getSearchPhysicalCircularNo() {
		return searchPhysicalCircularNo;
	}

	/**
	 * @param searchPhysicalCircularNo the searchPhysicalCircularNo to set
	 */
	public void setSearchPhysicalCircularNo(String searchPhysicalCircularNo) {
		this.searchPhysicalCircularNo = searchPhysicalCircularNo;
	}

	/**
	 * @return the searchCircularDocType
	 */
	public String getSearchCircularDocType() {
		return searchCircularDocType;
	}

	/**
	 * @param searchCircularDocType the searchCircularDocType to set
	 */
	public void setSearchCircularDocType(String searchCircularDocType) {
		this.searchCircularDocType = searchCircularDocType;
	}

	/**
	 * @return the searchStatus
	 */
	public String getSearchStatus() {
		return searchStatus;
	}

	/**
	 * @param searchStatus the searchStatus to set
	 */
	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}

	/**
	 * @return the circularListingPagingDataModel
	 */
	public IPagingDataModel<CtCircularVer> getCircularListingPagingDataModel() {
		return circularListingPagingDataModel;
	}

	/**
	 * @param circularListingPagingDataModel the circularListingPagingDataModel to set
	 */
	public void setCircularListingPagingDataModel(
			IPagingDataModel<CtCircularVer> circularListingPagingDataModel) {
		this.circularListingPagingDataModel = circularListingPagingDataModel;
	}

	/**
	 * @param circularListingPagingDataModel the circularListingPagingDataModel to reset
	 */
	public void resetCircularListingPagingDataModel() {
		this.circularListingPagingDataModel = new CircularListingPagingDataModel();
	}

	/**
	 * @return the searchSupplierName
	 */
	public String getSearchSupplierName() {
		return searchSupplierName;
	}

	/**
	 * @param searchSupplierName the searchSupplierName to set
	 */
	public void setSearchSupplierName(String searchSupplierName) {
		this.searchSupplierName = searchSupplierName;
	}

	/**
	 * @return the searchItemName
	 */
	public String getSearchItemName() {
		return searchItemName;
	}

	/**
	 * @param searchItemName the searchItemName to set
	 */
	public void setSearchItemName(String searchItemName) {
		this.searchItemName = searchItemName;
	}

	/**
	 * @return the searchStartDate
	 */
	public Date getSearchStartDate() {
		return searchStartDate;
	}

	/**
	 * @param searchStartDate the searchStartDate to set
	 */
	public void setSearchStartDate(Date searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	/**
	 * @return the searchEndDate
	 */
	public Date getSearchEndDate() {
		return searchEndDate;
	}

	/**
	 * @param searchEndDate the searchEndDate to set
	 */
	public void setSearchEndDate(Date searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	/**
	 * @return the searchStatusId
	 */
	public Long getSearchStatusId() {
		return searchStatusId;
	}

	/**
	 * @param searchStatusId the searchStatusId to set
	 */
	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the trackingDiarySearch
	 */
	public boolean isTrackingDiarySearch() {
		return trackingDiarySearch;
	}

	/**
	 * @param trackingDiarySearch the trackingDiarySearch to set
	 */
	public void setTrackingDiarySearch(boolean trackingDiarySearch) {
		this.trackingDiarySearch = trackingDiarySearch;
	}

	/**
	 * @return the searchCircularStatus
	 */
	public int getSearchCircularStatus() {
		return searchCircularStatus;
	}

	/**
	 * @param searchCircularStatus the searchCircularStatus to set
	 */
	public void setSearchCircularStatus(int searchCircularStatus) {
		this.searchCircularStatus = searchCircularStatus;
	}
	
	// Start - #30866
	public Long getPmParameterId() {
		return pmParameterId;
	}

	public void setPmParameterId(Long pmParameterId) {
		this.pmParameterId = pmParameterId;
	}

	public boolean isPqCircularType() {
		return isPqCircularType;
	}

	public void setPqCircularType(boolean isPqCircularType) {
		this.isPqCircularType = isPqCircularType;
	}

	public String getProcurementModeCode() {
		return procurementModeCode;
	}

	public void setProcurementModeCode(String procurementModeCode) {
		this.procurementModeCode = procurementModeCode;
	}

	public String getProcurementMode() {
		return procurementMode;
	}

	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	public IPagingDataModel<VCcDp> getCircularDpListingDataModel() {
		return circularDpListingDataModel;
	}

	public void setCircularDpListingDataModel(IPagingDataModel<VCcDp> circularDpListingDataModel) {
		this.circularDpListingDataModel = circularDpListingDataModel;
	}

	public IPagingDataModel<VCcCt> getCircularCtListingDataModel() {
		return circularCtListingDataModel;
	}

	public void setCircularCtListingDataModel(IPagingDataModel<VCcCt> circularCtListingDataModel) {
		this.circularCtListingDataModel = circularCtListingDataModel;
	}

	public IPagingDataModel<VCcQt> getCircularQtListingDataModel() {
		return circularQtListingDataModel;
	}

	public void setCircularQtListingDataModel(IPagingDataModel<VCcQt> circularQtListingDataModel) {
		this.circularQtListingDataModel = circularQtListingDataModel;
	}

	public boolean isContinuityCircularAuditSearch() {
		return isContinuityCircularAuditSearch;
	}

	public void setContinuityCircularAuditSearch(boolean isContinuityCircularAuditSearch) {
		this.isContinuityCircularAuditSearch = isContinuityCircularAuditSearch;
	}

	public String getExternalPageUrlForAudit() {
		return externalPageUrlForAudit;
	}

	public void setExternalPageUrlForAudit(String externalPageUrlForAudit) {
		this.externalPageUrlForAudit = externalPageUrlForAudit;
	}

	public String getCircularDocType() {
		return circularDocType;
	}

	public void setCircularDocType(String circularDocType) {
		this.circularDocType = circularDocType;
	}

	public CtCircularVer getCircularCqCentralContractList() {
		return circularCqCentralContractList;
	}

	public void setCircularCqCentralContractList(CtCircularVer circularCqCentralContractList) {
		this.circularCqCentralContractList = circularCqCentralContractList;
	}

	public CtCircularVer getCircularPqDirectPurchaseList() {
		return circularPqDirectPurchaseList;
	}

	public void setCircularPqDirectPurchaseList(CtCircularVer circularPqDirectPurchaseList) {
		this.circularPqDirectPurchaseList = circularPqDirectPurchaseList;
	}

	public CtCircularVer getCircularPqQtList() {
		return circularPqQtList;
	}

	public void setCircularPqQtList(CtCircularVer circularPqQtList) {
		this.circularPqQtList = circularPqQtList;
	}
	// End - #30866
}
