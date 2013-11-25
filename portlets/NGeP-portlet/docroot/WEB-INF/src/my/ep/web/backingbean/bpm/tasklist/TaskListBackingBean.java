package my.ep.web.backingbean.bpm.tasklist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.ct.suspend.ISuspendActiveSO;
import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.fl.request.IShippingInstructionSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constants.FlProcessNameConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.ct.suspend.ContractSuspendReqDTO;
import my.ep.dto.ct.suspend.ContractSuspendResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.jaxb.cd.requestdata.CDRequestDataType;
import my.ep.jaxb.ct.agreement.CTAgreementDataType;
import my.ep.jaxb.ct.bankaccount.CTBankAccountDataType;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.jaxb.ct.circular.cancellation.CTCircularCancellationDataType;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.jaxb.ct.factoring.CTFactoringDataType;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.jaxb.fl.dofullfillmentdata.FLDOFullfillmentDataType;
import my.ep.jaxb.fl.invoicepaymentdata.FLInvoiceAndPaymentDataType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.pp.budgetdata.PPBudgetDataType;
import my.ep.jaxb.pp.plandata.PPPlanDataType;
import my.ep.jaxb.sm.mofaccmaindata.SMMOFAccMainDataType;
import my.ep.jaxb.sm.mofregcandata.SMMOFRegCanDataType;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.jaxb.sm.procsofficedata.SMProcsOfficeDataType;
import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.fl.request.FlShippingInstr;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.ct.agreement.AgreementBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.backingbean.ct.circular.panel.PanelBackingBean;
import my.ep.web.backingbean.ct.supplier.SupplierBackingBean;
import my.ep.web.backingbean.ct.suspend.ContractSuspendBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.cr.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.dan.DebitAdjustmentNoteBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.paymentmatch.PaymentMatchBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.backingbean.fl.shippingInstr.ShippingInstrBackingBean;
import my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean;
import my.ep.web.backingbean.pp.procurementplan.PlanBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.sdoreview.SdoReviewBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvcSiteAssignBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.bpm.tasklist.TaskListManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "tasklistBackingBean")
@RequestScoped
public class TaskListBackingBean extends BaseBackingBean<TaskListManagedBean> {

	private static Logger log = Logger.getLogger(TaskListBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yy hh:mm a");

	public TaskListBackingBean() {

		/*
		 * System.out.println("testing BPM******");
		 * 
		 * String viewBy = ""; try{ BpmResponseDTO responseDTO =
		 * retrieveWorklistItems(this.getManagedBean().getStatus(), viewBy);
		 * }catch(Exception e){ log.info("BPM Errors"); e.printStackTrace(); }
		 */
	}

	public BpmResponseDTO initiateTask() {
		System.out.println("initiateTask******");
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setProcessId("Codification.RequestUNSPSC");
		reqDTO.setOutcome("SUBMIT");
		CDRequestDataType obj = new CDRequestDataType();

		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);

		return res;
	}

	public BpmResponseDTO retrieveWorklistItems(String status, String viewBy) {

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		// String token = bpmSO.authenticate("roslan", "welcome1");
		String token = bpmSO.authenticate(this.getManagedBean()
				.getTempLoginId(), "welcome1");
		System.out.println("testing BPM auth******" + token);
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
		reqDTO.setOffset(calculateFinalOffSet(1));
		reqDTO.setState(status);
		reqDTO.setViewBy(viewBy);
		reqDTO.setCtxToken(token);
		BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);

		System.out.println("testing BPM******"
				+ responseDTO.getWorklistVO().getWorklistItem().size());

		this.getManagedBean().setTaskList(
				responseDTO.getWorklistVO().getWorklistItem());

		return responseDTO;

	}

	public String navigateToURL() {

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		String urlKey = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("key");
		String taskId = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("taskId");
		String ctx = bpmSO.authenticate(this.getManagedBean().getTempLoginId(),
				"welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		log.info("taskId:" + taskId);
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		System.out.println("********getProcessName********"
				+ res.getProcessName());
		System.out.println("********res.getAction()********" + res.getAction());
		System.out.println("********res.getTaskName()()********"
				+ res.getTaskName());

		this.getManagedBean().setProcessName(res.getProcessName());
		this.getManagedBean().setTaskName(res.getTaskName());
		this.getManagedBean().setAction(res.getAction());
		Object obj = res.getPojo();
		this.getManagedBean().setTaskId(taskId);

		if (res.getProcessName().equalsIgnoreCase("UNSPSC Code Request")) {

			CDRequestDataType unspsc = (CDRequestDataType) obj;

			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
					.getInstance().getServiceObject("cm", IRequestCodeSO.class);
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			RequestCodeResDTO resDTO = requestSO
					.retrieveUnspscCodeReview(codeDTO);

			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			unspscBB.getManagedBean().setRequestCode(resDTO.getRequestCode());

		} else if (res.getProcessName().equalsIgnoreCase(
				"Extension Code Request")) {
			CDRequestDataType unspsc = (CDRequestDataType) obj;

			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
					.getInstance().getServiceObject("cm", IRequestCodeSO.class);
			ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			CodificationReqDTO reqCodeDTO = new CodificationReqDTO();
			RequestCodeResDTO resDTO = null;
			unspscBB.getManagedBean().setReviewExtCodeList(null);
			for (String itemId : unspsc.getItemId()) {
				System.out.println("itemId:" + itemId);
				codeDTO.setRequestId(new Long(itemId));
				resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
				unspscBB.getManagedBean().getReviewExtCodeList()
						.add(resDTO.getRequestCode());
			}

			reqCodeDTO.setUnspscCode(resDTO.getRequestCode().getTempCode());
			CodificationResDTO resCodeDTO = codificationSO
					.searchCommodityObj(reqCodeDTO);

			unspscBB.getManagedBean().setTypeList(
					resCodeDTO.getCommodityObj().getCmType());
			unspscBB.getManagedBean().setBrandList(
					resCodeDTO.getCommodityObj().getCmBrand());
			unspscBB.getManagedBean().setMeasurementList(
					resCodeDTO.getCommodityObj().getCmMeasurement());
			resCodeDTO = codificationSO.searchColor(reqCodeDTO);
			unspscBB.getManagedBean().setColorList(resCodeDTO.getColorList());

		} else if (res.getProcessName().equalsIgnoreCase(
				"Extension Code Maintenance")) {
			CDRequestDataType unspsc = (CDRequestDataType) obj;

			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
					.getInstance().getServiceObject("cm", IRequestCodeSO.class);
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			RequestCodeResDTO resDTO = requestSO
					.retrieveUnspscCodeReview(codeDTO);

			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			unspscBB.getManagedBean().setRequestCode(resDTO.getRequestCode());

		} else if(res.getProcessName().equalsIgnoreCase(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME) ||res.getProcessName().equalsIgnoreCase(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME) 
				|| res.getProcessName().equalsIgnoreCase(FlProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME) || res.getProcessName().equalsIgnoreCase(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME)){
			
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			IShippingInstructionSO shippingSO = (IShippingInstructionSO) BusinessDelegate.getInstance().getServiceObject("fl", IShippingInstructionSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			
			FLContractOrderDataType coObj = (FLContractOrderDataType)obj;
			if(coObj.getContractRequestId().intValue() == 0){
				flReqDTO.setFlRequestId(coObj.getParentContractRequestId());
			}else{
				flReqDTO.setFlRequestId(coObj.getContractRequestId());
			}
			flReqDTO.setSelectedApprovers(coObj.getApproverList());
			
			if(getManagedBean().getTaskName().equalsIgnoreCase(FlProcessNameConst.REVISE_SI_HT) || getManagedBean().getTaskName().equalsIgnoreCase(FlProcessNameConst.REVIEW_SI_HT)){
				resDTO = shippingSO.retrieveShippingInstruction(flReqDTO);
			}else{
			resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);
			}
			if(getManagedBean().getTaskName().equalsIgnoreCase(FlProcessNameConst.REVISE_SI_HT)|| getManagedBean().getTaskName().equalsIgnoreCase(FlProcessNameConst.REVIEW_SI_HT)){
				ShippingInstrBackingBean siBB = new ShippingInstrBackingBean();
				siBB.getManagedBean().setShippingInstr(resDTO.getShippingInstr());
				siBB.getManagedBean().setFlRequest(resDTO.getShippingInstr().getFlFulfilmentRequest());
				siBB.getManagedBean().setBpmTaskId(taskId);
				siBB.getManagedBean().setActions(res.getAction());
				siBB.getManagedBean().setTempLogin(this.getManagedBean().getTempLoginId());
				}else if (getManagedBean().getTaskName().equalsIgnoreCase(FlProcessNameConst.CREATE_SI_HT)){
					ShippingInstrBackingBean siBB = new ShippingInstrBackingBean();
					FlShippingInstr flSi = new FlShippingInstr();
					flSi.setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());
					siBB.getManagedBean().setShippingInstr(flSi);
					siBB.getManagedBean().setBpmTaskId(taskId);
					siBB.getManagedBean().setActions(res.getAction());
					siBB.getManagedBean().setTempLogin(this.getManagedBean().getTempLoginId());
				}
				else if(res.getTaskName().equalsIgnoreCase(FlProcessNameConst.CREATE_NEW_CO_HT)||res.getTaskName().equalsIgnoreCase(FlProcessNameConst.CREATE_CO_HT) 
						|| res.getTaskName().equalsIgnoreCase(FlProcessNameConst.REVIEW_CR_HT) || res.getTaskName().equalsIgnoreCase(FlProcessNameConst.REVISE_CR_HT) 
						|| res.getTaskName().equalsIgnoreCase(FlProcessNameConst.CREATE_NORMAL_CO_HT) || res.getTaskName().equalsIgnoreCase(FlProcessNameConst.REVIEW_MTO_CR_HT)){

				ContractRequestBackingBean crBB = new ContractRequestBackingBean();
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
				flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
				flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());

				IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
				//TODO
				orgProfileReqDTO.setOrgProfileId(4L);
				OrgProfileResDTO pmResDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);

				if(coObj.getContractRequestId().intValue() == 0){
					FlFulfilmentRequest flRequest = new FlFulfilmentRequest();
					flRequest.setCtContract(resDTO.getFlFulfilmentRequest().getCtContract().getParentContract());

					crBB.getManagedBean().setFlRequest(flRequest);
					crBB.getManagedBean().getFlRequest().setPmDocStatus(new PmStatus());
					crBB.getManagedBean().getFlRequest().setIssuedOrgProfile(new PmOrgProfile());
					crBB.getManagedBean().getFlRequest().setChargeOrgProfile(new PmOrgProfile());
					crBB.getManagedBean().getFlRequest().setReceivedOrgProfile(new PmOrgProfile());
					crBB.getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());

				} else{
					crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				}
				crBB.getManagedBean().setBpmTaskId(taskId);
				crBB.getManagedBean().setActions(res.getAction());
				crBB.getManagedBean().setTempLogin(this.getManagedBean().getTempLoginId());
				crBB.getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
				crBB.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());
				if(pmResDTO.getPmOrgProfile() != null){
					crBB.getManagedBean().getFlRequest().setCreatedOrgProfile(pmResDTO.getPmOrgProfile());
				}
			}else if(res.getTaskName().equals(FlProcessNameConst.MTO_CO_ACK_HT) || res.getTaskName().equals(FlProcessNameConst.CO_ACK_HT)){

				FulfilmentOrderBackingBean florderBB  = new FulfilmentOrderBackingBean();
				florderBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				florderBB.getManagedBean().setTaskId(taskId);
				florderBB.getManagedBean().setActions(res.getAction());
				florderBB.getManagedBean().setContractRequest(Boolean.TRUE);
			}

		}else if(res.getProcessName().equalsIgnoreCase(FlProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME)){
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLInvoiceAndPaymentDataType paymentObj = (FLInvoiceAndPaymentDataType)obj;
			flReqDTO.setFlOrderId(paymentObj.getFullfillmentRequestId());
			if((res.getTaskName().equalsIgnoreCase(FlProcessNameConst.CREATE_INVOICE))){	
				IFulfilmentOrderSO flOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);

				resDTO = flOrderSO.retrieveFulfilmentOrder(flReqDTO);
				//				resDTO = invoiceSO.retrieveInvoice(flReqDTO);//if save button are exist
				FlInvoice inv = new FlInvoice();
				inv.setFlFulfilmentOrder(resDTO.getFlOrder());

				InvoiceBackingBean invBB = new InvoiceBackingBean();
				invBB.getManagedBean().setInvoice(inv);
				invBB.getManagedBean().setBpmTaskId(taskId);
				invBB.getManagedBean().setActions(res.getAction());
			}
			if((res.getTaskName().equalsIgnoreCase(FlProcessNameConst.PERFORM_PAYMNET_MATCH_HT))){
				IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject("fl",IInvoiceSO.class);

				resDTO = invoiceSO.retrieveInvoiceByFlOrderId(flReqDTO);
				FlPaymentAdvice paymentAdvice = new FlPaymentAdvice();
				paymentAdvice.setFlInvoice(resDTO.getInvoice());

				PaymentMatchBackingBean paymentBB = new PaymentMatchBackingBean();
				paymentBB.getManagedBean().setPaymentAdvice(paymentAdvice);
				paymentBB.getManagedBean().setBpmTaskId(taskId);
				paymentBB.getManagedBean().setActions(res.getAction());
			}
		}
		else if(res.getProcessName().equalsIgnoreCase(FlProcessNameConst.DELIVERY_ORDER_FULFILMENT_PROCESS_NAME)){
			ReceivedNoteBackingBean createFRNBackingBean = new ReceivedNoteBackingBean();  
			FLDOFullfillmentDataType flDeliveryOrder = (FLDOFullfillmentDataType)obj;
			createFRNBackingBean.getManagedBean().setDeliveryOrderId(flDeliveryOrder.getDeliveryOrderId());
			createFRNBackingBean.getManagedBean().setTaskId(taskId);
			createFRNBackingBean.getManagedBean().setActions(res.getAction());
			
		}else if(res.getProcessName().equalsIgnoreCase(FlProcessNameConst.DAN_GENERATION_PROCESS_NAME)){

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLDanDataType danObj = (FLDanDataType)obj;
			flReqDTO.setDanId(danObj.getDanId());
				IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject("fl",IAdjustmentSO.class);

				resDTO = adjustmentSO.retrieveAdjustmentById(flReqDTO);
				FlAdjustment adjust = resDTO.getAdjustment();

				DebitAdjustmentNoteBackingBean danBB = new DebitAdjustmentNoteBackingBean();
				danBB.getManagedBean().setAdjustment(adjust);
				danBB.getManagedBean().setBpmTaskId(taskId);
				danBB.getManagedBean().setActions(res.getAction());

		}
		else if(res.getProcessName().equalsIgnoreCase(FlProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME)){
			System.out.println("in Task List Create Purchase Order");
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType)obj;
			flReqDTO.setFlRequestId(flPurchaseOrd.getPurchaseRequestId());
			flReqDTO.setSelectedApprovers(flPurchaseOrd.getApproverList());
			resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			flReqDTO.setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());
			
			PurchaseRequestBackingBean purchaseRequestBackingBean=new PurchaseRequestBackingBean();  
			purchaseRequestBackingBean.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			purchaseRequestBackingBean.getManagedBean().setTaskId(taskId);
			System.out.println("Action In task list"+res.getAction());
			purchaseRequestBackingBean.getManagedBean().setActions(res.getAction());
			purchaseRequestBackingBean.getManagedBean().setTaskName(res.getTaskName());
			purchaseRequestBackingBean.getManagedBean().setTempLoginId(this.getManagedBean().getTempLoginId());
			List<String> source = new ArrayList<String>();   
			List<String> target = new ArrayList<String>();  
			purchaseRequestBackingBean.getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			purchaseRequestBackingBean.getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
			purchaseRequestBackingBean.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());
			if(null!=res.getAction() && !"".equals(res.getAction())){
				purchaseRequestBackingBean.getManagedBean().setSubmitButtonStatus(false);
			}
			if(res.getTaskName().equals(FlProcessNameConst.PR_ACK_HT) || res.getTaskName().equals(FlProcessNameConst.REVIEW_REJECTED_PO_HT)){
				FulfilmentOrderBackingBean florderBB=new FulfilmentOrderBackingBean();
				florderBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				florderBB.getManagedBean().setTaskId(taskId);
				florderBB.getManagedBean().setActions(res.getAction());
				florderBB.getManagedBean().setTempLoginId(this.getManagedBean().getTempLoginId());
			}
			
		} else if (res.getProcessName().equalsIgnoreCase("Budget Endorsement")) {
			log.info("task list loading " + res.getProcessName());
			// Procurement Plan - Budget
			PPBudgetDataType budgetObj = (PPBudgetDataType) obj;

			BudgetAllocationReqDTO budgetReqDTO = new BudgetAllocationReqDTO();
			budgetReqDTO.setBudgetAllocId(budgetObj.getBudgetId().longValue());
			IBudgetAllocationSO budgetSO = (IBudgetAllocationSO) BusinessDelegate
					.getInstance().getServiceObject("pp",
							IBudgetAllocationSO.class);
			BudgetAllocationResDTO budgetResSO = budgetSO
					.retrieveBudgetAllocation(budgetReqDTO);

			BudgetAllocationBackingBean budgetBB = new BudgetAllocationBackingBean();
			budgetBB.getManagedBean().setLoginUser(
					this.getManagedBean().getTempLoginId());
			budgetBB.getManagedBean().setBpmTaskId(taskId);
			budgetBB.getManagedBean().setActions(res.getAction());
			budgetBB.loadTask(budgetResSO.getBudgetAlloc(), res.getTaskName());

		} else if (res.getProcessName().equalsIgnoreCase("Plan Preparation")
				|| res.getProcessName().equalsIgnoreCase("Plan Revision")) {
			log.info("task list loading " + res.getProcessName());
			// Procurement Plan - Plan
			PPPlanDataType planObj = (PPPlanDataType) obj;

			ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
			planReqDTO.setPlanId(planObj.getPlanId().longValue());
			IProcurementPlanSO planSO = (IProcurementPlanSO) BusinessDelegate
					.getInstance().getServiceObject("pp",
							IProcurementPlanSO.class);
			ProcurementPlanResDTO planResDTO = planSO
					.retrievePlanById(planReqDTO);

			PlanBackingBean planBB = new PlanBackingBean();
			planBB.getManagedBean().setLoginUser(
					this.getManagedBean().getTempLoginId());
			planBB.getManagedBean().setBpmTaskId(taskId);
			planBB.getManagedBean().setActions(res.getAction());
			planBB.loadTask(planResDTO.getPlan(), res.getTaskName());

		} else if (res.getProcessName().equalsIgnoreCase("Contract Creation")) {

			CTContractDataType ctDataType = (CTContractDataType) obj;

			IContractSO contractSO = (IContractSO) BusinessDelegate
					.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			ctReqDTO.setContractId(ctDataType.getContractId().longValue());
			ContractResDTO resDTO = contractSO.retrieveContract(ctReqDTO);

			ContractDetailsBackingBean ctdBB = new ContractDetailsBackingBean();
			ctdBB.getManagedBean().setCtContract(resDTO.getContract());
			ctdBB.getManagedBean().setOutComes(res.getAction());
			ctdBB.getManagedBean().setTaskId(taskId);
			ctdBB.getManagedBean().setToken(ctx);

		} else if (res.getProcessName().equalsIgnoreCase("Agreement Creation")) {

			CTAgreementDataType agDataType = (CTAgreementDataType) obj;

			IAgreementSO agreementSO = (IAgreementSO) BusinessDelegate
					.getInstance().getServiceObject("ct", IAgreementSO.class);
			ContractAgreementReqDTO ctReqDTO = new ContractAgreementReqDTO();

			ctReqDTO.setContractId(agDataType.getContractId().longValue());
			AgreementBackingBean ctaBB = new AgreementBackingBean();
			ctaBB.getManagedBean().setContractId(
					agDataType.getContractId().longValue());
			ctaBB.getManagedBean().setOutComes(res.getAction());
			ctaBB.getManagedBean().setTaskId(taskId);
			ctaBB.getManagedBean().setToken(ctx);
			ctaBB.getManagedBean().setAgreementType(
					agDataType.getAgreementType());
			if (agDataType.getAgreementId().intValue() != 0) {
				ctReqDTO.setAgreementId(agDataType.getAgreementId().longValue());
				ContractAgreementResDTO resDTO = agreementSO
						.retrieveAgreement(ctReqDTO);
				ctaBB.getManagedBean().setAgreement(resDTO.getAgreement());
			}

		} else if (res.getProcessName().equalsIgnoreCase(
				"Contract Activation Suspension Request")) {

			CTContractSuspensionDataType ctsDataType = (CTContractSuspensionDataType) obj;

			ISuspendActiveSO suspendActiveSO = (ISuspendActiveSO) BusinessDelegate
					.getInstance().getServiceObject("ct",
							ISuspendActiveSO.class);
			IContractSO contractSO = (IContractSO) BusinessDelegate
					.getInstance().getServiceObject("ct", IContractSO.class);
			ContractSuspendReqDTO ctsReqDTO = new ContractSuspendReqDTO();
			ctsReqDTO.setContractId(new Long(ctsDataType.getContractId()));
			ctsReqDTO.setSuspendActiveId(new Long(ctsDataType
					.getSuspendActiveId()));
			ContractSuspendResDTO resDTO = suspendActiveSO
					.retrieveSuspension(ctsReqDTO);

			ContractSuspendBackingBean ctdBB = new ContractSuspendBackingBean();
			ctdBB.getManagedBean().setSuspendActive(resDTO.getSuspendActive());
			ctdBB.getManagedBean().setOutComes(res.getAction());
			ctdBB.getManagedBean().getContract()
					.setContractId(new Long(ctsDataType.getContractId()));

			ctdBB.getManagedBean().setTaskId(taskId);
			ctsReqDTO.setTaskId(taskId);

		} else if (res.getProcessName().equalsIgnoreCase(
				"Bank Account Change Request")) {

			CTBankAccountDataType ctsDataType = (CTBankAccountDataType) obj;

			IContractSupplierSO supplierSO = (IContractSupplierSO) BusinessDelegate
					.getInstance().getServiceObject("ct",
							IContractSupplierSO.class);
			ContractSupplierReqDTO ctsReqDTO = new ContractSupplierReqDTO();
			ctsReqDTO.setBankId(new Long(ctsDataType.getBankAccountId()));
			ctsReqDTO.setContractId(new Long(ctsDataType.getContractId()));
			ContractSupplierResDTO resDTO = supplierSO
					.retrieveBankAccRequest(ctsReqDTO);

			SupplierBackingBean ctSBB = new SupplierBackingBean();
			ctSBB.getManagedBean().setSupplierBank(resDTO.getSupplierBank());
			ctSBB.getManagedBean().setOutComes(res.getAction());
			ctSBB.getManagedBean().setTaskId(taskId);

			ctsReqDTO.setTaskId(taskId);

		} else if (res.getProcessName().equalsIgnoreCase(
				"Factoring Company Request")) {

			CTFactoringDataType ctsDataType = (CTFactoringDataType) obj;

			IContractSupplierSO factoringSO = (IContractSupplierSO) BusinessDelegate
					.getInstance().getServiceObject("ct",
							IContractSupplierSO.class);
			ContractSupplierReqDTO ctsReqDTO = new ContractSupplierReqDTO();
			ctsReqDTO.setFactoringId(new Long(ctsDataType.getFactoringId()));
			ctsReqDTO.setContractId(new Long(ctsDataType.getContractId()));
			ContractSupplierResDTO resDTO = factoringSO
					.retrieveFactoringRequest(ctsReqDTO);

			SupplierBackingBean ctSBB = new SupplierBackingBean();
			ctSBB.getManagedBean().setFactoring(resDTO.getFactoring());
			ctSBB.getManagedBean().setOutComes(res.getAction());
			ctSBB.getManagedBean().setTaskId(taskId);

			ctsReqDTO.setTaskId(taskId);

		} else if (res.getProcessName().equalsIgnoreCase(
				"Contract Amendment Request")) {

			CTContractAmendmentDataType dataType = (CTContractAmendmentDataType) obj;
			ContractDetailsBackingBean ctdBB = new ContractDetailsBackingBean();
//			ctdBB.taskListAmendmentAction(dataType.getAmendmentId().longValue(),
//					dataType.getContractId().longValue());
			ctdBB.getManagedBean().setOutComes(res.getAction());
			ctdBB.getManagedBean().setTaskId(taskId);
			ctdBB.getManagedBean().setToken(ctx);

		}else if (res.getProcessName().equalsIgnoreCase("Contract Termination")) {
			CTContractTerminationDataType dataType = (CTContractTerminationDataType) obj;
			ContractDetailsBackingBean ctdBB = new ContractDetailsBackingBean();
//			ctdBB.taskListTerminationAction(dataType.getTerminationId().longValue(), dataType.getContractId().longValue());
			ctdBB.getManagedBean().setOutComes(res.getAction());
			ctdBB.getManagedBean().setTaskId(taskId);
			ctdBB.getManagedBean().setToken(ctx);
		} else if (res.getProcessName().equalsIgnoreCase(
				"MOF Supplier Registration")) {
			SMMOFRegDataType smObj = (SMMOFRegDataType) obj;
			this.getManagedBean().setTaskName(res.getTaskName());
			if (res.getTaskName().equalsIgnoreCase(
					"Verify Supporting Documents")) {
				System.out
						.println("********Verify Supporting Documents********");
				System.out.println("********getTaskId********" + taskId);
				System.out.println("********getTaskName********"
						+ this.getManagedBean().getTaskName());
				SdoReviewBackingBean sdoReviewBB = new SdoReviewBackingBean();
				sdoReviewBB.getManagedBean().setAppId(
						Long.parseLong(smObj.getAppId()));
				sdoReviewBB.getManagedBean().setBpmAction(res.getAction());
				sdoReviewBB.getManagedBean().setTaskId(taskId);
			}
			if (res.getTaskName().equalsIgnoreCase(
					"Review Application (Escalated)")) {
				System.out
						.println("********Review Application Escalted for new mof********");
				System.out.println("********getTaskId********" + taskId);
				System.out.println("********getTaskName********"
						+ this.getManagedBean().getTaskName());
				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean
				setNavigationForSmDecision(smObj.getAppType());
				smApplBB.proceedToApplication(smObj.getAppType());
				smApplBB.getManagedBean().getSmAppl()
						.setApplId(Long.parseLong(smObj.getAppId()));
				smApplBB.getManagedBean().setBpmActions(res.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);

			}
		} else if (res.getProcessName()
				.equalsIgnoreCase("MOF Account Maintain")) {
			SMMOFAccMainDataType smObj = (SMMOFAccMainDataType) obj;
			this.getManagedBean().setTaskName(res.getTaskName());

			if (res.getTaskName().equalsIgnoreCase(
					"Review Application (Escalated)")) {
				System.out
						.println("********Review Application Escalted for account maintain********"
								+ smObj.getAppType());
				System.out.println("********getTaskId********" + taskId);
				System.out.println("********getTaskName********"
						+ this.getManagedBean().getTaskName());
				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean
				setNavigationForSmDecision(smObj.getAppType());
				smApplBB.proceedToApplication(smObj.getAppType());
				smApplBB.getManagedBean().getSmAppl()
						.setApplId(Long.parseLong(smObj.getAppId()));
				smApplBB.getManagedBean().setBpmActions(res.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);

			}
		} else if (res.getProcessName().equalsIgnoreCase(
				"Processing Officer Manage")) {
			// SMMOFRegDataType smObj = (SMMOFRegDataType) obj;
			SMProcsOfficeDataType smObj = (SMProcsOfficeDataType) obj;
			if (res.getTaskName().equalsIgnoreCase("Review Application")) {
				System.out.println("********Review Application********"
						+ smObj.getAppType());
				System.out.println("********getTaskId********" + taskId);
				System.out.println("********getTaskName********"
						+ this.getManagedBean().getTaskName());
				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean
				smApplBB.proceedToApplication(smObj.getAppType());
				setNavigationForSmDecision(smObj.getAppType());
				smApplBB.getManagedBean().getSmAppl()
						.setApplId(Long.parseLong(smObj.getAppId()));
				smApplBB.getManagedBean().setBpmActions(res.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				System.out.println(" The final result id ==== >>>"
						+ smApplBB.getManagedBean().getSmAppl().getApplId()
						+ "  and doc type ====>  "
						+ smApplBB.getManagedBean().getSmAppl()
								.getSupportingDocMode());
			}
		} else if (res.getProcessName().equalsIgnoreCase(
				"MOF Registration Cancellation")) {
			System.out
					.println("**********MOF Registration Cancellation***********"
							+ taskId);
			SMMOFRegCanDataType srcObj = (SMMOFRegCanDataType) obj;
			this.getManagedBean().setTaskName(res.getTaskName());
			if (res.getTaskName().equalsIgnoreCase("Review Cancellation")) {
				System.out.println("********Review Cancellation********");
				System.out.println("********Assign Site Visit APPID********"
						+ srcObj.getAppId());
				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean
				smApplBB.proceedToApplication("C");
				getManagedBean()
						.setPageUrl(
								"/xhtml/sm/approverDecisionPages/approverCanRegistration_DescisionPage.xhtml");
				smApplBB.getManagedBean().getSmAppl()
						.setApplId(Long.parseLong(srcObj.getAppId()));
				smApplBB.getManagedBean().setBpmActions(res.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
			}
		} else if (res.getProcessName().equalsIgnoreCase("Site Visit Manage")) {
			System.out.println("**********Site Visit Manage***********"
					+ taskId);
			SMSiteVisitDataType svObj = (SMSiteVisitDataType) obj;
			this.getManagedBean().setTaskName(res.getTaskName());
			if (res.getTaskName().equalsIgnoreCase("Assign Site Visit")) {
				System.out.println("********Assign Site Visit********");
				System.out.println("********Assign Site Visit APPID********"
						+ svObj.getAppId());
				SvcSiteAssignBackingBean svcBB = new SvcSiteAssignBackingBean();
				svcBB.getManagedBean().setAppId(
						(Long.parseLong(svObj.getAppId())));
				svcBB.getManagedBean().setBpmAction(res.getAction());
				svcBB.getManagedBean().setTaskId(taskId);
			} else if (res.getTaskName().equalsIgnoreCase(
					"Complete Site Visit Report")) {
				SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
				svoBB.getManagedBean().setAppId(
						(Long.parseLong(svObj.getAppId())));
				svoBB.getManagedBean().setBpmAction(res.getAction());
				svoBB.getManagedBean().setTaskId(taskId);
			}
		} else if (res.getProcessName().equalsIgnoreCase(
				"Circular Contract Maintenance")) {
			CTCircularContractDataType dataType = (CTCircularContractDataType) obj;
			if (dataType.getType().equalsIgnoreCase("Central")) {
				CentralContractBackingBean centralContractBB = new CentralContractBackingBean();
				centralContractBB.taskListAction(dataType.getCircularId()
						.longValue());
				centralContractBB.getManagedBean().setToken(ctx);
				centralContractBB.getManagedBean().setOutComes(res.getAction());
				centralContractBB.getManagedBean().setTaskId(taskId);
				getManagedBean()
						.setPageUrl(
								"/xhtml/ct/circular/centralcontract/circularBase.xhtml");
			} else {
				PanelBackingBean panelCircularBB = new PanelBackingBean();
				panelCircularBB.taskListAction(dataType.getCircularId()
						.longValue());
				panelCircularBB.getManagedBean().setToken(ctx);
				panelCircularBB.getManagedBean().setOutComes(res.getAction());
				panelCircularBB.getManagedBean().setTaskId(taskId);
				getManagedBean().setPageUrl(
						"/xhtml/ct/circular/panel/circularBase.xhtml");
			}
		} else if (res.getProcessName().equalsIgnoreCase(
				"Circular Contract Cancellation")) {
			CTCircularCancellationDataType dataType = (CTCircularCancellationDataType) obj;
			PanelBackingBean panelCircularBB = new PanelBackingBean();
			panelCircularBB.taskListCancelCircularAction(dataType
					.getCircularIds().get(0).longValue());
			panelCircularBB.getManagedBean().setToken(ctx);
			panelCircularBB.getManagedBean().setOutComes(res.getAction());
			panelCircularBB.getManagedBean().setTaskId(taskId);
		}

		return "taskListCommonPage";
	}

	private void setNavigationForSmDecision(String applType) {
		switch (applType) {
		case "N":
			getManagedBean().setPageUrl(
					"/xhtml/sm/NewMofApplication/mofRegBasePage.xhtml");
			break;
		case "A":
			getManagedBean()
					.setPageUrl(
							"/xhtml/sm/addAdditionalCategoryCodes/addAdditionalCategoryBasePage.xhtml");
			break;
		case "B":
			getManagedBean()
					.setPageUrl(
							"/xhtml/sm/bumiPuteraApplication/applyBumiStatusBasePage.xhtml");
			break;
		case "U":
			getManagedBean()
					.setPageUrl(
							"/xhtml/sm/updateMofDetails/updateMofDetailsBasePage.xhtml");
			break;
		case "R":
			getManagedBean().setPageUrl(
					"/xhtml/sm/renewMofApplication/renewMofBasePage.xhtml");
			break;
		default:
			break;
		}
	}

	private int calculateFinalOffSet(int currentPage) {

		int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;

		return offSet;
	}

	public void viewby(String viewBy) {
		System.out.println("viewBy:" + viewBy);

		BpmResponseDTO responseDTO = retrieveWorklistItems(this
				.getManagedBean().getStatus(), viewBy);

	}

	public void filterStatus() {

		BpmResponseDTO responseDTO = retrieveWorklistItems(this
				.getManagedBean().getStatus(), "");
	}

	public void tempFilterLogin() {

		BpmResponseDTO responseDTO = retrieveWorklistItems(this
				.getManagedBean().getStatus(), "");
	}

}
