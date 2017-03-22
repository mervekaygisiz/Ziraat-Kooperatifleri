package vt_lab_proje;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;

class TDPanel extends JPanel{
    private final JLabel inputLbl  = new JLabel(" ");
    private final JTextField txt    = new JTextField(10);
    private final JButton btn      = new JButton("Kapat");
    private final JButton btn15     = new JButton("Kredi adina gore filtrele.");
    private final JButton btn16     = new JButton("Sirket vergi noya filtrele.");
    private final JLabel outputLbl  = new JLabel(" ");
    
    String[] insTitle = new String[] {"Ciftci ekle.", "Kredi ekle.",
                                    "Urun ekle.", "Sirket ekle."};
    private final JComboBox<String> insertler = new JComboBox<String>(insTitle);
    
    String[] gosTitle = new String[] {"Ciftci goster.", "Kredi goster.",
                                    "Urun goster.", "Sirket goster."};
    private final JComboBox<String> goster = new JComboBox<String>(gosTitle);    
    
    String[] fonkTitle = new String[] {"Ciftci ortalama maas bul.", "Sirketin verdigi toplam urun miktari hesapla.",
                                    "Kredi odemesi biten kisiden krediyi siler."};
    
    private final JComboBox<String> fonk = new JComboBox<String>(fonkTitle); 
    
    String[] filtTitle = new String[] {"Urunu mevsimine gore filtrele.", "Ayni memleketten olan cifcileri filtrele.",
                                    "Kredi suresine gore filtrele.", "Sirket ismine gore filtrele."};
    private final JComboBox<String> filtre = new JComboBox<String>(filtTitle);
    
    private final DefaultTableModel tablo;

    private Connection conne;
    
    public TDPanel(Connection conn){
	Object[] columnNames = {"Tablo"};
	tablo = new DefaultTableModel(columnNames, 0);
	JTable tbl = new JTable(tablo);
	JScrollPane sp = new JScrollPane(tbl);	
        Font font = new Font("Verdana", Font.ITALIC | Font.BOLD ,12);

        add(txt);
        add(inputLbl);
        add(insertler);
        insertler.setToolTipText("Istenilen tabloya ekleme yapmasi icin ek ekranlar cikarir.");
        add(goster);
        goster.setToolTipText("Secilen tablodaki tum verileri gosterir.");
        add(fonk);
        fonk.setToolTipText("<html>Ciftci ortalama maas bul.: Ciftci tablosundaki ortalama maasi hesaplar.<br>"
                + "Sirketin verdigi toplam urun miktari hesapla.: Yazi kutusundan<br>"
                + "alinan bir sirket vergi numarasina gore toplam urun miktari hesaplar.<br>"
                + "Kredi odemesi biten kisiden krediyi siler.: Tc kimlik numarasi girilen ciftcinin<br> "
                + "kredi odemesi bittiyse onu kredisiz olarak gunceller.</html>");
                
         add(filtre);
         filtre.setToolTipText("<html>Verilen sekillerde kullanicidan girdi alarak <br> "
                 + "tablolari filtreler.<br>" + 
                 "Not: Yil ve ay bilgisi 1 Yil / 2 Ay seklinde belirtilerek girilmelidir.</html>");
         
		
       // btn1.setForeground(Color.black);
        //btn1.setBackground(Color.cyan);	
        add(btn);
	add(outputLbl); 
        add(sp);
	//tablo.setSize(200, 500);
	conne = conn;
		
        insertler.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                JComboBox<String> ins = (JComboBox<String>) arg0.getSource();
                String selected = (String) ins.getSelectedItem();
 
                if (selected.equals("Urun ekle.")) {
                    ekleU();
                }else if (selected.equals("Kredi ekle.")) {
                    ekleK();
                }else if (selected.equals("Ciftci ekle.")) {
                    ekleC();
                }else if (selected.equals("Sirket ekle.")) {
                    ekleS();
                }     
            }
        });
        
        goster.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                JComboBox<String> gos = (JComboBox<String>) arg0.getSource();
                String selected = (String) gos.getSelectedItem();
 
                if (selected.equals("Urun goster.")) {
                    gosterU();
                }else if (selected.equals("Kredi goster.")) {
                    gosterK();
                }else if (selected.equals("Ciftci goster.")) {
                    gosterC();
                }else if (selected.equals("Sirket goster.")) {
                    gosterS();
                }     
            }
        });
        
        fonk.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                JComboBox<String> f = (JComboBox<String>) arg0.getSource();
                String selected = (String) f.getSelectedItem();
 
                if (selected.equals("Ciftci ortalama maas bul.")) {
                    ortMaas();
                }else if (selected.equals("Sirketin verdigi toplam urun miktari hesapla.")) {
                    sirketUrunMik();
                }else if (selected.equals("Kredi odemesi biten kisiden krediyi siler.")) {
                    krediSil();
                }     
            }
        });
        
        filtre.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                JComboBox<String> fo = (JComboBox<String>) arg0.getSource();
                String selected = (String) fo.getSelectedItem();
 
                if (selected.equals("Ayni memleketten olan cifcileri filtrele.")) {
                    System.out.println(".actionPerformed()");
                    filtreleC();
                }else if (selected.equals("Urunu mevsimine gore filtrele.")) {
                    filtreleU();
                }else if (selected.equals("Kredi suresine gore filtrele.")) {
                    filtreleK();
                }else if (selected.equals("Sirket ismine gore filtrele.")) {
                    filtreleS();
                }   
            }
        });
        
	btn.addActionListener(new ActionListener(){			
            public void actionPerformed(ActionEvent arg0){
		try{
                    conne.close();
		}catch(SQLException e){
                    e.printStackTrace();
		}
		System.exit(0);
            }			 
	});	
    }
    private void krediSil() {
        String tc = txt.getText();
        Object[] colname={"Sirket Urun Miktari"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();      
        String query2 = "SELECT krediBittiMi('" + tc + "')";
        PreparedStatement p2;
        
        try{	
            p2 = conne.prepareStatement(query2);
            ResultSet r2 = p2.executeQuery();   
            String uyari = "Istenen kisi kontrol edildi ve geregi yapildi.";       
            Object[] satir ={ uyari };
	    tablo.addRow(satir);         
	    p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}
    }
    
    private void filtreleS()  {
        String ad = txt.getText();
        Object[] colname={"VergiNo", "Ad"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();      
        
        String query2 = "SELECT * FROM sirket WHERE ad = '" + ad +"'";
        PreparedStatement p2;
        
        try{	
		p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
                
                while(r2.next()){
                   String vno = r2.getString(1);
                   String adi = r2.getString(2);
                   
                   
                   Object[] satir ={ vno, adi };
		   tablo.addRow(satir);
                }   
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}
    }
    
    private void filtreleK()  {
        String sure = txt.getText();
        Object[] colname={"KrediNo", "KrediAdi", "KrediSuresi", "KrediIcerigi", "Miktari"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();      
        
        String query2 = "SELECT * FROM kredi WHERE kredisuresi = '" + sure +"'";
        PreparedStatement p2;
        
        try{	
		p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
                
                while(r2.next()){
                   String no = r2.getString(1);
                   String ad = r2.getString(2);
                   String ks = r2.getString(3);
                   String ki = r2.getString(4);
                   String mik = r2.getString(5);
                   
                   Object[] satir ={ no, ad, ks, ki, mik };
		   tablo.addRow(satir);
                }   
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}
    }
    
    private void filtreleU()  {
        String mevsim = txt.getText();
        Object[] colname={"UrunId", "TeminEdilenSirket", "Ad", "Mevsim", "Miktar"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();      
        
        String query2 = "SELECT * FROM urun WHERE mevsim = '" + mevsim +"'";
        PreparedStatement p2;
        
        try{	
		p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
                
                while(r2.next()){
                   String id = r2.getString(1);
                   String tes = r2.getString(2);
                   String ad = r2.getString(3);
                   String mev = r2.getString(4);
                   String mik = r2.getString(5);
                   
                   Object[] satir ={ id, tes, ad, mev, mik };
		   tablo.addRow(satir);
                }   
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}
    }
    
    private void filtreleC()  {
        String memleket = txt.getText();
        Object[] colname={"TcKimlik", "AktifKrediId", "Ad", "Soyad", "DTarihi", "BolgeBilgisi", "Maas"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();
	
        String query2 = "SELECT * FROM ciftci WHERE bolgebilgisi = '" + memleket +"'";
        PreparedStatement p2;
        try{				
		p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
				
		while (r2.next()){
		    String tckim = r2.getString(1);
                    String name = r2.getString(2);      	
		    String lname = r2.getString(3);
		    String tarih= r2.getString(4);
		    String bolge = r2.getString(5);
		    String maas = r2.getString(6);
		    String id = r2.getString(7);
		            
		    Object[] satir ={tckim, name, lname, tarih, bolge, maas, id};
		    tablo.addRow(satir);
		}
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}
    }
    
    private void ortMaas()  {
        Object[] colname={"Ortalama Maas"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();      
        
        try{				
		String query2 = "SELECT ort_maas()";
		PreparedStatement p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
                
                while(r2.next()){
                   String maas = r2.getString(1);
                   
                   Object[] satir ={ maas };
		   tablo.addRow(satir);
                }   
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}
    }
    
    private void sirketUrunMik()  {
        String vno = txt.getText();
        Object[] colname={"Sirket Urun Miktari"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();      
        String query2 = "SELECT urunMik('" + vno + "')";
        PreparedStatement p2;
        
        try{	
		p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
                
                while(r2.next()){
                   String urunMik = r2.getString(1);
                   
                   Object[] satir ={ urunMik };
		   tablo.addRow(satir);
                }   
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}
    }
    
    private void gosterC()  {
        Object[] colname={"TcKimlik", "AktifKrediId", "Ad", "Soyad", "DTarihi", "BolgeBilgisi", "Maas"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();
	
        try{				
		String query2 = "SELECT * FROM ciftci";
		PreparedStatement p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
				
		while (r2.next()){
		    String tckim = r2.getString(1);
                    String name = r2.getString(2);      	
		    String lname = r2.getString(3);
		    String tarih= r2.getString(4);
		    String bolge = r2.getString(5);
		    String maas = r2.getString(6);
		    String id = r2.getString(7);
		            
		    Object[] satir ={tckim, name, lname, tarih, bolge, maas, id};
		    tablo.addRow(satir);
		}
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}				    
    }
	
    private void ekleC()  {
        
        int a = JOptionPane.showConfirmDialog(inputLbl, "Ekleme haftasonu olmuyor haberiniz olsun.");
        if(a==JOptionPane.YES_OPTION){
            System.exit(0);
        }else{
	String tc       = JOptionPane.showInputDialog("Ciftci TC'si giriniz.");
	String ad    = JOptionPane.showInputDialog("Ciftci adini giriniz.");
	String soyad      = JOptionPane.showInputDialog("Ciftci soyadini giriniz.");
	String trh   = JOptionPane.showInputDialog("Ciftci dogum tarihini DD/MON/YYYY formatinda giriniz.");
	String blg  = JOptionPane.showInputDialog("Ciftci bolgesini giriniz");
	String kredi      = JOptionPane.showInputDialog("Ciftci varsa kredi id 'krediid' formatinda yoksa null olarak giriniz.");
	String maas     = JOptionPane.showInputDialog("Ciftci maasini giriniz.");
	
		
	String query = "INSERT INTO ciftci(tckimlik, aktifkrediid, isim, soyisim, dogumtarihi, bolgebilgisi, maas)" +
                       "VALUES( '" + tc + "', " + kredi + " , '" + ad + "' ,'" + soyad + "', '" + trh + 
                       "', '" + blg + "', " + maas + " )";
                
        System.out.println(query);
        
	Statement s = null;
       
        try {
            s = conne.createStatement();
            s.executeUpdate(query);
            conne.setAutoCommit(false);
            conne.commit();
            s.close();
	}catch(SQLException e){
            e.printStackTrace();
	}	
        }
    }
    
    private void gosterK()  {
        Object[] colname={"KrediNo", "KrediAdi", "KrediSuresi", "KrediIcerigi", "Miktari", "SonOdeme"};
        tablo.setColumnIdentifiers(colname);        
	tablo.getDataVector().removeAllElements();
        try{
				
		String query2 = "SELECT * FROM kredi";
		PreparedStatement p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
				
		while (r2.next()){
		    String kno = r2.getString(1);
                    String adi = r2.getString(2);      	
		    String sure = r2.getString(3);
		    String icerik = r2.getString(4);
		    String miktar = r2.getString(5);
                    String son =r2.getString(6);
		            
		    Object[] satir ={kno, adi, sure, icerik, miktar, son};
		    tablo.addRow(satir);
		}
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}				
    }
	
    private void ekleK()  {

	String kno   = JOptionPane.showInputDialog("Kredi no giriniz.");
	String adi    = JOptionPane.showInputDialog("Kredi adini giriniz.");
	String sure      = JOptionPane.showInputDialog("Kredi suresini giriniz.");
	String icerik   = JOptionPane.showInputDialog("Kredi icerigini giriniz.");
	String miktar  = JOptionPane.showInputDialog("Kredi miktarini giriniz");
        String sonodeme =JOptionPane.showInputDialog("Son odeme tarihi giriniz");
	
		
	String query = "INSERT INTO kredi(kredino, krediadi, kredisuresi, krediicerigi, miktari, sonodeme)" +
                       "VALUES( '" + kno + "', '" + adi + "', '" + sure + "' ,'" + icerik + "', '" + miktar + "', '"+ sonodeme +"')";
                
        System.out.println(query);
        
	Statement s = null;
       
        try {
            s = conne.createStatement();
            s.executeUpdate(query);
            conne.setAutoCommit(false);
            conne.commit();
            s.close();
	}catch(SQLException e){
            e.printStackTrace();
	}	
    }
    
    private void gosterS()  {
        Object[] colname={"VergiNo", "Ad"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();
	
        try{
				
		String query2 = "SELECT * FROM sirket";
		PreparedStatement p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
				
		while (r2.next()){
		    String vno = r2.getString(1);
                    String ad = r2.getString(2);      	
		    
		            
		    Object[] satir ={vno, ad};
		    tablo.addRow(satir);
		}
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}				
    }
	
    private void ekleS()  {
        
	String vno    = JOptionPane.showInputDialog("Sirket vergi no giriniz.");
	String ad    = JOptionPane.showInputDialog("Sirket adini giriniz.");
	
	
		
	String query = "INSERT INTO sirket(vergino, ad)" +
                       "VALUES( '" + vno + "', '" + ad + "' )";
                
        System.out.println(query);
        
	Statement s = null;
       
        try {
            s = conne.createStatement();
            s.executeUpdate(query);
            conne.setAutoCommit(false);
            conne.commit();
            s.close();
	}catch(SQLException e){
            e.printStackTrace();
	}	
    }
    
    private void gosterU()  {
        Object[] colname={"UrunId", "TeminEdilenSirket", "Ad", "Mevsim", "Miktar"};
        tablo.setColumnIdentifiers(colname);
        tablo.getDataVector().removeAllElements();
	
        try{
				
		String query2 = "SELECT * FROM urun";
		PreparedStatement p2 = conne.prepareStatement(query2);
		ResultSet r2 = p2.executeQuery();
				
		while (r2.next()){
		    String id = r2.getString(1);
                    String tes = r2.getString(2);      	
		    String ad = r2.getString(3);
		    String mvs= r2.getString(4);
		    String mik = r2.getString(5);
		    
		            
		    Object[] satir ={id, tes, ad, mvs, mik};
		    tablo.addRow(satir);
		}
		p2.close();
	}catch(SQLException e){
            e.printStackTrace();
	}				
    }
	
    private void ekleU()  {
        
	String id      = JOptionPane.showInputDialog("Urun id giriniz.");
	String tes    = JOptionPane.showInputDialog("Urunu temin eden sirketin vergi no giriniz.");
	String ad     = JOptionPane.showInputDialog("Urun adini giriniz.");
	String mev   = JOptionPane.showInputDialog("Urun mevsimini giriniz.");
	String mik  = JOptionPane.showInputDialog("Urun miktarini giriniz");
	
		
	String query = "INSERT INTO urun(urunid, teminedilensirket, ad, mevsim, miktar)" +
                       "VALUES( '" + id + "', '" + tes + "', '" + ad + "' ,'" + mev + "', " + mik + 
                       " )";
                
        System.out.println(query);
        
	Statement s = null;
       
        try {
            s = conne.createStatement();
            s.executeUpdate(query);
            conne.setAutoCommit(false);
            conne.commit();
            s.close();
	}catch(SQLException e){
            e.printStackTrace();
	}	
    }
    
}