package my.ep.web.backingbean.pm.category;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.category.CategoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "categoryBackingBean")
@ViewScoped
public class CategoryBackingBean extends BaseBackingBean<CategoryManagedBean> {
	private static Logger log = Logger.getLogger(CategoryBackingBean.class);

	private IPagingDataModel<Object[]> searchCategoryPagingDataModel = new SearchCategoryPagingDataModel();
	private boolean flag;
	boolean result = false;

	public CategoryBackingBean() {
		
	}
	
	private class SearchCategoryPagingDataModel extends PagingDataModel<Object[]> {

		private static final long serialVersionUID = 1L;

		public SearchCategoryPagingDataModel() {
			super("searchCategoryPagingDataModel");
		}

		public ISearchResultBean<Object[]> pageChangeListener(ISearchBean<Object[]> searchBean) {
			ICategoryCodeSO categoryCodeSO;
			try {
				categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

				if (getManagedBean().getMainCatCode() != null) {
					reqDTO.setMainCatCode(getManagedBean().getMainCatCode());
				}
				if (getManagedBean().getMainCatDesc() != null) {
					reqDTO.setMainCatDesc(getManagedBean().getMainCatDesc());
				}
				
				if (getManagedBean().getSubCatCode() != null) {
					reqDTO.setSubCatCode(getManagedBean().getSubCatCode());
				}
				if (getManagedBean().getSubCatDesc() != null) {
					reqDTO.setSubCatDesc(getManagedBean().getSubCatDesc());
				}
				
				if (getManagedBean().getSubDivCode() != null) {
					reqDTO.setSubDivCode(getManagedBean().getSubDivCode());
				}
				if (getManagedBean().getSubDivDesc() != null) {
					reqDTO.setSubDivDesc(getManagedBean().getSubDivDesc());
				}
				
				CategoryCodeResDTO resDTO =categoryCodeSO.searchAllCategory(reqDTO);

				if (resDTO.isSuccessful()) {
					//resDTO.getCategoryResultBean().getResultList();
					return resDTO.getCategoryResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				categoryCodeSO = null;
			}
			return null;
		}
	}
	
	public String addCategory() {
		this.getManagedBean().setPmCategoryL1(new PmCategoryL1());
		this.getManagedBean().setCatLevel("1");
		this.getManagedBean().setNewCategory(true);
		return "/xhtml/pm/category/categoryl1.xhtml";
	}
	
	public String addSubCategory() {
		PmCategoryL2 pmCategoryL2 = new PmCategoryL2();
		pmCategoryL2.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
		
		this.getManagedBean().setPmCategoryL2(pmCategoryL2);
		this.getManagedBean().setCatLevel("2");
		this.getManagedBean().setNewCategory(true);
		return "/xhtml/pm/category/categoryl2.xhtml";
	}
	
	public String addSubDivision() {
		PmCategoryL3 pmCategoryL3 = new PmCategoryL3();
		pmCategoryL3.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());
		pmCategoryL3.setSupplierTypeCode(new PmParameter());
		pmCategoryL3.setCategoryTypeCode(new PmParameter());
		
		this.getManagedBean().setPmCategoryL3(pmCategoryL3);
		this.getManagedBean().setCatLevel("3");
		this.getManagedBean().setNewCategory(true);
		reloadSelectionPmCategoryL3();
		return "/xhtml/pm/category/categoryl3.xhtml";
	}
	
	public String editCategory(Long id) {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			reqDTO.setCategoryL1Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL1DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel("1");
				this.getManagedBean().setPmCategoryL1(resDTO.getCategoryL1());
				this.getManagedBean().setNewCategory(false);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return "/xhtml/pm/category/categoryl1.xhtml";
	}
	
	public String editSubCategory(Long id) {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			reqDTO.setCategoryL2Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL2DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel("2");
				this.getManagedBean().setPmCategoryL2(resDTO.getCategoryL2());
				this.getManagedBean().setNewCategory(false);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return "/xhtml/pm/category/categoryl2.xhtml";
	}
	
	public String editSubDivision(Long id) {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			reqDTO.setCategoryL3Id(id);
			CategoryCodeResDTO resDTO = categoryCodeSO.retrieveCategoryL3DtlById(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCatLevel("3");
				this.getManagedBean().setPmCategoryL3(resDTO.getCategoryL3());
				this.getManagedBean().setNewCategory(false);
				reloadSelectionPmCategoryL3();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}
		return "/xhtml/pm/category/categoryl3.xhtml";
	}
	
	public String saveCategory() {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			this.getManagedBean().getPmCategoryL1().setCategoryName(this.getManagedBean().getPmCategoryL1().getCategoryName().toUpperCase());
			reqDTO.setPmCategoryL1(this.getManagedBean().getPmCategoryL1());
			CategoryCodeResDTO resDTO = categoryCodeSO.saveCategory(reqDTO);

			if (resDTO.isSuccessful()) {
				result = resDTO.isSuccessful();
				resetCategory();
				FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_700004,
							new String[]{"",ResourceBundleUtils.getMessage("lbl.pm.main.category",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}

		return "/xhtml/pm/category/search.xhtml";
	}
	
	public String saveSubCategory() {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			this.getManagedBean().getPmCategoryL2().setCategoryName(this.getManagedBean().getPmCategoryL2().getCategoryName().toUpperCase());
			reqDTO.setPmCategoryL2(this.getManagedBean().getPmCategoryL2());
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
			CategoryCodeResDTO resDTO = categoryCodeSO.saveSubCategory(reqDTO);

			if (resDTO.isSuccessful()) {
				result = resDTO.isSuccessful();
				resetCategory();
				FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_700004,
							new String[]{"",ResourceBundleUtils.getMessage("lbl.pm.sub.category",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}

		if (this.getManagedBean().isNewCategory()){
			return editCategory(this.getManagedBean().getPmCategoryL2().getPmCategoryL1().getCategoryL1Id());
		}else
			return "/xhtml/pm/category/search.xhtml";
	}
	
	public String saveSubDivision() {
		ICategoryCodeSO categoryCodeSO;
		try{
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			categoryCodeSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICategoryCodeSO.class);

			this.getManagedBean().getPmCategoryL3().setCategoryName(this.getManagedBean().getPmCategoryL3().getCategoryName().toUpperCase());
			reqDTO.setPmCategoryL3(this.getManagedBean().getPmCategoryL3());
			reqDTO.setNewCategory(this.getManagedBean().isNewCategory());
			CategoryCodeResDTO resDTO = categoryCodeSO.saveSubDivision(reqDTO);

			if (resDTO.isSuccessful()) {
				result = resDTO.isSuccessful();
				resetCategory();
				FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_700004,
							new String[]{"",ResourceBundleUtils.getMessage("lbl.pm.sub.div",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			categoryCodeSO = null;
		}

		if (this.getManagedBean().isNewCategory()){
			return editSubCategory(this.getManagedBean().getPmCategoryL3().getPmCategoryL2().getCategoryL2Id());
		}else
			return "/xhtml/pm/category/search.xhtml";
	}
	
	public void reloadSelectionPmCategoryL3() {
		IParameterSO parameterSO;
		try{
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
			
			reqDTO.setParameterType("ST");
			ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
			this.getManagedBean().setSupplierType(resDTO.getParameterDescList());
			
			reqDTO.setParameterType("CAT");
			resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
			this.getManagedBean().setSpecialCategory(resDTO.getParameterDescList());
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			parameterSO = null;
		}
	}
	
	public void resetCategory() {
		this.getManagedBean().setMainCatCode(null);
		this.getManagedBean().setMainCatDesc(null);
		this.getManagedBean().setSubCatCode(null);
		this.getManagedBean().setSubCatDesc(null);
		this.getManagedBean().setSubDivCode(null);
		this.getManagedBean().setSubDivDesc(null);
	}
	
	public void search() {
		searchCategoryPagingDataModel.reloadData();
	}
	
	public String backToSearch() {
		resetCategory();
		return "/xhtml/pm/category/search.xhtml";
	}

	public IPagingDataModel<Object[]> getSearchCategoryPagingDataModel() {
		return searchCategoryPagingDataModel;
	}

	public void setSearchCategoryPagingDataModel(
			IPagingDataModel<Object[]> searchCategoryPagingDataModel) {
		this.searchCategoryPagingDataModel = searchCategoryPagingDataModel;
	}
	
	
	
	
	
	
	
	


	
	

	public void reset() {
		
	}
	

	public String editOrganization(PmOrgProfile orgProfile) {
		/* getManagedBean().setOrganisationActiveIndex(1);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		OrgProfileResDTO orgResDTO = orgProfileSO
				.retrieveOrgAddressByOrgId(reqDTO);
		orgProfile = orgResDTO.getPmOrgProfile();
		getManagedBean().setOrgType(
				orgProfile.getOrgTypeCode().getParameterCode());
		// getManagedBean().getOrgProfile().setPmZone(orgProfile.getPmZone());

		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			retrievePmZoneList();
			getManagedBean().setZoneId(orgProfile.getPmZone().getZoneId());

		}

		List<PmAddressType> pmAddressTypes = orgProfile.getPmAddressTypes();
		if (pmAddressTypes != null && !pmAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : pmAddressTypes) {
				if (pmAddressType.getAddressType().equalsIgnoreCase("R")) {

					retrieveCategories();

					if (pmAddressType.getPmAddress().getPmDivision() == null) {
						pmAddressType.getPmAddress().setPmDivision(
								new PmDivision());
					}

					StateReqDTO stateReqDTO = new StateReqDTO();
					stateReqDTO.setCountryId(pmAddressType.getPmAddress()
							.getPmCountry().getCountryId());
					IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IStateSO.class);
					StateResDTO resDTO = reqSO
							.retriveStatesListByCountryID(stateReqDTO);
					getManagedBean().setStateList(resDTO.getStateList());

					DistrictReqDTO districtReqDTO = new DistrictReqDTO();
					IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IDistrictSO.class);
					if (pmAddressType.getPmAddress().getPmDivision()
							.getDivisionId() != null) {
						DivisionReqDTO divReqDTO = new DivisionReqDTO();
						divReqDTO.setStateId(pmAddressType.getPmAddress()
								.getPmState().getStateId());
						IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IDivisionSO.class);
						DivisionResDTO divResDTO = divisionSO
								.retriveDivisionListByState(divReqDTO);
						getManagedBean().setDivisionList(
								divResDTO.getDivisionList());
						districtReqDTO
								.setDivisionId(pmAddressType.getPmAddress()
										.getPmDivision().getDivisionId());
						DistrictResDTO districtResDTO = districtSO
								.retriveDistrictListByDivision(districtReqDTO);
						getManagedBean().setDistrictList(
								districtResDTO.getDistrictList());
					} else {
						districtReqDTO.setStateId(pmAddressType.getPmAddress()
								.getPmState().getStateId());
						DistrictResDTO districtResDTO = districtSO
								.retriveDistrictListByStateID(districtReqDTO);
						getManagedBean().setDistrictList(
								districtResDTO.getDistrictList());
					}



					CityReqDTO cityReqDTO = new CityReqDTO();
					cityReqDTO.setDistrictId(pmAddressType.getPmAddress()
							.getPmDistrict().getDistrictId());
					ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									ICitySO.class);
					CityResDTO cityResDTO = citySO
							.retriveCityListByDistrictId(cityReqDTO);
					this.getManagedBean().setCityList(cityResDTO.getCityList());



					//pmAddressType.setEditable(true);
					this.getManagedBean().setOfficeAddress(
							pmAddressType.getPmAddress());

				} else if (pmAddressType.getAddressType().equalsIgnoreCase("D")) {

					retrieveCategories();

					StateReqDTO stateReqDTO = new StateReqDTO();
					stateReqDTO.setCountryId(pmAddressType.getPmAddress()
							.getPmCountry().getCountryId());
					IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IStateSO.class);
					StateResDTO resDTO = reqSO
							.retriveStatesListByCountryID(stateReqDTO);
					getManagedBean().setStateList(resDTO.getStateList());

					DistrictReqDTO districtReqDTO = new DistrictReqDTO();
					IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IDistrictSO.class);

					districtReqDTO.setStateId(pmAddressType.getPmAddress()
							.getPmState().getStateId());
					DistrictResDTO districtResDTO = districtSO
							.retriveDistrictListByStateID(districtReqDTO);
					getManagedBean().setDistrictList(
							districtResDTO.getDistrictList());

					CityReqDTO cityReqDTO = new CityReqDTO();
					cityReqDTO.setDistrictId(pmAddressType.getPmAddress()
							.getPmDistrict().getDistrictId());
					ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									ICitySO.class);
					CityResDTO cityResDTO = citySO
							.retriveCityListByDistrictId(cityReqDTO);
					this.getManagedBean().setCityList(cityResDTO.getCityList());

				
					//pmAddressType.setEditable(true);
					this.getManagedBean().setDeliveryAddress(
							pmAddressType.getPmAddress());

				}
				if (!pmAddressType.getPmContacts().isEmpty()) {
					this.getManagedBean().setOfficeContactPerson(
							pmAddressType.getPmContacts().get(0));
				}
			}
		}

		this.getManagedBean().setOrgValidity(
				orgResDTO.getPmOrgProfile().getPmOrgValidity());
		this.getManagedBean().setOrgProfile(orgResDTO.getPmOrgProfile());*/

		return "/xhtml/pm/organisation/maintainOrganisation.xhtml";

	}



	public void okContacePersonAction() {

		// BO validation
		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.hide()");
	}


	public SelectItem[] getOrgTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add("SYS");
		recommendList.add("FJA");
		recommendList.add("FFC");
		recommendList.add("SUP");
		recommendList.add("OTH");

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		searchBean.getSortColumns();

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.RECORD_STATUS, searchBean);
		return items;
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}


	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
