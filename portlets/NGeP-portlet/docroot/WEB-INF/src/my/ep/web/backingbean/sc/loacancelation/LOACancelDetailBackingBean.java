package my.ep.web.backingbean.sc.loacancelation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.bean.application.NgepApplicationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.virtualcert.IVirtualCertSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaCancelAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScPbAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.loa.*;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loa.LOADetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

// Auto-generated Javadoc
/**
 * The Class LOACancelDetailBackingBean.
 *
 * @author Viswanath
 * 
 * @author Sri
 */
@ManagedBean(name = "lOACancelDetailBackingBean")
@RequestScoped
public class LOACancelDetailBackingBean extends LOABaseBackingBean<LOADetailManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The log. */
	public static Logger log = EpLogger.getLogger(LOACancelDetailBackingBean.class);

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/** The text before sign. */
	private String textBeforeSign;

	/** The flag. */
	private boolean flag;
	private boolean sqExpiredMessageFlag = false;

	private static final String  BRANCH_INFO_TITLE = "Branch Information";

	private static final String MALAY_NA = "T/B";

	/**
	 * Instantiates a new LOA detail backing bean.
	 */
	public LOACancelDetailBackingBean() {
		log.info("inside LOACancelDetailBackingBean constructor");
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
	 * Pre render page navigation LOA cancelation screen to loa page based on task id
	 * @return void
	 */
	public void loaCancelApprovalPreRenderViewLoa() {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		debugLogger("loaCancelApprovalPreRenderViewLoa");
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
            log.info("SCAwardSupplierDataType     "+dpDataType);
            log.info(" Source Id   "+dpDataType.getSourceId());
            log.info(" Award Id    "+dpDataType.getAwardId());
			Long sourceId = Long.valueOf(dpDataType.getSourceId());			
			Long loaId = Long.valueOf(dpDataType.getAwardId()!=null?dpDataType.getAwardId():0);       
			getManagedBean().setPayLoad(dpDataType);
			String language = new FacesScopes().getUserLocale().getLanguage();
			
			//hardcode***********
//			SCAwardSupplierDataType awardDataType  = new SCAwardSupplierDataType();		
//			awardDataType.setTitle("WNM 21423 - Scenario no 7");
//			awardDataType.setSupplierId("36627");
//			awardDataType.setApprover("580401035583");
//			awardDataType.setLetterCreator("580401035583");
//			awardDataType.setAwardId(91827L);
//			awardDataType.setDeskOfficer("580401035583");
//			awardDataType.setSourceNumber("33333");
//			awardDataType.setBidNumber("2222");
//			awardDataType.setAwardType("FRB");
//			awardDataType.setSourceId(15517L);
//			getManagedBean().setPayLoad(awardDataType);	
//	
//            Long sourceId = 15517L;			
//			Long loaId = 91827L;
//			getManagedBean().setTaskId("6ad05efe-d799-4e58-a30b-f9643f0cbffd");
			//hardcode*************
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);
			AwardSupplierResDTO resDT = getQtAwardSO()
					.retrieveLOADtlByLoaId(reqDTO);
			
			reqDTO.setScLoa(resDT.getScLoa());
			reqDTO.setLanguageCode(language);
			
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOADtl(reqDTO);
			ScLoa scLoa = resDTO.getScLoa();

			resDTO.setScLoa(scLoa);

			populateLOADetails(resDTO);

			
			getManagedBean().setScLoa(scLoa);
			getManagedBean().setScQt(resDTO.getScQt());

			
			//set approval	
			ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
			PmUser approverUser = null;
			if (scLoaApproval != null) {
				approverUser = scLoaApproval.getApprover();
			}
			if (approverUser == null) { 
				approverUser = new PmUser();
			}
			getManagedBean().setApproverUser(approverUser);
					
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setRoleCode(getUserRoleCode(scLoa.getScLoiLoa().getDocType()));
			userReqDTO.setOrgProfileId(scLoa.getScLoiLoa().getPmOrgProfile().getOrgProfileId());
			
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
			getManagedBean().setApproverList(userResDTO.getUserList());	
			
			getManagedBean().setScLoa(scLoa);
			getManagedBean().setScQt(scLoa.getScQt());			
			
			getManagedBean().setSourceId(sourceId);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.LOA_CANCEL_APPROVAL_PAGE);

		} finally{

		}
	}
	

	public PmOrgProfile retrieveMinistryOrgProfile(ScLoa scLoa) {
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();

		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(scLoiLoa.getPmOrgProfile().getOrgProfileId());

		PmOrgProfile ministryOrgProfile = getOrgProfileSOGlobal().retrieveMinistryByOrgProfileId(orgProfileReqDTO)
				.getPmOrgProfile();
		return ministryOrgProfile;
	}
	
	/**
	 * Cancel loa.
	 *
	 * @param loaId the loa id
	 */
	public void cancelationLOA(long loaId) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		debugLogger("cancelationLOA");
		try {

			SCAwardSupplierDataType dpDataType = new SCAwardSupplierDataType();							
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);
			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrieveLOADtlByLoaId(reqDTO);
			
			String currentUser = new FacesScopes().getUserSessionBean().getLoginId();
			String letterCreator = null;
			ScLoa scLoa = resDTO.getScLoa();
			
			getManagedBean().setPayLoad(dpDataType);	
			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoad();

			if (awardDataType != null) 
				letterCreator = awardDataType.getLetterCreator();
				
				if (scLoa != null && ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType())) {
					if (StringUtils.isBlank(letterCreator)) {
						if (new FacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())) {
							awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
							awardDataType.setDeskOfficer(currentUser);
						} else {
							awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_REQUISITIONER);
							awardDataType.setRequisitioner(currentUser);
						}
					} else {
						if (letterCreator.equals(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER)) {
							awardDataType.setDeskOfficer(currentUser);
						} else {
							awardDataType.setRequisitioner(currentUser);
						}
					}

				} else {
					if (StringUtils.isBlank(letterCreator)) {
						if (new FacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())) {
							awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
							awardDataType.setDeskOfficer(currentUser);
						} else {
							awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_SECRETARY);
							awardDataType.setSecretary(currentUser);
						}
					} else {
						if (letterCreator.equals(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER)) {
							awardDataType.setDeskOfficer(currentUser);
						} else {
							awardDataType.setSecretary(currentUser);
						}
					}
				}
			
			getManagedBean().setPayLoad(awardDataType);
			
			String language = new FacesScopes().getUserLocale().getLanguage();

			AwardSupplierReqDTO reqDTO2 = new AwardSupplierReqDTO();
			reqDTO2.setScLoa(scLoa);
			reqDTO2.setLanguageCode(language);			
			
			AwardSupplierResDTO resDTO2 = getQtAwardSO().retrieveLOADtl(reqDTO2);
			ScLoa scLoa2 = resDTO2.getScLoa();
			
			resDTO2.setScLoa(scLoa2);
			
			ScLoa scLoa3 = resDTO2.getScLoa();
			getManagedBean().setScLoa(scLoa3);
			getManagedBean().setScQt(resDTO2.getScQt());

			
			// Remarks
			scLoa3.setScLoaRemarks(initializeRemarks(scLoa3, 
					scLoa3.getCurrentStatus(),
					scLoa3.getScLoaRemarks(), ScLoaRemark.class));
			
			//set approval	
			ScLoaApproval scLoaApproval = scLoa3.getScLoaApproval();
			PmUser approverUser = null;
			if (scLoaApproval != null) {
				approverUser = scLoaApproval.getApprover();
			}
			if (approverUser == null) { 
				approverUser = new PmUser();
				
				AwardSupplierResDTO resGetRNApprover  = getQtAwardSO().retrieveRequestNoteApproverByLoa(reqDTO2);
				approverUser = resGetRNApprover.getApproverList().get(0);
				
			}
			getManagedBean().setApproverUser(approverUser);
					
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setRoleCode(RoleCode.RN_APPROVER.value());
			userReqDTO.setOrgProfileId(scLoa3.getScLoiLoa().getPmOrgProfile().getOrgProfileId());
			
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
			getManagedBean().setApproverList(userResDTO.getUserList());	
			
			FacesContext fc = FacesContext.getCurrentInstance();
			
			if(getManagedBean().isDpMaintenanceSource()){
			getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			}
			
			getManagedBean().setSourcePage(fc.getViewRoot().getViewId());
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.LOA_CANCEL_MAIN_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * @ param Date date
	 * @return days of month
	 */
	private	static	String	getDays(Date date){
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		String	days	=	String.valueOf(c1.get(Calendar.DAY_OF_MONTH));
		return days;
	}

	/**
	 * Populate loa details.
	 *
	 * @param resDTO the res dto
	 */
	private void populateLOADetails(AwardSupplierResDTO resDTO) {
		ScLoa scLoa = resDTO.getScLoa();
		getManagedBean().setScLoa(scLoa);
		getManagedBean().setScQt(resDTO.getScQt());

		BigDecimal overallCtAmout = overallCtAmountWithSST(scLoa.getLoaAmt(), scLoa.getSstAmt());
		log.info(":::populateLOADetails() overallCtAmout (RM) = "+ overallCtAmout);
		//end Enhancement #24259 #24269
		getManagedBean().setOverallContractAmt(overallCtAmout);
		scLoa.setOverallContractAmt(overallCtAmout);

		// Procurement Type Category Id
		if(null == scLoa.getProcurementTypeCatId()){
			scLoa.setProcurementTypeCatId(new PmParameter());
		}

		SmPersonnel supplierHQPersonnel = resDTO.getSupplierHQPersonnel();
		if(null == supplierHQPersonnel){
			throw new EpException(MessageConst.ERROR_760294);
		} else{
			getManagedBean().setSupplierHQPersonnel(supplierHQPersonnel); 
		}
		getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());

		// if loi created , copy supplier branch dtl from Loi to LOA
		if(scLoa.getScLoiLoa().getScLois() != null 
				&& scLoa.getScLoiLoa().getScLois().size()>0){
			scLoa.setSupplierBranch(scLoa.getScLoiLoa().getScLois().get(0).getSupplierBranch());
			scLoa.setLoiCreated(Boolean.TRUE);
		}

		// Supplier Witness
		ScLoaWitness loaSupplierWitness = null;
		if(scLoa.getScLoaWitnesses() != null){
			for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
				String partyType = scLoaWitness.getPartyType();
				if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER
						.equals(partyType)) {
					loaSupplierWitness = scLoaWitness;
				}
			}
		}
		getManagedBean().setLoaSupplierWitness(loaSupplierWitness);

		// Acknowledge Date
		if (QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa
				.getPmStatus().getStatusId().longValue()) ||
				QTStatusConst.LOA_PENDING_REVISION.equals(scLoa
						.getPmStatus().getStatusId().longValue())) {
			Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOA_RESPONSE_PERIOD);
			scLoa.setAckDueDate(DateUtils.addDays(DateUtils.getCurrentDateWithoutTime(), loaRespPeriod));
		}

		// check whether loa from s1 or s2.
		if(scLoa.getScLoiLoa() != null){
			if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
				if (getManagedBean().getScQt() != null) {
					if (("P").equals(getManagedBean().getScQt().getScheduleType())) {
						// progressive payment.
						formProgressivePayment(scLoa);
					} else if (("F").equals(getManagedBean().getScQt()
							.getScheduleType())) {
						// Fulfilment Schedule
						formFulfilmentSchedule(scLoa);
					}
				}
			} else{
				// for s1
				// Fulfilment Schedule
				formFulfilmentSchedule(scLoa);
			}
		}

		// show default clause
		if(scLoa.getScQtDeductions() != null && scLoa.getScQtDeductions().size()>0){
			getManagedBean().setSelectedScQtDeduction(scLoa.getScQtDeductions().get(0));
		}

		// Contract Signer
		prepareSignerInfo();

		// Approver
		ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
		PmUser approverUser = null;
		if (scLoaApproval != null) {
			approverUser = scLoaApproval.getApprover();

			if (!QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa
					.getPmStatus().getStatusId().longValue())) {
				getManagedBean().setScLoaApproval(scLoa.getScLoaApproval());
			}
		}
		if (approverUser == null) {
			approverUser = new PmUser();
		}
		getManagedBean().setApproverUser(approverUser);
		getManagedBean().setApproverList(resDTO.getApproverList());

		// Remarks
		scLoa.setScLoaRemarks(initializeRemarks(scLoa, 
				scLoa.getCurrentStatus(),
				scLoa.getScLoaRemarks(), ScLoaRemark.class));

		// check agreement
		checkAgreementMandatory();

		// check performance bond
		checkPerformanceBondMandatory();
		
		//Enhancement #24259 #24269
//		scLoa = calcBondAmount(scLoa, false);
		scLoa = calcBondAmountWithSST(scLoa, false);
		
		if (scLoa.getBondAmt() != null) {
			getManagedBean().setBondAmountWord(
					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		}
		
		
		//awarded supplier and proposal
		if(resDTO.getSupplierProposal()!= null){
			getManagedBean().setScQtProposal(resDTO.getSupplierProposal());
		}

		// clauses title
		getClausesTitle(null);

		//Filter skop kerja dan perkhidmatan repo.
		filterSkopKerjaDanPerkhidmatanRepo(scLoa);
	}

	/**
	 * Form fulfilment schedule.
	 *
	 * @param scLoa the sc loa
	 */
	private void formFulfilmentSchedule(ScLoa scLoa){
		if(null != scLoa.getScQtSchedules() && scLoa.getScQtSchedules().size()>0){
			this.getManagedBean().setTotalFrequency(scLoa.getScQtSchedules().size());
			try {
				for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
					int months;
					Date startDate = null;
					Date endDate = null;
					String fromdate = dateFormat.format(scQtSchedule.getFromDate()==null?new Date():scQtSchedule.getFromDate());
					String todate = dateFormat.format(scQtSchedule.getToDate()==null?new Date():scQtSchedule.getToDate());
					String startMonth = fromdate.substring(3, 5);
					String startYear = fromdate.substring(6, fromdate.length());
					String fromDay = fromdate.substring(0, 2);

					String endMonth = todate.substring(3, 5);
					String endYear = todate.substring(6, todate.length());
					String endDay = todate.substring(0, 2);

					int sMonth = Integer.parseInt(startMonth);
					int eMonth = Integer.parseInt(endMonth);

					String fromDate = fromDay + "/" + startMonth + "/" + startYear;
					String toDate = endDay + "/" + endMonth + "/" + endYear;

					startDate = dateFormat.parse(fromDate);
					endDate  = dateFormat.parse(toDate);

					months  = DateUtils.getMonths(startDate, endDate);
					scQtSchedule.setTotalMonths(new BigDecimal(months));

					scQtSchedule.setStartMonth(String.valueOf(sMonth));
					scQtSchedule.setEndMonth(String.valueOf(eMonth));
					scQtSchedule.setStartYear(startYear);
					scQtSchedule.setEndYear(endYear);
				}
				this.sortFrequentScheduleList(scLoa.getScQtSchedules());

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		} else{
			this.getManagedBean().setTotalFrequency(0);
		}
	}

	/**
	 * Form progressive payment.
	 *
	 * @param scLoi the sc loi
	 */
	private void formProgressivePayment(ScLoa scLoa){
		List<ScQtProgressivePayment> progressivePayment  = new ArrayList<ScQtProgressivePayment>();
		if (null != scLoa.getScQtProgPayments() && scLoa.getScQtProgPayments().size()>0) {
			this.getManagedBean().setTotalProgressivePayment(
					scLoa.getScQtProgPayments().size());
			for (ScQtProgressivePayment scQtProgressivePayment : scLoa
					.getScQtProgPayments()) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);

				String fromdate = dateFormat.format(scQtProgressivePayment.getFromDate());
				String todate = dateFormat.format(scQtProgressivePayment.getToDate());

				String startMonth = fromdate.substring(3, 5);
				String startYear = fromdate.substring(6, fromdate.length());
				String endMonth = todate.substring(3, 5);
				String endYear = todate.substring(6, todate.length());

				int sMonth = Integer.parseInt(startMonth);
				int eMonth = Integer.parseInt(endMonth);

				scQtProgressivePayment.setStartMonth(String.valueOf(sMonth));
				scQtProgressivePayment.setEndMonth(String.valueOf(eMonth));
				scQtProgressivePayment.setStartYear(startYear);
				scQtProgressivePayment.setEndYear(endYear);
				progressivePayment.add(scQtProgressivePayment);

			}
			this.sortProgressivePaymentList(progressivePayment);
			this.getManagedBean().setProgressivePaymentList(
					progressivePayment);
		}
	}

	/**
	 * Deduction type change.
	 * Deduction Clause will show when deduction type changes
	 * @param scQtDeduction the sc qt deduction
	 */
	public void deductionTypeChange(ScQtDeduction scQtDeduction) {

		debugLogger("deductionTypeChange");

		Long parameterId = scQtDeduction.getDeductionType().getParameterId();
		if (null != parameterId) {
			scQtDeduction.setDeductionType(retrieveParameterById(parameterId));
			getClausesTitle(scQtDeduction);
			scQtDeduction.setScQtDeductionClauses(null);
			getManagedBean().setSelectedScQtDeduction(scQtDeduction);
		} else {
			scQtDeduction.setDeductionType(new PmParameter());
			getManagedBean().setSelectedScQtDeduction(scQtDeduction);
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
	 * Adds the deduction payment in deduction payment.
	 */
	public void addDeductionPaymentAction() {
		debugLogger("addDeductionPaymentAction");

		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScQtDeduction> scQtDeductions = scLoa.getScQtDeductions();

		if (scQtDeductions == null) {
			scQtDeductions = new ArrayList<ScQtDeduction>();

			scLoa.setScQtDeductions(scQtDeductions);
		}

		ScQtDeduction scQtDeduction = new ScQtDeduction();
		scQtDeduction.setScLoa(scLoa);
		scQtDeduction.setDeductionType(new PmParameter());

		scQtDeductions.add(scQtDeduction);
		getManagedBean().setSelectedScQtDeduction(null);
	}

	/**
	 * Adds the deduction clause in deduction payment.
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
	 * Delete deduction payment deduction payment.
	 */
	public void deleteDeductionPaymentAction() {
		debugLogger("deleteDeductionPaymentAction");
		if (getManagedBean().getScLoa().getScQtDeductions() != null
				&& getManagedBean().getSelectedScQtDeduction() != null) {
			getManagedBean().getScLoa().getScQtDeductions()
			.remove(getManagedBean().getSelectedScQtDeduction());
		}
		getManagedBean().setSelectedScQtDeduction(null);
	}

	/**
	 * Delete deduction clause.
	 */
	public void deleteDeductionClause() {
		List<ScQtDeductionClause> result = new ArrayList<ScQtDeductionClause>();
		boolean flag = true;
		List<ScQtDeductionClause> clauses = getManagedBean().getSelectedScQtDeduction().getScQtDeductionClauses();
		if (clauses != null && clauses.size() > 0) {
			for (ScQtDeductionClause deductionClause : clauses) {
				if (!deductionClause.isSelected()) {
					result.add(deductionClause);
				} else {
					flag = false;
				}
			}
		}
		if (flag) {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean().getSelectedScQtDeduction().setScQtDeductionClauses(result);
	}

	private static String splitLongLengthString(String text, int split){
		StringBuilder sb = new StringBuilder();
		String[] array = text.split("(?<=\\G.{" + split + "})");
		int counter=1;
		for (String string : array) {
			sb.append(string);
			if(array.length != counter){
				sb.append("<br />");
				++counter;
			}
		}
		return sb.toString();
	}

	/**
	 * submit LOA details.
	 * submit - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */
	public String submitAction() {
		debugLogger("submitAction");

		try {
			
			PmParameter contractType = getManagedBean().getScLoa().getContractType();
			if(this.getManagedBean().getFlowamt()==null && null != contractType 
					&& ParameterTypeConst.CONTRACT_TYPE.equals(contractType.getParameterType()) 
					&& ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType.getParameterCode()))
			{
				throw new EpException(MessageConst.ERROR_760485);	
			}else{
				
				String outcome = BpmOutcomeConst.BPM_SUBMIT;
				ScLoa scLoa = this.getManagedBean().getScLoa();
				if((scLoa!=null) && (this.getManagedBean().getFlowamt()!=null))
				{
					scLoa.setFollowLoaAmt(this.getManagedBean().getFlowamt());
				}
				parseLOADetails(scLoa, outcome);
				validateLOADetails(scLoa, outcome);
				updateContractDetails(scLoa);
				validateContractDetails(scLoa);
	
				scLoa.setScLoaWorkFlowStatuses(null);
				// To avoid insert workflow status when direct submit without save.
				if (scLoa.isBrandNew()) {
					List<ScLoaRemark> remarks = scLoa.getScLoaRemarks();
					if (CollectionUtils.isNotEmpty(remarks)) {
						for (ScLoaRemark remark : remarks) {
							remark.setScWorkFlowStatus(null);
						}
					}	
				}
	
				AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
				reqDTO.setScLoa(scLoa);
				reqDTO.setPayLoad(getManagedBean().getPayLoad());
				reqDTO.setOutcome(outcome);
				reqDTO.setSourceId(getManagedBean().getSourceId());
				reqDTO.setRoleCode(getUserRoleCode(scLoa.getScLoiLoa().getDocType()));
				reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setUserLocale(new FacesScopes().getUserLocale());
				reqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveFreqSchedules());
				reqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgressivePayments());
				AwardSupplierResDTO resDTO = getQtAwardSO().saveLOADtl(reqDTO);
	
				if (resDTO.isSuccessful()) {
					scLoa = resDTO.getScLoa();
					RenameTempLoaNoRepoDirByLoaNo(scLoa);
					renewSesionObject();
					successMessage(MessageConst.INFO_700033, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
							scLoa.getLoaNo(), Boolean.TRUE);
				}
			}		

		} finally {
			//qtAwardSO = null;
		}

		return null;
	}

	/**
	 * cancel LOA details.
	 * cancel - update status to cancel for that data in database.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */
	public String cancelPrepareLoaAction() {
		debugLogger("cancelPrepareLoaAction");
		
		try{
			String outcome = BpmOutcomeConst.BPM_LOA_CANCEL;
			ScLoa scLoa = this.getManagedBean().getScLoa();	
			PmUser approverUser = currentApprovalSession();
			
			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoad();		
			validateRequiredApprover(approverUser,scLoa);
			validateRequiredAttachment();
			PmUser user = PmCommonUtil.retrieveUserById(approverUser.getUserId(), false);    
						
			awardDataType.setApprover(user.getLoginId());
			
			Long currentStatusId = scLoa.getPmStatus().getStatusId().longValue();
			awardDataType.setBidNumber(currentStatusId.toString());
						
			populatePayLoad(awardDataType,scLoa);			
			getManagedBean().setPayLoad(awardDataType);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setScLoa(scLoa);
			reqDTO.setOutcome(outcome);
			reqDTO.setRoleCode(getUserRoleCode(scLoa.getScLoiLoa().getDocType()));
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			reqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveFreqSchedules());
			reqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgressivePayments());
			getQtAwardSO().validateCancelLoaRequiredRemark(reqDTO);	
			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOADtl(reqDTO);
			
			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();			
				RenameTempLoaNoRepoDirByLoaNo(scLoa);
				renewSesionObject();
				successMessage(MessageConst.INFO_700037, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
						scLoa.getLoaNo(), Boolean.TRUE);
			}		
		} finally {
			//qtAwardSO = null;
		}
		
		return null;
	}
	
	
	private void validateRequiredApprover(PmUser approverUser, ScLoa scLoa) {
		if (approverUser.getUserId() != null) {
			ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();

			if (scLoaApproval == null) {
				scLoaApproval = new ScLoaApproval();
				scLoaApproval.setScLoa(scLoa);
				scLoaApproval.setApprover(approverUser);

				scLoa.setScLoaApproval(scLoaApproval);
			} else {
				scLoaApproval.setApprover(approverUser);
			}
		}
		else{
			throw new EpException(
					MessageConst.ERROR_760315,
					new String[] {ResourceBundleUtils.getMessage("lbl.sc.approverName",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}		
	}
	
	/**
	 * Update bpm task.
	 *
	 * @param outcome the outcome
	 * @param digiSign the digi sign
	 * @return AwardSupplierResDTO
	 */
	private AwardSupplierResDTO updateBPMTask(String outcome, PmDigiSign digiSign) {
		debugLogger("updateBPMTask");
		FacesScopes facesScopes = new FacesScopes();
		AwardSupplierResDTO resDTO = null;
		try {

			ScLoa scLoa = this.getManagedBean().getScLoa();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();
		
			//parseLOADetails(scLoa, outcome);
			parseApprovalDetails(scLoa, outcome);

			// if reject, remove the new witness
			if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
				ScLoaWitness scLoaWitness = null;
				Iterator<ScLoaWitness> it = scLoa.getScLoaWitnesses()
						.iterator();
				while (it.hasNext()) {
					scLoaWitness = it.next();
					if (scLoaWitness.isBrandNew()
							&& null == scLoaWitness.getWitnessName()) {
						it.remove();
					}
				}
				//Bug #24713
				if(this.getManagedBean().getLoaSupplierWitness() != null 
						&& getManagedBean().getScLoa().getScLoaWitnesses() != null) {

					getManagedBean().getScLoa().getScLoaWitnesses().remove(
							this.getManagedBean().getLoaSupplierWitness());					
					this.getManagedBean().setLoaSupplierWitness(null);
				}
			}

			if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
				// gpki for g2g seller
				if(isDigiSigningRequired(RoleCode.GOVT_SELLER.value())) {
					if (null != digiSign) { 
						updateSpkiDigiSignData(digiSign,
								ParameterConst.DOCTYPE_LETTER_OF_AWARD, scLoa.getLoaId(), scLoa.getLoaNo(), 
								prepareOriginalText(scLoa.getScLoiLoa().getSmSupplier(), Boolean.TRUE)); 
					}
					RequestContext.getCurrentInstance().execute("gpki_dlg.hide();");

				} else {

					// spki for normal supplier
					String originalText = prepareOriginalText(scLoa.getScLoiLoa().getSmSupplier(), Boolean.FALSE);
					byte[] signedData = getManagedBean().getSpkiBackingBean().spkiSignTransaction(originalText);
					digiSign.setSpkiSignedData(signedData);

					if (null != digiSign) {
						updateSpkiDigiSignData(digiSign, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
								scLoa.getLoaId(), scLoa.getLoaNo(), originalText);
					}

					RequestContext.getCurrentInstance().execute("blockWidget.hide();");
				}

				// Insert reports when supplier accept Loa
				//parseReports(scLoa);
			} else{
				// gpki for approver
				if (null != digiSign) {
					updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
							scLoa.getLoaId(), scLoa.getLoaNo());
				}
			}

			validateLOADetails(scLoa, outcome);
			scLoa.setScLoaRemarks(scLoa.getScLoaRemarks());
			
			boolean newRemark = false;
			if (scLoa != null) {
				for (ScLoaRemark remark : scLoa.getScLoaRemarks()) {
					// no new remark being insert
					if (remark.getRemark() == null
							|| remark.getRemark().isEmpty()) {
						newRemark = true;
					}
				}
			}

			if (newRemark) {
				throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.remark",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
			}

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outcome.concat(BpmOutcomeConst.BPM_LOA_CANCEL));
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setDigiSignData(digiSign);
			reqDTO.setRoleCode(getUserRoleCode(scLoa.getScLoiLoa().getDocType()));
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			reqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveFreqSchedules());
			reqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgressivePayments());
			
//			updateCancelLoaCurrentWorkflowStatus(scLoa,outcome);	
			
			if(!BpmOutcomeConst.BPM_LOA_CANCEL.equals(outcome)){
					getQtAwardSO().prepareCancelLoaTrackingDiary(reqDTO);
			}
			
			log.info("reqDTO object inside updateBPMTask " + reqDTO);
			log.info("letter creator is " + ((SCAwardSupplierDataType) reqDTO.getPayLoad()).getLetterCreator());
			log.info("loa id is " + reqDTO.getScLoa().getLoaId());
			log.info("role code is " + reqDTO.getRoleCode());
			log.info("user is " + reqDTO.getPmUser().getUserId());
			log.info("desk officer is " + ((SCAwardSupplierDataType) reqDTO.getPayLoad()).getDeskOfficer());		
			
			resDTO = getQtAwardSO().saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				
				try
				{
				    Thread.sleep(5000);
				}
				catch(InterruptedException ex)
				{
				    Thread.currentThread().interrupt();
				}
				long previousStatusId = Long.parseLong(((SCAwardSupplierDataType) reqDTO.getPayLoad()).getBidNumber());
				updateCancelLoaCurrentWorkflowStatus(scLoa,outcome,previousStatusId);
				
				String RequestNoteNo = getQtAwardSO().retrieveRequestNoteNoByLoa(reqDTO);
				scLoa = resDTO.getScLoa();								
				RenameTempLoaNoRepoDirByLoaNo(scLoa);
				renewSesionObject();
				if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700038, scLoa.getLoaNo(), RequestNoteNo, Boolean.TRUE);
				}
				else if(BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700039, scLoa.getLoaNo(), RequestNoteNo, Boolean.TRUE);
				}
				
			}

		} finally {
			//qtAwardSO = null;
		}
		return resDTO;
	}

	/**
	 * To get message common
	 * 
	 * @param name
	 * @return String instance
	 */
	public static String getMessageCommon(String name) {
		return ResourceBundleUtils.getMessage(name,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
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
		updateBPMTask(outcome, null);
	}

	/**
	 * Prepare original text.
	 *
	 * @param smSupplier the sm supplier
	 * @param isGovtSeller the is govt seller
	 * @return the string
	 */
	private String prepareOriginalText(SmSupplier smSupplier, boolean isGovtSeller) {
		/*
		 * The first one is the Government user, it is the combination of IC.No,
		 * > Organization Code and Country Code. >
		 * 0000000081102813580400000000000211602001MY > IC No. , Organization
		 * code, Country code > > The second one is for Supplier, it is the
		 * combination of IC. No, Reg No and > Country Code. >
		 * 00000000680427115037000000000KT0143162-VMY > IC No. , Registration
		 * No., Country code
		 */
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

		sb.append(getManagedBean().getScLoa().getLoaNo()).append("|")
		.append(facesScopes.getUserSessionBean().getIdentificationNo()).append("|");

		if (isGovtSeller) {
			UserReqDTO userReqDTO  = new UserReqDTO();
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			userReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			PmUser pmUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
			if(pmUser.getOrgTypeCode() != null){
				sb.append(pmUser.getOrgTypeCode().getParameterCode());
			}

		} else {
			sb.append(smSupplier.getRegNo());
		}

		sb.append("|").append(countryCode);
		orginalText  = sb.toString();
		return orginalText;
	}

	/**
	 * Validate loa details.
	 *
	 * @param scLoa the sc loa
	 * @param outcome the outcome
	 */
	private void validateLOADetails(ScLoa scLoa, String outcome) {
		Long statusId = scLoa.getPmStatus().getStatusId().longValue();

		if(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_ACCEPT.equals(outcome)) {

			// check to assign UNSPSC code 
			for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
				if(scLoiLoaItem.getScRequestItem()!= null && 
						null == scLoiLoaItem.getScRequestItem().getCmItem()){
					throw new EpException(MessageConst.ERROR_760436);
				}
			}
			//LOA CR to remove side menu
			/*if (!getManagedBean().isAgreeWithTermsConditions()) {
				throw new EpException(MessageConst.ERROR_750002);
			}*/
		}

	}
	
	private void parseApprovalDetails(ScLoa scLoa, String outcome) {
		
			ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
			String action = null;
			if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_APPROVED.value();
			} else if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REJECTED.value();
			}
			if(null != action){
				scLoaApproval.setApproverAction(action);
				scLoaApproval.setApproverActionDate(new Date());
				scLoa.setScLoaApproval(scLoaApproval);
			}
		
	}
		
	/**
	 * Parses the loa details.
	 *
	 * @param scLoa the sc loa
	 * @param outcome the outcome
	 */
	private void parseLOADetails(ScLoa scLoa, String outcome) {
		if (scLoa.isBrandNew()) {
			ScLoaWorkFlowStatus currentStatus = (ScLoaWorkFlowStatus)scLoa.getCurrentStatus();
			List<ScLoaWorkFlowStatus> workFlowStatuses = new ArrayList<ScLoaWorkFlowStatus>();
			workFlowStatuses.add(currentStatus);
			scLoa.setScLoaWorkFlowStatuses(workFlowStatuses);

			// To avoid workflow status reset to null when got error during direct submit without save.
			List<ScLoaRemark> remarks = scLoa.getScLoaRemarks();
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScLoaRemark remark : remarks) {
					remark.setScWorkFlowStatus(currentStatus);
				}
			}
		}

		scLoa.setIssueDate(scLoa.getContractEffDate());

		// Acknowledge Date
		if(BpmOutcomeConst.BPM_APPROVE.equals(outcome) || BpmOutcomeConst.BPM_SUBMIT.equals(outcome)){
			Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOA_RESPONSE_PERIOD);
			scLoa.setAckDueDate(DateUtils.addDays(DateUtils.getCurrentDateWithoutTime(), loaRespPeriod));
		}

		Long statusId = scLoa.getPmStatus().getStatusId().longValue();
		if (scLoa.isApprovalReq()
				&& QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId)) {
			PmUser approverUser = getManagedBean().getApproverUser();

			if (approverUser.getUserId() != null) {
				ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
				if (scLoaApproval == null) {
					scLoaApproval = new ScLoaApproval();
					scLoaApproval.setScLoa(scLoa);
					scLoaApproval.setApprover(approverUser);
					scLoa.setScLoaApproval(scLoaApproval);
				} else {
					scLoaApproval.setApprover(approverUser);
				}
			}
		} else if (scLoa.isApprovalReq()
				&& QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId)) {
			ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
			String action = null;
			if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_APPROVED.value();
			} else if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REJECTED.value();
			} else if (BpmOutcomeConst.BPM_REVISE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REVISED.value();
			}

			if(null != action){
				scLoaApproval.setApproverAction(action);
				scLoaApproval.setApproverActionDate(new Date());
				scLoa.setScLoaApproval(scLoaApproval);
			}

		} else if (!scLoa.isApprovalReq()) {
			scLoa.setScLoaApproval(null);
		}

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			scLoa.setAckDate(new Date());
			scLoa.setBondResDueDate(new Date());
		}  else{
			if(!(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome))){
				scLoa.setScLoaRemarks(finalizeRemarks(scLoa.getScLoaRemarks()));
			}
		}
		//20200302@asyrafhaziq_#27525 - Start
		//#25022
		// check whether loa from s1 or s2.
		if(scLoa.getScLoiLoa() != null){
			if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
				// For s2
				if (getManagedBean().getScQt() != null) {
					if (("P").equals(getManagedBean().getScQt().getScheduleType())) {
						saveProgressivePaymntList();

					} else if (("F").equals(getManagedBean().getScQt()
							.getScheduleType())) {
						saveScheduleList();
					}
				}
			} else{
				// for s1
				//saveScheduleList();
			}
		}
		//20200302@asyrafhaziq_#27525 - End

		PmUser contractSignerUser = getManagedBean().getContractSignerUser();
		if (contractSignerUser != null
				&& contractSignerUser.getUserId() != null) {
			scLoa.setContractSigner(contractSignerUser);
		}
		//20200302@asyrafhaziq_#27525 - Start
		//#25022
		//saveScheduleList();
		//20200302@asyrafhaziq_#27525 - End
	}

	/**
	 * Contract agency changed.
	 * shows Contract Administrator based on agency changes.
	 */
	public void contractAgencyChanged() {
		debugLogger("contractAgencyChanged");

		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			ScLoa scLoa = getManagedBean().getScLoa();
			String contractType = scLoa.getContractType().getParameterCode();
			List<PmUser> userList = null;

			if (scLoa.getContractAgency() != null) {
				if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());
					reqDTO.setOrgProfileId(scLoa.getContractAgency().getOrgProfileId());
					UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
					userList = resDTO.getUserList();

				} else if (ParameterConst.CONTRACT_TYPE_CENTRAL
						.equals(contractType)) {
					// filter user based on central contract.
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setRoleCode(RoleCode.BPK_CCA.value());
					reqDTO.setOrgProfileId(
							scLoa.getContractAgency().getOrgProfileId());

					UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);

					userList = resDTO.getUserList();
				}
			}

			getManagedBean().setContractAdminList(userList);

		} finally {
			userSO = null;
		}
	}

	/**
	 * Contract admin changed.
	 */
	public void contractAdminChanged() {
		debugLogger("contractAdminChanged");

		IUserSO userSO;
		ScLoa scLoa = getManagedBean().getScLoa();
		PmUser pmUser = scLoa.getContractAdmin();

		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(pmUser.getUserId());

			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
			scLoa.setContractAdmin(resDTO.getPmUser());

		} finally {
			userSO = null;
		}
	}

	/**
	 * Contract eff date changed.
	 *  calculate Completion Expiry Date based on Completion Period.
	 */
	public void contractEffDateChanged() {
		debugLogger("contractEffDateChanged");

		ScLoa scLoa = getManagedBean().getScLoa();
		Date contractEffDate = scLoa.getContractEffDate();
		BigDecimal contractDuration = scLoa.getContractDuration();

		Calendar contractExpDate = Calendar.getInstance();
		contractExpDate.setTime(contractEffDate);
		contractExpDate.add(Calendar.MONTH, contractDuration.intValue());
		contractExpDate.add(Calendar.DATE, -1);

		scLoa.setContractExpDate(contractExpDate.getTime());
	}

	/**
	 * Approver changed.
	 */
	public void approverChanged() {
		debugLogger("approverChanged");

		PmUser approverUser = getManagedBean().getApproverUser();

		IUserSO userSO = null;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(approverUser.getUserId());

			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
			getManagedBean().setApproverUser(resDTO.getPmUser());

		} finally {
			userSO = null;
		}

	}

	/**
	 * Contract signer event.
	 */
	public void contractSignerEvent() {
		debugLogger("contractSignerEvent");
		PmUser contractSigner = getManagedBean().getContractSignerUser();
		if(contractSigner != null){
			if(getManagedBean().getContractSignerList() != null && getManagedBean().getContractSignerList().size()>0 ){
				List<PmUser> contractSignerList  = getManagedBean().getContractSignerList();
				for (PmUser user : contractSignerList) {

					if(contractSigner.getUserId().equals(user.getUserId())){
						getManagedBean().getScLoa().setLoaSignerDesig(user.getDesignation());
					}
				}
			}
		}
	}

	/**
	 * Supplier Contract signer event.
	 */
	public void supplierContractSignerEvent() {
		debugLogger("supplierContractSignerEvent");
		PmUser supplierContractSigner = getManagedBean().getScLoa().getAckBy();
		IUserSO userSO = null;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			if (supplierContractSigner != null) {
				if (getManagedBean().getSupplierContractSignerList() != null
						&& getManagedBean().getSupplierContractSignerList().size() > 0) {
					List<PmUser> supplierContractSignerList = getManagedBean()
							.getSupplierContractSignerList();
					for (PmUser user : supplierContractSignerList) {
						if (user.getUserId().equals(supplierContractSigner.getUserId())) {
							UserReqDTO reqDTO = new UserReqDTO();
							reqDTO.setUserId(user.getUserId());
							UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
							getManagedBean().getScLoa().setAckBy(resDTO.getPmUser());
							break;
						}
					} 
				}
			} 
		} finally {
			userSO = null;
		}
	}

	/**
	 * Adds the schedule details in Fulfillment Schedule.
	 */
	public void addScheduleDetails() {
		IParameterSO parameterSo = null;
		try {
			if(this.getManagedBean().getScLoa()
					.getScQtSchedules()==null){
				this.getManagedBean().getScLoa().setScQtSchedules(new ArrayList<ScQtSchedule>());
			}
			List<ScQtSchedule> freqSchedules = this.getManagedBean().getScLoa()
					.getScQtSchedules();
			
			parameterSo = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			// int year = Calendar.getInstance().get(Calendar.YEAR);
			ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
			docDefTypeReqDTO.setParameterType(ParameterTypeConst.SCHEDULE_TYPE);
			docDefTypeReqDTO
					.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
			ParameterResDTO docDefTypeResDTO = parameterSo
					.retrieveParameterByTypeAndCode(docDefTypeReqDTO);

				ScQtSchedule schedule = new ScQtSchedule();
				schedule.setScQt(this.getManagedBean().getScQt());
				schedule.setScLoa(this.getManagedBean().getScLoa());
				schedule.setScheduleType(docDefTypeResDTO.getPmParameter());

				freqSchedules.add(schedule);
			this.getManagedBean().getScLoa().setScQtSchedules(freqSchedules);
		} 
		finally {
			parameterSo = null;
		}
		
	}

	/**
	 * Gets the year details.
	 * Shows start month and end month drop downs based on Total Frequency.
	 * @return the year details
	 */
	public List<SelectItem> getYearDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		FacesScopes face = new FacesScopes();
		Locale local = face.getUserLocale();
		try {
			selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, local)));
			for (int i = 1; i <= 12; i++) {
				selectItems.add(new SelectItem(i, getMonth(i, local)));
			}
			this.getManagedBean().setYearList(selectItems);
		} finally {
		}
		return getManagedBean().getYearList();
	}

	/**
	 * Gets the month.
	 * 
	 * @param monthId the month id
	 * @param locale 
	 * @return the month
	 */
	private String getMonth(int monthId, Locale locale){
		Calendar fullMonth = Calendar.getInstance();
		fullMonth.set(00, monthId, 0000);

		Format formatter = new SimpleDateFormat("MMMM", locale); 
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
	 * Shows start year and end year drop downs based on Total Frequency.
	 * @return the years display dtls
	 */
	public List<SelectItem> getYearsDisplayDtls() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		ScLoa loa = 	getManagedBean().getScLoa();
		try {
			//int year = Calendar.getInstance().get(Calendar.YEAR);
			Calendar calendar = Calendar.getInstance(); 
			int effYear = 0;
			int expYear = 0;
			if(loa != null){

				if(loa.getContractEffDate() != null){
					calendar.setTime(loa.getContractEffDate());  
					effYear = calendar.get(Calendar.YEAR);
				}

				if(loa.getContractExpDate() != null){
					calendar.setTime(loa.getContractExpDate());  
					expYear = calendar.get(Calendar.YEAR);
				}

				int noOfYears = expYear - effYear;
				selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));

				for (int i = 0; i <= noOfYears +1; i++) {

					if(i == 0){
						selectItems.add(new SelectItem(effYear, String.valueOf(effYear)));
					}

					if(noOfYears == 0){
						break;
					}
					effYear = effYear+1;

					if(effYear == expYear){
						selectItems.add(new SelectItem(effYear, String.valueOf(effYear)));
						break;
					}

					selectItems.add(new SelectItem(effYear, String.valueOf(effYear)));
				}
			}

			/*
			 * selectItems.add(new SelectItem(year, String.valueOf(year))); for
			 * (int i = 0; i < 20; i++) { year = year + 1; selectItems.add(new
			 * SelectItem(year, String.valueOf(year))); }
			 */

			this.getManagedBean().setYearsDisplayList(selectItems);
		} finally {

		}
		return getManagedBean().getYearsDisplayList();
	}

	/**
	 * Calculate schedule months.
	 * Calculating start month , start year , end month and end year to display total months.
	 * @param schedule the schedule
	 * @param startMonthAction the start month action
	 * @throws ParseException 
	 */
	public void calculateScheduleMonths(ScQtSchedule schedule, Boolean startMonthAction) throws ParseException {
		List<ScQtSchedule> freqSchedule = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();
		}
		Integer startMonth = 0, endMonth = 0, startYear = 0, endYear = 0,  months = 0;
		startMonth = schedule.getStartMonth() != null ? Integer
				.parseInt(schedule.getStartMonth()) : null;
				endMonth = schedule.getEndMonth() != null ? Integer.parseInt(schedule
						.getEndMonth()) : null;
				startYear = schedule.getStartYear() != null ? Integer.parseInt(schedule
						.getStartYear()) : null;
				endYear = schedule.getEndYear() != null ? Integer.parseInt(schedule
						.getEndYear()) : null;

				if (startMonthAction !=null && startMonthAction && startMonth != null) {
					endMonth = startMonth;
					endYear = startYear;
					schedule.setEndMonth(endMonth.toString());
					if(endYear != null){
						schedule.setEndYear(endYear.toString());
					}
				}

				if (startMonth != null && endMonth != null && startYear != null
						&& endYear != null) {
					//Date currentDate = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
					//String localDate = sdf.format(currentDate);
					//String day = localDate.substring(0, 2);
					String fromDate = 01 + "/" + startMonth + "/" + startYear;
					String toDate = 01 + "/" + endMonth + "/" + endYear;
					Date startDate  = sdf.parse(fromDate);
					Date endDate  = sdf.parse(toDate);

					months  = DateUtils.getMonths(startDate, endDate);
					schedule.setTotalMonths(new BigDecimal(months));
				} else {
					schedule.setTotalMonths(BigDecimal.ZERO);
				}

				int index = freqSchedule.indexOf(schedule);
				freqSchedule.set(index, schedule);
				this.getManagedBean().setScheduleList(freqSchedule);
				getManagedBean().getScLoa().setScQtSchedules(freqSchedule);
	}

	/**
	 * Save schedule list.
	 */
	private void saveScheduleList() {
		List<ScQtSchedule> freqschedule = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
		if (freqschedule != null && !freqschedule.isEmpty()) {
			for (ScQtSchedule schedule : freqschedule) {
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String fromdate = 01 + "/"
						+ getMonth(schedule.getStartMonth()) + "/"
						+ schedule.getStartYear();
				String toDate = 01 + "/" + getMonth(schedule.getEndMonth())
						+ "/" + schedule.getEndYear();
				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					debugLogger("Parce Exception");
				}
				localScheduleList.add(schedule);
			}
			getManagedBean().getScLoa().setScQtSchedules(localScheduleList);
		} else {
			getManagedBean().getScLoa().setScQtSchedules(null);
		}
	}

	private String getMonth(String month) {
		//#25022
		FacesScopes facesScopes = new FacesScopes();
		List <ScQtSchedule> scQtSchedule = getManagedBean().getScLoa().getScQtSchedules();
		for (ScQtSchedule schedule : scQtSchedule) {
			if (StringUtils.isEmpty(month)
					|| schedule.getStartMonth() == null) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { ResourceBundleUtils.getMessage(
								"lbl.sc.start.month",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								facesScopes.getUserLocale()) });
			}
			else if(StringUtils.isEmpty(month)
					|| schedule.getEndMonth() == null) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { ResourceBundleUtils.getMessage(
								"lbl.sc.end.month",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								facesScopes.getUserLocale()) });
			}
		}
		int mon = Integer.parseInt(month);
		String finalMonth = null;
		if (mon <= 9) {
			finalMonth = "0" + mon;
		} else {
			return month;
		}
		return finalMonth;

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
	 * Generate frequency schedule pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unused")
	private void generateFrequencySchedulePdfFromVelocity(
			ScAttachment attachment, ScLoa scLoa, ScQt scQt) throws Exception {
		debugLogger("generateFrequencySchedulePdfFromVelocity");
		populateAttachmentDetails(attachment);
		VelocityContext context = new VelocityContext();
		//For s2
		String frequencyScheduleType = "";
		if (scQt != null && scQt.getScheduleType() != null) {
			frequencyScheduleType = scQt.getScheduleType();
		} else{
			// For s1
			frequencyScheduleType = "F";
		}

		context.put("frequencyScheduleType", frequencyScheduleType);

		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		IParameterSO parameterSO = null;
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		Date currentDate = new Date();
		List<ScQtSchedule> scheduleList = scLoa.getScQtSchedules();
		List<ScQtSchedule> scheduleReportList = new ArrayList<ScQtSchedule>();
		ScQtSchedule reportSchedule = null;
		PmParameter scheduleType =  null;
		int totalFrequency = 0;
		if (scheduleList != null && scheduleList.size() > 0) {
			totalFrequency = scheduleList.size();
			for (ScQtSchedule schedule : scheduleList) {
				try {
					reportSchedule = new ScQtSchedule();
					if(StringUtils.isNotBlank(schedule.getStartMonth()) 
							&& StringUtils.isNotBlank(schedule.getStartYear())){
						String localDate = df.format(currentDate);
						String day = localDate.substring(0, 2);
						String fromdate = day + "/" + schedule.getStartMonth() + "/"
								+ schedule.getStartYear();
						schedule.setFromDate(df.parse(fromdate));

						reportSchedule.setStartMonth(monthFormat.format(schedule
								.getFromDate()));
						reportSchedule.setStartYear(yearFormat.format(schedule
								.getFromDate()));
					} 
					if(StringUtils.isNotBlank(schedule.getEndMonth()) && 
							StringUtils.isNotBlank(schedule.getEndYear())){
						String localDate = df.format(currentDate);
						String day = localDate.substring(0, 2);
						String toDate = day + "/" + schedule.getEndMonth() + "/"
								+ schedule.getEndYear();
						schedule.setToDate(df.parse(toDate));

						reportSchedule.setEndMonth(monthFormat.format(schedule
								.getToDate()));
						reportSchedule.setEndYear(yearFormat.format(schedule
								.getToDate()));
					} 

					if(scheduleType ==  null){
						parameterSO = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IParameterSO.class);
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						reqDTO.setParameterId(schedule.getScheduleType()
								.getParameterId());
						ParameterResDTO resDTO = parameterSO
								.retrieveParameterById(reqDTO);
						scheduleType  = resDTO.getPmParameter();
					}
					reportSchedule.setScheduleType(scheduleType);
					reportSchedule.setScheduleNo(schedule.getScheduleNo());
					reportSchedule.setTotalMonths(schedule.getTotalMonths());
					scheduleReportList.add(reportSchedule);

				} finally {
					parameterSO = null;
				}
			}
			context.put("totalFrequency", totalFrequency);
			context.put("scheduleList", scheduleReportList);
			context.put("bean", this);
		}
		int totalPhase = 0;
		List<ScQtProgressivePayment> progressivePayments = scLoa.getScQtProgPayments();
		if (null != progressivePayments && progressivePayments.size() > 0) {
			totalPhase = progressivePayments.size();
			context.put("totalPhase", totalPhase);
			context.put("paymentList", progressivePayments);

		}

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_FREQUENCY_SCHEDULE, context, 
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Form specification.
	 * To download specification.xlsx file
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */
	public void formSpecification(ScAttachment attachment, ScLoa scLoa)
			throws Exception {
		populateAttachmentDetails(attachment);
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
		TemplateSpecReqDTO templateSpecReqDTO = new TemplateSpecReqDTO();

		List<String> itemNames = new ArrayList<String>();
		if(scLoa.getScLoiLoaItems() != null && scLoa.getScLoiLoaItems().size()>0){
			for (ScLoiLoaItem item : scLoa.getScLoiLoaItems()) {
				itemNames.add(item.getScRequestItem().getItemName());
			}
		}
		templateSpecReqDTO.setQtId(scLoa.getScLoiLoa().getDocId());
		templateSpecReqDTO.setSupplier(scLoa.getScLoiLoa().getSmSupplier());
		templateSpecReqDTO.setItemNameList(itemNames);

		List<TplSpecAnswer> serviceAnswers  = new ArrayList<>();
		List<TplSpecAnswer> productAnswers  = new ArrayList<>();
		TemplateSpecResDTO templateSpecResDTO  = templateSpecSO.retrieveSupplierTechAnswersByItemNames(templateSpecReqDTO);
		if(templateSpecResDTO.getSpecAnswerList() != null && templateSpecResDTO.getSpecAnswerList().size()>0){
			for (TplSpecAnswer tplSpecAnswer : templateSpecResDTO.getSpecAnswerList()) {
				if(ParameterConst.ITEM_TYPE_PRODUCT.equals(tplSpecAnswer.getTplSpecQuestion().getTplSpecMaster().getItemTypeCode().getParameterCode())){
					productAnswers.add(tplSpecAnswer);
				} else if(ParameterConst.ITEM_TYPE_SERVICE.equals(tplSpecAnswer.getTplSpecQuestion().getTplSpecMaster().getItemTypeCode().getParameterCode())){
					serviceAnswers.add(tplSpecAnswer);
				}
			}
			generateSpecification(attachment, productAnswers, serviceAnswers);
		}
	}

	/**
	 * Generate Specification.
	 *
	 * @param attachment the attachment
	 * @param productList the product list
	 * @param serviceList the service list
	 * @throws Exception the exception
	 */
	private void generateSpecification(ScAttachment attachment,
			List<TplSpecAnswer> productList, List<TplSpecAnswer> serviceList)
					throws Exception {
		if (serviceList.size() > 0 || productList.size() > 0) {
			InputStream inputStream = null;
			try {
				/*FileInputStream inputStream = new FileInputStream(new File(
						FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/WEB-INF/templates")
						+ "/sc/loa/specification.xlsx"));*/

				final String templatePath = "templates" + SourcingConstants.DOCPATH_SC_LOA_SPECIFICATION;
				inputStream = this.getClass().getClassLoader().getResourceAsStream(templatePath);
				if (inputStream != null) {
					XSSFWorkbook wb = new XSSFWorkbook(inputStream); //or new HSSFWorkbook();
					boolean emptyProductList = false;
					Sheet productSheet = null;
					Sheet serviceSheet = null;
					Row row = null;
					int rowIndex = 0;
					int cellIndex = 0;
					if (productList.size() > 0) {
						rowIndex = 0;
						productSheet = wb.getSheetAt(0);
						wb.setSheetName(0, "ProductSheet");
						for (TplSpecAnswer answer : productList) {
							rowIndex++;
							cellIndex = 0;
							row = productSheet.createRow(rowIndex);
							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClItem() ? 
									"" : answer.getTplSpecQuestion().getClItem());

							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClSpecDetail() ? 
									"" : answer.getTplSpecQuestion().getClSpecDetail());

							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUom() ? 
									"" : answer.getTplSpecQuestion().getClUom().getUomName());

							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClPriceTypeCode() ? 
									"" : getParameterDesc(answer.getTplSpecQuestion().getClPriceTypeCode()));

							/*row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClQuantity() ? 
									"" : answer.getTplSpecQuestion().getClQuantity().toString());*/

						}

						// Auto size all the columns
						for (int x = 0; x < productSheet.getRow(0)
								.getPhysicalNumberOfCells(); x++) {
							productSheet.autoSizeColumn(x);
						}
					} else {
						wb.removeSheetAt(0);
						emptyProductList = true;
					}

					if (serviceList.size() > 0) {
						rowIndex = 0;
						if (emptyProductList) {
							serviceSheet = wb.getSheetAt(0);
							wb.setSheetName(0, "ServiceSheet");
						} else {
							serviceSheet = wb.getSheetAt(1);
							wb.setSheetName(1, "ServiceSheet");
						}

						for (TplSpecAnswer answer : serviceList) {
							rowIndex++;
							cellIndex = 0;
							row = serviceSheet.createRow(rowIndex);
							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClItem() ? 
									"" : answer.getTplSpecQuestion().getClItem());

							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClSpecDetail() ? 
									"" : answer.getTplSpecQuestion().getClSpecDetail());

							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUom() ? 
									"" : answer.getTplSpecQuestion().getClUom().getUomName());

							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUomFrequency() ? 
									"" : answer.getTplSpecQuestion().getClUomFrequency().toString());

							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUomPerDay() ? 
									"" : answer.getTplSpecQuestion().getClUomPerDay().toString());

							row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUomPerMonth() ? 
									"" : answer.getTplSpecQuestion().getClUomPerMonth().toString());

							/*row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClQuantity() ? 
									"" : answer.getTplSpecQuestion().getClQuantity().toString());*/
						}

						for (int x = 0; x < serviceSheet.getRow(0)
								.getPhysicalNumberOfCells(); x++) {
							serviceSheet.autoSizeColumn(x);
						}
					} else {
						wb.removeSheetAt(1);
					}

					wb.setActiveSheet(0);
					FileOutputStream os = new FileOutputStream(
							getActualFilePath(attachment));
					wb.write(os);
					os.flush();
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(inputStream != null) {
				inputStream.close();
			}
		}
	}

	/**
	 * Adds the progressive details.
	 */
	public void addProgressiveDetails() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		ScLoa scLoa = getManagedBean().getScLoa();
		
		if (this.getManagedBean().getProgressivePaymentList() == null) {
			this.getManagedBean().setProgressivePaymentList(
					new ArrayList<ScQtProgressivePayment>());
		}
		List<ScQtProgressivePayment> progressivePayList = this.getManagedBean()
				.getProgressivePaymentList();
		if(CollectionUtils.isNotEmpty(progressivePayList)){
			List<ScQtProgressivePayment> progressiveList=	this.getManagedBean().getToBeRemoveProgressivePayments();
			if(null==progressiveList){
				progressiveList=new ArrayList<ScQtProgressivePayment>();
			}

			for(ScQtProgressivePayment payment:progressivePayList){
				if(null!=payment.getQtPaymentId()){
					payment.setDeleted(true);
					progressiveList.add(payment);
				}
			}
			this.getManagedBean().setToBeRemoveProgressivePayments(progressiveList);

		}

		/*if (null == progressivePayList) {
			progressivePayList = new ArrayList<ScQtProgressivePayment>();
		}

		if(null == totalProgressivePayment){
			totalProgressivePayment = new Integer(0);
		}*/

		int value=	 this.getManagedBean()
				.getTotalProgressivePayment()!=null? this.getManagedBean()
						.getTotalProgressivePayment():0;
						/*	if (progressivePayList.size() > totalProgressivePayment) {
			List<ScQtProgressivePayment> progressiveList = new ArrayList<ScQtProgressivePayment>();
			for (int j = 0; j < totalProgressivePayment ; j++) {
				progressiveList.add(progressivePayList.get(j));
			}

			this.getManagedBean().setProgressivePaymentList(progressiveList);
		} else {*/
						progressivePayList = new ArrayList<ScQtProgressivePayment>();			
						for (int i = 0; i < value ; i++) {
							ScQtProgressivePayment payment = new ScQtProgressivePayment();
							payment.setStartYear(String.valueOf(year));
							payment.setEndYear(String.valueOf(year));
							payment.setPhaseNo(BigDecimal.valueOf(i + 1));
							payment.setScLoa(scLoa);
							setUserDetails(payment);
							progressivePayList.add(payment);
						}
						this.getManagedBean().setProgressivePaymentList(progressivePayList);
						/*}*/

	}

	/**
	 * Save progressive paymnt list.
	 */
	private void saveProgressivePaymntList() {
		List<ScQtProgressivePayment> progressivePayemnt = this.getManagedBean()
				.getProgressivePaymentList();
		List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
		if (progressivePayemnt != null && !progressivePayemnt.isEmpty()) {
			for (ScQtProgressivePayment payment : progressivePayemnt) {
				Date currentDate = new Date();
				SimpleDateFormat format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
				String localDate = format.format(currentDate);
				String day = localDate.substring(0, 2);
				String fromdate = day + "/" + payment.getStartMonth() + "/"
						+ payment.getStartYear();
				String toDate = day + "/" + payment.getEndMonth() + "/"
						+ payment.getEndYear();

				try {
					if (format.parse(fromdate).after(format.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						payment.setFromDate(format.parse(fromdate));
						payment.setToDate(format.parse(toDate));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				getManagedBean().getScLoa().getScQtProgPayments();

				paymentList.add(payment);
			}
			getManagedBean().getScLoa().setScQtProgPayments(paymentList);

		} else {
			getManagedBean().getScLoa().setScQtProgPayments(null);
		}
	}

	/**
	 * Gets the bond letter stream content.
	 *
	 * @return the bond letter stream content
	 * @throws Exception the exception
	 */
	public StreamedContent getBondLetterStreamContent() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoa().getLoaNo());
		generateBondLetterFromVelocityToPdf((ScAttachment) pbAttachment,null);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				pbAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return pbStreamContent;

	}

	/**
	 * Gets the approval action display name.
	 *
	 * @param value the value
	 * @return the approval action display name
	 */
	public String getApprovalActionDisplayName(String value) {
		FacesScopes scope=new FacesScopes();
		Locale locale=	scope.getUserLocale();
		log.info("display language"+locale.getDisplayLanguage());
		String approverName= ApprovalAction.getDisplayName(value);
		log.info("approver Status"+approverName);

		if (!locale.getDisplayLanguage().equalsIgnoreCase("english")) {
			log.info("not  equallanguage");

			if("Approved".equalsIgnoreCase(approverName)){	 
				approverName=getApproverStatus("lbl.sc.status.approved");	 
			}
			else if("Rejected".equalsIgnoreCase(approverName)){	 
				approverName=getApproverStatus("lbl.sc.status.rejected");	 
			}
			else if("Revised".equalsIgnoreCase(approverName)){	 
				approverName=getApproverStatus("lbl.sc.status.revised");	 
			}
		}
		return approverName;
	}

	private String getApproverStatus(String label){
		return	ResourceBundleUtils
				.getMessage(label,
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());	
	}


	/**
	 * Calc sub total price.
	 * calculating subtotal for item list in award details.
	 * @param scLoiLoaItem the sc loi loa item
	 * @return the big decimal
	 */
	public BigDecimal calcSubTotalPrice(ScLoiLoaItem scLoiLoaItem) {
		BigDecimal total = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
				scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());

		return total;
	}

	/**
	 * Spki sign action.
	 * @param digiSign the digi sign
	 */
	public void spkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
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
		if(getManagedBean().getScLoa() != null){
			sb.append(getManagedBean().getScLoa().getLoaNo());
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
	 * Page navigation from loa details page to qt loa list or dp loa list.
	 */
	public void backToPrevious() {
		log.info("backToPrevious");
		
		if(getManagedBean().isDpMaintenanceSource()){
			getManagedBean().setEditFlag(Boolean.FALSE);
			getManagedBean().setBackToPreviousPage(null);
		}
		
		getManagedBean().setReadOnly(Boolean.FALSE);
		getManagedBean().setSelectedTabIndex(0);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getSourcePage());
	}
	
	public PmUser currentApprovalSession(){
		Long approverUserId = this.getManagedBean().getApproverUser().getUserId();
		
		UserReqDTO userReqDTO  = new UserReqDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		userReqDTO.setUserId(approverUserId);
		PmUser approverUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
		
		return approverUser;
	}

	/**
	 * retrieving LOI LOA List from database.
	 *
	 * @param qtId the qt id
	 */
	public void retrieveLOILOAList(Long qtId) {
		debugLogger("retrieveLOILOAList");
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setDocId(qtId);
		reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);

		AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOILOAList(reqDTO);
		QtLOABackingBean  loaBackingBean = new QtLOABackingBean();
		if (resDTO.isSuccessful()) {
			loaBackingBean.getManagedBean().setQt(resDTO.getScQt());
			List<ScLoiLoa> loiLoaList = resDTO.getLoiLoas();
			if (loiLoaList != null && loiLoaList.size() > 0) {
				loaBackingBean.getManagedBean().setSuppLoiLoaMap(resDTO.getLoiLoaSupplierMap());
				loaBackingBean.getManagedBean().setScLoiLoaList(loiLoaList);
				loaBackingBean.getManagedBean().setSelectedLoiLoa(loiLoaList.get(0));
			}	
		}
		loaBackingBean.generateSuppLoiLoaList();
	}

	/**
	 * To check is performance bond required
	 * @return boolean isPerformanceBondRequired
	 */
	public void checkPerformanceBondMandatory() {
		//		boolean isPerformanceBondMandatory = false;
		ScLoa loa = getManagedBean().getScLoa();
		BigDecimal overallContractAmtOrLoaAmt = overallCtAmtOrLoaAmt(loa);
		List<ScQtCategory> qtCategories = null;
		if(loa.getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)) {
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(loa.getScLoiLoa().getDocId());
			qtCategories = getQtDetailSO().getQtCategoryByQtId(reqDTO).getCategories();
		}

		boolean isBondReq = isPerformanceBondRequired(overallContractAmtOrLoaAmt, qtCategories);
		
		System.out.println("____ checkPerformanceBondMandatory docId: "+loa.getScLoiLoa().getDocId()
				+" docType: " +loa.getScLoiLoa().getDocType() +" isBondReq: "+isBondReq+" overallContractAmtOrLoaAmt: "+overallContractAmtOrLoaAmt);
		
		if(loa.isBrandNew()){
			loa.setBondReq(isBondReq);
		}
		getManagedBean().setPerformanceBondMandatory(isBondReq);
	}

	/**
	 * To check is agreement required
	 * @return boolean isRequirementRequired
	 */
	public void checkAgreementMandatory() {
		boolean isAgreementMandatory = false;
		ScLoa loa = getManagedBean().getScLoa();
		BigDecimal overallContractAmtOrLoaAmt = overallCtAmtOrLoaAmt(loa);

		String fulfilmentType = loa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode();

		boolean isOneOffFulfilment = ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(fulfilmentType);
		if(!isOneOffFulfilment) {
			isAgreementMandatory = true;
			loa.setAgreementReq(isAgreementMandatory);
		} else if (isOneOffFulfilment && !isQuotationThresHold(overallContractAmtOrLoaAmt)){
			//Bug #24065
			//isAgreementMandatory = false;
			isAgreementMandatory = true;
			loa.setAgreementReq(isAgreementMandatory);
		}

		getManagedBean().setAgreementMandatory(isAgreementMandatory);
	}

	private BigDecimal overallCtAmtOrLoaAmt(ScLoa loa){
		BigDecimal overallContractAmtOrLoaAmt = BigDecimal.valueOf(0d);
		// if supplier Gst Registered  
		//Enhancement #24259 #24269
//		if(isGSTRegActive()){		
		if (loa.getSstAmt() != null) { 
			overallContractAmtOrLoaAmt = loa.getOverallContractAmt(); 
		} else {
			overallContractAmtOrLoaAmt = loa.getLoaAmt();
		}
		
		return overallContractAmtOrLoaAmt;
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
	 * Gets the qt detail so.
	 *
	 * @return the qt detail so
	 */
	private IQTDetailSO getQtDetailSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	}

	/**
	 * Called on click of Update Specification button & shows item details
	 * 
	 * @param loa
	 * @return
	 */
	public String setTemplateSpec(ScLoa loa){

		List<ScLoiLoaItem> items = loa.getScLoiLoaItems();
		ScLoiLoa scLoiLoa = loa.getScLoiLoa();
		SmSupplier smSupplier = scLoiLoa.getSmSupplier();

		TemplateSpecBean specBean = new TemplateSpecBean();
		int size = items.size();
		List<String> itemNames = new ArrayList<String>(size);
		for (ScLoiLoaItem item : items) {

			TplSpecQuestion tplSpecQuestion = item.getScRequestItem()
					.getTplSpecQuestion();
			itemNames.add(tplSpecQuestion.getClItem());

		}

		Long qtId = this.getManagedBean().getScQt().getQtId();
		TemplateSpecResDTO specResDTO = getTplSpecAnswers(qtId, smSupplier, itemNames);

		if (null != specResDTO && specResDTO.isSuccessful()) {
			List<TplSpecAnswer> answerList = specResDTO.getSpecAnswerList();
			if (answerList != null && !answerList.isEmpty()) {
				List<TplSpecAnswer> tempAnsList = new ArrayList<TplSpecAnswer>();
				// tempAnsList.addAll(answerList);
				for (TplSpecAnswer answer : answerList) {
					TplSpecQuestion parentQuest = answer.getTplSpecQuestion().getParentSpecQuestion();
					if (parentQuest == null) {
						tempAnsList.add(answer);
					} else {
						if (tempAnsList.size() > 0) {

							for (TplSpecAnswer ans : tempAnsList) {
								if (ans.getTplSpecQuestion()
										.getSpecQuestionId()
										.equals(parentQuest
												.getSpecQuestionId())) {
									int index = tempAnsList.indexOf(ans);
									int questionorder =  answer.getTplSpecQuestion().getQuestionOrder();
									int parentOrder = parentQuest.getQuestionOrder();
									int position = questionorder-parentOrder;
									tempAnsList.add((index+position), answer);
									break;
								}

							}
						} else {
							tempAnsList.add(answer);
						}
					}

				}

				specBean.setSpecAnswerList(tempAnsList);
				specBean.setSpecMaster(answerList.get(0)
						.getTplSpecQuestion().getTplSpecMaster());
			}
		}
		specBean.setContentType(TemplateSpecConst.CONTENT_TYPE_UPDATE_SPEC);
		specBean.setUpdateSpecByAnswers(true);
		this.getManagedBean().setTemplateSpecBean(specBean);
		return "loaTemplateSpecDtls";
	}

	private TemplateSpecResDTO getTplSpecAnswers(Long qtId, SmSupplier supplier,
			List<String> itemNameList) {

		ITemplateSpecSO templateSpecSO = null;
		TemplateSpecResDTO specResDTO = null;
		try {
			templateSpecSO = getTemplateSpecSO();
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setSupplier(supplier);
			reqDTO.setItemNameList(itemNameList);
			specResDTO = templateSpecSO.retrieveSupplierTechAnswersByItemNames(reqDTO);

		} finally {
			templateSpecSO = null;
		}
		return specResDTO;
	}

	/**
	 * Save template spec.
	 *
	 * @param templateSpecBean the template spec bean
	 */
	public void saveTemplateSpec(TemplateSpecBean templateSpecBean){

		ITemplateSpecSO templateSpecSO = null;
		try {
			templateSpecSO = getTemplateSpecSO();
			List<TplSpecAnswer> answerList = templateSpecBean
					.getSpecAnswerList();
			if (answerList != null && !answerList.isEmpty()) {
				List<TplSpecQuestion> questionList = new ArrayList<TplSpecQuestion>();
				for (TplSpecAnswer answer : answerList) {
					questionList.add(answer.getTplSpecQuestion());
				}
				TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
				reqDTO.setQuestionList(questionList);
				TemplateSpecResDTO resDTO = templateSpecSO
						.saveTemplateSpecQuestion(reqDTO);
				if (resDTO != null && resDTO.isSuccessful()) {
					FacesContext.getCurrentInstance()
					.addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_INFO,
							null,
							MessageUtils
							.getMessage(
									MessageConst.INFO_100002,
									new String[] {},
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes()
									.getUserLocale())));
				}
			}
		} finally {
			templateSpecSO = null;
		}

	}

	private ITemplateSpecSO getTemplateSpecSO(){
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
	}

	/**
	 * Back to previous.
	 *
	 * @param tabIndex the tab index
	 */
	public void backToPrevious(int tabIndex) {
		log.info("backToPrevious");
		getManagedBean().setSelectedTabIndex(tabIndex);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, QTConstants.LOA_PAGE);

	}

	/**
	 * Filter skop kerja dan perkhidmatan repo.
	 *
	 * @param scLoa the sc loa
	 */
	private void filterSkopKerjaDanPerkhidmatanRepo(ScLoa scLoa){
		// get this repo when qt with service scope otherwise empty
		String SkopKerjaDP = "Skop Kerja dan Perkhidmatan";

		List<ScLoaReport> scLoaReports = new ArrayList<ScLoaReport>();

		if(null  != scLoa.getScLoaReports() && scLoa.getScLoaReports().size()>0 ){
			for (ScLoaReport scLoaReport : scLoa.getScLoaReports()) {
				boolean isSkopKerja = Boolean.TRUE;
				if (scLoaReport.getTitle().trim().equals(SkopKerjaDP.trim())) {
					if (null == scLoaReport.getScLoaReportAttachment()) {
						isSkopKerja = Boolean.FALSE;
					} 
				}
				if(isSkopKerja){
					scLoaReports.add(scLoaReport);	
				}
			}
			scLoa.setScLoaReports(scLoaReports);
		}
	}
	
	/**
	 * Sum of sstAmount & loa offer price
	 * 
	 * @param sstAmount
	 */
	public void calculateOverallAmount(BigDecimal sstAmount) {
		ScLoa scLoa = getManagedBean().getScLoa();
		getManagedBean().setOverallContractAmt(overallCtAmountWithSST(scLoa.getLoaAmt(), sstAmount));
		scLoa.setOverallContractAmt(getManagedBean().getOverallContractAmt());

		if (null != getManagedBean().getContractSignerUser() && getManagedBean().getContractSignerUser().getUserId() != null) {

			scLoa.setLoaSignerDesig(null);
			scLoa.setContractSigner(null);
			getManagedBean().setContractSignerUser(null);
			prepareSignerInfo();
			RequestContext.getCurrentInstance().execute("sstSignerConfirmPopupWin.show()");
		} else {
			prepareSignerInfo();
		}

		checkAgreementMandatory();

		checkPerformanceBondMandatory();

		calcBondAmountWithSST(scLoa, Boolean.FALSE);
		
		// bond amount in words
		if (scLoa.getBondAmt() != null) {
			getManagedBean().setBondAmountWord(NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		}
	}
	
	/**
	 * Check proposal validity date reach by qt.
	 *
	 * @param qt the qt
	 */
	public boolean checkProposalValidityDateReachByQt(ScQt qt){
		Boolean isProposalValidDateReach = Boolean.FALSE;

		// s2
		if(qt != null){
			isProposalValidDateReach =  checkProposalValidityDateReach(qt.getProposalValidityEndDate());

		} else{
			// s1 - no proposal validity
			isProposalValidDateReach = Boolean.TRUE;
		}
		return isProposalValidDateReach;
	}

	/**
	 * Upload branch info.
	 *
	 * @param event the event
	 */
	public void uploadBranchInfo(FileUploadEvent event) {

		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		ScAttachment scAttachment = null;
		boolean isBIReport = false;
		if(scLoaReports == null){
			scLoaReports = new ArrayList<ScLoaReport>();
			scLoa.setScLoaReports(scLoaReports);
		}
		for(ScLoaReport report:scLoaReports){
			PmDocument document = report.getPmDocument();
			// Check whether Branch Info attachment is existed or not
			if (null != document && SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
					.equalsIgnoreCase(report.getPmDocument().getDocDefinitionKey())) {
				ScLoaReportAttachment scLoaReportAttachment = new ScLoaReportAttachment();
				report.setScLoaReportAttachment(scLoaReportAttachment);
				scLoaReportAttachment.setDocNo(scLoa.getLoaNo());
				scLoaReportAttachment.setScLoaReport(report);
				report.setScLoaReportAttachment(scLoaReportAttachment);
				scAttachment = scLoaReportAttachment;
				isBIReport = true;
				getManagedBean().setBranchInfoReport(report);
				break;
			}
		}
		if(!isBIReport){
			ScLoaReport scLoaReport = new ScLoaReport();
			scLoaReport.setScLoa(scLoa);
			scLoaReport.setTitle(BRANCH_INFO_TITLE);
			IParameterSO parameterSO = null;
			try {
				parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
				reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
				ParameterResDTO resDTO = parameterSO
						.retrieveParameterByTypeAndCode(reqDTO);
				scLoaReport.setDefinationType(resDTO.getPmParameter());
				String docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER; // AQ
				if (ParameterConst.DOCTYPE_PURCHASE_REQUEST
						.equalsIgnoreCase(scLoa.getScLoiLoa().getDocType())) {
					docCategory = ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_DIRECT_PURCHASE; // AD
				}
				scLoaReport.setDocCategory(docCategory); 
				//scLoaReport.setPmDocument(getDocumentDtls(docCategory)); // PM document

			} finally {
				parameterSO = null;
			}
			ScLoaReportAttachment scLoaReportAttachment = new ScLoaReportAttachment();
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			scLoaReportAttachment.setDocNo(scLoa.getLoaNo());
			scLoaReportAttachment.setScLoaReport(scLoaReport);
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			scAttachment = scLoaReportAttachment;
			getManagedBean().setBranchInfoReport(scLoaReport);
			scLoaReports.add(scLoaReport);
		}

		try {
			uploadAttachment(event.getFile(), scAttachment);
		} catch (FileNotFoundException e) {
			log.error("Exceptionn occured while uploading the file"+e.getMessage());
		} catch (IOException e) {
			log.error("Exceptionn occured while uploading the file"+e.getMessage());
		}
	}


	public void setMinSL1MParticipant(VelocityContext context,
			ScLoa scLoa, DecimalFormat integerDF){

		BigDecimal loaAmount = scLoa.getLoaAmt();
		BigDecimal gstAmt = scLoa.getGstAmt();
		BigDecimal value = new BigDecimal(18000);
		BigDecimal hundred = new BigDecimal(100);
		BigDecimal totalAmount = new BigDecimal(0);

		String minSL1M = "........................";
		if(gstAmt != null){
			totalAmount = loaAmount.add(gstAmt);
		}else{
			totalAmount = loaAmount;
		}
		minSL1M = integerDF.format(((totalAmount)
				.divide(hundred))
				.divide(value,0, BigDecimal.ROUND_HALF_UP));
		context.put("minSL1M", minSL1M);
	}

	/**
	 * Gets the mof account by supplier id.
	 *
	 * @param supplierId the supplier id
	 * @return the mof account by supplier id
	 */
	public SmMofAccount getMofAccountBySupplierId(Long supplierId) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofAccountSO.class);
		MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
		accountReqDTO.setSupplierId(supplierId);
		MofAccountResDTO mofAccountResDTO = mofAccountSO
				.retrieveMofNoBySupplierId(accountReqDTO);
		return mofAccountResDTO.getMofAccount();
	}

	/**
	 * Gets the mof cert by mof account id.
	 *
	 * @param mofAccountId the mofAccountId id
	 * @return the mof cert by mof account id
	 */
	public SmMofCert getMofCertBySupplierId(Long mofAccountId) {
		IVirtualCertSO virtualCertSO = (IVirtualCertSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IVirtualCertSO.class);
		VirtualCertReqDTO certReqDTO = new VirtualCertReqDTO();
		certReqDTO.setMofAccountId(mofAccountId);
		VirtualCertResDTO certResDTO = virtualCertSO
				.retrieveLatestCertByAccountId(certReqDTO);
		return certResDTO.getSmMofCert();
	}

	/**
	 * Tab change action.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void tabChangeAction(int selectedTabIndex){

		if(selectedTabIndex != 0 && this.getManagedBean().isDisplayAlert()){
			this.getManagedBean().setDisplayAlert(false);
			RequestContext.getCurrentInstance().execute(
					"gstConfirmPopupWid.show()");
		}

	}

	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
	}	

	/**
	 * Prepare signer info.
	 */
	private void prepareSignerInfo() {

		ScLoa scLoa = getManagedBean().getScLoa();

		// Contract Signer
		PmUser contractSignerUser = scLoa.getContractSigner();
		if (contractSignerUser == null) {
			contractSignerUser = new PmUser();
		}

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());
		
		if (scLoa.getLoaAmt() != null) {
			userReqDTO.setApprovalLimit(overallCtAmountWithSST(scLoa.getLoaAmt(), scLoa.getSstAmt()));
//			userReqDTO.setApprovalLimit(overallCtAmount(scLoa.getLoaAmt(),
//					scLoa.getGstAmt()));
		}
		userReqDTO.setOrgProfileId(scLoa.getScLoiLoa().getPmOrgProfile().getOrgProfileId());
		
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
		getManagedBean().setContractSignerList(userResDTO.getUserList());
		
		getManagedBean().setContractSignerUser(contractSignerUser);
	}

	private void RenameTempLoaNoRepoDirByLoaNo(ScLoa scLoa){
		String tempFilePath = splitFilePath(getManagedBean().getTempFilePath());
		String actualFilePath = getActualFilePathLoaNo(scLoa);
		if(!StringUtils.isBlank(tempFilePath)){
			// /docuRepo/dev/ngep/2016/SC/LB16000001565237 
			//StringBuilder tFilePath = new StringBuilder("c:");
			StringBuilder tFilePath = new StringBuilder();
			tFilePath.append(tempFilePath);
			//create source File object
			File oldName = new File(tFilePath.toString());
			log.info("Old Path : " + tFilePath.toString());
			//create destination File object
			//StringBuilder aFilePath = new StringBuilder("c:");
			StringBuilder aFilePath = new StringBuilder();
			aFilePath.append(actualFilePath);
			File newName = new File(aFilePath.toString());
			log.info("New Path : " + aFilePath.toString());
			log.info("Before rename to new path");
			boolean isFileRenamed = oldName.renameTo(newName);
			log.info("After rename to new path");
			if(isFileRenamed)
				log.info("Temporary Loa No folder has been renamed.");
			else
				log.info("Temporary Loa No folder has not been renamed.");

		}
	}

	private String getActualFilePathLoaNo(ScLoa loa) {
		String  actualFilePath = "";
		List<ScLoaReport> scLoaReports = loa.getScLoaReports();
		if (scLoaReports != null && scLoaReports.size() > 0) {
			for (ScLoaReport scLoaReport : scLoaReports) {
				if (null == scLoaReport.getPmDocument() || SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
						.equals(scLoaReport.getPmDocument().getDocDefinitionKey())) {
					if (scLoaReport.getScLoaReportAttachment() != null) {
						String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
								EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
						StringBuilder path = new StringBuilder(docRepoBaseDir);
						path.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
						path.append(scLoaReport.getScLoaReportAttachment().getFilePath());
						actualFilePath = splitFilePath(path.toString());
					}
				}	
			}
		}
		return actualFilePath;
	}

	private String splitFilePath(String filePath){
		StringBuilder path = new StringBuilder();
		if(!StringUtils.isBlank(filePath)){
			String[] filePathArr = filePath.split("/");
			for (int i = 0; i < filePathArr.length-2 ; i++) {
				path.append(filePathArr[i]).append("/");
			}
			path.deleteCharAt(path.toString().length()-1);
		}
		return path.toString();
	}

	/**
	 * Update unspsc item.
	 *
	 * @param requestItem the request item
	 */
	public void updateUnspscItem(ScRequestItem requestItem) {
		log.info("updateUnspscItem " + requestItem);
		ScLoa scLoa = getManagedBean().getScLoa();

		if(scLoa.getScLoiLoaItems() != null){
			for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
				if(scLoiLoaItem.getScRequestItem()!= null && scLoiLoaItem.getScRequestItem().getRequestNoteItemId().equals(requestItem.getRequestNoteItemId())){
					scLoiLoaItem.setScRequestItem(requestItem);
				}
			}
		}
	}


	/**
	 * Show prepared by.
	 *
	 * @param createdBy the created by
	 * @return the string
	 */
	public String showPreparedBy(Long createdBy) {
		String userName = "";
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		if (createdBy != null) {
			reqDTO.setUserId(createdBy);
			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);

			if (resDTO.getPmUser() != null) {
				userName = resDTO.getPmUser().getUserName();
			}
		}
		return userName;
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

	public String constructUserConditionFields() {
		StringBuilder sb = new StringBuilder();
		ScLoa scLoa = getManagedBean().getScLoa();
		sb.append("pmUserOrgs.pmUserRoles.pmRole.roleCode^");
		sb.append(RoleCode.CONTRACT_SIGNER.value());
		sb.append("^9");

		if (scLoa.getLoaAmt() != null) {
			sb.append("|pmUserOrgs.pmUserRoles.pmRole.isLimitRequired^");
			sb.append(true);
			sb.append("^1");
			sb.append("|pmUserOrgs.pmUserRoles.approvalLimit^");
			//Enhancement #24259 #24269
			sb.append(overallCtAmountWithSST(scLoa.getLoaAmt(), scLoa.getSstAmt())); 
//			sb.append(overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt())); 
			sb.append("^4");
		}
		
		return sb.toString();
	}

	public void signerUserListener(PmUser contractSignerUser) {
		log.info("signerUserListener");

		ScLoa scLoa = getManagedBean().getScLoa();
		scLoa.setLoaSignerDesig(contractSignerUser.getDesignation());

	}

	/**
	 * Validate Contract Admin
	 * 
	 * @param scLoa
	 *          the scLoa
	 */	
	private void validateContractDetails(ScLoa scLoa) {
		Locale locale = new FacesScopes().getUserLocale();

		if (StringUtils.isBlank(
				scLoa.getContractAdmin().getUserName())) {
			throw new EpException(MessageConst.ERROR_760400,
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.loiloa.loa.cont.admin", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
		}	
	}
	
	public void confirmSupplierAccept() {
		this.getManagedBean().setAction(BpmOutcomeConst.BPM_ACCEPT);
		log.info(":::confirmSupplierAccept() to "+ BpmOutcomeConst.BPM_ACCEPT +" LOA.");

		// spki
		getManagedBean().getSpkiBackingBean().getChallengeQuestions();
		RequestContext.getCurrentInstance().execute("spki_dlg.show();");
	}
	
	public void confirmSupplierReject() {
		this.getManagedBean().setAction(BpmOutcomeConst.BPM_REJECT);
		log.info(":::confirmSupplierReject() to "+ BpmOutcomeConst.BPM_REJECT +" LOA.");
		updateBPMTask(BpmOutcomeConst.BPM_REJECT, null);
	}
	//end Enhancement #21573
		
	
	/**
	 * To get userRole Code
	 * 
	 * @return String roleCode
	 */
	private String getUserRoleCode(String docType) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		reqDTO.setLanguageCode(CommonConstants.DEFAULT_LANGUAGE_CODE);
		UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
		List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
		List<String> roleCodes = new ArrayList<String>();
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST
				.equalsIgnoreCase(docType)) {
		roleCodes.add(RoleCode.GOVT_SELLER.value());
		roleCodes.add(RoleCode.G2G_SUPPLIER_USER.value());
		roleCodes.add(RoleCode.G2G_SUPPLIER_ADMIN.value());
		roleCodes.add(RoleCode.MOF_SUPPLIER_ADMIN.value());
		roleCodes.add(RoleCode.MOF_SUPPLIER_USER.value());
		roleCodes.add(RoleCode.BASIC_SUPPLIER_ADMIN.value());
		roleCodes.add(RoleCode.BASIC_SUPPLIER_USER.value());
		roleCodes.add(RoleCode.REQUISITIONER.value());
		roleCodes.add(RoleCode.RN_APPROVER.value());
		roleCodes.add(RoleCode.CT_APPROVER.value());
		roleCodes.add(RoleCode.BPK_APPROVER.value());
		roleCodes.add(RoleCode.DESK_OFFICER.value());
		roleCodes.add(RoleCode.SC_SECRETARY.value());
		roleCodes.add(RoleCode.SC_MEMBER.value());
		roleCodes.add(RoleCode.SC_CHAIRPERSON.value());
		}else if(ParameterConst.DOCTYPE_QUOTATION.equals(docType) || ParameterConst.DOCTYPE_DIRECT_NEGO.equals(docType)) {
			roleCodes.add(RoleCode.DESK_OFFICER.value());
			roleCodes.add(RoleCode.SC_SECRETARY.value());
			roleCodes.add(RoleCode.SC_MEMBER.value());
			roleCodes.add(RoleCode.SC_CHAIRPERSON.value());
			roleCodes.add(RoleCode.MOF_SUPPLIER_ADMIN.value());
			roleCodes.add(RoleCode.MOF_SUPPLIER_USER.value());
			roleCodes.add(RoleCode.BASIC_SUPPLIER_ADMIN.value());
			roleCodes.add(RoleCode.BASIC_SUPPLIER_USER.value());
			roleCodes.add(RoleCode.GOVT_SELLER.value());
			roleCodes.add(RoleCode.G2G_SUPPLIER_USER.value());
			roleCodes.add(RoleCode.G2G_SUPPLIER_ADMIN.value());
			roleCodes.add(RoleCode.REQUISITIONER.value());
			roleCodes.add(RoleCode.RN_APPROVER.value());
			roleCodes.add(RoleCode.CT_APPROVER.value());
			roleCodes.add(RoleCode.BPK_APPROVER.value());
			
		}
		
		String roleCode = null;

		List<String> pmRoleCodes = new ArrayList<String>();

		if (null != roleDesc) {
			for (PmRoleDesc pmRoleDesc : roleDesc) {
				pmRoleCodes.add(pmRoleDesc.getPmRole().getRoleCode());
			}

			for (String role : roleCodes) {
				if (pmRoleCodes.contains(role)) {
					roleCode = role;
					break;
				}
			}
		}
		return roleCode;
	}
	
	
	private FacesScopes getFacesScopes(){
		FacesScopes fs = new FacesScopes();
		return fs;
	}
	
	/**
	 * @param e
	 */
	public void selectAllSchedule(AjaxBehaviorEvent e) {

		if (getManagedBean().getScLoa().getScQtSchedules() != null) {
			for (ScQtSchedule ScQtSchedule : getManagedBean().getScLoa()
					.getScQtSchedules()) {
				if (getManagedBean().isSelectAll()) {
					ScQtSchedule.setSelected(true);
					if (getManagedBean().isButtonHideSchActive()) {
						getManagedBean().setButtonHideSchActive(false);
					}
				} else {
					ScQtSchedule.setSelected(false);
					if (!getManagedBean().isButtonHideSchActive()) {
						getManagedBean().setButtonHideSchActive(true);
					}
				}
			}
		}
		if (getManagedBean().getScLoa().getScQtProgPayments() != null) {
			for (ScQtProgressivePayment ScQtProgressivePayment : getManagedBean()
					.getScLoa().getScQtProgPayments()) {
				if (getManagedBean().isSelectAll()) {
					ScQtProgressivePayment.setSelected(true);
					if (getManagedBean().isButtonHideSchActive()) {
						getManagedBean().setButtonHideSchActive(false);
					}
				} else {
					ScQtProgressivePayment.setSelected(false);
					if (!getManagedBean().isButtonHideSchActive()) {
						getManagedBean().setButtonHideSchActive(true);
					}
				}
			}
		}
	}

	public void selectOneScheCheckboxSelect(AjaxBehaviorEvent e) {
		getManagedBean().setSelectAll(false);
		if (getManagedBean().getScLoa().getScQtSchedules() != null) {
			for (ScQtSchedule ScQtSchedule : getManagedBean().getScLoa()
					.getScQtSchedules()) {
				if (ScQtSchedule.isSelected()) {
					getManagedBean().setButtonHideSchActive(false);
					return;
				}
			}
		}
		if (getManagedBean().getScLoa().getScQtProgPayments() != null) {
			for (ScQtProgressivePayment ScQtProgressivePayment : getManagedBean()
					.getScLoa().getScQtProgPayments()) {
				if (ScQtProgressivePayment.isSelected()) {
					getManagedBean().setButtonHideSchActive(false);
					return;
				}
			}
		}
		
		if ((boolean) e.getComponent().getAttributes().get("value")) {
			getManagedBean().setButtonHideSchActive(false);
		} else {
			getManagedBean().setButtonHideSchActive(true);
		}
	}
		
	protected void updateCancelLoaCurrentWorkflowStatus(ScLoa scLoa, String outcome, Long previousStatus) {		
		IScWorkFlowStatusSO workFlowStatusSO;
					workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
		reqDTO.setDocId(scLoa.getLoaId());

		ScLoaWorkFlowStatus workFlowStatus = new ScLoaWorkFlowStatus();
		if (outcome.contains(BpmOutcomeConst.BPM_REJECT)){
//			workFlowStatus.setPmStatus(getPmStatusByStatusId(QTStatusConst.LOA_CANCELLATION_REJECTED));
			workFlowStatus.setPmStatus(getPmStatusByStatusId(previousStatus));
			log.info("-----Check 62523L------ " + workFlowStatus.getPmStatus().getStatusId());
		}else if (outcome.contains(BpmOutcomeConst.BPM_APPROVE)){			
			workFlowStatus.setPmStatus(getPmStatusByStatusId(QTStatusConst.LOA_CANCELLED));	
			log.info("-----Check 62509L------ " + workFlowStatus.getPmStatus().getStatusId());
		}
		workFlowStatus.setScLoa(scLoa);
		workFlowStatus.setCurrent(true);
		scLoa.setCurrentStatus(workFlowStatus);

		reqDTO.setScWorkFlowStatus(workFlowStatus);		
		workFlowStatusSO.updateCurrentWorkflowStatus(reqDTO);

	}
	
	/**
	 * @param awardSupplierDataType
	 */
	private void populatePayLoad(SCAwardSupplierDataType awardSupplierDataType,
			ScLoa scLoa) {

		String title = scLoa.getScQuote().getTitle();
		// To cater for migration QTs
		if (StringUtils.isNotBlank(title)
				&& title.length() > 500) {
			title = title.substring(0, 500);
		}
		awardSupplierDataType.setTitle(title);
		awardSupplierDataType.setSupplierId(scLoa.getScLoiLoa().getSmSupplier()
				.getSupplierId().toString());
		awardSupplierDataType.setAwardId(scLoa.getLoaId());
		awardSupplierDataType.setSourceId(scLoa.getScQuote().getQuoteId());
		awardSupplierDataType.setSourceNumber(scLoa.getScQuote().getQuoteNo());

	}
	
	/**
	 * only if qt no prefix is 'QT'
	 * @return
	 */
	public boolean qtPrefixIsQT(){		
		if(this.getManagedBean().getScQt()!=null){
			String qtNo = this.getManagedBean().getScQt().getQtNo();
			if(!StringUtils.isBlank(qtNo)){
				return ParameterConst.DOCTYPE_QUOTATION.equalsIgnoreCase(qtNo.trim().substring(0, ParameterConst.DOCTYPE_QUOTATION.length()));
			}
		}
		return false;
	}
	
	
	/* 19336#99 - start */
	public String getManagedBeanLoaContractAgencyOrgName(){
		try{
			return getManagedBean().getScLoa().getContractAgency().getOrgName();
		}
		catch(Exception ex){
			return "";
		}
	}
	
	public String getManagedBeanSelectedPtjOrgName(){
		try{
			return getManagedBean().getSelectedPtj().getOrgName();
		}
		catch(Exception ex){
			return "";
		}
	}
	/* 19336#99 - end */
	
	/**
	 * Check either SQ is valid or not (SQ only valid for 60 days after it closing date)
	 */
	public boolean isSqStillValid(){		
		
		Boolean validDate = true;	
		Long currentStatusId = getManagedBean().getScLoa().getPmStatus().getStatusId();
		ScQuote scQuote = getManagedBean().getScLoa().getScQuote();

		Integer sqTimeValidity = NgepApplicationBean.getSqTimeValidity();

		if(scQuote != null){
			
			Date currentDate = new Date();
			Date closingDate = scQuote.getEndDate();
			
			Calendar cal = Calendar.getInstance();
			cal.clear(); 
			cal.setTime(closingDate);
			cal.add(Calendar.DATE, sqTimeValidity); //add 60days to closing/end date
			
			if(currentStatusId.equals(QTStatusConst.LOA_PENDING_SUBMISSION)
			   || currentStatusId.equals(QTStatusConst.LOA_PENDING_APPROVAL)
			   || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION)
			   || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)){
				if(currentDate.compareTo(cal.getTime()) >= 0){
					validDate = false;
					sqExpiredMessageFlag = true;
				}
			}	
		}			
		return validDate;	
	}
	
	public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScLoa scLoa = getManagedBean().getScLoa();
			ScLoaCancelAttachment attachment = new ScLoaCancelAttachment();

			List<ScLoaCancelAttachment> attachments = scLoa.getScLoaCancelAttachments();
			if (null == attachments) {
				attachments = new ArrayList<ScLoaCancelAttachment>();
				scLoa.setScLoaCancelAttachments(attachments);
			}
			attachment.setScLoa(scLoa);
			attachment.setDocNo(scLoa.getLoaNo());
			attachments.add(attachment);
			uploadAttachment(event.getFile(), attachment);
		}

	}
	
	public void deleteUploadFile() {
		log.debug("deleteUploadFile - START");
		getManagedBean().getScLoa().setScLoaCancelAttachments(null);
		log.debug("deleteUploadFile - END");
	}
	
	
	
	public void validateRequiredAttachment() {
		List<ScLoaCancelAttachment> attachments = getManagedBean().getScLoa().getScLoaCancelAttachments();
		boolean noAttachment = false;

		if ((null == attachments) || (!CollectionUtils.isNotEmpty(attachments))) {
			noAttachment = true;
		}

		if (noAttachment) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { "Attachment" });
		}

	}
	
	
}

