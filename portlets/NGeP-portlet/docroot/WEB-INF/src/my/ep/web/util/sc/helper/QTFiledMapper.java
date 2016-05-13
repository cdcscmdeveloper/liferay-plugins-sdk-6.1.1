/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.sc.helper;

import java.util.Arrays;
import java.util.List;

import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.type.IEnumType;

/**
 * 
 * QTFiledMapper class is used to enable or disable fileds
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public class QTFiledMapper {

	/**
	 * To enable fileds by statusid and maintance flow
	 * 
	 * @param statusId
	 * @param isMaintanceFlow
	 * @return List<String> fieldNames
	 */
	public static List<String> getEnableFieldsByStatus(Long statusId,
			boolean isMaintanceFlow, boolean isDeskOfficer) {
		List<String> updatedFiledsList = null;
		String[] fileds = null;
		if (isMaintanceFlow) {
			if (isDeskOfficer) {
				fileds = QTMaintanaceEnableFields.getValuesByStatusId(statusId);
			}
		} else {
			fileds = QTTaskListEnableFields.getValuesByStatusId(statusId);
		}
		if (null != fileds) {
			updatedFiledsList = Arrays.asList(fileds);
		}
		return updatedFiledsList;

	}

	/**
	 * Constant for Maintance Flow Fileds
	 * 
	 * Add the filed names which needs to be editable on the screen upon QT status
	 */
	enum QTMaintanaceEnableFields implements IEnumType {
		
		// Pending QT Submission
		QT_PENDING_QT_SUBMISSION(
				QTStatusConst.QT_PENDING_QT_SUBMISSION,
				new String[] { QTFieldConst.FILED_QT_TITLE,
						QTFieldConst.FILED_QT_FILE_NO,
						QTFieldConst.FILED_QT_ESTMT_AMT,
						QTFieldConst.FILED_QT_GOSODO,
						QTFieldConst.FILED_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FILED_QT_CATEGORY_CODE,
						QTFieldConst.FILED_QT_QT_TYPE,
						QTFieldConst.FILED_QT_OPEN_FOR,
						QTFieldConst.FILED_QT_PROCUREMENT_CATEGORY,
						QTFieldConst.FILED_QT_EVALUATION_TYPE,
						QTFieldConst.FILED_QT_CONTRACT_TYPE,
						QTFieldConst.FILED_QT_FULFILMENT_TYPE,
						QTFieldConst.FILED_QT_ZONE_COVERAGE,
						QTFieldConst.FILED_QT_EXISTING_CONTRACT,
						QTFieldConst.FILED_QT_REQUIRED_MINIS_SPEC_APPROVAL}), 
				
		// QT_PENDING_APPROVALS_CHECKLIST_SCORING
		QT_PENDING_APPROVALS_CHECKLIST_SCORING(
				QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING,
				new String[] { QTFieldConst.FILED_QT_TITLE,
						QTFieldConst.FILED_QT_FILE_NO,
						QTFieldConst.FILED_QT_QT_TYPE,
						QTFieldConst.FILED_QT_OPEN_FOR,
						QTFieldConst.FILED_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FILED_QT_EXISTING_CONTRACT,
						QTFieldConst.FILED_QT_ZONE_COVERAGE,
						QTFieldConst.FILED_QT_EVALUATION_TYPE,
						QTFieldConst.FILED_QT_FULFILMENT_TYPE,
						QTFieldConst.FILED_QT_REQUIRED_MINIS_SPEC_APPROVAL,
						QTFieldConst.FILED_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FILED_QT_DOCUMENTS}),
						
		//QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT		
		QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT,
				QT_PENDING_APPROVALS_CHECKLIST_SCORING.value()),
							      
		//QT_PENDING_CHECKLIST_SCORING_SIGN_OFF				         
		QT_PENDING_CHECKLIST_SCORING_SIGN_OFF(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF,
				QT_PENDING_APPROVALS_CHECKLIST_SCORING.value()),
										      
		//QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH				         
		QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH( QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH,
				QT_PENDING_APPROVALS_CHECKLIST_SCORING.value()),
										      
		//QT_PENDING_CHECKLIST_SCORING_REVISION						         
		QT_PENDING_CHECKLIST_SCORING_REVISION(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION,
				QT_PENDING_APPROVALS_CHECKLIST_SCORING.value()),		
						
         // Pending QT publication Approval
		QT_PENDING_PUBLICATION_APPROVAL(
				QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL, 
				new String[] {
						QTFieldConst.FILED_QT_TITLE,
						QTFieldConst.FILED_QT_CATEGORY_CODE,
						QTFieldConst.FILED_QT_LOCATION_COVERAGE,
						QTFieldConst.FILED_QT_SUPPQUALIFICATION,
						QTFieldConst.FILED_QT_BSVDETAILS,
						QTFieldConst.FILED_QT_PUBLISH_DATE,
						QTFieldConst.FILED_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FILED_QT_DOCUMENTS }),
						
        // Pending QT Publication
		QT_PENDING_PUBLICATION(QTStatusConst.QT_PENDING_PUBLICATION,
				new String[] { 
						QTFieldConst.FILED_QT_TITLE,
						QTFieldConst.FILED_QT_VALIDITY_PERIOD,
						QTFieldConst.FILED_QT_PUBLISH_PERIOD, }),
		
		
		// QT_PENDING_RESCHEDULE_PUBLICATION
		QT_PENDING_RESCHEDULE_PUBLICATION(QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION,
				new String[] { 
						QTFieldConst.FILED_QT_PUBLISH_DATE,
						QTFieldConst.FILED_QT_BSVDETAILS,
						QTFieldConst.FILED_QT_VALIDITY_PERIOD,
						QTFieldConst.FILED_QT_PUBLISH_PERIOD }),
						
		// Briefing/Site visit
		QT_BRIEFING_SITE_VISIT(QTStatusConst.QT_BRIEFING_SITE_VISIT,
				new String[] { 
						QTFieldConst.FILED_QT_VALIDITY_PERIOD,
						QTFieldConst.FILED_QT_PUBLISH_PERIOD }),
						
        //Proposal Preparation 
		QT_PROPOSAL_PREPARATION(QTStatusConst.QT_PROPOSAL_PREPARATION,
				QT_BRIEFING_SITE_VISIT.value()),
						
		// QT Closed				
		QT_CLOSED(QTStatusConst.QT_CLOSED,
				new String[] { QTFieldConst.FILED_QT_VALIDITY_PERIOD }),

		// Evaluation Proposal		
		QT_EVALUATION_PROPOSAL(QTStatusConst.QT_EVALUATION_PROPOSAL,
				QT_CLOSED.value() ),
        // Re Evaluation
		QT_RE_EVALUATION(QTStatusConst.QT_RE_EVALUATION, 
				QT_CLOSED.value()),
			
		// Pending Consolidation Report
		QT_CONSOLIDATION_REPORT(QTStatusConst.QT_CONSOLIDATION_REPORT,
						QT_CLOSED.value() ),

		// Supplier Finalization
		QT_SUPPLIER_FINALIZATION(QTStatusConst.QT_SUPPLIER_FINALIZATION,
				QT_CLOSED.value() ),
			
		// Re-Finalization
		QT_RE_FINALIZATION(QTStatusConst.QT_RE_FINALIZATION,
						QT_CLOSED.value() ),
				
		// Pending LOI
		QT_PENDING_LOI(QTStatusConst.QT_PENDING_LOI,
				QT_CLOSED.value() ),
		
		// Pending LOA
		QT_PENDING_LOA(QTStatusConst.QT_PENDING_LOA,
				QT_CLOSED.value() );
		
		private Long statusId;
		private String[] toBeEnableFields;

		private QTMaintanaceEnableFields(Long statusId,
				String[] tobeEnableFileds) {
			this.statusId = statusId;
			this.toBeEnableFields = tobeEnableFileds;

		}

		@Override
		public String[] value() {

			return toBeEnableFields;
		}

		public static String[] getValuesByStatusId(Long statusId) {
			for (QTMaintanaceEnableFields filedMapper : QTMaintanaceEnableFields
					.values()) {
				if (filedMapper.statusId.equals(statusId)) {
					return filedMapper.value();
				}
			}
			return null;
		}

	}

	/**
	 * Constant for TaskList Fileds
	 */
	enum QTTaskListEnableFields implements IEnumType {
		// QT_PENDING_QT_SUBMISSION
		QT_PENDING_QT_SUBMISSION(QTStatusConst.QT_PENDING_QT_SUBMISSION,
				new String[] { QTFieldConst.FILED_QT_TITLE,
						QTFieldConst.FILED_QT_FILE_NO,
						QTFieldConst.FILED_QT_ESTMT_AMT,
						QTFieldConst.FILED_QT_GOSODO,
						QTFieldConst.FILED_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FILED_QT_CATEGORY_CODE,
						QTFieldConst.FILED_QT_QT_TYPE,
						QTFieldConst.FILED_QT_OPEN_FOR,
						QTFieldConst.FILED_QT_PROCUREMENT_CATEGORY,
						QTFieldConst.FILED_QT_EVALUATION_TYPE,
						QTFieldConst.FILED_QT_CONTRACT_TYPE,
						QTFieldConst.FILED_QT_FULFILMENT_TYPE,
						QTFieldConst.FILED_QT_ZONE_COVERAGE,
						QTFieldConst.FILED_QT_EXISTING_CONTRACT,
						QTFieldConst.FILED_QT_REQUIRED_MINIS_SPEC_APPROVAL}),

		// QT_PENDING_APPROVALS_CHECKLIST_SCORING
		QT_PENDING_APPROVALS_CHECKLIST_SCORING(
				QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING,

				new String[] {
						QTFieldConst.FILED_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FILED_QT_EVALUATION_TYPE,
						QTFieldConst.FILED_QT_FULFILMENT_TYPE,
						QTFieldConst.FILED_QT_ZONE_COVERAGE,
						QTFieldConst.FILED_QT_EXISTING_CONTRACT,
						QTFieldConst.FILED_QT_QT_TYPE,
						QTFieldConst.FILED_QT_OPEN_FOR}), 
		
		// QT_PENDING_APPROVALS_CHECKLIST_SCORING_REVISION
		QT_PENDING_APPROVALS_CHECKLIST_SCORING_REVISION(
				QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION,

				QT_PENDING_APPROVALS_CHECKLIST_SCORING.value()), 
				
		// QT_PENDING_SCHEDULE_DOCUMENT
		QT_PENDING_SCHEDULE_DOCUMENT(
				QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, new String[] {
						QTFieldConst.FILED_QT_TITLE,
						QTFieldConst.FILED_QT_BSVDETAILS,
						QTFieldConst.FILED_QT_PUBLISH_DATE,
						QTFieldConst.FILED_QT_SCHDETAILS,
						QTFieldConst.FILED_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FILED_QT_DOCUMENTS,
						QTFieldConst.FILED_QT_PUB_APPROVAL,
						QTFieldConst.FILED_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FILED_QT_MINIMUM_PAID_UP_CAPITAL,
						QTFieldConst.FILED_QT_CATEGORY_CODE,
						QTFieldConst.FILED_QT_LOCATION_COVERAGE,
						QTFieldConst.FILED_QT_SUPPQUALIFICATION,
						QTFieldConst.FILED_QT_QT_TYPE,
						QTFieldConst.FILED_QT_OPEN_FOR,
						QTFieldConst.FILED_QT_EVALUATION_TYPE,
						QTFieldConst.FILED_QT_EXISTING_CONTRACT,
						QTFieldConst.FILED_QT_PUBLISH_PERIOD,
						QTFieldConst.FILED_QT_VALIDITY_PERIOD}), 
						
		//QT_PENDING_SCHEDULE_REVISION				
		QT_PENDING_SCHEDULE_REVISION(
				QTStatusConst.QT_PENDING_SCHEDULE_REVISION, new String[] {
						QTFieldConst.FILED_QT_TITLE,
						QTFieldConst.FILED_QT_FILE_NO,
						QTFieldConst.FILED_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FILED_QT_EXISTING_CONTRACT,
						QTFieldConst.FILED_QT_PUBLISH_PERIOD,
						QTFieldConst.FILED_QT_VALIDITY_PERIOD,
						QTFieldConst.FILED_QT_CATEGORY_CODE,
						QTFieldConst.FILED_QT_LOCATION_COVERAGE,
						QTFieldConst.FILED_QT_SUPPQUALIFICATION,
						QTFieldConst.FILED_QT_BSVDETAILS,
						QTFieldConst.FILED_QT_PUBLISH_DATE,
						QTFieldConst.FILED_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FILED_QT_DOCUMENTS,
						QTFieldConst.FILED_QT_SCHDETAILS,
						QTFieldConst.FILED_QT_PUB_APPROVAL}),
				
		// QT_REVISION_PUBLICATION
		QT_REVISION_PUBLICATION(
				QTStatusConst.QT_REVISION_PUBLICATION, new String[] {
						QTFieldConst.FILED_QT_ESTMT_AMT,
						QTFieldConst.FILED_QT_TITLE,
						
						QTFieldConst.FILED_QT_CATEGORY_CODE,
						QTFieldConst.FILED_QT_LOCATION_COVERAGE,
						QTFieldConst.FILED_QT_SUPPQUALIFICATION,
						QTFieldConst.FILED_QT_BSVDETAILS,
						QTFieldConst.FILED_QT_PUBLISH_DATE,
						QTFieldConst.FILED_QT_SCHDETAILS,
						QTFieldConst.FILED_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FILED_QT_DOCUMENTS,
						QTFieldConst.FILED_QT_PUB_APPROVAL,
						QTFieldConst.FILED_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FILED_QT_PUBLISH_PERIOD,
						QTFieldConst.FILED_QT_VALIDITY_PERIOD}),
						
						// QT_PENDING_BPK_APPROVAL
						QT_PENDING_BPK_APPROVAL(
								QTStatusConst.QT_PENDING_BPK_APPROVAL, new String[] {
										QTFieldConst.FILED_QT_TITLE,
										QTFieldConst.FILED_QT_FILE_NO,
										QTFieldConst.FILED_QT_OPEN_FOR,
										QTFieldConst.FILED_QT_PROCUREMENT_TYPE_CAT_ID,
										QTFieldConst.FILED_QT_EXISTING_CONTRACT,
										QTFieldConst.FILED_QT_CATEGORY_CODE,
										QTFieldConst.FILED_QT_LOCATION_COVERAGE,
										QTFieldConst.FILED_QT_SUPPQUALIFICATION,
										QTFieldConst.FILED_QT_BSVDETAILS,
										QTFieldConst.FILED_QT_PUBLISH_DATE,
										QTFieldConst.FILED_QT_DEDUCTIONPAYMENT,
										QTFieldConst.FILED_QT_PUBLISH_PERIOD,
										QTFieldConst.FILED_QT_VALIDITY_PERIOD,
										QTFieldConst.FILED_QT_DOCUMENTS,
										QTFieldConst.FILED_QT_SCHDETAILS,
										QTFieldConst.FILED_QT_PUB_APPROVAL}),
										
						   // QT_PENDING_PB_APPROVAL both status have the same filed names			
		                  QT_PENDING_PB_APPROVAL(QTStatusConst.QT_PENDING_PB_APPROVAL,
				                                              QT_PENDING_BPK_APPROVAL.value() );

				                            
		
		private Long statusId;
		private String[] toBeEnableFields;

		private QTTaskListEnableFields(Long statusId, String[] tobeEnableFileds) {
			this.statusId = statusId;
			this.toBeEnableFields = tobeEnableFileds;

		}

		@Override
		public String[] value() {

			return toBeEnableFields;
		}

		public static String[] getValuesByStatusId(Long statusId) {
			for (QTTaskListEnableFields filedMapper : QTTaskListEnableFields
					.values()) {
				if (filedMapper.statusId.equals(statusId)) {
					return filedMapper.value();
				}
			}
			return null;
		}

	}

}
