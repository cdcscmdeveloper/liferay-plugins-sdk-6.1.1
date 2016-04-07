/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
// TODO: Auto-generated Javadoc
package my.ep.web.backingbean.sc.purchaseindefault;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
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

	/** The log. */
	private static Logger log = Logger
			.getLogger(PurchaseInDefaultBackingBean.class);

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/** The text before sign. */
	private String textBeforeSign;

	/** The purchase in default page. */
	private String PURCHASE_IN_DEFAULT_PAGE = "purchaseInDefaultMain";

	/**
	 * Instantiates a new purchase in default backing bean.
	 */
	public PurchaseInDefaultBackingBean() {
		super();
		if (MDC.get(LogKey.USER_ID) == null) {
			PortletRequest request = (PortletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			User user = (User) request.getAttribute(WebKeys.USER);
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
		ISearchBean<VCmSupplierItem> viewSearchBean = null;
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
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
			if (contractItem != null) {
				scPurchaseInDefault.setContractedItem(contractItem);
				retriveContractSupplierGstInfomation(scPurchaseInDefault
						.getContractedItem());
				getManagedBean().setCheckContractItem(Boolean.TRUE);
				
			}
			populatePidReason(resDTO.getCtPidReasonList(), resDTO.getCircularPidReasonList());
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
			if (ctPanelItem != null) {
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
				List<CtCircularPidReason> pidReason = new ArrayList<CtCircularPidReason>();
				if(ctPanelItem.getCtCircularVer() != null) {
					pidReason = ctPanelItem.getCtCircularVer().getCtCircularPidReason();
				}
				populatePidReason(null, pidReason);
			}
		}
	
		// WorkFlow Status
		ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus();
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(DPStatusConstants.PID_PENDING_SUBMISSION);
		StatusResDTO statusResDTO = iStatusSO
				.retrieveStatusByStatusId(statusReqDTO);
		workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
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

		// by default value as cheaper price
		scPurchaseInDefault.setReasonType(new PmReason());
		
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

	/**
	 * submit PurchaseInDefault. submit - save data into database and BPM
	 * process will route to the next respective user. page navigate to
	 * acknowledge page.
	 */
	public String submitPurchaseInDefault() {
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
			scPurchaseInDefault.setRecordStatus(Boolean.TRUE);
			scPurchaseInDefault.setCreatedDate(DateUtils.getCurrentDate());
			
			// Target Approver
			formApprover(scPurchaseInDefault);
			
			// WorkFlow Status
			scPurchaseInDefault.setWorkFlowStatuses(null);

			// justification
			if (scPurchaseInDefault.getReasonType().getReasonCode() != null) {
				for (PmReasonDesc reasonDesc : getManagedBean().getPidJustifications()) {
					if (scPurchaseInDefault
							.getReasonType()
							.getReasonCode()
							.equalsIgnoreCase(
									reasonDesc.getPmReason().getReasonCode())) {
						scPurchaseInDefault.setReasonType(reasonDesc
								.getPmReason());
					}
				}
			}
			
			// validate pid
			validatePID(scPurchaseInDefault);
			
			if (scPurchaseInDefault.getContractedItem() != null) {
				if (scPurchaseInDefault.getContractedItem().getCtContractVer()
						.getCtContract().getContractType() != null) {
					if (scPurchaseInDefault.getContractedItem()
							.getCtContractVer().getCtContract()
							.getContractType().getParameterCode()
							.equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
						reqDTO.setContractType(SourcingConstants.PID_TYPE_MINISTRY);
					} else if (scPurchaseInDefault.getContractedItem()
							.getCtContractVer().getCtContract()
							.getContractType().getParameterCode()
							.equals(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
						reqDTO.setContractType(SourcingConstants.PID_TYPE_CENTRAL);
					}
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
			} else if (scPurchaseInDefault.getPanelItem() != null) {
				reqDTO.setContractType(SourcingConstants.PID_TYPE_PANEL);
				int checkQty = 0;

				if (scPurchaseInDefault.getQuantity() != null) {
					checkQty = Double.compare(scPurchaseInDefault.getQuantity()
							.doubleValue(), scPurchaseInDefault.getPanelItem()
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
				if (scPurchaseInDefault.getReasonType().getReasonCode()
						.equalsIgnoreCase(ParameterConst.PID_RI)) {
					if (checkQty > 0) {
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
			}
			reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
			// Hoa: add token authenticate to support BPM
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setBackupApproverList(getManagedBean().getBackupApprovers());
			// reqDTO.setConditionField(getManagedBean().getConditionField());
			PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO
					.submitPurchaseInDefault(reqDTO);
			if (resDTO.isSuccessful()) {
				ScPurchaseInDefault purchaseInDefault = resDTO
						.getPurchaseInDefaultDtl();
				successMessage(MessageConst.INFO_700006,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
						purchaseInDefault.getPurchaseInDefaultNo(), Boolean.TRUE);
				// clear pid data
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
		// validate quantity
		if (scPurchaseInDefault.getQuantity() != null
				&& scPurchaseInDefault.getQuantity().intValue() < 0) {
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
			scPurchaseInDefault.setRemarks(initializeRemarks(
					scPurchaseInDefault,
					scPurchaseInDefault.getCurrentStatus(),
					scPurchaseInDefault.getRemarks(), ScPIDRemark.class));

			if (!BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
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
			resDTO = purchaseInDefaultSO.updatePurchaseInDefault(reqDTO);

			if (resDTO.isSuccessful()) {
				if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)
						|| BpmOutcomeConst.BPM_VERIFY.equalsIgnoreCase(outcome)) {
					Long statusId = getManagedBean().getPidStatus()
							.getStatusId();

					if (DPStatusConstants.PID_PENDING_VERIFICATION
							.equals(statusId)) {
						successMessage(MessageConst.INFO_700062,
								ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT,
								getManagedBean().getPurchaseInDefault()
										.getPurchaseInDefaultNo(), Boolean.TRUE);
					} else if (DPStatusConstants.PID_PENDING_APPROVAL
							.equals(statusId)) {
						successMessage(MessageConst.INFO_700007,
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

			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}

		} finally {
			purchaseInDefaultSO = null;
		}

		return resDTO;
	}

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
			if (isDigiSigningRequired(RoleCode.BPK_PCA.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

			} else if (isDigiSigningRequired(RoleCode.BPK_CCA.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

			} else if (isDigiSigningRequired(RoleCode.MIN_CONTRACT_ADMIN.value())) {
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
	 * Event for justification type drop down list to show order quantity to key
	 * in.
	 */
	public void justificationTypeEvent() {
		String reasonCode = getManagedBean().getPurchaseInDefault().getReasonType()
				.getReasonCode();
		log.info("justificationTypeEvent : "
				+ reasonCode);
		
		IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IReasonSO.class);
		
		PmParameter reasonCategory = getParameterByTypeAndCode(ParameterTypeConst.REASON_CATEGORY, ReasonCategory.PID_JUSTIFICATION
						.value());
		
		if(reasonCategory != null) {
			// call PM API to check output for reason
			ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
			reasonReqDTO.setReasonCategoryId(reasonCategory.getParameterId());
			reasonReqDTO.setReasonCode(reasonCode);
			ReasonResDTO reasonResDTO = reasonSO.retrieveReasonByReasonCode(reasonReqDTO);
			PmReason pmReason = reasonResDTO.getPmReason();
			
			if(pmReason != null) {
				PmParameter conditionField = pmReason.getConditionField();
				PmParameter sourceMethod = pmReason.getSourceMethod();
				
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
		}

		/*if (getManagedBean().getPurchaseInDefault().getReasonType()
				.getReasonCode() != null) {
			if (getManagedBean().getPurchaseInDefault().getReasonType()
					.getReasonCode().equalsIgnoreCase(ParameterConst.PID_CP)
					|| getManagedBean().getPurchaseInDefault().getReasonType()
							.getReasonCode()
							.equalsIgnoreCase(ParameterConst.PID_SS)) {
				getManagedBean().setVisibleOrderQty(Boolean.FALSE);

			} else if (getManagedBean().getPurchaseInDefault().getReasonType()
					.getReasonCode().equalsIgnoreCase(ParameterConst.PID_RI)
					|| getManagedBean().getPurchaseInDefault().getReasonType()
							.getReasonCode()
							.equalsIgnoreCase(ParameterConst.PID_EQ)) {
				getManagedBean().setVisibleOrderQty(Boolean.TRUE);

			} else {
				getManagedBean().setVisibleOrderQty(Boolean.FALSE);
			}
		}*/
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
		getManagedBean().setJustifications(
				getDropdownCodeValues(results));
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
				resDTO = getDirectPurchaseSO().retrieveGroupListByOrgId(reqDTO);
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
		// panel item
		if (purchaseInDefault.getPanelItem() != null) {
			getManagedBean().setCheckContractItem(Boolean.FALSE);
		}

		getManagedBean().setPurchaseInDefault(purchaseInDefault);

		IStatusSO statusSO = null;
		try {

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(getManagedBean().getCurrentStatusId());

			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);

			getCurrentStatus(statusResDTO.getPmStatus());

		} finally {
			statusSO = null;
		}

		// remarks
		purchaseInDefault.setRemarks(initializeRemarks(purchaseInDefault,
				purchaseInDefault.getCurrentStatus(),
				purchaseInDefault.getRemarks(), ScPIDRemark.class));

		initializeApproverGroup();
		populatePidReason(purchaseInDefaultResDTO.getCtPidReasonList(), 
				purchaseInDefaultResDTO.getCircularPidReasonList());
		justificationTypeEvent();
		// title checker
		getManagedBean().setTitleChecker(Boolean.FALSE);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, PURCHASE_IN_DEFAULT_PAGE);
	}

	/**
	 * delete uploaded file in purchase in default
	 */
	public void deleteUploadFile() {
		debugLogger("deleteUploadFile");
		getManagedBean().getPurchaseInDefault().setScPurchaseInDefaultAttachment(null);
	}

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

			if(purchaseInDefault != null){
				// contract item 
				if(purchaseInDefault.getContractedItem() != null){
					getManagedBean().setCheckContractItem(Boolean.TRUE);

				} else{ //panel item
					getManagedBean().setCheckContractItem(Boolean.FALSE);
				}

				getManagedBean().setPurchaseInDefault(purchaseInDefault);
				initializeApproverGroup();
				populatePidReason(purchaseInDefaultResDTO.getCtPidReasonList(), 
						purchaseInDefaultResDTO.getCircularPidReasonList());
				justificationTypeEvent();

				try {
					StatusReqDTO statusReqDTO = new StatusReqDTO();
					statusReqDTO.setStatusId(DPStatusConstants.PID_COMPLETED);

					StatusResDTO statusResDTO = getStatusSO().retrieveStatusByStatusId(statusReqDTO);

					getCurrentStatus(statusResDTO.getPmStatus());

				} finally {
					//statusSO = null;
				}
				
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

	
	/**
	 * Gets the status so.
	 *
	 * @return the status so
	 */
	private IStatusSO getStatusSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
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
				paramMap.put(ReportConst.PARAM_IS_SAVED_FLAG, 1 + "");
				if (scPurchaseInDefault.getPanelItem() != null) {
					requestItem = scPurchaseInDefault.getPanelItem()
							.getScRequestItem();
					if (requestItem.getCmItem() != null) {
						itemCode = requestItem.getCmItem().getExtensionCode();
					}

				} else if (scPurchaseInDefault.getContractedItem() != null) {
					requestItem = scPurchaseInDefault.getContractedItem()
							.getScRequestItem();
					if (requestItem.getCmItem() != null) {
						itemCode = requestItem.getCmItem().getExtensionCode();
					}
				}
			}

			paramMap.put(ReportConst.PARAM_ITEM_CODE, itemCode);

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
			resDTO = getDirectPurchaseSO().retrieveApproverListByGroupId(reqDTO);
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
	 * Gets the faces scopes.
	 *
	 * @return the faces scopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * Gets the direct purchase so.
	 *
	 * @return the direct purchase so	
	 */
	private IDirectPurchaseSO getDirectPurchaseSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
	}
}