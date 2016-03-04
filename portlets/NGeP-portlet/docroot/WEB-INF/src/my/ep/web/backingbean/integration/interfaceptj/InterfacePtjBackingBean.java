package my.ep.web.backingbean.integration.interfaceptj;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.integration.PmInterfacePtj;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.intergration.interfaceptj.InterfacePtjManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "interfacePtjBackingBean")
@RequestScoped
public class InterfacePtjBackingBean extends
BaseBackingBean<InterfacePtjManagedBean> {

	public static Logger log = Logger.getLogger(InterfacePtjBackingBean.class);



	public void cloneItemSelection(int i){

		this.getManagedBean().setRefreshFlag(true);
		int index = i%10;
		System.out.println("*******getCloneLs*************"+this.getManagedBean().getCloneLs().size());


		PmInterfacePtj ptj =  this.getManagedBean().getSearchDataModel().getList().get(index);
		System.out.println("getInterfacePtjId"+ptj.getInterfacePtjId());




		if(this.getManagedBean().getCloneLs()!=null && this.getManagedBean().getCloneLs().size()>0){

			boolean addInLs = true;
			for(PmInterfacePtj obj : this.getManagedBean().getCloneLs()){

				if(obj.getInterfacePtjId() == ptj.getInterfacePtjId() && !ptj.isSelected()){
					this.getManagedBean().getCloneLs().remove(obj);
					addInLs = false;
					break;
				}



			}
			System.out.println("addInLs:"+addInLs);
			if(addInLs){
				this.getManagedBean().getCloneLs().add(ptj);
			}

		}else{
			this.getManagedBean().getCloneLs().add(ptj);
		}

		System.out.println("*******size*************"+this.getManagedBean().getCloneLs().size());




		int count =0;
		Map tempMap = new HashMap();
		for (int j = 0; j < this.getManagedBean().getSearchDataModel().getList().size(); j++) {
			PmInterfacePtj suppItemJ =  this.getManagedBean().getSearchDataModel().getList().get(j);

			if(this.getManagedBean().getCloneLs()!=null && this.getManagedBean().getCloneLs().size()>0){

				for(PmInterfacePtj obj : this.getManagedBean().getCloneLs()){

					if(suppItemJ.getInterfacePtjId()==obj.getInterfacePtjId()){

						if(!tempMap.containsKey(obj.getInterfacePtjId())){
							count = count + 1;
							System.out.println("**loop***"+count);
							tempMap.put(obj.getInterfacePtjId(), obj.getInterfacePtjId());
						}
					}
				}

			}

		}
		if(count == this.getManagedBean().getSearchDataModel().getList().size()){
			this.getManagedBean().setCheckBoxAll(true);
		}else{
			this.getManagedBean().setCheckBoxAll(false);
		}
		System.out.println("isCheckBoxAll:"+this.getManagedBean().isCheckBoxAll());
	}
	
	public void cloneAllSelection(){
		this.getManagedBean().setRefreshFlag(true);
		Map checkDuplicateAdd = new HashMap();
		for (int i = 0; i < this.getManagedBean().getSearchDataModel().getList().size(); i++) {



			PmInterfacePtj ptj = this.getManagedBean().getSearchDataModel().getList().get(i);


			if(this.getManagedBean().isCheckBoxAll()){
				ptj.setSelected(true);

			}else{
				ptj.setSelected(false);

			}


			if(this.getManagedBean().getCloneLs()!=null && this.getManagedBean().getCloneLs().size()>0){

				boolean addInLs = true;
				for(PmInterfacePtj obj : this.getManagedBean().getCloneLs()){

					if(!checkDuplicateAdd.containsKey(obj.getInterfacePtjId())){
						checkDuplicateAdd.put(obj.getInterfacePtjId(), obj.getInterfacePtjId());
					}

					if(!ptj.isSelected() && ptj.getInterfacePtjId()==obj.getInterfacePtjId()){
						this.getManagedBean().getCloneLs().remove(obj);
						addInLs = false;
						break;
					}

				}
				System.out.println("addInLs:"+addInLs);
				if(addInLs){
					System.out.println("checkDuplicateAdd:"+checkDuplicateAdd);
					if(!checkDuplicateAdd.containsKey(ptj.getInterfacePtjId())){
						this.getManagedBean().getCloneLs().add(ptj);
					}
				}

			}else{

				if(ptj.isSelected()){
					if(!checkDuplicateAdd.containsKey(ptj.getInterfacePtjId())){
						this.getManagedBean().getCloneLs().add(ptj);
					}
				}
			}

			System.out.println("*******size*************"+this.getManagedBean().getCloneLs().size());

		}

	}
	
	
	public void checkingCheckAll(){

		System.out.println("****final****"+this.getManagedBean().isCheckBoxAll());



		int count =0;
		Map tempMap = new HashMap();
		for (int i = 0; i < this.getManagedBean().getSearchDataModel().getList().size(); i++) {
			PmInterfacePtj ptj = this.getManagedBean().getSearchDataModel().getList().get(i);

			if(this.getManagedBean().getCloneLs()!=null && this.getManagedBean().getCloneLs().size()>0){

				for(PmInterfacePtj obj : this.getManagedBean().getCloneLs()){
					//if((String.valueOf(objects[4])+String.valueOf(objects[5])+String.valueOf(objects[6])+String.valueOf(objects[7])).equalsIgnoreCase(obj.getKey())) {
					if(ptj.getInterfacePtjId()==obj.getInterfacePtjId()){
						if(!tempMap.containsKey(obj.getInterfacePtjId())){
							ptj.setSelected(true);
							count = count + 1;
							System.out.println("**loop***"+count);
							tempMap.put(obj.getInterfacePtjId(), obj.getInterfacePtjId());
						}
					}
				}

			}

		}
		System.out.println("count:"+count);
		System.out.println("resDTO.getSearchDataModel().getResultList().size():"+ this.getManagedBean().getSearchDataModel().getList().size());
		if(count ==  this.getManagedBean().getSearchDataModel().getList().size()){
			this.getManagedBean().setCheckBoxAll(true);
		}else{
			this.getManagedBean().setCheckBoxAll(false);
		}


		System.out.println("****last****"+this.getManagedBean().isCheckBoxAll());
	}
	
	
	public void preRenderInterfacePtj() {
		System.out.println("********preRenderInterfacePtj************");
		this.getManagedBean().setRefreshFlag(false);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "ptjGfmasInboundView");
	}
	


}
