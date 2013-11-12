/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum CommitteType implements IEnumType{
	DeskOfficerAdminAssistant("DOA"),
	TechnicalSpecificationCommittee("TSC"),
	OpeningCommittee("OC"),
	EvaluationCommittee("EC"),
	TechnicalEvaluationCommittee("TEC"),
	FinancialEvaluationCommittee("FEC"),
	MinistrySpecificationCommittee("MSC"),
	QCPBPBAMOFSecretary("QCS"),
	QuotationCommittee("QC"),
	ProcurementBoardA("PBA"),
	ProcurementBoardB("PBB"),
	MinistryofFinance("MOF"),
	FCSceretary("FCS");
	
	private String type;
	
	CommitteType(String committeType){
		this.type=committeType;
	}
	@Override
	public String value() {
		// TODO Auto-generated method stub
		return type;
	}

}
