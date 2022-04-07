public class ServerObj{
    String type, state;
    Integer id, curStartTime, core, memory, disk;

    public ServerObj(String server){
        String[] s = server.split(" ");
        type = s[0];
        id = Integer.parseInt(s[1]);
        state = s[2];
        curStartTime = Integer.parseInt(s[3]);
        core = Integer.parseInt(s[4]);
        memory = Integer.parseInt(s[5]);
        disk = Integer.parseInt(s[6]);
    }
}
