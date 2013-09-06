package my.ep.web.managedbean.fl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
  
import org.primefaces.model.DualListModel;

@ManagedBean(name = "screenDesignListsBean", eager = true)
@RequestScoped
public class ScreenDesignListsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DualListModel<String> approverDetails;

	/**
	 * @return the approverDetails
	 */
	public DualListModel<String> getApproverDetails() {
		return approverDetails;
	}

	/**
	 * @param approverDetails
	 *            the approverDetails to set
	 */
	public void setApproverDetails(DualListModel<String> approverDetails) {
		this.approverDetails = approverDetails;
	}

	public ScreenDesignListsBean() {

		List<String> availableSource = new ArrayList<String>();
		List<String> selectedTarget = new ArrayList<String>();

		availableSource.add("APPROVER DETAIL1");
		availableSource.add("APPROVER DETAIL2");
		availableSource.add("APPROVER DETAIL3");
		availableSource.add("APPROVER DETAIL4");
		availableSource.add("APPROVER DETAIL5");

		approverDetails = new DualListModel<String>(availableSource,
				selectedTarget);
	}

	
}
/*
 * public void onTransfer(TransferEvent event) { StringBuilder builder = new
 * StringBuilder(); for(Object item : event.getItems()) {
 * builder.append(((Player) item).getName()).append("<br />"); }
 */

// FacesMessage msg = new FacesMessage();
// msg.setSeverity(FacesMessage.SEVERITY_INFO);
// msg.setSummary("Items Transferred");
// msg.setDetail(builder.toString());

// FacesContext.getCurrentInstance().addMessage(null, msg);

