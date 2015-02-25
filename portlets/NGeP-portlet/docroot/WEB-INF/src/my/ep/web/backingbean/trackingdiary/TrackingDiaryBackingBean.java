package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.trackingdiary.TrackingDiaryManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "trackingDiaryBackingBean")
@RequestScoped
public class TrackingDiaryBackingBean extends
		BaseBackingBean<TrackingDiaryManagedBean> {
	private UserReqDTO reqDTO;

	public String getUserName(Long userId) {
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		UserResDTO resDTO = new UserResDTO();
		IUserSO trackingDiarySO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		resDTO = trackingDiarySO.retrieveUserByUserId(reqDTO);
		return resDTO.getPmUser().getUserName();
	}
	
}

