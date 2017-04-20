/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */

package my.ep.web.backingbean.report.qt;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.managedbean.report.qt.QtReportManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class QtReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
public class QtReportBackingBean <T extends QtReportManagedBean>
extends ReportBaseBackingBean<T> {
private static final long serialVersionUID = 1L;
private static final Logger log = EpLogger
	.getLogger(QtReportBackingBean.class);


public void reportOnChange() {
	if (!StringUtils.isBlank(getManagedBean().getReportCode())) {
		retrieveReportValueFromDb();
	}
}

}
