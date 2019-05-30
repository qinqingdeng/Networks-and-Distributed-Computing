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
	//web��Դ·�����˴�Ϊ���·��
		public static final String ROOT = "./resource";
		//����������,��ȡ���������
		private InputStream input;
		//�����������Ӧ���ݸ������
		private OutputStream out;
		//��Ϊ�������⣬text/html,��ֱ��ʹ��out�����ʹ��Ĭ�ϱ��룬������UTF-8,��˻ᵼ����������
		private OutputStreamWriter osw;
	 
	    //��ʼ��socket����,��ȡ��Ӧ ���룬�����
		public HttpServer(Socket socket) {
			try {
				input = socket.getInputStream();
				out = socket.getOutputStream();
				osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	 
		//���̷߳�������
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
	  		
			//��ֹ�û�ֱ������"localhost:portnumber",����·��Ϊ".\resource"�����ִ���
			if (file.exists() && !file.getPath().equals(".\\resource")) {
				//��Դ���ڣ���ȡ��Դ
				try {
					//��ȡ�ļ����ͣ��ж�Content-Type
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
				//��Դ�����ڣ���ʾ File not found
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
	 
	    //��������·��
		private String read() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			try {
				// ��ȡ����ͷ�� �磺GET /index.html HTTP/1.1
				String readLine = reader.readLine();
				// ��ֹ�û�ֱ������"localhost:portnumber"�����ִ���
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
