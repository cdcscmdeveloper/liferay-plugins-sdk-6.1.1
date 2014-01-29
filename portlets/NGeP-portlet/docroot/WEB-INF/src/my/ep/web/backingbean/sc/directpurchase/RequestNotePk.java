package my.ep.web.backingbean.sc.directpurchase;

import java.io.Serializable;

import my.ep.persistence.entity.sm.SmSupplier;

public class RequestNotePk implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7392411609413713945L;
	private String fulfillmentType;
	private SmSupplier supplier;
	private	String	deliverPeriodType;
	private	Double	deliveryPeriodValue;

	public String getFulfillmentType() {
		return fulfillmentType;
	}

	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}
	
	public SmSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}
	
	public String getDeliverPeriodType() {
		return deliverPeriodType;
	}

	public void setDeliverPeriodType(String deliverPeriodType) {
		this.deliverPeriodType = deliverPeriodType;
	}

	public Double getDeliveryPeriodValue() {
		return deliveryPeriodValue;
	}

	public void setDeliveryPeriodValue(Double deliveryPeriodValue) {
		this.deliveryPeriodValue = deliveryPeriodValue;
	}

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

	public int hashCode() {
		int result;
		result = this.getFulfillmentType().hashCode();
		result = 31 * result + (new Long(this.getSupplier().getSupplierId())).hashCode();
		result = 31 * result + this.getDeliverPeriodType().hashCode();
		result = 31 * result + this.getDeliveryPeriodValue().hashCode();
		return result;
	}
}
