/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loa.DpLOAManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

// TODO: Auto-generated Javadoc
/**
 * The Class DpLOABackingBean.
 *
 * @author kenny
 * @since 27.05.2014
 */
@ManagedBean
@RequestScoped
public class DpLOABackingBean extends SourcingBaseBackingBean<DpLOAManagedBean> {
	private static final long serialVersionUID = 1L;
	/** The log. */
	public static Logger log = EpLogger.getLogger(DpLOABackingBean.class);
	
	/** The Flag */
	private boolean sqExpiredMessageFlag = false;

	/**
	 * Instantiates a new dp loa backing bean.
	 */
	public DpLOABackingBean() {
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
	}

	/**
	 * render the view based on the task id
	 * page navigation to Loa List page
	 */
	public void preRenderView() {
		debugLogger("preRenderView");

		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			getManagedBean().setSourceId(sourceId);
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			loaDetailBackingBean.renewSesionObject();
			loaDetailBackingBean.getManagedBean().setSourceId(sourceId);
			loaDetailBackingBean.getManagedBean().setPayLoad(dpDataType);

			retrieveLOAList(sourceId);

			// retrieveLOAList(2122L);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.DP_LOA_LIST_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**c
	 * retrieving LOI LOA List from database.
	 * 
	 * @param requestNoteId the request note id
	 */
	public void retrieveLOAList(Long requestNoteId) {
		debugLogger("retrieveLOAList");

		IPurchaseRequestSO purchaseRequestSO = null;
		try {
			purchaseRequestSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IPurchaseRequestSO.class);
			Long userId = new FacesScopes().getUserSessionBean().getUserId();

			PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
			reqDTO.setRequestNoteId(requestNoteId);
			List<String> fulfilmentTypes = new ArrayList<String>();
			fulfilmentTypes.add(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
			fulfilmentTypes.add(ParameterConst.FULFILMENT_TYPE_SCHEDULED);
			reqDTO.setFulfillmentTypes(fulfilmentTypes);
			reqDTO.setAssignedTo(userId);

			PurchaseRequestResDTO resDTO = purchaseRequestSO
					.retrievePurchaseRequestsByReqNote(reqDTO);

			if (resDTO.isSuccessful()) {
				List<ScPurchaseRequest> purchaseRequestList = resDTO
						.getListPurchaseRequest();
				if (purchaseRequestList != null
						&& purchaseRequestList.size() > 0) {
					ScPurchaseRequest purchaseRequest = purchaseRequestList
							.iterator().next();
					getManagedBean().setScRequestNote(
							purchaseRequest.getRequestNote());
					getManagedBean()
							.setPurchaseRequestList(purchaseRequestList);

					Long statusId = null;
					int completeCount = 0;
					boolean isCancelled = false;
					
					for (ScPurchaseRequest scPurchaseRequest : purchaseRequestList) {
						statusId = scPurchaseRequest.getScLoa().getPmStatus()
								.getStatusId();			
						
						if (QTStatusConst.LOA_REJECTED.equals(statusId)) {
							isCancelled = true;							
						} else if (!QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId) && 
								!QTStatusConst.LOA_COMPLETED.equals(statusId) && 
								!QTStatusConst.LOA_CANCELLED_DUE_TO_SIMPLE_QUOTE_VALIDITY_EXPIRED.equals(statusId) &&
								!QTStatusConst.LOA_REJECTED_DUE_TO_SIMPLE_QUOTE_VALIDITY_EXPIRED.equals(statusId) ) {
							
							continue;
						}
						
						completeCount++;
					}			
					
					if (isCancelled && completeCount == purchaseRequestList.size()) {
						getManagedBean().setShowBPMButtons(true);
					} else {
						getManagedBean().setShowBPMButtons(false);
					}
				}
			}

		} finally {
			purchaseRequestSO = null;
		}
	}
	
	/**
	 * update bpm - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */	
	public String updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");

		IQTAwardSO qtAwardSO = null;

		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			String requestNoteNo = getManagedBean().getScRequestNote().getRequestNoteNo();
			boolean isSuccessful = qtAwardSO.updateBPMTask(
					requestNoteNo,
					getManagedBean().getSourceId(),
					new FacesScopes().getBPMAuthenticationCtxToken(),
					getManagedBean().getTaskId(), outcome);

			if (isSuccessful) {
				boolean isAcknowledgeRequired = Boolean.TRUE;
				if(BpmOutcomeConst.BPM_RERECOMMEND.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700059, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_REQUEST_NOTE,
							requestNoteNo, isAcknowledgeRequired);
				}
				if(BpmOutcomeConst.BPM_REQUOTE.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700058, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_SIMPLE_QUOTE,
							getManagedBean().getScRequestNote().getQuote().getQuoteNo(), isAcknowledgeRequired);
				}
				renewSesionObject();
			}
		} finally {
			qtAwardSO = null;
		}

		return null;
	}
	
	/**
	 * Check either SQ is valid or not (SQ only valid for 60 days after it closing date)
	 */
	public boolean isSqStillValid(Long currentStatusId){		
		
		Boolean validDate = true;	
		ScQuote scQuote = getManagedBean().getScRequestNote().getQuote();
			
		Integer sqTimeValidity = Integer.valueOf(EpPropertiesUtils.getInstance()
				.getProperty("sqTimeValidity"));
		
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
	
	//26284 - Start
	public void supplierDisciplinaryActionInfo(SmSupplier smSupplier){
		SmSupplier currSupp = smSupplier;
		if(currSupp != null){
			List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(currSupp);
			if(!EntityHelper.isCollectionEmpty(voList)
					&& voList.size() > 0){
				getManagedBean().setDisciplineVOs(voList);
			} else {
				getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
			}
		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		RequestContext.getCurrentInstance().execute(
				"disciplinaryActionActiveDpDlg.show();");
	}
				
	private List<DisciplinaryActionVO> getDisciplinaryActionVO(SmSupplier currSupp) {
		ISupplierDataMaintenanceSO supplierDataMaintenanceSO = (ISupplierDataMaintenanceSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierDataMaintenanceSO.class);
					
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(currSupp);
		List<DisciplinaryActionVO> voList = new ArrayList<DisciplinaryActionVO>();
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		perReqDTO.setSmSupplierId(currSupp.getSupplierId());
		StringBuilder str1 = new StringBuilder();
		StringBuilder str2 = new StringBuilder();
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierId(perReqDTO);
					
		List<SmDisciplinaryAction> resultList = supplierDataMaintenanceSO.retrieveActiveDisciplinaryActionBySupplierDp(reqDTO).getSmDisciplinaryActions();
		if (resultList != null
				&& resultList.size() > 0) {
			for(SmDisciplinaryAction disciplinaryAction: resultList){
				if(disciplinaryAction.isWarning() || disciplinaryAction.isCancelCategory() 
						|| disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration() 
						|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isManufacturerCancel()
						|| disciplinaryAction.isBumiputeraCancel()){
					DisciplinaryActionVO retVal = new DisciplinaryActionVO();
					if(disciplinaryAction.getStartDate() != null)
						retVal.setStartDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getStartDate()));
						retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getEndDate()));
						str1.append(retVal.getStartDate()).append(" "); 
						getManagedBean().setDisciplinaryActionStartDate(str1.toString());
					if(disciplinaryAction.getEndDate() != null)
						retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getEndDate()));
						str2.append(retVal.getEndDate()).append(".");
						getManagedBean().setDisciplinaryActionEndDate(str2.toString());
					if(disciplinaryAction.getRemark() != null)
						retVal.setRemarks(disciplinaryAction.getRemark());
								
					if(disciplinaryAction.isWarning()){
						retVal.setWarning(true);
						getManagedBean().setDisciplinaryActionWarning(true);
					}
					if(disciplinaryAction.isCancelCategory())
						retVal.setCancelCategory(true);
					if(disciplinaryAction.isSuspendRegistration())
						retVal.setSuspendRegistration(true);
					if(disciplinaryAction.isCancelRegistration())
						retVal.setCancelRegistration(true);
					if(disciplinaryAction.isBumiputeraCancel())
						retVal.setBumiputeraCancel(true);
					if(disciplinaryAction.isManufacturerCancel())
						retVal.setManufacturerCancel(true);
					if(disciplinaryAction.isBlacklistOwner())
						retVal.setBlacklistOwnerDirector(true);
								
					if(disciplinaryAction.isCancelCategory()){
						List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
						List<Object[]> catList = SmApplicationUtil.retrieveCancelCategoryBySupplier(currSupp);
									
						if(!EntityHelper.isCollectionEmpty(catList)
								&& catList.size() > 0){
							for(Object[] arr : catList){
								if(arr[7] != null && disciplinaryAction.getDisciplinaryActionId().toString().equalsIgnoreCase(arr[7].toString())){
									DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
									String catCode = StringUtils.EMPTY;
									catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
									catVo.setCategoryCode(catCode);	
									catVoList.add(catVo);
								}
							}						
							retVal.setCategoryCodeList(catVoList);
						}
					}	
								
					if(disciplinaryAction.isBlacklistOwner()
							|| disciplinaryAction.isSuspendRegistration()
								|| disciplinaryAction.isCancelRegistration()){
						List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
						if(personnelResDTO != null 
								&& personnelResDTO.getSmPersonnelList().size() > 0){
							for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
								BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
								if(personnel.isCancelRegistration()
										|| personnel.isSuspendRegistration()
											|| personnel.isBlacklisted()){
									blacklistOwner.setIcNo(personnel.getIdentificationNo());
									blacklistownerDirectorList.add(blacklistOwner);
								}
							}
							retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
						}
					}
				voList.add(retVal);
				}				
			}
		}
		return voList;
	}
				
	public Boolean getDisciplinaryActionActive(SmSupplier supplier) {
		ISupplierDataMaintenanceSO supplierDataMaintenanceSO = (ISupplierDataMaintenanceSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierDataMaintenanceSO.class);
					
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(supplier);
		return supplierDataMaintenanceSO.retrieveActiveDisciplinaryActionBySupplierDp(reqDTO).isDisciplinaryActionActiveDp();
	}
	//26284 - End
}