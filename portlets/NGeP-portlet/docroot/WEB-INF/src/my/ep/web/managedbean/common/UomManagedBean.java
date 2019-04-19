/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Common uom Details Managed Bean
 * 
 * @author Mangaiah Garikapati
 * @since 27.02.2018
 */

@ManagedBean(name = "UomDetailManagedBean")
@SessionScoped
public class UomManagedBean extends BaseManagedBean {

private static final long serialVersionUID = 1L;

public static Logger log = EpLogger
.getLogger(UomManagedBean.class);

private String uomCode;
private String uomName;
private IPagingDataModel<PmUom> unitOfMeasurementDataModel;
/**
 * @return the uomCode
 */
public String getUomCode() {
	return uomCode;
}
/**
 * @param uomCode the uomCode to set
 */
public void setUomCode(String uomCode) {
	this.uomCode = uomCode;
}
/**
 * @return the uomName
 */
public String getUomName() {
	return uomName;
}
/**
 * @param uomName the uomName to set
 */
public void setUomName(String uomName) {
	this.uomName = uomName;
}
/**
 * @return the unitOfMeasurementDataModel
 */
public IPagingDataModel<PmUom> getUnitOfMeasurementDataModel() {
	return unitOfMeasurementDataModel;
}
/**
 * @param unitOfMeasurementDataModel the unitOfMeasurementDataModel to set
 */
public void setUnitOfMeasurementDataModel(
		IPagingDataModel<PmUom> unitOfMeasurementDataModel) {
	this.unitOfMeasurementDataModel = unitOfMeasurementDataModel;
} 







}
