![Build Status](https://www.bitrise.io/app/bitrise_id.svg?token=token)
![minSdkVersion](https://img.shields.io/badge/minSdk-19-red.svg)
![compileSdkVersion](https://img.shields.io/badge/compileSdkVersion-25-green.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![License](https://img.shields.io/badge/license-Prolific_Interactive-blue.svg)

![Project Code name](app/src/debug/res/mipmap-xxxhdpi/ic_launcher.png)

# Overview

Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

# Links
[![Dropbox](art/logo_dropbox.png)](https://www.dropbox.com/work/project_name/) | [![Pivotal Tracker](art/logo_pt.png)](https://www.pivotaltracker.com/n/projects/project_id) | [![Sketch](art/logo_sketch.png)](https://www.dropbox.com/work/sketck_file) |[![Bitrise](art/logo_bitrise.jpg)](https://www.bitrise.io/app/bitrise_id) | [![Fabric](art/logo_fabric.png)](https://www.fabric.io/package_name)
:---:|:---:|:---:|:---:|:---:|:---:
[Dropbox](https://www.dropbox.com/work/project_name/) | [Pivotal Tracker](https://www.pivotaltracker.com/n/projects/project_id) | [Sketch](https://www.dropbox.com/work/sketck_file) | [Bitrise](https://www.bitrise.io/app/bitrise_id)

# Project Setup

## Cloning

```
$ git clone https://{{username}}@bitbucket.org/prolificinteractive/project_name.git
$ echo 'BUILD_NUMBER = 1' > version.properties
```

## Setup

Fetch the following files from [Dropbox](https://www.dropbox.com/work/Engineering/android/Security/project_name) to the root of the project:
```
.
â”œâ”€â”€ debug.properties
â”œâ”€â”€ gradle.properties
â”œâ”€â”€ keystores
â”‚Â Â  â””â”€â”€ debug.jks
â””â”€â”€ version.properties
```

## Required keys

### Main

```
# gradle.properties

FABRIC_KEY
CLIENT_ID
CLIENT_SECRET
DEV_MODE # should be set to false for hockey builds
GOOGLE_MAPS_KEY
```

### Debug

```
# debug.properties

DEBUG_STORE_FILE
DEBUG_STORE_PASSWORD
DEBUG_KEY_ALIAS
DEBUG_KEY_PASSWORD
HOCKEYAPP_APP_ID
INSTABUG_KEY
```

### Release

```
# release.properties

# NONE
```

## Build

### Clean Build

```
$ ./gradlew clean build
```

### Debug variant

```
$ ./gradlew assembleDebug [-PdisablePreDex]
```

- -PdisablePreDex: disable pre dexing

### Release variant

```
$ ./gradlew assembleRelease [-PdisablePreDex] [-Psplit]
```
- -PdisablePreDex: disable pre dexing
- -Psplit: enable apk splitting

### Code analysis

```
$ ./gradlew check
```

# Android Specs

### Libraries and Tools Overview
- [Support libraries](https://developer.android.com/topic/libraries/support-library/index.html)
- [Android Annotations](https://github.com/androidannotations/androidannotations/wiki)
- Rx Libraries
    - [RxAndroid](https://github.com/ReactiveX/RxAndroid): Android specific bindings for RxJava 2
    - [RxBinding](https://github.com/JakeWharton/RxBinding): RxJava binding APIs for Android UI widgets from the platform and support libraries
    - [RxJava](https://github.com/ReactiveX/RxJava): Java VM implementation of Reactive Extensions
    - [RxLifecycle](https://github.com/trello/RxLifecycle): Automatic completion of sequences based on Activity or Fragment lifecycle events
    - [RxLocation](https://github.com/mcharmas/Android-ReactiveLocation): Small library that wraps Google Play Services API in RxJava
    - [RxPermissions](https://github.com/tbruyelle/RxPermissions): usage of RxJava with the new Android M permission model
    - [RxRelay](https://github.com/JakeWharton/RxRelay): Relays are RxJava types which are both an Observable and a Consumer
- [Retrofit 2](http://square.github.io/retrofit/): A type-safe HTTP client for Android and Java
- [Dagger 2](http://google.github.io/dagger/): Fully static, compile-time dependency injection framework for both Java and Android
- [AutoValue](https://github.com/google/auto): Generated immutable value classes for Java
- [Conceal](https://github.com/facebook/conceal): Provides a set of Java APIs to perform cryptography on Android
- [Timber](https://github.com/JakeWharton/timber): Logger with a small, extensible API
- [Debug Drawer](https://github.com/palaima/DebugDrawer)
- [Checkstyle](http://checkstyle.sourceforge.net/), [PMD](https://pmd.github.io/) and [Findbugs](http://findbugs.sourceforge.net/) for code analysis

### Explanation of Build Flavors

* Debug: Hockey Build
    + :white_check_mark: Logging
    + :white_check_mark: Instabug
    + :white_check_mark: Crashlytics
    + :white_check_mark: LeakCanary
    + :white_check_mark: Debug Drawer
    + :negative_squared_cross_mark: Proguard
* Release: Play Store
    + :negative_squared_cross_mark: Logging
    + :negative_squared_cross_mark: Instabug
    + :white_check_mark: Crashlytics
    + :negative_squared_cross_mark: LeakCanary
    + :negative_squared_cross_mark: Debug Drawer
    + :white_check_mark: Proguard


### Architecture

#### MVP

MVP is a user interface architectural pattern engineered to facilitate automated unit testing and improve the separation of concerns in presentation logic:

- The __model__ is an interface defining the data to be displayed or otherwise acted upon in the user interface.
- The __presenter__ acts upon the model and the view. It retrieves data from repositories (the model), and formats it for display in the view.
- The __view__ is a passive interface that displays data (the model) and routes user commands (events) to the presenter to act upon that data.

#### Loaders

[Loaders](https://developer.android.com/reference/android/content/Loader.html) are a tool provided by the Android framework that survive configuration changes. Their lifecycle will be managed by the system and they will be automatically cleaned up when the requesting Activity or Fragment is permanently destroyed.

### Debug Process

- Debug Drawer: accessible as a right navigation drawer

### Continuous Integration

1. Git Clone Repository
2. Get Keys
3. Get Build Number
4. Android SDK Update
5. Extra Android Repo
6. Open JDK
7. Gradle Runner
8. HockeyApp Android Deploy
9. Send a Slack message
10. Bump Build Number
11. Finish Build :white_check_mark:

## Contributing

1. Create your feature branch (`git checkout -b ab_feature_name`)
2. Commit your changes (`git commit -am 'Add some feature'`)
3. Publish the branch (`git push origin ab_feature_name`)
4. Create a new Pull Request
5. Profit! :white_check_mark:

## Team

[![Member 1](member_1_avatar.png)](https://bitbucket.org/member_1/) | [![Member 2](member_2_avatar.png)](https://bitbucket.org/member_2/) | [![Member 3](member_3_avatar.png)](https://bitbucket.org/member_3/) | [![Member 4](member_4_avatar.png)](https://bitbucket.org/member_4/) |
:---:|:---:|:---:|:---:
[Member 1](https://bitbucket.org/member_1/) | [Member 2](https://bitbucket.org/member_2/) | [Member 3](https://bitbucket.org/member_3/) | [Member 4](https://bitbucket.org/member_4/)
Product Manager | Android Engineer | Android Engineer | Product Designer

## License

    Copyright (c) 2017 Prolific Interactive.
    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.