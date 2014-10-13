package my.ep.web.managedbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "viewSmApplMB")
@SessionScoped
public class ViewSmApplManagedBean extends BaseReviewApplicationManagedBean {
	private static final long serialVersionUID = 1L;

	public ViewSmApplManagedBean() {
		super.initMain();	
	}
}
