package my.ep.web.backingbean.sm.softcertreg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.jaxb.sm.useradddata.SMUserAddDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.payment.PaymentBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.softcertreg.SoftCertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "softCertBackingBean")
@RequestScoped
public class SoftCertBackingBean extends BaseBackingBean<SoftCertManagedBean> {

	public static Logger log = Logger.getLogger(SoftCertBackingBean.class);

	public SoftCertBackingBean() {

	}

	@PostConstruct
	public void init() {

	}

	public void preRenderSoftCert() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
		setSoftCertPaymentBpmTask();
		redirectToPage("softCertUserSearch.xhtml", "");
	}

	public void preRenderSoftCertPay() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();

		String token = new FacesScopes().getBPMAuthenticationCtxToken();

		reqDTO.setTaskId(taskId);
		reqDTO.setToken(token);

		BpmResponseDTO resDTO = (BpmResponseDTO) getPersonnelSO()
				.getBPMTaskDetails(reqDTO);
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		if (resDTO.getTaskName().equalsIgnoreCase("Make Payment")) {
			Object bpmObj = new Object();
			PaymentBackingBean paymentBB = new PaymentBackingBean();
			bpmObj = (SMUserAddDataType) resDTO.getPojo();
			paymentBB.getManagedBean().setAppId(
					(long) ((SMUserAddDataType) bpmObj).getDocumentId());
			paymentBB.getManagedBean().setTaskId(taskId);
			paymentBB.getManagedBean().setBpmPojo(bpmObj);
			paymentBB.retrieveBillInfo();
			redirectToPage("/xhtml/sm/payment/portletViewMode.xhtml", "");
		}
	}

	public String cancel() {
		return "softCertUserSearch.xhtml";
	}

	public void getPersonnelByPmUser(PmUser usr) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setUserId(Long.valueOf(usr.getUserId()));
		PersonnelResDTO resDTO = getPersonnelSO().retrievePersonnelByUserId(
				reqDTO);
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		this.getManagedBean().setLoginUserPersonnelInfo(smPersonnel);
	}

	public String navigateToPaymentPage() {
		this.getManagedBean().setSoftCertPayList(
				this.getManagedBean().getSoftCertSelectList());
		updateAmount();
		return "softCertPaymentPage.xhtml";
	}

	public void updateAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		for (SmPersonnel personnel : this.getManagedBean().getSoftCertPayList()) {
			this.getManagedBean().getPaymentMap()
					.put(personnel.getPersonnelId(), BigDecimal.valueOf(150));
			amount = amount.add(BigDecimal.valueOf(150));
		}
		this.getManagedBean().setTotalPaymentAmount(amount);
	}

	public void makePayment() {
		if (getManagedBean().getSoftCertPayList() != null
				&& getManagedBean().getSoftCertPayList().size() > 0) {
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			List<String> userList = new ArrayList<String>();
			userList.add(pmUser.getLoginId());

			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			
			//if already got soft cert request, include in new bill.
			for (SmPersonnel smPersonnel : getManagedBean()
					.getLoginUserPersonnelInfo().getSmAppl().getSmPersonnels()) {
				if (smPersonnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ) {
					getManagedBean().getSoftCertPayList().add(smPersonnel);
				}
			}
			reqDTO.setSmPersonnelList(getManagedBean().getSoftCertPayList());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setToken(getManagedBean().getToken());
			reqDTO.setUserList(userList);
			reqDTO.setUserId(pmUser.getUserId());
			reqDTO.setSoftCertPaymentExistInTaskList(getManagedBean()
					.getSoftCertBpmTask() == null ? false : true);
			reqDTO.setSoftCertPaymentDocId(getManagedBean()
					.getSoftCertBpmTask() == null ? null : getManagedBean()
					.getSoftCertBpmTask().getDocId());
			reqDTO.setSoftCertPaymentTaskId(getManagedBean()
					.getSoftCertBpmTask() == null ? null : getManagedBean()
					.getSoftCertBpmTask().getTaskId());

			PersonnelResDTO resDTO = new PersonnelResDTO();
			resDTO = getPersonnelSO().updatePersonnelAndRequestSoftCert(reqDTO);

			if (resDTO.isSuccessful()) {
				renewSesionObject();
				redirectToPage(SmApplicationUtil.getApplicationErrorPage(),
						MessageConst.INFO_721060);
			}
		}
	}

	public void unSelectPersonnel(AjaxBehaviorEvent e) {
		if (getManagedBean().getSoftCertSelectList() != null) {
			Iterator<SmPersonnel> iterator = getManagedBean()
					.getSoftCertSelectList().iterator();
			while (iterator.hasNext()) {
				SmPersonnel personnel = iterator.next();
				if (personnel.getSoftcertFlg() != SupplierManagementConst.SOFT_CERT_PENDING_REQ) {
					iterator.remove();
				} else {
					if (personnel.getPmUser() == null) {
						iterator.remove();
					}
				}
			}
		}
	}

	private void setSoftCertPaymentBpmTask() {
		getManagedBean().setSoftCertBpmTask(null);
		getManagedBean().setToken(
				new FacesScopes().getBPMAuthenticationCtxToken());
		List<WorklistItemVO> taskList = SmCommonUtil
				.getBpmTaskListByAuthToken(getManagedBean().getToken());
		if (taskList != null && !taskList.isEmpty()) {
			for (WorklistItemVO worklistItemVO : taskList) {
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						"lbl.sm.addAdditional.makePayment")) {
					getManagedBean().setSoftCertBpmTask(worklistItemVO);
					break;
				}
			}
		}
	}

	private IPersonnelSO getPersonnelSO() {
		return (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
	}

	private void redirectToPage(String page, String msgKeyId) {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (StringUtils.isNotBlank(msgKeyId)) {
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(msgKeyId,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, page);
	}
}
