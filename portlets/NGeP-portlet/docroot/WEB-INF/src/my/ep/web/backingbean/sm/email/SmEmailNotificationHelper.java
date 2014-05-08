package my.ep.web.backingbean.sm.email;

import java.util.ArrayList;
import java.util.List;

import my.ep.ntf.dto.NtfRequestDTO;
import my.ep.ntf.dto.NtfResponseDTO;
import my.ep.ntf.service.INtfSO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.util.lookupservice.EpNtfBusinessDelegate;

public class SmEmailNotificationHelper {

	public static void sendEmail(String templateId, List<String> emailLst,
			List<String> subParams, List<String> contentParams) {
		System.out.println("initiateTask******");
		INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance()
				.getOSBServiceObject(INtfSO.class);

		NtfRequestDTO reqDTO = new NtfRequestDTO();

		reqDTO.setTemplateId(templateId);
		reqDTO.setEmailToArr(emailLst.toArray(new String[emailLst.size()]));
		reqDTO.setSubParaArr(subParams.toArray(new String[subParams.size()]));
		reqDTO.setCntParaArr(contentParams.toArray(new String[contentParams
				.size()]));

		try {
			NtfResponseDTO resDTO = ntfSO.processAndPublishViaEmail(reqDTO);
			System.out.println(resDTO.getStatusCode());
			System.out.println(resDTO.getSeverity());
			System.out.println(resDTO.getStatusDesc());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("END");
	}

	public static void sendApplicaitonRejectNotifMail(SmAppl smAppl) {
		String templateId = "399999";
		List<String> emailLst = new ArrayList<String>();
		List<String> contList = new ArrayList<String>();
		for (SmPersonnel per : smAppl.getSmPersonnels()) {
			System.out.println("** smAppl.getSmPersonnels().size() ** "
					+ smAppl.getSmPersonnels().size());
			if (per.getEpRole() != null
					&& per.getEpRole().equalsIgnoreCase("A")) {
				emailLst.add(per.getEmail());
				contList.add(per.getName());
				System.out.println(" **The email id ***" + per.getEmail());
				break;
			}
		}

		List<String> subList = new ArrayList<String>();
		subList.add("SM Application - " + smAppl.getApplNo() + " Rejected");

		contList.add("Rejection codes");
		for (SmApplRejectReason res : smAppl.getSmApplRejectReasons()) {
			contList.add(res.getPmReason().getPmReasonDescs().get(0)
					.getReasonDesc());
		}

		sendEmail(templateId, emailLst, subList, contList);

	}
}
