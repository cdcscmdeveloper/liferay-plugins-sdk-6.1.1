/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.suspension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.ct.suspension.IContractSuspensionSO;
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.persistence.entity.ct.CtActor;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScRequestItemSpecOffer;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.ct.suspension.SuspensionManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.RoleHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Contract Suspension Details
 * 
 * @author Alam
 * @since 1/13/2014
 */
@ManagedBean (name = "suspensionBackingBean")
@RequestScoped
public class SuspensionBackingBean extends ContractBaseBackingBean<SuspensionManagedBean> {

	private static final long serialVersionUID = 1L;
	private ContractBackingBean ctBB = null;
	private static Logger log = Logger.getLogger(SuspensionBackingBean.class);
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";

	/**
	 * This constructor method used to initialize the variable on start up
	 * Setting button if user not call from BPM
	 * Method used to initialize approver list
	 * Method used to check view able mode
	 */
	public SuspensionBackingBean() {
		if(ctBB == null){
			ctBB = new ContractBackingBean();
		}
		/*Initialization of button*/
		if(getManagedBean().getOutComes() == null) 
			getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});		
		if(getManagedBean().getUserMap().size()==0) {
			initializeApproverList();
		}
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);	          
		if(originalServletRequest.getParameter(ContractConst.PARAMETER_ID) != null)
			getManagedBean().setViewOnly(true);
	}

	/**
	 * This  method used to initialize approver list
	 */
	private void initializeApproverList() {
		if(getManagedBean().getApproverModel() == null) {
			getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(), new ArrayList<PmUser>()));
		}
	}

	/**
	 * This  method call once user call suspension page from task list
	 * Method used to retrieve all information from BPM
	 * Method used to retrieve contract information from BPM
	 * Method return null if task id null
	 */
	public void preRenderView(){
		ContractReqDTO reqDTO = null;
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter(ContractConst.TASK_ID);
		String contractVerId = originalServletRequest.getParameter(ContractConst.PARAMETER_ID);

		/*Start if statement to check login user*/
		if(contractVerId!=null) {
			reqDTO = new ContractReqDTO();
			reqDTO.setContractVerId(Long.parseLong(contractVerId));
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			reqDTO.setUpdateAction(false);
			setSuspensionContractVer(reqDTO);

			/*Start if statement if login user not contract admin*/
			String roleCode = null;
			if (getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				roleCode = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)) {
				roleCode = RoleCode.BPK_CCA.value();
			}
			
			CtActor contractAdminActor = retrieveContractActor(
					getManagedBean().getContractVer().getContractVerId(),
					getManagedBean().getContractVer().getDocType(),roleCode);

			if (contractAdminActor != null
					&& !getFacesScopes().getUserSessionBean().getUserId().equals(contractAdminActor.getUser().getUserId())) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"", MessageUtils.getMessage(MessageConst.ERROR_700116,
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			
				return;
			}/*end if statement*/

			getManagedBean().setViewOnly(true);
			getManagedBean().setTaskId(taskId);
			return;
		}/*End if statement to check  login user */
		// 20997 - start : to reset search list after save
	    //added reloadDatatable() for #28643
		if(getManagedBean().getTaskId()!= null){
			getManagedBean().getSuspensionProductListingPagingDataModel().reloadDatatable();
		}
		// 20997 - end
		/*Return when not call from tasklist*/
		if(taskId==null) 
			return;

		getManagedBean().setViewOnly(false);
		CTContractSuspensionDataType dataType =  super.getBpmTaskDetails(CTContractSuspensionDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});		

		/*Retrieve contract id from BPM  */
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setUpdateAction(false);
		ctReqDTO.setContractId(new Long(dataType.getContractId()));
		ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
		ctReqDTO.setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
		
		setSuspensionContractVer(ctReqDTO);
		/*End*/
	}

	/**
	 * This  method used to set retrieve value from BPM or contract maintenance
	 */
	public void setSuspensionContractVer(ContractReqDTO reqDTO){
		ctBB.renewSesionObject();
		IContractSuspensionSO contractSuspensionSO= null;
		ContractResDTO resDTO = null;
		if(ctBB.getManagedBean().getDeleteAttachmentList() != null ){
			ctBB.getManagedBean().getDeleteAttachmentList().clear();
		}

		try{
			contractSuspensionSO = (IContractSuspensionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSuspensionSO.class);
			resDTO = contractSuspensionSO.retrieveSuspensionContractVerDtls(reqDTO);
			getManagedBean().setContractVer(resDTO.getCtContractVer());	
			getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE, getManagedBean().getCtContract().getContractNo()));
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());	  
			
			//#15851 start
			if(getManagedBean().getTaskId()!= null){
				ctBB.getManagedBean().setContractVerIdFromTaskList(reqDTO.getContractVerId());//#15851
			}
			//#15851 end

			//CT_CHANGE_LOG
			ctBB.getManagedBean().setUpdateAgencyMap(resDTO.getUpdateAgencyMap());
			ctBB.getManagedBean().setUpdateItemMap(resDTO.getUpdateItemMap());

			//Retrieve agreement contract version for physical no & signed date
			retrieveContractVerOfAgreement();
			
			// Clear for search item
			getManagedBean().setSearchItemString("");
			getManagedBean().getContractItemList().clear();
			
			/*If statement  for check contact item start*/
			if(getManagedBean().getContractVer().getCtContractItems() != null && getManagedBean().getContractVer().getCtContractItems().size() > 0 ) {
				Collections.sort(getManagedBean().getContractVer().getCtContractItems(), new CtContractItem());

				for (CtContractItem contractItem : getManagedBean().getContractVer().getCtContractItems()) {
					getManagedBean().getContractItemList().add(contractItem);
					/*Get latest contract item and map with the sc request item*/
					for (CtContractItem latestContractItem : getManagedBean().getCtContract().getLatestContractVer().getCtContractItems()) {
						if (contractItem.getScRequestItem().getRequestNoteItemId().equals(latestContractItem.getScRequestItem().getRequestNoteItemId())) {
							getManagedBean().getItemMap().put(contractItem.getScRequestItem().getRequestNoteItemId(), latestContractItem);
							break;
						}
					}
				}
			}/*If statement for contract item end*/			

			/*If statement  to check agency start*/
			if(getManagedBean().getContractVer().getCtAgencies() != null && getManagedBean().getContractVer().getCtAgencies().size() > 0 )  {	
				for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
					/*Get latest agency and map with the org profile*/
					for (CtAgency latestAgency : getManagedBean().getCtContract().getLatestContractVer().getCtAgencies()) {
						if(ctAgency.getOrgProfile().getOrgProfileId().equals(latestAgency.getOrgProfile().getOrgProfileId())) {
							getManagedBean().getAgencyMap().put(ctAgency.getOrgProfile().getOrgProfileId(), latestAgency);
							break;
						}
					}			 		
				}
			}/*If statement for agency end*/    

			if(getManagedBean().getUserMap().size()==0) {
				initializeApproverList();
			}
			// Retrieve Approver List Information
			if (isApproverSetting()) {				
				getManagedBean().getApproverModel().setSource(
						getAvailableApproverList(getManagedBean().getCtContract().getContractType().getParameterCode(),
								getManagedBean().getUserMap()));

				if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
					getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
					List<PmUser> invalidApproverList = new ArrayList<PmUser>();
					for (PmUser approverUser : resDTO.getApproverList())  {
						Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();	
						boolean isValidApprover = false;
						while (sourceIt.hasNext()) {
							PmUser user = sourceIt.next();
							log.debug("user : "+user+"; approverUser: "+approverUser);
							if (user!=null && approverUser!=null && (user.getUserId().longValue() == approverUser.getUserId().longValue())) {
								sourceIt.remove();
								isValidApprover = true;
								break;
							}
						}
						if (!isValidApprover) {
							invalidApproverList.add(approverUser);
						}
					}
					if (invalidApproverList.size() > 0) {
						getManagedBean().getApproverModel().getTarget().removeAll(invalidApproverList);
					}
				} else {
					getManagedBean().getApproverModel().setTarget(new ArrayList<PmUser>());
				}
			}

			/*Setting to list of remark and attachement of document*/
			getManagedBean().setRemarkList(resDTO.getCtRemarks());
			getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			ctBB.getManagedBean().setViewContract(getManagedBean().isViewOnly());
			ctBB.getManagedBean().setContractVer(getManagedBean().getContractVer());
			ctBB.initializeRemarkAndAttachement(getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
			/*End remark setting*/

			// Enable or Disable GST field
			if (!ctBB.isLoaContractWithGst(getManagedBean().getCtContract().getLoa().getGstEffDate())
					&& !ctBB.isLoaContractWithGst(getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
				ctBB.getManagedBean().setGstCheckingGUI(false);	// Display N/A
			} else {
				if (getManagedBean().getContractVer().getCtContractAmount().getGstAmount() == null) {
					getManagedBean().getContractVer().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
				}
				ctBB.getManagedBean().setGstCheckingGUI(true);
			}
			
			// Enable or Disable STAX field

				if (getManagedBean().getContractVer().getCtContractAmount().getStaxAmount() == null) {
					getManagedBean().getContractVer().getCtContractAmount().setStaxAmount(BigDecimal.ZERO);
				}
				ctBB.getManagedBean().setStaxCheckingGUI(true);
			
			
		}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			contractSuspensionSO = null;
			resDTO = null;
		}
	}

	/**
	 * The method used to validate the entered Effective and Expiary Date of contract item .
	 * 
	 * @param ctContractItem   object of contract item
	 */
	public void onChangeEffExpDate(CtContractItem ctContractItem) {
		
		if (ctContractItem.getEffDate() == null && ctContractItem.getExpDate() == null) {
			throw new EpException(MessageConst.ERROR_100033);
		
		} 

		//getManagedBean().getUpdateItemMap().put(ctContractItem.getScRequestItem().getRequestNoteItemId(), ctContractItem);
		
		ContractItemReqDTO reqDTO =  new ContractItemReqDTO();
		reqDTO.setCtContactItem(ctContractItem);
		
		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
		
		contractItemSO.updateItemSearchService(reqDTO);
		
		this.getManagedBean().setSuspensionProductListingPagingDataModel(this.getManagedBean().resetSuspensionProductListingPagingDataModel());
	}
	
	/**
	 * This  method used for back button
	 * @return the string
	 * @throws PortalException the portal exception
	 * @throws SystemException the system exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String backAction() throws PortalException, SystemException, IOException {
		SuspensionBackingBean  supoendBB= new SuspensionBackingBean();
		supoendBB.getManagedBean().setItemcode(null);
		supoendBB.getManagedBean().setItemstatus(null);
		supoendBB.getManagedBean().setItemtype(null);
		supoendBB.getManagedBean().setUOM(null);
		supoendBB.getManagedBean().setSkuno(null);
		supoendBB.getManagedBean().setTypeofgoods(null);
	
		ContractBackingBean ccBB = new ContractBackingBean();
		ctBB.getManagedBean().setSelectedItem(null);
		ctBB.getManagedBean().setServiceselectedItem(null);
		
		supoendBB.getManagedBean().getSuspensionProductListingPagingDataModel().resetData();
		if (!getManagedBean().isViewOnly()) {
			return CONTRACT_MAINT_PAGE;
		} else { // view suspension from notification
			FacesContext.getCurrentInstance().getExternalContext().redirect(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL + "notifications"));
			return null;
		}
	}

	/**
	 * The method used for retrieving the contract version 
	 * for agreement or non-agreement contract.
	 * If it's original agreement, return the first contract version.
	 * If it's supplementary agreement, return the amended contract version.
	 */
	private void retrieveContractVerOfAgreement() {
		IContractSO contractSO = null;
		IContractAmendmentSO amendmentSO = null;
		ContractResDTO resDTO = null;
		ContractAmendmentResDTO amendmentResDTO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			amendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			ContractAmendmentReqDTO amendmentReqDTO = new ContractAmendmentReqDTO();

			if (getManagedBean().getContractVer().getCtContractAmount().isAgreement()) {
				//Retrieve amendment
				amendmentReqDTO.setContractId(getManagedBean().getCtContract().getContractId());
				amendmentReqDTO.setAmendmentEffectiveDate(getManagedBean().getContractVer().getCtContractAmount().getEffDate());
				amendmentResDTO = amendmentSO.getCtAmendmentByAmendmentEffDate(amendmentReqDTO);

				if (amendmentResDTO.getCtAmendment() != null) {
					ctBB.getManagedBean().setAgreementContractVer(amendmentResDTO.getCtAmendment().getCtContractVer());
				} else {
					reqDTO.setContractId(getManagedBean().getCtContract().getContractId());
					reqDTO.setParamCode(ParameterConst.DOCTYPE_CONTRACT_CREATION);

					resDTO = contractSO.retrieveContractVerByIdAndDocType(reqDTO);
					ctBB.getManagedBean().setAgreementContractVer(resDTO.getCtContractVer());
				}				
			} else {
				ctBB.getManagedBean().setAgreementContractVer(getManagedBean().getContractVer());
			}
		} catch(Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			contractSO = null;
			amendmentSO = null;
			amendmentResDTO = null;
			resDTO = null;
		}
	}

	/**
	 * This  method used to set contract item status value for suspension
	 * Status set to zero is suspension of item
	 */
	public void setSuspensionStatusValue(CtContractItem item) {
		Date currentDate = DateUtils.getCurrentDateWithoutTime();
		//#20997 - start
		//for(CtContractItem ctItem :this.getManagedBean().getContractVer().getCtContractItems()) {
		for (CtContractItem ctItem : getManagedBean().getSuspensionProductListingPagingDataModel().getList()) {
			if(ctItem != null && item != null) {
				// #28367
				if(ctItem.equals(item)) {
					CtContractItem latestVerItem = getManagedBean().getItemMap().get(ctItem.getScRequestItem().getRequestNoteItemId());
					Date effDate = null;
					Date expDate = null;
					if(item.getRecordStatusFlg().intValue() == 1) {
						effDate = (latestVerItem.getRecordStatusFlg() == 0) ? currentDate : latestVerItem.getEffDate();
						expDate = (latestVerItem.getRecordStatusFlg() == 0) ? getManagedBean().getContractVer().getCtContractValue().getExpDate() : latestVerItem.getExpDate();
						item.setEffDate(effDate);
						item.setExpDate(expDate);
					} else {
						effDate = (latestVerItem.getRecordStatusFlg() == 0) ? latestVerItem.getEffDate() : currentDate;
						expDate = (latestVerItem.getRecordStatusFlg() == 0) ?  latestVerItem.getExpDate() : getManagedBean().getContractVer().getCtContractValue().getExpDate();
						item.setEffDate(effDate);
						item.setExpDate(expDate);
					}
				}
			}
		}
		//CT_CHANGE_LOG
		ctBB.getManagedBean().getUpdateItemMap().put(item.getScRequestItem().getRequestNoteItemId(), item);
	}

	/**
	 * This  method used to set agency status  for suspension
	 * Status set to 0 is suspension of item
	 */
	public void setSuspensionAgencyStatusValue(CtAgency agency){
		Date currentDate = DateUtils.getCurrentDateWithoutTime();
		for(CtAgency ctAgency  : getManagedBean().getContractVer().getCtAgencies()) {
			if(ctAgency.equals(agency)) {
				CtAgency latestAgency = getManagedBean().getAgencyMap().get(agency.getOrgProfile().getOrgProfileId());
				Date effDate = null;
				Date expDate = null;

				if(ctAgency.getRecordStatusFlg().intValue() == 1) {
					effDate = (latestAgency.getRecordStatusFlg() == 0) ? currentDate : latestAgency.getEffDate();
					expDate = (latestAgency.getRecordStatusFlg() == 0) ? getManagedBean().getContractVer().getCtContractValue().getExpDate() : latestAgency.getExpDate();
					ctAgency.setEffDate(effDate);
					ctAgency.setExpDate(expDate);					
				} else {
					effDate = (latestAgency.getRecordStatusFlg() == 0) ? latestAgency.getEffDate() : currentDate;
					expDate = (latestAgency.getRecordStatusFlg() == 0) ? latestAgency.getExpDate() : getManagedBean().getContractVer().getCtContractValue().getExpDate();
					ctAgency.setEffDate(effDate);
					ctAgency.setExpDate(expDate);		
				}
			}
		}  
		//CT_CHANGE_LOG
		ctBB.getManagedBean().getUpdateAgencyMap().put(agency.getOrgProfile().getOrgCode(), agency);
	}

	/**
	 * This method used to suspend item and agency
	 * This method call validate function to validate suspension details
	 *  This method will navigate to acknowledgment page with
	 *   a confirmation message once submit successfully
	 */
	public String saveContractSuspension(String outcome) throws Exception{
		IContractSuspensionSO contractSuspensionSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;
		try{
			/*Validation for login user's org profile before initiate contract task*/
			if (!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)
					&& getManagedBean().getContractVer().getStatus() != null
					&& getManagedBean().getContractVer().getStatus().getStatusId() == ContractStatusConst.FS_PENDING_SUBMISSION.longValue()) {
				validateOwnerOrgProfile(
						getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			}
			
			validateSuspensionDetails(outcome);		
			contractSuspensionSO = (IContractSuspensionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSuspensionSO.class);
			//#20997 - to copy from searching list to items list
			if (getManagedBean().getSuspensionProductListingPagingDataModel() != null && getManagedBean().getSuspensionProductListingPagingDataModel().getList() != null) {
				for (CtContractItem ctContractItem : getManagedBean().getSuspensionProductListingPagingDataModel().getList()) {			
					for(CtContractItem ctItem : this.getManagedBean().getContractVer().getCtContractItems()){
						if(ctContractItem.getContractItemId().equals(ctItem.getContractItemId())
								|| ctItem.getScRequestItem().getRequestNoteItemId().equals(ctContractItem.getScRequestItem().getRequestNoteItemId())) {
							ctItem.setEffDate(ctContractItem.getEffDate());
							ctItem.setExpDate(ctContractItem.getExpDate());
							if(ctContractItem.getRecordStatusFlg() != null) {
								ctItem.setRecordStatusFlg(ctContractItem.getRecordStatusFlg().intValue());
							}
						}
					}
				}
			}
			//#20997 - end

			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			reqDTO = new ContractReqDTO();
			reqDTO.setToken(token);
			reqDTO.setOutcome(outcome);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);

			reqDTO.setCtContract(getManagedBean().getContractVer().getCtContract());
			reqDTO.setCtContractVer(getManagedBean().getContractVer());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
			reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());
			reqDTO.setDeleteAttachments(ctBB.getManagedBean().getDeleteAttachmentList());
			reqDTO.setUserMap(getManagedBean().getUserMap());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setUpdateAgencyMap(ctBB.getManagedBean().getUpdateAgencyMap());
			// #28367 - start
			if(getManagedBean().getTaskId() == null) {
				for(CtContractItem ctContractItem : ctBB.getManagedBean().getUpdateItemMap().values()) {
					ctContractItem.setContractItemId(null);
				}
			}
			// #28367 - end
			reqDTO.setUpdateItemMap(ctBB.getManagedBean().getUpdateItemMap());

			if(this.getManagedBean().getPmDigiSign() !=null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			resDTO = contractSuspensionSO.saveContractSuspensionDetails(reqDTO);
			if(resDTO.isSuccessful()) {
				log.debug("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				String nextRole = null;

				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					messageId = MessageConst.INFO_700021;
					if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
							ParameterConst.CONTRACT_TYPE_MINISTRY)) {
						nextRole = RoleCode.CT_APPROVER.value();
					} else { // Central Contract
						nextRole = RoleCode.BPK_CC_APPROVER.value();
					}
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				}

				if(StringUtils.isNotBlank(nextRole)){
					PmRoleDesc roleDesc = RoleHelper.getInstance().getRoleDescByCode(nextRole);

					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
									this.getManagedBean().getContractVer().getCtContract().getContractNo(),
									roleDesc.getRoleName()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

				} else {
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
									this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}

				if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
				else {	
					ContractReqDTO ctReqDTO = new ContractReqDTO();
					ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
					ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
					setSuspensionContractVer(ctReqDTO);
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			contractSuspensionSO = null;
			reqDTO = null;
			resDTO = null;
		}
		return null;
	} 

	/**
	 * This method used to validate suspend item and agency as per rule
	 */
	public void validateSuspensionDetails(String outcome){
		getManagedBean().setAutoUpdate(true);
		/*Remark is mandatory during revise and reject*/
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			CtRemark remark = getManagedBean().getRemarkList().get(0);
			if(remark.getRemark()==null || remark.getRemark().equalsIgnoreCase("")){
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())}); 
			}
		}

		/*Approver is mandatory for document submission. Please select at least one approver. */
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && isApproverSetting()
				&& getManagedBean().getApproverModel().getTarget().size()==0) {
			throw new EpException(MessageConst.ERROR_750010); 
		}
	}

	/**
	 * Method to set user role as per user login
	 * User role used in BPM to identify the task
	 */
	private String getUserRole() {
		String userRole = null;		
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FS_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FS_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FS_HT_NAME)) {
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
			}
		} else if (getManagedBean().getTaskName() != null
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FS_HT_NAME)) {
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
			}
		}

		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}

	/**
	 * Method to used auto update of contract
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}

	/**
	 * Method to retrieve portal name from property file
	 */
	public String getPortletTitle() {
		return ResourceBundleUtils.getMessage("lbl.ct.contract.suspension", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}

	/**
	 * Method used to set value for security check
	 */
	public String preSubmitContractSuspension(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveContractSuspension(getManagedBean().getButtonAction());
	}

	/**
	 * Method used to set update value false for disable purpose
	 */
	public void disableUpdate(){
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * Method used to check login user as central or ministry admin
	 */
	public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}

	/**
	 * Method used to check navigation of page for approver list
	 * Return list of user for contract admin 
	 */
	public boolean isApproverSetting() {
		if(isContractAdmin() && (getManagedBean().getTaskName() == null ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FS_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FS_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FS_HT_NAME)))
			return true;
		else 
			return false;
	}

	/**
	 * Method to authenticate user during submit of suspension
	 * Navigate confirmation dialog for authenticate of login user
	 * Call save suspension method once authentication done
	 */
	public String gpkiSignAction(String outcome) throws Exception{
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome) || BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
			validateSuspensionDetails(outcome);
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(outcome);			
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}else if (BpmOutcomeConst.BPM_CANCEL.equals(outcome)) {
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		}   else {
			return saveContractSuspension(outcome);
		}
	}

	// #28854 - Start
	/**
	 * The method used to invoke and initiate the item specification dialog.
	 */
	public void viewItemSpecDlg(CtContractItem ctContractItem) {
		// Start of method implementation
		getManagedBean().setSelectedItemForSpec(ctContractItem);
		IRequestItemSO requestItemSO = (IRequestItemSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IRequestItemSO.class);
		Long requestItemId = getManagedBean().getSelectedItemForSpec().getScRequestItem().getRequestNoteItemId();
		String procurementMode = getManagedBean().getContractVer().getCtContract().getLoa().getProcurementMode()
				.getParameterCode();

		if (procurementMode.equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			handleDirectPurchaseMode(requestItemSO, requestItemId);
		} else if (procurementMode.equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_QUOTATION)
				|| procurementMode.equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_TENDER)
				|| procurementMode.equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO)) {
			handleQuotationTenderOrDirectNegoMode(requestItemSO, requestItemId);
		}

		RequestContext.getCurrentInstance().execute("itemSpecDlg.show()");
	}

	/**
	 * Handles the logic specific to the direct purchase procurement mode.
	 * 
	 * @param requestItemSO
	 *            the service object used to retrieve item specifications; must
	 *            not be null
	 * @param requestItemId
	 *            the ID of the request item; must not be null
	 */
	private void handleDirectPurchaseMode(IRequestItemSO requestItemSO, Long requestItemId) {
		List<String> itemSpecList = new ArrayList<>();
		List<ScRequestItemSpecOffer> itemSpecOfferList = requestItemSO.retrieveNewOfferSpec(requestItemId);

		for (ScRequestItemSpecOffer itemSpecOffer : itemSpecOfferList) {
			String questionType = itemSpecOffer.getQuestionType();
			if (ContractConst.QUESTION_TYPE_ITEM.equals(questionType)) {
				getManagedBean().setSupplierOffer(encodeHtmlStringOrNull(itemSpecOffer.getSupplierOffer()));
				getManagedBean().setNewOfferSpec(encodeHtmlStringOrNull(itemSpecOffer.getNewOfferSpec()));
			} else if (ContractConst.QUESTION_TYPE_SPEC.equals(questionType)) {
				itemSpecList.add(encodeHtmlStringOrNull(itemSpecOffer.getItemSpec()));
			}
		}
		getManagedBean().setItemSpecification(itemSpecList);
	}

	/**
	 * Handles the logic for procurement modes that involve quotations, tenders,
	 * or direct negotiations.
	 * 
	 * @param requestItemSO
	 *            the service object used to retrieve item specifications; must
	 *            not be null
	 * @param requestItemId
	 *            the ID of the request item; must not be null
	 */
	private void handleQuotationTenderOrDirectNegoMode(IRequestItemSO requestItemSO, Long requestItemId) {
		List<ScRequestItemSpecOffer> itemSpecOffers = requestItemSO.retrieveNewOfferSpec(requestItemId);

		for (Iterator<ScRequestItemSpecOffer> iterator = itemSpecOffers.iterator(); iterator.hasNext();) {
			ScRequestItemSpecOffer itemSpecOffer = iterator.next();

			// Encoding by directly applying to non-null fields
			itemSpecOffer.setItemSpec(encodeHtmlStringOrNull(itemSpecOffer.getItemSpec()));
			itemSpecOffer.setSupplierOffer(encodeHtmlStringOrNull(itemSpecOffer.getSupplierOffer()));
			itemSpecOffer.setNewOfferSpec(encodeHtmlStringOrNull(itemSpecOffer.getNewOfferSpec()));

			// Remove items with specific question type
			if (ContractConst.QUESTION_TYPE_ITEM.equals(itemSpecOffer.getQuestionType())) {
				iterator.remove();
			}
		}

		getManagedBean().setScRequestItemSpecOfferList(itemSpecOffers);
	}

	// #28854 - End
	
	/**
	 * The method used to concat both Item code and Item Name.
	 */
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
		//String result = itemName +","+ itemcode;
		ScRequestItem scRequestItem = new ScRequestItem();
		scRequestItem.setItemName(itemName);
		scRequestItem.setCmItem(ctItem.getScRequestItem().getCmItem());
		scRequestItem.getCmItem().setExtensionCode(itemcode);
		
		return scRequestItem;
	    
	}
	//#31558 end
	
	
	/**
	 * The method used to search for the item.
	 */
	public void searchItemAction() {

		if( getManagedBean().getItemcode().equals("") && 
				getManagedBean().getSearchItemString().equals("") &&
				getManagedBean().getItemstatus() == null &&
				getManagedBean().getItemtype()== null &&
				getManagedBean().getUOM().equals("") &&	
				getManagedBean().getSkuno().equals("") &&
				getManagedBean().getTypeofgoods()== null  )
	{
		
		throw new EpException(MessageConst.ERROR_750049);
		
	} 
	else if(getManagedBean().getItemtype()!=null)
	{
		if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT))
		{
			getManagedBean().getSuspensionProductListingPagingDataModel().reloadData();
		
		}
		if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE))
		{
			  getManagedBean().getSuspensionProductListingPagingDataModel().reloadData();
	
		}
		if(getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE))
		{
			 getManagedBean().getSuspensionProductListingPagingDataModel().reloadData();
	
		}
	}else if(getManagedBean().getItemtype()==null) {
		
		getManagedBean().getSuspensionProductListingPagingDataModel().reloadData();
		
	}

		/*if (getManagedBean().getSearchItemString() != null &
				!getManagedBean().getSearchItemString().trim().isEmpty()) {
			getManagedBean().getContractItemList().clear();
			for (CtContractItem ctContractItem : getManagedBean().getContractVer().getCtContractItems()) {
				if (ctContractItem.getScRequestItem().getItemName().toLowerCase().contains(
						getManagedBean().getSearchItemString().trim().toLowerCase())) {
					getManagedBean().getContractItemList().add(ctContractItem);
				}
			}
		}*/
	}
	
	/**
	 * The method used to reset the search criteria for item.
	 */
	public void resetSearchItemAction() {
		//getManagedBean().getContractItemList().clear();
		ContractBackingBean ccBB = new ContractBackingBean();
		getManagedBean().setSearchItemString("");
		getManagedBean().setItemcode("");
		getManagedBean().setItemstatus(null);
		getManagedBean().setItemtype(null);
		getManagedBean().setUOM("");
		getManagedBean().setSkuno("");
		getManagedBean().setTypeofgoods(null);
		ccBB.getManagedBean().setSelectedItem(null);
		ccBB.getManagedBean().setServiceselectedItem(null);
		ccBB.getManagedBean().setCtItemServiceList(null);
		if(getManagedBean().isReadOnly()) {
			getManagedBean().getSuspensionProductListingPagingDataModel().resetData();
			//getManagedBean().getSuspensionProductListingPagingDataModel().reloadData();
		} else {
			//getManagedBean().resetSuspensionProductListingPagingDataModel();
			getManagedBean().getSuspensionProductListingPagingDataModel().resetData();
			
		}

		/*getManagedBean().getContractItemList().clear();
		for (CtContractItem ctContractItem : getManagedBean().getContractVer().getCtContractItems()) {
			getManagedBean().getContractItemList().add(ctContractItem);
		}*/
	}
	
	//Enhancement 15851, 34142 - shaqimi start 02/12/2022
	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	@Override
	public void goToLoaView(ScLoa scLoa) {
		String slLoaNo = this.getManagedBean().getContractVer().getCtContractValue().getLoaNo();
		Long contractId = this.getManagedBean().getContractVer().getCtContract().getContractId();

		if(slLoaNo.substring(0,2).equals(ParameterConst.DOCTYPE_SUPPLEMENTAL_CONTRACT_LETTER)){
			AmendmentBackingBean abb = new AmendmentBackingBean();
			
			FacesContext fc = FacesContext.getCurrentInstance();
			abb.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId()+"?faces-redirect=true");
			abb.getManagedBean().setTrackingDairyView(true);
			abb.getManagedBean().setSupplementalLoaNo(slLoaNo);
			
			ctBB.getManagedBean().setViewSCL(true);
			ctBB.getManagedBean().setDocTypeFromTaskList(this.getManagedBean().getContractVer().getDocType());
			ctBB.getManagedBean().setStatusIdFromTaskList(getManagedBean().getContractVer().getStatus().getStatusId());
			ctBB.getManagedBean().setContractIdFromTaskList(null);
			ctBB.getManagedBean().setTaskNameFromTaskList(null);

			if(ctBB.getManagedBean().isViewContractFromAgreement()){
				ctBB.getManagedBean().setViewSCLFromContract(true);
			}
			abb.gotoAmendViewPage(contractId, scLoa.getLoaId());
		} else {
			super.goToLoaView(scLoa);
		}
	}
	//Enhancement 15851, 34142 - shaqimi end 02/12/2022
}
