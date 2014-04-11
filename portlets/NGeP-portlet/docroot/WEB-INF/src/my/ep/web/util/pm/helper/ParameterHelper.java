/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.pm.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Singleton Helper For Parameter.
 * 
 * @author kenny
 * @since 29.12.2013
 * 
 */
public class ParameterHelper {
	private static Logger log = EpLogger.getLogger(ParameterHelper.class);
	
	public static final String KEY_SEPARATOR = "_";
	
	private static ParameterHelper instance;	
	private static Map<String, List<PmParameterDesc>> parameterMap = new HashMap<String, List<PmParameterDesc>>();
	
	/**
	 * Get singleton of ParameterHelper
	 * 
	 * @return ParameterHelper singleton
	 */
	public static ParameterHelper getInstance(){
		if (instance == null){
			instance = new ParameterHelper();
		}
		return instance;
	}
	
	/**
	 * Get a list of parameter description by parameter type
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @return list of PmParameterDesc
	 */
	public List<PmParameterDesc> getParameterDescList(String parameterType) {
		return getParameterDescList(parameterType, null);
	}
	
	/**
	 * Get a list of parameter description by parameter type and search bean.
	 * 
	 * @param parameterType
	 * 			the parameter type
	 * @param searchBean
	 * 			the searchBean instance
	 * @return list of PmParameterDesc
	 */
	public List<PmParameterDesc> getParameterDescList(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		debugLogger("getParameterDescList.parameterType: " + parameterType);
		
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = new StringBuilder()
			.append(parameterType)
			.append(KEY_SEPARATOR)
			.append(languageCode)
			.toString();
		
		debugLogger("key: " + key);
		
		// Get the parameter description list from map.
		List<PmParameterDesc> list = parameterMap.get(key);
		
		debugLogger("list: " + list);
		
		if (list == null) {
			IParameterSO parameterSO;
			
			try {
				parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterDescSearchBean(searchBean);
				reqDTO.setParameterType(parameterType);
				reqDTO.setLanguageCode(languageCode);
				
				// Get parameter description list from db.
				ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
				
			    list = resDTO.getParameterDescList();
			    
			    // Put parameter description list into map.
				parameterMap.put(key, list);
				
				debugLogger("put into list: " + list);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				parameterSO = null;
			}
		}
		
		return list;
	}
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
}
