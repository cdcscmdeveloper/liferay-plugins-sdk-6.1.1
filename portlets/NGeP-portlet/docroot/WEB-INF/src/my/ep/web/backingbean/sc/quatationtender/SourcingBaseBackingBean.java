/**
 *  Copyright  2013 Archisoft. All rights reserved.
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
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.HashSet; //Bug #33988@Syazani - 27/09/2022
import java.util.Set; //Bug #33988@Syazani - 27/09/2022

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
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
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.bpm.ICompositeValiditySO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.schedulebidding.IScheduleBiddingSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.bpm.vo.WorklistVO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTSearchBeanConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateDocConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.BpmConstants;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.constants.UnicodeCharacterConstant;
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
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
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
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
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
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecTierQty;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.EvaluationReport;
import my.ep.util.CustomUtils;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.shared.PerformanceBondUtil;
import my.ep.util.type.MimeType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewSuppProfileBackingBean;
import my.ep.web.bean.sc.common.ZonesBean;	// Bug #36730@Syazani 07/01/2025
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.constant.SSTConstant;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentDefaultFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;	// Bug #36730@Syazani 07/01/2025
import my.ep.web.type.sc.quationtender.ApprovalStatus;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.VmUtil;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.CountingOutputStream;	// Bug #35485@Syazani 08/02/2024
import org.apache.commons.lang.ArrayUtils;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

//29661 -start-
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.biz.bean.sc.CompanyExpBean;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.biz.service.ct.termination.IContractTerminationSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.backingbean.sc.loa.QtLOABackingBean;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.persistence.entity.sm.SmCompExpMasterDoc;
import my.ep.persistence.entity.sc.loaloi.ScLoaForQt;
import my.ep.web.util.EpPortletUtil;
import my.ep.constant.MapKeyEnum;
import java.util.EnumMap;

//29661 -end-

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

	private final Map<String, String> recordStatusParamMap = new HashMap<>();
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
		// #25378 revert from HEAD -start-
//		ByteArrayInputStream fis = null;
//		OutputStream gzipOS = null;
		// #25378 revert from HEAD -end-

		try{
			if (fileBytes != null) {
//				fis= new ByteArrayInputStream(fileBytes); // #25378 revert from HEAD
				attachment.setFileName(fileName);
				attachment.setFileDesc("File uploded");
				populateAttachmentDetails(attachment);
				
//				attachment.setFilePath(attachment.getFilePath()+".gz"); // #25378 revert from HEAD
				
				fos = new FileOutputStream(getActualFilePath(attachment));
			
				fos.write(fileBytes);

				// #25378 revert from HEAD -start-
				//#25378 - danial
//				gzipOS =new GZIPOutputStream(fos){{def.setLevel(Deflater.BEST_COMPRESSION);}};
//	            byte[] buffer = new byte[1024];
//	            int len;
//	            while((len=fis.read(buffer)) != -1){
//	                gzipOS.write(buffer, 0, len);
//	            }
	         // #25378 revert from HEAD -end-
			}
		}
		finally{
			if(fos != null){
				// #25378 revert from HEAD -start-
//			if(fos != null && gzipOS!=null){
//				gzipOS.close();
				// #25378 revert from HEAD -end-
				fos.close();
//				fis.close(); // #25378 revert from HEAD
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

		// Bug #35485@Syazani 08/02/2024 -- Start
		if (attachment.getAttachmentId() == null
				|| (attachment.getAttachmentId() != null && attachment.getAttachmentId() == 0)) {
			attachment.setCreatedBy(createdUser.getUserId());
			attachment.setCreatedDate(date);
		}
		// Bug #35485@Syazani 08/02/2024 -- End
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
						fileName, scAttachment);	// Bug #35485@Syazani 08/02/2024
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
	 * @param scAttachment
	 * @return File instance
	 * @throws IOException
	 */
	public File downloadFile(String filePath, String fileName, ScAttachment scAttachment)	// Bug #35485@Syazani 08/02/2024
			throws IOException {
        long byteSize = 0;	// Bug #35485@Syazani 08/02/2024
		if (filePath != null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());

			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			byteSize = downloadFileByInputStream(input, fileName);	// Bug #35485@Syazani 08/02/2024
			
			// #25378 revert from HEAD -start-
//			boolean zipped=false;
//			
//			//#25378 - danial
//			if(filePath.endsWith(".gz")){
//				zipped=true;
//			}
//			downloadFileByInputStream(input, fileName,zipped);
			// #25378 revert from HEAD -end-
		}
         //input has been closed in the called method bcoz here in catch they alternative 
		// use to upload back to database
		File file = new File(filePath);
		scAttachment.setFileSize(byteSize);	// Bug #35485@Syazani 08/02/2024
		return file;
	}

	/**
	 * To download file by stream.
	 * 
	 * @param input
	 * @param fileName
	 * @throws IOException
	 */
	// Bug #35485@Syazani 08/02/2024 -- Start
	@SuppressWarnings("resource")
	protected long downloadFileByInputStream(InputStream input, String fileName)
			throws IOException {
	// Bug #35485@Syazani 08/02/2024 -- End
	// #25378 revert from HEAD -start-
//	protected void downloadFileByInputStream(InputStream input, String fileName,boolean zipped)
//			throws IOException {
	// #25378 revert from HEAD -end-
		log.info("Inside downloadFile(..)");
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		OutputStream out = null;
		// Bug #35485@Syazani 08/02/2024 -- Start
		CountingOutputStream countingStream = null;
		long bytesWritten = 0;
		// Bug #35485@Syazani 08/02/2024 -- End
		// #25378 revert from HEAD -start-
		//#25378 - danial
//		GZIPInputStream gis =null;
		// #25378 revert from HEAD -end-
		
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
			countingStream = new CountingOutputStream(out);	// Bug #35485@Syazani 08/02/2024
			
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				countingStream.write(buffer, 0, bytesRead);	// Bug #35485@Syazani 08/02/2024
				buffer = new byte[4096];
			}
			
			bytesWritten = countingStream.getByteCount();	// Bug #35485@Syazani 08/02/2024

			// #25378 revert from HEAD -start-
			//#25378 - danial
//			if(zipped){
//				gis= new GZIPInputStream(input);
//				while ((bytesRead = gis.read(buffer)) != -1) {
//					out.write(buffer, 0, bytesRead);
//					buffer = new byte[4096];
//				}
//			}else{
//				while ((bytesRead = input.read(buffer)) != -1) {
//					out.write(buffer, 0, bytesRead);
//					buffer = new byte[4096];
//				}
//			}
			// #25378 revert from HEAD -end-
			
			out.flush();
			FacesContext.getCurrentInstance().responseComplete();
		}
		finally{
			// #25378 revert from HEAD -start-
//			if(gis!=null){
//				gis.close();
//			}
			// #25378 revert from HEAD -end-

			if(input!= null){
				input.close();
			}
			if(out != null){
				out.close();
			}
		}
		return bytesWritten;	// Bug #35485@Syazani 08/02/2024
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
			ex.printStackTrace();
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
	//Bug #33726 start
	public PmUser retrieveUserDtlById(Long userId) {
		PmUser user = null;
		try {
			UserReqDTO reqDTo = new UserReqDTO();
			reqDTo.setUserId(userId);
			UserResDTO resDTO = getUserSOGlobal().retrieveUserDtlById(reqDTo);
			if (resDTO.isSuccessful() == true) {
				user = resDTO.getPmUser();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e); //SQUBE QT MINOR 60
		}

		return user;
	}
	//Bug #33726 end	

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

	//38343 - start
	protected IDocumentSO getDocumentSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
	}
	//38343 - end

	// 26247 - start
	protected IQTSuppFinalizationSO getQtSuppFinalizationSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSuppFinalizationSO.class);
	}

	protected IQTBriefingSO getQtBriefingSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTBriefingSO.class);
	}

	protected IScheduleBiddingSO getScheduleBiddingSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IScheduleBiddingSO.class);
	}
	// 26247 - end
	
	// 38523 - start
	protected IReasonSO getReasonSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
	}

	// 38523 - end
	
	// 12471 - start
	protected IScWorkFlowStatusSO getScWorkflowStatusSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
	}
	// 12471 - end
	/**
	 * To initialize remarks.
	 * 
	 * @param parent
	 * @param workFlowStatus
	 * @param scRemarks
	 * @param clazz
	 * @return ScRemark instance
	 */
	// 25037 - start - note 284 
	protected <PARENT_ENTITY extends BaseEntity, REMARK_CLASS extends ScRemark> List<REMARK_CLASS> initializeRemarks(
			PARENT_ENTITY parent, ScWorkFlowStatus workFlowStatus, List<REMARK_CLASS> scRemarks,
			Class<REMARK_CLASS> clazz) {
		return initializeRemarks(parent, workFlowStatus, scRemarks, clazz, false);
	}

	protected <PARENT_ENTITY extends BaseEntity, REMARK_CLASS extends ScRemark> List<REMARK_CLASS> initializeRemarks(
			PARENT_ENTITY parent, ScWorkFlowStatus workFlowStatus, List<REMARK_CLASS> scRemarks,
			Class<REMARK_CLASS> clazz, boolean isSameScreen) {
	// 25037 - end - note 284 
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
							!isDiffWithPrevWorkFlowStatus(workFlowStatus, scRemark) && !isSameScreen) {//25037 note 284 
						scRemark.setEditable(true);
					}
				}
			}

			FacesScopes facesScopes = new FacesScopes();
			if (scRemarks.size() == 0
					|| !compareUserName(prevRemark != null ?prevRemark.getCreatedUser()
							.getLoginId():null)//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
					|| isDiffWithPrevWorkFlowStatus(workFlowStatus, prevRemark)
					|| isSameScreen) {// 25037 - note 284

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
		String mimeType = "";	// Improvement #35657@Syazani 14/05/2024
		String reportUrl= generateReportURL(reportName,paramMap);
		log.info("reportUrl: " + reportUrl);

		if (log.isDebugEnabled()) {
			log.debug("reportUrl: " + reportUrl);
		}
		OutputStream out = null;
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
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
					mimeType = uc.getContentType();	// Improvement #35657@Syazani 14/05/2024
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
				
				// Improvement #35657@Syazani 14/05/2024 -- Start
				boolean mimeTypePDF = mimeType.equalsIgnoreCase(MimeType.pdf.contentType)
						|| mimeType.equalsIgnoreCase(MimeType.bin.contentType);
				if (!mimeTypePDF) {
					log.info("Report server return '" + mimeType + "' Type");
					log.info(bos.toString());
					throw new EpException(MessageConst.ERROR_760530);
				}
				// Improvement #35657@Syazani 14/05/2024 -- End
				
				byte[] bytes = bos.toByteArray();
				fos = new FileOutputStream(localFile);
				fos.write(bytes);
				fos.flush();
			}
			if (!isDirectDownload && out != null ) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
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
		Timestamp date = new Timestamp(System.currentTimeMillis());
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
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
			reqDTO.setCompareValue(estimatedAmount != null ? estimatedAmount.toString() : "0");	// Bug #36072@Syazani 22/07/2024
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
		TplSpecZoneDtl zoneDtlTmp = zoneDtl; //20230711@Asyrafhaziq-SQUBE Bug Minor #16
		if (null != treeNodes && !treeNodes.isEmpty()) {
			for (TreeNode treeNode : treeNodes) {
				TplSpecZoneDtl tplSpecZoneDtl = null;
				results = new ArrayList<TplSpecZoneLoc>();
				List<TreeNode> zoneNodes = treeNode.getChildren();
				// zoneDtl=(TplSpecZoneDtl)treeNode.getData();
				// zoneDtl = (TplSpecZoneDtl) zoneNodes.getData();
				
				zoneDtlTmp = (TplSpecZoneDtl) zoneNodes.get(0).getData(); //20230711@Asyrafhaziq-SQUBE Bug Minor #16
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
	
	//Enhancement #24259 #24269	#25726
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
			//20231122@asyrafhaziq_25726 - Issue 16
			/*
			 * #29766 Start Vinod updated
			 * getQTTypeItemInPK7AndPK7WPChecking to check 2 QT type
			 * codes and refactor old code.
			 */
			List<String> flTypeCodes = Arrays.asList(ParameterConst.FULFILMENT_TYPE_ONE_OFF,
					ParameterConst.FULFILMENT_TYPE_SCHEDULED,
					ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
			boolean isQTtypePK7 = null != scLoa.getQtType()
					&& null != scLoa.getQtType().getParameterId()
					&& ParameterConst.getQTTypeItemInPK7AndPK7WPChecking().contains(
							scLoa.getQtType().getParameterCode());
			boolean isFulfilmentType = null != scLoa.getScQt() && null != scLoa.getScQt().getFulfilmentType()
					&& null != scLoa.getScQt().getFulfilmentType().getParameterId()
					&& flTypeCodes.contains(scLoa.getScQt().getFulfilmentType().getParameterCode());

			if (isQTtypePK7 && isFulfilmentType) {
				// #29766 End Vinod
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
	//end Enhancement #24259 #24269	#25726

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
			overallCtAmount.setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024
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
			overallCtAmount.setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024
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
	
	// 25037 - start
	/**
	 * Determine state is currently in evaluation state based on status provided
	 * 
	 * @param currentStatusId
	 * @return flag to determine current state is in evaluation
	 */
	public boolean isQTStatusEvaluation(Long currentStatusId) {
		return isQTStatusGreaterThanOrEquals(currentStatusId, QTStatusConst.QT_EVALUATION_PROPOSAL);
	}

	// 25037 - end
	
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
	
	
	// 25037 - start
	/**
	 * Gets the role type dropdown values.
	 * 
	 * @param isChairPersonReq
	 * @param scqt
	 * @param user
	 * @return the list of role description
	 */
	public List<PmParameterDesc> getRoleTypeDropdownValuesBasedOnPTJ(boolean isChairPersonReq, ScQt scqt, PmUser user) {
		return getRoleTypeDropdownValuesBasedOnPTJ(isChairPersonReq, scqt, user, null);
	}

	/**
	 * Gets the role type dropdown values.
	 * 
	 * @param isChairPersonReq
	 * @param scqt
	 * @param user
	 * @param filteredRoles
	 * @return
	 */
	public List<PmParameterDesc> getRoleTypeDropdownValuesBasedOnPTJ(boolean isChairPersonReq, ScQt scqt, PmUser user,
			String[] filteredRoles) {
	// 25037 - end	
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
		// 25037 - start
		List<PmParameterDesc> list = retrieveParametersBySearchBean(ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		if (null == list)
			list = new ArrayList<>();

		if (filteredRoles != null) {
			for (PmParameterDesc role : list) {
				if (Arrays.asList(filteredRoles).contains(role.getPmParameter().getParameterCode())) {
					role.setSelectable(false);
				}
			}
		}
		return list;
		// 25037 - end
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
		if (null != scQt.getProposalStartDate() && null!=scQt.getPublishPeriod()) {//25635&33724
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
			if(null!=scQt.getQtValidityPeriod()){//25635&33724-proposal validity days null
			proposalValidityEndDate = DateUtils.addDays(clsDate,
					scQt.getQtValidityPeriod());
			}//25635&33724-proposal validity days null
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

		if (deductions != null && CollectionUtils.isNotEmpty(deductions)) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			for (ScQtDeduction deduction : deductions) {
				PmParameter deductionType = deduction.getDeductionType();
				if (null != deductionType
						&& null == deductionType.getParameterId()) {
					throw new EpException(MessageConst.ERROR_740035);
				} else {
					if (CollectionUtils.isEmpty(deduction
							.getScQtDeductionClauses())&& deductionType != null) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
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
							//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
							if (!deduction.equals(scQtDeduction) && 
								 deductionType != null && !(deductionType.getParameterCode().equals(
									ParameterConst.DEDUCTION_TYPE_FIXED)
									|| deductionType
											.getParameterCode()
											.equals(ParameterConst.DEDUCTION_TYPE_OTHERS) || deductionType
											.getParameterCode()
											.equals(ParameterConst.DEDUCTION_TYPE_NON_FIXED))&&
										deduction.getDeductionType()
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
		
		// #35320 - Start
		else if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_QC);
		}// #35320 - End
		
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
		List<TplSpecMaster> masters = new ArrayList<>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(scLoa.getScLoiLoa().getDocId());
			reqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			reqDTO.setPrepareSpec(true);
			// #21695 - Start.
			reqDTO.setScLoa(scLoa);
			// #21695 - End.
			SpecificationResDTO resDto = specSO.retrieveSpecDetails(reqDTO);
			String dafaltValue = SSTConstant.TB;
			if (CommonConstants.DEFAULT_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
				dafaltValue = SSTConstant.NA;
			}
			List<ScQtCheckList> scQtCheckLists = resDto.getSpecResultBean().getResultList();
			
			// #26130 - Start
			
			/*if (CollectionUtils.isNotEmpty(scQtCheckLists)) {
				scQtCheckLists.addAll(resDto.getFinSpecResultBean().getResultList());
			} else {
				scQtCheckLists = resDto.getFinSpecResultBean().getResultList();
			}*/
			
			if (CollectionUtils.isNotEmpty(scQtCheckLists)) {
				loopScQtCheckListsConstructSupplierSpecificationAndRemarks(scQtCheckLists, masters, scLoa, dafaltValue);	// Bug #36438@Syazani - 24/10/2024
			}
			
			// #26130 - End
			
			context.put(SSTConstant.isZonal, scLoa.getScLoiLoa().isZonal());
			//#26130 - start 04072022
			context.put(SSTConstant.serviceScope, SSTConstant.TB);
			//#26130 - end 04072022
			context.put(SSTConstant.masters, masters);
			context.put(SSTConstant.bean, this);
		} finally {
			specSO = null;
		}
	}
	
	// Bug #36438@Syazani - 24/10/2024 -- Start
	private void loopScQtCheckListsConstructSupplierSpecificationAndRemarks(List<ScQtCheckList> scQtCheckLists,
			List<TplSpecMaster> masters, ScLoa scLoa, String dafaltValue) {
		// Bug #36438@Syazani - 24/10/2024 -- End
		for (ScQtCheckList scQtCheckList : scQtCheckLists) {
			List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<>();
			TplSpecMaster master = null;
			// Bug #36438@Syazani - 24/10/2024 -- Start
			// Making sure it didn't impact original value
			if (null != scQtCheckList.getScQtDoc() && null != scQtCheckList.getScQtDoc().getTplSpecMaster()) {
				master = (TplSpecMaster) scQtCheckList.getScQtDoc().getTplSpecMaster().cloneEntity();
			}
			// Bug #36438@Syazani - 24/10/2024 -- End

			if (master != null) { // Bug #36438@Syazani - 24/10/2024
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
					// 26130 - 26052023
					// #26130 - Start.
					if (validateTplSpecMaster(scLoa, master)) {
						// #26130 - Start.
						constructSpecData(scLoa, master, question);
						// #26130 - End.
						constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master, question);
						// #26130 start
						if (validateTplSpecQuestion(scLoa, question)) {
							question.setListCombineZonal(getCOmbineZonalSPec(question, master));
							// #26130 end
						}
					}
					// 26130 - 26052023
				}
				// 26130 - End.
				master.setTplSpecQuestions(newTplSpecQuestionsList);
				masters.add(master);
			} // Bug #36438@Syazani - 24/10/2024
		}
	}	// Bug #36438@Syazani - 24/10/2024
	
	//Bug #36104 start
	private void getAnswerRemarkItem(ScLoa scLoa, List<String> answerRemarkList, TplSpecQuestion tplSpecQuestion) {

		SmSupplier suppLoa = scLoa.getScLoiLoa().getSmSupplier();
		if(CollectionUtils.isNotEmpty(tplSpecQuestion.getTplSpecAnswers())){		
			for (TplSpecAnswer tplSpecAnswer : tplSpecQuestion.getTplSpecAnswers()) {
				
				SmSupplier suppAns = new SmSupplier();
			
				if(null != tplSpecAnswer.getTplSpecSuppAnswer() 
						&& null != tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier()){
					suppAns = tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier();
				}
				//Bug #39043 start
				if (null != suppAns.getSupplierId()
						&& suppAns.getSupplierId().longValue() == suppLoa.getSupplierId().longValue()){   
						//&& null != tplSpecAnswer.getAnswerRemark()){ Bug #39043 end				
					answerRemarkList.add(tplSpecAnswer.getAnswerRemark());
					break;
				}
			}
		}
	}
	//Bug #36104 end
	
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
					
					getAnswerRemarkItem(scLoa, answerRemarkList, tplSpecQuestion); //Bug #36104 
					
					addSpecList = true;
					continue;
				}
				if (addSpecList) {
					if (tplSpecQuestion.getQuestionType().equals(SSTConstant.specification)) {
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
									//#26130 - start 04072022
									if (tplSpecAnswer.getAnswerCompliance()) {
										feedback = SSTConstant.Ya;
									} else {
										feedback = SSTConstant.Tidak;
									}
									//#26130 - end 04072022
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
			if (specList != null && CollectionUtils.isNotEmpty(specList)) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
				if (CollectionUtils.isNotEmpty(question.getTplSpecZoneQties())) {
					for (int i=0; i < question.getTplSpecZoneQties().size(); i++) {
						if (feedbackList != null && CollectionUtils.isNotEmpty(feedbackList)) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
							feedbackList.add(i, SSTConstant.whiteSpace);
						}
						if (answerRemarkList != null && CollectionUtils.isNotEmpty(answerRemarkList) && answerRemarkList.size() != (question.getTplSpecZoneQties().size() + specList.size())) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
							answerRemarkList.add(i, SSTConstant.whiteSpace);
						}
					}
				} else if (feedbackList != null && CollectionUtils.isNotEmpty(feedbackList)) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
						feedbackList.add(0, SSTConstant.whiteSpace);				
				}
			}
			// #26130 - End
			question.setFeedbackList(feedbackList);
			question.setAnswerRemarkList(answerRemarkList);
			
			//#26130 - zaim start
			
			List<Map<String, String>> listCombine = null;
		
		if (listCombine == null){
			listCombine = new ArrayList<>();
		}
		
		if (specList != null){
			int tempFeedbackIndex = 0;
			int tempRemarkIndex = 0;
			int tempSpecZoneQty = 0;
			int indexFeedbackStore = 0;
			int indexRemarkStore = 0;
			if (CollectionUtils.isNotEmpty(question.getTplSpecZoneQties())){
				tempSpecZoneQty = question.getTplSpecZoneQties().size();
			}
			
			for(String tempSpecVal: specList){
				HashMap<String, String> map = new HashMap<>();
				map.put(SSTConstant.specValue, tempSpecVal);
				//[Start] - Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
				if (feedbackList != null && CollectionUtils.isNotEmpty(feedbackList)) { // 20230712@asyrafhaziq- SQUBE Bug NullPointer#3
				    for (String tempFeedback : feedbackList) {
				        boolean shouldBreak = false;

				        if (tempSpecZoneQty > 0) {
				        	//[Start] - #36325- Sqube DP Code Smell Major G100
				        	if (tempFeedbackIndex >= tempSpecZoneQty && (indexFeedbackStore == 0 || tempFeedbackIndex > indexFeedbackStore)) {
				        	    map.put(SSTConstant.Feedback, tempFeedback);
				        	    indexFeedbackStore = tempFeedbackIndex;
				        	    shouldBreak = true;
				        	}
				        	//[End] - #36325- Sqube DP Code Smell Major G100
				        } else {
				            if (tempFeedbackIndex > 0 && tempFeedbackIndex > indexFeedbackStore) {
				                map.put(SSTConstant.Feedback, tempFeedback);
				                indexFeedbackStore = tempFeedbackIndex;
				                shouldBreak = true;
				            }
				        }

				        tempFeedbackIndex++;

				        if (shouldBreak) {
				            break;
				        }
				    }
				}
				if (answerRemarkList != null && CollectionUtils.isNotEmpty(answerRemarkList)) { // 20230712@asyrafhaziq- SQUBE Bug NullPointer#3
				    for (String tempRemark : answerRemarkList) {
				        boolean shouldBreak = false;

				        if (tempRemark == null) {
				            tempRemark = SSTConstant.TB;
				        } //else { Bug #39043
				            if (tempSpecZoneQty > 0) {
				            	//[Start] - #36326- Sqube DP Code Smell Major G100
				            	if (tempRemarkIndex >= tempSpecZoneQty && (indexRemarkStore == 0 || tempRemarkIndex > indexRemarkStore)) {
				                    map.put(SSTConstant.Remark, tempRemark);
				                    indexRemarkStore = tempRemarkIndex;
				                    shouldBreak = true;
				                }
				            	//[End] - #36326- Sqube DP Code Smell Major G100
				            } else {
				                if (indexRemarkStore >= 0 && tempRemarkIndex > indexRemarkStore) {
				                    map.put(SSTConstant.Remark, tempRemark);
				                    indexRemarkStore = tempRemarkIndex;
				                    shouldBreak = true;
				                }
				            }
				            tempRemarkIndex++;
				        //} Bug #39043

				        if (shouldBreak) {
				            break;
				        }
				    }
				}
				//[End] - Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
					tempFeedbackIndex = 0;
					tempRemarkIndex = 0;
				listCombine.add(map);	
			}
			question.setListCombineSpec(listCombine);
		}
		//#26130 - zaim end
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
		Integer days = null;
		Long zoneId = new Long(0l);
		boolean isSpecificationEnable = false;
		boolean isWipData = false; // #29006
		// 35295 @zaim start
		List<List<Map<String, String>>> listConstructQuestion = new ArrayList<>();
		boolean isZonalAdded = Boolean.FALSE;
		String answerRemark = "";
		List<ScLoiLoaItem> listSSTItem = new ArrayList<>();
		Long tempRequestItemId = 0L;
		// 35295 @zaim end
		try {
			quoteSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQuoteSO.class);
			String supplierName = scLoa.getScLoiLoa().getSupplierName();
			//35295 @zaim start
			Long supplierId = scLoa.getScLoiLoa().getSmSupplier().getSupplierId();
			//35295 @zaim end
			QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
			quoteReqDTO.setQuoteId(scLoa.getScQuote().getQuoteId());
			quoteReqDTO.setQuoteId(scLoa.getScQuote().getQuoteId());
			quoteReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());

			if (scLoa.getScLoiLoa().getSmSupplier() != null) {
				quoteReqDTO.setSupplierId(scLoa.getScLoiLoa().getSmSupplier().getSupplierId());
				quoteReqDTO.setSmSupplier(scLoa.getScLoiLoa().getSmSupplier());
			}
			QuoteResDTO quoteResDTO = quoteSO.getQuoteDtlForLOASupplier(quoteReqDTO);

			List<ScLoiLoaItem> items = scLoa.getScLoiLoaItems();
			String dafaltValue = SSTConstant.TB;
			if (!masters.isEmpty()) { //Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
				masters.clear();
			}
			for (ScLoiLoaItem item : items) {
				Long itemId = item.getScRequestItem().getTplSpecQuestion().getSpecQuestionId();
				if (CollectionUtils.isNotEmpty(quoteResDTO.getServiceAnswersTpl())) {
					TplSpecMaster master = null;
					List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<TplSpecQuestion>();

					for (TplSpecAnswer answer : quoteResDTO.getServiceAnswersTpl()) {
						if (answer.getTplSpecQuestion().getTplSpecMaster().getTplSpecZone() != null) {
							zoneId = answer.getTplSpecQuestion().getTplSpecMaster().getTplSpecZone().getSpecZoneId();
						} else {
							zoneId = 0l; // Long Object initialization
						}

						if (answer.getTplSpecQuestion().getClFulfilmentTypeCode() == null) {
							answer.getTplSpecQuestion().setClFulfilmentTypeCode(
									scLoa.getScLoiLoa().getFulfilmentTypeCode());
						}
						if (days == null) {
							days = answer.getTplSpecQuestion().getClDeliveryTerm();
						} else {
							if (!days.equals(answer.getTplSpecQuestion().getClDeliveryTerm())
									&& (answer.getTplSpecQuestion().getClDeliveryTerm() != null)) {
								days = answer.getTplSpecQuestion().getClDeliveryTerm();
							}
						}
						
						if (answer.getAnswerRemark() != null) {
							answer.getTplSpecQuestion().setAnswerRemarks(answer.getAnswerRemark());
						} else {
							answer.getTplSpecQuestion().setAnswerRemarks(dafaltValue);
						}

						isWipData = answer.getIsWip();// #29006

						if ((days.equals(answer.getTplSpecQuestion().getClDeliveryTerm()))
								&& (answer.getTplSpecQuestion().getClItem() != null)) {
							// #25025 - Start fixing sonarlint
							List<String> specList = null;

							for (TplSpecAnswer tplSpecAns : quoteResDTO.getProductAnswersTpl()) {
								if (tplSpecAns == null || tplSpecAns.getTplSpecQuestion() == null) {
									continue; // Skip null values to prevent
												// NullPointerException

								}

								if (Objects.equals(tplSpecAns.getTplSpecQuestion().getSpecQuestionId(), answer
										.getTplSpecQuestion().getSpecQuestionId())
										&& SSTConstant.specification.equals(tplSpecAns.getTplSpecQuestion()
												.getQuestionType())
										&& tplSpecAns.getTplSpecQuestion().getClSpecDetail() != null) {

									if (specList == null) {
										specList = new ArrayList<>();
									}

									specList.add(tplSpecAns.getTplSpecQuestion().getClSpecDetail());

								}
							}
							answer.getTplSpecQuestion().setSpecList(specList);
							// #25025 - End fixing sonarlint


							/** This code is for LOA with Single Item Details **/

							if (items.size() == 1) {
								if (itemId.equals(answer.getTplSpecQuestion().getSpecQuestionId())
										|| (answer.getTplSpecQuestion().getParentSpecQuestion() != null && itemId
												.equals(answer.getTplSpecQuestion().getParentSpecQuestion()
														.getSpecQuestionId()))) {
									master = answer.getTplSpecQuestion().getTplSpecMaster();
									if (master.getTplSpecZone() != null) {
										for (int i = 0; i < master.getTplSpecZone().getTplSpecZoneDtls().size(); i++) {
											TplSpecZoneDtl tplspecZoneDtls = master.getTplSpecZone()
													.getTplSpecZoneDtls().get(i);
											tplspecZoneDtls.getZoneName();
										}
									}
									master.setSpecCreatedBySQ(Boolean.TRUE); //36122 - @zaim - add flag to indicate data from DP 
									constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master,
											answer.getTplSpecQuestion());
									// #26226 - Start
									populateSpecAnswers(answer);
									// #26226 - End
									master.setTplSpecQuestions(newTplSpecQuestionsList);
									isSpecificationEnable = true;
								}
							} else {
								/**
								 * This code is for LOA with multiple Items
								 * Details
								 **/
								if (days.equals(answer.getTplSpecQuestion().getClDeliveryTerm())) {
									master = answer.getTplSpecQuestion().getTplSpecMaster();
									if (master.getTplSpecZone() != null) {
										for (int i = 0; i < master.getTplSpecZone().getTplSpecZoneDtls().size(); i++) {
											TplSpecZoneDtl tplspecZoneDtls = master.getTplSpecZone()
													.getTplSpecZoneDtls().get(i);
											tplspecZoneDtls.getZoneName();
										}
									}
									master.setSpecCreatedBySQ(Boolean.TRUE); //36122 - @zaim - add flag to indicate data from DP
									constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master,
											answer.getTplSpecQuestion());
									// #26226 - Start
									populateSpecAnswers(answer);
									// #26226 - End
									master.setTplSpecQuestions(newTplSpecQuestionsList);
									isSpecificationEnable = true;
								}
							}
						} else if (isSpecificationEnable && answer.getTplSpecQuestion().getClSpecDetail() != null) {
							/**
							 * This code is for LOA with Single Item
							 * specification
							 **/
							if (items.size() == 1) {
								if (itemId.equals(answer.getTplSpecQuestion().getSpecQuestionId())
										|| (answer.getTplSpecQuestion().getParentSpecQuestion() != null && itemId
												.equals(answer.getTplSpecQuestion().getParentSpecQuestion()
														.getSpecQuestionId()))) {

									master = answer.getTplSpecQuestion().getTplSpecMaster();
									master.setSpecCreatedBySQ(Boolean.TRUE); //36122 - @zaim - add flag to indicate data from DP
									constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master,
											answer.getTplSpecQuestion());
									// #26226 - Start
									populateSpecAnswers(answer);
									// #26226 - End
									master.setTplSpecQuestions(newTplSpecQuestionsList);
									// #25025 - Start.
									isSpecificationEnable = true;
									// #25025 - End.
								}
							} else {
								/**
								 * This code is for LOA with multiple Items
								 * specification
								 **/
								if (answer.getTplSpecQuestion().getClDeliveryTerm() == null) {
									master = answer.getTplSpecQuestion().getTplSpecMaster();
									master.setSpecCreatedBySQ(Boolean.TRUE); //36122 - @zaim - add flag to indicate data from DP
									constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master,
											answer.getTplSpecQuestion());
									// #26226 - Start
									populateSpecAnswers(answer);
									// #26226 - End
									master.setTplSpecQuestions(newTplSpecQuestionsList);
									// #25025 - Start.
									isSpecificationEnable = true;
									// #25025 - End.
								}
							}
						}
					}
					if (master != null) {
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
				//start - #29006 note 794
				if (CollectionUtils.isNotEmpty(quoteResDTO.getProductAnswersTpl())) {

					TplSpecMaster master = null;
					List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<>(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]

					for (TplSpecAnswer answer : quoteResDTO.getProductAnswersTpl()) {
						if (answer.getTplSpecQuestion().getTplSpecMaster().getTplSpecZone() != null) {
							zoneId = answer.getTplSpecQuestion().getTplSpecMaster().getTplSpecZone().getSpecZoneId();
						} else {
							zoneId = 0l; // Long Object initialization
						}

						if (answer.getTplSpecQuestion().getClFulfilmentTypeCode() == null) {
							answer.getTplSpecQuestion().setClFulfilmentTypeCode(
									scLoa.getScLoiLoa().getFulfilmentTypeCode());
						}
						if (days == null) {
							days = answer.getTplSpecQuestion().getClDeliveryTerm();
						} else {
							if (!days.equals(answer.getTplSpecQuestion().getClDeliveryTerm())
									&& (answer.getTplSpecQuestion().getClDeliveryTerm() != null)) {
								days = answer.getTplSpecQuestion().getClDeliveryTerm();
							}
						}
						
						if (answer.getAnswerRemark() != null) {
							answer.getTplSpecQuestion().setAnswerRemarks(answer.getAnswerRemark());
						} else {
							answer.getTplSpecQuestion().setAnswerRemarks(dafaltValue);
						}

						isWipData = answer.getIsWip();// #29006

						if ((days.equals(answer.getTplSpecQuestion().getClDeliveryTerm()))
								&& (answer.getTplSpecQuestion().getClItem() != null)) {
							// #25025 - Start fixing sonarlint
							List<String> specList = null;

							for (TplSpecAnswer tplSpecAns : quoteResDTO.getProductAnswersTpl()) {
								if (tplSpecAns == null || tplSpecAns.getTplSpecQuestion() == null) {
									continue; // Skip null values to prevent
												// NullPointerException
								}
								if (Objects.equals(tplSpecAns.getTplSpecQuestion().getSpecQuestionId(), answer
										.getTplSpecQuestion().getSpecQuestionId())
										&& SSTConstant.specification.equals(tplSpecAns.getTplSpecQuestion()
												.getQuestionType())
										&& tplSpecAns.getTplSpecQuestion().getClSpecDetail() != null) {

									if (specList == null) {
										specList = new ArrayList<>();
									}

									specList.add(tplSpecAns.getTplSpecQuestion().getClSpecDetail());
								}
							}
							answer.getTplSpecQuestion().setSpecList(specList);
							// #25025 - End fixing sonarlint

							/** This code is for LOA with Single Item Details **/

							if (items.size() == 1) {
								if (itemId.equals(answer.getTplSpecQuestion().getSpecQuestionId())
										|| (answer.getTplSpecQuestion().getParentSpecQuestion() != null && itemId
												.equals(answer.getTplSpecQuestion().getParentSpecQuestion()
														.getSpecQuestionId()))) {
									master = answer.getTplSpecQuestion().getTplSpecMaster();
									if (master.getTplSpecZone() != null) {
										for (int i = 0; i < master.getTplSpecZone().getTplSpecZoneDtls().size(); i++) {
											TplSpecZoneDtl tplspecZoneDtls = master.getTplSpecZone()
													.getTplSpecZoneDtls().get(i);
											tplspecZoneDtls.getZoneName();
										}
									}
									master.setSpecCreatedBySQ(Boolean.TRUE); //36122 - @zaim - add flag to indicate data from DP
									constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master,
											answer.getTplSpecQuestion());
									// #26226 - Start
									populateSpecAnswers(answer);
									// #26226 - End
									master.setTplSpecQuestions(newTplSpecQuestionsList);
									isSpecificationEnable = true;
								}
							} else {
								/**
								 * This code is for LOA with multiple Items
								 * Details
								 **/
								if (days.equals(answer.getTplSpecQuestion().getClDeliveryTerm())) {
									master = answer.getTplSpecQuestion().getTplSpecMaster();
									if (master.getTplSpecZone() != null) {
										for (int i = 0; i < master.getTplSpecZone().getTplSpecZoneDtls().size(); i++) {
											TplSpecZoneDtl tplspecZoneDtls = master.getTplSpecZone()
													.getTplSpecZoneDtls().get(i);
											tplspecZoneDtls.getZoneName();
										}
									}
									master.setSpecCreatedBySQ(Boolean.TRUE); //36122 - @zaim - add flag to indicate data from DP
									constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master,
											answer.getTplSpecQuestion());
									// #26226 - Start
									populateSpecAnswers(answer);
									// #26226 - End
									master.setTplSpecQuestions(newTplSpecQuestionsList);
									isSpecificationEnable = true;
								}
							}
						} else if (isSpecificationEnable && answer.getTplSpecQuestion().getClSpecDetail() != null) {
							/**
							 * This code is for LOA with Single Item
							 * specification
							 **/
							if (items.size() == 1) {
								if (itemId.equals(answer.getTplSpecQuestion().getSpecQuestionId())
										|| (answer.getTplSpecQuestion().getParentSpecQuestion() != null && itemId
												.equals(answer.getTplSpecQuestion().getParentSpecQuestion()
														.getSpecQuestionId()))) {

									master = answer.getTplSpecQuestion().getTplSpecMaster();
									master.setSpecCreatedBySQ(Boolean.TRUE); //36122 - @zaim - add flag to indicate data from DP
									constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master,
											answer.getTplSpecQuestion());
									// #26226 - Start
									populateSpecAnswers(answer);
									// #26226 - End
									master.setTplSpecQuestions(newTplSpecQuestionsList);
									// #25025 - Start.
									isSpecificationEnable = true;
									// #25025 - End.
								}
							} else {
								/**
								 * This code is for LOA with multiple Items
								 * specification
								 **/
								if (answer.getTplSpecQuestion().getClDeliveryTerm() == null) {
									master = answer.getTplSpecQuestion().getTplSpecMaster();
									
									constructTplSpecQuestion(dafaltValue, newTplSpecQuestionsList, master,
											answer.getTplSpecQuestion());
									// #26226 - Start
									populateSpecAnswers(answer);
									// #26226 - End
									master.setTplSpecQuestions(newTplSpecQuestionsList);
									// #25025 - Start.
									isSpecificationEnable = true;
									// #25025 - End.
								}
							}
						}
					}
					if (master != null) {
						if (masters.isEmpty()) { //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
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
				//end - #29006 note 794
				//35295 @zaim start
				if (tempRequestItemId.equals(0L) || !tempRequestItemId.equals(item.getScRequestItem().getRequestNoteItemId())) {
			        tempRequestItemId = item.getScRequestItem().getRequestNoteItemId();
			        listSSTItem.add(item);
			    }
				//35295 @zaim end
			}
			//35295 - start
			if (listSSTItem != null && !listSSTItem.isEmpty()) {
				for (ScLoiLoaItem sstItem : listSSTItem) {
					if ((quoteResDTO.getSpecMaster() != null && !quoteResDTO.getSpecMaster().isEmpty())) {
						for (TplSpecMaster specMaster : quoteResDTO.getSpecMaster()) {
							if (specMaster.getTplSpecQuestions() != null && !specMaster.getTplSpecQuestions().isEmpty()) {
								if ((specMaster.getTplSpecZone() != null && specMaster.getTplSpecZone()
										.getTplSpecZoneDtls() != null)
										&& !specMaster.getTplSpecZone().getTplSpecZoneDtls().isEmpty()) { // zonal
									// sorting
									Collections.sort(specMaster.getTplSpecZone().getTplSpecZoneDtls(),
											new Comparator<TplSpecZoneDtl>() {
												@Override
												public int compare(TplSpecZoneDtl o1, TplSpecZoneDtl o2) {
													return Long.compare(o1.getSpecZoneId(), o2.getSpecZoneId());
												}
											});
									Map<String, String> map = null;
									for (TplSpecQuestion q : specMaster.getTplSpecQuestions()) {
										List<Map<String, String>> listPRQuestion = new ArrayList<>();
										if (q.getSpecQuestionId().equals(
												sstItem.getScRequestItem().getTplSpecQuestion().getSpecQuestionId())
												|| (q.getParentSpecQuestion() != null && q
														.getParentSpecQuestion()
														.getSpecQuestionId()
														.equals(sstItem.getScRequestItem().getTplSpecQuestion()
																.getSpecQuestionId()))) {
											int zoneCount = specMaster.getTplSpecZone().getTplSpecZoneDtls().size();
											for (TplSpecZoneDtl zoneDtl : specMaster.getTplSpecZone()
													.getTplSpecZoneDtls()) {
												// retrieve item details for
												// first loop only

												map = new HashMap<>();
												if (q.getQuestionType().equalsIgnoreCase(
														ParameterConst.TPL_SPEC_QUESTION_TYPE_ITEM)
														&& !isZonalAdded) {
													map.put(SSTConstant.clItem, VmUtil.getInstance().escapeAndWrapText(q.getClItem(), 16,SSTConstant.FILE_TYPE_DOC));//35295 #124 @zaim
													map.put(SSTConstant.CL_FULFILMENT_TYPE_CODE,
															getParameterDesc(q.getClFulfilmentTypeCode(),
																	SourcingConstants.MY_LOCALE));
													map.put(SSTConstant.uomName, sstItem.getScRequestItem().getUom()
															.getUomName());
													//35295 - start #124
													map.put(SSTConstant.zoneName, VmUtil.getInstance().escapeAndWrapText(zoneDtl.getZoneName(), 11,SSTConstant.FILE_TYPE_DOC));
													//35295 - end
													map.put(SSTConstant.quantity, sstItem.getQty().toString());
													map.put(SSTConstant.clUomFrequency,
															q.getClUomFrequency() == null ? SSTConstant.TB : q
																	.getClUomFrequency().toString());
													map.put(SSTConstant.clUomPerDay,
															q.getClUomPerDay() == null ? SSTConstant.TB : q
																	.getClUomPerDay().toString());
													map.put(SSTConstant.clUomPerMonth,
															q.getClUomPerMonth() == null ? SSTConstant.TB : q
																	.getClUomPerMonth().toString());
													map.put(SSTConstant.CL_DURATION_MONTH,
															q.getClDurationMonth() == null ? SSTConstant.TB : q
																	.getClDurationMonth().toString());
													for (TplSpecAnswer ans : q.getTplSpecAnswers()) {
														if ((ans.getTplSpecQuestion().getSpecQuestionId()
																.equals(q.getSpecQuestionId()) && ans
																.getTplSpecSuppAnswer().getSmSupplier().getSupplierId()
																.equals(supplierId))
																&& ans.getTplSpecSuppAnswer().getTplSpecZoneDtl()
																		.getSpecZoneId()
																		.equals(zoneDtl.getSpecZoneId())) {
															answerRemark = ans.getAnswerRemark() == null ? SSTConstant.TB
																	: ans.getAnswerRemark();
															map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answerRemark, 13,SSTConstant.FILE_TYPE_DOC)); //35295 #124 @zaim
														}
													}
													isZonalAdded = Boolean.TRUE;

												} else if (q.getQuestionType().equalsIgnoreCase(
														ParameterConst.TPL_SPEC_QUESTION_TYPE_ITEM)
														&& isZonalAdded) {
													// second row zone name
													map.put(SSTConstant.clItem, SSTConstant.nbsp);
													map.put(SSTConstant.uomName, SSTConstant.nbsp);
													//35295 - start #124
													map.put(SSTConstant.zoneName, VmUtil.getInstance().escapeAndWrapText(zoneDtl.getZoneName(), 11,SSTConstant.FILE_TYPE_DOC));
													//35295 - end
													map.put(SSTConstant.quantity, SSTConstant.nbsp);
													map.put(SSTConstant.clUomFrequency, SSTConstant.nbsp);
													map.put(SSTConstant.clUomPerDay, SSTConstant.nbsp);
													map.put(SSTConstant.clUomPerMonth, SSTConstant.nbsp);
													map.put(SSTConstant.CL_DURATION_MONTH, SSTConstant.nbsp);
													map.put(SSTConstant.CL_FULFILMENT_TYPE_CODE, SSTConstant.nbsp);
													if (isWipData) {
														for (TplSpecAnswer ans : q.getTplSpecAnswers()) {
															if ((ans.getTplSpecQuestion().getSpecQuestionId()
																	.equals(q.getSpecQuestionId()) && ans
																	.getTplSpecSuppAnswer().getSmSupplier()
																	.getSupplierId().equals(supplierId))
																	&& ans.getTplSpecSuppAnswer().getTplSpecZoneDtl()
																			.getSpecZoneId()
																			.equals(zoneDtl.getSpecZoneId())) {
																answerRemark = ans.getAnswerRemark() == null ? SSTConstant.TB
																		: ans.getAnswerRemark();
																map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answerRemark, 13,SSTConstant.FILE_TYPE_DOC)); //35295 - #124 start
																break;
															}
														}
													}

												} else {
													map.put(SSTConstant.specValue, VmUtil.getInstance().escapeAndWrapText(q.getClSpecDetail(), 60,SSTConstant.FILE_TYPE_DOC)); //35295 #124 @zaim
													map.put(SSTConstant.questionType, q.getQuestionType());
													listPRQuestion.add(map);
													break;
												}
												map.put(SSTConstant.ROWSPAN_ITEM_NAME,
														String.valueOf(q.getListSpecQuestion().size() + zoneCount));
												map.put(SSTConstant.ROWSPAN_ZONE_QUANTITY, String.valueOf(zoneCount));
												map.put(SSTConstant.questionType, q.getQuestionType());
												listPRQuestion.add(map);
											}
											listConstructQuestion.add(listPRQuestion);
											isZonalAdded = Boolean.FALSE;
										}
									}
								} else { // non zonal
									Map<String, String> map;
									for (TplSpecQuestion q : specMaster.getTplSpecQuestions()) {
										List<Map<String, String>> listPRQuestion = new ArrayList<>();
										map = new HashMap<>();
										if (q.getQuestionType().equalsIgnoreCase(
												ParameterConst.TPL_SPEC_QUESTION_TYPE_ITEM)) {
											if (q.getSpecQuestionId()
													.equals(sstItem.getScRequestItem().getTplSpecQuestion()
															.getSpecQuestionId())) {

												map.put(SSTConstant.clItem, VmUtil.getInstance().escapeAndWrapText(q.getClItem(), 16,SSTConstant.FILE_TYPE_DOC)); //35295 #124 @zaim
												map.put(SSTConstant.CL_FULFILMENT_TYPE_CODE,
														getParameterDesc(q.getClFulfilmentTypeCode(),
																SourcingConstants.MY_LOCALE));
												map.put(SSTConstant.uomName, sstItem.getScRequestItem().getUom()
														.getUomName());
												map.put(SSTConstant.quantity, sstItem.getQty().toString());
												map.put(SSTConstant.clUomFrequency,
														q.getClUomFrequency() == null ? SSTConstant.TB : q
																.getClUomFrequency().toString());
												map.put(SSTConstant.clUomPerDay,
														q.getClUomPerDay() == null ? SSTConstant.TB : q
																.getClUomPerDay().toString());
												map.put(SSTConstant.clUomPerMonth,
														q.getClUomPerMonth() == null ? SSTConstant.TB : q
																.getClUomPerMonth().toString());
												map.put(SSTConstant.CL_DURATION_MONTH,
														q.getClDurationMonth() == null ? SSTConstant.TB : q
																.getClDurationMonth().toString());
												for (TplSpecAnswer ans : q.getTplSpecAnswers()) {
													if (ans.getTplSpecQuestion().getSpecQuestionId()
															.equals(q.getSpecQuestionId()) && ans
															.getTplSpecSuppAnswer().getSmSupplier().getSupplierId()
															.equals(supplierId)) {
														answerRemark = ans.getAnswerRemark() == null ? SSTConstant.TB
																: ans.getAnswerRemark();
														map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answerRemark, 13,SSTConstant.FILE_TYPE_DOC)); //35295 #124 @zaim
													}
												}
												map.put(SSTConstant.ROWSPAN_ITEM_NAME,
														String.valueOf(q.getListSpecQuestion().size() + SSTConstant.NON_ZONAL_ROW));
												map.put(SSTConstant.questionType, q.getQuestionType());
												listPRQuestion.add(map);
												listConstructQuestion.add(listPRQuestion);
											}
										} else {
											if (q.getParentSpecQuestion().getSpecQuestionId() != null
													&& q.getParentSpecQuestion()
															.getSpecQuestionId()
															.equals(sstItem.getScRequestItem().getTplSpecQuestion()
																	.getSpecQuestionId())) {
												map.put(SSTConstant.specValue, VmUtil.getInstance().escapeAndWrapText(q.getClSpecDetail(), 60,SSTConstant.FILE_TYPE_DOC)); //35295 #124 @zaim
												map.put(SSTConstant.questionType, q.getQuestionType());
												listPRQuestion.add(map);
												listConstructQuestion.add(listPRQuestion);
											}
										}
									}
								}
							}
						}
					}
					// 35295 @zaim end
				}
			}
			//35295 - end
			context.put(SSTConstant.serviceScope, dafaltValue);
			context.put(SSTConstant.SUPPLIER_NAME, supplierName);
			context.put(SSTConstant.ZONE_ID, zoneId);
			context.put(SSTConstant.masters, masters);
			context.put(SSTConstant.bean, this);
			context.put(SSTConstant.isZonal, scLoa.getScLoiLoa().isZonal());
			context.put("isWipData", isWipData); // #29006
			context.put("supplierId", scLoa.getScLoiLoa().getSmSupplier().getSupplierId()); // #29006
			context.put(SSTConstant.LIST_PURCHASE_REQUEST_SST,listConstructQuestion); //35295
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.DIRECT_PURCHASE, "GenerateSST", scLoa.getLoaNo(),
					"constructPRSupplierSpecificationAndRemarks", getUserIdSessionFromLoginId(), e);
		} finally {
			quoteSO = null;
			days = null;
			// masters.clear();
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
			SLAUtils.getInstance().logSLAError("DP", "populateSpecAnswers", answer.getSpecAnswerId().toString(),
					"populateSpecAnswers", getUserIdSessionFromLoginId(), e);
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
			//36122 - start @zaim - prevent data from dp execute due to performance in zonal.
			if (!master.isSpecCreatedBySQ()) {
				String clItem = question.getClItem();
				// 20190513@asyrafhaziq - 25812 - Start
				// add a condition to filter the character (character after
				// space and before space) length for data 'ITEM'.
				String[] words = clItem.split(" "); // get array of words
				StringBuilder sb = new StringBuilder();
				StringBuilder subSb; //Bug #36438@Syazani - 24/10/2024
				for (String word : words) // affect each word in string
				{
					int length = word.length();
					if (length >= 20) {
						// subSb reset here due to redundant text issue
						subSb = new StringBuilder();	//Bug #36438@Syazani - 24/10/2024
						String subWord;
						for (int i = 0; i < length; i += 20) {
							subWord = word.substring(i, Math.min(length, i + 20));
							subSb.append(subWord).append(TemplateDocConst.PARAM_SPACE);
						}
						word = subSb.toString();
					}
					sb.append(word).append(TemplateDocConst.PARAM_SPACE);
				}
				clItem = sb.toString();
				// 20190513@asyrafhaziq - 25812 - End
				clItem = clItem.replaceAll(QTConstants.NEW_LINE_CHARACTER, TemplateDocConst.PARAM_SPACE);
				clItem = clItem.replaceAll(QTConstants.AMBRA_SENT, TemplateDocConst.PARAM_SPACE);
				clItem = clItem.replaceAll(QTConstants.LESS_THEN, TemplateDocConst.PARAM_SPACE);
				clItem = clItem.replaceAll(QTConstants.GREATER_THEN, TemplateDocConst.PARAM_SPACE);
				question.setClItem(clItem);
			}
		    // 36122 - end @zaim
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
		if (newTplSpecQuestionsList != null) {
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
	 * @param specMastersParam 
	 */
	public void constructSupplierSpecificationAndRemarks(VelocityContext context, ScLoa scLoa, List<TplSpecMaster> specMastersParam) {	//Bug #36438@Syazani - 24/10/2024
		//Making sure it didn't impact original value
		List<TplSpecMaster> specMasters = CustomUtils.getInstance().deepCopyArrayList(specMastersParam);	//Bug #36438@Syazani - 24/10/2024
		List<TplSpecMaster> masters = new ArrayList<>();
		String dafaltValue =SSTConstant.TB;
		if (CommonConstants.DEFAULT_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			dafaltValue = SSTConstant.NA;
		}
		
		if (CollectionUtils.isNotEmpty(specMasters)) {
			for (TplSpecMaster master : specMasters) {
				if (CollectionUtils.isNotEmpty(master.getTplSpecQuestions())) {
					List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<>();
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
		context.put(SSTConstant.isZonal, scLoa.getScLoiLoa().isZonal());
		context.put(SSTConstant.serviceScope, dafaltValue);
		context.put(SSTConstant.masters, masters);
		context.put(SSTConstant.bean, this);
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
						QTConstants.EVALUATION_COMMITTEE_REPORT_ZIP_TITLE);	// Bug #35485@Syazani 08/02/2024

			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				ScAttachment attachment = scqt.getScOpenCommitteeReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						QTConstants.OPENING_COMMITTEE_REPORT_ZIP_TITLE);	// Bug #35485@Syazani 08/02/2024

				this.getManagedBean().setScAttachment(attachment);

			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				ScAttachment attachment = scqt.getScTechnicalEvaluationReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						QTConstants.TECHNICAL_EVALUATION_COMMITTEE_REPORT_ZIP_TITLE);	// Bug #35485@Syazani 08/02/2024

				this.getManagedBean().setScAttachment(attachment);

			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				ScAttachment attachment = scqt.getScFinancialEvaluationReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						QTConstants.FINANCIAL_EVALUATION_COMMITTEE_REPORT_ZIP_TITLE);	// Bug #35485@Syazani 08/02/2024

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
	 * @param scqt
	 * @param typeRegen
	 * @param scAttachment
	 * @throws Exception
	 *             the exception
	 */
	// Bug #35485@Syazani 08/02/2024 -- Start
	public void downloadAllReports(ScQt scqt, String typeRegen, ScAttachment scAttachment, boolean downloadable)
			throws Exception {
		// Bug #35485@Syazani 08/02/2024 -- End
		log.debug(this.getClass().getName() + ".downloadAllReports()");
		List<ScQtFinalizationReport> scQtFinalizationReports = getManagedBean()
				.getNewBriefingPaperReportList();
		List<ScAttachment> attachments = new ArrayList<ScAttachment>();
		String fileName = getManagedBean().getReportDownloadFileName();	// Bug #35485@Syazani 08/02/2024
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
		// Bug #35485@Syazani 08/02/2024 -- Start
		if ("evaluation".equalsIgnoreCase(typeRegen)) {
			ScAttachment attachment1 = regenerateDocumentEvaAttachmentByFileName(scqt, (ScAttachment) scAttachment.cloneEntity(), fileName);
			attachments.add(attachment1);
			scAttachment.setFileName(fileName + ".zip");
			scAttachment.setFileDesc("File uploded");
			scAttachment.setDocNo(scqt.getQtNo());
			scAttachment.setDocType(attachment1.getDocType());
			populateAttachmentDetails(scAttachment);
			scAttachment.setDocType(QTConstants.DOC_TYPE_SC_QT_FINALIZATION_ATTACHMENT);
			savedZipScAttachmentFiles(scAttachment, attachments);
			if (downloadable)
				downloadZipScAttachmentFiles(scAttachment);
			updateScAttachment(scAttachment);
		} else {
			attachments.add(generateDocumentAttachment(scqt, ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER));
			downloadAllFiles(this.getManagedBean().getReportDownloadFileName(),
					attachments);
		}
		// Bug #35485@Syazani 08/02/2024 -- End
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
	 * @param committeeType
	 * @param regen
	 */
	private void generateEvaluationPdf(ScAttachment attachment, ScQt qt, String committeeType)
			throws Exception {
		log.debug(this.getClass().getName() + ".generateEvaluationPdf()");
		//#35367 download all report, separate wip and new version
		IQTEvaluationSO evaluationSO;
		populateAttachmentDetails(attachment);
		evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		EvaluationReqDTO req4SerDTO = new EvaluationReqDTO();
		req4SerDTO.setQtId(qtId);
		int wipstatus = evaluationSO.checkWipScoreSetting(req4SerDTO);
		String reportName = StringUtils.EMPTY;
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(committeeType)) {
			if (wipstatus <= ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE) {
				reportName = ReportConst.SC_EC_RPT_WIP_JSP;
			} else {
				reportName = ReportConst.SC_EC_RPT_JSP;
			}
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(committeeType)) {
			if (wipstatus <= ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE) {
				reportName = ReportConst.SC_OC_RPT_WIP_JSP;
			} else {
				reportName = ReportConst.SC_OC_RPT_JSP;
			}
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(committeeType)) {
			if (wipstatus <= ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE) {
				reportName = ReportConst.SC_TEC_RPT_WIP_JSP;
			} else {
				reportName = ReportConst.SC_TEC_RPT_JSP;
			}
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(committeeType)) {
			if (wipstatus <= ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE) {
				reportName = ReportConst.SC_FEC_RPT_WIP_JSP;
			} else {
				reportName = ReportConst.SC_FEC_RPT_JSP;
			}
		}
		//#35367 end
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
						log.info("=======sourbasebackingBeanTitle===="+report.getTitle());
						if (getManagedBean().getEvaluationReportList() == null) {
							List<ScQtFinalizationReport> erList = new ArrayList<>();
							getManagedBean().setEvaluationReportList(erList);
						}
						// 18685 - START.                                     //#28784
						if (null != report.getPmDocument() || report.getTitle().equalsIgnoreCase(SourcingConstants.DEMO_EVALUATION_REPORT)) {
							if (SourcingConstants.EVALUATION_TYPE_TIER1.equals(evaluationType)) {
								if (SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT.equals(report.getPmDocument().getDocDefinitionKey())) {
									getManagedBean().getEvaluationReportList().add(report);
//									iterator.remove();// 37622 - remove code
								}
							} else if (SourcingConstants.EVALUATION_TYPE_TIER2.equals(evaluationType)) {
								//#28784
								if (SourcingConstants.DEMO_EVALUATION_REPORT.equalsIgnoreCase(report.getTitle())
										|| SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
										|| SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
										|| SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT.equals(report.getPmDocument().getDocDefinitionKey())
										|| SourcingConstants.DEMO_EVALUATION_REPORT.equalsIgnoreCase(report.getTitle())) {//#28784
									getManagedBean().getEvaluationReportList().add(report);
//									iterator.remove();// 37622 - remove code
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
		Set<String> validateDuplicateSets = null; //Bug #33988@Syazani - 27/09/2022
		if (null != scQtFinalizationReportList && scQtFinalizationReportList.size() > 0) {
			String reportNames = "";
			// #18685 - Start.
			resetUnSavedReportContent(scQtFinalizationReportList);
			// #18685 - End.
			
			//Bug #33988@Syazani -- Start 27/09/2022 
			if (taskType.equals(SourcingConstants.SUPPLIER_FINALIZATION_TASK)) {
				validateDuplicateSets = new HashSet<String>();
			}
			//Bug #33988@Syazani -- End 27/09/2022
			
			for (ScQtFinalizationReport scQtFinalizationReport : scQtFinalizationReportList) {
				
				//Bug #33988@Syazani -- Start 27/09/2022 
				//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				if (taskType.equals(SourcingConstants.SUPPLIER_FINALIZATION_TASK) && (validateDuplicateSets !=null && !validateDuplicateSets.add(scQtFinalizationReport.getTitle().toLowerCase().trim()))) { //Bug #33988@Syazani -- 01/03/2023
					throw new EpException(MessageConst.ERROR_775190, new String[] { scQtFinalizationReport.getTitle() }); //Bug #33988@Syazani - 06/12/2022
				}
				//Bug #33988@Syazani -- End 27/09/2022 
				
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
				repLoop: for (ScQtFinalizationReport repScQtFinalizationReport : resDTO.getScQtFinalizationReports()) {
					for (ScQtFinalizationReport scQtFinalizationReport : scQtFinalizationReportList) {
						//Bug #33988@Syazani -- Start 14/03/2023
						String repParaCode = repScQtFinalizationReport.getDefinationType() != null ? repScQtFinalizationReport
								.getDefinationType().getParameterCode() : "";
						String scQtParaCode = scQtFinalizationReport.getDefinationType() != null ? scQtFinalizationReport
								.getDefinationType().getParameterCode() : "";
						if (repScQtFinalizationReport.getTitle().equals(scQtFinalizationReport.getTitle())
								&& repParaCode.equals(scQtParaCode)) {
							//Bug #33988@Syazani -- End 14/03/2023
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
		} else {
			actualIntendedPrice = scQt.getEstimatedAmount();
		}

		if (!scQt.isPanel() && actualIntendedPrice != null) {
			// #25729 #174
			// Bug #35061 - asyrafhaziq
			// get total indicative price threshold validation value
			BigDecimal indPriceThreshold = getTotalIndPriceThresholdAmount();

			if ((actualIntendedPrice.compareTo(indPriceThreshold) <= 0)) {
				// Bug #35061 -end-
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
						  boolean deleteFile=file.delete();//#28215&18685-sonarQubeIssue4#54
						  log.info("======FileDeletedSucessFully======"+deleteFile);//#28215&18685-sonarQubeIssue4#54
					  }
					  finalizationReport.setScQtFinalizationReportAttachment(null);
				}
				
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				List<ScQtFinalizationReport> briefingPaperReportList = new ArrayList<>();
				// #18685 - Start. 
				if (finalizationReport != null){//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
				finalizationReport.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
				finalizationReport.setChangedBy(getUserIdSessionFromLoginId()); //#31849 add log key
				}
                // #18685 - End. 
				briefingPaperReportList.add(finalizationReport);
				reqDTO.setAddNewBriefingPaperReportList(briefingPaperReportList );
				finalizationReport = qtSuppFinalizationSO.saveFinalizationReports(reqDTO);
				int index = 0;
				// #18685 - Start.
				for (ScQtFinalizationReport report : getManagedBean().getNewBriefingPaperReportList()) {
					//Bug #33988@Syazani -- Start 27/09/2022
					if (report.getTitle().equalsIgnoreCase(finalizationReport.getTitle())
							&& (report.getDefinationType() != null && report.getDefinationType().getParameterCode()
									.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_USER_INPUT))) {
						//Bug #33988@Syazani -- End 27/09/2022
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
					//35061 - Start
					if(ruleResDTO.getRuleValueList()!=null && !ruleResDTO.getRuleValueList().isEmpty()) {
					//35061 - End
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
			if (	// #25638 - #31322 - Start.
					null != this.getManagedBean().getOutComes()
					&& this.getManagedBean().getOutComes().length > 0
					&& ArrayUtils.contains(this.getManagedBean().getOutComes(), BpmOutcomeConst.BPM_REVISE)
					&&
					// #25638 - #31322 - End.			
					null != getManagedBean().getCurrentStatusId()) {
				long currentStatus = getManagedBean().getCurrentStatusId().longValue();
				if ((currentStatus == QTStatusConst.SF_PENDING_ENDORSEMENT.longValue())
						|| (currentStatus == QTStatusConst.SF_PENDING_SIGN_OFF.longValue())) {
					for (ScQtCommitteeActivity committeeActivity : committeeList) {
						if (null != committeeActivity && null != committeeActivity.getScCommitteeMember()
								&& null != committeeActivity.getScCommitteeMember().getMemberRole()
								&& ParameterConst.COMMITTEE_ROLE_SECRETARY.equalsIgnoreCase(committeeActivity.getScCommitteeMember().getMemberRole().getParameterCode())) {
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
			List<String> roleCodes = new ArrayList<>(); //Sonarqube DP Code Smell Info Mohamad Fadzly [26/4/2024]
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

			List<String> pmRoleCodes = new ArrayList<>(); //Sonarqube DP Code Smell Info Mohamad Fadzly [26/4/2024]

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
		
	// 25037 - Start
	/**
	 * Validate if current task bpm version is supported by the version
	 * provided.
	 * 
	 * @param docNo
	 * @param validityId
	 * @return flag that determine if task is supported
	 */
	public boolean validateSupportedTask(String docNo, Long validityId) {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		ICompositeValiditySO compositeSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICompositeValiditySO.class);

		CompositeValidityReqDTO cReqDTO = new CompositeValidityReqDTO();
		cReqDTO.setCompositeValidityId(validityId);
		String minVersion = compositeSO.getCompositeByValidityId(cReqDTO).getPmCompositeValidity()
				.getMinCompositeVersion();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setDocNumber(docNo);
		WorklistVO worklist = bpmSO.getTasksByDocNumber(reqDTO).getWorklistVO();
		worklist = worklist == null ? new WorklistVO() : worklist;
		String version = null;
		if (CollectionUtils.isNotEmpty(worklist.getWorklistItem())) {
			for (WorklistItemVO i : worklist.getWorklistItem()) {
				if (StringUtils.isNotEmpty(i.getInstanceVersion())) {
					version = i.getInstanceVersion();
					break;
				}
			}
		}
		if (version != null) {
			return CustomUtils.compositeVersionComparator(version, minVersion) >= 0;
		}
		return false;
	}

	/**
	 * Generate the roles that allow for committee maintenance for different
	 * committee
	 * 
	 * @param scQt
	 * @param committeeType
	 * @return array of roles
	 */
	public String[] getRoleFilterListForCommitteeMaintenance(ScQt scQt, String committeeType) {
		String[] arr = new String[0];
		List<String> list = new ArrayList<>();
		if (scQt == null) {
			return arr;
		}
		IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		reqDTO.setDocId(scQt.getQtId());

		ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
		if (wfStatus == null) {
			return arr;
		}
		Long statusId = wfStatus.getPmStatus().getStatusId();
		if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE.contentEquals(committeeType)) {
			if (Arrays.asList(QTStatusConst.QT_SPEC_COMM_UPDATE_GROUP).contains(statusId)) {
				// 25037 - start - note 372
				Long[] secretaryExceptSpecCommittee = new Long[] { QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT,
						QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL,
						QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL_RESCHEDULE,
						QTStatusConst.QT_REVISION_PUBLICATION, QTStatusConst.QT_PENDING_SCHEDULE_REVISION };
				// 25037 - end - note 372

				if (!Arrays.asList(secretaryExceptSpecCommittee).contains(statusId)) {
					list.add(ParameterConst.COMMITTEE_ROLE_SECRETARY);
				}
				if (QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF.equals(statusId)) {
					list.add(ParameterConst.COMMITTEE_ROLE_MEMBER);
				}
			}
		} else {
			if (this.isStatusGreaterThanOrEquals(statusId, QTStatusConst.QT_RE_EVALUATION,
					QTStatusConst.QT_ORDERED_STATUSES)) {
				boolean isCommitteeEvalStage = false;
				if (StringUtils.isNotEmpty(committeeType)) {
					reqDTO = new ScWorkFlowStatusReqDTO();
					reqDTO.setDocType(committeeType);
					reqDTO.setDocId(scQt.getQtId());
					wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					Long evalStatusId = wfStatus != null ? wfStatus.getPmStatus().getStatusId() : 0L;
					isCommitteeEvalStage = Arrays.asList(QTStatusConst.QT_EVAL_PREPARATION_GROUP)
							.contains(evalStatusId)
							|| Arrays.asList(QTStatusConst.QT_EVAL_FINALIZATION_GROUP).contains(evalStatusId);
				}

				if (isCommitteeEvalStage) {
					list.add(ParameterConst.COMMITTEE_ROLE_SECRETARY);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(list)) {
			arr = new String[list.size()];
			list.toArray(arr);
		}
		return arr;
	}
	
	/**
	 * Omit latest remark from display
	 * 
	 * @param remarks
	 * @return list of ScRemark
	 */
	public List<ScQtCommitteRemarks> omitLatestRemarkForDisplay(List<ScQtCommitteRemarks> remarks) {
		List<ScQtCommitteRemarks> omittedList = new ArrayList<>();
		if (CollectionUtils.isEmpty(remarks)){
			return omittedList;
		}

		for (ScQtCommitteRemarks r : remarks) {
			if (StringUtils.isNotEmpty(r.getRemark())) {
				if (r.isEditable() || r.isBrandNew()) {
					ScQtCommitteRemarks rn = new ScQtCommitteRemarks();
					rn.setRemark(r.getRemark());
					rn.setCreatedBy(r.getCreatedBy());
					rn.setCreatedUser(r.getCreatedUser());
					rn.setCreatedDate(r.getCreatedDate());
					rn.setChangedDate(r.getChangedDate());
					rn.setEditable(false);
					rn.setBrandNew(false);
					omittedList.add(rn);
				} else {
					omittedList.add(r);
				}
			}
		}
		return omittedList;
	}

	/**
	 * Deserialize and populate history list
	 * @param scCommittee
	 * @return list of QtCommitteeMemberBean
	 */
		@SuppressWarnings("unchecked")
	public List<QtCommitteeMemberBean> getMemberHistoryList(ScCommittee scCommittee) {
		List<QtCommitteeMemberBean> list = new ArrayList<>();
		if (scCommittee != null) {
			List<ScCommitteeMemberHistory> histories = scCommittee.getScCommitteeMemberHistories();

			if (CollectionUtils.isNotEmpty(histories)) {
				Collections.sort(histories, new Comparator<ScCommitteeMemberHistory>() {
					@Override
					public int compare(ScCommitteeMemberHistory o1, ScCommitteeMemberHistory o2) {
						return o2.getCreatedDate().compareTo(o1.getCreatedDate());
					}
				});

				ScCommitteeMemberHistory h = histories.get(0);
				list = (List<QtCommitteeMemberBean>) (Object) h.deserializeList(h.getCommitteeMembers());
				CustomUtils.sortQtCommitteeMemberRole(list);
			}
		}
		return list;

	}

	/**
	 * Populate all currently assigned taskname for a qt
	 * 
	 * @param qt
	 * @return list of task names
	 */
	public List<String> getCurrentlyAssignedTaskNames(ScQt qt) {
		List<String> taskNames = new ArrayList<>();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		bpmReqDTO.setDocNumber(qt.getQtNo());
		bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
		BpmResponseDTO resDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
		if (resDTO.getWorklistVO() != null && CollectionUtils.isNotEmpty(resDTO.getWorklistVO().getWorklistItem())) {
			for (WorklistItemVO currentVO : resDTO.getWorklistVO().getWorklistItem()) {
				if (StringUtils.isNotBlank(currentVO.getTaskName())) {
					taskNames.add(currentVO.getTaskName());
				}
			}
		}

		if (CollectionUtils.isEmpty(taskNames)) {
			return null;
		}
		return taskNames;
	}

	// 25037 - start - note 510
	/**
	 * Validate if maintenance was in progress
	 * 
	 * @param qt
	 * @param validateCommitteeApproval
	 */

	@SuppressWarnings("unchecked")
	// 25037 note 629
	public void assertSpecCommitteeMaintenanceInProgress(ScQt qt, boolean validateCommitteeApproval) {
		// specification maintenance cannot be concurrent to spec committee
		// maintenance
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		ICommitteSO committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				ICommitteSO.class);
		reqDTO.setQtId(qt.getQtId());
		reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		ScQtCommittee qtCommittee = committeSO.retrieveCommitteByQtIdNCommType(reqDTO).getScQtCommittee();
		boolean pendingApproval = false;// 25037 note 629
		if (null == qtCommittee || null == qtCommittee.getScCommittee()) {
			return;
		}
		ScCommittee scCommittee = qtCommittee.getScCommittee();
		if (validateCommitteeApproval) {
			Long statusId = scCommittee.getCommitteStatus().getStatusId();
			statusId = statusId == null ? 0L : statusId;
			if (!Arrays.asList(new Long[] { QTStatusConst.CMT_COMPLETED, QTStatusConst.CMT_PENDING_SUBMISSION })
					.contains(statusId)) {
				throw new EpException(getMessage("lbl.sc.popup.cannotSubmitWaitingCMT", null));
			}
			pendingApproval = true;
		}

		if (CollectionUtils.isEmpty(scCommittee.getScCommitteeMembers()))
			return;

		List<ScCommitteeMember> members = scCommittee.getScCommitteeMembers();

		// 25037 - start - note 629
		List<QtCommitteeMemberBean> memberList = new ArrayList<>();
		final List<String> sortOrder = Arrays.asList(ParameterConst.COMMITTEE_ROLE_MEMBER,
				ParameterConst.COMMITTEE_ROLE_CHAIRPERSON, ParameterConst.COMMITTEE_ROLE_SECRETARY);

		if (pendingApproval) {
			if (CollectionUtils.isNotEmpty(scCommittee.getScCommitteeMemberHistories())) {
				List<ScCommitteeMemberHistory> histories = scCommittee.getScCommitteeMemberHistories();
				Collections.sort(histories, new Comparator<ScCommitteeMemberHistory>() {
					@Override
					public int compare(ScCommitteeMemberHistory o1, ScCommitteeMemberHistory o2) {
						return o2.getCreatedDate().compareTo(o1.getCreatedDate());
					}
				});
				ScCommitteeMemberHistory history = histories.get(0);
				memberList = (List<QtCommitteeMemberBean>) (Object) history.deserializeList(history
						.getCommitteeMembers());
				Collections.sort(memberList, new Comparator<QtCommitteeMemberBean>() {
					@Override
					public int compare(QtCommitteeMemberBean o1, QtCommitteeMemberBean o2) {
						return Integer.valueOf(sortOrder.indexOf(o1.getMemberRole())).compareTo(
								Integer.valueOf(sortOrder.indexOf(o2.getMemberRole())));
					}
				});
			}
		} else {
			Collections.sort(members, new Comparator<ScCommitteeMember>() {
				@Override
				public int compare(ScCommitteeMember o1, ScCommitteeMember o2) {
					return Integer.valueOf(sortOrder.indexOf(o1.getMemberRole().getParameterCode())).compareTo(
							Integer.valueOf(sortOrder.indexOf(o2.getMemberRole().getParameterCode())));
				}
			});
			for (ScCommitteeMember member : members) {
				QtCommitteeMemberBean memberBean = new QtCommitteeMemberBean();
				memberBean.setIdentificationNo(member.getIcPassport());
				memberBean.setMemberRole(member.getMemberRole().getParameterCode());
				memberBean.setMemberName(member.getMemberName());
				memberList.add(memberBean);
			}
		}
		this.getManagedBean().setInactiveSCMemberStr(retrieveInactiveUserString(memberList));
	}

	private String retrieveInactiveUserString(List<QtCommitteeMemberBean> memberList) {
		StringBuilder inactiveUserSb = new StringBuilder();
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setUserSearchBean(new SearchBean<PmUser>());
		userReqDTO
				.getUserSearchBean()
				.getDataFilterBeans()
				.add(new SearchFilterBean("recordStatusFlg", SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));
		userReqDTO
				.getUserSearchBean()
				.getDataFilterBeans()
				.add(new SearchFilterBean("pmUserOrgs.recordStatusFlg", SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));

		Map<String, String> roleMap = new HashMap<>();
		roleMap.put(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON, SourcingConstants.CONCAT_STRING_CHAIRPERSON);
		roleMap.put(ParameterConst.COMMITTEE_ROLE_MEMBER, SourcingConstants.CONCAT_STRING_MEMBER);
		for (QtCommitteeMemberBean member : memberList) {
			String memberRoleCode = member.getMemberRole();
			if (memberRoleCode.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)){
				continue;
			}

			String memberRole = ResourceBundleUtils.getMessage("lbl.sc.committee.role.".concat(memberRoleCode),
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			userReqDTO.setRoleCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE.concat(roleMap
					.get(memberRoleCode)));
			userReqDTO.setIdentificationNo(member.getIdentificationNo());
			UserResDTO resDTO = userSO.retrieveUserListByRole(userReqDTO);
			if (CollectionUtils.isEmpty(resDTO.getUserList())) {
				if (inactiveUserSb.length() > 0) {
					inactiveUserSb.append(", ");
				}
				inactiveUserSb.append(memberRole);
				inactiveUserSb.append(" ");
				inactiveUserSb.append(member.getMemberName());
			}
		}
		return inactiveUserSb.toString();
	}
	//25037 - end - note 510
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
					
					//#9019 Start #note-445 User unable to open side menu for Committee Appointment and Approval when ctType is null. 
					//      Contract type is not selected while cancel QT
					if(ctType != null){
						if (isRuleBpk && ctType.equals(SourcingConstants.CENTRAL_CONTRACT) && !isSbhOrSrwk) {
							return isBpkCentralContract = true;
						}
					}
					//9019 End #note-445
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
		//#26130 start		
	/**
	 * @param question
	 * @param master
	 * @return
	 */
	private List<Map<String, String>>getCOmbineZonalSPec(TplSpecQuestion question, TplSpecMaster master){
		List<Map<String, String>> listSpec = new ArrayList<>();
		int countIndex = 0;
		int indi = 0;
		boolean getFirstValue = true;
		//26130 - start 
		if (master.getTplSpecZone() != null){
		//26130 - end	
		if (question.getTplSpecZoneQties().size()  > 0){
			for (TplSpecZoneQty zone: question.getTplSpecZoneQties()){
				Map<String, String> map = new HashMap<>();
				if (countIndex == 0){
					if (getFirstValue){
						map.put(SSTConstant.clItem, question.getClItem());
						map.put(SSTConstant.uomName, question.getClUom().getUomName());
						map.put(SSTConstant.clPriceTypeCode, getParameterDesc(question.getClPriceTypeCode(),SourcingConstants.MY_LOCALE));
						map.put(SSTConstant.zoneName,zone.getTplSpecZoneDtl().getZoneName());
						map.put(SSTConstant.quantity, zone.getQty().toString());
						if (null != question.getTplSpecTierQties() && question.getTplSpecTierQties().size() > 0 ){
							String tierValue = retrieveTierQuantity(question.getTplSpecTierQties());
							map.put(SSTConstant.qtyBerperingkat, tierValue);
						}else {
							map.put(SSTConstant.qtyBerperingkat, SSTConstant.TB);
						}
						if(getParameterDescForReport(master.getItemTypeCode()).equalsIgnoreCase(SSTConstant.service) || getParameterDescForReport(master.getItemTypeCode()).equalsIgnoreCase(SSTConstant.perkhidmatan)){
							map.put(SSTConstant.clUomFrequency, question.getClUomFrequency().toString());
							map.put(SSTConstant.clUomPerDay, question.getClUomPerDay().toString());
							map.put(SSTConstant.clUomPerMonth, question.getClUomPerMonth().toString());
						}
						for (TplSpecAnswer answer: question.getTplSpecAnswers())
						if (answer.getAnswerNumeric() != null){
							map.put(SSTConstant.answerNumeric, answer.getAnswerNumeric().toString());
						}
						getFirstValue = false;
					}else {
						map.put(SSTConstant.clItem, SSTConstant.nbsp);
						map.put(SSTConstant.uomName, SSTConstant.nbsp);
						map.put(SSTConstant.clPriceTypeCode, SSTConstant.nbsp);
						map.put(SSTConstant.zoneName,zone.getTplSpecZoneDtl().getZoneName());
						map.put(SSTConstant.qtyBerperingkat, SSTConstant.nbsp);
						if(getParameterDescForReport(master.getItemTypeCode()).equalsIgnoreCase(SSTConstant.service) || getParameterDescForReport(master.getItemTypeCode()).equalsIgnoreCase(SSTConstant.perkhidmatan)){
							map.put(SSTConstant.clUomFrequency, SSTConstant.nbsp);
							map.put(SSTConstant.clUomPerDay, SSTConstant.nbsp);
							map.put(SSTConstant.clUomPerMonth, SSTConstant.nbsp);
						}
						map.put(SSTConstant.quantity, zone.getQty().toString());
						for (TplSpecAnswer answer: question.getTplSpecAnswers()) {
							if (answer.getAnswerNumeric() != null){
								map.put(SSTConstant.answerNumeric, answer.getAnswerNumeric().toString());
							}
						}
					}
					for (int i = indi; i <question.getTplSpecZoneQties().size(); i++){
						map.put(SSTConstant.Feedback,question.getFeedbackList().get(i));
						break;
					}
					//26130 - start 04072022
					for (int i = indi; i <question.getTplSpecZoneQties().size(); i++){
						if (!question.getAnswerRemarkList().get(i).equalsIgnoreCase(SSTConstant.whiteSpace) && !question.getAnswerRemarkList().get(i).equalsIgnoreCase(SSTConstant.TB)){
							map.put(SSTConstant.answerRemark,question.getAnswerRemarkList().get(i));
							break;
						}else {
							map.put(SSTConstant.answerRemark,question.getAnswerRemarkList().get(i));
						}
					}
					//#26130 - end 04072022
					indi++;
				}
				listSpec.add(map);
			}
		}
		//26130 - start
		}
		//26130 - end
		else {
			//non-zonal
				HashMap<String, String> map = new HashMap<>();
				if (question.getQuestionType().equalsIgnoreCase(SSTConstant.item)){
					if (countIndex == 0){
						if (getFirstValue){
							map.put(SSTConstant.clItem, question.getClItem());
							map.put(SSTConstant.uomName, question.getClUom().getUomName());
							map.put(SSTConstant.quantity, question.getClQuantity().toString());
							if(getParameterDescForReport(master.getItemTypeCode()).equalsIgnoreCase(SSTConstant.service) || getParameterDescForReport(master.getItemTypeCode()).equalsIgnoreCase(SSTConstant.perkhidmatan)){
								map.put(SSTConstant.clUomFrequency, question.getClUomFrequency().toString());
								map.put(SSTConstant.clUomPerDay, question.getClUomPerDay().toString());
								map.put(SSTConstant.clUomPerMonth, question.getClUomPerMonth().toString());
							}else {
								//product
								map.put(SSTConstant.clPriceTypeCode, getParameterDesc(question.getClPriceTypeCode(),SourcingConstants.MY_LOCALE));
								String tierValue = retrieveTierQuantity(question.getTplSpecTierQties());
								map.put(SSTConstant.qtyBerperingkat, tierValue);
							}
							for (String feedback:question.getFeedbackList()){
								map.put(SSTConstant.Feedback, feedback);
								break;
							}
							for(String remark:question.getAnswerRemarkList()){
								map.put(SSTConstant.Remark, remark);
								break;
							}
						}
						listSpec.add(map);
					}
				}
		}
		return listSpec;
		//#26130 end
	}
		//#26130 - zaim end				
		
		//#26130 start
		public String retrieveTierQuantity(List<TplSpecTierQty> listTplSpecTierQties) {
			StringBuilder sb = new StringBuilder();
			if (!listTplSpecTierQties.isEmpty()) { //Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
				for (TplSpecTierQty qty : listTplSpecTierQties) {
					sb.append(qty.getFromQty() + " - " + qty.getToQty() + "<br />");
				}
			} else {
				sb.append(SSTConstant.TB);
			}
			return sb.toString();
		}
		//#26130 end
		public String replaceWithBR(String specDetails) {//11104_start
			
			return specDetails.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER,
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}//11104_end
		
		/**25635
		 * is clone button disable
		 * 
		 * 
		 * 
		 * @return
		 */
		public boolean isCloneBtnDisable() {
			log.info("========cloneButtonDisableStart=======");
				String isClonehBtn = EpPropertiesUtils.getInstance()
						.getProperty(EpPropertiesConst.SC_CLONE_BUTTON_DISABLEQT);
				log.info("========cloneButtonDisableEnd======="+isClonehBtn);
				if (StringUtils.isNotBlank(isClonehBtn)) {
					return Boolean.valueOf(isClonehBtn);
				}
				log.info("========cloneButtonDisableEnd=======");	
			
			return false;
		}//25635
		
	// 25037 - start
	/**
	 * Evaluate if taskname is endorsement and signoff
	 * 
	 * @param taskName
	 * @return String (BPM task Name)
	 */
	public String getRepresentativeType(String committeeType) {
		switch (committeeType) {
		case ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B:
		case ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A:
			return ParameterTypeConst.REPRESENTATIVE_TYPE_QC;
		case ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD:
			return ParameterTypeConst.REPRESENTATIVE_TYPE_PPR;
		case ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B:
			return ParameterTypeConst.REPRESENTATIVE_TYPE_PBB;
		case ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A:
			return ParameterTypeConst.REPRESENTATIVE_TYPE_PBA;
		case ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH:
			return ParameterTypeConst.REPRESENTATIVE_TYPE_FPBH;
		case ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK:
			return ParameterTypeConst.REPRESENTATIVE_TYPE_FPBK;
		default:
			return StringUtils.EMPTY;
		}
	}
	
	/**
	 * Evaluate if taskname is endorsement and signoff
	 * 
	 * @param taskName
	 * @return String (BPM task Name)
	 */
	public boolean evaluateEndorseSignoffTask(String taskName) {
		if (StringUtils.isEmpty(taskName))
			return false;
		String[] actions = new String[] { BpmOutcomeConst.BPM_ENDORSE, BpmOutcomeConst.BPM_SIGNOFF,
				BpmOutcomeConst.BPM_VIEW, BpmOutcomeConst.BPM_UPDATE };
		String action = StringUtils.upperCase(taskName.split(" ")[0]);
		return Arrays.asList(actions).contains(action);
	}
	
	/**
	 * Evaluate if committee is editable by taskname for screen update committee 
	 * during evaluation and finalization
	 * 
	 * @param currentTaskName
	 * @return String (BPM task Name)
	 */
	public boolean isCommitteeEditable(String currentTaskName) {
		String taskName = currentTaskName;
		if (taskName == null) {
			taskName = StringUtils.EMPTY;
		}

		List<String> taskNames = new ArrayList<>();
		taskNames.addAll(Arrays.asList(SCProcessNameConst.EVAL_COMM_PREPARATION_GROUP));
		taskNames.addAll(Arrays.asList(SCProcessNameConst.EVAL_COMM_FINALIZE_GROUP));
		taskNames.addAll(Arrays.asList(SCProcessNameConst.FINALISE_FC_SELECTION,
				SCProcessNameConst.COORDINATE_FC_MEETING));
		return taskNames.contains(taskName);
	}
	
	/**
	 * Extract committee description from committees and concat as a single
	 * string
	 * 
	 * @param scQtCommittees
	 * @return String, concatenated committee description
	 */
	public String getCommitteeNames(List<ScQtCommittee> scQtCommittees) {
		String committeeNames = StringUtils.EMPTY;
		int count = 0;
		if (CollectionUtils.isNotEmpty(scQtCommittees)) {
			for (ScQtCommittee c : scQtCommittees) {
				count++;
				String code = c.getScCommittee().getCommitteeType().getParameterCode();
				committeeNames = committeeNames.concat(this.getParameterDesc(ParameterConst.COMMITTE_TYPE, code));
				if (count < scQtCommittees.size()) {
					committeeNames = committeeNames.concat("/");
				}
			}
		}
		return committeeNames;
	}
	
	/**
	 * Sort outcomes.
	 * 
	 * @param outcomes
	 * @return the string[]
	 */
	public String[] sortQtOutcomes(String[] outcomes) {
		if (null == outcomes) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		List<String> bpmActions = new ArrayList<>(Arrays.asList(outcomes));
		Collections.sort(bpmActions, new Comparator<String>() {
			String[] orderedAction = new String[] { BpmOutcomeConst.BPM_REVISE };
			List<String> sortOrder = Arrays.asList(orderedAction);

			@Override
			public int compare(String s1, String s2) {
				return Integer.valueOf(sortOrder.indexOf(s2)).compareTo(Integer.valueOf(sortOrder.indexOf(s1)));
			}
		});
		String[] bpmOutcomes = new String[bpmActions.size()];
		return bpmActions.toArray(bpmOutcomes);
	}
	//25037 - end
	
	//26130 - start
	public boolean validateTplSpecQuestion(ScLoa scloa, TplSpecQuestion question) {
		boolean isExist = Boolean.FALSE;
		Long tempSpecQuestionID = null;
		if (scloa.getScLoiLoaItems() != null && !scloa.getScLoiLoaItems().isEmpty()) {
			for (ScLoiLoaItem item : scloa.getScLoiLoaItems()) {
				if (item.getScRequestItem() != null && item.getScRequestItem().getTplSpecQuestion() != null
						&& item.getScRequestItem().getTplSpecQuestion().getParentSpecQuestion() != null) {
					TplSpecQuestion specQuestion = item.getScRequestItem().getTplSpecQuestion();
					if (question.getParentSpecQuestion() == null) {
						tempSpecQuestionID = question.getSpecQuestionId();
					} else {
						tempSpecQuestionID = question.getParentSpecQuestion().getSpecQuestionId();
					}
					if (specQuestion.getParentSpecQuestion().getSpecQuestionId().equals(tempSpecQuestionID)) {
						isExist = Boolean.TRUE;
						return isExist;
					}
				}
			}
		}
		return isExist;
	}
	
	public boolean validateTplSpecMaster(ScLoa scloa, TplSpecMaster master){
		boolean isExist = false;
		if (scloa.getScLoiLoaItems() != null) {
			for (ScLoiLoaItem item : scloa.getScLoiLoaItems()) {
				if (item.getScRequestItem() != null && item.getScRequestItem().getTplSpecQuestion() != null) {
					TplSpecQuestion specQuestion = item.getScRequestItem().getTplSpecQuestion();
					if (specQuestion.getParentSpecQuestion() != null
							&& specQuestion.getParentSpecQuestion().getTplSpecMaster() != null
							&& master.getSpecMasterId().equals(
									specQuestion.getParentSpecQuestion().getTplSpecMaster().getSpecMasterId())) {
						isExist = Boolean.TRUE;
						return isExist;
					}
				}
			}
		}
		return isExist;
	}
	//26130 - end

	//10542 - Function for testing (Will be removed)
	protected long convertMilliToSeconds(long milli){
		return (milli/1000) % 60;
	}
	
	/**
	 * Defect: #34924
	 * Method: Checking and validate using validator: string value has unrecognized symbol/character.
	 * @param context the context
	 * @param component the component
	 * @param value the value
	 */
	public void isContentExistUnicode(FacesContext context, UIComponent component,
			Object value) {
		
		String strContent = (String) value;
		String labelNameVal = (String) component.getAttributes().get("label");
		
		boolean isExistUnicodeSymbol = Boolean.FALSE;
		
		if(strContent != null && !strContent.isEmpty()){
			String [] arrChar = UnicodeCharacterConstant.getUnicodeCharacterList();
	        
	        for(int i=0;i<arrChar.length;i++) {
	            if(strContent.contains(arrChar[i])){
	            	isExistUnicodeSymbol = Boolean.TRUE;
	            }
	        }
		}
		
		if(isExistUnicodeSymbol && labelNameVal != null && !labelNameVal.isEmpty()){
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils
					.getMessage(MessageConst.ERROR_760543,
							new String[] {labelNameVal },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			
		}
	}
	/***
	 * 11104_newEndorseStart
	 * get TemplatespecQuestion based on Checklist.
	 * @param sampleSpecs
	 * @return  List<TplSpecQuestion>
	 */
	protected List<TplSpecQuestion> getTemplateSampleQuestions(ScQtCheckList sampleSpecs){//11104_newEndorseStart
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO.setSpecMaster(sampleSpecs.getScQtDoc()
				.getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		specReqDTO.setQuestionType(TemplateSpecConst.QUESTION_TYPE_ITEM);
		TemplateSpecResDTO tmpresDTO = templateSpecSO
				.getTemplateMaster(specReqDTO);
		 return tmpresDTO.getSpecMaster()
		.getTplSpecQuestions();
	}//11104_newEndorseStart
	/***
	 * 11104_newEndorseStart
	 * check Sample Item exist in the questions for work in progress qt.
	 * @param scQt
	 * @return  boolean
	 */
	public boolean  checkISSampleExistWIPChecklist(ScQt scQt){//11104_newEndorseStart
		boolean qtSample = scQt.isSampleReq();
		 if (qtSample
				&& !isQTStatusPublished(scQt
						.getQtStatus().getStatusId())
				&& SourcingConstants.SAMPLE_WIP_CHECK.equals(scQt.getDemoNotificationCheck())) {
			return Boolean.TRUE;
		} else if (qtSample
				&& !isQTStatusPublished(scQt
						.getQtStatus().getStatusId())
				&& null == scQt
						.getDemoNotificationCheck()) {
			
			return Boolean.FALSE;
		} else if(qtSample) {
			IQTSpecSO qtSpecSO=BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO specificationReqDTO = new SpecificationReqDTO();
			specificationReqDTO.setScQt(scQt);
			specificationReqDTO
					.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			SpecificationResDTO specificationResDTO = qtSpecSO
					.retrieveTechnicalService(specificationReqDTO);
			List<ScQtCheckList> sampleTemplateSpecChecklist = new ArrayList<>();
			for (ScQtCheckList sampleCheckList : specificationResDTO
					.getTechnicalCheckList()) {
				if (QTConstants.TEMPLETE_SPEC_CODE
						.equalsIgnoreCase(sampleCheckList.getDefinationType()
								.getParameterCode())
						&& QTStatusConst.TS_COMPLETED.equals(sampleCheckList
								.getPmStatus().getStatusId())) {
					sampleTemplateSpecChecklist.add(sampleCheckList);
				}
			}
			return checkWiPSampleQuestionExist(sampleTemplateSpecChecklist);
			
		}
		return Boolean.FALSE;
	}//11104_newEndorseEnd
	private boolean checkWiPSampleQuestionExist(List<ScQtCheckList> sampleTemplateSpecChecklist){//11104_newEndorseStart
		for (ScQtCheckList sampleSpecs : sampleTemplateSpecChecklist) {	
			for (TplSpecQuestion specQuestion : getTemplateSampleQuestions(sampleSpecs)) {
				if (BooleanUtils.isTrue(specQuestion.isSampleReq())) {
					return Boolean.TRUE;
				}
			}

		}
		return Boolean.FALSE;
	}//11104_newEndorseEnd
	
	//12471 Start
	/**
	 * Initialize member info for  
	 * @param memberInfo
	 * @return void
	 */
	public void onClickMemberInfo(PmUser memberInfo) {
		this.getManagedBean().setMemberName(memberInfo.getUserName());
		for (PmUserOrg org : memberInfo.getPmUserOrgs()) {
			if (org.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				if (org.getPmOrgProfile().getEpPtj()) {
					IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
					OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
					reqDTO.setOrgProfileId(org.getPmOrgProfile().getOrgProfileId());
					reqDTO.setOrgCode(org.getPmOrgProfile().getPmOrgValidity().getOrgCode());
					OrgProfileResDTO resDTO = orgProfileSO.retrieveMinistryByOrgProfileId(reqDTO);
					this.getManagedBean().setMemberMinOrg(resDTO.getPmOrgProfile().getPmOrgValidity().getOrgName());
				} else {
					String orgType = this.getParameterDesc(org.getPmOrgProfile().getOrgTypeCode());
					orgType = orgType == null ? "" : orgType;
					this.getManagedBean().setMemberMinOrg(orgType);
				}
				this.getManagedBean().setMemberPtjOrgCode(org.getPmOrgProfile().getPmOrgValidity().getOrgCode());
				this.getManagedBean().setMemberPtjOrgName(org.getPmOrgProfile().getPmOrgValidity().getOrgName());
				break;
			}
		}
		RequestContext.getCurrentInstance().execute(QTConstants.MEMBER_INFO_POPUP);
	}
	/**
	 * Get user organization description
	 * @param pmUserOrgs
	 * @param orgType
	 * @return String - organization description
	 */
	public String getOrgDesc(List<PmUserOrg> pmUserOrgs, String orgType) {
		String desc = "";
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		for (PmUserOrg org : pmUserOrgs) {
			if (org.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				reqDTO.setOrgProfileId(org.getPmOrgProfile().getOrgProfileId());
				if (orgType.equalsIgnoreCase(ParameterConst.ORG_TYPE_MINISTRY)) {
					OrgProfileResDTO resDTO = orgProfileSO.retrieveMinistryByOrgProfileId(reqDTO);
					desc = resDTO.getPmOrgProfile().getOrgName();
				} else if (orgType.equalsIgnoreCase(ParameterConst.ORG_TYPE_PTJ_GROUP)) {
					OrgProfileResDTO resDTO = orgProfileSO.retrieveJabatanByOrgProfileId(reqDTO);
					desc = resDTO.getPmOrgProfile().getOrgName();
				} else if (orgType.equalsIgnoreCase(ParameterConst.ORG_TYPE_PTJ)) {
					desc = org.getPmOrgProfile().getOrgName();
				}
				break;
			}
		}
		return desc;
	}
	/**
	 * Generate organization type dropdown
	 */
	public void buildOrgTypeDD() {
		ISearchBean<Object[]> searchBean = super.createSearchBean(Object[].class, QTSearchBeanConst.SEARCH_SOMOTHERORGTYPE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean(QTSearchBeanConst.SEARCH_PMPARAMETER_PARAMETERCODE, SearchOperator.OPERATOR_EQUALS.value(),
						ParameterConst.APPROVAL_CATEGORY_OTHERS));
		SelectItem[] items = getDropdownObjectValuesBySearchBean(ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		this.getManagedBean().setOrgTypeDDList(items);
	}
	/**
	 * Generate organization name dropdown
	 */
	public void buildOrgNameDD() {
		UserCommitteeSearchBean searchBean = this.getManagedBean().getCommitteeSearchBean();
		if (null == searchBean)
			return;

		SelectItem[] items = new SelectItem[1];
		List<PmOrgProfile> list = new ArrayList<>();
		if (searchBean.getOrgType() != null) {
			ParameterReqDTO paramReqDTO = new ParameterReqDTO();
			paramReqDTO.setParameterId(Long.valueOf(searchBean.getOrgType()));
			String orgType = this.getParameterSOGlobal().retrieveParameterById(paramReqDTO).getPmParameter()
					.getParameterCode();
			ISearchBean<PmOrgProfile> orgProfileSearchBean = new SearchBean<>();
			orgProfileSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(QTSearchBeanConst.SEARCH_ORGTYPECODE_PARAMETERCODE,
							SearchOperator.OPERATOR_EQUALS.value(), orgType));
			orgProfileSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(QTSearchBeanConst.SEARCH_PMORGVALIDITY_ORGNAME,
							SearchOperator.OPERATOR_IS_NOT_NULL.value(), null));
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileSearchBean(orgProfileSearchBean);
			OrgProfileResDTO resDTO = orgProfileSO.searchOrgProfile(reqDTO);
			list = resDTO.getOrgProfileResultBean().getResultList();
			items = new SelectItem[list.size() + 1];
		}

		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(QTConstants.LABEL_TEXT_SELECT,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		int i = 1;
		for (PmOrgProfile object : list) {
			items[i++] = new SelectItem(object.getOrgProfileId(), object.getOrgName());
		}
		this.getManagedBean().setOrgNameDDList(items);
	}
	/**
	 * Reset committee user search field and rebuild dropdowns
	 * @param scQt - ScQt
	 */
	public void resetCommitteeSearchBean(ScQt scQt) {
		UserCommitteeSearchBean searchBean = this.getManagedBean().getCommitteeSearchBean();
		if (null == searchBean)
			return;
		if (ParameterConst.ORG_CATEGORY_PTJ.equalsIgnoreCase(searchBean.getOrgCat())) {
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(scQt.getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveMinistryByOrgProfileId(reqDTO);
			searchBean.setMinistry(resDTO.getPmOrgProfile().getOrgProfileId().toString());
			buildMinistryDD();
			resDTO = orgProfileSO.retrieveJabatanByOrgProfileId(reqDTO);
			searchBean.setPtjGroup(resDTO.getPmOrgProfile().getOrgProfileId().toString());
			buildPtjGroupDD();
			searchBean.setPtj(scQt.getOrgProfileId().toString());
			buildPtjDD();
			searchBean.setOrgType(null);
			searchBean.setOrgName(null);
		} else if (ParameterConst.ORG_CATEGORY_OTHERS.equalsIgnoreCase(searchBean.getOrgCat())) {
			PmParameter categoryCodeParameter = this.getParameterByTypeAndCode(ParameterTypeConst.ORGANISATION_TYPES,
					ParameterConst.APPROVAL_CATEGORY_OTHERS);
			if (categoryCodeParameter != null) {
				searchBean.setOrgType(String.valueOf(categoryCodeParameter.getParameterId()));
			}
			buildOrgTypeDD();
			searchBean.setOrgName(QTConstants.ORG_CAT_NAME_EXTERNAL_PARTIES_ID.toString());
			buildOrgNameDD();
		}
		searchBean.setName(null);
		searchBean.setIdentificationType(null);
		searchBean.setIdentificationNo(null);
	}

	/**
	 * Generate ministry dropdown
	 */
	public void buildMinistryDD() {
		this.getManagedBean().setMinistryDDList(PmCommonUtil.getMinistryDropdown());
	}

	/**
	 * Generate ptj group dropdown
	 */
	public void buildPtjGroupDD() {
		UserCommitteeSearchBean searchBean = this.getManagedBean().getCommitteeSearchBean();
		if (null == searchBean)
			return;
		
		SelectItem[] items = new SelectItem[1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(QTConstants.LABEL_TEXT_SELECT,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		if (null != searchBean.getMinistry()) {
			items = PmCommonUtil.getJabatanDropdown(Long.parseLong(searchBean.getMinistry()));
		}
		this.getManagedBean().setPtjGroupDDList(items);
	}

	/**
	 * Generate ptj dropdown
	 */
	public void buildPtjDD() {
		UserCommitteeSearchBean searchBean = this.getManagedBean().getCommitteeSearchBean();
		if (null == searchBean)
			return;

		SelectItem[] items = new SelectItem[1];
		List<Object[]> list = new ArrayList<>();
		if (null != searchBean.getPtjGroup()) {
			Long ptjGroupId = Long.parseLong(searchBean.getPtjGroup());
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setParentOrgProfileId(ptjGroupId);
			reqDTO.setOrgCode(OrganizationType.DIVISION.value());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);
			list = resDTO.getOrgProfileDDList();
			items = new SelectItem[list.size() + 1];
		} 

		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(QTConstants.LABEL_TEXT_SELECT,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		int i = 1;
		for (Object[] object : list) {
			items[i++] = new SelectItem(Long.parseLong(object[0].toString()), object[1].toString());
		}

		this.getManagedBean().setPtjDDList(items);
	}

	/**
	 * Generate dropdown on parent change
	 * @param orgType - Dropdown type (Organization Type)
	 */
	public void onChangeDD(String orgType) {
		UserCommitteeSearchBean searchBean = this.getManagedBean().getCommitteeSearchBean();
		if (null == searchBean)
			return;

		String orgCategory = searchBean.getOrgCat();
		if (orgCategory.equalsIgnoreCase(ParameterConst.ORG_CATEGORY_PTJ)) {
			if (orgType.equalsIgnoreCase(ParameterConst.ORG_TYPE_MINISTRY)) {
				searchBean.setPtjGroup(null);
				buildPtjGroupDD();
				searchBean.setPtj(null);
				buildPtjDD();
			} else if (orgType.equalsIgnoreCase(ParameterConst.ORG_TYPE_PTJ_GROUP)) {
				searchBean.setPtj(null);
				buildPtjDD();
			}
		} else if (orgCategory.equalsIgnoreCase(ParameterConst.ORG_CATEGORY_OTHERS)
				&& orgType.equalsIgnoreCase(ParameterTypeConst.ORGANISATION_TYPES)) {
			searchBean.setOrgName(null);
			buildOrgNameDD();
		}
	}
	// 12471 - end
	protected Map<String,String> getMonthNumberDtls(){//SonarQubeIssuesFixed25635-Start
		Map<String,String> monthNumbers=new HashMap<>();
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_JAN), QTConstants.MONTH_ZERO);
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_FEB), QTConstants.MONTH_JAN);
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_MARCH), QTConstants.MONTH_FEB);
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_APRIL), QTConstants.MONTH_MARCH);
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_MAY), QTConstants.MONTH_APRIL);
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_JUNE), QTConstants.MONTH_MAY);
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_JULY), QTConstants.MONTH_JUNE);
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_AUG), QTConstants.MONTH_JULY);
		monthNumbers.put(QTConstants.MONTH_ZERO.concat(QTConstants.MONTH_SEPT), QTConstants.MONTH_AUG);
		monthNumbers.put(QTConstants.MONTH_OCT, QTConstants.MONTH_SEPT);
		monthNumbers.put(QTConstants.MONTH_NOV, QTConstants.MONTH_OCT);
		monthNumbers.put(QTConstants.MONTH_DEC, QTConstants.MONTH_NOV);
		return monthNumbers;
	}//SonarQubeIssuesFixed25635-End
	
	// Bug #35485@Syazani 08/02/2024 -- Start
	private ScAttachment regenerateDocumentEvaAttachmentByFileName(ScQt scqt, ScAttachment reportAttachment,
			String fileName) throws Exception {
		StringBuilder fileNameSB;
		String committeeType = "";
		if (fileName.equalsIgnoreCase(QTConstants.EVALUATION_COMMITTEE_REPORT_ZIP_TITLE)) {
			committeeType = ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE;
			reportAttachment.setDocType(QTConstants.DOC_TYPE_SC_EVALUATION_COMMITTEE_REPORT_ATTACHMENT);
		} else if (fileName.equalsIgnoreCase(QTConstants.OPENING_COMMITTEE_REPORT_ZIP_TITLE)) {
			committeeType = ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE;
			reportAttachment.setDocType(QTConstants.DOC_TYPE_SC_OPEN_COMMITTEE_REPORT_ATTACHMENT);
		} else if (fileName.equalsIgnoreCase(QTConstants.TECHNICAL_EVALUATION_COMMITTEE_REPORT_ZIP_TITLE)) {
			committeeType = ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE;
			reportAttachment.setDocType(QTConstants.DOC_TYPE_SC_TECHNICAL_EVALUATION_REPORT_ATTACHMENT);
		} else if (fileName.equalsIgnoreCase(QTConstants.FINANCIAL_EVALUATION_COMMITTEE_REPORT_ZIP_TITLE)) {
			committeeType = ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE;
			reportAttachment.setDocType(QTConstants.DOC_TYPE_SC_FINANCIAL_EVALUATION_REPORT_ATTACHMENT);
		}

		fileNameSB = new StringBuilder(fileName);
		fileNameSB.append(".pdf");
		if (reportAttachment != null) {
			reportAttachment.setFileName(fileNameSB.toString());
			reportAttachment.setDocNo(scqt.getQtNo());
			generateEvaluationPdf((ScAttachment) reportAttachment, scqt, committeeType);
		}
		return reportAttachment;
	}

	private boolean updateScAttachment(ScAttachment attachment) throws IOException {
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		return evaluationSO.updateScAttachment(attachment);
	}

	private <ATTACHMENT extends ScAttachment> void savedZipScAttachmentFiles(ScAttachment attachment,
			List<ATTACHMENT> scAttachments) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(getActualFilePath(attachment)));

		try {
			downloadAllFilesAsZipFile(zos, scAttachments);

		} catch (Exception ex) {
			// Log the exception
			log.error("Error while creating ZIP file for attachment: " + attachment.getFileName(), ex);
			throw new IOException("Error while creating ZIP file", ex);
		} finally {
			zos.close();
		}
	}

	private void downloadZipScAttachmentFiles(ScAttachment attachment) throws IOException {
		PortletResponse portalresponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getFileName());

		try (FileInputStream fileInputStream = new FileInputStream(getActualFilePath(attachment));
				OutputStream outputStream = response.getOutputStream()) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
		} catch (FileNotFoundException e) {
			// Log the exception
			log.error("Error while creating ZIP file for attachment: " + attachment.getFileName(), e);

			// Rethrow the exception
			throw e;
		}
		// No need to explicitly close the stream; it's done automatically
	}
	// Bug #35485@Syazani 08/02/2024 -- End

	// 25791-start
	/**
	 * The method used to retrieve the contract status of the contract.
	 * 
	 * @param contractStatus
	 * @return Effective, Terminated, Expired, Inactive
	 */
	public String getContractStatusDesc(int contractStatus) {
		if (contractStatus == ContractConst.CONTRACT_STATUS_EFFECTIVE) {
			return ResourceBundleUtils.getMessage(QTConstants.CONTRACT_STATUS_EFFECTIVE,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_INACTIVE) {
			return ResourceBundleUtils.getMessage(QTConstants.CONTRACT_STATUS_INACTIVE,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_EXPIRED) {
			return ResourceBundleUtils.getMessage(QTConstants.CONTRACT_STATUS_EXPIRED,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_TERMINATED) {
			return ResourceBundleUtils.getMessage(QTConstants.CONTRACT_STATUS_TERMINATED,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			return "";
		}
	}
	// 25791 -end-

	// 25791 -start-
	/**
	 * The method used to retrieve the status completion of the contract.
	 * 
	 * @param contractNo
	 * @param status
	 * @return complete or incomplete
	 */
	public String chkContractStatusType(String contractNo, int status) {
		if (StringUtils.isNotEmpty(contractNo) && StringUtils.isNotBlank(contractNo)) {
			if (status == ContractConst.STATUS_COMPLETE) {
				return ResourceBundleUtils.getMessage(QTConstants.CONTRACT_STATUS_COMPLETE,
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			} else if (status == ContractConst.STATUS_INCOMPLETE) {
				return ResourceBundleUtils.getMessage(QTConstants.CONTRACT_STATUS_INCOMPLETE,
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			} else if (status == ContractConst.STATUS_NOT_APPLICABLE) {
				return ResourceBundleUtils.getMessage(QTConstants.CONTRACT_STATUS_NOT_APPLICABLE,
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			}
		}
		
		return "";
	}
	// 25791-End
	
	// Bug #36730@Syazani 07/01/2025 -- Start
	protected void validateSupplierCriteriaIntegrity(ScQt scQt) {
		ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
		if (null == supplierCriteria) {
			return;
		}

		Long locLevel = null != supplierCriteria.getLocationLevel()
				&& null != supplierCriteria.getLocationLevel().getParameterId() ? supplierCriteria.getLocationLevel()
				.getParameterId() : 0L;

		if (!scQt.isZonal()) {
			validateSupplierCriteriaIntegrityNonZonal(supplierCriteria, locLevel);
		} else {
			validateSupplierCriteriaIntegrityZonal(supplierCriteria, scQt);
		}
	}
	
	private void validateSupplierCriteriaIntegrityNonZonal(ScQtSupplierCriteria supplierCriteria, Long locLevel) {
		if (!CollectionUtils.isEmpty(supplierCriteria.getScQtLocCriteria())) {
			for (ScQtLocCriteria sqlc : supplierCriteria.getScQtLocCriteria()) {
				if ((null == sqlc.getPmDistrict() && locLevel
						.equals(ParameterConst.SUPPLIER_CRITERIA_LOCATION_LEVEL_DISTRICT))
						|| (null != sqlc.getPmDistrict() && locLevel
								.equals(ParameterConst.SUPPLIER_CRITERIA_LOCATION_LEVEL_STATE))) {
					((QTDetailManagedBean) getManagedBean()).setZoneTrees(null);
					throw new EpException(MessageConst.ERROR_760067);
				}
			}
		}
	}
	
	private void validateSupplierCriteriaIntegrityZonal(ScQtSupplierCriteria supplierCriteria, ScQt scQt) {
		String zoneLocationLevel;
		List<TplSpecZoneDtl> zoneDetails = null;
		if (null != supplierCriteria.getQtZoneCriterias() && null != supplierCriteria.getQtZoneCriterias().getTplSpecZoneDtls()) {
			zoneDetails = supplierCriteria.getQtZoneCriterias().getTplSpecZoneDtls();
		}

		if (null != scQt.getZoneLocationLevel() && null != scQt.getZoneLocationLevel().getParameterId()
				&& !CollectionUtils.isEmpty(zoneDetails)) {
			zoneLocationLevel = null != scQt.getZoneLocationLevel().getParameterCode() ? scQt.getZoneLocationLevel()
					.getParameterCode() : "";
			List<TplSpecZoneLoc> locDetails;
			for (TplSpecZoneDtl zoneDtl : zoneDetails) {
				locDetails = zoneDtl.getTplSpecZoneLocs();
				if (!CollectionUtils.isEmpty(locDetails)) {
					validateSupplierCriteriaIntegrityZonalLoopLocDetails(locDetails, zoneLocationLevel);
				}
			}
		}
	}

	private void validateSupplierCriteriaIntegrityZonalLoopLocDetails(List<TplSpecZoneLoc> locDetails,
			String zoneLocationLevel) {
		for (TplSpecZoneLoc locDetail : locDetails) {
			boolean isDistrictProb = zoneLocationLevel.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)
					&& (null == locDetail.getPmDistrict() && locDetail.isZonalLocation());
			boolean isStateProb = zoneLocationLevel.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)
					&& (null != locDetail.getPmDistrict() && locDetail.isZonalLocation());
			if (isDistrictProb || isStateProb) {
				((QTDetailManagedBean) this.getManagedBean()).setZoneGeneralTrees(null);
				((QTDetailManagedBean) this.getManagedBean()).setBeans(new ArrayList<ZonesBean>());
				throw new EpException(MessageConst.ERROR_760067);
			}
		}
	}
	// Bug #36730@Syazani 07/01/2025 -- End
	
	// 37622 - start
	/**
	 * Create a new list for displaying briefing paper report and filter
	 * evaluation report form getting displayed
	 * 
	 * @param briefingPaperList
	 * @return report list
	 */
	public List<ScQtFinalizationReport> briefingPaperDisplayFilter(List<ScQtFinalizationReport> briefingPaperList) {
		List<ScQtFinalizationReport> reportList = new ArrayList<>();
		if (CollectionUtils.isEmpty(briefingPaperList)) {
			return reportList;
		}
		for (ScQtFinalizationReport briefingPaperReport : briefingPaperList) {
			String definitionKey = null != briefingPaperReport.getPmDocument() ? briefingPaperReport.getPmDocument()
					.getDocDefinitionKey() : "";
			String subStringDemoReport = SourcingConstants.DEMO_EVALUATION_REPORT.substring(0, 30);//#28784
			if (!Arrays.asList(SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT,
					SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT,
					SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT,
					SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT).contains(definitionKey)
					&& !briefingPaperReport.getTitle().contains(subStringDemoReport)) {//#28784
				reportList.add(briefingPaperReport);
			}
		}
		return reportList;
	}
	// 37622 - end

	// #29007 Start vinod
	/**
	 * This method will check the BPM version and status to insert item spec
	 * offer.
	 * 
	 * @param scLoa
	 *            .
	 * @return boolean.
	 */
	public boolean checkWIPVersionAndInsertItemSpecOffer(ScLoa scLoa) {
		Long workFlowStatus = scLoa.getPmStatus().getStatusId();
		String docType = scLoa.getScLoiLoa().getDocType();

		ICompositeValiditySO compositeSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICompositeValiditySO.class);

		CompositeValidityReqDTO cReqDTO = new CompositeValidityReqDTO();

		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(docType)) {
			cReqDTO.setCompositeValidityId(QTConstants.COMPOSITE_VALIDITY_ID_DP);
		} else {
			cReqDTO.setCompositeValidityId(QTConstants.COMPOSITE_VALIDITY_ID_QT);
		}

		String minVersion = compositeSO.getCompositeByValidityId(cReqDTO).getPmCompositeValidity()
				.getMinCompositeVersion();
		String version = this.getManagedBean().getBpmVersionNo();

		return isQualifiedToInsertItemSpecOffer(workFlowStatus, minVersion, version);
	}

	// #29007 End vinod
	// #29007 Start vinod
	private boolean isQualifiedToInsertItemSpecOffer(Long workFlowStatus, String minVersion, String version) {
		boolean isOldVersion = StringUtils.isNotBlank(minVersion) && StringUtils.isNotBlank(version)
				&& CustomUtils.compositeVersionComparator(version, minVersion) < 0;
		boolean isPendingAcknowledgement = QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(workFlowStatus);
		boolean isPendingVerify = QTStatusConst.LOA_PENDING_VERIFICATION.equals(workFlowStatus);

		if ((isPendingAcknowledgement || isPendingVerify) && isOldVersion) {
			return true;
		}
		return false;
	}

	// #29007 End vinod
	
	// 26247 - start
	/**
	 * Retrieve mof status descriptions
	 * @param mofAccount
	 * @return mof status description
	 */
	public String evaluateMOFStatus(SmMofAccount mofAccount) {
		if (recordStatusParamMap.isEmpty()) {
			List<PmParameterDesc> paramDescs = this.getParameterDescsByTypeAndSearchBean(
					ParameterTypeConst.RECORD_STATUS, null);
			for (PmParameterDesc paramDesc : paramDescs) {
				recordStatusParamMap.put(
						paramDesc.getPmParameter().getParameterCode().concat(paramDesc.getLanguageCode()),
						paramDesc.getCodeName());
			}
		}

		Locale locale = new FacesScopes().getUserLocale();
		String mofStatus = "0";
		if (null != mofAccount) {
			mofStatus = mofAccount.getRecordStatusFlg().toString();
		}
		return recordStatusParamMap.get(mofStatus.concat(locale.getLanguage()));
	}
	// 26247 - end
	
	// 26247 - start
	/**
	 * Evaluate if qt is disabled for futher action for non/disciplinary action scenario
	 * @param scQt
	 * @param supplier
	 * @return
	 */
	public boolean disabledQTProcess(SmSupplier supplier) {
		if (null == supplier || null == supplier.getScQt()) {
			return false;
		}
		ViewSuppProfileBackingBean viewSuppProfileBean = new ViewSuppProfileBackingBean();
		boolean isDirectDiciplinaryAction = false;
		if (CollectionUtils.isNotEmpty(supplier.getSmDisciplinaryActions())) {
			SmDisciplinaryAction disciplinaryAction = supplier.getSmDisciplinaryActions().get(0);
			isDirectDiciplinaryAction = Arrays.asList(disciplinaryAction.isBlacklistOwner(),
					disciplinaryAction.isCancelRegistration(), disciplinaryAction.isSuspendRegistration()).contains(
					true);
		}
		
		boolean isDisciplinaryAction = isDirectDiciplinaryAction && supplier.isDisciplinaryActions() && !supplier.isWarningDA();
		boolean isCancelledOrBlacklisted = viewSuppProfileBean.evaluateCancellationBlacklist(supplier);
		int mofRecordStatus = supplier.getSmMofAccount().getRecordStatusFlg();
		boolean noBumiAccount = ParameterConst.OPEN_FOR_BUMI.equals(supplier.getScQt().getOpenFor())
				&& !supplier.isBumi();

		List<String> cancelledCategoryCodes = new ArrayList<>();
		cancelledCategoryCodes.addAll(supplier.getCancelledCategoryCodes());
		cancelledCategoryCodes.addAll(supplier.getDeletedCategoryCodes());
		boolean hasCancelledCategories = CollectionUtils.isNotEmpty(cancelledCategoryCodes);

		boolean isSuspendRegistration = viewSuppProfileBean.evaluateSuspendRegistration(supplier);

		return Arrays.asList(hasCancelledCategories, noBumiAccount, isCancelledOrBlacklisted, isDisciplinaryAction,
				isSuspendRegistration).contains(true)
				|| !Arrays.asList(DbValueConst.RECORD_STATUS_ACTIVE, DbValueConst.RECORD_STATUS_EXPIRED).contains(
						mofRecordStatus);
	}
	// 26247 - end
	// #33787 Start Vinod
	/**
	 * This method is added to display title if the checklist is
	 * "Modal Berbayar" then display the title from lable.
	 * 
	 * @param scQtCheckList
	 * 
	 * @return String
	 * 
	 */
	public String getChecklistTitle(ScQtCheckList scQtCheckList) {
		return null != scQtCheckList.getPmDocument()
				&& null != scQtCheckList.getPmDocument().getDocDefinitionKey()
				&& scQtCheckList.getPmDocument().getDocDefinitionKey()
						.equalsIgnoreCase(SourcingConstants.DOC_DEF_KEY_PAID_UP_CAPITAL) ? getMessageCommon(QTConstants.LBL_SC_SPEC_PAID_UP_CAPITAL_INITIAL_FUND)
				: scQtCheckList.getTitle();

	}
	// #33787 End Vinod


	// 29661 -start-
	/**
	 * set all details to requDTO.
	 * 
	 * @param scQtProposalChecklist
	 * @param scQt
	 * @param clientType
	 * @param contractStatus
	 * @param isSearchOperator
	 * @return
	 */
	protected EvaluationReqDTO setDetailsToReqDTO(
			ScQtProposalChecklist scQtProposalChecklist, ScQt scQt,
			String clientType, int contractStatus, boolean isSearchOperator) {

		Long qtId = scQt.getQtId();
		EvaluationReqDTO evaluationReqDTO = new EvaluationReqDTO();
		evaluationReqDTO.setProposalChecklist(scQtProposalChecklist);
		evaluationReqDTO.setScQt(scQt);
		evaluationReqDTO.setQtId(qtId);
		evaluationReqDTO.setCategoryL3Codes(getCategoryL3Codes(scQt));
		evaluationReqDTO.setClientType(clientType);
		evaluationReqDTO.setContractStatus(Integer.valueOf(contractStatus));
		evaluationReqDTO.setSearchOperator(isSearchOperator);
		evaluationReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		return evaluationReqDTO;
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * fetch categoryL3Id's Based on qtId.
	 * 
	 * @param scQt
	 * @return List<Long>
	 */
	protected List<Long> getCategoryL3Codes(ScQt scQt) {
		List<Long> categoryL3Ids = new ArrayList<>();
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtId(scQt.getQtId());
		List<ScQtCategory> qtCategories = getQTDetailSO().getQtCategoryByQtId(
				reqDTO).getCategories();
		if (CollectionUtils.isNotEmpty(qtCategories)) {
			for (ScQtCategory scQtCategory : qtCategories) {
				Long categoryL3Id = scQtCategory.getPmCategoryL3()
						.getCategoryL3Id();
				categoryL3Ids.add(categoryL3Id);
			}
		}
		return categoryL3Ids;
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * Gets the QTEvaluation so.
	 * 
	 * @return the QTEvaluation so
	 */
	private IQTEvaluationSO getQTEvaluationSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * calculate total contract value amount for supplier.
	 * 
	 * @param valueOfAmount
	 * @return BigDecimal.
	 */
	protected BigDecimal calculateContractValueAmount(
			CompanyExpBean valueOfAmount) {
		BigDecimal inEffectiveValue = valueOfAmount
				.getInEffectiveContractValue() != null ? valueOfAmount
				.getInEffectiveContractValue() : BigDecimal.ZERO;
		BigDecimal effectiveValue = valueOfAmount.getEffectiveContractValue() != null ? valueOfAmount
				.getEffectiveContractValue() : BigDecimal.ZERO;
		BigDecimal expiredValue = valueOfAmount.getExpiredContractValue() != null ? valueOfAmount
				.getExpiredContractValue() : BigDecimal.ZERO;
		BigDecimal terminatedValue = valueOfAmount.getTerminatedContractValue() != null ? valueOfAmount
				.getTerminatedContractValue() : BigDecimal.ZERO;
		return inEffectiveValue.add(effectiveValue).add(expiredValue)
				.add(terminatedValue);
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * check contract status type terminated or others.
	 * 
	 * @param contractStatus
	 * @param isTerminated
	 * @return boolean.
	 */
	public boolean checkContractStatusType(int contractStatus,
			boolean isTerminated) {

		if (SourcingConstants.COMPANY_EXP_CONTRACT_STATUS_TERMINATED == contractStatus
				&& isTerminated) {

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * based on contract status code get contract status lable data.
	 * 
	 * @param contractStatusValue
	 * @return String
	 */
	public String checkContractStatusLabel(int contractStatusValue) {

		final Locale locale = new FacesScopes().getUserLocale();

		final String key;
		switch (contractStatusValue) {
		case SourcingConstants.COMPANY_EXP_CONTRACT_STATUS_INACTIVE:
			key = QTConstants.COMPANY_EXP_STATUS_INACTIVE;
			break;
		case SourcingConstants.COMPANY_EXP_CONTRACT_STATUS_EFFECTIVE:
			key = QTConstants.COMPANY_EXP_STATUS_EFFECTIVE;
			break;
		case SourcingConstants.COMPANY_EXP_CONTRACT_STATUS_EXPIRED:
			key = QTConstants.COMPANY_EXP_STATUS_EXPIRED;
			break;
		case SourcingConstants.COMPANY_EXP_CONTRACT_STATUS_TERMINATED:
			key = QTConstants.COMPANY_EXP_STATUS_TERMINATED;
			break;
		default:
			return ""; // unknown status
		}

		return ResourceBundleUtils.getMessage(key,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);

	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * check loa number exist for this company experience.
	 * 
	 * @param content
	 * @return String
	 */
	public String getLabelContent(String content) {

		boolean isNotEmpty = StringUtils.isNotBlank(content)
				&& StringUtils.isNotEmpty(content);

		if (isNotEmpty) {
			return content;
		}
		return ResourceBundleUtils.getMessage(QTConstants.LABEL_TEXT_NA,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * fetch contractver and terminattion detials based on loa number.
	 * 
	 * @param loaNo
	 * @return CtContractVer
	 */
	public CtContractVer retrieveContractVerTerminationDtls(String loaNo) {
		if (StringUtils.isNotBlank(loaNo) && StringUtils.isNotEmpty(loaNo)) {
			IContractTerminationSO contractTerminationSO = BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT,
							IContractTerminationSO.class);
			ContractReqDTO contractReqDTO = new ContractReqDTO();
			contractReqDTO.setLoaNo(loaNo);
			ContractResDTO contractResDTO = contractTerminationSO
					.retrieveContractVerTerminationDtls(contractReqDTO);

			return checkContractVer(contractResDTO.getCtContractVer());
		} else {
			return checkContractVer(null);
		}
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * create contractver and termination instances for the terminated
	 * contracts.
	 * 
	 * @param ctContractver
	 * @return CtContractVer
	 */
	private CtContractVer checkContractVer(CtContractVer ctContractver) {
		CtContractVer newCtContractver = new CtContractVer();
		if (null == ctContractver) {
			newCtContractver.setCtTermination(new CtTermination());
		} else {
			return ctContractver;
		}

		return newCtContractver;
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * check loa reports exist for this supplier.
	 * 
	 * @param prpExp
	 * @return true/false.
	 */
	public boolean loaReportCheck(ScQtPRPExperience prpExp) {
		PmParameter sourceType = prpExp.getSourceType();
		boolean isSourceType = null != sourceType && null != prpExp.getScLoa();

		boolean isLoaSourceType = isSourceType
				&& SourcingConstants.SOURECE_TYPE_QT_ID.equals(sourceType
						.getParameterId())
				&& CollectionUtils.isNotEmpty(prpExp.getScLoa()
						.getScLoaReports());

		boolean isSelfSourceType = null != sourceType
				&& SourcingConstants.SOURECE_TYPE_SELF_ID.equals(sourceType
						.getParameterId())
				&& null != prpExp.getSmCompExpMaster()
				&& CollectionUtils.isNotEmpty(prpExp.getSmCompExpMaster()
						.getCompExpMasterDocs());

		if (isLoaSourceType || isSelfSourceType) {

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * download loareportsdetils based on loa number.
	 * 
	 * @param scQtPRPExperience
	 * @param scQt
	 * @throws IOException
	 */
	public void downloadCompExpLOAReports(ScQtPRPExperience scQtPRPExperience)
			throws IOException {
		try {
			PmParameter sourceType = scQtPRPExperience.getSourceType();
			boolean isSourceType = null != sourceType
					&& null != scQtPRPExperience.getScLoa();
			boolean isLoaSourceType = isSourceType
					&& SourcingConstants.SOURECE_TYPE_QT_ID.equals(sourceType
							.getParameterId())
					&& CollectionUtils.isNotEmpty(scQtPRPExperience.getScLoa()
							.getScLoaReports());
			boolean isSelfSourceType = null != sourceType
					&& SourcingConstants.SOURECE_TYPE_SELF_ID.equals(sourceType
							.getParameterId())
					&& null != scQtPRPExperience.getSmCompExpMaster()
					&& CollectionUtils.isNotEmpty(scQtPRPExperience
							.getSmCompExpMaster().getCompExpMasterDocs());
			if (isSelfSourceType) {
				downloadCompExpSupportingDocuments(scQtPRPExperience
						.getSmCompExpMaster().getCompExpMasterDocs());
			} else if (isLoaSourceType) {
				ScQt scQt = scQtPRPExperience.getScLoa().getScQt();
				downLoadLOAReports(scQtPRPExperience, scQt);
			}
		} catch (Exception e) {
			Long qtPrpExpId = scQtPRPExperience.getQtPrpExperienceId();
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_SOURCING_BACKINGBEAN,
					String.valueOf(qtPrpExpId),
					SLALogConstant.DOWNLOAD_SUPPLIER_LOA_DETAILS_REPORTS,
					getUserIdSessionFromLoginId(), e);
		}
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * download loa compnay experience report details.
	 * 
	 * @param scQtPRPExperience
	 * @param scQt
	 * @throws IOException
	 */
	private void downLoadLOAReports(ScQtPRPExperience scQtPRPExperience,
			ScQt scQt) throws IOException {
		ScLoa scLoa = scQtPRPExperience.getScLoa();
		LOADetailBackingBean loaDetailAll = new LOADetailBackingBean();
		AwardSupplierReqDTO awardSuppReqDTO = new AwardSupplierReqDTO();
		awardSuppReqDTO.setScLoa(scLoa);

		AwardSupplierResDTO awardSuppResDTO = getQtAwardSO().retrieveLOADtl(
				awardSuppReqDTO);

		loaDetailAll.getManagedBean().setScLoa(awardSuppResDTO.getScLoa());
		loaDetailAll.getManagedBean().getScLoa()
				.setPmStatus(awardSuppResDTO.getScLoa().getPmStatus());
		loaDetailAll.getManagedBean().getScLoa()
				.setScLoaReports(awardSuppResDTO.getScLoa().getScLoaReports());
		if (null != awardSuppResDTO.getScQt()) {
			loaDetailAll.getManagedBean().setScQt(awardSuppResDTO.getScQt());
		} else {
			loaDetailAll.getManagedBean().setScQt(scQt);
		}
		if (awardSuppResDTO.getScLoa().getContractSigner() == null) {
			awardSuppResDTO.getScLoa().setContractSigner(new PmUser());
			loaDetailAll.getManagedBean().setContractSignerUser(
					awardSuppResDTO.getScLoa().getContractSigner());
		} else {
			loaDetailAll.getManagedBean().setContractSignerUser(
					awardSuppResDTO.getScLoa().getContractSigner());
		}

		getSuppLoiLoaMapData(loaDetailAll);
		loaDetailAll.downloadAllReports();
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * getQtAwardSO - Gets the qt award so.
	 * 
	 * @return the qtAwardSO
	 */
	private IQTAwardSO getQtAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * getSuppLoiLoaMapData - Gets the supp loi loa map data.
	 * 
	 * @param loaDetail
	 *            the loa detail
	 * 
	 */
	private void getSuppLoiLoaMapData(LOADetailBackingBean loaDetail) {
		Long qtId = loaDetail.getManagedBean().getScQt().getQtId();
		loaDetail.getManagedBean().setSuppLoiLoaMap(
				retrieveSuppLoiLoaMapForReport(qtId));

	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * retrieveSuppLoiLoaMapForReport - will retrieving SuppLoiLoaMap based on
	 * qtid.
	 * 
	 * @param qtId
	 *            the qt id
	 * @retrun the list scloiloa instances.
	 */
	private Map<Long, List<ScLoiLoa>> retrieveSuppLoiLoaMapForReport(Long qtId) {

		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocId(qtId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAList(reqDTO);

			return resDTO.getLoiLoaSupplierMap();

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_SOURCING_BACKINGBEAN,
					String.valueOf(qtId),
					SLALogConstant.RETRIEVE_SUPP_MAP_REPORT,
					getUserIdSessionFromLoginId(), e);
		}
		return null;

	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * retrieve company exp contract status and value count.
	 * 
	 * @param scQtProposalChecklist
	 * @param scQt
	 * @param clientType
	 * @param contractStatus
	 * @param isSearchOperator
	 * @return contract status and value count objects.
	 */
	protected EvaluationResDTO retrieveContractStatusValueCount(
			ScQtProposalChecklist scQtProposalChecklist, ScQt scQt,
			String clientType, int contractStatus, boolean isSearchOperator) {
		try {

			EvaluationReqDTO evaluationReqDTO = setDetailsToReqDTO(
					scQtProposalChecklist, scQt, clientType, contractStatus,
					isSearchOperator);
			return getQTEvaluationSO().retrieveContractStatusValueCount(
					evaluationReqDTO);
		} catch (Exception e) {
			Long qtProposalCheckListId = scQtProposalChecklist
					.getQtProposalChecklistId();
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_SOURCING_BACKINGBEAN,
					String.valueOf(qtProposalCheckListId),
					SLALogConstant.RETRIEVE_CONTRACT_STATUS_COUNT,
					getUserIdSessionFromLoginId(), e);
		}

		return null;

	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * The Class ViewSupplierCompExpDtlsPagingDataModel.
	 */
	protected class ViewSupplierCompExpDtlsPagingDataModel extends
			PagingDataModel<ScQtPRPExperience> {
		private static final long serialVersionUID = 1L;
		private EvaluationReqDTO evaluationReqDTO;

		public ViewSupplierCompExpDtlsPagingDataModel(String componentId) {
			super(componentId);
		}

		public ViewSupplierCompExpDtlsPagingDataModel(String componentId,
				EvaluationReqDTO evaluationReqDTO) {
			super(componentId, Boolean.TRUE);
			this.evaluationReqDTO = evaluationReqDTO;
		}

		@Override
		protected ISearchResultBean<ScQtPRPExperience> pageChangeListener(
				ISearchBean<ScQtPRPExperience> searchBean) {

			try {
				evaluationReqDTO.setCompExpDtlsSerachBean(searchBean);
				EvaluationResDTO evaluationResDTO = getQTEvaluationSO()
						.retrieveCompanyExpDetails(evaluationReqDTO);

				return evaluationResDTO.getCompExpDtlsSerachResults();
			} catch (Exception e) {
				Long qtId = evaluationReqDTO.getQtId();

				SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
						SLALogConstant.QT_SOURCING_BACKINGBEAN,
						String.valueOf(qtId),
						SLALogConstant.COMP_EXP_SUPP_PAGING_DATA_MODEL,
						getUserIdSessionFromLoginId(), e);

			}
			return null;
		}
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * download supplier managemnt supporting doucmnets during proposal and
	 * evalation company experience viewing time.
	 * 
	 * @param supportingDocuments
	 * @throws IOException
	 */
	public void downloadCompExpSupportingDocuments(
			List<SmCompExpMasterDoc> supportingDocuments) throws IOException {
		if (CollectionUtils.isNotEmpty(supportingDocuments)) {
			List<ScAttachment> attachmentList = new ArrayList<>();
			for (SmCompExpMasterDoc compExpDocs : supportingDocuments) {
				ScLoaReportAttachment attachemnt = new ScLoaReportAttachment();
				attachemnt.setFileName(compExpDocs.getFileName());
				attachemnt.setFilePath(compExpDocs.getFilePath());
				attachmentList.add(attachemnt);

			}
			downloadAllFiles(QTConstants.COMP_EXP_SUPPORTING_DOCUMENTS,
					attachmentList);
		}
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * This method is used to view the Loa Details page.
	 * 
	 * @param scQtPRPExperience
	 * @param previousPageUrl
	 * @return loaPagedetails
	 * 
	 */
	public String viewLOADetailsPage(ScQtPRPExperience scQtPRPExperience,
			String previousPageUrl) {
		try {
			ScLoa scLoa = scQtPRPExperience.getScLoa();
			if (null != scLoa && null != scLoa.getLoaId()) {
				EnumMap<MapKeyEnum, Object> loaArgs = new EnumMap<>(
						MapKeyEnum.class);
				ScLoaForQt scLoaForQt = new ScLoaForQt();
				Long loaId = scLoa.getLoaId();

				scLoaForQt.setLoaId(loaId);
				loaArgs.put(MapKeyEnum.loiLoaSelected, scLoaForQt);

				QtLOABackingBean qtLoaBbean = new QtLOABackingBean();

				loaArgs.put(MapKeyEnum.showTrackingDiary, Boolean.TRUE);
				ScQt scQt = scLoa.getScQt();
				qtLoaBbean.preRenderViewForQtMaintenance(scQt, previousPageUrl,
						loaArgs);
			}

		} catch (Exception e) {
			Long qtPrpExpId = scQtPRPExperience.getQtPrpExperienceId();

			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_SOURCING_BACKINGBEAN,
					String.valueOf(qtPrpExpId),
					SLALogConstant.COMP_EXP_LOA_DETAILS_PAGE,
					getUserIdSessionFromLoginId(), e);
		}

		return null;
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * check portlet is supplier proposal
	 * 
	 * @return boolean
	 */
	public boolean isCheckProposalPortlet() {
		return QTConstants.PORTLET_NAME_SUPPLIER_PROPOSAL.equals(EpPortletUtil
				.getDisplayPortletId());
	}

	// 29661 -end-

	// 29661 -start-
	/**
	 * check the portlet is evaluation
	 * 
	 * @return boolean.
	 */
	public boolean isCheckEvaluationPortlet() {
		return QTConstants.PROPOSAL_EVALUATION_PORTLET.equals(EpPortletUtil
				.getDisplayPortletId());
	}

	// 29661 -end-

}
