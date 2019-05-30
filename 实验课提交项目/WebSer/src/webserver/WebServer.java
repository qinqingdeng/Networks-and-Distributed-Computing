package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WebServer {
	public static void startServer(int port){
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);
			while(true){
				Socket socket = serverSocket.accept();
				new HttpServer(socket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args){
		@SuppressWarnings("resource")
		Scanner in =new Scanner(System.in);
		System.out.print("Please input the Port Number£¨ 1024 -65535 £©£º");
		int portNumber=80;
		do{
			portNumber=in.nextInt();
			if(portNumber>=1024 && portNumber <= 65535 )
				break;
			else
				System.out.print("The Input Port Number is wrong,please input again£¨ 1024 -65535 £©£º");
		}while(in.hasNext());
		System.out.println("**********WebServer start!*********");
		startServer(portNumber);
	}
}
