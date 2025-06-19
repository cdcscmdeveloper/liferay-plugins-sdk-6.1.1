package my.ep.web.backingbean.sc.requestnote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl; // Import your JPA entity
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CommodityGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemName;
    private String itemCode;
    private String itemCodeName; // Derived from ScRequestNoteDtl.requestItem.cmItem...
    private BigDecimal currentItemTotalAmount; // Sum of totalPricePerItemRounded for items in this group
    private BigDecimal accumulativeAmountPerItemGroup; // Could be a sum, or a target, or a budget value.

    private String decision; // For the "Decision" dropdown

    // This list will hold your actual JPA entities, ScRequestNoteDtl
    private List<ScRequestNoteDtl> items;

    /**
     * Instantiates a new CommodityGroup.
     * Initializes fields to prevent NullPointerExceptions.
     */
    public CommodityGroup() {
        this.items = new ArrayList<>(); // Initialize the list
        this.currentItemTotalAmount = BigDecimal.ZERO; // Initialize BigDecimal fields
        this.accumulativeAmountPerItemGroup = BigDecimal.ZERO;
        this.itemCode = ""; 
        this.itemName = ""; 
        this.itemCodeName = ""; // Initialize String fields (optional, but good practice)
        this.decision = "";
    }

    /**
     * Instantiates a new CommodityGroup with initial values.
     * @param itemCode the item code
     * @param itemName the item name
     * @param itemCodeName the item code name
     * @param currentItemTotalAmount the current item total amount
     * @param accumulativeAmountPerItemGroup the accumulative amount per item group
     * @param decision the decision
     */
    public CommodityGroup(String itemCode, String itemName, String itemCodeName, BigDecimal currentItemTotalAmount,
                          BigDecimal accumulativeAmountPerItemGroup, String decision) {
    	this.itemCode = itemCode;
    	this.itemName = itemName;
    	this.itemCodeName = itemCodeName;
        this.currentItemTotalAmount = currentItemTotalAmount;
        this.accumulativeAmountPerItemGroup = accumulativeAmountPerItemGroup;
        this.decision = decision;
        this.items = new ArrayList<>(); // Still initialize the list here
    }

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

    public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

    public String getItemCodeName() {
        return itemCodeName;
    }

    public void setItemCodeName(String itemCodeName) {
        this.itemCodeName = itemCodeName;
    }

    public BigDecimal getCurrentItemTotalAmount() {
        return currentItemTotalAmount;
    }

    public void setCurrentItemTotalAmount(BigDecimal currentItemTotalAmount) {
        this.currentItemTotalAmount = currentItemTotalAmount;
    }

    public BigDecimal getAccumulativeAmountPerItemGroup() {
        return accumulativeAmountPerItemGroup;
    }

    public void setAccumulativeAmountPerItemGroup(BigDecimal accumulativeAmountPerItemGroup) {
        this.accumulativeAmountPerItemGroup = accumulativeAmountPerItemGroup;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public List<ScRequestNoteDtl> getItems() {
        return items;
    }

    public void setItems(List<ScRequestNoteDtl> items) {
        this.items = items;
    }

    public void addItem(ScRequestNoteDtl item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }
}
