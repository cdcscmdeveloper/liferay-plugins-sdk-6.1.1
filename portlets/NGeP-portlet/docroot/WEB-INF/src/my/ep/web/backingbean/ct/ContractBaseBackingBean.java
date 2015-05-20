/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * ContractBaseBackingBean is the Base class for contract
 * 
 * @author Nancy Ang
 * @since 19.05.2015
 * 
 */
public abstract class ContractBaseBackingBean<T extends BaseManagedBean>
		extends BaseBackingBean<T> {

	/**
	 * Logger instance.
	 */
	public static Logger log = EpLogger.getLogger(ContractBaseBackingBean.class);
	
	/**
	 * Sort agency list.
	 *
	 * @param ctAgencyList the agency list
	 */
	public void sortAgencyList(List<CtAgency> ctAgencyList) {
		Comparator<CtAgency> comparator = new Comparator<CtAgency>() {
			@Override
			public int compare(CtAgency obj1, CtAgency obj2) {
			    String minstryName = retrieveMinistryByOrgProfileId(obj1.getOrgProfile().getOrgProfileId());
		        String minstryName2 = retrieveMinistryByOrgProfileId(obj2.getOrgProfile().getOrgProfileId());
		        int agency = minstryName.compareTo(minstryName2);
		        if (agency != 0) {
		        	return agency;
		        } else  {
		        	String ptjName = obj1.getOrgProfile().getOrgName();
		        	String ptjName2 = obj2.getOrgProfile().getOrgName();
		        	int ptj = ptjName.compareTo(ptjName2);
		        	if (ptj != 0) {
		        		return ptj;
		        	} else {
		        		Date date1 = ((CtAgency) obj1).getEffDate();
		        		Date date2 = ((CtAgency) obj2).getEffDate();
		        		int date = date1.compareTo(date2);
		        		if (date != 0)
		        			return date;
		        		else
		        			return 0;
			    	}
		        }
			}
		};
		Collections.sort(ctAgencyList, comparator);
	}
	
	/**
	 * Sort item ceiling quantity list.
	 *
	 * @param ctItemCeilingQty the item ceiling quantity list
	 */
	public void sortItemCeilingQtyList(List<CtItemCeilingQty> ctItemCeilingQty) {
		Comparator<CtItemCeilingQty> comparator = new Comparator<CtItemCeilingQty>() {
			@Override
			public int compare(CtItemCeilingQty obj1, CtItemCeilingQty obj2) {				   
				if (obj1 != null) {					   
					String minstryName = retrieveMinistryByOrgProfileId(obj1.getCtAgency().getOrgProfile().getOrgProfileId());
					String minstryName2 = retrieveMinistryByOrgProfileId(obj2.getCtAgency().getOrgProfile().getOrgProfileId());
					int agency = minstryName.compareTo(minstryName2);
					if (agency != 0) {
						return agency;
					} else  {
						String ptjName =  obj1.getCtAgency().getOrgProfile().getOrgName();
						String ptjName2 = obj2.getCtAgency().getOrgProfile().getOrgName();
						int ptj = ptjName.compareTo(ptjName2);
		              	if (ptj != 0) {
		              		return ptj;
		              	} else {
		              		Date date1 = obj1.getEffDate();
		              		Date date2 =  obj2.getEffDate();
		              		return date1.compareTo(date2);
		              	}
					}
				} 
				return 0;
			}
		};
		Collections.sort(ctItemCeilingQty, comparator);
	}
	
}

