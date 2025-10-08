package com.pharmacyfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class CreateController implements Initializable {
    @FXML
    TextField txtdname;
    @FXML
    TextField txtduse;
    @FXML
    TextField txtdamount;
    @FXML
    DatePicker txtpurchase;
    @FXML
    TextField txtpharmId;
    @FXML
    Button btnclear;
    @FXML
    Button btncreate;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Setting the default date to the current day
        txtpurchase.setValue(LocalDate.now());

        btncreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String dname = txtdname.getText();
                String duse = txtduse.getText();
                String damount = txtdamount.getText();
                java.sql.Date dpurchase = java.sql.Date.valueOf(txtpurchase.getValue());
                //Note that with FX, there's less code involved. You don't have to turn it to a util then use getTime()
                String pharmId = txtpharmId.getText();//You can't parse it to an int without ensuring that it is not empty. If nothing is entered, parseInt will throw an error

                if(!dname.isEmpty() && !duse.isEmpty() && !damount.isEmpty() && dpurchase != null && !pharmId.isEmpty()){
                    //isEmpty() only works on text so with date, you just check if it is null or containing a value
                    //It also doesn't work for int so that's another reason why I've parsed it to an Integer after
                    int intpharmId = Integer.parseInt(pharmId);//Sometimes parseInt() can work alone but to be safe we specify the class it belongs to. Note: for Doubles:Double.parseDouble(), Floats:Float.parseFloat() etc
                    CreateRecord(dname,duse,damount,dpurchase,intpharmId);
                }else{
                    System.out.println("Empty fields detected");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failure Message");
                    alert.setHeaderText("Empty fields detected");
                    alert.setContentText("Please fill in all the fields " + Emojis.sadFace);
                    alert.showAndWait();
                }
            }
        });

        btnclear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                txtdname.setText("");
                txtduse.setText("");
                txtdamount.setText("");
                txtpurchase.setValue(LocalDate.now());//Returning it to default(Current day)
                txtpharmId.setText("");
            }
        });
    }

    public static void CreateRecord(String Dname, String Duse, String Damount, java.sql.Date Purchase, int pharmId){
        String query = "INSERT INTO drugs (dname, duse, damount, dpurchase, pharmacistId) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, Dname);
            stmt.setString(2,Duse);
            stmt.setString(3,Damount);
            stmt.setDate(4,Purchase);
            stmt.setInt(5,pharmId);

            int rows_inserted = stmt.executeUpdate();
            if(rows_inserted > 0){
                System.out.println("Record created successfully");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success Message");
                alert.setHeaderText("Successful Addition");
                alert.setContentText("You have successfully inserted a record! " + Emojis.smileyFace);
                alert.showAndWait();
            }else{
                System.out.println("Unsuccessful creation of record");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failure Message");
                alert.setHeaderText("Unsuccessful Addition");
                alert.setContentText("Failed to add new record. Please try again " + Emojis.sadFace);
                alert.showAndWait();
            }
        }catch(SQLException e){
            System.out.println("SQL Error");
            e.printStackTrace();
        }
    }

}
