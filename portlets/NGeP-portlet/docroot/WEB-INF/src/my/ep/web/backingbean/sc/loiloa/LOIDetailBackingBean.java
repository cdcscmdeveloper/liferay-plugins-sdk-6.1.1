/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.loiloa;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
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
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
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
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.type.sc.LoiLoaDocType;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.LoiLoaBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loiloa.LOIDetailManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;

// TODO: Auto-generated Javadoc
/**
 * The Class LOIDetailBackingBean.
 * 
 * @author Viswanath
 * 
 * @author Sri
 * 
 * @author Praneeth
 */
@ManagedBean(name = "lOIDetailBackingBean")
@RequestScoped
public class LOIDetailBackingBean extends
		SourcingBaseBackingBean<LOIDetailManagedBean> {

	/** The log. */
	public static Logger log = EpLogger.getLogger(LOIDetailBackingBean.class);

	/** The Constant LOI_DETAILS_MAIN_MENU. */
	public static final String LOI_DETAILS_MAIN_MENU = "/xhtml/sc/loi/loiDetailsMain.xhtml";

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
		// populateUserMinistryList();
		// initializeRemarks(new ScLoi());
		// getApproverNames();
	}

	/**
	 * Pre render page navigation to loi list view page based on task id
	 * 
	 * @return void
	 */
	public void preRenderLoiListView() {
		debugLogger("preRenderLoiListView");

		try {

			SCAwardSupplierDataType awardDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

			Long sourceId = Long.valueOf(awardDataType.getSourceId());
			
			String letterCreator = awardDataType.getLetterCreator();
			String currentUser = new FacesScopes().getUserSessionBean().getLoginId();
			
			if(StringUtils.isBlank(letterCreator)) {
				if (new FacesScopes()
				.hasRoleUser(RoleCode.DESK_OFFICER.value())) {
					awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
					awardDataType.setDeskOfficer(currentUser);
				} else {
					awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_REQUISITIONER);
					awardDataType.setSecretary(currentUser);
				}
			} else {
				if(letterCreator.equals(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER)) {
					awardDataType.setDeskOfficer(currentUser);
				} else {
					awardDataType.setSecretary(currentUser);
				}
			} 

			getManagedBean().setSourceId(sourceId);
			getManagedBean()
					.setRecommendDtlIds(awardDataType.getGroupkeyList());
			
			String finalizationMethod = getFinalizationMethodByAwardType(awardDataType.getAwardType());
			getManagedBean().setDocType(finalizationMethod);

			retrieveLOIList(sourceId);

			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(getManagedBean().getScQt());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.LOI_LIST_PAGE);
		} catch (Exception e) {
			handleException(e);
		} 
	}
	
	private String getFinalizationMethodByAwardType(String awardType) {		
		if (SourcingConstants.AWARD_TYPE_BIDDING_AWARD.equals(awardType)) {
			return ParameterConst.RECOMMEND_BY_ONLINE_BIDDING;
		} else if (SourcingConstants.AWARD_TYPE_MULTIPLE_LOI_AWARD.equals(awardType)) {
			return ParameterConst.RECOMMEND_BY_MULTIPLE_LOI;
		} else {
			return ParameterConst.RECOMMEND_BY_AWARD;
		}
	}

	/**
	 * Pre render page navigation to loi page based on task id
	 * 
	 * @return void
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		try {
			SCAwardSupplierDataType awardDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

			Long sourceId = Long.valueOf(awardDataType.getSourceId());

			getManagedBean()
					.setRecommendDtlIds(awardDataType.getGroupkeyList());

			Long currentStatusId = getManagedBean().getCurrentStatusId();

			getManagedBean().setDocType(getPmStatusByStatusId(currentStatusId).getDocType());

			Long loiId = Long.valueOf(awardDataType.getAwardId());
			getManagedBean().setSourceId(sourceId);

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoiId(loiId);
			AwardSupplierResDTO awardSupplierResDTO = getQtAwardSO()
					.retrieveLOIDtlByLoiId(awardSupplierReqDTO);
			if (awardSupplierResDTO.getScLoi() != null) {
				ScLoi scLoi = awardSupplierResDTO.getScLoi();
				editLOI(scLoi, Boolean.TRUE);
			}
			getManagedBean().setEditFlag(Boolean.FALSE);
			
		} catch (Exception e) {
			handleException(e);
		} finally {
			// qtAwardSO = null;
		}
	}

	/**
	 * Pre render page navigation to suppler response page based on task id
	 * 
	 * @return void
	 */
	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");
		
		SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

		Long sourceId = Long.valueOf(dpDataType.getSourceId());
		Long loiId = Long.valueOf(dpDataType.getAwardId());
		
		String redirectUrl = retrieveSupplierLOIDtl(sourceId, loiId, null, false);

		if (StringUtils.isNotBlank(redirectUrl)) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, redirectUrl);	
		}
	}
	
	/**
	 * Edits the supplier loi action.
	 * 
	 * @param sourceId 
	 * 		the source id (qt id / rn id
	 * @param loiId 
	 * 		the loi id
	 * @param previousPageUrl
	 * 		the previous page url.
	 * @param isReadonly
	 * 		the readonly flag
	 * 
	 * @return the supplier loi page
	 */
	public String retrieveSupplierLOIDtl(Long sourceId, Long loiId, String previousPageUrl, boolean isReadonly) {
		ScQt scQt = null;
		
		try { 
			getManagedBean().setSourceId(sourceId);
			if (previousPageUrl != null) {
				getManagedBean().setBackToPreviousPage(previousPageUrl);	
			}
			getManagedBean().setReadOnly(isReadonly);

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoiId(loiId);
			AwardSupplierResDTO awardSupplierResDTO = getQtAwardSO()
					.retrieveSupplierLOIDtl(awardSupplierReqDTO);
			if (awardSupplierResDTO.getScLoi() != null) {
				ScLoi scLoi = awardSupplierResDTO.getScLoi();
				getManagedBean().setScLoi(scLoi);
				// calc sub total items in acceptance letter.
				calculateOfferedAndFinalPriceByItem(scLoi);

				// contract signer
				if (scLoi.getContractSigner() != null) {
					getManagedBean().setContractSignerUser(
							scLoi.getContractSigner());
				}
				
				ScQtFinalization finalization  = awardSupplierResDTO.getScQtFinalization();

				if(finalization != null){
					scQt  = finalization.getScQt();
					if(scQt != null) {
						this.getManagedBean().setScQt(scQt);
						if(checkQTCancelRuquested(scQt)) {
							getManagedBean().setQtCancelRequested(true);
							Locale userLocale = new FacesScopes().getUserLocale();

							FacesContext.getCurrentInstance().addMessage(
									null,
									new FacesMessage(FacesMessage.SEVERITY_INFO, null,
											MessageUtils.getMessage(MessageConst.ERROR_760434,
													null,
													MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale)));
						}
					}
				}
				
				//check wheather refinalization bidding or not
				checkReFinalizationBidding(finalization, scQt);
				
				// QT Proposal validity expired
				getQtProposalValidExpiredMessage(scQt);
				
				//generateDeclarationFromVelocity(scLoi, scQt);
				
				return QTConstants.SUPPLIER_LOI_PAGE;
			}
		} catch (Exception e) {
			handleException(e);
		}
		
		return null;
	}
	
	/**
	 * Pre render page navigation to multiple loi page based on task id
	 * 
	 * @return void
	 */
	public void preRenderMultipleLoiView() {
		debugLogger("preRenderMultipleLoiView");

		FacesContext fc = FacesContext.getCurrentInstance();
		
		try {
			SCEvaluationDataType evaluationDataType = getBpmTaskDetails(SCEvaluationDataType.class, null);

			Long sourceId = Long.valueOf(evaluationDataType.getQtId());
			
			String loggedInUserIdentificationNo = getFacesScopes().getUserSessionBean()
														.getIdentificationNo();
			if (StringUtils.equals(loggedInUserIdentificationNo, 
					evaluationDataType.getFcSecretary())) {
				this.getManagedBean().setSecretaryUser(true);
			}

			retrieveMultipleLoiDtls(sourceId,
					evaluationDataType.getCommitteeType(),
					evaluationDataType.getGroupkeyMa());
			getManagedBean().setMultipleLoiFlag(Boolean.TRUE);
			
			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(getManagedBean().getScQt());
			
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.MULTI_LOI_PAGE);
		} catch (Exception e) {
			handleException(e);
			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null,
					CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			
		}
	}
	
	/**
	 * Retrieve multiple loi dtls.
	 * 
	 * @param qtId
	 *            the qt id
	 * @param committeeType
	 *            the committee type
	 * @param recommendDtlIds
	 *            the recommend dtl ids
	 */
	private void retrieveMultipleLoiDtls(Long qtId, String committeeType,
										 List<String> recommendDtlIds) {
		try {
			boolean isZonal = false;
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteeType(committeeType);
			reqDTO.setRecommendDtlIdList(recommendDtlIds);

			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrieveMultipleLoiDtls(reqDTO);
			if (resDTO.isSuccessful()) {
				ScQtFinalization scQtFinalization = resDTO
						.getScQtFinalization();
				List<ScQtRecommend> scQtRecommendList = resDTO
						.getScQtRecommendList();
				isZonal = scQtFinalization.getScQt().isZonal();
				List<ScLoi> scLois = resDTO.getScLois();
				Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap 
							= new HashMap<Long,List<ScLoiLoaItem>>();
				Map<Long,List<ScLoiLoaItem>> supplierLoiLoaItemsMap = null;
				if (CollectionUtils.isNotEmpty(scLois) && 
						CollectionUtils.isNotEmpty(recommendDtlIds)) {
					List<ScLoi> previousLois = null;
					
					for (ScLoi scLoi : scLois) {
						if (CollectionUtils.isNotEmpty(
									scLoi.getScLoiLoaItems())) {
							for (ScLoiLoaItem loiLoaItem : scLoi.getScLoiLoaItems()) {
								Long recommendDtlId = loiLoaItem
										.getScQtRecommendDtl().getRecommendDtlId();
								if (!recommendDtlIds.contains(
										String.valueOf(recommendDtlId))) {
									if (previousLois == null) {
										previousLois = new ArrayList<ScLoi>();
									}
									previousLois.add(scLoi);
									break;
								}
								
								if (isZonal) {
									Long specZoneId = loiLoaItem.getTplSpecZoneDtl().getSpecZoneId();
									if (loiLoaItemsMap.containsKey(specZoneId)) {
										loiLoaItemsMap.get(specZoneId).add(loiLoaItem);
									} else {
										List<ScLoiLoaItem> loiLoaItems = new ArrayList<ScLoiLoaItem>();
										loiLoaItems.add(loiLoaItem);
										loiLoaItemsMap.put(specZoneId, loiLoaItems);
									}
								}
							}
							
							if (!isZonal) { 
								if (CollectionUtils.isEmpty(previousLois) || 
										!previousLois.contains(scLoi)) {
									if (supplierLoiLoaItemsMap == null) {
										supplierLoiLoaItemsMap = new HashMap<Long,List<ScLoiLoaItem>>();
									}
									Long supplierId = scLoi.getScLoiLoa().getSmSupplier().getSupplierId();
									supplierLoiLoaItemsMap.put(supplierId, scLoi.getScLoiLoaItems());								
								}
							}
						}
					}
					if (CollectionUtils.isNotEmpty(previousLois)) {
						scLois.removeAll(previousLois);
					}
				}
				
				if (CollectionUtils.isNotEmpty(scLois)) {
					 for (ScLoi scLoi : scLois) {
						 calculateOfferedAndFinalPriceByItem(scLoi);
					 }
				}

				getManagedBean().setScQtFinalization(scQtFinalization);
				getManagedBean().setScQt(scQtFinalization.getScQt());
				getManagedBean().setCommitteeType(committeeType);
				getManagedBean().setLois(scLois);
				getManagedBean().setLoiLoaItemsMap(loiLoaItemsMap);
				getManagedBean().setSupplierLoiLoaItemsMap(supplierLoiLoaItemsMap);
				getManagedBean().setRecommendDtlIds(recommendDtlIds);
				
				String recommendType = scQtFinalization.getRecommendedTypeId()
													   .getParameterCode();
				if (isZonal) {
					this.getManagedBean().setMultiLOIZoneDetails(
							new ArrayList<TplSpecZoneDtl>());
					this.getManagedBean().setScQtRecommendsByZone(
							new LinkedHashMap<Long, List<ScQtRecommend>>());
					
					TplSpecZoneDtl specZoneDtl = null;
					List<TplSpecZoneDtl> zoneDetails = new ArrayList<TplSpecZoneDtl>();
					Map<Long, List<ScQtRecommend>> scQtRecommendsByZone 
									= new LinkedHashMap<Long, List<ScQtRecommend>>();					
					Map<Long,TplSpecZoneDtl> zoneMap = new HashMap<Long,TplSpecZoneDtl>();
					getManagedBean().setZoneMap(zoneMap);
					
					for (ScQtRecommend scQtRecommend : scQtRecommendList) {
						specZoneDtl = scQtRecommend.getTplSpecZoneDtl();
						
						if (!zoneDetails.contains(specZoneDtl)) {
							zoneDetails.add(specZoneDtl);
							zoneMap.put(specZoneDtl.getSpecZoneId(), specZoneDtl);
						}
						if (scQtRecommendsByZone.containsKey(
										specZoneDtl.getSpecZoneId())) {
							List<ScQtRecommend> scQtRecommends 
									= scQtRecommendsByZone.get(specZoneDtl.getSpecZoneId());
							scQtRecommends.add(scQtRecommend);
						} else {
							List<ScQtRecommend> scQtRecommends 
									= new ArrayList<ScQtRecommend>();
							scQtRecommends.add(scQtRecommend);
							scQtRecommendsByZone.put(
									specZoneDtl.getSpecZoneId(), scQtRecommends);
						}						
					}
					if (CollectionUtils.isNotEmpty(zoneDetails)) {
						this.getManagedBean().setMultiLOIZoneDetails(zoneDetails);
						this.getManagedBean().setSelectedZone(zoneDetails.get(0));
						this.getManagedBean().setZoneActiveIndex(
									zoneDetails.get(0).getZoneTabIndex());
						this.getManagedBean().setScQtRecommendsByZone(scQtRecommendsByZone);
						processMultiLOIDetails(recommendType, 
								scQtRecommendsByZone.get(zoneDetails.get(0).getSpecZoneId()));					
						generateZoneMenuModel();
					}					
				} else {
					processMultiLOIDetails(recommendType, scQtRecommendList);
				}

				// Committee Details
				ScQtCommittee scQtCommittee = resDTO.getScQtCommittee();
				getManagedBean().setEcActivityList(resDTO.getEcActivityList());
				getManagedBean().setTecActivityList(
						resDTO.getTecActivityList());
				getManagedBean().setFecActivityList(
						resDTO.getFecActivityList());
				getManagedBean().setScQtCommittee(scQtCommittee);
				if (scQtCommittee != null) {
					List<ScQtCommitteeActivity> fcActivityList
								= new ArrayList<ScQtCommitteeActivity>();
					for (ScQtCommitteeActivity commActivity 
									: scQtCommittee.getCommitteeActivities()) {
						if (commActivity.isAttended()) {
							fcActivityList.add(commActivity);
						}
					}
					getManagedBean().setFcActivityList(fcActivityList);
					
					scQtCommittee.setQtCommitteRemarks(initializeRemarks(
							scQtCommittee, scQtFinalization.getCurrentStatus(),
							scQtCommittee.getQtCommitteRemarks(),
							ScQtCommitteRemarks.class));
				}
			
				// LOI Reports
				getManagedBean().setRecommendedLoi(resDTO.getScLoi());
				getManagedBean().setScLoi(resDTO.getScLoi());

				// Display the negotiation results of the first supplier
				// in the order of display
				onSupplierSelectEvent();

				// status
				getCurrentStatus(getManagedBean().getCurrentStatusId());
			}

		} finally {
			// qtAwardSO = null;
		}
	}
	
	private void processMultiLOIDetails(String recommendType, 
										List<ScQtRecommend> scQtRecommendList) {
		getManagedBean().setRecommendList(scQtRecommendList);
		
		if (ParameterConst.RECOMMEND_TYPE_ITEM.equals(recommendType)) {
			getManagedBean().setItemized(true);
			
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				setRecommendedSupplierList(scQtRecommend);
				updateRecommendBy(scQtRecommend);
			}
		} else {
			getManagedBean().setItemized(false);
			
			ScQtEvaluationResultDtl awardedSupplier 
						= processRecommendedSuppliersByPKG(scQtRecommendList);
			getManagedBean().setRecommendedSupplierList(
					scQtRecommendList.get(0).getAwardSupplierList());	
			getManagedBean().setSelectedSupplier(awardedSupplier);
		}				
	}
	
	private ScQtEvaluationResultDtl processRecommendedSuppliersByPKG(List<ScQtRecommend> scQtRecommendList) {
		ScQtEvaluationResultDtl awardedSupplier = null;
		ScQtRecommend scQtRecommend = scQtRecommendList.get(0);
		setRecommendedSupplierList(scQtRecommend);
		
		List<ScQtEvaluationResultDtl> recommendedSuppplierList 
								= scQtRecommend.getAwardSupplierList();
		if (CollectionUtils.isNotEmpty(recommendedSuppplierList)) {
			for (ScQtEvaluationResultDtl recommendedSupplier 
											: recommendedSuppplierList) {
				Long recommendedSupplierId = recommendedSupplier.getQtEvaluationResult()
						.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
				BigDecimal totalOfferedPrice = new BigDecimal(0);
				if (recommendedSupplier.isAwareded()) {
					for (ScQtRecommendDtl recommendDtl 
								: scQtRecommend.getRecommendDtls()) {
						if (recommendedSupplierId.equals(
								recommendDtl.getSupplier().getSupplierId())) {
							recommendDtl.setRecordStatus(true);
							recommendDtl.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_ACTIVE);
							awardedSupplier = recommendedSupplier;
							break;
						}
					}
				}

				List<ScLoiLoaItem> awardedSupplierLoiLoaItems = new ArrayList<ScLoiLoaItem>();				
				Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap 
							= getManagedBean().getLoiLoaItemsMap();
				
				if (getManagedBean().isZonal()) {
					List<ScLoiLoaItem> loiLoaItems = loiLoaItemsMap.get(
							scQtRecommend.getTplSpecZoneDtl().getSpecZoneId());
					for (ScLoiLoaItem scLoiLoaItem : loiLoaItems) {
						Long itemSupplierId = scLoiLoaItem.getScQtRecommendDtl()
													.getSupplier().getSupplierId();
						if (recommendedSupplierId.equals(itemSupplierId)) {
							awardedSupplierLoiLoaItems.add(scLoiLoaItem);
						}
					}					
				} else {
					
					// Get loiLoaItems corresponding to the supplier
					awardedSupplierLoiLoaItems = getManagedBean().getSupplierLoiLoaItemsMap()
													.get(recommendedSupplierId);
				}
				totalOfferedPrice = calculateAfterNegoSubTotal(
							awardedSupplierLoiLoaItems, scQtRecommendList);
				totalOfferedPrice = totalOfferedPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				recommendedSupplier.getQtEvaluationResult()
								   .setOfferedPrice(totalOfferedPrice);
			}
		}	
		
		return awardedSupplier;
	}	
	
	private void generateZoneMenuModel() {
		if (CollectionUtils.isNotEmpty(
					this.getManagedBean().getMultiLOIZoneDetails())) {
			List<TplSpecZoneDtl> zoneDtls 
						= this.getManagedBean().getMultiLOIZoneDetails();

			// Dynamically generating the zone tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);

					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
//					zoneItem.setAjax(true);
					zoneItem.setTransient(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOncomplete("onZoneTabChangeAction([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]);");
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
				}
			}
			this.getManagedBean().setZoneMenuModel(zoneMenuModel);
		}
	}	
	
	/**
	 * To retrieve the bidding details on selection of a zone tab
	 */
	public void onZoneTabChangeAction() {
		Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedZoneTabIndex"));
		if (selectedZoneTabIndex == 
				this.getManagedBean().getZoneActiveIndex()) {
			return;
		} 
		
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getMultiLOIZoneDetails();	
		
		TplSpecZoneDtl selectedZone = zoneDetails.get(selectedZoneTabIndex);
		this.getManagedBean().setSelectedZone(selectedZone);
		this.getManagedBean().setZoneActiveIndex(selectedZoneTabIndex);
		processMultiLOIDetails(scQtFinalization.getRecommendedTypeId().getParameterCode(), 
				this.getManagedBean().getScQtRecommendsByZone().get(selectedZone.getSpecZoneId()));
	}	

	/**
	 * Retrieve qt details. get scQt details from the database.
	 * 
	 * @param qtId
	 *            the qt id
	 */
	public void retrieveQTDetails(Long qtId) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

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
	 * Retrieve loi list. get loi list from database to show in loi list page
	 * 
	 * @param qtId
	 *            the qt id
	 */
	public void retrieveLOIList(Long qtId) {
		debugLogger("retrieveLOIList");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setRecommendDtlIdList(getManagedBean().getRecommendDtlIds());
			
			AwardSupplierResDTO resDTO = getQtAwardSO().retrievePendingLoiList(
					reqDTO);

			getManagedBean().setLoiLoas(resDTO.getLoiLoas());
			getManagedBean().setScQt(resDTO.getScQt());
			getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());

			if (resDTO.getLoiLoas() != null && resDTO.getLoiLoas().size() > 0) {
				getManagedBean().setRecSupplierList(resDTO.getLoiLoas());
				// rec supplier in loi list
				//disableIsLoiRequired(resDTO.getLoiLoas());
			}
			// status
			getCurrentStatus(getManagedBean().getCurrentStatusId());

			// loiLoaList Title
			getLoiLoaListTitle(null);

		} finally {
		}
	}

	/**
	 * Prepare loi loa list.
	 * 
	 * @param loiloas
	 *            the loiloas
	 * @return the list
	 */
	private List<LoiLoaBean> prepareLoiLoaList(List<ScLoi> loiList, List<ScLoa> loaList) {
		List<LoiLoaBean> loiloaBeans = new ArrayList<LoiLoaBean>();

		for (ScLoa scLoa : loaList) {
			LoiLoaBean loiLoaBean = new LoiLoaBean();
			loiLoaBean.setScLoa(scLoa);
			loiLoaBean
			.setLoiLoaType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			loiLoaBean.setLoiLoaStatus(getStatusDescById(scLoa
					.getPmStatus().getStatusId()));
			loiLoaBean.setPmStatus(scLoa.getPmStatus());
			loiloaBeans.add(loiLoaBean);
		}

		for (ScLoi scLoi : loiList) {
			LoiLoaBean loiLoaBean = new LoiLoaBean();
			loiLoaBean.setScLoi(scLoi);
			loiLoaBean
			.setLoiLoaType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			loiLoaBean.setLoiLoaStatus(getStatusDescById(scLoi
					.getScWorkFlowStatus().getPmStatus()
					.getStatusId()));
			loiLoaBean.setPmStatus(scLoi.getScWorkFlowStatus()
					.getPmStatus());
			loiloaBeans.add(loiLoaBean);
		}
		return loiloaBeans;
	}

	/**
	 * On row rec supplier action. show item details and nego terms by selecting
	 * supplier in award details.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowRecSupplierAction(SelectEvent event) {
		debugLogger("onRowRecSupplierAction");
		ScLoiLoa scLoiLoa = (ScLoiLoa) event.getObject();
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			getLoiLoaListTitle(scLoiLoa);
			List<Long> loiLoaList = new ArrayList<Long>();
			loiLoaList.add(scLoiLoa.getLoiLoaId());
			reqDTO.setLoiLoaIds(loiLoaList);
			AwardSupplierResDTO resDTO = getQtAwardSO().getLoiAndLoaByLoiLoaList(reqDTO);
			if(!CollectionUtils.isEmpty(resDTO.getScLoas()) ||
					!CollectionUtils.isEmpty(resDTO.getScLois())) {
				getManagedBean().setLoiLoaBeans(
						prepareLoiLoaList(resDTO.getScLois(), resDTO.getScLoas()));
			}
			else {
				getManagedBean().setLoiLoaBeans(new ArrayList<LoiLoaBean>());
			}
		} finally {
		}
	}

	/**
	 * Tab menu listener. Tab navigation from one tab to another.
	 * 
	 * @param selectedTabIndex
	 *            the selected tab index.
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
	 * @param bean
	 *            the bean
	 */
	public void viewItemDetail(LoiLoaBean bean) {
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			String language = new FacesScopes().getUserLocale().getLanguage();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			reqDTO.setLanguageCode(language);

			if (bean.getScLoa() != null) {
				reqDTO.setScLoa(bean.getScLoa());
				resDTO = qtAwardSO.retrieveLOADtl(reqDTO);
			} else if (bean.getScLoi() != null) {
				reqDTO.setScLoi(bean.getScLoi());
				resDTO = qtAwardSO.retrieveLOIDtl(reqDTO);
			}

			if (resDTO != null) {
				if (resDTO.getScLoa() != null) {
					ScLoa loa = resDTO.getScLoa();
					bean.setScLoa(loa);
					bean.setScLoiLoaItems(loa.getScLoiLoaItems());
					bean.setSupplierName(loa.getScLoiLoa().getSupplierName());
				} else if (resDTO.getScLoi() != null) {
					ScLoi loi = resDTO.getScLoi();
					bean.setScLoi(loi);
					bean.setScLoiLoaItems(loi.getScLoiLoaItems());
					bean.setSupplierName(loi.getScLoiLoa().getSupplierName());
				}
			}

			getManagedBean().setSelectedLoiLoaBean(bean);

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}

	/**
	 * onSupplierSelectEvent method to view item details and nego terms in
	 * multiple LOI.
	 */
	public void onSupplierSelectEvent() {
		ScLoi scLoi = getManagedBean().getScLoi();
		if (!QTStatusConst.LOI_REJECTED.equals(
				scLoi.getScWorkFlowStatus().getPmStatus().getStatusId())) {
			boolean isTermPriceFound = Boolean.FALSE;
			getManagedBean().setItemDetailsFlag(Boolean.TRUE);
	
			isTermPriceFound = checkNegoTermPrice(scLoi);
			
			if (isTermPriceFound) {
				getManagedBean().setHideColumnFlag(Boolean.TRUE);
			}  else {
				getManagedBean().setHideColumnFlag(Boolean.FALSE);
			}
			calculateOfferedAndFinalPriceByItem(scLoi);
			
		} else {
			scLoi.setScNegoTerms(null);
			scLoi.setScLoiSupportingDocs(null);
			getManagedBean().setItemDetailsFlag(Boolean.FALSE);
			this.getManagedBean().setProgressivePaymentList(null);
		}
		
		getManagedBean().setSelectedScQtDeduction(null);
	}
	
	/**
	 * Calculate offered and final price by item.
	 *
	 * @param scLoi the sc loi
	 */
	private void calculateOfferedAndFinalPriceByItem(ScLoi scLoi) {
		getManagedBean().setScLoi(scLoi);
		List<ScLoiLoaItem> filteredMaxPriceItems  = getManagedBean().getScLoiLoaItems();
		filteredMaxPriceItems  = filterZoneTierOrTierPriceItems(scLoi);
		getManagedBean().setScLoiLoaItems(filteredMaxPriceItems);

		// calculate max price items only.
		boolean isCalculateMaxPriceItem = Boolean.FALSE;
		boolean isZonal = Boolean.FALSE;
		if (scLoi.getScLoiLoaItems() != null
				&& scLoi.getScLoiLoaItems().size() > 0) {
			BigDecimal offerPrice = BigDecimal.valueOf(0d);
			BigDecimal finalPrice = BigDecimal.valueOf(0d);
			BigDecimal offeredPriceSubTotal = BigDecimal.valueOf(0d);
			BigDecimal offeredPriceAmount = BigDecimal.valueOf(0d);

			BigDecimal afterNegoSubTotal = BigDecimal.valueOf(0d);
			BigDecimal afterNegoTotalAmount = BigDecimal.valueOf(0d);
			BigDecimal quantity = BigDecimal.valueOf(0d);

			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				
				if(scLoiLoaItem.getTplSpecZoneDtl() != null){
					isZonal = Boolean.TRUE;
				}
				
				quantity = scLoiLoaItem.getQty();
				if(null == scLoiLoaItem.getTplSpecTierQty() ){
					isCalculateMaxPriceItem = Boolean.TRUE;
				} else{
					if(filteredMaxPriceItems.contains(scLoiLoaItem)){
						isCalculateMaxPriceItem = Boolean.TRUE;
					} else{
						isCalculateMaxPriceItem = Boolean.FALSE;
					}
				}

				if(isCalculateMaxPriceItem ){
					if (scLoiLoaItem.getScQtProposalItem() != null) {
						offerPrice = scLoiLoaItem.getScQtProposalItem().getOfferPrice();
						offerPrice = offerPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						scLoiLoaItem.getScQtProposalItem().setOfferPrice(offerPrice);

						offeredPriceSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(
								scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), offerPrice, quantity);

						finalPrice = scLoiLoaItem.getScQtProposalItem().getFinalPrice();
						finalPrice = finalPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						scLoiLoaItem.getScQtProposalItem().setFinalPrice(finalPrice);	

						// offered price sub total and total
						offeredPriceAmount = offeredPriceAmount.add(offeredPriceSubTotal);
						offeredPriceSubTotal = offeredPriceSubTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						scLoiLoaItem.setOfferedPriceSubTotal(offeredPriceSubTotal);
						offeredPriceAmount = offeredPriceAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						
						afterNegoSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(
								scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), finalPrice, quantity);
						afterNegoSubTotal = afterNegoSubTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);

						// after nego price sub total and total
						afterNegoTotalAmount = afterNegoTotalAmount.add(afterNegoSubTotal);
						afterNegoTotalAmount = afterNegoTotalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						scLoiLoaItem.setAfterNegoSubTotal(afterNegoSubTotal);
						scLoiLoaItem.setBidFinalPriceSubTotal(afterNegoSubTotal);
					}
				}
			}

			scLoi.setScLoiLoaItems(scLoiLoaItems);
			scLoi.setOfferedPriceTotal(offeredPriceAmount); // offered price total
			scLoi.setLoiAmt(afterNegoTotalAmount); // unit price total
			scLoi.setAfterNegoTotal(afterNegoTotalAmount); // after nego total
			scLoi.setBidFinalPriceTotal(afterNegoTotalAmount); 
			getManagedBean().setZonal(isZonal);
		}		
	}
	
	/**
	 * Filter zone tier or tier price items.
	 *
	 * @param loi the loi
	 * @return the list
	 */
	private List<ScLoiLoaItem>  filterZoneTierOrTierPriceItems(ScLoi loi) {
		List<ScLoiLoaItem> scLoiLoaItems = loi.getScLoiLoaItems();
		List<ScLoiLoaItem> groupItems = new ArrayList<ScLoiLoaItem>();
		List<ScLoiLoaItem> filteredMaxPriceItems = new ArrayList<ScLoiLoaItem>();
		
		if(loi.getScLoiLoaItems() != null && loi.getScLoiLoaItems().size()>0){
			ScLoiLoaItem scLoiLoaItem  = null;
			int counter = 0;

			for (ScLoiLoaItem loiLoaItem : scLoiLoaItems) {

				if(scLoiLoaItem == null){
					scLoiLoaItem = loiLoaItem;
					groupItems.add(loiLoaItem);
				} else{

					if(loiLoaItem.getTplSpecZoneDtl() != null){
						if(loiLoaItem.getScRequestItem().equals(scLoiLoaItem.getScRequestItem()) && 
								loiLoaItem.getTplSpecZoneDtl().equals(scLoiLoaItem.getTplSpecZoneDtl())){
							groupItems.add(loiLoaItem);

						} else{
							scLoiLoaItem =  loiLoaItem;
							ScLoiLoaItem maxPriceItem  = findMaxPriceItem(groupItems);
							filteredMaxPriceItems.add(maxPriceItem);
							groupItems.clear();
							groupItems.add(loiLoaItem);
						}
					} else{
						if(loiLoaItem.getScRequestItem().equals(scLoiLoaItem.getScRequestItem())){
							groupItems.add(loiLoaItem);

						} else{
							scLoiLoaItem =  loiLoaItem;
							ScLoiLoaItem maxPriceItem  = findMaxPriceItem(groupItems);
							filteredMaxPriceItems.add(maxPriceItem);
							groupItems.clear();
							groupItems.add(loiLoaItem);
						}
					}
				}
				++counter;

				if(scLoiLoaItems.size() == counter){
					ScLoiLoaItem maxPriceItem  = findMaxPriceItem(groupItems);
					filteredMaxPriceItems.add(maxPriceItem);
					groupItems.clear();
				}
			}
		}
		return filteredMaxPriceItems;
	}
	
	

	/**
	 * Validate item tier price by item.
	 *
	 * @param scLoiLoaItem the sc loi loa item
	 * @param filteredMaxPriceItems the filtered max price items
	 */
	private void validateItemTierPriceByItem(ScLoiLoaItem scLoiLoaItem, List<ScLoiLoaItem> filteredMaxPriceItems) {
		debugLogger("validateItemTierPriceByItem");
		ScLoiLoaItem currentItem = null;
		ScLoiLoaItem prevItem = null;
		List<ScLoiLoaItem> groupByItems = new ArrayList<ScLoiLoaItem>();
		List<ScLoiLoaItem> scLoiLoaItems = getManagedBean().getScLoi().getScLoiLoaItems();
		for (ScLoiLoaItem loiLoaItem : scLoiLoaItems) {

			// zone tier group items
			if(null !=  loiLoaItem.getTplSpecZoneDtl()){
				if(loiLoaItem.getScRequestItem().getRequestNoteItemId().equals(scLoiLoaItem.getScRequestItem().getRequestNoteItemId()) && 
						loiLoaItem.getTplSpecZoneDtl().getZoneName().equals(scLoiLoaItem.getTplSpecZoneDtl().getZoneName())){
					groupByItems.add(loiLoaItem);
				}
			} else{
				// tier group items
				if(loiLoaItem.getScRequestItem().getRequestNoteItemId().equals(scLoiLoaItem.getScRequestItem().getRequestNoteItemId())){
					groupByItems.add(loiLoaItem);
				}
			}

		}
		findLowestIterPriceItem(groupByItems); 

		//validate tier price items
		for (ScLoiLoaItem item : groupByItems) {
			currentItem = item;

			if(prevItem != null && currentItem != null){
				if(prevItem.getUnitPrice().doubleValue() < currentItem.getUnitPrice().doubleValue()){
					throw new EpException(MessageConst.ERROR_700102, 
							new String[]{
							currentItem.getTplSpecZoneDtl() != null ? currentItem.getTplSpecZoneDtl().getZoneName() : "",
							currentItem.getTplSpecTierQty().getFromQty().toString(), 
							currentItem.getTplSpecTierQty().getToQty().toString(),
							prevItem.getUnitPrice().toString()}); 
				}
			}

			prevItem = currentItem;
		}
	}

	/**
	 * Find max price item.
	 *
	 * @param items the items
	 * @return the sc loi loa item
	 */
	private ScLoiLoaItem findMaxPriceItem(List<ScLoiLoaItem> items) {
		Comparator<ScLoiLoaItem> comparator = new Comparator<ScLoiLoaItem>() {
			@Override
			public int compare(ScLoiLoaItem o1, ScLoiLoaItem o2) {
				return new CompareToBuilder().append(o1.getTplSpecTierQty().getFromQty(),
						o2.getTplSpecTierQty().getFromQty()).toComparison();
			}
		};
		 ScLoiLoaItem  scLoiLoaItem = Collections.min(items, comparator);
		return scLoiLoaItem;
	}
	
	
	/**
	 * Find max price item.
	 *
	 * @param items the items
	 * @return the sc loi loa item
	 */
	private void findLowestIterPriceItem(List<ScLoiLoaItem> items) {
		Comparator<ScLoiLoaItem> comparator = new Comparator<ScLoiLoaItem>() {
			@Override
			public int compare(ScLoiLoaItem o1, ScLoiLoaItem o2) {
				return new CompareToBuilder().append(o1.getTplSpecTierQty().getFromQty(),
						o2.getTplSpecTierQty().getFromQty()).toComparison();
			}
		};
		 Collections.sort(items, comparator);
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
			/*
			 * IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
			 * ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class); UserReqDTO
			 * reqDTO = new UserReqDTO(); reqDTO.setUserId(user.getUserId());
			 * //UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			 */
			IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.PUBLICATION_APPROVER.value());

			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			getManagedBean().setApproverNames(userResDTO.getUserList());

		}
		return approverUsers;
	}

	/**
	 * To get current status for loi.
	 * 
	 * @param statusId
	 *            the status id
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

		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						getManagedBean().getBackToPreviousPage());
	}

	/**
	 * Adds the negotiation location in negotiation details.
	 */
	public void addNegotiationLocation() {
		debugLogger("addNegotiationLocation");
		ScLoiNegoLocation negoLocation = new ScLoiNegoLocation();
		List<ScLoiNegoLocation> negoLocations = getManagedBean().getScLoi()
				.getScNegoLocations();

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
		ScLoi scLoi = getManagedBean().getScLoi();
		List<ScLoiTerm> scNegoTerms = scLoi
				.getScNegoTerms();
		if (null == scNegoTerms) {
			scNegoTerms = new ArrayList<ScLoiTerm>();
		}
		ScLoiTerm negoTerm = new ScLoiTerm();
		negoTerm.setPmTerm(null);
		negoTerm.setSelected(Boolean.TRUE);
		negoTerm.setScLoi(scLoi);
		negoTerm.setModified(true);
		scNegoTerms.add(negoTerm);
		scLoi.setScNegoTerms(scNegoTerms);
	}


	/**
	 * Delete nego terms in negotiation details.
	 */
	public void deleteNegoTerm() {
		List<ScLoiTerm> negoTermsList = new ArrayList<ScLoiTerm>();
		boolean flag = true;
		List<ScLoiTerm> scLoiTerms = getManagedBean().getScLoi()
				.getScNegoTerms();
		if (scLoiTerms != null && scLoiTerms.size() > 0) {

			for (ScLoiTerm scLoiTerm : scLoiTerms) {
				if (!scLoiTerm.isSelectedTerm()) {
					negoTermsList.add(scLoiTerm);
				} else {
					flag = false;
				}
			}
		}
		if (flag) {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean().getScLoi().setScNegoTerms(negoTermsList);
	}

	/**
	 * Delete multiple loi nego term.
	 */
	public void deleteMultipleLoiNegoTerm() {
		ScLoi scLoi = getManagedBean().getScLoi();
		List<ScLoiTerm> scLoiTerms = getManagedBean()
				.getSelectedNegoTermsList();
		if (scLoiTerms != null && scLoiTerms.size() > 0) {
			deleteRowsAction(scLoi.getScNegoTerms(), scLoiTerms);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
	}

	/**
	 * Delete nego locations in negotiation details.
	 */
	public void deleteNegoLocations() {
		List<ScLoiNegoLocation> result = new ArrayList<ScLoiNegoLocation>();
		boolean flag = true;
		List<ScLoiNegoLocation> scLoiNegoLocations = getManagedBean()
				.getScLoi().getScNegoLocations();
		if (scLoiNegoLocations != null && scLoiNegoLocations.size() > 0) {

			for (ScLoiNegoLocation loiNegoLocation : scLoiNegoLocations) {
				if (!loiNegoLocation.isSelected()) {
					result.add(loiNegoLocation);
				} else {
					flag = false;
				}
			}
		}
		if (flag) {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean().getScLoi().setScNegoLocations(result);
	}

	/**
	 * Creates the loi. page navigate to loi details page.
	 * 
	 * @param scLoiLoa
	 *            the sc loi loa
	 * @return the string
	 * 
	 */
	public void createLOI(ScLoiLoa scLoiLoa) {
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			renewSessionWithPreviousValues();
		
			ScLoi loi = new ScLoi();
			loi.setScLoiLoa(scLoiLoa);
			reqDTO.setScLoi(loi);
			reqDTO.setAwardType(getManagedBean().getDocType());
			reqDTO.setRecommendDtlIdList(getManagedBean()
					.getRecommendDtlIds());
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(
					reqDTO);

			populateLOIDetails(resDTO);
			getManagedBean().setEditFlag(true);
			getManagedBean().setButtonFlag(true);
			getManagedBean().setSelectedTabIndex(0);
			FacesContext fc = FacesContext.getCurrentInstance();
			getManagedBean().setBackToPreviousPage(
					fc.getViewRoot().getViewId());

			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.LOI_PAGE);
		} finally {
		}
	}

	/**
	 * Edits the loi. page navigate to loi details page.
	 * 
	 * @param scLoi
	 *            the sc loi
	 */
	public void editLOI(ScLoi scLoi, boolean isEditFlag) {
		debugLogger("editLOI");
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			renewSessionWithPreviousValues();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setRecommendDtlIdList(null);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(reqDTO);

			populateLOIDetails(resDTO);

			if (isEditFlag) {
				getManagedBean().setButtonFlag(Boolean.TRUE);
			} else {
				getManagedBean().setButtonFlag(Boolean.FALSE);
			}

			getManagedBean().setEditFlag(Boolean.TRUE);

			if (getManagedBean().getBackToPreviousPage() == null
					|| getManagedBean().getBackToPreviousPage().isEmpty()) {
				getManagedBean()
				.setBackToPreviousPage(fc.getViewRoot().getViewId());
			}
			getManagedBean().setSelectedTabIndex(0);
			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(getManagedBean().getScQt());

		} finally {
		}

		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, QTConstants.LOI_PAGE);
	}

	/**
	 * Reaload loi.
	 * 
	 * @param scLoi
	 *            the sc loi
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

		} finally {
		}
	}

	/**
	 * Populate loi details.
	 * 
	 * @param resDTO
	 *            the res dto
	 */
	private void populateLOIDetails(AwardSupplierResDTO resDTO) {
		boolean isCalculateMaxPriceItem = Boolean.FALSE;// calculate max price items only.
		boolean isTermPriceFound = Boolean.FALSE;
		boolean isBiddingAward = Boolean.FALSE;
		ScLoi scLoi = resDTO.getScLoi();
		getManagedBean().setScLoi(scLoi);
		ScWorkFlowStatus currentStatus = scLoi.getScWorkFlowStatus();
		Long statusId = currentStatus.getPmStatus().getStatusId()
				.longValue();

		// online bidding
		if (scLoi.getRecommendBy() != null) {
			PmParameter recommendedBy = scLoi.getRecommendBy();
			if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
					.equals(recommendedBy.getParameterCode())) {
				isBiddingAward = Boolean.TRUE;
				// remove nego price for online bidding.
				removeNegoTermPrice(scLoi);
			} else if (ParameterConst.RECOMMEND_BY_AWARD
					.equals(recommendedBy.getParameterCode())) {
				// remove nego price for Direct Award
				removeNegoTermPrice(scLoi);
			}
		}
		
		// check nego term price
		if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
			isTermPriceFound = checkNegoTermPrice(scLoi);
		}
		
		if(!isBiddingAward){
			if (isTermPriceFound) {
				getManagedBean().setHideColumnFlag(Boolean.TRUE);

			} else {
				getManagedBean().setHideColumnFlag(Boolean.FALSE);
			}
		}
	
		List<ScLoiLoaItem> filteredItems  = getManagedBean().getScLoiLoaItems();
		if(null == filteredItems){
			filteredItems  = filterZoneTierOrTierPriceItems(scLoi);
			getManagedBean().setScLoiLoaItems(filteredItems);
		}

		// calc total items in award details
		if (scLoi.getScLoiLoaItems() != null && scLoi.getScLoiLoaItems().size() > 0) {
			BigDecimal offerPrice = BigDecimal.valueOf(0d);
			BigDecimal offeredPriceSubTotal = BigDecimal.valueOf(0d);
			BigDecimal offeredPriceAmount = BigDecimal.valueOf(0d);

			BigDecimal finalPrice = BigDecimal.valueOf(0d);
			BigDecimal finalPriceSubTotal = BigDecimal.valueOf(0d);
			BigDecimal finalPriceAmount = BigDecimal.valueOf(0d);

			BigDecimal afterNegoSubTotal = BigDecimal.valueOf(0d);
			BigDecimal afterNegoTotalAmount = BigDecimal.valueOf(0d);
			
			BigDecimal quantity = BigDecimal.valueOf(0d);

			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				quantity = scLoiLoaItem.getQty();
				if(null == scLoiLoaItem.getTplSpecTierQty() ){
					isCalculateMaxPriceItem = Boolean.TRUE;
				} else{

					if(filteredItems.contains(scLoiLoaItem)){
						isCalculateMaxPriceItem = Boolean.TRUE;
					} else{
						isCalculateMaxPriceItem = Boolean.FALSE;
					}
				}

				if(isCalculateMaxPriceItem){
					if (scLoiLoaItem.getScQtProposalItem() != null) {
						// offered price sub total and total
						offerPrice = scLoiLoaItem.getScQtProposalItem().getOfferPrice();
						offeredPriceSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), offerPrice, quantity);
						if(offeredPriceSubTotal != null){
							scLoiLoaItem.setOfferedPriceSubTotal(offeredPriceSubTotal);
							offeredPriceAmount = offeredPriceAmount.add(offeredPriceSubTotal);
						}

						// final price sub total and total - for bidding.
						finalPrice = scLoiLoaItem.getScQtProposalItem().getFinalPrice();
						finalPriceSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), finalPrice, quantity);
						if(finalPriceSubTotal != null){
							scLoiLoaItem.setAfterNegoSubTotal(finalPriceSubTotal);
							
							// For bid re finalization final price as bid offer price - subtotal
							scLoiLoaItem.setBidFinalPriceSubTotal(finalPriceSubTotal);
							finalPriceAmount = finalPriceAmount.add(finalPriceSubTotal);
						}

						// after nego unit price sub total and total
						afterNegoSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), scLoiLoaItem.getUnitPrice(), quantity);
						if (afterNegoSubTotal != null) {
							afterNegoTotalAmount = afterNegoTotalAmount.add(afterNegoSubTotal);
							scLoiLoaItem.setAfterNegoSubTotal(afterNegoSubTotal);

						}
					}
				}
			}
			
			scLoi.setOfferedPriceTotal(offeredPriceAmount); // offered price total
			
			// when bid re finalization final price as bid offer price
			scLoi.setBidFinalPriceTotal(finalPriceAmount); 
			
			if(isBiddingAward){
				scLoi.setLoiAmt(finalPriceAmount); // unit price total
				scLoi.setAfterNegoTotal(finalPriceAmount); // after nego total
			} else{
				scLoi.setLoiAmt(afterNegoTotalAmount); // unit price total
				scLoi.setAfterNegoTotal(afterNegoTotalAmount); // after nego total
			}

			scLoi.setScLoiLoaItems(scLoiLoaItems);
		}
		
		checkReFinalizationBidding(resDTO.getScQtFinalization(), resDTO.getScQt());
		
		// branch list
		if (resDTO.getSmSupplierBranchs() != null
				&& resDTO.getSmSupplierBranchs().size() > 0) {
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
		}

		// Supplier Branch
		getManagedBean().setBranchName(scLoi.getSupplierBranch());
		getManagedBean()
				.setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());
		getManagedBean().setScQt(resDTO.getScQt());

		if (scLoi.isBrandNew()) {
			setUserDetails(scLoi);
			handleBranchDetailsChanged();
		}

		// Issue & Acknowledge Date
		scLoi.setIssueDate(DateUtils.getCurrentDateWithoutTime());
		Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOI_RESPONSE_PERIOD);
		scLoi.setAckDueDate(DateUtils.addDays(scLoi.getIssueDate(), loaRespPeriod));

		// approvers
		ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();
		PmUser approverUser = null;
		if (scLoiApproval != null) {
			approverUser = scLoiApproval.getApprover();

			if (!QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoi
					.getScWorkFlowStatus().getPmStatus().getStatusId()
					.longValue())) {
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

		getManagedBean().setScLoi(resDTO.getScLoi());

		// APPPROVER HISTORY
		getManagedBean().getLois().add(resDTO.getScLoi());

		// remarks
		getManagedBean().getScLoi().setScLoiRemarks(
				initializeRemarks(getManagedBean().getScLoi(), 
						currentStatus,
						getManagedBean().getScLoi().getScLoiRemarks(),
						ScLoiRemark.class));

		// status
		getCurrentStatus(currentStatus.getPmStatus()
				.getStatusId());

		// Contract Signer
		PmUser contractSignerUser = scLoi.getContractSigner();
		if (contractSignerUser == null) {
			contractSignerUser = new PmUser();
		}

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());
		if (scLoi.getLoiAmt() != null) {
			userReqDTO.setApprovalLimit(scLoi.getLoiAmt());
		}
		userReqDTO.setOrgProfileId(scLoi.getScLoiLoa().getPmOrgProfile()
				.getOrgProfileId());
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

		getManagedBean().setContractSignerUser(contractSignerUser);
		getManagedBean().setContractSigners(userResDTO.getUserList());

		// TODO
		scLoi.setFileExtnNo("001");

		/*
		 * if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(scLoi
		 * .getScWorkFlowStatus().getPmStatus().getStatusId())) {
		 * 
		 * if (getManagedBean().getScQt() != null) { if
		 * (("P").equals(getManagedBean().getScQt().getScheduleType())) { //
		 * progressive payment. formProgressivePayment(scLoi); } else if
		 * (("F").equals(getManagedBean().getScQt() .getScheduleType())) { //
		 * Fulfilment Schedule formFulfilmentSchedule(scLoi); } } }
		 */
	}

	
	/**
	 * Check re finalization bidding.
	 *
	 * @param scQtFinalization the sc qt finalization
	 * @param scQt the sc qt
	 */
	private void checkReFinalizationBidding(ScQtFinalization scQtFinalization, ScQt scQt){
		if(scQtFinalization != null && scQt != null){
			getManagedBean().setScQt(scQt);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			boolean isScQtRefinalized = getQtAwardSO().isQtRefinalized(reqDTO).isRefinalized();

			if(isScQtRefinalized){
				PmParameter awardType = scQtFinalization.getFinalizationMethod();
				boolean isBidding = awardType.getParameterCode().equals(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING);
				if(isBidding) {
					getManagedBean().setRefinalizedBidding(Boolean.TRUE);
				} else{
					getManagedBean().setRefinalizedBidding(Boolean.FALSE);
				}	
			}
		}
	}

	/**
	 * Handle branch details changed. shows the supplier address details.
	 */
	public void handleBranchDetailsChanged() {
		debugLogger("handleBranchDetailsChanged");
		ScLoi scLoi = this.getManagedBean().getScLoi();
		String selectedSupplierBranchName = this.getManagedBean()
				.getBranchName();
		   String supplierBranchAsHQ = QTConstants.SUPPLIER_INFO_HQ;
		if (selectedSupplierBranchName == null
				|| supplierBranchAsHQ.equals(selectedSupplierBranchName)) {
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

						scLoi.setSupplierBranch(supplierBranchAsHQ);
						
						ScAddress supplierAddress = createOrUpdateScAddress(
														scLoi.getScSupplierAddress(),
														smCompanyAddress.getSmAddress(), 
														supplierHQPersonnel, smCompanyBasic);
						scLoi.setScSupplierAddress(supplierAddress);

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
					
					SmCompanyBasic smCompanyBasic = null;
					SmPersonnel smPersonnel = supplierBranch.getSmPersonnel();
					if (smPersonnel != null) {						
						if (smPersonnel.getSmAppl() != null) {
							smCompanyBasic = smPersonnel.getSmAppl()
												.getSmCompanyBasic();
						}
					}					
					ScAddress supplierAddress = createOrUpdateScAddress(
													scLoi.getScSupplierAddress(),
													supplierBranch.getSmAddress(), 
													smPersonnel, smCompanyBasic);
					scLoi.setScSupplierAddress(supplierAddress);

					break;
				}
			}
		}

	}

	/**
	 * save the LOI details. save - save data into database and initiate task
	 * for LOI details. page navigate to loi details page.
	 */
	public void saveAction() {
		debugLogger("saveAction");
		try {
			ScLoi scLoi = this.getManagedBean().getScLoi();
			ScQt scQt=this.getManagedBean().getScQt();
			String outcome = BpmOutcomeConst.BPM_SAVE;
			
			parseLOIDetails(scLoi, outcome);
			
			//pending loiloas
			List<ScLoiLoa> pendingloiloas  = getPendingLoiLoas(scLoi, getManagedBean().getRecSupplierList());

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setOutcome(outcome);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setPendingloiloas(pendingloiloas);
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOIDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoi = resDTO.getScLoi();
				reloadLOI(scLoi);
				this.getManagedBean().setScQt(scQt);
				successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES, 
						ParameterConst.DOCTYPE_LETTER_OF_INTENT, scLoi.getLoiNo(), Boolean.FALSE);
			}

		} finally {
		}
	}
	
	/**
	 * Gets the pending loi loas.
	 *
	 * @param scLoi the sc loi
	 * @param pendingloiloas the pendingloiloas
	 * @return the pending loi loas
	 */
	private List<ScLoiLoa> getPendingLoiLoas(ScLoi scLoi, List<ScLoiLoa> pendingloiloas){
		List<ScLoiLoa> results = new ArrayList<ScLoiLoa>();
		for (ScLoiLoa scLoiLoa : pendingloiloas) {
			if(scLoi != null && null != scLoi.getScLoiLoa()){
				if(!scLoiLoa.getLoiLoaId().equals(scLoi.getScLoiLoa().getLoiLoaId())){
					results.add(scLoiLoa);
				}
			}
		}
		return results;
	}

	/**
	 * submit LOI list details. submit - save data into database and BPM process
	 * will route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public String submitLoiListAction() {
		debugLogger("submitLoiListAction");

		try {
			ScQt scQt = getManagedBean().getScQtFinalization().getScQt();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setDocType(getManagedBean().getDocType());
			reqDTO.setLoiLoaList(getManagedBean().getRecSupplierList());
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);

			AwardSupplierResDTO resDTO = getQtAwardSO().completeLoiListBPMTask(
					reqDTO);

			if (resDTO.isSuccessful()) {
				renewSesionObject();
				this.getManagedBean().setScQt(scQt);
				successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION, scQt.getQtNo(), Boolean.TRUE);
			}

		} finally {
			// iqtAwardSO = null;
		}

		return null;
	}

	/**
	 * save LOI list details. save - save data into database.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string
	 */
	public String savetLoiListAction() {
		debugLogger("savetLoiListAction");
		boolean isLoiLoasUpdate = Boolean.FALSE;
		AwardSupplierResDTO resDTO = new AwardSupplierResDTO();
		try {
			ScQtFinalization scQtFinalization = getManagedBean()
					.getScQtFinalization();
			String docType = getManagedBean().getDocType();
			Long sourceId = getManagedBean().getSourceId();
			List<String> recommendDtlIds = getManagedBean()
					.getRecommendDtlIds();
			Object payLoadData = getManagedBean().getPayLoad();
			boolean isLoiRequired  = getManagedBean().isLoiRequired();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			List<ScLoiLoa> recSupplierList =getManagedBean().getRecSupplierList();
			isLoiLoasUpdate = updatePendingLoiLoas(recSupplierList, docType);
			if(isLoiLoasUpdate){
				reqDTO.setLoiLoaList(getManagedBean().getRecSupplierList());
				resDTO = getQtAwardSO().saveLoiLoaList(reqDTO);
			} else{
				resDTO.setSuccessful(Boolean.TRUE);
			}

			if (resDTO.isSuccessful()) {
				if(isLoiLoasUpdate){
					renewSesionObject();
					getManagedBean().setScQtFinalization(scQtFinalization);
					getManagedBean().setDocType(docType);
					getManagedBean().setRecSupplierList(resDTO.getLoiLoas());
					getManagedBean().setSourceId(sourceId);
					getManagedBean().setRecommendDtlIds(recommendDtlIds);
					getManagedBean().setPayLoad(payLoadData);
					getManagedBean().setLoiRequired(isLoiRequired);
					reloadLOIList(sourceId);
				}
				if(null!=scQtFinalization){
				this.getManagedBean().setScQt(scQtFinalization.getScQt());
				}
				successMessage(MessageConst.INFO_700025, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION, scQtFinalization.getScQt().getQtNo(), Boolean.FALSE);
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.LOI_LIST_PAGE);
			}

		} finally {
		}

		return null;
	}
	
	
	/**
	 * Reload loi list.
	 *
	 * @param qtId the qt id
	 */
	private void reloadLOIList(Long qtId) {
		debugLogger("reloadLOIList");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setRecommendDtlIdList(getManagedBean().getRecommendDtlIds());
			AwardSupplierResDTO resDTO = getQtAwardSO().retrievePendingLoiList(
					reqDTO);
			// rec supplier in loi list
			List<ScLoiLoa> recSupplierList = resDTO.getLoiLoas();
			getManagedBean().setRecSupplierList(recSupplierList);
			//disableIsLoiRequired(recSupplierList);
		} finally{
		}
	}

	/**
	 * Loi required listener.
	 * 
	 * @param scLoiLoa
	 *            the sc loi loa
	 */
	public void loiRequiredListener(ScLoiLoa scLoiLoa) {
		debugLogger("loiRequiredListener");
		boolean isSubmitButton = Boolean.TRUE;

		int index = getManagedBean().getRecSupplierList().indexOf(scLoiLoa);
		getManagedBean().getRecSupplierList().set(index, scLoiLoa);
		List<ScLoiLoa> pendingLois  = getManagedBean().getRecSupplierList();

		for (ScLoiLoa loiLoa : pendingLois) {

			if (null != loiLoa.getLoiRequired()) {
				if(loiLoa.getLoiRequired() && loiLoa.isLoiLoaActive()){
					isSubmitButton = Boolean.FALSE;
					break;
				}

			} else{
				isSubmitButton = Boolean.FALSE;
				break;
			}
		}

		if (isSubmitButton) {
			getManagedBean().setLoiRequired(Boolean.TRUE);
		} else {
			getManagedBean().setLoiRequired(Boolean.FALSE);
		}
	}

	/**
	 * submit LOI details. submit - save data into database and BPM process will
	 * route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public String submitAction() {
		debugLogger("submitAction");
		try {
			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoad();

			ScLoi scLoi = this.getManagedBean().getScLoi();
			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScQt scQt=this.getManagedBean().getScQt();
			parseLOIDetails(scLoi, outcome);
			
			scLoi.setScLoiWorkFlowStatuses(null);
			// To avoid insert workflow status when direct submit without save.
			if (scLoi.isBrandNew()) {
				List<ScLoiRemark> remarks = scLoi.getScLoiRemarks();
				if (CollectionUtils.isNotEmpty(remarks)) {
					for (ScLoiRemark remark : remarks) {
						remark.setScWorkFlowStatus(null);
					}
				}	
			}
			
			//pending loiloas
			List<ScLoiLoa> pendingloiloas  = getPendingLoiLoas(scLoi, getManagedBean().getRecSupplierList());

			scLoi.setIssueDate(DateUtils.getCurrentDateWithoutTime());
			Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOI_RESPONSE_PERIOD);
			scLoi.setAckDueDate(DateUtils.addDays(scLoi.getIssueDate(), loaRespPeriod));

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setOutcome(outcome);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setPayLoad(awardDataType);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setPendingloiloas(pendingloiloas);
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOIDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoi = resDTO.getScLoi();
				renewSesionObject();
				this.getManagedBean().setScQt(scQt);
				successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_INTENT, scLoi.getLoiNo(), Boolean.TRUE);
			}

		} finally {
			// qtAwardSO = null;
		}
		return null;
	}

	/**
	 * Update loi list bpm task action. update bpm - save data into database and
	 * BPM process will route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public void updateLOIListBpmTaskAction(String outcome) {
		debugLogger("updateLOIListBpmTaskAction");
		try {

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());

			AwardSupplierResDTO resDTO = getQtAwardSO().updateBPMAction(reqDTO);

			if (resDTO.isSuccessful()) {
				String qtNo = getManagedBean().getScQtFinalization().getScQt()
						.getQtNo();

				renewSesionObject();

				successMessage(MessageConst.INFO_700005,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT, qtNo, Boolean.TRUE);
			}
		} finally {
			// iqtAwardSO = null;
		}
	}

	/**
	 * Update bpm task. update bpm - save data into database and BPM process
	 * will route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @param digiSign
	 *            the digi sign
	 * @return AwardSupplierResDTO
	 */
	private AwardSupplierResDTO updateBPMTask(String outcome,
			PmDigiSign digiSign) {
		debugLogger("updateBPMTask");
		AwardSupplierResDTO awardSupplierResDTO = null;
		try {

			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoad();

			ScLoi scLoi = this.getManagedBean().getScLoi();
			Long statusId = scLoi.getScWorkFlowStatus().getPmStatus()
					.getStatusId().longValue();

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
			awardSupplierReqDTO.setPmUser(new FacesScopes()
					.getUserSessionBean());
			awardSupplierReqDTO.setRoleCode(getUserRoleCode());
			awardSupplierReqDTO.setUserLocale(new FacesScopes().getUserLocale());

			// spki
			if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
				String originalText = prepareOriginalText(scLoi.getScLoiLoa()
						.getSmSupplier());
				byte[] signedData = getManagedBean().getSpkiBackingBean()
						.spkiSignTransaction(originalText);
				digiSign.setSpkiSignedData(signedData);
				if (null != digiSign) {
					updateSpkiDigiSignData(digiSign,
							ParameterConst.DOCTYPE_LETTER_OF_INTENT,
							scLoi.getLoiId(), scLoi.getLoiNo(), originalText);
				}
				
				RequestContext.getCurrentInstance().execute(
						"blockWidget.hide();");
			} else {
				// gpki
				if (null != digiSign) {
					updatePmDigiSignData(digiSign,
							ParameterConst.DOCTYPE_LETTER_OF_INTENT,
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
					if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700066;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					}
				} else if (QTStatusConst.LOI_PENDING_NEGOTIATION
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700001;
				}

				successMessage(messageCode, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT,
						scLoi.getLoiNo(), Boolean.TRUE);
			}
		} finally {
			// iqtAwardSO = null;
		}
		return awardSupplierResDTO;
	}

	/**
	 * Update bpm task action. update bpm - save data into database and BPM
	 * process will route to the next respective user. Authenticating gpki
	 * before update bpm.
	 * 
	 * @param outcome
	 *            the outcome
	 */
	public void updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");
		this.getManagedBean().setAction(outcome);
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
			if (isDigiSigningRequired(RoleCode.RN_APPROVER.value())
					|| isDigiSigningRequired(RoleCode.PUBLICATION_APPROVER
							.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
		} else if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
			getManagedBean().getSpkiBackingBean().getChallengeQuestions();
			RequestContext.getCurrentInstance().execute("spki_dlg.show();");
			RequestContext.getCurrentInstance().execute("blockWidget.hide();");
		} else {
			updateBPMTask(outcome, null);
		}
	}

	/**
	 * Prepare original text.
	 * 
	 * @param smSupplier
	 *            the sm supplier
	 * @return the string
	 */
	private String prepareOriginalText(SmSupplier smSupplier) {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		String countryCode = "";
		String orginalText = "";

		if (smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null
				&& smSupplier.getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().size() > 0) {
			List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl()
					.getSmCompanyBasic().getSmCompanyAddresses();

			for (SmCompanyAddress compaAddress : companyAddress) {
				if (compaAddress.getAddressType().equalsIgnoreCase(
						AddressType.CONTACT_ADDRESS.value())) {
					countryCode = compaAddress.getSmAddress().getPmCountry()
							.getCountryCode();
					break;
				}
			}
		}
		sb.append(getManagedBean().getScLoi().getLoiNo()).append("|")
				.append(facesScopes.getUserSessionBean().getIdentificationNo())
				.append("|").append(smSupplier.getRegNo()).append("|")
				.append(countryCode);
		orginalText = sb.toString();
		return orginalText;
	}

	/**
	 * The method used to verify the login user is supplier.
	 * 
	 * @return true, if login user is supplier false, if login user is not
	 *         supplier
	 */
	public boolean isSupplierRole() {
		return (getFacesScopes().hasRoleUser(
				RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(
						RoleCode.BASIC_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(
						RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(
						RoleCode.MOF_SUPPLIER_USER.value()) || getFacesScopes()
				.hasRoleUser(RoleCode.GOVT_SELLER.value()));
	}

	/**
	 * This method used to retrieve login user information.
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * save multiple loi action. save - save data into database and initiate
	 * task for LOI details.
	 * 
	 * page navigate to acknowledge page.
	 */
	public void saveMultipleLoiAction() {
		debugLogger("saveMultipleLoiAction");
		
		try {
			ScQtFinalization scQtFinalization = getManagedBean()
					.getScQtFinalization();

			AwardSupplierReqDTO reqDTO = parseMultipleLoiDtls(false);

			AwardSupplierResDTO resDTO = getQtAwardSO().saveMultipleLOIDtl(
					reqDTO);

			if (resDTO.isSuccessful()) {
				renewSessionWithPreviousValues();

				retrieveMultipleLoiDtls(scQtFinalization.getScQt().getQtId(),
						getManagedBean().getCommitteeType(), getManagedBean()
								.getRecommendDtlIds());
				successMessage(MessageConst.INFO_700004, ParameterTypeConst.FINALIZATION_RECOMMEND_BY, ParameterConst.RECOMMEND_BY_MULTIPLE_LOI, 
						scQtFinalization.getScQt().getQtNo(), Boolean.FALSE);
			}

		} finally {
			// qtAwardSO = null;
		}
	}

	/**
	 * Update multi loi bpm task action. update bpm - save data into database
	 * and BPM process will route to the next respective user. Authenticating
	 * gpki before update bpm.
	 * 
	 * @param outcome
	 *            the outcome
	 */
	public void updateMultiLoiBpmTaskAction(String outcome) {
		debugLogger("updateMultiLoiBpmTaskAction");
		this.getManagedBean().setAction(outcome);
		String committeeType = getManagedBean().getCommitteeType();
		CommitteResDTO committeeResDTO = getCommitteMemberByUserId();
		ScCommitteeMember committeeMember = committeeResDTO.getCommMember();
		
		if (StringUtils.isNotBlank(committeeType) && 
				ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equalsIgnoreCase(committeeMember.getMemberRole().getParameterCode()) &&
				isDigiSigningRequired(committeeResDTO.getRoleCode(),
						this.getManagedBean().getFcActivityList())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else {
			signOff();
			updateMultiLoiBpmTask(outcome, null);
		}
	}
	
	private CommitteResDTO getCommitteMemberByUserId() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		String committeeType = this.getManagedBean()
				.getCommitteeType();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setUser(curLogedInUser);
		committeReqDTO.setCommitteeType(committeeType);
		
		return committeSO.retrieveCommitteMemberByUserId(
				committeReqDTO);
	}

	/**
	 * Update multi loi bpm task.
	 * 
	 * @param outcome
	 *            the outcome
	 * @param digiSign
	 *            the digi sign
	 */
	private void updateMultiLoiBpmTask(String outcome, PmDigiSign digiSign) {
		debugLogger("updateMultiLoiBpmTask");
		
		try {
			ScQtFinalization scQtFinalization = getManagedBean()
					.getScQtFinalization();
			SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) getManagedBean()
					.getPayLoad();

			AwardSupplierReqDTO reqDTO = parseMultipleLoiDtls(true);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setOutcome(outcome);
			if (null != digiSign) {
				signOff();
				updatePmDigiSignData(digiSign,
						ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION, scQtFinalization
								.getScQt().getQtId(), scQtFinalization
								.getScQt().getQtNo());
			}

			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setPayLoad(evaluationDataType);
			reqDTO.setDigiSignData(digiSign);
			
			reqDTO.setStatusId(getManagedBean().getCurrentStatusId());
			AwardSupplierResDTO resDTO = getQtAwardSO().saveMultipleLOIDtl(
					reqDTO);

			if (resDTO.isSuccessful()) {
				super.renewSesionObject();

				successMessage(MessageConst.INFO_700005,
						ParameterTypeConst.FINALIZATION_RECOMMEND_BY,
						ParameterConst.RECOMMEND_BY_MULTIPLE_LOI, scQtFinalization
								.getScQt().getQtNo(), Boolean.TRUE);
			}

		} finally {
			// qtAwardSO = null;
		}
	}
	
	/**
	 * Action performed when committee members endorsed the Supplier
	 */
	public void signOff() {
		if (StringUtils.isNotBlank(
				getManagedBean().getCommitteeType())) {
			for (ScQtCommitteeActivity scQtCommitteeActivity 
								: getManagedBean().getFcActivityList()) {
				if (scQtCommitteeActivity.getScCommitteeMember().getPmUser().getUserId()
						.equals(getFacesScopes().getUserSessionBean().getUserId())) {
					scQtCommitteeActivity.setMultiLoiSignedOff(true);
					break;
				}
			}
		}
	}

	/**
	 * Parses the multiple loi dtls. prepare mulitple loi details to save or
	 * submit into database.
	 * 
	 * @return the award supplier req dto
	 */
	private AwardSupplierReqDTO parseMultipleLoiDtls(boolean isSubmit) {
		Long statusId  = getManagedBean().getCurrentStatusId();
		if (QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION
				.equals(statusId)) {

			List<TplSpecZoneDtl> zoneDeDtls = getManagedBean().getMultiLOIZoneDetails();
			if (getManagedBean().isItemized()) {
				if (getManagedBean().getScQt().isZonal() && 
						CollectionUtils.isNotEmpty(zoneDeDtls)) {
					Map<Long,List<ScQtRecommend>> scQtRecommendsByZone 
										= getManagedBean().getScQtRecommendsByZone();
					for (TplSpecZoneDtl zone : zoneDeDtls) {
						validateAwardOfSuppliersByItem(isSubmit, zone,
								scQtRecommendsByZone.get(zone.getSpecZoneId()));
					}
				} else {
					validateAwardOfSuppliersByItem(isSubmit, null,
							getManagedBean().getRecommendList());
				}
			} else {
				if (getManagedBean().getScQt().isZonal() && 
							CollectionUtils.isNotEmpty(zoneDeDtls)) {
					Map<Long,List<ScQtRecommend>> scQtRecommendsByZone 
										= getManagedBean().getScQtRecommendsByZone();
					for (TplSpecZoneDtl zone : zoneDeDtls) {
						validateAwardOfSuppliersByPKG(isSubmit, zone,
								scQtRecommendsByZone.get(zone.getSpecZoneId())
								.get(0));
					}
				} else {
					if (this.getManagedBean().getSelectedSupplier() == null) {
						throw new EpException(MessageConst.ERROR_760293);
					}
				}				
			}
			
			if (isSubmit) {
				ScLoi scLoi = this.getManagedBean().getScLoi();
				if (CollectionUtils.isNotEmpty(
							scLoi.getScLoiReports())) {
					for (ScLoiReport report 
								: scLoi.getScLoiReports()) {
						if (report.getScLoiReportAttachment() == null && 
								ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equals(report.getDefinationType().getParameterCode())) {
							tabMenuListener(3);
							throw new EpException(MessageConst.ERROR_760415, 
												  new String[] { report.getTitle() });
						}
					}
				}
			}
		}
		
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

		reqDTO.setScLoi(getManagedBean().getRecommendedLoi());
		reqDTO.setScQtFinalization(scQtFinalization);
		reqDTO.setScLois(getManagedBean().getLois());
		reqDTO.setScLoiReports(getManagedBean()
								.getRecommendedLoi().getScLoiReports());
		reqDTO.setDeletedScLoiReports(getManagedBean()
								.getDeletedScLoiReports());

		// Save EC/TEC/FEC activities details.
		List<ScQtCommitteeActivity> ecActivityList = new ArrayList<ScQtCommitteeActivity>();
		if (getManagedBean().getEcActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean()
					.getEcActivityList()) {
				ecActivityList.add(committeeActivity);
			}
		}
		if (getManagedBean().getTecActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean()
					.getTecActivityList()) {
				ecActivityList.add(committeeActivity);
			}
		}
		if (getManagedBean().getFecActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean()
					.getFecActivityList()) {
				ecActivityList.add(committeeActivity);
			}
		}
		reqDTO.setEcActivityList(ecActivityList);

//		boolean itemized = this.getManagedBean().isItemized();
//		ScQtEvaluationResultDtl awardedSupplier = this.getManagedBean()
//				.getSelectedSupplier();
//		if (!itemized && awardedSupplier != null) {
//			awardedSupplier.setAwareded(true);
//			reqDTO.setAwardedSupplier(awardedSupplier);
//		}

		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
		if (scQtCommittee != null) {
			scQtCommittee.setQtCommitteRemarks(finalizeRemarks(scQtCommittee
					.getQtCommitteRemarks()));
			reqDTO.setScQtCommittee(scQtCommittee);
		}
		
		List<ScQtRecommend> scQtRecommends = this.getManagedBean().getRecommendList();
		if (getManagedBean().isZonal()) {
			scQtRecommends = new ArrayList<ScQtRecommend>();
			Map<Long,List<ScQtRecommend>> qtRecommendsByZone 
						= getManagedBean().getScQtRecommendsByZone();
			for (Long specZoneId : qtRecommendsByZone.keySet()) {
				scQtRecommends.addAll(qtRecommendsByZone.get(specZoneId));
			}
		}
		reqDTO.setScQtRecommendList(scQtRecommends);
		reqDTO.setItemized(this.getManagedBean().isItemized());
		reqDTO.setDeletedMeetingAttendanceList(
				getManagedBean().getDeletedMeetingAttendanceList());

		return reqDTO;
	}
	
	private void validateAwardOfSuppliersByPKG(boolean isSubmit,
			TplSpecZoneDtl specZone, ScQtRecommend recommendedItem) {
		if (CollectionUtils.isNotEmpty(
				recommendedItem.getRecommendDtls()) &&
					isSubmit) {
			int rejectedSupplierCount = 0;
			int recommendedSupplierCount = 0;
			for (ScQtRecommendDtl recommendDtl 
						: recommendedItem.getRecommendDtls()) {
				if (BooleanUtils.isTrue(
						recommendDtl.getLoiAwarded()) && 
							recommendDtl.isRecordStatus()) {
					++recommendedSupplierCount;
					break;
				} else if (BooleanUtils.isFalse(
								recommendDtl.getLoiAwarded()) || 
						   (recommendDtl.getLoaAwarded() != null && 
								   !recommendDtl.getLoaAwarded()) ) {
					++rejectedSupplierCount;
				}
			}
			if (rejectedSupplierCount != 
					recommendedItem.getRecommendDtls().size() && 
					recommendedSupplierCount == 0) {
				this.getManagedBean().setSelectedTabIndex(2);
				if (specZone != null) {
					this.getManagedBean().setSelectedZone(specZone);
					this.getManagedBean().setZoneActiveIndex(
							specZone.getZoneTabIndex());
				}
				
				throw new EpException(MessageConst.ERROR_760293);
			}
		}
	}	
	
	private void validateAwardOfSuppliersByItem(boolean isSubmit, TplSpecZoneDtl specZone,
										  List<ScQtRecommend> recommendedItemList) {
		for (ScQtRecommend scQtRecommend 
								: recommendedItemList) {
			if (scQtRecommend.getActiveSupplierCount() > 1) {
				validateAwardQuantity(scQtRecommend);
			}
			if (CollectionUtils.isNotEmpty(
					scQtRecommend.getRecommendDtls()) && 
						isSubmit) {
				int rejectedSupplierCount = 0;
				int recommendedSupplierCount = 0;
				for (ScQtRecommendDtl recommendDtl 
							: scQtRecommend.getRecommendDtls()) {
					if (BooleanUtils.isTrue(
							recommendDtl.getLoiAwarded()) &&
								recommendDtl.isRecordStatus()) {
						++recommendedSupplierCount;
					} else if (BooleanUtils.isFalse(
									recommendDtl.getLoiAwarded()) || 
							   (recommendDtl.getLoaAwarded() != null && 
									   !recommendDtl.getLoaAwarded())) {
						++rejectedSupplierCount;
					}
				}
				if (rejectedSupplierCount != 
						scQtRecommend.getRecommendDtls().size() &&
							recommendedSupplierCount == 0) {
					this.getManagedBean().setSelectedTabIndex(2);
					if (specZone != null) {
						this.getManagedBean().setSelectedZone(specZone);
						this.getManagedBean().setZoneActiveIndex(
											specZone.getZoneTabIndex());
					}
					
					if (!this.getManagedBean().isItemized()) {
						throw new EpException(MessageConst.ERROR_760317);
					} else {
						throw new EpException(MessageConst.ERROR_760318, 
								new String[] {scQtRecommend.getRequestItem().getItemName()});
					}
				}
			}
		}		
	}
	
	private void validateAwardQuantity(ScQtRecommend scQtRecommend) {
		BigDecimal totalQuantity = scQtRecommend.getOrderQty();
		BigDecimal tempQuantity = new BigDecimal(0.0);
		for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
			if (BooleanUtils.isTrue(
					recommendDtl.getLoiAwarded()) && 
					recommendDtl.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_ACTIVE)) {
				BigDecimal quantityAwarded = recommendDtl.getAwardQty();
				if (quantityAwarded == null
						|| quantityAwarded.compareTo(totalQuantity) >= 0) {
					this.getManagedBean().setSelectedTabIndex(2);
					throw new EpException(MessageConst.ERROR_760214);
				}
				tempQuantity = tempQuantity.add(quantityAwarded);
			}
		}
		if (totalQuantity.compareTo(tempQuantity) != 0) {
			this.getManagedBean().setSelectedTabIndex(2);
			throw new EpException(MessageConst.ERROR_760214);
		}		
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Gets the loi letter stream content. to preview loi content as a pdf and
	 * can download.
	 * 
	 * @return the loi letter stream content
	 * @throws Exception
	 *             the exception
	 */
	public StreamedContent getLoiLetterStreamContent() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoi().getLoiNo());
		generateLoiLetterFromVelocityToPdf(getManagedBean().getScLoi(),
				(ScAttachment) pbAttachment);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				pbAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return pbStreamContent;
	}

	/**
	 * Preview letter. to preview loi cover letter as a pdf and can download.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void previewLetter() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoi().getLoiNo());
		generateLoiLetterFromVelocityToPdf(getManagedBean().getScLoi(),
				(ScAttachment) pbAttachment);
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
			List<ScQtMeetingAttendance> meetingAttendanceList = scQtCommittee
					.getMeetingAttendances();

			if (null == meetingAttendanceList) {
				meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
				scQtCommittee.setMeetingAttendances(meetingAttendanceList);
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
		List<ScQtMeetingAttendance> selectedMeetingAttendances = getManagedBean()
				.getSelectedMeetingAttList();
		if (CollectionUtils.isNotEmpty(
				selectedMeetingAttendances)) {
			deleteRowsAction(scQtCommittee.getMeetingAttendances(),
					selectedMeetingAttendances);
			if (getManagedBean().getDeletedMeetingAttendanceList() == null) {
				getManagedBean().setDeletedMeetingAttendanceList(
							new ArrayList<ScQtMeetingAttendance>());
			}
			for (ScQtMeetingAttendance meetingAttd
							: getManagedBean().getSelectedMeetingAttList()) {
				if (!meetingAttd.isBrandNew()) {
					meetingAttd.setDeleted(true);
					getManagedBean().getDeletedMeetingAttendanceList()
									.add(meetingAttd);
				}
			}
		} else {
			throw new EpException("Please select atleast one record");
		}
		getManagedBean()
		.setSelectedMeetingAttList(null);
	}

	/**
	 * Generate loi letter from velocity to pdf.
	 * 
	 * @param scLoi
	 *            the sc loi
	 * @param attachment
	 *            the attachment
	 * @throws Exception
	 *             the exception
	 */
	private void generateLoiLetterFromVelocityToPdf(ScLoi scLoi,
			ScAttachment attachment) throws Exception {
		attachment.setFileName("Loi Letter.pdf");
		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATETIME));
		DecimalFormat numberDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		String userName = fs.getUserSessionBean().getUserName();
		context.put("hostUrl", hostUrl);
		context.put("userName", userName);

		Date newDate = new Date();
		String date = "";
		if (newDate != null) {
			date = dateSDF.format(newDate);
		}
		context.put("date", date);

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
		if (scLoi.getScSupplierAddress() != null) {
			supplierAddress = getFullAddress(scLoi.getScSupplierAddress());
			supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
			supplierAddress = supplierAddress.replaceAll("&", "&#38;");
		} else {
			supplierAddress = "&lt;&lt;Address&gt;&gt;";
		}
		context.put("supplierAddress", supplierAddress);

		String title = scLoiLoa.getTitle();
		title = title.replaceAll("&", "&#38;");
		context.put("title", title);
		
		boolean isZonal = Boolean.FALSE;
		List<ScLoiLoaItem> loiloaItems = new ArrayList<ScLoiLoaItem>();
		if (scLoi.getScLoiLoaItems() != null
				&& scLoi.getScLoiLoaItems().size() > 0) {
			loiloaItems = scLoi.getScLoiLoaItems();

			for (ScLoiLoaItem scLoiLoaItem : loiloaItems) {
				// zonal
				if(scLoiLoaItem.getTplSpecZoneDtl() != null){
					isZonal = Boolean.TRUE;
				}
				
				if (scLoiLoaItem.getScRequestItem().getItemName() != null
						&& !scLoiLoaItem.getScRequestItem().getItemName()
								.isEmpty()) {
					String itemName = scLoiLoaItem.getScRequestItem()
							.getItemName();
					itemName = itemName.replaceAll(" & ", "&#38;");
					scLoiLoaItem.getScRequestItem().setItemName(itemName);
				}

				if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion() != null
						&& scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.getTplSpecMaster() != null) {
					BigDecimal bigDecimal = BigDecimal.valueOf(0d);
					if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClUomPerDay() == null) {
						scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.setClUomPerDay(bigDecimal);
					}

					if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClUomFrequency() == null) {
						scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.setClUomFrequency(bigDecimal);
					}

					if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClUomPerMonth() == null) {
						scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.setClUomPerMonth(bigDecimal);
					}

					if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClDurationMonth() == null) {
						scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.setClDurationMonth(bigDecimal);
					}
					if (scLoiLoaItem.getSubTotal() == null) {
						scLoiLoaItem.setSubTotal(bigDecimal);
					}
				}
			}
			context.put("scLoiLoaItems", loiloaItems);
		}
		
		context.put("isZonal", isZonal);
		context.put("scLoi", scLoi);
		if (scLoi.getScNegoLocations() != null
				&& scLoi.getScNegoLocations().size() > 0) {
			context.put("scNegoLocations", scLoi.getScNegoLocations());
		}

		List<ScLoiTerm> selectedNegoTerms = new ArrayList<ScLoiTerm>();
		if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			for (ScLoiTerm scLoiTerm : scLoi.getScNegoTerms()) {
				if (scLoiTerm.isSelected()) {
					selectedNegoTerms.add(scLoiTerm);
				}
			}
		}

		String Keputusan = "&lt;&lt;Keputusan&gt;&gt;";
		if (selectedNegoTerms != null && selectedNegoTerms.size() > 0) {
			context.put("scNegoTerms", selectedNegoTerms);
		} else if (scLoi.getScNegoTerms() != null
				&& scLoi.getScNegoTerms().size() > 0) {
			context.put("scNegoTerms", scLoi.getScNegoTerms());
			context.put("Keputusan", Keputusan);
		}

		String ackDueDays = "7";
		if (scLoi.getIssueDate() != null && scLoi.getAckDueDate() != null) {
			ackDueDays = DateUtils.getDaysDifference(scLoi.getIssueDate(),
					scLoi.getAckDueDate()) + "";
		}
		context.put("ackDueDays", ackDueDays);

		String loiSigner = "";
		if (getManagedBean().getContractSignerUser() != null) {
			if (null != getManagedBean().getContractSignerUser().getUserName() && !getManagedBean().getContractSignerUser().getUserName().isEmpty()) {
				loiSigner =  getManagedBean().getContractSignerUser().getUserName();
			} 
		}
		context.put("loiSigner", loiSigner);
		
		String loiSignerDesignation = "";
		if(null != scLoi.getLoiSignerDesig() && !scLoi.getLoiSignerDesig().isEmpty()){
			loiSignerDesignation = scLoi.getLoiSignerDesig();
		}
		context.put("loiSignerDesignation", loiSignerDesignation);
		
		context.put("isReFinalizationBidding", true);
		
		// online bidding
		boolean isBiddingAward = Boolean.FALSE;
		if (scLoi.getRecommendBy() != null) {
			PmParameter recommendedBy = scLoi.getRecommendBy();
			if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
					.equals(recommendedBy.getParameterCode())) {
				isBiddingAward = Boolean.TRUE;
			}
		}
		context.put("isBiddingAward", isBiddingAward);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOI_LETTER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Parses the loi details.
	 * 
	 * @param scLoi
	 *            the sc loi
	 * @param outcome
	 *            the outcome
	 */
	private void parseLOIDetails(ScLoi scLoi, String outcome) {
		if (scLoi.isBrandNew()) {
			scLoi.setIssueDate(new Date());

			ScLoiWorkFlowStatus currentStatus = (ScLoiWorkFlowStatus)scLoi.getScWorkFlowStatus();
			List<ScLoiWorkFlowStatus> workFlowStatuses = new ArrayList<ScLoiWorkFlowStatus>();
			workFlowStatuses.add(currentStatus);
			scLoi.setScLoiWorkFlowStatuses(workFlowStatuses);
			
			// To avoid workflow status reset to null when got error during direct submit without save.
			List<ScLoiRemark> remarks = scLoi.getScLoiRemarks();
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScLoiRemark remark : remarks) {
					remark.setScWorkFlowStatus(currentStatus);
				}
			}
		}
		
		// validate
		validateLOIDetails(scLoi, outcome);
		
		Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId()
				.longValue();

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
		} else if(!scLoi.isApprovalReq()){
			scLoi.setScLoiApproval(null);
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

		if(!(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome))){
			scLoi.setScLoiRemarks(finalizeRemarks(scLoi.getScLoiRemarks()));
		}

		// nego terms
		if (QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)
				|| QTStatusConst.LOI_PENDING_REVISION.equals(statusId)
				|| QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
			if (scLoi.getScNegoTerms() != null
					&& scLoi.getScNegoTerms().size() > 0) {
				List<ScLoiTerm> selectedTerms = new ArrayList<ScLoiTerm>();
				for (ScLoiTerm loiTerm : scLoi.getScNegoTerms()) {
					if (loiTerm.isSelected()) {
						selectedTerms.add(loiTerm);
					}
				}
				scLoi.setScNegoTerms(selectedTerms);
			}
		}
	}
	
	/**
	 * Validate loi details. 
	 *
	 * @param scLoi the sc loi
	 * @param outcome the outcome
	 */
	private void validateLOIDetails(ScLoi scLoi, String outcome) {
		Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId().longValue();

		if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome) && (QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)
				|| QTStatusConst.LOI_PENDING_REVISION.equals(statusId))) {

			// online bidding
			if (scLoi.getRecommendBy() != null) {
				PmParameter recommendedBy = scLoi.getRecommendBy();
				if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
						.equals(recommendedBy.getParameterCode())) {
					// validate negotiation location
					if (scLoi.getScNegoLocations() == null
							|| scLoi.getScNegoLocations().isEmpty()) {
						throw new EpException(MessageConst.ERROR_760274);
					}
				}
			}
			
			// validate negotiation Term
			boolean isSelected = Boolean.TRUE;
			if (scLoi.getScNegoTerms() != null
					&& scLoi.getScNegoTerms().size() > 0) {
				for (ScLoiTerm scLoiTerm : scLoi.getScNegoTerms()) {
					if (scLoiTerm.isSelected()) {
						isSelected = Boolean.FALSE;
					}
				}
				if (isSelected) {
					throw new EpException(MessageConst.ERROR_760289);
				}
			}

			if(scLoi.isApprovalReq()){
				if(null == getManagedBean().getApproverUser() || null == getManagedBean().getApproverUser().getUserId()){
					throw new EpException("Approver is required.");
				}
			}
		}

		if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
			// validate zone tier and tier pricing
			for (ScLoiLoaItem  scLoiLoaItem : scLoi.getScLoiLoaItems()) {
				if(null != scLoiLoaItem.getTplSpecTierQty()){
					List<ScLoiLoaItem> filteredMaxPriceItems  = getManagedBean().getScLoiLoaItems();
					if(null == filteredMaxPriceItems){
						filteredMaxPriceItems  = filterZoneTierOrTierPriceItems(scLoi);
						getManagedBean().setScLoiLoaItems(filteredMaxPriceItems);
					}

					if(filteredMaxPriceItems != null && filteredMaxPriceItems.contains(scLoiLoaItem)){
						validateItemTierPriceByItem(scLoiLoaItem, filteredMaxPriceItems);
					}
				}
			}

			// validate deduction payamment for Others
			validateOthersDeductionPay(scLoi);

			// validate  supporting document
			if(scLoi.getScLoiSupportingDocs()  != null && scLoi.getScLoiSupportingDocs().size()>0){
				for (ScLoiSupportingDoc scLoiSupportingDoc : scLoi.getScLoiSupportingDocs() ) {
					if( null == scLoiSupportingDoc.getTitle() || StringUtils.isBlank(scLoiSupportingDoc.getTitle())){
						throw new EpException("Please enter supporting document title");
					}
					if( null == scLoiSupportingDoc.getScLoiSupportingDocAttachment()){
						throw new EpException("Please attach supporting document");
					}
				}
			}
			
			// validate negotiation location
			if (scLoi.getScNegoLocations() == null
					|| scLoi.getScNegoLocations().isEmpty()) {
				throw new EpException(MessageConst.ERROR_760274);
			}
			
		}

		if (QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)
				|| QTStatusConst.LOI_PENDING_REVISION.equals(statusId)) {
			// validate nego location duplicate 
			if(null != scLoi.getScNegoLocations()){
				List<Date> uniqueNegoDates = new ArrayList<Date>();
				for (ScLoiNegoLocation scLoiNegoLocation : scLoi.getScNegoLocations()) {
					if (uniqueNegoDates.contains(scLoiNegoLocation.getNegoDate())) {
						throw new EpException(MessageConst.ERROR_760339);
					}
					uniqueNegoDates.add(scLoiNegoLocation.getNegoDate());
				}
			}

			// validate supplier contract address
			if(null == scLoi.getScSupplierAddress()){
				throw new EpException("No supplier contact address");
			}
		}
	}

	/**
	 * Renew session with previous values.
	 */
	private void renewSessionWithPreviousValues() {
		List<ScLoiLoa>  recSupplierList = getManagedBean().getRecSupplierList();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		Object payLoadData = getManagedBean().getPayLoad();
		Long sourceId = getManagedBean().getSourceId();
		String taskId = getManagedBean().getTaskId();
		String[] outcomes = getManagedBean().getOutComes();
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		String docType = getManagedBean().getDocType();
		String committeeType = getManagedBean().getCommitteeType();
		String backToPreviousPage = getManagedBean().getBackToPreviousPage();
		List<String> recommendDtlIds = getManagedBean().getRecommendDtlIds();
		List<ScLoiTerm> negoTerms = getManagedBean().getNegoTermsList();
		boolean editButton = getManagedBean().isEditFlag();
		boolean buttonFlag = getManagedBean().isButtonFlag();
		int selectedTabIndex = getManagedBean().getSelectedTabIndex();
		boolean isSecretaryUser = getManagedBean().isSecretaryUser();

		this.renewSesionObject();

		getManagedBean().setSourceId(sourceId);
		getManagedBean().setPayLoad(payLoadData);
		getManagedBean().setTaskId(taskId);
		getManagedBean().setOutComes(outcomes);
		getManagedBean().setCurrentStatusId(currentStatusId);
		getManagedBean().setBackToPreviousPage(backToPreviousPage);
		if (payLoadData != null) {
			if (payLoadData instanceof SCEvaluationDataType) {
				getManagedBean().setRecommendDtlIds(
						((SCEvaluationDataType) payLoadData).getGroupkeyMa());
			} else if (payLoadData instanceof SCAwardSupplierDataType) {
				getManagedBean().setRecommendDtlIds(
						((SCAwardSupplierDataType) payLoadData)
								.getGroupkeyList());
			}
		}
		getManagedBean().setDocType(docType);
		getManagedBean().setRecommendDtlIds(recommendDtlIds);
		getManagedBean().setCommitteeType(committeeType);
		getManagedBean().setNegoTermsList(negoTerms);
		getManagedBean().setEditFlag(editButton);
		getManagedBean().setButtonFlag(buttonFlag);
		getManagedBean().setScQtFinalization(scQtFinalization);
		getManagedBean().setRecSupplierList(recSupplierList);
		getManagedBean().setSelectedTabIndex(selectedTabIndex);
		getManagedBean().setSecretaryUser(isSecretaryUser);
		if(null!=scQtFinalization){
		this.getManagedBean().setScQt(scQtFinalization.getScQt());
		}
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
	 * Deduction type change. Deduction Clause will show when deduction type
	 * changes
	 * 
	 * @param scQtDeduction
	 *            the sc qt deduction
	 */
	public void deductionTypeChange(ScQtDeduction scQtDeduction) {
		
		debugLogger("deductionTypeChange");
			Long parameterId = scQtDeduction.getDeductionType()
					.getParameterId();
			scQtDeduction.setDeductionType(retrieveParameterById(parameterId));
			validateDuplicateDeductionPay(scQtDeduction);
			getClausesTitle(scQtDeduction);
			scQtDeduction.setScQtDeductionClauses(null);
			getManagedBean().setSelectedScQtDeduction(scQtDeduction);
		
	}

	/**
	 * Validate deduction payment.
	 *
	 * @param scQtDeduction the sc qt deduction
	 */
	private void validateDuplicateDeductionPay(ScQtDeduction scQtDeduction) {
		List<ScQtDeduction> scQtDeductions = getManagedBean().getScLoi().getScQtDeductions();
		if(scQtDeductions != null && scQtDeductions.size()>1){
			for (ScQtDeduction qtDeduction : scQtDeductions) {
				if(!qtDeduction.equals(scQtDeduction)){
					if(qtDeduction.getDeductionType().getParameterId() == scQtDeduction.getDeductionType().getParameterId()){
						throw new EpException(MessageConst.ERROR_700027, new String[] {ResourceBundleUtils.getMessage(getParameterDesc(scQtDeduction.getDeductionType()),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
		}

	}

	/**
	 * Gets the clauses title.
	 * 
	 * @param deduction
	 *            the deduction
	 * @return the clauses title
	 */
	private void getClausesTitle(ScQtDeduction deduction) {
		StringBuilder title = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if (deduction != null && deduction.getDeductionType() != null) {
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
	 * @param deduction
	 *            the deduction
	 * @return the clauses title
	 */
	private void getLoiLoaListTitle(ScLoiLoa scLoiLoa) {
		StringBuilder title = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		
		if(scLoiLoa != null && scLoiLoa.getSmSupplier() != null){
			title.append(scLoiLoa.getSmSupplier().getCompanyName());
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
		scQtDeduction.setScQt(getManagedBean().getScQt());
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
		if (qtDeduction != null) {
			deleteRowAction(scLoi.getScQtDeductions(), qtDeduction);
			getManagedBean().setSelectedScQtDeduction(null);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean().setSelectedScQtDeduction(null);
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
		ScQtDeduction scQtDeduction = getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> clauses = getManagedBean()
				.getSelectedDeductionClauses();
		if (clauses != null && clauses.size() > 0) {
			deleteRowsAction(scQtDeduction.getScQtDeductionClauses(), clauses);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean()
		.setSelectedDeductionClauses(null);
	}

	/**
	 * Contract signer event.
	 */
	public void contractSignerEvent() {
		debugLogger("contractSignerEvent");
		PmUser contractSigner = getManagedBean().getContractSignerUser();
		if (contractSigner != null) {
			if (getManagedBean().getContractSigners() != null
					&& getManagedBean().getContractSigners().size() > 0) {
				List<PmUser> contractSignerList = getManagedBean()
						.getContractSigners();
				for (PmUser user : contractSignerList) {

					if (contractSigner.getUserId().equals(user.getUserId())) {
						getManagedBean().getScLoi().setLoiSignerDesig(
								user.getDesignation());
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
		if (scLoi.isApprovalReq()) {
			ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();
			PmUser approverUser = null;
			if (scLoiApproval != null) {
				approverUser = scLoiApproval.getApprover();
			}
			if (approverUser == null) {
				approverUser = new PmUser();
			}
			getManagedBean().setApproverUser(approverUser);
		} else {
			getManagedBean().setApproverUser(new PmUser());
		}
	}

	/**
	 * Calculate start month.
	 * 
	 * @param event
	 *            the event
	 */
	public void calculateStartMonth(AjaxBehaviorEvent event) {
		event.getComponent().getAttributes().get("value");

	}

	/**
	 * Handle file upload.
	 * 
	 * @param event
	 *            the event
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleFileUpload");
		if (event.getFile() != null) {
			// String attachmentDocType =
			// getManagedBean().getAttachmentDocType();
			ScAttachment scAttachment = null;
			ScLoiReport selectedScLoiReport = this.getManagedBean()
					.getSelectedScLoiReport();
			ScLoiReportAttachment scLoiReportAttachment = selectedScLoiReport
					.getScLoiReportAttachment();
			if (null == scLoiReportAttachment) {
				scLoiReportAttachment = new ScLoiReportAttachment();
				scLoiReportAttachment.setScLoiReport(selectedScLoiReport);
				selectedScLoiReport
						.setScLoiReportAttachment(scLoiReportAttachment);
			}
			scLoiReportAttachment.setScLoiReport(selectedScLoiReport);
			scAttachment = scLoiReportAttachment;

			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	/**
	 * Checkbox event to select Supplier Finalization Report under Briefing Paper tab
	 * 
	 * @param ScQtFinalizationReport
	 *            the ScQtFinalizationReport to set
	 */
	public void selectReportEvent(ScLoiReport report) {
		debugLogger("selectReportEvent - Checkbox select");

		List<ScLoiReport> selectedLoiReports
					= getManagedBean().getSelectedScLoiReports();
		if (selectedLoiReports == null) {
			selectedLoiReports= new ArrayList<ScLoiReport>();
			getManagedBean().setSelectedScLoiReports(
								   selectedLoiReports);
		}
		if (report.isSelected()) {
			selectedLoiReports.add(report);
		} else {
			selectedLoiReports.remove(report);
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
//		scLoiReport.setScLoiReportAttachment(new ScLoiReportAttachment());

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
			scLoiReport.setDocCategory(
					ParameterConst.DOCUMENT_CATEGORY_LOI_NEGOTIATION_RESULT); // NR
		} finally {
			parameterSO = null;
		}

		scLoiReports.add(scLoiReport);
	}

	/**
	 * Delete loi report.
	 */
	public void deleteLoiReport() {
		ScLoi scLoi = this.getManagedBean().getScLoi();
		List<ScLoiReport> scLoiReports = scLoi.getScLoiReports();
		List<ScLoiReport> selectedLoiReports
							= getManagedBean().getSelectedScLoiReports();
		List<ScLoiReport> deletedLoiReports
							= getManagedBean().getDeletedScLoiReports();
		
		if (deletedLoiReports == null) {
			deletedLoiReports = new ArrayList<ScLoiReport>();
			getManagedBean().setDeletedScLoiReports(
								   deletedLoiReports);
		}
		
		if (CollectionUtils.isNotEmpty(selectedLoiReports)) {
			for (ScLoiReport loiReport 
						: selectedLoiReports) {
				if (!loiReport.isBrandNew()) {
					loiReport.setDeleted(true);
					deletedLoiReports.add(loiReport);
				}
				scLoiReports.remove(loiReport);
			}
		}
	}

	/**
	 * Download report.
	 * 
	 * @param scLoiReport
	 *            the sc loi report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadReport(ScLoiReport scLoiReport) throws IOException {
		debugLogger("downloadReport");
		ScLoiReportAttachment scLoiReportAttachment = scLoiReport
				.getScLoiReportAttachment();
		if (QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION
				.equals(getManagedBean().getCurrentStatusId())
							|| scLoiReportAttachment == null) {
			scLoiReportAttachment = generateDocumentAttachment(scLoiReport);
			scLoiReport.setScLoiReportAttachment(scLoiReportAttachment);
		}
		downloadFile(scLoiReportAttachment);
	}

	/**
	 * Download all reports.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");
		List<ScLoiReport> scLoiReports = getManagedBean().getScLoi()
				.getScLoiReports();
		List<ScLoiReportAttachment> scAttachments = new ArrayList<ScLoiReportAttachment>();
		for (ScLoiReport scLoiReport : scLoiReports) {
			ScLoiReportAttachment scLoiReportAttachment = scLoiReport
					.getScLoiReportAttachment();
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
		scLoiSuppDoc
				.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOI_SUPPORTING_DOC);
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
		List<ScLoiSupportingDoc> selectedScLoiSuppDocs = getManagedBean()
				.getSelectedScLoiSuppDocs();

		if (selectedScLoiSuppDocs != null && selectedScLoiSuppDocs.size() > 0) {
			deleteRowsAction(scLoi.getScLoiSupportingDocs(),
					selectedScLoiSuppDocs);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
	}

	/**
	 * Handle supp doc file upload.
	 * 
	 * @param event
	 *            the event
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleSuppDocFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleSuppDocFileUpload");
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScLoiSupportingDoc selectedScLoiSuppDoc = this.getManagedBean()
					.getSelectedScLoiSuppDoc();
			ScLoiSupportingDocAttachment scLoiSupportingDocAttachment = selectedScLoiSuppDoc
					.getScLoiSupportingDocAttachment();
			if (null == scLoiSupportingDocAttachment) {
				scLoiSupportingDocAttachment = new ScLoiSupportingDocAttachment();
				scLoiSupportingDocAttachment
						.setScLoiSupportingDoc(selectedScLoiSuppDoc);
				selectedScLoiSuppDoc
						.setScLoiSupportingDocAttachment(scLoiSupportingDocAttachment);
			}
			scLoiSupportingDocAttachment.setDocNo(getManagedBean().getScLoi().getLoiNo());
			scAttachment = scLoiSupportingDocAttachment;

			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	/**
	 * Generate document attachment.
	 * 
	 * @param scLoiReport
	 *            the sc loi report
	 * @return the sc loi report attachment
	 */
	private ScLoiReportAttachment generateDocumentAttachment(
			ScLoiReport scLoiReport) {
		String definitionType = scLoiReport.getDefinationType()
				.getParameterCode();
		ScLoiReportAttachment scLoiReportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {
			try {
				String docDefinitionKey = scLoiReport.getPmDocument()
						.getDocDefinitionKey();
				scLoiReportAttachment = new ScLoiReportAttachment();
				scLoiReportAttachment.setScLoiReport(scLoiReport);
				scLoiReportAttachment.setDocNo(scLoiReport.getScLoi()
						.getLoiNo());

				if (SourcingConstants.DOC_DEF_KEY_COVER
						.equals(docDefinitionKey)) {
					generateCoverPdfFromVelocity(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_OFFERED_QT_DTLS
						.equals(docDefinitionKey)) {
					generateOfferedAndQTDtlsPdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_LOCATION
						.equals(docDefinitionKey)) {
					generateNegotiationLocationPdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_COMMITTEE
						.equals(docDefinitionKey)) {
					generateNegotiationCommitteePdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_RESULT
						.equals(docDefinitionKey)) {
					generateNegotiationResultPdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_RECOMMENDATION
						.equals(docDefinitionKey)) {
					generateNegotiationRecommendationPdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
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
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateNegotiationRecommendationPdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Recommendation.pdf");
		populateAttachmentDetails(attachment);
		VelocityContext context = new VelocityContext();
		List<ScQtRecommend> recommendedItems = getManagedBean().getRecommendList();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(
						commonUtils.getDisplayNumberFormat(
								CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));		
		
		String afterNegoSubTotal = "";
		List<ScLoi> lois = getManagedBean().getLois();
		NegotiationRecommendationReportDTO reportDTO = null;
		Map<TplSpecZoneDtl,NegotiationRecommendationReportDTO> zoneDetailsMap = null;
		
		if (CollectionUtils.isNotEmpty(lois)) {
			for (ScLoi loi : lois) {
				if (!QTStatusConst.LOI_REJECTED.equals(
						loi.getScWorkFlowStatus().getPmStatus().getStatusId())) {
					Map<Long,TplSpecZoneDtl> zoneMap = getManagedBean().getZoneMap();
					if (recommendedItems != null) {
						Long awardedSupplierId = null;
						
						if (!getManagedBean().isItemized()) {
							Set<ScQtEvaluationResultDtl> awardedSuppliers = null;
							if (getManagedBean().isZonal()) {
								if (zoneDetailsMap == null) {
									zoneDetailsMap = new HashMap<TplSpecZoneDtl,NegotiationRecommendationReportDTO>();
								}
								Map<Long,List<ScQtRecommend>> scQtRecommendsByZone
										= getManagedBean().getScQtRecommendsByZone();		
								
								for (Long zoneId : zoneMap.keySet()) {
									awardedSupplierId = null;
									reportDTO = new NegotiationRecommendationReportDTO();
									ScQtRecommend scQtRecommend 
											= scQtRecommendsByZone.get(zoneId).get(0);
									if (CollectionUtils.isEmpty(
											scQtRecommend.getAwardSupplierList())) {
										processRecommendedSuppliersByPKG(
												scQtRecommendsByZone.get(zoneId));
									}
									
									if (CollectionUtils.isNotEmpty(
											scQtRecommend.getAwardSupplierList())) {
										for (ScQtEvaluationResultDtl evalResDtl
													: scQtRecommend.getAwardSupplierList()) {
											if (evalResDtl.isAwareded()) {
												awardedSuppliers = new HashSet<ScQtEvaluationResultDtl>();												
												awardedSuppliers.add(evalResDtl);
												awardedSupplierId = evalResDtl.getQtEvaluationResult()
														.getScQtProposal().getScQtSupplier()
														.getSmSupplier().getSupplierId();
												break;
											}
										}
									}
									
									Long loiSupplierId = loi.getScLoiLoa().getSmSupplier().getSupplierId();
									if (awardedSupplierId != null && 
											loiSupplierId.equals(awardedSupplierId)) {
										Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap 
													= getManagedBean().getLoiLoaItemsMap();
										List<ScLoiLoaItem> loiLoaItems = new ArrayList<ScLoiLoaItem>();
										for (ScLoiLoaItem scLoiLoaItem 
												: loiLoaItemsMap.get(zoneId)) {
											Long itemSupplierId = scLoiLoaItem.getScQtRecommendDtl()
																		.getSupplier().getSupplierId();
											if (awardedSupplierId.equals(itemSupplierId)) {
												loiLoaItems.add(scLoiLoaItem);
											}
										}
							
										BigDecimal afterNegotiationSubTotal = calculateAfterNegoSubTotal(
												loiLoaItems, scQtRecommendsByZone.get(zoneId));
										afterNegoSubTotal = amountDF.format(afterNegotiationSubTotal);
										reportDTO.setAwardedSuppliers(
												new ArrayList<ScQtEvaluationResultDtl>(awardedSuppliers));
										reportDTO.setRecommendedItems(scQtRecommendsByZone.get(zoneId));
										reportDTO.setLoiLoaItems(loiLoaItems);
										reportDTO.setAfterNegoSubTotal(afterNegoSubTotal);
										zoneDetailsMap.put(zoneMap.get(zoneId),reportDTO);
									}
								}
							} else {
								reportDTO = new NegotiationRecommendationReportDTO();
								reportDTO.setEmpty(true);
								Set<ScQtEvaluationResultDtl> recommendedSuppliers 
											= new HashSet<ScQtEvaluationResultDtl>(
														getManagedBean().getRecommendedSupplierList());
								if (CollectionUtils.isNotEmpty(recommendedSuppliers)) {
									for (ScQtEvaluationResultDtl evalResDtl
														: recommendedSuppliers) {
										if (evalResDtl.isAwareded()) {
											awardedSuppliers = new HashSet<ScQtEvaluationResultDtl>();
											awardedSuppliers.add(evalResDtl);
											awardedSupplierId = evalResDtl.getQtEvaluationResult()
													.getScQtProposal().getScQtSupplier()
													.getSmSupplier().getSupplierId();											
											break;
										}
									}
								}			
								
								Long loiSupplierId = loi.getScLoiLoa().getSmSupplier().getSupplierId();
								if (loiSupplierId.equals(awardedSupplierId)) {
									reportDTO.setEmpty(false);
									BigDecimal afterNegotiationSubTotal = calculateAfterNegoSubTotal(
											loi.getScLoiLoaItems(), recommendedItems);
									afterNegoSubTotal = amountDF.format(afterNegotiationSubTotal);									
									reportDTO.setAwardedSuppliers(
											new ArrayList<ScQtEvaluationResultDtl>(awardedSuppliers));
									reportDTO.setRecommendedItems(recommendedItems);
									reportDTO.setAfterNegoSubTotal(afterNegoSubTotal);
									reportDTO.setLoiLoaItems(loi.getScLoiLoaItems());
									
									break;
								}
							}
						} else {
							if (getManagedBean().isZonal()) {
								if (zoneDetailsMap == null) {
									zoneDetailsMap = new HashMap<TplSpecZoneDtl,NegotiationRecommendationReportDTO>();
								}
								Map<Long,List<ScQtRecommend>> scQtRecommendsByZone
										= getManagedBean().getScQtRecommendsByZone();
								
								for (Long zoneId : zoneMap.keySet()) {
									reportDTO = new NegotiationRecommendationReportDTO();
									Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap 
												= getManagedBean().getLoiLoaItemsMap();

									processMultiLOIDetails(ParameterConst.RECOMMEND_TYPE_ITEM, 
															 scQtRecommendsByZone.get(zoneId));
									
									BigDecimal afterNegotiationSubTotal = calculateAfterNegoSubTotal(
											loiLoaItemsMap.get(zoneId), scQtRecommendsByZone.get(zoneId));
									afterNegoSubTotal = amountDF.format(afterNegotiationSubTotal);								
									
									reportDTO.setRecommendedItems(scQtRecommendsByZone.get(zoneId));
									reportDTO.setAfterNegoSubTotal(afterNegoSubTotal);
									zoneDetailsMap.put(zoneMap.get(zoneId),reportDTO);
								}
							} else {
								reportDTO = new NegotiationRecommendationReportDTO();
								BigDecimal afterNegotiationSubTotal = calculateAfterNegoSubTotal(
										loi.getScLoiLoaItems(), recommendedItems);
								afterNegoSubTotal = amountDF.format(afterNegotiationSubTotal);								
								reportDTO.setRecommendedItems(recommendedItems);
								reportDTO.setAfterNegoSubTotal(afterNegoSubTotal);
							}

						}
					}	
					
				}
			}
		}
		
		if (getManagedBean().isZonal()) {
			for (TplSpecZoneDtl specZone
						: getManagedBean().getZoneMap().values()) {
				if (!zoneDetailsMap.containsKey(
						specZone)) {
					NegotiationRecommendationReportDTO tempDTO
							= new NegotiationRecommendationReportDTO();
					tempDTO.setEmpty(true);
					zoneDetailsMap.put(specZone,tempDTO);
				}
			}
		}
		
		context.put("reportDTO", reportDTO);
		context.put("zoneDetailsMap", zoneDetailsMap);
		context.put("numberFormatter", new DecimalFormat("#,###,##0.00"));
		context.put("itemized", getManagedBean().isItemized());
		context.put("isZonal", getManagedBean().isZonal());
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				"/sc/loi/loi-negotiationRecommendation.vm", context,
				new FileOutputStream(getActualFilePath(attachment)));
	}
	
	private BigDecimal calculateAfterNegoSubTotal(List<ScLoiLoaItem> scLoiLoaItems, 
											  List<ScQtRecommend> recommendedItems) {
		BigDecimal afterNegotiationSubTotal = new BigDecimal(0.0);
		
		if (CollectionUtils.isNotEmpty(scLoiLoaItems)) {
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				if (scLoiLoaItem.getAfterNegoSubTotal() != null) {
					BigDecimal itemAfterNegoSubTotal = scLoiLoaItem.getAfterNegoSubTotal(); 
					itemAfterNegoSubTotal = itemAfterNegoSubTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					afterNegotiationSubTotal = afterNegotiationSubTotal.add(
														itemAfterNegoSubTotal);
					afterNegotiationSubTotal = afterNegotiationSubTotal
													.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				}
			}
		}
				
		return afterNegotiationSubTotal;
	}	

	/**
	 * Generate negotiation result pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateNegotiationResultPdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Result.pdf");
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();
		List<ScLoi> lois = getManagedBean().getLois();
		if (CollectionUtils.isNotEmpty(lois)) {
			for (ScLoi loi : lois) {
				if(!loi.equals(getManagedBean().getScLoi())){
					calculateOfferedAndFinalPriceByItem(loi);	
				}
			}
		}
		context.put("isZonal", getManagedBean().isZonal());
		context.put("decimalFormatter", new DecimalFormat("#,###,##0.00"));
		context.put("scLois", lois);
		context.put("status", QTStatusConst.LOI_REJECTED);
		context.put("bean", this);

		DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.LOI_NEGOTIONATION_RESULT, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate negotiation committee pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateNegotiationCommitteePdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Committee.pdf");
		populateAttachmentDetails(attachment);

		// ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		VelocityContext context = new VelocityContext();
		List<ScQtCommitteeActivity> activityList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtMeetingAttendance> meetingAttendances = new ArrayList<ScQtMeetingAttendance>();
		List<ScQtMeetingAttendance> meetingAttendancesList = new ArrayList<ScQtMeetingAttendance>();

		if (getManagedBean().getEcActivityList() != null) {
			for (ScQtCommitteeActivity ecActivity : getManagedBean()
					.getEcActivityList()) {
				if (ecActivity.isMultiLoiAttended()) {
					activityList.add(ecActivity);
				}
			}
		}
		if (getManagedBean().getTecActivityList() != null) {
			for (ScQtCommitteeActivity tecActivity : getManagedBean()
					.getTecActivityList()) {
				if (tecActivity.isMultiLoiAttended()) {
					activityList.add(tecActivity);
				}
			}
		}
		if (getManagedBean().getFecActivityList() != null) {
			for (ScQtCommitteeActivity fecActivity : getManagedBean()
					.getFecActivityList()) {
				if (fecActivity.isMultiLoiAttended()) {
					activityList.add(fecActivity);
				}
			}
		}
		if (getManagedBean().getScQtCommittee() != null) {
			for (ScQtCommitteeActivity activity : getManagedBean()
					.getScQtCommittee().getCommitteeActivities()) {
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
					}
					if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getGrade() == null) {
							activity.getScCommitteeMember().setGrade("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setGrade("");
					}
					if (activity.getScCommitteeMember() != null) {
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
			if (null != getManagedBean().getScQtCommittee().getMeetingAttendances()
					&& !getManagedBean().getScQtCommittee().getMeetingAttendances().isEmpty()) {
				meetingAttendances = getManagedBean().getScQtCommittee().getMeetingAttendances();
				for (ScQtMeetingAttendance attendance : meetingAttendances) {
					if (attendance.getName() == null) {
						attendance.setName("");
					}
					if (attendance.getIcPassport() == null) {
						attendance.setIcPassport("");
					}
					meetingAttendancesList.add(attendance);
				}
			}
		}
		context.put("activityLists", activityList);
		context.put("meetingAttendances", meetingAttendancesList);
		context.put("bean", this);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				"/sc/loi/loi-negotiationCommittee.vm", context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate negotiation location pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateNegotiationLocationPdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Location.pdf");
		populateAttachmentDetails(attachment);
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		VelocityContext context = new VelocityContext();
		
		context.put("dateSDF", dateSDF);
		context.put("loiList", getManagedBean().getLois());
		context.put("rejectedStatus", "62406");

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				"/sc/loi/loi-negotiationLocation.vm", context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate offered and qt dtls pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateOfferedAndQTDtlsPdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("OfferedAndQTDetails.pdf");
		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();

		context.put("title", scLoiLoa.getTitle());
		/*context.put("fulfilmentType",
				getParameterDesc(scLoiLoa.getFulfilmentTypeCode()));*/
		StringBuilder contractDuration = new StringBuilder();
		if (this.getManagedBean().getScQt().getContractDuration() != null) {
			contractDuration.append(this.getManagedBean().getScQt().getContractDuration() + " " + getMessageCommon("lbl.sc.sp.month")); 
		}
		context.put("contractDuration", contractDuration);
		String ptjName=" ";
		if(null!=this.getManagedBean().getCurrentStatusId()){
			log.info("=====if generateOfferedAndQTDtlsPdf"+this.getManagedBean().getCurrentStatusId());
			log.info("publish date"+this.getManagedBean().getScQtFinalization().getScQt().getPublishDate());
			log.info("current date"+this.getManagedBean().getScQtFinalization().getScQt().getCreatedDate());
		 ptjName=retrievePTJByOrgProfileId(pmOrgProfile.getOrgProfileId(),isQTStatusPublished(this.getManagedBean().getCurrentStatusId())?this.getManagedBean().getScQtFinalization().getScQt().getPublishDate() : this.getManagedBean().getScQtFinalization().getScQt().getCreatedDate());
		}else{
			log.info("=====else generateOfferedAndQTDtlsPdf");
			ptjName=pmOrgProfile.getOrgName();
		}
		log.info("ptjName generateOfferedAndQTDtlsPdf"+ptjName);
		context.put("ptj", ptjName);
		String totalSuppliers = "";
		if (this.getManagedBean().getLois() != null) {
			totalSuppliers = String.valueOf(this.getManagedBean().getLois()
					.size());
		}
		context.put("supplierSize", totalSuppliers);

		List<ScLoi> localList = new ArrayList<ScLoi>();
		if (this.getManagedBean().getLois() != null) {
			for (ScLoi loi : this.getManagedBean().getLois()) {
				if (QTStatusConst.LOI_COMPLETED.equals(loi.getScWorkFlowStatus().getPmStatus()
						.getStatusId())) {
					localList.add(loi);
				}
			}
		}
		String totalStatusSuppliers = "";
		if (localList != null) {
			totalStatusSuppliers = String.valueOf(localList.size());
		}
		context.put("statusSupplierSize", totalStatusSuppliers);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				"/sc/loi/loi-offeredAndQTDetails.vm", context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate cover pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateCoverPdfFromVelocity(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Cover.pdf");
		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();
		
		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() +":" + fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		// context.put("ministry",
		// pmOrgProfile.getParentOrgProfile().getOrgName());
		String ptjName=" ";
		if(null!=this.getManagedBean().getCurrentStatusId()){
			log.info("=====if generateCoverPdfFromVelocity"+this.getManagedBean().getCurrentStatusId());
			log.info("publish date"+this.getManagedBean().getScQtFinalization().getScQt().getPublishDate());
			log.info("current date"+this.getManagedBean().getScQtFinalization().getScQt().getCreatedDate());
		 ptjName=retrievePTJByOrgProfileId(pmOrgProfile.getOrgProfileId(),isQTStatusPublished(this.getManagedBean().getCurrentStatusId())?this.getManagedBean().getScQtFinalization().getScQt().getPublishDate() : this.getManagedBean().getScQtFinalization().getScQt().getCreatedDate());
		}else{
			log.info("=====else generateCoverPdfFromVelocity");
			ptjName=pmOrgProfile.getOrgName();
		}
		log.info("ptjName generateCoverPdfFromVelocity"+ptjName);
		context.put("agency",ptjName);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOI_COVER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Select all select all nego terms
	 * 
	 * @param event
	 *            the event
	 */
	public void selectAll(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScLoiTerm> scLoiTerms = getManagedBean().getScLoi()
				.getScNegoTerms();
		if (scLoiTerms != null && scLoiTerms.size() > 0) {
			for (ScLoiTerm scLoiTerm : scLoiTerms) {
				if (isSelectAll && scLoiTerm.getPmTerm() == null) {
					scLoiTerm.setSelectedTerm(true);
				} else {
					scLoiTerm.setSelectedTerm(false);
				}
			}
		}
	}

	/**
	 * Select all select all nego locations
	 * 
	 * @param event
	 *            the event
	 */
	public void selectAllNegoLocations(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScLoiNegoLocation> scLoiLocations = getManagedBean().getScLoi()
				.getScNegoLocations();
		if (scLoiLocations != null && scLoiLocations.size() > 0) {
			for (ScLoiNegoLocation scloiLocation : scLoiLocations) {
				if (isSelectAll) {
					scloiLocation.setSelected(true);
				} else {
					scloiLocation.setSelected(false);
				}
			}
		}
	}

	/**
	 * Nego term price event. when price found in terms, shows original offered
	 * price and after nego offered price.
	 */
	public void negoTermPriceEvent() {
		boolean termPriceFound = false;
		ScLoi scLoi = getManagedBean().getScLoi();

		if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if (term.getTermDesc() != null) {
					if (QTConstants.LOI_NEGO_TERM_PRICE
							.equals(term.getTermDesc())) {
						termPriceFound = true;
					}
				}
			}
		}
		getManagedBean().setHideColumnFlag(termPriceFound);
	}

	/**
	 * Price key in event. when keyin unit price, calculate subtotal after nego.
	 */
	public void priceKeyInEvent() {
		debugLogger("priceKeyInEvent");
		ScLoi scLoi = getManagedBean().getScLoi();

		List<ScLoiLoaItem> filteredMaxPriceItemsbyQty  = getManagedBean().getScLoiLoaItems();
		if(null == filteredMaxPriceItemsbyQty){
			filteredMaxPriceItemsbyQty  = filterZoneTierOrTierPriceItems(scLoi);
			getManagedBean().setScLoiLoaItems(filteredMaxPriceItemsbyQty);
		}

		// calculate max price items only.
		boolean isCalculateMaxPriceItem = Boolean.TRUE;
		BigDecimal quantity = BigDecimal.valueOf(0d);
		// calc after nego total
		if (scLoi.getScLoiLoaItems() != null
				&& scLoi.getScLoiLoaItems().size() > 0) {
			BigDecimal subTotal = BigDecimal.valueOf(0d);
			BigDecimal totalAmount = BigDecimal.valueOf(0d);
			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				quantity = scLoiLoaItem.getQty();
				if(null == scLoiLoaItem.getTplSpecTierQty() ){
					// with out Tier quantity
					//quantity = scLoiLoaItem.getQty();
				} else{
					if(filteredMaxPriceItemsbyQty.contains(scLoiLoaItem)){
						isCalculateMaxPriceItem = Boolean.TRUE;
					} else{
						isCalculateMaxPriceItem = Boolean.FALSE;
					}
				}

				if(isCalculateMaxPriceItem){
					subTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(
							scLoiLoaItem.getScRequestItem().getTplSpecQuestion(),
							scLoiLoaItem.getUnitPrice(), quantity);

					scLoiLoaItem.setAfterNegoSubTotal(subTotal);
					totalAmount = totalAmount.add(subTotal);	
				}
			}
			scLoi.setScLoiLoaItems(scLoiLoaItems);
			scLoi.setAfterNegoTotal(totalAmount);
			scLoi.setLoiAmt(totalAmount);
		}
		getManagedBean().setScLoi(scLoi);
	}

	/**
	 * Check nego term price.
	 * 
	 * @param scLoi
	 *            the sc loi
	 * @return true, if successful
	 */
	private boolean checkNegoTermPrice(ScLoi scLoi) {
		boolean termPriceFound = false;
		// ScLoi scLoi = getManagedBean().getScLoi();
		if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if (QTConstants.LOI_NEGO_TERM_PRICE.equals(term.getTermDesc())) {
					termPriceFound = true;
				}
			}
		}
		return termPriceFound;
	}

	/**
	 * Removes the nego term price.
	 * 
	 * @param scLoi
	 *            the sc loi
	 */
	private void removeNegoTermPrice(ScLoi scLoi) {
		List<ScLoiTerm> scNegoTerms = new ArrayList<ScLoiTerm>();
		if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if (!QTConstants.LOI_NEGO_TERM_PRICE
						.equals(term.getTermDesc())) {
					scNegoTerms.add(term);
				}
			}
			scLoi.setScNegoTerms(scNegoTerms);
		}
	}

	/**
	 * Gets the award supplier list drop down.
	 * 
	 * @return the award supplier list drop down
	 */
	public List<SelectItem> getAwardSupplierListDropDown() {
		List<ScQtRecommend> selectedRecommendList = getManagedBean()
				.getSelectedRecommendList();
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		if (selectedRecommendList != null) {
			List<Long> supplierIds = new ArrayList<Long>();

			for (ScQtRecommend scQtRecommend : selectedRecommendList) {
				for (ScQtRecommendDtl scQtRecommendDtl 
							: scQtRecommend.getRecommendDtls()) {
					if (BooleanUtils.isTrue(
							scQtRecommendDtl.getLoiAwarded())) {
						SmSupplier smSupplier = scQtRecommendDtl.getSupplier();
						Long supplierId = smSupplier.getSupplierId();
	
						if (!supplierIds.contains(supplierId)) {
							selectItems.add(new SelectItem(supplierId, scQtRecommendDtl
									.getScQtProposalAmount().getScQtProposal().getSupplierNo().toString()));
							supplierIds.add(supplierId);
						}
					}
				}
			}
		}

		return selectItems;
	}

	/**
	 * Recommend supplier selected action.
	 * 
	 * @param scQtRecommendDtl
	 *           	the selected supplier
	 */
	public void recommendSupplierSelectedAction(ScQtRecommend scQtRecommend,
												ScQtRecommendDtl scQtRecommendDtl) {
		scQtRecommendDtl.setRecordStatusFlg(
				scQtRecommendDtl.isRecordStatus() ? DbValueConst.RECORD_STATUS_ACTIVE
						: DbValueConst.RECORD_STATUS_SUSPENDED);
		
		PmParameter itemType = scQtRecommend.getRequestItem()
				.getTplSpecQuestion().getTplSpecMaster().getItemTypeCode();
		boolean isItemTypeService = StringUtils.equals(ParameterConst.ITEM_TYPE_SERVICE,
										itemType.getParameterCode());
		
		if (scQtRecommend.getActiveSupplierCount() == 0) {
			scQtRecommendDtl.setAwardQty(
					scQtRecommend.getOrderQty());
		} else {
			if (scQtRecommendDtl.getAwardQty() == null) {
				scQtRecommendDtl.setAwardQty(new BigDecimal(0));
			}
		}
		
		// If item type is service, then disable multiple award
		if (isItemTypeService && 
				scQtRecommendDtl.isRecordStatus()) {
			for (ScQtRecommendDtl recommendDtl 
						: scQtRecommend.getRecommendDtls()) {
				if (!recommendDtl.equals(scQtRecommendDtl) && 
						BooleanUtils.isTrue(
								recommendDtl.getLoiAwarded())) {
					recommendDtl.setRecordStatus(false);
					recommendDtl.setRecordStatusFlg(
							DbValueConst.RECORD_STATUS_SUSPENDED);
				}
			}
		}
		updateRecommendBy(scQtRecommend);
		
		Long selectedSupplierId = scQtRecommendDtl.getSupplier().getSupplierId();
		
		for (ScQtEvaluationResultDtl shortlistedSupplier 
						: scQtRecommend.getShortListedSupplierList()) {
			Long shortlistedSupplierId = shortlistedSupplier.getQtEvaluationResult()
					.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
			if (selectedSupplierId == shortlistedSupplierId) {
				shortlistedSupplier.setAwareded(scQtRecommendDtl.isRecordStatus());
				break;
			}
		}

	}

	/**
	 * Award selected supplier action.
	 */
	public void awardSelectedSupplierAction() {
		ScQtEvaluationResultDtl awardedSupplier = this.getManagedBean()
				.getSelectedSupplier();
		Long awardedSupplierId = awardedSupplier.getQtEvaluationResult()
				.getScQtProposal().getScQtSupplier().getSmSupplier()
				.getSupplierId();
		List<ScQtRecommend> recommendedItems 
								= this.getManagedBean().getRecommendList();
		for (ScQtEvaluationResultDtl evalResultDtl
						: getManagedBean().getRecommendedSupplierList()) {
			if (awardedSupplier.equals(evalResultDtl)) {
				evalResultDtl.setAwareded(true);
			} else {
				evalResultDtl.setAwareded(false);
			}
		}
		if (CollectionUtils.isNotEmpty(recommendedItems)) {
			for (ScQtRecommend recommendedItem : recommendedItems) {
				for (ScQtRecommendDtl recommendDtl 
						: recommendedItem.getRecommendDtls()) {
					if (BooleanUtils.isTrue(
							recommendDtl.getLoiAwarded())) {
						Long recommendedSupplierId = recommendDtl.getSupplier()
								.getSupplierId();
						if (awardedSupplierId == recommendedSupplierId) {
							recommendDtl.setRecordStatus(true);
							recommendDtl.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_ACTIVE);
						} else if (BooleanUtils.isTrue(
										recommendDtl.getLoiAwarded())) {
							recommendDtl.setRecordStatus(false);
							recommendDtl.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_SUSPENDED);
						}
					}
				}
			}
		}
		
	}

	/**
	 * Event to update offered price by the supplier when the value of quantity
	 * changes, where Recommend Type is Itemized, item Recommend By is Multiple
	 * Award under Supplier Finalization tab
	 * 
	 * @param recommendDtl
	 *            the recommendDtl to set
	 */
	public void updateOfferedPrice(ScQtRecommend scQtRecommend, ScQtRecommendDtl recommendDtl) {
		debugLogger("updateOfferedPrice...");

		SmSupplier currentSupplier = recommendDtl.getSupplier();
		List<ScQtEvaluationResultDtl> recommendedSupplierList
											= scQtRecommend.getAwardSupplierList();
		if (currentSupplier != null
				&& CollectionUtils.isNotEmpty(recommendedSupplierList)) {
			for (ScQtEvaluationResultDtl evalResultDtl : recommendedSupplierList) {
				SmSupplier shortListedSupplier = evalResultDtl
						.getQtEvaluationResult().getScQtProposal()
						.getScQtSupplier().getSmSupplier();
				ScQtProposalItem proposalItem = evalResultDtl
						.getQtEvaluationResult().getScQtProposalItem();
				
				if (proposalItem != null
						&& (currentSupplier.getSupplierId())
								.equals(shortListedSupplier.getSupplierId())) {
					BigDecimal offeredQuantity = null;
					if (recommendDtl.getAwardQty() == null) {
						offeredQuantity = new BigDecimal(0);
					} else {
						offeredQuantity = recommendDtl.getAwardQty();
					}
					offeredQuantity = offeredQuantity.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					
					BigDecimal offerPrice = proposalItem.getOfferPrice();
					offerPrice = offerPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					
					ScRequestItem requestItem = recommendDtl
							.getScQtRecommend().getRequestItem();
					
					BigDecimal offeredPrice = TemplateSpecHelper
							.getInstance().calculateTotalAmount(
									requestItem.getTplSpecQuestion(),
									offerPrice, offeredQuantity);
					if (offeredPrice != null) {
						offeredPrice = offeredPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					}
					evalResultDtl.getQtEvaluationResult()
								 .setOfferedPrice(offeredPrice);
				}
			}
		}
	}	

	/**
	 * Update recommend by.
	 * 
	 * @param scQtRecommend
	 *            the sc qt recommend
	 */
	private void updateRecommendBy(ScQtRecommend scQtRecommend) {
		int activeSupplierCount = 0;
		for (ScQtRecommendDtl recommendDtl 
						: scQtRecommend.getRecommendDtls()) {
			if (BooleanUtils.isTrue(
					recommendDtl.getLoiAwarded()) && 
						recommendDtl.isRecordStatus()) {
				++activeSupplierCount;
			}
		}
		scQtRecommend.setActiveSupplierCount(activeSupplierCount);
//		String awardType = ParameterConst.RECOMMEND_BY_SINGLE_AWARD;
		
//		TplSpecQuestion specQuestion = scQtRecommend.getRequestItem().getTplSpecQuestion();
//		boolean isPriceTypeTier = TemplateSpecHelper.getInstance().isPriceTypeTier(
//				specQuestion.getClPriceTypeCode().getParameterCode());
//		if (isPriceTypeTier || 
//				!this.getManagedBean().isItemized()) {
//			awardType = ParameterConst.RECOMMEND_BY_AWARD;
//		}
//		String recommendBy = activeCount > 1 
//				? ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD : awardType;
//		scQtRecommend.setRecommendByStr(recommendBy);
//		scQtRecommend.setRecommendBy(ParameterHelper.getInstance()
//				.getParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, recommendBy));
		
		if (CollectionUtils.isEmpty(
				scQtRecommend.getAwardSupplierList())) {
			setRecommendedSupplierList(scQtRecommend);
		}
	}

	/**
	 * Sets the recommended supplier list.
	 *
	 * @param scQtRecommend the new recommended supplier list
	 */
	private void setRecommendedSupplierList(ScQtRecommend scQtRecommend) {
		boolean isSuccessful = false;
		List<ScQtEvaluationResultDtl> shortlistedSupplierList = null;
		boolean isItemized = this.getManagedBean().isItemized();
		
		if (CollectionUtils.isEmpty(
				scQtRecommend.getShortListedSupplierList())) {
			
			// Retrieve the suppliers short listed for the selected item
			SuppFinalizationResDTO resDTO = null;
			if (isItemized) {
				resDTO = this.getShortListedSuppliersByItem(scQtRecommend);	
				shortlistedSupplierList = resDTO.getQualifiedByItemSuppliers();
			} else {
				resDTO = this.getShortListedSuppliersByPkg(
									scQtRecommend.getTplSpecZoneDtl());
				shortlistedSupplierList = resDTO.getQualifiedByPackageSuppliers();
			}
			
			isSuccessful = resDTO.isSuccessful();
			scQtRecommend.setShortListedSupplierList(shortlistedSupplierList);
		} else {
			isSuccessful = true;
			shortlistedSupplierList = scQtRecommend
					.getShortListedSupplierList();
		}
		if (isSuccessful) {
			
			// set the checkbox or radio button of supplier selected
			// Multiple LOI or Online Bidding
			Map<Long,ScQtRecommendDtl> recommendDtlsMap = new HashMap<Long,ScQtRecommendDtl>();
			for (ScQtRecommendDtl scQtRecommendDtl 
										: scQtRecommend.getRecommendDtls()) {
				
				// Skip the rejected suppliers
				if (BooleanUtils.isTrue(
						scQtRecommendDtl.getLoiAwarded())) {
					Long supplierId = scQtRecommendDtl.getSupplier().getSupplierId();
					recommendDtlsMap.put(supplierId, scQtRecommendDtl);
				}
			}
			scQtRecommend.setAwardSupplierList(
							new ArrayList<ScQtEvaluationResultDtl>());
			if (CollectionUtils.isNotEmpty(shortlistedSupplierList)) {
				for (ScQtEvaluationResultDtl evaluationResultDtl : shortlistedSupplierList) {
					Long supplierId = evaluationResultDtl.getQtEvaluationResult()
							.getScQtProposal().getScQtSupplier().getSmSupplier()
							.getSupplierId();
					if (recommendDtlsMap.containsKey(supplierId)) {
						if (evaluationResultDtl.getScQtRecommendDtl() == null) {
							evaluationResultDtl.setScQtRecommendDtl(
										new ArrayList<ScQtRecommendDtl>());
						}
						evaluationResultDtl.getScQtRecommendDtl()
							.add(recommendDtlsMap.get(supplierId));						
						
						scQtRecommend.getAwardSupplierList()
									 .add(evaluationResultDtl);
						
						if (isItemized) {
							calculateOfferedPrice(
									evaluationResultDtl,scQtRecommend.getRequestItem());	
						} 
					}
				}				
			}			
		}
		
	}

	/**
	 * Calculate offered price.
	 *
	 * @param evalResultDtl the eval result dtl
	 * @param requestItem the request item
	 * @return the big decimal
	 */
	private BigDecimal calculateOfferedPrice(ScQtEvaluationResultDtl evalResultDtl,
			ScRequestItem requestItem) {
		BigDecimal offeredPrice = BigDecimal.valueOf(0d);
		
		ScQtProposalItem proposalItem = null;
		ScQtRecommend recommend = null;
		BigDecimal awardQuantity = null;
		
		List<ScQtRecommendDtl> recommendDtls = evalResultDtl.getScQtRecommendDtl();
		for (ScQtRecommendDtl recommendDtl : recommendDtls) {
			if (recommendDtl.getScQtRecommend().getRequestItem()
					.getItemName().equalsIgnoreCase(requestItem.getItemName())) {
					
				proposalItem = recommendDtl.getScQtProposalItem();
				recommend = recommendDtl.getScQtRecommend();
				awardQuantity = recommendDtl.getAwardQty();
				break;
			}
		}

		if (proposalItem != null && recommend != null) {
			if (awardQuantity == null) {
				awardQuantity = recommend.getOrderQty();
			}			
			awardQuantity = awardQuantity.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			
			BigDecimal finalPrice = proposalItem.getFinalPrice();
			finalPrice = finalPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			
			offeredPrice = TemplateSpecHelper.getInstance()
					.calculateTotalAmount(requestItem.getTplSpecQuestion(),
							finalPrice, awardQuantity);
			
			if (offeredPrice != null) {
				offeredPrice = offeredPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			}
			evalResultDtl.getQtEvaluationResult().setOfferedPrice(offeredPrice);
		}

		return offeredPrice;
	}

	/**
	 * Gets the short listed suppliers by pkg.
	 *
	 * @param tplSpecZoneDtl the tpl spec zone dtl
	 * @return the short listed suppliers by pkg
	 */
	private SuppFinalizationResDTO getShortListedSuppliersByPkg(TplSpecZoneDtl tplSpecZoneDtl) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setScQtFinalization(this.getManagedBean().getScQtFinalization());
			reqDTO.setTplSpecZoneDtl(tplSpecZoneDtl);

			resDTO = qtSuppFinalizationSO
						.retrievePackagedShortlistedSupplierInfo(reqDTO);
		} finally {
			qtSuppFinalizationSO = null;
		}

		return resDTO;
	}
	
	/**
	 * Gets the short listed suppliers by item.
	 *
	 * @param scQtRecommend the sc qt recommend
	 * @return the short listed suppliers by item
	 */
	private SuppFinalizationResDTO getShortListedSuppliersByItem(
			ScQtRecommend scQtRecommend) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			ScQt scQt = this.getManagedBean().getScQt();
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setScQtFinalization(scQtFinalization);
			
			if (scQt.isZonal() && 
					scQtRecommend.getTplSpecZoneDtl() != null) {
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
	 * Set the selected supplier details in managed bean associated with the
	 * current user session
	 * 
	 * @param scQtRecommend
	 *            the scQtRecommend which holds the item
	 * @param selectedRecommendDtl
	 *            the selectedRecommendDtl while holds the recommended supplier
	 */
	public void showSupplierDetails(ScQtEvaluationResultDtl supplierDtl) {
		debugLogger("showSuppDetails");

		resetCompanyInfo();

		if (supplierDtl != null) {
			SmSupplier smSupplier = supplierDtl
					.getQtEvaluationResult().getScQtProposal()
					.getScQtSupplier().getSmSupplier();
			if (smSupplier != null) {
				QTSupplierResDTO resDTO = this
						.retrieveSupplierDetails(smSupplier);
				smSupplier = resDTO.getSmSupplier();
				getManagedBean().setSmSupplier(smSupplier);
				getManagedBean().setSmMofAccount(
						smSupplier.getSmMofAccounts().get(0));

				SmCompanyBasic smCompanyBasic = smSupplier.getSmAppl()
						.getSmCompanyBasic();
				getManagedBean().setSmCompanyBasic(smCompanyBasic);

				List<SmPersonnel> smPersonnelList = smSupplier.getSmAppl()
						.getSmPersonnels();
				for (SmPersonnel smPersonnel : smPersonnelList) {
					if (smPersonnel.isContactPerson()) {
						getManagedBean().setSmPersonnel(smPersonnel);
						break;
					}
				}
				for (SmCompanyAddress smCompanyAddress : smCompanyBasic
						.getSmCompanyAddresses()) {
					if (AddressType.CONTACT_ADDRESS.value().equals(
							smCompanyAddress.getAddressType())) {
						getManagedBean().setSupplierBranch(
								QTConstants.SUPPLIER_INFO_HQ);
						getManagedBean().setSupplierAddress(
								smCompanyAddress.getSmAddress());
						break;
					}
				}
			}
		}
	}	
	
	private QTSupplierResDTO retrieveSupplierDetails(SmSupplier smSupplier) {
		QTSupplierResDTO resDTO = new QTSupplierResDTO();
		IQTSupplierSO qtSupplierSO = null;
		try {
			qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSupplierSO.class);
			QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
			reqDTO.setSupplierId(smSupplier.getSupplierId());
			resDTO = qtSupplierSO.retrieveSupplierDetails(reqDTO);
		} finally {
			qtSupplierSO = null;
		}
		return resDTO;
	}

	/**
	 * Award supplier event.
	 */
	public void awardSupplierEvent() {
		Long awardSupplierId = getManagedBean().getAwardSupplierId();
		List<ScQtRecommend> selectedRecommendList = getManagedBean()
				.getSelectedRecommendList();

		if (selectedRecommendList != null && awardSupplierId != null) {
			for (ScQtRecommend scQtRecommend : selectedRecommendList) {
				for (ScQtRecommendDtl scQtRecommendDtl 
							: scQtRecommend.getRecommendDtls()) {
					Long supplierId = scQtRecommendDtl.getSupplier()
							.getSupplierId();

					if (awardSupplierId.equals(supplierId)) {
						scQtRecommendDtl.setRecordStatus(true);
						recommendSupplierSelectedAction(scQtRecommend, scQtRecommendDtl);
					}
				}
			}
		}
	}

	/**
	 * Calc sub total price. calculating subtotal for item list in award
	 * details.
	 * 
	 * @param scLoiLoaItem
	 *            the sc loi loa item
	 * @return the big decimal
	 */
	public BigDecimal calcSubTotalPrice(ScLoiLoaItem scLoiLoaItem) {
		BigDecimal total = TemplateSpecHelper.getInstance().calculateTotalAmount(
				scLoiLoaItem.getScRequestItem().getTplSpecQuestion(),
				scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());

		/*
		 * BigDecimal total = scLoiLoaItem.getUnitPrice();
		 * 
		 * if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
		 * .getClUomFrequency() != null) { total =
		 * total.multiply(scLoiLoaItem.getScRequestItem()
		 * .getTplSpecQuestion().getClUomFrequency()); }
		 * 
		 * if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
		 * .getClUomPerDay() != null) { total =
		 * total.multiply(scLoiLoaItem.getScRequestItem()
		 * .getTplSpecQuestion().getClUomPerDay()); }
		 * 
		 * if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
		 * .getClUomPerMonth() != null) { total =
		 * total.multiply(scLoiLoaItem.getScRequestItem()
		 * .getTplSpecQuestion().getClUomPerMonth()); }
		 * 
		 * if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
		 * .getClDurationMonth() != null) { total =
		 * total.multiply(scLoiLoaItem.getScRequestItem()
		 * .getTplSpecQuestion().getClDurationMonth()); }
		 * 
		 * total = total.multiply(scLoiLoaItem.getQty());
		 */

		return total;
	}

	/**
	 * Gpki sign action. Authenticating gpki before update bpm.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Spki sign action.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void spkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Gpki sign multi loi action. Authenticating gpki before update bpm.
	 * 
	 * @param digiSign
	 *            the digi sign
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
	 * @param pmDigiSign
	 *            the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Gets the text before sign. prepare sign in data for gpki authentication.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if (getManagedBean().getScLoi() != null) {
			sb.append(getManagedBean().getScLoi().getLoiNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 * 
	 * @param textBeforeSign
	 *            the new text before sign
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
	 * @param flag
	 *            the new flag
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	/**
	 * Reset company info.
	 */
	private void resetCompanyInfo() {
		this.getManagedBean().setSmSupplier(null);
		this.getManagedBean().setSmMofAccount(null);
		this.getManagedBean().setSmCompanyBasic(null);
		this.getManagedBean().setSmPersonnel(null);
		this.getManagedBean().setSupplierBranch(null);
		this.getManagedBean().setSupplierAddress(null);
	}
	
	/**
	 * Update pending loi loas.
	 *
	 * @param recSupplierList the rec supplier list
	 * @param docType the doc type
	 * @return true, if successful
	 */
	private boolean updatePendingLoiLoas(List<ScLoiLoa> recSupplierList, String docType){
		boolean isLoiRequired = Boolean.FALSE;
		boolean isLoiLoasUpdate = Boolean.FALSE;
		for (ScLoiLoa scLoiLoa : recSupplierList) {
			if(null == scLoiLoa.getLoiRequired() || !scLoiLoa.isLoiRequiredDisabled()){
				isLoiRequired = Boolean.TRUE;
				break;
			}
		}

		if (!ParameterConst.RECOMMEND_BY_MULTIPLE_LOI.equals(docType) && isLoiRequired) {
			isLoiLoasUpdate = Boolean.TRUE;
		}
		return isLoiLoasUpdate;
	}
	

	/**
	 * Disable is loi required.
	 *
	 * @param recSupplierList the rec supplier list
	 */
	/*
	 * private void disableIsLoiRequired(List<ScLoiLoa> recSupplierList){
	 * boolean isRequiredSubmitBtn = Boolean.TRUE; for (ScLoiLoa scLoiLoa :
	 * recSupplierList) {
	 * 
	 * if(null == scLoiLoa.getLoiRequired()){
	 * scLoiLoa.setLoiRequiredDisabled(Boolean.FALSE); } else{
	 * scLoiLoa.setLoiRequiredDisabled(Boolean.TRUE); }
	 * 
	 * if (null != scLoiLoa.getLoiRequired()) { if(scLoiLoa.getLoiRequired() &&
	 * scLoiLoa.isLoiLoaActive()){ isRequiredSubmitBtn = Boolean.FALSE; }
	 * 
	 * } else{ isRequiredSubmitBtn = Boolean.FALSE; } }
	 * 
	 * if (isRequiredSubmitBtn) { getManagedBean().setLoiRequired(Boolean.TRUE);
	 * } else { getManagedBean().setLoiRequired(Boolean.FALSE); } }
	 */
	
	/**
	 * Validate deduction payment dtls.
	 *
	 * @param scLoi the sc loi
	 */
	public void validateOthersDeductionPay(ScLoi scLoi) {
		
		validateDeductionPaymentDtls(scLoi.getScQtDeductions());
	}
	
	/**
	 * Gets the qt proposal valid expired message.
	 *
	 * @param scQt the sc qt
	 * @return the qt proposal valid expired message
	 */
	private void getQtProposalValidExpiredMessage(ScQt scQt){
		if(scQt != null && scQt.getProposalValidityEndDate() != null){
			if(!checkProposalValidityDateReach(scQt.getProposalValidityEndDate())){
				StringBuilder qtProposalExpiredMsg = new StringBuilder();
				SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
				String proposalValidityEndDate = sdf.format(scQt.getProposalValidityEndDate());
				Locale locale = new FacesScopes().getUserLocale();
				qtProposalExpiredMsg.append(ResourceBundleUtils.getMessage("lbl.sc.qt.proposal.val.expired",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
				qtProposalExpiredMsg.append(" ");
				qtProposalExpiredMsg.append(proposalValidityEndDate);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
								.getMessage(
										qtProposalExpiredMsg.toString(),
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										locale)));
			}
		}
	}

	
	/**
	 * The Class NegotiationRecommendationReportDTO.
	 */
	public class NegotiationRecommendationReportDTO {
		private List<ScQtEvaluationResultDtl> awardedSuppliers;
		private List<ScQtRecommend> recommendedItems;
		private List<ScLoiLoaItem> loiLoaItems;
		private String afterNegoSubTotal;
		private boolean empty;
		
		public NegotiationRecommendationReportDTO() {}
		
		/**
		 * @return the awardedSuppliers
		 */
		public List<ScQtEvaluationResultDtl> getAwardedSuppliers() {
			return awardedSuppliers;
		}

		/**
		 * @param awardedSuppliers 
		 * 				the awardedSuppliers to set
		 */
		public void setAwardedSuppliers(
						List<ScQtEvaluationResultDtl> awardedSuppliers) {
			this.awardedSuppliers = awardedSuppliers;
		}

		/**
		 * @return the recommendedItems
		 */
		public List<ScQtRecommend> getRecommendedItems() {
			return recommendedItems;
		}
		
		/**
		 * @param recommendedItems 
		 * 				the recommendedItems to set
		 */
		public void setRecommendedItems(List<ScQtRecommend> recommendedItems) {
			this.recommendedItems = recommendedItems;
		}

		/**
		 * @return the loiLoaItems
		 */
		public List<ScLoiLoaItem> getLoiLoaItems() {
			return loiLoaItems;
		}

		/**
		 * @param loiLoaItems 
		 * 				the loiLoaItems to set
		 */
		public void setLoiLoaItems(List<ScLoiLoaItem> loiLoaItems) {
			this.loiLoaItems = loiLoaItems;
		}

		/**
		 * @return the afterNegoSubTotal
		 */
		public String getAfterNegoSubTotal() {
			return afterNegoSubTotal;
		}

		/**
		 * @param afterNegoSubTotal 
		 * 				the afterNegoSubTotal to set
		 */
		public void setAfterNegoSubTotal(String afterNegoSubTotal) {
			this.afterNegoSubTotal = afterNegoSubTotal;
		}

		/**
		 * @return the empty
		 */
		public boolean isEmpty() {
			return empty;
		}

		/**
		 * @param empty the empty to set
		 */
		public void setEmpty(boolean empty) {
			this.empty = empty;
		}
		
	}
	
	
	public  boolean deletebuttonNegotationResultDisable(
			List<ScLoiTerm> list) {
		if (list != null) {
			for (ScLoiTerm entity : list) {
				if(entity.isSelectedTerm()){
					return false;
				}
				
			}
		}
		return true;
	}

	public String retrivemessage(Date date){
		String message="";
		if(null!=date){
			long dayBal = DateUtils.getDaysDifference(DateUtils.getCurrentDateWithoutTime(),
					date);
			String[] dayLeftParam = { dayBal + "" };
			 message=SmCommonUtil.getMessage(
					"lbl.sc.loiloa.loi.res.due.date.tooltip", dayLeftParam);
			
		}
		return message;
		
	}
	
	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
	}
	
	/**
	 * Checks if is loi required disabled.
	 *
	 * @param scLoiLoa the sc loi loa
	 * @return true, if is loi required disabled
	 */
	public boolean isLoiRequiredDisabled(ScLoiLoa scLoiLoa){
		if(CollectionUtils.isNotEmpty(scLoiLoa.getScLois())){
			ScLoi scLoi =  scLoiLoa.getScLois().get(0);
			if(scLoi.getScWorkFlowStatus() != null){
				Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId()
						.longValue();
				
				if(QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId) ||
						QTStatusConst.LOI_PENDING_APPROVAL.equals(statusId)){
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
}
