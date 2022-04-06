import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyClient {
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

            while(!response.contains("NONE")){

            // REDY
            dout.write(("REDY\n").getBytes());
            response = din.readLine();
            System.out.println("Response REDY: " + response);
            dout.flush();

            if(response.contains("JOBN")){
            // GETS
            String job[] = response.split(" ");
            // System.out.println("Job[] " + job[0]);
            while(!response.contains("DATA")){
                dout.write(("GETS Capable "+job[4]+" "+job[5]+" "+job[6]+"\n").getBytes());
                response = din.readLine();
                System.out.println("Response: " + response);
                dout.flush();
            }
            
            // OK
            ArrayList<String> servers = new ArrayList<String>();
            dout.write(("OK\n").getBytes());
            String data[] = response.split(" ");
            for(int a=0; a<Integer.parseInt(data[1]); a++){
                response = din.readLine();
                System.out.println("A: " + data[1]);
                System.out.println("Response in loop: " + response);
                servers.add(response);
            }
            dout.flush();

            System.out.println("Confirm after server loop");

            //Confirm server names received
            dout.write(("OK\n").getBytes());
            dout.flush();

            //Find largest and schedule it
            //Determine largest server based on core count which is 4 for joon 0 and 16 for super-silk
            String largestServer = servers.get(0);
            // System.out.println("Servers Check " + servers.toString());
            for(String serv: servers){
                if(Integer.parseInt(serv.split(" ")[4]) > Integer.parseInt(largestServer.split(" ")[4]) && serv.split(" ")[2].equals("inactive")){
                    System.out.println("New Largest: " + serv);
                    largestServer = serv;
                }
            }
            dout.write(("SCHD " + job[2] + " " + largestServer.split(" ")[0] + " " +largestServer.split(" ")[1] +"\n").getBytes());
            response = din.readLine();
            System.out.println("Response SCHD: " + response);
            dout.flush();
            }
            }


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
