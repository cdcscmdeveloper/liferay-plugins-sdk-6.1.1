package my.ep.web.backingbean.sc.loa;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletSession;

import my.ep.bean.application.NgepApplicationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.actor.IScActorSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.virtualcert.IVirtualCertSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MapKeyEnum;
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
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.actor.ScActorReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.requestitem.RequestItemReqDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.common.ScActor;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
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
import my.ep.persistence.entity.sc.loaloi.ScLoiTerm;
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
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
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
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.cm.codification.CodificationBackingBean;
import my.ep.web.backingbean.ct.amendment.JadualHargaBean;
import my.ep.web.bean.sc.common.ItemBean;
import my.ep.web.bean.sc.common.JadualHargaItemBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.SSTConstant;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentDefaultFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.sc.loa.LOADetailManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.VmUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
//Bug #34882@Syazani 26/06/2023
//#12177 Refactoe

// Auto-generated Javadoc
/**
 * The Class LOADetailBackingBean.
 *
 * @author Viswanath
 * 
 * @author Sri
 */
@ManagedBean(name = "lOADetailBackingBean")
@RequestScoped
public class LOADetailBackingBean extends LOABaseBackingBean<LOADetailManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The log. */
	public static Logger log = EpLogger.getLogger(LOADetailBackingBean.class);

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/** The text before sign. */
	private String textBeforeSign;

	/** The flag. */
	private boolean flag;
	private boolean sqExpiredMessageFlag = false;

	private static final String  BRANCH_INFO_TITLE = "Branch Information";

	private static final String MALAY_NA = "T/B";
	
	// #29006 - start
	private String notAvailabilityStr = MALAY_NA;
	private String selectedSupplierOffer = StringUtils.EMPTY;
	private String itemNameSearchCriteria;
	private List<ScLoiLoaItem> completeScLoiLoaItemList;
	private ScLoiLoaItem selectedScLoiLoaItem;
	private String lampiranA3SubStr = "Lampiran A3";
	// #29006 - end
		
	// 29007 -start
	private static final Locale myLocale = new Locale(
			SourcingConstants.MALAY_LANGUAGE_CODE,
			SourcingConstants.MALAYSIA_COUNTRY_CODE);

	// 29007 -end-

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
			
			if(!scLoa.isBrandNew())
			{
				getManagedBean().setFlowamt(scLoa.isFollowLoaAmt());
			}
			
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
				//#32497 - start
				if (scLoa != null && ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType())){
					awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
					awardDataType.setDeskOfficer(currentUser);
					awardDataType.setRequisitioner(null);
				}
				//#32497 - end
			}

			if(scLoa.isBrandNew()) {
				setUserDetails(scLoa);
				scLoa.setContractAgency(null);
			}

			PmOrgProfile ministryOrgProfile = retrieveMinistryOrgProfile(scLoa);
			getManagedBean().setMinistryOrgProfile(ministryOrgProfile);
			viewContractDetails(scLoa);

			resDTO.setScLoa(scLoa);

			populateLOADetails(resDTO);
			getManagedBean().setEditFlag(Boolean.TRUE);
			// #26475 - start
			if (getManagedBean().getSelectedPtj() == null) {
				getManagedBean().setSelectedPtj(scLoa.getContractAgency());
			}
			// #26475 - end

			//Enhancement #12177
			if(!getManagedBean().isTrackingDairyView()){
				if(scLoa.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_SUBMISSION)){
					if(!scLoa.isBrandNew() && !isSqStillValid() && sqExpiredMessageFlag ){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_760481, 
								null,MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					}
				}
				else if(!isSqStillValid() && sqExpiredMessageFlag ){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						MessageUtils.getMessage(MessageConst.ERROR_760481, 
							null,MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
			}
			
			FacesContext fc = FacesContext.getCurrentInstance();
			if(getManagedBean().getBackToPreviousPage() == null || getManagedBean().getBackToPreviousPage().isEmpty()){
				getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			}
			
			//#29007 Start Vinod
			if (Arrays.asList(QTStatusConst.LOA_PENDING_SUBMISSION,
					QTStatusConst.LOA_PENDING_VERIFICATION,
					QTStatusConst.LOA_PENDING_APPROVAL,
					QTStatusConst.LOA_PENDING_REVISION,
					QTStatusConst.LOA_PENDING_REVISION_APPROVAL,
					QTStatusConst.LOA_COMPLETED).contains(
					scLoa.getPmStatus().getStatusId())) {// 29007,#32119
				getManagedBean().setSpecificationAndSupplierDialogFlagQT(true);
			}
			ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
			String message = ResourceBundleUtils
					.getMessage(
							QTConstants.LBL_SC_LOILOA_SUPPLIERS_PROPOSAL_SPECIFICATION_UPDATE,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
			if (scLoiLoa.getScLois() != null) {
				for (ScLoi scLoi : scLoiLoa.getScLois()) {
					for (ScLoiTerm terms : scLoi.getScNegoTerms()) {
						if (terms.getTermDesc().contains(message)
								&& scLoa.getPmStatus().getStatusId() != QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT) {
							getManagedBean().setSupPropSpecUpdate(true);
							break;
						}
					}
				}
			}
			if (Arrays.asList(QTStatusConst.LOA_PENDING_VERIFICATION,
					QTStatusConst.LOA_COMPLETED).contains(
					scLoa.getPmStatus().getStatusId())) {// 29007,#32119-start
				getManagedBean().setSupPropSpecUpdate(true);
			}
			//#29007 End Vinod
			// #34915 idham @01/08/2023 - Start
			// sort by scLoiLoaItem.getTplSpecZoneDtl() and
			// scLoiLoaItem..getTplSpecTierQty()
			if (null != this.getManagedBean().getScLoa()
					&& !this.getManagedBean().getScLoa().getScLoiLoa()
							.getDocType()
							.equals(ParameterConst.DOCTYPE_PURCHASE_REQUEST)
					&& CollectionUtils.isNotEmpty(this.getManagedBean()
							.getScLoa().getScLoiLoaItems())) {
				sortLoiLoaItems(this.getManagedBean().getScLoa()
						.getScLoiLoaItems(), this.getManagedBean().getScLoa()
						.getScQt().isZonal());
			}
			// #34915 idham @01/08/2023 - End

		} finally {
			//qtAwardSO = null;
		}
		
		// Bug 34404 - related to 29007 - start
		if (this.getManagedBean().getScLoa() != null) {
			// fix issue 2 search action note 347
			this.getManagedBean().setLoiLoaItemsResult(
					new ArrayList<ScLoiLoaItem>(this.getManagedBean()
							.getScLoa().getScLoiLoaItems()));
		} else {
			this.getManagedBean().setLoiLoaItemsResult(null);
		}
		// Bug 34404 - related to 29007 - end
		
		return QTConstants.LOA_PAGE;
	}
	
	// #25201 -start-
	/**
	 * Get the supplier loa details.
	 * 
	 * @param sourceId 
	 * 		the source id (qt id / rn id
	 * @param loaId 
	 * 		the loa id
	 * @param previousPageUrl
	 * 		the previous page url.
	 * @param isReadonly
	 * 		the readonly flag
	 * @param enableDownloadAllDocuments
	 * 		either user can download documents
	 * 
	 * @return the supplier loa page
	 */
	public String retrieveSupplierLOADtl(Long sourceId, Long loaId, String previousPageUrl, boolean isReadonly, boolean enableDownloadAllDocuments) {
		Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
		otherArgs.put(MapKeyEnum.enableDownloadAllDocuments, Boolean.valueOf(enableDownloadAllDocuments));

		return retrieveSupplierLOADtlPriv(sourceId, loaId, previousPageUrl, isReadonly, otherArgs);
	}
	// #25201 -end-


	/**
	 * Get the supplier loa details.
	 * 
	 * @param sourceId 
	 * 		the source id (qt id / rn id
	 * @param loaId 
	 * 		the loa id
	 * @param previousPageUrl
	 * 		the previous page url.
	 * @param isReadonly
	 * 		the readonly flag
	 * 
	 * @return the supplier loa page
	 */
	public String retrieveSupplierLOADtl(Long sourceId, Long loaId, String previousPageUrl, boolean isReadonly) {
		// #26226, #33089 - Start
		Map<MapKeyEnum, Object> otherArgs = null;

		if (null != getManagedBean().getCurrentStatusId()
				&& getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.longValue()) {
			otherArgs = new HashMap<>();
			otherArgs.put(MapKeyEnum.enableDownloadAllDocuments, Boolean.TRUE);
		}
		
		// #19336, #25201, #17376, #19287 -start-
		return retrieveSupplierLOADtlPriv(sourceId, loaId, previousPageUrl, isReadonly, otherArgs);
		// #19336, #25201, #17376, #19287 -end-
		// #26226, #33089 - End
	}
	
	// #19336, #25201, #17376, #19287 -start-
	/**
	 * Get the supplier loa details.
	 * 
	 * @param sourceId 
	 * 		the source id (qt id / rn id
	 * @param loaId 
	 * 		the loa id
	 * @param previousPageUrl
	 * 		the previous page url.
	 * @param isReadonly
	 * 		the readonly flag
	 * @param otherArgs
	 * 		enableDownloadAllDocuments - java.lang.Boolean
	 * 		either user can download all attachments/documents or not
	 * 
	 * @return the supplier loa page
	 */
	private String retrieveSupplierLOADtlPriv(Long sourceId, Long loaId, String previousPageUrl, boolean isReadonly, Map<MapKeyEnum, Object> otherArgs) {
		try {
			//to avoid otherArgs is null checking next time
			if (otherArgs == null) {
				otherArgs = new HashMap<MapKeyEnum, Object>();
			}

			getManagedBean().setSourceId(sourceId);
			getManagedBean().setBackToPreviousPage(previousPageUrl);
			getManagedBean().setReadOnly(isReadonly);
			
			if (otherArgs.containsKey(MapKeyEnum.enableDownloadAllDocuments)
					&& otherArgs.get(MapKeyEnum.enableDownloadAllDocuments) instanceof Boolean) {
				Object o = otherArgs.get(MapKeyEnum.enableDownloadAllDocuments);
				
				if (o instanceof Boolean) {
					getManagedBean().setEnableDownloadAllDocuments((Boolean) o);
				}
			}

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);

			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrieveSupplierLOADtl(reqDTO);

			ScLoa scLoa = resDTO.getScLoa();
			getManagedBean().setScLoa(scLoa);

			// Supplier Bank Account
			getManagedBean().setBankList(resDTO.getSmSupplierBanks());

			// Setting OverallContractAmount
			calculateOverallCtAmtForSupplier();
			
			// setting supplier proposal
			getManagedBean().setScQtProposal(resDTO.getSupplierProposal());
			
			// #11134 - start
			// Remarks
			scLoa.setScLoaRemarks(initializeRemarks(scLoa, 
					scLoa.getCurrentStatus(),
					scLoa.getScLoaRemarks(), ScLoaRemark.class));
			// #11134 - end
			
			retrieveValBankAccNoDropdown(); //Bug #33674@Syazani -- 14/09/2022
			
			//Enhancement #24259 #24269
//			isGSTRegActive();
//			isSSTRegActive();

			// Setting BI Report
			List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
			if (scLoaReports != null && !scLoaReports.isEmpty()) {
				//Bug #32188 - start
				if (ParameterConst.DOCTYPE_DIRECT_NEGO
						.equalsIgnoreCase(scLoa.getScLoiLoa().getDocType())) {
					for (ScLoaReport report : scLoaReports) {
						if (null != report.getPmDocument()) {
							if (SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
									.equalsIgnoreCase(report.getPmDocument().getDocDefinitionKey())) {
								getManagedBean().setBranchInfoReport(report);
								break;
							}
						}
					}
				}else if (ParameterConst.DOCTYPE_QUOTATION
						.equalsIgnoreCase(scLoa.getScLoiLoa().getDocType())){
					for (ScLoaReport report : scLoaReports) {
						if (null != report.getPmDocument()) {
							if (SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
									.equalsIgnoreCase(report.getPmDocument().getDocDefinitionKey())
									&& BRANCH_INFO_TITLE.equalsIgnoreCase(report.getTitle())) {
								getManagedBean().setBranchInfoReport(report);
								break;
							}
						}
					}
				}
				else{
					for (ScLoaReport report : scLoaReports) {
						// Check whether Branch Info attachment is existed or not
						if (null == report.getPmDocument()) {
							getManagedBean().setBranchInfoReport(report);
							break;
						}
					}
				}
				//Bug #32188 - end
				
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

			if(scLoa.isBondReq()){
				//Enhancement #24259 #24269
//				scLoa = calcBondAmount(scLoa, Boolean.FALSE);
				scLoa = calcBondAmountWithSST(scLoa, Boolean.FALSE);
				
				
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

			// Bug #24502
			// QT Proposal validity expired
			// getQtProposalValidExpiredMessage(scQt);

			prepareSupplierSignerInfo();
			
			// Bug 34404 - related to 29007 - start
			if (this.getManagedBean().getScLoa() != null) {
				// fix issue 2 search action note 347
				this.getManagedBean().setLoiLoaItemsResult(
						new ArrayList<ScLoiLoaItem>(this.getManagedBean()
								.getScLoa().getScLoiLoaItems()));
			} else {
				this.getManagedBean().setLoiLoaItemsResult(null);
			}
			// Bug 34404 - related to 29007 - end
			
			// Bug #35078@Syazani -- 22/09/2023 -- Start
			if (isQtAudit()) {
				this.getManagedBean().setDownloadLOADisabled(false);
				this.getManagedBean().setReadOnlyLOADownload(false);
			} else {
				this.getManagedBean().setDownloadLOADisabled(!this.getManagedBean().getEnableDownloadAllDocuments());
				this.getManagedBean().setReadOnlyLOADownload(this.getManagedBean().isReadOnly());
			}
			// Bug #35078@Syazani -- 22/09/2023 -- End

			return QTConstants.SUPPLIER_LOA_PAGE;

		} catch (Exception e) {
			handleException(e);
		} finally {
			//qtAwardSO = null;
		}

		return null;
	}
	// #19336, #25201, #17376, #19287 -end-

	/**
	 * Pre render page navigation to loa page based on task id
	 * @return void
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
            
			log.info("SCAwardSupplierDataType     "+dpDataType);
            log.info(" Source Id   "+dpDataType.getSourceId());
            log.info(" Award Id    "+dpDataType.getAwardId());
            
			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			
			Long loaId = Long.valueOf(dpDataType.getAwardId()!=null?dpDataType.getAwardId():0);
			
			getManagedBean().setSourceId(sourceId);
			getManagedBean().setPayLoad(dpDataType);

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOADtlByLoaId(reqDTO);
			
			if (resDTO.getScLoa() != null) {
				ScLoa scLoa = resDTO.getScLoa();

				// active tab index for loa contents
				if(getManagedBean().getCurrentStatusId() != null){
					if(QTStatusConst.LOA_PENDING_VERIFICATION.equals(getManagedBean().getCurrentStatusId())){
						if(scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getDocType() != null){
							getManagedBean().setSelectedTabIndex(1);
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

	public PmOrgProfile retrieveMinistryOrgProfile(ScLoa scLoa) {
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();

		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(scLoiLoa.getPmOrgProfile().getOrgProfileId());

		PmOrgProfile ministryOrgProfile = getOrgProfileSOGlobal().retrieveMinistryByOrgProfileId(orgProfileReqDTO)
				.getPmOrgProfile();
		return ministryOrgProfile;
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
			getManagedBean().setLoaTitle(Boolean.TRUE);
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
		SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
		Long sourceId = Long.valueOf(dpDataType.getSourceId());
		Long loaId = Long.valueOf(dpDataType.getAwardId());

		getManagedBean().setPayLoad(dpDataType);

		String redirectUrl = retrieveSupplierLOADtl(sourceId, 
				loaId, null, getManagedBean().isReadOnly());
		
		// #29007 Start Vinod
		if (Arrays.asList(QTStatusConst.LOA_PENDING_SUBMISSION,
				QTStatusConst.LOA_PENDING_REVISION).contains(
				this.getManagedBean().getScLoa().getPmStatus().getStatusId())) {
			getManagedBean().setSpecificationAndSupplierDialogFlagQT(true);
		}
		ScLoiLoa scLoiLoa = this.getManagedBean().getScLoa().getScLoiLoa();
		String message = ResourceBundleUtils.getMessage(
				"lbl.sc.loiloa.suppliers.proposal.specification.update",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		if (scLoiLoa.getScLois() != null) {
			for (ScLoi scLoi : scLoiLoa.getScLois()) {
				for (ScLoiTerm terms : scLoi.getScNegoTerms()) {
					if (terms.getTermDesc().contains(message)
							&& this.getManagedBean().getScLoa().getPmStatus()
									.getStatusId() != QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT) {
						getManagedBean().setSupPropSpecUpdate(true);
						break;
					}
				}
			}
		}

		

		if (getManagedBean().getScLoa().getScLoaReports() != null) {
			List<ScLoaReport> loaReports = getManagedBean().getScLoa()
					.getScLoaReports();
			for (ScLoaReport scLoaReport : loaReports) {
				if (scLoaReport.getTitle().contains(lampiranA3SubStr)) {
					scLoaReport.setUploadlampiranA3(true);
				}
			}
		}
		// #29007 End Vinod
		
		// #19336 -start-
		getManagedBean().setValidatewitness(Boolean.TRUE);
		// #19336 -end-

		if (StringUtils.isNotBlank(redirectUrl)) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, redirectUrl);	
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

		//25022
		List<BigDecimal> scheduleNo = new ArrayList<BigDecimal>();
		for (ScQtSchedule schedule : loa.getScQtSchedules()) {
			if (scheduleNo.contains(schedule.getScheduleNo())) {
				throw new EpException(MessageConst.ERROR_760319);
			} else {
				scheduleNo.add(schedule.getScheduleNo());
			}
		}
		
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

		//Supplier overall contract amount
		//Enhancement #24259 #24269
//		BigDecimal overallCtAmout = overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt());
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
		handleBranchDetailsChanged(false);

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

		if (scLoa.getContractType().getParameterId() != null) {
			this.getManagedBean().setContractTypeId(scLoa.getContractType().getParameterId());
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
		scLoa.setScLoaRemarks(initializeRemarks(scLoa, 
				scLoa.getCurrentStatus(),
				scLoa.getScLoaRemarks(), ScLoaRemark.class));
		
		// #11134 - start
		List<ScLoaRemark> supplierRemarks = filterSupplierRemarks(scLoa.getScLoaRemarks(), false);
		
		getManagedBean().setSupplierRemarks(supplierRemarks);
		// #11134 - end		

		// check agreement
		checkAgreementMandatory();

		// check performance bond		
		//Bug #29885 start isPendingSubmissionStatus or isPendingRevisionStatus
		if (QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa.getPmStatus().getStatusId().longValue()) || 
				QTStatusConst.LOA_PENDING_REVISION .equals(scLoa.getPmStatus().getStatusId().longValue())){
			checkPerformanceBondMandatory();
		}
		//Bug #29885 end
		
		//Enhancement #24259 #24269
//		scLoa = calcBondAmount(scLoa, false);
		scLoa = calcBondAmountWithSST(scLoa, false);
		
		if (scLoa.getBondAmt() != null) {
			getManagedBean().setBondAmountWord(
					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		}

		// #19336, #25201, #17376, #19287 -start-
		//awarded supplier and proposal
		if (resDTO.getSupplierProposal() != null) {
			getManagedBean().setScQtProposal(resDTO.getSupplierProposal());
		}
		// #19336, #25201, #17376, #19287 -end-

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
				log.error(e.getMessage(), e);
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
		if(scLoaReport.getTitle().equalsIgnoreCase(QTConstants.REPORT_ACKNOWLEDGEMENT_LOA_RECEIPT) &&
				getManagedBean().getScLoa().getPmStatus().getStatusId().
				equals(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT)) {
			if (this.getManagedBean().isValidatewitness()) { // #19336
				validateLoaWitnessAndAckBy();
			} // #19336
		}

		ScLoaReportAttachment scLoaReportAttachmentGenerated = generateDocumentAttachment(scLoaReport);
		if(scLoaReport.getDefinationType().getParameterCode().equals(
				ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD) ||
				scLoaReport.getDefinationType().getParameterCode().equals(
						ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD)) {
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
		}
		if(scLoaReportAttachmentGenerated != null) {
			String prefixName = scLoaReport.getTitle().replaceAll(
					SourcingConstants.REG_EXP_WHITE_SPACE, "");
			scLoaReportAttachmentGenerated.setTempFileName(prefixName);
		}
		
		// #19336, #25201, #17376, #19287 -start-
		try {
			downloadFile(scLoaReportAttachmentGenerated);
		} catch (IOException ex) {
			if (ex instanceof FileNotFoundException) {
				handleException(new EpException(MessageConst.ERROR_760411,
						new String[] { scLoaReport.getTitle() }));
			}

		} catch (EpException ex) {
			handleException(ex);
		}
		// #19336, #25201, #17376, #19287 -end-
	}

	/**
	 * Generate document attachment.
	 *
	 * @param scLoaReport the sc loa report
	 * @return the sc loa report attachment
	 */
	private ScLoaReportAttachment generateDocumentAttachment(
			ScLoaReport scLoaReport) {
		ScLoa scLoa = getManagedBean().getScLoa();
		ScQt scQt = getManagedBean().getScQt();//26226
		String definitionType = scLoaReport.getDefinationType()
				.getParameterCode();
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();
		StringBuilder filename = new StringBuilder();//26226
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType) || 
				ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD.equals(definitionType) ||
				ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD.equals(definitionType) ||
				ParameterConst.DOCUMENT_DEFINITION_KEY_LETTER_OF_ACCEPTANCE.equals(definitionType)) {

			try {
				String docDefinitionKey = scLoaReport.getPmDocument()
						.getDocDefinitionKey();
				Long statusId = scLoa.getPmStatus().getStatusId(); //26226

				if (scLoaReportAttachment == null) {
					//replace white space due to firefox 
					filename.append(scLoaReport.getTitle().replaceAll("\\s",""));
					scLoaReportAttachment = new ScLoaReportAttachment();
					scLoaReportAttachment.setScLoaReport(scLoaReport);
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());

					if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER.trim().equals(docDefinitionKey.trim())) {
						//#29007 Start Vinod
						if (StringUtils.isNotBlank(scLoaReport.getTitle()) && scLoaReport.getTitle().contains(lampiranA3SubStr)
							&&	scLoa.getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)) {
							filename.append(QTConstants.LAMPIRAN_A3_FILE_EXTENSION_PDF);
							scLoaReportAttachment.setFileName(filename.toString().replaceAll("/",""));
							log.info("filename--->    "+scLoaReportAttachment.getFileName());
							generateLampiranA3PdfFromVelocity(scLoaReportAttachment, scLoa);
						}else{
						//#29007 End Vinod
						filename.append(".docx");
						scLoaReportAttachment.setFileName(filename.toString());
						generateAgreementLetterDocFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa, this.getManagedBean().getScQt());
						}//#29007 Added Vinod
					}else if (SourcingConstants.DOC_DEF_KEY_ACKNOWLEDGEMENT_LOA_RECEIPT
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateAckOfLOAReceiptPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					}else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateDeclarationPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					}else if (SourcingConstants.DOC_DEF_KEY_SURAT_AKUAN_SYARIKAT
							.equals(docDefinitionKey)){
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateDeclarationOfOathFromVelocity(scLoaReportAttachment, scLoa);
					}
					/*else if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateContractSummaryPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
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
					}*/

				}else if(scLoaReportAttachment != null && 
						QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId) || QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId)
						|| QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId)){
					// if pending supplier acknowledgment , always regenerate 
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());

					if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
							.equals(docDefinitionKey)) {
						//filename.append(".pdf");
						//scLoaReportAttachment.setFileName(filename.toString());
						/*generateAgreementLetterDocFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa, getManagedBean().getScQt());*/
					}else if (SourcingConstants.DOC_DEF_KEY_ACKNOWLEDGEMENT_LOA_RECEIPT
							.equals(docDefinitionKey)) {
						//filename.append(".pdf");
						//scLoaReportAttachment.setFileName(filename.toString());
						/*generateAckOfLOAReceiptPdfFromVelocity((ScAttachment) scLoaReportAttachment, scLoa);*/
					}else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
							.equals(docDefinitionKey)) {
						//filename.append(".pdf");
						//scLoaReportAttachment.setFileName(filename.toString());
						/*generateDeclarationPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);*/
					}else if (SourcingConstants.DOC_DEF_KEY_SURAT_AKUAN_SYARIKAT
							.equals(docDefinitionKey)){
						//generateDeclarationOfOathFromVelocity(scLoaReportAttachment, scLoa);
					}

					/*else if (SourcingConstants.DOC_DEF_KEY_JADUAL_HARGA
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
					} else if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateContractSummaryPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateContractSummaryPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					}*/

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

		ScLoa scLoa = getManagedBean().getScLoa();
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();

		for (ScLoaReport scLoaReport : scLoaReports) {
			if(scLoaReport.getTitle().equalsIgnoreCase(QTConstants.REPORT_ACKNOWLEDGEMENT_LOA_RECEIPT) &&
					scLoa.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT)) {
				if (this.getManagedBean().isValidatewitness()) { // #19336
					validateLoaWitnessAndAckBy();
				} // #19336
			}
			
			ScLoaReportAttachment scLoaReportAttachment = scLoaReport
					.getScLoaReportAttachment();
			ScLoaReportAttachment scLoaReportAttachmentGenerated = generateDocumentAttachment(scLoaReport);

			if(scLoaReportAttachmentGenerated != null){
				if(scLoaReport.getDefinationType().getParameterCode().equals(
						ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD) ||
						scLoaReport.getDefinationType().getParameterCode().equals(
								ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD)) {
					scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
				} 
				
				// #29006 - start
				String prefixName = scLoaReport.getTitle().contains(lampiranA3SubStr) ? scLoaReport.getTitle().replaceAll("/", "-")
						: scLoaReport.getTitle().replaceAll(SourcingConstants.REG_EXP_WHITE_SPACE, "");
				// #29006 - end, #29007 Vinod changed setting title for lampiranA3
				
				// 29007-start
				if (Arrays.asList(QTStatusConst.LOA_PENDING_VERIFICATION).contains(
						this.getManagedBean().getScLoa().getPmStatus().getStatusId())
						&& scLoaReport.getTitle().contains(lampiranA3SubStr)
						&& ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa()
								.getDocType()) && scLoaReportAttachmentGenerated.isBrandNew()) {
					prefixName = prefixName.concat("1");
				}
				// 29007-End

				scLoaReportAttachmentGenerated.setTempFileName(prefixName);
				scAttachments.add(scLoaReportAttachmentGenerated);
			}
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
		String title=scLoiLoa.getTitle();
		if (title == null) {
			title = "";
		}
		title = title.trim();
		//replace all special characters
		//title = title.replaceAll("\\W", ""); 
		title = title.replaceAll("<", "&#60;");
		title = title.replaceAll(">", "&#62;");
		title = title.replaceAll("\r\n", "<br />");
		//title=title.replaceAll("&", "&#38;");

		context.put("title", title);
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

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_CONTRACT_SUMMARY, context,
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
	 * Generate agreement letter doc from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @param scQt the sc qt
	 * @throws Exception the exception
	 */
	private void generateAgreementLetterDocFromVelocity(ScAttachment attachment, ScLoa scLoa, ScQt scQt) throws Exception {
		debugLogger("generateAgreementLetterDocFromVelocity");
		populateAttachmentDetails(attachment);
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
				.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();

		DecimalFormat integerDF = new DecimalFormat(
				commonUtils
				.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
				.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		DecimalFormat percentageDF = new DecimalFormat(
				commonUtils
				.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));
		VelocityContext context = new VelocityContext();

		// date formatter
		context.put("dateSDF", dateSDF);
		boolean isZonal = scLoa.getScLoiLoa().isZonal();
		context.put("isZonal", isZonal);

		boolean isSL1M = scLoa.isSl1mReq();
		context.put("isSL1M", isSL1M);

		String fileNo = "";
		if(!StringUtils.isBlank(scLoiLoa.getFileNo())){
			fileNo = scLoiLoa.getFileNo();
			if(fileNo.length()>30){
				fileNo = splitLongLengthString(fileNo, 29);
			}
		}
		context.put("fileNo", fileNo + "-" + scLoa.getFileExtNo());
		context.put("loaNo", scLoa.getLoaNo());

		String issueDate = "______________";
		if (scLoa.getIssueDate() != null) {
			issueDate = getLocalizeDate(scLoa.getIssueDate(), myLocale);
		}
		context.put("issueDate", issueDate);

		String govtSignDate = "______________";
		if (scLoa.getGovtSignDate() != null) {
			govtSignDate = getLocalizeDate(scLoa.getGovtSignDate(), myLocale);
		}
		context.put("govtSignDate", govtSignDate);

		context.put("supplierName", scLoiLoa.getSupplierName());
		String supplierAddress = "";
		if (scLoa.getScSupplierAddress() != null) {
			supplierAddress = getFullAddress(scLoa.getScSupplierAddress());
		}
		log.info("supplierAddress" + supplierAddress);
		//context.put("supplierAddress", supplierAddress);
		StringBuilder supplierAddressTable = null;
		if(StringUtils.isNotBlank(supplierAddress)){
			String[] address = supplierAddress.split(System.getProperty("line.separator"));
			supplierAddressTable = new StringBuilder();
			for (String suppAddr : address) {
				supplierAddressTable.append(suppAddr);
				supplierAddressTable.append("<br />");
			}
		}
		log.info("SupplierAddressTable" + supplierAddressTable);
		context.put("supplierAddressTable", supplierAddressTable);


		String headerTitle = "";
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			headerTitle = "Pembelian Terus (Bermasa)";
		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			if (ParameterConst.PROCUREMENT_MODE_TENDER.equals(scLoa.getProcurementMode().getParameterCode())) {
				headerTitle = "Tender";
			} else {
				headerTitle = "Sebut Harga";
			}

		}
		context.put("headerTitle", headerTitle); 
		context.put("subTitle", headerTitle.toLowerCase()); 

		// loa title
		String title = scLoiLoa.getTitle();
		if (title == null) {
			title = "";
		}
		title = title.trim();

		//replace all special characters
		//title = title.replaceAll("\\W", ""); 
		//title=title.replaceAll("&", "&#38;");
		title = title.replaceAll("<", "&#60;");
		title = title.replaceAll(">", "&#62;");
		title = title.replaceAll("\r\n", "<br />");
		context.put("loaTitle", title);

		context.put("hasLoi", false);
		context.put("bondReq", scLoa.isBondReq());

		String bondAmt = "______________";
		String bondAmtText = "______________";
		if (scLoa.getBondAmt() != null) {
			bondAmt = amountDF.format(scLoa.getBondAmt());
			bondAmtText = NumberWordUtil.convert(scLoa.getBondAmt().toString());
		}
		context.put("bondAmt", bondAmt);
		context.put("bondAmtText", bondAmtText);

		String bondPercentage = "______________";
		String bondPercentageText = "______________";
		if (scLoa.getBondPercentage() != null) {
			bondPercentage = percentageDF.format(scLoa.getBondPercentage());
			bondPercentageText  = NumberWordUtil.convert(scLoa.getBondPercentage().toString());
		}
		context.put("bondPercentageText", bondPercentageText); 
		context.put("bondPercentage", bondPercentage);

		String bondExpDate = "______________";
		//bond exp date should be one year after the end of the contract period
		if(scLoa.getContractExpDate() != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(scLoa.getContractExpDate());
			cal.add(Calendar.YEAR, 1); //add one year
			bondExpDate = getLocalizeDate(cal.getTime(), myLocale);
		} 
		context.put("bondExpDate", bondExpDate); 

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

		//ontractDuration
		context.put("contractDuration", scLoa.getContractDuration());

		context.put("isAgreementReq", scLoa.isAgreementReq());

		String LoaSigner = "______________";
		String loaSignerDesig = "______________";
		if(getManagedBean().getContractSignerUser() != null){
			PmUser contractSigner = getManagedBean().getContractSignerUser();
			if(StringUtils.isNotEmpty(contractSigner.getUserName())){
				LoaSigner = contractSigner.getUserName();
			}
		}
		context.put("loaSigner", LoaSigner);	

		loaSignerDesig = scLoa.getLoaSignerDesig();
		if(StringUtils.isNotEmpty(loaSignerDesig) && loaSignerDesig.length()>86){
			loaSignerDesig = splitLongLengthString(loaSignerDesig, 86);
		}
		context.put("loaSignerDesignation", loaSignerDesig);

		// Setting GST info
		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
		String gstRegFlag = "false";
		if(supplier != null && supplier.getGstRegisteredFlag()== DbValueConst.RECORD_STATUS_ACTIVE){
			gstRegFlag = "true";
		}
		context.put("gstRegFlag", gstRegFlag);
		
//		setGstInfoForAgreementLetter(context, scLoa, amountDF, myLocale);
		setSstInfoForAgreementLetter(context, scLoa, amountDF, myLocale);
		setSSMInfoForAgreementLetter(context, scLoa, scQt, myLocale);
		setMofInfoForAgreementLetter(context, scLoa, scQt, myLocale);
		setMinSL1MParticipant(context, scLoa, integerDF);

		String fiService = "T/B";
		if(scQt != null){
			if(ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode())){
				fiService = "0.8%";
			}
			else if(ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scQt.getFulfilmentType().getParameterCode())
					|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(scQt.getFulfilmentType().getParameterCode())){ //Bug #22393
				fiService = "0.4%";
			}
		}else{
			//Normal DP LOA - FufillmentType (schedule) //Bug #22393
			fiService = "0.4%";
		}
		context.put("fiService", fiService);

		String qtNo = MALAY_NA;
		if(scQt != null) {
			qtNo = scQt.getQtNo();
		}
		// qtno
		context.put("qtNo", qtNo);

		// JadualHarga
		formJadualHarga(context, scLoa);

		// Specification
		formSpecification(context, scLoa);
		
		// #26130 - Start
		if (scQt != null
				&& (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType()) || ParameterConst.DOCTYPE_DIRECT_NEGO
						.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode())
						|| ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scQt.getFulfilmentType().getParameterCode()) || ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
							.equals(scQt.getFulfilmentType().getParameterCode()))) {
			constructSupplierSpecificationAndRemarks(context, scLoa);
		}
		// #26130 - End

		// #26130 - Start
		if (scQt != null
				&& (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType()) || ParameterConst.DOCTYPE_DIRECT_NEGO
						.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode())
						|| ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scQt.getFulfilmentType().getParameterCode()) || ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
							.equals(scQt.getFulfilmentType().getParameterCode()))) {
			constructSupplierSpecificationAndRemarks(context, scLoa);
		}
		// #26130 - End

		// #26226 - Start
		if ((ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode()
						.getParameterCode()) || ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scLoa.getScLoiLoa()
						.getFulfilmentTypeCode().getParameterCode()))) {

			constructPRSupplierSpecificationAndRemarks(context, scLoa);
		}
		// #26226 - End

		// scLoa
		context.put("scLoa", scLoa);

		String content = null;
		if(scLoa.getProcurementTypeCatId() != null && 
				scLoa.getProcurementTypeCatId().getParameterCode().equals(ParameterConst.PROCUREMENT_TYPE_CODE_SUPPLY)){
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(SourcingConstants.LOA_AGREEMENT_LETTER_PRODUCT, context);
		} else{
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(SourcingConstants.LOA_AGREEMENT_LETTER_SERVICE, context);
		}

		if (content != null) {
			DocumentGenerator.convertHtmlToDocFile(content, getActualFilePath(attachment), scLoa.getLoaNo());
		}
	}

	/**
	 * Form jadual harga.
	 * @param context the context
	 * @param scLoa the sc loa
	 */
	private void formJadualHarga(VelocityContext context, ScLoa scLoa){
		JadualHargaItemBean jadualHargaItemBean = new JadualHargaItemBean();
		List<ItemBean> productItemBeans = new ArrayList<ItemBean>();
		List<ItemBean> serviceItemBeans = new ArrayList<ItemBean>();
		ItemBean itembean= new ItemBean(); // 26226
		ItemBean jadualHarga = null;
		String itemType = null;
		//26226 Start 
		String dafaltValue = SSTConstant.TB;
		if (SSTConstant.LANGUAGE_CODE_EN.equals(new FacesScopes().getUserLocale().getLanguage())) {
			dafaltValue = SSTConstant.TB;
		}
		//End

		for (ScLoiLoaItem item : scLoa.getScLoiLoaItems()) {
			if(null != item.getScRequestItem().getItemType()){
				itemType = item.getScRequestItem().getItemType().getParameterCode();
				if (ParameterConst.ITEM_TYPE_PRODUCT.equals(itemType)) {
					jadualHarga = mappingJadualHarga(item); //26226
					productItemBeans.add(jadualHarga);
				} else if (itemType.equals(ParameterConst.ITEM_TYPE_SERVICE)) {
					//26226 Start   
					//serviceItemBeans.add(jadualHarga);
					TplSpecQuestion tplSpecQuestion=item.getScRequestItem().getTplSpecQuestion();
					if(tplSpecQuestion.getQuestionType().equals("I"))
					{
						jadualHarga = mappingJadualHarga(item);
				
						serviceItemBeans.add(jadualHarga);
					}
					//26226 end
				}
			}
		}
		jadualHargaItemBean.setProductItemBeans(productItemBeans);
		jadualHargaItemBean.setServiceItemBeans(serviceItemBeans);
		context.put("jadualHargaItemBean", jadualHargaItemBean);
		// 26226 Start
		context.put(SSTConstant.serviceScope, dafaltValue);
		context.put(SSTConstant.itemType, itemType);
		// End
	}

	public void formSpecification(VelocityContext context, ScLoa scLoa)
			throws Exception {
		List<ItemBean> serviceBeans  = null;
		List<ItemBean> productBeans  = null;

		// Filter By - docType & docId
		String docType = scLoa.getScLoiLoa().getDocType();
		Long docId = scLoa.getScLoiLoa().getDocId();
		if (ParameterConst.DOCTYPE_QUOTATION.equals(docType)) {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(docId);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveQTDocByQtId(reqDTO);

			if (resDTO.isSuccessful()) {
				if(resDTO.getProductDoc() != null) {
					TplSpecMaster productSpecMaster = resDTO.getProductDoc().getTplSpecMaster();
					productBeans = convertSpecQuestionToSpecAnswer(productSpecMaster);
				}
				if(resDTO.getServiceDoc() != null) {
					TplSpecMaster serviceSpecMaster = resDTO.getServiceDoc().getTplSpecMaster();
					serviceBeans = convertSpecQuestionToSpecAnswer(serviceSpecMaster);
				}
			}
		} else {
			serviceBeans = new ArrayList<ItemBean>();
			List<Long> requestItemIds = new ArrayList<Long>();
			for(ScLoiLoaItem loiLoaItem : scLoa.getScLoiLoaItems()) {
				ScRequestItem requestItem = loiLoaItem.getScRequestItem();
				Long requestItemId = requestItem.getRequestNoteItemId();
				if(!requestItemIds.contains(requestItemId)) {
					ItemBean itemBean = new ItemBean();
					itemBean.setItem(requestItem.getItemName());
					List<String> specs = new ArrayList<String>();
					specs.add(requestItem.getItemDesc());
					itemBean.setSpecs(specs);
					serviceBeans.add(itemBean);
					requestItemIds.add(requestItemId);
				}
			}
		}

		context.put("productAnswers", productBeans);
		context.put("serviceAnswers", serviceBeans);
	}

	/**
	 * Generate ack of loa receipt pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */
	public void generateAckOfLOAReceiptPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception {
		populateAttachmentDetails(attachment);
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		VelocityContext context = new VelocityContext();

		context.put("loaNo", scLoa.getLoaNo());

		String fileNo ="";
		if(!StringUtils.isBlank(scLoiLoa.getFileNo())){
			fileNo = scLoiLoa.getFileNo();
			if(fileNo.length()>30){
				fileNo = splitLongLengthString(fileNo, 29);
			}
		}
		context.put("fileNo", fileNo + "-" + scLoa.getFileExtNo());

		String govtSignDate	 = "";
		if (scLoa.getGovtSignDate() != null) {
			govtSignDate = getLocalizeDate(scLoa.getGovtSignDate(), myLocale);
		}
		context.put("govtSignDate", govtSignDate);

		String supplierSignDate = "";
		if (scLoa.getSupplierSignDate() != null) {
			supplierSignDate = getLocalizeDate(scLoa.getSupplierSignDate(), myLocale);
		}
		context.put("supplierSignDate", supplierSignDate);

		// Supplier Witness
		String supplierWitnessUser = "";
		String supplierWitnessIcNo = "";
		String supplierWitnessAddress = "";

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
			supplierWitnessAddress = supplierWitness.getAddress();
		}

		supplierWitnessAddress = supplierWitnessAddress.replaceAll("\r\n", "<br />");
		context.put("supplierWitnessUser", supplierWitnessUser);
		context.put("supplierWitnessIcNo", supplierWitnessIcNo);
		context.put("supplierWitnessAddress", supplierWitnessAddress);

		// Supplier Signer
		String supplierSignerUser = "";
		String supplierSignerIcNo = "";
		String supplierSignerAddress = "";

		PmUser ackBy = scLoa.getAckBy();
		if(ackBy !=null){
			supplierSignerUser = ackBy.getUserName();
			supplierSignerIcNo = ackBy.getIdentificationNo();
		}

		if (scLoa.getScSupplierAddress() != null) {
			supplierSignerAddress = getFullAddress(scLoa.getScSupplierAddress());
		}
		log.info("supplierAddress" + supplierSignerAddress);
		//context.put("supplierAddress", supplierAddress);
		StringBuilder supplierAddressTable = null;
		if(StringUtils.isNotBlank(supplierSignerAddress)){
			String[] address = supplierSignerAddress.split(System.getProperty("line.separator"));
			supplierAddressTable = new StringBuilder();
			for (String suppAddr : address) {
				supplierAddressTable.append(suppAddr);
				supplierAddressTable.append("<br />");
			}
		}
		log.info("SupplierAddressTable" + supplierAddressTable);

		context.put("supplierSignerUser", supplierSignerUser);
		context.put("supplierSignerIcNo", supplierSignerIcNo);
		context.put("supplierSignerAddress", supplierAddressTable);

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_ACKNOWLEDGEMENT_OF_LOA_RECEIPT, context,
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

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdf(
					fos,
					termsConditions);
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
		String procurementMode = "";
		String ptjName=" ";
		String qtNo = "&#60;Quotation/Tender No.&#62;";	
		
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa()
				.getDocType())) {

			qtNo = scLoa.getScQuote().getQuoteNo();
			procurementMode = SourcingConstants.PROCUREMENT_MODE_SIMPLE_QUOTE;
			log.info("=====if dp generateDeclarationFromVelocity");
			ptjName=retrievePTJByOrgProfileId(pmOrgProfile.getOrgProfileId(),scLoa.getScQuote().getCreatedDate());
			if(null==ptjName || "".equals(ptjName)){
				ptjName= pmOrgProfile.getOrgName();
			}
		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			if (scQt != null) {
				if(null!=scLoa.getPmStatus() && null!=scLoa.getPmStatus().getStatusId()){
					log.info("=====if qt generateDeclarationFromVelocity");
					log.info("publish date qt "+scQt.getPublishDate());
					log.info("current date qt "+scQt.getCreatedDate());
					ptjName=retrievePTJByOrgProfileId(pmOrgProfile.getOrgProfileId(),isQTStatusPublished(scLoa.getPmStatus().getStatusId())?scQt.getPublishDate() : scQt.getCreatedDate());
				}else{
					log.info("=====else generateOfferedAndQTDtlsPdf");
					ptjName=pmOrgProfile.getOrgName();
				}
				if (ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt
						.getProcurementMode().getParameterCode())) {
					procurementMode = "";//#32739 & #32738
				} else {
					procurementMode = "";//#32739 & #32738
				}
				if(scQt.getQtNo() != null){
					qtNo = scQt.getQtNo();
				}
			}
		}

		// #32739 & #32738 -start-
		// #32647 & #32648 remove uppercase -start-
		String title = procurementMode != "" ? procurementMode + "\r\n" + scLoiLoa.getTitle() : scLoiLoa.getTitle();
		// #32647 & #32648 remove uppercase -end-
		// #32739 & #32738 -end-		

				if (title == null) {
					title = "";
				}
				title = title.trim();

				//replace all special characters
				//title = title.replaceAll("\\W", ""); 

				//#13245 don't escape
				//title=title.replaceAll("&", "&#38;");
				int countTitle = title.length();//#32738 & #32739 spacing
				title = title.replaceAll("<", "&#60;");
				title = title.replaceAll(">", "&#62;");
				title = title.replaceAll("\r\n", "<br />");
				//#32739 & #32738 - start
				if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa()
						.getDocType())) {
					context.put("title", title + "<br />");
				} else if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa()
						.getDocType())) {
				context.put("title", title);
				}
				////#32739 & #32738 - end
				context.put("countTitle", countTitle);//#32738 & #32739 spacing

				String userName = "&#60;Nama Wakil Syarikat&#62;";//#32738 & #32739
				String icNo = "&#60;No. Kad Pengenalan&#62;";
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
				context.put("agency", ptjName);
				context.put("qtNo", qtNo);
				
				//#32648 populate lampiran 7 if LOA at supplier - start 
				int isLoa = 0;
				if (null != pmUser) {
					isLoa = 1;
				}

				context.put("isSupplierLoa", isLoa);
				//#32648 populate lampiran 7 if LOA at supplier - end

				String loaNo = "&#60;LOA No.&#62;";
				if(scLoa.getLoaNo() != null){
					loaNo = scLoa.getLoaNo();
				}
				context.put("loaNo", loaNo);
				context.put("procurementMode", procurementMode.toLowerCase());

				String termsConditions = DocumentGenerator
						.getInstance()
						.getHtmlFromVelocity(SourcingConstants.LOA_DECLARATION, context);

				getManagedBean().setTermsConditions(termsConditions);

				return termsConditions;
	}

	private void generateDeclarationOfOathFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception{
		populateAttachmentDetails(attachment);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		VelocityContext context = new VelocityContext();

		context.put("supplierName", scLoiLoa.getSupplierName());
		
		String loaNo = "&#60;LOA No.&#62;";
		if(scLoa.getLoaNo() != null){
			loaNo = scLoa.getLoaNo();
		}
		context.put("loaNo", loaNo);

		if(scLoiLoa.getSmSupplier() != null){
			SmSupplier smSupplier = scLoiLoa.getSmSupplier();
			if(smSupplier.getSmMofAccount() != null && smSupplier.getSmMofAccounts().size()>0){
				context.put("mofNo", smSupplier.getSmMofAccounts().get(0).getMofNo());
			}
			else{
				context.put("mofNo", smSupplier.getRegNo());
			}
		}

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_SURAT_AKUAN_SUMPAH_SYARIKAT, context, 
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
	 * Gets the agreement letter.
	 *
	 * @return the loa stream content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public StreamedContent getLoaStreamContent() throws IOException {
		StreamedContent loaStreamContent = null;
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
	@SuppressWarnings("unused")
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
		context.put("status", QTStatusConst.LOI_REJECTED);

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOI_NEGOTIONATION_RESULT, context, 
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
	public void handleBranchDetailsChanged(boolean isBranchChanged) {
		debugLogger("handleBranchDetailsChanged");
		ScLoa scLoa = this.getManagedBean().getScLoa();
		Long selectedSupplierBranchCode = scLoa.getSupplierBranchCode();
		String supplierBranchAsHQ = QTConstants.SUPPLIER_INFO_HQ;

		String BranchName = getSupplierBranchNameByBranchCode(selectedSupplierBranchCode);

		if (null == BranchName || supplierBranchAsHQ.equals(BranchName)) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean().getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = supplierHQPersonnel.getSmAppl().getSmCompanyBasic();

			for (SmCompanyAddress smCompanyAddress : smCompanyBasic.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(smCompanyAddress.getAddressType())) {
					log.info("found hq address");

					scLoa.setSupplierBranch(supplierBranchAsHQ);

					ScAddress supplierAddress = createOrUpdateScAddress(scLoa.getScSupplierAddress(),
							smCompanyAddress.getSmAddress(), supplierHQPersonnel, smCompanyBasic);
					scLoa.setScSupplierAddress(supplierAddress);

					// Get Gst info for HQ
					Long supplierId = scLoa.getScLoiLoa().getSmSupplier().getSupplierId();
//					SupplierSstResDTO resDTO = getSSTInfo(supplierId, null);
					SupplierGstResDTO resDTO = getGSTInfo(supplierId, null);
					
					if(isBranchChanged){
//						setSSTInfo(resDTO, scLoa);
						setGSTInfo(resDTO, scLoa);
					}

					break;
				}
			}
		} else {
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean().getBranchsList();

			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchCode().equals(selectedSupplierBranchCode)) {
					log.info("found branch address");

					scLoa.setSupplierBranch(supplierBranch.getBranchName());

					SmCompanyBasic smCompanyBasic = null;
					SmPersonnel smPersonnel = supplierBranch.getSmPersonnel();
					if (smPersonnel != null) {
						if (smPersonnel.getSmAppl() != null) {
							smCompanyBasic = smPersonnel.getSmAppl()
									.getSmCompanyBasic();
						}
					}					
					ScAddress supplierAddress = createOrUpdateScAddress(scLoa.getScSupplierAddress(), 
							supplierBranch.getSmAddress(), smPersonnel, smCompanyBasic);
					scLoa.setScSupplierAddress(supplierAddress);

					// Get Gst info based on branch
					Long supplierId = scLoa.getScLoiLoa().getSmSupplier().getSupplierId();
//					SupplierSstResDTO resDTO = getSSTInfo(supplierId,supplierBranch.getBranchCode());
					SupplierGstResDTO resDTO = getGSTInfo(supplierId,supplierBranch.getBranchCode());
					
					if(isBranchChanged){
//						setSSTInfo(resDTO,scLoa);
						setGSTInfo(resDTO,scLoa);
					}
					
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

		ScLoa scLoa = this.getManagedBean().getScLoa();
		//Bug #33674@Syazani -- Start 08/09/2022
		Long supplierBankId = this.getManagedBean().getSupplierBankId();

		// if (scLoa.getBankAccNo() != null) {
		if (supplierBankId != null) {
			List<SmSupplierBank> bankList = this.getManagedBean().getBankList();

			for (SmSupplierBank supplierBank : bankList) {
				// if (supplierBank.getAccountNo().equals(scLoa.getBankAccNo())) {
				if (supplierBank.getSupplierBankId().equals(supplierBankId)) {
					// Bug #33674@Syazani -- End 08/09/2022
					log.info("found supplier bank");

					scLoa.setBankAccNo(supplierBank.getAccountNo());
					scLoa.setBankName(supplierBank.getPmFinancialOrg()
							.getFinOrgName());
					scLoa.setBankBranch(supplierBank.getBankBranch());
					scLoa.setScBankAddress(createOrUpdateScAddress(
							scLoa.getScBankAddress(), 
							supplierBank.getSmAddress(), null, null));				
					break;
				}
			}
		}
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
		@SuppressWarnings("unused")
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
	public void saveAction(boolean supplier) {
		debugLogger("saveAction");

		try {
			Long contractType = getManagedBean().getScLoa().getContractType().getParameterId();

			if (this.getManagedBean().getFlowamt() == null && contractType == ParameterConst.CONTRACT_TYPE_CENTRAL_CODE) {
				throw new EpException(MessageConst.ERROR_760485);

			} else {

				String outcome = BpmOutcomeConst.BPM_SAVE;
				ScLoa scLoa = this.getManagedBean().getScLoa();
				// #26475, #26474 -start-
				Long statusId = null;
				PmUser pmUserNull = null;
				// #26475, #26474 -end-
				boolean isNewLoa = false; // Bug #33188
				
				//#29007 Start Vinod
				boolean specificationAndSupplierDialogFlagQT=this.getManagedBean().isSpecificationAndSupplierDialogFlagQT();
				boolean supPropSpecUpdate=this.getManagedBean().isSupPropSpecUpdate();
				//#29007 End Vinod

				// #19336 -start-
				boolean validateWitness = this.getManagedBean().isValidatewitness();
				// #19336 -end-

				if ((scLoa != null) && (this.getManagedBean().getFlowamt() != null)) {
					scLoa.setFollowLoaAmt(this.getManagedBean().getFlowamt());
				}
				ScLoaWitness loaSupplierWitness = this.getManagedBean().getLoaSupplierWitness();

				// #29173 - CR 26475: Static Code Analysis
				// start
				// #29173 - removed longValue() from getStatusId
				// #29173 - added if statement to check if null
				if (scLoa != null) {
					statusId = scLoa.getPmStatus().getStatusId();
				}
				// #29173 - CR 26475: Static Code Analysis -end-

				if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
					validateSupplierWitnessDetails(scLoa, loaSupplierWitness, outcome);
					// #29006 - start - Bug #33903 start
					if (CollectionUtils.isNotEmpty(scLoa.getScLoaRemarks())) {
						ScLoaRemark removeRemark = new ScLoaRemark();
						for (ScLoaRemark scLoaRemark : scLoa.getScLoaRemarks()) {
							// If scLoaRemark.getRemark() is null or empty
							// String
							if (StringUtils.isEmpty(scLoaRemark.getRemark())) {
								// Set SPACE String for each in
								// scLoa.getScLoaRemarks() so that
								// system will not encounter
								// "cannot insert null into" error
								// for ScLoaRemark.remark property
								// Set space when remark Id is not null only
								if (null == scLoaRemark.getRemarkId()) {
									removeRemark = scLoaRemark;
								} else {
									scLoaRemark.setRemark(" ");
								}
							}
						}
						scLoa.getScLoaRemarks().remove(removeRemark);
					}
					// #29006 - end - Bug #33903 end
				}
				parseLOADetails(scLoa, outcome);
				validateLOADetails(scLoa, outcome);

				// start - #26475 & #26474 - disable validation on save action
				scLoa.setContractAdmin(pmUserNull);
				// end - #26475 & #26474

				if (!supplier) {
					updateContractDetails(scLoa);
				}
				// Bug #33188 start
				if (scLoa.isBrandNew()) {
					isNewLoa = true;
				}
				// Bug #33188 end

				AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
				reqDTO.setPayLoad(getManagedBean().getPayLoad());
				reqDTO.setScLoa(scLoa);
				reqDTO.setOutcome(outcome);
				reqDTO.setRoleCode(getUserRoleCode(scLoa.getScLoiLoa().getDocType()));
				reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				reqDTO.setUserLocale(new FacesScopes().getUserLocale());
				reqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveFreqSchedules());
				reqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgressivePayments());
				AwardSupplierResDTO resDTO = getQtAwardSO().saveLOADtl(reqDTO);

				if (resDTO.isSuccessful()) {
					scLoa = resDTO.getScLoa();
					if (QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId)) {
						// rename temporary Loa No folder name in docRepo
						RenameTempLoaNoRepoDirByLoaNo(scLoa);
					}

					if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
						retrieveSupplierLOADtl(getManagedBean().getSourceId(), scLoa.getLoaId(), null, false);
					} else {
						editLOAAction(scLoa);
					}
					
					//#29007 Start Vinod
					this.getManagedBean().setSupPropSpecUpdate(supPropSpecUpdate);
					this.getManagedBean().setSpecificationAndSupplierDialogFlagQT(specificationAndSupplierDialogFlagQT);
					if(getManagedBean().getScLoa().getScLoaReports() != null){
						List<ScLoaReport> loaReports = getManagedBean().getScLoa().getScLoaReports();
						for(ScLoaReport scLoaReport : loaReports){
							if(scLoaReport.getTitle().contains(lampiranA3SubStr)){
								scLoaReport.setUploadlampiranA3(true);
							}
						}
					}
					//#29007 End Vinod

					// #19336 -start-
					this.getManagedBean().setValidatewitness(validateWitness);
					// #19336 -end-

					// Bug #33188 start
					if (isNewLoa) {
						renewSesionObject();
						successMessage(MessageConst.INFO_7000044, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, scLoa.getLoaNo(),
								Boolean.TRUE);
					} else {
						successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, scLoa.getLoaNo(),
								Boolean.FALSE);
					}
					// Bug #33188 end
				}
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
			
			Long contractType = getManagedBean().getScLoa().getContractType().getParameterId();
			
			if(this.getManagedBean().getFlowamt()==null && contractType == ParameterConst.CONTRACT_TYPE_CENTRAL_CODE)
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
	
	//Enhancement #12177
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
			String outcome = BpmOutcomeConst.BPM_CANCEL;
			ScLoa scLoa = this.getManagedBean().getScLoa();		
			scLoa.setScLoaRemarks(null); //To skip adding remark
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setScLoa(scLoa);
			reqDTO.setOutcome(outcome);
			reqDTO.setRoleCode(getUserRoleCode(scLoa.getScLoiLoa().getDocType()));
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			reqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveFreqSchedules());
			reqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgressivePayments());
			reqDTO.setCancelPrepareLoaActionMethod(true);//#22211
			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOADtl(reqDTO);
			
			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();			
				RenameTempLoaNoRepoDirByLoaNo(scLoa);
				renewSesionObject();
				successMessage(MessageConst.INFO_700031, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
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
			ScLoaWitness loaSupplierWitness = this.getManagedBean().getLoaSupplierWitness();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();

			if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
					.equals(statusId)) {
				validateSupplierWitnessDetails(scLoa, loaSupplierWitness, outcome);
			}
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
				parseReports(scLoa);
			} else{
				// gpki for approver
				if (null != digiSign) {
					updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
							scLoa.getLoaId(), scLoa.getLoaNo());
				}
			}

			// #11134 remove empty remark - start
			List<ScLoaRemark> scLoaRemarks = scLoa.getScLoaRemarks();
			List<ScLoaRemark> finalScLoaRemarks = new ArrayList<>();
			
			if (CollectionUtils.isNotEmpty(scLoaRemarks)) {
				for (ScLoaRemark each : scLoaRemarks) {
					if (!StringUtils.isEmpty(each.getRemark())) {
						finalScLoaRemarks.add(each);
					}
				}
			}
			
			scLoa.setScLoaRemarks(finalScLoaRemarks);
			// #11134 remove empty remark - end

			validateLOADetails(scLoa, outcome);

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

			// #26475 - start
			if (this.getManagedBean().getSelectedPtj() != null) {
				scLoa.setContractAgency(this.getManagedBean().getSelectedPtj());
			}
			// #26475 - end

			reqDTO.setScLoa(scLoa);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setDigiSignData(digiSign);
			reqDTO.setRoleCode(getUserRoleCode(scLoa.getScLoiLoa().getDocType()));
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			//22211 - start
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
			//22211 - end
			reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			reqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveFreqSchedules());
			reqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgressivePayments());
			resDTO = getQtAwardSO().saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				String messageCode = null;
				if (QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId) || 
						QTStatusConst.LOA_PENDING_REVISION_APPROVAL.equals(statusId)) {

					if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700007;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						//12177 - Start
						if(!isSqStillValid()){
							messageCode = MessageConst.INFO_700035;
						}else{
							messageCode = MessageConst.INFO_700008;
						}
						//12177 - End
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
						messageCode = MessageConst.INFO_700068;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					}
				} else if (QTStatusConst.LOA_PENDING_VERIFICATION
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700001;
				}

				if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					supplierSuccessMessage(messageCode,scLoa.getLoaNo(), Boolean.TRUE);

				}else{
					successMessage(messageCode, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_LETTER_OF_AWARD,
							scLoa.getLoaNo(), Boolean.TRUE);
				}

				renewSesionObject();
			}

		} finally {
			//qtAwardSO = null;
		}
		return resDTO;
	}

	/**
	 * Supplier Success message.
	 * 
	 * @param messageCode
	 *            the message code           
	 * @param docNo
	 *            the doc no
	 * @param isAcknowledgeRequired
	 *            the is acknowledge required
	 */
	public void supplierSuccessMessage(String messageCode, 
			String docNo, boolean isAcknowledgeRequired) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(
								messageCode,
								new String[] {
										getMessageCommon("lbl.sc.loa.supplier.response.success.info.message"),
										docNo },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

		if (isAcknowledgeRequired) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}
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
	
	//20190405@asyrafhaziq - CR25062 - Start 
	/**
	 * Approve before date bpm task action. Approve - Will show notification
	 * If yes it will go to authenticating gpki
	 * 
	 * @param outcome
	 *            the outcome
	 */
	public void approveBeforeupdateBpmTaskAction(String outcome) {
		debugLogger("approveBeforeupdateBpmTaskAction");
		this.getManagedBean().setAction(outcome);
		 		
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
				RequestContext.getCurrentInstance().execute("loaApprovalPopUp.show()");
		}else if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)){
			//22111 - START
			//REVISE DOCUMENT FROM VERIFIER TO SUPPLIER AGAIN FOR REUPLOAD DOCUMENT
			if(QTStatusConst.LOA_PENDING_VERIFICATION.equals(this.getManagedBean().getScLoa().getPmStatus()
							.getStatusId().longValue())) {
				if (this.getManagedBean().getScQt() != null){
					// Code for QT
					updateBpmTaskAction(outcome);
				}else{
					// Code for DP
					updateReviseBPMTaskActionWithoutQt(outcome);
				}
			}else{
				updateBpmTaskAction(outcome);
			}
			//22211 - END
		}else {
			updateBpmTaskAction(outcome);
		}
	}
	//20190405@asyrafhaziq - CR25062 - End
	
	// #22211 - Start
	public void updateReviseBPMTaskActionWithoutQt(String outcome) {
		debugLogger("updateReviseBPMTaskActionWithoutQt");
	
		AwardSupplierResDTO resDTO = null;
	
		ScLoa scLoa = this.getManagedBean().getScLoa();
		parseLOADetails(scLoa, outcome);
	
		validateLOADetails(scLoa, outcome);
		scLoa.setScLoaRemarks(null); //To skip adding remark
	
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
	
		if (this.getManagedBean().getSelectedPtj() != null) {
			scLoa.setContractAgency(this.getManagedBean().getSelectedPtj());
		}
		reqDTO.setScLoa(scLoa);
		reqDTO.setSourceId(getManagedBean().getSourceId());
		reqDTO.setRequestNoteNo(getQtAwardSO().retrieveRequestNoteNoByLoa(reqDTO));
		reqDTO.setPayLoad(getManagedBean().getPayLoad());
		reqDTO.setOutcome(outcome);
		reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setRoleCode(getUserRoleCode(scLoa.getScLoiLoa().getDocType()));
		reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		reqDTO.setScQt(this.getManagedBean().getScQt());
		reqDTO.setUserLocale(new FacesScopes().getUserLocale());
		reqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveFreqSchedules());
		reqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgressivePayments());
		resDTO = getQtAwardSO().reviseDocumentLOAwithoutQt(reqDTO);
	
		if (resDTO.isSuccessful()) {
			/*******************
			 * ACKNOWLEDGE_PAGE
			 *******************/
			String[] param = new String[3];
	
			param[0] = this.getManagedBean().getScLoa().getLoaNo();
			param[1] = reqDTO.getRequestNoteNo();
			param[2] = this.getManagedBean().getScLoa().getScLoiLoa().getSupplierName();
			if (reqDTO.getScLoa().getPmStatus().getStatusId()
					.equals(QTStatusConst.LOA_PENDING_ACKNOWLEDGE_RECEIPT_SUPPLIER_ACTION)) {
				successMessage(MessageConst.INFO_700085, param);
			} else {
				successMessage(MessageConst.INFO_700084, param);
			}
	
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}
	}
	// #22211 - End
	
	// #22211 - start
	public boolean isLoaReviseStatus() {
	
		Boolean loaReviseStatus = false;
		Long currentStatusId = getManagedBean().getScLoa().getPmStatus().getStatusId();
	
		if (currentStatusId.equals(QTStatusConst.LOA_REVISE_DOC)
				|| currentStatusId.equals(QTStatusConst.LOA_PENDING_ACKNOWLEDGE_RECEIPT_SUPPLIER_ACTION)) {
			loaReviseStatus = true;
		}
	
		return loaReviseStatus;
	}
	
	// #22211 - end
	
	/**
	 * Update bpm task action.
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 * Authenticating gpki before update bpm.
	 * @param outcome the outcome
	 */
	public void updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");
		this.getManagedBean().setAction(outcome);
		validateLOADetails(getManagedBean().getScLoa(), outcome);
		if(BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)){
			if(isDigiSigningRequired(RoleCode.RN_APPROVER.value()) || isDigiSigningRequired(RoleCode.PUBLICATION_APPROVER.value())) {
				this.getManagedBean().setAutoUpdate(Boolean.FALSE);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			} 
		} else if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
			//gpki for only govt seller
			if(isDigiSigningRequired(RoleCode.GOVT_SELLER.value())) {
				this.getManagedBean().setAutoUpdate(Boolean.FALSE);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

			}  else {
				// spki
				getManagedBean().getSpkiBackingBean().getChallengeQuestions();
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
		
		// #11134 - Start
		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_REJECT.equals(outcome)) {

			boolean remarksEmpty = false;

			// check if remark is empty
			List<ScLoaRemark> scLoaRemarks = scLoa.getScLoaRemarks();
			
			if (CollectionUtils.isNotEmpty(scLoaRemarks)) {
				for (ScLoaRemark each : scLoaRemarks) {
					
					if (StringUtils.isEmpty(each.getRemark())) {
						remarksEmpty = true;
						break;
					}
				}
			} else {
				remarksEmpty = true;
			}

			// throw message
			if (remarksEmpty) {
				throw new EpException(MessageConst.ERROR_716024);
				// Remark is mandatory for rejection.
			}
		}
		// #11134 -End

		if(QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId) || 
				QTStatusConst.LOA_PENDING_REVISION.equals(statusId)) {

			if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome)){
				if(scLoa.isApprovalReq()){
					if(null == getManagedBean().getApproverUser() || 
							null == getManagedBean().getApproverUser().getUserId()){
						throw new EpException(
								MessageConst.ERROR_760315,
								new String[] {ResourceBundleUtils.getMessage("lbl.sc.approver",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
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

			if(null != scLoa.getGovtSignDate()){
				if(scLoa.getContractEffDate().before(scLoa.getGovtSignDate())) {
					throw new EpException(
							MessageConst.ERROR_760468);
				}
			}
		}
		// validation for fulfillment schedule and progressivepayment
		if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome) || BpmOutcomeConst.BPM_SAVE.equals(outcome)) {
			// check whether loa from s1 or s2.
			if(scLoa.getScLoiLoa() != null){
				if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
					if (getManagedBean().getScQt() != null) {
						//Bug #24061
						if(ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode())){
							// For s2
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
		
		if(BpmOutcomeConst.BPM_ACCEPT.equals(outcome) || BpmOutcomeConst.BPM_SUBMIT.equals(outcome)) {
			if(CollectionUtils.isNotEmpty(scLoa.getScLoaReports())) {
				for(ScLoaReport loaReport : scLoa.getScLoaReports()) {
					if(loaReport.getDefinationType().getParameterCode().equals(
							ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD) &&
							loaReport.getScLoaReportAttachment() == null) {
						throw new EpException(
								MessageConst.ERROR_760441, new String[] {loaReport.getTitle()});
					}
					if(statusId.equals(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT) && 
							loaReport.getDefinationType().getParameterCode().equals(
									ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD) &&
									null == loaReport.getScLoaReportAttachment() && 
									!loaReport.getTitle().contains(lampiranA3SubStr)){//#29007 Vinod added lampiranA3SubStr
						throw new EpException(MessageConst.ERROR_760441,
								new String[] {loaReport.getTitle()});
					}
				}
			}
		}
		
		//22211 - start
		if(BpmOutcomeConst.BPM_RESUBMIT.equals(outcome) && CollectionUtils.isNotEmpty(scLoa.getScLoaReports())){
			if(CollectionUtils.isNotEmpty(scLoa.getScLoaReports())) {
				for(ScLoaReport loaReport : scLoa.getScLoaReports()) {
					if(loaReport.getDefinationType().getParameterCode().equals(
							ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD) &&
							loaReport.getScLoaReportAttachment() == null) {
						throw new EpException(
								MessageConst.ERROR_760441, new String[] {loaReport.getTitle()});
					}
					if(statusId.equals(QTStatusConst.LOA_PENDING_ACKNOWLEDGE_RECEIPT_SUPPLIER_ACTION) && 
							loaReport.getDefinationType().getParameterCode().equals(
									ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD) &&
									null == loaReport.getScLoaReportAttachment()){
						throw new EpException(MessageConst.ERROR_760441,
								new String[] {loaReport.getTitle()});
					}
				}
			}
		}
		//22211 - end
		
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
				saveScheduleList();
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
			if(scLoa.getContractType() !=null){
				reqDTO.setParameterId(scLoa.getContractType().getParameterId());
	
				ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
				scLoa.setContractType(resDTO.getPmParameter());
			}
			
			// to reset Contract Admin User
			if(getManagedBean().getContractTypeId() != null) {
				if (!scLoa.getContractType().getParameterId().equals(getManagedBean().getContractTypeId())){
					scLoa.setContractAdmin(new PmUser());
				}
				getManagedBean().setContractTypeId(scLoa.getContractType().getParameterId());
			} else {
				scLoa.setContractAdmin(new PmUser());
			}

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
			Long userId = null;

			if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
				// for s2
				userId = getActorUserIdByQtIdAndRoleCode(scLoa.getScLoiLoa().getDocId());
			} else{
				// for s1
				userId = new FacesScopes().getUserSessionBean().getUserId();
			}

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
				} catch(EpException e) {
					//
					log.info("Org validity is null for supplier user");
				}
				finally {
					userSO = null;
				}
			}			
		}

		contractAgencyChanged();
	}

	private Long getActorUserIdByQtIdAndRoleCode(Long qtId) {
		Long userId = null;
		IScActorSO actorSO = null;
		try {
			actorSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScActorSO.class);

			ScActorReqDTO reqDTO = new ScActorReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			reqDTO.setDocId(qtId);
			reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());

			ScActor scActor = actorSO.retrieveActorByTypeIdAndRoleCode(reqDTO)
					.getScActor();

			if (scActor != null) {
				userId = scActor.getUserId();	
			}

			return userId;

		} finally {
			actorSO = null;
		}
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
			//#30474 - start
			if(this.getManagedBean().getScLoa()
					.getScQtSchedules()==null){
				this.getManagedBean().getScLoa().setScQtSchedules(new ArrayList<ScQtSchedule>());
			}
			//#30474 - end
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
	private String getMonth(String month){
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
				//Date currentDate = new Date();
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
				//String localDate = df.format(currentDate);
				//String day = localDate.substring(0, 2);
				String fromdate = 01 + "/" + schedule.getStartMonth() + "/"
						+ schedule.getStartYear();
				String toDate = 01 + "/" + schedule.getEndMonth() + "/"
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
	 * Mapping jadual harga.
	 * @param item the item
	 * @return the item bean
	 */
	private ItemBean mappingJadualHarga(ScLoiLoaItem item) {
		ItemBean itemBean = new ItemBean();

		// is zonal
		if(item.getTplSpecZoneDtl() != null){
			itemBean.setZonal(Boolean.TRUE);
		}

		itemBean.setZoneName(item.getTplSpecZoneDtl() != null ? item
				.getTplSpecZoneDtl().getZoneName() : SSTConstant.EMPTY_STRING);
		//20200720@asyrafhaziq  - #28609 - Start
		String itemName = item.getScRequestItem().getItemName();
		if (itemName == null) {
			itemName = "";
		  }
		itemName = itemName.trim();
		//replace all special characters
		itemName = itemName.replaceAll("<", "&#60;");
		itemName = itemName.replaceAll(">", "&#62;");
		itemName = itemName.replaceAll("\u2264", "&#60;=");//symbol less then equal
		itemName = itemName.replaceAll("\u2265", "&#62;=");//symbol greater then equal
		itemName = itemName.replaceAll("\r\n", "<br/>");		
		itemBean.setItem(itemName);
		//20200720@asyrafhaziq  - #28609 - End
		
		if(item.getTplSpecTierQty() != null){
			itemBean.setTier(Boolean.TRUE);
			itemBean.setFromQty(null == item.getTplSpecTierQty().getFromQty()? "" 
					: item.getTplSpecTierQty().getFromQty().toString());
			itemBean.setToQty(null == item.getTplSpecTierQty().getToQty()? "" 
					: item.getTplSpecTierQty().getToQty().toString());
		}
		//26226 Start
		TplSpecQuestion tplSpecQuestion=item.getScRequestItem().getTplSpecQuestion();
		itemBean.setItem(item.getScRequestItem().getItemName());
		itemBean.setZoneName(item.getTplSpecZoneDtl() != null ? item.getTplSpecZoneDtl().getZoneName() : SSTConstant.EMPTY_STRING);
				
		if (null != tplSpecQuestion.getClDeliveryTerm()) {
			itemBean.setClDeliveryTerm(tplSpecQuestion.getClDeliveryTerm().toString());
		}
		if (null != tplSpecQuestion.getClDurationMonth()) {
			itemBean.setClDurationMonth(tplSpecQuestion.getClDurationMonth().toString());
		}
		//End
		/*BigDecimal unitPrice = item.getUnitPrice().setScale(2, BigDecimal.ROUND_HALF_EVEN);
		jadualHarga.setUnitPrice(unitPrice.toString());
		jadualHarga.setFreqUom(null == item.getScRequestItem().getTplSpecQuestion()
				.getClUom() ? "" : item.getScRequestItem().getTplSpecQuestion()
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
				.toString());*/
		itemBean.setQuantity(null == item.getQty()? "" : item.getQty()
				.toString());

		/*BigDecimal  totalAmount = BigDecimal.valueOf(0d);
		BigDecimal subTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(item.getScRequestItem().getTplSpecQuestion(), 
				item.getUnitPrice(), item.getQty());
		totalAmount = totalAmount.add(subTotal);
		totalAmount =  totalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		jadualHarga.setTotalAmount(totalAmount.toString());*/

		return itemBean;
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
			InputStream inputStream = null;
			try {
				/*FileInputStream inputStream = new FileInputStream(new File(
						FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/WEB-INF/templates")
						+ "/sc/loa/jadual-harga.xlsx"));*/

				final String templatePath = "templates" + SourcingConstants.DOCPATH_SC_LOA_JADUAL_HARGA;
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
			if(inputStream != null) {
				inputStream.close();
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
	 * Creates the QT Loa.
	 * page navigate to qt loi details page.
	 * @param scLoiLoa the sc loi loa
	 * @return the string
	 */
	public String createQtLOAAction(ScLoiLoa scLoiLoa) {
		debugLogger("createQtLOAAction");
		ScLoa scLoa = new ScLoa();
		scLoa.setScLoiLoa(scLoiLoa);
		ScLoaWorkFlowStatus currentStatus = new ScLoaWorkFlowStatus();
		currentStatus.setScLoa(scLoa);
		currentStatus.setPmStatus(getPmStatusByStatusId(QTStatusConst.LOA_PENDING_SUBMISSION));
		currentStatus.setCurrent(true);
		scLoa.setCurrentStatus(currentStatus);
		scLoa.setPmStatus(currentStatus.getPmStatus());
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
				ScLoa scLoa = resDTO.getScLoa();
				if (scLoa != null) {
					if(QTStatusConst.LOA_PENDING_VERIFICATION.equals(scLoa.getPmStatus().getStatusId())){
						getManagedBean().setSelectedTabIndex(1);
					}
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
					totalAmount = totalAmount.add(subTotal).setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024
				}
			}
		}
		totalAmount =  totalAmount.setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024
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
				if(o1.getTplSpecTierQty() != null && o2.getTplSpecTierQty() != null){
					return new CompareToBuilder().append(o1.getTplSpecTierQty().getFromQty(),
							o2.getTplSpecTierQty().getFromQty()).toComparison();
				}
				return 0;
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
		if(getManagedBean().getScQt() != null && getManagedBean().getSourceId() != null){
			retrieveLOILOAList(getManagedBean().getSourceId());	
		} else{
			// back to dp loa list
			if(getManagedBean().getSourceId() != null){
				DpLOABackingBean dpLOABackingBean = new DpLOABackingBean();
				dpLOABackingBean.retrieveLOAList(getManagedBean().getSourceId());
			}
		}

		getManagedBean().setEditFlag(Boolean.FALSE);
		getManagedBean().setReadOnly(Boolean.FALSE);
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
				
		//Bug #29885 start
		if(loa.isBrandNew() || isBondReq){ 
			loa.setBondReq(isBondReq);
		}
		//Bug #29885 end
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
	 * Gets the request item so.
	 *
	 * @return the request item so
	 */
	private IRequestItemSO getRequestItemSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestItemSO.class);
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
	 * Validate deduction payment dtls.
	 *
	 * @param scLoa the sc loa
	 */
	private void validateDeductionPaymentDtls(ScLoa scLoa) {
		validateDeductionPaymentDtls(scLoa.getScQtDeductions());
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

	//Enhancement #24259 #24269
//	/**
//	 * Sum of gstAmount & loa offer price
//	 * 
//	 * @param gstAmount
//	 */
//	public void calculateOverallAmount(BigDecimal gstAmount) {
//		ScLoa scLoa = getManagedBean().getScLoa();
//		getManagedBean().setOverallContractAmt(
//				overallCtAmount(scLoa.getLoaAmt(), gstAmount));
//
//		scLoa.setOverallContractAmt(getManagedBean().getOverallContractAmt());
//
//		if (null != getManagedBean().getContractSignerUser()
//				&& getManagedBean().getContractSignerUser().getUserId() != null) {
//
//			scLoa.setLoaSignerDesig(null);
//			scLoa.setContractSigner(null);
//			getManagedBean().setContractSignerUser(null);
//			prepareSignerInfo();
//			RequestContext.getCurrentInstance().execute(
//					"gstSignerConfirmPopupWin.show()");
//		} else {
//			prepareSignerInfo();
//		}
//
//		checkAgreementMandatory();
//
//		checkPerformanceBondMandatory();
//
//		calcBondAmount(scLoa, Boolean.FALSE);
//		// bond amount in words
//		if (scLoa.getBondAmt() != null) {
//			getManagedBean().setBondAmountWord(
//					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
//		}
//	}
	
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

//	/**
//	 * Checks if is GST reg active.
//	 *
//	 * @return true, if is GST reg active
//	 */
//	public boolean isGSTRegActive() {
//		boolean isActive = Boolean.FALSE;
//		ScLoa scLoa = getManagedBean().getScLoa();
//		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
//		getManagedBean().setRenderRemainder(false);
//		if ((supplier != null && supplier.getGstRegisteredFlag() == 
//				DbValueConst.RECORD_STATUS_ACTIVE)) {
//			if (null != scLoa.getGstEffDate()){
//				if(scLoa.getGstEffDate().compareTo(new Date()) <= 0){
//					isActive = Boolean.TRUE;
//				}else {
//					getManagedBean().setRenderRemainder(isActive);
//				}
//			}
//		}
//		return isActive;
//	}
	
//	/**
//	 * Checks if is SST reg active.
//	 *
//	 * @return true, if is SST reg active
//	 */
//	public boolean isSSTRegActive() { 
//		boolean isActive = Boolean.FALSE;
//		
//		ScLoa scLoa = getManagedBean().getScLoa();
//		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
//			
//		if ((supplier != null && supplier.getSstRegisteredFlag() == DbValueConst.RECORD_STATUS_ACTIVE)) {		
//			if (null != scLoa.getSstEffDate()) {		
//				if(scLoa.getSstEffDate().compareTo(new Date()) <= 0){
//					isActive = Boolean.TRUE;
//				}
//			}
//		}
//		
//		return isActive;
//	}

	private void calculateOverallCtAmtForSupplier(){
		ScLoa scLoa = getManagedBean().getScLoa();
		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
		BigDecimal overallContractAmt = scLoa.getLoaAmt();
//		if ((supplier != null && supplier.getGstRegisteredFlag() == DbValueConst.RECORD_STATUS_ACTIVE)
//				&& null != scLoa.getGstAmt()) {
//			overallContractAmt = overallContractAmt.add(scLoa.getGstAmt());
//		} else if ((supplier != null && supplier.getGstRegisteredFlag() != DbValueConst.RECORD_STATUS_ACTIVE)) {
//		if ((supplier != null && supplier.getSstRegisteredFlag() == DbValueConst.RECORD_STATUS_ACTIVE)
//				&& null != scLoa.getSstAmt()) {
		if (scLoa.getSstAmt() != null) {
			overallContractAmt = overallContractAmt.add(scLoa.getSstAmt());
//		} else if ((supplier != null && supplier.getSstRegisteredFlag() != DbValueConst.RECORD_STATUS_ACTIVE)) {
		} else {
			BigDecimal durationMnth = null;
			BigDecimal noOfMnth = new BigDecimal(12);
			
			for (ScLoiLoaItem loiLoaItem : scLoa.getScLoiLoaItems()) {
				TplSpecQuestion specQuestion = loiLoaItem.getScRequestItem().getTplSpecQuestion();
				
				if (specQuestion != null) {
					TplSpecMaster specMaster = specQuestion.getTplSpecMaster();
					
					if (specMaster.getItemTypeCode().getParameterCode()
							.compareTo(ParameterConst.ITEM_TYPE_SERVICE) == 0) {
						durationMnth = specQuestion.getTplSpecMaster().getDurationMonth();
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
	//end Enhancement #24259 #24269
	
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
				//Bug #32188 - start
				else if(ParameterConst.DOCTYPE_DIRECT_NEGO
						.equalsIgnoreCase(scLoa.getScLoiLoa().getDocType())) {
					docCategory = ParameterConst.DOCUMENT_CATEGORY_DIRECT_LOA_FOR_QUOTATION_TENDER; // DL
					scLoaReport.setPmDocument(getDocumentDtls(docCategory));
				}else if(ParameterConst.DOCTYPE_QUOTATION
						.equalsIgnoreCase(scLoa.getScLoiLoa().getDocType())) {
					scLoaReport.setPmDocument(getDocumentDtls(docCategory)); // AQ
				}
				//Bug #32188 - end
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
	
	//Enhancement #24259 #24269	
//	private void setGstInfoForAgreementLetter(VelocityContext context, ScLoa scLoa,
//			DecimalFormat amountDF, Locale myLocale) {
//
//		String gstRegNo = scLoa.getGstRegNo() != null ? scLoa.getGstRegNo(): "______________";
//		context.put("gstRegNo", gstRegNo);
//
//		String gstEffectiveDate = scLoa.getGstEffDate() != null ? DateUtils
//				.getLocalizeDate(scLoa.getGstEffDate(), myLocale) : "______________";
//				context.put("gstEffectiveDate", gstEffectiveDate);
//
//				String gstDeclaredDate = scLoa.getGstDeclaredDate() != null ? gstEffectiveDate = DateUtils
//						.getLocalizeDate(scLoa.getGstDeclaredDate(), myLocale) : "______________";
//						context.put("gstDeclaredDate", gstDeclaredDate);
//
//						String offerPrice = "______________";
//						String offerPriceWording = "______________";
//						BigDecimal loaAmount = scLoa.getLoaAmt();
//						if (loaAmount != null) {
//							offerPrice = amountDF.format(loaAmount);
//							offerPriceWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
//						}
//						context.put("offerPrice", offerPrice);
//						context.put("offerPriceWording", offerPriceWording);
//
//						String gstAmount = MALAY_NA;
//						String gstAmountWording = MALAY_NA;
//						String overallctAmt = "______________";
//						String overallctAmtWording = "______________";
//						BigDecimal gstAmt = scLoa.getGstAmt();
//						if (gstAmt != null) {
//							gstAmount = amountDF.format(gstAmt);
//							gstAmountWording = WordUtils.capitalizeFully(NumberWordUtil.convert(gstAmt
//									.toString()));
//							overallctAmt = amountDF.format(loaAmount.add(gstAmt));
//							overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.add(gstAmt).toString()));
//						} else {
//							overallctAmt = amountDF.format(loaAmount);
//							overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
//						}
//						context.put("gstAmt", gstAmount);
//						context.put("gstAmtWording", gstAmountWording);
//						context.put("overallCtAmt", overallctAmt);
//						context.put("overallCtAmtWording", overallctAmtWording);
//
//	}	
	
	private void setSstInfoForAgreementLetter(VelocityContext context, ScLoa scLoa,	DecimalFormat amountDF, Locale myLocale) {

		String gstRegNo = scLoa.getGstRegNo() != null ? scLoa.getGstRegNo(): "______________";
		context.put("gstRegNo", gstRegNo);

		String gstEffectiveDate = scLoa.getGstEffDate() != null ? DateUtils
				.getLocalizeDate(scLoa.getGstEffDate(), myLocale) : "______________";
		context.put("gstEffectiveDate", gstEffectiveDate);

		String gstDeclaredDate = scLoa.getGstDeclaredDate() != null ? gstEffectiveDate = DateUtils
				.getLocalizeDate(scLoa.getGstDeclaredDate(), myLocale) : "______________";
		context.put("gstDeclaredDate", gstDeclaredDate);

		String offerPrice = "______________";
		String offerPriceWording = "______________";
		BigDecimal loaAmount = scLoa.getLoaAmt();
				
		if (loaAmount != null) {
			offerPrice = amountDF.format(loaAmount);
			offerPriceWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
		}
		
		context.put("offerPrice", offerPrice);
		context.put("offerPriceWording", offerPriceWording);
	
		String sstAmount = MALAY_NA;
		String overallctAmt = "______________";
		String overallctAmtWording = "______________";
		BigDecimal sstAmt = scLoa.getSstAmt();		
		
		if (sstAmt != null) {
			sstAmount = amountDF.format(sstAmt);
			overallctAmt = amountDF.format(loaAmount.add(sstAmt));
			log.info(":::setSstInfoForAgreementLetter(). loaId: "+ scLoa.getLoaId() +". overallctAmt inc. sstAmt: "+ overallctAmt);
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.add(sstAmt).toString()));						
		} else {
			overallctAmt = amountDF.format(loaAmount);
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
		}
		
		context.put("sstAmt", sstAmount);
		context.put("overallCtAmt", overallctAmt);
		context.put("overallCtAmtWording", overallctAmtWording);
				
	}	
	//end Enhancement #24259 #24269	

	/**
	 * @param context
	 * @param scLoa
	 * @param myLocale
	 */
	private void setMofInfoForAgreementLetter(VelocityContext context,
			ScLoa scLoa, ScQt scQt, Locale myLocale) {

		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		SmSupplier smSupplier = scLoiLoa.getSmSupplier();
		Long supplierId = smSupplier.getSupplierId();

		SmMofAccount mofAccount = getMofAccountBySupplierId(supplierId);

		//String mofNo = MALAY_NA;
		String mofValidityDuration = MALAY_NA;
		String mofCertValidityDuration = MALAY_NA;
		String isBumi = MALAY_NA;
		String kodBidang = MALAY_NA;

		if(mofAccount != null) {
			//mofNo = mofAccount.getMofNo();
			String mofEffectiveDate = null;
			String mofExpiredDate = null;
			if(mofAccount.getEffDate() != null) {
				mofEffectiveDate = DateUtils
						.getLocalizeDate(mofAccount.getEffDate(), myLocale);
			}
			if(mofAccount.getExpDate() != null) {
				mofExpiredDate = DateUtils
						.getLocalizeDate(mofAccount.getExpDate(), myLocale);
			}

			if(mofEffectiveDate != null && mofExpiredDate != null) {
				mofValidityDuration = new StringBuilder().append(mofEffectiveDate).
						append(" - ").append(mofExpiredDate).toString();
			}

			SmMofCert mofCert = getMofCertBySupplierId(mofAccount.getMofAccountId());

			String mofCertEffectiveDate = null;
			String mofCertExpiredDate = null;
			if(mofCert != null){

				if(mofCert.getEffDate() != null) {
					mofCertEffectiveDate = DateUtils
							.getLocalizeDate(mofCert.getEffDate(), myLocale);
				}
				if(mofCert.getExpDate() != null) {
					mofCertExpiredDate = DateUtils
							.getLocalizeDate(mofCert.getExpDate(), myLocale);
				}
			}

			if(mofCertEffectiveDate != null && mofCertExpiredDate != null) {
				mofValidityDuration = new StringBuilder().append(mofCertEffectiveDate).
						append(" - ").append(mofCertExpiredDate).toString();
			}

			if(smSupplier.isBumi()) {
				isBumi = "Bumiputera";
			} else {
				isBumi = "Bukan Bumiputera";
			}

			if(scQt != null) {
				if(!CollectionUtils.isEmpty(scQt.getScQtCategorys())) {
					List<String> categoryCodes = new ArrayList<String>();
					for(ScQtCategory category : scQt.getScQtCategorys()) {
						String l1Code = category.getPmCategoryL1().getCategoryL1Code();
						String l2Code = category.getPmCategoryL2().getCategoryL2Code();
						String l3Code = category.getPmCategoryL3().getCategoryL3Code();
						String categoryCode = new StringBuilder().append(l1Code).append(l2Code).append(l3Code).toString();
						categoryCodes.add(categoryCode);
					}
					kodBidang = StringUtils.join(categoryCodes, ",");
				}
			}
		}
		context.put("mofValidityDuration", mofValidityDuration);
		context.put("mofCertValidityDuration", mofCertValidityDuration);
		context.put("isBumi", isBumi);		
		context.put("kodBidang", kodBidang);
		context.put("mofNo", scLoiLoa.getMofNo());
	}

	/**
	 * @param context
	 * @param scLoa
	 * @param myLocale
	 */
	private void setSSMInfoForAgreementLetter(VelocityContext context,
			ScLoa scLoa, ScQt scQt, Locale myLocale) {

		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		SmSupplier smSupplier = scLoiLoa.getSmSupplier();

		String regNo = MALAY_NA;

		if(StringUtils.isNotEmpty(smSupplier.getRegNo())) {
			regNo = smSupplier.getRegNo();
		}
		context.put("regNo", regNo);
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

	private SupplierGstResDTO getGSTInfo(Long supplierId, Long branchCode) {

		ISupplierGstSO supplierGstSO = null;
		try {
			supplierGstSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
			SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
			gstReqDTO.setSupplierId(supplierId);
			gstReqDTO.setBranchCode(branchCode);
			SupplierGstResDTO gstResDTO = supplierGstSO
					.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
			return gstResDTO;
		} finally {
			supplierGstSO = null;
		}
	}
	
//	private SupplierSstResDTO getSSTInfo(Long supplierId, Long branchCode) {
//		ISupplierSstSO supplierSstSO = null;
//		
//		try {
//			supplierSstSO = BusinessDelegate.getInstance().getServiceObject(
//					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSstSO.class);
//			
//			SupplierSstReqDTO sstReqDTO = new SupplierSstReqDTO();
//			sstReqDTO.setSupplierId(supplierId);
//			sstReqDTO.setBranchCode(branchCode);
//			
//			SupplierSstResDTO sstResDTO = supplierSstSO.retrieveSstInfoBySupplierIdAndBranchCode(sstReqDTO);
//			
//			return sstResDTO;
//		} finally {
//			supplierSstSO = null;
//		}
//	}

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

		//Enhancement #24259 #24269	
//		if(scLoa.getGstAmt() != null){
		if(scLoa.getSstAmt() != null) {
			BigDecimal ctAmount = getManagedBean().getOverallContractAmt();
			
			if ((ctAmount != null && scLoa.getLoaAmt() != null)	&& !scLoa.getLoaAmt().equals(ctAmount)) {
//				ctAmount = ctAmount.subtract(scLoa.getGstAmt());
				ctAmount = ctAmount.subtract(scLoa.getSstAmt());
				
				ctAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				
				this.getManagedBean().setOverallContractAmt(ctAmount);
			}
		}
		
		scLoa.setSstAmt(null);
//		scLoa.setGstAmt(null);
		//end Enhancement #24259 #24269	
	}
	
//	private void setSSTInfo(SupplierSstResDTO resDTO, ScLoa scLoa) {
//		if(scLoa.getSstTypeId() != null && 
//				scLoa.getSstTypeId().getParameterCode().equals(ParameterConst.SST_SALES_TAX)) {
//			
//			if(resDTO.isSuccessful() && resDTO.getSmSstSupplier() != null) {				
//				SmSstSupplier sstSupplier = resDTO.getSmSstSupplier();
//				
//				scLoa.setSstRegNo(sstSupplier.getSalestaxregno());
//				scLoa.setSstDeclaredDate(sstSupplier.getSalestaxregdecDate());
//				scLoa.setSstEffDate(sstSupplier.getSalestaxregDate());
//			} else {
//				scLoa.setSstRegNo(null);
//				scLoa.setSstDeclaredDate(null);
//				scLoa.setSstEffDate(null);
//			}
//		
//		} else { //ParameterConst.SST_SERVICE_TAX
//			
//			if(resDTO.isSuccessful() && resDTO.getSmSstSupplier() != null) {				
//				SmSstSupplier sstSupplier = resDTO.getSmSstSupplier();
//				
//				scLoa.setSstRegNo(sstSupplier.getServtaxregno());
//				scLoa.setSstDeclaredDate(sstSupplier.getServtaxdecDate());
//				scLoa.setSstEffDate(sstSupplier.getServtaxregDate());
//			} else {
//				scLoa.setSstRegNo(null);
//				scLoa.setSstDeclaredDate(null);
//				scLoa.setSstEffDate(null);
//			}		
//		}
//		
//		if(scLoa.getSstAmt() != null) {
//			BigDecimal ctAmount = getManagedBean().getOverallContractAmt();
//			
//			if ((ctAmount != null && scLoa.getLoaAmt() != null) && !scLoa.getLoaAmt().equals(ctAmount)) {
//				ctAmount = ctAmount.subtract(scLoa.getSstAmt());
//				
//				ctAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
//				
//				this.getManagedBean().setOverallContractAmt(ctAmount);
//			}
//		}
//		
//		scLoa.setSstAmt(null);
//	}

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
			userReqDTO.setApprovalLimit(overallCtAmountWithSST(scLoa.getLoaAmt(), scLoa.getSstAmt()));
//			userReqDTO.setApprovalLimit(overallCtAmount(scLoa.getLoaAmt(),
//					scLoa.getGstAmt()));
		}
		userReqDTO.setOrgProfileId(scLoa.getScLoiLoa().getPmOrgProfile().getOrgProfileId());
		
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
		getManagedBean().setContractSignerList(userResDTO.getUserList());
		
		getManagedBean().setContractSignerUser(contractSignerUser);
	}

	/**
	 * Prepare Supplier Contract signer info.
	 */
	private void prepareSupplierSignerInfo() {

		ScLoa scLoa = getManagedBean().getScLoa();

		// Supplier Contract Signer
		PmUser supplierContractSignerUser = scLoa.getAckBy();
		if (supplierContractSignerUser == null) {
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
		}

		ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
		supplierReqDTO.setSupplierId(scLoa.getScLoiLoa().getSmSupplier().getSupplierId());
		supplierReqDTO.setAuthorized(true);
		List<SmPersonnel> personnels = supplierSearchSO.retrieveContractSigners(supplierReqDTO).getPersonnels();
		List<PmUser> supplierSigners = new ArrayList<PmUser>();
		for(SmPersonnel personnel : personnels) {
			supplierSigners.add(personnel.getPmUser());
		}
		getManagedBean().setSupplierContractSignerList(supplierSigners);
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
	 * Proc type category changed.
	 *
	 * @param scLoa the sc loa
	 */
	public void procTypeCategoryChanged(ScLoa scLoa) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = scLoa.getProcurementTypeCatId().getParameterId();

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scLoa.setProcurementTypeCatId(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
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

	/**
	 * Download guideline pdf.
	 *
	 * @throws Exception the exception
	 */
	public void downloadGuidelinePdf() throws Exception{
		debugLogger("downloadGuidelinePdf");
		ScPbAttachment pbAttachment = new ScPbAttachment();
		//pbAttachment.setDocNo(getManagedBean().getScLoa().getLoaNo());
		pbAttachment.setFileName("Guideline.pdf");

		String docRepoSharedDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_DIR);

		StringBuilder filePath = new StringBuilder(docRepoSharedDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
		pbAttachment.setFilePath(filePath.toString());

		File theDir = new File(getActualFilePath(pbAttachment));
		theDir.mkdirs();

		AttachmentFormat scFormat = new AttachmentDefaultFormat();
		String formatedFileName = scFormat.format(SourcingConstants.DOCNAME_QT_SST_GUIDELINE,
				pbAttachment.getPrefixFileName());

		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(formatedFileName);
		pbAttachment.setFilePath(filePath.toString());

		downloadFile(pbAttachment);
	}

	/**
	 * Retrieve fresh copy of ScRequestItem before assign product code
	 * @param requestNoteDtl
	 * @return
	 */
	public String assignProductCode(ScLoiLoaItem loiLoaItem) {
		log.info(">>>>>>>>>loaDetailBackingBean assignProductCode");
		ScRequestItem requestItem = loiLoaItem.getScRequestItem();
		RequestItemReqDTO reqDTO = new RequestItemReqDTO();
		reqDTO.setRequestItemId(requestItem.getRequestNoteItemId());
		ScRequestItem freshRequestItem = getRequestItemSO().retrieveItemByRequestItemId(reqDTO).getRequestItem();
		if(freshRequestItem != null) {
			requestItem = freshRequestItem;
		}
		loiLoaItem.setScRequestItem(requestItem);
		CodificationBackingBean codificationBB = new CodificationBackingBean();
		return codificationBB.assignProductCode(requestItem, null, false);
	}

	/**
	 * Validate ScLoaWitness and suppler contract signer before download attachment of Acknowledge LOA Receipt
	 */
	private void validateLoaWitnessAndAckBy() {
		ScLoaWitness witness = this.getManagedBean().getLoaSupplierWitness();
		PmUser ackBy = this.getManagedBean().getScLoa().getAckBy();
		if(witness == null || witness.getWitnessName().isEmpty() || witness.getIdentificationNo().isEmpty() ||
				witness.getDesignation().isEmpty() || witness.getAddress().isEmpty() || 
				witness.getPhoneNo().isEmpty() || witness.getFaxNo().isEmpty()) {
			throw new EpException(MessageConst.ERROR_760442);
		}
		if(ackBy == null || ackBy.getUserName().isEmpty()) {
			throw new EpException(MessageConst.ERROR_760442);
		}
	}

	private List<ItemBean> convertSpecQuestionToSpecAnswer(
			TplSpecMaster specMaster) {
		TreeMap<Integer, ItemBean> questionSpecMap = new TreeMap<Integer, ItemBean>(); //Bug #34882@Syazani 26/06/2023

		if (specMaster != null && specMaster.getTplSpecQuestions() != null) {
			for (TplSpecQuestion specQuestion : specMaster
					.getTplSpecQuestions()) {
				if(specQuestion.getParentSpecQuestion() == null) {
					int order = specQuestion.getQuestionOrder();
					if (!questionSpecMap.containsKey(order)) {
						ItemBean specBean = new ItemBean();
						//20200824@asyrafhaziq  - #28609 - Start
						String itemName = specQuestion.getClItem();
						if (itemName == null) {
							itemName = "";
						}
						itemName = itemName.trim();
						//replace all special characters
						itemName = itemName.replaceAll("<", "&#60;");
						itemName = itemName.replaceAll(">", "&#62;");
						itemName = itemName.replaceAll("\u2264", "&#60;=");//symbol less then equal
						itemName = itemName.replaceAll("\u2265", "&#62;=");//symbol greater then equal
						itemName = itemName.replaceAll("\r\n", "<br/>");
						specBean.setItem(itemName);
						//20200824@asyrafhaziq  - #28609 - End
						specBean.setSpecs(new ArrayList<String>());
						questionSpecMap.put(order, specBean);						} 
				} else {
					int parentOrder = specQuestion.getParentSpecQuestion().getQuestionOrder();
					questionSpecMap.get(parentOrder).getSpecs().add(specQuestion.getClSpecDetail());
				}
			}
		}

		return new ArrayList<ItemBean>(questionSpecMap.values());
	}

	private String getSupplierBranchNameByBranchCode(Long branchCode){
		List<SmSupplierBranch> supplierBranchs = this.getManagedBean()
				.getBranchsList();
		String branchName = null;
		if(supplierBranchs != null && branchCode != null){
			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchCode().equals(
						branchCode)) {
					branchName = supplierBranch.getBranchName();
				}
			}
		}
		return branchName;
	}

	public void downloadViewLoa() throws Exception {
		log.info("start downloadViewLoa");
		List<ScLoaReport> scloaReports=	this.getManagedBean().getScLoa().getScLoaReports();
		if(CollectionUtils.isNotEmpty(scloaReports)){
			ScLoaReport report=null;
			for(ScLoaReport loaReport:scloaReports){
				if(null!=loaReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER.equalsIgnoreCase(loaReport.getPmDocument().getDocDefinitionKey())){
					log.info("LoaReport Title"+loaReport.getTitle());
					//log.info("DocDefinitionType"+loaReport.getDefinationType().getParameterCode());
					//log.info("LoaReportAttachment"+loaReport.getScLoaReportAttachment());
					report = loaReport;									
					break;	
				}
			}
			if(null!=report){
				//log.info("report not null"+report.getScLoaReportAttachment().getFileName());
				downloadFile(report.getScLoaReportAttachment());
			}
		}
	}

	public String constructUserConditionFields() {
		StringBuilder sb = new StringBuilder();
		ScLoa scLoa = getManagedBean().getScLoa();
		//Bug #30698
		Long MinistryOrgProfileId = retrieveMinistryOrgProfile(scLoa.getScLoiLoa().getPmOrgProfile().getOrgProfileId()).getOrgProfileId();
		sb.append("pmUserOrgs.pmUserRoles.pmRole.roleCode^");
		sb.append(RoleCode.CONTRACT_SIGNER.value());
		sb.append("^9");
		//Bug #30698
		 if (null != MinistryOrgProfileId) {
			 sb.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			 sb.append("pmUserOrgs.pmOrgProfile.");
			 sb.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + MinistryOrgProfileId);
		} 		
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
		// #26475 - start
		List<PmUser> pmUserList = getUsersByAgency();
		PmUser pmUserNull = null;
		if (CollectionUtils.isEmpty(pmUserList)) {
			throw new EpException(MessageConst.ERROR_760512);
		} else {
			scLoa.setContractAdmin(pmUserNull);
		}
		// #26475 - end

		//Bug #30837
		BigDecimal contractDuration = scLoa.getContractDuration();
		Calendar contractExpDate = Calendar.getInstance();
		contractExpDate.setTime(scLoa.getContractEffDate());
		contractExpDate.add(Calendar.MONTH, contractDuration.intValue());
		contractExpDate.add(Calendar.DATE, -1);

		if (!scLoa.getContractExpDate().equals(contractExpDate.getTime())){
			scLoa.setContractExpDate(contractExpDate.getTime());
		}		
	}
	
	//Enhancement #21573 To have confirmation box for reject/acknowledge button at document LOI/LOA		
	/**
	 * Update bpm task action for supplier.
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 * Authenticating spki before update bpm.
	 * @param outcome the outcome
	 */
	public void updateBpmTaskActionSupplier(String outcome) {
		this.getManagedBean().setAction(outcome);
		Long statusId = this.getManagedBean().getScLoa().getPmStatus().getStatusId().longValue();

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
				.equals(statusId)) {
			validateSupplierWitnessDetails(this.getManagedBean().getScLoa(), this.getManagedBean().getLoaSupplierWitness(), outcome);
		}
		validateLOADetails(getManagedBean().getScLoa(), outcome);
		
		String loa = getMessage("lbl.sc.loiloa.loa.header.title", null);
		String procurementMode = null; 
		String mode = this.getManagedBean().getScLoa().getProcurementMode().getParameterCode();
		
		if (mode.equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			procurementMode = getMessage("lbl.sc.sq", null);
		} else if (mode.equals(ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
			procurementMode = getMessage("lbl.rpt.sc.quotation", null);
		} else if (mode.equals(ParameterConst.PROCUREMENT_MODE_TENDER)) {
			procurementMode = getMessage("lbl.rpt.sc.tender", null);
		}
		
		if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {			
			this.getManagedBean().setAutoUpdate(false);		
			
			String acceptMsg = getMessage("lbl.sc.loa.supplier.confirm.button.accept", new String[] { loa.trim(), procurementMode.trim() });
			
			this.getManagedBean().setSuppConfirmMsgAccept(acceptMsg.toString().trim());
			RequestContext.getCurrentInstance().execute("confmAcceptDlg.show()"); 
		} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {			
			this.getManagedBean().setAutoUpdate(false);
			
			String rejectMsg = getMessage("lbl.sc.loiloa.supplier.confirm.button.reject", new String[] { loa.trim(), procurementMode.trim() });

			this.getManagedBean().setSuppConfirmMsgReject(rejectMsg.toString().trim());
			RequestContext.getCurrentInstance().execute("confmRejectDlg.show()");		
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
	
	//Bug #24713
	/**
	 * Validate supplier details
	 * 
	 * @param scLoa
	 *            the scLoa
	 */
	private void validateSupplierWitnessDetails(ScLoa scLoa,
			ScLoaWitness scLoaWitness, String outcome) {
		Locale locale = new FacesScopes().getUserLocale();
		
		if (!BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {			
			if (scLoa.getSupplierSignDate() == null) {
				throw new EpException(MessageConst.ERROR_760400, 
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.loiloa.loa.supplier.sign.date",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoa.getBankAccNo())) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.account.no",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (scLoa.getAckBy().getUserId() == null) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.loiloa.signer.full.name",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoa.getAckBy().getIdentificationNo())) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.identification.no",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getWitnessName())) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.loiloa.witness.full.name",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getIdentificationNo())) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.identification.no",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getDesignation())) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.designation",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getAddress())) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.address",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getPhoneNo())) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.tel.no",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getFaxNo())) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.fax.no",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
		}
	}
	
	
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
	
	// 25201 -start-
	public Boolean canShowSupplierProposal() {
		// ***25201#82 - start
		return true;
		// ***25201#82 - end
	}
	// 25201 -end-
	
	// ***25201#35 -start-
	public Boolean canShowSupplierProposalByLOAStatus(){
		/*
		i) Pending Letter of Acceptance Preparation 
		ii) Pending Letter Of Acceptance Submission 
		iii) Pending Letter of Acceptance Approval
		iv) Pending Letter of Acceptance Revision
		v) Pending Letter of Acceptance Revision Approval
		vi) Pending Acknowledgement Receipt's Letter Of Acceptance 
		vii) Pending Verification of Acknowledgement Receipt's Letter Of Acceptance
		*/
		Long[] statusList = {
				QTStatusConst.QT_PENDING_LOA, //i
				QTStatusConst.QT_RE_FINALIZATION,//viii
				QTStatusConst.QT_AWARDED, //ix Bug #27412 #33 #35
				QTStatusConst.QT_SUPPLIER_FINALIZATION, //x Bug #27412 #33 #35
				QTStatusConst.QT_CANCELLED, // Bug #27412 #33 #35
				QTStatusConst.QT_RE_EVALUATION, //Bug #27412 #33 #35
				QTStatusConst.QT_RE_FINALIZATION, //Bug #27412 #33 #35
				QTStatusConst.LOA_PENDING_SUBMISSION, //ii 
				QTStatusConst.LOA_PENDING_APPROVAL, //iii
				QTStatusConst.LOA_PENDING_REVISION, //iv
				QTStatusConst.LOA_PENDING_REVISION_APPROVAL, //v
				QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT, //vi
				QTStatusConst.LOA_PENDING_VERIFICATION, //vii
				QTStatusConst.LOA_COMPLETED, //xi Bug #27412 #33 #35
				QTStatusConst.LOA_PENDING_CONTRACT_SIGNING,//xi Bug #27412 #33 #35
				QTStatusConst.LOA_REJECTED, //xi Bug #27412 #33 #35
				QTStatusConst.LOA_CANCELLED, //xi Bug #27412 #33 #35
				QTStatusConst.LOA_REVISE_DOC
				};
		
		if (getManagedBean() != null
				&& getManagedBean().getScLoa() != null
				&& getManagedBean().getScLoa().getPmStatus() != null
				&& getManagedBean().getScLoa().getPmStatus().getStatusId() != null) {
			for (Long s : statusList) {
				if (s.longValue() == getManagedBean().getScLoa().getPmStatus()
						.getStatusId().longValue()) {
					return true;
				}
			}
		}
		
		return false;
	}
	// ***25201#35 -end-
	
	//#22211 - show LOA Cancellation Approver Detail
	public boolean isLOACancelByApprover(){
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);		
		
		Boolean loaCancelled = false;	
		Long currentStatusId = getManagedBean().getScLoa().getPmStatus().getStatusId();
		ScLoa scloa = getManagedBean().getScLoa();		
		
		ScLoaApproval scLoaApproval = scloa.getScLoaApproval();
		PmUser approverUser = null;
		if (scLoaApproval != null) {
			approverUser = scLoaApproval.getApprover();
		}
		if (approverUser == null) { 
			approverUser = new PmUser();
		}
		getManagedBean().setLoacancelapproverUser(approverUser);
				
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setRoleCode(getUserRoleCode(scloa.getScLoiLoa().getDocType()));
		userReqDTO.setOrgProfileId(scloa.getScLoiLoa().getPmOrgProfile().getOrgProfileId());
		
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
		getManagedBean().setLoacancelapproverList(userResDTO.getUserList());	
		
		if(currentStatusId.equals(QTStatusConst.LOA_CANCELLED)){
			loaCancelled = true;
		}
		return loaCancelled;
	}
	
	//#22211 - Check if Loa already generated
	public boolean isLoaGenerated(){
		Boolean loaGenerated = false;	
		ScLoa scloa = this.getManagedBean().getScLoa();
		
		if(!getManagedBean().getScLoa().isBrandNew()){
			loaGenerated = true;	
		}
		return loaGenerated;
	}
	
	public void cancelLoa(String outcome) {
		debugLogger("cancelLoa");
		this.getManagedBean().setAction(outcome);
		
		updateBPMTask(outcome,null);
	}
	//#22211 - End
	
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
	
	//#25022-start	
	public void deleteFreqScheduleDetails() {
		AwardSupplierResDTO resDTO = null;
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setScLoa(this.getManagedBean().getScLoa());

		try {
			if(getManagedBean().getScLoa().isBrandNew()){
				removeNewFreqSchedules();
			}else{
				if (null != getManagedBean().getScLoa().getScQtSchedules()) {
					
					resDTO = getQtAwardSO()
							.deletetScheduleIdAndPaymentId(reqDTO);
					if (resDTO.isSuccessful()) {
						getManagedBean().setSelectAll(false);
						getManagedBean().setButtonHideSchActive(true);
					}
					getManagedBean().getScLoa().setScQtSchedules(resDTO.getScLoa().getScQtSchedules());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

	}
	//25022
	private void removeNewFreqSchedules() {
		List<ScQtSchedule> names = getManagedBean().getScLoa()
				.getScQtSchedules();
		Iterator<ScQtSchedule> i = names.iterator();
		while (i.hasNext()) {
			ScQtSchedule s = i.next();
			if(s.isSelected()){
				i.remove();
			}
		}
	}
	
	public void deleteProgPaymentDetails() {
		AwardSupplierResDTO resDTO = null;
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setScLoa(this.getManagedBean().getScLoa());
		try {
			if (null != getManagedBean().getScLoa().getScQtProgPayments()) {
				
				resDTO = getQtAwardSO()
						.deletetScheduleIdAndPaymentId(reqDTO);
				if (resDTO.isSuccessful()) {
					getManagedBean().setSelectAll(false);
					getManagedBean().setButtonHideSchActive(true);
				}
				getManagedBean().getScLoa().setScQtProgPayments(resDTO.getScLoa().getScQtProgPayments());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}

	// 25201#73 -start-
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
	// 25201#73 -end-	
	
	/* 19336#99 - start */
	public String getManagedBeanLoaContractAgencyOrgName() {
		try {
			return getManagedBean().getScLoa().getContractAgency().getOrgName();
		} catch (Exception ex) {
			return "";
		}
	}

	public String getManagedBeanSelectedPtjOrgName() {
		try {
			return getManagedBean().getSelectedPtj().getOrgName();
		} catch (Exception ex) {
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

		// Refactor: #12177 - Start
		Integer sqTimeValidity = NgepApplicationBean.getSqTimeValidity();
		log.info("Refactor 12177 (LOADetailBackingBean) : getSqTimeValidity - " + sqTimeValidity);
		// Refactor: #12177 - End
		
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
	
	//Bug #33674@Syazani -- Start 14/09/2022
	private void retrieveValBankAccNoDropdown() {
		List<SmSupplierBank> lstSupplierBank = getManagedBean().getBankList();
		ScLoa tmpScLoa = getManagedBean().getScLoa();
		if (tmpScLoa != null && lstSupplierBank != null && lstSupplierBank.size() > 0 && tmpScLoa.getBankAccNo() != null) {
			for (SmSupplierBank ssb : lstSupplierBank) {
				if (ssb.getAccountNo().equalsIgnoreCase(tmpScLoa.getBankAccNo())
						&& ssb.getPmFinancialOrg().getFinOrgName().equalsIgnoreCase(tmpScLoa.getBankName())) {
					getManagedBean().setSupplierBankId(ssb.getSupplierBankId());
					break;
				}
			}
		}
	}
	//Bug #33674@Syazani -- End 14/09/2022
	
	// #32648 populate lampiran 7 if LOA at supplier - start
	public boolean isDpAuditOrQtAuditOrQtMaint() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| SourcingConstants.PORTLET_NAME_DP_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| SourcingConstants.PORTLET_NAME_DP_LOA_LIST.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_LIST.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// #32648 populate lampiran 7 if LOA at supplier - end
	
	// Bug #35078@Syazani -- Start 14/09/2023
	public boolean isQtAudit() {
		return QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// Bug #35078@Syazani -- End 14/09/2023
	
	// #29007 -start-
	public void onBackToZoneItemDetailsTab() {
		onResetNewSupplierOffered();

		getManagedBean().setNewSupplierJustification(null);
		getManagedBean().setSelectedItemId(null);
		getManagedBean().setSelectedTabIndex(2);

		this.getManagedBean().setSpecKeyword(null);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.LOA_PAGE);
	}

	// #29007 -end-

	// #29007 -start-
	public void onResetNewSupplierOffered() {
		getManagedBean().setNewSupplierOffered(null);
	}
	// #29007 -end-
	
	// #29007 -start-
	public String populateLabelLampiranA3(String dateOfUpdate,
			String deskOfficerName) {
		String labelLampiranA3 = StringUtils.EMPTY;
		FacesScopes facesScopes = new FacesScopes(); // #29006 - note 587
		labelLampiranA3 = ResourceBundleUtils.getMessage(
				"lbl.sc.doc.name.lampiran.a3", new String[] { dateOfUpdate,
						deskOfficerName },
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				facesScopes.getUserLocale()); // #29006 - note 587

		return labelLampiranA3;
	}

	// #29007 -end-

	// #29007 -start-
	public void createLampiranA3(ScLoiLoaItem scLoiLoaItem) {
		removeUnwantedLampiranA3Report(2);

		FacesScopes facesScopes = new FacesScopes();
		// start - #29006 (fixing on comment 570)
		ScLoaReport lampiranA3 = getQtAwardSO().createLampiranA3(
				getManagedBean().getScLoa(),
				populateLabelLampiranA3(
						" "
								+ new SimpleDateFormat(
										CommonConstants.DEFAULT_DATE_FORMAT)
										.format(DateUtils.getCurrentDate())
								+ ",  "
								+ new SimpleDateFormat(
										CommonConstants.DEFAULT_TIME_FORMAT)
										.format(DateUtils.getCurrentDate())
								+ ",",
						SmCommonUtil.getUserByUserId(
								facesScopes.getUserSessionBean().getUserId())
								.getUserName()));
		// end - #29006 (fixing on comment 570)

		try {

			StringBuilder filename = new StringBuilder();
			filename.append(lampiranA3.getTitle());
			// #29006,#31869 - start
			// lampiranA3.setScLoaReportAttachment(new ScLoaReportAttachment());
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			int year = cal.get(Calendar.YEAR);

			ScLoaReportAttachment scLoaReportAttachment = new ScLoaReportAttachment();
			AttachmentFormat scFormat = new AttachmentDateFormat();
			String formatedFileName = scFormat.format(filename.toString()
					.replaceAll("/", ""), null);
			scLoaReportAttachment.setFilePath(Integer.toString(year) + "/SC/"
					+ lampiranA3.getScLoa().getLoaNo() + "/LAR/"
					+ formatedFileName);

			lampiranA3.setScLoaReportAttachment(scLoaReportAttachment);
			// #29006,#31869 - end
			lampiranA3.getScLoaReportAttachment().setScLoaReport(lampiranA3);
			lampiranA3.getScLoaReportAttachment().setDocNo(
					lampiranA3.getScLoa().getLoaNo());

			filename.append(".pdf");

			lampiranA3.getScLoaReportAttachment().setFileName(
					filename.toString().replaceAll("/", ""));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, e
							.getMessage()));
		}

		getManagedBean().getScLoa().getScLoaReports().add(lampiranA3);
		// #29006,#31869 - start
		AwardSupplierReqDTO finalAwardReqDTO = new AwardSupplierReqDTO();

		if (null != this.getManagedBean().getScLoa()
				&& !this.getManagedBean().getScLoa().isBrandNew()) {
			finalAwardReqDTO.setScLoa(getManagedBean().getScLoa());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLampiranA3Report(
					finalAwardReqDTO);
			List<ScLoaReport> scLoaReports = resDTO.getScLoa()
					.getScLoaReports();
			ScLoa scLoa = getManagedBean().getScLoa();
			scLoa.setScLoaReports(scLoaReports);
			getManagedBean().setScLoa(scLoa);
		}
		// #29006,#31869 - end
	}

	// #29007 -end-

	// #29007 -start-
	private void removeUnwantedLampiranA3Report(int mode) {
		if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa()
				.getScLoaReports())) {
			List<ScLoaReport> oldLampiranA3List = new ArrayList<>();

			for (ScLoaReport report : getManagedBean().getScLoa()
					.getScLoaReports()) {
				if (StringUtils.isNotBlank(report.getTitle())) {
					if ((mode == 1)
							&& report.getTitle().contains(lampiranA3SubStr)
							&& report.getTitle().contains(
									"<date update> <Desk Officer Name>")) {
						report.setDeleted(true); // #29006,#31869
						oldLampiranA3List.add(report);
					} else if ((mode == 2)
							&& report.getTitle().contains(lampiranA3SubStr)) {
						report.setDeleted(true); // #29006,#31869
						oldLampiranA3List.add(report);
					}
				}
			}
			getManagedBean().getScLoa().getScLoaReports()
					.removeAll(oldLampiranA3List);
			// #29006,#31869 - start
			AwardSupplierReqDTO awardReqDTO = new AwardSupplierReqDTO();
			if (null != this.getManagedBean().getScLoa()
					&& !this.getManagedBean().getScLoa().isBrandNew()) {
				ScLoa scLoa = new ScLoa();
				scLoa.setScLoaReports(oldLampiranA3List);
				awardReqDTO.setScLoa(scLoa);
				getQtAwardSO().deleteLampiranA3Report(awardReqDTO);
			}
			// #29006,#31869 - end
		}
	}
	// #29007 -end-
	
	public String getNotAvailabilityStr() {
		return notAvailabilityStr;
	}

	public void setNotAvailabilityStr(String notAvailabilityStr) {
		this.notAvailabilityStr = notAvailabilityStr;
	}

	public String getSelectedSupplierOffer() {
		return selectedSupplierOffer;
	}

	public void setSelectedSupplierOffer(String selectedSupplierOffer) {
		this.selectedSupplierOffer = selectedSupplierOffer;
	}

	public String getItemNameSearchCriteria() {
		return itemNameSearchCriteria;
	}

	public void setItemNameSearchCriteria(String itemNameSearchCriteria) {
		this.itemNameSearchCriteria = itemNameSearchCriteria;
	}

	public List<ScLoiLoaItem> getCompleteScLoiLoaItemList() {
		return completeScLoiLoaItemList;
	}

	public void setCompleteScLoiLoaItemList(List<ScLoiLoaItem> completeScLoiLoaItemList) {
		this.completeScLoiLoaItemList = completeScLoiLoaItemList;
	}

	public ScLoiLoaItem getSelectedScLoiLoaItem() {
		return selectedScLoiLoaItem;
	}

	public void setSelectedScLoiLoaItem(ScLoiLoaItem selectedScLoiLoaItem) {
		this.selectedScLoiLoaItem = selectedScLoiLoaItem;
	}
	
	public String getLampiranA3SubStr() {
		return lampiranA3SubStr;
	}

	public void setLampiranA3SubStr(String lampiranA3SubStr) {
		this.lampiranA3SubStr = lampiranA3SubStr;
	}
	//#29006 - end
	
	/*#29007 Start Vinod*/
//	public void resetSpecKeyword(){
//		this.getManagedBean().setSpecKeyword(null);
//	}
	// #29007 -end-

	// #29007 -start-
	// 29007 - fix issue 2 search action start note 347
	/**
	 * Search populated ScLoiLoaItems by ScRequestItem.itemName and build filtered results
	 * @return void
	 */
	public void searchSupplierItemByName() {
		String itemNameInput = this.getItemNameSearchCriteria() == null ? "" : this.getItemNameSearchCriteria().trim();
	
		if (StringUtils.isEmpty(itemNameInput)) {
			this.getManagedBean().setLoiLoaItemsResult(
					new ArrayList<ScLoiLoaItem>(this.getManagedBean().getScLoa().getScLoiLoaItems()));
			return;
		}

		this.getManagedBean().setLoiLoaItemsResult(new ArrayList<ScLoiLoaItem>());

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoaItems())) {
			for (ScLoiLoaItem item : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
				String itemName = item.getScRequestItem() != null ? item.getScRequestItem().getItemName().trim() : "";
				
				if (item.getScRequestItem() != null && StringUtils.containsIgnoreCase(itemName, itemNameInput)) {
					this.getManagedBean().getLoiLoaItemsResult().add(item);
				}
			}
		}
		// 29007 - fix issue 2 search action end note 347
	}
	// #29007 -end-

	// #29007 -start-
	public void searchSupplierSpecification(ScLoiLoaItem loiLoaItemMain, boolean mainSearch) {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();

		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setScLoiLoaItem(loiLoaItemMain);

		AwardSupplierResDTO resDTO = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTO);

		ScLoiLoaItem loiLoaItem = resDTO.getScLoiLoaItem();

		this.getManagedBean().setItemNamePopup(loiLoaItem.getScRequestItem().getItemName());
		this.getManagedBean().setScLoiLoaItem(loiLoaItem);
		this.getManagedBean().setNewSupplierJustification(loiLoaItem.getLoaSpecJustification());

		if (this.getManagedBean().getScQt().isZonal() && mainSearch) {
			List<ScLoiLoaItem> zonalItemList = new ArrayList<>();
			
			for (ScLoiLoaItem loacalloiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
				if (loiLoaItem.getLoiLoaItemId() == loacalloiLoaItem.getLoiLoaItemId()
						|| loiLoaItem.getLoiLoaItemId().equals(loacalloiLoaItem.getLoiLoaItemId())
						|| loiLoaItem.getScRequestItem().getRequestNoteItemId() == loacalloiLoaItem.getScRequestItem()
								.getRequestNoteItemId()
						|| loiLoaItem.getScRequestItem().getRequestNoteItemId()
								.equals(loacalloiLoaItem.getScRequestItem().getRequestNoteItemId())) {
					zonalItemList.add(loacalloiLoaItem);
				}
			}
			
			if (zonalItemList.size() > 1) {
				checkItemSorting(zonalItemList);
				
				ScLoiLoaItem latestChangedItem = zonalItemList.get(zonalItemList.size() - 1);
				
				this.getManagedBean().setNewSupplierJustification(latestChangedItem.getLoaSpecJustification());
			}
		}

		specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
		specReqDto.setScQt(this.getManagedBean().getScQt());
		specReqDto.setScLoiLoaItem(loiLoaItem);

		if (this.getManagedBean().getSpecKeyword() != null) {
			TplSpecQuestion tplSpecQuestion = new TplSpecQuestion();
			TplSpecAnswer tplSpecAnswer = new TplSpecAnswer();

			tplSpecQuestion.setClSpecDetail(this.getManagedBean().getSpecKeyword());
			tplSpecAnswer.setTplSpecQuestion(tplSpecQuestion);

			specReqDto.setTplSpecAnswer(tplSpecAnswer);
		}
		
		boolean qtMaint = isQTMaintenanceScreen();
		
		if (QTStatusConst.LOA_PENDING_VERIFICATION.equals(getManagedBean().getScLoa().getPmStatus().getStatusId())
				|| QTStatusConst.LOA_PENDING_SUBMISSION.equals(getManagedBean().getScLoa().getPmStatus().getStatusId())
				|| QTStatusConst.LOA_PENDING_REVISION.equals(getManagedBean().getScLoa().getPmStatus().getStatusId())
				&& !qtMaint) {
			getManagedBean().setSupPropSpecDisable(false);
		} else {
			getManagedBean().setSupPropSpecDisable(true);
		}

		specReqDto.setSpecSearchForApp(true);
		
		TemplateSpecResDTO templateSpecResDTO = templateSpecSO.searchSupplierSpecification(specReqDto);

		for (TplSpecAnswer tplSpecAnswer : templateSpecResDTO.getSpecAnswerList()) {
			if (tplSpecAnswer.getNewSupplierOffer() != null && tplSpecAnswer.getNewSupplierOffer() != "") {
				tplSpecAnswer.setRenderNewSpec(true);
			}
		}

		this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
		this.getManagedBean().setScLoiLoaItem(loiLoaItem);
		
		if (mainSearch) {
			PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			portletSession.setAttribute(QTConstants.PORTLET_ATTRIBUTE_NEW_OFFERED_SPECIFICATION, loiLoaItem);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.REDIRECT_URL_NEW_OFFERED_SPECIFICATION_LOA);
		}
	}
	// #29007 -end-

	// #29007 -start-
	public void saveSupplierSpecification(boolean isComplete) {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		IQTAwardSO iQTAwardSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTAwardSO.class);
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		
		if (this.getManagedBean().getTplSpecAnswerList() != null
				|| this.getManagedBean().getNewSupplierJustification() != null) {
			if (this.getManagedBean().getScQt().isZonal() && this.getManagedBean().isResetAll()) {
				for (ScLoiLoaItem zoneLoiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (null != this.getManagedBean().getScLoiLoaItem()
							&& zoneLoiLoaItem.getLoiLoaItemId() == this.getManagedBean().getScLoiLoaItem()
									.getLoiLoaItemId()) {
						zoneLoiLoaItem.setLoaSpecJustification(null);
					}
					
					if (this.getManagedBean().getScLoa().isBrandNew()) {
						zoneLoiLoaItem.setScLoa(null);
					}
				}

				reqDTO.setScLoiLoaItemList(this.getManagedBean().getScLoa().getScLoiLoaItems());

				AwardSupplierResDTO resDTOLatest = getQtAwardSO().saveZoneItemListForResetAll(reqDTO);

				if (resDTOLatest.isSuccessful()) {
					ScLoa scLoa = this.getManagedBean().getScLoa();
					scLoa.setScLoiLoaItems(resDTOLatest.getScLoiLoaItemList());
				}

				for (ScLoiLoaItem updatedLoiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (updatedLoiLoaItem.getLoiLoaItemId() == this.getManagedBean().getScLoiLoaItem()
							.getLoiLoaItemId()) {
						this.getManagedBean().setScLoiLoaItem(updatedLoiLoaItem);
						break;
					}
				}

				reqDTO = new AwardSupplierReqDTO();
			}
			
			ScLoiLoaItem scLoiLoaItem = this.getManagedBean().getScLoiLoaItem();

			AwardSupplierReqDTO reqDTOLatest = new AwardSupplierReqDTO();
			reqDTOLatest.setScLoiLoaItem(scLoiLoaItem);

			AwardSupplierResDTO resDTOLatest = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTOLatest);

			if (resDTOLatest.getScLoiLoaItem() != null) {
				scLoiLoaItem = resDTOLatest.getScLoiLoaItem();
			}
			
			scLoiLoaItem.setComplete(isComplete);
			
			if (isComplete) {
				if (null == this.getManagedBean().getNewSupplierJustification()
						|| StringUtils.isBlank(this.getManagedBean().getNewSupplierJustification())
						|| StringUtils.isEmpty(this.getManagedBean().getNewSupplierJustification())) {
					String errorMessage = ResourceBundleUtils.getMessage(QTConstants.LBL_SC_JUSTIFICATION,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
					throw new EpException(MessageConst.ERROR_775185, new String[] { errorMessage });
				}
			}

			if (this.getManagedBean().getNewSupplierJustification() != null
					&& this.getManagedBean().getNewSupplierJustification() != "") {
				int newoffered = 0;
				
				for (TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()) {
					if (tplSpecAnswer.getNewSupplierOffer() != null && tplSpecAnswer.getNewSupplierOffer() != "") {
						newoffered++;
					}
				}
				
				if (isComplete && newoffered < 1) {
					int index = 0;
					
					for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
						if (loiLoaItem.getLoiLoaItemId() == scLoiLoaItem.getLoiLoaItemId()) {
							this.getManagedBean().getScLoa().getScLoiLoaItems().set(index, scLoiLoaItem);
							break;
						}
						
						index++;
					}
					
					throw new EpException(MessageConst.ERROR_775170);
				}
			}
			
			if (null != this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()) {
				scLoiLoaItem.setScLoa(null);
			}

			scLoiLoaItem.setLoaSpecJustification(this.getManagedBean().getNewSupplierJustification());

			if (scLoiLoaItem.getLoaSpecJustification() != null
					&& StringUtils.isNotBlank(scLoiLoaItem.getLoaSpecJustification())
					&& StringUtils.isNotEmpty(scLoiLoaItem.getLoaSpecJustification())) {
				scLoiLoaItem.getLoaSpecJustification();
				
				StringBuilder builder = new StringBuilder();
				builder.append(scLoiLoaItem.getLoaSpecJustification()).append(" ");
				
				scLoiLoaItem.setLoaSpecJustification(builder.toString());

			}

			reqDTO.setTplSpecAnswerList(this.getManagedBean().getTplSpecAnswerList());
			reqDTO.setScLoiLoaItem(scLoiLoaItem);

			AwardSupplierResDTO resDTO = iQTAwardSO.saveSupplierSpecification(reqDTO);
			int index = 0;
			
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoaItems())) {
				for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (loiLoaItem.getLoiLoaItemId() == resDTO.getScLoiLoaItem().getLoiLoaItemId()
							|| loiLoaItem.getLoiLoaItemId().equals(resDTO.getScLoiLoaItem().getLoiLoaItemId())) {
						if (null != this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()) {
							resDTO.getScLoiLoaItem().setScLoa(this.getManagedBean().getScLoa());
						}
						
						this.getManagedBean().getScLoa().getScLoiLoaItems().remove(index);
						this.getManagedBean().getScLoa().getScLoiLoaItems().add(index, resDTO.getScLoiLoaItem());
						break;
					}
					
					index++;
				}
				
				// 29007 note 503 start
				List<ScLoiLoaItem> items = new ArrayList<>();
				AwardSupplierReqDTO reqDTONew = new AwardSupplierReqDTO();

				for (ScLoiLoaItem item : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (!scLoiLoaItem.getLoiLoaItemId().equals(item.getLoiLoaItemId())
							&& scLoiLoaItem.getScRequestItem().getRequestNoteItemId()
									.equals(item.getScRequestItem().getRequestNoteItemId())) {
						reqDTOLatest.setScLoiLoaItem(item);
						
						item = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTOLatest).getScLoiLoaItem();
						
						reqDTONew.setTplSpecAnswerList(null);
						
						item.setLoaSpecJustification(this.getManagedBean().getNewSupplierJustification());
						item.setComplete(resDTO.getScLoiLoaItem().isComplete());
						
						reqDTONew.setScLoiLoaItem(item);
						
						getQtAwardSO().saveSupplierSpecification(reqDTONew);	
						
						items.add(item);
					}
				}
				
				for (ScLoiLoaItem item : items) {
					int indexNew = 0;
					
					reqDTOLatest.setScLoiLoaItem(item);
					
					item = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTOLatest).getScLoiLoaItem();
					
					for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
						if (loiLoaItem.getLoiLoaItemId() == item.getLoiLoaItemId()
								|| loiLoaItem.getLoiLoaItemId().equals(item.getLoiLoaItemId())) {
							if (null != this.getManagedBean().getScLoa()
									&& this.getManagedBean().getScLoa().isBrandNew()) {
								resDTO.getScLoiLoaItem().setScLoa(this.getManagedBean().getScLoa());
							}
							
							this.getManagedBean().getScLoa().getScLoiLoaItems().remove(indexNew);
							this.getManagedBean().getScLoa().getScLoiLoaItems().add(indexNew, item);
							break;
						}

						indexNew++;
					}
				}
				// 29007 note 503 end
				
				if (this.getManagedBean().getScQt().isZonal() && !isComplete) {
					for (ScLoiLoaItem loacalloiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
						if (resDTO.getScLoiLoaItem().getLoiLoaItemId() == loacalloiLoaItem.getLoiLoaItemId()
								|| resDTO.getScLoiLoaItem().getLoiLoaItemId()
										.equals(loacalloiLoaItem.getLoiLoaItemId())
								|| resDTO.getScLoiLoaItem().getScRequestItem().getRequestNoteItemId() == loacalloiLoaItem
										.getScRequestItem().getRequestNoteItemId()
								|| resDTO.getScLoiLoaItem().getScRequestItem().getRequestNoteItemId()
										.equals(loacalloiLoaItem.getScRequestItem().getRequestNoteItemId())) {
							loacalloiLoaItem.setComplete(isComplete);
						}
					}
				}
				
				for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (null != this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()) {
						loiLoaItem.setScLoa(this.getManagedBean().getScLoa());
					}
				}
			}

			if (resDTO.isSuccessful() && !this.getManagedBean().isResetAllforNoReport()) {
				if (!getManagedBean().getScLoa().getPmStatus().getStatusId()
						.equals(QTStatusConst.LOA_PENDING_VERIFICATION)) {
					createLampiranA3forQT(isComplete);
				} else {
					createLampiranA3forQTVerification(isComplete);
				}
				
				if (isComplete) {
					onBackToZoneItemDetailsTab();
				} else {
					TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();

					this.getManagedBean().setScLoiLoaItem(resDTO.getScLoiLoaItem());

					if (this.getManagedBean().getSpecKeyword() != null) {
						TplSpecQuestion tplSpecQuestion = new TplSpecQuestion();
						TplSpecAnswer tplSpecAnswer = new TplSpecAnswer();

						specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
						specReqDto.setScQt(this.getManagedBean().getScQt());

						tplSpecQuestion.setClSpecDetail(this.getManagedBean().getSpecKeyword());
						
						tplSpecAnswer.setTplSpecQuestion(tplSpecQuestion);

						specReqDto.setTplSpecAnswer(tplSpecAnswer);
						specReqDto.setScLoiLoaItem(resDTO.getScLoiLoaItem());
						specReqDto.setSpecSearchForApp(true);
					} else {
						specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
						specReqDto.setScQt(this.getManagedBean().getScQt());
						specReqDto.setScLoiLoaItem(this.getManagedBean().getScLoiLoaItem());
						specReqDto.setSpecSearchForApp(true);
					}
					
					TemplateSpecResDTO templateSpecResDTO = templateSpecSO.searchSupplierSpecification(specReqDto);

					this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
					for(TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()){
						if(tplSpecAnswer.getNewSupplierOffer()!=null && tplSpecAnswer.getNewSupplierOffer()!=""){
							tplSpecAnswer.setRenderNewSpec(true);
						}
					}
					successMessage(MessageConst.INFO_700004,
							getMessageCommon(QTConstants.LBL_SC_LOILOA_SUPPLIER_SPECIFICATION_SAVE), "", Boolean.FALSE);
				}
			}
		}
	}
	// #29007 -end-

	// #29007 -start-
	private void createLampiranA3forQT(boolean isComplete) {
		AwardSupplierReqDTO awardReqDTO = new AwardSupplierReqDTO();
		List<ScLoaReport> oldLampiranA3List = new ArrayList<>();
		int recordCount = 0;

		if (isComplete) {
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
						report.setTitle(populateLabelLampiranA3(new SimpleDateFormat(
								CommonConstants.DEFAULT_DATETIME_FORMAT).format(DateUtils.getCurrentDate()),
								SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy()).getUserName()));
						report.setIslampiranA3(true);
						
						recordCount++;
					}
					
					oldLampiranA3List.add(report);
				}
			}
			
			if (recordCount > 0) {
				getManagedBean().getScLoa().setScLoaReports(oldLampiranA3List);
				
				if (null != this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()) {
					awardReqDTO.setScLoa(getManagedBean().getScLoa());

					AwardSupplierResDTO resDTO = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
					List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
					
					ScLoa scLoa = getManagedBean().getScLoa();
					scLoa.setScLoaReports(scLoaReports);
					
					getManagedBean().setScLoa(scLoa);
				}
			} else {
				ScLoaReport lampiranA3 = getQtAwardSO()
						.createLampiranA3(
								getManagedBean().getScLoa(),
								populateLabelLampiranA3(new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT)
										.format(DateUtils.getCurrentDate()),
										SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy())
												.getUserName()));
				lampiranA3.setIslampiranA3(true);
				lampiranA3.setScLoa(getManagedBean().getScLoa());

				IParameterSO parameterSO = null;
				
				try {
					parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);

					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
					reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD);
					
					ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
					
					lampiranA3.setDefinationType(resDTO.getPmParameter());
					lampiranA3.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER); // AQ
				} catch (Exception e) {
					SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
							SLALogConstant.QT_LOA_DETAIL_BACKINGBEAN, this.getManagedBean().getScLoa().getLoaNo(),
							SLALogConstant.QT_CREATE_LAMPIRANA3FORQT, getUserIdSessionFromLoginId(), e);
				}

				getManagedBean().getScLoa().getScLoaReports().add(lampiranA3);
				
				if (null != this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()) {
					awardReqDTO.setScLoa(getManagedBean().getScLoa());

					AwardSupplierResDTO resDTO = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
					List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
					
					ScLoa scLoa = getManagedBean().getScLoa();
					scLoa.setScLoaReports(scLoaReports);
					
					getManagedBean().setScLoa(scLoa);
				}
			}
		} else {
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
						report.setDeleted(true);
						
						oldLampiranA3List.add(report);
						
						recordCount++;
					}
				}
			}

			int loaItemslampirian = 0;

			for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
				if (loiLoaItem.isComplete()) {
					loaItemslampirian++;
					break;
				}
			}

			if (recordCount > 0 && loaItemslampirian == 0) {
				getManagedBean().getScLoa().getScLoaReports().removeAll(oldLampiranA3List);

				if (null != this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()) {
					ScLoa scLoa = new ScLoa();
					scLoa.setScLoaReports(oldLampiranA3List);
					
					awardReqDTO.setScLoa(scLoa);
					
					getQtAwardSO().deleteLampiranA3Report(awardReqDTO);
				}
			}
		}
	}
	// #29007 -end-

	// #29007 -start-
	/**
	 * generate LampiranA3Pdf From Velocity
	 * @param attachment
	 * @param scLoa
	 * @return 
	 */
	private void generateLampiranA3PdfFromVelocity(ScAttachment attachment, ScLoa scLoa) throws Exception {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		IQTAwardSO iQTAwardSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTAwardSO.class);		
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();
		List<Long> itemIds = new ArrayList<>();

		populateAttachmentDetails(attachment);
		
		VelocityContext context = new VelocityContext();
		List<ScLoiLoaItem> scLoiLoaItemsList = null;
		String dafaltValue = QTConstants.LAMPIRAN_A3_TEXT_TB;
		FacesScopes facesScopes = new FacesScopes();
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(facesScopes.getUserScreenName());
		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
		if (ReportConst.ENG_LANGUAGE_CODE.equals(pmUser.getPreferredLanguage())) {
			dafaltValue = QTConstants.LAMPIRAN_A3_TEXT_TB;//29007
		}
		
		
		if (!scLoa.getScLoiLoaItems().isEmpty()) {
			scLoiLoaItemsList = new ArrayList<>();
			for (ScLoiLoaItem scLoiLoaItemOld : scLoa.getScLoiLoaItems()) {
				AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
				reqDTO.setScLoiLoaItem(scLoiLoaItemOld);

				AwardSupplierResDTO resDTO = iQTAwardSO.searchLoiLoaItemDetails(reqDTO);
				ScLoiLoaItem scLoiLoaItem = resDTO.getScLoiLoaItem();
				DateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT);
				String date = df.format(scLoiLoaItem.getChangedDate());
				StringBuilder finalMsg = new StringBuilder();

				if (this.getManagedBean().getScQt().isZonal()) {
					List<ScLoiLoaItem> zonalItemList = new ArrayList<>();
					
					for (ScLoiLoaItem loacalloiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
						if (scLoiLoaItem.getLoiLoaItemId() == loacalloiLoaItem.getLoiLoaItemId()
								|| scLoiLoaItem.getLoiLoaItemId().equals(loacalloiLoaItem.getLoiLoaItemId())
								|| scLoiLoaItem.getScRequestItem().getRequestNoteItemId() == loacalloiLoaItem
										.getScRequestItem().getRequestNoteItemId()
								|| scLoiLoaItem.getScRequestItem().getRequestNoteItemId()
										.equals(loacalloiLoaItem.getScRequestItem().getRequestNoteItemId())) {
							zonalItemList.add(loacalloiLoaItem);
						}
					}
					
					if (zonalItemList.size() > 1) {
						checkItemSorting(zonalItemList);
						
						ScLoiLoaItem latestChangedItem = zonalItemList.get(zonalItemList.size() - 1);
						
						if (latestChangedItem.getLoaSpecJustification() != null
								&& StringUtils.isNotBlank(latestChangedItem.getLoaSpecJustification())
								&& StringUtils.isNotEmpty(latestChangedItem.getLoaSpecJustification())) {
							scLoiLoaItem.setLoaSpecJustification(latestChangedItem.getLoaSpecJustification());
							scLoiLoaItem.setLatestChangedDate(latestChangedItem.getChangedDate());
							scLoiLoaItem.setComplete(latestChangedItem.isComplete());

							date = df.format(scLoiLoaItem.getLatestChangedDate());
						}
					} else {
						scLoiLoaItem.setLatestChangedDate(scLoiLoaItem.getChangedDate());
						
						date = df.format(scLoiLoaItem.getLatestChangedDate());
					}
				}
				if (scLoiLoaItem.getScRequestItem() != null
						&& scLoiLoaItem.getScRequestItem().getItemName() != null
						&& StringUtils.isNotBlank(scLoiLoaItem.getScRequestItem().getItemName())
						&& StringUtils.isNotEmpty(scLoiLoaItem.getScRequestItem().getItemName())) {
					String itemName = VmUtil.getInstance()
							.replaceAllSpecialCharactersToHTML(scLoiLoaItem.getScRequestItem().getItemName());
					scLoiLoaItem.setItemNameForVelocity(VmUtil.getInstance()
							.addLineBreaks(itemName, 60));
				}
				if (scLoiLoaItem.getLoaSpecJustification() != null
						&& StringUtils.isNotBlank(scLoiLoaItem.getLoaSpecJustification())
						&& StringUtils.isNotEmpty(scLoiLoaItem.getLoaSpecJustification())) {
					finalMsg.append(QTConstants.LAMPIRAN_A3_JUSTIFICATION
							+ scLoiLoaItem.getLoaSpecJustification() + " "
							+ date);
					String finalMessage = VmUtil.getInstance()
							.replaceAllSpecialCharactersToHTML(
									finalMsg.toString());
					scLoiLoaItem.setLoaSpecJustificationForVelocity(VmUtil
							.getInstance().addLineBreaks(finalMessage, 60));
				}

				specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
				specReqDto.setScQt(this.getManagedBean().getScQt());
				specReqDto.setScLoiLoaItem(scLoiLoaItem);
				specReqDto.setSpecSearchForApp(false);

				TemplateSpecResDTO templateSpecResDTO = templateSpecSO.searchSupplierSpecification(specReqDto);
				
				if (null != templateSpecResDTO && null != templateSpecResDTO.getSpecAnswerList()
						&& CollectionUtils.isNotEmpty(templateSpecResDTO.getSpecAnswerList())) {
					boolean newOfferedSpecification = false;
					List<Long> replacedQuestionIds = new ArrayList<>();
					for (TplSpecAnswer specAnswer : templateSpecResDTO.getSpecAnswerList()) {
						String feedback = null;
						
						if (null != specAnswer.getAnswerCompliance()) {
							if (specAnswer.getAnswerCompliance()) {
								feedback = QTConstants.LAMPIRAN_A3_TEXT_YA;
							} else {
								feedback = QTConstants.LAMPIRAN_A3_TEXT_TIDAK;
							}
							
							specAnswer.setFeedback(feedback);
						}
						
						if (!scLoiLoaItem.isComplete()) {
							specAnswer.setNewSupplierOffer(null);
						}

						if(specAnswer.getNewSupplierOffer() != null 
								&& StringUtils.isNotBlank(specAnswer.getNewSupplierOffer()) 
								&& StringUtils.isNotEmpty(specAnswer.getNewSupplierOffer())){
							newOfferedSpecification=true;
							String newSupplierOffered=VmUtil.getInstance().replaceAllSpecialCharactersToHTML(specAnswer.getNewSupplierOffer());
							specAnswer.setNewSupplierOffer(VmUtil.getInstance().addLineBreaks(newSupplierOffered,25));
						} else if (specAnswer.getTplSpecQuestion().getClSpecDetail()!=null && StringUtils.isNotBlank(specAnswer.getTplSpecQuestion().getClSpecDetail())){
							String spec=VmUtil.getInstance().replaceAllSpecialCharactersToHTML(specAnswer.getTplSpecQuestion().getClSpecDetail());
							specAnswer.setNewSupplierOffer(VmUtil.getInstance().addLineBreaks(spec,25));
						}
						boolean isReplaceSpChar = replacedQuestionIds.isEmpty()
								|| !replacedQuestionIds.contains(specAnswer.getTplSpecQuestion()
										.getSpecQuestionId());
						boolean isItemNotNull = specAnswer.getTplSpecQuestion().getClItem() != null
								&& StringUtils.isNotBlank(specAnswer.getTplSpecQuestion().getClItem())
								&& StringUtils.isNotEmpty(specAnswer.getTplSpecQuestion().getClItem());
						if (specAnswer.getTplSpecQuestion() != null && isItemNotNull && isReplaceSpChar) {
							String itemName = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
									specAnswer.getTplSpecQuestion().getClItem());
							specAnswer.getTplSpecQuestion().setClItem(
									VmUtil.getInstance().addLineBreaks(itemName, 30));
							replacedQuestionIds.add(specAnswer.getTplSpecQuestion().getSpecQuestionId());
						}
						if(specAnswer.getAnswerRemark() != null && StringUtils.isNotBlank(specAnswer.getAnswerRemark()) && StringUtils.isNotEmpty(specAnswer.getAnswerRemark())){
							String ansRemark=VmUtil.getInstance().replaceAllSpecialCharactersToHTML(specAnswer.getAnswerRemark());
							specAnswer.setAnswerRemark(VmUtil.getInstance().addLineBreaks(ansRemark,25));
						}
					}
					
					if (!newOfferedSpecification) {
						scLoiLoaItem.setLoaSpecJustificationForVelocity(null);
					}
				}

				// 20230706@asyrafhaziq- SQUBE Bug NullPointer#3 -start-
				scLoiLoaItem.setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
				// 20230706@asyrafhaziq- SQUBE Bug NullPointer#3 -end-
				
				if(itemIds.isEmpty() || !itemIds.contains(scLoiLoaItem.getScRequestItem().getRequestNoteItemId())){
					scLoiLoaItemsList.add(scLoiLoaItem);
					itemIds.add(scLoiLoaItem.getScRequestItem().getRequestNoteItemId());
				}
			}
		}

		context.put(QTConstants.LAMPIRAN_A3_TEXT_SERVICE_SCOPE, dafaltValue);
		context.put(QTConstants.LAMPIRAN_A3_TEXT_ITEMS_LIST, scLoiLoaItemsList); 
		context.put(QTConstants.LAMPIRAN_A3_TEXT_IS_ZONAL, scLoa.getScLoiLoa().isZonal());
		context.put(QTConstants.LAMPIRAN_A3_TEXT_BEAN, this);

		try (FileOutputStream fos = new FileOutputStream(getActualFilePath(attachment))) {
			DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.LOA_LAMPIRAN_A3, context, fos);
		} catch (FileNotFoundException e) {
			// Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] START
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_LOA_DETAIL_BACKINGBEAN,
					this.getManagedBean().getScQt().getQtNo(), SLALogConstant.QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY,
					getUserIdSessionFromLoginId(), e);
			// Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] END
		} catch (Exception ex) {
			// Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] START
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_LOA_DETAIL_BACKINGBEAN,
					this.getManagedBean().getScQt().getQtNo(), SLALogConstant.QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY,
					getUserIdSessionFromLoginId(), ex);
			// Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] END
		}
	}
	// #29007 -end-

	// #29007 -start-
	public void submitConformationForLampiranA3() {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();
		int recordCount = 0;
		int itemCount = 0;
		StringBuilder itemsList = new StringBuilder();
		int completeItemCount = 0;
		List<ScLoiLoaItem> completeItems = new ArrayList<>();
		int zoneitemCount = 0;
		ScLoiLoaItem zoneloiLoaItem = null;
		boolean isZonal = this.getManagedBean().getScLoa().getScLoiLoa().isZonal();
		List<Long> itemIds = new ArrayList<>();
		HashMap<Long, Long> itemZoneMap = new HashMap<>();

		if (this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoa().getScLois())) {
			for(ScLoiLoaItem loiLoaItem : sortLoiLoaItems(this.getManagedBean().getScLoa().getScLoiLoaItems(), isZonal)){
				specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
				specReqDto.setScQt(this.getManagedBean().getScQt());
				specReqDto.setScLoiLoaItem(loiLoaItem);
				specReqDto.setSpecSearchForApp(true);
				TemplateSpecResDTO templateSpecResDTO  = templateSpecSO.searchSupplierSpecification(specReqDto);
			
				int newSupplierOffer = checkSupplierOffered(templateSpecResDTO);
				if(null!=this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()){
					loiLoaItem.setScLoa(this.getManagedBean().getScLoa());
				}
				boolean addItem = !loiLoaItem.isComplete()
						&& (newSupplierOffer > 0 || StringUtils.isNotBlank(loiLoaItem.getLoaSpecJustification()));
				if(addItem
					&& !isZonal
					&& (CollectionUtils.isEmpty(itemIds) || !itemIds.contains(loiLoaItem.getScRequestItem()
							.getRequestNoteItemId()))){
					itemIds.add(loiLoaItem.getScRequestItem().getRequestNoteItemId());
					itemCount++;
					itemsList.append(itemCount).append(". ").append(loiLoaItem.getScRequestItem().getItemName())
					.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
				}else if(addItem
						&& isZonal
						&& (itemZoneMap.isEmpty() || checkItemAndZoneAdded(itemZoneMap, loiLoaItem.getScRequestItem()
								.getRequestNoteItemId(), loiLoaItem.getTplSpecZoneDtl().getSpecZoneId()))){
					itemZoneMap.put(loiLoaItem.getScRequestItem().getRequestNoteItemId(), loiLoaItem
							.getTplSpecZoneDtl().getSpecZoneId());
					Long zoneIdAdded = null != zoneloiLoaItem ? new Long(zoneloiLoaItem.getTplSpecZoneDtl()
							.getSpecZoneId()) : null;
					Long zoneIdNew = null != loiLoaItem ? new Long(loiLoaItem.getTplSpecZoneDtl().getSpecZoneId())
							: null;
					boolean addZoneName = null == zoneloiLoaItem || zoneIdAdded.compareTo(zoneIdNew) != 0;
					if (addZoneName) {
						zoneloiLoaItem = loiLoaItem;
						itemsList.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE)
								.append(loiLoaItem.getTplSpecZoneDtl().getZoneName())
								.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
						zoneitemCount = 0;
					}
					zoneitemCount++;
					itemsList.append(zoneitemCount).append(". ").append(loiLoaItem.getScRequestItem().getItemName())
							.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
					itemCount++;
				}else if(loiLoaItem.isComplete()){
					completeItemCount++;
					completeItems.add(loiLoaItem);
				}
			}
		}
		
		if (itemCount > 0) {
			itemsList.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
			throw new EpException(MessageConst.ERROR_775175, new String[] { itemsList.toString() });
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
			for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
				if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
					recordCount++;
				}
			}
		}
		
		if (recordCount > 0
				&& this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)) {
			RequestContext.getCurrentInstance().execute("submitConfirmPopUp.show()");
		} else if (recordCount == 0 && completeItemCount > 0) {
			for (ScLoiLoaItem scLoiLoaItem : completeItems) {
				this.getManagedBean().setSpecKeyword(" ");
				this.getManagedBean().setScLoiLoaItem(scLoiLoaItem);
				this.getManagedBean().setResetAllforNoReport(true);
				this.resetAllNewOfferedSpecificationSubmit();
			}
			
			this.submitAction();
			return;
		} else {
			this.submitAction();
			return;
		}
	}
	// #29007 -end-
	
	// #29007 -start-
	public void verifyConformationForLampiranA3(String bpmAction) {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();
		int recordCount = 0;
		int itemCount = 0;
		StringBuilder itemsList = new StringBuilder();
		int zoneitemCount = 0;
		ScLoiLoaItem zoneloiLoaItem = null;
		boolean isZonal = this.getManagedBean().getScLoa().getScLoiLoa().isZonal();
		List<Long> itemIds = new ArrayList<>();
		HashMap<Long, Long> itemZoneMap = new HashMap<>();

		if (this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoa().getScLois())) {
			for(ScLoiLoaItem loiLoaItem : sortLoiLoaItems(this.getManagedBean().getScLoa().getScLoiLoaItems(), isZonal)){
				specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
				specReqDto.setScQt(this.getManagedBean().getScQt());
				specReqDto.setScLoiLoaItem(loiLoaItem);
				specReqDto.setSpecSearchForApp(true);
				TemplateSpecResDTO templateSpecResDTO  = templateSpecSO.searchSupplierSpecification(specReqDto);
			
				int newSupplierOffer = checkSupplierOffered(templateSpecResDTO);
				boolean addItemZonal = !loiLoaItem.isComplete()
						&& (newSupplierOffer > 0 || StringUtils.isNotBlank(loiLoaItem.getLoaSpecJustification())) && isZonal;
				boolean addItemNonZonal = !loiLoaItem.isComplete()
						&& (newSupplierOffer > 0 || StringUtils.isNotBlank(loiLoaItem.getLoaSpecJustification())) && !isZonal;
				if(addItemNonZonal
					&& (CollectionUtils.isEmpty(itemIds) || !itemIds.contains(loiLoaItem.getScRequestItem()
							.getRequestNoteItemId()))){
					itemIds.add(loiLoaItem.getScRequestItem().getRequestNoteItemId());
					itemCount++;
					itemsList.append(itemCount).append(". ").append(loiLoaItem.getScRequestItem().getItemName())
							.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
				}else if(addItemZonal
						&& (itemZoneMap.isEmpty() || checkItemAndZoneAdded(itemZoneMap, loiLoaItem.getScRequestItem()
								.getRequestNoteItemId(), loiLoaItem.getTplSpecZoneDtl().getSpecZoneId()))){
					itemZoneMap.put(loiLoaItem.getScRequestItem().getRequestNoteItemId(), loiLoaItem
							.getTplSpecZoneDtl().getSpecZoneId());
					Long zoneIdAdded = null != zoneloiLoaItem ? new Long(zoneloiLoaItem.getTplSpecZoneDtl()
							.getSpecZoneId()) : null;
					Long zoneIdNew = null != loiLoaItem ? new Long(loiLoaItem.getTplSpecZoneDtl().getSpecZoneId())
							: null;
					boolean addZoneName = null == zoneloiLoaItem || zoneIdAdded.compareTo(zoneIdNew) != 0;
					if (addZoneName) {
						zoneloiLoaItem = loiLoaItem;
						itemsList.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE)
								.append(loiLoaItem.getTplSpecZoneDtl().getZoneName())
								.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
						zoneitemCount = 0;
					}
					zoneitemCount++;
					itemsList.append(zoneitemCount).append(". ").append(loiLoaItem.getScRequestItem().getItemName())
							.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
					itemCount++;
				}
			}
		}
		
		if (itemCount > 0) {
			itemsList.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
			throw new EpException(MessageConst.ERROR_775176, new String[] { itemsList.toString() });
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
			for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
				if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
					recordCount++;
				}
			}
		}
		
		if (recordCount > 0
				&& this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)) {
			this.getManagedBean().setLoaBpmVerify(bpmAction);
			
			RequestContext.getCurrentInstance().execute("verifyConfirmPopUp.show()");
		} else {
			this.approveBeforeupdateBpmTaskAction(bpmAction);
			return;
		}
	}
	// #29007 -end-
	
	// #29007 -start-
	private List<ScLoiLoaItem> sortLoiLoaItems(List<ScLoiLoaItem> loiLoaItems,
			boolean isZonal) {

		if (isZonal) {
			Comparator<ScLoiLoaItem> zonalComparator = new Comparator<ScLoiLoaItem>() {
				@Override
				public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
					if (s1 == null || s2 == null) {
						return 0;
					}

					long zoneId1 = (s1.getTplSpecZoneDtl() != null) ? s1
							.getTplSpecZoneDtl().getSpecZoneId()
							: Long.MIN_VALUE;
					long zoneId2 = (s2.getTplSpecZoneDtl() != null) ? s2
							.getTplSpecZoneDtl().getSpecZoneId()
							: Long.MIN_VALUE;

					int zoneCompare = Long.compare(zoneId1, zoneId2);
					if (zoneCompare != 0) {
						return zoneCompare;
					}

					if (s1.getTplSpecTierQty() != null
							&& s2.getTplSpecTierQty() != null) {

						int tierCompare = Long.compare(s1.getTplSpecTierQty()
								.getSpecTierQtyId(), s2.getTplSpecTierQty()
								.getSpecTierQtyId());
						if (tierCompare != 0) {
							return tierCompare;
						}
					}

					long requestNoteItemId1 = (s1.getScRequestItem() != null) ? s1
							.getScRequestItem().getRequestNoteItemId()
							: Long.MIN_VALUE;
					long requestNoteItemId2 = (s2.getScRequestItem() != null) ? s2
							.getScRequestItem().getRequestNoteItemId()
							: Long.MIN_VALUE;

					return Long.compare(requestNoteItemId1, requestNoteItemId2);
				}
			};

			Collections.sort(loiLoaItems, zonalComparator);
		} else {
			Comparator<ScLoiLoaItem> nonZonalComparator = new Comparator<ScLoiLoaItem>() {
				@Override
				public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
					if (s1 == null || s2 == null) {
						return 0;
					}

					long requestNoteItemId1 = (s1.getScRequestItem() != null) ? s1
							.getScRequestItem().getRequestNoteItemId()
							: Long.MIN_VALUE;
					long requestNoteItemId2 = (s2.getScRequestItem() != null) ? s2
							.getScRequestItem().getRequestNoteItemId()
							: Long.MIN_VALUE;

					if (s1.getTplSpecTierQty() != null
							&& s2.getTplSpecTierQty() != null) {

						int tierCompare = Long.compare(s1.getTplSpecTierQty()
								.getSpecTierQtyId(), s2.getTplSpecTierQty()
								.getSpecTierQtyId());
						if (tierCompare != 0) {
							return tierCompare;
						}
					}

					return Long.compare(requestNoteItemId1, requestNoteItemId2);
				}
			};

			Collections.sort(loiLoaItems, nonZonalComparator);
		}

		return loiLoaItems;
	}
	// #29007 -end-

	// #29007 -start-
	private int checkSupplierOffered(TemplateSpecResDTO templateSpecResDTO) {
		int newSupplierOffer = 0;
		for (TplSpecAnswer tplSpecAnswer : templateSpecResDTO
				.getSpecAnswerList()) {
			if (StringUtils.isNotBlank(tplSpecAnswer.getNewSupplierOffer())) {
				newSupplierOffer++;
			}
		}
		return newSupplierOffer;
	}
	// #29007 -end-

	// #29007 -start-
	private boolean checkItemAndZoneAdded(HashMap<Long, Long> itemZoneMap,
			Long itemId, Long specZoneId) {
		for (Map.Entry<Long, Long> itemZone : itemZoneMap.entrySet()) {
			Long eItemId = itemZone.getKey();
			Long eZoneId = itemZone.getValue();
			boolean alreadyAdded = (eItemId.equals(itemId) || eItemId == itemId)
					&& (eZoneId == specZoneId || eZoneId.equals(specZoneId));
			if (alreadyAdded) {
				return false;
			}
		}
		return true;
	}
	// #29007 -end-

	// #29007 -start-
	public boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());// 29007&31423
	}
	// #29007 -end-

	// #29007 -start-
	public void addNewOfferedSpec(TplSpecAnswer spec) {
		String specDet = spec.getTplSpecQuestion().getClSpecDetail();
		
		for (TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()) {
			if (tplSpecAnswer.getTplSpecQuestion().getClSpecDetail().equals(specDet)) {
				tplSpecAnswer.setRenderNewSpec(true);
			}
		}
	}
	// #29007 -end-

	// #29007 -start-
	public void refreshNewOfferedSpec(TplSpecAnswer spec) {
		String specDet = spec.getTplSpecQuestion().getClSpecDetail();

		for (TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()) {
			if (tplSpecAnswer.getTplSpecQuestion().getClSpecDetail().equals(specDet)) {
				tplSpecAnswer.setNewSupplierOffer(null);
				tplSpecAnswer.setRenderNewSpec(false);
			}
		}
	}
	// #29007 -end-

	// #29007 -start-
	public void resetAllNewOfferedSpecification() {
		RequestContext.getCurrentInstance().execute("resetAllConfirmPopUp.show()");
	}
	// #29007 -end-

	// #29007 -start-
	public void resetAllNewOfferedSpecificationSubmit() {
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();

		if (this.getManagedBean().getSpecKeyword() != null) {
			this.getManagedBean().setSpecKeyword(null);
			
			specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
			specReqDto.setScQt(this.getManagedBean().getScQt());
			specReqDto.setScLoiLoaItem(this.getManagedBean().getScLoiLoaItem());
			specReqDto.setSpecSearchForApp(true);

			TemplateSpecResDTO templateSpecResDTO = getTemplateSpecSO().searchSupplierSpecification(specReqDto);

			this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
		}

		for (TplSpecAnswer answer : this.getManagedBean().getTplSpecAnswerList()) {
			answer.setNewSupplierOffer(null);
		}

		this.getManagedBean().setNewSupplierJustification(null);
		this.getManagedBean().setResetAll(true);

		saveSupplierSpecification(false);
	}
	/* #29007 End Vinod */
	
	/* #29007 Start Vinod */
	/**
	 * replace all special characters and next line characters to html codes.
	 * @param str
	 * @return updatedStr
	 */
	public String replaceAllSpecialCharactersAndNewline(String str) {
		String updatedStr = str.replaceAll(TemplateSpecConst.LESS_THEN_SYMBOL, TemplateSpecConst.LESS_THEN_HTML_ENTITY);
		updatedStr = updatedStr.replaceAll(TemplateSpecConst.LESS_THEN_SYMBOL, TemplateSpecConst.LESS_THEN_HTML_ENTITY);
		updatedStr = updatedStr.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER,TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		updatedStr = updatedStr.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER_R,TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		return updatedStr;
	}
	/*#29007 End Vinod*/
	
	/*
	 * 29007 Start sorting LoiItems
	 */
	private void checkItemSorting(List<ScLoiLoaItem> loiLoaItem) {
		Collections.sort(loiLoaItem, new Comparator<ScLoiLoaItem>() {
			public int compare(ScLoiLoaItem loiItem1, ScLoiLoaItem loiItem2) {

				return new CompareToBuilder().append(loiItem1.getChangedDate(), loiItem2.getChangedDate())
						.toComparison();
			}
		});
	}
	// 29007 End
	
	/**
	 * 29007-start Get Parameter description by parameter for report.
	 * 
	 * @param parameter
	 *            the PmParameter instance.
	 * @return parameter description
	 */
	@Override
	public String getParameterDescForReport(PmParameter parameter) {
		if (parameter != null && StringUtils.isNotBlank(parameter.getParameterType())) {
			return getParameterDesc(parameter, myLocale);
		} else {
			return null;
		}
	}
	// #29007 -end-

	// #29007 -start-
	/**
	 * this method to display N/A or T/B based on language for Supplier remark.
	 * 
	 * @param supplierRemarks
	 * @return updated string
	 */
	public String supplierRemarks(String supplierRemarks) {
		String remarks = QTConstants.LAMPIRAN_A3_TEXT_TB;
		if (null != supplierRemarks && StringUtils.isNotBlank(supplierRemarks)
				&& StringUtils.isNotEmpty(supplierRemarks)) {
			return this.replaceAllSpecialCharactersAndNewline(supplierRemarks);
		}
		if (ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			remarks = QTConstants.LAMPIRAN_A3_TEXT_NA;
		}
		return remarks;
	}
	// #29007 -end-

	// #29007 -start-
	private void createLampiranA3forQTVerification(boolean isComplete) {
		AwardSupplierReqDTO awardReqDTO = new AwardSupplierReqDTO();
		List<ScLoaReport> oldLampiranA3List = new ArrayList<>();
		int recordCount = 0;

		if (isComplete) {
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					if (StringUtils.isNotBlank(report.getTitle())
							&& report.getTitle().contains(lampiranA3SubStr)
							&& null != report.getDefinationType()
							&& ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD.equals(report
									.getDefinationType().getParameterCode())) {
						report.setTitle(populateLabelLampiranA3(new SimpleDateFormat(
								CommonConstants.DEFAULT_DATETIME_FORMAT).format(DateUtils.getCurrentDate()),
								SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy()).getUserName()));
						report.setIslampiranA3(true);
						
						recordCount++;
					}
					
					oldLampiranA3List.add(report);
				}
			}
			
			if (recordCount > 0) {
				getManagedBean().getScLoa().setScLoaReports(oldLampiranA3List);
				
				if (null != this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()) {
					awardReqDTO.setScLoa(getManagedBean().getScLoa());

					AwardSupplierResDTO resDTO = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
					List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
					
					ScLoa scLoa = getManagedBean().getScLoa();
					scLoa.setScLoaReports(scLoaReports);
					
					getManagedBean().setScLoa(scLoa);
				}
			} else {
				ScLoaReport lampiranA3 = getQtAwardSO()
						.createLampiranA3(
								getManagedBean().getScLoa(),
								populateLabelLampiranA3(new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT)
										.format(DateUtils.getCurrentDate()),
										SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy())
												.getUserName()));
				lampiranA3.setIslampiranA3(true);
				lampiranA3.setScLoa(getManagedBean().getScLoa());

				IParameterSO parameterSO = null;
				
				try {
					parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);

					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
					reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD);
					
					ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
					
					lampiranA3.setDefinationType(resDTO.getPmParameter());
					lampiranA3.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER); // AQ
				} catch (Exception e) {
					SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
							SLALogConstant.QT_LOA_DETAIL_BACKINGBEAN,
							this.getManagedBean().getScQt().getQtNo(),
							SLALogConstant.QT_CREATE_LAMPIRANA3PDF_FOR_QT_VERIFICATION,
							getUserIdSessionFromLoginId(), e);
				}

				getManagedBean().getScLoa().getScLoaReports().add(lampiranA3);
				
				if (null != this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()) {
					awardReqDTO.setScLoa(getManagedBean().getScLoa());

					AwardSupplierResDTO resDTO = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
					List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
					
					ScLoa scLoa = getManagedBean().getScLoa();
					scLoa.setScLoaReports(scLoaReports);
					
					getManagedBean().setScLoa(scLoa);
				}
			}
		} else {
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					String title = StringUtils.isNotBlank(report.getTitle()) ? report.getTitle() : null;
					if (null!=title 
							&& title.contains(lampiranA3SubStr)) {
						report.setDeleted(true);
						
						oldLampiranA3List.add(report);
						
						recordCount++;
					}
				}
			}

			int loaItemslampirian = 0;

			for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
				if (loiLoaItem.isComplete()) {
					loaItemslampirian++;
					break;
				}
			}

			if (recordCount > 0 && loaItemslampirian == 0) {
				getManagedBean().getScLoa().getScLoaReports().removeAll(oldLampiranA3List);
				
				if (null != this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()) {
					ScLoa scLoa = new ScLoa();
					scLoa.setScLoaReports(oldLampiranA3List);
					
					awardReqDTO.setScLoa(scLoa);
					
					getQtAwardSO().deleteLampiranA3Report(awardReqDTO);
				}
			}
		}
	}
	// 29007-End
	
	// 34242, #29007 - Start
	public void retainAwardPage() {
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("form:itemtbl");

		if (null != dataTable) {
			this.getManagedBean().setRetainPageNumber(dataTable.getFirst());
		}
	}

	// #34242, #29007 - End
	
	// 29007 - fix issue 2 search action note 347 - start
	/**
	 * To calculate total price based on item search result
	 * 
	 * @return BigDecimal - Total Price of ScLoiLoaItem list
	 */
	public BigDecimal calculateTotalItemPrice() {
		BigDecimal totalPrice = BigDecimal.ZERO;

		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getLoiLoaItemsResult())) {
			for (ScLoiLoaItem item : this.getManagedBean()
					.getLoiLoaItemsResult()) {
				totalPrice = calcSubTotalPrice(item).add(totalPrice);
			}
		}

		totalPrice = totalPrice.setScale(2, BigDecimal.ROUND_UP);

		return totalPrice;
	}

	// 29007 - fix issue 2 search action note 347 - end

	// Bug #34757, #29007 - Start
	/**
	 * retrieve latest item code from LOA detail
	 * 
	 * @param reqItem
	 * @return
	 */
	public String getLatestItemCode(ScRequestItem reqItem) {
		// #34757 note-#41 - Start
		for (ScLoiLoaItem item : this.getManagedBean().getLoiLoaItemsResult()) {
			if (item.getScRequestItem().getRequestNoteItemId()
					.equals(reqItem.getRequestNoteItemId())) {
				item.getScRequestItem().setCmItem(reqItem.getCmItem());
			}
		}
		// #34757 note-#41 - End

		if (reqItem.getTplSpecQuestion().getClPriceTypeCode()
				.getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER)
				|| reqItem.getTplSpecQuestion().getClPriceTypeCode()
						.getParameterCode()
						.equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)) {
			return getQtAwardSO().getCmItemExtensionCode(reqItem);
		} else {
			return reqItem.getCmItem().getExtensionCode();
		}
	}

	// Bug #34757, #29007 -end-

	// Bug #34757, #29007 -start-
	/**
	 * decide whether to show 'Assign' button or not show button if data is not
	 * tier qty
	 * 
	 * @param loiLoaItem
	 * @return
	 */
	public boolean isShowAssignButton(ScLoiLoaItem loiLoaItem) {
		// 35284 - start
		if (loiLoaItem == null) {
			return false;
		}
		// 35284 - end

		if (!this.getManagedBean().getScLoa().getScLoiLoa().getDocType()
				.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)) {
			return isShowAssignButtonIfNotPR(loiLoaItem);
		} else {
			return true;
		}
	}

	// Bug #34757, #29007 - End

	// Bug #34757, #29007 -start-
	private boolean isShowAssignButtonIfNotPR(ScLoiLoaItem loiLoaItem) {
		boolean isShow = false;

		if (loiLoaItem.getTplSpecZoneDtl() != null) {
			if (this.getManagedBean().getFirstZone() == null) {
				this.getManagedBean().setFirstZone(
						loiLoaItem.getTplSpecZoneDtl().getZoneName());
			}

			if (this.getManagedBean().getFirstZone()
					.equals(loiLoaItem.getTplSpecZoneDtl().getZoneName())) {
				isShow = true;
			}

			isShow = checkItemList(loiLoaItem);
		} else {
			isShow = true;
		}

		// 35284 - start
		boolean isFromQty = null != loiLoaItem.getTplSpecTierQty()
				&& null != loiLoaItem.getTplSpecTierQty().getFromQty() ? loiLoaItem
				.getTplSpecTierQty().getFromQty().compareTo(BigDecimal.ONE) == 0
				: false;
		boolean isSpecTierQty = null == loiLoaItem.getTplSpecTierQty()
				|| isFromQty;
		isShow = isSpecTierQty && isShow;
		// 35284 - end

		return isShow;
	}

	// Bug #34757, #29007 - End

	// Bug #34757, #29007 -start-
	private boolean checkItemList(ScLoiLoaItem loiLoaItem) {
		if (this.getManagedBean().getItemList() == null) {
			this.getManagedBean().setItemList(new ArrayList<Long>());
		}

		if (this.getManagedBean().getLoaItemIds() == null) {
			this.getManagedBean().setLoaItemIds(new ArrayList<Long>());
		}

		if (!this.getManagedBean().getItemList()
				.contains(loiLoaItem.getScRequestItem().getRequestNoteItemId())) {
			this.getManagedBean().getItemList()
					.add(loiLoaItem.getScRequestItem().getRequestNoteItemId());
			this.getManagedBean().getLoaItemIds()
					.add(loiLoaItem.getLoiLoaItemId());
		}

		return this.getManagedBean().getLoaItemIds()
				.contains(loiLoaItem.getLoiLoaItemId());
	}
	// Bug #34757, #29007 - End
	
}
