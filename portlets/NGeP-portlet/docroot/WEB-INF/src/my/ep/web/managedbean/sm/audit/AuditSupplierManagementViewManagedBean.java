package my.ep.web.managedbean.sm.audit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.logging.LogKey;
import my.ep.web.bean.sm.AuSmSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Firdaus Omar
 * @since 06.08.2024 #28895_35501
 */

@ManagedBean(name = "AuditSupplierManagementViewManagedBean")
@SessionScoped
public class AuditSupplierManagementViewManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private LogKey logKey = new LogKey();

	private int auditTabActiveIndex;
	private AuSmSearchBean auSmSearchBean;
	private boolean isLazy = false;
	private int activeIndex;

	public int getAuditTabActiveIndex() {
		return auditTabActiveIndex;
	}

	public void setAuditTabActiveIndex(int auditTabActiveIndex) {
		this.auditTabActiveIndex = auditTabActiveIndex;
	}

	public AuSmSearchBean getAuSmSearchBean() {
		return auSmSearchBean;
	}

	public void setAuSmSearchBean(AuSmSearchBean auSmSearchBean) {
		this.auSmSearchBean = auSmSearchBean;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

}
