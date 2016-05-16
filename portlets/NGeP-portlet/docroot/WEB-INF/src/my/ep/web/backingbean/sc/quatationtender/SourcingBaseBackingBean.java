/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.IPanelGroupItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.circular.PanelGroupItemReqDTO;
import my.ep.dto.ct.circular.PanelGroupItemResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScEvaluationCommitteeReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScFinancialEvaluationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScOpenCommitteeReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScTechnicalEvaluationReportAttachment;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentDefaultFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.type.sc.quationtender.ApprovalStatus;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.QTFiledMapper;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * 
 * SourcingBaseBackingBean is the Base class for Quation Tender
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public abstract class SourcingBaseBackingBean<T extends BaseManagedBean>
		extends BaseBackingBean<T> {

	/**
	 * Logger instance.
	 */
	public static Logger log = EpLogger
			.getLogger(SourcingBaseBackingBean.class);

	/**
	 * Method to retrive orgprofiles based on user id and parent org id
	 * 
	 * @param profileReqDTO
	 * @return List<PmOrgProfile> orgProfiles.
	 */
	public List<PmOrgProfile> retriveOrgProfiles(OrgProfileReqDTO profileReqDTO) {

		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByOrgType(profileReqDTO);
		return resDTO.getOrgProfileList();
	}

	/**
	 * To constuct SelectItem s for dropndowns for orgprofiles
	 * 
	 * @param orgProfile
	 * @return List<SelectItem> selectItems
	 */
	public List<SelectItem> toOrgProfileSelectItems(
			List<PmOrgProfile> orgProfile) {
		List<SelectItem> orgProfileSelectItems = new ArrayList<SelectItem>();
		orgProfileSelectItems.add(new SelectItem("", ResourceBundleUtils
				.getMessage("lbl.txt.select",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));
		for (PmOrgProfile pmOrgProfile : orgProfile) {
			orgProfileSelectItems.add(new SelectItem(pmOrgProfile
					.getOrgProfileId().toString(), pmOrgProfile.getOrgCode()));
		}
		return orgProfileSelectItems;
	}

	/**
	 * To constuct SelectItem s for dropndowns for users
	 * 
	 * @param users
	 * @return List<SelectItem> selectItems
	 */
	public List<SelectItem> toSelectItmes(List<PmUser> users) {
		List<SelectItem> userList = new ArrayList<SelectItem>();
		userList.add(new SelectItem("", "SelectOne"));
		for (PmUser user : users) {
			userList.add(new SelectItem(user.getUserId(), user.getUserName()));
		}
		return userList;
	}

	/**
	 * To upload the attachment.
	 * 
	 * @param file
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void uploadAttachment(UploadedFile file, ScAttachment attachment)
			throws FileNotFoundException, IOException {
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();

		if (fileBytes != null) {
			attachment.setFileName(fileName);
			attachment.setFileDesc("File uploded");

			populateAttachmentDetails(attachment);

			FileOutputStream fos;
			fos = new FileOutputStream(getActualFilePath(attachment));
			fos.write(fileBytes);
			fos.close();

			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"The file was uploaded successfully.", null);

			FacesContext.getCurrentInstance().addMessage(null, message);	
		}
	}

	/**
	 * To populate details into ScAttachment instance.
	 * 
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void populateAttachmentDetails(ScAttachment attachment)
			throws FileNotFoundException, IOException {
		Timestamp date = new Timestamp(System.currentTimeMillis());
		PmUser createdUser = new FacesScopes().getUserSessionBean();

		attachment.setCreatedBy(createdUser.getUserId());
		attachment.setCreatedDate(date);
		attachment.setChangedBy(createdUser.getUserId());
		attachment.setCreatedUser(createdUser);

		StringBuilder filePath = new StringBuilder();
		filePath.append(Calendar.getInstance().get(Calendar.YEAR));
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(attachment.getDocNo());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(attachment.getDocType());
		attachment.setFilePath(filePath.toString());

		File theDir = new File(getActualFilePath(attachment));
		theDir.mkdirs();

		AttachmentFormat scFormat = getAttachmentFormat(attachment);
		String formatedFileName = scFormat.format(attachment.getFileName(),
				attachment.getPrefixFileName());

		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(formatedFileName);
		attachment.setFilePath(filePath.toString());

		log.info("=======get File path====" + getActualFilePath(attachment));
	}

	private AttachmentFormat getAttachmentFormat(ScAttachment scAttachment) {
		if (scAttachment instanceof ScLoaReportAttachment
				|| scAttachment instanceof ScQtCheckListAttachment
				|| scAttachment instanceof ScQtProposalChecklistAttachment
				|| scAttachment instanceof ScQtFinalizationAttachment
				|| scAttachment instanceof ScQtFinalizationReportAttachment
				|| scAttachment instanceof ScEvaluationCommitteeReportAttachment
				|| scAttachment instanceof ScOpenCommitteeReportAttachment
				|| scAttachment instanceof ScTechnicalEvaluationReportAttachment
				|| scAttachment instanceof ScFinancialEvaluationReportAttachment) {

			return new AttachmentDateFormat();
		} else {

			return new AttachmentDefaultFormat();
		}
	}

	/**
	 * To get Actual file path for attachement.
	 * 
	 * @param attachment
	 * @return filePath.
	 */
	protected String getActualFilePath(ScAttachment attachment) {
		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder actualFilePath = new StringBuilder(docRepoBaseDir);
		actualFilePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		actualFilePath.append(attachment.getFilePath());

		return actualFilePath.toString();
	}

	/**
	 * To set Icon.
	 * 
	 * @param attachment
	 * @return
	 */
	public <ATTACHMENT extends ScAttachment> String determineIcon(
			ATTACHMENT attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";

		if (attachment != null) {
			String fileName = attachment.getFileName();

			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}

			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equalsIgnoreCase(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)
					|| "csv".equalsIgnoreCase(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equalsIgnoreCase(ext) || "bmp".equalsIgnoreCase(ext)
					|| "png".equalsIgnoreCase(ext) || "gif".equalsIgnoreCase(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equalsIgnoreCase(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

	/**
	 * To download the file.
	 * 
	 * @param scAttachment
	 * @return File instance.
	 * @throws IOException
	 */
	public <ATTACHMENT extends ScAttachment> File downloadFile(
			ATTACHMENT scAttachment) throws IOException {
		if (scAttachment != null
				&& StringUtils.isNotBlank(scAttachment.getFileName())) {

			return downloadFile(getActualFilePath(scAttachment),
					scAttachment.getFileName());
		} else {
			throw new EpException("Attachment not found.");
		}
	}

	/**
	 * To download the file
	 * 
	 * @param filePath
	 * @param fileName
	 * @return File instance
	 * @throws IOException
	 */
	public File downloadFile(String filePath, String fileName)
			throws IOException {

		if (filePath != null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());

			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			downloadFileByInputStream(input, fileName);
		}

		// use to upload back to database
		File file = new File(filePath);
		return file;
	}

	/**
	 * To download file by stream.
	 * 
	 * @param input
	 * @param fileName
	 * @throws IOException
	 */
	protected void downloadFileByInputStream(InputStream input, String fileName)
			throws IOException {
		log.info("Inside downloadFile(..)");
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		// to fix firefox issue of space
		String finalFileName = fileName.replaceAll("\\s+",
				QTConstants.REPLACE_CHAR);
		log.info("finalFileName: " + finalFileName);
		response.setHeader("Content-Disposition", "attachment; filename="
				+ finalFileName);
		response.setContentType("application/octet-stream");
		response.flushBuffer();

		OutputStream out = response.getOutputStream();

		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			buffer = new byte[4096];
		}
		input.close();
		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
	}

	/**
	 * To download multiple files.
	 * 
	 * @param zipFileName
	 * @param scAttachments
	 * @throws IOException
	 */
	protected <ATTACHMENT extends ScAttachment> void downloadAllFiles(
			String zipFileName, List<ATTACHMENT> scAttachments)
			throws IOException {
		List<File> files = new ArrayList<File>();

		for (ScAttachment scAttachment : scAttachments) {
			if (null != scAttachment
					&& StringUtils.isNotBlank(scAttachment.getFilePath())) {
				files.add(new File(getActualFilePath(scAttachment)));
			}
		}

		PortletResponse portalresponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ zipFileName + ".zip");

		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
				response.getOutputStream()));
		try {
			for (File file : files) {
				zos.putNextEntry(new ZipEntry(file.getName()));

				// Get the file
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					zos.write(("ERROR: Could not find file " + file.getName())
							.getBytes());
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
	 * To conver html to doc file.
	 * 
	 * @param content
	 * @param filePath
	 */
	public void converthtmlToDocFile(String content, String filePath) {
		log.info("Inside converthtmlToDocFile(..)");
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
		log.info("Exit converthtmlToDocFile(..)");
	}

	/**
	 * To populate parentOrgProfile.
	 * 
	 * @param scQt
	 */
	public void populateParentOrgProfile(ScQt scQt) {
		PmOrgProfile pmOrgProfile = scQt.getPmOrgProfile();

		PmOrgProfile departmentOrgProfile = null;
		PmOrgProfile ministryOrgProfile = null;
		if (pmOrgProfile != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());

			try {
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveJabatanByOrgProfileId(reqDTO);
				departmentOrgProfile = resDTO.getPmOrgProfile();

				resDTO = orgProfileSO.retrieveMinistryByOrgProfileId(reqDTO);
				ministryOrgProfile = resDTO.getPmOrgProfile();
			} finally {

			}
		}

		if (departmentOrgProfile != null) {
			scQt.setDepartmentOrgProfile(departmentOrgProfile);
		}
		if (ministryOrgProfile != null) {
			scQt.setMinistryOrgProfile(ministryOrgProfile);
		}
	}

	/**
	 * TO Check wheter object is empty or null
	 * 
	 * @param testObject
	 * @return boolean flag
	 */
	public boolean isNotNullOREmpty(List<? extends Object> testObject) {
		if (null != testObject && !testObject.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * To set user Details.
	 * 
	 * @param baseObject
	 */
	public void setUserDetails(BaseEntity baseObject) {
		PmUser user = new FacesScopes().getUserSessionBean();

		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(user.getUserId());
			baseObject
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(user.getUserId());
	}

	/**
	 * To get Message constant.
	 * 
	 * @param outCome
	 * @return String messageConstnt
	 */
	public String getMessgeConstant(String outCome) {
		String msgCode = null;
		if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
			msgCode = MessageConst.INFO_700005;
		} else if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			msgCode = MessageConst.INFO_700008;
		} else if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
			msgCode = MessageConst.INFO_700012;
		} else if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
			msgCode = MessageConst.INFO_700007;
		} else if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)) {
			msgCode = MessageConst.INFO_700013;
		}
		else if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
			msgCode = MessageConst.INFO_700013;
		}
		else if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_SIGNOFF)) {
			msgCode = MessageConst.INFO_700032;
		}
		return msgCode;
	}

	/**
	 * To retrieve UserName.
	 * 
	 * @param userId
	 * @return String userName
	 */
	public String retriveUserName(Long userId) {
		IUserSO userSo = null;
		String userName = "";
		try {
			userSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTo = new UserReqDTO();
			reqDTo.setUserId(userId);
			UserResDTO resDTO = userSo.retrieveUserByUserId(reqDTo);
			if (resDTO.isSuccessful() == true) {
				PmUser user = resDTO.getPmUser();
				userName = user.getUserName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userName;
	}

	/**
	 * To initialize remarks.
	 * 
	 * @param parent
	 * @param workFlowStatus
	 * @param scRemarks
	 * @param clazz
	 * @return ScRemark instance
	 */
	protected <PARENT_ENTITY extends BaseEntity, REMARK_CLASS extends ScRemark> List<REMARK_CLASS> initializeRemarks(
			PARENT_ENTITY parent, ScWorkFlowStatus workFlowStatus,
			List<REMARK_CLASS> scRemarks, Class<REMARK_CLASS> clazz) {

		try {
			if (scRemarks == null) {
				scRemarks = new ArrayList<REMARK_CLASS>();
			}

			REMARK_CLASS prevRemark = null;
			if (scRemarks.size() > 0) {
				prevRemark = scRemarks.get(0);
				
				for (int row = 0; row < scRemarks.size(); row++) {
					ScRemark scRemark = scRemarks.get(row);
					if (!isDiffWithPrevWorkFlowStatus(workFlowStatus, scRemark)) {
						scRemark.setEditable(true);
					}
				}
			}

			FacesScopes facesScopes = new FacesScopes();
			if (scRemarks.size() == 0
					|| !compareUserName(prevRemark.getCreatedUser()
							.getLoginId())
					|| isDiffWithPrevWorkFlowStatus(workFlowStatus, prevRemark)) {

				REMARK_CLASS remark = clazz.newInstance();
				remark.setCreatedBy(facesScopes.getUserSessionBean()
						.getUserId());
				remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				remark.setChangedBy(remark.getCreatedBy());
				remark.setCreatedUser(facesScopes.getUserSessionBean());
				remark.setParent(parent);
				remark.setScWorkFlowStatus(workFlowStatus);

				scRemarks.add(0, remark);
			}

			return scRemarks;
		} catch (InstantiationException ie) {
			ie.printStackTrace();
			throw new EpException(ie.getMessage());
		} catch (IllegalAccessException ia) {
			ia.printStackTrace();
			throw new EpException(ia.getMessage());
		}
	}
	
	private boolean isDiffWithPrevWorkFlowStatus(ScWorkFlowStatus workFlowStatus, ScRemark prevRemark) {
		return workFlowStatus != null && 
				(
					prevRemark.getScWorkFlowStatus() == null || 
					(
						prevRemark.getScWorkFlowStatus() != null &&
						prevRemark.getScWorkFlowStatus().getWorkflowStatusId() != null 
						&& !prevRemark.getScWorkFlowStatus().getWorkflowStatusId().equals(workFlowStatus.getWorkflowStatusId())
					)
				);
	}

	/**
	 * To remove empty remarks
	 * 
	 * @param scRemarks
	 * @return List<ScRemarks> remarks
	 */
	protected <REMARK_CLASS extends ScRemark> List<REMARK_CLASS> finalizeRemarks(
			List<REMARK_CLASS> scRemarks) {
		if (scRemarks != null && scRemarks.size() > 0) {
			ScRemark scRemark = scRemarks.get(0);

			if (StringUtils.isBlank(scRemark.getRemark())) {
				scRemarks.remove(0);
			}
		}

		return scRemarks;
	}

	/**
	 * To get Attachement as a stream
	 * 
	 * @param scAttachment
	 * @param contentType
	 * @return StreamContent instance
	 * @throws IOException
	 */
	protected <ATTACHMENT extends ScAttachment> StreamedContent getAttachmentStreamContent(
			ATTACHMENT scAttachment, String contentType) throws IOException {
		if (scAttachment != null) {
			File file = new File(getActualFilePath(scAttachment));
			ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
			FileInputStream in = new FileInputStream(file);
			byte[] buf = new byte[512];
			int len;
			while (true) {
				len = in.read(buf);
				if (len == -1) {
					break;
				}
				tmpOut.write(buf, 0, len);
			}
			in.close();
			tmpOut.close();
			ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0,
					tmpOut.size());

			ByteArrayInputStream stream = new ByteArrayInputStream(bb.array());

			return new DefaultStreamedContent(stream, contentType);
		} else {
			return null;
		}
	}

	protected List<PmParameterDesc> retrieveParametersBySearchBean(
			String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				parameterType, searchBean);

	}

	/**
	 * To get report from orcle reports
	 * 
	 * @param reportName
	 * @param paramMap
	 * @param attachment
	 * @param checkDownloadstatus
	 * @return File instance
	 * @throws IOException
	 */
	protected File generatePdfFromOracle(String reportName,
			Map<String, String> paramMap, ScAttachment attachment,
			boolean checkDownloadstatus) throws IOException {
		EpPropertiesUtils propertiesUtils = EpPropertiesUtils.getInstance();
		String excPrevUrl = propertiesUtils
				.getProperty(EpPropertiesConst.EXC_PREV_URL);
		String excNextUrl = propertiesUtils
				.getProperty(EpPropertiesConst.EXC_NEXT_URL);

		StringBuilder reportUrl = new StringBuilder(excPrevUrl);
		reportUrl.append(ReportConst.AMP_REPORT_NAME);
		reportUrl.append(reportName);

		for (Entry<String, String> params : paramMap.entrySet()) {
			reportUrl.append(ReportConst.PARAM_SEPARATOR);
			reportUrl.append(params.getKey());
			reportUrl.append(ReportConst.VALUE_SEPARATOR);
			reportUrl.append(params.getValue());
		}
		reportUrl.append(excNextUrl);
		log.info("reportUrl: " + reportUrl);

		if (log.isDebugEnabled()) {
			log.debug("reportUrl: " + reportUrl);
		}
		OutputStream out = null;
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		if (!checkDownloadstatus) {
			res.setHeader("Content-Disposition", "attachment; filename="
					+ attachment.getFileName());
			res.setHeader("Content-Transfer-Encoding", "binary");
			res.setContentType("application/octet-stream");
			res.flushBuffer();
			out = res.getOutputStream();
		}

		File localFile = new File(getActualFilePath(attachment));

		if (reportUrl.toString() != null) {
			InputStream input = null;
			try {
				URL obj = new URL(reportUrl.toString());
				HttpURLConnection uc = (HttpURLConnection) obj.openConnection();
				input = new BufferedInputStream(uc.getInputStream());
			} catch (ConnectException e) {
				throw new EpException(MessageConst.ERROR_100058);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			try {
				for (int readNum; (readNum = input.read(buf)) != -1;) {
					bos.write(buf, 0, readNum);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			byte[] bytes = bos.toByteArray();
			FileOutputStream fos = new FileOutputStream(localFile);
			fos.write(bytes);
			fos.flush();
			fos.close();
			input.close();
		}
		if (!checkDownloadstatus) {
			out.flush();
			out.close();
		}
		return localFile;
	}

	/**
	 * To update digisign data for gpki
	 * 
	 * @param pmDigiSign
	 * @param docType
	 * @param docId
	 * @param docNumber
	 */
	protected void updatePmDigiSignData(PmDigiSign pmDigiSign, String docType,
			Long docId, String docNumber) {
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setOriginalData(docNumber);
	}

	/**
	 * To update digisign data for spki
	 * 
	 * @param pmDigiSign
	 * @param docType
	 * @param docId
	 * @param docNumber
	 * @param textBeforeSign
	 */
	protected void updateSpkiDigiSignData(PmDigiSign pmDigiSign,
			String docType, Long docId, String docNumber, String textBeforeSign) {
		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setCreatedBy(userId);
		pmDigiSign.setCreatedDate(date);
		pmDigiSign.setOriginalData(textBeforeSign);
	}

	FacesScopes facesScope = new FacesScopes();
	Long userId = facesScope.getUserSessionBean().getUserId();
	// Long userId = (Long) MDC.get(LogKey.USER_ID);
	Timestamp date = new Timestamp(System.currentTimeMillis());

	/**
	 * To check Digisining Required
	 * 
	 * @param roleCode
	 * @return boolean flag
	 */
	protected boolean isDigiSigningRequired(String roleCode) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		reqDTO.setLanguageCode(CommonConstants.DEFAULT_LANGUAGE_CODE);
		UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
		List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();

		if (null != roleDesc) {
			for (PmRoleDesc pmRoleDesc : roleDesc) {
				if (roleCode.equalsIgnoreCase(pmRoleDesc.getPmRole()
						.getRoleCode())) {
					return true;
				}

			}
		} else {
			throw new EpException("User does not exist with the role"
					+ roleCode);
		}
		return false;
	}

	/**
	 * To check Digisining Required
	 * 
	 * @param roleCode
	 * @param List
	 *            <ScQtCommitteeActivity> committeeActivities
	 * 
	 * @return boolean flag
	 */
	protected boolean isDigiSigningRequired(String roleCode,
			List<ScQtCommitteeActivity> committeeActivities) {
		boolean hasRequiredRole = isDigiSigningRequired(roleCode);

		if (hasRequiredRole && CollectionUtils.isNotEmpty(committeeActivities)) {
			Long currentUsersId = new FacesScopes().getUserSessionBean()
					.getUserId();
			for (ScQtCommitteeActivity commActivity : committeeActivities) {
				ScCommitteeMember commMember = commActivity
						.getScCommitteeMember();
				if (ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equals(commMember
						.getMemberRole().getParameterCode())
						&& commMember.getPmUser().getUserId()
								.equals(currentUsersId)) {
					return true;
				}
			}
		}

		return false;
	}

	protected boolean isNullOrEmpty(String str) {
		if (null == str || "".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * To get message common
	 * 
	 * @param name
	 * @return String instance
	 */
	public static String getMessageCommon(String name) {
		return ResourceBundleUtils.getMessage(name,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}

	/**
	 * To get supplier information
	 * 
	 * @param supplierId
	 * @return String instance
	 */
	public String retriveSupplierDescById(Long supplierId) {

		String status = "";
		try {
			ISupplierSearchSO suppSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISupplierSearchSO.class);
			SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
			reqDTO.setSupplierId(supplierId);
			reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
			SupplierSearchResDTO resDTO = suppSO
					.retriveSupplierStatusDescById(reqDTO);
			if (resDTO != null) {
				status = resDTO.getSupplierStatus();
			}
		} finally {

		}

		return status;
	}

	/**
	 * To compare supplier status date
	 * 
	 * @param enddate
	 * @return boolean flag
	 */
	public boolean supplierStatusDateComparision(Date enddate) {
		boolean isDate = false;
		Date d1 = new Date();

		if (null != enddate) {
			long days = DateUtils.getDaysDifference(d1, enddate);
			int day = (int) days;
			if (day >= 0) {
				isDate = true;
			}
		}

		return isDate;
	}

	/**
	 * To get userRole Code
	 * 
	 * @return String roleCode
	 */
	public String getUserRoleCode() {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		reqDTO.setLanguageCode(CommonConstants.DEFAULT_LANGUAGE_CODE);
		UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
		List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
		List<String> roleCodes = new ArrayList<String>();
		roleCodes.add(RoleCode.GOVT_SELLER.value());
		roleCodes.add(RoleCode.G2G_SUPPLIER_USER.value());
		roleCodes.add(RoleCode.G2G_SUPPLIER_ADMIN.value());
		roleCodes.add(RoleCode.MOF_SUPPLIER_ADMIN.value());
		roleCodes.add(RoleCode.MOF_SUPPLIER_USER.value());
		roleCodes.add(RoleCode.BASIC_SUPPLIER_ADMIN.value());
		roleCodes.add(RoleCode.BASIC_SUPPLIER_USER.value());
		roleCodes.add(RoleCode.REQUISITIONER.value());
		roleCodes.add(RoleCode.RN_APPROVER.value());
		roleCodes.add(RoleCode.CT_APPROVER.value());
		roleCodes.add(RoleCode.BPK_APPROVER.value());
		roleCodes.add(RoleCode.DESK_OFFICER.value());
		roleCodes.add(RoleCode.SC_SECRETARY.value());
		roleCodes.add(RoleCode.SC_MEMBER.value());
		roleCodes.add(RoleCode.SC_CHAIRPERSON.value());

		String roleCode = null;

		List<String> pmRoleCodes = new ArrayList<String>();

		if (null != roleDesc) {
			for (PmRoleDesc pmRoleDesc : roleDesc) {
				pmRoleCodes.add(pmRoleDesc.getPmRole().getRoleCode());
			}

			for (String role : roleCodes) {
				if (pmRoleCodes.contains(role)) {
					roleCode = role;
					break;
				}
			}
		}
		return roleCode;
	}

	/**
	 * To mark Selected records to be deleted
	 * 
	 * @param list
	 * @param markDeletedList
	 */
	public List<ScQtCheckList> markDeleteSelectedRowsActionById(
			List<ScQtCheckList> list, List<ScQtCheckList> markDeletedList) {
		// List<Integer> toBeRemoveIndx = new ArrayList<Integer>();
		if (log.isDebugEnabled()) {
			log.debug("markDeleteSelectedRowsAction");
		}
		try {
			if (markDeletedList == null) {
				markDeletedList = new ArrayList<ScQtCheckList>();
			}
			if (list != null) {

				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).isSelected()) {
						list.get(i).setDeleted(true);
						if (null == list.get(i).getTitle()
								|| StringUtils.isBlank(list.get(i).getTitle())) {
							list.get(i).setTitle("hellodelete");
						}
						markDeletedList.add(list.get(i));

						list.remove(list.get(i));
						i--;
					}
				}
				if (markDeletedList.size() == 0) {
					throw new EpException(MessageConst.ERROR_760146);
				}

				// deleteRowsActionById(list, toBeRemoveIndx);
			} else {
				throw new EpException(MessageConst.ERROR_760146);
			}
		} finally {

		}
		return markDeletedList;
	}

	/**
	 * To remove records for existing.
	 * 
	 * @param list
	 * @param toBeRemovedList
	 */
	public void deleteRowsActionById(List<? extends BaseEntity> list,
			List<Integer> toBeRemovedList) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}

		if (CollectionUtils.isNotEmpty(list)
				&& CollectionUtils.isNotEmpty(toBeRemovedList)) {
			for (int i = 0; i < toBeRemovedList.size(); i++) {
				list.remove(toBeRemovedList.get(i).intValue());
			}
		}
	}

	/**
	 * To check whether enable field or not.
	 * 
	 * @param fieldName
	 * @param statusId
	 * @param isMaintance
	 * @return boolean flag
	 */
	public boolean enableFieldByStatusNFiledName(String fieldName,
			Long statusId, boolean isMaintance, boolean isDeskOfficer) {

		List<String> values = QTFiledMapper.getEnableFieldsByStatus(statusId,
				isMaintance, isDeskOfficer);
		if (CollectionUtils.isNotEmpty(values)) {
			if (values.contains(fieldName)) {
				return false;
			}

		}
		return true;

	}

	/**
	 * Validate Duplicate Category code.
	 * 
	 * @param pmCategoryL3
	 *            the category l3
	 * @param categoryList
	 *            the category list
	 */
	public void validateDuplicateCategoryCode(PmCategoryL3 pmCategoryL3,
			List<ScQtCategory> categoryList) {
		boolean isDuplicate = false;
		for (int i = 0; i < categoryList.size(); i++) {
			ScQtCategory scQtCategory = categoryList.get(i);

			if (scQtCategory.getPmCategoryL3().getCategoryL3Code()
					.equals(pmCategoryL3.getCategoryL3Code())
					&& scQtCategory
							.getPmCategoryL2()
							.getCategoryL2Code()
							.equals(pmCategoryL3.getPmCategoryL2()
									.getCategoryL2Code())
					&& scQtCategory
							.getPmCategoryL1()
							.getCategoryL1Code()
							.equals(pmCategoryL3.getPmCategoryL2()
									.getPmCategoryL1().getCategoryL1Code())) {
				isDuplicate = true;

				break;
			}
		}

		if (isDuplicate) {
			throw new EpException(MessageConst.ERROR_760111);
		}
	}

	/**
	 * To save Evaluation Attachment
	 * 
	 * @param fileName
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void SaveEvaluationAttachment(String fileName,
			ScAttachment attachment) throws FileNotFoundException, IOException {

		attachment.setFileName(fileName);
		attachment.setFileDesc("File uploded");

		populateAttachmentDetails(attachment);

		// FileOutputStream fos;
		// fos = new FileOutputStream(getActualFilePath(attachment));
		// fos.write(fileBytes);
		// fos.close();

		/*
		 * FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
		 * "The file was uploaded successfully.", null);
		 * 
		 * FacesContext.getCurrentInstance().addMessage(null, message);
		 */
	}

	public List<String> getRequestTypes(String paramaterTypes) {
		if (StringUtils.isNotEmpty(paramaterTypes)) {
			String[] paramTypes = paramaterTypes.split(",");
			return Arrays.asList(paramTypes);
		}
		return null;
	}

	/**
	 * Update approval request.
	 * 
	 * @param approvalRequests
	 *            the approval requests
	 * @param outCome
	 *            the out come
	 */
	protected void updateApprovalRequest(
			List<ScQtApprovalRequest> approvalRequests, String outCome) {

		PmParameter parameter = null;
		if (!BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outCome)) {
			parameter = getParameterByTypeAndCode(
					ParameterTypeConst.APPROVAL_DECISION_TYPE, ApprovalStatus
							.valueOf(outCome).getApprovalStatus());
		}
		if (CollectionUtils.isNotEmpty(approvalRequests)) {
			for (ScQtApprovalRequest approvalRequest : approvalRequests) {
				if (null != parameter) {
					approvalRequest.setApproverAction(parameter);
				}
				approvalRequest.setApproverActionDate(new Date());
				approvalRequest.setApproverId(new FacesScopes()
						.getUserSessionBean());

			}
		}

	}

	/**
	 * Check is multiple proposal category
	 * 
	 * @param qtId
	 *            the quotation/tender id (Mandatory),
	 * @param scQtCategories
	 *            the list of ScQtCategory instance (optional)
	 * @return
	 */
	public boolean isMultiplePorposalCategory(Long qtId,
			List<ScQtCategory> scQtCategories) {
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCategoryList(scQtCategories);
			return qtDetailSO.isMultiplePorposalCategory(reqDTO);

		} finally {
			qtDetailSO = null;
		}
	}

	/**
	 * Check if one tier threshold
	 * 
	 * @param estimatedAmount
	 * 		the estimated amount
	 * @return	the indicator is one tier threshold
	 */
	public boolean isOneTierThresHold(BigDecimal estimatedAmount) {
		IRuleSO ruleSO = null;
		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(RuleTypeConst.ONE_TIER_EVALUATION_THRESHOLD);
			
			List<PmConfigRuleValue> configRuleValues = ruleSO.getRuleValuesByTypeInCurrentDate(reqDTO)
					.getRuleValueList();
			if (configRuleValues != null && configRuleValues.size() > 0) {
				PmConfigRuleValue configRuleValue = configRuleValues.get(0);
				
				if (estimatedAmount != null && estimatedAmount.compareTo(BigDecimal.valueOf(0)) > 0) {
					if (configRuleValue != null && configRuleValue.getRuleValue() != null) {
						return estimatedAmount.compareTo(new BigDecimal(configRuleValue.getRuleValue())) <= 0;
					}
				}
				
			} else {
				throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(reqDTO.getRuleType())});
			}
			
			return false;

		} finally {
			ruleSO = null;
		}
	}

	/**
	 * Check if bumiputra threshold
	 * 
	 * @param estimatedAmount
	 * 		the estimated amount
	 * @return	the indicator is bumiputra threshold
	 */
	protected boolean isOpenForBumiputraThresHold(BigDecimal estimatedAmount) {
		IRuleSO ruleSO = null;
		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(RuleTypeConst.OPEN_FOR_BUMI_THRESHOLD);
			
			List<PmConfigRuleValue> configRuleValues = ruleSO.getRuleValuesByTypeInCurrentDate(reqDTO)
					.getRuleValueList();

			if (estimatedAmount != null && estimatedAmount.compareTo(BigDecimal.valueOf(0)) > 0) {
				if (configRuleValues != null && configRuleValues.size() > 0) {
					PmConfigRuleValue configRuleValue = configRuleValues.get(0);
					
					if (configRuleValue != null && configRuleValue.getFromValue() != null && configRuleValue.getToValue() != null) {
						return estimatedAmount.compareTo(configRuleValue.getFromValue()) >= 0 && 
								estimatedAmount.compareTo(configRuleValue.getToValue()) <= 0;
					}
					
				} else {
					throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(reqDTO.getRuleType())});
				}	
			}
			
			return false;

		} finally {
			ruleSO = null;
		}
	}

	/**
	 * Check if quotation threshold
	 * 
	 * @param estimatedAmount
	 * 		the estimated amount
	 * @return	the indicator is quotation threshold
	 */
	protected boolean isQuotationThresHold(BigDecimal estimatedAmount) {
		IRuleSO ruleSO = null;
		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(RuleTypeConst.QUOTATION_THRESHOLD);
			reqDTO.setCompareValue(estimatedAmount.toString());
			List<String> ruleValueStrList = ruleSO
					.getRuleValuesByTypeAndCurrentDateRange(reqDTO)
					.getRuleValueStrList();

			if (ruleValueStrList != null && ruleValueStrList.size() > 0) {
				return ParameterConst.PROCUREMENT_MODE_QUOTATION
						.equals(ruleValueStrList.get(0));
			}/* else {
				throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(reqDTO.getRuleType())});
			}*/ // temporary take out because xhtml using it.
			
			return true;

		} finally {
			ruleSO = null;
		}
	}
	
	/**
	 * Get purchase request threshold amount
	 * 
	 * @return	the purchase request threshold amount
	 */
	protected BigDecimal getPRThresholdAmount() {
		IRuleSO ruleSO = null;
		BigDecimal maxProcurementAmt = new BigDecimal(0);

		try {
			ruleSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(RuleTypeConst.PURCHASE_REQUEST_THERSHOLD);
			RuleResDTO ruleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);

			if (ruleResDTO.isSuccessful()) {
				if (ruleResDTO.getRuleValueList() != null
						&& ruleResDTO.getRuleValueList().size() > 0) {
					maxProcurementAmt = new BigDecimal(ruleResDTO
							.getRuleValueList().get(0).getRuleValue());
				} else {
					throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
				}	
			}

		} finally {
			ruleSO = null;
		}

		return maxProcurementAmt;
	}
	
	/**
	 * Get panel threshold amount
	 * 
	 * @return	the panel threshold amount
	 */
	protected BigDecimal getPanelThresholdAmount() {
		IRuleSO ruleSO = null;
		BigDecimal maxProcurementAmt = new BigDecimal(0);

		try {
			ruleSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(RuleTypeConst.PANEL_THRESHOLD);
			RuleResDTO ruleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
			
			if (ruleResDTO.isSuccessful()) {
				if(ruleResDTO.getRuleValueList()!=null && ruleResDTO.getRuleValueList().size()>0) {
					maxProcurementAmt = new BigDecimal(ruleResDTO.getRuleValueList().get(0).getRuleValue());
				} else {
					throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
				}	
			}
			
		} finally {
			ruleSO = null;
		}

		return maxProcurementAmt;
	}

	/**
	 * Gets the role type dropdown values.
	 * 
	 * @return the role type dropdown values
	 */
	public List<PmParameterDesc> getRoleTypeDropdownValues(
			boolean isChairPersonReq) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		if (!isChairPersonReq) {
			List<String> recommendList = new ArrayList<String>();
			recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(),
							recommendList));
		}

		return retrieveParametersBySearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, searchBean);

	}

	public SmMofAccount retriveMofAccountBySupplier(SmSupplier smSupplier) {
		SmMofAccount mofAccount = null;

		MofAccountResDTO mofAccResDTO = new MofAccountResDTO();
		IMofAccountSO mofAccountSO = null;

		try {
			if (null != smSupplier && null != smSupplier.getSupplierId()) {
				log.info("===sourcingbasebackingbean mof account smsupplierid==="
						+ smSupplier.getSupplierId());
				mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								IMofAccountSO.class);
				MofAccountReqDTO mofAccReqDTO = new MofAccountReqDTO();
				mofAccReqDTO.setSupplierId(smSupplier.getSupplierId());
				mofAccReqDTO
						.setSupplierType(ParameterConst.SUPPLIER_TYPE_CONTRACTOR);
				mofAccResDTO = mofAccountSO
						.retrieveMofNoBySupplierId(mofAccReqDTO);
				if (null != mofAccResDTO.getMofAccount()) {
					mofAccount = mofAccResDTO.getMofAccount();
					smSupplier.setSmMofAccount(mofAccount);
				} else {
					mofAccount = new SmMofAccount();
				}
			} else {
				mofAccount = new SmMofAccount();
			}

		} finally {
			mofAccountSO = null;
		}
		return mofAccount;

	}

	/**
	 * Convert tree nodes to zone criteria.convert General treenode list to
	 * TplSpecZoneLoc objects.
	 * 
	 * @param treeNodes
	 *            (list of treeNodes)
	 * @param zoneDtl
	 *            (TplSpecZoneDtl Object)
	 * @return List<TplSpecZoneDtl>
	 */
	public List<TplSpecZoneDtl> convertTreeNodesToZoneCriteria(
			List<TreeNode> treeNodes, TplSpecZoneDtl zoneDtl) {

		List<TplSpecZoneDtl> zoneList = new ArrayList<TplSpecZoneDtl>();
		List<TplSpecZoneLoc> results = null;

		if (null != treeNodes && !treeNodes.isEmpty()) {
			for (TreeNode treeNode : treeNodes) {
				TplSpecZoneDtl tplSpecZoneDtl = null;
				results = new ArrayList<TplSpecZoneLoc>();
				List<TreeNode> zoneNodes = treeNode.getChildren();
				// zoneDtl=(TplSpecZoneDtl)treeNode.getData();
				// zoneDtl = (TplSpecZoneDtl) zoneNodes.getData();
				// TplSpecZoneDtl zoneDtl=new TplSpecZoneDtl();
				zoneDtl = (TplSpecZoneDtl) zoneNodes.get(0).getData();
				for (TreeNode treeNode1 : zoneNodes) {

					List<TreeNode> stateNodes = treeNode1.getChildren();

					for (TreeNode treeNode2 : stateNodes) {

						List<TreeNode> district = treeNode2.getChildren();
						if (null != district && !district.isEmpty()) {
							for (TreeNode devision : district) {

								if (devision.getChildCount() > 0) {
									for (TreeNode districtNode : devision
											.getChildren()) {
										TplSpecZoneLoc criteria = (TplSpecZoneLoc) districtNode
												.getData();

										criteria.setPmDistrict(((TplSpecZoneLoc) districtNode
												.getData()).getPmDistrict());
										criteria.setPmDivision(((TplSpecZoneLoc) districtNode
												.getParent().getData())
												.getPmDivision());
										criteria.setPmState(((TplSpecZoneLoc) treeNode2
												.getData()).getPmState());
										criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
												.getData()));
										tplSpecZoneDtl = criteria
												.getTplSpecZoneDtl();
										// criteria.setScQtSupplierCriteria(supplierCriteria);
										setUserDetails(criteria);
										results.add(criteria);
										// tplSpecZoneDtl
										// .setTplSpecZoneLocs(results);

									}

								} else {

									TplSpecZoneLoc criteria = (TplSpecZoneLoc) devision
											.getData();
									criteria.setPmDistrict(((TplSpecZoneLoc) devision
											.getData()).getPmDistrict());
									criteria.setPmState(((TplSpecZoneLoc) treeNode2
											.getData()).getPmState());

									criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
											.getData()));
									tplSpecZoneDtl = criteria
											.getTplSpecZoneDtl();
									// criteria.setScQtSupplierCriteria(supplierCriteria);
									setUserDetails(criteria);
									results.add(criteria);
									// tplSpecZoneDtl.setTplSpecZoneLocs(results);

								}

								// zoneDtl.setTplSpecZoneLocs(results);
								// setUserDetails(tplSpecZoneDtl);

								// zoneList.add(tplSpecZoneDtl);

							}
						} else {

							TplSpecZoneLoc criteria = (TplSpecZoneLoc) treeNode2
									.getData();
							criteria.setPmState(((TplSpecZoneLoc) treeNode2
									.getData()).getPmState());

							criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
									.getData()));
							tplSpecZoneDtl = criteria.getTplSpecZoneDtl();
							// criteria.setScQtSupplierCriteria(supplierCriteria);
							setUserDetails(criteria);
							results.add(criteria);
							// tplSpecZoneDtl.setTplSpecZoneLocs(results);
						}

					}
					if (tplSpecZoneDtl != null) {
						tplSpecZoneDtl.setTplSpecZoneLocs(results);
						setUserDetails(tplSpecZoneDtl);
						zoneList.add(tplSpecZoneDtl);
						// tplSpecDtls.add(tplSpecZoneDtl);
						// scQuoteZone.setTplSpecZoneDtls(tplSpecDtls);
					} else {
						tplSpecZoneDtl = (TplSpecZoneDtl) treeNode1.getData();
						setUserDetails(tplSpecZoneDtl);
						zoneList.add(tplSpecZoneDtl);
					}

				}

			}

			// zoneDtl.setTplSpecZone(new ScQtZoneCriteria());
			// setUserDetails(zoneDtl);

			// zoneList.add(zoneDtl);

		}

		return zoneList;
	}

	/**
	 * Convert tree nodes to location criteria.convert supplier location tree
	 * node list to supplier location objects
	 * 
	 * @param treeNodes
	 *            (list of treenodes)
	 * @param supplierCriteria
	 *            (supplierCriteria object)
	 * @return List<ScQtLocCriteria>
	 */
	public List<ScQtLocCriteria> convertTreeNodesToLocationCriteria(
			List<TreeNode> treeNodes, ScQtSupplierCriteria supplierCriteria) {
		List<ScQtLocCriteria> locCriterias = new ArrayList<ScQtLocCriteria>();
		if (null != treeNodes && !treeNodes.isEmpty()) {
			for (TreeNode treeNode : treeNodes) {
				List<TreeNode> stateNodes = treeNode.getChildren();
				for (TreeNode treeNode2 : stateNodes) {
					List<TreeNode> district = treeNode2.getChildren();
					if (null != district && !district.isEmpty()) {
						for (TreeNode devision : district) {
							if (devision.getChildCount() > 0) {
								for (TreeNode districtNode : devision
										.getChildren()) {
									ScQtLocCriteria criteria = ((ScQtLocCriteria) districtNode
											.getData());
									criteria.setPmDistrict(((ScQtLocCriteria) districtNode
											.getData()).getPmDistrict());
									criteria.setPmDivision(((ScQtLocCriteria) districtNode
											.getParent().getData())
											.getPmDivision());
									criteria.setPmState(((ScQtLocCriteria) treeNode2
											.getData()).getPmState());
									criteria.setScQtSupplierCriteria(supplierCriteria);
									setUserDetails(criteria);
									locCriterias.add(criteria);
								}
							} else {
								ScQtLocCriteria criteria = ((ScQtLocCriteria) devision
										.getData());
								criteria.setPmDistrict(((ScQtLocCriteria) devision
										.getData()).getPmDistrict());
								criteria.setPmState(((ScQtLocCriteria) treeNode2
										.getData()).getPmState());
								criteria.setScQtSupplierCriteria(supplierCriteria);
								setUserDetails(criteria);
								locCriterias.add(criteria);
							}

						}
					} else {

						ScQtLocCriteria criteria = (ScQtLocCriteria) treeNode2
								.getData();
						criteria.setScQtSupplierCriteria(supplierCriteria);
						setUserDetails(criteria);

						locCriterias.add(criteria);
					}
				}
			}
		}

		return locCriterias;
	}

	/**
	 * Generate bond letter from velocity to pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @throws Exception
	 *             the exception
	 */
	public void generateBondLetterFromVelocityToPdf(ScAttachment attachment,
			ScLoa scLoa) throws Exception {
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		attachment.setFileName("Bond Letter.pdf");
		populateAttachmentDetails(attachment);
		VelocityContext context = new VelocityContext();
		String contractNo = "Contract Number";
		String supplierName = null;
		String sourcingTitle = null;
		String bondAmount = null;
		String bondAmountWord = null;
		String expiryPeriod = null;
		int type = 0;

		if (scLoa != null) {
			type = 1;
			supplierName = scLoa.getScLoiLoa() != null ? scLoa.getScLoiLoa()
					.getSupplierName() : " ";
			sourcingTitle = scLoa.getScLoiLoa() != null ? scLoa.getScLoiLoa()
					.getTitle() : " ";
			bondAmount = scLoa.getBondAmt() != null ? amountDF.format(scLoa
					.getBondAmt()) : " ";
			expiryPeriod = scLoa.getBondResDueDate() != null ? dateFormat
					.format(scLoa.getBondResDueDate()) : " ";
			bondAmountWord = scLoa.getBondAmt() != null ? NumberWordUtil
					.convert(scLoa.getBondAmt().longValue()) : " ";

		} else {
			type = 0;
			supplierName = "Supplier Name";
			sourcingTitle = "Sourcing Title";
			bondAmount = "Bond Amount";
			expiryPeriod = "Expiry Period";
			bondAmountWord = "Bond Amount Word";
		}

		context.put("type", type);
		context.put("contractNo", contractNo);
		context.put("supplierName", supplierName);
		context.put("sourcingTitle", sourcingTitle);
		context.put("bondAmount", bondAmount);
		context.put("bondAmountWord", bondAmountWord);
		context.put("expiryPeriod", expiryPeriod);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_BOND_LETTER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Calculate bond amount.
	 * 
	 * @param scLoa
	 *            the sc loa
	 * @return the big decimal
	 */
	public ScLoa calcBondAmount(ScLoa scLoa,boolean isDirectLoa) {
	/*	The formula for bond should be as follow
		LOA Amount/duration (in year) = x
		x is being checked against the rate either 2.5% or 5%, if X >=RM 500,000, the rate being used is 5%

		In the sample below - the calculation should be
		2,482,400/0.25 yr ( since the duration is 3 month) - 9933600
		Bond amount - 9933600 * 0.05 = 496680*/

		if(isDirectLoa){
			scLoa.setBondReq(isPerformanceBondRequired(scLoa.getLoaAmt()));
		}
		
		if (scLoa.isBondReq()) {
			BigDecimal bondAmt = BigDecimal.ZERO;
			BigDecimal totalDuration = BigDecimal.ZERO;
			BigDecimal bondPercentageLoaAmt = BigDecimal.ZERO;
			BigDecimal bondPercentage = BigDecimal.ZERO;
			if (scLoa.getLoaAmt() != null && scLoa.getContractDuration() != null) {
				BigDecimal overallCtAmount = overallCtAmount(scLoa.getLoaAmt(),
						scLoa.getGstAmt());
				BigDecimal duration = scLoa.getContractDuration();
				duration = duration.setScale(4, BigDecimal.ROUND_HALF_UP);
				// Duration in years
				totalDuration = duration.divide(new BigDecimal(12), 4,
						BigDecimal.ROUND_HALF_UP);
				
				double ceilingDuration = Math.ceil(totalDuration.doubleValue());
				
				totalDuration = BigDecimal.valueOf(ceilingDuration);

				// Total amount to get bond percentage
				bondPercentageLoaAmt = overallCtAmount.divide(
						totalDuration, 4, BigDecimal.ROUND_HALF_UP);
				
				// Get performance bond rule percentage
				IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IRuleSO.class);
				RuleReqDTO ruleReqDTO = new RuleReqDTO();
				ruleReqDTO.setRuleType(RuleTypeConst.BOND_PERCENTAGE);
				ruleReqDTO.setCompareValue(bondPercentageLoaAmt.toString());
				RuleResDTO roleResDTO = ruleSO
						.getRuleValuesByTypeAndCurrentDateRange(ruleReqDTO);

				if (roleResDTO.isSuccessful()) {
					if (roleResDTO.getRuleValueStrList() != null
							&& roleResDTO.getRuleValueStrList().size() > 0) {
						String percentage = roleResDTO.getRuleValueStrList().get(0);
						BigDecimal res = new BigDecimal(percentage);
						bondPercentage = res.divide(new BigDecimal(100), 4,
								BigDecimal.ROUND_HALF_UP);
						scLoa.setBondPercentage(new BigDecimal(percentage));
					} else {
						throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
					}
				}
				if (bondPercentage.doubleValue() > 0) {
					bondAmt = bondPercentageLoaAmt.multiply(bondPercentage);
					bondAmt = bondAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
					scLoa.setBondAmt(bondAmt);
				}
			}

		}else{
			scLoa.setBondAmt(null);
			scLoa.setBondPercentage(null);
		}
		return scLoa;
	}

	/**
	 * @param offerPrice
	 * @param gstAmount
	 * @return
	 */
	public BigDecimal overallCtAmount(BigDecimal offerPrice,
			BigDecimal gstAmount) {

		BigDecimal overallCtAmount = offerPrice;
		if (gstAmount != null) {
			overallCtAmount = overallCtAmount.add(gstAmount);
			overallCtAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}

		return overallCtAmount;

	}

	/**
	 * @param scQt
	 * @return
	 */
	public boolean handleProposalValidityDays(ScQt scQt) {
		Date validityDate = null;
		if (null != scQt.getProposalValidityEndDate()) {
			log.info("scQt.getProposalValidityEndDate() not null"
					+ scQt.getProposalValidityEndDate());
			validityDate = scQt.getProposalValidityEndDate();
		} else {
			log.info("scQt.getProposalValidityEndDate()  null value");
			if (null != scQt.getClosingDate()) {
				validityDate = DateUtils.addDays(scQt.getClosingDate(),
						scQt.getQtValidityPeriod());
			}
		}
		
		boolean isValidityDateNotReached = false;
		if (null != validityDate) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
			scQt.setProposalValidityUntil(sdf.format(validityDate));
			isValidityDateNotReached = 
						checkProposalValidityDateReach(validityDate);
		}

		return isValidityDateNotReached;

	}

	/**
	 * To check is performance bond required
	 * 
	 * @return boolean isPerformanceBondRequired
	 */
	public boolean isPerformanceBondRequired(BigDecimal loaAmount) {
		if (null != loaAmount) {
			IRuleSO ruleSO = null;
			try {
				ruleSO = (IRuleSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IRuleSO.class);
				RuleReqDTO ruleReqDTO = new RuleReqDTO();
				ruleReqDTO.setRuleType(RuleTypeConst.BOND);
				List<PmConfigRuleValue> ruleValueList = ruleSO
						.getRuleValuesByRuleType(ruleReqDTO).getRuleValueList();

				if (ruleValueList != null && ruleValueList.size() > 0) {
					PmConfigRuleValue configRuleValue = ruleValueList.get(0);
					if (loaAmount.compareTo(new BigDecimal(configRuleValue
							.getRuleValue())) > 0) {

						return true;
					}

				}

			} finally {
				ruleSO = null;
			}

		}
		
		return false;
	}

	/**
	 * To check whether proposalValidityDatereached or not..
	 * @param proposalValidityEndDate
	 * @return isProposalValidityDateReached
	 */
	public boolean checkProposalValidityDateReach(Date proposalValidityEndDate){
		if (proposalValidityEndDate != null) {
			long days = DateUtils.getDaysDifference(
								  new Date(), proposalValidityEndDate);
			int day = (int) days;
			if (day >= 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the PTJ Organization Profile By Login User
	 * 
	 * @return	the PmOrgProfile instance
	 */
	public PmOrgProfile getPTJOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		
		if (userId != null) {
			IOrgProfileSO orgProfileSO = null;
			try {
				orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setOrgType(OrganizationType.DIVISION.value());
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);
				List<PmOrgProfile> pmOrgProfiles = resDTO.getOrgProfileList();
				if (pmOrgProfiles != null && pmOrgProfiles.size() > 0) {
					pmOrgProfile = pmOrgProfiles.get(0);
				}

			} finally {
				orgProfileSO = null;
			}	
		}
		
		return pmOrgProfile;
	}
	
	/**
	 * Gets the message.
	 * 
	 * @param key
	 *            - String key
	 * @param params
	 *            - String[]
	 * @return the message
	 */
	public static String getMessage(String key, String[] params) {
		Locale locale = new FacesScopes().getUserLocale();
		String msg = "";

		if (params != null) {
			msg = ResourceBundleUtils.getMessage(key, params,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else {
			msg = ResourceBundleUtils.getMessage(key,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		}

		return msg;
	}
	
	protected SmAddress cloneSmAddress(SmAddress address) {
		log.info("cloneSmAddress : " + address);
		
		SmAddress newAddress = null;

		if (address != null) {
			try {
				newAddress = (SmAddress) BeanUtils.cloneBean(address);
				
				if (newAddress != null) {
					newAddress.setBrandNew(true);
					newAddress.setAddressId(null);
					
					newAddress.setSmAssociateCompanies(null);
					newAddress.setSmCompanyAddresses(null);
					newAddress.setSmEquityCompanies(null);
					newAddress.setSmFacilities(null);
					newAddress.setSmPersonnelAddresses(null);
					newAddress.setSmSupplierBanks(null);
					newAddress.setSmSupplierBranches(null);
					
					setUserDetails(newAddress);
				}
			} catch (IllegalAccessException
					| InstantiationException
					| InvocationTargetException
					| NoSuchMethodException e) {
				e.printStackTrace();
				log.error(e);
				log.warn("clone " + address + ": " + address.getAddressId() + " failed.");
			}
		}
		
		return newAddress;
	}
	
	public List<CtPanelItemGroup> getPanelGroupItemsByCirculerVer(CtCircularVer circular) {
		IPanelGroupItemSO ipanelGroupItemSO;
		try {
			PanelGroupItemReqDTO reqDTO = new PanelGroupItemReqDTO();
			
			ipanelGroupItemSO = (IPanelGroupItemSO) EpEjbBusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelGroupItemSO.class);
			if (null != circular) {
				reqDTO.setCircularVerId(circular.getCircularVerId());
								
				PanelGroupItemResDTO resDTO = ipanelGroupItemSO
						.retrievePanelGroupItemsByCircularVerId(reqDTO);
				
						return resDTO.getCtPanelItemGroupList();
			}

		} finally {

		}
		return null;

	}
	
	public List<PmUser> getUsersByRoleCode(String roleCode) {
		
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(roleCode);
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		return userResDTO.getUserList();
		
	}
	
	/**
	 * Check if QT Status published.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @return	the indicator qt status published.
	 */
	public boolean isQTStatusPublished(Long currentStatusId) {
		return isQTStatusGreaterThanOrEquals(currentStatusId, QTStatusConst.QT_BRIEFING_SITE_VISIT);
	}
	
	/**
	 * Check if QT Status greater than or equals from status.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @param fromStatusId
	 * 		the from status id
	 * @return	the indicator qt status greater than or equals from status.
	 */
	public boolean isQTStatusGreaterThanOrEquals(Long currentStatusId, Long fromStatusId) {
		List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		
		return orderedStatuses.indexOf(currentStatusId) >= orderedStatuses.indexOf(fromStatusId);
	}
	
	/**
	 * Check if QT Status greater than from status.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @param fromStatusId
	 * 		the from status id
	 * @return	the indicator qt status greater than from status.
	 */
	public boolean isQTStatusGreaterThan(Long currentStatusId, Long fromStatusId) {
		List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		
		return orderedStatuses.indexOf(currentStatusId) > orderedStatuses.indexOf(fromStatusId);
	}
	
	/**
	 * Check if QT Status less than or equals from status.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @param toStatusId
	 * 		the to status id
	 * @return	the indicator qt status less than from status.
	 */
	public boolean isQTStatusLessThanOrEquals(Long currentStatusId, Long toStatusId) {
		List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		
		return orderedStatuses.indexOf(currentStatusId) <= orderedStatuses.indexOf(toStatusId);
	}
	
	/**
	 * Check if QT Status is in between from and to status.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @param fromStatusId
	 * 		the from status id
	 * @param toStatusId
	 * 		the to status id
	 * @return	the indicator qt status in between from and to status.
	 */
	public boolean isQTStatusInBetween(Long currentStatusId, Long fromStatusId, Long toStatusId) {
		List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		orderedStatuses = orderedStatuses.subList(
				orderedStatuses.indexOf(fromStatusId), 
				orderedStatuses.indexOf(toStatusId)+1);
		
		return orderedStatuses.contains(currentStatusId);
	}
	
	/**
	 * Check if Current Committee greater than or equals compare committee.
	 * 
	 * @param currentCommittee
	 * 		the current committee
	 * @param otherCommittee
	 * 		the compare committee
	 * @return	the indicator greater than or equals compare committee.
	 */
	public boolean isCurrentCommitteeGreaterThanOrEquals(String currentCommittee, String otherCommittee) {
		List<String> committeesOrder = Arrays.asList(SourcingConstants.COMMITTEES_ORDER);
		
		return committeesOrder.indexOf(currentCommittee) >= committeesOrder.indexOf(otherCommittee);
	}
	
	/**
	 * Contract type drop down values.
	 * @return the list
	 */
	public List<PmParameterDesc> getContractTypes() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		// check logged in user allow central contract.
		boolean  isAllowCentralContract =  isAllowCentralContract();

		if(!isAllowCentralContract){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), ParameterConst.CONTRACT_TYPE_CENTRAL));
		} 

		List<PmParameterDesc> contractTypes = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(
						ParameterTypeConst.CONTRACT_TYPE, searchBean);

		return contractTypes;
	}
	
	/**
	 * is Allow Central Contract
	 * 
	 * @return 
	 */
	private boolean isAllowCentralContract(){
		boolean allowCentralContract  = false;
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		
		if (userId != null) {
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

				if(resDTO.getUserOrgList() != null && resDTO.getUserOrgList().size()>0){
					for (PmUserOrg pmUserOrg :  resDTO.getUserOrgList()) {
						if( pmUserOrg.getPmOrgProfile() != null){
							Boolean isBPK = pmUserOrg.getPmOrgProfile().getBpk();
							if(isBPK != null  && isBPK){
								allowCentralContract  = true;
								break;
							}
						}
					}
				}
			}finally{
				userSO = null;
			}
		}
		
		return allowCentralContract;		
	}
	
	/**
	 * Get maximum no of supplier for restricted tender
	 * 
	 * @return the maximum no of supplier for restricted tender
	 */
	public BigDecimal getNoOfSupplierForRestrictedTender() {
		IRuleSO ruleSO = null;
		try {
			ruleSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(RuleTypeConst.QT_RT_MINIMUM_NUMBER_SUPPLIER);
			
			List<PmConfigRuleValue> configRuleValues = ruleSO.getRuleValuesByTypeInCurrentDate(reqDTO)
					.getRuleValueList();

			if (configRuleValues != null && configRuleValues.size() > 0) {
				PmConfigRuleValue configRuleValue = configRuleValues.get(0);
				
				if (configRuleValue != null && configRuleValue.getFromValue() != null && configRuleValue.getToValue() != null) {
					return configRuleValue.getToValue();
				}
				
			} else {
				log.warn("Rule " + RuleTypeConst.QT_RT_MINIMUM_NUMBER_SUPPLIER + " is not found.");
			}

		} finally {
			ruleSO = null;
		}
		
		return BigDecimal.valueOf(0);
	}
	
	
	/**
	 * Gets the role type dropdown values.
	 * 
	 * @return the role type dropdown values
	 */
	public List<PmParameterDesc> getRoleTypeDropdownValuesBasedOnPTJ(
			boolean isChairPersonReq,ScQt scqt,PmUser user) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		boolean isptj=false;
		if (!isChairPersonReq) {
			isptj=orgProfileCheckExist( scqt, user,isptj);
		if(isptj==true){
			recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(),
							recommendList));
			
		}
			
		}else{
			isptj=orgProfileCheckExist( scqt, user,isptj);
			
			
		}
		if(isptj==false){
				recommendList.add(ParameterConst.COMMITTEE_ROLE_MEMBER);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								recommendList));
			}

		return retrieveParametersBySearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, searchBean);

	}
	
	private boolean orgProfileCheckExist(ScQt scqt,PmUser user, boolean isptj){
		PmOrgProfile orgProfile=	scqt.getPmOrgProfile();
		log.info("===qt PtjId==="+orgProfile.getOrgProfileId());
		if(null!=user.getPmUserOrgs()){
		for(PmUserOrg userorg:user.getPmUserOrgs()){
			log.info("===user org record status==="+userorg.isRecordStatus());
			if(userorg.isRecordStatus() && null!=userorg.getPmOrgProfile() && userorg.getPmOrgProfile().getOrgProfileId().equals(orgProfile.getOrgProfileId())){
				isptj=true;
				break;
			}
			
		}
		}
		return isptj;
	}
	
	/**
	 * Gets the days by rule type.
	 *
	 * @param RuleType the rule type
	 * @return the days by rules
	 */
	protected Integer getDaysByRuleType(String ruleType){
		Integer days = 0;
		IRuleSO ruleSO;
		try {
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(ruleType);
			
			List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList();
			if (ruleValueList != null && ruleValueList.size() > 0) {
				PmConfigRuleValue configRuleValue = ruleValueList.get(0);
				days  = Integer.valueOf(configRuleValue.getRuleValue());
			} else {
				throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
			}
		} finally {
			ruleSO= null;
		}
		
		return days;
	}
	
	/**
	 * To update closing date and proposal validity date.
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 */
	protected void updatePublishClosingDate(ScQt scQt) {
		Date clsDate = null;
		if (null != scQt.getProposalStartDate()) {
			clsDate = DateUtils.addDays(scQt.getProposalStartDate(),
					scQt.getPublishPeriod());
		} 
		
		if (null != clsDate) {
			clsDate = DateUtils.setDefaultDateTime(clsDate);
		}

		scQt.setClosingDate(clsDate);
		
		if(scQt.isSampleReq()){
			scQt.setSampleClosingDate(clsDate);
		}
		
		updateProposalValidityDate(scQt);
	}
	
	/**
	 * To update proposal validity date.
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 */
	protected void updateProposalValidityDate(ScQt scQt) {
		Date clsDate = scQt.getClosingDate();
		Date proposalValidityEndDate = null;
		
		if (null != clsDate) {
			clsDate = DateUtils.setDefaultDateTime(clsDate);
			proposalValidityEndDate = DateUtils.addDays(clsDate,
					scQt.getQtValidityPeriod());
		}
		scQt.setProposalValidityEndDate(proposalValidityEndDate);
	}
	
	/**
	 * Role change listner.
	 * 
	 * @param member
	 *            the member
	 */
	public void roleChangedListener(ScCommitteeMember member) {
		Long paramId = member.getMemberRoleId();
		if (paramId != null) {
			member.setMemberRole(ParameterHelper
					.getInstance()
					.getPmParameterById(paramId));
		}
	}
	
	/**
	 * Representive type change.
	 *
	 * @param member the member
	 */
	public void representiveTypeChangedListener(ScCommitteeMember member) {
		Long paramId = member.getRepresentativeTypeId();
		if (null != paramId) {
			member.setRepresentativeType(ParameterHelper.getInstance()
					.getPmParameterById(paramId));
		}
	}
	
	/**
	 * Get PmStatus by status id
	 * 
	 * @param statusId
	 * 		the status id
	 * @return	the PmStatus instance
	 */
	public PmStatus getPmStatusByStatusId(Long statusId) {
		if (statusId != null) {
			IStatusSO statusSO = null;
			try {
				statusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setStatusId(statusId);
				return statusSO
						.retrieveStatusByStatusId(reqDTO)
						.getPmStatus();

			} finally {
				statusSO = null;
			}	
		}
		
		return null;
	}
}