/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.common;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Common Supplier Details Managed Bean
 * 
 * @author Kenny
 * @since 05.07.2014
 */

@ManagedBean(name = "supplierDetailMB")
@SessionScoped
public class SupplierDetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String mofNo;
	private String supplierName;
	private Long smApplId;
	private SmAppl smAppl;
	private SmSupplier smSupplier;
	private SmGstSupplier smGstSupplier;
	private boolean hasQtProposal;
	private boolean hasBod;
	private List<SmPersonnel> boardofDirectors;
	
	public SupplierDetailManagedBean() {
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier
	 *            the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the mofNo
	 */
	public String getMofNo() {
		return mofNo;
	}

	/**
	 * @param mofNo
	 *            the mofNo to set
	 */
	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName
	 *            the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the smApplId
	 */
	public Long getSmApplId() {
		return smApplId;
	}

	/**
	 * @param smApplId
	 *            the smApplId to set
	 */
	public void setSmApplId(Long smApplId) {
		this.smApplId = smApplId;
	}

	/**
	 * @return the smAppl
	 */
	public SmAppl getSmAppl() {
		return smAppl;
	}

	/**
	 * @param smAppl
	 *            the smAppl to set
	 */
	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	/**
	 * @return the smGstSupplier
	 */
	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	/**
	 * @param smGstSupplier
	 *            the smGstSupplier to set
	 */
	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	/**
	 * @return the hasQtProposal
	 */
	public boolean isHasQtProposal() {
		return hasQtProposal;
	}

	/**
	 * @param hasQtProposal
	 *            the hasQtProposal to set
	 */
	public void setHasQtProposal(boolean hasQtProposal) {
		this.hasQtProposal = hasQtProposal;
	}

	/**
	 * 
	 * @return hasBod - for rendering BOD
	 */
	public boolean isHasBod() {
		return hasBod;
	}

	/**
	 * 
	 * @param hasBod
	 * 			-for rendering BOD
	 */
	public void setHasBod(boolean hasBod) {
		this.hasBod = hasBod;
	}

	/**
	 * 
	 * @return board of directors data
	 */
	public List<SmPersonnel> getBoardofDirectors() {
		return boardofDirectors;
	}

	/**
	 * 
	 * @param boardofDirectors
	 * 			-board of directors data
	 */
	public void setBoardofDirectors(List<SmPersonnel> boardofDirectors) {
		this.boardofDirectors = boardofDirectors;
	}

}
