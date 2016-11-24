/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.pm.city;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.states.IListOfStatesSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.CitySearchBean;
import my.ep.web.managedbean.pm.city.CityManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;

/**
 * The Class CityBackingBean.
 * 
 * @author Nagasiva
 * @since 04.07.2016
 */
@ManagedBean(name = "cityBackingBean")
@ViewScoped
public class CityBackingBean extends BaseBackingBean<CityManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger.getLogger(CityBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/pm/cityortown/cityOrTownSearchMain.xhtml";


	private IPagingDataModel<PmCity> searchCity = new SearchCityPagingDataModel();

	/** The states map. */
	private Map<String, PmState> statesMap = new HashMap<String, PmState>();

	
	public CityBackingBean() {

		log.info("inside CityBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}

	
	public void preLoadingCity() {
		log.info("preLoadingCity");
		this.resetCitySearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Reset district search bean.
	 */
	public void resetCitySearchBean() {

		debugLogger("resetsCitySearchBean");
		this.getManagedBean().setLazy(false);	
		this.getManagedBean().setAdoptDivision(false);
		this.getManagedBean().setCitySearchBean(new CitySearchBean());
		searchCity = new SearchCityPagingDataModel();

	}

	/**
	 * Search district.
	 */
	public void searchCity() {
		searchCity.reloadData();
	}

	/**
	 * Back to search.
	 * 
	 * @return the string
	 */
	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	
	public IPagingDataModel<PmCity> getSearchCity() {
		return searchCity;
	}

	
	public void setSearchDistrict(IPagingDataModel<PmCity> searchCity) {
		this.searchCity = searchCity;
	}

	/**
	 * Gets the search bean data.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the search bean data
	 */
	private ISearchBean<PmCity> getSearchBeanData(
			ISearchBean<PmCity> searchBean) {

		CitySearchBean citySearchBean = this.getManagedBean()
				.getCitySearchBean();

		if (StringUtils.isNotBlank(citySearchBean.getStateCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmDistrict.pmState.stateCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							citySearchBean.getStateCode()));
			
			
		}

		if (StringUtils.isNotBlank(citySearchBean.getDivisionName())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmDistrict.pmDivision.divisionName",
							SearchOperator.OPERATOR_LIKE.value(),
							citySearchBean.getDivisionName().toLowerCase(),
							true));
			
		}		
		if (StringUtils.isNotBlank(citySearchBean.getDistrictName())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmDistrict.districtName",
							SearchOperator.OPERATOR_LIKE.value(),
							citySearchBean.getDistrictName().toLowerCase(),
							true));
		}		
		if (StringUtils.isNotBlank(citySearchBean.getCityName())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("cityName",
							SearchOperator.OPERATOR_LIKE.value(),
							citySearchBean.getCityName().toLowerCase(),
							true));
		}
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmDistrict.pmState.pmCountry.countryCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						CommonConstants.DEFAULT_COUNTRY_CODE,
						true));	
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmDistrict.pmState.recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmDistrict.recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));
		
		
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));		
		
		return searchBean;
	}

	/**
	 * The Class SearchDistrictPagingDataModel.
	 */
	private class SearchCityPagingDataModel extends
			PagingDataModel<PmCity> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search district paging data model.
		 */
		public SearchCityPagingDataModel() {
			super("city", getManagedBean().isLazy());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmCity> pageChangeListener(
				ISearchBean<PmCity> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			ICitySO citySO;

			try {
				ISearchBean<PmCity> searchBeanData = getSearchBeanData(searchBean);
				CityReqDTO cityReqDTO = new CityReqDTO();
				cityReqDTO.setCitySearchBean(searchBeanData);
				citySO = (ICitySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICitySO.class);
				CityResDTO resDTO = citySO
						.retriveCityListDataBySearchBean(cityReqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {

					return resDTO.getCityResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	/**
	 * Gets the list of states.
	 * 
	 * @return the list of states
	 */
	public List<SelectItem> getListOfStates() {

		IListOfStatesSO listOfStatesSO = null;
		
		List<PmState> stateList=null;

		if (CollectionUtils.isEmpty(this.getManagedBean().getListOfStates())) {

			try {
				statesMap.clear();
				listOfStatesSO = (IListOfStatesSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IListOfStatesSO.class);

				stateList = listOfStatesSO.retrieveListOfStates();

				for (PmState state : stateList) {

					statesMap.put(state.getStateCode(), state);

					SelectItem selectItem = new SelectItem();

					selectItem.setValue(state.getStateCode().toString());
					selectItem.setLabel(state.getStateName().toString());

					this.getManagedBean().getListOfStates().add(selectItem);
				}

				log.info("...List Of States.size()...=" + stateList.size());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.getManagedBean().getListOfStates();
	}

	/**
	 * Change state selection.
	 */
	public void changeStateSelection() {

		PmState pmState = statesMap.get(this.getManagedBean()
				.getCitySearchBean().getStateCode());
		if (pmState != null) {
			this.getManagedBean().setAdoptDivision(pmState.getAdoptDivision());
		}

	}

}
