import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClientPt2 {
    public static void main(String[] args) { // TO RUN SERVER USE COMMAND "./ds-server -c ../../configs/sample-configs/ds-sample-config01.xml -v all -n"
        try {
            Socket s = new Socket("localhost", 50000);
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            String response = new String();

            // Run handshake protocol
            response = handshake(din, dout);
            System.out.println("post handshake response: "+response);

            //Run Scheduling Algorithm
            scheduleAlgorithm(din, dout);
            
            //Exit
            dout.write(("QUIT\n").getBytes());
            response = din.readLine();
            System.out.println("Response: " + response);
            dout.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static String handshake(DataInputStream din, DataOutputStream dout){
        String response = new String();
        try {
            // HELO
            dout.write(("HELO\n").getBytes());
            response = din.readLine();
            dout.flush();
            System.out.println("Response: " + response);
            // Auth
            String username = System.getProperty("user.name");
            dout.write(("AUTH " + username+"\n").getBytes());
            response = din.readLine();
            System.out.println("Response: " + response);
            dout.flush();
        } catch (Exception e) {
            System.out.println("Failed Handshake Protocol");
        }
        return response;
    }

    public static void scheduleAlgorithm(DataInputStream din, DataOutputStream dout){
        try {
            String [] job;
            String response = new String();

            while(!response.contains("NONE")){
                dout.write(("REDY\n").getBytes());
                response = din.readLine();
                dout.flush();
                if(response.contains("JOBN")){
                    ArrayList<ServerObj> serverList = new ArrayList<ServerObj>();

                    job = response.split(" ");
                    dout.write(("GETS Capable " + job[4] + " " + job[5] + " " + job[6] + "\n").getBytes());
                    response = din.readLine();
                    Integer numServers = Integer.parseInt(response.split(" ")[1]);

                    dout.write(("OK\n").getBytes());

                    System.out.println(numServers);
                    for(int a=0;a<numServers;a++){
                        response = din.readLine();
                        serverList.add(new ServerObj(response));
                    }
                    System.out.println("ServerList: " + serverList.toString());
                    dout.write(("OK\n").getBytes());                    
                    
                    response = din.readLine();
                    System.out.println("after server OK response: "+response);
                    dout.write(("SCHD " + job[2] + " " + serverList.get(0).type() + " " +serverList.get(0).id() +"\n").getBytes());
                    response = din.readLine();
                    System.out.println("SCHD response: "+response);
                }
            }
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Failed to run schedule algorithm");
        }
    }
}
