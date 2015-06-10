package my.ep.web.backingbean.pm.fund;

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
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.FundSearchBean;
import my.ep.web.managedbean.pm.fund.FundManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "fundBackingBean")
@ViewScoped
public class FundBackingBean extends
		BaseBackingBean<FundManagedBean> {
	private static Logger log = Logger
			.getLogger(FundBackingBean.class);
	
	public static final String NAV_TO_MAIN = "/xhtml/pm/fund/fundSearchMain";

	private IPagingDataModel<PmVotFund> searchFund = new SearchFundPagingDataModel();

	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public FundBackingBean() {

		log.info("inside FundBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}

	}

	public void preLoadingFund() {
		log.info("preLoading fund method");
		this.resetFundSearchBean();
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
	public void resetFundSearchBean() {

		debugLogger("resetVotSearchBean");		
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setFundCode(new String());
		this.getManagedBean().setFundName(new String());		
		this.getManagedBean().setStatus(null);
		this.getManagedBean().setFundSearchBean(new FundSearchBean());
		searchFund = new SearchFundPagingDataModel();
	}

	public void searchFund() {
		searchFund.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	private ISearchBean<PmVotFund> getSearchBeanData(
			ISearchBean<PmVotFund> searchBean) {

		FundSearchBean fundSearchBean = this.getManagedBean().getFundSearchBean();
		boolean	isEmpty = true;
		Date currentDate = new Date();
		if (StringUtils.isNotBlank(fundSearchBean.getFundCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("votFundCode",
							SearchOperator.OPERATOR_EQUALS.value(), fundSearchBean
									.getFundCode().toUpperCase() , true));
			isEmpty = false;
			
		}
		if (StringUtils.isNotBlank(fundSearchBean.getFundName())){

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("description",
							SearchOperator.OPERATOR_LIKE.value(), fundSearchBean
									.getFundName().toUpperCase(), true));
			isEmpty = false;
		}
		
				
		if(StringUtils.isNotBlank(fundSearchBean.getStatus())&& fundSearchBean.getStatus().equals(DbValueConst.RECORD_STATUS_ACTIVE))
		{
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
		      }
		
		if(fundSearchBean.getStatus() != null
				&& !"".equals(fundSearchBean.getStatus())) {
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						fundSearchBean.getStatus()));
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

	private class SearchFundPagingDataModel extends
			PagingDataModel<PmVotFund> {
		private static final long serialVersionUID = 1L;

		public SearchFundPagingDataModel() {
			super("searchFund",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmVotFund> pageChangeListener(
				ISearchBean<PmVotFund> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IVotFundSO votFundSO;

			try {
				ISearchBean<PmVotFund> searchBeanData = getSearchBeanData(searchBean);
				VotFundReqDTO votReqDTO = new VotFundReqDTO();
				votReqDTO.setVotFundSearchBean(searchBeanData);
				votFundSO = (IVotFundSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IVotFundSO.class);
				VotFundResDTO resDTO = votFundSO.searchVotFundData(votReqDTO);
				getManagedBean().setLazy(true);
						
				if (resDTO.isSuccessful()) {

					return resDTO.getVotResultBean();
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
	 * @return the searchVot
	 */
	public IPagingDataModel<PmVotFund> getSearchFund() {
		return searchFund;
	}

	/**
	 * @param searchVot the searchVot to set
	 */
	public void setSearchFund(IPagingDataModel<PmVotFund> searchFund) {
		this.searchFund = searchFund;
	}

}
