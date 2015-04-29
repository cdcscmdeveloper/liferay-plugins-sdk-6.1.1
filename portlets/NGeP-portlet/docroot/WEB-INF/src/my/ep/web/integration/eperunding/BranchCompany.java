package my.ep.web.integration.eperunding;

import my.ep.web.integration.phis.MaterialColor;

public class BranchCompany {

	private String mOFNo;

	private Branch branch;

	public String getMOFNo() {
		return mOFNo;
	}

	public void setMOFNo(String mOFNo) {
		this.mOFNo = mOFNo;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof BranchCompany)
	    {
	    	BranchCompany temp = (BranchCompany) obj;
	        if(this.mOFNo.equalsIgnoreCase(temp.mOFNo) && this.branch.getBranchName().equalsIgnoreCase(temp.branch.getBranchName()))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.mOFNo.hashCode()+this.branch.getBranchName().hashCode());        
	}

}
