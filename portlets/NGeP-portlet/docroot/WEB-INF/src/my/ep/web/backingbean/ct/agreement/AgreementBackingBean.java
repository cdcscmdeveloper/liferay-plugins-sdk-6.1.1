/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.agreement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.agreement.CTAgreementDataType;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementContent;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.backingbean.sc.loa.DirectLOABackingBean;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.agreement.AgreementManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.RoleHelper;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.template.MergeDocx;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import org.apache.commons.collections.CollectionUtils;

/**
 * Backing Bean For Agreement
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean(name = "agreementBackingBean")
@RequestScoped
public class AgreementBackingBean extends ContractBaseBackingBean<AgreementManagedBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AgreementBackingBean.class);
	private static final String CONTRACT_PAGE = "/xhtml/ct/fulfilmentdetails/creation/fulfilment.xhtml?faces-redirect=true";
	private ContractBackingBean ctBB = null;

	private String agreementZipFileName = null;
	private String agreementZipFilePath = null;

	//26686 start
	private String contractAmtChange = null;
	private String gstAmtChange = null;
	private String sTaxAmtChange = null;	
	//26686 end

	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public AgreementBackingBean() {
		if(ctBB == null){
			ctBB = new ContractBackingBean();
		}

		/* Initialization of button */
		if (getManagedBean().getOutComes() == null) {
			getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
	}

	/**
	 * This  method used to initialize approver list.
	 */
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(), new ArrayList<PmUser>()));
		}
	}

	/**
	 * The method used to retrieve available approver(s).
	 * 
	 * @return sourceList  the approver list
	 */
	private List<PmUser> getAvailableApproverList() {
		IUserSO userSO = null;
		List<PmUser> sourceList = new ArrayList<PmUser>();
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());

			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			if (resDTO.getUserOrgList().size() > 0) {
				reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			}
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
			} else { // Central Contract
				reqDTO.setRoleCode(RoleCode.BPK_CC_APPROVER.value());
			}
			resDTO = userSO.retrieveUserListByRole(reqDTO);

			List<PmUser> list = resDTO.getUserList();
			for (PmUser user : list) {
				sourceList.add(user);
				getManagedBean().getUserMap().put(user.getUserId(), user);
			}
		} catch (Exception e) {
			log.error(e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
		return sourceList;
	}

	/**
	 * The method used to clear the previous viewed information before
	 * rendering the screen.
	 */
	public void preRenderAgreement() {
		renewSesionObject();
		ctBB.renewSesionObject();//Bug 34142, 15851 - shaqimi 06/12/2022
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"agreementTab.xhtml?faces-redirect=true");
	}

	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		IContractSO contractSO = null;
		IAgreementSO agreementSO = null;
		ContractAgreementReqDTO ctReqDTO = null;
		ContractAgreementResDTO resDTO = null;
		ContractReqDTO conReqDTO = null;

		try {
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			
			//Bug 34142, 15851 - shaqimi start 04/12/2022
			if(!ctBB.getManagedBean().isInPage()){
				if(ctBB.getManagedBean().isBackFromSCL() || ctBB.getManagedBean().isBackFromContract()){
					getManagedBean().setSelectedTabIndex(0);
					ctBB.getManagedBean().setBackFromSCL(false);
					ctBB.getManagedBean().setBackFromContract(false);
				}
				
				if(taskId != null && !taskId.isEmpty()){
					ctBB.getManagedBean().setTaskIdFromAgreementTaskList(taskId);
				} else {
					if(ctBB.getManagedBean().getTaskIdFromAgreementTaskList() != null && !ctBB.getManagedBean().getTaskIdFromAgreementTaskList().isEmpty()){
						taskId = ctBB.getManagedBean().getTaskIdFromAgreementTaskList();
					}
				}
				ctBB.getManagedBean().setInPage(true);
			}
			//Bug 34142, 15851 - shaqimi end 04/12/2022
			
			if (taskId == null) {
				//Enhancement 15851 - shaqimi start
				if(!getManagedBean().isFromViewSCL()){
					return;
				} else {
					agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
					ctReqDTO = new ContractAgreementReqDTO();
					resDTO = new ContractAgreementResDTO();
					
					ctReqDTO.setContractId(getManagedBean().getCtContract().getContractId());
					ctReqDTO.setDocId(getManagedBean().getContractVer().getContractVerId());
					ctReqDTO.setAgreementId(getManagedBean().getCtAgreement().getAgreementId());
					ctReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);

					resDTO = agreementSO.retrieveAgreement(ctReqDTO); 
					setCtAgreementDetails(resDTO);
					
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_DRAFT_AGREEMENT_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_AGREEMENT_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_AGREEMENT_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_AC_HT_NAME)) {
						getManagedBean().setFinalAgreement(true);
					}
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_AGREEMENT_HT_NAME)) {
						getManagedBean().setApproveFinalAgreement(true);
					}
					
					getManagedBean().setFromViewSCL(false);
					return;
				}
				//Enhancement 15851 - shaqimi end
			}
			
			//Bug 34142, 15851 - shaqimi start 04/12/2022
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
			
			if (ctBB.getManagedBean().getDocTypeFromAgreementTaskList() != null){
				ctReqDTO = new ContractAgreementReqDTO();
				
				ctReqDTO.setDocId(ctBB.getManagedBean().getDocIdFromAgreementTaskList());
				ctReqDTO.setDocType(ctBB.getManagedBean().getDocTypeFromAgreementTaskList());
				ctReqDTO.setContractId(ctBB.getManagedBean().getContractIdFromAgreementTaskList());
				ctReqDTO.setAgreementId(ctBB.getManagedBean().getAgreementIdFromAgreementTaskList());
				
				resDTO = agreementSO.retrieveAgreement(ctReqDTO);
				setCtAgreementDetails(resDTO);
				ctBB.getManagedBean().setBackFromSCL(false);
				ctBB.getManagedBean().setBackFromContract(false);
				return;
			}
			
			ctReqDTO = new ContractAgreementReqDTO();
			//Bug 34142, 15851 - shaqimi end 04/12/2022
			CTAgreementDataType dataType =  super.getBpmTaskDetails(CTAgreementDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});		
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setDocId(new Long(dataType.getDocId()));
			ctReqDTO.setAgreementId(new Long(dataType.getAgreementId()));
			getManagedBean().setAgreementType(dataType.getAgreementType() == null  ?  "" : dataType.getAgreementType());
			resDTO = new ContractAgreementResDTO();
			// If agreement type = supplemental, set the supplemental agreement; else set the agreement
			if(getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
				ctReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);	
				if (dataType.getAgreementId().intValue() == 0) {
					ContractAgreementReqDTO contractAgreementReqDTO = new ContractAgreementReqDTO();
					contractAgreementReqDTO.setContractVerId(new Long(dataType.getDocId()));
					ContractAgreementResDTO contractAgreementResDTO = agreementSO.retrieveSupAgreementByContractVersion(contractAgreementReqDTO); 
					if (contractAgreementResDTO.getAgreement() == null) { 
						conReqDTO = new ContractReqDTO();
						conReqDTO.setContractVerId(new Long(dataType.getDocId()));
						conReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);
						contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
						ContractResDTO conResDTO = contractSO.retrieveContractVerDtls(conReqDTO);
						ctReqDTO.setCtContractVer(conResDTO.getCtContractVer());
						resDTO = agreementSO.setSupplementalAgreement(ctReqDTO);
						getManagedBean().setContractVer(conResDTO.getCtContractVer());
						getManagedBean().setAgreementContractVer(conResDTO.getCtContractVer());
						setCtAgreementDetails(resDTO);
						saveAgreement(BpmOutcomeConst.BPM_SAVE);
					} else {
						ctReqDTO.setAgreementId(contractAgreementResDTO.getAgreement().getAgreementId());
						resDTO = agreementSO.retrieveAgreement(ctReqDTO); 
						setCtAgreementDetails(resDTO);
					}
				} else {
					resDTO = agreementSO.retrieveAgreement(ctReqDTO);
					setCtAgreementDetails(resDTO);
				}
			} else {
				ctReqDTO.setDocType(ParameterConst.DOCTYPE_AGREEMENT);
				resDTO = agreementSO.retrieveAgreement(ctReqDTO);
				setCtAgreementDetails(resDTO);
			}
			
			//Bug 34142, 15851 - shaqimi start 04/12/2022
			ctBB.getManagedBean().setDocIdFromAgreementTaskList(ctReqDTO.getDocId());
			ctBB.getManagedBean().setDocTypeFromAgreementTaskList(ctReqDTO.getDocType());
			ctBB.getManagedBean().setContractIdFromAgreementTaskList(ctReqDTO.getContractId());
			ctBB.getManagedBean().setAgreementIdFromAgreementTaskList(ctReqDTO.getAgreementId());
			//Bug 34142, 15851 - shaqimi end 04/12/2022
			
//#28715 - Remove Code
//			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FINALIST_AGREEMENT_HT_NAME)
//					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FINALIST_AGREEMENT_HT_NAME)
//					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FINALIST_AGREEMENT_HT_NAME)) {
//				getManagedBean().setFinalAgreement(true);
//			}
//			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FINALIST_AGREEMENT_HT_NAME)) {
//				getManagedBean().setApproveFinalAgreement(true);
//			}
			//#28715 -Start
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_DRAFT_AGREEMENT_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_AGREEMENT_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_AGREEMENT_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_AC_HT_NAME)) {
				getManagedBean().setFinalAgreement(true);
			}
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_AGREEMENT_HT_NAME)) {
				getManagedBean().setApproveFinalAgreement(true);
			}
			//#28715 -End
		} catch (Exception ex) {
			log.error(ex);
		} finally {
			contractSO = null;
			agreementSO = null;
			ctReqDTO = null;
			conReqDTO = null;
		}
	}

	/**
	 * The method used to set the agreement details information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for agreement
	 */
	private void setCtAgreementDetails(ContractAgreementResDTO resDTO) {
		if(ctBB.getManagedBean().getDeleteAttachmentList() != null ){
			ctBB.getManagedBean().getDeleteAttachmentList().clear();
		}
		if (resDTO.getAgreement() != null) {
			getManagedBean().setCtAgreement(resDTO.getAgreement());
			getManagedBean().setCtContract(resDTO.getAgreement().getCtContract());
			getManagedBean().setRemarkList(resDTO.getCtRemarks());
			getManagedBean().setAttachmentList(resDTO.getCtAttachments());

			if (getManagedBean().getContractVer() == null) {
				getManagedBean().setContractVer(resDTO.getCtContractVer());
			}
			if (getManagedBean().getAgreementContractVer() == null) {
				getManagedBean().setAgreementContractVer(resDTO.getAgreementContractVer());
			}
			if (getManagedBean().getCtContract().getFulfillmentType() == null) {
				getManagedBean().getCtContract().setFulfillmentType(new PmParameter());
			}
			if (!getManagedBean().getCtAgencySigner().isEmpty()) {
				getManagedBean().getCtAgencySigner().clear();
			}
			if (!getManagedBean().getCtSupplierSigner().isEmpty()) {
				getManagedBean().getCtSupplierSigner().clear();
			}
			if (!getManagedBean().getCtSignAgencyWitness().isEmpty()) {
				getManagedBean().getCtSignAgencyWitness().clear();
			}
			if (!getManagedBean().getCtSupplierWitness().isEmpty()) {
				getManagedBean().getCtSupplierWitness().clear();
			}

			for (CtAgreementParty ctAgreementParty : getManagedBean().getCtAgreement().getCtSignParties()) {

				if (ctAgreementParty.getAgreementPartyType().getParameterCode().equalsIgnoreCase(ParameterTypeConst.AGREEMENT_AGENCY_SIGNER)) {
					if (ctAgreementParty.getOrgProfile() != null && ctAgreementParty.getOrgProfile().getOrgTypeCode() != null) {
						/* If agency singer only ministry*/
						if (ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.MINISTRY.value())) {
							ctAgreementParty.setMinistryName(retrieveOrgProfileByProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
						} else {
							ctAgreementParty.setMinistryName(retrieveMinistryByOrgProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
						}
						/* If agency singer  ministry and pengawai pengawal */
						if (ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DEPARTMENT.value())
								|| ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DIVISION.value())) {
							ctAgreementParty.setPengawaiPengawalName(retrievePegawaiPengawalByOrgProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));	
						} else if (ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.PENGAWAI_PENGAWAL.value())) {
							ctAgreementParty.setPengawaiPengawalName(retrieveOrgProfileByProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
						}
						/* If agency singer  jabatan */
						if (ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DIVISION.value())) {
							ctAgreementParty.setJabatanName(retrieveJabatanByOrgProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
						} else if(ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DEPARTMENT.value())) {
							ctAgreementParty.setJabatanName(retrieveOrgProfileByProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
						}
						/* If agency singer  ptj */
						if (ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DIVISION.value())) {
							ctAgreementParty.setPtjName(ctAgreementParty.getOrgProfile().getOrgName());
						}
					}
					if (ctAgreementParty.getOfficer() == null) {
						ctAgreementParty.setOfficer(new PmUser());
					}
					getManagedBean().getCtAgencySigner().add(ctAgreementParty);		 		
				} else if (ctAgreementParty.getAgreementPartyType().getParameterCode().equalsIgnoreCase(ParameterTypeConst.AGREEMENT_AGENCY_WITNESS)) {
					getManagedBean().getCtSignAgencyWitness().add(ctAgreementParty);	 		
				} else if (ctAgreementParty.getAgreementPartyType().getParameterCode().equalsIgnoreCase(ParameterTypeConst.AGREEMENT_SUPPLIER_SIGNER)) {
					if (ctAgreementParty.getOfficer() == null) {
						ctAgreementParty.setOfficer(new PmUser());
					}
					getManagedBean().getCtSupplierSigner().add(ctAgreementParty);
				} else {
					getManagedBean().getCtSupplierWitness().add(ctAgreementParty);
				}
			}
		} else {
			getManagedBean().setCtAgreement(new CtAgreement());
		}

		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
		// Retrieve Approver List Information
		if (isApproverSetting()) {
			getManagedBean().getApproverModel().setSource(getAvailableApproverList());
			if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
				getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
				List<PmUser> invalidApproverList = new ArrayList<PmUser>();
				for (PmUser approverUser : resDTO.getApproverList())  {
					Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();				
					boolean isValidApprover = false;
					while (sourceIt.hasNext()) {
						PmUser user = sourceIt.next();
						if (user.getUserId().longValue() == approverUser.getUserId().longValue()) {
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

		String docType = ParameterConst.DOCTYPE_AGREEMENT;
		if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
			docType = ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT;
		}

		getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
				ModuleCodeConst.CONTRACT_MANAGEMENT, docType,getManagedBean().getCtContract().getContractNo()));
		initializeRemarks();
		ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		ctBB.initializeRemarkAndAttachement(getManagedBean().getCtAgreement().getCtWorkflowStatus(),
				getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());

		// Retrieve bond req flag
		if (!getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
				&& !getManagedBean().getCtAgreement().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			if (getManagedBean().getCtAgreement().isPendingVerificationAmendment()
					&& getManagedBean().getCtAgreement().getCtAmendment() != null) {
				getManagedBean().setBondReq(getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().isBondReq());
			} else {
				getManagedBean().setBondReq(getManagedBean().getContractVer().getCtContractAmount().isBondReq());
			}
		} else {
			getManagedBean().setBondReq(false);
		}

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
		

		// Check for valid Bond Attachment
		if (getManagedBean().isBondReq()) {
			for (CtPerformanceBond ctPerformanceBond : getManagedBean().getContractVer().getCtContractAmount().getCtPerformanceBonds()) {
				if (StringUtils.isNotBlank(ctPerformanceBond.getFilePath())
						&& !ctPerformanceBond.getFilePath().equalsIgnoreCase("N/A")) {
					getManagedBean().setValidBondAttachment(true);
					break;
				}
			}
		} else {
			getManagedBean().setValidBondAttachment(false);
		}
		
		// #24333 - Start
		if (getManagedBean().getCtAgreement().getCtAmendment().isContractExtend()) {
			if (getManagedBean().getContractVer().getCtContractAmounts() != null) {
				for (CtContractAmount contractAmt : resDTO.getCtContractVer().getCtContractAmounts()) {
					if (contractAmt.getEffDate().getTime() <= getManagedBean().getContractVer().getCtContractValue()
							.getExpDate().getTime()
							&& contractAmt.getExpDate().getTime() >= getManagedBean().getContractVer()
									.getCtContractValue().getExpDate().getTime()
							&& (contractAmt.getIsBondAttachment() == ContractConst.STATUS_COMPLETE)
							&& contractAmt.isBondReq()) {
						getManagedBean().setBondAttachmentCompleted(true);
						break;
					}
				}
			}
		}
		// #24333 - End

		// Check for migrated contract
		if (getManagedBean().getCtAgreement().getCtAmendment() == null
				&& (getManagedBean().getCtContract().getContractNo().startsWith("Z")
						|| getManagedBean().getCtContract().getContractNo().startsWith("M"))) {
			getManagedBean().setMigrationContract(true);
		} else {
			getManagedBean().setMigrationContract(false);
		}
	}

	/**
	 * The method used to save the agreement.
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully. 
	 *
	 * @param  action  the user action on this agreement
	 * @return the navigate URL 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public String saveAgreement(String action) throws Exception {
		IAgreementSO agreementSO = null;
		try {
			
			validateAgreement(action);

			ContractAgreementReqDTO reqDTO = new ContractAgreementReqDTO();
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);

			if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
				reqDTO.setAgreementType(ContractConst.SUPPLEMENTAL_AGREEMENT);
				reqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);   
			} else {
				reqDTO.setAgreementType(ContractConst.AGREEMENT);
				reqDTO.setDocType(ParameterConst.DOCTYPE_AGREEMENT);
			}
			
			getManagedBean().getCtAgreement().setCtContract(getManagedBean().getCtContract());
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setOutcome(action);
			reqDTO.setUserRole(getUserRole());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());

			if(getManagedBean().getCtAgreement().getCtSignParties().size() > 0) {
				getManagedBean().getCtAgreement().getCtSignParties().clear();
			}

			getManagedBean().getCtAgreement().getCtSignParties().addAll(getManagedBean().getCtAgencySigner());
			getManagedBean().getCtAgreement().getCtSignParties().addAll(getManagedBean().getCtSupplierSigner());
			getManagedBean().getCtAgreement().getCtSignParties().addAll(getManagedBean().getCtSignAgencyWitness());
			getManagedBean().getCtAgreement().getCtSignParties().addAll(getManagedBean().getCtSupplierWitness());

			// Generate agreement File
			//#28715
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_AGREEMENT_HT_NAME)
					&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				generateAgreementFile();
				getManagedBean().getCtAgreement().setFilePath(DocumentRepositoryUtils.getDBFilePath(agreementZipFilePath.replace("\\", "/")));
				getManagedBean().getCtAgreement().setFileName(agreementZipFileName);
			}

			//Long statusId = getManagedBean().getCtAgreement().getStatus().getStatusId();
			boolean isBrandNew = getManagedBean().getCtAgreement().isBrandNew();
			reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());
			reqDTO.setDeleteAttachmentList(ctBB.getManagedBean().getDeleteAttachmentList());
			reqDTO.setUserMap(getManagedBean().getUserMap());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setCtContractVer(getManagedBean().getAgreementContractVer());
			reqDTO.setAgreement(getManagedBean().getCtAgreement());
			if (this.getManagedBean().getPmDigiSign() != null) {
				reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
			}

			ContractAgreementResDTO resDTO = agreementSO.saveAgreement(reqDTO);
			if (resDTO.isSuccessful()) {
				log.debug("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				String prefix = "";
				String nextRole = null;
				//#28715 -Start
//				if(statusId.longValue()==ContractStatusConst.AC_PENDING_AGREEMENT_CREATION.longValue() || statusId.longValue()==ContractStatusConst.AC_PENDING_DRAFT_AGREEMENT_APPROVAL.longValue() || 
//						statusId.longValue()==ContractStatusConst.AC_PENDING_DRAFT_AGREEMENT_REVISION.longValue() || statusId.longValue()==ContractStatusConst.AC_PENDING_DRAFT_AGREEMENT_REVISION_APPROVAL.longValue() || 
//						statusId.longValue()==ContractStatusConst.SA_PENDING_SUPPLEMENTAL_AGREEMENT_CREATION.longValue() || statusId.longValue()==ContractStatusConst.SA_PENDING_DRAFT_SUPPLEMENTAL_AGREEMENT_APPROVAL.longValue() || 
//						statusId.longValue()==ContractStatusConst.SA_PENDING_DRAFT_SUPPLEMENTAL_AGREEMENT_REVISION.longValue() || statusId.longValue()==ContractStatusConst.SA_PENDING_DRAFT_SUPPLEMENTAL_AGREEMENT_REVISION_APPROVAL.longValue()) {
//					prefix = ResourceBundleUtils.getMessage("lbl.ct.draft", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
//				}
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					messageId = MessageConst.INFO_700021;
					if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
							ParameterConst.CONTRACT_TYPE_MINISTRY)) {
						nextRole = RoleCode.CT_APPROVER.value();
					} else { // Central Contract
						nextRole = RoleCode.BPK_CC_APPROVER.value();
					}
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_COMPLETE)) {
					messageId = MessageConst.INFO_700001;
				}

				if (!isBrandNew) {
					if(StringUtils.isNotBlank(nextRole)){
						PmRoleDesc roleDesc = RoleHelper.getInstance().getRoleDescByCode(nextRole);
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null,
										MessageUtils.getMessage(messageId, 
												new String[]{prefix+" "+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, reqDTO.getDocType()), getManagedBean().getCtContract().getContractNo(),roleDesc.getRoleName()}, 
												MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					} else {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null,
										MessageUtils.getMessage(messageId,
												new String[] {prefix+" "+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, reqDTO.getDocType()), getManagedBean().getCtContract().getContractNo()},
												MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					}
				}

				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)
						|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null)) {
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					ContractAgreementReqDTO agreementReqDTO = new ContractAgreementReqDTO();
					agreementReqDTO.setAgreementId(resDTO.getAgreement().getAgreementId());
					if (resDTO.getAgreement().getCtAmendment() == null) {
						agreementReqDTO.setDocType(ParameterConst.DOCTYPE_AGREEMENT);
					} else {
						agreementReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);
					}
					ContractAgreementResDTO agreementResDTO = agreementSO.retrieveAgreement(agreementReqDTO);
					setCtAgreementDetails(agreementResDTO);
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			agreementSO = null;
		}
		return null;
	}

	/**
	 * The method used for validating the agreement information.
	 * A warning or error message will display on screen when the validation
	 * is failed. User required to modify the information accordingly
	 * to proceed.
	 *
	 * @param  outcome  the user action on this agreement. The validation will
	 * 				    invoke base on the action
	 */
	private void validateAgreement(String outcome) {
		// Validate for Revise's Remarks
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
			if (getManagedBean().getRemarkList().get(0).getRemark() == null  
					|| getManagedBean().getRemarkList().get(0).getRemark().length() == 0) {
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		}

		if (!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
			if (getManagedBean().getAgreementContractVer().getContractPhysicalNo() == null  
					|| getManagedBean().getAgreementContractVer().getContractPhysicalNo().isEmpty()) {
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.physical.no", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}

			// Validation for Agency Signer
			if (getManagedBean().getCtAgencySigner().size() == 0) {
				getManagedBean().setSelectedTabIndex(1);
				throw new EpException(MessageConst.ERROR_100023, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.agency.signer", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			if (getManagedBean().getCtAgencySigner() != null && getManagedBean().getCtAgencySigner().size() > 0) {
				for (CtAgreementParty agencySigner : getManagedBean().getCtAgencySigner()) {
					if (agencySigner.getOfficerIc() == null) {
						getManagedBean().setSelectedTabIndex(1);
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.contract.signer.agency", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			// Validation for Agency Witness
			if (getManagedBean().getCtSignAgencyWitness().size() == 0) {
				getManagedBean().setSelectedTabIndex(1);
				throw new EpException(MessageConst.ERROR_100023, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.agency.witness", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			for (CtAgreementParty agencyWitness : getManagedBean().getCtSignAgencyWitness()) {
				if (agencyWitness.getOfficerName() == null || agencyWitness.getOfficerName().isEmpty()) {
					getManagedBean().setSelectedTabIndex(1);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.name.agency.witness", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});

				}
				if (agencyWitness.getOfficerIc() == null || agencyWitness.getOfficerIc().isEmpty()) {
					getManagedBean().setSelectedTabIndex(1);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.ic.agency.witness", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (agencyWitness.getOfficerPosition() == null || agencyWitness.getOfficerPosition().isEmpty()) {
					getManagedBean().setSelectedTabIndex(1);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.position.agency.witness", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
			// Validation for Supplier Signer
			if (getManagedBean().getCtSupplierSigner().size() == 0) {
				getManagedBean().setSelectedTabIndex(1);
				throw new EpException(MessageConst.ERROR_100023, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.supplier.signer", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} 
			if (getManagedBean().getCtSupplierSigner() != null && getManagedBean().getCtSupplierSigner().size() > 0) {
				for (CtAgreementParty supplierSigner : getManagedBean().getCtSupplierSigner()) {
					if (supplierSigner.getOfficerIc() == null) {
						getManagedBean().setSelectedTabIndex(1);
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.contract.signer.supplier", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			// Validation for Supplier Witness
			if (getManagedBean().getCtSupplierWitness().size() == 0) {
				getManagedBean().setSelectedTabIndex(1);
				throw new EpException(MessageConst.ERROR_100023,
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.supplier.witness", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			for (CtAgreementParty supplierWitness : getManagedBean().getCtSupplierWitness()) {
				if (supplierWitness.getOfficerName() == null || supplierWitness.getOfficerName().isEmpty()) {
					getManagedBean().setSelectedTabIndex(1);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.name.supplier.witness", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});

				}
				if (supplierWitness.getOfficerIc() == null || supplierWitness.getOfficerIc().isEmpty()) {
					getManagedBean().setSelectedTabIndex(1);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.ic.supplier.witness", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (supplierWitness.getOfficerPosition() == null || supplierWitness.getOfficerPosition().isEmpty()) {
					getManagedBean().setSelectedTabIndex(1);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.position.supplier.witness", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
			// Validation for duplicate IC No
			checkDuplicateAgreementParty();

			// Validate for contract document
			if (getManagedBean().getCtAgreement().getContractDocumentAttachments().size() == 0) {
				getManagedBean().setSelectedTabIndex(2);
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attachment.contract.document", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}

			// Validate for Signed & Stamped Process
			if (getManagedBean().isFinalAgreement()) {
				if (getManagedBean().getAgreementContractVer().getSignedDate() == null){
					getManagedBean().setSelectedTabIndex(2);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.signed.date", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (getManagedBean().getCtAgreement().getSignedPageName() == null) {
					getManagedBean().setSelectedTabIndex(2);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.attachment.signed.page", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (getManagedBean().getCtAgreement().getStampedPageName() == null) {
					getManagedBean().setSelectedTabIndex(2);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.attachment.stamped.page", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
		}
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && isApproverSetting() 
				&& getManagedBean().getApproverModel().getTarget().size() == 0) {
			//Approver is mandatory for document submission. Please select at least one approver.
			getManagedBean().setSelectedTabIndex(3);
			throw new EpException(MessageConst.ERROR_750010);
		}

	/**	// Contract Approver cannot approve the draft agreement if:-
		// -FD Creation still in process.
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) 
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_DRAFT_AGREEMENT_HT_NAME)) {			
			if (getManagedBean().getCtContract().getLatestContractVer() == null) { // FD creation in progress
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_750041, 
						new String[]{ResourceBundleUtils.getMessage(getManagedBean().getCtContract().getContractNo(), 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}		
		} **/

		// Contract Admin cannot submit the finalist agreement if:-
		// -Performance bond not complete
		// -FD Update/FD Creation/Change Bank process still in process. 
		//#28715
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_AC_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_DRAFT_AGREEMENT_HT_NAME))) {

			IAgreementSO agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
			ContractAgreementReqDTO reqDTO = new ContractAgreementReqDTO();
			reqDTO.setContractId(getManagedBean().getCtContract().getContractId());
			ContractAgreementResDTO resDTO = agreementSO.validatePendingFinalistApproval(reqDTO);

			if (!resDTO.isFdComplete()) {
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_750041, 
						new String[]{ResourceBundleUtils.getMessage(getManagedBean().getCtContract().getContractNo(), 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (!resDTO.isBondComplete()) {
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_750043, 
						new String[]{ResourceBundleUtils.getMessage(getManagedBean().getCtContract().getContractNo(), 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (getManagedBean().getCtAgreement().getCtAmendment().isContractExtend() && !getManagedBean().isBondAttachmentCompleted()) { //#24333 - Start
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_750043, new String[] { ResourceBundleUtils.getMessage(getManagedBean().getCtContract()
						.getContractNo(), ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
			}
			//#24333 - End
		}

		// Contract Approver cannot approve the final agreement if:-
		// -Disclaimer is not checked
		//#28715
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) 
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_AGREEMENT_HT_NAME)) {			
			if (!getManagedBean().isAgreementDisclaimer()) {
				getManagedBean().setSelectedTabIndex(2);
				throw new EpException(MessageConst.ERROR_750042);
			}		
		}
	}

	/**
	 * The method used to check duplicated identification no of agreement parties.
	 * A error will display on screen when there is duplicated identification no.
	 */
	public void checkDuplicateAgreementParty() {
		List<CtAgreementParty> ctAgreementPartyList = new ArrayList<CtAgreementParty>();
		ctAgreementPartyList.addAll(getManagedBean().getCtAgencySigner());
		ctAgreementPartyList.addAll(getManagedBean().getCtSignAgencyWitness());
		ctAgreementPartyList.addAll(getManagedBean().getCtSupplierSigner());
		ctAgreementPartyList.addAll(getManagedBean().getCtSupplierWitness());

		Map<String, String> checkedAgreementPartyMap =  new HashMap<String, String>();
		for (CtAgreementParty ctAgreementParty : ctAgreementPartyList) {
			if (checkedAgreementPartyMap.size() > 0) {
				if (ctAgreementParty.getOfficerIc() != null 
						&& checkedAgreementPartyMap.containsKey(ctAgreementParty.getOfficerIc())) {
					getManagedBean().setSelectedTabIndex(1);
					throw new EpException(MessageConst.ERROR_750013, new String[]{
							ctAgreementParty.getOfficerIc(),
							getParameterDesc(ParameterTypeConst.AGREEMENT_PARTY_TYPE,
									checkedAgreementPartyMap.get(ctAgreementParty.getOfficerIc())),
									getParameterDesc(ParameterTypeConst.AGREEMENT_PARTY_TYPE,
											ctAgreementParty.getAgreementPartyType().getParameterCode())});
				}
			}
			checkedAgreementPartyMap.put(
					ctAgreementParty.getOfficerIc(), ctAgreementParty.getAgreementPartyType().getParameterCode());			
		}		
	}

	/**
	 * The method used to authenticate user during submit the agreement.
	 * This method will invoke a confirmation dialog for user authentication.
	 * This method will call save agreement method once authentication success.
	 * 
	 * @param  outcome  the user action on this agreement
	 * @return result of the saveAgreement process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
	 */
	public String gpkiSignAction(String outcome) throws Exception {
		
		//#29417 start
		Date signDate = getManagedBean().getAgreementContractVer()!=null ? getManagedBean().getAgreementContractVer().getSignedDate() : null;
		Date expDate = getManagedBean().getCtAgreement().getExpDate();
		getManagedBean().setButtonProcess(outcome);
		//#29417 end
		
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
			validateAgreement(outcome);
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getCtContract().getContractNo());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(getManagedBean().getContractVer().getDocType());
			sb.append(ContractConst.SEPRATE_LINE);
			//#28715
			if(getManagedBean().isApproveFinalAgreement()){
				sb.append(ContractConst.AGREEMENT_FINAL);
			} else {
				sb.append(ContractConst.AGREEMENT_DRAFT);
			}
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(outcome);
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			
			//#29417 start
			if(signDate != null && expDate != null && signDate.after(expDate)){	
				RequestContext.getCurrentInstance().execute("dateValidationExpGpki.show()");				
			}else{
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
			//#29417 end
			return null;
		}
		// 29417 - START
		else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) ){			
			if(signDate != null && expDate != null && signDate.after(expDate)){	
				RequestContext.getCurrentInstance().execute("dateValidationExp.show()");				
			}
			else {
				return this.saveAgreement(outcome);
			}
			return null;
		}
		// 29417 - END 
			
		else {	
			return this.saveAgreement(outcome);
		}
	}

	/**
	 * The method used to set value for security check
	 * before save the contract.
	 * 
	 * @param  pmDigiSign  an object of digital sign
	 * @return result of the saveContract process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
	 */
	public String preSubmitAgreement(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveAgreement(getManagedBean().getButtonAction());
	}

	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}

	/**
	 * The method used to set update value false for disable purpose.
	 */
	public void disableUpdate() {
		getManagedBean().setAutoUpdate(false);
	}	


	/**
	 * The Method used to set user role as per user login.
	 * This user role is required to identify the task.
	 * 
	 * @return the role of login user
	 */
	private String getUserRole() {
		String userRole = null;	
		//#28715
		//log.info("Check Task :"+getManagedBean().getTaskName().toString());
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_DRAFT_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_AC_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_AC_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FINALIST_AGREEMENT_HT_NAME)
//				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_DRAFT_AGREEMENT_HT_NAME)
//				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FINALIST_AGREEMENT_HT_NAME)
//				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_FINALIST_AGREEMENT_HT_NAME)
//				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)
				) {
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
				//#28715
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_AGREEMENT_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FINALIST_AGREEMENT_HT_NAME)
						)) {
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
	 * The method used for initializing the remarks of agreement signing.
	 */
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getCtAgreement().getSignAgreementRemark());
	}

	/**
	 * The method used for initializing the remarks of agreement signing.
	 * This method will create new remark object for every single new 
	 * process, else will display all remarks.
	 * 
	 * @param remarkList  the list of remarks to display. If the remarkList is null, 
	 *                    initiate a new remark
	 */
	private void initiateRemark(List<CtRemark> remarks) {
		boolean ctWorkflowStatusMatch = false;
		if (getManagedBean().getCtAgreement().getCtWorkflowStatus() != null) {
			if (remarks.size() > 0 && remarks.get(0).getCtWorkflowStatus() != null) {
				if (getManagedBean().getCtAgreement().getCtWorkflowStatus().getWorkflowStatusId()
						== remarks.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}

		if (remarks.size() == 0 || !ctWorkflowStatusMatch) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remarks.add(0, remark);
		}
	}

	/**
	 * The method used for generating a list of contract signer
	 * for agency as selection.
	 * 
	 * @param  agreementParty  an object of agreement party
	 * @return items		   the selection list
	 */
	public SelectItem[] getContractSignerDropdown(CtAgreementParty agreementParty) {		
		SelectItem[] items = null;
		IUserSO userSO = null;
		UserReqDTO reqDTO = null;
		UserResDTO resDTO = null;
		
		try {

			int i = 1;
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());

			if (agreementParty.getOrgProfile() != null && agreementParty.getOrgProfile().getOrgProfileId() != null) { 
				reqDTO.setOrgProfileId(agreementParty.getOrgProfile().getOrgProfileId());
			}
			
			//#26686 - start 
			BigDecimal approvalLimit;
			if (getManagedBean().getCtAgreement().getCtAmendment() != null && (getManagedBean().getCtAgreement().getCtAmendment().isValueChange()
					|| getManagedBean().getCtAgreement().getCtAmendment().isValueChangeByGst() || getManagedBean().getCtAgreement().getCtAmendment().isValueChangeByStax())) {
				setContractSignerChanges();					
				approvalLimit = setLimitContractSigner();
			} else {
				approvalLimit = getContractAmt().add(getGstAmt().add(getStaxAmt()));
			}					
			
			reqDTO.setApprovalLimit(approvalLimit);
			resDTO = userSO.retrieveUserListByRole(reqDTO);
			//#26686 - end

			List<PmUser> list = resDTO.getUserList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			for (PmUser pmUser : list) {
				items[i++] = new SelectItem(pmUser.getUserId(), pmUser.getUserName());
			}
		} finally {
			userSO = null;
			reqDTO = null;
			resDTO = null;
			//#26686 start- reset value from global variable
			contractAmtChange = null;
			gstAmtChange = null;
			sTaxAmtChange = null;
			//#26686 end
		}
		return items;
	}

	/**
	 * The method used for generating a list of contract signer
	 * for supplier as selection.
	 * 
	 * @return the selection list
	 */
	public SelectItem[] getSupplierSignerDropdown() {		
		if(getManagedBean().getSupplierSignerDropdown() == null ) {
			SelectItem[] items = null;
			ISupplierSearchSO supplierSearchSO;
			SupplierSearchReqDTO reqDTO = null;
			SupplierSearchResDTO resDTO = null;

			try {
				int i = 1;
				supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
				reqDTO = new SupplierSearchReqDTO();
				reqDTO.setSupplierId(getManagedBean().getCtContract().getSmSupplier().getSupplierId());
				resDTO = supplierSearchSO.retrieveContractSigners(reqDTO);

				items = new SelectItem[resDTO.getPersonnels().size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				for (SmPersonnel personnel : resDTO.getPersonnels()) {
					items[i++] = new SelectItem(personnel.getName(),personnel.getName());
					getManagedBean().getSupplierUserMap().put(personnel.getName(),personnel);
				} 			             
				getManagedBean().setSupplierSignerDropdown(items);

				//Reset the supplier  signer ic no and designation if record didn't exist in the drop down						    
				for (CtAgreementParty supplierSigner: getManagedBean().getCtSupplierSigner()) {
					if (supplierSigner.getOfficer() == null) {
						supplierSigner.setOfficer(new PmUser());
					}
					SmPersonnel personnel = getManagedBean().getSupplierUserMap().get(supplierSigner.getOfficerName());
					if (personnel == null) {
						supplierSigner.setOfficerIc(null);
						supplierSigner.setOfficerPosition(null);
					}
				}
			} finally {
				supplierSearchSO = null;
			}
		}
		return getManagedBean().getSupplierSignerDropdown();
	}

	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String goToContractView() {
		Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));

		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractVerId(contractVerId);
		ctReqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
		ctReqDTO.setViewOnly(true);
		ctBB.getManagedBean().setViewContract(true);
		ctBB.getManagedBean().setViewContractFromAgreement(true);//Bug 34142 - shaqimi 16112022
		ctBB.getManagedBean().setInPage(false);//Bug 34142 - shaqimi 04/12/2022
		ctBB.getManagedBean().setContractVerIdFromAgreementTaskList(contractVerId); //15851
		ContractDetailsBackingBean contractDetailsBB = new ContractDetailsBackingBean();
		contractDetailsBB.getManagedBean().setLoaContractVerList(null); //#15851
		contractDetailsBB.getManagedBean().setLoaContractVerId(null); //#15851
		contractDetailsBB.getManagedBean().setAsAtDate(null);
		contractDetailsBB.setValidateContractField(ctReqDTO);
		contractDetailsBB.getManagedBean().setSelectedTabIndex(0);
		contractDetailsBB.getManagedBean().setExternalPageUrl(FacesContext.getCurrentInstance().getViewRoot().getViewId()+"?faces-redirect=true"); //15851
		return CONTRACT_PAGE;
	}

	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public void goToLoaView(ScLoa scLoa) {
		//#15851 - start
		String slLoaNo = this.getManagedBean().getContractVer().getCtContractValue().getLoaNo();
		Long contractId = this.getManagedBean().getContractVer().getCtContract().getContractId();
		
		ctBB.getManagedBean().setInPage(false);//Bug 34142 - shaqimi 04/12/2022
		
		if(slLoaNo.substring(0,2).equals(ParameterConst.DOCTYPE_SUPPLEMENTAL_CONTRACT_LETTER)){
			AmendmentBackingBean abb = new AmendmentBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();

			//Bug 34142, 15851 - shaqimi start 04/12/2022
			if(ctBB.getManagedBean().getTaskIdFromAgreementTaskList() != null){
				ctBB.getManagedBean().setAmendFromTaskList(true);
			}
			//Bug 34142, 15851 - shaqimi end 04/12/2022
			
			getManagedBean().setFromViewSCL(true);
			
			abb.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId()+"?faces-redirect=true"); //15851
			abb.getManagedBean().setTrackingDairyView(true);
			ctBB.getManagedBean().setViewSCL(true);
			ctBB.getManagedBean().setViewSclFromAgreement(true); //#15851
			//Bug 33096 - shaqimi start
			abb.getManagedBean().setSupplementalLoaNo(slLoaNo);
			//Bug 33096 - shaqimi end
			abb.gotoAmendViewPage(contractId, scLoa.getLoaId());
		}
		//#15851 - end
		else if(scLoa.getScLoiLoa().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_DIRECT_NEGO)) { //Direct Nego
			DirectLOABackingBean directLoaBackingBean = new DirectLOABackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			directLoaBackingBean.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			directLoaBackingBean.getManagedBean().setTrackingDairyView(true);
			directLoaBackingBean.getManagedBean().setIconForDL(true); //28854
			directLoaBackingBean.viewLOA(scLoa.getLoaId());
			ctBB.getManagedBean().setBackFromSCL(true);//Bug 34142, 15851 - shaqimi 04/12/2022
		} else {
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			loaDetailBackingBean.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			loaDetailBackingBean.getManagedBean().setTrackingDairyView(true);
			loaDetailBackingBean.viewLOA(scLoa.getLoaId());
			ctBB.getManagedBean().setBackFromSCL(true);//Bug 34142, 15851 - shaqimi 04/12/2022
		}
	}

	/**
	 * The method used to navigate to template doc screen
	 * for creation or view purpose.
	 * 
	 * @param agreementContent  an object of agreement content
	 * @return templateDocUrl   the navigate URL 
	 */
	public String gotoTemplateDoc(CtAgreementContent agreementContent) {
		String templateDocUrl = null;

		TemplateDocBackingBean tplDocBB = new TemplateDocBackingBean();
		tplDocBB.getManagedBean().setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_CONTRACT);
		FacesContext fc = FacesContext.getCurrentInstance();
		tplDocBB.getManagedBean().setExternalPageUrl(fc.getViewRoot().getViewId());
		tplDocBB.getManagedBean().setCtAgreementContent(agreementContent);
		// Create new template doc
		if (agreementContent.getTplDocMaster() == null) {
			templateDocUrl = tplDocBB.createNewTemplate();
		} else {
			templateDocUrl = tplDocBB.goToMaint(agreementContent.getTplDocMaster());
		}
		return templateDocUrl;
	}

	/**
	 * The method used to upload agreement content file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void fileUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(getManagedBean().getFileDirectory());
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath);
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();

		CtAttachment attachment = new CtAttachment();
		attachment.setFileName(item.getFileName());
		attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		attachment.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));

		if(event.getComponent().getId().equalsIgnoreCase("uploadSignAttachId")){			
			getManagedBean().getCtAgreement().getSignAgreementAttachments().add(attachment);
		} else if(event.getComponent().getId().equalsIgnoreCase("uploadContractDocumentId")) {
			getManagedBean().getCtAgreement().getContractDocumentAttachments().add(attachment);
		}
	}

	/**
	 * The method used to upload the attachment for agreement stamped page.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void uploadStampedPage(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/"+ attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath);
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
		getManagedBean().getCtAgreement().setStampedPageName(item.getFileName());
		getManagedBean().getCtAgreement().setStampedPagePath(DocumentRepositoryUtils.getDBFilePath(filePath));

		RequestContext.getCurrentInstance().execute("upStampedPageDlg.hide()");
	}

	/**
	 * The method used to upload the attachment for agreement signed page.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void uploadSignedPage(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/"+ attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath);
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
		getManagedBean().getCtAgreement().setSignedPageName(item.getFileName());
		getManagedBean().getCtAgreement().setSignedPagePath(DocumentRepositoryUtils.getDBFilePath(filePath));

		RequestContext.getCurrentInstance().execute("upAgrSignedPageDlg.hide()");
	}

	/**
	 * The method used to remove the attachment for agreement signed page.
	 */
	public void delUploadedStampedPage(){
		getManagedBean().getCtAgreement().setStampedPageName(null);
		getManagedBean().getCtAgreement().setStampedPagePath(null);
	}

	/**
	 * The method used to remove the attachment for agreement signed page.
	 */
	public void delUploadedSignedPage(){
		getManagedBean().getCtAgreement().setSignedPageName(null);
		getManagedBean().getCtAgreement().setSignedPagePath(null);
	}

	/**
	 * The method used to download the attached file.
	 * 
	 * @param  filePath	    will be download file path
	 * @param  fileName     will be download filename
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void clickDownloadIcon(CtAgreementContent agreementContent) throws Exception {
		if (agreementContent.getContentType().getParameterCode().equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_DOCUMENT_SAMPLE)
				|| agreementContent.getContentType().getParameterCode().equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_DOCUMENT)) {
			downloadDocFile(agreementContent);
		} else if (agreementContent.getContentType().getParameterCode().equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_GENERATED_FROM_FULFILMENT)) {
			downloadBondAttachment();
		} else {
			if (agreementContent.getFilePath() == null) {
				throw new EpException(MessageConst.INFO_100052,
						new String[] {agreementContent.getTitle()});
			}
			commonDownload(agreementContent.getFilePath(), agreementContent.getFileName());
		}
	}

	/**
	 * The method used to download the performance bond letter.
	 * 
	 * @throws IOException       if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadBondAttachment() throws   Exception {
		List<File> fileList = new ArrayList<File>();
		for (CtPerformanceBond ctPerformanceBond : getManagedBean().getContractVer().getCtContractAmount().getCtPerformanceBonds()) {
			if (StringUtils.isNotBlank(ctPerformanceBond.getFilePath())) {
				fileList.add(new File(DocumentRepositoryUtils.getBaseDir() + ctPerformanceBond.getFilePath()));
			}
		}

		if (fileList.size() > 1) {	// Generate zip file
			String zipFileName = ContractConst.TEMPLATE_AGREEMENT_PERFORMANCE_BOND.replaceAll("\\s+","") + ".zip";
			downloadFilesAsZip(zipFileName, fileList);
		} else {
			commonDownloadUtil(fileList.get(0).getPath(), fileList.get(0).getName());
		}		
	}

	/**
	 * The method used to download the agreement content.
	 * 
	 * @param  agreementContent  an object of agreement content
	 * @throws IOException       if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadDocFile(CtAgreementContent agreementContent) throws Exception {

		AttachmentFormat attachmentFormat = null;

		try {
			String fileName = agreementContent.getTitle().replaceAll("\\s+","") + ".docx";
			String content = null;
			String filePath = getManagedBean().getFileDirectory();
			File file = new File(filePath);
			file.mkdirs();

			attachmentFormat = new AttachmentDateFormat();
			filePath = filePath + attachmentFormat.format(fileName, null);

			if(agreementContent.getContentType().getParameterCode().equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_DOCUMENT)) {
				getTemplateContent(agreementContent);
				content = previewAction();
			} else {
				content = getContent(agreementContent.getTitle());
			}
			if (content != null) {
				DocumentGenerator.getInstance().generateDoc(filePath, content);
				commonDownload(DocumentRepositoryUtils.getDBFilePath(filePath), fileName);
			}
		}catch (UnsupportedEncodingException e) {
			log.error(e);
			throw new EpException(e.getMessage());
		} finally {
			attachmentFormat = null;
		}
	}

	/**
	 * The method used to download the template doc.
	 * 
	 * @param  agreementContent  an object of agreement content
	 * @throws IOException       if an input or output exception occurred
	 * @see    IOException
	 */
	private void getTemplateContent(CtAgreementContent agreementContent) throws Exception {
		if (agreementContent.getTplDocMaster() != null && 
				agreementContent.getTplDocMaster().getTplDocSections() != null && !agreementContent.getTplDocMaster().getTplDocSections().isEmpty()) {
			getManagedBean().setDocSectionMap(new HashMap<TplDocSection, TreeNode>());
			getManagedBean().setDocSectionTN(new DefaultTreeNode("root", "", null));

			Map<Long, TreeNode> secSeqMap = new HashMap<Long, TreeNode>();
			TreeNode node = null;

			Collections.sort(agreementContent.getTplDocMaster().getTplDocSections(), new Comparator<TplDocSection>() {
				public int compare(TplDocSection o1,  TplDocSection o2) {
					return o1.getDocSectionId().compareTo(o2.getDocSectionId());
				}});

			for (TplDocSection objL1 : agreementContent.getTplDocMaster().getTplDocSections()) {
				if (objL1.getParent() == null) {
					node = new DefaultTreeNode(objL1, getManagedBean().getDocSectionTN());
					getManagedBean().getDocSectionMap().put(objL1, node);
					secSeqMap.put(objL1.getDocSectionId(), node);
				} else {
					node = new DefaultTreeNode(objL1, secSeqMap.get(objL1.getParent().getDocSectionId()));
					getManagedBean().getDocSectionMap().put(objL1, node);
					secSeqMap.put(objL1.getDocSectionId(), node);
				}
			}
		}
	}	

	/**
	 * The method used for generating the content of agreement
	 * that defined from template.
	 * 
	 * @param titleName  the tile name of the template. Different title will have different template.
	 * @return the complete content of the template
	 * @throws Exception     if an exception occurred
	 * @see    Exception
	 */
	private String getContent(String titleName) throws Exception {
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
				CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		Calendar c = Calendar.getInstance();
		VelocityContext context = null;
		String vmFile = null;
		String content = null;

		if (ContractConst.TEMPLATE_AGREEMENT_COVER_PAGE.equalsIgnoreCase(titleName.trim())) {
			String imgUrl = getServerUrl()+ "NGeP-portlet/css/images/gov.png";
			context = new VelocityContext();
			vmFile = ContractConst.DOCPATH_AGREEMENT_COVER_PAGE;
			context.put("imgUrl", imgUrl);
			if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
				context.put("agreementTypeUpper", ContractConst.SUPP_AGREEMENT_STRING.toUpperCase());
			} else {
				context.put("agreementTypeUpper", ContractConst.AGREEMENT_STRING.toUpperCase());
			}
			context.put("contractTitle", getManagedBean().getCtContract().getContractName().toUpperCase());
			context.put("supplierName", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			context.put("supplierRegistrationNo", getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
			String physicalContactNo = getManagedBean().getCtContract().getContractNo();
			if (getManagedBean().getAgreementContractVer().getContractPhysicalNo() == null
					|| getManagedBean().getAgreementContractVer().getContractPhysicalNo().isEmpty()) {
				physicalContactNo = physicalContactNo + " / " + getManagedBean().getAgreementContractVer().getCtContractValue().getLoaNo();
			} else {
				physicalContactNo = physicalContactNo + " / " + getManagedBean().getAgreementContractVer().getContractPhysicalNo();
			}
			context.put("physicalContactNo", physicalContactNo);
			context.put("contractStartDate", df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
			context.put("contractEndDate", df.format(getManagedBean().getCtAgreement().getExpDate()));
			context.put("contractPeriod", getManagedBean().getAgreementContractVer().getCtContractValue().getContractPeriod() + "");

			BigDecimal overallAmount  = getContractAmt().add(getGstAmt().add(getStaxAmt()));
			BigDecimal lamt  = overallAmount.setScale(
					CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
			context.put("contractValue", nf.format(lamt) + "");
			context.put("contractValueText", NumberWordUtil.convert(lamt.toString()));
			if (getManagedBean().isBondReq()) {
				context.put("isBondReq", true);
			} else {
				context.put("isBondReq", false);
			}
			if (getManagedBean().isBondReq()) {
				if (getManagedBean().getCtAgreement().isPendingVerificationAmendment()
						&& getManagedBean().getCtAgreement().getCtAmendment() != null) {
					if (getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt() != null) {
						context.put("performanceBondValue", nf.format(getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt()) + "");
						context.put("performanceBondValueText", NumberWordUtil.convert(
								getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt().doubleValue() != 0 ? getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt().toString() : ""));
					}
				} else {
					if (getManagedBean().getContractVer().getCtContractAmount().getTotalBondAmt() != null) {
						context.put("performanceBondValue", nf.format(getManagedBean().getContractVer().getCtContractAmount().getTotalBondAmt()) + "");
						context.put("performanceBondValueText", NumberWordUtil.convert(
								getManagedBean().getContractVer().getCtContractAmount().getTotalBondAmt().doubleValue() != 0 ? getManagedBean().getContractVer().getCtContractAmount().getTotalBondAmt().toString() : ""));
					}
				}
			}
		} else if (ContractConst.TEMPLATE_AGREEMENT_PARTY.equalsIgnoreCase(titleName.trim())
				|| ContractConst.TEMPLATE_SUPP_AGREEMENT_PARTY.equalsIgnoreCase(titleName.trim())) {
			context = new VelocityContext();
			vmFile = ContractConst.DOCPATH_AGREEMENT_AGREEMENT_PARTY;

			if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
				context.put("agreementTypeUpper", ContractConst.SUPP_AGREEMENT_STRING.toUpperCase());
				context.put("agreementTypeLower", ContractConst.SUPP_AGREEMENT_STRING);
			} else {
				context.put("agreementTypeUpper", ContractConst.AGREEMENT_STRING.toUpperCase());
				context.put("agreementTypeLower", ContractConst.AGREEMENT_STRING);
			}
			context.put("userInputDate", c.get(Calendar.DATE) + "");
			context.put("userInputMonth", c.get(Calendar.MONTH) + "");
			context.put("currentYear", c.get(Calendar.YEAR) + "");
			context.put("ministryName", retrieveMinistryByOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
			context.put("jabatanName", retrieveJabatanByOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
			
			if (getManagedBean().getContractVer().getOwnerOrgAddress() != null) {
				String jabatanAddress = getFullAddress(getManagedBean().getContractVer().getOwnerOrgAddress());
				context.put("jabatanAddress", jabatanAddress);
			} else {
				context.put("jabatanAddress", "");
			}
			context.put("supplierName", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			context.put("supplierRegNo", getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
			if (getManagedBean().getContractVer().getCtContractValue().getSupplierAddress() != null) {
				String supplierAddress = getFullAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
				context.put("supplierAddress", supplierAddress);
			} else {
				context.put("supplierAddress", "");
			}
			context.put("contractTitle", getManagedBean().getCtContract().getContractName());

		} else if (ContractConst.TEMPLATE_AGREEMENT_SIGNING_PAGE.equalsIgnoreCase(titleName.trim())) {
			context = new VelocityContext();
			vmFile = ContractConst.DOCPATH_AGREEMENT_SIGNING_PAGE;
			context.put("ctAgencySignerList", getManagedBean().getCtAgencySigner());
			context.put("ctSignAgencyWitnessList", getManagedBean().getCtSignAgencyWitness());
			context.put("ctSupplierSignerList", getManagedBean().getCtSupplierSigner());
			context.put("ctSupplierWitnessList", getManagedBean().getCtSupplierWitness());

			context.put("currentYear", c.get(Calendar.YEAR) + "");
			context.put("ministryName", retrieveMinistryByOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()));

			context.put("companyName", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			context.put("companyRegNo", getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
		}

		if (vmFile != null && context != null) {
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(vmFile, context);
		}
		return content;
	}	
	
	/**
	 * Download files as a zip.
	 *
	 * @param zipFileName the zip file name
	 * @param fileList the list of the files that want to compress
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void downloadFilesAsZip(String zipFileName, List<File> fileList) throws IOException {
		PortletResponse portalresponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);

		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
				response.getOutputStream()));
		try {
			for (File file : fileList) {
				zos.putNextEntry(new ZipEntry(file.getName()));	
				// Get the file
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					zos.write(("ERROR: Could not find file " + file
							.getName()).getBytes());
					zos.closeEntry();
					continue;
				}
				BufferedInputStream fif = new BufferedInputStream(fis);
				// Write the contents of the file
				int data = 0;
				while ((data = fif.read()) != -1) {
					zos.write(data);
				}
				fif.close();
				zos.closeEntry();
			}
		} catch (Exception ex) {

		} finally {
			zos.close();
			response.flushBuffer();

			FacesContext.getCurrentInstance().responseComplete();
		}
	}

	/**
	 * The method used to generate agreement zip file.
	 */
	private void generateAgreementFile() {
		List<File> fileList = new ArrayList<File>();

		for (CtAttachment ctAttachment : getManagedBean().getCtAgreement().getContractDocumentAttachments()) {
			if (StringUtils.isNotBlank(ctAttachment.getFilePath())) {
				fileList.add(new File(DocumentRepositoryUtils.getBaseDir() + ctAttachment.getFilePath()));
			}
		}	
		try {
			if (fileList.size() == 1) {
				agreementZipFilePath = fileList.get(0).getPath();
				agreementZipFileName = fileList.get(0).getName();
			} else {	// Generate agreement zip file
				String zipFileName = "agreement.zip";
				if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
					zipFileName = "supplemental_agreement.zip";
				}
				File zipFile = new File(getManagedBean().getFileDirectory()+ zipFileName);		
				zipFile.createNewFile();
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
				for (File file : fileList) {
					zos.putNextEntry(new ZipEntry(file.getName()));	
					// Get the file
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
					} catch (FileNotFoundException fnfe) {
						zos.write(("ERROR: Could not find file " + file
								.getName()).getBytes());
						zos.closeEntry();
						continue;
					}
					BufferedInputStream fif = new BufferedInputStream(fis);
					// Write the contents of the file
					int data = 0;
					while ((data = fif.read()) != -1) {
						zos.write(data);
					}
					fif.close();
					zos.closeEntry();
				}
				zos.close();
				agreementZipFilePath = zipFile.getPath();
				agreementZipFileName = zipFile.getName();
			}
		} catch (IOException e) {
			log.error(e);
		}
	}

	/**
	 * The method used to retrieve the base URL.
	 * 
	 * @return url  the base URL
	 */
	private String getServerUrl() {
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		String url = requestURL.substring(0, requestURL.length() - httpServletRequest.getRequestURI().length()) +  httpServletRequest.getContextPath() + "/";
		return url;
	}

	/**
	 * The method used to generate the contract signer information once
	 * user select a representative for agency.
	 * A warning message will display on screen when duplicate
	 * representative is selected.
	 */
	public void setUserValueAgency(CtAgreementParty ctParty) {
		for (CtAgreementParty party : getManagedBean().getCtAgencySigner()) {
			if (ctParty.getOfficer() == null || ctParty.getOfficer().getUserId() == null) {
				ctParty.setOfficerIc(null);
				ctParty.setOfficerPosition(null);

			} else if (party.equals(ctParty) && ctParty.getOfficer().getUserId() != null) {
				IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(ctParty.getOfficer().getUserId());
				UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
				party.setOfficerName(resDTO.getPmUser().getUserName());
				party.setOfficerIc(resDTO.getPmUser().getIdentificationNo());
				party.setOfficerPosition(resDTO.getPmUser().getDesignation());
			}
		}
		checkDuplicateAgreementParty();
	}

	/**
	 * The method used for adding representative of witness(s)
	 * for agency party.
	 * A warning message will display on screen when duplicate
	 * representative is added.
	 */
	public void addAgencyWitnes() {
		if (getManagedBean().getCtSignAgencyWitness().size() > 0 ) {
			for (CtAgreementParty agencyWitness : getManagedBean().getCtSignAgencyWitness()) {
				if (StringUtils.isBlank(agencyWitness.getOfficerName())
						&& StringUtils.isBlank(agencyWitness.getOfficerIc())
						&& StringUtils.isBlank(agencyWitness.getOfficerPosition())) {
					return;
				}
			}
		}
		CtAgreementParty ctAgreementParty = new CtAgreementParty();
		ctAgreementParty.setAgreementPartyType(
				getParameterByTypeAndCode(ParameterTypeConst.AGREEMENT_PARTY_TYPE, 
						ParameterTypeConst.AGREEMENT_AGENCY_WITNESS));
		getManagedBean().getCtSignAgencyWitness().add(ctAgreementParty);
	}

	/**
	 * The method used for adding representative of signer(s)
	 * for supplier party.
	 * A warning message will display on screen when duplicate
	 * representative is added.
	 */
	public void addSupplierSigner() {
		CtAgreementParty ctAgreementParty = new CtAgreementParty();
		ctAgreementParty.setAgreementPartyType(
				getParameterByTypeAndCode(ParameterTypeConst.AGREEMENT_PARTY_TYPE, 
						ParameterTypeConst.AGREEMENT_SUPPLIER_SIGNER));
		if (ctAgreementParty.getOfficer() == null) {
			ctAgreementParty.setOfficer(new PmUser());
		}
		getManagedBean().getCtSupplierSigner().add(ctAgreementParty);
	}

	/**
	 * The method used to generate the contract signer information once
	 * user select a representative for supplier.
	 * A warning message will display on screen when duplicate
	 * representative is selected.
	 */
	public void setSupplierUserValue(CtAgreementParty ctParty) {
		SmPersonnel personnel = getManagedBean().getSupplierUserMap().get(ctParty.getOfficerName());

		if (personnel != null) {
			ctParty.setOfficerIc(personnel.getIdentificationNo());
			ctParty.setOfficerPosition(personnel.getDesignation());
			if (personnel.getPmUser() != null) {
				ctParty.setOfficer(personnel.getPmUser());
			}				
		} else {
			ctParty.setOfficerIc(null);
			ctParty.setOfficerPosition(null);
			ctParty.setOfficer(null);
		}
		checkDuplicateAgreementParty();
	}

	/**
	 * The method used for adding representative of witness(s)
	 * for supplier party.
	 * A warning message will display on screen when duplicate
	 * representative is added.
	 */
	public void addSupplierWitness() {		
		if (getManagedBean().getCtSupplierWitness().size() > 0) {
			for (CtAgreementParty supplierWitness : getManagedBean().getCtSupplierWitness()) {
				if (StringUtils.isBlank(supplierWitness.getOfficerName())
						&& StringUtils.isBlank(supplierWitness.getOfficerIc())
						&& StringUtils.isBlank(supplierWitness.getOfficerPosition())) {
					return;
				}
			}
		}		
		CtAgreementParty ctAgreementParty = new CtAgreementParty();
		ctAgreementParty.setAgreementPartyType(
				getParameterByTypeAndCode(ParameterTypeConst.AGREEMENT_PARTY_TYPE,
						ParameterTypeConst.AGREEMENT_SUPPLIER_WITNESS));
		getManagedBean().getCtSupplierWitness().add(ctAgreementParty);		
	}

	/**
	 * The method used to check whether approver setting tab menu is required to display.
	 * 
	 * @return true, if approver setting tab menu is required to display
	 *         false, if approver setting tab menu is not required
	 */
	public boolean isApproverSetting() {
		//#28715
		if (isContractAdmin() && (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_DRAFT_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_AC_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_AC_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_AGREEMENT_HT_NAME)
//				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FINALIST_AGREEMENT_HT_NAME)
//				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FINALIST_AGREEMENT_HT_NAME)
				)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The method used to verify the login user is central contract 
	 * administrator or ministry administrator.
	 * 
	 * @return true, if login user is central contract administrator or ministry administrator
	 *         false, if login user is not central contract administrator or ministry administrator
	 */
	public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}

	private String previewAction() {
		String combinedContentHtml ="";
		PmOrgProfile pmOrgProfile = getManagedBean().getCtContract().getOwnerOrgProfile();

		try {
			String outputFileName = "Document" + "_"+ DateUtils.dateToString("yyMMddHHmmssZ", new Date());
			combinedContentHtml = MergeDocx.mergePreviewAction(getManagedBean().getDocSectionTN(), outputFileName, "P");

			// Replace variables
			CommonUtils commonUtils = new CommonUtils();
			DecimalFormat af = new DecimalFormat(commonUtils.getDisplayNumberFormat(
					CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
			DecimalFormat pf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
					CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));			

			// Define default variable value
			String bondAmountTxt = "______________________";
			String bondAmount = "___________";
			String bondPercentageTxt = "_________________";
			String bondPercentage = "_____";
			String ministryName = "Kementerian ...................... ";
			String ptjName = "______________________";
			String ptjAddress = "______________________";
			String ptjTelNo = "N/A";
			String ptjFaksNo = "N/A";
			String ptjEmel = "N/A";
			String supplierName = "______________________";
			String supplierAddress = "______________________";
			String supplierTelNo = "N/A";
			String supplierFaksNo = "N/A";
			String supplierEmel = "N/A";


			if (getManagedBean().isBondReq()) {
				if (getManagedBean().getCtAgreement().isPendingVerificationAmendment()
						&& getManagedBean().getCtAgreement().getCtAmendment() != null) {
					if (getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt() != null) {
						bondAmount = af.format(getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt());
						bondAmountTxt =  NumberWordUtil.convert(
								getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt().doubleValue() != 0 ? getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt().toString() : "");
						bondPercentage = pf.format(getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondPercentage());
						bondPercentageTxt = bondPercentage;
					}						
				} else {
					if (getManagedBean().getContractVer().getCtContractAmount().getTotalBondAmt() != null) {
						bondAmount = af.format(getManagedBean().getContractVer().getCtContractAmount().getTotalBondAmt());
						bondAmountTxt =  NumberWordUtil.convert(
								getManagedBean().getContractVer().getCtContractAmount().getTotalBondAmt().doubleValue() != 0 ? getManagedBean().getContractVer().getCtContractAmount().getTotalBondAmt().toString() : "");
						bondPercentage = pf.format(getManagedBean().getCtContract().getLoa().getBondPercentage());
						bondPercentageTxt = bondPercentage;
					}
				}
			}

			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				ministryName = retrieveMinistryByOrgProfileId(pmOrgProfile.getOrgProfileId());
			} else {
				ministryName = pmOrgProfile.getOrgName();
			}
			ptjName = pmOrgProfile.getOrgName();
			if (getManagedBean().getContractVer().getOwnerOrgAddress() != null) {
				ptjAddress = getFullAddress(getManagedBean().getContractVer().getOwnerOrgAddress());
				ptjAddress = ptjAddress.replaceAll("\r\n", "<br />");
				ptjAddress = ptjAddress.replaceAll("&", "&#38;");
				ptjTelNo = getManagedBean().getContractVer().getOwnerOrgAddress().getPhoneNoFull();
				ptjFaksNo = getManagedBean().getContractVer().getOwnerOrgAddress().getFaxNoFull();
				ptjEmel = getManagedBean().getContractVer().getOwnerOrgAddress().getContactEmail();
			}
			if (getManagedBean().getContractVer().getCtContractValue().getSupplierName() != null)
				supplierName = getManagedBean().getContractVer().getCtContractValue().getSupplierName();
			if (getManagedBean().getContractVer().getCtContractValue().getSupplierAddress() != null) {
				supplierAddress = getFullAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
				supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
				supplierAddress = supplierAddress.replaceAll("&", "&#38;");

				supplierTelNo = getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPhoneNoFull();
				supplierFaksNo = getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getFaxNoFull();
				if (getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getContactEmail() != null)
					supplierEmel = getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getContactEmail();		
			}

			Map<String,String> replaceString = new HashMap<String,String>();
			replaceString.put("bondAmountTxt", bondAmountTxt);
			replaceString.put("bondAmount", bondAmount);
			replaceString.put("bondPercentageTxt", bondPercentageTxt);
			replaceString.put("bondPercentage", bondPercentage);
			replaceString.put("ministryName", ministryName);
			replaceString.put("ptjName", ptjName);
			replaceString.put("ptjAddress", ptjAddress);
			replaceString.put("ptjTelNo", ptjTelNo);
			replaceString.put("ptjFaksNo", ptjFaksNo);
			replaceString.put("ptjEmel", ptjEmel);
			replaceString.put("supplierName", supplierName);
			replaceString.put("supplierAddress", supplierAddress);
			replaceString.put("supplierTelNo", supplierTelNo);
			replaceString.put("supplierFaksNo", supplierFaksNo);
			replaceString.put("supplierEmel", supplierEmel);	

			String patternString = "%(" + StringUtils.join(replaceString.keySet(), "|") + ")%";
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(combinedContentHtml);

			StringBuffer sb = new StringBuffer();
			while(matcher.find()) {
				matcher.appendReplacement(sb, replaceString.get(matcher.group(1)));
			}
			matcher.appendTail(sb);

			combinedContentHtml = sb.toString();
		} catch (Exception e) {
			log.error(e);
		}
		return combinedContentHtml;
	}

	/**
	 * The method used for generating a list of available ministry
	 * for searching agency as selection.
	 * 
	 * @return the selection list of ministry
	 */
	public SelectItem[] getMinistryDropdown() {		
		SelectItem[] items = null;

		try {
			int i = 1;
			//  if(getManagedBean().isAgencySignerDropDown()) {

			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(retrieveMinistryOrgProfile(
					getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());

			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByMinistry(reqDTO);
			List<PmOrgProfile> list = resDTO.getOrgProfileList();

			items = new SelectItem[list.size()+1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

			for (PmOrgProfile orgProfile : list) {
				items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
			return items;
			//   }
		} catch (Exception e) {
			log.error(e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return null;
	}


	/**
	 * The method used for generating a list of available jabatan
	 * for searching agency as selection.
	 * 
	 * @return the selection list of jabatan
	 */
	public SelectItem[] getPengawaiPengawalDropdown() {		
		SelectItem[] items = null;

		try {
			int i = 1;
			if(getManagedBean().getMinistryProfileId() != null && getManagedBean().isAgencySignerDropDown()) {
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(getManagedBean().getMinistryProfileId());
				reqDTO.setOrgCode(OrganizationType.PENGAWAI_PENGAWAL.value());
				OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);

				List<Object[]> list = resDTO.getOrgProfileDDList();
				items = new SelectItem[list.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

				for(Object[] objects : list){
					items[i++] = new SelectItem(Long.parseLong(objects[0].toString()), objects[1].toString());
				}

				return items;
			}
		} catch (Exception e) {
			log.error(e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return null;
	}

	/**
	 * The method used for generating a list of available jabatan
	 * for searching agency as selection.
	 * 
	 * @return the selection list of jabatan
	 */
	public SelectItem[] getJabatanDropdown() {		
		SelectItem[] items = null;

		try {
			int i = 1;
			if(getManagedBean().getMinistryProfileId() != null ) {
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(getManagedBean().getMinistryProfileId());
				reqDTO.setOrgCode(OrganizationType.DEPARTMENT.value());
				OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);

				List<Object[]> list = resDTO.getOrgProfileDDList();
				items = new SelectItem[list.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

				for(Object[] objects : list){
					items[i++] = new SelectItem(Long.parseLong(objects[0].toString()), objects[1].toString());
				}
				return items;
			}
		} catch (Exception e) {
			log.error(e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return null;
	}

	/**
	 * Call by ajax listener to set value of
	 * @param officerProfileId  and jabatanProfileId
	 * to set value null
	 */
	public void settingMinistryListener(){
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjProfileId(null);
		getManagedBean().setButtonHideActive(false);
	}

	/**
	 * Call by ajax listener to set value of
	 * @param jabatanProfileId  and ptjProfileId
	 * to set value null
	 */
	public void settingPengawaiPengawalListener(){
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjProfileId(null);
	}

	/**
	 * Call by ajax listener to set value of
	 * @param ptjProfileId the ptjProfileId 
	 * to set value null
	 */
	public void settingJabatanListener(){
		getManagedBean().setPtjProfileId(null);
	}

	/**
	 * The method used to reset the search criteria for agency.
	 */
	public void resetSignerAgencyDetails() {
		getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
		getManagedBean().setJabatanProfileId(retrieveJabatanOrgProfile(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
		getManagedBean().setPtjProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
		//getManagedBean().setAgencySignerDropDown(true);
		getManagedBean().setButtonHideActive(false);
	}

	/**
	 * The method used to reset the search criteria for agency.
	 */
	public void resetAgencySignerDetails() {
		getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
		getManagedBean().setJabatanProfileId(null);
	}


	/**
	 * The method used for generating a list of available jabatan
	 * for searching agency as selection.
	 * 
	 * @return the selection list of jabatan
	 */
	public SelectItem[] getPtjDropdown() {		
		SelectItem[] items = null;

		try {
			int i = 1;
			if(getManagedBean().getJabatanProfileId() != null) {
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(getManagedBean().getJabatanProfileId());
				reqDTO.setOrgCode(OrganizationType.DIVISION.value());
				OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);

				List<Object[]> list = resDTO.getOrgProfileDDList();
				items = new SelectItem[list.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

				for(Object[] objects : list){
					items[i++] = new SelectItem(Long.parseLong(objects[0].toString()), objects[1].toString());
				}return items;
			}
		} catch (Exception e) {
			log.error(e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return null;
	}

	/**
	 * The method used to add a new agency record.
	 * This method will generate a new record in agency
	 * list, user allow to enter the agency details. 
	 */
	public void addAgencySignerDetails() {

		IAgreementSO agreementSO = null;
		Long profileId = null;
		CtAgreementParty agreementParty = new CtAgreementParty();
		ContractAgreementReqDTO reqDTO = null;

		try{

			if(getManagedBean().getMinistryProfileId() != null) {
				profileId = getManagedBean().getMinistryProfileId();
				agreementParty.setMinistryName(retrieveOrgProfileByProfileId(profileId));
			}
			
			if(getManagedBean().getJabatanProfileId() != null){
				profileId = getManagedBean().getJabatanProfileId();
				agreementParty.setJabatanName(retrieveOrgProfileByProfileId(profileId));
			}

			if(getManagedBean().getPtjProfileId() != null){
				profileId = getManagedBean().getPtjProfileId();
				agreementParty.setPtjName(retrieveOrgProfileByProfileId(profileId));
			}

			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
			reqDTO = new ContractAgreementReqDTO();
			reqDTO.setOrgProfileId(profileId);
			agreementParty.setOrgProfile(agreementSO.retrieveOrgProfileListByProfileId(reqDTO).getPmOrgProfile());
			agreementParty.setAgreementPartyType(getParameterByTypeAndCode(
					ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_AGENCY_SIGNER));
			agreementParty.setOfficer(new PmUser());
			getManagedBean().getCtAgencySigner().add(agreementParty);
			//getManagedBean().setAgencySignerDropDown(false);
			getManagedBean().setButtonHideActive(true);
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * The method used to close agency signer popup windows
	 * @return portletTitle  the name of the portal
	 */
	public void closeAgencySignerDetails(){
		getManagedBean().setButtonHideActive(true);
		RequestContext.getCurrentInstance().execute("dlgAgencySigner.hide()");
	}

	/*
	 * Fetch organization name using profile id
	 * @Return type string orgName
	 * */
	public String retrieveOrgProfileByProfileId(Long profileId){
		IAgreementSO agreementSO = null;
		ContractAgreementReqDTO reqDTO = null;
		try {

			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
			reqDTO = new ContractAgreementReqDTO();
			reqDTO.setOrgProfileId(profileId);
			return agreementSO.retrieveOrgProfileListByProfileId(reqDTO).getPmOrgProfile().getOrgName();

		}catch(Exception ex){
			log.error(ex);
		}
		return null;
	}

	/**
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
	 */
	public String getPortletTitle() {
		String portletTitle = StringUtils.EMPTY;
		if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.contract.supp.agreement.creation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());;
		} else {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.contract.agreement.creation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}

	/**
	 * The method used to retrieve proper section name from property file.
	 * 
	 * @return sectionName  the name of the section
	 */
	public String retrieveContractDocumentName() {
		String sectionName = StringUtils.EMPTY;
		if (getManagedBean().isFinalAgreement()) {
			sectionName = (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT))
					? "lbl.ct.panel.final.sup.ag.contract.document" : "lbl.ct.panel.final.ag.contract.document";
		} else {
			sectionName = (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT))
					? "lbl.ct.menu.sup.agreement.contract.document" : "lbl.ct.menu.agreement.contract.document";
		}

		sectionName = ResourceBundleUtils.getMessage(sectionName,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return sectionName;
	}

	/**
	 * The method used to set 
	 * number of index of list in datatable 
	 */   
	public void setIndexProperty(int index){
		getManagedBean().setVarIndex(index);
	}

	/**
	 * Function to check / uncheck all checkbox into agency signer datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllAgencySignerCheckBox(AjaxBehaviorEvent e) {

		for (CtAgreementParty ctAgreementPartys : getManagedBean().getCtAgencySigner()) {
			if (getManagedBean().isSelectAll()) {
				ctAgreementPartys.setSelectedCheckBox(true);
			}else {
				ctAgreementPartys.setSelectedCheckBox(false);
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Function to check / uncheck single checkbox into agency signer datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneAgencySignerCheckBox(AjaxBehaviorEvent e){

		getManagedBean().setSelectAll(false);
		for (CtAgreementParty ctAgreementPartys : getManagedBean().getCtAgencySigner()) {
			if(ctAgreementPartys.isSelectedCheckBox()){
				getManagedBean().setButtonHideActive(false);
				return;
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * The method used to delete the selected row(s)
	 */
	public void deleteTableRow(String tableType) {
		try {

			if(tableType.equalsIgnoreCase("AGENCY_SIGNER")){	
				Iterator<CtAgreementParty> ctAgreementPartyIterator = getManagedBean().getCtAgencySigner().iterator();
				while(ctAgreementPartyIterator.hasNext()){
					CtAgreementParty  ctAgreementPartys = ctAgreementPartyIterator.next();
					if(ctAgreementPartys.isSelectedCheckBox()){
						ctAgreementPartyIterator.remove();
					}
				}
				getManagedBean().setSelectedAgencySigner(null);
				getManagedBean().setSelectAll(false);

			} else if(tableType.equalsIgnoreCase("AGENCY_WITNESS")){
				Iterator<CtAgreementParty> ctAgreementPartyIterator= getManagedBean().getCtSignAgencyWitness().iterator();
				while(ctAgreementPartyIterator.hasNext()){
					CtAgreementParty  ctSignAgencyWitnes = ctAgreementPartyIterator.next();
					if(ctSignAgencyWitnes.isSelectedCheckBox()){
						ctAgreementPartyIterator.remove();
					}
				} 
				getManagedBean().setSelectedAgencyWitnes(null);
				getManagedBean().setSelectAll(false);

			}  else if(tableType.equalsIgnoreCase("SUPPLIER_SIGNER")){
				Iterator<CtAgreementParty> ctAgreementPartyIterator = getManagedBean().getCtSupplierSigner().iterator();
				while(ctAgreementPartyIterator.hasNext()){
					CtAgreementParty  ctSignAgencyWitnes = ctAgreementPartyIterator.next();
					if(ctSignAgencyWitnes.isSelectedCheckBox()){
						ctAgreementPartyIterator.remove();
					}
				} 
				getManagedBean().setSelectedSupplierSigner(null);
				getManagedBean().setSelectAll(false);

			} else if(tableType.equalsIgnoreCase("SUPPLIER_WITNESS")){
				Iterator<CtAgreementParty> ctAgreementPartyIterator = getManagedBean().getCtSupplierWitness().iterator();
				while(ctAgreementPartyIterator.hasNext()){
					CtAgreementParty  ctSignAgencyWitnes = ctAgreementPartyIterator.next();
					if(ctSignAgencyWitnes.isSelectedCheckBox()){
						ctAgreementPartyIterator.remove();
					}
				} 
				getManagedBean().setSelectedSupplierWitnes(null);
				getManagedBean().setSelectAll(false);
			} 
			getManagedBean().setButtonHideActive(true);
		} catch (Exception e) {
			log.error(e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	/**
	 * Function to check / uncheck all checkbox into agency witness datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllAgencyWitnessCheckBox(AjaxBehaviorEvent e){
		for (CtAgreementParty ctAgreementPartys : getManagedBean().getCtSignAgencyWitness()) {
			if (getManagedBean().isSelectAll()) {
				ctAgreementPartys.setSelectedCheckBox(true);
			}else {
				ctAgreementPartys.setSelectedCheckBox(false);
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Function to check / uncheck single checkbox into agency witness datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneAgencyWitnessCheckBox(AjaxBehaviorEvent e){

		getManagedBean().setSelectAll(false);
		for (CtAgreementParty ctAgreementPartys : getManagedBean().getCtSignAgencyWitness()) {
			if(ctAgreementPartys.isSelectedCheckBox()){
				getManagedBean().setButtonHideActive(false);
				return;
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Function to check / uncheck all checkbox into supplier signer datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllSupplierSignerCheckBox(AjaxBehaviorEvent e) {

		for (CtAgreementParty ctAgreementPartys : getManagedBean().getCtSupplierSigner()) {
			if (getManagedBean().isSelectAll()) {
				ctAgreementPartys.setSelectedCheckBox(true);
			}else {
				ctAgreementPartys.setSelectedCheckBox(false);
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Function to check / uncheck single checkbox into supplier signer datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneSupplierSignerCheckBox(AjaxBehaviorEvent e){

		getManagedBean().setSelectAll(false);
		for (CtAgreementParty ctAgreementPartys : getManagedBean().getCtSupplierSigner()) {
			if(ctAgreementPartys.isSelectedCheckBox()){
				getManagedBean().setButtonHideActive(false);
				return;
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		} else {
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Function to check / uncheck all checkbox into supplier witness datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllSupplierWitnessCheckBox(AjaxBehaviorEvent e){
		for (CtAgreementParty ctAgreementPartys : getManagedBean().getCtSupplierWitness()) {
			if (getManagedBean().isSelectAll()) {
				ctAgreementPartys.setSelectedCheckBox(true);
			}else {
				ctAgreementPartys.setSelectedCheckBox(false);
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	* Function to check / uncheck single checkbox into supplier witness datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneSupplierWitnessCheckBox(AjaxBehaviorEvent e){

		getManagedBean().setSelectAll(false);
		for (CtAgreementParty ctAgreementPartys : getManagedBean().getCtSignAgencyWitness()) {
			if(ctAgreementPartys.isSelectedCheckBox()){
				getManagedBean().setButtonHideActive(false);
				return;
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Method to set boolean value for checkbox 
	 * Set boolean value for user action 
	 * 
	 * @param pass ajax event type
	 * */
	public void onToggleSelect(ToggleSelectEvent event) {
		getManagedBean().setButtonHideActive(false);
	}

	/**
	 * Method to set boolean value for checkbox 
	 * Set boolean value for user action 
	 * 
	 * @param pass ajax event type
	 * */
	public void onRowSelect(SelectEvent event) {
		getManagedBean().setButtonHideActive(false);
	}

	/**
	 * Method to uncheck boolean value for checkbox 
	 * Set boolean value for user action 
	 * 
	 * @param pass ajax event type
	 * */
	public void onRowUnselect(UnselectEvent event) {
		getManagedBean().setButtonHideActive(true);
	}

	/**
	 * The method used to delete the 
	 * signer attachment from attachment  datatable.
	 */
	public void deleteSignAttachmentAction(int index){
		CtAttachment attachements =  getManagedBean().getCtAgreement().getSignAgreementAttachments().get(index);
		if(attachements.getAttachmentId() != null){
			ctBB.getManagedBean().getDeleteAttachmentList().add(attachements);
		}
		this.getManagedBean().getCtAgreement().getSignAgreementAttachments().remove(index);
	}

	/**
	 * The method used to delete the 
	 * contract document attachment from attachment datatable.
	 */
	public void deleteContractDocumentAction(int index){
		CtAttachment attachements =  getManagedBean().getCtAgreement().getContractDocumentAttachments().get(index);
		if(attachements.getAttachmentId() != null){
			ctBB.getManagedBean().getDeleteAttachmentList().add(attachements);
		}
		this.getManagedBean().getCtAgreement().getContractDocumentAttachments().remove(index);
	}

	/**
	 * The method used to retrieve the correct contract amount
	 * base on pending verification flag
	 * 
	 * @return contract amount
	 */
	public BigDecimal getContractAmt() {
		BigDecimal contractAmount = BigDecimal.ZERO;
		if (getManagedBean().getCtAgreement().isPendingVerificationAmendment()
				&& getManagedBean().getCtAgreement().getCtAmendment() != null) {
			contractAmount = getManagedBean().getContractVer().getCtContractAmount().getContractAmount().add(
					getManagedBean().getCtAgreement().getCtAmendment().getAdditionalAmount());
		} else {
			contractAmount = getManagedBean().getContractVer().getCtContractAmount().getContractAmount();
		}
		return (contractAmount != null) ? contractAmount : BigDecimal.ZERO;
	}

	/**
	 * The method used to retrieve the correct gst amount
	 * base on pending verification flag
	 * 
	 * @return gst amount
	 */
	public BigDecimal getGstAmt() {
		BigDecimal gstAmount = BigDecimal.ZERO;
		if (getManagedBean().getCtAgreement().isPendingVerificationAmendment()
				&& getManagedBean().getCtAgreement().getCtAmendment() != null) {
			gstAmount = getManagedBean().getCtAgreement().getCtAmendment().getNewGstAmount();
		} else {
			gstAmount = getManagedBean().getContractVer().getCtContractAmount().getGstAmount();
		}
		return (gstAmount != null) ? gstAmount : BigDecimal.ZERO;
	}
	
	/**
	 * The method used to retrieve the correct stax amount
	 * base on pending verification flag
	 * 
	 * @return stax amount
	 */
	
	public BigDecimal getStaxAmt() {
		BigDecimal staxAmount = BigDecimal.ZERO;
		if (getManagedBean().getCtAgreement().isPendingVerificationAmendment()
				&& getManagedBean().getCtAgreement().getCtAmendment() != null) {
			staxAmount = getManagedBean().getCtAgreement().getCtAmendment().getNewStaxAmount();
		} else {
			staxAmount = getManagedBean().getContractVer().getCtContractAmount().getStaxAmount();
		}
		return (staxAmount != null) ? staxAmount : BigDecimal.ZERO;
	}

	/**
	 * The method used to retrieve the file reference no
	 * base on pending verification flag
	 * 
	 * @return file reference no
	 */
	public String getFileRefNo() {
		if (getManagedBean().getCtAgreement().isPendingVerificationAmendment()
				&& getManagedBean().getCtAgreement().getCtAmendment() != null) {
			return getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getScLoiLoa().getFileNo();
		} else {
			return getManagedBean().getContractVer().getCtContractValue().getLoaPhysicalNo();
		}
	}
	
	/**
	 * The method used to download Agreement Guideline file.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void downloadAgreementGuideline() throws IOException {
		StringBuilder filePath = new StringBuilder();
		
		filePath.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR));					
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_DIR));
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.CONTRACT_MANAGEMENT.toUpperCase());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ContractConst.DOCNAME_AGREEMENT_GUIDELINE);

		commonDownloadUtil(filePath.toString(), ContractConst.DOCNAME_AGREEMENT_GUIDELINE);
	}
	
	//#26686 - start
	
	public void setContractSignerChanges() {
		CtContractAmount ctContractAmt = null;
		if(getManagedBean().getContractVer().getCtContractAmounts() != null && getManagedBean().getContractVer().getCtContractAmounts().size() >= 2){
			List<CtContractAmount> listContractAmounts = getManagedBean().getContractVer().getCtContractAmounts();
			sortContractAmount(listContractAmounts);
			ctContractAmt = listContractAmounts.get(listContractAmounts.size() - 2);
		}else{
			ctContractAmt = getManagedBean().getContractVer().getCtContractAmounts().get(0);
		}
		if(ctContractAmt.getGstAmount()== null){
			ctContractAmt.setGstAmount(BigDecimal.ZERO);
		}
		if(ctContractAmt.getStaxAmount()== null){
			ctContractAmt.setStaxAmount(BigDecimal.ZERO);
		}

		// Determine the type of changes
		if (getManagedBean().getCtAgreement().getCtAmendment().getAdditionalAmount().compareTo(BigDecimal.ZERO) > 0) {
			contractAmtChange = new String(ContractConst.ADD_AMOUNT);
		} else if (getManagedBean().getCtAgreement().getCtAmendment().getAdditionalAmount().compareTo(BigDecimal.ZERO) < 0) {
			contractAmtChange = new String(ContractConst.DEDUCT_AMOUNT);
		} else {
			contractAmtChange = new String(ContractConst.NO_CHANGE_AMOUNT);
		} 

		if (getManagedBean().getCtAgreement().getCtAmendment().getNewGstAmount().compareTo(ctContractAmt.getGstAmount()) > 0) {
			gstAmtChange = new String(ContractConst.ADD_AMOUNT);
		} else if (getManagedBean().getCtAgreement().getCtAmendment().getNewGstAmount().compareTo(ctContractAmt.getGstAmount()) < 0) {
			gstAmtChange = new String(ContractConst.DEDUCT_AMOUNT);
		} else {
			gstAmtChange = new String(ContractConst.NO_CHANGE_AMOUNT);
		} 

		if (getManagedBean().getCtAgreement().getCtAmendment().getNewStaxAmount().compareTo(ctContractAmt.getStaxAmount()) > 0) {
			sTaxAmtChange = new String(ContractConst.ADD_AMOUNT);
		} else if (getManagedBean().getCtAgreement().getCtAmendment().getNewStaxAmount().compareTo(ctContractAmt.getStaxAmount()) < 0) {
			sTaxAmtChange = new String(ContractConst.DEDUCT_AMOUNT);
		} else {
			sTaxAmtChange = new String(ContractConst.NO_CHANGE_AMOUNT);
		} 		
	}
	
	public BigDecimal setLimitContractSigner() {
		// Calculation of new Contract Signer limit bases on the changes
		CtContractAmount ctContractAmount = null;
		BigDecimal result = BigDecimal.ZERO;
		BigDecimal newAdditionAmt = BigDecimal.ZERO;
		BigDecimal newAdditionGst = BigDecimal.ZERO;
		BigDecimal newAdditionSst = BigDecimal.ZERO;
		BigDecimal additionGst = getManagedBean().getCtAgreement().getCtAmendment().getNewGstAmount();
		BigDecimal additionSst = getManagedBean().getCtAgreement().getCtAmendment().getNewStaxAmount();
		BigDecimal additionContract = getManagedBean().getCtAgreement().getCtAmendment().getAdditionalAmount();
		
		if(getManagedBean().getContractVer().getCtContractAmounts() != null && getManagedBean().getContractVer().getCtContractAmounts().size() >= 2){
			List<CtContractAmount> listContractAmounts = getManagedBean().getContractVer().getCtContractAmounts();
			sortContractAmount(listContractAmounts);
			ctContractAmount = listContractAmounts.get(listContractAmounts.size() - 2);
		}else{
			ctContractAmount = getManagedBean().getContractVer().getCtContractAmounts().get(0);
		}

		if (contractAmtChange.equals(ContractConst.ADD_AMOUNT)) {
			newAdditionAmt = additionContract;

		}else if(contractAmtChange.equals(ContractConst.DEDUCT_AMOUNT)){
		
			newAdditionAmt = ctContractAmount.getContractAmount().add(additionContract);
		}
		
		if (gstAmtChange.equals(ContractConst.ADD_AMOUNT)) {
			newAdditionGst = additionGst.subtract(ctContractAmount.getGstAmount());
		}else if(gstAmtChange.equals(ContractConst.DEDUCT_AMOUNT)){
			newAdditionGst = additionGst;
		}
		
		if (sTaxAmtChange.equals(ContractConst.ADD_AMOUNT)) {
			newAdditionSst = additionSst.subtract(ctContractAmount.getStaxAmount());
		}else if(sTaxAmtChange.equals(ContractConst.DEDUCT_AMOUNT)){
			newAdditionSst = additionSst;
		}

		// Addition in contract and tax
		return result = newAdditionAmt.add(newAdditionGst.add(newAdditionSst));
			
	}

	public void sortContractAmount (List<CtContractAmount> list){
		if(list!=null){
			Collections.sort(list, new Comparator<CtContractAmount>() {	
				public int compare(CtContractAmount l1, CtContractAmount l2) {
					return l1.getExpDate().compareTo(l2.getExpDate());
				}
			});
		}
	}
	//#26686 - end
}
