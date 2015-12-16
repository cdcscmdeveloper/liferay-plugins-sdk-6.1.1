package my.ep.web.backingbean.sc.directpurchase;

	import java.util.ArrayList;
import java.util.List;

import my.ep.ntf.dto.NtfRequestDTO;
import my.ep.ntf.dto.NtfResponseDTO;
import my.ep.ntf.service.INtfSO;
import my.ep.util.lookupservice.EpNtfBusinessDelegate;

	/**
	  *
	  */

	/**
	  * @author MD
	  *
	  */
	public class TestNtfOSBRs {
	     public static void main(String args[]) {
	         System.out.println("initiateTask******");
	         INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance()
	                 .getOSBServiceObject(INtfSO.class);
	         //TODO NTF
	         //INtfClientSO ntfClientSO = (INtfClientSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.MDB, INtfClientSO.class);

	         NtfRequestDTO reqDTO = new NtfRequestDTO();
	         reqDTO.setTemplateId("399999");
	         reqDTO.setSenderId("4");
	         // reqDTO.setSendEmailOnly(true);

	         List<String> usrLst = new ArrayList<String>();
	          usrLst.add("239");
	         // usrLst.add("28");
	         reqDTO.setUserIdArr(usrLst.toArray(new String[usrLst.size()]));

	         List<String> subParaLst = new ArrayList<String>();
	         subParaLst.add("#SM#");
	         reqDTO.setSubParaArr(subParaLst.toArray(new 
	String[subParaLst.size()]));
	         List<String> cntParaLst = new ArrayList<String>();
	         cntParaLst.add("Jenson");

	         cntParaLst
	                 .add("<a href=\"http://google.com.my\">dadfas</a> "+
	"Hello&lt;add&gt;World");
	         cntParaLst.add("U_103301");
	         cntParaLst.add("123");
	         reqDTO.setCntParaArr(cntParaLst.toArray(new 
	          String[cntParaLst.size()]));

	         try {
	             NtfResponseDTO resDTO = ntfSO.processAndPublish(reqDTO);
   	             //TODO NTF
	             //NtfResponseDTO resDTO = ntfClientSO.processAndPublish(reqDTO);
	             System.out.println(resDTO.getStatusCode());
	             System.out.println(resDTO.getSeverity());
	             System.out.println(resDTO.getStatusDesc());
	         } catch (Exception e) {
	             e.printStackTrace();
	         }
	         System.out.println("END");
	     }
	}

