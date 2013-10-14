package my.ep.web.backingbean.ss.quatationtender;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;


@ManagedBean(name="qtBackingBean")
@RequestScoped
public class QTDetailBackingBean extends BaseBackingBean<QTDetailManagedBean>{
	
	private static final String MODULE_NAME="ss";
		

	public void prepareQTDetail(){
		
	}
	
	
	public QTDetailBackingBean(){
		IQTDetailSO iqtDetailSO = (IQTDetailSO)BusinessDelegate.getInstance().getServiceObject(MODULE_NAME, IQTDetailSO.class);
		
		
		
	}
}
