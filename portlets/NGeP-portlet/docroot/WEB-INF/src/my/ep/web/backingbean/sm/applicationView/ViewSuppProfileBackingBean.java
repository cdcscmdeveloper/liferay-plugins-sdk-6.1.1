package my.ep.web.backingbean.sm.applicationView;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.SLAUtils;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

//#28895_35501 Start - AUDIT TRAIL SM
import com.liferay.compat.portal.util.PortalUtil;
import javax.servlet.http.HttpServletRequest;
//#28895_35501 End
@ManagedBean(name = "viewSuppProfileBackingBean")
@RequestScoped
public class ViewSuppProfileBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> implements Serializable {
	private static Logger logger = Logger.getLogger(ViewSuppProfileBackingBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 377766773512626830L;

	/**
	 * Pre - render for View Supplier Profile
	 */
	public void preRenderViewSupplierProfile() {
		 //20211005@fadhli - #31754
		String sessionId = ((PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest()).getRequestedSessionId(); 
		//LOG : 26549 : SLA Monitoring
		long startTime = System.currentTimeMillis();
		long subStartTime = 0L;
		renewSesionObject();
		initLogin();
		// #28895_35501 Start - AUDIT TRAIL SM
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String isAudit = originalServletRequest.getParameter("isAudit");
		if (SmCommonUtil.md5("true").equals(isAudit)) {
			this.getManagedBean().setAuditAccess(true);
		} else if (SmCommonUtil.md5("false").equals(isAudit)) {
			this.getManagedBean().setAuditAccess(false);
		}
		// #28895_35501 End
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		logger.info("[preRenderViewSupplierProfile] #31754 getManagedBean().isSupplierUser(): " + getManagedBean().isSupplierUser());
		if (getManagedBean().isSupplierUser()) {
			SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			subStartTime = System.currentTimeMillis(); //20211005@fadhli - #31754
			MofRegistrationResDTO resDTO = SmServiceLookup
					.getMofRegistrationSO()
					.retrieveLatestSmApplWithChildBySupplierId(reqDTO);
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "retrieveLatestSmApplWithChildBySupplierId", 
					subStartTime); //20211005@fadhli - #31754
			String redirectToAppl = SmApplicationUtil.getApplicationErrorPage();

			logger.info("[preRenderViewSupplierProfile] #31754 resDTO.getSmAppl(): " + resDTO.getSmAppl());
			if (resDTO.getSmAppl() != null) {
				String applNo = resDTO.getSmAppl().getApplNo();
				getManagedBean().setApplicationNoParam(applNo + "_APP");
				logger.info("[preRenderViewSupplierProfile] #31754 applNo: " + applNo);
				if (applNo != null && StringUtils.isNotBlank(applNo)) {
					subStartTime = System.currentTimeMillis();
					viewApplication(resDTO.getSmAppl());
					SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, applNo,
							"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "viewApplication", 
							subStartTime); //20211005@fadhli - #31754
				} else {
					SmCommonUtil.redirectToPage(redirectToAppl,
							MessageConst.INFO_721212, null, null);
				}
			} else {
				SmCommonUtil.redirectToPage(redirectToAppl,
						MessageConst.INFO_721212, null, null);
			}
			subStartTime = System.currentTimeMillis();
			setTabParam();
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "setTabParam", 
					subStartTime); //20211005@fadhli - #31754
			SLAUtils.getInstance().logSLAInfo("SM","SP",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
			getManagedBean().setRenderedBackButton(false);
		} else {
			subStartTime = System.currentTimeMillis();
			initLoginUserZone();
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "initLoginUserZone", 
					subStartTime); //20211005@fadhli - #31754
			getManagedBean().setRenderedBackButton(true);
			String supplierIdParam = getSupplierIdParam();
			subStartTime = System.currentTimeMillis();
			getManagedBean().setRegStatusDescList(
					retrieveRegistrationStatusList());
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "retrieveRegistrationStatusList", 
					subStartTime); //20211005@fadhli - #31754
			subStartTime = System.currentTimeMillis();
			getManagedBean().setZoneList(
					SmServiceLookup.getZoneSO().retrieveActiveZoneList()
							.getZoneList());
			SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId, "null",
					"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "retrieveActiveZoneList", 
					subStartTime); //20211005@fadhli - #31754
			logger.info("[preRenderViewSupplierProfile] #31754 supplierIdParam: " + supplierIdParam);
			if (StringUtils.isNotBlank(supplierIdParam)) {
				getManagedBean().setRenderedBackButton(false);
				subStartTime = System.currentTimeMillis();
				viewApplication(SmApplicationUtil
						.getLatestApplBySupplierId(Long
								.parseLong(supplierIdParam)));
				SLAUtils.getInstance().logSLASubInfo("SM","SP", sessionId,
						SmApplicationUtil.getLatestApplBySupplierId(Long.valueOf(supplierIdParam)).getApplNo(),
						"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", "viewApplication", 
						subStartTime); //20211005@fadhli - #31754
				//LOG : 26549 : SLA Monitoring
				SLAUtils.getInstance().logSLAInfo("SM","SP",
						"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
			} else {
				//LOG : 26549 : SLA Monitoring
				SLAUtils.getInstance().logSLAInfo("SM","SP",
						"ViewSuppProfileBackingBean.preRenderViewSupplierProfile", startTime);
				SmCommonUtil
						.redirectToPage(
								"/xhtml/sm/revisionSmApplView/viewSupplierProfile/searchSupplierProfilePage.xhtml",
								"", null, null);
			}
		}
	}

	public void search() {
		validateCompanySearch();
		getManagedBean().searchSupplier();
	}
	
	//#29482 - Start
	public void assignSelAcadQualify(SmPersonnel smPersonnel) {
		this.getManagedBean().setSmPersonnel(smPersonnel);
		RequestContext.getCurrentInstance().execute("qualifyDialog.show()");
	}
	//#29482 - End
	//35336 START
	/**
	 * Check whether the rejected application was expired
	 * compare to no. of days allowed to resubmit after rejected
	 * @param SmAppl appl
	 * @return Boolean
	 */
	public Boolean checkRejectedApplExpired(SmAppl appl){
		Boolean isExpired = false;
		if(appl.getApplId() != null) {
			if(!SmApplicationUtil.isRejectedApplValidForResubmission(appl)) {
				isExpired = true;
			}
		}		
		return isExpired;
	}
	//35336 END

}
