package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;

import org.apache.commons.lang.StringUtils;

import com.liferay.compat.portal.util.PortalUtil;

@ManagedBean(name = "viewPreRenderSmApplBackingBean")
@RequestScoped
public class ViewPreRenderSmApplBackingBean {
	
	public void navigateToRevAppl() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String applNo = originalServletRequest.getParameter("applicationNo");
		String applType = originalServletRequest
				.getParameter("applicationType");

		if (StringUtils.isNotBlank(applNo) && StringUtils.isNotBlank(applType)) {
			String redirectToAppl = applNo.substring(applNo.length() - 3,
					applNo.length()).equals("APP") ? SmApplicationUtil
					.getReviewApplicationBasePage(applType) : SmApplicationUtil
					.getReviewApplicationSupplierBasePage(applType);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, redirectToAppl);
		}

	}
	
}
