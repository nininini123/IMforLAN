import java.io.Serializable;
class encrypt_info extends Object implements Serializable {
    // private static final long serialVersionUID = 1L;
    final static String LOG_IN = "1";
    final static String SIGN_UP = "2";
    final static String FIND_PWD = "3";
    private  String InfoType;
    private String name;
    private String password;
    private  String tel;

    //constructor
    public encrypt_info() {
        
    }
    
    // public encrypt_info(String info_type) {
    //     this.InfoType = info_type;
    // }
    
    public encrypt_info(String str_array) {
        String[] str = str_array.split(" ");
        if (str.length==4) {
            this.setInfoType(str[0]);
            this.setName(str[1]);
            this.setTel(str[2]);
            this.setPassword(str[3]);
        }
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
    public String getTel() {
        return tel;
    }
    public String getInfoType() {
        return InfoType;
    }
    
    public void setInfoType(String infotype) {
        this.InfoType = infotype;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setTel(String teli) {
        this.tel = teli;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}