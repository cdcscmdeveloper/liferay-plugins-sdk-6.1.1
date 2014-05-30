package my.ep.web.backingbean.pm.notify;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.notify.INotifySO;
import my.ep.dto.pm.common.notify.NotifyReqDTO;
import my.ep.dto.pm.common.notify.NotifyResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmNotifyUser;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.notify.NotifyManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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

		if (user.getUserId() != null) {
			NotifyReqDTO reqDTO = new NotifyReqDTO();
			reqDTO.setNotificationMode("A");
			reqDTO.setUserId(user.getUserId());

			INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", INotifySO.class);
			NotifyResDTO resDTO = reqSO.retrieveNotifyActiveList(reqDTO);

			debugLogger("retrieveNotifyActiveList["
					+ resDTO.getPmNotifyUserList().size() + "]");
			this.getManagedBean().setNotifyLists(resDTO.getPmNotifyUserList());
		}
		resetSelectedNotify();
	}

	public void retrieveUnreadNotifyList() {
		debugLogger("retrieveUnreadNotifyList");

		List<PmNotifyUser> unReadList = new ArrayList<PmNotifyUser>();
		for (PmNotifyUser notifyUser : this.getManagedBean().getNotifyLists()) {
			if (notifyUser.getPmAlert().getIsRead() == 0) {
				unReadList.add(notifyUser);
			}
		}
		this.getManagedBean().setNotifyLists(unReadList);
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
				reqDTO.getPmNotifyUserList().add(notifyUser);
			}
			NotifyResDTO resDTO = reqSO.removeNotifyList(reqDTO);
			debugLogger("NotifyResDTO isSuccessful[" + resDTO.isSuccessful()
					+ "]");
			if (resDTO.isSuccessful()) {
				retrieveNotifyList();
				String message = "";
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record removed successfully.", message));
			}
		}
	}

	public void selectedNotify() {
		debugLogger("=== selectedNotify ===");
		PmNotifyUser selectedNotify = this.getManagedBean().getSelectedNotify();
		debugLogger("IsRead[" + selectedNotify.getPmAlert().getIsRead() + "]");
		if (selectedNotify.getPmAlert().getIsRead() == 0) {
			INotifySO reqSO = (INotifySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", INotifySO.class);
			NotifyReqDTO reqDTO = new NotifyReqDTO();
			reqDTO.setPmNotifyUser(selectedNotify);
			NotifyResDTO resDTO = reqSO.updateIsReadNotify(reqDTO);
			debugLogger("NotifyResDTO updateIsReadNotify["
					+ resDTO.isSuccessful() + "]");
			if (resDTO.isSuccessful()) {
				selectedNotify.getPmAlert().setIsRead(1);
			}
		}
	}

	private void resetSelectedNotify() {
		getManagedBean().setSelectedNotify(null);
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
}
