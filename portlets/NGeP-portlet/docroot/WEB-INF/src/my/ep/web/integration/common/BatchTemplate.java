package my.ep.web.integration.common;

import my.ep.web.integration.common.InitializeSO;

/*
 *@author P Ganga prasad
 */
public abstract class BatchTemplate extends InitializeSO {

	// template method, final so subclasses can't override
	public final void gfmasInboundServices(String fileName) {
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
	
	// to put messages into queue
	public final void gfmasInboundServices()  {
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

	public abstract void pejabatPerakaunanGfmasInbound(String fileName,String transId);

	public abstract void pegawaiPengawalGfmasInbound(String fileName,String transId);

	public abstract void kumpulanPtjGfmasInbound(String fileName,String transId);

	public abstract void votDanaGfmasInbound(String fileName,String transId);

	public abstract void votGfmasInbound(String fileName,String transId);

	public abstract void programAktivitiGfmasInbound(String fileName,String transId);

	public abstract void projectGfmasInbound(String fileName,String transId);

	public abstract void kodAkaunGfmasInbound(String fileName,String transId);

	public abstract void masterDataPtjGfmasInbound(String fileName,String transId);

	public abstract void kodBankGfmasInbound(String fileName,String transId);
	
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

	public void masterDataVendorGfmasInbound(String fileName,String transId) {
	}

	public void paymentGfmasInbound(String fileName,String transId) {
	} 

}
