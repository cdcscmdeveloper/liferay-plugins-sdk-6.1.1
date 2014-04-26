package my.ep.web.backingbean.sm.commonappl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.constants.SMProcessNameConst;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.jaxb.sm.mofaccmaindata.SMMOFAccMainDataType;
import my.ep.jaxb.sm.mofregcandata.SMMOFRegCanDataType;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.sdoreview.SdoReviewBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvcSiteAssignBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.managedbean.sm.commonappl.CommonApplManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "commApplBackingBean")
@RequestScoped
public class CommonApplBackingBean extends
		BaseBackingBean<CommonApplManagedBean> {
	public static Logger log = Logger.getLogger(CommonApplBackingBean.class);

	public void preRenderView() {
		log.info("-------ComAppl prerender view-------");
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();

		String bpmUserName = new FacesScopes().getUserScreenName();

		reqDTO.setTaskId(taskId);
		reqDTO.setBpmUserName(bpmUserName);

		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);

		BpmResponseDTO resDTO = (BpmResponseDTO) reqSO
				.getBPMTaskDetails(reqDTO);

		this.getManagedBean().setTaskName(resDTO.getTaskName());

		if (resDTO.getTaskName()
				.equalsIgnoreCase("Verify Supporting Documents")) {
			SdoReviewBackingBean sdoBB = new SdoReviewBackingBean();
			SMMOFRegDataType smMofObj = (SMMOFRegDataType) resDTO.getPojo();

			log.info("------Verify Supporting Documents-----------"
					+ smMofObj.getDocumentId());
			sdoBB.getManagedBean().setAppId((long) smMofObj.getDocumentId());
			sdoBB.getManagedBean().setBpmAction(resDTO.getAction());
			sdoBB.getManagedBean().setTaskId(taskId);
			sdoBB.getManagedBean().setBpmPojo(smMofObj);
			sdoBB.viewSupportingDocument();

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/sdoreview/reviewSupportingDocuments");
		} else if (resDTO.getTaskName().equalsIgnoreCase("Make Payment")) {
			SMMOFRegDataType smMofObj = (SMMOFRegDataType) resDTO.getPojo();
			SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
			svoBB.getManagedBean().setAppId(smMofObj.getDocumentId());
			svoBB.getManagedBean().setBpmAction(resDTO.getAction());
			svoBB.getManagedBean().setTaskId(taskId);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/shared/paymentProcess/finalPayment.xhtml");
			// FacesContext fc = FacesContext.getCurrentInstance();
			// fc.getApplication().getNavigationHandler().handleNavigation(fc,
			// null, "/xhtml/sm/svositevisit/siteVisitView");
		} else if (resDTO.getTaskName().equalsIgnoreCase(
				"Review Application (Escalated)")
				|| resDTO.getTaskName().equalsIgnoreCase("Review Application")
				|| resDTO.getTaskName().equalsIgnoreCase("Verify Application")) {
			System.out.println(" ******* The selected process name is ******"
					+ resDTO.getProcessName());
			String applicationType = "";
			if (resDTO.getProcessName()
					.equalsIgnoreCase("MOF Account Maintain")) {
				SMMOFAccMainDataType smObj = (SMMOFAccMainDataType) resDTO
						.getPojo();
				// TODO Remove this code and get the applicationType From DB
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = retriveApplicationById(smObj.getDocumentId());
				SmAppl appl = mofResDto.getSmAppl();

				// applicationType =
				// retriveApplicationById(smObj.getDocumentId());
				System.out
						.println("********Review Application Escalted for account maintain********"
								+ applicationType);
				System.out.println("********getTaskId********" + taskId);
				System.out.println("********getTaskName********"
						+ this.getManagedBean().getTaskName());
				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean

				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());
				// smApplBB.getManagedBean().getSmAppl()
				// .setApplId(new Long(smObj.getDocumentId()));
				// smApplBB.getManagedBean().setSmAppl(appl);
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				setNavigationForSmDecision(appl.getApplType(), smApplBB);
			} else if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Supplier Registration")) {
				SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO.getPojo();
				// TODO Remove this code and get the applicationType From DB
				System.out
						.println(" *** DOCUMENT NUMBER  FROM THE BPM IS ******"
								+ resDTO.getDocNumber());
				System.out.println("**** APPLICATION TYPE ******"
						+ resDTO.getDocNumber().charAt(1));
				System.out.println(" *** DOCUMENT ID FROM THE BPM IS ******"
						+ smObj.getDocumentId());
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = retriveApplicationById(smObj.getDocumentId());
				SmAppl appl = mofResDto.getSmAppl();

				// applicationType =
				// retriveApplicationById(smObj.getDocumentId());
				System.out
						.println("********Review Application Escalted for new mof********");
				System.out.println("********getTaskId********" + taskId);
				System.out.println("********getTaskName********"
						+ this.getManagedBean().getTaskName());
				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean

				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());
				// smApplBB.getManagedBean().getSmAppl()
				// .setApplId(new Long(smObj.getDocumentId()));
				// smApplBB.getManagedBean().setSmAppl(appl);
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				setNavigationForSmDecision(appl.getApplType(), smApplBB);
			}

		} else if (resDTO.getTaskName().equalsIgnoreCase("Review Cancellation")) {
			System.out
					.println("**********MOF Registration Cancellation***********"
							+ taskId);
			SMMOFRegCanDataType srcObj = (SMMOFRegCanDataType) resDTO.getPojo();
			this.getManagedBean().setTaskName(resDTO.getTaskName());
			System.out.println("********Review Cancellation********");
			System.out.println("********Assign Site Visit APPID********"
					+ srcObj.getDocumentId());
			MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
			mofResDto = retriveApplicationById(srcObj.getDocumentId());
			SmAppl appl = mofResDto.getSmAppl();
			SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
			// TODO Change this impl to get values from DB and set
			// smSupplier , smAppl of the managedBean
			smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
					appl, mofResDto.getSmSupplier());
			// smApplBB.getManagedBean().getSmAppl()
			// .setApplId(new Long(smObj.getDocumentId()));
			// smApplBB.getManagedBean().setSmAppl(appl);
			smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
			smApplBB.getManagedBean().setTaskId(taskId);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/approverDecisionPages/approverCanRegistration_DescisionPage.xhtml");
		}

	}

	private void setNavigationForSmDecision(String applType,
			SmApplicationBackingBean smApplBB) {
		FacesContext fc = FacesContext.getCurrentInstance();

		switch (applType) {
		case "N":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/NewMofApplication/smMofRegSideMenu_BasePage.xhtml");
			// "/xhtml/sm/NewMofApplication/mofRegBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review New Mof Registration Applicatoin - ");
			break;
		case "A":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/addAdditionalCategoryCodes/smAddtnlCatApplnSideMenu_BasePage.xhtml");
			// "/xhtml/sm/addAdditionalCategoryCodes/addAdditionalCategoryBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Additional Category Code(s) Applicatoin - ");
			break;
		case "B":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/bumiPuteraApplication/smBumiSideMenu_BasePage.xhtml");
			// "/xhtml/sm/bumiPuteraApplication/applyBumiStatusBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Bumi Status Applicatoin - ");
			break;
		case "U":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/updateMofDetails/smUpdateMofSideMenu_BasePage.xhtml");
			// "/xhtml/sm/updateMofDetails/updateMofDetailsBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Update Mof Details Applicatoin - ");
			break;
		case "R":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			// "/xhtml/sm/renewMofApplication/renewMofBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Renew Mof Registration Applicatoin - ");
			break;
		default:
			System.out
					.println("***** THIS IS DEFAULT PAGE --- i.e. no matching type ---"
							+ applType);
			break;
		}
	}

	private MofRegistrationResDTO retriveApplicationById(int applId) {

		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setApplId(applId);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrievePendingApplicationDetails(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out
					.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}

	}

	// public void preRender(){
	// log.info("------ preRender-------");
	// HttpServletRequest servletRequest = PortalUtil
	// .getHttpServletRequest(new FacesScopes().getRequest());
	// HttpServletRequest originalServletRequest = PortalUtil
	// .getOriginalServletRequest(servletRequest);
	// String taskId = originalServletRequest.getParameter("taskId");
	// String loginId = originalServletRequest.getParameter("loginId");
	//
	// //log.info("--------------------" + taskId);
	// log.info("----------managedBean----------" +
	// this.getManagedBean().getTaskId());
	// if(taskId != null){
	// this.getManagedBean().setTaskId(taskId);
	// }
	//
	// //this.getManagedBean().setTaskId(taskId);
	// }
}