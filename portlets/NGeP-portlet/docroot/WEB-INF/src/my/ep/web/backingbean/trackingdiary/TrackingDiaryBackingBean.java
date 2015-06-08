package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.trackingdiary.TrackingDiaryManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.RoleHelper;

@ManagedBean(name = "trackingDiaryBackingBean")
@RequestScoped
public class TrackingDiaryBackingBean extends
BaseBackingBean<TrackingDiaryManagedBean> {
	private ISearchBean<PmParameterDesc> docTypeSearchBean;
	public static final String SC_TRACKING_DIARY_MANAGED_BEAN = "scTrackingDiaryManagedBean";
	public static final String DP_TRACKING_DIARY_MANAGED_BEAN = "dpTrackingDiaryManagedBean";
	public static final String FL_TRACKING_DIARY_MANAGED_BEAN = "flTrackingDiaryManagedBean";
	public static final String CT_TRACKING_DIARY_MANAGED_BEAN = "ctTrackingDiaryManagedBean";
	public static final String CM_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
	public static final String CD_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
	public static final String SM_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
	public static final String PP_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";

	public ISearchBean<PmParameterDesc> getDocTypeSearchBean() {
		if (this.docTypeSearchBean == null) {
			this.docTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "functionMOD");
		}

		return this.docTypeSearchBean;
	}

	/**
	 * Page navigation 
	 */
	public void onModuleSelect() {
		//			fc.getApplication().getNavigationHandler()
		//			.handleNavigation(fc, null, "taskListView");
		System.out.println("1onModuleSelect" +getManagedBean().getPreviousSelectedModule());


		resetSearchDataModelResult();




		FacesContext fc=FacesContext.getCurrentInstance();
		if(!getManagedBean().getModuleSelected().equalsIgnoreCase(getManagedBean().getPreviousSelectedModule())){
			String previousSelectedModule = getManagedBean().getModuleSelected() + new String("_TRACKING_DIARY_MANAGED_BEAN");
			System.out.println("1onModuleSelect" +getManagedBean().getPreviousSelectedModule());
			getManagedBean().setPreviousSelectedModule(getManagedBean().getModuleSelected());
			if (fc.getExternalContext().getSessionMap().containsKey(SC_TRACKING_DIARY_MANAGED_BEAN)) {
				fc.getExternalContext().getSessionMap().remove(SC_TRACKING_DIARY_MANAGED_BEAN);
				System.out.println("remove ModuleSelect" +SC_TRACKING_DIARY_MANAGED_BEAN);
			}   	 	
			if (fc.getExternalContext().getSessionMap().containsKey(DP_TRACKING_DIARY_MANAGED_BEAN)) {
				fc.getExternalContext().getSessionMap().remove(DP_TRACKING_DIARY_MANAGED_BEAN);
				System.out.println("remove ModuleSelect" +DP_TRACKING_DIARY_MANAGED_BEAN);
			}   	 	
			if (fc.getExternalContext().getSessionMap().containsKey(FL_TRACKING_DIARY_MANAGED_BEAN)) {
				fc.getExternalContext().getSessionMap().remove(FL_TRACKING_DIARY_MANAGED_BEAN);
				System.out.println("remove ModuleSelect" +FL_TRACKING_DIARY_MANAGED_BEAN);
			}   	 	
			if (fc.getExternalContext().getSessionMap().containsKey(CT_TRACKING_DIARY_MANAGED_BEAN)) {
				fc.getExternalContext().getSessionMap().remove(CT_TRACKING_DIARY_MANAGED_BEAN);
				System.out.println("remove ModuleSelect" +CT_TRACKING_DIARY_MANAGED_BEAN);
			}   	 	
			if (fc.getExternalContext().getSessionMap().containsKey(CM_TRACKING_DIARY_MANAGED_BEAN)) {
				fc.getExternalContext().getSessionMap().remove(CM_TRACKING_DIARY_MANAGED_BEAN);
				System.out.println("remove ModuleSelect" +CM_TRACKING_DIARY_MANAGED_BEAN);
			}   	 	
			if (fc.getExternalContext().getSessionMap().containsKey(CD_TRACKING_DIARY_MANAGED_BEAN)) {
				fc.getExternalContext().getSessionMap().remove(CD_TRACKING_DIARY_MANAGED_BEAN);
				System.out.println("remove ModuleSelect" +CD_TRACKING_DIARY_MANAGED_BEAN);
			}   	 	
			if (fc.getExternalContext().getSessionMap().containsKey(SM_TRACKING_DIARY_MANAGED_BEAN)) {
				fc.getExternalContext().getSessionMap().remove(SM_TRACKING_DIARY_MANAGED_BEAN);
				System.out.println("remove ModuleSelect" +SM_TRACKING_DIARY_MANAGED_BEAN);
			}   	 	
			if (fc.getExternalContext().getSessionMap().containsKey(PP_TRACKING_DIARY_MANAGED_BEAN)) {
				fc.getExternalContext().getSessionMap().remove(PP_TRACKING_DIARY_MANAGED_BEAN);
				System.out.println("remove ModuleSelect" +PP_TRACKING_DIARY_MANAGED_BEAN);
			}   	 	
		}else{
			//			 renewSesionObject() ;			getManagedBean().setModuleSelected("");
			getManagedBean().setPreviousSelectedModule(getManagedBean().getModuleSelected());
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "trackingDiary");
			System.out.println("2onModuleSelect"+ getManagedBean().getModuleSelected());
		}
	}	

	/**
	 * pre Render
	 */
	public void preRender() {
		System.out.println("preRender");
		renewSesionObject() ;			

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "trackingDiary");
	}

	public String getUserName(Long userId) {
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		UserResDTO resDTO = new UserResDTO();
		IUserSO trackingDiarySO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		resDTO = trackingDiarySO.retrieveUserByUserId(reqDTO);
		return resDTO.getPmUser().getUserName();
	}

	public PmRoleDesc getRoleDescByCode(String roleCode) {
		RoleHelper roleHelper = new RoleHelper();

		return roleHelper.getRoleDescByCode(roleCode);
	}

	/**
	 * Populate values for Module By drop down.
	 * 
	 * @return an array of Recommend By values
	 */
	public SelectItem[] getModulesDropDown() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> moduleList = new ArrayList<String>();
		moduleList.add(ParameterConst.MODULE_PROCUREMENT_PLAN);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), moduleList));

		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.MODULE_NAME, searchBean);
		return items;
	}



	public void resetSearchDataModelResult(){

		CmTrackingDiaryBackingBean bb = new CmTrackingDiaryBackingBean();
		bb.getManagedBean().reset();

		CdTrackingDiaryBackingBean cd = new CdTrackingDiaryBackingBean();
		cd.getManagedBean().reset();



	}
}

