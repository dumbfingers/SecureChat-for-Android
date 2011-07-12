package com.yeyaxi.SecureChat;

import javax.crypto.Cipher;
import java.io.IOException;

public interface AESInterface {
	/**
	 * AESEncrypt
	 * @author Yaxi Ye 
	 * @param SecretKey
	 * @param PlainMsg
	 * @return CypherText with Base64 Encoded
	 * @throws Exception
	 */
	String AESEnrypt(String SecretKey, String PlainMsg) throws Exception;
	/**
	 * 
	 * @param SecretKey
	 * @param EncryptMsg
	 * @return Decrypted PlainText
	 * @throws Exception
	 */
	String AESDecrypt(String SecretKey, String EncryptMsg) throws Exception;
}
