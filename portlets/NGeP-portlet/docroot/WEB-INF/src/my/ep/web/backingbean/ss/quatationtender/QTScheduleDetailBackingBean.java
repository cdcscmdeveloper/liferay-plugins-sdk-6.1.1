package my.ep.web.backingbean.ss.quatationtender;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
/**
 * @author SudheerKumar
 *
 */
@ManagedBean(name="qtScheduleDetailBB")
@RequestScoped
public class QTScheduleDetailBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean>{
	private static final Logger log=EpLogger.getLogger(QTScheduleDetailBackingBean.class);
    
	public QTScheduleDetailBackingBean(){
	}
	
}
