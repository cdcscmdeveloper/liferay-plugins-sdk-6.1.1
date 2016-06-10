package my.ep.web.common.passwordcipher;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.util.PasswordUtils;

@ManagedBean(name = "passwordCipherBackingBean")
@RequestScoped
public class PasswordCipherBackingBean {
	
	public String userText;
	public String encryptedText;
	public String decryptedText;
	
	public void decodeUserText() {
		if(StringUtils.isNotBlank(this.getUserText())) {
			this.setEncryptedText(PasswordUtils.encrypt(this.getUserText()));
			this.setDecryptedText(PasswordUtils.decrypt(this.getEncryptedText()));
		}
	}
	
	public String getUserText() {
		return userText;
	}

	public void setUserText(String userText) {
		this.userText = userText;
	}

	public String getEncryptedText() {
		return encryptedText;
	}

	public void setEncryptedText(String encryptedText) {
		this.encryptedText = encryptedText;
	}

	public String getDecryptedText() {
		return decryptedText;
	}

	public void setDecryptedText(String decryptedText) {
		this.decryptedText = decryptedText;
	}
	
}
