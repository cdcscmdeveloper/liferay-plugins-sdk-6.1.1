package my.ep.web.integration.eperunding;

import java.util.Date;

public class ProfBodyDetail {
	private String profBody;
	private String profBodyNo;
	private Date profBodyExpiry;

	public String getProfBody() {
		return profBody;
	}

	public void setProfBody(String profBody) {
		this.profBody = profBody;
	}

	public String getProfBodyNo() {
		return profBodyNo;
	}

	public void setProfBodyNo(String profBodyNo) {
		this.profBodyNo = profBodyNo;
	}

	public Date getProfBodyExpiry() {
		return profBodyExpiry;
	}

	public void setProfBodyExpiry(Date profBodyExpiry) {
		this.profBodyExpiry = profBodyExpiry;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof ProfBodyDetail)
	    {
	    	ProfBodyDetail temp = (ProfBodyDetail) obj;
	        if(this.profBody.equalsIgnoreCase(temp.profBody)&& this.profBodyNo.equalsIgnoreCase(temp.profBodyNo)&& this.profBodyExpiry.equals(temp.profBodyExpiry))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.profBody.hashCode()+this.profBodyNo.hashCode()+this.profBodyExpiry.hashCode());        
	}

}
