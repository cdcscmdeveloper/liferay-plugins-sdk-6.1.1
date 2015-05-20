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
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AccountingOfficeSearchBean;
import my.ep.web.managedbean.pm.accountingoffice.AccountingOfficeManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "accountingOfficeBackingBean")
@ViewScoped
public class AccountingOfficeBackingBean extends
		BaseBackingBean<AccountingOfficeManagedBean> {
	private static Logger log = Logger
			.getLogger(AccountingOfficeBackingBean.class);
	
	public static final String NAV_TO_MAIN = "/xhtml/pm/accountingoffice/accountingOfficeSearchMain";

	private IPagingDataModel<PmAgOffice> searcrOffice = new SearchOfficePagingDataModel();

	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public AccountingOfficeBackingBean() {

	}

	public void preLoadingAccountingOffice() {
		log.info("preLoading Office method");
		this.resetOfficeSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * reset search bean fields
	 */
	public void resetOfficeSearchBean() {

		this.getManagedBean().setAccountingOfficeSearchBean(new AccountingOfficeSearchBean());
		searcrOffice = new SearchOfficePagingDataModel();
	}

	public void searchCtrOfficer() {
		searcrOffice.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	private ISearchBean<PmAgOffice> getSearchBeanData(
			ISearchBean<PmAgOffice> searchBean) {

		AccountingOfficeSearchBean officeSearchBean = this.getManagedBean().getAccountingOfficeSearchBean();
		boolean	isEmpty = true;
		if (officeSearchBean.getOfficeCode() != null
				&& officeSearchBean.getOfficeCode() .trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("officeCode",
							SearchOperator.OPERATOR_LIKE.value(), officeSearchBean.getOfficeCode() , true));
			isEmpty = false;
			
		}
		if (officeSearchBean.getOfficeName() != null
				&& officeSearchBean.getOfficeName().trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("officeName",
							SearchOperator.OPERATOR_LIKE.value(), officeSearchBean
									.getOfficeName(), true));
			isEmpty = false;
		}
		if(officeSearchBean.getStatus() != null
				&& !"".equals(officeSearchBean.getStatus())) {
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
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
		}
		return searchBean;
	}

	private class SearchOfficePagingDataModel extends
			PagingDataModel<PmAgOffice> {
		private static final long serialVersionUID = 1L;

		public SearchOfficePagingDataModel() {
			super("searchCtrOfficer");
		}

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
				AgOfficeResDTO resDTO = new AgOfficeResDTO(); //agOfficeSO.searchAgOfficerData(ctrReqDTO);
						
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

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	

	

}
