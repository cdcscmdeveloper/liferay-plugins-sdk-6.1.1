/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.common.SupplierDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
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
	
	// #11134 - start
	/**
	 * Pass a true = render BOD. Must set this before set ScQtProposal using setScQtProposal
	 * @param hasBod
	 */
	public void setHasBod(boolean hasBod) {
		this.getManagedBean().setHasBod(hasBod);
	}
	// #11134 - stop

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
		

		// BOD 
		// #11134 start
		if(this.getManagedBean().isHasBod()
				&& this.getManagedBean().getSmSupplier() != null){
			showBoardofDirectors(this.getManagedBean().getSmSupplier());
		}
		// #11134 end
		
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
	// #11134 - start
	private void showBoardofDirectors(SmSupplier selectedSupplier) {
		debugLogger("showBoardofDirectors");

		if (selectedSupplier == null) {
			return;
		}
		log.info("Supplier Name :: " + selectedSupplier.getCompanyName());

		List<SmPersonnel> boardofDirectors = null;
		IPersonnelSO personnelSO = null;
		try {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(selectedSupplier.getSupplierId());
			reqDTO.setSupplierType(ParameterConst.SUPPLIER_TYPE_CONTRACTOR);

			personnelSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
			PersonnelResDTO resDTO = personnelSO
					.retrieveBordOfDirectors(reqDTO);
			if (resDTO.isSuccessful()) {
				boardofDirectors = resDTO.getSmPersonnelList();
				if (CollectionUtils.isNotEmpty(boardofDirectors)) {
					for (SmPersonnel smPersonnel : boardofDirectors) {
						SmMofAccount smMofAccount = retriveMofAccountBySupplier(smPersonnel
								.getSmAppl().getSmSupplier());
						smPersonnel.getSmAppl().getSmSupplier()
								.setSmMofAccount(smMofAccount);
					}
					
					Collections.sort(boardofDirectors, boardOfDirectorComparator);
				}
			}
		} finally {
			personnelSO = null;
		}
		
		getManagedBean().setBoardofDirectors(boardofDirectors);
	}
	// #11134 - end
	
	/**
	 * method to handle reset data.
	 * 
	 * 
	 */
	public void reset() {
		this.getManagedBean().setMofNo(null);
		this.getManagedBean().setSmApplId(null);
		this.getManagedBean().setSupplierName(null);
		this.getManagedBean().setBoardofDirectors(null); // #11134
	}
	
	private static Comparator<SmPersonnel> boardOfDirectorComparator = new Comparator<SmPersonnel>() {

		@Override
		public int compare(SmPersonnel o1, SmPersonnel o2) {
			String a = o1.getSmAppl().getSmSupplier().getCompanyName();
			String b = o2.getSmAppl().getSmSupplier().getCompanyName();
			return CompareToBuilder.reflectionCompare(a, b);
		}
		
	};
	
}
