package my.ep.web.backingbean.pm.settings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.CalenderSchedulerManageBean;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "calenderSchedulerBackingBean")
@ViewScoped
public class CalenderSchedulerBackingBean extends BaseBackingBean<CalenderSchedulerManageBean> {
private static Logger log = Logger
	.getLogger(CalenderSchedulerBackingBean.class);


/**
 * get state list by country id
 * 
 * @param countryId
 * @return List<PmState>
 */
public List<PmState> getStateListOfMalaysia() {
	log.info("getStateList");
	return PmCommonUtil.retrievePmStateList(131L);//countryId for Malaysia = 131
}
	
}
