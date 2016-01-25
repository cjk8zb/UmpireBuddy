# UmpireBuddy

## Version 1.0 (Lab #1)
The purpose of this lab assignment is to help you get started with Android programming. It will introduce you to the syntax and semantics of Android/Java along with the mechanics of using Android Studio with the Android plugin.

The lab assignment is to create an Android application that can function as a ball-strike counter an umpire might use during a baseball game to keep track of the count for a batter.

For now, the application only needs to keep track of balls and strikes for a single batter. In a future assignment, I may have you extend it to keep track of outs and/or innings. (If you want to add extra features during assignment #1, have at it.)

If the count reaches 3 strikes first, your application should display a dialog box announcing the batter is “Out!” If the count reaches 4 balls first, your application should display a dialog box announcing “Walk!” After either one, the count should be reset for the next batter.

The final result should look something like:

![Picture 1](docs/doc-files/Lab1Picture1.png)
![Picture 2](docs/doc-files/Lab1Picture2.png)


### Questions
1. What, if anything, is created for you when you enter the following as a value in an .xml layout? `android:id="@+id/strike_button"`
  > The plus sign "+" indicates that a new resource reference should be created and added to the R.java file.

2. What about when you enter: `android:text="@string/strike_button_label"`
  > This is a reference to a resource that is located in the string namespace.
