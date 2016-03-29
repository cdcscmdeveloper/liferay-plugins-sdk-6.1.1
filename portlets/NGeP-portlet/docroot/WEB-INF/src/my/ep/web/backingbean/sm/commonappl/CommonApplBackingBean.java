package my.ep.web.backingbean.sm.commonappl;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sm.attachment.ISmAttachmentSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.SMProcessNameConst;
import my.ep.dto.sm.attachment.SmAttachmentReqDTO;
import my.ep.dto.sm.attachment.SmAttachmentResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sm.mofappcandata.SMMOFAppCanDataType;
import my.ep.jaxb.sm.mofregcandata.SMMOFRegCanDataType;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewApplHistoryBackingBean;
import my.ep.web.backingbean.sm.payment.PaymentBackingBean;
import my.ep.web.backingbean.sm.sdoreview.SdoReviewBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvcSiteAssignBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.backingbean.sm.supportingdocinquiry.SupportingDocInquiryBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.commonappl.CommonApplManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

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
		boolean isClaim = Boolean.valueOf(originalServletRequest.getParameter("claim"));
		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();

		Object token = new FacesScopes().getBPMAuthenticationCtxToken();

		reqDTO.setTaskId(taskId);
		reqDTO.setToken(token);
		// reqDTO.setBpmUserName(bpmUserName);

		ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISdoReviewSO.class);

		BpmResponseDTO resDTO = (BpmResponseDTO) reqSO
				.getBPMTaskDetails(reqDTO);

		this.getManagedBean().setTaskName(resDTO.getTaskName());
		
		if (isRedirectToViewOnlyApplication(isClaim, resDTO)) {
			ViewApplHistoryBackingBean backingBean = new ViewApplHistoryBackingBean();
			backingBean.getManagedBean().setTaskId(taskId);
			backingBean.getManagedBean().setClaimTask(isClaim);
			backingBean.viewUnClaimTask(resDTO.getDocNumber());
			SmCommonUtil
					.redirectToPage(
							"/xhtml/sm/revisionSmApplView/unclaimtaskView/smViewUnClaimTask.xhtml",
							"", null);
		}
		else if (resDTO.getTaskName()
				.equalsIgnoreCase(SMProcessNameConst.VERIFY_SUPP_DOC)) {
			SdoReviewBackingBean sdoBB = new SdoReviewBackingBean();
			Object bpmObj = new Object();

			log.info("------Verify Supporting Documents-----------");

			if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_SUPP_REGISTRATION)) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				sdoBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			} else if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_ACC_MAINTAIN)) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				sdoBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			}
			// sdoBB.getManagedBean().setAppId((long) bpmObj.getDocumentId());
			sdoBB.getManagedBean().setBpmAction(resDTO.getAction());
			sdoBB.getManagedBean().setTaskId(taskId);
			sdoBB.getManagedBean().setBpmPojo(bpmObj);
			sdoBB.getManagedBean().setClaimTask(isClaim);
			sdoBB.viewSupportingDocument();

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/sdoreview/reviewSupportingDocuments");
		} else if (resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.MAKE_PAYMENT)) {
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
			// .equalsIgnoreCase(SMProcessNameConst.PROCESS_NAME_MOF_ACC_MAINTAIN)) {
			// SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO
			// .getPojo();
			// documentId = smObj.getDocumentId();
			//
			// } else if (resDTO.getProcessName().equalsIgnoreCase(
			// SMProcessNameConst.PROCESS_NAME_MOF_SUPP_REGISTRATION)) {
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
			
/*			if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_SUPP_REGISTRATION)) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				paymentBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			} else if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_ACC_MAINTAIN)) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				paymentBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());
			}
*/	
			bpmObj = resDTO.getPojo();
			log.info("Doc No>>>>>" + resDTO.getDocNumber());
			paymentBB.getManagedBean().setDocNo(resDTO.getDocNumber());
			paymentBB.getManagedBean().setTaskId(taskId);
			paymentBB.getManagedBean().setBpmPojo(bpmObj);

			paymentBB.retrieveBillInfo();

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/payment/portletViewMode.xhtml");
		} else if (resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.RESPOND_PO_INQUIRY)) {
			log.info("** Respond to PO Inquiry *********"
					+ resDTO.getProcessName());
			if (resDTO.getProcessName()
					.equalsIgnoreCase(SMProcessNameConst.PROCESS_NAME_MOF_ACC_MAINTAIN)) {
				SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO
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
				smApplBB.getManagedBean().setClaimTask(isClaim);
				setNavigationForSmDecision("RS", smApplBB);
			} else if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_SUPP_REGISTRATION)) {
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

				SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
				// TODO Change this impl to get values from DB and set
				// smSupplier , smAppl of the managedBean
				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());
				smApplBB.assignMissingDocsList();
				// smApplBB.getManagedBean().setSmAppl(appl);
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				smApplBB.getManagedBean().setClaimTask(isClaim);
				setNavigationForSmDecision("RS", smApplBB);
			}

		} else if (resDTO.getTaskName().equalsIgnoreCase(
				SMProcessNameConst.REVIEW_APPL_ESCALATED)
				|| resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL)
				|| resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL_SPLCAT)
				|| resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.VERIFY_APPL)
				|| resDTO
						.getTaskName()
						.equalsIgnoreCase(
								SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE)) {
			log.info(" ******* The selected process name is ******"
					+ resDTO.getProcessName());
			if (resDTO.getProcessName()
					.equalsIgnoreCase(SMProcessNameConst.PROCESS_NAME_MOF_ACC_MAINTAIN)) {
				SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO
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
				// TODO get the status from BPM for the header part
				BPMTaskListBackingBean bpmBean = new BPMTaskListBackingBean();
				String currentStatus = bpmBean.applicationStatus(resDTO
						.getDocStatus());
				smApplBB.getManagedBean().setCurrentStatus(currentStatus);
				//
				if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_APPL_SPLCAT)
						|| resDTO.getTaskName().equalsIgnoreCase(
								SMProcessNameConst.VERIFY_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE)) {
					smApplBB.getManagedBean().setPoUser(true);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(false);
				} else if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL_ESCALATED)) {
					smApplBB.getManagedBean().setPoUser(false);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(true);
					smApplBB.getManagedBean().setReviewSiteVisit(
							smObj.isSiteVisitComplete());

				}
				if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.VERIFY_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE)) {
					log.info("*** This is po or approver action after site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(true);
				} else if (!resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL_ESCALATED)) {
					log.info("*** This is po or approver for normal review without site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(false);
				}
				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());

				if (smObj.getSupplierList() != null) {
					smApplBB.getManagedBean().setSupplierToPOList(
							smObj.getSupplierList());
				}
				smApplBB.getManagedBean().setBpmTaskName(resDTO.getTaskName());
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				smApplBB.getManagedBean().setClaimTask(isClaim);
				setNavigationForSmDecision(appl.getApplType(), smApplBB);
			} else if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_SUPP_REGISTRATION)) {
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
				// TODO get the status from BPM for the header part
				BPMTaskListBackingBean bpmBean = new BPMTaskListBackingBean();
				String currentStatus = bpmBean.applicationStatus(resDTO
						.getDocStatus());
				smApplBB.getManagedBean().setCurrentStatus(currentStatus);
				if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_APPL_SPLCAT)
						|| resDTO.getTaskName().equalsIgnoreCase(
								SMProcessNameConst.VERIFY_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE)) {
					smApplBB.getManagedBean().setPoUser(true);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(false);
				} else if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL_ESCALATED)) {
					smApplBB.getManagedBean().setPoUser(false);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(true);
					smApplBB.getManagedBean().setReviewSiteVisit(
							smObj.isSiteVisitComplete());
				}
				if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.VERIFY_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE)) {
					log.info("*** This is po or approver action after site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(true);
				} else if (!resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL_ESCALATED)) {
					log.info("*** This is po or approver for normal review without site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(false);
				}
				log.info("mofResDto.getSmSupplier()>>>" + mofResDto.getSmSupplier());
				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());

				smApplBB.getManagedBean().setBpmTaskName(resDTO.getTaskName());
				if (smObj.getSupplierList() != null) {
					smApplBB.getManagedBean().setSupplierToPOList(
							smObj.getSupplierList());
				}
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				smApplBB.getManagedBean().setClaimTask(isClaim);
				setNavigationForSmDecision(appl.getApplType(), smApplBB);
			}else if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_SUPP_SITE_VISIT)){
				//TODO: new added by andrew - temporary solve the new implementation
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
				// TODO get the status from BPM for the header part
				BPMTaskListBackingBean bpmBean = new BPMTaskListBackingBean();
				String currentStatus = bpmBean.applicationStatus(resDTO
						.getDocStatus());
				smApplBB.getManagedBean().setCurrentStatus(currentStatus);
				if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_APPL_SPLCAT)
						|| resDTO.getTaskName().equalsIgnoreCase(
								SMProcessNameConst.VERIFY_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE)) {
					smApplBB.getManagedBean().setPoUser(true);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(false);
				} else if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL_ESCALATED)) {
					smApplBB.getManagedBean().setPoUser(false);
					smApplBB.getManagedBean().setSupplierUser(false);
					smApplBB.getManagedBean().setApproverUser(true);
					smApplBB.getManagedBean().setReviewSiteVisit(
							smObj.isSiteVisitComplete());
				}
				if (resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.VERIFY_APPL)
						|| resDTO
								.getTaskName()
								.equalsIgnoreCase(
										SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE)) {
					log.info("*** This is po or approver action after site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(true);
				} else if (!resDTO.getTaskName().equalsIgnoreCase(
						SMProcessNameConst.REVIEW_APPL_ESCALATED)) {
					log.info("*** This is po or approver for normal review without site visit *** ");
					smApplBB.getManagedBean().setReviewSiteVisit(false);
				}
				smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
						appl, mofResDto.getSmSupplier());

				smApplBB.getManagedBean().setBpmTaskName(resDTO.getTaskName());
				if (smObj.getSupplierList() != null) {
					smApplBB.getManagedBean().setSupplierToPOList(
							smObj.getSupplierList());
				}
				smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
				smApplBB.getManagedBean().setTaskId(taskId);
				smApplBB.getManagedBean().setClaimTask(isClaim);
				setNavigationForSmDecision(appl.getApplType(), smApplBB);
			}

		} else if (resDTO.getTaskName().equalsIgnoreCase(
				SMProcessNameConst.DRAFT_REGISTER_ACCOUNT)) {
			log.info(" ******* The selected process name is  Draft Register Account ******"
					+ resDTO.getProcessName());
			// if (resDTO.getProcessName().equalsIgnoreCase(
			// "Start MOF Supplier Registration")) {
			SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO.getPojo();
			// TODO Remove this code and get the applicationType From DB
			log.info(" *** DOCUMENT NUMBER  FROM THE BPM IS ******"
					+ resDTO.getDocNumber());
			log.info(" *** DOCUMENT ID FROM THE BPM IS ******"
					+ smObj.getDocumentId());
			MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
			mofResDto = retriveApplicationById(smObj.getDocumentId());
			SmAppl appl = mofResDto.getSmAppl();

			// applicationType =
			// retriveApplicationById(smObj.getDocumentId());
			log.info("********Draft Register Account For New Mof********");
			log.info("********getTaskId********" + taskId);
			SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();

			smApplBB.processDraftApplication(appl.getApplType(), Boolean.TRUE,
					appl, mofResDto.getSmSupplier());
			smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
			smApplBB.getManagedBean().setTaskId(taskId);
			smApplBB.getManagedBean().setDraftedAppl(true);
			setNavigationForDraftAppl(appl.getApplType());
			// }

		} else if (resDTO.getTaskName().equalsIgnoreCase(
				SMProcessNameConst.DRAFT_MAINTAIN_ACCOUNT)) {
			log.info(" ******* The selected process name is  Draft Maintain Account******"
					+ resDTO.getProcessName());
			// if (resDTO.getProcessName().equalsIgnoreCase(
			// "Start MOF Account Maintain")) {
			SMMOFRegDataType smObj = (SMMOFRegDataType) resDTO
					.getPojo();
			// TODO Remove this code and get the applicationType From DB
			MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
			mofResDto = retriveApplicationById(smObj.getDocumentId());
			SmAppl appl = mofResDto.getSmAppl();

			// applicationType =
			// retriveApplicationById(smObj.getDocumentId());
			log.info("********Review Application Escalted for account maintain********"
					+ appl.getSupplierType());
			log.info("********getTaskId********" + taskId);
			log.info("********getTaskName********"
					+ this.getManagedBean().getTaskName());
			SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
			// TODO Change this impl to get values from DB and set
			// smSupplier , smAppl of the managedBean

			// applicationType =
			// retriveApplicationById(smObj.getDocumentId());
			log.info("********Draft Register Account For New Mof********");
			log.info("********getTaskId********" + taskId);

			smApplBB.processDraftApplication(appl.getApplType(), Boolean.TRUE,
					appl, mofResDto.getSmSupplier());
			smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
			smApplBB.getManagedBean().setTaskId(taskId);
			smApplBB.getManagedBean().setDraftedAppl(true);
			setNavigationForDraftAppl(appl.getApplType());
			// }

		} else if (resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.REVIEW_CANCELLATION)
				&& resDTO.getProcessName().equalsIgnoreCase(
						SMProcessNameConst.PROCESS_NAME_MOF_REGISTRATION_CANCELLATION)) {
			log.info("**********MOF Registration Cancellation***********"
					+ taskId);
			SMMOFRegCanDataType srcObj = (SMMOFRegCanDataType) resDTO.getPojo();
			this.getManagedBean().setTaskName(resDTO.getTaskName());
			log.info("********Review RegistrationCancellation********");
			log.info("********Assign Review Cancellation APPID********"
					+ srcObj.getDocumentId());
			MofRegistrationResDTO mofResDto = new MofRegistrationResDTO();
			mofResDto = retriveApplicationById(srcObj.getDocumentId());
			SmAppl appl = mofResDto.getSmAppl();
			SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
			smApplBB.getManagedBean();
			// TODO Change this impl to get values from DB and set
			// smSupplier , smAppl of the managedBean
			smApplBB.getManagedBean().setPoUser(false);
			smApplBB.getManagedBean().setSupplierUser(false);
			smApplBB.getManagedBean().setApproverUser(true);
			smApplBB.proceedToApplication(appl.getApplType(), Boolean.TRUE,
					appl, mofResDto.getSmSupplier());
			// smApplBB.getManagedBean().getSmAppl()
			// .setApplId(new Long(smObj.getDocumentId()));
			// smApplBB.getManagedBean().setSmAppl(appl);
			if (srcObj.getSupplierList() != null) {
				smApplBB.getManagedBean().setSupplierToPOList(
						srcObj.getSupplierList());
			}
			smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
			smApplBB.getManagedBean().setTaskId(taskId);
			smApplBB.getManagedBean().setClaimTask(isClaim);
			smApplBB.getManagedBean().setBpmTaskName(resDTO.getTaskName());
			SmAttachmentResDTO attResDto = new SmAttachmentResDTO();
			attResDto = retriveSmAttachmentByDocIdAndType(smApplBB
					.getManagedBean().getSmAppl().getApplId(), smApplBB
					.getManagedBean().getSmAppl().getApplNo().substring(0, 2));
			if (attResDto != null && attResDto.getAttachment() != null) {
				smApplBB.getManagedBean().setCancellationAttachment(
						attResDto.getAttachment());
			}
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/approverDecisionPages/approverCanRegistration_DescisionPage.xhtml");
		} else if (resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.REVIEW_CANCELLATION)
				&& resDTO.getProcessName().equalsIgnoreCase(
						SMProcessNameConst.PROCESS_NAME_MOF_APPL_CANCELLATION)) {
			log.info("**********MOF Application Cancellation***********"
					+ taskId);
			SMMOFAppCanDataType srcObj = (SMMOFAppCanDataType) resDTO.getPojo();
			this.getManagedBean().setTaskName(resDTO.getTaskName());
			log.info("********Review Cancellation********");
			log.info("********Review For deletion for pending application********"
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
			smApplBB.getManagedBean().setPoUser(true);
			smApplBB.getManagedBean().setSupplierUser(false);
			smApplBB.getManagedBean().setApproverUser(false);
			if (srcObj.getSupplierList() != null) {
				smApplBB.getManagedBean().setSupplierToPOList(
						srcObj.getSupplierList());
			}
			SmRemaksResDTO remResDTO = getRemarksByDoc(
					ParameterConst.NEW_SM_APPLN_FOR_DELETE_PENDING_APPL,
					appl.getApplId());
			String suppRemarks = "";
			StringBuilder sb = new StringBuilder();
			if (resDTO != null && remResDTO.getRemarksList() != null
					&& !remResDTO.getRemarksList().isEmpty()) {

				sb.append(remResDTO.getRemarksList().get(0).getRemark());

				// for (SmRemark rem : remResDTO.getRemarksList()) {
				// sb.append(rem.getRemark()).append("\n");
				// }
			}
			if (sb.length()>0) {
				suppRemarks = sb.toString().replaceAll(
						"(?s),", "<br />");
			}
			smApplBB.getManagedBean().setSupplierRemarks(suppRemarks);
			smApplBB.getManagedBean().setSmAppl(appl);
			smApplBB.getManagedBean().setBpmActions(resDTO.getAction());
			smApplBB.getManagedBean().setTaskId(taskId);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/deletePendingMofAppln/cancelPendingApplicationDescisionPage.xhtml");
		} else if (resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.RESPOND_SDO_INQUIRY)) {
			Object bpmObj = new Object();
			SupportingDocInquiryBackingBean suppDocIn = new SupportingDocInquiryBackingBean();

			if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_SUPP_REGISTRATION)) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				suppDocIn.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			} else if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_ACC_MAINTAIN)) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				suppDocIn.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			}

			suppDocIn.getManagedBean().setBpmAction(resDTO.getAction());
			suppDocIn.getManagedBean().setTaskId(taskId);
			suppDocIn.getManagedBean().setBpmPojo(bpmObj);
			suppDocIn.getManagedBean().setInquiryStartDate(resDTO.getCreateDate()!= null ? resDTO.getCreateDate().getTime() : null);
			suppDocIn.getManagedBean().setInquiryEndDate(resDTO.getDueDate() != null ? resDTO.getDueDate().getTime() : null);
			suppDocIn.viewDocInquiry();
			log.info("*********Respond SDO Inquiry********");

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/suppliersupportingdocinquiry/inquiryresponse.xhtml");

		} else if (resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.PROCESSING_FEE)) {
			Object bpmObj = new Object();
			PaymentBackingBean paymentBB = new PaymentBackingBean();
			if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_SUPP_REGISTRATION)) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				paymentBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());

			} else if (resDTO.getProcessName().equalsIgnoreCase(
					SMProcessNameConst.PROCESS_NAME_MOF_ACC_MAINTAIN)) {
				bpmObj = (SMMOFRegDataType) resDTO.getPojo();
				paymentBB.getManagedBean().setAppId(
						(long) ((SMMOFRegDataType) bpmObj).getDocumentId());
			}

			log.info("Doc No>>>>>" + resDTO.getDocNumber());
			paymentBB.getManagedBean().setDocNo(resDTO.getDocNumber());
			paymentBB.getManagedBean().setTaskId(taskId);
			paymentBB.getManagedBean().setBpmPojo(bpmObj);

			paymentBB.retrieveBillInfo();

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/payment/portletViewMode.xhtml");
		}else if(resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.COMPLETE_SV_REPORT)){
			siteVisitTask(resDTO,taskId, isClaim);
		}

	}
	
	private boolean isRedirectToViewOnlyApplication(boolean isClaim,
			BpmResponseDTO resDTO) {
		boolean retVal = false;
		if (!isClaim
				&& (resDTO.getTaskName().equalsIgnoreCase(
								SMProcessNameConst.REVIEW_APPL_ESCALATED)
						|| resDTO.getTaskName().equalsIgnoreCase(
								SMProcessNameConst.REVIEW_APPL)
						|| resDTO.getTaskName().equalsIgnoreCase(
								SMProcessNameConst.REVIEW_APPL_SPLCAT)
						|| resDTO.getTaskName().equalsIgnoreCase(
								SMProcessNameConst.VERIFY_APPL) || resDTO
						.getTaskName()
						.equalsIgnoreCase(
								SMProcessNameConst.REVIEW_SITE_VISIT_AFTER_APPROVE))) {
			retVal = true;
		}
		return retVal;
	}
	
	private void siteVisitTask(BpmResponseDTO resDTO,String taskId, boolean isClaim){
		Object svObj =  null;
		Long docId = null;
		String svTaskId = null;
		
		if (resDTO.getPojo() instanceof SMMOFRegDataType) {
			svObj = resDTO.getPojo();
			SMMOFRegDataType mofRegDT = (SMMOFRegDataType) resDTO.getPojo();
			docId = mofRegDT.getDocumentId();
			svTaskId = mofRegDT.getSvoTaskId();
		}else if(resDTO.getPojo() instanceof SMMOFRegDataType) {
			svObj = resDTO.getPojo();
			SMMOFRegDataType mofRegMainDT = (SMMOFRegDataType) resDTO.getPojo();
			docId = mofRegMainDT.getDocumentId();
			//svTaskId = mofRegMainDT.get
		}else if(resDTO.getPojo() instanceof SMSiteVisitDataType){
			svObj = resDTO.getPojo();
			SMSiteVisitDataType siteVisitDT = (SMSiteVisitDataType) resDTO.getPojo();
			docId = siteVisitDT.getDocumentId();
			svTaskId = siteVisitDT.getSvoTaskId();
		}
		
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		
		log.info("----------Common SIte Visit--------" + resDTO.getTaskName());
		if(resDTO.getTaskName().equalsIgnoreCase("Assign Site Visit")){
			SvcSiteAssignBackingBean svcBB = new SvcSiteAssignBackingBean();
			svcBB.renewSesionObject();
			
			log.info("------Assign Site Visit AppID-------------" + docId);
			svcBB.getManagedBean().setAppId(docId);
			svcBB.getManagedBean().setBpmAction(resDTO.getAction());
			svcBB.getManagedBean().setTaskId(taskId);
			//svcBB.getManagedBean().setAppType(svObj.getAppType());
			svcBB.getManagedBean().setBpmPojo(svObj);
			svcBB.retrieveSiteAssignInfo(docId);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/svcassignment/assignSiteVisitView");
		}else if(resDTO.getTaskName().equalsIgnoreCase(SMProcessNameConst.COMPLETE_SV_REPORT)){
			log.info("------Complete Site Visit Report-------------" + docId);
			SvoSiteVisitBackingBean svoBB = new SvoSiteVisitBackingBean();
			svoBB.renewSesionObject();
			log.info("Document id>>>>" + docId);
			//svoBB.getManagedBean().setAppId(svObj.getDocumentId());
			svoBB.getManagedBean().setSvTaskId(Long.valueOf(svTaskId));
			svoBB.getManagedBean().setBpmAction(resDTO.getAction());
			svoBB.getManagedBean().setTaskId(taskId);
			//svoBB.getManagedBean().setAppType(svObj.getAppType());
			svoBB.getManagedBean().setBpmPojo(svObj);
			svoBB.retrieveSiteVisitInfo("");
			svoBB.getManagedBean().setClaimTask(isClaim);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/svositevisit/siteVisitMain");
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
		log.info("****** Make Payment **********");

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
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			// TODO changed to satisfy enhancement of adding the bank section
			// "/xhtml/sm/addAdditionalCategoryCodes/smAddtnlCatApplnSideMenu_BasePage.xhtml");
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
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			// TODO changed to satisfy enhancement of adding the bank section
			// "/xhtml/sm/addAdditionalCategoryCodes/smAddtnlCatApplnSideMenu_BasePage.xhtml");
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
			log.info(" *** DOC MODE ****"
					+ smApplBB.getManagedBean().getSmAppl()
							.getSupportingDocMode());
			String out = "";
			if (smApplBB
					.getManagedBean()
					.getSmAppl()
					.getSupportingDocMode()
					.equalsIgnoreCase(
							SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
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
			log.info("***** THIS IS DEFAULT PAGE --- i.e. no matching type ---"
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

	private MofRegistrationResDTO retriveApplicationById(long applId) {

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
			log.info("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}

	}

	private SmAttachmentResDTO retriveSmAttachmentByDocIdAndType(long applId,
			String docType) {

		ISmAttachmentSO attchSO = null;

		try {
			attchSO = (ISmAttachmentSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISmAttachmentSO.class);
			SmAttachmentReqDTO reqDto = new SmAttachmentReqDTO();
			reqDto.setDocId(applId);
			reqDto.setDocType(docType);
			SmAttachmentResDTO resDTO = attchSO
					.retrieveSmAttachmentByDocIdAndType(reqDto);
			if (resDTO == null
					|| (resDTO != null && resDTO.getAttachment() == null)) {
				throw new EpException(
						"Invalid Application : No file uploaded for the application");
			}
			return resDTO;

		} catch (Exception e) {
			log.info("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			e.printStackTrace();
			throw new EpException(
					"Invalid Application : No file uploaded for the application");
		} finally {
			attchSO = null;
		}

	}
	// private SmWorkflowStatus getCurrentActiveWokFlowStatus(Long applId) {
	// log.info("*** This is resubmit to get po ****** ");
	// SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
	// reqDTO.setApplId(applId);
	// ISmWorkFlowStatusSO reqSO = (ISmWorkFlowStatusSO) BusinessDelegate
	// .getInstance()
	// .getServiceObject("sm", ISmWorkFlowStatusSO.class);
	// SmWorkFlowStatusResDTO resDTO = reqSO
	// .retrieveCurrentWorkFlowStatusByDocId(reqDTO);
	// if (resDTO != null && resDTO.getWorkflowStatus() != null) {
	// return resDTO.getWorkflowStatus();
	// } else {
	// return null;
	// }
	// }

	// private void setPmStatusDesc(String statusId,SmApplicationManagedBean
	// managedBean) {
	// log.info("****** Status Id ******" + statusId);
	// StatusReqDTO reqDTO = new StatusReqDTO();
	// reqDTO.setStatusId(Long.parseLong(statusId));
	// reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
	// IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
	// .getServiceObject("pm", IStatusSO.class);
	// StatusResDTO resDTO = reqSO.retrieveStatusDescById(reqDTO);
	// if (resDTO != null && resDTO.getPmStatusDesc() != null) {
	// managedBean.setCurrentStatus(
	// resDTO.getPmStatusDesc().getStatusName());
	// }
	// }
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