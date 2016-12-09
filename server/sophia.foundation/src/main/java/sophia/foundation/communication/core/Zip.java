/**
 * 
 */
package sophia.foundation.communication.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Zip {
	public static byte[] zip(byte[] bytes)
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ZipOutputStream zipStream = new ZipOutputStream(byteStream);

		ZipEntry zipEntry = new ZipEntry("zip");
		try {
			zipStream.putNextEntry(zipEntry);
			zipStream.write(bytes);
			zipStream.flush();
			zipStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		byte[] compressBytes = byteStream.toByteArray();
		return compressBytes;
	}
	
	public static byte[] unZip(byte[] zipBytes)
	{
		//解开压缩的消息
		ByteArrayInputStream byteStream = new ByteArrayInputStream( zipBytes );
		ZipInputStream zipStream = new ZipInputStream(byteStream);
		byte[] bytes = null;
		try {
			zipStream.getNextEntry();
			byte[] buf = new byte[1024];
		    int num = -1;
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    while ((num = zipStream.read(buf, 0, buf.length)) != -1) {
		    	out.write(buf, 0, num);
		    }
		    bytes = out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public static byte[] gZip(byte[] data) {
		byte[] compressBytes = null;
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(byteStream);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			compressBytes = byteStream.toByteArray();
			byteStream.close();
		} catch (Exception ex) {
			  ex.printStackTrace();
		}
		return compressBytes;
	}
	
	public static byte[] unGZip(byte[] data) {
		 byte[] bytes = null;
		 try {
			   ByteArrayInputStream in = new ByteArrayInputStream(data);
			   GZIPInputStream gzip = new GZIPInputStream(in);
			   byte[] buf = new byte[1024];
			   int num = -1;
			   ByteArrayOutputStream out = new ByteArrayOutputStream();
			   while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				   out.write(buf, 0, num);
			   }
			   bytes = out.toByteArray();
			   out.flush();
			   out.close();
			   gzip.close();
			   in.close();
		} catch (Exception ex) {
			   ex.printStackTrace();
		}
		return bytes;
	}
}
