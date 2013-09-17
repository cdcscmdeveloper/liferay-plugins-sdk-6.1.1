package my.ep.web.managedbean.ct;

import my.ep.web.bean.ct.CreateContractDetails;
import my.ep.web.managedbean.BaseManagedBean;



public class ContractDetailsManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 private CreateContractDetails createContractDetails;
		
	 public CreateContractDetails getCreateContractDetails() {
			return createContractDetails;
		}


		public void setCreateContractDetails(CreateContractDetails createContractDetails) {
			this.createContractDetails = createContractDetails;
		}
	
}
