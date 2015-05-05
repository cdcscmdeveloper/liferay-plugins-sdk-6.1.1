package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
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
		FacesContext fc=FacesContext.getCurrentInstance();
		if(getManagedBean().getModuleSelected()==getManagedBean().getPreviousSelectedModule()){
			System.out.println("1onModuleSelect");
//			getManagedBean().setModuleSelected("");
//			if (fc.getExternalContext().getSessionMap().containsKey(SC_TRACKING_DIARY_MANAGED_BEAN)) {
//				fc.getExternalContext().getSessionMap().remove(SC_TRACKING_DIARY_MANAGED_BEAN);
//				System.out.println(SC_TRACKING_DIARY_MANAGED_BEAN);
//			}   	 	
		}else{
			getManagedBean().setPreviousSelectedModule(getManagedBean().getModuleSelected());
			System.out.println("2onModuleSelect");

		}
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
}

