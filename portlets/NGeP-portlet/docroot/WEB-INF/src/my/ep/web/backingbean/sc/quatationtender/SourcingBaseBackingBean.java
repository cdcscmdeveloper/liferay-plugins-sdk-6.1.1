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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.sc.QtCommitteeMemberBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.IPanelGroupItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.orgprofile.IPtjSpecialApprovalSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.bpm.ICompositeValiditySO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.bpm.vo.WorklistVO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateDocConst;
import my.ep.constants.BpmConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.circular.PanelGroupItemReqDTO;
import my.ep.dto.ct.circular.PanelGroupItemResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjSpecialApprovalReqDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjSpecialApprovalResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.general.settings.bpm.CompositeValidityReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.EvaluationResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmPtjSpecialApproval;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMemberHistory;
import my.ep.persistence.entity.sc.quationtender.ScEvaluationCommitteeReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScFinancialEvaluationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScOpenCommitteeReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScTechnicalEvaluationReportAttachment;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalReportAttachment;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.EvaluationReport;
import my.ep.util.CustomUtils;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.shared.PerformanceBondUtil;
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
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * 
 * SourcingBaseBackingBean is the Base class for Quotation Tender
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public abstract class SourcingBaseBackingBean<T extends BaseManagedBean>
		extends BaseBackingBean<T> {
	private static final long serialVersionUID = 1L;
	private static final Locale myLocale = new Locale(SourcingConstants.MALAY_LANGUAGE_CODE,
			  SourcingConstants.MALAYSIA_COUNTRY_CODE);

	
	/**
	 * Logger instance.
	 */
	public static Logger log = EpLogger
			.getLogger(SourcingBaseBackingBean.class);
	
	private static final String UTF = "UTF-8";
	
	// #18685 - Start
	protected static final String  UNDER_SCORE = "_";
	// #18685 - End

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
		FileOutputStream fos = null;
		ByteArrayInputStream fis = null;
		OutputStream gzipOS = null;
		try{
			if (fileBytes != null) {
				fis= new ByteArrayInputStream(fileBytes);
				attachment.setFileName(fileName);
				attachment.setFileDesc("File uploded");
				populateAttachmentDetails(attachment);
				attachment.setFilePath(attachment.getFilePath()+".gz");
				fos = new FileOutputStream(getActualFilePath(attachment));
			
				//fos.write(fileBytes);
				//#25378 - danial
				gzipOS =new GZIPOutputStream(fos){{def.setLevel(Deflater.BEST_COMPRESSION);}};
	            byte[] buffer = new byte[1024];
	            int len;
	            while((len=fis.read(buffer)) != -1){
	                gzipOS.write(buffer, 0, len);
	            }
			}
		}
		finally{
			if(fos != null && gzipOS!=null){
				gzipOS.close();
				fos.close();
				fis.close();
			}
		}
	}

	// Redmine 25396: remove duplication option view by adding new drop down field
		/** 
		 * get faces scopes
		 * 
		 * @return FacesScopes
		 */
		private FacesScopes getFacesScopes() {
			FacesScopes facesScopes = new FacesScopes();
			return facesScopes;
		}
		// end of 25396
		
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
		boolean isFromWebService = (null!=FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("hostUrl"));
		PmUser createdUser = isFromWebService ? retrivePmUser(new Long(1)): new FacesScopes().getUserSessionBean();

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
				|| scAttachment instanceof ScFinancialEvaluationReportAttachment
				|| scAttachment instanceof ScQtProposalReportAttachment) {

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
		File file = null;
		if (scAttachment != null
				&& StringUtils.isNotBlank(scAttachment.getFileName())) {
			try {
				String fileName = scAttachment.getFileName();
				if(StringUtils.isNotBlank(scAttachment.getTempFileName())) {
					String extension = FilenameUtils.getExtension(scAttachment.getFileName());
					fileName = scAttachment.getTempFileName() + "." + extension;
				}
				file = downloadFile(getActualFilePath(scAttachment),
						fileName);
			} catch (FileNotFoundException exception) {
				// Separate the file name from absolute path
				String splitMsg[] = StringUtils.split(exception.getMessage(),
						SourcingConstants.BACK_SLASH);
				int size = splitMsg.length;
				String filePath = Arrays.toString(splitMsg);
				if (size > 1) {
					filePath = splitMsg[size - 1];
				}
				throw new EpException(MessageConst.ERROR_760411,
						new String[] { StringUtils.replace(filePath,
								SourcingConstants.FILE_EXCEPTION_MSG, " ") });
						
			}
		} else {
			throw new EpException(MessageConst.ERROR_760412);
		}
		return file;
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
		
		log.info(" filePath  :"+filePath);
		log.info(" fileName  :"+fileName);

		if (filePath != null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());

			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			boolean zipped=false;
			
			//#25378 - danial
			if(filePath.endsWith(".gz")){
				zipped=true;
			}
			downloadFileByInputStream(input, fileName,zipped);
		}
         //input has been closed in the called method bcoz here in catch they alternative 
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
	protected void downloadFileByInputStream(InputStream input, String fileName,boolean zipped)
			throws IOException {
		log.info("Inside downloadFile(..)");
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		OutputStream out = null;
		//#25378 - danial
		GZIPInputStream gis =null;
		
		try{
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			// response.setContentType("application/vnd.ms-excel");
			// response.setHeader("Content-Transfer-Encoding", "binary");
			// to fix firefox issue of space
			String finalFileName = fileName.replaceAll("\\s+",
					QTConstants.REPLACE_CHAR);
			log.info("finalFileName: " + finalFileName);
			response.setHeader("Content-Disposition", "attachment; filename=\""+ finalFileName + "\";");
//			response.setHeader("Content-Disposition", "attachment; filename=" + finalFileName);
//			response.setContentType("application/octet-stream");
			response.setContentType(getMimeType(finalFileName));
			response.flushBuffer();

			out = response.getOutputStream();
			
			byte[] buffer = new byte[4096];
			int bytesRead;
			
			//#25378 - danial
			if(zipped){
				gis= new GZIPInputStream(input);
				while ((bytesRead = gis.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					buffer = new byte[4096];
				}
			}else{
				while ((bytesRead = input.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					buffer = new byte[4096];
				}
			}
			
			
			out.flush();
			FacesContext.getCurrentInstance().responseComplete();
		}
		finally{
			if(gis!=null){
				gis.close();
			}
			if(input!= null){
				input.close();
			}
			if(out != null){
				out.close();
			}
		}
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
			downloadAllFilesAsZipFile(zos,scAttachments);
			
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
		}finally{
			wordMLPackage = null;
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
	//Bug #31583 - start
	public void setUserDetails(BaseEntity baseObject) {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		PmUser user = new PmUser();
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(new FacesScopes().getUserScreenName());
		UserResDTO resDTO = userSO.retrieveUserByLoginId(userReqDTO);
		if (resDTO.getPmUser() == null) {
		  user = new FacesScopes().getUserSessionBean();
		}else{
		  user = resDTO.getPmUser();
		}
		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(user.getUserId());
			baseObject
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(user.getUserId());
		//Long userId = Long.valueOf(String.valueOf(MDC.get(LogKey.USER_ID)));
	}
	//Bug #31583 - end



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
		//28834 start
		else if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISEBP)) {
			msgCode = MessageConst.INFO_700091;
		}
		//28834 ends
		return msgCode;
	}

	/**
	 * To retrieve UserName.
	 * 
	 * @param userId
	 * @return String userName
	 */
	public String retriveUserName(Long userId) {
		String userName = "";
		PmUser user = retrivePmUser(userId);
		if (user != null) {
			userName = user.getUserName();
		}

		return userName;
	}

	public PmUser retrivePmUser(Long userId) {
		PmUser user = null;
		try {
			UserReqDTO reqDTo = new UserReqDTO();
			reqDTo.setUserId(userId);
			UserResDTO resDTO = getUserSOGlobal().retrieveUserByUserId(reqDTo);
			if (resDTO.isSuccessful() == true) {
				user = resDTO.getPmUser();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	protected IUserSO getUserSOGlobal() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	}

	protected IOrgProfileSO getOrgProfileSOGlobal() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}

	protected IParameterSO getParameterSOGlobal() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	protected IQTDetailSO getQTDetailSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
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
					if (compareUserName(scRemark.getCreatedUser().getLoginId()) &&
							!isDiffWithPrevWorkFlowStatus(workFlowStatus, scRemark)) {
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
				if(null!=workFlowStatus && !workFlowStatus.isBrandNew()){
				remark.setScWorkFlowStatus(workFlowStatus);
				}

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
		ByteArrayOutputStream tmpOut = null;
		FileInputStream in = null;
		try{
			if (scAttachment != null) {
				File file = new File(getActualFilePath(scAttachment));
				tmpOut = new ByteArrayOutputStream();
				in = new FileInputStream(file);
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
		}finally{
			//TODO baffled to close tmpOut
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
	 * @param isDirectDownload
	 * @return File instance
	 * @throws IOException
	 */
	protected File generatePdfFromOracle(String reportName,
			Map<String, String> paramMap, ScAttachment attachment,
			boolean isDirectDownload) throws IOException {
	/*	EpPropertiesUtils propertiesUtils = EpPropertiesUtils.getInstance();
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
			reportUrl.append(URLEncoder.encode(params.getValue(), UTF));
		}
		reportUrl.append(excNextUrl);*/
		String reportUrl= generateReportURL(reportName,paramMap);
		log.info("reportUrl: " + reportUrl);

		if (log.isDebugEnabled()) {
			log.debug("reportUrl: " + reportUrl);
		}
		OutputStream out = null;
		boolean isFromWebService = (null!=FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("hostUrl"));
		HttpServletResponse res = null;
		if(!isFromWebService){
			PortletResponse portletResponse = (PortletResponse) FacesContext
					.getCurrentInstance().getExternalContext().getResponse();
			 res = com.liferay.portal.util.PortalUtil
					.getHttpServletResponse(portletResponse);
		}else{
			res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		}
		
		InputStream input = null;
		FileOutputStream fos = null;
		try{
			if (!isDirectDownload) {
				res.setHeader("Content-Disposition", "attachment; filename="
						+ attachment.getFileName());
				res.setHeader("Content-Transfer-Encoding", "binary");
				res.setContentType("application/octet-stream");
				res.flushBuffer();
				out = res.getOutputStream();
			}

			File localFile = new File(getActualFilePath(attachment));

			if (reportUrl.toString() != null) {
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
				fos = new FileOutputStream(localFile);
				fos.write(bytes);
				fos.flush();
			}
			if (!isDirectDownload) {
				out.flush();
				
			}
			return localFile;
		}finally{
			if(fos != null){
				fos.close();
			}
			if(input != null){
				input.close();
			}
			if(out != null){
				out.close();
			}
		}
		
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
					int index=	list.get(i).getIndex();
					index=index-markDeletedList.size();
					log.debug("DeletedIndex"+index);
					markDeletedList.add(list.get(index));

						list.remove(index);
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
	//20190416@asyarfhaziq_Enhancement #8197 - Start
		/**
		 * Validate Mix Category code.
		 * 
		 * @param pmCategoryL3
		 *            the category l3
		 * @param categoryList
		 *            the category list
		 */
		public void validateMixCategoryCode(PmCategoryL3 pmCategoryL3,
				List<ScQtCategory> categoryList) {
			boolean isDuplicate = false;
			for (int i = 0; i < categoryList.size(); i++) {
				ScQtCategory scQtCategory = categoryList.get(i);
				if (("99".equals(pmCategoryL3.getCategoryL3Code()) && !"99"
						.equals(scQtCategory.getPmCategoryL3().getCategoryL3Code()))
						|| (!"99".equals(pmCategoryL3.getCategoryL3Code()) && "99"
								.equals(scQtCategory.getPmCategoryL3()
										.getCategoryL3Code()))) {
					isDuplicate = true;
					break;
				}
			}
			if (isDuplicate) {
				throw new EpException(MessageConst.ERROR_760482);
			}
		}
		//20190416@asyarfhaziq_Enhancement #8197 - End
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
	 * Gets the request types.
	 *
	 * @param paramaterTypes the paramater types
	 * @return the request types
	 */
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
	public boolean isMultipleProposalCategory(ScQt scQt,
			List<ScQtCategory> scQtCategories) {
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtdetail(scQt);
			reqDTO.setCategoryList(scQtCategories);
			return qtDetailSO.isMultipleProposalCategory(reqDTO);

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

	/**
	 * Retrive mof account by supplier.
	 *
	 * @param smSupplier the sm supplier
	 * @return the sm mof account
	 */
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
		String contractNo = "...........................";
		String supplierName = "...........................";
		String sourcingTitle = "...........................";
		String bondAmount = "..............................";
		String expiryPeriod = "..........................";
		String bondAmountWord = "..................................................";
		int type = 0;
		
		if (scLoa != null) {
			type = 1;
			
            if(scLoa.getScLoiLoa() != null){
            	if(StringUtils.isNotEmpty(scLoa.getScLoiLoa().getSupplierName())){
            		supplierName =  scLoa.getScLoiLoa().getSupplierName() ;
            	}
            	if(StringUtils.isNotEmpty(scLoa.getScLoiLoa().getTitle())){
            		sourcingTitle =  scLoa.getScLoiLoa().getTitle() ;
            	}
            }
            if(scLoa.getBondAmt() != null){
            	bondAmount = amountDF.format(scLoa.getBondAmt());
            	bondAmountWord = NumberWordUtil.convert(scLoa.getBondAmt().longValue());
            }
            if(scLoa.getBondResDueDate() != null){
            	expiryPeriod = dateFormat.format(scLoa.getBondResDueDate());
            }
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
	public ScLoa calcBondAmount(ScLoa scLoa, boolean isDirectLoa) {
		/*
		 * The formula for bond should be as follow LOA Amount/duration (in
		 * year) = x x is being checked against the rate either 2.5% or 5%, if X
		 * >=RM 500,000, the rate being used is 5%
		 * 
		 * In the sample below - the calculation should be 2,482,400/0.25 yr (
		 * since the duration is 3 month) - 9933600 Bond amount - 9933600 * 0.05
		 * = 496680
		 * 
		 * redmine Bug #11021 and Bug #11494 
		 * 
		 * If less than 2 years: 
		 * Bond calculation base on total contract value multiple by 2.5% or 5%
		 * 900,000 * 5% = 45,000
		 * -------------------------------------------------------------------
		 * If more than 2 years: 
		 * Bond calculation base on contract value per year, multiple
		 * by 2.5% or 5%. 900,000/30 bulan = 30,000
		 * 
		 * Setahun : 30,000*12 = 360,000
		 * 
		 * Nilai Bon : 360,000 * 2.5% = 9,000 
		 * 
		 */
		BigDecimal overallCtAmount = overallCtAmount(scLoa.getLoaAmt(),
				scLoa.getGstAmt());
		if(isDirectLoa/* && scLoa.isBrandNew()*/){
//			scLoa.setBondReq(isPerformanceBondRequired(scLoa.getLoaAmt()));
			scLoa.setBondReq(PerformanceBondUtil.isPerformanceBondRequired(overallCtAmount, null));
		}
		
		if (scLoa.isBondReq()) {
			if (scLoa.getLoaAmt() != null && scLoa.getContractDuration() != null) {
				BigDecimal contractPeriod = scLoa.getContractDuration();

				PerformanceBondUtil.calculatePerformanceBondAmt(overallCtAmount, contractPeriod, scLoa);
			}
		}else{
			scLoa.setBondAmt(null);
			scLoa.setBondPercentage(null);
		}
		return scLoa;
	}
	
	//Enhancement #24259 #24269	
	/**
	 * Calculate bond amount with SST.
	 * 
	 * @param scLoa
	 *            the sc loa
	 * @return the big decimal
	 */	
	public ScLoa calcBondAmountWithSST(ScLoa scLoa, boolean isDirectLoa) {
		BigDecimal overallCtAmount = overallCtAmountWithSST(scLoa.getLoaAmt(), scLoa.getSstAmt());
		
		if(isDirectLoa) {
			boolean setBondReq = true;
			if (null != scLoa.getQtType() 
					&& null != scLoa.getQtType().getParameterId() 
					&& scLoa.getQtType().getParameterCode().equals(ParameterConst.QUOTATION_TENDER_TYPE_ITEM_IN_PK7)  
					&& null != scLoa.getScQt() 
					&& null != scLoa.getScQt().getFulfilmentType()
					&& null != scLoa.getScQt().getFulfilmentType().getParameterId()
					&& scLoa.getScQt().getFulfilmentType().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_ONE_OFF)  
					&& null != scLoa.getContractType() 
					&& null != scLoa.getContractType().getParameterId()
					&& scLoa.getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				setBondReq = false;
			}
			
			if (null != scLoa.getQtType() 
					&& null != scLoa.getQtType().getParameterId() 
					&& scLoa.getQtType().getParameterCode().equals(ParameterConst.QUOTATION_TENDER_TYPE_ITEM_IN_PK7)  
					&& null != scLoa.getScQt() 
					&& null != scLoa.getScQt().getFulfilmentType()
					&& null != scLoa.getScQt().getFulfilmentType().getParameterId()
					&& (scLoa.getScQt().getFulfilmentType().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
					|| scLoa.getScQt().getFulfilmentType().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN))) {
				setBondReq = false;
			}
			
			if (setBondReq) {
				scLoa.setBondReq(PerformanceBondUtil.isPerformanceBondRequired(overallCtAmount, null));
			}
		}
		
		System.out.println("____ calcBondAmountWithSST "+scLoa.getScLoiLoa().getDocId()+": "+scLoa.getScLoiLoa().getDocId() +"overallAmount: "+overallCtAmount+" scLoa.isBondReq():"+scLoa.isBondReq());
		
		if (scLoa.isBondReq()) {
			if (scLoa.getLoaAmt() != null && scLoa.getContractDuration() != null) {
				System.out.println("____ calcBondAmountWithSST LoaAmt: "+scLoa.getLoaAmt()+" Contract Duration: "+scLoa.getContractDuration());
				BigDecimal contractPeriod = scLoa.getContractDuration();

				PerformanceBondUtil.calculatePerformanceBondAmt(overallCtAmount, contractPeriod, scLoa);
			}
		} else {
			scLoa.setBondAmt(null);
			scLoa.setBondPercentage(null);
		} 
		
		return scLoa;
	}		
	//end Enhancement #24259 #24269	

	/**
	 * Get the OverallContractAmount.
	 * @param offerPrice
	 * @param gstAmount
	 * @return the contractAmount.
	 */
	public BigDecimal overallCtAmount(BigDecimal offerPrice,
			BigDecimal gstAmount) {

		BigDecimal overallCtAmount = offerPrice;
		if (gstAmount != null) {
			overallCtAmount = overallCtAmount.add(gstAmount);
			overallCtAmount.setScale(2, BigDecimal.ROUND_UP);
		}

		return overallCtAmount;

	}

	//Enhancement #24259 #24269	
	/**
	 * Get the OverallContractAmount with SST.
	 * @param offerPrice
	 * @param sstAmount
	 * @return the contractAmount.
	 */
	public BigDecimal overallCtAmountWithSST(BigDecimal offerPrice, BigDecimal sstAmount) {
		BigDecimal overallCtAmount = offerPrice;
		if (sstAmount != null) {
			overallCtAmount = overallCtAmount.add(sstAmount);
			overallCtAmount.setScale(2, BigDecimal.ROUND_UP);
		}

		return overallCtAmount;
	}
	//end Enhancement #24259 #24269	
	
	/**
	 * Check ProposalValidity Days Reached or not.
	 * @param scQt
	 * @return boolean
	 */
	public boolean handleProposalValidityDays(ScQt scQt) {
		Date validityDate = null;
		if (null != scQt.getProposalValidityEndDate()) {
			log.info("scQt.getProposalValidityEndDate() not null"
					+ scQt.getProposalValidityEndDate());
			validityDate = scQt.getProposalValidityEndDate();
		}
		
		boolean isValidityDateNotReached = false;
		if (null != validityDate) {
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
	/*public boolean isPerformanceBondRequired(BigDecimal loaAmount) {
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
	}*/

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
		
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		return getPTJOrgProfileByUserId(userId);
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
	
	/**
	 * Gets the panel group items by circuler ver.
	 *
	 * @param circular the circular
	 * @return the panel group items by circuler ver
	 */
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
	
	public boolean isQTStatusEvaluation(Long currentStatusId) {//#25037
		return isQTStatusGreaterThanOrEquals(currentStatusId, QTStatusConst.QT_EVALUATION_PROPOSAL);
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
	 * Check if QT Status less than from status.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @param toStatusId
	 * 		the to status id
	 * @return	the indicator qt status less than from status.
	 */
	public boolean isQTStatusLessThan(Long currentStatusId, Long toStatusId) {
		List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		
		return orderedStatuses.indexOf(currentStatusId) < orderedStatuses.indexOf(toStatusId);
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
	public List<PmParameterDesc> getContractTypes(String procurementMode) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		// check logged in user allow central contract.
		boolean  isAllowCentralContract =  isAllowCentralContract();
		
		if(!isAllowCentralContract || procurementMode.equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), ParameterConst.CONTRACT_TYPE_CENTRAL));
		} 

		List<PmParameterDesc> contractTypes = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(
						ParameterTypeConst.CONTRACT_TYPE, searchBean);

		return contractTypes;
	}
	
	// add rule #27502 START
	public List<PmParameterDesc> getContractTypesForQt(String procurementMode,ScQt scqt) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		// check logged in user allow central contract for qt.
		boolean isAllowCentralContract =  isAllowCentralContract();
		boolean isRuleBpk = false;
		
		if (scqt != null){
			IRuleSO ruleSO = (IRuleSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			List<Object[]> bpkList = ruleSO.getBpkByOrgProfileIdAndRuleType(scqt.getOrgProfileId(), RuleTypeConst.PTJCO);
			
			if(bpkList != null){
				isRuleBpk = true;
			}
		}
	
		if(!isRuleBpk || !isAllowCentralContract || procurementMode.equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)){
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_NOT_EQUALS.value(), ParameterConst.CONTRACT_TYPE_CENTRAL));
			} 
		
		List<PmParameterDesc> contractTypes = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(
						ParameterTypeConst.CONTRACT_TYPE, searchBean);
		
		return contractTypes;
	}
	// add rule #27502 END
	
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
	 * Gets the role type dropdown values.
	 * 
	 * @return the role type dropdown values
	 */
	public List<PmParameterDesc> getRoleTypeDropdownValuesBasedOnPTJ(boolean isChairPersonReq,ScQt scqt,PmUser user) {//#25037
		return getRoleTypeDropdownValuesBasedOnPTJ( isChairPersonReq, scqt, user, null);
	}
	public List<PmParameterDesc> getRoleTypeDropdownValuesBasedOnPTJ(
			boolean isChairPersonReq,ScQt scqt,PmUser user, String[] filteredRoles) {//#25037
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
		//#25037 START
		List<PmParameterDesc> list = retrieveParametersBySearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		list = list == null ? new ArrayList<PmParameterDesc>() : list;

		if(filteredRoles!=null){
			for(PmParameterDesc role: list){
				if(Arrays.asList(filteredRoles).contains(role.getPmParameter().getParameterCode())){
					role.setSelectable(false);
				}
			}
		}
		return list;
		//#25037 END

	}
	
	private boolean orgProfileCheckExist(ScQt scqt,PmUser user, boolean isptj){
		PmOrgProfile orgProfile=null;
		try{
			orgProfile=retriveMinistryByOrgprofile(scqt.getPmOrgProfile().getOrgProfileId());
		if(null!=user.getPmUserOrgs() && null!=orgProfile){
			//orgProfile.setOrgProfileId(273481L);
			log.info("===qt PtjId==="+orgProfile.getOrgProfileId());
		for(PmUserOrg userorg:user.getPmUserOrgs()){
			log.info("===user org record status==="+userorg.isRecordStatus());
			if(userorg.isRecordStatus() && null!=userorg.getPmOrgProfile()){
				PmOrgProfile localorgProfile=retriveMinistryByOrgprofile(userorg.getPmOrgProfile().getOrgProfileId());
				if(null!=localorgProfile && localorgProfile.getOrgProfileId().equals(orgProfile.getOrgProfileId())){
				isptj=true;
				break;
				}
			}
			
		}
		}
		}finally{
			
		}
		return isptj;
	}
	
	private PmOrgProfile retriveMinistryByOrgprofile(Long orgProfileId){
		IOrgProfileSO	orgProfileSO=null;
		PmOrgProfile orgProfile=null;
		try{
			
			orgProfileSO= BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			if(null!=orgProfileId){
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(orgProfileId);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveMinistryByOrgProfileId(reqDTO);
			if (resDTO.getPmOrgProfile() != null) {
			 orgProfile=resDTO.getPmOrgProfile();
			}
		}
		}finally{
			orgProfileSO=null;
		}
		return orgProfile;
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
			if(null!=scQt.getQtValidityPeriod()){//25635-proposal validity days null
			proposalValidityEndDate = DateUtils.addDays(clsDate,
					scQt.getQtValidityPeriod());
			}//25635-proposal validity days null
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
	
	protected ScAddress createOrUpdateScAddress(ScAddress scAddress, SmAddress smAddress, 
							SmPersonnel smPersonnel, SmCompanyBasic smCompanyBasic) {
		
		if (smAddress != null) {
			if (scAddress == null) {
				scAddress = new ScAddress();
			}
			scAddress.setAddress1(smAddress.getAddress1());
			scAddress.setAddress2(smAddress.getAddress2());
			scAddress.setAddress3(smAddress.getAddress3());
			//scAddress.setAddress4(smAddress.getAddress4());
			scAddress.setPostcode(smAddress.getPostcode());
			scAddress.setPmCountry(smAddress.getPmCountry());
			scAddress.setPmState(smAddress.getPmState());
			scAddress.setPmDivision(smAddress.getPmDivision());
			scAddress.setPmDistrict(smAddress.getPmDistrict());
			scAddress.setPmCity(smAddress.getPmCity());
			scAddress.setMapLatitude(smAddress.getMapLatitude());
			scAddress.setMapLongitude(smAddress.getMapLongitude());
			
			if (smPersonnel == null) {
				// to reset all contact person details as null.
				smPersonnel = new SmPersonnel();
			} 
			scAddress.setContactEmail(smPersonnel.getEmail());
			scAddress.setContactFaxArea(smPersonnel.getFaxArea());
			scAddress.setContactFaxCountry(smPersonnel.getFaxCountry());
			scAddress.setContactFaxNo(smPersonnel.getFaxNo());
			scAddress.setContactMobileArea(smPersonnel.getMobileArea());
			scAddress.setContactMobileCountry(smPersonnel.getMobileCountry());
			scAddress.setContactMobileNo(smPersonnel.getMobileNo());
			scAddress.setContactPerson(smPersonnel.getName());
			scAddress.setContactPhoneArea(smPersonnel.getPhoneArea());
			scAddress.setContactPhoneCountry(smPersonnel.getPhoneCountry());
			scAddress.setContactPhoneNo(smPersonnel.getPhoneNo());
			//30449 - start
			if(smPersonnel.getPmUser() != null) {
				scAddress.setPmContactUserID(smPersonnel.getPmUser());
			}
			//30449 -end			
			if (smCompanyBasic == null) {
				// to reset all company contact details as null.
				smCompanyBasic = new SmCompanyBasic();
			} 
			scAddress.setFaxArea(smCompanyBasic.getFaxArea());
			scAddress.setFaxCountry(smCompanyBasic.getFaxCountry());
			scAddress.setFaxNo(smCompanyBasic.getFaxNo());
			scAddress.setPhoneArea(smCompanyBasic.getPhoneArea());
			scAddress.setPhoneCountry(smCompanyBasic.getPhoneCountry());
			scAddress.setPhoneNo(smCompanyBasic.getPhoneNo());
		}
		
		return scAddress;
	}
	
	/**
	 * if exceeds 12 PM,next day else today 
	 *  
	 * @return Date
	 */
	public Date handlePublishDate() {
		
		int hour = DateUtils.getCurrentHours();
		Date today = DateUtils.getCurrentDateWithoutTime();
		return hour >= 12 ? DateUtils.addDays(today, 1) : today;
	}
	
	/**
	 * Fetch PmParameter based on parameter Id
	 * 
	 * @param parameterId
	 * @return PmParameter
	 */
	public PmParameter retrieveParameterById(Long parameterId){
		
		PmParameter pmParameter = null;
		if (parameterId != null) {
			IParameterSO parameterSO = null;
			try {
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO.setParameterId(parameterId);
				parameterSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				ParameterResDTO parameterResDTO = parameterSO
						.retrieveParameterById(parameterReqDTO);
				if(parameterResDTO != null && parameterResDTO.isSuccessful()){
					pmParameter = parameterResDTO.getPmParameter();
				}
			} finally {
				parameterSO = null;
			}
		}

		return pmParameter;
	
	}
	
	public PmParameter retrieveParameterByTypeAndCode(String parameterType, String parameterCode) {
		PmParameter pmParameter = null;
		try {

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(parameterType);
			reqDTO.setParameterCode(parameterCode);
			ParameterResDTO resDTO = getParameterSOGlobal().retrieveParameterByTypeAndCode(reqDTO);
			if (resDTO != null && resDTO.isSuccessful()) {
				pmParameter = resDTO.getPmParameter();
			}
		} finally {
		}

		return pmParameter;
	}

	/**
	 * Validate deduction payment dtls.
	 * 
	 * @param List<ScQtDeduction>
	 *          	  the deductions
	 *          
	 */
	public void validateDeductionPaymentDtls(List<ScQtDeduction> deductions) {

		if (CollectionUtils.isNotEmpty(deductions)) {
			for (ScQtDeduction deduction : deductions) {
				PmParameter deductionType = deduction.getDeductionType();
				if (null != deductionType
						&& null == deductionType.getParameterId()) {
					throw new EpException(MessageConst.ERROR_740035);
				} else {
					if (CollectionUtils.isEmpty(deduction
							.getScQtDeductionClauses())) {
						throw new EpException(MessageConst.ERROR_760324,
								new String[] { getParameterDesc(
										deductionType.getParameterType(),
										deductionType.getParameterCode()) });
					}
					for(ScQtDeductionClause clause : deduction.getScQtDeductionClauses()) {
						if(StringUtils.isBlank(clause.getClauseDesc())) {
							throw new EpException(MessageConst.ERROR_760451,
									new String[] { getParameterDesc(
											deductionType.getParameterType(),
											deductionType.getParameterCode()) });
						}
					}
				}

				// allow multiple Fixed deduction And Others only
				if (deductions.size() > 1) {
					for (ScQtDeduction scQtDeduction : deductions) {
						if (!deduction.equals(scQtDeduction)) {
							if (!(deductionType.getParameterCode().equals(
									ParameterConst.DEDUCTION_TYPE_FIXED)
									|| deductionType
											.getParameterCode()
											.equals(ParameterConst.DEDUCTION_TYPE_OTHERS) || deductionType
											.getParameterCode()
											.equals(ParameterConst.DEDUCTION_TYPE_NON_FIXED))) {
								if (deduction.getDeductionType()
										.getParameterId() == scQtDeduction
										.getDeductionType().getParameterId()) {
									throw new EpException(
											MessageConst.ERROR_700027,
											new String[] { ResourceBundleUtils
													.getMessage(
															getParameterDesc(scQtDeduction
																	.getDeductionType()),
															ResourceBundleUtils.BUNDLE_COMMON_LABELS,
															new FacesScopes()
																	.getUserLocale()) });
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Check Is QT send Cancel request
	 *
	 * @param qt the qt
	 */
	public boolean checkQTCancelRuquested(ScQt scQt){
		boolean isQTCancelRequested = false;
		IScWorkFlowStatusSO workFlowStatusSO;
		try {
			workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_QT_CANCELLATION);
			reqDTO.setDocId(scQt.getQtId());
			
			ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
			if(currentWorkFlowStatus != null && currentWorkFlowStatus.getPmStatus() != null &&
					currentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.QC_PENDING_CANCELATON)) {
				isQTCancelRequested = true;
			}
		} finally {
			workFlowStatusSO = null;
		}
		return isQTCancelRequested;
	}
	
	/**
	 * Gets the mof no by supplier id.
	 *
	 * @param supplierId the supplier id
	 * @return the mof no by supplier id
	 */
	public String getMofNoBySupplierId(Long supplierId) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofAccountSO.class);
		MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
		accountReqDTO.setSupplierId(supplierId);
		MofAccountResDTO mofAccountResDTO = mofAccountSO
				.retrieveMofNoBySupplierId(accountReqDTO);
		if (mofAccountResDTO.getMofAccount() != null) {
			return mofAccountResDTO.getMofAccount().getMofNo();
		}
		return null;
	}
	
	/**
	 * To check is performance bond required
	 * 
	 * @return boolean isPerformanceBondRequired
	 */
	public boolean isPerformanceBondRequired(BigDecimal loaAmount, List<ScQtCategory> qtCategories) {
		return PerformanceBondUtil.isPerformanceBondRequired(loaAmount, qtCategories);
	}
	
	/**
	 * Get member role based parameter type 'FCR' and code
	 * 
	 * @param pmParameter
	 * @return memberRole
	 */
	public String getFCMemberRole(PmParameter pmParameter) {

		String memberRole = null;
		if (pmParameter != null) {
				memberRole = getParameterDesc(
						ParameterTypeConst.FINALIZATION_COMMITTEE_ROLE,
						pmParameter.getParameterCode());
			
		}
		return memberRole;
	}
	
	/**
	 * Gets the procurement type tool tip.
	 *
	 * @return the procurement type tool tip
	 */
	public String getProcurementTypeToolTip(){
		// MaxProcurementAmt
		BigDecimal maxProcurementAmt = getPRThresholdAmount();
		DecimalFormat amountDF = new DecimalFormat(new CommonUtils()
				.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

		String message = ResourceBundleUtils.getMessage(
				"lbl.sc.procurementtype.tooltip", new String[] {amountDF.format(maxProcurementAmt)}, 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		return message;
	}
	
	/**
	 * Set PTJ Org Profile of user for committee member.
	 * 
	 * @param scMember
	 */
	public void setCommitteeMemberOrgProfile(ScCommitteeMember scMember) {
		/*
		 * Enhancement #21689
		 * IF BPK DONT SET ORG_PROFILE (PTJ)
		 */
		if(scMember.getPmUser()!=null && 
				scMember.getPmUser().getOrgTypeCode()!=null){
			//20210716@asyrafhaziq - 30421 -start
			String orgTypeCode = scMember.getPmUser().getOrgTypeCode().getParameterCode();
			if(orgTypeCode.equalsIgnoreCase(ParameterConst.ORG_TYPE_BPK)){
				scMember.setPmOrgProfile(getBKPOrgProfileByUserId(scMember.getPmUser()
						.getUserId()));
			} else if (orgTypeCode.equalsIgnoreCase(ParameterConst.ORG_TYPE_OTHERS)){
				scMember.setPmOrgProfile(getOTHOrgProfileByUserId(scMember.getPmUser()
						.getUserId()));
			}
			//20210716@asyrafhaziq - 30421 -end
			else {
				scMember.setPmOrgProfile(getPTJOrgProfileByUserId(scMember.getPmUser()
						.getUserId()));
			}			
		}
	}
	
	/*
	 * Enhancement #21689
	 */
	private PmOrgProfile getBKPOrgProfileByUserId(Long userId) {

		PmOrgProfile pmOrgProfile = null;
		if (userId != null) {
			IOrgProfileSO orgProfileSO = null;
			try {
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);

				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setOrgType(OrganizationType.BAHAGIAN_PERBENDAHARAAN_KERAJAAN.value());
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
	//20210716@asyrafhaziq - 30421 -start
	private PmOrgProfile getOTHOrgProfileByUserId(Long userId) {
		PmOrgProfile pmOrgProfile = null;
		
		if (userId != null) {
			IOrgProfileSO orgProfileSO = null;
			
			try {
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);

				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setOrgType(OrganizationType.OTHERS.value());
				
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
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
	//20210716@asyrafhaziq - 30421 -end
	private PmOrgProfile getPTJOrgProfileByUserId(Long userId) {

		PmOrgProfile pmOrgProfile = null;
		if (userId != null) {
			IOrgProfileSO orgProfileSO = null;
			try {
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);

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
	 * Populate values into Representative Type drop down in Committee Members table
	 * @return  an array of representative type(s)
	 */
	public SelectItem[] getRepresentativeTypeDropdownValues(String committeeType){
		SelectItem[] items = null;
		
		if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B.equalsIgnoreCase(committeeType) 
				|| ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equalsIgnoreCase(committeeType) ) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_QC);
		}
		  else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PPR);
		}
		  else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBB);
		
		} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBA);
		
		}
		else if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_FPBH);
		
		} else if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_FPBK);
		}
		return items;
	}
	
	/**
	 * Set Committeemember data.
	 * 
	 * @param deleteScMember
	 *  @param currentScMember
	 */
	public ScCommitteeMember setCommitteeMemberData(ScCommitteeMember deletedCommitteeMember,ScCommitteeMember currentCommitteeMember) {
		deletedCommitteeMember.setDeleted(false);
		deletedCommitteeMember.setRepresentativeType(
				currentCommitteeMember.getRepresentativeType());
		deletedCommitteeMember.setMemberRole(
				currentCommitteeMember.getMemberRole());
		deletedCommitteeMember.setMemberRoleId(
				currentCommitteeMember.getMemberRoleId());
		deletedCommitteeMember.setMemberName(currentCommitteeMember.getMemberName());
		deletedCommitteeMember.setDesignation(currentCommitteeMember.getDesignation());
		deletedCommitteeMember.setEmail(currentCommitteeMember.getEmail());
		deletedCommitteeMember.setGrade(currentCommitteeMember.getGrade());
		deletedCommitteeMember.setPNP(currentCommitteeMember.isPNP());
		deletedCommitteeMember.setPmUser(currentCommitteeMember.getPmUser());
		deletedCommitteeMember.setMemberRole(currentCommitteeMember.getMemberRole());
		deletedCommitteeMember.setAccessRight(currentCommitteeMember.getAccessRight());
		deletedCommitteeMember.setScCommittee(currentCommitteeMember.getScCommittee());
		if (deletedCommitteeMember.getMemberRole() != null) {
			deletedCommitteeMember.setMemberRoleId(
					deletedCommitteeMember.getMemberRole().getParameterId());	
		}
		if (deletedCommitteeMember.getRepresentativeType() != null) {
			deletedCommitteeMember.setRepresentativeTypeId(
					deletedCommitteeMember.getRepresentativeType().getParameterId());
		}
		
		return deletedCommitteeMember;
	}
	
	/**
	 * Get PmPtjSpecialApproval based on ptj orgprofile id
	 * 
	 * @param orgProfileId
	 * @return PmPtjSpecialApproval
	 */
	public PmPtjSpecialApproval getPtjSpecialApprovalByOrgProfileId(
			Long orgProfileId) {

		PmPtjSpecialApproval ptjSpecApproval = null;
		if (orgProfileId != null) {
			IPtjSpecialApprovalSO ptjSpecialApprovalSO = null;
			try {
				ptjSpecialApprovalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IPtjSpecialApprovalSO.class);

				PtjSpecialApprovalReqDTO reqDTO = new PtjSpecialApprovalReqDTO();
				reqDTO.setOrgProfileId(orgProfileId);
				//reqDTO.setOrgProfileId(238501L);
				PtjSpecialApprovalResDTO resDTO = ptjSpecialApprovalSO
						.isPtjSpecialApproval(reqDTO);
				if (resDTO.isValid()) {
					ptjSpecApproval = resDTO.getPtjSpecialApproval();
				}

			} finally {
				ptjSpecialApprovalSO = null;
			}
		}

		return ptjSpecApproval;
	}
	
	/**
	 * @param orgProfileId
	 * @return true if addressType present
	 */
	public boolean isPtjAddressTypePresent(Long orgProfileId) {

		boolean isPtjAddressTypePresent = false;
		if (orgProfileId != null) {
			IOrgProfileSO orgProfile = null;
			try {
				orgProfile = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO orgResDto = orgProfile
						.retrieveOrgStateCodeByOrgProfileId(orgProfileId);
				if (orgResDto.getStateStatusId() != null) {
					isPtjAddressTypePresent = true;
				}
			} catch(Exception e){
				//14648-No need to throw exception at time of page loading.
				isPtjAddressTypePresent = false;
			}
			
			finally {
				orgProfile = null;
			}
		}
		return isPtjAddressTypePresent;
	}
	
	/**
	 * Checks if is supplier.
	 *
	 * @return true, if is supplier
	 */
	public boolean isSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		if(facesScopes.hasRoleUser(RoleCode.GOVT_SELLER.value()) // supplier and govt seller
				|| facesScopes.hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())
				|| facesScopes.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())
				|| facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				|| facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())){

			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Enable drop down for ProcumentMethod if true
	 * 
	 * @param scQt
	 * @return isProcumentMethodDropdownReq
	 */
	protected boolean checkForProcumentMethodDropdown(ScQt scQt) {

		boolean isProcumentMethodDropdownReq = false;
		BigDecimal amount = scQt.getEstimatedAmount() != null ? scQt
				.getEstimatedAmount() : scQt.getTmpExpectedIntendedPrice();
		PmPtjSpecialApproval ptjSpecialApproval = scQt.getPtjSpecialApproval();
		if (!scQt.isPanel() && 
				!isQuotationThresHold(amount) &&
				BooleanUtils.isTrue(scQt.getObtainSpecialApproval()) && 
				ptjSpecialApproval != null &&
				ptjSpecialApproval.getProcureAmt() != null &&
				amount != null &&
				amount.compareTo(ptjSpecialApproval.getProcureAmt()) <= 0) {
			isProcumentMethodDropdownReq = true;
		}
		return isProcumentMethodDropdownReq;
	}
	
	/**
	 * Get list of statuses which are not recommended for Spec Maintenance
	 * 
	 * @return nonRecommendedStatuses
	 */
	protected List<Long> nonRecommendedStatusesForSpecMaint(){
		
		List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		orderedStatuses = orderedStatuses.subList(
						  orderedStatuses.indexOf(QTStatusConst.QT_PENDING_QT_SUBMISSION),
						  orderedStatuses
								.indexOf(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) + 1);
		List<Long> nonRecommendedStatuses = new ArrayList<Long>(orderedStatuses.size());
		nonRecommendedStatuses.addAll(orderedStatuses);
		
		return nonRecommendedStatuses;
	}
	
	/**
	 * Get file name as doc title with actual file type.
	 * 
	 * @param increment
	 * @param docTitle
	 * @param filePath
	 * @return fileName
	 */
	protected String getDocTitleAsFileName(int increment, String docTitle, String filePath) {

		String fileName = null;
		if (filePath != null) {
			StringBuilder builder = new StringBuilder();
			String number = (String) (increment <= 9 ? "0" + (increment)
					: Integer.toString(increment));
			builder.append(number).append("_").append(docTitle);
			String[] splitFile = filePath.split("\\.");
			if (splitFile.length > 0) {
				builder.append(".").append(splitFile[1]);
			}
			fileName = builder.toString();
		}
		return fileName;
	}
	
	/**
	 * Add defined message to FacesMessage based on msgcode from
	 * message-common.properties
	 * 
	 * @param fc
	 * @param msgCode
	 */
	protected void addFacesMessage(FacesContext fc, String msgCode, Object[] params) {

		fc.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						MessageUtils.getMessage(msgCode, params,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())
								+ " (" + msgCode + ")"));
	}
	
	
	/**
	 * checkKertasTaklimat Uploaded report
	 * 
	 * 
	 * @param finalizationReports
	 * 
	 */
	protected void checkKertasTaklimatReports(List<ScQtFinalizationReport> finalizationReports){
		if(CollectionUtils.isNotEmpty(finalizationReports)){
			int count=0;
			for(ScQtFinalizationReport finalizationReport:finalizationReports){
				if(null!=finalizationReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocDefinitionKey())){
				++count;	
				}
				
			}
			log.info("checkKertasTaklimatReports count"+count);
			if(count==1){
				log.info("enter count1"+count);
				for(ScQtFinalizationReport finalizationReport:finalizationReports){
					if(null!=finalizationReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocDefinitionKey())){
						finalizationReport.setKertasTaklimatUpload(Boolean.TRUE);	
					}
					
				}	
			}else if(count>1){
				log.info("enter countGreaterThan1"+count);
				 String SPACE = " ";
				  String reportTitle = ResourceBundleUtils.getMessage("lbl.sc.kertas.taklimat", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, SourcingConstants.MY_LOCALE) 
				 			+ SPACE + "(" + (count-1) + ")";
				  log.info("reportTitle"+reportTitle);
				for(ScQtFinalizationReport finalizationReport:finalizationReports){
					if(null!=finalizationReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocDefinitionKey()) && reportTitle.equalsIgnoreCase(finalizationReport.getTitle()) ){
						finalizationReport.setKertasTaklimatUpload(Boolean.TRUE);	
						break;
					}
					
				}	
			}
		}
		
	}
	
	/**
	 * this method used to delete deliveryAddress when zonedetailschange.
	 * 
	 * 
	 * 
	 * 
	 */
	protected void deleteDeliveryAdreesZoneDetails(ScQt scQt){
		IQTSpecSO specSO = null;
		try{
			 specSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
			 SpecificationReqDTO reqDTO=new SpecificationReqDTO();
			 reqDTO.setScQt(scQt);
			 specSO.removeZonalDeliveryDetails(reqDTO);
		}finally{
			specSO=null;
		}
	}
	
	/**
	 * Sort frequent schedule list.
	 *
	 * @param scQtScheduleList the sc qt schedule list
	 */
	protected void sortFrequentScheduleList(List<ScQtSchedule> scQtScheduleList) {
		Comparator<ScQtSchedule> comparator = new Comparator<ScQtSchedule>() {
			@Override
			public int compare(ScQtSchedule o1, ScQtSchedule o2) {
				return new CompareToBuilder().append(o1.getScheduleNo(),
						o2.getScheduleNo()).toComparison();
			}
		};
		Collections.sort(scQtScheduleList, comparator);
	}
	
	/**
	 * Sort progressive payment list.
	 *
	 * @param progressivePaymentList the progressive payment list
	 */
	protected void sortProgressivePaymentList(
			List<ScQtProgressivePayment> progressivePaymentList) {
		Comparator<ScQtProgressivePayment> comparator = new Comparator<ScQtProgressivePayment>() {
			@Override
			public int compare(ScQtProgressivePayment o1,
					ScQtProgressivePayment o2) {
				return new CompareToBuilder().append(o1.getPhaseNo(),
						o2.getPhaseNo()).toComparison();
			}
		};
		Collections.sort(progressivePaymentList, comparator);
	}
	
	private String getMimeType(String fileName) {
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
		
		String mimeType = (String)CommonConstants.getFileExtMap().get(fileExt);
		
		return (mimeType != null) ? mimeType : "octet-stream";
	}
	
	protected <ATTACHMENT extends ScAttachment> void downloadAllFilesAsZipFile(ZipOutputStream zos,List<ATTACHMENT> scAttachments) throws IOException{
		//ZipOutputStream zos = new ZipOutputStream(os);
		for (ScAttachment scAttachment : scAttachments) {
			// Get the file
			FileInputStream fis = null;
			BufferedInputStream fif = null;
			try{
				if (null != scAttachment
						&& StringUtils.isNotBlank(scAttachment.getFilePath())) {
					File file = new File(getActualFilePath(scAttachment));

					if(StringUtils.isNotBlank(scAttachment.getTempFileName())) {
						String extension = FilenameUtils.getExtension(scAttachment.getFileName());
						zos.putNextEntry(new ZipEntry(scAttachment.getTempFileName() + "." + extension));
					} else {
						zos.putNextEntry(new ZipEntry(scAttachment.getFileName()));
					}
					
					try {
						fis = new FileInputStream(file);
					} catch (FileNotFoundException fnfe) {
						zos.write(("ERROR: Could not find file " + file
								.getName()).getBytes());
						continue;
					}
					 fif = new BufferedInputStream(fis);
					// Write the contents of the file
					int data = 0;
					while ((data = fif.read()) != -1) {
						zos.write(data);
					}
				}
			}
			finally{
				if(fis != null){
					fis.close();
				}
				if(fif != null){
					fif.close();
				}
				zos.closeEntry();
			}
		}
	}
	
	/**
	 * @param context
	 * @param scLoa
	 */
	public void constructSupplierSpecificationAndRemarks(VelocityContext context, ScLoa scLoa) {
		List<TplSpecMaster> masters = new ArrayList<TplSpecMaster>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(scLoa.getScLoiLoa().getDocId());
			reqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			reqDTO.setPrepareSpec(true);
			SpecificationResDTO resDto = specSO.retrieveSpecDetails(reqDTO);
			String dafaltValue = "T/B";
			if ("en".equals(new FacesScopes().getUserLocale().getLanguage())) {
				dafaltValue = "N/A";
			}
			List<ScQtCheckList> scQtCheckLists = resDto.getSpecResultBean().getResultList();
			
			// #26130 - Start
			
			/*if (CollectionUtils.isNotEmpty(scQtCheckLists)) {
				scQtCheckLists.addAll(resDto.getFinSpecResultBean().getResultList());
			} else {
				scQtCheckLists = resDto.getFinSpecResultBean().getResultList();
			}*/
			
			if (CollectionUtils.isNotEmpty(scQtCheckLists)) {
				for (ScQtCheckList scQtCheckList : scQtCheckLists) {
					List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<TplSpecQuestion>();
					TplSpecMaster master = null;
					master = scQtCheckList.getScQtDoc().getTplSpecMaster();
					// 26130 - Start.
					for (TplSpecQuestion question : master.getTplSpecQuestions()) {
						/*if (CollectionUtils.isNotEmpty(question.getTplSpecAnswers())) {
							Iterator<TplSpecAnswer> iterator =  question.getTplSpecAnswers().iterator();
							while (iterator.hasNext()) {
								TplSpecAnswer tplSpecAnswer = iterator.next();
								if (null != tplSpecAnswer && null != tplSpecAnswer.getTplSpecSuppAnswer()
										&& null != tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier()
										&& tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier().getSupplierId().longValue() == scLoa.getScLoiLoa().getSmSupplier().getSupplierId().longValue()) {
									String feedback = null;
									if (null != tplSpecAnswer.getAnswerNumeric()) {
										feedback = String.valueOf(tplSpecAnswer.getAnswerNumeric());
									} else if (null != tplSpecAnswer.getAnswerCompliance()) {
										if (tplSpecAnswer.getAnswerCompliance()) {
											feedback = "Yes";
										} else {
											feedback = "No";
										}
									} else if (StringUtils.isNotEmpty(tplSpecAnswer.getAnswerText())) {
										feedback = String.valueOf(tplSpecAnswer.getAnswerText());
									} 
									tplSpecAnswer.setFeedback(feedback);
								} else {
									iterator.remove();
								}
								
							}
						}*/
						// #26130 - Start.
						constructSpecData(scLoa, master, question);
						// #26130 - End.
						constructTplSpecQuestion(dafaltValue,
								newTplSpecQuestionsList, master, question);
					}
					// 26130 - End.
					master.setTplSpecQuestions(newTplSpecQuestionsList);
					masters.add(master);
				}
			}
			
			// #26130 - End
			
			context.put("isZonal", scLoa.getScLoiLoa().isZonal());
			context.put("serviceScope", dafaltValue);
			context.put("masters", masters);
			context.put("bean", this);
		} finally {
			specSO = null;
		}
	}
	
	// #26130 - Start
	/**
	 * @param scLoa
	 * @param master
	 * @param question
	 */
	private void constructSpecData(ScLoa scLoa, TplSpecMaster master,
			TplSpecQuestion question) {
		if (null != question.getClItem()) {
			List<String> specList = null;
			List<String> feedbackList = null;
			List<String> answerRemarkList = null;
			boolean addSpecList = false;
			
			for (TplSpecQuestion tplSpecQuestion : master.getTplSpecQuestions()) {
				if (tplSpecQuestion.getSpecQuestionId() == question.getSpecQuestionId()) {
					// #26130 - Start
					if (answerRemarkList == null) {
						answerRemarkList = new ArrayList<>();
					}
					answerRemarkList.add(tplSpecQuestion.getTplSpecAnswers().get(0).getAnswerRemark());
					// #26130 - End
					addSpecList = true;
					continue;
				}
				if (addSpecList) {
					if (tplSpecQuestion.getQuestionType().equals("S")) {
						if (specList == null) {
							specList = new ArrayList<>();
						}
						if (feedbackList == null) {
							feedbackList = new ArrayList<>();
						}
						if (answerRemarkList == null) {
							answerRemarkList = new ArrayList<>();
						}
						specList.add(tplSpecQuestion.getClSpecDetail());
						
						for (TplSpecAnswer tplSpecAnswer: tplSpecQuestion.getTplSpecAnswers()) {
							if (null != tplSpecAnswer && null != tplSpecAnswer.getTplSpecSuppAnswer()
									&& null != tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier()
									&& tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier().getSupplierId().longValue() == scLoa.getScLoiLoa().getSmSupplier().getSupplierId().longValue()) {
								String feedback = null;
								if (null != tplSpecAnswer.getAnswerNumeric()) {
									feedback = String.valueOf(tplSpecAnswer.getAnswerNumeric());
								} else if (null != tplSpecAnswer.getAnswerCompliance()) {
									if (tplSpecAnswer.getAnswerCompliance()) {
										feedback = "Yes";
									} else {
										feedback = "No";
									}
								} else if (StringUtils.isNotEmpty(tplSpecAnswer.getAnswerText())) {
									feedback = String.valueOf(tplSpecAnswer.getAnswerText());
								} 
								feedbackList.add(feedback);
								answerRemarkList.add(tplSpecAnswer.getAnswerRemark());
							} 
						}
					} else {
						break;
					}
				}
				
			}
			question.setSpecList(specList);
			// #26130 - Start
			if (CollectionUtils.isNotEmpty(specList)) {
				if (CollectionUtils.isNotEmpty(question.getTplSpecZoneQties())) {
					for (int i=0; i < question.getTplSpecZoneQties().size(); i++) {
						if (CollectionUtils.isNotEmpty(feedbackList)) {
							feedbackList.add(i, "&nbsp;");
						}
						if (CollectionUtils.isNotEmpty(answerRemarkList) && answerRemarkList.size() != (question.getTplSpecZoneQties().size() + specList.size())) {
							answerRemarkList.add(i, "&nbsp;");
						}
					}
				} else {
					if (CollectionUtils.isNotEmpty(feedbackList)) {
						feedbackList.add(0, "&nbsp;");
					}
					// #26130 - Start
					/*if (CollectionUtils.isNotEmpty(answerRemarkList)) {
						answerRemarkList.add(0, "&nbsp;");
					}*/
					// #26130 - End
				}
			}
			// #26130 - End
			question.setFeedbackList(feedbackList);
			question.setAnswerRemarkList(answerRemarkList);
		}
	}
	// #26130 - End

	// #26226 - Start
	/**
	 * @param context
	 * @param scLoa
	 */
	/**  
	 * CR Enhancement 26226  Nov 2019
	 */
	public void constructPRSupplierSpecificationAndRemarks(VelocityContext context, ScLoa scLoa) {
		List<TplSpecMaster> masters = new ArrayList<TplSpecMaster>();	
		IQuoteSO quoteSO;
		Integer days=null;
		Long zoneId =new Long(0l);
		boolean isSpecificationEnable=false;
		boolean isWipData=false; //#29006
		try {
			quoteSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
			String supplierName= scLoa.getScLoiLoa().getSupplierName();
			log.info("constructPRSupplierSpecificationAndRemarks supplierName    "+supplierName+ " quoteId :   "+scLoa.getScQuote().getQuoteId() );
			
			QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
		
			quoteReqDTO.setQuoteId(scLoa.getScQuote().getQuoteId());
			
			quoteReqDTO.setQuoteId(scLoa.getScQuote().getQuoteId());
			quoteReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			
			
			if (scLoa.getScLoiLoa().getSmSupplier() != null) {
				quoteReqDTO.setSupplierId(scLoa.getScLoiLoa().getSmSupplier().getSupplierId());
				quoteReqDTO.setSmSupplier(scLoa.getScLoiLoa().getSmSupplier());
			}
			//quoteReqDTO.setFetchSubEntity(true);
			QuoteResDTO quoteResDTO = quoteSO.getQuoteDtlForLOASupplier(quoteReqDTO);
			log.info("Supplier ID"+quoteReqDTO.getSupplierId());  
		
			List<ScLoiLoaItem> items = scLoa.getScLoiLoaItems();
			log.info("ScLoiLoaItem Item size  -->"+items.size());
			String dafaltValue = "T/B";
			if(masters.size() > 0)
			{	
				masters.clear();
				log.info("Master Record List Size I -->"+masters.size());
			}
			for (ScLoiLoaItem item :items)	
			{	
				Long  itemId =item.getScRequestItem().getTplSpecQuestion().getSpecQuestionId();
				
				Long loaid=scLoa.getLoaId();
				
				//	Long requestItemId=item.getScRequestItem().getRequestNote().getRequestNoteId();
				log.info("Loa id +"   +"+ItemId"+"    "+loaid+"  --->   " +itemId);
				
			
			if ("en".equals(new FacesScopes().getUserLocale().getLanguage())) {
				dafaltValue = "N/A";
			}
			
			if (CollectionUtils.isNotEmpty(quoteResDTO.getServiceAnswersTpl())) {
				TplSpecMaster master = null;
				List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<TplSpecQuestion>();
				
				for (TplSpecAnswer answer : quoteResDTO.getServiceAnswersTpl()) {
					//log.info("constructPRSupplierSpecificationAndRemarks I"+answer.getTplSpecQuestion().getClFulfilmentTypeCode().getParameterCode());
					if(answer.getTplSpecQuestion().getTplSpecMaster().getTplSpecZone()!=null)
					{
						//quoteResDTO.getServiceTpl().getTplSpecZone().getSpecZoneId()
					  zoneId =answer.getTplSpecQuestion().getTplSpecMaster().getTplSpecZone().getSpecZoneId();
					 log.info("Size of the Zone ID" +zoneId);
					}else{
						zoneId=0l;  //Long Object initialization
					}
					
					if(answer.getTplSpecQuestion().getClFulfilmentTypeCode() == null)
					{
						answer.getTplSpecQuestion().setClFulfilmentTypeCode(scLoa.getScLoiLoa().getFulfilmentTypeCode()); 
						
						
					}
				     if(days == null)
				     {
				    	 days=answer.getTplSpecQuestion().getClDeliveryTerm();
				     }else{
				    	 
				    	 if(!days.equals(answer.getTplSpecQuestion().getClDeliveryTerm()) && (answer.getTplSpecQuestion().getClDeliveryTerm()!=null)){
				    		 days=answer.getTplSpecQuestion().getClDeliveryTerm();
				    	 }
				     }
					//log.info("constructPRSupplierSpecificationAndRemarks I"+ answer.getTplSpecQuestion().getClFulfilmentTypeCode().getParameterCode());
					log.info("TplSpecAnswer for service:- "
							+ "|" + answer.getSpecAnswerId()
							+ "|" + answer.getAnswerText()
							+ "|" + answer.getTplSpecQuestion().getSpecQuestionId()
							+ "|" + answer.getTplSpecQuestion().getClItem()
							//+ "|" + SmCommonUtil.retrieveParameterDescByTypeAndCodeAndLang(answer.getTplSpecQuestion().getClFulfilmentTypeCode().getParameterCode(),new FacesScopes().getUserLocale().getLanguage())
							+ "|"+getParameterDescForReport(answer.getTplSpecQuestion().getClFulfilmentTypeCode())
							+ "|" + answer.getTplSpecQuestion().getClSpecDetail()
							//+ "|" + answer.getTplSpecQuestion().getClUom().getUomCode()
							+ "|" + answer.getTplSpecQuestion().getClUomFrequency()
							+ "|" + answer.getTplSpecQuestion().getClUomPerDay()
							+ "|" + answer.getTplSpecQuestion().getClUomPerMonth()
							+ "|" + answer.getTplSpecQuestion().getClQuantity()
							+ "|" + answer.getTplSpecQuestion().getClReviewRemark()
							+ "|" + answer.getRatePerUom() 
							+ "|" + answer.getAnswerRemark()
							+ "|" + answer.getTplSpecQuestion().getClDeliveryTerm()
							+ "|" + answer.getIsWip() //#29006
							//+ "|" + answer.getTplSpecZoneDtls().size()
							//+ "|" + answer.getTplSpecZoneQty()
							//+ "|" + answer.getTplSpecQuestion().getTplSpecMaster().getTplSpecZone().getSpecZoneId()
							);
					if(answer.getTplSpecZoneDtls()!=null)
					{
						log.info("Size of the Zone Dtls" +answer.getTplSpecZoneDtls().size());
						log.info("Size of the Zone Qty" +answer.getTplSpecZoneQty());
					}
					//log.info("TplSpecQuestion for Delivery Term:-------> "+answer.getTplSpecQuestion().getClDeliveryTerm());
					log.info("TplSpecQuestion for days:-------> "+days);
					if(answer.getAnswerRemark()!=null)
					{
						answer.getTplSpecQuestion().setAnswerRemarks(answer.getAnswerRemark());
					}
					else{
						answer.getTplSpecQuestion().setAnswerRemarks(dafaltValue);
					}
					
					isWipData = answer.getIsWip();//#29006
					
					if((days.equals(answer.getTplSpecQuestion().getClDeliveryTerm())) && (answer.getTplSpecQuestion().getClItem()!=null))
					{ 
						// #25025 - Start.
						List<String> specList = null;
						boolean addSpecList = false;
						for (TplSpecAnswer tplSpecAns : quoteResDTO.getServiceAnswersTpl()) {
							if (tplSpecAns.getTplSpecQuestion().getSpecQuestionId() == answer.getTplSpecQuestion().getSpecQuestionId()) {
								addSpecList = true;
								continue;
							}
							if (addSpecList) {
								if (tplSpecAns.getTplSpecQuestion().getQuestionType().equals("S")) {
									if (specList == null) {
										specList = new ArrayList<>();
									}
									specList.add(tplSpecAns.getTplSpecQuestion().getClSpecDetail());
								} else {
									break;
								}
							}
							
						}
						answer.getTplSpecQuestion().setSpecList(specList);
						// #25025 - End.
						
						/**  This code is for LOA with Single Item Details  **/
						
						if(items.size() == 1)
						{
							
							if(itemId.equals(answer.getTplSpecQuestion().getSpecQuestionId()) || (answer.getTplSpecQuestion().getParentSpecQuestion()!=null && itemId.equals(answer.getTplSpecQuestion().getParentSpecQuestion().getSpecQuestionId())))
							{
								master = answer.getTplSpecQuestion().getTplSpecMaster();
								if(master.getTplSpecZone()!=null)
								{
									
									for(int i=0; i<master.getTplSpecZone().getTplSpecZoneDtls().size();i++)
									{
										TplSpecZoneDtl tplspecZoneDtls =master.getTplSpecZone().getTplSpecZoneDtls().get(i);
										tplspecZoneDtls.getZoneName();
										log.info("Zone Name"+tplspecZoneDtls.getZoneName());
									}
								}
								constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master, answer.getTplSpecQuestion());
								// #26226 - Start
								populateSpecAnswers(answer);
								// #26226 - End
								master.setTplSpecQuestions(newTplSpecQuestionsList);
								isSpecificationEnable=true;
							}
							
						}else {
							/** This code is for  LOA with multiple Items Details **/
							if(days.equals(answer.getTplSpecQuestion().getClDeliveryTerm()))
							{
								master = answer.getTplSpecQuestion().getTplSpecMaster();
								if(master.getTplSpecZone()!=null)
								{
									
									for(int i=0; i<master.getTplSpecZone().getTplSpecZoneDtls().size();i++)
									{
										TplSpecZoneDtl tplspecZoneDtls =master.getTplSpecZone().getTplSpecZoneDtls().get(i);
										tplspecZoneDtls.getZoneName();
										log.info("Zone Name "+tplspecZoneDtls.getZoneName());
									}
								}
								constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master, answer.getTplSpecQuestion());
								// #26226 - Start
								populateSpecAnswers(answer);
								// #26226 - End
								master.setTplSpecQuestions(newTplSpecQuestionsList);
								isSpecificationEnable=true;
							}
							
						}
						
					}
					else if(isSpecificationEnable && answer.getTplSpecQuestion().getClSpecDetail()!=null)
					{
						/**  This code is for LOA with Single Item specification **/
						if(items.size() == 1)
						{
							if(itemId.equals(answer.getTplSpecQuestion().getSpecQuestionId()) || (answer.getTplSpecQuestion().getParentSpecQuestion()!=null && itemId.equals(answer.getTplSpecQuestion().getParentSpecQuestion().getSpecQuestionId())))
							{
			
							master = answer.getTplSpecQuestion().getTplSpecMaster();
							constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master, answer.getTplSpecQuestion());
							// #26226 - Start
							populateSpecAnswers(answer);
							// #26226 - End
							master.setTplSpecQuestions(newTplSpecQuestionsList);
							// #25025 - Start.
							isSpecificationEnable=true;
							// #25025 - End.
							}
						
						
						}else{
							/** This code is for LOA with multiple Items specification**/
							if(answer.getTplSpecQuestion().getClDeliveryTerm() == null)
							{
								master = answer.getTplSpecQuestion().getTplSpecMaster();
								constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master, answer.getTplSpecQuestion());
								// #26226 - Start
								populateSpecAnswers(answer);
								// #26226 - End
								master.setTplSpecQuestions(newTplSpecQuestionsList);
								// #25025 - Start.
								isSpecificationEnable=true;
								// #25025 - End.
								
							}

						}
						
												
					}
					
				}
				if(master !=null)
				{
					if (masters.size() == 0) {
						masters.add(master);
					} else {
						boolean itemFound = false;
						for (TplSpecMaster tplSpecMaster : masters) {
							if (tplSpecMaster.getSpecMasterId() == master.getSpecMasterId()) {
								itemFound = true;
								break;
							}
						}
						if (!itemFound) {
							masters.add(master);
						}
					}
					
				}
			}
			
		}	
			log.info("Master Record List Size II -->"+masters.size());
			context.put("serviceScope", dafaltValue);
			context.put("supplierName", supplierName);
			context.put("zoneId", zoneId);
			context.put("masters", masters);
			context.put("bean", this);
			context.put("isZonal", scLoa.getScLoiLoa().isZonal());
			context.put("isWipData", isWipData); //#29006
			
		} finally {
			quoteSO = null;
			days=null;
			//masters.clear();
		}
	}
	// #26226 - End

	// #26226 - Start
	/**
	 * @param answer
	 */
	private void populateSpecAnswers(TplSpecAnswer answer) {
		try {
			boolean itemFound = false;
			for (TplSpecAnswer tplSpecAnswer : answer.getTplSpecQuestion().getTplSpecAnswers()) {
				if (tplSpecAnswer.getSpecAnswerId() == answer.getSpecAnswerId()) {
					itemFound = true;
					break;
				}
			}
			if (!itemFound) {
				answer.getTplSpecQuestion().getTplSpecAnswers().add(answer);
			}
		} catch (Exception e) {
			List<TplSpecAnswer> answers = new ArrayList<>();
			answers.add(answer);
			answer.getTplSpecQuestion().setTplSpecAnswers(answers);
		}
	}
	// #26226 - End

	/**  
	 * CR Enhancement 26226 Nov 2019   End of the method 
	 */
	
		
	
	
	
	/**
	 * @param dafaltValue
	 * @param newTplSpecQuestionsList
	 * @param master
	 * @param question
	 */
	private void constructTplSpecQuestion(String dafaltValue,
			List<TplSpecQuestion> newTplSpecQuestionsList,
			TplSpecMaster master, TplSpecQuestion question) {
		TplSpecQuestion parentQuestion = question
				.getParentSpecQuestion();
		if(null!=master.getSpecName()){
               String specTitle= master.getSpecName();
               specTitle=specTitle.replaceAll(QTConstants.NEW_LINE_CHARACTER,TemplateDocConst.PARAM_SPACE);
               specTitle=specTitle.replaceAll(QTConstants.AMBRA_SENT,TemplateDocConst.PARAM_SPACE);
               specTitle=specTitle.replaceAll(QTConstants.LESS_THEN,TemplateDocConst.PARAM_SPACE);
               specTitle=specTitle.replaceAll(QTConstants.GREATER_THEN,TemplateDocConst.PARAM_SPACE);
               master.setSpecName(specTitle);
			
		}
		if (question.getClItem() == null) {
			question.setClItem("");
		}else{
		    String clItem = question.getClItem();
		//20190513@asyrafhaziq - 25812 - Start
		// add a condition to filter the character (character after space and before space) length for data 'ITEM'.
		    String[] words = clItem.split(" "); //get array of words
		    StringBuilder sb = new StringBuilder();
		    StringBuilder subSb = new StringBuilder();//this is for perfomance
		    for(String word : words) //affect each word in string
		    {
		      int length = word.length();
		      if ( length >= 20)
		    	{
		    	  String subWord;
		            for (int i = 0; i < length; i += 20) {
		            	subWord = word.substring(i, Math.min(length, i + 20));
		            	subSb.append(subWord).append(TemplateDocConst.PARAM_SPACE);
		            }
		    		word = subSb.toString();	
		    	}
		        sb.append(word).append(TemplateDocConst.PARAM_SPACE);
		    }
		    clItem =  sb.toString();
		    //20190513@asyrafhaziq - 25812 - End
			clItem=clItem.replaceAll(QTConstants.NEW_LINE_CHARACTER,TemplateDocConst.PARAM_SPACE);
			clItem=clItem.replaceAll(QTConstants.AMBRA_SENT,TemplateDocConst.PARAM_SPACE);
			clItem=clItem.replaceAll(QTConstants.LESS_THEN,TemplateDocConst.PARAM_SPACE);
			clItem=clItem.replaceAll(QTConstants.GREATER_THEN,TemplateDocConst.PARAM_SPACE);
			question.setClItem(clItem);
			//log.info("CIItem  ----------->"+question.getClItem());
		}
		if (question.getClSpecDetail() == null) {
			question.setClSpecDetail(dafaltValue);
		} else{
			 String clSpecDetail = question.getClSpecDetail()
					.replaceAll(QTConstants.NEW_LINE_CHARACTER,TemplateDocConst.PARAM_SPACE);
			 clSpecDetail=clSpecDetail.replaceAll(QTConstants.AMBRA_SENT,TemplateDocConst.PARAM_SPACE);
			 clSpecDetail=clSpecDetail.replaceAll(QTConstants.LESS_THEN,TemplateDocConst.PARAM_SPACE);
			 clSpecDetail=clSpecDetail.replaceAll(QTConstants.GREATER_THEN,TemplateDocConst.PARAM_SPACE);
			question.setClSpecDetail(clSpecDetail);
		}
		if (question.getClUom() != null) {
			if (question.getClUom().getUomName() == null) {
				question.getClUom().setUomName(dafaltValue);
			}
		} else {
			question.setClUom(new PmUom());
			question.getClUom().setUomName(dafaltValue);
		}
		
		if (question.getClUomFrequency() == null) {
			question.setClUomFrequency(new BigDecimal(0));
		}
		
		if (question.getClUomPerDay() == null) {
			question.setClUomPerDay(new BigDecimal(0));
		}
		
		if (question.getClUomPerMonth() == null) {
			question.setClUomPerMonth(new BigDecimal(0));
		}
		
		if (question.getClPriceTypeCode() == null) {
			question.setClPriceTypeCode(new PmParameter());
			question.getClPriceTypeCode().setParameterCode(dafaltValue);
		} else {
			if (question.getClPriceTypeCode().getParameterCode() == null) {
				question.getClPriceTypeCode().setParameterCode(dafaltValue);
			}
		}
		if (question.getClQuantity() == null) {
			question.setClQuantity(new BigDecimal(0));
		}

		if (question.isClAnswerRequired()) {
			question.setClAnswerRequired(true);
		}
		if (question.getClReviewRemark() == null) {
			question.setClReviewRemark(dafaltValue);
		}
		if (parentQuestion != null) {
			int specCount = parentQuestion.getSpecCount();
			if (specCount == 0) {
				specCount = 1;
			}
			parentQuestion.setSpecCount(specCount + 1);
		}
		question.setTplSpecMaster(master);
		// #26226 - Start
		if(newTplSpecQuestionsList !=null)
		{
			if (newTplSpecQuestionsList.size() == 0) {
				newTplSpecQuestionsList.add(question);
			} else {
				boolean itemFound = false;
				for (TplSpecQuestion tplSpecQuestion : newTplSpecQuestionsList) {
					if (tplSpecQuestion.getSpecQuestionId() == question.getSpecQuestionId()) {
						itemFound = true;
						break;
					}
				}
				if (!itemFound) {
					newTplSpecQuestionsList.add(question);
				}
			}
			
		}
		// #26226 - End
	}
	
	
	/**
	 * @param context
	 * @param scLoa
	 * @param specMasters 
	 */
	public void constructSupplierSpecificationAndRemarks(VelocityContext context, ScLoa scLoa, List<TplSpecMaster> specMasters) {
		List<TplSpecMaster> masters = new ArrayList<TplSpecMaster>();
		String dafaltValue = "T/B";
		if ("en".equals(new FacesScopes().getUserLocale().getLanguage())) {
			dafaltValue = "N/A";
		}
		
		if (CollectionUtils.isNotEmpty(specMasters)) {
			
			for (TplSpecMaster master : specMasters) {
				if (CollectionUtils.isNotEmpty(master.getTplSpecQuestions())) {
					List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<TplSpecQuestion>();
					for (TplSpecQuestion question : master.getTplSpecQuestions()) {
						// #26130 - Start.
						constructSpecData(scLoa, master, question);
						// #26130 - End.
						constructTplSpecQuestion(dafaltValue,
								newTplSpecQuestionsList, master, question);
					}
					master.setTplSpecQuestions(newTplSpecQuestionsList);
					masters.add(master);
				}
			}
		}
		context.put("isZonal", scLoa.getScLoiLoa().isZonal());
		context.put("serviceScope", dafaltValue);
		context.put("masters", masters);
		context.put("bean", this);
	}
	
	/**
	 * Get Parameter description by parameter for report.
	 * 
	 * @param parameter
	 *            the PmParameter instance.
	 * @return parameter description
	 */
	public String getParameterDescForReport(PmParameter parameter) {
		if (parameter != null
				&& StringUtils.isNotBlank(parameter.getParameterType())) {
			return getParameterDesc(
					parameter,
					new FacesScopes().getUserLocale());
		} else {
			return null;
		}
	}
	
	
	/**
	 * Get Parameter description by parameter for report.
	 * 
	 * @param parameter
	 *            the PmParameter instance.
	 * @return parameter description
	 */
	public String getParameterDescForVelocityReport(PmParameter parameter) {
		if (parameter != null
				&& StringUtils.isNotBlank(parameter.getParameterType())) {
			return getParameterDesc(
					parameter,
					myLocale);
		} else {
			return null;
		}
	}
	/**
	 * Method to return true, if is printing or false if is not printing
	 * @param category of printing only
	 * @return	true or false
	 **/
	
	public boolean isPrintedForBumiputraOnly(List<ScQtCategory> listQfCategory, boolean printed) { 
		ICategoryCodeSO categoryCodeSO = null;
	    try{
	    	categoryCodeSO = BusinessDelegate.getInstance().getServiceObject(
	    			ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
				
				if (!EntityHelper.isCollectionEmpty(listQfCategory)) {
					for(ScQtCategory cat : listQfCategory){
						if(cat.getPmCategoryL3().getCategoryL3Code() != null){
							CategoryCodeReqDTO catCodeReqDTO = new CategoryCodeReqDTO();
							catCodeReqDTO.setCategoryL3Id(cat.getPmCategoryL3().getCategoryL3Id());	
							catCodeReqDTO.setIsPrinting(1);
							//true, if is printing or false if is not printing	
							CategoryCodeResDTO catCodeResDTO = categoryCodeSO.checkIsPrinting(catCodeReqDTO);
							if(catCodeResDTO.isPrinting()){
								printed = true;
								break;
							}
						}
					}
				}
				
				return printed;	
				
	    }finally{
	    	categoryCodeSO = null;
	    }
	}
	
	// Redmine 25396: remove duplication option view by adding new drop down field
		/**
		 * Checks if is supplier role.
		 *
		 * @return true, if is supplier role
		 */
		public boolean hasSupplierRole(){
			if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
				return Boolean.TRUE;
			} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
				return Boolean.TRUE;
			} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				return Boolean.TRUE;
			} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
				return Boolean.TRUE;
			} else if(getFacesScopes().hasRoleUser(RoleCode.FL_USER.value())) {
				return Boolean.TRUE;
			} else if(getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
				return Boolean.TRUE;
			} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {
				return Boolean.TRUE;
			} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		
		/**
		 * Check is it login user is Government User
		 * 
		 * @return true, if user is Government User
		 */
		protected boolean hasGovernmentUserRole() {
			if (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()))
				return true;
			else if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
				return true;
			else if (getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value()))
				return true;
			else if (getFacesScopes().hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value()))
				return true;		
			else if (getFacesScopes().hasRoleUser(RoleCode.PAY_OFFICER.value()))
				return true;		
			else
				return false;		
		}
	// end of 25396
		
	/**
	 * Retrieve report tab dtl.Event handle set attachment for Reports tab.
	 */
	/**
	 * @param scqt
	 * @param committeeType
	 * @param taskType 
	 */
	public void retrieveReportTabDtl (ScQt scqt, String committeeType, String taskType) {
		log.debug("start retrieveReportTabDtl");
		//retrieveReportHeader();  
		IQTEvaluationSO evaluationSO;
		EvaluationResDTO resDTO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(scqt.getQtId());
			reqDTO.setCommType(EvaluationReport.getName(committeeType));
			reqDTO.setDocDefinationKey(ParameterConst.DOCUMENT_DEFINITION_TYPE);
			// qtReqDTO.setQtId(qtId);
			
			resDTO = evaluationSO.retriveQtDtlsByRemarksAttachments(reqDTO);
			
			resDTO = evaluationSO.retriveReportDetails(reqDTO);
			if (null != resDTO.getReports()
					&& !resDTO.getReports().isEmpty()) {
				Iterator<ScQtEvaluationReport> iterator = resDTO.getReports().iterator();
				if (taskType.equals(SourcingConstants.CONSOLIDATION_TASK)) {
					while (iterator.hasNext()) {
						ScQtEvaluationReport report = iterator.next(); 
						// 18685 - START.
						if (SourcingConstants.PERAKUAN_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
								|| SourcingConstants.DOC_DEF_KEY_UDPUPBM_REPORT.equals(report.getPmDocument().getDocDefinitionKey())) {
							iterator.remove();
						}
						// 18685 - END.
					}
				}
				
				if (taskType.equals(SourcingConstants.SUPPLIER_FINALIZATION_TASK)) {
					while (iterator.hasNext()) {
						ScQtEvaluationReport report = iterator.next(); 
						// 18685 - START.
						if (SourcingConstants.PERAKUAN_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
								|| SourcingConstants.DOC_DEF_KEY_LATAR_BELAKANG_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
								|| SourcingConstants.DOC_DEF_KEY_UDPUP_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
								|| SourcingConstants.DOC_DEF_KEY_METODOLOGI_PENILAIAN_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
								|| SourcingConstants.DOC_DEF_KEY_TUJUAN_REPORT.equals(report.getPmDocument().getDocDefinitionKey())) {
							iterator.remove(); 
						}
						// 18685 - END.
					}
					
					if (resDTO.getReports().isEmpty()) {
						//createReportForSF(scqt); 
					}
				}
				getManagedBean().setScQtEvaluationReportList(
						resDTO.getReports());
			}
				
		
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				/*if (null != scqt.getScEvaluationCommitteeReportAttachment()) {
					ScAttachment attachment = scqt.getScEvaluationCommitteeReportAttachment();
					this.getManagedBean().setScAttachment(attachment);
				}*/
				this.getManagedBean().setReportDownloadFileName(
						"NGeP-QT-Evaluation-Committee-Report");

			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				ScAttachment attachment = scqt.getScOpenCommitteeReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						"NGeP-QT-Opening-Committee-Report");

				this.getManagedBean().setScAttachment(attachment);

			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				ScAttachment attachment = scqt.getScTechnicalEvaluationReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						"NGeP-QT-TechnicalEvaluation-Committee-Report");

				this.getManagedBean().setScAttachment(attachment);

			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				ScAttachment attachment = scqt.getScFinancialEvaluationReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						"NGeP-QT-FinancialEvaluation-Committee-Report");

				this.getManagedBean().setScAttachment(attachment);

			}
			log.debug("end retrieveReportTabDtl");
		} finally {
			evaluationSO = null;
		}

	}

	// 18685 - START.
	/**
	 * Create new report during Supplier Finalization Preparation.
	 * @param qtId 
	 * @param scqt
	 */
	private void createReportForSF(PmDocument document, ScQtFinalization scQtFinalization, Long qtId) {
		log.debug("Entered " + this.getClass().getName() +".createReportForSF");
		DocumentReqDTO docReqDTO = new DocumentReqDTO();
		docReqDTO.setDocName(document.getDocName());
		docReqDTO.setDocumentId(qtId);
		List<String> docDefinitionKeys = new ArrayList<>();
		docDefinitionKeys.add(document.getDocDefinitionKey());
		docReqDTO.setDocDefinitionKeys(docDefinitionKeys);
		IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDocumentSO.class);
		
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO paramReqDTO = new ParameterReqDTO();
		paramReqDTO.setParameterType(ParameterTypeConst.DOCUMENT_CATEGORY);
		paramReqDTO.setParameterCode(ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER);
		ParameterResDTO paramResDTO = parameterSO.retrieveParameterByTypeAndCode(paramReqDTO);
		PmParameter docCategoryCode = null;
		if (null != paramResDTO) {
			docCategoryCode = paramResDTO.getPmParameter();
		}
		DocumentResDTO docResDTO = reqSO.retrieveDocument(docReqDTO, docCategoryCode);
		if (null != docResDTO && docResDTO.isSuccessful() && null != docResDTO.getDocument()) {
			
			
			ScQtFinalizationReport report = new ScQtFinalizationReport();
			if (scQtFinalization == null) {
				scQtFinalization = new ScQtFinalization();
			}
			//report.setSelected(true);
			
			try {
				parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				
				report.setScQtFinalization(scQtFinalization);
				report.setDocCategory(docResDTO.getDocument().getDocCategoryCode().getParameterCode());
				report.setPmDocument(docResDTO.getDocument());
				report.setTitle(docResDTO.getDocument().getDocName());
				report.setDefinationType(docResDTO.getDocument().getDocDefinitionTypeCode());
				report.setDocCategory(
						ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER); 
			} finally {
				parameterSO = null;
				reqSO = null;
			}
			
			if (null == getManagedBean().getNewBriefingPaperReportList()) {
				List<ScQtFinalizationReport> newBriefingPaperReportList = new ArrayList<>();
				getManagedBean().setNewBriefingPaperReportList(newBriefingPaperReportList );
			}
			getManagedBean().getNewBriefingPaperReportList().add(report);
			
		}

	}
	// 18685 - END.
	
	/**
	 * Download all reports.download all evaluation reports.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void downloadAllReports(ScQt scqt) throws Exception {
		log.debug(this.getClass().getName() + ".downloadAllReports()");
		List<ScQtFinalizationReport> scQtFinalizationReports = getManagedBean()
				.getNewBriefingPaperReportList();
		List<ScAttachment> attachments = new ArrayList<ScAttachment>();
		/*for (ScQtEvaluationReport scQtEvaluationReport : evaluationReports) {
			ScQtEvaluationReportAttachment reportAttachment = scQtEvaluationReport
					.getScQtEvaluationReportAttachment();
			if (reportAttachment == null) {
				//reportAttachment = generateDocumentAttachment(scQtEvaluationReport);
				reportAttachment =(ScQtEvaluationReportAttachment)generateDocumentAttachment();
				scQtEvaluationReport
						.setScQtEvaluationReportAttachment(reportAttachment);
			}
			attachments.add(reportAttachment);
		}*/
		for(ScQtFinalizationReport scQtFinalizationReport:scQtFinalizationReports){
			if(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD.equalsIgnoreCase(scQtFinalizationReport.getDefinationType().getParameterCode())){
				if(null!=scQtFinalizationReport.getScQtFinalizationReportAttachment()){
					attachments.add(scQtFinalizationReport.getScQtFinalizationReportAttachment());	
				}
			}
			
		}
		attachments.add(generateDocumentAttachment(scqt, ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER));
		downloadAllFiles(this.getManagedBean().getReportDownloadFileName(),
				attachments);
	}
	
	/**
	 * @param scqt
	 * @param committeeType
	 * @return
	 * @throws Exception
	 */
	private ScAttachment generateDocumentAttachment(ScQt scqt, String committeeType) throws Exception {
		log.debug(this.getClass().getName() + ".generateDocumentAttachment()");
		
		ScAttachment reportAttachment = new ScQtFinalizationReportAttachment();
		ScQt scQt = scqt;
		
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(committeeType)) {
			reportAttachment = new ScEvaluationCommitteeReportAttachment();
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equalsIgnoreCase(committeeType)) {
			reportAttachment = new ScOpenCommitteeReportAttachment();
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equalsIgnoreCase(committeeType)) {
			reportAttachment = new ScTechnicalEvaluationReportAttachment();
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equalsIgnoreCase(committeeType)) {
			reportAttachment = new ScFinancialEvaluationReportAttachment();
		}	
		
		StringBuilder fileName = new StringBuilder(
				getManagedBean().getReportDownloadFileName());
		fileName.append(".pdf");
		reportAttachment.setFileName(fileName.toString());
		reportAttachment.setDocNo(scQt.getQtNo());
		generateEvaluationPdf(
				(ScAttachment) reportAttachment,
				scqt, committeeType);
		
		return reportAttachment;
	}
	
	/**
	 * @param committeeType 
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateEvaluationPdf(ScAttachment attachment, ScQt qt, String committeeType)
			throws Exception {
		log.debug(this.getClass().getName() + ".generateEvaluationPdf()");
		populateAttachmentDetails(attachment);
		
		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportName = StringUtils.EMPTY;
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equals(committeeType)) {
			reportName = ReportConst.SC_EC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equals(committeeType)) {
			reportName = ReportConst.SC_OC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(committeeType)) {
			reportName = ReportConst.SC_TEC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(committeeType)) {
			reportName = ReportConst.SC_FEC_RPT_JSP;
		}
		
		String estimatedAmountStr = null;
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			BigDecimal estimatedAmount = qtDetailSO.getdecryptedEstAmount(qtId);
			if (null != estimatedAmount) { 
				estimatedAmountStr = estimatedAmount.toPlainString();
			}
		} finally {
			qtDetailSO = null;
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_ESTIMATED_AMOUNT, estimatedAmountStr);
		
		if (StringUtils.isNotBlank(reportName)) { 
			// Add dynamic value for boolean value in below line.
			generatePdfFromOracle(reportName, paramMap, attachment, true);
		} else {
			log.info("ReportName is empty");
		}
	}
	
	/**
	 * Delete reports upload file.Event handle to delete upload file in
	 * reports tab.
	 * @param delEntity
	 *               (BaseEntity delEntity).
	 */
	public void deleteReportsUploadFile(BaseEntity delEntity) {
		log.debug("deleteUploadFile");
		
		try{
		log.debug("deleteReportsUploadFile");
		ScQtFinalizationReport selectedEvaluationReport = getManagedBean()
				.getSelectedBriefingReport();
		selectedEvaluationReport.setScQtFinalizationReportAttachment(null);
		 getManagedBean()
			.setSelectedBriefingReport(selectedEvaluationReport);
		}finally{
		
		}

	}
	
	/**
	 * @param finalizationReports
	 * @param scQt 
	 */
	public void populateEvolutionReports(List<ScQtFinalizationReport> finalizationReports, ScQt scQt) {
		if (null != scQt) {
			String evaluationType = scQt.getEvaluationType();
			if (StringUtils.isNotEmpty(evaluationType)) {
				Iterator<ScQtFinalizationReport> iterator = finalizationReports.iterator();
				while (iterator.hasNext()) {
					ScQtFinalizationReport report = iterator.next();
					if (null != report) {
						if (getManagedBean().getEvaluationReportList() == null) {
							List<ScQtFinalizationReport> erList = new ArrayList<>();
							getManagedBean().setEvaluationReportList(erList);
						}
						// 18685 - START.
						if (null != report.getPmDocument()) {
							if (SourcingConstants.EVALUATION_TYPE_TIER1.equals(evaluationType)) {
								if (SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT.equals(report.getPmDocument().getDocDefinitionKey())) {
									getManagedBean().getEvaluationReportList().add(report);
									iterator.remove();
								}
							} else if (SourcingConstants.EVALUATION_TYPE_TIER2.equals(evaluationType)) {
								//#28784
								if (SourcingConstants.DEMO_EVALUATION_REPORT.equalsIgnoreCase(report.getTitle())
										|| SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
										|| SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
										|| SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
										|| SourcingConstants.DEMO_EVALUATION_REPORT.equalsIgnoreCase(report.getTitle())) {//#28784
									getManagedBean().getEvaluationReportList().add(report);
									iterator.remove();
								}
							}
						// 18685 - END.	
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param taskType 
	 * 
	 */
	public void validateEvaluationReports(String taskType){
		List<ScQtFinalizationReport> scQtFinalizationReportList = this.getManagedBean().getNewBriefingPaperReportList();
		if (null != scQtFinalizationReportList && scQtFinalizationReportList.size() > 0) {
			String reportNames = "";
			// #18685 - Start.
			resetUnSavedReportContent(scQtFinalizationReportList);
			// #18685 - End.
			for (ScQtFinalizationReport scQtFinalizationReport : scQtFinalizationReportList) {
				
					// 18685 - START.
					if (null != scQtFinalizationReport.getPmDocument()) {
						
						if (taskType.equals(SourcingConstants.CONSOLIDATION_TASK)) {
							if (SourcingConstants.DOC_DEF_KEY_LATAR_BELAKANG_REPORT.equals(scQtFinalizationReport.getPmDocument().getDocDefinitionKey())
									|| SourcingConstants.DOC_DEF_KEY_TUJUAN_REPORT.equals(scQtFinalizationReport.getPmDocument().getDocDefinitionKey())
									|| SourcingConstants.DOC_DEF_KEY_METODOLOGI_PENILAIAN_REPORT.equals(scQtFinalizationReport.getPmDocument().getDocDefinitionKey())
									|| SourcingConstants.DOC_DEF_KEY_UDPUP_REPORT.equals(scQtFinalizationReport.getPmDocument().getDocDefinitionKey())) {
								
								if (StringUtils.isEmpty(scQtFinalizationReport.getReportContent())) {
									if (StringUtils.isEmpty(reportNames)) {
										reportNames = reportNames + scQtFinalizationReport.getTitle();
									} else {
										reportNames = reportNames + ", " + scQtFinalizationReport.getTitle();
									}
								}
							} else if (scQtFinalizationReport.getScQtFinalizationReportAttachment() == null){
								//throw new EpException(MessageConst.ERROR_775150, new String[] { scQtFinalizationReport.getTitle() });
							}
						} else if (taskType.equals(SourcingConstants.SUPPLIER_FINALIZATION_TASK)) {
							if (SourcingConstants.DOC_DEF_KEY_UDPUPBM_REPORT.equals(scQtFinalizationReport.getPmDocument().getDocDefinitionKey())) {
								if (StringUtils.isEmpty(scQtFinalizationReport.getReportContent())) {
									if (StringUtils.isEmpty(reportNames)) {
										reportNames = reportNames + scQtFinalizationReport.getTitle();
									} else {
										reportNames = reportNames + ", " + scQtFinalizationReport.getTitle();
									}
								}
							} else if (scQtFinalizationReport.getScQtFinalizationReportAttachment() == null){
								//throw new EpException(MessageConst.ERROR_775150, new String[] { scQtFinalizationReport.getTitle() });
							}
						}
					}
					// 18685 - END.
					// #18685 - Start.
					if ("UP".equals(scQtFinalizationReport.getDefinationType().getParameterCode())
							&& scQtFinalizationReport.getScQtFinalizationReportAttachment() == null) {
						throw new EpException(MessageConst.ERROR_775166, new String[] { scQtFinalizationReport.getTitle() });
					}
					// #18685 - End.
			} 
				
			if (StringUtils.isNotEmpty(reportNames)) {
				throw new EpException(MessageConst.ERROR_775149, new String[] { reportNames });
			}
		}
	}
	
	// #18685 - Start.
	/**
	 * @param scQtFinalizationReportList
	 */
	public void resetUnSavedReportContent(
			List<ScQtFinalizationReport> scQtFinalizationReportList) {
		IQTSuppFinalizationSO qtSuppFinalizationSO;
		List<Long> qtReportIds = new ArrayList<Long>();
		
		for (ScQtFinalizationReport scQtFinalizationReport : scQtFinalizationReportList) {
			qtReportIds.add(scQtFinalizationReport.getQtReportId());
		}

		try {
		    qtSuppFinalizationSO = BusinessDelegate.getInstance()
		        .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
		                IQTSuppFinalizationSO.class);
		    SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		    ScQtFinalization scQtFinalization = this.getManagedBean().getNewBriefingPaperReportList().get(0).getScQtFinalization();
		    if (scQtFinalization == null) {
		      scQtFinalization = new ScQtFinalization();
		    }
		    reqDTO.setScQtFinalization(scQtFinalization);
		    reqDTO.setQtReportIdList(qtReportIds);
		    reqDTO.setDocumentCategory(this.getManagedBean().getNewBriefingPaperReportList().get(0).getDocCategory());
		    SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveFinalizationReport(reqDTO);
		    if (null != resDTO && null != resDTO.getScQtFinalizationReports()
		    		&& resDTO.getScQtFinalizationReports().size() > 0) {
		    repLoop:	for (ScQtFinalizationReport repScQtFinalizationReport : resDTO.getScQtFinalizationReports()) {
		    		for (ScQtFinalizationReport scQtFinalizationReport : scQtFinalizationReportList) {
		    			if (repScQtFinalizationReport.getTitle().equals(scQtFinalizationReport.getTitle())) {
		    				scQtFinalizationReport.setReportContent(repScQtFinalizationReport.getReportContent());
		    				continue repLoop;
		    			}
		    		}
		    	}
		    }
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	// #18685 - End.
	
	/**
	 * this method is used to save the userInput type popup window content in reports tab.
	 * @param brefingPaperRepor
	 *                 (ScQtEvaluationReport evaluationReport).
	 *                 
	 */
	public void saveEditTemplateDocContent(ScQtFinalizationReport brefingPaperRepor){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try{
			List<ScQtFinalizationReport> brefingPaperReportList = this.getManagedBean().getNewBriefingPaperReportList();
			if(null!=brefingPaperRepor && !this.getManagedBean().isReadOnly() && CollectionUtils.isNotEmpty(brefingPaperReportList)){
				int index =	brefingPaperReportList.indexOf(brefingPaperRepor);
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				ScQtFinalizationReport updatedReport = getManagedBean().getNewBriefingPaperReportList().get(index);
				List<ScQtFinalizationReport> updatedReportList = new ArrayList<>();
				updatedReportList.add(updatedReport);
				reqDTO.setAddNewBriefingPaperReportList(updatedReportList);
				
				qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				qtSuppFinalizationSO.saveFinalizationReports(reqDTO);
			}
		}finally{
			qtSuppFinalizationSO = null;
		}
		
	}
	
	/**
	 *reports file upload.to upload files for manual added reports.
	 * 
	 * @param event
	 *            (Mandatory)
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void reportsUploadAttachment(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtFinalizationReport briefingReport = getManagedBean()
					.getSelectedBriefingReport();
			ScQtFinalizationReportAttachment reportAttachment = briefingReport
					.getScQtFinalizationReportAttachment();
			if (null == reportAttachment) {
				reportAttachment = new ScQtFinalizationReportAttachment();
				reportAttachment.setScQtFinalizationReport(briefingReport);
				briefingReport
						.setScQtFinalizationReportAttachment(reportAttachment);
			} else {
				reportAttachment.setScQtFinalizationReport(briefingReport);
				briefingReport
						.setScQtFinalizationReportAttachment(reportAttachment);
			}
			
			// #18685 - Start
			int index = 0;
			
			if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())
			    && this.getManagedBean().getNewBriefingPaperReportList().size() > 0) {
			  for (ScQtFinalizationReport finalizationReport : getManagedBean().getNewBriefingPaperReportList()) {
			    if (finalizationReport.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)) {
			      index++;
			    }
			  }
			}
			reportAttachment.setDocNo(getManagedBean().getDocNo());
			scAttachment = reportAttachment;
			uploadAttachment(event.getFile(), scAttachment);
			String fileName = scAttachment.getFileName();
			
			if (index >= 0 && fileName != null) {
              reportAttachment.setFileName(index + UNDER_SCORE
                      + fileName);
			}
			
			briefingReport.setScQtFinalizationReportAttachment(reportAttachment);
			//saveScQtFinalizationReport(briefingReport);
			// #18685 - End
			
		}
	}
	
	// 18685 - START.
	/**
	 * @param reportTitle
	 * @param scQtFinalization
	 * @param qtId 
	 */
	public void saveNewBriefingPaperReportList(List<String> reportDocDefList, ScQtFinalization scQtFinalization, Long qtId){
		
		IDocumentSO documentSO = null;
		
		try {
			documentSO = (IDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDocumentSO.class);
			DocumentReqDTO documentReqDTO = new DocumentReqDTO();
			documentReqDTO.setDocDefinitionKeys(reportDocDefList);
			documentReqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER);
			DocumentResDTO documentResDTO = documentSO.retrieveDocuementsByDocDefKeys(documentReqDTO);
			if(documentResDTO.isSuccessful()){
				if (scQtFinalization == null) {
					scQtFinalization = new ScQtFinalization();
				}
				for(PmDocument document:documentResDTO.getDocumentList()){
					createReportForSF(document, scQtFinalization, qtId);
				}
			}
		} finally {
			documentSO = null;
		}
		
			
		if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())) {
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			try {
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				reqDTO.setPayLoad(getManagedBean().getPayLoad());
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
				qtSuppFinalizationSO.saveFinalizationReports(reqDTO);
			} finally {
				qtSuppFinalizationSO = null;
			}
		}
		
	}
	// 18685 - END.
	
	/**
	 * delete existing report details records from database.
	 *  
	 */
	public void deleteReportsTabDtls() {
		List<ScQtFinalizationReport> removeFinalizationReports = getManagedBean()
				.getRemoveFinalizationReports();
		if (null == removeFinalizationReports) {
			removeFinalizationReports = new ArrayList<ScQtFinalizationReport>();
			getManagedBean()
			.setRemoveFinalizationReports(removeFinalizationReports);
		}
		markDeleteSelectedRowsAction(getManagedBean()
				.getNewBriefingPaperReportList(), removeFinalizationReports);
		if(CollectionUtils.isNotEmpty(removeFinalizationReports)){
			getManagedBean()
			.setRemoveFinalizationReports(removeFinalizationReports);
		}

	}
	
	// 18685 - START.
	/**
	 * checkKertasTaklimat Uploaded report
	 * 
	 * 
	 * @param finalizationReports
	 * @param reportTitleList 
	 * 
	 */
	protected void checkKertasTaklimatReports(List<ScQtFinalizationReport> finalizationReports, List<String> reportDocDefList, String taskType){
		if(CollectionUtils.isNotEmpty(finalizationReports)){
			int count=0;
			List<ScQtFinalizationReport> localListReport=new ArrayList<ScQtFinalizationReport>();
			Iterator<ScQtFinalizationReport> iterator = finalizationReports.iterator();
			while(iterator.hasNext()){
				ScQtFinalizationReport finalizationReport = iterator.next();
				if(null!=finalizationReport.getPmDocument()){ 
					if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocCategoryCode().getParameterCode())) {
						log.info("=======sourbasebackingBeanTitle===="+finalizationReport.getTitle());
						if(finalizationReport.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED)){
						log.info("=======mangatitlecodesexecutes========"+finalizationReport.getPmDocument().getDocName());	
						count++;
						localListReport.add(finalizationReport);
						continue;
					}
						// #18685 - Start
						if (null == getManagedBean().getNewBriefingPaperReportList()) {
							List<ScQtFinalizationReport> newBriefingPaperReportList = new LinkedList<>();
							getManagedBean().setNewBriefingPaperReportList(newBriefingPaperReportList );
						}
						// #18685 - End
						if (reportDocDefList.contains(finalizationReport.getPmDocument().getDocDefinitionKey())) {
							getManagedBean().getNewBriefingPaperReportList().add(finalizationReport);
							// #18685 - Start
							/*if (QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION.longValue() == getManagedBean().getCurrentStatusId() 
									&& taskType.equals(SourcingConstants.SUPPLIER_FINALIZATION_TASK)) {
								finalizationReport.setPreviousReportContent(finalizationReport.getReportContent());
								saveScQtFinalizationReport(finalizationReport);
							}*/
							// #18685 - End
							iterator.remove();
							continue;
						} 
					} 
					
					if (taskType.equals(SourcingConstants.CONSOLIDATION_TASK) && finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_UDPUPBM_REPORT)) {
						// #18685 - Start
						if (null == getManagedBean().getSfBriefingPaperReportList()) {
							List<ScQtFinalizationReport> sFBriefingPaperReportList = new ArrayList<>();
							getManagedBean().setSfBriefingPaperReportList(sFBriefingPaperReportList );
						}
						getManagedBean().getSfBriefingPaperReportList().add(finalizationReport);
						// #18685 - End
						iterator.remove();
						continue;
					}
					
					if (taskType.equals(SourcingConstants.SUPPLIER_FINALIZATION_TASK) && 
							(finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_LATAR_BELAKANG_REPORT)
									|| finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_TUJUAN_REPORT)
									|| finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_METODOLOGI_PENILAIAN_REPORT)
									|| finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_UDPUP_REPORT))) {
						// #18685 - Start
						if (null == getManagedBean().getConsolidationBriefingPaperReportList()) {
							List<ScQtFinalizationReport> consolidationBriefingPaperReportList = new ArrayList<>();
							getManagedBean().setConsolidationBriefingPaperReportList(consolidationBriefingPaperReportList );
						}
						getManagedBean().getConsolidationBriefingPaperReportList().add(finalizationReport);
						// #18685 - End
						iterator.remove(); 
						continue;
						
					}
					
				} 
				// #18685 - Start.
				if ("UP".equals(finalizationReport.getDefinationType().getParameterCode()) 
						&& !finalizationReport.getTitle().contains("Kertas Keputusan")) {
					// #18685 - Start.
					if (null == getManagedBean().getNewBriefingPaperReportList()) {
						List<ScQtFinalizationReport> newBriefingPaperReportList = new LinkedList<>();
						getManagedBean().setNewBriefingPaperReportList(newBriefingPaperReportList );
					}
					// #18685 - End.
					getManagedBean().getNewBriefingPaperReportList().add(finalizationReport);
					iterator.remove();
				}
				// #18685 - End.
			}
			
			// #18685 - Start
			if (taskType.equals(SourcingConstants.CONSOLIDATION_TASK) && CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())) {
				List<ScQtFinalizationReport>  tempList = new ArrayList<>();
				for (String reportName : reportDocDefList) {
					for (ScQtFinalizationReport report : getManagedBean().getNewBriefingPaperReportList()) {
						// #18685 - Start.
						if (null != report && null != report.getPmDocument() && reportName.equals(report.getPmDocument().getDocDefinitionKey())) {
							tempList.add(report);
							break;
						}
						// #18685 - End.
					}
				}
				// #18685 - Start.
				for (ScQtFinalizationReport report : getManagedBean().getNewBriefingPaperReportList()) {
					if ("UP".equals(report.getDefinationType().getParameterCode())) {
						tempList.add(report);
					}
				}
				// #18685 - End.
				getManagedBean().getNewBriefingPaperReportList().clear();
				getManagedBean().getNewBriefingPaperReportList().addAll(tempList);
			}
			// #18685 - End
			log.info("=========localListReportsizefirst===="+localListReport.size());
		/*	if(localListReport.size()>1){
				log.info("=========localListReportsize===="+localListReport.size());
				log.info("=========finalizationReportssize===="+finalizationReports.size());
			finalizationReports.addAll(localListReport);
			log.info("=========finalizationReportssize2222222===="+finalizationReports.size());
			localListReport=new ArrayList<ScQtFinalizationReport>();
			List<String> localTitleList=new ArrayList<String>();
			for(ScQtFinalizationReport localfinalReport:finalizationReports){
				if(!localTitleList.contains(localfinalReport.getTitle())){
					localTitleList.add(localfinalReport.getTitle());
					localListReport.add(localfinalReport)	;
				}
			}
			finalizationReports.clear();
			finalizationReports.addAll(localListReport);
			log.info("=========finalizationReportssize111111111===="+finalizationReports.size());
			}*/
			log.info("checkKertasTaklimatReports count"+count);
			if(count==1){
				log.info("enter count1"+count);
				for(ScQtFinalizationReport finalizationReport:finalizationReports){
					if(null!=finalizationReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocDefinitionKey())){
						finalizationReport.setKertasTaklimatUpload(Boolean.TRUE);	
					}
					
				}	
			}else if(count>1){
				log.info("enter countGreaterThan1"+count);
				 String SPACE = " ";
				  String reportTitle = ResourceBundleUtils.getMessage("lbl.sc.kertas.taklimat", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, SourcingConstants.MY_LOCALE) 
				 			+ SPACE + "(" + (count-1) + ")";
				  log.info("reportTitle"+reportTitle);
				for(ScQtFinalizationReport finalizationReport:finalizationReports){
					if(null!=finalizationReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocDefinitionKey()) && reportTitle.equalsIgnoreCase(finalizationReport.getTitle()) ){
						finalizationReport.setKertasTaklimatUpload(Boolean.TRUE);	
						break;
					}
					
				}	
			}
		}
		
	}
	// 18685 - END.

	// #25729 #142 Start - Allow create QT below threshold RM20k
		public boolean expectTotalIndPriceBelowThreshold(ScQt scQt, String ScreenType) {
			
			BigDecimal actualIntendedPrice = new BigDecimal(0);
			if ("createQt".equals(ScreenType)) {
				actualIntendedPrice = scQt.getTmpExpectedIntendedPrice();
			}
			else {
				actualIntendedPrice = scQt.getEstimatedAmount();
			}
			
			
			if (!scQt.isPanel() && actualIntendedPrice != null) {
				log.info(":::expectTotalIndPriceBelowThreshold() scQt.isPanel(): "+ scQt.isPanel() +" :::");
				log.info(":::expectTotalIndPriceBelowThreshold() ExpectedIntendedPrice: "+ actualIntendedPrice +" :::");

				// #25729 #174			 
				if ((actualIntendedPrice.compareTo(BigDecimal.valueOf(SourcingConstants.QT_BELOW_20K)) <=0))
				{
					return Boolean.TRUE;
				}
			}
			
			return Boolean.FALSE;
		}
		//#25729 - end -
	
		// #28215 - Start
		/**
		 * @param finalizationReport
		 */
		public void saveScQtFinalizationReport(ScQtFinalizationReport finalizationReport) {
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			try {
				
				if (null != finalizationReport && null != finalizationReport.getScQtFinalizationReportAttachment()
						&& StringUtils.isNotEmpty(finalizationReport.getScQtFinalizationReportAttachment().getFilePath())) {
					  String filePath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR) 
							  + CommonConstants.DOC_REPO_PATH_SEPARATOR 
							  + finalizationReport.getScQtFinalizationReportAttachment().getFilePath();
					  File file = new File(filePath);
					  if (null != file && file.exists()) {
						  file.delete();
					  }
					  finalizationReport.setScQtFinalizationReportAttachment(null);
				}
				
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				List<ScQtFinalizationReport> briefingPaperReportList = new ArrayList<>();
				// #18685 - Start. 
				finalizationReport.setCreatedBy(new FacesScopes().getUserSessionBean().getUserId());
				finalizationReport.setChangedBy(new FacesScopes().getUserSessionBean().getUserId());
                // #18685 - End. 
				briefingPaperReportList.add(finalizationReport);
				reqDTO.setAddNewBriefingPaperReportList(briefingPaperReportList );
				finalizationReport = qtSuppFinalizationSO.saveFinalizationReports(reqDTO);
				int index = 0;
				// #18685 - Start.
				for (ScQtFinalizationReport report : getManagedBean().getNewBriefingPaperReportList()) {
					if (report.getTitle().equalsIgnoreCase(finalizationReport.getTitle())) {
						getManagedBean().getNewBriefingPaperReportList().set(index, finalizationReport);
						getManagedBean().setSelectedBriefingReport(finalizationReport);
						break;
					}
					index++;
				}
				// #18685 - Start.
			} finally {
				qtSuppFinalizationSO = null;
			}
		}
		// #28215 - End
		
		//#25729 #142 start - checking total indicative price <= 20k
		/**
		 * Get Total Indicative Price threshold amount
		 * 
		 * @return	the panel Total Indicative Price amount
		 */
		protected BigDecimal getTotalIndPriceThresholdAmount() {
			IRuleSO ruleSO = null;
			BigDecimal indPriceThreshold = new BigDecimal(0);

			try {
				ruleSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

				RuleReqDTO ruleReqDTO = new RuleReqDTO();
				ruleReqDTO.setRuleType(RuleTypeConst.QTOTAL_INDI_PRICE_THRESHOLD);
				RuleResDTO ruleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
				
				if (ruleResDTO.isSuccessful()) {
					if(ruleResDTO.getRuleValueList()!=null && ruleResDTO.getRuleValueList().size()>0) {
						indPriceThreshold = new BigDecimal(ruleResDTO.getRuleValueList().get(0).getRuleValue());
					} else {
						throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
					}	
				}
				
			} finally {
				ruleSO = null;
			}

			return indPriceThreshold;
		}
		
		//QT threshold RM20k - 50k
		public boolean expectTotalIndPriceBetweenThreshold(ScQt scQt) {
			BigDecimal tmpExpectedIntendedPrice = scQt.getTmpExpectedIntendedPrice();
			
			if (!scQt.isPanel() && tmpExpectedIntendedPrice != null) {
				log.info(":::expectTotalIndPriceBetweenThreshold() scQt.isPanel(): "+ scQt.isPanel() +" :::");
				log.info(":::expectTotalIndPriceBetweenThreshold() tmpExpectedIntendedPrice: "+ tmpExpectedIntendedPrice +" :::");
				log.info(":::expectTotalIndPriceBetweenThreshold() tmpExpectedIntendedPrice.doubleValue: "+ tmpExpectedIntendedPrice.doubleValue() +" :::");

				if (SourcingConstants.QT_BELOW_20K <= tmpExpectedIntendedPrice.doubleValue() &&
						SourcingConstants.QT_BELOW_50K >= tmpExpectedIntendedPrice.doubleValue()) {

					return Boolean.TRUE;
				}
			}
			
			return Boolean.FALSE;
		}
		//#25729 #142 end
		
		// #25638 - Start
		public void displayReviseInstrn(List<ScQtCommitteeActivity> committeeList) {
			if (null != getManagedBean().getCurrentStatusId()) {
				long currentStatus = getManagedBean().getCurrentStatusId().longValue();
				if ((currentStatus == QTStatusConst.SF_PENDING_ENDORSEMENT.longValue())
						|| (currentStatus == QTStatusConst.SF_PENDING_SIGN_OFF.longValue())) {
					for (ScQtCommitteeActivity committeeActivity : committeeList) {
						if (null != committeeActivity && null != committeeActivity.getScCommitteeMember()
								&& null != committeeActivity.getScCommitteeMember().getMemberRole()
								&& "S".equalsIgnoreCase(committeeActivity.getScCommitteeMember().getMemberRole().getParameterCode())) {
							PmUser secretaryUser = committeeActivity.getScCommitteeMember().getPmUser();
							if (secretaryUser.getUserId().longValue() != new FacesScopes().getUserSessionBean().getUserId()) {
								getManagedBean().setDisplayReviseInstrn(true);
							}
							break;
						}
					}
				}
			}
		}
		// #25638 - End
		
		//29194 - Start
		/**
		 * To get userRole Code
		 * 
		 * @return String roleCode
		 */
		public String getUserRoleCodeSQ() {
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
				
				if (pmRoleCodes.contains(RoleCode.REQUISITIONER.value())) {
					roleCode = RoleCode.REQUISITIONER.value();
				}else{
					for (String role : roleCodes) {
						if (pmRoleCodes.contains(role)) {
							roleCode = role;
							break;
						}
					}
				}		
			}
			return roleCode;
		}
		//29194 - End
		
		//25037 - Start
		public boolean validateSupportedTask(String docNo, Long validityId){
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			ICompositeValiditySO compositeSO = (ICompositeValiditySO)  BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICompositeValiditySO.class);
			
			CompositeValidityReqDTO cReqDTO = new CompositeValidityReqDTO();
			cReqDTO.setCompositeValidityId(validityId);
			String minVersion = compositeSO.getCompositeByValidityId(cReqDTO).getPmCompositeValidity().getMinCompositeVersion();
			
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setDocNumber(docNo);
			WorklistVO worklist = bpmSO.getTasksByDocNumber(reqDTO).getWorklistVO();
			worklist = worklist == null ? new WorklistVO(): worklist;
			String version = null;
			if(CollectionUtils.isNotEmpty(worklist.getWorklistItem())){
				for(WorklistItemVO i: worklist.getWorklistItem()){
					if(StringUtils.isNotEmpty(i.getInstanceVersion())){
						version = i.getInstanceVersion();
						break;
					}
				}
			}
			if(version!=null){
				return CustomUtils.compositeVersionComparator(version, minVersion)>=0;
			}
			return false;
		}
		public String[] getRoleFilterListForCommitteeMaintenance(ScQt scQt, String committeeType){	
			String[] arr = new String[0];
			List<String> list = new ArrayList<String>();
			if(scQt == null){
				return arr;
			}
			IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			reqDTO.setDocId(scQt.getQtId());
			
			ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
			if(wfStatus==null){
				return arr;
			}
			Long statusId = wfStatus.getPmStatus().getStatusId();	
			if(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE.contentEquals(committeeType)){
				if(Arrays.asList(QTStatusConst.QT_SPEC_COMM_UPDATE_GROUP).contains(statusId)){
					//25037 #372 -Start
					Long[] secretaryExceptSpecCommittee = new Long[]{
							QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, 
							QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL,
							QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL_RESCHEDULE,
							QTStatusConst.QT_REVISION_PUBLICATION,
							QTStatusConst.QT_PENDING_SCHEDULE_REVISION};
					//25037 #372 -end

					if(!Arrays.asList(secretaryExceptSpecCommittee).contains(statusId)){
						list.add(ParameterConst.COMMITTEE_ROLE_SECRETARY);
					}
					if(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF.equals(statusId)){
						list.add(ParameterConst.COMMITTEE_ROLE_MEMBER);
					}
				}	
			}else{
				if(this.isStatusGreaterThanOrEquals(statusId, QTStatusConst.QT_RE_EVALUATION, QTStatusConst.QT_ORDERED_STATUSES)){
					boolean isCommitteeEvalStage = false;
					if(StringUtils.isNotEmpty(committeeType)){
						reqDTO = new ScWorkFlowStatusReqDTO();
						reqDTO.setDocType(committeeType);
						reqDTO.setDocId(scQt.getQtId());
						wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
						Long evalStatusId = wfStatus!=null ? wfStatus.getPmStatus().getStatusId(): 0L;
						isCommitteeEvalStage = Arrays.asList(QTStatusConst.QT_EVAL_PREPARATION_GROUP).contains(evalStatusId) 
								|| Arrays.asList(QTStatusConst.QT_EVAL_FINALIZATION_GROUP).contains(evalStatusId);
					}
					
					if(isCommitteeEvalStage){
						list.add(ParameterConst.COMMITTEE_ROLE_SECRETARY);
					}
				}
			}
			if(CollectionUtils.isNotEmpty(list)){
				arr = new String[list.size()];
			    list.toArray(arr);
			}		
			return arr;
		}
		
		public List<ScQtCommitteRemarks> omitLatestRemarkForDisplay(List<ScQtCommitteRemarks> remarks){
			List<ScQtCommitteRemarks> omittedList = new ArrayList<ScQtCommitteRemarks>();
			if(CollectionUtils.isNotEmpty(remarks)){
				for(ScQtCommitteRemarks r: remarks){
					if(StringUtils.isNotEmpty(r.getRemark())){
						if(r.isEditable() || r.isBrandNew()){
							ScQtCommitteRemarks rn = new ScQtCommitteRemarks();
							rn.setRemark(r.getRemark());
							rn.setCreatedBy(r.getCreatedBy());
							rn.setCreatedUser(r.getCreatedUser());
							rn.setCreatedDate(r.getCreatedDate());
							rn.setChangedDate(r.getChangedDate());
							rn.setEditable(false);
							rn.setBrandNew(false);
							omittedList.add(rn);
						}else{
							omittedList.add(r);
						}
					}
				}
			}
			return omittedList;
		}

		@SuppressWarnings("unchecked")
		public List<QtCommitteeMemberBean> getMemberHistoryList(ScCommittee scCommittee){
			List<QtCommitteeMemberBean> list = new ArrayList<QtCommitteeMemberBean>();
			try{
				if(scCommittee!=null){
					List<ScCommitteeMemberHistory> histories = scCommittee.getScCommitteeMemberHistories();
					
					if(CollectionUtils.isNotEmpty(histories)){
						Collections.sort(histories, new Comparator<ScCommitteeMemberHistory>() {
							public int compare(ScCommitteeMemberHistory o1, ScCommitteeMemberHistory o2) {
								return o2.getCreatedDate().compareTo(o1.getCreatedDate());
							}
						});
						
						ScCommitteeMemberHistory h = histories.get(0);
						list = (List<QtCommitteeMemberBean>)(Object)h.deserializeList(h.getCommitteeMembers());
						CustomUtils.sortQtCommitteeMemberRole(list);
					}
				}
			}catch(Exception e){
				log.debug(e);
			}
			return list;
			
		}
		
		public List<String> getCurrentlyAssignedTaskNames(ScQt qt){
			List<String> taskNames = new ArrayList<String>();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			bpmReqDTO.setDocNumber(qt.getQtNo());
			bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
			BpmResponseDTO resDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
			if(resDTO.getWorklistVO()!=null){
				if(CollectionUtils.isNotEmpty(resDTO.getWorklistVO().getWorklistItem())){
					for(WorklistItemVO currentVO: resDTO.getWorklistVO().getWorklistItem()){
						if(StringUtils.isNotBlank(currentVO.getTaskName())){
							taskNames.add(currentVO.getTaskName());
						}
					}
				}
			}

			if(CollectionUtils.isEmpty(taskNames)){
				return null;
			}
			return taskNames;
		}		
		//25037 - End
		
		/*
		 * To check if Application fall under MOF Central Contract flows
		 * #27502		 
		 */
		public boolean isBpkCentralContract(ScQt scQt) {
			boolean isBpkCentralContract = false;
			boolean isSbhOrSrwk = false;
			boolean isRuleBpk = false;
			Long ctType = null;		
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			if (!scQt.getQtStatus().getStatusId().equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)
					&& !scQt.getQtStatus().getStatusId().equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)
					&& !Arrays.asList(QTStatusConst.CMT_APPROVAL_GROUP).contains(scQt.getQtStatus().getStatusId())) {//#25037
				
					IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
					List<String> stateCodeList = new ArrayList<String>();
					stateCodeList.add(ProfileManagementConst.STATE_CODE_SABAH);
					stateCodeList.add(ProfileManagementConst.STATE_CODE_SARAWAK);
					stateCodeList.add(ProfileManagementConst.STATE_CODE_LABUAN);
					
					for(String s:stateCodeList){
						List<Object[]> pjtcode = addressSO.getSabahOrSrwkPtj(scQt.getOrgProfileId(), s);
						
						if(pjtcode != null){
							isSbhOrSrwk = true;
							break;
						}					
					}	
					
					IRuleSO ruleSO = (IRuleSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
					List<Object[]> bpkList = ruleSO.getBpkByOrgProfileIdAndRuleType(scQt.getOrgProfileId(), RuleTypeConst.PTJCO);
					
					if(bpkList != null){
						isRuleBpk = true;
					}
					
					if (scQt.getContractType() == null) {
						QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
						qtBackingBean.retrieveQuationTenderDetails(scQt.getQtId());
						ctType = qtBackingBean.getManagedBean().getQt().getContractType().getParameterId();
					} else {
						ctType = scQt.getContractType().getParameterId();
					}

					if (isRuleBpk && ctType.equals(SourcingConstants.CENTRAL_CONTRACT) && !isSbhOrSrwk) {
						return isBpkCentralContract = true;
					}
				}
			
			return isBpkCentralContract;
		}
		
		// #18685-Start.
        /**
         * @param finalizationReport
         */
        public void closeScQtFinalizationReport(ScQtFinalizationReport finalizationReport) {
          IQTSuppFinalizationSO qtSuppFinalizationSO;
          // 18685 - Start.
          boolean reportFound = false;
          try {
            qtSuppFinalizationSO = BusinessDelegate.getInstance()
                .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
                        IQTSuppFinalizationSO.class);
  
            SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
            ScQtFinalization scQtFinalization = finalizationReport.getScQtFinalization();
            if (scQtFinalization == null) {
              scQtFinalization = new ScQtFinalization();
            }
            
            reqDTO.setScQtFinalization(scQtFinalization);
            List<Long> qtReportIds = new ArrayList<Long>();
            qtReportIds.add(finalizationReport.getQtReportId());
            reqDTO.setQtReportIdList(qtReportIds);
            reqDTO.setDocumentCategory(finalizationReport.getDocCategory());
        
            SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveFinalizationReport(reqDTO);
            
            if (null != resDTO && null != resDTO.getScQtFinalizationReports()
            		&& resDTO.getScQtFinalizationReports().size() > 0) {
            	ScQtFinalizationReport scQtFinalizationReport = resDTO.getScQtFinalizationReports().get(0);
            	int index = 0;
                for (ScQtFinalizationReport report : getManagedBean().getNewBriefingPaperReportList()) {
                  if (report.getTitle().equalsIgnoreCase(finalizationReport.getTitle())) {
                    getManagedBean().getNewBriefingPaperReportList().get(index).setReportContent(scQtFinalizationReport.getReportContent());
                    reportFound = true;
                    break;
                  }
                  index++;
                }
            }
            
          } finally {
	    	  if (!reportFound) {
	          	finalizationReport.setReportContent("");
	          }
              qtSuppFinalizationSO = null;
          }
          // 18685 - End.
     }
     // #18685-End.   
        
    // #11134 - Start
    protected <A extends ScRemark> List<A> filterSupplierRemarks(List<A> scRemarksIn, boolean includedBrandNew){
    	List<A> supplierRemarks = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(scRemarksIn)){
			
			List<Long> userIds = new ArrayList<>();
			for(ScRemark remark : scRemarksIn){
				if(remark != null && remark.getCreatedBy() != null
						&& (includedBrandNew || !remark.isBrandNew())){
					userIds.add(remark.getCreatedBy());
				}
			}
			
			// user id of supplier
			if(CollectionUtils.isNotEmpty(userIds)){
				userIds = getUserSupplierListByUserIds(userIds);
			}
			
			for(A remark : scRemarksIn){
				if(remark != null && remark.getCreatedBy() != null
						&& userIds.contains(remark.getCreatedBy())
						&& (includedBrandNew || !remark.isBrandNew())){
					supplierRemarks.add(remark);
				}
			}
		}
        return supplierRemarks;
    }
    protected List<Long> getUserSupplierListByUserIds(List<Long> userIds){
		List<Long> result = new ArrayList<>();
		
		IUserSO userSO = null;
		try{
			userSO = BusinessDelegate.getInstance()
					.getServiceObject(IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserIds(userIds);
			reqDTO.setRoleCodes(Arrays.asList(RoleCode.BASIC_SUPPLIER_ADMIN.value(), 
					RoleCode.BASIC_SUPPLIER_USER.value(), RoleCode.BASIC_SUPPLIER_ADMIN.value(),
					RoleCode.MOF_SUPPLIER_USER.value(), RoleCode.MOF_SUPPLIER_ADMIN.value()));
			
			
			UserResDTO resDTO = userSO.retrieveUserRoleListByUserIds(reqDTO);
			
			if(resDTO != null && resDTO.isSuccessful()){
				List<PmUserRole> pmUserRoleList = resDTO.getUserRoleList();
				
				if(CollectionUtils.isNotEmpty(pmUserRoleList)){
					for(PmUserRole each : pmUserRoleList){
						if(each.getPmUserOrg() != null && each.getPmUserOrg().getPmUser() != null){
							result.add(each.getPmUserOrg().getPmUser().getUserId());
						}
					}
				}
			}
			
		} finally {
			userSO = null;
		}
		
		return result;
	}
    // #11134 - end
    
    //#27927 - Start
    /**
  	 * Method to return true, if branch in supplier has been deleted
  	 * @param supplier id and scQt
  	 * @return	true or false
  	 **/
  	
  	public boolean localitySupplierBranchListDeleted(Long supplierId, ScQt scQt){ 
  		
  		 QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
  		 qtBackingBean.retrieveQuationTenderDetails(scQt.getQtId());
  		 ScQt qt = qtBackingBean.getManagedBean().getQt();
  		 ScQtSupplierCriteria scQtSupplierCriteria = qt.getScQtSupplierCriteria();
  			
  		 ISupplierSearchSO supplierSearchSO = null;
  		 IQTDetailSO QtDetailsSO = null;
  	     boolean isSupplierBranchListDeleted = false;
  		 List<SmSupplierBranch> supplierBranchListDeleted = new ArrayList<>();
  		 try{
  			 
  			 supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
  			 ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
  			 QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
  			 ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
  			 
  			 SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
  		 	 suppSearchReqDTO.setSupplierId(supplierId);
  		     SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retrieveSmSupplierBranchDeletedBySupplier(suppSearchReqDTO);
  		     supplierBranchListDeleted = suppSearchResDTO.getSmSupplierBranchList();
  		     
  		     if(scQtSupplierCriteria.isAllState()){
  		    	  for (SmSupplierBranch supplierBranch : supplierBranchListDeleted) {
  						if(supplierBranch.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)){
  							isSupplierBranchListDeleted = true;
  						}
  				 }
  		    	 
  		     }else{
  		    	 
  		    	 for (SmSupplierBranch supplierBranch : supplierBranchListDeleted) {
  						QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
  			    		tenderReqDTO.setQtdetail(qt);
  			    		QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
  			    		if(tenderResDTO != null && tenderResDTO.getLocationDetails()!= null){
  			    			for(TplSpecLoc location : tenderResDTO.getLocationDetails()){
  								if(supplierBranch.getSmAddress().getPmState().getStateId() != null && location.getPmState().getStateId() != null){
  			    					if(supplierBranch.getSmAddress().getPmState().getStateCode().equals(location.getPmState().getStateCode())){
  			    						if(supplierBranch.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)){
  				    						isSupplierBranchListDeleted = true;
  			    						}			    						
  			    					}	
  			    				}
  			    			}
  			    		}
  		    		}
  		    	 
  		     }
  			 
  		} finally {
  			supplierSearchSO = null;
  		}     
  		return isSupplierBranchListDeleted;
  	}
    //#27927 - End
  	
  	public boolean isQTCloneStatus(Long currentStatusId){//25635-start
  		List<Long> CheckStatus=new ArrayList<Long>();
  		CheckStatus.add(QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER);
  		CheckStatus.add(QTStatusConst.QT_CANCELED_DUETO_PROPOSALVALIDITY_EXPIRED);
  		CheckStatus.add(QTStatusConst.QT_CLOSE_PERMANENTLY);
  		CheckStatus.add(QTStatusConst.QT_CANCELED_DUETO_MINISTRY_RESTRUCTING);
  		CheckStatus.add(QTStatusConst.QT_PENDING_SPECIFICATION_COMMITTEE_APPOINTMENT);
  		return CheckStatus.contains(currentStatusId);
 
  	}//25635-End
  	
	public boolean isQTSampleStatus(Long currentStatusId){//11104-start
  		List<Long> CheckStatus=new ArrayList<Long>();
  		CheckStatus.add(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT);
  		CheckStatus.add(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF);
  		CheckStatus.add(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH);
  		CheckStatus.add(QTStatusConst.SE_PENDING_ENDORSMENT);
  		CheckStatus.add(QTStatusConst.SE_PENDING_SIGNOFF);
  		return CheckStatus.contains(currentStatusId);
 
  	}//11104-End

}