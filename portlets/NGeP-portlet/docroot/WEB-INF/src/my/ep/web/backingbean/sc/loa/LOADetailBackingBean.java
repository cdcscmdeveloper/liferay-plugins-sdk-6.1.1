package my.ep.web.backingbean.sc.loa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.clause.IClauseSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
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
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.clause.ClauseReqDTO;
import my.ep.dto.pm.common.clause.ClauseResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmClause;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
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
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.ct.amendment.JadualHargaBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loa.LOADetailManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

// TODO: Auto-generated Javadoc
/**
 * The Class LOADetailBackingBean.
 *
 * @author Viswanath
 * 
 * @author Sri
 */
@ManagedBean(name = "lOADetailBackingBean")
@RequestScoped
public class LOADetailBackingBean extends
		SourcingBaseBackingBean<LOADetailManagedBean> {
	
	/** The log. */
	public static Logger log = EpLogger.getLogger(LOADetailBackingBean.class);
	
	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	/** The text before sign. */
	private String textBeforeSign;

	/** The flag. */
	private boolean flag;

	private static final String  BRANCH_INFO_TITLE = "Branch Information";
	
	private static final String MALAY_NA = "T/B";
	
	/**
	 * Instantiates a new LOA detail backing bean.
	 */
	public LOADetailBackingBean() {
		log.info("inside LOADetailBackingBean constructor");
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
	 * Edits the loa action.
	 * page navigate to loa details page.
	 * @param scLoa the sc loa
	 * @return the string
	 */
	public String editLOAAction(ScLoa scLoa) {
		debugLogger("editLOAAction");
		try {
			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoad();

			String language = new FacesScopes().getUserLocale().getLanguage();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setLanguageCode(language);
			
			List<ScLoiLoa> supplierLoiLoas = new ArrayList<ScLoiLoa>();
			if(getManagedBean().getSuppLoiLoaMap() != null && getManagedBean().getSuppLoiLoaMap().size()>0){
				Map<Long, List<ScLoiLoa>> suppLoiLoaMap = getManagedBean().getSuppLoiLoaMap();
				if(scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getSmSupplier() != null){
					List<ScLoiLoa> loiloas = suppLoiLoaMap.get(scLoa.getScLoiLoa().getSmSupplier().getSupplierId());
					supplierLoiLoas = loiloas;
				}
			}
			
			scLoa.setSameSupplierLoiLoaList(supplierLoiLoas);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOADtl(reqDTO);
			scLoa = resDTO.getScLoa();
			this.getManagedBean().setScQt(resDTO.getScQt());
			String currentUser = new FacesScopes().getUserSessionBean().getLoginId();
			if (awardDataType != null){
				String letterCreator = awardDataType.getLetterCreator();

				if (scLoa != null
						&& ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType())) {
					if(StringUtils.isBlank(letterCreator)) {
						if (new FacesScopes()
						.hasRoleUser(RoleCode.DESK_OFFICER.value())) {
							awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
							awardDataType.setDeskOfficer(currentUser);
						} else {
							awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_REQUISITIONER);
							awardDataType.setRequisitioner(currentUser);
						}
					} else {
						if(letterCreator.equals(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER)) {
							awardDataType.setDeskOfficer(currentUser);
						} else {
							awardDataType.setRequisitioner(currentUser);
						}
					} 

				} else {
					if(StringUtils.isBlank(letterCreator)) { 
						if (new FacesScopes()
						.hasRoleUser(RoleCode.DESK_OFFICER.value())) {
							awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
							awardDataType.setDeskOfficer(currentUser);
						} else {
							awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_SECRETARY);
							awardDataType.setSecretary(currentUser);
						}
					} else {
						if(letterCreator.equals(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER)) {
							awardDataType.setDeskOfficer(currentUser);
						} else {
							awardDataType.setSecretary(currentUser);
						}
					} 
				}
			}

			if(scLoa.isBrandNew()) {
				setUserDetails(scLoa);
				scLoa.setContractAgency(null);
				resDTO.setScLoa(scLoa);
			}
			
			populateLOADetails(resDTO);
			getManagedBean().setEditFlag(Boolean.TRUE);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			if(getManagedBean().getBackToPreviousPage() == null || getManagedBean().getBackToPreviousPage().isEmpty()){
				getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			}

		} finally {
			//qtAwardSO = null;
		}
		return QTConstants.LOA_PAGE;
	}
	
	
	/**
	 * Edits the supplier loa action.
	 * page navigate to loa supplier page.
	 * @param loaId the loa id
	 */
	public void editSupplierLOAAction(Long loaId) {
		debugLogger("editSupplierLOAAction");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);

			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrieveSupplierLOADtl(reqDTO);

			ScLoa scLoa = resDTO.getScLoa();
			getManagedBean().setScLoa(scLoa);
			
			// Supplier Bank Account
			getManagedBean().setBankList(resDTO.getSmSupplierBanks());
			if (!scLoa.isBrandNew()) {
				getManagedBean().setSelectedBankId(
						getSelectedBankIdByScLoa(scLoa));
			}
			
			// Setting OverallContractAmount
			calculateOverallCtAmtForSupplier();
			isGSTRegActive();
			
			// Setting BI Report
			List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
			if (scLoaReports != null && !scLoaReports.isEmpty()) {
				for (ScLoaReport report : scLoaReports) {
					// Check whether Branch Info attachment is existed or not
					PmDocument document = report.getPmDocument();
					if (null != document && SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
							.equalsIgnoreCase(document.getDocDefinitionKey())) {
						getManagedBean().setBranchInfoReport(report);
						break;
					}
				}
			}
			
			// Supplier Witness
			ScLoaWitness loaSupplierWitness = null;
			if(scLoa.getScLoaWitnesses() != null){
				List<ScLoaWitness> scLoaWitnesses = scLoa.getScLoaWitnesses();
				for (ScLoaWitness scLoaWitness : scLoaWitnesses) {
					String partyType = scLoaWitness.getPartyType();

					if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER
							.equals(partyType)) {
						loaSupplierWitness = scLoaWitness;
					}
				}

				if (loaSupplierWitness == null) {
					loaSupplierWitness = new ScLoaWitness();
					loaSupplierWitness
					.setPartyType(ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER);
					loaSupplierWitness.setScLoa(scLoa);
					scLoaWitnesses.add(loaSupplierWitness);
				}
			}
			getManagedBean().setLoaSupplierWitness(loaSupplierWitness);
			ScQt scQt = resDTO.getScQt();
			if(scQt != null){
				getManagedBean().setScQt(resDTO.getScQt());
			}
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
			
			if(scLoa.isBondReq()){
				scLoa = calcBondAmount(scLoa, Boolean.FALSE);
				if (scLoa.getBondAmt() != null) {
					getManagedBean().setBondAmountWord(
							NumberWordUtil.convert(scLoa.getBondAmt().toString()));
				}	
			}
			
			// show default clause
			if(scLoa.getScQtDeductions() != null && scLoa.getScQtDeductions().size()>0){
				getManagedBean().setSelectedScQtDeduction(scLoa.getScQtDeductions().get(0));
			}
			
			// bond resp due date + 30 days.
			Date issueDate  = scLoa.getIssueDate();
			Date bondRespDueDate = DateUtils.addDays(issueDate, 30);
			scLoa.setBondResDueDate(bondRespDueDate);
			
			// loa signer
			if(scLoa.getContractSigner() != null){
				getManagedBean().setContractSignerUser(scLoa.getContractSigner());
			}
			
			// check whether loa from s1 or s2.
			if(scLoa.getScLoiLoa() != null){
				if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
					// For s2
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
					formFulfilmentSchedule(scLoa);
				}
			}
			
			generateDeclarationFromVelocity(scLoa, scQt);
			
			// Filter skop kerja dan perkhidmatan repo.
			filterSkopKerjaDanPerkhidmatanRepo(scLoa);
			
			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(scQt);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.SUPPLIER_LOA_PAGE);

		} catch (Exception e) {
			handleException(e);
		} finally {
			//qtAwardSO = null;
		}
	}

	/**
	 * Pre render page navigation to loa page based on task id
	 * @return void
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			Long loaId = Long.valueOf(dpDataType.getAwardId());

			getManagedBean().setSourceId(sourceId);
			getManagedBean().setPayLoad(dpDataType);

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);
			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrieveLOADtlByLoaId(reqDTO);
			if (resDTO.getScLoa() != null) {
				ScLoa scLoa = resDTO.getScLoa();

				// active tab index for loa contents
				if(getManagedBean().getCurrentStatusId() != null){
					if(QTStatusConst.LOA_PENDING_VERIFICATION.equals(getManagedBean().getCurrentStatusId())){
						if(scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getDocType() != null){
							if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
								getManagedBean().setSelectedTabIndex(6);
							} else{
								getManagedBean().setSelectedTabIndex(5);
							}
						}
					}
				}

				editLOAAction(scLoa);
			}
			getManagedBean().setEditFlag(Boolean.FALSE);
			
			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(getManagedBean().getScQt());

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.LOA_PAGE);
			
		} finally{
			
		}
	}
	
	/**
	 * View loa.
	 *
	 * @param loaId the loa id
	 */
	public void viewLOA(long loaId) {
		debugLogger("viewLOA");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);
			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrieveLOADtlByLoaId(reqDTO);
			if (resDTO.getScLoa() != null) {
				ScLoa scLoa = resDTO.getScLoa();
				editLOAAction(scLoa);
			}
			getManagedBean().setEditFlag(Boolean.FALSE);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.LOA_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	
	/**
	 * Pre render page navigation to loa supplier page based on task id
	 * @return void
	 */
	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			Long loaId = Long.valueOf(dpDataType.getAwardId());

			getManagedBean().setSourceId(sourceId);
			getManagedBean().setPayLoad(dpDataType);
		
			editSupplierLOAAction(loaId);
		} finally {
		}
	}

	/**
	 * Freq schedule validation.
	 *
	 * @param scQtSchedule the sc qt schedule
	 */
	private void freqScheduleValidation(ScQtSchedule scQtSchedule) {
		debugLogger("frequencyScheduleValidation");
		FacesScopes facesScopes = new FacesScopes();
		Calendar startDate = null;
		Calendar endDate = null;
		
		if(scQtSchedule.getStartYear() == null || scQtSchedule.getStartYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.year",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(scQtSchedule.getStartMonth() == null || scQtSchedule.getStartMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(scQtSchedule.getEndYear() == null || scQtSchedule.getEndYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.year",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(scQtSchedule.getEndMonth() == null || scQtSchedule.getEndMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}

		ScLoa loa = 	getManagedBean().getScLoa();
		Calendar expDate = Calendar.getInstance();
		expDate.setTime(loa.getContractExpDate());
		expDate.add(Calendar.MONTH, 1);
		expDate.set(Calendar.DAY_OF_MONTH, 1);
		expDate.add(Calendar.DATE, -1);
		Date contractExpDate = expDate.getTime();
		
		Calendar effDate =Calendar.getInstance();
		effDate.setTime(loa.getContractEffDate());
		effDate.set(Calendar.DAY_OF_MONTH, 1);
		Date contractEffDate = effDate.getTime();  
	   
	    if(scQtSchedule.getStartYear() != null && scQtSchedule.getStartMonth() != null){
	    	startDate = Calendar.getInstance();
	    	startDate.clear();
	    	startDate.set(Integer.valueOf(scQtSchedule.getStartYear()), Integer.valueOf(scQtSchedule.getStartMonth())-1, 1, 0, 0, 0);
	    }
	   
	    if(scQtSchedule.getEndYear() != null && scQtSchedule.getEndMonth() != null){
	    	endDate = Calendar.getInstance();
	    	endDate.clear();
	    	endDate.set(Integer.valueOf(scQtSchedule.getEndYear()), Integer.valueOf(scQtSchedule.getEndMonth())-1, Integer.valueOf(getDays(contractExpDate)), 0, 0, 0);
	    }
	    
	    if(startDate != null && endDate != null){
	    	if (startDate.after(endDate)) {
	    		throw new EpException(MessageConst.ERROR_760345);
	    	}

	    	if (endDate.before(startDate)) {
	    		throw new EpException(MessageConst.ERROR_760346);
	    	}

	    	if (!(startDate.getTime().after(contractEffDate)
					&& (startDate.getTime().before(contractExpDate)) || contractEffDate.equals(startDate.getTime()))) {
				throw new EpException(MessageConst.ERROR_760347);
			}

			if (!(endDate.getTime().after(contractEffDate)
					&& (endDate.getTime().before(contractExpDate)) || contractExpDate.equals(endDate.getTime()))) {
				throw new EpException(MessageConst.ERROR_760348);
			}
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
		
		// calc total award details
		if(scLoa.getScLoiLoaItems() != null && scLoa.getScLoiLoaItems().size()>0){
			scLoa.setLoaAmt(calcTotalAmount(scLoa.getScLoiLoaItems()));
		}

		// calculate performance bond amt for S2
		if (!ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType())) {
			scLoa = calcBondAmount(scLoa,false);
		}
		
        //Supplier overall contract amount
		BigDecimal overallCtAmout = overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt());
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
		//if (scLoa.isBrandNew()) {
		handleBranchDetailsChanged();
		//}
		
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
		Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOA_RESPONSE_PERIOD);
		scLoa.setAckDueDate(DateUtils.addDays(DateUtils.getCurrentDateWithoutTime(), loaRespPeriod));

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
		
		if (scLoa.getContractType().getParameterId() != null) {
			contractTypeChanged();
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
		scLoa.setScLoaRemarks(initializeRemarks(scLoa, null,
				scLoa.getScLoaRemarks(), ScLoaRemark.class));

		if (scLoa.getBondAmt() != null) {
			getManagedBean().setBondAmountWord(
					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		}
		checkPerformanceBondMandatory();
		checkAgreementMandatory();
		
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
					String fromdate = dateFormat.format(scQtSchedule.getFromDate());
					String todate = dateFormat.format(scQtSchedule.getToDate());
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
			validateDeductionPayment(scQtDeduction);
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
	 * Validate deduction payment.
	 *
	 * @param scQtDeduction the sc qt deduction
	 */
	private void validateDeductionPayment(ScQtDeduction scQtDeduction) {
		List<ScQtDeduction> scQtDeductions = getManagedBean().getScLoa().getScQtDeductions();
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
	
	/**
	 * Adds the loa content action.
	 */
	public void addLoaContentAction() {
		debugLogger("addLoaContentAction");

		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();

		if (scLoaReports == null) {
			scLoaReports = new ArrayList<ScLoaReport>();

			scLoa.setScLoaReports(scLoaReports);
		}

		ScLoaReport scLoaReport = new ScLoaReport();
		scLoaReport.setScLoa(scLoa);
		scLoaReport.setScLoaReportAttachment(new ScLoaReportAttachment());

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
			scLoaReport.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER); //AQ
		} finally {
			parameterSO = null;
		}

		scLoaReports.add(scLoaReport);
	}
	
	/**
	 * Delete loa document.
	 */
	public void deleteLoaDocument() {
		List<ScLoaReport> result = new ArrayList<ScLoaReport>();
		ScLoa scLoa = getManagedBean().getScLoa();
		boolean flag = true;
		List<ScLoaReport> selectedScLoaReports = scLoa.getScLoaReports();
		if (selectedScLoaReports != null && selectedScLoaReports.size() > 0) {
			for (ScLoaReport scLoaReport : selectedScLoaReports) {
				if (!scLoaReport.isSelected()) {
					result.add(scLoaReport);
				} else {
					flag = false;
				}
			}
		}
		if (flag) {
			throw new EpException(MessageConst.ERROR_760158);
		}
		scLoa.setScLoaReports(result);
	}
	
	/**
	 * Select all documents.
	 *
	 * @param event the event
	 */
	public void selectAllDocuments(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		if (scLoaReports != null && scLoaReports.size() > 0) {
			for (ScLoaReport scLoaReport : scLoaReports) {
				if (isSelectAll && null == scLoaReport.getPmDocument()) {
					scLoaReport.setSelected(true);
				} else {
					scLoaReport.setSelected(false);
				}
			}
		}
	}
	
	
	/**
	 * Select all clauses.
	 *
	 * @param event the event
	 */
	public void selectAllClauses(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScQtDeductionClause> deductionClauses = getManagedBean().getSelectedScQtDeduction().getScQtDeductionClauses();
		if (deductionClauses != null && deductionClauses.size() > 0) {
			for (ScQtDeductionClause deductionClause : deductionClauses) {
				if (isSelectAll && null == deductionClause.getPmClause()) {
					deductionClause.setSelected(true);
				} else {
					deductionClause.setSelected(false);
				}
			}
		}
	}
	

	/**
	 * Handle file upload.
	 *
	 * @param event the event
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		ScLoa scLoa = this.getManagedBean().getScLoa();
		if (event.getFile() != null) {
			String attachmentDocType = getManagedBean().getAttachmentDocType();
			ScAttachment scAttachment = null;

			if (ParameterConst.DOCTYPE_LETTER_OF_AWARD
					.equals(attachmentDocType)) {
				
				ScLoaAttachment scLoaAttachment = scLoa.getScLoaAttachment();
				if (null == scLoaAttachment) {
					scLoaAttachment = new ScLoaAttachment();
					scLoaAttachment.setScLoa(scLoa);

					scLoa.setScLoaAttachment(scLoaAttachment);
				}

				if(StringUtils.isBlank(scLoa.getLoaNo())){
					// Generate Loa Temp No.
					scLoaAttachment.setDocNo(getQtAwardSO().generateLoaTempNo());	
				} else{
					scLoaAttachment.setDocNo(scLoa.getLoaNo());	
				}

				scLoaAttachment.setScLoa(scLoa);
				scAttachment = scLoaAttachment;

			} else {
				ScLoaReport selectedScLoaReport = this.getManagedBean()
						.getSelectedScLoaReport();
				ScLoaReportAttachment scLoaReportAttachment = selectedScLoaReport
						.getScLoaReportAttachment();
				if (null == scLoaReportAttachment) {
					scLoaReportAttachment = new ScLoaReportAttachment();
					scLoaReportAttachment.setScLoaReport(selectedScLoaReport);

					selectedScLoaReport
							.setScLoaReportAttachment(scLoaReportAttachment);
				}
				
				if (StringUtils.isBlank(scLoa.getLoaNo())) {
					// Generate Loa Temporary No.
					if(null == getManagedBean().getTempLoaNo()){
						getManagedBean().setTempLoaNo(getQtAwardSO().generateLoaTempNo());
						scLoaReportAttachment.setDocNo(getManagedBean().getTempLoaNo());
					} else{
						scLoaReportAttachment.setDocNo(getManagedBean().getTempLoaNo());
					}
					
				} else{
					scLoaReportAttachment.setDocNo(scLoa.getLoaNo());
				}
				scLoaReportAttachment.setScLoaReport(selectedScLoaReport);
				scAttachment = scLoaReportAttachment;
			}

			uploadAttachment(event.getFile(), scAttachment);
			getManagedBean().setTempFilePath(getActualFilePath(scAttachment));
		}
	}

	/**
	 * Download report as pdf or xlsx files in Letter of Acceptance.
	 *
	 * @param scLoaReport the sc loa report
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadReport(ScLoaReport scLoaReport) throws IOException {
		debugLogger("downloadReport");
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();
		scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
		downloadFile(scLoaReportAttachment);
	}

	/**
	 * Generate document attachment.
	 *
	 * @param scLoaReport the sc loa report
	 * @return the sc loa report attachment
	 */
	private ScLoaReportAttachment generateDocumentAttachment(
			ScLoaReport scLoaReport) {
		String definitionType = scLoaReport.getDefinationType()
				.getParameterCode();
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();

		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {
			try {
				String docDefinitionKey = scLoaReport.getPmDocument()
						.getDocDefinitionKey();
				ScLoa scLoa = getManagedBean().getScLoa();
				Long statusId = scLoa.getPmStatus().getStatusId().longValue();
				StringBuilder filename = new StringBuilder();
				//replace white space due to firefox 
				filename.append(scLoaReport.getTitle().replaceAll("\\s",""));
				if (scLoaReportAttachment == null) {
					scLoaReportAttachment = new ScLoaReportAttachment();
					scLoaReportAttachment.setScLoaReport(scLoaReport);
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());
					
					if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateContractSummaryPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateAgreementLetterPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa,
								getManagedBean().getScQt());
					} else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateDeclarationPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						// saveScheduleList();
						generateFrequencySchedulePdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa, getManagedBean().getScQt());
					} else if (SourcingConstants.DOC_DEF_KEY_JADUAL_HARGA
							.equals(docDefinitionKey)) {
						filename.append(".xlsx");
						scLoaReportAttachment.setFileName(filename.toString());
						formJadualHarga((ScAttachment) scLoaReportAttachment,
								scLoa);
						
					}else if (SourcingConstants.DOC_DEF_KEY_SPECIFICATION
							.equals(docDefinitionKey)) {
						filename.append(".xlsx");
						scLoaReportAttachment.setFileName(filename.toString());
						formSpecification((ScAttachment) scLoaReportAttachment,
								scLoa);
						
					}else if (SourcingConstants.DOC_DEF_KEY_FORMAT_BOND_EXECUTION
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateBondLetterFromVelocityToPdf((ScAttachment) scLoaReportAttachment,scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_LOI_NEGOTIATIONS_OUTCOME
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateNegotiationResultPdf((ScAttachment) scLoaReportAttachment);
						
					} else if (SourcingConstants.DOC_DEF_KEY_SCOPE_SERVICES
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
					}
					
				}else if(scLoaReportAttachment != null && 
						QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId) || QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId)
						|| QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId)){
					// if pending supplier acknowledgement , always regenerate 
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());
					if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateContractSummaryPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateAgreementLetterPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa,
								getManagedBean().getScQt());
					} else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
							.equals(docDefinitionKey)) {	
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateDeclarationPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
						
					} else if (SourcingConstants.DOC_DEF_KEY_JADUAL_HARGA
							.equals(docDefinitionKey)) {
						filename.append(".xlsx");
						scLoaReportAttachment.setFileName(filename.toString());
						formJadualHarga((ScAttachment) scLoaReportAttachment,
								scLoa);
						
					} else if (SourcingConstants.DOC_DEF_KEY_LOI_NEGOTIATIONS_OUTCOME
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateNegotiationResultPdf((ScAttachment) scLoaReportAttachment);
					} else if (SourcingConstants.DOC_DEF_KEY_SCOPE_SERVICES
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
					} else if (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						// saveScheduleList();
						generateFrequencySchedulePdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa, getManagedBean().getScQt());
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}

			// TODOs
			if (scLoaReportAttachment.getFileName() != null
					&& scLoaReportAttachment.getFilePath() != null) {
				scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			}
		}

		return scLoaReportAttachment;
	}


	/**
	 * Download all reports as  pdf or xlsx files in Letter of Acceptance.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");

		List<ScLoaReport> scLoaReports = getManagedBean().getScLoa()
				.getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();

		for (ScLoaReport scLoaReport : scLoaReports) {
			ScLoaReportAttachment scLoaReportAttachment = scLoaReport
					.getScLoaReportAttachment();
			scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
			if (scLoaReportAttachment != null
					&& StringUtils.isNotBlank(scLoaReportAttachment.getFileName())) {
				scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			}
		
			scAttachments.add(scLoaReportAttachment);
		}
	        
		downloadAllFiles("NGeP-LOA-Report", scAttachments);
	}

	/**
	 * Show verify button.
	 */
	public void showVerifyButton() {
	       
		if(getManagedBean().isVerifyBtnFlag()){
			getManagedBean().setVerifyBtnFlag(false);
		}
	}

	/**
	 * Generate contract summary pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */
	public void generateContractSummaryPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception {

		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();

		context.put("fileNo", scLoiLoa.getFileNo() + "-" + scLoa.getFileExtNo());
		context.put("docNo", "");
		context.put("title", scLoiLoa.getTitle());
		context.put("supplierName", scLoiLoa.getSupplierName());
		context.put("loaAmt", amountDF.format(scLoa.getLoaAmt()));
		context.put("fulfilmentType",
				getParameterDesc(scLoiLoa.getFulfilmentTypeCode()));

		StringBuilder deductionTypes = new StringBuilder();
		List<ScQtDeduction> scQtDeductions = scLoa.getScQtDeductions();
		if (scQtDeductions != null) {
			for (ScQtDeduction scQtDeduction : scQtDeductions) {
				if (deductionTypes.length() > 0) {
					deductionTypes.append(",");
				}
				deductionTypes.append(getParameterDesc(scQtDeduction
						.getDeductionType()));
			}
		}
		context.put("deductionTypes", deductionTypes.toString());

		context.put("contractDuration", scLoa.getContractDuration());
		
		if(null == scLoa.getContractEffDate()){
			context.put("contractEffDate","");
		} else{
			context.put("contractEffDate",
					dateSDF.format(scLoa.getContractEffDate()));
		}
		
		if(null == scLoa.getContractExpDate()){
			context.put("contractExpDate","");
		} else{
			context.put("contractExpDate",
					dateSDF.format(scLoa.getContractExpDate()));
		}
		

		String bondAmtStr = "";
		if (scLoa.getBondAmt() != null) {
			bondAmtStr = amountDF.format(scLoa.getBondAmt());
		}
		context.put("bondAmt", bondAmtStr);
		
		String gstAmount = MALAY_NA;
		String overallctAmt = "";
		BigDecimal loaAmount = scLoa.getLoaAmt();
		if (scLoa.getGstAmt() != null) {
			gstAmount = amountDF.format(scLoa.getGstAmt());
			overallctAmt = amountDF.format(loaAmount.add(scLoa.getGstAmt()));
		} else {
			overallctAmt = amountDF.format(scLoa.getLoaAmt());
		}
		context.put("gstAmt",gstAmount );
		context.put("overallctAmt", overallctAmt);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_CONTRACT_SUMMARY, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate agreement letter pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @param scQt the sc qt
	 * @throws Exception the exception
	 */
	public void generateAgreementLetterPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa, ScQt scQt) throws Exception {
		populateAttachmentDetails(attachment);
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		PmUser ackByUser = scLoa.getAckBy();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		DecimalFormat percentageDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));
		VelocityContext context = new VelocityContext();
		
		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() +":" + fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		IAddressSO addressSO = null;
		PmAddressType pmAddressType = null;
		IOrgProfileSO orgProfileSO = null;
		try {
			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);

			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			reqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);

			if (resDTO.getAddressTypeList().size() > 0) {
				pmAddressType = resDTO.getAddressTypeList().get(0);
			}

			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileDtlByProfileId(orgProfileReqDTO);

			if (orgProfileResDTO.getPmOrgProfile() != null) {
				context.put("agency", orgProfileResDTO.getPmOrgProfile()
						.getOrgName());
				context.put("ministry", orgProfileResDTO.getPmOrgProfile()
						.getParentOrgProfile().getOrgName());
			}

		} finally {
			addressSO = null;
			orgProfileSO = null;
		}

		String agencyAddress = "";
		String agencyPhoneNo = "";
		String agencyFaxNo = "";
		if (pmAddressType != null) {
			PmAddress pmAddress = pmAddressType.getPmAddress();

			agencyAddress = getFullAddress(pmAddress);
			agencyAddress = agencyAddress.replaceAll("\r\n", "<br />");
			agencyPhoneNo = pmAddress.getPhoneFull();
			agencyFaxNo = pmAddress.getFaxFull();
		}
		context.put("agencyAddress", agencyAddress);
		context.put("agencyPhoneNo", agencyPhoneNo);
		context.put("agencyFaxNo", agencyFaxNo);
		
		String fileNo ="";
		if(!StringUtils.isBlank(scLoiLoa.getFileNo())){
			fileNo = scLoiLoa.getFileNo();
			if(fileNo.length()>30){
				fileNo = splitLongLengthString(fileNo, 29);
			}
		}
		
		context.put("fileNo", fileNo + "-" + scLoa.getFileExtNo());
		context.put("loaNo", scLoa.getLoaNo());

		String issueDate = "";
		if (scLoa.getIssueDate() != null) {
			issueDate = getLocalizeDate(scLoa.getIssueDate(), myLocale);
		}
		context.put("issueDate", issueDate);

		context.put("supplierName", scLoiLoa.getSupplierName());
		String supplierAddress = "";
		if (scLoa.getSupplierAddress() != null) {
			supplierAddress = getFullAddress(scLoa.getSupplierAddress());
			supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
			supplierAddress = supplierAddress.replaceAll("&", "&#38;");
		}
		context.put("supplierAddress", supplierAddress);
		String supplierPhoneNo = scLoa.getSupplierPhoneNo();
		if (supplierPhoneNo == null) {
			supplierPhoneNo = "";
		}
		context.put("supplierPhoneNo", supplierPhoneNo);
		String supplierFaxNo = scLoa.getSupplierFaxNo();
		if (supplierFaxNo == null) {
			supplierFaxNo = "";
		}
		context.put("supplierFaxNo", supplierFaxNo);
		String supplierMobNo = scLoa.getSupplierMobNo();
		if (supplierMobNo == null) {
			supplierMobNo = "";
		}
		context.put("supplierMobNo", supplierMobNo);
		String supplierEmail = scLoa.getSupplierEmail();
		if (supplierEmail == null) {
			supplierEmail = "";
		}
		context.put("supplierEmail", supplierEmail);

		String procurementMode = "";
		String s1ors2 = "";
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			 s1ors2 = "tawaran pelawaan";
			procurementMode = SourcingConstants.PROCUREMENT_MODE_SIMPLE_QUOTE;

		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			s1ors2 = "tender/ sebut";

			if (scQt != null && scQt.getProcurementMode() != null) {
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
				reqDTO.setLanguageCode(CommonConstants.LANGUAGE_CODE_MALAY);
				reqDTO.setParameterCode(scQt.getProcurementMode().getParameterCode());
				IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);

				List<PmParameterDesc> procurementModes = resDTO.getParameterDescList();
				if(procurementModes != null && procurementModes.size() >0){
					procurementMode = procurementModes.get(0).getCodeName();
				}
			}
		}
		context.put("s1ors2", s1ors2); 
		context.put("procurementMode", procurementMode); 
		
		context.put("title", scLoiLoa.getTitle().replaceAll("&", "&#38;"));
		
		context.put("hasLoi", false); // TODO
		context.put("bondReq", scLoa.isBondReq());

		String bondAmt = "";
		if (scLoa.getBondAmt() != null) {
			bondAmt = amountDF.format(scLoa.getBondAmt());
		}
		context.put("bondAmt", bondAmt);
		

		String bondPercentage = "";
		String bondPercentageText = "";
		if (scLoa.getBondPercentage() != null) {
			bondPercentage = percentageDF.format(scLoa.getBondPercentage());
			bondPercentageText  = NumberWordUtil.convert(scLoa.getBondPercentage().toString());
		}
		context.put("bondPercentageText", bondPercentageText); 
		context.put("bondPercentage", bondPercentage);
		
		String bondExpDate = "";
		//bond exp date should be one year after the end of the contract period
		if(scLoa.getContractExpDate() != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(scLoa.getContractExpDate());
			cal.add(Calendar.YEAR, 1); //add one year
			bondExpDate = getLocalizeDate(cal.getTime(), myLocale);
		} 
		context.put("bondExpDate", bondExpDate); 

		/*
		 * String bondResDueDays = ""; if (scLoa.getBondResDueDate() != null) {
		 * bondResDueDays =
		 * DateUtils.getDaysDifference(DateUtils.getCurrentDate(),
		 * scLoa.getBondResDueDate()) + ""; }
		 */

		// bond response due days  - 30
		context.put("bondResDueDays", 30);

		String ackDate = "";
		if (scLoa.getAckDate() != null) {
			ackDate = DateUtils.getLocalizeDate(scLoa.getAckDate(), myLocale);
		}
		context.put("ackDate", ackDate);
		
		context.put("insWarrantyReq", scLoa.isInsWarrantyReq());

		String contractEffDate = "";
		if (scLoa.getContractEffDate() != null) {
			contractEffDate = DateUtils.getLocalizeDate(scLoa.getContractEffDate(), myLocale);
		}
		context.put("contractEffDate", contractEffDate);
		

		String LoaSigner = "";
		if(getManagedBean().getContractSignerUser() != null){
			if(getManagedBean().getContractSignerUser().getUserName() != null && !getManagedBean().getContractSignerUser().getUserName().isEmpty()){
				LoaSigner = getManagedBean().getContractSignerUser().getUserName();
			}
		}
		context.put("loaSigner", LoaSigner);	

		String loaSignerDesig = "";
		if(getManagedBean().getContractSignerUser().getUserName() != null && !getManagedBean().getContractSignerUser().getUserName().isEmpty()){
			loaSignerDesig = scLoa.getLoaSignerDesig();

			if(loaSignerDesig.length()>86){
				loaSignerDesig = splitLongLengthString(loaSignerDesig, 86);
			}
		}
		context.put("loaSignerDesignation", loaSignerDesig);
		// Supplier user details
		String supplierUser = "";
		String supplierIcNo = "";
		String supplierDesignation = "";
		if (ackByUser != null) {
			supplierUser = ackByUser.getUserName();
			supplierIcNo = ackByUser.getIdentificationNo();
			supplierDesignation = ackByUser.getDesignation();
		}
		context.put("supplierUser", supplierUser);
		context.put("supplierIcNo", supplierIcNo);
		context.put("supplierDesignation", supplierDesignation);
		
		// with LOI and without LOI content for LOA to display in acceptance letter
		if(scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getScLois() != null 
				&& scLoa.getScLoiLoa().getScLois().size()>0){
			context.put("loiContentFlag", true);
		} else{
			context.put("loiContentFlag", false);
		}
		
		// Supplier Witness
		String supplierWitnessUser = "";
		String supplierWitnessIcNo = "";
		String supplierWitnessDesignation = "";
		String supplierWitnessAddress = "";
		String supplierWitnessPhoneNo = "";
		String supplierWitnessFaxNo = "";
		String supplierWitnessMobNo = "";
		String supplierWitnessEmail = "";
		ScLoaWitness supplierWitness = null;
		if(scLoa.getScLoaWitnesses() !=null){
			for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
				String partyType = scLoaWitness.getPartyType();

				if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER.equals(partyType)) {
					supplierWitness = scLoaWitness;

					break;
				}
			}
		}
		if (supplierWitness != null) {
			supplierWitnessUser = supplierWitness.getWitnessName();
			supplierWitnessIcNo = supplierWitness.getIdentificationNo();
			supplierWitnessDesignation = supplierWitness.getDesignation();
			supplierWitnessAddress = supplierWitness.getAddress();
			supplierWitnessPhoneNo = supplierWitness.getPhoneNo();
			supplierWitnessFaxNo = supplierWitness.getFaxNo();
			supplierWitnessMobNo = supplierWitness.getMobileNo();
			supplierWitnessEmail = supplierWitness.getEmail();
		}
		context.put("supplierWitnessUser", supplierWitnessUser);
		context.put("supplierWitnessIcNo", supplierWitnessIcNo);
		context.put("supplierWitnessDesignation", supplierWitnessDesignation);
		context.put("supplierWitnessAddress", supplierWitnessAddress);
		context.put("supplierWitnessPhoneNo", supplierWitnessPhoneNo);
		context.put("supplierWitnessFaxNo", supplierWitnessFaxNo);
		context.put("supplierWitnessMobNo", supplierWitnessMobNo);
		context.put("supplierWitnessEmail", supplierWitnessEmail);

		// Bank account details.
		String bankAccNo = scLoa.getBankAccNo();
		if (bankAccNo == null) {
			bankAccNo = "";
		}
		context.put("bankAccNo", bankAccNo);
		String bankName = scLoa.getBankName();
		if (bankName == null) {
			bankName = "";
		}
		context.put("bankName", bankName.replaceAll("&", "&#38;"));
		String bankBranch = scLoa.getBankBranch();
		if (bankBranch == null) {
			bankBranch = "";
		}
		context.put("bankBranch", bankBranch);
		String bankAddress = "";
		if (scLoa.getBankAddress() != null) {
			bankAddress = getFullAddress(scLoa.getBankAddress());
			bankAddress = bankAddress.replaceAll("\r\n", "<br />");
			//bankAddress = bankAddress.replaceAll("&", "&#38;");
		}
		context.put("bankAddress", bankAddress);
       
		// Setting GST info
		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
	    String gstRegFlag = "false";
		if(supplier != null && supplier.getGstRegisteredFlag()== DbValueConst.RECORD_STATUS_ACTIVE){
			gstRegFlag = "true";
		}
		context.put("gstRegFlag", gstRegFlag);
		setGstInfoForAgreementLetter(context, scLoa, amountDF, myLocale);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_AGREEMENT_LETTER, context,
				new FileOutputStream(getActualFilePath(attachment)));
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
	 * Generate declaration pdf from velocity in Performance Bond.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */ 
	public void generateDeclarationPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception {
		String termsConditions = generateDeclarationFromVelocity(scLoa,getManagedBean().getScQt());

		populateAttachmentDetails(attachment);

		DocumentGenerator.getInstance().generatePdf(
				new FileOutputStream(getActualFilePath(attachment)),
				termsConditions);
	}

	/**
	 * Generate declaration from velocity.
	 *
	 * @param scLoa the sc loa
	 * @param scQt the sc qt
	 * @return the string
	 * @throws Exception the exception
	 */
	private String generateDeclarationFromVelocity(ScLoa scLoa,ScQt scQt)
			throws Exception {
		PmUser pmUser = scLoa.getAckBy();
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();
		String procurementMode = null;
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			procurementMode = SourcingConstants.PROCUREMENT_MODE_SIMPLE_QUOTE;
		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa()
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

		String userName = "Nama Pembekal";
		String icNo = "No. Kad Pengenalan";
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
				.getHtmlFromVelocity(SourcingConstants.LOA_DECLARATION, context);

		getManagedBean().setTermsConditions(termsConditions);

		return termsConditions;
	}

	/**
	 * Gets the agreement letter.
	 *
	 * @return the loa stream content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public StreamedContent getLoaStreamContent() throws IOException {
		StreamedContent loaStreamContent = getManagedBean()
				.getLoaStreamContent();

		if (loaStreamContent == null) {
			ScLoaReport agreementLetterReport = null;
			for (ScLoaReport scLoaReport : getManagedBean().getScLoa()
					.getScLoaReports()) {
				if(null != scLoaReport.getPmDocument()){
					String docDefinitionKey = scLoaReport.getPmDocument()
							.getDocDefinitionKey();

					if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
							.equals(docDefinitionKey)) { 
						agreementLetterReport = scLoaReport;

						break;
					}
				}
			}

			if (agreementLetterReport != null) {
				ScLoaReportAttachment agreementLetterAttachment = generateDocumentAttachment(agreementLetterReport);
				agreementLetterReport
						.setScLoaReportAttachment(agreementLetterAttachment);

				loaStreamContent = getAttachmentStreamContent(
						agreementLetterAttachment,
						CommonConstants.FILE_CONTENT_TYPE_PDF);

				getManagedBean().setLoaStreamContent(loaStreamContent);
			}
		} else {
			loaStreamContent.getStream().reset();
		}

		return loaStreamContent;
	}

	/**
	 * Generate negotiation result pdf.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */
	private void generateNegotiationResultPdf(ScAttachment attachment) throws Exception {
		attachment.setFileName("Negotiation_Result.pdf");
		ScLoa scLoa = getManagedBean().getScLoa();
		populateAttachmentDetails(attachment);
		List<ScLoi> lois = new ArrayList<ScLoi>();
		if(getManagedBean().getSuppLoiLoaMap() != null && getManagedBean().getSuppLoiLoaMap().size()>0){
			Map<Long, List<ScLoiLoa>> suppLoiLoaMap = getManagedBean().getSuppLoiLoaMap();
			if(scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getSmSupplier() != null){
				List<ScLoiLoa> loiloas = suppLoiLoaMap.get(scLoa.getScLoiLoa().getSmSupplier().getSupplierId());
				if(loiloas != null && loiloas.size()>0){
					for (ScLoiLoa scLoiLoa : loiloas) {
						if(scLoiLoa.getScLois() != null && scLoiLoa.getScLois().size()>0){
							lois.addAll(scLoiLoa.getScLois()) ;
						}
					}
				} 
			}
		} else{
			if(scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getScLois() != null && scLoa.getScLoiLoa().getScLois().size()>0){
				lois  = scLoa.getScLoiLoa().getScLois();
			}
		}
		
		if(lois != null && lois.size()>0){
			for (ScLoi scLoi : lois) {
				calculateOfferedAndFinalPriceByItem(scLoi);
			}	
		}

		VelocityContext context = new VelocityContext();
		
		context.put("isZonal", getManagedBean().isZonal());
		context.put("scLois", lois);
		context.put("bean", this);

		DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.LOI_NEGOTIONATION_RESULT , 
				context, new FileOutputStream(getActualFilePath(attachment)));
	}

	
	/**
	 * Calculate offered and final price by item.
	 *
	 * @param scLoi the sc loi
	 */
	private void calculateOfferedAndFinalPriceByItem(ScLoi scLoi) {
		boolean isCalculateMaxPriceItem = Boolean.FALSE;// calculate max price items only.
		boolean isZonal = Boolean.FALSE;
		// filtered zone tier or tier max price items to be calculated only.
		List<ScLoiLoaItem> filteredMaxPriceItems  = filterZoneTierOrTierPriceItems(scLoi);
		
		// calc total items in award details
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

				if(isCalculateMaxPriceItem){
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
					}

					afterNegoSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(
							scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), finalPrice, quantity);
					afterNegoSubTotal = afterNegoSubTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);

					// after nego price sub total and total
					afterNegoTotalAmount = afterNegoTotalAmount.add(afterNegoSubTotal);
					afterNegoTotalAmount = afterNegoTotalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);

					scLoiLoaItem.setAfterNegoSubTotal(afterNegoSubTotal);
					//scLoiLoaItem.setSubTotal(afterNegoSubTotal);
				}
			}

			scLoi.setScLoiLoaItems(scLoiLoaItems);
			scLoi.setOfferedPriceTotal(offeredPriceAmount); // offered price total
			scLoi.setLoiAmt(afterNegoTotalAmount); // unit price total
			scLoi.setAfterNegoTotal(afterNegoTotalAmount); // after nego total
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
		List<ScLoiLoaItem> scLoiLoaItems = null;
		List<ScLoiLoaItem> groupItems = new ArrayList<ScLoiLoaItem>();
		List<ScLoiLoaItem> filteredMaxPriceItemsByQty = new ArrayList<ScLoiLoaItem>();

		if( null != loi){
			scLoiLoaItems = loi.getScLoiLoaItems();
		} else{
			scLoiLoaItems = getManagedBean().getScLoa().getScLoiLoaItems();
		}

		if(scLoiLoaItems != null && scLoiLoaItems.size()>0){
			ScLoiLoaItem scLoiLoaItem  = null;
			int lastIndex = 0;

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
							filteredMaxPriceItemsByQty.add(maxPriceItem);
							groupItems.clear();
							groupItems.add(loiLoaItem);
						}
					} else{
						if(loiLoaItem.getScRequestItem().equals(scLoiLoaItem.getScRequestItem())){
							groupItems.add(loiLoaItem);

						} else{
							scLoiLoaItem =  loiLoaItem;
							ScLoiLoaItem maxPriceItem  = findMaxPriceItem(groupItems);
							filteredMaxPriceItemsByQty.add(maxPriceItem);
							groupItems.clear();
							groupItems.add(loiLoaItem);
						}
					}
				}
				++lastIndex;

				if(scLoiLoaItems.size() == lastIndex){
					ScLoiLoaItem maxPriceItem  = findMaxPriceItem(groupItems);
					filteredMaxPriceItemsByQty.add(maxPriceItem);
					groupItems.clear();
				}
			}
		}
		return filteredMaxPriceItemsByQty;
	}

	/**
	 * Handle branch details changed.
	 *  shows the supplier address details.
	 */
	public void handleBranchDetailsChanged() {
		debugLogger("handleBranchDetailsChanged");
		ScLoa scLoa = this.getManagedBean().getScLoa();
		String selectedSupplierBranch = scLoa.getSupplierBranch();
        String supplierBranchAsHQ = QTConstants.SUPPLIER_INFO_HQ;
		if (null == selectedSupplierBranch
				|| supplierBranchAsHQ.equals(selectedSupplierBranch)) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean()
					.getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = supplierHQPersonnel.getSmAppl()
					.getSmCompanyBasic();

			for (SmCompanyAddress smCompanyAddress : smCompanyBasic
					.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(
						smCompanyAddress.getAddressType())) {
					log.info("found hq address");

					scLoa.setSupplierBranch(supplierBranchAsHQ);
					scLoa.setSupplierAddress(smCompanyAddress.getSmAddress());
					scLoa.setSupplierContact(supplierHQPersonnel.getName());
					scLoa.setSupplierPhoneNo(smCompanyBasic.getPhoneFull());
					scLoa.setSupplierFaxNo(smCompanyBasic.getFaxFull());
					scLoa.setSupplierMobNo(supplierHQPersonnel.getMobileFull());
					scLoa.setSupplierEmail(supplierHQPersonnel.getEmail());
					// Get Gst info for HQ
					Long supplierId = scLoa.getScLoiLoa().getSmSupplier()
							.getSupplierId();
					SupplierGstResDTO resDTO = getGSTInfo(supplierId, null);
					setGSTInfo(resDTO, scLoa);
					break;
				}
			}
		} else {
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean()
					.getBranchsList();

			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (selectedSupplierBranch.equals(supplierBranch
						.getBranchName().replaceAll("\\s+", " ").trim())) {
					log.info("found branch address");

					scLoa.setSupplierBranch(supplierBranch.getBranchName());
					scLoa.setSupplierAddress(supplierBranch.getSmAddress());
					
					scLoa.setSupplierPhoneNo(supplierBranch.getPhoneFull());
					scLoa.setSupplierFaxNo(supplierBranch.getFaxFull());
					SmPersonnel personnel = supplierBranch.getSmPersonnel();
					if(personnel != null){
						scLoa.setSupplierContact(personnel.getName());
						scLoa.setSupplierMobNo(personnel.getMobileFull());
						scLoa.setSupplierEmail(personnel.getEmail());
					}
					
					// Get Gst info based on branch
					Long supplierId = scLoa.getScLoiLoa().getSmSupplier().getSupplierId();
					SupplierGstResDTO resDTO = getGSTInfo(supplierId,supplierBranch.getSupplierBranchId());
					setGSTInfo(resDTO,scLoa);
					break;
				}
			}
		}
	}

	/**
	 * Handle bank details changed.
	 * shows the supplier address details for selected branch and bank.
	 */
	public void handleBankDetailsChanged() {
		debugLogger("handleBankDetailsChanged");

		Long selectedBankId = this.getManagedBean().getSelectedBankId();
		ScLoa scLoa = this.getManagedBean().getScLoa();

		if (selectedBankId != null) {
			List<SmSupplierBank> bankList = this.getManagedBean().getBankList();

			for (SmSupplierBank supplierBank : bankList) {
				if (supplierBank.getSupplierBankId().equals(selectedBankId)) {
					log.info("found supplier bank");

					scLoa.setBankAccNo(supplierBank.getAccountNo());
					scLoa.setBankName(supplierBank.getPmFinancialOrg()
							.getFinOrgName());
					scLoa.setBankBranch(supplierBank.getBankBranch());
					scLoa.setBankAddress(supplierBank.getSmAddress());

					break;
				}
			}
		}
	}

	/**
	 * Gets the selected bank id by sc loa.
	 *
	 * @param scLoa the sc loa
	 * @return the selected bank id by sc loa
	 */
	private Long getSelectedBankIdByScLoa(ScLoa scLoa) {
		Long selectedBankId = null;
		SupplierBankFK loaSupplierBankFK = new SupplierBankFK(scLoa);
		List<SmSupplierBank> bankList = this.getManagedBean().getBankList();
		if (bankList != null) {
			for (SmSupplierBank supplierBank : bankList) {
				if (new SupplierBankFK(supplierBank).equals(loaSupplierBankFK)) {
					log.info("found supplier bank");

					selectedBankId = supplierBank.getSupplierBankId();

					break;
				}
			}
		}

		return selectedBankId;
	}

	/**
	 * The Class SupplierBankFK.
	 */
	private class SupplierBankFK {
		
		/** The account no. */
		private String accountNo;
		
		/** The bank name. */
		private String bankName;
		
		/** The bank branch. */
		private String bankBranch;

		/**
		 * Instantiates a new supplier bank fk.
		 *
		 * @param smSupplierBank the sm supplier bank
		 */
		public SupplierBankFK(SmSupplierBank smSupplierBank) {
			this.accountNo = smSupplierBank.getAccountNo();
			this.bankName = smSupplierBank.getPmFinancialOrg().getFinOrgName();
			this.bankBranch = smSupplierBank.getBankBranch();
		}

		/**
		 * Instantiates a new supplier bank fk.
		 *
		 * @param scLoa the sc loa
		 */
		public SupplierBankFK(ScLoa scLoa) {
			this.accountNo = scLoa.getBankAccNo();
			this.bankName = scLoa.getBankName();
			this.bankBranch = scLoa.getBankBranch();
		}

		/**
		 * Equals.
		 *
		 * @param other the other
		 * @return true, if successful
		 */
		public boolean equals(SupplierBankFK other) {
			return getPK(this).equals(getPK(other));
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			int result = 3;

			if (this.getAccountNo() != null) {
				result = 31 * result + this.getAccountNo().hashCode();
			}

			if (this.getBankName() != null) {
				result = 31 * result + this.getBankName().hashCode();
			}

			if (this.getBankBranch() != null) {
				result = 31 * result + this.getBankBranch().hashCode();
			}

			return result;
		}

		/**
		 * Gets the account no.
		 *
		 * @return the accountNo
		 */
		public String getAccountNo() {
			return accountNo;
		}

		/**
		 * Sets the account no.
		 *
		 * @param accountNo            the accountNo to set
		 */
		public void setAccountNo(String accountNo) {
			this.accountNo = accountNo;
		}

		/**
		 * Gets the bank name.
		 *
		 * @return the bankName
		 */
		public String getBankName() {
			return bankName;
		}

		/**
		 * Sets the bank name.
		 *
		 * @param bankName            the bankName to set
		 */
		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		/**
		 * Gets the bank branch.
		 *
		 * @return the bankBranch
		 */
		public String getBankBranch() {
			return bankBranch;
		}

		/**
		 * Sets the bank branch.
		 *
		 * @param bankBranch            the bankBranch to set
		 */
		public void setBankBranch(String bankBranch) {
			this.bankBranch = bankBranch;
		}

		/**
		 * Gets the pk.
		 *
		 * @param fk the fk
		 * @return the pk
		 */
		private String getPK(SupplierBankFK fk) {
			return new StringBuilder(
					fk.getAccountNo() != null ? fk.getAccountNo() : "")
					.append("_")
					.append(fk.getBankName() != null ? fk.getBankName() : "")
					.append("_")
					.append(fk.getBankBranch() != null ? fk.getBankBranch()
							: "").toString();
		}

	}

	/**
	 * save the LOA details.
	 * save - save data into database and initiate task for LOA details.
	 * page navigate to loa details page.
	 */
	public void saveAction() {
		debugLogger("saveAction");

		try {
			String outcome = BpmOutcomeConst.BPM_SAVE;
			ScLoa scLoa = this.getManagedBean().getScLoa();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();
			
			parseLOADetails(scLoa, outcome);
			validateLOADetails(scLoa, outcome);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setScLoa(scLoa);
			reqDTO.setOutcome(outcome);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				if (QTStatusConst.LOA_PENDING_SUBMISSION
						.equals(statusId)) {
					// rename temporary Loa No folder name in docRepo
					RenameTempLoaNoRepoDirByLoaNo(scLoa);
				}

				if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					editSupplierLOAAction(scLoa.getLoaId());
				} else {
					editLOAAction(scLoa);
				}

				successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
						scLoa.getLoaNo(), Boolean.FALSE);
			}

		} finally {
			//qtAwardSO = null;
		}
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
			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScLoa scLoa = this.getManagedBean().getScLoa();

			parseLOADetails(scLoa, outcome);
			validateLOADetails(scLoa, outcome);
			
			scLoa.setScLoaWorkFlowStatuses(null);
			if(scLoa.getScQtSchedules() != null && scLoa.getScQtSchedules().size()>0){
				for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
					freqScheduleValidation(scQtSchedule);
				}
			}

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outcome);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				RenameTempLoaNoRepoDirByLoaNo(scLoa);
				renewSesionObject();
				successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
						scLoa.getLoaNo(), Boolean.TRUE);
			}

		} finally {
			//qtAwardSO = null;
		}

		return null;
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
		AwardSupplierResDTO resDTO = null;
		try {

			ScLoa scLoa = this.getManagedBean().getScLoa();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();

			parseLOADetails(scLoa, outcome);

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
					RequestContext.getCurrentInstance().execute("spki_dlg.hide();");
				}

				// Insert reports when supplier accept Loa
				parseReports(scLoa);
			} else{
				// gpki for approver
				if (null != digiSign) {
					updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
							scLoa.getLoaId(), scLoa.getLoaNo());
				}
			}

			validateLOADetails(scLoa, outcome);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setDigiSignData(digiSign);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setScQt(this.getManagedBean().getScQt());
		    resDTO = getQtAwardSO().saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				String messageCode = null;
				if (QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId)) {
					if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700007;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					} else if (BpmOutcomeConst.BPM_REVISE
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700009;
					}
				} else if (QTStatusConst.LOA_PENDING_REVISION.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOA_PENDING_CONTRACT_SIGNING
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700011;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					}
				} else if (QTStatusConst.LOA_PENDING_VERIFICATION
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700001;
				}
				successMessage(messageCode, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_AWARD,
						scLoa.getLoaNo(), Boolean.TRUE);
				
				renewSesionObject();
			}

		} finally {
			//qtAwardSO = null;
		}
		return resDTO;
	}
	
	/**
	 * Parses the reports.
	 * Insert reports when supplier accept Loa
	 * @param scLoa the sc loa
	 */
	private void parseReports(ScLoa scLoa){
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();

		for (ScLoaReport scLoaReport : scLoaReports) {
			ScLoaReportAttachment scLoaReportAttachment = scLoaReport
					.getScLoaReportAttachment();
			scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
			if (scLoaReportAttachment != null
					&& StringUtils.isNotBlank(scLoaReportAttachment.getFileName())) {
				scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			}
			scAttachments.add(scLoaReportAttachment);
		}
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
		} else if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
			//gpki for only govt seller
			if(isDigiSigningRequired(RoleCode.GOVT_SELLER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

			}  else {
				// spki
				RequestContext.getCurrentInstance().execute("spki_dlg.show();");
			}

		} else {
			updateBPMTask(outcome, null);
		}	
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

			if (!getManagedBean().isAgreeWithTermsConditions()) {
				throw new EpException(MessageConst.ERROR_750002);
			}
		}

		if(QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId) || QTStatusConst.LOA_PENDING_REVISION.equals(statusId)
				&& BpmOutcomeConst.BPM_SUBMIT.equals(outcome)) {
			if(scLoa.isApprovalReq()){
				if(null == getManagedBean().getApproverUser() || null == getManagedBean().getApproverUser().getUserId()){
					throw new EpException(
							MessageConst.ERROR_760315,
							new String[] {ResourceBundleUtils.getMessage("lbl.sc.approver",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}

			if(null  == scLoa.getContractEffDate()){
				throw new EpException(
						MessageConst.ERROR_760349);
			}

			if(null  == scLoa.getContractExpDate()){
				throw new EpException(
						MessageConst.ERROR_760350);
			}
		}
       // validation for fulfillment schedule and progressivepayment
		if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome) || BpmOutcomeConst.BPM_SAVE.equals(outcome)) {
			// check whether loa from s1 or s2.
			if(scLoa.getScLoiLoa() != null){
				if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
					// For s2
					if (getManagedBean().getScQt() != null) {
						if (("P").equals(getManagedBean().getScQt().getScheduleType())) {
							if(scLoa.getScQtProgPayments() != null && scLoa.getScQtProgPayments().size()>0){
								for (ScQtProgressivePayment progressivePayment : scLoa.getScQtProgPayments()) {
									progressivePayValidation(progressivePayment);
								}
							}

						} else if (("F").equals(getManagedBean().getScQt()
								.getScheduleType())) {
							if(scLoa.getScQtSchedules() != null && scLoa.getScQtSchedules().size()>0){
								for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
									freqScheduleValidation(scQtSchedule);
								}
							}
						}
					}
				} else{
					// for s1
					if(scLoa.getScQtSchedules() != null && scLoa.getScQtSchedules().size()>0){
						for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
							freqScheduleValidation(scQtSchedule);
						}
					}
				}
			}
		}
		// validate deduction payamment for Others
		validateDeductionPaymentDtls(scLoa);
		
	}

	/**
	 * Parses the loa details.
	 *
	 * @param scLoa the sc loa
	 * @param outcome the outcome
	 */
	private void parseLOADetails(ScLoa scLoa, String outcome) {
		if (scLoa.isBrandNew()) {
			List<ScLoaWorkFlowStatus> workFlowStatuses = new ArrayList<ScLoaWorkFlowStatus>();
			ScLoaWorkFlowStatus workFlowStatus = new ScLoaWorkFlowStatus();
			workFlowStatus.setScLoa(scLoa);
			workFlowStatus.setPmStatus(scLoa.getPmStatus());
			workFlowStatus.setCurrent(true);
			workFlowStatuses.add(workFlowStatus);
			scLoa.setScLoaWorkFlowStatuses(workFlowStatuses);
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

			scLoaApproval.setApproverAction(action);
			scLoaApproval.setApproverActionDate(new Date());
			scLoa.setScLoaApproval(scLoaApproval);
		} else if (!scLoa.isApprovalReq()) {
			scLoa.setScLoaApproval(null);
		}

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			scLoa.setAckDate(new Date());
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
			scLoa.setBondResDueDate(new Date());
		}  else{
			if(!(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome))){
				scLoa.setScLoaRemarks(finalizeRemarks(scLoa.getScLoaRemarks()));
			}
		}
		
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
				saveScheduleList();
			}
		}

		PmUser contractSignerUser = getManagedBean().getContractSignerUser();
		if (contractSignerUser != null
				&& contractSignerUser.getUserId() != null) {
			scLoa.setContractSigner(contractSignerUser);
		}
	}

	/**
	 * Contract type changed.
	 * shows Agency based on contract type changes.
	 */
	public void contractTypeChanged() {
		debugLogger("contractTypeChanged");

		ScLoa scLoa = getManagedBean().getScLoa();

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(scLoa.getContractType().getParameterId());

			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scLoa.setContractType(resDTO.getPmParameter());

		} finally {
			parameterSO = null;
		}

		String contractType = scLoa.getContractType().getParameterCode();

		if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
			// ministry condtion field for LOV 
			getMinistryConditionField(scLoa);
			if(null == scLoa.getContractAgency()){
				scLoa.setContractAgency(scLoa.getScLoiLoa().getPmOrgProfile());
			}

		} else if (ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType)) {

			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			
			if (userId != null) {
				IUserSO userSO = null;
				try {
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setUserId(userId);
					userSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

					if (resDTO.getUserOrgList() != null
							&& resDTO.getUserOrgList().size() > 0) {
						for (PmUserOrg pmUserOrg : resDTO.getUserOrgList()) {
							if (pmUserOrg.getPmOrgProfile() != null) {
								Boolean isBPK = pmUserOrg.getPmOrgProfile().getBpk();
								if (BooleanUtils.isTrue(isBPK)) {
									scLoa.getScLoiLoa().setPmOrgProfile(
											pmUserOrg.getPmOrgProfile());
									scLoa.setContractAgency(scLoa
											.getScLoiLoa().getPmOrgProfile());
									break;
								}
							}
						}
					}
				} finally {
					userSO = null;
				}
			}			
		}

		contractAgencyChanged();
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
					// TODO - filter user based on central contract.
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
	 * Adds the schedule details in Fulfillment Schedule.
	 */
	public void addScheduleDetails() {
		try {
			Integer totalFrequency = getManagedBean().getTotalFrequency();
			int year = Calendar.getInstance().get(Calendar.YEAR);
			List<ScQtSchedule> freqSchedules = this.getManagedBean().getScLoa()
					.getScQtSchedules();
			
			if (null == freqSchedules) {
				freqSchedules = new ArrayList<ScQtSchedule>();
			}
			
			if(null == totalFrequency){
				totalFrequency = new Integer(0);
			}

			if (freqSchedules.size() > totalFrequency) {
				List<ScQtSchedule> localSchedules = new ArrayList<ScQtSchedule>();
				for (int j = 0; j < totalFrequency; j++) {
					localSchedules.add(freqSchedules.get(j));
				}
				this.getManagedBean().getScLoa().setScQtSchedules(localSchedules);
			} else {
				for (int i = freqSchedules.size(); i < totalFrequency; i++) {
					ScQtSchedule schedule = new ScQtSchedule();
					schedule.setStartYear(String.valueOf(year));
					schedule.setEndYear(String.valueOf(year));
					schedule.setScheduleNo(BigDecimal.valueOf(i + 1));
					PmParameter parameter = this.getParameterByTypeAndCode(ParameterTypeConst.SCHEDULE_TYPE, ParameterConst.SCHEDULE_TYPE_PERIODIC);
					schedule.setScheduleType(parameter);					
					setUserDetails(schedule);
					freqSchedules.add(schedule);
				}
				this.getManagedBean().getScLoa().setScQtSchedules(freqSchedules);
			}
		} finally {
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
				Date currentDate = new Date();
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
				String localDate = df.format(currentDate);
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
					e.printStackTrace();
					throw new EpException(e.toString());
				}
				if (schedule.getScLoa() == null) {
					schedule.setScLoa(this.getManagedBean().getScLoa());
				}
				localScheduleList.add(schedule);
			}
			getManagedBean().getScLoa().setScQtSchedules(localScheduleList);
		} else {
			getManagedBean().getScLoa().setScQtSchedules(null);
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
	 * Generate frequency schedule pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */
	private void generateFrequencySchedulePdfFromVelocity(
			ScAttachment attachment, ScLoa scLoa, ScQt scQt) throws Exception {

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
		
		List<ScQtSchedule> scheduleList = scLoa.getScQtSchedules();
		List<ScQtSchedule> scheduleReportList = new ArrayList<ScQtSchedule>();
		ScQtSchedule reportSchedule = null;
		int totalFrequency = 0;
		if (scheduleList != null && scheduleList.size() > 0) {
			totalFrequency = scheduleList.size();
			for (ScQtSchedule schedule : scheduleList) {
				try {
					reportSchedule = new ScQtSchedule();
					parameterSO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IParameterSO.class);
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterId(schedule.getScheduleType()
							.getParameterId());
					ParameterResDTO resDTO = parameterSO
							.retrieveParameterById(reqDTO);
					reportSchedule.setScheduleType(resDTO.getPmParameter());
					reportSchedule.setStartMonth(monthFormat.format(schedule
							.getFromDate()));
					reportSchedule.setStartYear(yearFormat.format(schedule
							.getFromDate()));
					reportSchedule.setEndMonth(monthFormat.format(schedule
							.getToDate()));
					reportSchedule.setEndYear(yearFormat.format(schedule
							.getToDate()));
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

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_FREQUENCY_SCHEDULE, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Form jadual harga.
	 * To download jadual-harga.xlsx file
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */
	public void formJadualHarga(ScAttachment attachment, ScLoa scLoa)
			throws Exception {
		populateAttachmentDetails(attachment);
		List<JadualHargaBean> productJadualHargaList = new ArrayList<>();
		List<JadualHargaBean> serviceJadualHargaList = new ArrayList<>();
		JadualHargaBean jadualHarga = null;
		String itemType = null;
		for (ScLoiLoaItem item : scLoa.getScLoiLoaItems()) {
			jadualHarga = mappingJadualHarga(item);
			itemType = item.getScRequestItem().getItemType().getParameterCode();
			if (ParameterConst.ITEM_TYPE_PRODUCT.equals(itemType)) {
				productJadualHargaList.add(jadualHarga);
			} else if (itemType.equals(ParameterConst.ITEM_TYPE_SERVICE)) {
				serviceJadualHargaList.add(jadualHarga);
			}
		}
		
		// check whether loa from s1 or s2.
		boolean isS1OrS2 = Boolean.TRUE;
		if(scLoa.getScLoiLoa() != null){
			if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
				isS1OrS2 = Boolean.FALSE;
			}
		}

		generateJadualHarga(attachment, productJadualHargaList,
				serviceJadualHargaList , isS1OrS2);

	}

	/**
	 * Mapping jadual harga.
	 *
	 * @param item the item
	 * @return the jadual harga bean
	 */
	private JadualHargaBean mappingJadualHarga(ScLoiLoaItem item) {
		JadualHargaBean jadualHarga = new JadualHargaBean();
		
		jadualHarga.setZone(item.getTplSpecZoneDtl() != null ? item
				.getTplSpecZoneDtl().getZoneName() : "");
		jadualHarga.setItem(item.getScRequestItem().getItemName());
		
		BigDecimal unitPrice = item.getUnitPrice().setScale(2, BigDecimal.ROUND_HALF_EVEN);
		jadualHarga.setUnitPrice(unitPrice.toString());
		jadualHarga.setUom(item.getScRequestItem().getTplSpecQuestion()
				.getClUom().getUomName());
		jadualHarga.setFreqUom(null == item.getScRequestItem()
				.getTplSpecQuestion().getClUomFrequency() ? "" : item
				.getScRequestItem().getTplSpecQuestion().getClUomFrequency()
				.toString());
		jadualHarga.setUomPerDay(null == item.getScRequestItem()
				.getTplSpecQuestion().getClUomPerDay() ? "" : item
				.getScRequestItem().getTplSpecQuestion().getClUomPerDay()
				.toString());
		jadualHarga.setUomPerMonth(null == item.getScRequestItem()
				.getTplSpecQuestion().getClUomPerMonth() ? "" : item
				.getScRequestItem().getTplSpecQuestion().getClUomPerMonth()
				.toString());
		jadualHarga.setUomPerYear(null == item.getScRequestItem()
				.getTplSpecQuestion().getClDurationMonth() ? "" : item
				.getScRequestItem().getTplSpecQuestion().getClDurationMonth()
				.toString());
		jadualHarga.setQuantity(null == item.getQty()? "" : item.getQty()
				.toString());

		BigDecimal  totalAmount = BigDecimal.valueOf(0d);
		BigDecimal subTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(item.getScRequestItem().getTplSpecQuestion(), 
				item.getUnitPrice(), item.getQty());
		totalAmount = totalAmount.add(subTotal);
		totalAmount =  totalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		jadualHarga.setTotalAmount(totalAmount.toString());
		return jadualHarga;

	}

	/**
	 * Generate jadual harga.
	 *
	 * @param attachment the attachment
	 * @param productList the product list
	 * @param serviceList the service list
	 * @throws Exception the exception
	 */
	private void generateJadualHarga(ScAttachment attachment,
			List<JadualHargaBean> productList, List<JadualHargaBean> serviceList, boolean isS1OrS2)
					throws Exception {
		if (serviceList.size() > 0 || productList.size() > 0) {
			try {
				/*FileInputStream inputStream = new FileInputStream(new File(
						FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/WEB-INF/templates")
						+ "/sc/loa/jadual-harga.xlsx"));*/

				final String templatePath = "templates" + SourcingConstants.DOCPATH_SC_LOA_JADUAL_HARGA;
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(templatePath);
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
						for (JadualHargaBean product : productList) {
							rowIndex++;
							cellIndex = 0;
							row = productSheet.createRow(rowIndex);
							row.createCell(cellIndex++).setCellValue(
									product.getZone());
							row.createCell(cellIndex++).setCellValue(
									product.getItem());
							row.createCell(cellIndex++).setCellValue(
									product.getUom());
							row.createCell(cellIndex++).setCellValue(
									product.getQuantity());
							row.createCell(cellIndex++).setCellValue(
									product.getSubSequence());
							row.createCell(cellIndex++).setCellValue(
									product.getUnitPrice());
							row.createCell(cellIndex++).setCellValue(
									product.getTotalAmount());
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
						
						if(isS1OrS2){
							Row row0 = serviceSheet.getRow(0);
							Cell cell9 = row0.getCell(9);
							cell9.setCellValue("Jumlah Harga (RM)");
						}

						for (JadualHargaBean service : serviceList) {
							rowIndex++;
							cellIndex = 0;
							row = serviceSheet.createRow(rowIndex);
							row.createCell(cellIndex++).setCellValue(
									service.getZone());
							row.createCell(cellIndex++).setCellValue(
									service.getItem());
							row.createCell(cellIndex++).setCellValue(
									service.getUom());
							row.createCell(cellIndex++).setCellValue(
									service.getFreqUom());
							row.createCell(cellIndex++).setCellValue(
									service.getUomPerDay());
							row.createCell(cellIndex++).setCellValue(
									service.getUomPerMonth());
							row.createCell(cellIndex++).setCellValue(
									service.getUomPerYear());
							row.createCell(cellIndex++).setCellValue(
									service.getQuantity());
							row.createCell(cellIndex++).setCellValue(
									service.getUnitPrice());
							row.createCell(cellIndex++).setCellValue(
									service.getTotalAmount());

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
			try {
				/*FileInputStream inputStream = new FileInputStream(new File(
						FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/WEB-INF/templates")
						+ "/sc/loa/specification.xlsx"));*/

				final String templatePath = "templates" + SourcingConstants.DOCPATH_SC_LOA_SPECIFICATION;
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(templatePath);
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
		}
	}
	

	/**
	 * Creates the QT Loa.
	 * page navigate to qt loi details page.
	 * @param scLoiLoa the sc loi loa
	 * @return the string
	 */
	public String createQtLOAAction(ScLoiLoa scLoiLoa) {
		IStatusSO statusSO = null;
		ScLoa scLoa = new ScLoa();
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			scLoa.setScLoiLoa(scLoiLoa);
			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(QTStatusConst.LOA_PENDING_SUBMISSION);
			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			scLoa.setPmStatus(statusResDTO.getPmStatus());
			//scLoa.setLoaNo(getQtAwardSO().generateLoaNo());
		
		} finally {
			statusSO = null;
		}

		FacesContext fc = FacesContext.getCurrentInstance();
        getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
		
		return editLOAAction(scLoa);

	}
	
	/**
	 * Go qt edit loa action.
	 * page navigate to loa details page.
	 * @param suppLoiLoaBean the supp loi loa bean
	 * @return the string
	 */
	public String goQtEditLoaAction(SuppLoiLoaBean suppLoiLoaBean) {
		try {

			String language = new FacesScopes().getUserLocale().getLanguage();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			reqDTO.setLanguageCode(language);

			if (suppLoiLoaBean.getScLoa() != null) {
				reqDTO.setScLoa(suppLoiLoaBean.getScLoa());
				resDTO = getQtAwardSO().retrieveLOADtl(reqDTO);
			} else if (suppLoiLoaBean.getScLoi() != null) {
				reqDTO.setScLoi(suppLoiLoaBean.getScLoi());
				resDTO = getQtAwardSO().retrieveLOIDtl(reqDTO);
			}

			if (resDTO != null) {
				if (resDTO.getScLoa() != null) {
					return editLOAAction(resDTO.getScLoa());
				} else if (resDTO.getScLoi() != null) {
					return null;
				}
			}

		} finally {
			//qtAwardSO = null;
		}
		return "";
	}

	/**
	 * Adds the progressive details.
	 */
	public void addProgressiveDetails() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		Integer totalProgressivePayment = getManagedBean().getTotalProgressivePayment();
		ScLoa scLoa = getManagedBean().getScLoa();
		List<ScQtProgressivePayment> progressivePayList = this.getManagedBean()
				.getProgressivePaymentList();

		if (null == progressivePayList) {
			progressivePayList = new ArrayList<ScQtProgressivePayment>();
		}

		if(null == totalProgressivePayment){
			totalProgressivePayment = new Integer(0);
		}
		if (progressivePayList.size() > totalProgressivePayment) {
			List<ScQtProgressivePayment> progressiveList = new ArrayList<ScQtProgressivePayment>();
			for (int j = 0; j < totalProgressivePayment ; j++) {
				progressiveList.add(progressivePayList.get(j));
			}

			this.getManagedBean().setProgressivePaymentList(progressiveList);
		} else {
			for (int i = progressivePayList.size(); i < totalProgressivePayment ; i++) {
				ScQtProgressivePayment payment = new ScQtProgressivePayment();
				payment.setStartYear(String.valueOf(year));
				payment.setEndYear(String.valueOf(year));
				payment.setPhaseNo(BigDecimal.valueOf(i + 1));
				payment.setScLoa(scLoa);
				setUserDetails(payment);
				progressivePayList.add(payment);
			}
			this.getManagedBean().setProgressivePaymentList(progressivePayList);
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
	 * Gets the approval action display name.
	 *
	 * @param value the value
	 * @return the approval action display name
	 */
	public String getApprovalActionDisplayName(String value) {
		return ApprovalAction.getDisplayName(value);
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
	 * Calc total amount.
	 *
	 * @param scLoiLoaItems the sc loi loa items
	 * @return the big decimal
	 */
	private BigDecimal calcTotalAmount(List<ScLoiLoaItem> scLoiLoaItems) {
		boolean isCalculateMaxPriceItem = Boolean.FALSE;// calculate max price items only.
		
		// filtered zone tier or tier max price items to be calculated only.
		List<ScLoiLoaItem> filteredMaxPriceItems  = filterZoneTierOrTierPriceItems(null);
		BigDecimal  totalAmount = BigDecimal.valueOf(0d);
		BigDecimal  subTotal = BigDecimal.valueOf(0d);
		BigDecimal quantity = BigDecimal.valueOf(0d);
		for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
			quantity = scLoiLoaItem.getQty();
			if(null == scLoiLoaItem.getTplSpecTierQty() ){
				// without Tier quantity
				
				isCalculateMaxPriceItem = Boolean.TRUE;
			} else{
				if(filteredMaxPriceItems.contains(scLoiLoaItem)){
					isCalculateMaxPriceItem = Boolean.TRUE;
				} else{
					isCalculateMaxPriceItem = Boolean.FALSE;
				}
			}
			if(isCalculateMaxPriceItem){
				if(quantity != null){
					subTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), 
							scLoiLoaItem.getUnitPrice(), quantity);
					totalAmount = totalAmount.add(subTotal);
				}
			}
		}
		totalAmount =  totalAmount.setScale(4, BigDecimal.ROUND_HALF_EVEN);
		return totalAmount;
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
		
		// back to qt loa list
		if(getManagedBean().getScQt() != null){
			retrieveLOILOAList(getManagedBean().getSourceId());	
		} else{
			// back to dp loa list
			if(getManagedBean().getSourceId() != null){
				DpLOABackingBean dpLOABackingBean = new DpLOABackingBean();
				dpLOABackingBean.retrieveLOAList(getManagedBean().getSourceId());
			}
		}
		
		getManagedBean().setEditFlag(Boolean.FALSE);
		getManagedBean().setSelectedTabIndex(0);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getBackToPreviousPage());
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
		boolean isPerformanceBondMandatory = false;
		ScLoa loa = getManagedBean().getScLoa();
		BigDecimal overallContractAmtOrLoaAmt = overallCtAmtOrLoaAmt(loa);
		
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		String ruleType = RuleTypeConst.BOND;
		ruleReqDTO.setRuleType(ruleType);
		List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByRuleType(ruleReqDTO).getRuleValueList();

		if (ruleValueList != null && ruleValueList.size() > 0) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			if(overallContractAmtOrLoaAmt.compareTo(new BigDecimal(configRuleValue.getRuleValue())) > 0 && !isbondExceptionCategory(loa)) {
				isPerformanceBondMandatory = true;
				loa.setBondReq(true);
			}
			else if(overallContractAmtOrLoaAmt.compareTo(new BigDecimal(configRuleValue.getRuleValue())) <= 0) {
				isPerformanceBondMandatory = true;
				loa.setBondReq(false);
			}
		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleType)});
		}
		getManagedBean().setPerformanceBondMandatory(isPerformanceBondMandatory);
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
		} else if (isOneOffFulfilment && !isQuotationThresHold(overallContractAmtOrLoaAmt)){
			isAgreementMandatory = true;
		}
		
		getManagedBean().setAgreementMandatory(isAgreementMandatory);
		if(isAgreementMandatory) {
			loa.setAgreementReq(Boolean.TRUE);
		} else{
			loa.setAgreementReq(Boolean.FALSE);
		} 
	}

	private BigDecimal overallCtAmtOrLoaAmt(ScLoa loa){
		BigDecimal overallContractAmtOrLoaAmt = BigDecimal.valueOf(0d);
		// if supplier Gst Registered  
		if(isGSTRegActive()){
			overallContractAmtOrLoaAmt = loa.getOverallContractAmt(); 

		} else{
			overallContractAmtOrLoaAmt = loa.getLoaAmt();
		}
		return overallContractAmtOrLoaAmt;
	}

	private boolean isbondExceptionCategory(ScLoa loa) {
		ScQt scQt = null;
		if(ParameterConst.DOCTYPE_QUOTATION.equals(loa.getScLoiLoa().getDocType())) {
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(loa.getScLoiLoa().getDocId());
			scQt = getQtSuppFinalizationSO().retrieveQTDtls(reqDTO).getScQt();
			List<String> excepCategoryCodes = getBondExceptionCategory();
			if(!CollectionUtils.isEmpty(scQt.getScQtCategorys())) {
				for(ScQtCategory category : scQt.getScQtCategorys()) {
					String categoryCode = null;
					categoryCode = category.getPmCategoryL1().getCategoryL1Code().
							concat(category.getPmCategoryL2().getCategoryL2Code()).
							concat(category.getPmCategoryL3().getCategoryL3Code());
					if(excepCategoryCodes.contains(categoryCode)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private List<String> getBondExceptionCategory() {
		// Get performance bond exception category
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(ParameterTypeConst.RULE_BOND_CATEGORY);
		List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByRuleType(ruleReqDTO).getRuleValueList();
		List<String> categoryCodes = new ArrayList<String>();
		for(PmConfigRuleValue ruleValue : ruleValueList) {
			StringBuilder sb = new StringBuilder();
			PmCategoryL3 l3 = ruleValue.getCategoryL3Id();
			PmCategoryL2 l2 = l3.getPmCategoryL2();
			PmCategoryL1 l1 = l2.getPmCategoryL1();
			String l3Code = l3.getCategoryL3Code();
			String l2Code = l2.getCategoryL2Code();
			String l1Code = l1.getCategoryL1Code();
			sb.append(l1Code).append(l2Code).append(l3Code);
			categoryCodes.add(sb.toString());
		}
		return categoryCodes;
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
	 * Gets the qtDetail so.
	 *
	 * @return the qtDetail so
	 */
	private IQTSuppFinalizationSO getQtSuppFinalizationSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
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
	
	public void backToPrevious(int tabIndex) {
		log.info("backToPrevious");
		getManagedBean().setSelectedTabIndex(tabIndex);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.LOA_PAGE);

	}

	/**
	 * Retrieve org profile by org type.
	 *
	 * @param ortType the ort type
	 * @return the pm org profile
	 */
	private PmOrgProfile retrieveOrgProfileByOrgType(String ortType){
		PmOrgProfile pmOrgProfile = null;
		OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
		profileReqDTO.setOrgType(ortType);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileResDTO orgProfileResDTO = orgProfileSO
				.retrieveOrgProfileListByOrgType(profileReqDTO);
		if(orgProfileResDTO.getOrgProfileList() != null && orgProfileResDTO.getOrgProfileList().size()>0){
			pmOrgProfile =  orgProfileResDTO.getOrgProfileList().get(0);
		}
		return pmOrgProfile;
	}
	
	/**
	 * Retrieve org profile by org profile id.
	 *
	 * @param ortType the ort type
	 * @return the pm org profile
	 */
	private PmOrgProfile retrieveOrgProfileByOrgProfileId(Long orgProfileId){
		PmOrgProfile pmOrgProfile = null;
		OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
		profileReqDTO.setOrgProfileId(orgProfileId);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileResDTO orgProfileResDTO = orgProfileSO
				.retrieveOrgProfileDtlByProfileId(profileReqDTO);
		if(orgProfileResDTO.getPmOrgProfile() != null){
			pmOrgProfile =  orgProfileResDTO.getPmOrgProfile();
		}
		return pmOrgProfile;
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
	 * Validate deduction payment dtls.
	 *
	 * @param scLoa the sc loa
	 */
	private void validateDeductionPaymentDtls(ScLoa scLoa) {
		List<ScQtDeduction> deductions = scLoa.getScQtDeductions();
		if (CollectionUtils.isNotEmpty(deductions)) {
			for (ScQtDeduction deduction : deductions) {
				if (ParameterConst.DEDUCTION_TYPE_OTHERS.equals(deduction.getDeductionType().getParameterCode())) {
					if (CollectionUtils.isEmpty(deduction.getScQtDeductionClauses())) {
						throw new EpException(MessageConst.ERROR_760324);
					}
				}
			}
		}
	}

	/**
	 * Progressive pay validation.
	 *
	 * @param progressivePayment the progressive payment
	 */
	private void progressivePayValidation(ScQtProgressivePayment progressivePayment) {
		debugLogger("frequencyScheduleValidation");
		FacesScopes facesScopes = new FacesScopes();
		Calendar startDate = null;
		Calendar endDate = null;
		
		if(progressivePayment.getStartYear() == null || progressivePayment.getStartYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.year",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(progressivePayment.getStartMonth() == null || progressivePayment.getStartMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(progressivePayment.getEndYear() == null || progressivePayment.getEndYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.year",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(progressivePayment.getEndMonth() == null || progressivePayment.getEndMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		
		ScLoa loa = 	getManagedBean().getScLoa();
		Calendar expDate = Calendar.getInstance();
		expDate.setTime(loa.getContractExpDate());
		expDate.add(Calendar.MONTH, 1);
		expDate.set(Calendar.DAY_OF_MONTH, 1);
		expDate.add(Calendar.DATE, -1);
		Date contractExpDate = expDate.getTime();
		
		Calendar effDate =Calendar.getInstance();
		effDate.setTime(loa.getContractEffDate());
		effDate.set(Calendar.DAY_OF_MONTH, 1);
		Date contractEffDate = effDate.getTime();  
	   
	    if(progressivePayment.getStartYear() != null && progressivePayment.getStartMonth() != null){
	    	startDate = Calendar.getInstance();
	    	startDate.clear();
	    	startDate.set(Integer.valueOf(progressivePayment.getStartYear()), Integer.valueOf(progressivePayment.getStartMonth())-1, 1, 0, 0, 0);
	    }
	   
	    if(progressivePayment.getEndYear() != null && progressivePayment.getEndMonth() != null){
	    	endDate = Calendar.getInstance();
	    	endDate.clear();
	    	endDate.set(Integer.valueOf(progressivePayment.getEndYear()), Integer.valueOf(progressivePayment.getEndMonth())-1, Integer.valueOf(getDays(contractExpDate)), 0, 0, 0);
	    }
	    
	    if(startDate != null && endDate != null){
	    	if (startDate.after(endDate)) {
	    		throw new EpException(MessageConst.ERROR_760345);
	    	}

	    	if (endDate.before(startDate)) {
	    		throw new EpException(MessageConst.ERROR_760346);
	    	}

	    	if (!(startDate.getTime().after(contractEffDate)
					&& (startDate.getTime().before(contractExpDate)) || contractEffDate.equals(startDate.getTime()))) {
				throw new EpException(MessageConst.ERROR_760347);
			}

			if (!(endDate.getTime().after(contractEffDate)
					&& (endDate.getTime().before(contractExpDate)) || contractExpDate.equals(endDate.getTime()))) {
				throw new EpException(MessageConst.ERROR_760348);
			}
	    }
	}

	/**
	 * Gets the supplier branches.
	 *
	 * @return the supplier branches
	 */
	public SelectItem[]  getSupplierBranches(){
		SelectItem[] items = null;
        boolean isBranch = Boolean.FALSE;
		if(getManagedBean().getBranchsList() != null && getManagedBean().getBranchsList().size()>0){
			items = new SelectItem[getManagedBean().getBranchsList().size()+1];
			isBranch = Boolean.TRUE;
		} else{
			items = new SelectItem[1];
		}

		int i = 1;
		items[0] = new SelectItem("HQ", "HQ");
		if(isBranch){
			for(SmSupplierBranch smSupplierBranch : getManagedBean().getBranchsList()) {
				items[i] = new SelectItem(smSupplierBranch.getBranchName().replaceAll("\\s+", " ").trim(), smSupplierBranch.getBranchName().trim().replaceAll("\\s+", " ").trim());
				i++;
			}	
		}

		return items;
	}
	
	/**
	 * Sum of gstAmount & loa offer price
	 * 
	 * @param gstAmount
	 */
	public void calculateOverallAmount(BigDecimal gstAmount) {
		
		getManagedBean().setOverallContractAmt(
				overallCtAmount(getManagedBean().getScLoa().getLoaAmt(), gstAmount));
		if (null != getManagedBean().getContractSignerUser()
				&& getManagedBean().getContractSignerUser().getUserId() != null) {

			getManagedBean().getScLoa().setLoaSignerDesig(null);
			getManagedBean().setContractSignerUser(null);
			getManagedBean().getScLoa().setContractSigner(null);
			prepareSignerInfo();
			RequestContext.getCurrentInstance().execute(
					"gstSignerConfirmPopupWin.show()");
		} else {
			prepareSignerInfo();
		}
		getManagedBean().getScLoa().setOverallContractAmt(getManagedBean().getOverallContractAmt());
		checkPerformanceBondMandatory();
		checkAgreementMandatory();
	}
	
	public boolean isGSTRegActive() {
		boolean isActive = Boolean.FALSE;
		ScLoa scLoa = getManagedBean().getScLoa();
		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
		getManagedBean().setRenderRemainder(false);
		if ((supplier != null && supplier.getGstRegisteredFlag() == 
												DbValueConst.RECORD_STATUS_ACTIVE)) {
			if (null != scLoa.getGstEffDate()){
				if(scLoa.getGstEffDate().compareTo(new Date()) <= 0){
					isActive = Boolean.TRUE;
				}else {
					getManagedBean().setRenderRemainder(isActive);
				}
			}
		}
		return isActive;
	}
	
	private void calculateOverallCtAmtForSupplier(){
		
		ScLoa scLoa = getManagedBean().getScLoa();
		SmSupplier supplier = getManagedBean().getScLoa().getScLoiLoa()
				.getSmSupplier();
		BigDecimal overallContractAmt = getManagedBean().getScLoa().getLoaAmt();
		if ((supplier != null && supplier.getGstRegisteredFlag() == DbValueConst.RECORD_STATUS_ACTIVE)
				&& null != getManagedBean().getScLoa().getGstAmt()) {
			overallContractAmt.add(getManagedBean().getScLoa().getGstAmt());

		} else if ((supplier != null && supplier.getGstRegisteredFlag() != DbValueConst.RECORD_STATUS_ACTIVE)) {

			BigDecimal durationMnth = null;
			BigDecimal noOfMnth = new BigDecimal(12);
			for (ScLoiLoaItem loiLoaItem : scLoa.getScLoiLoaItems()) {
				TplSpecQuestion specQuestion = loiLoaItem.getScRequestItem()
						.getTplSpecQuestion();
				if (specQuestion != null) {
					TplSpecMaster specMaster = specQuestion.getTplSpecMaster();
					if (specMaster.getItemTypeCode().getParameterCode()
							.compareTo(ParameterConst.ITEM_TYPE_SERVICE) == 0) {
						durationMnth = specQuestion.getTplSpecMaster()
								.getDurationMonth();
						break;
					}

				}
			}
			if (durationMnth != null) {
				BigDecimal awardedPrice = (overallContractAmt.divide(
						durationMnth, 2, BigDecimal.ROUND_HALF_UP)).multiply(noOfMnth);
				awardedPrice = awardedPrice.setScale(2,BigDecimal.ROUND_HALF_EVEN);
				if (awardedPrice.intValue() > 500000) {
					getManagedBean().setDisplayAlert(true);
				}
			}
		}
		overallContractAmt = overallContractAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		getManagedBean().setOverallContractAmt(overallContractAmt);
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
	 * @param event
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
				scLoaReport.setPmDocument(getDocumentDtls(docCategory)); // PM document
				
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
	
	private void setGstInfoForAgreementLetter(VelocityContext context, ScLoa scLoa,
			DecimalFormat amountDF, Locale myLocale) {

		String gstRegNo = scLoa.getGstRegNo() != null ? scLoa.getGstRegNo(): "";
		context.put("gstRegNo", gstRegNo);

		String gstEffectiveDate = scLoa.getGstEffDate() != null ? DateUtils
				.getLocalizeDate(scLoa.getGstEffDate(), myLocale) : "";
		context.put("gstEffectiveDate", gstEffectiveDate);

		String gstDeclaredDate = scLoa.getGstDeclaredDate() != null ? gstEffectiveDate = DateUtils
				.getLocalizeDate(scLoa.getGstDeclaredDate(), myLocale) : "";
		context.put("gstDeclaredDate", gstDeclaredDate);

		String offerPrice = "";
		String offerPriceWording = "";
		BigDecimal loaAmount = scLoa.getLoaAmt();
		if (loaAmount != null) {
			offerPrice = amountDF.format(loaAmount);
			offerPriceWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
		}
		context.put("offerPrice", offerPrice);
		context.put("offerPriceWording", offerPriceWording);

		String gstAmount = MALAY_NA;
		String gstAmountWording = MALAY_NA;
		String overallctAmt = "";
		String overallctAmtWording = "";
		BigDecimal gstAmt = scLoa.getGstAmt();
		if (gstAmt != null) {
			gstAmount = amountDF.format(gstAmt);
			gstAmountWording = WordUtils.capitalizeFully(NumberWordUtil.convert(gstAmt
					.toString()));
			overallctAmt = amountDF.format(loaAmount.add(gstAmt));
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.add(gstAmt).toString()));
		} else {
			overallctAmt = amountDF.format(loaAmount);
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
		}
		context.put("gstAmt", gstAmount);
		context.put("gstAmtWording", gstAmountWording);
		context.put("overallCtAmt", overallctAmt);
		context.put("overallCtAmtWording", overallctAmtWording);

	}
	
	private SupplierGstResDTO getGSTInfo(Long supplierId, Long branchId) {
		
		ISupplierGstSO supplierGstSO = null;
		try {
			supplierGstSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
			SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
			gstReqDTO.setSupplierId(supplierId);
			gstReqDTO.setBranchCode(branchId);
			SupplierGstResDTO gstResDTO = supplierGstSO
					.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
			return gstResDTO;
		} finally {
			supplierGstSO = null;
		}
	}
	
	private void setGSTInfo(SupplierGstResDTO resDTO, ScLoa scLoa){
		
		if(resDTO.isSuccessful() && resDTO.getSmGstSupplier() != null){
			SmGstSupplier gstSupplier = resDTO.getSmGstSupplier();
			scLoa.setGstRegNo(gstSupplier.getGstRegNo());
			scLoa.setGstDeclaredDate(gstSupplier.getGstDeclaredDate());
			scLoa.setGstEffDate(gstSupplier.getGstEffDate());
		} else {
			scLoa.setGstRegNo(null);
			scLoa.setGstDeclaredDate(null);
			scLoa.setGstEffDate(null);
		}
		if(scLoa.getGstAmt() != null){
			BigDecimal ctAmount = getManagedBean().getOverallContractAmt();
			if ((ctAmount != null && scLoa.getLoaAmt() != null)
					&& !scLoa.getLoaAmt().equals(ctAmount)) {
				ctAmount = ctAmount.subtract(scLoa.getGstAmt());
				ctAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				this.getManagedBean().setOverallContractAmt(ctAmount);
			}
		}
		scLoa.setGstAmt(null);
	}
	
	public void tabChangeAction(int selectedTabIndex){
		
		if(selectedTabIndex != 0 && this.getManagedBean().isDisplayAlert()){
			this.getManagedBean().setDisplayAlert(false);
			RequestContext.getCurrentInstance().execute(
					"gstConfirmPopupWid.show()");
		}
		
	}
	
	private PmDocument getDocumentDtls(String docCategoryCode){
		IDocumentSO documentSO = null;
		PmDocument document = null;
		try{
		documentSO = (IDocumentSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDocumentSO.class);
		DocumentReqDTO reqDTO = new DocumentReqDTO();
		reqDTO.setDocCategoryCode(docCategoryCode);
		DocumentResDTO resDTO = documentSO.retrieveDocumentCheckList(reqDTO);

		for (PmDocument pmDocument : resDTO.getDocumentList()) {
			if (SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
					.equalsIgnoreCase(pmDocument.getDocDefinitionKey())) {
				document = pmDocument;
				break;
			}
		}
		return document;
		}
		finally{
			documentSO = null;
		}
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
			userReqDTO.setApprovalLimit(overallCtAmount(scLoa.getLoaAmt(),
					scLoa.getGstAmt()));
		}
		userReqDTO.setOrgProfileId(scLoa.getScLoiLoa().getPmOrgProfile()
				.getOrgProfileId());
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
		getManagedBean().setContractSignerList(userResDTO.getUserList());
		getManagedBean().setContractSignerUser(contractSignerUser);
	}
	
	/**
	 * Localize date.
	 *
	 * @param date the date
	 * @param locale the locale
	 * @return the string
	 */
	private static String getLocalizeDate(Date date, Locale locale) { 
		String DEFAULT_DATE_FORMAT_WITH_MONTH_NAME = "dd MMMM yyyy";
	    SimpleDateFormat localDateFormate = new SimpleDateFormat(DEFAULT_DATE_FORMAT_WITH_MONTH_NAME, locale);       
	    
	    return localDateFormate.format(date);
	}
	

	/**
	 * Gets the qt proposal valid expired message.
	 *
	 * @param scQt the sc qt
	 * @return the qt proposal valid expired message
	 */
	private void getQtProposalValidExpiredMessage(ScQt scQt){
		if(scQt != null && scQt.getProposalValidityEndDate() != null){
			if(!checkProposalValidityDateReachByQt(scQt)){
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
										new FacesScopes().getUserLocale())));
			}
		}
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

			//create destination File object
			//StringBuilder aFilePath = new StringBuilder("c:");
			StringBuilder aFilePath = new StringBuilder();
			aFilePath.append(actualFilePath);
			File newName = new File(aFilePath.toString());
			boolean isFileRenamed = oldName.renameTo(newName);
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
				if (null == scLoaReport.getPmDocument()) {
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
	 * Gets the ministry condition field.
	 *
	 * @param scLoa the sc loa
	 * @return the ministry condition field
	 */
	private void getMinistryConditionField(ScLoa scLoa) {
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(scLoiLoa.getPmOrgProfile()
				.getOrgProfileId());
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IOrgProfileSO.class);
		PmOrgProfile ministryOrgProfile = orgProfileSO
				.retrieveMinistryByOrgProfileId(orgProfileReqDTO)
				.getPmOrgProfile();
		StringBuilder criteria = new StringBuilder();
		criteria.append(
				"orgTypeCode.parameterCode")
				.append("^").append(OrganizationType.DIVISION.value())
				.append("|");
		
		criteria.append(
				"parentOrgProfile.orgProfileId")
				.append("^").append(ministryOrgProfile.getOrgProfileId())
				.append("|");
		
		criteria.append("recordStatus^1");
		getManagedBean().setMinistryConditionField(criteria.toString());
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
