package vt_lab_proje;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;

public class KoopInfo {
    
    public static void main (String args []) throws SQLException, IOException {
        String user, pass;
	user = "postgres";
        pass = "123456";
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/kooperatif", user,pass);
        
	JFrame frame = new TDFrame(conn);
	frame.setVisible(true);
    }
}