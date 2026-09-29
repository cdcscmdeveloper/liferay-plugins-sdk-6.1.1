package my.ep.web.constant.sc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zaim
 * This class contains constants related to the DP module.
 */
public class DPConstants {
	
	// 26284 - Start
	/**
	 * A constant representing a single space character (" "). It can be used
	 * for formatting or string concatenation.
	 */
	public static final String SPACE = " ";
	// 26284 - End

	//36362 - start
	/**
	 * A constant representing parameter id 
	 * for price type zonal tier standard
	 */
	public static	final	Long	PRICE_TYPE_ZONAL_TIER_STANDARD	=	300L;
	
	/**
	 * A constant representing parameter id 
	 * for price type tier standard
	 */
	public static	final	Long	PRICE_TYPE_TIER_STANDARD	=	296L;
	//36362 - end
	
	// 26059 - start
	/**
	 * Indicate non panel report file name in pm_report
	 */
	public static final String SC_ITEM_EXCEEDED_THRESHOLD_NON_PANEL = "SC_ITEM_EXCEEDED_THRESHOLD_NON_PANEL.jsp";

	/**
	 * Indicate panel report file name in pm_report
	 */
	public static final String SC_ITEM_EXCEEDED_THRESHOLD_PANEL = "SC_ITEM_EXCEEDED_THRESHOLD_PANEL.jsp";

	/**
	 * Indicate fulfilment type G2G
	 */
	public static final String FULFILMENT_TYPE_G2G = "G2G";

	/**
	 * Indicate fulfilment type normal
	 */
	public static final String FULFILMENT_TYPE_NORMAL = "Normal";

	/**
	 * Contain set of status id for status dropdown in item exceed threshold
	 * report
	 */
	public static final Set<Long> STATUS_ITEM_EXCEED_THRESHOLD_REPORT = Collections.unmodifiableSet(new HashSet<>(
			Arrays.asList(62603L, 60708L, 60703L, 60706L, 60710L, 60715L, 60711L)));

	public static final Long PRODUCT_TYPE = 815L;

	public static final int NUMBER_EIGHT = 8;

	public static final int NUMBER_EIGHTEEN = 18;

	public static final int NUMBER_ZERO = 0;

	public static final int NUMBER_ONE = 1;

	// 26059 - end
	
	private DPConstants() {
		// This constructor is intentionally left empty
	}

}
