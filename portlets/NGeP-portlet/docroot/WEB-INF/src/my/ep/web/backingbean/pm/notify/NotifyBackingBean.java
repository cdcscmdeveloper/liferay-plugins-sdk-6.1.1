package my.ep.web.backingbean.pm.notify;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.notify.INotifySO;
import my.ep.dto.pm.common.notify.NotifyReqDTO;
import my.ep.dto.pm.common.notify.NotifyResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmNotifyLog;
import my.ep.persistence.entity.pm.common.PmNotifyUser;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.notify.NotifyManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "notifyBackingBean")
@RequestScoped
public class NotifyBackingBean extends BaseBackingBean<NotifyManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(NotifyBackingBean.class);

	public NotifyBackingBean() {
		if (getManagedBean().isInit()) {
			retrieveNotifyList();
			getManagedBean().setInit(false);
		}
	}

	public void retrieveNotifyList() {
		debugLogger("retrievePmNotifyUserList");

		FacesScopes facesScopes = new FacesScopes();
		PmUser user = facesScopes.getUserSessionBean();

		debugLogger("== retrieveNotifyList, userId[" + user.getUserId() + "]");

		NotifyReqDTO reqDTO = new NotifyReqDTO();
		reqDTO.setNotificationMode("A");
		reqDTO.setUserId(user.getUserId());

		INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", INotifySO.class);
		NotifyResDTO resDTO = reqSO.retrieveNotifyActiveList(reqDTO);

		debugLogger("retrieveNotifyActiveList["
				+ resDTO.getPmNotifyUserList().size() + "]");

		this.getManagedBean().setNotifyLists(resDTO.getPmNotifyUserList());

		resetSelectedNotify();
	}

	public void removeNotifys() {
		debugLogger("removeNotifys");
		NotifyReqDTO reqDTO = new NotifyReqDTO();
		if (this.getManagedBean().getSelectedNotifys() != null) {
			debugLogger("getSelectedNotifys length["
					+ this.getManagedBean().getSelectedNotifys().length + "]");
			INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", INotifySO.class);
			for (PmNotifyUser notifyUser : this.getManagedBean()
					.getSelectedNotifys()) {
				reqDTO.setPmNotifyUser(notifyUser);
				NotifyResDTO resDTO = reqSO.removeNotify(reqDTO);
				debugLogger("Result[" + resDTO.isSuccessful() + "]");
			}
			retrieveNotifyList();
			String message = "";
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Record removed successfully.", message));
		}
	}

	public void onRowSelect(SelectEvent event){
		debugLogger("onRowSelect");
//		PmNotifyUser notifyUser = ((PmNotifyUser) event.getObject());
//		this.getManagedBean().setSelectedNotify(notifyUser);
	}

	private void resetSelectedNotify() {
		PmNotifyUser resetNotify = new PmNotifyUser();
		resetNotify.setPmUser(new PmUser());
		resetNotify.setPmNotifyLog(new PmNotifyLog());
		getManagedBean().setSelectedNotify(resetNotify);
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
}
