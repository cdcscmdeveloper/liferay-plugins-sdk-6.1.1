package my.ep.web.backingbean.bpm.tasklist;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constants.FlProcessNameConst;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.bpm.tasklist.BPMTaskListManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;

@ManagedBean(name="bpmTasklistBackingBean")
@RequestScoped
public class BPMTaskListBackingBean extends BaseBackingBean<BPMTaskListManagedBean>{

	private static Logger log = Logger.getLogger(BPMTaskListBackingBean.class);

	public BPMTaskListBackingBean(){
		this.getManagedBean().setBpmMap(null);
		Map temp = new HashMap();

		temp.put("UNSPSC Code Request", "review-unspsc-code");
		temp.put("Extension Code Request", "review-extension");
		temp.put("Extension Code Maintenance", "review-extension-maintenance");

		//Procurement Plan - Start
		temp.put("Plan Preparation", "review-plan");
		temp.put("Plan Revision", "revise-plan");
		temp.put("Budget Endorsement", "review-budget");
		//Procurement Plan - End

		//Supplier Management - Start
		temp.put("Disciplinary Action", "disciplinary-action");
		temp.put("Site Visit Manage", "site-visit");
		temp.put("MOF Supplier Registration", "mof-application");
		temp.put("MOF Account Maintain", "mof-application");
		temp.put("DirectPurchase Creation", "review-directpuchase");
		temp.put("Supplier Recommendation", "review-directpuchase");

		//QuationTender 
		temp.put("MITI Approval", "qtmaintanance");
		temp.put("Budget Approval", "qtmaintanance");
		temp.put("ICT Approval", "qtmaintanance");
		temp.put("BPK Approval", "qtmaintanance");
		temp.put("QuotationTender Creation", "qtreview");

		temp.put("Specification Preparation", "qtsprecprepartaion");
		temp.put("View Preparation", "qtsprecprepartaion");

		temp.put("Circular Contract Maintenance", "create-central-contract-circular");
		temp.put("Circular Contract Maintenance", "create-panel-circular");
		temp.put("Circular Contract Maintenance", "central-contract-circular-maintenance");
		temp.put("Circular Contract Maintenance", "panel-circular-maintenance");
		temp.put("Circular Contract Cancellation", "cancel-circular");
		temp.put("Factoring Company Request", "request-factoring");
		temp.put("Contract Creation", "fulfilment-details");
		temp.put("Contract Activation Suspension Request", "contract-maintenance");
		temp.put("Contract Amendment Request", "contract-maintenance");
		temp.put("Contract Termination", "contract-maintenance");
		temp.put("Agreement Creation", "agreement");
		temp.put("Agreement Creation", "supplemental-agreement");

		//Fulfilment Management - Start
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVIEW_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVISE_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_NORMAL_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVISE_MTO_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVIEW_MTO_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_NEW_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.INTIATE_MTO_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_NEW_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME+FlProcessNameConst.CREATE_NEW_CO_HT, "contract-request");

		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_SI_HT, "shipping-instruction");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVIEW_SI_HT, "shipping-instruction");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVISE_SI_HT, "shipping-instruction");

		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.MTO_CO_ACK_HT, "contract-order");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CO_ACK_HT, "contract-order");

		temp.put(FlProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_INVOICE, "invoice");
		temp.put(FlProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME+FlProcessNameConst.PERFORM_PAYMNET_MATCH_HT, "payment-match");

		temp.put(FlProcessNameConst.DELIVERY_ORDER_FULFILMENT_PROCESS_NAME, "fulfilment-received-note");
		temp.put(FlProcessNameConst.DAN_GENERATION_PROCESS_NAME, "debit-adjustment-note");
		temp.put(FlProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME, "contract-order");

		temp.put(FlProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_PR_HT, "purchase-request");
		temp.put(FlProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVIEW_PR_HT, "purchase-request");
		temp.put(FlProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVISE_PR_HT, "purchase-request");
		temp.put(FlProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.PR_ACK_HT, "purchase-order");
		temp.put(FlProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVIEW_REJECTED_PO_HT, "purchase-order");


		this.getManagedBean().setBpmMap(temp);


		System.out.println("testing BPM******");

		String viewBy = "";
		try{
			BpmResponseDTO responseDTO = retrieveWorklistItems(this.getManagedBean().getStatus(), viewBy);
		}catch(Exception e){
			log.info("BPM Errors");
			e.printStackTrace();
		}
	}


	public BpmResponseDTO retrieveWorklistItems(String status, String viewBy){



		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();

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


	public void navigateToURL(){

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		try {
			log.info("taskId:"+taskId);
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			System.out.println("********getProcessName********"+res.getProcessName());
			System.out.println("********getTaskName********"+res.getTaskName());
			System.out.println("********res.getAction()********"+res.getAction());
			this.getManagedBean().setProcessName(res.getProcessName());
			this.getManagedBean().setAction(res.getAction());
			Object obj = res.getPojo();



			String url = "";

			if(!this.getManagedBean().getBpmMap().isEmpty() && this.getManagedBean().getBpmMap().size() > 0 ){ 

				url= (String)this.getManagedBean().getBpmMap().get(res.getProcessName());
				if(url == null || url.isEmpty()){
					url= (String)this.getManagedBean().getBpmMap().get(res.getProcessName()+res.getTaskName());
				}

			}
			log.info("url:"+url);




			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			externalContext.redirect("/web/guest/" +url +"?taskId="+taskId);
//			externalContext.redirect("/web/dashboard/" +url +"?taskId="+taskId);
			//externalContext.redirect("http://localhost:8080/web/guest/" +url +"?taskId="+taskId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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




	public String backToTaskList(){

		System.out.println("*******backToTaskList*******");

		return "/xhtml/bpmtasklist/taskListView";
	}

}
