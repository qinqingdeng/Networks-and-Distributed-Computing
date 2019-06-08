package webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class HttpServer extends Thread {
	//web资源路径，此处为相对路径
		public static final String ROOT = "./resource";
		//输入流对象,读取浏览器请求
		private InputStream input;
		//输出流对象，响应内容给浏览器
		private OutputStream out;
		//因为编码问题，text/html,若直接使用out输出会使用默认编码，而不是UTF-8,因此会导致中文乱码
		private OutputStreamWriter osw;
	 
	    //初始化socket对象,获取对应 输入，输出流
		public HttpServer(Socket socket) {
			try {
				input = socket.getInputStream();
				out = socket.getOutputStream();
				osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	 
		//多线程方法调用
		@Override
		public void run() {
			String filePath = read();
			response(filePath);
		}
	 
		private void response(String filePath) {
			File file = new File(ROOT + filePath);
	    	//System.out.println("filePath:"+filePath);
	  	    System.out.println("filePath():"+file.getPath());
	     	//System.out.println("file.getName():"+file.getName());
	  		
			//防止用户直接输入"localhost:portnumber",导致路径为".\resource"而出现错误。
			if (file.exists() && !file.getPath().equals(".\\resource")) {
				//资源存在，读取资源
				try {
					//获取文件类型，判断Content-Type
					String fileType =file.getName().split("\\.")[1];
					//System.out.println("fileType:"+fileType);
					if(fileType.equals("html")) {
						FileInputStream fis = new FileInputStream(file);   
						InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
						BufferedReader reader = new BufferedReader(isr);
						StringBuffer sb = new StringBuffer();
						String line = null;
						while ((line = reader.readLine()) != null) {
							sb.append(line).append("\r\n");
						}
						StringBuffer result = new StringBuffer();
						result.append("HTTP/1.1 200 OK \r\n");
						result.append("accept-ranges: bytes \r\n");
						result.append("Content-Type:text/html;charset=UTF-8 \r\n");
						result.append("Content-Length:" + file.length() + "\r\n");
						result.append("\r\n" + sb.toString());
						//System.out.println(result.toString());   
						osw.write(result.toString());   
						osw.flush();
						osw.close();
						fis.close();
						isr.close();
						reader.close();
					}else if(fileType.equals("jpg")) {
						StringBuffer result = new StringBuffer();
						result.append("HTTP/1.1 200 OK \r\n");
						result.append("accept-ranges: bytes \r\n");
						result.append("Content-Type:image/jpeg \r\n");
						result.append("Content-Length:" + file.length() + "\r\n");
						result.append("\r\n");
						//System.out.println(result.toString());   
						out.write(result.toString().getBytes());   
						FileInputStream fis = new FileInputStream(file);
						byte[] buf=new byte[1024];
						int len=0; 
					    while((len=fis.read(buf))!=-1){
					       out.write(buf,0,len);
					    }
						out.flush();
						out.close();
						fis.close();
					}
					
				} catch (Exception e) {
					e.printStackTrace();                                    
				}
	 
			} else {   
				//资源不存在，提示 File not found
				try {
					StringBuffer error = new StringBuffer();
					String html="<h1>404 File Not Found.</h1>";
					
					error.append("HTTP/1.1 404 Not Found \r\n");
					error.append("Content-Type:text/html;charset=UTF-8 \r\n");
					error.append("Content-Length:").append(html.length()).append("\r\n").append("\r\n");
					error.append(html);

					osw.write(error.toString());
					osw.flush();
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
		}
	 
	    //解析请求路径
		private String read() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			try {
				// 读取请求头， 如：GET /index.html HTTP/1.1
				String readLine = reader.readLine();
				// 防止用户直接输入"localhost:portnumber"而出现错误。
				System.out.println(readLine);
				if(readLine==null)
					return "/";
				String[] split = readLine.split(" ");
				if (split.length != 3) {
					return "/";
				}
				return split[1];
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}	
}
