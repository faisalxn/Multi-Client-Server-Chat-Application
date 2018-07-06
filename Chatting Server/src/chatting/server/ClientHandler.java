
package chatting.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import javafx.util.Pair;

class ClientHandler implements Runnable 
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
    public ClientHandler(Socket s, String name,DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        //this.isloggedin=true;
    }
 
    @Override
    public void run() {
 
        String received;
        while (true) 
        {
            try
            {
                received = dis.readUTF();
                //System.out.println(received);
                String blank = "";
                if(received.equals("get list") && isloggedin==true ){
                    for (ClientHandler mc : Server.ar) 
                    {
                        //System.out.println(mc.name);
                        if(mc.isloggedin){
                        
                            blank += mc.name+"\n";
                        
                        }
                        
                    }
                    this.dos.writeUTF("online user list : \n"+blank);
                    continue;
                }
                
                
                else if(received.equals("sign up")){
                    
                    received = dis.readUTF();
                    Server.userName.add(received);
                        
                        
                    this.name = received;
                        
                        
                    received = dis.readUTF();
                    Server.password.add(received);
                    
                }
                else if(received.equals("login")){
                    String name = dis.readUTF();
                    String pass = dis.readUTF();
                        
                    for (int i = 0 ; i<Server.userName.size() ; i++) 
                    {
                        String ut = Server.userName.get(i);
                        String pt = Server.password.get(i);
                        
                        if(name.equals(ut) && pass.equals(pt)){
                            this.isloggedin=true;
                            break;
                        }
                    }
                }
                
                
                else if(received.equals("logout")){
                    this.isloggedin=false;
                    //this.s.close();
                    //Server.ar.remove(this);
                    //break;
                    this.dos.writeUTF("Logged out\n");
                    
                }
                
                else{
                    
                    StringTokenizer st = new StringTokenizer(received, ":");
                    if(st.countTokens()==2){
                        String MsgToSend = st.nextToken();
                        String recipient = st.nextToken();

                        if(recipient.equals("all")){
                            for (ClientHandler mc : Server.ar) 
                            {
                                if (mc.isloggedin==true) 
                                {
                                    if(this.name.equals(mc.name)==false){
                                        mc.dos.writeUTF(this.name+" : "+MsgToSend);
                                    }
                                }
                            }

                        }
                        else if(MsgToSend.equals("request")){
                            for (ClientHandler mc : Server.ar) 
                            {
                                if (mc.name.equals(recipient) && mc.isloggedin==true) 
                                {
                                    mc.dos.writeUTF(MsgToSend+":"+this.name);
                                    
                                    String sender = this.name;
                                    Pair<String,String> p = new Pair<>(sender,recipient);
                                    Server.map.put(p, -1);
                                    break;
                                }
                            }
                        }
                        
                        else if(MsgToSend.equals("accept")){
                            for (ClientHandler mc : Server.ar) 
                            {
                                if (mc.name.equals(recipient) && mc.isloggedin==true) 
                                {
                                    
                                    
                                    String sender = this.name;
                                    Pair<String,String> p = new Pair<>(recipient,sender);
                                    
                                    if(Server.map.get(p)==-1){
                                        mc.dos.writeUTF(sender+" accepted your friend request.");
                                        
                                        Server.map.put(p, 1);
                                        
                                        Pair<String,String> p2 = new Pair<>(sender,recipient);
                                        Server.map.put(p2, 1);
                                        
                                        break;
                                    
                                    }
                                }
                            }
                        }
                        
                        else if(MsgToSend.equals("reject")){
                            for (ClientHandler mc : Server.ar) 
                            {
                                if (mc.name.equals(recipient) && mc.isloggedin==true) 
                                {
                                    
                                    
                                    String sender = this.name;
                                    Pair<String,String> p = new Pair<>(recipient,sender);
                                    
                                    if(Server.map.get(p)==-1){
                                        mc.dos.writeUTF(sender+" rejected your friend request.");
                                        Server.map.put(p, 0);
                                        break;
                                    
                                    }
                                }
                            }
                        }
                        
                        else{
                            String sender = this.name;
                            Pair<String,String> p = new Pair<>(recipient,sender);
                            int check ;
                            
                            
                            if(Server.map.containsKey(p))
                                check = (Integer)Server.map.get(p) ;
                            else
                                check = 0 ;
                            
                            
                            
                            
                            for (ClientHandler mc : Server.ar) 
                            {
                                if (mc.name.equals(recipient) && mc.isloggedin==true && check==1) 
                                {
                                    mc.dos.writeUTF(this.name+" : "+MsgToSend);
                                    break;
                                }
                            }
                        }
                    }
                    else{
                        int length = st.countTokens() ;
                        String MsgToSend = st.nextToken();

                        
                        for(int i = 1 ; i<=length-1 ; i++){
                            String recipient = st.nextToken();
                            
                            //---------
                            Pair<String,String> p = new Pair<>(recipient,this.name);
                            int check ;
                            
                            
                            if(Server.map.containsKey(p))
                                check = (Integer)Server.map.get(p) ;
                            else
                                check = 0 ;
                            //---------
                            
                            
                            for (ClientHandler mc : Server.ar) 
                            {
                                if (mc.name.equals(recipient) && mc.isloggedin==true  && check==1 ) 
                                {
                                    mc.dos.writeUTF(this.name+" : "+MsgToSend);
                                }
                            }
                        }
                    }
                    
                    
                }
            } catch (IOException e) {
                 
                e.printStackTrace();
            }
             
        }
        /*
        try
        {
            this.dis.close();
            this.dos.close();
             
        }catch(IOException e){
            e.printStackTrace();
        }
        */
    }
}
