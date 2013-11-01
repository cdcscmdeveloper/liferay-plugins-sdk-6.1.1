/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;
import my.ep.type.pp.ProcurementMode;

/**
 * @author Vidyasagar
 * 
 */
public enum SSProcurmentMode implements IEnumType {

	QUOTATION('Q'), TENDER('T'), DIRECTPURCHASE('D');

	private char mode;

	SSProcurmentMode(char pmode) {
		this.mode = pmode;
	}

	@Override
	public Character value() {
		// TODO Auto-generated method stub
		return mode;
	}

	public char getMode() {
		return mode;
	}

	public void setMode(char mode) {
		this.mode = mode;
	}
}
