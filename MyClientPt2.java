import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClientPt2 {
    public static void main(String[] args) { // TO RUN SERVER USE COMMAND "./ds-server -c
                                             // ../../configs/sample-configs/ds-sample-config01.xml -v all -n"
        try {
            Socket s = new Socket("localhost", 50000);
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            String response = new String();

            // Run handshake protocol
            response = handshake(din, dout);

            // Run Scheduling Algorithm
            response = Algorithm_BF(din, dout);

            // Exit
            dout.write(("QUIT\n").getBytes());
            response = din.readLine();
            dout.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String handshake(DataInputStream din, DataOutputStream dout) {
        String response = new String();
        try {
            // HELO
            dout.write(("HELO\n").getBytes());
            response = din.readLine();
            dout.flush();
            // Auth
            String username = System.getProperty("user.name");
            dout.write(("AUTH " + username + "\n").getBytes());
            response = din.readLine();
            dout.flush();
        } catch (Exception e) {
            System.out.println("Failed Handshake Protocol");
        }
        return response;
    }

    // Implementation of slightly modified BF algorithm
    public static String Algorithm_BF(DataInputStream din, DataOutputStream dout) {
        String response = "";
        String[] job;
        Integer num_servers;
        ArrayList<ServerObj> server_list;
        ArrayList<ServerObj> server_listOG;
        try {
            // Get JOBN
            dout.write(("REDY\n").getBytes());
            response = din.readLine();
            job = response.split(" ");
            dout.flush();

            // Get All servers
            dout.write(("GETS All\n").getBytes());
            response = din.readLine();
            num_servers = Integer.parseInt(response.split(" ")[1]);
            dout.flush();

            dout.write(("OK\n").getBytes());

            //Get OG server list used as fallback list if all Servers are busy
            server_listOG = Get_ServerList(din, response, num_servers);
            dout.write(("OK\n").getBytes());
            response = din.readLine();
            dout.flush();

            //Loop to schedule all jobs
            while (!response.contains("NONE")) {
                // Get JOBN
                dout.write(("REDY\n").getBytes());
                response = din.readLine();

                if (response.contains("JOBN")) {
                    job = response.split(" ");
                    dout.flush();

                    // Get All servers in their current state
                    dout.write(("GETS All\n").getBytes());
                    response = din.readLine();
                    num_servers = Integer.parseInt(response.split(" ")[1]);
                    dout.flush();

                    dout.write(("OK\n").getBytes());

                    server_list = Get_ServerList(din, response, num_servers);
                    dout.write(("OK\n").getBytes());
                    response = din.readLine();
                    dout.flush();
                    Boolean scheduled = false;

                    // SCHD Job with first server with enough resources starting half-way through the array to improve resource utilisation
                    for (int a = server_list.size()/2; a < server_list.size(); a++) {
                        if (server_list.get(a).core() >= Integer.parseInt(job[4]) && server_list.get(a).memory() >= Integer.parseInt(job[5]) && server_list.get(a).disk() >= Integer.parseInt(job[6])) {
                            dout.write(("SCHD " + job[2] + " " + server_list.get(a).type() + " "
                                    + server_list.get(a).id() + "\n").getBytes());
                            response = din.readLine();
                            scheduled = true;
                            break;
                        }
                    }
                    // SCHD Job using OG list if all servers are currently occupied
                    if (scheduled == false) {
                        for (int a = server_listOG.size()/2; a < server_listOG.size(); a++) {
                            if (server_listOG.get(a).core() >= Integer.parseInt(job[4]) && server_listOG.get(a).memory() >= Integer.parseInt(job[5]) && server_listOG.get(a).disk() >= Integer.parseInt(job[6])) {
                                dout.write(("SCHD " + job[2] + " " + server_listOG.get(a).type() + " "
                                        + server_listOG.get(a).id() + "\n").getBytes());
                                response = din.readLine();
                                break;
                            }
                        }
                    }
                }

            } // End of While loop
        } catch (Exception e) {
            System.out.println("BF Algorithm Failed");
            System.out.println("Error: " + e);
        }
        return response;
    }

    public static ArrayList<ServerObj> Get_ServerList(DataInputStream din, String response, Integer num_servers) {
        ArrayList<ServerObj> server_listTemp = new ArrayList<ServerObj>();
        try {
            // Get list of servers
            for (int a = 0; a < num_servers; a++) {
                response = din.readLine();
                server_listTemp.add(new ServerObj(response));
            }
            // Sort list by core count ASC
            server_listTemp.sort((server1, server2) -> server1.core().compareTo(server2.core()));
        } catch (Exception e) {
            System.out.println("Failed to get server list" + e);
        }
        return server_listTemp;
    }
}
