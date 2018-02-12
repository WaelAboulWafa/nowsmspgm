import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;
import java.io.*;
import java.net.*;


public class NowSMS_Pgm 
{
	
	
	 public static OutputStream logfile; 
     public static String IP="127.0.0.1";
     public static int Port=80;
     public static String success="OK";
     
	public static void main(String[] args) throws Exception
	{
		
      logfile = new FileOutputStream("logs/" + ((new SimpleDateFormat ("yyyy-MM-dd")).format(new java.util.Date())).trim() + ".txt",true);
   	  logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
      logfile.write(("Application Started.\r\n").getBytes("ASCII"));    	

      String filespath="C:/Program Files (x86)/NowSMS/SMS-IN/";		
      String backuppath="C:/Program Files (x86)/NowSMS/SMS-IN.BAK/";
      String filterfiles=".SMS";
    

 	  try
 	  {
      

          

       main_loop :
        while(true) 
        { 
         

           logfile.close();logfile=null;
           logfile = new FileOutputStream("logs/"+  ((new SimpleDateFormat ("yyyy-MM-dd")).format(new java.util.Date())).trim() + ".txt",true);


           File path = new File(filespath);
           String[] list;
           
           
           DirFilter df= new DirFilter(filterfiles);
           df.accept(path,"");
           list = path.list(df);
           
           String smppconnector="";
           String msisdn="";
           String contents="";
           String shortcode="";
           String lang="E";
           int len=0;
              
           if(list.length >= 1)
           {
            BufferedReader buff = new BufferedReader(new FileReader(filespath +list[0]));  
            boolean eof = false;
            while(!eof)
            {
          	 String line = buff.readLine();
          	 if(line==null) {eof=true;}
          	 else
          	 {
              StringTokenizer st =new StringTokenizer(line,"=");
              

              if(line.indexOf("ModemName=")!= -1){st.nextToken();smppconnector= st.nextToken();}              
              if(line.indexOf("Sender=")!= -1){st.nextToken();msisdn= st.nextToken();}
              if(line.indexOf("Data=")!= -1){try{st.nextToken();contents= st.nextToken();}catch(Exception e){contents="";}}   
              if(line.indexOf("PhoneNumber=")!= -1){st.nextToken();shortcode= st.nextToken();}
              if(line.indexOf("Binary=")!= -1)
              {
              	 if(line.indexOf("Binary=0")!= -1){lang="E";}
              	 else{lang="A";}
              }
              
              
                              

           
             }
           }
             
            /////
            buff.close();
            
                         
            
      	     String thehttprequest="";
             thehttprequest=Generate_HTTP_Request(smppconnector,msisdn,contents,lang,shortcode);            	
             
             logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
             logfile.write(("Trying to send file " + list[0] + " ...\r\n").getBytes("ASCII"));    	




             int return_value = SendandLogRequest(thehttprequest,list[0]);    
                      

            if (return_value==0)
            {
             backup_file(filespath +list[0],backuppath +list[0]);
             org.apache.commons.io.FileUtils.forceDelete(new File(filespath +list[0]));
            }

           
            
            
            System.out.println("file---->" + list[0]);
            
            System.out.println("Modem---->" + smppconnector);
            System.out.println("msisdn---->" + msisdn);
            System.out.println("contents---->" + contents);
            System.out.println("lang---->" + lang);
            System.out.println("length--->" + len);
            System.out.println("\r\n\r\n");
            
            
            
             list=null;
             buff = null;  
             
             Thread.sleep(400); 
            }
            
             
            
            else
            {
             logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
             logfile.write(("Empty Queue\r\n").getBytes("ASCII"));   
             
             Thread.sleep(5000); 
                     	
            }
         
            
            

         
              
         
            
          }


       
       
       	                      
                             
       
      

    
   
    
    
    
    
              }catch(Exception e){
          	       	             try{
       	                           	  logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
                                      logfile.write (( e.toString() +"\r\n").getBytes("ASCII"));    	
                                    }catch(Exception e1){}

          	                   System.out.println(e); 
          	                   return;
                             }
    
    
    
    
         
   }
   
   
   
   
   public static void backup_file(String src,String dest) throws Exception
   {

			File fOrig = new File(src);  
			File fDest = new File(dest);  
			org.apache.commons.io.FileUtils.copyFile(fOrig, fDest); 
    
   }
   
   
   
   
   
   
   
   
   
   
      	public static int SendandLogRequest(String thereq,String filename) throws Exception
    	{
    	 
    	  String remain="";
    	  PrintWriter out; 
          BufferedReader in;
          Socket s;
          ByteArrayOutputStream fout; 


       	   logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
           logfile.write (("Trying : " +IP +":" + Integer.toString(Port)+"\r\n").getBytes("ASCII"));

    	     s = new Socket(IP,Port);

    	   
    	   
    	   out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),false);        
	  	   in = new BufferedReader(new InputStreamReader(s.getInputStream())); 	
  	  	   out.println(thereq);
	  	   out.println("\r\n");
	  	   out.flush();
	  	   System.out.println(thereq);	  

      	   logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
           logfile.write (( thereq +"\r\n").getBytes("ASCII"));

	       //read response
           fout = new ByteArrayOutputStream();
           int b;
           while((b = in.read()) != -1) fout.write(b);
           remain = fout.toString();

           in.close();out.close();s.close();
           in= null;out=null;s=null;fout=null;
           System.out.println(remain);

      	   logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
           logfile.write (( remain +"\r\n").getBytes("ASCII"));    	


          if(remain.endsWith(success))
           {
            logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
            logfile.write(("file " + filename + " sent successfuly.\r\n").getBytes("ASCII"));    	      	    
            
            return 0;
      	    
           }
           else
           { 
             logfile.write( ("<"+ ((new SimpleDateFormat ("yyyy-MM-dd kk:mm:ss")).format(new java.util.Date())).trim() +"> ").getBytes("ASCII") ); 
             logfile.write (("UNsuccessful response received for file : "+ filename + "\r\n").getBytes("ASCII"));   
             
        
             return 1;
            }
            

            
            	  
    	} 
    	
    	
    	
    	
    	
    	
     	
       public static String Generate_HTTP_Request(String smppconnector,String themsisdn,String thecontents, String thelang,String theshortcode)
       {
    	  String tempcontents=thecontents;
    	  
    	  if (thelang.equalsIgnoreCase("E")){tempcontents = URLEncoder.encode(thecontents);}

     	  return  "GET /nowsmscdrs/smscdr.jsp?" + 
     	  		  "provider=" + URLEncoder.encode(smppconnector.trim())  + 
     	  		  "&shortcode=" + URLEncoder.encode(theshortcode.trim()) + 
     	  		  "&msisdn="+ URLEncoder.encode(themsisdn.trim()) + 
     	  		  "&contents=" + URLEncoder.encode(tempcontents) + 
     	  		  "&lang=" + thelang ;
          

        }
   
   
   
   
   
   
   
   
   
   
   
   
  }
  
  
  
 
 
 
 
  
  
  
  
  class DirFilter implements FilenameFilter {
  String afn;
  DirFilter(String afn) { this.afn = afn; }
  public boolean accept(File dir, String name) {
    // Strip path information:
    String f = new File(name).getName();
    
        return f.indexOf(afn) != -1;
  }
}
