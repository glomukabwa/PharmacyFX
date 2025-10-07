package com.pharmacyfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    @FXML
    private Label lblemailstatus;
    @FXML
    private Label lblpwstatus;

    //I'm trying to give my application more personality so I'm adding emojis:
    String smileyFace = "\uD83D\uDE0A";//When you copy an emoji, then put it in between quotes like this, it appears as its unicode though when I paste it outside, it appears like this see: 😊
    String sadFace = "\uD83D\uDE1E";
    String tick = "✅";//Ok the emojis can also be stored like this. For some reason, haileti code anymore
    String cross = "❌";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Email Validation using Action Listener:
        txtemail.textProperty().addListener((observable, oldValue, newValue) -> {
            //Ok so txtemail.textProperty() creates an object that we can listen to. It is different from txtemail.getText() which will give you the current text right now(a one-time snapshot)
            //addListener is different from setOnAction.addListener is used for live monitoring of a property so it reacts every time the property chages not just when an action happens which is how setOnAction.
            // Chat says that setOnAction is for action events. I'm guessing typing is not considered an action cz its continuous
            //observable is the object that is being observed which is the textProperty()
            //oldValue is the previous text before the change eg if you have already typed abc, if you add d, the old value is abc
            //the new value is the current text after the change so in the above example: abcd
            //Chat said that observable, newValue and oldValue would automatically be added with the addListener but I have had to type them
            if (newValue.trim().isEmpty()){//This checks if there is a new value and if there isn't, the label remains empty meaning invisible
                lblemailstatus.setText("");
            } else if(isValidEmail(newValue.trim())){
                lblemailstatus.setText(tick + " Valid email");//So apparently some fonts don't support emojis so the colors of this emojis won't show
                //Chat says I can change the font of these labels to a font that supports emojis:
                //lblemailstatus.setStyle("-fx-font-family: 'Segoe UI Emoji';"); //I'm not gonna do this though cz I want uniformity of font plus I really like the one I'm using. I've tested it though and it's still black and white but the emojis are clearer
                lblemailstatus.setStyle("-fx-text-fill: green;");
            } else {
                lblemailstatus.setText(cross + "Invalid Email");
                //lblemailstatus.setStyle("-fx-font-family: 'Segoe UI Emoji;'");
                lblemailstatus.setStyle("-fx-text-fill: red;");
            }
        });

        //Password Validation:
        txtpassword.textProperty().addListener((observable, oldValue, newValue) -> {//We don't use all of them but I think its just nice to include all of them just in case you use them. Yes, I've had to still manually type them
            //Plz note that we make an object out of the text in the password field. I had made a mistake of using the label and obviously it didn't display
            String mess = PasswordValidation.validationMessage(newValue);
            lblpwstatus.setText(mess);

            //I don't know why Chat separated the methods for checking validity and getting the messages cz I think you can have them as one but I've used its methods to learn so I'm not gonna change them
            //I'll use the different methods to change get the messages then change the color. Hope u understand now y I've set color after
            if (PasswordValidation.isValid(newValue)){
                lblpwstatus.setStyle("-fx-text-fill: green;");
            }else{
                lblpwstatus.setStyle("-fx-text-fill: red;");
            }
        });

        btnsignup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String Fname = txtfname.getText().trim();
                String Lname = txtlname.getText().trim();
                String Email = txtemail.getText().trim();

                //You can't directly use trim() on getPassword cz it returns characters and tbh rn I'm too lazy to learn the manual coding of trimming that Chat has given me so I just won't do it
                //Ok so I just found out that getPassword() is not part of FX and instead there is a dependency I can get from Maven Central to encrypt the password before storing it for safety.
                //The dependency I've used here is called BCrypt:
                String Password = txtpassword.getText();//Chat said that it is not wise to trim passwords since a user may have put that space intentionally
                String hashedPassword = BCrypt.hashpw(Password, BCrypt.gensalt(12));
                //The genSalt() method generates salt. This means it generates a random string that is mixed with the password entered using hashpw().
                //The 12 in the brackets means the cost factor. The higher tha cost factor, the more secure the password is. The default is usually 10 so if you don't specify a number in the brackets, that will be ur default
                //The length of the hashed password is normally 22. The cost factor doesn't affect this

                if (isDuplicate(Email)){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Duplicated Email");
                    alert.setHeaderText(" Email already exists");//Header is the text between the one on the window and the message
                    alert.setContentText("Please enter another email.This one already exists. " + sadFace);
                    alert.showAndWait();
                }else{
                    SignUp(Fname,Lname,Email,hashedPassword);
                }
            }
        });
    }

    //Ok so I've just found out that instead of manually coding the instructions of how an email should look like, you can just use a dependency called javax.mail to check if ur email is valid:
    //Also, the reason this method is outside is because you can't declare a method inside another method(initialize() above) or you'll get an error
    public static boolean isValidEmail(String validateEmail){
        boolean result = true;
        try {
            InternetAddress intEmail = new InternetAddress(validateEmail);//This InternetAddress is a class we are importing and it has to be surrounded by a try...catch. When you type just this sentence, an error occurs
            //The line above tells the compiler to interpret the String as an email then validate() method below checks that it is a valid email
            //InternetAddress follows the RFC standards which are official internet rules for email addresses, domain names and web protocols
            intEmail.validate();
            //This is a better way to validate emails since with manual coding, things like: text@@gmail.com will still pass since the regex usually looks like this: "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$" and that @ there just specifies that it should contain an @ symbol not one @ symbol
        } catch (AddressException e){
            //throw new RuntimeException(e); //I've commented this line since once Java encounters a throw, it immediately exits the method and no code after it runs. I want to use the line below
            result = false;
        }
        return result;
    }

    //Checking to make sure that email doesn't already exist:
    public static boolean isDuplicate(String email){
        boolean result = false;//The default is false
        String query = "SELECT email FROM pharmacists WHERE email = ?";

        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,email);

            ResultSet res = stmt.executeQuery();
            if(res.next()){
                result = true;//It will become true if there is such an email in the database already
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void SignUp(String fname, String lname, String email, String password){
        String query = "INSERT INTO pharmacists (fname, lname, email, password) VALUES (?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1,fname);
            stmt.setString(2,lname);
            stmt.setString(3,email);
            stmt.setString(4,password);

            int rows_inserted = stmt.executeUpdate();
            if(rows_inserted  > 0){
                System.out.println("Successful Sign Up");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Successful Message");
                alert.setHeaderText("Successful Sign Up");
                alert.setContentText("You have successfully signed up! " + smileyFace);
                alert.showAndWait();//Chat has advised me to use this instead oh show()
                //The difference is that showAndWait() stops the execution till the user closes the alert box while show() just shows the alert box but the execution continues
                //So if you think user input is important, use show and wait
                //showAndWait will ensure that eg the scene doesn't switch before the user has pressed OK or close
            }else{
                System.out.println("Unsuccessful Sign Up");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unsuccessful Message");
                alert.setHeaderText("Unsuccessful Sign Up");
                alert.setContentText("Unsuccessful Sign Up! " + sadFace);
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error");
        }finally{
            txtpassword.clear();//Since we've used encryption, we don't need to erase the password like we did in the Swing version of this application. However, I'm clearing the password field after the user has logged in for safety. You'll notice that most apps do this
        }
    }
}
