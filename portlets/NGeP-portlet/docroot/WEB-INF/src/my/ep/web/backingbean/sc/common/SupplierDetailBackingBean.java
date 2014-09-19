/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.common.SupplierDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Common Supplier Detail Backing Bean
 * 
 * @author Kenny
 * @since 05.07.2014
 */
@ManagedBean
@RequestScoped
public class SupplierDetailBackingBean extends SourcingBaseBackingBean<SupplierDetailManagedBean> {
	public static Logger log = EpLogger.getLogger(SupplierDetailBackingBean.class);
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }

	public SupplierDetailBackingBean() {
	}
	
	public void setSmSupplier(SmSupplier smSupplier) {
		debugLogger("setSmSupplier");
		
		SmSupplier selectedSupplier = getManagedBean().getSmSupplier();
		
		if (smSupplier != null && (selectedSupplier == null || smSupplier.getSupplierId().longValue() != selectedSupplier.getSupplierId().longValue())) {
			IQTSupplierSO qtSupplierSO = null;
			try {
				qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSupplierSO.class);
				
				QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
				reqDTO.setSupplierId(smSupplier.getSupplierId());
				
				QTSupplierResDTO resDTO = qtSupplierSO.retrieveSupplierDtlById(reqDTO);
				
				getManagedBean().setSmSupplier(resDTO.getSmSupplier());
				getManagedBean().setMofNo(resDTO.getMofNo());
			} finally {
				qtSupplierSO = null;
			}	
		} else if (smSupplier == null) {
			log.warn("smSupplier is null.");
		} else {
			log.info("show supplier details from session.");
		}
	}
}
