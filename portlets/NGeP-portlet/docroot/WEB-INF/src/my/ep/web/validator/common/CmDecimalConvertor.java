package my.ep.web.validator.common;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import my.ep.constant.MessageConst;
import my.ep.util.MessageUtils;
import my.ep.web.util.FacesScopes;

/**
 * Throw custom converter error for integer and decimal
 */
@FacesConverter("cmNumberConverter")
public class CmDecimalConvertor extends javax.faces.convert.NumberConverter {

    public static final String DECIMAL = "DECIMAL";
    public static final String DECIMAL_PATTERN = "#,##0.00";
    public static final String INTEGER_PATTERN = "#,##0";

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String numberType = (String) comp.getAttributes().get("numberType");
        String submitValue = value.replace(",", "").trim();

        try {
            return DECIMAL.equals(numberType) ? new BigDecimal(submitValue) : Integer.valueOf(submitValue);
        } catch (NumberFormatException e) {
            throw createConverterException(numberType, submitValue);
        }
    }

    private ConverterException createConverterException(String numberType, String value) {
        String messageCode = DECIMAL.equals(numberType) ? MessageConst.ERROR_716027 : MessageConst.ERROR_716028;

        return new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
        		messageCode, new String[] { value },
        		MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Object value) {
        String numberType = (String) comp.getAttributes().get("numberType");

        if (value == null) {
            return "";
        }

        if (DECIMAL.equals(numberType)) {
            setPattern(DECIMAL_PATTERN);
        } else {
            setPattern(INTEGER_PATTERN);
        }

        return super.getAsString(ctx, comp, value);
    }
}
