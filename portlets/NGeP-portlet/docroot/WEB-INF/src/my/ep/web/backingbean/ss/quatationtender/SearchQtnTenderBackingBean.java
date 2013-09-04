package my.ep.web.backingbean.ss.quatationtender;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.ss.quationtender.SearchQtnTenderManagedBean;

@ManagedBean(name="searchQtnTenderBB")
@RequestScoped
public class SearchQtnTenderBackingBean extends BaseBackingBean<SearchQtnTenderManagedBean>{

	private boolean checked;

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
	}

