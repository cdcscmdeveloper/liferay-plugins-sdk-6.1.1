package my.ep.web.managedbean.ct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;

import sun.util.logging.resources.logging;



/**
 *  Contract Item Search Managed Bean
 * 
 * @author Venkat
 * @since 04/7/2019
 */
@ManagedBean
@SessionScoped
public class ContractItemSearchManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	private boolean resetval=true;
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
	private IPagingDataModel<CtContractItem> contractListingPagingDataModel = new ContractListingPagingDataModel();
	private IPagingDataModel<CtContractItem> ServiceListingPagingDataModel =  new ServiceListingPagingDataModel();
	private boolean itemProductExist;
	private boolean itemServiceExist;
	private boolean itemProductNotExist;
	private boolean itemServiceNotExist;
	private PmUom selectedSearchItem;

	
	
	public void reassignValue(boolean value){
		readOnly = value;	
		resetval= value;
		servicecollapse=value;
		prodcollapse=value;

	}
	public void prodValue(boolean value){
		servicecollapse=false;
		prodcollapse=value;
		
	}
	public void serviceValue(boolean value){
		servicecollapse=value;
		prodcollapse=false;
		
	}
	public void allValue(boolean value){
		servicecollapse=value;
		prodcollapse=value;
		
	}
	
	private class ServiceListingPagingDataModel extends PagingDataModel<CtContractItem> {
		
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
				//log.info("*****ServiceListingPagingDataModel******"+"servicecollapse--->"+servicecollapse+"prodcollapse"+prodcollapse);
				//log.info("*****ServiceListingPagingDataModel******"+"isServicecollapse--->"+isServicecollapse()+"isProdcollapse"+isProdcollapse());
				//private String contractId = null;
				IContractSO	contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO ctReqDTO  = new ContractReqDTO();	
				
				ContractBackingBean contractBb=new ContractBackingBean();
				
				Long contId=contractBb.getManagedBean().getContractVer().getCtContract().getContractId();
				Long contractVerId=null;
				contractVerId = contractBb.getManagedBean().getContractVer().getContractVerId();				
				log.info("**SuspensionProductListingPagingDataModel******ContractID **--> : "+contId);
				log.info("**SuspensionProductListingPagingDataModel******ContractVerID **--> : "+contractVerId);
				
				if(contractVerId==null){
					ctReqDTO.setContractId(contId);
					 contractVerId = contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId();					
				}
//				if(contractBb.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId()
//						.equals(contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId())){
//							contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId();	
//						}else {
//							contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId();	
//						}
				
			  if(contId!=null ){
					
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId", SearchOperator.OPERATOR_EQUALS.value(),contId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
			  if(contractVerId!=null ){
					
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId", SearchOperator.OPERATOR_EQUALS.value(),contractVerId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getItemname()!=null && getItemname().length()>0) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName", SearchOperator.OPERATOR_LIKE.value(),getItemname());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				
				SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),ParameterConst.ITEM_TYPE_SERVICE);
				filterBean1.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean1);
				
				if(getItemcode()!=null && getItemcode().length()>0 ) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",	SearchOperator.OPERATOR_LIKE.value(),getItemcode());
					filterBean.setCaseInsensitive(true);   //scRequestItem.itemType.parameterId
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getItemstatus()!=null && getItemstatus().length()>0) {
					
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE) || getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE)){
						SearchFilterBean filterBean = new SearchFilterBean("recordStatus", SearchOperator.OPERATOR_LIKE.value(),getItemstatus()); // # Fix - #31042
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL)){
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("recordStatusFlg"); // # Fix - #31042
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				//#20997 - Start-Fixing
				if(getSelectedSearchItem().getUomName()!=null && getSelectedSearchItem().getUomName().trim().length()>0) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.uom.uomName", SearchOperator.OPERATOR_LIKE.value(),getSelectedSearchItem().getUomName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				//#20997 - End-Fixing
				if(getSkuno()!=null && getSkuno().length()>0 ) {
					SearchFilterBean filterBean = new SearchFilterBean("sku", SearchOperator.OPERATOR_LIKE.value(),getSkuno());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getTypeofgoods()!=null && getTypeofgoods().length()>0) {
					
					if(getTypeofgoods().equals(ParameterConst.LOCAL_GOODS_ID) || getTypeofgoods().equals(ParameterConst.IMPORTED_GOODS_ID))
					{
						SearchFilterBean filterBean = new SearchFilterBean("itemOrigin.parameterId", SearchOperator.OPERATOR_EQUALS.value(),getTypeofgoods());
						searchBean.getDataFilterBeans().add(filterBean);
					
					}
					if(getTypeofgoods().equals(ParameterConst.BOTH_GOODS_ID)){
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemOrigin.parameterId");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.LOCAL_GOODS_ID, ParameterConst.IMPORTED_GOODS_ID)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);

					}
				}
			
				
				//servicecollapse=true;
				ContractItemReqDTO reqDTO=new ContractItemReqDTO();
				reqDTO.setContractItemSearchBean(searchBean);
				ContractItemResDTO resDTO = null;
				//CircularBackingBean cbb=new CircularBackingBean();
				//log.info("*****ServiceListingPagingDataModel Resetval()******"+isResetval());
				//log.info("*****ServiceListingPagingDataModel Resetval()******"+cbb.getManagedBean().isResetval());
				if(isResetval()){
					//log.info("*****ServiceListingPagingDataModel ParameterCode *** "+contractBb.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode());
					if(contractBb.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_CENTRAL)){
						if(getItemtype()!=null && getItemtype().length()>0){
	                		if(getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE)){
								setServicecollapse(isResetval());
		                		//log.info("*****ServiceListingPagingDataModel *** isServicecollapse()******"+isServicecollapse());
	                		}
                		}else{
                			setServicecollapse(isResetval());
                		}
                	}
					
					if(isServicecollapse()){
						IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
						
						
						resDTO=contractItemSO.searchProductServiceItemDtls(reqDTO);
					    resDTO.getContractItemResultBean();
					    //contractBb.getManagedBean().setServicecollapse(true);
					    //log.info("*****ServiceListingPagingDataModel getContractItemResultBean().getResultList().size()******"+resDTO.getContractItemResultBean().getResultList().size());
					    if(resDTO.getContractItemResultBean().getResultList().size()>0){
							//servicecollapse=false;
					    	//contractBb.getManagedBean().setServicecollapse(false);
							readOnly=false;
							//contractBb.getManagedBean().setReadOnly(false);contractBb.getManagedBean().isReadOnly()

					    }
					  
						return resDTO.getContractItemResultBean();
	                }
                }
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}

	}

	
	private class ContractListingPagingDataModel extends PagingDataModel<CtContractItem> {
		private static final long serialVersionUID = 1L;
		
		private Logger log = EpLogger.getLogger(ContractListingPagingDataModel.class);
		public ContractListingPagingDataModel() {
			super("itemTable", isReadOnly());
		}
		public ContractListingPagingDataModel(String table) {
			super("itemTable", false);
		}
		@Override
		public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
			try {		
				//log.info("*****ContractListingPagingDataModel******"+"servicecollapse--->"+servicecollapse+"prodcollapse"+prodcollapse);
				//log.info("*****ServiceListingPagingDataModel******"+"isServicecollapse--->" +isServicecollapse()+"isProdcollapse" +isProdcollapse());
				//private String contractId = null;
				IContractSO	contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO ctReqDTO  = new ContractReqDTO();	
				
				ContractBackingBean contractBb=new ContractBackingBean();
				
				Long contId=contractBb.getManagedBean().getContractVer().getCtContract().getContractId();
				Long contractVerId=null;
				contractVerId = contractBb.getManagedBean().getContractVer().getContractVerId();				
				
				if(contractVerId==null){
					ctReqDTO.setContractId(contId);
					 contractVerId = contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId();					
				}
				if(contId!=null ){
						SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId", SearchOperator.OPERATOR_EQUALS.value(),contId);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				if(contractVerId!=null ){
					
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId", SearchOperator.OPERATOR_EQUALS.value(),contractVerId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getItemname()!=null && getItemname().length()>0) {
						
						SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName", SearchOperator.OPERATOR_LIKE.value(),getItemname());
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				
				 SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),ParameterConst.ITEM_TYPE_PRODUCT);
				 filterBean1.setCaseInsensitive(true);
				 searchBean.getDataFilterBeans().add(filterBean1);

				if(getItemcode()!=null && getItemcode().length()>0 ) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",	SearchOperator.OPERATOR_LIKE.value(),getItemcode());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getItemstatus()!=null && getItemstatus().length()>0){
					
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE) || getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE)){
						
						SearchFilterBean filterBean = new SearchFilterBean("recordStatus", SearchOperator.OPERATOR_LIKE.value(),getItemstatus()); // Fix - #31042
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL)){
						
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("recordStatusFlg"); // Fix - #31042
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				//#20997 - Start-Fixing
				if(getSelectedSearchItem().getUomName()!=null && getSelectedSearchItem().getUomName().trim().length()>0) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.uom.uomName", SearchOperator.OPERATOR_LIKE.value(),getSelectedSearchItem().getUomName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				//#20997 - End-Fixing
				
				if(getSkuno()!=null && getSkuno().length()>0 ) {
					
					SearchFilterBean filterBean = new SearchFilterBean("sku", SearchOperator.OPERATOR_LIKE.value(),getSkuno());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getTypeofgoods()!=null && getTypeofgoods().length()>0) {
					
					if(getTypeofgoods().equals(ParameterConst.LOCAL_GOODS_ID) || getTypeofgoods().equals(ParameterConst.IMPORTED_GOODS_ID)){
						
						SearchFilterBean filterBean = new SearchFilterBean("itemOrigin.parameterId", SearchOperator.OPERATOR_EQUALS.value(),getTypeofgoods());
						searchBean.getDataFilterBeans().add(filterBean);
					
					}
					if(getTypeofgoods().equals(ParameterConst.BOTH_GOODS_ID)){
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemOrigin.parameterId");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.LOCAL_GOODS_ID, ParameterConst.IMPORTED_GOODS_ID)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);

					}
				}
			

				//prodcollapse=true;
				
				ContractItemReqDTO reqDTO=new ContractItemReqDTO();
				reqDTO.setContractItemSearchBean(searchBean);
				ContractItemResDTO resDTO = null;
				//CircularBackingBean cbb=new CircularBackingBean();
				//setResetval(isProdcollapse());
				
				//log.info("*****ContractListingPagingDataModel Resetval()******"+isResetval());
				//log.info("*****ContractListingPagingDataModel Resetval()******"+cbb.getManagedBean().isResetval());
                if(isResetval()){
                	log.info("*****ContractListingPagingDataModel ParameterCode *** "+contractBb.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode());
                	if(contractBb.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_CENTRAL)){
                		if(getItemtype()!=null && getItemtype().length()>0){
	                		if(getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT)){
		                		setProdcollapse(isResetval());
		                		//log.info("*****ContractListingPagingDataModel ***Prodcollapse()******"+isProdcollapse());
	                		}	
                		}else{
                			setProdcollapse(isResetval());
                		}
                	}
                	
                	if(isProdcollapse()){
    					
    					IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
    					
					resDTO=contractItemSO.searchProductServiceItemDtls(reqDTO);
					//contractBb.getManagedBean().setProdcollapse(true);
					//log.info("*****ContractListingPagingDataModel getContractItemResultBean().getResultList().size()******"+resDTO.getContractItemResultBean().getResultList().size());
					if(resDTO.getContractItemResultBean().getResultList().size()>0){
						//contractBb.getManagedBean().setProdcollapse(false);
				    	 readOnly=false;
				    }
					return resDTO.getContractItemResultBean();
				}
              }
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
	public IPagingDataModel<CtContractItem> getContractListingPagingDataModel() {
		return contractListingPagingDataModel;
	}

	/**
	 * @param circularListingPagingDataModel the circularListingPagingDataModel to set
	 */
	public void setContractListingPagingDataModel(
			IPagingDataModel<CtContractItem> circularListingPagingDataModel) {
		this.contractListingPagingDataModel = circularListingPagingDataModel;
	}

	/**
	 * @param circularListingPagingDataModel the circularListingPagingDataModel to reset
	 */
	public void resetContractListingPagingDataModel() {
		this.contractListingPagingDataModel = new ContractListingPagingDataModel();
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


	public boolean isResetval() {
		return resetval;
	}

	public void setResetval(boolean resetval) {
		this.resetval = resetval;
	}
	
	/**
	 * @return the itemProductExist
	 */
	public boolean isItemProductExist() {
		return itemProductExist;
	}

	/**
	 * @param itemProductExist the itemProductExist to set
	 */
	public void setItemProductExist(boolean itemProductExist) {
		this.itemProductExist = itemProductExist;
	}
	
	/**
	 * @return the itemServiceExist
	 */
	public boolean isItemServiceExist() {
		return itemServiceExist;
	}

	/**
	 * @param itemServiceExist the itemServiceExist to set
	 */
	public void setItemServiceExist(boolean itemServiceExist) {
		this.itemServiceExist = itemServiceExist;
	}
	public boolean isItemProductNotExist() {
		return itemProductNotExist;
	}
	public void setItemProductNotExist(boolean itemProductNotExist) {
		this.itemProductNotExist = itemProductNotExist;
	}
	public boolean isItemServiceNotExist() {
		return itemServiceNotExist;
	}
	public void setItemServiceNotExist(boolean itemServiceNotExist) {
		this.itemServiceNotExist = itemServiceNotExist;
	}
	
	//#20997 - Start
	// For Searching Autocomplete type UOM
	public PmUom getSelectedSearchItem() {
		if (selectedSearchItem == null)
			selectedSearchItem = new PmUom();
		return selectedSearchItem;
	}
	public void setSelectedSearchItem(PmUom selectedSearchItem) {
		this.selectedSearchItem = selectedSearchItem;
	}
	//#20997 - End
	
}
