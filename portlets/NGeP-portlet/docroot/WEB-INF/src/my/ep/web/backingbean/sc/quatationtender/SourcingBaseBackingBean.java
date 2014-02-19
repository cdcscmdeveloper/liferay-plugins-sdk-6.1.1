/**
 * 
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.approval.ApprovalBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * @author VidyasagarReddy
 * 
 */
public abstract class SourcingBaseBackingBean<T extends BaseManagedBean>
		extends BaseBackingBean<T> {
	/**
	 * Method to retrive orgprofiles based on user id and parent org id
	 * 
	 * @param profileReqDTO
	 * @return
	 */
	public List<PmOrgProfile> retriveOrgProfiles(OrgProfileReqDTO profileReqDTO) {

		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		IQTDetailSO detailSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();

		if ("Proposal Preparation".equalsIgnoreCase(res.getProcessName())) {

			SCSiteVisitProposalDataType proposalDateType = (SCSiteVisitProposalDataType) obj;
			/*tenderReqDTO.setQtId(new Long(proposalDateType.getQtId())
					.longValue());
			QuationTenderResDTO tenderResDTO = detailSO
					.retriveQTDetails(tenderReqDTO);
			
			detailBackingBean.getManagedBean()
					.setQt(tenderResDTO.getQtDetail());*/

			SPDetailBackingBean backingBean = new SPDetailBackingBean();
			backingBean.getManagedBean().setProposalData(proposalDateType);
			// detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
			backingBean.retrieveTechnicalProposalList(proposalDateType.getQtId().longValue());
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(
							fc,
							null,
							"/xhtml/ss/supplierproposal/supplierProposalMain.xhtml"
									+ "?faces-redirect=true");
		}else if ("Service QuotationTender Creation".equalsIgnoreCase(res.getProcessName())) {

			 SCQuotationTenderDataType datatype = (SCQuotationTenderDataType) obj;
			tenderReqDTO.setQtId(new Long(datatype.getQtId())
					.longValue());
			QuationTenderResDTO tenderResDTO = detailSO
					.retriveQTDetails(tenderReqDTO);
			
			detailBackingBean.getManagedBean()
					.setQt(tenderResDTO.getQtDetail());
			detailBackingBean.getManagedBean().setTaskId(taskId);			
			detailBackingBean.getManagedBean().setQuotationTenderDataType(datatype);
			populateParentOrgProfile(tenderResDTO.getQtDetail());
			detailBackingBean.onChangeAction();
			tenderResDTO = detailSO.retriveQTApprovalDetails(tenderReqDTO);
			ApprovalBackingBean approvalBackingBean = new ApprovalBackingBean();
			
			// detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/ss/quatationtender/quatationtendercreation.xhtml" + "?faces-redirect=true");
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
			}
			// Department
			else if (OrganizationType.DEPARTMENT.value().equals(
					parentOrgProfile.getOrgTypeCode().getParameterCode())) {
				scQt.setDepartmentOrgProfile(parentOrgProfile);
			}

			parentOrgProfile = parentOrgProfile.getParentOrgProfile();
		}
	}
}
