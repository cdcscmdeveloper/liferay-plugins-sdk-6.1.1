package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.persistence.entity.ss.directpurchase.ScPIDItem;
import my.ep.persistence.entity.ss.directpurchase.ScPidSupplierItem;
import my.ep.persistence.entity.ss.directpurchase.ScPurchaseInDefault;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="purchaseInDefaultMB")
@SessionScoped
public class PurchaseInDefaultManagedBean extends BaseManagedBean{
	private static Logger log = Logger.getLogger(PurchaseInDefaultManagedBean.class);
	private	ScPurchaseInDefault	purchaseInDefault;
	private	List<ScPIDItem>	selectedPidItems	=	new ArrayList<ScPIDItem>();
	private	List<ScPidSupplierItem>	supplierItems	=	new ArrayList<ScPidSupplierItem>();
	public ScPurchaseInDefault getPurchaseInDefault() {
		return purchaseInDefault;
	}
	public void setPurchaseInDefault(ScPurchaseInDefault purchaseInDefault) {
		this.purchaseInDefault = purchaseInDefault;
	}
	public List<ScPIDItem> getSelectedPidItems() {
		return selectedPidItems;
	}
	public void setSelectedPidItems(List<ScPIDItem> selectedPidItems) {
		this.selectedPidItems = selectedPidItems;
	}
	public List<ScPidSupplierItem> getSupplierItems() {
		return supplierItems;
	}
	public void setSupplierItems(List<ScPidSupplierItem> supplierItems) {
		this.supplierItems = supplierItems;
	}
}
