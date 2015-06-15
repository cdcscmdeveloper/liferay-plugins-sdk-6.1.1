package my.ep.web.backingbean.pm.programactivity;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
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

@ManagedBean(name = "programActivityBackingBean")
@ViewScoped
public class ProgramActivityBackingBean extends
BaseBackingBean<ProgramActivityManagedBean> {
	private static Logger log = Logger
			.getLogger(ProgramActivityBackingBean.class);

	public static final String NAV_TO_MAIN = "/xhtml/pm/programActivity/programActivitySearchMain";

	private IPagingDataModel<PmPrgActivity> searchVotProgramActivity = new SearchVotPagingDataModel();

	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

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

	public void preLoadingProgramActivity() {
		log.info("preLoading ProgramActivity  method");
		this.resetVotProgramActivitySearchBean();
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
	public void resetVotProgramActivitySearchBean() {

		debugLogger("resetVotSearchBean");		
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setVotCode(new String());
		this.getManagedBean().setVotName(new String());
		this.getManagedBean().setProgramActivityCode(new String());
		this.getManagedBean().setProgramActivityDesc(new String());
		this.getManagedBean().setStatus(null);
		this.getManagedBean().setProgramSearchBean(new ProgramSearchBean());
		searchVotProgramActivity = new SearchVotPagingDataModel();

	}

	public void searchProgramActivity() {
		searchVotProgramActivity.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

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

	private class SearchVotPagingDataModel extends
	PagingDataModel<PmPrgActivity> {
		private static final long serialVersionUID = 1L;

		public SearchVotPagingDataModel() {
			super("searchVotProgramActivity",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmPrgActivity> pageChangeListener(
				ISearchBean<PmPrgActivity> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IVotFundSO votFundSO;

			try {
				ISearchBean<PmPrgActivity> searchBeanData = getSearchBeanData(searchBean);
				VotFundReqDTO votReqDTO = new VotFundReqDTO();
				votReqDTO.setPmPrgActivitySearchBean(searchBeanData);
				votFundSO = (IVotFundSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IVotFundSO.class);
				VotFundResDTO resDTO = votFundSO.searchVotProgramActivityData(votReqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {					
					return resDTO.getPmPrgActivityResultBean();
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
	 * @return the searchVotProgramActivity
	 */
	public IPagingDataModel<PmPrgActivity> getSearchVotProgramActivity() {
		return searchVotProgramActivity;
	}

	/**
	 * @param searchVotProgramActivity the searchVotProgramActivity to set
	 */
	public void setSearchVotProgramActivity(
			IPagingDataModel<PmPrgActivity> searchVotProgramActivity) {
		this.searchVotProgramActivity = searchVotProgramActivity;
	}



}
