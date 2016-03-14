# Nest Manager (Unofficial)

### Nest Manager App
Nest Manager is the unofficial user created SmartThings SmartApp and device handler for Nest devices to be used with SmartThings.
This SmartApp and Device Handlers work together to provide integration to the SmartThings ecosystem using Nest's Official API. 

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_newInst.png" width="281" height="500">

### The Devices Types
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_protect.PNG" width="281" height="500">
## Author
* @tonesto7

## Contributors 
* @desertBlade

## Version Info
__Latest App Version:__ 

* Nest Manager - __*v0.7.0*__
 
__Latest Device Versions:__

* Nest Thermostat Device - __*v0.4.10*__
* Nest Protect Device - __*v0.3.5*__

## What's New
Everything! 

## Links
#### GitHub Issues Link
__[GitHub Project Issues Link](https://github.com/tonesto7/st-nest-unofficial/issues)__

#### Forum Link
__[https://community.smartthings.com/t/beta-full-nest-manager] (https://community.smartthings.com/t/beta-full-nest-manager)__

#### Wiki
__[Wiki Home Page](https://github.com/tonesto7/st-nest-unofficial/wiki/Home)__

## Things to Know
 * __This is still a BETA so you may experience issues!!!__
 * __This does NOT support Nest Cams and I don't have any to test__
 * __At this moment the app will only support one location and the Thermostats and Protects within__
 * The token used with this application is using my 'Works for Nest' distribution appId and secret.  It's an older one which allows 1000 user before I will need to certify with Nest.
__FYI: I do not have the ability to see any of your data or who is using the token__


## Advantages
 * Able to add/remove multiples devices from a single SmartApp
 * No need to many enter device serial number and login info in preferences
 * No need to use Polling for device updates
 * One single API call for all Nest devices
 * Since there is only a single poll for all devices, updates are more often

## Disadvantages
 * The level of info available through the Official API is very limited compared to the hacked version currently available.

## What's Working
 * Nest API Authentication 
 * Add/Remove Devices
 * Polling every 60 seconds by default (user can change in application)
 * Thermostat Devices
 * Protect devices
 * Works with Routines
 * Works w/Rule Machine (Rules)
 * Works w/Rule Machine (Custom Commands)

 
## New Features
 * I've added in the ability to select Temp, Energy or Power device events to assist in polling.

## To-Do Items
 * Find bugs and optimize code
 
## Installation

###Install Method 1: _The Semi-Automated Way_
_This is the by far the easiest way to install and get the latest updates for Nest Connect App, Protect and Thermostat devices the best way is to enable the GitHub Integration in your IDE._

####__The Connect App__
 
 * Go to My SmartApps in the IDE
 * Click on Settings
 * Click on Add new repository
 * Owner: __tonesto7__, Name: __st-nest-unofficial__, Branch: __master__
 * Under My SmartApps Click on _Update from Repo_ and select the _st-nest-unofficial_
 * Check the box next to _nest-manager_ and click _Execute Update_
 * Click on the _nest manager_ app link and select _publish_ and _publish for me_ 
 
	#### ___You will also need to Enable OAuth under the app preferences in the IDE___

####__The Devices__
 
 * Go to My Device Types in the IDE (Not necessary if you added repository for the app)
 * Click on Settings
 * Click on Add new repository
 * Owner: __tonesto7__, Name: __st-nest-unofficial__, Branch: __master__
 * Under My Device Types Click on _Update from Repo_ and select the _st-nest-unofficial_
 * Check the box next to _nest-protect_ and _nest-thermostat_ then click _Execute Update_
 * Click on each device link and select _publish_ and _publish for me_
 
That's it your Done in the IDE... Just install __'Nest Manager'__ from the Marketplace > MyApps
When updates are made you will see the color change in the IDE.

### Method 2: The Manual Way
####__Adding the Nest Manager SmartApp__
 
 * Log into your SmartThings account at https://graph.api.smartthings.com/
 * Goto "_My SmartApps_"
 * Click on __+__ New SmartApp
 * Choose "From Code"
 * Copy source code from nest-manager.src
 * Click Create
 * Go into SmartApp __Nest Manager__ Settings
 * Click on App Settings and enable OAuth and click update
 
####__Adding the Thermostat and Protect Device Handlers__
 * Goto My Device Handlers
 * Create New Device Handler
 * Choose "From Code"
 * Copy Source code from nest-protect.src
 * Repeat for nest-thermostat.src
 * Remember to open each device and click _Publish for Me_

####__Setting up Nest Manager App__
 * In the SmartThings Mobile App
 * Goto "***Marketplace***" and select "***SmartApps***"
 * At the bottom of the list, select "***My Apps***"
 * Select "Nest Manager" from the list.
 * Enter you nest login credentials when prompted.
 * Choose **Structure**
 * Choose **Thermostats**
 * Choose **Protects**
 * *Done*

## Issues and Troubleshooting
_If you are experiencing any issues, please let us know by heading over to projects issues page on GitHub. If you don't see the issue reported please help open an new one and provide as much detail as you can._ 
  [Project Issues Link](https://github.com/tonesto7/st-nest-unofficial/issues) 
In an effort to make it easier for some of you troubleshoot without having to use the IDE constantly.  I have added in a diagnostic option under the preference section of the SmartApp.  
Once this is enabled it will begin to store non-user identifiable error logs from the app and store in a local state variable of the app.  
This feature will help you to view and export the logs (_also see the tip below_) directly from the Smart App. This also allows you to copy & paste them into the issues form on GitHub.
*add screen shot of the diagnostic pages*
__Tip:__ If you enable diagnostic logs in the app you can share the logs from with in the app you can store the direct link to log JSON file on you computer and browse directly to the log everytime you need to review the errors.

### Feature Requests
 * We love new ideas so please head on over to github and open an issue for the feature you would like to see.  This will help prioritize what is important and what is not.

### Nest API Documentation
 https://developer.nest.com/documentation/cloud/get-started

 To view the json returned from the API just get your authToken from the SmartApp state data and add it to this url
 https://developer-api.nest.com/devices?auth=_yourAuthTokenHere_
