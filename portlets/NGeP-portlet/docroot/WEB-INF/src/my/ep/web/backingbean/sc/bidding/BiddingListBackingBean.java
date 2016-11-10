package my.ep.web.backingbean.sc.bidding;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.BiddingListManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

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
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, docType);
			List<PmStatusDesc> requiredStatusDescs = new ArrayList<PmStatusDesc>();
			for (PmStatusDesc statusDesc : statusDescs) {
				if (statusDesc.getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_PENDING_BIDDING)
						|| statusDesc.getPmStatus().getStatusId()
								.equals(BidStatusConst.BID_IN_PROGRESS)
						|| statusDesc.getPmStatus().getStatusId()
								.equals(BidStatusConst.BID_RESCHEDULE_BIDDING)) {
					requiredStatusDescs.add(statusDesc);
				}

			}

			items = new SelectItem[requiredStatusDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDesc : requiredStatusDescs) {

				items[i++] = new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName());

			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

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
