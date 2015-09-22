package my.ep.web.integration.common;

/*
*@author P Ganga prasad
*/
public abstract class BatchTemplate extends InitializeSO{
	
	//template method, final so subclasses can't override
    public final void gfmasInboundServices(){
    	System.out.println("inside template method...");
    	pejabatPerakaunanGfmasInbound();
		pegawaiPengawalGfmasInbound();
		kumpulanPtjGfmasInbound();
        votGfmasInbound();
		votDanaGfmasInbound();
		programAktivitiGfmasInbound();
		projectGfmasInbound();
		kodAkaunGfmasInbound();
		masterDataPtjGfmasInbound();
		kodBankGfmasInbound();	
    }
    
    public abstract void pejabatPerakaunanGfmasInbound();
    public abstract void pegawaiPengawalGfmasInbound();
    public abstract void kumpulanPtjGfmasInbound();
    public abstract void votDanaGfmasInbound();
    public abstract void votGfmasInbound();
    public abstract void programAktivitiGfmasInbound();	
    public abstract void projectGfmasInbound();
    public abstract void kodAkaunGfmasInbound();
    public abstract void masterDataPtjGfmasInbound();
    public abstract void kodBankGfmasInbound();

}
