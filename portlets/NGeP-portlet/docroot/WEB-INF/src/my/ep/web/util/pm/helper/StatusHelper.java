/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.pm.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmDataRestrValue;
import my.ep.persistence.entity.pm.authorization.PmScreenRestr;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Singleton Helper For Status.
 * 
 * @author kenny
 * @since 29.12.2013
 * 
 */
public class StatusHelper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	 * @param searchBean
	 * 			the search bean instance
	 * @return list of PmStatusDesc
	 */
	public List<PmStatusDesc> getStatusDescList(String moduleCode, String docType, SearchBean<PmStatusDesc> searchBean) {
		debugLogger("getStatusDescList.moduleCode: " + moduleCode + ", docType: " + docType);
		
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		StringBuilder key = new StringBuilder()
			.append(moduleCode)
			.append(KEY_SEPARATOR)
			.append(docType)
			.append(KEY_SEPARATOR)
			.append(languageCode);
		String searchBeanKey = getSearchBeanKey(searchBean);
		
		if (StringUtils.isNotBlank(searchBeanKey)) {
			key.append(KEY_SEPARATOR);
			key.append(searchBeanKey);
		}
		
		debugLogger("key: " + key);
		
		// Get the status description list from map.
		List<PmStatusDesc> list = statusListMap.get(key.toString());
		
		debugLogger("list: " + list);
		
		if (list == null) {
			IStatusSO statusSO;
			
			try {
				statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setModuleCode(moduleCode);
				reqDTO.setDocType(docType);
				reqDTO.setLanguageCode(languageCode);
				reqDTO.setStatusDescSearchBean(searchBean);
				
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
			    statusListMap.put(key.toString(), list);
				
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
	 * Gets the search bean key.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the search bean key
	 */
	private String getSearchBeanKey(ISearchBean<PmStatusDesc> searchBean) {
		if (searchBean == null)
			return null;
		
		StringBuilder searchBeanKey = new StringBuilder();
		
		for (BaseSearchFilterBean baseSearchFilterBean : searchBean.getDataFilterBeans()) {
			if (baseSearchFilterBean instanceof SearchFilterBean) {
				SearchFilterBean searchFilterBean = (SearchFilterBean)baseSearchFilterBean;
				
				if (searchBeanKey.length() > 0) {
					searchBeanKey.append(KEY_SEPARATOR);
				}
				searchBeanKey.append(searchFilterBean.getSearchField());
				searchBeanKey.append(KEY_SEPARATOR);
				searchBeanKey.append(searchFilterBean.getSearchOperator());
				searchBeanKey.append(KEY_SEPARATOR);
				searchBeanKey.append(searchFilterBean.getSearchValue());
			}
		}
		
		for (PmScreenRestr pmScreenRestr : searchBean.getDataFilterMetadatas()) {
			if (searchBeanKey.length() > 0) {
				searchBeanKey.append(KEY_SEPARATOR);
			}
			
			for (PmDataRestrValue pmDataRestrValue : pmScreenRestr.getPmDataRestrValues()) {
				searchBeanKey.append(pmDataRestrValue.getDataRestrValueId());
				searchBeanKey.append(KEY_SEPARATOR);
			}
			
		}
		
		return searchBeanKey.toString();
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
				statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				
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
