package my.ep.web.managedbean.sm.travellingTicket;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmMachineSection;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.web.constant.sm.MachineSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "travellingTicketMB")
@SessionScoped
public class TravellingTicketManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	

	public static Logger log = Logger.getLogger(TravellingTicketManagedBean.class);
	
	private MachineSearchBean machineSearchBean;
	
	private boolean isLazy;
	
	public TravellingTicketManagedBean(){}
	
	private List<SelectItem> listOfCategories;
	
	private List<PmParameter> listPmParameter;
	
	private PmMachine pmMachine;
	
	private PmMachineSection pmMachineSection;
	private PmMachine selectedMachine;
	
	private String Code;
	
	
	
	private int recordStatus;
	
	private boolean disabledVal;

	public List<SelectItem> getListOfCategories() {
		if(listOfCategories==null){
			listOfCategories=new ArrayList<SelectItem>();
		}
		return listOfCategories;
	}

	/**
	 * @param listOfStates the listOfStates to set
	 */
	public void setListOfCategories(List<SelectItem> listOfCategories) {
		this.listOfCategories = listOfCategories;
	}

	public MachineSearchBean getMachineSearchBean() {
		return machineSearchBean;
	}

	public void setMachineSearchBean(MachineSearchBean machineSearchBean) {
		this.machineSearchBean = machineSearchBean;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public PmMachine getPmMachine() {
		return pmMachine;
	}

	public void setPmMachine(PmMachine pmMachine) {
		this.pmMachine = pmMachine;
	}

	public PmMachineSection getPmMachineSection() {
		return pmMachineSection;
	}

	public void setPmMachineSection(PmMachineSection pmMachineSection) {
		this.pmMachineSection = pmMachineSection;
	}

	public int getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
	}

	public boolean isDisabledVal() {
		return disabledVal;
	}

	public void setDisabledVal(boolean disabledVal) {
		this.disabledVal = disabledVal;
	}
	public PmMachine getSelectedMachine() {
		if(selectedMachine==null)
			selectedMachine = new PmMachine();
		return selectedMachine;
	}
	public void setSelectedMachine(PmMachine selectedMachine) {
		this.selectedMachine = selectedMachine;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}
}
