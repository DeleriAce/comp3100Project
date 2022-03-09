import java.io.*;
import java.net.*;
public class MyClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 6666);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("Hello Server"); //Doesn't work with ds use printWriter instead
            dout.flush();
            dout.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}