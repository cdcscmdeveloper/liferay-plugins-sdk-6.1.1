/* Copyright © 2014 Archisoft. All rights reserved. */
package my.ep.web.managedbean.sc.bsvattendance;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * BSVAttendanceManagedBean class holds the properties of BSV Attendance screen.
 * 
 * @author sri 
 */
@ManagedBean(name = "bsvAttendanceManagedBean")
@SessionScoped
public class BSVAttendanceManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private List<ScQtBsvRegistration> briefingRegistrationList;

	private List<ScQtBsvAttendance> briefingAttendanceList;

	private ScQtBsvDtl scQtBriefing;

	private ScQtBsvRegistration selectedbriefRegistration;

	private ScQtBsvRegistration scQtBriefingRegistration;
	private ScQtBsvAttendance scQtBriefingAttendance;
	private ScQt scQt;

	private String previousPage;

	private String location;
	private ScQtBsvDtl bsvDetails;

	private ScQtBsv scQtBsv;

	private List<BriefingZoneBean> zoneBean;

	private BriefingZoneBean normalBsvBean;
	
	private List<SmMofAccount> mofAccounts;
	private String bsvZoneindx = "0";
	private boolean maintanceFlow;
	
	private List<ScQtBsvRegistration> toBeDeletedRegistartions;
	private List<CtPanelSupplier> ctZoneSupplierList;
	private List<SmMofAccount> mofAccountsList;
	private List<ScQtSupplier> restrictedSupplierList;
	
	/**
	 * @return the briefingRegistrationList
	 */
	public List<ScQtBsvRegistration> getBriefingRegistrationList() {
		return briefingRegistrationList;
	}

	/**
	 * @param briefingRegistrationList the briefingRegistrationList to set
	 */
	public void setBriefingRegistrationList(
			List<ScQtBsvRegistration> briefingRegistrationList) {
		this.briefingRegistrationList = briefingRegistrationList;
	}

	/**
	 * @return the briefingAttendanceList
	 */
	public List<ScQtBsvAttendance> getBriefingAttendanceList() {
		return briefingAttendanceList;
	}

	/**
	 * @param briefingAttendanceList the briefingAttendanceList to set
	 */
	public void setBriefingAttendanceList(
			List<ScQtBsvAttendance> briefingAttendanceList) {
		this.briefingAttendanceList = briefingAttendanceList;
	}

	/**
	 * @return the scQtBriefing
	 */
	public ScQtBsvDtl getScQtBriefing() {
		return scQtBriefing;
	}

	/**
	 * @param scQtBriefing the scQtBriefing to set
	 */
	public void setScQtBriefing(ScQtBsvDtl scQtBriefing) {
		this.scQtBriefing = scQtBriefing;
	}

	/**
	 * @return the selectedbriefRegistration
	 */
	public ScQtBsvRegistration getSelectedbriefRegistration() {
		return selectedbriefRegistration;
	}

	/**
	 * @param selectedbriefRegistration the selectedbriefRegistration to set
	 */
	public void setSelectedbriefRegistration(
			ScQtBsvRegistration selectedbriefRegistration) {
		this.selectedbriefRegistration = selectedbriefRegistration;
	}

	/**
	 * @return the scQtBriefingRegistration
	 */
	public ScQtBsvRegistration getScQtBriefingRegistration() {
		return scQtBriefingRegistration;
	}

	/**
	 * @param scQtBriefingRegistration the scQtBriefingRegistration to set
	 */
	public void setScQtBriefingRegistration(
			ScQtBsvRegistration scQtBriefingRegistration) {
		this.scQtBriefingRegistration = scQtBriefingRegistration;
	}

	/**
	 * @return the scQtBriefingAttendance
	 */
	public ScQtBsvAttendance getScQtBriefingAttendance() {
		return scQtBriefingAttendance;
	}

	/**
	 * @param scQtBriefingAttendance the scQtBriefingAttendance to set
	 */
	public void setScQtBriefingAttendance(ScQtBsvAttendance scQtBriefingAttendance) {
		this.scQtBriefingAttendance = scQtBriefingAttendance;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * @return the previousPage
	 */
	public String getPreviousPage() {
		return previousPage;
	}

	/**
	 * @param previousPage the previousPage to set
	 */
	public void setPreviousPage(String previousPage) {
		this.previousPage = previousPage;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the address
	 */
	public ScQtBsvDtl getBsvDetails() {
		return bsvDetails;
	}

	/**
	 * @param address the address to set
	 */
	public void setBsvDetails(ScQtBsvDtl bsvDetails) {
		this.bsvDetails = bsvDetails;
	}

	/**
	 * @return the scQtBsv
	 */
	public ScQtBsv getScQtBsv() {
		return scQtBsv;
	}

	/**
	 * @param scQtBsv the scQtBsv to set
	 */
	public void setScQtBsv(ScQtBsv scQtBsv) {
		this.scQtBsv = scQtBsv;
	}

	/**
	 * @return the zoneBean
	 */
	public List<BriefingZoneBean> getZoneBean() {
		return zoneBean;
	}

	/**
	 * @param zoneBean the zoneBean to set
	 */
	public void setZoneBean(List<BriefingZoneBean> zoneBean) {
		this.zoneBean = zoneBean;
	}

	/**
	 * @return the normalBsvBean
	 */
	public BriefingZoneBean getNormalBsvBean() {
		return normalBsvBean;
	}

	/**
	 * @param normalBsvBean the normalBsvBean to set
	 */
	public void setNormalBsvBean(BriefingZoneBean normalBsvBean) {
		this.normalBsvBean = normalBsvBean;
	}

	/**
	 * @return the mofAccounts
	 */
	public List<SmMofAccount> getMofAccounts() {
		return mofAccounts;
	}

	/**
	 * @param mofAccounts the mofAccounts to set
	 */
	public void setMofAccounts(List<SmMofAccount> mofAccounts) {
		this.mofAccounts = mofAccounts;
	}

	/**
	 * @return the bsvZoneindx
	 */
	public String getBsvZoneindx() {
		return bsvZoneindx;
	}

	/**
	 * @param bsvZoneindx the bsvZoneindx to set
	 */
	public void setBsvZoneindx(String bsvZoneindx) {
		this.bsvZoneindx = bsvZoneindx;
	}

	/**
	 * @return the toBeDeletedRegistartions
	 */
	public List<ScQtBsvRegistration> getToBeDeletedRegistartions() {
		return toBeDeletedRegistartions;
	}

	/**
	 * @param toBeDeletedRegistartions the toBeDeletedRegistartions to set
	 */
	public void setToBeDeletedRegistartions(
			List<ScQtBsvRegistration> toBeDeletedRegistartions) {
		this.toBeDeletedRegistartions = toBeDeletedRegistartions;
	}

	public boolean isMaintanceFlow() {
		return maintanceFlow;
	}

	public void setMaintanceFlow(boolean maintanceFlow) {
		this.maintanceFlow = maintanceFlow;
	}

	/**
	 * @return the ctZoneSupplierList
	 */
	public List<CtPanelSupplier> getCtZoneSupplierList() {
		return ctZoneSupplierList;
	}

	/**
	 * @param ctZoneSupplierList the ctZoneSupplierList to set
	 */
	public void setCtZoneSupplierList(List<CtPanelSupplier> ctZoneSupplierList) {
		this.ctZoneSupplierList = ctZoneSupplierList;
	}

	/**
	 * @return the mofAccountsList
	 */
	public List<SmMofAccount> getMofAccountsList() {
		return mofAccountsList;
	}

	/**
	 * @param mofAccountsList the mofAccountsList to set
	 */
	public void setMofAccountsList(List<SmMofAccount> mofAccountsList) {
		this.mofAccountsList = mofAccountsList;
	}

	/**
	 * @return the restrictedSupplierList
	 */
	public List<ScQtSupplier> getRestrictedSupplierList() {
		return restrictedSupplierList;
	}

	/**
	 * @param restrictedSupplierList the restrictedSupplierList to set
	 */
	public void setRestrictedSupplierList(List<ScQtSupplier> restrictedSupplierList) {
		this.restrictedSupplierList = restrictedSupplierList;
	}
	
}
