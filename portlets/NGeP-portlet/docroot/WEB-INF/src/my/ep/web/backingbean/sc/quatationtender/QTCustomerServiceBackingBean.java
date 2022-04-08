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
package my.ep.web.backingbean.sc.quatationtender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTCustomerServiceSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.sc.quationtender.QTCustomerServiceReqDTO;
import my.ep.dto.sc.quationtender.QTCustomerServiceResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QTCustomerServiceSearchBean;
import my.ep.web.bean.sc.quatationtender.SourcingCustomerServiceBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTCustomerServiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class QTCustomerServiceBackingBean.
 * 
 * @author Garikapati Mangaiah.
 */

@ManagedBean(name = "qtCustomerServiceBB")
@ViewScoped
public class QTCustomerServiceBackingBean extends
		SourcingBaseBackingBean<QTCustomerServiceManagedBean> implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(QTCustomerServiceBackingBean.class);

	// private IPagingDataModel<ScQt> qtCustomerServiceDataModel = new
	// QTCustomerServiceSearchQtDataModel();
	private static final String NAV_QT_CUSTOMER_SERVICE_VIEW = "/xhtml/sc/qtcustomerservice/qtCustomerServiceViewMain.xhtml";
	private static final String NAV_QT_CUSTOMER_SERVICE_SEARCH_VIEW = "/xhtml/sc/qtcustomerservice/qtcustomerServiceSearchview.xhtml";

	public QTCustomerServiceBackingBean() {

	}

	/**
	 * set user orgProfile data.
	 * 
	 */
	public void setorgProfileData() {
		try {
			QTCustomerServiceSearchBean qtCustomerService = getManagedBean()
					.getQtCustomerServiceSearchBean();
			PmOrgProfile orgProfileCode = qtCustomerService
					.getOrgProfilePtjCode();
			this.getManagedBean().getQtCustomerServiceSearchBean()
					.setOrgProfilePtjname(orgProfileCode);
		} finally {

		}

	}

	/**
	 * Method to Reset the QTCustomer Service data.
	 */
	public void qtCustomerServicereset() {
		try {
			getManagedBean().getQtCustomerServiceSearchBean()
					.setOrgProfilePtjCode(new PmOrgProfile());
			getManagedBean().getQtCustomerServiceSearchBean()
					.setOrgProfilePtjname(new PmOrgProfile());
			getManagedBean().getQtCustomerServiceSearchBean().setQtNo(" ");
			getManagedBean().getQtCustomerServiceSearchBean().setQtTitle(" ");
			getManagedBean().getQtCustomerServiceSearchBean().setQtStatus(" ");
			getManagedBean().getQtCustomerServiceSearchBean()
					.setCreatedFromDate(null);
			getManagedBean().getQtCustomerServiceSearchBean().setCreatedToDate(
					null);
			getManagedBean().getQtCustomerServiceSearchBean()
					.setPublishFromDate(null);
			getManagedBean().getQtCustomerServiceSearchBean().setPublishToDate(
					null);
			getManagedBean().getQtCustomerServiceSearchBean().setCloseFromDate(
					null);
			getManagedBean().getQtCustomerServiceSearchBean().setCloseToDate(
					null);
			this.getManagedBean().getQtCustomerServiceDataModel().resetData();
		} finally {

		}
	}

	/**
	 * Method to search the customerService data based on serach fields.
	 */
	public void searchQtCustomerService() {
		QTCustomerServiceSearchBean qtCustomerSearchBean = getManagedBean()
				.getQtCustomerServiceSearchBean();
		PmOrgProfile orgProfileCode = qtCustomerSearchBean
				.getOrgProfilePtjCode();
		if (null == orgProfileCode
				|| StringUtils.isBlank(orgProfileCode.getOrgCode())
				|| StringUtils.isEmpty(orgProfileCode.getOrgCode())) {
			throw new EpException(MessageConst.ERROR_760443);
		}
		this.getManagedBean().getQtCustomerServiceDataModel().reloadData();
	}

	/**
	 * View doc.
	 * 
	 * @param ScQt
	 *            the qt obj
	 */
	public void viewDoc(ScQt scQt, String url, Long statusId) {
		List<String> docNumberList = new ArrayList<String>();
		List<String> userIds = new ArrayList<String>();

		try {
			QTCustomerServiceSearchBean customerService = this.getManagedBean()
					.getQtCustomerServiceSearchBean();
			IPagingDataModel<ScQt> customerServiceModel = this.getManagedBean()
					.getQtCustomerServiceDataModel();
			renewSesionObject();
			this.getManagedBean().setQtCustomerServiceSearchBean(
					customerService);
			this.getManagedBean().setQtCustomerServiceDataModel(
					customerServiceModel);
			this.getManagedBean().setOfficerName(" ");
			log.debug("=status Id===" + statusId);
			if (isQTStatusGreaterThanOrEquals(statusId,
					QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)) {
				log.debug("=status greater===" + statusId);
				retriveCustomerServiceBriefingSiteVisitdtls(scQt.getQtId());
				if (isQTStatusGreaterThan(statusId,
						QTStatusConst.QT_PENDING_PUBLICATION)) {
					this.getManagedBean().setTotalProposalSubmitCount(
							totalproposaSubmitteed(scQt.getQtId()));
				}
				if (isQTStatusGreaterThanOrEquals(statusId,
						QTStatusConst.QT_SUPPLIER_FINALIZATION)) {
					List<ScBidSchedule> bidScheduleList = retriveCustomerServiceBidSchedules(scQt
							.getQtId());
					if (CollectionUtils.isNotEmpty(bidScheduleList)) {
						this.getManagedBean().setBidScheduleList(
								bidScheduleList);
					}
				}
				if (isQTStatusGreaterThanOrEquals(statusId,
						QTStatusConst.QT_PENDING_LOI)
						|| isQTStatusGreaterThanOrEquals(statusId,
								QTStatusConst.QT_PENDING_LOA)) {
					docNumberList = retriveListOfLoiLoaDocNumbers(scQt
							.getQtId());
				}
			}

			String ministryName = retrieveMinistryByOrgProfileId(this
					.getManagedBean().getQtCustomerServiceSearchBean()
					.getOrgProfilePtjname().getOrgProfileId());
			String kampulanPTJ = retrieveJabatanByOrgProfileId(this
					.getManagedBean().getQtCustomerServiceSearchBean()
					.getOrgProfilePtjname().getOrgProfileId());
			this.getManagedBean().setMinistryName(ministryName);
			this.getManagedBean().setKampulanPtjName(kampulanPTJ);
			this.getManagedBean().setPtjCode(
					this.getManagedBean().getQtCustomerServiceSearchBean()
							.getOrgProfilePtjCode().getOrgCode());
			this.getManagedBean().setPtjName(
					this.getManagedBean().getQtCustomerServiceSearchBean()
							.getOrgProfilePtjname().getOrgName());
			this.getManagedBean().setViewOriginalUrl(url);
			this.getManagedBean().setCustomerScQt(scQt);

			BpmRequestDTO reqDTO = new BpmRequestDTO();

			String bpmUserLogin = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.BPM_PRINCIPAL);

			Object token = getBpmSO().authenticate(bpmUserLogin);
			reqDTO.setUserId(bpmUserLogin);
			docNumberList.add(scQt.getQtNo());
			reqDTO.setDocNumberList(docNumberList);
			reqDTO.setCtxToken(token);
			reqDTO.setState(BpmConstants.STATE_ASSIGNED);
			BpmResponseDTO resDTO = getBpmSO().getTasksByDocNumberList(reqDTO);
			if (null != resDTO && null != resDTO.getWorklistVO()
					&& null != resDTO.getWorklistVO().getWorklistItem()
					&& resDTO.getWorklistVO().getWorklistItem().size() > 0) {
				getManagedBean().setCustomerServiceEntity(
						new ArrayList<SourcingCustomerServiceBean>());
				for (WorklistItemVO worklistItem : resDTO.getWorklistVO()
						.getWorklistItem()) {

					if (CollectionUtils.isNotEmpty(worklistItem.getAssignees())) {
						// custServiceEntity.setOfficerName(worklistItem.getAssignees().get(0));
						log.debug("assignee" + worklistItem.getAssignees());
						// log.info("assignee1"+worklistItem.getAssignees().get(0));
						for (int i = 0; i < worklistItem.getAssignees().size(); i++) {
							SourcingCustomerServiceBean custServiceEntity = new SourcingCustomerServiceBean();
							IUserSO userSO = (IUserSO) BusinessDelegate
									.getInstance().getServiceObject(
											ModuleCodeConst.PROFILE_MANAGEMENT,
											IUserSO.class);
							UserReqDTO reqUserDTO = new UserReqDTO();
							reqUserDTO.setLoginId(worklistItem.getAssignees()
									.get(i));
							UserResDTO userResDTO = userSO
									.retrieveUserByLoginId(reqUserDTO);
							if (null != userResDTO
									&& null != userResDTO.getPmUser()) {
								log.debug("====user name==="
										+ userResDTO.getPmUser().getUserName());
								custServiceEntity.setOfficerName(userResDTO
										.getPmUser().getUserName());
							}

							reqDTO = new BpmRequestDTO();
							reqDTO.setCtxToken(token);
							reqDTO.setTaskId(worklistItem.getTaskId());
							PmStatusDesc statusDesc = getPmStatusDesc(worklistItem
									.getDocStatus());
							log.debug("display doc numner"
									+ worklistItem.getDocNumber());
							custServiceEntity.setDocumentNum(worklistItem
									.getDocNumber());
							if (null != statusDesc) {
								custServiceEntity.setStatusDesc(statusDesc
										.getStatusName());
								custServiceEntity
										.setDocumentType(getParameterDesc(
												ParameterTypeConst.DOCUMENT_TYPES,
												statusDesc.getPmStatus()
														.getDocType()));
								log.debug("customer status Id"
										+ statusDesc.getPmStatus()
												.getStatusId());
								custServiceEntity.setStatus(statusDesc
										.getPmStatus().getStatusId());
							}
							getManagedBean().getCustomerServiceEntity().add(
									custServiceEntity);
						}
					}
					/*
					 * this.getManagedBean().setOfficerName(userIds.toString());
					 * if
					 * (StringUtils.isNotBlank(this.getManagedBean().getOfficerName
					 * ()) &&
					 * StringUtils.isNotEmpty(this.getManagedBean().getOfficerName
					 * ())){ String replaceString=
					 * this.getManagedBean().getOfficerName().substring(1,
					 * this.getManagedBean
					 * ().getOfficerName().length()-1).trim();
					 * this.getManagedBean().setOfficerName(replaceString); }
					 */
					log.debug("========staus====" + worklistItem.getState());
				}
			}
			if (QTStatusConst.QT_PROPOSAL_PREPARATION.equals(statusId)) {
				if (null == getManagedBean().getCustomerServiceEntity()) {
					getManagedBean().setCustomerServiceEntity(
							new ArrayList<SourcingCustomerServiceBean>());
				}
				SourcingCustomerServiceBean custServiceEntity = new SourcingCustomerServiceBean();
				custServiceEntity.setStatus(statusId);
				getManagedBean().getCustomerServiceEntity().add(
						custServiceEntity);
			}
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, NAV_QT_CUSTOMER_SERVICE_VIEW);
		} finally {
			docNumberList = null;
			userIds = null;
		}
	}

	/**
	 * Sets the pm status desc.
	 * 
	 * @param statusId
	 *            - pm status id passed as String
	 */
	private PmStatusDesc getPmStatusDesc(Integer docStatus) {

		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(new Long(docStatus));
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			return resDTO.getPmStatusDesc();
		}
		return null;
	}

	/**
	 * Gets the user so.
	 * 
	 * @return the user so
	 */
	private IBpmSO getBpmSO() {
		return (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(
				IBpmSO.class);
	}

	private List<String> retriveListOfLoiLoaDocNumbers(Long qtId) {
		log.debug("start retriveListOfLoiLoaDocNumbers");
		IQTCustomerServiceSO customerServiceInfo = null;
		try {
			customerServiceInfo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTCustomerServiceSO.class);
			QTCustomerServiceReqDTO customerServiceReqDTO = new QTCustomerServiceReqDTO();
			QTCustomerServiceResDTO resDto = new QTCustomerServiceResDTO();
			customerServiceReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			customerServiceReqDTO.setQuotationTenderId(qtId);
			resDto = customerServiceInfo
					.retriveCustomerServiceLoiLoaList(customerServiceReqDTO);
			log.debug("end retriveListOfLoiLoaDocNumbers");
			return resDto.getLoiLoaNumbers();
		} finally {
			customerServiceInfo = null;
		}

	}

	private List<ScBidSchedule> retriveCustomerServiceBidSchedules(Long qtId) {
		IBiddingSO BiddingSO = null;
		log.debug("start retriveCustomerServiceBidSchedules");
		try {
			BiddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			ISearchBean<ScBidSchedule> searchBean = new SearchBean<ScBidSchedule>();

			// Filter By - qtId
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("scBid.docId",
							SearchOperator.OPERATOR_EQUALS.value(), qtId));
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("scBid.docType",
							SearchOperator.OPERATOR_EQUALS.value(),
							ParameterConst.DOCTYPE_QUOTATION));
			BiddingReqDTO reqDTo = new BiddingReqDTO();
			BiddingResDTO resDTo = new BiddingResDTO();
			reqDTo.setBidSchSearchBean(searchBean);
			resDTo = BiddingSO.retriveCustomerServiceBidSchedules(reqDTo);
			log.debug("end retriveCustomerServiceBidSchedules");
			return resDTo.getActiveBidSchedules();

		} finally {
			BiddingSO = null;
		}
	}

	private void retriveCustomerServiceBriefingSiteVisitdtls(Long qtId) {
		IQTBriefingSO qTBriefingSO = null;
		try {
			qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
			BriefingResDTO briefingResDTO = new BriefingResDTO();
			ISearchBean<ScQtBsvDtl> brifingsearchBean = new SearchBean<ScQtBsvDtl>();
			// Filter By - qtId
			brifingsearchBean.getDataFilterBeans().add(
					new SearchFilterBean("scQtBsv.scQt.qtId",
							SearchOperator.OPERATOR_EQUALS.value(), qtId));
			brifingsearchBean.getDataFilterBeans().add(
					new SearchFilterBean("bsvType",
							SearchOperator.OPERATOR_EQUALS.value(),
							QTConstants.BSV_TYPE_BRIEFING));
			briefingReqDTO.setCustomerBriefingSearchBean(brifingsearchBean);

			ISearchBean<ScQtBsvDtl> sitevisitsearchBean = new SearchBean<ScQtBsvDtl>();
			// Filter By - qtId
			sitevisitsearchBean.getDataFilterBeans().add(
					new SearchFilterBean("scQtBsv.scQt.qtId",
							SearchOperator.OPERATOR_EQUALS.value(), qtId));
			sitevisitsearchBean.getDataFilterBeans().add(
					new SearchFilterBean("bsvType",
							SearchOperator.OPERATOR_EQUALS.value(),
							QTConstants.BSV_TYPE_SITEVISIT));
			briefingReqDTO.setCustomerSiteVisitSearchBean(sitevisitsearchBean);
			briefingResDTO = qTBriefingSO
					.retriveCustomerServiceBriefingSiteVisitDtls(briefingReqDTO);
			if (CollectionUtils.isNotEmpty(briefingResDTO.getBriefingList())) {
				this.getManagedBean().setBriefingDtlsList(
						briefingResDTO.getBriefingList());
			}
			if (CollectionUtils.isNotEmpty(briefingResDTO.getSiteVisitList())) {
				this.getManagedBean().setSiteVisitDtlsList(
						briefingResDTO.getSiteVisitList());
			}
		} finally {
			qTBriefingSO = null;
		}

	}

	private Long totalproposaSubmitteed(Long qtId) {
		IQTProposalSO qTProposalSO = null;
		log.debug("start totalproposaSubmitteed");
		try {
			qTProposalSO = (IQTProposalSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTProposalSO.class);
			ProposalReqDTO proposalReqDto = new ProposalReqDTO();
			ProposalResDTO proposalResDto = new ProposalResDTO();
			proposalReqDto.setQtId(qtId);
			proposalResDto = qTProposalSO
					.getTotalSubmittedProposal(proposalReqDto);
			log.debug("end totalproposaSubmitteed");
			return proposalResDto.getTotalSubmitted();
		} finally {
			qTProposalSO = null;
		}

	}

	/**
	 * navigate to original page
	 * 
	 * @return navigation to original page.
	 */
	public String navigateToCurrentPage() {
		// QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		return getManagedBean().getViewOriginalUrl();
	}

	/**
	 * The Class QTCustomerServiceSearchQtDataModel.customer service paging data
	 * model.
	 */
	private class QTCustomerServiceSearchQtDataModel extends
			PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new QTCustomerService paging data model.
		 */
		public QTCustomerServiceSearchQtDataModel(String componentId) {

			super(componentId, false);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {

			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				populateQTSearchCriteria(reqDTO, getManagedBean()
						.getQtCustomerServiceSearchBean(), searchBean);
				QuationTenderResDTO resDTO = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);

				// set search result doc type for navigate to diff page based on
				// diff doc type selected.
				// getManagedBean().getQtnTenderSearchBean().setResultDocType(getManagedBean().getQtnTenderSearchBean().getDocType());
				if (resDTO.isSuccessful()) {
					return resDTO.getQtResultBean();
				}
			} finally {
				detailSO = null;
			}
			return null;

		}
	}

	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @param customerServiceSearchBean
	 *            the Customer search criteria bean
	 * @param qtSearchBean
	 *            the qt search filter bean
	 */
	private void populateQTSearchCriteria(QuationTenderReqDTO reqDTO,
			QTCustomerServiceSearchBean customerServiceSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (customerServiceSearchBean != null) {
			if (CollectionUtils.isNotEmpty(qtSearchBean.getDataFilterBeans())) {
				qtSearchBean.getDataFilterBeans().clear();
			}
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = customerServiceSearchBean.getQtTitle();
			if (StringUtils.isNotBlank(qtTitle)
					&& StringUtils.isNotEmpty(qtTitle)) {
				searchFilterBean.add(new SearchFilterBean(
						QTConstants.QT_TITILE, SearchOperator.OPERATOR_LIKE
								.value(), qtTitle, true));
			}

			// docType & docNo
			String docType = customerServiceSearchBean.getDocumentType();
			String docNo = customerServiceSearchBean.getQtNo();
			if (StringUtils.isNotEmpty(docType)) {
				if (ParameterConst.DOCTYPE_QUOTATION.equals(docType)) {
					if (StringUtils.isNotBlank(docNo)
							&& StringUtils.isNotEmpty(docNo)) {
						SearchFilterBean bean = new SearchFilterBean();
						bean.setSearchField(QTConstants.QT_NUMBER);
						bean.setSearchOperator(SearchOperator.OPERATOR_LIKE
								.value());
						bean.setSearchValue(docNo);
						searchFilterBean.add(bean);
					}
				} else {
					reqDTO.setDocType(docType);
					reqDTO.setDocNo(docNo);
				}
			}

			Date fromCreatedDate = customerServiceSearchBean
					.getCreatedFromDate();
			Date toCreatedDate = customerServiceSearchBean.getCreatedToDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
				if (toCreatedDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toCreatedDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toCreatedDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromPublishDate = customerServiceSearchBean
					.getPublishFromDate();
			Date toPublishDate = customerServiceSearchBean.getPublishToDate();
			if (null != fromPublishDate || null != toPublishDate) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromPublishDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromPublishDate = fromDate.getTime();
				if (toPublishDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toPublishDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toPublishDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromClosingDate = customerServiceSearchBean.getCloseFromDate();
			Date toClosingDate = customerServiceSearchBean.getCreatedToDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			String status = customerServiceSearchBean.getQtStatus();
			if (StringUtils.isNotBlank(status)
					&& StringUtils.isNotEmpty(status)) {
				searchFilterBean.add(new SearchFilterBean(
						"scQtStatus.pmStatus.statusId",
						SearchOperator.OPERATOR_EQUALS.value(), status));
				searchFilterBean.add(new SearchFilterBean("scQtStatus.current",
						SearchOperator.OPERATOR_EQUALS.value(), true));
			}

			PmOrgProfile ptjProfiles = customerServiceSearchBean
					.getOrgProfilePtjname();
			if (ptjProfiles != null && ptjProfiles.getOrgProfileId() != null) {
				// Filter By - PTJ
				searchFilterBean
						.add(new SearchFilterBean(
								"pmOrgProfile."
										+ OrgProfileUtil
												.getSearchBeanParentString(OrganizationType.DIVISION
														.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ptjProfiles.getOrgProfileId()));
			}

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);

			reqDTO.setQtSearchBean(qtSearchBean);
		}
	}

	/**
	 * navigate to CustomerService search page.
	 * 
	 * 
	 */
	public void preRenderView() {
		log.debug("QT customer service preRenderView");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null, NAV_QT_CUSTOMER_SERVICE_SEARCH_VIEW);
	}

	/**
	 * @return the qtCustomerServiceDataModel
	 */
	public IPagingDataModel<ScQt> getQtCustomerServiceDataModel() {
		IPagingDataModel<ScQt> customerService = getManagedBean()
				.getQtCustomerServiceDataModel();
		if (null == customerService) {
			customerService = new QTCustomerServiceSearchQtDataModel(
					"qtCustomerServiceSearchResult");
			getManagedBean().setQtCustomerServiceDataModel(customerService);
		}
		return customerService;

	}

	/**
	 * Method to get the list of bidding suppliers count.
	 * 
	 * @param bidScheduleList
	 * 
	 * @return the bidSuppliers Count.
	 */
	public int totalBidSuppliersCount(List<ScBidSchedule> bidScheduleList) {
		int count = 0;
		try {
			if (CollectionUtils.isNotEmpty(bidScheduleList)) {
				for (ScBidSchedule bidSchedule : bidScheduleList) {
					if (CollectionUtils.isNotEmpty(bidSchedule
							.getScBidSuppliers())) {
						count = count + bidSchedule.getScBidSuppliers().size();
					}

				}
			}

		} finally {

		}
		return count;
	}

}
