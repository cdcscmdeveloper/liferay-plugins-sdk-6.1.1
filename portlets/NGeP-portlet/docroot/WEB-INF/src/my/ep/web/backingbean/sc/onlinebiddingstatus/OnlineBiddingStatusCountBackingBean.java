/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.onlinebiddingstatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.onlinebiddingstatus.OnlineBiddingStatusCountManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * The Class OnlineBiddingStatusCountBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean(name = "onlineBiddingStatusBB")
@RequestScoped
public class OnlineBiddingStatusCountBackingBean extends
		SourcingBaseBackingBean<OnlineBiddingStatusCountManagedBean> {
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(OnlineBiddingStatusCountBackingBean.class);

	public OnlineBiddingStatusCountBackingBean() {

	}

	@PostConstruct
	public void init() {

	}

	/* navigate to schedule bidding page */
	public void navigatetoScheduleBidding() {
		ThemeDisplay themeDisplay = (ThemeDisplay) new FacesScopes()
				.getRequestScope(WebKeys.THEME_DISPLAY);
		try {
			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());


			String screenUrl =String.valueOf(SourcingConstants.NAV_PAGE_BIDDING_MANAGEMENT);
			StringBuilder navigationUrl = new StringBuilder();
			navigationUrl.append(commonUrl).append("/").append(screenUrl);
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.redirect(navigationUrl.toString());
			log.info("getNavigationUrl ==" + navigationUrl.toString());
		} catch (IOException e) {
			log.error(e);
		} catch (PortalException e) {
			log.error(e);
		} catch (SystemException e) {
			log.error(e);
		}
	}

	/**
	 * method to fetch status drop down list values
	 * 
	 * @return
	 */
	public void getStatusDescList() {
		List<PmStatusDesc> statusDescs = getManagedBean().getStatusDescList();
		if (statusDescs == null || statusDescs.size() == 0) {
			statusDescs = getBiddingDropdownValues(
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(),
					ParameterConst.DOCTYPE_BIDDING);
			getManagedBean().setStatusDescList(statusDescs);
		}
	}

	private List<PmStatusDesc> getBiddingDropdownValues(String moduleCode,
			String docType) {
		SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
		// Filter By - statusId
		List<Long> statusIds = new ArrayList<Long>();
		statusIds.add(BidStatusConst.BID_PENDING_BIDDING);
		statusIds.add(BidStatusConst.BID_IN_PROGRESS);
		statusIds.add(BidStatusConst.BID_RESCHEDULE_BIDDING);
		statusIds.add(BidStatusConst.BID_PAUSED);
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.statusId",
				SearchOperator.OPERATOR_IN.value(),
				statusIds));

		return StatusHelper.getInstance().getStatusDescList(
				moduleCode, docType, searchBean);
	}

	public String getBidInProgress() {
		return String.valueOf(BidStatusConst.BID_IN_PROGRESS);
	}

	public String getBidPending() {
		return String.valueOf(BidStatusConst.BID_PENDING_BIDDING);
	}

	public String getBidReschedule() {
		return String.valueOf(BidStatusConst.BID_RESCHEDULE_BIDDING);
	}

	public String getBidPause() {
		return String.valueOf(BidStatusConst.BID_PAUSED);
	}

	public int searchAllStatusBiddingCount(String status) {
		IBiddingSO biddingSO = null;
		log.debug("=====start searchAllStatusBiddingCount====" + status);
		int count = 0;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			ISearchBean<ScBidSchedule> bidSchSearchBean = new SearchBean<ScBidSchedule>();
			/*bidSchSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("startDate",
							SearchOperator.OPERATOR_GREATER_EQUAL.value(),
							DateUtils.getCurrentDateWithoutTime()));*/
			if (StringUtils.isNotBlank(status)
					&& StringUtils.isNotEmpty(status)) {
				bidSchSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), Long
										.valueOf(status)));
			}

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setBidSchSearchBean(bidSchSearchBean);
			BiddingResDTO resDTO = biddingSO.searchBiddingScheduleList(reqDTO);

			if (null != resDTO && null != resDTO.getBidSchResultBean()
					&& null != resDTO.getBidSchResultBean().getResultList()) {
				count = resDTO.getBidSchResultBean().getResultList().size();
			}

			log.debug("=====end searchAllStatusBiddingCount====" + status);
		} finally {
			biddingSO = null;
		}

		return count;
	}

	public void preRenderOnlineBiddingStatus() {

		try {
			HttpServletRequest servletRequest = PortalUtil
					.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(servletRequest);
			getStatusDescList();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.ONLINE_BIDDING_STATUS_COUNT);

		} finally {

		}
	}

}
