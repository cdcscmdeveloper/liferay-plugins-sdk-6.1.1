package my.ep.web.util.sm.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.id.Hex;
import org.apache.log4j.Logger;

public class SmCommonUtil {
	private static Logger log = Logger.getLogger(SmCommonUtil.class);
	
	public static String hashString(String str) {
		
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
//            return;
        }
        md.update(str.getBytes());
        byte[] shaDig = md.digest();
        return new String(Hex.encodeHex(shaDig));
	}
	
	public static List<PmParameterDesc> retrieveParameterDescList(String paramType,String lang){
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(paramType);
		reqDTO.setLanguageCode(lang);
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterResDTO resDTO = reqSO.retrieveParameterDescListByType(reqDTO);
		
		return resDTO.getParameterDescList();
	}
}
