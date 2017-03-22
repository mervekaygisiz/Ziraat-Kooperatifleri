package vt_lab_proje;

import java.sql.*;
import javax.swing.*;

public class TDFrame extends JFrame {
    public TDFrame (Connection conn) {

	setTitle("Kooperatif Bilgileri");
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setSize(750, 500);
	setLocation(200, 200);
	getContentPane().add(new TDPanel(conn));		
    }

    
}