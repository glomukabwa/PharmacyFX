package com.pharmacyfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SignInController implements Initializable{

    @FXML
    private TextField txtfname;
    @FXML
    private TextField txtlname;
    @FXML
    private TextField txtemail;
    @FXML
    private PasswordField txtpassword;
    @FXML
    private Button btnsignup;

    //I'm trying to give my application more personality so I'm adding emojis:
    String smileyFace = "\uD83D\uDE0A";//When you copy an emoji, then put it in between quotes like this, it appears as its unicode though when I paste it outside, it appears like this see: 😊
    String sadFace = "\uD83D\uDE1E";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnsignup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String Fname = txtfname.getText().trim();
                String Lname = txtlname.getText().trim();
                String Email = txtemail.getText().trim();
                //You can't directly use trim() on getPassword cz it returns characters and tbh rn I'm too lazy to learn the manual coding of trimming that Chat has given me so I just won't do it
                //Ok so I just found out that getPassword() is not part of FX and instead there is a dependancy I can get from Maven Central to encrypt the password before storing it for safety.
                //I am too tired and lazy rn to learn new things so till next time!!I'm putting this here to remind myself
                //Oh also, don't forget about email validation. U never even did it with Swing, do it!!! Bye!
            }
        });
    }

    public void SignUp(String fname, String lname, String email, char[] password){//Check explanation of why I make it an array in the Java Swing version of this application
        String query = "INSERT INTO pharmacists (fname, lname, email, password) VALUES (?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1,fname);
            stmt.setString(2,lname);
            stmt.setString(3,email);
            String pass = new String(password);//You do this cz prepared statement doesn't have a method for setting characters plus you want your password to be a word
            stmt.setString(4,pass);

            int rows_inserted = stmt.executeUpdate();
            if(rows_inserted  > 0){
                System.out.println("Successful Sign Up");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You have successfully signed up! " + smileyFace);
                alert.show();
            }else{
                System.out.println("Unsuccessful Sign Up");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Unsuccessful Sign Up! " + sadFace);
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error");
        }finally{
            Arrays.fill(password,' ');//Erasing the password from memory after we use getPassword(). This is for safety
        }
    }
}
