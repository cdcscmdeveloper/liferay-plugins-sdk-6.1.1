/**
 * 
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * @author SudheerKumar
 * 
 */
public enum CloneAll{
	
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
		
	
	public abstract String getValue();

	public abstract String getDisplayName();

}
