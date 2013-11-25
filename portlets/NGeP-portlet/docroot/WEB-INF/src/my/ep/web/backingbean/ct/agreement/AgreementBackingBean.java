package my.ep.web.backingbean.ct.agreement;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtSignParty;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.ct.AgreementDetails;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.ct.agreement.AgreementManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	
	
	public AgreementBackingBean(){
		
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		
		agreementDetails = new AgreementDetails();
		contractListItem = new ContractListItem();
		if(this.getManagedBean().getOutComes()==null) {
			this.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
	}
	
	@PostConstruct
	public void init(){
		
	 if(this.getManagedBean().getAgreement() == null){
			  
		System.out.println("Object initialize in post contruct>>>>>>>");
		this.getManagedBean().setAgreement(new CtAgreement());
		this.getManagedBean().getAgreement().setClauseDocMasterId(new Long(1));
		this.getManagedBean().getAgreement().setApprovedDate(new Date());
		this.getManagedBean().getAgreement().setCtContract(new CtContract());
	
		
		CtSignParty ctSignParty = new CtSignParty();
		ctSignParty.setOfficerIc("897452346523");
		ctSignParty.setOfficerName("Khan");
		ctSignParty.setOfficerPosition("PTJ HEAD");
		ctSignParty.setOfficer(new PmUser());
		ctSignParty.getOfficer().setUserId(new Long(1));
		ctSignParty.setOrgProfile(new PmOrgProfile());
		ctSignParty.getOrgProfile().setOrgProfileId(new Long(1));
		ctSignParty.setSignPartyTypeCode(new PmCode());
		ctSignParty.getSignPartyTypeCode().setCodeId(new Long(1));
		ctSignParty.setCtAgreement(this.getManagedBean().getAgreement());
		this.getManagedBean().getAgreement().getCtSignParties().add(ctSignParty);
	  }
	 
	  if(sourcingFiles == null){
		 sourcingFiles = new ArrayList<SelectItem>();  
		 SelectItem doc = new SelectItem("Scope of Work.docx", "Scope of Work.docx");  
		 SelectItem doc1 = new SelectItem("Floor_map.docx", "Floor_map.docx");  
		 SelectItem doc2 = new SelectItem("work_schedule.docx", "work_schedule.docx");  
		 SelectItem doc3 = new SelectItem("performance_bond.docx", "performance_bond.docx");  
		 sourcingFiles.add(doc);
		 sourcingFiles.add(doc1);
		 sourcingFiles.add(doc2);
		 sourcingFiles.add(doc3);
		 
	  }
	}
	
	public String createAgreement(String action){
	
		IAgreementSO agreementSO = null;
		 ContractAgreementReqDTO reqDTO = null;	
		try{
			
			 reqDTO = new ContractAgreementReqDTO();
		     agreementSO = (IAgreementSO)BusinessDelegate.getInstance().getServiceObject("ct", IAgreementSO.class);
		     this.getManagedBean().getAgreement().setCtAgreementAppendixs(this.getManagedBean().getAppendixList());
		     
		     IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			 ContractReqDTO ctReqDTO = new ContractReqDTO();
			 ctReqDTO.setContractId(this.getManagedBean().getContractId());
			 ContractResDTO ctResDTO = contractSO.retrieveContract(ctReqDTO);
			 this.getManagedBean().getAgreement().setCtContract(ctResDTO.getContract());
			 
			 reqDTO.setAgreement(this.getManagedBean().getAgreement());
			 
			 String token = getManagedBean().getToken();
				if(token==null) {
					IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
					token = bpmSO.authenticate("roslan", "welcome1");
					if(token==null) {
						throw new EpException("BPM token not found");
					}
				}

				reqDTO.setOutcome(action);
				if(action == BpmOutcomeConst.BPM_SAVE){
					  reqDTO.setOutcome("");
			     }
				reqDTO.setToken(token);
				reqDTO.setTaskId(getManagedBean().getTaskId());
			 
				 ContractAgreementResDTO resDTO=  agreementSO.createAgreement(reqDTO);
			 
			  if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Contract Name. : "+this.getManagedBean().getAgreement().getAgreementId()));
					if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
						renewSesionObject();
						return "/xhtml/shared/acknowledge.xhtml";
					}
				} else {
					throw new EpException("Failed.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Contract save failed."));
		
		}finally{
			agreementSO = null;
			reqDTO = null;
		}	
		
		return null;
	}
	
	public void createSupAgreement(String outcome){
		
		IAgreementSO agreementSO = null;
		 ContractAgreementReqDTO reqDTO = null;	
		try{
			
			 reqDTO = new ContractAgreementReqDTO();
		     agreementSO = (IAgreementSO)BusinessDelegate.getInstance().getServiceObject("ct", IAgreementSO.class);
		     this.getManagedBean().getAgreement().setCtAmendment(new CtAmendment());
		     this.getManagedBean().getAgreement().getCtAmendment().setAmendmentId(1L);
		     this.getManagedBean().getAgreement().setCtAgreementAppendixs(this.getManagedBean().getAppendixList());
			 reqDTO.setAgreement(this.getManagedBean().getAgreement());
			 reqDTO.setOutcome(outcome);
			 reqDTO.setTaskId(this.getManagedBean().getTaskId());
			 
			 if(outcome == BpmOutcomeConst.BPM_SAVE){
			    reqDTO.setOutcome("");
			  
			 }else if(outcome == BpmOutcomeConst.BPM_REVISE)
			   reqDTO.setTaskId(this.getManagedBean().getTaskId());
			  
			 agreementSO.createAgreement(reqDTO);
	   
		 }catch(Exception ex){
		   log.error("Exception>>>" +ex);
		
		}finally{
			agreementSO = null;
			reqDTO = null;
		}	 
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
