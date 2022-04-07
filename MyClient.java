import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClient {

    public static ArrayList<ServerObj> largestServers(DataInputStream din, DataOutputStream dout){
        ArrayList<String> serverList = new ArrayList<String>();
        ArrayList<String> largestServerList = new ArrayList<String>();
        String response = "";
        int numServers;
        String largestServer = "";


        ArrayList<ServerObj> serverList2 = new ArrayList<ServerObj>();
        ArrayList<ServerObj> largestServerList2 = new ArrayList<ServerObj>();
        ServerObj largestServer2 = null;
        try {
            //Get number of servers
            dout.write(("GETS All\n").getBytes());
            response = din.readLine();
            numServers = Integer.parseInt(response.split(" ")[1]);
            System.out.println("Resp GETS: " + response);

            //Read server details
            dout.write(("OK\n").getBytes());

            // Storing Servers as objects
            for(int a=0;a<numServers;a++){
                response = din.readLine();
                serverList2.add(new ServerObj(response));
                System.out.println("serverList2: " + serverList2.toString());
                if(serverList2.size() == 1){
                    largestServer2 = serverList2.get(0);
                }else{
                    //Compare cpu cores
                    if(serverList2.get(serverList2.size()-1).core > largestServer2.core){
                        System.out.println("New Largest: " + response);
                        largestServer2 = serverList2.get(serverList2.size()-1);
                    }
                }
                System.out.println("Resp: " + response);
            }
            // Find all servers with same server type as the largest server
            for(ServerObj server: serverList2){
                if(server.type.equals(largestServer2.type)){
                    largestServerList2.add(server);
                }
            }

            //Confirm Received Server list
            dout.write(("OK\n").getBytes());
            response = din.readLine();
            System.out.println("Response OK: " + response);
            dout.flush();
            // Storing Servers as strings
            // for(int a=0;a<numServers;a++){
            //     response = din.readLine();
            //     serverList.add(response);
            //     if(serverList.indexOf(response) == 0){
            //         largestServer = response;
            //     }else{
            //         //Compare cpu cores
            //         if(Integer.parseInt(response.split(" ")[4]) > Integer.parseInt(largestServer.split(" ")[4])){
            //             System.out.println("New Largest: " + response);
            //             largestServer = response;
            //         }
            //     }
            //     System.out.println("Resp: " + response);
            // }
            // // Find all servers with same server type as the largest server
            // for(String server: serverList){
            //     if(server.split(" ")[0].equals(largestServer.split(" ")[0])){
            //         largestServerList.add(server);
            //     }
            // }

            // //Confirm Received Server list
            // dout.write(("OK\n").getBytes());
            // response = din.readLine();
            // System.out.println("Response OK: " + response);
            // dout.flush();
        } catch (Exception e) {
            System.out.println("Failed to get server list");
        }
        return largestServerList2;
    }
    public static void main(String[] args) { // TO RUN SERVER USE COMMAND "./ds-server -c ../../configs/sample-configs/ds-sample-config01.xml -v all -n"
        try {
            Socket s = new Socket("localhost", 50000); // Is this declared properly?
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());


            // HELO
            dout.write(("HELO\n").getBytes());
            String response = new String();
            response = din.readLine();
            dout.flush();
            System.out.println("Response: " + response);
            // Auth
            String username = System.getProperty("user.name");
            dout.write(("AUTH " + username+"\n").getBytes());
            response = din.readLine();
            System.out.println("Response: " + response);
            dout.flush();

            // REDY
            dout.write(("REDY\n").getBytes());
            response = din.readLine();
            System.out.println("Response REDY: " + response);
            dout.flush();

            //Get List of Largest Servers
            ArrayList<ServerObj> listLargestServers = largestServers(din, dout);
            int count = 0;
            String[] job;

            //Schedule jobs to largest Servers (Servers as Objects)
            while(!response.contains("NONE")){
                if(response.contains("JOBN")){
                    job = response.split(" ");
                    System.out.println("Job: " + response);
                    dout.write(("SCHD " + job[2] + " " + listLargestServers.get(count).type + " " +listLargestServers.get(count).id +"\n").getBytes());
                    response = din.readLine();
                    System.out.println("Response SCHD: " + response);
                    dout.flush();

                    // Increment through server list
                    if(count+1 >= listLargestServers.size()){
                        count = 0;
                    }else{
                        count++;
                    }
                }
                // get new Job
                dout.write(("REDY\n").getBytes());
                response = din.readLine();
                System.out.println("Response REDY: " + response);
                dout.flush();
            }
            //Schedule jobs to largest Servers (Servers as Strings)
            // while(!response.contains("NONE")){
            //     if(response.contains("JOBN")){
            //         job = response.split(" ");
            //         System.out.println("Job: " + response);
            //         dout.write(("SCHD " + job[2] + " " + listLargestServers.get(count).split(" ")[0] + " " +listLargestServers.get(count).split(" ")[1] +"\n").getBytes());
            //         response = din.readLine();
            //         System.out.println("Response SCHD: " + response);
            //         dout.flush();

            //         // Increment through server list
            //         if(count+1 >= listLargestServers.size()){
            //             count = 0;
            //         }else{
            //             count++;
            //         }
            //     }
            //     // get new Job
            //     dout.write(("REDY\n").getBytes());
            //     response = din.readLine();
            //     System.out.println("Response REDY: " + response);
            //     dout.flush();
            // }

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
}
