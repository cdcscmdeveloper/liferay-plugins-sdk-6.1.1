/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.suspension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

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
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sc.common.ScRequestItemSpecOffer;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.ct.suspension.SuspensionManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Managed Bean For Contract Suspension
 * 
 * @author Alam
 * @since 1/13/2014	 
 */

@ManagedBean(name ="suspensionManagedBean")
@SessionScoped
public class SuspensionManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private  boolean readOnly=true;
	private String taskName;
	private String fileDirectory;
	private String textBeforeSign;
	private String buttonAction;
	private String searchItemString;
	private String contractId;
	private String itemtype;
	private String itemcode;
	private String itemstatus;
	private String skuno;
	private String UOM;
	private String typeofgoods;

	private boolean autoUpdate = true;
	private boolean viewOnly = false;

	private PmDigiSign pmDigiSign;
	private CtContract ctContract;
	private CtContractVer contractVer;
	private CtContractItem selectedItemForSpec;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtContractItem> contractItemList;
	private HashMap<Long, CtAgency> agencyMap;
	private HashMap<Long, CtContractItem> itemMap;	
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUser> userMap;
	
	//#28854 - start
	private List<String> itemSpecification;
	private String newOfferSpec;
	private String supplierOffer;
	private List<ScRequestItemSpecOffer> scRequestItemSpecOfferList;
	//#28854 - end	

private IPagingDataModel<CtContractItem> SuspensionProductListingPagingDataModel = new SuspensionProductListingPagingDataModel();

     
	 private class SuspensionProductListingPagingDataModel extends PagingDataModel<CtContractItem> {
		
		private static final long serialVersionUID = 1L;
		
		private  Logger log = EpLogger.getLogger(SuspensionProductListingPagingDataModel.class);
		public SuspensionProductListingPagingDataModel() {
			super("suspensionProdDataModel", isReadOnly());
		}
      
		@Override
		public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
			try {
				
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
				
			  if(contId!=null ) {
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId", SearchOperator.OPERATOR_EQUALS.value(),contId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
			  if(contractVerId!=null ) 
				{					
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId", SearchOperator.OPERATOR_EQUALS.value(),contractVerId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				
				if(getSearchItemString()!=null && getSearchItemString().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName", SearchOperator.OPERATOR_LIKE.value(),getSearchItemString().toLowerCase(),true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getItemtype()!=null && getItemtype().length()>0 )
				{					
					if(getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT) || getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE))
					{
						SearchFilterBean filterBean = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),getItemtype());
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if(getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE))
					{
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemType.parameterCode");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.ITEM_TYPE_SERVICE,ParameterConst.ITEM_TYPE_PRODUCT)));
						//filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				if(getItemcode()!=null && getItemcode().length()>0 ) {
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",	SearchOperator.OPERATOR_EQUALS.value(),getItemcode());
					filterBean.setCaseInsensitive(true);   
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getItemstatus() != null && getItemstatus().length() > 0) {
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE) || getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE)) {
						SearchFilterBean filterBean = new SearchFilterBean("recordStatus", SearchOperator.OPERATOR_LIKE.value(), getItemstatus());//bug #31042
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL)) {
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("recordStatusFlg");//bug #31042
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList( ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
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
				
				ContractItemReqDTO reqDTO=new ContractItemReqDTO();
				reqDTO.setContractItemSearchBean(searchBean);			

				IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
				ContractItemResDTO resDTO = null;
				resDTO=contractItemSO.retrieveProductServiceContractItemDtls(reqDTO);
				return resDTO.getContractItemResultBean();
				//return null;
				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName, the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the ctContract
	 */
	public CtContract getCtContract() {
		return ctContract;
	}

	/**
	 * @param ctContract the ctContract to set
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	/**
	 * @return the contractVer
	 */
	public CtContractVer getContractVer() {
		return contractVer;
	}

	/**
	 * @param contractVer the contractVer to set
	 */
	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}

	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * @return the attachmentList
	 */
	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null)
			attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the contractItemList
	 */
	public List<CtContractItem> getContractItemList() {
		if(contractItemList == null)
			contractItemList = new ArrayList<CtContractItem>();
		return contractItemList;
	}

	/**
	 * @param contractItemList the contractItemList to set
	 */
	public void setContractItemList(List<CtContractItem> contractItemList) {
		this.contractItemList = contractItemList;
	}

	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}

	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	/**
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * @return the viewOnly
	 */
	public boolean isViewOnly() {
		return viewOnly;
	}

	/**
	 * @param viewOnly the viewOnly to set
	 */
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}

	/**
	 * @return the agencyMap
	 */
	public HashMap<Long, CtAgency> getAgencyMap() {
		if(agencyMap == null) {
			setAgencyMap(new HashMap<Long, CtAgency>());
		}
		return agencyMap;
	}

	/**
	 * @param agencyMap the agencyMap to set
	 */
	public void setAgencyMap(HashMap<Long, CtAgency> agencyMap) {
		this.agencyMap = agencyMap;
	}

	/**
	 * @return the itemMap
	 */
	public HashMap<Long, CtContractItem> getItemMap() {
		if(itemMap == null) {
			setItemMap(new HashMap<Long, CtContractItem>());
		}
		return itemMap;
	}

	/**
	 * @param itemMap the itemMap to set
	 */
	public void setItemMap(HashMap<Long, CtContractItem> itemMap) {
		this.itemMap = itemMap;
	}

	/**
	 * @return the approverModel
	 */
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
	}

	/**
	 * @return the userMap
	 */
	public Map<Long, PmUser> getUserMap() {
		if(userMap==null) {
			userMap = new HashMap<Long, PmUser>();
		}
		return userMap;
	}

	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<Long, PmUser> userMap) {
		this.userMap = userMap;
	}

	/**
	 * @return the selectedItemForSpec
	 */
	public CtContractItem getSelectedItemForSpec() {
		return selectedItemForSpec;
	}

	/**
	 * @param selectedItemForSpec the selectedItemForSpec to set
	 */
	public void setSelectedItemForSpec(CtContractItem selectedItemForSpec) {
		this.selectedItemForSpec = selectedItemForSpec;
	}
	
	/**
	 * @return the searchItemString
	 */
	public String getSearchItemString() {
		return searchItemString;
	}

	/**
	 * @param searchItemString the searchItemString to set
	 */
	public void setSearchItemString(String searchItemString) {
		this.searchItemString = searchItemString;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getItemcode() {
		return itemcode;
	}

	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}

	public String getItemstatus() {
		return itemstatus;
	}

	public void setItemstatus(String itemstatus) {
		this.itemstatus = itemstatus;
	}

	public String getSkuno() {
		return skuno;
	}

	public void setSkuno(String skuno) {
		this.skuno = skuno;
	}

	public String getUOM() {
		return UOM;
	}

	public void setUOM(String uOM) {
		UOM = uOM;
	}

	public String getTypeofgoods() {
		return typeofgoods;
	}
	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setTypeofgoods(String typeofgoods) {
		this.typeofgoods = typeofgoods;
	}

	public IPagingDataModel<CtContractItem> getSuspensionProductListingPagingDataModel() {
		return SuspensionProductListingPagingDataModel;
	}

	public void setSuspensionProductListingPagingDataModel(
			IPagingDataModel<CtContractItem> suspensionProductListingPagingDataModel) {
		SuspensionProductListingPagingDataModel = suspensionProductListingPagingDataModel;
	}
	
	/**
	 * @param the SuspensionProductListingPagingDataModel to reset
	 */
	public SuspensionProductListingPagingDataModel  resetSuspensionProductListingPagingDataModel() {
		return new SuspensionProductListingPagingDataModel();
	}
	
	/* #28854 start*/
	public String getNewOfferSpec() {
		return newOfferSpec;
	}

	public void setNewOfferSpec(String newOfferSpec) {
		this.newOfferSpec = newOfferSpec;
	}

	public String getSupplierOffer() {
		return supplierOffer;
	}

	public void setSupplierOffer(String supplierOffer) {
		this.supplierOffer = supplierOffer;
	}	

	public List<String> getItemSpecification() {
		return itemSpecification;
	}

	public void setItemSpecification(List<String> itemSpecification) {
		this.itemSpecification = itemSpecification;
	}

	public List<ScRequestItemSpecOffer> getScRequestItemSpecOfferList() {
		return scRequestItemSpecOfferList;
	}
	
	public void setScRequestItemSpecOfferList(List<ScRequestItemSpecOffer> scRequestItemSpecOfferList) {
		this.scRequestItemSpecOfferList = scRequestItemSpecOfferList;
	}
	/* #28854 end*/	
}
