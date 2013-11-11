package my.ep.web.backingbean.ct.circular.central;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.maintain.IContractAssociationSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.ct.maintain.ContractAssociationReqDTO;
import my.ep.dto.ct.maintain.ContractAssociationResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.ct.circular.central.CentralManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;



@ManagedBean(name = CentralBackingBean.FACE_BEAN_NAME)
@RequestScoped
public class CentralBackingBean extends BaseBackingBean<CentralManagedBean>{

	
	private static Logger log = Logger.getLogger(CentralBackingBean.class);
	public static final String FACE_BEAN_NAME = "centralBackingBean";
	private static final String PROCESS_EDIT_PAGE="/xhtml/ct/circular/shared/sidemenu/updateCentralTab.xhtml";
	private static final String PROCESS_CANCEL_PAGE="/xhtml/ct/circular/shared/sidemenu/cancelCentralTab.xhtml";
	
	private Date date;
	private static boolean viewCircular;
	private static boolean editCircular;
	private static boolean cancelCircular;
	private PickListBean pickListBean = null;
	
	private List<PmUser> dsLst; 
	private CtContract[] selectedContractList;
	private CtCircularVer[] selectedContractCircular;
	
	public CentralBackingBean(){
	   
	   dsLst = new ArrayList<PmUser>();
	   if(this.getManagedBean().getOutComes()==null) {
		   this.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}
	}
	
	@PostConstruct
	public void init() throws ParseException{
		
		if(this.getManagedBean().getCircular() == null){
			this.getManagedBean().setCircular(new CtCircular());
			this.getManagedBean().getCircular().setLatestCircularVer(new CtCircularVer());
			this.getManagedBean().getCircular().getLatestCircularVer().setCircularTypeCode(new PmCode());
			this.getManagedBean().setCircularVarList(new ArrayList<CtCircularVer>());
			
			this.getManagedBean().getCircularVer().setCtCircularContracts(new ArrayList<CtCircularContract>());
			this.getManagedBean().getCircularVer().setCtCancelCircularList(new ArrayList<CtCancelCircular>());
			
			this.getManagedBean().getCircularVer().setCircularNo("CR-2013-0001");
			this.getManagedBean().getCircularVer().setName("Central Circular");
			this.getManagedBean().getCircularVer().setCircularFileRefNo("FR-1234567");
			this.getManagedBean().getCircularVer().setDirectPurchaseLimit(0000.00);
			Date d1=new Date();
		    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    Date date = dateFormat.parse(dateFormat.format(d1));
		    long time = date.getTime();
			this.getManagedBean().getCircularVer().setStartDate(new Timestamp(time));
			this.getManagedBean().getCircularVer().setEndDate(new Timestamp(time));
			this.getManagedBean().getCircularVer().setPeninsular(true);
		}
	}
	
	public void searchContract(){
		
		 log.info("Contract Search starts>>>>");
		 IContractSO contractSO = null;
		
		 try {
			
		      ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
			  SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			  equalFilterBean.setSearchField(this.getManagedBean().getSearchFilterBean().getSearchField());
			  equalFilterBean.setSearchValue(this.getManagedBean().getSearchValue());
			  searchBean.getDataFilterBeans().add(equalFilterBean);

			 		 
			  contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			  ContractReqDTO reqDTO = new ContractReqDTO();
			  reqDTO.setContractSearchBean(searchBean);
			  ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			  this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			  this.getManagedBean().setSelectedIndex(0);

		 } catch (Exception ex) {
			log.error("Exception>>", ex);
		
		 } finally {
			 contractSO = null;
		 }
	}
	
	public void searchCircular(){
		
		ICircularSO circularSO = null;
		CircularReqDTO reqDTO = new CircularReqDTO();

		try{
			
			ISearchBean<CtCircular> searchBean = new SearchBean<CtCircular>();
			SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	
			    if(this.getManagedBean().getCircularVer().getCircularNo()!=null) {
				  equalFilterBean.setSearchField("circularNo");
				  equalFilterBean.setSearchValue(this.getManagedBean().getCircularVer().getCircularNo());
				  searchBean.getDataFilterBeans().add(equalFilterBean);
		    	}
				
			   if(this.getManagedBean().getCircularVer().getName()!=null) {
				  equalFilterBean.setSearchField("name");
				  equalFilterBean.setSearchValue(getManagedBean().getCircularVer().getName());
				  searchBean.getDataFilterBeans().add(equalFilterBean);
			   }
					
				 circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct", ICircularSO.class);
				 reqDTO.setCircularSearchBean(super.createSearchBean(CtCircular.class, "dataTable")); 
				 reqDTO.setCircularSearchBean(searchBean);
				 CircularResDTO resDTO = circularSO.searchCircular(reqDTO);
				 this.getManagedBean().setCircularList(resDTO.getCircularResultBean().getResultList());
				 this.getManagedBean().setSelectedIndex(0);

		 }catch(Exception ex){
			   log.error("Exception>>>" +ex);
			
			 }finally{
				circularSO = null;
			}
		
	}
	
	public void saveCircular(String outcome){
		
		log.info("Circular Save starts>>>>");
		ICircularSO circularSO = null;
		CtCircularContract contractObj = null;
		CircularReqDTO reqDTO = new CircularReqDTO();
		
		try{
			
			for(CtContract contract : this.getManagedBean().getCentralContractList()){
			  	 contractObj = new CtCircularContract();
				 contractObj.setCtContract(contract);
				 contractObj.setCtCircularVer(this.getManagedBean().getCircularVer());
				this.getManagedBean().getCircularVer().getCtCircularContracts().add(contractObj);
			}
			
		    this.getManagedBean().getCircularVer().setCtContactPersons(this.getManagedBean().getContractPerson());
		    
		    reqDTO.setCircular(this.getManagedBean().getCircular());
		    reqDTO.setOutcome(outcome);
		    reqDTO.setTaskId(this.getManagedBean().getTaskId());
		 
		    if(outcome == BpmOutcomeConst.BPM_SAVE){
		      reqDTO.setOutcome("");
		  
		    }else if(outcome == BpmOutcomeConst.BPM_REVISE)
		      reqDTO.setTaskId(this.getManagedBean().getTaskId());
		    
		    circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct", ICircularSO.class);
			
		  //  CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
		//    System.out.println("Save Successful>>>:: "+ resDTO.isSuccessful());
	
		 }catch(Exception ex){
		   log.error("Exception>>>" +ex);
		
		 }finally{
			circularSO = null;
		}
	}

    public void fileUpload(FileUploadEvent event) {
		
		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		log.info("Processed uploaded file " + fileName + " of size:"+ fileBytes.length);
		this.getManagedBean().getCircularVer().setCircularFileName(fileName);
		this.getManagedBean().getCircularVer().setCircularFilePath("C:\\upload");
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
	public void addSearchContract(ActionEvent event){
	  
		for (int i = 0; i < selectedContractList.length; i++) {
		    CtContract ad = selectedContractList[i];
			this.getManagedBean().getCentralContractList().add(ad);
		} selectedContractList=null;
	}

	public void addSearchCircular(ActionEvent event){
		
	    for (int i = 0; i < selectedContractCircular.length; i++) {
			CtCircularVer ad = selectedContractCircular[i];
			this.getManagedBean().getCancelCircularList().add(ad);
		} selectedContractList=null;
	}
	
	public void addNewPerson(){

		log.debug("*****addNewContractPerson*****");
		CtContactPerson person = new CtContactPerson();
	    this.getManagedBean().getContractPerson().add(person);
	}

	public void deletePersonList(){			
		this.getManagedBean().getContractPerson().remove(this.getManagedBean().getDeleteContractPerson());
	}

	public void deleteContractList(){			
		this.getManagedBean().getCentralContractList().remove(this.getManagedBean().getContract());
	}
	
	public List<SelectItem> getSearchOptions() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		selectItems.add(new SelectItem("contractNo","Contract No"));
		selectItems.add(new SelectItem("contractName","Contract Name"));
		return selectItems;
	}

	public List<SelectItem> getCircularSearchOptions() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		selectItems.add(new SelectItem("circularNo","Circular No"));
		selectItems.add(new SelectItem("name","Circular Name"));
	    return selectItems;
	}
	
	public CtContract[] getSelectedContractList() {
		return selectedContractList;
	}

	public void setSelectedContractList(CtContract[] selectedContractList) {
		this.selectedContractList = selectedContractList;
	}

	public CtCircularVer[] getSelectedContractCircular() {
		return selectedContractCircular;
	}

	public void setSelectedContractCircular(CtCircularVer[] selectedContractCircular) {
		this.selectedContractCircular = selectedContractCircular;
	}
	
	public String gotoEditPage() {	
		flush();
		editCircular = true;
		return PROCESS_EDIT_PAGE;
	}

	public String gotoCancelPage() {	
		flush();
		cancelCircular = true;
		return PROCESS_CANCEL_PAGE;
	}

	public void flush(){
		editCircular = false;
		cancelCircular = false;
		viewCircular = true;
	}

    

	public  PickListBean getPickListBean() {
		 pickListBean = new PickListBean(dsLst);
		return pickListBean;

	}


	public void setPickListBean(PickListBean pickListBean) {
		this.pickListBean = pickListBean;
	}

public void onCellEdit(CellEditEvent event) {  
    Object oldValue = event.getOldValue();  
    Object newValue = event.getNewValue();  
      
    if(newValue != null && !newValue.equals(oldValue)) {  
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  
}  








public Date getDate() {
	return date;
}
public void setDate(Date date) {
	this.date = date;
}

public boolean isViewCircular() {
	return viewCircular;
}
public void setViewCircular(boolean viewCircular) {
	this.viewCircular = viewCircular;
}
public boolean isEditCircular() {
	return editCircular;
}
public void setEditCircular(boolean editCircular) {
	this.editCircular = editCircular;
}
public boolean isCancelCircular() {
	return cancelCircular;
}
public void setCancelCircular(boolean cancelCircular) {
	this.cancelCircular = cancelCircular;
}

	
}