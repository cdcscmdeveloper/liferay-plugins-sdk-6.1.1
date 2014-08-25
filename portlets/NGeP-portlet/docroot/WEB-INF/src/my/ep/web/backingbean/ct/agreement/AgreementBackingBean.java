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
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
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
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.agreement.CTAgreementDataType;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementContent;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.agreement.AgreementManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
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

	private boolean supplementValue = true;
	private ContractBackingBean ctBB = null;
	private CtAgreementParty[] selectedAgencyWitnes;
	private CtAgreementParty[] selectedAgencySigner;
	private CtAgreementParty[] selectedSupplierWitnes;
	private CtAgreementParty[] selectedSupplierSigner;

	public AgreementBackingBean() {
		if (this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(
					new String[] { BpmOutcomeConst.BPM_SAVE,
							BpmOutcomeConst.BPM_SUBMIT });
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}

		if (ctBB == null) {
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
			ctBB.initializeRemarkAndAttachement(getManagedBean()
					.getRemarkList(), getManagedBean().getAttachmentList());
		}
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void preRenderAgreement() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"agreementTab.xhtml?faces-redirect=true");
	}

	public void preRenderView() throws NamingException {
		IContractSO contractSO = null;
		IAgreementSO agreementSO = null;

		try {
			HttpServletRequest servletRequest = PortalUtil
					.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			if (taskId == null)
				return;

			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setSelectedTabIndex(0);

			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			getManagedBean().setTaskName(res.getTaskName());

			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			getManagedBean().setAction(outcome);

			ContractAgreementReqDTO ctReqDTO = new ContractAgreementReqDTO();
			CTAgreementDataType dataType = (CTAgreementDataType) obj;
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setAgreementId(new Long(dataType.getAgreementId()));
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IAgreementSO.class);

			ContractAgreementResDTO resDTO = new ContractAgreementResDTO();
			String agrrementType = dataType.getAgreementType() == null ? ""
					: dataType.getAgreementType();
			getManagedBean().setAgreementType(agrrementType);

			if (getManagedBean().getAgreementType() != null
					&& getManagedBean().getAgreementType().equalsIgnoreCase(
							ContractConst.SUP_AGREEMENT)) {
				ctReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);

				if (dataType.getAgreementId().intValue() == 0) {
					ContractReqDTO conReqDTO = new ContractReqDTO();
					conReqDTO.setContractVerId(new Long(dataType.getDocId()));
					contractSO = (IContractSO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.CONTRACT_MANAGEMENT,
									IContractSO.class);
					ContractResDTO conResDTO = contractSO
							.retrieveContractVer(conReqDTO);
					ctReqDTO.setCtContractVer(conResDTO.getCtContractVer());
					resDTO = agreementSO.setSupplementalAgreement(ctReqDTO);
					getManagedBean().setContractVer(
							conResDTO.getCtContractVer());
				} else {
					resDTO = agreementSO.retrieveAgreement(ctReqDTO);
				}
			} else {
				ctReqDTO.setDocType(ParameterConst.DOCTYPE_AGREEMENT);
				resDTO = agreementSO.retrieveAgreement(ctReqDTO);
			}

			setCtAgreementDetails(resDTO);
			if (res.getTaskName().equalsIgnoreCase(
					CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)) {
				getManagedBean().setUploadStampedAndSignedPage(true);
				getManagedBean().setUploadFinalAgreement(true);
			} else if (res.getTaskName().equalsIgnoreCase(
					CTProcessNameConst.PRINT_FINALIST_AGREEMENT_HT_NAME)) {
				getManagedBean().setUploadFinalAgreement(true);
			}

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							ContractManagementConst.NAV_TO_CONTRACT_AGREEMENT);

		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			contractSO = null;
			agreementSO = null;
		}
	}

	private void setCtAgreementDetails(ContractAgreementResDTO resDTO)
			throws NamingException {
		if (resDTO.getAgreement() != null) {

			getManagedBean().setCtAgreement(resDTO.getAgreement());
			getManagedBean().setCtContract(
					resDTO.getAgreement().getCtContract());

			if (getManagedBean().getContractVer() == null)
				getManagedBean().setContractVer(
						resDTO.getAgreement().getCtContract()
								.getCurrentContractVer());

			if (this.getManagedBean().getCtContract().getFulfillmentType() == null)
				this.getManagedBean().getCtContract()
						.setFulfillmentType(new PmParameter());

			this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());

			if (this.getManagedBean().getCtAgencySigner().size() > 0)
				this.getManagedBean().getCtAgencySigner().clear();

			if (this.getManagedBean().getCtSupplierSigner().size() > 0)
				this.getManagedBean().getCtSupplierSigner().clear();

			if (this.getManagedBean().getCtSignAgencyWitness().size() > 0)
				this.getManagedBean().getCtSignAgencyWitness().clear();

			if (this.getManagedBean().getCtSupplierWitness().size() > 0)
				this.getManagedBean().getCtSupplierWitness().clear();

			for (CtAgreementParty ctAgreementParty : getManagedBean()
					.getCtAgreement().getCtSignParties()) {
				if (ctAgreementParty
						.getAgreementPartyType()
						.getParameterId()
						.equals(retrieveParameterByCodeType(
								ParameterTypeConst.AGREEMENT_PARTY_TYPE,
								ParameterTypeConst.AGREEMENT_AGENCY_SIGNER)
								.getParameterId())) {

					if (ctAgreementParty.getOrgProfile() == null) {
						ctAgreementParty.setOrgProfile(new PmOrgProfile());
					}

					if (ctAgreementParty.getOrgProfile().getParentOrgProfile() == null) {
						ctAgreementParty.getOrgProfile().setParentOrgProfile(
								new PmOrgProfile());
					}
					this.getManagedBean().getCtAgencySigner()
							.add(ctAgreementParty);

				} else if (ctAgreementParty
						.getAgreementPartyType()
						.getParameterId()
						.equals(retrieveParameterByCodeType(
								ParameterTypeConst.AGREEMENT_PARTY_TYPE,
								ParameterTypeConst.AGREEMENT_AGENCY_WITNESS)
								.getParameterId())) {
					this.getManagedBean().getCtSignAgencyWitness()
							.add(ctAgreementParty);

				} else if (ctAgreementParty
						.getAgreementPartyType()
						.getParameterId()
						.equals(retrieveParameterByCodeType(
								ParameterTypeConst.AGREEMENT_PARTY_TYPE,
								ParameterTypeConst.AGREEMENT_SUPPLIER_SIGNER)
								.getParameterId())) {
					this.getManagedBean().getCtSupplierSigner()
							.add(ctAgreementParty);

				} else {
					this.getManagedBean().getCtSupplierWitness()
							.add(ctAgreementParty);
				}
			}
		} else {
			getManagedBean().setCtAgreement(new CtAgreement());
		}

		this.getManagedBean().getApproverModel()
				.setSource(getAvailableApproverList());
		if (resDTO.getApproverList() != null
				&& resDTO.getApproverList().size() != 0) {
			this.getManagedBean().getApproverModel()
					.setTarget(resDTO.getApproverList());
			this.getManagedBean().getApproverModel().getSource()
					.removeAll(resDTO.getApproverList());
		}

		String docType = ParameterConst.DOCTYPE_AGREEMENT;
		if (getManagedBean().getAgreementType().equalsIgnoreCase(
				ContractConst.SUP_AGREEMENT)) {
			docType = ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT;
		}

		getManagedBean().setFileDirectory(
				DocumentRepositoryUtils.getUploadDirectoryPath(
						ModuleCodeConst.CONTRACT_MANAGEMENT, docType,
						getManagedBean().getCtContract().getContractNo()));
		initializeRemarks();
		ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(),
				getManagedBean().getAttachmentList());
	}

	public String saveAgreement(String action) throws Exception {
		IAgreementSO agreementSO = null;
		try {
			validateAgreement(action);
			ContractAgreementReqDTO reqDTO = new ContractAgreementReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IAgreementSO.class);
			if (getManagedBean().getAgreementType().equalsIgnoreCase(
					ContractConst.SUP_AGREEMENT)) {
				reqDTO.setAgreementType(ContractConst.SUP_AGREEMENT);
				reqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);
			} else {
				reqDTO.setAgreementType(ContractConst.AGREEMENT);
				reqDTO.setDocType(ParameterConst.DOCTYPE_AGREEMENT);
			}

			getManagedBean().getCtAgreement().setCtContract(
					getManagedBean().getCtContract());
			reqDTO.setToken(token);
			reqDTO.setOutcome(action);
			reqDTO.setUserRole(getUserRole());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());

			if (getManagedBean().getCtAgreement().getCtSignParties().size() > 0) {
				getManagedBean().getCtAgreement().getCtSignParties().clear();
			}
			getManagedBean().getCtAgreement().getCtSignParties()
					.addAll(this.getManagedBean().getCtAgencySigner());
			getManagedBean().getCtAgreement().getCtSignParties()
					.addAll(this.getManagedBean().getCtSupplierSigner());
			getManagedBean().getCtAgreement().getCtSignParties()
					.addAll(this.getManagedBean().getCtSignAgencyWitness());
			getManagedBean().getCtAgreement().getCtSignParties()
					.addAll(this.getManagedBean().getCtSupplierWitness());

			boolean isBrandNew = getManagedBean().getCtAgreement().isBrandNew();

			reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			reqDTO.setUserMap(getManagedBean().getUserMap());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel()
					.getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel()
					.getSource());
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
				} else if (action
						.equalsIgnoreCase(BpmOutcomeConst.BPM_COMPLETE)) {
					messageId = MessageConst.INFO_700001;
				}
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														messageId,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		reqDTO.getDocType()),
																this.getManagedBean()
																		.getCtContract()
																		.getContractNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)
						|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean()
								.getTaskId() == null)) {
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else if (isBrandNew) {
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					setCtAgreementDetails(resDTO);
				}
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			agreementSO = null;
		}
		return null;
	}

	private void validateAgreement(String outcome) {
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
			if (getManagedBean().getRemarkList().get(0).getRemark().length() == 0) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { "Remark" });
			}
		}

		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
			if (getManagedBean().getApproverModel().getTarget().size() == 0) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { "Selected approver(s)" });
			}
		}

		if (getManagedBean().getTaskName().equalsIgnoreCase(
				CTProcessNameConst.PRINT_FINALIST_AGREEMENT_HT_NAME)) {
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_PRINT)
					&& getManagedBean().getCtAgreement().getFileName() == null) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { "Attachment for Finalist Agreement" });
			}
		} else if (getManagedBean().getTaskName().equalsIgnoreCase(
				CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)) {
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_COMPLETE)
					&& getManagedBean().getCtAgreement().getSignedPageName() == null) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { "Attachment for Agreement Signed Page" });
			}
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_COMPLETE)
					&& getManagedBean().getCtAgreement().getStampedPageName() == null) {
				throw new EpException(
						MessageConst.ERROR_100022,
						new String[] { "Attachment for Agreement Stamped Page" });
			}
		}

		if (getManagedBean().getCtAgencySigner().size() == 0) {
			throw new EpException(MessageConst.ERROR_100023,
					new String[] { "Agency Signer" });
		} else {
			for (CtAgreementParty agencySigner : getManagedBean()
					.getCtAgencySigner()) {
				if (getManagedBean().getCtContract().getOwnerOrgProfile()
						.getParentOrgProfile() != null
						&& getManagedBean().getCtContract()
								.getOwnerOrgProfile().getParentOrgProfile()
								.getOrgProfileId() != null
						&& agencySigner.getOrgProfile().getOrgProfileId() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "PTJ" });
				}
				if (agencySigner.getOfficer().getUserId() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "Contract Signer of Agency" });
				}
			}
		}

		if (getManagedBean().getCtSignAgencyWitness().size() == 0) {
			throw new EpException(MessageConst.ERROR_100023,
					new String[] { "Agency Witness" });
		} else {
			for (CtAgreementParty agencyWitness : getManagedBean()
					.getCtSignAgencyWitness()) {
				if (agencyWitness.getOfficerName() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "Name of Agency Witness" });
				}
				if (agencyWitness.getOfficerIc() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "IC No of Agency Witness" });
				}
				if (agencyWitness.getOfficerPosition() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "Position of Agency Witness" });
				}
			}
		}

		if (getManagedBean().getCtSupplierSigner().size() == 0) {
			throw new EpException(MessageConst.ERROR_100023,
					new String[] { "Supplier Signer" });
		} else {
			for (CtAgreementParty supplierSigner : getManagedBean()
					.getCtSupplierSigner()) {
				if (supplierSigner.getOfficerName() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "Contract Signer of Supplier" });
				}
			}
		}

		if (getManagedBean().getCtSupplierWitness().size() == 0) {
			throw new EpException(MessageConst.ERROR_100023,
					new String[] { "Supplier Witness" });
		} else {
			for (CtAgreementParty supplierWitness : getManagedBean()
					.getCtSupplierWitness()) {
				if (supplierWitness.getOfficerName() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "Name of Supplier Witness" });
				}
				if (supplierWitness.getOfficerIc() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "IC No of Supplier Witness" });
				}
				if (supplierWitness.getOfficerPosition() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { "Postion of Supplier Witness" });
				}
			}
		}
	}

	public SelectItem[] getPtjCodeName() {
		SelectItem[] items = null;
		try {
			int i = 1;
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DIVISION.value());

			Long ministryId = (getManagedBean().getCtContract()
					.getOwnerOrgProfile().getParentOrgProfile() != null) ? getManagedBean()
					.getCtContract().getOwnerOrgProfile().getParentOrgProfile()
					.getOrgProfileId()
					: getManagedBean().getCtContract().getOwnerOrgProfile()
							.getOrgProfileId();

			reqDTO.setParentOrgProfileId(ministryId);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> list = resDTO.getOrgProfileList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			for (PmOrgProfile orgProfile : list) {
				items[i++] = new SelectItem(orgProfile.getOrgProfileId(),
						orgProfile.getOrgName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}

	public SelectItem[] getContractSignerDropdown(
			CtAgreementParty agreementParty) {
		SelectItem[] items = null;
		IUserSO userSO = null;
		try {
			int i = 1;
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());
			if (agreementParty.getAgreementPartyType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.AGENCY_SIGNER_CODE)) {
				if (agreementParty.getOrgProfile() != null
						&& agreementParty.getOrgProfile().getOrgProfileId() != null) { // PTJ
					reqDTO.setOrgProfileId(agreementParty.getOrgProfile()
							.getOrgProfileId());
				} else if (agreementParty.getOrgProfile() != null
						&& agreementParty.getOrgProfile().getParentOrgProfile() != null
						&& agreementParty.getOrgProfile().getParentOrgProfile()
								.getOrgProfileId() != null) { // Department
					reqDTO.setOrgProfileId(agreementParty.getOrgProfile()
							.getParentOrgProfile().getOrgProfileId());
				} else { // default to ministry
					reqDTO.setOrgProfileId(getManagedBean().getCtContract()
							.getOwnerOrgProfile().getOrgProfileId());
				}
			}
			UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
			List<PmUser> list = resDTO.getUserList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			for (PmUser pmUser : list) {
				items[i++] = new SelectItem(pmUser.getUserId(),
						pmUser.getUserName());
			}
		} finally {
			userSO = null;
		}
		return items;
	}

	public SelectItem[] getSupplierSignerDropdown(
			CtAgreementParty agreementParty) {
		SelectItem[] items = null;
		ISupplierSearchSO supplierSearchSO;
		try {
			int i = 1;
			supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISupplierSearchSO.class);
			SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
			reqDTO.setSupplierId(getManagedBean().getCtContract()
					.getSmSupplier().getSupplierId());

			SupplierSearchResDTO resDTO = supplierSearchSO
					.retrieveContractSigners(reqDTO);

			for (SmPersonnel personnel : resDTO.getPersonnels()) {
				System.out.println("personnel.getName(): "
						+ personnel.getName() + " | " + personnel.getPmUser());
			}

			items = new SelectItem[resDTO.getPersonnels().size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			for (SmPersonnel personnel : resDTO.getPersonnels()) {
				items[i++] = new SelectItem(personnel.getName(),
						personnel.getName());
				getManagedBean().getSupplierUserMap().put(personnel.getName(),
						personnel);
			}
		} finally {
			supplierSearchSO = null;
		}
		return items;
	}

	public void resetDepartment(Long id) {
		this.getManagedBean().setDepartmentProfileId(id);
	}

	public String gotoContractView() {
		Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("contractVerId"));

		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractVerId(contractVerId);

		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
						IContractSO.class);
		ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		ContractDetailsBackingBean contractDetailsBB = new ContractDetailsBackingBean();
		contractDetailsBB.setValidateContractField(resDTO);
		contractDetailsBB.getManagedBean().setViewContract(true);
		contractDetailsBB.getManagedBean().setExternalPageUrl(
				AGREEMENT_DOC_PAGE);

		return CONTRACT_PAGE;
	}

	public void fileUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/"
				+ attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath);
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
		for (CtAgreementContent agreementContent : getManagedBean()
				.getCtAgreement().getAgreementContents()) {
			if (agreementContent.equals(getManagedBean()
					.getSelectedAgreementContent())) {
				agreementContent.setFileName(item.getFileName());
				agreementContent.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
			}
		}
	}

	public void uploadAgreement(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/"
				+ attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath);
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
		getManagedBean().getCtAgreement().setFileName(item.getFileName());
		getManagedBean().getCtAgreement().setFilePath(filePath);
	}

	public void uploadStampedPage(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/"
				+ attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath);
		this.getManagedBean().getCtAgreement()
				.setStampedPageName(item.getFileName());
		this.getManagedBean().getCtAgreement().setStampedPagePath(filePath);
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
	}

	public void uploadSignedPage(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/"
				+ attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath);
		this.getManagedBean().getCtAgreement()
				.setSignedPageName(item.getFileName());
		this.getManagedBean().getCtAgreement().setSignedPagePath(filePath);
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
	}

	public void downloadFile(String filePath, String fileName)
			throws IOException {
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);

		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");//
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

	public void downloadDocFile(CtAgreementContent agreementContent)
			throws IOException, Exception {		
		AttachmentFormat attachmentFormat = null;

		try {
			String fileName = getTemplateFileName(agreementContent.getTitle());
			if (fileName != null) {
				String content = null;
				String filePath = getManagedBean().getFileDirectory() + "/temp";
				File file = new File(filePath);
				file.mkdirs();

				attachmentFormat = new AttachmentDateFormat();
				filePath = filePath + "/"
						+ attachmentFormat.format(fileName, null);

				content = getContent(agreementContent.getTitle());
				if (content != null) {
					convertHtmlToDocFile(content, filePath);
					downloadFile(DocumentRepositoryUtils.getDBFilePath(filePath), fileName);
				}

			} else {
				if (agreementContent.getFilePath() == null) {
					throw new EpException(MessageConst.INFO_100052,
							new String[] { agreementContent.getTitle() });
				}
				downloadFile(DocumentRepositoryUtils.getDBFilePath(agreementContent.getFilePath()),
						agreementContent.getFileName());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
	
			attachmentFormat = null;
		}
	}

	public void downloadAll() {
		List<File> fileList = new ArrayList<File>();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String content = null;
		String filePath = null;
		String tempDirectory = getManagedBean().getFileDirectory() + "temp";
		File file = new File(tempDirectory);
		file.mkdirs();
		String fileName = null;

		try {
			for (CtAgreementContent agreementContent : getManagedBean().getCtAgreement().getAgreementContents()) {
				// System Define Doc
				if (agreementContent.getContentType().getParameterCode().equalsIgnoreCase(ParameterConst.AGREEMENT_SYSTEM_DEFINE_DOC)) {                        
					    fileName = getTemplateFileName(agreementContent.getTitle());
                        if(fileName != null) {
						    content = getContent(agreementContent.getTitle());
						    filePath = tempDirectory + "/"+ attachmentFormat.format(fileName, null);
                            convertHtmlToDocFile(content, filePath);
						    fileList.add(new File(filePath));
					    } else {
						    // TODO: get from LOA, external link
					    }
			    	}
				// Template Doc
				else if (agreementContent.getContentType().getParameterId().equals((retrieveParameterByCodeType(
						    ParameterConst.DOCUMENT_DEFINITION_TYPE,ParameterConst.AGREEMENT_TEMPLATE_DOC).getParameterId()))) {
					// TODO: download template doc
				}
				// User Upload Doc
				else if (agreementContent.getContentType().getParameterId().equals((retrieveParameterByCodeType(
						    ParameterConst.DOCUMENT_DEFINITION_TYPE,ParameterConst.AGREEMENT_USER_UPLOAD_DOC).getParameterId()))) {
				     	   if (agreementContent.getFilePath() != null)
						     fileList.add(new File(agreementContent.getFilePath()));
				}
			}

			String zipFileName = "agreement.zip";
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
			downloadFile(DocumentRepositoryUtils.getDBFilePath(zipFile.getPath().replace("\\", "/")), zipFileName);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	private String getTemplateFileName(String templateType) {
		if (templateType.trim().equalsIgnoreCase(
				ContractConst.TEMPLATE_AGREEMENT_COVER_PAGE)) {
			return "AgreementCoverPage.docx";
		} else if (templateType.trim().equalsIgnoreCase(
				ContractConst.TEMPLATE_AGREEMENT_PARTY)) {
			return "AgreementPartyPage.docx";
		} else if (templateType.trim().equalsIgnoreCase(
				ContractConst.TEMPLATE_AGREEMENT_SIGNING_PAGE)) {
			return "AgreementSigningPage.docx";
		}
		return null;
	}

	private void convertHtmlToDocFile(String content, String filePath) {
		WordprocessingMLPackage wordMLPackage;
		try {
			wordMLPackage = WordprocessingMLPackage.createPackage();
			AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(
					new PartName("/hw.html"));
			afiPart.setBinaryData(content.getBytes());
			afiPart.setContentType(new ContentType("text/html"));
			Relationship altChunkRel = wordMLPackage.getMainDocumentPart()
					.addTargetPart(afiPart);

			CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
			ac.setId(altChunkRel.getId());
			wordMLPackage.getMainDocumentPart().addObject(ac);
			wordMLPackage.getContentTypeManager().addDefaultContentType("html",
					"text/html");
			wordMLPackage.save(new java.io.File(filePath));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (Docx4JException e) {
			e.printStackTrace();
		}
	}


	public void downloadTemplateDoc() throws IOException {
		// TODO download the templateDoc file
		/*
		 * IContractTemplateSO contractTemplateSO = null; ContractTemplateReqDTO
		 * reqDTO = null; ContractTemplateResDTO ResDTO = null; try {
		 * //this.getManagedBean().setFilePath(agreementDetails.getFilePath());
		 * contractTemplateSO =
		 * (IContractTemplateSO)BusinessDelegate.getInstance
		 * ().getServiceObject("ct",IContractTemplateSO.class); reqDTO = new
		 * ContractTemplateReqDTO(); reqDTO.setTemplateId(agreementContent);
		 * ResDTO = contractTemplateSO.retrieveTemplateById(reqDTO);
		 * this.getManagedBean().setCtTemplates(ResDTO.getCtTemplate());
		 * }catch(Exception ex) { log.error("Exception>>", ex); }finally{
		 * contractTemplateSO = null; }
		 * agreementDetails.getFileType().;ParameterConst.agreementTemplateDoc;
		 */
		// agreementContent.getTitle();
	}

	public String gotoTemplateDoc(CtAgreementContent agreementContent) {
		String templateDocUrl = null;

		TemplateDocBackingBean tplDocBB = new TemplateDocBackingBean();
		tplDocBB.getManagedBean().setProcessType(
				ParameterConst.TPL_DOC_PROCESS_TYPE_CONTRACT);
		tplDocBB.getManagedBean().setExternalPageUrl(AGREEMENT_DOC_PAGE);
		tplDocBB.getManagedBean().setCtAgreementContent(agreementContent);
		// Create new template doc
		if (agreementContent.getTplDocMaster() == null) {
			templateDocUrl = tplDocBB.createNewTemplate();
		} else {
			templateDocUrl = tplDocBB.goToMaint(agreementContent
					.getTplDocMaster());
		}
		return templateDocUrl;
	}

	public PmParameter retrieveParameterByCodeType(String parameterType,
			String parameterCode) throws NamingException {

		IParameterSO parameterSO = null;
		ParameterReqDTO reqDTO = null;
		try {

			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType(parameterType);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			return resDTO.getPmParameter();
		} catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		} finally {
			parameterSO = null;
		}
		return null;
	}

	public void addAgreementContents() throws NamingException {
		CtAgreementContent contents = new CtAgreementContent();
		if (contents.getContentType() == null) {
			contents.setContentType(new PmParameter());
			contents.setContentType(retrieveParameterByCodeType(
					ParameterConst.DOCUMENT_DEFINITION_TYPE,
					ParameterConst.AGREEMENT_USER_UPLOAD_DOC));
		}
		getManagedBean().getCtAgreement().getAgreementContents().add(contents);
	}

	public void delAgreementContents() {
		this.getManagedBean().getCtAgreement().getAgreementContents()
				.remove(getManagedBean().getSelectedAgreementContent());
	}

	public void addAgencySignerParty() {
		try {
			CtAgreementParty ctAgreementParty = new CtAgreementParty();
			ctAgreementParty.setAgreementPartyType(new PmParameter());
			ctAgreementParty.setAgreementPartyType(retrieveParameterByCodeType(
					ParameterTypeConst.AGREEMENT_PARTY_TYPE,
					ParameterTypeConst.AGREEMENT_AGENCY_SIGNER));
			if (ctAgreementParty.getOfficer() == null)
				ctAgreementParty.setOfficer(new PmUser());
			if (ctAgreementParty.getOrgProfile() == null) {
				ctAgreementParty.setOrgProfile(new PmOrgProfile());
				if (ctAgreementParty.getOrgProfile().getParentOrgProfile() == null) {
					ctAgreementParty.getOrgProfile().setParentOrgProfile(
							new PmOrgProfile());
				}
			}
			this.getManagedBean().getCtAgencySigner().add(ctAgreementParty);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void deleteAgencySignerParty() {
		for (int i = 0; i < selectedAgencySigner.length; i++) {
			CtAgreementParty ctAgreementParty = selectedAgencySigner[i];
			this.getManagedBean().getCtAgencySigner().remove(ctAgreementParty);
		}
		selectedAgencySigner = null;
	}

	public void addSupplierSigner() {
		try {
			CtAgreementParty ctAgreementParty = new CtAgreementParty();
			ctAgreementParty.setAgreementPartyType(new PmParameter());
			ctAgreementParty.getAgreementPartyType().setParameterId(
					retrieveParameterByCodeType(
							ParameterTypeConst.AGREEMENT_PARTY_TYPE,
							ParameterTypeConst.AGREEMENT_SUPPLIER_SIGNER)
							.getParameterId());
			if (ctAgreementParty.getOfficer() == null)
				ctAgreementParty.setOfficer(new PmUser());
			this.getManagedBean().getCtSupplierSigner().add(ctAgreementParty);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void deleteSupplierSigner() {
		for (int i = 0; i < selectedSupplierSigner.length; i++) {
			CtAgreementParty ctAgreementParty = selectedSupplierSigner[i];
			this.getManagedBean().getCtSupplierSigner()
					.remove(ctAgreementParty);
		}
		selectedSupplierSigner = null;
	}

	public void addSupplierWitness() {
		try {
			CtAgreementParty ctAgreementParty = new CtAgreementParty();
			ctAgreementParty.setAgreementPartyType(new PmParameter());
			ctAgreementParty.getAgreementPartyType().setParameterId(
					retrieveParameterByCodeType(
							ParameterTypeConst.AGREEMENT_PARTY_TYPE,
							ParameterTypeConst.AGREEMENT_SUPPLIER_WITNESS)
							.getParameterId());
			this.getManagedBean().getCtSupplierWitness().add(ctAgreementParty);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void deleteSupplierWitness() {
		for (int i = 0; i < selectedSupplierWitnes.length; i++) {
			CtAgreementParty ctAgreementParty = selectedSupplierWitnes[i];
			this.getManagedBean().getCtSupplierWitness()
					.remove(ctAgreementParty);
		}
		selectedSupplierWitnes = null;
	}

	public void addAgencyWitnes() {
		try {
			CtAgreementParty ctAgreementParty = new CtAgreementParty();
			ctAgreementParty.setAgreementPartyType(new PmParameter());
			ctAgreementParty.getAgreementPartyType().setParameterId(
					retrieveParameterByCodeType(
							ParameterTypeConst.AGREEMENT_PARTY_TYPE,
							ParameterTypeConst.AGREEMENT_AGENCY_WITNESS)
							.getParameterId());
			this.getManagedBean().getCtSignAgencyWitness()
					.add(ctAgreementParty);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteAgencyWitnes() {
		for (int i = 0; i < selectedAgencyWitnes.length; i++) {
			CtAgreementParty ctAgreementParty = selectedAgencyWitnes[i];
			this.getManagedBean().getCtSignAgencyWitness()
					.remove(ctAgreementParty);
		}
		selectedAgencyWitnes = null;
	}

	public boolean isContractAdmin() {
		return getFacesScopes()
				.hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value());
	}

	private String getUserRole() {

		if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()))
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
			return RoleCode.CT_APPROVER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()))
			return RoleCode.BPK_CCA.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
			return RoleCode.BPK_APPROVER.value();
		else
			return null;
	}

	public String blobToStr(byte[] content) throws IOException {
		String decoded = "";
		try {
			if (content == null)
				content = new byte[0];
			decoded = new String(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decoded;
	}

	public String fileNames(String filePath) {
		if (filePath.equalsIgnoreCase("") || filePath.equalsIgnoreCase(null)
				|| filePath.equalsIgnoreCase(" "))
			return null;

		String fileName = null;
		File file = new File(filePath);
		fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
		return fileName;
	}

	public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()) || getFacesScopes()
				.hasRoleUser(RoleCode.BPK_APPROVER.value()));
	}

	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(
					new DualListModel<String>(source, target));
		}
	}

	private List<String> getAvailableApproverList() {
		IUserSO userSO = null;
		List<String> sourceList = new ArrayList<String>();
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());

			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0)
					.getPmOrgProfile().getOrgProfileId());

			if (getManagedBean().getCtContract().getContractType()
					.getParameterCode()
					.equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
			} else { // Central Contract
				reqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());
			}

			resDTO = userSO.retrieveUserListByRole(reqDTO);
			List<PmUser> list = resDTO.getUserList();

			for (PmUser user : list) {
				sourceList.add(user.getUserName());
				getManagedBean().getUserMap().put(user.getUserName(), user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		} finally {
			userSO = null;
		}
		return sourceList;
	}

	public void setUserValueAgency(CtAgreementParty ctParty, Long userId) {
		for (CtAgreementParty party : this.getManagedBean().getCtAgencySigner()) {
			if (party.equals(ctParty)) {
				IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IUserSO.class);
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
				UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
				party.setOfficerName(resDTO.getPmUser().getUserName());
				party.setOfficerIc(resDTO.getPmUser().getIdentificationNo());
				party.setOfficerPosition(resDTO.getPmUser().getDesignation());
			}
		}
	}

	public void setSupplierUserValue(CtAgreementParty ctParty,
			String officerName) {
		SmPersonnel personnel = getManagedBean().getSupplierUserMap().get(
				officerName);
		if (personnel != null) {
			ctParty.setOfficerIc(personnel.getIdentificationNo());
			ctParty.setOfficerPosition(personnel.getDesignation());
		}
	}

	private void initializeRemarks() {
		initiateRemark(getManagedBean().getCtAgreement()
				.getSignAgreementRemark());
	}

	private void initiateRemark(List<CtRemark> remarks) {
		if (remarks.size() == 0
				|| !compareUserName(remarks.get(0).getCreatedUser()
						.getLoginId())) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(getFacesScopes().getUserSessionBean()
					.getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remarks.add(0, remark);
		}
	}

	public String getContent(String parameterCode) throws Exception {
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		DecimalFormat nf = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		Calendar c = Calendar.getInstance();
		VelocityContext context = null;
		String vmFile = null;
		String content = null;

		if (ContractConst.TEMPLATE_AGREEMENT_COVER_PAGE
				.equalsIgnoreCase(parameterCode)) {

			String imgUrl = getServerUrl()
					+ "NGeP-portlet/images/letter/gov.png";
			context = new VelocityContext();
			vmFile = "/ct/agreement/agreement-cover-page.vm";
			context.put("imgUrl", imgUrl);
			context.put("contractTitle", getManagedBean().getCtContract()
					.getQuotationTender().getQtTitle());
			context.put("supplierName", getManagedBean().getCtContract()
					.getCurrentContractVer().getCtContractValue()
					.getSupplierName());
			context.put("supplierRegistrationNo", getManagedBean()
					.getCtContract().getSmSupplier().getRegNo());
			context.put("physicalContactNo", getManagedBean().getCtContract()
					.getContractPhysicalNo());
			context.put(
					"contractStartDate",
					df.format(getManagedBean().getCtContract()
							.getCurrentContractVer().getCtContractValue()
							.getEffDate()));
			context.put(
					"contractEndDate",
					df.format(getManagedBean().getCtContract()
							.getCurrentContractVer().getCtContractValue()
							.getExpDate()));
			context.put("contractPeriod", getManagedBean().getCtContract()
					.getCurrentContractVer().getCtContractValue()
					.getContractPeriod()
					+ "");
			context.put(
					"contractValue",
					nf.format(getManagedBean().getCtContract()
							.getCurrentContractVer().getCtContractValue()
							.getContractAmount())
							+ "");
			context.put("contractValueText", NumberWordUtil.convert(getManagedBean().getCtContract()
					.getCurrentContractVer().getCtContractValue()
					.getContractAmount().toString())
					);
			context.put("isBondReq", getManagedBean().getCtContract()
					.isBondReq());
			if (getManagedBean().getCtContract().isBondReq()) {
				if (getManagedBean().getCtContract().getLoa().getBondAmt() != null) {
					context.put(
							"performanceBondValue",
							nf.format(getManagedBean().getCtContract().getLoa()
									.getBondAmt())
									+ "");
					context.put("performanceBondValueText", NumberWordUtil.convert(getManagedBean()
							.getCtContract().getLoa().getBondAmt().toString())
							);
				}
			}
		} else if (ContractConst.TEMPLATE_AGREEMENT_PARTY
				.equalsIgnoreCase(parameterCode)) {
			context = new VelocityContext();
			vmFile = "/ct/agreement/agreement-party.vm";
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAddressSO.class);
			AddressReqDTO addressReqDTO = new AddressReqDTO();
			addressReqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
			addressReqDTO.setOrgProfileId(getManagedBean().getCtContract()
					.getOwnerOrgProfile().getOrgProfileId());
			AddressResDTO addressResDTO = addressSO
					.retrieveAddressTypeList(addressReqDTO);
			PmAddressType pmAddressType = null;
			if (addressResDTO.getAddressTypeList().size() > 0) {
				pmAddressType = addressResDTO.getAddressTypeList().get(0);
			}

			context.put("userInputDate", c.get(Calendar.DATE) + "");
			context.put("userInputMonth", c.get(Calendar.MONTH) + "");
			context.put("currentYear", c.get(Calendar.YEAR) + "");
			context.put("jabatanName", getManagedBean().getCtContract()
					.getOwnerOrgProfile().getOrgName());
			if (getManagedBean().getCtContract().getOwnerOrgProfile()
					.getParentOrgProfile() != null) {
				context.put("ministryName", getManagedBean().getCtContract()
						.getOwnerOrgProfile().getParentOrgProfile()
						.getOrgName());
			} else {
				context.put("ministryName", "");
			}
			if (pmAddressType != null) {
				context.put("jabatanAddress", AddressUtils
						.getFullAddress(pmAddressType.getPmAddress()));
			} else {
				context.put("jabatanAddress", "");
			}
			context.put("supplierName", getManagedBean().getCtContract()
					.getCurrentContractVer().getCtContractValue()
					.getSupplierName());
			context.put("supplierRegNo", getManagedBean().getCtContract()
					.getSmSupplier().getRegNo());
			context.put(
					"supplierAddress",
					AddressUtils.getFullAddress(getManagedBean()
							.getCtContract().getCurrentContractVer()
							.getCtContractValue().getSupplierAddress()));
			context.put("contractTitle", getManagedBean().getCtContract()
					.getQuotationTender().getQtTitle());

		} else if (ContractConst.TEMPLATE_AGREEMENT_SIGNING_PAGE
				.equalsIgnoreCase(parameterCode)) {
			context = new VelocityContext();
			vmFile = "/ct/agreement/agreement-signing-page.vm";
			context.put("ctAgencySignerList", getManagedBean()
					.getCtAgencySigner());
			context.put("ctSignAgencyWitnessList", getManagedBean()
					.getCtSignAgencyWitness());
			context.put("ctSupplierSignerList", getManagedBean()
					.getCtSupplierSigner());
			context.put("ctSupplierWitnessList", getManagedBean()
					.getCtSupplierWitness());

			context.put("currentYear", c.get(Calendar.YEAR) + "");
			if (getManagedBean().getCtContract().getOwnerOrgProfile()
					.getParentOrgProfile() != null) {
				context.put("ministryName", getManagedBean().getCtContract()
						.getOwnerOrgProfile().getParentOrgProfile()
						.getOrgName());
			} else {
				context.put("ministryName", "");

			}
			context.put("companyName", getManagedBean().getCtContract()
					.getCurrentContractVer().getCtContractValue()
					.getSupplierName());
			context.put("companyRegNo", getManagedBean().getCtContract()
					.getSmSupplier().getRegNo());
		}

		if (vmFile != null && context != null) {
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(
					vmFile, context);
		}

		return content;
	}

	private String getServerUrl() {
		ExternalContext extcontext = FacesContext.getCurrentInstance()
				.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext
				.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil
				.getOriginalServletRequest(PortalUtil
						.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		String url = requestURL.substring(0, requestURL.length()
				- httpServletRequest.getRequestURI().length())
				+ httpServletRequest.getContextPath() + "/";

		return url;
	}

	public boolean isSupplementValue() {
		return supplementValue;
	}

	public void setSupplementValue(boolean supplementValue) {
		this.supplementValue = supplementValue;
	}

	public CtAgreementParty[] getSelectedAgencyWitnes() {
		return selectedAgencyWitnes;
	}

	public void setSelectedAgencyWitnes(CtAgreementParty[] selectedAgencyWitnes) {
		this.selectedAgencyWitnes = selectedAgencyWitnes;
	}

	public CtAgreementParty[] getSelectedAgencySigner() {
		return selectedAgencySigner;
	}

	public void setSelectedAgencySigner(CtAgreementParty[] selectedAgencySigner) {
		this.selectedAgencySigner = selectedAgencySigner;
	}

	public CtAgreementParty[] getSelectedSupplierWitnes() {
		return selectedSupplierWitnes;
	}

	public void setSelectedSupplierWitnes(
			CtAgreementParty[] selectedSupplierWitnes) {
		this.selectedSupplierWitnes = selectedSupplierWitnes;
	}

	public CtAgreementParty[] getSelectedSupplierSigner() {
		return selectedSupplierSigner;
	}

	public void setSelectedSupplierSigner(
			CtAgreementParty[] selectedSupplierSigner) {
		this.selectedSupplierSigner = selectedSupplierSigner;
	}
}
