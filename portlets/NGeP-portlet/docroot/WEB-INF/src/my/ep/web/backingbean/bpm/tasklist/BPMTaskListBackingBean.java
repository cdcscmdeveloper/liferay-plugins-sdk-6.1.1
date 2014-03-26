package my.ep.web.backingbean.bpm.tasklist;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ct.ContractConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.FlProcessNameConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.jaxb.ct.agreement.CTAgreementDataType;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.bpm.tasklist.BPMTaskListManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "bpmTasklistBackingBean")
@RequestScoped
public class BPMTaskListBackingBean extends
		BaseBackingBean<BPMTaskListManagedBean> {
	
	public	static	final	String	DIRECT_PURCHASE_REVIEW_PATH	=	"review-directpurchase";
	public	static	final	String	DIRECT_PURCHASE_BPM_PROCESS_NAME = "Start DirectPurchase Creation";
	
	public	static	final	String	PIQ_BPM_PROCESS_NAME = "Purchase Inquiry Creation";
	
	
	

	private static Logger log = Logger.getLogger(BPMTaskListBackingBean.class);

	public BPMTaskListBackingBean() {
		this.getManagedBean().setBpmMap(null);
		Map temp = new HashMap();

		temp.put("UNSPSC Code Request", "review-unspsc-code");
		temp.put("Extension Code Request", "review-extension");
		temp.put("Extension Code Maintenance", "review-extension-maintenance");

		// Procurement Plan - Start
		temp.put("Plan Preparation", "review-plan");
		temp.put("Plan Revision", "revise-plan");
		temp.put("Budget Endorsement", "review-budget");
		// Procurement Plan - End

		// Supplier Management - Start
		temp.put("Disciplinary Action", "disciplinary-action");
		temp.put("Site Visit Manage", "site-visit");
		temp.put("MOF Supplier Registration", "mof-application");
		temp.put("MOF Account Maintain", "mof-application");
		temp.put("MOF Registration Cancellation","mof-application");
		temp.put(DIRECT_PURCHASE_BPM_PROCESS_NAME, DIRECT_PURCHASE_REVIEW_PATH);
		temp.put("Purchase Review", DIRECT_PURCHASE_REVIEW_PATH);
		temp.put("Purchase Inquiry Creation", DIRECT_PURCHASE_REVIEW_PATH);
		temp.put("Purchase In Default Creation", DIRECT_PURCHASE_REVIEW_PATH);
		

		// QuationTender
		temp.put("MITI Approval", "qtmaintanance");
		temp.put("Budget Approval", "qtmaintanance");
		temp.put("ICT Approval", "qtmaintanance");
		temp.put("BPK Approval", "qtmaintanance");
		temp.put("QuotationTender Creation", "qtreview");

		temp.put("Specification Preparation", "qtspecprepartaion");
		temp.put("View Preparation", "qtspecprepartaion");
		temp.put("Site Visit", "sitevisit");
		temp.put("Proposal Preparation","supplierproposal");

		temp.put(CTProcessNameConst.CIRCULAR_CANCELLATION_PROCESS_NAME, "cancel-circular");
		temp.put(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME+CTProcessNameConst.ACK_FACTORING_REQUEST_HT_NAME, "review-factoring-request");
		temp.put(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME+CTProcessNameConst.ENDORSE_FACTORING_REQUEST_HT_NAME, "review-factoring-request");
		temp.put("Contract Creation", "fulfilment-details");
		temp.put("Contract Activation Suspension Request", "contract-suspension");
		temp.put("Contract Amendment Request", "contract-amendment");
		temp.put("Contract Termination", "contract-termination");
		temp.put(CTProcessNameConst.AGREEMENT_CREATION_PROCESS_NAME, "contract-agreement");
		temp.put(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME+CTProcessNameConst.REVIEW_CB_REQUEST_HT_NAME, "change-bank-account");

		//Fulfilment Management - Start
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVIEW_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVISE_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_NORMAL_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVISE_MTO_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.REVIEW_MTO_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_NEW_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.INTIATE_MTO_CR_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME+FlProcessNameConst.CREATE_NEW_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME+FlProcessNameConst.CREATE_NORMAL_CO_HT, "contract-request");
		temp.put(FlProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME+FlProcessNameConst.CREATE_NEW_MTO_CO_HT, "contract-request");

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

		temp.put("Service QuotationTender Creation", "supplierpp");
		temp.put("Committee Approval", "committeMaintance");
		
		this.getManagedBean().setBpmMap(temp);

		System.out.println("testing BPM******");

		String viewBy = "";
		try{
			BpmResponseDTO responseDTO = retrieveWorklistItems("ASSIGNED",this.getManagedBean().getViewBy());
		}catch(Exception e){
			log.info("BPM Errors");
			e.printStackTrace();
		}
	}

	public BpmResponseDTO retrieveWorklistItems(String status, String viewBy) {

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		// String token = bpmSO.authenticate("roslan", "welcome1");
		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();

		System.out.println("testing BPM auth******" + token);
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

	public void navigateToURL() throws PortalException, SystemException {

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		String taskId = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("taskId");
		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		try {
			log.info("taskId:" + taskId);
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			System.out.println("********getProcessName********"
					+ res.getProcessName());
			System.out.println("********res.getAction()********"
					+ res.getAction());
			System.out.println("********getTaskName********"
					+ res.getTaskName());
			this.getManagedBean().setProcessName(res.getProcessName());
			this.getManagedBean().setAction(res.getAction());
			Object obj = res.getPojo();

			String url = "";

			if (!this.getManagedBean().getBpmMap().isEmpty()
					&& this.getManagedBean().getBpmMap().size() > 0) {

				url = (String) this.getManagedBean().getBpmMap()
						.get(res.getProcessName());
				if (url == null || url.isEmpty()) {
					url = (String) this.getManagedBean().getBpmMap()
							.get(res.getProcessName() + res.getTaskName());
				}

			}
			log.info("url:" + url);
			
			if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.CIRCULAR_MAINTENANCE_PROCESS_NAME)) {
				CTCircularContractDataType dataType = (CTCircularContractDataType) obj;
				if(dataType.getType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT)) {
					url = "central-contract-circular";
				} else {
					url = "panel-circular";
				}
				
			}
			
			// TODO
			if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME) ||
			   res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME) ||
			   res.getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME) || 
			   res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) ||
					res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)) {
				url = "review-amendment";
			}
			
			if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)) {
				url = "review-amendment";
			}
			
			FacesScopes facesScope = new FacesScopes();
			ThemeDisplay themeDisplay = (ThemeDisplay)facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
			
			System.out.println("getURLCurrent"+themeDisplay.getURLCurrent());
			
			String commonUrl = themeDisplay.getURLCurrent().substring(0, themeDisplay.getURLCurrent().indexOf(themeDisplay.getLayout().getGroup().getFriendlyURL())+themeDisplay.getLayout().getGroup().getFriendlyURL().length());
			System.out.println("commonUrl"+commonUrl);
			
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			//externalContext.redirect("/web/guest/" +url +"?taskId="+taskId);
			externalContext.redirect(commonUrl+"/" + url+"?taskId="+taskId);
			//externalContext.redirect("http://localhost:8080/web/guest/" +url +"?taskId="+taskId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int calculateFinalOffSet(int currentPage) {

		int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;

		return offSet;
	}

	public void filterViewby() {
		System.out.println("this.getManagedBean().getViewBy()"+this.getManagedBean().getViewBy());
		BpmResponseDTO responseDTO = retrieveWorklistItems("ASSIGNED",this.getManagedBean().getViewBy());

	}

/*	public void filterStatus() {

		BpmResponseDTO responseDTO = retrieveWorklistItems("ASSIGNED" ,this
				.getManagedBean().getStatus());
	}*/

/*	public void tempFilterLogin() {

		BpmResponseDTO responseDTO = retrieveWorklistItems("ASSIGNED" ,this
				.getManagedBean().getStatus());
	}
*/
	public String backToTaskList() {

		System.out.println("*******backToTaskList*******");

		return "/xhtml/bpmtasklist/taskListView";
	}
	
	
	public String applicationStatus(Integer docInt){
		IStatusSO statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject("pm",IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		
		StatusReqDTO reqDTO = new StatusReqDTO();
		
		reqDTO.setLanguageCode(languageCode);
		reqDTO.setStatusId(docInt.longValue()); 
		StatusResDTO resDTO = statusSO.retrieveStatusDescById(reqDTO);
		
		
		System.out.println("docInt:"+docInt);
		
		return resDTO.getPmStatusDesc()!=null?resDTO.getPmStatusDesc().getStatusName():"Review";
	}
	
	public void refreshTaskList(){
		
		BpmResponseDTO responseDTO = retrieveWorklistItems("ASSIGNED",this.getManagedBean().getViewBy());
	}
	

}
