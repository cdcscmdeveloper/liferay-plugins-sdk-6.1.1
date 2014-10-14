package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpServletRequest;

import my.ep.web.managedbean.sm.applicationView.ViewSmApplManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;

import com.liferay.compat.portal.util.PortalUtil;

@ManagedBean(name = "viewSmApplBackingBean")
@RequestScoped
public class ViewSmApplBackingBean extends BaseReviewApplicationBackingBean<ViewSmApplManagedBean> {

	public static Logger log = Logger.getLogger(ViewSmApplBackingBean.class);

	public ViewSmApplBackingBean() {
		if(getManagedBean().getApplicationNoParam()==null){
			initRedirectBackingBeanApplication();
		}
	}

}
