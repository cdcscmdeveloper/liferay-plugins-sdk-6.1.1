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
package my.ep.web.util.pm.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * Singleton Helper For Uom.
 * 
 * @author kenny
 * @since 20.02.2019
 * 
 */
public class UomHelper implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = EpLogger.getLogger(UomHelper.class);
	
	/** The instance. */
	private static UomHelper instance;	
	
	/** The uom map. */
	private static Map<Long, PmUom> uomMap = new HashMap<Long, PmUom>();
	
	/**
	 * Get singleton of UomHelper.
	 * 
	 * @return UomHelper singleton
	 */
	public static UomHelper getInstance(){
		if (instance == null){
			instance = new UomHelper();
		}
		return instance;
	}
	
	/**
	 * Get PmUom by uom code.
	 * 
	 * @param uomCode
	 * 			the uom code
	 * @return the PmUom instance
	 */
	public PmUom retrieveUomById(long uomId) {
		debugLogger("retrieveUomById.uomId: " + uomId);
		
		// Get the parameter description list from map.
		PmUom pmUom = uomMap.get(uomId);
		
		debugLogger("pmUom: " + pmUom);
		
		if (pmUom == null) {
			// Get uom from db directly.
			IUomSO uomSO;
			
			try {
				uomSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);
				
				UomReqDTO reqDTO = new UomReqDTO();
				reqDTO.setUomId(uomId);
				
				// Get PmUom from db.
				UomResDTO resDTO = uomSO.retrieveUomByUomId(reqDTO);
				pmUom = resDTO.getPmUom();
				
				// Put uom into map.			   
				uomMap.put(uomId, pmUom);	
				
			} finally {
				uomSO = null;
			}
		}
		return pmUom;
	}
	
	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
}
