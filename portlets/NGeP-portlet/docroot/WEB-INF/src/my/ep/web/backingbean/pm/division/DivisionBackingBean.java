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
package my.ep.web.backingbean.pm.division;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.states.IListOfStatesSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.DivisionSearchBean;
import my.ep.web.managedbean.pm.division.DivisionManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class DivisionBackingBean.
 *    @author Nagasiva
 *    @since 13.06.2016
 */
@ManagedBean(name = "divisionBackingBean")
@ViewScoped
public class DivisionBackingBean extends BaseBackingBean<DivisionManagedBean> {

	/** The log. */
	private static Logger log = Logger.getLogger(DivisionBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/pm/division/divisionSearchMain";

	/** The search division. */
	private IPagingDataModel<PmDivision> searchDivision = new SearchDivisionPagingDataModel();

	/**
	 * Instantiates a new division backing bean.
	 */
	public DivisionBackingBean() {

		log.info("inside DivisionBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}

	/**
	 * Pre loading division.
	 */
	public void preLoadingDivision() {
		log.info("preLoadingDivision");
		this.resetDivisionSearchBean();
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
	 * reset search bean fields.
	 */
	public void resetDivisionSearchBean() {

		debugLogger("resetDivisionSearchBean");
		this.getManagedBean().setLazy(false);
		/*this.getManagedBean().setStateCode(new String());
		this.getManagedBean().setDivisionName(new String());*/
		this.getManagedBean().setDivisionSearchBean(new DivisionSearchBean());
		searchDivision = new SearchDivisionPagingDataModel();

	}

	/**
	 * Search division.
	 */
	public void searchDivision() {
		searchDivision.reloadData();
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
	 * Gets the search division.
	 * 
	 * @return the searchDivision
	 */
	public IPagingDataModel<PmDivision> getSearchDivision() {
		return searchDivision;
	}

	/**
	 * Sets the search division.
	 * 
	 * @param searchDivision
	 *            the searchDivision to set
	 */
	public void setSearchDivision(IPagingDataModel<PmDivision> searchDivision) {
		this.searchDivision = searchDivision;
	}

	/**
	 * Gets the search bean data.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the search bean data
	 */
	private ISearchBean<PmDivision> getSearchBeanData(
			ISearchBean<PmDivision> searchBean) {

		DivisionSearchBean divisionSearchBean = this.getManagedBean()
				.getDivisionSearchBean();

		if (StringUtils.isNotBlank(divisionSearchBean.getStateCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmState.stateCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							divisionSearchBean.getStateCode()));
			
		}

		if (StringUtils.isNotBlank(divisionSearchBean.getDivisionName())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("divisionName",
							SearchOperator.OPERATOR_LIKE.value(),
							divisionSearchBean.getDivisionName().toUpperCase(),
							true));
		}
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmState.recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));

		/*// Order by divisionName
		if (searchBean.getSortColumns().size() == 0) {
			searchBean.getSortColumns()
					.add(new SearchOrderBean("divisionName"));
		}*/

		return searchBean;
	}

	/**
	 * The Class SearchDivisionPagingDataModel.
	 */
	private class SearchDivisionPagingDataModel extends
			PagingDataModel<PmDivision> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search division paging data model.
		 */
		public SearchDivisionPagingDataModel() {
			super("division", getManagedBean().isLazy());
		}
		
		@Override
		public ISearchResultBean<PmDivision> pageChangeListener(
				ISearchBean<PmDivision> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IDivisionSO divisionSO;

			try {
				ISearchBean<PmDivision> searchBeanData = getSearchBeanData(searchBean);
				DivisionReqDTO divisionReqDTO = new DivisionReqDTO();
				divisionReqDTO.setDivisionSearchBean(searchBeanData);
				divisionSO = (IDivisionSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IDivisionSO.class);
				DivisionResDTO resDTO = divisionSO
						.retriveDivisionListByStateCodeAndDivisionName(divisionReqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {

					return resDTO.getDivisionResultBean();
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
	public List<PmState> getListOfStates() {

		IListOfStatesSO listOfStatesSO = null;
		List<PmState> result = null;
		try {

			listOfStatesSO = (IListOfStatesSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IListOfStatesSO.class);

			result = listOfStatesSO.retrieveStateListByAdoption();
			//this.getManagedBean().setListOfStates(result);
			
			log.info("...List Of States.size()....="
					+ result.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
