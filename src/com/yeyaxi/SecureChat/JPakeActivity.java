/**
 * 
 */
package com.yeyaxi.SecureChat;

import java.math.BigInteger;

/**
 * @author Yaxi Ye
 *
 */
public class JPakeActivity implements JPakeInterface {
	BigInteger p = new BigInteger("C196BA05AC29E1F9C3C72D56DFFC6154A033F1477AC88EC37F09BE6C5BB95F51C296DD20D1A28A067CCC4D4316A4BD1DCA55ED1066D438C35AEBAABF57E7DAE428782A95ECA1C143DB701FD48533A3C18F0FE23557EA7AE619ECACC7E0B51652A8776D02A425567DED36EABD90CA33A1E8D988F0BBB92D02D1D20290113BB562CE1FC856EEB7CDD92D33EEA6F410859B179E7E789A8F75F645FAE2E136D252BFFAFF89528945C1ABE705A38DBC2D364AADE99BE0D0AAD82E5320121496DC65B3930E38047294FF877831A16D5228418DE8AB275D7D75651CEFED65F78AFC3EA7FE4D79B35F62A0402A1117599ADAC7B269A59F353CF450E6982D3B1702D9CA83", 16);
	BigInteger q = new BigInteger("90EAF4D1AF0708B1B612FF35E0A2997EB9E9D263C9CE659528945C0D", 16);
	BigInteger g = new BigInteger("A59A749A11242C58C894E9E5A91804E8FA0AC64B56288F8D47D51B1EDC4D65444FECA0111D78F35FC9FDD4CB1F1B79A3BA9CBEE83A3F811012503C8117F98E5048B089E387AF6949BF8784EBD9EF45876F2E6A5A495BE64B6E770409494B7FEE1DBB1E4B2BC2A53D4F893D418B7159592E4FFFDF6969E91D770DAEBD0B5CB14C00AD68EC7DC1E5745EA55C706C4A1C5C88964E34D09DEB753AD418C1AD0F4FDFD049A955E5D78491C0B7A2F1575A008CCD727AB376DB6E695515B05BD412F5B8C2F4C77EE10DA48ABD53F5DD498927EE7B692BBBCDA2FB23A516C5B4533D73980B2A3B60E384ED200AE21B40D273651AD6060C13D97FD69AA13C5611A51B9085", 16);
	@Override
	public String GetPassWord(String pwd) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String GetSignerId(String signerId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BigInteger init() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BigInteger step1(BigInteger p, BigInteger x1, BigInteger x2,
			String signerId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public BigInteger[] generateZKP(BigInteger p, BigInteger q, BigInteger g,
			BigInteger gx, BigInteger x, String signerId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean verifyZKP(BigInteger p, BigInteger q, BigInteger g,
			BigInteger gx, BigInteger x, BigInteger[] sig, String signerId) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public BigInteger step2(BigInteger p, BigInteger q, BigInteger gx1,
			BigInteger gx3, BigInteger gx4, BigInteger x2,
			BigInteger signerId_sender, BigInteger[] sig) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String sessionKey(BigInteger gx4, BigInteger x2,
			BigInteger signerId_sender, BigInteger p, BigInteger q, BigInteger B) {
		// TODO Auto-generated method stub
		return null;
	}	 


}
