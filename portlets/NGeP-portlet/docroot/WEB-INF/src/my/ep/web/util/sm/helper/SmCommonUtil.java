package my.ep.web.util.sm.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.sm.ApplType;
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
	
	public static String md5(String str) {

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			System.out.println(ex.getMessage());
			//	            return;
		}
		md.update(str.getBytes());
		byte[] md5Dig = md.digest();
		
		return new String(Hex.encodeHex(md5Dig));
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
	
	public static PmCountry retrieveCountryByCountryCode(String countryCode){
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(countryCode);
		
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		
		return resDTO.getPmCountry();
	}
	
	public static List<PmStatusDesc> retrievePmStatusList(String moduleCode,String docType, String lang){
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setDocType(docType);
		reqDTO.setModuleCode(moduleCode);
		reqDTO.setLanguageCode(lang);
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusDescList(reqDTO);
		
		return resDTO.getStatusDescList();
	}
	
	public static Map<Long,PmStatusDesc> converPmStatusListToMap(List<PmStatusDesc> pmStatusDescLs){
		Map<Long, PmStatusDesc> statDescMap= new HashMap<Long, PmStatusDesc>();
		for(PmStatusDesc pmStatDesc : pmStatusDescLs){
			statDescMap.put(pmStatDesc.getPmStatus().getStatusId(), pmStatDesc);
		}
		return statDescMap;
	}
	
	public static String getApplTypeDesc(String applType){
		System.out.println("--------getApplTypeDesc start--------" + applType);
		String apType = "";

		switch(applType){
		case "O" : 
			apType = ApplType.O.value();
			break;

		case "N" : 
			apType = ApplType.N.value();
			break;

		case "B" : 
			apType = ApplType.B.value();
			break;

		case "A" : 
			apType = ApplType.A.value();
			break;

		case "U" : 
			apType = ApplType.U.value();
			break;

		case "R" : 
			apType = ApplType.R.value();
			break;

		case "C" : 
			apType = ApplType.C.value();
			break;
		}
		
		System.out.println("--------getApplTypeDesc--------" + apType);
		
		return apType;
	}
	
}
