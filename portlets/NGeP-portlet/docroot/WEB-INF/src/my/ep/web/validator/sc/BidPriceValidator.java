package my.ep.web.validator.sc;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import my.ep.constant.MessageConst;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
/**
 * 
 * @author Venkat Chanumolu
 * 
 */
@FacesValidator("my.ep.web.validator.sc.BidPriceValidator")
public class BidPriceValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object obj)
			throws ValidatorException {
		BigDecimal newPrice = (BigDecimal)obj ;
		ScBidPrice scBidPrice = (ScBidPrice)component.getAttributes().get("bidPrice");
		if (scBidPrice.getNewPrice() != null && scBidPrice.getNewPrice().doubleValue() != 0.0d) {
			if (scBidPrice.getNewPrice().doubleValue() < scBidPrice
					.getScBidSupplierItem().getScBidItem().getDecrementAmt()
					.doubleValue()) {

				FacesMessage msg = new FacesMessage(MessageConst.ERROR_760301);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			} else if (scBidPrice.getNewPrice().doubleValue() > scBidPrice
					.getScBidSupplierItem().getScBidItem().getDecrementAmt()
					.doubleValue()
					&& (scBidPrice.getNewPrice().doubleValue() % scBidPrice
							.getScBidSupplierItem().getScBidItem()
							.getDecrementAmt().doubleValue()) != 0.0d) {
				FacesMessage msg = new FacesMessage(MessageConst.ERROR_760301);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			} else {
				BigDecimal lastBidPrice = scBidPrice.getBidPrice() == null ? scBidPrice
						.getScBidSupplierItem().getItemStartPrice()
						: scBidPrice.getBidPrice();
				if (scBidPrice.getNewPrice().doubleValue() > lastBidPrice
						.doubleValue()) {
					FacesMessage msg = new FacesMessage(MessageConst.ERROR_760302);
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException(msg);
				}
			}
		}
	}

}
