/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.dtl.IContractZoneSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
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
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.CtWorkflowStatus;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtPidReason;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
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
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.ContractManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

/**
 * Backing Bean For Contract
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="ctBackingBean")
@ViewScoped
public class ContractBackingBean extends BaseBackingBean<ContractManagedBean> {

	private static Logger log = Logger.getLogger(ContractBackingBean.class);
	private CtSchedule[] selectedSchedule;
	private CtProgressivePayment[] selectedProgressive;
	private CtDeductionClause[] selectedDeductionClauses;
	private BigDecimal paidPercentage = new BigDecimal(0);	
	private BigDecimal paidAdvancePercentage = new BigDecimal(0);
	private BigDecimal balanceToPaid = new BigDecimal(0);
	private PmOrgProfile[] selectedPmOrgProfile;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
	private IPagingDataModel<PmOrgProfile> agencyDataModel = new AgencyPagingDataModel();	
	private static final String CENTRAL_CIRCULAR_PAGE = "/xhtml/ct/circular/portal/centralCircularTab.xhtml?faces-redirect=true";
	
	public ContractBackingBean() {
		log.debug("ContractBackingBean.constructor()");
		if (getManagedBean().getFileDirectory() == null) {
			getManagedBean().setFileDirectory(EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT");
		}
	}
	
	/**
	 * Constructor used for specifying the directory of file storage.
	 *
	 * @param fileDirectory  the directory for file storage. If the fileDirectory is null, 
	 *                       default to temporary directory
	 */
	public ContractBackingBean(String fileDirectory) {
		log.debug("ContractBackingBean.constructor(fileDirectory)");
		if (fileDirectory == null) {
			getManagedBean().setFileDirectory(EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT");
		} else {
			getManagedBean().setFileDirectory(fileDirectory);
		}
	}
	
	/**
	 * @deprecated  As of 23/10/2014, replaced by initializeRemarkAndAttachement(CtWorkflowStatus,List<CtRemark>,List<CtAttachment>)
	 */
	@Deprecated
	public void initializeRemarkAndAttachement(List<CtRemark> remarkList, List<CtAttachment> attachmentList) {
		FacesScopes facesScopes = new FacesScopes();
		
		if (remarkList.size() == 0 || !compareUserName(remarkList.get(0).getCreatedUser().getLoginId())) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(facesScopes.getUserSessionBean());
			getManagedBean().setRemarkList(remarkList);
			getManagedBean().getRemarkList().add(0, remark);
		} else {
			getManagedBean().setRemarkList(remarkList);
		}
		getManagedBean().setAttachmentList(attachmentList);
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
		
		if (remarkList.size() == 0 || !ctWorkflowStatusMatch) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(facesScopes.getUserSessionBean());
			getManagedBean().setRemarkList(remarkList);
			getManagedBean().getRemarkList().add(0, remark);
		} else {
			getManagedBean().setRemarkList(remarkList);
		}
		getManagedBean().setAttachmentList(attachmentList);
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
		this.getManagedBean().getAttachmentList().add(attachment);
	}
	
	/**
	 * The method used to download the attached file.
	 * 
	 * @param  attachment   an object of attached file
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadFile(CtAttachment attachment) throws IOException {
		PortletResponse portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getFileName() + "\"");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		
		String filePath = null;
		OutputStream out = res.getOutputStream();
		
		if (attachment.getFilePath() != null) {
			filePath = DocumentRepositoryUtils.getBaseDir()+attachment.getFilePath();
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
			while ((bytesRead = input.read( buffer )) != -1) { 
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096]; 
			}
			input.close();			
		} else {
			/*out.write(filePath.getFileByte()); */
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
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
	public void deleteAttachmentAction(int index) {
		this.getManagedBean().getAttachmentList().remove(index);
	}
	
	/**
	 * The method used to retrieve the address of supplier.
     */
	public void retrieveSupplierAddress(AjaxBehaviorEvent event) {
		if (((SelectBooleanCheckbox)event.getSource()).isSelected()) {
			 getManagedBean().setCtAddress(CtAddressUtil.retrieveSupplierAddress(
					 getManagedBean().getContractVer().getCtContract().getSmSupplier()));
		}
		else {
			  getManagedBean().setCtAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
		}
		 getManagedBean().setSupplierAddress(getFullAddress(getManagedBean().getCtAddress()));
	}	
	
	/**
	 * The method used to retrieve the company name of supplier.
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
			getManagedBean().setSupplierCompanyName(getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			getManagedBean().setSupplierBranchName(getManagedBean().getContractVer().getCtContractValue().getSupplierBranch());
			getManagedBean().setSupplierRegNo(getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
			getManagedBean().setSupplierMofNo(getManagedBean().getContractVer().getCtContractValue().getSupplierMofNo());
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
	 * The method used to set the alert flag when the original contract imposed service tax.
     */
	public void chkServiceTaxAlert() {
		try {
			if (isBeforeGstReliefDueDate()	// currentDate <= 31/3/2017
					&& imposedServiceTaxCondition()	// ack_date < 01/04/2015 & procurement category = Service
					&& isContractWithGst(getManagedBean().getContractVer().getCtContractValue().getGstEffDate())	// supplier gst is effective
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
    	IQTDetailSO qtDetailSO = (IQTDetailSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
    	QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
    	
    	String docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER; // AQ
		if (getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
	    		ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_DIRECT_PURCHASE; // AD
		}
		reqDTO.setDocCategory(docCategory);
		reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION);
		ScLoaReport scLoaReport = new ScLoaReport();
		scLoaReport.setScLoa(new ScLoa());
		scLoaReport.getScLoa().setLoaId(getManagedBean().getContractVer().getCtContract().getLoa().getLoaId());
		reqDTO.setScReport(scLoaReport);

		QuationTenderResDTO resDTO = qtDetailSO.retriveScQtReportByDefKey(reqDTO);
		scLoaReport = (ScLoaReport)resDTO.getScReport();
		
		String filePath = (scLoaReport != null && scLoaReport.getScLoaReportAttachment() != null) ? scLoaReport.getScLoaReportAttachment().getFilePath() : null;
		getManagedBean().setBranchInfoFilePath(filePath);
    }
    
    /**
   	 * The method used to download file 
   	 * @param filePath  the filePath
   	 */
    public void downloadFile(String filePath) throws IOException {
		StringBuilder downloadPath = new StringBuilder();
		downloadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append(
				ContractConst.SPACE_SLASH).append(filePath);
		
		super.downloadFile(downloadPath.toString());
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
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
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
			while ((bytesRead = input.read( buffer )) != -1) { 
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
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
	 * @return selectItems the selection list of start month
	 */
	public List<SelectItem> retrieveStartMonthList(String  tableStartYear) {
		List<SelectItem> selectItems = null;
	   	Map<String, String> months = null;
	    
	   	try {
	   		months = new HashMap<>();
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
	 * @return selectItems the selection list of end month
	 */
	public List<SelectItem> retrieveEndMonthList(String tableEndYear) {
		List<SelectItem> selectItems = null;
		Map<String, String> months = null;
		
		try {
			months = new HashMap<>();
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
   		    ctSchedules.setScheduleNo(getManagedBean().getContractVer().getCtSchedules().size() + 1);
   		    ctSchedules.setScheduleType(new PmParameter());
   		    getManagedBean().getContractVer().getCtSchedules().add(ctSchedules);	
   	   }
       
       /**
   	 * The method used to delete the selected frequency schedule.
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
   	 * @return the selection list of year
   	 */
   	public List<String> getYearList() {	 
   		if (getManagedBean().getYearList() == null) {
   			getManagedBean().setYearList(new ArrayList<String>());
   		    int startYear = Integer.valueOf(dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()).substring(6, 10));
   		    int endYear = Integer.valueOf(dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()).substring(6, 10));
   		    for (int i = startYear; i <= endYear; i++) {
   		    	getManagedBean().getYearList().add(String.valueOf(i));
   		    }
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
		float addValue = 0.0f;
  	    getManagedBean().setProgSchedButtonDeactive(false);
  	    for (CtProgressivePayment progressivePayment: getManagedBean().getContractVer().getCtProgressivePayments()) {
		    addValue = addValue + progressivePayment.getPercentage();
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
	 * This method used 
	 * to get parameter code using parameter id
	 * @param parameterObj
	 * @return String, parameter code
	 */
	public String getParameterCode(PmParameter parameterObj){
		if(parameterObj == null || parameterObj.getParameterId() == null) {
			  return "Str";
		}
		return ParameterHelper.getInstance().getPmParameterById(parameterObj.getParameterId()).getParameterCode();
	}
	
	/**
	 * This method used to retrieve 
	 * deduction type from database 
	 * @return menu item, List of deduction types
	 */
	public SelectItem[] retrieveParameterDescListByType() {
		
		SelectItem[] items = null;
		 int i = 1;
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>(); ;
		
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
	 * The method used to validate deduction type
	 * User can not selected same type of deduction again
	 */	
	public void validateDeductionType(Long parameteId)  {
		
		   getManagedBean().setSelectedDeduction(null);
		if(getManagedBean().getContractVer().getCtDeductions().size() > 0 ) {
			    int count = 0;
		         for(CtDeduction deId : getManagedBean().getContractVer().getCtDeductions()){
					    if(deId.getDeductionType().getParameterId() != null && parameteId.intValue() == deId.getDeductionType().getParameterId().intValue()){
						    count++;
						    if(count == 2)
							     throw new EpException(MessageConst.ERROR_100046,
							    		 new String[] {getParameterDesc(ParameterHelper.getInstance().getPmParameterById(parameteId))});
				         }
				    }
			  }
	   }
	
	/**
	 * The method used to add new deduction .
	 * This method will generate new record in deduction 
	 */	
	public void addDeduction() {
		
		CtDeduction deduction = new CtDeduction();
		deduction.setDeductionType(new PmParameter());
		this.getManagedBean().getContractVer().getCtDeductions().add(deduction);
	}

	/**
	 * The method used to delete the selected deduction clause.
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
	 * The method used to delete the selected deduction clause.
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

	public CtDeductionClause[] getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	public void setSelectedDeductionClauses(
			CtDeductionClause[] selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}
	
	 /**
     * Method to set boolean value for checkbox 
     * @param pass ajax event type
     * Set boolean value for user action 
     * */
    public void selectOneCheckboxSelect(AjaxBehaviorEvent e){
		
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}
    
    /**
     * Method to set boolean value for checkbox 
     * @param pass ajax event type
     * Set boolean value for user action 
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
		if(getManagedBean().getPaidInvoiceAmt() != null) {
			paidPercentage = (getManagedBean().getPaidInvoiceAmt().multiply(
				BigDecimal.valueOf(100)).divide(
						getManagedBean().getContractVer().getCtContractAmount().getContractAmount(), 2, RoundingMode.HALF_UP));
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
    
    /*Zone start* /
     /**
	 * The method used for creating the zone tree node.
     */
	public void retriveZonesByContractVer() {
		TreeNode rootLocationTree =  new DefaultTreeNode("Root", null);
		TreeNode zoneNode =  null;
	    
		for (CtContractZone zone : this.getManagedBean().getContractVer().getCtContractZones()) {
			if (zone.getDeliveryTermUom() == null) {
				zone.setDeliveryTermUom(new PmParameter()); 
				zone.getDeliveryTermUom().setParameterId(0L);
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
    	if (agencyList.size() > 0) {
			double ceilingAmt = 0.0;
			DecimalFormat df = new DecimalFormat("#.00");
			for (CtAgency ctAgency : agencyList) {
				ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
			}
			ceilingAmt = Double.valueOf(df.format(ceilingAmt));
			
			double latestContractAmount = 0.0;
			for (CtContractAmount contractAmt : getManagedBean().getContractVer().getCtContractAmounts()){
	    		if (getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
	    				&& getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
	    			latestContractAmount = contractAmt.getContractAmount().doubleValue();
	    			latestContractAmount = Double.valueOf(df.format(latestContractAmount));
	    			break;
	    		}
	    	}
			if (ceilingAmt > latestContractAmount) {
				agencyData.setCeilingAmount(BigDecimal.valueOf(0));
				CommonUtils commonUtils = new CommonUtils();
				DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
						CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
				throw new EpException(MessageConst.ERROR_750007,
						new String[] {nf.format(latestContractAmount)}); // Total ceiling value exceeded contract value. Please check your ceiling amount.
			}
    	}
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
			agencyDataModel.resetData();
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
				// Filter By - parameterType
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));
			
				// Filter By - Org Type
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),OrganizationType.DIVISION.value()));
				
				
				// Filter By - Ministry Profile Id
				Long ministryProfileId = getManagedBean().getMinistryProfileId();
				if (ministryProfileId == null) {
					ministryProfileId = 0L;
				}
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ministryProfileId));
				
				
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
				
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgProfileSearchBean(searchBean);
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
		
		
	
	/**
	 * The method used to add new agency's delivery address.
	 * This method will generate new record in delivery address
	 * list. 
	 */	
	public void addDeliveryAddress(List<PmAddressType> addressTypeList) {
		
		try {
			if (addressTypeList != null) {
				List<CtAgencyAddress> ctAgencyAddress = new ArrayList<CtAgencyAddress>();
				List<CtAgencyAddress> agencyAddressList = new ArrayList<CtAgencyAddress>();				
				ctAgencyAddress = getManagedBean().getSelectedAgency().getCtAgencyAddresses();
				
				for (PmAddressType addressType : addressTypeList) {
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
			}
			getManagedBean().setButtonHideActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
		
	/**
	 * The method used to retrieve the condition for construct the LOV listing
	 * @return the condition
	 */
	public String getDeliveryAddressCondition() {
		String condition = "recordStatus^1|addressType^" + AddressType.DELIVERY_ADDRESS.value()
				+ "|pmOrgProfile.orgProfileId^" + this.getManagedBean().getSelectedAgency().getOrgProfile().getOrgProfileId();
		
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
				condition += "|pmAddress.pmState.stateId^" + stateList + "^" + SearchOperator.OPERATOR_IN.value();
			}
			if (!districtList.isEmpty()) {
				condition += "|pmAddress.pmDistrict.districtId^" + districtList + "^" + SearchOperator.OPERATOR_IN.value();
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
			getManagedBean().setButtonHideActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
		
	/**
	 * The method used to delete the selected agency.
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
					}
				}
			}
			this.getManagedBean().setSelectedAgency(null);
			getManagedBean().setButtonHideActive(true);
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
	 * @return the selectedPmOrgProfile
	 */
	public PmOrgProfile[] getSelectedPmOrgProfile() {
		return selectedPmOrgProfile;
	}

	/**
	 * @param selectedPmOrgProfile the selectedPmOrgProfile to set
	 */
	public void setSelectedPmOrgProfile(PmOrgProfile[] selectedPmOrgProfile) {
		this.selectedPmOrgProfile = selectedPmOrgProfile;
	 }
		
	/**
	 * The method used to add a new agency record.
	 * This method will generate a new record in agency
	 * list, user allow to enter the agency details. 
	 */
	public void addAgencyDetails() {
		List<CtAgency> ctAgencyList = new ArrayList<CtAgency>();		
		for (int i = 0; i < selectedPmOrgProfile.length; i++) {
			//Remove if already exist, avoid duplicate
		  	for (CtAgency agency : this.getManagedBean().getContractVer().getCtAgencies()) {
		  		if (agency.getOrgProfile().getOrgProfileId().equals(selectedPmOrgProfile[i].getOrgProfileId())) {
		  			throw new EpException(MessageConst.INFO_758000, new String[]{agency.getOrgProfile().getOrgName()}); 
		  		}
		  	}
			
			CtAgency ctAgency = new CtAgency();
			if (getManagedBean().getContractVer().getCtContractValue().getEffDate().before(DateUtils.getCurrentDateWithoutTime())) {
				ctAgency.setEffDate(DateUtils.getCurrentDateWithoutTime());
			} else {
				ctAgency.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
			}
			ctAgency.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			ctAgency.setCeilingAmount(new BigDecimal(0));
			ctAgency.setChkStatus(true);
			
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(selectedPmOrgProfile[i].getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileDtlByProfileId(reqDTO);
			ctAgency.setOrgProfile(resDTO.getPmOrgProfile());
			 
			if (!retrieveMinistryOrgProfile(getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId().equals(
					retrieveMinistryOrgProfile(ctAgency.getOrgProfile().getOrgProfileId()).getOrgProfileId())) {
				ctAgency.setAp178(true);
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
		if (selectedPmOrgProfile.length>0) {
			this.getManagedBean().getContractVer().getCtAgencies().addAll(ctAgencyList);
		}
		selectedPmOrgProfile = null;
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
		public void agencyFileUpload(FileUploadEvent event) throws Exception {
			AttachmentFormat attachmentFormat = new AttachmentDateFormat();
			
			UploadedFile item = event.getFile();		
			String filePath = getManagedBean().getFileDirectory();
			File file = new File(filePath);
			file.mkdirs();
			filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
			
			FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		    fileOuputStream.write(item.getContents());
		    fileOuputStream.close();
		    
		    CtAgency agency =  getManagedBean().getContractVer().getCtAgencies().get(getManagedBean().getIndex());
		    getManagedBean().getContractVer().getCtAgencies().remove(getManagedBean().getIndex());
		    agency.setFileName(item.getFileName());
		    agency.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		    getManagedBean().getContractVer().getCtAgencies().add(getManagedBean().getIndex(), agency);
		 
		}
		
		/**
		 * The method used to delete the uploaded agency file.
		 */    
	    public void deleteAgencyAttachement(){
	    	CtAgency agency =  getManagedBean().getContractVer().getCtAgencies().get(getManagedBean().getIndex());
	 	    getManagedBean().getContractVer().getCtAgencies().remove(getManagedBean().getIndex());
	 	    agency.setFileName(null);
	 	    agency.setFilePath(null);
	 	    getManagedBean().getContractVer().getCtAgencies().add(getManagedBean().getIndex(), agency);
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
			    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByMinistry(reqDTO);
			    
			    List<PmOrgProfile> list = resDTO.getOrgProfileList();
			    items = new SelectItem[list.size() + 1];
			    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
			    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
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
			    //for (PmOrgProfile orgProfile : list) {
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
		 * calculate over all amount with GST
	     */
		 public void calculateOrverAllAmt(){
		    	if(getManagedBean().getCtContractAmount()!= null) {
		    		BigDecimal gstAmt = BigDecimal.ZERO;
		    		BigDecimal contractAmt = BigDecimal.ZERO;
		    		
		    		if (getManagedBean().getCtContractAmount().getGstAmount() != null)
		    			gstAmt = getManagedBean().getCtContractAmount().getGstAmount();
		    		
		    		if (getManagedBean().getCtContractAmount().getContractAmount() != null)
		    			contractAmt = getManagedBean().getCtContractAmount().getContractAmount();
		    		
		    		getManagedBean().setGstTotalAmount(gstAmt.add(contractAmt));
		    	}
		    	// Recalculate the performance bond amount
		    	if (!getManagedBean().isDirectPurchase() && !getManagedBean().getCtContractAmount().isBondExempted()) {
		    		calculateNewBondAmount();
		    	}		    	
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
			 reqDTO.setFulfilmentType(getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode());
			 
			 ContractResDTO resDTO = contractSO.retrievePerformanceBondAmt(reqDTO);			 
			 getManagedBean().getCtContractAmount().setBondReq(resDTO.isBondReq());
			 if (resDTO.isBondReq()) {				 
				 getManagedBean().getCtContractAmount().setTotalBondAmt(resDTO.getBondAmount());
			 } else {
				 getManagedBean().getCtContractAmount().setTotalBondAmt(null);
			 }
		 }
		 
		 /**
			 * The method used to set the justification value.
		     */
		    public void chkJustificationAction() {
		    	try {
		    		if (this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()) {
		    			this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
		    		}
		    		if (this.getManagedBean().getContractVer().getCtContractValue().isConcession()) {
		    			this.getManagedBean().getContractVer().getCtContractValue().setConcession(true);  
		    			if (!this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()){
		    				this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
		    				this.getManagedBean().setChkJustification(true);
		    			}
		    		}
		    		if (this.getManagedBean().isChkJustification() 
		    				&& !this.getManagedBean().getContractVer().getCtContractValue().isConcession()) {
		    			this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(false);
		    			this.getManagedBean().setChkJustification(false);
					}
		    		if (this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() 
		    				&& this.getManagedBean().getContractVer().getCtContractValue().isConcession()) {
		    			this.getManagedBean().setChkConceValue(true);
					} else {
						this.getManagedBean().setChkConceValue(false);
					}  
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		    
		    /**
			 * The method used to upload AP201 file.
			 * 
			 * @param  event      the upload event for file upload from screen
			 * @throws Exception  if an exception occurred
			 * @see    Exception
			 */
			public void uploadAp201File(FileUploadEvent event) throws Exception {
				UploadedFile item = event.getFile();
				AttachmentFormat attachmentFormat = new AttachmentDateFormat();
					
				String filePath = getManagedBean().getFileDirectory();
				File file = new File(filePath);
				file.mkdirs();
				filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
				FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
			    fileOuputStream.write(item.getContents());
			    fileOuputStream.close();
			    
			    getManagedBean().getContractVer().getCtContractValue().setAp201FileName(item.getFileName());
			    getManagedBean().getContractVer().getCtContractValue().setAp201FilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
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
			}
			
			/**
			 * The method used to select all added performance bond(s).
		     */
			public void selectAllBond(AjaxBehaviorEvent e) {
				
					for (CtPerformanceBond bonds : getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
						if (getManagedBean().isSelectAll()) {
						      bonds.setSelectedCheckBox(true);
					} else{
						bonds.setSelectedCheckBox(false);
					}
				}
				
				if((boolean) e.getComponent().getAttributes().get("value")){
					getManagedBean().setButtonHideActive(false);
				}else{
					getManagedBean().setButtonHideActive(true);
				}	
			}
			
			public void selectAllDeduction(AjaxBehaviorEvent e) {
				
					for (CtDeduction deduct : getManagedBean().getContractVer().getCtDeductions()) {
						if (getManagedBean().isSelectAll()) {
						        deduct.setSelectedCheckBox(true);
					       }else {
					    	   deduct.setSelectedCheckBox(false);
					       }
				}
				
				if((boolean) e.getComponent().getAttributes().get("value")){
					getManagedBean().setButtonHideDedActive(false);
				}else{
					getManagedBean().setButtonHideDedActive(true);
				}	
			}
			
			
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
			
			public void selectAllScheduleDtl(AjaxBehaviorEvent e) {
				
				for (CtSchedule ctSchedules : getManagedBean().getContractVer().getCtSchedules()) {
					if (getManagedBean().isSelectAll()) {
						ctSchedules.setSelectedCheckBox(true);
				       }else {
				    	   ctSchedules.setSelectedCheckBox(false);
				       }
			}
			
			if((boolean) e.getComponent().getAttributes().get("value")){
				getManagedBean().setButtonHideSchActive(false);
			}else{
				getManagedBean().setButtonHideSchActive(true);
			}	
		}
			
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
		
		public void selectAllPidDtl(AjaxBehaviorEvent e) {
			
			for (CtPidReason ctPidReasons : getManagedBean().getContractVer().getCtPidReasons()) {
				if (getManagedBean().isSelectAll()) {
					ctPidReasons.setSelectedCheckBox(true);
			       }else {
			    	   ctPidReasons.setSelectedCheckBox(false);
			       }
		}
		
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}
		
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
		
		public void selectAllProPaymentDtl(AjaxBehaviorEvent e) {
			
			for (CtProgressivePayment ctProgressivePayments : getManagedBean().getContractVer().getCtProgressivePayments()) {
				if (getManagedBean().isSelectAll()) {
					ctProgressivePayments.setSelectedCheckBox(true);
			       }else {
			    	   ctProgressivePayments.setSelectedCheckBox(false);
			       }
		}
		
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideSchActive(false);
		}else{
			getManagedBean().setButtonHideSchActive(true);
		}	
	}
		
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
		        
		     	getManagedBean().getCtContractAmount().getCtPerformanceBonds().add(newBond);
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
			    
			    for (CtPerformanceBond ctPerformanceBond : getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
			    	if (ctPerformanceBond.equals(getManagedBean().getCtPerformanceBond())) {
			    		ctPerformanceBond.setFileName(item.getFileName());
			    		ctPerformanceBond.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
			    	 }
			     }
			}
		    
			/**
			 * The method used to remove the selected performance bond(s).
		     */
			public void removePerformanceBond() {
				try {
					if (getManagedBean().getCtContractAmount().getCtPerformanceBonds() != null) {
						Iterator<CtPerformanceBond> performanceBond = getManagedBean().getCtContractAmount().getCtPerformanceBonds().iterator();
						while (performanceBond.hasNext()) {
							CtPerformanceBond  bond = performanceBond.next();
							if (bond.isSelectedCheckBox()) {
								performanceBond.remove();
							}
						}
					}
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
		    	if (getManagedBean().getSelectedItem() != null) {
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
		    		} else {
		    			getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
		    		}
		    		// retrieve zone label for new price
		    		if (getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)
		    				|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
		    				|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
		    			itemPriceZoneLabelList();
		    			itemMaxQtyZoneLabelList();
		    		}
			  	}
			}
		    
		    /**
			 * The method used to refresh the ceiling quantity information.
			 */
		    private void refreshAgencyCeilingQuantityList() {
		    	Map<Long, CtItemCeilingQty> itemCeilingQtyMap = new HashMap<Long, CtItemCeilingQty>();
		    	for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
		    		itemCeilingQtyMap.put(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId(), ctItemCeilingQty);
		    	}
		    	getManagedBean().getSelectedItem().getCtItemCeilingQties().clear();
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
			 * The method used to indicate the label flag for new price of product item.
			 */
		    private void itemPriceZoneLabelList() {
		    	if (!getManagedBean().getSelectedItem().getCtItemPrices().isEmpty()) {
					for (int temp = 0;temp<getManagedBean().getSelectedItem().getCtItemPrices().size(); temp++) {
						Date date = getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getEffDate();
						int counter =0;
						int index=0;
						int countRow =0;
						for (CtItemPrice itemPrice1 : getManagedBean().getSelectedItem().getCtItemPrices()) {
							countRow++;
							if (getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)
									|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
									|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TARIFF)
									|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
								if (getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getFromQty().equals(itemPrice1.getFromQty()) 
										&& getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getToQty().equals( itemPrice1.getToQty()) 
										&& getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName() != null
										&& getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(itemPrice1.getCtContractZone().getZoneName())) {
									counter++;
									if (counter >= 2) {
										if (date.before(itemPrice1.getEffDate())) {
											index = countRow;
											date = itemPrice1.getEffDate();
										}	
									}
								}
							} else if (getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(itemPrice1.getCtContractZone().getZoneName())) {
								counter++;
								if (counter >= 2) {
									if (date.before(itemPrice1.getEffDate())) {
										index = countRow;
										date = itemPrice1.getEffDate();
									}
								}
							}
						}
						if (index > 0) {
							CtItemPrice priceOfItem = getManagedBean().getSelectedItem().getCtItemPrices().get(index-1);
							priceOfItem.setChangeZoneLabel(true);
		    	          	getManagedBean().getSelectedItem().getCtItemPrices().remove(index-1);
		    	          	getManagedBean().getSelectedItem().getCtItemPrices().add(index-1, priceOfItem);
						}
					}
				}
		    }
		    
	  /**
	    * The method used to indicate the label flag for new max qty of product item.
	    */
	    private void itemMaxQtyZoneLabelList() {
		    if (!getManagedBean().getSelectedItem().getCtItemMaxQties().isEmpty()) {
					for (CtItemMaxQty zonelOfItem : getManagedBean().getSelectedItem().getCtItemMaxQties()) {
						zonelOfItem.setChangeZoneLabel(false);
		    	    }
					for (int temp = 0; temp < getManagedBean().getSelectedItem().getCtItemMaxQties().size(); temp++) {
						Date date = getManagedBean().getSelectedItem().getCtItemMaxQties().get(temp).getEffDate();
						int counter =0;
					 	int index = 0;
					   	int countRow =0;
					   	for (CtItemMaxQty itemMaxQty1 : getManagedBean().getSelectedItem().getCtItemMaxQties()) {
					   		countRow++;
					   		if (getManagedBean().getSelectedItem().getCtItemMaxQties().get(temp).getCtContractZone() != null
					   				&& getManagedBean().getSelectedItem().getCtItemMaxQties().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(
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
					   	CtItemMaxQty maxQtyOfItem = getManagedBean().getSelectedItem().getCtItemMaxQties().get(index-1);
					   	maxQtyOfItem.setChangeZoneLabel(true);
					    getManagedBean().getSelectedItem().getCtItemMaxQties().remove(index-1);
					    getManagedBean().getSelectedItem().getCtItemMaxQties().add(index-1, maxQtyOfItem);
					}
				}
			}
	   }
		    
     /**
	   * The method used to render the selected product item information.
	   */
	  public void changeField() {
		  try {
				  if (getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
						refreshAgencyCeilingQuantityList();
				  }
			} catch (Exception e) {}
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
				for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
					if (ctItemCeilingQty.getCtAgency().isChkStatus()) {
						 activeList.add(ctItemCeilingQty);
					}
				}
			}
			return activeList;
		}
		    
	 /**
	   * The method used for validating the ceiling quantity of items.
	   * A warning message will display on screen when the total ceiling quantity is 
	   * larger than the maximum quantity of the item.
	  */
	  public void validateCeilingQty(CtItemCeilingQty ceilingQtyData) {
		    double totalCeilingQuantity = 0.0;
		    CtContractItem item = this.getManagedBean().getSelectedItem();
			if (item.getCtItemMaxQties().size() != 0) {
				  for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
						totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
				 
				  } if (item.getTotalMaxQuantity().doubleValue() < totalCeilingQuantity) {
						ceilingQtyData.setCeilingQuantity(BigDecimal.valueOf(0));
						throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
				  }
			 }
	   }
		    
	 /**
	   * The method used to indicate the label flag for new price of service item.
	   */
	   public void onSelectServiceItem() {
		   if (getManagedBean().getSelectedServiceItem() != null 
				 && !getManagedBean().getSelectedServiceItem().getCtItemPrices().isEmpty()
				  && getManagedBean().getSelectedServiceItem().getPriceType().getParameterCode().equalsIgnoreCase(
				  ParameterConst.PRICE_TYPE_ZONAL_STANDARD)) {
					  for(CtItemPrice zonelOfItem : getManagedBean().getSelectedServiceItem().getCtItemPrices()) {
						    zonelOfItem.setChangeZoneLabel(false);
		    		  }
					  for(int temp = 0; temp < getManagedBean().getSelectedServiceItem().getCtItemPrices().size(); temp++) {
						  Date date = getManagedBean().getSelectedServiceItem().getCtItemPrices().get(temp).getEffDate();
						  int counter =0;
					 	  int index = 0;
					   	  int countRow =0;
					   	  for(CtItemPrice itemPrice1 : getManagedBean().getSelectedServiceItem().getCtItemPrices()) {
					   		  countRow++;
					   		  if (getManagedBean().getSelectedServiceItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName() != null
					   				&& getManagedBean().getSelectedServiceItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(
					   						itemPrice1.getCtContractZone().getZoneName())) {
					   			     counter++;
					   			     if (counter >= 2) {
					   			    	if(date.before(itemPrice1.getEffDate())) {
					   					    index = countRow;
					   					    date = itemPrice1.getEffDate();
					   				    }
					   			    }
					   		   }
						   }
					 if (index > 0) {
					   	  CtItemPrice priceOfItem = getManagedBean().getSelectedServiceItem().getCtItemPrices().get(index-1);
					   	  priceOfItem.setChangeZoneLabel(true);
					      getManagedBean().getSelectedServiceItem().getCtItemPrices().remove(index-1);
					      getManagedBean().getSelectedServiceItem().getCtItemPrices().add(index-1, priceOfItem);
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
	    * for amendment if used selected other change
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
							    	Date d1 = new Date();
								    String localDate = dateFormat.format(d1);
								    String date = localDate.substring(0, 2);
								    String fromDate = date + "/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
								    String toDate = date + "/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
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
							Date d1 = new Date();
							String localDate = dateFormat.format(d1);
							String date = localDate.substring(0, 2);
							String fromDate = date + "/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
							String toDate = date + "/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
							try {
								schedule.setFromDate(dateFormat.parse(fromDate));
								schedule.setToDate(dateFormat.parse(toDate));
							} catch (ParseException e) {
									log.debug(e.getMessage());
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
	   */
	   public void renderUpdateCheckbox(CtContractVer ctContractVer) {
		   if ((ctContractVer.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
				   || ctContractVer.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT))
				   && getManagedBean().isUpdateContract()) {
			   if (!ctContractVer.getCtContractValue().getSupplierName().equalsIgnoreCase(
					   getManagedBean().getContractVer().getCtContract().getSmSupplier().getCompanyName())) {
				   getManagedBean().setUpdateCompanyName(true);
			   }
			   if (CtAddressUtil.isSupplierInfoChanged(
					   getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl(),
					   ctContractVer.getCtContractValue().getSupplierAddress())) {
				   getManagedBean().setUpdateSupplierAddress(true);
			   }
		   } else {
			   getManagedBean().setUpdateCompanyName(ctContractVer.getCtContractValue().isCompanyNameChange());
			   getManagedBean().setUpdateSupplierAddress(ctContractVer.getCtContractValue().isAddressChange());
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
			  CtPerformanceBond ctBondAttch =  getManagedBean().getCtContractAmount().getCtPerformanceBonds().get(getManagedBean().getIndex());
			  getManagedBean().getCtContractAmount().getCtPerformanceBonds().remove(getManagedBean().getIndex());
			  ctBondAttch.setFileName(null);
			  ctBondAttch.setFilePath(null);
			  getManagedBean().getCtContractAmount().getCtPerformanceBonds().add(getManagedBean().getIndex(), ctBondAttch);
		 }
			    
	 /**
	   * This method used to retrieve 
	   * deduction type from database 
	   * @return menu item, List of deduction types
	   */
	   public SelectItem[] getDropdownPidCodeValues() {
					
            SelectItem[] items = null;
		    ReasonReqDTO reqDTO = null;
		    IReasonSO reasonSO = null;
		    List<PmReasonDesc> list = null;
		    ReasonResDTO resDTO  = null;
		    ISearchBean<PmReasonDesc> searchBean = new SearchBean<PmReasonDesc>(); ;
		    int i = 1;
		    try {
		    	
			        reqDTO = new ReasonReqDTO();
			        FacesScopes facesScopes = new FacesScopes();
				    reqDTO.setLanguage(facesScopes.getUserLocale().getLanguage());
			        reqDTO.setSortBy("pmReason.reasonId");
			        searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.reasonCategoryCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(), ReasonCategory.PID_JUSTIFICATION.value()));
			        searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.pmPIDReasonList.docType", SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.CONTRACT_TYPE_MINISTRY));
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
	   * @param PmReason pmReason
	   * @return void
      */		
	  public void addPurchaseInDefault(){
		    CtPidReason ctPidReasons = new CtPidReason();
			ctPidReasons.setPmReason(new PmReason());
			getManagedBean().getContractVer().getCtPidReasons().add(ctPidReasons);
		}
	 /**
	    * This method used to delete
		* selected PID reason types from datatables
	    * @param List<PmReason> selectedPidReason
	    * @return void
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
	   * @param Long reasonId
	   * @return boolean
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
	   * Route to Central Contract Circular Page. 
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
	   * The method used to verify contract is with GST.
	   * 
	   * @param gstEffDate  the gst effective date
	   * 
	   * @return true, if contract is with GST
	   *         false, if contract is without GST
	   */
	  public boolean isContractWithGst(Date gstEffDate) {
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
		  
		  if (isContractWithGst(getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
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
	     * @param pass ajax event type
	     * Set boolean value for user action 
	     * */
		
		public void onToggleSelect(ToggleSelectEvent event) {
			getManagedBean().setButtonHideActive(false);
		}
		
		public void onRowSelect(SelectEvent event) {
			getManagedBean().setButtonHideActive(false);
		}
	 
	    public void onRowUnselect(UnselectEvent event) {
	    	getManagedBean().setButtonHideActive(true);
	    }

		
		
		/**
		 * The method used for searching the agency.
		 *//*
		public void searchCtitemDetails() {
			try {
				ctItemDataModel.reloadData();
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
			}
		}*/
}
