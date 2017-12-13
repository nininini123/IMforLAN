import java.sql.*;

class db_query {

    Statement statement = null;
    
    public db_query() {     //���캯��
        //����������
        String driver = "com.mysql.jdbc.Driver";
        // URLָ��Ҫ���ʵ����ݿ���user
        String url = "jdbc:mysql://127.0.0.1:3306/socket_app";
        // MySQL����ʱ���û���
        String user = "mysocket"; 
        // MySQL����ʱ������
        String password = "mysocket";
        try { 
            // ������������
            Class.forName(driver);

            // �������ݿ�
            Connection conn = DriverManager.getConnection(url, user, password);

            if(!conn.isClosed()) 
            System.out.println("Succeeded connecting to the Database!");

            // statement����ִ��SQL���
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

