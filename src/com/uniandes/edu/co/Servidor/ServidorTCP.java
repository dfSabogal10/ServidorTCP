package com.uniandes.edu.co.Servidor;

import java.net.*;
import java.io.*;


public class ServidorTCP extends Thread{

	public final static int maximoNumeroConexiones=32000;
	public final static int tamanio=2560000;
	public final static int timeout=20000;
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
			listenSocket.setReceiveBufferSize(tamanio);
			listenSocket.setSoTimeout(timeout);
			listenSocket.setPerformancePreferences(0, 0, 2);
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
