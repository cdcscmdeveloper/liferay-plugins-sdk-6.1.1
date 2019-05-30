/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import my.ep.web.managedbean.ct.circular.CircularManagedBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.backingbean.ct.circular.panel.PanelBackingBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.factory.EpLogger;
/**
 * Backing Bean for Circular
 * 
 * @author kieyun
 * @since 23/12/2014
 */
@ManagedBean( name="circularBackingBean")
@RequestScoped 
public class CircularBackingBean extends BaseBackingBean<CircularManagedBean>{
	
	private static final long serialVersionUID = 1L;
	
	private static Logger log = EpLogger.getLogger(CircularBackingBean.class);

	/**
	 * The method used for searching the circular list.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchCircularListing() throws Exception {
		if ((getManagedBean().getSearchCircularNo() == null || getManagedBean().getSearchCircularNo().length() == 0) && 
				(getManagedBean().getSearchCircularName() == null || getManagedBean().getSearchCircularName().length() == 0) &&
				(getManagedBean().getSearchPhysicalCircularNo() == null || getManagedBean().getSearchPhysicalCircularNo().length() == 0) && 
				(getManagedBean().getSearchCircularDocType() == null) && (getManagedBean().getSearchStatus() == null)) {
			throw new EpException(MessageConst.ERROR_100060);
		}
		else {
			getManagedBean().getCircularListingPagingDataModel().reloadData();
		}		
	}

	/**
	 * The method used for generating a list of available circular document
	 * type.
	 * 
	 * @return the selection list of circular document type
	 */
	public SelectItem[] getCircularDocTypeValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> circularDocTypes = new ArrayList<String>();
		if(isAuditor() || isCentralContractAdmin()) {
			circularDocTypes.add(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
		}
		if(isAuditor() || isPanelAdmin()) {
			circularDocTypes.add(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		}
		if(!isAuditor() && !isPanelAdmin() && !isCentralContractAdmin()) { // public view
			circularDocTypes.add(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
			circularDocTypes.add(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		}
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterType", SearchOperator.OPERATOR_EQUALS.value(), ParameterTypeConst.DOCUMENT_TYPES));
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), circularDocTypes));
		return getDropdownCodeValuesBySearchBean(ParameterTypeConst.DOCUMENT_TYPES, searchBean);
	}

	/**
	 * The method used for generating a list of available circular status.
	 * 
	 * @return the selection list of circular status
	 */
	public SelectItem[] getCircularStatusValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> statusList = new ArrayList<String>();
		statusList.add(ParameterConst.RECORD_STATUS_ACTIVE);
		statusList.add(ParameterConst.RECORD_STATUS_CANCLED);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), statusList));
		return getDropdownCodeValuesBySearchBean(ParameterTypeConst.RECORD_STATUS, searchBean);
	}

	/**
	 * The method used for generating a list of contract status 
	 * for searching contract as selection.
	 * 
	 * @return the selection list of contract status
	 */
	public SelectItem[] getSearchCircularStatusDropdown() {
		SelectItem[] items = null;

		try {
			items = new SelectItem[5];
			int i = 0;
			items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.bpm.all", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CIRCULAR_STATUS_EFFECTIVE,  
					ResourceBundleUtils.getMessage("lbl.ct.effective", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CIRCULAR_STATUS_INACTIVE,  
					ResourceBundleUtils.getMessage("lbl.ct.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CIRCULAR_STATUS_EXPIRED,  
					ResourceBundleUtils.getMessage("lbl.ct.expired", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CIRCULAR_STATUS_CANCELLED,  
					ResourceBundleUtils.getMessage("lbl.ct.cancelled", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}

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
		

		if(getManagedBean().isReadOnly()) {
			getManagedBean().getCircularListingPagingDataModel().resetData();
			getManagedBean().getServiceListingPagingDataModel().resetData();
			//getManagedBean().getCircularListingPagingDataModel().reloadData();
			//getManagedBean().getServiceListingPagingDataModel().reloadData();
			//getManagedBean().resetServiceListingPagingDataModel();
			//getManagedBean().resetCircularListingPagingDataModel();
			getManagedBean().setResetval(false);
			getManagedBean().setReadOnly(true);
			
		} else {
			getManagedBean().getCircularListingPagingDataModel().resetData();
			getManagedBean().getServiceListingPagingDataModel().resetData();
			//getManagedBean().resetServiceListingPagingDataModel();
			//getManagedBean().resetCircularListingPagingDataModel();
			getManagedBean().setResetval(false);
			getManagedBean().setReadOnly(true);
			
		}
	}


	/**
	 * The method used to navigate to circular details screen.
	 * 
	 * @param circularVerId  the circular version id
	 * @param docType  the document type
	 * @return the navigate URL 
	 */
	public String gotoPage(Long circularVerId, String docType) {

		CircularReqDTO reqDTO = new CircularReqDTO();
		reqDTO.setCircularVerId(circularVerId);
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
			PanelBackingBean ppBB = new PanelBackingBean(); 
			ppBB.getManagedBean().setSelectedPanelZone(null);
			ppBB.getManagedBean().setSelectedTabIndex(0);
			ppBB.getManagedBean().setAction("view");
			ppBB.getManagedBean().setUpdateAction(false);
			ppBB.getManagedBean().setViewContract(true);
			ppBB.getManagedBean().setOutComes(new String[]{});
			ppBB.setPanelCircularVar(reqDTO);
			ppBB.getManagedBean().setFromCircularNotice(true);
			return "../panel/circularBase.xhtml?faces-redirect=true";
		} else {
			CentralContractBackingBean ccBB = new CentralContractBackingBean();
			ccBB.getManagedBean().setSelectedCircularContract(null);
			ccBB.getManagedBean().setSelectedCircularItem(null);
			ccBB.getManagedBean().setSelectedZoneItems(null);
			ccBB.getManagedBean().setSelectedTabIndex(0);
			ccBB.getManagedBean().setAction("view");
			ccBB.getManagedBean().setViewContract(true);
			ccBB.getManagedBean().setUpdateAction(false);
			ccBB.getManagedBean().setOutComes(new String[]{});
			ccBB.setContractCircularVar(reqDTO);	
			ccBB.getManagedBean().setFromCircularNotice(true);
			return "../centralcontract/circularBase.xhtml?faces-redirect=true";
		}
	}

	/**
	 * The method used to navigate to circular details screen
	 * for view purpose.
	 * 
	 * @param circularVerId  the circular version id
	 * @param docType  the document type
	 * @return the navigate URL
	 */
	public String gotoViewPage(Long circularVerId, String docType) {

		CircularReqDTO reqDTO = new CircularReqDTO();
		reqDTO.setCircularVerId(circularVerId);
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
			PanelBackingBean ppBB = new PanelBackingBean();
			ppBB.getManagedBean().setSelectionOfZone(false);
			ppBB.getManagedBean().setSelectedPanelItems(null);
			ppBB.getManagedBean().setSelectedPanelZone(null);
			ppBB.getManagedBean().setSelectedTabIndex(0);
			ppBB.getManagedBean().setAction("view");
			ppBB.getManagedBean().setUpdateAction(false);
			ppBB.getManagedBean().setViewContract(true);
			ppBB.getManagedBean().setOutComes(new String[]{});
			ppBB.setPanelCircularVar(reqDTO);
			ppBB.getManagedBean().setFromCircularNotice(true);
			return "panelCircularTab.xhtml?faces-redirect=true";
		} else {
			CentralContractBackingBean ccBB = new CentralContractBackingBean();
			ccBB.getManagedBean().setSelectedCircularContract(null);
			ccBB.getManagedBean().setSelectedCircularItem(null);
			ccBB.getManagedBean().setSelectedZoneItems(null);
			ccBB.getManagedBean().setSelectedTabIndex(0);
			ccBB.getManagedBean().setAction("view");
			ccBB.getManagedBean().setViewContract(true);
			ccBB.getManagedBean().setUpdateAction(false);
			ccBB.getManagedBean().setOutComes(new String[]{});
			ccBB.setContractCircularVar(reqDTO);	
			ccBB.getManagedBean().setFromCircularNotice(true);
			return "centralCircularTab.xhtml?faces-redirect=true";
		}
	}

	/**
	 * This  method used to fetch status of circular
	 * 
	 * @return the selection list of circular status
	 */
	public SelectItem[] getStatusDropdown() {
		IStatusSO statusSO = null;
		SelectItem[] items = null;
		try {
			statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
			String languageCode = new FacesScopes().getUserLocale().getLanguage();

			int i = 1;
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode(ModuleCodeConst.CONTRACT_MANAGEMENT);
			reqDTO.setDocType(getManagedBean().getSearchCircularDocType());
			reqDTO.setLanguageCode(languageCode);
			StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);

			List<PmStatusDesc> list =  resDTO.getStatusDescList();
			items = new SelectItem[list.size()+1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

			for(PmStatusDesc statusDesc : list) {
				items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			statusSO = null;
		}
		return items;
	}

	/**
	 * Method used to retrieve the information and render it on screen.
	 */
	public void preRenderSearch(boolean trackingDiarySearch) {
		//searchReset();
		//getManagedBean().setReadOnly(true);
		//getManagedBean().resetCircularListingPagingDataModel();
		getManagedBean().setTrackingDiarySearch(trackingDiarySearch);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "search.xhtml");
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
							getManagedBean().getUOM().equals("") &&	
							getManagedBean().getSkuno().equals("") &&
							getManagedBean().getTypeofgoods()== null  )
				{
					
					throw new EpException(MessageConst.ERROR_750049);
					
				}else if(getManagedBean().getItemtype()!=null)
				{
					getManagedBean().setResetval(true);
					if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT))
					{
						//getManagedBean().getServiceListingPagingDataModel().resetData();
						getManagedBean().resetServiceListingPagingDataModel();
						getManagedBean().getCircularListingPagingDataModel().reloadData();
						getManagedBean().prodValue(true);
						//log.info("Search function executed Prodcut"+ParameterConst.ITEM_TYPE_PRODUCT+"  "+getManagedBean().getItemtype()); 						
					}else if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE))
					{
						  //getManagedBean().getCircularListingPagingDataModel().resetData();
						  getManagedBean().resetCircularListingPagingDataModel();
						  getManagedBean().getServiceListingPagingDataModel().reloadData();
						  getManagedBean().serviceValue(true);
							
						//log.info("Search function executed Service"+ParameterConst.ITEM_TYPE_SERVICE+"  "+getManagedBean().getItemtype()); 
					}else if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE))
					{
						  getManagedBean().getCircularListingPagingDataModel().reloadData();
						  getManagedBean().getServiceListingPagingDataModel().reloadData(); 
						  getManagedBean().allValue(true);
						 
						  //log.info("Search function executed Product and Service"+ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE+"  "+getManagedBean().getItemtype()); 
					}
				}else if(getManagedBean().getItemtype()== null)
				{
					throw new EpException(MessageConst.ERROR_750051);
				}
				
			   
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

	public String contactItem(CtContractItem ctItem) {
		
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
		String result = itemName +","+ itemcode;

		/*result.append(itemcode);
		result.append(",");
		result.append(itemName);
		*/
		
		return result;
	     
	}
	
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
	 * User need to make decision on circular maintenance page
	 * Navigate to circular page as per user selection for update or view
	 * 
	 * @param action			the user action
	 * @param circularVerId		the circular version id
	 * @return the navigate URL 
	 * */
	public String gotoPage(String action, Long circularVerId) {

		CircularReqDTO reqDTO = new CircularReqDTO();
		reqDTO.setCircularVerId(circularVerId);

		if(getManagedBean().getSearchCircularDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
			PanelBackingBean panelBackingBean = new PanelBackingBean();
			panelBackingBean.getManagedBean().setSelectionOfZone(false);
			panelBackingBean.getManagedBean().setSelectedPanelItems(null);
			panelBackingBean.getManagedBean().setSelectedPanelZone(null);
			panelBackingBean.getManagedBean().setSelectedTabIndex(0);
			panelBackingBean.getManagedBean().setAction(action);
			panelBackingBean.getManagedBean().setGotoPreviewPage("/xhtml/ct/circular/maintenance/search.xhtml?faces-redirect=true");

			if(action.equalsIgnoreCase("view")) {
				panelBackingBean.getManagedBean().setUpdateAction(false);
				panelBackingBean.getManagedBean().setViewContract(true);
				panelBackingBean.getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				panelBackingBean.getManagedBean().setUpdateAction(true);
				panelBackingBean.getManagedBean().setViewContract(false);
				panelBackingBean.getManagedBean().setCircularExtension(false);
				panelBackingBean.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			}
			panelBackingBean.setPanelCircularVar(reqDTO);
			return "/xhtml/ct/circular/panel/circularBase.xhtml?faces-redirect=true";
		} else {
			CentralContractBackingBean centralContractBackingBean = new CentralContractBackingBean();
			centralContractBackingBean.getManagedBean().setSelectedCircularContract(null);
			centralContractBackingBean.getManagedBean().setSelectedCircularItem(null);
			centralContractBackingBean.getManagedBean().setSelectedZoneItems(null);
			centralContractBackingBean.getManagedBean().setSelectedTabIndex(0);
			centralContractBackingBean.getManagedBean().setAction(action);
			centralContractBackingBean.getManagedBean().setGotoPreviewPage("/xhtml/ct/circular/maintenance/search.xhtml?faces-redirect=true");

			if(action.equalsIgnoreCase("view")) {
				centralContractBackingBean.getManagedBean().setViewContract(true);
				centralContractBackingBean.getManagedBean().setUpdateAction(false);
				centralContractBackingBean.getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				centralContractBackingBean.getManagedBean().setUpdateAction(true);
				centralContractBackingBean.getManagedBean().setViewContract(false);
				centralContractBackingBean.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			}
			centralContractBackingBean.setContractCircularVar(reqDTO);
			return "/xhtml/ct/circular/centralcontract/circularBase.xhtml?faces-redirect=true";
		}
	}

	/**
	 * Method used to check is it the login user have the Panel Circular Admin Role
	 * 
	 * @return true, if login user is panel administrator
	 *         false, if login user is not panel administrator
	 */
	public boolean isPanelAdmin() {
		return new FacesScopes().hasRoleUser(RoleCode.BPK_PCA.value());
	}

	/**
	 * Method used to check is it the login user have the Cental Contract Admin Role
	 * 
	 * @return true, if login user is central contract administrator
	 *         false, if login user is not central contract administrator
	 */
	public boolean isCentralContractAdmin() {
		return new FacesScopes().hasRoleUser(RoleCode.BPK_CCA.value());
	}

	/**
	 * Method used to check is it the login user have the Auditor Role
	 * 
	 * @return true, if login user is auditor
	 *         false, if login user is not auditor
	 */
	public boolean isAuditor() {
		return new FacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) ||  
				new FacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value());
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
