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
package my.ep.web.backingbean.sc.pendingresponseqtdp;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.service.sc.supplierintivitation.ISCSupplierInvitationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.supplierintivitation.ScSupplierInvitationReqDTO;
import my.ep.dto.sc.supplierintivitation.ScSupplierInvitationResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.pendingresponseqtdp.PendingResponseDpQtManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * The Class PendingResponseDpQtBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean(name = "pendingResponseDpQtBB")
@RequestScoped
public class PendingResponseDpQtBackingBean extends
		SourcingBaseBackingBean<PendingResponseDpQtManagedBean> {
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(PendingResponseDpQtBackingBean.class);

	public PendingResponseDpQtBackingBean() {

	}

	@PostConstruct
	public void init() {

	}

	private void searchQuotationTenderCount(String type) {
		log.debug("start searchQuotationTenderCount" + type);
		ScSupplierInvitationReqDTO reqDTO = new ScSupplierInvitationReqDTO();
		// BSV
		ISearchBean<Object[]> searchBean = new SearchBean<Object[]>();

		PmParameter parameter = getParameterByTypeAndCode(
				ParameterTypeConst.SC_PROCUREMENT_MODE, type);
		// QT Proposal
		reqDTO.setStatusId(QTStatusConst.QT_PROPOSAL_PREPARATION);
		reqDTO.setParameterId(parameter != null ? parameter.getParameterId()
				: null);

		ISCSupplierInvitationSO invitationSO = null;
		ScSupplierInvitationResDTO resDTO = new ScSupplierInvitationResDTO();
		try {
			invitationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					ISCSupplierInvitationSO.class);
			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			if (userId != null) {
				reqDTO.setUserId(userId);
				reqDTO.setSearchBean(searchBean);
				resDTO = invitationSO.retrieveSupplierInviteQtsByUserId(reqDTO);
				if (null != resDTO.getSupplierInvitationList()
						&& null != resDTO.getSupplierInvitationList()
								.getResultList()) {
					if (SourcingConstants.SIT_QUOTATION.equalsIgnoreCase(type)) {
						this.getManagedBean().setQuotationCount(
								resDTO.getSupplierInvitationList()
										.getResultList().size());
					} else if (SourcingConstants.SIT_TENDER
							.equalsIgnoreCase(type)) {
						this.getManagedBean().setTenderCount(
								resDTO.getSupplierInvitationList()
										.getResultList().size());
					}

				}
			}
			log.debug("end searchQuotationTenderCount" + type);
		} finally {
			invitationSO = null;
		}
	}

	private void searchBriefingSiteVisitCount() {
		log.debug("start searchBriefingSiteVisitCount");
		ScSupplierInvitationReqDTO reqDTO = new ScSupplierInvitationReqDTO();
		// BSV
		ISearchBean<Object[]> searchBean = new SearchBean<Object[]>();

		reqDTO.setStatusId(QTStatusConst.QT_BRIEFING_SITE_VISIT);

		ISCSupplierInvitationSO invitationSO = null;
		ScSupplierInvitationResDTO resDTO = new ScSupplierInvitationResDTO();
		try {
			invitationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					ISCSupplierInvitationSO.class);
			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			if (userId != null) {
				reqDTO.setUserId(userId);
				reqDTO.setSearchBean(searchBean);
				resDTO = invitationSO.retrieveSupplierInviteQtsByUserId(reqDTO);
				if (null != resDTO.getSupplierInvitationList()
						&& null != resDTO.getSupplierInvitationList()
								.getResultList()) {
					this.getManagedBean().setBriefingSiteVisitCount(
							resDTO.getSupplierInvitationList().getResultList()
									.size());

				}

			}
			log.debug("end searchBriefingSiteVisitCount");

		} finally {
			invitationSO = null;
		}
	}

	private void searchBiddingCount() {
		log.debug("start searchBiddingCount");
		ScSupplierInvitationReqDTO reqDTO = new ScSupplierInvitationReqDTO();
		// BSV
		ISearchBean<Object[]> searchBean = new SearchBean<Object[]>();

		reqDTO.setStatusId(QTStatusConst.SF_PENDING_BIDDING);
		// move code to ScSupplierInvitationDAO to sync.
//		List<Long> sqStatusIds = new ArrayList<Long>();
//		sqStatusIds.add(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE);
//		searchBean.getDataFilterBeans().add(
//				new SearchFilterBean("sqStatus.STATUS_ID",
//						SearchOperator.OPERATOR_IN.value(), sqStatusIds));
//
//		searchBean.getDataFilterBeans().add(
//				new SearchFilterBean("quote.END_DATE",
//						SearchOperator.OPERATOR_GREATER_EQUAL.value(),
//						new Date()));
//
//		searchBean.getSortColumns().add(
//				new SearchOrderBean("to_char(quote.CREATED_DATE,'yyyy-MM-dd')",
//						false));

		ISCSupplierInvitationSO invitationSO = null;
		ScSupplierInvitationResDTO resDTO = new ScSupplierInvitationResDTO();
		try {
			invitationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					ISCSupplierInvitationSO.class);
			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			if (userId != null) {
				reqDTO.setUserId(userId);
				reqDTO.setSearchBean(searchBean);
				resDTO = invitationSO.retrieveSupplierInviteQtsByUserId(reqDTO);
				if (null != resDTO.getSupplierInvitationList()
						&& null != resDTO.getSupplierInvitationList()
								.getResultList()) {
					this.getManagedBean().setBiddingCount(
							resDTO.getSupplierInvitationList().getResultList()
									.size());
				}

			}
			log.debug("end searchBiddingCount");

		} finally {
			invitationSO = null;
		}
	}

	private void searchSimpleQuoteCount() {
		log.debug("start searchSimpleQuoteCount");
		ScSupplierInvitationReqDTO reqDTO = new ScSupplierInvitationReqDTO();
		// BSV
		ISearchBean<Object[]> searchBean = new SearchBean<Object[]>();

		reqDTO.setStatusId(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE);
		// move code to ScSupplierInvitationDAO to sync.
//		List<Long> sqStatusIds = new ArrayList<Long>();
//		sqStatusIds.add(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE);
//		searchBean.getDataFilterBeans().add(
//				new SearchFilterBean("sqStatus.STATUS_ID",
//						SearchOperator.OPERATOR_IN.value(), sqStatusIds));
//
//		searchBean.getDataFilterBeans().add(
//				new SearchFilterBean("quote.END_DATE",
//						SearchOperator.OPERATOR_GREATER_EQUAL.value(),
//						new Date()));
//
//		searchBean.getSortColumns().add(
//				new SearchOrderBean("to_char(quote.CREATED_DATE,'yyyy-MM-dd')",
//						false));

		ISCSupplierInvitationSO invitationSO = null;
		ScSupplierInvitationResDTO resDTO = new ScSupplierInvitationResDTO();
		try {
			invitationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					ISCSupplierInvitationSO.class);
			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			if (userId != null) {
				reqDTO.setUserId(userId);
				reqDTO.setSearchBean(searchBean);
				resDTO = invitationSO.retrieveSupplierInviteQtsByUserId(reqDTO);
				if (null != resDTO.getSupplierInvitationList()
						&& null != resDTO.getSupplierInvitationList()
								.getResultList()) {
					this.getManagedBean().setSimpleQuoteCount(
							resDTO.getSupplierInvitationList().getResultList()
									.size());
				}

			}
			log.debug("end searchSimpleQuoteCount");

		} finally {
			invitationSO = null;
		}
	}

	/**
	 * navigate to suppliers page under DP
	 */
//	public void navigatetoSupplierredirect() {
//
//		ThemeDisplay themeDisplay = (ThemeDisplay) new FacesScopes()
//				.getRequestScope(WebKeys.THEME_DISPLAY);
//		try {
//			String commonUrl = themeDisplay.getURLCurrent().substring(
//					0,
//					themeDisplay.getURLCurrent().indexOf(
//							themeDisplay.getLayout().getGroup()
//									.getFriendlyURL())
//							+ themeDisplay.getLayout().getGroup()
//									.getFriendlyURL().length());
//			
//			String screenUrl = String
//					.valueOf(SourcingConstants.NAV_PAGE_PROPOSAL_INVITATION);
//			StringBuilder navigationUrl = new StringBuilder();
//			navigationUrl.append(commonUrl).append("/").append(screenUrl);
//			ExternalContext externalContext = FacesContext.getCurrentInstance()
//					.getExternalContext();
//			externalContext.redirect(navigationUrl.toString());
//			log.info("getNavigationUrl ==" + navigationUrl.toString());
//		} catch (IOException e) {
//			log.error(e);
//		} catch (PortalException e) {
//			log.error(e);
//		} catch (SystemException e) {
//			log.error(e);
//		}
//	}
	
	
	/**
	 * navigate to suppliers page under DP
	 */
	public void navigatetoSupplierredirectDp() {

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
			
			String screenUrl = String
					.valueOf(SourcingConstants.NAV_PAGE_SIMPLE_QUOTE_RESPOND);
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
	 * navigate to suppliers page under QT
	 */
	public void navigatetoSupplierredirectQt() {

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
			
			String screenUrl = String
					.valueOf(SourcingConstants.NAV_PAGE_QUOTATION_TENDER_PROPOSAL_PREPARATION);
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
	 * navigate to PreRenderResponse page
	 */
	public void preRenderPendingResposeDpQt() {

		try {
			HttpServletRequest servletRequest = PortalUtil
					.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(servletRequest);
			searchQuotationTenderCount(SourcingConstants.SIT_QUOTATION);
			searchQuotationTenderCount(SourcingConstants.SIT_TENDER);
			searchBriefingSiteVisitCount();
			searchBiddingCount();
			searchSimpleQuoteCount();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.PENDING_RESPONSE_DP_QT);

		} finally {

		}
	}

}
