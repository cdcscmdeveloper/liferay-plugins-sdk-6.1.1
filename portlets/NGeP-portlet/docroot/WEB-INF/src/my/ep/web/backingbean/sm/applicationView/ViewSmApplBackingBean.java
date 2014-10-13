package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.managedbean.sm.applicationView.ViewSmApplManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "viewSmApplBackingBean")
@RequestScoped
public class ViewSmApplBackingBean extends BaseReviewApplicationBackingBean<ViewSmApplManagedBean> {

	public static Logger log = Logger.getLogger(ViewSmApplBackingBean.class);

	public ViewSmApplBackingBean() {
		
	}

	

}
