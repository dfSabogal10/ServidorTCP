package com.uniandes.edu.co.Servidor;
import java.net.*;
import java.io.*;

public class Conexion extends Thread{

	public final static int tamañoPaquete=64000;
	DataInputStream input; 
	DataOutputStream output; 
	Socket clientSocket; 
	ServidorTCP server;
	boolean termino;
	public Conexion (Socket aClientSocket, ServidorTCP servidorTCP) { 
		server=servidorTCP;
		try { 
			clientSocket = aClientSocket; 
			input = new DataInputStream( clientSocket.getInputStream()); 
			output =new DataOutputStream( clientSocket.getOutputStream()); 
			termino = false;
			this.start();  
		} 
		catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
		} 
	} 


	public void run() { 
		while (!termino)
		{
			try { 
				//Heart-beat
				String solicitud=input.readUTF();
				System.out.println(solicitud);
				if(solicitud.equals("VIVE"))
				{
					output.writeUTF(solicitud);
					output.flush();
					System.out.println("heartbeat sent");
				}
				//solicitud protocolo
				else if(solicitud.equals("ZUPP"))
				{
					output.writeUTF(solicitud);
					output.flush();
					System.out.println("saludo protocolo enviado:"+solicitud);
				}
				//Solicitud archivos
				else if(solicitud.equals("ARCHIVOS"))
				{
					File folder = new File("./files");
					File[] listOfFiles = folder.listFiles();
					String archivos="";
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							archivos+=listOfFiles[i].getName()+","+listOfFiles[i].length()+";";
						} else if (listOfFiles[i].isDirectory()) {
							System.out.println("Directory " + listOfFiles[i].getName());
						}
					}
					output.writeUTF(archivos);
					System.out.println("lista de archivos enviada");
					output.flush();
					System.out.println("lista de archivos enviada");
				}
				else if(solicitud.contains("ARCHIVO:"))
				{
					//Descarga archivo
					//Step 1 read length
					//			  int nb = input.readInt();
					//			  System.out.println(nb);
					//			  System.out.println("Read Length"+ nb);
					//			  byte[] digit = new byte[nb];
					//			  //Step 2 read byte
					//			   System.out.println("Writing.......");
					//			  for(int i = 0; i < nb; i++){
					//				digit[i] = input.readByte();
					//			  }			  
					//			  String filename = new String(digit);
					String filename = solicitud.split(":")[1];
					System.out.println ("receive from : " + 
							clientSocket.getInetAddress() + ":" +
							clientSocket.getPort() + " archivo solicitado - " + filename);
					File aEnviar= new File("./files/"+filename);
					//Step 1 send length
					//	  output.writeLong(aEnviar.length());
					//Step 2 send file
					
					byte []  out=new byte[(int)aEnviar.length()];
					FileInputStream fis = new FileInputStream(aEnviar);
					BufferedInputStream bis = new BufferedInputStream(fis);
					bis.read(out,0,out.length);	 
					System.out.println("sending file: "+filename+" length: "+out.length+" bytes ...");
					output.writeInt(out.length);
					int contadorBytes;
					for (contadorBytes = 0; contadorBytes < out.length; contadorBytes++) {
						if(out.length-contadorBytes >= tamañoPaquete)
						{
							output.write(out,contadorBytes,tamañoPaquete);
							contadorBytes+=tamañoPaquete;
			//				output.flush();
							System.out.println("paquete de "+tamañoPaquete +" bytes enviado.");
						}
						else
						{
							output.write(out,contadorBytes,out.length-contadorBytes);
							contadorBytes+=out.length-contadorBytes;
			//				output.flush();
							System.out.println("Ultimo paquete de "+(out.length-contadorBytes) +" bytes enviado.");
						}
					}
					
					output.flush();
					System.out.println("file "+ filename+ " sent. ( "+contadorBytes+ " bytes sent.)");
					System.out.println("Done.");	
					termino=true;
				}
				
				//output.write(aEnviar); // UTF is a string encoding
				//  output.writeUTF(data); 
			} 
			catch(EOFException e) {
				System.out.println("EOF:"+e.getMessage()); 
				termino=true;
				} 
				
			catch(IOException e) {
				System.out.println("IO:"+e.getMessage()); 
				termino=true;} 
			finally{
				if(termino) 
				{
					server.disminuirConexionesActivas();
					break;
				}
			}

		}
	}


}
