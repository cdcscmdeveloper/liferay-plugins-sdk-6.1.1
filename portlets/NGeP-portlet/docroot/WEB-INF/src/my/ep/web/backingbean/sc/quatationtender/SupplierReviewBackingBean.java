/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.quatationtender;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;

import org.apache.log4j.Logger;

/**
 * Backing Bean For Supplier Review
 * 
 * @author kenny
 * @since 18.12.2013
 * 
 */
@ManagedBean
@RequestScoped
public class SupplierReviewBackingBean  extends SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log = EpLogger.getLogger(SupplierReviewBackingBean.class);
	
	public SupplierReviewBackingBean(){
		debugLogger("SupplierReviewBackingBean");
		
		ScQt scQt = getManagedBean().getQt();
		if (scQt == null) {
			scQt = new ScQt();
			getManagedBean().setQt(scQt);
			//getManagedBean().setQtCriteria(new ScQtCriteria());
		}
	}
			
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}
}
