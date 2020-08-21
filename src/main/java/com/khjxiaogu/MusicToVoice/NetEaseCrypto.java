package com.khjxiaogu.MusicToVoice;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class NetEaseCrypto {

	public NetEaseCrypto() {}

	static final String[] userAgentList = new String[] {
	        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36",
	        "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
	        "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
	        "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
	        "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
	        "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36",
	        "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89;GameHelper",
	        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4",
	        "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1",
	        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
	        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:46.0) Gecko/20100101 Firefox/46.0",
	        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:46.0) Gecko/20100101 Firefox/46.0",
	        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
	        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
	        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
	        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)",
	        "Mozilla/5.0 (Windows NT 6.3; Win64, x64; Trident/7.0; rv:11.0) like Gecko",
	        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/13.10586",
	        "Mozilla/5.0 (iPad; CPU OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1" };

	public static String getUserAgent() {

		Double index = Math.floor(Math.random() * NetEaseCrypto.userAgentList.length);
		return NetEaseCrypto.userAgentList[index.intValue()];
	}

	/**
	 * The preset key.<br />
	 * 预置密钥.
	 */
	private static String presetKey = "0CoJUm6Qyw8W8jud";

	/**
	 * The public key.<br />
	 * 公钥.
	 */
	private static String publicKey = "010001";

	/**
	 * The modulus for RSA.<br />
	 * 模数.
	 */
	private static String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";

	/**
	 * The input vector for AES.<br />
	 * 输入向量.
	 */
	private static String iv = "0102030405060708";

	/**
	 * Create a AES secret key.<br />
	 * 生成AES密钥
	 *
	 * @param size length/长度<br />
	 * @return return the secret key <br />
	 *         返回密钥串
	 */
	private static String createSecretKey(int size) {
		String keys = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String key = "";
		for (int i = 0; i < size; i++) {
			Double index = Math.floor(Math.random() * keys.length());
			key += keys.charAt(index.intValue());
		}
		return key;
	}

	/**
	 * AES encrypt.<br />
	 * AES加密
	 *
	 * @param content the content<br />
	 *                要加密的内容
	 * @param key     the key<br />
	 *                密钥
	 * @param iv      the iv<br />
	 *                输入向量
	 * @return return AES encrypted base64 string <br />
	 *         返回加密后的base64字符串
	 */
	private static String aesEncrypt(String content, String key, String iv) {
		String result = null;
		if (content == null || key == null)
			return result;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] bytes = new byte[0];
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"),
			        new IvParameterSpec(iv.getBytes("utf-8")));
			bytes = cipher.doFinal(content.getBytes("utf-8"));
			result = Base64.getEncoder().encodeToString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * pad the string from left with 0 to size.<br />
	 * 用0填充字符串到指定长度
	 *
	 * @param str  the source string<br />
	 *             源字符串
	 * @param size the new size<br />
	 *             新长度
	 * @return return padded string <br />
	 *         返回填充了的字符串
	 */
	private static String fillString(String str, int size) {
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() < size)
			sb.insert(0, "0");
		return sb.toString();
	}

	/**
	 * RSA encrypt.<br />
	 * RSA加密
	 *
	 * @param text    the text to encrypt<br />
	 *                明文
	 * @param pubKey  the public key<br />
	 *                公钥
	 * @param modulus the modulus<br />
	 *                模数
	 * @return return RSA encrypted base64 string <br />
	 *         返回加密后的base64字符串
	 */
	private static String rsaEncrypt(String text, String pubKey, String modulus) {

		// 反转字符串
		text = new StringBuffer(text).reverse().toString();

		BigInteger biText = new BigInteger(Utils.bytesToHex(text.getBytes()), 16);
		BigInteger biEx = new BigInteger(pubKey, 16);
		BigInteger biMod = new BigInteger(modulus, 16);
		BigInteger biRet = biText.modPow(biEx, biMod);

		return NetEaseCrypto.fillString(biRet.toString(16), 256);

	}

	/**
	 * Encrypt as weapi parameters.<br />
	 * 按照Weapi标准加密
	 *
	 * @param content the content<br />
	 *                明文
	 * @return return encrypted strings <br />
	 *         返回加密后的参数
	 */
	public static String[] weapiEncrypt(String content) {
		String[] result = new String[2];
		String key = NetEaseCrypto.createSecretKey(16);

		String encText = NetEaseCrypto.aesEncrypt(
		        NetEaseCrypto.aesEncrypt(content, NetEaseCrypto.presetKey, NetEaseCrypto.iv), key, NetEaseCrypto.iv);
		String encSecKey = NetEaseCrypto.rsaEncrypt(key, NetEaseCrypto.publicKey, NetEaseCrypto.modulus);
		try {
			result[0] = URLEncoder.encode(encText, "UTF-8");
			result[1] = URLEncoder.encode(encSecKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {}
		return result;

	}
}
