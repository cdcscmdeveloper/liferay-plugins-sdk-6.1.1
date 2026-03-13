/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;

/**
 * Tab Menu for Contract Amendment
 * 
 * @author Alam
 * @since 7/24/2014	 
 */
public class AmendTabSideMenus {

	FacesScopes face;
	List<String> menuWithAmendFulfilment = null;
	List<String> menuWithAmendSetting = null;
	List<String> menuWithZone = null;
	List<String> menuWithItem = null;
	List<String> menuWithPrepareLoa = null;
	List<String> menuWithPrepareBond = null;
	List<String> menuWithDecision = null;
	List<String> menuWithMeetings  = null;
	List<String> menuWithDeclaration = null;
	List<String> menuWithAmendSupplier = null;
	List<String> menuWithBond = null; //24333 

	List<String> menuWithAgency = null;
	List<String> menuDeduction= null;
	List<String> menuSchedule = null;
	List<String> menuApprover = null; //#35132

	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the tab menu list.
	 */
	public AmendTabSideMenus() {
		face = new FacesScopes();

		if (menuWithAmendFulfilment == null) {
			menuWithAmendFulfilment =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.fd", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.ct.menu.amendment", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithAmendSetting == null) {
			menuWithAmendSetting =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.setting", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithZone == null) {
			menuWithZone=  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.zone", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithItem == null) {
			menuWithItem =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.item", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithPrepareLoa ==  null) {
			menuWithPrepareLoa =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.loa.details", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.ct.menu.loa.content", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithPrepareBond ==  null) {
			menuWithPrepareBond =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.performance.bond", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}
		//24333 - Start
		if (menuWithBond ==  null) {
			menuWithBond =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.performance.bond", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}
		//24333 - End
		if (menuWithDecision == null) {
			menuWithDecision =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.decision", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithMeetings == null) {
			menuWithMeetings =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithDeclaration == null) {
			menuWithDeclaration =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.declaration", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuDeduction == null) {
			menuDeduction =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.deduction", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithAgency == null) {
			menuWithAgency =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.agency", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuSchedule == null) {
			menuSchedule =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.schedule", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}

		if (menuWithAmendSupplier == null) {
			menuWithAmendSupplier =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.loa.details", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.ct.menu.loa.content", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.ct.menu.view.loa", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}
		//#35132
		if (menuApprover == null) {
			menuApprover =  new ArrayList<String>(Arrays.asList(
					ResourceBundleUtils.getMessage("lbl.ct.menu.approver.setting", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
		}
	}
}
