package my.ep.web.backingbean.ct.request.ctadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.ct.reqctadmin.IRequestContractAdminSO;
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
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.requestctadmin.CTContractReqadminDataType;
import my.ep.persistence.entity.ct.CtActor;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.ct.requestCtAdmin.RequestCtAdminManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.RoleHelper;




@ManagedBean (name = "requestCtAdminBackingBean")
@ViewScoped
public class RequestCtAdminBackingBean extends ContractBaseBackingBean<RequestCtAdminManagedBean>{

	
	private static final long serialVersionUID = 1L;
	private ContractBackingBean ctBB = null;
	private static Logger log = Logger.getLogger(RequestCtAdminBackingBean.class);
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";

	/**
	 * This constructor method used to initialize the variable on start up
	 * Setting button if user not call from BPM
	 * Method used to initialize approver list
	 * Method used to check view able mode
	 */
	public RequestCtAdminBackingBean() {
		if(ctBB == null){
			ctBB = new ContractBackingBean();
		}
		/*Initialization of button*/
		if(getManagedBean().getOutComes() == null) 
			getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});		
		if(getManagedBean().getUserMap().size()==0) {
			initializeApproverList();
		}
		/*HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);	          
		if(originalServletRequest.getParameter(ContractConst.PARAMETER_ID) != null)
			getManagedBean().setViewOnly(true);*/
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
	 * Method used to check navigation of page for approver list
	 * Return list of user for contract admin 
	 */
	public boolean isApproverSetting() {
		if(isContractAdmin() && (getManagedBean().getTaskName() == null ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_RA_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_RA_HT_NAME) ))
			return true;
		else 
			return false;
	}
	
	/**
	 * Method used to check login user as central or ministry admin
	 */
	public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}
	
	/**
	 * Method to retrieve portal name from property file
	 */
	public String getPortletTitle() {	
		String docType = getManagedBean().getContractVer().getDocType();
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_REQUEST_CT_ADMIN))
			return ResourceBundleUtils.getMessage("lbl.ct.contract.request.ct.admin", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		else
			return ResourceBundleUtils.getMessage("lbl.ct.contract.remove.ct.admin", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		
	}
	
	/**
	 * Method to used auto update of contract
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * This  method used for back button
	 * @return the string
	 * @throws PortalException the portal exception
	 * @throws SystemException the system exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String backAction() throws PortalException, SystemException, IOException {
		RequestCtAdminBackingBean  reqCtAdBB= new RequestCtAdminBackingBean();
	
		ContractBackingBean ccBB = new ContractBackingBean();
		ctBB.getManagedBean().setSelectedItem(null);
		ctBB.getManagedBean().setServiceselectedItem(null);
		
		if (!getManagedBean().isViewOnly()) {
			return CONTRACT_MAINT_PAGE;
		} else { // view  requestCtadmin from notification
			FacesContext.getCurrentInstance().getExternalContext().redirect(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL + "notifications"));
			return null;
		}
	}
	
	/**
	 * Method to authenticate user during submit of requestCtadmin
	 * Call save requestCtadmin method once authentication done
	 * Navigate confirmation dialog for authenticate of login user
	 */
	public String gpkiSignAction(String outcome) throws Exception{
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome) || BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
			validateRequestCtAdminDetails(outcome);
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ContractConst.DOC_TYPE_REQUEST_CONTRACT_ADMIN);
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(outcome);			
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			//21387 start
			if(BpmOutcomeConst.BPM_APPROVE.equals(outcome)){
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}else{
				return saveRequestCtAdmin(outcome);
			}//21387 end
			return null;
		}else if (BpmOutcomeConst.BPM_CANCEL.equals(outcome)) {
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		}   else {
			return saveRequestCtAdmin(outcome);
		}
	}
	
	/**
	 * Method used to set value for security check
	 */
	public String preSubmitRequestCtAdmin(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveRequestCtAdmin(getManagedBean().getButtonAction());
	}
	
	
	public String saveRequestCtAdmin(String outcome) throws Exception{
		IRequestContractAdminSO contractReqAdminSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;
		try{
			/*Validation for login user's org profile before initiate contract task*/
			if (!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)
					&& getManagedBean().getContractVer().getStatus() != null
					&& getManagedBean().getContractVer().getStatus().getStatusId() == ContractStatusConst.RA_PENDING_SUBMISSION.longValue()) {
				validateOwnerOrgProfile(
						getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			}
			
			validateRequestCtAdminDetails(outcome);		
			contractReqAdminSO = (IRequestContractAdminSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IRequestContractAdminSO.class);

			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			reqDTO = new ContractReqDTO();
			reqDTO.setToken(token);
			reqDTO.setOutcome(outcome);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			/*this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_REQUEST_CONTRACT_ADMIN);*/

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
			reqDTO.setUpdateItemMap(ctBB.getManagedBean().getUpdateItemMap());
			

			if(this.getManagedBean().getPmDigiSign() !=null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			resDTO = contractReqAdminSO.saveReqCtAdminDetails(reqDTO);
			if(resDTO.isSuccessful()) {
				log.debug("Record saved successfully");
				String messageId = MessageConst.INFO_700041;
				String nextRole = null;

				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					messageId = MessageConst.INFO_700041;
					if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
							ParameterConst.CONTRACT_TYPE_MINISTRY)) {
						nextRole = RoleCode.CT_APPROVER.value();
					} else { // Central Contract
						nextRole = RoleCode.BPK_CC_APPROVER.value();
					}
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700040;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700022;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700044;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700042;
				}

				if(StringUtils.isNotBlank(nextRole)){
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
									this.getManagedBean().getContractVer().getCtContract().getContractNo(),
									getFacesScopes().getUserSessionBean().getUserName()}, 
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
				}else {	
					ContractReqDTO ctReqDTO = new ContractReqDTO();
					ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
					ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
					setRequestCtAdminContractVer(ctReqDTO);
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			contractReqAdminSO = null;
			reqDTO = null;
			resDTO = null;
		}
		return null;
	} 
	
	/*
	 * Method to set user role as per user login
	 * User role used in BPM to identify the task
	 */
	private String getUserROLE() {
		String userRole = null;		
		
		/*contract admin submit*/
		
				
			if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
			}else if (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
			}
		
	
		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
	
	/**
	 * This  method used to set retrieve value from BPM or contract maintenance
	 */
	public void setRequestCtAdminContractVer(ContractReqDTO reqDTO){
		ctBB.renewSesionObject();
		IRequestContractAdminSO reqCtAdminSO= null;
		ContractResDTO resDTO = null;
		if(ctBB.getManagedBean().getDeleteAttachmentList() != null ){
			ctBB.getManagedBean().getDeleteAttachmentList().clear();
		}

		try{
			
			reqDTO.setUserRole(getUserROLE());
			reqCtAdminSO = (IRequestContractAdminSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IRequestContractAdminSO.class);
			resDTO = reqCtAdminSO.retrieveReqCtAdminContractVerDtls(reqDTO);
			getManagedBean().setContractVer(resDTO.getCtContractVer());	
			getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					reqDTO.getDocType(), getManagedBean().getCtContract().getContractNo()));
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());	  

			//CT_CHANGE_LOG
			ctBB.getManagedBean().setUpdateAgencyMap(resDTO.getUpdateAgencyMap());
			ctBB.getManagedBean().setUpdateItemMap(resDTO.getUpdateItemMap());

			//Retrieve agreement contract version for physical no & signed date
			retrieveContractVerOfAgreement();
			
			
			/*initialize value for assignedCtAdmin*/
			if(getManagedBean().getContractVer().getCtContract().getAssignedCtAdmin() == null){
				getManagedBean().getContractVer().getCtContract().setAssignedCtAdmin(new PmUser());
				
			}
			if(getManagedBean().getCtContract().getCurrentContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.RA_APPROVED){
				log.debug("remove request contract admin");
				getManagedBean().setRemoveRequestAdmin(true);
				getManagedBean().setDocType(reqDTO.getDocType());
				
			}else{
				getManagedBean().setRemoveRequestAdmin(false);
				getManagedBean().setDocType(reqDTO.getDocType());
			}
			

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
			ctBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
			String status = ctBB.getStatusDescById(ctBB.getManagedBean().getContractVer().getStatus().getStatusId());
			ctBB.getManagedBean().setSupplierCompanyName(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			ctBB.getManagedBean().setSupplierBranchName(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierBranch());
			ctBB.getManagedBean().setSupplierRegNo(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
			ctBB.getManagedBean().setSupplierMofNo(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierMofNo());
			
			ctBB.getManagedBean().setViewContract(getManagedBean().isViewOnly());
			ctBB.getManagedBean().setContractVer(getManagedBean().getContractVer());
			ctBB.initializeRemarkAndAttachement(getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
			/*End remark setting*/
			
			
		}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			reqCtAdminSO = null;
			resDTO = null;
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
	 * This  method call once user call request admin page from task list
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
		log.info("RequestCtAdminManagedBean preRenderView contractVerId*******"+contractVerId);
		log.info("RequestCtAdminManagedBean preRenderView Task Name*******"+getManagedBean().getTaskName());
		
		/*Start if statement to check login user*/
		if(contractVerId!=null) {
			reqDTO = new ContractReqDTO();
			reqDTO.setContractVerId(Long.parseLong(contractVerId));
			reqDTO.setDocType(ParameterConst.DOCTYPE_REQUEST_CT_ADMIN);
			reqDTO.setUpdateAction(false);
			setRequestCtAdminContractVer(reqDTO);

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

		// 20997 - end
		/*Return when not call from tasklist*/
		if(taskId==null) 
			return;

		getManagedBean().setViewOnly(false);
		CTContractReqadminDataType dataType =  super.getBpmTaskDetails(CTContractReqadminDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});		

		//#21387 - basri
		/* re-Setting Cancel button*/
		if(this.getManagedBean().getOutComes()!= null){
		String[] str_array = this.getManagedBean().getOutComes();
		List<String> list = new ArrayList<String>(Arrays.asList(str_array));
		if(list.contains(BpmOutcomeConst.BPM_CANCEL)){
			list.remove(BpmOutcomeConst.BPM_CANCEL);
			list.add(BpmOutcomeConst.BPM_CANCEL);
			str_array = list.toArray(new String[0]);
			this.getManagedBean().setOutComes(null);
			this.getManagedBean().setOutComes(str_array);
			}
		}

		//#21387 - End
		
		/*Retrieve contract id from BPM  */
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setUpdateAction(false);
		ctReqDTO.setContractId(new Long(dataType.getContractId()));
		ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
		ctReqDTO.setDocType(dataType.getDocType());
		
		FacesContext context = FacesContext.getCurrentInstance();
		PortletRequest request = (PortletRequest)context.getExternalContext().getRequest();
		RenderResponse response = (RenderResponse) request.getAttribute("javax.portlet.response");
		
		if(dataType.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_REQUEST_CT_ADMIN))
			response.setTitle(ResourceBundleUtils.getMessage("lbl.ct.contract.request.ct.admin",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, (new FacesScopes())
							.getUserLocale()));
		else
			response.setTitle(ResourceBundleUtils.getMessage("lbl.ct.contract.remove.ct.admin",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, (new FacesScopes())
							.getUserLocale()));
		
		
		
		
		
		setRequestCtAdminContractVer(ctReqDTO);
		/*End*/
	}
	
	
	
	/**
	 * This method used to validate suspend item and agency as per rule
	 */
	public void validateRequestCtAdminDetails(String outcome){
		getManagedBean().setAutoUpdate(true);
		/*Remark is mandatory during revise and reject*/
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
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
		
		/*Approver is mandatory for document submission. Please select at least one approver. */
		if((outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT))
				&& getManagedBean().getCtContract().getAssignedCtAdmin().getUserId() == null) {
			throw new EpException(MessageConst.ERROR_750054); 
		}
	}
	
	/*
	 * Method to set user role as per user login
	 * User role used in BPM to identify the task
	 */
	private String getUserRole() {
		String userRole = null;		
		
		/*contract admin submit*/
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_RA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_RA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_APPROVER_RA_HT_NAME)) {
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY) && getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY) && getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
			}
		} else if (getManagedBean().getTaskName() != null
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_RA_HT_NAME)) {
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
			}
		}
		
	
		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
}
