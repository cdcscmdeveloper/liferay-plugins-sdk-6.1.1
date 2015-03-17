package my.ep.web.backingbean.pm.ctrlOfficer;

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
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmAgCtrlOfficer;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ControllerOfficerSearchBean;
import my.ep.web.managedbean.pm.ctrlOfficer.ContrilOfficerManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "ctrlBackingBean")
@ViewScoped
public class ControlOfficerBackingBean extends
		BaseBackingBean<ContrilOfficerManagedBean> {
	private static Logger log = Logger
			.getLogger(ControlOfficerBackingBean.class);
	
	public static final String NAV_TO_MAIN = "/xhtml/pm/controlingofficer/controlOfficerSearchMain";

	private IPagingDataModel<PmAgCtrlOfficer> searchCtrOfficer = new SearchCtrlPagingDataModel();

	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public ControlOfficerBackingBean() {

	}

	public void preLoadingControlOfficer() {
		log.info("preLoading control method");
		this.resetCtrlSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * reset search bean fields
	 */
	public void resetCtrlSearchBean() {

		this.getManagedBean().setControllerSearchBean(new ControllerOfficerSearchBean());
		searchCtrOfficer = new SearchCtrlPagingDataModel();
	}

	public void searchCtrOfficer() {
		searchCtrOfficer.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	private ISearchBean<PmAgCtrlOfficer> getSearchBeanData(
			ISearchBean<PmAgCtrlOfficer> searchBean) {

		ControllerOfficerSearchBean ctrlSearchBean = this.getManagedBean().getControllerSearchBean();
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		if (ctrlSearchBean.getCtrlCode() != null
				&& ctrlSearchBean.getCtrlCode() .trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("ctrlOfficerCode",
							SearchOperator.OPERATOR_LIKE.value(), ctrlSearchBean
									.getCtrlCode() , true));
			isEmpty = false;
		}
		if (ctrlSearchBean.getCtrlDescription() != null
				&& ctrlSearchBean.getCtrlDescription().trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("description",
							SearchOperator.OPERATOR_LIKE.value(), ctrlSearchBean
									.getCtrlDescription(), true));
			isEmpty = false;
		}
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						ctrlSearchBean.getStatus()));
		return searchBean;
	}

	private class SearchCtrlPagingDataModel extends
			PagingDataModel<PmAgCtrlOfficer> {
		private static final long serialVersionUID = 1L;

		public SearchCtrlPagingDataModel() {
			super("searchCtrOfficer");
		}

		@Override
		public ISearchResultBean<PmAgCtrlOfficer> pageChangeListener(
				ISearchBean<PmAgCtrlOfficer> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IAgOfficeSO agOfficeSO;

			try {
				ISearchBean<PmAgCtrlOfficer> searchBeanData = getSearchBeanData(searchBean);
				AgOfficeReqDTO ctrReqDTO = new AgOfficeReqDTO();
				ctrReqDTO.setCtrlOfficerSearchBean(searchBeanData);
				agOfficeSO = (IAgOfficeSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IAgOfficeSO.class);
				AgOfficeResDTO resDTO = agOfficeSO.searchAgOfficerData(ctrReqDTO);
						
				if (resDTO.isSuccessful()) {

					return resDTO.getCtrlOfficerResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptions();
	}

	/**
	 * @return the searchCtrOfficer
	 */
	public IPagingDataModel<PmAgCtrlOfficer> getSearchCtrOfficer() {
		return searchCtrOfficer;
	}

	/**
	 * @param searchCtrOfficer the searchCtrOfficer to set
	 */
	public void setSearchCtrOfficer(IPagingDataModel<PmAgCtrlOfficer> searchCtrOfficer) {
		this.searchCtrOfficer = searchCtrOfficer;
	}

	

}
