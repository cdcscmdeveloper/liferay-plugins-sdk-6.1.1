package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class CompanyBean implements Serializable {




	private static final Logger logger = Logger
			.getLogger(CompanyBean.class.getName());

	private TreeNode root2;

	private TreeNode[] selectedNodes;

	public CompanyBean () {
		root2 = new DefaultTreeNode("Root", null);
		TreeNode node0 = new DefaultTreeNode("Group of Companies", root2);

		TreeNode node00 = new DefaultTreeNode("CDC", node0);
		
		TreeNode node000 = new DefaultTreeNode("Archisoft Consulting", node00);
		

	}

	public TreeNode getRoot2() {
		return root2;
	}

	public void setRoot2(TreeNode root2) {
		this.root2 = root2;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

}
