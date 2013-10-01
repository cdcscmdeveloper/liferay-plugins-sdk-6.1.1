package my.ep.web.backingbean.ct.agreement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.AgreementDetails;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.managedbean.ct.agreement.AgreementManagedBean;


@ManagedBean(name="agreementBackingBean")
@RequestScoped
public class AgreementBackingBean extends BaseBackingBean<AgreementManagedBean> {

	private static Logger log = Logger.getLogger(AgreementBackingBean.class);
	private static boolean menuAgreement;
	
	private AgreementDetails agreementDetails;
	private List<AgreementDetails> agreementList;
	private boolean systemDefined;
	private boolean userUploaded;
	private DefaultStreamedContent download;
	private ContractListItem contractListItem;
	private StreamedContent file;
	private boolean supplementValue = true; 
	private String agreements;

	public AgreementBackingBean() throws FileNotFoundException{
		agreementDetails = new AgreementDetails();
		contractListItem = new ContractListItem();
		menuAgreement = false; 
	    changeFileDownload();
	}
	
	public void changeFileDownload() {
	    File file1 = new File("C:/Users/HP/Desktop/Time1.docx");
	    InputStream input;
		try {
			input = new FileInputStream(file1);
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String ext = getExtension(file1);
			 System.out.println("PREP 1024 =>>>ext>>> " + ext);
		    setDownload(new DefaultStreamedContent(input, ext, file1.getName()));
		    System.out.println("PREP 1024 =>>>>>>>>>>>>>>>>>>>>>> " + file1.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
    	   if(agreementDetails.getAppendexType().equalsIgnoreCase("System Defined")){			
				systemDefined = true;
			}
			else if(agreementDetails.getAppendexType().equalsIgnoreCase("User Uploaded")){
				userUploaded = true;
			}
	     }catch(Exception e){
    	     System.out.println(e);
         }
      }
	
	  public List<AgreementDetails> getAgreementList() {
		agreementList = new ArrayList<AgreementDetails>();
		
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
	
		
	public static String getExtension(File f) {
		    String ext = null;
		    String result = "";
		    String s = f.getName();
		    int i = s.lastIndexOf('.');

		    if (i > 0 &&  i < s.length() - 1) {
		        ext = s.substring(i+1).toLowerCase();
		    }
		    if(ext.matches("doc")) result = "application/msword";
		    else if(ext.matches("docx")) result = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		    else if(ext.matches("xls")) result = "application/vnd.ms-excel";
		    else if(ext.matches("xlsx")) result = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		    else if(ext.matches("odt")) result = "application/vnd.oasis.opendocument.text";
		    else if(ext.matches("ods")) result = "application/vnd.oasis.opendocument.spreadsheet";

		    return result;
	  }
		
	   public StreamedContent getFile() {
		    return file;
		}
		
		public String getAgreements() {
			setAgreements(this.getManagedBean().getAgreement());
			return agreements;
		}

		public void setAgreements(String agreements) {
			this.agreements = agreements;
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

		public boolean isMenuAgreement() {
				return menuAgreement;
		}

		public void setMenuAgreement(boolean menuAgreement) {
				this.menuAgreement = menuAgreement;
		}

        public ContractListItem getContractListItem() {
				return contractListItem;
		}

		public void setContractListItem(ContractListItem contractListItem) {
				this.contractListItem = contractListItem;
		}

		public void setDownload(DefaultStreamedContent download) {
			    this.download = download;
		}

		public DefaultStreamedContent getDownload() throws Exception {
			    System.out.println("GET 1024 =>>>>>>>>>>>>>>>>>> " + download.getName());
			    return download;
		}	
		
		public void setAgreementList(List<AgreementDetails> agreementList) {
			this.agreementList = agreementList;
		}
		public AgreementDetails getAgreementDetails() {
			return agreementDetails;
		}
		public void setAgreementDetails(AgreementDetails agreementDetails) {
			this.agreementDetails = agreementDetails;
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
}
