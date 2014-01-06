package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ss.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.ss.directpurchase.ScRequestItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name="purchaseInDefaultMB")
@SessionScoped
public class PurchaseInDefaultManagedBean extends BaseManagedBean{
	private static Logger log = Logger.getLogger(PurchaseInDefaultManagedBean.class);
	private	ScPurchaseInDefault	purchaseInDefault;
	private	List<ScRequestItem>	selectedPidItems	=	new ArrayList<ScRequestItem>();
	private	List<ScRequestSupplierItem>	supplierItems	=	new ArrayList<ScRequestSupplierItem>();
	public ScPurchaseInDefault getPurchaseInDefault() {
		return purchaseInDefault;
	}
	public void setPurchaseInDefault(ScPurchaseInDefault purchaseInDefault) {
		this.purchaseInDefault = purchaseInDefault;
	}
	public List<ScRequestItem> getSelectedPidItems() {
		return selectedPidItems;
	}
	public void setSelectedPidItems(List<ScRequestItem> selectedPidItems) {
		this.selectedPidItems = selectedPidItems;
	}
	public List<ScRequestSupplierItem> getSupplierItems() {
		return supplierItems;
	}
	public void setSupplierItems(List<ScRequestSupplierItem> supplierItems) {
		this.supplierItems = supplierItems;
	}
}
