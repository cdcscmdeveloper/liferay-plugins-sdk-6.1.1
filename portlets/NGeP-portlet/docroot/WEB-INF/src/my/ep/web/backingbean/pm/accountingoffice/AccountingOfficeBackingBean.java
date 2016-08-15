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
package my.ep.web.backingbean.pm.accountingoffice;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AccountingOfficeSearchBean;
import my.ep.web.managedbean.pm.accountingoffice.AccountingOfficeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class AccountingOfficeBackingBean.
 */
@ManagedBean(name = "accountingOfficeBackingBean")
@ViewScoped
public class AccountingOfficeBackingBean extends
		BaseBackingBean<AccountingOfficeManagedBean> {
	
	/** The log. */
	private static Logger log = Logger
			.getLogger(AccountingOfficeBackingBean.class);
	
	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/pm/accountingoffice/accountingOfficeSearchMain";

	/** The search accounting office. */
	private IPagingDataModel<PmAgOffice> searchAccountingOffice = new SearchOfficePagingDataModel();

	/** The result. */
	boolean result = false;
	// Default value
	/** The Constant SEARCH_DEFAULT. */
	public static final boolean SEARCH_DEFAULT = false;

	/**
	 * Instantiates a new accounting office backing bean.
	 */
	public AccountingOfficeBackingBean() {
		
		log.info("inside AccountingOfficeBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {

			System.out.println("***userId: " + pmUser.getUserId());
			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}

	/**
	 * Pre loading accounting office.
	 */
	public void preLoadingAccountingOffice() {
		log.info("preLoading AccountingOffice method");
		renewSesionObject();
		this.resetOfficeSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * reset search bean fields.
	 */
	public void resetOfficeSearchBean() {
		
		debugLogger("resetAccountingOfficeSearch");
		this.getManagedBean().setAccountingOfficeSearchBean(new AccountingOfficeSearchBean());
		this.getManagedBean().setLazy(false);         
        searchAccountingOffice = new SearchOfficePagingDataModel();
	}

	/**
	 * Search accounting office.
	 */
	public void searchAccountingOffice() {
		debugLogger("SearchAccountingOffice--->reloadData");
		searchAccountingOffice.reloadData();
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
	 * Gets the search accounting office.
	 * 
	 * @return the searchAccountingOffice
	 */
	public IPagingDataModel<PmAgOffice> getSearchAccountingOffice() {
		return searchAccountingOffice;
	}

	/**
	 * Sets the search accounting office.
	 * 
	 * @param searchAccountingOffice
	 *            the searchAccountingOffice to set
	 */
	public void setSearchAccountingOffice(
			IPagingDataModel<PmAgOffice> searchAccountingOffice) {
		this.searchAccountingOffice = searchAccountingOffice;
	}
	
	/**
	 * get salutation info into maintenance page.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the search bean data
	 *//*
	public String goToMaintaincePage(PmAgOffice pmAccountingOffice) {
		debugLogger("goToMaintainceAction");
		this.getManagedBean().setPmAccountingOffice(pmAccountingOffice);

		return "/xhtml/pm/salutation/salutationMaintenance.xhtml";
	}	*/
	
	private ISearchBean<PmAgOffice> getSearchBeanData(
			ISearchBean<PmAgOffice> searchBean) {

		AccountingOfficeSearchBean officeSearchBean = this.getManagedBean().getAccountingOfficeSearchBean();
		debugLogger("getSearchBeanData--Search Criteria Data");
		if (StringUtils.isNotBlank(officeSearchBean.getOfficeCode())){

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("officeCode",
							SearchOperator.OPERATOR_EQUALS.value(), officeSearchBean.getOfficeCode().toUpperCase() , true));
			
			
		}
		if (StringUtils.isNotBlank(officeSearchBean.getOfficeName())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmAgValidityList.officeName",
							SearchOperator.OPERATOR_LIKE.value(), officeSearchBean
									.getOfficeName().toUpperCase(), true));
			
		}
		if(StringUtils.isNotBlank(officeSearchBean.getStatus())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							officeSearchBean.getStatus()));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmAgValidityList.recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							officeSearchBean.getStatus()));
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
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmAgValidityList.recordStatusFlg",
							SearchOperator.OPERATOR_IN.value(), 
							activeAndInActiveStatus
			));
		}
		return searchBean;
	}

	/**
	 * The Class SearchOfficePagingDataModel.
	 */
	private class SearchOfficePagingDataModel extends
			PagingDataModel<PmAgOffice> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search office paging data model.
		 */
		public SearchOfficePagingDataModel() {
			super("accountingSR",getManagedBean().isLazy());
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmAgOffice> pageChangeListener(
				ISearchBean<PmAgOffice> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IAgOfficeSO agOfficeSO;

			try {
				ISearchBean<PmAgOffice> searchBeanData = getSearchBeanData(searchBean);
				AgOfficeReqDTO ctrReqDTO = new AgOfficeReqDTO();
				ctrReqDTO.setOfficeSearchBean(searchBeanData);
				agOfficeSO = (IAgOfficeSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IAgOfficeSO.class);
				AgOfficeResDTO resDTO= agOfficeSO.retrieveAgOfficeByOfficeCodeBySearch(ctrReqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {

					return resDTO.getOfficeResultBean();
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


}
