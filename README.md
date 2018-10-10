# uSearch
uSearch is an Android app aimed for research on Mobile Information Retrieval. Developed at Università della Svizzera italiana (USI), Lugano, Switzerland.

## Requirements

This application need a [Firebase account](https://firebase.google.com/) to work.

An account for Crashanalytics is needed for Crash Analytics reporting

Qualtrics is used for the survey. The link for the questionnaires are in MainActivity (openSurvey).
TaskBrowserActivity uses an interceptor to know when the user has finished a questionnaire, the url that Qualtrics use might change in the future. 

## Usage

This application can be used for task-based or self-report user studies in cross-app mobile search research.
 
Sensors data from participants' smartphones are collected for the duration of a study. The collected data
is routinely uploaded to the cloud (Firebase Storage) for easy access and analysis.

## Quick Start

In order to run uSearch, follow the steps below:

1. Change the project's package name. To do so, follow [this useful post](https://stackoverflow.com/questions/16804093/android-studio-rename-package) on Android Studio.
2. Create a new project on Firebase and add the new package name in your project. Note that uSearch uses Firebase "Storage" service.
3. Create a new bucket on Firebase Storage.
4. Download "google-services.json" file and add it to the project under: uSearch/app/src
5. Configure the app to connect to your Fabric account.
6. The app's survey data is available only to the authors, therefore you should update its link to your own survey. The code can be found in MainActivity in "openSurvey" method.
7. Build your first APK file! 

## Enable/Disable Sensors

To enable/disable the collection of a sensor the 2 classes to edit are:

* RecordStorageManager.java (Edit this to enable/disable the upload)

Add the constant of the sensor to the array
 
__filesNamesList__

If the data need to be uploaded when only mobile data are available add it also to 

__lightFilesNamesList__

To stop the upload of a sensor remove it from both arrays. 

* BackgroundRecorder.java (edit this to enable/disable the gathering)

To enable the gathering of a sensor, reach the definition of the runnable __s__ and uncomment the function related to the sensor. 
To disable simply comment the function of the sensor.

## Update the Order/Presence of Apps

As the list of apps is different on every device based on the version and build, one may need to define certain apps to appear higher in the app list. Also, certain apps may be system apps and not needed in the list.

The app reads two lists from the raw resource directory: 
* apps_order.csv : top apps are ordered in this list by their name to appear as indicated in the list.
* apps_blacklist.csv : unwanted apps are listed here to be removed from the list.

Below you can see an example of top apps that are ordered as indicated in apps_order.csv as well as some unwanted apps that may appear in the list of apps.

 <img alt="manually ordered top apps" src="top_apps.png" width="45%" border=1/> <img alt="unwanted apps example" src="unwanted_apps.png" width="45%" border=1/>

Please note that apps that do not appear in either of the lists are listed after the "top apps" in the order of Android's package manager.

## Acknowledgements

The design and development of uSearch was in part supported by the RelMobIR project of Swiss National Science Foundation (SNSF) and in part by the UROP grant, provided by the Università della Svizzera italiana (USI). We would like to thank Jacopo Fidacaro and Luca Costa for their efforts in developing this app. Last but not least, we would like to thank all the participants who helped us improve the app and collect data.

## Contact

Please do feel free to contact us for any questions.
* Mohammad Aliannejadi: mohammad.alian.nejadi@usi.ch
* Hamed Zamani: zamani@cs.umass.edu

## Citing

Please cite our CIKM '18 paper if you use uSearch or its components:
* Mohammad Aliannejadi, Hamed Zamani, Fabio Crestani, W. Bruce Croft. "In Situ and Context-Aware Target Apps Selection  for Unified Mobile Search", In Proc. of CIKM 2018.
