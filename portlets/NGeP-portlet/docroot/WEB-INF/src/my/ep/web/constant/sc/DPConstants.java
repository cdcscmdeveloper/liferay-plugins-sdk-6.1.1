package my.ep.web.constant.sc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zaim
 * This class contains constants related to the DP module.
 */
public class DPConstants {

	// 26284 - Start
	/**
	 * A constant representing a single space character (" ").
	 * It can be used for formatting or string concatenation.
	 */
	public static final String SPACE = " ";
	// 26284 - End

	// 36362 - start
	/**
	 * A constant representing parameter id for price type zonal tier standard
	 */
	public static final Long PRICE_TYPE_ZONAL_TIER_STANDARD = 300L;

	/**
	 * A constant representing parameter id for price type tier standard
	 */
	public static final Long PRICE_TYPE_TIER_STANDARD = 296L;

	// 36362 - end
	
	//27868 - start
	/**
	 * Indicate redmine no in PM_APP_VERSION
	 */
	public static final Long REDMINE_NO_27868 = 27868L;
	
	/**
	 * Indicate language code EN
	 */
	public static final String LANGUAGE_CODE_EN = "EN";
	
	/**
	 * Indicate supplier account type Contractor
	 */
	public static final String SUPPLIER_ACCOUNT_TYPE_CONTRACTOR = "K";
	/**
	 * Indicate supplier account type Consultant
	 */
	public static final String SUPPLIER_ACCOUNT_TYPE_CONSULTANT = "J";
	/**
	 * Indicate supplier account type Basic Account
	 */
	public static final String SUPPLIER_ACCOUNT_TYPE_BASIC = "B";
	/**
	 * Indicate supplier account type G2G
	 */
	public static final String SUPPLIER_ACCOUNT_TYPE_G2G = "G";
	
	/**
	 * Indicate supplier account type PTJ Government Seller
	 */
	public static final String SUPPLIER_ACCOUNT_TYPE_GOVT_SELLER = "P";

	/**
	 * Indicate MOF Account parameter code in pm_parameter
	 */
	public static final String PARAMETER_CODE_MOF_ACCOUNT = "MA";
	
	/**
	 * Indicate Basic Account parameter code in pm_parameter
	 */
	public static final String PARAMETER_CODE_BASIC_ACCOUNT = "BA";
	
	/**
	 * Indicate G2G Account parameter code in pm_parameter
	 */
	public static final String PARAMETER_CODE_G2G_ACCOUNT = "GA";
	
	/**
	 * JavaScript command to hide the PTJ popup before saving.
	 */
	public static final String USER_PTJ_POPUP_RN_HIDE = "beforeSavePopup.hide();";

	/**
	 * Pattern for validating a 12-digit IC number.
	 */
	public static final String IC_PATTERN = "^\\d{12}$";

	/**
	 * Code representing Identification Card type.
	 */
	public static final String IDENTIFICATION_TYPE_IC = "I";

	/**
	 * String used for plural grammar ("are").
	 */
	public static final String ARE_STRING = "are";

	/**
	 * String used for singular grammar ("is").
	 */
	public static final String IS_STRING = "is";
	
	/**
	 * Separator string constant used for joining text values.
	 */
	public static final String SEPARATOR_STRING = ", ";

	/** Procurement type code for Agency. */
	public static final String PARAMETER_CODE_A = "A";

	/** Procurement type code for Panel. */
	public static final String PARAMETER_CODE_P = "P";

	/** Procurement type code for Special Item. */
	public static final String PARAMETER_CODE_S = "S";

	/** Constant representing an empty string value. */
	public static final String EMPTY_STRING = "";
	
	//27868 - end

	// 26059 - start
	public static final String SC_ITEM_EXCEEDED_THRESHOLD_NON_PANEL = "SC_ITEM_EXCEEDED_THRESHOLD_NON_PANEL.jsp";

	public static final String SC_ITEM_EXCEEDED_THRESHOLD_PANEL = "SC_ITEM_EXCEEDED_THRESHOLD_PANEL.jsp";

	public static final String FULFILMENT_TYPE_G2G = "G2G";
	
	public static final String FULFILMENT_TYPE_NORMAL = "Normal";

	public static final List<Long> LIST_STATUS_ITEM_EXCEED_THRESHOLD_REPORT =
	        Collections.unmodifiableList(
	            Arrays.asList(62603L,60708L,60703L,60706L,60710L,60715L,60711L)
	        );
	// 26059 - end
	private DPConstants() {
		// This constructor is intentionally left empty
	}

}
