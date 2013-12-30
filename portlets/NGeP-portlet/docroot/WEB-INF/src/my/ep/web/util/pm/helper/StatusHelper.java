/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.pm.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Singleton Helper For Status.
 * 
 * @author kenny
 * @since 29.12.2013
 * 
 */
public class StatusHelper {
	private static Logger log = EpLogger.getLogger(StatusHelper.class);
	
	public static final String KEY_SEPARATOR = "_";
	
	private static StatusHelper instance;
	private static Map<String, PmStatusDesc> statusMap = new HashMap<String, PmStatusDesc>();
	private static Map<String, List<PmStatusDesc>> statusListMap = new HashMap<String, List<PmStatusDesc>>();
	
	/**
	 * Get singleton of StatusHelper
	 * 
	 * @return StatusHelper singleton
	 */
	public static StatusHelper getInstance(){
		if (instance == null){
			instance = new StatusHelper();
		}
		return instance;
	}
	
	/**
	 * Get a list of status description by module and doc type
	 * 
	 * @param moduleCode
	 * 			the module code
	 * @param docType
	 * 			the document type
	 * @return list of PmStatusDesc
	 */
	public List<PmStatusDesc> getStatusDescList(String moduleCode, String docType) {
		debugLogger("getStatusDescList.moduleCode: " + moduleCode + ", docType: " + docType);
		
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = new StringBuilder()
			.append(moduleCode)
			.append(KEY_SEPARATOR)
			.append(docType)
			.append(KEY_SEPARATOR)
			.append(languageCode)
			.toString();
		
		debugLogger("key: " + key);
		
		// Get the status description list from map.
		List<PmStatusDesc> list = statusListMap.get(key);
		
		debugLogger("list: " + list);
		
		if (list == null) {
			IStatusSO statusSO;
			
			try {
				statusSO = BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);
				
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setModuleCode(moduleCode);
				reqDTO.setDocType(docType);
				reqDTO.setLanguageCode(languageCode);
				
				// Get status description list from db.
				StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
				
			    list = resDTO.getStatusDescList();
			    
			    String statusKey;
			    for (PmStatusDesc pmStatusDesc : list) {
			    	statusKey = getStatusKey(
			    			pmStatusDesc.getPmStatus().getStatusId(), 
			    			languageCode);
			    	
			    	statusMap.put(statusKey, pmStatusDesc);
				}
			    
			    // Put status description list into map.
			    statusListMap.put(key, list);
				
				debugLogger("put into list: " + list);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				statusSO = null;
			}
		}
		
		return list;
	}
	
	/**
	 * Get Status description by status id.
	 * 
	 * @param statusId
	 * 			the status id.
	 * @return PmStatusDesc instance
	 */
	public PmStatusDesc getStatusDescById(long statusId) {
		debugLogger("getStatusDescById.statusId: " + statusId);
		
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = getStatusKey(statusId, languageCode)
		.toString();
		
		// Get the status description list from map.
		PmStatusDesc statusDesc = statusMap.get(key);
		
		if (statusDesc == null) {
			IStatusSO statusSO;
			
			try {
				statusSO = BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);
				
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setStatusId(statusId);
				reqDTO.setLanguageCode(languageCode);
				
				// Get status description from db.
				StatusResDTO resDTO = statusSO.retrieveStatusDescById(reqDTO);
				
				statusDesc = resDTO.getPmStatusDesc();
			    
			    // Put status description into map.
			    statusMap.put(key, statusDesc);
				
				debugLogger("put into list: " + statusDesc);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				statusSO = null;
			}
		}
		
		return statusDesc;
	}
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
	
	private String getStatusKey(long statusId, String languageCode) {
		return new StringBuilder()
			.append(statusId)
			.append(KEY_SEPARATOR)
			.append(languageCode)
			.toString();
	}
}
