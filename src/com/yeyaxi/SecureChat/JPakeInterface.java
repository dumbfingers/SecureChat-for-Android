/**
 * 
 */
package com.yeyaxi.SecureChat;

import java.math.BigInteger;

/**
 * JPakeInterface
 * @author Yaxi Ye
 * @version 1
 * @since 20 Jul 2011, BST 2200
 *
 */
public interface JPakeInterface {
	/**
	 * GetPassWord - Get Passwd to proceed the JPake process.
	 * @param pwd
	 * @return String of PassWord that set by user.
	 * @throws IllegalArgumentException
	 */
	String GetPassWord (String pwd) throws IllegalArgumentException;
	
	/**
	 * GetSignerId - Get SignerId from User's phone number
	 * @param signerId
	 * @return
	 * @throws Exception
	 */
	String GetSignerId (String signerId) throws Exception;
	
	/**
	 * init - Initialize 2 BigIntegers
	 * @return For sender, return x1 and x2; for receiver, return x3 and x4; For both side, return p, q and g.
	 */
	BigInteger init ();
	
	/**
	 * step1 - JPake Step1
	 * @param p
	 * @param x1 For sender, it should be selected from the range of [0, pow(2, (bitLength q))-1], for receiver, it should be selected from the range of [1, pow(2, (bitLength q)) -1
	 * @param x2 Random BigInteger
	 * @param signerId
	 * @return BigInteger gx, and BigInteger s from String signerId
	 */
	BigInteger step1 (BigInteger p, BigInteger x1, BigInteger x2, String signerId);
	
	/**
	 * generateZKP - Generate the "Zero Knowledge Proof"(ZKP)
	 * @param p
	 * @param q
	 * @param g
	 * @param gx
	 * @param x
	 * @param signerId
	 * @return BigInteger sig[]
	 */
	BigInteger[] generateZKP (BigInteger p, BigInteger q, BigInteger g, BigInteger gx, BigInteger x, String signerId);
	
	/**
	 * verifyZKP - Verify the ZKP
	 * @param p
	 * @param q
	 * @param g
	 * @param gx
	 * @param x
	 * @param sig
	 * @param signerId
	 * @return success or not
	 */
	boolean verifyZKP (BigInteger p, BigInteger q, BigInteger g, BigInteger gx, BigInteger x, BigInteger[] sig, String signerId);
	
	/**
	 * step2 - Step2 of JPAKE
	 * @param p
	 * @param q
	 * @param gx1
	 * @param gx3
	 * @param gx4
	 * @param x2
	 * @param signerId_sender
	 * @param sig - ZKP from the receiver
	 * @return calculated A (from sender) or B (for receiver)
	 */
	BigInteger step2 (BigInteger p, BigInteger q, BigInteger gx1, BigInteger gx3, BigInteger gx4, BigInteger x2, BigInteger signerId_sender, BigInteger[] sig);
	
	/**
	 * sessionKey - For computing the session key.
	 * @param gx4 - received from receiver
	 * @param x2
	 * @param signerId_sender - sender's signerID in the form of BigInteger
	 * @param p
	 * @param q
	 * @param B - sender received from receiver via Step2
	 * @return sessionKey
	 */
	String sessionKey (BigInteger gx4, BigInteger x2, BigInteger signerId_sender, BigInteger p, BigInteger q, BigInteger B);
	

}
