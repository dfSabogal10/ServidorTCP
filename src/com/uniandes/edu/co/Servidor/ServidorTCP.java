package com.uniandes.edu.co.Servidor;

import java.net.*;
import java.io.*;

public class ServidorTCP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{ 
			int serverPort = 6880; 
			ServerSocket listenSocket = new ServerSocket(serverPort); 
	  
			System.out.println("server start listening... ... ...");
		
			while(true) { 
				Socket clientSocket = listenSocket.accept(); 
				Conexion c = new Conexion(clientSocket); 
			} 
	} 
	catch(IOException e) {
		System.out.println("Listen :"+e.getMessage());} 

	}

}
