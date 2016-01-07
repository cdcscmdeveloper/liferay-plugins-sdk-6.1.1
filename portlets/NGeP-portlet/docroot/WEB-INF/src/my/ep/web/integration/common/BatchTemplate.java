package my.ep.web.integration.common;

/*
 *@author P Ganga prasad
 */
public abstract class BatchTemplate extends InitializeSO {

	// template method, final so subclasses can't override
	public final void gfmasInboundServices() {
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

	public final void gfmasSmOutboundService() {
		masterDataVendorGfmasOutbound();
	}

	public final void gfmasSmInboundService() {
		masterDataVendorGfmasInbound();
	}

	public final void gfmasScInboundService() {
		sstInformationGfmas();
	}

	public final void gfmasFlInboundService() {
		paymentGfmasInbound();
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

	public abstract void masterDataVendorGfmasOutbound();

	public abstract void masterDataVendorGfmasInbound();

	public abstract void sstInformationGfmas();

	public abstract void paymentGfmasInbound();
	
	public abstract void receiptInformationGfmasOutbound();

}
