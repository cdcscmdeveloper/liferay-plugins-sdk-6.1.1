package my.ep.web.backingbean.sc.requestnote;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import my.ep.util.SLAUtils; //#25790

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.bean.application.NgepApplicationBean;
import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.ct.IContractSO; //#25790
import my.ep.biz.service.fl.itemsummary.IFlItemSummarySO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO; //#25790
import my.ep.biz.service.fl.order.IFulfilmentOrderSO; //#25790
import my.ep.biz.service.osb.sc.IDPOsbSO; //#25790
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.bpm.ICompositeValiditySO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.audit.IAuditDPRequestNoteSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO; //#25790
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.bpm.vo.WorklistVO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.AuditDPRequestNoteConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.ContinuityDPConst; //#25790
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.ct.ContractReqDTO; //#25790
import my.ep.dto.ct.ContractResDTO; //#25790
import my.ep.dto.fl.itemsummary.FlItemSummaryReqDTO;
import my.ep.dto.fl.FulfilmentReqDTO; //#25790
import my.ep.dto.fl.FulfilmentResDTO; //#25790
import my.ep.dto.osb.sc.DPOsbReqDTO; //#25790
import my.ep.dto.osb.sc.DPOsbResDTO; //#25790
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.pm.general.settings.bpm.CompositeValidityReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO; //#25790
import my.ep.dto.sc.quationtender.AwardSupplierResDTO; //#25790
import my.ep.dto.sc.requestitem.RequestItemReqDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.CtContract; //#25790
import my.ep.persistence.entity.ct.VCtContract; //#25790
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest; //#25790
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScRequestItemAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScPIReportAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRNZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sc.loaloi.ScLoa; //#25790
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa; //#25790
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmDisciplinaryActionRemarks;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.cm.codification.CodificationBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean; //#25790
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean; //#25790
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean; //#25790
import my.ep.web.backingbean.sc.audit.AuditDPRequestNoteBackingBean;
import my.ep.web.backingbean.sc.audit.AuditDPRequestNoteBackingBeanTemplate;
//import my.ep.web.backingbean.sc.directpurchase.DirectPurchaseBackingBean.SearchTrackingDiaryPagingDataModel;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean; //#25790
import my.ep.web.backingbean.sc.loacancelation.LOACancelDetailBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.backingbean.trackingdiary.TrackingDiarySearchBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.directpurchase.MarketSummaryAndPIBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.DPConstants;
import my.ep.web.managedbean.sc.requestnote.ApprovalExceedThresholdManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sc.helper.SourcingZoneHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.backingbean.ct.circular.CircularBackingBean; //#30866

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;

import com.liferay.portal.util.PortalUtil;

// TODO: Auto-generated Javadoc

/**
 * The Class ApprovalExceedThresholdBackingBean.
 * 
 * @author Zairul
 * @since 28.04.2025
 */
@ManagedBean(name = "approvalExceedThresholdBackingBean")
@RequestScoped
public class ApprovalExceedThresholdBackingBean extends SourcingBaseBackingBean<ApprovalExceedThresholdManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	private String APPROVAL_EXCEED_THRESHOLD_PAGE_NAV = "/xhtml/sc/directpurchase/exceedThresholdApproval/approvalExceedThreshold.xhtml";
	
	// 25790 - start
	/**
	 * List of Goverment role code
	 */
	private static final List<String> ALLOWED_ROLES = Arrays.asList(RoleCode.REQUISITIONER.value(),
			RoleCode.FL_APPROVER.value(), RoleCode.RECEIVING_OFFICER.value(), RoleCode.ACKNOWLEDGE_OFFICER.value(),
			RoleCode.PAY_OFFICER.value(), RoleCode.DESK_OFFICER.value());

	// 25790 - end

	private void getDefaultDocumentValues() {
		getManagedBean().setSearchDocumentType(ParameterConst.DOCTYPE_REQUEST_NOTE);
		getManagedBean().setSearchStatusId(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION);
	}

	
	public void preRenderView() {
		debugLogger("preRenderView");
		FacesContext fc = FacesContext.getCurrentInstance();
		// reset search info
		reset(Boolean.FALSE);
		
	 
		
		if(isBPKApprover()){
			getDocumentTypes(Boolean.TRUE, Boolean.FALSE);
			getManagedBean().setGovernment(Boolean.TRUE);
			
			if(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())){
				getManagedBean().setDisableMinistryPtjLov(Boolean.TRUE);
			}
			
			// show default Ministry, Kumpulan PTJ and PTJ
			getDefaultPtjAndMinistryValues();

		} else if(isSupplier()){
			getManagedBean().setGovernment(Boolean.FALSE);
		}
		fc.getApplication()
		.getNavigationHandler()
		.handleNavigation(fc, null, APPROVAL_EXCEED_THRESHOLD_PAGE_NAV);
	}
	
	/**
	 * Checks if is government.
	 *
	 * @return true, if is government
	 */
	private boolean isGovernment(){
		if(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())  // Government 
				|| getFacesScopes().hasRoleUser(RoleCode.RN_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
				|| getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())){ //#28837 - shafie 

			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	
	// start - 25396
	/**
	 *  Get document types based on View As selected.
	 */
	public void getDocTypesByViewAs() {
		getManagedBean().setSearchDocumentType(null);		
		Long paramViewAsId = 0L;
		
		if (getManagedBean().getSearchFunctionType() != null && getManagedBean().getSearchFunctionType() != "") {
			paramViewAsId = Long.parseLong(getManagedBean().getSearchFunctionType());
			PmParameter viewAsTypeCode = ParameterHelper.getInstance().getPmParameterById(paramViewAsId);
			
			if(viewAsTypeCode.getParameterCode().equals(ParameterConst.ORG_TYPE_PTJ)){
				getDocumentTypes(Boolean.TRUE, Boolean.FALSE);
				getManagedBean().setGovernment(Boolean.TRUE);
				getManagedBean().setViewAsPTJ(Boolean.TRUE);
				getManagedBean().setViewAsSupplier(Boolean.FALSE);
				getManagedBean().setDisableMinistryPtjLov(Boolean.TRUE);
				getManagedBean().setShowMinistryPtjField(Boolean.TRUE);
				
				PmTrackingDiaryBackingBean  pmTrackingDiaryBackingBean= new PmTrackingDiaryBackingBean();
				pmTrackingDiaryBackingBean.getManagedBean().setUseViewAsOption(Boolean.TRUE);
				pmTrackingDiaryBackingBean.getManagedBean().setViewAsPTJ(Boolean.TRUE);
				pmTrackingDiaryBackingBean.getManagedBean().setViewAsSupplier(Boolean.FALSE);
				
			}else if(viewAsTypeCode.getParameterCode().equals(ParameterConst.ORG_TYPE_G2G_ROLECODE)){
				getDocumentTypes(Boolean.FALSE, Boolean.FALSE);
				getManagedBean().setGovernment(Boolean.FALSE);
				getManagedBean().setViewAsPTJ(Boolean.FALSE);
				getManagedBean().setViewAsSupplier(Boolean.TRUE);	
				getManagedBean().setShowMinistryPtjField(Boolean.FALSE);
				
				PmTrackingDiaryBackingBean  pmTrackingDiaryBackingBean= new PmTrackingDiaryBackingBean();
				pmTrackingDiaryBackingBean.getManagedBean().setUseViewAsOption(Boolean.TRUE);
				pmTrackingDiaryBackingBean.getManagedBean().setViewAsPTJ(Boolean.FALSE);
				pmTrackingDiaryBackingBean.getManagedBean().setViewAsSupplier(Boolean.TRUE);
			}	
		}
		
	}
	// end - 25396
	
	/**
	 * Checks if is BPK Approver.
	 *
	 * @return true, if is BPK Approver
	 */
	public boolean isBPKApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value());
	}
	
	/**
	 * Gets the document types.
	 *
	 * @param isGovernament the is governament
	 * @return the document types
	 */
	private void getDocumentTypes(boolean isGovernament, boolean isMaintenance) {
		List<SelectItem> items = null;
		List<String> docTypeList = null;
		try {
			items = new ArrayList<SelectItem>();
			docTypeList = new ArrayList<String>();

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));

			PmParameter pmParameter = null;
			if (StringUtils.isNotEmpty(getManagedBean().getSearchFunctionType())) {
				try {
					pmParameter = retrieveParameterById(Long.valueOf(getManagedBean().getSearchFunctionType()));
					getManagedBean().setG2G(false);
					//pmParameter = retrieveParameterByTypeAndCode(parameterType, parameterCode)
				 } catch (NumberFormatException  e) {
					 log.info("-->getSearchFunctionType() is null ");
				}
			}
			
			if (isMaintenance) {
				if (isRnApprover()) {
					docTypeList.add(ParameterConst.DOCTYPE_REQUEST_NOTE);
				}
			}
			
			else if (pmParameter != null && pmParameter.getParameterCode().equals(ParameterConst.ORG_TYPE_G2G_ROLECODE)){
				docTypeList.add(ParameterConst.DOCTYPE_SIMPLE_QUOTE);
				docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_INQUIRY);
				getManagedBean().setG2G(Boolean.TRUE);
				getManagedBean().setGovernment(Boolean.FALSE);
			}
			
			else {
				docTypeList.add(ParameterConst.DOCTYPE_SIMPLE_QUOTE);
				docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_INQUIRY);
				if (isGovernament) {
					docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT);
				}

				// Redmine 20311: remove RN search for Supplier
				if ( isDeskOfficer()) {//#25790
					docTypeList.add(ParameterConst.DOCTYPE_REQUEST_NOTE);
				}

				docTypeList.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			}
			for (String docType : docTypeList) {
				items.add(new SelectItem(docType, ResourceBundleUtils.getMessage(
						getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType), ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale())));
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		getManagedBean().setDocumentTypes(items);
	}

	/**
	 * Search status desc.
	 *
	 * @return the list
	 * @throws NamingException the naming exception
	 */
	public SelectItem[] searchStatusDesc() throws NamingException {
		//Redmine 20311: remove RN search
		if(isSupplier() && getManagedBean().getSearchDocumentType() != null){
			if(getManagedBean().getSearchDocumentType().equals(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
				SelectItem[] items = null;
				FacesScopes face = new FacesScopes();
				List<PmStatusDesc> statusDescsTemp = new ArrayList<PmStatusDesc>();
				
				List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
						.getStatusDescList(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
								getManagedBean().getSearchDocumentType(), null);

				for (PmStatusDesc statusDesc : statusDescs) {
					if(statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_PENDING_SUBMISSION)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_COMPLETED)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_CANCELLED)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_CANCELLED_DUE_TO_PID_VALIDITY_EXPIRED)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_RESPONSE_SUBMITTED)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_PENDING_ITEM_CODE_ASSIGNMENT)){
						
						statusDescsTemp.add(statusDesc);
					}
				}
				
				int i = 1;
				items = new SelectItem[statusDescsTemp.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale()));
				
				for (PmStatusDesc statusDescr : statusDescsTemp) {
					items[i++] = new SelectItem(statusDescr.getPmStatus()
							.getStatusId(), statusDescr.getStatusName());
				}
				return items;
			}else{
				return super.getStatusDropdownValues(
						ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
						getManagedBean().getSearchDocumentType());
			}
		}else{
			//start - 36615
			if(StringUtils.isNotBlank(getManagedBean().getSearchDocumentType())){
				return super.getStatusDropdownValues(
						ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
						getManagedBean().getSearchDocumentType());
			}
				return null;
			//end - 36615
		}
	}

	/**
	 * Search.
	 */
	public void search() {
		if ((StringUtils.isNotBlank(getManagedBean().getRequestNoteNo()) && getManagedBean().getRequestNoteNo().trim()
				.length() < 3)
				|| (StringUtils.isNotBlank(getManagedBean().getRequestNoteTitle()) && getManagedBean()
						.getRequestNoteTitle().trim().length() < 3)) {
			throw new EpException(MessageConst.ERROR_716019);
		}

		getManagedBean().setSearchDocumentType(ParameterConst.DOCTYPE_REQUEST_NOTE);
		getManagedBean().setDocumentTypeDesc(ParameterConst.RN_TITLE);
		getManagedBean().getDpDocumentDataModel().reloadData();
	}

	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {
		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {
			Object[] dpObj = getManagedBean().getDpObj();
			String docType = (String) dpObj[3];
			Long docId = (Long) dpObj[0];
			
			if (docId == null && docType == null) {
				log.info(String.format("DirectPurchase tracking diary search filter empty[%s=%s][%s=%s]", "docType", docType, "docId", docId));
				return new SearchResultBean<PmTrackingDiary>();
			}

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(), docType);
			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(), docId);
			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",SearchOperator.OPERATOR_EQUALS.value(), docType);
			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",SearchOperator.OPERATOR_EQUALS.value(), docId);
			List<Object[]> parents = getParentDocument(docType, docId);
			if(!CollectionUtils.isEmpty(parents)) {
				for(Object[] parent : parents) {
					BaseSearchFilterBean multiFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(), parent[0]), SearchCondition.CONDITION_OR.value(), 
							new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(), parent[1]));
					searchBean.getDataFilterBeans().add(multiFilterBean);
				}
			}

			BaseSearchFilterBean multiFilterBean1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);
			BaseSearchFilterBean multiFilterBean2 = new SearchMultiFilterBean(searchFilterBean3, SearchCondition.CONDITION_OR.value(), searchFilterBean4);
			BaseSearchFilterBean multiFilterBean3 = new SearchMultiFilterBean(multiFilterBean1, SearchCondition.CONDITION_OR.value(), multiFilterBean2);
			searchBean.getDataFilterBeans().add(multiFilterBean3);

			reqDTO.setSearchBean(searchBean);
			
			setSearchBeanDescription(searchBean, searchFilterBean1, searchFilterBean2);
			
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resDTO.getResultBean();
		}
	}

	/**
	 * View doc.
	 *
	 * @param dpObj the dp obj
	 */
	public void viewMaintenance(Object[] dpObj, boolean isCancel) {
		log.info("viewMaintenance");
		long docId = (long) dpObj[0];
		String docNo = (String) dpObj[1];
		String docType = (String) dpObj[3];
		long statusId = (long) dpObj[4];

		getManagedBean().setSearchStatusId(statusId);

		TrackingDiarySearchBackingBean diarySearchBackingBean = new TrackingDiarySearchBackingBean();
		diarySearchBackingBean.getManagedBean().setDocNo(docNo);
		diarySearchBackingBean.getManagedBean().setDocType(docType);
		diarySearchBackingBean.getManagedBean().setStatusId(statusId);

		RequestNoteBackingBean rnBackingBean = new RequestNoteBackingBean();
		rnBackingBean.viewRequestNote(docId);
		rnBackingBean.getManagedBean().setDpObj(dpObj);
		rnBackingBean.getManagedBean().setTrackingDairyView(Boolean.FALSE);
		rnBackingBean.getManagedBean().setCancel(isCancel);

	}
	
	/**
	 * @param prObj
	 */
	@SuppressWarnings("unchecked")
	public void viewDpMaintenance(Object[] prObj) {
		
		//#36119 - start
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		RequestNoteBackingBean rnBackingBean = new RequestNoteBackingBean();
		//#36119 - end
		
		ScPurchaseRequest purchaseRequest =  (ScPurchaseRequest) prObj[2];
		if (null != purchaseRequest) {
			Long purchaseRequestId = purchaseRequest.getPurchaseRequestId();
			Long supplierId = null; //#36119
			System.out.println("Bug 28878 : purchaseRequestId : " + purchaseRequestId);
			//#36729 - Start
			List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
			//#36729 - End
			ScRequestNote requestNote = null;
			if (null != purchaseRequestId && null != getManagedBean().getDpDocumentDataModel()) {
				requestNote = (ScRequestNote) prObj[7];
				//#36119 - start
				supplierId = (Long) prObj[9]; 					
				requestNoteReqDTO.setRequestNoteId(requestNote.getRequestNoteId());
				requestNoteReqDTO.setSupplierId(supplierId);
				RequestNoteResDTO requestNoteResDTO = getRequestNoteSO().getRequestNoteDtlForCodificationBySupplier(
						requestNoteReqDTO);
				rnBackingBean.reloadRequestNoteForCodificationWrapper(requestNoteResDTO);
				//#36119 - end
				
				//#36729 - Start
				List<ScRequestNoteDtl> tempRnDtls = requestNoteResDTO.getRequestNoteDtlList();
				requestNoteDtls = addSelectedRequestNoteDtls(tempRnDtls, purchaseRequestId);
				//#36729 - End					
			}
			
			if (!CollectionUtils.isEmpty(requestNoteDtls)) { 				
				rnBackingBean.getManagedBean().setSelectedPrDtls(prObj);
				rnBackingBean.getManagedBean().setRequestNoteDtls(requestNoteDtls);
				rnBackingBean.getManagedBean().setCancel(true);
				rnBackingBean.getManagedBean().setViewOnly(true);
				rnBackingBean.getManagedBean().setReadOnly(true);
				//28878 - Start
				rnBackingBean.getManagedBean().setPurchaseRequestId(purchaseRequestId);
				//28878 - End
				//#36119 - start
				rnBackingBean.getManagedBean().setSourcingDpNewBpmVer(validateSupportedTask(requestNote.getRequestNoteNo(), SourcingConstants.DP_MAINTENANCE_CANCEL_CODIFY_PR));
				rnBackingBean.getManagedBean().setSupplierId(supplierId);
				//#36119 - end
				if (null != requestNote.getCreatedBy()) {
					rnBackingBean.getManagedBean().setCreatedBy(rnBackingBean.getUserById(requestNote.getCreatedBy()).getUserName());
				}
				SmSupplier smSupplier = new SmSupplier();
				smSupplier.setCompanyName((String) prObj[3]);
				rnBackingBean.getManagedBean().setSmSupplier(smSupplier);
				System.out.println("Bug 28878 : smSupplier.getCompanyName : "  + smSupplier.getCompanyName());
				
				PmStatus requestNoteStatus = new PmStatus();
				requestNoteStatus.setStatusId((Long) prObj[4]);
				rnBackingBean.getManagedBean().setRequestNoteStatus(requestNoteStatus);
				rnBackingBean.getManagedBean().setRequestNote(requestNote);
				rnBackingBean.viewRequestNote();
			}
		}
	}
	
	//start -  #36018
	/**
	 * Loads and retrieves the list of ScRequestNoteDtl associated with a
	 * specific purchase request ID from objList.
	 * 
	 * @param purchaseRequestId
	 *            The ID of the purchase request to match.
	 * @param objList
	 *            The list of objects containing purchase request and associated
	 *            details.
	 * @return The updated list of ScRequestNoteDtl associated with the
	 *         specified purchase request ID.
	 */
	private List<ScRequestNoteDtl> loadScPurchaseRequest(Long purchaseRequestId, List<Object[]> objList) {
		List<ScRequestNoteDtl> requestNoteDtls = null;
		if (!CollectionUtils.isEmpty(objList)) {
			requestNoteDtls = loadScPurchaseRequestDtl(purchaseRequestId, objList);
		}
		return requestNoteDtls;
	}
	
	/**
	 * Loads and retrieves the list of ScRequestNoteDtl associated with a
	 * specific purchase request ID from objList.
	 * 
	 * @param purchaseRequestId
	 *            The ID of the purchase request to match.
	 * @param objList
	 *            The list of objects containing purchase request and associated
	 *            details.
	 * @param requestNoteDtls
	 *            The list of ScRequestNoteDtl to populate with matching
	 *            details.
	 * @return The updated list of ScRequestNoteDtl associated with the
	 *         specified purchase request ID.
	 */
	private List<ScRequestNoteDtl> loadScPurchaseRequestDtl(Long purchaseRequestId, List<Object[]> objList) {
		List<ScRequestNoteDtl> requestNoteDtls = null;
		for (Object[] obj : objList) {
			if (obj.length > 6 && obj[2] instanceof ScPurchaseRequest) {
				ScPurchaseRequest purchaseRequestFromModel = (ScPurchaseRequest) obj[2];
				// Check if the purchase request IDs match
				if (purchaseRequestId.equals(purchaseRequestFromModel.getPurchaseRequestId())
						&& obj[6] instanceof List<?>) {
					@SuppressWarnings("unchecked")
					List<ScRequestNoteDtl> castedList = (List<ScRequestNoteDtl>) obj[6];
					requestNoteDtls = castedList;
					break;
				}
			}
		}
		return requestNoteDtls;
	}
	//end - #36018
	
	public void viewDoc(Object[] dpObj){
		debugLogger("viewDoc");
		long docId = (long) dpObj[0];
		String docNo = (String) dpObj[1];
		String docType = (String) dpObj[3];
		long statusId = (long) dpObj[4];

		TrackingDiarySearchBackingBean diarySearchBackingBean = new TrackingDiarySearchBackingBean();
		diarySearchBackingBean.getManagedBean().setDocNo(docNo);
		diarySearchBackingBean.getManagedBean().setDocType(docType);
		diarySearchBackingBean.getManagedBean().setStatusId(statusId);

		RequestNoteBackingBean rnBackingBean = new RequestNoteBackingBean();
		rnBackingBean.viewBpkApproverRequestNote(docId);
		rnBackingBean.getManagedBean().setDpObj(dpObj);
	}
	
	/**
	 * View doc.
	 *
	 * @param dpObj the dp obj
	 */
	//Redmine 20311: remove RN search
	public void viewDocSupplierSQ(Object[] dpObj){
		debugLogger("viewDocSupplier");
		long docId = (long) dpObj[0];
		String docNo = (String) dpObj[1];
		String docType = (String) dpObj[3];
		long statusId = (long) dpObj[4];

		debugLogger("docId"+docId+" | docNo"+docNo+" | docType"+docType+" | statusId"+statusId);
		
		TrackingDiarySearchBackingBean diarySearchBackingBean = new TrackingDiarySearchBackingBean();
		diarySearchBackingBean.getManagedBean().setDocNo(docNo);
		diarySearchBackingBean.getManagedBean().setDocType(docType);
		diarySearchBackingBean.getManagedBean().setStatusId(statusId);

		SimpleQuoteBackingBean sqBackingBean = new SimpleQuoteBackingBean();
		String docIdStr = Long.toString(docId);
		sqBackingBean.supplierViewSearch(docIdStr);
		sqBackingBean.getManagedBean().setDpObj(dpObj);
		sqBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);
		sqBackingBean.getManagedBean().setSaveButtonFlg(Boolean.FALSE); //#25396
		sqBackingBean.getManagedBean().setSubmitButtonFlg(Boolean.FALSE); //#25396
	}
	
	/**
	 * @param dpObj
	 */
	public void performViewDoc(Object[] dpObj) {
		this.viewDoc(dpObj);
	}
	
	/**
	 * Generate report.
	 *
	 * @param dpObj the dp obj
	 */
	public void generateReport(Object[] dpObj){
		debugLogger("generateReport");
		String docNo = (String) dpObj[1];
		String docType = (String) dpObj[3];
		
		Map<String, String> paramMap = new HashMap<String, String>();
		String reportName = null;
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_REQUEST_NOTE)){
			reportName = ReportConst.DP_REQUEST_NOTE_DTL_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_RN_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_SUMMARY_REQUEST_NOTE_S1_RPT_VAL);
			paramMap.put(ReportConst.PARAM_GROUP, ReportConst.PARAM_STATUS);
			
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_INQUIRY)){
			reportName = ReportConst.DP_ITEM_INQUIRY_DTL_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_PI_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_PURCHASE_ITEM_INQUIRY_FINAL_S1_RPT_VAL);
			paramMap.put(ReportConst.PARAM_GROUP, ReportConst.PARAM_STATUS);
			
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT)){
			reportName = ReportConst.DP_PID_DTL_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_PID_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_S1_PURCHASE_DEFAULT_DTLS_RPT_VAL);
			paramMap.put(ReportConst.PARAM_GROUP, ReportConst.PARAM_PID_GROUP);
			
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
			reportName = ReportConst.DP_SIMPLE_QUOTE_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_QUOTE_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL);
			paramMap.put(ReportConst.PARAM_GROUP, ReportConst.PARAM_QUOTE_GROUP);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_LETTER_OF_AWARD)){
			reportName = ReportConst.DP_LOA_DTL_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_LOA_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_S1_LOA_RPT_VAL);
		}
		paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
		
		if(reportName!=null) {
			try {
				generateReport(reportName, paramMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new EpException("Report is not available for this document.");
		}
	}

	/**
	 * Reset direct purchase search.
	 */
	public void reset(boolean isMaintenance){
		this.getManagedBean().setSearchFunctionType(null);
		this.getManagedBean().setRequestNoteNo(null);
		this.getManagedBean().setSearchStatusId(null);
		this.getManagedBean().setRequestNoteTitle(null);
		this.getManagedBean().resetDocumentDataModel();
	}

	/**
	 * Gets the search tracking diary data model.
	 *
	 * @return the search tracking diary data model
	 */
	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	/**
	 * Sets the search tracking diary data model.
	 *
	 * @param searchTrackingDiaryDataModel the new search tracking diary data model
	 */
	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

	private List<Object[]> getParentDocument(String docType, Long docId) {
		List<Object[]> docParents = new ArrayList<Object[]>();

		SourcingIntegrationReqDTO searchReqDTO = new SourcingIntegrationReqDTO();
		searchReqDTO.setDocType(docType);
		//start - Sonarqube DP Bug Minor Ruzaimah [22/4/2024]
		searchReqDTO.setDocId(docId);
		//stop - Sonarqube DP Bug Minor Ruzaimah [22/4/2024]


		Long groupDocId = docId;
		String groupDocType = docType;

		while(groupDocId != null && groupDocType != null) {
			SourcingIntegrationResDTO resDTO = getSourcingIntegrationSO().retrieveGroupDocByDocIdAndDocType(searchReqDTO);
			groupDocId = resDTO.getGroupDocId();
			groupDocType = resDTO.getGroupDocType();
			if(resDTO != null) {
				if(groupDocId != null && groupDocType != null) {
					docParents.add(new Object[]{groupDocType, groupDocId});
				}
			}
			searchReqDTO.setDocType(groupDocType);
			searchReqDTO.setDocId(groupDocId);
		}

		return docParents;
	}

	/**
	 * Gets the user name.
	 *
	 * @param userId the user id
	 * @return the user name
	 */
	public String getUserName(long userId){
		String userName = "";
		UserReqDTO userReqDTO  = new UserReqDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		userReqDTO.setUserId(userId);
		PmUser pmUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
		if(pmUser != null){
			userName = pmUser.getUserName();
		}
		return userName;
	}

	private ISourcingIntegrationSO getSourcingIntegrationSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
	}
	
	private IRequestNoteSO getRequestNoteSO() {
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
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
	 * Gets the org name.
	 *
	 * @param orgProfileId the org profile id
	 * @return the org name
	 */
	public String getOrgName(Long orgProfileId) {
		String orgName="";

		if (orgProfileId != null) {
			IOrgProfileSO orgProfileSO = null;
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(orgProfileId);
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileDtlByID(orgProfileReqDTO); //#27440
			if(orgProfileResDTO.getPmOrgProfile() != null){
				orgName = orgProfileResDTO.getPmOrgProfile().getOrgName();
			}
			return orgName;
		}
		return null;
	}
	
	/**
	 * Get PTJ Group condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJGroupCondition() {
		debugLogger("getPTJGroupCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		PmOrgProfile searchMinistryProfile = this.getManagedBean().getSearchMinistryProfile();
		if (null != searchMinistryProfile && searchMinistryProfile.getOrgProfileId() != null) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
				for (int i = 0; i < orgLevel; i++) {
					condition.append("parentOrgProfile.");
				}
				condition.append("orgProfileId");
				condition.append(SourcingConstants.LOV_CONDITION_SEPARATOR);
				condition.append(searchMinistryProfile.getOrgProfileId());

			}

		} 
		return condition.toString();
	}

	/**
	 * Get PTJ condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPtjCondition() {
		debugLogger("getPTJCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		PmOrgProfile searchMinistryProfile = this.getManagedBean().getSearchMinistryProfile();
		PmOrgProfile searchPtjGroupProfile = this.getManagedBean().getSearchPtjGroupProfile();
		if (null != searchPtjGroupProfile && searchPtjGroupProfile.getOrgProfileId() != null) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + searchPtjGroupProfile.getOrgProfileId());
		} else if (null != searchMinistryProfile && searchMinistryProfile.getOrgProfileId() != null) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + searchMinistryProfile.getOrgProfileId());
		} 
		return condition.toString();
	}
	
	/**
	 * The method used set parent profile and reset child profile value
	 * 
	 * @param orgProfile
	 * 		the PmOrgProfile instance
	 */
	public void postUpdateOrgProfileListener(BaseEntity orgProfile) {
		debugLogger("postUpdateOrgProfileListener");
		
		if (orgProfile instanceof PmOrgProfile) {
			PmOrgProfile pmOrgProfile = (PmOrgProfile)orgProfile;
			String orgType = pmOrgProfile.getOrgTypeCode().getParameterCode();
			
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				PmOrgProfile ministryOrgProfile = new PmOrgProfile();
				PmParameter orgTypeCode = new PmParameter();
				orgTypeCode.setParameterCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
				ministryOrgProfile.setOrgTypeCode(orgTypeCode);
				getManagedBean().setSearchPtjGroupProfile(ministryOrgProfile);
				postUpdateOrgProfileListener(ministryOrgProfile);
				
				//showRoleType();
				
			} else { 
				if (getManagedBean().getSearchMinistryProfile() == null || 
						getManagedBean().getSearchMinistryProfile().getOrgProfileId() == null) {
					getManagedBean().setSearchMinistryProfile(retrieveMinistryOrgProfile(
							pmOrgProfile.getOrgProfileId()));	
				}
				
				if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
					getManagedBean().setSearchPtjProfile(new PmOrgProfile());
					
				} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
					if (getManagedBean().getSearchPtjGroupProfile() == null || 
							getManagedBean().getSearchPtjGroupProfile().getOrgProfileId() == null) {
						getManagedBean().setSearchPtjGroupProfile(retrieveJabatanOrgProfile(
								pmOrgProfile.getOrgProfileId()));	
					}
				}
				//showRoleType();
			}
				
		}
	}
    
    /**
   	 * The method used to disable the ministry lov
   	 * if login user is or ministry administrator
   	 * or internal auditor.
   	 * 
   	 * @return true, if login user is ministry contract administrator/internal auditor
   	 *         false, if login user is not ministry contract administrator/internal auditor
	 */
    public boolean disableMinistryLov() {
    	return (isReqOrApprover() || isContractAdminOrInternalAudit());
    }
     
  
    /**
     * Checks if is requisitioner or approver.
     *
     * @return true, if is requisitioner or approver
     */
    public boolean isReqOrApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.RN_APPROVER.value()));
	}
   
   
    /**
     * Checks if is contract admin or internal audit.
     *
     * @return true, if is contract admin or internal audit
     */
    public boolean isContractAdminOrInternalAudit() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()));
	}
    
    /**
     * Checks if is search all.
     *
     * @return true, if is search all
     */
    public boolean isSearchAll() {
		return (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()));
	}
    
    /**
	 * Gets the default ptj and ministry search values based on user role.
	 */
	private void getDefaultPtjAndMinistryValues() {

		debugLogger("getDefaultPtjAndMinistryValues");
		PmOrgProfile ministryProfile = new PmOrgProfile();
		PmOrgProfile ptjGroupProfile = new PmOrgProfile();
		PmOrgProfile ptjProfile = new PmOrgProfile();
		
		PmOrgProfile orgProfile = getOrgProfileByLoginUser();
		if (orgProfile != null) {
			String orgType = orgProfile.getOrgTypeCode().getParameterCode();
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				ministryProfile = orgProfile;
			} else if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
				ptjGroupProfile = orgProfile;
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
				ptjProfile = orgProfile;
				ptjGroupProfile = retrieveJabatanOrgProfile(
						orgProfile.getOrgProfileId());
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			}
		}
		
		getManagedBean().setSearchMinistryProfile(ministryProfile);
		getManagedBean().setSearchPtjGroupProfile(ptjGroupProfile);
		getManagedBean().setSearchPtjProfile(ptjProfile);
	}
	
	private PmOrgProfile getOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
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
							pmOrgProfile = pmUserOrg.getPmOrgProfile();
							break;
						}
					}
				}
			} finally {
				userSO = null;
			}	
		}
		
		return pmOrgProfile;
	}
	
	/**
	 * set search bean description for tracking purpose
	 *
	 */
	private void setSearchBeanDescription(ISearchBean<PmTrackingDiary> searchBean, SearchFilterBean searchFilterBean1, SearchFilterBean searchFilterBean2) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[%s=%s]", searchFilterBean1.getSearchField(), searchFilterBean1.getSearchValue()));
		sb.append(String.format("[%s=%s]", searchFilterBean2.getSearchField(), searchFilterBean2.getSearchValue()));
		searchBean.setDescription("searchTrackingDiary" + sb.toString() + "[Caller:DirectPurchaseBackingBean.SearchTrackingDiaryPagingDataModel]");
	}
	
	// 12177 - Start
	/**
	 * 
	 * @param dpObj
	 * @return boolean
	 */
	public boolean checkStatusSqExpired(Object[] dpObj){
		
		Long docId = null;
		String docNo = null;
		Long docStatusId = null;
		String docType = null;
		
		if(dpObj[0] != null){
			docId = (Long) dpObj[0];
		}
		if(dpObj[1] != null){
			docNo = (String) dpObj[1];
		}
		if(dpObj[4] != null){
			docStatusId = (Long) dpObj[4];
		}
		if(dpObj[3] != null){
			docType = (String) dpObj[3];
		}
			
 	    QuoteResDTO quoteResDTO = null;
	    Long currentStatusId = docStatusId;
	    Boolean sqExpired = false;
	    
	    if(docType.equals(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
	    	if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_RN_CREATION)){
	    		IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	    		quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, docNo, docStatusId, docType, false);	
	    		if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
	    			if(!isSqStillValid(quoteResDTO.getEndDate())){
	    				sqExpired = true;
				    }
	    		}
			}								   
	    }else if(docType.equals(ParameterConst.DOCTYPE_REQUEST_NOTE)){
	    	if(currentStatusId.equals(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION)
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION) 
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL)
	    	   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)){
	    		IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	    		quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, docNo, docStatusId, docType, false);	
	    		if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
	    			if(!isSqStillValid(quoteResDTO.getEndDate())){
	    				sqExpired = true;
				    }
	    		}
	    	}  	
	    }else if(docType.equals(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
	    	if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL)
	 		   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)
			   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
			   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)){
	    		IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	    		quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, docNo, docStatusId, docType, false);	
	    		if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
	    			if(!isSqStillValid(quoteResDTO.getEndDate())){
	    				sqExpired = true;
				    }
	    		}
	    	}
	    }else if(docType.equals(ParameterConst.DOCTYPE_LETTER_OF_AWARD)){
	    	if(currentStatusId.equals(QTStatusConst.LOA_PENDING_SUBMISSION)
			   || currentStatusId.equals(QTStatusConst.LOA_PENDING_APPROVAL)
		       || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION)
		       || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)){
	    		IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	    		quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, docNo, docStatusId, docType, false);	
	    		if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
	    			if(!isSqStillValid(quoteResDTO.getEndDate())){
	    				sqExpired = true;
				    }
	    		}
	    	}
	    }
	    
		return sqExpired;
	}
	
	public boolean isSqStillValid(Date sqEndDate){		
		
		Boolean validDate = true;	
			
		// Refactor: #12177 - Start
		Integer sqTimeValidity = NgepApplicationBean.getSqTimeValidity();
		log.info("Refactor 12177 (DirectPurchaseBackingBean) : getSqTimeValidity - " + sqTimeValidity);
		// Refactor: #12177 - End
		
		if(sqEndDate != null){
				Date currentDate = new Date();
				Date closingDate = sqEndDate;
					
				Calendar cal = Calendar.getInstance();
				cal.clear(); 
				cal.setTime(closingDate);
				cal.add(Calendar.DATE, sqTimeValidity); //add 60days to closing/end date
					
				if(currentDate.compareTo(cal.getTime()) >= 0){
						validDate = false;
				}							
			}						
			return validDate;	
	}
	
	/**
	 * Triggered from: directPurchaseSearch.xhtml - dataTable id="scDocumentTableID".
	 * 
	 * Code performance risk: Executed inside loop - records from search result. 
	 * 
	 * Mitigation: 
	 * 1. Make sure logic and service calls is optimized. 
	 * 2. Create new service to execute and return specific value. Fast execution time.
	 * 3. Disable logging
	 * 
	 * @param dpObj
	 * @return String
	 */
	public String getDocStatus(Object[] dpObj){
		
		if(dpObj == null){
			return null;
		}
		
		if(checkStatusPidExpired(dpObj)){
			return getStatusDescById(DPStatusConstants.PID_EXPIRED);
		}else if(checkStatusSqExpired(dpObj)){
			return getStatusDescById(DPStatusConstants.SQ_EXPIRED);
		}
		Long statusId = null;
		String docType = null;
		
		if(dpObj[3] != null){
			docType = (String) dpObj[3];
		}
		if(dpObj[4] != null){
			statusId = (Long) dpObj[4];
		}		
		if(docType.equals("PD")){
			if(statusId.equals(DPStatusConstants.PID_EXPIRED)){
				return getStatusDescById(DPStatusConstants.PID_COMPLETED);
			}
		}
		return getStatusDescById(statusId);
	}
	
	// 12177 - End
	
	//12183 - Start
	/**
	 * 
	 * @param dpObj
	 * @return boolean
	 */
	public boolean checkStatusPidExpired(Object[] dpObj){
		
		String docNo = null;
		Long docStatusId = null;
		String docType = null;
		Date pidChangedDate = null;
		Integer pidValidDays = null;
		
		if(dpObj[1] != null){
			docNo = (String) dpObj[1];
		}
		if(dpObj[4] != null){
			docStatusId = (Long) dpObj[4];
		}
		if(dpObj[3] != null){
			docType = (String) dpObj[3];
		}
		
		ScPurchaseInDefault scPID = null;
		
		Long currentStatusId = docStatusId;
		
		if(docType.equals(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
	    	if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_RN_CREATION)
	    			|| currentStatusId.equals(DPStatusConstants.SQ_PENDING_SUBMISSION) ){
	    		
	    		//scPID = (ScPurchaseInDefault)dpObj[8];
	    		if(dpObj[8]!=null){
	    			scPID = new ScPurchaseInDefault();
		    		scPID.setPurchaseInDefaultId((Long) dpObj[8]);
		    		scPID.setPurchaseInDefaultNo((String) dpObj[9]);
		    		scPID.setPidValidDays((Integer) dpObj[10]);
		    		scPID.setChangedDate((Timestamp) dpObj[11]);
		    				    		
		    		pidChangedDate = scPID.getChangedDate();
		    		pidValidDays = scPID.getPidValidDays();
	    		}
	    		
			}								   
	    }else if(docType.equals(ParameterConst.DOCTYPE_REQUEST_NOTE)){
	    	if(currentStatusId.equals(DPStatusConstants.RN_PENDING_SUBMISSION)
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION) 
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL)
	    	   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)){
	    		
	    		
	    		//scPID = (ScPurchaseInDefault)dpObj[8];
	    		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
	    		RequestNoteResDTO resDTO = new RequestNoteResDTO();
	    		IRequestNoteSO reqSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
	    		
	    		reqDTO.setRequestNoteId((Long)dpObj[0]);
	    		reqDTO.setRequestNoteNo(docNo);
	    		// Refactor #20 - Start.
	    		resDTO = reqSO.getPidDtlsByRnIdAndRnNo(reqDTO);

	    		pidChangedDate = resDTO.getPidChangedDate();
	    		pidValidDays = resDTO.getPidValidDays();
	    		// Refactor #20 - End.
	    		
	    	}  	
	    }else if(docType.equals(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
	    	if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL)
	 		   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)
			   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
			   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)){
	    		
	    		scPID = (ScPurchaseInDefault)dpObj[8];

	    		pidChangedDate = scPID.getChangedDate();
	    		pidValidDays = scPID.getPidValidDays();
	    	}
	    }
		
		return pidValidityBoolean(pidChangedDate, pidValidDays);
	
	}
	
		/**
		 * Pid validity.
		 *
		 * @param purchaseInDefault the purchase in default
		 * @param scRequestNote the sc request note
		 */
		private void pidValidity(ScPurchaseInDefault purchaseInDefault) {
			Date today  = DateUtils.getCurrentDate();
			Date ExpDate = DateUtils.addDays(purchaseInDefault.getChangedDate(), purchaseInDefault.getPidValidDays());
			log.info("pID validity begin");
			if(today.after(ExpDate)){

				throw new EpException(
						MessageConst.ERROR_760355,
						new String[] {purchaseInDefault.getPurchaseInDefaultNo()});
			}  
			log.info("pID validity end");
		}
		
		
		/**
		 * Pid validity.
		 *
		 * @param purchaseInDefault the purchase in default
		 * @param scRequestNote the sc request note
		 */
		private boolean pidValidityBoolean(Date pidChangedDate, Integer pidValidDays) {
			boolean valid = false;
			
			if(pidChangedDate != null && pidValidDays != null){
				Date today  = DateUtils.getCurrentDate();
				Date ExpDate = DateUtils.addDays(pidChangedDate, pidValidDays);
							
				if(today.after(ExpDate)){
					valid = true;
				}
			}
			return valid;
		}
		
		//12183 - end
		
		//#22211 - start -  if loa prepared by user, return true
		public boolean allowCancel(Object[] dpObj) {
			IQTAwardSO qtAwardSO;
			qtAwardSO =  EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			boolean disable = true;
			String docNo = (String) dpObj[1];
			
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoaNo(docNo);
			AwardSupplierResDTO awardSupplierResDTO = qtAwardSO.retrieveLOADtlByLoaNo(awardSupplierReqDTO);
									
			if(String.valueOf(awardSupplierResDTO.getScLoa().getCreatedBy()).equalsIgnoreCase(String.valueOf(getFacesScopes().getUserSessionBean().getUserId()))){
				disable = false;				
			}
			return disable;
		}
		//#22211 - end
		
	//  Redmine 25396: remove duplication option view by adding new drop down field
		
			public SelectItem[] getFunctionType() {
				
				List<String> typeList = new ArrayList<String>();
				typeList.add(ParameterConst.ORG_TYPE_PTJ);
				typeList.add(ParameterConst.ORG_TYPE_G2G_ROLECODE);
				
				ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

				searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),typeList));

				return super.getDropdownValuesBySearchBean(ParameterTypeConst.ORGANISATION_TYPES, searchBean);
				
				
			}
			

			/**
			 * Validate if user is RN Approver
			 * @return boolean
			 */
			public boolean isRnApprover() {
				if (getFacesScopes().hasRoleUser(RoleCode.RN_APPROVER.value())) {		
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
		
			/**
			 * Validate if user is Desk Officer
			 * @return boolean
			 */
			public boolean isDeskOfficer() {
				if (getFacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())) {		
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
			
			/**
			 * Return boolean TRUE for dual role only, else FALSE
			 * @return boolean
			 */
			public boolean showFunctionType() {
				if (hasSupplierRole() && hasGovernmentUserRole()) {
					return true;
				} 
				return false;
			}
			
						
			/**
			 * Return boolean TRUE for dual role and PTJ, else FALSE
			 * @return boolean
			 */
			public boolean showRoleType() {
				if(hasGovernmentUserRole()) {
					return true;
				} else {
					return false;
				}
			}
			
			// end 25396
			
	// #36119 - start
	/**
	 * To validate supported task
	 * 
	 * @param String docNo
	 * @param Long validityId
	 * 
	 * @return boolean
	 */
	public boolean validateSupportedTask(String docNo, Long validityId) {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		ICompositeValiditySO compositeSO = (ICompositeValiditySO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICompositeValiditySO.class);
		CompositeValidityReqDTO cReqDTO = new CompositeValidityReqDTO();
		cReqDTO.setCompositeValidityId(validityId);
		String minVersion = compositeSO.getCompositeByValidityId(cReqDTO).getPmCompositeValidity()
				.getMinCompositeVersion();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setDocNumber(docNo);
		WorklistVO worklist = bpmSO.getTasksByDocNumber(reqDTO).getWorklistVO();
		worklist = worklist == null ? new WorklistVO() : worklist;
		String version = null;
		if (!CollectionUtils.isEmpty(worklist.getWorklistItem())) {
			for (WorklistItemVO i : worklist.getWorklistItem()) {
				if (StringUtils.isNotEmpty(i.getInstanceVersion())) {
					version = i.getInstanceVersion();
					break;
				}
			}
		}
		if (version != null) {
			return CustomUtils.compositeVersionComparator(version, minVersion) >= 0;
		}
		return false;
	}
	// #36199 - end
	// 25790 - start
	/**
	 * Check is it login user is Government User
	 * 
	 * @return true, if user is Government User
	 */
	@Override
	protected boolean hasGovernmentUserRole() {
		for (String role : ALLOWED_ROLES) {
			if (getFacesScopes().hasRoleUser(role)) {
				return true;
			}
		}
		return false;

	}
	// 25790 - end
	
	//36729 - Start
	/**
	 * Compare with selected request note details and add into the request note details selected list
	 * 
	 * @param tempRnDtls
	 *			  Temporary store all the selected request note details for matching.
	 * @param purchaseRequestId
	 *            The ID of the purchase request to match with id in request note details.
	 * @return list of selected requestNoteDtls           
	 */
	public List<ScRequestNoteDtl> addSelectedRequestNoteDtls(List<ScRequestNoteDtl> tempRnDtls, Long purchaseRequestId){
		List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
		for(ScRequestNoteDtl rnDtl : tempRnDtls){
			if(purchaseRequestId.compareTo(rnDtl.getPurchaseRequest().getPurchaseRequestId()) == 0){
				requestNoteDtls.add(rnDtl);
			}
		}
		return requestNoteDtls;
	}
	//36729 - End
}