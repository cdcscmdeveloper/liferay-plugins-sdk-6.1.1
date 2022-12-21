package my.ep.web.backingbean.sc.bidding;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.BiddingListManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * Bidding List Backing Bean
 * 
 * @author Venkat Chanumolu
 */

@ManagedBean(name = "biddingListBB")
@ViewScoped
public class BiddingListBackingBean extends
		BaseBackingBean<BiddingListManagedBean> implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(BiddingListBackingBean.class);

	/**
	 * Default Constructor
	 */
	public BiddingListBackingBean() {
		getStatusDescList();
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * method to fetch status drop down list values
	 * 
	 * @return
	 */
	public void getStatusDescList() {
		SelectItem[] statusDescs = getManagedBean().getStatusDescs();
		if (statusDescs == null || statusDescs.length == 0) {
			statusDescs = getBiddingDropdownValues(
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(),
					ParameterConst.DOCTYPE_BIDDING);
			getManagedBean().setStatusDescs(statusDescs);
		}
	}

	private SelectItem[] getBiddingDropdownValues(String moduleCode,
			String docType) {
		SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
		// Filter By - statusId
		List<Long> statusIds = new ArrayList<Long>();
		statusIds.add(BidStatusConst.BID_PENDING_BIDDING);
		statusIds.add(BidStatusConst.BID_IN_PROGRESS);
		statusIds.add(BidStatusConst.BID_RESCHEDULE_BIDDING);
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.statusId",
				SearchOperator.OPERATOR_IN.value(),
				statusIds));

		return super.getStatusDropdownValuesBySearchBean(
				moduleCode, 
				docType, 
				searchBean);
	}

	/**
	 * Method to navigate to Bid Management Screen.
	 * @param scheduledBid
	 * @throws PortalException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void navigateToBidManagementAction(ScBidSchedule scheduledBid)
			throws PortalException, SystemException, IOException {
		this.getManagedBean().setSelectedBidSchedule(scheduledBid);
		 navigateUsingExternalContext(scheduledBid);
		//return getNavigationURL(scheduledBid);
	}

	
	private void navigateUsingExternalContext(ScBidSchedule scheduledBid)
			throws PortalException, SystemException, IOException {

		ThemeDisplay themeDisplay = (ThemeDisplay) new FacesScopes()
				.getRequestScope(WebKeys.THEME_DISPLAY);
		String commonUrl = themeDisplay.getURLCurrent().substring(
				0,
				themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())
						+ themeDisplay.getLayout().getGroup().getFriendlyURL()
								.length());

		System.out.println("$$$$$$$$$$$ commonUrl" + commonUrl);

		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.redirect(commonUrl + "/"
				+ SourcingConstants.NAV_PAGE_VIEW_BIDDING
				+ SourcingConstants.NAV_PARAM_BID_SCHEDULE_ID
				+ SourcingConstants.NAV_PARAM_SEPARATOR
				+ scheduledBid.getBidScheduleId());
	}

	/**
	 * Pre-Renderer method for bidding list screen
	 */
	public void viewBiddingPreRender() {
		IBiddingSO biddingSO = null;
		try {
			HttpServletRequest servletRequest = PortalUtil
					.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(servletRequest);
			String bidScheduleId = originalServletRequest
					.getParameter(SourcingConstants.NAV_PARAM_BID_SCHEDULE_ID);
			BiddingBackingBean biddingBB = new BiddingBackingBean();
			ScBidSchedule selecteedBidSchedule = null;
			if (this.getManagedBean().getSelectedBidSchedule() != null) {
				selecteedBidSchedule = this.getManagedBean()
						.getSelectedBidSchedule();

			} else {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
				BiddingReqDTO reqDTO = new BiddingReqDTO();
				reqDTO.setBidScheduleId(Long.parseLong(bidScheduleId));
				selecteedBidSchedule = biddingSO
						.retrieveBidScheduleById(reqDTO).getScBidSchedule();
			}
			biddingBB.getManagedBean().setScheduledBid(selecteedBidSchedule);
			biddingBB.bidManagementAction(selecteedBidSchedule);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"bidManagementForPTJPortlet.xhtml?faces-redirect=true");

		} finally {
			biddingSO = null;
		}
	}
}
