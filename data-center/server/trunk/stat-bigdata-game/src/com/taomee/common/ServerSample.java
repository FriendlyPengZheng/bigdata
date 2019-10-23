package com.taomee.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Local Socket Server for test
 * 
 * @author cheney
 * @date 2013-11-08
 */
public class ServerSample {

	public static void main(String[] args) {
		
		ServerSocket server = null;
		Socket socket = null;
		
		try {
			server = new ServerSocket(6000);
			System.out.println("Server Listen on 6000");
			
			while(true){
				socket = server.accept();
				System.out.println("Receive msg...");
				new Thread(new SSocket(socket)).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
//服务器进程
class SSocket implements Runnable {
	Socket client;
	public SSocket(Socket client) {
		this.client = client;
	}
	public void run() {
		BufferedReader is = null;
		try {
			is = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			String line;
			while ((line = is.readLine()) != null) {
				System.out.println("Client:" + line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
