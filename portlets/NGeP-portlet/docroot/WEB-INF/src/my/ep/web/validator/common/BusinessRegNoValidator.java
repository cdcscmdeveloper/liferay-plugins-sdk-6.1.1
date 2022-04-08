package my.ep.web.validator.common;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.util.MessageUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang3.StringUtils;

@FacesValidator("busRegNoValidator")
public class BusinessRegNoValidator implements Validator {

	private static List<String> robList = Arrays.asList("C", "F");
	private static List<String> rocList = Arrays.asList("I", "J");
	private static List<String> llpList = Arrays.asList("B");
	private static List<String> llpLastThreeCombinations = Arrays.asList("LGN",
			"FGN", "LCS", "FCS", "LLW", "FLW", "LCA", "FCA");

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		UIInput dropdown = (UIInput) component.getParent().findComponent(
				"busTypeId");
		String businessType = "";

		if (value != null && StringUtils.isNotBlank(value.toString())) {
			if (dropdown != null && dropdown.getValue() != null) {
				businessType = dropdown.getValue().toString();

				if (robList.contains(businessType)) {
				//30987 - start
					String rob = value.toString();
					rob=rob.trim();
					validateRob(rob);
					value = rob.replace(value.toString(), rob);
				} else if (rocList.contains(businessType)) {
					String roc = value.toString();
					roc = roc.trim();	
					validateRoc(roc);
					value = roc.replace(value.toString(),roc);
				//30987 - end
				} else if (llpList.contains(businessType)) {
					validateLlp(value.toString());
				} else {
					validateRos(businessType, value);
				}
			}
		}
	}
	
	

	/**
	 * Validate ROS
	 * 
	 * @param String
	 *            businessType
	 * @param String
	 *            value
	 */
	private void validateRos(String businessType, Object value) {
		if (businessType.equals("K")) {
			validateRosPertubuhan(value.toString());
		} else if (businessType.equals("L")) {
			validateRosKoperasi(value.toString());
		}
	}
	/**
	 * Validate ROC
	 * 
	 * @param String
	 *            rocNo
	 */
	private void validateRoc(String rocNo) {
		//30987 
		rocNo = rocNo.trim();
		String[] splitRoc = rocNo.split("-");
		String splitRoc1 = "";
		String splitRoc2 = "";
		if (splitRoc != null && splitRoc.length > 1) {
			splitRoc1 = splitRoc[0] != null ? splitRoc[0] : "";
			splitRoc2 = splitRoc[1] != null ? splitRoc[1] : "";
			if (splitRoc1.length() > 7) {
				throwErrorMsg("lbl.sm.businessregno.beforedash.roc");
			} else {
				if (StringUtils.isBlank(splitRoc2) || splitRoc2.length() != 1) {
					throwErrorMsg("lbl.sm.businessregno.afterdash.roc");
				}
			}
		} else {
			throwErrorMsg("lbl.sm.businessregno.notvalid.roc");
		}
	}

	/**
	 * Validate ROB
	 * 
	 * @param String
	 *            robNo
	 */
	private void validateRob(String robNo) {
		String[] splitRob = robNo.split("-");
		//30987 
		robNo = robNo.trim();
		String splitRob1 = "";
		String splitRob2 = "";
		if (splitRob != null && splitRob.length > 1) {
			splitRob1 = splitRob[0] != null ? splitRob[0] : "";
			splitRob2 = splitRob[1] != null ? splitRob[1] : "";
			if (splitRob1.length() > 9) {
				throwErrorMsg("lbl.sm.businessregno.beforedash.rob");
			} else {
				if (StringUtils.isBlank(splitRob2) || splitRob2.length() != 1) {
					throwErrorMsg("lbl.sm.businessregno.afterdash.rob");
				}
			}
		} else {
			throwErrorMsg("lbl.sm.businessregno.notvalid.rob");
		}
	}

	/**
	 * Validate LLP
	 * 
	 * @param String
	 *            llpNo
	 */
	private void validateLlp(String llpNo) {
		String[] splitLlp = llpNo.split("-");
		String splitLlp1 = "";
		String splitLlp2 = "";
		if (splitLlp != null && splitLlp.length == 2) {
			splitLlp1 = splitLlp[0] != null ? splitLlp[0] : "";
			splitLlp2 = splitLlp[1] != null ? splitLlp[1] : "";
			if (llpNo.length() > 30) {
				throwErrorMsg("lbl.sm.businessregno.max.llp");
			} else {
				if (splitLlp1.length() < 3
						|| !splitLlp1.substring(0, 3).equals("LLP")) {
					throwErrorMsg("lbl.sm.businessregno.notvalid.firstthree.llp");
				} else {
					if (StringUtils.isBlank(splitLlp2)
							|| splitLlp2.length() != 3) {
						throwErrorMsg("lbl.sm.businessregno.afterdash.llp");
					} else if (!llpLastThreeCombinations.contains(splitLlp2)) {
						throwErrorMsg("lbl.sm.businessregno.notvalid.lastthreecombination.llp");
					}
				}
			}

		} else {
			throwErrorMsg("lbl.sm.businessregno.notvalid.llp");
		}
	}

	/**
	 * Validate ROS Koperasi
	 * 
	 * @param String
	 *            rosNo
	 */
	private void validateRosKoperasi(String rosNo) {
		
		// #27601 - Start - 8/2/2021
//		String[] splitRos = rosNo.split("-");
//		String splitRos1 = "";
//		String splitRos2 = "";
//		String splitRos3 = "";
//
//		if (splitRos != null && splitRos.length == 3) {
//			splitRos1 = splitRos[0];
//			splitRos2 = splitRos[1];
//			splitRos3 = splitRos[2];
//			if (splitRos1.length() != 1 || StringUtils.isNumeric(splitRos1)) {
//				throwErrorMsg("lbl.sm.businessregno.invalid.format.roskoperasiskm");
//			} else if (splitRos2.length() != 1
//					|| !StringUtils.isNumeric(splitRos2)) {
//				throwErrorMsg("lbl.sm.businessregno.invalid.format.roskoperasiskm");
//			} else if (splitRos3.length() != 4
//					|| !StringUtils.isNumeric(splitRos3)) {
//				throwErrorMsg("lbl.sm.businessregno.invalid.format.roskoperasiskm");
//			}
//		} else {
//			throwErrorMsg("lbl.sm.businessregno.notvalid.roskoperasiskm");
//		}
		
		if(rosNo != null && !StringUtils.isNumeric(rosNo)){
			throwErrorMsg("lbl.sm.businessregno.notvalid.enhancementroskoperasiskm");
		} else if (rosNo != null && StringUtils.isNumeric(rosNo) && rosNo.length() != 12){
			throwErrorMsg("lbl.sm.businessregno.notvalid.enhancementroskoperasiskm");
		}
		
		// #27601 - End - 8/2/2021
	}

	/**
	 * Validate ROS Pertubuhan
	 * 
	 * @param String
	 *            rosNo
	 */
	private void validateRosPertubuhan(String rosNo) {
		String[] splitRos = rosNo.split("-");
		String splitRos1 = "";
		String splitRos2 = "";
		String splitRos3 = "";
		String splitRos4 = "";

		if (splitRos != null && splitRos.length == 4) {
			splitRos1 = splitRos[0];
			splitRos2 = splitRos[1];
			splitRos3 = splitRos[2];
			splitRos4 = splitRos[3];

			if (splitRos1.length() != 3 || StringUtils.isNumeric(splitRos1)) {
				throwErrorMsg("lbl.sm.businessregno.invalid.format.rospertubuhanjppm");
			} else if (splitRos2.length() != 3
					|| !StringUtils.isAlphanumeric(splitRos2)) {
				throwErrorMsg("lbl.sm.businessregno.invalid.format.rospertubuhanjppm");
			} else if (splitRos3.length() != 2
					|| !StringUtils.isNumeric(splitRos3)) {
				throwErrorMsg("lbl.sm.businessregno.invalid.format.rospertubuhanjppm");
			} else if (!isValidDate(splitRos4)) {
				throwErrorMsg("lbl.sm.businessregno.invalid.format.rospertubuhanjppm");
			}
		} else {
			throwErrorMsg("lbl.sm.businessregno.notvalid.rospertubuhanjppm");
		}
	}

	/**
	 * Show validation message
	 * 
	 * @param String
	 *            key
	 */
	private void throwErrorMsg(String key) {		
		throw new ValidatorException(new FacesMessage(
				FacesMessage.SEVERITY_ERROR, "Business Reg No Error",
				SmCommonUtil.getMessage(key, null)));
	}

	/**
	 * Check if date entered is valid
	 * 
	 * @param String
	 *            argValue
	 * @return boolean
	 */
	private boolean isValidDate(String argValue) {
		boolean retVal = true;
		if (argValue.length() != 8) {
			retVal = false;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			sdf.setLenient(false);
			try {
				Date tmpDate = sdf.parse(argValue);
				if (tmpDate.getTime() > new Date().getTime()) {
					retVal = false;
				}
			} catch (Exception ex) {
				retVal = false;
			}
		}
		return retVal;
	}
	//START SSM INTEGRATION CHANGES
	private void throwErrorMsgFC(String key,boolean useFC) {
		if(useFC){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("ssmInvalid",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageUtils.getMessage(MessageConst.ERROR_721081,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()),
							""));
		}
		throw new EpException(MessageConst.ERROR_721081);
		
	}
	private void validateRobFc(String robNo,boolean useFC) {
		//30987
		robNo = robNo.trim();
		String[] splitRob = robNo.split("-");
		String splitRob1 = "";
		String splitRob2 = "";
		if (splitRob != null && splitRob.length > 1) {
			splitRob1 = splitRob[0] != null ? splitRob[0] : "";
			splitRob2 = splitRob[1] != null ? splitRob[1] : "";
			if (splitRob1.length() > 9) {
				throwErrorMsgFC("lbl.sm.businessregno.beforedash.rob",useFC);
			} else {
				if (StringUtils.isBlank(splitRob2) || splitRob2.length() != 1) {
					throwErrorMsgFC("lbl.sm.businessregno.afterdash.rob",useFC);
				}
			}
		} else {
			throwErrorMsgFC("lbl.sm.businessregno.notvalid.rob",useFC);
		}
	}
	private void validateRocFc(String rocNo,boolean useFC) {
		//30987
		rocNo = rocNo.trim();
		String[] splitRoc = rocNo.split("-");
		String splitRoc1 = "";
		String splitRoc2 = "";
		if (splitRoc != null && splitRoc.length > 1) {
			splitRoc1 = splitRoc[0] != null ? splitRoc[0] : "";
			splitRoc2 = splitRoc[1] != null ? splitRoc[1] : "";
			if (splitRoc1.length() > 7) {
				throwErrorMsgFC("lbl.sm.businessregno.beforedash.roc",useFC);
			} else {
				if (StringUtils.isBlank(splitRoc2) || splitRoc2.length() != 1) {
					throwErrorMsgFC("lbl.sm.businessregno.afterdash.roc",useFC);
				}
			}
		} else {
			throwErrorMsgFC("lbl.sm.businessregno.notvalid.roc",useFC);
		}
	}
	public void validateRegNo(String businessType,String regNo,boolean useFc){
		if (businessType != null && StringUtils.isNotBlank(businessType.toString()))
				if (robList.contains(businessType)) {
				//30987 - start
					regNo = regNo.toString();
					regNo = regNo.trim();
					validateRobFc(regNo,useFc);
				} else if (rocList.contains(businessType)) {
					regNo = regNo.toString();
					regNo = regNo.trim();					
					validateRocFc(regNo,useFc);
				} 
				//30987 - end
			
		}
	//END SSM INTEGRATION CHANGES
}
