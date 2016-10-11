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
package my.ep.web.backingbean.pm.programactivity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ProgramSearchBean;
import my.ep.web.managedbean.pm.programactivity.ProgramActivityManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class ProgramActivityBackingBean.
 */
@ManagedBean(name = "programActivityBackingBean")
@ViewScoped
public class ProgramActivityBackingBean extends
BaseBackingBean<ProgramActivityManagedBean> {
	
	/** The log. */
	private static Logger log = Logger
			.getLogger(ProgramActivityBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/pm/programActivity/programActivitySearchMain";

	/** The search vot program activity. */
	private IPagingDataModel<Object[]> searchVotProgramActivity = new SearchVotPagingDataModel();

	/** The result. */
	boolean result = false;
	// Default value
	/** The Constant SEARCH_DEFAULT. */
	public static final boolean SEARCH_DEFAULT = false;

	/**
	 * Instantiates a new program activity backing bean.
	 */
	public ProgramActivityBackingBean() {

		log.info("inside ProgramActivityBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}

	}

	/**
	 * Pre loading program activity.
	 */
	public void preLoadingProgramActivity() {
		log.info("preLoading ProgramActivity  method");
		this.resetVotProgramActivitySearchBean();
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
	public void resetVotProgramActivitySearchBean() {

		debugLogger("resetVotProgramActivitySearchBean");		
		this.getManagedBean().setLazy(false);
		/*this.getManagedBean().setVotCode(new String());
		this.getManagedBean().setVotName(new String());
		this.getManagedBean().setProgramActivityCode(new String());
		this.getManagedBean().setProgramActivityDesc(new String());
		this.getManagedBean().setStatus(null);*/
		this.getManagedBean().setProgramSearchBean(new ProgramSearchBean());
		searchVotProgramActivity = new SearchVotPagingDataModel();

	}

	/**
	 * Search program activity.
	 */
	public void searchProgramActivity() {
		searchVotProgramActivity.reloadData();
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
	 * Gets the search bean data.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the search bean data
	 */
	private ISearchBean<PmPrgActivity> getSearchBeanData(
			ISearchBean<PmPrgActivity> searchBean) {

		ProgramSearchBean votProgramSearchBean = this.getManagedBean().getProgramSearchBean();

		Date currentDate = new Date();
		if (StringUtils.isNotBlank(votProgramSearchBean.getVotCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmVotFund.votFundCode",
							SearchOperator.OPERATOR_EQUALS.value(), votProgramSearchBean
							.getVotCode().toUpperCase() , true));


		}
		if (StringUtils.isNotBlank(votProgramSearchBean.getVotName())){

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmVotFund.description",
							SearchOperator.OPERATOR_LIKE.value(), votProgramSearchBean
							.getVotName().toUpperCase(), true));

		}

		if (StringUtils.isNotBlank(votProgramSearchBean.getProgramActivityCode())){

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("prgActivityCode",
							SearchOperator.OPERATOR_EQUALS.value(), 
							votProgramSearchBean.getProgramActivityCode().toUpperCase(), true));

		}

		if (StringUtils.isNotBlank(votProgramSearchBean.getProgramActivityDesc())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("description",
							SearchOperator.OPERATOR_LIKE.value(), votProgramSearchBean
							.getProgramActivityDesc().toUpperCase(), true));

		}

		if((StringUtils.isNotBlank(votProgramSearchBean.getStatus()) && (votProgramSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE)))) ||
				(StringUtils.isNotBlank(votProgramSearchBean.getStatus()) && (votProgramSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_SUSPENDED)))))
		{
			if(votProgramSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
				// Filter By - User effDate
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("effDate",
								SearchOperator.OPERATOR_LESS_EQUAL.value(),
								currentDate));		
				// Filter By - User expDate
				searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(new SearchFilterBean(
								"expDate",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(),
								currentDate), new SearchFilterBean(
										"expDate",
										SearchOperator.OPERATOR_IS_NULL.value(), null,
										SearchCondition.CONDITION_OR.value())));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								votProgramSearchBean.getStatus()));
			}
			else{
				// Filter By - User expDate
				searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(new SearchFilterBean(
								"expDate",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(),
								currentDate), new SearchFilterBean(
										"expDate",
										SearchOperator.OPERATOR_IS_NULL.value(), null,
										SearchCondition.CONDITION_OR.value())));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								votProgramSearchBean.getStatus()));
			}

		}		
		else{
			List<String> activeAndInActiveStatus = new ArrayList<String>();
			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_IN.value(), 
							activeAndInActiveStatus
							));
		}
		return searchBean;
	}

	/**
	 * The Class SearchVotPagingDataModel.
	 */
	private class SearchVotPagingDataModel extends
	PagingDataModel<Object[]> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search vot paging data model.
		 */
		public SearchVotPagingDataModel() {
			super("programActivitySR",getManagedBean().isLazy());
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IProgramActivitySO programActivitySO;

			try {
			//	ISearchBean<PmPrgActivity> searchBeanData = getSearchBeanData(searchBean);
				ProgramActivityReqDTO programActivityReqDTO = new ProgramActivityReqDTO();				
				ProgramSearchBean votProgramSearchBean = getManagedBean().getProgramSearchBean();
				if (StringUtils.isNotBlank(votProgramSearchBean.getVotCode())) {
					programActivityReqDTO.setVotCode(votProgramSearchBean.getVotCode())	;
				}
				if (StringUtils.isNotBlank(votProgramSearchBean.getVotName())){
					programActivityReqDTO.setVotDesc(votProgramSearchBean.getVotName());
				}

				if (StringUtils.isNotBlank(votProgramSearchBean.getProgramActivityCode())){
					programActivityReqDTO.setPrgActCode(votProgramSearchBean.getProgramActivityCode());
				}

				if (StringUtils.isNotBlank(votProgramSearchBean.getProgramActivityDesc())) {
					programActivityReqDTO.setPrgActDesc(votProgramSearchBean.getProgramActivityDesc());
				}

				if(StringUtils.isNotBlank(votProgramSearchBean.getStatus())){
					programActivityReqDTO.setPrgActRecordStatus(Integer.parseInt(votProgramSearchBean.getStatus()));
				}
				
				programActivityReqDTO.setPmPrgActivityObjSearchBean(searchBean);
				programActivitySO = (IProgramActivitySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IProgramActivitySO.class);
				ProgramActivityResDTO resDTO = programActivitySO.searchVotProgramActivityData(programActivityReqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {					
					return resDTO.getProgramActivityObjBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	/**
	 * Gets the status options.
	 * 
	 * @return the status options
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	/**
	 * @return the searchVotProgramActivity
	 */
	public IPagingDataModel<Object[]> getSearchVotProgramActivity() {
		return searchVotProgramActivity;
	}

	/**
	 * @param searchVotProgramActivity the searchVotProgramActivity to set
	 */
	public void setSearchVotProgramActivity(
			IPagingDataModel<Object[]> searchVotProgramActivity) {
		this.searchVotProgramActivity = searchVotProgramActivity;
	}

}
