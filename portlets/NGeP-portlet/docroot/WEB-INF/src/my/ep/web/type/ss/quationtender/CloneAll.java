/**
 * 
 */
package my.ep.web.type.ss.quationtender;

/**
 * @author SudheerKumar
 * 
 */
public enum CloneAll {
	
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

	};
	public abstract String getValue();

	public abstract String getDisplayName();

}
