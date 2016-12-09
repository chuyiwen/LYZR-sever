package newbee.morningGlory.http.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public final class UrlBase64 {

	private static final Logger logger = Logger.getLogger(UrlBase64.class); 
	
	public static String decode(String src)
	{
		String txt = src;
		txt = txt.replaceAll("_REP_", "\\=");
		txt = txt.replaceAll("_RAP_", "\\+");
		txt = txt.replaceAll("_RBP_", "\\-");
		txt = txt.replaceAll("_RCP_", "\\\\");
		try {
			return new String( Base64.decodeBase64( txt ) , "utf-8" );
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return "";
		}
	}
	
	public static String encode(String src){

		String encodeBase64String = null;
		try {
			encodeBase64String = Base64.encodeBase64String(src.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		encodeBase64String = encodeBase64String.replaceAll("\\=", "_REP_");
		encodeBase64String = encodeBase64String.replaceAll("\\+", "_RAP_");
		encodeBase64String = encodeBase64String.replaceAll("\\-", "_RBP_");
		encodeBase64String = encodeBase64String.replaceAll("\\\\", "_RCP_");
		
		return encodeBase64String;
	}
	
	public static void main( String[] aa )
	{
//		 String t = "a134010238589我的撒的减肥了金卡的放假阿萨德飞拉沙德飞啦水电费723495723945adf()jdjfjd121233eeasdfasdfagsdbxcvbsgh;lkmx,vboaskdhflavklzsdhfiaeyhdfoasdfkasnkvjbcABC123_=+-)(*&^%$#@!><:\"?|}[][]\';/.,'";
//		 String c = UrlBase64.encode(t);
//		 System.out.println( c );
//		 String t2 = UrlBase64.decode(c);
//		 System.out.println( t );
//		 System.out.println( t2 );
		
//		System.out.println( "_REP__REP__RAP__RAP__RBP__RBP__RCP__RCP_".replaceAll("_REP_", "\\=").replaceAll("_RAP_", "\\+").replaceAll("_RBP_", "\\-").replaceAll("_RCP_", "\\\\") );


		String txt = "6Ziz6Z2Z5p_RAP_U";
		txt = txt.replaceAll("_REP_", "\\=");
		txt = txt.replaceAll("_RAP_", "\\+");
		txt = txt.replaceAll("_RBP_", "\\-");
		txt = txt.replaceAll("_RCP_", "\\\\");
		
		System.out.println(  txt  );
		System.out.println(  UrlBase64.decode( txt )  );
//		System.out.println(  UrlBase64.decode("6Ziz6Z2Z5p_RAP_U")  );
//		System.out.println(  new String( Base64.decodeBase64("6Ziz6Z2Z5p_RAP_U") ) );
		
		
		
		
	}
	
}
