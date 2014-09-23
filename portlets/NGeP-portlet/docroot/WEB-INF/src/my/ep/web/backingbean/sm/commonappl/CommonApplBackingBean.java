package my.ep.web.backingbean.sm.commonappl;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.jaxb.sm.mofaccmaindata.SMMOFAccMainDataType;
import my.ep.jaxb.sm.mofappcandata.SMMOFAppCanDataType;
import my.ep.jaxb.sm.mofregcandata.SMMOFRegCanDataType;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.payment.PaymentBackingBean;
import my.ep.web.backingbean.sm.sdoreview.SdoReviewBackingBean;
import my.ep.web.backingbean.sm.supportingdocinquiry.SupportingDocInquiryBackingBean;
import my.ep.web.constant.CommonUIConst;
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

		String token = new FacesScopes().getBPMAuthenticationCtxToken();

		reqDTO.setTaskId(taskId);
		reqDTO.setToken(token);
		// reqDTO.setBpmUserName(bpmUserName);

		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);

		BpmResponseDTO resDTO = (BpmResponseDTO) reqSO
				.getBPMTaskDetails(reqDTO);

		this.getManagedBean().setTaskName(resDTO.getTaskName());

		if (resDTO.getTaskName()
				.equalsIgnoreCase("Verify Supporting Documents")) {
			SdoReviewBackingBean sdoBB = new SdoReviewBackingBean();
			Object bpmObj = new Object();

			log.info("------Verify Supporting Documents-----------");

			if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Supplier Registration")) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				sdoBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			} else if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Account Maintain")) {
				bpmObj = (SMMOFAccMainDataType) resDTO.getPojo();
				sdoBB.getManagedBean().setAppId(
						(long) ((SMMOFAccMainDataType) bpmObj).getDocumentId());

			}
			// sdoBB.getManagedBean().setAppId((long) bpmObj.getDocumentId());
			sdoBB.getManagedBean().setBpmAction(resDTO.getAction());
			sdoBB.getManagedBean().setTaskId(taskId);
			sdoBB.getManagedBean().setBpmPojo(bpmObj);
			sdoBB.viewSupportingDocument();

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/sdoreview/reviewSupportingDocuments");
		} else if (resDTO.getTaskName().equalsIgnoreCase("Make Payment")) {
			// System.out.println("Process Name: " + resDTO.getProcessName());
			// /*
			// * SMMOFRegDataType smMofObj = (SMMOFRegDataType)
			// resDTO.getPojo();
			// * SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
			// * svoBB.getManagedBean().setAppId(smMofObj.getDocumentId());
			// * svoBB.getManagedBean().setBpmAction(resDTO.getAction());
			// * svoBB.getManagedBean().setTaskId(taskId);
			// */
			//
			// int documentId = 0;
			// if (resDTO.getProcessName()
			// .equalsIgnoreCase("MOF Account Maintain")) {
			// SMMOFAccMainDataType smObj = (SMMOFAccMainDataType) resDTO
			// .getPojo();
			// documentId = smObj.getDocumentId();
			//
			// } else if (resDTO.getProcessName().equalsIgnoreCase(
			// "MOF Supplier Registration")) {
			// SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO.getPojo();
			// documentId = smObj.getDocumentId();
			// }
			//
			// MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
			// mofResDto = retriveApplicationById(documentId);
			// SmAppl appl = mofResDto.getSmAppl();
			// this.getManagedBean().setApplType(appl.getApplType());
			// this.getManagedBean().setPersonnelList(appl.getSmPersonnels());
			// this.getManagedBean().getPersonnelList().add(new SmPersonnel());
			// this.getManagedBean().setSelectPersonnelList(
			// this.getManagedBean().getPersonnelList());
			// this.getManagedBean().setpListSize(
			// this.getManagedBean().getPersonnelList().size() - 1);
			// updateAmount();
			// FacesContext fc = FacesContext.getCurrentInstance();
			// fc.getApplication()
			// .getNavigationHandler()
			// .handleNavigation(fc, null,
			// "/xhtml/sm/shared/paymentProcess/finalPayment.xhtml");
			// // FacesContext fc = FacesContext.getCurrentInstance();
			// //
			// fc.getApplication().getNavigationHandler().handleNavigation(fc,
			// // null, "/xhtml/sm/svositevisit/siteVisitView");

			Object bpmObj = new Object();
			PaymentBackingBean paymentBB = new PaymentBackingBean();
			if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Supplier Registration")) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				paymentBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			} else if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Account Maintain")) {
				bpmObj = (SMMOFAccMainDataType) resDTO.getPojo();
				paymentBB.getManagedBean().setAppId(
						(long) ((SMMOFAccMainDataType) bpmObj).getDocumentId());
			}

			paymentBB.getManagedBean().setTaskId(taskId);
			paymentBB.getManagedBean().setBpmPojo(bpmObj);

			paymentBB.retrieveBillInfo();

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/payment/portletViewMode.xhtml");
		} else if (resDTO.getTaskName().equalsIgnoreCase("Respond PO Inquiry")) {
			System.out.println("** Respond to PO Inquiry *********"
					+ resDTO.getProcessName());
			if (resDTO.getProcessName()
					.equalsIgnoreCase("MOF Account Maintain")) {
				SMMOFAccMainDataType smObj = (SMMOFAccMainDataType) resDTO
						.getPojo();
				// TODO Remove this code and get the applicationType From DB
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = retriveApplicationById(smObj.getDocumentId());
				SmAppl appl = mofResDto.getSmAppl();

				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean
				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());
				smApplBB.assignMissingDocsList();
				// smApplBB.getManagedBean().setSmAppl(appl);
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				setNavigationForSmDecision("RS", smApplBB);
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

				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean
				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());
				smApplBB.assignMissingDocsList();
				// smApplBB.getManagedBean().setSmAppl(appl);
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				setNavigationForSmDecision("RS", smApplBB);
			}

		} else if (resDTO.getTaskName().equalsIgnoreCase(
				"Review Application (Escalated)")
				|| resDTO.getTaskName().equalsIgnoreCase("Review Application")
				|| resDTO.getTaskName().equalsIgnoreCase(
						"Review Application(Special Category)")
				|| resDTO.getTaskName().equalsIgnoreCase("Verify Application")) {
			System.out.println(" ******* The selected process name is ******"
					+ resDTO.getProcessName());
			if (resDTO.getProcessName()
					.equalsIgnoreCase("MOF Account Maintain")) {
				SMMOFAccMainDataType smObj = (SMMOFAccMainDataType) resDTO
						.getPojo();

				// TODO Remove this code and get the applicationType From DB
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = retriveApplicationById(smObj.getDocumentId());
				SmAppl appl = mofResDto.getSmAppl();
				log.info("********Review Application Escalted for account maintain********"
						+ appl.getSupplierType());
				log.info("********getTaskId********" + taskId);
				log.info("********getTaskName********"
						+ this.getManagedBean().getTaskName());
				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				smApplBB.getManagedBean();
				if (resDTO.getTaskName().equalsIgnoreCase("Review Application")
						|| resDTO.getTaskName().equalsIgnoreCase(
								"Review Application(Special Category)")
						|| resDTO.getTaskName().equalsIgnoreCase(
								"Verify Application")) {
					smApplBB.getManagedBean().setPoUser(true);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(false);
				} else if (resDTO.getTaskName().equalsIgnoreCase(
						"Review Application (Escalated)")) {
					smApplBB.getManagedBean().setPoUser(false);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(true);
				}
				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());
				if (resDTO.getTaskName().equalsIgnoreCase("Verify Application")) {
					log.info("*** This is po or approver action after site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(true);
				} else {
					log.info("*** This is po or approver for normal review without site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(false);
				}
				if (smObj.getSupplierList() != null) {
					smApplBB.getManagedBean().setSupplierToPOList(
							smObj.getSupplierList());
				}
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				setNavigationForSmDecision(appl.getApplType(), smApplBB);
			} else if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Supplier Registration")) {
				SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO.getPojo();
				// TODO Remove this code and get the applicationType From DB
				log.info(" *** DOCUMENT NUMBER  FROM THE BPM IS ******"
						+ resDTO.getDocNumber());
				log.info("**** APPLICATION TYPE ******"
						+ resDTO.getDocNumber().charAt(1));
				log.info(" *** DOCUMENT ID FROM THE BPM IS ******"
						+ smObj.getDocumentId());
				MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
				mofResDto = retriveApplicationById(smObj.getDocumentId());
				SmAppl appl = mofResDto.getSmAppl();
				log.info("********Review Application Escalted for new mof********");
				log.info("********getTaskId********" + taskId);
				log.info("********getTaskName********"
						+ this.getManagedBean().getTaskName());
				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				smApplBB.getManagedBean();
				if (resDTO.getTaskName().equalsIgnoreCase("Review Application")
						|| resDTO.getTaskName().equalsIgnoreCase(
								"Review Application(Special Category)")
						|| resDTO.getTaskName().equalsIgnoreCase(
								"Verify Application")) {
					smApplBB.getManagedBean().setPoUser(true);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(false);
				} else if (resDTO.getTaskName().equalsIgnoreCase(
						"Review Application (Escalated)")) {
					smApplBB.getManagedBean().setPoUser(false);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(true);
				}
				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());
				if (resDTO.getTaskName().equalsIgnoreCase("Verify Application")) {
					log.info("*** This is po or approver action after site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(true);
				} else {
					log.info("*** This is po or approver for normal review without site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(false);
				}
				if (smObj.getSupplierList() != null) {
					smApplBB.getManagedBean().setSupplierToPOList(
							smObj.getSupplierList());
				}
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				setNavigationForSmDecision(appl.getApplType(), smApplBB);
			}

		} else if (resDTO.getTaskName().equalsIgnoreCase(
				"Draft Register Account")) {
			System.out
					.println(" ******* The selected process name is  Draft Register Account ******"
							+ resDTO.getProcessName());
			// if (resDTO.getProcessName().equalsIgnoreCase(
			// "Start MOF Supplier Registration")) {
			SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO.getPojo();
			// TODO Remove this code and get the applicationType From DB
			System.out.println(" *** DOCUMENT NUMBER  FROM THE BPM IS ******"
					+ resDTO.getDocNumber());
			System.out.println(" *** DOCUMENT ID FROM THE BPM IS ******"
					+ smObj.getDocumentId());
			MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
			mofResDto = retriveApplicationById(smObj.getDocumentId());
			SmAppl appl = mofResDto.getSmAppl();

			// applicationType =
			// retriveApplicationById(smObj.getDocumentId());
			System.out
					.println("********Draft Register Account For New Mof********");
			System.out.println("********getTaskId********" + taskId);
			SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();

			smApplBB.processDraftApplication(appl.getApplType(), Boolean.TRUE,
					appl, mofResDto.getSmSupplier());
			smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
			smApplBB.getManagedBean().setTaskId(taskId);
			smApplBB.getManagedBean().setDraftedAppl(true);
			setNavigationForDraftAppl(appl.getApplType());
			// }

		} else if (resDTO.getTaskName().equalsIgnoreCase(
				"Draft Maintain Account")) {
			System.out
					.println(" ******* The selected process name is  Draft Maintain Account******"
							+ resDTO.getProcessName());
			// if (resDTO.getProcessName().equalsIgnoreCase(
			// "Start MOF Account Maintain")) {
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
							+ appl.getSupplierType());
			System.out.println("********getTaskId********" + taskId);
			System.out.println("********getTaskName********"
					+ this.getManagedBean().getTaskName());
			SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
			// TODO Change this impl to get values from DB and set
			// smSupplier , smAppl of the managedBean

			// applicationType =
			// retriveApplicationById(smObj.getDocumentId());
			System.out
					.println("********Draft Register Account For New Mof********");
			System.out.println("********getTaskId********" + taskId);

			smApplBB.processDraftApplication(appl.getApplType(), Boolean.TRUE,
					appl, mofResDto.getSmSupplier());
			smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
			smApplBB.getManagedBean().setTaskId(taskId);
			smApplBB.getManagedBean().setDraftedAppl(true);
			setNavigationForDraftAppl(appl.getApplType());
			// }

		} else if (resDTO.getTaskName().equalsIgnoreCase("Review Cancellation")
				&& resDTO.getProcessName().equalsIgnoreCase(
						"MOF Registration Cancellation")) {
			System.out
					.println("**********MOF Registration Cancellation***********"
							+ taskId);
			SMMOFRegCanDataType srcObj = (SMMOFRegCanDataType) resDTO.getPojo();
			this.getManagedBean().setTaskName(resDTO.getTaskName());
			System.out
					.println("********Review RegistrationCancellation********");
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
		} else if (resDTO.getTaskName().equalsIgnoreCase("Review Cancellation")
				&& resDTO.getProcessName().equalsIgnoreCase(
						"MOF Application Cancellation")) {
			System.out
					.println("**********MOF Application Cancellation***********"
							+ taskId);
			SMMOFAppCanDataType srcObj = (SMMOFAppCanDataType) resDTO.getPojo();
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
			// smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
			// appl, mofResDto.getSmSupplier());
			// smApplBB.getManagedBean().getSmAppl()
			// .setApplId(new Long(smObj.getDocumentId()));
			SmRemaksResDTO remResDTO = getRemarksByDoc(
					ParameterConst.NEW_SM_APPLN_FOR_DELETE_PENDING_APPL,
					appl.getApplId());
			StringBuffer sb = new StringBuffer();
			if (resDTO != null && remResDTO.getRemarksList() != null
					&& !remResDTO.getRemarksList().isEmpty()) {

				for (SmRemark rem : remResDTO.getRemarksList()) {
					sb.append(rem.getRemark()).append("\n");
				}
			}
			smApplBB.getManagedBean().setSupplierRemarks(sb.toString());
			smApplBB.getManagedBean().setSmAppl(appl);
			smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
			smApplBB.getManagedBean().setTaskId(taskId);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/deletePendingMofAppln/cancelPendingApplicationDescisionPage.xhtml");
		} else if (resDTO.getTaskName().equalsIgnoreCase("Respond SDO Inquiry")) {
			Object bpmObj = new Object();
			SupportingDocInquiryBackingBean suppDocIn = new SupportingDocInquiryBackingBean();

			if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Supplier Registration")) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				suppDocIn.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			} else if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Account Maintain")) {
				bpmObj = (SMMOFAccMainDataType) resDTO.getPojo();
				suppDocIn.getManagedBean().setAppId(
						(long) ((SMMOFAccMainDataType) bpmObj).getDocumentId());

			}

			suppDocIn.getManagedBean().setBpmAction(resDTO.getAction());
			suppDocIn.getManagedBean().setTaskId(taskId);
			suppDocIn.getManagedBean().setBpmPojo(bpmObj);
			suppDocIn.viewDocInquiry();
			System.out.println("*********Respond SDO Inquiry********");

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/suppliersupportingdocinquiry/inquiryresponse.xhtml");

		} else if (resDTO.getTaskName().equalsIgnoreCase("Processing Fee")) {
			Object bpmObj = new Object();
			PaymentBackingBean paymentBB = new PaymentBackingBean();
			if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Supplier Registration")) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				paymentBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			} else if (resDTO.getProcessName().equalsIgnoreCase(
					"MOF Account Maintain")) {
				bpmObj = (SMMOFAccMainDataType) resDTO.getPojo();
				paymentBB.getManagedBean().setAppId(
						(long) ((SMMOFAccMainDataType) bpmObj).getDocumentId());
			}

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

	public void updateAmount() {

		BigDecimal amount = BigDecimal.ZERO;
		for (SmPersonnel personnel : this.getManagedBean()
				.getSelectPersonnelList()) {
			amount = amount.add(BigDecimal.valueOf(150));
		}
		amount = amount.subtract(BigDecimal.valueOf(150));
		amount = amount.add(BigDecimal.valueOf(400));
		this.getManagedBean().setTotalAmount(amount);
	}

	public String makePayment() {
		System.out.println("****** Make Payment **********");

		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_721059,
								new String[] { "" + 1 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}

	private void setNavigationForDraftAppl(String appType) {
		FacesContext fc = FacesContext.getCurrentInstance();
		switch (appType) {

		case "N":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/NewMofApplication/mofRegSmUser_BaseWizardPage.xhtml");
			break;
		case "A":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/addAdditionalCategoryCodes/smAddtnlCatApplnSideMenu_BasePage.xhtml");
			// PROCEED_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/bumiPuteraApplication/smBumiSideMenu_BasePage.xhtml");
			// PROCEED_BUMI_APPLN;
			break;
		case "U":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/updateMofDetails/smUpdateMofSideMenu_BasePage.xhtml");
			// PROCEED_UPDATE_MOF_APPLN;
			break;
		case "R":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			// PROCEED_RENEW_MOF_APPLN;
			break;
		default:
			break;

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
					"Review New Mof Registration Application - ");
			break;
		case "C":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/NewMofApplication/smMofRegSideMenu_BasePage.xhtml");
			// "/xhtml/sm/NewMofApplication/mofRegBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Cancel Mof Registration Application - ");
			break;
		case "A":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/addAdditionalCategoryCodes/smAddtnlCatApplnSideMenu_BasePage.xhtml");
			// "/xhtml/sm/addAdditionalCategoryCodes/addAdditionalCategoryBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Additional Category Code(s) Application - ");
			break;
		case "B":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/bumiPuteraApplication/smBumiSideMenu_BasePage.xhtml");
			// "/xhtml/sm/bumiPuteraApplication/applyBumiStatusBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Bumi Status Application - ");
			break;
		case "U":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/updateMofDetails/smUpdateMofSideMenu_BasePage.xhtml");
			// "/xhtml/sm/updateMofDetails/updateMofDetailsBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Update Mof Details Application - ");
			break;
		case "R":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			// "/xhtml/sm/renewMofApplication/renewMofBasePage.xhtml");
			smApplBB.getManagedBean().setHeaderMsg(
					"Review Renew Mof Registration Application - ");
			break;
		case "RS":
			System.out.println(" *** DOC MODE ****"
					+ smApplBB.getManagedBean().getSmAppl()
							.getSupportingDocMode());
			String out = "";
			if (smApplBB.getManagedBean().getSmAppl().getSupportingDocMode()
					.equalsIgnoreCase("O")) {
				out = "/xhtml/sm/shared/respondToPoInquiry/onlineResendSupportingDocsForm.xhtml";
			} else {
				out = "/xhtml/sm/shared/respondToPoInquiry/offlineResendSupportingDocsForm.xhtml";
			}
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, out);
			smApplBB.getManagedBean().setHeaderMsg(
					" Respond PO Inquiry For Application - ");
			break;
		default:
			System.out
					.println("***** THIS IS DEFAULT PAGE --- i.e. no matching type ---"
							+ applType);
			break;
		}
	}

	private SmRemaksResDTO getRemarksByDoc(String docType, Long docId) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocId(docId);
		reqDTO.setDocType(docType);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISmRemarksSO.class);
		SmRemaksResDTO resDTO = reqSO.retrieveSmRemarksByDocument(reqDTO);
		return resDTO;
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
					.retrieveSmApplicationDetailsById(reqDto);
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