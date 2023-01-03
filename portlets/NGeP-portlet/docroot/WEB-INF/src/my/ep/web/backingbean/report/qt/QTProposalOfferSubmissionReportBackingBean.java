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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.util.SLAUtils;
import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.managedbean.report.qt.QTProposalOfferSubmissionReportManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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

//	private static final Logger log = EpLogger.getLogger(ScPropValidPeriodExtReportBackingBean.class);

	@PostConstruct
	public void init() {
		log.info("=====init executed QTProposalOfferSubmissionReportBackingBean====");
		//this.getManagedBean().setReportCode(QtReportConst.SC_QUOT_TENDER_PROPOSAL_OFFER_SUBM_RPT_BY_SUPPLIER_VAL);
		
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
		resetSearchBB();
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String, String> params = new LinkedHashMap<>();
		ScReportFieldsSearchBean reportFieldBean = this.getManagedBean().getScReportSearchBean();

		//report change date strformat -start-
		String fromDate = new SimpleDateFormat("dd/MM/yyyy").format(reportFieldBean.getScPublishFromDate());
		String toDate = new SimpleDateFormat("dd/MM/yyyy").format(reportFieldBean.getScPublishToDate());
		
		params.put(QtReportConst.PARAM_SUBMIT_FR_DT, fromDate);
		params.put(QtReportConst.PARAM_SUBMIT_TO_DT, toDate);
		//report change date strformat -end-
		
		if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationNumber())) {
			params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		}
		
		log.info("this.getManagedBean().getAutoQtTitle().getQtTitle()================="+this.getManagedBean().getAutoScQt().getQtTitle());
		if (StringUtils.isNotEmpty(this.getManagedBean().getAutoScQt().getQtTitle())) {
			params.put(QtReportConst.QUOTATION_TENDER_TITLE, this.getManagedBean().getAutoScQt().getQtTitle());
		}
		
		
		if (null != reportFieldBean.getPtjProfile()) {
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getPtjProfile().getOrgCode());
		} else if (StringUtils.isNotEmpty(reportFieldBean.getScPtj())) {
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getScPtj());
		}

		if (StringUtils.isNotEmpty(reportFieldBean.getScDeskOfficer())) {
			params.put(QtReportConst.PARAM_DESK_OFFCR_EQ, reportFieldBean.getScDeskOfficer());
		}

		return params;
	}
	
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

		} finally {
			qtDetailSO = null;
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
		} finally {
			qtDetailSO = null;
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
		} finally {
			qtDetailSO = null;
		}

		return qtNoLs;
	}

}
