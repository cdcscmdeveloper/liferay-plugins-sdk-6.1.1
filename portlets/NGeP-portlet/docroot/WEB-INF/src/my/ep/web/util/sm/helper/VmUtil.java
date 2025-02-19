package my.ep.web.util.sm.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import my.ep.constant.tpl.TemplateDocConst;	// Bug #36702@Syazani 28/11/2024
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.constant.SSTConstant;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;

public class VmUtil {
	
	private static VmUtil instance;
	
	public static Logger log = Logger.getLogger(VmUtil.class);

	
	/**
	 * Instance of VmUtil
	 * @return VmUtil
	 */
	public static VmUtil getInstance(){
		if (instance == null){
			instance = new VmUtil();
		}
		return instance;
	}
	
	/*
	 * for Site Visit VM purpose
	 */
	public static String getParameterDesc(PmParameter parameter){
		return ParameterHelper.getInstance().getParameterDesc(
				parameter.getParameterType(), parameter.getParameterCode());
	}
	
	public static String formatDate(Date date){
		if(date != null){
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String cdate = df.format(date);
			return cdate;
		}
		
		return null;
	}
	
	/**
	 * Format to two decimal point.
	 * 
	 * @param amount
	 *            the amount
	 * @return the string
	 */
	public static String formatToTwoDecimalPoint(BigDecimal amount) {
		DecimalFormat formatter = new DecimalFormat("###,##0.00");
		return formatter.format(amount);
	}
	
	/**
	 * Get Label
	 * @param String label
	 * @return String 
	 */
	public String getDescByLabel(String label){
		Locale locale = new FacesScopes().getUserLocale();
		String desc = ResourceBundleUtils.getMessage(
				label,ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		
		return desc;
	}
	//
	public String getBusinessNature(SmSupplierBusiness suppBusinessNat){
		RegistrationBackingBean registrationBackingBean = new RegistrationBackingBean();
		return registrationBackingBean.getBusinessNatureSection(suppBusinessNat);
	}
	
	public String getFomatDate(SmPersonnel smpersonnel){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String birthdate = sdf.format(smpersonnel.getBirthDate());
		log.info("THIS IS BIRTHDATE---------------------------------------------" + birthdate);
		return birthdate;
		
	}
	
	public String setStatusMessageSSM(SmCompanyBasic smcompany){
		String statusCode = smcompany.getCompanySsmStatus();
		if("A".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.active", null);
		}else if("E".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.existing", null);
		}else if("L".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.expired", null);
		}else if("T".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.terminated", null);
		}else if("B".equalsIgnoreCase(statusCode)){ 
		//27382
			return SmCommonUtil.getMessage("lbl.sm.status.dissolved", null);
		}else if("W".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.w", null);
		}else if("D".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.d", null);
		}else if("R".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.r", null);
		}else if("C".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.c", null);
		}else if("X".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.x", null);
		}else if("Y".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.y", null);
		}
		//start #29935
		else if("F".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.winding.up.under.cva", null);
		}
		else if("G".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.existing.under.jm.under.cva", null);
		}
		else if("J".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.existing.under.jm", null);
		}
		else if("V".equalsIgnoreCase(statusCode)){
			return SmCommonUtil.getMessage("lbl.sm.status.existing.under.cva", null);
		}
		//end #29935
		return "";
	}
	
	public String getCompleteAddress(SmAddress smAdd) {
		return AddressUtils.getFullAddress(smAdd);
	}
	
	// Bug #36702@Syazani 28/11/2024 -- Start
	/**
	 * This method will split Long words to display properly in velocity
	 * template.
	 * 
	 * @param key
	 * @param maxLength
	 * @return the updatedKey
	 */
	public String splitText(String key, int maxLength) {
		if (null == key) {
			return key;
		}
		
		String[] words = key.split(" ");
		StringBuilder sb = new StringBuilder();
		StringBuilder subSb;
		for (String word : words) {
			int length = word.length();
			if (length >= maxLength) {
				subSb = new StringBuilder();
				String newWord;
				String subWord;
				for (int i = 0; i < length; i += maxLength) {
					subWord = word.substring(i, Math.min(length, i + maxLength));
					subSb.append(subWord).append(TemplateDocConst.PARAM_SPACE);
				}
				newWord = subSb.toString();
				sb.append(newWord).append(TemplateDocConst.PARAM_SPACE);
			} else {
				sb.append(word).append(TemplateDocConst.PARAM_SPACE);
			}
		}
		return sb.toString();
	}
	// Bug #36702@Syazani 28/11/2024 -- End
	
	// 36122 - start
	/**
	 * This method will break Long words to display properly in velocity
	 * template.
	 * 
	 * @param key
	 * @param maxLength
	 * @return the updatedKey
	 */
	public String addLineBreaks(String key, int maxLength) {
		if (key == null || maxLength <= 0) {
			return key;
		}
		int newMaxLength;
		String[] stringArray = key.split("\\s+");
		StringBuilder updatedKey = new StringBuilder();
		for (String s : stringArray) {
			if (s.length() > maxLength) {
				newMaxLength = isStringContainHTMLEntity(maxLength, s);
				String chopString = s.replaceAll(
						"(.{" + newMaxLength + "})(?!$)",
						"$1" + TemplateSpecConst.HTML_NEW_LINE_CHARACTER)
						.concat(SSTConstant.ADD_WHITESPACE);
				chopString = validateHtmlEntitySyntax(chopString);
				updatedKey.append(chopString);

			} else {
				updatedKey.append(s.concat(SSTConstant.ADD_WHITESPACE));
			}
		}
		log.info("input : " + key + " maxLength : " + maxLength + " String : "
				+ updatedKey.toString());
		return updatedKey.toString();
	}

	/**
	 * @param chopString
	 * @param isHtmlSyntaxIncomplete
	 * @return
	 */
	public String validateHtmlEntitySyntax(String chopString) {
		String newChopString = chopString;
		newChopString = validateBreakLine(newChopString);

		newChopString = validateLessThanBracket(newChopString);

		newChopString = validateGreaterThanBracket(newChopString);

		newChopString = validateProtegeCharacterUpperCase(newChopString); // 35295 #93 @zaim

		newChopString = validateProtegeCharacterLowerCase(newChopString); // 35295 #93 @zaim

		return newChopString;
	}

	/**
	 * @param newChopString
	 * @return
	 */
	private String validateGreaterThanBracket(String newChopString) {
		String checkBracket = newChopString;
		if (checkBracket.contains("&#<br />62;")) {
			checkBracket = checkBracket.replaceAll("&#<br />62;",
					SSTConstant.GREATER_THAN_SYMBOL
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkBracket.contains("&#62<br />;")) {
			checkBracket = checkBracket.replaceAll("&#62<br />;",
					SSTConstant.GREATER_THAN_SYMBOL
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkBracket.contains("&#6<br />2;")) {
			checkBracket = checkBracket.replaceAll("&#6<br />2;",
					SSTConstant.GREATER_THAN_SYMBOL
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkBracket.contains("&<br />#62;")) {
			checkBracket = checkBracket.replaceAll("&<br />#62;",
					SSTConstant.GREATER_THAN_SYMBOL
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		return checkBracket;
	}

	/**
	 * @param newChopString
	 * @return
	 */
	private String validateLessThanBracket(String newChopString) {
		String checkBracket = newChopString;
		if (checkBracket.contains("&#<br />60;")) {
			checkBracket = checkBracket.replaceAll("&#<br />60;",
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER
							+ SSTConstant.LESS_THAN_SYMBOL); // 35295 #93 @zaim
		}
		if (checkBracket.contains("&#60<br />;")) {
			checkBracket = checkBracket.replaceAll("&#60<br />;",
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER
							+ SSTConstant.LESS_THAN_SYMBOL); // 35295 #93 @zaim
		}
		if (checkBracket.contains("&#6<br />0;")) {
			checkBracket = checkBracket.replaceAll("&#6<br />0;",
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER
							+ SSTConstant.LESS_THAN_SYMBOL); // 35295 #93 @zaim
		}
		if (checkBracket.contains("&<br />#60;")) {
			checkBracket = checkBracket.replaceAll("&<br />#60;",
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER
							+ SSTConstant.LESS_THAN_SYMBOL); // 35295 #93 @zaim
		}
		return checkBracket;
	}

	/**
	 * @param newChopString
	 * @return
	 */
	private String validateBreakLine(String newChopString) {
		String checkBreakLine = newChopString;
		if (checkBreakLine.contains("<br<br /> />")) {
			checkBreakLine = checkBreakLine.replaceAll("<br<br />/>",
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkBreakLine.contains("<b<br />r />")) {
			checkBreakLine = checkBreakLine.replaceAll("<b<br />r/>",
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkBreakLine.contains("<b<br />r")) {
			checkBreakLine = checkBreakLine.replaceAll("<b<br />r", "<br");
		}
		if (checkBreakLine.contains("<<br />br")) {
			checkBreakLine = checkBreakLine.replaceAll("<<br />br", "<br");
		}
		return checkBreakLine;
	}

	/**
	 * @param maxLength
	 * @param s
	 * @return
	 */
	private int isStringContainHTMLEntity(int maxLength, String s) {
		int newMaxlength = maxLength;
		if (s.contains(TemplateSpecConst.HTML_NEW_LINE_CHARACTER)
				|| s.contains("<b")) {
			newMaxlength = newMaxlength
					+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER.length();
		}
		if (s.contains(SSTConstant.LESS_THAN_SYMBOL)) {
			newMaxlength = newMaxlength + SSTConstant.LESS_THAN_SYMBOL.length();
		}
		if (s.contains(SSTConstant.GREATER_THAN_SYMBOL)) {
			newMaxlength = newMaxlength
					+ SSTConstant.GREATER_THAN_SYMBOL.length();
		}
		// 35295 - start
		if (s.contains(SSTConstant.PROTEGE_LOWERCASE_E)) {
			newMaxlength = newMaxlength
					+ (SSTConstant.PROTEGE_LOWERCASE_E.length() * 2);
		}
		if (s.contains(SSTConstant.PROTEGE_UPPERCASE_E)) {
			newMaxlength = newMaxlength
					+ (SSTConstant.PROTEGE_UPPERCASE_E.length() * 2);
		}
		// 35295 - end
		return newMaxlength;
	}

	// 36122 - end
	
	// 35295 - start
	private String validateProtegeCharacterUpperCase(String newChopString) {
		String checkProtege = newChopString;
		if (checkProtege.contains("&<br />#201;")) {
			checkProtege = checkProtege.replaceAll("&<br />#201;",
					SSTConstant.PROTEGE_UPPERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkProtege.contains("&#<br />201;")) {
			checkProtege = checkProtege.replaceAll("&#<br />201;",
					SSTConstant.PROTEGE_UPPERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkProtege.contains("&#2<br />01;")) {
			checkProtege = checkProtege.replaceAll("&#2<br />01;",
					SSTConstant.PROTEGE_UPPERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkProtege.contains("&#20<br />1;")) {
			checkProtege = checkProtege.replaceAll("&#20<br />1;",
					SSTConstant.PROTEGE_UPPERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkProtege.contains("&#201<br />;")) {
			checkProtege = checkProtege.replaceAll("&#201<br />;",
					SSTConstant.PROTEGE_UPPERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}

		return checkProtege;

	}

	private String validateProtegeCharacterLowerCase(String newChopString) {
		String checkProtege = newChopString;
		if (checkProtege.contains("&<br />#233;")) {
			checkProtege = checkProtege.replaceAll("&<br />#233;",
					SSTConstant.PROTEGE_LOWERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkProtege.contains("&#<br />233;")) {
			checkProtege = checkProtege.replaceAll("&#<br />233;",
					SSTConstant.PROTEGE_LOWERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkProtege.contains("&#2<br />33;")) {
			checkProtege = checkProtege.replaceAll("&#2<br />33;",
					SSTConstant.PROTEGE_LOWERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkProtege.contains("&#23<br />3;")) {
			checkProtege = checkProtege.replaceAll("&#23<br />3;",
					SSTConstant.PROTEGE_LOWERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (checkProtege.contains("&#233<br />;")) {
			checkProtege = checkProtege.replaceAll("&#233<br />;",
					SSTConstant.PROTEGE_LOWERCASE_E
							+ TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}

		return checkProtege;
		// 35295 - end
	}
	
	// #29007 - note 724 Vinod start
	/**
		* This method will replace following special characters from the string to HTML keys
		* '<' to "&#60;" , '>' to "&#62;" and  '\r\n' to "<br />"
		* to avoid errors and achieve line spacing in VM report.
		* 
		* @param key
		* @return updated String
		*/
	public String replaceAllSpecialCharactersToHTML(String key) {
		if (key == null || key == "") {
			return key;
		}
		String localKey = key;

		if (localKey.contains(TemplateSpecConst.LESS_THEN_SYMBOL)) {
			localKey = localKey.replaceAll(TemplateSpecConst.LESS_THEN_SYMBOL,
					TemplateSpecConst.LESS_THEN_HTML_ENTITY);
		}
		if (localKey.contains(TemplateSpecConst.GREATER_THEN_SYMBOL)) {
			localKey = localKey.replaceAll(
					TemplateSpecConst.GREATER_THEN_SYMBOL,
					TemplateSpecConst.GREATER_THEN_HTML_ENTITY);
		}
		if (localKey.contains(TemplateSpecConst.NEW_LINE_CHARACTER)) {
			localKey = localKey.replaceAll(
					TemplateSpecConst.NEW_LINE_CHARACTER,
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}
		if (localKey.contains(TemplateSpecConst.NEW_LINE_CHARACTER_R)) {
			localKey = localKey.replaceAll(
					TemplateSpecConst.NEW_LINE_CHARACTER_R,
					TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		}

		return localKey;
	}
	// #29007 - note 724 Vinod End
	
}
