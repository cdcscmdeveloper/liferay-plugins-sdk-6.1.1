package my.ep.web.backingbean.ct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.ep.constant.ct.ContractConst;

public  class TabSidemenuName {

	List<String> menuWithZone =  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_DETAILS, ContractConst.MENU_FULFILMENT_AGENCY, ContractConst.MENU_FULFILMENT_ZONE,ContractConst.MENU_FULFILMENT_ITEM));
	List<String> menuWithNoZone=  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_DETAILS, ContractConst.MENU_FULFILMENT_AGENCY ,ContractConst.MENU_FULFILMENT_ITEM));
	
	List<String> menuWithNoPurchase =  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_DEDUCTION, ContractConst.MENU_FULFILMENT_SCHEDULE));
	List<String> menuWithDirectPurchase =  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_SCHEDULE));
	
	List<String> menuWithFactoring =  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_FACTORING));
	List<String> menuWithPayment =  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_PAYMENT));
	List<String> menuWithFactoringAndPayment =  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_FACTORING,ContractConst.MENU_FULFILMENT_PAYMENT));
	
	List<String> menuWithSetting =  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_SETTING));
	
	List<String> menuCentralFulfilment =  new ArrayList<String>(Arrays.asList(ContractConst.MENU_FULFILMENT_DETAILS,  ContractConst.MENU_FULFILMENT_ITEM));
}
