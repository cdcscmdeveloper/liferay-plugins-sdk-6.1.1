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
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtRptBriefingPaperAttachment;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.approval.ApprovalBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentDefaultFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.BaseManagedBean;
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
	
	public static Logger log = EpLogger.getLogger(SourcingBaseBackingBean.class);
	
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

	protected void uploadAttachment(UploadedFile file, ScAttachment attachment) throws FileNotFoundException, IOException {
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		
		attachment.setFileName(fileName);
		attachment.setFileDesc("File uploded");
		
		populateAttachmentDetails(attachment);
		
		FileOutputStream fos;
		
		fos = new FileOutputStream(attachment.getFilePath());
		fos.write(fileBytes);
		fos.close();
		
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was uploaded successfully.", null);
		
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	protected void populateAttachmentDetails(ScAttachment attachment) throws FileNotFoundException, IOException {
		Timestamp date = new Timestamp(System.currentTimeMillis());
		PmUser createdUser = new FacesScopes().getUserSessionBean();
		
		attachment.setCreatedBy(createdUser.getUserId());
		attachment.setCreatedDate(date);
		attachment.setChangedBy(createdUser.getUserId());
		attachment.setCreatedUser(createdUser);
		
		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder filePath = new StringBuilder(docRepoBaseDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(DateUtils.getLast2DigitOfYear());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(attachment.getDocNo());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(attachment.getDocType());
		
		File theDir = new File(filePath.toString());
		theDir.mkdirs();
		
		AttachmentFormat scFormat = getAttachmentFormat(attachment);
		String formatedFileName = scFormat.format(
				attachment.getFileName(), attachment.getPrefixFileName());
		
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(formatedFileName);
		attachment.setFilePath(filePath.toString());
		
		log.info("=======get File path===="
				+ attachment.getFilePath());
	}
	
	private AttachmentFormat getAttachmentFormat(ScAttachment scAttachment) {
		if (scAttachment instanceof ScLoaReportAttachment ||
			scAttachment instanceof ScQtCheckListAttachment ||
			scAttachment instanceof ScQtProposalChecklistAttachment ||
			scAttachment instanceof ScQtRptBriefingPaperAttachment) {
			
			return new AttachmentDateFormat(); 
		} else {
			
			return new AttachmentDefaultFormat();
		}
	}
	
	/**
	 * determineIcon for displaying image icon
	 */
	public <ATTACHMENT extends ScAttachment> String determineIcon(ATTACHMENT attachment) {
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
	
	public <ATTACHMENT extends ScAttachment> File downloadFile(ATTACHMENT scAttachment) throws IOException {
		if (scAttachment != null && StringUtils.isNotBlank(scAttachment.getFileName())) {
			return downloadFile(scAttachment.getFilePath(), scAttachment.getFileName());	
		} else {
			throw new EpException("Attachment not found.");
		}
	}
	
	public File downloadFile(String filePath, String fileName) throws IOException {
		log.info("Inside downloadFile(..)");
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);

		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
		
		if(filePath!=null) {
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
			while ((bytesRead = input.read( buffer )) != -1){ 
				out.write( buffer, 0, bytesRead ); 
				buffer = new byte[4096]; 
			}
			input.close();	
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
		
		//use to upload back to database
		File file = new File(filePath);
		return file;
	}
	
	protected <ATTACHMENT extends ScAttachment> void downloadAllFiles(String zipFileName, List<ATTACHMENT> scAttachments) throws IOException {
		List<File> files = new ArrayList<File>();
		
		for (ScAttachment scAttachment : scAttachments) {
			if (null != scAttachment && StringUtils.isNotBlank(scAttachment.getFilePath())) {
				files.add(new File(scAttachment.getFilePath()));
			}
		}

		PortletResponse portalresponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + zipFileName + ".zip");

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
	
	public void converthtmlToDocFile(String content, String filePath){
		log.info("Inside converthtmlToDocFile(..)");
		WordprocessingMLPackage wordMLPackage;
	 	try {
	 		wordMLPackage = WordprocessingMLPackage.createPackage();
	 		AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
	 		afiPart.setBinaryData(content.getBytes());
	 		afiPart.setContentType(new ContentType("text/html"));
	 		Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);
	 		 
	 		CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
	 		ac.setId(altChunkRel.getId() );
	 		wordMLPackage.getMainDocumentPart().addObject(ac);
	 		wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html");
	 		wordMLPackage.save(new java.io.File(filePath));
	 	} catch (InvalidFormatException e) {
	 		e.printStackTrace();
	 	}catch (Docx4JException e) {
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
		//	backingBean.retrieveTechnicalProposalList(proposalDateType
			//		.getQtId().longValue());
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
			//populateParentOrgProfile(tenderResDTO.getQtDetail());
			//detailBackingBean.onChangeAction();
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
		baseObject.setChangedBy(user.getUserId());
		baseObject.setCreatedBy(user.getUserId());
		baseObject.setCreatedDate(new Timestamp(System.currentTimeMillis()));
	
	}
	
	public String getMessgeConstant(String outCome){
		String msgCode=null;
		if(outCome.equalsIgnoreCase("SUBMIT")){
			 msgCode=MessageConst.INFO_700005;
		 }else if(outCome.equalsIgnoreCase("REJECT")){
			 msgCode=MessageConst.INFO_700008;
		 }else if(outCome.equalsIgnoreCase("REVISE")){
			 msgCode=MessageConst.INFO_700012;
		 }else if(outCome.equalsIgnoreCase("APPROVE")){
			 msgCode=MessageConst.INFO_700007;
		 }else if(outCome.equalsIgnoreCase("ACCEPT")){
			 msgCode=MessageConst.INFO_700013;
		 }
		return msgCode;
	}
	
	public String retriveUserName(Long userId){
		IUserSO userSo=null;
		String userName="";
		try{
			userSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTo=new UserReqDTO();
			reqDTo.setUserId(userId);
			UserResDTO resDTO=userSo.retrieveUserByUserId(reqDTo);
			if(resDTO.isSuccessful()==true){
			PmUser user=resDTO.getPmUser();
			userName=user.getUserName();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return userName;
	}
	
	protected <PARENT_ENTITY extends BaseEntity, REMARK_CLASS extends ScRemark> List<REMARK_CLASS> initializeRemarks(
			PARENT_ENTITY parent, List<REMARK_CLASS> scRemarks, Class<REMARK_CLASS> clazz) {
		
		try {
			if (scRemarks == null) {
				scRemarks = new ArrayList<REMARK_CLASS>();
			}
			
			FacesScopes facesScopes = new FacesScopes();
			if (scRemarks.size() == 0 || !compareUserName(scRemarks.get(0).getCreatedUser().getLoginId())) {
				REMARK_CLASS remark = clazz.newInstance();
				remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
				remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				remark.setChangedBy(remark.getCreatedBy());
				remark.setCreatedUser(facesScopes.getUserSessionBean());
				remark.setParent(parent);
				
				scRemarks.add(0, remark);
			}
			
			return scRemarks;	
		} catch (InstantiationException ie) {
			throw new EpException(ie.getMessage());
		} catch (IllegalAccessException ia) {
			throw new EpException(ia.getMessage());
		}
	}
	
	protected <REMARK_CLASS extends ScRemark> List<REMARK_CLASS> finalizeRemarks(List<REMARK_CLASS> scRemarks) {
		if (scRemarks != null && scRemarks.size() > 0) {
			ScRemark scRemark = scRemarks.get(0);
			
			if (StringUtils.isBlank(scRemark.getRemark())) {
				scRemarks.remove(0);
			}
		}
		
		return scRemarks;
	}
	
	@SuppressWarnings("unchecked")
	protected <BPM_DATA_TYPE> BPM_DATA_TYPE getBpmTaskDetails(Class<BPM_DATA_TYPE> clazz) {
		FacesScopes facesScopes = new FacesScopes();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(facesScopes.getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		
		String taskId = originalServletRequest.getParameter("taskId");
		
		IBpmSO bpmSO;
		
		try {
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(taskId);
			
			BpmResponseDTO resDTO = bpmSO.getTaskDetails(reqDTO);
			
			this.renewSesionObject();
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setOutComes(resDTO.getAction());
			this.getManagedBean().setCurrentStatusId(Long.valueOf(resDTO.getDocStatus()));
			
			return (BPM_DATA_TYPE)resDTO.getPojo();
			
		} finally {
			bpmSO = null;
		}
	}
	
	protected <ATTACHMENT extends ScAttachment> StreamedContent getAttachmentStreamContent(ATTACHMENT scAttachment, String contentType) throws IOException {
		if (scAttachment != null) {
			File file = new File(scAttachment.getFilePath());
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
	
	protected List<PmParameterDesc> retrieveParametersBySearchBean(String parameterType,ISearchBean<PmParameterDesc> searchBean){
		 return ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(parameterType, searchBean);
		
	}
	   


}