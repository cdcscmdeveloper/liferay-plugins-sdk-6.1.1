package my.ep.liferay;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalService;
import com.liferay.portal.service.LayoutLocalServiceWrapper;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

public class ExtLayoutLocalServiceWrapper extends LayoutLocalServiceWrapper {

	public static Logger log = Logger.getLogger(ExtLayoutLocalServiceWrapper.class);
	
	public final String RENEW_MOF_URL = "/renew-mof-account";
	public final String REGISTER_MOF_URL = "/upgrade-basic-to-mof";
	
	public ExtLayoutLocalServiceWrapper(LayoutLocalService layoutLocalService) {
		super(layoutLocalService);
		// TODO Auto-generated constructor stub
	}

	public List<Layout>	getLayouts(int start, int end) throws SystemException {
		return super.getLayouts(start, end);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout) throws SystemException {
		
		List<Layout> layouts = new ArrayList<Layout>();
		
		if(!isBasicSupplierUser()) {
			return super.getLayouts( groupId,  privateLayout);
		} else {
			
			for(Layout layout : super.getLayouts( groupId,  privateLayout)) {
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL)) {
					if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && validBasicToRenewMOF()) {
						layouts.add(layout);
					} else if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) && !validBasicToRenewMOF()) {
						layouts.add(layout);
					}
				} else {
					layouts.add(layout);
				}
			}
		}
		
		/*List<Layout> layouts = new ArrayList<Layout>();
		
		for(Layout layout : super.getLayouts( groupId,  privateLayout)) {
			if(isBasicSupplierUser()) {
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL)) {
					if(validBasicToRenewMOF()) {
						layouts.add(layout);
					}
				} else if(layout.getFriendlyURL().equals(RENEW_MOF_URL)) {
					layouts.add(layout);
				} else {
					layouts.add(layout);
				}
			} else {
				layouts.add(layout);
			}			
		}*/
//		remoteLayout(layouts, "/catalogue-search");
		
//		return super.getLayouts( groupId,  privateLayout);
		return layouts;
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long parentLayoutId) throws SystemException {
		
		List<Layout> layouts = new ArrayList<Layout>();
		
		if(!isBasicSupplierUser()) {
			return super.getLayouts(groupId, privateLayout, parentLayoutId);
		} else {
			
			for(Layout layout : super.getLayouts(groupId, privateLayout, parentLayoutId)) {
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL)) {
					if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && validBasicToRenewMOF()) {
						layouts.add(layout);
					} else if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) && !validBasicToRenewMOF()) {
						layouts.add(layout);
					}
				} else {
					layouts.add(layout);
				}
			}
		}
		
		return layouts;
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long[] layoutIds) throws SystemException, PortalException {

		return super.getLayouts( groupId,  privateLayout, layoutIds);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long parentLayoutId, boolean incomplete, int start, int end) throws SystemException {

		return super.getLayouts( groupId,  privateLayout,  parentLayoutId,  incomplete,  start,  end);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, String type) throws SystemException {

		return super.getLayouts( groupId,  privateLayout,  type);
	}
	
	public List<Layout>	getNullFriendlyURLLayouts() throws SystemException {
		return super.getNullFriendlyURLLayouts();
	}
	
	public List<Layout>	getScopeGroupLayouts(long groupId, boolean privateLayout) throws SystemException {

		return super.getScopeGroupLayouts(groupId, privateLayout);
	}
	
	private boolean validBasicToRenewMOF() {
		long userId = 0;
		if(new FacesScopes().getUserSessionBean() != null) {
			userId = new FacesScopes().getUserSessionBean().getUserId();
		}
		
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setUserId(userId);
		
		IMofAccountSO mofAccountSO = null;
		
		try {
			mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofAccountSO.class);
			MofAccountResDTO resDTO = mofAccountSO.checkExpiredDowngradedToBasic(reqDTO);
			
			return !resDTO.isExpired();
		} finally {
			mofAccountSO = null;
		}
	}	
	
	public boolean isBasicSupplierUser() {
		List<Role> loginUserRoles = getUserRoleList();
		
		for (Role r : loginUserRoles) {
			if(StringUtils.containsAny(r.getName(), "BASIC")) {
				return true;
			}
		}
		
		return false;
	}

	public String getScreenName() {

		if(FacesContext.getCurrentInstance() != null) {
			ThemeDisplay themeDisplay = (ThemeDisplay)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
			if(themeDisplay != null) {
				if(themeDisplay.getUser() != null) {
					return themeDisplay.getUser().getScreenName();
				}
			}
		}
		return null;
	}
	
	public List<Role> getUserRoleList() {
		List<Role> userRoleList = new ArrayList<Role>();
		if(getScreenName() != null) {
			try {
				User user = UserServiceUtil.getUserById(Long.valueOf(getScreenName()));
				userRoleList = user.getRoles();
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return userRoleList;
	}
	
/*	public static void main(String[] args) {
		String roleName = "BASIC_SUPPLIER_ADMIN";
		System.out.println("roleName.indexOf(\"BASIC\"): " + roleName.indexOf("BASIC"));
		System.out.println("ddddd" + StringUtils.containsAny(roleName, "BASIC"));
		
		if(roleName.indexOf("B") > 0) {
			System.out.println(true);
		}
	}*/
}
