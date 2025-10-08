package com.pharmacyfx;

public class Emojis {
    //I'm trying to give my application more personality so I'm adding emojis:
    //Making them static makes them accessible without creation of an object
    //Example showing accessing them:
    //System.out.println(Emojis.smileyFace)
    static String smileyFace = "\uD83D\uDE0A";//When you copy an emoji, then put it in between quotes like this, it appears as its unicode though when I paste it outside, it appears like this see: 😊
    static String sadFace = "\uD83D\uDE1E";
    static String tick = "✅";//Ok the emojis can also be stored like this. For some reason, haileti code anymore
    static String cross = "❌";
}
