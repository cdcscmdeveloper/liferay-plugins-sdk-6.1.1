package my.ep.web.backingbean.bpm.tasklist;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.cc.dashsummary.IDashSummarySO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.cc.dashsummary.BpmTaskStatusDTO;
import my.ep.dto.cc.dashsummary.BpmTaskSummaryDTO;
import my.ep.dto.cc.dashsummary.DashSummaryReqDTO;
import my.ep.dto.cc.dashsummary.DashSummaryResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.bpm.tasklist.BPMTaskDashSumManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "bpmTaskDashSumBackingBean")
@RequestScoped
public class BPMTaskDashSumBackingBean extends
		BaseBackingBean<BPMTaskDashSumManagedBean> {

	private static Logger log = Logger
			.getLogger(BPMTaskDashSumBackingBean.class);
	private long totalTask = 0;
	private List<BpmTaskSummaryDTO> dashboardSummary = new ArrayList<BpmTaskSummaryDTO>();
	private int pendingDeliveryListNum;
	private long pendingApprovalItemNum;
	private int pendingMOFSubmissionNum;
	private long pendingCodifiedItemsNum;
	private long pendingPublishedItemsNum;
	private long rejectedItemsNum;
	private ISearchBean<PmParameterDesc> docTypeSearchBean;
	private MenuModel model;
	
    
	public BPMTaskDashSumBackingBean() {
		super();
		createTaskBarModel();
		model = new DefaultMenuModel();
		createMenu();
	}

	private void createTaskBarModel() {
		IDashSummarySO dashSummarySO = null;
		try {

			dashSummarySO = (IDashSummarySO) BusinessDelegate.getInstance()
					.getServiceObject("cc", IDashSummarySO.class);
			FacesScopes face = new FacesScopes();
			Object token = face.getBPMAuthenticationCtxToken();
			long userId = new FacesScopes().getUserSessionBean().getUserId();

			DashSummaryReqDTO reqDTO = new DashSummaryReqDTO();

			reqDTO.setCtxToken(token);
			reqDTO.setUserId(userId);
			// ///getting supplier Id//////////////////////////////
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
			supplierReqDTO.setUserId(userId);

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
//			log.info("smResDTO="+smResDTO);
//			log.info("smResDTO.getResultBean()="+smResDTO.getResultBean());
//			log.info("smResDTO.getResultBean().getResultList()="+smResDTO.getResultBean().getResultList());
//			log.info("smResDTO.getResultBean().getResultList().get(0)="+smResDTO.getResultBean().getResultList().get(0));
        if(smResDTO.getResultBean().getResultList() != null && smResDTO.getResultBean().getResultList().size() >0 ){
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
					.get(0).getSupplierId());
         }
			// /////////////////////////////////////////////////////
			if (isCatalogueAdmin()) {
				reqDTO.setCatalogueAdminRole(true);
			} else {
				reqDTO.setCatalogueAdminRole(false);
			}
			DashSummaryResDTO responseDTO = dashSummarySO
					.getTaskSummary(reqDTO);
			// log.info("responseDTO*****" + responseDTO);
			// log.info("responseDTO.getTaskSummaryDTOList()*****"
			// + responseDTO.getTaskSummaryDTOList());
			log.info("after call service in backingBean: " + new Date());
			dashboardSummary = responseDTO.getTaskSummaryDTOList();
			// log.info("responseDTO.getTotalTask()*****"
			// + responseDTO.getTotalTask());
			totalTask = responseDTO.getTotalTask();
			log.info("after getting responce list in backingBean: "
					+ new Date());
			pendingDeliveryListNum = responseDTO.getPendingDeliveryNum();
			// log.info("responseDTO.getPendingDeliveryNum()*****"
			// + responseDTO.getPendingDeliveryNum());
			// log.info("back in backingBean: " + new Date());
			log.info("after getting pendingDeliveryListNum in backingBean: "
					+ new Date());
			pendingApprovalItemNum = responseDTO.getPendingApprovalItemNum();
			log.info("after getting pendingApprovalItemNum in backingBean: "
					+ new Date());
			pendingCodifiedItemsNum = responseDTO.getPendingCodifiedItemsNum();
			log.info("after getting pendingPublishedItemsNum in backingBean: "
					+ new Date());
			pendingPublishedItemsNum = responseDTO
					.getPendingPublishedItemsNum();
			log.info("after getting rejectedItemsNum in backingBean: "
					+ new Date());
			rejectedItemsNum = responseDTO.getRejectedItemsNum();
			// log.info("pendingApprovalItemNum*****" + pendingApprovalItemNum);
			//
			// log.info("pendingCodifiedItemsNum*****" +
			// pendingCodifiedItemsNum);
			log.info("after getting pendingCodifiedItemsNum and end backingBean: "
					+ new Date());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dashSummarySO = null;
		}

	}

	public boolean isCatalogueAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser(RoleCode.CM_ADMIN.value());

	}

	public void redirectToTaskList() throws PortalException, SystemException {

		try {

			FacesScopes facesScope = new FacesScopes();
			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			System.out.println("commonUrl" + commonUrl);

			externalContext.redirect(commonUrl + "/" + "task-list");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void goToTaskListFilterDocType(String docTypeId)
			throws PortalException, SystemException {
		System.out
				.println("....................goToTaskListFilterDocType.......................");

		try {
			FacesScopes facesScope = new FacesScopes();

			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			System.out.println("commonUrl" + commonUrl);

			externalContext.redirect(commonUrl + "/" + "task-list?docTypeId="
					+ docTypeId);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void redirectToCatalogueTaskList() throws PortalException,
			SystemException {
		System.out.println(">>>>>>>>>>redirectToCatalogueTaskList<<<<<<<<<<");
		try {
			FacesScopes facesScope = new FacesScopes();

			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			System.out.println("commonUrl" + commonUrl);

			externalContext.redirect(commonUrl + "/" + "pending-approval-item");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void redirectToPendingCodifiedItems() throws PortalException,
			SystemException {
		System.out
				.println(">>>>>>>>>>redirectToPendingCodifiedItems<<<<<<<<<<");
		try {
			FacesScopes facesScope = new FacesScopes();

			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			System.out.println("commonUrl" + commonUrl);

//			externalContext.redirect(commonUrl + "/" + "contracted-item");
			externalContext.redirect(commonUrl + "/" + "pending-codification-assignment");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void redirectToCatalogueMaintenance(String itemStatus) throws PortalException,
			SystemException {
		System.out
				.println(">>>>>>>>>>redirectToCatalogueMaintenance<<<<<<<<<<");
		
		try {
			FacesScopes facesScope = new FacesScopes();

			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			System.out.println("commonUrl" + commonUrl);
			System.out.println("itemStatus=" + itemStatus);
		
			externalContext.redirect(commonUrl + "/" + "catalogue-maintenance-file?itemStatus="+itemStatus);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void redirectToPendingDeliveryList() throws PortalException,
			SystemException {
		System.out.println(">>>>>>>>>>redirectToPendingDeliveryList<<<<<<<<<<");
		try {
			FacesScopes facesScope = new FacesScopes();

			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			System.out.println("commonUrl" + commonUrl);

			externalContext.redirect(commonUrl + "/" + "pending-delivery-list");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public List<FlFulfilmentOrder> retrievePendingDeliverylist() {
	// System.out.println(">>>>>>>>>>retrievePendingDeliverylist<<<<<<<<<<");
	// IFulfilmentOrderSO fulfilmentOrderSO = (IFulfilmentOrderSO)
	// BusinessDelegate.getInstance()
	// .getServiceObject("fl", IFulfilmentOrderSO.class);
	//
	//
	// List<FlFulfilmentOrder> flFulFilmentOrderList =
	// fulfilmentOrderSO.getPendingDoList();
	//
	// return flFulFilmentOrderList;
	// }

	public List<BpmTaskSummaryDTO> getDashboardSummary() {
		return dashboardSummary;
	}

	public long getTotalTask() {
		return totalTask;
	}

	public void setTotalTask(long totalTask) {
		this.totalTask = totalTask;
	}

	public void setDashboardSummary(List<BpmTaskSummaryDTO> dashboardSummary) {
		this.dashboardSummary = dashboardSummary;
	}

	public int getPendingDeliveryListNum() {
		return pendingDeliveryListNum;
	}

	public void setPendingDeliveryListNum(int pendingDeliveryListNum) {
		this.pendingDeliveryListNum = pendingDeliveryListNum;
	}

	public long getPendingApprovalItemNum() {
		return pendingApprovalItemNum;
	}

	public void setPendingApprovalItemNum(long pendingApprovalItemNum) {
		this.pendingApprovalItemNum = pendingApprovalItemNum;
	}

	public int getPendingMOFSubmissionNum() {
		return pendingMOFSubmissionNum;
	}

	public void setPendingMOFSubmissionNum(int pendingMOFSubmissionNum) {
		this.pendingMOFSubmissionNum = pendingMOFSubmissionNum;
	}

	public long getPendingCodifiedItemsNum() {
		return pendingCodifiedItemsNum;
	}

	public void setPendingCodifiedItemsNum(long pendingCodifiedItemsNum) {
		this.pendingCodifiedItemsNum = pendingCodifiedItemsNum;
	}

	public ISearchBean<PmParameterDesc> getDocTypeSearchBean() {
		if (this.docTypeSearchBean == null) {
			this.docTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "docTypeId");
		}

		return this.docTypeSearchBean;
	}

	public long getPendingPublishedItemsNum() {
		return pendingPublishedItemsNum;
	}

	public void setPendingPublishedItemsNum(long pendingPublishedItemsNum) {
		this.pendingPublishedItemsNum = pendingPublishedItemsNum;
	}

	public long getRejectedItemsNum() {
		return rejectedItemsNum;
	}

	public void setRejectedItemsNum(long rejectedItemsNum) {
		this.rejectedItemsNum = rejectedItemsNum;
	}

	/**
	 * Get meaningful application status message
	 * 
	 * @param docInt
	 * @return String
	 */
	public String applicationStatus(Integer docInt) {
		log.info("applicationStatus");
		log.info("docInt:" + docInt);
		if (docInt != null) {
			return getStatusDescById(docInt.longValue());
		} else {
			return "";
		}
	}
	
    public void createMenu(){
        
    	List<BpmTaskSummaryDTO> list = this.getDashboardSummary();
		
		if (CollectionUtils.isNotEmpty(list)) {
				
			Map<Long,BpmTaskSummaryDTO> zoneMap = new HashMap<Long,BpmTaskSummaryDTO>();
			
			if (CollectionUtils.isNotEmpty(list)) {
				
				for (BpmTaskSummaryDTO taskSummary : list) {
					
					String subMenuTitle = this.getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,taskSummary.getDocTypeId());
				
					Submenu submenu = new Submenu();
				    submenu.setLabel(subMenuTitle);
				    
					for (BpmTaskStatusDTO bpmTaskStatusDTO : taskSummary.getBpmTaskStatusList()){
						
						MenuItem menuItem = new MenuItem();
						
						int total = bpmTaskStatusDTO.getTotal();
					
						String applicationStatus = this.applicationStatus(Integer.parseInt(bpmTaskStatusDTO.getStatusId()));
			
						menuItem.setValue(total + " " + applicationStatus);
						menuItem.setTransient(true);
						menuItem.setPartialSubmit(true);
						menuItem.setProcess("@this");
						menuItem.setUpdate("@this");
						menuItem.setOnstart("blockWidget.show();");
					
						submenu.getChildren().add(menuItem);
					}
					
					model.addSubmenu(submenu);
				}
			}
		}
    }
 
    public MenuModel getModel() {
        return model;
    }
}
