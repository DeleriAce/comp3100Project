import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClient {

    public static ArrayList<String> largestServers(DataInputStream din, DataOutputStream dout){
        ArrayList<String> serverList = new ArrayList<String>();
        ArrayList<String> largestServerList = new ArrayList<String>();
        String response = "";
        int numServers;
        String largestServer = "";
        try {
            //Get number of servers
            dout.write(("GETS All\n").getBytes());
            response = din.readLine();
            numServers = Integer.parseInt(response.split(" ")[1]);
            System.out.println("Resp GETS: " + response);

            //Read server details
            dout.write(("OK\n").getBytes());
            for(int a=0;a<numServers;a++){
                response = din.readLine();
                serverList.add(response);
                if(serverList.indexOf(response) == 0){
                    largestServer = response;
                }else{
                    //Compare cpu cores
                    if(Integer.parseInt(response.split(" ")[4]) > Integer.parseInt(largestServer.split(" ")[4])){
                        System.out.println("New Largest: " + response);
                        largestServer = response;
                    }
                }
                System.out.println("Resp: " + response);
            }
            // Find all servers with same server type as the largest server
            for(String server: serverList){
                if(server.split(" ")[0].equals(largestServer.split(" ")[0])){
                    largestServerList.add(server);
                }
            }

            //Confirm Received Server list
            dout.write(("OK\n").getBytes());
            response = din.readLine();
            System.out.println("Response OK: " + response);
            dout.flush();
        } catch (Exception e) {
            System.out.println("Failed to get server list");
        }
        return largestServerList;
    }
    public static void main(String[] args) { // TO RUN SERVER USE COMMAND "./ds-server -c ../../configs/sample-configs/ds-sample-config01.xml -v all -n"
        try {
            Socket s = new Socket("localhost", 50000); // Is this declared properly?
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            // BufferedReader in = new BufferedReader(new
            // InputStreamReader(s.getInputStream()));

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
            ArrayList<String> listLargestServers = largestServers(din, dout);
            int count = 0;
            String[] job;
            //Schedule jobs to largest Servers
            while(!response.contains("NONE")){
                if(response.contains("JOBN")){
                    job = response.split(" ");
                    System.out.println("Job: " + response);
                    dout.write(("SCHD " + job[2] + " " + listLargestServers.get(count).split(" ")[0] + " " +listLargestServers.get(count).split(" ")[1] +"\n").getBytes());
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
            
            // while(!response.contains("NONE")){

            //     // REDY
            //     dout.write(("REDY\n").getBytes());
            //     response = din.readLine();
            //     System.out.println("Response REDY: " + response);
            //     dout.flush();

                

            //     if(response.contains("JOBN")){
            //         // GETS
            //         String job[] = response.split(" ");
            //         // System.out.println("Job[] " + job[0]);
            //         // while(!response.contains("DATA")){
            //             dout.write(("GETS Capable "+job[4]+" "+job[5]+" "+job[6]+"\n").getBytes());
            //             response = din.readLine();
            //             System.out.println("Response: " + response);
            //             dout.flush();
            //         // }
                    
            //         // OK
            //         ArrayList<String> servers = new ArrayList<String>();
            //         dout.write(("OK\n").getBytes());
            //         String data[] = response.split(" ");
            //         for(int a=0; a<Integer.parseInt(data[1]); a++){
            //             response = din.readLine();
            //             System.out.println("A: " + data[1]);
            //             System.out.println("Response in loop: " + response);
            //             servers.add(response);
            //         }
            //         dout.flush();

            //         //Confirm server names received
            //         dout.write(("OK\n").getBytes());
            //         dout.flush();

            //         //Find largest and schedule it
            //         //Determine largest server based on core count which is 4 for joon 0 and 16 for super-silk
            //         String largestServer = servers.get(0);
            //         // System.out.println("Servers Check " + servers.toString());

            //         // Additional code to check active " && serv.split(" ")[2].equals("inactive")"
            //         for(String serv: servers){
            //             System.out.println("Contender: " + serv);
            //             // System.out.println("Server " + serv.split(" ")[0] + " count: "+Integer.parseInt(serv.split(" ")[4])+", Server " + largestServer.split(" ")[0]+ " count: "+Integer.parseInt(largestServer.split(" ")[4]));
            //             if(Integer.parseInt(serv.split(" ")[4]) > Integer.parseInt(largestServer.split(" ")[4])){
            //                 System.out.println("New Largest: " + serv);
            //                 largestServer = serv;
            //             }
            //         }
            //         dout.write(("SCHD " + job[2] + " " + largestServer.split(" ")[0] + " " +largestServer.split(" ")[1] +"\n").getBytes());
            //         response = din.readLine();
            //         System.out.println("Response SCHD: " + response);
            //         dout.flush();

            //         //Waits for server confirmation
            //         while(response.contains(".")){
            //             System.out.println("Ok then");
            //             response = din.readLine();
            //         }
            //     }
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
