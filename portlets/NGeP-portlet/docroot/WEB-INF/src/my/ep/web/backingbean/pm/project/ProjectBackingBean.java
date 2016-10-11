package my.ep.web.backingbean.pm.project;
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
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ProjectSearchBean;
import my.ep.web.managedbean.pm.project.ProjectManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "projectBackingBean")
@ViewScoped
public class ProjectBackingBean extends
BaseBackingBean<ProjectManagedBean> {
	private static Logger log = Logger
			.getLogger(ProjectBackingBean.class);

	public static final String NAV_TO_MAIN = "/xhtml/pm/project/projectSearchMain";

	private IPagingDataModel<Object[]> searchProject = new SearchProjectPagingDataModel();



	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public ProjectBackingBean() {
		log.info("inside ProjectBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}

	}

	public void preLoadingProject() {
		log.info("preLoading ProgramActivity  method");
		this.resetProjectSearchBean();
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
	public void resetProjectSearchBean() {
		debugLogger("resetProjectSearchBean");		
		this.getManagedBean().setLazy(false);
	/*	this.getManagedBean().setVotCode(new String());
		this.getManagedBean().setVotName(new String());
		this.getManagedBean().setProjectCode(new String());
		this.getManagedBean().setProjectDesc(new String());
		this.getManagedBean().setStatus(null);*/
		this.getManagedBean().setProjectSearchBean(new ProjectSearchBean());
		searchProject = new SearchProjectPagingDataModel();
	}

	public void searchProject() {
		searchProject.reloadData();
	}

	public String backToSearch() {

		return NAV_TO_MAIN;
	}

	private ISearchBean<PmSubSetia> getSearchBeanData(
			ISearchBean<PmSubSetia> searchBean) {

		ProjectSearchBean projectSearchBean = this.getManagedBean().getProjectSearchBean();
		
		Date currentDate = new Date();
		if (StringUtils.isNotBlank(projectSearchBean.getVotCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmVotFund.votFundCode",
							SearchOperator.OPERATOR_EQUALS.value(), projectSearchBean
							.getVotCode().toUpperCase() , true));
			

		}
		if (StringUtils.isNotBlank(projectSearchBean.getVotName())){

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmVotFund.description",
							SearchOperator.OPERATOR_LIKE.value(), projectSearchBean
							.getVotName().toUpperCase(), true));
			
		}

		if (StringUtils.isNotBlank(projectSearchBean.getProjectCode())){

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("projectCode",
							SearchOperator.OPERATOR_EQUALS.value(), 
							projectSearchBean.getProjectCode().toUpperCase(), true));
			
		}

		if (StringUtils.isNotBlank(projectSearchBean.getProjectDesc())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("description",
							SearchOperator.OPERATOR_LIKE.value(), projectSearchBean
							.getProjectDesc().toUpperCase(), true));
			
		}



		if((StringUtils.isNotBlank(projectSearchBean.getStatus()) && projectSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))) ||
				(StringUtils.isNotBlank(projectSearchBean.getStatus()) && projectSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_SUSPENDED))))
		{
			if(projectSearchBean.getStatus().equals(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
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
								projectSearchBean.getStatus()));
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
								projectSearchBean.getStatus()));
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

	private class SearchProjectPagingDataModel extends
	PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public SearchProjectPagingDataModel() {
			super("projectSR",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IProjectSetiaSO projectSO;

			try {
				//ISearchBean<PmSubSetia> searchBeanData = getSearchBeanData(searchBean);
				ProjectSetiaReqDTO projectSetiaReqDTO = new ProjectSetiaReqDTO();
				ProjectSearchBean projectSearchBean = getManagedBean().getProjectSearchBean();
			
				if (StringUtils.isNotBlank(projectSearchBean.getVotCode())) {
					projectSetiaReqDTO.setVotFundCode(projectSearchBean.getVotCode());
				}
				if (StringUtils.isNotBlank(projectSearchBean.getVotName())){
					projectSetiaReqDTO.setVotFundDesc(projectSearchBean.getVotName());
				}

				if (StringUtils.isNotBlank(projectSearchBean.getProjectCode())){
					projectSetiaReqDTO.setProjectCode(projectSearchBean.getProjectCode());
				}

				if (StringUtils.isNotBlank(projectSearchBean.getProjectDesc())) {
					projectSetiaReqDTO.setProjectDesc(projectSearchBean.getProjectDesc());
				}

				if(StringUtils.isNotBlank(projectSearchBean.getStatus())){
					projectSetiaReqDTO.setSubSetiaRecordStatus(Integer.parseInt(projectSearchBean.getStatus()));
				} 
				
				projectSetiaReqDTO.setPmSubSetiaObjSearchBean(searchBean);
				projectSO = (IProjectSetiaSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IProjectSetiaSO.class);
				ProjectSetiaResDTO projectSetiResDTO = projectSO.searchProjectData(projectSetiaReqDTO);
				getManagedBean().setLazy(true);
				if (projectSetiResDTO.isSuccessful()) {

					return projectSetiResDTO.getPmSubSetiaObjResultBean();
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
	 * @return the searchProject
	 */
	public IPagingDataModel<Object[]> getSearchProject() {
		return searchProject;
	}

	/**
	 * @param searchProject the searchProject to set
	 */
	public void setSearchProject(IPagingDataModel<Object[]> searchProject) {
		this.searchProject = searchProject;
	}


}
