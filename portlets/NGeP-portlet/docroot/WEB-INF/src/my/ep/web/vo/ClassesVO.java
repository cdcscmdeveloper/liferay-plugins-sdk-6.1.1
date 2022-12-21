package my.ep.web.vo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;
/* PrimeFaces impact - kenny - 20130129
import com.google.common.collect.Iterators;*/




public class ClassesVO extends NameNode implements TreeNode {
	
	private List<CommoditiesVO> commodities = new ArrayList<CommoditiesVO>();
	private FamiliesVO families;
	
	
	public ClassesVO() {
		this.setType("class");
	}

	public TreeNode getChildAt(int childIndex) {
		return commodities.get(childIndex);
	}

	public int getChildCount() {
		return commodities.size();
	}

	public TreeNode getParent() {
		return families;
	}

	public void setParent(FamiliesVO families) {
		this.families = families;
	}

	public int getIndex(TreeNode node) {
		return commodities.indexOf(node);
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return commodities.isEmpty();
	}

	public Enumeration children() {
		/* PrimeFaces impact - kenny - 20130129
		return Iterators.asEnumeration(commodities.iterator());*/
		return null;
	}

	
	

	/**
	 * @return the families
	 */
	public FamiliesVO getFamilies() {
		return families;
	}

	/**
	 * @param families the families to set
	 */
	public void setFamilies(FamiliesVO families) {
		this.families = families;
	}

	/**
	 * @return the commodities
	 */
	public List<CommoditiesVO> getCommodities() {
		return commodities;
	}

	/**
	 * @param commodities the commodities to set
	 */
	public void setCommodities(List<CommoditiesVO> commodities) {
		this.commodities = commodities;
	}
	
	
	
	
	
	
	
}