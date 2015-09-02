package my.ep.web.backingbean.sm.bulkapplication;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.managedbean.sm.bulkapplication.SmBulkApplicationManagedBean;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SmBulkApplicationVO;

@ManagedBean(name = "bulkApplicationBackingBean")
@RequestScoped
public class SmBulkApplicationBackingBean extends
		BaseReviewApplicationBackingBean<SmBulkApplicationManagedBean> {
	/**
	 * Pre-render bulk application screen
	 */
	public void preRenderBulkApplication() {
		renewSesionObject();
		getManagedBean().setSelectedTasks(new ArrayList<SmBulkApplicationVO>());
		SmCommonUtil.setMDCUserId();
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/bulkApplication/bulkApplicationTaskList.xhtml", "");
	}

	public void approveBulkApplication() {
		if(getManagedBean().get)
	}

	public void refreshTaskList() {
		this.getManagedBean().getTaskListDataModel().reloadData();
	}

	public String applicationStatus(Integer docInt) {
		PmStatusDesc pmStatusDesc = StatusHelper.getInstance()
				.getStatusDescById(docInt.longValue());
		return pmStatusDesc != null ? pmStatusDesc.getStatusName() : "Review";
	}
}
