package my.ep.web.backingbean.ct;


import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import my.ep.web.managedbean.ct.ContractItemSearchManagedBean;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.circular.CircularBackingBean;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.factory.EpLogger;
/**
 * Backing Bean for Circular
 * 
 * @author Venkat
 * @since 04/7/2014
 */
@ManagedBean( name="contractItemSearchBackingBean")
@RequestScoped 
public class ContractItemSearchBackingBean extends BaseBackingBean<ContractItemSearchManagedBean>{
	
  private static final long serialVersionUID = 1L;
  private static Logger log = EpLogger.getLogger(CircularBackingBean.class);
  
  
  /**
	 * The method used to reset the search criteria for circular.
	 */
	public void searchReset(){
		
		ContractBackingBean ccBB = new ContractBackingBean();
		
		//getManagedBean().setSearchCircularDocType(null);
		//getManagedBean().setSearchPhysicalCircularNo(null);
	    //getManagedBean().setSearchCircularNo(null);
		//getManagedBean().setSearchCircularName(null);
		//getManagedBean().setSearchSupplierName(null);g
		//getManagedBean().setSearchItemName(null);
		//getManagedBean().setSearchStartDate(null);
		//getManagedBean().setSearchEndDate(null);
		//getManagedBean().setSearchStatusId(null);
		//getManagedBean().setSearchStatus(null);
		//getManagedBean().setSearchCircularStatus(0);
		
		getManagedBean().setItemcode(null);
		getManagedBean().setItemname(null);
		getManagedBean().setItemstatus(null);
		getManagedBean().setItemtype(null);
		getManagedBean().setUOM(null);
		getManagedBean().setSkuno(null);
		getManagedBean().setTypeofgoods(null);
		//getManagedBean().setServiceListingPagingDataModel(null);
		//getManagedBean().setCircularListingPagingDataModel(null);
		//ccBB.getManagedBean().setShowItem(null);
		//ccBB.getManagedBean().setShowServiceItem(null);
		//ccBB.getManagedBean().setServiceselectedItem(null);
		ccBB.getManagedBean().setSelectedItem(null);
		ccBB.getManagedBean().setServiceselectedItem(null);
		// Start Fix - #31042		
		getManagedBean().getContractListingPagingDataModel().resetData();
		getManagedBean().getServiceListingPagingDataModel().resetData();     
		
		if(ccBB.getManagedBean().getCtItemList().size() > 0 || !ccBB.getManagedBean().getCtItemList().isEmpty()){
			getManagedBean().setItemProductExist(true);
		}else{
			getManagedBean().setItemProductExist(false);
			getManagedBean().setItemProductNotExist(false);
		}
		
		if(ccBB.getManagedBean().getCtItemServiceList().size() > 0 || !ccBB.getManagedBean().getCtItemServiceList().isEmpty()){
			getManagedBean().setItemServiceExist(true);
		}else{
			getManagedBean().setItemServiceExist(false);
			getManagedBean().setItemServiceNotExist(false);
		} 
		// End Fix - #31042
	}

	
	/**
	 * The method used for searching the circular list.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchCircular() throws Exception {
		try {				 
					if( getManagedBean().getItemcode().equals("") && 
							getManagedBean().getItemname().equals("") &&
							getManagedBean().getItemstatus() == null &&
							getManagedBean().getItemtype()== null &&
							getManagedBean().getSkuno().equals("") &&
							getManagedBean().getTypeofgoods()== null  )
				{
					
					throw new EpException(MessageConst.ERROR_750049);
					
				}else if(getManagedBean().getItemtype()!=null)
				{
					getManagedBean().setResetval(true);
					ContractBackingBean ccBB = new ContractBackingBean();
					ccBB.getManagedBean().setSelectedItem(null);
					ccBB.getManagedBean().setServiceselectedItem(null);
					if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT)){
						getManagedBean().getContractListingPagingDataModel().reloadData();

							getManagedBean().setItemProductExist(true);
							getManagedBean().setItemServiceExist(false);
							
					}else if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE)){

						  getManagedBean().getServiceListingPagingDataModel().reloadData();

								getManagedBean().setItemServiceExist(true);

							getManagedBean().setItemProductExist(false); 
					}else if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE))
					{
						  getManagedBean().getContractListingPagingDataModel().reloadData();
						  getManagedBean().getServiceListingPagingDataModel().reloadData(); 
						  
							if(ccBB.getManagedBean().getCtItemList().size() > 0 || !ccBB.getManagedBean().getCtItemList().isEmpty()){
								getManagedBean().setItemProductExist(true);
							}else{
								getManagedBean().setItemProductExist(false);
								getManagedBean().setItemProductNotExist(false);
							}
							
							if(ccBB.getManagedBean().getCtItemServiceList().size() > 0 || !ccBB.getManagedBean().getCtItemServiceList().isEmpty()){
								getManagedBean().setItemServiceExist(true);
							}else{
								getManagedBean().setItemServiceExist(false);
								getManagedBean().setItemServiceNotExist(false);
							} 
					}
					}// Start Fix - #31042
				 else if(getManagedBean().getItemtype() == null){
					
					  getManagedBean().getContractListingPagingDataModel().reloadData();
					  getManagedBean().getServiceListingPagingDataModel().reloadData();
					  
				}
				    // End Fix - #31042
					
			//System.out.println("*****************searchCircular************-->5"+getManagedBean().get);
			/*if (getManagedBean().getSearchCircularDocType() == null) {
				
		
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_100022,
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.circular.type", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())},
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			else {
				
				getManagedBean().getCircularListingPagingDataModel().reloadData();
			}*/
		} finally{
			    // Do nothing
			}
	}

	/** to concat Itemcode and Item Name */
	//#31558 start
	public ScRequestItem contactItem(CtContractItem ctItem) {
		
		String itemcode=ctItem.getScRequestItem().getCmItem().getExtensionCode();
		String itemName=ctItem.getScRequestItem().getItemName();
		if(itemcode==null)
		{
			itemcode=ParameterConst.ITEM_CODE;
		}
		if(itemName==null)
		{
			itemName=ParameterConst.ITEM_NAME;
		}
		
		ScRequestItem scRequestItem = new ScRequestItem();
		scRequestItem.setItemName(itemName);
		scRequestItem.setCmItem(ctItem.getScRequestItem().getCmItem());
		scRequestItem.getCmItem().setExtensionCode(itemcode);

		/*result.append(itemcode);
		result.append(",");
		result.append(itemName);
		*/
		
		return scRequestItem;
	     
	}
	//#31558 end
	
	public String contactItemProd(CtContractItem ctItem) {
		//String result=new String();
		
		String itemcode=ctItem.getScRequestItem().getCmItem().getExtensionCode();
		String itemName=ctItem.getScRequestItem().getItemName();
		String sKuNo=ctItem.getSku();
		if(itemcode==null)
		{
			itemcode=ParameterConst.ITEM_CODE;
		}
		if(itemName==null)
		{
			itemName=ParameterConst.ITEM_NAME;
		}
		if(sKuNo==null)
		{
			sKuNo=ParameterConst.SKUNO;
		}
		String result = itemName +","+ itemcode+","+sKuNo;

		/*result.append(itemcode);
		result.append(",");
		result.append(itemName);
		*/
	
		return result;
	     
	}
	
	/**
	* Gets the local goods radio values.
	* 
	* @param itemType
	* the item type
	* @return the local goods radio values
	*/
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {
	List<String> types = new ArrayList<String>();
	types.add(ParameterConst.LOCAL_GOODS);
	types.add(ParameterConst.IMPORTED_GOODS);
	SelectItem[] items = null;
	try {
	items = new SelectItem[types.size()];
	int i = 0;
	for (String location : types) {
	List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);
	for (PmParameterDesc paramDesc : localParamDesc) {
	if (paramDesc.getPmParameter().getParameterCode()
	.equals(location)) {
	items[i++] = new SelectItem(paramDesc.getPmParameter()
	.getParameterId(), paramDesc.getCodeName());
	}
	}
	}
	} finally {

	}
	return items;
	}
	
	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            (String type)
	 * @returnList<PmParameterDesc>
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

}
