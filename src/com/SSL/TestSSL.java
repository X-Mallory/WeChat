package com.SSL;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class TestSSL {
      public static void main(String[] args) throws Exception {
//		SSLSocketFactory factory=(SSLSocketFactory) SSLSocketFactory.getDefault();
//		SSLSocket socket=(SSLSocket) factory.createSocket("mail.cauc.edu.cn",443);
//		String [] suits=socket.getSupportedCipherSuites();
//		
		String keyString="654321";
		KeyStore keyStore=KeyStore.getInstance("JKS");
		char []password=keyString.toCharArray();
		keyStore.load(new FileInputStream("test.keys"),password);
		
		
		KeyManagerFactory keyManagerFactory=KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore,password);
		KeyManager[] keyManagers=keyManagerFactory.getKeyManagers();
		
		TrustManagerFactory trustManagerFactory=TrustManagerFactory.getInstance("SunX509");
		trustManagerFactory.init(keyStore);
		TrustManager[] trustManagers=trustManagerFactory.getTrustManagers();
		
		SSLContext sslContext=SSLContext.getInstance("TLS");
		sslContext.init(keyManagers, trustManagers, null);
		
		
		
		/*for(String ss:suits)
		{
			System.out.println(ss);
		}*/
	}
}
