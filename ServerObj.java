public class ServerObj{
    private String serverType, serverState;
    private Integer serverID, serverCurrStartTime, serverCore, serverMemory, serverDisk;

    public ServerObj(String server){
        String[] s = server.split(" ");
        setType(s[0]);
        setID(Integer.parseInt(s[1]));
        setState(s[2]);
        setCurStartTime(Integer.parseInt(s[3]));
        setCore(Integer.parseInt(s[4]));
        setMemory(Integer.parseInt(s[5]));
        setDisk(Integer.parseInt(s[6]));
    }

    //Setters
    private void setType(String s){
        serverType = s;
    }
    private void setID(Integer i){
        serverID = i;
    }
    private void setState(String s){
        serverState = s;
    }
    private void setCurStartTime(Integer i){
        serverCurrStartTime = i;
    }
    private void setCore(Integer i){
        serverCore = i;
    }
    private void setMemory(Integer i){
        serverMemory = i;
    }
    private void setDisk(Integer i){
        serverDisk = i;
    }

    // Getters
    public String type(){
        return serverType;
    }
    public Integer id(){
        return serverID;
    }
    public String state(){
        return serverState;
    }
    public Integer currStartTime(){
        return serverCurrStartTime;
    }
    public Integer core(){
        return serverCore;
    }
    public Integer memory(){
        return serverMemory;
    }
    public Integer disk(){
        return serverDisk;
    }
}
