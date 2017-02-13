package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.managedbean.sm.applicationView.ViewSmApplManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "viewSmApplBackingBean")
@RequestScoped
public class ViewSmApplBackingBean extends
		BaseReviewApplicationBackingBean<ViewSmApplManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3813287377990129080L;
	public static Logger log = Logger.getLogger(ViewSmApplBackingBean.class);

	/**
	 *  Initialized and construct ViewSmApplBackingBean
	 */
	public ViewSmApplBackingBean() {
		
	}

}
