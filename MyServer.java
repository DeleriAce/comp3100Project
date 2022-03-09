import java.io.*;
import java.net.*;
public class MyServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(6666);
            Socket s = ss.accept(); //Establishes connection
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String str = (String)dis.readUTF(); //Doesn't work with ds use printwriter instead
            System.out.println("message= "+str);
            ss.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
