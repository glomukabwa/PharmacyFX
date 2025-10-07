package com.pharmacyfx;
import org.passay.*;
import java.util.Arrays;
import java.util.List;

public class PasswordValidation {

    private static final PasswordValidator validator = new PasswordValidator(Arrays.asList(//Plz note that PasswordValidator is what we're getting from passay so you have to spell it right or u'll get errors. It's kinda like Connection in java.sql
            new LengthRule(8,30),//Length between 8 and 30
            new CharacterRule(EnglishCharacterData.UpperCase, 1),//At least one uppercase
            new CharacterRule(EnglishCharacterData.LowerCase,1),//At least one lowercase
            new CharacterRule(EnglishCharacterData.Digit, 1),//At least one number
            new CharacterRule(EnglishCharacterData.Special, 1),//At least one special character
            new WhitespaceRule()//No whitespace allowed
    ));

    public static boolean isValid(String password){
        RuleResult result = validator.validate(new PasswordData(password));
        //new PasswordData(password) creates a PasswordData object. Passay requires this object to analyze if the password fulfills the rules we set above
        //validator is our PasswordValidator object that we defined earlier with all the rules above
        //When we call: validator.validate(new PasswordData(password)) it runs the password through each rule in that list and checks whether it passes or fails.
        //validate() method returns a RuleResult object which is basically an object that holds whether the password has passed the rules or failed and which ones it has failed if any
        //isValid() extracts the the true or false result from that RuleResult object. I'm guessing that isValid() is an actual passay method that just returns a boolean but this whole method has been named after it
        return result.isValid();//So this returns true if it has passed and false if it hasn't
    }

    public static String validationMessage(String password){
        RuleResult message = validator.validate(new PasswordData(password));
        if (message.isValid()){
            return "✅ Strong password";
        }//The if condition will be executed if it has passed but if it hasn't, the below will be executed
        List<String> messageS = validator.getMessages(message);//We're storing all the messages telling us why it has failed and storing them in a list. (Remember that the biggest difference between an array and list is that a list is mutable)
        messageS.replaceAll(msg -> msg
                .replace("Password must be 8 or more characters in length", "❌Must be 8 or more characters")
                .replace("Password must contain","❌Needs"));
        return String.join("\n", messageS);//This is saying, separate the messages with a line break. If you wanted them to be separated with a comma, you would've put it in the position of \n
        //I don't know how to comment in fxml so I'll put the comments here:
        //To make the label show all the messages in their own lines, you have to use wrap and it has to have a prefWidth
        //The prefWidth is automatically set when you place the label and edit it in the fxml file however sth else that is set at the time is the prefHeight
        //If you have enabled wrapping but the text is still not displaying how you want it to(like in my case when I was developing), remove the prefHeight cz it makes the height fixed so only the messages that fit are visible
    }

    //Using a regex for password validation:
    //^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$
    //Now step-by-step:
    //^
    //Anchor: start of the string.
    //
    //(?=.*[0-9])
    //A positive lookahead.
    //(?=...) doesn’t consume characters, it just asserts “somewhere ahead there must exist …”.
    //.* means any characters (0 or more), [0-9] means a digit.
    //So this asserts “the string contains at least one digit”.
    //
    //(?=.*[a-z])
    //Another positive lookahead: at least one lowercase letter.
    //
    //(?=.*[A-Z])
    //At least one uppercase letter.
    //
    //(?=.*[@#$%^&+=!])
    //At least one character from the special set @ # $ % ^ & + = !.
    //(You can change that set to include other allowed specials; if you include regex-significant chars like - or ] you must escape them properly.)
    //
    //.{8,}
    //Now the actual consuming part of the regex: . matches any character (except line break by default), {8,} means 8 or more times.
    //So the whole password must be at least 8 characters long (and can be longer).
    //
    //$
    //Anchor: end of the string.
    //Why lookaheads? Because they let you require the presence of different character classes without caring about their order.
    //Lookaheads check existence, then .{8,} actually matches the full string.

}
