/**
 * 
 */
package my.ep.web.sc.quationtender.common;

/**
 * @author Vidyasagar
 * 
 */
public enum RFP {

	YES(1), NO(2);
	private int code;

	private RFP(int code) {
		this.code = code;
	}

	private int getCode() {
		return code;
	}

	public static int getValue(RFP mode) {
		for (RFP code : values()) {
			if (code == mode) {
				return code.getCode();
			}
		}
		return 0;
	}
}
