package my.ep.web.backingbean.ct.circular.central;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.CellEditEvent;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.circular.panel.PanelBackingBean;
import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CentralContract;
import my.ep.web.bean.ct.ContractPerson;
import my.ep.web.managedbean.ct.circular.central.CentralManagedBean;



@ManagedBean(name = CentralBackingBean.FACE_BEAN_NAME)
@RequestScoped
public class CentralBackingBean extends BaseBackingBean<CentralManagedBean>{

	
	private static Logger log = Logger.getLogger(PanelBackingBean.class);
	public static final String FACE_BEAN_NAME = "centralBackingBean";
	private static final String PROCESS_EDIT_PAGE="/xhtml/ct/circular/centralcontract/updateCircular.xhtml";
	private static final String PROCESS_CANCEL_PAGE="/xhtml/ct/circular/centralcontract/cancelCircular.xhtml";
	
	private Date date;
	private static boolean editCircular;
	private static boolean cancelCircular;
	private static boolean viewCircular;
	
	
	public CentralBackingBean(){
		
		
	}
	
	public void flush(){
		editCircular = false;
		cancelCircular = false;
		viewCircular = true;
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
	
	
	public void addNewPerson(){

		log.debug("*****addNewContractPerson*****");
		ContractPerson contractPerson = new ContractPerson();
        this.getManagedBean().getContractPerson().add(contractPerson);
	}
	
	public void deletePersonList(){			
		this.getManagedBean().getContractPerson().remove(this.getManagedBean().getDeleteContractPerson());
	}
	
	public void onCellEdit(CellEditEvent event) {  
        Object oldValue = event.getOldValue();  
        Object newValue = event.getNewValue();  
          
        if(newValue != null && !newValue.equals(oldValue)) {  
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);  
            FacesContext.getCurrentInstance().addMessage(null, msg);  
        }  
    }  
	
	
	public List<CentralContract> getCentralContract(){
		List<CentralContract> centralContract = new ArrayList<CentralContract>(); 
		   return centralContract;
	   }
	
	public List<CentralContract> getCentralCircularContract(){
		List<CentralContract> centralCircularContract = new ArrayList<CentralContract>(); 
		   return centralCircularContract;
	   }
	
	
	
}