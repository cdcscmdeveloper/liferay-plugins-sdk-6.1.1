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

package my.ep.web.backingbean.report.qt;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTSearchBeanConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.report.qt.QTProposalOfferSubmissionReportManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sc.helper.ReportHelper;

import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

/**
 * The Class BriefingPaperReportBackingBean.
 * 
 * @author:Harun.
 */

@ManagedBean(name = "qtPropOfferSubReportBB")
@ViewScoped
public  class QTProposalOfferSubmissionReportBackingBean extends
QtReportBackingBean<QTProposalOfferSubmissionReportManagedBean> implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(QTProposalOfferSubmissionReportBackingBean.class);
	
	private String strLanguage = ResourceBundleUtils.getMessage(
			ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
			new FacesScopes().getUserLocale());
	
	/** The search quotation tender. */
	private IPagingDataModel<ScQt> searchQTProposal = new qtProposalDataModel();
	
	
	/**
	 * Instantiates a new evaluation meeting backing bean.
	 */
	public QTProposalOfferSubmissionReportBackingBean() {

		if (this.getManagedBean().getProposalQtnTenderSearchBean() == null) {
			this.getManagedBean().setProposalQtnTenderSearchBean(
					new QtnTenderSearchBean());
		}
//		if (this.getManagedBean().getCommitteMember() == null) {
//			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
//		}

	}

	@PostConstruct
	public void init() {
		log.info("=====init executed QTProposalOfferSubmissionReportBackingBean====");
		
		ScReportFieldsSearchBean reportFileSearchBean = this.getManagedBean().getScReportSearchBean();
		
		if (null == reportFileSearchBean) {
			log.info("=====init  QTProposalOfferSubmissionReportBackingBean null value====");
			reportFileSearchBean = new ScReportFieldsSearchBean();
			reportFileSearchBean = getDefaultValuesFromAndToDate(reportFileSearchBean);
			
			getSupplierName();
			this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		}
	}

	@Override
	public void generateReport(LinkedHashMap<String, String> params) {
		log.info("=====QTProposalOfferSubmissionReportBackingBean generateReport====");
		generateSCReport(params);
	}
	
	@Override
	public void resetSearch() {
		this.getManagedBean().setQuotationNumber(null);
		this.getManagedBean().setQuotationId(null);
		this.getManagedBean().getAutoScQt().setQtNo(null);
		this.getManagedBean().getAutoScQt().setQtTitle(null);
		this.getManagedBean().getAutoScQtProposal().setProposalNo(null);
		ScReportFieldsSearchBean reportFileSearchBean=this.getManagedBean().getScReportSearchBean();
		reportFileSearchBean=new ScReportFieldsSearchBean();
		reportFileSearchBean=getDefaultValuesFromAndToDate(reportFileSearchBean);
		this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
	}
	
	//14306 - Start (Harun)
	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String, String> params = new LinkedHashMap<>();
		ScReportFieldsSearchBean reportFieldBean = this.getManagedBean().getScReportSearchBean();
										
																									  
//		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
//				QtReportConst.URL_RPT_DATE_ANOTHER_FORMAT);
		
		if (StringUtils.isNotEmpty(reportFieldBean.getScPropSubmitToMonth())
				&& StringUtils.isNotEmpty(reportFieldBean
						.getScPropSubmitFromMonth())) {
			StringBuilder propSubmitFromDate=new StringBuilder();
			propSubmitFromDate.append(reportFieldBean
						.getScPropSubmitFromMonth()).append(reportFieldBean.getScPropSubmitFromYear());
			params.put(QtReportConst.PARAM_SUBMIT_FR_DT, propSubmitFromDate.toString());
			log.info("proposalSubmitFromDate"+propSubmitFromDate.toString());
			StringBuilder propSubmitToDate=new StringBuilder();
			propSubmitToDate.append(reportFieldBean
						.getScPropSubmitToMonth()).append(reportFieldBean.getScPropSubmitToYear());
			params.put(QtReportConst.PARAM_SUBMIT_TO_DT, propSubmitToDate.toString());
			log.info("proposalSubmitToDate"+propSubmitToDate.toString());
			
		} else {
			params.put(QtReportConst.PARAM_SUBMIT_FR_DT, reportFieldBean.getScPropSubmitFromYear());
			params.put(QtReportConst.PARAM_SUBMIT_TO_DT, reportFieldBean.getScPropSubmitToYear());
			
		}
		
		if (null != this.getManagedBean().getSupplierId()) {
			params.put(QtReportConst.PARAM_SUPPLIER_ID, String.valueOf(this.getManagedBean().getSupplierId()));
		}
		
		if (StringUtils.isNotEmpty(this.getManagedBean().getAutoScQt().getQtNo())) {
			params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getAutoScQt().getQtNo());
		}
		
		if (StringUtils.isNotEmpty(this.getManagedBean().getAutoScQt().getQtTitle())) {
			params.put(QtReportConst.QUOTATION_TENDER_TITLE, this.getManagedBean().getAutoScQt().getQtTitle());
		}
												
																								
		if (StringUtils.isNotEmpty(this.getManagedBean().getAutoScQtProposal().getProposalNo())) {
			params.put(QtReportConst.PARAM_PROPOSAL_NO, this.getManagedBean().getAutoScQtProposal().getProposalNo());
		}

		//14306 - Start (Harun) Change new method for STATUS parameter
		if (CollectionUtils.isNotEmpty(reportFieldBean.getScStatusManyMenu())) {
			List<String> conversionList = new ArrayList<>();

			for (String strManyMenu : reportFieldBean.getScStatusManyMenu()) {
				conversionList.add(getConversionParam(strManyMenu));
			}
			String status = StringUtils.join(conversionList, ",");
			params.put(QtReportConst.PARAM_PROCURE_STATUS_EQ, status.replaceAll(",$", ""));
		}
		//14306 - End (Harun)

		return params;
	}
	//14306 - End (Harun)
	
	//14306 - Start (Harun) get SupplierName
	public void getSupplierName() {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		
		UserReqDTO reqDTO = new UserReqDTO();
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		if (quationTenderReqDTO.getPmUser().getUserId() != null) {
			reqDTO.setUserId(quationTenderReqDTO.getPmUser().getUserId());
			UserResDTO resDTO = userSO.retrieveUserProfileByUserId(reqDTO);
			
			if(resDTO.getPmUser() != null){
				for (PmUserOrg pmUserOrg : resDTO.getPmUser().getPmUserOrgs()){
					this.getManagedBean().setSupplierName(pmUserOrg.getPmOrgProfile().getSmSupplier().getCompanyName());
					this.getManagedBean().setLatestApplId(pmUserOrg.getPmOrgProfile().getSmSupplier().getSmAppl().getApplId());
					this.getManagedBean().setSupplierId(pmUserOrg.getPmOrgProfile().getSmSupplier().getSupplierId());
					
				}
			}
		}
	}
	//14306 - End (Harun)
	
	/**
	 * To Retrieve autocomplete or wildsearch for QT Title
	 * 
	 * @param reqDTO.setQtTitle
	 *        reqDTO.setLatestApplId 
	 *
	 * @return List<String>
	 */
	public List<String> autoCompleteQtTitle(String query) {

		log.info("autoCompleteQt query:::::::::::::::::::::::::::::: " + query);

		IQTDetailSO qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

		List<String> qtTitleLs = new ArrayList<>();

		try {

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtTitle(query);
			reqDTO.setLatestApplId(this.getManagedBean().getLatestApplId());

			QuationTenderResDTO resDTO = qtDetailSO.searchQtTitleByAuto(reqDTO);

			if (resDTO.getQtAutoLs() != null) {
				for (ScQt scQt : resDTO.getQtAutoLs()) {
					qtTitleLs.add(scQt.getQtTitle());
				}
			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("QT", "RetrieveWildsearchForQtTitle", "qtTitle",
					"autoCompleteQtTitle", getUserIdSessionFromLoginId(), e);

		}

		return qtTitleLs;
	}
	
	/**
	 * To Retrieve autocomplete or wildsearch for Proposal No
	 * 
	 * @param reqDTO.setProposalNo
	 *        reqDTO.setLatestApplId 
	 *
	 * @return List<String>
	 */
	public List<String> autoCompleteProposalNo(String query) {

		log.info("autoCompleteProposalNo query:::::::::::::::::::::::::::::: " + query);
		log.info("autoCompleteProposalNo LatestApplId :::::::::::::::::::::::::::::: "
				+ this.getManagedBean().getLatestApplId());

		IQTDetailSO qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

		List<String> proposalNoLs = new ArrayList<>();

		try {

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setProposalNo(query);
			reqDTO.setLatestApplId(this.getManagedBean().getLatestApplId());

			QuationTenderResDTO resDTO = qtDetailSO.searchQtProposalNoByAuto(reqDTO);

			if (resDTO.getQtProposalAutoLs() != null) {
				for (ScQtProposal scQtProp : resDTO.getQtProposalAutoLs()) {
					proposalNoLs.add(scQtProp.getProposalNo());
				}
			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("QT", "RetrieveWildsearchForProposalNo", "proposalNo",
					"autoCompleteProposalNo", getUserIdSessionFromLoginId(), e);
		}

		return proposalNoLs;
	}
	
	/**
	 * To Retrieve autocomplete or wildsearch for QT No
	 * 
	 * @param reqDTO.setQtNo
	 *        reqDTO.setLatestApplId 
	 *
	 * @return List<String>
	 */
	public List<String> autoCompleteQtNo(String query) {

		log.info("autoCompleteQt query:::::::::::::::::::::::::::::: " + query);

		IQTDetailSO qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

		List<String> qtNoLs = new ArrayList<>();

		try {

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtNo(query);
			reqDTO.setLatestApplId(this.getManagedBean().getLatestApplId());

			QuationTenderResDTO resDTO = qtDetailSO.searchQtNoByAuto(reqDTO);

			if (resDTO.getQtAutoLs() != null) {
				for (ScQt scQt : resDTO.getQtAutoLs()) {
					qtNoLs.add(scQt.getQtNo());
				}
			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("QT", "RetrieveWildsearchForQtNo", "qtNo", "autoCompleteQtNo",
					getUserIdSessionFromLoginId(), e);
		}

		return qtNoLs;
	}
	
	//14306 - Start (Harun) 
	/**
	 * To Retrieve conversion Parameter for Report Status
	 * 
	 * @param status ScStatus Multiple Menu
	 *
	 * @return HashMap<String>
	 */
	
	public String getConversionParam(String status) {
		HashMap<String, String> statusmap = new HashMap<>();
		statusmap.put(getStatusDesc(QtReportConst.LABEL_PENDING_PROPOSAL_SUBMISSION), QtReportConst.STATUS_PENDING_PROPOSAL_SUBMISSION);
		statusmap.put(getStatusDesc(QtReportConst.LABEL_PROPOSAL_SUBMITTED), QtReportConst.STATUS_PROPOSAL_SUBMITTED);
		
		return statusmap.get(status);
	}
	
//	/**
//	 * To Retrieve Status Description conversion
//	 * 
//	 * @param label Status Desc
//	 *
//	 * @return ResourceBundleUtils<String>
//	 */
//	@Override
//	public String getStatusDesc(String label) {
//		return ResourceBundleUtils.getMessage(label,
//				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
//				new FacesScopes().getUserLocale());
//	}
	//14306 - End (Harun)
	
	/**
	 * Clone search committee action.Event handle to search clone committee
	 * members.
	 */
	public void searchProposalListAction() {
		log.info("harun cek searchProposalListAction 11111========");
		try {
			// searchQuotatioTender = new clonePagingDataModel();
			log.info("harun cek searchProposalListAction 22222========");
			searchQTProposal.reloadData();

		} finally {

		}
	}
	
	/**
	 * Gets the search quotatio tender/Proposal
	 * 
	 * @return the search quotatio tender/Proposal
	 */
	public IPagingDataModel<ScQt> getSearchQTProposal() {
		return searchQTProposal;
	}

	/**
	 * Sets the search quotatio tender.
	 * 
	 * @param searchQuotatioTender
	 *            the new search quotatio tender
	 */
	public void setSearchQTProposal(
			IPagingDataModel<ScQt> searchQTProposal) {
		this.searchQTProposal = searchQTProposal;
	}
	
	/**
	 * The Class clonePagingDataModel.to retrieve all the existing
	 * quotation/Tenders in clone popup.
	 */
	private class qtProposalDataModel extends PagingDataModel<ScQt> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new clone paging data model.
		 */
		public qtProposalDataModel() {

			super("qtProposalListid", false);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@SuppressWarnings("deprecation")
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			log.info("harun cek pageChangeListener 1111 4444========");
			IQTDetailSO detailSO;
			try {
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				List<Long> supplierIdList = new ArrayList<Long>();
				reqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL);

				//String qtTitle = getManagedBean().getProposalQtnTenderSearchBean()
				//		.getQtTitle();
				//if (getManagedBean().getProposalQtnTenderSearchBean() != null || !StringUtils.isEmpty(getManagedBean().getProposalQtnTenderSearchBean().getQtTitle())) {
				//if (getManagedBean().getAutoScQt().getQtTitle() != null){
				if (getManagedBean().getProposalQtnTenderSearchBean().getQtTitle() != null){
//					searchBean.getDataFilterBeans().add(
//							new SearchFilterBean(QTSearchBeanConst.SEARCH_QTTITLE,
//							//new SearchFilterBean(QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SCQT_QTTITLE,
//									SearchOperator.OPERATOR_LIKE.value(),
//									getManagedBean().getProposalQtnTenderSearchBean().getQtTitle(), true));
					reqDTO.setQtTitle(getManagedBean().getProposalQtnTenderSearchBean().getQtTitle());
				}

				//String proposalNo = getManagedBean().getProposalQtnTenderSearchBean()
				//		.getProposalNo();
				//if (getManagedBean().getProposalQtnTenderSearchBean() != null || !StringUtils.isEmpty(getManagedBean().getProposalQtnTenderSearchBean().getProposalNo())) {
				//if (getManagedBean().getAutoScQtProposal().getProposalNo() != null){
				if (getManagedBean().getProposalQtnTenderSearchBean().getProposalNo() != null){
//					searchBean.getDataFilterBeans().add(
//							new SearchFilterBean(QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SCQTPROPOSALS_PROPOSALNO,
//							//new SearchFilterBean(QTSearchBeanConst.SEARCH_PROPOSALNO,
//									SearchOperator.OPERATOR_EQUALS.value(),
//									getManagedBean().getProposalQtnTenderSearchBean().getProposalNo(), true));
					reqDTO.setDocNo(getManagedBean().getProposalQtnTenderSearchBean().getProposalNo());
					
				}
				
				//String qtNumber = getManagedBean()
				//		.getProposalQtnTenderSearchBean().getQtNo();
				/*if (null == qtNumber || "".equalsIgnoreCase(qtNumber)) {
					qtNumber = "QT";
				}*/
				//if (getManagedBean().getProposalQtnTenderSearchBean() != null || !StringUtils.isEmpty(getManagedBean().getProposalQtnTenderSearchBean().getQtNo())) {
				//if (getManagedBean().getAutoScQt().getQtNo() != null){
				if (getManagedBean().getProposalQtnTenderSearchBean().getQtNo() != null){
//					searchBean.getDataFilterBeans().add(
//							new SearchFilterBean(QTSearchBeanConst.SEARCH_QTNO,
//							//new SearchFilterBean(QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SCQT_QTNO, 
//									SearchOperator.OPERATOR_LIKE.value(),
//									getManagedBean().getProposalQtnTenderSearchBean().getQtNo(), true));
					//reqDTO.setDocNo(getManagedBean().getProposalQtnTenderSearchBean().getProposalNo());
				}

				//String status = getManagedBean().getProposalQtnTenderSearchBean()
				//		.getQtStatus();
				
				
//				if (CollectionUtils.isNotEmpty(getManagedBean().getScReportSearchBean().getScStatusManyMenu())) {
//					for (String statusManyMenu : getManagedBean().getScReportSearchBean().getScStatusManyMenu()) {
				if (CollectionUtils.isNotEmpty(getManagedBean().getProposalQtnTenderSearchBean().getScStatusManyMenu())) {
					for (String statusManyMenu : getManagedBean().getProposalQtnTenderSearchBean().getScStatusManyMenu()) {
						log.info("harun cek statusManyMenu 1111======================"+statusManyMenu);
						String conversion = getConversionParam(statusManyMenu);
						log.info("harun cek conversion ============="+conversion);
						
						int fromMonth = NumberUtils.stringToInt(getManagedBean().getScReportSearchBean()
								.getScCreatedFromMonth());
						int toMonth = NumberUtils.stringToInt(getManagedBean().getScReportSearchBean()
								.getScCreatedToMonth());
						int fromYear = NumberUtils.stringToInt(getManagedBean().getScReportSearchBean()
								.getScCreatedFromYear());
						int toYear = NumberUtils.stringToInt(getManagedBean().getScReportSearchBean()
								.getScCreatedToYear());
						
						log.info("harun cek fromMonth 1111==================="+fromMonth);
						log.info("harun cek toMonth 1111==================="+toMonth);
						log.info("harun cek fromYear 111==================="+fromYear);
						log.info("harun cek toYear 1111==================="+toYear);
						
						log.info("harun cek statusManyMenu 22222======================"+statusManyMenu);
						Date fromSubmittedDate = null;
						Date toSubmittedDate = null;

						Calendar c1 = Calendar.getInstance();
						// c1.setTime(fromClosingDate);
						c1.clear();
						c1.set(Calendar.MONTH, mapToCalendarMonth(fromMonth));
						c1.set(Calendar.YEAR, fromYear);
						c1.setTime(c1.getTime());
						Calendar fromDate = Calendar.getInstance();
						fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH),
								0, 0, 0);
						fromDate.set(Calendar.MILLISECOND, 0);
						fromSubmittedDate = fromDate.getTime();

						Calendar c2 = Calendar.getInstance();
						// c2.setTime(toClosingDate);
						c2.clear();
						c2.set(Calendar.MONTH, mapToCalendarMonth(toMonth));
						c2.set(Calendar.YEAR, toYear);
						c2.setTime(c2.getTime());
						Calendar toDate = Calendar.getInstance();
						toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.getActualMaximum(Calendar.DAY_OF_MONTH),
								23, 59, 59);
						toDate.set(Calendar.MILLISECOND, 0);
						toSubmittedDate = toDate.getTime();

						log.info("harun cek fromSubmittedDate===========" + fromSubmittedDate);
						log.info("harun cek toSubmittedDate===========" + toSubmittedDate);
						
						if (conversion.equalsIgnoreCase(QtReportConst.STATUS_PENDING_PROPOSAL_SUBMISSION)) {
							SearchFilterBean bean = new SearchFilterBean();
							bean.setSearchField(QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SCQTPROPOSALS_CREATEDDATE);
							bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
							SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
									fromSubmittedDate, toSubmittedDate);
							bean.setSearchValue(betweenVal);
							searchBean.getDataFilterBeans().add(bean);
							
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean(
											QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SCQTPROPOSALS_ISSUBMITTED,
											SearchOperator.OPERATOR_EQUALS.value(),
											QtReportConst.STATUS_PROPOSAL_SUBMITTED_ISSUBMITTED_NO, true));
							
						}else{
							SearchFilterBean bean = new SearchFilterBean();
							bean.setSearchField(QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SCQTPROPOSALS_CHANGEDDATE);
							bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
							SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
									fromSubmittedDate, toSubmittedDate);
							bean.setSearchValue(betweenVal);
							searchBean.getDataFilterBeans().add(bean);
							
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean(
											QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SCQTPROPOSALS_ISSUBMITTED,
											SearchOperator.OPERATOR_EQUALS.value(),
											QtReportConst.STATUS_PROPOSAL_SUBMITTED_ISSUBMITTED_YES, true));
						}
					}
					
//					String procurementMode = getManagedBean()
//							.getCloneQtnTenderSearchBean().getProcurementMode();
//					if (!StringUtils.isBlank(procurementMode)) {
//						searchBean.getDataFilterBeans().add(
//								new SearchFilterBean(
//										"procurementMode.parameterCode",
//										SearchOperator.OPERATOR_EQUALS.value(),
//										procurementMode, true));
//
//					} else {
//						// Filter By - procurementMode
//						List<String> procurementModes = new ArrayList<String>();
//						procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
//						procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
//						searchBean.getDataFilterBeans().add(
//								new SearchFilterBean(
//										"procurementMode.parameterCode",
//										SearchOperator.OPERATOR_IN.value(), 
//										procurementModes));
//					}
						
//						searchBean.getDataFilterBeans().add(
//								new SearchFilterBean(
//										"scQtStatus.pmStatus.statusId",
//										SearchOperator.OPERATOR_EQUALS.value(),
//										getManagedBean().getProposalQtnTenderSearchBean().getQtStatus()));
//						searchBean.getDataFilterBeans().add(
//								new SearchFilterBean("scQtStatus.current",
//										SearchOperator.OPERATOR_EQUALS.value(),
//										true));

				}
				
				if (getManagedBean().getSupplierId() != null){
//					searchBean.getDataFilterBeans().add(
//							new SearchFilterBean(QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SMSUPPLIER_SUPPLIERID,
//									SearchOperator.OPERATOR_EQUALS.value(),
//									String.valueOf(getManagedBean().getSupplierId()), true));
//					if(reqDTO.getSupplierIds() != null){
//						log.info("harun cek reqDTO.getSupplierIds() != null===========");
//						//reqDTO.setSupplierId(getManagedBean().getSupplierId());
//					}
					supplierIdList.add(getManagedBean().getSupplierId());
					reqDTO.setSupplierIds(supplierIdList);
					log.info("harun cek reqDTO.getSupplierIds().size()======="+reqDTO.getSupplierIds().size());
					
				}
				
//				if (getManagedBean().getAutoScQt().getQtNo() != null){
//					searchBean.getDataFilterBeans().add(
//							//new SearchFilterBean(QTSearchBeanConst.SEARCH_QTNO,
//							new SearchFilterBean(QTSearchBeanConst.SEARCH_SCQTSUPPLIERS_SCQT_QTNO, 
//									SearchOperator.OPERATOR_LIKE.value(),
//									getManagedBean().getAutoScQt().getQtNo(), true));
//				}
				
				reqDTO.setQtSearchBean(searchBean);
				//reqDTO.setQtProposalSearchBean(searchBean);

				QuationTenderResDTO dto = null;
			

				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				dto = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);

				if (null != dto && dto.isSuccessful()) {
					//log.info("harun cek dto.getQtProposalResultBean().getTotalRecord()=================="+dto.getQtProposalResultBean().getTotalRecord());
					log.info("harun cek dto.getQtResultBean().getTotalRecord()=================="+dto.getQtResultBean().getTotalRecord());
					//return dto.getQtProposalResultBean();
					return dto.getQtResultBean();
				}
			}catch (Exception e){
				e.printStackTrace();
			} finally {
				detailSO=null;
			}

			return null;
			
		}
	}
	
	/**
	 * On row clone action.Event handle to select the scqt object based on scqt
	 * and committee type to display existing committee members in clone popup.
	 * 
	 * @param event
	 *            (Mandatory)
	 */
	public void onRowProposalAction(SelectEvent event) {
		// debugLogger("onRowCloneAction");
		log.info("harun cek onRowProposalAction================");
		ScQt scQt = (ScQt) event.getObject();
//		this.getManagedBean().setCloneSelectQt(scQt);
//		this.getManagedBean().setCloneCommitteMemebersSelect(
//				new ArrayList<ScCommitteeMember>());
//		CommitteReqDTO reqDTO = new CommitteReqDTO();
//
//		reqDTO.setQtId(scQt.getQtId());
//		reqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());
//		CommitteResDTO resDTO = null;
//		ICommitteSO committeSO;
//		try {
//			 committeSO = BusinessDelegate.getInstance()
//					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
//							ICommitteSO.class);
//			resDTO = committeSO
//					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
//			this.getManagedBean().setCloneCommitteMemebers(
//					new ArrayList<ScCommitteeMember>());
//			if (null != resDTO
//					&& null != resDTO.getCommMemberResultBean()
//					&& null != resDTO.getCommMemberResultBean().getResultList()
//					&& !resDTO.getCommMemberResultBean().getResultList()
//							.isEmpty()) {
//
//				this.getManagedBean().setCloneCommitteMemebers(
//						resDTO.getCommMemberResultBean().getResultList());
//			}
//
//		} finally {
//			committeSO=null;
//		}
	}
	
//    public static String getMonthName(int calendarMonth) {
//        String[] monthNames = new java.text.DateFormatSymbols().getMonths();
//        if (calendarMonth < 0 || calendarMonth > 11) {
//            throw new IllegalArgumentException("Calendar month must be between 0 and 11");
//        }
//        log.info("harun cek monthNames========="+monthNames);
//        
//        return monthNames[calendarMonth];
//    }
	
    public static int mapToCalendarMonth(int userMonth) {
    	// Get the current month (0-based, where January = 0) according to java.util.Calendar
    	if (userMonth < 1 || userMonth > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        return userMonth - 1;
    }
    
	/**
	 * Get Status description by status id.
	 * 
	 * @param statusId
	 *            the status id.
	 * @return status description
	 */
	public String getProposalNoForSupplierReport(Long qtId) {
		String proposalNo = null;
		log.info("harun cek qtId=================="+qtId);
		log.info("harun cek this.getManagedBean().getSupplierId()=================="+this.getManagedBean().getSupplierId());
		
		if (this.getManagedBean().getSupplierId() != null) {
			// Get the status description.
			ScQtProposal scQtProposal = ReportHelper.getInstance()
					.getProposalDetails(qtId, this.getManagedBean().getSupplierId());

			log.info("harun cek proposalNo 222222============"+proposalNo);
			if (scQtProposal != null) {
				proposalNo = scQtProposal.getProposalNo();
			}
		}
		

		return proposalNo;
	}
	
//	/**
//	 * construct the proposal Submitted to year based on ScPropSubmitFromYear(
//	 * 
//	 * @param reportSearchbean getScPropSubmitFromYear
//	 * 
//	 * @return ScPropSubmitToYear 
//	 */
//	//public void propSubmitToDtUpdate(ScReportFieldsSearchBean reportSearchbean) {
//	public void propSubmitToYearUpdate(QtnTenderSearchBean qtTenderSearchbean) {
//		qtTenderSearchbean.setScCreatedToYear(qtTenderSearchbean.getScCreatedFromYear());
//		qtTenderSearchbean.setScPropSubmitFromYear(qtTenderSearchbean.getScCreatedFromYear());
//		qtTenderSearchbean.setScPropSubmitToYear(qtTenderSearchbean.getScCreatedFromYear());
//	}
//
//	/**
//	 * construct the proposal Submitted to month based on ScPropSubmitFromMonth(
//	 * 
//	 * @param reportSearchbean getScPropSubmitFromMonth
//	 * 
//	 * @return ScPropSubmitToMonth
//	 */
//	public void propSubmitToMonthUpdate(QtnTenderSearchBean qtTenderSearchbean) {
//		qtTenderSearchbean.setScCreatedToMonth(qtTenderSearchbean.getScCreatedFromMonth());
//		qtTenderSearchbean.setScPropSubmitFromMonth(qtTenderSearchbean.getScCreatedFromMonth());
//		qtTenderSearchbean.setScPropSubmitToMonth(qtTenderSearchbean.getScCreatedFromMonth());
//	}
	
	public List<String> reportStatusDropdownValuesProposalOfferSubmission() {
		List<String> statusValues = new ArrayList<String>();

		statusValues.add(getStatusDesc("lbl.rpt.pending.proposal.submission"));
		statusValues.add(getStatusDesc("lbl.rpt.proposal.submitted"));
	
		return statusValues;
	}
	
	public boolean isDisabled(String item,String reportCode) {
		List<String> allList = reportStatusDropdownValuesSelectManyMenu(reportCode);
	
		if(this.getManagedBean().getProposalQtnTenderSearchBean().getScStatusManyMenu() != null 
				&& this.getManagedBean().getProposalQtnTenderSearchBean().getScStatusManyMenu().size() > 0){
			for (String stsvalue : this.getManagedBean().getProposalQtnTenderSearchBean().getScStatusManyMenu()) {
				if(stsvalue.equalsIgnoreCase(getStatusDesc("lbl.rpt.all"))){
					for (String strSelectItem : allList) {
						if(strSelectItem.equalsIgnoreCase(item)){
							return strSelectItem.equals(item);
						}
					}
				}
			}
		}else{
			return false;
		}
		return false;
	}
	
	public void onSelectAll(String reportCode) {

		boolean checkAll = false;
		List<String> statusList = new ArrayList<String>(this.getManagedBean().getProposalQtnTenderSearchBean()
				.getScStatusManyMenu());
		List<String> allList = new ArrayList<String>();

		for (String stsvalue : statusList) {
			if (stsvalue.equalsIgnoreCase(getStatusDesc("lbl.rpt.all"))) {
				allList = reportStatusDropdownValuesSelectManyMenu(reportCode);

				List<SelectItem> items = new ArrayList<SelectItem>();
				for (String strSelectItem : allList) {
					items.add(new SelectItem(strSelectItem));
				}
				for (Iterator<SelectItem> it = items.iterator(); it.hasNext();) {
					SelectItem si = (SelectItem) it.next();
					//this.getManagedBean().getScReportSearchBean().getScStatusManyMenu().add((String) si.getValue());
					this.getManagedBean().getProposalQtnTenderSearchBean().getScStatusManyMenu().remove((String) si.getValue());
					si.setDisabled(Boolean.TRUE);
				}
				checkAll = true;
			} else {
				checkAll = false;
			}
		}
		if (!checkAll) {
			if (allList.size() == statusList.size()) {
				List<SelectItem> items = new ArrayList<SelectItem>();
				for (String strSelectItem : statusList) {
					items.add(new SelectItem(strSelectItem));
				}
				for (Iterator<SelectItem> it = items.iterator(); it.hasNext();) {
					SelectItem si = (SelectItem) it.next();
					//this.getManagedBean().getScReportSearchBean().getScStatusManyMenu().remove((String) si.getValue());
					si.setDisabled(Boolean.FALSE);
				}
			}
		}

	}
	
//	/**
//	 * Generate month list.
//	 * 
//	 * @param language
//	 *            the language
//	 * @return the list
//	 */
//	public List<SelectItem> generateMonthList() {
//		List<SelectItem> monthList = new ArrayList<SelectItem>();
//		if ((strLanguage != null) && (strLanguage.equalsIgnoreCase("en"))) {
//
//			String month = ReportConst.EMPTY_VAL;
//			int monthNoInt;
//			String monthNoStr = ReportConst.EMPTY_VAL;
//			DateFormatSymbols dfs = new DateFormatSymbols();
//			String[] months = dfs.getMonths();
//			for (int i = 0; i <= 11; i++) {
//				monthNoInt = i + 1;
//				if (monthNoInt < 10) {
//					monthNoStr = ReportConst.ZERO_VAL + monthNoInt;
//				} else {
//					monthNoStr = monthNoInt + ReportConst.EMPTY_VAL;
//				}
//				month = months[i];
//				SelectItem selectItem = new SelectItem();
//				selectItem.setLabel(month);
//				selectItem.setValue(monthNoStr);
//				monthList.add(selectItem);
//			}
//		} else if ((strLanguage != null)
//				&& (strLanguage.equalsIgnoreCase("ms"))) {
//			SelectItem selectItem = new SelectItem();
//			selectItem.setLabel("Januari");
//			selectItem.setValue("01");
//			monthList.add(selectItem);
//			SelectItem selectItem1 = new SelectItem();
//			selectItem1.setLabel("Februari");
//			selectItem1.setValue("02");
//			monthList.add(selectItem1);
//			SelectItem selectItem2 = new SelectItem();
//			selectItem2.setLabel("Mac");
//			selectItem2.setValue("03");
//			monthList.add(selectItem2);
//			SelectItem selectItem3 = new SelectItem();
//			selectItem3.setLabel("April");
//			selectItem3.setValue("04");
//			monthList.add(selectItem3);
//			SelectItem selectItem4 = new SelectItem();
//			selectItem4.setLabel("Mei");
//			selectItem4.setValue("05");
//			monthList.add(selectItem4);
//			SelectItem selectItem5 = new SelectItem();
//			selectItem5.setLabel("Jun");
//			selectItem5.setValue("06");
//			monthList.add(selectItem5);
//			SelectItem selectItem6 = new SelectItem();
//			selectItem6.setLabel("Julai");
//			selectItem6.setValue("07");
//			monthList.add(selectItem6);
//			SelectItem selectItem7 = new SelectItem();
//			selectItem7.setLabel("Ogos");
//			selectItem7.setValue("08");
//			monthList.add(selectItem7);
//			SelectItem selectItem8 = new SelectItem();
//			selectItem8.setLabel("September");
//			selectItem8.setValue("09");
//			monthList.add(selectItem8);
//			SelectItem selectItem9 = new SelectItem();
//			selectItem9.setLabel("Oktober");
//			selectItem9.setValue("10");
//			monthList.add(selectItem9);
//			SelectItem selectItem10 = new SelectItem();
//			selectItem10.setLabel("November");
//			selectItem10.setValue("11");
//			monthList.add(selectItem10);
//			SelectItem selectItem11 = new SelectItem();
//			selectItem11.setLabel("Disember");
//			selectItem11.setValue("12");
//			monthList.add(selectItem11);
//
//		}
//		return monthList;
//	}
//
//	
//	/**
//	 * Generate year list.
//	 * 
//	 * @return the list
//	 */
//	public List<SelectItem> generateYearList() {
//
//		int year = Calendar.getInstance().get(Calendar.YEAR);
//
//		List<SelectItem> yearList = new ArrayList<SelectItem>();
//
//		for (int i = year - 7; i <= year; i++) {
//
//			yearList.add(new SelectItem(i, String.valueOf(i)));
//
//		}
//		return yearList;
//	}

	
	/**
	 * Get Status description by status id.
	 * 
	 * @param statusId
	 *            the status id.
	 * @return status description
	 */
	public String getStatusForReportBySupplier(Long qtId) {
		String statusDesc = null;
		log.info("harun cek qtId=================="+qtId);
		log.info("harun cek this.getManagedBean().getSupplierId()=================="+this.getManagedBean().getSupplierId());
		
		if (this.getManagedBean().getSupplierId() != null) {
			// Get the status description.
			ScQtProposal scQtProposal = ReportHelper.getInstance()
					.getProposalDetails(qtId, this.getManagedBean().getSupplierId());

			log.info("harun cek scQtProposal.isSubmitted() 222222============"+scQtProposal.isSubmitted());
			if (scQtProposal.isSubmitted() == true) {
				statusDesc = getStatusDesc(QtReportConst.LABEL_PROPOSAL_SUBMITTED);
			} else {
				statusDesc = getStatusDesc(QtReportConst.LABEL_PENDING_PROPOSAL_SUBMISSION);
			}
		}
		

		return statusDesc;
	}
	
	
	
}
