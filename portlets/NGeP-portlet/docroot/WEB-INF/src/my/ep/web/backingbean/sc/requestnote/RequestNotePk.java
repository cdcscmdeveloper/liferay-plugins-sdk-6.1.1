package my.ep.web.backingbean.sc.requestnote;

import java.io.Serializable;

import my.ep.persistence.entity.sm.SmSupplier;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestNotePk.
 */
public class RequestNotePk implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7392411609413713945L;
	
	/** The fulfillment type. */
	private String fulfillmentType;
	
	/** The supplier. */
	private SmSupplier supplier;
	
	/** The deliver period type. */
	private	String	deliverPeriodType;
	
	/** The delivery period value. */
	private	Double	deliveryPeriodValue;

	/**
	 * Gets the fulfillment type.
	 *
	 * @return the fulfillment type
	 */
	public String getFulfillmentType() {
		return fulfillmentType;
	}

	/**
	 * Sets the fulfillment type.
	 *
	 * @param fulfillmentType the new fulfillment type
	 */
	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}
	
	/**
	 * Gets the supplier.
	 *
	 * @return the supplier
	 */
	public SmSupplier getSupplier() {
		return supplier;
	}

	/**
	 * Sets the supplier.
	 *
	 * @param supplier the new supplier
	 */
	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}
	
	/**
	 * Gets the deliver period type.
	 *
	 * @return the deliver period type
	 */
	public String getDeliverPeriodType() {
		return deliverPeriodType;
	}

	/**
	 * Sets the deliver period type.
	 *
	 * @param deliverPeriodType the new deliver period type
	 */
	public void setDeliverPeriodType(String deliverPeriodType) {
		this.deliverPeriodType = deliverPeriodType;
	}

	/**
	 * Gets the delivery period value.
	 *
	 * @return the delivery period value
	 */
	public Double getDeliveryPeriodValue() {
		return deliveryPeriodValue;
	}

	/**
	 * Sets the delivery period value.
	 *
	 * @param deliveryPeriodValue the new delivery period value
	 */
	public void setDeliveryPeriodValue(Double deliveryPeriodValue) {
		this.deliveryPeriodValue = deliveryPeriodValue;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (this == other) {
			return true;
		}
		if (!(other instanceof RequestNotePk)) {
			return false;
		}

		final RequestNotePk requestNotePk = (RequestNotePk) other;

		return this.getFulfillmentType().equals(requestNotePk.getFulfillmentType()) && (this.getSupplier().getSupplierId() == requestNotePk.getSupplier().getSupplierId())
				&& this.getDeliverPeriodType().equals(requestNotePk.getDeliverPeriodType()) && this.getDeliveryPeriodValue().equals(requestNotePk.getDeliveryPeriodValue());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result;
		result = this.getFulfillmentType().hashCode();
		result = 31 * result + (new Long(this.getSupplier().getSupplierId())).hashCode();
		result = 31 * result + this.getDeliverPeriodType().hashCode();
		result = 31 * result + this.getDeliveryPeriodValue().hashCode();
		return result;
	}
}
