/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.common.SupplierDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Common Supplier Detail Backing Bean
 * 
 * @author Kenny
 * @since 05.07.2014
 */
@ManagedBean
@RequestScoped
public class SupplierDetailBackingBean extends
		SourcingBaseBackingBean<SupplierDetailManagedBean> {
	public static Logger log = EpLogger
			.getLogger(SupplierDetailBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	public SupplierDetailBackingBean() {
	}

	/**
	 * Get  Supplier GstDetails   based on Supplier.
	 * 
	 * @param smSupplier
	 * 
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		debugLogger("setSmSupplier");
		ISupplierGstSO supplierGstSo = null;
		SmSupplier selectedSupplier = getManagedBean().getSmSupplier();
		boolean hasQtProposal = this.getManagedBean().isHasQtProposal();
		
		if (smSupplier != null
				&& (selectedSupplier == null || smSupplier.getSupplierId()
						.longValue() != selectedSupplier.getSupplierId()
						.longValue())) {
			if (!hasQtProposal) {
				reset();
			}
			
			IQTSupplierSO qtSupplierSO = null;
			try {
				qtSupplierSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSupplierSO.class);

				QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
				reqDTO.setSupplierId(smSupplier.getSupplierId());
				reqDTO.setSmApplId(getManagedBean().getSmApplId());

				QTSupplierResDTO resDTO = qtSupplierSO
						.retrieveSupplierDtlById(reqDTO);

				getManagedBean().setSmSupplier(resDTO.getSmSupplier());
				if (getManagedBean().getMofNo() == null && 
							resDTO.getSmMofAccount() != null) {
					getManagedBean().setMofNo(
							resDTO.getSmMofAccount().getMofNo());
				}
				if (getManagedBean().getSupplierName() == null) {
					getManagedBean().setSupplierName(
									 smSupplier.getCompanyName());
				}
				getManagedBean().setSmAppl(resDTO.getSmAppl());
				
				supplierGstSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierGstSO.class);
				SupplierGstReqDTO supplierGstreqDTO = new SupplierGstReqDTO();

				supplierGstreqDTO.setSupplierId(smSupplier.getSupplierId());
				supplierGstreqDTO.setBranchCode(null);

				SupplierGstResDTO supplierGstresDTO = supplierGstSo
						.retrieveGstInfoBySupplierIdAndBranchCode(supplierGstreqDTO);
				if (null != supplierGstresDTO
						&& null != supplierGstresDTO.getSmGstSupplier()) {
					this.getManagedBean().setSmGstSupplier(
							supplierGstresDTO.getSmGstSupplier());
				} else {
					this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
				}

			} finally {
				qtSupplierSO = null;
				supplierGstSo = null;
			}
		} else if (smSupplier == null) {
			log.warn("smSupplier is null.");
		} else {
			log.info("show supplier details from session.");
		}
		this.getManagedBean().setHasQtProposal(false);
	}
	
	/**
	 * Get  SmSupplier data based on proposal.
	 * 
	 * @param scQtProposal
	 * 
	 */
	public void setScQtProposal(ScQtProposal scQtProposal) {
		debugLogger("setScQtProposal");

		this.getManagedBean().setHasQtProposal(true);
		SmSupplier selectedSupplier = getManagedBean().getSmSupplier();
		if (scQtProposal != null
				&& (selectedSupplier == null || 
						scQtProposal.getScQtSupplier().getSmSupplier().getSupplierId()
							.longValue() != selectedSupplier.getSupplierId().longValue())) {
			
			this.getManagedBean().setMofNo(
						scQtProposal.getMofNo());			
			this.getManagedBean().setSupplierName(
						scQtProposal.getSupplierName());
			this.getManagedBean().setSmApplId(
					scQtProposal.getSupplierApplId());
			
			setSmSupplier(scQtProposal.getScQtSupplier().getSmSupplier());
		} else if (scQtProposal == null) {
			log.warn("scQtProposal is null.");
		}
	}
	
	/**
	 * method to handle reset data.
	 * 
	 * 
	 */
	public void reset() {
		this.getManagedBean().setMofNo(null);
		this.getManagedBean().setSmApplId(null);
		this.getManagedBean().setSupplierName(null);
	}
	
}
