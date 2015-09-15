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
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.CtWorkflowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.ContractManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean For Contract
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="ctBackingBean")
@RequestScoped
public class ContractBackingBean extends BaseBackingBean<ContractManagedBean> {

	private static Logger log = Logger.getLogger(ContractBackingBean.class);
	
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
			 getManagedBean().setSmAddress(retrieveLatestSupplierAddress());
		}
		else {
			  getManagedBean().setSmAddress(getManagedBean().getCtContractVer().getCtContractValue().getSupplierAddress());
		}
		 getManagedBean().setSupplierAddress(AddressUtils.getFullAddress(getManagedBean().getSmAddress()));
	}
	
	/**
	 * The method used to retrieve the latest supplier address.
     */
	private SmAddress retrieveLatestSupplierAddress() {
		SmAddress smAddress = new SmAddress();
		List<SmCompanyAddress> smCompanyAddresses = new ArrayList<SmCompanyAddress>();
		smCompanyAddresses = this.getManagedBean().getCtContractVer().getCtContract().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses();
		for (SmCompanyAddress address : smCompanyAddresses) {
			if (address.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())) { // C-Company Address, M-Mailing Address
				smAddress = address.getSmAddress();
			}
		}
		return smAddress;
	}
	
	
	/**
	 * The method used to retrieve the company name of supplier.
     */
	public void retrieveCompanyName(AjaxBehaviorEvent event) {
		if (((SelectBooleanCheckbox)event.getSource()).isSelected()) {
			    getManagedBean().setSupplierCompanyName(
					getManagedBean().getCtContractVer().getCtContract().getSmSupplier().getCompanyName());
			
			ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
			SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
            reqDTO.setSmAppl(this.getManagedBean().getCtContractVer().getCtContract().getSmSupplier().getSmAppl());
            SupplierSearchResDTO resDTO = supplierSearchSO.retrieveUpdateSmSupplierBranchFromLatestApplication(reqDTO);			
			if (resDTO.getLatestBranch() != null) {
				this.getManagedBean().setSupplierBranchName(resDTO.getLatestBranch().getBranchName());
			}
		} else {
			  getManagedBean().setSupplierCompanyName(
				getManagedBean().getCtContractVer().getCtContractValue().getSupplierName());
			this.getManagedBean().setSupplierBranchName(
					getManagedBean().getCtContractVer().getCtContractValue().getSupplierBranch());
		}
	}
	
	/**
     * The method used to check whether contract is services with LOA signed 
     * before 01/04/2015.
	 * 
	 * @return true, if contract is services and LOA is signed before 01/04/2015
	 *         false, if contract is not services or LOA is not signed before 01/04/2015
     */
	public boolean chkProcurementTypeService() {		
		try {
			if(getManagedBean().getCtContractVer().getCtContractValue().getProcurementTypeCategory() != null &&
					getManagedBean().getCtContractVer().getCtContractValue().getProcurementTypeCategory().getParameterCode() != null) {
		        	if(getManagedBean().getCtContractVer().getCtContractValue().getProcurementTypeCategory().getParameterCode().equalsIgnoreCase(
				   ParameterConst.PROCUREMENT_TYPE_CODE_SERVICE) && 
				   DateUtils.isDateGreaterOrEqual(DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT,"01/04/2015"), getManagedBean().getCtContractVer().getCtContract().getAckDate())){
			   return true;
                }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return false;
	}
	
	/**
     * The method used to verify the login user is BPK approver 
     * or contract approver.
	 * 
	 * @return true, if login user is BPK approver or contract approver
	 *         false, if login user is not BPK approver or contract approver
     */
    public boolean isApprover() {
    	FacesScopes facesScopes = new FacesScopes();
		return (facesScopes.hasRoleUser(RoleCode.CT_APPROVER.value()) 
				|| facesScopes.hasRoleUser(RoleCode.BPK_APPROVER.value()));
	}
    
    /**
	 * The method used to retrieve the branch info file path
     */
    public void retrieveBranchInfo() {
    	IQTDetailSO qtDetailSO = (IQTDetailSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
    	QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
    	
    	String docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER; // AQ
		if (getManagedBean().getCtContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
	    		ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_DIRECT_PURCHASE; // AD
		}
		reqDTO.setDocCategory(docCategory);
		reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION);
		ScLoaReport scLoaReport = new ScLoaReport();
		scLoaReport.setScLoa(new ScLoa());
		scLoaReport.getScLoa().setLoaId(getManagedBean().getCtContractVer().getCtContract().getLoa().getLoaId());
		reqDTO.setScReport(scLoaReport);

		QuationTenderResDTO resDTO = qtDetailSO.retriveScQtReportByDefKey(reqDTO);
		scLoaReport = (ScLoaReport)resDTO.getScReport();
		
		String filePath = (scLoaReport != null && scLoaReport.getScLoaReportAttachment() != null) ? scLoaReport.getScLoaReportAttachment().getFilePath() : null;
		getManagedBean().setBranchInfoFilePath(filePath);
    }
    
    /**
   	 * The method used to download file
   	 * 
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
}
