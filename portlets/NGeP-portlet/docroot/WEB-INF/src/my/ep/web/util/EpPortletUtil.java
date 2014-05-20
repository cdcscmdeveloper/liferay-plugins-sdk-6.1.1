package my.ep.web.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.web.bean.MenuBean;
import my.ep.web.bean.PortletBean;
import my.ep.web.bean.PortletCategoryBean;
import my.ep.web.bean.RoleBean;

import org.apache.log4j.Logger;

import com.liferay.portal.DuplicateRoleException;
import com.liferay.portal.NoSuchRoleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.PortletCategory;
import com.liferay.portal.model.Resource;
import com.liferay.portal.model.ResourceAction;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class EpPortletUtil {
	private static Logger log = EpLogger.getLogger(EpPortletUtil.class);

	private static List<PortletCategoryBean> portletCategoryBeans;

	private static void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	public static String getDisplayPortletId() {
		return getThemeDisplay().getPortletDisplay().getId();
	}
	
	

	public String getDisplayLayoutName() {

		ThemeDisplay themeDisplay = getThemeDisplay();		

		return themeDisplay.getLayout().getName(themeDisplay.getLocale());
	}

	public static String getLoginUserId(FacesContext context) {
		return getThemeDisplay().getUser().getScreenName();
	}
	
	public static long getUserId() {
		return getThemeDisplay().getUserId();
	}

	public static List<PortletCategoryBean> getPortletCategories() {
		if (portletCategoryBeans == null) {
			portletCategoryBeans = new ArrayList<PortletCategoryBean>();

			try {
				PortletContext ctx = ((PortletContext) FacesContext
						.getCurrentInstance().getExternalContext().getContext());
				String servletContextName = ctx.getPortletContextName();
				String xml = HttpUtil.URLtoString(ctx
						.getResource("/WEB-INF/liferay-display.xml"));
				PortletCategory portletRootCategory = PortletLocalServiceUtil
						.getWARDisplay(servletContextName, xml);

				for (PortletCategory portletCategory : portletRootCategory
						.getCategories()) {
					portletCategoryBeans
							.add(toPortletCategoryBean(portletCategory));
				}
			} catch (MalformedURLException e) {
				handleException(e);
			} catch (IOException e) {
				handleException(e);
			} catch (SystemException e) {
				handleException(e);
			}
		}

		return portletCategoryBeans;
	}

	public static PortletCategoryBean getPortletCategoryById(String categoryId) {
		return getPortletCategoryById(categoryId, getPortletCategories());
	}

	public static PortletBean getPortletById(String portletId) {
		ThemeDisplay themeDisplay = getThemeDisplay();
		Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);
		String portletTitle = PortalUtil.getPortletTitle(portlet,
				themeDisplay.getLocale());

		return new PortletBean(portletId, portlet.getPortletName(),
				portletTitle);
	}

	public static List<MenuBean> getMenuBeans() {
		List<MenuBean> menuBeans = new ArrayList<MenuBean>();

		try {
			MenuBean menuBean;
			ThemeDisplay themeDisplay = getThemeDisplay();
			PermissionChecker permissionChecker = themeDisplay
					.getPermissionChecker();
			List<Layout> layouts = themeDisplay.getLayouts();
			//Group group = themeDisplay.getLayout().getGroup();
			Locale locale = themeDisplay.getLocale();/*
			boolean hasViewStagingPermission = (group.isStagingGroup() || group
					.isStagedRemotely())
					&& GroupPermissionUtil.contains(permissionChecker,
							group.getGroupId(), ActionKeys.VIEW_STAGING);*/

			for (Layout mainLayout : layouts) {
				if (mainLayout.getFriendlyURL().equals("/my-application")) {
					for (Layout layout : mainLayout.getChildren(permissionChecker)) {
						if (!layout.isHidden()/*
								&& (isViewableLayout(permissionChecker, layout) || hasViewStagingPermission)*/) {

							menuBean = toMenuBean(permissionChecker, layout, locale);

							if (log.isDebugEnabled()) {
								log.debug(new StringBuilder().append("name:")
										.append(menuBean.getName()).append(", title:")
										.append(menuBean.getTitle())
										.append(", imageId:")
										.append(menuBean.getImageId()));

								for (MenuBean childMenuBean : menuBean.getChildren()) {
									log.debug(new StringBuilder().append("childname:")
											.append(childMenuBean.getName())
											.append(", title:")
											.append(childMenuBean.getTitle())
											.append(", imageId:")
											.append(childMenuBean.getImageId())
											.append(", url:")
											.append(childMenuBean.getUrl()));
								}
							}

							menuBeans.add(menuBean);
						}	
					}	
				}
			}
		} catch (PortalException e) {
			handleException(e);
		} catch (SystemException e) {
			handleException(e);
		}

		return menuBeans;
	}

	public static void addRole(RoleBean roleBean) {
		String name = roleBean.getName();
		String description = roleBean.getDescription();

		debugLogger(new StringBuilder().append("addRole-->name:").append(name));

		try {
			ThemeDisplay themeDisplay = getThemeDisplay();
			long companyId = themeDisplay.getCompanyId();
			Map<Locale, String> titleMap = null;
			Map<Locale, String> descriptionMap = new HashMap<Locale, String>();

			descriptionMap.put(themeDisplay.getLocale(), description);

			RoleLocalServiceUtil.addRole(themeDisplay.getUserId(), companyId,
					name, titleMap, descriptionMap, RoleConstants.TYPE_REGULAR);
		} catch (DuplicateRoleException e) {
			log.warn("user role " + name + " already exists in portal.");
		} catch (PortalException e) {
			handleException(e);
		} catch (SystemException e) {
			handleException(e);
		}
	}

	public static void updateRole(RoleBean roleBean) {
		String name = roleBean.getName();
		String description = roleBean.getDescription();

		debugLogger(new StringBuilder().append("updateRole-->name:").append(
				name));

		try {
			ThemeDisplay themeDisplay = getThemeDisplay();
			long companyId = themeDisplay.getCompanyId();
			Locale locale = themeDisplay.getLocale();
			Role role = RoleLocalServiceUtil.getRole(companyId, name);

			if (!description.equals(role.getDescription(locale))) {
				debugLogger("update existing user role.");

				role.setDescription(description);

				RoleLocalServiceUtil.updateRole(role);
			}
		} catch (NoSuchRoleException e) {
			log.warn("user role " + name + " not exists in portal.");

			// add new role when not exists.
			addRole(roleBean);

			// add all resource permission attached to this role.
			for (String portletId : roleBean.getPortletIds()) {
				addResourcePermission(name, portletId);
			}
		} catch (PortalException e) {
			handleException(e);
		} catch (SystemException e) {
			handleException(e);
		}
	}

	public static void deleteRole(String name) {
		debugLogger(new StringBuilder().append("deleteRole-->name:").append(
				name));

		try {
			ThemeDisplay themeDisplay = getThemeDisplay();
			long companyId = themeDisplay.getCompanyId();
			Role role = RoleLocalServiceUtil.getRole(companyId, name);

			RoleLocalServiceUtil.deleteRole(role);
		} catch (NoSuchRoleException e) {
			log.warn("user role " + name + " not exists in portal.");
		} catch (PortalException e) {
			handleException(e);
		} catch (SystemException e) {
			handleException(e);
		}
	}

	public static void addResourcePermission(String userRoleCode,
			String screenId) {
		debugLogger(new StringBuilder()
				.append("addResourcePermission-->userRoleCode:")
				.append(userRoleCode).append(", screenId:").append(screenId));

		try {
			ThemeDisplay themeDisplay = getThemeDisplay();
			long companyId = themeDisplay.getCompanyId();
			Role role = RoleLocalServiceUtil.getRole(companyId, userRoleCode);
			Resource resource = ResourceLocalServiceUtil.getResource(companyId,
					screenId, ResourceConstants.SCOPE_COMPANY,
					String.valueOf(companyId));
			ResourceAction action = ResourceActionLocalServiceUtil
					.getResourceAction(screenId, ActionKeys.VIEW);

			/*
			 * try{ ResourcePermission resPermission =
			 * ResourcePermissionLocalServiceUtil
			 * .getResourcePermission(companyId, resource.getName(),
			 * resource.getScope(), resource.getPrimKey(), role.getRoleId());
			 * 
			 * // update existing resource permission.
			 * debugLogger("update existing resource permission");
			 * 
			 * resPermission.setActionIds(action.getBitwiseValue());
			 * 
			 * ResourcePermissionLocalServiceUtil.updateResourcePermission(
			 * resPermission);
			 * 
			 * } catch (NoSuchResourcePermissionException e) { // create new if
			 * resource permission is not exists.
			 * debugLogger("create new if resource permission is not exists");
			 * 
			 * long resourcePermissionId =
			 * CounterLocalServiceUtil.increment(ResourcePermission
			 * .class.getName()); ResourcePermission resPermission =
			 * ResourcePermissionLocalServiceUtil
			 * .createResourcePermission(resourcePermissionId);
			 * 
			 * resPermission.setCompanyId(companyId);
			 * resPermission.setName(resource.getName());
			 * resPermission.setScope(resource.getScope());
			 * resPermission.setPrimKey(resource.getPrimKey());
			 * resPermission.setRoleId(role.getRoleId());
			 * resPermission.setActionIds(action.getBitwiseValue());
			 * 
			 * ResourcePermissionLocalServiceUtil.addResourcePermission(
			 * resPermission);
			 * 
			 * ResourcePermissionLocalServiceUtil.addResourcePermissions(
			 * screenId, userRoleCode, ResourceConstants.SCOPE_COMPANY,
			 * action.getBitwiseValue()); }
			 */

			ResourcePermissionLocalServiceUtil.addResourcePermission(companyId,
					resource.getName(), resource.getScope(),
					resource.getPrimKey(), role.getRoleId(),
					action.getActionId());
		} catch (NoSuchRoleException e) {
			log.warn("user role " + userRoleCode + " not exists in portal.");
		} catch (PortalException e) {
			handleException(e);
		} catch (SystemException e) {
			handleException(e);
		}
	}

	public static void deleteResourcePermission(String userRoleCode,
			String screenId) {
		debugLogger(new StringBuilder()
				.append("deleteResourcePermission-->userRoleCode:")
				.append(userRoleCode).append(", screenId:").append(screenId));

		try {
			ThemeDisplay themeDisplay = getThemeDisplay();
			long companyId = themeDisplay.getCompanyId();
			Role role = RoleLocalServiceUtil.getRole(companyId, userRoleCode);
			Resource resource = ResourceLocalServiceUtil.getResource(companyId,
					screenId, ResourceConstants.SCOPE_COMPANY,
					String.valueOf(companyId));
			ResourceAction action = ResourceActionLocalServiceUtil
					.getResourceAction(screenId, ActionKeys.VIEW);

			ResourcePermissionLocalServiceUtil.removeResourcePermission(
					companyId, resource.getName(), resource.getScope(),
					resource.getPrimKey(), role.getRoleId(),
					action.getActionId());
		} catch (NoSuchRoleException e) {
			log.warn("user role " + userRoleCode + " not exists in portal.");
		} catch (PortalException e) {
			handleException(e);
		} catch (SystemException e) {
			handleException(e);
		}
	}

	private static ThemeDisplay getThemeDisplay() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (ThemeDisplay) ((PortletRequest) context.getExternalContext()
				.getRequest()).getAttribute(WebKeys.THEME_DISPLAY);
	}

	private static PortletCategoryBean getPortletCategoryById(
			String categoryId, List<PortletCategoryBean> portletCategoryBeans) {
		for (PortletCategoryBean tmpPortletCategoryBean : portletCategoryBeans) {
			if (tmpPortletCategoryBean.getName().equals(categoryId)) {
				return tmpPortletCategoryBean;
			} else if (tmpPortletCategoryBean.getChildren().size() > 0) {
				PortletCategoryBean portletCategoryBean = getPortletCategoryById(
						categoryId, tmpPortletCategoryBean.getChildren());
				if (portletCategoryBean != null) {
					return portletCategoryBean;
				}
			}
		}

		return null;
	}

	private static PortletCategoryBean toPortletCategoryBean(
			PortletCategory portletCategory) {
		PortletCategoryBean portletCategoryBean = new PortletCategoryBean(
				portletCategory.getName(), portletCategory.getName(),
				portletCategory.getPortletIds());

		List<PortletCategoryBean> subCategories = new ArrayList<PortletCategoryBean>();
		for (PortletCategory portletSubCategory : portletCategory
				.getCategories()) {
			subCategories.add(toPortletCategoryBean(portletSubCategory));
		}
		portletCategoryBean.setChildren(subCategories);

		return portletCategoryBean;
	}

	private static MenuBean toMenuBean(PermissionChecker permissionChecker,
			Layout layout, Locale locale) throws PortalException,
			SystemException {
		MenuBean menuBean = new MenuBean(layout.getName(locale),
				layout.getTitle(locale),
				layout.isIconImage() ? layout.getIconImageId() : null,
				PortalUtil.getLayoutFriendlyURL(layout, getThemeDisplay()));

		List<MenuBean> childs = new ArrayList<MenuBean>();
		for (Layout child : layout.getChildren(permissionChecker)) {
			if (/*isViewableLayout(permissionChecker, child)*/!child.isHidden()) {
				childs.add(toMenuBean(permissionChecker, child, locale));
			}
		}
		menuBean.setChildren(childs);

		return menuBean;
	}

	/*private static boolean isViewableLayout(
			PermissionChecker permissionChecker, Layout layout)
			throws PortalException, SystemException {

		return LayoutPermissionUtil.contains(permissionChecker, layout, false,
				ActionKeys.VIEW);
	}*/

	private static void handleException(Exception e) {
		e.printStackTrace();
		log.error(e);
		throw new EpException(MessageConst.EP_MAJ_00001);
	}
	
	public String getDisplayPortletName() {
		return getThemeDisplay().getPortletDisplay().getTitle();
	}
}
