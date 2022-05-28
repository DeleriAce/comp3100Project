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
            response = Algorithm_FF(din, dout);

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

    // Implementation of FF algorithm
    public static String Algorithm_FF(DataInputStream din, DataOutputStream dout) {
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

            server_listOG = Get_ServerList(din, response, num_servers);
            dout.write(("OK\n").getBytes());
            response = din.readLine();
            dout.flush();
            while (!response.contains("NONE")) {
                // Get JOBN
                dout.write(("REDY\n").getBytes());
                response = din.readLine();

                if (response.contains("JOBN")) {
                    job = response.split(" ");
                    dout.flush();

                    // Get All servers
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

                    System.out.println("Pre SCHD: " + response);
                    // SCHD Job with first server with enough resources 
                    for (int a = 0; a < num_servers; a++) {
                        if (server_list.get(a).core() >= Integer.parseInt(job[4]) && server_list.get(a).memory() >= Integer.parseInt(job[5]) && server_list.get(a).disk() >= Integer.parseInt(job[6])) {
                            dout.write(("SCHD " + job[2] + " " + server_list.get(a).type() + " "
                                    + server_list.get(a).id() + "\n").getBytes());
                            response = din.readLine();
                            scheduled = true;
                            break;
                        }
                    }
                    if (scheduled == false) {
                        System.out.println("Backup");
                        for (int a = 0; a < num_servers; a++) {
                            if (server_listOG.get(a).core() >= Integer.parseInt(job[4]) && server_listOG.get(a).memory() >= Integer.parseInt(job[5]) && server_listOG.get(a).disk() >= Integer.parseInt(job[6])) {
                                dout.write(("SCHD " + job[2] + " " + server_listOG.get(a).type() + " "
                                        + server_listOG.get(a).id() + "\n").getBytes());
                                response = din.readLine();
                                break;
                            }
                        }
                    }
                    System.out.println("SCHD Response; "+response);
                }

            } // End of While loop
        } catch (Exception e) {
            System.out.println("FF Algorithm Failed");
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

    // Partial implementation of LRR in new code structure (has errors in test but
    // functions outside of that)
    public static String Algorithm_LRR(DataInputStream din, DataOutputStream dout) {
        // Get List of LRR Servers to be used
        ArrayList<ServerObj> listServersUsed = largestServers(din, dout);

        int count = 0;
        String[] job;
        String response = new String();
        try {
            // Schedule jobs to largest Servers
            while (!response.contains("NONE")) {
                dout.write(("REDY\n").getBytes());
                response = din.readLine();
                dout.flush();
                if (response.contains("JOBN")) {
                    job = response.split(" ");
                    fcServer(din, dout, job[4], job[5], job[6]);
                    dout.write(("SCHD " + job[2] + " " + listServersUsed.get(count).type() + " "
                            + listServersUsed.get(count).id() + "\n").getBytes());
                    response = din.readLine();
                    dout.flush();

                    // Increment through server list
                    if (count + 1 >= listServersUsed.size()) {
                        count = 0;
                    } else {
                        count++;
                    }
                }
                // get new Job
                // dout.write(("REDY\n").getBytes());
                // response = din.readLine();
                // System.out.println("Response REDY: " + response);
                // dout.flush();
            }
        } catch (Exception e) {
            System.out.println("Failed to run LRR algorithm");
            System.out.println("Error: " + e);
        }
        return response;
    }

    public static ArrayList<ServerObj> largestServers(DataInputStream din, DataOutputStream dout) {
        String response = "";
        int numServers;
        ArrayList<ServerObj> serverList2 = new ArrayList<ServerObj>();
        ArrayList<ServerObj> largestServerList2 = new ArrayList<ServerObj>();
        ServerObj largestServer2 = null;

        try {
            dout.write(("REDY\n").getBytes());
            response = din.readLine();
            dout.flush();
            // Get number of servers
            dout.write(("GETS All\n").getBytes());
            response = din.readLine();
            numServers = Integer.parseInt(response.split(" ")[1]);

            // Read server details
            dout.write(("OK\n").getBytes());

            // Storing Servers as objects
            for (int a = 0; a < numServers; a++) {
                response = din.readLine();
                serverList2.add(new ServerObj(response));
                if (serverList2.size() == 1) {
                    largestServer2 = serverList2.get(0);
                } else {
                    // Compare cpu cores
                    if (serverList2.get(serverList2.size() - 1).core() > largestServer2.core()) {
                        largestServer2 = serverList2.get(serverList2.size() - 1);
                    }
                }
            }
            // Find all servers with same server type as the largest server
            for (ServerObj server : serverList2) {
                if (server.type().equals(largestServer2.type())) {
                    largestServerList2.add(server);
                }
            }

            // Confirm Received Server list
            dout.write(("OK\n").getBytes());
            response = din.readLine();
            dout.flush();
        } catch (Exception e) {
            System.out.println("Failed to get server list");
            System.out.println("Error: " + e);
        }
        return largestServerList2;
    }

    // Gets the first capable
    public static void fcServer(DataInputStream din, DataOutputStream dout, String core, String memory,
            String diskSpace) {
        String response = "";
        try {
            dout.write(("GETS Capable " + core + " " + memory + " " + diskSpace + "\n").getBytes());
            response = din.readLine();
            dout.write(("OK\n").getBytes());
            dout.flush();
            dout.write(("OK\n").getBytes());
            dout.flush();
        } catch (Exception e) {
            System.out.println("Failed fcServer function");
        }

    }

    public static String Algorithm_FC(DataInputStream din, DataOutputStream dout) {
        String[] job;
        String response = new String();
        try {
            while (!response.contains("NONE")) {
                dout.write(("REDY\n").getBytes());
                response = din.readLine();
                dout.flush();
                if (response.contains("JOBN")) {
                    ArrayList<ServerObj> serverList = new ArrayList<ServerObj>();

                    job = response.split(" ");
                    dout.write(("GETS Capable " + job[4] + " " + job[5] + " " + job[6] + "\n").getBytes());
                    response = din.readLine();
                    Integer numServers = Integer.parseInt(response.split(" ")[1]);

                    dout.write(("OK\n").getBytes());

                    for (int a = 0; a < numServers; a++) {
                        response = din.readLine();
                        serverList.add(new ServerObj(response));
                    }
                    dout.write(("OK\n").getBytes());

                    response = din.readLine();
                    dout.write(("SCHD " + job[2] + " " + serverList.get(0).type() + " " + serverList.get(0).id() + "\n")
                            .getBytes());
                    response = din.readLine();
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to run FC algorithm");
            System.out.println("Error: " + e);
        }
        return response;
    }
}
