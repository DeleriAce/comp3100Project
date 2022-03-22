import java.io.*;
import java.net.*;
public class MyClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 50000);
            // DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //HELO
            dout.write(("HELO").getBytes());
            // String str = (String)br.readLine();
            // System.out.println("Received: " + str);
            dout.flush();

            //Auth
            String username = System.getProperty("user.name");
            dout.write(("AUTH "+username).getBytes());
            // str = (String)br.readLine();
            // System.out.println("Received: " + str);

            dout.flush();

            //Read xml and ready

            dout.write(("REDY").getBytes());
            // str = (String)br.readLine();
            // System.out.println("Received: " + str);
            dout.flush();

            dout.write(("QUIT").getBytes());
            // str = (String)br.readLine();
            // System.out.println("Received: " + str);
            
            dout.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
