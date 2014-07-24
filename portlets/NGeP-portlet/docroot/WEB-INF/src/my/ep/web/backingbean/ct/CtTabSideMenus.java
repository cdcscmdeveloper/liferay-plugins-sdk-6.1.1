package my.ep.web.backingbean.ct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

public  class CtTabSideMenus {

	FacesScopes face;
	List<String> menuWithZone= null;
	List<String> menuWithNoZone = null;
	List<String> menuWithNoPurchase  = null;
	List<String> menuWithDirectPurchase = null;
	List<String> menuWithFactoring = null;
	List<String> menuWithPayment  = null;
	List<String> menuWithSetting = null;
	List<String> menuCentralFulfilment = null;
	
	public CtTabSideMenus(){
		   face = new FacesScopes();
		   
		   if(menuWithZone == null){
			   menuWithZone =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.fd", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.agency", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.zone", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.item", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		  if(menuWithNoZone == null){
			  menuWithNoZone=  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.fd", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.agency", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.item", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithNoPurchase == null){
			  menuWithNoPurchase =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.dedu", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.sched", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithDirectPurchase ==  null){
			  menuWithDirectPurchase =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.sched", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithFactoring == null){
			  menuWithFactoring =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.factoring", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithPayment == null){
			  menuWithPayment =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.advPaymentTracking", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithSetting == null){
			  menuWithSetting =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.sett", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuCentralFulfilment == null){
			  menuCentralFulfilment =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.fd", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()), 
						ResourceBundleUtils.getMessage("lbl.ct.menu.item", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
	}
}
