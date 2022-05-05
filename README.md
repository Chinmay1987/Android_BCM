# {Bank name here} retail banking application

This is the code base for the {Bank name here} retail banking Android application. It is based on the Backbase mobile
Journey architecture. The Backbase retail Journey release version on which this project is based is recorded in
[gradle.properties](gradle.properties).

## Getting started

This project depends on various artifacts published to repositories on [Backbase Repo](https://repo.backbase.com). You
must have read access to these repositories to build this project. To ensure the build system can access these
artifacts on your machine, follow these steps:
1. Log in to [Repo](https://repo.backbase.com) and click your name in the top right.
2. Type in your password to get access to your encrypted password. Copy your encrypted password.
3. Open/create the `gradle.properties` file at `/Users/<username>/.gradle/` (Mac) /
   `C:\Users\<username>\.gradle\` (Windows) / `/home/<username>/.gradle/` (Linux).
4. Add `backbaseRepoUsername=<your username>` to the file.
5. Add `backbaseRepoEncryptedPassword=<your encrypted password>` to the file.

Note that this process should **not** result in your password being added to source control. Each developer of this
project must do this individually.

To get your new project started, resolve the few TODO items in the project related to the app's application ID, package
name, and API root. Once you've done this you can run the app and see how it looks. From there you can customize the
[theme](app/src/main/res/values/theme.xml) or change any of the code in the project
