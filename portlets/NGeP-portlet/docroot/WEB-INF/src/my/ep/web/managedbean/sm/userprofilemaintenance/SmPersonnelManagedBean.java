package my.ep.web.managedbean.sm.userprofilemaintenance;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "smPersonnelMB")
@SessionScoped
public class SmPersonnelManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2739463536376846067L;
	
	private String searchEPRole;
	
	private String searchCompanyRole;
	
	private String searchUsername;
	
	private List<SmPersonnel> personnelList;
	
	private SmPersonnel updateSmPersonnel;
	
	private SmAddress updateSmPersonnelMailingAddress;
	
	private SmAddress updateSmPersonnelPermanentAddress;

	private SmPersonnel editSmPersonnel;
	
	private SmAddress editSmPersonnelMailingAddress;
	
	private SmAddress editSmPersonnelPermanentAddress;
	
	private List<SelectItem> countryList;
	
	private List<SelectItem> stateList;
	
	private List<SelectItem> districtList;
	
	private List<SelectItem> cityList;

	private SmPersonnel newSmPersonnel;
	
	private SmPersonnelAddress newSmPersonnelMailingAddress;
	
	private SmPersonnelAddress newSmPersonnelPermanentAddress;
	
	private List<SelectItem> mailAddCountryList;
	
	private List<SelectItem> mailAddStateList;
	
	private List<SelectItem> mailAddDistrictList;
	
	private List<SelectItem> mailAddCityList;

	private List<SelectItem> permAddCountryList;
	
	private List<SelectItem> permAddStateList;
	
	private List<SelectItem> permAddDistrictList;
	
	private List<SelectItem> permAddCityList;

	private IPagingDataModel<SmPersonnel> smPersonnelDataModel2 = new SmPersonnelPagingDataModel();
	
	private boolean disableRole;
	  
	public SmPersonnelManagedBean() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	private class SmPersonnelPagingDataModel extends PagingDataModel<SmPersonnel> {
		private static final long serialVersionUID = 1L;

		public SmPersonnelPagingDataModel() {
			super("smPersonnelDataModelId");
		}

		@Override
		protected ISearchResultBean<SmPersonnel> pageChangeListener(ISearchBean<SmPersonnel> searchBean) {
			try {
				
				
/*				SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierSearchReqDTO.setUserId(pmUser.getUserId());

				ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
						.getInstance().getServiceObject("sm",
								ISupplierSearchSO.class);
				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierSearchReqDTO);
				SmSupplier smSupp = (SmSupplier) smResDTO.getResultBean().getResultList()
						.get(0);
*/
				searchBean.getDataFilterBeans().add(new SearchFilterBean("smAppl.smSupplier.supplierId",SearchOperator.OPERATOR_EQUALS.value(),getUpdateSmPersonnel().getSmAppl().getSmSupplier().getSupplierId()));
				
				if(getSearchUsername() !=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("name",SearchOperator.OPERATOR_LIKE.value(),getSearchUsername()));
				}

				if(getSearchEPRole() !=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("epRole",SearchOperator.OPERATOR_LIKE.value(),getSearchEPRole()));
				}

				PersonnelReqDTO reqDTO = new PersonnelReqDTO();
				reqDTO.setSearchPersonnelBean(searchBean);

				IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
						.getInstance().getServiceObject("sm", IPersonnelSO.class);
				PersonnelResDTO resDTO = smPersonnelSO.retrievePersonnelBySearchCriteria(reqDTO);

				if(resDTO.isSuccessful()) {
					return resDTO.getSearchPersonnelResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public void searchSmPersonnel(){
		smPersonnelDataModel2.reloadData();
	}

	public String getSearchEPRole() {
		return searchEPRole;
	}

	public void setSearchEPRole(String searchEPRole) {
		this.searchEPRole = searchEPRole;
	}

	public String getSearchCompanyRole() {
		return searchCompanyRole;
	}

	public void setSearchCompanyRole(String searchCompanyRole) {
		this.searchCompanyRole = searchCompanyRole;
	}

	public String getSearchUsername() {
		return searchUsername;
	}

	public void setSearchUsername(String searchUsername) {
		this.searchUsername = searchUsername;
	}

	public List<SmPersonnel> getPersonnelList() {
		if(personnelList == null)
			personnelList = new ArrayList<SmPersonnel>();
		return personnelList;
	}

	public void setPersonnelList(List<SmPersonnel> personnelList) {
		this.personnelList = personnelList;
	}

	public SmPersonnel getUpdateSmPersonnel() {
		return updateSmPersonnel;
	}

	public void setUpdateSmPersonnel(SmPersonnel updateSmPersonnel) {
		this.updateSmPersonnel = updateSmPersonnel;
	}

	public SmAddress getUpdateSmPersonnelMailingAddress() {
		if(updateSmPersonnelMailingAddress == null){
			updateSmPersonnelMailingAddress = new SmAddress();
		}
		return updateSmPersonnelMailingAddress;
	}

	public void setUpdateSmPersonnelMailingAddress(
			SmAddress updateSmPersonnelMailingAddress) {
		this.updateSmPersonnelMailingAddress = updateSmPersonnelMailingAddress;
	}

	public SmAddress getUpdateSmPersonnelPermanentAddress() {
		return updateSmPersonnelPermanentAddress;
	}

	public void setUpdateSmPersonnelPermanentAddress(
			SmAddress updateSmPersonnelPermanentAddress) {
		this.updateSmPersonnelPermanentAddress = updateSmPersonnelPermanentAddress;
	}

	public SmPersonnel getEditSmPersonnel() {
		return editSmPersonnel;
	}

	public void setEditSmPersonnel(SmPersonnel editSmPersonnel) {
		this.editSmPersonnel = editSmPersonnel;
	}

	public SmAddress getEditSmPersonnelMailingAddress() {
		if(editSmPersonnelMailingAddress == null){
			editSmPersonnelMailingAddress = new SmAddress();
		}
		return editSmPersonnelMailingAddress;
	}

	public void setEditSmPersonnelMailingAddress(
			SmAddress editSmPersonnelMailingAddress) {
		this.editSmPersonnelMailingAddress = editSmPersonnelMailingAddress;
	}

	public SmAddress getEditSmPersonnelPermanentAddress() {
		return editSmPersonnelPermanentAddress;
	}

	public void setEditSmPersonnelPermanentAddress(
			SmAddress editSmPersonnelPermanentAddress) {
		this.editSmPersonnelPermanentAddress = editSmPersonnelPermanentAddress;
	}

	public List<SelectItem> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<SelectItem> countryList) {
		this.countryList = countryList;
	}

	public List<SelectItem> getStateList() {
		return stateList;
	}

	public void setStateList(List<SelectItem> stateList) {
		this.stateList = stateList;
	}

	public List<SelectItem> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(List<SelectItem> districtList) {
		this.districtList = districtList;
	}

	public List<SelectItem> getCityList() {
		return cityList;
	}

	public void setCityList(List<SelectItem> cityList) {
		this.cityList = cityList;
	}

	public SmPersonnel getNewSmPersonnel() {
		return newSmPersonnel;
	}

	public void setNewSmPersonnel(SmPersonnel newSmPersonnel) {
		this.newSmPersonnel = newSmPersonnel;
	}

	public SmPersonnelAddress getNewSmPersonnelMailingAddress() {
		return newSmPersonnelMailingAddress;
	}

	public void setNewSmPersonnelMailingAddress(
			SmPersonnelAddress newSmPersonnelMailingAddress) {
		this.newSmPersonnelMailingAddress = newSmPersonnelMailingAddress;
	}

	public SmPersonnelAddress getNewSmPersonnelPermanentAddress() {
		return newSmPersonnelPermanentAddress;
	}

	public void setNewSmPersonnelPermanentAddress(
			SmPersonnelAddress newSmPersonnelPermanentAddress) {
		this.newSmPersonnelPermanentAddress = newSmPersonnelPermanentAddress;
	}

	public List<SelectItem> getMailAddCountryList() {
		return mailAddCountryList;
	}

	public void setMailAddCountryList(List<SelectItem> mailAddCountryList) {
		this.mailAddCountryList = mailAddCountryList;
	}

	public List<SelectItem> getMailAddStateList() {
		return mailAddStateList;
	}

	public void setMailAddStateList(List<SelectItem> mailAddStateList) {
		this.mailAddStateList = mailAddStateList;
	}

	public List<SelectItem> getMailAddDistrictList() {
		return mailAddDistrictList;
	}

	public void setMailAddDistrictList(List<SelectItem> mailAddDistrictList) {
		this.mailAddDistrictList = mailAddDistrictList;
	}

	public List<SelectItem> getMailAddCityList() {
		return mailAddCityList;
	}

	public void setMailAddCityList(List<SelectItem> mailAddCityList) {
		this.mailAddCityList = mailAddCityList;
	}

	public List<SelectItem> getPermAddCountryList() {
		return permAddCountryList;
	}

	public void setPermAddCountryList(List<SelectItem> permAddCountryList) {
		this.permAddCountryList = permAddCountryList;
	}

	public List<SelectItem> getPermAddStateList() {
		return permAddStateList;
	}

	public void setPermAddStateList(List<SelectItem> permAddStateList) {
		this.permAddStateList = permAddStateList;
	}

	public List<SelectItem> getPermAddDistrictList() {
		return permAddDistrictList;
	}

	public void setPermAddDistrictList(List<SelectItem> permAddDistrictList) {
		this.permAddDistrictList = permAddDistrictList;
	}

	public List<SelectItem> getPermAddCityList() {
		return permAddCityList;
	}

	public void setPermAddCityList(List<SelectItem> permAddCityList) {
		this.permAddCityList = permAddCityList;
	}

	public IPagingDataModel<SmPersonnel> getSmPersonnelDataModel2() {
		return smPersonnelDataModel2;
	}

	public void setSmPersonnelDataModel2(
			IPagingDataModel<SmPersonnel> smPersonnelDataModel2) {
		this.smPersonnelDataModel2 = smPersonnelDataModel2;
	}

	public boolean isDisableRole() {
		return disableRole;
	}

	public void setDisableRole(boolean disableRole) {
		this.disableRole = disableRole;
	}

	
	
}
