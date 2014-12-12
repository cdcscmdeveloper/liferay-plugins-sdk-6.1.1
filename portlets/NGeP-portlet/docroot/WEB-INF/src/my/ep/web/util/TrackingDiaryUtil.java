/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util;

import java.sql.Timestamp;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class TrackingDiaryUtil {

	public static final String CM10000 = "Permohonan Kod UNSPSC <VAR1> telah dihantar kepada Pentadbir Katalog untuk semakan.";
	public static final String CM10001 = "Permohonan Kod UNSPSC <VAR1> telah disyorkan.";
	public static final String CM10002 = "Permohonan Kod UNSPSC <VAR1> telah dihantar kepada Pentadbir Kodifikasi untuk penciptaan UNSPSC baru.";
	public static final String CM10003 = "";
	public static final String CM10004 = "";
	public static final String CM10005 = "";
	public static final String CM10006 = "";
	public static final String CM10007 = "";
	public static final String CM10008 = "";
	public static final String CM10009 = "";
	public static final String CM10010 = "";
	public static final String CM10011 = "";
	public static final String CM10012 = "";
	public static final String CM10013 = "";
	public static final String CM10014 = "";
	public static final String SM10000 = "";
	public static final String SM10001 = "";
	public static final String SM10002 = "";
	public static final String SM10003 = "";
	public static final String SM10004 = "";
	public static final String SM10005 = "";
	public static final String SM10006 = "";
	public static final String SM10007 = "";
	public static final String SM10008 = "";
	public static final String SM10009 = "";
	public static final String SM10010 = "";
	public static final String SM10011 = "";
	public static final String SM10012 = "";
	public static final String SM10013 = "";
	public static final String SM10014 = "";
	public static final String S110000 = "";
	public static final String S110001 = "";
	public static final String S110002 = "";
	public static final String S110003 = "";
	public static final String S110004 = "";
	public static final String S110005 = "";
	public static final String S110006 = "";
	public static final String S110007 = "";
	public static final String S110008 = "";
	public static final String S110009 = "";
	public static final String S110010 = "";
	public static final String S110011 = "";
	public static final String S110012 = "";
	public static final String S110013 = "";
	public static final String S110014 = "";
	public static final String FL10000 = "";
	public static final String FL10001 = "";
	public static final String FL10002 = "";
	public static final String FL10003 = "";
	public static final String FL10004 = "";
	public static final String FL10005 = "";
	public static final String FL10006 = "";
	public static final String FL10007 = "";
	public static final String FL10008 = "";
	public static final String FL10009 = "";
	public static final String FL10010 = "";
	public static final String FL10011 = "";
	public static final String FL10012 = "";
	public static final String FL10013 = "";
	public static final String FL10014 = "";
	public static final String CT10000 = "";
	public static final String CT10001 = "";
	public static final String CT10002 = "";
	public static final String CT10003 = "";
	public static final String CT10004 = "";
	public static final String CT10005 = "";
	public static final String CT10006 = "";
	public static final String CT10007 = "";
	public static final String CT10008 = "";
	public static final String CT10009 = "";
	public static final String CT10010 = "";
	public static final String CT10011 = "";
	public static final String CT10012 = "";
	public static final String CT10013 = "";
	public static final String CT10014 = "";

	private TrackingDiaryUtil() {
	}
	
	public static boolean insertTrackingDiary(String actionDesc, Long actionedBy, String actionedByRole, 
			Long docId, String docNo, String docType, Long groupId, Long statusId, String groupDocType) {
	
		System.out.println("In insertTrackingDiary  action...................");

		PmTrackingDiary pmTrackingDiary = new PmTrackingDiary();
		pmTrackingDiary.setActionDesc(actionDesc);
		pmTrackingDiary.setActionedBy(actionedBy);
		pmTrackingDiary.setActionedDate(new Timestamp(System.currentTimeMillis()));
		pmTrackingDiary.setDocId(docId);
		pmTrackingDiary.setDocNo(docNo);
		pmTrackingDiary.setDocType(docType);
		pmTrackingDiary.setGroupId(groupId);
		pmTrackingDiary.setStatusId(statusId);
		pmTrackingDiary.setGroupDocType(groupDocType);
	
		TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
		reqDTO.setPmTrackingDiary(pmTrackingDiary);
		
		ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
		TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
		resDTO = trackingDiarySO.saveOrUpdateTrackingDiary(reqDTO);
		
		return true;
	}


}