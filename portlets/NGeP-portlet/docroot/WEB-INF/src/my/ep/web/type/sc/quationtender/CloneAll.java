/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.type.sc.quationtender;


// TODO: Auto-generated Javadoc
/**
 * 
 * Enum class for CloneAll.
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public enum CloneAll{
	
	/** The Qtn tender title. */
	QtnTenderTitle {
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return "QTT";
		}

		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return "Qtn/Tender Title";
		}

		
	},
	
	/** The Qtn tender details. */
	QtnTenderDetails {

		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return "QTD";
		}

		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return "Qtn/Tender Details";
		}

	},
	
	/** The Approval workflow. */
	ApprovalWorkflow{

		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return "APW";
		}

		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return "Approval Workflow";
		}
		
	},
	
	/** The Supplier qualification criteria. */
	SupplierQualificationCriteria{

		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return "SQC";
		}

		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return "Supplier Qualification Criteria";
		}
	
	},
	
	/** The Schedule. */
	Schedule{

		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return "SCH";
		}

		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return "Schedule";
		}
		
	},
	
	/** The Documents. */
	Documents{

		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return "DOC";
		}

		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return "Documents";
		}
		
	};
		
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public abstract String getValue();

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public abstract String getDisplayName();

}
