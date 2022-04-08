/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.yep;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for PurchaseRequestRpt.
 *
 * @author NORIZAN
 * @since 25/5/2017
 */
@ManagedBean(name = "yepRptManagedBean")
@SessionScoped
public class YearEndRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	private List<String> highRoleCodeList = new ArrayList<String>();

	private String reportUrl;
	private String documentType;
	private String PurchaseOrderStatus;
	private String poTitle;
	private String AP58A;
	private String CarryForward;
	private String ClosedManualPayment;
	private String NonActionableTransaction;
	private String FinancialYear;
	private String ReferenceNo;
	private Date ContractFromDate;
	private Date ContractToDate;
	private boolean contractFlag;
	private boolean ap58Flag;
	private boolean cfwdFlag;
	private boolean cmpFlag;
	private boolean natFlag;
	private boolean checkAP58;
	private boolean checkCFwd;
	private boolean checkCMP;
	private boolean checkNAT;

	
	public YearEndRptManagedBean(){}
	
	public List<String> getHighRoleCodeList() {
		if(highRoleCodeList == null){
			setHighRoleCodeList(new ArrayList<String>());
		}
		return highRoleCodeList;
	}
	public void setHighRoleCodeList(List<String> highRoleCodeList) {
		this.highRoleCodeList = highRoleCodeList;
	}

	public String getDocumentType(){
		return documentType;
	}
	
	public void setDocumentType(String documentType){
		this.documentType = documentType;
	}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public String getPurchaseOrderStatus() {
		return PurchaseOrderStatus;
	}

	public void setPurchaseOrderStatus(String purchaseOrderStatus) {
		PurchaseOrderStatus = purchaseOrderStatus;
	}

	public String getPoTitle() {
		return poTitle;
	}

	public void setPoTitle(String poTitle) {
		this.poTitle = poTitle;
	}

	public String getAP58A() {
		return AP58A;
	}

	public void setAP58A(String aP58A) {
		AP58A = aP58A;
	}

	public String getCarryForward() {
		return CarryForward;
	}

	public void setCarryForward(String carryForward) {
		CarryForward = carryForward;
	}

	public String getClosedManualPayment() {
		return ClosedManualPayment;
	}

	public void setClosedManualPayment(String closedManualPayment) {
		ClosedManualPayment = closedManualPayment;
	}

	public String getNonActionableTransaction() {
		return NonActionableTransaction;
	}

	public void setNonActionableTransaction(String nonActionableTransaction) {
		NonActionableTransaction = nonActionableTransaction;
	}

	public String getFinancialYear() {
		return FinancialYear;
	}

	public void setFinancialYear(String financialYear) {
		FinancialYear = financialYear;
	}

	public Date getContractFromDate() {
		return ContractFromDate;
	}

	public void setContractFromDate(Date contractFromDate) {
		ContractFromDate = contractFromDate;
	}

	public Date getContractToDate() {
		return ContractToDate;
	}

	public void setContractToDate(Date contractToDate) {
		ContractToDate = contractToDate;
	}

	public String getReferenceNo() {
		return ReferenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		ReferenceNo = referenceNo;
	}

	public boolean isContractFlag() {
		return contractFlag;
	}

	public void setContractFlag(boolean contractFlag) {
		this.contractFlag = contractFlag;
	}

	public boolean isAp58Flag() {
		return ap58Flag;
	}

	public void setAp58Flag(boolean ap58Flag) {
		this.ap58Flag = ap58Flag;
	}

	public boolean isCfwdFlag() {
		return cfwdFlag;
	}

	public void setCfwdFlag(boolean cfwdFlag) {
		this.cfwdFlag = cfwdFlag;
	}

	public boolean isCmpFlag() {
		return cmpFlag;
	}

	public void setCmpFlag(boolean cmpFlag) {
		this.cmpFlag = cmpFlag;
	}

	public boolean isNatFlag() {
		return natFlag;
	}

	public void setNatFlag(boolean natFlag) {
		this.natFlag = natFlag;
	}

	public boolean isCheckAP58() {
		return checkAP58;
	}

	public void setCheckAP58(boolean checkAP58) {
		this.checkAP58 = checkAP58;
	}
	
	public boolean isCheckCFwd() {
		return checkCFwd;
	}

	public void setCheckCFwd(boolean checkCFwd) {
		this.checkCFwd = checkCFwd;
	}

	public boolean isCheckCMP() {
		return checkCMP;
	}

	public void setCheckCMP(boolean checkCMP) {
		this.checkCMP = checkCMP;
	}

	public boolean isCheckNAT() {
		return checkNAT;
	}

	public void setCheckNAT(boolean checkNAT) {
		this.checkNAT = checkNAT;
	}

	
}
