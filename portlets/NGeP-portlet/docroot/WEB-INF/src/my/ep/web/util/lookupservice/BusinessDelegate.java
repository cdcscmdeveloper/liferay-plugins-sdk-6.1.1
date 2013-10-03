/**
 * 
 */
package my.ep.web.util.lookupservice;

import my.ep.biz.service.IGenericSO;
//import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author siew mei
 *
 */
public class BusinessDelegate{
	
	private static Logger log = Logger.getLogger(BusinessDelegate.class);
	
	
	private static BusinessDelegate instance;
	//private static EpBusinessDelegate epInstance;
	private BusinessDelegate(){
		
	}
	
	public static BusinessDelegate getInstance(){
		if (instance == null){
			instance = new BusinessDelegate();
		}
		return instance;
	}
	
	public <T extends IGenericSO> T getServiceObject(Class<T> clazz){
		return EpEjbBusinessDelegate.getInstance().getServiceObject(clazz);
	}

	public <T extends IGenericSO> T getServiceObject(String moduleCode, Class<T> clazz){
		return EpEjbBusinessDelegate.getInstance().getServiceObject(moduleCode, clazz);
	}
	
/*	@SuppressWarnings("unchecked")
	public <T extends my.ep.bpm.service.IGenericSO> T getBpmServiceObject(Class<T> clazz){
		return (T)EpBpmBusinessDelegate.getInstance().getServiceObject(clazz);
	}*/
	
	
	
}
