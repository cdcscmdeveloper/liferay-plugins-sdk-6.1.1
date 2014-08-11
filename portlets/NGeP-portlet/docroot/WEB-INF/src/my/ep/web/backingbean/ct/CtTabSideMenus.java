package my.ep.web.backingbean.ct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

public  class CtTabSideMenus {

	FacesScopes face;
	
	List<String> menuFulfilement= null;
	List<String> menuAgency= null;
	List<String> menuZone = null;
	List<String> menuItem  = null;
	List<String> menuDeduction= null;
	List<String> menuSchedule = null;
	List<String> menuFactoring  = null;
	List<String> menuPaymentTracking = null;
	List<String> menuSetting = null;
	
	public CtTabSideMenus(){
		   face = new FacesScopes();
		   
		   if(menuFulfilement == null){
			   menuFulfilement =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.fd", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		   if(menuAgency == null){
			   menuAgency =  new ArrayList<String>(Arrays.asList(
					   ResourceBundleUtils.getMessage("lbl.ct.menu.agency", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		   if(menuZone == null){
			   menuZone =  new ArrayList<String>(Arrays.asList(
					   ResourceBundleUtils.getMessage("lbl.ct.menu.zone", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		   if(menuItem == null){
			   menuItem =  new ArrayList<String>(Arrays.asList(
					   ResourceBundleUtils.getMessage("lbl.ct.menu.item", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		   if(menuDeduction == null){
			   menuDeduction =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.dedu", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		   if(menuSchedule == null){
			   menuSchedule =  new ArrayList<String>(Arrays.asList(
					   ResourceBundleUtils.getMessage("lbl.ct.menu.sched", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		   if(menuFactoring == null){
			   menuFactoring =  new ArrayList<String>(Arrays.asList(
					   ResourceBundleUtils.getMessage("lbl.ct.menu.factoring", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		   if(menuSetting == null){
			   menuSetting =  new ArrayList<String>(Arrays.asList(
					   ResourceBundleUtils.getMessage("lbl.ct.menu.sett", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
	}
}
