package my.ep.web.bean.ct;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import my.ep.web.bean.ct.SettingDetails;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

public class ApproverSetting {

	
	/*Used for setting pickuplist*/
	private DualListModel<SettingDetails> settingDetails;  
   	List<SettingDetails> source = new ArrayList<SettingDetails>();  
    List<SettingDetails> target = new ArrayList<SettingDetails>();  
    /*End for setting pickuplist*/
    
    public ApproverSetting(){
    	
    	/*Used for setting pickuplist*/
		 source.add(new SettingDetails("Khan", 10));  
	     source.add(new SettingDetails("Alam", 8));  
	     source.add(new SettingDetails("Kieyun", 7));  
	     source.add(new SettingDetails("Rajesh", 2));  
	     settingDetails = new DualListModel<SettingDetails>(source, target);  
	     /*End for setting pickuplist*/
    }
    
    /*Setting Details Start*/
    public void onTransfer(TransferEvent event) {  
        StringBuilder builder = new StringBuilder();  
        for(Object item : event.getItems()) {  
            builder.append(((SettingDetails) item).getAproverName()).append("<br />");  
        }  
          
        FacesMessage msg = new FacesMessage();  
        msg.setSeverity(FacesMessage.SEVERITY_INFO);  
        msg.setSummary("Items Transferred");  
        msg.setDetail(builder.toString());  
          
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  

    public DualListModel<SettingDetails> getSettingDetails() {
    	return settingDetails;
    }




    public void setSettingDetails(DualListModel<SettingDetails> settingDetails) {
    	this.settingDetails = settingDetails;
    }

}
