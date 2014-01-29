/**
 * 
 */
package my.ep.web.bean.tree;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author VidyasagarReddy
 *
 */
public class EpDefaultTreeNode extends DefaultTreeNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EpDefaultTreeNode(){
		super();
	}
	
	public EpDefaultTreeNode(Object data, TreeNode parent) {
		super(data,parent);
	}
	
	public EpDefaultTreeNode(String type, Object data, TreeNode parent) {
		super(type,data,parent);
	}
	

	public boolean isLeaf() {
		return false;
	}
}
