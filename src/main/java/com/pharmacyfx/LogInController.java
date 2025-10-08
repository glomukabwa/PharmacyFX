package com.pharmacyfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class LogInController implements Initializable {

    @FXML
    TextField txtemail;
    @FXML
    TextField txtpassword;
    @FXML
    Button btnlogin;
    @FXML
    Button btnsignup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnlogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String email = txtemail.getText().trim();
                String pass = txtpassword.getText().trim();
                if(!email.isEmpty() && !pass.isEmpty()){
                    LogIn(email, pass);
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Empty Fields");
                    alert.setHeaderText("One or more fields are empty");
                    alert.setContentText("Please fill in all the fields");
                    alert.showAndWait();
                }
            }
        });

        btnsignup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("sign-in.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    stage.setTitle("Sign Up Page");
                    stage.setScene(scene);
                    stage.show();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void LogIn(String email, String password){
        String query = "SELECT password FROM pharmacists WHERE email = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1,email);
            ResultSet result = stmt.executeQuery();//Get the result of the selection
            if(result.next()){//Check if that username actually exists meaning a password exists too
                String storedHash = result.getString("password");//If there is a result then get the password
                if(BCrypt.checkpw(password,storedHash)){//Use BCrypt to check if they match, checkpw() returns true if they do
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success Message");
                    alert.setHeaderText("Successful Log In!");
                    alert.setContentText("You have successfully logged in! " + Emojis.smileyFace);
                    alert.showAndWait();
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failure Message");
                    alert.setHeaderText("Unsuccessful Log In!");
                    alert.setContentText("Please enter the right credentials " + Emojis.sadFace);
                    alert.showAndWait();
                }
            }else{//If user hasn't entered an existing email:
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failure Message");
                alert.setHeaderText("Unsuccessful Log In!");
                alert.setContentText("Email does not exist " + Emojis.sadFace);
                alert.showAndWait();
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
