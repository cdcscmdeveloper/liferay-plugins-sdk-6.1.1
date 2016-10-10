package my.ep.web.backingbean.pm.vot;

import org.apache.commons.lang3.StringUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.VotSearchBean;
import my.ep.web.managedbean.pm.vot.VotManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
/**
 * FL Budget Committment
 *  **/
@ManagedBean(name = "votBackingBean")
@ViewScoped
public class VotBackingBean extends
BaseBackingBean<VotManagedBean> {
	private static Logger log = Logger
			.getLogger(VotBackingBean.class);

	public static final String NAV_TO_MAIN = "/xhtml/pm/vot/votSearchMain";

	private IPagingDataModel<Object[]> searchVot = new SearchVotPagingDataModel();

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

//	private ISearchBean<Object[]> getSearchBeanData(
//			ISearchBean<Object[]> searchBean) {
//
//		VotSearchBean votSearchBean = this.getManagedBean().getVotSearchBean();
//		boolean	isEmpty = true;
//		Date currentDate = DateUtils.getCurrentDateWithoutTime();
//		if (StringUtils.isNotBlank(votSearchBean.getVotCode())) {
//
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("votFundCode",
//							SearchOperator.OPERATOR_EQUALS.value(), votSearchBean
//							.getVotCode().toLowerCase() , true));
//			isEmpty = false;
//
//		}
//		if (StringUtils.isNotBlank(votSearchBean.getVotName())){
//
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("description",
//							SearchOperator.OPERATOR_LIKE.value(), votSearchBean
//							.getVotName().toLowerCase(), true));
//			isEmpty = false;
//		}
//
//		if (StringUtils.isNotBlank(votSearchBean.getKumpulanPtjCode())){
//
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("pmOrgProfile.pmOrgValidity.orgCode",
//							SearchOperator.OPERATOR_EQUALS.value(), votSearchBean
//							.getKumpulanPtjCode().toLowerCase(), true));
//			isEmpty = false;
//		}
//
//		if (StringUtils.isNotBlank(votSearchBean.getKumpulanPtjDesc())) {
//
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("pmOrgProfile.pmOrgValidity.orgName",
//							SearchOperator.OPERATOR_LIKE.value(), votSearchBean
//							.getKumpulanPtjDesc().toLowerCase(), true));
//			isEmpty = false;
//		}
//
//
//		if((StringUtils.isNotBlank(votSearchBean.getStatus()) && votSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))) ||
//				(StringUtils.isNotBlank(votSearchBean.getStatus()) && votSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_SUSPENDED))))
//		{
//			if(votSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
//	
//				// Filter By - User expDate
//				searchBean.getDataFilterBeans().add(
//						new SearchMultiFilterBean(new SearchFilterBean(
//								"expDate",
//								SearchOperator.OPERATOR_GREATER.value(),
//								currentDate), new SearchFilterBean(
//										"expDate",
//										SearchOperator.OPERATOR_IS_NULL.value(), null,
//										SearchCondition.CONDITION_OR.value())));
//				
//			}
//			else{
//				// Filter By - User expDate
//				searchBean.getDataFilterBeans().add(new SearchFilterBean(
//						"expDate",
//						SearchOperator.OPERATOR_LESS.value(),
//						currentDate));
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
//			
//		}
//		
//		searchBean.getDataFilterBeans().add(
//				new SearchFilterBean("votFundType",
//						SearchOperator.OPERATOR_NOT_EQUALS.value(),
//						ParameterConst.VOT_FUND_CODE_TYPE));
//		return searchBean;
//	}

	private class SearchVotPagingDataModel extends
	PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public SearchVotPagingDataModel() {
			super("vot",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IVotFundSO votFundSO;

			try {
				//ISearchBean<Object[]> searchBeanData = getSearchBeanData(searchBean);
				VotSearchBean votSearchBean = getManagedBean().getVotSearchBean();
				VotFundReqDTO votReqDTO = new VotFundReqDTO();
				if (StringUtils.isNotBlank(votSearchBean.getVotCode())) {
					votReqDTO.setVotFundCode(votSearchBean.getVotCode());
				}
				
				if (StringUtils.isNotBlank(votSearchBean.getVotName())){
					votReqDTO.setVotFundDesc(votSearchBean.getVotName());
				}

				if (StringUtils.isNotBlank(votSearchBean.getKumpulanPtjCode())){
					votReqDTO.setPtjGroupCode(votSearchBean.getKumpulanPtjCode());
				}

				if (StringUtils.isNotBlank(votSearchBean.getKumpulanPtjDesc())) {
					votReqDTO.setPtjGroupDesc(votSearchBean.getKumpulanPtjDesc());
				}
				
				if(StringUtils.isNotBlank(votSearchBean.getStatus())) {
					votReqDTO.setVotRecordStatus(Integer.parseInt(votSearchBean.getStatus()));
				}

				votReqDTO.setVotFundObjectSearchBean(searchBean);
				votFundSO = (IVotFundSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IVotFundSO.class);
				VotFundResDTO resDTO = votFundSO.searchVotFundData(votReqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {

					return resDTO.getVotObjectResultBean();
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
	public IPagingDataModel<Object[]> getSearchVot() {
		return searchVot;
	}

	/**
	 * @param searchVot the searchVot to set
	 */
	public void setSearchVot(IPagingDataModel<Object[]> searchVot) {
		this.searchVot = searchVot;
	}


}
