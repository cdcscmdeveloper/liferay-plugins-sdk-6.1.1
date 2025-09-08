package my.ep.web.backingbean.sc.requestnote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a commodity group.
 * This class is serializable.
 */
public class CommodityGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemName;
    private String itemCode;
    private String itemCodeName;
    private BigDecimal currentItemTotalAmount;
    private BigDecimal accumulativeAmountPerItemGroup;
    private String decision;

    private List<ScRequestNoteDtl> items;

    /**
     * Instantiates a new CommodityGroup.
     * Initializes fields to prevent NullPointerExceptions.
     */
    public CommodityGroup() {
        this.items = new ArrayList<>();
        this.currentItemTotalAmount = BigDecimal.ZERO;
        this.accumulativeAmountPerItemGroup = BigDecimal.ZERO;
        this.itemCode = ""; 
        this.itemName = ""; 
        this.itemCodeName = "";
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
        this.items = new ArrayList<>();
    }

    /**
     * Retrieves the item code.
     *
     * @return The item code as a String.
     */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * Sets the item code.
	 *
	 * @param itemCode The item code to set.
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * Retrieves the item name.
	 *
	 * @return The item name as a String.
	 */
    public String getItemName() {
		return itemName;
	}
    
    /**
     * Sets the item name.
     *
     * @param itemName The item name to set.
     */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Retrieves the concatenated item code and name.
	 *
	 * @return The item code and name as a String.
	 */
    public String getItemCodeName() {
        return itemCodeName;
    }

    /**
     * Sets the concatenated item code and name.
     *
     * @param itemCodeName The item code and name to set.
     */
    public void setItemCodeName(String itemCodeName) {
        this.itemCodeName = itemCodeName;
    }

    /**
     * Retrieves the current total amount for the item.
     *
     * @return The current item total amount as a BigDecimal.
     */
    public BigDecimal getCurrentItemTotalAmount() {
        return currentItemTotalAmount;
    }

    /**
     * Sets the current total amount for the item.
     *
     * @param currentItemTotalAmount The current item total amount to set.
     */
    public void setCurrentItemTotalAmount(BigDecimal currentItemTotalAmount) {
        this.currentItemTotalAmount = currentItemTotalAmount;
    }

    /**
     * Retrieves the accumulative amount for the item group.
     *
     * @return The accumulative amount per item group as a BigDecimal.
     */
    public BigDecimal getAccumulativeAmountPerItemGroup() {
        return accumulativeAmountPerItemGroup;
    }

    /**
     * Sets the accumulative amount for the item group.
     *
     * @param accumulativeAmountPerItemGroup The accumulative amount per item group to set.
     */
    public void setAccumulativeAmountPerItemGroup(BigDecimal accumulativeAmountPerItemGroup) {
        this.accumulativeAmountPerItemGroup = accumulativeAmountPerItemGroup;
    }
    
    /**
     * Retrieves the decision.
     *
     * @return The decision as a String.
     */
    public String getDecision() {
        return decision;
    }

    /**
     * Sets the decision.
     *
     * @param decision The decision to set.
     */
    public void setDecision(String decision) {
        this.decision = decision;
    }

    /**
     * Retrieves the list of items.
     *
     * @return A list of {@link ScRequestNoteDtl} objects.
     */
    public List<ScRequestNoteDtl> getItems() {
        return items;
    }

    /**
     * Sets the list of items.
     *
     * @param items The list of {@link ScRequestNoteDtl} objects to set.
     */
    public void setItems(List<ScRequestNoteDtl> items) {
        this.items = items;
    }

    /**
     * Adds a single item to the list of items.
     * If the item list is null, it initializes it as a new ArrayList.
     *
     * @param item The {@link ScRequestNoteDtl} item to add.
     */
    public void addItem(ScRequestNoteDtl item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }
}
