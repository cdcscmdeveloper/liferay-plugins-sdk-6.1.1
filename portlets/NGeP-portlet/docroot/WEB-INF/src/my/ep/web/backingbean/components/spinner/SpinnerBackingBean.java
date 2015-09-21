package my.ep.web.backingbean.components.spinner;

import java.math.BigDecimal;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang3.StringUtils;
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

	/**
	 * Get the new spin value based on step factor value.
	 * 
	 * @param component
	 * 		the UIComponent instance
	 * @param value
	 * 		the current spin value
	 * @param stepFactor
	 * 		the step factor value
	 * @param min
	 * 		the minimum value
	 * @param max
	 * 		the maximum value
	 * @param postUpdateListener
	 * 		the post update listener string
	 * @return	the new spin value
	 */
	public BigDecimal getSpinValue(UIComponent component, Object value, Object stepFactor, Object min, Object max, String postUpdateListener) {
		debugLogger("getSpinValue");
		
		BigDecimal newValue;
		BigDecimal stepFactorValue = convertObjectToBigDecimal(stepFactor);
		BigDecimal minValue = convertObjectToBigDecimal(min);
		BigDecimal maxValue = convertObjectToBigDecimal(max);
		UIComponent spinnerInput = (UIComponent)component.findComponent("spinnerInput");
		
		if (value == null || !(value instanceof BigDecimal)) {
			newValue = BigDecimal.valueOf(0);
		} else {
			newValue = (BigDecimal)value;
		}
		
		if (stepFactorValue != null) {
			newValue = newValue.add(stepFactorValue);
		}
		
		if (minValue != null && newValue.compareTo(minValue) < 0) {
			newValue = minValue;
		}
		
		if (maxValue != null && newValue.compareTo(maxValue) > 0) {
			newValue = maxValue;
		}
		
		invokeMethodAction(postUpdateListener, spinnerInput, newValue);

		return newValue;
	}

	/**
	 * Value Changed Action fired during blur event on spinner input text.
	 * 
	 * @param component
	 * 		the UIComponent instance
	 * @param value
	 * 		the current spin value
	 * @param stepFactor
	 * 		the step factor value
	 * @param min
	 * 		the minimum value
	 * @param max
	 * 		the maximum value
	 * @param postUpdateListener
	 * 		the post update listener string
	 */
	public void valueChangedAction(UIComponent component, Object value, Object stepFactor, Object min, Object max, String postUpdateListener) {
		debugLogger("valueChangedAction");
		
		UIComponent spinnerInput = (UIComponent)component.findComponent("spinnerInput");
		BigDecimal currentValue = convertObjectToBigDecimal(value);
		
		if (currentValue != null && stepFactor != null) {
			BigDecimal stepFactorValue = convertObjectToBigDecimal(stepFactor);
			BigDecimal minValue = convertObjectToBigDecimal(min);
			BigDecimal maxValue = convertObjectToBigDecimal(max);
			
			if (stepFactorValue.compareTo(BigDecimal.valueOf(0)) > 0) {
				// check must be greater or equal to min value.
				if (minValue != null) {
					if (currentValue.compareTo(minValue) < 0) {
						throwExcepion(MessageConst.ERROR_760301, spinnerInput);
					}
				}
				
				// check must be less than or equal to max value.
				if (maxValue != null) {
					if (currentValue.compareTo(maxValue) > 0) {
						throwExcepion(MessageConst.ERROR_760302, spinnerInput);
					}
				}
				
				// check value must be multiple by step factor.
				if (currentValue.remainder(stepFactorValue)
						.compareTo(BigDecimal.valueOf(0)) != 0) {
					throwExcepion(MessageConst.ERROR_760301, spinnerInput);
				}
			}
		}
		
		invokeMethodAction(postUpdateListener, spinnerInput, currentValue);
	}
	
	private void invokeMethodAction(String postUpdateListener, UIComponent component, BigDecimal newValue) {
		if (StringUtils.isNotBlank(postUpdateListener)) {
			MethodExpression methodExp = FacesScopes
	                .createMethodExpression(FacesContext.getCurrentInstance(),
	                		postUpdateListener, null,
	                        new Class[] { UIComponent.class, Object.class });
			
			methodExp.invoke(
					FacesContext.getCurrentInstance().getELContext(), 
					new Object[]{component, newValue});
		}
	}
	
	private void throwExcepion(String msgId, UIComponent spinnerInput) {
		spinnerInput.getValueExpression("value").setValue(
				FacesContext.getCurrentInstance().getELContext(), null);
		
		throw new EpException(msgId);
	}
	
	private BigDecimal convertObjectToBigDecimal(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				return (BigDecimal)value;
			} else {
				return new BigDecimal(value.toString());
			}
		}
		
		return null;
	}
}
