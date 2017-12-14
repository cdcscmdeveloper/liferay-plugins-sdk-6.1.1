package my.ep.web.managedbean.ct.customer.service;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.ct.CustomerServiceEntity;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean
@SessionScoped
public class CustomerServiceManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private IPagingDataModel<VCtContract> customerServiceDataModel = new CustomerServicePagingDataModel();
	private String ptjCode;
	private String ptjName;
	private String contractNo;
	private String physicalContactNo;
	private String contractTitle;
	private int searchContractStatus;
	private PmOrgProfile searchPtjProfile;
	private CtContract ctContract;
	private CtContractVer ctContractVer;
	private String kumpulanPtjName;
	private String ministryName;
	private int cotractStatus;
	private int status;
	private List<CustomerServiceEntity> customerServiceEntity;

	public String getPtjCode() {
		return ptjCode;
	}

	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}

	public String getPtjName() {
		return ptjName;
	}

	public void setPtjName(String ptjName) {
		this.ptjName = ptjName;
	}


	public String getContractNo() {
		return contractNo;
	}


	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}


	public String getContractTitle() {
		return contractTitle;
	}


	public void setContractTitle(String contractTitle) {
		this.contractTitle = contractTitle;
	}

	public int getSearchContractStatus() {
		return searchContractStatus;
	}

	public void setSearchContractStatus(int searchContractStatus) {
		this.searchContractStatus = searchContractStatus;
	}

	public IPagingDataModel<VCtContract> getCustomerServiceDataModel() {
		return customerServiceDataModel;
	}

	public void setCustomerServiceDataModel(
			IPagingDataModel<VCtContract> customerServiceDataModel) {
		this.customerServiceDataModel = customerServiceDataModel;
	}

	/**
	 * Pagination for available contract search list
	 */
	private class CustomerServicePagingDataModel extends PagingDataModel<VCtContract> {
		private static final long serialVersionUID = 1L;

		public CustomerServicePagingDataModel() {
			super("contractTbl", false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<VCtContract> pageChangeListener(ISearchBean<VCtContract> searchBean) {
			IContractSO contractSO = null;
			try {

				if (StringUtils.isNotBlank(getContractNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("CONTRACT_NO",SearchOperator.OPERATOR_LIKE.value(), getContractNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getSearchPtjProfile() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("PTJ_ORG_ID", SearchOperator.OPERATOR_EQUALS.value(),getSearchPtjProfile().getOrgProfileId()));

				}if (StringUtils.isNotBlank(getContractTitle())) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("CONTRACT_TITLE", SearchOperator.OPERATOR_EQUALS.value(), getContractTitle()));

				}if (StringUtils.isNotBlank(getPhysicalContactNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("CONTRACT_PHYSICAL_NO",SearchOperator.OPERATOR_LIKE.value(),getPhysicalContactNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}

				if (getSearchContractStatus()> 0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("CONTRACT_STATUS", 
							SearchOperator.OPERATOR_EQUALS.value(), getSearchContractStatus()));
				}
				searchBean.setUseEntityClass(true);

				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setvCtContractSearchBean(searchBean);
				ContractResDTO resDTO = contractSO.searchContractDocument(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getvCtContractResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public PmOrgProfile getSearchPtjProfile() {
		if(searchPtjProfile == null){
			searchPtjProfile = new PmOrgProfile();
		}
		return searchPtjProfile;
	}

	public void setSearchPtjProfile(PmOrgProfile searchPtjProfile) {
		this.searchPtjProfile = searchPtjProfile;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public CtContractVer getCtContractVer() {
		return ctContractVer;
	}

	public void setCtContractVer(CtContractVer ctContractVer) {
		this.ctContractVer = ctContractVer;
	}

	public String getKumpulanPtjName() {
		return kumpulanPtjName;
	}

	public void setKumpulanPtjName(String kumpulanPtjName) {
		this.kumpulanPtjName = kumpulanPtjName;
	}

	public String getMinistryName() {
		return ministryName;
	}

	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCotractStatus() {
		return cotractStatus;
	}

	public void setCotractStatus(int cotractStatus) {
		this.cotractStatus = cotractStatus;
	}

	public List<CustomerServiceEntity> getCustomerServiceEntity() {
		if(customerServiceEntity == null){
			customerServiceEntity = new ArrayList<CustomerServiceEntity>();
		}
		return customerServiceEntity;
	}

	public void setCustomerServiceEntity(
			List<CustomerServiceEntity> customerServiceEntity) {
		this.customerServiceEntity = customerServiceEntity;
	}

	public String getPhysicalContactNo() {
		return physicalContactNo;
	}

	public void setPhysicalContactNo(String physicalContactNo) {
		this.physicalContactNo = physicalContactNo;
	}
}
