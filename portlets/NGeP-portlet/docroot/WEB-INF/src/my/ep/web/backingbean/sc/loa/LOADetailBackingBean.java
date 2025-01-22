package my.ep.web.backingbean.sc.loa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode; // Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
import java.sql.Timestamp;
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
import java.util.TreeMap; //Bug #34882@Syazani 26/06/2023

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
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
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
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateDocConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.SpecialCharacter;
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
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
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
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sc.common.ScActor;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaForQt;
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
import my.ep.persistence.entity.sm.SmSupplierCategory;
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
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean; //#25790
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
import my.ep.web.util.NumberWordUtilEn;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

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
	
	//#29006 - start
	private String notAvailabilityStr = MALAY_NA;
	private String selectedSupplierOffer = StringUtils.EMPTY;
	private String itemNameSearchCriteria;
	private List<ScLoiLoaItem> completeScLoiLoaItemList;
	private ScLoiLoaItem selectedScLoiLoaItem;
	private String lampiranA3SubStr = "Lampiran A3";
	//#29006 - end
	private static final Locale myLocale = new Locale(SourcingConstants.MALAY_LANGUAGE_CODE,
			  SourcingConstants.MALAYSIA_COUNTRY_CODE);//29007

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
		//#29173 - CR 26475: Static Code Analysis
		//start
		//#29173 - removed try-finally clause as it is not needed. exception are always thrown in main code
		SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
				.getPayLoad();

		String language = new FacesScopes().getUserLocale().getLanguage();

		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setScLoa(scLoa);
		reqDTO.setLoaId(scLoa.getLoaId()); //27797
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
		//end #29173 - CR 26475: Static Code Analysis
		
		//#29006 - start
		if (Arrays.asList(QTStatusConst.LOA_PENDING_SUBMISSION, QTStatusConst.LOA_PENDING_REVISION,
				QTStatusConst.LOA_CANCELLED, QTStatusConst.LOA_PENDING_APPROVAL,
				QTStatusConst.LOA_PENDING_REVISION_APPROVAL).contains(scLoa.getPmStatus().getStatusId())) {
			getManagedBean().setSpecificationAndSupplierDialogFlag(true);
			getManagedBean().setUpdateSupplierInstructionFlag(false); //avoid duplicate flag
		}

		if (Arrays.asList(QTStatusConst.LOA_PENDING_VERIFICATION, QTStatusConst.LOA_COMPLETED).contains(
				scLoa.getPmStatus().getStatusId())) {
			getManagedBean().setUpdateSupplierInstructionFlag(true);
			getManagedBean().setSpecificationAndSupplierDialogFlag(false); //avoid duplicate flag
		}
		
		if (Arrays.asList(QTStatusConst.LOA_PENDING_ACKNOWLEDGE_RECEIPT_SUPPLIER_ACTION,
				QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT).contains(scLoa.getPmStatus().getStatusId())) {
			getManagedBean().setSpecificationAndSupplierDialogFlag(false);
			getManagedBean().setUpdateSupplierInstructionFlag(false);
		}
		
		//#31869 - start (comment out removeUnwantedLampiranA3Report)
		//removeUnwantedLampiranA3Report(1);
		//#31869 - end
		//#29006 - end
		//#29007 Start Vinod
		if (Arrays.asList(QTStatusConst.LOA_PENDING_SUBMISSION, QTStatusConst.LOA_PENDING_VERIFICATION,
				QTStatusConst.LOA_PENDING_APPROVAL, QTStatusConst.LOA_PENDING_REVISION,
				QTStatusConst.LOA_PENDING_REVISION_APPROVAL, QTStatusConst.LOA_COMPLETED).contains(
				scLoa.getPmStatus().getStatusId())) {// 29007,#32119
			getManagedBean().setSpecificationAndSupplierDialogFlagQT(true);
		}
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		String message = ResourceBundleUtils.getMessage(QTConstants.LBL_SC_LOILOA_SUPPLIERS_PROPOSAL_SPECIFICATION_UPDATE,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
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
		if (Arrays.asList(QTStatusConst.LOA_PENDING_VERIFICATION, QTStatusConst.LOA_COMPLETED).contains(
				scLoa.getPmStatus().getStatusId())) {// 29007,#32119-start
			getManagedBean().setSupPropSpecUpdate(true);
		}// 29007-End
		//#29007 End Vinod
		
		// #27797 - start
		if (isZonalWithPaOrPs()) {//29006 - change condition
			if(scLoiLoa.isZonal()){
				//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				Map<Long, Integer> reqItemRowSpanMap = new HashMap<>();
				Map<Long, BigDecimal> reqItemMaxTotalMap = new HashMap<>();
				//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				AwardSupplierResDTO itemsResDTO = getQtAwardSO().searchScLoiLoaItemByLoaId(reqDTO);
				
				//29006 - start
				if(itemsResDTO.getScLoiLoaItemList().size()==0){
					List<ScLoiLoaItem> tempScLoiLoaItemList = scLoa.getScLoiLoaItems();
					
					//sort by scLoiLoaItem.getScRequestItem().getRequestNoteItemId()
					Collections.sort(tempScLoiLoaItemList, new Comparator<ScLoiLoaItem>() {
						@Override
						public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
							return Long.compare(s1.getScRequestItem().getRequestNoteItemId(), s2.getScRequestItem()
									.getRequestNoteItemId());
						}
					});
					itemsResDTO.setScLoiLoaItemList(tempScLoiLoaItemList);
				}
				//29006 - end
				
				//to set rowspan value / max sub total for every RequestNoteItemId
				for (ScLoiLoaItem item : itemsResDTO.getScLoiLoaItemList()) {
					BigDecimal subPrice = calcSubTotalPrice(item);
					
					if (reqItemRowSpanMap.containsKey(item.getScRequestItem().getRequestNoteItemId())) {
						reqItemRowSpanMap.put(item.getScRequestItem().getRequestNoteItemId(),reqItemRowSpanMap.get(item.getScRequestItem().getRequestNoteItemId()) + 1);
					} else {
						reqItemRowSpanMap.put(item.getScRequestItem().getRequestNoteItemId(), 1);
					}
					
					if (reqItemMaxTotalMap.containsKey(item.getScRequestItem().getRequestNoteItemId())) {
						int result = subPrice.compareTo(reqItemMaxTotalMap.get(item.getScRequestItem()
								.getRequestNoteItemId()));
						if (result == 1) {
							reqItemMaxTotalMap.put(item.getScRequestItem().getRequestNoteItemId(), subPrice);
						}
					} else {
						reqItemMaxTotalMap.put(item.getScRequestItem().getRequestNoteItemId(), subPrice);
					}
				}
				
				//count total sub
				BigDecimal totalZonePrice = BigDecimal.ZERO; 
				for(BigDecimal value: reqItemMaxTotalMap.values())
					totalZonePrice = totalZonePrice.add(value);
				
				this.getManagedBean().setNameZoneScLoiLoaItemList(itemsResDTO.getScLoiLoaItemList());
				this.getManagedBean().setReqItemMaxSubTotalMap(reqItemMaxTotalMap);
				this.getManagedBean().setReqItemRowSpanMap(reqItemRowSpanMap);
				this.getManagedBean().setReqItemSession(0L);
				this.getManagedBean().setReqItemSubTotalSession(0L);
				this.getManagedBean().setTotalZonePrice(totalZonePrice);
				this.getManagedBean().getScLoa().setLoaAmt(getManagedBean().getTotalZonePrice());//27797 & 29006
				
				if (scLoa.getSstAmt() != null) {
					//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					BigDecimal overallAmt = BigDecimal.ZERO;
					overallAmt = totalZonePrice.add(scLoa.getSstAmt());
					overallAmt = overallAmt.setScale(2, RoundingMode.HALF_EVEN);
					//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]

					this.getManagedBean().setOverallContractAmt(overallAmt);

				} else {
					this.getManagedBean().setOverallContractAmt(totalZonePrice);
				}
			}
		}
		// #27797 - end
		// #34915 idham @01/08/2023 - Start
		// sort by scLoiLoaItem.getTplSpecZoneDtl() and scLoiLoaItem..getTplSpecTierQty()
		if (null != this.getManagedBean().getScLoa()
				&& !this.getManagedBean().getScLoa().getScLoiLoa().getDocType()
						.equals(ParameterConst.DOCTYPE_PURCHASE_REQUEST)
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoaItems())) {
			sortLoiLoaItems(this.getManagedBean().getScLoa().getScLoiLoaItems(), this.getManagedBean().getScLoa()
					.getScQt().isZonal());
		}
		// #34915 idham @01/08/2023 - End
		removeUnwantedLampiranA3Report(1); //#29006 - note 650
		// Bug 34404 - related to 29007 - start
		if (this.getManagedBean().getScLoa() != null) {
			this.getManagedBean().setLoiLoaItemsResult(
					new ArrayList<ScLoiLoaItem>(this.getManagedBean().getScLoa().getScLoiLoaItems()));// 29007 - fix issue 2 search action note 347
		} else {
			this.getManagedBean().setLoiLoaItemsResult(null);
		}
		// Bug 34404 - related to 29007 - end
		new QtLOABackingBean().isSupplierEPVStatusRejected();//#36339, #21692
		return QTConstants.LOA_PAGE;
	}
	
	/**
	 * Enhancement #21694
	 * to be used in Screen From Tasklist Only
	 * @param obj
	 * 		instance of ScQt/ScLoiForQt/ScLoaForQt
	 * @param previousPageUrl
	 * @return
	 */
	public String reverseAction() {
		
		debugLogger("viewReverseStatusDetails");
		FacesContext fc = FacesContext.getCurrentInstance();
		QTDetailBackingBean qtDtl = new QTDetailBackingBean();
		ScLoa scLoa = this.getManagedBean().getScLoa();
		//String docType = getManagedBean().getQtnTenderSearchBean().getResultDocType();
		ScQt scQt = this.getManagedBean().getScQt();
		ScLoaForQt scLoaForQt = new ScLoaForQt();
		
		/**
		 * FINDING QT WORKFLOW-> CASE REVERSAL QT
		 */
		IScWorkFlowStatusSO workFlowStatusSO;
		ScWorkFlowStatusReqDTO scWreqDTO = new ScWorkFlowStatusReqDTO();
		workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);	
		scWreqDTO.setDocId(scQt.getQtId());		
		scWreqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		ScWorkFlowStatus qtcurrentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(scWreqDTO).getWorkFlowStatus();
		if (!qtcurrentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.QT_PENDING_LOA)) {
			throw new EpException(MessageConst.ERROR_760497,new String[]{scQt.getQtNo()});
		}
		
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setLoaId(scLoa.getLoaId());
		AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLoaForQtByLoaId(reqDTO);
		
		scLoaForQt = resDTO.getScLoaForQt();
		
		qtDtl.getManagedBean().setEditable(Boolean.TRUE);		
			
		Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
		otherArgs.put(MapKeyEnum.loiLoaSelected, scLoa);
		
		/* FINDING WORKFLOW STATS*/
		scWreqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
		scWreqDTO.setDocId(scLoa.getLoaId());
		
		ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(scWreqDTO).getWorkFlowStatus();
		qtDtl.getManagedBean().setPreviousPage(this.getManagedBean().getBackToPreviousPage());
		qtDtl.getManagedBean().setQt(scQt);
		qtDtl.getManagedBean().setScLoaForQt(scLoaForQt);
		qtDtl.getManagedBean().setStatus(getStatusDescById(currentWorkFlowStatus.getPmStatus().getStatusId()));
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, qtDtl.reverseQtDetails(Boolean.FALSE,Boolean.TRUE,qtDtl));
		
		return null;
	}
	
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
		// #26226 - Start
		Map<MapKeyEnum, Object> otherArgs = null;
		if (null != getManagedBean().getCurrentStatusId() 
				&& getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.longValue()) {
			otherArgs = new HashMap<>();
			otherArgs.put(MapKeyEnum.enableDownloadAllDocuments, Boolean.TRUE);
		}
		return retrieveSupplierLOADtlPriv(sourceId, loaId, previousPageUrl, isReadonly, otherArgs);
		// #26226 - End
	}
	
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
		debugLogger("retrieveSupplierLOADtl");
		try {
			//to avoid otherArgs is null checking next time
			if(otherArgs == null){
				otherArgs = new HashMap<MapKeyEnum, Object>();
			}
			getManagedBean().setSourceId(sourceId);
			getManagedBean().setBackToPreviousPage(previousPageUrl);
			getManagedBean().setReadOnly(isReadonly);
			if(otherArgs.containsKey(MapKeyEnum.enableDownloadAllDocuments) && otherArgs.get(MapKeyEnum.enableDownloadAllDocuments) instanceof Boolean){
				Object o = otherArgs.get(MapKeyEnum.enableDownloadAllDocuments);
				if(o instanceof Boolean){	getManagedBean().setEnableDownloadAllDocuments((Boolean)o);	}
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
				}else{
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
				
				// #25726#323 - Start
				if (scLoa.getScQt() == null) {
					scLoa.setScQt(scQt);
				}
				// #25726#323 - End
				
				scLoa = calcBondAmountWithSST(scLoa, Boolean.FALSE);

				// #35977 - Start
				if (scLoa.getBondAmt() != null
						&& ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes()
								.getUserLocale().getLanguage())) {
					getManagedBean()
							.setBondAmountWord(
									NumberWordUtilEn.convert(scLoa.getBondAmt()
											.toString()));
				} else if (scLoa.getBondAmt() != null
						&& ReportConst.MALAY_LANGUAGE_CODE.equals(new FacesScopes()
								.getUserLocale().getLanguage())) {
					getManagedBean().setBondAmountWord(
							NumberWordUtil.convert(scLoa.getBondAmt().toString()));
				} else {
					getManagedBean().setBondAmountWord(null);
				}
				// #35977 - End
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
			//Bug 34404 - related to 29007 - start
			if(this.getManagedBean().getScLoa()!=null){
				this.getManagedBean().setLoiLoaItemsResult(new ArrayList<ScLoiLoaItem>(this.getManagedBean().getScLoa().getScLoiLoaItems()));//29007 - fix issue 2 search action note 347			
			}else{
				this.getManagedBean().setLoiLoaItemsResult(null);
			}
			//Bug 34404 - related to 29007 - end
			
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
			
			// #29006 - start
			//#31869 - start (comment out removeUnwantedLampiranA3Report)
			removeUnwantedLampiranA3Report(1); //#29006 - note 650
			//#31869 - end
			getManagedBean().setDownloadedLoaReportsTitleList(new ArrayList<String>());
			//note 870,872 - start
			// bug fixing on report list especially lampiran A3 naming if user
			// generate lampiran a3 and reload the page through task list
			if (resDTO.getScLoa() != null
					&& ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(getManagedBean().getScLoa().getScLoiLoa()
							.getDocType()) && getManagedBean().getScLoa().getScLoaReports().size() == 5) {
				AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
				awardSupplierReqDTO.setDocId(getManagedBean().getScLoa().getLoaId());
				
				AwardSupplierResDTO awardSupplierResDTO =  getQtAwardSO().retrieveLOAReportList(awardSupplierReqDTO);
				List<ScLoaReport> scLoaReports = awardSupplierResDTO.getLoaReports();
				
				ScLoa scLoa = getManagedBean().getScLoa();
				scLoa.setScLoaReports(scLoaReports);
				getManagedBean().setScLoa(scLoa);
				
			}
			//note 870,872 - end
			// #29006 - end
			
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
		//#29007 Start Vinod
		if (Arrays.asList(QTStatusConst.LOA_PENDING_SUBMISSION, QTStatusConst.LOA_PENDING_REVISION).contains(
				this.getManagedBean().getScLoa().getPmStatus().getStatusId())) {
			getManagedBean().setSpecificationAndSupplierDialogFlagQT(true);
		}
		ScLoiLoa scLoiLoa = this.getManagedBean().getScLoa().getScLoiLoa();
		String message=	ResourceBundleUtils.getMessage("lbl.sc.loiloa.suppliers.proposal.specification.update",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());
		if (scLoiLoa.getScLois() != null) {
			for (ScLoi scLoi : scLoiLoa.getScLois()) {
				for(ScLoiTerm terms : scLoi.getScNegoTerms()){
					if(terms.getTermDesc().contains(message) && this.getManagedBean().getScLoa().getPmStatus().getStatusId() != QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT){
						getManagedBean().setSupPropSpecUpdate(true);
						break;
					}
				}
			}
		}
		
		// #27797 - start
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setLoaId(loaId);
		
		if (isZonalWithPaOrPs()) {//29006 - change condition
			if(scLoiLoa.isZonal()){
				//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				Map<Long, Integer> reqItemRowSpanMap = new HashMap<>();
				Map<Long, BigDecimal> reqItemMaxTotalMap = new HashMap<>();
				//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				AwardSupplierResDTO itemsResDTO = getQtAwardSO().searchScLoiLoaItemByLoaId(reqDTO);
				
				//29006 - start
				if(itemsResDTO.getScLoiLoaItemList().size()==0){
					List<ScLoiLoaItem> tempScLoiLoaItemList = this.getManagedBean().getScLoa().getScLoiLoaItems();
					
					//sort by scLoiLoaItem.getScRequestItem().getRequestNoteItemId()
					Collections.sort(tempScLoiLoaItemList, new Comparator<ScLoiLoaItem>() {
						@Override
						public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
							return Long.compare(s1.getScRequestItem().getRequestNoteItemId(), s2.getScRequestItem()
									.getRequestNoteItemId());
						}
					});
					itemsResDTO.setScLoiLoaItemList(tempScLoiLoaItemList);
				}
				//29006 - end
				
				//to set rowspan value / max sub total for every RequestNoteItemId
				for (ScLoiLoaItem item : itemsResDTO.getScLoiLoaItemList()) {
					BigDecimal subPrice = calcSubTotalPrice(item);
					
					if (reqItemRowSpanMap.containsKey(item.getScRequestItem().getRequestNoteItemId())) {
						reqItemRowSpanMap.put(item.getScRequestItem().getRequestNoteItemId(),reqItemRowSpanMap.get(item.getScRequestItem().getRequestNoteItemId()) + 1);
					} else {
						reqItemRowSpanMap.put(item.getScRequestItem().getRequestNoteItemId(), 1);
					}
					
					if (reqItemMaxTotalMap.containsKey(item.getScRequestItem().getRequestNoteItemId())) {
						int result = subPrice.compareTo(reqItemMaxTotalMap.get(item.getScRequestItem()
								.getRequestNoteItemId()));
						if (result == 1) {
							reqItemMaxTotalMap.put(item.getScRequestItem().getRequestNoteItemId(), subPrice);
						}
					} else {
						reqItemMaxTotalMap.put(item.getScRequestItem().getRequestNoteItemId(), subPrice);
					}
				}
				
				//count total sub
				BigDecimal totalZonePrice = BigDecimal.ZERO; 
				for(BigDecimal value: reqItemMaxTotalMap.values())
					totalZonePrice = totalZonePrice.add(value);
				
				this.getManagedBean().setNameZoneScLoiLoaItemList(itemsResDTO.getScLoiLoaItemList());
				this.getManagedBean().setReqItemMaxSubTotalMap(reqItemMaxTotalMap);
				this.getManagedBean().setReqItemRowSpanMap(reqItemRowSpanMap);
				this.getManagedBean().setReqItemSession(0L);
				this.getManagedBean().setReqItemSubTotalSession(0L);
				this.getManagedBean().setTotalZonePrice(totalZonePrice);
				this.getManagedBean().getScLoa().setLoaAmt(getManagedBean().getTotalZonePrice());//27797 & 29006
				
				if (this.getManagedBean().getScLoa().getSstAmt() != null) {
					//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					BigDecimal overallAmt = BigDecimal.ZERO;
					overallAmt = totalZonePrice.add(this.getManagedBean().getScLoa().getSstAmt());
					overallAmt = overallAmt.setScale(2, RoundingMode.HALF_EVEN);
					//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]

					this.getManagedBean().setOverallContractAmt(overallAmt);

				} else {
					this.getManagedBean().setOverallContractAmt(totalZonePrice);
				}
			}
		}
		// #27797 - end

		if(getManagedBean().getScLoa().getScLoaReports() != null){
			List<ScLoaReport> loaReports = getManagedBean().getScLoa().getScLoaReports();
			for(ScLoaReport scLoaReport : loaReports){
				if(scLoaReport.getTitle().contains(lampiranA3SubStr)){
					scLoaReport.setUploadlampiranA3(true);
				}
			}
		}
		//#29007 End Vinod

		
		if(null!=this.getManagedBean().getScLoa()){//#29826-start
			this.getManagedBean().setFlowamt(this.getManagedBean().getScLoa().isFollowLoaAmt());
		}//#29826-End
		
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
	private void freqScheduleValidation(ScQtSchedule scQtSchedule, String docType) {
		debugLogger("frequencyScheduleValidation");
		FacesScopes facesScopes = new FacesScopes();
		Calendar startDate = null;
		Calendar endDate = null;

		if(scQtSchedule.getStartMonth() == null || scQtSchedule.getStartMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(scQtSchedule.getStartYear() == null || scQtSchedule.getStartYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.year",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(scQtSchedule.getEndMonth() == null || scQtSchedule.getEndMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(scQtSchedule.getEndYear() == null || scQtSchedule.getEndYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.year",
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
				//#29474 - start
				if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(docType)) {
					throw new EpException(MessageConst.ERROR_760347);
				}
				//#29474 - end
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
		
		// #26247 - #33073 - #33354 - Start.
		if (null != scLoa && null != scLoa.getScLoiLoa() && null != scLoa.getScLoiLoa().getSmSupplier()){
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				
				if (null != qtSuppFinalizationSO) {
					SmSupplier smSupplier = qtSuppFinalizationSO.retrieveActiveDisciplinaryActionBySupplier(scLoa.getScLoiLoa().getSmSupplier());
					scLoa.getScLoiLoa().setSmSupplier(smSupplier);
				}
			} finally {
				qtSuppFinalizationSO = null;
			}
		}
		// #26247 - #33073 - #33354 - End.
		
		getManagedBean().setScLoa(scLoa);
		getManagedBean().setScQt(resDTO.getScQt());

		// calc total award details
		if(scLoa != null && scLoa.getScLoiLoaItems() != null && !scLoa.getScLoiLoaItems().isEmpty()){//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
			scLoa.setLoaAmt(calcTotalAmount(scLoa.getScLoiLoaItems()));
		}

		//Supplier overall contract amount
		//Enhancement #24259 #24269
//		BigDecimal overallCtAmout = overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt());
		//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
		BigDecimal overallCtAmout = null;
		if (scLoa !=null )
			overallCtAmout = overallCtAmountWithSST(scLoa.getLoaAmt(), scLoa.getSstAmt());

		log.info(":::populateLOADetails() overallCtAmout (RM) = "+ overallCtAmout);
		//end Enhancement #24259 #24269
		getManagedBean().setOverallContractAmt(overallCtAmout);
		if (scLoa != null ) //20230706@asyrafhaziq- SQUBE Bug NullPointer#3 
		    scLoa.setOverallContractAmt(overallCtAmout);

		// Procurement Type Category Id
		if(scLoa != null && null == scLoa.getProcurementTypeCatId()){ //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
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
		if(scLoa != null && scLoa.getScLoiLoa().getScLois() != null  //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
				&& !scLoa.getScLoiLoa().getScLois().isEmpty()){
			scLoa.setSupplierBranch(scLoa.getScLoiLoa().getScLois().get(0).getSupplierBranch());
			scLoa.setLoiCreated(Boolean.TRUE);
		}
		handleBranchDetailsChanged(false);

		// Supplier Witness
		ScLoaWitness loaSupplierWitness = null;
		if(scLoa != null && scLoa.getScLoaWitnesses() != null){ //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
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
		//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
				if (scLoa != null && (QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa
						.getPmStatus().getStatusId()) ||
						QTStatusConst.LOA_PENDING_REVISION.equals(scLoa
						.getPmStatus().getStatusId()))) {
			Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOA_RESPONSE_PERIOD);
			scLoa.setAckDueDate(DateUtils.addDays(DateUtils.getCurrentDateWithoutTime(), loaRespPeriod));
		}

		// check whether loa from s1 or s2.
				if(scLoa != null && scLoa.getScLoiLoa() != null){ //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
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
		if(scLoa != null && scLoa.getScQtDeductions() != null && !scLoa.getScQtDeductions().isEmpty()){ //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
			getManagedBean().setSelectedScQtDeduction(scLoa.getScQtDeductions().get(0));
		}

		if (scLoa != null && scLoa.getContractType().getParameterId() != null) { //20230706@asyrafhaziq- SQUBE Bug NullPointer#3s
			this.getManagedBean().setContractTypeId(scLoa.getContractType().getParameterId());
			contractTypeChanged();
		}

		// Contract Signer
		prepareSignerInfo();

		// Approver
		ScLoaApproval scLoaApproval = scLoa != null ? scLoa.getScLoaApproval():null; //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
		PmUser approverUser = null;
		if (scLoaApproval != null) {
			approverUser = scLoaApproval.getApprover();

			if (scLoa != null && !QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
					.getPmStatus().getStatusId())) {
				getManagedBean().setScLoaApproval(scLoa.getScLoaApproval());
			}
		}
		if (approverUser == null) {
			approverUser = new PmUser();
		}
		getManagedBean().setApproverUser(approverUser);
		getManagedBean().setApproverList(resDTO.getApproverList());

		// Remarks
		if (scLoa != null){ //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
			scLoa.setScLoaRemarks(initializeRemarks(scLoa, 
				scLoa.getCurrentStatus(),
				scLoa.getScLoaRemarks(), ScLoaRemark.class));
		}
		// #11134 - start
		List<ScLoaRemark> supplierRemarks = filterSupplierRemarks(scLoa != null ?scLoa.getScLoaRemarks():null, false);//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
		getManagedBean().setSupplierRemarks(supplierRemarks);
		// #11134 - end
		
		//#25725 start
		// check agreement
		if(scLoa != null && scLoa.getLoaId() == null){ //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
			checkAgreementMandatory();			
		}else{			
			ScLoa loa = getManagedBean().getScLoa();
			String fulfilmentType = loa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode();			
			boolean isPeriodic = (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(fulfilmentType))||(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(fulfilmentType));
			boolean isNonPeriodic = (ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN.equals(fulfilmentType))||(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED.equals(fulfilmentType));
			boolean agreementReq = getManagedBean().isAgreementMandatory();
										
			if (isPeriodic){
				if(!agreementReq){
					getManagedBean().setAgreementReqChange(true);
				}					
			}else if(isNonPeriodic && !over500K(loa)){
				if(agreementReq){
					getManagedBean().setAgreementReqChange(true);
				}					
			}else if(isNonPeriodic && over500K(loa)){
				if(!agreementReq){
					getManagedBean().setAgreementReqChange(true);
				}					
			}else{
				getManagedBean().setAgreementReqChange(false);
			}			
		}
		//#25725 end

		// check performance bond		
		//Bug #29885 start isPendingSubmissionStatus or isPendingRevisionStatus
		//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
				if (scLoa != null && (QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa.getPmStatus().getStatusId()) || 
						QTStatusConst.LOA_PENDING_REVISION .equals(scLoa.getPmStatus().getStatusId()))){
			checkPerformanceBondMandatory();
		}
		//Bug #29885 end
		
		//Enhancement #24259 #24269
//		scLoa = calcBondAmount(scLoa, false);
		scLoa = calcBondAmountWithSST(scLoa, false);
		
		// #35977 - Start
		if (scLoa.getBondAmt() != null
				&& ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes()
						.getUserLocale().getLanguage())) {
			getManagedBean()
					.setBondAmountWord(
							NumberWordUtilEn.convert(scLoa.getBondAmt()
									.toString()));
		} else if (scLoa.getBondAmt() != null
				&& ReportConst.MALAY_LANGUAGE_CODE.equals(new FacesScopes()
						.getUserLocale().getLanguage())) {
			getManagedBean().setBondAmountWord(
					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		} else {
			getManagedBean().setBondAmountWord(null);
		}
		// #35977 - End
		
		
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
				log.error(e.toString());
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
			//#25726 remove unnecessary code
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
	
	//#25725 - start
	public void fileUpload(FileUploadEvent event)
		throws FileNotFoundException, IOException {
		debugLogger("FileUpload");
		FacesScopes facesScopes = new FacesScopes();
		if (event.getFile() != null) {
			ScLoa scLoa = getManagedBean().getScLoa();			
			ScLoaAttachment attachment = new ScLoaAttachment();							
			
			attachment.setScLoa(scLoa);	
			// #25726 START
			if(getManagedBean().getScLoa().getProcurementMode().getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)){
				attachment.setDocNo(scLoa.getScQuote().getQuoteNo());
			}else{
				attachment.setDocNo(scLoa.getScQt().getQtNo());
			}
			// #25726 END
			attachment
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			attachment.setCreatedUser(facesScopes.getUserSessionBean());
			uploadAttachment(event.getFile(), attachment);
			scLoa.setScLoaAttachment(attachment);
			}
	}
	
	public void fileDownload(){
		debugLogger("FileDownload");
		ScAttachment scAttachment = getManagedBean().getScLoa().getScLoaAttachment();
		if(scAttachment != null){
			try{
				downloadFile(scAttachment);
			}
			catch(IOException ex){
				if(ex instanceof FileNotFoundException){
					handleException(new EpException(MessageConst.ERROR_760411, new String[]{scAttachment.getFileName()}));
				}
				
			}
			catch(EpException ex){
				handleException(ex);
			}
		}
	}
	
	public void fileDelete(){
		debugLogger("FileDelete");
		getManagedBean().getScLoa().setScLoaAttachment(null);
	}
	
	//#25725 - end

	/**
	 * Download report as pdf or xlsx files in Letter of Acceptance.
	 *
	 * @param scLoaReport the sc loa report
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadReport(ScLoaReport scLoaReport) throws IOException {
		debugLogger("downloadReport");
		// #29006 - start
		if (scLoaReport.getTitle().contains(lampiranA3SubStr) && !ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa().getDocType())){//29007&30963
			try {
				//#31425 - start
				if(scLoaReport.getScLoaReportAttachment().getDocNo() == null){
					scLoaReport.getScLoaReportAttachment().setDocNo(getManagedBean().getScLoa().getLoaNo());
				}
				//#31425 - end
				generateUpdatedLampiranA3FromVelocity((ScAttachment) scLoaReport.getScLoaReportAttachment(),
						getManagedBean().getScLoa(), this.getManagedBean().getScQt());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// #29006 - end
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();
		if(scLoaReport.getTitle().equalsIgnoreCase(QTConstants.REPORT_ACKNOWLEDGEMENT_LOA_RECEIPT) &&
				getManagedBean().getScLoa().getPmStatus().getStatusId().
				equals(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT)) {
			if(this.getManagedBean().isValidatewitness()){ //#19336
			validateLoaWitnessAndAckBy();
			} //#19336
		}
		
		/*if (Arrays.asList(QTStatusConst.LOA_PENDING_VERIFICATION).contains(this.getManagedBean().getScLoa().getPmStatus().getStatusId()) && scLoaReport.getTitle().contains(lampiranA3SubStr) && ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa().getDocType())){//29007-start
			//scLoaReport.setScLoaReportAttachment(null);//29007	
		}*///29007-End
		ScLoaReportAttachment scLoaReportAttachmentGenerated = generateDocumentAttachment(scLoaReport);
		if(scLoaReport.getDefinationType().getParameterCode().equals(
				ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD) ||
				scLoaReport.getDefinationType().getParameterCode().equals(
						ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD)) {
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
		}
		if(scLoaReportAttachmentGenerated != null) {
			// #29006 - start
			String prefixName = scLoaReport.getTitle().contains(lampiranA3SubStr) ? scLoaReport.getTitle().replaceAll(
					"/", "-") : scLoaReport.getTitle().replaceAll(SourcingConstants.REG_EXP_WHITE_SPACE, "");
			// #29006 - end
			//#25025&29006 - start		
			scLoaReportAttachmentGenerated.setTempFileName(setValueLowercase(prefixName,18).trim());
			//#25025&29006 - end
		}
		
		//#29006 - start
		if (StringUtils.isNotBlank(scLoaReport.getTitle()) && scLoaReport.getTitle().contains(lampiranA3SubStr)) {
			getManagedBean().setUpdatedLampiranA3DownloadedFlag(true);
		}
		
		// Add current report title into
		// managedBean.downloadedLoaReportsTitleList
		// only if the title does not exist in the said list
		if (CollectionUtils.isEmpty(getManagedBean().getDownloadedLoaReportsTitleList())) {
			getManagedBean().setDownloadedLoaReportsTitleList(new ArrayList<String>());
			getManagedBean().getDownloadedLoaReportsTitleList().add(scLoaReport.getTitle());
		} else {
			if (!getManagedBean().getDownloadedLoaReportsTitleList().contains(scLoaReport.getTitle())) {
				getManagedBean().getDownloadedLoaReportsTitleList().add(scLoaReport.getTitle());
			}
		}
		//#29006 - end
		
		try{
			downloadFile(scLoaReportAttachmentGenerated);			
			//#25726 start							
			scLoaReport.setIsPK42BondCalc(true);
			//#25726 end			
		}
		catch(IOException ex){
			if(ex instanceof FileNotFoundException){
				handleException(new EpException(MessageConst.ERROR_760411, new String[]{scLoaReport.getTitle()}));
			}
			
		}
		catch(EpException ex){
			handleException(ex);
		}
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
		//ScQt scQt = getManagedBean().getScQt();
		
		String definitionType = scLoaReport.getDefinationType()
				.getParameterCode();
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();
		StringBuilder filename = new StringBuilder();
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType) || 
				ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD.equals(definitionType) ||
				ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD.equals(definitionType) ||
				ParameterConst.DOCUMENT_DEFINITION_KEY_LETTER_OF_ACCEPTANCE.equals(definitionType)) {
			
			try {
				String docDefinitionKey = scLoaReport.getPmDocument().getDocDefinitionKey();
				Long statusId = scLoa.getPmStatus().getStatusId();
				log.info("docDefinitionKey--->   "+docDefinitionKey);
				log.info("statusId --->    "+statusId);
				
			
				
				//development purpose - enable create document when the document doesn't exist
				/* if(scLoaReportAttachment == null || getManagedBean().getEnableDownloadAllDocuments()) {*/
				if (scLoaReportAttachment == null ) {
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
						log.info("filename--->    "+filename);
						scLoaReportAttachment.setFileName(filename.toString());
						log.info("scLoaReportAttachment--->   "+scLoaReportAttachment.getFileName());
						generateAgreementLetterDocFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa, this.getManagedBean().getScQt());
						//log.info("docDefinitionKey--->    "+scLoa.getScLoiLoa());
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
				if(this.getManagedBean().isValidatewitness()){ //#19336
				validateLoaWitnessAndAckBy();
				} //#19336
			}
			// #29006 - start
			if (scLoaReport.getTitle().contains(lampiranA3SubStr) && !ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa().getDocType())) {//29007&30963
				try {
					//#31425 - start
					if(scLoaReport.getScLoaReportAttachment().getDocNo() == null){
						scLoaReport.getScLoaReportAttachment().setDocNo(getManagedBean().getScLoa().getLoaNo());
					}
					//#31425 - end
					generateUpdatedLampiranA3FromVelocity((ScAttachment) scLoaReport.getScLoaReportAttachment(),
							getManagedBean().getScLoa(), this.getManagedBean().getScQt());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// #29006 - end

			ScLoaReportAttachment scLoaReportAttachment = scLoaReport
					.getScLoaReportAttachment();
			/*if (Arrays.asList(QTStatusConst.LOA_PENDING_VERIFICATION).contains(this.getManagedBean().getScLoa().getPmStatus().getStatusId()) && scLoaReport.getTitle().contains(lampiranA3SubStr) && ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa().getDocType())){//29007-start
				//scLoaReport.setScLoaReportAttachment(null);//29007	
			}*///29007-End
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
				if (Arrays.asList(QTStatusConst.LOA_PENDING_VERIFICATION).contains(this.getManagedBean().getScLoa().getPmStatus().getStatusId()) && scLoaReport.getTitle().contains(lampiranA3SubStr) && ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa().getDocType()) && scLoaReportAttachmentGenerated.isBrandNew()){//29007-start
					prefixName=prefixName.concat("1");	
				}//29007-End
				//#25025-start
				scLoaReportAttachmentGenerated.setTempFileName(setValueLowercase(prefixName,18));
				//#25025 - end
				scAttachments.add(scLoaReportAttachmentGenerated);
			}
			
			//#29006 - start
			if (StringUtils.isNotBlank(scLoaReport.getTitle())
					&& scLoaReport.getTitle().contains(lampiranA3SubStr)) {
				getManagedBean().setUpdatedLampiranA3DownloadedFlag(true);
			}
			//#29006 - end
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
		//debugLogger("generateAgreementLetterDocFromVelocity" );
		populateAttachmentDetails(attachment);
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
				.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		debugLogger("generateAgreementLetterDocFromVelocity");
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
		// ##25025 #509 -start
		fileNo = fileNo.trim().replaceAll("é", SSTConstant.PROTEGE_LOWERCASE_E);
		fileNo = fileNo.trim().replaceAll("É", SSTConstant.PROTEGE_UPPERCASE_E);
		context.put("fileNo", fileNo + "-" + scLoa.getFileExtNo());
		// ##25025 #509 -end	context.put("loaNo", scLoa.getLoaNo());

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
			//#25025 - start
			headerTitle = "Pembelian Terus";
			//#25025 - end
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
		// 25025 - note 436 start
		title = title.replaceAll("&", SSTConstant.AND_SYMBOL);
		title = title.replaceAll("<", SSTConstant.LESS_THAN_SYMBOL);
		title = title.replaceAll(">", SSTConstant.GREATER_THAN_SYMBOL);
		title = title.replaceAll("\r\n", SSTConstant.BREAKLINE);
		title = title.replaceAll("é", SSTConstant.PROTEGE_LOWERCASE_E);
		title = title.replaceAll("É", SSTConstant.PROTEGE_UPPERCASE_E);
		// 25025 - note 436- end
		context.put("loaTitle", title);

		context.put("hasLoi", false);
		context.put("bondReq", scLoa.isBondReq());
		//25025 - start
		String bondAmt = SSTConstant.UNDERLINE_BOND_AMT;
		String bondAmtText = SSTConstant.UNDERLINE;
		if (scLoa.getBondAmt() != null) {
			bondAmt = amountDF.format(scLoa.getBondAmt());
			bondAmtText = NumberWordUtil.convert(scLoa.getBondAmt().toString());
		}
		context.put(SSTConstant.BOND_AMOUNT, bondAmt);
		context.put(SSTConstant.BOND_AMOUNT_TEXT, bondAmtText);
		
		String bondPercentage = SSTConstant.UNDERLINE;
		String bondPercentageText = SSTConstant.UNDERLINE;
		if (scLoa.getBondPercentage() != null) {
			bondPercentage = percentageDF.format(scLoa.getBondPercentage())+SSTConstant.SIGN_PERCENTAGE;
			bondPercentageText  = NumberWordUtil.convert(scLoa.getBondPercentage().toString());
		}
		context.put(SSTConstant.BOND_PERCENTAGE_TEXT, bondPercentageText); 
		context.put(SSTConstant.BOND_PERCENTAGE, bondPercentage);
		//25025 - end
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

		
		
		//#25025 & #26770 - start
		if (null == scLoa.getContractEffDate()) {
			context.put(SSTConstant.CONTRACT_EFFECTIVE_DATE, SSTConstant.EMPTY_STRING);
		}else{
			context.put(SSTConstant.CONTRACT_EFFECTIVE_DATE, new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT).format(scLoa.getContractEffDate()));
		}
		
		if (null == scLoa.getContractExpDate()) {
			context.put(SSTConstant.CONTRACT_EXPIRED_DATE, SSTConstant.EMPTY_STRING);
		} else {
			
			context.put(SSTConstant.CONTRACT_EXPIRED_DATE,new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT).format(scLoa.getContractExpDate()));
		}
		//#25025 & #26770 - end
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
		
		// #25025 - Start.
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			if (null != scLoa.getScQuote() && StringUtils.isNotEmpty(scLoa.getScQuote().getQuoteNo())) {
				qtNo = scLoa.getScQuote().getQuoteNo();
			}
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			String rnNo = getQtAwardSO().retrieveRequestNoteNoByLoa(reqDTO);
			if (qtNo.contains("SQ") && StringUtils.isNotEmpty(rnNo)) {
				qtNo = qtNo + "/" + rnNo;
			} else {
				qtNo = rnNo;
			}
			context.put("docType", "Pembelian Terus"); 
		} else if(scQt != null && (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())
				|| ParameterConst.DOCTYPE_DIRECT_NEGO.equals(scLoa.getScLoiLoa().getDocType()))) {
			// #26770 - Start.
			if (isQuotationOrTender(scQt)) {
				context.put("docType", "Sebut Harga"); 
			} else {
				context.put("docType", "Tender"); 
			}
			// #26770 - End.
		} 
		// #25025 - End.
		
		// qtno
		context.put("qtNo", qtNo);

		// JadualHarga
		formJadualHarga(context, scLoa);

		// Specification
		formSpecification(context, scLoa);
		
		// #26130 - Start
		if(scQt != null && (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())
				|| ParameterConst.DOCTYPE_DIRECT_NEGO.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode()) 
					|| ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scQt.getFulfilmentType().getParameterCode())
					|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(scQt.getFulfilmentType().getParameterCode()))){
				constructSupplierSpecificationAndRemarks(context, scLoa);
		}
		// #26130 - End
		
		// #26226 - Start
		if((ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode()) ||
						ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode()))){
			
			constructPRSupplierSpecificationAndRemarks(context, scLoa);
			log.info(".....CR Enhancement 26226 generateAgreementLetterDocFromVelocity method.....");
		}
		// #26130 - End
		
		// #29006 - start		
		 ArrayList list = new ArrayList();
		 Map map = new HashMap();
		
		int count = 0;
		if (CollectionUtils.isNotEmpty(scLoa.getScLoiLoaItems())) {
			for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
				map.put("index", count);
				map.put("itemName", scLoiLoaItem.getScRequestItem().getItemName());
				map.put("itemQty", scLoiLoaItem.getQty());
				//start - #29006 note 794
				if(scLoiLoaItem.getScRequestItem().getTplSpecQuestion() != null)
					map.put("specQuestionId", scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());
				
				if(scLoiLoaItem.getTplSpecTierQty() != null){
					map.put("specTierQtyId", scLoiLoaItem.getTplSpecTierQty().getSpecTierQtyId());
					map.put("fromQty", scLoiLoaItem.getTplSpecTierQty().getFromQty());
					map.put("toQty", scLoiLoaItem.getTplSpecTierQty().getToQty());
				}
				//end - #29006 note 794
				list.add(map);
				map = new HashMap();
				count++;
			}
		}

		context.put("scLoiLoaItemList", list);
		// #29006 - end
		
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
		ItemBean jadualHarga = null;
		//26226 Start 
		String itemType = null;
		
		String dafaltValue = SSTConstant.TB;
		if (SSTConstant.LANGUAGE_CODE_EN.equals(new FacesScopes().getUserLocale().getLanguage())) {
			dafaltValue = SSTConstant.NA;//#29006
		}
		//End

		for (ScLoiLoaItem item : scLoa.getScLoiLoaItems()) {
			
			if(null != item.getScRequestItem().getItemType()){
				itemType = item.getScRequestItem().getItemType().getParameterCode();
				//log.info("Item Type --->"+itemType );
				if (ParameterConst.ITEM_TYPE_PRODUCT.equals(itemType)) {
					jadualHarga = mappingJadualHarga(item);
					productItemBeans.add(jadualHarga);
				} else if (itemType.equals(ParameterConst.ITEM_TYPE_SERVICE)) {
					//26226 Start   
					//serviceItemBeans.add(jadualHarga);  
					TplSpecQuestion tplSpecQuestion=item.getScRequestItem().getTplSpecQuestion();
					if(tplSpecQuestion.getQuestionType().equals(TemplateSpecConst.QUESTION_TYPE_ITEM))
					{
						jadualHarga = mappingJadualHarga(item);
				
						serviceItemBeans.add(jadualHarga);
					}//End
					
				}   
			}
		}
		jadualHargaItemBean.setProductItemBeans(productItemBeans);
		jadualHargaItemBean.setServiceItemBeans(serviceItemBeans);
		context.put("jadualHargaItemBean", jadualHargaItemBean);
		//26226 Start
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
		
		// #25025 - start
		String headerTitle = "";
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType())) {
			headerTitle = "Pembelian Terus";
		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())) {
			if (ParameterConst.PROCUREMENT_MODE_TENDER.equals(scLoa.getProcurementMode().getParameterCode())) {
				headerTitle = "Tender";
			} else {
				headerTitle = "Sebut Harga";
			}

		}
		context.put("headerTitle", headerTitle);
		// #25025 - end
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
		String title = procurementMode != "" ? procurementMode + "\r\n" + 
				scLoiLoa.getTitle() : scLoiLoa.getTitle();//#32739 & #32738//#32647 & #32648 remove uppercase
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

		// #25025 - start
		String headerTitle = "";
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType())) {
			// #25025 - start
			headerTitle = "Pembelian Terus";
			// #25025 - end
		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())) {
			if (ParameterConst.PROCUREMENT_MODE_TENDER.equals(scLoa.getProcurementMode().getParameterCode())) {
				headerTitle = "Tender";
			} else {
				headerTitle = "Sebut Harga";
			}

		}
		context.put("headerTitle", headerTitle);
		// #25025 - end
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
//	private class SupplierBankFK {
//
//		/** The account no. */
//		private String accountNo;
//
//		/** The bank name. */
//		private String bankName;
//
//		/** The bank branch. */
//		private String bankBranch;
//
//		/**
//		 * Instantiates a new supplier bank fk.
//		 *
//		 * @param smSupplierBank the sm supplier bank
//		 */
//		public SupplierBankFK(SmSupplierBank smSupplierBank) {
//			this.accountNo = smSupplierBank.getAccountNo();
//			this.bankName = smSupplierBank.getPmFinancialOrg().getFinOrgName();
//			this.bankBranch = smSupplierBank.getBankBranch();
//		}
//
//		/**
//		 * Instantiates a new supplier bank fk.
//		 *
//		 * @param scLoa the sc loa
//		 */
//		@SuppressWarnings("unused")
//		public SupplierBankFK(ScLoa scLoa) {
//			this.accountNo = scLoa.getBankAccNo();
//			this.bankName = scLoa.getBankName();
//			this.bankBranch = scLoa.getBankBranch();
//		}
//
//		/**
//		 * Equals.
//		 *
//		 * @param other the other
//		 * @return true, if successful
//		 */
//		public boolean equals(SupplierBankFK other) {
//			return getPK(this).equals(getPK(other));
//		}
//
//		/* (non-Javadoc)
//		 * @see java.lang.Object#hashCode()
//		 */
//		public int hashCode() {
//			int result = 3;
//
//			if (this.getAccountNo() != null) {
//				result = 31 * result + this.getAccountNo().hashCode();
//			}
//
//			if (this.getBankName() != null) {
//				result = 31 * result + this.getBankName().hashCode();
//			}
//
//			if (this.getBankBranch() != null) {
//				result = 31 * result + this.getBankBranch().hashCode();
//			}
//
//			return result;
//		}
//
//		/**
//		 * Gets the account no.
//		 *
//		 * @return the accountNo
//		 */
//		public String getAccountNo() {
//			return accountNo;
//		}
//
//		/**
//		 * Sets the account no.
//		 *
//		 * @param accountNo            the accountNo to set
//		 */
//		public void setAccountNo(String accountNo) {
//			this.accountNo = accountNo;
//		}
//
//		/**
//		 * Gets the bank name.
//		 *
//		 * @return the bankName
//		 */
//		public String getBankName() {
//			return bankName;
//		}
//
//		/**
//		 * Sets the bank name.
//		 *
//		 * @param bankName            the bankName to set
//		 */
//		public void setBankName(String bankName) {
//			this.bankName = bankName;
//		}
//
//		/**
//		 * Gets the bank branch.
//		 *
//		 * @return the bankBranch
//		 */
//		public String getBankBranch() {
//			return bankBranch;
//		}
//
//		/**
//		 * Sets the bank branch.
//		 *
//		 * @param bankBranch            the bankBranch to set
//		 */
//		public void setBankBranch(String bankBranch) {
//			this.bankBranch = bankBranch;
//		}
//
//		/**
//		 * Gets the pk.
//		 *
//		 * @param fk the fk
//		 * @return the pk
//		 */
//		private String getPK(SupplierBankFK fk) {
//			return new StringBuilder(
//					fk.getAccountNo() != null ? fk.getAccountNo() : "")
//			.append("_")
//			.append(fk.getBankName() != null ? fk.getBankName() : "")
//			.append("_")
//			.append(fk.getBankBranch() != null ? fk.getBankBranch()
//					: "").toString();
//		}
//
//	}
	
	/**
	 * save the LOA details.
	 * save - save data into database and initiate task for LOA details.
	 * page navigate to loa details page.
	 */
	public void saveAction(boolean supplier) {
		debugLogger("saveAction");

		try {
			PmParameter contractType = getManagedBean().getScLoa().getContractType();
			if(this.getManagedBean().getFlowamt()==null && null != contractType 
					&& ParameterTypeConst.CONTRACT_TYPE.equals(contractType.getParameterType()) 
					&& ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType.getParameterCode()) &&
					!this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT))//29826,//27502, adding condition caz error when save at supplier loa acceptance
			{
				throw new EpException(MessageConst.ERROR_760485);
				
			}else{

					String outcome = BpmOutcomeConst.BPM_SAVE;
					ScLoa scLoa = this.getManagedBean().getScLoa();
					Long statusId = null;
					PmUser pmUserNull = null;//#26475 & #26474
					
					boolean isNewLoa = false; //Bug #33188
					
					boolean validateWitness=this.getManagedBean().isValidatewitness(); //#19336
					//#29007 Start Vinod
					boolean specificationAndSupplierDialogFlagQT=this.getManagedBean().isSpecificationAndSupplierDialogFlagQT();
					boolean supPropSpecUpdate=this.getManagedBean().isSupPropSpecUpdate();
					//#29007 End Vinod
					if((scLoa!=null) && (this.getManagedBean().getFlowamt()!=null))
					{
						scLoa.setFollowLoaAmt(this.getManagedBean().getFlowamt());
					}
					ScLoaWitness loaSupplierWitness = this.getManagedBean().getLoaSupplierWitness();
					//#29173 - CR 26475: Static Code Analysis
					//start
					//#29173 - removed longValue() from getStatusId
					//#29173 - added if statement to check if null
					if(scLoa != null)
						statusId = scLoa.getPmStatus().getStatusId();
		
					if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
							.equals(statusId)) {
						validateSupplierWitnessDetails(scLoa, loaSupplierWitness, outcome);
						// #29006 - start  - Bug #33903 start					
						if (scLoa != null && CollectionUtils.isNotEmpty(scLoa.getScLoaRemarks())) { //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
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
									if (null == scLoaRemark.getRemarkId()){
										removeRemark = scLoaRemark;
									}else{
										scLoaRemark.setRemark(" ");
									}
								}
							}
							scLoa.getScLoaRemarks().remove(removeRemark);
						}
						// #29006 - end  - Bug #33903 end
					}
					parseLOADetails(scLoa, outcome);
					validateLOADetails(scLoa, outcome);
					
					// start - #26475 & #26474 - disable validation on save action
					//validateContractDetails(scLoa);
					if (scLoa != null ){ //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
					   scLoa.setContractAdmin(pmUserNull);
					}
					// end - #26475 & #26474 
					if (!supplier) {
						updateContractDetails(scLoa);
					}
					
					//Bug #33188 start
					if (scLoa != null && scLoa.isBrandNew()) { //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
						isNewLoa = true;
					}
					//Bug #33188 end

					//Bug #35196 - haziq
					if(!scLoa.getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION) && scLoa.isBondReq() ) {	
						scLoa.setBondReq(false);
					 }	
		
					AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
					reqDTO.setPayLoad(getManagedBean().getPayLoad());
					reqDTO.setScLoa(scLoa);
					reqDTO.setOutcome(outcome);
					reqDTO.setRoleCode(getUserRoleCode(scLoa != null ? scLoa.getScLoiLoa().getDocType():null)); //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
					reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
					reqDTO.setUserLocale(new FacesScopes().getUserLocale());
					reqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveFreqSchedules());
					reqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgressivePayments());
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
							retrieveSupplierLOADtl(getManagedBean().getSourceId(), scLoa.getLoaId(), null, false);
						} else {
							editLOAAction(scLoa);
						}
						this.getManagedBean().setValidatewitness(validateWitness); //#19336
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
						removeUnwantedLampiranA3Report(1); //#29006 - note 650
						
						//Bug #33188 start
						if (isNewLoa){
							renewSesionObject();
							successMessage(MessageConst.INFO_7000044, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
									scLoa.getLoaNo(), Boolean.TRUE);
						}else{
							successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
									scLoa.getLoaNo(), Boolean.FALSE);
						}
						//Bug #33188 end
					}
				 }

		} catch (NullPointerException e) {
			log.info(e.getStackTrace());
		}
	}

	/**
	 * submit LOA details.
	 * submit - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */
	//#25725 start change return type to void
	public void submitAction() {
		//#25725 end
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
				
				//#25726 start
				if (null != scLoa && CollectionUtils.isNotEmpty(scLoa.getScLoaReports())) {
					if (null == scLoa.getScQuote()) {
						if (!scLoa.isBrandNew()) {
							if (null != scLoa.getScLoiLoa()) {
								if (!scLoa.getScLoiLoa().getFulfilmentTypeCode()
										.equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)
										&& !scLoa.getScLoiLoa().getFulfilmentTypeCode()
												.equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
									for (ScLoaReport report : this.getManagedBean().getScLoa().getScLoaReports()) {
										if (null != report
												&& QTConstants.SURAT_SETUJU_TERIMA
														.equals(report.getTitle())) {
											if (report.getScLoaReportAttachment() != null) {
												if (null == report.getIsPK42BondCalc()) {
													boolean newBond = validateNewBond(scLoa);
													if (newBond == false) {
														return;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				//#25726 end
				if (scLoa != null){//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
				    scLoa.setScLoaWorkFlowStatuses(null);
				}
				// To avoid insert workflow status when direct submit without save.
				if (scLoa != null && scLoa.isBrandNew()) {//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
					List<ScLoaRemark> remarks = scLoa.getScLoaRemarks();
					if (CollectionUtils.isNotEmpty(remarks)) {
						for (ScLoaRemark remark : remarks) {
							remark.setScWorkFlowStatus(null);
						}
					}	
				}
				
				//Bug #35196 - haziq
				if(!scLoa.getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION) && scLoa.isBondReq() ) {	
					scLoa.setBondReq(false);
				 }	
				
				//#25725 start
				boolean approvalLetter = validateApprovalLetter(scLoa);
				
				if(approvalLetter == false){
					return;
				}				
				//#25725 end
	
				AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
				reqDTO.setScLoa(scLoa);
				reqDTO.setPayLoad(getManagedBean().getPayLoad());
				reqDTO.setOutcome(outcome);
				reqDTO.setSourceId(getManagedBean().getSourceId());
				reqDTO.setRoleCode(getUserRoleCode(scLoa != null ?scLoa.getScLoiLoa().getDocType():null));//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
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
		//#25725 start - remove return null
		//#25725 end
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
		//#29173 - CR 26475: Static Code Analysis
		//start
		//#29173 - removed try-finally clause as it is not needed. exception are always thrown in main code
		
		// #29006 - start
		if (QTStatusConst.LOA_PENDING_VERIFICATION.equals(this.getManagedBean().getScLoa().getPmStatus().getStatusId()
				.longValue())
				&& CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
			// #29006 - start - reendorsed (comment 528)
			boolean lampiranA3Exists = false;
			//Timestamp lampiranA3Time = null; //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
			//Timestamp justificationTime = null; //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
			int count = 0;
			StringBuffer updatedItems = new StringBuffer();			
			// #29006 - end - reendorsed (comment 528)
			for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
				if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
					lampiranA3Exists = true;
					//lampiranA3Time = report.getChangedDate(); // #29006 - reendorsed (comment 528)
					break;
				}
			}			
			// #29006 - start - reendorsed (comment 528)
			//#29006 - start - note 607/619
			String currentItemName = "";
			for (ScLoiLoaItem each : getManagedBean().getScLoa().getScLoiLoaItems()) {				
				List<ScLoiLoaItem> items = getQtAwardSO().getLoiLoaItemByLoaIdAndReqItemId(
						each.getScLoa().getLoaId(), each.getScRequestItem().getRequestNoteItemId());
				//start - #35828(fixing 29006) 
				boolean isNewOffereComplete = isNewOfferComplete(each);
				boolean isJustificationComplete = isJustificationComplete(items);
				
				boolean condition1 = !isJustificationComplete && isNewOffereComplete;
				boolean condition2 = !isJustificationComplete && !isNewOffereComplete
						&& CollectionUtils.isNotEmpty(items);

				if (((condition1) || (condition2))
						&& !currentItemName.equalsIgnoreCase(each.getScRequestItem().getItemName())) {
					count++;
					updatedItems.append(count + ". " + each.getScRequestItem().getItemName() + "</br>");// #29006 - uat issue
					currentItemName = each.getScRequestItem().getItemName();
				}
				//end - #35828(fixing 29006)
			}
			//#29006 - end - note 607/619
			
			if(count > 0){
				// #29006 - start - (fixing on comment 570)
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_775178, new String[] {updatedItems.toString()}, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				return resDTO;
				// #29006 - end - (fixing on comment 570)
			}
			// #29006 - end - reendorsed (comment 528)
			if (lampiranA3Exists && getManagedBean().isNewSupplierUpdatedFlag()
					&& !getManagedBean().isUpdatedLampiranA3DownloadedFlag()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, ResourceBundleUtils.getMessage(
								"lbl.sc.download.new.lampiran.a3", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())));
				return resDTO;
			}
		}
		// #29006 - end

		ScLoa scLoa = this.getManagedBean().getScLoa();
		ScLoaWitness loaSupplierWitness = this.getManagedBean().getLoaSupplierWitness();
		//#29173 - removed longValue() from getStatusId()
		Long statusId = scLoa.getPmStatus().getStatusId();

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
				.equals(statusId) && !outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_EPV_CANCEL)) {//#21692 if cancel for epv no need checking
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
		
		// remove empty remark - start
		List<ScLoaRemark> scLoaRemarks = scLoa.getScLoaRemarks();
		List<ScLoaRemark> finalScLoaRemarks = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(scLoaRemarks)){
			for(ScLoaRemark each : scLoaRemarks){
				if(!StringUtils.isEmpty(each.getRemark())){
					finalScLoaRemarks.add(each);
				}
			}
		}
		scLoa.setScLoaRemarks(finalScLoaRemarks);
		// remove empty remark - end

		validateLOADetails(scLoa, outcome);

		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		// #26475 - start
		if (this.getManagedBean().getSelectedPtj() != null) {
			scLoa.setContractAgency(this.getManagedBean().getSelectedPtj());
		}
		// #26475 - end
		// #29006 - start - avoid error 100048
		if (QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId)) {
			AwardSupplierReqDTO suppReqDTO = new AwardSupplierReqDTO();
			suppReqDTO.setLoaId(scLoa.getLoaId());
			AwardSupplierResDTO suppResTO = getQtAwardSO()
					.retrieveLOADtlByLoaId(suppReqDTO);
			reqDTO.setScLoa(suppResTO.getScLoa());
		}
		
		else
			reqDTO.setScLoa(scLoa);
		// #35828 - start - 29006 fixing
		if (scLoa.getCurrentStatus() != null)
			reqDTO.getScLoa().setCurrentStatus(scLoa.getCurrentStatus());
		// #35828 - end - 29006 fixing
		// #29006 - end - avoid error 100048
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
			if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome) && !isSqStillValid()){
				messageCode = MessageConst.INFO_700031; 
			}
			else if (QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId) || 
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
			//#21692 - start
			if (BpmOutcomeConst.BPM_EPV_CANCEL
					.equalsIgnoreCase(outcome)) {
				messageCode = MessageConst.INFO_700031;
			}
			//#21692 - end

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
		//end #29173 - CR 26475: Static Code Analysis
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
		
		//#25726 start #34910
		ScLoa scLoa = this.getManagedBean().getScLoa();
		if (null != this.getManagedBean().getScLoa()
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoaReports())) {
			if (!scLoa.isBrandNew()) {
				if (null == scLoa.getScQuote()) {
					for (ScLoaReport report : this.getManagedBean().getScLoa().getScLoaReports()) {
						if (null != report && QTConstants.SURAT_SETUJU_TERIMA.equals(report.getTitle())) {
							if (report.getScLoaReportAttachment() != null) {
								if (null == report.getIsPK42BondCalc()) {
									boolean newBond = validateNewBond(scLoa);
									if (newBond == false) {
										return;
									}
								}
							}
						}
					}
				}
			}
		}
		//#25726 end
		// #25725 start
		boolean approvalLetter = validateApprovalLetter(scLoa);

		if (approvalLetter == false) {
			return;
		}
		// #25725 end
        
		this.getManagedBean().setAction(outcome);
		 		
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
        		// #26770 - Start.
        		validateReportAttachment();
        		// #26770 - End.
				RequestContext.getCurrentInstance().execute("loaApprovalPopUp.show()");
		}else if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)){
			// Enhancement #21694 START
			//REVISE DOCUMENT FROM VERIFIER TO SUPPLIER AGAIN FOR REUPLOAD DOCUMENT
			if(this.getManagedBean().getScLoa().getPmStatus().getStatusId() == QTStatusConst.LOA_PENDING_VERIFICATION
					//22111 - START
					|| QTStatusConst.LOA_PENDING_VERIFICATION.equals(this.getManagedBean().getScLoa().getPmStatus()
							.getStatusId().longValue())) {				
				if (this.getManagedBean().getScQt() != null ){
					//22211 - END
					updateReviseBPMTaskAction(outcome);				
				//22111 - START
				}else{
					updateReviseBPMTaskActionWithoutQt(outcome);
				}//22211 - END
			}else{
				updateBpmTaskAction(outcome);
			}
			//Enhancement #21694 END
		}else {
			updateBpmTaskAction(outcome);
		}
	}
	//20190405@asyrafhaziq - CR25062 - End
	
	// #26770 - Start.
    /**
    * 
    */
   private void validateReportAttachment() {
     if (null != this.getManagedBean().getScLoa() && CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoaReports())) {
         for (ScLoaReport report : this.getManagedBean().getScLoa().getScLoaReports()) {
             if (null != report && "Surat Setuju Terima (Termasuk Lampiran A)".equals(report.getTitle())) {
                 if (report.getScLoaReportAttachment() == null) {
                   throw new EpException(MessageConst.ERROR_775159);
                 }
                break;
            }
         }
      }
   }
   // #26770 - End.
   
	/**
	 * Enhancement #21694
	 * Update bpm task action.
	 * STAGE REVISE (VERIFY) -> LOA Acceptance (Supplier Action)
	 * STAGE LOA Acceptance(Supplier Action) -> REVISE (VERIFY)
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 */
	public void updateReviseBPMTaskAction(String outcome){
		debugLogger("updateReviseBPMTaskAction");
		
		/*******************
		 * INITIATE BPM REVISE
		 */
		PmParameter procurementMode = this.getManagedBean().getScQt().getProcurementMode();
		IScActorSO actorSO;
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		ScActorReqDTO actorReqDTO=new ScActorReqDTO();
		actorSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScActorSO.class);
		reqDTO.setScLoa(this.getManagedBean().getScLoa());
		reqDTO.setPayLoad(getManagedBean().getPayLoad());
		reqDTO.setOutcome(outcome);
		reqDTO.setSourceId(getManagedBean().getSourceId());
		reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setScQt(this.getManagedBean().getScQt());
		reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		reqDTO.setQuotation(null!=procurementMode && procurementMode.getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_QUOTATION)?true:false);
		
		actorReqDTO.setDocId(this.getManagedBean().getScQt().getQtId());
		actorReqDTO.setDocTypes(Arrays.asList(ParameterConst.DOCTYPE_QUOTATION));
		switch(this.getManagedBean().getScQt().getFinalization().getLoiLoaBy()){
			case ParameterConst.LOILOA_PREPARED_BY_DO	:	//DESK OFFICER
																actorReqDTO.setUserId(this.getManagedBean().getScQt().getCreatedBy());
																break;
			case ParameterConst.LOILOA_PREPARED_BY_FC_SECRETARY	:	//SECRETARY
																if(null!=this.getManagedBean().getScLoa()){
																	actorReqDTO.setUserId(this.getManagedBean().getScLoa().getCreatedBy());
																}else{
																	actorReqDTO.setUserId(this.getManagedBean().getScLoa().getCreatedBy());
																}
																break;
		}
		reqDTO.setScActors(actorSO.retreiveActorByDocTypesDocIdUserId(actorReqDTO).getScActorList());
		AwardSupplierResDTO resDTO = getQtAwardSO().reviseDocumentLOA(reqDTO);		
		if(resDTO.isSuccessful()){
			/*******************
			 * ACKNOWLEDGE_PAGE
			 *******************/
			String[] param = new String[3];
			
			param[0] = this.getManagedBean().getScLoa().getLoaNo();
			param[1] = this.getManagedBean().getScQt().getQtNo();
			param[2] = this.getManagedBean().getScLoa().getScLoiLoa().getSupplierName();
			if(reqDTO.getScLoa().getPmStatus().getStatusId().equals(QTStatusConst.LOA_REVISE_DOC)){
				if (procurementMode != null
						&& procurementMode.getParameterCode().equals(
								ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
					successMessage(MessageConst.INFO_700079,param);
				} else if (procurementMode != null
						&& procurementMode.getParameterCode().equals(
								ParameterConst.PROCUREMENT_MODE_TENDER)) {
					successMessage(MessageConst.INFO_700080,param);
				}
			}else{
				if (procurementMode != null
						&& procurementMode.getParameterCode().equals(
								ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
					successMessage(MessageConst.INFO_700077,param);
				} else if (procurementMode != null
						&& procurementMode.getParameterCode().equals(
								ParameterConst.PROCUREMENT_MODE_TENDER)) {
					successMessage(MessageConst.INFO_700078,param);
				}
			}			
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}		
	}
		
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
		if (outcome.equals(BpmOutcomeConst.BPM_REVISE))
			reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
		else
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
	
	// #27797 & 29006 - start
	public boolean isZonalWithPaOrPs() {

		Boolean isZonalWithPaOrPs = false;
		Boolean isZonal = getManagedBean().getScLoa().getScLoiLoa().isZonal();
//		String fulfilmentType = getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]		
		String docType = getManagedBean().getScLoa().getScLoiLoa().getDocType();
		/*if (fulfilmentType.equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
				|| fulfilmentType.equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)
						|| fulfilmentType.equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)
								|| fulfilmentType.equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN)) {
			if (isZonal && (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(docType)))
				isZonalWithPaOrPs = true;
		}*/
		//only detect Zonal PR
		if (isZonal && (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(docType)))
			isZonalWithPaOrPs = true;
		
		return isZonalWithPaOrPs;
	}

	// #27797 & 29006 - end
	
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
		//#29173 - CR 26475: Static Code Analysis
		//start
		//#29173 - removed longValue() from getStatusId()
		Long statusId = scLoa.getPmStatus().getStatusId();

		if(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_ACCEPT.equals(outcome)) {

			//#32280 - start
			if(isZonalWithPaOrPs())
			{
				Long tempRequestNoteItemId = 0L;
				
				for (ScLoiLoaItem scLoiLoaItem : getManagedBean().getNameZoneScLoiLoaItemList()) {
					if(scLoiLoaItem.getScRequestItem()!= null && 
							null == scLoiLoaItem.getScRequestItem().getCmItem() && tempRequestNoteItemId.compareTo(scLoiLoaItem.getScRequestItem().getRequestNoteItemId()) != 0){
						throw new EpException(MessageConst.ERROR_760436);
					}
					tempRequestNoteItemId = scLoiLoaItem.getScRequestItem().getRequestNoteItemId();
				}
			}
			
			else{
				// check to assign UNSPSC code 
				for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
					if(scLoiLoaItem.getScRequestItem()!= null && 
							null == scLoiLoaItem.getScRequestItem().getCmItem()){
						throw new EpException(MessageConst.ERROR_760436);
					}
				}
				//#29173 - remove old commented block
			}
			//#32280 - end
		}
		
		// #11134 - Start
		if(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
			
			boolean remarksEmpty = false;
			
			// check if remark is empty
			List<ScLoaRemark> scLoaRemarks = scLoa.getScLoaRemarks();
			if(CollectionUtils.isNotEmpty(scLoaRemarks)){
				for(ScLoaRemark each : scLoaRemarks){
					if(StringUtils.isEmpty(each.getRemark())){
						remarksEmpty = true;
						break;
					}
				}
			}else {
				remarksEmpty = true;
			}
			
			//throw message
			if(remarksEmpty){
				throw new EpException(
						MessageConst.ERROR_716024);
				 // Remark is mandatory for rejection.
			}
		}
		// #11134 -End
		//#21692 add supplier remark - start
		if(BpmOutcomeConst.BPM_EPV_CANCEL.equals(outcome)
				&& isSupplierStatusRejectedEPV(scLoa.getScLoiLoa().getSmSupplier().getSupplierId())) {
			
			boolean remarksEmpty = false;
			
			// check if remark is empty
			List<ScLoaRemark> scLoaRemarks = scLoa.getScLoaRemarks();
			if(CollectionUtils.isNotEmpty(scLoaRemarks)){
				for(ScLoaRemark each : scLoaRemarks){
					if(StringUtils.isEmpty(each.getRemark())){
						each.setRemark(SourcingConstants.SUPPLIER_COMPLIANCE_REMARK);
						break;
					}
				}
			}else {
				for (ScLoaRemark each : scLoaRemarks) {
					each.setRemark(SourcingConstants.SUPPLIER_COMPLIANCE_REMARK);
				}
			}
		}
		//#21692 add supplier remark - end

		if(QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId) || 
				QTStatusConst.LOA_PENDING_REVISION.equals(statusId)) {

			//#29173 - merged if statements
			if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome) &&
					scLoa.isApprovalReq()){
				if(null == getManagedBean().getApproverUser() || 
						null == getManagedBean().getApproverUser().getUserId()){
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

			//#29173 - merged if statements
			if(null != scLoa.getGovtSignDate() &&
					scLoa.getContractEffDate().before(scLoa.getGovtSignDate())){
				throw new EpException(MessageConst.ERROR_760468);
			}
		}
		// validation for fulfillment schedule and progressivepayment

		//#29173 - merged if statements
		if((BpmOutcomeConst.BPM_SUBMIT.equals(outcome) || BpmOutcomeConst.BPM_SAVE.equals(outcome)) &&
				scLoa.getScLoiLoa() != null) {
			// check whether loa from s1 or s2.
			//#29173 - merged if statements
			//25726 note 298 - issue 1 - seperate by document and add another fullfilment type schedule
			if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType()) &&
					getManagedBean().getScQt() != null){				
			  if(ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode())||
					 ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode())){
				// For s2
				if (("P").equals(getManagedBean().getScQt().getScheduleType())) {
					//#29173 - change size()>0 to !isEmpty()
					if(scLoa.getScQtProgPayments() != null && !scLoa.getScQtProgPayments().isEmpty()){
						for (ScQtProgressivePayment progressivePayment : scLoa.getScQtProgPayments()) {
							progressivePayValidation(progressivePayment, scLoa.getScLoiLoa().getDocType());
						}
					}
					//#29173 - merged if statements
					//#29173 - change size()>0 to !isEmpty()
				} else if (("F").equals(getManagedBean().getScQt().getScheduleType()) &&
						scLoa.getScQtSchedules() != null && !scLoa.getScQtSchedules().isEmpty()) {
					for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
						freqScheduleValidation(scQtSchedule, scLoa.getScLoiLoa().getDocType());
					}
				}
			  } 
			}else{
				// for s1
				//#29173 - change size()>0 to !isEmpty()
				if(scLoa.getScQtSchedules() != null && !scLoa.getScQtSchedules().isEmpty()){
					for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
						freqScheduleValidation(scQtSchedule, scLoa.getScLoiLoa().getDocType());
					}
				}
			}
		}

		//#29173 - merged if statements
		if((BpmOutcomeConst.BPM_ACCEPT.equals(outcome) || BpmOutcomeConst.BPM_SUBMIT.equals(outcome)) &&
				CollectionUtils.isNotEmpty(scLoa.getScLoaReports())) {
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
								null == loaReport.getScLoaReportAttachment() && !loaReport.getTitle().contains(lampiranA3SubStr)){//#29007 Vinod added lampiranA3SubStr
					throw new EpException(MessageConst.ERROR_760441,
							new String[] {loaReport.getTitle()});
				}
			}
		}
		
		//22211 - start
		if(BpmOutcomeConst.BPM_RESUBMIT.equals(outcome) && CollectionUtils.isNotEmpty(scLoa.getScLoaReports())){
			//#29173 - merged if statements
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
			//end #29173 - CR 26475: Static Code Analysis
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
		
		//#25022
		// check whether loa from s1 or s2.
//		if(scLoa.getScLoiLoa() != null){
//			if(ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
//				// For s2
//				if (getManagedBean().getScQt() != null) {
//					if (("P").equals(getManagedBean().getScQt().getScheduleType())) {
//						saveProgressivePaymntList();
//
//					} else if (("F").equals(getManagedBean().getScQt()
//							.getScheduleType())) {
//						saveScheduleList();
//					}
//				}
//			} else{
//				// for s1
//				saveScheduleList();
//			}
//		}

		PmUser contractSignerUser = getManagedBean().getContractSignerUser();
		if (contractSignerUser != null
				&& contractSignerUser.getUserId() != null) {
			scLoa.setContractSigner(contractSignerUser);
		}
		
	//25726 note 298 - issue 1 - start
			if(scLoa.getScLoiLoa() != null && 
			   ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())){
					if (getManagedBean().getScQt() != null && ("F").equals(getManagedBean().getScQt()
							.getScheduleType())) {
						//#25022
						saveScheduleList();
					}
			}else{
				//#25022
				saveScheduleList();	
			}
	//25726 note 298 - issue 1 - end
		
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
			//Reopen #25726 note 110
			//#35046 - start
			if ((null != this.getManagedBean().getScLoa()) && (null != this.getManagedBean().getScLoa().getScLoiLoa())) {
				if (null != this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode()) {
					if (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode()
							.equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
						docDefTypeReqDTO.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
					} else {
						docDefTypeReqDTO.setParameterCode(ParameterConst.SCHEDULE_TYPE_NON_PERIODIC);
					}
				}
			}
			//#35046 end
			//#25726 end
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
				//SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
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
				.getTplSpecZoneDtl().getZoneName() : "");
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
		//#28609
		//itemBean.setItem(item.getScRequestItem().getItemName());
		itemBean.setZoneName(item.getTplSpecZoneDtl() != null ? item.getTplSpecZoneDtl().getZoneName() : "");
		
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
		//itemBean.set
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
	/*
	private void generateJadualHarga(ScAttachment attachment,
			List<JadualHargaBean> productList, List<JadualHargaBean> serviceList, boolean isS1OrS2)
					throws Exception {
		if (serviceList.size() > 0 || productList.size() > 0) {
			InputStream inputStream = null;
			try {

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
	*/

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
	@SuppressWarnings("resource")
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

					setTabActiveIndex(); // 25791

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
	/*
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
	*/

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
		// #32757- Start.
		getManagedBean().setBackToPreviousPage(null);
		// #32757- End.
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
			// #26247 - #33073 - #33354 - Start.
			if (CollectionUtils.isNotEmpty(loiLoaList) && loiLoaList.size() > 0) {
				IQTSuppFinalizationSO qtSuppFinalizationSO = null;
				try {
					for (ScLoiLoa scLoiLoa: loiLoaList) {
						qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
										IQTSuppFinalizationSO.class);
						
						if (null != qtSuppFinalizationSO) {
							SmSupplier smSupplier = qtSuppFinalizationSO.retrieveActiveDisciplinaryActionBySupplier(scLoiLoa.getSmSupplier());
							scLoiLoa.setSmSupplier(smSupplier);
						}
					}
				} finally {
					qtSuppFinalizationSO = null;
				}
			}
			// #26247 - #33073 - #33354 - End.
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
		//} Bug #35196 - haziq

		boolean isBondReq = isPerformanceBondRequired(overallContractAmtOrLoaAmt, qtCategories);
		
		System.out.println("____ checkPerformanceBondMandatory docId: "+loa.getScLoiLoa().getDocId()
				+" docType: " +loa.getScLoiLoa().getDocType() +" isBondReq: "+isBondReq+" overallContractAmtOrLoaAmt: "+overallContractAmtOrLoaAmt);
		
		//Bug #29885 start
		if(loa.isBrandNew() || isBondReq){ 
			loa.setBondReq(isBondReq);
		}
		//Bug #29885 end
		getManagedBean().setPerformanceBondMandatory(isBondReq);
	  }//Bug #35196 - haziq
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
		
		//#25725 start
		
		boolean isPeriodic = (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(fulfilmentType))||(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(fulfilmentType));
		boolean isNonPeriodic = (ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN.equals(fulfilmentType))||(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED.equals(fulfilmentType));
		
		if(getManagedBean().getScLoa().getProcurementMode().getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)){
			if(isOneOffFulfilment) {
				isAgreementMandatory = false;
				loa.setAgreementReq(isAgreementMandatory);
			} else if (isPeriodic){
				//Bug #24065
				//isAgreementMandatory = false;
				isAgreementMandatory = true;
				loa.setAgreementReq(isAgreementMandatory);
			}else if(isNonPeriodic && !over500K(loa)){
				isAgreementMandatory = false;
				loa.setAgreementReq(isAgreementMandatory);
			}else if(isNonPeriodic && over500K(loa)){
				isAgreementMandatory = true;
				loa.setAgreementReq(isAgreementMandatory);
			}
		}else{
			// #25726 START
			if((isOneOffFulfilment && !over500K(loa)) || (isNonPeriodic && !over500K(loa))) {
				isAgreementMandatory = false;
				loa.setAgreementReq(isAgreementMandatory);
			} else if ((isOneOffFulfilment && over500K(loa)) || (isNonPeriodic && over500K(loa)) || (isPeriodic)){
				//Bug #24065
				//isAgreementMandatory = false;
				isAgreementMandatory = true;
				loa.setAgreementReq(isAgreementMandatory);
			}			
			// #25726 END
		}		

		getManagedBean().setAgreementMandatory(isAgreementMandatory);
	}
	
	private boolean over500K (ScLoa loa){
		BigDecimal contractAmt = loa.getOverallContractAmt();
		BigDecimal limit = this.getManagedBean().getLimit();		
		
		if(contractAmt.compareTo(limit) == 0){
			return false;
		//start - Sonarqube DP Bug Minor Iskandar [12/7/2023]
		}else if(contractAmt.compareTo(limit) > 0){
			return true;
		//stop - Sonarqube DP Bug Minor Iskandar [12/7/2023]
		}else{
			return false;
		}		
	}
	
	// #25725 - note 146 - add javadoc and change to public
	/**
	 * To check is PK7 procurement
	 * 
	 * @return boolean isPK7
	 */
	public boolean isPK7(ScLoa loa){				
		boolean PK7 = false;
		if(loa.getProcurementType()!=null){					
			if(loa.getProcurementType().getParameterCode().equals(ParameterConst.PROC_TYPE_PK7)){
				this.getManagedBean().setProcurementType(loa.getProcurementType().getParameterCode());
				PK7 = true;
			}else{
				this.getManagedBean().setProcurementType(loa.getProcurementType().getParameterCode());				
			}
		}
		return PK7;
	}
	
	//#25725 - end
	//#25725 - note 125/128 - start
	/**
	 * To check is reason PK2
	 * @return boolean isReasonPK2
	 */
	public boolean isReasonPK2(ScLoa loa) { // #25725 - note 146 - change to public
		boolean isReasonPK2 = false;
		if(loa.getScQuote().getJustificationType()!=null){
			if(loa.getScQuote().getJustificationType().getReasonCode().equals(ParameterConst.PK2_REASON_CODE)){
				isReasonPK2 = true;
			}			
		}
		return isReasonPK2;	
	}
	//#25725 - note 125/128 - end
	// #25725 - note 146 - start
	/**
	 * To check is reason special item
	 * 
	 * @return boolean isReasonSpecialItem
	 */
	public boolean isReasonSpecialItem(ScLoa loa) {
		boolean isReasonSpecialItem = false;
		if (loa.getScQuote().getJustificationType() != null) {
			if (loa.getScQuote().getJustificationType().getReasonCode().equals(ParameterConst.SPECIAL_ITEM_REASON_CODE)) {
				isReasonSpecialItem = true;
			}
		}
		return isReasonSpecialItem;
	}

	/**
	 * To enable/disable approval letter field
	 */
	public void showApprovalLetter() {
		ScLoa loa = getManagedBean().getScLoa();
		Boolean isNonPeriodic = (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)) || (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN));
		Boolean isOneOff = loa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
		Boolean isDocTypeQuotation = loa.getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION);
		
		if (!loa.isAgreementReq() && over500K(loa) && isNonPeriodic
				&& (!isPK7(loa) || isReasonPK2(loa) || isReasonSpecialItem(loa)) && !isDocTypeQuotation) {
			this.getManagedBean().setShowApprovalLetter(Boolean.TRUE);
		} else if (isDocTypeQuotation
				&& !loa.isAgreementReq()
				&& ((isNonPeriodic || isOneOff) && (!isPK7(loa) || isReasonPK2(loa) || isReasonSpecialItem(loa)) && over500K(loa))) {
			this.getManagedBean().setShowApprovalLetter(Boolean.TRUE);
		}
		else{
			this.getManagedBean().setShowApprovalLetter(Boolean.FALSE);
		}		
	}

	// #25725 - note 146 - end
	
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
	private void progressivePayValidation(ScQtProgressivePayment progressivePayment, String docType) {
		debugLogger("progressivePayValidation");
		FacesScopes facesScopes = new FacesScopes();
		Calendar startDate = null;
		Calendar endDate = null;
		//#25022
		if(progressivePayment.getStartMonth() == null || progressivePayment.getStartMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(progressivePayment.getStartYear() == null || progressivePayment.getStartYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.year",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(progressivePayment.getEndMonth() == null || progressivePayment.getEndMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});
		}
		if(progressivePayment.getEndYear() == null || progressivePayment.getEndYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.year",
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
				//#29474 - start
				if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(docType)) {
					throw new EpException(MessageConst.ERROR_760347);
				}
				//#29474 - end
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
		// #35977 - Start
		if (scLoa.getBondAmt() != null
				&& ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes()
						.getUserLocale().getLanguage())) {
			getManagedBean()
					.setBondAmountWord(
							NumberWordUtilEn.convert(scLoa.getBondAmt()
									.toString()));
		} else if (scLoa.getBondAmt() != null
				&& ReportConst.MALAY_LANGUAGE_CODE.equals(new FacesScopes()
						.getUserLocale().getLanguage())) {
			getManagedBean().setBondAmountWord(
					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		} else {
			getManagedBean().setBondAmountWord(null);
		}
		// #35977 - End
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
		//SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
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

		String gstRegNo = scLoa.getGstRegNo() != null ? scLoa.getGstRegNo(): SSTConstant.UNDERLINE;
		context.put(SSTConstant.GST_REG_NO, gstRegNo);

		String gstEffectiveDate = scLoa.getGstEffDate() != null ? DateUtils
				.getLocalizeDate(scLoa.getGstEffDate(), myLocale) : SSTConstant.UNDERLINE;
		context.put("gstEffectiveDate", gstEffectiveDate);

		String gstDeclaredDate = scLoa.getGstDeclaredDate() != null ? gstEffectiveDate = DateUtils
				.getLocalizeDate(scLoa.getGstDeclaredDate(), myLocale) : "______________";
		context.put("gstDeclaredDate", gstDeclaredDate);

		String offerPrice = SSTConstant.UNDERLINE;
		String offerPriceWording = SSTConstant.UNDERLINE;
		BigDecimal loaAmount = scLoa.getLoaAmt();
				
		if (loaAmount != null) {
			offerPrice = amountDF.format(loaAmount);
			offerPriceWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
		}
		
		context.put(SSTConstant.OFFER_PRICE, offerPrice);
		context.put(SSTConstant.OFFER_PRICE_WORDING, offerPriceWording);
	
		String sstAmount = MALAY_NA;
		String overallctAmt = SSTConstant.UNDERLINE;
		String overallctAmtWording = SSTConstant.UNDERLINE;
		BigDecimal sstAmt = scLoa.getSstAmt();		
		
		if (sstAmt != null && loaAmount != null) {//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
			sstAmount = amountDF.format(sstAmt);
			overallctAmt = amountDF.format(loaAmount.add(sstAmt));
			log.info(":::setSstInfoForAgreementLetter(). loaId: "+ scLoa.getLoaId() +". overallctAmt inc. sstAmt: "+ overallctAmt);
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.add(sstAmt).toString()));						
		} else if (loaAmount != null) {//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
			overallctAmt = amountDF.format(loaAmount);
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
		}
		
		context.put(SSTConstant.SST_AMOUNT, sstAmount);
		context.put(SSTConstant.OVERALL_CONTRACT_AMOUNT, overallctAmt);
		context.put(SSTConstant.OVERALL_CONTRACT_AMOUNT_WORDING, overallctAmtWording);
				
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

	/*		if(scQt != null) {
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
		}*/
		
	// #26770 new kod bidang
		if ((smSupplier.getSmAppl() != null && smSupplier.getSmAppl().getSmSupplierCategories() != null)
				&& !smSupplier.getSmAppl().getSmSupplierCategories().isEmpty()) {
			int counter = 0;
			StringBuilder categoryCode = new StringBuilder();
			for (SmSupplierCategory category : smSupplier.getSmAppl().getSmSupplierCategories()) {
				String l1Code = category.getPmCategoryL1().getCategoryL1Code();
				String l2Code = category.getPmCategoryL2().getCategoryL2Code();
				String l3Code = category.getPmCategoryL3().getCategoryL3Code();
				String catcode = l1Code + l2Code + l3Code + SSTConstant.SEPARATOR;

				counter++;
				if (counter % 4 == 0 && counter < smSupplier.getSmAppl().getSmSupplierCategories().size()) {
					catcode = catcode + SSTConstant.LINE_BREAK;
				}
				categoryCode.append(catcode);
			}
			kodBidang = StringUtils.removeEnd(categoryCode.toString(), SSTConstant.SEPARATOR);
		}
		
	//26770 - end	
		}
		context.put("mofValidityDuration", mofValidityDuration);
		context.put("mofCertValidityDuration", mofCertValidityDuration);
		context.put("isBumi", isBumi);		
		context.put("kodBidang", kodBidang);
		// #25025 - Start.
		if (StringUtils.isNotEmpty(scLoiLoa.getMofNo())) {
			context.put("mofNo", scLoiLoa.getMofNo());
		} else {
			context.put("mofNo", SSTConstant.UNDERLINE);
		}
		// #25025 - End.
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

		String regNo = SSTConstant.TB;

		if(StringUtils.isNotEmpty(smSupplier.getRegNo())) {
			regNo = smSupplier.getRegNo();
		}
		context.put(SSTConstant.REGISTRATION_NO, regNo);
		//#26770 - start
		if (null != smSupplier.getSmAppl()){
			if (null != smSupplier.getSmAppl().getSmCompanyBasic()){
				if (null != smSupplier.getSmAppl().getSmCompanyBasic().getRobExpiryDate()){
					context.put(SSTConstant.SSM_EXPIRED,new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT).format(smSupplier.getSmAppl().getSmCompanyBasic().getRobExpiryDate()));
				}else{
					context.put(SSTConstant.SSM_EXPIRED,SSTConstant.UNDERLINE); 
				}
			}
		}
		//#26770 - end
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
		String procurementMode = ""; //20230706@asyrafhaziq- SQUBE Bug NullPointer#3
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
			
			//#29006 - start
			String acceptMsg = getMessage("lbl.sc.loa.supplier.confirm.button.accept.question",
					new String[] { loa.trim(), procurementMode.trim() });
			//#29006 - end
			
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
	
	
	public Boolean canShowSupplierProposal(){
		
		// ***25201#82 - start
		// allow as in ***25201#35
		return true;
		// ***25201#82 - end
		/*IUserSO userSO = null;
		try{
			userSO = BusinessDelegate.getInstance()
					.getServiceObject(IUserSO.class);
			
			
			
			*//**
			 * 	1. find qt pm_org_profile
			 *  2. find user pm_org_profile
			 *  3. give permission to user within same organization with the role accesibility  
			 *  (cancel)4. by state group (sabah, sarawak, peninsular) within same PTJ
			 *//*
			
			//1			
			ScQt scQt = getManagedBean().getScQt();
			PmOrgProfile qtOrgProfile =  scQt.getPmOrgProfile();
						
			//2 & 3
			Long userId = getFacesScopes().getUserSessionBean().getUserId();
			debugLogger(getFacesScopes().getUserRoleList());
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(userId);
			reqDTO.setOrgProfileId(qtOrgProfile.getOrgProfileId());
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			RoleCode[] roleCodes = {
					RoleCode.DESK_OFFICER,
					RoleCode.FPB_SECRETARY,
					RoleCode.QCA_SECRETARY,
					RoleCode.QCB_SECRETARY,
					RoleCode.PBA_SECRETARY,
					RoleCode.PBB_SECRETARY};
			List<String> roleCodesList = new ArrayList<String>();
			for(RoleCode s: roleCodes){
				roleCodesList.add(s.value());
			}
			reqDTO.setRoleCodes(roleCodesList);
			reqDTO.setForceGetAllUserOrgList(false);
			UserResDTO resDTO = userSO.checkAndGetUserInOrgProfile(reqDTO);
			reqDTO.setForceGetAllUserOrgList(true);
			UserResDTO allUserOrgResDTO = userSO.checkAndGetUserInOrgProfile(reqDTO);
			reqDTO.setForceGetAllUserOrgList(false);
			
			boolean ret = false;
			if(resDTO!=null && resDTO.isSuccessful()){
				debugLogger("checkUserInOrgProfile Successful");
				if(!CollectionUtils.isEmpty(resDTO.getUserOrgList())){
					ret = true;
				}
			}
			// ***25201#82 - start
			if(roleCodes!=null && roleCodes.length > 0){
				if(userHasRole(roleCodes)){
					ret = true;
				}
			}
			// ***25201#82 - end
			
			return ret;
			
		}
		finally{
			userSO = null;
		}*/
	}
	
	// ***25201#35
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
				QTStatusConst.LOA_VERIFY,//xi Bug #27412 #33 #35
				QTStatusConst.LOA_REVERSED,//xi Bug #27412 #33 #35
				QTStatusConst.LOA_REVISE_DOC,//xi Bug #27412 #33 #35
				QTStatusConst.LOA_CANCELLED_REVERSE //xi Bug #27412 #33 #35
				};
		if(getManagedBean()!=null && getManagedBean().getScLoa()!=null && getManagedBean().getScLoa().getPmStatus()!=null && getManagedBean().getScLoa().getPmStatus().getStatusId()!=null){
			for(Long s : statusList){
				if(s.longValue() == getManagedBean().getScLoa().getPmStatus().getStatusId().longValue()){
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	private FacesScopes getFacesScopes(){
		FacesScopes fs = new FacesScopes();
		return fs;
	}
	*/
	
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
	}//#25022-end	
	
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
	//#29006 - start
	public void onClickSpecsAndSupplierOfferLink(ScLoiLoaItem scLoiLoaItem) {
		if (StringUtils.equals(new FacesScopes().getUserLocale().getLanguage(), "en")) {
			setNotAvailabilityStr("N/A");
		} else {
			setNotAvailabilityStr(MALAY_NA);
		}
		
		Long currentSupplier = getManagedBean().getScLoa().getScLoiLoa().getSmSupplier().getSupplierId();

		setSelectedSupplierOffer(null);
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		List<TplSpecAnswer> tplSpecAnswerList = templateSpecSO.findSpecAnswersByQuestion(scLoiLoaItem
				.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());
		StringBuffer supplierOffer = new StringBuffer("");
		Boolean isZonalWithPaOrPs = isZonalWithPaOrPs();
		
		if (CollectionUtils.isNotEmpty(tplSpecAnswerList)) {
			int count = 0;
			String remarkValue = "";
			if (isZonalWithPaOrPs) {
				for (TplSpecAnswer tplSpecAnswer : tplSpecAnswerList) {
					
					if(tplSpecAnswer.getIsWip() == null){
						tplSpecAnswer.setIsWip(false);
					}
					
					if (tplSpecAnswer.getIsWip()) {
						if(tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier().getSupplierId().equals(currentSupplier)){
							if (StringUtils.isNotBlank(tplSpecAnswer.getAnswerRemark()))
								remarkValue = tplSpecAnswer.getAnswerRemark();
							else
								remarkValue = getNotAvailabilityStr();
	
							supplierOffer.append("Zone "
									+ tplSpecAnswer.getTplSpecSuppAnswer().getTplSpecZoneDtl().getZoneName() + " : "
									+ remarkValue);
	
							remarkValue = "";
							if (count < tplSpecAnswerList.size()) {
								supplierOffer.append(System.lineSeparator()+"<br/>");
							}
							count++; //29006-963
						}
					} else {
						if (StringUtils.isNotBlank(tplSpecAnswer.getAnswerRemark())) {
							if(tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier().getSupplierId().equals(currentSupplier)){
								supplierOffer.append(tplSpecAnswer.getAnswerRemark());
								break;
							}
						}
					}
				}
				
				//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				getManagedBean().setMultipleSuppOffer(count > 1);
				//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				setSelectedSupplierOffer(supplierOffer.toString());
			}

			else {
				for (TplSpecAnswer tplSpecAnswer : tplSpecAnswerList) {
					if (StringUtils.isNotBlank(tplSpecAnswer.getAnswerRemark())) {
						if(tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier().getSupplierId().equals(currentSupplier))						
							setSelectedSupplierOffer(tplSpecAnswer.getAnswerRemark());
					}
				}
			}
		}

		try {
			selectedScLoiLoaItem = scLoiLoaItem;
			getManagedBean().setSelectedItemId(scLoiLoaItem.getLoiLoaItemId());
			// #29006 - start - reendorsed (comment 528)
			
//29006-916 - comment start 		
//			//note 901
//			String selectSuppOffer = getSelectedSupplierOffer().replaceAll(QTConstants.LESS_THEN, QTConstants.HTML_LESS_THEN);
//			selectSuppOffer = selectSuppOffer.replaceAll(QTConstants.GREATER_THEN, QTConstants.HTML_GREATER_THEN);		
			getManagedBean().setSelectedSupplierOffer(getSelectedSupplierOffer());
//29006-916 - comment end 			
			getManagedBean().setItemNamePopup(scLoiLoaItem.getScRequestItem().getItemName());
			// #29006 - end - reendorsed (comment 528)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getManagedBean().setScLoiLoaItemSpecsList(new ArrayList<String>());
		if ((scLoiLoaItem.getScRequestItem() != null)
				&& (StringUtils.isNotBlank(scLoiLoaItem.getScRequestItem().getItemName()))) {
			// Populate rows under Specifications column
			List<TplSpecQuestion> tpqSpecList = templateSpecSO.findQuestionsBySpecMasterIdAndParentQuestionId(
					scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getTplSpecMaster().getSpecMasterId(), "S",
					scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());
			//36103 - start
			String specDtl;
			if (CollectionUtils.isEmpty(tpqSpecList)) {
				specDtl = scLoiLoaItem.getScRequestItem().getItemDesc();
				if (getManagedBean().getScLoa().getPmStatus() != null
						&& getManagedBean().getScLoa().getPmStatus().getStatusId()
								.equals(QTStatusConst.LOA_PENDING_VERIFICATION)
						|| getManagedBean().getScLoa().getPmStatus().getStatusId().equals(QTStatusConst.LOA_COMPLETED)) {
					specDtl = specDtl.replaceAll(TemplateSpecConst.LESS_THEN_SYMBOL,
							TemplateSpecConst.LESS_THEN_HTML_ENTITY);
					specDtl = specDtl.replaceAll(TemplateSpecConst.GREATER_THEN_SYMBOL,
							TemplateSpecConst.GREATER_THEN_HTML_ENTITY);
					specDtl = specDtl.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER,
							TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
				}
				getManagedBean().getScLoiLoaItemSpecsList().add(specDtl);
			} else {
				for (TplSpecQuestion tpqSpec : tpqSpecList) {
					if (StringUtils.isNotBlank(tpqSpec.getClSpecDetail())) {
						specDtl = tpqSpec.getClSpecDetail();
						if (getManagedBean().getScLoa().getPmStatus() != null
								&& getManagedBean().getScLoa().getPmStatus().getStatusId()
										.equals(QTStatusConst.LOA_PENDING_VERIFICATION)
								|| getManagedBean().getScLoa().getPmStatus().getStatusId()
										.equals(QTStatusConst.LOA_COMPLETED)) {
							specDtl = specDtl.replaceAll(TemplateSpecConst.LESS_THEN_SYMBOL,
									TemplateSpecConst.LESS_THEN_HTML_ENTITY);
							specDtl = specDtl.replaceAll(TemplateSpecConst.GREATER_THEN_SYMBOL,
									TemplateSpecConst.GREATER_THEN_HTML_ENTITY);
							specDtl = specDtl.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER,
									TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
						}
						getManagedBean().getScLoiLoaItemSpecsList().add(specDtl);
					}
				}
				//36103 - end
				//29006 @zaim
				getManagedBean().setListSpecAnswer(tpqSpecList);
				//29006 @zaim
			}
			
			//#31869 - start
			if(getManagedBean().getSelectedItemId() == scLoiLoaItem.getLoiLoaItemId()){
				ScRequestItem requestItem = scLoiLoaItem.getScRequestItem();
				RequestItemReqDTO itemReqDTO = new RequestItemReqDTO();
				itemReqDTO.setRequestItemId(requestItem.getRequestNoteItemId());							
				ScRequestItem selectedRequestItem = getRequestItemSO().retrieveItemByRequestItemId(itemReqDTO).getRequestItem();
				
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecQuestionId(selectedRequestItem.getTplSpecQuestion().getSpecQuestionId());
				TemplateSpecResDTO specResDTO = templateSpecSO.retrieveSpecAnswersByQuestionId(specReqDTO);
				//#34766 - start
				AwardSupplierReqDTO reqDTO=new AwardSupplierReqDTO();
				reqDTO.setScLoiLoaItem(scLoiLoaItem);
				
				//start - #29006 note 819
				AwardSupplierResDTO resDTO  = new AwardSupplierResDTO();
				
				if(reqDTO.getScLoiLoaItem().getLoiLoaItemId() != null)
					resDTO = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTO);
				//end - #29006 note 819
				
				//#34766 - end
				
				//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				List<String> nsoJustList = new ArrayList<>();
				//[End] -Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				if(specResDTO.getSpecAnswerList().size() > 0){
					for(TplSpecAnswer tplSpecAnswer : specResDTO.getSpecAnswerList()){
						if (tplSpecAnswer.getNewSupplierOffer() != null
								&& tplSpecAnswer.getNewSupplierOffer().trim().length() > 0
								&& StringUtils.isNotBlank(tplSpecAnswer.getNewSupplierOffer())) {							
							nsoJustList.add("NSO_" + tplSpecAnswer.getNewSupplierOffer());
							//#34766 - start
							nsoJustList.add("NSOJ_" + resDTO.getScLoiLoaItem().getLoaSpecJustification());
							//nsoJustList.add("NSOJ_" + scLoiLoaItem.getLoaSpecJustification());
							//#34766 - end
							break;
						}
					}
				}
				
				if(!nsoJustList.isEmpty()){
					if (MapUtils.isEmpty(getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap())) {
						getManagedBean().setScRequestItemIdNewSuppOfferAndJustMap(new HashMap<Long, List<String>>());
					}
					
					getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap().put(
							requestItem.getRequestNoteItemId(), nsoJustList);
				}
			}
			//#31869 - end
			
			// Populate New SUpplier Offered and Justification fields for
			// selcted item
			if (MapUtils.isEmpty(getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap())) {
				getManagedBean().setScRequestItemIdNewSuppOfferAndJustMap(new HashMap<Long, List<String>>());
			}
			for (Map.Entry<Long, List<String>> entry : getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap()
					.entrySet()) {
				if (entry.getKey() != null
						&& entry.getKey().compareTo(scLoiLoaItem.getScRequestItem().getRequestNoteItemId()) == 0) {
					List<String> nsoJustList = entry.getValue();
					getManagedBean().setNewSupplierOffered(
							StringUtils.isNotBlank(nsoJustList.get(0)) ? nsoJustList.get(0).replace("NSO_",
									StringUtils.EMPTY) : StringUtils.EMPTY);
					getManagedBean().setNewSupplierJustification(
							StringUtils.isNotBlank(nsoJustList.get(1)) ? nsoJustList.get(1).replace("NSOJ_",
									StringUtils.EMPTY) : StringUtils.EMPTY);
				}
			}
		}
		// #29006 - start - reendorsed (comment 528)
		if (getManagedBean().getNewSupplierOffered() != null || getManagedBean().getNewSupplierJustification() != null) {
			if (getManagedBean().getNewSupplierOffered().equalsIgnoreCase("null"))
				getManagedBean().setNewSupplierOffered(StringUtils.EMPTY);
			if (getManagedBean().getNewSupplierJustification().equalsIgnoreCase("null"))
				getManagedBean().setNewSupplierJustification(StringUtils.EMPTY);
		}
		// #29006 - end - reendorsed (comment 528)
	}

	public void onSearchSupplier() {
		if (CollectionUtils.isEmpty(completeScLoiLoaItemList)) {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(getManagedBean().getScLoa().getLoaId());
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOADtlByLoaId(reqDTO);
			if (CollectionUtils.isNotEmpty(resDTO.getScLoa().getScLoiLoaItems())) {
				setCompleteScLoiLoaItemList(resDTO.getScLoa().getScLoiLoaItems());
				for (ScLoiLoaItem item : completeScLoiLoaItemList) {
					RequestItemReqDTO reqRequestItemDTO = new RequestItemReqDTO();
					reqRequestItemDTO.setRequestItemId(item.getScRequestItem().getRequestNoteItemId());
					ScRequestItem freshRequestItem = getRequestItemSO().retrieveItemByRequestItemId(reqRequestItemDTO)
							.getRequestItem();
					if (freshRequestItem != null) {
						item.setScRequestItem(freshRequestItem);
					}
				}
			}
		}
		if (StringUtils.isBlank(itemNameSearchCriteria)) {
			getManagedBean().getScLoa().setScLoiLoaItems(getCompleteScLoiLoaItemList());
		} else {
			List<ScLoiLoaItem> filteredItemList = new ArrayList<>();
			for (ScLoiLoaItem item : completeScLoiLoaItemList) {
				
				String searchCriteria = itemNameSearchCriteria.toUpperCase().trim().replaceAll("[ ]{2,}", " ");
				String checkedItem = item.getScRequestItem().getItemName().toUpperCase().trim().replaceAll("[ ]{2,}", " ");
				
				if (item.getScRequestItem() != null
						&& StringUtils.isNotBlank(item.getScRequestItem().getItemName())
						&& StringUtils
								.containsIgnoreCase(checkedItem, searchCriteria)) {
					filteredItemList.add(item);
				}
			}
			getManagedBean().getScLoa().setScLoiLoaItems(filteredItemList);
		}
		//Bug 34404 - related to 29007 - start
		if(this.getManagedBean().getScLoa()!=null){
			this.getManagedBean().setLoiLoaItemsResult(new ArrayList<ScLoiLoaItem>(this.getManagedBean().getScLoa().getScLoiLoaItems()));//29007 - fix issue 2 search action note 347			
		}else{
			this.getManagedBean().setLoiLoaItemsResult(null);
		}
		//Bug 34404 - related to 29007 - end
	}

	public void onResetSupplierCriteria() {
		setItemNameSearchCriteria(null);
		this.getManagedBean().setLoiLoaItemsResult(new ArrayList<ScLoiLoaItem>(this.getManagedBean().getScLoa().getScLoiLoaItems()));//29007 - fix issue 2 search action note 347
	}

	public void onClickSupplierProposalUpdateLink(ScLoiLoaItem scLoiLoaItem) {
		onClickSpecsAndSupplierOfferLink(scLoiLoaItem);
		PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext()
				.getSession(false);
		portletSession.setAttribute("selectedScLoiLoaItem", scLoiLoaItem);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/sc/loa/loarequest/supplierProposalUpdate.xhtml");
	}

	public void onBackToZoneItemDetailsTab() {
		onResetNewSupplierOffered();
		getManagedBean().setNewSupplierJustification(null);
		getManagedBean().setSelectedItemId(null);
		getManagedBean().setSelectedTabIndex(2);
		this.getManagedBean().setSpecKeyword(null);//29007
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, QTConstants.LOA_PAGE);
	}

	public void onResetNewSupplierOffered() {
		getManagedBean().setNewSupplierOffered(null);
	}
	// #29006 - start - reendorsed (comment 528)
	public void onResetAll() {
		RequestContext.getCurrentInstance().execute("resetConfirmDlg.show()");	
	}
	
	public void onResetAllAction() {
			removeUnwantedLampiranA3Report(2);
			this.getManagedBean().setScLoiLoaItemAnswerMap(new HashMap<Long, List<TplSpecAnswer>>());
			for (ScLoiLoaItem each : getManagedBean().getScLoa().getScLoiLoaItems()) {
				List<TplSpecAnswer> tpaList = getTemplateSpecSO().findSpecAnswersByQuestion(
						each.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());
				if ((each.getScRequestItem() != null)
						&& (StringUtils.isNotBlank(each.getScRequestItem().getItemName()))
						&& getManagedBean().getSelectedItemId().compareTo(each.getLoiLoaItemId()) == 0) {
					AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
					reqDTO.setTplSpecAnswerList(new ArrayList<TplSpecAnswer>());
					List<Long> tpaIdList = new ArrayList<>(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					int oldNewSuppOfferSameCount = 0;
					List<TplSpecAnswer> clonedAnswerList = new ArrayList<>(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					for (TplSpecAnswer tpa : tpaList) {
						tpa.setNewSupplierOffer(null);
						tpaIdList.add(tpa.getSpecAnswerId());
						if (StringUtils.isNotBlank(tpa.getAnswerRemark())
								&& StringUtils.equalsIgnoreCase(null,
										tpa.getAnswerRemark())) {
							oldNewSuppOfferSameCount++;
						}
						try {
							// POPULATE tpaClone GOES HERE
							TplSpecAnswer tpaClone = new TplSpecAnswer();
							tpaClone = populateTplSpecAnswerClone(tpa, each.getScRequestItem().getTplSpecQuestion(), tpaClone);
							// END
							clonedAnswerList.add(tpaClone);
						} catch (Exception e) {
							//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
							log.error(e);
							//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
						}
					}
					// Put into scLoiLoaItemAnswerMap for use in generating
					// updated Lampiran A3 later
					this.getManagedBean().getScLoiLoaItemAnswerMap().put(each.getLoiLoaItemId(), clonedAnswerList);
					//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					getManagedBean().setOldNewSuppOfferSameFlag(oldNewSuppOfferSameCount > 0);
					//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					each.setLoaSpecJustification(null);
					try {
						// Save and update new supplier offer into each related TplSpecAnswer in DB
						getTemplateSpecSO().updateNewSupplierOffer(null, tpaIdList);
					} catch (Exception e) {
						//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
						log.error(e);
						//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
					}
					try {
						// Save and update new supplier offer into related ScLoiLoaItem in DB
						getQtAwardSO().updateAndSaveNewSupplierJustification(null,
								each.getLoiLoaItemId(), false); //#29006 - note 607
						log.info("-- SAVE JUSTIFICATION --");
						for(ScLoiLoaItem scLoiLoaItem : getManagedBean().getNameZoneScLoiLoaItemList()){
							log.info("-- RELOAD JUSTIFICATION FOR ZONAL --");
							log.info("scLoiLoaItem.getLoiLoaItemId() : "+scLoiLoaItem.getLoiLoaItemId());
							log.info("each.getLoiLoaItemId() : "+each.getLoiLoaItemId());
							if(scLoiLoaItem.getLoiLoaItemId().compareTo(each.getLoiLoaItemId()) == 0){
								log.info("-- value assign into managedbean --");
								scLoiLoaItem.setLoaSpecJustification(null);
							}							
						}
						
					} catch (Exception e) {
						//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
						log.error(e);
						//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
					}
					//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					List<String> nsoJustList = new ArrayList<>();
					//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					nsoJustList.add("NSO_" + null);
					nsoJustList.add("NSOJ_" + null);
					if (MapUtils.isEmpty(getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap())) {
						getManagedBean().setScRequestItemIdNewSuppOfferAndJustMap(new HashMap<Long, List<String>>());
						getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap().put(
								each.getScRequestItem().getRequestNoteItemId(), nsoJustList);
					} else {
						boolean itemFound = false;
						for (Map.Entry<Long, List<String>> entry : getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap()
								.entrySet()) {
							if (entry.getKey() != null
									&& entry.getKey().compareTo(each.getScRequestItem().getRequestNoteItemId()) == 0) {
								entry.setValue(nsoJustList);
								itemFound = true;
							}
						}
						if (!itemFound) {
							getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap().put(
									each.getScRequestItem().getRequestNoteItemId(), nsoJustList);
						}
					}
					/*if (outcome.equalsIgnoreCase("SAVE")) {
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null, ResourceBundleUtils.getMessage(
										"lbl.sc.update.success", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale())));
						// remove existing updated Lampiran A3
						removeUnwantedLampiranA3Report(2);
					}*/
					
				} else {
					// Need to populate detauls into
					// getManagedBean().getScLoiLoaItemAnswerMap()
					// even if the item does not have new supplier offered.
					// Otherwise, the Updated Lampiran A3 will not display
					// the data of items without new supplier offered.
					//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					List<TplSpecAnswer> clonedAnswerList = new ArrayList<>();
					//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					for (TplSpecAnswer tpa : tpaList) {
						try {
							// POPULATE tpaClone GOES HERE
							TplSpecAnswer tpaClone = new TplSpecAnswer();
							tpaClone = populateTplSpecAnswerClone(tpa, each.getScRequestItem().getTplSpecQuestion(),
									tpaClone);
						
							// END
							clonedAnswerList.add(tpaClone);
						} catch (Exception e) {
							//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
							log.error(e);
							//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
						}
					}
					// Put into scLoiLoaItemAnswerMap for use in generating
					// updated Lampiran A3 later
					this.getManagedBean().getScLoiLoaItemAnswerMap().put(each.getLoiLoaItemId(), clonedAnswerList);
				}
			}
		
	
		getManagedBean().setNewSupplierOffered(null);
		getManagedBean().setNewSupplierJustification(null);
		//#29006 - start - note 619		
		boolean justificationIsChanged = false;
		for (ScLoiLoaItem scLoiLoaItem : getManagedBean().getScLoa().getScLoiLoaItems()) {
			List<ScLoiLoaItem> items = getQtAwardSO().getLoiLoaItemByLoaIdAndReqItemId(
					scLoiLoaItem.getScLoa().getLoaId(), scLoiLoaItem.getScRequestItem().getRequestNoteItemId());
			
			//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
			if (!items.isEmpty()) {
			//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				justificationIsChanged = true;
				break;
			}
		}if (justificationIsChanged) {
			createLampiranA3(null);
			//note 870,872 - start
			for (ScLoaReport scLoaReport : this.getManagedBean().getScLoa().getScLoaReports()) {
				if (scLoaReport.getTitle().contains(lampiranA3SubStr)
						&& !ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa()
								.getDocType())) {
					try {
						loadScLoaReport(scLoaReport);
					} catch (Exception e) {
						//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [23/4/2024]
						log.error("Error : " + e.getMessage());
						//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [23/4/2024]
					}
				}
			}
			//note 870,872 - end
			//onBackToZoneItemDetailsTab(); #29006 - note 633
		}
		//#29006 - end - note 619
	}
	// #29006 - end - reendorsed (comment 528)
	public void onSaveSupplierOffer(String outcome) {
		//#29006 - start - note 662
		AwardSupplierReqDTO requestDTO = new AwardSupplierReqDTO();
		requestDTO.setLoaId(getManagedBean().getScLoa().getLoaId());
		AwardSupplierResDTO responseDTO = getQtAwardSO().retrieveLOADtlByLoaId(requestDTO);
		if (CollectionUtils.isNotEmpty(responseDTO.getScLoa().getScLoiLoaItems())) {
			 getManagedBean().getScLoa().setScLoiLoaItems(responseDTO.getScLoa().getScLoiLoaItems());
			for (ScLoiLoaItem item : getManagedBean().getScLoa().getScLoiLoaItems()) {
				RequestItemReqDTO reqRequestItemDTO = new RequestItemReqDTO();
				reqRequestItemDTO.setRequestItemId(item.getScRequestItem().getRequestNoteItemId());
				ScRequestItem freshRequestItem = getRequestItemSO().retrieveItemByRequestItemId(reqRequestItemDTO)
						.getRequestItem();
				if (freshRequestItem != null) {
					item.setScRequestItem(freshRequestItem);
				}
			}
		}
		//#29006 - end - note 662
		//#29006 - start - note 607
		Boolean isComplete = false;
		if (outcome.equalsIgnoreCase("COMPLETE")) {
			isComplete = true;
		}
		//#29006 - end - note 607
		if (StringUtils.isBlank(getManagedBean().getNewSupplierOffered())
				&& StringUtils.isBlank(getManagedBean().getNewSupplierJustification()) && outcome.equalsIgnoreCase("COMPLETE")) { // #29006 (allow complete outcome only-fixing on comment 570)		
			
			// start - #29006 (fixing on comment 570)
			StringBuffer mandatoryItems = new StringBuffer();
			String justificationMandatory =	ResourceBundleUtils.getMessage("lbl.sc.new.supplier.offered.justification.mandatory",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String supplierOfferMandatory=	ResourceBundleUtils.getMessage("lbl.sc.new.supplier.offer.mandatory",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			
			mandatoryItems.append(justificationMandatory+"<br/>"+SpecialCharacter.NON_BREAKING_SPACE.getHtmlEntityName()+supplierOfferMandatory); //#29006 - note 702
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
							mandatoryItems.toString(),
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			// end - #29006 (fixing on comment 570)
			return;
		}
		if (StringUtils.isBlank(getManagedBean().getNewSupplierOffered()) && outcome.equalsIgnoreCase("COMPLETE")) { // #29006 (allow complete outcome only-fixing on comment 570)
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
							"lbl.sc.new.supplier.offer.mandatory", ResourceBundleUtils.BUNDLE_COMMON_LABELS, // #29006 - reendorsed (comment 528)
							new FacesScopes().getUserLocale())));
			return;
		}
		if (StringUtils.isBlank(getManagedBean().getNewSupplierJustification()) && outcome.equalsIgnoreCase("COMPLETE")) { // #29006 (allow complete outcome only-fixing on comment 570)
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
							"lbl.sc.new.supplier.offered.justification.mandatory",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			return;
		}
		if (StringUtils.isNotEmpty(getManagedBean().getNewSupplierOffered()) && StringUtils.isNotEmpty(getManagedBean().getNewSupplierJustification())) {
			this.getManagedBean().setScLoiLoaItemAnswerMap(new HashMap<Long, List<TplSpecAnswer>>());
			for (ScLoiLoaItem each : getManagedBean().getScLoa().getScLoiLoaItems()) {
				List<TplSpecAnswer> tpaList = getTemplateSpecSO().findSpecAnswersByQuestion(
						each.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());
				if ((each.getScRequestItem() != null)
						&& (StringUtils.isNotBlank(each.getScRequestItem().getItemName()))
						&& getManagedBean().getSelectedItemId().compareTo(each.getLoiLoaItemId()) == 0) {
					AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
					reqDTO.setTplSpecAnswerList(new ArrayList<TplSpecAnswer>());
					List<Long> tpaIdList = new ArrayList<Long>();
					int oldNewSuppOfferSameCount = 0;
					List<TplSpecAnswer> clonedAnswerList = new ArrayList<TplSpecAnswer>();
					for (TplSpecAnswer tpa : tpaList) {
						tpa.setNewSupplierOffer(getManagedBean().getNewSupplierOffered());
						tpaIdList.add(tpa.getSpecAnswerId());
						if (StringUtils.isNotBlank(tpa.getAnswerRemark())
								&& StringUtils.equalsIgnoreCase(getManagedBean().getNewSupplierOffered(),
										tpa.getAnswerRemark())) {
							oldNewSuppOfferSameCount++;
						}
						try {
							// POPULATE tpaClone GOES HERE
							TplSpecAnswer tpaClone = new TplSpecAnswer();
							tpaClone = populateTplSpecAnswerClone(tpa, each.getScRequestItem().getTplSpecQuestion(), tpaClone);
							// END
							clonedAnswerList.add(tpaClone);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// Put into scLoiLoaItemAnswerMap for use in generating
					// updated Lampiran A3 later
					this.getManagedBean().getScLoiLoaItemAnswerMap().put(each.getLoiLoaItemId(), clonedAnswerList);
					if (oldNewSuppOfferSameCount > 0) {
						getManagedBean().setOldNewSuppOfferSameFlag(true);
					} else {
						getManagedBean().setOldNewSuppOfferSameFlag(false);
					}
					each.setLoaSpecJustification(getManagedBean().getNewSupplierJustification());
					try {
						// Save and update new supplier offer into each related TplSpecAnswer in DB
						getTemplateSpecSO().updateNewSupplierOffer(getManagedBean().getNewSupplierOffered(), tpaIdList);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						// Save and update new supplier offer into related ScLoiLoaItem in DB
						//#29006 - note 702 - start
						if (!StringUtils.isBlank(getManagedBean().getNewSupplierOffered()))
							getQtAwardSO().updateAndSaveNewSupplierJustification("",
									each.getLoiLoaItemId(),isComplete); 
						//#29006 - note 702 - end
						getQtAwardSO().updateAndSaveNewSupplierJustification(getManagedBean().getNewSupplierJustification(),
								each.getLoiLoaItemId(),isComplete); //#29006 - note 607
						log.info("-- SAVE JUSTIFICATION --");
						//29006 @zaim start add null check
						if (getManagedBean().getNameZoneScLoiLoaItemList() != null && !getManagedBean().getNameZoneScLoiLoaItemList().isEmpty()){
							for(ScLoiLoaItem scLoiLoaItem : getManagedBean().getNameZoneScLoiLoaItemList()){
								log.info("-- RELOAD JUSTIFICATION FOR ZONAL --");
								log.info("scLoiLoaItem.getLoiLoaItemId() : "+scLoiLoaItem.getLoiLoaItemId());
								log.info("each.getLoiLoaItemId() : "+each.getLoiLoaItemId());
								if(scLoiLoaItem.getLoiLoaItemId().compareTo(each.getLoiLoaItemId()) == 0){
									log.info("-- value assign into managedbean --");
									scLoiLoaItem.setLoaSpecJustification(getManagedBean().getNewSupplierJustification());
								}							
							}
						}
						//29006 @zaim end
					} catch (Exception e) {
						e.printStackTrace();
					}
					List<String> nsoJustList = new ArrayList<String>();
					nsoJustList.add("NSO_" + getManagedBean().getNewSupplierOffered());
					nsoJustList.add("NSOJ_" + getManagedBean().getNewSupplierJustification());
					if (MapUtils.isEmpty(getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap())) {
						getManagedBean().setScRequestItemIdNewSuppOfferAndJustMap(new HashMap<Long, List<String>>());
						getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap().put(
								each.getScRequestItem().getRequestNoteItemId(), nsoJustList);
					} else {
						boolean itemFound = false;
						for (Map.Entry<Long, List<String>> entry : getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap()
								.entrySet()) {
							if (entry.getKey() != null
									&& entry.getKey().compareTo(each.getScRequestItem().getRequestNoteItemId()) == 0) {
								entry.setValue(nsoJustList);
								itemFound = true;
							}
						}
						if (!itemFound) {
							getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap().put(
									each.getScRequestItem().getRequestNoteItemId(), nsoJustList);
						}
					}
					if (outcome.equalsIgnoreCase("SAVE")) {
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null, ResourceBundleUtils.getMessage(
										"lbl.sc.update.success", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale())));
						// remove existing updated Lampiran A3
						removeUnwantedLampiranA3Report(2);
					}
					if (outcome.equalsIgnoreCase("COMPLETE")) {
						//each.setComplete(true); //#29006 - note 607
						getManagedBean().setNewSupplierUpdatedFlag(true);
						getManagedBean().setUpdatedLampiranA3DownloadedFlag(false);
					}
				} else {
					// Need to populate detauls into
					// getManagedBean().getScLoiLoaItemAnswerMap()
					// even if the item does not have new supplier offered.
					// Otherwise, the Updated Lampiran A3 will not display
					// the data of items without new supplier offered.
					List<TplSpecAnswer> clonedAnswerList = new ArrayList<TplSpecAnswer>();
					for (TplSpecAnswer tpa : tpaList) {
						try {
							// POPULATE tpaClone GOES HERE
							TplSpecAnswer tpaClone = new TplSpecAnswer();
							tpaClone = populateTplSpecAnswerClone(tpa, each.getScRequestItem().getTplSpecQuestion(),
									tpaClone);
							if (outcome.equalsIgnoreCase("COMPLETE")) {
								for (Map.Entry<Long, List<String>> entry : this.getManagedBean()
										.getScRequestItemIdNewSuppOfferAndJustMap().entrySet()) {
									if (entry.getKey().compareTo(each.getLoiLoaItemId()) == 0) {
										if (StringUtils.isNotBlank(entry.getValue().get(0))) {
											tpaClone.setNewSupplierOffer(entry.getValue().get(0)
													.replace("NSO_", StringUtils.EMPTY));
										}
									}
								}
							}
							// END
							clonedAnswerList.add(tpaClone);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// Put into scLoiLoaItemAnswerMap for use in generating
					// updated Lampiran A3 later
					this.getManagedBean().getScLoiLoaItemAnswerMap().put(each.getLoiLoaItemId(), clonedAnswerList);
				}
			}
			if (outcome.equalsIgnoreCase("COMPLETE")) {
				createLampiranA3(null);
				//#29006 - start - note 837
				for (ScLoaReport scLoaReport : this.getManagedBean().getScLoa().getScLoaReports()) {
				if (scLoaReport.getTitle().contains(lampiranA3SubStr)
							&& !ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa()
									.getDocType())) {
						try {
							loadScLoaReport(scLoaReport);
						} catch (Exception e) {
							//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
							log.error(e);
							//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
						}
					}
				}
				//#29006 - end - note 837
				onBackToZoneItemDetailsTab();
			}
		}
		
		// start - #29006 (fixing on comment 570)
		if (outcome.equalsIgnoreCase("SAVE")) {

			if (!StringUtils.isNotEmpty(getManagedBean().getNewSupplierOffered())
					|| !StringUtils.isNotEmpty(getManagedBean().getNewSupplierJustification())) {

				this.getManagedBean().setScLoiLoaItemAnswerMap(new HashMap<Long, List<TplSpecAnswer>>());
				for (ScLoiLoaItem each : getManagedBean().getScLoa().getScLoiLoaItems()) {
					List<TplSpecAnswer> tpaList = getTemplateSpecSO().findSpecAnswersByQuestion(
							each.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());
					if ((each.getScRequestItem() != null)
							&& (StringUtils.isNotBlank(each.getScRequestItem().getItemName()))
							&& getManagedBean().getSelectedItemId().compareTo(each.getLoiLoaItemId()) == 0) {
						AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
						reqDTO.setTplSpecAnswerList(new ArrayList<TplSpecAnswer>());
						List<Long> tpaIdList = new ArrayList<>(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
						int oldNewSuppOfferSameCount = 0;
						List<TplSpecAnswer> clonedAnswerList = new ArrayList<>(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
						for (TplSpecAnswer tpa : tpaList) {
							tpa.setNewSupplierOffer(getManagedBean().getNewSupplierOffered());
							tpaIdList.add(tpa.getSpecAnswerId());
							if (StringUtils.isNotBlank(tpa.getAnswerRemark())
									&& StringUtils.equalsIgnoreCase(getManagedBean().getNewSupplierOffered(),
											tpa.getAnswerRemark())) {
								oldNewSuppOfferSameCount++;
							}
							try {
								// POPULATE tpaClone GOES HERE
								TplSpecAnswer tpaClone = new TplSpecAnswer();
								tpaClone = populateTplSpecAnswerClone(tpa,
										each.getScRequestItem().getTplSpecQuestion(), tpaClone);
								// END
								clonedAnswerList.add(tpaClone);
							} catch (Exception e) {
								//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
								log.error(e);
								//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
							}
						}
						// Put into scLoiLoaItemAnswerMap for use in generating
						// updated Lampiran A3 later
						this.getManagedBean().getScLoiLoaItemAnswerMap().put(each.getLoiLoaItemId(), clonedAnswerList);
						//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
						getManagedBean().setOldNewSuppOfferSameFlag(oldNewSuppOfferSameCount > 0);
						//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
						each.setLoaSpecJustification(getManagedBean().getNewSupplierJustification());
						try {
							// Save and update new supplier offer into each
							// related TplSpecAnswer in DB
							// removed conditional statement - #35828 - bug 29006
								getTemplateSpecSO().updateNewSupplierOffer(getManagedBean().getNewSupplierOffered(),
										tpaIdList);
						} catch (Exception e) {
							//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
							log.error(e);
							//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
						}
						try {
							// Save and update new supplier offer into related
							// ScLoiLoaItem in DB
							// removed conditional statement - #35828 - bug 29006
								getQtAwardSO().updateAndSaveNewSupplierJustification(
										getManagedBean().getNewSupplierJustification(), each.getLoiLoaItemId(),isComplete); //#29006 - note 607
							log.info("-- SAVE JUSTIFICATION --");
							for (ScLoiLoaItem scLoiLoaItem : getManagedBean().getNameZoneScLoiLoaItemList()) {
								log.info("-- RELOAD JUSTIFICATION FOR ZONAL --");
								log.info("scLoiLoaItem.getLoiLoaItemId() : " + scLoiLoaItem.getLoiLoaItemId());
								log.info("each.getLoiLoaItemId() : " + each.getLoiLoaItemId());
								if (scLoiLoaItem.getLoiLoaItemId().compareTo(each.getLoiLoaItemId()) == 0) {
									log.info("-- value assign into managedbean --");
									scLoiLoaItem
											.setLoaSpecJustification(getManagedBean().getNewSupplierJustification());
								}
							}

						} catch (Exception e) {
							//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
							log.error(e);
							//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
						}
						List<String> nsoJustList = new ArrayList<>(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
						nsoJustList.add("NSO_" + getManagedBean().getNewSupplierOffered());
						nsoJustList.add("NSOJ_" + getManagedBean().getNewSupplierJustification());
						if (MapUtils.isEmpty(getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap())) {
							getManagedBean()
									.setScRequestItemIdNewSuppOfferAndJustMap(new HashMap<Long, List<String>>());
							getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap().put(
									each.getScRequestItem().getRequestNoteItemId(), nsoJustList);
						} else {
							boolean itemFound = false;
							for (Map.Entry<Long, List<String>> entry : getManagedBean()
									.getScRequestItemIdNewSuppOfferAndJustMap().entrySet()) {
								if (entry.getKey() != null
										&& entry.getKey().compareTo(each.getScRequestItem().getRequestNoteItemId()) == 0) {
									entry.setValue(nsoJustList);
									itemFound = true;
								}
							}
							if (!itemFound) {
								getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap().put(
										each.getScRequestItem().getRequestNoteItemId(), nsoJustList);
							}
						}
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null, ResourceBundleUtils.getMessage(
										"lbl.sc.update.success", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale())));
						// remove existing updated Lampiran A3
						removeUnwantedLampiranA3Report(2);

					} else {
						// Need to populate detauls into
						// getManagedBean().getScLoiLoaItemAnswerMap()
						// even if the item does not have new supplier offered.
						// Otherwise, the Updated Lampiran A3 will not display
						// the data of items without new supplier offered.
						List<TplSpecAnswer> clonedAnswerList = new ArrayList<>(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
						for (TplSpecAnswer tpa : tpaList) {
							try {
								// POPULATE tpaClone GOES HERE
								TplSpecAnswer tpaClone = new TplSpecAnswer();
								tpaClone = populateTplSpecAnswerClone(tpa,
										each.getScRequestItem().getTplSpecQuestion(), tpaClone);
								// END
								clonedAnswerList.add(tpaClone);
							} catch (Exception e) {
								//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
								log.error(e);
								//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
							}
						}
						// Put into scLoiLoaItemAnswerMap for use in generating
						// updated Lampiran A3 later
						this.getManagedBean().getScLoiLoaItemAnswerMap().put(each.getLoiLoaItemId(), clonedAnswerList);
					}
				}

			}
			//#29006 - start - note 619
			boolean justificationIsChanged = false;
			for (ScLoiLoaItem scLoiLoaItem : getManagedBean().getScLoa().getScLoiLoaItems()) {
				List<ScLoiLoaItem> items = getQtAwardSO().getLoiLoaItemByLoaIdAndReqItemId(
						scLoiLoaItem.getScLoa().getLoaId(), scLoiLoaItem.getScRequestItem().getRequestNoteItemId());
				
				//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				if (!items.isEmpty()) {
				//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
					justificationIsChanged = true;
					break;
				}
			}if (justificationIsChanged) {
				createLampiranA3(null);
				//onBackToZoneItemDetailsTab(); #29006 - note 633
			}
			//#29006 - end - note 619
			//#29006 - start - note 850,852,856
			for (ScLoaReport scLoaReport : this.getManagedBean().getScLoa().getScLoaReports()) {
			if (scLoaReport.getTitle().contains(lampiranA3SubStr)
						&& !ParameterConst.DOCTYPE_QUOTATION.equals(this.getManagedBean().getScLoa().getScLoiLoa()
								.getDocType())) {
					try {
						loadScLoaReport(scLoaReport);
					} catch (Exception e) {
						//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
						log.error(e);
						//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
					}
				}
			}
			//#29006 - end - note 850,852,856		
		}
		// end - #29006 (fixing on comment 570)	
	}

	/**
	 * @param tpa
	 * @param tplSpecQuestion
	 * @param tpaClone
	 * @return populate clone of TplSpecAnswer for generating updated Lampiran
	 * A3 without experiencing persistence exception during saving of
	 * Supplier Proposal Update screen.
	 */
	private TplSpecAnswer populateTplSpecAnswerClone(TplSpecAnswer tpa, TplSpecQuestion tplSpecQuestion,
			TplSpecAnswer tpaClone) {
		// populate clUom
		PmUom clUomClone = new PmUom();
		clUomClone.setDecimalScale(tplSpecQuestion.getClUom().getDecimalScale());
		clUomClone.setRecordStatusFlg(tplSpecQuestion.getClUom().getRecordStatusFlg());
		clUomClone.setUomCode(tplSpecQuestion.getClUom().getUomCode());
		clUomClone.setUomName(tplSpecQuestion.getClUom().getUomName());

		// populate tpq
		TplSpecQuestion tpqClone = new TplSpecQuestion();
		tpqClone.setClAnswerRequired(tplSpecQuestion.isClAnswerRequired());
		tpqClone.setClDeliveryTerm(tplSpecQuestion.getClDeliveryTerm());
		tpqClone.setClDurationMonth(tplSpecQuestion.getClDurationMonth());
		tpqClone.setClItem(tplSpecQuestion.getClItem());
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterCode(tplSpecQuestion.getClPriceTypeCode().getParameterCode());
		reqDTO.setParameterType(tplSpecQuestion.getClPriceTypeCode().getParameterType());
		ParameterResDTO resDTO = getParameterSOGlobal().retrieveParameterByTypeAndCode(reqDTO);
		if ((resDTO != null) && (resDTO.getPmParameter() != null)) {
			tpqClone.setClPriceTypeCode(resDTO.getPmParameter());
		}
		if (tplSpecQuestion.getClFulfilmentTypeCode() != null) {
			reqDTO.setParameterCode(tplSpecQuestion.getClFulfilmentTypeCode().getParameterCode());
			reqDTO.setParameterType(tplSpecQuestion.getClFulfilmentTypeCode().getParameterType());
			resDTO = getParameterSOGlobal().retrieveParameterByTypeAndCode(reqDTO);
			if ((resDTO != null) && (resDTO.getPmParameter() != null)) {
				tpqClone.setClFulfilmentTypeCode(resDTO.getPmParameter());
			}
		}
		tpqClone.setClQuantity(tplSpecQuestion.getClQuantity());
		tpqClone.setClReviewRemark(tplSpecQuestion.getClReviewRemark());
		tpqClone.setClSpecDetail(tplSpecQuestion.getClSpecDetail());
		tpqClone.setClUom(clUomClone);
		tpqClone.setClUomFrequency(tplSpecQuestion.getClUomFrequency());
		tpqClone.setClUomPerDay(tplSpecQuestion.getClUomPerDay());
		tpqClone.setClUomPerMonth(tplSpecQuestion.getClUomPerMonth());
		tpqClone.setClUomPerYear(tplSpecQuestion.getClUomPerYear());
		tpqClone.setQuestionOrder(tplSpecQuestion.getQuestionOrder());
		tpqClone.setQuestionType(tplSpecQuestion.getQuestionType());
		tpqClone.setRecordStatusFlg(tplSpecQuestion.getRecordStatusFlg());

		// POPULATE tpa clone
		tpaClone.setAllowAnotherPindaan(tpa.isAllowAnotherPindaan());
		tpaClone.setAmountPerYear(tpa.getAmountPerYear());
		tpaClone.setAnswerNumeric(tpa.getAnswerNumeric());
		// 29006 -start
		if(tpa.getIsWip() == null){
			tpa.setIsWip(false);
		}
		
		if(tpa.getIsWip()){
			Long currentSupplier = getManagedBean().getScLoa().getScLoiLoa().getSmSupplier().getSupplierId();
			
			if(tpa.getTplSpecSuppAnswer().getSmSupplier().getSupplierId().equals(currentSupplier)){
				//29006 @zaim start cater non zonal for wip
				if ( tpa.getTplSpecSuppAnswer().getTplSpecZoneDtl() != null){
					tpaClone.setAnswerRemark("Zone " + tpa.getTplSpecSuppAnswer().getTplSpecZoneDtl().getZoneName() + " : " + tpa.getAnswerRemark());
				}else {
					tpaClone.setAnswerRemark(tpa.getAnswerRemark());
				}
				//29006 @zaim end
			}
		}else
			tpaClone.setAnswerRemark(tpa.getAnswerRemark());
		// 29006 -end
		tpaClone.setDisableEditAccount(tpa.isDisableEditAccount());
		tpaClone.setDisableForPindaan(tpa.isDisableForPindaan());
		tpaClone.setDisableReviseItem(tpa.isDisableReviseItem());
		tpaClone.setDisableRow(tpa.isDisableRow());
		tpaClone.setEnableRevise(tpa.isEnableRevise());
		tpaClone.setFirstTierAnswer(tpa.isFirstTierAnswer());
		tpaClone.setJustificationEnabled(tpa.isJustificationEnabled());
		tpaClone.setNewSupplierOffer(tpa.getNewSupplierOffer());
		tpaClone.setRatePerUom(tpa.getRatePerUom());
		tpaClone.setRating(tpa.getRating());
		tpaClone.setReasonForUpdateEnabled(tpa.isReasonForUpdateEnabled());
		tpaClone.setRecordStatus(tpa.isRecordStatus());
		tpaClone.setRecordStatusFlg(tpa.getRecordStatusFlg());
		tpaClone.setStatusEnabled(tpa.isStatusEnabled());
		tpaClone.setTplSpecQuestion(tpqClone);
		// 29006 @zaim start
		tpaClone.setTplSpecSuppAnswer(tpa.getTplSpecSuppAnswer());
		// 29006 @zaim end

		return tpaClone;
	}

	public void onClickSupplierProposalUpdateCompleteButton() {
		onSaveSupplierOffer("COMPLETE");
	}

	public String populateLabelLampiranA3(String dateOfUpdate, String deskOfficerName) {
		String labelLampiranA3 = StringUtils.EMPTY;
		FacesScopes facesScopes = new FacesScopes(); //#29006 - note 587
		labelLampiranA3 = ResourceBundleUtils.getMessage("lbl.sc.doc.name.lampiran.a3", new String[] {
				dateOfUpdate, deskOfficerName }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				facesScopes.getUserLocale()); //#29006 - note 587
		return labelLampiranA3;
	}

	public void createLampiranA3(ScLoiLoaItem scLoiLoaItem) {
		removeUnwantedLampiranA3Report(2);
		FacesScopes facesScopes = new FacesScopes();
		// start - #29006 (fixing on comment 570)
		ScLoaReport lampiranA3 = getQtAwardSO().createLampiranA3(
				getManagedBean().getScLoa(),
				populateLabelLampiranA3(
						" "
								+ new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT).format(DateUtils
										.getCurrentDate())
								+ ",  "
								+ new SimpleDateFormat(CommonConstants.DEFAULT_TIME_FORMAT).format(DateUtils
										.getCurrentDate()) + ",",
						SmCommonUtil.getUserByUserId(facesScopes.getUserSessionBean().getUserId()).getUserName()));
		// end - #29006 (fixing on comment 570)
		try {
			StringBuilder filename = new StringBuilder();
			filename.append(lampiranA3.getTitle());
			//#31869 - start
			//lampiranA3.setScLoaReportAttachment(new ScLoaReportAttachment());
			Date today = new Date(); 
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(today);
			int year = cal.get(Calendar.YEAR); 		
			
			ScLoaReportAttachment scLoaReportAttachment = new ScLoaReportAttachment();
			AttachmentFormat scFormat = new AttachmentDateFormat();
			String formatedFileName = scFormat.format(filename.toString().replaceAll("/",""), null);
			scLoaReportAttachment.setFilePath(Integer.toString(year)+"/SC/"+lampiranA3.getScLoa().getLoaNo()+"/LAR/"+formatedFileName);
			
			lampiranA3.setScLoaReportAttachment(scLoaReportAttachment);
			//#31869 - end
			lampiranA3.getScLoaReportAttachment().setScLoaReport(lampiranA3);
			lampiranA3.getScLoaReportAttachment().setDocNo(lampiranA3.getScLoa().getLoaNo());
			filename.append(".pdf");
			lampiranA3.getScLoaReportAttachment().setFileName(filename.toString().replaceAll("/",""));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, e.getMessage()));
		}
		
		getManagedBean().getScLoa().getScLoaReports().add(lampiranA3);
		//#31869 - start
		AwardSupplierReqDTO finalAwardReqDTO = new AwardSupplierReqDTO();
		
		if(null!=this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()){
			finalAwardReqDTO.setScLoa(getManagedBean().getScLoa());
		
			AwardSupplierResDTO resDTO  = getQtAwardSO().saveLampiranA3Report(finalAwardReqDTO);
			List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
			ScLoa scLoa = getManagedBean().getScLoa();
			scLoa.setScLoaReports(scLoaReports);
			getManagedBean().setScLoa(scLoa);
		}
		//#31869 - end
	}

	private void generateUpdatedLampiranA3FromVelocity(ScAttachment attachment, ScLoa scLoa, ScQt scQt)
			throws Exception {
		populateAttachmentDetails(attachment);
		// MODIFY ATTACHEMENT FILE PATH HERE INSTEAD OF BACKING BEAN
		AttachmentFormat scFormat = new AttachmentDateFormat();
		String formatedFileName = scFormat.format(attachment.getFileName(), attachment.getPrefixFileName());
		attachment.setFilePath(attachment.getFilePath().replace(formatedFileName, attachment.getFileName()));
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		VelocityContext context = new VelocityContext();
		// #29006 start
		String defaultValue = SSTConstant.TB;
		/*if ("en".equals(new FacesScopes().getUserLocale().getLanguage())) {
			defaultValue = "N/A";
		}*/
		// #29006 end
		context.put("defaultValue", defaultValue);
//		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO) BusinessDelegate.getInstance().getServiceObject(
//				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);

		boolean isZonal = scLoa.getScLoiLoa().isZonal();
		context.put("isZonal", isZonal);

		String fileNo = StringUtils.EMPTY;
		if (!StringUtils.isBlank(scLoiLoa.getFileNo())) {
			fileNo = scLoiLoa.getFileNo();
			if (fileNo.length() > 30) {
				fileNo = splitLongLengthString(fileNo, 29);
			}
		}
		context.put("loaNo", scLoa.getLoaNo());

		// loa title
		String title = scLoiLoa.getTitle();
		if (title == null) {
			title = StringUtils.EMPTY;
		}
		title = title.trim();

		String qtNo = MALAY_NA;
		if (scQt != null) {
			qtNo = scQt.getQtNo();
		}
		// qtno
		context.put("qtNo", qtNo);

		// JadualHarga
		formJadualHarga(context, scLoa);

		// Specification
		formSpecification(context, scLoa);

		// Justificatio date stamp
		context.put("dateStamp", DateUtils.getLocalizeDate(DateUtils.getCurrentDate(), myLocale));
		context.put("timeStamp", new SimpleDateFormat(CommonConstants.DEFAULT_TIME_FORMAT).format(DateUtils
				.getCurrentDate()));//#29006 - note 633
		// scloiloa items
		List<ScLoiLoaItem> scLoiLoaItemCloneList = new ArrayList<>();
		Set<Long> requestNoteIdList = new HashSet<Long>();
		for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
			requestNoteIdList.add(scLoiLoaItem.getScRequestItem().getRequestNoteItemId());
		}
		
		if (requestNoteIdList.size() > 1) {
			// SINGLE LOA, MULTIPLE ITEMS
			scLoiLoaItemCloneList.addAll(populateMultipleScLoiLoaItemClone(scLoa));
		} else {
			// SINGLE LOA, SINGLE ITEM
			scLoiLoaItemCloneList.add(populateSingleScLoiLoaItemClone(scLoa));
		}
		
		Long currentSupplier = getManagedBean().getScLoa().getScLoiLoa().getSmSupplier().getSupplierId();		
		for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItemCloneList) {
			ITemplateSpecSO templateSpecSO = (ITemplateSpecSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
			List<TplSpecAnswer> tplSpecAnswerList = templateSpecSO.findSpecAnswersByQuestion(scLoiLoaItem
					.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());	
			//29006 @zaim start
			getManagedBean().setTplSpecAnswerList(tplSpecAnswerList);
			//29006 @zaim end
			boolean shouldBreak = false;
			//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
			for (TplSpecAnswer tplSpecAnswer : tplSpecAnswerList) {
			    if (tplSpecAnswer.getIsWip() == null) {
			        tplSpecAnswer.setIsWip(false);
			    }

			    if (tplSpecAnswer.getIsWip()) {
			        shouldBreak = true;
			    } else {
			        scLoiLoaItem.getSupplierOfferDetailList().clear();

			        if (StringUtils.isNotBlank(tplSpecAnswer.getAnswerRemark())) {
			            if (tplSpecAnswer.getTplSpecSuppAnswer().getSmSupplier().getSupplierId().equals(currentSupplier)) {
			                scLoiLoaItem.getSupplierOfferDetailList().add(tplSpecAnswer.getAnswerRemark().replaceAll("<", "&#60;")); //#29006 note 819
			                shouldBreak = true;
			            }
			        }
			    }

			    if (shouldBreak) {
			        break;
			    }
			}
			//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]				
		}
				
		for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItemCloneList) {
			for (Map.Entry<Long, List<TplSpecAnswer>> entry : this.getManagedBean().getScLoiLoaItemAnswerMap()
					.entrySet()) {
				if (entry.getKey().compareTo(scLoiLoaItem.getLoiLoaItemId()) == 0) {
					// populate Jenis Pemenuhan and Tempoh (Bulan) columns
					context.put("jenisPemenuhan", scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClFulfilmentTypeCode() != null ? getParameterDescForVelocityReport(scLoiLoaItem
							.getScRequestItem().getTplSpecQuestion().getClFulfilmentTypeCode()) : StringUtils.EMPTY);
					context.put(
							"durationMonth",
							scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getClDurationMonth() != null ? scLoiLoaItem
									.getScRequestItem().getTplSpecQuestion().getClDurationMonth().toString()
									: StringUtils.EMPTY);
					context.put("bilUnitUkuranSehari", scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClUomPerDay() != null ? scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClUomPerDay().toString() : defaultValue);
					context.put("bilUnitUkuranSebulan", scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClUomPerMonth() != null ? scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getClUomPerMonth().toString() : defaultValue);
				}
			}
		}
		//#29006 - start -  note 607
		for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItemCloneList) {
			List<ScLoiLoaItem> items = getQtAwardSO().getLoiLoaItemByLoaIdAndReqItemId(
					scLoiLoaItem.getScLoa().getLoaId(), scLoiLoaItem.getScRequestItem().getRequestNoteItemId());
			
			//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
			if(!items.isEmpty())
			//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
				scLoiLoaItem.setComplete(items.get(0).isComplete());
			else
				scLoiLoaItem.setComplete(false);
			
			// start - #34898/#29006 - start - note 850,852,856
			if (scLoiLoaItem.getLoaSpecJustification() != null) {
				scLoiLoaItem.setLoaSpecJustification(VmUtil.getInstance().escapeAndWrapText(scLoiLoaItem.getLoaSpecJustification(), 90, SSTConstant.FILE_TYPE_PDF)); ////35295 #124 @zaim
			}

			for (TplSpecAnswer tplSpecAnswer : scLoiLoaItem.getTplSpecAnswerList()) {
				if (tplSpecAnswer.getNewSupplierOffer() != null) {
					String newSupplierOffer = tplSpecAnswer.getNewSupplierOffer();
					newSupplierOffer = newSupplierOffer.replaceAll("<", "&#60;");
					newSupplierOffer = newSupplierOffer.replaceAll(">", "&#62;");
					newSupplierOffer = newSupplierOffer.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER, TemplateSpecConst.HTML_NEW_LINE_CHARACTER); //36122 @zaim
					
					tplSpecAnswer.setNewSupplierOffer(newSupplierOffer);
				}
			}
			// end - #34898/#29006 - start - note 850,852,856
			
			//#29006 - note 702,850,852,856 - start
			//note 870,872 - start
			for (ScLoiLoaItem item : items) {
				scLoiLoaItem.setDateGeneratedA3(null);				
				if(item.getLoiLoaItemId().equals(scLoiLoaItem.getLoiLoaItemId())
						&& ((item.getScLoa().getLoaId().equals(scLoiLoaItem.getScLoa().getLoaId())) && (item
								.getScRequestItem().getRequestNoteItemId().equals(scLoiLoaItem.getScRequestItem()
								.getRequestNoteItemId())))) {
					scLoiLoaItem.setDateGeneratedA3(DateUtils.getLocalizeDate(item.getChangedDate(), myLocale));
					scLoiLoaItem.setTimeGeneratedA3(new SimpleDateFormat(CommonConstants.DEFAULT_TIME_FORMAT).format(item.getChangedDate()));
					break;
				}					
			}
			
			if(scLoiLoaItem.getDateGeneratedA3() == null)
			{
				for (ScLoiLoaItem item : items) {
					if(item.getLoiLoaItemId().equals(scLoiLoaItem.getLoiLoaItemId())
							|| ((item.getScLoa().getLoaId().equals(scLoiLoaItem.getScLoa().getLoaId())) && (item
									.getScRequestItem().getRequestNoteItemId().equals(scLoiLoaItem.getScRequestItem()
									.getRequestNoteItemId())))) {
						scLoiLoaItem.setDateGeneratedA3(DateUtils.getLocalizeDate(item.getChangedDate(), myLocale));
						scLoiLoaItem.setTimeGeneratedA3(new SimpleDateFormat(CommonConstants.DEFAULT_TIME_FORMAT).format(item.getChangedDate()));
						break;
					}
				}
			}
			//36122 - start @zaim
			if (scLoiLoaItem.getScRequestItem() != null && scLoiLoaItem.getScRequestItem().getItemName() != null){
				String itemName = VmUtil.getInstance().escapeAndWrapText(scLoiLoaItem.getScRequestItem().getItemName(), 90,SSTConstant.FILE_TYPE_PDF); //35295 - #124 @zaim
				scLoiLoaItem.getScRequestItem().setItemName(itemName);
			}
			//36122 - end
			//note 870,872 - end
			//#29006 - note 702,850,852,856 - end
			//29006 start @zaim
			ConstructTplSpecAnswerForLampiranA3(scLoiLoaItem,scLoa,currentSupplier);
			//29006 end @zaim
		}
		//#29006 - end -  note 607
		context.put("itemsList", scLoiLoaItemCloneList);
		
		if (scQt != null
				&& (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType()) || ParameterConst.DOCTYPE_DIRECT_NEGO
						.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode())
						|| ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scQt.getFulfilmentType().getParameterCode()) || ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
							.equals(scQt.getFulfilmentType().getParameterCode()))) {
			constructSupplierSpecificationAndRemarks(context, scLoa);
		}

		if ((ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode()
						.getParameterCode()) || ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scLoa.getScLoiLoa()
						.getFulfilmentTypeCode().getParameterCode()))) {

			constructPRSupplierSpecificationAndRemarks(context, scLoa);
		}

		context.put("scLoiLoaItemList", scLoa.getScLoiLoaItems());
		// scLoa
		context.put("scLoa", scLoa);
		context.put("bean", this);
		
		FileOutputStream fos = null;

		try {
		
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_LAMPIRAN_A3_UPDATED, context, 
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

	private void removeUnwantedLampiranA3Report(int mode) {
		if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
			List<ScLoaReport> oldLampiranA3List = new ArrayList<>();
			for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
				if (StringUtils.isNotBlank(report.getTitle())) {
					if ((mode == 1) && report.getTitle().contains(lampiranA3SubStr)
							&& report.getTitle().contains("<date update> <Desk Officer Name>")) {
						report.setDeleted(true); //#31869
						oldLampiranA3List.add(report);
					} else if ((mode == 2) && report.getTitle().contains(lampiranA3SubStr)) {
						report.setDeleted(true); //#31869
						oldLampiranA3List.add(report);
					}
				}
			}
			getManagedBean().getScLoa().getScLoaReports().removeAll(oldLampiranA3List);
			//#31869 - start
			AwardSupplierReqDTO awardReqDTO = new AwardSupplierReqDTO();
			if(null!=this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()){
				ScLoa scLoa = new ScLoa();
				scLoa.setScLoaReports(oldLampiranA3List);
				awardReqDTO.setScLoa(scLoa);
				getQtAwardSO().deleteLampiranA3Report(awardReqDTO);
			}
			//#31869 - end
		}
	}
	
	private ScLoiLoaItem populateSingleScLoiLoaItemClone(ScLoa scLoa) {
		boolean isZonal = scLoa.getScLoiLoa().isZonal();
		// Initialize ine ScLoiLoaItem which will store all details to be shown
		// in one table
		ScLoiLoaItem scLoiLoaItemClone = createAndPopulateScLoiLoaItemClone(scLoa.getScLoiLoaItems().get(0));

		// Get new supplier offer
		if (StringUtils.isNotBlank(scLoiLoaItemClone.getTplSpecAnswerList().get(0).getNewSupplierOffer())) {
			scLoiLoaItemClone.getSupplierOfferDetailList().add(
					scLoiLoaItemClone.getTplSpecAnswerList().get(0).getNewSupplierOffer());
		}

		if (CollectionUtils.isNotEmpty(scLoiLoaItemClone.getTplSpecAnswerList())) {
			if (isZonal) {
				// Remove old supplier offer which in in first index so that the
				// new supplier offer is shown as new supplier offer and old
				// supplier offer of the zone are not shown together
				if (scLoiLoaItemClone.getSupplierOfferDetailList().size() > 2) {
					scLoiLoaItemClone.getSupplierOfferDetailList().remove(1);
				}
			}
		}

		// Remove all TplSpecAnswer except first one to resolve issue
		// where Updated Lampiran A3 shows one table for one TplSpecAnswer
		TplSpecAnswer tsa = scLoiLoaItemClone.getTplSpecAnswerList().get(0);
		scLoiLoaItemClone.getTplSpecAnswerList().clear();
		scLoiLoaItemClone.getTplSpecAnswerList().add(tsa);
		List<String> zoneNameList = new ArrayList<>();

		// Sort scLoa.getScLoiLoaItems() in item ascending order
		// so that the items/details are shown in ascending order
		Collections.sort(scLoa.getScLoiLoaItems(), new Comparator<ScLoiLoaItem>() {
			@Override
			public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
				return Long.compare(s1.getScRequestItem().getRequestNoteItemId(), s2.getScRequestItem()
						.getRequestNoteItemId());
			}
		});

		for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
			if (StringUtils.isNotBlank(scLoiLoaItem.getLoaSpecJustification())) {
				scLoiLoaItemClone.setLoaSpecJustification(scLoiLoaItem.getLoaSpecJustification());
			}
			if (isZonal) {
				if (scLoiLoaItem.getTplSpecZoneDtl() != null
						&& StringUtils.isNotBlank(scLoiLoaItem.getTplSpecZoneDtl().getZoneName())) {
					zoneNameList.add(scLoiLoaItem.getTplSpecZoneDtl().getZoneName());
				}
			}

		}

		if (isZonal && CollectionUtils.isNotEmpty(zoneNameList)) {
			scLoiLoaItemClone.setZoneNameList(zoneNameList);
		}
		if (CollectionUtils.isEmpty(scLoiLoaItemClone.getSupplierOfferDetailList())) {
			scLoiLoaItemClone.getSupplierOfferDetailList().add("T/B");
		}

		return scLoiLoaItemClone;
	}

	private List<ScLoiLoaItem> populateMultipleScLoiLoaItemClone(ScLoa scLoa) {
		List<ScLoiLoaItem> scLoiLoaItemCloneList = new ArrayList<ScLoiLoaItem>();
		Map<Long, ScLoiLoaItem> scLoiLoaItemCloneMap = new HashMap<Long, ScLoiLoaItem>();
		boolean isZonal = scLoa.getScLoiLoa().isZonal();

		for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
			ScLoiLoaItem scLoiLoaItemClone = null;
			String newSupplierOffer = StringUtils.EMPTY;
			String oldSupplierOffer = StringUtils.EMPTY;

			if (MapUtils.isEmpty(scLoiLoaItemCloneMap)
					|| scLoiLoaItemCloneMap.get(scLoiLoaItem.getScRequestItem().getRequestNoteItemId()) == null) {
				scLoiLoaItemClone = createAndPopulateScLoiLoaItemClone(scLoiLoaItem);
				if (isZonal) {
					List<String> oldSupplierOfferList = retrieveOldSupplierOfferList(scLoiLoaItemClone
							.getTplSpecAnswerList());
					if (CollectionUtils.isNotEmpty(oldSupplierOfferList)) {
						scLoiLoaItemClone.setSupplierOfferDetailList(oldSupplierOfferList);
					}
				}
				TplSpecAnswer tsa = scLoiLoaItemClone.getTplSpecAnswerList().get(0);
				scLoiLoaItemClone.getTplSpecAnswerList().clear();
				scLoiLoaItemClone.getTplSpecAnswerList().add(tsa);
				List<String> zoneNameList = new ArrayList<>();
				if (StringUtils.isNotBlank(scLoiLoaItem.getLoaSpecJustification())) {
					scLoiLoaItemClone.setLoaSpecJustification(scLoiLoaItem.getLoaSpecJustification());
				}
				if (isZonal && scLoiLoaItem.getTplSpecZoneDtl() != null
						&& StringUtils.isNotBlank(scLoiLoaItem.getTplSpecZoneDtl().getZoneName())) {
					zoneNameList.add(scLoiLoaItem.getTplSpecZoneDtl().getZoneName());
				}
				if (isZonal && CollectionUtils.isNotEmpty(zoneNameList)) {
					scLoiLoaItemClone.setZoneNameList(zoneNameList);
				}

				// Add into scLoiLoaItemCloneMap
				scLoiLoaItemCloneMap.put(scLoiLoaItem.getScRequestItem().getRequestNoteItemId(), scLoiLoaItemClone);
			} else {
				if (scLoiLoaItemCloneMap.get(scLoiLoaItem.getScRequestItem().getRequestNoteItemId()) != null) {
					scLoiLoaItemClone = scLoiLoaItemCloneMap
							.get(scLoiLoaItem.getScRequestItem().getRequestNoteItemId());

					// Populate justifikasi of item
					if (StringUtils.isNotBlank(scLoiLoaItem.getLoaSpecJustification())
							&& StringUtils.isBlank(scLoiLoaItemClone.getLoaSpecJustification())) {
						scLoiLoaItemClone.setLoaSpecJustification(scLoiLoaItem.getLoaSpecJustification());
					}

					// Loop to populate newSupplierOffer and oldSupplierOffer
					if (CollectionUtils.isNotEmpty(scLoiLoaItem.getTplSpecAnswerList())) {
						for (TplSpecAnswer tpa : scLoiLoaItem.getTplSpecAnswerList()) {
							if (StringUtils.isNotBlank(tpa.getNewSupplierOffer())) {
								newSupplierOffer = tpa.getNewSupplierOffer();
							} else if (StringUtils.isNotBlank(tpa.getAnswerRemark())) {
								oldSupplierOffer = tpa.getAnswerRemark();
							}
						}
					}

					// Populate Tawaran Pembekal column
					if (StringUtils.isNotBlank(newSupplierOffer)) {
						scLoiLoaItemClone.getTplSpecAnswerList().get(0).setNewSupplierOffer(newSupplierOffer);
					} else if (StringUtils.isNotBlank(oldSupplierOffer)) {
						scLoiLoaItemClone.getTplSpecAnswerList().get(0).setAnswerRemark(oldSupplierOffer);
					}

					// Populate Zone details
					if (isZonal && (scLoiLoaItem.getTplSpecZoneDtl() != null)
							&& StringUtils.isNotBlank(scLoiLoaItem.getTplSpecZoneDtl().getZoneName())) {
						scLoiLoaItemClone.getZoneNameList().add(scLoiLoaItem.getTplSpecZoneDtl().getZoneName());
					}

					// Add into scLoiLoaItemCloneMap
					scLoiLoaItemCloneMap.put(scLoiLoaItem.getScRequestItem().getRequestNoteItemId(), scLoiLoaItemClone);
				}
			}
		}

		// Add each valid scLoiLoaItemClone into scLoiLoaItemCloneList
		for (Map.Entry<Long, ScLoiLoaItem> scLoiLoaItemClone : scLoiLoaItemCloneMap.entrySet()) {
			if (isZonal) {
				if (CollectionUtils.isNotEmpty(scLoiLoaItemClone.getValue().getSupplierOfferDetailList())) {
					if (StringUtils.isNotBlank(scLoiLoaItemClone.getValue().getTplSpecAnswerList().get(0)
							.getNewSupplierOffer())) {
						// remove old supplier offer list so that only new
						// supplier offer is displayed
						scLoiLoaItemClone.getValue().getSupplierOfferDetailList().clear();
					} else {
						// if there is no new supplier offer, update list of old
						// supplier offer by removing null/empty ones and then
						// assign to scLoiLoaItemClone
						List<String> updatedOldSupplierOfferList = new ArrayList<>();
						for (String oldSupplierOffer : scLoiLoaItemClone.getValue().getSupplierOfferDetailList()) {
							if (oldSupplierOffer != null && StringUtils.equals(oldSupplierOffer, "")) {
								updatedOldSupplierOfferList.add(oldSupplierOffer);
							}
						}
						if (CollectionUtils.isNotEmpty(updatedOldSupplierOfferList)) {
							scLoiLoaItemClone.getValue().setSupplierOfferDetailList(updatedOldSupplierOfferList);
						}
					}
				}
			}
			scLoiLoaItemCloneList.add(scLoiLoaItemClone.getValue());
		}

		// Sort scLoiLoaItemCloneList by
		// scLoiLoaItem.getScRequestItem().getRequestNoteItemId()
		// in ascending order
		Collections.sort(scLoiLoaItemCloneList, new Comparator<ScLoiLoaItem>() {
			@Override
			public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
				return Long.compare(s1.getScRequestItem().getRequestNoteItemId(), s2.getScRequestItem()
						.getRequestNoteItemId());
			}
		});

		return scLoiLoaItemCloneList;
	}

	private List<String> retrieveOldSupplierOfferList(List<TplSpecAnswer> tplSpecAnswerList) {
		List<String> oldSupplierOfferList = new ArrayList<String>();
		for (TplSpecAnswer tpa : tplSpecAnswerList) {
			if (StringUtils.isNotBlank(tpa.getAnswerRemark())) {
				oldSupplierOfferList.add(tpa.getAnswerRemark());
			}
		}
		return oldSupplierOfferList;
	}

	private ScLoiLoaItem createAndPopulateScLoiLoaItemClone(ScLoiLoaItem scLoiLoaItem) {
		// This method creates ScLoiLoaItem for each item in the LOA for Updated
		// Lampiran A3 display
		
		ScLoiLoaItem itemClone = (ScLoiLoaItem) scLoiLoaItem.cloneEntity();
		List<TplSpecAnswer> tpaCloneList = new ArrayList<>();
		
		for (Map.Entry<Long, List<TplSpecAnswer>> entry : getManagedBean().getScLoiLoaItemAnswerMap().entrySet()) {
			if (entry.getKey().compareTo(scLoiLoaItem.getLoiLoaItemId()) == 0) {
				List<String> nsoJusList = getManagedBean().getScRequestItemIdNewSuppOfferAndJustMap().get(
						scLoiLoaItem.getScRequestItem().getRequestNoteItemId());
				for (TplSpecAnswer tpa : entry.getValue()) {
					TplSpecAnswer tpac = (TplSpecAnswer) tpa.cloneEntity();
					if (CollectionUtils.isNotEmpty(nsoJusList)) {
						tpac.setNewSupplierOffer(nsoJusList.get(0).replace("NSO_", StringUtils.EMPTY));
					} else {
						if (StringUtils.isNotBlank(tpa.getNewSupplierOffer())) {
							tpac.setNewSupplierOffer(tpa.getNewSupplierOffer());
						}
					}
					tpaCloneList.add(tpac);
				}
			}
		}
		
		itemClone.setScRequestItem((ScRequestItem) scLoiLoaItem.getScRequestItem().cloneEntity());
		itemClone.getScRequestItem().setTplSpecQuestion(
				(TplSpecQuestion) scLoiLoaItem.getScRequestItem().getTplSpecQuestion().cloneEntity());
		itemClone.setTplSpecAnswerList(tpaCloneList);
		itemClone.setLoaSpecJustification(scLoiLoaItem.getLoaSpecJustification());
		List<TplSpecQuestion> tpqSpecList = getTemplateSpecSO().findQuestionsBySpecMasterIdAndParentQuestionId(
				scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getTplSpecMaster().getSpecMasterId(), "S",
				scLoiLoaItem.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());
		
		if (CollectionUtils.isNotEmpty(tpqSpecList)) {
			itemClone.setItemSpecificationList(new ArrayList<String>());
			for (TplSpecQuestion tpqSpec : tpqSpecList) {
				if (StringUtils.isNotBlank(tpqSpec.getClSpecDetail())) {
					//36122 - start @zaim
					itemClone.getItemSpecificationList().add(VmUtil.getInstance().escapeAndWrapText(tpqSpec.getClSpecDetail(), 70,SSTConstant.FILE_TYPE_PDF)); //35295 - #124 @zaim
					//36122 - @zaim
				}
			}
		}
		if (CollectionUtils.isEmpty(itemClone.getZoneNameList())) {
			itemClone.setZoneNameList(new ArrayList<String>());
		}
		if (itemClone.getTplSpecZoneDtl() != null
				&& StringUtils.isNotBlank(itemClone.getTplSpecZoneDtl().getZoneName())) {
			itemClone.getZoneNameList().add(itemClone.getTplSpecZoneDtl().getZoneName());
		}
		if (CollectionUtils.isEmpty(itemClone.getSupplierOfferDetailList())) {
			itemClone.setSupplierOfferDetailList(new ArrayList<String>());
		}

		return itemClone;
	}
	
	// #29006 - note 837 - start
	/**
	 * Load ScLoaReport data.
	 * 
	 * @param scLoaReport
	 *            the sc loa report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void loadScLoaReport(ScLoaReport scLoaReport) throws IOException {
		
		try {
			if (scLoaReport.getScLoaReportAttachment().getDocNo() == null) {
				scLoaReport.getScLoaReportAttachment().setDocNo(getManagedBean().getScLoa().getLoaNo());
			}

			generateUpdatedLampiranA3FromVelocity((ScAttachment) scLoaReport.getScLoaReportAttachment(),
					getManagedBean().getScLoa(), this.getManagedBean().getScQt());
		} catch (Exception e) {
			//start - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
			log.error(e);
			//stop - Sonarqube DP Vulnerability Minor Zairul Rizwan [8/8/2023]
		}
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport.getScLoaReportAttachment();

		ScLoaReportAttachment scLoaReportAttachmentGenerated = generateDocumentAttachment(scLoaReport);
		if (scLoaReport.getDefinationType().getParameterCode()
				.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD)
				|| scLoaReport.getDefinationType().getParameterCode()
						.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD)) {
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
		}
		if (scLoaReportAttachmentGenerated != null) {
			String prefixName = scLoaReport.getTitle().contains(lampiranA3SubStr) ? scLoaReport.getTitle().replaceAll(
					"/", "-") : scLoaReport.getTitle().replaceAll(SourcingConstants.REG_EXP_WHITE_SPACE, "");
			scLoaReportAttachmentGenerated.setTempFileName(setValueLowercase(prefixName, 18));
		}
	}
	// #29006 - note 837 - end
	// #35828 - fixing 29006 - start
	/**
	 * Checks if the new offer is complete for the given LOI/LOA item. It
	 * verifies that each template specification answer associated with the
	 * specified question has a non-blank and non-null new supplier offer.
	 * 
	 * @param loiLoaItem
	 *            The LOI/LOA item to check.
	 * @return true if all new supplier offers are complete; false otherwise.
	 */
	public boolean isNewOfferComplete(ScLoiLoaItem loiLoaItem) {
		List<TplSpecAnswer> tpaList = getTemplateSpecSO().findSpecAnswersByQuestion(
				loiLoaItem.getScRequestItem().getTplSpecQuestion().getSpecQuestionId());
		//Maximum result 21 rows
		for (TplSpecAnswer tplSpecAnswer : tpaList) {
			if (StringUtils.isBlank(tplSpecAnswer.getNewSupplierOffer()) || tplSpecAnswer.getNewSupplierOffer() == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if items in the list are complete.
	 * 
	 * @param items
	 *            The list of items to check
	 * @return true if all items are complete, false otherwise
	 */
	public boolean isJustificationComplete(List<ScLoiLoaItem> items) {
		if (CollectionUtils.isNotEmpty(items)) {
			//loop only 1 row
			for (ScLoiLoaItem scloiLoaItem : items) {
				if (scloiLoaItem.isComplete())
					return true;
				break;
			}
		}
		return false;
	}

	// #35828 - fixing 29006 - end
	// #29006 - note 746 - start
	/**
	 * To calculate total zonal price based on item search result
	 * 
	 * @return BigDecimal - Total Price of ScLoiLoaItem list
	 */
	public BigDecimal calculateTotalPriceZonal() {
		BigDecimal totalPrice = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getNameZoneScLoiLoaItemList())) {
			Long currentRequestItem = 0L;
			
			for (ScLoiLoaItem item : this.getManagedBean().getNameZoneScLoiLoaItemList()) {				
				if (!currentRequestItem.equals(item.getScRequestItem().getRequestNoteItemId())){
					totalPrice = getScRequestSubTotalZonal(item.getScRequestItem().getRequestNoteItemId()).add(totalPrice);
					currentRequestItem = item.getScRequestItem().getRequestNoteItemId();
				}
			}
		}
		//[Start] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
		totalPrice = totalPrice.setScale(2, RoundingMode.UP);
		//[End] - Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
		return totalPrice;
	}
	// #29006 - note 746 - end

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
	/**
	 * Search populated ScLoiLoaItems by ScRequestItem.itemName and build filtered results
	 * @return 
	 */
	public void searchSupplierItemByName(){ 
		String itemNameInput = this.getItemNameSearchCriteria()==null? "": this.getItemNameSearchCriteria().trim();
		if(StringUtils.isEmpty(itemNameInput)){
			this.getManagedBean().setLoiLoaItemsResult(new ArrayList<ScLoiLoaItem>(this.getManagedBean().getScLoa().getScLoiLoaItems()));
			return;
		} 
		this.getManagedBean().setLoiLoaItemsResult(new ArrayList<ScLoiLoaItem>());
		if(CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoaItems())){
			for(ScLoiLoaItem item: this.getManagedBean().getScLoa().getScLoiLoaItems()){
				String itemName = item.getScRequestItem()!=null ? item.getScRequestItem().getItemName().trim():"";
				if(item.getScRequestItem()!=null && StringUtils.containsIgnoreCase(itemName, itemNameInput)){
					this.getManagedBean().getLoiLoaItemsResult().add(item);
				}
			}
		} 
	}
	/**
	 * Search supplier specification based on item details
	 * @param loiLoaItemMain
	 * @param mainSearch
	 * @return 
	 */
	public void searchSupplierSpecification(ScLoiLoaItem loiLoaItemMain,boolean mainSearch){
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
			TemplateSpecReqDTO specReqDto=new TemplateSpecReqDTO();
			AwardSupplierReqDTO reqDTO=new AwardSupplierReqDTO();
			reqDTO.setScLoiLoaItem(loiLoaItemMain);
			AwardSupplierResDTO resDTO  = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTO);
			ScLoiLoaItem loiLoaItem=resDTO.getScLoiLoaItem();
			this.getManagedBean().setItemNamePopup(loiLoaItem.getScRequestItem().getItemName());
			this.getManagedBean().setScLoiLoaItem(loiLoaItem);
			this.getManagedBean().setNewSupplierJustification(loiLoaItem.getLoaSpecJustification());
			if(this.getManagedBean().getScQt().isZonal() && mainSearch){//29007-start
				log.info("===========zonalcodesexecuted=====");
				List<ScLoiLoaItem> zonalItemList=new ArrayList<>();
				for(ScLoiLoaItem loacalloiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
					if(loiLoaItem.getLoiLoaItemId()== loacalloiLoaItem.getLoiLoaItemId() || loiLoaItem.getLoiLoaItemId().equals(loacalloiLoaItem.getLoiLoaItemId()) || loiLoaItem.getScRequestItem().getRequestNoteItemId()==loacalloiLoaItem.getScRequestItem().getRequestNoteItemId() || loiLoaItem.getScRequestItem().getRequestNoteItemId().equals(loacalloiLoaItem.getScRequestItem().getRequestNoteItemId())){//29007-start
						zonalItemList.add(loacalloiLoaItem);
					}
				}
				if(zonalItemList.size()>1){					
					checkItemSorting(zonalItemList);				
					ScLoiLoaItem latestChangedItem=zonalItemList.get(zonalItemList.size()-1);
					this.getManagedBean().setNewSupplierJustification(latestChangedItem.getLoaSpecJustification());
				}
			}//29007-End
			
			specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
			specReqDto.setScQt(this.getManagedBean().getScQt());
			specReqDto.setScLoiLoaItem(loiLoaItem);
			
			if(this.getManagedBean().getSpecKeyword() != null){
				TplSpecQuestion tplSpecQuestion = new TplSpecQuestion();
				TplSpecAnswer tplSpecAnswer = new TplSpecAnswer();
				
				tplSpecQuestion.setClSpecDetail(this.getManagedBean().getSpecKeyword());
				tplSpecAnswer.setTplSpecQuestion(tplSpecQuestion);
				
				specReqDto.setTplSpecAnswer(tplSpecAnswer);
				
			}
			boolean qtMaint = isQTMaintenanceScreen();
			if(QTStatusConst.LOA_PENDING_VERIFICATION.equals(getManagedBean().getScLoa().getPmStatus().getStatusId())
				|| QTStatusConst.LOA_PENDING_SUBMISSION.equals(getManagedBean().getScLoa().getPmStatus().getStatusId())
				|| QTStatusConst.LOA_PENDING_REVISION.equals(getManagedBean().getScLoa().getPmStatus().getStatusId())
				&& !qtMaint) {
				getManagedBean().setSupPropSpecDisable(false);
			}else{
				getManagedBean().setSupPropSpecDisable(true);
			}
			
			specReqDto.setSpecSearchForApp(true);
			TemplateSpecResDTO templateSpecResDTO  = templateSpecSO.searchSupplierSpecification(specReqDto);
			
			for(TplSpecAnswer tplSpecAnswer: templateSpecResDTO.getSpecAnswerList()){
				if(tplSpecAnswer.getNewSupplierOffer() != null && tplSpecAnswer.getNewSupplierOffer() != ""){
					tplSpecAnswer.setRenderNewSpec(true);
				}
			}
			
			this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
			this.getManagedBean().setScLoiLoaItem(loiLoaItem);
			if(mainSearch){
				PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext()
												.getSession(false);
				portletSession.setAttribute(QTConstants.PORTLET_ATTRIBUTE_NEW_OFFERED_SPECIFICATION, loiLoaItem);
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.REDIRECT_URL_NEW_OFFERED_SPECIFICATION_LOA);
			}
	}
	/**
	 * save item details
	 * @param isComplete
	 * @return 
	 */
	public void saveSupplierSpecification(boolean isComplete){
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
		IQTAwardSO iQTAwardSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		if(this.getManagedBean().getTplSpecAnswerList() != null || this.getManagedBean().getNewSupplierJustification() != null){
			if(this.getManagedBean().getScQt().isZonal() && this.getManagedBean().isResetAll()){
				for(ScLoiLoaItem zoneLoiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
					if(null!=this.getManagedBean().getScLoiLoaItem()
						&& zoneLoiLoaItem.getLoiLoaItemId()==this.getManagedBean().getScLoiLoaItem().getLoiLoaItemId()){
						zoneLoiLoaItem.setLoaSpecJustification(null);
					}
					if(this.getManagedBean().getScLoa().isBrandNew()){
						zoneLoiLoaItem.setScLoa(null);
					}
				}
				reqDTO.setScLoiLoaItemList(this.getManagedBean().getScLoa().getScLoiLoaItems());
				AwardSupplierResDTO resDTOLatest  = getQtAwardSO().saveZoneItemListForResetAll(reqDTO);
				if(resDTOLatest.isSuccessful()){
					ScLoa scLoa= this.getManagedBean().getScLoa();
					scLoa.setScLoiLoaItems(resDTOLatest.getScLoiLoaItemList());
				}
				for(ScLoiLoaItem updatedLoiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
					if(updatedLoiLoaItem.getLoiLoaItemId()==this.getManagedBean().getScLoiLoaItem().getLoiLoaItemId()){
						this.getManagedBean().setScLoiLoaItem(updatedLoiLoaItem);
						break;
					}
				}
				reqDTO = new AwardSupplierReqDTO();
			}
			ScLoiLoaItem  scLoiLoaItem  = this.getManagedBean().getScLoiLoaItem();
			AwardSupplierReqDTO reqDTOLatest=new AwardSupplierReqDTO();
			reqDTOLatest.setScLoiLoaItem(scLoiLoaItem);
			AwardSupplierResDTO resDTOLatest  = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTOLatest);
			if(resDTOLatest.getScLoiLoaItem()!=null){
				scLoiLoaItem=resDTOLatest.getScLoiLoaItem();
			}
			scLoiLoaItem.setComplete(isComplete);
			if(isComplete){//29007-start
				if(null==this.getManagedBean().getNewSupplierJustification() || StringUtils.isBlank(this.getManagedBean().getNewSupplierJustification())|| StringUtils.isEmpty(this.getManagedBean().getNewSupplierJustification())){
					String errorMessage=	ResourceBundleUtils.getMessage(QTConstants.LBL_SC_JUSTIFICATION,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());	
					throw new EpException(MessageConst.ERROR_775185,
							new String[] {errorMessage });
					
				}
			}//29007-End
			if(this.getManagedBean().getNewSupplierJustification() != null && this.getManagedBean().getNewSupplierJustification() != ""){
				int newoffered = 0;
				for(TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()){
					if(tplSpecAnswer.getNewSupplierOffer() != null && tplSpecAnswer.getNewSupplierOffer() !=""){
						newoffered++;
					}
				}
				if(isComplete &&  newoffered <1){
					int index=0;
					for(ScLoiLoaItem loiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
						if(loiLoaItem.getLoiLoaItemId()== scLoiLoaItem.getLoiLoaItemId()){
							this.getManagedBean().getScLoa().getScLoiLoaItems().set(index, scLoiLoaItem);
							break;
						}
						index++;
					}
					throw new EpException(MessageConst.ERROR_775170);
				}
			}
			if(null!=this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()){//29007-start
				scLoiLoaItem.setScLoa(null);
			}//29007-End
			scLoiLoaItem.setLoaSpecJustification(this.getManagedBean().getNewSupplierJustification());//29007
			if(scLoiLoaItem.getLoaSpecJustification() != null && StringUtils.isNotBlank(scLoiLoaItem.getLoaSpecJustification()) && StringUtils.isNotEmpty(scLoiLoaItem.getLoaSpecJustification())){//29007-start
				scLoiLoaItem.getLoaSpecJustification();
				StringBuilder builder=new StringBuilder();
				builder.append(scLoiLoaItem.getLoaSpecJustification()).append(" ");
				scLoiLoaItem.setLoaSpecJustification(builder.toString());
				
			}//29007-End
			reqDTO.setTplSpecAnswerList(this.getManagedBean().getTplSpecAnswerList());
			reqDTO.setScLoiLoaItem(scLoiLoaItem);
						
			AwardSupplierResDTO resDTO  = iQTAwardSO.saveSupplierSpecification(reqDTO);
			int index=0;
			if(CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoaItems())){//29007-start
			for(ScLoiLoaItem loiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
				if(loiLoaItem.getLoiLoaItemId()== resDTO.getScLoiLoaItem().getLoiLoaItemId() || loiLoaItem.getLoiLoaItemId().equals(resDTO.getScLoiLoaItem().getLoiLoaItemId()) ){//29007-start
					if(null!=this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()){
						resDTO.getScLoiLoaItem().setScLoa(this.getManagedBean().getScLoa());
					}
					this.getManagedBean().getScLoa().getScLoiLoaItems().remove(index);
					this.getManagedBean().getScLoa().getScLoiLoaItems().add(index, resDTO.getScLoiLoaItem());//29007-End
					break;
				}
				index++;
			}
			//29007 note 503 start
			List<ScLoiLoaItem> items=new ArrayList<>();
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
				int indexNew=0;
				reqDTOLatest.setScLoiLoaItem(item);
				item = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTOLatest).getScLoiLoaItem();
				for(ScLoiLoaItem loiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
					if(loiLoaItem.getLoiLoaItemId()== item.getLoiLoaItemId() 
							|| loiLoaItem.getLoiLoaItemId().equals(item.getLoiLoaItemId()) ){
						if(null!=this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()){
							resDTO.getScLoiLoaItem().setScLoa(this.getManagedBean().getScLoa());
						}
						this.getManagedBean().getScLoa().getScLoiLoaItems().remove(indexNew);
						this.getManagedBean().getScLoa().getScLoiLoaItems().add(indexNew, item);
						break;
					}
					indexNew++;
				}
			}
			//29007 note 503 end 
			if(this.getManagedBean().getScQt().isZonal() && !isComplete){//29007-start
				for(ScLoiLoaItem loacalloiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
					if( resDTO.getScLoiLoaItem().getLoiLoaItemId()== loacalloiLoaItem.getLoiLoaItemId() ||  resDTO.getScLoiLoaItem().getLoiLoaItemId().equals(loacalloiLoaItem.getLoiLoaItemId()) ||  resDTO.getScLoiLoaItem().getScRequestItem().getRequestNoteItemId()==loacalloiLoaItem.getScRequestItem().getRequestNoteItemId() ||  resDTO.getScLoiLoaItem().getScRequestItem().getRequestNoteItemId().equals(loacalloiLoaItem.getScRequestItem().getRequestNoteItemId())){//29007-start
						loacalloiLoaItem.setComplete(isComplete);
					}
				}
			}//29007-End
			for(ScLoiLoaItem loiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
				if(null!=this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()){
					loiLoaItem.setScLoa(this.getManagedBean().getScLoa());
				}
			}
			}//29007-End
			if(resDTO.isSuccessful()
				&& !this.getManagedBean().isResetAllforNoReport()){
				if(!getManagedBean().getScLoa().getPmStatus().getStatusId().
						equals(QTStatusConst.LOA_PENDING_VERIFICATION)){
				createLampiranA3forQT(isComplete);
				}else{
					createLampiranA3forQTVerification(isComplete);
				}
				if(isComplete){
					onBackToZoneItemDetailsTab();
				}else{
				TemplateSpecReqDTO specReqDto=new TemplateSpecReqDTO();
				
				this.getManagedBean().setScLoiLoaItem(resDTO.getScLoiLoaItem());
				
				if(this.getManagedBean().getSpecKeyword() != null){
					TplSpecQuestion tplSpecQuestion = new TplSpecQuestion();
					TplSpecAnswer tplSpecAnswer = new TplSpecAnswer();
					
					specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
					specReqDto.setScQt(this.getManagedBean().getScQt());
					
					tplSpecQuestion.setClSpecDetail(this.getManagedBean().getSpecKeyword());
					tplSpecAnswer.setTplSpecQuestion(tplSpecQuestion);
					specReqDto.setTplSpecAnswer(tplSpecAnswer);
					specReqDto.setScLoiLoaItem(resDTO.getScLoiLoaItem());
					
					specReqDto.setSpecSearchForApp(true);
					
				}else{
					specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
					specReqDto.setScQt(this.getManagedBean().getScQt());
					specReqDto.setScLoiLoaItem(this.getManagedBean().getScLoiLoaItem());
					specReqDto.setSpecSearchForApp(true);
				}
				TemplateSpecResDTO templateSpecResDTO  = templateSpecSO.searchSupplierSpecification(specReqDto);
				
				this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
				for(TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()){
					if(tplSpecAnswer.getNewSupplierOffer()!=null && tplSpecAnswer.getNewSupplierOffer()!=""){
						tplSpecAnswer.setRenderNewSpec(true);
					}
				}
				successMessage(MessageConst.INFO_700004, getMessageCommon(QTConstants.LBL_SC_LOILOA_SUPPLIER_SPECIFICATION_SAVE), "", Boolean.FALSE);//29007
			}
			}
		}
	}
	/**
	 * create LampiranA3 for QT
	 * @param isComplete
	 * @return 
	 */
	private void createLampiranA3forQT(boolean isComplete){
		AwardSupplierReqDTO awardReqDTO = new AwardSupplierReqDTO();
		List<ScLoaReport> oldLampiranA3List = new ArrayList<>();
		int recordCount = 0;
		
		if(isComplete){
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
						report.setTitle(populateLabelLampiranA3(
								 new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT).format(DateUtils.getCurrentDate()),
								 SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy()).getUserName()));
						report.setIslampiranA3(true);
						recordCount++;
					}
					oldLampiranA3List.add(report);
				}
			}
			if(recordCount>0){
				getManagedBean().getScLoa().setScLoaReports(oldLampiranA3List);
				if(null!=this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()){//29007-start
				awardReqDTO.setScLoa(getManagedBean().getScLoa());
			
				AwardSupplierResDTO resDTO  = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
				List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
				ScLoa scLoa = getManagedBean().getScLoa();
				scLoa.setScLoaReports(scLoaReports);
				getManagedBean().setScLoa(scLoa);
				}//29007-End
			}else{
			ScLoaReport lampiranA3 = getQtAwardSO().createLampiranA3(getManagedBean().getScLoa(),populateLabelLampiranA3(
									 new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT).format(DateUtils.getCurrentDate()),
									 SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy()).getUserName()));
			lampiranA3.setIslampiranA3(true);
			lampiranA3.setScLoa(getManagedBean().getScLoa());
					
			IParameterSO parameterSO = null;
			try {
					parameterSO = BusinessDelegate.getInstance().getServiceObject(
								  ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
					reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD);
					ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
					lampiranA3.setDefinationType(resDTO.getPmParameter());
					lampiranA3.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER); //AQ
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_LOA_DETAIL_BACKINGBEAN, this.getManagedBean().getScLoa().getLoaNo(),
						SLALogConstant.QT_CREATE_LAMPIRANA3FORQT, getUserIdSessionFromLoginId(), e);
			}
					
				getManagedBean().getScLoa().getScLoaReports().add(lampiranA3);
				if(null!=this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()){//29007-start
				awardReqDTO.setScLoa(getManagedBean().getScLoa());
			
				AwardSupplierResDTO resDTO  = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
				List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
				ScLoa scLoa = getManagedBean().getScLoa();
				scLoa.setScLoaReports(scLoaReports);
				getManagedBean().setScLoa(scLoa);
				}//29007-End
			}
		}else{
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
						report.setDeleted(true);
						oldLampiranA3List.add(report);
						recordCount++;
					}
				}
			}
			 
			 int loaItemslampirian=0;//29007-start
			for(ScLoiLoaItem loiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
				if(loiLoaItem.isComplete()){
					loaItemslampirian++;
					break;
				}
			}//29007-End
			
			if(recordCount >0&&loaItemslampirian==0){//29007
				getManagedBean().getScLoa().getScLoaReports().removeAll(oldLampiranA3List);//29007
				if(null!=this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()){//29007-start
				ScLoa scLoa = new ScLoa();
				scLoa.setScLoaReports(oldLampiranA3List);
				awardReqDTO.setScLoa(scLoa);
				getQtAwardSO().deleteLampiranA3Report(awardReqDTO);
				}//29007-End
			}
		}
	}
	/**
	 * generate LampiranA3Pdf From Velocity
	 * @param attachment
	 * @param scLoa
	 * @return 
	 */
	private void generateLampiranA3PdfFromVelocity(ScAttachment attachment, ScLoa scLoa) throws Exception{
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
		IQTAwardSO iQTAwardSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
		TemplateSpecReqDTO specReqDto=new TemplateSpecReqDTO();
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
		if(!scLoa.getScLoiLoaItems().isEmpty()){
			scLoiLoaItemsList = new ArrayList<>();
			for (ScLoiLoaItem scLoiLoaItemOld : scLoa.getScLoiLoaItems()) {
				AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
				reqDTO.setScLoiLoaItem(scLoiLoaItemOld);
				AwardSupplierResDTO resDTO = iQTAwardSO.searchLoiLoaItemDetails(reqDTO);
				ScLoiLoaItem scLoiLoaItem =resDTO.getScLoiLoaItem();
				DateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT);//29007
				String date = df.format(scLoiLoaItem.getChangedDate());
				StringBuilder finalMsg = new StringBuilder();//29007
				if(this.getManagedBean().getScQt().isZonal()){//29007-start
					List<ScLoiLoaItem> zonalItemList=new ArrayList<>();
					for(ScLoiLoaItem loacalloiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
						if(scLoiLoaItem.getLoiLoaItemId()== loacalloiLoaItem.getLoiLoaItemId() || scLoiLoaItem.getLoiLoaItemId().equals(loacalloiLoaItem.getLoiLoaItemId()) || scLoiLoaItem.getScRequestItem().getRequestNoteItemId()==loacalloiLoaItem.getScRequestItem().getRequestNoteItemId() || scLoiLoaItem.getScRequestItem().getRequestNoteItemId().equals(loacalloiLoaItem.getScRequestItem().getRequestNoteItemId())){//29007-start
							zonalItemList.add(loacalloiLoaItem);
						}
					}
					if(zonalItemList.size()>1){
						checkItemSorting(zonalItemList);	
						ScLoiLoaItem latestChangedItem=zonalItemList.get(zonalItemList.size()-1);
						if(latestChangedItem.getLoaSpecJustification() != null && StringUtils.isNotBlank(latestChangedItem.getLoaSpecJustification()) && StringUtils.isNotEmpty(latestChangedItem.getLoaSpecJustification())){//29007
							scLoiLoaItem.setLoaSpecJustification(latestChangedItem.getLoaSpecJustification());	
							scLoiLoaItem.setLatestChangedDate(latestChangedItem.getChangedDate());
							scLoiLoaItem.setComplete(latestChangedItem.isComplete());//29007
							date = df.format(scLoiLoaItem.getLatestChangedDate());
						}
					}else{
						scLoiLoaItem.setLatestChangedDate(scLoiLoaItem.getChangedDate());
						date = df.format(scLoiLoaItem.getLatestChangedDate());
					}
				}//29007-End
				if(scLoiLoaItem.getScRequestItem() != null && scLoiLoaItem.getScRequestItem().getItemName() !=null && StringUtils.isNotBlank(scLoiLoaItem.getScRequestItem().getItemName()) && StringUtils.isNotEmpty(scLoiLoaItem.getScRequestItem().getItemName())){
					String itemName=VmUtil.getInstance().replaceAllSpecialCharactersToHTML(scLoiLoaItem.getScRequestItem().getItemName());
					scLoiLoaItem.setItemNameForVelocity(VmUtil.getInstance().addLineBreaks(itemName,60));
				}
				if(scLoiLoaItem.getLoaSpecJustification() != null && StringUtils.isNotBlank(scLoiLoaItem.getLoaSpecJustification()) && StringUtils.isNotEmpty(scLoiLoaItem.getLoaSpecJustification())){//29007
					finalMsg.append(QTConstants.LAMPIRAN_A3_JUSTIFICATION+scLoiLoaItem.getLoaSpecJustification()+" "+date);
					String finalMessage=VmUtil.getInstance().replaceAllSpecialCharactersToHTML(finalMsg.toString());
					scLoiLoaItem.setLoaSpecJustificationForVelocity(VmUtil.getInstance().addLineBreaks(finalMessage,60));//29007
				}
				
				
				specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
				specReqDto.setScQt(this.getManagedBean().getScQt());
				specReqDto.setScLoiLoaItem(scLoiLoaItem);
				specReqDto.setSpecSearchForApp(false);
				
				TemplateSpecResDTO templateSpecResDTO  = templateSpecSO.searchSupplierSpecification(specReqDto);
				if(null!=templateSpecResDTO && null!=templateSpecResDTO.getSpecAnswerList() && CollectionUtils.isNotEmpty(templateSpecResDTO.getSpecAnswerList())){//29007-start
					boolean newOfferedSpecification=false;//29007
					List<Long> replacedQuestionIds = new ArrayList<>();
					for(TplSpecAnswer specAnswer:templateSpecResDTO.getSpecAnswerList()){
						String feedback = null;
						if (null != specAnswer.getAnswerCompliance()) {
							if (specAnswer.getAnswerCompliance()) {
								feedback = QTConstants.LAMPIRAN_A3_TEXT_YA;
							} else {
								feedback = QTConstants.LAMPIRAN_A3_TEXT_TIDAK;
							}
							specAnswer.setFeedback(feedback);
						}
						if(!scLoiLoaItem.isComplete()){//29007-start
							specAnswer.setNewSupplierOffer(null);	
						}//29007-End
						if(specAnswer.getNewSupplierOffer() != null && StringUtils.isNotBlank(specAnswer.getNewSupplierOffer()) && StringUtils.isNotEmpty(specAnswer.getNewSupplierOffer())){//29007-start
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
					if(!newOfferedSpecification){//29007-start
					scLoiLoaItem.setLoaSpecJustificationForVelocity(null);	
					}//29007-End
					scLoiLoaItem.setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());//20230706@asyrafhaziq- SQUBE Bug NullPointer#3
				}//29007-End
				
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
		

		try(FileOutputStream fos = new FileOutputStream(getActualFilePath(attachment))) {
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_LAMPIRAN_A3, context, 
					fos);
		} catch (FileNotFoundException e) {
			//Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] START			 
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_LOA_DETAIL_BACKINGBEAN, this.getManagedBean().getScQt().getQtNo(),
					SLALogConstant.QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY, getUserIdSessionFromLoginId(), e);
			//Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] END
		} catch (Exception ex) {
			//Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] START			 
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_LOA_DETAIL_BACKINGBEAN, this.getManagedBean().getScQt().getQtNo(),
					SLALogConstant.QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY, getUserIdSessionFromLoginId(), ex);
			//Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] END
		} 
	}
	/**
	 * submit Conformation For LampiranA3
	 * @param 
	 * @return 
	 */
	public void submitConformationForLampiranA3(){
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDto=new TemplateSpecReqDTO();
		int recordCount = 0;
		int itemCount = 0;
		StringBuilder itemsList = new StringBuilder();
		int completeItemCount = 0;
		List<ScLoiLoaItem> completeItems= new ArrayList<>();
		int zoneitemCount = 0;
		ScLoiLoaItem zoneloiLoaItem = null;
		boolean isZonal = this.getManagedBean().getScLoa().getScLoiLoa().isZonal();
		List<Long> itemIds = new ArrayList<>();
		HashMap<Long, Long> itemZoneMap = new HashMap<>();
		
		if(this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoa().getScLois())){
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
		if(itemCount>0){
			itemsList.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
			throw new EpException(MessageConst.ERROR_775175, new String[] {itemsList.toString()});
		}
		if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
			for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
				if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
					recordCount++;
				}
			}
		}
		if(recordCount>0 && this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)){
			RequestContext.getCurrentInstance().execute("submitConfirmPopUp.show()");
		}else if(recordCount==0 && completeItemCount>0){
			for(ScLoiLoaItem scLoiLoaItem : completeItems){
				this.getManagedBean().setSpecKeyword(" ");
				this.getManagedBean().setScLoiLoaItem(scLoiLoaItem);
				this.getManagedBean().setResetAllforNoReport(true);
				this.resetAllNewOfferedSpecificationSubmit();
			}
			this.submitAction();
			return;
		}else{
			this.submitAction();
			return;
		}
	}
	/**
	 * verify Conformation For LampiranA3
	 * @param bpmAction
	 * @return 
	 */
	public void verifyConformationForLampiranA3(String bpmAction){
		log.info("verifyConformationForLampiranA3");
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDto=new TemplateSpecReqDTO();
		int recordCount = 0;
		int itemCount = 0;
		StringBuilder itemsList = new StringBuilder();
		int zoneitemCount = 0;
		ScLoiLoaItem zoneloiLoaItem = null;
		boolean isZonal = this.getManagedBean().getScLoa().getScLoiLoa().isZonal();
		List<Long> itemIds = new ArrayList<>();
		HashMap<Long, Long> itemZoneMap = new HashMap<>();
		
		if(this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoiLoa().getScLois())){
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
		if(itemCount>0){
			itemsList.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
			throw new EpException(MessageConst.ERROR_775176, new String[] {itemsList.toString()});
		}
		if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
			for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
				if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
					recordCount++;
				}
			}
		}
		if(recordCount>0 && this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)){
			this.getManagedBean().setLoaBpmVerify(bpmAction);
			RequestContext.getCurrentInstance().execute("verifyConfirmPopUp.show()");
		}else{
			this.approveBeforeupdateBpmTaskAction(bpmAction);
			return;
		}
	}
	private List<ScLoiLoaItem> sortLoiLoaItems(List<ScLoiLoaItem> loiLoaItems, boolean isZonal){
		
		if (isZonal) {
			Comparator<ScLoiLoaItem> zonalComparator = new Comparator<ScLoiLoaItem>() {
				@Override
				public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
					if (s1 == null || s2 == null) {
						return 0;
					}

					long zoneId1 = (s1.getTplSpecZoneDtl() != null) ? s1.getTplSpecZoneDtl().getSpecZoneId()
							: Long.MIN_VALUE;
					long zoneId2 = (s2.getTplSpecZoneDtl() != null) ? s2.getTplSpecZoneDtl().getSpecZoneId()
							: Long.MIN_VALUE;

					int zoneCompare = Long.compare(zoneId1, zoneId2);
					if (zoneCompare != 0) {
						return zoneCompare;
					}

					if (s1.getTplSpecTierQty() != null && s2.getTplSpecTierQty() != null) {

						int tierCompare = Long.compare(s1.getTplSpecTierQty().getSpecTierQtyId(), s2
								.getTplSpecTierQty().getSpecTierQtyId());
						if (tierCompare != 0) {
							return tierCompare;
						}
					}

					long requestNoteItemId1 = (s1.getScRequestItem() != null) ? s1.getScRequestItem()
							.getRequestNoteItemId() : Long.MIN_VALUE;
					long requestNoteItemId2 = (s2.getScRequestItem() != null) ? s2.getScRequestItem()
							.getRequestNoteItemId() : Long.MIN_VALUE;

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

					long requestNoteItemId1 = (s1.getScRequestItem() != null) ? s1.getScRequestItem()
							.getRequestNoteItemId() : Long.MIN_VALUE;
					long requestNoteItemId2 = (s2.getScRequestItem() != null) ? s2.getScRequestItem()
							.getRequestNoteItemId() : Long.MIN_VALUE;

					if (s1.getTplSpecTierQty() != null && s2.getTplSpecTierQty() != null) {

						int tierCompare = Long.compare(s1.getTplSpecTierQty().getSpecTierQtyId(), s2
								.getTplSpecTierQty().getSpecTierQtyId());
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
	private int checkSupplierOffered(TemplateSpecResDTO templateSpecResDTO){
		int newSupplierOffer=0;
		for(TplSpecAnswer tplSpecAnswer: templateSpecResDTO.getSpecAnswerList()){
			if(StringUtils.isNotBlank(tplSpecAnswer.getNewSupplierOffer())){
				newSupplierOffer++;
			}
		}
		return newSupplierOffer;
	}
	private boolean checkItemAndZoneAdded(HashMap<Long, Long> itemZoneMap,Long itemId,Long specZoneId){
		for (Map.Entry<Long, Long> itemZone : itemZoneMap.entrySet()) {
			Long eItemId = itemZone.getKey();
			Long eZoneId = itemZone.getValue();
			boolean alreadyAdded=(eItemId.equals(itemId) || eItemId==itemId)
					 && (eZoneId==specZoneId || eZoneId.equals(specZoneId));
			if(alreadyAdded){
				return false;
			}
		}
		return true;
	}
	public boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())|| QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());//29007&31423
	}
	/**
	 * add New Offered Specification
	 * @param spec
	 * @return 
	 */
	public void addNewOfferedSpec(TplSpecAnswer spec){
		log.info("addNewOfferedSpec");
		String specDet = spec.getTplSpecQuestion().getClSpecDetail();
		for(TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()){
			if(tplSpecAnswer.getTplSpecQuestion().getClSpecDetail().equals(specDet)){
				tplSpecAnswer.setRenderNewSpec(true);
			}
		}
	}
	/**
	 * refresh New Offered Specification
	 * @param spec
	 * @return 
	 */
	public void refreshNewOfferedSpec(TplSpecAnswer spec){
		log.info("refreshNewOfferedSpec");
		String specDet = spec.getTplSpecQuestion().getClSpecDetail();
		for(TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()){
			if(tplSpecAnswer.getTplSpecQuestion().getClSpecDetail().equals(specDet)){
				tplSpecAnswer.setNewSupplierOffer(null);
				tplSpecAnswer.setRenderNewSpec(false);//29007
			}
		}
	}
	/**
	 * resetAll New Offered Specification confirmation.
	 * @param 
	 * @return 
	 */
	public void resetAllNewOfferedSpecification(){
		RequestContext.getCurrentInstance().execute("resetAllConfirmPopUp.show()");
	}
	/**
	 * resetAll New Offered Specification submit.
	 * @param 
	 * @return 
	 */
	public void resetAllNewOfferedSpecificationSubmit(){
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();
		if(this.getManagedBean().getSpecKeyword() != null){
			this.getManagedBean().setSpecKeyword(null);
			specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
			specReqDto.setScQt(this.getManagedBean().getScQt());
			specReqDto.setScLoiLoaItem(this.getManagedBean().getScLoiLoaItem());
			specReqDto.setSpecSearchForApp(true);
			
			TemplateSpecResDTO templateSpecResDTO  = getTemplateSpecSO().searchSupplierSpecification(specReqDto);
			
			this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
		}
		
		for(TplSpecAnswer answer : this.getManagedBean().getTplSpecAnswerList()){
			answer.setNewSupplierOffer(null);
		}
		this.getManagedBean().setNewSupplierJustification(null);
		this.getManagedBean().setResetAll(true);
		saveSupplierSpecification(false);
	}
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
	
	//#25725 start
	public void onChangeAgreementReqType(AjaxBehaviorEvent event){
		Boolean agreementReq = (Boolean) event.getComponent().getAttributes()
				.get("value");
		Boolean isPeriodic = (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) || (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN));
		Boolean isNonPeriodic = (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)) || (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN));
		ScLoa scLoa = this.getManagedBean().getScLoa();
		// #25726 START
		Boolean isOneOff = this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
		// #25726 END
		
		if(over500K(scLoa)){			
			this.getManagedBean().setAbove500K(true);
		}else{			
			this.getManagedBean().setAbove500K(false);
		}
		if(((isPK7(scLoa) && !isReasonPK2(scLoa) && !isReasonSpecialItem(scLoa)) && (isPeriodic || (isNonPeriodic && over500K(scLoa))))&& !agreementReq){// #25725 - note 146
			this.getManagedBean().setAgreementReqChange(true);
			RequestContext.getCurrentInstance().execute("changeAgreementReqDlg.show()");			
		}else if((agreementReq == false)&&((isNonPeriodic && over500K(scLoa)) && (!isPK7(scLoa) || isReasonPK2(scLoa) || isReasonSpecialItem(scLoa)))){// #25725 - note 146					
			scLoa.setAgreementReq(false);
			this.getManagedBean().setShowApprovalLetter(Boolean.TRUE); // #25725 - note 146
			this.getManagedBean().setAgreementReqChange(true);
			RequestContext.getCurrentInstance().update("contractDetailsId");
		}else{					
			scLoa.setAgreementReq(true);
			this.getManagedBean().setAgreementReqChange(true);
			this.getManagedBean().setShowApprovalLetter(Boolean.FALSE); // #25725 - note 146
			this.getManagedBean().getScLoa().setScLoaAttachment(null);
			RequestContext.getCurrentInstance().update("contractDetailsId");
		}
		
		// #25726 START
		if(!this.getManagedBean().getScLoa().getProcurementMode().getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)){
			
			if(((isOneOff && over500K(scLoa)) || (isNonPeriodic && over500K(scLoa))) && !agreementReq){
				scLoa.setAgreementReq(false);
				this.getManagedBean().setAgreementReqChange(true);
				RequestContext.getCurrentInstance().update("contractDetailsId");
			}else if(((isOneOff && over500K(scLoa)) || (isNonPeriodic && over500K(scLoa))) && agreementReq){
				scLoa.setAgreementReq(true);				
				RequestContext.getCurrentInstance().update("contractDetailsId");
			}
		}
		// #25726 END
			
	}
	
	public boolean disabledButton(){
		Boolean result = true;
		Boolean isPeriodic = (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) || (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN));
		if(this.getManagedBean().getScLoa().getProcurementMode().getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)){			
			Boolean isNonPeriodic = (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)) || (this.getManagedBean().getScLoa().getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN));
			Long disabledID = new Long(62505);
			
			if(isNonPeriodic || (isPK7(this.getManagedBean().getScLoa()) && isPeriodic)){
				result = false;
				
				//#25725 - note 125/128 - start
				//#25725 note 146 - start (comment out)
				/*if(isReasonPK2(this.getManagedBean().getScLoa()) && isPeriodic){
					result = true;
				}*/
				//#25725 note 146 - end
				//#25725 - note 125/128 - end
				if(getManagedBean().getScLoa().getPmStatus().getStatusId().equals(disabledID)
						|| (isReasonPK2(this.getManagedBean().getScLoa()) && isPeriodic) //#25725 note 146
						|| (isReasonSpecialItem(this.getManagedBean().getScLoa()) && isPeriodic)) { //#25725 note 146
					result = true;
				}
			}
		}else{
			result = false;
			// #25726 START
			if(isPeriodic){
				result = true;
			}
			// #25726 END
		}		
		return result;
	}
	
	public void agreementReqYes(){
		ScLoa scLoa = this.getManagedBean().getScLoa();
		
		scLoa.setAgreementReq(false);
		RequestContext.getCurrentInstance().update("agReqID");
	}
	
	public void agreementReqNo(){
		ScLoa scLoa = this.getManagedBean().getScLoa();
		
		scLoa.setAgreementReq(true);
		RequestContext.getCurrentInstance().update("agReqID");
	}
	
	public String docStatus(){
		return getStatusDescById(getManagedBean().getScLoa().getPmStatus().getStatusId());			
	}	
	
	//#25725 end
	
	/*29007 Start
	 * sorting LoiItems
	 */
	private void checkItemSorting(List<ScLoiLoaItem> loiLoaItem) {
		Collections.sort(loiLoaItem, new Comparator<ScLoiLoaItem>() {
			public int compare(ScLoiLoaItem loiItem1,
					ScLoiLoaItem loiItem2) {

				return new CompareToBuilder()
						.append(loiItem1.getChangedDate(),
								loiItem2.getChangedDate())
						.toComparison();
			}
		});
	}
	//29007 End
	
	//27797 & 29006 start
	public Integer getScRequestRowCount(Long reqItemId) {
		int rowCount = 0;

		for (Map.Entry<Long, Integer> entry : this.getManagedBean().getReqItemRowSpanMap().entrySet()) {
			if (entry.getKey() != null&& entry.getKey().compareTo(reqItemId) == 0) {
				rowCount = entry.getValue();
			}
		}
		
		return rowCount;
	}
	
	public BigDecimal getScRequestSubTotalZonal(Long reqItemId) {
		BigDecimal maxSubTotal = new BigDecimal(0);
		
		for (Map.Entry<Long, BigDecimal> entry : this.getManagedBean().getReqItemMaxSubTotalMap().entrySet()) {
			if (entry.getKey() != null&& entry.getKey().compareTo(reqItemId) == 0) {
				maxSubTotal = entry.getValue();
			}
		}
		
		return maxSubTotal;
	}
	
	public Long getScRequestSession(Long reqItemId, int index) {
		if(index == 0){
			this.getManagedBean().setReqItemSession(reqItemId);
			return 0L;
		}else if(this.getManagedBean().getReqItemSession().equals(reqItemId)) {
			return reqItemId;
		}else{
			this.getManagedBean().setReqItemSession(reqItemId);
			return 0L;
		}
	}
	
	public Long getScRequestSubTotalSession(Long reqItemId, int index) {
		if(index == 0){
			this.getManagedBean().setReqItemSubTotalSession(reqItemId);
			return 0L;
		}else if(this.getManagedBean().getReqItemSubTotalSession().equals(reqItemId)) {
			return reqItemId;
		}else{
			this.getManagedBean().setReqItemSubTotalSession(reqItemId);
			return 0L;
		}
	}
	
	public void onZonalSearchSupplier() {
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		
		if (this.getItemNameSearchCriteria() != null && this.getItemNameSearchCriteria() != "") {
			reqDTO.setLoaId(this.getManagedBean().getScLoa().getLoaId());
			AwardSupplierResDTO resDTO = getQtAwardSO().searchScLoiLoaItemByLoaId(reqDTO);
			List<ScLoiLoaItem> containCriteriaScLoiLoaItemList = new ArrayList<>(); //Sonarqube DP Code Smell Minor Zairul Rizwan [26/4/2024]
			
			for(ScLoiLoaItem scLoiLoaItem : resDTO.getScLoiLoaItemList()){
				String itemNameSearchCriteria = this.getItemNameSearchCriteria().toUpperCase().trim().replaceAll("[ ]{2,}", " ");
				String checkedItem = scLoiLoaItem.getScRequestItem().getItemName().toUpperCase().trim().replaceAll("[ ]{2,}", " ");
				
				if(checkedItem.contains(itemNameSearchCriteria)){
					containCriteriaScLoiLoaItemList.add(scLoiLoaItem);
				}			
				
			}

			this.getManagedBean().setNameZoneScLoiLoaItemList(containCriteriaScLoiLoaItemList);
			
			
		} else {
			reqDTO.setLoaId(this.getManagedBean().getScLoa().getLoaId());
			AwardSupplierResDTO resDTO = getQtAwardSO().searchScLoiLoaItemByLoaId(reqDTO);

			this.getManagedBean().setNameZoneScLoiLoaItemList(resDTO.getScLoiLoaItemList());
		}
	}
	//27797 & 29006 end
	
	//#32280 - start
	public Long getScRequestAssignButtonSession(Long reqItemId, int index) {
		if(index == 0){
			this.getManagedBean().setReqItemAssignButtonSession(reqItemId);
			return 0L;
		}else if(this.getManagedBean().getReqItemAssignButtonSession().equals(reqItemId)) {
			return reqItemId;
		}else{
			this.getManagedBean().setReqItemAssignButtonSession(reqItemId);
			return 0L;
		}
	}	
	//#32280 - end
	
	/**
	 * 29007-start
	 * Get Parameter description by parameter for report.
	 * 
	 * @param parameter
	 *            the PmParameter instance.
	 * @return parameter description
	 */
	@Override
	public String getParameterDescForReport(PmParameter parameter) {
		if (parameter != null
				&& StringUtils.isNotBlank(parameter.getParameterType())) {
			return getParameterDesc(
					parameter,
					myLocale);
		} else {
			return null;
		}
	} 
	
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
	
	private void createLampiranA3forQTVerification(boolean isComplete){
		log.info("==============startcreateLampiranA3forQTVerification=============");
		AwardSupplierReqDTO awardReqDTO = new AwardSupplierReqDTO();
		List<ScLoaReport> oldLampiranA3List = new ArrayList<>();
		int recordCount = 0;
		
		if(isComplete){
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr) && null!=report.getDefinationType() && ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD.equals(report.getDefinationType().getParameterCode())) {
						report.setTitle(populateLabelLampiranA3(
								 new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT).format(DateUtils.getCurrentDate()),
								 SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy()).getUserName()));
						report.setIslampiranA3(true);
						recordCount++;
					}
					oldLampiranA3List.add(report);
				}
			} 
			if(recordCount>0){
				getManagedBean().getScLoa().setScLoaReports(oldLampiranA3List);
				if(null!=this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()){//29007-start
				awardReqDTO.setScLoa(getManagedBean().getScLoa());
			
				AwardSupplierResDTO resDTO  = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
				List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
				ScLoa scLoa = getManagedBean().getScLoa();
				scLoa.setScLoaReports(scLoaReports);
				getManagedBean().setScLoa(scLoa);
				}//29007-End
			}else{
			ScLoaReport lampiranA3 = getQtAwardSO().createLampiranA3(getManagedBean().getScLoa(),populateLabelLampiranA3(
									 new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT).format(DateUtils.getCurrentDate()),
									 SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy()).getUserName()));
			lampiranA3.setIslampiranA3(true);
			lampiranA3.setScLoa(getManagedBean().getScLoa());
					
			IParameterSO parameterSO = null;
			try {
					parameterSO = BusinessDelegate.getInstance().getServiceObject(
								  ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
					reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD);
					ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
					lampiranA3.setDefinationType(resDTO.getPmParameter());
					lampiranA3.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER); //AQ
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
						SLALogConstant.QT_LOA_DETAIL_BACKINGBEAN,
						this.getManagedBean().getScQt().getQtNo(),
						SLALogConstant.QT_CREATE_LAMPIRANA3PDF_FOR_QT_VERIFICATION,
						getUserIdSessionFromLoginId(), e);
			}
					
				getManagedBean().getScLoa().getScLoaReports().add(lampiranA3);
				if(null!=this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()){//29007-start
				awardReqDTO.setScLoa(getManagedBean().getScLoa());
			
				AwardSupplierResDTO resDTO  = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
				List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();
				ScLoa scLoa = getManagedBean().getScLoa();
				scLoa.setScLoaReports(scLoaReports);
				getManagedBean().setScLoa(scLoa);
				}//29007-End
			}
		}else{
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
			 
			 int loaItemslampirian=0;//29007-start
			for(ScLoiLoaItem loiLoaItem:this.getManagedBean().getScLoa().getScLoiLoaItems()){
				if(loiLoaItem.isComplete()){
					loaItemslampirian++;
					break;
				}
			}//29007-End
			
			if(recordCount >0&&loaItemslampirian==0){//29007
				getManagedBean().getScLoa().getScLoaReports().removeAll(oldLampiranA3List);//29007
				if(null!=this.getManagedBean().getScLoa() && !this.getManagedBean().getScLoa().isBrandNew()){//29007-start
				ScLoa scLoa = new ScLoa();
				scLoa.setScLoaReports(oldLampiranA3List);
				awardReqDTO.setScLoa(scLoa);
				getQtAwardSO().deleteLampiranA3Report(awardReqDTO);
				}//29007-End
			}
		}
		log.info("==============endcreateLampiranA3forQTVerification=============");
	}//29007-End
	
	//#25025 - start
	public String setValueLowercase(String text, int index){
		//Change SuratSetujuterima(TermasukLampiranA) capital T to small t.
		if (text.replaceAll(SourcingConstants.REG_EXP_WHITE_SPACE, "").equalsIgnoreCase("SuratSetujuterima(TermasukLampiranA)")){
			char[] arr = text.toCharArray();
			arr[index] = Character.toLowerCase(arr[index]);
			return new String(arr);//return string in lowercase  //Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
		}
		return text;	
	}
	//#25025 - end
	
	//#25725 start
	
	private boolean validateApprovalLetter(ScLoa scLoa) {
		Boolean approvalLetter = true;
		Boolean isNonPeriodic = (scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)) || (scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN));
		Boolean isOneOff = scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
		Boolean isDocTypeQuotation = scLoa.getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION);
		
		if (scLoa.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_SUBMISSION)
				|| scLoa.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_APPROVAL)
				|| scLoa.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_REVISION)
				|| scLoa.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)) {		
			
			if(this.getManagedBean().isShowApprovalLetter() &&  over500K(scLoa) && isNonPeriodic && (!isPK7(scLoa) || isReasonPK2(scLoa) || isReasonSpecialItem(scLoa)) && !isDocTypeQuotation){
				if(scLoa.getScLoaAttachment() == null){
					RequestContext.getCurrentInstance().execute("approvalLetterAlertDlg.show()");
					approvalLetter = false;
					return approvalLetter;
				}
			}
			else if(isDocTypeQuotation && this.getManagedBean().isShowApprovalLetter()&&((isNonPeriodic||isOneOff) && (!isPK7(scLoa) || isReasonPK2(scLoa) || isReasonSpecialItem(scLoa)) && over500K(scLoa))){
				if(scLoa.getScLoaAttachment() == null){
					RequestContext.getCurrentInstance().execute("approvalLetterAlertDlg.show()");
					approvalLetter = false;
					return approvalLetter;
				}
			}else{
				scLoa.setScLoaAttachment(null);
			}
		}		
		return approvalLetter;
	}
	
	//#25725 end
	
	//#25726 start
	private boolean validateNewBond(ScLoa scLoa) {
		if (null != this.getManagedBean().getScLoa() && CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoaReports())) {
	         for (ScLoaReport report : this.getManagedBean().getScLoa().getScLoaReports()) {
	             if (null != report && QTConstants.SURAT_SETUJU_TERIMA.equals(report.getTitle())) {
	                 if (report.getScLoaReportAttachment() != null) {
	                	 RequestContext.getCurrentInstance().execute("bondChangesAlertDlg.show()");
	                	 return false;
	                 }	                
	            }
	         }
	     }
		return true;
	}	
	//#25726 end
	
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
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE
				.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_SEARCH
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
					|| SourcingConstants.PORTLET_NAME_DP_SEARCH
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
						|| SourcingConstants.PORTLET_NAME_DP_LOA_LIST
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
						|| QTConstants.PORTLET_NAME_QT_LIST
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// #32648 populate lampiran 7 if LOA at supplier - end
	
	//#21692 - start
	public boolean isSupplierStatusRejectedEPV(Long supplierId) {
		boolean supplierStatus = false;
		
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			List<Long> suppList = new ArrayList<>();
			if (null != supplierId) {
				suppList.add(supplierId);
				supplierStatus = detailSO.checkSupplierEPVStatus(getManagedBean().getScQt().getQtId(), suppList);
			}
			
		} finally {
			detailSO = null;
		}
		
		return supplierStatus;
	}
	
	public boolean isLOAStatusAbleToCancel() {
		boolean isAble = false;
		
		IScWorkFlowStatusSO workFlowStatusSO;
		ScWorkFlowStatusReqDTO scWreqDTO = new ScWorkFlowStatusReqDTO();
		workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		scWreqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
		scWreqDTO.setDocId(this.getManagedBean().getScLoa().getLoaId());
		
		ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(scWreqDTO).getWorkFlowStatus();
		
		isAble = null != currentWorkFlowStatus && (
				currentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_SUBMISSION) ||
				currentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_APPROVAL)||
				currentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_REVISION)||
				currentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)
				)? true : false;
		
		return isAble;
	}
	//#21692 - end
	
	// 34242 - Start
	public void retainAwardPage() {
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("form:itemtbl");
		if (null != dataTable) {
			this.getManagedBean().setRetainPageNumber(dataTable.getFirst());
		}
	}
	// #34242 - End
	
	//29007 - fix issue 2 search action note 347 - start
	/**
	 * To calculate total price based on item search result
	 * @return BigDecimal - Total Price of ScLoiLoaItem list
	 */
	public BigDecimal calculateTotalItemPrice() {
		BigDecimal totalPrice = BigDecimal.ZERO;
		if(CollectionUtils.isNotEmpty(this.getManagedBean().getLoiLoaItemsResult())){
			for(ScLoiLoaItem item: this.getManagedBean().getLoiLoaItemsResult()){
				totalPrice = calcSubTotalPrice(item).add(totalPrice) ;
			}
		}
		totalPrice = totalPrice.setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024

		return totalPrice;
	}
	//29007 - fix issue 2 search action note 347 - end
	
	//Bug #34757 - Start
	/**
	 * retrieve latest item code from LOA detail
	 * 
	 * @param reqItem
	 * @return
	 */
	public String getLatestItemCode(ScRequestItem reqItem) {

		//#34757 note-#41 - Start
		for (ScLoiLoaItem item : this.getManagedBean().getLoiLoaItemsResult()) {
			if (item.getScRequestItem().getRequestNoteItemId().equals(reqItem.getRequestNoteItemId())) {
				item.getScRequestItem().setCmItem(reqItem.getCmItem());
			}
		}
		//#34757 note-#41 - End
		if (reqItem.getTplSpecQuestion().getClPriceTypeCode().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER)
				|| reqItem.getTplSpecQuestion().getClPriceTypeCode().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)) {
			return getQtAwardSO().getCmItemExtensionCode(reqItem);
		} else {
			return reqItem.getCmItem().getExtensionCode();
		}
	}
	
	/**
	 * decide whether to show 'Assign' button or not
	 * show button if data is not tier qty
	 * 
	 * @param loiLoaItem
	 * @return
	 */
	public boolean isShowAssignButton(ScLoiLoaItem loiLoaItem) {
		//35284 - start
		if (loiLoaItem == null) {
			return false;
		}
		//35284 - end
		if (!this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)) {
			return isShowAssignButtonIfNotPR(loiLoaItem);
		} else {
			return true;
		}
	}
	
	private boolean isShowAssignButtonIfNotPR(ScLoiLoaItem loiLoaItem) {
		boolean isShow = false;
		if (loiLoaItem.getTplSpecZoneDtl() != null) {
			if (this.getManagedBean().getFirstZone() == null) {
				this.getManagedBean().setFirstZone(loiLoaItem.getTplSpecZoneDtl().getZoneName());
			}
			
			if (this.getManagedBean().getFirstZone().equals(loiLoaItem.getTplSpecZoneDtl().getZoneName())) {
				isShow = true;
			}
			
			isShow = checkItemList(loiLoaItem);
		} else {
			isShow = true;
		}
		//35284 - start
		boolean isFromQty = null != loiLoaItem.getTplSpecTierQty() && null != loiLoaItem.getTplSpecTierQty().getFromQty() ? loiLoaItem.getTplSpecTierQty().getFromQty().compareTo(BigDecimal.ONE)==0 : false;
		boolean isSpecTierQty = null == loiLoaItem.getTplSpecTierQty() || isFromQty;
		isShow = isSpecTierQty && isShow;
		//35284 - end

		return isShow;
	}
	
	private boolean checkItemList(ScLoiLoaItem loiLoaItem) {
		if (this.getManagedBean().getItemList() == null) {
			this.getManagedBean().setItemList(new ArrayList<Long>());
		}
		if (this.getManagedBean().getLoaItemIds() == null) {
			this.getManagedBean().setLoaItemIds(new ArrayList<Long>());
		}
		if (!this.getManagedBean().getItemList().contains(loiLoaItem.getScRequestItem().getRequestNoteItemId())) {
			this.getManagedBean().getItemList().add(loiLoaItem.getScRequestItem().getRequestNoteItemId());
			this.getManagedBean().getLoaItemIds().add(loiLoaItem.getLoiLoaItemId());
		}
		return this.getManagedBean().getLoaItemIds().contains(loiLoaItem.getLoiLoaItemId());
	}
	//Bug #34757 - End
	
	// Bug #35078@Syazani -- Start 14/09/2023
	public boolean isQtAudit() {
		return QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// Bug #35078@Syazani -- End 14/09/2023
	//29006 start @zaim
		/**
		 * @param scLoiLoaItem
		 * @param scLoa
		 * @param currentSupplier
		 */
		public void ConstructTplSpecAnswerForLampiranA3(ScLoiLoaItem scLoiLoaItem, ScLoa scLoa, Long currentSupplier){
			List<Map<String, String>> listItem = new ArrayList<>();
			String answerRemark = SSTConstant.EMPTY_STRING;
			int zoneCount = 1;
			if ((scLoiLoaItem != null && scLoiLoaItem.getTplSpecAnswerList() != null)
					&& !scLoiLoaItem.getTplSpecAnswerList().isEmpty()) {
				if (scLoiLoaItem.getScRequestItem() != null & scLoiLoaItem.getScRequestItem().getTplSpecQuestion() != null) {
					TplSpecQuestion question = scLoiLoaItem.getScRequestItem().getTplSpecQuestion();
					List<TplSpecAnswer> answerList = getTemplateSpecSO().findSpecAnswersByQuestion(
							question.getSpecQuestionId());
					if (CollectionUtils.isNotEmpty(scLoiLoaItem.getZoneNameList())) {
						// zonal
						for (String zone : scLoiLoaItem.getZoneNameList()) {
							Map<String, String> map = new HashMap<>();
							if (CollectionUtils.isEmpty(listItem)) {
								map.put(SSTConstant.questionType, question.getQuestionType());
								//36122 - start @zaim
								map.put(SSTConstant.clItem, VmUtil.getInstance().escapeAndWrapText(question.getClItem(), 12,SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim
								//36122 - end @zaim
								map.put(SSTConstant.CL_FULFILMENT_TYPE_CODE,getParameterDesc(question.getClFulfilmentTypeCode(),SourcingConstants.MY_LOCALE));
								map.put(SSTConstant.uomName, question.getClUom().getUomName());
								map.put(SSTConstant.clUomFrequency, question.getClUomFrequency().toString());
								map.put(SSTConstant.clUomPerDay, question.getClUomPerDay() == null ? SSTConstant.TB
										: question.getClUomPerDay().toString());
								map.put(SSTConstant.clUomPerMonth, question.getClUomPerMonth() == null ? SSTConstant.TB
										: question.getClUomPerMonth().toString());
								map.put(SSTConstant.CL_DURATION_MONTH, question.getClDurationMonth().toString());
								if (zone != null) {
									//36122 start
									map.put(SSTConstant.zoneName, VmUtil.getInstance().escapeAndWrapText(zone, 11,SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim
									//36122 - end
								}
								map.put(SSTConstant.ZONE_SIZE, String.valueOf(scLoiLoaItem.getZoneNameList().size()));
								map.put(SSTConstant.quantity, scLoiLoaItem.getQty() == null? SSTConstant.TB : scLoiLoaItem.getQty().toString());
								map.put(SSTConstant.serviceScope, SSTConstant.TB);
								// retrieve supplier offer start
								if (answerList != null && !answerList.isEmpty()) {
									for (TplSpecAnswer answer : answerList) {
										int indi = 1;
										if (answer.getTplSpecSuppAnswer() != null
												&& answer.getTplSpecSuppAnswer().getSmSupplier().getSupplierId()
														.equals(currentSupplier)) {
											if (answer.getIsWip() != null) {
												if ((answer.getNewSupplierOffer() != null && StringUtils.isNotEmpty(answer
														.getNewSupplierOffer())) && scLoiLoaItem.isComplete()) {
													// supplier respond and make new
													// supplier offer
													answerRemark = answer.getNewSupplierOffer(); //36681&35295 - zaim
													map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answer.getNewSupplierOffer(), 13, SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim

													if (indi == scLoiLoaItem.getZoneNameList().size()){
														map.put(SSTConstant.LAST_INDEX_WIP, "true");
													}
													break;
												} else if ((answer.getNewSupplierOffer() == null)
														&& (answer.getAnswerRemark() != null && !answer.getAnswerRemark()
																.isEmpty())) {
													// supplier respond but no
													// changes
													answerRemark = answer.getAnswerRemark(); //36681&35295 - zaim
													map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answer.getAnswerRemark(), 13, SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim
													map.put(SSTConstant.IS_WIP, "true");
													if (indi == scLoiLoaItem.getZoneNameList().size()){
														map.put(SSTConstant.LAST_INDEX_WIP, "true");
													}
													break;
												}
												
											} else {
												if ((answer.getNewSupplierOffer() != null && StringUtils.isNotEmpty(answer
														.getNewSupplierOffer())) && scLoiLoaItem.isComplete()) {
													// supplier respond and make new
													// supplier offer
													answerRemark = answer.getNewSupplierOffer();
													map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answer.getNewSupplierOffer(), 13, SSTConstant.FILE_TYPE_PDF));//35295 #124 @zaim
												} else if ((answer.getNewSupplierOffer() == null || answer
														.getNewSupplierOffer().isEmpty())
														&& (answer.getAnswerRemark() != null && !answer.getAnswerRemark()
																.isEmpty())) {
													// supplier respond but no
													// changes
													answerRemark = answer.getAnswerRemark();
													map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answer.getAnswerRemark(), 13, SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim
												}
											}
										}
									}
									if (StringUtils.isBlank(answerRemark)) {
										answerRemark = SSTConstant.TB;
										map.put(SSTConstant.answerRemark, answerRemark);
									}
								}
								// end supplier offer
								// start retrieve spec size
								if (getManagedBean().getListSpecAnswer() != null
										&& !getManagedBean().getListSpecAnswer().isEmpty()) {
									map.put(SSTConstant.SPEC_SIZE, String.valueOf(getManagedBean().getListSpecAnswer().size()));
								}
								// end retrieve spec size
							} else {
								map.put(SSTConstant.questionType, SSTConstant.whiteSpace);
								map.put(SSTConstant.clItem, SSTConstant.whiteSpace);
								map.put(SSTConstant.CL_FULFILMENT_TYPE_CODE, SSTConstant.whiteSpace);
								map.put(SSTConstant.uomName, SSTConstant.whiteSpace);
								map.put(SSTConstant.clUomFrequency, SSTConstant.whiteSpace);
								map.put(SSTConstant.clUomPerDay, SSTConstant.whiteSpace);
								map.put(SSTConstant.clUomPerMonth, SSTConstant.whiteSpace);
								map.put(SSTConstant.CL_DURATION_MONTH, SSTConstant.whiteSpace);
								if (zone != null) {
									//36122 - start
									map.put(SSTConstant.zoneName, VmUtil.getInstance().escapeAndWrapText(zone, 11, SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim
									//36122 - end
								}
								map.put(SSTConstant.quantity, SSTConstant.whiteSpace);
								map.put(SSTConstant.SUPPLIER_OFFER, SSTConstant.whiteSpace);
								map.put(SSTConstant.answerRemark, SSTConstant.whiteSpace);
								map.put(SSTConstant.serviceScope, SSTConstant.whiteSpace);
								// retrieve supplier offer start
								if (answerList != null && !answerList.isEmpty()) {
									int indi = 1;
									for (TplSpecAnswer answer : answerList) {
										if ((answer.getTplSpecSuppAnswer() != null && answer.getTplSpecSuppAnswer()
												.getSmSupplier().getSupplierId().equals(currentSupplier))
												&& answer.getIsWip() != null) {
											map.put(SSTConstant.IS_WIP, "true");
											if (answer.getNewSupplierOffer() == null && indi == zoneCount) {
												if (answer.getAnswerRemark() != null && !answer.getAnswerRemark().isEmpty()) {
													// supplier respond but no
													// changes
													map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answer.getAnswerRemark(), 13, SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim
												} else {
													// no changes and no supplier
													// respond
													map.put(SSTConstant.answerRemark, SSTConstant.TB);
												}
												if (indi == scLoiLoaItem.getZoneNameList().size()){
													map.put(SSTConstant.LAST_INDEX_WIP, "true");
												}
											}
										}
										indi++;
									}
								}
								// end supplier offer
							}
							listItem.add(map);
							zoneCount++;
						}
					} else {
						// non zonal
						Map<String, String> map = new HashMap<>();
						map.put(SSTConstant.questionType, question.getQuestionType());
						//36122 - start @zaim
						map.put(SSTConstant.clItem, VmUtil.getInstance().escapeAndWrapText(question.getClItem(), 12, SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim 
						//36122 - end @zaim
						map.put(SSTConstant.CL_FULFILMENT_TYPE_CODE, getParameterDesc(question.getClFulfilmentTypeCode(),SourcingConstants.MY_LOCALE));
						map.put(SSTConstant.uomName, question.getClUom().getUomName());
						map.put(SSTConstant.clUomFrequency, question.getClUomFrequency().toString());
						map.put(SSTConstant.clUomPerDay, question.getClUomPerDay() == null ? SSTConstant.TB : question
								.getClUomPerDay().toString());
						map.put(SSTConstant.clUomPerMonth, question.getClUomPerMonth() == null ? SSTConstant.TB : question
								.getClUomPerMonth().toString());
						map.put(SSTConstant.CL_DURATION_MONTH, question.getClDurationMonth().toString());
						map.put(SSTConstant.quantity, scLoiLoaItem.getQty() == null? SSTConstant.TB : scLoiLoaItem.getQty().toString());
						map.put(SSTConstant.serviceScope, SSTConstant.TB);

					for (TplSpecAnswer answer : answerList) {
						//35596 @zaim start
						if (answer.getTplSpecSuppAnswer() != null
								&& answer.getTplSpecSuppAnswer().getSmSupplier().getSupplierId()
										.equals(currentSupplier)) {
						//35596 @zaim end	
							if ((answer.getNewSupplierOffer() != null && StringUtils.isNotEmpty(answer
									.getNewSupplierOffer())) && scLoiLoaItem.isComplete()) {
								// supplier respond and make new supplier offer
								map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answer.getNewSupplierOffer(), 13,SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim
								break;
							} else if ((answer.getNewSupplierOffer() == null || answer.getNewSupplierOffer().isEmpty())
									&& (answer.getAnswerRemark() != null && !answer.getAnswerRemark().isEmpty())) {
								// supplier respond but no changes
								map.put(SSTConstant.answerRemark, VmUtil.getInstance().escapeAndWrapText(answer.getAnswerRemark(), 13, SSTConstant.FILE_TYPE_PDF)); //35295 #124 @zaim
							} else {
								// no changes and no supplier respond
								map.put(SSTConstant.answerRemark, SSTConstant.TB);
							}
						}
					//35596 @zaim start
					}
					//35596 @zaim end
						// end supplier offer
						// start retrieve spec size
						if (getManagedBean().getListSpecAnswer() != null && !getManagedBean().getListSpecAnswer().isEmpty()) {
							map.put(SSTConstant.SPEC_SIZE, String.valueOf(getManagedBean().getListSpecAnswer().size()));
						}
						listItem.add(map);
					}
				}
				scLoiLoaItem.setTplSpecAnswerLampiranA3(listItem);
			}
		}
		//29006 end
		
	// #25790 - start
	/**
	 * Navigate to RequestNote view page.
	 * 
	 * @param requestNoteId
	 */
	public void navigateRequestNotePage(Long requestNoteId) {
		RequestNoteBackingBean reqNoteBB = new RequestNoteBackingBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		reqNoteBB.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
		reqNoteBB.getManagedBean().setTrackingDairyView(Boolean.TRUE);
		reqNoteBB.viewRequestNote(requestNoteId);
	}
	// #25790 - end

	// 25791 -start-
	/**
	 * set tab activeIndex.
	 */
	private void setTabActiveIndex() {
		if (isQTMaintenanceScreen()) {
			getManagedBean().setSelectedTabIndex(QTConstants.TAB_MAIN_INDEX);
		}
	}
	// 25791 -end-
	
}

