/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.association;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.assoc.CtAssocContract;
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Details
 * 
 * @author Alam
 * @since 23/9/2013	 
 */
@ManagedBean(name ="associateManagedBean")
@SessionScoped
public class AssociationManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	private String ctName;
	private String ctNumber;
	private String ctDateEnd;
	private String ctDateStart;
	private String projectName;
	private String ctProductCode;
	private String ctSupplierName;
	private String projectDescription;
	
	private int selectedIndex;
	private List<CtContract> contractList; 
	private List<CtContract> associateSearchList;
	private List<CtAssociation> associationList;
	
	private CtAssociation ctAssociation;
	private CtAssociation ctUpdateAssociation;
	private CtContract assocoiateCtContract;
	private CtAssocContract ctDelAssocContract;
	private CtAssocContract ctAssocContract;
	
	public String getCtName() {
		return ctName;
	}
	public void setCtName(String ctName) {
		this.ctName = ctName;
	}
	public String getCtDateEnd() {
		return ctDateEnd;
	}
	public void setCtDateEnd(String ctDateEnd) {
		this.ctDateEnd = ctDateEnd;
	}
	public String getCtDateStart() {
		return ctDateStart;
	}
	public void setCtDateStart(String ctDateStart) {
		this.ctDateStart = ctDateStart;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getCtProductCode() {
		return ctProductCode;
	}
	public void setCtProductCode(String ctProductCode) {
		this.ctProductCode = ctProductCode;
	}
	public String getCtSupplierName() {
		return ctSupplierName;
	}
	public void setCtSupplierName(String ctSupplierName) {
		this.ctSupplierName = ctSupplierName;
	}
	public String getProjectDescription() {
		return projectDescription;
	}
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	public CtAssociation getCtAssociation() {
		return ctAssociation;
	}

	public void setCtAssociation(CtAssociation ctAssociation) {
		this.ctAssociation = ctAssociation;
	}

	public List<CtAssociation> getAssociationList() {
		if(associationList == null)
			associationList = new ArrayList<CtAssociation>();
		return associationList;
	}

	public void setAssociationList(List<CtAssociation> associationList) {
		this.associationList = associationList;
	}

	public List<CtContract> getAssociateSearchList() {
		if(associateSearchList == null)
			  associateSearchList = new ArrayList<CtContract>();
		return associateSearchList;
	}

	public void setAssociateSearchList(List<CtContract> associateSearchList) {
		this.associateSearchList = associateSearchList;
	}

	public CtContract getAssocoiateCtContract() {
		return assocoiateCtContract;
	}

	public void setAssocoiateCtContract(CtContract assocoiateCtContract) {
		this.assocoiateCtContract = assocoiateCtContract;
	}
	
	public CtAssocContract getCtDelAssocContract() {
		return ctDelAssocContract;
	}

	public void setCtDelAssocContract(CtAssocContract ctDelAssocContract) {
		this.ctDelAssocContract = ctDelAssocContract;
	}
	
	public String getCtNumber() {
		return ctNumber;
	}
	
	public void setCtNumber(String ctNumber) {
		this.ctNumber = ctNumber;
	}
	public List<CtContract> getContractList() {
		if(contractList == null)
			contractList = new ArrayList<CtContract>();
		return contractList;
	}
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}
	public CtAssocContract getCtAssocContract() {
		return ctAssocContract;
	}
	public void setCtAssocContract(CtAssocContract ctAssocContract) {
		this.ctAssocContract = ctAssocContract;
	}
	public CtAssociation getCtUpdateAssociation() {
		return ctUpdateAssociation;
	}
	public void setCtUpdateAssociation(CtAssociation ctUpdateAssociation) {
		this.ctUpdateAssociation = ctUpdateAssociation;
	}

	
}
