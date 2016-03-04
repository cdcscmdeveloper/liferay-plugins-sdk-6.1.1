package my.ep.web.util.pm.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.role.RoleResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

public class RoleHelper {
	private static Logger log = EpLogger.getLogger(RoleHelper.class);
	
	public static final String KEY_SEPARATOR = "_";
	
	private static RoleHelper instance;
	private static Map<String, PmRoleDesc> roleMap = new HashMap<String, PmRoleDesc>();
	private static Map<String, List<PmRoleDesc>> roleListMap = new HashMap<String, List<PmRoleDesc>>();
	
	/**
	 * Get singleton of RoleHelper
	 * 
	 * @return RoleHelper singleton
	 */
	public static RoleHelper getInstance(){
		if (instance == null){
			instance = new RoleHelper();
		}
		return instance;
	}
	
//	/**
//	 * Get a list of role description by org code
//	 * 
//	 * @param orgTypeCode
//	 * 			the Org Type Code
//	 * @return list of PmRoleDesc
//	 */
//	public List<PmRoleDesc> getRoleDescList(String orgTypeCode) {
//		debugLogger("getRoleDescList.orgTypeCode: " + orgTypeCode);
//		
//		String languageCode = new FacesScopes().getUserLocale().getLanguage();
//		String key = new StringBuilder()
//			.append(orgTypeCode)
//			.append(KEY_SEPARATOR)
//			.append(languageCode)
//			.toString();
//		
//		debugLogger("key: " + key);
//		
//		// Get the role description list from map.
//		List<PmRoleDesc> list =roleListMap.get(key);
//		
//		debugLogger("list: " + list);
//		
//		if (list == null) {
//			IRoleSO roleSO;
//			
//			try {
//				roleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
//				
//				RoleReqDTO reqDTO = new RoleReqDTO();
//				reqDTO.setOrgTypeCode(orgTypeCode);
//				reqDTO.setLanguage(languageCode);
//				
//				// Get role description list from db.
//				RoleResDTO resDTO = roleSO.retrieveRoleDecsListByOrgCode(reqDTO);
//				
//			    list = resDTO.getRoleList();
//			    
//			    String roleKey;
//			    for (PmRoleDesc pmRoleDesc : list) {
//			    	roleKey = getRoleKey(
//			    			pmRoleDesc.getPmRole().getRoleCode(), 
//			    			languageCode);
//			    	
//			    	roleMap.put(roleKey, pmRoleDesc);
//				}
//			    
//			    // Put role description list into map.
//			    roleListMap.put(key, list);
//				
//				debugLogger("put into list: " + list);
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new EpException(e.getMessage());
//			} finally {
//				roleSO = null;
//			}
//		}
//		
//		return list;
//	}
	
	/**
	 * Get role description by status id.
	 * 
	 * @param roleCode
	 * 			the role code.
	 * @return PmRoleDesc instance
	 */
	public PmRoleDesc getRoleDescByCode(String roleCode) {
		debugLogger("getRoleDescByCode.roleCode: " + roleCode);
		
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = getRoleKey(roleCode, languageCode)
		.toString();
		
		// Get the role description list from map.
		PmRoleDesc roleDesc = roleMap.get(key);
		
		if (roleDesc == null) {
			IRoleSO roleSO;
			
			try {
				roleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
				
				RoleReqDTO reqDTO = new RoleReqDTO();
				reqDTO.setRoleCode(roleCode);
				reqDTO.setLanguage(languageCode);
				
				// Get role description from db.
				RoleResDTO resDTO = roleSO.retrieveRoleDescByRoleCode(reqDTO);
				
				roleDesc = resDTO.getPmRoleDesc();
			    
			    // Put status description into map.
			    roleMap.put(key, roleDesc);
				
				debugLogger("put into list: " + roleDesc);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				roleSO = null;
			}
		}
		
		return roleDesc;
	}
	
	/**
	 * Get role description by role code and language code.
	 * 
	 * @param roleCode
	 * 			the role code.
	 * @param languageCode
	 * 			the language Code.
	 * @return PmRoleDesc instance
	 */
	public PmRoleDesc getRoleDescByCodeAndLanguage(String roleCode, String languageCode) {
		debugLogger("getRoleDescByCode.roleCode: " + roleCode);
		
		String key = getRoleKey(roleCode, languageCode)
		.toString();
		
		// Get the role description list from map.
		PmRoleDesc roleDesc = roleMap.get(key);
		
		if (roleDesc == null) {
			IRoleSO roleSO;
			
			try {
				roleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
				
				RoleReqDTO reqDTO = new RoleReqDTO();
				reqDTO.setRoleCode(roleCode);
				reqDTO.setLanguage(languageCode);
				
				// Get role description from db.
				RoleResDTO resDTO = roleSO.retrieveRoleDescByRoleCode(reqDTO);
				
				roleDesc = resDTO.getPmRoleDesc();
			    
			    // Put status description into map.
			    roleMap.put(key, roleDesc);
				
				debugLogger("put into list: " + roleDesc);
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			} finally {
				roleSO = null;
			}
		}
		
		return roleDesc;
	}
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
	
	private String getRoleKey(String roleCode, String languageCode) {
		return new StringBuilder()
			.append(roleCode)
			.append(KEY_SEPARATOR)
			.append(languageCode)
			.toString();
	}
}
