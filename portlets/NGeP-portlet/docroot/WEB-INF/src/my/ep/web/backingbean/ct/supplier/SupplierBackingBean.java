package my.ep.web.backingbean.ct.supplier;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;



@ManagedBean(name = SupplierBackingBean.FACE_BEAN_NAME)
@RequestScoped
public class SupplierBackingBean extends BaseBackingBean<SupplierManagedBean> {
	
	public static final String FACE_BEAN_NAME = "supplierBackingBean";
	private static final String PROCESS_BANK_PAGE="/xhtml/ct/supplier/changeBankAccount.xhtml";
	private static final String PROCESS_FACTORING_PAGE="/xhtml/ct/supplier/requestOfFactoring.xhtml";
	private static final String PROCESS_TERMINATION_PAGE="/xhtml/ct/supplier/terminationForm.xhtml";
	
	private static boolean menuBank;
	private static boolean menuFactoring;
	private static boolean menuTerminate;
	
    private CheckBoxSelections checkBoxSelections;
	private ContractListItem contractListItem;
	
	
	public boolean isMenuTerminate() {
		return menuTerminate;
	}


	public void setMenuTerminate(boolean menuTerminate) {
		this.menuTerminate = menuTerminate;
	}

	
	public SupplierBackingBean(){
		checkBoxSelections = new CheckBoxSelections();
		contractListItem = new ContractListItem();
	}
	
	
	public ContractListItem getContractListItem() {
		return contractListItem;
	}

	public void setContractListItem(ContractListItem contractListItem) {
		this.contractListItem = contractListItem;
	}

	public CheckBoxSelections getCheckBoxSelections() {
		return checkBoxSelections;
	}

	public void setCheckBoxSelections(CheckBoxSelections checkBoxSelections) {
		this.checkBoxSelections = checkBoxSelections;
	}

	
	
	public boolean isMenuBank() {
		return menuBank;
	}

	public void setMenuBank(boolean menuBank) {
		this.menuBank = menuBank;
	}

	public boolean isMenuFactoring() {
		return menuFactoring;
	}

	public void setMenuFactoring(boolean menuFactoring) {
		this.menuFactoring = menuFactoring;
	}

	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public void flush(){
		menuBank = false;
		menuFactoring = false;
		menuTerminate = false;
	}
	public String gotoBankPage() {	
		flush();
		menuBank = true;
		return PROCESS_BANK_PAGE;
	}
	
	public String gotoFactoringPage() {	
		flush();
		menuFactoring = true;
		return PROCESS_FACTORING_PAGE;
	}
	
	public String gotoTerminationPage() {	
		flush();
		menuTerminate = true;
		return PROCESS_TERMINATION_PAGE;
	}

}
