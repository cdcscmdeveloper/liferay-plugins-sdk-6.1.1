package my.ep.web.backingbean.pm.vot;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.VotSearchBean;
import my.ep.web.managedbean.pm.vot.VotManagedBean;
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

	}

	public void preLoadingVot() {
		log.info("preLoading vot method");
		this.resetVotSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * reset search bean fields
	 */
	public void resetVotSearchBean() {

		this.getManagedBean().setVotSearchBean(new VotSearchBean());
		searchVot = new SearchVotPagingDataModel();
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
		if (votSearchBean.getVotCode() != null
				&& votSearchBean.getVotCode() .trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("ctrlOfficerCode",
							SearchOperator.OPERATOR_LIKE.value(), votSearchBean
									.getVotCode() , true));
			isEmpty = false;
			
		}
		if (votSearchBean.getVotName() != null
				&& votSearchBean.getVotName().trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("description",
							SearchOperator.OPERATOR_LIKE.value(), votSearchBean
									.getVotName(), true));
			isEmpty = false;
		}
		if(votSearchBean.getStatus() != null
				&& !"".equals(votSearchBean.getStatus())) {
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						votSearchBean.getStatus()));
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
			PagingDataModel<PmVotFund> {
		private static final long serialVersionUID = 1L;

		public SearchVotPagingDataModel() {
			super("searchVot");
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
