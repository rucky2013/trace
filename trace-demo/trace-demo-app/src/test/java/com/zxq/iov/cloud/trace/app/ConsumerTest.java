package com.zxq.iov.cloud.trace.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxq.iov.cloud.trace.dto.OTAMessage;

public class ConsumerTest {

//	@Test
//	public void testSayHello() {
//		String url = "http://localhost:8080/trace-demo-app/hello/aaa";
//		String result = doGet(url, "GET");
//		String hello = getJsonToObj(result, "data", String.class);
//		Assert.assertNotNull(result, hello);
//	}
	
	@Test
	public void testSend() throws IOException {
//		String url = "http://localhost:8080/trace-demo-app/message/send";
		String url = "http://10.25.23.102:8080/trace-demo-app/message/send";
		String result = doPost(url, "POST");
//		String hello = getJsonToObj(result, "data", String.class);
		Assert.assertNull(result, null);
	}

	private static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private <T> T getJsonToObj(String jsonContent, String nodeName, Class<T> classT) {
		JsonNode node = null;
		try {
			node = mapper.readTree(jsonContent).findValue(nodeName);
			if (node == null) {
				return null;
			} else {
				return mapper.readValue(node.toString(), classT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String doPost(String urlAddress, String method) throws IOException {
        URL url = new URL(urlAddress);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);// 提交模式
        // conn.setConnectTimeout(10000);//连接超时 单位毫秒
        // conn.setReadTimeout(2000);//读取超时 单位毫秒
        conn.setDoOutput(true);// 是否输入参数

        OTAMessage message = new OTAMessage();
        message.setAckMessageCounter(1);
        message.setAckRequired(true);
        message.setAid("aid");
        message.setAppData("appData".getBytes());
        
        String para = mapper.writeValueAsString(message);
//        System.out.println(para);
        OutputStream outStream = conn.getOutputStream();
        outStream.write(("message=" + para).getBytes());// 输入参数
        outStream.flush();
        outStream.close();
        System.out.println(conn.getResponseCode()); //响应代码 200表示成功
		return null;
	}

	private String doGet(String urlAddress, String method) {
		URL url;
		HttpURLConnection con = null;
		BufferedReader br = null;
		StringBuffer outInfo = new StringBuffer();
		String input = null;
		try {
			url = new URL(urlAddress);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(method);
			con.setRequestProperty("Accept-Ranges", "qwdwe");
			con.connect();
			System.out.println("****** Content of the URL ********urlAddress=" + urlAddress);

			if (con != null) {

				System.out.println("****** getResponseCode=" + con.getResponseCode());
				for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
					System.out.print(String.format("****HeaderField:%s=%s", entry.getKey(), entry.getValue().get(0)));
				}
			}
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			while ((input = br.readLine()) != null) {
				outInfo.append(input);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.disconnect();
				}
				if (br != null) {
					br.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return outInfo.toString();
	}

}
