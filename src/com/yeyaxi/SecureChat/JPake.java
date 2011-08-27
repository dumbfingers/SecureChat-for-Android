package com.yeyaxi.SecureChat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import android.app.Activity;
/**
 * JPake - For JPake implementation on Android
 * Inspired by JPAKEDemo.java written by Dr. Feng Hao and the J-PAKE model
 * @see http://grouper.ieee.org/groups/1363/Research/contributions/hao-ryan-2008.pdf
 * 
 * @author Yaxi Ye
 *
 */
public class JPake extends Activity{
	BigInteger p = new BigInteger(Constants.BigInteger_P, 16);
	BigInteger q = new BigInteger(Constants.BigInteger_Q, 16);
	BigInteger g = new BigInteger(Constants.BigInteger_G, 16);
	ArrayList<String> step1Result = new ArrayList<String> ();
	ArrayList<String> step2Result = new ArrayList<String> ();
	
	//Below this line are the implementation of JPake
	public BigInteger GetPassWord(String pwd) throws IllegalArgumentException {
		BigInteger bn_pwd = new BigInteger(pwd.getBytes());
		return bn_pwd;
	}

	//public String GetSignerId(){
		//Get Signer's ID from IMEI
		//JPakeActivity jpake = new JPakeActivity();
		//String signerId = jpake.getUID();
		//return signerId;
	//}

	public void step1(String signerId) throws Exception {
		BigInteger x1;
		BigInteger x2;
		//Generate x1 in the range of [0,q-1]
		do {
			x1 = new BigInteger (160, new SecureRandom());
		}
		while (x1.compareTo(BigInteger.ZERO) < 0 || 
				x1.compareTo(q.subtract(BigInteger.ONE)) > 0); 
		
		//Generate x2 in the range of [1,q-1]
		do {
			x2 = new BigInteger(160, new SecureRandom());
		} 
		while (x2.compareTo(BigInteger.ONE) < 0 || 
				x2.compareTo(q.subtract(BigInteger.ONE)) > 0);
		
		BigInteger gx1 = g.modPow(x1, p);
		BigInteger gx2 = g.modPow(x2, p);
		
		BigInteger[] sigX1 = generateZKP(p,q,g,gx1,x1, signerId);
		BigInteger[] sigX2 = generateZKP(p,q,g,gx2,x2, signerId);
		
		//return gx1, sigX1, gx2, sigX2
		step1Result.add(0, gx1.toString(16));
		step1Result.add(1, sigX1[0].toString(16));
		step1Result.add(2, sigX1[1].toString(16));
		step1Result.add(3, gx2.toString(16));
		step1Result.add(4, sigX2[0].toString(16));
		step1Result.add(5, sigX2[1].toString(16));
		step1Result.add(6,x2.toString(16));
	}

	public BigInteger[] generateZKP(BigInteger p, BigInteger q, BigInteger g,
			BigInteger gx, BigInteger x, String signerId) throws Exception {
		//signerId = GetSignerId();
		BigInteger [] ZKP = new BigInteger [2];
		BigInteger v = new BigInteger(160, new SecureRandom());
		BigInteger gv = g.modPow(v, p);
		BigInteger h = getHash(g,gv,gx,signerId);
		
		ZKP[0] = gv;
		ZKP[1] = v.subtract(x.multiply(h)).mod(q);
		
		return ZKP;
	}

	public boolean verifyZKP(BigInteger p, BigInteger q, BigInteger g,
			BigInteger gx, BigInteger[] sig, String signerId) throws NoSuchAlgorithmException {
		BigInteger h = getHash(g, sig[0], gx, signerId);
		if (gx.compareTo(p.subtract(BigInteger.ZERO)) == 1 && 
				gx.compareTo(p.subtract(BigInteger.ONE)) == -1 && 
				gx.modPow(q, p).compareTo(BigInteger.ONE) == 0 && 
				g.modPow(sig[1], p).multiply(gx.modPow(h, p)).mod(p).compareTo(sig[0]) == 0)
			return true;
		else
			return false;
	}
	
	public BigInteger getHash(BigInteger g, BigInteger gr, BigInteger gx, String signerId) throws NoSuchAlgorithmException {
		
		MessageDigest hash = null;
		hash = MessageDigest.getInstance("SHA256");
		hash.update(g.toByteArray());
		hash.update(gr.toByteArray());
		hash.update(gx.toByteArray());
		hash.update(signerId.getBytes());
		return new BigInteger(hash.digest());
		
	}
	
	public BigInteger getHash(BigInteger k) throws NoSuchAlgorithmException {
		MessageDigest hash = null;
		hash.getInstance("SHA256");
		hash.update(k.toByteArray());
		return new BigInteger(1, hash.digest());
	}
	
	/**
	 * step2 - Step 2 of JPake
	 * @param gx1 is derived from the sender's step1
	 * @param gx3 is gx1 of receiver's. Derived from the receiver's step1
	 * @param gx4 is gx2 of receiver's. Derived from the receiver's step1
	 * @param x2 is derived from the sender's step1
	 * @param pwd is derived from the EditText of the sender's.
	 * @throws Exception
	 */

	public void step2(BigInteger gx1, BigInteger gx3, BigInteger gx4, BigInteger x2, BigInteger pwd, String signerId) throws Exception {
		BigInteger gA = gx1.multiply(gx3).multiply(gx4).mod(p);
		//pwd is the shared passwd
		BigInteger A = gA.modPow(x2.multiply(pwd).mod(q), p);
		BigInteger[] sigX2s = generateZKP(p, q, gA, A, x2.multiply(pwd).mod(q), signerId);
		
		//return gA, A, sigX2s
		step2Result.add(0, gA.toString(16));
		step2Result.add(1, A.toString(16));
		step2Result.add(2, sigX2s[0].toString(16));
		step2Result.add(3, sigX2s[1].toString(16));
	}

	public String sessionKey(BigInteger gx4, BigInteger x2, BigInteger p, BigInteger q, BigInteger B, BigInteger pwd) throws NoSuchAlgorithmException {
		BigInteger k = getHash(gx4.modPow(x2.multiply(pwd).negate().mod(q), p).multiply(B).modPow(x2, p));
		return new String(k.toString(16)) ;
	}
	
	//build for multiple return in Step2
	/*
	class step2Result {
		private BigInteger gA;
		private BigInteger A;
		private BigInteger[] sigX2s;
		
		public void setgA(BigInteger gA) {
			this.gA = gA;
		}
		public BigInteger getgA() {
			return gA;
		}
		public void setA(BigInteger A) {
			this.A = A;
		}
		public BigInteger getA() {
			return A;
		}
		public void setsigX2s(BigInteger[] sigX2s) {
			this.sigX2s = sigX2s;
		}
		public BigInteger[] getsigX2s() {
			return sigX2s;
		}
	}
	*/
	
}
