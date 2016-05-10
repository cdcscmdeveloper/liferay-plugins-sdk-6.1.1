package my.ep.web.backingbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.List;

import my.ep.biz.service.osb.ntf.INtfClientSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.osb.ntf.NtfRequestDTO;
import my.ep.dto.osb.ntf.NtfResponseDTO;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

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
	         INtfClientSO ntfClientSO = (INtfClientSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.NTF, INtfClientSO.class);

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
	             NtfResponseDTO resDTO = ntfClientSO.processAndPublish(reqDTO);
	             System.out.println(resDTO.getStatusCode());
	             System.out.println(resDTO.getSeverity());
	             System.out.println(resDTO.getStatusDesc());
	         } catch (Exception e) {
	             e.printStackTrace();
	         }
	         System.out.println("END");
	     }
	}

