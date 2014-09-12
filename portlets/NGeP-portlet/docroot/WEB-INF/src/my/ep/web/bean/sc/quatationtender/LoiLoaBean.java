package my.ep.web.bean.sc.quatationtender;

import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoi;

public class LoiLoaBean {
	private ScLoi scLoi ;
	private ScLoa scLoa ;
	private String loiLoaType;
	private String loiLoaStatus;
	
	/**
	 * @return the scLoi
	 */
	public ScLoi getScLoi() {
		return scLoi;
	}
	/**
	 * @param scLoi the scLoi to set
	 */
	public void setScLoi(ScLoi scLoi) {
		this.scLoi = scLoi;
	}
	/**
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}
	/**
	 * @param scLoa the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}
	/**
	 * @return the loiLoaType
	 */
	public String getLoiLoaType() {
		return loiLoaType;
	}
	/**
	 * @param loiLoaType the loiLoaType to set
	 */
	public void setLoiLoaType(String loiLoaType) {
		this.loiLoaType = loiLoaType;
	}
	/**
	 * @return the loiLoaStatus
	 */
	public String getLoiLoaStatus() {
		return loiLoaStatus;
	}
	/**
	 * @param loiLoaStatus the loiLoaStatus to set
	 */
	public void setLoiLoaStatus(String loiLoaStatus) {
		this.loiLoaStatus = loiLoaStatus;
	}
}
