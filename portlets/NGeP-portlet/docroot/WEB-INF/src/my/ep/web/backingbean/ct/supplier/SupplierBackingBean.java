package my.ep.web.backingbean.ct.supplier;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;



@ManagedBean(name = SupplierBackingBean.FACE_BEAN_NAME)
@RequestScoped
public class SupplierBackingBean extends BaseBackingBean<SupplierManagedBean> {
	
	private static Logger log = Logger.getLogger(SupplierBackingBean.class);
	public static final String FACE_BEAN_NAME = "supplierBackingBean";
	private static final String PROCESS_BANK_PAGE="/xhtml/ct/supplier/bank/tabView.xhtml";
	private static final String PROCESS_FACTORING_PAGE="/xhtml/ct/supplier/factoring/tabView.xhtml";
	private static final String PROCESS_TERMINATION_PAGE="/xhtml/ct/supplier/termination/tabView.xhtml";
	
	private static boolean menuBank;
	private static boolean menuFactoring;
	private static boolean menuTerminate;
	
    private CheckBoxSelections checkBoxSelections;
	private ContractListItem contractListItem;

	public SupplierBackingBean(){
		checkBoxSelections = new CheckBoxSelections();
		contractListItem = new ContractListItem();
	}
	
    public void searchContract() {
		
		ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
		SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	//	SearchFilterBean likeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		if(this.getManagedBean().getLoaNo()!=null) {
		equalFilterBean.setSearchField("qtLoaNo");
		equalFilterBean.setSearchValue(this.getManagedBean().getLoaNo());
		searchBean.getDataFilterBeans().add(equalFilterBean);
		}
		if(this.getManagedBean().getContractName()!=null) {
		equalFilterBean.setSearchField("contractName");
		equalFilterBean.setSearchValue(getManagedBean().getContractName());
		searchBean.getDataFilterBeans().add(equalFilterBean);
		}
		
		 log.info("searchAction starts");
		 IContractSO contractSO = null;
		 try {
			
			
			 
			 contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			 ContractReqDTO reqDTO = new ContractReqDTO();
			// reqDTO.setContractSearchBean(super.createSearchBean(CtContract.class, "supTable")); 
			 reqDTO.setContractSearchBean(searchBean);
			 ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			 this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			 this.getManagedBean().setSelectedIndex(0);

		 } catch (Exception ex) {
			log.error("Exception>>", ex);
		 } finally {
			 contractSO = null;
		 }
	}
	
    public boolean isMenuTerminate() {
		return menuTerminate;
	}


	public void setMenuTerminate(boolean menuTerminate) {
		this.menuTerminate = menuTerminate;
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
		return PROCESS_BANK_PAGE;
	}
	
	public String gotoFactoringPage() {	
		return PROCESS_FACTORING_PAGE;
	}
	
	public String gotoTerminationPage() {	
		return PROCESS_TERMINATION_PAGE;
	}

}
