/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.agreement;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.agreement.CTAgreementDataType;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementAppendix;
import my.ep.persistence.entity.ct.agreement.CtSignParty;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.ct.AgreementDetails;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.agreement.AgreementManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Agreement
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean(name="agreementBackingBean")
@RequestScoped
public class AgreementBackingBean extends BaseBackingBean<AgreementManagedBean> {

	private static Logger log = Logger.getLogger(AgreementBackingBean.class);

	private boolean systemDefined;
	private boolean userUploaded;
	private DefaultStreamedContent download;
	private ContractListItem contractListItem;
	private StreamedContent file;
	private boolean supplementValue = true; 
	private AgreementDetails agreementDetails;
	private List<SelectItem> sourcingFiles;  
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public AgreementBackingBean(){
		
		agreementDetails = new AgreementDetails();
		contractListItem = new ContractListItem();
		if(this.getManagedBean().getAction()==null) {
			this.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
	}
	
	@PostConstruct
	public void init(){
		if (sourcingFiles == null) {
			sourcingFiles = new ArrayList<SelectItem>();
			SelectItem doc = new SelectItem("Scope of Work.docx", "Scope of Work.docx");
			SelectItem doc1 = new SelectItem("Floor_map.docx", "Floor_map.docx");
			SelectItem doc2 = new SelectItem("work_schedule.docx","work_schedule.docx");
			SelectItem doc3 = new SelectItem("performance_bond.docx","performance_bond.docx");
			sourcingFiles.add(doc);
			sourcingFiles.add(doc1);
			sourcingFiles.add(doc2);
			sourcingFiles.add(doc3);
		}
		
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		
		if(this.getManagedBean().getAgreement() == null){
			this.getManagedBean().setAgreement(new CtAgreement());
			this.getManagedBean().getAgreement().setCtAgreementAppendixs(new ArrayList<CtAgreementAppendix>());
			this.getManagedBean().getAgreement().setCtSignParties(new ArrayList<CtSignParty>());
			this.getManagedBean().getAgreement().setClauseDocMaster(new TplDocMaster());
			this.getManagedBean().getAgreement().getClauseDocMaster().setDocMasterId(1L);
		}
	}
	
	 public void preRenderView(){
			
			try{	
				System.out.println("**********preRenderView**************");
			//	BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
				HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
				HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
				String taskId = originalServletRequest.getParameter("taskId");
				System.out.println("taskId:"+taskId);

				FacesScopes face = new FacesScopes();
				String token =face.getBPMAuthenticationCtxToken();
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

				BpmRequestDTO reqDTO = new BpmRequestDTO();


				reqDTO.setCtxToken(token);
				reqDTO.setTaskId(taskId);
				BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
				Object obj = res.getPojo();
				
				ContractReqDTO ctReqDTO = new ContractReqDTO();
				CTAgreementDataType dataType = (CTAgreementDataType)obj;
				System.out.println("dataType.getContractId()>>"+dataType.getContractId());
				ctReqDTO.setContractId(new Long(dataType.getContractId()));
				
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
				ContractResDTO resDTO = contractSO.retrieveContract(ctReqDTO);
				this.getManagedBean().setCtContract(resDTO.getContract());
				
				if(dataType.getAgreementId() != 0){
				      ContractAgreementReqDTO agReqDTO = new ContractAgreementReqDTO();
				      System.out.println("dataType.getAgreementId()>>"+dataType.getContractId());
				      agReqDTO.setContractId(new Long(dataType.getContractId()));
				      agReqDTO.setAgreementId(new Long(dataType.getAgreementId().intValue()));
				
				     IAgreementSO agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject("ct",IAgreementSO.class);
				     ContractAgreementResDTO agResDTO = agreementSO.retrieveAgreement(agReqDTO);
				     this.getManagedBean().setAgreement(agResDTO.getAgreement());
				}             
 
				this.getManagedBean().setTaskName(res.getTaskName());
				this.getManagedBean().setTaskId(taskId);
				this.getManagedBean().setAction(res.getAction());
				
				if(res.getProcessName().equalsIgnoreCase("Agreement Creation")){
					
				if(dataType.getAgreementType().equalsIgnoreCase("supplemental")){	
					FacesContext fc = FacesContext.getCurrentInstance();
					this.getManagedBean().setAgreementType("supplemental");
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_SUP_AGREEMENT);
				}else{
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_AGREEMENT);
				}
		      }
			}catch(Exception e){
					e.printStackTrace();
				}

			}
	
	public String createAgreement(String action) {
		
		IAgreementSO agreementSO = null;
		ContractAgreementReqDTO reqDTO = null;
		
		try {
		
			reqDTO = new ContractAgreementReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject("ct", IAgreementSO.class);
			this.getManagedBean().getAgreement().setCtAgreementAppendixs(this.getManagedBean().getAppendixList());

			this.getManagedBean().getAgreement().setCtContract(this.getManagedBean().getCtContract());
			reqDTO.setAgreement(this.getManagedBean().getAgreement());

			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
		    
			ContractAgreementResDTO resDTO = agreementSO.saveAgreement(reqDTO);
			
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully1");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Agreement Name. : "+ this.getManagedBean().getCtContract().getContractId()));
				renewSesionObject();
				return "/xhtml/shared/acknowledge.xhtml";
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Agreement save failed."));
		} finally {
			agreementSO = null;
			reqDTO = null;
		}
		return null;
	}

	public String createSupAgreement(String action) {
		IAgreementSO agreementSO = null;
		ContractAgreementReqDTO reqDTO = null;
		try {
			reqDTO = new ContractAgreementReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject("ct", IAgreementSO.class);
			this.getManagedBean().getAgreement().setCtAmendment(this.getManagedBean().getCtContract().getLatestContractVer().getCtAmendment());
			this.getManagedBean().getAgreement().setCtAgreementAppendixs(this.getManagedBean().getAppendixList());
			this.getManagedBean().getAgreement().setCtContract(this.getManagedBean().getCtContract());
			reqDTO.setAgreement(this.getManagedBean().getAgreement());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setAgreementType(this.getManagedBean().getAgreementType());
		    
			ContractAgreementResDTO resDTO = agreementSO.saveAgreement(reqDTO);
			
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully1");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Agreement Name. : "+ this.getManagedBean().getCtContract().getContractId()));
				renewSesionObject();
				return "/xhtml/shared/acknowledge.xhtml";
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Agreement save failed."));
		} finally {
			agreementSO = null;
			reqDTO = null;
		}
		return null;
	}
	
	public void fileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		log.info("Processed uploaded file " + fileName + " of size:"+ fileBytes.length);
		this.getManagedBean().getAgreement().setFileName(fileName);
		this.getManagedBean().getAgreement().setFilePath("C:\\upload");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"The file was uploaded successfully.", null);
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg); 
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("C:\\upload\\" + fileName);
			fos.write(fileBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
	 }
	
	public void fileApexUpload(FileUploadEvent event) {
	    System.out.println("==============123================================");
		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		log.info("Processed uploaded file " + fileName + " of size:"+ fileBytes.length);
		this.getManagedBean().getAgreementAppendix().setFileName(fileName);
		this.getManagedBean().getAgreementAppendix().setFilePath("C:\\upload");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"The file was uploaded successfully.", null);
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg); 
		FileOutputStream fos;
		try {
			  fos = new FileOutputStream("C:\\upload\\" + fileName);
			  fos.write(fileBytes);
			  fos.close();
		
		} catch (FileNotFoundException e) {
			  e.printStackTrace();
		
		} catch (IOException e) {
			  e.printStackTrace();
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
     }

     public void addAgreementAppendex(ActionEvent event){
	
    	if(this.getManagedBean().getAgreementAppendix() != null){
    		this.getManagedBean().getAgreementAppendix().setFilePath("D://upload");
    		this.getManagedBean().getAgreementAppendix().setFileName("Doc");
	        this.getManagedBean().getAppendixList().add(this.getManagedBean().getAgreementAppendix());
    	}
     }

     public void deleteAgreementAppendix(){
    	 if(this.getManagedBean().getAgreementAppendix() != null)
    		    this.getManagedBean().getAppendixList().remove(this.getManagedBean().getAgreementAppendix());
     }
     
     public List<SelectItem> getAgreementApendex() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("System Defined","System Defined"));
		list.add(new SelectItem("User Uploaded","User Uploaded"));
		return list;
	 }

     private void flush(){
	   systemDefined = false;
	   userUploaded = false;
     }
	
     public void changeApendexType(){  
       try{	
    	   flush();    	
    	   if(this.getManagedBean().getAgreementAppendix().getTitle().equalsIgnoreCase("System Defined")){			
				systemDefined = true;
			}
			else if(this.getManagedBean().getAgreementAppendix().getTitle().equalsIgnoreCase("User Uploaded")){
				userUploaded = true;
			}
	     }catch(Exception e){
    	     System.out.println(e);
         }
      }
	
     public void addSignParty(){
 		CtSignParty signParty = new CtSignParty();
 		this.getManagedBean().getSignPartyList().add(signParty);
 	}
 	
 	public void deleteSignParty(){
 		System.out.println("-------------------inside delete------------");
 		this.getManagedBean().getSignPartyList().remove(this.getManagedBean().getSignParty());
 	}
 	
 	public void addSignSupplier(){
 		CtSignParty signParty = new CtSignParty();
 		this.getManagedBean().getSignSupplierList().add(signParty);
 	}
 	
 	public void deleteSignSupplier(){
 		System.out.println("-------------------inside delete1------------");
 		this.getManagedBean().getSignSupplierList().remove(this.getManagedBean().getSignSupplier());
 	}
 	
	public List<AgreementDetails> getAgreementList() {
		List<AgreementDetails> agreementList = new ArrayList<AgreementDetails>();

		agreementDetails.setAgrementTitle("Agreement Cover Page");
		agreementDetails.setAgrementType("System Defined");
		agreementList.add(agreementDetails);

		AgreementDetails agreementDetails1 = new AgreementDetails();
		agreementDetails1.setAgrementTitle("Agreement Details");
		agreementDetails1.setAgrementType("System Defined");
		agreementList.add(agreementDetails1);

		AgreementDetails agreementDetails2 = new AgreementDetails();
		agreementDetails2.setAgrementTitle("Agreement Clause");
		agreementDetails2.setAgrementType("Template Document");
		agreementList.add(agreementDetails2);

		AgreementDetails agreementDetails3 = new AgreementDetails();
		agreementDetails3.setAgrementTitle("Agreement Signing Page");
		agreementDetails3.setAgrementType("System Defined");
		agreementList.add(agreementDetails3);
		return agreementList;
	}

	public StreamedContent getFile() {
		return file;
	}

	public boolean isSupplementValue() {
		return supplementValue;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public void setSupplementValue(boolean supplementValue) {
		this.supplementValue = supplementValue;
	}

	public void setDownload(DefaultStreamedContent download) {
		this.download = download;
	}

	public DefaultStreamedContent getDownload() throws Exception {
		System.out.println("GET 1024 =>>>>>>>>>>>>>>>>>> " + download.getName());
		return download;
	}

	public boolean isSystemDefined() {
		return systemDefined;
	}

	public void setSystemDefined(boolean systemDefined) {
		this.systemDefined = systemDefined;
	}

	public boolean isUserUploaded() {
		return userUploaded;
	}

	public void setUserUploaded(boolean userUploaded) {
		this.userUploaded = userUploaded;
	}

	public ContractListItem getContractListItem() {
		return contractListItem;
	}

	public void setContractListItem(ContractListItem contractListItem) {
		this.contractListItem = contractListItem;
	}
     
	public AgreementDetails getAgreementDetails() {
		return agreementDetails;
	}
	public void setAgreementDetails(AgreementDetails agreementDetails) {
		this.agreementDetails = agreementDetails;
	}

	public List<SelectItem> getSourcingFiles() {
		return sourcingFiles;
	}

	public void setSourcingFiles(List<SelectItem> sourcingFiles) {
		this.sourcingFiles = sourcingFiles;
	}
	
}
