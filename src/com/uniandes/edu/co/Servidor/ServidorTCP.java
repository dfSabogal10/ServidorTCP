package com.uniandes.edu.co.Servidor;

import java.net.*;
import java.io.*;


public class ServidorTCP extends Thread{

	public final static int maximoNumeroConexiones=5;
	public final static int tamañoBuffer=1024;
	public final static int timeout=50000;
	private int conexionesActivas;
	/**
	 * @param args
	 */

	public ServidorTCP()
	{
		conexionesActivas=0;
		this.start();
		
	}
	public void run()
	{
		try{ 
			int serverPort = 6880; 
			ServerSocket listenSocket = new ServerSocket(serverPort);
			listenSocket.setReceiveBufferSize(tamañoBuffer);
			listenSocket.setSoTimeout(timeout);
			System.out.println("server start listening... ... ...");
			while(true) { 
				if(conexionesActivas<maximoNumeroConexiones)
				{
					Socket clientSocket = listenSocket.accept();
					Conexion c = new Conexion(clientSocket,this); 
					synchronized(this){
						conexionesActivas++;
						System.out.println("se agrego conexion: "+conexionesActivas);
					}
				}
			} 
		} 
		catch(IOException e) {
			System.out.println("Listen :"+e.getMessage());} 
	}
	public void disminuirConexionesActivas(){
		synchronized(this){
			conexionesActivas--;
			System.out.println("se cerro conexion: "+conexionesActivas);

		}
	}
	public static void main(String[] args) {
		new ServidorTCP();

	}

	

}
