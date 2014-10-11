/**
 * 
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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.approval.ApprovalBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentDefaultFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * @author VidyasagarReddy
 * 
 */
public abstract class SourcingBaseBackingBean<T extends BaseManagedBean>
		extends BaseBackingBean<T> {

	public static Logger log = EpLogger
			.getLogger(SourcingBaseBackingBean.class);

	/**
	 * Method to retrive orgprofiles based on user id and parent org id
	 * 
	 * @param profileReqDTO
	 * @return
	 */
	public List<PmOrgProfile> retriveOrgProfiles(OrgProfileReqDTO profileReqDTO) {

		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByOrgType(profileReqDTO);
		return resDTO.getOrgProfileList();
	}

	public List<SelectItem> toOrgProfileSelectItems(
			List<PmOrgProfile> orgProfile) {
		List<SelectItem> orgProfileSelectItems = new ArrayList<SelectItem>();
		orgProfileSelectItems.add(new SelectItem("", "Select One"));
		for (PmOrgProfile pmOrgProfile : orgProfile) {
			orgProfileSelectItems.add(new SelectItem(pmOrgProfile
					.getOrgProfileId().toString(), pmOrgProfile.getOrgCode()));
		}
		return orgProfileSelectItems;
	}

	public List<SelectItem> toSelectItmes(List<PmUser> users) {
		List<SelectItem> userList = new ArrayList<SelectItem>();
		userList.add(new SelectItem("", "SelectOne"));
		for (PmUser user : users) {
			userList.add(new SelectItem(user.getUserId(), user.getUserName()));
		}
		return userList;
	}

	protected void uploadAttachment(UploadedFile file, ScAttachment attachment)
			throws FileNotFoundException, IOException {
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();

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

	protected void populateAttachmentDetails(ScAttachment attachment)
			throws FileNotFoundException, IOException {
		Timestamp date = new Timestamp(System.currentTimeMillis());
		PmUser createdUser = new FacesScopes().getUserSessionBean();

		attachment.setCreatedBy(createdUser.getUserId());
		attachment.setCreatedDate(date);
		attachment.setChangedBy(createdUser.getUserId());
		attachment.setCreatedUser(createdUser);

		StringBuilder filePath = new StringBuilder();
		filePath.append(DateUtils.getLast2DigitOfYear());
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
				|| scAttachment instanceof ScQtFinalizationAttachment) {

			return new AttachmentDateFormat();
		} else {

			return new AttachmentDefaultFormat();
		}
	}

	protected String getActualFilePath(ScAttachment attachment) {
		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder actualFilePath = new StringBuilder(docRepoBaseDir);
		actualFilePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		actualFilePath.append(attachment.getFilePath());

		return actualFilePath.toString();
	}

	/**
	 * determineIcon for displaying image icon
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

			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

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
		response.setHeader("Content-Disposition", "attachment; filename="
				+ fileName);
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
		zos.close();
		response.flushBuffer();

		FacesContext.getCurrentInstance().responseComplete();
	}

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

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");

		this.getManagedBean().setTaskId(taskId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setOutComes(res.getAction());

		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();

		if ("Proposal Preparation".equalsIgnoreCase(res.getProcessName())) {

			SCSiteVisitProposalDataType proposalDateType = (SCSiteVisitProposalDataType) obj;
			/*
			 * tenderReqDTO.setQtId(new Long(proposalDateType.getQtId())
			 * .longValue()); QuationTenderResDTO tenderResDTO = detailSO
			 * .retriveQTDetails(tenderReqDTO);
			 * 
			 * detailBackingBean.getManagedBean()
			 * .setQt(tenderResDTO.getQtDetail());
			 */

			SPDetailBackingBean backingBean = new SPDetailBackingBean();
			backingBean.getManagedBean().setProposalData(proposalDateType);
			// detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
			// backingBean.retrieveTechnicalProposalList(proposalDateType
			// .getQtId().longValue());
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(
							fc,
							null,
							"/xhtml/ss/supplierproposal/supplierProposalMain.xhtml"
									+ "?faces-redirect=true");
		} else if ("Service QuotationTender Creation".equalsIgnoreCase(res
				.getProcessName())) {

			SCQuotationTenderDataType datatype = (SCQuotationTenderDataType) obj;
			tenderReqDTO.setQtId(new Long(datatype.getQtId()).longValue());
			QuationTenderResDTO tenderResDTO = detailSO
					.retriveQTDetails(tenderReqDTO);

			detailBackingBean.getManagedBean()
					.setQt(tenderResDTO.getQtDetail());
			detailBackingBean.getManagedBean().setTaskId(taskId);
			detailBackingBean.getManagedBean().setQuotationTenderDataType(
					datatype);
			// populateParentOrgProfile(tenderResDTO.getQtDetail());
			// detailBackingBean.onChangeAction();
			tenderResDTO = detailSO.retriveQTApprovalDetails(tenderReqDTO);
			ApprovalBackingBean approvalBackingBean = new ApprovalBackingBean();

			// detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(
							fc,
							null,
							"/xhtml/ss/quatationtender/quatationtendercreation.xhtml"
									+ "?faces-redirect=true");
		}
	}

	/**
	 * Method to populate parent orgprofiles for scqt.
	 */
	public void populateParentOrgProfile(ScQt scQt) {
		PmOrgProfile parentOrgProfile = scQt.getPmOrgProfile()
				.getParentOrgProfile();

		while (parentOrgProfile != null) {
			// Ministry
			if (OrganizationType.MINISTRY.value().equals(
					parentOrgProfile.getOrgTypeCode().getParameterCode())) {
				scQt.setMinistryOrgProfile(parentOrgProfile);
				break;
			}
			// Department
			else if (OrganizationType.DEPARTMENT.value().equals(
					parentOrgProfile.getOrgTypeCode().getParameterCode())) {
				scQt.setDepartmentOrgProfile(parentOrgProfile);
				break;
			}

			parentOrgProfile = parentOrgProfile.getParentOrgProfile();
		}
	}

	public boolean isNotNullOREmpty(List<? extends Object> testObject) {
		if (null != testObject && !testObject.isEmpty()) {
			return true;
		}
		return false;
	}

	public void setUserDetails(BaseEntity baseObject) {
		PmUser user = new FacesScopes().getUserSessionBean();

		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(user.getUserId());
			baseObject
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(user.getUserId());
	}

	public String getMessgeConstant(String outCome) {
		String msgCode = null;
		if (outCome.equalsIgnoreCase("SUBMIT")) {
			msgCode = MessageConst.INFO_700005;
		} else if (outCome.equalsIgnoreCase("REJECT")) {
			msgCode = MessageConst.INFO_700008;
		} else if (outCome.equalsIgnoreCase("REVISE")) {
			msgCode = MessageConst.INFO_700012;
		} else if (outCome.equalsIgnoreCase("APPROVE")) {
			msgCode = MessageConst.INFO_700007;
		} else if (outCome.equalsIgnoreCase("ACCEPT")) {
			msgCode = MessageConst.INFO_700013;
		}
		return msgCode;
	}

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

	protected <PARENT_ENTITY extends BaseEntity, REMARK_CLASS extends ScRemark> List<REMARK_CLASS> initializeRemarks(
			PARENT_ENTITY parent, PmStatus pmStatus,
			List<REMARK_CLASS> scRemarks, Class<REMARK_CLASS> clazz) {

		try {
			if (scRemarks == null) {
				scRemarks = new ArrayList<REMARK_CLASS>();
			}

			REMARK_CLASS prevRemark = null;
			if (scRemarks.size() > 0) {
				prevRemark = scRemarks.get(0);
			}

			FacesScopes facesScopes = new FacesScopes();
			if (scRemarks.size() == 0 || 
				!compareUserName(prevRemark.getCreatedUser().getLoginId()) ||
				(pmStatus != null && !prevRemark.getPmStatus().getStatusId().equals(pmStatus.getStatusId()))) {
				
				REMARK_CLASS remark = clazz.newInstance();
				remark.setCreatedBy(facesScopes.getUserSessionBean()
						.getUserId());
				remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				remark.setChangedBy(remark.getCreatedBy());
				remark.setCreatedUser(facesScopes.getUserSessionBean());
				remark.setParent(parent);
				remark.setPmStatus(pmStatus);

				scRemarks.add(0, remark);
			}

			return scRemarks;
		} catch (InstantiationException ie) {
			throw new EpException(ie.getMessage());
		} catch (IllegalAccessException ia) {
			throw new EpException(ia.getMessage());
		}
	}

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

	@SuppressWarnings("unchecked")
	protected <BPM_DATA_TYPE> BPM_DATA_TYPE getBpmTaskDetails(
			Class<BPM_DATA_TYPE> clazz) {
		FacesScopes facesScopes = new FacesScopes();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(facesScopes.getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);

		String taskId = originalServletRequest.getParameter("taskId");

		IBpmSO bpmSO;

		try {
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(taskId);

			BpmResponseDTO resDTO = bpmSO.getTaskDetails(reqDTO);

			this.renewSesionObject();
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setOutComes(resDTO.getAction());
			this.getManagedBean().setCurrentStatusId(
					Long.valueOf(resDTO.getDocStatus()));

			return (BPM_DATA_TYPE) resDTO.getPojo();

		} finally {
			bpmSO = null;
		}
	}

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



	public String gotoQtSearchPage() {
		return QTConstants.SEARCH_PAGE;
	}

	
	protected File generatePdfFromOracle(String reportName, Map<String, String> paramMap, ScAttachment attachment) throws IOException {
		EpPropertiesUtils propertiesUtils = EpPropertiesUtils.getInstance();
		String excPrevUrl = propertiesUtils
				.getProperty(EpPropertiesConst.EXC_PREV_URL);
		String excNextUrl = propertiesUtils
				.getProperty(EpPropertiesConst.EXC_NEXT_URL);
		StringBuilder reportUrl = new StringBuilder(excPrevUrl);
		reportUrl.append(ReportConst.AMP_REPORT_NAME);
		reportUrl.append(reportName);
		for (Entry<String, String> params : paramMap.entrySet()) {
			reportUrl.append(params.getKey());
			reportUrl.append(params.getValue());
		}
		reportUrl.append(excNextUrl);

		if (log.isDebugEnabled()) {
			log.debug("reportUrl: " + reportUrl);
		}

		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename="
				+ attachment.getFileName());
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		File localFile = new File(getActualFilePath(attachment));

		if (reportUrl.toString() != null) {
			InputStream input = null;
			try {
				URL obj = new URL(reportUrl.toString());
				HttpURLConnection uc = (HttpURLConnection) obj.openConnection();				
				input = new BufferedInputStream(uc.getInputStream()); 
			} catch (Exception e) {
				e.printStackTrace();
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
		out.flush();
		out.close(); 		
		return localFile;
	}

	/**
	 * To update digisign data
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
	}

	protected boolean isDigiSigningRequired(String roleCode) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		reqDTO.setLanguageCode("en");
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

}