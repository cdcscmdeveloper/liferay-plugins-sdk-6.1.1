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
	private static String htmlLineSeparator ="<br/>";
	
	/**
	 * Get Full Formatted Address based on SmAddress
	 * @param smAddress
	 * @return full address
	 */
	public static String getSmFullFormattedAddress(SmAddress smAddress) {
		StringBuilder address = new StringBuilder();
		if(smAddress!=null) {
			if (smAddress.getAddress1() != null && smAddress.getAddress1().length() > 0) {
				address.append(smAddress.getAddress1());
				address.append(separator).append(htmlLineSeparator);
			}
			if (smAddress.getAddress2() != null && smAddress.getAddress2().length() > 0) {
				address.append(smAddress.getAddress2());
				address.append(separator).append(htmlLineSeparator);
			}
			if (smAddress.getAddress3() != null && smAddress.getAddress3().length() > 0) {
				address.append(smAddress.getAddress3());
				address.append(separator).append(htmlLineSeparator);
			}
			if (smAddress.getAddress4() != null && smAddress.getAddress4().length() > 0) {
				address.append(smAddress.getAddress4());
				address.append(separator).append(htmlLineSeparator);
			}
			if (smAddress.getPmDistrict() != null && smAddress.getPmDistrict().getDistrictName() !=null && smAddress.getPmDistrict().getDistrictName().length() > 0) {
				address.append(smAddress.getPmDistrict().getDistrictName());
				address.append(separator).append(htmlLineSeparator);
			}
			if (smAddress.getPostcode() != null && smAddress.getPostcode().length() > 0) {
				address.append(smAddress.getPostcode());
				address.append(" ");
				//.append(htmlLineSeparator);
			}
			if (smAddress.getPmCity() != null && smAddress.getPmCity().getCityName() !=null && smAddress.getPmCity().getCityName().length() > 0) {
				address.append(smAddress.getPmCity().getCityName());
				address.append(separator).append(htmlLineSeparator);
			}

			if (smAddress.getPmState() != null && smAddress.getPmState().getStateName() !=null && smAddress.getPmState().getStateName().length() > 0) {
				address.append(smAddress.getPmState().getStateName());
				address.append(separator);
			}
			
			if (smAddress.getPmCountry() != null && smAddress.getPmCountry().getCountryName() !=null && smAddress.getPmCountry().getCountryName().length() > 0) {
				address.append(smAddress.getPmCountry().getCountryName());
			}
			
		}
		return address.toString();
	}
	
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
				address.append(System.getProperty("line.separator"));
				address.append(smAddress.getAddress3());
				address.append(separator);
			}
			if (smAddress.getAddress4() != null && smAddress.getAddress4().length() > 0) {
				address.append(smAddress.getAddress4());
				address.append(separator);
			}
			
			if (smAddress.getPmDistrict() != null && smAddress.getPmDistrict().getDistrictName() !=null && smAddress.getPmDistrict().getDistrictName().length() > 0) {
				address.append(smAddress.getPmDistrict().getDistrictName());
				address.append(separator);
			}
			
			if (smAddress.getPostcode() != null && smAddress.getPostcode().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(smAddress.getPostcode());
				address.append(" ");
			}
			if (smAddress.getPmCity() != null && smAddress.getPmCity().getCityName() !=null && smAddress.getPmCity().getCityName().length() > 0) {
				address.append(smAddress.getPmCity().getCityName());
				address.append(separator);
			}

			if (smAddress.getPmState() != null && smAddress.getPmState().getStateName() !=null && smAddress.getPmState().getStateName().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(smAddress.getPmState().getStateName());
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
			if (pmAddress.getAddressName() != null && pmAddress.getAddressName().length() > 0 && !pmAddress.getAddressName().equals("-")) {
				address.append(pmAddress.getAddressName());
				address.append(separator);
			}
			if (pmAddress.getAddress1() != null && pmAddress.getAddress1().length() > 0) {
				address.append(address.length() > 0 ? System.getProperty("line.separator") : "");
				address.append(pmAddress.getAddress1());
				address.append(separator);
			}
			if (pmAddress.getAddress2() != null && pmAddress.getAddress2().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(pmAddress.getAddress2());
				address.append(separator);
			}
			if (pmAddress.getAddress3() != null && pmAddress.getAddress3().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(pmAddress.getAddress3());
				address.append(separator);
			}
			if (pmAddress.getAddress4() != null && pmAddress.getAddress4().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(pmAddress.getAddress4());
				address.append(separator);
			}
			if (pmAddress.getPostcode() != null && pmAddress.getPostcode().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(pmAddress.getPostcode());
				address.append(separator);
			}
			if (pmAddress.getPmCity() != null && pmAddress.getPmCity().getCityName() !=null && pmAddress.getPmCity().getCityName().length() > 0) {
				address.append(pmAddress.getPmCity().getCityName());
				address.append(separator);
			}
			if (pmAddress.getPmState() != null && pmAddress.getPmState().getStateName() !=null && pmAddress.getPmState().getStateName().length() > 0) {
				address.append(pmAddress.getPmState().getStateName());
			}						
		}
		return address.toString();
	}
	
	/**
	 * Get Full Address based on SmAddress
	 * @param smAddress
	 * @return full address without postcode, state, district and city 
	 */
	public static String getAddress(SmAddress smAddress) {
		StringBuilder address = new StringBuilder();
		
		if(smAddress!=null) {
			if (smAddress.getAddress1() != null && smAddress.getAddress1().length() > 0) {
				address.append(smAddress.getAddress1());
				address.append(separator).append(htmlLineSeparator);;
			}
			if (smAddress.getAddress2() != null && smAddress.getAddress2().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(smAddress.getAddress2());
				address.append(separator).append(htmlLineSeparator);;
			}
			if (smAddress.getAddress3() != null && smAddress.getAddress3().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(smAddress.getAddress3());
				address.append(separator).append(htmlLineSeparator);;
			}
			if (smAddress.getAddress4() != null && smAddress.getAddress4().length() > 0) {
				address.append(smAddress.getAddress4());
			}
		}
		return address.toString();
	}
}
