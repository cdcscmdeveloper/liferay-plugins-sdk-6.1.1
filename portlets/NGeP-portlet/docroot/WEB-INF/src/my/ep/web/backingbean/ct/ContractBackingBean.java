/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.dtl.IContractZoneSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.sc.document.IQTDocumentSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.ct.dtl.ContractZoneReqDTO;
import my.ep.dto.ct.dtl.ContractZoneResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.sc.document.QTDocumentReqDTO;
import my.ep.dto.sc.document.QTDocumentResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtAddress;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtBondWaiverDetail;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.CtWorkflowStatus;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtPidReason;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.VCtAgencyDeliveryAddress;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItemSpecOffer;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.ct.CtAddressUtil;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.backingbean.sc.loa.DirectLOABackingBean;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.ContractManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.NumberWordUtilEn;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
//Enhancement 13847 - shaqimi
//import my.ep.biz.service.ct.dtl.ContractItemSO;

/**
 * Backing Bean For Contract
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="ctBackingBean")
@ViewScoped
public class ContractBackingBean extends BaseBackingBean<ContractManagedBean> {

	private static Logger log = EpLogger.getLogger(ContractBackingBean.class);

	private static final long serialVersionUID = 1L;
	private CtSchedule[] selectedSchedule;
	private CtProgressivePayment[] selectedProgressive;
	private CtDeductionClause[] selectedDeductionClauses;
	private BigDecimal paidPercentage = new BigDecimal(0);	
	private BigDecimal paidsumPercentage = new BigDecimal(0);	
	private BigDecimal paidAdvancePercentage = new BigDecimal(0);
	private BigDecimal balanceToPaid = new BigDecimal(0);
	private SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
	private IPagingDataModel<PmOrgProfile> agencyDataModel = new AgencyPagingDataModel();	
	private static final String CENTRAL_CIRCULAR_PAGE = "/xhtml/ct/circular/portal/centralCircularTab.xhtml?faces-redirect=true";
	private static final Map<String, String> months;
	
	
	static
	{
		months = new HashMap<String, String>();
		months.put("01", "January");
		months.put("02", "February");
		months.put("03", "March");
		months.put("04", "April");
		months.put("05", "May");
		months.put("06", "June");
		months.put("07", "July");
		months.put("08", "August");
		months.put("09", "September");
		months.put("10", "October");
		months.put("11", "November");
		months.put("12", "December");  
	}

	/**
	 * Constructor used to initialize file path directory
	 */
	public ContractBackingBean() {
		if (getManagedBean().getFileDirectory() == null) {
			getManagedBean().setFileDirectory(EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT");
		}
	}

	/**
	 * Constructor used for specifying the directory of file storage.	 
	 * If the fileDirectory is null, default to temporary directory.
	 * 
	 * @param fileDirectory  the directory for file storage. 
	 */
	public ContractBackingBean(String fileDirectory) {
		if (fileDirectory == null) {
			getManagedBean().setFileDirectory(EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT");
		} else {
			getManagedBean().setFileDirectory(fileDirectory);
		}
	}

	/**
	 * The method used for initializing the remarks and attachments.
	 * This method will create new remark and attachment object
	 * for every single new process, else will display all
	 * remarks and attachments.
	 * 
	 * @param ctWorkflowStatus  the status of current contract. It is null for every single new process
	 * @param remarkList        the list of remarks to display. If the remarkList is null, 
	 *              		    initiate a new remark
	 * @param attachmentList    the list of attachments to display. If the attachmentList is null, 
	 *              		    initiate a new attachment
	 */
	public void initializeRemarkAndAttachement(CtWorkflowStatus ctWorkflowStatus, 
			List<CtRemark> remarkList, List<CtAttachment> attachmentList) {
		FacesScopes facesScopes = new FacesScopes();

		boolean ctWorkflowStatusMatch = false;
		if (ctWorkflowStatus != null) {
			if (remarkList.size() > 0 && remarkList.get(0).getCtWorkflowStatus() != null) {
				if (ctWorkflowStatus.getWorkflowStatusId() == remarkList.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}

		//Bug #29446 - start
		log.info(!getManagedBean().isViewContract());
		if(!getManagedBean().isViewContract()){
			if (remarkList.size() == 0 || !ctWorkflowStatusMatch) {
				CtRemark remark = new CtRemark();
				PmUser pmUser = facesScopes.getUserSessionBean();
				remark.setCreatedBy(pmUser.getUserId());
				remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				remark.setChangedBy(remark.getCreatedBy());
				remark.setChangedDate(remark.getCreatedDate());
				remark.setCreatedUser(pmUser);
				getManagedBean().setRemarkList(remarkList);
				getManagedBean().getRemarkList().add(0, remark);
			} else {
				getManagedBean().setRemarkList(remarkList);
			}
		} else if (getManagedBean().isViewContract()){
			getManagedBean().setRemarkList(remarkList);
		}
		getManagedBean().setAttachmentList(attachmentList);
		//Bug #29446 - end
	}	
	
	

	/**
	 * Returns a representative icon filename that can then be displayed
	 * on the screen for the attachment.
	 * 
	 * @param  attachment     an object of attached file
	 * @return imageFileName  the icon filename base on the filename extension
	 */
	public String determineIcon(CtAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String fileExtension = "";

		String fileName = attachment.getFileName();

		// Retrieve the file extension base on filename
		if (fileName != null) {
			int mid = fileName.lastIndexOf(".");
			fileExtension = fileName.substring(mid + 1, fileName.length());  
		}

		if ("doc".equals(fileExtension) || "docx".equals(fileExtension)) {
			imageFileName = "icon_doc.png";
		} else if ("pdf".equals(fileExtension)) {
			imageFileName = "icon_pdf.png";
		} else if ("xls".equals(fileExtension) || "xlsx".equals(fileExtension) 
				|| "csv".equals(fileExtension)) {
			imageFileName = "icon_exel.png";
		} else if ("jpg".equals(fileExtension) || "bmp".equals(fileExtension) 
				|| "png".equals(fileExtension) || "gif".equals(fileExtension)) {
			imageFileName = "icon_image.png";
		} else if ("txt".equals(fileExtension)) {
			imageFileName = "icon_txt.png";
		}
		return imageFileName;
	}

	/**
	 * The method used to upload file into the attachment list.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void fileUpload(FileUploadEvent event) throws Exception {

		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		UploadedFile item = event.getFile();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();

		CtAttachment attachment = new CtAttachment();
		attachment.setFileName(item.getFileName());
		attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));

		if (event.getComponent().getId().equalsIgnoreCase("uploadAgency1")){
			getManagedBean().getContractVer().getAgencyAttachements().add(attachment);
		} else if (event.getComponent().getId().equalsIgnoreCase("uploadItemId")){
			getManagedBean().getContractVer().getItemAttachments().add(attachment);
		} else if (event.getComponent().getId().equalsIgnoreCase("uploadDeductId")){
			getManagedBean().getContractVer().getDeductionAttachments().add(attachment);
		} else if (event.getComponent().getId().equalsIgnoreCase("uploadScheduleId")){
			getManagedBean().getContractVer().getScheduleAttachments().add(attachment);
		} else if (event.getComponent().getId().equalsIgnoreCase("uploadItemAmendId")){
			getManagedBean().getContractVer().getItemAmendAttachments().add(attachment);
		} else if (event.getComponent().getId().equalsIgnoreCase("uploadTermLetterId")){
			getManagedBean().getContractVer().getTerminationLetterAttachments().add(attachment);
			RequestContext.getCurrentInstance().execute("uploadTermLetterDlg.hide()");
		} else if (event.getComponent().getId().equalsIgnoreCase("approvalLetterId")){
			getManagedBean().getContractVer().setApprovalLetterAttachement(attachment);
		} else if (event.getComponent().getId().equalsIgnoreCase("pbqcDecisionLetterId")){
			getManagedBean().getContractVer().setPbqcDecisionLetterAttachment(attachment);
		} else if (event.getComponent().getId().equalsIgnoreCase("mofDecisionLetterId")){
			getManagedBean().getContractVer().setMofDecisionLetterAttachment(attachment);
		} 
		//26186 start
		else if (event.getComponent().getId().equalsIgnoreCase("disciplinaryAttachmentId")){
			getManagedBean().getContractVer().getDisciplinaryActionAttachments().add(attachment);
		} 
		//26186 end
		
		//#29549 start
		else if(event.getComponent().getId().equalsIgnoreCase("meetingAmendmendAttachmentId")){
			attachment.setDocType(getManagedBean().getContractVer().getDocType());
			attachment.setDocId(getManagedBean().getContractVer().getContractVerId());
			attachment.setSectionId(ContractConst.ATTACHMENT_MEETING_AMENDMENT);
			getManagedBean().getContractVer().getCtAmendment().getCtMeetings().get(this.getManagedBean().getAttachFileIndex()).setMeetingAmendmentAttachment(attachment);
		}
		//#29549 end
		
		else {
			getManagedBean().getAttachmentList().add(attachment);
		}
	}
	
	//#Start Fix - #31657
	/**
	 * The method used to remove file from the attachment list.
	 * 
	 * @param  event      the delete event for file attach from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void deleteAttachmentActionByEvent(ActionEvent event) throws Exception {
		CtAttachment attachment = new CtAttachment();
		if(event.getComponent().getId().equalsIgnoreCase("deleteItemAttachment")){
			attachment = getManagedBean().getContractVer().getItemAttachments().get(this.getManagedBean().getAttachFileIndex());
			 if(attachment.getAttachmentId() != null){
					getManagedBean().getDeleteAttachmentList().add(attachment);
				}
				this.getManagedBean().getContractVer().getItemAttachments().remove(getManagedBean().getAttachFileIndex());
		}
		//#26186 start
		else if(event.getComponent().getId().equalsIgnoreCase("delDisciplinaryAttachmentId")){
			attachment = getManagedBean().getContractVer().getDisciplinaryActionAttachments().get(this.getManagedBean().getAttachFileIndex());
			if(attachment.getAttachmentId() != null){
				getManagedBean().getDeleteAttachmentList().add(attachment);
			}
			getManagedBean().getContractVer().getDisciplinaryActionAttachments().remove(this.getManagedBean().getAttachFileIndex());
		}
		//#26186 end
		//#29549 start
		else if(event.getComponent().getId().equalsIgnoreCase("delMeetingAmendmentAttachId")){
			attachment = getManagedBean().getContractVer().getCtAmendment().getCtMeetings().get(this.getManagedBean().getAttachFileIndex()).getMeetingAmendmentAttachment();
			if(attachment.getAttachmentId() != null){
				getManagedBean().getDeleteAttachmentList().add(attachment);
			}
			
			//getManagedBean().getContractVer().getCtAmendment().getCtMeetings().get(this.getManagedBean().getAttachFileIndex()).setMeetingAmendmentAttachment(new CtAttachment());		
			getManagedBean().getContractVer().getCtAmendment().getCtMeetings().get(this.getManagedBean().getAttachFileIndex()).setMeetingAmendmentAttachment(null);
		} //#29549 end
		else {
			attachment = this.getManagedBean().getAttachmentList().get(getManagedBean().getAttachFileIndex());
			if(attachment.getAttachmentId() != null){
				getManagedBean().getDeleteAttachmentList().add(attachment);
			}
			this.getManagedBean().getAttachmentList().remove(getManagedBean().getAttachFileIndex());
		}
	}
	//#End Fix - #31657

	/**
	 * The method used to download the attached file.
	 * 
	 * @param  attachment   an object of attached file
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadFile(CtAttachment attachment) throws IOException {
		commonDownload(attachment.getFilePath(), attachment.getFileName());
	}

	/**
	 * The method used to set the file index of attachment list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void setAttachFileIndex(int index) {
		this.getManagedBean().setAttachFileIndex(index);
	}

	/**
	 * The method used to delete attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteAttachmentActionByIndex(int index) {
		CtAttachment attachment = this.getManagedBean().getAttachmentList().get(index);
		if(attachment.getAttachmentId() != null){
			getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		this.getManagedBean().getAttachmentList().remove(index);
	}

	/**
	 * The method used to delete attachment from the list.
	 * 
	 * @param ctAttachment  an object of attached file
	 */
	public void deleteAttachmentAction(CtAttachment ctAttachment) {
		if (ctAttachment.getAttachmentId() != null){
			getManagedBean().getDeleteAttachmentList().add(ctAttachment);
		}
		ctAttachment.setFileName(null);
		ctAttachment.setFilePath(null);		
	}

	/**
	 * The method used to delete agency attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteAgncyAttachmentAction(int index) {
		CtAttachment attachment = getManagedBean().getContractVer().getAgencyAttachements().get(index);
		if(attachment.getAttachmentId() != null){
			getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		getManagedBean().getContractVer().getAgencyAttachements().remove(index);
	}

	/**
	 * The method used to delete bond waiver attachment from the list.
	 */
	public void deleteBondWaiverAttach() {
		//LOG : 13847, by Fahmi 22032019
		if(getManagedBean().getCtAttachmentLatest() != null){
			getManagedBean().getCtAttachmentLatest().setFileName(null);
		}
		RequestContext.getCurrentInstance().execute("deleteWaiverAttachDlg.hide()");
		
	}
	
	//Enhancement 13847 - start
	public void deleteWaiverHistoryAttachment() throws EpException {
		CtAttachment ctAttachmentHistory = getManagedBean().getWaiverDetail().get(getManagedBean().getAttachFileIndex());

		ctAttachmentHistory.setFileName(null);
		ctAttachmentHistory.setFilePath(null);
		
		RequestContext.getCurrentInstance().execute("deleteHistoryWaiverAttachDlg.hide()");
	}
	//Enhancement 13847 - end

	/**
	 * The method used to delete termination letter attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteTerminationLetterAction(int index) {
		CtAttachment attachment = getManagedBean().getContractVer().getTerminationLetterAttachments().get(index);
		if(attachment.getAttachmentId() != null){
			getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		getManagedBean().getContractVer().getTerminationLetterAttachments().remove(index);
	}

	/**
	 * The method used to delete item attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteItemAttachmentAction(int index) {
		CtAttachment attachment = getManagedBean().getContractVer().getItemAmendAttachments().get(index);
		if(attachment.getAttachmentId() != null){
			getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		getManagedBean().getContractVer().getItemAmendAttachments().remove(index);
	}

	/**
	 * The method used to delete deduction attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteDeductAttachmentAction(int index) {
		CtAttachment attachment = getManagedBean().getContractVer().getDeductionAttachments().get(index);
		if(attachment.getAttachmentId() != null){
			getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		getManagedBean().getContractVer().getDeductionAttachments().remove(index);
	}

	/**
	 * The method used to delete schedule attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteScheduleAttachmentAction(int index){
		CtAttachment attachment = getManagedBean().getContractVer().getScheduleAttachments().get(index);
		if(attachment.getAttachmentId() != null){
			getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		getManagedBean().getContractVer().getScheduleAttachments().remove(index);
	}
		
	/**
	 * The method used to retrieve the address of supplier.
	 * 
	 * @param ajax event
	 */
	public void retrieveSupplierAddress(AjaxBehaviorEvent event) {
		if (((SelectBooleanCheckbox)event.getSource()).isSelected()) {
			getManagedBean().setCtAddress(CtAddressUtil.retrieveSupplierAddress(getManagedBean().getContractVer().getCtContract().getSmSupplier()));
		}
		else {
			if (getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) {
				getManagedBean().setCtAddress(
						getManagedBean().getContractVer().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress());
			} else {
				getManagedBean().setCtAddress(
						getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
			} 
		}
		getManagedBean().setSupplierAddress(getFullAddress(getManagedBean().getCtAddress()));
	}	

	/**
	 * The method used to retrieve the company name of supplier.
	 * 
	 * @param ajax event
	 */
	public void retrieveCompanyName(AjaxBehaviorEvent event) {
		if (((SelectBooleanCheckbox)event.getSource()).isSelected()) {
			// Supplier Company Name
			getManagedBean().setSupplierCompanyName(
					getManagedBean().getContractVer().getCtContract().getSmSupplier().getCompanyName());
			// Retrieve Supplier Branch Name
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
			SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
			reqDTO.setSmAppl(getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl());
			SupplierSearchResDTO resDTO = supplierSearchSO.retrieveUpdateSmSupplierBranchFromLatestApplication(reqDTO);			
			if (resDTO.getLatestBranch() != null) {
				getManagedBean().setSupplierBranchName(resDTO.getLatestBranch().getBranchName());
			}
			// Retrieve Supplier Reg No
			getManagedBean().setSupplierRegNo(
					getManagedBean().getContractVer().getCtContract().getSmSupplier().getRegNo());
			// Retrieve Mof No by supplier id
			IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
			MofAccountReqDTO mofReqDTO = new MofAccountReqDTO();
			mofReqDTO.setSupplierId(getManagedBean().getContractVer().getCtContract().getSmSupplier().getSupplierId());
			MofAccountResDTO mofresDTO = mofAccountSO.retrieveMofNoBySupplierId(mofReqDTO);
			if (mofresDTO.isSuccessful() && mofresDTO.getMofAccount() != null) {
				getManagedBean().setSupplierMofNo(mofresDTO.getMofAccount().getMofNo());
			}
		} else {
			if (getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) {
				getManagedBean().setSupplierCompanyName(getManagedBean().getContractVer().getCtContract().getLatestContractVer().getCtContractValue().getSupplierName());
				getManagedBean().setSupplierBranchName(getManagedBean().getContractVer().getCtContract().getLatestContractVer().getCtContractValue().getSupplierBranch());
				getManagedBean().setSupplierRegNo(getManagedBean().getContractVer().getCtContract().getLatestContractVer().getCtContractValue().getSupplierRegNo());
				getManagedBean().setSupplierMofNo(getManagedBean().getContractVer().getCtContract().getLatestContractVer().getCtContractValue().getSupplierMofNo());
			} else {
				getManagedBean().setSupplierCompanyName(getManagedBean().getContractVer().getCtContractValue().getSupplierName());
				getManagedBean().setSupplierBranchName(getManagedBean().getContractVer().getCtContractValue().getSupplierBranch());
				getManagedBean().setSupplierRegNo(getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
				getManagedBean().setSupplierMofNo(getManagedBean().getContractVer().getCtContractValue().getSupplierMofNo());
			}
		}
	}

	/**
	 * The method used to check whether contract is services with LOA signed 
	 * before GST effective date (01/04/2015).
	 * 
	 * @return true, if contract is services and LOA is signed before 01/04/2015
	 *         false, if contract is not services or LOA is not signed before 01/04/2015
	 */
	public boolean imposedServiceTaxCondition() {
		try {
			if (getManagedBean().getContractVer().getCtContractValue().getProcurementTypeCategory() != null
					&& getManagedBean().getContractVer().getCtContractValue().getProcurementTypeCategory().getParameterCode() != null) {
				if (getManagedBean().getContractVer().getCtContractValue().getProcurementTypeCategory().getParameterCode().equalsIgnoreCase(
						ParameterConst.PROCUREMENT_TYPE_CODE_SERVICE)) {
					if (getManagedBean().getContractVer().getCtContract().getAckDate().getTime() < getGstLaunchDate().getTime()) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return false;
	}

	/**
	 * The method used to check whether current date is before the GST relief due date (31/03/2017).
	 * 
	 * @return true, if current date is before or equal 31/03/2017
	 *         false, if current date is after 31/03/2017
	 */
	public boolean isBeforeGstReliefDueDate() {
		try {
			if (DateUtils.getCurrentDateWithoutTime().getTime() <= getGstReliefDueDate().getTime()) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * The method used to set the alert flag 
	 * when the original contract imposed service tax.
	 */
	public void chkServiceTaxAlert() {
		try {
			if (isBeforeGstReliefDueDate()	// currentDate <= 31/3/2017
					&& imposedServiceTaxCondition()	// ack_date < 01/04/2015 & procurement category = Service
					&& isLoaContractWithGst(getManagedBean().getContractVer().getCtContractValue().getGstEffDate())	// supplier gst is effective
					&& !getManagedBean().getContractVer().getCtContractValue().isServiceTax()	// imposed_service_tax = No
					&& getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() > getGstReliefDueDate().getTime()) {	// contract_end_date > 31/3/2017
				getManagedBean().setServiceTaxAlertFlg(true);				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The method used to verify the login user is BPK CC approver 
	 * or contract approver.
	 * 
	 * @return true, if login user is BPK CC approver or contract approver
	 *         false, if login user is not BPK CC approver or contract approver
	 */
	public boolean isApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return (facesScopes.hasRoleUser(RoleCode.CT_APPROVER.value()) 
				|| facesScopes.hasRoleUser(RoleCode.BPK_CC_APPROVER.value()));
	}

	/**
	 * The method used to retrieve the branch info file path
	 */
	public void retrieveBranchInfo() {
		IQTDocumentSO qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
		QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();

		String docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER; // AQ
		if (getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_DIRECT_PURCHASE; // AD
		}
		reqDTO.setDocCategory(docCategory);
		reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION);
		ScLoaReport scLoaReport = new ScLoaReport();
		scLoaReport.setScLoa(new ScLoa());
		scLoaReport.getScLoa().setLoaId(getManagedBean().getContractVer().getCtContract().getLoa().getLoaId());
		reqDTO.setScReport(scLoaReport);

		QTDocumentResDTO resDTO = qtDocumentSO.retriveScQtReportByDefKey(reqDTO);
		scLoaReport = (ScLoaReport)resDTO.getScReport();

		String filePath = (scLoaReport != null && scLoaReport.getScLoaReportAttachment() != null) ? scLoaReport.getScLoaReportAttachment().getFilePath() : null;
		getManagedBean().setBranchInfoFilePath(filePath);
	}

	/**
	 * The method used to download Item and Specification Guideline file 
	 */
	public void downloadItemSpecGuideline() throws IOException {
		String docRepoSharedDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_DIR);

		StringBuilder filePath = new StringBuilder(docRepoSharedDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ContractConst.DOCNAME_ITEM_SPEC_GUIDELINE);

		commonDownload(filePath.toString(), ContractConst.DOCNAME_ITEM_SPEC_GUIDELINE);
	}

	/* Implementation of contract schedule start*/
	/**
	 * The method used to reset the schedule information once 
	 * user select the schedule type.
	 */
	public void changeScheduleTypeListener() {		
		getManagedBean().setChkSchedule(false);
		getManagedBean().setChkProgressive(false);
		getManagedBean().setProgSchedButtonDeactive(false);
		getManagedBean().setButtonHideSchActive(true);
		if (getManagedBean().getFrequencySchedule().equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_SCHEDULE)) {
			getManagedBean().getContractVer().getCtContract().setFrequencySchedule(true);
		} else if (getManagedBean().getFrequencySchedule().equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_PROGRESSIVE)) {
			getManagedBean().getContractVer().getCtContract().setFrequencySchedule(false);
		}   
	}

	/**
	 * The method used for generating a list of available start month
	 * as selection. 
	 * 
	 * @param tableStartYear  start year
	 * @return selectItems    the selection list of start month
	 */
	public List<SelectItem> retrieveStartMonthList(String  tableStartYear) {
		List<SelectItem> selectItems = null;

		try {

			int schedStartYear = 0;
			String fromdate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate());
			int startMonth = Integer.valueOf(fromdate.substring(3, 5));
			int startYear = Integer.valueOf(fromdate.substring(6, 10));

			if (tableStartYear != null && !tableStartYear.equalsIgnoreCase("")) {
				schedStartYear = Integer.valueOf(tableStartYear);
			}
			String enddate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			int endMonth = Integer.valueOf(enddate.substring(3, 5));
			int endYear = Integer.valueOf(enddate.substring(6, 10));

			selectItems = new ArrayList<SelectItem>();
			selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));		
			if (schedStartYear == 0 && startYear == endYear) { 
				for (int i = startMonth; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if ((schedStartYear == 0) || (schedStartYear == startYear && schedStartYear != endYear)) { 
				for (int i = startMonth; i <= 12; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if (schedStartYear == startYear  && schedStartYear == endYear) { 
				for (int i = startMonth; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if (schedStartYear != startYear  && schedStartYear == endYear) { 
				for (int i = 1; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if (schedStartYear > startYear && schedStartYear < endYear) {
				for (int i = 1; i <= 12; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} 
		} finally { }

		return selectItems;
	}

	/**
	 * The method used for generating a list of available end month
	 * as selection.
	 * 
	 * @param tableEndYear   end year
	 * @return selectItems   the selection list of end month
	 */
	public List<SelectItem> retrieveEndMonthList(String tableEndYear) {
		List<SelectItem> selectItems = null;

		try {

			int schedEndYear =0;
			String fromdate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate());
			int startMonth = Integer.valueOf(fromdate.substring(3, 5));
			int startYear = Integer.valueOf(fromdate.substring(6, 10));

			if (tableEndYear != null && !tableEndYear.equalsIgnoreCase("")) {
				schedEndYear = Integer.valueOf(tableEndYear);
			}

			String enddate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			int endMonth = Integer.valueOf(enddate.substring(3, 5));
			int endYear = Integer.valueOf(enddate.substring(6, 10));

			selectItems = new ArrayList<SelectItem>();
			selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));						 
			if (schedEndYear == endYear && schedEndYear == startYear) { 
				for (int i = startMonth; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if (schedEndYear == endYear && schedEndYear != startYear) { 
				for (int i = 1; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if (schedEndYear != endYear && schedEndYear == startYear) { 
				for (int i = startMonth; i <= 12; i++) { 
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if ( schedEndYear >  startYear && schedEndYear < endYear) {
				for (int i = 1; i <= 12; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} 
		} finally { }
		return selectItems;
	}

	/**
	 * The method used to convert the month index to
	 * string text. 
	 * 
	 * @param index    the index of the month
	 * @return months  the string text of the month
	 */
	private String changeIntToString(int index) {
		String months = "";
		if (index == 1) {
			months = "01";
		} else if (index == 2) {
			months = "02";
		} else if (index == 3) {
			months = "03";
		} else if (index == 4) {
			months = "04";
		} else if (index == 5) {
			months = "05";
		} else if (index == 6) {
			months = "06";
		} else if (index == 7) {
			months = "07";
		} else if (index == 8) {
			months = "08";
		} else if (index == 9) {
			months = "09";
		} else if (index == 10) {
			months = "10";
		} else if (index == 11) {
			months = "11";
		} else if (index == 12) {
			months = "12";
		}
		return months;		
	}

	/**
	 * The method used for validating the period of frequency schedule.
	 * A warning message will display on screen when the end date is 
	 * larger than start date.
	 */
	public void validateScheduleDate(CtSchedule ctSchedule, int index) {
		getManagedBean().getContractVer().getCtSchedules().remove(index);
		getManagedBean().getContractVer().getCtSchedules().add(index, ctSchedule);

		getManagedBean().setProgSchedButtonDeactive(false);
		if (ctSchedule.getEndMonth() != null && ctSchedule.getStartMonth() != null) {
			int startYear = Integer.valueOf((String) ctSchedule.getStartYear());  
			int endYear = Integer.valueOf((String) ctSchedule.getEndYear());
			int startMonth = Integer.valueOf((String) ctSchedule.getStartMonth());  
			int endMonth = Integer.valueOf((String) ctSchedule.getEndMonth());

			if ((startMonth > endMonth && startYear >= endYear)|| (startMonth <= endMonth && startYear > endYear)) {
				for (CtSchedule dataList : this.getManagedBean().getContractVer().getCtSchedules()) {			            
					if (dataList.toString().equalsIgnoreCase(ctSchedule.toString())) {
						getManagedBean().setProgSchedButtonDeactive(true);
						throw new EpException(MessageConst.ERROR_750023);
					}
				}
			} 
		}
	}

	/**
	 * The method used to add a new frequency schedule.
	 * This method will generate a new record in frequency schedule
	 * list. 
	 */
	public void addScheduleDetails() {
		CtSchedule ctSchedules = new CtSchedule();
		int number = 1;
		if (getManagedBean().getContractVer().getCtSchedules().size() > 0) {
			// Get the last schedule record from the list and plus 1 to the schedule no
			number = getManagedBean().getContractVer().getCtSchedules().get(
					getManagedBean().getContractVer().getCtSchedules().size() - 1).getScheduleNo() + 1;
		}
		ctSchedules.setScheduleNo(number);
		getManagedBean().getContractVer().getCtSchedules().add(ctSchedules);	
	}

	/**
	 * The method used to delete the selected frequency schedule(s).
	 */
	public void deleteScheduleDetails() {
		try {
			Iterator<CtSchedule> ctScheduleIterator = getManagedBean().getContractVer().getCtSchedules() .iterator();
			while(ctScheduleIterator.hasNext()){
				CtSchedule  ctSchedules = ctScheduleIterator.next();
				if(ctSchedules.isSelectedCheckBox()){
					ctScheduleIterator.remove();
				}
			}
			getManagedBean().setSelectAll(false);
			getManagedBean().setButtonHideSchActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	/**
	 * The method used to delete the selected progressive payment(s).
	 */
	public void deleteProgressiveDetails(){
		try {
			Iterator<CtProgressivePayment> ctProIterator = getManagedBean().getContractVer().getCtProgressivePayments().iterator();
			while(ctProIterator.hasNext()){
				CtProgressivePayment  ctProgressivePayments = ctProIterator.next();
				if(ctProgressivePayments.isSelectedCheckBox()){
					ctProIterator.remove();
				}
			}
			getManagedBean().setSelectAll(false);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	/**
	 * The method used for generating a list of available year
	 * as selection.
	 * 
	 * @return the selection list of year
	 */
	public List<String> getYearList() {
		getManagedBean().setYearList(new ArrayList<String>());
		int startYear = Integer.valueOf(dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()).substring(6, 10));
		int endYear = Integer.valueOf(dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()).substring(6, 10));
		for (int i = startYear; i <= endYear; i++) {
			getManagedBean().getYearList().add(String.valueOf(i));
		}
		return getManagedBean().getYearList();
	}

	/**
	 * The method used for validating the period of progressive payment.
	 * A warning message will display on screen when the end date is 
	 * larger than start date.
	 */
	public void validateProgressiveDate(CtProgressivePayment  ctProgressivePayment, int index) {
		getManagedBean().getContractVer().getCtProgressivePayments().remove(index);
		getManagedBean().getContractVer().getCtProgressivePayments().add(index, ctProgressivePayment);

		calculatePercentage(ctProgressivePayment);
		getManagedBean().setProgSchedButtonDeactive(false);
		if (ctProgressivePayment.getEndMonth() != null && ctProgressivePayment.getStartMonth() != null) {
			int startYear = Integer.valueOf((String) ctProgressivePayment.getStartYear());  
			int endYear = Integer.valueOf((String) ctProgressivePayment.getEndYear());
			int startMonth = Integer.valueOf((String) ctProgressivePayment.getStartMonth());  
			int endMonth = Integer.valueOf((String) ctProgressivePayment.getEndMonth());

			if ((startMonth > endMonth  &&  startYear >= endYear)||(startMonth <= endMonth  && startYear > endYear)) {
				for (CtProgressivePayment dataList : this.getManagedBean().getContractVer().getCtProgressivePayments()) {
					if (dataList.equals(ctProgressivePayment)) {
						getManagedBean().setProgSchedButtonDeactive(true);
						throw new EpException(MessageConst.ERROR_750024);
					}
				}
			}
		}
	}

	/**
	 * The method used for calculating the percentage of progressive payment.
	 * A warning message will display on screen when the percentage
	 * is more than 100%.
	 */
	public void calculatePercentage(CtProgressivePayment progPayment) {
		double addValue = 0.0f;
		getManagedBean().setProgSchedButtonDeactive(false);
		for (CtProgressivePayment progressivePayment: getManagedBean().getContractVer().getCtProgressivePayments()) {
			if(progressivePayment.getPercentage()!=null) {
				addValue = addValue + progressivePayment.getPercentage().doubleValue();
			}
		}
		for (CtProgressivePayment progressivePayment: getManagedBean().getContractVer().getCtProgressivePayments()) {
			if (progressivePayment.equals(progPayment)) {
				if (Math.abs(addValue) > 100) {
					getManagedBean().setProgSchedButtonDeactive(true);
					progressivePayment.setPercentageExceeded(true);
				} else if (Math.abs(addValue) == 100) {
					getManagedBean().setProgSchedButtonDeactive(true);
					progressivePayment.setPercentageExceeded(false);
				} else {
					progressivePayment.setPercentageExceeded(false);
				}
			} else {
				progressivePayment.setPercentageExceeded(false);
			}
		}  
	}

	/**
	 * The method used to retrieve the total month from selected
	 * start month to selected end month.
	 * 
	 * @return the total number of month(s)
	 */
	public int calculateTotalMonth(Object Var, String frequencyType) {
		DateFormat dateFormat = null;
		String fromDate = null;
		String toDate = null;

		try {		     
			dateFormat = new SimpleDateFormat("MM-yyyy");		  
			if (frequencyType.equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_SCHEDULE)) {
				CtSchedule schedules = (CtSchedule)Var;
				if (schedules.getEndMonth() != null) {
					fromDate = schedules.getStartMonth()+"-"+ schedules.getStartYear();
					toDate = schedules.getEndMonth() +"-"+schedules.getEndYear();
				}
			} else if (frequencyType.equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_PROGRESSIVE)) {
				CtProgressivePayment progressivePayment = (CtProgressivePayment)Var;
				if (progressivePayment.getEndMonth() != null) {
					fromDate = progressivePayment.getStartMonth() + "-" + progressivePayment.getStartYear();
					toDate =  progressivePayment.getEndMonth() + "-" + progressivePayment.getEndYear();
				}
			}
			Date startDate = dateFormat.parse((fromDate));
			Date endDate = dateFormat.parse((toDate));
			Calendar sDate = Calendar.getInstance();
			Calendar eDate = Calendar.getInstance();
			sDate.setTime(startDate);
			eDate.setTime(endDate);

			int months  = (eDate.get(Calendar.YEAR) - sDate.get(Calendar.YEAR)) * 12
					+ (eDate.get(Calendar.MONTH)- sDate.get(Calendar.MONTH));

			if (months < 0) {
				return 0;
			}
			return months + 1;
		} catch(Exception ex) {

		} finally { }
		return 0;
	}

	/**
	 * The method used to add a new progressive payment.
	 * This method will generate a new record in progressive payment
	 * list. 
	 */
	public void addFrequencyDetails() {
		CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
		ctProgressivePayment.setPhaseNo(getManagedBean().getContractVer().getCtProgressivePayments().size() + 1);
		ctProgressivePayment.setPercentage(BigDecimal.ZERO);
		getManagedBean().getContractVer().getCtProgressivePayments().add(ctProgressivePayment);
	}

	/**
	 * The method used for generating a list of fulfillment type
	 * for schedule as selection.
	 * 
	 * @return the selection list of fulfillment type
	 */
	public SelectItem[] getScheduleTypeDropDown() {
		SelectItem[] items = null;
		ISearchBean<PmParameterDesc> searchBean = null;

		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.SCHEDULE_TYPE, searchBean);
			items = new SelectItem[parameterDescs.size()];
			int i = 0;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(
						parameterDesc.getPmParameter().getParameterId(), 
						parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return items;
	}

	/**
	 * @return the selectedSchedule
	 */
	public CtSchedule[] getSelectedSchedule() {
		return selectedSchedule;
	}

	/**
	 * @param selectedSchedule the selectedSchedule to set
	 */
	public void setSelectedSchedule(CtSchedule[] selectedSchedule) {
		this.selectedSchedule = selectedSchedule;
	}

	/**
	 * @return the selectedProgressive
	 */
	public CtProgressivePayment[] getSelectedProgressive() {
		return selectedProgressive;
	}

	/**
	 * @param selectedProgressive the selectedProgressive to set
	 */
	public void setSelectedProgressive(CtProgressivePayment[] selectedProgressive) {
		this.selectedProgressive = selectedProgressive;
	}/* Implementation of contract schedule end*/

	/* Implementation of contract deduction start*/
	/**
	 * This method used to get parameter code using parameter id
	 * 
	 * @param parameterObj an object of pmparameter
	 * @return String, parameter code
	 */
	public String getParameterCode(PmParameter parameterObj){
		if(parameterObj == null || parameterObj.getParameterId() == null) {
			return "Str";
		}
		return ParameterHelper.getInstance().getPmParameterById(parameterObj.getParameterId()).getParameterCode();
	}

	/**
	 * This method used to retrieve deduction type from database 
	 * 
	 * @return the selection list of deduction type
	 */
	public SelectItem[] retrieveParameterDescListByType() {

		SelectItem[] items = null;
		int i = 1;
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		try {

			List<String> deductionType = new ArrayList<String>();
			deductionType.add(ParameterConst.DEDUCTION_TYPE_PENALTY);
			deductionType.add(ParameterConst.DEDUCTION_TYPE_ROYALTY_FEE);
			deductionType.add(ParameterConst.DEDUCTION_TYPE_FIXED);
			deductionType.add(ParameterConst.DEDUCTION_TYPE_NON_FIXED);
			deductionType.add(ParameterConst.DEDUCTION_TYPE_OTHERS);

			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode",SearchOperator.OPERATOR_IN.value(), deductionType));
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.DEDUCTION_TYPE, searchBean);
			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter().getParameterId(), parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return items;
	}

	/**
	 * The method used to validate deduction type.
	 * User can not selected same type of deduction again.
	 */	
	public void validateDeductionType(CtDeduction deduction)  {

		if(getManagedBean().getContractVer().getCtDeductions().size() > 0 ) {
			int count = 0;
			String parameterCode;
			for(CtDeduction deId : getManagedBean().getContractVer().getCtDeductions()){
				PmParameter deductionTypeById = deId.getDeductionType();
				PmParameter deductionType = deduction.getDeductionType();
				if (deductionTypeById == null || deductionType == null)
					break;
				if (deductionTypeById.getParameterId() != null && deductionType.getParameterId() != null
						&& deductionType.getParameterId().intValue() == deductionTypeById.getParameterId().intValue()) {
					count++;
					parameterCode =  getParameterCode(deductionTypeById);
					if(!parameterCode.equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_OTHERS) &&
							!parameterCode.equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_FIXED)  && 
							!parameterCode.equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_NON_FIXED) && count == 2)
						throw new EpException(MessageConst.ERROR_100046,
								new String[] {getParameterDesc(ParameterHelper.getInstance().getPmParameterById(deduction.getDeductionType().getParameterId()))});
				}
			}
		}
	}

	/**
	 * The method used to add new deduction.
	 * This method will generate new record in deduction.
	 */	
	public void addDeduction() {

		CtDeduction deduction = new CtDeduction();
		deduction.setDeductionType(new PmParameter());
		this.getManagedBean().getContractVer().getCtDeductions().add(deduction);
	}

	/**
	 * The method used to delete the selected deduction(s).
	 */
	public void deleteDeductionDetails() {
		try {
			Iterator<CtDeduction> deductionIterator = getManagedBean().getContractVer().getCtDeductions().iterator();
			while(deductionIterator.hasNext()){
				CtDeduction  deducts = deductionIterator.next();
				if(deducts.isSelectedCheckBox()){
					deductionIterator.remove();
				}
			}
			getManagedBean().setSelectedDeduction(null);
			getManagedBean().setSelectAll(false);
			getManagedBean().setButtonHideDedActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	/**
	 * The method used to add new deduction clause.
	 * This method will generate new record in deduction clause
	 * list. 
	 */	
	public void addDeductionClause() {
		CtDeductionClause clause = new CtDeductionClause();
		this.getManagedBean().getSelectedDeduction().getCtDeductionClauses().add(clause);
	}

	/**
	 * The method used to delete the selected deduction clause(s).
	 */
	public void deleteClauseDetails() {
		try {
			Iterator<CtDeductionClause> deductionClauseIterator = getManagedBean().getSelectedDeduction().getCtDeductionClauses() .iterator();
			while(deductionClauseIterator.hasNext()){
				CtDeductionClause  clause = deductionClauseIterator.next();
				if(clause.isSelectedCheckBox()){
					deductionClauseIterator.remove();
				}
			}
			getManagedBean().setSelectAll(false);
			getManagedBean().setButtonHideDedActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	/**
	 * The method used for getter
	 *  and setter for deduction clause
	 */
	public CtDeductionClause[] getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	/**
	 * The method used for getter
	 *  and setter for deduction clause
	 */
	public void setSelectedDeductionClauses(
			CtDeductionClause[] selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}

	/**
	 * Function to check / uncheck single checkbox into datatable.
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneCheckboxSelect(AjaxBehaviorEvent e){

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Function to check / uncheck single checkbox into deduction datatable.
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneDeductCheckboxSelect(AjaxBehaviorEvent e){
		getManagedBean().setSelectAll(false);
		for (CtDeduction deduct : getManagedBean().getContractVer().getCtDeductions()) {
			if(deduct.isSelectedCheckBox()){
				getManagedBean().setButtonHideDedActive(false);
				return;
			}
		}
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideDedActive(false);
		}else{
			getManagedBean().setButtonHideDedActive(true);
		}	
	}

	/**
	 * Function to check / uncheck single checkbox into deduction clause datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneClauseCheckboxSelect(AjaxBehaviorEvent e){
		getManagedBean().setSelectAll(false);
		for (CtDeductionClause ctDeductionClauses : getManagedBean().getSelectedDeduction().getCtDeductionClauses()) {
			if(ctDeductionClauses.isSelectedCheckBox()){
				getManagedBean().setButtonHideDedActive(false);
				return;
			}
		}
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideDedActive(false);
		}else{
			getManagedBean().setButtonHideDedActive(true);
		}	
	}

	/**
	 * @return the paidPercentage
	 */
	public BigDecimal getPaidPercentage() {
		if(getManagedBean().getPaidInvoiceAmt() != null && getManagedBean().getPaidInvoiceAmt().intValue() > 0) {
			paidPercentage = (getManagedBean().getPaidInvoiceAmt().multiply(
					BigDecimal.valueOf(100)).divide(
							getManagedBean().getGstTotalAmount(), 2, RoundingMode.HALF_UP));
		}
		return paidPercentage;
	}
	


	/**
	 * @param paidPercentage the paidPercentage to set
	 */
	public void setPaidPercentage(BigDecimal paidPercentage) {
		this.paidPercentage = paidPercentage;
	}
	
	/**
	 * @return the paidsumPercentage
	 */
	public BigDecimal getPaidsumPercentage() {
		if(getManagedBean().getPaidsumInvoiceAmt() != null && getManagedBean().getPaidsumInvoiceAmt().intValue() > 0) {
			paidsumPercentage = (getManagedBean().getPaidsumInvoiceAmt().multiply(
					BigDecimal.valueOf(100)).divide(
							getManagedBean().getGstTotalAmount(), 2, RoundingMode.HALF_UP));
		}
		return paidsumPercentage;
	}
	
	/**
	 * @param paidsumPercentage the paidsumPercentage to set
	 */
	public void setPaidsumPercentage(BigDecimal paidsumPercentage) {
		this.paidsumPercentage = paidsumPercentage;
	}

	/**
	 * @return the paidAdvancePercentage
	 */
	public BigDecimal getPaidAdvancePercentage() {

		if (getManagedBean().getPaidAdvancePaymentAmt() != null && 
				getManagedBean().getPaidAdvancePaymentAmt().doubleValue() != 0) {
			paidAdvancePercentage = (getManagedBean().getPaidAdvancePaymentAmt().multiply(
					BigDecimal.valueOf(100)).divide(
							BigDecimal.valueOf(getManagedBean().getAdvancePaymentAmt()), 2, RoundingMode.HALF_UP));
		}
		return paidAdvancePercentage;
	}

	/**
	 * @param paidAdvancePercentage the paidAdvancePercentage to set
	 */
	public void setPaidAdvancePercentage(BigDecimal paidAdvancePercentage) {
		this.paidAdvancePercentage = paidAdvancePercentage;
	}

	/**
	 * @return the balanceToPaid
	 */
	public BigDecimal getBalanceToPaid() {
		balanceToPaid = (BigDecimal.valueOf(this.getManagedBean().getAdvancePaymentAmt()).subtract(
				this.getManagedBean().getPaidAdvancePaymentAmt()));
		return balanceToPaid;
	}

	/**
	 * @param balanceToPaid the balanceToPaid to set
	 */
	public void setBalanceToPaid(BigDecimal balanceToPaid) {
		this.balanceToPaid = balanceToPaid;
	}    

	/*Zone start*/
	/**
	 * The method used for creating the zone tree node.
	 */
	public void retriveZonesByContractVer() {
		TreeNode rootLocationTree =  new DefaultTreeNode("Root", null);
		TreeNode zoneNode =  null;

		for (CtContractZone zone : this.getManagedBean().getContractVer().getCtContractZones()) {
			if (zone.getDeliveryTermUom() == null) {
				PmParameter pmParameter = getParameterByTypeAndCode(
						ParameterTypeConst.DELIVERY_TERM, ParameterConst.DELIVERY_TERM_DAYS);
				zone.setDeliveryTermUom(pmParameter); 
				if (pmParameter != null) {
					zone.getDeliveryTermUom().setParameterId(pmParameter.getParameterId());
				}
			}
			zoneNode = new DefaultTreeNode("zone", new LocationDTO(
					zone.toString(), zone.getZoneName(), zone.getDeliveryTerm(), 
					zone.getDeliveryTermUom().getParameterId()), rootLocationTree);
			List<PmState> state = new ArrayList<PmState>();
			if (zone.getCtContractZoneDtls() != null) {
				for (CtContractZoneDtl zoneDtl : zone.getCtContractZoneDtls()) {
					if (zoneDtl.getState() != null && getManagedBean().getContractVer().getCtContract().getLocationLevel().getParameterCode().equalsIgnoreCase(
							ParameterConst.LOCATION_LEVEL_STATE)) {
						if (!state.contains(zoneDtl.getState())) {
							new DefaultTreeNode("state",new LocationDTO(zoneDtl.getContractZoneDtlId(), zoneDtl.getState()), zoneNode);
						}
						state.add(zoneDtl.getState());
					}
					if (zoneDtl.getDistrict() != null && getManagedBean().getContractVer().getCtContract().getLocationLevel().getParameterCode().equalsIgnoreCase(
							ParameterConst.LOCATION_LEVEL_DISTRICT)) {
						new DefaultTreeNode("district", new LocationDTO(zoneDtl.getContractZoneDtlId(), zoneDtl.getDistrict()), zoneNode);
					}
				}
			}
		}
		this.getManagedBean().setCtZoneTrees(rootLocationTree);
	}

	/*Agency start here*/
	/**
	 * The method used for validating the ceiling amount of agencies.
	 * A warning message will display on screen when the total ceiling amount is 
	 * larger than contract value.
	 */
	public void validateCeilingAmount(CtAgency agencyData) {
		List<CtAgency> agencyList = this.getManagedBean().getContractVer().getCtAgencies();
		// Start added log for  Bug #31740
		log.info(" Bug #31740 agencyList : "+agencyList.size()+" and  Contract No : "+getManagedBean().getContractVer().getCtContract().getContractNo());
		log.info(" Bug #31740 Changes value using(agencyData) in ContractBackingBean  :"+agencyData.getCeilingAmount()+" and Contract NO : "+getManagedBean().getContractVer().getCtContract().getContractNo()+" agency_profile_id : "+agencyData.getOrgProfile().getOrgProfileId());
		if (agencyList.size() > 0) {
			double ceilingAmt = 0.0;
			DecimalFormat df = new DecimalFormat("#.00");
			for (CtAgency ctAgency : agencyList) {
				if(ctAgency.getOrgProfile().getOrgProfileId().equals(agencyData.getOrgProfile().getOrgProfileId())){
					log.info(" Bug #31740 Changes value using(ctAgency) in ContractBackingBean  :"+ctAgency.getCeilingAmount()+" and contract No : "+getManagedBean().getContractVer().getCtContract().getContractNo()+" agency_profile_id  :"+ctAgency.getOrgProfile().getOrgProfileId());
				}
				ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();						
		// End added log for  Bug #31740 			
				
			}
			ceilingAmt = Double.valueOf(df.format(ceilingAmt));
          
			double overallContractAmount = 0.0;
			for (CtContractAmount contractAmt : getManagedBean().getContractVer().getCtContractAmounts()){
				if (getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
						&& getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
					overallContractAmount = contractAmt.getContractAmount().doubleValue();					
					
					if (contractAmt.getGstAmount() != null || contractAmt.getStaxAmount() != null) {
						//28203				   
						double gstAmt = 0.0;
					    double StaxAmt = 0.0;
					     if(contractAmt.getGstAmount() != null){
					        gstAmt = contractAmt.getGstAmount().doubleValue();
					       }	 
					     if(contractAmt.getStaxAmount() != null){
					    	 StaxAmt = contractAmt.getStaxAmount().doubleValue();
						   }	 
					     //overallContractAmount += contractAmt.getGstAmount().doubleValue() + contractAmt.getStaxAmount().doubleValue();					    	 	 
					     overallContractAmount += gstAmt + StaxAmt;
					}
					overallContractAmount = Double.valueOf(df.format(overallContractAmount));
					break;
					
				}
			}
			if (ceilingAmt > overallContractAmount) {
				agencyData.setCeilingAmount(BigDecimal.valueOf(0.00));
				CommonUtils commonUtils = new CommonUtils();
				DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
						CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
				throw new EpException(MessageConst.ERROR_750007,
						new String[] {nf.format(overallContractAmount)}); // Total ceiling value exceeded contract value. Please check your ceiling amount.
			}
			
			
		}
		
		//log.info("filtered value in list ==================== : "+getFilteredAgencies().size());
		
	}

	/**
	 * The method used to set status of agency.
	 * The status is displayed according to the effective date.
	 * 
	 * @param  ctAgency  an object of agency
	 */
	public void checkAgencyStatus(CtAgency ctAgency) {
		// If effective date is after current date, status is still display old status
		if (ctAgency.getRecordStatusFlg() == 0) {
			if (ctAgency.getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
				ctAgency.setChkStatus(true);
			} else {
				ctAgency.setChkStatus(false);
			}
		} else {
			if (ctAgency.getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
				ctAgency.setChkStatus(false);
			} else {
				ctAgency.setChkStatus(true);
			}
		}
		
		// #25703 start
		// check status from PM_ORG_VALIDITY
		if (ctAgency.getOrgProfile().getPmOrgValidity().getRecordStatusFlg() == 0) {
			if (ctAgency.getOrgProfile().getPmOrgValidity().getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
				ctAgency.setChkStatusPmOrgValidity(true);
			} else {
				ctAgency.setChkStatusPmOrgValidity(false);
			}
		} else {
			if (ctAgency.getOrgProfile().getPmOrgValidity().getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
				ctAgency.setChkStatusPmOrgValidity(false);
			} else {
				ctAgency.setChkStatusPmOrgValidity(true);
			}
		}
		// #25703 end
	}

	/**
	 * The method used to set the upload index for
	 * file upload purpose.
	 * The index is used to differentiate what type of file has
	 * been uploaded.
	 * 
	 * @param index index of the uploaded file
	 */
	public void setIndexProperty(int index){
		getManagedBean().setIndex(index);
	}

	/**
	 * The method used to reset the search criteria for agency.
	 */
	public void resetAgencyDetails() {
		getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
		getManagedBean().setPtjCodeNameSearchValues(null);
		getManagedBean().setButtonHideActive(true);
		agencyDataModel.resetData();
	}

	/**
	 * The method used to reset the search criteria for agency.
	 */
	public void resetMinistryDetails() {
		
		System.out.println("<<<======== WE HERE ON resetMinistryDetails() ========>>>");
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
		getManagedBean().setPtjCodeNameSearchValues(null);
		getManagedBean().setButtonHideActive(true);
		agencyDataModel.resetData();
		System.out.println("<<<======== WE DONE resetMinistryDetails() ========>>>");
	}

	/**
	 * The method used to reset the search criteria for agency.
	 */
	public void resetJabatanDetails() {
		
		System.out.println("<<<======== WE HERE ON resetJabatanDetails() ========>>>");
		getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
		getManagedBean().setPtjCodeNameSearchValues(null);
		getManagedBean().setButtonHideActive(true);
		agencyDataModel.resetData();
		System.out.println("<<<======== WE DONE resetJabatanDetails() ========>>>");
	}


	/**
	 * @return the agencyDataModel
	 */
	public IPagingDataModel<PmOrgProfile> getAgencyDataModel() {
		return agencyDataModel;
	}

	/**
	 * @param agencyDataModel the agencyDataModel to set
	 */
	public void setAgencytDataModel(IPagingDataModel<PmOrgProfile> agencyDataModel) {
		this.agencyDataModel = agencyDataModel;
	}

	/**
	 * Pagination for available agency search list
	 */
	private class AgencyPagingDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public AgencyPagingDataModel() {
			super("tableAgency", false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmOrgProfile> pageChangeListener(ISearchBean<PmOrgProfile> searchBean) {			
			try {
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				
				Date currentDate = DateUtils.getCurrentDateWithoutTime();
				// Filter By - Org effDate
				searchBean.getDataFilterBeans()
						.add(new SearchFilterBean("pmOrgValidity.effDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

				// Filter By - Org expDate
				searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(new SearchFilterBean(
								"pmOrgValidity.expDate",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(),
								currentDate), new SearchFilterBean(
								"pmOrgValidity.expDate",
								SearchOperator.OPERATOR_IS_NULL.value(), null,
								SearchCondition.CONDITION_OR.value())));
				
				reqDTO.setOrgProfileSearchBean(generateAgencySearchBean(searchBean));
				OrgProfileResDTO resDTO = orgProfileSO.searchOrgProfile(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getOrgProfileResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private ISearchBean<PmOrgProfile> generateAgencySearchBean(ISearchBean<PmOrgProfile> searchBean) {
		// Filter By - parameterType
		searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));

		// Filter By - Org Type
		searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),OrganizationType.DIVISION.value()));


		// Filter By - Ministry Profile Id
		if (getManagedBean().getMinistryProfileId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getMinistryProfileId()));
		}


		if (getManagedBean().getJabatanProfileId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getJabatanProfileId()));
		}

		Date currentDate = DateUtils.getCurrentDateWithoutTime();
		// Filter By - Org effDate
		searchBean.getDataFilterBeans()
		.add(new SearchFilterBean("pmOrgValidity.effDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

		// Filter By - Org expDate
		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(new SearchFilterBean(
						"pmOrgValidity.expDate",
						SearchOperator.OPERATOR_GREATER_EQUAL.value(),
						currentDate), new SearchFilterBean(
								"pmOrgValidity.expDate",
								SearchOperator.OPERATOR_IS_NULL.value(), null,
								SearchCondition.CONDITION_OR.value())));

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("epPtj",
						SearchOperator.OPERATOR_EQUALS.value(), true));

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatus",
						SearchOperator.OPERATOR_EQUALS.value(), true));

		if (getManagedBean().getPtjCodeName() != null
				&& (getManagedBean().getPtjCodeNameSearchValues() != null && !getManagedBean().getPtjCodeNameSearchValues().equalsIgnoreCase(""))) {
			if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_CODE)) {
				SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);

			} else if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_NAME)) {
				SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
		}
		// Filter By - existing agency
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getContractVer().getCtAgencies())) {
			List<Long> orgProfileIdList = new ArrayList<Long>();
			for (CtAgency agency : getManagedBean().getContractVer().getCtAgencies()) {
				orgProfileIdList.add(agency.getOrgProfile().getOrgProfileId());
			}
			//  sql maximum number of expressions IN is 1000, if > 1000 handle in else
			//#25406 - Start Fixing 
			if (getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
				orgProfileIdList.clear();
				orgProfileIdList.add(getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				SearchFilterBean filterDefaultPtj = new SearchFilterBean("orgProfileId",
						SearchOperator.OPERATOR_NOT_IN.value(),
						orgProfileIdList);
				searchBean.getDataFilterBeans().add(filterDefaultPtj);
			}else {
				if (orgProfileIdList.size() <= 1000 && !getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {				
					SearchFilterBean filterBean = new SearchFilterBean("orgProfileId",
							SearchOperator.OPERATOR_NOT_IN.value(),
							orgProfileIdList);
					searchBean.getDataFilterBeans().add(filterBean);
			}else {
				//#25406 - End Fixing 
				List<List<Long>> subOrgProfileIdList = new ArrayList<List<Long>>();
				int count = orgProfileIdList.size();
				for (int i = 0; i < count; i += 1000) {
					subOrgProfileIdList.add(new ArrayList<Long>(
							orgProfileIdList.subList(i, Math.min(count, i + 1000)))
							);
				}
				SearchFilterBean filterBean = new SearchFilterBean("orgProfileId",
						SearchOperator.OPERATOR_NOT_IN.value(),
						subOrgProfileIdList.get(0));
				SearchMultiFilterBean multi = null;

				for (int i = 1; i < subOrgProfileIdList.size(); i++) {
					SearchFilterBean subOrgProfileBean = new SearchFilterBean("orgProfileId",
							SearchOperator.OPERATOR_NOT_IN.value(),
							subOrgProfileIdList.get(i));
					if (i == 1) {
						multi = new SearchMultiFilterBean(filterBean, subOrgProfileBean);
					} else {
						multi = new SearchMultiFilterBean(multi, subOrgProfileBean);
					}
				}
				searchBean.getDataFilterBeans().add(multi);
			}
			} //#25406
		}
		return searchBean;
	}

	/**
	 * The method used to add new agency's delivery address.
	 * This method will generate new record in delivery address
	 * list. 
	 */	
	public void addDeliveryAddress(List<VCtAgencyDeliveryAddress> addressAddressList) {

		try {
			if (addressAddressList != null) {
				List<CtAgencyAddress> ctAgencyAddress = new ArrayList<CtAgencyAddress>();
				List<CtAgencyAddress> agencyAddressList = new ArrayList<CtAgencyAddress>();				
				ctAgencyAddress = getManagedBean().getSelectedAgency().getCtAgencyAddresses();

				for (VCtAgencyDeliveryAddress deliveryAddress : addressAddressList) {
					PmAddressType addressType = deliveryAddress.getPmAddressType();
					boolean isAddressExist = false;
					// if already exist, skip to avoid duplicate
					for (CtAgencyAddress address : ctAgencyAddress) {
						if( address.getAddressType().getAddressTypeId().equals(addressType.getAddressTypeId())) {
							isAddressExist = true;
							break;
						}
					}				
					if (isAddressExist) {
						continue;
					}

					CtAgencyAddress agencyAddress = new CtAgencyAddress();
					agencyAddress.setAddressType(addressType);
					agencyAddress.setCtAgency(getManagedBean().getSelectedAgency());

					AddressReqDTO addReqDTO = new AddressReqDTO();
					addReqDTO.setAddressTypeId(addressType.getAddressTypeId());
					addReqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
					IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
					AddressResDTO addResDTO = addressSO.retrieveAddressTypeList(addReqDTO);
					if (!addResDTO.getAddressTypeList().isEmpty()) {
						agencyAddress.setDeliveryAddress(CtAddressUtil.convertPmAddressTypeToCtAddress(addResDTO.getAddressTypeList().get(0)));
					}
					agencyAddressList.add(agencyAddress);
				}
				ctAgencyAddress.addAll(agencyAddressList);
				//CT_CHANGE_LOG
				if((!getManagedBean().getSelectedAgency().isBrandNew() || getManagedBean().getSelectedAgency().isCloned()) && 
						!getManagedBean().getUpdateAgencyMap().containsKey(getManagedBean().getSelectedAgency().getOrgProfile().getOrgCode()) && 
						!getManagedBean().getNewAgencyMap().containsKey(getManagedBean().getSelectedAgency().getOrgProfile().getOrgCode())) {
					getManagedBean().getUpdateAgencyMap().put(getManagedBean().getSelectedAgency().getOrgProfile().getOrgCode(), getManagedBean().getSelectedAgency());
				}
			}
			getManagedBean().setButtonHideActive(true);

			RequestContext.getCurrentInstance().execute(
					"deliveryAddressTblWidget.paginator.endLink.click()");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}

	/**
	 * The method used to retrieve the condition for construct the LOV listing
	 * 
	 * @return the condition
	 */
	public String getDeliveryAddressCondition() {
		String condition = "ORG_PROFILE_ID^" + this.getManagedBean().getSelectedAgency().getOrgProfile().getOrgProfileId();

		PmParameter locationLevel = getManagedBean().getContractVer().getCtContract().getLocationLevel();
		if (locationLevel != null) {
			IContractZoneSO contractZoneSO = (IContractZoneSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractZoneSO.class);
			ContractZoneReqDTO reqDTO = new ContractZoneReqDTO();
			reqDTO.setCtContract(getManagedBean().getContractVer().getCtContract());

			ContractZoneResDTO resDTO = contractZoneSO.getContractZoneDtls(reqDTO);
			String stateList = "";
			String districtList = "";

			for (CtContractZoneDtl zoneDtl : resDTO.getZoneDtlList()) {
				if (locationLevel.getParameterCode().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)
						&& zoneDtl.getState() != null) {
					stateList += zoneDtl.getState().getStateId() + ",";
				}
				if (locationLevel.getParameterCode().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)
						&& zoneDtl.getDistrict() != null) {
					districtList += zoneDtl.getDistrict().getDistrictId() + ",";
				}
			}
			if (!stateList.isEmpty()) {
				condition += "|STATE_ID^" + stateList + "^" + SearchOperator.OPERATOR_IN.value();
			}
			if (!districtList.isEmpty()) {
				condition += "|DISTRICT_ID^" + districtList + "^" + SearchOperator.OPERATOR_IN.value();
			}
		}
		return condition;
	}

	/**
	 * The method used to delete the selected agency's delivery address.
	 */
	public void deleteDeliveryAddress() {		
		try {
			getManagedBean().getSelectedAgency().getCtAgencyAddresses().removeAll(getManagedBean().getSelectedListOfAddress());
			//CT_CHANGE_LOG
			if((!getManagedBean().getSelectedAgency().isBrandNew() || getManagedBean().getSelectedAgency().isCloned()) && 
					!getManagedBean().getUpdateAgencyMap().containsKey(getManagedBean().getSelectedAgency().getOrgProfile().getOrgCode())) {
				getManagedBean().getUpdateAgencyMap().put(getManagedBean().getSelectedAgency().getOrgProfile().getOrgCode(), getManagedBean().getSelectedAgency());
			}
			getManagedBean().setButtonHideActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}

	/**
	 * The method used to delete the selected agency(s).
	 */
	public void deleteAgencyDetails() {
		try {
			if (this.getManagedBean().getContractVer().getCtAgencies() != null) {
				Iterator<CtAgency> agencyIterator = this.getManagedBean().getContractVer().getCtAgencies().iterator();
				while (agencyIterator.hasNext()) {
					CtAgency agency = agencyIterator.next();
					if (agency.isSelectedCheckBox()) {
						agencyIterator.remove();
						if (getManagedBean().getCtItemList() != null && getManagedBean().getCtItemList().size() > 0) {
							for (CtContractItem contractItem: this.getManagedBean().getCtItemList()) {
								Iterator<CtItemCeilingQty> ctItemCeilingQtyIterator =  contractItem.getCtItemCeilingQties().iterator();
								while (ctItemCeilingQtyIterator.hasNext()) {
									CtItemCeilingQty ctItemCeilingQty = ctItemCeilingQtyIterator.next();
									if (ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId().equals(
											agency.getOrgProfile().getOrgProfileId())) {
										ctItemCeilingQtyIterator.remove();
									}
								}
							}	
						}
						//CT_CHANGE_LOG
						if((agency.isBrandNew() && !agency.isCloned()) || 
								getManagedBean().getNewAgencyMap().containsKey(agency.getOrgProfile().getOrgCode())) {
							getManagedBean().getNewAgencyMap().remove(agency.getOrgProfile().getOrgCode());
						} else {
							if(getManagedBean().getUpdateAgencyMap().containsKey(agency.getOrgProfile().getOrgCode())) {
								getManagedBean().getUpdateAgencyMap().remove(agency.getOrgProfile().getOrgCode());
							}
							getManagedBean().getDeleteAgencyMap().put(agency.getOrgProfile().getOrgCode(), agency);
						}
					}
				}
				//Reset ceiling amount and ceiling quantity to Zero if only single agency
				if (this.getManagedBean().getContractVer().getCtAgencies().size() == 1) {
					this.getManagedBean().getContractVer().getCtAgencies().get(0).setCeilingAmount(BigDecimal.ZERO);
					for (CtContractItem contractItem: this.getManagedBean().getCtItemList()) {
						if (contractItem.getCtItemCeilingQties() != null && contractItem.getCtItemCeilingQties().size() > 0) {//25703
							contractItem.getCtItemCeilingQties().get(0).setCeilingQuantity(BigDecimal.ZERO);
						}
					}
				}
				//Reset show item
				if (this.getManagedBean().getContractVer().getCtAgencies().size() <= 1) {
					if (getManagedBean().getShowItem() != null
							&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
						getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
					}
				}

			}
			
			if(this.getManagedBean().getContractVer().getCtAgencies() == null  || this.getManagedBean().getContractVer().getCtAgencies().isEmpty() ){
				getManagedBean().setDisableAgent(true);
			}
			
			this.getManagedBean().setSelectedAgency(null);
			getManagedBean().setButtonHideActive(true);
			
			Long minstryId = retrieveMinistryOrgProfile(
					getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId();
			getManagedBean().setOwnerOrgProfile(true);
			
			for(CtAgency agencies : getManagedBean().getContractVer().getCtAgencies()){
				if(!minstryId.equals(agencies.getOrgProfile().getMinstryId())){
					getManagedBean().setOwnerOrgProfile(false);
					getManagedBean().setDisableAgent(false);
					break;
				}
			}

			if(getManagedBean().isSelectAll()){
				getManagedBean().setSelectAll(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	/**
	 * The method used for searching the agency.
	 */
	public void searchAgencyDetails() {
		try {
			agencyDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
		}
	}

	/**
	 * The method used to add a new agency record.
	 * This method will generate a new record in agency
	 * list, user allow to enter the agency details. 
	 * 
	 * @param selectedOnly   the boolean to indicate only selected agency to be added
	 */
	public void addAgencyDetails(boolean selectedOnly) {
		List<CtAgency> ctAgencyList = null;
		OrgProfileReqDTO reqDTO = null;
		List<PmOrgProfile> orgProfileList = null;
		getManagedBean().setOwnerOrgProfile(true);
		Long ministryId = retrieveMinistryOrgProfile(
				getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId();
		
		if (getManagedBean().getAgencySelected() != null && getManagedBean().getAgencySelected().size() >0 && selectedOnly) {
			ctAgencyList = new ArrayList<CtAgency>();
			for(PmOrgProfile pmOrgProfiles : getManagedBean().getAgencySelected()) {		
				CtAgency ctAgency = new CtAgency();
				if (getManagedBean().getContractVer().getCtContractValue().getEffDate().before(DateUtils.getCurrentDateWithoutTime())) {
					ctAgency.setEffDate(DateUtils.getCurrentDateWithoutTime());
				} else {
					ctAgency.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
				}
				ctAgency.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
				ctAgency.setCeilingAmount(new BigDecimal(0));
				ctAgency.setChkStatus(true);
				//#25703 start
				ctAgency.setChkStatusPmOrgValidity(true);
				//#25703 end
				pmOrgProfiles.setMinstryId(getManagedBean().getMinistryProfileId());
				ctAgency.setOrgProfile(pmOrgProfiles);
				ctAgency.setMinistryName(retrieveMinistryByOrgProfileId(pmOrgProfiles.getOrgProfileId()));
				ctAgency.setKumpulanPtjName(retrieveJabatanByOrgProfileId(pmOrgProfiles.getOrgProfileId()));

				StringBuilder str =  new StringBuilder();
				int len = (getManagedBean().getContractVer().getCtContract().getContractId()+pmOrgProfiles.getOrgProfileId().toString()).length();
				for(int i=len; i<16; i++){
					str = str.append("0");
				}
				str.append(getManagedBean().getContractVer().getCtContract().getContractId());
				str.append(ctAgency.getOrgProfile().getOrgProfileId());
				ctAgency.setCommitmentNo("K"+str.toString());
				if (!pmOrgProfiles.getMinstryId().equals(ministryId)) {
					ctAgency.setAp178(true);
					getManagedBean().setDisableAgent(false);
	
				}		  	
				ctAgency.setCtContractVer(this.getManagedBean().getContractVer());
				ctAgencyList.add(ctAgency);

				if (ctAgency != null && this.getManagedBean().getCtItemList() != null &&  this.getManagedBean().getCtItemList().size() > 0) {
					for (CtContractItem contractItem: this.getManagedBean().getCtItemList()) {
						CtItemCeilingQty itemCeilingQty = new CtItemCeilingQty();
						itemCeilingQty.setCtAgency(ctAgency);
						itemCeilingQty.setCtContractItem(contractItem);
						itemCeilingQty.setCeilingQuantity(new BigDecimal(0));
						itemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
						itemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
						contractItem.getCtItemCeilingQties().add(itemCeilingQty);
					}
				}
			}
		} else {    	
			boolean contractSharing = true;
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			try {
				ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();
				ctAgencyList = new ArrayList<CtAgency>();
				reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgProfileSearchBean(generateAgencySearchBean(searchBean));
				orgProfileList =  orgProfileSO.searchOrgProfile(reqDTO).getOrgProfileResultBean().getResultList();				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}

			for(PmOrgProfile orgProfile : orgProfileList) {
				CtAgency ctAgency = new CtAgency();
				orgProfile.setMinstryId(getManagedBean().getMinistryProfileId());
				ctAgency.setOrgProfile(orgProfile);
				ctAgency.setMinistryName(retrieveMinistryByOrgProfileId(orgProfile.getOrgProfileId()));
				ctAgency.setKumpulanPtjName(retrieveJabatanByOrgProfileId(orgProfile.getOrgProfileId()));

				StringBuilder str =  new StringBuilder();
				int len = (getManagedBean().getContractVer().getCtContract().getContractId()+ctAgency.getOrgProfile().getOrgProfileId().toString()).length();
				for(int i=len; i<16; i++){
					str = str.append("0");
				}
				str.append(getManagedBean().getContractVer().getCtContract().getContractId());
				str.append(ctAgency.getOrgProfile().getOrgProfileId());
				ctAgency.setCommitmentNo("K"+str.toString());
				if(!ctAgency.getOrgProfile().getMinstryId().equals(ministryId)){
					ctAgency.setAp178(true);
					getManagedBean().setDisableAgent(false);
				}
				if(contractSharing){
					ctAgency.setContractSharing(contractSharing);
					contractSharing = false;
				}
				if (getManagedBean().getContractVer().getCtContractValue().getEffDate().before(DateUtils.getCurrentDateWithoutTime())) {
					ctAgency.setEffDate(DateUtils.getCurrentDateWithoutTime());
				} else {
					ctAgency.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
				}
				ctAgency.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
				ctAgency.setCeilingAmount(new BigDecimal(0));
				ctAgency.setChkStatus(true);  	
				//#25703 start
				ctAgency.setChkStatusPmOrgValidity(true);
				//#25703 end
				ctAgency.setCtContractVer(this.getManagedBean().getContractVer());
				ctAgencyList.add(ctAgency);

				if (ctAgency != null && this.getManagedBean().getCtItemList() != null &&  this.getManagedBean().getCtItemList().size() > 0) {
					for (CtContractItem contractItem: this.getManagedBean().getCtItemList()) {
						CtItemCeilingQty itemCeilingQty = new CtItemCeilingQty();
						itemCeilingQty.setCtAgency(ctAgency);
						itemCeilingQty.setCtContractItem(contractItem);
						itemCeilingQty.setCeilingQuantity(new BigDecimal(0));
						itemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
						itemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
						contractItem.getCtItemCeilingQties().add(itemCeilingQty);
					}
				}
			}	
		}
		if (ctAgencyList.size()>0) {
			this.getManagedBean().getContractVer().getCtAgencies().addAll(ctAgencyList);
			//CT_CHANGE_LOG
			for(CtAgency ctAgency : ctAgencyList) {
				getManagedBean().getNewAgencyMap().put(ctAgency.getOrgProfile().getOrgCode(), ctAgency);
			}
		}

		for (CtAgency ctAgencies : getManagedBean().getContractVer().getCtAgencies()){
			if(!ctAgencies.getOrgProfile().getMinstryId().equals(ministryId)){
				getManagedBean().setOwnerOrgProfile(false);
				break;
			}
		}
	}

	/**
	 * The method used to check whether is contract sharing for agencies.
	 * 
	 * @return true, if added agency's ministry different with contract's ministry
	 *         false, if added agency's ministry same with contract's ministry
	 */
	public boolean isContractSharing() {
		PmOrgProfile contractMinistry = retrieveMinistryOrgProfile(
				getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
		PmOrgProfile agencyMinistry = null;
		for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
			agencyMinistry = retrieveMinistryOrgProfile(ctAgency.getOrgProfile().getOrgProfileId());
			if (agencyMinistry==null || !contractMinistry.getOrgProfileId().equals(agencyMinistry.getOrgProfileId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The method used to upload agency file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void ComonFileUpload(FileUploadEvent event) throws Exception {
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		FacesScopes facesScopes = new FacesScopes();
		UploadedFile item = event.getFile();		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();

		if(event.getComponent().getId().equalsIgnoreCase("agencyUploadId")){
			CtAttachment attachment = new CtAttachment();
			attachment.setFileName(item.getFileName());
			attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
			attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			attachment.setCreatedUser(facesScopes.getUserSessionBean());
			attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			attachment.setSectionId(ContractConst.ATTACHMENT_AGENCY_CONTRACT_SHARING);
			//#25703 start
			getManagedBean().getContractVer().getAgencyContractSharingAttachements().add(attachment);
			//RequestContext.getCurrentInstance().execute("ctSharingAttachDlg.hide()");
			//#25703 end
		}
		else if(event.getComponent().getId().equalsIgnoreCase("uploadWaiver")){
			//LOG : 13847, by Fahmi 22032019
			CtAttachment attachment = new CtAttachment();
			if(getManagedBean().getCtAttachmentLatest() != null){
				attachment = getManagedBean().getCtAttachmentLatest();
			}
			attachment.setFileName(item.getFileName());
			attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
			attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			attachment.setCreatedUser(facesScopes.getUserSessionBean());
			attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			//LOG : 13847, by Fahmi 22032019
			getManagedBean().setCtAttachmentLatest(attachment);
			if(getManagedBean().getWaiverDetail().size() == 0){
				if(getManagedBean().getCtAttachmentTemp() == null){
					getManagedBean().setCtAttachmentTemp(new CtAttachment());
					if(getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail() == null){
						getManagedBean().getCtAttachmentTemp().setCtBondWaiverDetail(new CtBondWaiverDetail());
					}
				}
				if(getManagedBean().getCtAttachmentTemp().getFileName() == null || "".equalsIgnoreCase(getManagedBean().getCtAttachmentTemp().getFileName())){
					getManagedBean().getCtAttachmentTemp().setFileName(item.getFileName());
					getManagedBean().getCtAttachmentTemp().setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
					getManagedBean().getCtAttachmentTemp().setCreatedBy(facesScopes.getUserSessionBean().getUserId());
					getManagedBean().getCtAttachmentTemp().setCreatedUser(facesScopes.getUserSessionBean());
					getManagedBean().getCtAttachmentTemp().setCreatedDate(new Timestamp(System.currentTimeMillis()));
				}
				
			}
			//getManagedBean().getContractVer().getBondWaiverAttachments().add(attachment);
			RequestContext.getCurrentInstance().execute("uploadWaiverDlg.hide()");
		}
		//#13847 - start
		else if(event.getComponent().getId().equalsIgnoreCase("uploadHistoryWaiver")){
			CtAttachment ctAttachmentHistory = getManagedBean().getWaiverDetail().get(getManagedBean().getAttachFileIndex());
			
			ctAttachmentHistory.setModified(true);
			ctAttachmentHistory.setFileName(item.getFileName());
			ctAttachmentHistory.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
			ctAttachmentHistory.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			ctAttachmentHistory.setCreatedUser(facesScopes.getUserSessionBean());
			ctAttachmentHistory.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			
			RequestContext.getCurrentInstance().execute("uploadHistoryWaiverDlg.hide()");
		}
		//#13847 - end
	}
	//LOG : 13847, by Fahmi 22032019
		/**
		 * The method used to set temp first data
		 * 
		 * @param  
		 * @throws 
		 * @see    
		 */
		public void setToTempFirstTime(){
			String task =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId1");
			String task2 =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId2");
			String task3 =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId3");
			if(getManagedBean().getWaiverDetail().size() == 0){
				if(getManagedBean().getCtAttachmentTemp() == null){
					getManagedBean().setCtAttachmentTemp(new CtAttachment());
					if(getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail() == null){
						getManagedBean().getCtAttachmentTemp().setCtBondWaiverDetail(new CtBondWaiverDetail());
					}
				}
				if("startDate".equalsIgnoreCase(task) && getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail().getWaiverStartDate() == null){
					getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail().setWaiverStartDate(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverStartDate());
				}else if("endDate".equalsIgnoreCase(task2) && getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail().getWaiverEndDate() == null){
					getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail().setWaiverEndDate(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverEndDate());
				}else if("referenceNo".equalsIgnoreCase(task3) && (getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail().getReferenceNo() == null || "".equalsIgnoreCase(getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail().getReferenceNo()))){
					getManagedBean().getCtAttachmentTemp().getCtBondWaiverDetail().setReferenceNo(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getReferenceNo());
				}
			}
			
			
		}
	//LOG : 13847, by Fahmi 22032019
	/**
	 * The method used to set waiver detail
	 * 
	 * @param  
	 * @throws 
	 * @see    
	 */
		public void setBondWaiverToTable() throws Exception {
			//#Enhancement 13847 - start
			Date bondStartDate = null, bondEndDate = null, entryBondStartDate = null, entryBondEndDate = null;
			boolean sameStartLessEnd = false, overStartSameEnd = false, lessStartLessEndMoreStart = false, lessStartMoreEnd = false, 
					moreStartLessEndMoreEnd = false, lessStartSameEnd = false, sameStartMoreEnd = false, moreStartLessEnd = false;
			//#Enhancement 13847 - end
					
			if(getManagedBean().getCtContractAmount().isBondWaived()){
				if(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverStartDate() == null &&
						getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverEndDate() == null &&
						(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getReferenceNo() == null || "".equalsIgnoreCase(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getReferenceNo())) &&
						(getManagedBean().getCtAttachmentLatest().getFileName() == null || "".equalsIgnoreCase(getManagedBean().getCtAttachmentLatest().getFileName()))){
					throw new EpException(MessageConst.ERROR_750061);//#Enhancement 13847 - shaqimi
				}
				if (getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverStartDate() == null) {
					throw new EpException(MessageConst.ERROR_750062);//#Enhancement 13847 - shaqimi
				}
				if (getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverEndDate() == null) {
					throw new EpException(MessageConst.ERROR_750063);//#Enhancement 13847 - shaqimi
				}
				if (getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getReferenceNo() == null || "".equalsIgnoreCase(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getReferenceNo())) {
					throw new EpException(MessageConst.ERROR_750064);//#Enhancement 13847 - shaqimi
				}
				if (getManagedBean().getCtAttachmentLatest().getFileName() == null || "".equalsIgnoreCase(getManagedBean().getCtAttachmentLatest().getFileName())) {
					/*throw new EpException(MessageConst.ERROR_100022,
							new String[] {ResourceBundleUtils.getMessage("lbl.ct.bond.waiver.deferment", 
									2, new FacesScopes().getUserLocale())});*/
					throw new EpException(MessageConst.ERROR_750065);//#Enhancement 13847 - shaqimi
				}
				
				/*
				 * the value 0 if the argument Date is equal to this Date;
				 * a value less than 0 if this Date is before the Date argument; 
				 * a value greater than 0 if this Date is after the Date argument
				 *   
				*/
				if (getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverStartDate()
						.compareTo(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverEndDate()) > 0) {
					// start date large than end date
					throw new EpException(MessageConst.ERROR_760164);
				}
			}else{//checking this when user untick/tick bond, if one field null, will return, if not copy data to history. only save data will show in history table
				if(getManagedBean().getCtAttachmentLatest().getAttachmentId() == null){
					return;
				}
				if (getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverStartDate() == null) {
					return;
				}
				if (getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverEndDate() == null) {
					return;
				}
				if (getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getReferenceNo() == null || "".equalsIgnoreCase(getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getReferenceNo())) {
					return;
				}
				if (getManagedBean().getCtAttachmentLatest().getFileName() == null || "".equalsIgnoreCase(getManagedBean().getCtAttachmentLatest().getFileName())) {
					return;
				}
				if(getManagedBean().getCtAttachmentLatest().getAttachmentId() == null || getManagedBean().getCtAttachmentLatest().getAttachmentId() <= 0){
					return;
				}
			}
			
			checkPerformanceBondExceedDate();
			
			//Enhancement 13847 - start
			for(CtAttachment ctAttachmentHistory : getManagedBean().getWaiverDetail()){
				bondStartDate = getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverStartDate();
				bondEndDate = getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverEndDate();
				entryBondStartDate = ctAttachmentHistory.getCtBondWaiverDetail().getWaiverStartDate();
				entryBondEndDate = ctAttachmentHistory.getCtBondWaiverDetail().getWaiverEndDate();
				
				//for easier reading
				sameStartLessEnd = bondStartDate.compareTo(entryBondStartDate) == 0 && bondEndDate.compareTo(entryBondEndDate) < 0;
				overStartSameEnd = (bondStartDate.compareTo(entryBondStartDate) > 0 && bondStartDate.compareTo(entryBondEndDate) < 0) &&
						bondEndDate.compareTo(entryBondEndDate) == 0;
				lessStartLessEndMoreStart = bondStartDate.compareTo(entryBondStartDate) < 0 &&
						(bondEndDate.compareTo(entryBondEndDate) < 0 && bondEndDate.compareTo(entryBondStartDate) > 0);
				lessStartMoreEnd = bondStartDate.compareTo(entryBondStartDate) < 0 && bondEndDate.compareTo(entryBondEndDate) > 0;
				moreStartLessEndMoreEnd = (bondStartDate.compareTo(entryBondStartDate) > 0 && bondStartDate.compareTo(entryBondEndDate) < 0) && 
						bondEndDate.compareTo(entryBondEndDate) > 0;
				lessStartSameEnd = bondStartDate.compareTo(entryBondStartDate) < 0 && bondEndDate.compareTo(entryBondEndDate) == 0;
				sameStartMoreEnd = bondStartDate.compareTo(entryBondStartDate) == 0 && bondEndDate.compareTo(entryBondEndDate) > 0;
				moreStartLessEnd = bondStartDate.compareTo(entryBondStartDate) > 0 && bondEndDate.compareTo(entryBondEndDate) < 0;
				
				if(entryBondStartDate.compareTo(bondStartDate) == 0 && entryBondEndDate.compareTo(bondEndDate) == 0){
					//date same as history entry
	 				throw new EpException(MessageConst.ERROR_750066);
				} else if (sameStartLessEnd || overStartSameEnd || lessStartLessEndMoreStart || lessStartMoreEnd || moreStartLessEndMoreEnd ||
						lessStartSameEnd || sameStartMoreEnd || moreStartLessEnd){
					//date range overlaps with history entry
	 				throw new EpException(MessageConst.ERROR_750067);
				}
			}
			
			RequestContext.getCurrentInstance().execute("addPBConfirmDlg.show()");
			//Enhancement 13847 - end
		}
		
		//Enhancement 13847 - start
		public void saveBondWaiverToTable() throws EpException {
			FacesScopes facesScopes = new FacesScopes();
			long noOfDays = 0;
			
			//add to tracking list first
			//reverse list to insert new data at first row
			Collections.reverse(getManagedBean().getWaiverDetail());
			//getManagedBean().getWaiverDetail().add(SerializationUtils.clone(getManagedBean().getCtAttachment()));
			
			if(getManagedBean().getCtAttachmentLatest().getAttachmentId() == null){
				getManagedBean().getCtAttachmentLatest().setBrandNew(true);
				getManagedBean().getCtAttachmentLatest().setTempAttachmentId(null);

				getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().setBondWaiverDetaiId(null);
				getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().setBrandNew(true);
				getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().setCtAttachment(new CtAttachment());
			}
			
			getManagedBean().getCtAttachmentLatest().setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			getManagedBean().getCtAttachmentLatest().setCreatedUser(facesScopes.getUserSessionBean());
			getManagedBean().getCtAttachmentLatest().setCreatedDate(new Timestamp(System.currentTimeMillis()));
			
			getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().setCreatedDate(new Timestamp(System.currentTimeMillis()));
			
			getManagedBean().getWaiverDetail().add(SerializationUtils.clone(getManagedBean().getCtAttachmentLatest()));
			
			//gets the last list for editing
			CtAttachment ctAttachmentNewHistory =  getManagedBean().getWaiverDetail().get(getManagedBean().getWaiverDetail().size() - 1);
			ctAttachmentNewHistory.setAttachmentId(null);
			ctAttachmentNewHistory.setBrandNew(true);
			ctAttachmentNewHistory.setTempAttachmentId(null);
			ctAttachmentNewHistory.setRecordStatus(false);
			ctAttachmentNewHistory.setRecordStatusFlg(DbValueConst.RECORD_STATUS_IN_PROGRESS);
			ctAttachmentNewHistory.getCtBondWaiverDetail().setBondWaiverDetaiId(null);
			ctAttachmentNewHistory.getCtBondWaiverDetail().setBrandNew(true);
			ctAttachmentNewHistory.getCtBondWaiverDetail().setCtAttachment(new CtAttachment());
			ctAttachmentNewHistory.getCtBondWaiverDetail().setRecordStatus(false);
			ctAttachmentNewHistory.getCtBondWaiverDetail().setRecordStatusFlg(DbValueConst.RECORD_STATUS_IN_PROGRESS);
			
			Collections.reverse(getManagedBean().getWaiverDetail());
			
			
			for(CtAttachment ctAttachmentHistory : getManagedBean().getWaiverDetail()){
				noOfDays += findDifference(ctAttachmentHistory.getCtBondWaiverDetail().getWaiverStartDate(), ctAttachmentHistory.getCtBondWaiverDetail().getWaiverEndDate());
			}
			
			if(noOfDays >= 60){
				getManagedBean().setDisableDeferment(true);
			} else {
				getManagedBean().setDisableDeferment(false);
			}
		}
		
		public long findDifference(Date start_date, Date end_date){
			// Calculate time difference 
			// in milliseconds 
			long difference_In_Time = end_date.getTime() - start_date.getTime(); 
			
			// Calculate time difference in seconds, 
			// minutes, hours, years, and days 
			long difference_In_Seconds = TimeUnit.MILLISECONDS.toSeconds(difference_In_Time) % 60;
			long difference_In_Minutes = TimeUnit.MILLISECONDS.toMinutes(difference_In_Time) % 60;
			long difference_In_Hours = TimeUnit.MILLISECONDS.toHours(difference_In_Time) % 24;
			long difference_In_Days = TimeUnit.MILLISECONDS.toDays(difference_In_Time) % 365;
			long difference_In_Years = TimeUnit.MILLISECONDS.toDays(difference_In_Time) / 365l;
			
			// Print the date difference in 
			// years, in days, in hours, in 
			// minutes, and in seconds 
			System.out.print("Difference" + " between two dates is: "); 
			
			// Print result 
			System.out.println(difference_In_Years + " years, " + difference_In_Days + " days, " + difference_In_Hours 
					+ " hours, " + difference_In_Minutes + " minutes, " + difference_In_Seconds + " seconds");
			
			return difference_In_Days;
		}
		
		public void checkPerformanceBondExceedDate() throws EpException {
			Date bondStartDate = getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverStartDate(), 
					bondEndDate = getManagedBean().getCtAttachmentLatest().getCtBondWaiverDetail().getWaiverEndDate();
			
			long diff = TimeUnit.DAYS.convert((bondEndDate.getTime() - bondStartDate.getTime()), TimeUnit.MILLISECONDS);
			
			if(diff > 60){
				throw new EpException(MessageConst.ERROR_750070);
			}
		}
		//Enhancement 13847 - end

	/**
	 * The method used to delete the uploaded agency file.
	 */    
	// #25703 start
	public void delCtMinstrySharringAttach(int index) {

		/*
		 * CtAttachment attachment =
		 * getManagedBean().getContractVer().getAgencyContractSharingAttachements
		 * (); if(attachment.getAttachmentId() != null){
		 * getManagedBean().getDeleteAttachmentList().add(attachment); }
		 * getManagedBean
		 * ().getContractVer().getAgencyContractSharingAttachements
		 * ().setFileName(null);
		 * getManagedBean().getContractVer().getAgencyContractSharingAttachements
		 * ().setFilePath(null);
		 */

		CtAttachment attachment = getManagedBean().getContractVer().getAgencyContractSharingAttachements().get(index);
		if (attachment.getAttachmentId() != null) {
			getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		getManagedBean().getContractVer().getAgencyContractSharingAttachements().remove(index);

		Long minstryId = retrieveMinistryOrgProfile(
				getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId())
				.getOrgProfileId();
		for (CtAgency agencies : getManagedBean().getContractVer().getCtAgencies()) {
			if (!minstryId.equals(agencies.getOrgProfile().getMinstryId())) {
				getManagedBean().setOwnerOrgProfile(false);
				break;
			}
		}
	}

	// #25703 end

	/**
	 * The method used for generating a list of available ministry
	 * for searching agency as selection.
	 * 
	 * @return the selection list of ministry
	 */
	public SelectItem[] getMinistryDropdown() {		
		SelectItem[] items = null;

		try {
			int i = 0;
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByMinistry(reqDTO);

			List<PmOrgProfile> list = resDTO.getOrgProfileList();
			items = new SelectItem[list.size()];
			for (PmOrgProfile orgProfile : list) {
				items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
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

			for(Object[] object : list){
				items[i++] = new SelectItem(Long.parseLong(object[0].toString()), object[1].toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}

	/**
	 * The method used to get the modifiability of contract physical no.
	 * 
	 * @return true, if contract physical no field is disabled
	 *         false, if contract physical no field is enabled
	 */
	public boolean getConditionalValue() {
		//If agreement is = NO, Physical contract no. = No and please disable the text box. And
		//If agreement is = NO, Physical contract no. = No, Consession = Yes, enable text box for Physical contract no.
		if (getManagedBean().isViewContract()) {
			return true;
		} else if(getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT)){
			return true;
		} else if(getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_NOTIFICATION_SETTING)){
			return true;
		}
		else if (!getManagedBean().getCtContractAmount().isAgreement() 
				&& getManagedBean().getContractVer().getContractPhysicalNo() == null) {
			if (getManagedBean().getContractVer().getCtContractValue().isConcession()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * The method used to 
	 * calculate over all amount with GST AND STAX
	 */
	public void calculateOrverAllAmt(){
		if(getManagedBean().getCtContractAmount()!= null) {
			BigDecimal gstAmt = BigDecimal.ZERO;
			BigDecimal staxAmt = BigDecimal.ZERO;
			BigDecimal contractAmt = BigDecimal.ZERO;

			if (getManagedBean().getCtContractAmount().getGstAmount() != null)
				gstAmt = getManagedBean().getCtContractAmount().getGstAmount();
			
			if (getManagedBean().getCtContractAmount().getStaxAmount() != null)
				staxAmt = getManagedBean().getCtContractAmount().getStaxAmount();

			if (getManagedBean().getCtContractAmount().getContractAmount() != null)
				contractAmt = getManagedBean().getCtContractAmount().getContractAmount();

			getManagedBean().setGstTotalAmount(staxAmt.add(gstAmt.add(contractAmt)));
			
			

		}
		// Recalculate the performance bond amount
		//#24333
//		if (!getManagedBean().isDirectPurchase() && !getManagedBean().getCtContractAmount().isBondExempted()) {
//			calculateNewBondAmount();
//		}		    	
	}

	/**
	 * The method used to calculate the new performance bond amount.
	 */
	private void calculateNewBondAmount() {
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);

		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setOverallContractAmt(getManagedBean().getGstTotalAmount());
		reqDTO.setContractPeriod(new BigDecimal(getManagedBean().getContractVer().getCtContractValue().getContractPeriod()));
		reqDTO.setScLoa(getManagedBean().getContractVer().getCtContract().getLoa());

		ContractResDTO resDTO = contractSO.retrievePerformanceBondAmt(reqDTO);
		//#24333 - Start
		if(resDTO.isBondReq() == reqDTO.getScLoa().isBondReq()){
			getManagedBean().getCtContractAmount().setBondReq(resDTO.isBondReq());
		}else{
			getManagedBean().getCtContractAmount().setBondReq(false);
		}
		
		if (resDTO.isBondReq() == reqDTO.getScLoa().isBondReq()) {				 
			getManagedBean().getCtContractAmount().setTotalBondAmt(resDTO.getBondAmount());
		} else {
			getManagedBean().getCtContractAmount().setTotalBondAmt(null);
		}
		//#24333 - End
	}

	/**
	 * The method used to set the bond waiver flag value.
	 */
	public void chkBondWaiverAction(){
		
		int noOfDays = 0; //Enhancement 13847 - shaqimi 28/10/2021
		if(!getManagedBean().getCtContractAmount().isBondWaived()){
			try {
				//-- LOG : 13847, by Fahmi 22032019
				//to copy latest data to history
				//setBondWaiverToTable();
				//set to delete data if data already save in database
				getManagedBean().setCtAttachmentDelete(null);
				if(getManagedBean().getCtAttachmentLatest()!= null && getManagedBean().getCtAttachmentLatest().getAttachmentId() != null && getManagedBean().getCtAttachmentLatest().getAttachmentId() > 0){
					getManagedBean().setCtAttachmentDelete(getManagedBean().getCtAttachmentLatest());
					getManagedBean().setCtAttachmentLatest(SerializationUtils.clone(getManagedBean().getCtAttachmentDelete()));
					getManagedBean().getCtAttachmentDelete().setDeleted(true);
					getManagedBean().getCtAttachmentDelete().getCtBondWaiverDetail().setDeleted(true);
					getManagedBean().getCtAttachmentDelete().setBrandNew(false);
				}
				//13847 - start
				getManagedBean().setWaiverDetailDelete(null);
				for(CtAttachment attachmentHistory : getManagedBean().getWaiverDetail()){
					if(attachmentHistory.getRecordStatusFlg().equals(5)){
						attachmentHistory.setDeleted(true);
						getManagedBean().getWaiverDetailDelete().add(attachmentHistory);
					}
				}
				
				for(CtAttachment attachmentDelete : getManagedBean().getWaiverDetailDelete()){
					getManagedBean().getWaiverDetail().remove(attachmentDelete);
				}
				//13847 - end
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getManagedBean().getCtContractAmount().setBondWaived(false);
			if(getManagedBean().getContractVer().getCtContract().getLatestContractVer() == null){
				getManagedBean().getContractVer().setBondWaiverAttachments(null);
			}
			else {
				Iterator<CtAttachment> attachIterator= getManagedBean().getContractVer().getBondWaiverAttachments().iterator();
				while(attachIterator.hasNext()){
					if(chkHistoryOfAttachment(attachIterator.next())){
						continue;
					}else {
						attachIterator.remove();
					}
				} 
			}   
		} else if(getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null){
			if(getManagedBean().getCtContractAmount().isBondWaived() ==  getManagedBean().getCtTestBondWaiverAmount().isBondWaived()){
				if(getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId() != null){
					Iterator<CtAttachment> attachIterator= getManagedBean().getContractVer().getBondWaiverAttachments().iterator();
					while(attachIterator.hasNext()){
						if(chkHistoryOfAttachment(attachIterator.next())){
							continue;
						}else {
							attachIterator.remove();
						}
					} 
				}   
				getManagedBean().setBondWaiver(true);
			}
		}	
		//-- LOG : 13847, by Fahmi 22032019
		///to set value null full tick/untick.
		getManagedBean().setCtAttachmentLatest(null);
		
		//Enhancement 13847 - shaqimi start 28/10/2021
		for(CtAttachment ctAttachmentHistory : getManagedBean().getWaiverDetail()){
			noOfDays += findDifference(ctAttachmentHistory.getCtBondWaiverDetail().getWaiverStartDate(), 
				ctAttachmentHistory.getCtBondWaiverDetail().getWaiverEndDate());
		}
		
		if(noOfDays >= 60){
			getManagedBean().setDisableDeferment(true);
		} else {
			getManagedBean().setDisableDeferment(false);
		}
		//Enhancement 13847 - shaqimi end 28/10/2021

	}
	
	private boolean chkHistoryOfAttachment(CtAttachment attachments){
		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setDocId(getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId());
		reqDTO.setDocType(getManagedBean().getContractVer().getCtContract().getLatestContractVer().getDocType());
		reqDTO.setSectionId(ContractConst.ATTACHMENT_BOND_WAIVER);
		IContractSO contractSO =   (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		List<CtAttachment> attach= contractSO.retrieveAttachmentBySectionId(reqDTO).getCtAttachments();
		if(attach != null && attach.size() > 0){
			for(CtAttachment waivedAttach : attach){
				if(waivedAttach.getFilePath().equalsIgnoreCase(attachments.getFilePath())){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * The method used to upload bond exempted file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void uploadBondExemptedFile(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();

		getManagedBean().getCtContractAmount().setBondExemptedFileName(item.getFileName());
		getManagedBean().getCtContractAmount().setBondExemptedFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		RequestContext.getCurrentInstance().execute("uploadBondExemptedDlg.hide();");
	}

	/**
	 * Function to check / uncheck all checkbox into performance bond datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllBond(AjaxBehaviorEvent e) { 

		for (CtPerformanceBond bonds : getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
			if (getManagedBean().isSelectAll()) {
				bonds.setSelectedCheckBox(true);
				if(getManagedBean().isButtonHideActive()){
					getManagedBean().setButtonHideActive(false);
				}
			} else{
				bonds.setSelectedCheckBox(false);
				if(!getManagedBean().isButtonHideActive()){
					getManagedBean().setButtonHideActive(true);
				}
			}
		}
	}

	/**
	 * Function to check / uncheck single checkbox into performance bond datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOnePerformanceCheckboxSelect(AjaxBehaviorEvent e){
		getManagedBean().setSelectAll(false);
		for (CtPerformanceBond bonds : getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
			if(bonds.isSelectedCheckBox()){
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
	 * Function to check / uncheck all checkbox into deduction datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllDeduction(AjaxBehaviorEvent e) {
		getManagedBean().setButtonHideDedActive(true);
		for (CtDeduction deduct : getManagedBean().getContractVer().getCtDeductions()) {
			if (getManagedBean().isSelectAll() && !deduct.isFromDpQt()) {
				if(getManagedBean().isButtonHideDedActive()){
					getManagedBean().setButtonHideDedActive(false);
				}
				deduct.setSelectedCheckBox(true);
			}else {
				deduct.setSelectedCheckBox(false);
			}
		}
	}

	/**
	 * Function to check / uncheck all checkbox into deduction clause datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllDeductionClause(AjaxBehaviorEvent e) {

		for (CtDeductionClause ctDeductionClauses : getManagedBean().getSelectedDeduction().getCtDeductionClauses()) {
			if (getManagedBean().isSelectAll()) {
				ctDeductionClauses.setSelectedCheckBox(true);
			}else {
				ctDeductionClauses.setSelectedCheckBox(false);
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideDedActive(false);
		}else{
			getManagedBean().setButtonHideDedActive(true);
		}	
	}

	/**
	 * Function to check / uncheck all checkbox into schedule datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllScheduleDtl(AjaxBehaviorEvent e) {

		for (CtSchedule ctSchedules : getManagedBean().getContractVer().getCtSchedules()) {
			if (getManagedBean().isSelectAll()) {
				ctSchedules.setSelectedCheckBox(true);
				if(getManagedBean().isButtonHideSchActive()){
					getManagedBean().setButtonHideSchActive(false);
				}
			}else {
				ctSchedules.setSelectedCheckBox(false);
				if(!getManagedBean().isButtonHideSchActive()){
					getManagedBean().setButtonHideSchActive(true);
				}
			}
		}
	}

	/**
	 * Function to check / uncheck single checkbox into schedule datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneScheCheckboxSelect(AjaxBehaviorEvent e){
		getManagedBean().setSelectAll(false);
		for (CtSchedule ctSchedules : getManagedBean().getContractVer().getCtSchedules()) {
			if(ctSchedules.isSelectedCheckBox()){
				getManagedBean().setButtonHideSchActive(false);
				return;
			}
		}
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideSchActive(false);
		}else{
			getManagedBean().setButtonHideSchActive(true);
		}	
	}

	/**
	 * Function to check / uncheck all checkbox into PID datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllPidDtl(AjaxBehaviorEvent e) {

		for (CtPidReason ctPidReasons : getManagedBean().getContractVer().getCtPidReasons()) {
			if (getManagedBean().isSelectAll()) {
				ctPidReasons.setSelectedCheckBox(true);
				if(getManagedBean().isButtonHideActive()){
					getManagedBean().setButtonHideActive(false);
				}
			}else {
				ctPidReasons.setSelectedCheckBox(false);
				if(!getManagedBean().isButtonHideActive()){
					getManagedBean().setButtonHideActive(true);
				}
			}
		}
	}

	/**
	 * Function to check / uncheck single checkbox into PID datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOnePidCheckboxSelect(AjaxBehaviorEvent e){
		getManagedBean().setSelectAll(false);
		for (CtPidReason ctPidReasons : getManagedBean().getContractVer().getCtPidReasons()) {
			if(ctPidReasons.isSelectedCheckBox()){
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
	 * Function to check / uncheck all checkbox into progressive payment datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllProPaymentDtl(AjaxBehaviorEvent e) {

		for (CtProgressivePayment ctProgressivePayments : getManagedBean().getContractVer().getCtProgressivePayments()) {
			if (getManagedBean().isSelectAll()) {
				ctProgressivePayments.setSelectedCheckBox(true);
				if(getManagedBean().isButtonHideSchActive()){
					getManagedBean().setButtonHideSchActive(false);
				}
			}else {
				ctProgressivePayments.setSelectedCheckBox(false);
				if(!getManagedBean().isButtonHideSchActive()){
					getManagedBean().setButtonHideSchActive(true);
				}
			}
		}
	}

	/**
	 * Function to check / uncheck single checkbox into progressive payment datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneProPaymentCheckboxSelect(AjaxBehaviorEvent e){
		getManagedBean().setSelectAll(false);
		for (CtProgressivePayment ctProgressivePayments : getManagedBean().getContractVer().getCtProgressivePayments()) {
			if(ctProgressivePayments.isSelectedCheckBox()){
				getManagedBean().setButtonHideSchActive(false);
				return;
			}
		}
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideSchActive(false);
		}else{
			getManagedBean().setButtonHideSchActive(true);
		}	
	}

	/**
	 * Function to check / uncheck all checkbox into agency datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllAgencyCheckbox(){
		for (CtAgency ctAgencies : getManagedBean().getContractVer().getCtAgencies()) {
			if (getManagedBean().isSelectAll()) {
				ctAgencies.setSelectedCheckBox(true);
				getManagedBean().setButtonHideActive(false);
			}else{
				ctAgencies.setSelectedCheckBox(false);
				getManagedBean().setButtonHideActive(true);
			}
		}	
	}

	/**
	 * Function to check / uncheck single checkbox into agency datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneAgencyCheckboxSelect(AjaxBehaviorEvent e){
		getManagedBean().setSelectAll(false);
		Long orgProfileId = new Long(0);
		getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
		if((boolean) e.getComponent().getAttributes().get("value")){
			for (CtAgency ctAgencies : getManagedBean().getContractVer().getCtAgencies()) {
				if(ctAgencies.isSelectedCheckBox()){
					orgProfileId = ctAgencies.getOrgProfile().getMinstryId();
				}
			}
			if(!getManagedBean().getMinistryProfileId().equals(orgProfileId)){
				for (CtAgency ctAgencies : getManagedBean().getContractVer().getCtAgencies()) {
					if(orgProfileId.equals(ctAgencies.getOrgProfile().getMinstryId())){
						ctAgencies.setSelectedCheckBox(true);
					}
				}
			}
			getManagedBean().setButtonHideActive(false);
		}else{
			for (CtAgency ctAgencies : getManagedBean().getContractVer().getCtAgencies()) {
				if(!ctAgencies.isSelectedCheckBox()){
					orgProfileId = ctAgencies.getOrgProfile().getMinstryId();
					if(orgProfileId == null){
						orgProfileId = new Long(0);
					}
				}
			}
			if(!getManagedBean().getMinistryProfileId().equals(orgProfileId)){
				for (CtAgency agencies : getManagedBean().getContractVer().getCtAgencies()){
					if(orgProfileId.equals(agencies.getOrgProfile().getMinstryId())){
						agencies.setSelectedCheckBox(false);
					}
				}
			}
			getManagedBean().setButtonHideActive(true);
		}	
	}
	/**
	 * The method used for generating a list of available financial institution
	 * for performance bond as selection.
	 * 
	 * @return the selection list of financial institution
	 */
	public SelectItem[] retrieveFinancialOrgList() {
		SelectItem[] items = null; 
		IFinancialOrgSO financialOrgSO;

		try {
			int i = 1;
			financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PERFORMANCE_BOND);
			FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);

			List<PmFinancialOrg> list = resDTO.getFinancialOrgList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

			for (PmFinancialOrg pmFinancialOrg : list) {
				items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(), pmFinancialOrg.getFinOrgName()); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			financialOrgSO = null;
		}
		return items;
	}

	/**
	 * The method used to add a new performance bond record.
	 * This method will generate a new record in performance bond
	 * list, user allow to enter the bond details and attachment. 
	 */
	public void addBondDetails() {
		CtPerformanceBond newBond = new CtPerformanceBond();
		newBond.setFinancialOrg(new PmFinancialOrg());
		newBond.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
		Calendar cal = Calendar.getInstance();
		cal.setTime(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		cal.add(Calendar.YEAR, 1); 
		newBond.setExpDate(cal.getTime());
		//#24333 - Start
		if(isContractExtendOrIsBond()){
			newBond.setBondNew(true);
			getManagedBean().getCtContractAmount().getCtPerformanceBondsNew().add(newBond);
			getManagedBean().getCtContractAmount().getCtPerformanceBonds().add(newBond);
		}else{
			newBond.setBondNew(false);
			getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal().add(newBond);
			getManagedBean().getCtContractAmount().getCtPerformanceBonds().add(newBond);
		}
		//#24333 - End
	}		    

	/**
	 * The method used to upload performance bond file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void fileUploadPerformance(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
		//#24333 - Start
		if(isContractExtendOrIsBond()){
			for (CtPerformanceBond ctPerformanceBondNew : getManagedBean().getCtContractAmount().getCtPerformanceBondsNew()) {
				if (ctPerformanceBondNew.equals(getManagedBean().getCtPerformanceBond())) {
					ctPerformanceBondNew.setFileName(item.getFileName());
					ctPerformanceBondNew.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
				}
			}
			for (CtPerformanceBond ctPerformanceBonds : getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
				if (ctPerformanceBonds.equals(getManagedBean().getCtPerformanceBond())) {
					ctPerformanceBonds.setBrandNew(true);
					ctPerformanceBonds.setFileName(item.getFileName());
					ctPerformanceBonds.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
				}
			}
		}else{
			for (CtPerformanceBond ctPerformanceBond : getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal()) {
				if (ctPerformanceBond.equals(getManagedBean().getCtPerformanceBond())) {
					ctPerformanceBond.setFileName(item.getFileName());
					ctPerformanceBond.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
				}
			}
		}
		//#24333 - End
		RequestContext.getCurrentInstance().execute("uploadBondDlg.hide()");
	}

	/**
	 * The method used to delete the selected performance bond(s).
	 */
	public void removePerformanceBond() {
		try {
			//#24333 - Start
			if(isContractExtendOrIsBond()){
				if (getManagedBean().getCtContractAmount().getCtPerformanceBondsNew() != null) {
					Iterator<CtPerformanceBond> performanceBond = getManagedBean().getCtContractAmount().getCtPerformanceBondsNew().iterator();
					while (performanceBond.hasNext()) {
						CtPerformanceBond  bond = performanceBond.next();
						if (bond.isSelectedCheckBox()) {
							performanceBond.remove();
						}
					}
				}
			}else{
				if (getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal() != null) {
					Iterator<CtPerformanceBond> performanceBond = getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal().iterator();
					while (performanceBond.hasNext()) {
						CtPerformanceBond  bond = performanceBond.next();
						if (bond.isSelectedCheckBox()) {
							performanceBond.remove();
						}
					}
				}
				//Start - Fix #24333
				if (getManagedBean().getCtContractAmount().getCtPerformanceBonds() != null) {
					Iterator<CtPerformanceBond> performanceBond = getManagedBean().getCtContractAmount().getCtPerformanceBonds().iterator();
					while (performanceBond.hasNext()) {
						CtPerformanceBond  bond = performanceBond.next();
						if (bond.isSelectedCheckBox()) {
							performanceBond.remove();
						}
					}
				}
				//End - Fix #24333
				
				
			}
		//#24333 - End
			getManagedBean().setSelectAll(false);
			getManagedBean().setButtonHideActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {

		}
	}

	/**
	 * The method used to render the ceiling quantity information
	 * of the selected product item.
	 */
	public void onSelectProductItem() {
		try{
			ContractItemSearchBackingBean cisbb = new ContractItemSearchBackingBean();
			log.info("Selected Item ***"+getManagedBean().getSelectedItem());
			log.info("Show Item ****"+getManagedBean().getShowItem());
			if (getManagedBean().getSelectedItem() != null || getManagedBean().getServiceselectedItem()!=null) {

				if (getManagedBean().getShowItem() != null
						&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
					getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
				} else if (getManagedBean().getShowItem() != null
						&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
					getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
					refreshAgencyCeilingQuantityList();
				}  else if (getManagedBean().getShowItem() != null
						&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY)) {
					getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY);
					//refreshAgencyCeilingQuantityServiceList();
				} else {
					getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
				}
				// retrieve zone label for new price
				if (getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)
						|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
						|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
					itemPriceZoneLabelList(getManagedBean().getSelectedItem());
					itemMaxQtyZoneLabelList(getManagedBean().getSelectedItem());
				}
			}
		}
		catch(Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * The method used to render the ceiling quantity information
	 * of the selected product item.
	 */
	public void onSelectServiceItem() {
		try{
			ContractItemSearchBackingBean cisbb = new ContractItemSearchBackingBean();
			log.info("Selected Item onSelectServiceItem ***"+getManagedBean().getServiceselectedItem());
			log.info("Show Item  onSelectServiceItem ****"+getManagedBean().getShowItem());
			if (getManagedBean().getSelectedItem() != null || getManagedBean().getServiceselectedItem()!=null) {
				//cisbb.getManagedBean().setItemProductExist(false);
				if (getManagedBean().getShowServiceItem() != null
						&& getManagedBean().getShowServiceItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
					getManagedBean().setShowServiceItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
				} else if (getManagedBean().getShowServiceItem() != null
						&& getManagedBean().getShowServiceItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
					getManagedBean().setShowServiceItem(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
					refreshAgencyCeilingQuantityServiceList();
				}  else if (getManagedBean().getShowServiceItem() != null
						&& getManagedBean().getShowServiceItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY)) {
					getManagedBean().setShowServiceItem(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY);
					refreshAgencyCeilingQuantityServiceList();
				} else {
					getManagedBean().setShowServiceItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
				}
				// retrieve zone label for new price
				if (getManagedBean().getServiceselectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)
						|| getManagedBean().getServiceselectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
						|| getManagedBean().getServiceselectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
					itemPriceZoneLabelList(getManagedBean().getServiceselectedItem());
					itemMaxQtyZoneLabelList(getManagedBean().getServiceselectedItem());
				}
			}
		}
		catch(Exception ex )
		{
			ex.printStackTrace();
		}
	}


	/**
	 * The method used to refresh the ceiling quantity information of Product details listing in UI.
	 */
	private void refreshAgencyCeilingQuantityList() {
		Map<Long, CtItemCeilingQty> itemCeilingQtyMap = new HashMap<Long, CtItemCeilingQty>();
		for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
			itemCeilingQtyMap.put(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId(), ctItemCeilingQty);
		}
		
		getManagedBean().getSelectedItem().getCtItemCeilingQties().clear();
		//getManagedBean().getServiceselectedItem().getCtItemCeilingQties().clear();
		for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
			if (!itemCeilingQtyMap.containsKey(ctAgency.getOrgProfile().getOrgProfileId())) {
				CtItemCeilingQty ctItemCeilingQty = new CtItemCeilingQty();
				ctItemCeilingQty.setCtAgency(ctAgency);
				ctItemCeilingQty.setCtContractItem(getManagedBean().getSelectedItem());
				
				
				ctItemCeilingQty.setCeilingQuantity(new BigDecimal(0));
				ctItemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
				ctItemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
				getManagedBean().getSelectedItem().getCtItemCeilingQties().add(ctItemCeilingQty);
				
			} else {
				getManagedBean().getSelectedItem().getCtItemCeilingQties().add(itemCeilingQtyMap.get(ctAgency.getOrgProfile().getOrgProfileId()));
				
			}
		}
	}
	/**
	 * The method used to refresh the ceiling quantity information of Service Details listing in UI.
	 */
	private void refreshAgencyCeilingQuantityServiceList() {
		Map<Long, CtItemCeilingQty> itemCeilingQtyMap = new HashMap<Long, CtItemCeilingQty>();
		
		for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getServiceselectedItem().getCtItemCeilingQties()) {
			itemCeilingQtyMap.put(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId(), ctItemCeilingQty);
		}
		
		getManagedBean().getServiceselectedItem().getCtItemCeilingQties().clear();
		//getManagedBean().getSelectedItem().getCtItemCeilingQties().clear();
		for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
			if (!itemCeilingQtyMap.containsKey(ctAgency.getOrgProfile().getOrgProfileId())) {
				CtItemCeilingQty ctItemCeilingQty = new CtItemCeilingQty();
				ctItemCeilingQty.setCtAgency(ctAgency);
				
				ctItemCeilingQty.setCtContractItem(getManagedBean().getServiceselectedItem());
				
				ctItemCeilingQty.setCeilingQuantity(new BigDecimal(0));
				ctItemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
				ctItemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
				
				getManagedBean().getServiceselectedItem().getCtItemCeilingQties().add(ctItemCeilingQty);
			} else {
		
				getManagedBean().getServiceselectedItem().getCtItemCeilingQties().add(itemCeilingQtyMap.get(ctAgency.getOrgProfile().getOrgProfileId()));
			}
		}
	}
	
	
	/**
	 * The method used to indicate the label flag for new price of contract item.  
	 */
	private void itemPriceZoneLabelList(CtContractItem ctContractItem) {
		if (!ctContractItem.getCtItemPrices().isEmpty()) {
			for (int temp = 0;temp<ctContractItem.getCtItemPrices().size(); temp++) {
				Date date = ctContractItem.getCtItemPrices().get(temp).getEffDate();
				int counter =0;
				int index=0;
				int countRow =0;
				for (CtItemPrice itemPrice1 : ctContractItem.getCtItemPrices()) {
					if(itemPrice1.getCtContractZone() != null){
						countRow++;
						if (ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)
								|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
								|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TARIFF)
								|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
							if (ctContractItem.getCtItemPrices().get(temp).getFromQty().equals(itemPrice1.getFromQty()) 
									&& ctContractItem.getCtItemPrices().get(temp).getToQty().equals( itemPrice1.getToQty()) 
									&& ctContractItem.getCtItemPrices().get(temp).getCtContractZone().getZoneName() != null
									&& ctContractItem.getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(itemPrice1.getCtContractZone().getZoneName())) {
								counter++;
								if (counter >= 2) {
									if (date.before(itemPrice1.getEffDate())) {
										index = countRow;
										date = itemPrice1.getEffDate();
									}	
								}
							}
						} else if (ctContractItem.getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(itemPrice1.getCtContractZone().getZoneName())) {
							counter++;
							if (counter >= 2) {
								if (date.before(itemPrice1.getEffDate())) {
									index = countRow;
									date = itemPrice1.getEffDate();
								}
							}
						}
					}
				}
				if (index > 0) {
					CtItemPrice priceOfItem = ctContractItem.getCtItemPrices().get(index-1);
					priceOfItem.setChangeZoneLabel(true);
					ctContractItem.getCtItemPrices().remove(index-1);
					ctContractItem.getCtItemPrices().add(index-1, priceOfItem);
				}
			}
		}
	}

	/**
	 * The method used to indicate the label flag for new max qty of contract item.
	 */
	private void itemMaxQtyZoneLabelList(CtContractItem ctContractItem) {
		if (!ctContractItem.getCtItemMaxQties().isEmpty()) {
			for (CtItemMaxQty zonelOfItem : ctContractItem.getCtItemMaxQties()) {
				zonelOfItem.setChangeZoneLabel(false);
			}
			for (int temp = 0; temp < ctContractItem.getCtItemMaxQties().size(); temp++) {
				Date date = ctContractItem.getCtItemMaxQties().get(temp).getEffDate();
				int counter =0;
				int index = 0;
				int countRow =0;
				for (CtItemMaxQty itemMaxQty1 : ctContractItem.getCtItemMaxQties()) {
					countRow++;
					if (ctContractItem.getCtItemMaxQties().get(temp).getCtContractZone() != null
							&& ctContractItem.getCtItemMaxQties().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(
									itemMaxQty1.getCtContractZone().getZoneName())) {
						counter++;
						if (counter >= 2) {
							if (date.before(itemMaxQty1.getEffDate())) {
								index = countRow;
								date = itemMaxQty1.getEffDate();
							}
						}
					}
				}
				if (index > 0) {
					CtItemMaxQty maxQtyOfItem = ctContractItem.getCtItemMaxQties().get(index-1);
					maxQtyOfItem.setChangeZoneLabel(true);
					ctContractItem.getCtItemMaxQties().remove(index-1);
					ctContractItem.getCtItemMaxQties().add(index-1, maxQtyOfItem);
				}
			}
		}
	}

	/**
	 * The method used to render the selected product item information.
	 */
	public void changeShowItem() {
		try {
				if(getManagedBean().getShowItem()!=null)
				{
					if (getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_QTY)) {
						refreshAgencyCeilingQuantityList();
					}else if (getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY)) {
						refreshAgencyCeilingQuantityList();
					}	
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The method used to render the selected service item information.
	 */
	public void changeServiceShowItem() {
		try {
			
			if(getManagedBean().getShowServiceItem()!=null)
			{
				if (getManagedBean().getShowServiceItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_QTY)) {
					refreshAgencyCeilingQuantityServiceList();
				}else if (getManagedBean().getShowServiceItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY)) {
					refreshAgencyCeilingQuantityServiceList();
				}	
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The method used to retrieve the item ceiling quantity which in active status
	 * to be displayed on screen.
	 * 
	 * @return activeList  the list of active ceiling quantity to be displayed 
	 */
	public List<CtItemCeilingQty> getActiveItemCeilingQty() {
		List<CtItemCeilingQty> activeList = new ArrayList<CtItemCeilingQty>();
		if (getManagedBean().getSelectedItem() != null) {
			if(getManagedBean().getSelectedItem().getCtItemCeilingQties().size()>0){
			  for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
				  //#30428 - agency not display because isChkStatus is false
				  //to re-check status for isChkStatus if it is false
				  if (!ctItemCeilingQty.getCtAgency().isChkStatus()) {
					  if (ctItemCeilingQty.getCtAgency().getRecordStatusFlg() != 0 
							  && ctItemCeilingQty.getCtAgency().getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
						  ctItemCeilingQty.getCtAgency().setChkStatus(false);
					  } else {
						  ctItemCeilingQty.getCtAgency().setChkStatus(true);
					  }
				  }
				  
				  if (ctItemCeilingQty.getCtAgency().isChkStatus()) {
					activeList.add(ctItemCeilingQty);
				  }
			  }
			}	  
		}
		return activeList;
	}

	/**
	 * The method used for validating the ceiling quantity of items.
	 * A warning message will display on screen when the total ceiling quantity is 
	 * larger than the maximum quantity of the item.
	 * 
	 * @param ceilingQtyData   object of item ceiling quantity
	 */
	public void validateCeilingQty(CtItemCeilingQty ceilingQtyData) {
		double totalCeilingQuantity = 0.0;
		CtContractItem item = this.getManagedBean().getSelectedItem();
		if (item.getCtItemMaxQties().size() != 0) {
			for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
				totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();

			} if (item.getTotalMaxQuantity().doubleValue() < totalCeilingQuantity) {
				ceilingQtyData.setCeilingQuantity(BigDecimal.valueOf(0.00));
				throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
			}
		}
	}

	/**
	 * The method used to validate the entered minimum order quantity.
	 * 
	 * @param ctContractItem   object of contract item
	 */
	public void onChangeMinimumQty(CtContractItem ctContractItem) {

		//Bug #28356 - Start
		//#28479 - Start Putra
		if(ctContractItem.getContractItemId() != null){	//28292	- null checker
			if (ctContractItem.getMinimumOrderQty() <= 0) {
				for (CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
				if(ctContractItem.getScRequestItem().getRequestNoteItemId().equals(item.getScRequestItem().getRequestNoteItemId())){
						item.setMinimumOrderQty(0);
						ctContractItem.setMinimumOrderQty(0);
						throw new EpException(MessageConst.ERROR_100033,
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.min.order.qty",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			
	if (ctContractItem.getMinimumOrderQty() > 0) {
		for (CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
		if(ctContractItem.getScRequestItem().getRequestNoteItemId().equals(item.getScRequestItem().getRequestNoteItemId())){
		   item.setMinimumOrderQty(ctContractItem.getMinimumOrderQty());
			if (!item.getCtItemMaxQties().isEmpty()) {
				Double maxValue = new Double(0);							
				for (CtItemMaxQty maxQty : item.getCtItemMaxQties()) {
					if(maxQty.getMaxQuantity().doubleValue() > maxValue)
						maxValue = maxQty.getMaxQuantity().doubleValue();				
				}
				if (item.getMinimumOrderQty() > maxValue) {
					throw new EpException(MessageConst.ERROR_100034,
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.min.order.qty",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});					
							}				
						}
					}//#28479 - End Putra
				}
			}			
		}
		getManagedBean().getUpdateItemMap().put(ctContractItem.getScRequestItem().getRequestNoteItemId(), ctContractItem);
		log.info("Putra Check Item :"+ctContractItem.getMinimumOrderQty() );			
	}

	/**
	 * The method used to validate the entered zone delivery term.
	 */
	public void onChangeDeliveryTerm(LocationDTO locationDTO) {
		if (!getManagedBean().getContractVer().getCtContractZones().isEmpty()) {
			for (CtContractZone ctContractZone : getManagedBean().getContractVer().getCtContractZones()) {
				if(ctContractZone.getZoneName().equalsIgnoreCase(locationDTO.getZone())) {
					getManagedBean().getUpdateZoneMap().put(locationDTO.getZone(), ctContractZone);
					break;
				}
			}
		}
	}	



	/**
	 * The method used to set the date of frequency sSchedule.
	 */
	public void scheduleDate() {	
		for(CtSchedule ctSchedules : getManagedBean().getContractVer().getCtSchedules()) {			   
			if (ctSchedules.getFromDate() != null && ctSchedules.getToDate() != null) {
				String fromDate = dateFormat.format(ctSchedules.getFromDate());
				ctSchedules.setStartMonth(fromDate.substring(3, 5));
				ctSchedules.setStartYear(fromDate.substring(6,fromDate.length()));

				String toDate = dateFormat.format(ctSchedules.getToDate());
				ctSchedules.setEndMonth(toDate.substring(3, 5));
				ctSchedules.setEndYear(toDate.substring(6, toDate.length()));
			}
		}
	}

	/**
	 * The method used to set the date of progressive payment.
	 */
	public void progressiveDate() {	
		for(CtProgressivePayment ctSchedules : getManagedBean().getContractVer().getCtProgressivePayments()) {			   
			if (ctSchedules.getFromDate() != null && ctSchedules.getToDate() != null) {
				String fromDate = dateFormat.format(ctSchedules.getFromDate());
				ctSchedules.setStartMonth(fromDate.substring(3, 5));
				ctSchedules.setStartYear(fromDate.substring(6,fromDate.length()));

				String toDate = dateFormat.format(ctSchedules.getToDate());
				ctSchedules.setEndMonth(toDate.substring(3, 5));
				ctSchedules.setEndYear(toDate.substring(6, toDate.length()));
			}
		}
	}

	/**
	 * The method used to validation 
	 * for amendment if user selected other change
	 * 
	 * @return true, if other change is selected
	 *         false, if other change is not selected
	 */
	public boolean getValueFromAmendment(){
		if(getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT)){
			if(getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * The method used to retrieve schedule or progressive 
	 * from date and to date value.
	 */
	public void scheduleProgressiveDate() {    	
		if (getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
			if (getManagedBean().getContractVer().getCtSchedules() != null 
					&& !getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
				for (CtSchedule schedule : getManagedBean().getContractVer().getCtSchedules()) {
					if (schedule.getStartMonth() != null && schedule.getEndMonth() != null) {
						String fromDate =  "01/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
						String toDate = "01/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
						try {
							schedule.setFromDate(dateFormat.parse(fromDate));
							schedule.setToDate(dateFormat.parse(toDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				getManagedBean().getContractVer().setCtProgressivePayments(null);
			}
		} else {
			if (getManagedBean().getContractVer().getCtProgressivePayments() != null 
					&& !getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
				for (CtProgressivePayment schedule : getManagedBean().getContractVer().getCtProgressivePayments()) {
					if (schedule.getStartMonth() != null && schedule.getEndMonth() != null) {
						String fromDate = "01/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
						String toDate = "01/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
						try {
							schedule.setFromDate(dateFormat.parse(fromDate));
							schedule.setToDate(dateFormat.parse(toDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				getManagedBean().getContractVer().setCtSchedules(null);
			} 
		}	
	}

	/**
	 * The method used to find active circular 
	 * record return boolean value in case record true
	 *
	 * @return true, if there is an active circular record
	 *         false, if there isn't active circular record
	 */
	public boolean chkCircularActiveRecord(){
		ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularSO.class);
		CircularReqDTO reqDTO = new CircularReqDTO();
		reqDTO.setContractId(getManagedBean().getContractVer().getCtContract().getContractId());
		CircularResDTO resDTO = circularSO.retrieveCircularVerByContractId(reqDTO);
		if(resDTO.getCircularVer()!=null) {
			getManagedBean().setCtCircularVer(resDTO.getCircularVer());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The method used to render the check box for company name
	 * and supplier address if there are any updates from profile
	 * management module.
	 * 
	 * @param ctContractVer   an object of contract version
	 */
	public void renderUpdateCheckbox(CtContractVer ctContractVer) {
		getManagedBean().setUpdateCompanyName(false);
		getManagedBean().setUpdateSupplierAddress(false);
		if (ctContractVer.getCtContractValue().isCompanyNameChange()
				|| !ctContractVer.getCtContractValue().getSupplierName().equalsIgnoreCase(
						getManagedBean().getContractVer().getCtContract().getSmSupplier().getCompanyName())) {
			getManagedBean().setUpdateCompanyName(true);
		}
		if (ctContractVer.getCtContractValue().isAddressChange()
				|| CtAddressUtil.isSupplierInfoChanged(
						getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl(),
						ctContractVer.getCtContractValue().getSupplierAddress())) {
			getManagedBean().setUpdateSupplierAddress(true);
		}
	}

	/**
	 * @param ctPerformanceBond the ctPerformanceBond to set
	 */
	public void PerformanceBondValue(CtPerformanceBond ctBond){
		getManagedBean().setCtPerformanceBond(ctBond);

	}

	/**
	 * The method used to delete 
	 * the uploaded bond attached file
	 */    
	public void deleteBondAttachement(){
		//#24333
		if(isContractExtendOrIsBond()){			
			CtPerformanceBond ctBondAttch =  getManagedBean().getCtContractAmount().getCtPerformanceBondsNew().get(getManagedBean().getIndex());
			getManagedBean().getCtContractAmount().getCtPerformanceBondsNew().remove(getManagedBean().getIndex());
			ctBondAttch.setFileName(null);
			ctBondAttch.setFilePath(null);
			getManagedBean().getCtContractAmount().getCtPerformanceBondsNew().add(getManagedBean().getIndex(), ctBondAttch);
		}else{
			CtPerformanceBond ctBondAttch =  getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal().get(getManagedBean().getIndex());
			getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal().remove(getManagedBean().getIndex());
			ctBondAttch.setFileName(null);
			ctBondAttch.setFilePath(null);
			getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal().add(getManagedBean().getIndex(), ctBondAttch);
		}
		

		
	}

	/**
	 * This method used to retrieve PID reason code from database 
	 * 
	 * @return the selection list of PID reason code
	 */
	public SelectItem[] getDropdownPidCodeValues() {

		SelectItem[] items = null;
		ReasonReqDTO reqDTO = null;
		IReasonSO reasonSO = null;
		List<PmReasonDesc> list = null;
		ReasonResDTO resDTO  = null;
		ISearchBean<PmReasonDesc> searchBean = new SearchBean<PmReasonDesc>();
		int i = 1;
		try {

			reqDTO = new ReasonReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setLanguage(facesScopes.getUserLocale().getLanguage());
			reqDTO.setSortBy("pmReason.reasonId");
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.reasonCategoryCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(), ReasonCategory.PID_JUSTIFICATION.value()));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.pmPIDReasonList.docType", SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.CONTRACT_TYPE_MINISTRY));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.recordStatusFlg", SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));
			reqDTO.setReasonDescSearchBean(searchBean);

			reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
			resDTO = reasonSO.retrieveReasonDescBySearchBean(reqDTO);
			list = resDTO.getReasonDescResultBean().getResultList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()));
			for (PmReasonDesc reasonDesc : list) {
				items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			reqDTO = null;
			reasonSO = null;
			list = null;
			searchBean = null;
			resDTO = null;
		}
		return items;
	}

	/**
	 * This method used to add
	 *  list of PID reason into data table
	 */		
	public void addPurchaseInDefault(){
		CtPidReason ctPidReasons = new CtPidReason();
		ctPidReasons.setPmReason(new PmReason());
		getManagedBean().getContractVer().getCtPidReasons().add(ctPidReasons);
	}

	/**
	 * This method used to delete the selected PID reason(s).
	 */		
	public void deletePidPurchase(){
		try {
			Iterator<CtPidReason> ctPidReasonIterator = getManagedBean().getContractVer().getCtPidReasons().iterator();
			while(ctPidReasonIterator.hasNext()){
				CtPidReason  ctPidReasons = ctPidReasonIterator.next();
				if(ctPidReasons.isSelectedCheckBox()){
					ctPidReasonIterator.remove();
				}
			}
			getManagedBean().setSelectAll(false);
			getManagedBean().setButtonHideActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	/**
	 * This method used to compare
	 * of purchase in default category reason equals other
	 * 
	 * @param reasonId   selected PID reason id
	 * @return true,     if selected PID reason code is equals other
	 *         false,    if selected PID reason code is not equals other
	 */	
	public boolean PidPurchaseTypeOther(Long reasonId) {
		if(reasonId == null || reasonId.intValue() == 0)
			return false;

		ReasonReqDTO reqDTO = null;
		IReasonSO reasonSO = null;
		PmReason pmReason = null;
		try {
			FacesScopes facesScope = new FacesScopes(); 
			reqDTO = new ReasonReqDTO();
			reqDTO.setReasonId(reasonId);
			reqDTO.setLanguage(facesScope.getUserLocale().getLanguage());
			reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
			pmReason = reasonSO.retrieveReasonById(reqDTO).getPmReason();
			if(pmReason != null && pmReason.getReasonCode().equalsIgnoreCase(ProfileManagementConst.PID_REASONTYPE_OTHER)){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			reqDTO = null;
			reasonSO = null;
			pmReason = null;
		}
		return false;
	}

	/**
	 * This method used to validate the duplicated pid reason
	 * 
	 * @param reasonId   selected PID reason id
	 */
	public void validatePidReason(Long resonId){

		if(resonId == null || resonId.intValue() == 0){
			return;
		}
		if(getManagedBean().getContractVer().getCtPidReasons().size() > 0){
			int counter = 0;
			for(CtPidReason  pidReasons : getManagedBean().getContractVer().getCtPidReasons()){
				if(pidReasons.getPmReason() != null 
						&& pidReasons.getPmReason().getReasonId() != null
						&& pidReasons.getPmReason().getReasonId().equals(resonId)){
					counter++;
					if(!PidPurchaseTypeOther(resonId) && counter == 2){
						throw new EpException(MessageConst.ERROR_750020);
					}
				}
			}
		}
	}

	/**
	 * Route to Central Contract Circular Page. 
	 * 
	 * @param contractVerId
	 * @return page url
	 */
	public String gotoCicularPage(Long contractVerId){
		CentralContractBackingBean centralBB = new CentralContractBackingBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		centralBB.getManagedBean().setSelectedCircularContract(null);
		centralBB.getManagedBean().setSelectedCircularItem(null);
		centralBB.getManagedBean().setSelectedZoneItems(null);
		centralBB.getManagedBean().setSelectedTabIndex(0);
		centralBB.getManagedBean().setAction("view");
		centralBB.getManagedBean().setViewContract(true);
		centralBB.getManagedBean().setUpdateAction(false);
		centralBB.getManagedBean().setOutComes(new String[]{});
		centralBB.getManagedBean().setGotoPreviewPage(fc.getViewRoot().getViewId());
		centralBB.getManagedBean().setChkCentralCircular(true);
		centralBB.gotoCommonPage(contractVerId);
		return CENTRAL_CIRCULAR_PAGE;
	}

	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public void goToLoaView(ScLoa scLoa) {
		//Enhancement 15851 - shaqimi start
		String slLoaNo = this.getManagedBean().getContractVer().getCtContractValue().getLoaNo();
		Long contractId = this.getManagedBean().getContractVer().getCtContract().getContractId();
		if(slLoaNo.substring(0,2).equals(ParameterConst.DOCTYPE_SUPPLEMENTAL_CONTRACT_LETTER)){
			AmendmentBackingBean abb = new AmendmentBackingBean();
			//follow structure but for amendment
			DirectLOABackingBean directLoaBackingBean = new DirectLOABackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			directLoaBackingBean.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			directLoaBackingBean.getManagedBean().setTrackingDairyView(true);
			directLoaBackingBean.viewLOA(scLoa.getLoaId());
			
			abb.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			abb.getManagedBean().setTrackingDairyView(true);
			abb.gotoAmendViewPage(contractId, scLoa.getLoaId());
		}
		//Enhancement 15851 - shaqimi end
		else if (scLoa.getScLoiLoa().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_DIRECT_NEGO)) { //Direct Nego
			DirectLOABackingBean directLoaBackingBean = new DirectLOABackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			directLoaBackingBean.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			directLoaBackingBean.getManagedBean().setTrackingDairyView(true);
			directLoaBackingBean.viewLOA(scLoa.getLoaId());
		} else {
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			loaDetailBackingBean.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			loaDetailBackingBean.getManagedBean().setTrackingDairyView(true);
			loaDetailBackingBean.viewLOA(scLoa.getLoaId());
		}
	}

	/**
	 * The method used to retrieve the zone object.
	 */		
	public void getZoneObjDetails() {
		if (!getManagedBean().getContractVer().getCtContractZones().isEmpty()) {
			for (CtContractZone ctContractZone : getManagedBean().getContractVer().getCtContractZones()) {
				for (TreeNode zonename : getManagedBean().getCtZoneTrees().getChildren()) {
					if (ctContractZone.toString().equalsIgnoreCase(((LocationDTO)zonename.getData()).getZoneId())) {
						if (ctContractZone.getDeliveryTermUom() == null) {
							ctContractZone.setDeliveryTermUom(new PmParameter());
						}
						ctContractZone.getDeliveryTermUom().setParameterId(((LocationDTO)zonename.getData()).getUomId());
						ctContractZone.setDeliveryTerm(((LocationDTO)zonename.getData()).getDeliveryTerm());
					}
				}
			}
		}	     
	}

	/**
	 * The method used to verify loa/contract is with GST.
	 * 
	 * @param gstEffDate  the gst effective date
	 * @return true, if loa/contract is with GST
	 * 		   false, if loa/contract is without GST
	 */
	public boolean isLoaContractWithGst(Date gstEffDate) {
		if (gstEffDate != null && gstEffDate.getTime() <= DateUtils.getCurrentDateWithoutTime().getTime())
			return true;
		else
			return false;
	}

	/**
	 * The method used to retrieve GST Launch Date.
	 * 
	 * @return gst launch date
	 */
	public Date getGstLaunchDate() {
		try {
			return DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, ContractConst.GST_LAUNCH_DATE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * The method used to retrieve GST Relief Due Date.
	 * 
	 * @return gst relief due date
	 */
	public Date getGstReliefDueDate() {
		try {
			return DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, ContractConst.GST_RELIEF_DUE_DATE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * The method used to 
	 * calculate over all amount with GST
	 */
	public void onChangeServiceTax() {
		chkServiceTaxAlert();
		getManagedBean().setDisableContractAmount(true);

		if (isLoaContractWithGst(getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
			getManagedBean().setDisableGstAmount(false);			  
			if (getManagedBean().getContractVer().getCtContractValue().isServiceTax()) {
				getManagedBean().setDisableContractAmount(false);
			} else {
				if (getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= getGstReliefDueDate().getTime()) {
					getManagedBean().setDisableGstAmount(true);
				}
			}
		} else {
			getManagedBean().setDisableGstAmount(true);
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
	 * @param pass ajax event type
	 * Set boolean value for user action 
	 * */
	public void onRowSelect(SelectEvent event) {
		getManagedBean().setButtonHideActive(false);
	}

	/**
	 * Method to uncheck boolean value for checkbox 
	 * Set boolean value false for user action 
	 * 
	 * @param pass ajax event type
	 * */
	public void onRowUnselect(UnselectEvent event) {
		getManagedBean().setButtonHideActive(true);
	}


	/**
	 * The method used for generating a list of available display type
	 * of product or service item as selection.
	 * 
	 * @return the selection list of display type
	 */
	public SelectItem[] getItemDisplayTypeDropdownList(String itemType) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		List<String> itemDisplayTypeList = new ArrayList<String> ();
			itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);	
			//itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_QTY);
		
		
		
		if (itemType.equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
			if (this.getManagedBean().getContractVer().getCtAgencies() != null
					&& this.getManagedBean().getContractVer().getCtAgencies().size() > 1) {
				itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
			}
			itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY);
		} else if (itemType.equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
			itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_QTY);	
			if (this.getManagedBean().getContractVer().getCtAgencies() != null
					&& this.getManagedBean().getContractVer().getCtAgencies().size() > 1){
				itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
			}
			
		}			

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(),itemDisplayTypeList));
		 //log.info("itemDisplayTypeList ****"+itemDisplayTypeList.size());
		return super.getDropdownCodeValuesBySearchBean(ParameterTypeConst.ITEM_DISPLAY_TYPE, searchBean);
	}

	/**
	 * The method used to invoke 
	 * and initiate the item specification dialog.
	 */
	public void viewItemSpecDlg(CtContractItem ctContractItem) {
		/*#28854 start*/
		IRequestItemSO requestItemSO = null;
		getManagedBean().setSelectedItemForSpec(ctContractItem);
		requestItemSO = (IRequestItemSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestItemSO.class);
		
		Long requestItemId = this.getManagedBean().getSelectedItemForSpec().getScRequestItem().getRequestNoteItemId();	
		
		if(getManagedBean().getContractVer().getCtContract().getLoa().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)){
			List<String> itemSpecList = new ArrayList<>();
			List<ScRequestItemSpecOffer> itemSpecOfferList = requestItemSO.retrieveNewOfferSpec(requestItemId);			
			
			for(ScRequestItemSpecOffer itemSpecOffer: itemSpecOfferList){
				if(itemSpecOffer.getQuestionType().equals(ContractConst.QUESTION_TYPE_ITEM)){
					if(itemSpecOffer.getSupplierOffer() != null && !itemSpecOffer.getSupplierOffer().isEmpty()){
						getManagedBean().setSupplierOffer(itemSpecOffer.getSupplierOffer());
					}else{
						getManagedBean().setSupplierOffer(null);
					}	
											
					if(itemSpecOffer.getNewOfferSpec() != null && !itemSpecOffer.getNewOfferSpec().isEmpty()){	
						getManagedBean().setNewOfferSpec(itemSpecOffer.getNewOfferSpec());
					}else{
						getManagedBean().setNewOfferSpec(null);
					}
				}
				else if(itemSpecOffer.getQuestionType().equals(ContractConst.QUESTION_TYPE_SPEC)){
					itemSpecList.add(itemSpecOffer.getItemSpec());				
				}	
			}
			getManagedBean().setItemSpecification(itemSpecList);
		}
		
		if(getManagedBean().getContractVer().getCtContract().getLoa().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_QUOTATION) || getManagedBean().getContractVer().getCtContract().getLoa().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_TENDER) || getManagedBean().getContractVer().getCtContract().getLoa().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO)){
			List<ScRequestItemSpecOffer> scRequestItemSpecOffer = requestItemSO.retrieveNewOfferSpec(requestItemId);	
			
			Iterator<ScRequestItemSpecOffer> itr = scRequestItemSpecOffer.iterator();
			while (itr.hasNext()){
				ScRequestItemSpecOffer scRequestItemSpecOfferTemp = itr.next();
				if(scRequestItemSpecOfferTemp.getQuestionType().equals(ContractConst.QUESTION_TYPE_ITEM)){
					itr.remove();
					}
			}
		
			getManagedBean().setScRequestItemSpecOfferList(scRequestItemSpecOffer);		
		}		
		/*#28854 end*/
		log.info("HHHHHHH");
		RequestContext.getCurrentInstance().execute("itemSpecDlg.show()");
	}

	/**
	 * The method used to set  bond 
	 * exempted file path and file name value is null.
	 */
	public void delBondExemptedDoc(){
		getManagedBean().getContractVer().getCtContractAmount().setBondExemptedFilePath(null);
		getManagedBean().getContractVer().getCtContractAmount().setBondExemptedFileName(null);
	}

	/**
	 * Function to check / uncheck all checkbox into agency datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllMinstryAgency(AjaxBehaviorEvent e) {
		if (getAgencyDataModel() != null && getAgencyDataModel().getList() != null) {
			for (PmOrgProfile pmOrgProfiles : getAgencyDataModel().getList()) {
				if (getManagedBean().isSelectAll()) {
					pmOrgProfiles.setSelectedCheckBox(true);
				} else {
					pmOrgProfiles.setSelectedCheckBox(false);
				}
			}

			if ((boolean) e.getComponent().getAttributes().get("value")) {
				getManagedBean().setButtonHideActive(false);
			} else {
				getManagedBean().setButtonHideActive(true);
			}	
		}
	}

	/**
	 * Function to check / uncheck single checkbox into agency datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectOneAgyCheckboxSelect(AjaxBehaviorEvent e) {
		if (getAgencyDataModel() != null && getAgencyDataModel().getList() != null) {
			getManagedBean().setSelectAll(false);
			for (PmOrgProfile pmOrgProfiles : getAgencyDataModel().getList()) {
				if (pmOrgProfiles.isSelectedCheckBox()) {
					getManagedBean().setButtonHideActive(false);
					return;
				}
			}

			if ((boolean) e.getComponent().getAttributes().get("value")) {
				getManagedBean().setButtonHideActive(false);
			} else {
				getManagedBean().setButtonHideActive(true);
			}	
		}
	}

	/**
	 * Get Full Delivery Address based on CtAddress
	 * 
	 * @param ctAddress   an object of contract address
	 * @return full delivery address
	 */
	public String getFullDeliveryAddress(CtAddress ctAddress) {
		String separator = ", ";
		StringBuilder address = new StringBuilder();
		if (ctAddress != null) {
			if (StringUtils.isNotBlank(
					ctAddress.getAddressName())) {
				address.append(ctAddress.getAddressName());
				address.append(separator);
			}
			address.append(getFullAddress(ctAddress));
		}
		return address.toString();
	}

	/**
	 * Function to check if bond required for contract
	 * 
	 * @return true, if performance bond is required
	 *         false, if performance bond is not required
	 * */
	public boolean isPerformanceBond(){
		//#24333
		if(getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
						&& (!getManagedBean().isDirectPurchase() || getManagedBean().isNewPk7())
						&& !getManagedBean().getCtContractAmount().isBondExempted()
						&& (!getManagedBean().isCommonView()
								|| getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().
								equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY))){
								
			return  true;
		}
		return false;
	}

	/**
	 * Function to convert date into string for view only
	 * 
	 * @param Date date
	 * @return String date
	 * */
	public String getMonthYears(Date date){
		if(date == null || date.toString() == ""){
			return " ";
		}
		String dates = dateFormat.format(date);
		String monthYear =months.get(String.valueOf(dates.substring(3, 5)))+", "
				+String.valueOf(dates.substring(6, 10));
		return monthYear;
	}

	/**
	 * The method used to check whether bond waiver attachment is uploaded in previous version.
	 * 
	 * @param  attachment     an object of attached file
	 * @return true, if bond waiver attachment is attached
	 *         false, if bond waiver attachment is not attached
	 */
	public boolean disableBondAttachmentBtn(){

		boolean isEqual = false;

		if(getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
				|| getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT)){
			for (CtContractAmount contractAmt : getManagedBean().getContractVer().getCtContract().getLatestContractVer().getCtContractAmounts()){
				if (getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
						&& getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
					getManagedBean().setCtTestBondWaiverAmount(contractAmt);
					break;
				}
			}
			isEqual = getManagedBean().getCtContractAmount().isBondWaived()==getManagedBean().getCtTestBondWaiverAmount().isBondWaived();
		}

		if(getManagedBean().isViewContract()){
			return true;
		}else if(!getManagedBean().getCtContractAmount().isBondWaived() &&
				getManagedBean().getContractVer().getCtContract().getLatestContractVer() == null){
			return true;
		}else if(isEqual){
			return true;
		} else if(!getManagedBean().getCtContractAmount().isBondReq()){
			return true;
		}
		return false;
	}
	
	public List<CtAgreement> filterAgreementList() {
		List<CtAgreement> newCtAgreementList = new ArrayList<CtAgreement>();
		List<CtAgreement> currentCtAgreementList = getManagedBean().getCtAgreementList();
		CtAgreement tempCtAgreement = null;
		int index = 0;

		if(currentCtAgreementList != null && !currentCtAgreementList.isEmpty())
		{
			//currentCtAgreementList is sorted by created date in descending order
			for(index = 0; index < currentCtAgreementList.size(); index++)
			{
				tempCtAgreement = currentCtAgreementList.get(index);
				
				if(tempCtAgreement.getAgreementId() == getManagedBean().getCtContractAmount().getCtAgreement().getAgreementId())
				{
					while(index >= 0)
					{
						//sort agreementList in ascending order starting from the selected Reference File No.
						newCtAgreementList.add(0, currentCtAgreementList.get(index));
						index--;
					}

					break;
				}
			}
			if(newCtAgreementList.isEmpty())
			{ 
				index = currentCtAgreementList.size() - 1;
				while(index >= 0)
				{
					//sort agreementList in ascending order
					newCtAgreementList.add(0, currentCtAgreementList.get(index));
					index--;
				}
			}
	
		}

		return newCtAgreementList;
	}
	
	public String getAgreementLoaPhysicalNo(CtAgreement ctAgreement) {
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = new ContractReqDTO();
		ContractResDTO resDTO = null; 
		
		reqDTO.setCtAgreement(ctAgreement);
		contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
		resDTO = contractSO.retrieveContractVerByAgreement(reqDTO);
		
		return resDTO.getCtContractVer().getCtContractValue().getLoaPhysicalNo();
	}
	
	public Date getAgreementSignedDate(CtAgreement ctAgreement) {
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = new ContractReqDTO();
		ContractResDTO resDTO = null; 
		
		reqDTO.setCtAgreement(ctAgreement);
		contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
		resDTO = contractSO.retrieveContractVerByAgreement(reqDTO);
		
		return resDTO.getCtContractVer().getSignedDate();
	}
	
	public String newProcurement(){
		String procur = null;
		if(getManagedBean().getContractVer().getCtContractItems().get(0).getScRequestItem().getRequestNote() != null){
			procur = getParameterDesc(getManagedBean().getContractVer().getCtContractItems().get(0).getScRequestItem().
					getRequestNote().getRequestNoteDtls().get(0).getProcurementType());
			return procur;
		}
		return null;
	}
	
	public String newJustification(){
		String justifi = null;
		if(getManagedBean().getContractVer().getCtContractItems().get(0).getScRequestItem().getRequestNote() != null){
			justifi = getReasonDesc(getManagedBean().getContractVer().getCtContractItems().get(0).getScRequestItem().
					getRequestNote().getRequestNoteDtls().get(0).getJustificationType());

			return justifi;
		}
		return null;
	}



	public String getLanguage() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.getUserLocale().getLanguage();
	}
	
	public List<CtAttachment> getSupplementalContractLetter(CtAgreement ctAgreement) {
		IAgreementSO agreementSO = null;
		List<CtAttachment> ctAttachments = null;
		agreementSO = (IAgreementSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IAgreementSO.class);
		ContractAgreementReqDTO reqDTO = new ContractAgreementReqDTO();
		ContractAgreementResDTO resDTO = null;
		
		if(ctAgreement != null) 
		{
			if(ctAgreement.getCtAmendment() != null)
			{
				reqDTO.setContractVerId(ctAgreement.getCtAmendment().getCtContractVer().getContractVerId());
				resDTO = agreementSO.retrieveSupplementalContractLetterAttachment(reqDTO);
				ctAttachments = resDTO.getCtAttachments();
			}
		}			
						
		return ctAttachments;
	}
	
	/**
	 * The method used to retrieve the bond amount
	 * in text format.
	 * Putra 24333
	 * @return  bond amount in text format.
	 */
	public String getBondAmountInText() {
		Locale locale = null;
		String lang = new FacesScopes().getUserLocale().getLanguage();
		if (getManagedBean().getContractVer().getCtContract().getLoa().getBondAmt() == null
				|| getManagedBean().getContractVer().getCtContract().getLoa().getBondAmt().doubleValue() == 0) {
			return "";
		}

		if (lang.equalsIgnoreCase(ReportConst.ENG_LANGUAGE_CODE)) {
			locale = new Locale(lang, ReportConst.ENG_COUNTRY_CODE);
			DecimalFormat df = new DecimalFormat("#.00");		
			return NumberWordUtilEn.convert(df.format(
					getManagedBean().getContractVer().getCtContract().getLoa().getBondAmt()));
		} else if (lang.equalsIgnoreCase(ReportConst.MALAY_LANGUAGE_CODE)) {
			locale = new Locale(lang, ReportConst.MALAYSIA_COUNTRY_CODE);
			DecimalFormat df = new DecimalFormat("#.00");
			return NumberWordUtil.convert(df.format(
					getManagedBean().getContractVer().getCtContract().getLoa().getBondAmt()));
		}else{
			return "";
		}
	}

	//#24333 - Start
	/**
	 * The method used to retrieve thecontract period
	 * 
	 * @return new contract period
	 */
	public String calculateBondPeriod() {
		if(getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
				|| getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT)){
			return String.valueOf(getManagedBean().getContractVer().getCtContractValue().getContractPeriod());

		} 
			return String.valueOf(getManagedBean().getContractVer().getCtContract().getLoa().getContractDuration());

	}
	//#24333 - End	
	/**
	 * Function to check if bond required for contract is extend
	 * 
	 * @return true, if performance bond is required contract extend
	 *         false, if performance bond is not required contract extend
	 * */
	
	public boolean isContractExtendOrIsBond() {
		//#24333 - Start
		String currentPeriod = null;
		String extendPeriod = null;
		boolean isContractExtendOrIsBond = false;
		
		if(getManagedBean().getContractVer().getCtContract().getLoa().getContractDuration() != null) {
			currentPeriod = BigDecimal.valueOf(getManagedBean().getContractVer().getCtContract().getLoa().getContractDuration().longValue()).toString();
		}
		if(getManagedBean().getContractVer().getCtContractValue().getContractPeriod() > 0) {
			extendPeriod = Integer.toString(getManagedBean().getContractVer().getCtContractValue().getContractPeriod()); 
		}		
		getManagedBean().setExtendContract(false);
		getManagedBean().setDisableOriginalBond(false);

		if((getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
				|| getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT)
				|| getManagedBean().isViewContract() == true
				)&& getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()){
			isContractExtendOrIsBond = true;
			if(currentPeriod != null && extendPeriod != null) {
				if(!extendPeriod.equals(currentPeriod) && getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()){
					isContractExtendOrIsBond = true;
					getManagedBean().setExtendContract(true);
					getManagedBean().setDisableOriginalBond(true);
				}
			}
		}
		if (getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT)
				&& getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()){
			if(getManagedBean().getContractVer().getCtAmendment() != null && getManagedBean().getContractVer().getCtAmendment().isContractExtend())
			isContractExtendOrIsBond = true;
		}

		return isContractExtendOrIsBond;
		//#24333 - End	
		}
	
	/**
	 * Function to check if bond required for contract status
	 * 
	 * @return true, if performance bond is Pending Amendment With Supplemental Submission
	 *         false, if performance bond is not Pending Amendment With Supplemental Submission
	 * */
	public boolean isPendingSupplementalContractLetter(){
		if(getManagedBean().getContractVer().getStatus().getStatusId() 
				== ContractStatusConst.FA_PENDING_LOA_SUBMISSION.longValue()
				|| getManagedBean().getContractVer().getStatus().getStatusId() 
				== ContractStatusConst.FA_PENDING_LOA_ACKNOWLEDGEMENT.longValue()
				|| getManagedBean().getContractVer().getStatus().getStatusId() 
				== ContractStatusConst.FA_PENDING_LOA_APPROVAL.longValue()
				|| getManagedBean().getContractVer().getStatus().getStatusId() 
				== ContractStatusConst.FA_PENDING_VERIFICATION.longValue()){								
			return true;
		}
		return false;
		
	}
	
	
	/**
	 * The method used to retreive the factoring attachment from the list.
	 */
	@SuppressWarnings("null")
	public List<CtAttachment> retrieveAttachmentList()
	{	
		/*#28868 - basri start*/
		if(!getManagedBean().getContractVer().getCtContract().isFactoringExpired()){
			if (getManagedBean().getContractVer().getCtContract().getCtFactoring() != null) {
				if (getManagedBean().getContractVer().getCtContract().getCtFactoring().getAttachement() != null) {
					return getManagedBean().getContractVer().getCtContract().getCtFactoring().getAttachement();
				}
				else{
					return null;
				}
			}

			else {
				return null;
			}
		}else{
			if (getManagedBean().getContractVer().getCtContract().getCtFactoringExpired() != null) {
				if (getManagedBean().getContractVer().getCtContract().getCtFactoringExpired().getAttachement() != null) {
					return getManagedBean().getContractVer().getCtContract().getCtFactoringExpired().getAttachement();
				}
				else{
					return null;
				}
			}

			else {
				return null;
			}
		}
		/*#28868 - basri end*/
		
	}
	
	/*#21387 - basri start*/
	public SelectItem[] getUsersByAgency() {
		ISearchBean<PmUser> searchBean = contractAdminSearchBean();
		IUserSO uSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		ISearchResultBean<PmUser> resultBean = uSO.retrieveMinistryUserList(searchBean);
		List<PmUser> pmUsers = resultBean.getResultList();
		log.info("Number of Users = " + pmUsers.size());

		SelectItem[] items = getDropdownUsers(pmUsers);
		return items;
	}

	protected ISearchBean<PmUser> contractAdminSearchBean() {
		ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();

		PmOrgProfile pmOrgProfile = this.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile();
		String roleCode = contractAdminRoleCode();
		
		if (null != pmOrgProfile.getPmOrgValidity()) {
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmUserOrgs.pmOrgProfile.pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE
						.value(), pmOrgProfile.getPmOrgValidity().getOrgCode()));

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmUserOrgs.pmOrgProfile."
						+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
						SearchOperator.OPERATOR_EQUALS.value(), retrieveMinistryOrgProfile(
								pmOrgProfile.getOrgProfileId()).getOrgProfileId()));
		}
		if (roleCode != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrgs.pmUserRoles.pmRole.roleCode", SearchOperator.OPERATOR_EQUALS
							.value(), roleCode));
		}

		return searchBean;
	}
	
	
	public boolean isToShow(){
		
		boolean show = false;
		
		if(this.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getStatus().getStatusId().intValue()
				== ContractStatusConst.RA_PENDING_APPROVAL){
			show = true;
		}else{
			show =  this.getManagedBean().getContractVer().getCtContract().isAssignedCtAdminFlag();
		}
		
		return show;
	}

	private String contractAdminRoleCode() {
		String contractType = getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode();
		String roleCode = null;
		if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
			roleCode = RoleCode.MIN_CONTRACT_ADMIN.value();
		} else if (ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType)) {
			roleCode = RoleCode.BPK_CCA.value();
		}
		return roleCode;
	}
	/*#21387 - basri end*/
	
	//#30428 - Start
	/**
	 * The method used to entered new ceiling quantity.
	 * 
	 * @param ctItemCeilingQty   object of CeilingQty item
	 */
	public void onChangeCeilingQty(CtItemCeilingQty ctItemCeilingQty) {

			if(getManagedBean().getSelectedItem().getCtItemCeilingQties().size()>0){
				for (CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
					if(getManagedBean().getSelectedItem().getScRequestItem().getRequestNoteItemId().equals(item.getScRequestItem().getRequestNoteItemId())){

						if (!item.getCtItemCeilingQties().isEmpty()) {						
							for (CtItemCeilingQty ceilingQty : item.getCtItemCeilingQties()) {
								if(ceilingQty.getCtAgency().getCommitmentNo().equalsIgnoreCase(ctItemCeilingQty.getCtAgency().getCommitmentNo())){
									ceilingQty.setCeilingQuantity(ctItemCeilingQty.getCeilingQuantity());
									break;
									}								
								}
							}							
						}
					}
				}
			}
	//#30428 - End
	
	  //#25002 - start
		
		public List<ScAttachment> getApprovalLetter(){
			List<ScAttachment> listAttachments = new ArrayList<>();
			IQTAwardSO awardSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			Long loaid = getManagedBean().getContractVer().getCtContract().getLoa().getLoaId();
			listAttachments = awardSO.retrieveScAttachmentByDocID(loaid,ParameterConst.EP_DOC_LOA);
			return listAttachments;
		}

		public void downloadFile(ScAttachment scAttachment){
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			try {
				loaDetailBackingBean.downloadFile(scAttachment);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//#25002 - end
		
		//Enhancement 29418 - shaqimi
		
		private FacesScopes getFacesScopes() {
			FacesScopes facesScopes = new FacesScopes();
			return facesScopes;
		}
		
		/**
		 * Generate report.
		 *
		 * @param dpObj the dp obj
		 */
		public void generateReportPDF(CtAgreement agreement){
			log.info("generateReportPDF");
			String contractVerId = agreement.getCtAmendment().getCtContractVer().getContractVerId().toString();
			String docType = "test";
			
			Map<String, Map<String, String>> paramMaps = new HashMap<String, Map<String, String>>();
			Map<String, String> paramMap = new HashMap<String, String>();
			String reportName = null;
			
			reportName = ReportConst.CT_AMENDMENT_UPDATE_RPT_AMENDMENT_DETAIL;
			paramMap.put(ReportConst.PARAM_CONTRACT_VER_ID, contractVerId);
			paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
			paramMaps.put(reportName, paramMap);
			
			if(reportName!=null) {
				try {
					generateReportPDF(reportName, paramMap);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				throw new EpException("Report is not available for this document.");
			}
		}
		//Enhancement 29418 - end

}  