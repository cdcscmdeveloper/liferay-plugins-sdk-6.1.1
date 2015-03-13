package my.ep.web.backingbean.sm.softcertreg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.jaxb.sm.useradddata.SMUserAddDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.payment.PaymentBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.softcertreg.SoftCertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "softCertBackingBean")
@RequestScoped
public class SoftCertBackingBean extends BaseBackingBean<SoftCertManagedBean> {

	public static Logger log = Logger.getLogger(SoftCertBackingBean.class);

	public SoftCertBackingBean() {

	}

	/**
	 * Pre - render Request Softcert page
	 */
	public void preRenderSoftCert() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
		initAuthorizedPersonnels();
		SmCommonUtil.redirectToPage("softCertUserSearch.xhtml", "");
	}

	/**
	 * Pre - render Softcert Payment from task list
	 */
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

		BpmResponseDTO resDTO = (BpmResponseDTO) SmServiceLookup
				.getPersonnelSO().getBPMTaskDetails(reqDTO);
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
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/payment/portletViewMode.xhtml", "");
		}
	}

	/**
	 * Return back to Request Softcert first page
	 * 
	 * @return String
	 */
	public String cancel() {
		return "softCertUserSearch.xhtml";
	}

	/**
	 * Get personnel by login user
	 * 
	 * @param PmUser
	 *            usr
	 */
	public void getPersonnelByPmUser(PmUser usr) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setUserId(Long.valueOf(usr.getUserId()));
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelByUserId(reqDTO);
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		this.getManagedBean().setLoginUserPersonnelInfo(smPersonnel);
	}

	/**
	 * Navigate to Softcert payment page
	 * 
	 * @return String
	 */
	public String navigateToPaymentPage() {
		this.getManagedBean().setSoftCertPayList(
				this.getManagedBean().getSoftCertSelectList());
		updateAmount();
		setSoftCertPaymentBpmTask();
		SmCommonUtil.closeBlockUI();
		return "softCertPaymentPage.xhtml";
	}

	/**
	 * Update total amount
	 */
	public void updateAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		int i = 0;
		for (SmPersonnel personnel : this.getManagedBean().getSoftCertPayList()) {
			if (i == 0 && !getManagedBean().isAlreadyHadFreeSoftcert()) {
				this.getManagedBean().getPaymentMap()
						.put(personnel.getPersonnelId(), BigDecimal.ZERO);
				amount = amount.add(BigDecimal.ZERO);
				personnel.setFreeSoftCert(true);
				getManagedBean().setFreeSoftCertPersonnel(personnel);
			} else {
				this.getManagedBean()
						.getPaymentMap()
						.put(personnel.getPersonnelId(),
								BigDecimal.valueOf(150));
				amount = amount.add(BigDecimal.valueOf(150));
			}
			i++;
		}
		this.getManagedBean().setTotalPaymentAmount(amount);
		setPaymentBtnLbl();
	}

	/**
	 * Create bill, update personnel and call softcert payment BPM
	 */
	public void makePayment() {
		if ((getManagedBean().getSoftCertPayList() != null && getManagedBean()
				.getSoftCertPayList().size() > 0)
				|| getManagedBean().getFreeSoftCertPersonnel() != null) {
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			List<String> userList = new ArrayList<String>();
			userList.add(pmUser.getLoginId());

			PersonnelReqDTO reqDTO = new PersonnelReqDTO();

			// if already got soft cert request, include in new bill.
			for (SmPersonnel smPersonnel : getManagedBean()
					.getLoginUserPersonnelInfo().getSmAppl().getSmPersonnels()) {
				if (smPersonnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ) {
					getManagedBean().getSoftCertPayList().add(smPersonnel);
				}
			}
			reqDTO.setSmPersonnelList(getFinalPaidPersonnels());
			reqDTO.setSmPersonnel(getManagedBean().getFreeSoftCertPersonnel());
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
			resDTO = SmServiceLookup.getPersonnelSO()
					.updatePersonnelAndRequestSoftCert(reqDTO);

			if (resDTO.isSuccessful()) {
				String msgKey = MessageConst.INFO_721060;
				if (getManagedBean().getTotalPaymentAmount().compareTo(
						BigDecimal.ZERO) == 0) {
					msgKey = MessageConst.INFO_721061;
				}
				renewSesionObject();
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(), msgKey);
			}
		}
	}

	/**
	 * Remove check box tick from data table
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
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

	/**
	 * Get personnel softcert price (free for first request)
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @return String
	 */
	public String getPersonnelSoftCertPrice(SmPersonnel smPersonnel) {
		if (smPersonnel.isFreeSoftCert()) {
			return "0.00 ("
					+ SmCommonUtil
							.getMessage("lbl.sm.softcert.firstfree", null)
					+ ")";
		} else {
			return "150.00";
		}
	}

	/**
	 * Set payment button label
	 */
	private void setPaymentBtnLbl() {
		if (getManagedBean().getTotalPaymentAmount().compareTo(BigDecimal.ZERO) == 0) {
			getManagedBean().setPaymentBtnLbl(
					SmCommonUtil.getMessage("lbl.sm.request.softcert", null));
		} else {
			getManagedBean().setPaymentBtnLbl(
					SmCommonUtil.getMessage("lbl.sm.makePayment", null));
		}
	}

	/**
	 * Get if already got softcert payment in task list for the login user
	 */
	private void setSoftCertPaymentBpmTask() {
		getManagedBean().setSoftCertBpmTask(null);
		if (getManagedBean().getToken() == null
				|| StringUtils.isBlank(getManagedBean().getToken())) {
			getManagedBean().setToken(
					new FacesScopes().getBPMAuthenticationCtxToken());
		}
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

	/**
	 * Initialized authorized personnel list
	 */
	private void initAuthorizedPersonnels() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setApplId(getManagedBean().getLoginUserPersonnelInfo()
				.getSmAppl().getApplId());
		getManagedBean().setAuthorizedPersonnels(new ArrayList<SmPersonnel>());
		List<SmPersonnel> personnelList = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelByApplIdForSoftCert(reqDTO)
				.getSmPersonnelList();
		if (personnelList != null && !personnelList.isEmpty()) {
			setPersonnelRowSelection(personnelList);
			getManagedBean().setAuthorizedPersonnels(personnelList);
		}
	}

	/**
	 * Set list of personnel row whether need to be disabled from check box tick
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 */
	private void setPersonnelRowSelection(List<SmPersonnel> personnels) {
		for (SmPersonnel personnel : personnels) {
			if (personnel.getSoftcertFlg() != SupplierManagementConst.SOFT_CERT_PENDING_REQ) {
				personnel.setDisableRowSelection(true);
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING) {
					getManagedBean().setAlreadyHadFreeSoftcert(true);
				}
			} else {
				if (StringUtils.isBlank(personnel.getSmAppl().getApplNo())) {
					if (personnel.getPmUser() == null) {
						personnel.setDisableRowSelection(true);
					}
				}
			}
		}
	}

	/**
	 * Get list of personnel that selected to request soft certificate
	 * 
	 * @return List<SmPersonnel>
	 */
	private List<SmPersonnel> getFinalPaidPersonnels() {
		List<SmPersonnel> finalPayPersonnels = new ArrayList<SmPersonnel>();
		if (getManagedBean().getFreeSoftCertPersonnel() != null) {
			for (SmPersonnel payPersonnel : getManagedBean()
					.getSoftCertPayList()) {
				if (!payPersonnel.isFreeSoftCert()) {
					finalPayPersonnels.add(payPersonnel);
				}
			}
		} else {
			finalPayPersonnels.addAll(getManagedBean().getSoftCertPayList());
		}
		return finalPayPersonnels;
	}

}
