/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.managedbean.intergration.interfacegfmas;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.persistence.entity.pm.common.DIInterfaceLog;
import my.ep.persistence.entity.pm.integration.PmInterfaceAgOff;
import my.ep.persistence.entity.pm.integration.PmInterfaceBank;
import my.ep.persistence.entity.pm.integration.PmInterfaceDana;
import my.ep.persistence.entity.pm.integration.PmInterfaceGlAccount;
import my.ep.persistence.entity.pm.integration.PmInterfaceKptj;
import my.ep.persistence.entity.pm.integration.PmInterfacePP;
import my.ep.persistence.entity.pm.integration.PmInterfaceProgAct;
import my.ep.persistence.entity.pm.integration.PmInterfaceProject;
import my.ep.persistence.entity.pm.integration.PmInterfacePtj;
import my.ep.persistence.entity.pm.integration.PmInterfaceVot;
import my.ep.web.managedbean.BaseManagedBean;

// TODO: Auto-generated Javadoc
/**
 * The Class GfMasManagedBean.
 *   @author NAGASIVA
 *   @since 17.06.2016
 */
@ManagedBean(name = "gfMasManagedBean")
@SessionScoped
public class GfMasManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    /** The service code. */
    private String serviceCode;
    
    /** The process id. */
    private String processId;
    
    /** The direction. */
    private String direction;
    
    /** The interface log id. */
    private Long interfaceLogId;
    
    /** The created date. */
    private Date createdDate;

	/** The is lazy. */
	private boolean isLazy;	
	
	/** The d i interface log. */
	private DIInterfaceLog dIInterfaceLog;
	
	/**
	 * @return the listOfPrgAct
	 */
	public List<PmInterfaceProgAct> getListOfPrgAct() {
		return listOfPrgAct;
	}

	/**
	 * @param listOfPrgAct the listOfPrgAct to set
	 */
	public void setListOfPrgAct(List<PmInterfaceProgAct> listOfPrgAct) {
		this.listOfPrgAct = listOfPrgAct;
	}

	/**
	 * @return the listOfAgOffice
	 */
	public List<PmInterfaceAgOff> getListOfAgOffice() {
		return listOfAgOffice;
	}

	/**
	 * @param listOfAgOffice the listOfAgOffice to set
	 */
	public void setListOfAgOffice(List<PmInterfaceAgOff> listOfAgOffice) {
		this.listOfAgOffice = listOfAgOffice;
	}

	/**
	 * @return the listOfBank
	 */
	public List<PmInterfaceBank> getListOfBank() {
		return listOfBank;
	}

	/**
	 * @param listOfBank the listOfBank to set
	 */
	public void setListOfBank(List<PmInterfaceBank> listOfBank) {
		this.listOfBank = listOfBank;
	}

	/**
	 * @return the listOfDana
	 */
	public List<PmInterfaceDana> getListOfDana() {
		return listOfDana;
	}

	/**
	 * @param listOfDana the listOfDana to set
	 */
	public void setListOfDana(List<PmInterfaceDana> listOfDana) {
		this.listOfDana = listOfDana;
	}

	/**
	 * @return the listOfGlAccount
	 */
	public List<PmInterfaceGlAccount> getListOfGlAccount() {
		return listOfGlAccount;
	}

	/**
	 * @param listOfGlAccount the listOfGlAccount to set
	 */
	public void setListOfGlAccount(List<PmInterfaceGlAccount> listOfGlAccount) {
		this.listOfGlAccount = listOfGlAccount;
	}

	/**
	 * @return the listOfKPTJ
	 */
	public List<PmInterfaceKptj> getListOfKPTJ() {
		return listOfKPTJ;
	}

	/**
	 * @param listOfKPTJ the listOfKPTJ to set
	 */
	public void setListOfKPTJ(List<PmInterfaceKptj> listOfKPTJ) {
		this.listOfKPTJ = listOfKPTJ;
	}

	/**
	 * @return the listOfProjectt
	 */
	public List<PmInterfaceProject> getListOfProjectt() {
		return listOfProjectt;
	}

	/**
	 * @param listOfProjectt the listOfProjectt to set
	 */
	public void setListOfProjectt(List<PmInterfaceProject> listOfProjectt) {
		this.listOfProjectt = listOfProjectt;
	}

	/**
	 * @return the listOfPTJ
	 */
	public List<PmInterfacePtj> getListOfPTJ() {
		return listOfPTJ;
	}

	/**
	 * @param listOfPTJ the listOfPTJ to set
	 */
	public void setListOfPTJ(List<PmInterfacePtj> listOfPTJ) {
		this.listOfPTJ = listOfPTJ;
	}

	/**
	 * @return the listOfVot
	 */
	public List<PmInterfaceVot> getListOfVot() {
		return listOfVot;
	}

	/**
	 * @param listOfVot the listOfVot to set
	 */
	public void setListOfVot(List<PmInterfaceVot> listOfVot) {
		this.listOfVot = listOfVot;
	}

	/**
	 * @return the listOfPP
	 */
	public List<PmInterfacePP> getListOfPP() {
		return listOfPP;
	}

	/**
	 * @param listOfPP the listOfPP to set
	 */
	public void setListOfPP(List<PmInterfacePP> listOfPP) {
		this.listOfPP = listOfPP;
	}

	/** The list of prg act. */
	private List<PmInterfaceProgAct> listOfPrgAct;
	
	/** The list of ag office. */
	private List<PmInterfaceAgOff> listOfAgOffice;
	
	/** The list of bank. */
	private List<PmInterfaceBank> listOfBank;
	
	/** The list of dana. */
	private List<PmInterfaceDana> listOfDana;
	
	/** The list of gl account. */
	private List<PmInterfaceGlAccount> listOfGlAccount;
	
	/** The list of kptj. */
	private List<PmInterfaceKptj> listOfKPTJ;
	
	/** The list of projectt. */
	private List<PmInterfaceProject> listOfProjectt;
	
	/** The list of ptj. */
	private List<PmInterfacePtj> listOfPTJ;
	
	/** The list of vot. */
	private List<PmInterfaceVot> listOfVot;
	
	/** The list of pp. */
	private List<PmInterfacePP> listOfPP;
	
	

	/**
	 * Checks if is lazy.
	 * 
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * Sets the lazy.
	 * 
	 * @param isLazy
	 *            the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * Gets the d i interface log.
	 * 
	 * @return the dIInterfaceLog
	 */
	public DIInterfaceLog getdIInterfaceLog() {
		return dIInterfaceLog;
	}

	

	/**
	 * Sets the d i interface log.
	 * 
	 * @param dIInterfaceLog
	 *            the dIInterfaceLog to set
	 */
	public void setdIInterfaceLog(DIInterfaceLog dIInterfaceLog) {
		this.dIInterfaceLog = dIInterfaceLog;
	}

	/**
	 * Gets the service code.
	 * 
	 * @return the serviceCode
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * Sets the service code.
	 * 
	 * @param serviceCode
	 *            the serviceCode to set
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	/**
	 * Gets the process id.
	 * 
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * Sets the process id.
	 * 
	 * @param processId
	 *            the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	/**
	 * Gets the direction.
	 * 
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * Gets the interface log id.
	 * 
	 * @return the interfaceLogId
	 */
	public Long getInterfaceLogId() {
		return interfaceLogId;
	}

	/**
	 * Sets the interface log id.
	 * 
	 * @param interfaceLogId
	 *            the interfaceLogId to set
	 */
	public void setInterfaceLogId(Long interfaceLogId) {
		this.interfaceLogId = interfaceLogId;
	}

	/**
	 * Sets the direction.
	 * 
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * Gets the created date.
	 * 
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	
	/**
	 * Sets the created date.
	 * 
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
