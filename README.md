# Nest Manager (Unofficial)

### Nest Manager App
This is the "***unofficial***" SmartThings user created SmartApp and Device handlers.
The SmartApp and Device Handlers work together to provide integration to the SmartThings ecosystem using Nest's Official API. 

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_newInst.jpg" width="330" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/main_page_1.png" width="281" height="500">

### The Devices Types
<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_thermostat_2.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_protect.PNG" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/weather_device.png" width="281" height="500"><img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/ss_nest_pres_dev.png" width="281" height="500">

## Author
* @tonesto7

## Contributors 
* @desertblade
* @E_sch

## Version Info
__Latest App Version:__ 

* Nest Manager - __*v2.0.0*__
* Nest Automations - __*v1.0.0*__

__Latest Device Versions:__

* Nest Presence Device - __*v2.0.0*__
* Nest Protect Device - __*v2.0.0*__
* Nest Thermostat Device - __*v2.0.0*__
* Nest Weather Device - __*v1.0.0*__

## What's New
 * In-app HTML help pages
 * All new polling logic (Also switched to Cron scheduling)
 * Support for sending commands from multiple devices by using Queuing of commands to help prevent being rate-limited by Nest.
 * Fresh nest device tile designs :smile: (These may cause some controversy and be asked to be reverted back)
 * New Weather Device that will be updated with the rest of the devices so using a polling app to update the weather is no longer necessary.
 * Added an Nest Automation Child app as a place to turn thermostats off based on contacts open/closed, outside weather temps, and available shortly after release support for customized remote sensor support.   
 * Much more transparent install process. The App will alert you in the app with a push notification and very clearly in the live logs in the IDE.  
 *It performs tests on the following:* 
 
	 * Verify OAuth is enabled
	 * That the device handlers are installed and published
	 * That your ST account has proper Zip-Code associated with it.
	 * It also notifies you when you are uninstalling that it can't remove the devices because they are being used by other apps/routines  
 

## Links
#### [GitHub Project Issues Link](https://github.com/tonesto7/nest-manager/issues)

#### [SmartThings Community Forum Link](https://community.smartthings.com/t/release-nest-manager/)

#### [Projects Help Page](https://cdn.rawgit.com/tonesto7/nest-manager/master/Documents/help-page.html)

#### [SmartThings IDE GitHub Integration Instructions](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html)

## Things to Know
 * __This is still technically in BETA so you may experience issues!!!__
 * This app **DOES NOT** support Nest Cams and I don't have any to test
 * At this moment the app will only support **One** location and the Thermostats and Protects within
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


## 3rd-Party SmartApp Compatibility (Confirmed)
 * Keep Me Cozy (I & II) (link)
 * [Keenect](https://community.smartthings.com/t/release-keenect-v1-2-0-optional-separate-vo-settings-for-cooling-vent-obstruction-auto-clear/39119)
 * SmartThings Routines
 * [Rule Machine (Rules and Custom Commands)](https://community.smartthings.com/t/rule-machine-version-1-9-released/43204)
 
## Installation

### Method 1: (Recommended)
_Enabling the GitHub Integration in your IDE is by far the easiest way to install and get the latest updates for Nest Connect App, Presence, Protect and Thermostat devices._

If you don't already have Git Integration setup please visit __*[GitHub Integration Instructions](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html)*__

#### The Nest Manager and Nest Automations Apps
 
 * Go to "**My SmartApps**" in the IDE
 * Click on "**Settings**"
 * Click on "**Add New Repository**"
 * Owner: **tonesto7**, Name: **nest-manager**, Branch: **master**
 * Under "**My SmartApps**" Click on "**Update from Repo**" and select ***nest-manager*** 
 * Check the box next to ***nest-manager*** and ***nest-automations*** then click "**Execute Update**"
 * Click on the ***Nest Manager*** app link and select "**Publish**" and "**For me**" 
 * Click on the ***Nest Automations*** app link and select "**Publish**" and "**For me**"
 
	### *You will also need to Enable OAuth under the Nest Manager app preferences in the IDE*

#### The Presence, Protect, Thermostat and Weather Device Handlers
 
 * Go to "**My Device Handlers**" in the IDE (Not necessary if you added repository for the app)
 * Click on "**Settings**"
 * Click on **Add new repository**
 * Owner: **tonesto7**, Name: **nest-manager**, Branch: **master**
 * Under My Device Types Click on ***Update from Repo*** and select the ***nest-manager***
 * Check the box next to ***nest-presence***, ***nest-protect***, ***nest-thermostat***, and ***nest-weather*** then click **Execute Update**
 * Click on each device link and select "**Publish** and **For Me**
 
That's it your Done in the IDE... Just install "**Nest Manager**" from the ***Marketplace > MyApps*** under the mobile app.

When updates are available to the source code you will see the color change from black in the IDE.

------

### Method 2: The Manual Way
#### The Nest Manager App
 
 * Log into your SmartThings account at [https://graph.api.smartthings.com/](https://graph.api.smartthings.com/)
 * Go to "**My SmartApps**"
 * Click on "**+ New SmartApp**"
 * Choose "**From Code**"
 * Copy source code from ***nest-manager.src***
 * Click "**Create**"
 * Click on "**Publish**" and "**For me**" 
 * Go into SmartApp **Nest Manager** Settings
 * Click on "**App Settings**" and ***enable OAuth*** and click "**Update**"
 
#### The Nest Automations App
 * Go to "**My SmartApps**"
 * Click on "**+ New SmartApp**"
 * Choose "**From Code**"
 * Copy source code from ***nest-automations.src***
 * Click "**Create**"
 * Click on "**Publish**" and "**For me**"
 
#### The Presence, Protect, and Thermostat Device Handlers
 * Go to "**My Device Handlers**"
 * Create "**New Device Handler**"
 * Choose "**From Code**"
 * Copy Source code from *nest-protect.src*
 * Repeat for ***nest-presence.src***
 * Repeat for ***nest-thermostat.src***
 * Remember to open each device and click "**Publish**" and "**For me**"

#### Setting up Nest Manager App
 * In the SmartThings Mobile App
 * Go to "**Marketplace**" and select "**SmartApps**"
 * At the bottom of the list, select "**My Apps**"
 * Select "**Nest Manager**" from the list.
 * Enter you Nest Login credentials when prompted.
 * Choose **Structure**
 * Choose **Thermostats**
 * Choose **Protects**
 * Choose *Add Presence Device* (Optional)
 * Choose *Add Weather Device* (Optional)
 * Modify any preferences you would like (Optional)
 * Tap on "**Done**"

## Issues and Troubleshooting

### Issues
*If you are experiencing any issues, please let us know by heading over to projects issues page on GitHub. If you don't see the issue reported please help open an new one and provide as much detail as you can.*

**[Project Issues Link](https://github.com/tonesto7/st-nest-unofficial/issues)** 

In an effort to make it easier for some of you troubleshoot without having to use the IDE constantly.  I have added in a diagnostic option under the preference section of the SmartApp.  
Once this is enabled it will begin to store non-user identifiable error logs from the app and store in a local state variable of the app.  
This feature will help you to view and export the logs (*also see the tip below*) directly from the Smart App. This also allows you to copy & paste them into the issues form on GitHub.

**Tip:** If you enable diagnostic logs in the app you can share the logs from with in the app you can store the direct link to log JSON file on you computer and browse directly to the log everytime you need to review the errors.

_______

### Troubleshooting
**Tip:** *The most common issue is forgetting to enable oAuth for the app under SmartThings*

Please check the Help Page before posting questions in the community forum. 
**[Nest Manager Help Page](https://cdn.rawgit.com/tonesto7/nest-manager/master/Documents/help-page.html)**

_______
### Feature Requests
 * We love new ideas so please head on over to github and open an issue for the feature you would like to see.  This will help prioritize what is important and what is not.
 * There is a Google Form that will allow you to vote for the newest features. ***[Voting Form](https://docs.google.com/forms/d/1bkGy14QyjLedpM31CQ4t6m7UIbxbNH8PCUAdB_-EB08/viewform)***

_______
### Donations
 * While donations are very much appreciated they are not expected or required.  If you feel the need to do so :smile: here is the ***[donation link](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2CJEVN439EAWS)***

_______
### Nest API Documentation
 [Nest Developer Documents](https://developer.nest.com/documentation/cloud/get-started)

 To view the json returned from the API just get your authToken from the SmartApp state data and add it to this Url
 
 *https://developer-api.nest.com/devices?auth=__yourAuthTokenHere__*
 
