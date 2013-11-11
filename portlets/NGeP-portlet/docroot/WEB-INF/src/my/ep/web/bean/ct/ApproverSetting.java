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
  
    public DualListModel<SettingDetails> getSettingDetails() {
    	return settingDetails;
    }




    public void setSettingDetails(DualListModel<SettingDetails> settingDetails) {
    	this.settingDetails = settingDetails;
    }

}
