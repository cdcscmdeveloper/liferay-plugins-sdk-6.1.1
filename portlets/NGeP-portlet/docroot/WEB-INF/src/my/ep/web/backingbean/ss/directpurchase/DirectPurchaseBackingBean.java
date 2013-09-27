package my.ep.web.backingbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.DualListModel;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.directpurchase.DirectPurchaseManagedBean;

/**
 * 
 * @author Tran Ngoc Hoa
 *
 */
@ManagedBean(name="directPurchaseBackingBean")
@RequestScoped

public class DirectPurchaseBackingBean extends BaseBackingBean<DirectPurchaseManagedBean>{
	//TODO temporary hard code, change it later in the development phase
	private DualListModel<String> approveUseritems;
	
	public DirectPurchaseBackingBean() {

		List<String> citiesSource = new ArrayList<String>();  
        List<String> citiesTarget = new ArrayList<String>();  
          
        citiesSource.add("user1");  
        citiesSource.add("user2");  
        citiesSource.add("user3");  
        citiesSource.add("user4");  
        citiesSource.add("user5");  
          
        approveUseritems = new DualListModel<String>(citiesSource, citiesTarget);
	}

	public DualListModel<String> getApproveUseritems() {
		return approveUseritems;
	}

	public void setApproveUseritems(DualListModel<String> approveUseritems) {
		this.approveUseritems = approveUseritems;
	}
	
}
