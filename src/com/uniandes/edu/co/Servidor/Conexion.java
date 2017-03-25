package com.uniandes.edu.co.Servidor;
import java.net.*;
import java.io.*;

public class Conexion extends Thread{
	
	DataInputStream input; 
	DataOutputStream output; 
	Socket clientSocket; 
	ServidorTCP server;
	public Conexion (Socket aClientSocket, ServidorTCP servidorTCP) { 
		server=servidorTCP;
		try { 
					clientSocket = aClientSocket; 
					input = new DataInputStream( clientSocket.getInputStream()); 
					output =new DataOutputStream( clientSocket.getOutputStream()); 
					this.start();  
			} 
			catch(IOException e) {
				System.out.println("Connection:"+e.getMessage());
			} 
	  } 


	public void run() { 
		try { 
				System.out.println("corre");
			  //Saludo protocolo
			  String saludo=input.readUTF();
			  if(saludo.equals("ZUPP"))
			  {
				 output.writeUTF(saludo);
				 output.flush();
			  }
			  //Solicitud archivos
			  String solicitud =input.readUTF();
			  if(solicitud=="ARCHIVOS")
			  {
				  File folder = new File("./files");
				  File[] listOfFiles = folder.listFiles();

				      for (int i = 0; i < listOfFiles.length; i++) {
				        if (listOfFiles[i].isFile()) {
				          output.writeUTF(listOfFiles[i].getName()+","+listOfFiles[i].length());
				        } else if (listOfFiles[i].isDirectory()) {
				          System.out.println("Directory " + listOfFiles[i].getName());
				        }
				      }
				      output.flush();
			  }
			  //Descarga archivo
			  //Step 1 read length
			  int nb = input.readInt();
			  System.out.println(nb);
			  System.out.println("Read Length"+ nb);
			  byte[] digit = new byte[nb];
			  //Step 2 read byte
			   System.out.println("Writing.......");
			  for(int i = 0; i < nb; i++){
				digit[i] = input.readByte();
			  }			  
			  String filename = new String(digit);
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
	          output.write(out,0,out.length);
	          output.flush();
	          System.out.println("file "+ filename+ " sent.");
	          System.out.println("Done.");			 
	          //output.write(aEnviar); // UTF is a string encoding
		  //  output.writeUTF(data); 
			} 
			catch(EOFException e) {
			System.out.println("EOF:"+e.getMessage()); } 
			catch(IOException e) {
			System.out.println("IO:"+e.getMessage());}  
   
			finally { 
			  try { 
				  clientSocket.close();
				  server.disminuirConexionesActivas();
			  }
			  catch (IOException e){/*close failed*/}
			}
		}

}
