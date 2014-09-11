/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.pm.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmDataRestrValue;
import my.ep.persistence.entity.pm.authorization.PmScreenRestr;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
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
	
	public static final int MAP_VALUE_PARAMETER_DESC_ID = 0;
	public static final int MAP_VALUE_PARAMETER_CODE = 1;
	
	private static ParameterHelper instance;	
	private static Map<String, String[][]> parameterMap = new HashMap<String, String[][]>();
	
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
		return getParameterDescListBySearchBean(parameterType, null);
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
	public List<PmParameterDesc> getParameterDescListBySearchBean(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		debugLogger("getParameterDescList.parameterType: " + parameterType);
		
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		StringBuilder key = new StringBuilder()
			.append(parameterType)
			.append(KEY_SEPARATOR)
			.append(languageCode);
		String searchBeanKey = getSearchBeanKey(searchBean);
		
		if (StringUtils.isNotBlank(searchBeanKey)) {
			key.append(KEY_SEPARATOR);
			key.append(searchBeanKey);
		}
		
		debugLogger("key: " + key);
		
		// Get the parameter description list from map.
		String[][] paramDescArry = parameterMap.get(key.toString());
		List<PmParameterDesc> list = null;
		
		debugLogger("paramDescArry: " + paramDescArry);
		
		if (paramDescArry == null) {
			// Get parameter description list from db directly.
			list = getParameterDescFromDB(key.toString(), languageCode, parameterType, searchBean);
		} else {				
			// Get parameter description list from jpa cache.
			list = getParameterDescFromCache(paramDescArry[MAP_VALUE_PARAMETER_DESC_ID]);
		}
		
		return list;
	}
	
	/**
	 * Get parameter description by parameter type and code.
	 * 
	 * @param parameterType
	 * 		the parameter type
	 * @param parameterCode
	 * 		the parameter code
	 * @return the parameter description
	 */
	public String getParameterDesc(String parameterType, String parameterCode) {
		debugLogger("getParameterDescList.parameterType: " + parameterType + ", parameterCode: " + parameterCode);
		
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		StringBuilder key = new StringBuilder()
			.append(parameterType)
			.append(KEY_SEPARATOR)
			.append(languageCode);
		
		debugLogger("key: " + key);
		
		// Get the parameter description list from map.
		String[][] paramDescArry = parameterMap.get(key.toString());
		
		if (paramDescArry == null) {
			// Get parameter description list from db directly.
			List<PmParameterDesc> list = getParameterDescFromDB(key.toString(), languageCode, parameterType, null);
			
			for (PmParameterDesc pmParameterDesc : list) {
				// compare matched parameterCode.
				if (pmParameterDesc.getPmParameter().getParameterCode()
						.equals(parameterCode)) {
					return pmParameterDesc.getCodeName();
				}
			}
			
		} else {
			// Get parameter description list from jpa cache.
			int i = 0;
			String[] parameterDescIds = new String[1];
			
			for (String tmpParameterCode : paramDescArry[MAP_VALUE_PARAMETER_CODE]) {
				// compare matched parameterCode.
				if (tmpParameterCode.equals(parameterCode)) {
					parameterDescIds[0] = paramDescArry[MAP_VALUE_PARAMETER_DESC_ID][i];
					break;
				}
				
				i++;
			}
			
			if (StringUtils.isNotBlank(parameterDescIds[0])) {
				List<PmParameterDesc> list = getParameterDescFromCache(parameterDescIds);
				
				if (list.size() > 0) {
					return list.get(0).getCodeName();
				}
			}
		}
		
		return null;
	}
	
	private String getSearchBeanKey(ISearchBean<PmParameterDesc> searchBean) {
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
	
	private List<PmParameterDesc> getParameterDescFromDB(String key, String languageCode, String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		IParameterSO parameterSO;
		List<PmParameterDesc> list = null;
		
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			
			reqDTO.setParameterDescSearchBean(searchBean);
			reqDTO.setParameterType(parameterType);
			reqDTO.setLanguageCode(languageCode);
			
			// Get parameter description list from db.
			ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
			
		    list = resDTO.getParameterDescList();
		    
		    // Only cache the parameter description without data filter.
		    if (searchBean == null || searchBean.getDataFilterMetadatas().size() == 0) {
		    	int i = 0;
		    	
		    	String[][] paramDescArry = new String[MAP_VALUE_PARAMETER_CODE+1][list.size()];
		    	
		    	for (PmParameterDesc parameterDesc : list) {
		    		paramDescArry[MAP_VALUE_PARAMETER_DESC_ID][i] = parameterDesc.getParameterDescId().toString();
		    		paramDescArry[MAP_VALUE_PARAMETER_CODE][i] = parameterDesc.getPmParameter().getParameterCode();
		    		
		    		i++;
		    	}
		    	
		    	// Put parameter description list into map.			   
				parameterMap.put(key, paramDescArry);	
		    }
		    
		    debugLogger("put into list: " + list);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			parameterSO = null;
		}
		
		return list;
	}
	
	private List<PmParameterDesc> getParameterDescFromCache(String[] parameterDescIds) {
		IParameterSO parameterSO;
		List<PmParameterDesc> list = null;
		
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			
			reqDTO.setParameterDescIds(parameterDescIds);
			
			// Get parameter description list from jpa cache.
			ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByDescIds(reqDTO);
			
		    list = resDTO.getParameterDescList();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			parameterSO = null;
		}
		
		return list;
	}
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
}
