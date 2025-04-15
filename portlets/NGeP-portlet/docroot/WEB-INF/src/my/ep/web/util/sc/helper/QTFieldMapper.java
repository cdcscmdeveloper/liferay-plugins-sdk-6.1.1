/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.type.IEnumType;

/**
 * 
 * QTFieldMapper class is used to enable or disable fields
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public class QTFieldMapper {

	/**
	 * To enable fields by statusid and maintance flow
	 * 
	 * @param statusId
	 * @param isMaintanceFlow
	 * @return List<String> fieldNames
	 */
	public static List<String> getEnableFieldsByStatus(Long statusId,
			boolean isMaintanceFlow, boolean isEditable) {
		List<String> updatedFieldsList = null;
		String[] fields = null;
		if (isMaintanceFlow) {
			if (isEditable) {
				fields = QTMaintanaceEnableFields.getValuesByStatusId(statusId);
			}
		} else {
			fields = QTTaskListEnableFields.getValuesByStatusId(statusId);
		}
		if (null != fields) {
			updatedFieldsList = Arrays.asList(fields);
		}
		return updatedFieldsList;

	}

	/**
	 * Constant for Maintance Flow Fields
	 * 
	 * Add the field names which needs to be editable on the screen upon QT status
	 */
	enum QTMaintanaceEnableFields implements IEnumType {
		
		// Pending QT Submission
		QT_PENDING_QT_SUBMISSION(
				QTStatusConst.QT_PENDING_QT_SUBMISSION,
				new String[] { QTFieldConst.FIELD_QT_TITLE,
						QTFieldConst.FIELD_QT_FILE_NO,
						QTFieldConst.FIELD_QT_ESTMT_AMT,
						QTFieldConst.FIELD_QT_GOSODO,
						QTFieldConst.FIELD_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FIELD_QT_CATEGORY_CODE,
						QTFieldConst.FIELD_QT_SUPPLIER_LIST,
						QTFieldConst.FIELD_QT_QT_TYPE,
						QTFieldConst.FIELD_QT_OPEN_FOR,
						QTFieldConst.FIELD_QT_PROCUREMENT_CATEGORY,
						QTFieldConst.FIELD_QT_EVALUATION_TYPE,
						QTFieldConst.FIELD_QT_CONTRACT_TYPE,
						QTFieldConst.FIELD_QT_FULFILMENT_TYPE,
						QTFieldConst.FIELD_QT_ZONE_COVERAGE,
						QTFieldConst.FIELD_QT_EXISTING_CONTRACT,
						QTFieldConst.FIELD_QT_REQUIRED_MINIS_SPEC_APPROVAL,
						QTFieldConst.FIELD_QT_CONTRACT_DURATION,
						QTFieldConst.FIELD_QT_OBTAIN_SPEC_APPROVAL}), 
				
		// QT_PENDING_APPROVALS_CHECKLIST_SCORING
		QT_PENDING_APPROVALS_CHECKLIST_SCORING(
				QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING,
				new String[] { QTFieldConst.FIELD_QT_TITLE,
						QTFieldConst.FIELD_QT_FILE_NO,
						QTFieldConst.FIELD_QT_QT_TYPE,
						QTFieldConst.FIELD_QT_OPEN_FOR,
						QTFieldConst.FIELD_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FIELD_QT_PROCUREMENT_CATEGORY,
						QTFieldConst.FIELD_QT_EXISTING_CONTRACT,
						QTFieldConst.FIELD_QT_ZONE_COVERAGE,
						QTFieldConst.FIELD_QT_EVALUATION_TYPE,
						QTFieldConst.FIELD_QT_FULFILMENT_TYPE,
						QTFieldConst.FIELD_QT_REQUIRED_MINIS_SPEC_APPROVAL,
						QTFieldConst.FIELD_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FIELD_QT_DOCUMENTS}),
						
		// QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT		
		QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT,
				new String[] { QTFieldConst.FIELD_QT_TITLE,
				QTFieldConst.FIELD_QT_FILE_NO,
				QTFieldConst.FIELD_QT_QT_TYPE,
				QTFieldConst.FIELD_QT_OPEN_FOR,
				QTFieldConst.FIELD_QT_PROCUREMENT_TYPE_CAT_ID,
				QTFieldConst.FIELD_QT_EXISTING_CONTRACT,
				QTFieldConst.FIELD_QT_EVALUATION_TYPE,
				QTFieldConst.FIELD_QT_FULFILMENT_TYPE,
				QTFieldConst.FIELD_QT_DEDUCTIONPAYMENT,
				QTFieldConst.FIELD_QT_DOCUMENTS}),
							      
		// QT_PENDING_CHECKLIST_SCORING_SIGN_OFF				         
		QT_PENDING_CHECKLIST_SCORING_SIGN_OFF(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF,
				QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT.value()),
										      
		// QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH				         
		QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH( QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH,
				QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT.value()),

		// QT_PENDING_MSC_APPROVAL
		QT_PENDING_MSC_APPROVAL( QTStatusConst.QT_PENDING_MSC_APPROVAL,
				QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT.value()),
									      
		//QT_PENDING_CHECKLIST_SCORING_REVISION						         
		QT_PENDING_CHECKLIST_SCORING_REVISION(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION,
				QT_PENDING_APPROVALS_CHECKLIST_SCORING.value()),		
				
		// QT_PENDING_PB_APPROVAL
		QT_PENDING_PB_APPROVAL(
				QTStatusConst.QT_PENDING_PB_APPROVAL, 
				new String[] { QTFieldConst.FIELD_QT_DOCUMENTS }),
				
		// QT_PENDING_BPK_APPROVAL
		// 25037 - start
		QT_PENDING_BPK_APPROVAL(QTStatusConst.QT_PENDING_BPK_APPROVAL, merge(new String[] {
				QTFieldConst.FIELD_QT_DESK_OFFICER_ASSISTANT}, QT_PENDING_PB_APPROVAL.value())), 
		// 25037 - end
		
        // Pending QT publication Approval
		QT_PENDING_PUBLICATION_APPROVAL(
				QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL, 
				new String[] {
						QTFieldConst.FIELD_QT_TITLE,
						QTFieldConst.FIELD_QT_CATEGORY_CODE,
						QTFieldConst.FIELD_QT_LOCATION_COVERAGE,
						QTFieldConst.FIELD_QT_SUPPQUALIFICATION,
						QTFieldConst.FIELD_QT_BSVDETAILS,
						QTFieldConst.FIELD_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FIELD_QT_DOCUMENTS,
						QTFieldConst.FIELD_QT_DESK_OFFICER_ASSISTANT }),// 25037	

						
		// QT_PENDING_PUBLICATION_REVISION_APPROVAL
		QT_PENDING_PUBLICATION_REVISION_APPROVAL(
				QTStatusConst.QT_PENDING_PUBLICATION_REVISION_APPROVAL, 
				QT_PENDING_PUBLICATION_APPROVAL.value()),
						
        // Pending QT Publication
		QT_PENDING_PUBLICATION(QTStatusConst.QT_PENDING_PUBLICATION,
				new String[] { 
						QTFieldConst.FIELD_QT_TITLE,
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD,
						QTFieldConst.FIELD_QT_PUBLISH_PERIOD,
						QTFieldConst.FIELD_QT_BSVDETAILS,
						QTFieldConst.FIELD_QT_DESK_OFFICER_ASSISTANT, // 25037	
						QTFieldConst.FIELD_QT_DOCUMENTS }),
						
		/*// QT_PENDING_SPEC_CHNAGES_ENDORSEMENT
		QT_PENDING_SPEC_CHNAGES_ENDORSEMENT(
				QTStatusConst.QT_PENDING_SPEC_CHNAGES_ENDORSEMENT,
				new String[] { QTFieldConst.FIELD_QT_PUBLISH_DATE,
						QTFieldConst.FIELD_QT_BSVDETAILS,
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD,
						QTFieldConst.FIELD_QT_PUBLISH_PERIOD }),*/
		
		// QT_PENDING_RESCHEDULE_PUBLICATION
		QT_PENDING_RESCHEDULE_PUBLICATION(QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION,
				new String[] { 
						QTFieldConst.FIELD_QT_PUBLISH_DATE,
						QTFieldConst.FIELD_QT_BSVDETAILS,
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD,
						QTFieldConst.FIELD_QT_PUBLISH_PERIOD }),
						
		// Briefing/Site visit
		QT_BRIEFING_SITE_VISIT(QTStatusConst.QT_BRIEFING_SITE_VISIT,
				new String[] { 
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD,
						QTFieldConst.FIELD_QT_PUBLISH_PERIOD,
						QTFieldConst.FIELD_QT_BSVDETAILS,
						QTFieldConst.FIELD_QT_DESK_OFFICER_ASSISTANT}), // 25037	
						
        //Proposal Preparation 
		QT_PROPOSAL_PREPARATION(QTStatusConst.QT_PROPOSAL_PREPARATION,
				new String[] { 
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD,
						QTFieldConst.FIELD_QT_PUBLISH_PERIOD}),
					
		// QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE
		QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE(QTStatusConst.QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE,
				new String[] { 
						QTFieldConst.FIELD_QT_CLOSING_DATE,
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD }),
						
		// QT Closed				
		QT_CLOSED(QTStatusConst.QT_CLOSED,
				new String[] { QTFieldConst.FIELD_QT_VALIDITY_PERIOD }),

		// Evaluation Proposal		
		QT_EVALUATION_PROPOSAL(QTStatusConst.QT_EVALUATION_PROPOSAL,
				QT_CLOSED.value() ),
        // Re Evaluation
		QT_RE_EVALUATION(QTStatusConst.QT_RE_EVALUATION, 
				QT_CLOSED.value()),
			
		// Pending Consolidation Report
		QT_CONSOLIDATION_REPORT(QTStatusConst.QT_CONSOLIDATION_REPORT,
						QT_CLOSED.value() ),

		// #28834 -start-
		// Pending Recommendation (Revise Briefing Paper)
		QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER(QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER,
				QT_CLOSED.value()),
		// #28834 -end-

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
				String[] tobeEnableFields) {
			this.statusId = statusId;
			this.toBeEnableFields = tobeEnableFields;

		}

		/**
		 * Get Field Values
		 * 
		 * @return the array of values.
		 */
		@Override
		public String[] value() {

			return toBeEnableFields;
		}
		/**
		 * Get Field Values by statusId.
		 * @param statusId
		 * @return the array of values.
		 */
		public static String[] getValuesByStatusId(Long statusId) {
			for (QTMaintanaceEnableFields fieldMapper : QTMaintanaceEnableFields
					.values()) {
				if (fieldMapper.statusId.equals(statusId)) {
					return fieldMapper.value();
				}
			}
			return null;
		}

		// 25037 - start
		private static String[] merge(String[] arr1, String[] arr2) {// #25037&&11104
			List<String> merged = new ArrayList<String>(Arrays.asList(arr1));
			merged.addAll(Arrays.asList(arr2));
			String[] mergedArr = new String[merged.size()];
			for (int i = 0; i < mergedArr.length; i++) {
				mergedArr[i] = merged.get(i);
			}
			return mergedArr;
		}
		// 25037 - end


	}

	/**
	 * Constant for TaskList Fields
	 */
	enum QTTaskListEnableFields implements IEnumType {
		// QT_PENDING_QT_SUBMISSION
		QT_PENDING_QT_SUBMISSION(QTStatusConst.QT_PENDING_QT_SUBMISSION,
				new String[] { QTFieldConst.FIELD_QT_TITLE,
						QTFieldConst.FIELD_QT_FILE_NO,
						QTFieldConst.FIELD_QT_ESTMT_AMT,
						QTFieldConst.FIELD_QT_GOSODO,
						QTFieldConst.FIELD_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FIELD_QT_CATEGORY_CODE,
						QTFieldConst.FIELD_QT_SUPPLIER_LIST,
						QTFieldConst.FIELD_QT_QT_TYPE,
						QTFieldConst.FIELD_QT_OPEN_FOR,
						QTFieldConst.FIELD_QT_PROCUREMENT_CATEGORY,
						QTFieldConst.FIELD_QT_EVALUATION_TYPE,
						QTFieldConst.FIELD_QT_CONTRACT_TYPE,
						QTFieldConst.FIELD_QT_FULFILMENT_TYPE,
						QTFieldConst.FIELD_QT_CONTRACT_DURATION,
						QTFieldConst.FIELD_QT_ZONE_COVERAGE,
						QTFieldConst.FIELD_QT_EXISTING_CONTRACT,
						QTFieldConst.FIELD_QT_REQUIRED_MINIS_SPEC_APPROVAL,
						QTFieldConst.FIELD_QT_OBTAIN_SPEC_APPROVAL}),

		// QT_PENDING_APPROVALS_CHECKLIST_SCORING
		QT_PENDING_APPROVALS_CHECKLIST_SCORING(
				QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING,

				new String[] {
						QTFieldConst.FIELD_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FIELD_QT_CATEGORY_CODE,
						QTFieldConst.FIELD_QT_SUPPLIER_LIST,
						QTFieldConst.FIELD_QT_PROCUREMENT_CATEGORY,
						QTFieldConst.FIELD_QT_EVALUATION_TYPE,
						QTFieldConst.FIELD_QT_FULFILMENT_TYPE,
						QTFieldConst.FIELD_QT_CONTRACT_DURATION,
						QTFieldConst.FIELD_QT_ZONE_COVERAGE,
						QTFieldConst.FIELD_QT_EXISTING_CONTRACT,
						QTFieldConst.FIELD_QT_QT_TYPE,
						QTFieldConst.FIELD_QT_OPEN_FOR,
						QTFieldConst.FIELD_QT_OBTAIN_SPEC_APPROVAL}), 
		
		// QT_PENDING_APPROVALS_CHECKLIST_SCORING_REVISION
		QT_PENDING_APPROVALS_CHECKLIST_SCORING_REVISION(
				QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION,

				QT_PENDING_APPROVALS_CHECKLIST_SCORING.value()), 
				
		// QT_PENDING_SCHEDULE_DOCUMENT
		QT_PENDING_SCHEDULE_DOCUMENT(
				QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, new String[] {
						QTFieldConst.FIELD_QT_TITLE,
						QTFieldConst.FIELD_QT_BSVDETAILS,
						QTFieldConst.FIELD_QT_PUBLISH_DATE,
						QTFieldConst.FIELD_QT_SCHDETAILS,
						QTFieldConst.FIELD_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FIELD_QT_DOCUMENTS,
						QTFieldConst.FIELD_QT_PUB_APPROVAL,
						QTFieldConst.FIELD_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FIELD_QT_MINIMUM_PAID_UP_CAPITAL,
						QTFieldConst.FIELD_QT_CATEGORY_CODE,
						QTFieldConst.FIELD_QT_SUPPLIER_LIST,
						QTFieldConst.FIELD_QT_LOCATION_COVERAGE,
						QTFieldConst.FIELD_QT_SUPPQUALIFICATION,
						QTFieldConst.FIELD_QT_QT_TYPE,
						QTFieldConst.FIELD_QT_OPEN_FOR,
						QTFieldConst.FIELD_QT_EVALUATION_TYPE,
						QTFieldConst.FIELD_QT_EXISTING_CONTRACT,
						QTFieldConst.FIELD_QT_PUBLISH_PERIOD,
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD,
						QTFieldConst.FIELD_QT_FILE_NO,
						QTFieldConst.FIELD_QT_OBTAIN_SPEC_APPROVAL,
						QTFieldConst.FIELD_QT_DESK_OFFICER_ASSISTANT}),// 25037
						
		//QT_PENDING_SCHEDULE_REVISION				
		QT_PENDING_SCHEDULE_REVISION(
				QTStatusConst.QT_PENDING_SCHEDULE_REVISION, 
				// #14398 - Start.
				QT_PENDING_SCHEDULE_DOCUMENT.value()
				// #14398 - End.
				),
				
		// QT_REVISION_PUBLICATION
		QT_REVISION_PUBLICATION(
				QTStatusConst.QT_REVISION_PUBLICATION, new String[] {
						QTFieldConst.FIELD_QT_ESTMT_AMT,
						QTFieldConst.FIELD_QT_TITLE,
						
						QTFieldConst.FIELD_QT_CATEGORY_CODE,
						QTFieldConst.FIELD_QT_LOCATION_COVERAGE,
						QTFieldConst.FIELD_QT_SUPPQUALIFICATION,
						QTFieldConst.FIELD_QT_BSVDETAILS,
						QTFieldConst.FIELD_QT_PUBLISH_DATE,
						QTFieldConst.FIELD_QT_SCHDETAILS,
						QTFieldConst.FIELD_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FIELD_QT_DOCUMENTS,
						QTFieldConst.FIELD_QT_PUB_APPROVAL,
						QTFieldConst.FIELD_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FIELD_QT_PUBLISH_PERIOD,
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD}),
						
		// QT_PENDING_BPK_APPROVAL
		QT_PENDING_BPK_APPROVAL(
				QTStatusConst.QT_PENDING_BPK_APPROVAL, new String[] {
						QTFieldConst.FIELD_QT_TITLE,
						QTFieldConst.FIELD_QT_FILE_NO,
						QTFieldConst.FIELD_QT_OPEN_FOR,
						QTFieldConst.FIELD_QT_PROCUREMENT_TYPE_CAT_ID,
						QTFieldConst.FIELD_QT_EXISTING_CONTRACT,
						QTFieldConst.FIELD_QT_CATEGORY_CODE,
						QTFieldConst.FIELD_QT_SUPPLIER_LIST,
						QTFieldConst.FIELD_QT_LOCATION_COVERAGE,
						QTFieldConst.FIELD_QT_SUPPQUALIFICATION,
						QTFieldConst.FIELD_QT_BSVDETAILS,
						QTFieldConst.FIELD_QT_PUBLISH_DATE,
						QTFieldConst.FIELD_QT_DEDUCTIONPAYMENT,
						QTFieldConst.FIELD_QT_PUBLISH_PERIOD,
						QTFieldConst.FIELD_QT_VALIDITY_PERIOD,
						QTFieldConst.FIELD_QT_DOCUMENTS,
						QTFieldConst.FIELD_QT_SCHDETAILS,
						QTFieldConst.FIELD_QT_PUB_APPROVAL}),
										
		// QT_PENDING_PB_APPROVAL both status have the same field names			
		QT_PENDING_PB_APPROVAL(
				QTStatusConst.QT_PENDING_PB_APPROVAL,
		            	QT_PENDING_BPK_APPROVAL.value() );

				                            
		
		private Long statusId;
		private String[] toBeEnableFields;

		private QTTaskListEnableFields(Long statusId, String[] tobeEnableFields) {
			this.statusId = statusId;
			this.toBeEnableFields = tobeEnableFields;

		}

		/**
		 * Get Field Values 
		 * 
		 * @return the array of values.
		 */
		@Override
		public String[] value() {

			return toBeEnableFields;
		}
         
		/**
		 * Get Field Values by statusId.
		 * @param statusId
		 * @return the array of values.
		 */
		public static String[] getValuesByStatusId(Long statusId) {
			for (QTTaskListEnableFields fieldMapper : QTTaskListEnableFields
					.values()) {
				if (fieldMapper.statusId.equals(statusId)) {
					return fieldMapper.value();
				}
			}
			return null;
		}

	}

}
