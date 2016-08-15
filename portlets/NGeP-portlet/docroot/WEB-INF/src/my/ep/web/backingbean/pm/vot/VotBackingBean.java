package my.ep.web.backingbean.pm.vot;

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
import my.ep.constant.pm.PmCodeConst;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.VotFundType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.VotSearchBean;
import my.ep.web.managedbean.pm.vot.VotManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "votBackingBean")
@ViewScoped
public class VotBackingBean extends
BaseBackingBean<VotManagedBean> {
	private static Logger log = Logger
			.getLogger(VotBackingBean.class);

	public static final String NAV_TO_MAIN = "/xhtml/pm/vot/votSearchMain";

	private IPagingDataModel<PmVotFund> searchVot = new SearchVotPagingDataModel();

	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public VotBackingBean() {

		log.info("inside VotBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}

	}

	public void preLoadingVot() {
		log.info("preLoading vot method");
		this.resetVotSearchBean();
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
	public void resetVotSearchBean() {

		debugLogger("resetVotSearchBean");		
		this.getManagedBean().setLazy(false);
		/*this.getManagedBean().setVotCode(new String());
		this.getManagedBean().setVotName(new String());
		this.getManagedBean().setKumpulanPtjCode(new String());
		this.getManagedBean().setKumpulanPtjDesc(new String());
		this.getManagedBean().setStatus(null);*/
		this.getManagedBean().setVotSearchBean(new VotSearchBean());
		searchVot = new SearchVotPagingDataModel();
		
		/*this.setSearchVot(null);
		searchVot = new SearchVotPagingDataModel("reset");*/
		
		/*DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("componentId");
		dataTable.setPaginator(_paginator)*/
	}

	public void searchVot() {
		searchVot.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	private ISearchBean<PmVotFund> getSearchBeanData(
			ISearchBean<PmVotFund> searchBean) {

		VotSearchBean votSearchBean = this.getManagedBean().getVotSearchBean();
		boolean	isEmpty = true;
		Date currentDate = new Date();
		if (StringUtils.isNotBlank(votSearchBean.getVotCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("votFundCode",
							SearchOperator.OPERATOR_EQUALS.value(), votSearchBean
							.getVotCode().toLowerCase() , true));
			isEmpty = false;

		}
		if (StringUtils.isNotBlank(votSearchBean.getVotName())){

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("description",
							SearchOperator.OPERATOR_LIKE.value(), votSearchBean
							.getVotName().toLowerCase(), true));
			isEmpty = false;
		}

		if (StringUtils.isNotBlank(votSearchBean.getKumpulanPtjCode())){

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.pmOrgValidity.orgCode",
							SearchOperator.OPERATOR_EQUALS.value(), votSearchBean
							.getKumpulanPtjCode().toLowerCase(), true));
			isEmpty = false;
		}

		if (StringUtils.isNotBlank(votSearchBean.getKumpulanPtjDesc())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.pmOrgValidity.orgName",
							SearchOperator.OPERATOR_LIKE.value(), votSearchBean
							.getKumpulanPtjDesc().toLowerCase(), true));
			isEmpty = false;
		}


		if((StringUtils.isNotBlank(votSearchBean.getStatus()) && votSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))) ||
				(StringUtils.isNotBlank(votSearchBean.getStatus()) && votSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_SUSPENDED))))
		{
			if(votSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
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
				
				/*searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								votSearchBean.getStatus()));			
								
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("votFundType",
								SearchOperator.OPERATOR_NOT_EQUALS.value(),
								ParameterConst.VOT_FUND_CODE_TYPE));*/
				
				        searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(new SearchFilterBean(
								"recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								votSearchBean.getStatus()), new SearchFilterBean(
										"votFundType",
										SearchOperator.OPERATOR_NOT_EQUALS.value(), ParameterConst.VOT_FUND_CODE_TYPE,
										SearchCondition.CONDITION_AND.value())));
				
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
				
				/*searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								votSearchBean.getStatus()));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("votFundType",
								SearchOperator.OPERATOR_NOT_EQUALS.value(),
								PmCodeConst.VOT_FUND_CODE_TYPE));*/
				
				
				 searchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(new SearchFilterBean(
									"recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(),
									votSearchBean.getStatus()), new SearchFilterBean(
											"votFundType",
											SearchOperator.OPERATOR_NOT_EQUALS.value(), ParameterConst.VOT_FUND_CODE_TYPE,
											SearchCondition.CONDITION_AND.value())));
			
				
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
						
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("votFundType",
							SearchOperator.OPERATOR_NOT_EQUALS.value(),
							ParameterConst.VOT_FUND_CODE_TYPE));
		}
		return searchBean;
	}

	private class SearchVotPagingDataModel extends
	PagingDataModel<PmVotFund> {
		private static final long serialVersionUID = 1L;

		public SearchVotPagingDataModel() {
			super("vot",getManagedBean().isLazy());
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
	public IPagingDataModel<PmVotFund> getSearchVot() {
		return searchVot;
	}

	/**
	 * @param searchVot the searchVot to set
	 */
	public void setSearchVot(IPagingDataModel<PmVotFund> searchVot) {
		this.searchVot = searchVot;
	}

}
