package my.ep.web.backingbean.pm.settings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.CalenderSchedulerManageBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "calenderSchedulerBackingBean")
@ViewScoped
public class CalenderSchedulerBackingBean extends BaseBackingBean<CalenderSchedulerManageBean> {
private static Logger log = Logger
	.getLogger(CalenderSchedulerBackingBean.class);
	
}
