/**
 * 
 */
package my.ep.web.backingbean.trackingdiary;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import org.apache.log4j.Logger;

/**
 * @author 
 * 
 */
public abstract class TrackingDiaryBaseBackingBean<T extends BaseManagedBean>
		extends BaseBackingBean<T> {

	public static Logger log = EpLogger
			.getLogger(TrackingDiaryBaseBackingBean.class);

	private String codeType;

	private int previousScreen = 0;//0=catSearch;1=catMaintenance;
	
	

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

}