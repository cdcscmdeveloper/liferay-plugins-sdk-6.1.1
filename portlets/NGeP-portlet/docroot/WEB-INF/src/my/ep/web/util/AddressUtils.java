/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util;

import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.sm.SmAddress;

/**
 * @author Kieyun
 * @since 07.06.2014
 *
 */
public class AddressUtils {
	
	private static String separator = ", ";
	
	/**
	 * Get Full Address based on SmAddress
	 * @param smAddress
	 * @return full address
	 */
	public static String getFullAddress(SmAddress smAddress) {
		StringBuilder address = new StringBuilder();
		if(smAddress!=null) {
			if (smAddress.getAddress1() != null) {
				address.append(smAddress.getAddress1());
				address.append(separator);
			}
			if (smAddress.getAddress2() != null) {
				address.append(smAddress.getAddress2());
				address.append(separator);
			}
			if (smAddress.getAddress3() != null) {
				address.append(smAddress.getAddress3());
				address.append(separator);
			}
			if (smAddress.getAddress4() != null) {
				address.append(smAddress.getAddress4());
			}
		}
		return address.toString();
	}

	/**
	 * Get Full Address based on SmAddress
	 * @param smAddress
	 * @return full address
	 */
	public static String getFullAddress(PmAddress pmAddress) {
		StringBuilder address = new StringBuilder();
		if(pmAddress!=null) {
			if (pmAddress.getAddress1() != null) {
				address.append(pmAddress.getAddress1());
				address.append(separator);
			}
			if (pmAddress.getAddress2() != null) {
				address.append(pmAddress.getAddress2());
				address.append(separator);
			}
			if (pmAddress.getAddress3() != null) {
				address.append(pmAddress.getAddress3());
				address.append(separator);
			}
			if (pmAddress.getAddress4() != null) {
				address.append(pmAddress.getAddress4());
			}
		}
		return address.toString();
	}
	
}
