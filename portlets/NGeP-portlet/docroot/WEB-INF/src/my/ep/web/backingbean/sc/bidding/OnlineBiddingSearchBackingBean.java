/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.bidding;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.BiddingConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.OnlineBiddingSearchBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.bidding.OnlineBiddingSearchManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * #8755 -start- Online Bidding Search Backing Bean
 * 
 * @author manga
 * 
 */
@ManagedBean(name = "onlineBidSearchBB")
@ViewScoped
public class OnlineBiddingSearchBackingBean extends
		BaseBackingBean<OnlineBiddingSearchManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger log = EpLogger
			.getLogger(OnlineBiddingSearchBackingBean.class);

	private static final DecimalFormat durationFormat = new DecimalFormat("#00");
	private static final DecimalFormat amountFormat = new DecimalFormat(
			(new CommonUtils())
					.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
	private String recommendTypeItem = ParameterConst.RECOMMEND_TYPE_ITEM;
	private String recommendTypePackage = ParameterConst.RECOMMEND_TYPE_PACKAGE;
	private Long servicetype = Long.parseLong(ParameterConst.ITEM_TYPE_SERVICE);
	private Long interval;

	private boolean isBidPaused = false;

	/**
	 * Instantiated backingbean class.
	 */
	public OnlineBiddingSearchBackingBean() {
		/**
		 * constructor data.
		 */
	}

	/**
	 * Initialized search bean related data.
	 */
	@PostConstruct
	private void init() {
		OnlineBiddingSearchBean onlineBidSearchBean = getManagedBean()
				.getOnlineBidSearch();
		if (null == onlineBidSearchBean) {
			onlineBidSearchBean = new OnlineBiddingSearchBean();
			getManagedBean().setOnlineBidSearch(onlineBidSearchBean);
		}
		getStatusDescList();
	}

	/**
	 * Navigate to page ScheduleBid search page for Supplier.
	 */
	public void prerendeOnlineBidSearch() {
		try {
			this.renewSesionObject();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.ONLINE_BIDDING_SEARCH_DETAILS);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(0),
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_DETAILS,
					getUserIdSessionFromLoginId(), e);
		}

	}

	/**
	 * Listener method for search for start date selection events
	 * 
	 * @param event
	 */
	public void startDateSelectListener(SelectEvent event) {

		getManagedBean().getOnlineBidSearch().setSearchBiddingDateToMinDate(
				(Date) event.getObject());

	}

	/**
	 * method to fetch status drop down list values
	 * 
	 * @return
	 */
	public void getStatusDescList() {
		SelectItem[] statusDescs = getManagedBean().getOnlineBidSearch()
				.getStatusDescs();
		if (statusDescs == null || statusDescs.length == 0) {
			SearchBean<PmStatusDesc> statusSearchBean = new SearchBean<>();
			// Filter By - statusId
			List<Long> statusIds = Arrays
					.asList(BidStatusConst.BID_HAS_BEEN_CANCELED);

			statusSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							SourcingConstants.QT_PMSTATUS_STATUS_ID,
							SearchOperator.OPERATOR_NOT_IN.value(), statusIds));
			statusDescs = getStatusDropdownValuesBySearchBean(
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(),
					ParameterConst.DOCTYPE_BIDDING, statusSearchBean);
			getManagedBean().getOnlineBidSearch().setStatusDescs(statusDescs);

		}

	}

	/**
	 * search bidding details for supplier view.
	 */
	public void searchForScheduledBids() {
		this.getManagedBean().getScheduleBidDataModel().reloadData();
	}

	/**
	 * fetch bidding details for supplier view paging datamodel.
	 * 
	 * @author mangaiah
	 * 
	 */
	private class OnlineBiddingPagingDataModel extends
			PagingDataModel<ScBidSchedule> {

		private static final long serialVersionUID = 1L;

		public OnlineBiddingPagingDataModel() {
			super(QTConstants.ONLINE_BIDDING_SEARCH_LIST, Boolean.FALSE);

		}

		@Override
		public ISearchResultBean<ScBidSchedule> pageChangeListener(
				ISearchBean<ScBidSchedule> bidSchSearchBean) {

			IBiddingSO biddingSO = null;

			try {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
				OnlineBiddingSearchBean bidSearchDtls = getManagedBean()
						.getOnlineBidSearch();
				String bidNumber = bidSearchDtls.getSearchBiddingNo();
				if (StringUtils.isNotBlank(bidNumber)) {

					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									SourcingConstants.QT_SEARCH_BID_NUMBER,
									SearchOperator.OPERATOR_LIKE.value(),
									bidNumber, Boolean.TRUE));
				}
				String docNumber = bidSearchDtls.getSearchDocumentNo();
				if (StringUtils.isNotBlank(docNumber)) {

					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									SourcingConstants.BIDDING_DOC_NO,
									SearchOperator.OPERATOR_LIKE.value(),
									docNumber, Boolean.TRUE));
				}
				String docTitle = bidSearchDtls.getSearchBidTitle();
				if (StringUtils.isNotBlank(docTitle)) {

					bidSchSearchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									SourcingConstants.ONLINE_BIDDING_SEARCH_DOC_TITLE,
									SearchOperator.OPERATOR_LIKE.value(),
									docTitle, Boolean.TRUE));
				}

				// should be like bidstart from date to bidstart to date
				Date fromDate = bidSearchDtls.getSearchBiddingDateFrom();
				Date toDate = bidSearchDtls.getSearchBiddingDateTo();
				if (null != fromDate && null != toDate) {

					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									SourcingConstants.QT_SEARCH_BID_START_DATE,
									SearchOperator.OPERATOR_BETWEEN.value(),
									new SearchBetweenValue<Date>(fromDate,
											toDate)));
				}
				Long statusDesc = bidSearchDtls.getSelectedStatusDesc();
				if (null != statusDesc) {
					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									SourcingConstants.QT_PMSTATUS_STATUS_ID,
									SearchOperator.OPERATOR_EQUALS.value(),
									statusDesc));
				} else {

					bidSchSearchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									SourcingConstants.QT_PMSTATUS_STATUS_ID,
									SearchOperator.OPERATOR_IN.value(),
									Arrays.asList(
											BidStatusConst.BID_PENDING_BIDDING,
											BidStatusConst.BID_IN_PROGRESS,
											BidStatusConst.BID_COMPLETED,
											BidStatusConst.BID_PAUSED,
											BidStatusConst.BID_RESCHEDULE_BIDDING,
											BidStatusConst.BID_CANCELLED)));
				}

				// Filter By - Supplier
				filterBySupplier(bidSchSearchBean);

				bidSchSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								SourcingConstants.ONLINE_BIDDING_SUPPLIER_ACTIVE_SCHEDULES,
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));

				// Order by created date
				bidSchSearchBean.getSortColumns().add(
						new SearchOrderBean(
								SourcingConstants.QT_SEARCH_BID_START_DATE,
								Boolean.FALSE));

				BiddingReqDTO reqDTO = new BiddingReqDTO();

				reqDTO.setBidSchSearchBean(bidSchSearchBean);
				BiddingResDTO resDTO = biddingSO
						.searchBiddingScheduleList(reqDTO);

				return resDTO.getBidSchResultBean();

			}

			catch (Exception e) {
				SLAUtils.getInstance().logSLAError(
						ModuleCodeConst.SUPPLIER_SELECTION,
						SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
						String.valueOf(0),
						SLALogConstant.VIEW_ONLINE_BIDDING_PAGE_LISTNER,
						getUserIdSessionFromLoginId(), e);
			}
			return null;
		}

		/**
		 * Gets the supplier by user id.
		 * 
		 * @param userId
		 *            the user id
		 * @return the supplier by user id
		 */
		private List<Long> getSupplierByUserId(Long userId) {
			// To get supplier id with matched hash supplier id.
			List<Long> supplierIds = new ArrayList<>();
			ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(userId);
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierSearchReqDTO);
			if (CollectionUtils.isNotEmpty(smResDTO.getResultBean()
					.getResultList())) {

				for (SmSupplier suppplier : smResDTO.getResultBean()
						.getResultList()) {
					supplierIds.add(suppplier.getSupplierId());
				}
			}
			return supplierIds;
		}

		/**
		 * fetch bid details based on login user.
		 * 
		 * @param bidSchSearchBean
		 * @return searchBean.
		 * 
		 */
		private ISearchBean<ScBidSchedule> filterBySupplier(
				ISearchBean<ScBidSchedule> bidSchSearchBean) {
			FacesScopes userScope = new FacesScopes();
			Long userId = userScope.getUserSessionBean().getUserId();

			List<Long> supplierIds = getSupplierByUserId(userId);
			if (CollectionUtils.isEmpty(supplierIds)) {
				return bidSchSearchBean;
			}
			final int maxSize = DbValueConst.MAX_FILTER_SIZE;
			final int total = supplierIds.size();
			if (total <= maxSize) {
				bidSchSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								SourcingConstants.ONLINE_BIDDING_SUPPLIER_SCHEDULE_IDS,
								SearchOperator.OPERATOR_IN.value(),
								new HashSet<>(supplierIds)));
				return bidSchSearchBean;
			}
			for (int start = 0, chunkIndex = 0; start < total; start += maxSize, chunkIndex++) {
				final int end = Math.min(start + maxSize, total);
				final Set<Long> chunk = new HashSet<>(supplierIds.subList(
						start, end));

				if (chunkIndex == 0) {
					bidSchSearchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									SourcingConstants.ONLINE_BIDDING_SUPPLIER_SCHEDULE_IDS,
									SearchOperator.OPERATOR_IN.value(), chunk));
				} else {
					bidSchSearchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									SourcingConstants.ONLINE_BIDDING_SUPPLIER_SCHEDULE_IDS,
									SearchOperator.OPERATOR_IN.value(), chunk,
									SearchCondition.CONDITION_OR.value()));
				}
			}
			return bidSchSearchBean;
		}

	}

	/**
	 * This method is used to view the BidSchedule details for the supplier.
	 * 
	 * @param bidSchedule
	 * @param viewId
	 * @return redirect to BidDetailspage.
	 * 
	 */
	public String viewOnlineBiddingDtls(ScBidSchedule bidSchedule, String viewId) {
		try {
			PmUser supplierUser = new FacesScopes().getUserSessionBean();
			Long bidScheduleId = bidSchedule.getBidScheduleId();
			Long userId = supplierUser.getUserId();
			retrieveOnlineBiddingDtls(bidScheduleId, userId);
			fetchTolerancePrice();
			fetchDecBlkMaxPercentageRuleValue();
			return QTConstants.ONLINE_BIDDING_SEARCH_VIEW_DETAILS;
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(bidSchedule),
					SLALogConstant.VIEW_ONLINE_BIDDING_SUPPLIER_DETAILS,
					getUserIdSessionFromLoginId(), e);
		}
		return QTConstants.ONLINE_BIDDING_SEARCH_VIEW_DETAILS;
	}

	/**
	 * This method is used to view the bid search screen. 
	 */
	public void backToPreviousSearchDtls() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						QTConstants.ONLINE_BIDDING_SEARCH_DETAILS);
	}

	/**
	 * Get Online Bidding Dtls
	 * 
	 * @param bidScheduleId
	 * @param userId
	 */
	public void retrieveOnlineBiddingDtls(Long bidScheduleId, Long userId) {

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setUserID(userId);
			reqDTO.setBidScheduleId(bidScheduleId);

			reqDTO.setOnlineBidSearch(isOnlineBidSearchPortlet());

			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidScheduleDtl(reqDTO);

			if (resDTO.isSuccessful()) {

				ScBidSchedule bidSchedule = resDTO.getScBidSchedule();

				this.getManagedBean().setActiveBidSchedules(
						resDTO.getActiveBidSchedules());
				this.getManagedBean().setScBidSchedule(bidSchedule);

				if (null != bidSchedule) {
					fetchBidQtDetails(bidSchedule);
					this.getManagedBean().setPreviousStatusId(
							bidSchedule.getPmStatus().getStatusId());
					this.getManagedBean().setSelectedBidScheduleId(
							bidSchedule.getBidScheduleId());

					setBidDetailsPause(bidSchedule);
				}
				this.getManagedBean().setBidContactPersons(
						resDTO.getBidContactPersons());
				this.getManagedBean().setSmSupplier(resDTO.getSmSupplier());

				if (resDTO.getPackageRanking() != null) {
					this.getManagedBean().setPackageRanking(
							resDTO.getPackageRanking());
				}

				ScBidAction scBidAction = resDTO.getScBidAction();
				setBidAction(scBidAction);

				if (resDTO.getBidPrices() != null) {
					this.getManagedBean().setBidPrices(resDTO.getBidPrices());
				}

				if (null != bidSchedule && bidSchedule.getScBid() != null
						&& resDTO.getSmSupplier() != null) {
					Long docId = bidSchedule.getScBid().getDocId();
					Long supplierId = resDTO.getSmSupplier().getSupplierId();
					checkSupplierDeleted(docId, supplierId);
				}

			}

		}

		catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(bidScheduleId),
					SLALogConstant.RETRIEVE_ONLINE_BIDDING_DETAILS,
					getUserIdSessionFromLoginId(), e);
		}
		itemStartPriceTotal();

	}

	/**
	 * check supplier deleted from the branch.
	 * 
	 * @param docId
	 * @param supplierId
	 */
	private void checkSupplierDeleted(Long docId, Long supplierId) {
		if (checkDisqualifiedSupplierBranchDeleted(docId, supplierId)) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.INFO_760522,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
	}

	/**
	 * check this portlet is from Online Bid Search.
	 * 
	 * @return true/false.
	 */
	private boolean isOnlineBidSearchPortlet() {
		return QTConstants.PORTLET_NAME_ONLINE_BIDDING_SEARCH
				.equals(EpPortletUtil.getDisplayPortletId());
	}

	/**
	 * fetch qt details Based on BidSchedule details.
	 * 
	 * @param bidSchedule
	 */
	private void fetchBidQtDetails(ScBidSchedule bidSchedule) {
		IQTDetailSO qTDetailSO = null;
		try {

			qTDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO qtreqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO qtresDTO = new QuationTenderResDTO();
			if (null != bidSchedule && null != bidSchedule.getScBid()) {
				String documentNumber = bidSchedule.getScBid().getDocNo();
				qtreqDTO.setQtTndrNo(documentNumber);
				qtresDTO = qTDetailSO.retriveQtByQtNumber(qtreqDTO);
			}
			if (null != qtresDTO.getQtDetail()) {
				this.getManagedBean().setScQt(qtresDTO.getQtDetail());
			}
		} catch (Exception e) {
			Long bidScheduleId = 0L;
			if (null != bidSchedule) {
				bidScheduleId = bidSchedule.getBidScheduleId();
			}
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(bidScheduleId),
					SLALogConstant.FETCH_BID_QT_DETAILS,
					getUserIdSessionFromLoginId(), e);
		}
	}

	/**
	 * click on reset button clear user keyin search details.
	 */
	public void resetOnlineBidSearchDtls() {
		getManagedBean().setOnlineBidSearch(new OnlineBiddingSearchBean());
	}

	/**
	 * Method to calculate remaining time after bid has been paused
	 * 
	 * @return remaining time string.
	 */
	private String remainingTimeAfterPauseCalculator() {
		ScBidAction scBidAction = this.getManagedBean().getScBidAction();
		String colon = ":";
		if (scBidAction != null
				&& scBidAction.getActionType().equalsIgnoreCase(
						BiddingConst.BID_ACTION_TYPE_PAUSED)) {
			long durationInMills = this.getManagedBean().getScBidSchedule()
					.getEndDate().getTime()
					- scBidAction.getCreatedDate().getTime();

			long diffSeconds = durationInMills / 1000 % 60;
			long diffMinutes = durationInMills / (60 * 1000) % 60;
			long diffHours = durationInMills / (60 * 60 * 1000) % 24;
			return durationFormat.format(diffHours) + colon
					+ durationFormat.format(diffMinutes) + colon
					+ durationFormat.format(diffSeconds);

		} else {
			return "";
		}
	}

	/**
	 * search OnlineBidding supplier Schedule Details.
	 * 
	 * @return the scheduleBidDataModel
	 */
	public IPagingDataModel<ScBidSchedule> getScheduleBidDataModel() {
		IPagingDataModel<ScBidSchedule> scheduleBidDataModel = getManagedBean()
				.getScheduleBidDataModel();
		if (null == scheduleBidDataModel) {
			scheduleBidDataModel = new OnlineBiddingPagingDataModel();
			this.getManagedBean().setScheduleBidDataModel(scheduleBidDataModel);
		}
		return scheduleBidDataModel;
	}

	/**
	 * Listener method for Bid selection events.
	 * 
	 */
	public void selectedBidChangeListener() {
		this.getManagedBean().getSelectedBidScheduleId();

	}

	/**
	 * Check whether Date is null or not
	 * 
	 * @param publishDate
	 * @param createdDate
	 * @return Date
	 */
	public Date retriveDatesforOrgProfile(Date publishDate, Date createdDate) {
		if (publishDate == null) {
			return createdDate;
		}
		return publishDate;
	}

	/**
	 * method to calculate remaining time for bid
	 * 
	 * @return the remainingBidTime
	 */
	public Long getRemainingBidTime() {
		ScBidSchedule scBidSchedule = this.getManagedBean().getScBidSchedule();
		Long remainingBidTime = 0L;
		if (null != scBidSchedule) {
			Long statusId = scBidSchedule.getPmStatus().getStatusId();
			if (BidStatusConst.BID_IN_PROGRESS

			.equals(statusId)) {

				remainingBidTime = durationCalculator(new Date(),
						scBidSchedule.getEndDate());
			}

			else if (BidStatusConst.BID_PENDING_BIDDING.equals(statusId)) {

				remainingBidTime = durationCalculator(new Date(),
						scBidSchedule.getStartDate());

			} else {
				remainingBidTime = 0L;
			}
		}

		return remainingBidTime;
	}

	/**
	 * Duration calculator.
	 * 
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the long
	 */
	private Long durationCalculator(Date startDate, Date endDate) {

		long durationInMills = endDate.getTime() - startDate.getTime();

		return TimeUnit.SECONDS.convert(durationInMills, TimeUnit.MILLISECONDS);

	}

	/**
	 * Method to check if this supplier disqualified
	 * 
	 * @param qtId
	 * @param supplierId
	 * @return true or false
	 **/
	public Boolean checkDisqualifiedSupplierBranchDeleted(Long qtId,
			Long supplierId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						ISourcingIntegrationSO.class);
		return sourcingIntegrationSO
				.flagBiddingDisqualifiedSupplierBranchDeleted(qtId, supplierId);
	}

	/**
	 * Method to check if any zones are disqualified
	 * 
	 * @param qtId
	 * @param supplierId
	 * @param specZoneId
	 * @return true or false
	 **/
	public Boolean checkZonesDisqualifiedSupplierBranchDeleted(Long qtId,
			Long supplierId, Long specZoneId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						ISourcingIntegrationSO.class);
		return sourcingIntegrationSO
				.flagZonesBiddingDisqualifiedSupplierBranchDeleted(qtId,
						supplierId, specZoneId);
	}

	/**
	 * Action method to populate item details
	 * 
	 * @param scBidPrice
	 */
	public void itemDetialsPopupAction(ScBidPrice scBidPrice) {
		this.getManagedBean().setScBidPrice(scBidPrice);
		scBidPrice.getScBidSupplierItem().getScBidItem().getScRequestItem()
				.getTplSpecQuestion().getSpecQuestionId();
	}

	/**
	 * Listener method for spinner component change events.
	 * 
	 * @param component
	 * @param newValue
	 */
	public void decrementAmountValueChangeListener(UIComponent component,
			Object newValue) {

		Object scBidPriceObj = component.getNamingContainer().getAttributes()
				.get("scBidPrice");

		if (scBidPriceObj != null) {
			ScBidPrice newBidPrice = new ScBidPrice();
			BigDecimal newPrice = null;
			ScBidPrice scBidPrice = (ScBidPrice) scBidPriceObj;

			if (newValue != null && newValue instanceof BigDecimal) {
				BigDecimal value = (BigDecimal) newValue;

				if (scBidPrice.getBidPrice() == null) {
					newPrice = scBidPrice.getScBidSupplierItem()
							.getItemStartPrice().subtract(value);
				}

				else {
					newPrice = scBidPrice.getBidPrice().subtract(value);
				}
			}

			newBidPrice.setBidPrice(newPrice);
			newBidPrice.setScBidSupplierItem(scBidPrice.getScBidSupplierItem());
			scBidPrice.setNewBidPrice(newBidPrice);
		}
	}

	/**
	 * method to calculate item start price total.
	 * 
	 * @param bidPriceItem
	 * @return the totalStartprice.
	 */
	public String startPriceTotal(ScBidPrice bidPriceItem) {

		BigDecimal lastStartPriceTotal = null;
		if (bidPriceItem.getScBidSupplierItem().getItemStartPrice() != null) {
			lastStartPriceTotal = TemplateSpecHelper.getInstance()
					.calcTotalAmount(
							bidPriceItem.getScBidSupplierItem().getScBidItem()
									.getScRequestItem().getTplSpecQuestion(),
							bidPriceItem.getScBidSupplierItem()
									.getItemStartPrice(),
							bidPriceItem.getScBidSupplierItem().getScBidItem()
									.getQty());
		}
		if (lastStartPriceTotal != null
				&& (lastStartPriceTotal.compareTo(BigDecimal.valueOf(0.0d)) > 0)) {
			return amountFormat.format(lastStartPriceTotal);
		} else {
			return null;
		}
	}

	/**
	 * method to calculate the last bid price total.
	 * 
	 * @param bidPriceItem
	 * @return the lastBidPriceTotal
	 */
	public String lastBidPriceTotal(ScBidPrice bidPriceItem) {

		BigDecimal lastBidPriceTotal = BigDecimal.ZERO;
		if (bidPriceItem.getBidPrice() != null) {
			lastBidPriceTotal = TemplateSpecHelper.getInstance()
					.calcTotalAmount(
							bidPriceItem.getScBidSupplierItem().getScBidItem()
									.getScRequestItem().getTplSpecQuestion(),
							bidPriceItem.getBidPrice(),
							bidPriceItem.getScBidSupplierItem().getScBidItem()
									.getQty());
		}
		if (lastBidPriceTotal != null
				&& (lastBidPriceTotal.compareTo(BigDecimal.valueOf(0.0d)) > 0)) {
			return amountFormat.format(lastBidPriceTotal);
		} else {
			return null;
		}
	}

	/**
	 * Action method to confirm and submit bid price.
	 * 
	 * @param scBidPrice
	 */
	public void confirmAndSubmitBidPrice(ScBidPrice scBidPrice) {

		BigDecimal newPrice;
		Double lastBidpriceTotal = null;
		Double newBidpriceTotal = null;
		if (scBidPrice
				.getNewPrice()
				.remainder(
						scBidPrice.getScBidSupplierItem().getScBidItem()
								.getDecrementAmt())
				.compareTo(BigDecimal.valueOf(0)) != 0) {

			throw new EpException(MessageConst.ERROR_760301);
		}
		if (scBidPrice.getBidPrice() == null
				&& scBidPrice.getNewPrice() != null) {

			newPrice = BigDecimal.valueOf(scBidPrice.getScBidSupplierItem()
					.getItemStartPrice().doubleValue()
					- scBidPrice.getNewPrice().doubleValue());
			lastBidpriceTotal = getBidPriceTotals(scBidPrice, scBidPrice
					.getScBidSupplierItem().getItemStartPrice());
			newBidpriceTotal = getBidPriceTotals(scBidPrice, newPrice);

		}

		else if (scBidPrice.getBidPrice() != null
				&& scBidPrice.getNewPrice() != null) {

			newPrice = BigDecimal.valueOf(scBidPrice.getBidPrice()
					.doubleValue() - scBidPrice.getNewPrice().doubleValue());
			lastBidpriceTotal = getBidPriceTotals(scBidPrice,
					scBidPrice.getBidPrice());
			newBidpriceTotal = getBidPriceTotals(scBidPrice, newPrice);

		}

		isTolerancePackageType();

		if (lastBidpriceTotal != null && newBidpriceTotal != null) {

			this.getManagedBean().setIndex(
					this.getManagedBean().getBidPrices().indexOf(scBidPrice));
			Double diffPercentage = ((lastBidpriceTotal - newBidpriceTotal) / lastBidpriceTotal) * 100;
			if (diffPercentage > this.getManagedBean()
					.getDecrementBlockPercentage().doubleValue()) {

				this.getManagedBean().setScBidPrice(scBidPrice);
				RequestContext.getCurrentInstance().execute(
						QTConstants.ONLINE_BIDDING_PRICE_POPUP_VIEW);
			} else {

				this.getManagedBean().setScBidPrice(scBidPrice);
				updateBidPriceAndCalculateRank();
			}
		}

	}

	/**
	 * Action method to update bid price and calculate rank.
	 */
	public void updateBidPriceAndCalculateRank() {
		log.info("updateBidPriceNDcalculateRank");

		long startTime = System.currentTimeMillis();

		ScBidPrice priceItemToPersist;
		priceItemToPersist = this.getManagedBean().getScBidPrice()
				.getNewBidPrice();
		priceItemToPersist
				.setBidTotalPrice(TemplateSpecHelper.getInstance()
						.calcTotalAmount(
								this.getManagedBean().getScBidPrice()
										.getScBidSupplierItem().getScBidItem()
										.getScRequestItem()
										.getTplSpecQuestion(),
								this.getManagedBean().getScBidPrice()
										.getNewBidPrice().getBidPrice(),
								this.getManagedBean().getScBidPrice()
										.getScBidSupplierItem().getScBidItem()
										.getQty()));

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setNewBidPrice(priceItemToPersist);

			BiddingResDTO resDTO = biddingSO.submitBidPrice(reqDTO);

			if (resDTO.isSuccessful()) {
				ScBidPrice persistedItem = resDTO.getScBidPrice();
				persistedItem.setNewPrice(BigDecimal.valueOf(0d));
				this.getManagedBean().getBidPrices()
						.set(this.getManagedBean().getIndex(), persistedItem);

				if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(this
						.getManagedBean().getScBidSchedule().getScBid()
						.getRecommendedTypeId().getParameterCode())) {
					this.getManagedBean().setPackageRanking(
							resDTO.getPackageRanking());
				}

				SLAUtils.getInstance()
						.logSLAInfo(ModuleCodeConst.SUPPLIER_SELECTION,
								ParameterConst.DOCTYPE_BIDDING,
								QTConstants.VIEW_ONLINE_BIDDING_UPDATE_PRICE,
								startTime);

				RequestContext.getCurrentInstance().execute("rc()");
			}

		} catch (Exception e) {
			Long bidPriceId = priceItemToPersist.getBidPriceId();
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(bidPriceId),
					SLALogConstant.UPDATE_BID_PRICE_RANK,
					getUserIdSessionFromLoginId(), e);
		}

	}

	/**
	 * Gets the bid price totals.
	 * 
	 * @param scBidPrice
	 * 
	 * @param itemPrice
	 * 
	 * @return the bid price totals
	 */
	private Double getBidPriceTotals(ScBidPrice scBidPrice, BigDecimal itemPrice) {

		return TemplateSpecHelper
				.getInstance()
				.calcTotalAmount(
						scBidPrice.getScBidSupplierItem().getScBidItem()
								.getScRequestItem().getTplSpecQuestion(),
						itemPrice,
						scBidPrice.getScBidSupplierItem().getScBidItem()
								.getQty()).doubleValue();
	}

	/**
	 * Fetch tolerance price.
	 */
	private void fetchTolerancePrice() {
		IRuleSO ruleSO = null;

		try {
			ruleSO = getRuleSO();
			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(RuleTypeConst.BIDDING_TOLERANCE_PERCENTAGE);
			RuleResDTO ruleResDTO = ruleSO
					.getRuleValuesByTypeInCurrentDate(ruleReqDTO);

			if (CollectionUtils.isNotEmpty(ruleResDTO.getRuleValueList())) {
				PmConfigRuleValue configRulevalue = ruleResDTO
						.getRuleValueList().get(0);
				BigDecimal tolerancePercentage = new BigDecimal(
						configRulevalue.getRuleValue());
				Date ruleExpiryDate = configRulevalue.getExpDate();
				boolean isTolerancePercentage = tolerancePercentage
						.compareTo(BigDecimal.ZERO) != 0;
				if (isTolerancePercentage
						&& (ruleExpiryDate == null || !ruleExpiryDate
								.before(new Date()))) {
					this.getManagedBean().setToleranceCheck(Boolean.TRUE);
					BigDecimal tolerancePriceLowerBound = (new BigDecimal(100))
							.subtract(tolerancePercentage);
					this.getManagedBean().setTolerancePriceLowerBound(
							tolerancePriceLowerBound);

					BigDecimal tolerancePriceUpperBound = (new BigDecimal(100))
							.add(tolerancePercentage);
					this.getManagedBean().setTolerancePriceUpperBound(
							tolerancePriceUpperBound);
				}

			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(0L), SLALogConstant.FETCH_TOLERENCE_PRICE,
					getUserIdSessionFromLoginId(), e);
		}
	}

	/**
	 * Method to fetch online bidding rule values
	 */
	private void fetchDecBlkMaxPercentageRuleValue() {
		IRuleSO ruleSO = null;
		try {
			ruleSO = getRuleSO();
			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO
					.setRuleType(RuleTypeConst.BIDDING_DECREMENT_BLOCK_PERCENTAGE);
			RuleResDTO ruleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
			if (CollectionUtils.isNotEmpty(ruleResDTO.getRuleValueList())) {
				PmConfigRuleValue configRulevalue = ruleResDTO
						.getRuleValueList().get(0);
				BigDecimal decremntBlockPercentageRuleValue = new BigDecimal(
						configRulevalue.getRuleValue());
				this.getManagedBean().setDecrementBlockPercentage(
						decremntBlockPercentageRuleValue);

				this.getManagedBean().setDecrementBlockPopupMessage(
						ResourceBundleUtils.getMessage(
								QTConstants.CONFIRM_BIDDING_MESSAGE,
								new String[] { this.getManagedBean()
										.getDecrementBlockPercentage()
										.toString() },
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()));
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(0L),
					SLALogConstant.FETCH_DECREMENT_BLOCK_RULE_VALUE,
					getUserIdSessionFromLoginId(), e);
		}
	}

	/**
	 * method to calculate new itemprice total.
	 * 
	 * @param bidPriceItem
	 * @return the itemNewPriceTotal
	 */
	public String itemNewPriceTotal(ScBidPrice bidPriceItem) {
		BigDecimal newPriceTotal = null;
		if (bidPriceItem.getNewBidPrice() != null
				&& bidPriceItem.getNewBidPrice().getBidPrice() != null) {
			newPriceTotal = TemplateSpecHelper.getInstance()
					.calcTotalAmount(
							bidPriceItem.getScBidSupplierItem().getScBidItem()
									.getScRequestItem().getTplSpecQuestion(),
							bidPriceItem.getNewBidPrice().getBidPrice(),
							bidPriceItem.getScBidSupplierItem().getScBidItem()
									.getQty());
		}
		if (newPriceTotal != null
				&& (newPriceTotal.compareTo(BigDecimal.valueOf(0.0d)) > 0)) {
			return amountFormat.format(newPriceTotal);
		} else {
			return null;

		}
	}

	/**
	 * Method to calculate Item Start Price total.
	 * 
	 * @return the itemStart Price total.
	 */
	public BigDecimal itemStartPriceTotal() {

		BigDecimal startPriceTotal = BigDecimal.ZERO;
		List<ScBidPrice> bidPriceList = this.getManagedBean().getBidPrices();

		if (CollectionUtils.isNotEmpty(bidPriceList)) {
			for (ScBidPrice bidPrice : bidPriceList) {
				BigDecimal temptotal = TemplateSpecHelper.getInstance()
						.calcTotalAmount(
								bidPrice.getScBidSupplierItem().getScBidItem()
										.getScRequestItem()
										.getTplSpecQuestion(),
								bidPrice.getScBidSupplierItem()
										.getItemStartPrice(),
								bidPrice.getScBidSupplierItem().getScBidItem()
										.getQty());
				startPriceTotal = startPriceTotal.add(temptotal);
			}
		}
		return startPriceTotal;
	}

	/**
	 * method to calculate item new price total
	 * 
	 * @return the itemNewBidPriceTotal
	 */
	public BigDecimal getItemNewBidPriceTotal() {
		BigDecimal newBidPriceTotal = BigDecimal.ZERO;
		List<ScBidPrice> bidPrices = this.getManagedBean().getBidPrices();
		if (CollectionUtils.isNotEmpty(bidPrices)) {
			for (ScBidPrice bidPrice : bidPrices) {

				if (bidPrice.getNewBidPrice() != null
						&& bidPrice.getNewBidPrice().getBidPrice() != null) {
					BigDecimal temptotal = TemplateSpecHelper.getInstance()
							.calcTotalAmount(
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getScRequestItem()
											.getTplSpecQuestion(),
									bidPrice.getNewBidPrice().getBidPrice(),
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getQty());

					newBidPriceTotal = newBidPriceTotal.add(temptotal);

				} else if (bidPrice.getBidPrice() != null) {

					BigDecimal temptotal = TemplateSpecHelper.getInstance()
							.calcTotalAmount(
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getScRequestItem()
											.getTplSpecQuestion(),
									bidPrice.getBidPrice(),
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getQty());
					newBidPriceTotal = newBidPriceTotal.add(temptotal);

				} else {

					BigDecimal temptotal = TemplateSpecHelper.getInstance()
							.calcTotalAmount(
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getScRequestItem()
											.getTplSpecQuestion(),
									bidPrice.getScBidSupplierItem()
											.getItemStartPrice(),
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getQty());
					newBidPriceTotal = newBidPriceTotal.add(temptotal);

				}
			}
		}

		return newBidPriceTotal;
	}

	/**
	 * method to calculate item last bid price total
	 * 
	 * @return the itemLastBidPriceTotal
	 */
	public BigDecimal getItemLastBidPriceTotal() {

		BigDecimal lastBidPriceTotal = BigDecimal.ZERO;
		List<ScBidPrice> bidPrices = this.getManagedBean().getBidPrices();
		if (CollectionUtils.isNotEmpty(bidPrices)) {
			for (ScBidPrice bidPrice : bidPrices) {

				if (bidPrice.getBidPrice() == null) {
					BigDecimal tempTotal = TemplateSpecHelper.getInstance()
							.calcTotalAmount(
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getScRequestItem()
											.getTplSpecQuestion(),
									bidPrice.getScBidSupplierItem()
											.getItemStartPrice(),
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getQty());
					lastBidPriceTotal = lastBidPriceTotal.add(tempTotal);

				} else {
					BigDecimal tempTotal = TemplateSpecHelper.getInstance()
							.calcTotalAmount(
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getScRequestItem()
											.getTplSpecQuestion(),
									bidPrice.getBidPrice(),
									bidPrice.getScBidSupplierItem()
											.getScBidItem().getQty());
					lastBidPriceTotal = lastBidPriceTotal.add(tempTotal);

				}
			}
		}

		return lastBidPriceTotal;
	}

	/**
	 * method to calculate refresh interval for poll component
	 * 
	 * @return the interval
	 */
	public Long getInterval() {
		ScBidSchedule scBidSchedule = this.getManagedBean().getScBidSchedule();
		if (scBidSchedule.getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_PENDING_BIDDING)) {
			long diff = durationCalculator(new Date(),
					scBidSchedule.getStartDate());
			if (diff >= 0) {

				interval = durationCalculator(new Date(),
						scBidSchedule.getStartDate());
			} else {
				interval = 10L;
			}

		} else if (scBidSchedule.getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_IN_PROGRESS)
				&& durationCalculator(new Date(), scBidSchedule.getStartDate()) < 20) {
			interval = 5L;
		} else {
			interval = 10L;
		}

		return interval;
	}

	/**
	 * method to call poll listerner when the bidding status is Inprogress or
	 * pending.
	 */
	public void pollListener() {

		ScBidSchedule scBidSchedule = this.getManagedBean().getScBidSchedule();
		Long bidScheduleId = scBidSchedule.getBidScheduleId();
		try {
			SmSupplier supplier = this.getManagedBean().getSmSupplier();
			Long previousStatusId = this.getManagedBean().getPreviousStatusId();
			Long statusId = scBidSchedule.getPmStatus().getStatusId();
			boolean isPaused = this.getManagedBean().isBidPaused();
			PmUser supplierUser = new FacesScopes().getUserSessionBean();
			Long userId = supplierUser.getUserId();
			getOnlineBidScheduleDetails(scBidSchedule.getBidScheduleId(),
					supplier.getSupplierId());
			if (BidStatusConst.BID_PAUSED.equals(statusId)) {
				RequestContext.getCurrentInstance().execute(
						QTConstants.ONLINE_BIDDING_POPUP_VIEW);
			} else if (BidStatusConst.BID_PENDING_BIDDING
					.equals(previousStatusId)
					&& BidStatusConst.BID_IN_PROGRESS.equals(statusId)) {
				this.getManagedBean().setPreviousStatusId(
						BidStatusConst.BID_IN_PROGRESS);
				retrieveOnlineBiddingDtls(bidScheduleId, userId);
			} else if (BidStatusConst.BID_IN_PROGRESS.equals(previousStatusId)
					&& BidStatusConst.BID_COMPLETED.equals(statusId)) {
				this.getManagedBean().setPreviousStatusId(
						BidStatusConst.BID_COMPLETED);
				retrieveOnlineBiddingDtls(bidScheduleId, userId);
			}
			if (isPaused && BidStatusConst.BID_IN_PROGRESS.equals(statusId)) {
				retrieveOnlineBiddingDtls(bidScheduleId, userId);
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(bidScheduleId),
					SLALogConstant.POLL_LISTENER,
					getUserIdSessionFromLoginId(), e);
		}

	}

	/**
	 * Listener method to redirect if bid status changed to pause.
	 * 
	 */
	public void bidStatusNotificationAction() {
		ScBidSchedule scBidSchedule = this.getManagedBean().getScBidSchedule();
		Long bidScheduleId = scBidSchedule.getBidScheduleId();

		try {
			PmUser supplierUser = new FacesScopes().getUserSessionBean();
			Long userId = supplierUser.getUserId();
			retrieveOnlineBiddingDtls(bidScheduleId, userId);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(bidScheduleId),
					SLALogConstant.BID_STATUS_NOTIFICATION_ACTION,
					getUserIdSessionFromLoginId(), e);
		}

	}

	/**
	 * fetch the current bidSchedule details.
	 * 
	 * @param bidScheduleID
	 * @param supplierID
	 * @return empty.
	 */
	private void getOnlineBidScheduleDetails(Long bidScheduleID, Long supplierID) {
		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setBidScheduleId(bidScheduleID);
			reqDTO.setSupplierId(supplierID);

			BiddingResDTO resDTO = biddingSO.retrieveCurrentBidDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setPackageRanking(
						resDTO.getPackageRanking());
				this.getManagedBean().setScBidAction(resDTO.getScBidAction());
				// always set scBidAction object before this
				if (resDTO.getScBidAction() != null
						&& resDTO
								.getScBidAction()
								.getActionType()
								.equalsIgnoreCase(
										BiddingConst.BID_ACTION_TYPE_PAUSED)) {
					this.getManagedBean().setRemainingTime(
							remainingTimeAfterPauseCalculator());
				}

				List<ScBidPrice> fetchedBidPrices = fetchCurrentBidprices(resDTO
						.getBidPrices());
				this.getManagedBean().setBidPrices(fetchedBidPrices);
				this.getManagedBean().setScBidSchedule(
						resDTO.getScBidSchedule());

			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					SLALogConstant.VIEW_ONLINE_BIDDING_SEARCH_BACKING_BEAN,
					String.valueOf(bidScheduleID),
					SLALogConstant.GET_ONLINE_BID_DETAILS,
					getUserIdSessionFromLoginId(), e);
		}
	}

	/**
	 * setBidPause Details.
	 * 
	 * @param bidSchedule
	 */
	private void setBidDetailsPause(ScBidSchedule bidSchedule) {
		Long statusId = bidSchedule.getPmStatus().getStatusId();
		if (BidStatusConst.BID_PAUSED.equals(statusId)) {
			this.getManagedBean().setBidPaused(Boolean.TRUE);
		} else {
			this.getManagedBean().setBidPaused(Boolean.FALSE);
		}
	}

	/**
	 * set BidAction Details.
	 * 
	 * @param scBidAction
	 */
	private void setBidAction(ScBidAction scBidAction) {
		if (scBidAction != null) {
			this.getManagedBean().setScBidAction(scBidAction);
			String actionType = scBidAction.getActionType();
			if (BiddingConst.BID_ACTION_TYPE_PAUSED
					.equalsIgnoreCase(actionType)) {
				this.getManagedBean().setRemainingTime(
						remainingTimeAfterPauseCalculator());
			}

		}
	}

	/**
	 * check is tolerance for recommendation by package.
	 */
	private void isTolerancePackageType() {
		if (this.getManagedBean().isToleranceCheck()
				&& this.getManagedBean().getScBidSchedule().getScBid()
						.getRecommendedTypeId().getParameterCode()
						.equals(recommendTypePackage)) {

			BigDecimal dividendLB = this
					.getManagedBean()
					.getScBidSchedule()
					.getPackageEstimateCost()
					.multiply(
							this.getManagedBean().getTolerancePriceLowerBound());
			BigDecimal tolerancePriceLB = dividendLB
					.divide(new BigDecimal(100));
			if (this.getManagedBean().getItemNewBidPriceTotal().doubleValue() < tolerancePriceLB
					.doubleValue()) {
				throw new EpException(MessageConst.ERROR_760369);
			}
			BigDecimal dividendUB = this
					.getManagedBean()
					.getScBidSchedule()
					.getPackageEstimateCost()
					.multiply(
							this.getManagedBean().getTolerancePriceUpperBound());
			BigDecimal tolerancePriceUB = dividendUB
					.divide(new BigDecimal(100));
			if (this.getManagedBean().getItemNewBidPriceTotal().doubleValue() > tolerancePriceUB
					.doubleValue()) {
				throw new EpException(MessageConst.ERROR_760433);
			}
		}
	}

	/**
	 * fetch current BidPrice and set those Bidprice details to index.
	 * 
	 * @param fetchedBidPrices
	 * @return list of bidPrices.
	 */
	private List<ScBidPrice> fetchCurrentBidprices(
			List<ScBidPrice> fetchedBidPrices) {
		for (int i = 0; i < fetchedBidPrices.size(); i++) {
			for (ScBidPrice bidPrice : this.getManagedBean().getBidPrices()) {
				if (bidPrice.getScBidSupplierItem().getBidSupplierItemId() == fetchedBidPrices
						.get(i).getScBidSupplierItem().getBidSupplierItemId()) {
					fetchedBidPrices.get(i).setNewBidPrice(
							bidPrice.getNewBidPrice());
					fetchedBidPrices.get(i).setNewPrice(bidPrice.getNewPrice());
					break;
				}

			}
		}
		return fetchedBidPrices;
	}

	/**
	 * To get RuleSO
	 * 
	 * @return IRuleSO
	 */
	private IRuleSO getRuleSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
	}

	/**
	 * @return the isBidPaused
	 */
	public boolean isBidPaused() {
		return isBidPaused;
	}

	/**
	 * @param isBidPaused
	 *            the isBidPaused to set
	 */
	public void setBidPaused(boolean isBidPaused) {
		this.isBidPaused = isBidPaused;
	}

	/**
	 * @return the recommendTypeItem
	 */
	public String getRecommendTypeItem() {
		return recommendTypeItem;
	}

	/**
	 * @param recommendTypeItem
	 *            the recommendTypeItem to set
	 */
	public void setRecommendTypeItem(String recommendTypeItem) {
		this.recommendTypeItem = recommendTypeItem;
	}

	/**
	 * @return the recommendTypePackage
	 */
	public String getRecommendTypePackage() {
		return recommendTypePackage;
	}

	/**
	 * @param recommendTypePackage
	 *            the recommendTypePackage to set
	 */
	public void setRecommendTypePackage(String recommendTypePackage) {
		this.recommendTypePackage = recommendTypePackage;
	}

	/**
	 * @return the servicetype
	 */
	public Long getServicetype() {
		return servicetype;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(Long interval) {
		this.interval = interval;
	}

}
