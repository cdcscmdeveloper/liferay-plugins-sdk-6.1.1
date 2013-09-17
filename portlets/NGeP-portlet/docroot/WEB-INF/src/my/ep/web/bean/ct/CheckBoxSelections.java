package my.ep.web.bean.ct;
public class CheckBoxSelections {

	private boolean checkedAgreement;
    private boolean disabledAgreement;

    private boolean checkedGood;
    private boolean disabledGood;
    
    private boolean checkedIGood;
    private boolean disabledIGood;
    
    private boolean checkedMto;
    private boolean disabledMto;
    
    private boolean CheckedConcession;
    private boolean disabledConcession;
    
    private boolean CheckedExempted;
    public boolean isCheckedConcession() {
		return CheckedConcession;
	}





	

	private boolean disabledExempted;
    
    private boolean checkedSemenanjung;
   	private boolean checkedSabah;
    private boolean checkedSarawak;
    
    private boolean checkSpecification;
   
   private boolean terminationWarningLetter;
   private boolean terminationNotice;
   private boolean terminationRejectLetter;
   


	public boolean isTerminationWarningLetter() {
	return terminationWarningLetter;
}





public void setTerminationWarningLetter(boolean terminationWarningLetter) {
	this.terminationWarningLetter = terminationWarningLetter;
}





public boolean isTerminationNotice() {
	return terminationNotice;
}





public void setTerminationNotice(boolean terminationNotice) {
	this.terminationNotice = terminationNotice;
}





public boolean isTerminationRejectLetter() {
	return terminationRejectLetter;
}





public void setTerminationRejectLetter(boolean terminationRejectLetter) {
	this.terminationRejectLetter = terminationRejectLetter;
}





	public CheckBoxSelections(){
    	
    	if(!checkedGood){
    		checkedGood=true;
    		disabledGood=true;
    		
    	} if(!checkedAgreement){
    		checkedAgreement=true;
    		disabledAgreement=true;
    
    	}  if(!checkedMto){
    		checkedMto=true;
    		disabledMto=true;
    	
    	} if(!checkedIGood){
    		checkedIGood=true;
    		disabledIGood=true;
    		
    	}
    	
    	
    	
    }





	public boolean isCheckedAgreement() {
		return checkedAgreement;
	}





	public void setCheckedAgreement(boolean checkedAgreement) {
		this.checkedAgreement = checkedAgreement;
	}





	public boolean isDisabledAgreement() {
		return disabledAgreement;
	}





	public void setDisabledAgreement(boolean disabledAgreement) {
		this.disabledAgreement = disabledAgreement;
	}





	public boolean isCheckedGood() {
		return checkedGood;
	}





	public void setCheckedGood(boolean checkedGood) {
		this.checkedGood = checkedGood;
	}





	public boolean isDisabledGood() {
		return disabledGood;
	}





	public void setDisabledGood(boolean disabledGood) {
		this.disabledGood = disabledGood;
	}





	public boolean isCheckedIGood() {
		return checkedIGood;
	}





	public void setCheckedIGood(boolean checkedIGood) {
		this.checkedIGood = checkedIGood;
	}





	public boolean isDisabledIGood() {
		return disabledIGood;
	}





	public void setDisabledIGood(boolean disabledIGood) {
		this.disabledIGood = disabledIGood;
	}





	public boolean isCheckedMto() {
		return checkedMto;
	}





	public void setCheckedMto(boolean checkedMto) {
		this.checkedMto = checkedMto;
	}





	public boolean isDisabledMto() {
		return disabledMto;
	}





	public void setDisabledMto(boolean disabledMto) {
		this.disabledMto = disabledMto;
	}





	public boolean isCheckedSemenanjung() {
		return checkedSemenanjung;
	}





	public void setCheckedSemenanjung(boolean checkedSemenanjung) {
		this.checkedSemenanjung = checkedSemenanjung;
	}





	public boolean isCheckedSabah() {
		return checkedSabah;
	}





	public void setCheckedSabah(boolean checkedSabah) {
		this.checkedSabah = checkedSabah;
	}





	public boolean isCheckedSarawak() {
		return checkedSarawak;
	}





	public void setCheckedSarawak(boolean checkedSarawak) {
		this.checkedSarawak = checkedSarawak;
	}





	public boolean isCheckSpecification() {
		return checkSpecification;
	}





	public void setCheckSpecification(boolean checkSpecification) {
		this.checkSpecification = checkSpecification;
	}
	
	public void setCheckedConcession(boolean checkedConcession) {
		CheckedConcession = checkedConcession;
	}





	public boolean isDisabledConcession() {
		return disabledConcession;
	}





	public void setDisabledConcession(boolean disabledConcession) {
		this.disabledConcession = disabledConcession;
	}





	public boolean isCheckedExempted() {
		return CheckedExempted;
	}





	public void setCheckedExempted(boolean checkedExempted) {
		CheckedExempted = checkedExempted;
	}





	public boolean isDisabledExempted() {
		return disabledExempted;
	}





	public void setDisabledExempted(boolean disabledExempted) {
		this.disabledExempted = disabledExempted;
	}




    
    
}