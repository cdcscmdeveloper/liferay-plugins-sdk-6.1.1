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
			if (smAddress.getAddress1() != null && smAddress.getAddress1().length() > 0) {
				address.append(smAddress.getAddress1());
				address.append(separator);
			}
			if (smAddress.getAddress2() != null && smAddress.getAddress2().length() > 0) {
				address.append(smAddress.getAddress2());
				address.append(separator);
			}
			if (smAddress.getAddress3() != null && smAddress.getAddress3().length() > 0) {
				address.append(smAddress.getAddress3());
				address.append(separator);
			}
			if (smAddress.getAddress4() != null && smAddress.getAddress4().length() > 0) {
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
			if (pmAddress.getAddressName() != null && pmAddress.getAddressName().length() > 0 ) {
				address.append(pmAddress.getAddressName());
			}
			if (pmAddress.getAddress1() != null && pmAddress.getAddress1().length() > 0) {
				address.append(address.length()>0?separator:"");
				address.append(pmAddress.getAddress1());
			}
			if (pmAddress.getAddress2() != null && pmAddress.getAddress2().length() > 0) {
				address.append(address.length()>0?separator:"");
				address.append(pmAddress.getAddress2());
			}
			if (pmAddress.getAddress3() != null && pmAddress.getAddress3().length() > 0) {
				address.append(address.length()>0?separator:"");
				address.append(pmAddress.getAddress3());
			}
			if (pmAddress.getAddress4() != null && pmAddress.getAddress4().length() > 0) {
				address.append(address.length()>0?separator:"");
				address.append(pmAddress.getAddress4());
			}
			if (pmAddress.getPostcode() != null && pmAddress.getPostcode().length() > 0) {
				address.append(address.length()>0?separator:"");
				address.append(pmAddress.getPostcode());
			}
			if (pmAddress.getPmCity() != null && pmAddress.getPmCity().getCityName() !=null && pmAddress.getPmCity().getCityName().length() > 0) {
				address.append(address.length()>0?separator:"");
				address.append(pmAddress.getPmCity().getCityName());
			}
			if (pmAddress.getPmState() != null && pmAddress.getPmState().getStateName() !=null && pmAddress.getPmState().getStateName().length() > 0) {
				address.append(address.length()>0?separator:"");
				address.append(pmAddress.getPmState().getStateName());
			}						
		}
		return address.toString();
	}
	
}
