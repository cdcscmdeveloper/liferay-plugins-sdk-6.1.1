package my.ep.web.util.sc.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
//import my.ep.web.util.FacesScopes;
//import my.ep.web.util.lookupservice.BusinessDelegate;


import org.apache.log4j.Logger;


public class ReportHelper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = EpLogger.getLogger(ReportHelper.class);
	
	public static final String KEY_SEPARATOR = "_";
	
	private static ReportHelper instance;
	
	/**
	 * Get singleton of StatusHelper
	 * 
	 * @return StatusHelper singleton
	 */
	public static ReportHelper getInstance(){
		if (instance == null){
			instance = new ReportHelper();
		}
		return instance;
	}
	
	
	/**
	 * Get Status description by status id.
	 * 
	 * @param statusId
	 * 			the status id.
	 * @return PmStatusDesc instance
	 */
	public ScQtProposal getProposalDetails(Long qtId, Long supplierId) {
		//log.info("harun getProposalDetails.qtSupplierId: " + supplierId);

		QuationTenderResDTO resDTO = new QuationTenderResDTO();
		IQTDetailSO qtDetailSO;

		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setSupplierId(supplierId);
			reqDTO.setQtId(qtId);
			

			// Get status description from db.
			resDTO = qtDetailSO.retrieveProposalDetails(reqDTO);
			//log.info("harun ReportHelper retrieveProposalNoForListProposal resDTO.getScQtProposal() 222222============="+resDTO.getScQtProposal());

			//debugLogger("cek proposal No: " + proposalNo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			qtDetailSO = null;
		}

		return resDTO.getScQtProposal();
	}
	
	
	

}
