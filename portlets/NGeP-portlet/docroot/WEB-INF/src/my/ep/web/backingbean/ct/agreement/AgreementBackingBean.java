/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.agreement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
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
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
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
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.agreement.CTAgreementDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementContent;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.agreement.AgreementManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.template.MergeDocx;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.docx4j.jaxb.*;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Agreement
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean(name = "agreementBackingBean")
@RequestScoped
public class AgreementBackingBean extends BaseBackingBean<AgreementManagedBean> {
	
	private static Logger log = Logger.getLogger(AgreementBackingBean.class);
	private static final String CONTRACT_PAGE = "/xhtml/ct/fulfilmentdetails/creation/fulfilment.xhtml?faces-redirect=true";
	private static final String AGREEMENT_DOC_PAGE = "/xhtml/ct/agreement/shared/sidemenu/agreementTab.xhtml?faces-redirect=true";
	private ContractBackingBean ctBB = null;
	private CtAgreementParty[] selectedAgencyWitnes;
	private CtAgreementParty[] selectedAgencySigner;
	private CtAgreementParty[] selectedSupplierWitnes;
	private CtAgreementParty[] selectedSupplierSigner;
	
	private String agreementZipFileName = null;
	private String agreementZipFilePath = null;
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public AgreementBackingBean() {
		/* Initialization of button */
		if (getManagedBean().getAction() == null) {
			getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
	}

	/**
     * This method used to retrieve login user information.
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * This  method used to initialize approver list.
	 */
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<PmUser> source = new ArrayList<PmUser>();
			List<PmUser> target = new ArrayList<PmUser>();
			getManagedBean().setApproverModel(new DualListModel<PmUser>(source, target));
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
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
		return sourceList;
	}

	/**
	 * @return the selectedAgencyWitnes
	 */
	public CtAgreementParty[] getSelectedAgencyWitnes() {
		return selectedAgencyWitnes;
	}

	/**
	 * @param selectedAgencyWitnes the selectedAgencyWitnes to set
	 */
	public void setSelectedAgencyWitnes(CtAgreementParty[] selectedAgencyWitnes) {
		this.selectedAgencyWitnes = selectedAgencyWitnes;
	}

	/**
	 * @return the selectedAgencySigner
	 */
	public CtAgreementParty[] getSelectedAgencySigner() {
		return selectedAgencySigner;
	}

	/**
	 * @param selectedAgencySigner the selectedAgencySigner to set
	 */
	public void setSelectedAgencySigner(CtAgreementParty[] selectedAgencySigner) {
		this.selectedAgencySigner = selectedAgencySigner;
	}

	/**
	 * @return the selectedSupplierWitnes
	 */
	public CtAgreementParty[] getSelectedSupplierWitnes() {
		return selectedSupplierWitnes;
	}

	/**
	 * @param selectedSupplierWitnes the selectedSupplierWitnes to set
	 */
	public void setSelectedSupplierWitnes(CtAgreementParty[] selectedSupplierWitnes) {
		this.selectedSupplierWitnes = selectedSupplierWitnes;
	}

	/**
	 * @return the selectedSupplierSigner
	 */
	public CtAgreementParty[] getSelectedSupplierSigner() {
		return selectedSupplierSigner;
	}

	/**
	 * @param selectedSupplierSigner the selectedSupplierSigner to set
	 */
	public void setSelectedSupplierSigner(CtAgreementParty[] selectedSupplierSigner) {
		this.selectedSupplierSigner = selectedSupplierSigner;
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the screen.
	 */
	public void preRenderAgreement() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"agreementTab.xhtml?faces-redirect=true");
	}

	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		IContractSO contractSO = null;
		IAgreementSO agreementSO = null;
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO res = null;
		ContractAgreementReqDTO ctReqDTO = null;
		ContractAgreementResDTO resDTO = null;
		ContractReqDTO conReqDTO = null;

		try {
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			
			String taskId = originalServletRequest.getParameter("taskId");
			if (taskId == null) {
				return;
			}
			FacesScopes face = new FacesScopes();
			Object token = face.getBPMAuthenticationCtxToken();
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			getManagedBean().setTaskId(taskId);
			getManagedBean().setSelectedTabIndex(0);

			res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			getManagedBean().setTaskName(res.getTaskName());

			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			getManagedBean().setAction(outcome);

			ctReqDTO = new ContractAgreementReqDTO();
			CTAgreementDataType dataType = (CTAgreementDataType) obj;
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setDocId(new Long(dataType.getDocId()));
			ctReqDTO.setAgreementId(new Long(dataType.getAgreementId()));
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);

			resDTO = new ContractAgreementResDTO();
			String agrrementType = (dataType.getAgreementType() == null) ? "" : dataType.getAgreementType();
			getManagedBean().setAgreementType(agrrementType);

			// If agreement type = supplemental, set the supplemental agreement; else set the agreement
		    if(getManagedBean().getAgreementType() != null
		    		&& getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
		    	
		    	ctReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);	
		    	if (dataType.getAgreementId().intValue() == 0) {
					ContractAgreementReqDTO contractAgreementReqDTO = new ContractAgreementReqDTO();
					ContractAgreementResDTO contractAgreementResDTO = new ContractAgreementResDTO();
					contractAgreementReqDTO.setContractVerId(new Long(dataType.getDocId()));
					contractAgreementResDTO = agreementSO.retrieveSupAgreementByContractVersion(contractAgreementReqDTO); 
			    	
			    	if (contractAgreementResDTO.getAgreement() == null) { 
			    		conReqDTO = new ContractReqDTO();
			    		conReqDTO.setContractVerId(new Long(dataType.getDocId()));
						conReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);
						contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
						
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
		    
		    if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)) {
				getManagedBean().setUploadStampedAndSignedPage(true);
				getManagedBean().setUploadFinalAgreement(true);
		    } else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_FINALIST_AGREEMENT_HT_NAME)) {
				getManagedBean().setUploadFinalAgreement(true);
            } else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FINALIST_AGREEMENT_HT_NAME)) {
				getManagedBean().setApproveFinalAgreement(true);
            }
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			contractSO = null;
			agreementSO = null;
			bpmSO = null;
			reqDTO = null;
			res = null;
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
			 			     /* If agency singer only ministry*/
			 			     if(ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.MINISTRY.value())){
			 				       ctAgreementParty.setMinistryName(retrieveOrgProfileByProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
			 			     } else {
					 			ctAgreementParty.setMinistryName(retrieveMinistryByOrgProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
			 			     }
			 			     
			 			    /* If agency singer  ministry and pengawai pengawal */
			 			    if(ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DEPARTMENT.value())
			 					 ||ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DIVISION.value()) ){
			 				      ctAgreementParty.setPengawaiPengawalName(retrievePegawaiPengawalByOrgProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));	
			 			    } else if(ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.PENGAWAI_PENGAWAL.value())){
			 				      ctAgreementParty.setPengawaiPengawalName(retrieveOrgProfileByProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
			 			    }
			 			    
			 			   /* If agency singer  jabatan */
			 			   if(ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DIVISION.value())){
			 				    ctAgreementParty.setJabatanName(retrieveJabatanByOrgProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
			 			   } else if(ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DEPARTMENT.value())) {
			 				   ctAgreementParty.setJabatanName(retrieveOrgProfileByProfileId(ctAgreementParty.getOrgProfile().getOrgProfileId()));
			 			   }
			 			
			 			  /* If agency singer  ptj */
			 			  if(ctAgreementParty.getOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(OrganizationType.DIVISION.value())){
			 				    ctAgreementParty.setPtjName(ctAgreementParty.getOrgProfile().getOrgName());
			 			   }
			 			
			 			  getManagedBean().getCtAgencySigner().add(ctAgreementParty);
			 		
		 		  } else if (ctAgreementParty.getAgreementPartyType().getParameterCode().equalsIgnoreCase(ParameterTypeConst.AGREEMENT_AGENCY_WITNESS)) {
		 			    getManagedBean().getCtSignAgencyWitness().add(ctAgreementParty);
		 		
		 		  } else if (ctAgreementParty.getAgreementPartyType().getParameterCode().equalsIgnoreCase(ParameterTypeConst.AGREEMENT_SUPPLIER_SIGNER)) {
		 			   getManagedBean().getCtSupplierSigner().add(ctAgreementParty);
		 		
		 		  } else {
		 			 getManagedBean().getCtSupplierWitness().add(ctAgreementParty);
			  	  }
		 	}
		} else {
			getManagedBean().setCtAgreement(new CtAgreement());
		}
		
		getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			for (PmUser approverUser : resDTO.getApproverList())  {
				Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();				
				while (sourceIt.hasNext()) {
					PmUser user = sourceIt.next();
					if (user.getUserId() == approverUser.getUserId()) {
						sourceIt.remove();
					}
				}
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
		if (getManagedBean().getContractVer().getCtContractValue().getGstEffDate() != null 
				&& getManagedBean().getContractVer().getCtContractValue().getGstEffDate().getTime() <= DateUtils.getCurrentDateWithoutTime().getTime()) {
			if (getManagedBean().getContractVer().getCtContractAmount().getGstAmount() == null) {
				getManagedBean().getContractVer().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
			}
			ctBB.getManagedBean().setGstCheckingGUI(true);
	    } else {
	    	ctBB.getManagedBean().setGstCheckingGUI(false);
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
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
	         
			if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
				reqDTO.setAgreementType(ContractConst.SUPPLEMENTAL_AGREEMENT);
				reqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);   
			} else {
				reqDTO.setAgreementType(ContractConst.AGREEMENT);
				reqDTO.setDocType(ParameterConst.DOCTYPE_AGREEMENT);
			}

			getManagedBean().getCtAgreement().setCtContract(getManagedBean().getCtContract());
			reqDTO.setToken(token);
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

			// Generate agreement Zip File
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)
					&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_COMPLETE)) {
				generateAgreementZipFile();
				getManagedBean().getCtAgreement().setFilePath(DocumentRepositoryUtils.getDBFilePath(agreementZipFilePath.replace("\\", "/")));
				getManagedBean().getCtAgreement().setFileName(agreementZipFileName);
			}
			
			boolean isBrandNew = getManagedBean().getCtAgreement().isBrandNew();
			reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());
			reqDTO.setUserMap(getManagedBean().getUserMap());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setCtContractVer(getManagedBean().getAgreementContractVer());
			reqDTO.setAgreement(getManagedBean().getCtAgreement());
			
			ContractAgreementResDTO resDTO = agreementSO.saveAgreement(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700026;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					messageId = MessageConst.INFO_700005;
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
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									MessageUtils.getMessage(messageId,
											new String[] {getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, reqDTO.getDocType()), getManagedBean().getCtContract().getContractNo()},
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
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
		
			for(CtAgreementContent agContent : getManagedBean().getCtAgreement().getAgreementContents()){
				if(agContent.getContentType().getParameterCode().equalsIgnoreCase(ParameterConst.AGREEMENT_USER_UPLOAD_DOC)){
					if(agContent.getTitle() == null ||  agContent.getTitle().isEmpty()){
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.contents", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					if(agContent.getFilePath() == null ||  agContent.getFilePath().isEmpty()){
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.contents.attach", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
				if (getManagedBean().getRemarkList().get(0).getRemark().length() == 0) {
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
		
			if(getManagedBean().getCtAgencySigner() != null && getManagedBean().getCtAgencySigner().size() > 0) {
			      for (CtAgreementParty agencySigner : getManagedBean().getCtAgencySigner()) {
				      if (agencySigner.getOfficer().getUserId() == null) {
					       throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.contract.signer.agency", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				       }
			      }
		    }
			
			if(getManagedBean().getCtSupplierSigner() != null && getManagedBean().getCtSupplierSigner().size() > 0) {
			      for (CtAgreementParty supplierSigner : getManagedBean().getCtSupplierSigner()) {
				      if(supplierSigner.getOfficer().getUserId() == null) {
					      throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.contract.signer.supplier", 
							  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				      }
			     }
		     }
		
			if (!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
		       if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)) {
			        if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_COMPLETE)) {
			        	if (getManagedBean().getCtAgreement().getSignedPageName() == null) {
				             throw new EpException(MessageConst.ERROR_100022, 
						        new String[]{ResourceBundleUtils.getMessage("lbl.ct.attachment.signed.page", 
								     ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			        	} else if (getManagedBean().getCtAgreement().getStampedPageName() == null) {
				             throw new EpException(MessageConst.ERROR_100022, 
						        new String[]{ResourceBundleUtils.getMessage("lbl.ct.attachment.stamped.page", 
								     ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			        	}
			        }
		      }
		
		     if (getManagedBean().getCtAgencySigner().size() == 0) {
			      throw new EpException(MessageConst.ERROR_100023, 
					  new String[]{ResourceBundleUtils.getMessage("lbl.ct.agency.signer", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		      } 
		
		      if (getManagedBean().getCtSignAgencyWitness().size() == 0) {
			       throw new EpException(MessageConst.ERROR_100023, 
					  new String[]{ResourceBundleUtils.getMessage("lbl.ct.agency.witness", 
					    ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	       	 } else {	
			     for (CtAgreementParty agencyWitness : getManagedBean().getCtSignAgencyWitness()) {
				       if (agencyWitness.getOfficerName() == null || agencyWitness.getOfficerName().isEmpty()) {
					         throw new EpException(MessageConst.ERROR_100022, 
							    new String[]{ResourceBundleUtils.getMessage("lbl.ct.name.agency.witness", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				      
				       } if (agencyWitness.getOfficerIc() == null || agencyWitness.getOfficerIc().isEmpty()) {
					          throw new EpException(MessageConst.ERROR_100022, 
							    new String[]{ResourceBundleUtils.getMessage("lbl.ct.ic.agency.witness", 
								  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				    
				       } if (agencyWitness.getOfficerPosition() == null || agencyWitness.getOfficerPosition().isEmpty()) {
					          throw new EpException(MessageConst.ERROR_100022, 
							      new String[]{ResourceBundleUtils.getMessage("lbl.ct.position.agency.witness", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				      }
	                checkDuplicateAgencyWitness(agencyWitness.getOfficerIc());
			     }
		 
	       	 } if (getManagedBean().getCtSupplierSigner().size() == 0) {
		        	throw new EpException(MessageConst.ERROR_100023, 
					   new String[]{ResourceBundleUtils.getMessage("lbl.ct.supplier.signer", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		     } 
	       	 
	       	 if(getManagedBean().getCtSupplierWitness().size() == 0) {
		    	   throw new EpException(MessageConst.ERROR_100023,
					   new String[]{ResourceBundleUtils.getMessage("lbl.ct.supplier.witness", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		     } else {
	    	     for(CtAgreementParty supplierWitness : getManagedBean().getCtSupplierWitness()) {
				      if(supplierWitness.getOfficerName() == null || supplierWitness.getOfficerName().isEmpty()) {
					       throw new EpException(MessageConst.ERROR_100022, 
							   new String[]{ResourceBundleUtils.getMessage("lbl.ct.name.supplier.witness", 
								  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				   
				      } if(supplierWitness.getOfficerIc() == null || supplierWitness.getOfficerIc().isEmpty()) {
					        throw new EpException(MessageConst.ERROR_100022, 
							   new String[]{ResourceBundleUtils.getMessage("lbl.ct.ic.supplier.witness", 
								  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				   
				      } if (supplierWitness.getOfficerPosition() == null || supplierWitness.getOfficerPosition().isEmpty()) {
					      throw new EpException(MessageConst.ERROR_100022, 
							 new String[]{ResourceBundleUtils.getMessage("lbl.ct.position.supplier.witness", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				      }
				   checkDuplicateSupplierWitness(supplierWitness.getOfficerIc());
			      }
		       }		
	       }
		     if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				    && getManagedBean().getApproverModel().getTarget().size() == 0) {
			        //Approver is mandatory for document submission. Please select at least one approver.
			        throw new EpException(MessageConst.ERROR_750010);
		      }
		
		      // Contract Approver cannot approve the draft agreement if:-
		      // -FD Creation still in process.
		      if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) 
				     && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_DRAFT_AGREEMENT_HT_NAME)) {			
			         if (getManagedBean().getCtContract().getLatestContractVer() == null) { // FD creation in progress
				         throw new EpException(MessageConst.ERROR_750041, 
						    new String[]{ResourceBundleUtils.getMessage(getManagedBean().getCtContract().getContractNo(), 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			         }		
		       }
		  
		      // Contract Admin cannot submit the finalist agreement if:-
		      // -Performance bond not complete
		      // -FD Update/FD Creation/Change Bank process still in process. 
		      if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				    && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FINALIST_AGREEMENT_HT_NAME)) {
			        
		    	    IAgreementSO agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
			        ContractAgreementReqDTO reqDTO = new ContractAgreementReqDTO();
			        reqDTO.setContractId(getManagedBean().getCtContract().getContractId());
			        ContractAgreementResDTO resDTO = agreementSO.validatePendingFinalistApproval(reqDTO);
			        
			        if(!resDTO.isFdComplete()) {
				         throw new EpException(MessageConst.ERROR_750041, 
						    new String[]{ResourceBundleUtils.getMessage(getManagedBean().getCtContract().getContractNo(), 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			        }			
		     }
		      
		   // Contract Approver cannot approve the final agreement if:-
	       // -Disclaimer is not checked
	       if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) 
			     && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FINALIST_AGREEMENT_HT_NAME)) {			
		         if (!getManagedBean().isAgreementDisclaimer()) {
			         throw new EpException(MessageConst.ERROR_750042);
		         }		
	       }
	   }
	
	/**
	 * The method used to validate the identification no 
	 * of agency witness, supplier signer and supplier witness.
	 * A warning will display on screen when there is duplicated identification no.
	 * with agency witness.
	 *
	 * @param  icNo  the identification no of agency witness
	 */
	private void checkDuplicateAgencyWitness(String icNo) {
		if (getManagedBean().getAgencyOfficerIcNo() != null 
				&& !getManagedBean().getAgencyOfficerIcNo().isEmpty()
				&& getManagedBean().getAgencyOfficerIcNo().contains(icNo)) {
			throw new EpException(MessageConst.ERROR_755005, new String[]{icNo});
	       	
		} else if (getManagedBean().getSupplierOfficerIcNo() != null
				&& !getManagedBean().getSupplierOfficerIcNo().isEmpty() 
				&& getManagedBean().getSupplierOfficerIcNo().contains(icNo)){
			throw new EpException(MessageConst.ERROR_755009, new String[]{icNo});
		}  else if (getManagedBean().getSupplierWitnessIcNo() != null 
				&& !getManagedBean().getSupplierWitnessIcNo().isEmpty() 
				&& getManagedBean().getSupplierWitnessIcNo().contains(icNo)){
			throw new EpException(MessageConst.ERROR_755012, new String[]{icNo});
		} 
	}
	
	/**
	 * The method used to validate the identification no 
	 * of agency witness, supplier signer and supplier witness.
	 * A warning will display on screen when there is duplicated identification no.
	 * with supplier witness.
	 *
	 * @param  icNo  the identification no of supplier witness
	 */
	private void checkDuplicateSupplierWitness(String icNo) {		
		if (getManagedBean().getSupplierOfficerIcNo() != null
				&& !getManagedBean().getSupplierOfficerIcNo().isEmpty() 
				&& getManagedBean().getSupplierOfficerIcNo().contains(icNo)) {
			throw new EpException(MessageConst.ERROR_755009, new String[]{icNo});
		} else if (getManagedBean().getAgencyOfficerIcNo() != null
				&& getManagedBean().getAgencyOfficerIcNo().isEmpty() 
				&& getManagedBean().getAgencyOfficerIcNo().contains(icNo)) {
			throw new EpException(MessageConst.ERROR_755005, new String[]{icNo});
		} else if (getManagedBean().getAgencyWitnessIcNo() != null 
				&& getManagedBean().getAgencyWitnessIcNo().isEmpty() 
				&& getManagedBean().getAgencyWitnessIcNo().contains(icNo)){
			throw new EpException(MessageConst.ERROR_755011, new String[]{icNo});
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
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
			validateAgreement(outcome);
			CtContractVer contractVer = getManagedBean().getContractVer();
			CtContract contract = getManagedBean().getCtContract();
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(contract.getContractNo());
			sb.append(contractVer.getDocType());
			sb.append(contractVer.getCtContractValue().getEffDate());
			sb.append(contractVer.getCtContractValue().getExpDate());
			sb.append(contractVer.getCtContractAmount().getContractAmount());
			sb.append(outcome);
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else {
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
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_DRAFT_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_AC_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_AC_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_DRAFT_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_DRAFT_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FINALIST_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FINALIST_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_FINALIST_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)) {
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
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_DRAFT_AGREEMENT_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FINALIST_AGREEMENT_HT_NAME))) {
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
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
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
		 	
		 	reqDTO.setApprovalLimit(getManagedBean().getContractVer().getCtContractAmount().getContractAmount());
		 	resDTO = userSO.retrieveUserListByRole(reqDTO);
		 	
		 	List<PmUser> list = resDTO.getUserList();
		 	items = new SelectItem[list.size() + 1];
		 	items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		 	Map<Long, PmUser> agencyUserMap =  new HashMap<Long, PmUser>();
		 	for (PmUser pmUser : list) {
		 		items[i++] = new SelectItem(pmUser.getUserId(), pmUser.getUserName());
		 		agencyUserMap.put(pmUser.getUserId(),pmUser);
		 	}
	     
		 	/*//Reset the agency signer ic no and designation if record didn't exist in the drop down
		 	for (CtAgreementParty agencySigner: getManagedBean().getCtAgencySigner()) {
		 		if (agencySigner.getOfficer() == null) {
		 			agencySigner.setOfficer(new PmUser());
		 		}
		 		PmUser user = agencyUserMap.get(agencySigner.getOfficer().getUserId());
		 		if (user == null) {
		 			agencySigner.setOfficerIc(null);
		 			agencySigner.setOfficerPosition(null);
		 		}
		 	}*/
		} finally {
			userSO = null;
			reqDTO = null;
			resDTO = null;
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
		Long contractVerId = Long.parseLong(
				FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));

		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractVerId(contractVerId);
    	ctReqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
    	ctReqDTO.setViewOnly(true);
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractResDTO resDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
		ContractDetailsBackingBean contractDetailsBB = new ContractDetailsBackingBean();		
		contractDetailsBB.getManagedBean().setViewContract(true);
		contractDetailsBB.getManagedBean().setAsAtDate(null);
		contractDetailsBB.setValidateContractField(resDTO);
		//contractDetailsBB.ConstructSideTabMenu();
		contractDetailsBB.getManagedBean().setSelectedTabIndex(0);
		contractDetailsBB.getManagedBean().setExternalPageUrl(AGREEMENT_DOC_PAGE);
		return CONTRACT_PAGE;
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
		tplDocBB.getManagedBean().setExternalPageUrl(AGREEMENT_DOC_PAGE);
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
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath);
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
		for (CtAgreementContent agreementContent : getManagedBean().getCtAgreement().getAgreementContents()) {
			if (agreementContent.equals(getManagedBean().getSelectedAgreementContent())) {
				agreementContent.setFileName(item.getFileName());
				agreementContent.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
			}
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
	}
	
	/**
	 * The method used to download the sample agreement file
	 * base on the quotation tender ID.
	 * 
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadSampleFile() throws IOException{
		if(getManagedBean().getCtContract().getQuotationTender() == null 
				|| getManagedBean().getCtContract().getQuotationTender().getQtId() == null) {
			throw new EpException(MessageConst.INFO_758001);
		}
		
		 IAgreementSO agreementSO = null;
		 ContractAgreementReqDTO reqDTO = new ContractAgreementReqDTO();
		 reqDTO.setQtid(getManagedBean().getCtContract().getQuotationTender().getQtId());
		 agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
		 ContractAgreementResDTO resDTO = agreementSO.downloadSampleFile(reqDTO);
		 if(resDTO.getScQtDocReport() != null && resDTO.getScQtDocReport().getScQtDocReportAttachment() != null) {
            downloadFile(resDTO.getScQtDocReport().getScQtDocReportAttachment().getFilePath(),resDTO.getScQtDocReport().getScQtDocReportAttachment().getFileName());
        } else {
        	throw new EpException(MessageConst.INFO_758001);
        }
	 }

	/**
	 * The method used to download the attached file.
	 * 
	 * @param  filePath	    will be download file path
	 * @param  fileName     will be download filename
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadFile(String filePath, String fileName) throws IOException {
		PortletResponse portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);

		res.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\"");//
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		if (filePath != null) {
			filePath = DocumentRepositoryUtils.getBaseDir()+filePath;
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096];
			}
			input.close();
		}
		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	/**
	 * The method used to download the template doc.
	 * 
	 * @param  agreementContent  an object of agreement content
	 * @throws IOException       if an input or output exception occurred
	 * @see    IOException
	 */
	public void getTemplateContent(CtAgreementContent agreementContent) throws Exception {
		// TODO download the templateDoc file
		
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
	 * The method used to download the agreement content.
	 * 
	 * @param  agreementContent  an object of agreement content
	 * @throws IOException       if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadDocFile(CtAgreementContent agreementContent) throws   Exception {
	
		AttachmentFormat attachmentFormat = null;
			
		try {
	    	String fileName = getTemplateFileName(agreementContent.getTitle());
			if (fileName != null) {
				String content = null;
				String filePath = getManagedBean().getFileDirectory();
				File file = new File(filePath);
				file.mkdirs();

				attachmentFormat = new AttachmentDateFormat();
				filePath = filePath + "/"+ attachmentFormat.format(fileName, null);
				
				if(agreementContent.getContentType().getParameterCode().equalsIgnoreCase(ParameterConst.AGREEMENT_TEMPLATE_DOC)) {
					getTemplateContent(agreementContent);
				    content = previewAction();
				} else {
					content = getContent(agreementContent.getTitle());
				}
				if (content != null) {
					convertHtmlToDocFile(content, filePath);
					downloadFile(DocumentRepositoryUtils.getDBFilePath(filePath), fileName);
				}
			} else {
				if (agreementContent.getFilePath() == null) {
					throw new EpException(MessageConst.INFO_100052,
							new String[] {agreementContent.getTitle()});
				}
				downloadFile(DocumentRepositoryUtils.getDBFilePath(agreementContent.getFilePath()),
						agreementContent.getFileName());
			}
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
	
			attachmentFormat = null;
		}
	}	

	/**
	 * The method used to download all agreement contents
	 * as a zip file.
	 */
	public void downloadAll() {
		try {
			generateAgreementZipFile();
			downloadFile(DocumentRepositoryUtils.getDBFilePath(agreementZipFilePath.replace("\\", "/")), agreementZipFileName);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	/**
	 * The method used to generate agreement zip file.
	 *
	 */
	private void generateAgreementZipFile() {
		List<File> fileList = new ArrayList<File>();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String content = null;
		String filePath = null;
		String tempDirectory = getManagedBean().getFileDirectory();
		File file = new File(tempDirectory);
		file.mkdirs();
		String fileName = null;

		try {
			for (CtAgreementContent agreementContent : getManagedBean().getCtAgreement().getAgreementContents()) {
				fileName = getTemplateFileName(agreementContent.getTitle());
				if (fileName != null) {
					if(agreementContent.getContentType().getParameterCode().equalsIgnoreCase(
							ParameterConst.AGREEMENT_TEMPLATE_DOC)) {
						getTemplateContent(agreementContent);
					    content = previewAction();
					} else {
						content = getContent(agreementContent.getTitle());
					}
					if (content != null) {
						filePath = tempDirectory + "/"+ attachmentFormat.format(fileName, null);
                        convertHtmlToDocFile(content, filePath);
					    fileList.add(new File(filePath));
					}
				} else {
					if (agreementContent.getFilePath() != null) {
						filePath = DocumentRepositoryUtils.getBaseDir() + agreementContent.getFilePath();
						fileList.add(new File(filePath));
					}
				}				
			}

			String zipFileName = "agreement.zip";
			if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
				zipFileName = "supplemental_agreement.zip";
			}
			File zipFile = new File(tempDirectory + "/"+ attachmentFormat.format(zipFileName, null));
			try {
				zipFile.createNewFile();
				byte[] buffer = new byte[1024];
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
				out.setLevel(Deflater.DEFAULT_COMPRESSION);
				for (File agreementFile : fileList) {
					FileInputStream in = new FileInputStream(agreementFile);
					out.putNextEntry(new ZipEntry(agreementFile.getName()));
					int len;
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					out.closeEntry();
					in.close();
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			agreementZipFilePath = zipFile.getPath();
			agreementZipFileName = zipFile.getName();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	/**
	 * The method used to retrieve filename of the file to download
	 * according to template type.
	 * 
	 * @return the filename
	 */
	private String getTemplateFileName(String templateType) {
		if (templateType.trim().equalsIgnoreCase(ContractConst.TEMPLATE_AGREEMENT_COVER_PAGE)) {
			return "AgreementCoverPage.docx";
		} else if (templateType.trim().equalsIgnoreCase(ContractConst.TEMPLATE_AGREEMENT_PARTY)) {
			return "AgreementPartyPage.docx";
		} else if (templateType.trim().equalsIgnoreCase(ContractConst.TEMPLATE_AGREEMENT_SIGNING_PAGE)) {
			return "AgreementSigningPage.docx";
		} else if (templateType.trim().equalsIgnoreCase(ContractConst.TEMPLATE_AGREEMENT_CLAUSE)) {
			return "AgreementClausePage.docx";
		} else if (templateType.trim().equalsIgnoreCase(ContractConst.TEMPLATE_SUPP_AGREEMENT_COVER_PAGE)) {
			return "SupplementalAgreementCoverPage.docx";
		} else if (templateType.trim().equalsIgnoreCase(ContractConst.TEMPLATE_SUPP_AGREEMENT_PARTY)) {
			return "SupplementalAgreementPartyPage.docx";
		} else if (templateType.trim().equalsIgnoreCase(ContractConst.TEMPLATE_SUPP_AGREEMENT_SIGNING_PAGE)) {
			return "SupplementalAgreementSigningPage.docx";
		} else if (templateType.trim().equalsIgnoreCase(ContractConst.TEMPLATE_SUPP_AGREEMENT_CLAUSE)) {
			return "SupplementalAgreementClausePage.docx";
		}
		return null;
	}

	/**
	 * The method used for generating the content of agreement
	 * that defined from template.
	 * 
	 * @param parameterCode  the code of the template. Different code will have different template.
	 * @return the complete content of the template
	 * @throws Exception     if an exception occurred
	 * @see    Exception
	 */
	private String getContent(String parameterCode) throws Exception {
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
				CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		Calendar c = Calendar.getInstance();
		VelocityContext context = null;
		String vmFile = null;
		String content = null;
		
		if (ContractConst.TEMPLATE_AGREEMENT_COVER_PAGE.equalsIgnoreCase(parameterCode.trim())
				|| ContractConst.TEMPLATE_SUPP_AGREEMENT_COVER_PAGE.equalsIgnoreCase(parameterCode.trim())) {
			String imgUrl = getServerUrl()+ "NGeP-portlet/css/images/gov.png";
			context = new VelocityContext();
			vmFile = "/ct/agreement/agreement-cover-page.vm";
			context.put("imgUrl", imgUrl);
			if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
				context.put("agreementTypeUpper", ContractConst.SUPP_AGREEMENT_STRING.toUpperCase());
			} else {
				context.put("agreementTypeUpper", ContractConst.AGREEMENT_STRING.toUpperCase());
			}
			context.put("contractTitle", getManagedBean().getCtContract().getContractName());
			context.put("supplierName", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			context.put("supplierRegistrationNo", getManagedBean().getCtContract().getSmSupplier().getRegNo());
			context.put("physicalContactNo", getManagedBean().getAgreementContractVer().getContractPhysicalNo());
			context.put("contractStartDate", df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
			context.put("contractEndDate", df.format(getManagedBean().getCtAgreement().getExpDate()));
			context.put("contractPeriod", getManagedBean().getAgreementContractVer().getCtContractValue().getContractPeriod() + "");

			BigDecimal overallAmount  = null;
			if (getManagedBean().getContractVer().getCtContractAmount().getGstAmount() != null) {
				overallAmount = getManagedBean().getContractVer().getCtContractAmount().getContractAmount().add(
						getManagedBean().getContractVer().getCtContractAmount().getGstAmount());
			} else {
				overallAmount = getManagedBean().getContractVer().getCtContractAmount().getContractAmount();
			}
			BigDecimal lamt  = overallAmount.setScale(
					CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP);
			context.put("contractValue", nf.format(lamt) + "");
			context.put("contractValueText", NumberWordUtil.convert(lamt.toString()));
			if (getManagedBean().getContractVer().getCtContractAmount().isBondReq()
					&& !getManagedBean().getContractVer().getCtContractAmount().isBondExempted()) {
				context.put("isBondReq", true);
			} else {
				context.put("isBondReq", false);
			}
			if (getManagedBean().getContractVer().getCtContractAmount().isBondReq()
					&& !getManagedBean().getContractVer().getCtContractAmount().isBondExempted()) {
				if (getManagedBean().getCtAgreement().getCtAmendment() != null) {
					if (getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt() != null) {
						context.put("performanceBondValue", nf.format(getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt()) + "");
						context.put("performanceBondValueText", NumberWordUtil.convert(
								getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt().doubleValue() != 0 ? getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt().toString() : ""));
					}
					
				} else {
					if (getManagedBean().getCtContract().getLoa().getBondAmt() != null) {
						context.put("performanceBondValue", nf.format(getManagedBean().getCtContract().getLoa().getBondAmt()) + "");
						context.put("performanceBondValueText", NumberWordUtil.convert(
								getManagedBean().getCtContract().getLoa().getBondAmt().doubleValue() != 0 ? getManagedBean().getCtContract().getLoa().getBondAmt().toString() : ""));
					}
				}
			}
		} else if (ContractConst.TEMPLATE_AGREEMENT_PARTY.equalsIgnoreCase(parameterCode.trim())
				|| ContractConst.TEMPLATE_SUPP_AGREEMENT_PARTY.equalsIgnoreCase(parameterCode.trim())) {
			context = new VelocityContext();
			vmFile = "/ct/agreement/agreement-party.vm";
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO addressReqDTO = new AddressReqDTO();
			addressReqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
			addressReqDTO.setOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			AddressResDTO addressResDTO = addressSO.retrieveAddressTypeList(addressReqDTO);
			PmAddressType pmAddressType = null;
			if (addressResDTO.getAddressTypeList().size() > 0) {
				pmAddressType = addressResDTO.getAddressTypeList().get(0);
			}
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
			
			if (pmAddressType != null) {
				context.put("jabatanAddress", AddressUtils.getFullAddress(pmAddressType.getPmAddress()));
			} else {
				context.put("jabatanAddress", "");
			}
			context.put("supplierName", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			context.put("supplierRegNo", getManagedBean().getCtContract().getSmSupplier().getRegNo());
			context.put("supplierAddress", AddressUtils.getFullAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress()));
			context.put("contractTitle", getManagedBean().getCtContract().getContractName());

		} else if (ContractConst.TEMPLATE_AGREEMENT_SIGNING_PAGE.equalsIgnoreCase(parameterCode.trim())
				|| ContractConst.TEMPLATE_SUPP_AGREEMENT_SIGNING_PAGE.equalsIgnoreCase(parameterCode.trim())) {
			context = new VelocityContext();
			vmFile = "/ct/agreement/agreement-signing-page.vm";
			context.put("ctAgencySignerList", getManagedBean().getCtAgencySigner());
			context.put("ctSignAgencyWitnessList", getManagedBean().getCtSignAgencyWitness());
			context.put("ctSupplierSignerList", getManagedBean().getCtSupplierSigner());
			context.put("ctSupplierWitnessList", getManagedBean().getCtSupplierWitness());

			context.put("currentYear", c.get(Calendar.YEAR) + "");
			context.put("ministryName", retrieveMinistryByOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
				
			context.put("companyName", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			context.put("companyRegNo", getManagedBean().getCtContract().getSmSupplier().getRegNo());
		}

		if (vmFile != null && context != null) {
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(vmFile, context);
		}
		return content;
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
	 * The method used to convert the template into document file.
	 */
	private  void convertHtmlToDocFile(String content, String filePath) {
		WordprocessingMLPackage wordMLPackage;
		try {
			wordMLPackage = WordprocessingMLPackage.createPackage();
			AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
			afiPart.setBinaryData(content.getBytes());
			afiPart.setContentType(new ContentType("text/html"));
			Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);

			CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
			ac.setId(altChunkRel.getId());
			wordMLPackage.getMainDocumentPart().addObject(ac);
			wordMLPackage.getContentTypeManager().addDefaultContentType("html","text/html");
			wordMLPackage.save(new java.io.File(filePath));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (Docx4JException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The method used to add a new user defined agreement content.
	 * This method will generate a new record in agreement content
	 * list, user allow to enter the title and attachment. 
	 */
	public void addAgreementContents() {
		CtAgreementContent contents = new CtAgreementContent();
		if (contents.getContentType() == null) {
			contents.setContentType(new PmParameter());
			contents.setContentType(getParameterByTypeAndCode(
					ParameterConst.DOCUMENT_DEFINITION_TYPE, ParameterConst.AGREEMENT_USER_UPLOAD_DOC));
		}
		getManagedBean().getCtAgreement().getAgreementContents().add(contents);
	}

	/**
	 * The method used to delete the selected agreement content that
	 * defined by user.
	 */
	public void delAgreementContents() {
		this.getManagedBean().getCtAgreement().getAgreementContents().remove(
				getManagedBean().getSelectedAgreementContent());
	}

	/**
	 * The method used for adding representative of signer(s)
	 * for agency party.
	 * A warning message will display on screen when duplicate
	 * representative is added.
	 */
	public void addAgencySignerParty() {	   
		if (!getManagedBean().isValidateWitness()) {
			for (CtAgreementParty agreementParty  : getManagedBean().getCtAgencySigner()) {
				if (agreementParty.getOfficer().getUserId() == null) {
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.contract.signer", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				 }
			 }
		     CtAgreementParty ctAgreementParty = new CtAgreementParty();
		     ctAgreementParty.setAgreementPartyType(new PmParameter());
		     ctAgreementParty.setAgreementPartyType(getParameterByTypeAndCode(
		    		 ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_AGENCY_SIGNER));
	         ctAgreementParty.setOfficer(new PmUser());
		     if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)){
		    	 ctAgreementParty.setOrgProfile(getManagedBean().getCtContract().getOwnerOrgProfile());
		     }else{
		    	 ctAgreementParty.setOrgProfile(new PmOrgProfile());
		     }
		     getManagedBean().getCtAgencySigner().add(ctAgreementParty);
		} else {
			 throw new EpException(MessageConst.ERROR_755003);
		}
	}

	/**
	 * The method used to delete the selected signer(s) for agency party.
	 */
	public void deleteAgencySignerParty() {
		getManagedBean().setValidateWitness(false);
		for (int i = 0; i < selectedAgencySigner.length; i++) {
			CtAgreementParty ctAgreementParty = selectedAgencySigner[i];
			this.getManagedBean().getCtAgencySigner().remove(ctAgreementParty);
		}
		selectedAgencySigner = null;
	}
	
	/**
	 * The method used for adding representative of witness(s)
	 * for agency party.
	 * A warning message will display on screen when duplicate
	 * representative is added.
	 */
	public void addAgencyWitnes(int temp) {	
		
		if(getManagedBean().getCtSignAgencyWitness().size() > 0 ) { 
		     for(CtAgreementParty agencyWitness : getManagedBean().getCtSignAgencyWitness()) {
			      if (agencyWitness.getOfficerName() == null || agencyWitness.getOfficerName().isEmpty()) {
				      throw new EpException(MessageConst.ERROR_100022, 
						   new String[]{ResourceBundleUtils.getMessage("lbl.ct.name.agency.witness", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			    
			      } if (agencyWitness.getOfficerIc() == null || agencyWitness.getOfficerIc().isEmpty()) {
				         throw new EpException(MessageConst.ERROR_100022, 
						     new String[]{ResourceBundleUtils.getMessage("lbl.ct.ic.agency.witness", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		      
			      } if (agencyWitness.getOfficerPosition() == null || agencyWitness.getOfficerPosition().isEmpty()) {
				           throw new EpException(MessageConst.ERROR_100022, 
						      new String[]{ResourceBundleUtils.getMessage("lbl.ct.position.agency.witness", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			      }
		     }
	     }
		
		getManagedBean().setValidateWitness(false);
		getManagedBean().setAgencyOfficerIcNo(new ArrayList<String>());			
		if (getManagedBean().getCtAgencySigner() != null && !getManagedBean().getCtAgencySigner().isEmpty()) {
	       	for(CtAgreementParty agencySigner: getManagedBean().getCtAgencySigner()) {
	       		getManagedBean().getAgencyOfficerIcNo().add(agencySigner.getOfficerIc());
	       	}
		}
		
		getManagedBean().setAgencyWitnessIcNo(new ArrayList<String>());
		if (getManagedBean().getCtSignAgencyWitness() != null 
				&& !getManagedBean().getCtSignAgencyWitness().isEmpty()) {
			for (CtAgreementParty agencyWitness : getManagedBean().getCtSignAgencyWitness()) {
				if(getManagedBean().getAgencyOfficerIcNo() != null 
						&& !getManagedBean().getAgencyOfficerIcNo().isEmpty()
	       				&& getManagedBean().getAgencyOfficerIcNo().contains(agencyWitness.getOfficerIc())) {
					getManagedBean().setValidateWitness(true);
					throw new EpException(MessageConst.ERROR_755005, new String[]{agencyWitness.getOfficerIc()});
				} else if (getManagedBean().getAgencyWitnessIcNo() != null 
						&& !getManagedBean().getAgencyWitnessIcNo().isEmpty()
       		    		&& getManagedBean().getAgencyWitnessIcNo().contains(agencyWitness.getOfficerIc())) {
					getManagedBean().setValidateWitness(true);
					throw new EpException(MessageConst.ERROR_755005, new String[]{agencyWitness.getOfficerIc()});
       		    }  else if (getManagedBean().getSupplierOfficerIcNo() != null 
       		    		&& !getManagedBean().getSupplierOfficerIcNo().isEmpty()
       		    		&& getManagedBean().getSupplierOfficerIcNo().contains(agencyWitness.getOfficerIc())) {
                	 getManagedBean().setValidateWitness(true);
	                 throw new EpException(MessageConst.ERROR_755009, new String[]{agencyWitness.getOfficerIc()});
   		        }  else if (getManagedBean().getSupplierWitnessIcNo() != null 
   		        		&& !getManagedBean().getSupplierWitnessIcNo().isEmpty()
   		        		&& getManagedBean().getSupplierWitnessIcNo().contains(agencyWitness.getOfficerIc())) {
   		        	getManagedBean().setValidateWitness(true);
   		        	throw new EpException(MessageConst.ERROR_755012, new String[]{agencyWitness.getOfficerIc()});
       		    } else {
       		    	getManagedBean().getAgencyWitnessIcNo().add(agencyWitness.getOfficerIc());
	            }
			}
		}
		if (!getManagedBean().isValidateWitness() && temp == 2) {			
			CtAgreementParty ctAgreementParty = new CtAgreementParty();
	        ctAgreementParty.setAgreementPartyType(new PmParameter());
	        ctAgreementParty.getAgreementPartyType().setParameterId(
	        		getParameterByTypeAndCode(ParameterTypeConst.AGREEMENT_PARTY_TYPE, 
	        				ParameterTypeConst.AGREEMENT_AGENCY_WITNESS).getParameterId());
	        getManagedBean().getCtSignAgencyWitness().add(ctAgreementParty);
	    }
	}
	
	/**
	 * The method used to delete the selected witness(s) for agency party.
	 */
	public void deleteAgencyWitnes() {
		getManagedBean().setValidateWitness(false);
		for (int i = 0; i < selectedAgencyWitnes.length; i++) {
			CtAgreementParty ctAgreementParty = selectedAgencyWitnes[i];
			getManagedBean().getCtSignAgencyWitness().remove(ctAgreementParty);
		}
		selectedAgencyWitnes = null;
	}
	
	/**
	 * The method used to generate the contract signer information once
	 * user select a representative for agency.
	 * A warning message will display on screen when duplicate
	 * representative is selected.
	 */
	public void setUserValueAgency(CtAgreementParty ctParty) {
		int counter = 0;
		getManagedBean().setValidateWitness(false);
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
			if( ctParty.getOfficer() != null  && ctParty.getOfficer().getUserId() != null 
					&& ctParty.getOrgProfile().getOrgProfileId().equals(party.getOrgProfile().getOrgProfileId())
					&& ctParty.getOfficer().getUserId().equals(party.getOfficer().getUserId())) {
				counter++;
				if (counter == 2) {
					getManagedBean().setValidateWitness(true);
					throw new EpException(MessageConst.ERROR_755003, new String[]{ctParty.getOfficerIc()});
				}
				
			} else if(getManagedBean().getAgencyWitnessIcNo() != null && ctParty.getOfficerIc() != null
					&& !getManagedBean().getAgencyWitnessIcNo().isEmpty()
					&& getManagedBean().getAgencyWitnessIcNo().contains(ctParty.getOfficerIc())) {
				getManagedBean().setValidateWitness(true);
				throw new EpException(MessageConst.ERROR_755004, new String[]{ctParty.getOfficerIc()});
			}
		}
	}
	
	/**
	 * The method used for adding representative of signer(s)
	 * for supplier party.
	 * A warning message will display on screen when duplicate
	 * representative is added.
	 */
	public void addSupplierSigner() {
		if (!getManagedBean().isValidateWitness()) {
			CtAgreementParty ctAgreementParty = new CtAgreementParty();
			ctAgreementParty.setAgreementPartyType(new PmParameter());
			ctAgreementParty.getAgreementPartyType().setParameterId(
					getParameterByTypeAndCode(ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_SUPPLIER_SIGNER).getParameterId());
			if (ctAgreementParty.getOfficer() == null) {
				ctAgreementParty.setOfficer(new PmUser());
			}
			getManagedBean().getCtSupplierSigner().add(ctAgreementParty);
		} else {
			throw new EpException(MessageConst.ERROR_755003);
		}
	}

	/**
	 * The method used to delete the selected signer(s) for supplier party.
	 */
	public void deleteSupplierSigner() {
		getManagedBean().setValidateWitness(false);
		for (int i = 0; i < selectedSupplierSigner.length; i++) {
			CtAgreementParty ctAgreementParty = selectedSupplierSigner[i];
			getManagedBean().getCtSupplierSigner().remove(ctAgreementParty);
		}
		selectedSupplierSigner = null;
	}	
	
	/**
	 * The method used for adding representative of witness(s)
	 * for supplier party.
	 * A warning message will display on screen when duplicate
	 * representative is added.
	 */
	public void addSupplierWitness(int temp) {
		
		if(getManagedBean().getCtSupplierWitness().size() > 0) {
		       for (CtAgreementParty supplierWitness : getManagedBean().getCtSupplierWitness()) {
			           if (supplierWitness.getOfficerName() == null || supplierWitness.getOfficerName().isEmpty()) {
				            throw new EpException(MessageConst.ERROR_100022, 
						        new String[]{ResourceBundleUtils.getMessage("lbl.ct.name.supplier.witness", 
								    ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			         
			           } if (supplierWitness.getOfficerIc() == null || supplierWitness.getOfficerIc().isEmpty()) {
				              throw new EpException(MessageConst.ERROR_100022, 
						         new String[]{ResourceBundleUtils.getMessage("lbl.ct.ic.supplier.witness", 
								     ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			         
			           } if (supplierWitness.getOfficerPosition() == null || supplierWitness.getOfficerPosition().isEmpty()) {
				            throw new EpException(MessageConst.ERROR_100022, 
						      new String[]{ResourceBundleUtils.getMessage("lbl.ct.position.supplier.witness", 
								 ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			           }
		         }
		   }
		
		getManagedBean().setValidateWitness(false);
		getManagedBean().setSupplierOfficerIcNo(new ArrayList<String>());
		if (getManagedBean().getCtSupplierSigner() != null && !getManagedBean().getCtSupplierSigner().isEmpty()) {
			for(CtAgreementParty supplierSigner: getManagedBean().getCtSupplierSigner()) {
				getManagedBean().getSupplierOfficerIcNo().add(supplierSigner.getOfficerIc());
			}
     	}
		
		getManagedBean().setSupplierWitnessIcNo(new ArrayList<String>());
		if (getManagedBean().getCtSupplierWitness() != null && !getManagedBean().getCtSupplierWitness().isEmpty()) {
			for (CtAgreementParty suplierWitness : getManagedBean().getCtSupplierWitness()) {
				if (getManagedBean().getSupplierOfficerIcNo() != null
						&& !getManagedBean().getSupplierOfficerIcNo().isEmpty() 
	       	       		&& getManagedBean().getSupplierOfficerIcNo().contains(suplierWitness.getOfficerIc())) {
					getManagedBean().setValidateWitness(true);
					throw new EpException(MessageConst.ERROR_755009, new String[]{suplierWitness.getOfficerIc()});
				} else if (getManagedBean().getSupplierWitnessIcNo() != null 
						&& !getManagedBean().getSupplierWitnessIcNo().isEmpty()
	       	        	&& getManagedBean().getSupplierWitnessIcNo().contains(suplierWitness.getOfficerIc())) {
					getManagedBean().setValidateWitness(true);
					throw new EpException(MessageConst.ERROR_755010, new String[]{suplierWitness.getOfficerIc()});
				} else if (getManagedBean().getAgencyOfficerIcNo() != null 
						&& !getManagedBean().getAgencyOfficerIcNo().isEmpty()
	       	       		&& getManagedBean().getAgencyOfficerIcNo().contains(suplierWitness.getOfficerIc())) {
					getManagedBean().setValidateWitness(true);
   		       		throw new EpException(MessageConst.ERROR_755005, new String[]{suplierWitness.getOfficerIc()});
   		       	} else if (getManagedBean().getAgencyWitnessIcNo() != null
   		       			&& !getManagedBean().getAgencyWitnessIcNo().isEmpty()
   		       			&& getManagedBean().getAgencyWitnessIcNo().contains(suplierWitness.getOfficerIc())) {
	       	     	getManagedBean().setValidateWitness(true);
	       	     	throw new EpException(MessageConst.ERROR_755011, new String[]{suplierWitness.getOfficerIc()});
   		       	} else {
   		       		getManagedBean().getSupplierWitnessIcNo().add(suplierWitness.getOfficerIc());
   		       	}
			}
		}
		if(!getManagedBean().isValidateWitness() && temp == 2) {
			CtAgreementParty ctAgreementParty = new CtAgreementParty();
			ctAgreementParty.setAgreementPartyType(new PmParameter());
			ctAgreementParty.getAgreementPartyType().setParameterId(
					getParameterByTypeAndCode(ParameterTypeConst.AGREEMENT_PARTY_TYPE,
							ParameterTypeConst.AGREEMENT_SUPPLIER_WITNESS).getParameterId());
			getManagedBean().getCtSupplierWitness().add(ctAgreementParty);	    
		}
	}
	
	/**
	 * The method used to delete the selected witness(s) for supplier party.
	 */
	public void deleteSupplierWitness() {
		getManagedBean().setValidateWitness(false);
		for (int i = 0; i < selectedSupplierWitnes.length; i++) {
			CtAgreementParty ctAgreementParty = selectedSupplierWitnes[i];
			getManagedBean().getCtSupplierWitness().remove(ctAgreementParty);
		}
		selectedSupplierWitnes = null;
	}
	
	/**
	 * The method used to generate the contract signer information once
	 * user select a representative for supplier.
	 * A warning message will display on screen when duplicate
	 * representative is selected.
	 */
	public void setSupplierUserValue(CtAgreementParty ctParty) {
		SmPersonnel personnel = getManagedBean().getSupplierUserMap().get(ctParty.getOfficerName());
		getManagedBean().setValidateWitness(false);
		if (personnel != null) {
			ctParty.setOfficerIc(personnel.getIdentificationNo());
			ctParty.setOfficerPosition(personnel.getDesignation());
		} else {
			ctParty.setOfficerIc(null);
			ctParty.setOfficerPosition(null);
		}
	
		int counter = 0;
		for (CtAgreementParty party : getManagedBean().getCtSupplierSigner()) {
			if (ctParty.getOfficerName() == null) {
			    	party.setOfficerIc(null);
				   party.setOfficerPosition(null);
			}
			if (ctParty.getOfficerIc() != null && party.getOfficerIc() != null
					&&  party.getOfficerIc().equalsIgnoreCase(ctParty.getOfficerIc())) {
				counter++;
				if (counter == 2) {
					getManagedBean().setValidateWitness(true);
					throw new EpException(MessageConst.ERROR_755007, new String[]{ctParty.getOfficerIc()});
				}
			} else if (getManagedBean().getSupplierOfficerIcNo() != null && ctParty.getOfficerIc() != null
					&& !getManagedBean().getSupplierOfficerIcNo().isEmpty() && getManagedBean().getSupplierOfficerIcNo().contains(ctParty.getOfficerIc())) {
				getManagedBean().setValidateWitness(true);
				throw new EpException(MessageConst.ERROR_755008, new String[]{ctParty.getOfficerIc()});
			}
		}
	}
	
	/**
	 * The method used for validating the PTJ of agency.
	 * A warning will display on screen when there is duplicated
	 * agency PTJ.
	 *
	 * @param  index       the current index of newly added record
	 * @param  agencyList  the list of agency that on screen
	 * @param  ptjProfile  the ptj profile that newly added
	 */
	public void validateDuplicateAgencyPtj(Long orgProfileId ) {
		
		    int counter =0;
			for (int i = 0; i < getManagedBean().getCtAgencySigner().size(); i++) {
				CtAgreementParty agreementParty = getManagedBean().getCtAgencySigner().get(i);
				if (agreementParty.getOrgProfile().getOrgProfileId().equals(orgProfileId)) {
					counter++;
					if (counter == 2) {
						throw new EpException(MessageConst.ERROR_755013,
								new String[] { agreementParty.getOrgProfile().getOrgName()});
					}
				}
			}
	 }
	
	/**
	 * The method used to check whether approver setting tab menu is required to display.
	 * 
	 * @return true, if approver setting tab menu is required to display
	 *         false, if approver setting tab menu is not required
     */
	public boolean isApproverSetting() {
		if (isContractAdmin() && (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_DRAFT_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_AC_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_AC_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_DRAFT_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FINALIST_AGREEMENT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FINALIST_AGREEMENT_HT_NAME))) {
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
	
	public String previewAction() {
		String combinedContentHtml ="";
		PmOrgProfile pmOrgProfile = getManagedBean().getCtContract().getOwnerOrgProfile();
		IAddressSO addressSO = null;
		PmAddressType pmAddressType = null;
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
		    
		    
		    if (getManagedBean().getContractVer().getCtContractAmount().isBondReq()
		    		&& !getManagedBean().getContractVer().getCtContractAmount().isBondExempted()) {
				if (getManagedBean().getCtAgreement().getCtAmendment() != null) {
					if (getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt() != null) {
						bondAmount = af.format(getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt());
						bondAmountTxt =  NumberWordUtil.convert(
								getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt().doubleValue() != 0 ? getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondAmt().toString() : "");
						bondPercentage = pf.format(getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondPercentage());
						//bondPercentageTxt =  NumberWordUtil.convert(getManagedBean().getCtAgreement().getCtAmendment().getAmendmentLoa().getBondPercentage().toString());
						bondPercentageTxt = bondPercentage;
					}						
				} else {
					if (getManagedBean().getCtContract().getLoa().getBondAmt() != null) {
						bondAmount = af.format(getManagedBean().getCtContract().getLoa().getBondAmt());
						bondAmountTxt =  NumberWordUtil.convert(
								getManagedBean().getCtContract().getLoa().getBondAmt().doubleValue() != 0 ? getManagedBean().getCtContract().getLoa().getBondAmt().toString() : "");
						bondPercentage = pf.format(getManagedBean().getCtContract().getLoa().getBondPercentage());
						//bondPercentageTxt =  NumberWordUtil.convert(getManagedBean().getCtContract().getLoa().getBondPercentage().toString());
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
		    
		    addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if (resDTO.getAddressTypeList().size() > 0) {
				pmAddressType = resDTO.getAddressTypeList().get(0);
			}
			
		    StringBuilder ptjAddressStr = new StringBuilder();				
			if (pmAddressType != null) {
				PmAddress pmAddress = pmAddressType.getPmAddress();
				if (pmAddress != null) {
					ptjAddressStr.append(pmAddress.getAddress1());
					ptjAddressStr.append(",<br />");
					ptjAddressStr.append(pmAddress.getAddress2());
					ptjAddressStr.append(",<br />");
					ptjAddressStr.append(pmAddress.getPostcode());
					ptjAddressStr.append(" ");
					ptjAddressStr.append(pmAddress.getPmCity() != null ? pmAddress.getPmCity().getCityName() : "");
					ptjAddressStr.append(",<br />");
					ptjAddressStr.append(pmAddress.getPmState() != null ? pmAddress.getPmState().getStateName() : "");
					
					ptjAddress = ptjAddressStr.toString();
					
					if (pmAddress.getPhoneNo() != null)
						ptjTelNo = pmAddress.getPhoneNo();
					if (pmAddress.getFaxNo() != null)
						ptjFaksNo = pmAddress.getFaxNo();
					if (pmAddressType.getPmContacts().size() > 0) {
						if (pmAddressType.getPmContacts().get(0).getEmail() != null)
							ptjEmel = pmAddressType.getPmContacts().get(0).getEmail();
					}
					
				}
			}
			if (getManagedBean().getContractVer().getCtContractValue().getSupplierName() != null)
				supplierName = getManagedBean().getContractVer().getCtContractValue().getSupplierName();
			String address = AddressUtils.getFullAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
			if (address != null)
				supplierAddress = address;
			if (getManagedBean().getContractVer().getCtContractValue().getSupplierPhoneNo() != null)
				supplierTelNo = getManagedBean().getContractVer().getCtContractValue().getSupplierPhoneNo();
			if (getManagedBean().getContractVer().getCtContractValue().getSupplierFaxNo() != null)
				supplierFaksNo = getManagedBean().getContractVer().getCtContractValue().getSupplierFaxNo();
			if (getManagedBean().getContractVer().getCtContractValue().getSupplierEmail() != null)
				supplierEmel = getManagedBean().getContractVer().getCtContractValue().getSupplierEmail();		
			
			
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
			e.printStackTrace();
		}
		return combinedContentHtml;
	}
	
	public static String mergePreviewAction(TreeNode root, String outputFileName, String action) throws Exception {
		//action= P-Preview, S-Save
		if(log.isDebugEnabled()){
			log.debug("Start mergePreviewAction");
		}
	
		List<TplDocSection> inputStreamList = new ArrayList<TplDocSection>();
		getDocSectionList(1,root,inputStreamList);

		String combinedContent = "";
		String combinedContentHtml="";
		for (TplDocSection obj : inputStreamList) {
			String text = "";
			if(obj.getSectionTitle() != null && !obj.getSectionTitle().isEmpty()){
				text += "<span><strong>" + obj.getSectionTitle() + "</strong></span><br />";
			}
			
			if(obj.getSectionContent() != null && !obj.getSectionContent().isEmpty()){
				
				if(obj.getSectionContent().trim().startsWith("<p>") && obj.getSectionContent().trim().endsWith("</p>")){
					obj.setSectionContent(obj.getSectionContent().trim().replaceFirst("<p>", "<span>"));
					obj.setSectionContent(obj.getSectionContent().trim().substring(0,obj.getSectionContent().trim().length()-4)+ "</span>");
				}
				text += obj.getSectionContent();
			}
						
			text = insertNumberingAndIndentHtml(text,  obj.getSectionNumber(), obj.getSectionLevel());
			
			combinedContent += text;
		}
		
		if(action.equals("P")){
						
			//=========================== START PDF ======================================
//			log.info("combinedContent ==== " + combinedContent);
			
			combinedContentHtml = "<html><body>" 
					+ combinedContent + "</body></html>";	
			
	    	//=========================== END PDF ======================================
			
		}
		
		if(action.equals("S")){
//			DocumentSettingsPart dsp = target.getMainDocumentPart().getDocumentSettingsPart();
//			ObjectFactory factory = new ObjectFactory();
//			CTDocProtect protection = factory.createCTDocProtect();
//			protection.setEdit(STDocProtect.READ_ONLY);
//			protection.setEnforcement(false);
//			dsp.getJaxbElement().setDocumentProtection(protection);					
//			target.save(new java.io.File(templateFilePath+outputFileName+".docx"));
		}
		
		
    	if(log.isDebugEnabled()){
			log.debug("End mergePreviewAction");
		}
		return combinedContentHtml;

	}
	
	private static String insertNumberingAndIndentHtml(String text, String indexKey, int templateLevel){		
		String newText = "";
		text = text.replaceAll("&nbsp;", "&#160;");
		int width = 5 + (templateLevel-2)*3 ;
		
		switch (templateLevel) {
		case 1:
			newText = "<table width=\"100%\"><tr><td width=\"5%\" valign=\"top\">" + indexKey + "</td><td width=\"95%\">" + text + "</td></tr></table>";
			break;
			
		default:
			newText = "<table width=\"100%\"><tr><td width=\""+width+"%\">&#160;</td><td width=\"5%\" valign=\"top\">" + indexKey + "</td><td width=\""+(95-width)+"%\">" + text + "</td></tr></table>";
			
		}
		return newText;
	}

  /*
   * Method to create tree node for doc section
   * Method to make tree 
   * */
  public static void getDocSectionList(int level,TreeNode node,List<TplDocSection> inputStreamList){
	 if(node != null && node.getData() != null){
		 for(TreeNode parent : node.getChildren()){
			  TplDocSection docSec = (TplDocSection)parent.getData();
			  docSec.setSectionLevel(level);
			  inputStreamList.add(docSec);
			  if(!parent.isLeaf()){
				  getDocSectionList(level+1,parent,inputStreamList);	
			  }
		 }
	}
}
  
  /*
   * Call method retrievePtjBylovCondition to 
   * retrieve value of ministry from lov
   * */
  public String retrievePtjBylovCondition() {
	  
	      String condition = "recordStatusFlg^1|";
		  condition = condition +OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value());
		  condition = condition + "^"+  retrieveMinistryOrgProfile(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId();
	       return condition;
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
		    if(getManagedBean().isAgencySignerDropDown()) {
		   
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
		    }
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
		    if(getManagedBean().getOfficerProfileId() != null && getManagedBean().isAgencySignerDropDown()) {
		         IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
		    		ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(getManagedBean().getOfficerProfileId());
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
			e.printStackTrace();
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
		getManagedBean().setOfficerProfileId(null);
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjProfileId(null);
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
		getManagedBean().setMinistryProfileId(null);
		getManagedBean().setOfficerProfileId(null);
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjProfileId(null);
		getManagedBean().setAgencySignerDropDown(true);
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
		    if(getManagedBean().getJabatanProfileId() != null && getManagedBean().isAgencySignerDropDown()) {
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
			e.printStackTrace();
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
			    
		 long startTime1 = System.currentTimeMillis();
		log.info("Start Time >>>>>>>>>>>>>>>>>>>>" +startTime1);
	     IAgreementSO agreementSO = null;
		 Long profileId = null;
		 CtAgreementParty agreementParty = new CtAgreementParty();
		 ContractAgreementReqDTO reqDTO = null;
		
		 try{
		       
			        if(getManagedBean().getMinistryProfileId() != null) {
			    	    profileId = getManagedBean().getMinistryProfileId();
			    	    agreementParty.setMinistryName(retrieveOrgProfileByProfileId(profileId));
			        }
			      
			        if(getManagedBean().getOfficerProfileId() != null){
			    	    profileId = getManagedBean().getOfficerProfileId();
			    	    agreementParty.setPengawaiPengawalName(retrieveOrgProfileByProfileId(profileId));
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
				   getManagedBean().setAgencySignerDropDown(false);
				   
				   long endTime1 = System.currentTimeMillis();
				   log.info("End Time1 >>>>>>>>>>>>>>>>>>>>" +endTime1);
				  log.info("Total Time1 >>>>>>>>>>>>>>>>>>>>" +(endTime1 - startTime1));
				
		 
		  } catch (Exception e) {
				e.printStackTrace();
		  }
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
    	    	ex.printStackTrace();
    	    }
    	    return null;
      }
    
    /**
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
     */
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.contract.agreement.creation",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		if (getManagedBean().getAgreementType().equalsIgnoreCase(ContractConst.SUPPLEMENTAL_AGREEMENT)) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.contract.supp.agreement.creation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());;
		} 
		return portletTitle;
	} 
	
	/**
	 * The method used to delete the uploaded agency file.
	 */    
    public void deleteContentAttachement(){
    	CtAgreementContent agContect =  getManagedBean().getCtAgreement().getAgreementContents().get(getManagedBean().getVarIndex());
    	getManagedBean().getCtAgreement().getAgreementContents().remove(getManagedBean().getVarIndex());
    	agContect.setFileName(null);
 	    agContect.setFilePath(null);
    	getManagedBean().getCtAgreement().getAgreementContents().add(getManagedBean().getVarIndex(), agContect);
    }
    
    /**
	 * The method used to set 
	 * number of index of list in datatable 
	 */   
    public void setIndexProperty(int index){
    	getManagedBean().setVarIndex(index);
    }
}
