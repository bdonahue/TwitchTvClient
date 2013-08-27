package com.wdonahue.rapidparsing.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Web {
	/**
	 * Will download a website via GET to a String. This is a very quick and
	 * dirty downloader and should not be used in production code.
	 */
	public static String getWebsite(String site) {
		try {
			URL url = new URL(site);
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[8192];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			return new String(baos.toByteArray(), encoding);
		} catch (Exception e) {
			return null;
		}
	}
}
