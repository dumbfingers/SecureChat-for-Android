package com.yeyaxi.SecureChat;

import javax.crypto.Cipher;
import java.io.IOException;
/**
 * AES Interface
 * @author Yaxi Ye
 *
 */
public interface AESInterface {
	/**
	 * 		
	 * @param SecretKey	-	The SecretKey for user to custom.
	 * @param PlainMsg	-	The Plain message user needs to encrypt and send.
	 * @return CypherText with Base64 Encoded
	 * @throws Exception
	 */
	String AESEncrypt(String SecretKey, String PlainMsg) throws Exception;
	/**
	 * 
	 * @param SecretKey	-	The SecretKey for validating user
	 * @param EncryptMsg	-	The cipher message needs to be decrypted
	 * @return Decrypted PlainText
	 * @throws Exception
	 */
	String AESDecrypt(String SecretKey, String EncryptMsg) throws Exception;
}
