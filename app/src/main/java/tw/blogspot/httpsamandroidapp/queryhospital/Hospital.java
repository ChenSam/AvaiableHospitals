package tw.blogspot.httpsamandroidapp.queryhospital;

public class Hospital {
    private int type;
    private String name;
    private String addr;
    private String phone;
    public Hospital(int type,String name,String addr, String phone) {
        this.type = type;
        this.name = name;
        this.addr = addr;
        this.phone = phone;
    }
    public int getType(){
        return type;
    }
    public void setType(int type){
        this.type = type;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getAddr(){
        return addr;
    }
    public void setAddr(String addr){
        this.addr = addr;
    }
    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}