package my.ep.web.managedbean;

/**
 * 
 * @author saharman
 * ***25692
 */
public enum UserTasks {
	SpecificationCommitteeApproval("SpecificationCommitteeApproval"),
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