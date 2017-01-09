package my.ep.web.backingbean.fl.customerService;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
// TODO: Auto-generated Javadoc

import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.exception.EpException;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.bean.ct.CustomerServiceEntity;
import my.ep.web.managedbean.fl.customerService.FlCustomerServiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * Backingbean for Customer Service.
 *
 * @author Giribabu
 * @since 23/11/2016
 */
@ManagedBean(name="flCustomerServiceBackingBean")
@ViewScoped
public class FlCustomerServiceBackingBean extends FulfilmentBaseBackingBean<FlCustomerServiceManagedBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The log. */
	private static Logger log = Logger.getLogger(FlCustomerServiceBackingBean.class);
	private String sysUserId = "weblogic";
	
	/**
	 * Advance search.
	 */
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}
	
	
	/**
	 * Transaction Type is mandatory field.
	 * <p>Document Type is mandatory field.
	 * <p>if the user specify the specific document no, the document type is not required to be filled in.
	 * <p>get fl document Search result based on selected and filled in parameters.
	 */
	public void searchDocument(){
		if(null == getManagedBean().getPtj()){ 
			throw new EpException(MessageConst.ERROR_100022, 
					new String[] {ResourceBundleUtils.getMessage("lbl.fl.ptj.code", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		getManagedBean().getFlDocumentDataModel().reloadData();
	}
	
	/**
	 * Gets all document types for document type drop down.
	 *
	 * @return the document type drop down
	 */
	public SelectItem[] getDocumentTypeDropDown() {
		
		List<SelectItem> selectedItemList = new ArrayList<SelectItem>();
		try {
			SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.FULFILMENT_DOCUMENT_TYPE);
			selectedItemList.add(items[0]);
			List<SelectItem> itemList = new ArrayList<SelectItem>();
		    for(SelectItem selectItem : items) {
		    	itemList.add(selectItem);
		    }
			List<String> flDocTypes = getManagedBean().getDocTypeListByRole();
			List<String> selectedItemCode = new ArrayList<String>();
		    for(SelectItem selectItem : itemList) {
		    	if(flDocTypes.contains(selectItem.getValue()) && !selectedItemCode.contains(selectItem.getValue().toString())) {
		    		selectedItemCode.add(selectItem.getValue().toString());
		    		selectedItemList.add(selectItem);
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(),e.getMessage()));
		}
	    return selectedItemList.toArray(new SelectItem[selectedItemList.size()]);
	}
	
	/**
	 * Navigate to page based on doc type.
	 * <p> if user select cancel action,to allow user to do document cancellation.
	 * <p> if user select update action,to allow user to do document modification.
	 * <p> if user select view action,to allow user to view document .
	 * <p> if user select stop instruction action,to allow user to do document Stop Delivery Instruction.
	 *
	 * @return the string
	 */
	public String navigateToPage(String docType, String flDocId,Long statusId,String docNo,String ordrNo){
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		String navgPage="/xhtml/fl/customerservice/customerServiceViewResult.xhtml";
		getManagedBean().setStatusDescId(statusId);
		getManagedBean().setViewDocNo(docNo);
		getManagedBean().setViewOrdrNo(ordrNo);
		getManagedBean().setViewKumpulanPTJ(retrieveJabatanByOrgProfileId(getManagedBean().getPtj().getOrgProfileId()));
		getManagedBean().setViewMinistry(retrieveMinistryByOrgProfileId(getManagedBean().getPtj().getOrgProfileId()));
		getManagedBean().setViewPtjName(getManagedBean().getPtj().getOrgName());
		getManagedBean().setViewPtjCode(getManagedBean().getPtj().getOrgCode());
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		 Object ctxToken = bpmSO.authenticate(sysUserId);
		 getManagedBean().getCustomerServiceEntity().clear();
		 BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		 bpmReqDTO.setDocNumber(docNo);
		 bpmReqDTO.setUserId(sysUserId);
		 bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
		 bpmReqDTO.setCtxToken(ctxToken);
		 BpmResponseDTO bpmResDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
		 if(bpmResDTO.getWorklistVO().getWorklistItem()!=null && bpmResDTO.getWorklistVO().getWorklistItem().size() > 0) {
				   for(WorklistItemVO worklistItemVO : bpmResDTO.getWorklistVO().getWorklistItem()){
				    	 		CustomerServiceEntity custServiceEntity = new CustomerServiceEntity();
					    	 	custServiceEntity.setOfficerName(worklistItemVO.getAssignees().get(0));
					    	 	bpmReqDTO = new BpmRequestDTO();
								bpmReqDTO.setCtxToken(ctxToken);
								bpmReqDTO.setTaskId(worklistItemVO.getTaskId());
								bpmResDTO = bpmSO.getTaskDetails(bpmReqDTO);
								getManagedBean().getCustomerServiceEntity().add(custServiceEntity);
				    	 }	
			}
		
		return navgPage;
	}
	
	/**
	 * Search reset.
	 */
	public void searchReset(){
		renewSesionObject();
	}
		
	public String getUsername(Long userId) {
		return PmCommonUtil.retriveUserName(userId);
	}
	
	public String backToPervious() {
		
	return "customerService";
	}
	/**
	 * Selected order type.
	 *
	 * @param e the e
	 */
	public void selectedOrderType(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().setSearchDocType(null);

		resetSearchCriteria();
	}
	
	/**
	 * Selected doctype.
	 *
	 * @param e the e
	 */
	public void selectedDoctype(AjaxBehaviorEvent e) {
		String docType = (String) e.getComponent().getAttributes().get("value");
		
		resetSearchCriteria();
		
		getManagedBean().setSearchDocType(docType);
	}
	
	/**
	 * Reset search criteria.
	 */
	private void resetSearchCriteria() {
		getManagedBean().setSearchDocNo(null);
		getManagedBean().setStatusDescId(null);
		getManagedBean().setSearchSuppName(null);
		getManagedBean().setSearchOrderNo(null);
	}
	
}
