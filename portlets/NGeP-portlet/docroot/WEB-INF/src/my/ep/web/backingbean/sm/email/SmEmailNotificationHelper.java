package my.ep.web.backingbean.sm.email;

import java.util.ArrayList;
import java.util.List;

import my.ep.biz.service.osb.ntf.INtfClientSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.NotificationTextConst;
import my.ep.dto.osb.ntf.NtfRequestDTO;
import my.ep.dto.osb.ntf.NtfResponseDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.FacesScopes;

public class SmEmailNotificationHelper {

	public static void sendEmail(String templateId, List<String> emailLst,
			List<String> subParams, List<String> contentParams) {
		System.out.println("initiateTask******");
		INtfClientSO ntfClientSO = (INtfClientSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.MDB, INtfClientSO.class);

		NtfRequestDTO reqDTO = new NtfRequestDTO();

		reqDTO.setTemplateId(templateId);
		reqDTO.setEmailToArr(emailLst.toArray(new String[emailLst.size()]));
		reqDTO.setSubParaArr(subParams.toArray(new String[subParams.size()]));
		reqDTO.setCntParaArr(contentParams.toArray(new String[contentParams
				.size()]));

		try {
			NtfResponseDTO resDTO = ntfClientSO
					.processAndPublishViaEmail(reqDTO);
			if (!NotificationTextConst.STATUS_CODE_SUCCESS
					.equals(resDTO.getStatusCode())) {
				throw new EpException(resDTO.getStatusDesc());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("END");
	}

	public static void sendNotif(String templateId,
			List<String> receiverIdList, String senderId,
			List<String> subParams, List<String> contentParams) {
		System.out.println("initiateTask******");
		//TODO NTF
		INtfClientSO ntfClientSO = (INtfClientSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.MDB, INtfClientSO.class);

		NtfRequestDTO reqDTO = new NtfRequestDTO();

		reqDTO.setTemplateId(templateId);
		if (senderId != null) {
			reqDTO.setSenderId(senderId);
		}
		reqDTO.setUserIdArr(receiverIdList.toArray(new String[receiverIdList
				.size()]));
		reqDTO.setSubParaArr(subParams.toArray(new String[subParams.size()]));
		reqDTO.setCntParaArr(contentParams.toArray(new String[contentParams
				.size()]));

		try {
			NtfResponseDTO resDTO = ntfClientSO
					.processAndPublishViaEmail(reqDTO);
			if (!NotificationTextConst.STATUS_CODE_SUCCESS
					.equals(resDTO.getStatusCode())) {
				throw new EpException(resDTO.getStatusDesc());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("END");
	}

	public static void sendApplicaitonRejectNotifMail(SmAppl smAppl,
			Long senderId, String reasonDesc) {
		String templateId = "400001";
		String senderIdVal = null;
		List<String> receiverList = new ArrayList<String>();
		List<String> contList = new ArrayList<String>();
		FacesScopes fs = new FacesScopes();
		for (SmPersonnel per : smAppl.getSmPersonnels()) {
			System.out.println("** smAppl.getSmPersonnels().size() ** "
					+ smAppl.getSmPersonnels().size());
			if (per.getEpRole() != null
					&& (per.getEpRole().equalsIgnoreCase(
							RoleCode.MOF_SUPPLIER_ADMIN.value()) || per
							.getEpRole().equalsIgnoreCase(
									RoleCode.BASIC_SUPPLIER_ADMIN.value()))) {
				// emailLst.add(per.getEmail());
				if (per.getPmUser() != null) {
					receiverList.add(per.getPmUser().getUserId().toString());
				} else {
					System.out
							.println(" **** THE MOF ADMIN IS AVAILABLE - BUT NO RECORD FOR THIS USER IN PM USER TO SEND MAIL******* ");
				}
				contList.add(per.getName());
				System.out.println(" **The email id ***" + per.getEmail());
				break;
			} else {
				System.out
						.println(" **** NO USER WITH THE MOF ADMIN IS AVAILABLE TO SEND MAIL******* ");
			}
		}

		if (senderId != null) {
			senderIdVal = senderId.toString();
		}
		String parameterString = "apId=" + smAppl.getApplId();
		String activationUrl = "http://" + fs.getRequest().getServerName()
				+ ":" + fs.getRequest().getServerPort()
				+ SupplierManagementConst.RESUBMIT_APPL_PATH + parameterString;
		System.out.println("**Activation Url=" + activationUrl);

		List<String> subList = new ArrayList<String>();
		subList.add(getApplicationDesc(smAppl.getApplType()));
		subList.add(smAppl.getApplNo());

		contList.add(smAppl.getApplNo());
		contList.add(getApplicationDesc(smAppl.getApplType()));
		// StringBuffer sb = new StringBuffer();
		// int count = 0;
		// for (SmApplRejectReason res : smAppl.getSmApplRejectReasons()) {
		// sb.append(++count).append(".)");
		// sb.append(res.getPmReason().getPmReasonDescs().get(0)
		// .getReasonDesc());
		// sb.append("\n");
		// }
		System.out.println("*** Reason Desc Before email ****" + reasonDesc);
		contList.add(reasonDesc);
		contList.add("<br/><a href=" + activationUrl
				+ ">Click to resubmit</a><br />");

		sendNotif(templateId, receiverList, senderIdVal, subList, contList);

	}

	private static String getApplicationDesc(String appType) {
		String s = null;
		switch (appType) {

		case "N":
			s = "New MOF Registration";
			break;
		case "A":
			s = "Additional Category Codes";
			// PROCEED_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = "Bumputera Status";
			// PROCEED_BUMI_APPLN;
			break;
		case "U":
			s = "Update MOF Profile";
			// PROCEED_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = "Renew MOF";
			// PROCEED_RENEW_MOF_APPLN;
			break;
		case "C":
			s = "Cancel MOF Registration";
			break;
		default:
			break;

		}
		return s;
	}
}
