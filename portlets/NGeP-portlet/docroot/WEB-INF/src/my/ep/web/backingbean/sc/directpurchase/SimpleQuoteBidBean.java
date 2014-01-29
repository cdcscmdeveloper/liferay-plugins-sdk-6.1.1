package my.ep.web.backingbean.sc.directpurchase;

public class SimpleQuoteBidBean {

	private int seqNo;
	private String supplier;
	private String description;
	private int qtyOrder;
	private String uom;
	private float unitPerPrice;
	private int amount;

	public SimpleQuoteBidBean() {
	}

	public SimpleQuoteBidBean(int seqNo, String supplier, String description,
			int qtyOrder, String uom, float unitPerPrice, int amount) {
		this.seqNo = seqNo;
		this.supplier = supplier;
		this.description = description;
		this.qtyOrder = qtyOrder;
        this.uom = uom;
        this.unitPerPrice = unitPerPrice;
        this.amount = amount;


	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQtyOrder() {
		return qtyOrder;
	}

	public void setQtyOrder(int qtyOrder) {
		this.qtyOrder = qtyOrder;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public float getUnitPerPrice() {
		return unitPerPrice;
	}

	public void setUnitPerPrice(float unitPerPrice) {
		this.unitPerPrice = unitPerPrice;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	

}
