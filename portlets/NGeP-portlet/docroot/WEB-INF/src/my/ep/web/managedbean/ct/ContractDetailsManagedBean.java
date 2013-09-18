package my.ep.web.managedbean.ct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.ct.CreateContractDetails;
import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean(name="contractDetailsManagedBean")
@SessionScoped
public class ContractDetailsManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ManagedProperty(value="#{viewAgreementbeans}")
	private String viewAgreement;
	
    public String getViewAgreement() {
		return viewAgreement;
	}


	public void setViewAgreement(String viewAgreement) {
		this.viewAgreement = viewAgreement;
	}


	private CreateContractDetails createContractDetails;
		
	 public CreateContractDetails getCreateContractDetails() {
			return createContractDetails;
		}


		public void setCreateContractDetails(CreateContractDetails createContractDetails) {
			this.createContractDetails = createContractDetails;
		}
	
}
