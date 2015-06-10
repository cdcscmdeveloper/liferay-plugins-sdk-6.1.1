package my.ep.web.backingbean.pm.glaccount;

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
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.GlAccountSearchBean;
import my.ep.web.managedbean.pm.glaccount.GlAccountManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "glAccountBackingBean")
@ViewScoped
public class GlAccountBackingBean extends
		BaseBackingBean<GlAccountManagedBean> {
	
	private static Logger log = Logger
			.getLogger(GlAccountBackingBean.class);
	
	public static final String NAV_TO_MAIN = "/xhtml/pm/glaccount/glAccountSearchMain";

	private IPagingDataModel<PmGlAccount> searchGlAccount = new SearchGlAccountPagingDataModel();

	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;
	
    public GlAccountBackingBean() {
		
		log.info("inside GlAccountBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}

	public void preLoadingGlAccount() {
		log.info("preLoading GlAccount method");
		this.resetGlAccountSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}	
	
	/**
	 * reset search bean fields
	 */
	public void resetGlAccountSearchBean() {
		debugLogger("resetGlAccountSearchBean");		
		this.getManagedBean().setLazy(false);
	    this.getManagedBean().setGlAccountCode(new String());
	    this.getManagedBean().setGlAccountName(new String());
	    this.getManagedBean().setStatus(null);
	    this.getManagedBean().setGlAccountSearchBean(new GlAccountSearchBean());
		searchGlAccount = new SearchGlAccountPagingDataModel();
	}

	public void searchGlAccount() {
		searchGlAccount.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	private ISearchBean<PmGlAccount> getSearchBeanData(
			ISearchBean<PmGlAccount> searchBean) {

		GlAccountSearchBean glAccountSearchBean = this.getManagedBean().getGlAccountSearchBean();
		boolean	isEmpty = true;
		if (StringUtils.isNotBlank(glAccountSearchBean.getAccountCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("glAccCode",
							SearchOperator.OPERATOR_EQUALS.value(), glAccountSearchBean
									.getAccountCode().toUpperCase() , true));
			isEmpty = false;
			
		}
		if (StringUtils.isNotBlank(glAccountSearchBean.getAccountName())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("description",
							SearchOperator.OPERATOR_LIKE.value(), glAccountSearchBean
									.getAccountName().toUpperCase(), true));
			isEmpty = false;
		}
		if (StringUtils.isNotBlank(glAccountSearchBean.getStatus())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							glAccountSearchBean.getStatus()));
		} else {
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

	private class SearchGlAccountPagingDataModel extends
			PagingDataModel<PmGlAccount> {
		private static final long serialVersionUID = 1L;

		public SearchGlAccountPagingDataModel() {
			super("searchCtrOfficer",getManagedBean().isLazy());
		}

		@SuppressWarnings("unchecked")
		@Override
		public ISearchResultBean<PmGlAccount> pageChangeListener(
				ISearchBean<PmGlAccount> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IGlAccountSO glAccountSO;

			try {
				ISearchBean<PmGlAccount> searchBeanData = getSearchBeanData(searchBean);
				GlAccountReqDTO glAccountReqDTO = new GlAccountReqDTO();
				glAccountReqDTO.setGlAccountSearchBean(searchBeanData);
				glAccountSO = (IGlAccountSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IGlAccountSO.class);
				GlAccountResDTO resDTO = glAccountSO.searchGlAccountData(glAccountReqDTO);				
				getManagedBean().setLazy(true);		
				if (resDTO.isSuccessful()) {

					return resDTO.getGlAccountResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	/**
	 * @return the searchGlAccount
	 */
	public IPagingDataModel<PmGlAccount> getSearchGlAccount() {
		return searchGlAccount;
	}

	/**
	 * @param searchGlAccount the searchGlAccount to set
	 */
	public void setSearchGlAccount(IPagingDataModel<PmGlAccount> searchGlAccount) {
		this.searchGlAccount = searchGlAccount;
	}

	
	

}
