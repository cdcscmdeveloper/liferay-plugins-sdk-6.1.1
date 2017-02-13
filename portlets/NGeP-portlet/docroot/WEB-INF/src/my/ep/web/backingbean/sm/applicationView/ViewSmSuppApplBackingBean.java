package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.managedbean.sm.applicationView.ViewSmSuppApplManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "viewSmSuppApplBackingBean")
@RequestScoped
public class ViewSmSuppApplBackingBean extends
		BaseReviewApplicationBackingBean<ViewSmSuppApplManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7637031371162055127L;
	public static Logger log = Logger
			.getLogger(ViewSmSuppApplBackingBean.class);

	/**
	 * Initialized and construct ViewSmSuppApplBackingBean
	 */
	public ViewSmSuppApplBackingBean() {
		
	}
}
