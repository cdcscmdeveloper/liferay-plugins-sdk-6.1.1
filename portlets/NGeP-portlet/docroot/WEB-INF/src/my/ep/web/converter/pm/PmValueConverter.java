package my.ep.web.converter.pm;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.NumberConverter;
import javax.faces.convert.FacesConverter;
import my.ep.constants.CommonConstants;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.web.backingbean.pm.settings.RuleCatalogueBackingBean;
import my.ep.web.util.CommonUtils;

import org.apache.log4j.Logger;

/**
 * Throw custom error
 */
@FacesConverter(value = "pmValueConverter")
public class PmValueConverter extends NumberConverter {
	private static Logger log = Logger.getLogger(PmValueConverter.class);
	private String errorDetail = "";

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		setPattern(new CommonUtils().getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
		try {
			return super.getAsObject(arg0, arg1, arg2);
			
		} catch (ConverterException e) {
			if (checkForRenewTypesAndZone(arg0, arg2)) {
				FacesMessage fac =  e.getFacesMessage();
				fac.setDetail(errorDetail);
				throw new ConverterException(fac, e);
			} 
			throw e;
		}
	}
	
	private boolean checkForRenewTypesAndZone(FacesContext con, String arg2) {
		try {
			RuleCatalogueBackingBean bBean = con.getApplication().evaluateExpressionGet(con, 
					"#{ruleCatalogueBackingBean}", RuleCatalogueBackingBean.class);
			PmConfigRule rule = bBean.getManagedBean().getSelectedRuleDescType().getRuleType();
			PmConfigRuleValue newVal = rule.getConfigRuleValues().get(rule.getConfigRuleValues().size()-1);
			
			String ruleDesc = bBean.getManagedBean().getSelectedRuleDescType().getRuleDesc();
			String zone = bBean.getZoneName(newVal.getZoneId());
			errorDetail = bBean.getLabel("lbl.pm.settings.value.number.zone", 
					new String[]{arg2, ruleDesc, zone});
			return bBean.isRenewTypes(rule.getRuleType()) && !zone.isEmpty();
			
		} catch (Exception e) {
			log.error(e);
		}
		return false;
	}	
}