package my.ep.web.managedbean.sm.complaint;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmComplaint;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "complaintMB")
@SessionScoped
public class ComplaintManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private List<SmSupplier> suppliersList = new ArrayList<SmSupplier>();

	private SmSupplier selectedSupplier = new SmSupplier();

	private SmComplaint smComplaint;

	private String searchSupName;

	private String searchSupRegNo;

	public List<SmSupplier> getSuppliersList() {
		return suppliersList;
	}

	public void setSuppliersList(List<SmSupplier> suppliersList) {
		this.suppliersList = suppliersList;
	}

	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

	public SmComplaint getSmComplaint() {
		return smComplaint;
	}

	public void setSmComplaint(SmComplaint smComplaint) {
		this.smComplaint = smComplaint;
	}

	public String getSearchSupName() {
		return searchSupName;
	}

	public void setSearchSupName(String searchSupName) {
		this.searchSupName = searchSupName;
	}

	public String getSearchSupRegNo() {
		return searchSupRegNo;
	}

	public void setSearchSupRegNo(String searchSupRegNo) {
		this.searchSupRegNo = searchSupRegNo;
	}

}
