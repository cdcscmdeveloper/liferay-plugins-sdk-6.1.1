/**
 * 
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.approval.ApprovalBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * @author VidyasagarReddy
 * 
 */
public abstract class SourcingBaseBackingBean<T extends BaseManagedBean>
		extends BaseBackingBean<T> {
	
	protected static final String DELIMITER_ADDRESS = ",";
	
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

	protected void uploadAttachment(UploadedFile file, ScAttachment attachment) {
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		Timestamp date = new Timestamp(System.currentTimeMillis());

		attachment.setCreatedBy(1L);
		attachment.setCreatedDate(date);
		attachment.setChangedBy(1L);

		attachment.setFileName(fileName);
		attachment.setFileDesc("File uploded");
		File theDir = new File("\\upload\\");

		// if the directory does not exist, create it
		if (!theDir.exists()) {

			theDir.mkdir();
			System.out.println("creating directory: " + theDir);
		}
		attachment.setFilePath(theDir + "\\" + fileName);
		System.out.println("=======get File path===="
				+ attachment.getFilePath());
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(theDir + "\\" + fileName);
			fos.write(fileBytes);
			fos.close();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"The file was uploaded successfully.", null);
			FacesContext.getCurrentInstance().addMessage(null, message);
			
			//FileHelper.getInstance().uploadFile("\\attachment\\sc\\", fileName, null, null, fileBytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected <ATTACHMENT extends ScAttachment>  File downloadFile(ATTACHMENT scAttachment) throws IOException {
		if (scAttachment != null && StringUtils.isNotBlank(scAttachment.getFileName())) {
			return downloadFile(scAttachment.getFilePath(), scAttachment.getFileName());	
		}
		
		return null;
	}
	
	public File downloadFile(String filePath, String fileName) throws IOException {
		System.out.println("Inside downloadFile(..)");
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
				files.add(new File(scAttachment.getFilePath(), scAttachment.getFileName()));
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
		System.out.println("Inside converthtmlToDocFile(..)");
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
	 	System.out.println("Exit converthtmlToDocFile(..)");
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
	
	public String getSupplierAddress(SmAddress pmAddress) {
		StringBuilder address = new StringBuilder();
		
		if (pmAddress != null) {
			String addr = pmAddress.getAddress1();
			
			if (StringUtils.isNotBlank(addr)) {
				address.append(addr);
				address.append(DELIMITER_ADDRESS);
			}
			
			addr = pmAddress.getAddress2();
			if (StringUtils.isNotBlank(addr)) {
				address.append(addr);
				address.append(DELIMITER_ADDRESS);
			}
			
			addr = pmAddress.getAddress3();
			if (StringUtils.isNotBlank(addr)) {
				address.append(addr);
				address.append(DELIMITER_ADDRESS);
			}
			
			addr = pmAddress.getAddress4();
			if (StringUtils.isNotBlank(addr)) {
				address.append(addr);
			}	
		}
		
		return address.toString();
	}
}
