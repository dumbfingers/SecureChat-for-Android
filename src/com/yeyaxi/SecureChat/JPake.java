package com.yeyaxi.SecureChat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
/**
 * JPake - For JPake implementation on Android
 * Inspired by JPAKEDemo.java written by Dr. Feng Hao and the J-PAKE model
 * http://grouper.ieee.org/groups/1363/Research/contributions/hao-ryan-2008.pdf
 * 
 * @author Yaxi Ye
 *
 */
public class JPake {
	BigInteger p = new BigInteger("C196BA05AC29E1F9C3C72D56DFFC6154A033F1477AC88EC37F09BE6C5BB95F51C296DD20D1A28A067CCC4D4316A4BD1DCA55ED1066D438C35AEBAABF57E7DAE428782A95ECA1C143DB701FD48533A3C18F0FE23557EA7AE619ECACC7E0B51652A8776D02A425567DED36EABD90CA33A1E8D988F0BBB92D02D1D20290113BB562CE1FC856EEB7CDD92D33EEA6F410859B179E7E789A8F75F645FAE2E136D252BFFAFF89528945C1ABE705A38DBC2D364AADE99BE0D0AAD82E5320121496DC65B3930E38047294FF877831A16D5228418DE8AB275D7D75651CEFED65F78AFC3EA7FE4D79B35F62A0402A1117599ADAC7B269A59F353CF450E6982D3B1702D9CA83", 16);
	BigInteger q = new BigInteger("90EAF4D1AF0708B1B612FF35E0A2997EB9E9D263C9CE659528945C0D", 16);
	BigInteger g = new BigInteger("A59A749A11242C58C894E9E5A91804E8FA0AC64B56288F8D47D51B1EDC4D65444FECA0111D78F35FC9FDD4CB1F1B79A3BA9CBEE83A3F811012503C8117F98E5048B089E387AF6949BF8784EBD9EF45876F2E6A5A495BE64B6E770409494B7FEE1DBB1E4B2BC2A53D4F893D418B7159592E4FFFDF6969E91D770DAEBD0B5CB14C00AD68EC7DC1E5745EA55C706C4A1C5C88964E34D09DEB753AD418C1AD0F4FDFD049A955E5D78491C0B7A2F1575A008CCD727AB376DB6E695515B05BD412F5B8C2F4C77EE10DA48ABD53F5DD498927EE7B692BBBCDA2FB23A516C5B4533D73980B2A3B60E384ED200AE21B40D273651AD6060C13D97FD69AA13C5611A51B9085", 16);
	ArrayList step1Result = new ArrayList();
	ArrayList step2Result = new ArrayList();
	
	public BigInteger GetPassWord(String pwd) throws IllegalArgumentException {
		BigInteger bn_pwd = new BigInteger(pwd.getBytes());
		return bn_pwd;
	}

	public String GetSignerId(){
		//Get Signer's ID from IMEI
		JPakeActivity jpake = new JPakeActivity();
		String signerId = jpake.getUID();
		return signerId;
	}

	public void step1(String signerId) throws Exception {
		BigInteger x1;
		BigInteger x2;
		//Generate x1 in the range of [0,q-1]
		do {
			x1 = new BigInteger (160, new SecureRandom());
		}
		while (x1.compareTo(BigInteger.ZERO) >= 0 && 
				x1.compareTo(q.subtract(BigInteger.ONE)) <= 0); 
		
		//Generate x2 in the range of [1,q-1]
		do {
			x2 = new BigInteger(160, new SecureRandom());
		} 
		while (x2.compareTo(BigInteger.ONE) >= 0 && 
				x2.compareTo(q.subtract(BigInteger.ONE)) <= 0);
		
		BigInteger gx1 = g.modPow(x1, p);
		BigInteger gx2 = g.modPow(x2, p);
		
		BigInteger[] sigX1 = generateZKP(p,q,g,gx1,x1,GetSignerId());
		BigInteger[] sigX2 = generateZKP(p,q,g,gx2,x2,GetSignerId());
		
		//return gx1, sigX1, gx2, sigX2
		step1Result.add(0,gx1);
		step1Result.add(1,sigX1);
		step1Result.add(2,gx2);
		step1Result.add(3,sigX2);
		step1Result.add(4,x2);
	}

	public BigInteger[] generateZKP(BigInteger p, BigInteger q, BigInteger g,
			BigInteger gx, BigInteger x, String signerId) throws Exception {
		signerId = GetSignerId();
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

	public void step2(BigInteger gx1, BigInteger gx3, BigInteger gx4, BigInteger x2, BigInteger pwd) throws Exception {
		BigInteger gA = gx1.multiply(gx3).multiply(gx4).mod(p);
		//pwd is the shared passwd
		BigInteger A = gA.modPow(x2.multiply(pwd).mod(q), p);
		BigInteger[] sigX2s = generateZKP(p,q,gA,A,x2.multiply(pwd).mod(q),GetSignerId());
		
		//return gA, A, sigX2s
		step2Result.add(0,gA);
		step2Result.add(1,A);
		step2Result.add(2,sigX2s);
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
