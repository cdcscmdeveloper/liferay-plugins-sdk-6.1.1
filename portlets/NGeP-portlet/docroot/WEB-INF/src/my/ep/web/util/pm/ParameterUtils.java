package my.ep.web.util.pm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;


public class ParameterUtils {
	private static Logger log = EpLogger.getLogger(ParameterUtils.class);
	
	public static final String KEY_SEPARATOR = "_";
	
	private static Map<String, List<PmParameterDesc>> parameterMap = new HashMap<String, List<PmParameterDesc>>();
	
	public List<PmParameterDesc> getParameterDescList(String parameterType) {
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
				parameterSO = BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
				
				ParameterReqDTO reqDTO = new ParameterReqDTO();
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
