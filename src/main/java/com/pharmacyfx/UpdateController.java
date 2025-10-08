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
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateController  implements Initializable {
    @FXML
    TextField txtsname;
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
    Button btnsearch;
    @FXML
    Button btnclear;
    @FXML
    Button btnupdate;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtpurchase.setValue(LocalDate.now());//Setting default date
        btnsearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String Sname = txtsname.getText();
                if(!Sname.isEmpty()){
                    List<Object> SearchObject = Search(Sname);//We don't write new before search cz its static
                    String dname = (String) SearchObject.get(1);//We need to cast them cz the list is of type Object meaning it can store any type so Java wants to ensure it knows the right respective data types. If u don't do this, there will be an error.
                    String duse = (String) SearchObject.get(2);
                    String damount = (String) SearchObject.get(3);
                    LocalDate dpurchase = (LocalDate) SearchObject.get(4);
                    int pharmId = (int) (SearchObject.get(5));//Java wants you to tell it the data type(casting) but that doesn't mean that it has forgotten so if you try to cast this to a String, u'll get an error. You have to do conversion
                    //Also, TextFields only accept text(There's no method like setInt() for textfields) so:
                    String pID = String.valueOf(pharmId);

                    txtdname.setText(dname);
                    txtduse.setText(duse);
                    txtdamount.setText(damount);
                    txtpurchase.setValue(dpurchase);
                    txtpharmId.setText(pID);
                }else{
                    System.out.println("Empty fields detected");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failure Message");
                    alert.setHeaderText("Empty fields detected");
                    alert.setContentText("Please enter the name of the drug you want to search " + Emojis.sadFace);
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

        btnupdate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String Sname = txtsname.getText();
                List<Object> searchRes = Search(Sname);
                Update(searchRes,txtdname.getText() , txtduse.getText(), txtdamount.getText(), txtpurchase.getValue(), txtpharmId.getText());
            }
        });
    }

    public static List<Object> Search(String Sname){
        List<Object> drugsData= new ArrayList<>();
        String query = "SELECT * FROM drugs WHERE dname = ?";//We're selecting everything cz everything needs to be displayed aside from Id. And Id will be used for the update method later

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1,Sname);

            ResultSet result = stmt.executeQuery();
            if(result.next()){
                int Did = result.getInt("drugId");
                String Dname = result.getString("dname");
                String Duse = result.getString("duse");
                String Damount = result.getString("damount");
                java.sql.Date sqlDate = result.getDate("dpurchase");//The date was stored as an SQL date but the date picker expects a local date so convert it:
                LocalDate localDate = sqlDate.toLocalDate();
                int PharmId = result.getInt("pharmacistId");//Don't forget that it was stored as an integer. You retrieve data the way it was stored

                //I don't know why I can't access the FXML components inside this method so I'm gonna return a list so that I can use it to assign the data inside initialize
                //Remember: List can store multiple data types but arrays can't
                drugsData.add(Did);//Index 0
                drugsData.add(Dname);
                drugsData.add(Duse);
                drugsData.add(Damount);
                drugsData.add(localDate);
                drugsData.add(PharmId);//Index 5


            }else{
                System.out.println("Drug not found");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failure Message");
                alert.setHeaderText("Drug not found");
                alert.setContentText("This drug is out of stock " + Emojis.sadFace);
                alert.showAndWait();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return drugsData;//Returns an empty list if nothing is found
    }

    public static void Update(List<Object> SearchResult, String Dname, String Duse, String Damount, LocalDate Dpurchase, String PharmId){
        int Did = (int) SearchResult.get(0);

        String query = "UPDATE drugs SET dname = ?, duse = ?, damount = ?, dpurchase = ?, pharmacistId = ? WHERE drugId = ?";//We're using drug id as the condition and not drug name cz the drug name can be altered by the user. We need sth unchangeable

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,Dname);//The first ? is for dname in the query
            stmt.setString(2,Duse);
            stmt.setString(3,Damount);
            java.sql.Date sqlDate = java.sql.Date.valueOf(Dpurchase);
            stmt.setDate(4,sqlDate);
            int Pid = Integer.parseInt(PharmId);
            stmt.setInt(5,Pid);
            stmt.setInt(6,Did);

            int rows_affected = stmt.executeUpdate();
            if(rows_affected > 0){
                System.out.println("Successful Update");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success Message");
                alert.setHeaderText("Successful Update");
                alert.setContentText("Record has been updated successfully! " + Emojis.smileyFace);
                alert.showAndWait();
            }else{
                System.out.println("Unsuccessful Update");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failure Message");
                alert.setHeaderText("Unsuccessful Update");
                alert.setContentText("Please try again " + Emojis.sadFace);
                alert.showAndWait();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
