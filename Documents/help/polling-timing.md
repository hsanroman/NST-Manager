## Polling & Timing Preferences

#### What is it For?
Polling preferences allows you to fine tune when data is polled in from nest.
There are two calls made: 

* One for the Location data
* The other is all Device data for the Location

#### If you are tinkering with the Devices modes and temps a lot the API has rate limiting.  It will block any calls for 60 to 120 seconds.  In everyday use this will not happens

#### Do not schedule polls to soon because Nest will start Rate limiting.  45-60 second device polls seem to be very reliable
For more info on Nest's Rate Limiting visit [here](https://developer.nest.com/documentation/cloud/data-rate-limits/)

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/poll_prefs_page_1.png" width="281" height="500">

----------
#### 1. Device Poll Rate
This is the value used when scheduling the next refresh of Device data from the API.

* *The default value is every 60 seconds*
* ***The custom values allowed are between (30-84600) seconds***
 

----------

#### 2. Location Poll Rate
This is the value used when scheduling the next refresh of Location data from the API.

* *The default value is every 180 seconds*
* The lowest I would set this would be 120 seconds.  
* ***The custom values allowed are between (30-84600) seconds***
	
----------
	
#### 3. Forced Refresh Limit
This is the value is the Delay used when using Refresh or App Touch button. Basically it's how much time needs to pass before it will force a refresh of the data.

* *The default value is every 10 seconds*

----------

#### 4. Manual Temp Change Delay
This is the the Delay used when changing temp manually from thermostat device. This allows you to hit the change buttons in succession and after the final tap the delay is used before sending the command to Nest.

* *The default value is 4 seconds*

----------

#### Only Update Children on New Data
There is a toggle that will allow you to set the poller to only update child devices when new/changed data is received from the Nest API.  This is here to reduce the load slightly on SmartThings.

* *The default value is Off*

----------

#### Advanced Polling Options
This ***Should Only*** be used if are experiencing Polling issues.  The selected devices events are used to determine if a scheduled poll was missed and reschedule it.
##### Please select as FEW devices as possible! 
#####More devices will not necessarily make for better polling!

<img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Screenshots/App/poll_prefs_page_2.png" width="281" height="500">