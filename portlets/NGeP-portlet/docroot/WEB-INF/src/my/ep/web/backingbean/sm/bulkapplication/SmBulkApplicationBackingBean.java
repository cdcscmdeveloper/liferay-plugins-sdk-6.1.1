package my.ep.web.backingbean.sm.bulkapplication;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.bulkapplication.SmBulkApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SmBulkApplicationVO;

@ManagedBean(name = "bulkApplicationBackingBean")
@RequestScoped
public class SmBulkApplicationBackingBean extends
		BaseBackingBean<SmBulkApplicationManagedBean> {
	/**
	 * Pre-render bulk application screen
	 */
	public void preRenderBulkApplication() {
		renewSesionObject();
		getManagedBean().setSelectedTasks(new ArrayList<SmBulkApplicationVO>());
		SmCommonUtil.setMDCUserId();
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/bulkApplication/bulkApplicationTaskList.xhtml", "",
				null);
	}

	public void approveBulkApplication() {
		if (getManagedBean().getSelectedTasks() != null
				&& !getManagedBean().getSelectedTasks().isEmpty()) {
			ReviewApplicationReqDTO reqDTO = null;
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			Long pmStatId = null;
			for (SmBulkApplicationVO bulkAppl : getManagedBean()
					.getSelectedTasks()) {
				pmStatId = bulkAppl.getAppl().getPmStatus().getStatusId();
			}
		}
	}

	public void onClaimBpmTask() {
		claimBpmTask();
		refreshTaskList();
	}

	public void onReleaseBpmTask() {
		releaseBpmTask();
		refreshTaskList();
	}

	public void refreshTaskList() {
		this.getManagedBean().getClaimTaskListDataModel().reloadData();
		this.getManagedBean().getUnClaimTaskListDataModel().reloadData();
	}

	public String applicationStatus(Integer docInt) {
		PmStatusDesc pmStatusDesc = StatusHelper.getInstance()
				.getStatusDescById(docInt.longValue());
		return pmStatusDesc != null ? pmStatusDesc.getStatusName() : "Review";
	}
}
