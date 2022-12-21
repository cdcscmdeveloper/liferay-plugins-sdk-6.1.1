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
package my.ep.web.backingbean.pm.district;

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
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.states.IListOfStatesSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.DistrictSearchBean;
import my.ep.web.managedbean.pm.district.DistrictManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class DistrictBackingBean.
 * 
 * @author Nagasiva
 * @since 14.06.2016
 */
@ManagedBean(name = "districtBackingBean")
@ViewScoped
public class DistrictBackingBean extends BaseBackingBean<DistrictManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger.getLogger(DistrictBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/pm/district/districtSearchMain";

	/** The search district. */
	private IPagingDataModel<PmDistrict> searchDistrict = new SearchDistrictPagingDataModel();

	/** The states map. */
	private Map<String, PmState> statesMap = new HashMap<String, PmState>();

	/**
	 * Instantiates a new district backing bean.
	 */
	public DistrictBackingBean() {

		log.info("inside DistrictBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}

	/**
	 * Pre loading district.
	 */
	public void preLoadingDistrict() {
		log.info("preLoadingDistrict");
		this.resetDistrictSearchBean();
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
	public void resetDistrictSearchBean() {

		debugLogger("resetDistrictSearchBean");
		this.getManagedBean().setLazy(false);	
		this.getManagedBean().setAdoptDivision(false);
		this.getManagedBean().setDistrictSearchBean(new DistrictSearchBean());
		searchDistrict = new SearchDistrictPagingDataModel();

	}

	/**
	 * Search district.
	 */
	public void searchDistrict() {
		searchDistrict.reloadData();
	}

	/**
	 * Back to search.
	 * 
	 * @return the string
	 */
	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	/**
	 * Gets the search district.
	 * 
	 * @return the search district
	 */
	public IPagingDataModel<PmDistrict> getSearchDistrict() {
		return searchDistrict;
	}

	/**
	 * Sets the search district.
	 * 
	 * @param searchDistrict
	 *            the new search district
	 */
	public void setSearchDistrict(IPagingDataModel<PmDistrict> searchDistrict) {
		this.searchDistrict = searchDistrict;
	}

	/**
	 * Gets the search bean data.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the search bean data
	 */
	private ISearchBean<PmDistrict> getSearchBeanData(
			ISearchBean<PmDistrict> searchBean) {

		DistrictSearchBean districtSearchBean = this.getManagedBean()
				.getDistrictSearchBean();

		if (StringUtils.isNotBlank(districtSearchBean.getStateCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmState.stateCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							districtSearchBean.getStateCode()));
		}

		if (StringUtils.isNotBlank(districtSearchBean.getDivisionName())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmDivision.divisionName",
							SearchOperator.OPERATOR_LIKE.value(),
							districtSearchBean.getDivisionName().toLowerCase(),
							true));
		}
		
	

		if (StringUtils.isNotBlank(districtSearchBean.getDistrictName())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("districtName",
							SearchOperator.OPERATOR_LIKE.value(),
							districtSearchBean.getDistrictName().toLowerCase(),
							true));
		}
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmState.pmCountry.countryCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						CommonConstants.DEFAULT_COUNTRY_CODE,
						true));	
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmState.recordStatusFlg",
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
	private class SearchDistrictPagingDataModel extends
			PagingDataModel<PmDistrict> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search district paging data model.
		 */
		public SearchDistrictPagingDataModel() {
			super("district", getManagedBean().isLazy());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmDistrict> pageChangeListener(
				ISearchBean<PmDistrict> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IDistrictSO districtSO;

			try {
				ISearchBean<PmDistrict> searchBeanData = getSearchBeanData(searchBean);
				DistrictReqDTO districtReqDTO = new DistrictReqDTO();
				districtReqDTO.setDistrictSearchBean(searchBeanData);
				districtSO = (IDistrictSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = districtSO
						.retriveDistrictListByStateCodeDivisionNameAndDistrictName(districtReqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {

					return resDTO.getDistrictResultBean();
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
				.getDistrictSearchBean().getStateCode());
		if (pmState != null) {
			this.getManagedBean().setAdoptDivision(pmState.getAdoptDivision());
		}

	}

}
