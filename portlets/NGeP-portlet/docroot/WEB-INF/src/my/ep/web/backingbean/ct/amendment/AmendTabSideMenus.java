package my.ep.web.backingbean.ct.amendment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

public class AmendTabSideMenus {

	FacesScopes face;
	List<String> menuWithAmendFulfilment = null;
	List<String> menuWithZone = null;
	List<String> menuWithItem = null;
	List<String> menuWithSpAprlAP201  = null;
	List<String> menuWithPrepareLoa = null;
	List<String> menuWithDecision = null;
	List<String> menuWithMeetings  = null;
	List<String> menuWithDeclaration = null;
	List<String> menuWithAmendSupplier = null;
	
	public AmendTabSideMenus(){
		   face = new FacesScopes();
		   
		   if(menuWithAmendFulfilment == null){
			   menuWithAmendFulfilment =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.fd", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.amendment", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		   
		  if(menuWithZone == null){
			  menuWithZone=  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.zone", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithItem == null){
			  menuWithItem =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.item", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithSpAprlAP201 == null){
			  menuWithSpAprlAP201 =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.special.ap201", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithPrepareLoa ==  null){
			  menuWithPrepareLoa =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.loa.details", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.performance.bond", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.loa", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithDecision == null){
			  menuWithDecision =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.decision", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithMeetings == null){
			  menuWithMeetings =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithDeclaration == null){
			  menuWithDeclaration =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.declaration", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
		  
		  if(menuWithAmendSupplier == null){
			  menuWithAmendSupplier =  new ArrayList<String>(Arrays.asList(
						ResourceBundleUtils.getMessage("lbl.ct.menu.loa.details", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.loa", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.ct.menu.declaration", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		  }
	}
}
