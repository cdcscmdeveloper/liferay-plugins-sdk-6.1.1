package my.ep.web.managedbean;

/**
 * 
 * @author saharman
 *
 */
public enum UserTasks {
	SpecificationCommitteeApproval("SpecificationCommitteeApproval"), //25692
	BPKApproval("BPKApproval"),
	CommitteeAppointmentApproval("CommitteeAppointmentApproval"), 
	PublicationApproval("PublicationApproval"), 
	CommitteeAppointmentApprovalEcOcTecFec("CommitteeAppointmentApprovalEcOcTecFec")
	;
	
	
	
	private String theString = "";
	private UserTasks(String str){
		theString = str;
	}
	
	public String toString(){
		return theString;
	}
}

// ***25692
