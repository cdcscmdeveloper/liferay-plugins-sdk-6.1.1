/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.apache.log4j.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import my.ep.factory.EpLogger;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.circular.CircularBackingBean;
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

	private  boolean readOnly = true;
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
	
	private String contractId;
	private String itemname;
	private String itemtype;
	private String itemcode;
	private String itemstatus;
	private String skuno;
	private String UOM;
	private String typeofgoods;
	private  boolean servicecollapse=false;
	private  boolean prodcollapse=false;
	private IPagingDataModel<CtContractItem> circularListingPagingDataModel = new CircularListingPagingDataModel();
	private IPagingDataModel<CtContractItem> ServiceListingPagingDataModel = new ServiceListingPagingDataModel();
	private CircularBackingBean circularBb=new CircularBackingBean();
	

	public class ServiceListingPagingDataModel extends PagingDataModel<CtContractItem> {
		
		private static final long serialVersionUID = 1L;
		
		private  Logger log = EpLogger.getLogger(ServiceListingPagingDataModel.class);
		
		public ServiceListingPagingDataModel() {
			super("itemServiceTable", isReadOnly());
		}
		
		public ServiceListingPagingDataModel(String table) {
			super(table, false);
		}
		@Override
		public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
			try {		
				
				//private String contractId = null;
				ContractBackingBean contractBb=new ContractBackingBean();
				
				Long contId=contractBb.getManagedBean().getContractVer().getCtContract().getContractId();
				Long contractVerId=contractBb.getManagedBean().getContractVer().getContractVerId();
				//contractBb.getManagedBean().setReadOnly(true);
				//log.info("**********readOnly Service 1*********"+circularBb.getManagedBean().isReadOnly());
				//log.info("**********Service collapse 1*********"+circularBb.getManagedBean().isServicecollapse());
				
			  if(contId!=null ){
					
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId", SearchOperator.OPERATOR_EQUALS.value(),contId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
			  if(contractVerId!=null ) 
				{
					
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId", SearchOperator.OPERATOR_EQUALS.value(),contractVerId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getItemname()!=null && getItemname().length()>0) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName", SearchOperator.OPERATOR_LIKE.value(),getItemname());
					searchBean.getDataFilterBeans().add(filterBean);
				}
				
				SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),ParameterConst.ITEM_TYPE_SERVICE);
				filterBean1.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean1);
				
		/*		if(getItemtype()!=null && getItemtype().length()>0 )
				{
					if(getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE))
					{
						SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),getItemtype());
						filterBean1.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean1);
					}
					if(getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE))
					{
						
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemType.parameterCode");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.ITEM_TYPE_SERVICE)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}*/
				if(getItemcode()!=null && getItemcode().length()>0 ) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",	SearchOperator.OPERATOR_EQUALS.value(),getItemcode());
					filterBean.setCaseInsensitive(true);   //scRequestItem.itemType.parameterId
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getItemstatus()!=null && getItemstatus().length()>0) {
					
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE) || getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE))
					{
						SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.recordStatus", SearchOperator.OPERATOR_LIKE.value(),getItemstatus());
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL))
					{
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("scRequestItem.recordStatusFlg");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				if(getUOM()!=null && getUOM().length()>0) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.uom.uomName", SearchOperator.OPERATOR_EQUALS.value(),getUOM());
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSkuno()!=null && getSkuno().length()>0 ) {
					SearchFilterBean filterBean = new SearchFilterBean("sku", SearchOperator.OPERATOR_EQUALS.value(),getSkuno());
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getTypeofgoods()!=null && getTypeofgoods().length()>0) {
					
					if(getTypeofgoods().equals(ParameterConst.LOCAL_GOODS_ID) || getTypeofgoods().equals(ParameterConst.IMPORTED_GOODS_ID))
					{
						SearchFilterBean filterBean = new SearchFilterBean("itemOrigin.parameterId", SearchOperator.OPERATOR_EQUALS.value(),getTypeofgoods());
						searchBean.getDataFilterBeans().add(filterBean);
					
					}
					if(getTypeofgoods().equals(ParameterConst.BOTH_GOODS_ID))
					{
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemOrigin.parameterId");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.LOCAL_GOODS_ID, ParameterConst.IMPORTED_GOODS_ID)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);

					}
				}
			
				/*String supplierNameField = null;
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
				}*/
				servicecollapse=true;
				ContractItemReqDTO reqDTO=new ContractItemReqDTO();
				reqDTO.setContractItemSearchBean(searchBean);
			

				IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
				ContractItemResDTO resDTO = null;
				resDTO=contractItemSO.retrieveProductServiceContractItemDtls(reqDTO);
			    resDTO.getContractItemResultBean();
			    circularBb.getManagedBean().setServicecollapse(true);
			    if(resDTO.getContractItemResultBean().getResultList().size()>0)
			    {
			    	//log.info("**********servicecollapse*********"+resDTO.getContractItemResultBean().getResultList().size());
					//servicecollapse=false;
					circularBb.getManagedBean().setServicecollapse(false);
					readOnly=false;
					//contractBb.getManagedBean().setReadOnly(false);contractBb.getManagedBean().isReadOnly()
				
					//log.info("**********ReadOnly Service 2*********"+circularBb.getManagedBean().isReadOnly());
			    }
			   // log.info("**********Service collapse 2*********"+circularBb.getManagedBean().isServicecollapse());
				return resDTO.getContractItemResultBean();
				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}

	}

	public class CircularListingPagingDataModel extends PagingDataModel<CtContractItem> {
		private static final long serialVersionUID = 1L;
		
		private Logger log = EpLogger.getLogger(CircularListingPagingDataModel.class);
		public CircularListingPagingDataModel() {
			super("itemTable", isReadOnly());
		}
		public CircularListingPagingDataModel(String table) {
			super("itemTable", false);
		}
		@Override
		public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
			try {		

				//private String contractId = null;
				ContractBackingBean contractBb=new ContractBackingBean();
				Long contId=contractBb.getManagedBean().getContractVer().getCtContract().getContractId();
				Long contractVerId=contractBb.getManagedBean().getContractVer().getContractVerId();
				
				//log.info("**********readOnlyProd 1*********"+contractBb.getManagedBean().isReadOnly());
				//log.info("**********Product collapse 1*********"+circularBb.getManagedBean().isProdcollapse());
				if(contId!=null ) 
				  	{
						SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId", SearchOperator.OPERATOR_EQUALS.value(),contId);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				if(contractVerId!=null ) 
				{
					
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId", SearchOperator.OPERATOR_EQUALS.value(),contractVerId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getItemname()!=null && getItemname().length()>0) {
						
						SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName", SearchOperator.OPERATOR_LIKE.value(),getItemname());
						searchBean.getDataFilterBeans().add(filterBean);
					}
				
				SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),ParameterConst.ITEM_TYPE_PRODUCT);
				filterBean1.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean1);
				
				/*if(getItemtype()!=null && getItemtype().length()>0 ) {

					if(getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT) )
					{
						SearchFilterBean filterBean = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),ParameterConst.ITEM_TYPE_PRODUCT);
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if(getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE))
					{
						
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemType.parameterCode");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.ITEM_TYPE_PRODUCT)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
						//getServiceListingPagingDataModel().reloadData();
					}
				
				}*/
				if(getItemcode()!=null && getItemcode().length()>0 ) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",	SearchOperator.OPERATOR_EQUALS.value(),getItemcode());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getItemstatus()!=null && getItemstatus().length()>0) {
					
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE) || getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE))
					{
						
						SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.recordStatus", SearchOperator.OPERATOR_LIKE.value(),getItemstatus());
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL))
					{
						
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("scRequestItem.recordStatusFlg");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				if(getUOM()!=null && getUOM().length()>0) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.uom.uomName", SearchOperator.OPERATOR_EQUALS.value(),getUOM());
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSkuno()!=null && getSkuno().length()>0 ) {
					
					SearchFilterBean filterBean = new SearchFilterBean("sku", SearchOperator.OPERATOR_EQUALS.value(),getSkuno());
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getTypeofgoods()!=null && getTypeofgoods().length()>0) {
					
					if(getTypeofgoods().equals(ParameterConst.LOCAL_GOODS_ID) || getTypeofgoods().equals(ParameterConst.IMPORTED_GOODS_ID))
					{
						
						SearchFilterBean filterBean = new SearchFilterBean("itemOrigin.parameterId", SearchOperator.OPERATOR_EQUALS.value(),getTypeofgoods());
						searchBean.getDataFilterBeans().add(filterBean);
					
					}
					if(getTypeofgoods().equals(ParameterConst.BOTH_GOODS_ID))
					{
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemOrigin.parameterId");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.LOCAL_GOODS_ID, ParameterConst.IMPORTED_GOODS_ID)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);

					}
				}
			
				/*String supplierNameField = null;
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
				}*/
				//prodcollapse=true;
				
				ContractItemReqDTO reqDTO=new ContractItemReqDTO();
				reqDTO.setContractItemSearchBean(searchBean);
			

				IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
				ContractItemResDTO resDTO = null;
				resDTO=contractItemSO.retrieveProductServiceContractItemDtls(reqDTO);
				circularBb.getManagedBean().setProdcollapse(true);
				if(resDTO.getContractItemResultBean().getResultList().size()>0)
			    {
					//log.info("**********prodcollapse*********"+resDTO.getContractItemResultBean().getResultList().size());
					circularBb.getManagedBean().setProdcollapse(false);
			    	 readOnly=false;
			    	//contractBb.getManagedBean().setReadOnly(false);
			    	
			    	//log.info("**********ReadOnly prod 2*********"+contractBb.getManagedBean().isReadOnly());
			    }
				 //log.info("**********Product collapse 2*********"+circularBb.getManagedBean().isProdcollapse());
				
				return resDTO.getContractItemResultBean();
				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}

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
	public IPagingDataModel<CtContractItem> getCircularListingPagingDataModel() {
		return circularListingPagingDataModel;
	}

	/**
	 * @param circularListingPagingDataModel the circularListingPagingDataModel to set
	 */
	public void setCircularListingPagingDataModel(
			IPagingDataModel<CtContractItem> circularListingPagingDataModel) {
		this.circularListingPagingDataModel = circularListingPagingDataModel;
	}

	/**
	 * @param circularListingPagingDataModel the circularListingPagingDataModel to reset
	 */
	public void resetCircularListingPagingDataModel() {
		this.circularListingPagingDataModel = new CircularListingPagingDataModel();
	}

	/**
	 * @param serviceListingPagingDataModel the serviceListingPagingDataModel to reset
	 */
	public void resetServiceListingPagingDataModel() {
		this.ServiceListingPagingDataModel = new ServiceListingPagingDataModel();
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


	/**
	 * @return the Item name from Item details screen
	 */
	public String getItemname() {
		return itemname;
	}

	/**
	 * @param the Item name to set in Item details screen 
	 */
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	

	/**
	 * @return the Item type from Item details screen
	 */
	
	public String getItemtype() {
		return itemtype;
	}
	/**
	 * @param the Item type to set in Item details screen 
	 */
	
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	/**
	 * @return the Item code from Item details screen
	 */
	public String getItemcode() {
		return itemcode;
	}
	/**
	 * @param the Item code to set in Item details screen 
	 */
	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}

	/**
	 * @return the Item status from Item details screen
	 */
	public String getItemstatus() {
		return itemstatus;
	}
	/**
	 * @param the Item status to set in Item details screen 
	 */
	public void setItemstatus(String itemstatus) {
		this.itemstatus = itemstatus;
	}

	/**
	 * @return the SKU NO from Item details screen
	 */
	public String getSkuno() {
		return skuno;
	}
	/**
	 * @param the SKU NO to set in Item details screen 
	 */
	public void setSkuno(String skuno) {
		this.skuno = skuno;
	}

	/**
	 * @return the UOM from Item details screen
	 */
	public String getUOM() {
		return UOM;
	}
	/**
	 * @param the UOM to set in Item details screen 
	 */
	public void setUOM(String uOM) {
		UOM = uOM;
	}

	/**
	 * @return the typeofgoods from Item details screen
	 */
	public String getTypeofgoods() {
		return typeofgoods;
	}

	/**
	 * @param the typeofgoods to set in Item details screen 
	 */
	public void setTypeofgoods(String typeofgoods) {
		this.typeofgoods = typeofgoods;
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
	

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public IPagingDataModel<CtContractItem> getServiceListingPagingDataModel() {
		return ServiceListingPagingDataModel;
	}

	public void setServiceListingPagingDataModel(IPagingDataModel<CtContractItem> serviceListingPagingDataModel) {
		ServiceListingPagingDataModel = serviceListingPagingDataModel;
	}

	public boolean isServicecollapse() {
		return servicecollapse;
	}

	public void setServicecollapse(boolean servicecollapse) {
		this.servicecollapse = servicecollapse;
	}

	public boolean isProdcollapse() {
		return prodcollapse;
	}

	public void setProdcollapse(boolean prodcollapse) {
		this.prodcollapse = prodcollapse;
	}




	
	
}
