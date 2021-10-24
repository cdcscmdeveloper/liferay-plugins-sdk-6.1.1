/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
// TODO: Auto-generated Javadoc
package my.ep.web.backingbean.sc.purchaseindefault;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal; //#28865
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang.StringUtils; //#28865

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse; //#28865

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO; //#28865
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO; //#28865
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.BaseEntity; //#28865
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.circular.cc.CtCircularItem;
import my.ep.persistence.entity.ct.circular.dtl.CtCircularPidReason;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.ct.dtl.CtPidReason;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScPIDApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPIDWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefaultAttachment;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sc.purchaseindefault.PurchaseInDefaultManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

/**
 * The Class PurchaseInDefaultBackingBean.
 * 
 * @author sri since 01.15.2014
 */
@ManagedBean(name = "purchaseInDefaultBackingBean")
@RequestScoped
public class PurchaseInDefaultBackingBean extends
		SourcingBaseBackingBean<PurchaseInDefaultManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger
			.getLogger(PurchaseInDefaultBackingBean.class);

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/** The text before sign. */
	private String textBeforeSign;

	/** The purchase in default page. */
	private String PURCHASE_IN_DEFAULT_PAGE = "purchaseInDefaultMain";
	
	// start - #28865
	private String PURCHASE_IN_DEFAULT_RESPONSE_PAGE = "purchaseInDefaultResponseMain";
	
	private String PURCHASE_IN_DEFAULT_REQUISITIONER_RESPONSE_PAGE = "/xhtml/sc/directpurchase/purchaseindefaultresponse/reqPurchaseInDefaultResponseMain.xhtml";
	// end - #28865

	/**
	 * Instantiates a new purchase in default backing bean.
	 */
	public PurchaseInDefaultBackingBean() {
		super();
		if (MDC.get(LogKey.USER_ID) == null) {
			PmUser user = new FacesScopes().getUserSessionBean();
			if (user != null) {
				Long userName = user.getUserId();
				new LogKey(userName);
				if (log.isDebugEnabled()) {
					log.debug("userName: " + userName);
				}
			}
		}
	}

	/**
	 * add panel or contract item to purchase in default
	 * 
	 * @param vCmSupplierItem
	 *            the v cm supplier item
	 * @return the string
	 */
	public String pidAction(VCmSupplierItem vCmSupplierItem) {
		debugLogger("pidAction");
		boolean isContractTypeAsMinistry = Boolean.FALSE;
		ISearchBean<VCmSupplierItem> viewSearchBean = null;
		IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IPurchaseInDefaultSO.class);
	/*	IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						IContractItemSO.class);*/
		ScPurchaseInDefault scPurchaseInDefault = new ScPurchaseInDefault();
		getManagedBean().setPurchaseInDefault(scPurchaseInDefault);
		// check contract item or panel item
		if (vCmSupplierItem.getContractNo() != null) {
			// contract item
			PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
			purchaseInDefaultReqDTO.setContractItemId(vCmSupplierItem.getDocId());
			PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.getContractItemByContractItemId(purchaseInDefaultReqDTO);
			CtContractItem contractItem = resDTO.getCtContractItem();
			CtCircularItem circularItem = resDTO.getCtCircularItem();
			
			
			// approved by
			getManagedBean().setApprovedBy(resDTO.getApprovedBy());
			
			if (contractItem != null) {
				
				// contract type as ministry
				if(contractItem.getCtContractVer().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY)){
					 isContractTypeAsMinistry = Boolean.TRUE;
				}
				
				//validation for non codify contract item
				if(contractItem.getScRequestItem() == null ||
						(contractItem.getScRequestItem() != null && 
						contractItem.getScRequestItem().getCmItem() == null)) {
					this.getManagedBean().setCodified(false);
					Locale userLocale = new FacesScopes().getUserLocale();
					String message = MessageUtils.getMessage(MessageConst.ERROR_760405,
							MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale);

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									message));
				}
				scPurchaseInDefault.setContractedItem(contractItem);
				retriveContractSupplierGstInfomation(scPurchaseInDefault
						.getContractedItem());
				getManagedBean().setCheckContractItem(Boolean.TRUE);
				if(circularItem != null) {
					scPurchaseInDefault.setCircularItem(circularItem);
					getManagedBean().setCheckCentralContractItem(true);
				}
			}
			//populatePidReason(resDTO.getCtPidReasonList(), resDTO.getCircularPidReasonList());
//			retrieveReasonDescListByCategory(Boolean.TRUE);
			
		} else if (vCmSupplierItem.getCircularNo() != null) {
			// panel item
			PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
			FacesScopes facesScope = new FacesScopes();
			Long userId = facesScope.getUserSessionBean().getUserId();
			purchaseInDefaultReqDTO.setPanelItemId(vCmSupplierItem.getDocId());
			purchaseInDefaultReqDTO.setFetchSubEntity(true);
			PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO
					.getPanelSupplierByPanelItem(purchaseInDefaultReqDTO);
			CtPanelItem ctPanelItem = resDTO.getCtPanelItem();
			
			// approved by
			getManagedBean().setApprovedBy(resDTO.getApprovedBy());
			
			if (ctPanelItem != null) {
				//validation for non codify contract item
				if(ctPanelItem.getScRequestItem() == null ||
						(ctPanelItem.getScRequestItem() != null && 
								ctPanelItem.getScRequestItem().getCmItem() == null)) {
					this.getManagedBean().setCodified(false);
					Locale userLocale = new FacesScopes().getUserLocale();
					String message = MessageUtils.getMessage(MessageConst.ERROR_760405,
							MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale);

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									message));
				}
				// check panel item zone or not
				if (ctPanelItem.getCtPanelZones() != null
						&& ctPanelItem.getCtPanelZones().size() > 0) {
					List<CtPanelSupplier> ctPanelSuppliers = uniquePanelZoneSuppliers(ctPanelItem);
					ctPanelItem.setCtPanelSuppliers(ctPanelSuppliers);
					scPurchaseInDefault.setPanelItem(ctPanelItem);
				} else {
					scPurchaseInDefault.setPanelItem(ctPanelItem);
				}

				getManagedBean().setCheckContractItem(Boolean.FALSE);
				purchaseInDefaultReqDTO.setPanelItem(ctPanelItem);
				purchaseInDefaultReqDTO.setUserId(userId);
//				PurchaseInDefaultResDTO defaultResDTO = purchaseInDefaultSO
//						.getIsExceedMaxProcurementAmount(purchaseInDefaultReqDTO);
//				retrieveReasonDescListByCategory(defaultResDTO
//						.isExceedProcurementAmt());
				/*List<CtCircularPidReason> pidReason = new ArrayList<CtCircularPidReason>();
				if(ctPanelItem.getCtCircularVer() != null) {
					pidReason = ctPanelItem.getCtCircularVer().getCtCircularPidReason();
				}
				populatePidReason(null, pidReason);*/
			}
		}
	
		// WorkFlow Status
		ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus();
		workFlowStatus.setPmStatus(getPmStatusByStatusId(DPStatusConstants.PID_PENDING_SUBMISSION));
		workFlowStatus.setPurchaseInDefault(scPurchaseInDefault);
		// scPurchaseInDefault.setCurrentStatus(workFlowStatus);
		getCurrentStatus(workFlowStatus.getPmStatus());

		// initializing remarks
		scPurchaseInDefault.setRemarks(initializeRemarks(scPurchaseInDefault,
				null, scPurchaseInDefault.getRemarks(), ScPIDRemark.class));

		// to get ptj
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(user.getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			if (resDTO.getUserOrgList() != null
					&& resDTO.getUserOrgList().size() > 0) {
				scPurchaseInDefault.setPmOrgProfile(resDTO.getUserOrgList()
						.get(0).getPmOrgProfile());
			}
		}
		String extensionCode = vCmSupplierItem.getItemCode();
		Long uomId = vCmSupplierItem.getUomId();
		StringBuilder sb = new StringBuilder();
		sb.append("ITEM_CODE^").append(extensionCode).append("|")// item Code
				// .append("catType^").append("XC").append("|")
				.append("UOM_ID^").append(uomId);
		viewSearchBean = new CatalogueBackingBean().getManagedBean()
				.getSearchDataModel().getSearchBean();
		List<BaseSearchFilterBean> filterBeans = viewSearchBean
				.getDataFilterBeans();

		for (BaseSearchFilterBean baseFilterBean : filterBeans) {
			if (baseFilterBean instanceof SearchFilterBean) {
				SearchFilterBean filterBean = (SearchFilterBean) baseFilterBean;
				sb.append("|").append(filterBean.getSearchField()).append("^");
				if (filterBean.getSearchOperator() == SearchOperator.OPERATOR_IN
						.value()) {
					sb.append(
							filterBean.getSearchValue().toString()
									.replaceAll("\\[", "")
									.replaceAll("\\]", "")
									.replaceAll("\\s", "")).append("^");
				} else {
					sb.append(filterBean.getSearchValue()).append("^");
				}

				sb.append(filterBean.getSearchOperator());
			}
		}
		getManagedBean().setConditionField(sb.toString());
		getManagedBean().setvCmSupplierItem(vCmSupplierItem);

		getManagedBean().setSourceMethods(
				getSourceMethodDropdownCodeValues(null, false));
		initializeApproverGroup();
		
		scPurchaseInDefault.setCreatedBy(user.getUserId());
		
		// by default days
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(ParameterConst.PURCHASE_IN_DEFAULT);
		List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList();
		if (ruleValueList != null && ruleValueList.size() > 0) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			scPurchaseInDefault.setPidValidDays(Integer.valueOf(configRuleValue.getRuleValue()));
		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
		}
		
		getManagedBean().setContractTypeAsMinistry(isContractTypeAsMinistry);
		
		return PURCHASE_IN_DEFAULT_PAGE;
	}

	/**
	 * Unique panel zone suppliers.
	 * 
	 * @param ctPanelItem
	 *            the ct panel item
	 * @return the list
	 */
	private List<CtPanelSupplier> uniquePanelZoneSuppliers(
			CtPanelItem ctPanelItem) {
		List<CtPanelSupplier> uniquePanelSuppliers = new ArrayList<>();
		Map<Long, CtPanelSupplier> uniqueCtPanelSuppliers = new HashMap<Long, CtPanelSupplier>();
		if (ctPanelItem.getCtPanelZones() != null) {
			for (CtPanelZone ctPanelZone : ctPanelItem.getCtPanelZones()) {
				if (ctPanelZone.getCtPanelSuppliers() != null
						&& ctPanelZone.getCtPanelSuppliers().size() > 0) {
					for (CtPanelSupplier ctPanelSupplier : ctPanelZone
							.getCtPanelSuppliers()) {
						uniqueCtPanelSuppliers.put(
								ctPanelSupplier.getPanelSupplierId(),
								ctPanelSupplier);
					}
				}
			}
			uniquePanelSuppliers = new ArrayList<CtPanelSupplier>(
					uniqueCtPanelSuppliers.values());
		}
		return uniquePanelSuppliers;
	}
	
	//start #28865
	public void dialogMsgCheck(){
		if(getManagedBean().getPmReason() != null){
			if (getManagedBean().getPmReason().getReasonId().intValue() == reasonAllowSupplierResponse().getReasonId().intValue())
				RequestContext.getCurrentInstance().execute("submitSuppDlg.show()");
			else
				submitPurchaseInDefault(null); //#9133
		}
		else{
			submitPurchaseInDefault(null); //#9133
		}
	}
	//end #28865

	/**
	 * submit PurchaseInDefault. submit - save data into database and BPM
	 * process will route to the next respective user. page navigate to
	 * acknowledge page.
	 */
	public String submitPurchaseInDefault(String outcome) { //#9133 - add parameter
		debugLogger("submitPurchaseInDefault");
		IPurchaseInDefaultSO purchaseInDefaultSO = null;
		FacesScopes facesScopes = new FacesScopes();
		try {
			purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION,
							IPurchaseInDefaultSO.class);
			PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
			ScPurchaseInDefault scPurchaseInDefault = getManagedBean()
					.getPurchaseInDefault();
			//#9133 - start
			PurchaseInDefaultResDTO resDTO = new PurchaseInDefaultResDTO(); 
			Boolean saveAsTaskList = false;
			
			if(outcome == "")
				outcome = null;
			//#9133 - end
			
			if (!isPidGenerated() || (outcome == null && !isPidGenerated())) { //#9133 start - add if statement	
				saveAsTaskList = true; //#9133
				scPurchaseInDefault.setRecordStatus(Boolean.TRUE);
				scPurchaseInDefault.setCreatedDate(DateUtils.getCurrentDate());
				
				// Target Approver
				formApprover(scPurchaseInDefault);
				
				// WorkFlow Status
				scPurchaseInDefault.setWorkFlowStatuses(null);
	
				boolean isQuantityCheckNeeded = false;
				
				// justification
				/*if (scPurchaseInDefault.getReasonType().getReasonCode() != null) {
					for (PmReasonDesc reasonDesc : getManagedBean().getPidJustifications()) {
						PmReason pmReason = reasonDesc.getPmReason();
						if (scPurchaseInDefault
								.getReasonType()
								.getReasonCode()
								.equalsIgnoreCase(
										pmReason.getReasonCode())) {
							scPurchaseInDefault.setReasonType(pmReason);
						}
					}
				}*/
				
				if(getManagedBean().isVisibleOrderQty()) {
					isQuantityCheckNeeded = true;
				}
				
				// validate pid
				validatePID(scPurchaseInDefault);
				int checkQty = 0;
				
				CtContractItem contractItem = scPurchaseInDefault.getContractedItem();
				CtPanelItem ctPanelItem = scPurchaseInDefault.getPanelItem();  
				
				if (contractItem != null) {
					if (contractItem.getCtContractVer()
							.getCtContract().getContractType() != null) {
						if (contractItem
								.getCtContractVer().getCtContract()
								.getContractType().getParameterCode()
								.equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							reqDTO.setContractType(SourcingConstants.PID_TYPE_MINISTRY);
						} else if (contractItem
								.getCtContractVer().getCtContract()
								.getContractType().getParameterCode()
								.equals(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
							reqDTO.setContractType(SourcingConstants.PID_TYPE_CENTRAL);
						}
					}
					if (scPurchaseInDefault.getQuantity() != null) {
						double minimumQuantity = contractItem
								.getMinimumOrderQty();
						if(scPurchaseInDefault.getCircularItem() != null) {
							minimumQuantity = scPurchaseInDefault.getCircularItem().
									getMinimumOrderQty().doubleValue();
						}
						checkQty = Double.compare(scPurchaseInDefault.getQuantity()
								.doubleValue(), minimumQuantity);
					}
	
					/*
					 * if(scPurchaseInDefault.getReasonType().getReasonCode().
					 * equalsIgnoreCase(ParameterConst.PID_EQ)){
					 * if(scPurchaseInDefault
					 * .getContractedItem().getMinimumOrderQty() >
					 * scPurchaseInDefault.getQuantity().doubleValue()){ throw new
					 * EpException(MessageConst.ERROR_100033, new String[]
					 * {ResourceBundleUtils.getMessage("lbl.sc.order.qty",
					 * ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					 * facesScopes.getUserLocale()), ResourceBundleUtils.getMessage(
					 * "lbl.sc.purchase.in.default.min.qty",
					 * ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					 * facesScopes.getUserLocale())}); } }
					 */
				} else if (ctPanelItem != null) {
					reqDTO.setContractType(SourcingConstants.PID_TYPE_PANEL);
	
					if (scPurchaseInDefault.getQuantity() != null) {
						checkQty = Double.compare(scPurchaseInDefault.getQuantity()
								.doubleValue(), ctPanelItem
								.getMinimumOrderQty());
					}
					/*
					 * if(scPurchaseInDefault.getReasonType().getReasonCode().
					 * equalsIgnoreCase(ParameterConst.PID_EQ)){ if(checkQty < 0) {
					 * throw new EpException(MessageConst.ERROR_100033, new String[]
					 * {ResourceBundleUtils.getMessage("lbl.sc.order.qty",
					 * ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					 * facesScopes.getUserLocale()), ResourceBundleUtils.getMessage(
					 * "lbl.sc.purchase.in.default.min.qty",
					 * ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					 * facesScopes.getUserLocale())}); } }
					 */
				}
				
				if (isQuantityCheckNeeded) {
					if (checkQty >= 0) {
						throw new EpException(
								MessageConst.ERROR_760203,
								new String[] {
										ResourceBundleUtils
												.getMessage(
														"lbl.sc.order.qty",
														ResourceBundleUtils.BUNDLE_COMMON_LABELS,
														facesScopes
																.getUserLocale()),
										ResourceBundleUtils
												.getMessage(
														"lbl.sc.purchase.in.default.min.qty",
														ResourceBundleUtils.BUNDLE_COMMON_LABELS,
														facesScopes
																.getUserLocale()) });
					}
				}
				
				//start - #28865
				if(getManagedBean().getPmReason().getReasonId().intValue() == reasonAllowSupplierResponse().getReasonId().intValue())//justification
				{
					if(scPurchaseInDefault.getQuantity() != null)
						scPurchaseInDefault.setReqQuantity(scPurchaseInDefault.getQuantity());
						
					reqDTO.setSupRes(true);
					reqDTO.setSmSupplier(getManagedBean().getPurchaseInDefault().getContractedItem().getCtContractVer().getCtContract().getSmSupplier());
				}
				//end - #28865
				
				reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
				// Hoa: add token authenticate to support BPM
				reqDTO.setPmUser(facesScopes.getUserSessionBean());
				reqDTO.setRoleCode(getUserRoleCode());
				reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
				reqDTO.setBackupApproverList(getManagedBean().getBackupApprovers());
				reqDTO.setUserLocale(facesScopes.getUserLocale());
				// reqDTO.setConditionField(getManagedBean().getConditionField());
				
				//#9133 - start
				if (outcome != null) {
					reqDTO.setSubmit(Boolean.FALSE);
				} else {
					reqDTO.setSubmit(Boolean.TRUE);
				}
				

				resDTO = purchaseInDefaultSO
						.submitPurchaseInDefault(reqDTO);

				/*PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO
						.submitPurchaseInDefault(reqDTO);*/
				//#9133 - end						
			} //#9133 end - add if statement
			//#9133 - start - else
			else{
				// Target Approver
				formApprover(scPurchaseInDefault);
				
				// WorkFlow Status
				scPurchaseInDefault.setWorkFlowStatuses(null);
	
				boolean isQuantityCheckNeeded = false;
				
				if(getManagedBean().isVisibleOrderQty()) {
					isQuantityCheckNeeded = true;
				}
				
				// validate pid
				if (outcome != null) {
					validateRemark();
				}
				
				validatePID(scPurchaseInDefault);
				int checkQty = 0;
				
				CtContractItem contractItem = scPurchaseInDefault.getContractedItem();
				CtPanelItem ctPanelItem = scPurchaseInDefault.getPanelItem();  
				
				if (contractItem != null) {
					if (contractItem.getCtContractVer()
							.getCtContract().getContractType() != null) {
						if (contractItem
								.getCtContractVer().getCtContract()
								.getContractType().getParameterCode()
								.equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							reqDTO.setContractType(SourcingConstants.PID_TYPE_MINISTRY);
						} else if (contractItem
								.getCtContractVer().getCtContract()
								.getContractType().getParameterCode()
								.equals(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
							reqDTO.setContractType(SourcingConstants.PID_TYPE_CENTRAL);
						}
					}
					if (scPurchaseInDefault.getQuantity() != null) {
						double minimumQuantity = contractItem
								.getMinimumOrderQty();
						if(scPurchaseInDefault.getCircularItem() != null) {
							minimumQuantity = scPurchaseInDefault.getCircularItem().
									getMinimumOrderQty().doubleValue();
						}
						checkQty = Double.compare(scPurchaseInDefault.getQuantity()
								.doubleValue(), minimumQuantity);
					}
				} else if (ctPanelItem != null) {
					reqDTO.setContractType(SourcingConstants.PID_TYPE_PANEL);
	
					if (scPurchaseInDefault.getQuantity() != null) {
						checkQty = Double.compare(scPurchaseInDefault.getQuantity()
								.doubleValue(), ctPanelItem
								.getMinimumOrderQty());
					}
				}
				
				if (isQuantityCheckNeeded) {
					if (checkQty >= 0) {
						throw new EpException(
								MessageConst.ERROR_760203,
								new String[] {
										ResourceBundleUtils
												.getMessage(
														"lbl.sc.order.qty",
														ResourceBundleUtils.BUNDLE_COMMON_LABELS,
														facesScopes
																.getUserLocale()),
										ResourceBundleUtils
												.getMessage(
														"lbl.sc.purchase.in.default.min.qty",
														ResourceBundleUtils.BUNDLE_COMMON_LABELS,
														facesScopes
																.getUserLocale()) });
					}
				}
				
				if(getManagedBean().getPmReason().getReasonId().intValue() == reasonAllowSupplierResponse().getReasonId().intValue())//justification
				{
					if(scPurchaseInDefault.getQuantity() != null)
						scPurchaseInDefault.setReqQuantity(scPurchaseInDefault.getQuantity());
						
					reqDTO.setSupRes(true);
					reqDTO.setSmSupplier(getManagedBean().getPurchaseInDefault().getContractedItem().getCtContractVer().getCtContract().getSmSupplier());
				}
				
				reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
				reqDTO.setPmUser(facesScopes.getUserSessionBean());
				reqDTO.setRoleCode(getUserRoleCode());
				reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
				reqDTO.setBackupApproverList(getManagedBean().getBackupApprovers());
				reqDTO.setUserLocale(facesScopes.getUserLocale());
				reqDTO.setTaskId(getManagedBean().getTaskId());
				reqDTO.setStatusId(getManagedBean().getPidStatus().getStatusId());
				
				if (outcome != null) {
					resDTO = purchaseInDefaultSO.savePurchaseInDefault(reqDTO);
				    reqDTO.setPurchaseInDefaultId(scPurchaseInDefault.getPurchaseInDefaultId());
					reqDTO.setFetchSubEntity(true);
					PurchaseInDefaultResDTO pidResDTO = purchaseInDefaultSO
							.getPurchaseInDefault(reqDTO);				
					getManagedBean().setPurchaseInDefault(pidResDTO.getPurchaseInDefaultDtl());
					
					ScPurchaseInDefault scPID = getManagedBean()
							.getPurchaseInDefault();
					
					scPID.setRemarks(initializeRemarks(scPID,
							null,
							scPID.getRemarks(), ScPIDRemark.class));
					
					justificationTypeEvent(scPID.getReasonType(), scPID.getReasonDesc());
				} else {
					reqDTO.setSubmit(Boolean.TRUE);
					reqDTO.setUpdateBpmTask(Boolean.TRUE);
					resDTO = purchaseInDefaultSO.submitPurchaseInDefault(reqDTO);
				}
		
			}
			//#9133 - end - else
			
			if (resDTO.isSuccessful()) {
				ScPurchaseInDefault purchaseInDefault = resDTO
						.getPurchaseInDefaultDtl();
				//start - #28865
				if(getManagedBean().getPmReason().getReasonId().intValue() == reasonAllowSupplierResponse().getReasonId().intValue()){
					updateWorkflowStatus(purchaseInDefault, Boolean.TRUE);
					
					//#9133 - start
					if(BpmOutcomeConst.BPM_DRAFT.equalsIgnoreCase(outcome)){
						if(saveAsTaskList){
							successMessage(MessageConst.INFO_700106,
									ParameterTypeConst.DOCUMENT_TYPES,
									ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
									purchaseInDefault.getPurchaseInDefaultNo(), Boolean.TRUE);
						}				
						else{
							successMessage(MessageConst.INFO_700004,
									ParameterTypeConst.DOCUMENT_TYPES,
									ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
									purchaseInDefault.getPurchaseInDefaultNo(), Boolean.FALSE);
						}
					}
					else{
						successMessage(MessageConst.INFO_700096,
								ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
								purchaseInDefault.getPurchaseInDefaultNo(), Boolean.TRUE);
					}
					//#9133 - end
				}
				else{
					//#9133 - start
					if(BpmOutcomeConst.BPM_DRAFT.equalsIgnoreCase(outcome)){
						if(saveAsTaskList){
							successMessage(MessageConst.INFO_700106,
									ParameterTypeConst.DOCUMENT_TYPES,
									ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
									purchaseInDefault.getPurchaseInDefaultNo(), Boolean.TRUE);
						}				
						else{
							successMessage(MessageConst.INFO_700004,
									ParameterTypeConst.DOCUMENT_TYPES,
									ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
									purchaseInDefault.getPurchaseInDefaultNo(), Boolean.FALSE);
						}
					}
					else{
					successMessage(MessageConst.INFO_700006,
							ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
							purchaseInDefault.getPurchaseInDefaultNo(), Boolean.TRUE);
					}
					//#9133 - end	
				}
				//end - #28865
				// clear pid data
				if(!BpmOutcomeConst.BPM_DRAFT.equalsIgnoreCase(outcome)) //#9133 add condition
					super.renewSesionObject();

			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			purchaseInDefaultSO = null;
		}
		return null;
	}
	
	/**
	 * Validate pid.
	 *
	 * @param scPurchaseInDefault the sc purchase in default
	 */
	private void validatePID(ScPurchaseInDefault scPurchaseInDefault){
		FacesScopes facesScopes = new FacesScopes();
		if(null == scPurchaseInDefault.getReasonType()){
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.purchase.in.default.just",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		}
		
		// validate quantity
		if (scPurchaseInDefault.getQuantity() != null
				&& scPurchaseInDefault.getQuantity().intValue() < 1) {
			throw new EpException(MessageConst.ERROR_100033, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.order.qty",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "0" });
		}

	}


	/**
	 * Update data into database and BPM process will route to the next
	 * respective user
	 * 
	 * @param outcome
	 *            the outcome
	 * @param digiSign
	 *            the digi sign navigate to acknowledge page.
	 * @return PurchaseInDefaultResDTO
	 */
	private PurchaseInDefaultResDTO updateBPMTask(String outcome,
			PmDigiSign digiSign) {
		PurchaseInDefaultResDTO resDTO = null;
		IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IPurchaseInDefaultSO.class);
		try {
			PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
			ScPurchaseInDefault scPurchaseInDefault = getManagedBean()
					.getPurchaseInDefault();
			// remarks
			//#9133 - comment out
			/*scPurchaseInDefault.setRemarks(initializeRemarks(
					scPurchaseInDefault,
					scPurchaseInDefault.getCurrentStatus(),
					scPurchaseInDefault.getRemarks(), ScPIDRemark.class));*/

			Long stsId = getManagedBean().getPidStatus().getStatusId(); //#28865
			
			if (!BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)
					&& !BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome) //#9133
					&& !BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) //#9133
					&& !DPStatusConstants.PID_PENDING_REVISION.equals(stsId) //#9133
					&& !DPStatusConstants.PID_PENDING_SUPPLIER_RESPONSE.equals(stsId)
					&& !DPStatusConstants.PID_PENDING_VERIFICATION_SUPPLIER_RESPONSE.equals(stsId)) {// #28865
				finalizeRemarks(scPurchaseInDefault.getRemarks());
			}

			if (null != digiSign) {
				updatePmDigiSignData(digiSign,
						ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
						scPurchaseInDefault.getPurchaseInDefaultId(),
						scPurchaseInDefault.getPurchaseInDefaultNo());
			}
			reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setOutcome(outcome);
			reqDTO.setDigiSignData(digiSign);
			reqDTO.setUserLocale(facesScopes.getUserLocale());
			
			//#28865 start	
			if (DPStatusConstants.PID_PENDING_SUPPLIER_RESPONSE.equals(stsId)) {
				boolean validateRemark = true;
				boolean isQuantityCheckNeeded = false;
				
				if(getManagedBean().isVisibleOrderQty()) {
					isQuantityCheckNeeded = true;
				}

				validateResponsePID(scPurchaseInDefault, validateRemark, isQuantityCheckNeeded, outcome);
				BigDecimal tempQty = scPurchaseInDefault.getQuantity();

				//---start----set remark supplier
				saveSupplierRemark();			
				//---end----set remark supplier
				savePidReason();				
				
				reqDTO.setContractId(getManagedBean().getContractId());
				reqDTO.setStatusId(stsId);
				reqDTO.setContractType(getPIDContractType (scPurchaseInDefault));
				reqDTO.setSmSupplier(scPurchaseInDefault.getContractedItem().getCtContractVer().getCtContract().getSmSupplier());
				reqDTO.setPurchaseInDefaultNo(scPurchaseInDefault.getPurchaseInDefaultNo());					
				reqDTO.setPurchaseInDefaultId(scPurchaseInDefault.getPurchaseInDefaultId());
				reqDTO.setFetchSubEntity(true);
				PurchaseInDefaultResDTO pidResDTO = purchaseInDefaultSO
						.getPurchaseInDefault(reqDTO);				
				
				ScPurchaseInDefault scPID = pidResDTO.getPurchaseInDefaultDtl();

				scPID.setQuantity(tempQty); //reset to previous value
				scPID.setSuppQuantity(getManagedBean().getQtyBySupplier());
				
				reqDTO.setPurchaseInDefaultDtl(scPID);
				purchaseInDefaultSO.savePurchaseInDefault(reqDTO);
	
				reqDTO.setSupRes(true);
				resDTO = purchaseInDefaultSO.updateResponsePurchaseInDefault(reqDTO);
			}else if(DPStatusConstants.PID_PENDING_VERIFICATION_SUPPLIER_RESPONSE.equals(stsId)){						
				boolean validateRemark = false; //#31603 - set to false
				boolean isQuantityCheckNeeded = false;
				
				if (getManagedBean().isVisibleOrderQty()) {
					isQuantityCheckNeeded = true;
				}

				validateRequisitionerResponsePID(scPurchaseInDefault, validateRemark, isQuantityCheckNeeded, outcome);

				getCurrentStatus(getPmStatusByStatusId(getManagedBean().getCurrentStatusId()));				
				saveRemark(scPurchaseInDefault);
				
				//reqDTO.setPurchaseInDefaultNo(scPurchaseInDefault.getPurchaseInDefaultNo());
				reqDTO.setContractId(getManagedBean().getContractId());
				reqDTO.setStatusId(stsId);
				reqDTO.setPurchaseInDefaultId(scPurchaseInDefault.getPurchaseInDefaultId());
				reqDTO.setFetchSubEntity(true);
				PurchaseInDefaultResDTO pidResDTO = purchaseInDefaultSO
						.getPurchaseInDefault(reqDTO);
				
				ScPurchaseInDefault scPID = pidResDTO.getPurchaseInDefaultDtl();
				
				if (scPID.getContractedItem() != null) {
					retriveContractSupplierGstInfomation(scPID.getContractedItem());
					getManagedBean().setCheckContractItem(Boolean.TRUE);
				}
				
				if(scPID.getCircularItem() != null) {
					getManagedBean().setCheckCentralContractItem(true);    
				}
				// panel item
				if (scPID.getPanelItem() != null) {
					getManagedBean().setCheckContractItem(Boolean.FALSE);
				}
			
				scPID.setTitle(getManagedBean().getPurchaseInDefault().getTitle());
				scPID.setJustification(getManagedBean().getPurchaseInDefault().getJustification());
				scPID.setReasonType(getManagedBean().getPmReason());
				scPID.setSourceMethod(getManagedBean().getPurchaseInDefault().getSourceMethod());
				scPID.setReqQuantity(getManagedBean().getPurchaseInDefault().getReqQuantity());
				
				if (getManagedBean().getPmReason().getReasonId().intValue() == reasonAllowSupplierResponse()
						.getReasonId().intValue()) {
					BigDecimal qtyByRequisitioner = getManagedBean().getPurchaseInDefault().getReqQuantity();
					BigDecimal qtyBySupplier = getManagedBean().getPurchaseInDefault().getSuppQuantity();

					if (qtyByRequisitioner.compareTo(qtyBySupplier) == -1)
						scPID.setQuantity(getManagedBean().getPurchaseInDefault().getSuppQuantity());
					else
						scPID.setQuantity(getManagedBean().getPurchaseInDefault().getReqQuantity());
				} else {
					scPID.setQuantity(getManagedBean().getPurchaseInDefault().getReqQuantity());
				}
				
				scPID.getContractedItem();
				scPID.getPanelItem();  
				scPID.setScPurchaseInDefaultAttachment(scPurchaseInDefault.getScPurchaseInDefaultAttachment());

				reqDTO.setPurchaseInDefaultDtl(scPID);
				purchaseInDefaultSO.savePurchaseInDefault(reqDTO);	
								
				//assign approver
				formApprover(scPurchaseInDefault);
				reqDTO.setBackupApproverList(getManagedBean().getBackupApprovers());
				
				reqDTO.setSupRes(true);
				reqDTO.setPayLoad(getManagedBean().getPayLoad()); //#9133
				resDTO = purchaseInDefaultSO.updateResponsePurchaseInDefault(reqDTO);
			}else{
				//#9133 -start
				if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)
						|| BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)
						|| BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome))
					validateRemark();
				else
					finalizeRemarks(scPurchaseInDefault.getRemarks());
					
				//saveRemark(scPurchaseInDefault); comment out this //#9133
				purchaseInDefaultSO.savePurchaseInDefault(reqDTO);
			    reqDTO.setPurchaseInDefaultId(scPurchaseInDefault.getPurchaseInDefaultId());
				reqDTO.setFetchSubEntity(true);
				PurchaseInDefaultResDTO pidResDTO = purchaseInDefaultSO
						.getPurchaseInDefault(reqDTO);
				
				reqDTO.setPurchaseInDefaultDtl(pidResDTO.getPurchaseInDefaultDtl());
				reqDTO.setStatusId(stsId);
				reqDTO.setPayLoad(getManagedBean().getPayLoad()); 
				//#9133 -end
				
				resDTO = purchaseInDefaultSO.updatePurchaseInDefault(reqDTO);
			}
			//#28865 end
			if (resDTO.isSuccessful()) {
					//#28865 start
				if (DPStatusConstants.PID_PENDING_SUPPLIER_RESPONSE.equals(stsId)) {

					successMessage(MessageConst.INFO_700097, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault()
									.getPurchaseInDefaultNo(), Boolean.TRUE);

				} else if (DPStatusConstants.PID_PENDING_VERIFICATION_SUPPLIER_RESPONSE.equals(stsId)) {
					if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)) {
						successMessage(MessageConst.INFO_700006, ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					} else if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
						successMessage(MessageConst.INFO_700010, ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					}
				}
				//#9133 - start
				else if (DPStatusConstants.PID_PENDING_SUBMISSION.equals(stsId)) {
					if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
						successMessage(MessageConst.INFO_700010, ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					}
				} else if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
					Long statusId = getManagedBean().getPidStatus().getStatusId();

					if (DPStatusConstants.PID_PENDING_VERIFICATION.equals(statusId)
							|| DPStatusConstants.PID_PENDING_APPROVAL.equals(statusId)
							|| DPStatusConstants.PID_PENDING_REVISION_VERIFICATION.equals(statusId)
							|| DPStatusConstants.PID_PENDING_REVISION_APPROVAL.equals(statusId)) {
						successMessage(MessageConst.INFO_700009, ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					}
				}else if (DPStatusConstants.PID_PENDING_REVISION.equals(stsId)) {
					if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
						successMessage(MessageConst.INFO_700010, ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					}else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)) {
						successMessage(MessageConst.INFO_700006, ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					}
					
				} 
				//#9133 - end				
					else if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome) //#28865 end
						|| BpmOutcomeConst.BPM_VERIFY.equalsIgnoreCase(outcome)) {
					Long statusId = getManagedBean().getPidStatus()
							.getStatusId();

					if (DPStatusConstants.PID_PENDING_VERIFICATION.equals(statusId)
							|| DPStatusConstants.PID_PENDING_REVISION_VERIFICATION.equals(statusId)) { //#9133 add new condition
						successMessage(MessageConst.INFO_700062,
								ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
								getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					} else if (DPStatusConstants.PID_PENDING_APPROVAL.equals(statusId)
							|| DPStatusConstants.PID_PENDING_REVISION_APPROVAL.equals(statusId)) { //#9133 add new condition
						successMessage(MessageConst.INFO_700065,
								ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
								getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					}
				} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
					successMessage(MessageConst.INFO_700008,
							ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
							getManagedBean().getPurchaseInDefault()
									.getPurchaseInDefaultNo(), Boolean.TRUE);
				}				
				super.renewSesionObject(); //#28865

			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}

		} finally {
			purchaseInDefaultSO = null;
		}

		return resDTO;
	}
	// 20190417@asyrafhaziq_12095 - Start 
	/**
	 * Before approve purchase to check reject 
	 * to return pop up message
	 * 
	 * @param outcome
	 *            
	 */
	public void beforeApproverAction(String outcome) {
		debugLogger("approverAction");
		
		if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
				RequestContext.getCurrentInstance().execute("rejectPID.show()");
		} else {
			approverAction(outcome);
		}
	}
	// 20190417@asyrafhaziq_12095 - End 	
	
	/**
	 * Approve purchase in default Update data into database and BPM process
	 * will route to the next respective user
	 * 
	 * @param outcome
	 *            the outcome navigate to acknowledge page.
	 * @return PurchaseInDefaultResDTO
	 */
	public void approverAction(String outcome) {
		debugLogger("approverAction");
		this.getManagedBean().setAction(outcome);
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			//BPK_PCA
			if (isDigiSigningRequired(RoleCode.BPK_PQ_APPROVER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			//BPK_CCA
			} else if (isDigiSigningRequired(RoleCode.BPK_CQ_APPROVER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			//MIN_CONTRACT_ADMIN
			} else if (isDigiSigningRequired(RoleCode.CT_APPROVER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
		} else {
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			updateBPMTask(outcome, null);
		}
	}
	
	/**
	 * Form approver.
	 *
	 * @param purchaseInDefault the purchase in default
	 */
	private void formApprover(ScPurchaseInDefault purchaseInDefault){
		Long selectedApproverId = getManagedBean().getSelectedApproverId();
		if(selectedApproverId != null) {
			List<PmUser> backupApprovers = new ArrayList<PmUser>();
			if(getManagedBean().getApproverList() != null) {
				List<PmUser> approverList = getManagedBean().getApproverList();

				for (PmUser user : approverList) {
					if(selectedApproverId.equals(user.getUserId())){
						ScPIDApprovalUser approver = new ScPIDApprovalUser();
						approver.setPmUser(user);
						approver.setPurchaseInDefault(purchaseInDefault);
						purchaseInDefault.setApprover(user);
					} else{
						backupApprovers.add(user);
					}
				}
			}
			getManagedBean().setBackupApprovers(backupApprovers);
		}  else{
			purchaseInDefault.setApprover(null);
		}
	}

	/**
	 * To get current status to display for Purchase In Default.
	 * 
	 * @param pmStatus
	 *            the pm status
	 */
	public void getCurrentStatus(PmStatus pmStatus) {
		getManagedBean().setPidStatus(pmStatus);
	}

	

	/**
	 * Justification type event.
	 *
	 * @param pmReason the pm reason
	 * @param otherDesc the other desc
	 */
	public void justificationTypeEvent(PmReason pmReason, String otherDesc) {
		log.info("justificationTypeEvent");
		ScPurchaseInDefault purchaseInDefault = getManagedBean().getPurchaseInDefault();
		IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IReasonSO.class);

		PmParameter reasonCategory = getParameterByTypeAndCode(ParameterTypeConst.REASON_CATEGORY, ReasonCategory.PID_JUSTIFICATION
				.value());

		if(reasonCategory != null) {
			// call PM API to check output for reason
			ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
			reasonReqDTO.setReasonCategoryId(reasonCategory.getParameterId());
			reasonReqDTO.setReasonCode(pmReason.getReasonCode());
			ReasonResDTO reasonResDTO = reasonSO.retrieveReasonByReasonCode(reasonReqDTO);
			pmReason = reasonResDTO.getPmReason();
			if(pmReason != null) {
				PmParameter conditionField = pmReason.getConditionField();
				PmParameter sourceMethod = pmReason.getSourceMethod();

				log.info(conditionField);
				if(conditionField != null) {
					log.info(conditionField.getParameterCode());
				}
				if(conditionField!= null && conditionField.getParameterCode().equals(ParameterConst.PID_FIELD_MINIMUM_QUANTITY)) {
					getManagedBean().setVisibleOrderQty(Boolean.TRUE);
				} else {
					getManagedBean().setVisibleOrderQty(Boolean.FALSE);
				}

				if(sourceMethod == null) {
					getManagedBean().setSourceMethods(
							getSourceMethodDropdownCodeValues(null, true));
				} else {
					getManagedBean().setSourceMethods(
							getSourceMethodDropdownCodeValues(sourceMethod.getParameterCode(), true));
				}
			}

			purchaseInDefault.setReasonType(pmReason);
			purchaseInDefault.setJustification(getReasonDescByReasonId(pmReason.getReasonId()).getReasonDesc());
		}
		purchaseInDefault.setReasonDesc(otherDesc);
		getManagedBean().setPmReason(pmReason); //#28865
		getManagedBean().setCtReason(null);
		getManagedBean().setPanelReason(null);
		// update order qty
		RequestContext.getCurrentInstance().update("reqDtlId");
	}

	/**
	 * handleFileUpload event for file upload in pid.
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
		FacesScopes facesScopes = new FacesScopes();
		IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION,
							IPurchaseInDefaultSO.class);
			
		if (event.getFile() != null) {
			ScPurchaseInDefault purchaseInDefault = getManagedBean()
					.getPurchaseInDefault();
			ScPurchaseInDefaultAttachment attachment = getManagedBean()
					.getPurchaseInDefault().getScPurchaseInDefaultAttachment();
			
			if(purchaseInDefault.getPurchaseInDefaultNo() == null){
				purchaseInDefault.setPurchaseInDefaultNo(purchaseInDefaultSO.generatePIDNo());
			}
			
			if (null == attachment) {
				attachment = new ScPurchaseInDefaultAttachment();
				attachment.setScPurchaseInDefault(purchaseInDefault);
			}

			attachment.setDocNo(purchaseInDefault.getPurchaseInDefaultNo());
			attachment
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			attachment.setCreatedUser(facesScopes.getUserSessionBean());

			uploadAttachment(event.getFile(), attachment);
			purchaseInDefault.setScPurchaseInDefaultAttachment(attachment);
		}
	}
	
	//start - #28865
	public void deleteUploadFile(BaseEntity delEntity) {
		debugLogger("deleteUploadFile");
		
		try{
		log.debug("deleteUploadFile");
		ScPurchaseInDefault purchaseInDefault = getManagedBean()
				.getPurchaseInDefault();
		IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IPurchaseInDefaultSO.class);
		
		if(purchaseInDefault.getScPurchaseInDefaultAttachment() != null){
			purchaseInDefaultSO.deleteAttachmentById(purchaseInDefault.getScPurchaseInDefaultAttachment().getAttachmentId());
		}
		
		purchaseInDefault.setScPurchaseInDefaultAttachment(null);
		getManagedBean().getPurchaseInDefault().setScPurchaseInDefaultAttachment(null);
		}finally{
		
		}

	}	
	//end - #28865

	/**
	 * show the attached icon based on attached type file
	 * 
	 * @param attachment
	 *            the attachment
	 * @return the string
	 */
	public String determineIcon(ScPurchaseInDefaultAttachment attachment) {
		debugLogger("determineIcon");
		String imageFileName = "icon_file_default.png";
		String ext = "";

		if (attachment != null) {
			String fileName = attachment.getFileName();

			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}

			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

	/**
	 * justification type drop down list for to select justification
	 * 
	 * @param isExceedProcAmt
	 *            the is exceed procurement amt
	 */
	public void retrieveReasonDescListByCategory(boolean isExceedProcAmt) {
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonCategory(ReasonCategory.PID_JUSTIFICATION.value());
		reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
		IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IReasonSO.class);
		ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);

		if (resDTO.getReasonDescList() != null
				&& resDTO.getReasonDescList().size() > 0) {
			List<PmReasonDesc> results = new ArrayList<PmReasonDesc>();
			if (getManagedBean().getPurchaseInDefault() != null
					&& getManagedBean().getPurchaseInDefault().getPanelItem() != null) {
				for (PmReasonDesc pmReasonDesc : resDTO.getReasonDescList()) {
					 
 					if (!pmReasonDesc.getPmReason().getReasonCode()
							.equals(ParameterConst.PID_CP) && !pmReasonDesc.getPmReason().getReasonCode()
							.equals(ParameterConst.PID_QR)) {
						results.add(pmReasonDesc);
					}
				}
			} else {
				results = resDTO.getReasonDescList();
			}

			if (isExceedProcAmt) {
				getManagedBean().setJustifications(
						getDropdownCodeValues(results));
			} else {
				List<PmReasonDesc> pmReasonDescs = new ArrayList<PmReasonDesc>();
				for (PmReasonDesc pmReasonDesc : results) {
					if (pmReasonDesc.getPmReason().getReasonCode()
							.equals(ParameterConst.PID_OT)) {
						pmReasonDescs.add(pmReasonDesc);
					}

					if (pmReasonDesc.getPmReason().getReasonCode()
							.equals(ParameterConst.PID_QR)) {
						pmReasonDescs.add(pmReasonDesc);
					}
				}
				if (pmReasonDescs != null && pmReasonDescs.size() > 0) {
					getManagedBean().setJustifications(
							getDropdownCodeValues(pmReasonDescs));
				}
			}
		}
	}
	
	/**
	 * justification type drop down list for to select justification
	 * 
	 * @param isExceedProcAmt
	 *            the is exceed procurement amt
	 */
	public void populatePidReason(List<CtPidReason> ctPidReasonList, List<CtCircularPidReason> circularReasonList) {
		List<PmReasonDesc> results = new ArrayList<PmReasonDesc>();
		if (CollectionUtils.isNotEmpty(ctPidReasonList)) {
			for(CtPidReason pidReason : ctPidReasonList) {
				PmReasonDesc reasonDesc = getReasonDescByReasonId(pidReason.getPmReason().getReasonId());
				if(reasonDesc.getPmReason().isRecordStatus()){
					results.add(reasonDesc);
				}
			}
			
		} else if(CollectionUtils.isNotEmpty(circularReasonList)){
			for(CtCircularPidReason pidReason : circularReasonList) {
				PmReasonDesc reasonDesc = getReasonDescByReasonId(pidReason.getPmReason().getReasonId());
				if(reasonDesc.getPmReason().isRecordStatus()){
					results.add(reasonDesc);
				}
			}
		}
		getManagedBean().setPidJustifications(results);
	}

	/**
	 * Gets the dropdown code values.
	 * 
	 * @param list
	 *            the list
	 * @return the dropdown code values
	 */
	private SelectItem[] getDropdownCodeValues(List<PmReasonDesc> list) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmReasonDesc reasonDesc : list) {
				items[i++] = new SelectItem(reasonDesc.getPmReason()
						.getReasonCode(), reasonDesc.getReasonDesc());
			}
		} finally {
		}
		return items;
	}
	
	/**
	 * Gets the Source method dropdown code values.
	 * 
	 * @param list
	 *            the list
	 * @return the dropdown code values
	 */
	private SelectItem[] getSourceMethodDropdownCodeValues(String sourceMethod, boolean isMethodSelected) {
		SelectItem[] sourceMethodDropDown = null;
		try {
			if(isMethodSelected) {
				ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
				if(sourceMethod == null) {
					if(!getManagedBean().isCodified()) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("pmParameter.parameterCode",
										SearchOperator.OPERATOR_NOT_EQUALS.value(),
										ParameterConst.PID_SOURCE_METHOD_RN));
					}

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmParameter.parameterCode",
									SearchOperator.OPERATOR_NOT_EQUALS.value(),
									ParameterConst.PID_SOURCE_METHOD_CR));

					sourceMethodDropDown = getDropdownCodeValuesBySearchBean(ParameterTypeConst.SOURCING_METHOD, searchBean);
				} else {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmParameter.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									sourceMethod));

					sourceMethodDropDown = getDropdownCodeValuesBySearchBean(ParameterTypeConst.SOURCING_METHOD, searchBean);
				}
			} else {
				sourceMethodDropDown = new SelectItem[1];
				sourceMethodDropDown[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
			}
			
			
		} finally {
		}
		return sourceMethodDropDown;
	}
	
	/**
	 * Method get approver group and approver List.
	 */
	public void initializeApproverGroup() {
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		RequestNoteResDTO resDTO = new RequestNoteResDTO();
		try {
			ScPurchaseInDefault purchaseInDefault = getManagedBean().getPurchaseInDefault();
			if(purchaseInDefault != null && purchaseInDefault.getPmOrgProfile() != null){
				Long orgProfileId = purchaseInDefault.getPmOrgProfile().getOrgProfileId();
				reqDTO.setOrgProfileId(orgProfileId);
				resDTO = getRequestNoteSO().retrieveGroupListByOrgId(reqDTO);
				if(resDTO.getUserGroupList().size()>0) {
					getManagedBean().setUserGroupList(resDTO.getUserGroupList());
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_745070, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
				if(purchaseInDefault.getPmUserGroup() != null){
					getManagedBean().setUserGroupId(purchaseInDefault.getPmUserGroup().getUserGroupId());
				}
				if(purchaseInDefault.getApprover() != null){
					getManagedBean().setSelectedApproverId(purchaseInDefault.getApprover().getUserId());
				}
				userGroupChangeListener();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
		}
	}

	/**
	 * Pre render page navigation to purchase in default based on task id
	 * 
	 * @return void
	 */
	public void preRenderView() {
		SCDirectPurchaseDataType directPurchaseDataType = super.getBpmTaskDetails(SCDirectPurchaseDataType.class, null);
		
		// Purchase In Default
		IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IPurchaseInDefaultSO.class);
		PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
		Long pidID = Long.valueOf(directPurchaseDataType.getDpId());
		purchaseInDefaultReqDTO.setPurchaseInDefaultId(pidID);
		purchaseInDefaultReqDTO.setFetchSubEntity(true);
		PurchaseInDefaultResDTO purchaseInDefaultResDTO = purchaseInDefaultSO
				.getPurchaseInDefault(purchaseInDefaultReqDTO);
		ScPurchaseInDefault purchaseInDefault = purchaseInDefaultResDTO
				.getPurchaseInDefaultDtl();
		
		// approved by
		getManagedBean().setApprovedBy(purchaseInDefaultResDTO.getApprovedBy());
		
		// contract item
		if (purchaseInDefault.getContractedItem() != null) {
			/*
			 * CtContractItem contractItem =
			 * purchaseInDefault.getContractedItem(); IContractItemSO
			 * contractItemSO =
			 * (IContractItemSO)BusinessDelegate.getInstance
			 * ().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
			 * IContractItemSO.class); ContractItemReqDTO contractItemReqDTO
			 * = new ContractItemReqDTO();
			 * contractItemReqDTO.setContractItemId
			 * (contractItem.getContractItemId()); ContractItemResDTO
			 * ConIresDTO =
			 * contractItemSO.getContractItem(contractItemReqDTO);
			 * contractItem = ConIresDTO.getCtContractItem();
			 * purchaseInDefault.setContractedItem(contractItem);
			 */
			retriveContractSupplierGstInfomation(purchaseInDefault.getContractedItem());
			getManagedBean().setCheckContractItem(Boolean.TRUE);
			//#9133 - start
			if(purchaseInDefault.getContractedItem().getCtContractVer().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY)){
				 
				 getManagedBean().setContractTypeAsMinistry(Boolean.TRUE);
			}
			//#9133 - end
		}
		
		if(purchaseInDefault.getCircularItem() != null) {
			getManagedBean().setCheckCentralContractItem(true);    
		}
		// panel item
		if (purchaseInDefault.getPanelItem() != null) {
			getManagedBean().setCheckContractItem(Boolean.FALSE);
		}

		getManagedBean().setPurchaseInDefault(purchaseInDefault);

		getCurrentStatus(getPmStatusByStatusId(getManagedBean().getCurrentStatusId()));

		// remarks
		
		//#9133 -start
		if (getManagedBean().getTaskId() != null && DPStatusConstants.PID_PENDING_SUBMISSION
						.equals(getManagedBean().getPidStatus().getStatusId())) {
			purchaseInDefault.setRemarks(initializeRemarks(purchaseInDefault,
					null,
					purchaseInDefault.getRemarks(), ScPIDRemark.class));
			getManagedBean().setPayLoad(directPurchaseDataType);
		}else{
			purchaseInDefault.setRemarks(initializeRemarks(purchaseInDefault,
					purchaseInDefault.getCurrentStatus(),
					purchaseInDefault.getRemarks(), ScPIDRemark.class));
		}	
		//#9133 - end

		initializeApproverGroup();
		/*populatePidReason(purchaseInDefaultResDTO.getCtPidReasonList(), 
				purchaseInDefaultResDTO.getCircularPidReasonList());*/
		justificationTypeEvent(purchaseInDefault.getReasonType(), purchaseInDefault.getReasonDesc());
		// title checker
		getManagedBean().setTitleChecker(Boolean.FALSE);
		FacesContext fc = FacesContext.getCurrentInstance();
		
		// start - #28865
		String PID_PAGE = PURCHASE_IN_DEFAULT_PAGE;
		
		PortletRequest request = (PortletRequest) fc.getExternalContext().getRequest();
		RenderResponse response = (RenderResponse) request.getAttribute("javax.portlet.response");
		response.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pid.review.default.authorization.title",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		
		Long statusId = getManagedBean().getPidStatus().getStatusId(); // #9133
		
		if (DPStatusConstants.PID_PENDING_REVISION.equals(statusId)
				|| DPStatusConstants.PID_PENDING_SUBMISSION.equals(statusId)) {
			response.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		}
		
		if (directPurchaseDataType.getRecommend() != null && directPurchaseDataType.isIsSuppResponse() != null
				&& !DPStatusConstants.PID_PENDING_REVISION.equals(statusId)) { // #9133  - add another condition
			if (directPurchaseDataType.getRecommend().equals(BpmOutcomeConst.BPM_REJECT)
					|| directPurchaseDataType.getRecommend().equals(BpmOutcomeConst.BPM_ACCEPT)) {
				
				getManagedBean().setAction(directPurchaseDataType.getRecommend());

				if (purchaseInDefault.getContractedItem().getCtContractVer().getCtContract().getContractType()
						.getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
					getManagedBean().setContractTypeAsMinistry(Boolean.TRUE);
					getManagedBean().setContractId(Long.valueOf(directPurchaseDataType.getContractId()));
				}

				if (purchaseInDefault.getReasonType() != null) {
					if (directPurchaseDataType.isIsSuppResponse()) {
						
						//Long statusId = getManagedBean().getPidStatus().getStatusId();

						if (DPStatusConstants.PID_PENDING_VERIFICATION_SUPPLIER_RESPONSE.equals(statusId)) {
							response.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
						}
						
						if (directPurchaseDataType.getRecommend().equals(BpmOutcomeConst.BPM_REJECT)) {
							String message = MessageConst.INFO_700098;

							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
											.getMessage(
													message,
													null,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
		

						} else if (directPurchaseDataType.getRecommend().equals(BpmOutcomeConst.BPM_ACCEPT)) {
							if (purchaseInDefault.getSuppQuantity() != null
									&& purchaseInDefault.getReqQuantity() != null) {

								if (purchaseInDefault.getSuppQuantity().compareTo(purchaseInDefault.getReqQuantity()) != 0) {
									String message = MessageConst.INFO_700099;

									FacesContext.getCurrentInstance().addMessage(
											null,
											new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
													message, null, MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
								}
							}
						}
						PID_PAGE = PURCHASE_IN_DEFAULT_REQUISITIONER_RESPONSE_PAGE;
					}
				}
			}
		}
		//start - #9133
		else if (DPStatusConstants.PID_PENDING_REVISION.equals(statusId)
				&& purchaseInDefault.getSuppQuantity() != null) {
			response.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			getManagedBean().setAction(BpmOutcomeConst.BPM_REVISE);

			if (purchaseInDefault.getContractedItem().getCtContractVer().getCtContract().getContractType()
					.getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				getManagedBean().setContractTypeAsMinistry(Boolean.TRUE);
				getManagedBean().setContractId(Long.valueOf(directPurchaseDataType.getContractId()));
			}

			if (purchaseInDefault.getReasonType() != null) {
				if (purchaseInDefault.getSuppQuantity() != null) {					
					if (directPurchaseDataType.getRecommend().equals(BpmOutcomeConst.BPM_REJECT)) {
						String message = MessageConst.INFO_700098;

						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
										.getMessage(
												message,
												null,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
	

					} else if (directPurchaseDataType.getRecommend().equals(BpmOutcomeConst.BPM_ACCEPT)) {
						if (purchaseInDefault.getSuppQuantity() != null
								&& purchaseInDefault.getReqQuantity() != null) {

							if (purchaseInDefault.getSuppQuantity().compareTo(purchaseInDefault.getReqQuantity()) != 0) {
								String message = MessageConst.INFO_700099;

								FacesContext.getCurrentInstance().addMessage(
										null,
										new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
												message, null, MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
							}
						}
					}

					PID_PAGE = PURCHASE_IN_DEFAULT_REQUISITIONER_RESPONSE_PAGE;
				}
			}
		}
		//end - #9133
		
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, PID_PAGE);
		// end - #28865
	}
	
	// start - #28865
	public void preRenderResponseView(){
		SCDirectPurchaseDataType directPurchaseDataType = super.getBpmTaskDetails(SCDirectPurchaseDataType.class, null);
		
		// Purchase In Default
		IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IPurchaseInDefaultSO.class);
		FacesScopes facesScopes = new FacesScopes();
		PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
		Long pidID = Long.valueOf(directPurchaseDataType.getDpId());
		purchaseInDefaultReqDTO.setPurchaseInDefaultId(pidID);
		purchaseInDefaultReqDTO.setFetchSubEntity(true);
		PurchaseInDefaultResDTO purchaseInDefaultResDTO = purchaseInDefaultSO
				.getPurchaseInDefault(purchaseInDefaultReqDTO);
		ScPurchaseInDefault purchaseInDefault = purchaseInDefaultResDTO
				.getPurchaseInDefaultDtl();
		
		getManagedBean().setContractId(Long.valueOf(directPurchaseDataType.getContractId()));
		
		// approved by
		getManagedBean().setApprovedBy(purchaseInDefaultResDTO.getApprovedBy());

		if (purchaseInDefault.getSuppQuantity() == null) {
			getManagedBean().setQtyBySupplier(purchaseInDefault.getReqQuantity());  //set default to req qty
			getManagedBean().setQtyByRequisitioner(purchaseInDefault.getReqQuantity());
		}
		else if (purchaseInDefault.getSuppQuantity() != null) {
			getManagedBean().setQtyBySupplier(purchaseInDefault.getSuppQuantity());
			getManagedBean().setQtyByRequisitioner(purchaseInDefault.getReqQuantity());
		}
		if (getManagedBean().getQtyBySupplier() == null) {
			purchaseInDefault.setSuppQuantity(getManagedBean().getQtyBySupplier());
		}
		
		
		// contract item
		if (purchaseInDefault.getContractedItem() != null) {
			/*
			 * CtContractItem contractItem =
			 * purchaseInDefault.getContractedItem(); IContractItemSO
			 * contractItemSO =
			 * (IContractItemSO)BusinessDelegate.getInstance
			 * ().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
			 * IContractItemSO.class); ContractItemReqDTO contractItemReqDTO
			 * = new ContractItemReqDTO();
			 * contractItemReqDTO.setContractItemId
			 * (contractItem.getContractItemId()); ContractItemResDTO
			 * ConIresDTO =
			 * contractItemSO.getContractItem(contractItemReqDTO);
			 * contractItem = ConIresDTO.getCtContractItem();
			 * purchaseInDefault.setContractedItem(contractItem);
			 */
			retriveContractSupplierGstInfomation(purchaseInDefault.getContractedItem());
			getManagedBean().setCheckContractItem(Boolean.TRUE);
		}
		
		if(purchaseInDefault.getCircularItem() != null) {
			getManagedBean().setCheckCentralContractItem(true);    
		}
		// panel item
		if (purchaseInDefault.getPanelItem() != null) {
			getManagedBean().setCheckContractItem(Boolean.FALSE);
		}
		getManagedBean().setPurchaseInDefault(purchaseInDefault);

		getCurrentStatus(getPmStatusByStatusId(getManagedBean().getCurrentStatusId()));

		// remarks
		purchaseInDefault.setRemarks(initializeRemarks(purchaseInDefault,
				null,
				purchaseInDefault.getRemarks(), ScPIDRemark.class));
		
		List<ScPIDRemark> remarks = purchaseInDefault.getRemarks();
		List<ScPIDRemark> finalScPIDRemarks = new ArrayList<>();
		List<ScPIDRemark> supScPIDRemarks = new ArrayList<>();
		
		//remove empty remark
		if (CollectionUtils.isNotEmpty(remarks)) {
			for (ScPIDRemark remark : remarks) {
				if(remark.getCreatedBy().equals(facesScopes.getUserSessionBean().getUserId()))
				{
					supScPIDRemarks.add(remark);
				}
				if(!StringUtils.isEmpty(remark.getRemark())){
					finalScPIDRemarks.add(remark);
				}
			}
		}
		
		purchaseInDefault.setRemarks(finalScPIDRemarks);
		
		if(!supScPIDRemarks.isEmpty()){
			getManagedBean().setSuppRemarks(supScPIDRemarks);
			getManagedBean().setSuppRemarkId(supScPIDRemarks.get(0).getRemarkId()); 
		}
			
		initializeApproverGroup();
		/*populatePidReason(purchaseInDefaultResDTO.getCtPidReasonList(), 
				purchaseInDefaultResDTO.getCircularPidReasonList());*/
		justificationTypeEvent(purchaseInDefault.getReasonType(), purchaseInDefault.getReasonDesc());
		// title checker
		getManagedBean().setTitleChecker(Boolean.FALSE);
		FacesContext fc = FacesContext.getCurrentInstance();
		PortletRequest request = (PortletRequest)fc.getExternalContext().getRequest();
		RenderResponse response = (RenderResponse) request.getAttribute("javax.portlet.response");
		response.setTitle(ResourceBundleUtils
				.getMessage(
						"lbl.sc.pid.response.reduce.min.title",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, PURCHASE_IN_DEFAULT_RESPONSE_PAGE);
	}
	
	public void updatePID(){
		debugLogger("updatePurchaseInDefault");
		IPurchaseInDefaultSO purchaseInDefaultSO = null;
		FacesScopes facesScopes = new FacesScopes();
		try {
			purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION,
							IPurchaseInDefaultSO.class);
			PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
			ScPurchaseInDefault scPurchaseInDefault = getManagedBean()
					.getPurchaseInDefault();
			
			
			Long statusId = getManagedBean().getPidStatus().getStatusId();
			
			if (DPStatusConstants.PID_PENDING_SUPPLIER_RESPONSE.equals(statusId)) {

				boolean validateRemark = false;
				boolean isQuantityCheckNeeded = false;
				String outcome = null;

				if (getManagedBean().isVisibleOrderQty()) {
					isQuantityCheckNeeded = true;
				}

				BigDecimal tempQty = scPurchaseInDefault.getQuantity();
				validateResponsePID(scPurchaseInDefault, validateRemark, isQuantityCheckNeeded, outcome);

				// ---start----set remark supplier
				saveSupplierRemark();
				// ---end----set remark supplier

				reqDTO.setPurchaseInDefaultNo(scPurchaseInDefault.getPurchaseInDefaultNo());
				PurchaseInDefaultResDTO pidResDTO = purchaseInDefaultSO.retrievePurchaseInDefault(reqDTO);

				ScPurchaseInDefault scPID = pidResDTO.getPurchaseInDefaultDtl();

				//to prevent ERR-100048 if user click save more than one
				scPID.setQuantity(tempQty); // reset to previous value
				scPID.setSuppQuantity(getManagedBean().getQtyBySupplier());

				reqDTO.setPurchaseInDefaultDtl(scPID);
				PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.savePurchaseInDefault(reqDTO);

				if (resDTO.isSuccessful()) {
					successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, scPurchaseInDefault.getPurchaseInDefaultNo(),
							Boolean.FALSE);

				}
			}else{
				
				boolean validateRemark = false;
				boolean isQuantityCheckNeeded = false;
				boolean validatePID = true; //#9133
				String outcome = null;

				if (getManagedBean().isVisibleOrderQty()) {
					isQuantityCheckNeeded = true;
				}
				
				// #9133 - start
				if (!DPStatusConstants.PID_PENDING_VERIFICATION_SUPPLIER_RESPONSE.equals(statusId)) {
					validatePID = false;
					//validateRemark();
				}
				// #9133 - end

				if(validatePID)//#9133 - add condition
					validateRequisitionerResponsePID(scPurchaseInDefault, validateRemark, isQuantityCheckNeeded, outcome);

				getCurrentStatus(getPmStatusByStatusId(getManagedBean().getCurrentStatusId()));				
				
				// #9133 - start
				scPurchaseInDefault.setRemarks(initializeRemarks(
						scPurchaseInDefault,
						scPurchaseInDefault.getCurrentStatus(),
						scPurchaseInDefault.getRemarks(), ScPIDRemark.class));
				// #9133 - end
				saveRemark(scPurchaseInDefault);
				
				//reqDTO.setPurchaseInDefaultNo(scPurchaseInDefault.getPurchaseInDefaultNo());
				reqDTO.setPurchaseInDefaultId(scPurchaseInDefault.getPurchaseInDefaultId());
				reqDTO.setFetchSubEntity(true);
				PurchaseInDefaultResDTO pidResDTO = purchaseInDefaultSO
						.getPurchaseInDefault(reqDTO);
				
				ScPurchaseInDefault scPID = pidResDTO.getPurchaseInDefaultDtl();

				//to prevent ERR-100048 if user click save more than one
				scPID.setTitle(getManagedBean().getPurchaseInDefault().getTitle());
				scPID.setJustification(getManagedBean().getPurchaseInDefault().getJustification());
				scPID.setReasonType(getManagedBean().getPmReason());
				scPID.setSourceMethod(getManagedBean().getPurchaseInDefault().getSourceMethod());
				scPID.setReqQuantity(getManagedBean().getPurchaseInDefault().getReqQuantity());
				scPID.setQuantity(getManagedBean().getPurchaseInDefault().getReqQuantity());
				scPID.setScPurchaseInDefaultAttachment(scPurchaseInDefault.getScPurchaseInDefaultAttachment());//#9133

				reqDTO.setPurchaseInDefaultDtl(scPID);
				PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.savePurchaseInDefault(reqDTO);
				
				// #9133 - start
				reqDTO.setPurchaseInDefaultId(scPurchaseInDefault.getPurchaseInDefaultId());
				reqDTO.setFetchSubEntity(true);
				PurchaseInDefaultResDTO resDTOpid = purchaseInDefaultSO.getPurchaseInDefault(reqDTO);
				getManagedBean().setPurchaseInDefault(resDTOpid.getPurchaseInDefaultDtl());

				ScPurchaseInDefault sPID = getManagedBean().getPurchaseInDefault();

				sPID.setRemarks(initializeRemarks(sPID, scPurchaseInDefault.getCurrentStatus(), sPID.getRemarks(), ScPIDRemark.class));

				justificationTypeEvent(sPID.getReasonType(), sPID.getReasonDesc());
				// #9133 - start					
					
				if (resDTO.isSuccessful()) {
					successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT, scPurchaseInDefault.getPurchaseInDefaultNo(),
							Boolean.FALSE);
				}
			}
		} catch (NullPointerException e) {
			log.info(e.getStackTrace());
		}
	}
	
	private void validateResponsePID(ScPurchaseInDefault scPurchaseInDefault, Boolean validateRemark, Boolean isQuantityCheckNeeded, String outcome){
		FacesScopes facesScopes = new FacesScopes();
		BigDecimal qtyByRequisitioner = getManagedBean().getQtyByRequisitioner();
		BigDecimal qtyBySupplier = getManagedBean().getQtyBySupplier();
		// validate if qtyBySupplier more than qtyByRequisitioner
		if (qtyBySupplier != null && qtyBySupplier.intValue() >= 1) {
			if (qtyBySupplier.compareTo(qtyByRequisitioner) < 0) {
				throw new EpException(MessageConst.ERROR_100035, new String[] {
						ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.supplier.allowed.order.qty",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()),
						qtyByRequisitioner.toString() });
			}
		}

		// validate quantity
		if (qtyBySupplier != null
				&& qtyBySupplier.intValue() < 1) {
			throw new EpException(MessageConst.ERROR_100033, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.supplier.allowed.order.qty",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "0" });
		}

		int checkQty = 0;
		
		CtContractItem contractItem = scPurchaseInDefault.getContractedItem();
		CtPanelItem ctPanelItem = scPurchaseInDefault.getPanelItem();  
		//BigDecimal tempQty = scPurchaseInDefault.getQuantity();
		scPurchaseInDefault.setQuantity(getManagedBean().getQtyBySupplier());
		if (contractItem != null) {

			if (scPurchaseInDefault.getQuantity() != null) {
				double minimumQuantity = contractItem
						.getMinimumOrderQty();
				if(scPurchaseInDefault.getCircularItem() != null) {
					minimumQuantity = scPurchaseInDefault.getCircularItem().
							getMinimumOrderQty().doubleValue();
				}
				checkQty = Double.compare(scPurchaseInDefault.getQuantity()
						.doubleValue(), minimumQuantity);
			}
		} else if (ctPanelItem != null) {
			if (scPurchaseInDefault.getQuantity() != null) {
				checkQty = Double.compare(scPurchaseInDefault.getQuantity()
						.doubleValue(), ctPanelItem
						.getMinimumOrderQty());
			}
		}
		
		if (isQuantityCheckNeeded) {
			if (checkQty >= 0) {
				throw new EpException(
						MessageConst.ERROR_760203,
						new String[] {
								ResourceBundleUtils
										.getMessage(
												"lbl.sc.purchase.in.default.supplier.allowed.order.qty",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												facesScopes
														.getUserLocale()),
								ResourceBundleUtils
										.getMessage(
												"lbl.sc.purchase.in.default.min.qty",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												facesScopes
														.getUserLocale()) });
			}
		}
		
		if (validateRemark) {
			if (qtyBySupplier.compareTo(qtyByRequisitioner) != 0 || BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
				boolean newRemark = false;
				for (ScPIDRemark remark : getManagedBean().getSuppRemarks()) {
					// no new remark being insert
					if (remark.getRemark() == null || remark.getRemark().equalsIgnoreCase("")) {
						newRemark = true;
					}
				}
				if (newRemark) {
					throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.remark", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
				}
			}
		}
	}
	
	private void validateRequisitionerResponsePID(ScPurchaseInDefault scPurchaseInDefault, Boolean validateRemark, Boolean isQuantityCheckNeeded, String outcome){
		FacesScopes facesScopes = new FacesScopes();
		BigDecimal qtyByRequisitioner = getManagedBean().getPurchaseInDefault().getReqQuantity();		
		Boolean isCheckCentralContractItem = getManagedBean().isCheckCentralContractItem();
		BigDecimal minQty = null;
		
		if(qtyByRequisitioner == null){
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.order.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
		}
		
		
		if(isCheckCentralContractItem)
			minQty = getManagedBean().getPurchaseInDefault().getCircularItem().getMinimumOrderQty();
		else
			minQty = new BigDecimal(getManagedBean().getPurchaseInDefault().getContractedItem().getMinimumOrderQty());
		
		
		if (qtyByRequisitioner != null
				&& qtyByRequisitioner.intValue() < 1) {
			throw new EpException(MessageConst.ERROR_100033, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.order.qty",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "0" });
		}

		int checkQty = 0;
		
		checkQty = Double.compare(qtyByRequisitioner.doubleValue(), minQty.doubleValue());
		
		if (checkQty >= 0) {
			throw new EpException(MessageConst.ERROR_760203, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.order.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.min.qty",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
		}

		// validate quantity
	
		if (validateRemark) {
				boolean newRemark = false;
				for (ScPIDRemark remark : getManagedBean().getPurchaseInDefault().getRemarks()) {
					// no new remark being insert
					if (remark.getRemark() == null || remark.getRemark().equalsIgnoreCase("") || remark.getRemark().isEmpty()) {
						newRemark = true;
					}
				}
				if (newRemark) {
					throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.remark", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
				}			
		}
		
		if(getManagedBean().getPurchaseInDefault().getScPurchaseInDefaultAttachment() == null){
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.txt.attachment", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
		}
	}
	
	public void saveSupplierRemark() {
		IPurchaseInDefaultSO purchaseInDefaultSO = null;
		//FacesScopes facesScopes = new FacesScopes();
		purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
		PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
		try {
			for (ScPIDRemark remark : getManagedBean().getSuppRemarks()) {
				if (remark.getRemark() != null && !remark.getRemark().equalsIgnoreCase("")) {
					if (remark.getRemarkId() == null && getManagedBean().getSuppRemarkId() == null) {
						reqDTO.setScPIDRemark(remark);
						PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.updateRemark(reqDTO);
						getManagedBean().setSuppRemarkId(resDTO.getScPIDRemark().getRemarkId());
						break;
					}

					ScPIDRemark fetchRemark = purchaseInDefaultSO
							.retrieveRemarkById(getManagedBean().getSuppRemarkId());
					fetchRemark.setRemark(remark.getRemark());
					fetchRemark.setCreatedBy(remark.getCreatedBy());
					reqDTO.setScPIDRemark(fetchRemark);
					purchaseInDefaultSO.updateRemark(reqDTO);
					break;
				}
			}

		} catch (NullPointerException e) {
			log.info(e.getStackTrace());
		}
	}
	
	public void saveRemark(ScPurchaseInDefault scPurchaseInDefault) {
		IPurchaseInDefaultSO purchaseInDefaultSO = null;
		//FacesScopes facesScopes = new FacesScopes();
		purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
		PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
		
		try {
			for (ScPIDRemark remark : scPurchaseInDefault.getRemarks()) {
				if (remark.getRemark() != null && !remark.getRemark().equalsIgnoreCase("")) {
					if (remark.getRemarkId() == null) {
						reqDTO.setScPIDRemark(remark);
						purchaseInDefaultSO.updateRemark(reqDTO);
						break;
					}

					ScPIDRemark fetchRemark = purchaseInDefaultSO
							.retrieveRemarkById(remark.getRemarkId());
					fetchRemark.setRemark(remark.getRemark());
					fetchRemark.setCreatedBy(remark.getCreatedBy());
					reqDTO.setScPIDRemark(fetchRemark);
					purchaseInDefaultSO.updateRemark(reqDTO);
					break;
				}
			}

		} catch (NullPointerException e) {
			log.info(e.getStackTrace());
		}
	}
	
	public void supplierAction(String outcome) {
		debugLogger("supplierAction");
		this.getManagedBean().setAction(outcome);
		if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
			//getManagedBean().setAutoUpdate(Boolean.FALSE);
			updateBPMTask(outcome, null);
		} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
			//getManagedBean().setAutoUpdate(Boolean.TRUE);
			RequestContext.getCurrentInstance().execute("rejectDlg.show()");
		}
	}
	
	public void requisitionerAction(String outcome) {
		debugLogger("requisitionerAction");
		this.getManagedBean().setAction(outcome);
		if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)) {
			//getManagedBean().setAutoUpdate(Boolean.FALSE);
			updateBPMTask(outcome, null);
		} else if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
			//getManagedBean().setAutoUpdate(Boolean.TRUE);
			RequestContext.getCurrentInstance().execute("cancelDlg.show()");
		}
	}
	
	public void updateBPMTasks(String outcome) {
		updateBPMTask(outcome, null);
	}
	
	private String getPIDContractType (ScPurchaseInDefault scPurchaseInDefault) {
		String contractType = null;
		if(scPurchaseInDefault.getPanelItem() != null) {
			contractType = SourcingConstants.PID_TYPE_PANEL;
		}
		else {
			if (scPurchaseInDefault.getContractedItem().getCtContractVer()
					.getCtContract().getContractType() != null) {
				if (scPurchaseInDefault.getContractedItem()
						.getCtContractVer().getCtContract()
						.getContractType().getParameterCode()
						.equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
					contractType = SourcingConstants.PID_TYPE_MINISTRY;
				} else if (scPurchaseInDefault.getContractedItem()
						.getCtContractVer().getCtContract()
						.getContractType().getParameterCode()
						.equals(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
					contractType = SourcingConstants.PID_TYPE_CENTRAL;
				}
			}		
		}
		return contractType;
	}
	
	public PmReason reasonAllowSupplierResponse(){

		IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IReasonSO.class);

		PmParameter reasonCategory = getParameterByTypeAndCode(ParameterTypeConst.REASON_CATEGORY, ReasonCategory.PID_JUSTIFICATION
				.value());
		
		PmParameter sourceMethod = getParameterByTypeAndCode(ParameterTypeConst.SOURCING_METHOD, ParameterConst.PID_SOURCE_METHOD_CR);

		ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
		reasonReqDTO.setReasonCategoryId(reasonCategory.getParameterId()); //475 - PID
		reasonReqDTO.setSourceMethodId(sourceMethod.getParameterId()); //842 - CR
		ReasonResDTO reasonResDTO = reasonSO.retrieveReasonBySourceMethodUsingContractPurchase(reasonReqDTO);
		
		PmReason pmReason = new PmReason();
		for (PmReason reason : reasonResDTO.getPmReasonList()) {
			pmReason = reason;
			break;
		}
		
		return pmReason;
	}
	
	public PmReason retrieveChildReason(PmReason parentReason) {
		IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);

		ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
		reasonReqDTO.setReasonCategoryId(parentReason.getReasonCategoryCode().getParameterId());
		reasonReqDTO.setParentReasonId(parentReason.getReasonId());
		ReasonResDTO reasonResDTO = reasonSO.retrieveChildReason(reasonReqDTO);

		PmReason pmReason = new PmReason();
		for (PmReason reason : reasonResDTO.getPmReasonList()) {
			pmReason = reason;
			break;
		}

		return pmReason;
	}
	
	public boolean reasonExitsCtPidReason(PmReason pmReason){
		boolean exist = false;
		IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);

		ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
		reasonReqDTO.setReasonCategoryId(pmReason.getReasonId());
		reasonReqDTO.setContractVerId(getManagedBean().getPurchaseInDefault().getContractedItem()
				.getCtContractVer().getContractVerId());
		ReasonResDTO reasonResDTO = reasonSO.checkExistingCtPidReason(reasonReqDTO);
		
		if(reasonResDTO.isSuccessful())
			exist = true;
			
		return exist;
	}
	
	public boolean reasonExitsCtCircularPidReason(PmReason pmReason){
		boolean exist = false;
		IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);

		ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
		reasonReqDTO.setReasonCategoryId(pmReason.getReasonId());
		reasonReqDTO.setContractVerId(getManagedBean().getPurchaseInDefault().getContractedItem()
				.getCtContractVer().getContractVerId());
		ReasonResDTO reasonResDTO = reasonSO.checkExistingCtCircularPidReason(reasonReqDTO);
		
		if(reasonResDTO.isSuccessful())
			exist = true;
			
		return exist;
	}
	
	//to insert child reason
	public void savePidReason() {
		ScPurchaseInDefault purchaseInDefault = getManagedBean().getPurchaseInDefault();
		PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
		IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IReasonSO.class);
		IPurchaseInDefaultSO purchaseInDefaultSO = null;
		purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);

		PmReason pmReason = retrieveChildReason(reasonAllowSupplierResponse());	

		try {
			if (getManagedBean().getPurchaseInDefault().getContractedItem().getCtContractVer().getCtContract()
					.getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							 
				boolean IsReasonExist = reasonExitsCtPidReason(pmReason);
				
				if(!IsReasonExist){
					CtPidReason ctPidReasons = new CtPidReason();
					ctPidReasons.setPmReason(pmReason);
					ctPidReasons.setReasonDesc(null);
					ctPidReasons.setCtContractVer(getManagedBean().getPurchaseInDefault().getContractedItem()
							.getCtContractVer());
					reqDTO.setCtPidReason(ctPidReasons);
					purchaseInDefaultSO.insertPidReason(reqDTO);
				}

			} else {
				
				boolean IsReasonExist = reasonExitsCtCircularPidReason(pmReason);

				if (!IsReasonExist) {
					CtCircularPidReason ctCircularPidReasons = new CtCircularPidReason();
					ctCircularPidReasons.setPmReason(pmReason);
					ctCircularPidReasons.setReasonDesc(null);
					ctCircularPidReasons.setCircularVer(getManagedBean().getPurchaseInDefault().getPanelItem()
							.getCtCircularVer());
					reqDTO.setCtCircularPidReason(ctCircularPidReasons);
					purchaseInDefaultSO.insertCircularPidReason(reqDTO);
				}
			}
		} catch (NullPointerException e) {
			log.info(e.getStackTrace());
		}
	}
	
	public boolean isQtySupEqualQtyReq() {
		Boolean isValueEqual = false;
		if (getManagedBean().getPurchaseInDefault().getReqQuantity() != null
				&& getManagedBean().getPurchaseInDefault().getSuppQuantity() != null) {
			BigDecimal qtyByRequisitioner = getManagedBean().getPurchaseInDefault().getReqQuantity();
			BigDecimal qtyBySupplier = getManagedBean().getPurchaseInDefault().getSuppQuantity();
			
			if (getManagedBean().getAction().equals(BpmOutcomeConst.BPM_ACCEPT)
					|| getManagedBean().getAction().equals(BpmOutcomeConst.BPM_REVISE)) {// #9133
				if (qtyBySupplier.compareTo(qtyByRequisitioner) == 0)
					isValueEqual = true;
			}
		}

		return isValueEqual;
	}
	
	public boolean isJustificationChanged() {
		Boolean isJustificationChanged = true;
		
		IPurchaseInDefaultSO purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
		
		PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
		purchaseInDefaultReqDTO.setPurchaseInDefaultId(getManagedBean().getPurchaseInDefault().getPurchaseInDefaultId());
		purchaseInDefaultReqDTO.setFetchSubEntity(true);
		PurchaseInDefaultResDTO purchaseInDefaultResDTO = purchaseInDefaultSO
				.getPurchaseInDefault(purchaseInDefaultReqDTO);
		ScPurchaseInDefault purchaseInDefault = purchaseInDefaultResDTO
				.getPurchaseInDefaultDtl();
				
		if (purchaseInDefault.getReasonType().getReasonId()
				.compareTo(getManagedBean().getPmReason().getReasonId()) != 0) {
			isJustificationChanged = false;
		}

		return isJustificationChanged;
	}
	// end - #28865

	/**
	 * Page navigation to catalogue search page in Purchase in default.
	 */
	public void backToPrevious() {
		log.info("backToPrevious");
		FacesContext fc = FacesContext.getCurrentInstance();
		if (getManagedBean().getPageNavToRequestNote() != null) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							getManagedBean().getPageNavToRequestNote());
		} else {
			// clear data
			super.renewSesionObject();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "catalogueSearch");
		}
	}
	

	/**
	 * Show purchase in default.
	 *
	 * @param purchaseInDefaultId the purchase in default id
	 */
	public void viewPurchaseInDefault(long purchaseInDefaultId){
		debugLogger("viewPurchaseInDefault");
		IPurchaseInDefaultSO purchaseInDefaultSO  = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		try{
			// Purchase In Default	
			purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
			PurchaseInDefaultReqDTO purchaseInDefaultReqDTO = new PurchaseInDefaultReqDTO();
			purchaseInDefaultReqDTO.setPurchaseInDefaultId(purchaseInDefaultId);
			purchaseInDefaultReqDTO.setFetchSubEntity(true);
			PurchaseInDefaultResDTO purchaseInDefaultResDTO = purchaseInDefaultSO.getPurchaseInDefault(purchaseInDefaultReqDTO);
			ScPurchaseInDefault purchaseInDefault = purchaseInDefaultResDTO.getPurchaseInDefaultDtl();

			// approved by
			getManagedBean().setApprovedBy(purchaseInDefaultResDTO.getApprovedBy());
			
			if(purchaseInDefault != null){
				// contract item 
				if(purchaseInDefault.getContractedItem() != null){
					getManagedBean().setCheckContractItem(Boolean.TRUE);

				} else{ //panel item
					getManagedBean().setCheckContractItem(Boolean.FALSE);
				}

				getManagedBean().setPurchaseInDefault(purchaseInDefault);
				initializeApproverGroup();
				/*populatePidReason(purchaseInDefaultResDTO.getCtPidReasonList(), 
						purchaseInDefaultResDTO.getCircularPidReasonList());*/
				justificationTypeEvent(purchaseInDefault.getReasonType(), purchaseInDefault.getReasonDesc());

				getCurrentStatus(getPmStatusByStatusId(DPStatusConstants.PID_COMPLETED));
				
				// disable all for pid view
				getManagedBean().setPidBtnFlag(Boolean.FALSE);

				// current page url for page navigation from pid to request note
				getManagedBean().setPageNavToRequestNote(fc.getViewRoot().getViewId());
			}

			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_IN_DEFAULT_PAGE);
		} finally {
			purchaseInDefaultSO = null;
		}
	}
	
	//start - #28865
	protected void updateWorkflowStatus(ScPurchaseInDefault purchaseInDefault, Boolean duringSubmitPID ) {		
		IScWorkFlowStatusSO workFlowStatusSO;
					workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT);
		reqDTO.setDocId(purchaseInDefault.getPurchaseInDefaultId());

		ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus();

		if (duringSubmitPID) {
			workFlowStatus.setPmStatus(getPmStatusByStatusId(DPStatusConstants.PID_PENDING_SUPPLIER_RESPONSE));
		}
		
		workFlowStatus.setPurchaseInDefault(purchaseInDefault);
		workFlowStatus.setCurrent(true);
		purchaseInDefault.setCurrentStatus(workFlowStatus);

		reqDTO.setScWorkFlowStatus(workFlowStatus);		
		workFlowStatusSO.updateCurrentWorkflowStatus(reqDTO);

	}
	//end - #28865
	
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
	 * Gpki sign action.
	 * 
	 * @param digiSign
	 *            the digi sign
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
	 * @param pmDigiSign
	 *            the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Gets the text before sign.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		sb.append(getManagedBean().getPurchaseInDefault()
				.getPurchaseInDefaultNo());
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
	 * View market research.
	 * 
	 * @param scPurchaseInDefault
	 *            the sc purchase in default
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void viewMarketResearch(ScPurchaseInDefault scPurchaseInDefault)
			throws IOException {
		debugLogger("viewMarketResearch");
		ScRequestItem requestItem = null;
		String itemCode = "";

		if (scPurchaseInDefault != null) {
			FacesScopes facesScopes = new FacesScopes();
			Map<String, String> paramMap = new HashMap<String, String>();
			String reportName = ReportConst.SC_MARKET_RESEARCH_RPT_JSP;

			paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
					.getUserSessionBean().getUserId() + "");

			if (scPurchaseInDefault.isBrandNew()) {
				paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG, 0 + "");
				if (getManagedBean().getvCmSupplierItem() != null) {
					itemCode = getManagedBean().getvCmSupplierItem()
							.getItemCode();

				}

			} else {
				paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG, 0 + "");
				if (scPurchaseInDefault.getPanelItem() != null) {
					requestItem = scPurchaseInDefault.getPanelItem()
							.getScRequestItem();
					if (requestItem != null && requestItem.getCmItem() != null) {
						itemCode = requestItem.getCmItem().getExtensionCode();
					}

				} else if (scPurchaseInDefault.getContractedItem() != null) {
					requestItem = scPurchaseInDefault.getContractedItem()
							.getScRequestItem();
					if (requestItem != null && requestItem.getCmItem() != null) {
						itemCode = requestItem.getCmItem().getExtensionCode();
					}
				}
			}
			debugLogger("viewMarketResearch - item code" + itemCode);
			paramMap.put(ReportConst.PARAM_ITEM_CODE, itemCode);
			
			if(!getManagedBean().isVisibleOrderQty()) {
				if(scPurchaseInDefault.getPanelItem() != null) {
					paramMap.put(ReportConst.PARAM_PANEL_CONTRACT_ITEM_ID, 
							scPurchaseInDefault.getPanelItem().getPanelItemId() + "");
					paramMap.put(ReportConst.PARAM_PANEL_OR_CONTRACT, 
							ReportConst.PARAM_PANEL_TYPE);
				} else if(scPurchaseInDefault.getContractedItem() != null) {
					paramMap.put(ReportConst.PARAM_PANEL_CONTRACT_ITEM_ID, 
							scPurchaseInDefault.getContractedItem().getContractItemId() + "");
					paramMap.put(ReportConst.PARAM_PANEL_OR_CONTRACT, 
							ReportConst.PARAM_CONTRACT_TYPE);
				}
			}
			
			// if(scPurchaseInDefault.isBrandNew()) {
			/*
			 * VCmSupplierItem vCmSupplierItem =
			 * getManagedBean().getvCmSupplierItem(); //long uomId =
			 * vCmSupplierItem.getUomId(); String itemCode =
			 * vCmSupplierItem.getItemCode(); String conditionField =
			 * getManagedBean().getConditionField();
			 * 
			 * for (String filter: conditionField.split("\\|")){
			 * System.out.println(filter); String searchField = null; String
			 * searchValue = null; int searchCondition =
			 * SearchOperator.OPERATOR_EQUALS.value(); String[] conditions =
			 * filter.split("\\^"); searchField = conditions[0]; searchValue =
			 * conditions[1]; if(conditions.length > 2) { searchCondition =
			 * Integer.parseInt(conditions[2]); } if
			 * ("itemName".equals(searchField)) {
			 * paramMap.put(ReportConst.PARAM_ITEM_NAME, searchValue); } else if
			 * ("companyName".equals(searchField)) {
			 * paramMap.put(ReportConst.PARAM_SUPPLIER_NAME, searchValue); }
			 * else if ("epNo".equals(searchField)) {
			 * paramMap.put(ReportConst.PARAM_EP_NO, searchValue); } else if
			 * ("bizRegNo".equals(searchField)) {
			 * paramMap.put(ReportConst.PARAM_BIZ_REG_NO, searchValue); } else
			 * if ("stateId".equals(searchField)) {
			 * paramMap.put(ReportConst.PARAM_STATE_ID, searchValue);
			 * 
			 * } else if ("districtId".equals(searchField)) {
			 * paramMap.put(ReportConst.PARAM_DISTRICT_ID, searchValue); } else
			 * if ("mofNo".equals(searchField)) {
			 * paramMap.put(ReportConst.PARAM_MOF_NO, searchValue); }
			 * paramMap.put(ReportConst.PARAM_ITEM_CODE, itemCode);
			 * //paramMap.put(ReportConst.PARAM_UOM_ID, uomId + "");
			 * System.out.println(searchField + "|" + searchValue + "|" +
			 * searchCondition); } paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG,
			 * 0 + "");
			 * 
			 * } else { if(scPurchaseInDefault.getPanelItem() != null){
			 * requestItem =
			 * scPurchaseInDefault.getPanelItem().getScRequestItem(); } else
			 * if(scPurchaseInDefault.getContractedItem() != null){ requestItem
			 * = scPurchaseInDefault.getContractedItem().getScRequestItem(); }
			 * 
			 * paramMap.put(ReportConst.PARAM_REQUEST_ITEM_ID,
			 * requestItem.getRequestNoteItemId() + "");
			 * paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG, 1 + ""); }
			 */
			generateReport(reportName, paramMap);
		}
	}

	private void retriveContractSupplierGstInfomation(
			CtContractItem contractItem) {
		ISupplierGstSO supplierGstSo = null;
		SupplierGstResDTO supplierGstresDTO = null;
		try {
			if (null != contractItem) {
				supplierGstSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierGstSO.class);
				SupplierGstReqDTO supplierGstreqDTO = new SupplierGstReqDTO();
				if (null != contractItem.getCtContractVer()
						&& null != contractItem.getCtContractVer()
								.getCtContract()
						&& null != contractItem.getCtContractVer()
								.getCtContract().getSmSupplier()) {
					supplierGstreqDTO.setSupplierId(contractItem
							.getCtContractVer().getCtContract().getSmSupplier()
							.getSupplierId());
					supplierGstreqDTO.setBranchCode(null);
					supplierGstresDTO = supplierGstSo
							.retrieveGstInfoBySupplierIdAndBranchCode(supplierGstreqDTO);
				}

				if (null != supplierGstresDTO
						&& null != supplierGstresDTO.getSmGstSupplier()) {
					this.getManagedBean().setSmGstSupplier(
							supplierGstresDTO.getSmGstSupplier());

				} else {
					this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
				}

			} else {
				this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
			}

		} finally {
			supplierGstSo = null;
		}

	}

	/**
	 * Retrive panel supplier gst infomation.
	 *
	 * @param supplier the supplier
	 * @return the sm gst supplier
	 */
	public SmGstSupplier retrivePanelSupplierGstInfomation(SmSupplier supplier) {
		ISupplierGstSO supplierGstSo = null;
		try {
			if (null != supplier && null != supplier.getSupplierId()) {
				supplierGstSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierGstSO.class);
				SupplierGstReqDTO supplierGstreqDTO = new SupplierGstReqDTO();
				supplierGstreqDTO.setSupplierId(supplier.getSupplierId());
				supplierGstreqDTO.setBranchCode(null);
				SupplierGstResDTO supplierGstresDTO = supplierGstSo
						.retrieveGstInfoBySupplierIdAndBranchCode(supplierGstreqDTO);
				if (null != supplierGstresDTO
						&& null != supplierGstresDTO.getSmGstSupplier()) {
					this.getManagedBean().setSmGstSupplier(
							supplierGstresDTO.getSmGstSupplier());

				} else {
					this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
				}

			} else {
				this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
			}

		} finally {
			supplierGstSo = null;
		}
		return this.getManagedBean().getSmGstSupplier();
	}
	
	/**
	 * Gets the user group dropdown.
	 *
	 * @return the user group dropdown
	 * 
	 */
	public SelectItem[] getUserGroupDropdown() {
		SelectItem[] items = null;
		if(!CollectionUtils.isEmpty(getManagedBean().getUserGroupList())) {
			items = new SelectItem[getManagedBean().getUserGroupList().size()];
			int i = 0;
			for(PmUserGroup userGroup : getManagedBean().getUserGroupList()) {
				items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName().replaceAll("\\s+", " ").trim());
			}
		} else {
			items = new SelectItem[0];
		}
		
	    return items;
	}
	
	/**
	 * User group change listener.
	 */
	public void userGroupChangeListener() {
		debugLogger("userGroupChangeListener");
		RequestNoteResDTO resDTO = null;
		
		ScPurchaseInDefault purchaseInDefault = getManagedBean().getPurchaseInDefault();
		Long userGroupId = getManagedBean().getUserGroupId();
		
		
		if(userGroupId != null && getManagedBean().getUserGroupList() != null) {
			for (PmUserGroup pmUserGroup : getManagedBean().getUserGroupList()) {
				if(userGroupId.equals(pmUserGroup.getUserGroupId())){
					purchaseInDefault.setPmUserGroup(pmUserGroup);
				}
			}
		}

		List<PmUser> approvers  = new ArrayList<PmUser>();
		
		if(purchaseInDefault.getPmUserGroup() != null) {
			RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
			reqDTO.setGroupId(purchaseInDefault.getPmUserGroup().getUserGroupId());
			reqDTO.setOrgProfileId(purchaseInDefault.getPmOrgProfile().getOrgProfileId());
			resDTO = getRequestNoteSO().retrieveApproverListByGroupId(reqDTO);
			List<PmUserGroupUser> groupUsers = resDTO.getApproverGroupUserList();

			if(!CollectionUtils.isEmpty(groupUsers)) {
				for(PmUserGroupUser groupUser : groupUsers) {
					approvers.add(groupUser.getPmUser());
				}
			}
		}
		
		getManagedBean().setApproverList(approvers);
	}
	
	/**
	 * Gets the ct contract criteria.
	 *
	 * @return the ct contract criteria
	 */
	public String getCtContractCriteria() {
		StringBuilder criteria = new StringBuilder();
		StringBuilder ctContractCriteria = new StringBuilder();
		ScPurchaseInDefault scPurchaseInDefault = getManagedBean().getPurchaseInDefault();

		//start - 28865
		Long statusId = getManagedBean().getPidStatus().getStatusId();
		
		if (DPStatusConstants.PID_PENDING_SUBMISSION.equals(statusId)
				|| DPStatusConstants.PID_PENDING_REVISION.equals(statusId)
				&& scPurchaseInDefault.getSuppQuantity() == null) { //#9133
			criteria.append("pmReason.parentReason").append("^").append("null").append("^")
			.append(SearchOperator.OPERATOR_IS_NULL.value()).append("|");
		}
		//end - 28865
			
		// contract ver id
		if(scPurchaseInDefault.getContractedItem() != null && scPurchaseInDefault.getContractedItem().getCtContractVer() != null){
			ctContractCriteria.append(scPurchaseInDefault.getContractedItem().getCtContractVer().getContractVerId());
		}
		ctContractCriteria.append("^").append(
					SearchOperator.OPERATOR_EQUALS.value());
			criteria.append("ctContractVer.contractVerId").append("^")
					.append(ctContractCriteria.toString());
		return criteria.toString();
	}
	
	
	/**
	 * Gets the circular criteria.
	 *
	 * @return the circular criteria
	 */
	public String getCircularCriteria() {
		StringBuilder criteria = new StringBuilder();
		StringBuilder ctCircularCriteria = new StringBuilder();
		ScPurchaseInDefault scPurchaseInDefault = getManagedBean().getPurchaseInDefault();
		
		// start - 28865
		Long statusId = getManagedBean().getPidStatus().getStatusId();

		if (DPStatusConstants.PID_PENDING_SUBMISSION.equals(statusId)
				|| DPStatusConstants.PID_PENDING_REVISION.equals(statusId)
				&& scPurchaseInDefault.getSuppQuantity() == null) { //#9133
			criteria.append("pmReason.parentReason").append("^").append("null").append("^")
					.append(SearchOperator.OPERATOR_IS_NULL.value()).append("|");
		}
		// end - 28865
		
		// for panel
		if(scPurchaseInDefault.getPanelItem() != null){
			ctCircularCriteria.append(scPurchaseInDefault.getPanelItem().getCtCircularVer().getCircularVerId());
		} else{
			// circular ver id for Circular
			if(scPurchaseInDefault.getCircularItem() != null && scPurchaseInDefault.getCircularItem().getCtCircularContract().getCtCircularVer().getCircularVerId() != null){
				ctCircularCriteria.append(scPurchaseInDefault.getCircularItem().getCtCircularContract().getCtCircularVer().getCircularVerId());
			}
		}
		
		ctCircularCriteria.append("^").append(
				SearchOperator.OPERATOR_EQUALS.value());
		criteria.append("ctCircularVer.circularVerId").append("^")
		.append(ctCircularCriteria.toString());
		return criteria.toString();
	}
	
	/**
	 * Gets the faces scopes.
	 *
	 * @return the faces scopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * Gets the request note so.
	 *
	 * @return the request note so	
	 */
	private IRequestNoteSO getRequestNoteSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
	}
	
	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);		
	}
	
	// #9133 - start
	public Boolean isPidGenerated(){
		boolean generated = false;
		ScPurchaseInDefault scPurchaseInDefault = getManagedBean().getPurchaseInDefault();
		Long scPurchaseInDefaultId = scPurchaseInDefault.getPurchaseInDefaultId();
		
		if (scPurchaseInDefaultId != null)
			generated = true;
		
		return generated;		
	}
	
	public void beforeCancelPid() {
		RequestContext.getCurrentInstance().execute("cancelDlg.show()");
	}
	
	private void validateRemark() {
		boolean newRemark = false;
		FacesScopes facesScopes = new FacesScopes();
		
		for (ScPIDRemark remark : getManagedBean().getPurchaseInDefault().getRemarks()) {
			// no new remark being insert
			if (remark.getRemark() == null || remark.getRemark().equalsIgnoreCase("") || remark.getRemark().isEmpty()) {
				newRemark = true;
			}
		}
		if (newRemark) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.remark", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
		}
	}
	// #9133 - end
}