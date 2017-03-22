package com.uniandes.edu.co.Servidor;
import java.net.*;
import java.io.*;

public class Conexion extends Thread{
	
	DataInputStream input; 
	DataOutputStream output; 
	Socket clientSocket; 
	
	public Conexion (Socket aClientSocket) { 
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
					
			  //Step 1 read length
			  int nb = input.readInt();
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
	          System.out.println("sending file: "+filename+" length: "+out.length);
	          output.write(out,0,out.length);
	          
	          output.flush();
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
			  }
			  catch (IOException e){/*close failed*/}
			}
		}

}
