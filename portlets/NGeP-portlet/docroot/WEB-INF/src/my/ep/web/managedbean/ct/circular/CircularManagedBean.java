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
	private IPagingDataModel<CtCircularVer> circularListingPagingDataModel = new CircularListingPagingDataModel();
	
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
	
	public String getSearchCircularNo() {
		return searchCircularNo;
	}
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}
	public String getSearchCircularName() {
		return searchCircularName;
	}
	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}
	public String getSearchPhysicalCircularNo() {
		return searchPhysicalCircularNo;
	}
	public void setSearchPhysicalCircularNo(String searchPhysicalCircularNo) {
		this.searchPhysicalCircularNo = searchPhysicalCircularNo;
	}
	public String getSearchCircularDocType() {
		return searchCircularDocType;
	}
	public void setSearchCircularDocType(String searchCircularDocType) {
		this.searchCircularDocType = searchCircularDocType;
	}
	public String getSearchStatus() {
		return searchStatus;
	}
	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}
	public IPagingDataModel<CtCircularVer> getCircularListingPagingDataModel() {
		return circularListingPagingDataModel;
	}
	public void setCircularListingPagingDataModel(
			IPagingDataModel<CtCircularVer> circularListingPagingDataModel) {
		this.circularListingPagingDataModel = circularListingPagingDataModel;
	}
	public void resetCircularListingPagingDataModel() {
		this.circularListingPagingDataModel = new CircularListingPagingDataModel();
	}
	public String getSearchSupplierName() {
		return searchSupplierName;
	}
	public void setSearchSupplierName(String searchSupplierName) {
		this.searchSupplierName = searchSupplierName;
	}
	public String getSearchItemName() {
		return searchItemName;
	}
	public void setSearchItemName(String searchItemName) {
		this.searchItemName = searchItemName;
	}
	public Date getSearchStartDate() {
		return searchStartDate;
	}
	public void setSearchStartDate(Date searchStartDate) {
		this.searchStartDate = searchStartDate;
	}
	public Date getSearchEndDate() {
		return searchEndDate;
	}
	public void setSearchEndDate(Date searchEndDate) {
		this.searchEndDate = searchEndDate;
	}
	public Long getSearchStatusId() {
		return searchStatusId;
	}
	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isTrackingDiarySearch() {
		return trackingDiarySearch;
	}
	public void setTrackingDiarySearch(boolean trackingDiarySearch) {
		this.trackingDiarySearch = trackingDiarySearch;
	}
	
}
