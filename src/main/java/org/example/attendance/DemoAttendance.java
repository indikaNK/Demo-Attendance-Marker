package org.example.attendance;

import org.example.utility.DBConn;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DemoAttendance extends JFrame {

    private int eid; //employee ID
    private Date date;

    //add in time and outTime variables
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private JPanel panel1;
    private JTextField txtEid;
    private JButton btnAdd;
    private JTable showTable;
    private JButton displayDataButton;
    private JButton CLEARTABLEButton;

    Connection conn;
    PreparedStatement pst;

    public DemoAttendance() {

        connect();
        //meta data
        setContentPane(panel1);
        setTitle("Demo ATD App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null); //display in center
        setVisible(true);


//        demo table data
//        Object[][] data = {};
//        Object[][] data = {
//                {"0001","2023-10-01", 001, "Kiyas", "08:00", "6:00"},
//                {"0002","2023-10-01", 002, "Herath", "08:00", "6:00"},
//                {"0003","2023-10-01", 003, "Kumara", "08:00", "6:00"},
//
//        };

//        table
//        showTable.setModel(new DefaultTableModel(
//                data, new String[]{
//                 "ID",
//                "Date",
//                "Employee ID",
//                "Name",
//                "Arrival Time",
//                "Leave Time",
//        }
//        ));

        //column adjustments
//        TableColumnModel columnModel = showTable.getColumnModel();
//
//        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
//
//        //call each columns and set to center
//        columnModel.getColumn(0).setCellRenderer(centerRenderer);
//        columnModel.getColumn(1).setCellRenderer(centerRenderer);
//        columnModel.getColumn(2).setCellRenderer(centerRenderer);
//        columnModel.getColumn(3).setCellRenderer(centerRenderer);
//        columnModel.getColumn(4).setCellRenderer(centerRenderer);
//        columnModel.getColumn(4).setCellRenderer(centerRenderer);

        //load data to table
        showTable();


        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtEid.getText().isEmpty() || txtEid.getText() == "") {
                    JOptionPane.showMessageDialog(DemoAttendance.this, "Please Enter Employee ID...");
                    return;
                }
                try {
                    int employeeID = Integer.parseInt(txtEid.getText());
                    //throry - if this emp ID already exist and the date is same as todays date then just add the out time
                    //otherwise add a new record
                    PreparedStatement readpst = conn.prepareStatement("SELECT * FROM demo_table WHERE empID = ? AND date = ?");
                    readpst.setInt(1, employeeID);
                    readpst.setDate(2, getDate());
                    ResultSet resultset = readpst.executeQuery();

                    //check if result set is empty , if empty enter as new data otherwise update the out time
                    if (resultset.next()) {
                        JOptionPane.showMessageDialog(DemoAttendance.this, "Updating Your Leave Time");
                        updateOutTimeData(employeeID);
                    } else {
                        JOptionPane.showMessageDialog(DemoAttendance.this, "Welcome !!! ");
                        insertData(employeeID);
                    }


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }


            }
        });
        displayDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTable();
            }
        });
        CLEARTABLEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //clear table
                showTable.setModel(new DefaultTableModel());
            }
        });
    }
    //data base connection
    //db - attendancedb table-demo_table
    public void connect() {
        //connect to DB
        conn = DBConn.getConnection();
    }

    public void showTable() {

        try {
            PreparedStatement pst = conn.prepareStatement("SELECT DISTINCT ID,empID,name,date,timeIN,timeOut FROM demo_table");
            ResultSet rs = pst.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            DefaultTableModel dtm = (DefaultTableModel) showTable.getModel();

            int cols = rsmd.getColumnCount();
            String[] colNames = new String[cols];

            for (int i = 0; i < cols; i++) {
                colNames[i] = rsmd.getColumnName(i + 1);
            }

            dtm.setColumnIdentifiers(colNames);
            String id, empID, name, date, in, out;

            while (rs.next()) {
                id = rs.getString(1);
                empID = rs.getString(2);
                name = rs.getString(3);
                date = rs.getString(4);
                in = rs.getString(5);
                out = rs.getString(6);
                String[] row = {id, empID, name, date, in, out};
                dtm.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(DemoAttendance.this, "An error occurred while fetching data from the database: " + e.getMessage());
        }

    }

    public void insertData(int employeeID) throws SQLException {

        PreparedStatement pst = conn.prepareStatement("INSERT INTO demo_table(empID,name,date,timeIN,timeOut) VALUES (?,?,?,?,?)");

        //conditions select name based on emp ID

        pst.setInt(1, employeeID);
        if (employeeID == 1) {
            pst.setString(2, "kiyas");
        } else if (employeeID == 2) {
            pst.setString(2, "herath");

        } else if (employeeID == 3) {
            pst.setString(2, "kumara");

        } else if (employeeID == 4) {
            pst.setString(2, "nuwan");

        } else {
            JOptionPane.showMessageDialog(DemoAttendance.this, "Employee ID not Found!");
            return;

        }
        pst.setDate(3, getDate());
        pst.setString(4, getInTime());
        pst.setString(5, getInTime());

        int a = pst.executeUpdate();

//                    success will be a =1
        if (a == 1) {
            JOptionPane.showMessageDialog(DemoAttendance.this, "Record Added Successfully");
            showTable.setModel(new DefaultTableModel());
            showTable();
        } else {
            JOptionPane.showMessageDialog(DemoAttendance.this, "Record Adding Failed !");

        }
    }

    public void updateOutTimeData(int employeeID) throws SQLException {
        PreparedStatement pst = conn.prepareStatement("UPDATE demo_table SET timeOut = ? WHERE empID = ? AND date = ? ");

        //conditions select name based on emp ID

        pst.setString(1, getInTime());
        pst.setInt(2, employeeID);
        pst.setDate(3, getDate());

        int a = pst.executeUpdate();

//                    success will be a =1
        if (a == 1) {
            JOptionPane.showMessageDialog(DemoAttendance.this, "Record Updated Successfully");
            showTable.setModel(new DefaultTableModel());
            showTable();
        } else {
            JOptionPane.showMessageDialog(DemoAttendance.this, "Record Update Failed !");

        }
    }

    public static void main(String[] args) {
        new DemoAttendance();
    }

    public String getInTime() {
        LocalDateTime dateTime = LocalDateTime.now();

        //convert miliseconds and seconds to minuts to get exact time/ or remove all seconds miliseconds
        LocalDateTime roundFloor = dateTime.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime roundCeiling = dateTime.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);

        String tempTime = String.valueOf(roundCeiling.toLocalTime());
        System.out.println("IN-" + tempTime);
//        11:18:26.838822600
        return tempTime;
    }
    public java.sql.Date getDate() {
        LocalDate localDate = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        return sqlDate;
    }
}
