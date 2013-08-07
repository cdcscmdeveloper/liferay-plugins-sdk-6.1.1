package my.ep.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.aut.mergerules.IMergeRulesSO;
import my.ep.type.aut.ScreenAuthority;
import my.ep.dto.aut.mergerules.MergeRulesReqDTO;
import my.ep.dto.aut.mergerules.MergeRulesResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.aut.AutScreenMetadata;
import my.ep.persistence.entity.aut.AutUserRoleScreenMetadata;
import my.ep.web.bean.SecurityFiltersBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SecurityFilterUtil {
	private static Logger log = EpLogger.getLogger(SecurityFilterUtil.class);
	
	private static final String SESS_SECURITY_FILTERS_BEAN = "securityFiltersBean";
	
	private static void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
	
	/**
	 * apply security filters if filters exist. 
	 *  
     * @param context
     *              FacesContext of the current view. 
     * @param userId
     *              user Id.
     * @param screenId
     *              screen id of the current view.
	 */
	public static void applyScreenFilters(UIViewRoot viewroot, String userId, String screenId) {
		debugLogger(new StringBuilder().append("applyScreenFilters[")
				.append("userId:")
				.append(userId)
				.append(", screenId:")
				.append(screenId)
				.append("]"));
		
		FacesScopes facesScopes = new FacesScopes();        
        SecurityFiltersBean securityFiltersBean = getScreenFilters(userId, screenId);
        Map<String, Map<String, String>> authScreenFilters = securityFiltersBean.getAuthScreenFilters();        
        HashMap<String, String> compMap = (HashMap<String, String>) authScreenFilters.get(screenId);
        
        if(compMap != null && !compMap.isEmpty()) {            
            for(String key : compMap.keySet()) {
                UIComponent component = facesScopes.findComponentById(viewroot, key);                
                if(component != null) {
                    mergeBizRules(compMap, component);
                }
                
            }
        }
	}
	
	/**
	 * apply data filters if filters exist. 
	 * 
     * @param userId
     *              user Id.
     * @param screenId
     *              screen id of the current view.
	 */
	public static void applyDataFilters(String userId, String screenId) {
		debugLogger(new StringBuilder().append("applyDataFilters[")
				.append("userId:")
				.append(userId)
				.append(", screenId:")
				.append(screenId)
				.append("]"));
		
		FacesScopes facesScope = new FacesScopes();
		SecurityFiltersBean securityFiltersBean = getSecurityFiltersBean(facesScope);
		
		// Make sure userId and screenId is not empty
		if(!StringUtils.isBlank(userId)) {
			if(!StringUtils.isBlank(screenId)) {
				Map<String, Map<String, List<AutScreenMetadata>>> authDataFilters = securityFiltersBean.getAuthDataFilters();
				if (!authDataFilters.containsKey(screenId)) {
					IMergeRulesSO mergeRuleSO = null;
					try {
						mergeRuleSO = (IMergeRulesSO)BusinessDelegate.getInstance().getServiceObject(IMergeRulesSO.class);
						
						MergeRulesReqDTO requestDTO = new MergeRulesReqDTO();
						requestDTO.setScreenId(screenId);
						requestDTO.setUserId(userId);
						
						MergeRulesResDTO mergeRulesResDTO = mergeRuleSO.getAuthDataFilters(requestDTO);
						List<AutScreenMetadata> screenMetadatas = mergeRulesResDTO.getScreenMetadatas();
						Map<String, List<AutScreenMetadata>> screenMetadataMap = new HashMap<String, List<AutScreenMetadata>>();;
						
						if (screenMetadatas != null) {
							String componentId;
							List<AutScreenMetadata> componentScreenMetadatas;
							
							for (AutScreenMetadata screenMetadata : screenMetadatas) {
								componentId = screenMetadata.getFilterObjectId();
								
								if (!StringUtils.isBlank(componentId)) {
									componentScreenMetadatas = screenMetadataMap.get(componentId);
									
									// Check based on component id (ScreenFilterObject) to create a new List
									if (componentScreenMetadatas == null) {
										componentScreenMetadatas = new ArrayList<AutScreenMetadata>();
										
										// Put to the map with the key as component id (ScreenFilterObject)
										screenMetadataMap.put(componentId , componentScreenMetadatas);
									}
									
									debugLogger(new StringBuilder().append("componentId:")
											.append(componentId)
											.append(", filterDataType:")
											.append(screenMetadata.getFilterDataType())
											.append(", filterDataSql:")
											.append(screenMetadata.getFilterDataSql()));
									
									// Put to the list
									componentScreenMetadatas.add(screenMetadata);
								}
							}
						}
						
						// Put to the map with the key as screen id in securityFiltersBean
						securityFiltersBean.getAuthDataFilters().put(screenId, screenMetadataMap);
						
					} finally {
						mergeRuleSO = null;
					}			
				}
			}
		}
	}
	
	/**
	 * This will return list of AutScreenMetadata based on current screen id and component id for Data Filters 
	 * 
	 * @param componentId
	 * 			the component id.
	 * @return List of AutScreenMetadata
	 */
	public static List<AutScreenMetadata> getDataFilterScreenMetadatas(String componentId) {
		FacesScopes facesScopes = new FacesScopes();
		String screenId = EpPortletUtil.getDisplayPortletId();
		SecurityFiltersBean securityFiltersBean = getSecurityFiltersBean(facesScopes);
		
		if (!StringUtils.isBlank(screenId) && !StringUtils.isBlank(componentId)) {
			Map<String, List<AutScreenMetadata>> screenMetadatasMap = securityFiltersBean.getAuthDataFilters().get(screenId);
			if (screenMetadatasMap != null) {
				List<AutScreenMetadata> screenMetadatas = screenMetadatasMap.get(componentId);
				if (screenMetadatas != null) {
					return screenMetadatas;
				}
			}
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * Retrieve user's screen filters and store into session if not retrieved yet.
	 * 
	 * @param userId
     *              user Id.
	 * @param screenId
	 *             screen id of the current view.
	 *             
	 * @return SecurityFiltersBean
	 */
	private static SecurityFiltersBean getScreenFilters(String userId, String screenId) {
		FacesScopes facesScope = new FacesScopes();
		SecurityFiltersBean securityFiltersBean = getSecurityFiltersBean(facesScope);
		
		if(!StringUtils.isBlank(userId)) {
			if(!StringUtils.isBlank(screenId)) {
				HashMap<String, Map<String, String>> authScreenFilters = securityFiltersBean.getAuthScreenFilters();				
				if (!authScreenFilters.containsKey(screenId)) {
					IMergeRulesSO mergeRuleSO = null;
					try {
						mergeRuleSO = (IMergeRulesSO)BusinessDelegate.getInstance().getServiceObject(IMergeRulesSO.class);
						
						MergeRulesReqDTO requestDTO = new MergeRulesReqDTO();
						requestDTO.setScreenId(screenId);
						requestDTO.setUserId(userId);
						System.out.println("mergeRuleSO: >>>>>>>>>>>>>>>>>>>>>>>>>>.." + mergeRuleSO);
						MergeRulesResDTO mergeRulesResDTO = mergeRuleSO.getAuthScreenFilters(requestDTO);
						List<AutUserRoleScreenMetadata> userRoleScreenMetadatas = mergeRulesResDTO.getUserRoleScreenMetadatas();
			
						AutScreenMetadata screenMetadata;
						HashMap<String, String> compMap = new HashMap<String, String>();
						if(userRoleScreenMetadatas != null) {
							for(AutUserRoleScreenMetadata userRoleScreenMetadata : userRoleScreenMetadatas) {
								screenMetadata = userRoleScreenMetadata.getAutFuncAreaScreenMetadata().getAutScreenMetadata();
								if(screenMetadata != null) {
									debugLogger(new StringBuilder().append("filterObjectId:")
											.append(screenMetadata.getFilterObjectId())
											.append(", screenAuth:")
											.append(userRoleScreenMetadata.getScreenAuth()));
									
									compMap.put(screenMetadata.getFilterObjectId(), 
											userRoleScreenMetadata.getScreenAuth());
								}
							}
						}
						
						authScreenFilters.put(screenId, compMap);
						
					} finally {
						mergeRuleSO = null;
					}
				}
			}
		}
		
		return securityFiltersBean;
	}
	
	/**
	 * set component's attribute to hide / disabled based on configured screen filter. 
	 * 
	 * @param componentMap
	 *             component map that are containing the component id & filter. 
	 * @param component
	 *             target component that are applying screen filter. 
	 * 
	 * @return
	 */
	private static void mergeBizRules(HashMap<String, String> componentMap, UIComponent component) {
		
		if(componentMap.containsKey(component.getId())) {
			String screenAuth = componentMap.get(component.getId());
			
			if(ScreenAuthority.DISABLED.value().equals(screenAuth)) {
			    disableAllParentNChildren(component);
			}
			if(ScreenAuthority.HIDDEN.value().equals(screenAuth)) {
				if(component.getChildren().size() > 0) {
					for(UIComponent child : component.getChildren()) {
						debugLogger("HIDDEN-child:" + child.getClientId());
						
						child.setRendered(false);
					}
				}
				
				debugLogger("HIDDEN-component:" + component.getClientId());
				
				component.setRendered(false);
			}
		}
	}
	
	/**
	 * disable all the children of the disabled component.
	 *  
	 * @param parentComponent
	 *             component that are having disabled filter, and need disable all children.
	 *             
	 * @return
	 */
    private static void disableAllParentNChildren(UIComponent parentComponent) {
        
        if(parentComponent != null) {
            
            ValueExpression disabledVE = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
                                                   .createValueExpression(FacesContext.getCurrentInstance().getELContext(), 
                                                           "true", Boolean.class);
            
            for(UIComponent component : parentComponent.getChildren()) {
            
                if(component.getChildCount() > 0) {
                
                    disableAllParentNChildren(component);
                
                } else {
                	debugLogger("DISABLED-component:" + component.getClientId());
                	
                	component.setValueExpression("disabled", disabledVE);
                }
            }
            try {
            	debugLogger("DISABLED-parentComponent:" + parentComponent.getClientId());
            	
            	parentComponent.setValueExpression("disabled", disabledVE);
            } catch(Exception e) {
                log.error(parentComponent + " " + e);
            }
        }
    }
	
	/**
	 * retrieve user's security filter bean from session.
	 * 
	 * @param facesScope
	 *             FacesScopes of the current view.
	 *              
	 * @return SecurityFiltersBean
	 */
	private static SecurityFiltersBean getSecurityFiltersBean(FacesScopes facesScope) {
		SecurityFiltersBean securityFiltersBean =
			(SecurityFiltersBean) facesScope.getSessionScope(SESS_SECURITY_FILTERS_BEAN);
		
		if (securityFiltersBean == null) {
			securityFiltersBean = new SecurityFiltersBean();
			
			facesScope.setSessionScope(SESS_SECURITY_FILTERS_BEAN, securityFiltersBean);
		}
		
		return securityFiltersBean;
	}
}
