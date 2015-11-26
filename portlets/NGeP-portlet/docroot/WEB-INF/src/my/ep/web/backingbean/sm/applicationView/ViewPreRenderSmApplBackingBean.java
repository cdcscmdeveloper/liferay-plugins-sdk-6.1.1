package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpServletRequest;

import my.ep.constant.MessageConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;

import com.liferay.compat.portal.util.PortalUtil;

@ManagedBean(name = "viewPreRenderSmApplBackingBean")
@RequestScoped
public class ViewPreRenderSmApplBackingBean {

	/**
	 * Redirect to view previous application page
	 */
	public void navigateToRevAppl() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String applNo = originalServletRequest.getParameter("applicationNo");
		String applType = originalServletRequest
				.getParameter("applicationType");

		String redirectToAppl = SmApplicationUtil.getApplicationErrorPage();

		if (StringUtils.isNotBlank(applNo) && StringUtils.isNotBlank(applType)) {
			redirectToAppl = applNo.substring(applNo.length() - 3,
					applNo.length()).equals("APP") ? SmApplicationUtil
					.getReviewApplicationBasePage(applType) : SmApplicationUtil
					.getReviewApplicationSupplierBasePage(applType);
			SmCommonUtil.redirectToPage(redirectToAppl, "",null);
		} else {
			SmCommonUtil.redirectToPage(redirectToAppl,
					MessageConst.INFO_721212,null);
		}

	}

}
