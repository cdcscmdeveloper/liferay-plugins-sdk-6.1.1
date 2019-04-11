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

import my.ep.biz.service.pm.authorization.screenrestriction.IScreenRestrSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.authorization.screenrestriction.ScreenRestrReqDTO;
import my.ep.dto.authorization.screenrestriction.ScreenRestrResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmFieldRestrValue;
import my.ep.persistence.entity.pm.authorization.PmScreenRestr;
import my.ep.type.aut.ScreenAuthority;
import my.ep.web.bean.SecurityFiltersBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SecurityFilterUtil {
	private static Logger log = EpLogger.getLogger(SecurityFilterUtil.class);

	private static final String SESS_SECURITY_FILTERS_BEAN = "securityFiltersBean";

	private static void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	/**
	 * apply security filters if filters exist.
	 * 
	 * @param context
	 *            FacesContext of the current view.
	 * @param userId
	 *            user Id.
	 * @param screenId
	 *            screen id of the current view.
	 */

	public static void applyScreenFilters(UIViewRoot viewroot, Long userId,
			String screenId) {
		debugLogger(new StringBuilder().append("applyScreenFilters[")
				.append("userId:").append(userId).append(", screenId:")
				.append(screenId).append("]"));

		FacesScopes facesScopes = new FacesScopes();
		SecurityFiltersBean securityFiltersBean = getScreenFilters(userId,
				screenId);
		Map<String, Map<String, String>> authScreenFilters = securityFiltersBean
				.getAuthScreenFilters();
		
		HashMap<String, String> compMap = (HashMap<String, String>) authScreenFilters
				.get(screenId);
		
		if (compMap != null && !compMap.isEmpty()) {
			for (String componentId : compMap.keySet()) {

				UIComponent component = facesScopes.findComponentById(viewroot,
						componentId);
				log.debug("veiwRoot : " + viewroot);
				log.debug("componentId : " + componentId);
				log.debug("CompMap : " + compMap);
				log.debug("Component : " + component);

				if (component != null) {
					mergeBizRules(compMap, component);
				}

			}
		}

	}

	

	/**
	 * Retrieve user's screen filters and store into session if not retrieved
	 * yet.
	 * 
	 * @param userId
	 *            user Id.
	 * @param screenId
	 *            screen id of the current view.
	 * 
	 * @return SecurityFiltersBean
	 */
	private static SecurityFiltersBean getScreenFilters(Long userId,
			String screenCode) {
		FacesScopes facesScope = new FacesScopes();
		SecurityFiltersBean securityFiltersBean = getSecurityFiltersBean(facesScope);

		if (!StringUtils.isBlank(screenCode)) {
			HashMap<String, Map<String, String>> authScreenFilters = securityFiltersBean
					.getAuthScreenFilters();
			if (!authScreenFilters.containsKey(screenCode)) {
				IScreenRestrSO screenRestrictionSO = null;
				try {
					screenRestrictionSO = (IScreenRestrSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IScreenRestrSO.class);
					ScreenRestrReqDTO requestDTO = new ScreenRestrReqDTO();
					requestDTO.setScreenCode(screenCode);
					requestDTO.setUserId(userId);
					ScreenRestrResDTO screenRestrictionResDTO = screenRestrictionSO
							.getFieldRestrValueData(requestDTO);

					List<PmFieldRestrValue> fieldRestrValues = screenRestrictionResDTO
							.getFieldRestrValues();
					PmScreenRestr screenRestr;
					HashMap<String, String> compMap = new HashMap<String, String>();
					if (fieldRestrValues != null) {
						for (PmFieldRestrValue fieldRestrData : fieldRestrValues) {
							screenRestr = fieldRestrData.getPmScreenRestr();						

							if (screenRestr != null) {

								compMap.put(screenRestr.getComponentId(),
										fieldRestrData.getAuthorisationType());
							}
						}
					}

					authScreenFilters.put(screenCode, compMap);

				} finally {
					screenRestrictionSO = null;
				}
			}
		}

		return securityFiltersBean;
	}

	/**
	 * set component's attribute to hide / disabled based on configured screen
	 * filter.
	 * 
	 * @param componentMap
	 *            component map that are containing the component id & filter.
	 * @param component
	 *            target component that are applying screen filter.
	 * 
	 * @return
	 */
	private static void mergeBizRules(HashMap<String, String> componentMap,
			UIComponent component) {
		

		if (componentMap.containsKey(component.getId())) {
			String screenAuth = componentMap.get(component.getId());
		

			if (ScreenAuthority.DISABLED.value().equals(screenAuth)) {
				disableAllParentNChildren(component);
			}
			if (ScreenAuthority.HIDDEN.value().equals(screenAuth)) {
				if (component.getChildren().size() > 0) {
					for (UIComponent child : component.getChildren()) {
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
	 *            component that are having disabled filter, and need disable
	 *            all children.
	 * 
	 * @return
	 */
	private static void disableAllParentNChildren(UIComponent parentComponent) {

		if (parentComponent != null) {

			ValueExpression disabledVE = FacesContext
					.getCurrentInstance()
					.getApplication()
					.getExpressionFactory()
					.createValueExpression(
							FacesContext.getCurrentInstance().getELContext(),
							"true", Boolean.class);

			for (UIComponent component : parentComponent.getChildren()) {

				if (component.getChildCount() > 0) {

					disableAllParentNChildren(component);

				} else {
					debugLogger("DISABLED-component:" + component.getClientId());

					component.setValueExpression("disabled", disabledVE);
				}
			}
			try {
				debugLogger("DISABLED-parentComponent:"
						+ parentComponent.getClientId());

				parentComponent.setValueExpression("disabled", disabledVE);
			} catch (Exception e) {
				log.error(parentComponent + " " + e);
			}
		}
	}

	/**
	 * retrieve user's security filter bean from session.
	 * 
	 * @param facesScope
	 *            FacesScopes of the current view.
	 * 
	 * @return SecurityFiltersBean
	 */
	private static SecurityFiltersBean getSecurityFiltersBean(
			FacesScopes facesScope) {
		SecurityFiltersBean securityFiltersBean = (SecurityFiltersBean) facesScope
				.getSessionScope(SESS_SECURITY_FILTERS_BEAN);

		if (securityFiltersBean == null) {
			securityFiltersBean = new SecurityFiltersBean();

			facesScope.setSessionScope(SESS_SECURITY_FILTERS_BEAN,
					securityFiltersBean);
		}

		return securityFiltersBean;
	}

	
	public static void applyDataFilters(Long userId, String screenCode) {
		debugLogger(new StringBuilder().append("applyDataFilters[")
				.append("userId:").append(userId).append(", screenId:")
				.append(screenCode).append("]"));
		
		FacesScopes facesScope = new FacesScopes();
		SecurityFiltersBean securityFiltersBean = getSecurityFiltersBean(facesScope);

		// Make sure userId and screenId is not empty
		if (!StringUtils.isBlank(userId.toString())){
		if (!StringUtils.isBlank(screenCode)) {
			Map<String, Map<String, List<PmScreenRestr>>> autDataFilters = securityFiltersBean
					.getAutDataFilters();
			if (!autDataFilters.containsKey(screenCode)) {
				IScreenRestrSO screenRestrSO = null;
				try {
					screenRestrSO = BusinessDelegate
							.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IScreenRestrSO.class);			

					ScreenRestrReqDTO reqDTO = new ScreenRestrReqDTO();
					reqDTO.setUserId(userId);
					reqDTO.setScreenCode(screenCode);

					ScreenRestrResDTO resDTO = screenRestrSO
							.getDataRestrValueData(reqDTO);
					List<PmScreenRestr> screenMetadatas = resDTO
							.getScreenRestrs();
					Map<String, List<PmScreenRestr>> screenMetadataMap = new HashMap<String, List<PmScreenRestr>>();

					if (screenMetadatas != null) {
						String componentId;
						List<PmScreenRestr> componentScreenMetadatas;

						for (PmScreenRestr screenMetadata : screenMetadatas) {
							componentId = screenMetadata
									.getComponentId();

							if (!StringUtils.isBlank(componentId)) {
								componentScreenMetadatas = screenMetadataMap
										.get(componentId);

								// Check based on component id
								// (ScreenFilterObject) to create a new List
								if (componentScreenMetadatas == null) {
									componentScreenMetadatas = new ArrayList<PmScreenRestr>();

									// Put to the map with the key as
									// component id (ScreenFilterObject)
									screenMetadataMap.put(componentId,
											componentScreenMetadatas);
								}

								
								  debugLogger(new StringBuilder()
								  .append("componentId:") .append(componentId)
								  .append(", filterDataType:")
								  .append(screenMetadata.getPropertyName())
								  .append(", filterDataSql:")
								  .append(screenMetadata.getDataRestrType()));
								 

								// Put to the list
								componentScreenMetadatas.add(screenMetadata);
							}
						}
					}

					// Put to the map with the key as screen id in
					// securityFiltersBean
					securityFiltersBean.getAutDataFilters().put(screenCode,
							screenMetadataMap);

				} finally {
					screenRestrSO = null;
				}
			}
			
		}
		}
		

	}

	public static List<PmScreenRestr> getDataFilterScreenMetadatas(String componentId) {
		FacesScopes facesScopes = new FacesScopes();		
		
		String screenId = EpPortletUtil.getDisplayPortletId();
		SecurityFiltersBean securityFiltersBean = getSecurityFiltersBean(facesScopes);

		if (!StringUtils.isBlank(screenId) && !StringUtils.isBlank(componentId)) {
			Map<String, List<PmScreenRestr>> screenMetadatasMap = securityFiltersBean
					.getAutDataFilters().get(screenId);
			if (screenMetadatasMap != null) {
				List<PmScreenRestr> screenMetadatas = screenMetadatasMap
						.get(componentId);
				if (screenMetadatas != null) {
					return screenMetadatas;
				}
			}
		}

		return Collections.emptyList();
	}
}
