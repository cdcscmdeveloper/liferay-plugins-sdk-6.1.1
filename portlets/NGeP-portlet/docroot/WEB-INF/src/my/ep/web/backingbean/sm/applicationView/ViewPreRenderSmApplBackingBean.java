package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpServletRequest;

import my.ep.constant.MessageConst;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.compat.portal.util.PortalUtil;

@ManagedBean(name = "viewPreRenderSmApplBackingBean")
@RequestScoped
public class ViewPreRenderSmApplBackingBean {
	
	public static Logger log = Logger.getLogger(ViewPreRenderSmApplBackingBean.class);
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
		SmAppl smAppl = null;
		log.info("TROUBLESHOOT REVISION ViewPreRenderSmApplBackingBean----------------------------applNo="+applNo);
		log.info("TROUBLESHOOT REVISION ViewPreRenderSmApplBackingBean----------------------------applType="+applType);
		if (StringUtils.isNotBlank(applNo) && StringUtils.isNotBlank(applType)) {
			String serFileType = applNo.substring(applNo.length() - 3,
					applNo.length());
			applNo = applNo.substring(0, applNo.length() - 4);

			if (serFileType.equals("APP")) {
				redirectToAppl = SmApplicationUtil
						.getReviewApplicationBasePage(applType);
				log.info("TROUBLESHOOT REVISION In IF ViewPreRenderSmApplBackingBean=="+redirectToAppl);
				ViewSmApplBackingBean viewSmApplBackingBean = new ViewSmApplBackingBean();
				smAppl = SmCommonUtil.retrieveSmApplByApplNo(applNo);

				if (smAppl != null) {
					viewSmApplBackingBean.setApplication(smAppl);
					if (!SmCommonUtil.isSerFileExist(applNo, serFileType)) {
						SmCommonUtil.createSerFile(smAppl, serFileType);
					}
				} else {
					SmCommonUtil.redirectToPage(
							SmApplicationUtil.getApplicationErrorPage(),
							MessageConst.INFO_721212, null);
				}
			} else {
			
				redirectToAppl = SmApplicationUtil
						.getReviewApplicationSupplierBasePage(applType);
				log.info("In ELSE  ViewPreRenderSmApplBackingBean=="+redirectToAppl);
				ViewSmSuppApplBackingBean viewSmSuppApplBackingBean = new ViewSmSuppApplBackingBean();
				viewSmSuppApplBackingBean.initMain();
			}
			SmCommonUtil.redirectToPage(redirectToAppl, "", null);
		} else {
			SmCommonUtil.redirectToPage(redirectToAppl,
					MessageConst.INFO_721212, null);
		}

	}
}
