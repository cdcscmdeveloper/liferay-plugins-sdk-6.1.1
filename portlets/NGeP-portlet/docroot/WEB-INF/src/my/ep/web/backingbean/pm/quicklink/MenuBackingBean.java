/**
 * 
 */
package my.ep.web.backingbean.pm.quicklink;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.MenuBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.EpPortletUtil;

import org.apache.log4j.Logger;

@ManagedBean
@RequestScoped
public class MenuBackingBean extends BaseBackingBean<BaseManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(MenuBackingBean.class);
	
	private List<MenuBean> menuBeans;
	private String selectedMenu;
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
	
	public List<MenuBean> getMenuBeans() {
		debugLogger("getMenuBeans");
		
		if (menuBeans == null) {
			menuBeans = EpPortletUtil.getMenuBeans();
		}
		
		return menuBeans;
	}
	
	public String getSelectedMenu() {
		if (selectedMenu == null) {
			selectedMenu = new EpPortletUtil().getDisplayLayoutName();
		}
		
		return selectedMenu;
	}
}
