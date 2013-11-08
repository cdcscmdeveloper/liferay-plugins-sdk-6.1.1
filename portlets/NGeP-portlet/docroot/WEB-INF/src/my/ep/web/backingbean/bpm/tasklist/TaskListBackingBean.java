package my.ep.web.backingbean.bpm.tasklist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.ct.IContractSO;
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
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
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
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.jaxb.fl.dofullfillmentdata.FLDOFullfillmentDataType;
import my.ep.jaxb.fl.invoicepaymentdata.FLInvoiceAndPaymentDataType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.pp.budgetdata.PPBudgetDataType;
import my.ep.jaxb.pp.plandata.PPPlanDataType;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.ct.suspend.ContractSuspendBackingBean;
import my.ep.web.backingbean.fl.createcontract.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.dan.DebitAdjustmentNoteBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.paymentmatch.PaymentMatchBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.backingbean.fl.shippingInstr.ShippingInstrBackingBean;
import my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean;
import my.ep.web.backingbean.pp.procurementplan.PlanBackingBean;
import my.ep.web.backingbean.sm.sdoreview.SdoReviewBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvcSiteAssignBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.bpm.tasklist.TaskListManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name="tasklistBackingBean")
@RequestScoped
public class TaskListBackingBean extends BaseBackingBean<TaskListManagedBean>{

	private static Logger log = Logger.getLogger(TaskListBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );



	public TaskListBackingBean(){

		System.out.println("testing BPM******");

		String viewBy = "";
		try{
			BpmResponseDTO responseDTO = retrieveWorklistItems(this.getManagedBean().getStatus(), viewBy);
		}catch(Exception e){
			log.info("BPM Errors");
			e.printStackTrace();
		}
	}



	public BpmResponseDTO initiateTask(){
		System.out.println("initiateTask******");
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setProcessId("Codification.RequestUNSPSC");
		reqDTO.setOutcome("SUBMIT");
		CDRequestDataType obj = new CDRequestDataType();
	
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);

		return res;
	}

	public BpmResponseDTO retrieveWorklistItems(String status, String viewBy){



		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		//String token = bpmSO.authenticate("roslan", "welcome1");
		String token = bpmSO.authenticate(this.getManagedBean().getTempLoginId(), "welcome1");
		System.out.println("testing BPM auth******"+token);
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
		reqDTO.setOffset(calculateFinalOffSet(1));
		reqDTO.setState(status);
		reqDTO.setViewBy(viewBy);
		reqDTO.setCtxToken(token);
		BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);

		System.out.println("testing BPM******"+responseDTO.getWorklistVO().getWorklistItem().size());



		this.getManagedBean().setTaskList(responseDTO.getWorklistVO().getWorklistItem());




		return responseDTO;

	}

	public String navigateToURL(){

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		String urlKey = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("key");
		String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
		String ctx = bpmSO.authenticate(this.getManagedBean().getTempLoginId(),"welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();


		log.info("taskId:"+taskId);
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		System.out.println("********getProcessName********"+res.getProcessName());
		System.out.println("********res.getAction()********"+res.getAction());
		System.out.println("********res.getTaskName()()********"+res.getTaskName());
		
		this.getManagedBean().setProcessName(res.getProcessName());
		this.getManagedBean().setTaskName(res.getTaskName());
		this.getManagedBean().setAction(res.getAction());
		Object obj = res.getPojo();
		this.getManagedBean().setTaskId(taskId);

		if(res.getProcessName().equalsIgnoreCase("UNSPSC Code Request")){

			CDRequestDataType unspsc = (CDRequestDataType)obj;

			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);

			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			unspscBB.getManagedBean().setRequestCode(resDTO.getRequestCode());


		}else if(res.getProcessName().equalsIgnoreCase("Extension Code Request")){
			CDRequestDataType unspsc = (CDRequestDataType)obj;

			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
			ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			CodificationReqDTO reqCodeDTO = new CodificationReqDTO();
			RequestCodeResDTO resDTO = null;
			unspscBB.getManagedBean().setReviewExtCodeList(null);
			for(String itemId:unspsc.getItemId()){
				System.out.println("itemId:"+itemId);
				codeDTO.setRequestId(new Long(itemId));
				resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
				unspscBB.getManagedBean().getReviewExtCodeList().add(resDTO.getRequestCode());
			}

			reqCodeDTO.setUnspscCode(resDTO.getRequestCode().getTempCode());
			CodificationResDTO resCodeDTO = codificationSO.searchCommodityObj(reqCodeDTO);

			unspscBB.getManagedBean().setTypeList(resCodeDTO.getCommodityObj().getCmType());
			unspscBB.getManagedBean().setBrandList(resCodeDTO.getCommodityObj().getCmBrand());		
			unspscBB.getManagedBean().setMeasurementList(resCodeDTO.getCommodityObj().getCmMeasurement());
			resCodeDTO = codificationSO.searchColor(reqCodeDTO);
			unspscBB.getManagedBean().setColorList(resCodeDTO.getColorList());


		}else if(res.getProcessName().equalsIgnoreCase("Extension Code Maintenance")){
			CDRequestDataType unspsc = (CDRequestDataType)obj;

			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);

			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			unspscBB.getManagedBean().setRequestCode(resDTO.getRequestCode());
			
			
		}else if(res.getProcessName().equalsIgnoreCase("Intiate Normal Contract Order")|| res.getProcessName().equalsIgnoreCase("Create MTO Contract Order")){
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			IShippingInstructionSO shippingSO = (IShippingInstructionSO) BusinessDelegate.getInstance().getServiceObject("fl", IShippingInstructionSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();

			FLContractOrderDataType coObj = (FLContractOrderDataType)obj;
			System.out.println("coObj.getContractRequestId(): "+coObj.getContractRequestId());
			flReqDTO.setFlRequestId(coObj.getContractRequestId());
			if(getManagedBean().getTaskName().equalsIgnoreCase("Create Shipping Instructions") || getManagedBean().getTaskName().equalsIgnoreCase("Review Shipping Instrctions")){
				resDTO = shippingSO.retrieveShippingInstruction(flReqDTO);
			}else{
				resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);
			}

			if(getManagedBean().getTaskName().equalsIgnoreCase("Create Shipping Instructions") || getManagedBean().getTaskName().equalsIgnoreCase("Review Shipping Instrctions")){
				ShippingInstrBackingBean siBB = new ShippingInstrBackingBean();
				siBB.getManagedBean().setShippingInstr(resDTO.getShippingInstr());
				siBB.getManagedBean().setBpmTaskId(taskId);
				siBB.getManagedBean().setActions(res.getAction());
			}else{
				ContractRequestBackingBean crBB = new ContractRequestBackingBean();

				crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());

				IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
				orgProfileReqDTO.setOrgProfileId(4L);// set current login user org profile ID
				OrgProfileResDTO pmResDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);
				if(pmResDTO.getPmOrgProfile() != null){
					crBB.getManagedBean().getFlRequest().setCreatedOrgProfile(pmResDTO.getPmOrgProfile());
				}
				crBB.getManagedBean().setBpmTaskId(taskId);
				crBB.getManagedBean().setActions(res.getAction());
			}

		}else if(res.getProcessName().equalsIgnoreCase("Create Contract Order")){
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLContractOrderDataType coObj = (FLContractOrderDataType)obj;
				if(coObj.getContractRequestId().intValue() == 0){
					flReqDTO.setFlRequestId(coObj.getParentContractRequestId());
				}else{
					flReqDTO.setFlRequestId(coObj.getContractRequestId());
				}

				resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);
				if(res.getTaskName().equals("Create Normal Comntract Order") || res.getTaskName().equals("Review Contract Order")){
				ContractRequestBackingBean crBB = new ContractRequestBackingBean();
				IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
				orgProfileReqDTO.setOrgProfileId(4L);// set current login user org profile ID
				OrgProfileResDTO pmResDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);

				if(coObj.getContractRequestId().intValue() == 0){
					FlFulfilmentRequest flRequest = new FlFulfilmentRequest();
					System.out.println("contractid: "+resDTO.getFlFulfilmentRequest().getCtContract().getParentContract().getContractId());
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
				if(pmResDTO.getPmOrgProfile() != null){
					crBB.getManagedBean().getFlRequest().setCreatedOrgProfile(pmResDTO.getPmOrgProfile());
				}
			}else if(res.getTaskName().equals("Acknowledg Contract Order")){
				FulfilmentOrderBackingBean florderBB  = new FulfilmentOrderBackingBean();
				florderBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
				florderBB.getManagedBean().setTaskId(taskId);
				florderBB.getManagedBean().setActions(res.getAction());
				florderBB.getManagedBean().setContractRequest(Boolean.TRUE);
			}
		
		}else if(res.getProcessName().equalsIgnoreCase("Create Invoice and Payment")){
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLInvoiceAndPaymentDataType paymentObj = (FLInvoiceAndPaymentDataType)obj;
			flReqDTO.setFlOrderId(paymentObj.getFullfillmentRequestId());
			if((res.getTaskName().equalsIgnoreCase("Create Invoice"))){	
				System.out.println("--Create Invoice--");
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
			if((res.getTaskName().equalsIgnoreCase("Perform Payment Match"))){
				System.out.println("--Perform Payment Match--");
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
		else if(res.getProcessName().equalsIgnoreCase("Delvery Order Fullfillment")){
			System.out.println("in Task List Delvery Order: ");
			ReceivedNoteBackingBean createFRNBackingBean = new ReceivedNoteBackingBean();  
			FLDOFullfillmentDataType flDeliveryOrder = (FLDOFullfillmentDataType)obj;
			createFRNBackingBean.getManagedBean().setDeliveryOrderId(flDeliveryOrder.getDeliveryOrderId());
			createFRNBackingBean.getManagedBean().setTaskId(taskId);
			createFRNBackingBean.getManagedBean().setActions(res.getAction());
			
		}else if(res.getProcessName().equalsIgnoreCase("Perform DAN")){

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLDanDataType danObj = (FLDanDataType)obj;
			flReqDTO.setDanId(danObj.getDanId());
				System.out.println("--Perform DAN--");
				IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject("fl",IAdjustmentSO.class);

				resDTO = adjustmentSO.retrieveAdjustmentById(flReqDTO);
				FlAdjustment adjust = resDTO.getAdjustment();

				DebitAdjustmentNoteBackingBean danBB = new DebitAdjustmentNoteBackingBean();
				danBB.getManagedBean().setAdjustment(adjust);
				danBB.getManagedBean().setBpmTaskId(taskId);
				danBB.getManagedBean().setActions(res.getAction());


		}
		else if(res.getProcessName().equalsIgnoreCase("Create Purchase Order")){
			System.out.println("in Task List Create Purchase Order");
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FulfilmentReqDTO fulfilmentReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			PurchaseRequestBackingBean purchaseRequestBackingBean=new PurchaseRequestBackingBean();  
			FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType)obj;
			System.out.println("Id in Task List:: "+flPurchaseOrd.getPurchaseRequestId());
			FlFulfilmentRequest flFulfilmentRequest=new FlFulfilmentRequest();
			flFulfilmentRequest.setFulfilmentReqId(Long.valueOf(flPurchaseOrd.getPurchaseRequestId()));
//			fulfilmentReqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			fulfilmentReqDTO.setFlRequestId(flPurchaseOrd.getPurchaseRequestId());
			resDTO=fulfilmentSO.retrieveFulfilmentRequest(fulfilmentReqDTO);
			purchaseRequestBackingBean.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			purchaseRequestBackingBean.getManagedBean().setTaskId(taskId);
			System.out.println("Action In task list"+res.getAction());
			purchaseRequestBackingBean.getManagedBean().setActions(res.getAction());
			purchaseRequestBackingBean.getManagedBean().setTaskName(res.getTaskName());
			if(res.getTaskName().equals("Acknowledge the Purchase Request")){
				FulfilmentOrderBackingBean florderBB=new FulfilmentOrderBackingBean();
				florderBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
				florderBB.getManagedBean().setTaskId(taskId);
				florderBB.getManagedBean().setActions(res.getAction());
			}
			
			
		}else if(res.getProcessName().equalsIgnoreCase("Budget Endorsement")){

			//Procurement Plan - Budget
			PPBudgetDataType budgetObj = (PPBudgetDataType)obj;
			
			BudgetAllocationReqDTO budgetReqDTO = new BudgetAllocationReqDTO();
			budgetReqDTO.setBudgetAllocId(Long.parseLong(budgetObj.getBudgetId()));
			IBudgetAllocationSO budgetSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject("pp", IBudgetAllocationSO.class);
			BudgetAllocationResDTO budgetResSO = budgetSO.retrieveBudgetAllocation(budgetReqDTO);

			BudgetAllocationBackingBean budgetBB = new BudgetAllocationBackingBean();
			budgetBB.getManagedBean().setLoginUser(this.getManagedBean().getTempLoginId());
			budgetBB.getManagedBean().setBpmTaskId(taskId);
			budgetBB.getManagedBean().setActions(res.getAction());
			budgetBB.loadTask(budgetResSO.getBudgetAlloc(), res.getTaskName());

			
		}else if(res.getProcessName().equalsIgnoreCase("Plan Preparation")){
			//Procurement Plan - Plan
			PPPlanDataType planObj = (PPPlanDataType)obj;
			
			ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
			planReqDTO.setPlanId(Long.parseLong(planObj.getPlanId()));
			IProcurementPlanSO planSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject("pp", IProcurementPlanSO.class);
			ProcurementPlanResDTO planResDTO = planSO.retrievePlanById(planReqDTO);

			PlanBackingBean planBB = new PlanBackingBean();
			planBB.getManagedBean().setLoginUser(this.getManagedBean().getTempLoginId());
			planBB.getManagedBean().setBpmTaskId(taskId);
			planBB.getManagedBean().setActions(res.getAction());
			planBB.loadTask(planResDTO.getPlan(), res.getTaskName());
		
		}else if(res.getProcessName().equalsIgnoreCase("Contract Creation")){
				
	        	CTContractDataType ctDataType = (CTContractDataType)obj;

				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
				ContractReqDTO ctReqDTO = new ContractReqDTO();
				ctReqDTO.setContractId(new Long(ctDataType.getContractId()));
				ContractResDTO resDTO = contractSO.retrieveContract(ctReqDTO);
				
				ContractDetailsBackingBean ctdBB = new ContractDetailsBackingBean();
				ctdBB.getManagedBean().setCtContract(resDTO.getContract());
				ctdBB.getManagedBean().setOutComes(res.getAction());
				
				ctdBB.getManagedBean().setTaskId(taskId);
				ctReqDTO.setTaskId(taskId);
				System.out.println("================Contract Id=================" +ctDataType.getContractId());
				System.out.println("====================task name====================" +res.getTaskName());
				System.out.println("====================taskId======================="+taskId);

			}else if(res.getProcessName().equalsIgnoreCase("Contract Activation Suspension Request")){
				
	        	 CTContractSuspensionDataType ctsDataType = (CTContractSuspensionDataType)obj;

	        	ISuspendActiveSO suspendActiveSO = (ISuspendActiveSO) BusinessDelegate.getInstance().getServiceObject("ct",ISuspendActiveSO.class);
				ContractSuspendReqDTO ctsReqDTO = new ContractSuspendReqDTO();
				ctsReqDTO.setContractId(new Long(ctsDataType.getContractId()));
				ContractSuspendResDTO resDTO = suspendActiveSO.retrieveSuspension(ctsReqDTO);
				
				ContractSuspendBackingBean ctdBB = new ContractSuspendBackingBean();
				ctdBB.getManagedBean().setContract(resDTO.getContract());
				ctdBB.getManagedBean().setOutComes(res.getAction());
				
				ctdBB.getManagedBean().setTaskId(taskId);
				ctsReqDTO.setTaskId(taskId);
				System.out.println("================Contract Id=================" +ctsReqDTO.getContractId());
				System.out.println("====================task name====================" +res.getTaskName());
				System.out.println("====================taskId======================="+taskId);

			}else if (res.getProcessName().equalsIgnoreCase(
					"MOF Supplier Registration")) {
				SMMOFRegDataType smObj = (SMMOFRegDataType) obj;
				this.getManagedBean().setTaskName(res.getTaskName());
				if (res.getTaskName().equalsIgnoreCase(
						"Verify Supporting Documents")) {
					System.out.println("********Verify Supporting Documents********");
					System.out.println("********getTaskId********" + taskId);
					System.out.println("********getTaskName********" + this.getManagedBean().getTaskName());
					SdoReviewBackingBean sdoReviewBB = new SdoReviewBackingBean();
					sdoReviewBB.getManagedBean().setAppId(
							Long.parseLong(smObj.getAppId()));
					sdoReviewBB.getManagedBean().setBpmAction(res.getAction());
					sdoReviewBB.getManagedBean().setTaskId(taskId);
				}
			} else if (res.getProcessName().equalsIgnoreCase("Site Visit Manage")) {
				System.out.println("**********Site Visit Manage***********" + taskId);
				SMSiteVisitDataType svObj = (SMSiteVisitDataType) obj;
				this.getManagedBean().setTaskName(res.getTaskName());
				if (res.getTaskName().equalsIgnoreCase("Assign Site Visit")) {
					System.out.println("********Assign Site Visit********");
					System.out.println("********Assign Site Visit APPID********" + svObj.getAppId());
					SvcSiteAssignBackingBean svcBB = new SvcSiteAssignBackingBean();
					svcBB.getManagedBean().setAppId(
							(Long.parseLong(svObj.getAppId())));
					svcBB.getManagedBean().setBpmAction(res.getAction());
					svcBB.getManagedBean().setTaskId(taskId);
				}else if(res.getTaskName().equalsIgnoreCase("Complete Site Visit Report")) {
					SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
					svoBB.getManagedBean().setAppId(
							(Long.parseLong(svObj.getAppId())));
					svoBB.getManagedBean().setBpmAction(res.getAction());
					svoBB.getManagedBean().setTaskId(taskId);
				}
			}
			



		return "taskListCommonPage";
	}

	private int calculateFinalOffSet(int currentPage){

		int offSet = (currentPage-1) *PagingConst.ROWS_PER_PAGE;

		return offSet;
	}

	public void viewby(String viewBy){
		System.out.println("viewBy:"+viewBy);

		BpmResponseDTO responseDTO = retrieveWorklistItems(this.getManagedBean().getStatus(), viewBy);


	}

	public void filterStatus(){

		BpmResponseDTO responseDTO = retrieveWorklistItems(this.getManagedBean().getStatus(), "");
	}
	
	public void tempFilterLogin(){

		BpmResponseDTO responseDTO = retrieveWorklistItems(this.getManagedBean().getStatus(), "");
	}


}
