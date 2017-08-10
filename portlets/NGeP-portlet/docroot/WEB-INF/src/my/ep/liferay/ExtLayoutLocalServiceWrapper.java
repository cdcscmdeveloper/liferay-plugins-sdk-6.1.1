package my.ep.liferay;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalService;
import com.liferay.portal.service.LayoutLocalServiceWrapper;

public class ExtLayoutLocalServiceWrapper extends LayoutLocalServiceWrapper {

	public static Logger log = Logger.getLogger(ExtLayoutLocalServiceWrapper.class);
	
	public ExtLayoutLocalServiceWrapper(LayoutLocalService layoutLocalService) {
		super(layoutLocalService);
		// TODO Auto-generated constructor stub
	}

	public List<Layout>	getLayouts(int start, int end) throws SystemException {
		System.out.println(11111);
		return super.getLayouts(start, end);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout) throws SystemException {
		System.out.println(22222);
		System.out.println("group id: " + groupId);
		
		List<Layout> layouts = new ArrayList<Layout>();
//		layouts.addAll(super.getLayouts( groupId,  privateLayout));
		
		for(Layout layout : super.getLayouts( groupId,  privateLayout)) {
			System.out.println(layout.getFriendlyURL());
			if(!layout.getFriendlyURL().equals("/upgrade-basic-to-mof")) {
				layouts.add(layout);
			}
		}
//		remoteLayout(layouts, "/catalogue-search");
		
//		return super.getLayouts( groupId,  privateLayout);
		return layouts;
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long parentLayoutId) throws SystemException {
		System.out.println(33333);
		System.out.println("group id: " + groupId);
		
		List<Layout> layouts = new ArrayList<Layout>();
//		layouts.addAll(super.getLayouts( groupId,  privateLayout, parentLayoutId));
		
		for(Layout layout : super.getLayouts( groupId,  privateLayout, parentLayoutId)) {
			System.out.println(layout.getFriendlyURL());
			if(!layout.getFriendlyURL().equals("/upgrade-basic-to-mof")) {
				layouts.add(layout);
			}
		}
//		remoteLayout(layouts, "/catalogue-search");
//		return super.getLayouts( groupId,  privateLayout,  parentLayoutId);
		return layouts;
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long[] layoutIds) throws SystemException, PortalException {
		System.out.println(44444);
		System.out.println("group id: " + groupId);
		return super.getLayouts( groupId,  privateLayout, layoutIds);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long parentLayoutId, boolean incomplete, int start, int end) throws SystemException {
		System.out.println(55555);
		System.out.println("group id: " + groupId);
		return super.getLayouts( groupId,  privateLayout,  parentLayoutId,  incomplete,  start,  end);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, String type) throws SystemException {
		System.out.println(66666);
		System.out.println("group id: " + groupId);
		return super.getLayouts( groupId,  privateLayout,  type);
	}
	
	/*public List<Layout>	getNoPermissionLayouts(long roleId) throws SystemException {
		return super.getNoPermissionLayouts(roleId);
	}*/
	
	public List<Layout>	getNullFriendlyURLLayouts() throws SystemException {
		System.out.println(77777);
		return super.getNullFriendlyURLLayouts();
	}
	
	public List<Layout>	getScopeGroupLayouts(long groupId, boolean privateLayout) throws SystemException {
		System.out.println(88888);
		System.out.println("group id: " + groupId);
		return super.getScopeGroupLayouts(groupId, privateLayout);
	}
	
	private void remoteLayout(List<Layout> layouts, String friendlyUrl) throws SystemException {
		for(Layout layout : layouts) {
			/*if(layout.getChildren().size() > 0) {
				remoteLayout(layout.getChildren(), friendlyUrl);				
			} */
			
			if(layout.getFriendlyURL().equals(friendlyUrl)) {
				layouts.remove(layout);
			}
		}
	}
	
	/*public boolean isValidToViewVirtualCert() {
		IMofAccountSO mofSO = null;
		boolean valid = false;
		try {
			mofSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofAccountSO.class);
			long userId = 0;
			if(new FacesScopes().getUserSessionBean() != null) {
				userId = new FacesScopes().getUserSessionBean().getUserId();
			}
			log.info("user id: " + userId);
			MofAccountReqDTO reqDTO = new MofAccountReqDTO();
			reqDTO.setUserId(userId);
			MofAccountResDTO resDTO = mofSO.retriveMofAccountByUserId(reqDTO);
			valid = resDTO.isSuccessful();
			System.out.println("valid: " + valid);
		} finally {
			mofSO = null;
		}
		return valid;
	}*/
	
	
}
