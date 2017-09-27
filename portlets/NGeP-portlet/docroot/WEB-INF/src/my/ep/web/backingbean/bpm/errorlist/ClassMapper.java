package my.ep.web.backingbean.bpm.errorlist;

import my.ep.jaxb.cd.requestdata.CDRequestDataType;
import my.ep.jaxb.ct.agreement.CTAgreementDataType;
import my.ep.jaxb.ct.bankaccount.CTBankAccountDataType;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.jaxb.ct.circular.cancellation.CTCircularCancellationDataType;
import my.ep.jaxb.ct.common.user.CTUserDataType;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.jaxb.ct.factoring.CTFactoringDataType;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.common.chargeline.FLChargeLineItemType;
import my.ep.jaxb.fl.common.factory.FLFactoringCompanyDataType;
import my.ep.jaxb.fl.common.flitem.FLItemDataType;
import my.ep.jaxb.fl.common.order.FLOrderDataType;
import my.ep.jaxb.fl.common.user.FLUserDataType;
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.jaxb.fl.dodata.FLDeliveryOrderDataType;
import my.ep.jaxb.fl.dodata.list.FLDeliveryOrderListDataType;
import my.ep.jaxb.fl.dofulfilmentdata.FLDOFulfillmentDataType;
import my.ep.jaxb.fl.fc.FLFactoringRemoveReqType;
import my.ep.jaxb.fl.frndata.FLFulFillmentReceivedNoteDataType;
import my.ep.jaxb.fl.invoice.FLInvoiceDataType;
import my.ep.jaxb.fl.maintenace.ap59.codata.FLCOCloseReqDataType;
import my.ep.jaxb.fl.maintenace.cancel.codata.FLCOCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.common.order.FLOrderCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.dofulfillment.FLDOFulfillmentCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.frn.FLFRNCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.invoice.FLInvoiceCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.podata.FLPOCancelReqType;
import my.ep.jaxb.fl.maintenace.modification.dofulfillment.FLDOFulfillmentModificationReqType;
import my.ep.jaxb.fl.maintenace.modification.invoice.FLInvoiceModificationReqType;
import my.ep.jaxb.fl.payment.FLPaymentDataType;
import my.ep.jaxb.fl.payment.bankaccount.FLBankDetailsDataType;
import my.ep.jaxb.fl.payment.instruction.FLPaymentInstructionDataType;
import my.ep.jaxb.fl.payment.penalty.FLDeductionPenaltyDataType;
import my.ep.jaxb.fl.phis.codata.FLPHISContractOrderDataType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.fl.stopdeliveryreqdata.FLStopDeliveryReqDataType;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.jaxb.pm.profiledata.PMProfileDataType;
import my.ep.jaxb.pp.plandata.PPPlanDataType;
import my.ep.jaxb.sample.dept.DeptDataType;
import my.ep.jaxb.sample.emp.EmpDataType;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.jaxb.sc.businessruledata.SCBusinessRuleDataType;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.jaxb.sc.sitevisitproposaldata.SCSiteVisitProposalDataType;
import my.ep.jaxb.sc.specificationdata.SCSpecificationDataType;
import my.ep.jaxb.sm.mofaccmaindata.SMMOFAccMainDataType;
import my.ep.jaxb.sm.mofappcandata.SMMOFAppCanDataType;
import my.ep.jaxb.sm.mofpreregdata.SMMOFPreRegDataType;
import my.ep.jaxb.sm.mofregcandata.SMMOFRegCanDataType;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.jaxb.sm.useradddata.SMUserAddDataType;

public class ClassMapper {
	
	public static String ns = "http://xmlns.oracle.com/bpm/bpmobject/Data/";
	
	/**
	 * Class to Tag Mapping
	 */
	public static BidirectionMap initMap() {
		
		BidirectionMap map = new BidirectionMap();
		//Procurement Plan
		map.put(PPPlanDataType.class, "PP_Plan_Data");
		//Profile Management
		map.put(PMProfileDataType.class, "PM_Profile_Data");
		//Codification
		map.put(CDRequestDataType.class, "CD_Request_Data");
		//Sourcing
		map.put(SCDirectPurchaseDataType.class, "SC_DirectPurchase_Data");
		map.put(SCQuotationTenderDataType.class, "SC_QuotationTender_Data");
		map.put(SCSpecificationDataType.class, "SC_Specification_Data");
		map.put(SCCommitteeDataType.class, "SC_Committee_Data");
		map.put(SCEvaluationDataType.class, "SC_Evaluation_Data");
		map.put(SCSiteVisitProposalDataType.class, "SC_SiteVisitProposal_Data");
		map.put(SCAwardSupplierDataType.class, "SC_AwardSupplier_Data");
		map.put(SCBusinessRuleDataType.class, "SC_BusinessRule_Data");		
		//Supplier Management
		map.put(SMMOFPreRegDataType.class, "SM_MOFPreReg_Data");
		map.put(SMMOFRegDataType.class, "SM_MOFReg_Data");
		map.put(SMMOFAccMainDataType.class, "SM_MOFAccMain_Data");
		map.put(SMSiteVisitDataType.class, "SM_SiteVisit_Data");
		map.put(SMMOFAppCanDataType.class, "SM_MOFAppCan_Data");
		map.put(SMMOFRegCanDataType.class, "SM_MOFRegCan_Data");
		map.put(SMUserAddDataType.class, "SM_UserAdd_Data");
		
		//Start FL
		map.put(FLContractOrderDataType.class, "FL_ContractOrder_Data");
		map.put(FLDanDataType.class, "FL_Dan_Data");
		map.put(FLDeliveryOrderDataType.class, "FL_DeliveryOrder_Data");
		map.put(FLDeliveryOrderListDataType.class, "FL_Delivery_Order_List_Data");
		map.put(FLInvoiceDataType.class, "FL_Invoice_Data");
		map.put(FLPurchaseOrderDataType.class, "FL_PurchaseOrder_Data");
		map.put(FLStopFullfillmentDataType.class, "FL_StopFullfillment_Data");
		map.put(FLFulFillmentReceivedNoteDataType.class, "FL_FulFillment_Received_Note_Data");
		map.put(FLPaymentInstructionDataType.class, "FL_Payment_Instruction_Data");
		map.put(FLPaymentDataType.class, "FL_Payment_Data");
		map.put(FLBankDetailsDataType.class, "FL_Bank_Details_Data");
		map.put(FLDeductionPenaltyDataType.class, "FL_Deduction_Penalty_Data");
		map.put(FLOrderDataType.class,"FL_Order_Data");
		map.put(FLChargeLineItemType.class,"FL_ChargeLine_Item");
		map.put(FLUserDataType.class,"FL_User_Data");
		map.put(FLFactoringCompanyDataType.class, "FL_Factoring_Company_Data");
		map.put(FLItemDataType.class,"FL_Item_Data");
		map.put(FLFactoringRemoveReqType.class,"FL_Factoring_Remove_Req");
		map.put(FLPHISContractOrderDataType.class,"FL_PHIS_ContractOrder_Data");
		map.put(FLDOFulfillmentDataType.class, "FL_DOFulfillment_Data");
		map.put(FLStopDeliveryReqDataType.class, "FL_Stop_Delivery_Req_Data");
		
		//Maintainence
		map.put(FLDOFulfillmentModificationReqType.class,"FL_DOFulfillment_Modification_Req");
		map.put(FLInvoiceModificationReqType.class,"FL_Invoice_Modification_Req");
				
		//Cancellation
		map.put(FLDOFulfillmentCancelReqType.class,"FL_DO_Fulfillment_Cancel_Req");
		map.put(FLFRNCancelReqType.class,"FL_FRN_Cancel_Req");
		map.put(FLInvoiceCancelReqType.class,"FL_Invoice_Cancel_Req");
		map.put(FLPOCancelReqType.class,"FL_PO_Cancel_Req");
		map.put(FLCOCancelReqType.class,"FL_CO_Cancel_Req");
		map.put(FLCOCloseReqDataType.class,"FL_CO_Close_Req_Data");
		map.put(FLOrderCancelReqType.class,"FL_Order_Cancel_Req");
		//End FL
		
		//Contract
		map.put(CTContractDataType.class, "CT_Contract_Data");
		map.put(CTAgreementDataType.class, "CT_Agreement_Data");
		map.put(CTContractSuspensionDataType.class, "CT_Contract_Suspension_Data");
		map.put(CTBankAccountDataType.class, "CT_Bank_Account_Data");
		map.put(CTCircularContractDataType.class, "CT_Circular_Contract_Data");
		map.put(CTCircularCancellationDataType.class, "CT_Circular_Cancellation_Data");
		
		map.put(CTContractAmendmentDataType.class, "CT_Contract_Amendment_Data");
		map.put(CTFactoringDataType.class, "CT_Factoring_Data");
		map.put(CTContractTerminationDataType.class, "CT_Contract_Termination_Data");
		map.put(CTUserDataType.class, "CT_User_Data");
		//Temp
		map.put(DeptDataType.class, "Dept_Data");
		map.put(EmpDataType.class,"Emp_Data");
		
		
		
		return map;
	}

}