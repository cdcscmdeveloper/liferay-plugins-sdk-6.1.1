package my.ep.web.vo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;
/* PrimeFaces impact - kenny - 20130129
import com.google.common.collect.Iterators;*/





public class SegmentVO extends NameNode implements TreeNode{
	private List<FamiliesVO> families = new ArrayList<FamiliesVO>();
	
	
	
	 
    /**
	 * @return the families
	 */
	public List<FamiliesVO> getFamilies() {
		return families;
	}

	/**
	 * @param families the families to set
	 */
	public void setFamilies(List<FamiliesVO> families) {
		this.families = families;
	}

	public SegmentVO() {
        this.setType("segment");
    }
    
    public TreeNode getChildAt(int childIndex) {
        return families.get(childIndex);
    }
    
    
 
    public int getChildCount() {
        return families.size();
    }
 
    public TreeNode getParent() {
        return null;
    }
    
    public int getIndex(TreeNode node) {
        return families.indexOf(node);
    }
 
    public boolean getAllowsChildren() {
        return true;
    }
 
    public boolean isLeaf() {
        return families.isEmpty();
    }
 
    public Enumeration<FamiliesVO> children() {
    	/* PrimeFaces impact - kenny - 20130129
        return Iterators.asEnumeration(families.iterator());*/
    	return null;
    }
	
	

}