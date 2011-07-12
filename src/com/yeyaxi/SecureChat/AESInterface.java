package com.yeyaxi.SecureChat;
/**
 * AES Interface
 * @author Yaxi Ye
 *
 */
public interface AESInterface {
	/**
	 * AESEncrypt		
	 * @param SecretKey	-	The SecretKey for user to custom.
	 * @param PlainMsg	-	The Plain message user needs to encrypt and send.
	 * @return CypherText with Base64 Encoded
	 * @throws Exception
	 */
	String AESEncrypt(String sKey, String PlainMsg) throws Exception;
	/**
	 * AESDecrypt
	 * @param SecretKey	-	The SecretKey for validating user
	 * @param EncryptMsg	-	The cipher message needs to be decrypted
	 * @return Decrypted PlainText
	 * @throws Exception
	 */
	String AESDecrypt(String sKey, String EncryptMsg) throws Exception;
	/**
	 * getRawKey
	 * @param seed	-	The Secret user entered, which will be utilized to generate an AES key.
	 * @return a raw key
	 * @throws Exception
	 */
	byte[] getRawKey(byte[] seed) throws Exception;

}
