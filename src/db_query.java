import java.sql.*;

class db_query {

    Statement statement = null;
    
    public db_query() {     //构造函数
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        // URL指向要访问的数据库名user
        String url = "jdbc:mysql://127.0.0.1:3306/socket_app";
        // MySQL配置时的用户名
        String user = "mysocket"; 
        // MySQL配置时的密码
        String password = "mysocket";
        try { 
            // 加载驱动程序
            Class.forName(driver);

            // 连接数据库
            Connection conn = DriverManager.getConnection(url, user, password);

            if(!conn.isClosed()) 
            System.out.println("Succeeded connecting to the Database!");

            // statement用来执行SQL语句
            statement = conn.createStatement();
            // statement = conn.createStatement( ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,ResultSet.HOLD_CURSORS_OVER_COMMIT);
        }
        catch (ClassNotFoundException CNFE) {
            System.out.println(CNFE.toString());
        }
        catch (SQLException sqle) {
            System.out.println(sqle.toString());
        }
    }
    public ResultSet SQLExecute(String query) {
        ResultSet rs = null;
        try {
            rs=statement.executeQuery(query) ;   
        }
        catch (SQLException sqle1) {
            System.out.println(sqle1.toString());
        }
        return rs;
    }
    public int SQLUpdate(String sql) {
        int i = 0;
        try {
            i=statement.executeUpdate(sql);
        }
        catch (SQLException sqle2) {
            System.out.println("cry... something wrong.."+sqle2.toString());
        }
        return i;
    }
}

