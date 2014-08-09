package my.ep.web.backingbean.pm.unitofmeasurement;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.unitofmeasurement.UnitOfMeasurementManagedBean;


/**
 * @author DK
 * 
 */
@ManagedBean(name = "unitOfMeasurementBackingBean")
@ViewScoped
public class UnitOfMeasurementBackingBean extends
		BaseBackingBean<UnitOfMeasurementManagedBean> {
	
	private static final Logger log = EpLogger
			.getLogger(UnitOfMeasurementManagedBean.class);
	
	public UnitOfMeasurementBackingBean() {
		log.info("inside constructor");

	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	/**
	 * reload to salutation maintain page
	 * 
	 */	
	public String createMeasurement() {
		debugLogger("createMeasurement");

		return "/xhtml/pm/unitofmeasurement/unitOfMeasurementMaintenance.xhtml";

	}		

}
