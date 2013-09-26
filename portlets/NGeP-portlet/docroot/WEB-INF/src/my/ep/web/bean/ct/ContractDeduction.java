package my.ep.web.bean.ct;

import java.io.Serializable;

public class ContractDeduction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String deductionType;
	private String deductionDesc;
	private String deductionCur;
	private String deductionAmtRecv;
	
	public String getDeductionAmtRecv() {
		return deductionAmtRecv;
	}
	public void setDeductionAmtRecv(String deductionAmtRecv) {
		this.deductionAmtRecv = deductionAmtRecv;
	}
	public String getDeductionCur() {
		return deductionCur;
	}
	public void setDeductionCur(String deductionCur) {
		this.deductionCur = deductionCur;
	}
	public String getDeductionDesc() {
		return deductionDesc;
	}
	public void setDeductionDesc(String deductionDesc) {
		this.deductionDesc = deductionDesc;
	}
	public String getDeductionType() {
		return deductionType;
	}
	public void setDeductionType(String deductionType) {
		this.deductionType = deductionType;
	}
}
