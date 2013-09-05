package my.ep.web.backingbean.ss.quatationtender;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.event.RowEditEvent;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ss.quatationtender.QtnTenderBean;
import my.ep.web.managedbean.BaseManagedBean;
import java.io.Serializable;  
import java.util.ArrayList;  
import java.util.List;  
import java.util.UUID;  
  
import javax.faces.context.FacesContext; 
import my.ep.web.managedbean.ss.quationtender.SearchQtnTenderManagedBean;

@ManagedBean(name="searchQtnTenderBB")
@RequestScoped
public class SearchQtnTenderBackingBean extends BaseBackingBean<SearchQtnTenderManagedBean>{

	private boolean checked;

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
	 public void onEdit(RowEditEvent event) {  
	        FacesMessage msg = new FacesMessage("Car Edited", ((QtnTenderBean) event.getObject()).getDeptCode());  
	  
	        FacesContext.getCurrentInstance().addMessage(null, msg);  
	    }  
	      
	    public void onCancel(RowEditEvent event) {  
	        FacesMessage msg = new FacesMessage("Car Cancelled", ((QtnTenderBean) event.getObject()).getDeptCode());  
	  
	        FacesContext.getCurrentInstance().addMessage(null, msg);  
	    }  
	}  
	
	
	

