package my.ep.web.backingbean.fl.pendingdo;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.CreateDOBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.managedbean.fl.creatdo.CreateDOManagedBean;
import my.ep.web.managedbean.fl.pendingdo.PendingDoListManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "pendingListBackingBean")
@RequestScoped
public class PendingDoListBackingBean extends BaseBackingBean<CreateDOManagedBean> {
	public static final String NAV_TO_CREATE_DO = "createDo";
	private static Logger log = Logger.getLogger(PendingDoListBackingBean.class);

	public PendingDoListBackingBean() {
		FulfilmentResDTO resDTO = retrievePendingDoItems();
	}
	public FulfilmentResDTO retrievePendingDoItems() {

		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);

		// String token = bpmSO.authenticate("roslan", "welcome1");
		// String token = bpmSO.authenticate("khai", "welcome1");
		// String token = bpmSO.authenticate("lailah", "welcome1");
		// String token = bpmSO.authenticate("alex", "welcome1");
		// System.out.println("testing BPM auth******"+token);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		// reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
		// reqDTO.setOffset(calculateFinalOffSet(1));
		// reqDTO.setState(status);
		// reqDTO.setViewBy(viewBy);
		// reqDTO.setCtxToken(token);
		List<FlFulfilmentOrder> pendingDoList = filmentOrderSO.getPendingDoList();
		this.getManagedBean().setPendingDoList(pendingDoList);
		return resDTO;

	}

	public String navigateToDO() {
		String fulfilmentOrderId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fulfilmentOrderId");
		String statusId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusId");
		String fulfilmentReqId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fulfilmentReqId");
		//CreateDOBackingBean dOBackingBean = new CreateDOBackingBean();
		this.getManagedBean().setFulfilmentOrderId(fulfilmentOrderId);
		this.getManagedBean().setStatusId(statusId);
		this.getManagedBean().setFulfillmentReqId(fulfilmentReqId);
		return NAV_TO_CREATE_DO;
		
		//return "taskListCommonPage";

	}

}
