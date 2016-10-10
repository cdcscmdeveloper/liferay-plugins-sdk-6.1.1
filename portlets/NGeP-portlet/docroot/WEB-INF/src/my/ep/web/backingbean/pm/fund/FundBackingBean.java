package my.ep.web.backingbean.pm.fund;

import org.apache.commons.lang3.StringUtils;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.fund.IFundSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.fund.FundReqDTO;
import my.ep.dto.pm.common.fund.FundResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.logging.LogKey;
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

	private IPagingDataModel<Object[]> searchFund = new SearchFundPagingDataModel();

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

		debugLogger("resetFundSearchBean");		
		this.getManagedBean().setLazy(false);
	/*	this.getManagedBean().setFundCode(new String());
		this.getManagedBean().setFundName(new String());		
		this.getManagedBean().setStatus(null);*/
		this.getManagedBean().setFundSearchBean(new FundSearchBean());
		searchFund = new SearchFundPagingDataModel();
	}

	public void searchFund() {
		searchFund.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

//	private ISearchBean<Object[]> getSearchBeanData(
//			ISearchBean<Object[]> searchBean) {
//
//		FundSearchBean fundSearchBean = this.getManagedBean().getFundSearchBean();
//		
//		Date currentDate = new Date();
//		if (StringUtils.isNotBlank(fundSearchBean.getFundCode())) {
//
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("votFundCode",
//							SearchOperator.OPERATOR_EQUALS.value(), fundSearchBean
//							.getFundCode().toUpperCase() , true));
//			
//
//		}
//		if (StringUtils.isNotBlank(fundSearchBean.getFundName())){
//
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("description",
//							SearchOperator.OPERATOR_LIKE.value(), fundSearchBean
//							.getFundName().toUpperCase(), true));
//			
//		}
//
//
//		if( (StringUtils.isNotBlank(fundSearchBean.getStatus()) 
//				&& (fundSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE)))) ||
//					(StringUtils.isNotBlank(fundSearchBean.getStatus()) 
//							&& (fundSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_SUSPENDED)))) )
//		{
//			if(fundSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
//				
//				
//				
//				// Filter By - User effDate
//				searchBean.getDataFilterBeans().add(
//						new SearchFilterBean("effDate",
//								SearchOperator.OPERATOR_LESS_EQUAL.value(),
//								currentDate));		
//				// Filter By - User expDate
//				searchBean.getDataFilterBeans().add(
//						new SearchMultiFilterBean(new SearchFilterBean(
//								"expDate",
//								SearchOperator.OPERATOR_GREATER_EQUAL.value(),
//								currentDate), new SearchFilterBean(
//										"expDate",
//										SearchOperator.OPERATOR_IS_NULL.value(), null,
//										SearchCondition.CONDITION_OR.value())));
//				
//				/*searchBean.getDataFilterBeans().add(
//						new SearchFilterBean("recordStatusFlg",
//								SearchOperator.OPERATOR_EQUALS.value(),
//								fundSearchBean.getStatus()));
//				
//				searchBean.getDataFilterBeans().add(
//						new SearchFilterBean("votFundType",
//								SearchOperator.OPERATOR_NOT_EQUALS.value(),
//								PmCodeConst.VOT_FUND_CODE_TYPE));
//				*/
//				
//				   searchBean.getDataFilterBeans().add(
//							new SearchMultiFilterBean(new SearchFilterBean(
//									"recordStatusFlg",
//									SearchOperator.OPERATOR_EQUALS.value(),
//									fundSearchBean.getStatus()), new SearchFilterBean(
//											"votFundType",
//											SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.VOT_FUND_CODE_TYPE,
//											SearchCondition.CONDITION_AND.value())));
//				
//			}
//			else{
//				
//				searchBean.getDataFilterBeans().add(
//						new SearchFilterBean("votFundType",
//								SearchOperator.OPERATOR_EQUALS.value(),
//								ParameterConst.VOT_FUND_CODE_TYPE));
//				
//				// Filter By - User expDate
//				searchBean.getDataFilterBeans().add(
//						new SearchMultiFilterBean(new SearchFilterBean(
//								"expDate",
//								SearchOperator.OPERATOR_GREATER_EQUAL.value(),
//								currentDate), new SearchFilterBean(
//										"expDate",
//										SearchOperator.OPERATOR_IS_NULL.value(), null,
//										SearchCondition.CONDITION_OR.value())));
//				
//				/*searchBean.getDataFilterBeans().add(
//						new SearchFilterBean("recordStatusFlg",
//								SearchOperator.OPERATOR_EQUALS.value(),
//								fundSearchBean.getStatus()));*/
//				
//				
//				 searchBean.getDataFilterBeans().add(
//							new SearchMultiFilterBean(new SearchFilterBean(
//									"recordStatusFlg",
//									SearchOperator.OPERATOR_EQUALS.value(),
//									fundSearchBean.getStatus()), new SearchFilterBean(
//											"votFundType",
//											SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.VOT_FUND_CODE_TYPE,
//											SearchCondition.CONDITION_AND.value())));
//			}
//
//		}		
//		else{
//			List<String> activeAndInActiveStatus = new ArrayList<String>();
//			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
//			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("recordStatusFlg",
//							SearchOperator.OPERATOR_IN.value(), 
//							activeAndInActiveStatus
//							));
//			
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("votFundType",
//							SearchOperator.OPERATOR_EQUALS.value(),
//							ParameterConst.VOT_FUND_CODE_TYPE));
//		}
//		return searchBean;
//	}

	private class SearchFundPagingDataModel extends
	PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public SearchFundPagingDataModel() {
			super("fund",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IFundSO fundSO;

			try {
//				ISearchBean<Object[]> searchBeanData = getSearchBeanData(searchBean);
				FundReqDTO fundReqDTO = new FundReqDTO();
				
				FundSearchBean fundSearchBean = getManagedBean().getFundSearchBean();
				if (StringUtils.isNotBlank(fundSearchBean.getFundCode())) {
					fundReqDTO.setFundCode(fundSearchBean.getFundCode());
				}
				
				if (StringUtils.isNotBlank(fundSearchBean.getFundName())){
					fundReqDTO.setFundName(fundSearchBean.getFundName());					
				}

				if(StringUtils.isNotBlank(fundSearchBean.getStatus())) {
					fundReqDTO.setFundRecordStatus(Integer.parseInt(fundSearchBean.getStatus()));
				}
				
				fundReqDTO.setFundSearchBean(searchBean);
				fundSO = (IFundSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IFundSO.class);
				FundResDTO resDTO = fundSO.searchFundData(fundReqDTO);
				getManagedBean().setLazy(true);

				if (resDTO.isSuccessful()) {

					return resDTO.getFundResultBean();
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
	 * @return the searchFund
	 */
	public IPagingDataModel<Object[]> getSearchFund() {
		return searchFund;
	}

	/**
	 * @param searchFund the searchFund to set
	 */
	public void setSearchFund(IPagingDataModel<Object[]> searchFund) {
		this.searchFund = searchFund;
	}



}
