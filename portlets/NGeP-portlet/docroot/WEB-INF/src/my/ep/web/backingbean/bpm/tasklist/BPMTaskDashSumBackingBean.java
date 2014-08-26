package my.ep.web.backingbean.bpm.tasklist;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cc.dashsummary.IDashSummarySO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.DashSummaryVO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.cc.dashsummary.BpmTaskSummaryDTO;
import my.ep.dto.cc.dashsummary.DashSummaryReqDTO;
import my.ep.dto.cc.dashsummary.DashSummaryResDTO;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.cm.DashSummaryStatus;
import my.ep.web.bean.cm.DashSummaryViewBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.bpm.tasklist.BPMTaskDashSumManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

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
	private ISearchBean<PmParameterDesc> docTypeSearchBean;

	public BPMTaskDashSumBackingBean() {
		super();
		createTaskBarModel();
	}

	private void createTaskBarModel() {
		System.out.println(">>>>>>>>>>>>>createTaskBarModel<<<<<<<<<<<<<<");
		IDashSummarySO dashSummarySO = null;
		try {

			dashSummarySO = (IDashSummarySO) BusinessDelegate.getInstance()
					.getServiceObject("cc", IDashSummarySO.class);
			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			long userId = new FacesScopes().getUserSessionBean().getUserId();

			System.out.println("testing BPM auth******" + token);
			DashSummaryReqDTO reqDTO = new DashSummaryReqDTO();

			reqDTO.setCtxToken(token);
			reqDTO.setUserId(userId);
			if (isCatalogueAdmin()) {
				reqDTO.setCatalogueAdminRole(true);
			}else{
				reqDTO.setCatalogueAdminRole(false);
			}
			DashSummaryResDTO responseDTO = dashSummarySO
					.getTaskSummary(reqDTO);
			log.info("responseDTO*****" + responseDTO);
			log.info("responseDTO.getTaskSummaryDTOList()*****"
					+ responseDTO.getTaskSummaryDTOList());
			dashboardSummary = responseDTO.getTaskSummaryDTOList();
			log.info("responseDTO.getTotalTask()*****"
					+ responseDTO.getTotalTask());
			totalTask = responseDTO.getTotalTask();
			pendingDeliveryListNum = responseDTO.getPendingDeliveryNum();
			log.info("responseDTO.getPendingDeliveryNum()*****"
					+ responseDTO.getPendingDeliveryNum());
			
				pendingApprovalItemNum = responseDTO
						.getPendingApprovalItemNum();
				pendingCodifiedItemsNum = responseDTO
						.getPendingCodifiedItemsNum();
			
			log.info("pendingApprovalItemNum*****" + pendingApprovalItemNum);

			log.info("pendingCodifiedItemsNum*****" + pendingCodifiedItemsNum);

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

			externalContext.redirect(commonUrl + "/" + "contracted-item");

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

}
