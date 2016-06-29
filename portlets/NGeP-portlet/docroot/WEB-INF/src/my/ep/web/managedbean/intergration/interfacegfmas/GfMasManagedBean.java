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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    /** The file name. */
    private String fileName;

	/** The is lazy. */
	private boolean isLazy;	
	
	/** The disable. */
	private boolean disable;
	
	/** The process button status. */
	private boolean processButtonStatus;
	
	/** The d i interface log. */
	private DIInterfaceLog dIInterfaceLog;
	
	/** The map of process. */
	private Map<String, List<DIInterfaceLog>> mapOfProcess;
	
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
	
  
    /** The selected prog act list. */
    private List<PmInterfaceProgAct> selectedProgActList;
	

	/** The selected ag office list. */
	private List<PmInterfaceAgOff> selectedAgOfficeList;
	

	/** The selected bank list. */
	private List<PmInterfaceBank> selectedBankList;
	
	
	/** The selected dana list. */
	private List<PmInterfaceDana> selectedDanaList;
	
	
	/** The selected gl acnt list. */
	private List<PmInterfaceGlAccount> selectedGlAcntList;
	
	
	/** The selected kptj list. */
	private List<PmInterfaceKptj> selectedKptjList;
	
	

	/** The selected project list. */
	private List<PmInterfaceProject> selectedProjectList;
	
	
	/** The selected ptj list. */
	private List<PmInterfacePtj> selectedPtjList;
	
	
	/** The selected vot list. */
	private List<PmInterfaceVot> selectedVotList;
	
	
	/** The selected pp list. */
	private List<PmInterfacePP> selectedPpList;
	
	
	/**
	 * Gets the list of prg act.
	 * 
	 * @return the listOfPrgAct
	 */
	public List<PmInterfaceProgAct> getListOfPrgAct() {
		return listOfPrgAct;
	}

	/**
	 * Sets the list of prg act.
	 * 
	 * @param listOfPrgAct
	 *            the listOfPrgAct to set
	 */
	public void setListOfPrgAct(List<PmInterfaceProgAct> listOfPrgAct) {
		this.listOfPrgAct = listOfPrgAct;
	}

	/**
	 * Gets the list of ag office.
	 * 
	 * @return the listOfAgOffice
	 */
	public List<PmInterfaceAgOff> getListOfAgOffice() {
		return listOfAgOffice;
	}

	/**
	 * Sets the list of ag office.
	 * 
	 * @param listOfAgOffice
	 *            the listOfAgOffice to set
	 */
	public void setListOfAgOffice(List<PmInterfaceAgOff> listOfAgOffice) {
		this.listOfAgOffice = listOfAgOffice;
	}

	/**
	 * Gets the list of bank.
	 * 
	 * @return the listOfBank
	 */
	public List<PmInterfaceBank> getListOfBank() {
		return listOfBank;
	}

	/**
	 * Sets the list of bank.
	 * 
	 * @param listOfBank
	 *            the listOfBank to set
	 */
	public void setListOfBank(List<PmInterfaceBank> listOfBank) {
		this.listOfBank = listOfBank;
	}

	/**
	 * Gets the list of dana.
	 * 
	 * @return the listOfDana
	 */
	public List<PmInterfaceDana> getListOfDana() {
		return listOfDana;
	}

	/**
	 * Checks if is process button status.
	 * 
	 * @return the processButtonStatus
	 */
	public boolean isProcessButtonStatus() {
		return processButtonStatus;
	}

	/**
	 * Sets the process button status.
	 * 
	 * @param processButtonStatus
	 *            the processButtonStatus to set
	 */
	public void setProcessButtonStatus(boolean processButtonStatus) {
		this.processButtonStatus = processButtonStatus;
	}

	/**
	 * Gets the map of process.
	 * 
	 * @return the mapOfProcess
	 */
	public Map<String, List<DIInterfaceLog>> getMapOfProcess() {
		if (mapOfProcess == null) {
			mapOfProcess = new HashMap<String, List<DIInterfaceLog>>();
		}
		return mapOfProcess;
	}

	/**
	 * Sets the map of process.
	 * 
	 * @param mapOfProcess
	 *            the mapOfProcess to set
	 */
	public void setMapOfProcess(Map<String, List<DIInterfaceLog>> mapOfProcess) {
		this.mapOfProcess = mapOfProcess;
	}

	/**
	 * Gets the file name.
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Checks if is disable.
	 * 
	 * @return the disable
	 */
	public boolean isDisable() {
		return disable;
	}

	/**
	 * Sets the disable.
	 * 
	 * @param disable
	 *            the disable to set
	 */
	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	/**
	 * Sets the list of dana.
	 * 
	 * @param listOfDana
	 *            the listOfDana to set
	 */
	public void setListOfDana(List<PmInterfaceDana> listOfDana) {
		this.listOfDana = listOfDana;
	}

	/**
	 * Gets the list of gl account.
	 * 
	 * @return the listOfGlAccount
	 */
	public List<PmInterfaceGlAccount> getListOfGlAccount() {
		return listOfGlAccount;
	}

	/**
	 * Sets the list of gl account.
	 * 
	 * @param listOfGlAccount
	 *            the listOfGlAccount to set
	 */
	public void setListOfGlAccount(List<PmInterfaceGlAccount> listOfGlAccount) {
		this.listOfGlAccount = listOfGlAccount;
	}

	/**
	 * Gets the list of kptj.
	 * 
	 * @return the listOfKPTJ
	 */
	public List<PmInterfaceKptj> getListOfKPTJ() {
		return listOfKPTJ;
	}

	/**
	 * Sets the list of kptj.
	 * 
	 * @param listOfKPTJ
	 *            the listOfKPTJ to set
	 */
	public void setListOfKPTJ(List<PmInterfaceKptj> listOfKPTJ) {
		this.listOfKPTJ = listOfKPTJ;
	}

	/**
	 * Gets the list of projectt.
	 * 
	 * @return the listOfProjectt
	 */
	public List<PmInterfaceProject> getListOfProjectt() {
		return listOfProjectt;
	}

	/**
	 * Sets the list of projectt.
	 * 
	 * @param listOfProjectt
	 *            the listOfProjectt to set
	 */
	public void setListOfProjectt(List<PmInterfaceProject> listOfProjectt) {
		this.listOfProjectt = listOfProjectt;
	}

	/**
	 * Gets the list of ptj.
	 * 
	 * @return the listOfPTJ
	 */
	public List<PmInterfacePtj> getListOfPTJ() {
		return listOfPTJ;
	}

	/**
	 * Gets the selected prog act list.
	 * 
	 * @return the selectedProgActList
	 */
	public List<PmInterfaceProgAct> getSelectedProgActList() {
		return selectedProgActList;
	}

	/**
	 * Sets the selected prog act list.
	 * 
	 * @param selectedProgActList
	 *            the selectedProgActList to set
	 */
	public void setSelectedProgActList(List<PmInterfaceProgAct> selectedProgActList) {
		this.selectedProgActList = selectedProgActList;
	}

	/**
	 * Sets the list of ptj.
	 * 
	 * @param listOfPTJ
	 *            the listOfPTJ to set
	 */
	public void setListOfPTJ(List<PmInterfacePtj> listOfPTJ) {
		this.listOfPTJ = listOfPTJ;
	}

	/**
	 * Gets the list of vot.
	 * 
	 * @return the listOfVot
	 */
	public List<PmInterfaceVot> getListOfVot() {
		return listOfVot;
	}

	/**
	 * Sets the list of vot.
	 * 
	 * @param listOfVot
	 *            the listOfVot to set
	 */
	public void setListOfVot(List<PmInterfaceVot> listOfVot) {
		this.listOfVot = listOfVot;
	}

	/**
	 * Gets the list of pp.
	 * 
	 * @return the listOfPP
	 */
	public List<PmInterfacePP> getListOfPP() {
		return listOfPP;
	}


	/**
	 * Gets the selected project list.
	 * 
	 * @return the selectedProjectList
	 */
	public List<PmInterfaceProject> getSelectedProjectList() {
		return selectedProjectList;
	}

	/**
	 * Sets the selected project list.
	 * 
	 * @param selectedProjectList
	 *            the selectedProjectList to set
	 */
	public void setSelectedProjectList(List<PmInterfaceProject> selectedProjectList) {
		this.selectedProjectList = selectedProjectList;
	}

	/**
	 * Gets the selected ag office list.
	 * 
	 * @return the selectedAgOfficeList
	 */
	public List<PmInterfaceAgOff> getSelectedAgOfficeList() {
		return selectedAgOfficeList;
	}

	/**
	 * Sets the selected ag office list.
	 * 
	 * @param selectedAgOfficeList
	 *            the selectedAgOfficeList to set
	 */
	public void setSelectedAgOfficeList(List<PmInterfaceAgOff> selectedAgOfficeList) {
		this.selectedAgOfficeList = selectedAgOfficeList;
	}

	/**
	 * Gets the selected bank list.
	 * 
	 * @return the selectedBankList
	 */
	public List<PmInterfaceBank> getSelectedBankList() {
		return selectedBankList;
	}

	/**
	 * Sets the selected bank list.
	 * 
	 * @param selectedBankList
	 *            the selectedBankList to set
	 */
	public void setSelectedBankList(List<PmInterfaceBank> selectedBankList) {
		this.selectedBankList = selectedBankList;
	}

	/**
	 * Gets the selected dana list.
	 * 
	 * @return the selectedDanaList
	 */
	public List<PmInterfaceDana> getSelectedDanaList() {
		return selectedDanaList;
	}

	/**
	 * Sets the selected dana list.
	 * 
	 * @param selectedDanaList
	 *            the selectedDanaList to set
	 */
	public void setSelectedDanaList(List<PmInterfaceDana> selectedDanaList) {
		this.selectedDanaList = selectedDanaList;
	}

	/**
	 * Gets the selected gl acnt list.
	 * 
	 * @return the selectedGlAcntList
	 */
	public List<PmInterfaceGlAccount> getSelectedGlAcntList() {
		return selectedGlAcntList;
	}

	/**
	 * Sets the selected gl acnt list.
	 * 
	 * @param selectedGlAcntList
	 *            the selectedGlAcntList to set
	 */
	public void setSelectedGlAcntList(List<PmInterfaceGlAccount> selectedGlAcntList) {
		this.selectedGlAcntList = selectedGlAcntList;
	}

	/**
	 * Gets the selected kptj list.
	 * 
	 * @return the selectedKptjList
	 */
	public List<PmInterfaceKptj> getSelectedKptjList() {
		return selectedKptjList;
	}

	/**
	 * Sets the selected kptj list.
	 * 
	 * @param selectedKptjList
	 *            the selectedKptjList to set
	 */
	public void setSelectedKptjList(List<PmInterfaceKptj> selectedKptjList) {
		this.selectedKptjList = selectedKptjList;
	}

	
	/**
	 * Gets the selected ptj list.
	 * 
	 * @return the selectedPtjList
	 */
	public List<PmInterfacePtj> getSelectedPtjList() {
		return selectedPtjList;
	}

	/**
	 * Sets the selected ptj list.
	 * 
	 * @param selectedPtjList
	 *            the selectedPtjList to set
	 */
	public void setSelectedPtjList(List<PmInterfacePtj> selectedPtjList) {
		this.selectedPtjList = selectedPtjList;
	}

	/**
	 * Gets the selected vot list.
	 * 
	 * @return the selectedVotList
	 */
	public List<PmInterfaceVot> getSelectedVotList() {
		return selectedVotList;
	}

	/**
	 * Sets the selected vot list.
	 * 
	 * @param selectedVotList
	 *            the selectedVotList to set
	 */
	public void setSelectedVotList(List<PmInterfaceVot> selectedVotList) {
		this.selectedVotList = selectedVotList;
	}

	/**
	 * Gets the selected pp list.
	 * 
	 * @return the selectedPpList
	 */
	public List<PmInterfacePP> getSelectedPpList() {
		return selectedPpList;
	}

	/**
	 * Sets the selected pp list.
	 * 
	 * @param selectedPpList
	 *            the selectedPpList to set
	 */
	public void setSelectedPpList(List<PmInterfacePP> selectedPpList) {
		this.selectedPpList = selectedPpList;
	}

	/**
	 * Sets the list of pp.
	 * 
	 * @param listOfPP
	 *            the listOfPP to set
	 */
	public void setListOfPP(List<PmInterfacePP> listOfPP) {
		this.listOfPP = listOfPP;
	}

	
	

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
