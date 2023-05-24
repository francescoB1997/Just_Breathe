
# JustBreathe Android Application

Mobile and Social Sensing Systems Project

## Description

**JustBreathe** is an application developed for Android devices that monitors the user's breathing by taking advantage of the smartphone's built-in accelerometer and measures its quality by giving a score from 0 to 100. This score indicates how well the user's breathing conforms to a certain breathing pattern, if performed correctly it will enable the user to improve his or her well-being.

## Quick Start

In order to use the JustBrethe App follow steps below:

1. Extract the contents of the **JusteBreathe.zip** file
2. Start the Android Studio IDE (Verify that you have the latest version), click on **Open** and select the **JustBreathe** folder extracted in the previous step
3. In the section of the IDE where the **Gradle** configuration file is located, add in **Gradle Scripts/build.gradle(Module: app)** respectively:

- In **"android"** section:

```
// Exclude these files to avoid they conflict with those imported from the jDSP package
packagingOptions {
    exclude 'META-INF/*'}
```

- In **"dependencies"** section:

```
 // To get the latest stable release of JDSP
implementation ('com.github.psambit9791:jdsp:2.0.1') {
	exclude group: 'org.apache.maven.surefire', module: 'common-java'
	exclude group: 'org.apache.maven.surefire', module: 'surefire-api' }
```

4. Click on **"Sync Now"** in the pop-up at the top of the screen to finalize the changes you made.
5. Launch the project to install the app on your Android smartphone
6. Enjoy the Application!

## How to interact with JustBreathe App

For proper use of the App visit the documentation section in **"JustBreathe.zip"** file for the User Manual.

## Authors

* Francesco Bruno, f.bruno10@studenti.unipi.it
* Gaetano Sferrazza, g.sferrazza@studenti.unipi.it
* Lorenzo Tonelli, l.tonelli4@studenti.unipi.it
* Nicolò Picchi, n.picchi3@studenti.unipi.it
