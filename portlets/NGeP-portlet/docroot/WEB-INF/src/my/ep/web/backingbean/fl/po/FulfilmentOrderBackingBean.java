package my.ep.web.backingbean.fl.po;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.suspend.ISuspendActiveSO;
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
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.pp.budgetdata.PPBudgetDataType;
import my.ep.jaxb.pp.plandata.PPPlanDataType;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.ct.suspend.ContractSuspendBackingBean;
import my.ep.web.backingbean.fl.createcontract.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean;
import my.ep.web.backingbean.pp.procurementplan.PlanBackingBean;
import my.ep.web.managedbean.fl.po.FulfilmentOrderManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
//import my.ep.web.backingbean.fl.frn.CreateFRNBackingBean;

@ManagedBean(name="flOrderBB")
@RequestScoped
public class FulfilmentOrderBackingBean extends BaseBackingBean<FulfilmentOrderManagedBean>{
	
	public static final String NAV_TO_CREATE_DO = "createDo";
	public static final String NAV_TO_FL_ORDER = "flOrder";

	private static Logger log = Logger.getLogger(FulfilmentOrderBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );

	public FulfilmentOrderBackingBean(){
		retrievePendingDoItems();
		getDeliveryAddress();
		getDeliveryOrderStatus();

	}
	@PostConstruct
	public void init() {
		if (getManagedBean().getFulfilmentRequest() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().getFulfilmentRequest().getSmSupplier().setSupplierId(27L);
		}
	}
	public void fulfillmentOrderAction(String action) throws ParseException {
		IFulfilmentOrderSO fulfilmentOrderSO=null;
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlRemark flRemark = getManagedBean().getFlRemark();
			FlAttachment flAttachment = this.getManagedBean().getFlAttachment();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(27);
			flFulfilmentRequest.setSmSupplier(smSupplier);
			String fileName = this.getManagedBean().getFlAttachment().getFileName();
			String fileDesc = this.getManagedBean().getFlAttachment().getFileDescription();
			String filePath = this.getManagedBean().getFlAttachment().getFilePath();
			flAttachment.setDocType("txt");
			flAttachment.setDocId(Long.valueOf(44));
			flAttachment.setFileName(fileName);
			flAttachment.setFileDescription(fileDesc);
			flAttachment.setFilePath(filePath);
			//log.info("flRemark.getRemark() in backing beans::"+ flRemark.getRemark());
			flRemark.setDocId(Long.valueOf(1));
			flRemark.setDocType("tex");
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
			reqDTO.setFlRemark(flRemark);
			reqDTO.setFlAttachment(flAttachment);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setContractRequest(false);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			fulfilmentOrderSO.acknowledgeFulfillmentOrder(reqDTO);
			//System.out.println("resDTO.isSuccessful() in backing bean:: "+ resDTO.isSuccessful());
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentOrderSO = null;
		}
	}
	public FulfilmentResDTO retrievePendingDoItems() {
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		List<FlFulfilmentOrder> pendingDoList = filmentOrderSO.getPendingDoList();
		this.getManagedBean().setPendingDoList(pendingDoList);
		return resDTO;

	}
	public FulfilmentResDTO getDeliveryAddress() {
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		List<FlDeliveryAddress> deliveryAddressList = filmentOrderSO.getDeliveryAddressList();
		this.getManagedBean().setDeliveryAddressList(deliveryAddressList);
		return resDTO;
		
	}
	public FulfilmentResDTO getDeliveryOrderStatus() {
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		List<FlDeliveryOrder> deliveryOrderStatusList = filmentOrderSO.getDeliveryOrderStatusList();
		this.getManagedBean().setDeliveryOrderStatusList(deliveryOrderStatusList);
		return resDTO;
		
	}
	public String navigateToDO(int index) {
		DeliveryOrderBackingBean doMB=new DeliveryOrderBackingBean();
		FlFulfilmentOrder fulfilmentOrder=this.getManagedBean().getFulfilmentOrder();
		doMB.getManagedBean().setFulfilmentOrder(fulfilmentOrder);
		doMB.getManagedBean().setDeliveryAddress(this.getManagedBean().getDeliveryAddressList().get(index));
		//this.getManagedBean().setDeliveryAddress(this.getManagedBean().getDeliveryAddressList().get(index));
		return NAV_TO_CREATE_DO;

	}

	public String navigateToFlOrder(int index) {
		this.getManagedBean().setFulfilmentOrder(this.getManagedBean().getPendingDoList().get(index));
		return NAV_TO_FL_ORDER;

	}
}
