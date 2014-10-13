package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.web.managedbean.sm.applicationView.ViewSmApplManagedBean;

@ManagedBean(name = "viewSmSuppApplBackingBean")
@RequestScoped
public class ViewSmSuppApplBackingBean extends BaseReviewApplicationBackingBean<ViewSmApplManagedBean>{
	
	public static Logger log = Logger.getLogger(ViewSmSuppApplBackingBean.class);

	public ViewSmSuppApplBackingBean() {
		
	}
}
