package my.ep.web.backingbean.sm.softcertreg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sm.mofaccmaindata.SMMOFAccMainDataType;
import my.ep.jaxb.sm.mofappcandata.SMMOFAppCanDataType;
import my.ep.jaxb.sm.mofregcandata.SMMOFRegCanDataType;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.jaxb.sm.useradddata.SMUserAddDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.commonappl.CommonApplBackingBean;
import my.ep.web.backingbean.sm.payment.PaymentBackingBean;
import my.ep.web.backingbean.sm.sdoreview.SdoReviewBackingBean;
import my.ep.web.backingbean.sm.supportingdocinquiry.SupportingDocInquiryBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.softcertreg.SoftCertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="softCertBackingBean")
@ViewScoped
public class SoftCertBackingBean extends BaseBackingBean<SoftCertManagedBean>{

	public static Logger log = Logger.getLogger(SoftCertBackingBean.class);

	public SoftCertBackingBean(){

	}

	@PostConstruct
	public void init() {
		
//		FacesScopes facesScopes = new FacesScopes();
//		PmUser usr = facesScopes.getUserSessionBean();
//		getPersonnelByPmUser(usr);
//		searchPersonnel();
	}

	public void preRenderSoftCert(){
		System.out.println("********************SoftCertBackingBean PreRender***************");

		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
//		searchPersonnel();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "softCertUserSearch.xhtml");
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

		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		
		BpmResponseDTO resDTO = (BpmResponseDTO) smPersonnelSO.getBPMTaskDetails(reqDTO);

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

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/payment/portletViewMode.xhtml");
		} 
	}
	
	public String cancel(){

		return "softCertUserSearch.xhtml";

	}
	
	public void searchPersonnel(){

		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		
//		reqDTO.setSmSupplierId(new Long(1));
		
		reqDTO.setSmSupplierId(this.getManagedBean().getLoginUserPersonnelInfo().getSmAppl().getSmSupplier().getSupplierId());
		
//		System.out.println("\nP Size 1:"+this.getManagedBean().getLoginUserPersonnelInfo().getSmAppl().getSmPersonnels().size());
		
		PersonnelResDTO resDTO = new PersonnelResDTO(); 
		
		if(this.getManagedBean().getLoginRoleName().equalsIgnoreCase(SupplierManagementConst.MOF_SUPPLIER_ADMIN))
			resDTO = smPersonnelSO.retrieveAuthorizedPersonnelSoftCert(reqDTO);
		else
			resDTO = smPersonnelSO.retrievePersonnelBySupplierIdForSoftCert(reqDTO);
		
		
		
		List<SmPersonnel> smPersonnelList = resDTO.getSmPersonnelList();
		this.getManagedBean().setPersonnelList(smPersonnelList);
		
	}
	
	public void getPersonnelByPmUser(PmUser usr){
		
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		
		reqDTO.setUserId(Long.valueOf(usr.getUserId()));

		PersonnelResDTO resDTO = smPersonnelSO.retrievePersonnelByUserId(reqDTO);
		
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		
		this.getManagedBean().setLoginUserPersonnelInfo(smPersonnel);
		
	}
	
	public String navigateToPaymentPage(){
		
		this.getManagedBean().setSoftCertPayList(this.getManagedBean().getSoftCertSelectList());
		for(SmPersonnel personnel : this.getManagedBean().getSoftCertPayList()){
			personnel.setSoftcertFlg(2);
		}
		updateAmount();
		return "softCertPaymentPage.xhtml";
	}
	
	public void updateAmount(){
		
		BigDecimal amount = BigDecimal.ZERO;
		for(SmPersonnel personnel : this.getManagedBean().getSoftCertPayList()){
			this.getManagedBean().getPaymentMap().put(personnel.getPersonnelId(), BigDecimal.valueOf(150));
			amount = amount.add(BigDecimal.valueOf(150));
		}
		this.getManagedBean().setTotalPaymentAmount(amount);
	}
	
	public String makePayment(){
		
		if(getManagedBean().getSoftCertPayList() != null && getManagedBean().getSoftCertPayList().size() > 0){
			for(SmPersonnel personnel : getManagedBean().getSoftCertPayList()){
				personnel.setSoftcertFlg(SupplierManagementConst.IS_SOFTCERT_REQ);
			}
			
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			List<String> userList = new ArrayList<String>();
			userList.add(pmUser.getLoginId());

			IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IPersonnelSO.class);
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmPersonnelList(getManagedBean().getSoftCertPayList());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setUserList(userList);
			reqDTO.setUserId(pmUser.getUserId());
			
			PersonnelResDTO resDTO = new PersonnelResDTO(); 
			resDTO = smPersonnelSO.updatePersonnelAndRequestSoftCert(reqDTO);
			
			if(resDTO.isSuccessful()){
				return "acknowledge.xhtml";
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
}
