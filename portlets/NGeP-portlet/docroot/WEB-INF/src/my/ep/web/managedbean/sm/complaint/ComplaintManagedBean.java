package my.ep.web.managedbean.sm.complaint;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmComplaint;
import my.ep.persistence.entity.sm.SmComplaintReview;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "complaintMB")
@SessionScoped
public class ComplaintManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private SmComplaint smComplaint;
	private List<SmSupplier> smSupplierLs;
	private SmSupplier selectedSupplier;
	private SmComplaintReview smComplReview;
	private long complaintId;
	private String taskId;
	private String[] bpmAction;
	private String taskName;
	private List<SmComplaint> smComplLs;
	private SmComplaint selectedComplaint;
	
	private PmCountry pmCountry;
	private PmState pmState;
	private PmCity pmCity;

	public SmComplaint getSmComplaint() {
		return smComplaint;
	}

	public void setSmComplaint(SmComplaint smComplaint) {
		this.smComplaint = smComplaint;
	}

	public List<SmSupplier> getSmSupplierLs() {
		return smSupplierLs;
	}

	public void setSmSupplierLs(List<SmSupplier> smSupplierLs) {
		this.smSupplierLs = smSupplierLs;
	}

	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

	public SmComplaintReview getSmComplReview() {
		return smComplReview;
	}

	public void setSmComplReview(SmComplaintReview smComplReview) {
		this.smComplReview = smComplReview;
	}


	public long getComplaintId() {
		return complaintId;
	}

	public void setComplaintId(long complaintId) {
		this.complaintId = complaintId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String[] getBpmAction() {
		return bpmAction;
	}

	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public List<SmComplaint> getSmComplLs() {
		return smComplLs;
	}

	public void setSmComplLs(List<SmComplaint> smComplLs) {
		this.smComplLs = smComplLs;
	}

	public SmComplaint getSelectedComplaint() {
		return selectedComplaint;
	}

	public void setSelectedComplaint(SmComplaint selectedComplaint) {
		this.selectedComplaint = selectedComplaint;
	}

	public PmCountry getPmCountry() {
		return pmCountry;
	}

	public void setPmCountry(PmCountry pmCountry) {
		this.pmCountry = pmCountry;
	}

	public PmState getPmState() {
		return pmState;
	}

	public void setPmState(PmState pmState) {
		this.pmState = pmState;
	}

	public PmCity getPmCity() {
		return pmCity;
	}

	public void setPmCity(PmCity pmCity) {
		this.pmCity = pmCity;
	}
	
}
