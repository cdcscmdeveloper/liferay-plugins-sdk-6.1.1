package my.ep.web.managedbean.ct.supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean
@SessionScoped
public class SupplierManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date endDate;
	private Date startDate;
	private String loaNo;
	private Long contractNo;
	private String contractName;
	
	private int selectedIndex;
	private SearchFilterBean searchFilterBean = new SearchFilterBean();
	private List<CtContract> contractList = new ArrayList<CtContract>();
	
	public String getLoaNo() {
		return loaNo;
	}
	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Long getContractNo() {
		return contractNo;
	}
	public void setContractNo(Long contractNo) {
		this.contractNo = contractNo;
	}
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public SearchFilterBean getSearchFilterBean() {
		return searchFilterBean;
	}
	public void setSearchFilterBean(SearchFilterBean searchFilterBean) {
		this.searchFilterBean = searchFilterBean;
	}
	public List<CtContract> getContractList() {
		return contractList;
	}
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}
	public int getSelectedIndex() {
		return selectedIndex;
	}
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	

}
