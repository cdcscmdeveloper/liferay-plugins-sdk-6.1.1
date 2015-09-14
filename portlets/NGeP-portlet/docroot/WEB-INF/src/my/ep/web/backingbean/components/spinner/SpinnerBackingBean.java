package my.ep.web.backingbean.components.spinner;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

/**
 * Backing Bean For Contract Details
 * 
 * @author Venkat Chanumolu
 * @since 10/09/2015
 */
@ManagedBean(name = "spinnerBackingBean")
@RequestScoped
public class SpinnerBackingBean {

	private static Logger log = Logger.getLogger(SpinnerBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	public void incrementValue(BigDecimal value, BigDecimal stepfactor,String methodExpression) {
		debugLogger("incrementValue");
		value = value.add(stepfactor);

	}

	public void decrementValue(BigDecimal value, BigDecimal stepfactor,String methodExpression) {
		debugLogger("decrementValue");
		value = value.subtract(stepfactor);
	}

}
