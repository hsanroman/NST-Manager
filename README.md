# Nest Manager (Unofficial)

### Nest Manager App
This is the **unofficial** SmartThings user community created SmartApp and Device handlers for Nest devices to be used with SmartThings.
The SmartApp and Device Handlers work together to provide integration to the SmartThings ecosystem using Nest's Official API. 

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_newInst.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_1.png" width="281" height="500">

### The Devices Types
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_protect.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_nest_pres_dev.png" width="281" height="500">

## Author
* @tonesto7

## Contributors 
* @desertBlade
* @E_sch

## Version Info
__Latest App Version:__ 

* Nest Manager - __*v0.7.3*__
 
__Latest Device Versions:__

* Nest Presence Device - __*v0.1.2*__
* Nest Protect Device - __*v0.3.6*__
* Nest Thermostat Device - __*v0.5.1*__

## What's New
Everything! 

## Links
#### [GitHub Project Issues Link](https://github.com/tonesto7/nest-manager/issues)

#### [https://community.smartthings.com/t/beta-full-nest-manager](https://community.smartthings.com/t/beta-full-nest-manager)

#### [GitHub Wiki Page](https://github.com/tonesto7/nest-manager/wiki/Home)

## Things to Know
 * __This is still technically in BETA so you may experience issues!!!__
 * This app __DOES NOT__ support Nest Cams and I don't have any to test__
 * At this moment the app will only support __One__ location and the Thermostats and Protects within__
 * _The token used with this application is using my 'Works for Nest' distribution *appId* and *secret*.  It's an older one which allows 1000 user before I will need to certify with Nest._
__I do not have the ability to see any of your data or who is even using the token__

## Advantages
 * Able to add/remove multiples devices from a single SmartApp
 * No need to many enter device serial number and login info in preferences
 * Nest Login info is not stored by the application
 * No need to use 3rd Party Polling apps for device updates
 * One single API call for all Nest devices
 * Since there is only a single poll for all devices, updates are more often
 * The devices look great :smile:

## Disadvantages
 * The level of info available through the Official API is very limited compared to the hacked version currently available.

## Works With
 * Routines
 * Rule Machine (Rules)
 * Rule Machine (Custom Commands)
 
## Future Items
 * Switching app to parent-child app to support multiple locations and allow creation of other child apps that perform various automation tasks.
 
## Installation

###Install Method 1: _The Semi-Automated Way_
_This is the by far the easiest way to install and get the latest updates for Nest Connect App, Protect and Thermostat devices the best way is to enable the GitHub Integration in your IDE._

####__The Connect App__
 
 * Go to "**My SmartApps**" in the IDE
 * Click on "**Settings**"
 * Click on "**Add New Repository**"
 * Owner: **tonesto7**, Name: **nest-manager**, Branch: **master**
 * Under "**My SmartApps**" Click on "**Update from Repo**" and select the ***nest-manager***
 * Check the box next to ***nest-manager*** and click "**Execute Update**"
 * Click on the ***Nest Manager*** app link and select "**Publish**" and "**For me**" 
 
	#### ___You will also need to Enable OAuth under the app preferences in the IDE___

####__The Devices__
 
 * Go to My Device Types in the IDE (Not necessary if you added repository for the app)
 * Click on "**Settings**"
 * Click on **Add new repository**
 * Owner: **tonesto7**, Name: **nest-manager**, Branch: **master**
 * Under My Device Types Click on ***Update from Repo*** and select the ***nest-manager***
 * Check the box next to ***nest-presence***, ***nest-protect*** and ***nest-thermostat*** then click **Execute Update**
 * Click on each device link and select "**Publish** and **For Me**
 
That's it your Done in the IDE... Just install "**Nest Manager**" from the ***Marketplace > MyApps*** under the mobile app.

When updates are available to the source code you will see the color change from black in the IDE.

### Method 2: The Manual Way
####__Adding the Nest Manager SmartApp__
 
 * Log into your SmartThings account at [https://graph.api.smartthings.com/](https://graph.api.smartthings.com/)
 * Go to "**My SmartApps**"
 * Click on "**+ New SmartApp**"
 * Choose "**From Code**"
 * Copy source code from ***nest-manager.src***
 * Click "**Create**"
 * Go into SmartApp **Nest Manager** Settings
 * Click on "**App Settings**" and ***enable OAuth*** and click "**Update**"
 
####__Adding the Presence, Protect, and Thermostat Device Handlers__
 * Go to "**My Device Handlers**"
 * Create "**New Device Handler**"
 * Choose "**From Code**"
 * Copy Source code from *nest-protect.src*
 * Repeat for ***nest-presence.src***
 * Repeat for ***nest-thermostat.src***
 * Remember to open each device and click "**Publish**" and "**For me**"

####__Setting up Nest Manager App__
 * In the SmartThings Mobile App
 * Go to "**Marketplace**" and select "**SmartApps**"
 * At the bottom of the list, select "**My Apps**"
 * Select "**Nest Manager**" from the list.
 * Enter you Nest Login credentials when prompted.
 * Choose **Structure**
 * Choose **Thermostats**
 * Choose **Protects**
 * Choose *Use Nest as Presence Device* (Optional)
 * Tap on "**Done**"

## Issues and Troubleshooting

### Issues
*If you are experiencing any issues, please let us know by heading over to projects issues page on GitHub. If you don't see the issue reported please help open an new one and provide as much detail as you can.*

**[Project Issues Link](https://github.com/tonesto7/st-nest-unofficial/issues)** 

In an effort to make it easier for some of you troubleshoot without having to use the IDE constantly.  I have added in a diagnostic option under the preference section of the SmartApp.  
Once this is enabled it will begin to store non-user identifiable error logs from the app and store in a local state variable of the app.  
This feature will help you to view and export the logs (*also see the tip below*) directly from the Smart App. This also allows you to copy & paste them into the issues form on GitHub.

**Tip:** If you enable diagnostic logs in the app you can share the logs from with in the app you can store the direct link to log JSON file on you computer and browse directly to the log everytime you need to review the errors.

### Troubleshooting
**Tip:** *The most common issue is forgetting to enable oAuth for the app under SmartThings*

Check the Wiki before posting question in the forums 
**[Wiki Home Page](https://github.com/tonesto7/nest-manager/wiki/Home)**

### Feature Requests
 * We love new ideas so please head on over to github and open an issue for the feature you would like to see.  This will help prioritize what is important and what is not.

### Nest API Documentation
 [Nest Developer Documents](https://developer.nest.com/documentation/cloud/get-started)

 To view the json returned from the API just get your authToken from the SmartApp state data and add it to this Url
 
 *https://developer-api.nest.com/devices?auth=__yourAuthTokenHere__*
 
