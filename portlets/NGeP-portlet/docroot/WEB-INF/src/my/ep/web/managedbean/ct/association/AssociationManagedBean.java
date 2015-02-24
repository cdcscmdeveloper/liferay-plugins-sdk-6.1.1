/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.association;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Details
 * 
 * @author Alam
 * @since 23/9/2013	 
 */
@ManagedBean(name ="associationManagedBean")
@SessionScoped
public class AssociationManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;	
	private String ctName;
	private String ctNumber;
	private String projectName;
	private String ctProductCode;
	private String ctSupplierName;
	private String projectDescription;
	private Date ctDateStart;
	private Date ctDateEnd;
	private List<CtContract> associateSearchList;
	private List<CtAssociation> associationList;
	private CtAssociation ctAssociation;
	private CtAssociation ctUpdateAssociation;
	
	/**
	 * @return the ctName
	 */
	public String getCtName() {
		return ctName;
	}
	
	/**
	 * @param ctName the ctName to set
	 */
	public void setCtName(String ctName) {
		this.ctName = ctName;
	}
	
	/**
	 * @return the ctNumber
	 */
	public String getCtNumber() {
		return ctNumber;
	}
	
	/**
	 * @param ctNumber the ctNumber to set
	 */
	public void setCtNumber(String ctNumber) {
		this.ctNumber = ctNumber;
	}
	
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	/**
	 * @return the ctProductCode
	 */
	public String getCtProductCode() {
		return ctProductCode;
	}
	
	/**
	 * @param ctProductCode the ctProductCode to set
	 */
	public void setCtProductCode(String ctProductCode) {
		this.ctProductCode = ctProductCode;
	}
	
	/**
	 * @return the ctSupplierName
	 */
	public String getCtSupplierName() {
		return ctSupplierName;
	}
	
	/**
	 * @param ctSupplierName the ctSupplierName to set
	 */
	public void setCtSupplierName(String ctSupplierName) {
		this.ctSupplierName = ctSupplierName;
	}
	
	/**
	 * @return the projectDescription
	 */
	public String getProjectDescription() {
		return projectDescription;
	}
	
	/**
	 * @param projectDescription the projectDescription to set
	 */
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}
	
	/**
	 * @return the ctDateStart
	 */
	public Date getCtDateStart() {
		return ctDateStart;
	}
	
	/**
	 * @param ctDateStart the ctDateStart to set
	 */
	public void setCtDateStart(Date ctDateStart) {
		this.ctDateStart = ctDateStart;
	}
	
	/**
	 * @return the ctDateEnd
	 */
	public Date getCtDateEnd() {
		return ctDateEnd;
	}
	
	/**
	 * @param ctDateEnd the ctDateEnd to set
	 */
	public void setCtDateEnd(Date ctDateEnd) {
		this.ctDateEnd = ctDateEnd;
	}
	
	/**
	 * @return the associateSearchList
	 */
	public List<CtContract> getAssociateSearchList() {
		if (associateSearchList == null) {
			associateSearchList = new ArrayList<CtContract>();
		}
		return associateSearchList;
	}
	
	/**
	 * @param associateSearchList the associateSearchList to set
	 */
	public void setAssociateSearchList(List<CtContract> associateSearchList) {
		this.associateSearchList = associateSearchList;
	}
	
	/**
	 * @return the associationList
	 */
	public List<CtAssociation> getAssociationList() {
		if (associationList == null) {
			associationList = new ArrayList<CtAssociation>();
		}
		return associationList;
	}
	
	/**
	 * @param associationList the associationList to set
	 */
	public void setAssociationList(List<CtAssociation> associationList) {
		this.associationList = associationList;
	}
	
	/**
	 * @return the ctAssociation
	 */
	public CtAssociation getCtAssociation() {
		return ctAssociation;
	}
	
	/**
	 * @param ctAssociation the ctAssociation to set
	 */
	public void setCtAssociation(CtAssociation ctAssociation) {
		this.ctAssociation = ctAssociation;
	}
	
	/**
	 * @return the ctUpdateAssociation
	 */
	public CtAssociation getCtUpdateAssociation() {
		return ctUpdateAssociation;
	}
	
	/**
	 * @param ctUpdateAssociation the ctUpdateAssociation to set
	 */
	public void setCtUpdateAssociation(CtAssociation ctUpdateAssociation) {
		this.ctUpdateAssociation = ctUpdateAssociation;
	}
}
