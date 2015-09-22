package my.ep.web.integration.common;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.ntf.dto.Attachment;
import my.ep.ntf.dto.NtfRequestDTO;
import my.ep.ntf.service.INtfSO;

import org.apache.log4j.Logger;

/**
 * 
 */

/**
 * @author P Ganga prasad
 * 
 */
public class TestNtfViaEmail {
	private static Logger log = BatchLogger.getLogger(TestNtfViaEmail.class);
	
//	public static void main(String args[]) {
//		TestNtfViaEmail testNtfViaEmail = new TestNtfViaEmail();
//		testNtfViaEmail.sendFilestoMail();
//	}
	
	
	
	
	
//	public static void main(String args[]) {
//		log.info("initiateTask******");
//		INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance()
//				.getOSBServiceObject(INtfSO.class);
//
//		NtfRequestDTO reqDTO = new NtfRequestDTO();
//		reqDTO.setTemplateId("800025");
//
//		List<String> subParaLst = new ArrayList<String>();
////		subParaLst.add("AssociatedCompany.txt");
//		subParaLst.add("10/06/2015");
//		reqDTO.setSubParaArr(subParaLst.toArray(new String[subParaLst.size()]));
//		List<String> cntParaLst = new ArrayList<String>();
////		cntParaLst.add("<a href=http://ngepdevwps01.ngep.com:8888/web/guest/supplier-user-activation?pr=9333&sp=d14bf0eeb496b177efb56111bbc0bdf7ee03d8761fd23648bcd5cfe36f47db26&kp=5519343f26ccbe1c65af6ef4df4c9adca62e62234732cff0ddfacb764f240727>Click to activate</a>");
////		cntParaLst.add("AssociatedCompany.txt");
//		cntParaLst.add("10/06/2015");
////		cntParaLst.add("1402477123009");
//		reqDTO.setCntParaArr(cntParaLst.toArray(new String[cntParaLst.size()]));
//
//		List<String> emailLst = new ArrayList<String>();
//		emailLst.add("gangaprasad21@gmail.com");
////		emailLst.add("boyapativenu@gmail.com");
////		emailLst.add("alextan@commercedc.com.my");
////		emailLst.add("prasad@archisoftconsulting.com");
//		// emailLst.add("relax522@gmail.com");
//		// emailLst.add("relax5220@hotmail.com");
//		// emailLst.add("relax522@yahoo.com");
//		reqDTO.setEmailToArr(emailLst.toArray(new String[emailLst.size()]));
//
////		 Attachments
//		 String path = "C:/Integration/Upload/20042015/";
//		 String filename1 = "EquityOwner.txt";
////		 String filename2 = "BoardOfDirector.txt";
////		 String filename3 = "ManagementPersonnel.txt";
////		 String filename4 = "Signatory.txt";
//		 
//		 File folder = new File("C:/Integration/Upload/20042015/");
//		 File[] listOfFiles = folder.listFiles();
//		 
//		 Attachment[] attArr = new Attachment[1];
////		 for (int i = 0; i < listOfFiles.length; i++) {
////			 attArr[i] = new Attachment();
//////		      if (listOfFiles[i].isFile()) {
////		    	  
////		 		  attArr[i].setData(processToByte(path+listOfFiles[i].getName()));
////		 		  log.info("File " + listOfFiles[i].getName());
//////		      } 
////		    }
//		
//		
////		log.info("attArr :"+reqDTO.getAttachmentArr().length);
//		 
//		 attArr[0] = new Attachment();
//		 attArr[0].setData(processToByte(path+filename1));
////		 attArr[0].setFilename(filename1);
////		 attArr[1] = new Attachment();
////		 attArr[1].setData(processToByte(path+filename2));
////		 attArr[1].setFilename(filename2);
////		 attArr[2] = new Attachment();
////		 attArr[2].setData(processToByte(path+filename3));
////		 attArr[2].setFilename(filename3);
////		 attArr[3] = new Attachment();
//////		 attArr[3].setData(processToByte(path+filename4));
//////		 attArr[3].setFilename(filename4);
//		
//		reqDTO.setAttachmentArr(attArr);
//		ntfSO.processAndPublishViaEmail(reqDTO);
//	}
//
	private static byte[] processToByte(final String filename) {
		File file = new File(filename);

		byte[] b = new byte[(int) file.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(b);
			fileInputStream.close();
		} catch (Exception e1) {
			log.info("Error Reading The File.");
			e1.printStackTrace();
		}
		return b;
	}
	
	public void sendFilestoMail(INtfSO ntfSO){
		
		Date curDate = new Date();

		SimpleDateFormat format = null;
		String DateToStr1,DateToStr2;

		format = new SimpleDateFormat("dd/MM/yyyy");
		DateToStr1 = format.format(curDate);

		format = new SimpleDateFormat("ddMMyyyy");
		DateToStr2 = format.format(curDate)+"/";
		
		log.info("sendFilestoMail");
//		INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance()
//				.getOSBServiceObject(INtfSO.class);

		NtfRequestDTO reqDTO = new NtfRequestDTO();
		reqDTO.setTemplateId("800025");

		List<String> subParaLst = new ArrayList<String>();
		subParaLst.add("None");
		subParaLst.add(DateToStr1);
		reqDTO.setSubParaArr(subParaLst.toArray(new String[subParaLst.size()]));
		List<String> cntParaLst = new ArrayList<String>();
		cntParaLst.add("None");
		cntParaLst.add(DateToStr1);
		reqDTO.setCntParaArr(cntParaLst.toArray(new String[cntParaLst.size()]));

		List<String> emailLst = new ArrayList<String>();
		emailLst.add("gangaprasad21@gmail.com");
		emailLst.add("integration-test@commercedc.com.my");
//	
//		emailLst.add("ahmadfaizul@commercedc.com.my");
		emailLst.add("mohdyusri@commercedc.com.my");
//		emailLst.add("ahmadasyraf@commercedc.com.my");
//		emailLst.add("sharudin@commercedc.com.my");
		reqDTO.setEmailToArr(emailLst.toArray(new String[emailLst.size()]));

//		 Attachments
		 String filePath = IntegrationConstants.EPERUNDING_UPLOAD_DIR+DateToStr2;
		 
		 File folder = new File(filePath);
		 File[] listOfFiles = folder.listFiles();
		 
		 Attachment[] attArr = new Attachment[listOfFiles.length];
		 Attachment attachment = null;
		 for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		    	  attachment = new Attachment();
		    	  attachment.setData(processToByte(filePath+listOfFiles[i].getName()));
		    	  attachment.setFilename(listOfFiles[i].getName());
		    	  attArr[i] = attachment;
		      } 
		    }
		
		reqDTO.setAttachmentArr(attArr);
		ntfSO.processAndPublishViaEmail(reqDTO);
	}
}
