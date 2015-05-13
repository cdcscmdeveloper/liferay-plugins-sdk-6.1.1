/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.loiloa;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.clause.IClauseSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.clause.ClauseReqDTO;
import my.ep.dto.pm.common.clause.ClauseResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmClause;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScLoiNegoLocation;
import my.ep.persistence.entity.sc.loaloi.ScLoiRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoiReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoiSupportingDoc;
import my.ep.persistence.entity.sc.loaloi.ScLoiSupportingDocAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoiTerm;
import my.ep.persistence.entity.sc.loaloi.ScLoiWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScPbAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.type.sc.LoiLoaDocType;
import my.ep.type.sc.TermType;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.LoiLoaBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loiloa.LOIDetailManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

// TODO: Auto-generated Javadoc
/**
 * The Class LOIDetailBackingBean.
 *
 * @author Viswanath
 * 
 * @author Sri
 */
@ManagedBean(name = "lOIDetailBackingBean")
@RequestScoped
public class LOIDetailBackingBean extends
		SourcingBaseBackingBean<LOIDetailManagedBean> {
	
	/** The log. */
	public static Logger log = EpLogger.getLogger(LOIDetailBackingBean.class);

	/** The Constant LOI_DETAILS_MAIN_MENU. */
	public static final String LOI_DETAILS_MAIN_MENU = "/xhtml/sc/loi/loiDetailsMain.xhtml.xhtml";
	
	/** The Constant LOI_DETAILS_MENU. */
	public static final String LOI_DETAILS_MENU = "/xhtml/sc/loi/loiDetailsMenu.xhtml";
	
	/** The Constant LOI_AWARD_DETAIL_MENU. */
	public static final String LOI_AWARD_DETAIL_MENU = "/xhtml/sc/loi/loiAwardDetailsMenu.xhtml";
	
	/** The Constant LOI_NEGO_DETAIL_MENU. */
	public static final String LOI_NEGO_DETAIL_MENU = "/xhtml/sc/loi/loiNegotiationDetailsMenu.xhtml";
	
	/** The Constant LOI_APPROVAL_MENU. */
	public static final String LOI_APPROVAL_MENU = "/xhtml/sc/loi/loiApprovalMenu.xhtml";

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	/** The text before sign. */
	private String textBeforeSign;
	
	/** The flag. */
	private boolean flag;
	
	/**
	 * Instantiates a new LOI detail backing bean.
	 */
	public LOIDetailBackingBean() {
		log.info("inside LOIDetailBackingBean constructor");
//		populateUserMinistryList();
//		initializeRemarks(new ScLoi());
//		getApproverNames();
	}

	/**
	 * Pre render page navigation to loi list view page based on task id
	 * @return void
	 */
	public void preRenderLoiListView() {
		debugLogger("preRenderLoiListView");

		IStatusSO statusSO;
		try {

			SCAwardSupplierDataType awardDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);

			Long sourceId = Long.valueOf(awardDataType.getSourceId());
			
			
			if (new FacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())){
				awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
				awardDataType.setDeskOfficer(new FacesScopes().getUserSessionBean().getLoginId());
			}
			else {
				awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_SECRETARY);
				awardDataType.setSecretary(new FacesScopes().getUserSessionBean().getLoginId());
			}

			getManagedBean().setSourceId(sourceId);
			getManagedBean().setPayLoadData(awardDataType);

			getManagedBean()
					.setRecommendDtlIds(awardDataType.getGroupkeyList());
 
			Long currentStatusId = getManagedBean().getCurrentStatusId();

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(currentStatusId);
			StatusResDTO resDTO = statusSO.retrieveStatusByStatusId(reqDTO);
			getManagedBean().setDocType(resDTO.getPmStatus().getDocType());

			// Long sourceId = 1450L;
			retrieveLOIList(sourceId);


			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.LOI_LIST_PAGE);
		} catch (Exception e) {
			handleException(e);
		} finally {
			statusSO = null;
		}
	}

	/**
	 * Pre render page navigation to loi page based on task id
	 * @return void
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		IStatusSO statusSO;
		try {
			SCAwardSupplierDataType awardDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);

			Long sourceId = Long.valueOf(awardDataType.getSourceId());
			getManagedBean().setPayLoadData(awardDataType);

			getManagedBean()
					.setRecommendDtlIds(awardDataType.getGroupkeyList());

			Long currentStatusId = getManagedBean().getCurrentStatusId();

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(currentStatusId);
			StatusResDTO resDTO = statusSO.retrieveStatusByStatusId(reqDTO);
			getManagedBean().setDocType(resDTO.getPmStatus().getDocType());

			Long loiId = Long.valueOf(awardDataType.getAwardId());

			getManagedBean().setSourceId(sourceId);

			// Long loiId = 299L;

			ScLoi scLoi = new ScLoi();

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoiId(loiId);
			AwardSupplierResDTO awardSupplierResDTO = getQtAwardSO()
					.retrieveLOIDtlByLoiId(awardSupplierReqDTO);
			if (awardSupplierResDTO.getScLoi() != null) {
				scLoi = awardSupplierResDTO.getScLoi();
			}
			editLOI(scLoi, Boolean.TRUE);
			getManagedBean().setEditFlag(false);

//			FacesContext fc = FacesContext.getCurrentInstance();
//			fc.getApplication().getNavigationHandler()
//					.handleNavigation(fc, null, QTConstants.LOI_PAGE);
		} catch (Exception e) {
			handleException(e);
		} finally {
			statusSO = null;
			//qtAwardSO = null;
		}
	}

	
	/**
	 * Pre render page navigation to suppler response page based on task id
	 * @return void
	 */
	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);

			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			Long loiId = Long.valueOf(dpDataType.getAwardId());

			getManagedBean().setSourceId(sourceId);
			getManagedBean().setPayLoadData(dpDataType);

			// editSupplierLOIAction(loiId);
			
			ScLoi scLoi = new ScLoi();
			

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoiId(loiId);
			AwardSupplierResDTO awardSupplierResDTO = getQtAwardSO()
					.retrieveSupplierLOIDtl(awardSupplierReqDTO);
			if (awardSupplierResDTO.getScLoi() != null) {
				scLoi = awardSupplierResDTO.getScLoi();
			}
			
			// calc sub total items in acceptance letter.
			if(scLoi.getScLoiLoaItems() != null && scLoi.getScLoiLoaItems().size()>0){
				BigDecimal  subTotal = BigDecimal.valueOf(0d);
				List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
				for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
					subTotal = TemplateSpecHelper.getInstance().calcTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
							scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());
					scLoiLoaItem.setSubTotal(subTotal);
				}
				scLoi.setScLoiLoaItems(scLoiLoaItems);
			}
			// contract signer
			if(scLoi.getContractSigner() != null){
				getManagedBean().setContractSignerUser(scLoi.getContractSigner());
			}
		
			generateDeclarationFromVelocity(scLoi, awardSupplierResDTO.getScQt());
			getManagedBean().setScLoi(scLoi);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.SUPPLIER_LOI_PAGE);

		} catch (Exception e) {
			handleException(e);
		}
	}
	
	
	/**
	 * Generate declaration from velocity.
	 *
	 * @param scLoi the sc loi
	 * @param scQt the sc qt
	 * @return the string
	 * @throws Exception the exception
	 */
	private String generateDeclarationFromVelocity(ScLoi scLoi,ScQt scQt)
			throws Exception {
		PmUser pmUser = scLoi.getAckByUser();
		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();
		String procurementMode = null;
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoi.getScLoiLoa()
				.getDocType())) {
			procurementMode = SourcingConstants.PROCUREMENT_MODE_SIMPLE_QUOTE;
		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoi.getScLoiLoa()
				.getDocType())) {
			if (scQt != null) {
				if (ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt
						.getProcurementMode().getParameterCode())) {
					procurementMode = SourcingConstants.PROCUREMENT_MODE_QUOTATION;
				} else {
					procurementMode = SourcingConstants.PROCUREMENT_MODE_TENDER;
				}
			}
		}
		String title = procurementMode !=null ? procurementMode + " " + scLoiLoa.getTitle() 
				: scLoiLoa.getTitle();
		context.put("title", title);

		String userName = "&#60;supplier user name&#62;";
		String icNo = "&#60;ic no&#62;";
		if (pmUser != null) {
			userName = pmUser.getUserName();
			icNo = pmUser.getIdentificationNo();
		}
		context.put("userName", userName);
		context.put("icNo", icNo);

		context.put("supplierName", scLoiLoa.getSupplierName());
		// show (MOF/PKK/CIDB/ROS/ROC/ROB) in declaration.
		if(scLoiLoa.getSmSupplier() != null){
			SmSupplier smSupplier = scLoiLoa.getSmSupplier();
			if(smSupplier.getSmMofAccounts() != null && smSupplier.getSmMofAccounts().size()>0){
				context.put("mofNo", smSupplier.getSmMofAccounts().get(0).getMofNo());
			} else{
				context.put("mofNo", smSupplier.getRegNo());
			}
		}
		context.put("agency", pmOrgProfile.getOrgName());

		String termsConditions = DocumentGenerator
				.getInstance()
				.getHtmlFromVelocity(SourcingConstants.LOI_DECLARATION, context);
		getManagedBean().setTermsConditions(termsConditions);

		return termsConditions;
	}
	
	
	/**
	 * Pre render page navigation to  multiple loi page based on task id
	 * @return void
	 */
	public void preRenderMultipleLoiView() {
		debugLogger("preRenderMultipleLoiView");
		
		try {
			SCEvaluationDataType evaluationDataType = getBpmTaskDetails(SCEvaluationDataType.class);
			
			Long sourceId = Long.valueOf(evaluationDataType.getQtId());
			getManagedBean().setPayLoadData(evaluationDataType);
			
			retrieveMultipleLoiDtls(sourceId, evaluationDataType.getCommitteeType(), evaluationDataType.getGroupkeyMa());
			getManagedBean().setMultipleLoiFlag(Boolean.TRUE);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, QTConstants.MULTI_LOI_PAGE);
		} catch (Exception e) {
			handleException(e);
		} 
	}
	
	/**
	 * Retrieve multiple loi dtls.
	 *
	 * @param qtId the qt id
	 * @param committeeType the committee type
	 * @param recommendDtlIds the recommend dtl ids
	 */
	private void retrieveMultipleLoiDtls(Long qtId, String committeeType, List<String> recommendDtlIds) {
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteeType(committeeType);
			reqDTO.setRecommendDtlIdList(recommendDtlIds);
			
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveMultipleLoiDtls(reqDTO);
			if(resDTO.isSuccessful()){
				ScQtFinalization scQtFinalization = resDTO.getScQtFinalization();
				List<ScQtRecommend> scQtRecommendList = resDTO.getScQtRecommendList();
				
				getManagedBean().setScQtFinalization(scQtFinalization);
				if(scQtFinalization.getScQt() != null){
					getManagedBean().setScQt(scQtFinalization.getScQt());
				}
				getManagedBean().setCommitteeType(committeeType);
				getManagedBean().setLois(resDTO.getScLois());
				getManagedBean().setRecommendList(scQtRecommendList);
				getManagedBean().setRecommendDtlIds(recommendDtlIds);
				
				if (scQtRecommendList != null) {
					for (ScQtRecommend scQtRecommend : scQtRecommendList) {
						updateRecommendBy(scQtRecommend);	
					}
				}
				
				// Committee Details
				ScQtCommittee scQtCommittee = resDTO.getScQtCommittee();
				getManagedBean().setEcActivityList(resDTO.getEcActivityList());
				getManagedBean().setTecActivityList(resDTO.getTecActivityList());
				getManagedBean().setFecActivityList(resDTO.getFecActivityList());
				getManagedBean().setScQtCommittee(scQtCommittee);
				scQtCommittee.setQtCommitteRemarks(initializeRemarks(
						scQtCommittee, 
						scQtFinalization.getCurrentStatus(),
						scQtCommittee.getQtCommitteRemarks(),
						ScQtCommitteRemarks.class));
				
				// LOI Reports
				getManagedBean().setRecommendedLoi(resDTO.getScLoi());
				getManagedBean().setScLoi(resDTO.getScLoi());

				// status
				getCurrentStatus(getManagedBean().getCurrentStatusId());
			}
			
		} finally {
			//qtAwardSO = null;
		}
	}
	
	/**
	 * Retrieve qt details.
	 * get scQt details from the database.
	 * @param qtId the qt id
	 */
	public void retrieveQTDetails(Long qtId) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);
			
			QuationTenderResDTO resDTO = qtDetailSO.retriveQtById(reqDTO);
			ScQt scQt = resDTO.getQtDetail();
			populateParentOrgProfile(scQt);
			getManagedBean().setScQt(scQt);
		} finally {
			qtDetailSO = null;
		}
	}

	/**
	 * Retrieve loi list.
	 * get loi list from database to show in loi list page
	 * @param qtId the qt id
	 */
	public void retrieveLOIList(Long qtId) {
		debugLogger("retrieveLOIList");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
//			reqDTO.setOrgProfileId(32950L); // TODO - change in SO to remove
//											// this.
			reqDTO.setRecommendDtlIdList(getManagedBean().getRecommendDtlIds());

			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrievePendingLoiList(reqDTO);

			// loi loa list
			//if (resDTO.getLoiLoas() != null && resDTO.getLoiLoas().size() > 0) {
				// getManagedBean().setLoiLoaBeans(prepareLoiLoaList(resDTO.getLoiLoas()));
				//reqDTO.setScLoi(resDTO.getLoiLoas().get(0).getScLois().get(0));
			//}

			getManagedBean().setLoiLoas(resDTO.getLoiLoas());
			getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());

			// rec supplier in loi list
			getManagedBean().setRecSupplierList(resDTO.getLoiLoas());

			// status
			getCurrentStatus(getManagedBean().getCurrentStatusId());
			
			// loiLoaList Title
			getLoiLoaListTitle(null);

//			populateUserMinistryList();
//			initializeRemarks(new ScLoi());
//			getApproverNames();

		} catch (Exception ex) {
			handleException(ex);
		}
	}

	/**
	 * Prepare loi loa list.
	 *
	 * @param loiloas the loiloas
	 * @return the list
	 */
	private List<LoiLoaBean> prepareLoiLoaList(List<ScLoiLoa> loiloas) {
		List<LoiLoaBean> loiloaBeans = new ArrayList<LoiLoaBean>();
		if (loiloas != null && loiloas.size() > 0) {
			for (ScLoiLoa scLoiLoa : loiloas) {

				if (scLoiLoa.getScloas() != null
						&& scLoiLoa.getScloas().size() > 0) {
					for (ScLoa scLoa : scLoiLoa.getScloas()) {
						LoiLoaBean loiLoaBean = new LoiLoaBean();
						loiLoaBean.setScLoa(scLoa);
						loiLoaBean.setLoiLoaType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
						loiLoaBean.setLoiLoaStatus(getStatusDescById(
								scLoa.getPmStatus().getStatusId()));
						loiLoaBean.setPmStatus(scLoa.getPmStatus());
						loiloaBeans.add(loiLoaBean);
					}
				}

				if (scLoiLoa.getScLois() != null
						&& scLoiLoa.getScLois().size() > 0) {
					for (ScLoi scLoi : scLoiLoa.getScLois()) {
						LoiLoaBean loiLoaBean = new LoiLoaBean();
						loiLoaBean.setScLoi(scLoi);
						loiLoaBean.setLoiLoaType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
						loiLoaBean.setLoiLoaStatus(getStatusDescById(
								scLoi.getScWorkFlowStatus().getPmStatus().getStatusId()));
						loiLoaBean.setPmStatus(scLoi.getScWorkFlowStatus().getPmStatus());
						loiloaBeans.add(loiLoaBean);
					}
				}
			}
		}
		return loiloaBeans;
	}

	/**
	 * On row rec supplier action.
	 * show item details and nego terms by selecting supplier in award details.
	 * @param event the event
	 */
	public void onRowRecSupplierAction(SelectEvent event) {
		ScLoiLoa scLoiLoa = (ScLoiLoa) event.getObject();
		try {
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			reqDTO.setSupplierId(scLoiLoa.getSmSupplier().getSupplierId());
			reqDTO.setDocId(scLoiLoa.getDocId());
			getLoiLoaListTitle(scLoiLoa.getSmSupplier());
			reqDTO.setRecommendDtlIdList(getManagedBean().getRecommendDtlIds());
			reqDTO.setViewSupplierLoiLoa(true);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOILOAList(reqDTO);

			if (resDTO.getLoiLoas() != null && resDTO.getLoiLoas().size() > 0) {
				getManagedBean().setLoiLoaBeans(
						prepareLoiLoaList(resDTO.getLoiLoas()));
			} else{
				getManagedBean().setLoiLoaBeans(new ArrayList<LoiLoaBean>());
			}
		} catch (Exception ex) {
			handleException(ex);
		}
	}

	/**
	 * Tab menu listener.
	 * Tab navigation from one tab to another.
	 * @param selectedTabIndex the selected tab index.
	 */
	public void tabMenuListener(Integer selectedTabIndex) {
		debugLogger("tabMenuListener");
		if (selectedTabIndex.equals(0)) {
			getManagedBean().setSelectedTabIndex(0);

		} else if (selectedTabIndex.equals(1)) {
			getManagedBean().setSelectedTabIndex(1);
		} else if (selectedTabIndex.equals(2)) {
			getManagedBean().setSelectedTabIndex(2);
		} else if (selectedTabIndex.equals(3)) {
			getManagedBean().setSelectedTabIndex(3);

		}
	}

	/**
	 * viewItemDetails method to view item details in loi listing.
	 *
	 * @param bean the bean
	 */
	public void viewItemDetails(LoiLoaBean bean) {
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

		reqDTO = new AwardSupplierReqDTO();

		if (bean.getScLoi() != null) {
			reqDTO.setScLoi(bean.getScLoi());
		}
		if (bean.getScLoa() != null) {
			reqDTO.setScLoa(bean.getScLoa());
		}

		AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(reqDTO);

		if (resDTO.getScLoi() != null) {
			getManagedBean().setScLoi(resDTO.getScLoi());
		}
	}

	/**
	 * onSupplierSelectEvent method to view item details and nego terms in multiple LOI.
	 */
	public void onSupplierSelectEvent() {
		ScLoi scLoi = getManagedBean().getScLoi();
		boolean isTermPriceFound = false;
		getManagedBean().setItemDetailsFlag(true);
		isTermPriceFound = checkNegoTermPrice(scLoi);
		// calc total items in award details
		if(scLoi.getScLoiLoaItems() != null && scLoi.getScLoiLoaItems().size()>0){
			BigDecimal subTotal = null;
			BigDecimal  totalAmount = BigDecimal.valueOf(0d);
			BigDecimal  offerPrice = BigDecimal.valueOf(0d);
			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				if(isTermPriceFound ){
					getManagedBean().setHideColumnFlag(Boolean.TRUE);
					if(scLoiLoaItem.getScQtRecommendDtl().getScQtProposalItem() != null){
						offerPrice = BigDecimal.valueOf(scLoiLoaItem.getScQtRecommendDtl().getScQtProposalItem().getOfferPrice());
					}
					subTotal = TemplateSpecHelper.getInstance().calcTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
							offerPrice, scLoiLoaItem.getQty());
				} else{
					getManagedBean().setHideColumnFlag(Boolean.FALSE);
					subTotal = TemplateSpecHelper.getInstance().calcTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
							scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());
				}

				scLoiLoaItem.setSubTotal(subTotal);
				totalAmount = totalAmount.add(subTotal);
			}
			scLoi.setLoiAmt(totalAmount);
			scLoi.setScLoiLoaItems(scLoiLoaItems);
			priceKeyInEvent();
		}
	}

	/**
	 * Gets the qt award so.
	 *
	 * @return the qt award so
	 */
	private IQTAwardSO getQtAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	}

	
	/**
	 * getting Approver drop down listing in approval details.
	 *
	 * @return the approver names
	 */
	public List<PmUser> getApproverNames() {
		List<PmUser> approverUsers = this.getManagedBean().getApproverNames();

		// to get Approver
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(user.getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

			IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.PUBLICATION_APPROVER.value());
			// userReqDto.setOrgProfileId(resDTO.getUserOrgList()
			// .get(0).getPmOrgProfile().getOrgProfileId());

			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			getManagedBean().setApproverNames(userResDTO.getUserList());

		}
		return approverUsers;
	}

	/**
	 * To get current status for loi.
	 *
	 * @param statusId the status id
	 * @return the current status
	 */
	public void getCurrentStatus(Long statusId) {
		if (statusId != null) {
			getManagedBean().setStatus(getStatusDescById(statusId));
		}
	}

	/**
	 * Page navigation from loi details page to loi loa list.
	 */
	public void backToPrevious() {
		log.info("backToPrevious");
		retrieveLOIList(getManagedBean().getSourceId());
		getManagedBean().setEditFlag(false);
		getManagedBean().setButtonFlag(false);
		FacesContext fc = FacesContext.getCurrentInstance();

		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getBackToPreviousPage());
	}

	/**
	 * Adds the negotiation location in negotiation details.
	 */
	public void addNegotiationLocation() {
		debugLogger("addNegotiationLocation");
		ScLoiNegoLocation negoLocation = new ScLoiNegoLocation();
		List<ScLoiNegoLocation> negoLocations =  getManagedBean().getScLoi().getScNegoLocations();

		if (null == negoLocations) {
			negoLocations = new ArrayList<ScLoiNegoLocation>();
		}
		negoLocation.setScLoi(getManagedBean().getScLoi());
		negoLocations.add(negoLocation);
		getManagedBean().getScLoi().setScNegoLocations(negoLocations);
		this.getManagedBean().setNegoLocationList(negoLocations);
	}

	/**
	 * Adds the negotiation term in negotiation details.
	 */
	public void addNegotiationTerm() {
		debugLogger("addNegotiationTerm");
		ScLoiTerm negoTerm = new ScLoiTerm();
		List<ScLoiTerm> scNegoTerms = getManagedBean().getScLoi()
				.getScNegoTerms();
		if (null == scNegoTerms) {
			scNegoTerms = new ArrayList<ScLoiTerm>();
		}
		negoTerm.setPmTerm(null);
		negoTerm.setSelected(Boolean.TRUE);
		negoTerm.setScLoi(getManagedBean().getScLoi());
		negoTerm.setModified(true);
		scNegoTerms.add(negoTerm);

		getManagedBean().getScLoi().setScNegoTerms(scNegoTerms);
	}

	/**
	 * Populate pre defined details.
	 */
	public void populatePreDefinedDetails() {
		debugLogger("populatePreDefinedDetails");
		try {

			String languageCode = new FacesScopes().getUserLocale()
					.getLanguage();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setTermType(TermType.TERM_LOI.value());
			reqDTO.setLanguageCode(languageCode);

			/*
			 * AwardSupplierResDTO resDTO = getQtAwardSO()
			 * .retrieveTermDescListByTermType(reqDTO);
			 * 
			 * List<ScTermDesc> list = resDTO.getTermDescResultBean()
			 * .getResultList();
			 * 
			 * getManagedBean().setScTermDescList(list);
			 */

		} finally {
			//qtAwardSO = null;
		}
	}

	/**
	 * Adds the predefined terms in negotiation details.
	 */
	public void addPredefinedTerms() {
		debugLogger("addPredefinedTerms");

		ScLoiTerm scNegoTerm = new ScLoiTerm();
		// ScTerm scTerm = new ScTerm();
		List<ScLoiTerm> list = getManagedBean().getNegoTermsList();

		if (list == null) {
			list = new ArrayList<ScLoiTerm>();
		}
		/*
		 * List<ScTermDesc> scTermDescList = getManagedBean()
		 * .getSelectedCheckDetails(); scTerm.setScTermDescs(scTermDescList);
		 * //scNegoTerm.setScTerm(scTerm); System.out.println("Description : " +
		 * scNegoTerm.getScTerm().getScTermDescs().get(0).getTermDesc());
		 */
		scNegoTerm.setScLoi(getManagedBean().getScLoi());
		list.add(scNegoTerm);
		getManagedBean().setNegoTermsList(list);

	}

	/**
	 * Delete nego terms in negotiation details.
	 */
	public void deleteNegoTerm() {
		List<ScLoiTerm> negoTermsList = new ArrayList<ScLoiTerm>();
		boolean flag = true;
		List<ScLoiTerm> scLoiTerms = getManagedBean().getScLoi().getScNegoTerms();
		if (scLoiTerms != null && scLoiTerms.size()>0) {

			for (ScLoiTerm scLoiTerm : scLoiTerms) {
				if (!scLoiTerm.isSelectedTerm()) {
					negoTermsList.add(scLoiTerm);
				} else{
					flag = false;
				}
			}
		}
		if(flag){
			throw new EpException("Please select At least one Item");
		}
		getManagedBean().getScLoi().setScNegoTerms(negoTermsList);
	}
	
	/**
	 * Delete multiple loi nego term.
	 */
	public void deleteMultipleLoiNegoTerm() {
		ScLoi scLoi = getManagedBean().getScLoi();
		List<ScLoiTerm> scLoiTerms = getManagedBean().getSelectedNegoTermsList();
		if(scLoiTerms != null && scLoiTerms.size()>0){
			deleteRowsAction(scLoi.getScNegoTerms(), scLoiTerms);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}
	/**
	 * Delete nego locations in negotiation details.
	 */
	public void deleteNegoLocations() {
		List<ScLoiNegoLocation> selectedNegoLocs = getManagedBean()
				.getSelectedNegoLocationList();
		if (selectedNegoLocs != null && selectedNegoLocs.size()>0) {

			for (ScLoiNegoLocation scLoiNegoLocation : selectedNegoLocs) {
				getManagedBean().getScLoi().getScNegoLocations()
						.remove(scLoiNegoLocation);
			}
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Handle Department drop down changed - reset selected ptj profile and
	 * populate list of department.
	 * 
	 */
	public void handleMinistryProfileChanged() {
		debugLogger("handleMinistryProfileChanged");

		Long parentOrgProfileId = this.getManagedBean().getQt()
				.getMinistryOrgProfile().getOrgProfileId();

		if (parentOrgProfileId != null) {
			PmUser user = new FacesScopes().getUserSessionBean();

			if (user != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

				reqDTO.setUserId(user.getUserId());
				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);

				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);

				this.getManagedBean().setUserDepartmentList(
						resDTO.getOrgProfileList());
			}
		} else {
			this.getManagedBean().setUserDepartmentList(
					new ArrayList<PmOrgProfile>());
		}

		this.getManagedBean().getQt().getDepartmentOrgProfile()
				.setOrgProfileId(null);

		handleDepartmentProfileChanged();
	}

	/**
	 * Handle Department dropdown changed - reset selected ptj profile and
	 * populate list of division.
	 */
	public void handleDepartmentProfileChanged() {
		debugLogger("handleDepartmentProfileChanged");

		Long parentOrgProfileId = this.getManagedBean().getQt()
				.getDepartmentOrgProfile().getOrgProfileId();

		if (parentOrgProfileId != null) {
			PmUser user = new FacesScopes().getUserSessionBean();

			if (user != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

				reqDTO.setUserId(user.getUserId());
				reqDTO.setOrgType(OrganizationType.DIVISION.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);

				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);

				/*
				 * this.getManagedBean().setUserDivisionList(
				 * resDTO.getOrgProfileList());
				 */
			}
		} else {
			/*
			 * this.getManagedBean().setUserDivisionList( new
			 * ArrayList<PmOrgProfile>());
			 */
		}

		this.getManagedBean().getQt().getPmOrgProfile().setOrgProfileId(null);
	}

	/**
	 * Method to populate parent orgprofiles for scqt.
	 */
	public void populateParentOrgProfile(ScQt scQt) {
		PmOrgProfile parentOrgProfile = scQt.getPmOrgProfile()
				.getParentOrgProfile();

		while (parentOrgProfile != null) {
			// Ministry
			if (OrganizationType.MINISTRY.value().equals(
					parentOrgProfile.getOrgTypeCode().getParameterCode())) {
				scQt.setMinistryOrgProfile(parentOrgProfile);
			}
			// Department
			else if (OrganizationType.DEPARTMENT.value().equals(
					parentOrgProfile.getOrgTypeCode().getParameterCode())) {
				scQt.setDepartmentOrgProfile(parentOrgProfile);
			}

			parentOrgProfile = parentOrgProfile.getParentOrgProfile();
		}
	}

	
	/**
	 * Creates the loi.
	 * page navigate to loi details page.
	 * @param scLoiLoa the sc loi loa
	 * @return the string
	 * 
	 */
	public void createLOI(ScLoiLoa scLoiLoa) {
		try {
			renewSessionWithPreviousValues();
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			ScLoi loi = new ScLoi();
			loi.setScLoiLoa(scLoiLoa);
			reqDTO.setScLoi(loi);
			reqDTO.setRecommendDtlIdList(getManagedBean().getRecommendDtlIds());
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(reqDTO);

			populateLOIDetails(resDTO);
			getManagedBean().setEditFlag(true);
			getManagedBean().setButtonFlag(true);
		} catch (Exception ex) {
			handleException(ex);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
		
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.LOI_PAGE);
	}

	/**
	 * Edits the loi.
	 * page navigate to loi details page.
	 * @param scLoi the sc loi
	 */
	public void editLOI(ScLoi scLoi, boolean isEditFlag) {
		 //getManagedBean().setScLoi(scLoi);
		try {
			renewSessionWithPreviousValues();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setRecommendDtlIdList(null);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(reqDTO);

			populateLOIDetails(resDTO);

		} catch (Exception ex) {
			handleException(ex);
		}
		
		
		if(isEditFlag){
			getManagedBean().setButtonFlag(Boolean.TRUE);
		} else{
			getManagedBean().setButtonFlag(Boolean.FALSE);
		}

		getManagedBean().setEditFlag(Boolean.TRUE);
		FacesContext fc = FacesContext.getCurrentInstance();
		if(getManagedBean().getBackToPreviousPage() == null || getManagedBean().getBackToPreviousPage().isEmpty()){
			getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
		}
		
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.LOI_PAGE);
	}
	
	/**
	 * Reaload loi.
	 *
	 * @param scLoi the sc loi
	 */
	private void reloadLOI(ScLoi scLoi) {
		try {
			renewSessionWithPreviousValues();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setRecommendDtlIdList(null);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(reqDTO);

			populateLOIDetails(resDTO);
			getManagedBean().setButtonFlag(Boolean.TRUE);

		} catch (Exception ex) {
			handleException(ex);
		}
	}

	/**
	 * Populate loi details.
	 *
	 * @param resDTO the res dto
	 */
	private void populateLOIDetails(AwardSupplierResDTO resDTO) {
		ScLoi scLoi = resDTO.getScLoi();
		getManagedBean().setScLoi(scLoi);
		boolean isTermPriceFound = Boolean.FALSE;
		boolean isBiddingAward = Boolean.FALSE;
		
		// check nego term price
		Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId().longValue();
		if(QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)){
			isTermPriceFound = checkNegoTermPrice(scLoi);
		}

		  // online bidding
		if(scLoi.getScLoiLoa().getRecommendBy() != null){
			PmParameter recommendedBy = scLoi.getScLoiLoa().getRecommendBy();
			if(recommendedBy.getParameterCode().equals(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING)){
               isBiddingAward = Boolean.TRUE;
			}
		}

		// calc total items in award details
		if(scLoi.getScLoiLoaItems() != null && scLoi.getScLoiLoaItems().size()>0){
			BigDecimal subTotal = null;
			BigDecimal  totalAmount = BigDecimal.valueOf(0d);
			BigDecimal  offerPrice = BigDecimal.valueOf(0d);
			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				if(isTermPriceFound || isBiddingAward){
					getManagedBean().setHideColumnFlag(Boolean.TRUE);
					if(scLoiLoaItem.getScQtRecommendDtl().getScQtProposalItem() != null){
						offerPrice = BigDecimal.valueOf(scLoiLoaItem.getScQtRecommendDtl().getScQtProposalItem().getOfferPrice());
					}
					subTotal = TemplateSpecHelper.getInstance().calcTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
							offerPrice, scLoiLoaItem.getQty());
				} else{
					getManagedBean().setHideColumnFlag(Boolean.FALSE);
					subTotal = TemplateSpecHelper.getInstance().calcTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
							scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());
				}

				scLoiLoaItem.setSubTotal(subTotal);
				totalAmount = totalAmount.add(subTotal);
			}
			scLoi.setLoiAmt(totalAmount);
			scLoi.setScLoiLoaItems(scLoiLoaItems);
			priceKeyInEvent();
		}
	
		getManagedBean().setScQt(resDTO.getScQt());
		// branch list
		if (resDTO.getSmSupplierBranchs() != null
				&& resDTO.getSmSupplierBranchs().size() > 0) {
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
		}

		// Supplier Branch
		getManagedBean().setBranchName(scLoi.getSupplierBranch());
		getManagedBean()
				.setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());

		if (scLoi.isBrandNew()) {
			setUserDetails(scLoi);
			handleBranchDetailsChanged();
		}

		// Issue & Acknowledge Date
		scLoi.setIssueDate(DateUtils.getCurrentDateWithoutTime());
		scLoi.setAckDueDate(DateUtils.addDays(scLoi.getIssueDate(), 14));

		// approvers
		ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();
		PmUser approverUser = null;
		if (scLoiApproval != null) {
			approverUser = scLoiApproval.getApprover();

			if (!QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoi
					.getScWorkFlowStatus().getPmStatus().getStatusId().longValue())) {
				getManagedBean().setScLoiApproval(scLoi.getScLoiApproval());
			}
		}
		if (approverUser == null) {
			approverUser = new PmUser();
		}
		getManagedBean().setApproverUser(approverUser);
		if (resDTO.getApproverList() != null
				&& resDTO.getApproverList().size() > 0) {
			getManagedBean().setApproverNames(resDTO.getApproverList());
		}

		getManagedBean().setScLoiLoaItems(resDTO.getScLoi().getScLoiLoaItems());
		getManagedBean().setScLoi(resDTO.getScLoi());

		// TODO APPPROVER HISTORY
		getManagedBean().getLois().add(resDTO.getScLoi());

		// remarks
	
		getManagedBean().getScLoi().setScLoiRemarks(initializeRemarks(getManagedBean().getScLoi(), null,
				getManagedBean().getScLoi().getScLoiRemarks(), ScLoiRemark.class));

		// status
		getCurrentStatus(scLoi.getScWorkFlowStatus().getPmStatus().getStatusId());

//		initializeRemarks(new ScLoi());
		// getApproverNames();

		// Contract Signer
		PmUser contractSignerUser = scLoi.getContractSigner();
		if (contractSignerUser == null) {
			contractSignerUser = new PmUser();
		}
		
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());
		if(scLoi.getLoiAmt() != null) {
			userReqDTO.setApprovalLimit(scLoi.getLoiAmt());
		}
		userReqDTO.setOrgProfileId(scLoi.getScLoiLoa().getPmOrgProfile().getOrgProfileId());
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
				  
		getManagedBean().setContractSignerUser(contractSignerUser);
		getManagedBean().setContractSigners(userResDTO.getUserList());

		// TODO
		scLoi.setFileExtnNo("001");

		
		if(QTStatusConst.LOI_PENDING_NEGOTIATION.equals(scLoi.getScWorkFlowStatus().getPmStatus().getStatusId())){
		
			
			// Fulfilment Schedule

			List<ScQtSchedule> scheduleList = scLoi.getScQtSchedules();

			if (null != scheduleList && !scheduleList.isEmpty()) {

				this.getManagedBean().setTotalFrequency(scheduleList.size());
				//String startMonth = "";
				//String endMonth = "";
				for (ScQtSchedule scQtSchedule : scLoi.getScQtSchedules()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

					String fromdate = dateFormat.format(scQtSchedule.getFromDate());
					String todate = dateFormat.format(scQtSchedule.getToDate());
					String startMonth = fromdate.substring(3, 5);
					String startYear = fromdate.substring(6, fromdate.length());
					String endMonth = todate.substring(3, 5);
					String endYear = todate.substring(6, todate.length());

				/*	if (startdate.equalsIgnoreCase("01")) {
						startMonth = "0";
					}
					if (startdate.equalsIgnoreCase("02")) {
						startMonth = "1";
					}
					if (startdate.equalsIgnoreCase("03")) {
						startMonth = "2";
					}
					if (startdate.equalsIgnoreCase("04")) {
						startMonth = "3";
					}
					if (startdate.equalsIgnoreCase("05")) {
						startMonth = "4";
					}
					if (startdate.equalsIgnoreCase("06")) {
						startMonth = "5";
					}

					if (startdate.equalsIgnoreCase("07")) {
						startMonth = "6";
					}
					if (startdate.equalsIgnoreCase("08")) {
						startMonth = "7";
					}
					if (startdate.equalsIgnoreCase("09")) {
						startMonth = "8";
					}
					if (startdate.equalsIgnoreCase("10")) {
						startMonth = "9";
					}
					if (startdate.equalsIgnoreCase("11")) {
						startMonth = "10";
					}
					if (startdate.equalsIgnoreCase("12")) {
						startMonth = "11";
					}

					if (enddate.equalsIgnoreCase("01")) {
						endMonth = "0";
					}
					if (enddate.equalsIgnoreCase("02")) {
						endMonth = "1";
					}
					if (enddate.equalsIgnoreCase("03")) {
						endMonth = "2";
					}
					if (enddate.equalsIgnoreCase("04")) {
						endMonth = "3";
					}
					if (enddate.equalsIgnoreCase("05")) {
						endMonth = "4";
					}
					if (enddate.equalsIgnoreCase("06")) {
						endMonth = "5";
					}

					if (enddate.equalsIgnoreCase("07")) {
						endMonth = "6";
					}
					if (enddate.equalsIgnoreCase("08")) {
						endMonth = "7";
					}
					if (enddate.equalsIgnoreCase("09")) {
						endMonth = "8";
					}
					if (enddate.equalsIgnoreCase("10")) {
						endMonth = "9";
					}
					if (enddate.equalsIgnoreCase("11")) {
						endMonth = "10";
					}
					if (enddate.equalsIgnoreCase("12")) {
						endMonth = "11";
					}*/
					int start = Integer.parseInt(startMonth);
					int end = Integer.parseInt(endMonth);
					int sYear = Integer.parseInt(startYear);
					int eYear = Integer.parseInt(endYear);
					int diffYear = 0;
					int count = 0;

					if (eYear >= sYear) {
						if (!endYear.equals(startYear)) {
							diffYear = eYear - sYear;
						}
						for (int i = start + 1; i <= end + 1; i++) {
							count++;
						}
						count = count + (diffYear * 12);
						scQtSchedule.setTotalMonths(new BigDecimal(count));
					} else {
						scQtSchedule.setTotalMonths(BigDecimal.ZERO);
					}
					scQtSchedule.setStartMonth(String.valueOf(start));
					scQtSchedule.setEndMonth(String.valueOf(end));
					scQtSchedule.setStartYear(startYear);
					scQtSchedule.setEndYear(endYear);
				}
				this.sortFrequentScheduleList(scLoi.getScQtSchedules());
				// this.getManagedBean().setScheduleList(scheduleList);
			}

				List<ScQtProgressivePayment> progressivePayment = this
						.getManagedBean().getProgressivePaymentList();
				if (progressivePayment == null) {
					progressivePayment = new ArrayList<ScQtProgressivePayment>();

					if (null != scLoi.getScQtProgPayments()
							&& !scLoi.getScQtProgPayments().isEmpty()) {
						this.getManagedBean().setTotalProgressivePayment(
								scLoi.getScQtProgPayments().size());
						String startMonth = "";
						String endMonth = "";
						for (ScQtProgressivePayment scQtProgressivePayment : scLoi
								.getScQtProgPayments()) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"dd/MM/yyyy");

							String fromdate = dateFormat
									.format(scQtProgressivePayment.getFromDate());
							String todate = dateFormat
									.format(scQtProgressivePayment.getToDate());
							String startdate = fromdate.substring(3, 5);
							String startYear = fromdate.substring(6,
									fromdate.length());
							String enddate = todate.substring(3, 5);
							String endYear = todate.substring(6, todate.length());

							if (startdate.equalsIgnoreCase("01")) {
								startMonth = "0";
							}
							if (startdate.equalsIgnoreCase("02")) {
								startMonth = "1";
							}
							if (startdate.equalsIgnoreCase("03")) {
								startMonth = "2";
							}
							if (startdate.equalsIgnoreCase("04")) {
								startMonth = "3";
							}
							if (startdate.equalsIgnoreCase("05")) {
								startMonth = "4";
							}
							if (startdate.equalsIgnoreCase("06")) {
								startMonth = "5";
							}

							if (startdate.equalsIgnoreCase("07")) {
								startMonth = "6";
							}
							if (startdate.equalsIgnoreCase("08")) {
								startMonth = "7";
							}
							if (startdate.equalsIgnoreCase("09")) {
								startMonth = "8";
							}
							if (startdate.equalsIgnoreCase("10")) {
								startMonth = "9";
							}
							if (startdate.equalsIgnoreCase("11")) {
								startMonth = "10";
							}
							if (startdate.equalsIgnoreCase("12")) {
								startMonth = "11";
							}

							if (enddate.equalsIgnoreCase("01")) {
								endMonth = "0";
							}
							if (enddate.equalsIgnoreCase("02")) {
								endMonth = "1";
							}
							if (enddate.equalsIgnoreCase("03")) {
								endMonth = "2";
							}
							if (enddate.equalsIgnoreCase("04")) {
								endMonth = "3";
							}
							if (enddate.equalsIgnoreCase("05")) {
								endMonth = "4";
							}
							if (enddate.equalsIgnoreCase("06")) {
								endMonth = "5";
							}

							if (enddate.equalsIgnoreCase("07")) {
								endMonth = "6";
							}
							if (enddate.equalsIgnoreCase("08")) {
								endMonth = "7";
							}
							if (enddate.equalsIgnoreCase("09")) {
								endMonth = "8";
							}
							if (enddate.equalsIgnoreCase("10")) {
								endMonth = "9";
							}
							if (enddate.equalsIgnoreCase("11")) {
								endMonth = "10";
							}
							if (enddate.equalsIgnoreCase("12")) {
								endMonth = "11";
							}
							scQtProgressivePayment.setStartMonth(startMonth);
							scQtProgressivePayment.setEndMonth(endMonth);
							scQtProgressivePayment.setStartYear(startYear);
							scQtProgressivePayment.setEndYear(endYear);
							progressivePayment.add(scQtProgressivePayment);

						}
						this.sortProgressivePaymentList(progressivePayment);
						this.getManagedBean().setProgressivePaymentList(
								progressivePayment);
					}
				}
		}
	}

	/**
	 * Handle branch details changed.
	 * shows the supplier address details.
	 */
	public void handleBranchDetailsChanged() {
		log.info("handleBranchDetailsChanged");
		debugLogger("handleBranchDetailsChanged");

		ScLoi scLoi = this.getManagedBean().getScLoi();
		String selectedSupplierBranchName = this.getManagedBean()
				.getBranchName();

		if (selectedSupplierBranchName == null
				|| "HQ".equals(selectedSupplierBranchName)) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean()
					.getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = null;
			if (supplierHQPersonnel != null) {
				smCompanyBasic = supplierHQPersonnel.getSmAppl()
						.getSmCompanyBasic();
			}

			if (smCompanyBasic != null) {
				for (SmCompanyAddress smCompanyAddress : smCompanyBasic
						.getSmCompanyAddresses()) {
					if (AddressType.CONTACT_ADDRESS.value().equals(
							smCompanyAddress.getAddressType())) {
						log.info("found hq address");

						scLoi.setSupplierBranch("HQ");
						scLoi.setSupplierAddress(smCompanyAddress
								.getSmAddress());
						scLoi.setSupplierContact(supplierHQPersonnel.getName());
						scLoi.setSupplierPhoneNo(smCompanyBasic.getPhoneNo());
						scLoi.setSupplierFaxNo(smCompanyBasic.getFaxNo());
						scLoi.setSupplierMobNo(supplierHQPersonnel
								.getMobileNo());
						scLoi.setSupplierEmail(supplierHQPersonnel.getEmail());

						break;
					}
				}
			}
		} else {
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean()
					.getBranchsList();

			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchName().equals(
						selectedSupplierBranchName)) {
					log.info("found branch address");

					scLoi.setSupplierBranch(supplierBranch.getBranchName());
					scLoi.setSupplierAddress(supplierBranch.getSmAddress());
					scLoi.setSupplierContact(supplierBranch.getSmPersonnel()
							.getName());
					scLoi.setSupplierPhoneNo(supplierBranch.getPhoneNo());
					scLoi.setSupplierFaxNo(supplierBranch.getFaxNo());
					scLoi.setSupplierMobNo(supplierBranch.getSmPersonnel()
							.getMobileNo());
					scLoi.setSupplierEmail(supplierBranch.getSmPersonnel()
							.getEmail());

					break;
				}
			}
		}

	}

	/**
	 * save the LOI details.
	 * save - save data into database and initiate task for LOI details.
	 * page navigate to loi details page.
	 */
	public void saveAction() {
		debugLogger("saveAction");
		// IStatusSO statusSO = null;
		// IUserSO userSO = null;

		try {
			ScLoi scLoi = this.getManagedBean().getScLoi();
			Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId().longValue();

			parseLOIDetails(scLoi, "SAVE");

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());

			// AwardSupplierReqDTO awardSupplierReqDTO = new
			// AwardSupplierReqDTO();
			// awardSupplierReqDTO
			// .setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			//
			// statusSO = BusinessDelegate.getInstance().getServiceObject(
			// ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			// userSO = BusinessDelegate.getInstance().getServiceObject(
			// ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			//
			// String outcome = BpmOutcomeConst.BPM_SUBMIT;
			// ScLoi scLoi = this.getManagedBean().getScLoi();
			// scLoi.setIssueDate(new Date());
			//
			// // Date date = new Date();
			// System.out.println("Date : " + scLoi.getIssueDate());
			// Calendar cal = Calendar.getInstance();
			// cal.setTime(scLoi.getIssueDate());
			// cal.add(Calendar.DATE, 14);
			// System.out.println("Date : " + cal.getTime());
			// scLoi.setAckDueDate(cal.getTime());
			// System.out.println("LOI Response Due Date : "
			// + scLoi.getAckDueDate());
			//
			// // Setting the pm user id for LOI details
			// UserReqDTO userReqDTO = new UserReqDTO();
			// /*
			// *
			// userReqDTO.setUserId(getManagedBean().getScLoi().getApproverUser()
			// * .getUserId());
			// */
			// UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);
			//
			// PmUser user = userResDTO.getPmUser();
			// System.out.println("User id and Name" + user.getUserId() + "|"
			// + user.getUserName());
			// // scLoi.setApproverUser(user);
			//
			// // Setting the pm status for LOI details
			// StatusReqDTO reqDTO = new StatusReqDTO();
			// reqDTO.setStatusId(60000l);
			// StatusResDTO pmStatus =
			// statusSO.retrieveStatusByStatusId(reqDTO);
			// PmStatus status = pmStatus.getPmStatus();
			//
			// List<ScLoiNegoLocation> negoLocations = this.getManagedBean()
			// .getNegoLocationList();
			// scLoi.setScNegoLocations(negoLocations);
			//
			// List<ScLoiTerm> negoTerms = this.getManagedBean()
			// .getNegoTermsList();
			// for (ScLoiTerm scNegoTerm : negoTerms) {
			// // scNegoTerm.getScTerm().setTermType("I");
			// //
			// scNegoTerm.getScTerm().getScTermDescs().get(0).setScTerm(scNegoTerm.getScTerm());
			// }
			// scLoi.setScNegoTerms(negoTerms);
			//
			// awardSupplierReqDTO.setScLoi(scLoi);
			// awardSupplierReqDTO.setOutcome(outcome);
			// awardSupplierReqDTO.setSourceId(getManagedBean().getSourceId());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOIDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoi = resDTO.getScLoi();
				reloadLOI(scLoi);

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700004,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_LETTER_OF_INTENT),
																scLoi.getLoiNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
			}

		} finally {
			// statusSO = null;
			// userSO = null;
			//qtAwardSO = null;
		}
	}
	
	/**
	 * submit LOI list details.
	 * submit - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */
	public String submitLoiListAction() {
		debugLogger("submitLoiListAction");

		try {
			ScQt scQt = getManagedBean().getScQtFinalization().getScQt();
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setDocType(getManagedBean().getDocType());

			AwardSupplierResDTO resDTO = getQtAwardSO().completeLoiListBPMTask(reqDTO);

			if (resDTO.isSuccessful()) {
				renewSesionObject();

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700026,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
																scQt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {
			//iqtAwardSO = null;
		}

		return null;
	}

	/**
	 * submit LOI details.
	 * submit - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */
	public String submitAction() {
		debugLogger("submitAction");
		try {
			
			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoadData();

			ScLoi scLoi = this.getManagedBean().getScLoi();
			Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId().longValue();
			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			parseLOIDetails(scLoi, outcome);

			scLoi.setIssueDate(DateUtils.getCurrentDateWithoutTime());
			scLoi.setAckDueDate(DateUtils.addDays(scLoi.getIssueDate(), 14));

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setOutcome(outcome);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setPayLoad(awardDataType);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOIDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoi = resDTO.getScLoi();

				renewSesionObject();

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700026,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_LETTER_OF_INTENT),
																scLoi.getLoiNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {

			//qtAwardSO = null;
		}

		return null;
	}

	/**
	 * Update loi list bpm task action.
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 *
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */
	public void updateLOIListBpmTaskAction(String outcome) {
		debugLogger("updateLOIListBpmTaskAction");
		try {

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPayLoad(getManagedBean().getPayLoadData());

			AwardSupplierResDTO resDTO =getQtAwardSO().updateBPMAction(reqDTO);

			if (resDTO.isSuccessful()) {
				String qtNo = getManagedBean().getScQtFinalization().getScQt()
						.getQtNo();

				renewSesionObject();

				successMessage(MessageConst.INFO_700005,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT, qtNo);
			}
		} finally {
			//iqtAwardSO = null;
		}
	}
	
	/**
	 * Update bpm task.
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @param digiSign the digi sign
	 * @return AwardSupplierResDTO
	 */
	private AwardSupplierResDTO updateBPMTask(String outcome, PmDigiSign digiSign) {
		debugLogger("updateBPMTask");
		AwardSupplierResDTO awardSupplierResDTO = null;
		try {
			
			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoadData();

			ScLoi scLoi = this.getManagedBean().getScLoi();
			Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId().longValue();
			
			parseLOIDetails(scLoi, outcome);
			
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
					.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			awardSupplierReqDTO.setScLoi(scLoi);
			awardSupplierReqDTO.setSourceId(getManagedBean().getSourceId());
			awardSupplierReqDTO.setOutcome(outcome);
			awardSupplierReqDTO.setToken(new FacesScopes()
					.getBPMAuthenticationCtxToken());
			awardSupplierReqDTO.setTaskId(this.getManagedBean().getTaskId());
			awardSupplierReqDTO.setPayLoad(awardDataType);
			awardSupplierReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			awardSupplierReqDTO.setRoleCode(getUserRoleCode());
			
			// spki 
			if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
				RequestContext.getCurrentInstance().execute("blockWidget.hide();");
				RequestContext.getCurrentInstance().execute("spki_dlg.hide();");	
				String originalText = prepareOriginalText(scLoi.getScLoiLoa().getSmSupplier());
				String signedData = getManagedBean().getSpkiBackingBean().spkiSignTransaction(originalText);
				digiSign.setSignedData(signedData);
				if (null != digiSign) {
					updateSpkiDigiSignData(digiSign, ParameterConst.DOCTYPE_LETTER_OF_INTENT, 
							scLoi.getLoiId(), scLoi.getLoiNo(), originalText);
				}
			} else{
				//gpki
				if (null != digiSign) {
					updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_LETTER_OF_INTENT, 
							scLoi.getLoiId(), scLoi.getLoiNo());
				}
			}
			
			awardSupplierReqDTO.setPmDigiSign(digiSign);

			 awardSupplierResDTO = getQtAwardSO()
					.saveLOIDtl(awardSupplierReqDTO);

			this.getManagedBean().setScLoi(awardSupplierResDTO.getScLoi());

			if (awardSupplierResDTO.isSuccessful()) {
				scLoi = awardSupplierResDTO.getScLoi();

				renewSesionObject();

				String messageCode = null;

				if (QTStatusConst.LOI_PENDING_APPROVAL.equals(statusId)) {
					if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700007;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					} else if (BpmOutcomeConst.BPM_REVISE
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700009;
					}
				} else if (QTStatusConst.LOI_PENDING_REVISION.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700013;
				} else if (QTStatusConst.LOI_PENDING_NEGOTIATION
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700001;
				}

				successMessage(messageCode, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT,
						scLoi.getLoiNo());
			}
		} finally {
			//iqtAwardSO = null;
		}
		return awardSupplierResDTO;
	}
	

	/**
	 * Update bpm task action.
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 * Authenticating gpki before update bpm.
	 * @param outcome the outcome
	 */
	public void updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");
		this.getManagedBean().setAction(outcome);
		if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
			if(isDigiSigningRequired(RoleCode.RN_APPROVER.value()) || isDigiSigningRequired(RoleCode.PUBLICATION_APPROVER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			} 
		}  else if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
			RequestContext.getCurrentInstance().execute("spki_dlg.show();");

		} else {
			updateBPMTask(outcome, null);
		}	
	}

	
	  /**
  	 * Prepare original text.
  	 *
  	 * @param smSupplier the sm supplier
  	 * @return the string
  	 */
  	private String prepareOriginalText(SmSupplier  smSupplier) {
		  FacesScopes facesScopes = new FacesScopes();
		  StringBuilder sb = new StringBuilder();
		  String countryCode ="";
		  String orginalText = "";

		  if(smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null 
				  && smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().size()>0){
			  List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses();

			  for (SmCompanyAddress compaAddress : companyAddress) {
				  if(compaAddress.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())){
					  countryCode = compaAddress.getSmAddress().getPmCountry().getCountryCode();
					  break;
				  }
			  }
		  }
		  sb.append(getManagedBean().getScLoi().getLoiNo())
		  .append("|")
		  .append(facesScopes.getUserSessionBean().getIdentificationNo())
		  .append("|")
		  .append(smSupplier.getRegNo())
		  .append("|")
		  .append(countryCode);
		  orginalText  = sb.toString();
		  return orginalText;
	  }
	/**
	 * The method used to verify the login user is supplier.
	 * 
	 * @return true, if login user is supplier
	 *         false, if login user is not supplier
     */
	public boolean isSupplierRole() {
		return (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				||getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				||getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				||getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				||getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value()));
	}
	
	/**
     * This method used to retrieve login user information.
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * save multiple loi action.
	 * save - save data into database and initiate task for LOI details.
	 * 
	 * page navigate to acknowledge page.
	 */
	public void saveMultipleLoiAction() {
		debugLogger("saveMultipleLoiAction");
		try {
			
			ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
					
			AwardSupplierReqDTO reqDTO = parseMultipleLoiDtls();

			AwardSupplierResDTO resDTO = getQtAwardSO().saveMultipleLOIDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				renewSessionWithPreviousValues();
				
				retrieveMultipleLoiDtls(scQtFinalization.getScQt().getQtId(), 
						getManagedBean().getCommitteeType(), 
						getManagedBean().getRecommendDtlIds());

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700004,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_MULTIPLE_LOI),
																		scQtFinalization.getScQt().getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
			}

		} finally {
			//qtAwardSO = null;
		}
	}
	
	/**
	 * Update multi loi bpm task action.
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 * Authenticating gpki before update bpm.
	 * @param outcome the outcome
	 */
	public void updateMultiLoiBpmTaskAction(String outcome) {
		debugLogger("updateMultiLoiBpmTaskAction");
		this.getManagedBean().setAction(outcome);
		if(isDigiSigningRequired(RoleCode.PBA_CHAIRPERSON.value())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else {
			updateMultiLoiBpmTask(outcome, null);
		}	
	}

	/**
	 * Update multi loi bpm task.
	 *
	 * @param outcome the outcome
	 * @param digiSign the digi sign
	 */
	private void updateMultiLoiBpmTask(String outcome , PmDigiSign digiSign) {
		debugLogger("updateMultiLoiBpmTask");
		try {
			
			ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
			SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) getManagedBean()
					.getPayLoadData();
					
			AwardSupplierReqDTO reqDTO = parseMultipleLoiDtls();
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setOutcome(outcome);
			if (null != digiSign) {
				updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_MULTIPLE_LOI, 
						scQtFinalization.getScQt().getQtId(), scQtFinalization.getScQt().getQtNo());
			}
			
			reqDTO.setToken(new FacesScopes()
					.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setPayLoad(evaluationDataType);
			reqDTO.setDigiSignData(digiSign);
			saveScheduleList(); // Prepare Fulfilment Schedule
			reqDTO.setStatusId(getManagedBean().getCurrentStatusId());
			AwardSupplierResDTO resDTO = getQtAwardSO().saveMultipleLOIDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				super.renewSesionObject();

				successMessage(MessageConst.INFO_700005, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_MULTIPLE_LOI,
						scQtFinalization.getScQt().getQtNo());
			}

		} finally {
			//qtAwardSO = null;
		}
	}
	
	/**
	 * Parses the multiple loi dtls.
	 * prepare mulitple loi details to save or submit into database.
	 * @return the award supplier req dto
	 */
	private AwardSupplierReqDTO parseMultipleLoiDtls() {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		
		reqDTO.setScLoi(getManagedBean().getRecommendedLoi());
		reqDTO.setScQtFinalization(scQtFinalization);		
		reqDTO.setScLois(getManagedBean().getLois());
		
		// Save EC/TEC/FEC activities details.
		List<ScQtCommitteeActivity> ecActivityList = new ArrayList<ScQtCommitteeActivity>();
		if (getManagedBean().getEcActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean().getEcActivityList()) {
				ecActivityList.add(committeeActivity);
			}
		}
		if (getManagedBean().getTecActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean().getTecActivityList()) {
				ecActivityList.add(committeeActivity);
			}
		}
		if (getManagedBean().getFecActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean().getFecActivityList()) {
				ecActivityList.add(committeeActivity);
			}
		}
		reqDTO.setEcActivityList(ecActivityList);
		
		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
		scQtCommittee.setQtCommitteRemarks(
				finalizeRemarks(scQtCommittee.getQtCommitteRemarks()));
		reqDTO.setScQtCommittee(scQtCommittee);
		
		reqDTO.setScQtRecommendList(getManagedBean().getRecommendList());
		
		return reqDTO;
	}
	
	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Gets the loi letter stream content.
	 * to preview loi content as a pdf and can download.
	 * @return the loi letter stream content
	 * @throws Exception the exception
	 */
	public StreamedContent getLoiLetterStreamContent() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoi().getLoiNo());
		generateLoiLetterFromVelocityToPdf(getManagedBean().getScLoi(), (ScAttachment) pbAttachment);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				pbAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return pbStreamContent;
	}

	/**
	 * Preview letter.
	 * to preview loi cover letter as a pdf and can download.
	 * @throws Exception the exception
	 */
	public void previewLetter() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoi().getLoiNo());
		generateLoiLetterFromVelocityToPdf(getManagedBean().getScLoi(), (ScAttachment) pbAttachment);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				pbAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);
		getManagedBean().setStreamedContent(pbStreamContent);
		RequestContext.getCurrentInstance().execute("loiLetterDlg.show();");
	}

	/**
	 * Adds the meeting attendance.
	 */
	public void addMeetingAttendance() {
		debugLogger("..In addMeetingAttendance()..");

		try {
			ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
			List<ScQtMeetingAttendance> meetingAttendanceList = scQtCommittee.getMeetingAttendances();

			if (null == meetingAttendanceList) {
				meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
				scQtCommittee.setMeetingAttendances(
						meetingAttendanceList);
			}

			ScQtMeetingAttendance meeting = new ScQtMeetingAttendance();
			meeting.setQtCommittee(scQtCommittee);
			setUserDetails(meeting);
			meetingAttendanceList.add(meeting);

		} finally {

		}
	}
	
	/**
	 * Delete meeting attendance.
	 */
	public void deleteMeetingAttendance() {
		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
		List<ScQtMeetingAttendance> selectedMeetingAttendances = getManagedBean().getSelectedMeetingAttList();
		if(selectedMeetingAttendances != null && selectedMeetingAttendances.size()>0){
			deleteRowsAction(scQtCommittee.getMeetingAttendances(), selectedMeetingAttendances);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Generate loi letter from velocity to pdf.
	 *
	 * @param scLoi the sc loi
	 * @param attachment the attachment
	 * @throws Exception the exception
	 */
	private void generateLoiLetterFromVelocityToPdf(ScLoi scLoi, ScAttachment attachment)
			throws Exception {
		attachment.setFileName("Loi Letter.pdf");
		populateAttachmentDetails(attachment);
		
		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils.getDisplayDateTimeFormat(
								CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat numberDF = new DecimalFormat(
				commonUtils.getDisplayNumberFormat(
						CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils.getDisplayNumberFormat(
						CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();
		
		context.put("dateSDF", dateSDF);
		context.put("numberDF", numberDF);
		context.put("amountDF", amountDF);
		
		context.put("fileNo", scLoiLoa.getFileNo());
		
		String fileExtnNo = scLoi.getFileExtnNo();
		if (fileExtnNo == null) {
			fileExtnNo = "&lt;&lt;3-digits&gt;&gt;";
		}
		context.put("fileExtnNo", fileExtnNo);
		
		context.put("loiNo", scLoi.getLoiNo());

		String supplierName = scLoiLoa.getSupplierName();
		supplierName = supplierName.replaceAll("&", "&#38;");
		context.put("supplierName", supplierName);
		

		String supplierAddress = null;
		if (scLoi.getSupplierAddress() != null) {
			supplierAddress = getFullAddress(scLoi.getSupplierAddress());
			supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
			supplierAddress = supplierAddress.replaceAll("&", "&#38;");
		} else {
			supplierAddress = "&lt;&lt;Address&gt;&gt;";
		}
		context.put("supplierAddress", supplierAddress);
		
		String title = scLoiLoa.getTitle();
		title = title.replaceAll("&", "&#38;");
		context.put("title", title);
		List<ScLoiLoaItem> loiloaItems= new ArrayList<ScLoiLoaItem>();
		if (scLoi.getScLoiLoaItems() != null && scLoi.getScLoiLoaItems().size() > 0) {
			loiloaItems = scLoi.getScLoiLoaItems();
			
			for (ScLoiLoaItem scLoiLoaItem : loiloaItems) {
				if(scLoiLoaItem.getScRequestItem().getItemName() != null && !scLoiLoaItem.getScRequestItem().getItemName().isEmpty()){
					String itemName= scLoiLoaItem.getScRequestItem().getItemName();
					itemName = itemName.replaceAll(" & ", "&#38;");
					scLoiLoaItem.getScRequestItem().setItemName(itemName);
				}

				if(scLoiLoaItem.getScRequestItem().getTplSpecQuestion() != null && scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getTplSpecMaster() != null ){
					BigDecimal  bigDecimal = BigDecimal.valueOf(0d);
					if(scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getClUomPerDay() == null){
						scLoiLoaItem.getScRequestItem().getTplSpecQuestion().setClUomPerDay(bigDecimal);
					}

					if(scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getClUomFrequency() == null){
						scLoiLoaItem.getScRequestItem().getTplSpecQuestion().setClUomFrequency(bigDecimal);
					}

					if(scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getClUomPerMonth() == null){
						scLoiLoaItem.getScRequestItem().getTplSpecQuestion().setClUomPerMonth(bigDecimal);
					}

					if(scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getClDurationMonth() == null){
						scLoiLoaItem.getScRequestItem().getTplSpecQuestion().setClDurationMonth(bigDecimal);
					}
					if(scLoiLoaItem.getSubTotal() == null){
						scLoiLoaItem.setSubTotal(bigDecimal);
					}
				}
			}
			context.put("scLoiLoaItems", loiloaItems);
		}
		
		if (scLoi.getScNegoLocations() != null && scLoi.getScNegoLocations().size() > 0) {
			context.put("scNegoLocations", scLoi.getScNegoLocations());
		}
		
		 List<ScLoiTerm> selectedNegoTerms  = new ArrayList<ScLoiTerm>();
		 if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			 for (ScLoiTerm scLoiTerm : scLoi.getScNegoTerms()) {
				 if(scLoiTerm.isSelected()){
					 selectedNegoTerms.add(scLoiTerm);
				 }
			}
		 }
		
		if(selectedNegoTerms != null && selectedNegoTerms.size()>0){
			context.put("scNegoTerms", selectedNegoTerms);
		}else if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			context.put("scNegoTerms", scLoi.getScNegoTerms());
		}

		String ackDueDays = "14";
		if (scLoi.getIssueDate() != null && scLoi.getAckDueDate() != null) {
			ackDueDays = DateUtils.getDaysDifference(scLoi.getIssueDate(), scLoi.getAckDueDate()) + "";
		}
		context.put("ackDueDays", ackDueDays);

		if(getManagedBean().getContractSignerUser() != null){
			if(getManagedBean().getContractSignerUser().getUserName() == null){
				context.put("loiSigner", "-");
			}else{
				context.put("loiSigner", getManagedBean().getContractSignerUser().getUserName());	
			}
		}
		context.put("loiSignerDesignation", scLoi.getLoiSignerDesig());


		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOI_LETTER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Parses the loi details.
	 *
	 * @param scLoi the sc loi
	 * @param outcome the outcome
	 */
	private void parseLOIDetails(ScLoi scLoi, String outcome) {
		if (scLoi.isBrandNew()) {
			scLoi.setIssueDate(new Date());

			List<ScLoiWorkFlowStatus> workFlowStatuses = new ArrayList<ScLoiWorkFlowStatus>();
			
			workFlowStatuses.add((ScLoiWorkFlowStatus)scLoi.getScWorkFlowStatus());
			scLoi.setScLoiWorkFlowStatuses(workFlowStatuses);
		}

		Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId().longValue();

		if (scLoi.isApprovalReq()
				&& QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)) {
			PmUser approverUser = getManagedBean().getApproverUser();

			if (approverUser.getUserId() != null) {
				ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();

				if (scLoiApproval == null) {
					scLoiApproval = new ScLoiApproval();
					scLoiApproval.setScLoi(scLoi);
					scLoiApproval.setApprover(approverUser);

					scLoi.setScLoiApproval(scLoiApproval);
				} else {
					scLoiApproval.setApprover(approverUser);
				}
			}
		} else if (scLoi.isApprovalReq()
				&& QTStatusConst.LOI_PENDING_APPROVAL.equals(statusId)) {
			ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();
			String action = null;
			if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_APPROVED.value();
			} else if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REJECTED.value();
			} else if (BpmOutcomeConst.BPM_REVISE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REVISED.value();
			}

			scLoiApproval.setApproverAction(action);
			scLoiApproval.setApproverActionDate(new Date());

			scLoi.setScLoiApproval(scLoiApproval);
		} 
		
		if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome)) {
			if (QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId) ||
					QTStatusConst.LOI_PENDING_REVISION.equals(statusId)) {
				if (!scLoi.isApprovalReq()){
					scLoi.setScLoiApproval(null);
				}
			}
		}

		if (QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			scLoi.setAckDate(new Date());
			scLoi.setAckByUser(new FacesScopes().getUserSessionBean());
		}

		PmUser contractSignerUser = getManagedBean().getContractSignerUser();
		if (contractSignerUser != null
				&& contractSignerUser.getUserId() != null) {
			scLoi.setContractSigner(contractSignerUser);
		}

		scLoi.setScLoiRemarks(finalizeRemarks(scLoi.getScLoiRemarks()));

		// nego terms
		if (QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId) ||
				QTStatusConst.LOI_PENDING_REVISION.equals(statusId) || QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
			if(scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size()>0){
				List<ScLoiTerm> selectedTerms = new ArrayList<ScLoiTerm>();
				for (ScLoiTerm loiTerm : scLoi.getScNegoTerms()) {
					if(loiTerm.isSelected()){
						selectedTerms.add(loiTerm);
					}
				}
				scLoi.setScNegoTerms(selectedTerms);
			}
		}

		if(QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)){
			if(getManagedBean().getScQt() != null){
				if(("P").equals(getManagedBean().getScQt().getScheduleType())){
					this.saveProgressivePaymntList();
				}else if(("F").equals(getManagedBean().getScQt().getScheduleType())){
					this.saveScheduleList();
				}
			}
		}
	}

	/**
	 * Renew session with previous values.
	 */
	private void renewSessionWithPreviousValues() {
		Object payLoadData = getManagedBean().getPayLoadData();
		Long sourceId = getManagedBean().getSourceId();
		String taskId = getManagedBean().getTaskId();
		String[] outcomes = getManagedBean().getOutComes();
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		String docType = getManagedBean().getDocType();
		String committeeType = getManagedBean().getCommitteeType();
		String  backToPreviousPage = getManagedBean().getBackToPreviousPage();
		List<String> recommendDtlIds = getManagedBean().getRecommendDtlIds();
		List<ScLoiTerm> negoTerms = getManagedBean().getNegoTermsList();
		boolean editButton  = getManagedBean().isEditFlag();
		boolean buttonFlag = getManagedBean().isButtonFlag();

		this.renewSesionObject();

		getManagedBean().setSourceId(sourceId);
		getManagedBean().setPayLoadData(payLoadData);
		getManagedBean().setTaskId(taskId);
		getManagedBean().setOutComes(outcomes);
		getManagedBean().setCurrentStatusId(currentStatusId);
		getManagedBean().setBackToPreviousPage(backToPreviousPage);
		if(payLoadData != null){
			if (payLoadData instanceof SCEvaluationDataType) {
				getManagedBean().setRecommendDtlIds(((SCEvaluationDataType)payLoadData).getGroupkeyMa());
			} else if (payLoadData instanceof SCAwardSupplierDataType) {
				getManagedBean().setRecommendDtlIds(((SCAwardSupplierDataType)payLoadData).getGroupkeyList());
			}
		}
		getManagedBean().setDocType(docType);
		getManagedBean().setRecommendDtlIds(recommendDtlIds);
		getManagedBean().setCommitteeType(committeeType);
		getManagedBean().setNegoTermsList(negoTerms);
		getManagedBean().setEditFlag(editButton);
		getManagedBean().setButtonFlag(buttonFlag);
	}

	/**
	 * Gets the deduction item type dropdown list.
	 *
	 * @return the deduction item type dropdown list
	 */
	public SelectItem[] getDeductionItemTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT));
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.DEDUCTION_TYPE, searchBean);
	}

	/**
	 * Deduction type change.
	 * Deduction Clause will show when deduction type changes
	 * @param scQtDeduction the sc qt deduction
	 */
	public void deductionTypeChange(ScQtDeduction scQtDeduction) {
		debugLogger("deductionTypeChange");

		IParameterSO parameterSO = null;
		IClauseSO clauseSO = null;
		try {
			Long parameterId = scQtDeduction.getDeductionType()
					.getParameterId();

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			clauseSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IClauseSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scQtDeduction.setDeductionType(resDTO.getPmParameter());
			getClausesTitle(scQtDeduction);
			ScQtDeductionClause deductionClause = null;
			List<ScQtDeductionClause> deductionClauseList = new ArrayList<ScQtDeductionClause>();
			if (!ParameterConst.DEDUCTION_TYPE_OTHERS
					.equalsIgnoreCase(scQtDeduction.getDeductionType()
							.getParameterCode())) {
				ClauseReqDTO clauseReqDTO = new ClauseReqDTO();
				clauseReqDTO.setDeductionType(scQtDeduction.getDeductionType()
						.getParameterCode());
				ClauseResDTO clauseResDTO = clauseSO
						.retrieveClausesByDeductionType(clauseReqDTO);
				if (clauseResDTO.getResultBean() != null) {
					List<PmClause> pmClauseList = clauseResDTO.getResultBean()
							.getResultList();
					for (PmClause pmClause : pmClauseList) {
						deductionClause = new ScQtDeductionClause();
						deductionClause.setPmClause(pmClause);
						deductionClause.setScQtDeduction(scQtDeduction);
						deductionClause.setClauseDesc(pmClause.getClauseDesc());
						deductionClauseList.add(deductionClause);
					}
					scQtDeduction.setScQtDeductionClauses(deductionClauseList);
				} else {
					scQtDeduction.setScQtDeductionClauses(null);
				}
			} else {
				scQtDeduction.setScQtDeductionClauses(null);
			}

			getManagedBean().setSelectedScQtDeduction(scQtDeduction);
		} finally {
			parameterSO = null;
		}
	}
	
	/**
	 * Gets the clauses title.
	 *
	 * @param deduction the deduction
	 * @return the clauses title
	 */
	private void getClausesTitle(ScQtDeduction deduction){
		StringBuilder title  = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if(deduction != null && deduction.getDeductionType() != null){
			title.append(getParameterDesc(deduction.getDeductionType()));
			title.append(">"); 
			title.append(" ");
		}

		Locale locale = new FacesScopes().getUserLocale();
		title.append(ResourceBundleUtils.getMessage("lbl.sc.deduction.clauses",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		getManagedBean().setClausesTitle(title.toString());
	}

	/**
	 * Gets the clauses title.
	 *
	 * @param deduction the deduction
	 * @return the clauses title
	 */
	private void getLoiLoaListTitle(SmSupplier smSupplier){
		StringBuilder title  = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if(smSupplier != null){
			title.append(smSupplier.getCompanyName());
			title.append(">"); 
			title.append(" ");
		}
		Locale locale = new FacesScopes().getUserLocale();
		title.append(ResourceBundleUtils.getMessage("lbl.sc.loiloa.loiloalist",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		getManagedBean().setLoiLoaListTitle(title.toString());
	}


	/**
	 * Adds the deduction payment action.
	 */
	public void addDeductionPaymentAction() {
		debugLogger("addDeductionPaymentAction");

		ScLoi scLoi = this.getManagedBean().getScLoi();
		List<ScQtDeduction> scQtDeductions = scLoi.getScQtDeductions();

		if (scQtDeductions == null) {
			scQtDeductions = new ArrayList<ScQtDeduction>();

			scLoi.setScQtDeductions(scQtDeductions);
		}

		ScQtDeduction scQtDeduction = new ScQtDeduction();
		scQtDeduction.setScLoi(scLoi);
		scQtDeduction.setDeductionType(new PmParameter());

		scQtDeductions.add(scQtDeduction);
	}
	
	
	/**
	 * Delete deduction payment.
	 */
	public void deleteDeductionPayment() {
		ScLoi scLoi = getManagedBean().getScLoi();
		ScQtDeduction qtDeduction = getManagedBean().getSelectedScQtDeduction();
		if(qtDeduction != null){
			deleteRowAction(scLoi.getScQtDeductions(), qtDeduction);
			getManagedBean().setSelectedScQtDeduction(null);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Adds the deduction clause action.
	 */
	public void addDeductionClauseAction() {
		debugLogger("addDeductionClauseAction");

		ScQtDeduction selectedScQtDeduction = this.getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> scQtDeductionClauses = selectedScQtDeduction
				.getScQtDeductionClauses();

		if (scQtDeductionClauses == null) {
			scQtDeductionClauses = new ArrayList<ScQtDeductionClause>();

			selectedScQtDeduction.setScQtDeductionClauses(scQtDeductionClauses);
		}

		ScQtDeductionClause scQtDeductionClause = new ScQtDeductionClause();
		scQtDeductionClause.setScQtDeduction(selectedScQtDeduction);
		scQtDeductionClauses.add(scQtDeductionClause);
	}

	
	/**
	 * Delete deduction clause.
	 */
	public void deleteDeductionClause() {
		ScQtDeduction scQtDeduction = getManagedBean().getSelectedScQtDeduction();
		List<ScQtDeductionClause> clauses = getManagedBean().getSelectedDeductionClauses();
		if(clauses != null && clauses.size()>0){
			deleteRowsAction(scQtDeduction.getScQtDeductionClauses(), clauses);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Contract signer event.
	 */
	public void contractSignerEvent() {
		debugLogger("contractSignerEvent");
		PmUser contractSigner = getManagedBean().getContractSignerUser();
		if(contractSigner != null){
			if(getManagedBean().getContractSigners() != null && getManagedBean().getContractSigners().size()>0 ){
				List<PmUser> contractSignerList  = getManagedBean().getContractSigners();
				for (PmUser user : contractSignerList) {

					if(contractSigner.getUserId().equals(user.getUserId())){
						getManagedBean().getScLoi().setLoiSignerDesig(user.getDesignation());
						getManagedBean().setContractSignerUser(user);
					}
				}
			}
		}
	}

	/**
	 * Approver required event.
	 */
	public void approverRequiredEvent() {
		debugLogger("approverRequiredEvent");
		ScLoi scLoi = getManagedBean().getScLoi();
		// approvers
		if(scLoi.isApprovalReq()){
			ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();
			PmUser approverUser = null;
			if (scLoiApproval != null) {
				approverUser = scLoiApproval.getApprover();
			}
			if (approverUser == null) {
				approverUser = new PmUser();
			}
			getManagedBean().setApproverUser(approverUser);
		} else{
			getManagedBean().setApproverUser(new PmUser());
		}
	} 


	/**
	 * Adds the schedule details.
	 */
	public void addScheduleDetails() {

		try {

			int year = Calendar.getInstance().get(Calendar.YEAR);
			List<ScQtSchedule> freqSchedule = this.getManagedBean().getScLoi()
					.getScQtSchedules();
			if (null == freqSchedule) {
				freqSchedule = new ArrayList<ScQtSchedule>();
			}

			if (freqSchedule.size() > this.getManagedBean().getTotalFrequency()) {

				List<ScQtSchedule> localSchedule = new ArrayList<ScQtSchedule>();
				for (int j = 0; j < this.getManagedBean().getTotalFrequency(); j++) {
					localSchedule.add(freqSchedule.get(j));

				}
				this.getManagedBean().getScLoi()
						.setScQtSchedules(localSchedule);
			} else {
				for (int i = freqSchedule.size(); i < this.getManagedBean()
						.getTotalFrequency(); i++) {
					ScQtSchedule schedule = new ScQtSchedule();
					schedule.setStartYear(String.valueOf(year));
					schedule.setEndYear(String.valueOf(year));
					schedule.setScheduleNo(BigDecimal.valueOf(i + 1));
					PmParameter parameter = this.getParameterByTypeAndCode(ParameterTypeConst.SCHEDULE_TYPE, ParameterConst.SCHEDULE_TYPE_PERIODIC);
					schedule.setScheduleType(parameter);
					setUserDetails(schedule);
					freqSchedule.add(schedule);
				}
				this.getManagedBean().getScLoi().setScQtSchedules(freqSchedule);
			}

		} finally {

		}
	}

	/**
	 * Gets the year details.
	 *
	 * @return the year details
	 */
	public List<SelectItem> getYearDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		FacesScopes face = new FacesScopes();
		try {

			selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
			selectItems.add(new SelectItem(1, getMonth(1)));
			selectItems.add(new SelectItem(2, getMonth(2)));
			selectItems.add(new SelectItem(3, getMonth(3)));
			selectItems.add(new SelectItem(4, getMonth(4)));
			selectItems.add(new SelectItem(5, getMonth(5)));
			selectItems.add(new SelectItem(6, getMonth(6)));
			selectItems.add(new SelectItem(7, getMonth(7)));
			selectItems.add(new SelectItem(8, getMonth(8)));
			selectItems.add(new SelectItem(9, getMonth(9)));
			selectItems.add(new SelectItem(10,getMonth(10)));
			selectItems.add(new SelectItem(11,getMonth(11)));
			selectItems.add(new SelectItem(12,getMonth(12)));

			this.getManagedBean().setYearList(selectItems);
		} finally {

		}
		return getManagedBean().getYearList();
	}
	
	/**
	 * Gets the month.
	 *
	 * @param monthId the month id
	 * @return the month
	 */
	private String getMonth(int monthId){
		Calendar fullMonth = Calendar.getInstance();
		fullMonth.set(00, monthId, 0000);

		Format formatter = new SimpleDateFormat("MMMM"); 
		String month = formatter.format(fullMonth.getTime());
		return month;
	}
	

	/**
	 * Calculate start month.
	 *
	 * @param event the event
	 */
	public void calculateStartMonth(AjaxBehaviorEvent event) {
		event.getComponent().getAttributes().get("value");

	}

	/**
	 * Gets the years display dtls.
	 *
	 * @return the years display dtls
	 */
	public List<SelectItem> getYearsDisplayDtls() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);

			selectItems.add(new SelectItem(null, 
					ResourceBundleUtils.getMessage(
						"lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(year, String.valueOf(year)));
			for (int i = 0; i < 20; i++) {
				year = year + 1;
				selectItems.add(new SelectItem(year, String.valueOf(year)));
			}

			this.getManagedBean().setYearsDisplayList(selectItems);
		} finally {

		}
		return getManagedBean().getYearsDisplayList();
	}

	/**
	 * Calculate schedule months.
	 *
	 * @param schedule the schedule
	 * @param startMonthAndYearFlag the start month and year flag
	 */
	public void calculateScheduleMonths(ScQtSchedule schedule , Boolean startMonthAndYearFlag) {

		List<ScQtSchedule> freqSchedule = this.getManagedBean().getScLoi()
				.getScQtSchedules();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}
		Integer startMonth = 0, endMonth = 0, startYear = 0, endYear = 0, diffYear = 0, count = 0;
		startMonth = schedule.getStartMonth() != null ? Integer
				.parseInt(schedule.getStartMonth()) : null;
		endMonth = schedule.getEndMonth() != null ? Integer.parseInt(schedule
				.getEndMonth()) : null;
		startYear = schedule.getStartYear() != null ? Integer.parseInt(schedule
				.getStartYear()) : null;
		endYear = schedule.getEndYear() != null ? Integer.parseInt(schedule
				.getEndYear()) : null;

		if (startMonthAndYearFlag && startMonth != null) {
			endMonth = startMonth;
			endYear = startYear;
			schedule.setEndMonth(endMonth.toString());
			if(endYear != null){
				schedule.setEndYear(endYear.toString());
			}
		}

		if (startMonth != null && endMonth != null && startYear != null
				&& endYear != null && endYear >= startYear) {
			if (!endYear.equals(startYear)) {
				diffYear = endYear - startYear;
			}
			for (int i = startMonth + 1; i <= endMonth + 1 + (diffYear * 12); i++) {
				count++;
			}
			schedule.setTotalMonths(new BigDecimal(count));
		} else {
			schedule.setTotalMonths(BigDecimal.ZERO);
		}

		int index = freqSchedule.indexOf(schedule);
		freqSchedule.set(index, schedule);

		this.getManagedBean().setScheduleList(freqSchedule);
	}
	
	/**
	 * Save schedule list.
	 */
	private void saveScheduleList() {
		List<ScQtSchedule> freqschedule = this.getManagedBean().getScLoi()
				.getScQtSchedules();
		List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
		if (freqschedule != null && !freqschedule.isEmpty()) {
			//String startMonth = "";
			//String endmonth = "";
			for (ScQtSchedule schedule : freqschedule) {
				/*if (schedule.getStartMonth().equalsIgnoreCase("0")) {
					startMonth = "01";
				} else if (schedule.getStartMonth().equalsIgnoreCase("1")) {
					startMonth = "02";
				} else if (schedule.getStartMonth().equalsIgnoreCase("2")) {
					startMonth = "03";
				} else if (schedule.getStartMonth().equalsIgnoreCase("3")) {
					startMonth = "04";
				} else if (schedule.getStartMonth().equalsIgnoreCase("4")) {
					startMonth = "05";
				} else if (schedule.getStartMonth().equalsIgnoreCase("5")) {
					startMonth = "06";
				}

				else if (schedule.getStartMonth().equalsIgnoreCase("6")) {
					startMonth = "07";
				} else if (schedule.getStartMonth().equalsIgnoreCase("7")) {
					startMonth = "08";
				} else if (schedule.getStartMonth().equalsIgnoreCase("8")) {
					startMonth = "09";
				} else if (schedule.getStartMonth().equalsIgnoreCase("9")) {
					startMonth = "10";
				} else if (schedule.getStartMonth().equalsIgnoreCase("10")) {
					startMonth = "11";
				} else if (schedule.getStartMonth().equalsIgnoreCase("11")) {
					startMonth = "12";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("0")) {
					endmonth = "01";
				} else if (schedule.getEndMonth().equalsIgnoreCase("1")) {
					endmonth = "02";
				} else if (schedule.getEndMonth().equalsIgnoreCase("2")) {
					endmonth = "03";
				}

				else if (schedule.getEndMonth().equalsIgnoreCase("3")) {
					endmonth = "04";
				} else if (schedule.getEndMonth().equalsIgnoreCase("4")) {
					endmonth = "05";
				} else if (schedule.getEndMonth().equalsIgnoreCase("5")) {
					endmonth = "06";
				}

				else if (schedule.getEndMonth().equalsIgnoreCase("6")) {
					endmonth = "07";
				} else if (schedule.getEndMonth().equalsIgnoreCase("7")) {
					endmonth = "08";
				} else if (schedule.getEndMonth().equalsIgnoreCase("8")) {
					endmonth = "09";
				} else if (schedule.getEndMonth().equalsIgnoreCase("9")) {
					endmonth = "10";
				} else if (schedule.getEndMonth().equalsIgnoreCase("10")) {
					endmonth = "11";
				} else if (schedule.getEndMonth().equalsIgnoreCase("11")) {
					endmonth = "12";
				}*/
				Date currentDate = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(currentDate);
				String day = localDate.substring(0, 2);
				String fromdate = day + "/" + schedule.getStartMonth() + "/"
						+ schedule.getStartYear();
				String toDate = day + "/" + schedule.getEndMonth() + "/"
						+ schedule.getEndYear();

				schedule.setScheduleNo(schedule.getScheduleNo());

				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					throw new EpException(e.toString());
				}
				if (schedule.getScLoi() == null) {
					schedule.setScLoi(this.getManagedBean().getScLoi());
				}
				localScheduleList.add(schedule);
			}
			getManagedBean().getScLoi().setScQtSchedules(localScheduleList);
		} else {
			getManagedBean().getScLoi().setScQtSchedules(null);
		}
	}

	/**
	 * Adds the progressive details.
	 */
	public void addProgressiveDetails() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		List<ScQtProgressivePayment> progressive = this.getManagedBean()
				.getProgressivePaymentList();
		if (null == progressive) {
			progressive = new ArrayList<ScQtProgressivePayment>();

		}

		if (progressive.size() > this.getManagedBean()
				.getTotalProgressivePayment()) {

			List<ScQtProgressivePayment> progressiveList = new ArrayList<ScQtProgressivePayment>();
			for (int j = 0; j < this.getManagedBean()
					.getTotalProgressivePayment(); j++) {
				progressiveList.add(progressive.get(j));

			}
			this.getManagedBean().setProgressivePaymentList(progressiveList);
		} else {
			for (int i = progressive.size(); i < this.getManagedBean()
					.getTotalProgressivePayment(); i++) {

				ScQtProgressivePayment payment = new ScQtProgressivePayment();
				payment.setStartYear(String.valueOf(year));
				payment.setEndYear(String.valueOf(year));
				payment.setPhaseNo(BigDecimal.valueOf(i + 1));
				// payment.setScheduleType(new BigDecimal(1));

				setUserDetails(payment);
				progressive.add(payment);
			}
			this.getManagedBean().setProgressivePaymentList(progressive);
		}
	}

	/**
	 * Save progressive paymnt list.
	 */
	private void saveProgressivePaymntList() {
		List<ScQtProgressivePayment> progressivePayemnt = this.getManagedBean()
				.getProgressivePaymentList();
		List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
		if (progressivePayemnt != null && !progressivePayemnt.isEmpty()) {
			String startMonth = "";
			String endmonth = "";
			for (ScQtProgressivePayment payment : progressivePayemnt) {
				if (payment.getStartMonth().equalsIgnoreCase("0")) {
					startMonth = "01";
				} else if (payment.getStartMonth().equalsIgnoreCase("1")) {
					startMonth = "02";
				} else if (payment.getStartMonth().equalsIgnoreCase("2")) {
					startMonth = "03";
				} else if (payment.getStartMonth().equalsIgnoreCase("3")) {
					startMonth = "04";
				} else if (payment.getStartMonth().equalsIgnoreCase("4")) {
					startMonth = "05";
				} else if (payment.getStartMonth().equalsIgnoreCase("5")) {
					startMonth = "06";
				} else if (payment.getStartMonth().equalsIgnoreCase("6")) {
					startMonth = "07";
				} else if (payment.getStartMonth().equalsIgnoreCase("7")) {
					startMonth = "08";
				} else if (payment.getStartMonth().equalsIgnoreCase("8")) {
					startMonth = "09";
				} else if (payment.getStartMonth().equalsIgnoreCase("9")) {
					startMonth = "10";
				} else if (payment.getStartMonth().equalsIgnoreCase("10")) {
					startMonth = "11";
				} else if (payment.getStartMonth().equalsIgnoreCase("11")) {
					startMonth = "12";
				}
				if (payment.getEndMonth().equalsIgnoreCase("0")) {
					endmonth = "01";
				} else if (payment.getEndMonth().equalsIgnoreCase("1")) {
					endmonth = "02";
				} else if (payment.getEndMonth().equalsIgnoreCase("2")) {
					endmonth = "03";
				}

				else if (payment.getEndMonth().equalsIgnoreCase("3")) {
					endmonth = "04";
				} else if (payment.getEndMonth().equalsIgnoreCase("4")) {
					endmonth = "05";
				} else if (payment.getEndMonth().equalsIgnoreCase("5")) {
					endmonth = "06";
				}

				else if (payment.getEndMonth().equalsIgnoreCase("6")) {
					endmonth = "07";
				} else if (payment.getEndMonth().equalsIgnoreCase("7")) {
					endmonth = "08";
				} else if (payment.getEndMonth().equalsIgnoreCase("8")) {
					endmonth = "09";
				} else if (payment.getEndMonth().equalsIgnoreCase("9")) {
					endmonth = "10";
				} else if (payment.getEndMonth().equalsIgnoreCase("10")) {
					endmonth = "11";
				} else if (payment.getEndMonth().equalsIgnoreCase("11")) {
					endmonth = "12";
				}
				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/" + startMonth + "/"
						+ payment.getStartYear();
				String toDate = date + "/" + endmonth + "/"
						+ payment.getEndYear();
				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						payment.setFromDate(df.parse(fromdate));
						payment.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getManagedBean().getScLoi().getScQtProgPayments();
				payment.setScLoi(getManagedBean().getScLoi());
				paymentList.add(payment);
			}
			getManagedBean().getScLoi().setScQtProgPayments(paymentList);

		} else {
			getManagedBean().getScLoi().setScQtProgPayments(null);
		}
	}
	
	/**
	 * Sort frequent schedule list.
	 *
	 * @param scQtScheduleList the sc qt schedule list
	 */
	private void sortFrequentScheduleList(List<ScQtSchedule> scQtScheduleList) {
		Comparator<ScQtSchedule> comparator = new Comparator<ScQtSchedule>() {
			@Override
			public int compare(ScQtSchedule o1, ScQtSchedule o2) {
				return new CompareToBuilder().append(o1.getScheduleNo(),
						o2.getScheduleNo()).toComparison();
			}
		};
		Collections.sort(scQtScheduleList, comparator);
	}
	
	/**
	 * Sort progressive payment list.
	 *
	 * @param progressivePaymentList the progressive payment list
	 */
	private void sortProgressivePaymentList(
			List<ScQtProgressivePayment> progressivePaymentList) {
		Comparator<ScQtProgressivePayment> comparator = new Comparator<ScQtProgressivePayment>() {
			@Override
			public int compare(ScQtProgressivePayment o1,
					ScQtProgressivePayment o2) {
				return new CompareToBuilder().append(o1.getPhaseNo(),
						o2.getPhaseNo()).toComparison();
			}
		};
		Collections.sort(progressivePaymentList, comparator);
	}


	/**
	 * Handle file upload.
	 *
	 * @param event the event
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		debugLogger("handleFileUpload");
		if (event.getFile() != null) {
			//String attachmentDocType = getManagedBean().getAttachmentDocType(); 
			ScAttachment scAttachment = null;
			ScLoiReport selectedScLoiReport = this.getManagedBean().getSelectedScLoiReport();
			ScLoiReportAttachment scLoiReportAttachment = selectedScLoiReport.getScLoiReportAttachment();
			if (null == scLoiReportAttachment) {
				scLoiReportAttachment = new ScLoiReportAttachment();
				scLoiReportAttachment.setScLoiReport(selectedScLoiReport);
				selectedScLoiReport.setScLoiReportAttachment(scLoiReportAttachment);
			}
			scLoiReportAttachment.setScLoiReport(selectedScLoiReport);
			scAttachment = scLoiReportAttachment;
			
			uploadAttachment(event.getFile(), scAttachment);
		}
	}
	
	/**
	 * Adds the loi report action.
	 */
	public void addLoiReportAction() {
		debugLogger("addLoiReportAction");
		
		ScLoi scLoi = this.getManagedBean().getScLoi();
		List<ScLoiReport> scLoiReports = scLoi.getScLoiReports();
		
		if (scLoiReports == null) {
			scLoiReports = new ArrayList<ScLoiReport>();
			scLoi.setScLoiReports(scLoiReports);
		}
		
		ScLoiReport scLoiReport = new ScLoiReport();
		scLoiReport.setScLoi(scLoi);
		scLoiReport.setScLoiReportAttachment(new ScLoiReportAttachment());

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scLoiReport.setDefinationType(resDTO.getPmParameter());
			scLoiReport.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOI_NEGOTIATION_RESULT); //NR
		} finally {
			parameterSO = null;
		}

		scLoiReports.add(scLoiReport);
	}
	
	/**
	 * Delete loi report.
	 */
	public void deleteLoiReport() {
		ScLoi scLoi = getManagedBean().getRecommendedLoi();
		List<ScLoiReport> selectedScLoiReports = getManagedBean().getSelectedScLoiReports();

		if(selectedScLoiReports != null && selectedScLoiReports.size()>0){
			deleteRowsAction(scLoi.getScLoiReports(), selectedScLoiReports);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Download report.
	 *
	 * @param scLoiReport the sc loi report
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadReport(ScLoiReport scLoiReport) throws IOException {
		debugLogger("downloadReport");
		ScLoiReportAttachment scLoiReportAttachment = scLoiReport.getScLoiReportAttachment();
		if (scLoiReportAttachment == null) {
			scLoiReportAttachment = generateDocumentAttachment(scLoiReport);
			scLoiReport.setScLoiReportAttachment(scLoiReportAttachment);
		}
		downloadFile(scLoiReportAttachment);
	}
	
	/**
	 * Download all reports.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");
		List<ScLoiReport> scLoiReports = getManagedBean().getScLoi().getScLoiReports();
		List<ScLoiReportAttachment> scAttachments = new ArrayList<ScLoiReportAttachment>();
		for (ScLoiReport scLoiReport : scLoiReports) {
			ScLoiReportAttachment scLoiReportAttachment = scLoiReport.getScLoiReportAttachment();
			if (scLoiReportAttachment == null) {
				scLoiReportAttachment = generateDocumentAttachment(scLoiReport);
				scLoiReport.setScLoiReportAttachment(scLoiReportAttachment);
			}
			scAttachments.add(scLoiReportAttachment);
		}
		downloadAllFiles("NGeP-LOI-Report", scAttachments);
	}
	
	/**
	 * Adds the loi supp doc action.
	 */
	public void addLoiSuppDocAction() {
		debugLogger("addLoiSuppDocAction");
		
		ScLoi scLoi = this.getManagedBean().getScLoi();
		List<ScLoiSupportingDoc> scLoiSuppDocs = scLoi.getScLoiSupportingDocs();
		
		if (scLoiSuppDocs == null) {
			scLoiSuppDocs = new ArrayList<ScLoiSupportingDoc>();
			scLoi.setScLoiSupportingDocs(scLoiSuppDocs);
		}
		
		ScLoiSupportingDoc scLoiSuppDoc = new ScLoiSupportingDoc();
		scLoiSuppDoc.setScLoi(scLoi);
		scLoiSuppDoc.setDocId(scLoi.getLoiId().toString());
		scLoiSuppDoc.setDocType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
		scLoiSuppDoc.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOI_SUPPORTING_DOC);
		scLoiSuppDoc.setPmDocument(null);

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scLoiSuppDoc.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}

		scLoiSuppDocs.add(scLoiSuppDoc);
	}
	
	/**
	 * Delete supporing docs in negotiation details.
	 */
	public void deleteSupporingDocs() {
		ScLoi scLoi = getManagedBean().getScLoi();
		List<ScLoiSupportingDoc> selectedScLoiSuppDocs = getManagedBean().getSelectedScLoiSuppDocs();

		if(selectedScLoiSuppDocs != null && selectedScLoiSuppDocs.size()>0){
			deleteRowsAction(scLoi.getScLoiSupportingDocs(), selectedScLoiSuppDocs);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Handle supp doc file upload.
	 *
	 * @param event the event
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void handleSuppDocFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		debugLogger("handleSuppDocFileUpload");
		if (event.getFile() != null) {
			//String attachmentDocType = getManagedBean().getAttachmentDocType(); 
			ScAttachment scAttachment = null;
			ScLoiSupportingDoc selectedScLoiSuppDoc = this.getManagedBean().getSelectedScLoiSuppDoc();
			ScLoiSupportingDocAttachment scLoiSupportingDocAttachment = selectedScLoiSuppDoc.getScLoiSupportingDocAttachment();
			if (null == scLoiSupportingDocAttachment) {
				scLoiSupportingDocAttachment = new ScLoiSupportingDocAttachment();
				scLoiSupportingDocAttachment.setScLoiSupportingDoc(selectedScLoiSuppDoc);
				selectedScLoiSuppDoc.setScLoiSupportingDocAttachment(scLoiSupportingDocAttachment);
			}			
			scLoiSupportingDocAttachment.setDocNo(selectedScLoiSuppDoc.getScLoi().getLoiNo());
			scAttachment = scLoiSupportingDocAttachment;
			
			uploadAttachment(event.getFile(), scAttachment);
		}
	}
	
	/**
	 * Generate document attachment.
	 *
	 * @param scLoiReport the sc loi report
	 * @return the sc loi report attachment
	 */
	private ScLoiReportAttachment generateDocumentAttachment(ScLoiReport scLoiReport) {
		String definitionType = scLoiReport.getDefinationType().getParameterCode();
		ScLoiReportAttachment scLoiReportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType)) {
			try {
				String docDefinitionKey = scLoiReport.getPmDocument().getDocDefinitionKey();
				scLoiReportAttachment = new ScLoiReportAttachment();
				scLoiReportAttachment.setScLoiReport(scLoiReport);
				scLoiReportAttachment.setDocNo(scLoiReport.getScLoi().getLoiNo());
				
				if (SourcingConstants.DOC_DEF_KEY_COVER.equals(docDefinitionKey)) {
					generateCoverPdfFromVelocity((ScAttachment) scLoiReportAttachment, getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_OFFERED_QT_DTLS.equals(docDefinitionKey)) {
					generateOfferedAndQTDtlsPdf((ScAttachment) scLoiReportAttachment, getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_LOCATION.equals(docDefinitionKey)) {
					generateNegotiationLocationPdf((ScAttachment) scLoiReportAttachment, getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_COMMITTEE.equals(docDefinitionKey)) {
					generateNegotiationCommitteePdf((ScAttachment) scLoiReportAttachment, getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_RESULT.equals(docDefinitionKey)) {
					generateNegotiationResultPdf((ScAttachment) scLoiReportAttachment, getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_RECOMMENDATION.equals(docDefinitionKey)) {
					generateNegotiationRecommendationPdf((ScAttachment) scLoiReportAttachment, getManagedBean().getScLoi());
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}
			scLoiReport.setScLoiReportAttachment(scLoiReportAttachment);
		}
		return scLoiReportAttachment;
	}

	/**
	 * Generate negotiation recommendation pdf.
	 *
	 * @param attachment the attachment
	 * @param scLoi the sc loi
	 * @throws Exception the exception
	 */
	private void generateNegotiationRecommendationPdf(ScAttachment attachment, ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Recommendation.pdf");
		populateAttachmentDetails(attachment);
		
		//ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		VelocityContext context = new VelocityContext();
		
		if (getManagedBean().getRecommendList() != null) {
			for (ScQtRecommend recommend : getManagedBean().getRecommendList()) {
				log.info("Item Name :" + recommend.getRequestItem().getItemName());
				/*for (ScQtRecommendDtl qtRecommendDtl : recommend.getRecommendDtls()) {
					if (qtRecommendDtl.isRecordStatus()) {
						System.out.println("Select Supplier Name : " + qtRecommendDtl.getSupplier().getCompanyName());
					}
					System.out.println("Supplier Name : " + qtRecommendDtl.getSupplier().getCompanyName());
					System.out.println("Supplier ID : " + qtRecommendDtl.getSupplier().getSupplierId());
				}*/
			}
			context.put("recommendList", getManagedBean().getRecommendList());
			context.put("bean", this);
		}
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/loi/loi-negotiationRecommendation.vm", context, new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate negotiation result pdf.
	 *
	 * @param attachment the attachment
	 * @param scLoi the sc loi
	 * @throws Exception the exception
	 */
	private void generateNegotiationResultPdf(ScAttachment attachment, ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Result.pdf");
		populateAttachmentDetails(attachment);
		
		//ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		VelocityContext context = new VelocityContext();
		if (getManagedBean().getLois() != null && !getManagedBean().getLois().isEmpty()) {
			for(ScLoi loi : getManagedBean().getLois()) {
				log.info("Supplier Name : " + loi.getScLoiLoa().getSupplierName());
				/*for (ScLoiTerm scLoiTerm : loi.getScNegoTerms()) {
					System.out.println("Location : " + scLoiTerm.getTermDesc());
				}
				for (ScLoiLoaItem loiLoaItem : loi.getScLoiLoaItems()) {
					System.out.println("Item name : " + loiLoaItem.getScRequestItem().getItemName());
					System.out.println("Offer Price : " + loiLoaItem.getScQtRecommendDtl().getScQtProposalItem().getOfferPrice());
					System.out.println("Final Price : " + loiLoaItem.getScQtRecommendDtl().getScQtProposalItem().getFinalPrice());
				}*/
			}
		}
		context.put("scLois", getManagedBean().getLois());
		context.put("bean", this);
			
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/loi/loi-negotiationResult.vm", context, new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate negotiation committee pdf.
	 *
	 * @param attachment the attachment
	 * @param scLoi the sc loi
	 * @throws Exception the exception
	 */
	private void generateNegotiationCommitteePdf(ScAttachment attachment, ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Committee.pdf");
		populateAttachmentDetails(attachment);
		
		//ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		VelocityContext context = new VelocityContext();
		List<ScQtCommitteeActivity> activityList = new ArrayList<ScQtCommitteeActivity>();
		
		if (getManagedBean().getEcActivityList() != null) {
			for (ScQtCommitteeActivity ecActivity : getManagedBean().getEcActivityList()) {
				if (ecActivity.isMultiLoiAttended()) {
					activityList.add(ecActivity);
				}
			}
		}
		if (getManagedBean().getTecActivityList() != null) {
			for (ScQtCommitteeActivity tecActivity : getManagedBean().getTecActivityList()) {
				if (tecActivity.isMultiLoiAttended()) {
					activityList.add(tecActivity);
				}
			}
		}
		if (getManagedBean().getFecActivityList() != null) {
			for (ScQtCommitteeActivity fecActivity : getManagedBean().getFecActivityList()) {
				if (fecActivity.isMultiLoiAttended()) {
					activityList.add(fecActivity);
				}
			}
		}
		if (getManagedBean().getScQtCommittee() != null) {
			for (ScQtCommitteeActivity activity : getManagedBean().getScQtCommittee().getCommitteeActivities()) {
				if (activity.isMultiLoiAttended()) {
					if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getMemberName() == null) {
							activity.getScCommitteeMember().setMemberName("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setMemberName("");
					}
					if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getDesignation() == null) {
							activity.getScCommitteeMember().setDesignation("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setDesignation("");
					}if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getGrade() == null) {
							activity.getScCommitteeMember().setGrade("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setGrade("");
					}if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getIcPassport() == null) {
							activity.getScCommitteeMember().setIcPassport("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setIcPassport("");
					}
					activityList.add(activity);
				}
			}
		}
		context.put("activityLists", activityList);
		context.put("bean", this);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/loi/loi-negotiationCommittee.vm", context, new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate negotiation location pdf.
	 *
	 * @param attachment the attachment
	 * @param scLoi the sc loi
	 * @throws Exception the exception
	 */
	private void generateNegotiationLocationPdf(ScAttachment attachment, ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Location.pdf");
		populateAttachmentDetails(attachment);
		
		//ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		VelocityContext context = new VelocityContext();
		
		if (getManagedBean().getLois() != null && !getManagedBean().getLois().isEmpty()) {
			for(ScLoi loi : getManagedBean().getLois()) {
				log.info("Supplier Name : " + loi.getScLoiLoa().getSupplierName());
				/*for (ScLoiNegoLocation location : loi.getScNegoLocations()) {
					
					System.out.println("Location : " + location.getNegoLocation());
					System.out.println("Date : " + location.getNegoDate());
				}*/
			}
		}
		context.put("loiList", getManagedBean().getLois());
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/loi/loi-negotiationLocation.vm", context, new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate offered and qt dtls pdf.
	 *
	 * @param attachment the attachment
	 * @param scLoi the sc loi
	 * @throws Exception the exception
	 */
	private void generateOfferedAndQTDtlsPdf(ScAttachment attachment, ScLoi scLoi) throws Exception {
		attachment.setFileName("OfferedAndQTDetails.pdf");
		populateAttachmentDetails(attachment);
		
		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();
		
		context.put("title", scLoiLoa.getTitle());
		context.put("fulfilmentType", getParameterDesc(scLoiLoa.getFulfilmentTypeCode()));
		context.put("ptj", pmOrgProfile.getOrgName());
		String totalSuppliers = "";
		if (this.getManagedBean().getLois() != null) {
			totalSuppliers = String.valueOf(this.getManagedBean().getLois().size());
		}
		context.put("supplierSize", totalSuppliers);
		
		List<ScLoi> localList = new ArrayList<ScLoi>();
		if (this.getManagedBean().getLois() != null) {
			for (ScLoi loi : this.getManagedBean().getLois()) {
				if ("62405".equals(loi.getScWorkFlowStatus().getPmStatus().getStatusId().toString())) {
					localList.add(loi);
				}
			}
		}
		String totalStatusSuppliers = "";
		if (localList != null) {
			totalStatusSuppliers = String.valueOf(localList.size());
		}
		context.put("statusSupplierSize", totalStatusSuppliers);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/loi/loi-offeredAndQTDetails.vm", context, new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate cover pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoi the sc loi
	 * @throws Exception the exception
	 */
	private void generateCoverPdfFromVelocity(ScAttachment attachment, ScLoi scLoi) throws Exception {
		attachment.setFileName("Cover.pdf");
		populateAttachmentDetails(attachment);
		
		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();
		
		//context.put("ministry", pmOrgProfile.getParentOrgProfile().getOrgName());
    	context.put("agency", pmOrgProfile.getOrgName());
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/loi/loi-cover.vm", context, new FileOutputStream(getActualFilePath(attachment)));
	}
	
	/**
	 * Select all
	 * select all nego terms 
	 * @param event the event
	 */
	public void selectAll(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScLoiTerm> scLoiTerms = getManagedBean().getScLoi().getScNegoTerms();
		if (scLoiTerms != null) {
			for (ScLoiTerm scLoiTerm : scLoiTerms) {
				if (flag && scLoiTerm.isModified()) {
					scLoiTerm.setDeleted(true);
				} else {
					scLoiTerm.setDeleted(false);
				}
			}
		}
	}

	/**
	 * Nego term price event.
	 * when price found in terms, shows original offered price and after nego offered price.
	 */
	public void negoTermPriceEvent() {
		boolean termPriceFound = false;
		ScLoi scLoi = getManagedBean().getScLoi();

		if(scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size()>0){
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if(term.getTermDesc() != null){
					if(term.getTermDesc().equals(QTConstants.LOI_NEGO_TERM_PRICE)){
						termPriceFound = true;
					}
				}
			}
		}
		getManagedBean().setHideColumnFlag(termPriceFound);	
	}

	/**
	 * Price key in event.
	 * when keyin unit price, calculate subtotal after nego.
	 */
	public void priceKeyInEvent() {
		ScLoi scLoi = getManagedBean().getScLoi();

		// calc after nego total
		if(scLoi.getScLoiLoaItems() != null && scLoi.getScLoiLoaItems().size()>0){
			BigDecimal  subTotal = BigDecimal.valueOf(0d);
			BigDecimal  totalAmount = BigDecimal.valueOf(0d);
			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {

				subTotal = TemplateSpecHelper.getInstance().calcTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
						scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());
				scLoiLoaItem.setAfterNegoSubTotal(subTotal);
				totalAmount = totalAmount.add(subTotal);
			}
			scLoi.setScLoiLoaItems(scLoiLoaItems);
			scLoi.setAfterNegoTotal(totalAmount);
		}
		getManagedBean().setScLoi(scLoi);
	}


	/**
	 * Check nego term price.
	 *
	 * @param scLoi the sc loi
	 * @return true, if successful
	 */
	private boolean checkNegoTermPrice(ScLoi scLoi) {
		boolean termPriceFound = false;
		//ScLoi scLoi = getManagedBean().getScLoi();
		if(scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size()>0){
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if(term.getTermDesc().equals(QTConstants.LOI_NEGO_TERM_PRICE)){
					termPriceFound = true;
				}
			}
		}
		return termPriceFound;
	}
	
	/**
	 * Gets the award supplier list drop down.
	 *
	 * @return the award supplier list drop down
	 */
	public List<SelectItem> getAwardSupplierListDropDown() {
		List<ScQtRecommend> selectedRecommendList = getManagedBean().getSelectedRecommendList();
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		
		if (selectedRecommendList != null) {
			List<Long> supplierIds = new ArrayList<Long>();
			
			for (ScQtRecommend scQtRecommend : selectedRecommendList) {
				for (ScQtRecommendDtl scQtRecommendDtl : scQtRecommend.getRecommendDtls()) {
					SmSupplier smSupplier = scQtRecommendDtl.getSupplier();
					Long supplierId = smSupplier.getSupplierId();
					
					if (!supplierIds.contains(supplierId)) {
						selectItems.add(new SelectItem(supplierId, smSupplier.getCompanyName()));
						
						supplierIds.add(supplierId);
					}
				}
			}	
		}
		
		return selectItems;
	}
	
	/**
	 * Recommend supplier selected action.
	 *
	 * @param scQtRecommendDtl the sc qt recommend dtl
	 */
	public void recommendSupplierSelectedAction(ScQtRecommendDtl scQtRecommendDtl) {
		ScQtRecommend scQtRecommend = scQtRecommendDtl.getScQtRecommend();
		
		scQtRecommendDtl.setRecordStatusFlg(scQtRecommendDtl.isRecordStatus() ? DbValueConst.RECORD_STATUS_ACTIVE : DbValueConst.RECORD_STATUS_SUSPENDED);
		
		updateRecommendBy(scQtRecommend);
		
		if (ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD.equals(scQtRecommend.getRecommendByStr())) {
			for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
				recommendDtl.setAwardQty(null);
			}
			
		} else {
			scQtRecommendDtl.setAwardQty(scQtRecommend.getRequestItem().getQuantity());
		}
	}
	
	/**
	 * Update recommend by.
	 *
	 * @param scQtRecommend the sc qt recommend
	 */
	private void updateRecommendBy(ScQtRecommend scQtRecommend) {
		int activeCount = 0;
		for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
			if (recommendDtl.isRecordStatus()) {
				activeCount++;
			}
		}
		scQtRecommend.setRecommendByStr(activeCount > 1 ? ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD 
				: ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
		
		setAwardSupplierListByItem(scQtRecommend);
	}
	
	private void setAwardSupplierListByItem(ScQtRecommend scQtRecommend) {
		List<ScQtEvaluationResultDtl> shortlistedSupplierList = null;
		boolean isSuccessful = false;
		if (CollectionUtils.isEmpty(
				scQtRecommend.getShortListedSupplierList())) {
			// Retrieve the suppliers short listed for the selected item
			SuppFinalizationResDTO resDTO = this.getShortListedSuppliersByItem(scQtRecommend);
			isSuccessful = resDTO.isSuccessful();
			shortlistedSupplierList = resDTO.getQualifiedByItemSuppliers();
			scQtRecommend.setShortListedSupplierList(shortlistedSupplierList);
		} else {
			isSuccessful = true;
			shortlistedSupplierList = scQtRecommend
					.getShortListedSupplierList();
		}
		if (isSuccessful) {
			if (CollectionUtils.isNotEmpty(shortlistedSupplierList)) {
				for (ScQtEvaluationResultDtl evalResultDtl : shortlistedSupplierList) {
					calculateOfferedPrice(evalResultDtl,
							scQtRecommend.getRequestItem());
				}
			}
		}	
		PmParameter recommendBy = scQtRecommend.getRecommendBy();
		scQtRecommend.setRecommendByStr(recommendBy
				.getParameterCode());

		// set the checkbox or radio button of supplier selected
		// Multiple LOI or Online Bidding
		List<Long> supplierIdList = new ArrayList<Long>();
		for (ScQtRecommendDtl scQtRecommendDtl : scQtRecommend
				.getRecommendDtls()) {
			supplierIdList.add(scQtRecommendDtl.getSupplier().getSupplierId());
		}

		scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResultDtl>());
		for (ScQtEvaluationResultDtl scQtEvaluationResultDtl 
											: shortlistedSupplierList) {
			Long supplierId = scQtEvaluationResultDtl
									.getQtEvaluationResult().getScQtProposal()
									.getScQtSupplier().getSmSupplier()
									.getSupplierId();
			if (supplierIdList.contains(supplierId)) {
				scQtRecommend.getAwardSupplierList().add(
						scQtEvaluationResultDtl);
			}
		}	
	}
	
	private void calculateOfferedPrice(ScQtEvaluationResultDtl evalResultDtl,
			ScRequestItem requestItem) {
		ScQtProposalItem proposalItem = evalResultDtl.getQtEvaluationResult()
				.getScQtProposalItem();
		if (proposalItem != null) {
			BigDecimal offeredPrice = TemplateSpecHelper.getInstance()
					.calcTotalAmount(requestItem.getTplSpecQuestion(),
							new BigDecimal(proposalItem.getOfferPrice()),
							new BigDecimal(proposalItem.getOfferQuantity()));
			evalResultDtl.getQtEvaluationResult().setOfferedPrice(offeredPrice);
		}
	}
	
	private SuppFinalizationResDTO getShortListedSuppliersByItem(
			ScQtRecommend scQtRecommend) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (scQtRecommend.getTplSpecZoneDtl() != null) {
				reqDTO.setQtZoneCriteriaId(scQtRecommend.getTplSpecZoneDtl()
						.getSpecZoneId());
			}
			reqDTO.setRequestItemId(scQtRecommend.getRequestItem()
					.getRequestNoteItemId());
			reqDTO.setRecommendType(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM);

			resDTO = qtSuppFinalizationSO
					.retrieveEvaluationResultsByItem(reqDTO);
		} finally {
			qtSuppFinalizationSO = null;
		}

		return resDTO;
	}	
	
	/**
	 * Award supplier event.
	 */
	public void awardSupplierEvent() {
		Long awardSupplierId = getManagedBean().getAwardSupplierId();
		List<ScQtRecommend> selectedRecommendList = getManagedBean().getSelectedRecommendList();
		
		if (selectedRecommendList != null && awardSupplierId != null) {
			for (ScQtRecommend scQtRecommend : selectedRecommendList) {
				for (ScQtRecommendDtl scQtRecommendDtl : scQtRecommend.getRecommendDtls()) {
					Long supplierId = scQtRecommendDtl.getSupplier().getSupplierId();
					
					if (awardSupplierId.equals(supplierId)) {
						scQtRecommendDtl.setRecordStatus(true);
						recommendSupplierSelectedAction(scQtRecommendDtl);
					}
				}
			}	
		}
	}
	
	/**
	 * Calc sub total price.
	 * calculating subtotal for item list in award details.
	 * @param scLoiLoaItem the sc loi loa item
	 * @return the big decimal
	 */
	public BigDecimal calcSubTotalPrice(ScLoiLoaItem scLoiLoaItem) {
		BigDecimal total = TemplateSpecHelper.getInstance().calcTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
				scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());
		
		/*		BigDecimal total = scLoiLoaItem.getUnitPrice();

		if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
				.getClUomFrequency() != null) {
			total = total.multiply(scLoiLoaItem.getScRequestItem()
					.getTplSpecQuestion().getClUomFrequency());
		}

		if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
				.getClUomPerDay() != null) {
			total = total.multiply(scLoiLoaItem.getScRequestItem()
					.getTplSpecQuestion().getClUomPerDay());
		}

		if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
				.getClUomPerMonth() != null) {
			total = total.multiply(scLoiLoaItem.getScRequestItem()
					.getTplSpecQuestion().getClUomPerMonth());
		}

		if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
				.getClDurationMonth() != null) {
			total = total.multiply(scLoiLoaItem.getScRequestItem()
					.getTplSpecQuestion().getClDurationMonth());
		}

		total = total.multiply(scLoiLoaItem.getQty());*/

		return total;
	}

	/**
	 * Gpki sign action.
	 * Authenticating gpki before update bpm.
	 * @param digiSign the digi sign
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}
	
	
	/**
	 * Spki sign action.
	 * @param digiSign the digi sign
	 */
	public void spkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}
	/**
	 * Gpki sign multi loi action.
	 * Authenticating gpki before update bpm.
	 * @param digiSign the digi sign
	 */
	public void gpkiSignMultiLoiAction(PmDigiSign digiSign) {
		updateMultiLoiBpmTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Gets the pm digi sign.
	 *
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 *
	 * @param pmDigiSign the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Gets the text before sign.
	 * prepare sign in data for gpki authentication.
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if(getManagedBean().getScLoi() != null){
			sb.append(getManagedBean().getScLoi().getLoiNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 *
	 * @param textBeforeSign the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	
	/**
	 * Checks if is flag.
	 *
	 * @return true, if is flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * Sets the flag.
	 *
	 * @param flag the new flag
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
