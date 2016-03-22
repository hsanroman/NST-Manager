/********************************************************************************************
|    Application Name: Nest Manager                                                         |
|    Author: Anthony S. (@tonesto7), 														|
|	 Contributors: Ben W. (@desertblade) Eric S. (@E_sch)                  					|
|                                                                                           |
|    Initial code was loosely based off of the SmartThings Ecobee App                       |
|********************************************************************************************
|    There maybe portions of the code that may resemble code from other apps in the         | 
|    community. I may have used some of it as a point of reference.                         |
|    Thanks go out to those Authors!!!                                                      |
|                                                                                           |
|    I apologize if i've missed anyone.  Please let me know and I will add your credits     |
|                                                                                           |
|    ### I really hope that we don't have a ton or forks being released to the community,   |
|    ### I hope that we can collaborate and make app and device type that will accomodate   |
|    ### every use case                                                                     |
*********************************************************************************************/
 
import groovy.json.*
import groovy.time.*
import java.text.SimpleDateFormat

definition(
    name: "${textAppName()}",
    namespace: "${textNamespace()}",
    author: "${textAuthor()}",
    description: "${textDesc()}",
    category: "My Apps",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/thermostat_blue%401x.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/thermostat_blue%402x.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/thermostat_blue%402x.png",
    //singleInstance: true,
    oauth: true )

{
    appSetting "clientId"
    appSetting "clientSecret"
}

def appVersion() { "1.0.4" }
def appVerDate() { "3-19-2016" }
def appVerInfo() {
	"V1.0.4 (Mar 19th, 2016)\n" +
    "Fixed: Handles missing ST timezone\n" +
    "Added: Pre-Req check for missing ST timezone/zipcodes on first run\n" +
    "Added: Device Handler Install Test On first run.\n" +
    "Added: Fix for logging missing device handlers #19.\n\n" +
    
    "V1.0.1 (Mar 16th, 2016)\n" +
    "Fixed: Diagnostic Log Overflow\n" +
    "Added: Option to enable 24 hour time display in devices #18.\n\n" +
    
	"V1.0.0 (Mar 16th, 2016)\n" +
    "Fixed: API Info page duplication Issue #9\n" +
    "Added: Thermostat device preference to disable changing mode to Auto when location is Away.\n\n" +
    "------------------------------------------------"
}

preferences {
    page(name: "authPage", title: "Nest", nextPage:"", content:"authPage", uninstall: true, install:true)
    page(name: "prefsPage")
    page(name: "infoPage")
    page(name: "nestInfoPage")
    page(name: "structInfoPage")
    page(name: "tstatInfoPage")
    page(name: "protInfoPage")
    page(name: "pollPrefPage")
    page(name: "debugPrefPage")
    page(name: "notifPrefPage")
    page(name: "diagPage")
    page(name: "resetDiagQueuePage")
    page(name: "quietTimePage")
    page(name: "modePresPage")
    page(name: "devPrefPage")
    page(name: "nestLoginPrefPage")
    page(name: "nestTokenResetPage")
}

mappings {
    path("/oauth/initialize") 	{action: [GET: "oauthInitUrl"]}
    path("/oauth/callback") 	{action: [GET: "callback"]}
    path("/renderLogs")			{action: [GET: "renderLogJson"]}
    path("/renderState")		{action: [GET: "renderStateJson"]}
}

def authPage() {
	//log.trace "authPage()"
	if(!atomicState?.preReqTested) { preReqCheck() }
    if(!atomicState?.testedDhInst) { deviceHandlerTest() }
    
    getWebFileData()
    //atomicState.exLogs = [] //Uncomment this is you are seeing a state size is over 100000 error and it will reset the logs
    
    if(!atomicState.accessToken) { atomicState.accessToken = createAccessToken() }
    
    def description
    def uninstallAllowed = false
    def oauthTokenProvided = false
	
    if(atomicState.authToken) {
    	description = "You are connected."
        uninstallAllowed = true
        oauthTokenProvided = true
       	setStateVar(true)
    } else { description = "Click to enter Nest Credentials" }
	
    
    def redirectUrl = buildRedirectUrl
    //log.debug "RedirectUrl = ${redirectUrl}"

    if (!oauthTokenProvided && atomicState.accessToken) {
        LogAction("AuthToken not found: Directing to Login Page...", "debug", true)
        return dynamicPage(name: "authPage", title: "Login Page", nextPage: "", uninstall:uninstallAllowed) {
            section("") {
                paragraph appInfoDesc(), image: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/thermostat_blue%401x.png"
                if(!deviceHandlerTest()) {
                	paragraph "Error: Device Handlers are missing.  Please visit the IDE and verify that the required Devices have been installed and Published..."
                }
            }
            section(){
                paragraph "Tap 'Nest Login' below to authorize SmartThings to access your Nest Account.\nAfter logon you will be taken to the 'Works with Nest' page. Read the info and if you 'Agree' press the 'Accept' button."
                href url: redirectUrl, style:"embedded", required: true, title: "Nest Login", description: description
            }
        }
    } else {
        return dynamicPage(name: "authPage", title: "Main Page", nextPage: "", uninstall: uninstallAllowed) {
            section("") {
                paragraph appInfoDesc(), image: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/thermostat_blue%401x.png"
                if(!deviceHandlerTest()) {
                	paragraph "Error: Device Handlers are missing.  Please visit the IDE and verify that the required Devices have been installed and Published..."
                }
                if(isAppUpdateAvail()) {
                	paragraph "There is an App Update available!!!\nCurrent: v${appVersion()} | New: ${atomicState.appData.versions.app.ver}\nPlease visit the IDE to update.", 
                    	image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/update_icon3.png")
                    href "infoPage", title:"Update Change Log...", description: "Tap to view..."
                }
            }
			def structs = getNestStructures()
            def structDesc = !structs?.size() ? "No Locations Found" : "Found (${structs?.size()}) Locations..."
        	LogAction("Locations: Found ${structs?.size()} (${structs})", "info", false)

            section("Select your Location:") {
                //paragraph "Select the Location from your Nest account."
                input(name: "structures", title:"Nest Locations", type: "enum", required: true, multiple: false, submitOnChange: true, description: structDesc, metadata: [values:structs], 
                			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_structure_icon.png")) }
			if (structures) {
               	atomicState?.structures = structures ? structures : null
                def stats = getNestThermostats()
                def statDesc = stats.size() ? "Found (${stats.size()}) Thermostats..." : "No Thermostats" 
    			LogAction("Thermostats: Found ${stats?.size()} (${stats})", "info", false)

				def coSmokes = getNestProtects()
                def coDesc = coSmokes.size() ? "Found (${coSmokes.size()}) Protects..." : "No Protects"
    			LogAction("Protects: Found ${coSmokes.size()} (${coSmokes})", "info", false)
                
                section("Select your Devices:") {
                    if (!stats.size() && !coSmokes.size()) { paragraph "No Devices were found..." }
                    if (stats?.size() > 0) { 
                    	input(name: "thermostats", title:"Nest Thermostats", type: "enum", required: false, multiple: true, submitOnChange: true, description: statDesc, metadata: [values:stats], 
                        		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_like.png")) 
                        atomicState?.thermostats = thermostats ? statState(thermostats) : null }
                    
                    if (coSmokes.size() > 0) { 
                    	input(name: "protects", title:"Nest Protects", type: "enum", required: false, multiple: true, submitOnChange: true, description: coDesc, metadata: [values:coSmokes], 
                    			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/protect_icon.png")) 
                    }
                    atomicState?.protects = protects ? coState(protects) : null
                    input(name: "presDevice", title:"Use Nest as Presence Device?", type: "bool", default: false, required: false, submitOnChange: true, 
                    			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/nest_dev_pres_icon.png")) 
                    atomicState?.presDevice = presDevice ? true : false
                }
                
               	if((atomicState?.isInstalled && atomicState.structures && (atomicState.thermostats || atomicState.protects)) || diagLogs) {
                    def diagInfoDesc = !diagLogs ? "API Info:" : "Diagnostics/Info:"
                    section(diagInfoDesc) {
                   		if(atomicState.structures && (atomicState.thermostats || atomicState.protects) && atomicState?.isInstalled) {
               				href "nestInfoPage", title: "View Nest API Info...", description: "Tap to view info...",
                   				image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/api_icon.png")
                		}
                        if(diagLogs) {
                   			href "diagPage", title:"View Diagnostics...", description:"Log Entries: (${getExLogSize()} Items)\nTap to view more...", 
                       			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/diag_icon.png")
                   		}
                	}
                }
                
                section(" ") { 
        			href "prefsPage", title: "Preferences", description: "Notifications: (${pushStatus()})\nApp Logs: (${debugStatus()})\nDevice Logs: (${childDebugStatus()})\nTap to configure...", 
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/settings_icon.png")
                }
                
            }
            section(" ") { 
                href "infoPage", title:"App Info and Licensing", description: "Tap to view...", 
                		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/info.png")
            }
        }
    }
}

//Defines the Preference Page
def prefsPage() {
	dynamicPage(name: "prefsPage", title: "Application Preferences", nextPage: "", install: false) {
    	
        section("Polling:") {
        	def pollStatus = !atomicState?.pollingOn ? "Not Active" : "Active"
        	href "pollPrefPage", title: "Polling Preferences", description: "Polling: ${pollStatus}\nTap to configure...", 
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/timer_icon.png")
        }
        section("Devices:") {
        	href "devPrefPage", title: "Device Customization", description: "Tap to configure...", 
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/device_pref_icon.png")
        }
        section("Nest Presence Automation:") {
        	def presDesc = (awayModes && homeModes) ? "Modes are Selected\n\nTap to configure..." : "Tap to configure..."
        	href "modePresPage", title: "Nest Presence Automation", description: presDesc,
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/nest_dev_pres_icon.png")
       	}
        
        section("Notifications:") {
        	href "notifPrefPage", title: "Notifications", description: "Notifications: (${pushStatus()})\n${getQTimeLabel()}\nTap to configure...", 
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/notification_icon.png")
        }
        
        section("Logging:") {
        	href "debugPrefPage", title: "Logs", description: "App Logs: (${debugStatus()})\nDevice Logs: (${childDebugStatus()})\nTap to configure...", 
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/log.png")
        }
        
        section ("Diagnostics:") {
            	input (name: "diagLogs", type: "bool", title: "Enable Diagnostics?", required: false, defaultValue: false, submitOnChange: true,
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/diag_icon.png"))
                paragraph "This will store errors withing the app which you can view. You can share those logs with the developer to help resolve issues..."
            	if (diagLogs && !atomicState?.diagLogs) { LogAction("Diagnostic Log Queuing is Enabled...", "info", false) }
            	else if (!diagLogs && atomicState?.diagLogs) { LogAction("Diagnostic Log Queuing is Disabled...", "info", false) }
                if(!atomicState?.diagLogs) { atomicState.exLogs = [] }
            	atomicState.diagLogs = diagLogs ? true : false
        }
        section ("Time Display:") {
            	input "use24Time", "bool", title: "Use 24 Hour Time?",  defaultValue: false, submitOnChange: true, required: false,
                		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/time_24_icon.png")
            	atomicState.use24Time = use24Time ? true : false
        }
        section ("App Icons:") {
            	input (name: "disAppIcons", type: "bool", title: "Disable App Icons?", required: false, defaultValue: false, submitOnChange: true, 
                        image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/no_icon.png"))
            	atomicState.disAppIcons = disAppIcons ? true : false
        }
        section("Nest Login:") {
        	href "nestLoginPrefPage", title: "Nest Login Preferences", description: "Tap to configure...",
                    image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/login_icon.png")
        }
		section("Change the Name of the App:") {
            label title:"Application Label (optional)", required:false 
    	}
	}
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has been installed...")
    atomicState.isInstalled = true
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has updated settings...")
    if(!atomicState.isInstalled) { atomicState.isInstalled = true }
}

def uninstalled() {
    atomicState.thermostats = []
    atomicState.protects = []
    addRemoveDevices()
    //Revokes Smartthings endpoint token...
	revokeAccessToken()
	//Revokes Nest Auth Token
    if(atomicState?.authToken) { revokeNestToken() }
    //sends notification of uninstall
    sendNotificationEvent("${textAppName()} is uninstalled...")
}

def initialize() {
	setStateVar()
	unsubscribe()
    addRemoveDevices()
    subscriber()
    setPollingState()
    schedFollowPoll()
	//getEndpointUrl() //This can stay for now
}

def subscriber() {
	subscribe(location, null, pollWatcher, [filterEvents:false])
    subscribe(app, onAppTouch)
	subscribe(location, "sunrise", pollWatcher, [filterEvents: false])
	subscribe(location, "sunset", pollWatcher, [filterEvents: false])
	subscribe(location, "mode", modeWatcher, [filterEvents: false])
	subscribe(location, "sunriseTime", pollWatcher, [filterEvents: false])
	subscribe(location, "sunsetTime", pollWatcher, [filterEvents: false])
    subscribe(location, "routineExecuted", pollWatcher, [filterEvents: false])
    if(temperatures) { subscribe(temperatures, "temperature", pollWatcher, [filterEvents: false]) }
    if(energies) { subscribe(energies, "energy", pollWatcher, [filterEvents: false]) }
    if(powers) { subscribe(powers, "power", pollWatcher, [filterEvents: false]) }
}

def setPollingState() {
	if (!atomicState.thermostats && !atomicState.protects) { 
    	LogAction("No Devices Selected...Polling is Off!!!", "info", true)
        atomicState.pollingOn = false 
    } else { 
    	if(!atomicState.pollingOn) { 
        	LogAction("Polling is Now ACTIVE!!!", "info", true)
            poll()
            atomicState.pollingOn = true }
    	if(!atomicState.isInstalled) { poll(true) }
    }
}

def onAppTouch(event) {
    poll(true)
}

/************************************************************************************************
|								API/Device Polling Methods										|
*************************************************************************************************/

def pollFollow() { if(isPollAllowed()) { poll() } }

def pollWatcher(evt) {
    if (isPollAllowed() && (ok2PollDevice() || ok2PollStruct())) { poll() }
}

def poll(force = false) {
	//setStateVar()
    schedFollowPoll()
   	if(isPollAllowed()) { 
   		def dev = false
        def str = false
        if (force == true) { forcedPoll() }
   		else if(!force) {
   			if(ok2PollDevice()) { 
            	LogAction("Polling Devices...(Last Updated (${getLastDevicePollSec()}) seconds ago)", "info", true)
            	dev = getApiDeviceData()
                scheduleNextPoll("dev")
            }
            if(ok2PollStruct()) {
            	if(getLastStructPollSec() > atomicState?.pollStrValue) { pollStr() }
            }    
            else { 
    			LogAction("Too Soon to poll Data!!! - Devices Last Updated (${getLastDevicePollSec()}) seconds ago... | Structures Last Updated (${getLastStructPollSec()}) seconds ago...", "info", false) 
    			scheduleNextPoll()
    		}
		}
        if(atomicState?.updChildOnNewOnly) {
        	if (force || dev || str || (getLastChildUpdSec() > 1800)) { updateChildData() }
        }
        else { updateChildData() }
        if(getLastWebUpdSec() > 1800) {
        	getWebFileData() //This reads a JSON file from a web server with timing values and version numbers
        }
        notificationCheck() //Checks if a notification needs to be sent for a specific event
	}
}

def pollStr() {
   	if(isPollAllowed()) { 
        def str = false
        if(ok2PollStruct()) {
   			LogAction("Polling Structures...(Last Updated (${getLastStructPollSec()}) seconds ago)", "info", true)
            str = getApiStructureData()
            scheduleNextPoll("str")
       	} 
        if(str) { updateChildData() }
    }
}

def schedDevPoll(val = null) {
	def pollVal = !val ? atomicState?.pollValue.toInteger() : val.toInteger()
    log.debug "scheduling Device Poll for (${pollVal}) seconds"
    runIn(pollVal, "poll",[overwrite: true])
}

def schedStrPoll(val = null) {
	def pollStrVal = !val ? atomicState?.pollStrValue.toInteger() : val.toInteger()
    log.debug "scheduling Structure Poll for (${pollStrVal}) seconds"
    runIn(pollStrVal, "pollStr",[overwrite: true])
}

def schedFollowPoll() {
    runIn(90, "pollFollow",[overwrite: true])
}

def scheduleNextPoll(type = null) {
    if(type == "dev" || !type) {
    	def pollVal = atomicState?.pollValue ? atomicState?.pollValue.toInteger() : 60
    	def lastDevPoll = getLastDevicePollSec().toInteger()
    	def newPollVal = ((pollVal - lastDevPoll) > 6) ? (int) (pollVal - lastDevPoll) : pollVal 
    	if	(newPollVal < pollVal) { schedDevPoll(newPollVal) }
    	else { schedDevPoll(pollVal) }
	}
    if(type == "str" || !type) {
    	def pollStrVal = atomicState?.pollStrValue ? atomicState?.pollStrValue.toInteger() : 180
    	def lastStrPoll = getLastStructPollSec().toInteger()
    	def newStrPollVal = ((pollStrVal - lastStrPoll) > 6) ? (int) (pollStrVal - lastStrPoll) : pollStrVal
    	if	(newStrPollVal < pollStrVal) { schedStrPoll(newStrPollVal) }
    	else { schedStrPoll(pollStrVal) }
    }
}

def forcedPoll(type = null) {
	log.warn "forcedPoll($type) received..."
	def lastFrcdPoll = getLastForcedPollSec()
    def pollWaitVal = !atomicState?.pollWaitValue ? 10 : atomicState?.pollWaitValue.toInteger()
    if (lastFrcdPoll > pollWaitVal) { //<< This limits manual forces to 10 seconds or more
   		LogAction("Forcing data poll... Last forced Poll was ${lastFrcdPoll} seconds ago.", "info", true)
    	if(!type) {
           	log.debug "Forcing Device and Structure Data Poll..."
           	getApiDeviceData()
          	getApiStructureData()
        }
        else if (type == "dev") { 
           	log.debug "Forcing Device Data Poll..."
            getApiDeviceData() 
        }
       	else if (type == "str") { 
           	log.debug "Forcing Structure Data Poll..."
            getApiStructureData() 
        }
       	atomicState?.lastForcePoll = getDtNow()
       	scheduleNextPoll()
        updateChildData()
   	} else { LogAction("Too Soon to Force data poll.  It's only been (${lastFrcdPoll}) seconds of the minimum (${atomicState.pollWaitValue})...", "debug", true) }
}

def postStrCmd() { 
	log.trace "postStrCmd()"
    forcedPoll("str") }
def postDevCmd() { 
	log.trace "postDevCmd()"
    forcedPoll("dev") }

def getApiStructureData() {
	LogAction("getApiStructureData()", "info", false)
    atomicState?.lastStrucDataUpd = getDtNow()
    def params = [
    	uri: getNestApiUrl(),
    	path: "/structures",
    	headers: ["Content-Type": "text/json", "Authorization": "Bearer ${atomicState?.authToken}"]
    ]
    try {
        httpGet(params) { resp ->
            if(resp.status == 200) {
            	LogTrace("API Structure Resp.Data: ${resp?.data}")
                if(!resp?.data?.equals(atomicState?.structData) || !atomicState?.structData) { 
                	LogAction("API Structure Data HAS Changed... Updating State data...", "debug", true)
                    atomicState?.structData = resp?.data
					atomicState?.apiIssues = false                  
                    return true
                }
                else { return false 
                	LogAction("API Structure Data HAS NOT Changed... Skipping Child Update...", "debug", true)
                }

			} else { 
            	LogAction("getApiStructureData - Received a diffent Response than expected: Resp (${resp?.status})", "error", true, true) 
            	return false
            }
        }
    } 
    catch(ex) {
        if(ex instanceof groovyx.net.http.HttpResponseException) {
        	if (ex.message.contains("Too Many Requests")) {
            	log.warn "Received '${ex.message}' response code..."
                atomicState?.apiIssues = true
                return false
            }
        } else { 
        	LogAction("getApiStructureData Exception: ${ex}", "error", true, true) 
        	return false
        }
    }
}

def getApiDeviceData() {
	LogAction("getApiDeviceData()", "info", false)
    atomicState?.lastDevDataUpd = getDtNow()
    def params = [
    	uri: getNestApiUrl(),
    	path: "/devices",
    	headers: ["Content-Type": "text/json", "Authorization": "Bearer ${atomicState.authToken}"]
    ]
    try {
        httpGet(params) { resp ->
        	if(resp?.status == 200) {
            	LogTrace("API Device Resp.Data: ${resp?.data}")
                
                if(!resp?.data.equals(atomicState?.deviceData) || !atomicState?.deviceData) { 
                    LogAction("API Device Data HAS Changed... Updating State data...", "debug", true)
                	atomicState?.deviceData = resp?.data
                    atomicState?.apiIssues = false  
                	return true
                }
                else { return false 
                	LogAction("API Device Data HAS NOT Changed... Skipping Child Update...", "debug", true)
                }
                
            } else { 
            	LogAction("getApiDeviceData - Received a diffent Response than expected: Resp (${resp?.status})", "error", true, true) 
               	return false
            }
        }
    } 
    catch(ex) {
    	if(ex instanceof groovyx.net.http.HttpResponseException) {
        	if (ex.message.contains("Too Many Requests")) {
            	log.warn "Received '${ex.message}' response code..."
                atomicState?.apiIssues = true
                return false
            }
        } else { 
        	LogAction("getApiDeviceData Exception: ${ex}", "error", true, true) 
        	return false
        }
    }
}

def updateChildData() {
	LogAction("updateChildData()", "info", true)
	try {
    	atomicState?.lastChildUpdDt = getDtNow()
		getAllChildDevices().each {
    		def devId = it.deviceNetworkId
			
			if(atomicState?.thermostats && atomicState?.deviceData?.thermostats[devId]) {
            	def tData = atomicState?.deviceData?.thermostats[devId]
				LogTrace("UpdateChildData >> Thermostat id: ${devId} | data: ${tData}")
            	it.generateEvent(tData) //parse received message from parent
                atomicState?.tDevVer = !it.devVer() ? "" : it.devVer()
            	return true
        	}
        
        	else if(atomicState?.protects && atomicState?.deviceData?.smoke_co_alarms[devId]) {
            	def pData = atomicState?.deviceData?.smoke_co_alarms[devId]
            	LogTrace("UpdateChildData >> Protect id: ${devId} | data: ${pData}")
            	it.generateEvent(pData) //parse received message from parent
                atomicState?.pDevVer = !it.devVer() ? "" : it.devVer()
            	return true
        	}
            
            else if(atomicState?.presDevice && devId == "NestPresenceDevice") {
            	LogTrace("UpdateChildData >> Presence id: ${devId}")
                it.generateEvent(null)
                atomicState?.presDevVer = !it.devVer() ? "" : it.devVer()
            	return true
        	} 
            
        	else if(!atomicState?.deviceData?.thermostats[devId] && !atomicState?.deviceData?.smoke_co_alarms[devId]) {
            	LogAction("Device connection removed? no data for ${devId}", "warn", true, true)
            	return null
        	}
        	else {
        		LogAction("updateChildData() for ${devId} after polling", "error", true, true)
        		return null
    		}
        }
    }
    catch (ex) {
    	LogAction("updateChildData Exception: ${ex}", "error", true, true)
   	}
}

def locationPresence() {
	if (atomicState?.structData[atomicState?.structures]) {
		def data = atomicState?.structData[atomicState?.structures]
        LogAction("Location Presence: ${data?.away}", "debug", false)
    	LogTrace("Location Presence: ${data?.away}")
		return data?.away.toString()
    }
    else { return null }
}

def apiIssues() {
	return atomicState?.apiIssues ? true : false
    LogAction("API Issues: ${atomicState.apiIssues}", "debug", false) 
}

def modeWatcher(evt) { checkPresMode() }

def checkPresMode() { 
	if (homeModes) {
    	homeModes?.each { m ->
        	if(m.toString() == location.mode.toString()) { 
            	LogAction("The mode ($location.mode) has triggered Nest 'Home'", "info", true)
        		setStructureAway(null, false) 
        	}
     	}  
    } 
   	if (awayModes) {
    	awayModes?.each { m ->
        	if(m.toString() == location.mode.toString()) { 
	           	LogAction("The mode ($location.mode) has triggered Nest 'Away'", "info", true)
                setStructureAway(null, true) 
            }
        }
    } 
}    

def isPollAllowed() { return (atomicState?.pollingOn && (atomicState?.thermostats || atomicState?.protects)) ? true : false }
def ok2PollDevice() { return (!atomicState?.lastDevDataUpd || ((getLastDevicePollSec() + 2) > (!atomicState.pollValue ? 60 : atomicState?.pollValue.toInteger()))) ? true : false }
def ok2PollStruct() { return (!atomicState?.lastStrucDataUpd|| ((getLastStructPollSec() + 2) > (!atomicState.pollStrValue ? 60 : atomicState?.pollStrValue.toInteger()))) ? true : false }
def getLastDevicePollSec() { return !atomicState?.lastDevDataUpd ? 1000 : GetTimeDiffSeconds(atomicState?.lastDevDataUpd).toInteger() }
def getLastStructPollSec() { return !atomicState?.lastStrucDataUpd ? 1000 : GetTimeDiffSeconds(atomicState?.lastStrucDataUpd).toInteger() }
def getLastForcedPollSec() { return !atomicState?.lastForcePoll ? 1000 : GetTimeDiffSeconds(atomicState?.lastForcePoll).toInteger() }
def getLastChildUpdSec() { return !atomicState?.lastChildUpdDt ? 1000 : GetTimeDiffSeconds(atomicState?.lastChildUpdDt).toInteger() }
def getLastWebUpdSec() { return !atomicState?.lastWebUpdDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastWebUpdDt).toInteger() }
/************************************************************************************************
|										Nest API Commands										|
*************************************************************************************************/

def apiVar() {
	def api = [	
    	types:	[	
        			struct:"structures", 
    				cos:"devices/smoke_co_alarms", 
                	tstat:"devices/thermostats", 
                	meta:"metadata"
              	],
    	objs:	[
    				targetF:"target_temperature_f", 
        			targetC:"target_temperature_c", 
        			targetLowF:"target_temperature_low_f",
        			targetLowC:"target_temperature_low_c",
        			targetHighF:"target_temperature_high_f",
        			targetHighC:"target_temperature_high_c",
        			fanActive:"fan_timer_active",
        			fanTimer:"fan_timer_timeout",
        			hvacMode:"hvac_mode",
        			away:"away"
    			],
        modes: 	[
    				heat:"heat",
        			cool:"cool",
        			heatCool:"heat-cool",
        			off:"off"
    			]
   	]
    return api
}

def setStructureAway(child, value) {
	def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId
	def val = value?.toBoolean()
	LogAction("setStructureAway: ${devId} (${val})", "debug", false, true)
    if(childDebug && child) { child?.log("setStructureAway: ${devId} | (${temp})${unit}") }
    try {
		if(val) { 	
        	if(sendNestApiCmd(getNestApiUrl(), atomicState?.structures, apiVar().types.struct, apiVar().objs.away, "away", child)) { runIn(3, "postStrCmd") } 
        }
    	else { 		
        	if(sendNestApiCmd(getNestApiUrl(), atomicState?.structures, apiVar().types.struct, apiVar().objs.away, "home", child)) { runIn(3, "postStrCmd") } 
        }
    	return true
    }
    catch (ex) { 
    	LogAction("setStructureAway Exception: ${ex}", "debug", true, true) 
    	if (childDebug && child) { child?.log("setStructureAway Exception: ${ex}", "error") }
        return false
    }
}

def setHvacMode(child, mode) {
	def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId
	LogAction("setHvacMode: ${devId} (${mode})", "debug", false, true)
    try {
    	if(sendNestApiCmd(getNestApiUrl(), devId, apiVar().types.tstat, apiVar().objs.hvacMode, mode.toString(), child)) { runIn(3, "postDevCmd") }
    	return true
    }
    catch (ex) { 
    	LogAction("setHvacMode Exception: ${ex}", "error", true, true) 
    	if(childDebug && child) { child?.log("setHvacMode Received: ${devId} (${mode})", "debug") }
        return false
    }
}

def setTargetTemp(child, unit, temp) {
	def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId
	LogAction("setTargetTemp: ${devId} | (${temp})${unit}", "debug", true, true)
    if(childDebug && child) { child?.log("setTargetTemp: ${devId} | (${temp})${unit}") }
    try {	
		if(unit == "C") { 
        	if(sendNestApiCmd(getNestApiUrl(), devId, apiVar().types.tstat, apiVar().objs.targetC, temp, child)) { runIn(3, "postDevCmd") }
        }
		else { 
        	if(sendNestApiCmd(getNestApiUrl(), devId, apiVar().types.tstat, apiVar().objs.targetF, temp, child)) { runIn(3, "postDevCmd") }
        }
    	return true
    }
    catch (ex) { 
    	LogAction("setTargetTemp Exception: ${ex}", "error", true, true) 
    	if(childDebug && child) { child?.log("setTargetTemp Exception: ${ex}", "error") }
		return false
    }
}

def setTargetTempLow(child, unit, temp) {
	def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId
	LogAction("setTargetTempLow: ${devId} | (${temp})${unit}", "debug", true, true)
    if(childDebug && child) { child?.log("setTargetTempLow: ${devId} | (${temp})${unit}") }
    try {	
		if(unit == "C") { 
        	if(sendNestApiCmd(getNestApiUrl(), devId, apiVar().types.tstat, apiVar().objs.targetLowC, temp, child)) { runIn(3, "postDevCmd") }
        }
		else { 
        	if(sendNestApiCmd(getNestApiUrl(), devId, apiVar().types.tstat, apiVar().objs.targetLowF, temp, child)) { runIn(3, "postDevCmd") } 
        }
    	return true
    }
    catch (ex) { 
    	LogAction("setTargetTempLow Exception: ${ex}", "error", true, true)
    	if(childDebug && child) { child?.log("setTargetTempLow Exception: ${ex}", "error") }
        return false
	}
}

def setTargetTempHigh(child, unit, temp) {
	def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId
	LogAction("setTargetTempHigh: ${devId} | (${temp})${unit}", "debug", true, true)
    if(childDebug && child) { child?.log("setTargetTempHigh: ${devId} | (${temp})${unit}") }
    try {
		if(unit == "C") { 
        	if(sendNestApiCmd(getNestApiUrl(), devId, apiVar().types.tstat, apiVar().targetHighC, temp, child)) { runIn(3, "postDevCmd") }
        }
		else { 
        	if(sendNestApiCmd(getNestApiUrl(), devId, apiVar().types.tstat, apiVar().objs.targetHighF, temp, child)) { runIn(3, "postDevCmd") }
        }
    	return true
    }
    catch (ex) { 
    	LogAction("setTargetTempHigh Exception: ${ex}", "error", true, true)
    	if(childDebug && child) { child?.log("setTargetTempHigh Exception: ${ex}", "error") }
        return false
    }
}

def setFanMode(child, fanOn) {
	def devId = !child?.device?.deviceNetworkId ? child?.toString() : child?.device?.deviceNetworkId
	def val = fanOn.toBoolean()
	LogAction("setFanMode: ${devId} (${val})", "debug", false, true)
    if(childDebug) { child?.log("setFanMode( devId: ${devId}, fanOn: ${val}, timeVal: ${timeVal}") }
    try {	
		if(sendNestApiCmd(getNestApiUrl(), devId, apiVar().types.tstat, apiVar().objs.fanActive, val, child)) { runIn(3, "postDevCmd") }
     }
    catch (ex) { 
    	LogAction("setFanMode Exception: ${ex}", "error", true, true) 
    	if(childDebug) { child?.log("setFanMode Exception: ${ex}", "error") }
        return false
    }
}

def sendNestApiCmd(uri, typeId, type, obj, objVal, child, redir = false) {
	//LogAction("SendNestApiCmd: ${typeId}, ${type}, ${obj}, ${objVal}, ${redir}", "debug", false, true)
    if(childDebug && child) { child?.log("sendNestApiCmd(uri: ${uri}, typeId: ${typeId}, type: ${type}, obj: ${obj}, objVal: ${objVal}, redir: ${redir}", "debug") }
	def data
    try {
    	def urlPath = redir ? "" : "/${type}/${typeId}"
    	data = new JsonBuilder("${obj}":objVal)
    	def params = [
        	uri: uri,
        	path: urlPath,
        	contentType: "application/json",
        	query: [ "auth": atomicState?.authToken ],
    		body: data.toString()
    	]
    	LogTrace("sendNestApiCmd params: ${params}")
        if(childDebug && child) { child.log("sendNestApiCmd params: ${params}", "debug") }
        httpPutJson(params) { resp ->
        	if (resp.status == 307) {
            	def newUrl = resp.headers.location.split("\\?")
                LogTrace("NewUrl: ${newUrl[0]}")
                sendNestApiCmd(newUrl[0], typeId, type, obj, objVal, child, true)
                if(atomicState?.diagLogs) {
                	atomicState?.lastCmdSent = "$type: (${obj}: ${objVal})"
                	atomicState?.lastCmdSentDt = getDtNow()
                }
            }
            else { 
            	if(childDebug && child) { child?.log("sendNestApiCmd Response: ${resp.status}") }
            }
       	}
        return true
    }   
	catch (ex) {
    	LogAction("sendNestApiCmd Exception: ${ex}", "error", true, true)
        if(childDebug && child) { child?.log("sendNestApiCmd Response Exception: ${ex}", "error") }
        //atomicState?.apiIssues = true
        return false
    }
}


/************************************************************************************************
|								Push Notification Functions										|
*************************************************************************************************/

def notificationCheck() {
	if(recipients) {
		if (atomicState?.missedPollNotif) { missedPollNotify() }
		if (atomicState?.updNotif) { newUpdNotify() }
    }
}

def missedPollNotify() {
	try {
        if(dayOk() && timeOk()  && quietModeOk() && getLastDevicePollSec() > (atomicState?.misPollNotifyWaitVal.toInteger())) {
			if((getLastMisPollMsgSec() > atomicState?.misPollNotifyMsgWaitVal.toInteger())) {
    			sendMsg("Warning", "${app.name} has not refreshed data in the last (${getLastDevicePollSec()}) seconds.  Please try refreshing manually.")
        		atomicState?.lastMisPollMsgDt = getDtNow()
            }
    	}
    } catch (ex) { LogAction("missedPollNotify Exception: ${ex}", "error", true, true) }
}

def newUpdNotify() {
	try {
    	def appUpd = isAppUpdateAvail()
    	def pUpd = isProtUpdateAvail()
        def prUpd = isPresUpdateAvail()
    	def tUpd = isTstatUpdateAvail()
		if(dayOk() && timeOk() && quietModeOk() ) { 
            if((appUpd || pUpd || prUpd || tUpd) && (getLastUpdMsgSec() > atomicState.updNotifyWaitVal.toInteger())) {
    			def appl = !appUpd ? "" : "Manager App: v${atomicState?.appData.versions.app.ver.toString()}, "
    			def prot = !pUpd ? "" : "Protect: v${atomicState?.appData.versions.protect.ver.toString()}, "
                def pres = !prUpd ? "" : "Presence: v${atomicState?.appData.versions.presence.ver.toString()}, "
        		def tstat = !tUpd ? "" : "Thermostat: v${atomicState?.appData.versions.thermostat.ver.toString()}"
        		sendMsg("Info", "Update(s) are available: ${appl}${pres}${prot}${tstat}...  Please visit the IDE to Update your code...")
        		atomicState?.lastUpdMsgDt = getDtNow()
            }
        }
    } catch (ex) { LogAction("newUpdNotify Exception: ${ex}", "error", true, true) }
}

def sendMsg(String msg, String msgType) {
	try {
    	def newMsg = "${msgType}: ${msg}"
		if (location.contactBookEnabled) { 
        	if(recipients) {
        		sendNotificationToContacts(newMsg, recipients)
            	atomicState?.lastMsg = newMsg
        		atomicState?.lastMsgDt = getDtNow()
            	log.debug "Push Message Sent: ${atomicState?.lastMsgDt}"	
            }
		} else {
       		LogAction("contact book not enabled", "debug", true)
       		if (usePush) {
            	sendPush(newMsg)
                atomicState?.lastMsg = newMsg
        		atomicState?.lastMsgDt = getDtNow()
            	log.debug "Push Message Sent: ${atomicState?.lastMsgDt}"
            }
            else if (phone) {
           		sendSms(phone, newMsg)
            	atomicState?.lastMsg = newMsg
        		atomicState?.lastMsgDt = getDtNow()
            	log.debug "SMS Message Sent: ${atomicState?.lastMsgDt}"	
       		}
    	}
    } catch (ex) { LogAction("sendMsg Exception: ${ex}", "error", true, true) }
}

def pushStatus() { return (recipients || phone || usePush) ? (usePush ? "Push Active" : "Active") : "Not Active" } //Keep this
def getLastMsgSec() { return !atomicState?.lastMsgDt ? 1000 : GetTimeDiffSeconds(atomicState?.lastMsgDt).toInteger() }
def getLastUpdMsgSec() { return !atomicState?.lastUpdMsgDt ? 1000 : GetTimeDiffSeconds(atomicState?.lastUpdMsgDt).toInteger() }
def getLastMisPollMsgSec() { return !atomicState?.lastMisPollMsgDt ? 1000 : GetTimeDiffSeconds(atomicState?.lastMisPollMsgDt).toInteger() }

def getRecipientsSize() { return !settings.recipients ? 0 : settings?.recipients.size() }

def getWebFileData() {
	def params = [ 
        uri: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Data/appParams.json",
       	contentType: 'application/json'
    ]
    try {
    	
        httpGet(params) { resp ->
			if(resp.data) {
            	LogAction("Retrieving Latest appParams.json File from Web", "info", true)
				atomicState?.appData = resp?.data
                atomicState?.lastWebUpdDt = getDtNow()
            }
            //LogAction("getGitAppData Resp: ${resp?.data}", "debug", false)
        }	
    }
	catch (ex) {
    	if(ex instanceof groovyx.net.http.HttpResponseException) {
           	log.warn  "appParams.json file not found..."
            return false
        } else { 
        	LogAction("getWebFileData Exception: ${ex}", "error", true, true) 
        	return false
        }
    }
}

def refresh() {
	LogAction("Refresh Received from Device...", "debug", true)
    poll(true, "dev")
}

def ver2IntArray(val) { 
	def ver = val?.split("\\.") 
    return [maj:"${ver[0]?.toInteger()}",min:"${ver[1]?.toInteger()}",rev:"${ver[2]?.toInteger()}"]
}

def getChildWaitVal() { return atomicState.tempChgWaitVal ? atomicState?.tempChgWaitVal.toInteger() : 4 }

def isAppUpdateAvail() {
	try {
    	def newAppVer = !atomicState?.appData.versions.app.ver ? "0.0.0" : atomicState?.appData.versions.app.ver.toString()
		if(	(ver2IntArray(appVersion()).maj.toInteger() < ver2IntArray(newAppVer).maj.toInteger()) || 
       		(ver2IntArray(appVersion()).min.toInteger() < ver2IntArray(newAppVer).min.toInteger()) || 
      		(ver2IntArray(appVersion()).rev.toInteger() < ver2IntArray(newAppVer).rev.toInteger())) { 
       		return true
    	} else { return false }
   	} catch (ex) { LogAction("isAppUpdateAvail Exception: ${ex}", "error", true, true) }
}

def isPresUpdateAvail() {
	try {
        def pVer = !atomicState?.pDevVer ? null : atomicState?.pDevVer.toString()
    	def newPVer = !atomicState?.appData.versions.presence.ver ? "0.0.0" : atomicState?.appData.versions.presence.ver.toString()
		if(pVer) {
        	if(	(ver2IntArray(pVer).maj.toInteger() < ver2IntArray(newPVer).maj.toInteger()) || 
       			(ver2IntArray(pVer).min.toInteger() < ver2IntArray(newPVer).min.toInteger()) || 
       			(ver2IntArray(pVer).rev.toInteger() < ver2IntArray(newPVer).rev.toInteger())) { 
       			return true 
            }
    	} else { return false }
    } catch (ex) { LogAction("isPresUpdateAvail Exception: ${ex}", "error", true, true) }
}

def isProtUpdateAvail() {
	try {
        def pVer = !atomicState?.pDevVer ? null : atomicState?.pDevVer.toString()
    	def newPVer = !atomicState?.appData.versions.protect.ver ? "0.0.0" : atomicState?.appData.versions.protect.ver.toString()
		if(pVer) {
        	if(	(ver2IntArray(pVer).maj.toInteger() < ver2IntArray(newPVer).maj.toInteger()) || 
       			(ver2IntArray(pVer).min.toInteger() < ver2IntArray(newPVer).min.toInteger()) || 
       			(ver2IntArray(pVer).rev.toInteger() < ver2IntArray(newPVer).rev.toInteger())) { 
       			return true
            }
    	} else { return false }
    } catch (ex) { LogAction("isProtUpdateAvail Exception: ${ex}", "error", true, true) }
}

def isTstatUpdateAvail() {
    try {
    	def tVer = !atomicState?.tDevVer ? null : atomicState?.tDevVer.toString()
    	def newTstatVer = !atomicState?.appData.versions.thermostat.ver ? "0.0.0" : atomicState?.appData.versions.thermostat.ver.toString()
		if(tVer) {
            if ((ver2IntArray(tVer).maj.toInteger() < ver2IntArray(newTstatVer).maj.toInteger()) || 
       			(ver2IntArray(tVer).min.toInteger() < ver2IntArray(newTstatVer).min.toInteger()) || 
       			(ver2IntArray(tVer).rev.toInteger() < ver2IntArray(newTstatVer).rev.toInteger())) {
    			return true
            }
    	} else { return false }
    } catch (ex) { LogAction("isTstatUpdateAvail Exception: ${ex}", "error", true, true) }
}
/************************************************************************************************
|			This Section Discovers all structures and devices on your Nest Account.				|
|			It also Adds/Removes Devices from ST												|
*************************************************************************************************/

// This is really the gather data from Nest as single routine
def getNestStructures() {
	LogTrace("Getting Nest Structures")
    def struct = [:]
    try {
    	if(ok2PollStruct()) { getApiStructureData() }
    	
        if (atomicState?.structData) {
            def structs = atomicState?.structData
            structs.eachWithIndex { struc, index ->
            	def strucId = struc?.key
            	def strucData = struc?.value

            	atomicState?.structures = strucId

            	def dni = [strucData?.structure_id].join('.')
            	struct[dni] = strucData?.name.toString()
            }
            if (ok2PollDevice()) { getApiDeviceData() }
        } else { LogAction("atomicState.structData is: ${atomicState?.structData}", "debug", true, true) }
        
    } catch (ex) { LogAction("getNestStructures Exception: ${ex}", "error", true, true) }

    return struct
}

def getNestThermostats() {
    LogTrace("Getting Thermostat list")
    def stats = [:]
    def tstats = atomicState?.deviceData?.thermostats
    //LogAction("Found ${tstats.size()} Thermostats...", "trace", false, false, true)
    //LogAction("settings.structures is: ${settings.structures}", "trace", false, false, true)
    tstats.each { stat ->
        def statId = stat?.key
        def statData = stat?.value
     
        def adni = [statData?.device_id].join('.')
        if (statData?.structure_id == settings?.structures) {
            stats[adni] = getThermostatDisplayName(statData)
        }
    }
    return stats
}

def getNestProtects() {
    LogTrace("Getting Nest Protect List...")
    def protects = [:]
    def nProtects = atomicState?.deviceData?.smoke_co_alarms
    //LogAction("Found ${nProtects.size()} Nest Protects...", "trace", false, false, true)
    nProtects.each { dev ->
       def devId = dev?.key
       def devData = dev?.value
                    
       def bdni = [devData?.device_id].join('.')
        if (devData?.structure_id == settings?.structures) {
            protects[bdni] = getProtectDisplayName(devData)
        }
    }
    return protects
}

def statState(val) {
	def stats = [:]
    def tstats = getNestThermostats()
    tstats.each { stat ->
       	def statId = stat?.key
       	def statData = stat?.value
    	val.each { st ->
            if(statId == st) {
          		def adni = [statId].join('.')
	        	stats[adni] = statData
           	}
        }
    }
	return stats
}

def coState(val) {
	def protects = [:]
    def nProtects = getNestProtects()
    nProtects.each { dev ->
       val.each { pt ->
            if(dev?.key == pt) {             
       			def bdni = [dev?.key].join('.')
            	protects[bdni] = dev?.value
            }
        }
    }
    return protects
}

def getThermostatDisplayName(stat) {
    if(stat?.name) { return stat.name.toString() }
}

def getProtectDisplayName(prot) {
    if(prot?.name) { return prot.name.toString() }
}

def addRemoveDevices() {
    LogAction("addRemoveDevices thermostats ${atomicState?.thermostats}", "debug", false)
    try {
		def tstats
    	def nProtects
    	def devsCrt = 0
    	if (atomicState?.thermostats) {
    		tstats = atomicState?.thermostats.collect { dni ->
        		def d = getChildDevice(dni.key.toString())
        		if(!d) {
            		d = addChildDevice(app.namespace, getThermostatChildName(), dni.key, null, [label: "Nest Thermostat - ${dni.value}"])
            		d.take()
                	devsCrt = devsCrt + 1
           			LogAction("Created: ${d.displayName} with (Id: ${dni.key})", "debug", true)
        		} else {
            		LogAction("Found ${d.displayName} with (Id: ${dni.key}) already exists", "debug", true)
        		}
        		return d
        	}
    	}
    	LogAction("addRemoveDevices protects ${atomicState?.protects}", "debug", false)
		if (atomicState?.protects) {
    		nProtects = atomicState?.protects.collect { dni ->
        		def d2 = getChildDevice(dni.key.toString())
        		if(!d2) {
            		d2 = addChildDevice(app.namespace, getProtectChildName(), dni.key, null, [label: "Nest Protect - ${dni.value}"])
            		d2.take()
                	devsCrt = devsCrt + 1
            		LogAction("Created: ${d2.displayName} with (Id: ${dni.key})", "debug", true)
        		} else {
            		LogAction("Found: ${d2.displayName} with (Id: ${dni.key}) already exists", "debug", true)
        		}
        		return d2
    		}
    	}    
    	if(devsCrt > 0) { LogAction("Created (${tstats?.size()}) Thermostat(s) and ${nProtects?.size()} Protect(s)", "debug", true) }
   		
        if(atomicState?.presDevice) {
        	try {
        		def dni = "NestPresenceDevice"
        		def d3 = getChildDevice(dni)
        		if(!d3) {
            		d3 = addChildDevice(app.namespace, getPresenceChildName(), dni, null, [label: "Nest Presence Device"])
            		d3.take()
                	devsCrt = devsCrt + 1
            		LogAction("Created: ${d3.displayName} with (Id: ${dni})", "debug", true)
        		} else {
            		LogAction("Found: ${d3.displayName} with (Id: ${dni}) already exists", "debug", true)
        		}
        		return d3
            } catch (ex) { LogAction("Nest Presence Device Type is Likely not installed/published", "warn", true) }
        }
        
    	def delete  // Delete any that are no longer in settings
    	if(!atomicState?.thermostats && !atomicState?.protects && !atomicState?.presDevice) {
        	//LogAction("Deleting All Nest Thermostats and Protects", "debug", true)
        	delete = getAllChildDevices() //inherits from SmartApp (data-management)
    	} else { //delete only thermostat
        	//LogAction("Deleteing individual Thermostat(s) and/or Protect(s)", "debug", )
        	if (!atomicState?.protects) {
            	delete = getChildDevices().findAll { !atomicState.thermostats?.toString().contains(it?.deviceNetworkId) }
        	}	 
        	else if (!atomicState?.thermostats) { 
        		delete = getChildDevices().findAll { !atomicState?.protects.toString().contains(it?.deviceNetworkId) }
        	}
            else if (!atomicState?.presDevice) {
            	delete = getChildDevices().findAll { it?.deviceNetworkId == "Nest Presence Device" }
            }
        	else {
            	delete = getChildDevices().findAll { !atomicState?.thermostats?.toString().contains(it?.deviceNetworkId) && !atomicState?.protects.toString().contains(it?.deviceNetworkId) }
        	}
    	}
    	if(delete.size() > 0) { 
        	LogAction("delete: ${delete}, deleting ${delete.size()} devices", "debug", true) 
    		delete.each { deleteChildDevice(it.deviceNetworkId) }
        }
    } catch (ex) { 
    	if(ex instanceof physicalgraph.app.exception.UnknownDeviceTypeException) {
        	LogAction("addRemoveDevices Device Handlers are Missing or Not Published.  Please verify all device handlers are present before continuing: ${ex}", "warn", true, true)
        } else { LogAction("addRemoveDevices Exception: ${ex}", "error", true, true) }
    }
}

def deviceHandlerTest() {
	if (atomicState.testedDhInst) { return true }
    def dni = now().toString()
    def d1
    def d2
    def d3
    
	try {    	
		d1 = addChildDevice(app.namespace, getThermostatChildName(), "testTstat-${dni}", null, ["label":"Nest Thermostat:InstallTest", completedSetup:true])
		d2 = addChildDevice(app.namespace, getPresenceChildName(), "testPres-${dni}", null, ["label":"Nest Presence:InstallTest", completedSetup:true])
        d3 = addChildDevice(app.namespace, getProtectChildName(), "testProtect-${dni}", null, ["label":"Nest Protect:InstallTest", completedSetup:true])
         
    	if (d1) deleteChildDevice("testTstat-${dni}") 
    	if (d2) deleteChildDevice("testPres-${dni}") 
    	if (d3) deleteChildDevice("testProtect-${dni}") 
        log.debug "device Handler Test completed..."
        atomicState?.testedDhInst = true
        return true
    } 
    catch (ex) {
    	if(ex instanceof physicalgraph.app.exception.UnknownDeviceTypeException) {
			LogAction("Device Handlers are missing: ${getThermostatChildName()}, ${getPresenceChildName()}, and ${getProtectChildName()}, Verify the Device Handlers are installed and Published via the IDE", "error", true, true)
       	} else { LogAction("deviceHandlerTest Exception: ${ex}", "error", true, true) }
        atomicState.testedDhInst = false
        return false
	}
}

def preReqCheck() {
	if(atomicState.preReqTested) { return true }
    if(!location?.timeZone || !location?.zipCode) { 
    	atomicState.preReqTested = false
        LogAction("SmartThings Location is not returning (TimeZone: ${location?.timeZone}) or (ZipCode: ${location?.zipCode}) Please edit these settings under the IDE...", "warn", true) 
    	return false
    }
    else { 
    	atomicState.preReqTested = true 
    	return true
    }
}
//This code really does nothing at the moment but return the dynamic url of the app's endpoints
def getEndpointUrl() {
	def params = [
        uri: "https://graph.api.smartthings.com/api/smartapps/endpoints",
        query: ["access_token": atomicState.accessToken],
       	contentType: 'application/json'
    ]
    try {
        httpGet(params) { resp ->
        	LogAction("EndPoint URL: ${resp?.data?.uri}", "trace", false, false, true)
        	return resp?.data?.uri
        	}	
    } catch (ex) { LogAction("getEndpointUrl Exception: ${ex}", "error", true, true) }
}

/************************************************************************************************
|					Below This line handle SmartThings >> Nest Token Authentication				|
*************************************************************************************************/

//These are the Nest OAUTH Methods to aquire the auth code and then Access Token.
def oauthInitUrl() {
    //log.debug "oauthInitUrl with callback: ${callbackUrl}"
    atomicState.oauthInitState = UUID.randomUUID().toString()
    def oauthParams = [
            response_type: "code",
            client_id: clientId(),
            state: atomicState.oauthInitState,
            redirect_uri: callbackUrl //"https://graph.api.smartthings.com/oauth/callback"
    ]
    redirect(location: "https://home.nest.com/login/oauth2?${toQueryString(oauthParams)}")
}

def callback() {
    try {
        LogTrace("callback()>> params: $params, params.code ${params.code}")
        def code = params.code
        LogTrace("Callback Code: ${code}")
        def oauthState = params.state
        LogTrace("Callback State: ${oauthState}")
    
        if (oauthState == atomicState.oauthInitState){
            def tokenParams = [
                code: code.toString(),
                client_id: clientId(),
                client_secret: clientSecret(),
                grant_type: "authorization_code",
            ]
            def tokenUrl = "https://api.home.nest.com/oauth2/access_token?${toQueryString(tokenParams)}"
            httpPost(uri: tokenUrl) { resp ->
                atomicState.tokenExpires = resp.data.expires_in
                atomicState.authToken = resp.data.access_token
            }
                
            if (atomicState.authToken) {
                success()
            } else {
                fail()
            }
        } 
        else { LogAction("callback() failed oauthState != state.oauthInitState", "error", true, true) }
    }
    catch (ex) {
        LogAction("Callback Exception: ${ex}", "error", true, true)
    }
}

def revokeNestToken() {
	def params = [
        uri: "https://api.home.nest.com",
        path: "/oauth2/access_tokens/${atomicState.authToken}",
       	contentType: 'application/json'
    ]
    try {
        httpDelete(params) { resp ->
            if (resp.status == 204) {
        		LogAction("Your Nest Token has been revoked successfully...", "warn", true)
        	}	
        }
    }
	catch (ex) { LogAction("revokeNestToken Exception: ${ex}", "error", true, true) }
}

//HTML Connections Pages
def success() {
    def message = """
    <p>Your SmartThings Account is now connected to Nest!</p>
    <p>Click 'Done' to finish setup.</p>
    """
    connectionStatus(message)
}

def fail() {
    def message = """
        <p>The connection could not be established!</p>
        <p>Click 'Done' to return to the menu.</p>
    """
    connectionStatus(message)
}

def connectionStatus(message, redirectUrl = null) {
    def redirectHtml = ""
    if (redirectUrl) {
        redirectHtml = """
            <meta http-equiv="refresh" content="3; url=${redirectUrl}" />
        """
    }

    def html = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=640">
<title>SmartThings & Nest connection</title>
<style type="text/css">
        @font-face {
                font-family: 'Swiss 721 W01 Thin';
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot');
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot?#iefix') format('embedded-opentype'),
                         url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.woff') format('woff'),
                         url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.ttf') format('truetype'),
                         url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.svg#swis721_th_btthin') format('svg');
                font-weight: normal;
                font-style: normal;
        }
        @font-face {
                font-family: 'Swiss 721 W01 Light';
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot');
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot?#iefix') format('embedded-opentype'),
                         url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.woff') format('woff'),
                         url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.ttf') format('truetype'),
                         url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.svg#swis721_lt_btlight') format('svg');
                font-weight: normal;
                font-style: normal;
        }
        .container {
                width: 90%;
                padding: 4%;
                /*background: #eee;*/
                text-align: center;
        }
        img {
                vertical-align: middle;
        }
        p {
                font-size: 2.2em;
                font-family: 'Swiss 721 W01 Thin';
                text-align: center;
                color: #666666;
                padding: 0 40px;
                margin-bottom: 0;
        }
        span {
                font-family: 'Swiss 721 W01 Light';
        }
</style>
</head>
<body>
        <div class="container">
                <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/st-logo%402x.png" alt="SmartThings logo" />
                <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/connected-device-icn%402x.png" alt="connected device icon" />
                <img src="https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_icon128.png" alt="nest icon" />
                ${message}
        </div>
</body>
</html>
"""
    render contentType: 'text/html', data: html
}

def getChildTstatsIdString() {
    return thermostats.collect { it.split(/\./).last() }.join(',')
}

def getChildProtectsIdString() {
    return protects.collect { it.split(/\./).last() }.join(',')
}

def toJson(Map m) {
    return new org.json.JSONObject(m).toString()
}

def toQueryString(Map m) {
    return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

def clientId() { 
    if (!appSettings.clientId) { return "63e9befa-dc62-4b73-aaf4-dcf3826dd704" }
    else { return appSettings.clientId }
}

def clientSecret() { 
    if (!appSettings.clientSecret) {return "8iqT8X46wa2UZnL0oe3TbyOa0" }
    else { return appSettings.clientSecret }
}

/************************************************************************************************
|									LOGGING AND Diagnostic										|
*************************************************************************************************/

def LogTrace(msg) { if(atomicState?.advAppDebug) { Logger(msg, "trace") } }

def LogAction(msg, type = "debug", showAlways = false, diag = false) {
	try {
    	if(showAlways) { Logger(msg, type) }
    
    	else if (atomicState?.appDebug && !showAlways) { Logger(msg, type) }
    
    	if (atomicState?.diagLogs && diag) { 
        	def timeStmp = getDtNow().toTimestamp()
        	def maxStateSize = 16300
        	def logEntry = [logType: type, logTime: timeStmp, logMsg: msg]
        	def logMsgLngth = logEntry ? logEntry.toString().length() - 100 : 100
        	def curStateSize = state.toString().length()
        	if (curStateSize < (maxStateSize - logMsgLngth)) {
        		//log.debug "State Size Before: ${state.toString().length()}"
    			atomicState?.exLogs << logEntry
            	//log.debug "State Size After: ${state.toString().length()}" 
   			}
        
        	else if (!atomicState?.exLogs) { 
        		atomicState?.exLogs = [] 
        		atomicState?.exLogs << logEntry
       		}
        
        	else { 
				if (curStateSize > (maxStateSize - logMsgLngth)) { 
            		    
            		atomicState?.exLogs.remove(0) // << Removes first item in the list to make room
                	//log.debug "State Size After Cleanup: ${state.toString().length()}"   
           		}
    			atomicState?.exLogs << logEntry
        	}	
    	}
    } catch (ex) { log.error("LogAction Exception: ${ex}") }
}

def renderLogJson() {
	try {
  		def values = []
  		if (!atomicState.exLogs) { values = [nothing: "found"] }
  		else {
    		def logJson = new groovy.json.JsonOutput().toJson(atomicState?.exLogs)
  			def logString = new groovy.json.JsonOutput().prettyPrint(logJson)
  			render contentType: "application/json", data: logString
  		}
    } catch (ex) { LogAction("renderLogJson Exception: ${ex}", "error", true, true) }
}

def renderStateJson() {
	try {
  		def values = []
  		state.each { item ->
    		switch (item.key) {
            	case ["accessToken", "authToken", "exLogs", "structData","deviceData"]:
                	break
                default:
                    values << item
                 	break
    		}
        }
        def logJson = new groovy.json.JsonOutput().toJson(values)
  		def logString = new groovy.json.JsonOutput().prettyPrint(logJson)
  		render contentType: "application/json", data: logString
  		
    } catch (ex) { LogAction("renderStateJson Exception: ${ex}", "error", true, true) }
}

def Logger(msg, type) {
	if(msg && type) { 
    	switch(type) {
    		case "debug":
        		log.debug "${msg}"
        		break
    		case "info":
        		log.info "${msg}"
        		break
        	case "trace":
           		log.trace "${msg}"
        		break
        	case "error":
            	log.error "${msg}"
        		break
        	case "warn":
            	log.warn "${msg}"
            	break
        	default:
            	log.debug "${msg}"
            	break
       	}
    }
    else { log.error "Logger Error - type: ${type} | msg: ${msg}" }
}

//Return size of Diagnostic Logs State
def getExLogSize() { 
	def cnt = 0
    atomicState?.exLogs.each { cnt = cnt + 1 }
    return (cnt > 0) ? cnt : 0 
}

def setStateVar(frc = false) {
	try {
    	//If the developer changes the version in the web appParams JSON it will trigger 
        //the app to create any new state values that might not exist or reset those that do to prevent errors 
        def stateVer = 2
		def stateVar = !atomicState?.stateVarVer ? 0 : atomicState?.stateVarVer.toInteger()
		if(!atomicState?.stateVarUpd || frc || (stateVer < atomicState?.appData.state.stateVarVer.toInteger())) { 
	 		if (!atomicState?.pollValue) { atomicState.pollValue = 60 }
     		if (!atomicState?.pollStrValue) { atomicState.pollStrValue = 180 }
     		if (!atomicState?.pollWaitVal) { atomicState.pollWaitVal = 10 }
     		if (!atomicState?.tempChgWaitVal) { atomicState?.tempChgWaitVal = 4 }
     		if (!atomicState?.appDebug) { atomicState.appDebug = false }
     		if (!atomicState?.childDebug) { atomicState.childDebug = false }
     		if (!atomicState?.exLogs) { atomicState.exLogs = [] }
     		if (!atomicState?.missedPollNotif) { atomicState.missedPollNotif = true }
     		if (!atomicState?.updNotif) { atomicState.updNotif = true }
     		if (!atomicState?.misPollNotifyWaitVal) { atomicState.misPollNotifyWaitVal = 300 }
     		if (!atomicState?.misPollNotifyMsgWaitVal) { atomicState.misPollNotifyMsgWaitVal = 3600 }
     		if (!atomicState?.updNotifyWaitVal) { atomicState.updNotifyWaitVal = 7200 }
            if (!atomicState?.updChildOnNewOnly) { atomicState.updChildOnNewOnly = false }
        	atomicState?.stateVarUpd = true
        	atomicState?.stateVarVer = atomicState?.appData.state.stateVarVer ? atomicState?.appData.state.stateVarVer.toInteger() : 0
            stateCleanup()
        }
    } catch (ex) { LogAction("setStateVar Exception: ${ex}", "error", true, true) }
}

def stateCleanup() {
    //This that I need to clear up on updates go here
}


/******************************************************************************  
*                			Keep These Methods				                  *
*******************************************************************************/
def getThermostatChildName()  { "Nest Thermostat" }
def getProtectChildName()     { "Nest Protect" }
def getPresenceChildName() 	  { "Nest Presence" }
def getServerUrl()            { "https://graph.api.smartthings.com" }
def getShardUrl()             { return getApiServerUrl() }
def getCallbackUrl()          { "https://graph.api.smartthings.com/oauth/callback" }
def getBuildRedirectUrl()     { "${serverUrl}/oauth/initialize?appId=${app.id}&access_token=${atomicState.accessToken}&apiServerUrl=${shardUrl}" }
def getNestApiUrl()           { return "https://developer-api.nest.com" }

def latestTstatVer() { return atomicState.appData?.versions?.thermostat ?: "unknown" }
def latestProtVer() { return atomicState.appData?.versions?.protect ?: "unknown" }
def latestPresVer() { return atomicState.appData?.versions?.presence ?: "unknown" }
def getUse24Time() { return atomicState.use24Time ? true : false }

private debugStatus() { return atomicState?.appDebug ? "On" : "Off" } //Keep this
private childDebugStatus() { return atomicState?.childDebug ? "On" : "Off" } //Keep this
private isAppDebug() { return atomicState?.appDebug ? true : false } //Keep This
private isChildDebug() { return atomicState?.childDebug ? true : false } //Keep This
def getQTimeStrtLbl() { return (qStartInput == "A specific time") ? (qStartTime ? "Start: ${time2Str(qStartTime)}" : null) : ((qStartInput == "sunset" || qStartInput == "sunrise") ? "Start: ${qstartInput.toString().capitalize()}" : null) }
def getQTimeStopLbl() { return (qStopInput == "A specific time") ? (qStopTime ? "Stop: ${time2Str(qStopTime)}" : null) : ((qStopInput == "sunset" || qStopInput == "sunrise") ? "Stop : ${qStopInput.toString().capitalize()}" : null) }
def getQModesLbl() { return quietModes ? "Quiet Mode(s): ${quietModes}" : null }
def getQDayLbl() { return quietDays ? "Days: ${quietDays}" : null }
def getQTimeLabel() { return ((getQTimeStrtLbl() && getQTimeStopLbl()) || getQDayLbl() || getQModesLbl()) ? "${(getQTimeStrtLbl() && getQTimeStopLbl()) ? "${getQTimeStrtLbl()} - ${getQTimeStopLbl()}\n" : ""}${(quietDays ? "${getQDayLbl()}" : "")}${(quietModes ? "\n${getQModesLbl()}" : "")}" : "Tap to Set..." }

def formatDt(dt) {
	def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
   	else {
        LogAction("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...", "warn", true, true)
    }
    return tf.format(dt)
}

//Returns time difference is seconds 
def GetTimeDiffSeconds(lastDate) {
	try {
		def now = new Date()
        def lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
		def start = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(lastDt)).getTime()
    	def stop = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(now)).getTime()
    	def diff = (int) (long) (stop - start) / 1000  
    	return diff
    }
    catch (ex) {
    	LogAction("GetTimeDiffSeconds Exception: ${ex}", "error", true)
        return 10000
    }
}

def dayOk() {
	try {
		if(quietDays) {
    		def day = new SimpleDateFormat("EEEE")
			if(location?.timeZone) { day.setTimeZone(location?.timeZone) }
			return quietDays.contains(day.format(new Date())) ? false : true
    	} else { return true }
    } catch (ex) { LogAction("dayOk Exception: ${ex}", "error", true, true) }
}

def timeOk() {
	try {
    	def strtTime = null
    	def stopTime = null
        def now = new Date()
        def sun = getSunriseAndSunset(zipCode: zipCode)
    	if(qStartTime && qStopTime) { 
            if(qStartInput == "sunset") { strtTime = sun.sunset }
    		else if(qStartInput == "sunrise") { strtTime = sun.sunrise }
    		else if(qStartInput == "A specific time" && qStartTime) { strtTime = qStartTime }
        
        	if(qStopInput == "sunset") { stopTime = sun.sunset }
    		else if(qStopInput == "sunrise") { stopTime = sun.sunrise }
    		else if(qStopInput == "A specific time" && qStopTime) { stopTime = qStopTime }
		} else { return true }  
        if (strtTime && stopTime) {
    		return timeOfDayIsBetween(strtTime, stopTime, new Date(), location?.timeZone) ? false : true
    	} else { return true }
    } catch (ex) { LogAction("timeOk Exception: ${ex}", "error", true, true) }
}

def quietModeOk() {
	if (quietModes) {
    	quietModes?.each { m ->
        	if(m.toString() == location.mode.toString()) { 
            	return false 
        	}
     	}  
        return true
    }
    return true
}

def time2Str(time) {
	if (time) {
		def t = timeToday(time, location?.timeZone)
		def f = new java.text.SimpleDateFormat("h:mm a")
		f.setTimeZone(location.timeZone ?: timeZone(time))
		f.format(t)
    }
}

def getDtNow() {
	def now = new Date()
    return formatDt(now)
}

def notifValEnum() {
	def vals = [
    	300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1800:"30 Minutes", 3600:"1 Hour", 7200:"2 Hours",
        14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours", 1000000:"Custom"
    ]
    return vals
}

def pollValEnum() {
	def vals = [
    	30:"30 Seconds", 45:"45 Seconds", 60:"1 Minute", 120:"2 Minutes", 180:"3 Minutes", 240:"4 Minutes",
        300:"5 Minutes", 600:"10 Minutes", 1800:"30 Minutes", 3600:"60 Minutes", 1000000:"Custom"
    ]
    return vals
}

def waitValEnum() {
	def vals = [
    	1:"1 Second", 2:"2 Seconds", 3:"3 Seconds", 4:"4 Seconds", 5:"5 Seconds", 6:"6 Seconds", 7:"7 Seconds",
        8:"8 Seconds", 9:"9 Seconds", 10:"10 Seconds"
    ]
    return vals
}

def appIcon(url) {
	return !atomicState?.disAppIcons ? url.toString() : ""
}

/******************************************************************************  
*                			Application Pages				                  *
*******************************************************************************/
def pollPrefPage() {
    dynamicPage(name: "pollPrefPage", install: false) {
        section("") {
        	paragraph "\nPolling Preferences\n", image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/timer_icon.png")
        }
        section("Device Polling:") {
        	def pollValDesc = !pollValue ? "Default: 1 Minute" : pollValue
            input ("pollValue", "enum", title: "Device Poll Rate\nDefault is (1 Minute)", required: false, defaultValue: null, metadata: [values:pollValEnum()], description: pollValDesc, submitOnChange: true)
            if(pollValue) {
            	atomicState?.pollValue = !pollValue ? 60 : pollValue.toInteger()
            	if (pollValue.toInteger() == 1000000) { 
            		input ("pollValueCust", "number", title: "Custom Device Poll Value in Seconds", range: "30..86400", required: false, defaultValue: 60, submitOnChange: true) 
            		if(pollValueCust) { atomicState?.pollValue = pollValueCust ? pollValueCust.toInteger() : 60 }
            	} 
            } else { atomicState?.pollValue = !pollValue ? 60 : pollValue.toInteger() }
        }
        section("Location Polling:") {   
        	def pollStrValDesc = !pollStrValue ? "Default: 3 Minutes" : pollStrValue
            input ("pollStrValue", "enum", title: "Location Poll Rate\nDefault is (3 Minutes)", required: false, defaultValue: null, metadata: [values:pollValEnum()], description: pollStrValDesc, submitOnChange: true)
            if(pollStrValue) {
            	atomicState?.pollStrValue = !pollStrValue ? 180 : pollStrValue.toInteger()
            	if (pollStrValue.toInteger() == 1000000) { 
            		input ("pollStrValueCust", "number", title: "Custom Location Poll Value in Seconds", range: "30..86400", required: false, defaultValue: 60, submitOnChange: true) 
            		if(pollValueCust) { atomicState?.pollStrValue = pollStrValueCust ? pollStrValueCust.toInteger() : 180 }
            	} 
            } else { atomicState?.pollStrValue = !pollStrValue ? 180 : pollStrValue.toInteger() }
        }
        section("Wait Values:") {
        	def pollWaitValDesc = !pollWaitVal ? "Default: 10 Seconds" : pollWaitVal
            input ("pollWaitVal", "enum", title: "Forced Refresh Limit\nDefault is (10 sec)", required: false, defaultValue: null, metadata: [values:waitValEnum()], description: pollWaitValDesc,submitOnChange: true)
            atomicState?.pollWaitVal = !pollWaitVal ? 10 : pollWaitVal.toInteger()
			
            def tempChgWaitValDesc = !tempChgWaitVal ? "Default: 4 Seconds" : tempChgWaitVal
            input ("tempChgWaitVal", "enum", title: "Manual Temp Change Delay\nDefault is (4 sec)", required: false, defaultValue: null, metadata: [values:waitValEnum()], description: tempChgWaitValDesc, submitOnChange: true)
       		atomicState?.tempChgWaitVal = !tempChgWaitVal ? 4 : tempChgWaitVal.toInteger()
        }
        section("Other Options:") {
        	input "updChildOnNewOnly", "bool", title: "Only Update Children On New Data?", required: false, defaultValue: false, submitOnChange: true
            atomicState.updChildOnNewOnly = updChildOnNewOnly ? true : false
        }
        section("Advanced Polling Options:") {
        	paragraph "If you are still experiencing Polling issues then you can select these devices to use there events to determine if a scheduled poll was missed\nPlease select as FEW devices as possible!\nMore devices will not make for a better polling."
            input "temperatures", "capability.temperatureMeasurement", title: "Which Temperature Sensors?", multiple: true, required: false, image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/temperature.png")
            input "energies", "capability.energyMeter",	title: "Which Energy Meters?", multiple: true, required: false, image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/lightning.png")
            input "powers",	"capability.powerMeter", title: "Which Power Meters?", multiple: true, required: false, image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/power_meter.png")
        }
    }
}


def notifPrefPage() {
    dynamicPage(name: "notifPrefPage", install: false) {
		
        section("Send Notifications") {
        	def notifDesc = !location.contactBookEnabled ? "Enable push notifications below..." : "Select people or devices to send Notifications too..."
        	paragraph "${notifDesc}"
           	//paragraph "To take advantage of advanced notification options please Configure the contact book under the SmartThings Mobile App Menu",
            //	image: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/alert_icon.png"
			if(!location.contactBookEnabled) {
            	input "usePush", "bool", title: "Send Push Notitifications", required: false, defaultValue: false, submitOnChange: true, 
                		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/notification_icon.png")
            }
            input("recipients", "contact", title: "Send notifications to", required: false, submitOnChange: true, 
            		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/notification_icon.png")) {
           		input ("phone", "phone", title: "Phone Number to send SMS to...", description: "Phone Number", required: false, submitOnChange: true)
        	}
            
            if(recipients || phone || usePush) { 
           		if((settings.recipients != recipients && recipients) || !atomicState.pushTested) {
           			sendMsg("Push Notification Test Successful... Notifications have been Enabled for ${textAppName()}", "info") 
           			atomicState.pushTested = true
               	} else { atomicState.pushTested = true }
           	} else { atomicState.pushTested = false }
        }
        if (recipients || phone || usePush) {
        	section(title: "Time Restrictions") {
            	href "quietTimePage", title: "Quiet Time...", description: "${getQTimeLabel()}",
                	image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/quiet_time.png")
			}
        	section("Missed Poll Notification:") {
        		input "missedPollNotif", "bool", title: "Send for Missed Polls...", required: false, defaultValue: true, submitOnChange: true,
                	image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/late_icon.png")
                if(missedPollNotif) {
                	atomicState.missedPollNotif = missedPollNotif ? true : false
                    def misPollNotifyWaitValDesc = !misPollNotifyWaitVal ? "Default: 15 Minutes" : misPollNotifyWaitVal
                    input ("misPollNotifyWaitVal", "enum", title: "Time Past the missed Poll?", required: false, defaultValue: null, metadata: [values:notifValEnum()], description: misPollNotifyWaitValDesc, submitOnChange: true)
            		if(misPollNotifyWaitVal) {
                    	atomicState.misPollNotifyWaitVal = !misPollNotifyWaitVal ? 900 : misPollNotifyWaitVal.toInteger()
                        if (misPollNotifyWaitVal.toInteger() == 1000000) { 
            				input ("misPollNotifyWaitValCust", "number", title: "Custom Missed Poll Value in Seconds", range: "30..86400", required: false, defaultValue: 900, submitOnChange: true) 
            				if(misPollNotifyWaitValCust) { atomicState?.misPollNotifyWaitVal = misPollNotifyWaitValCust ? misPollNotifyWaitValCust.toInteger() : 900 }
            			} 
                    }else { atomicState.misPollNotifyWaitVal = !misPollNotifyWaitVal ? 900 : misPollNotifyWaitVal.toInteger() }
                    
                    def misPollNotifyMsgWaitValDesc = !misPollNotifyMsgWaitVal ? "Default: 1 Hour" : misPollNotifyMsgWaitVal
                    input ("misPollNotifyMsgWaitVal", "enum", title: "Wait before sending another?", required: false, defaultValue: null, metadata: [values:notifValEnum()], description: misPollNotifyMsgWaitValDesc, submitOnChange: true)
            		if(misPollNotifyMsgWaitVal) {
                    	atomicState.misPollNotifyMsgWaitVal = !misPollNotifyMsgWaitVal ? 3600 : misPollNotifyMsgWaitVal.toInteger()
                    	if (misPollNotifyMsgWaitVal.toInteger() == 1000000) { 
            				input ("misPollNotifyMsgWaitValCust", "number", title: "Custom Msg Wait Value in Seconds", range: "30..86400", required: false, defaultValue: 3600, submitOnChange: true) 
            				if(misPollNotifyMsgWaitValCust) { atomicState.misPollNotifyMsgWaitVal = misPollNotifyMsgWaitValCust ? misPollNotifyMsgWaitValCust.toInteger() : 3600 }
            			} 
                    } else { atomicState.misPollNotifyMsgWaitVal = !misPollNotifyMsgWaitVal ? 3600 : misPollNotifyMsgWaitVal.toInteger() }
                } else { atomicState.missedPollNotif = false }
            }
            section("App and Device Updates:") {
                input "updNotif", "bool", title: "Send for Updates...", required: false, defaultValue: true, submitOnChange: true, 
                		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/update_icon3.png")
               	if(updNotif) {
                	atomicState.updNotif = updNotif ? true : false
                    def updNotifyWaitValDesc = !updNotifyWaitVal ? "Default: 2 Hours" : updNotifyWaitVal
                    input ("updNotifyWaitVal", "enum", title: "Send reminders every?", required: false, defaultValue: null, metadata: [values:notifValEnum()], description: updNotifyWaitValDesc, submitOnChange: true)
            		if(updNotifyWaitVal) {
                    	atomicState.updNotifyWaitVal = !updNotifyWaitVal ? 7200 : updNotifyWaitVal.toInteger()
                    	if (updNotifyWaitVal.toInteger() == 1000000) { 
            				input ("updNotifyWaitValCust", "number", title: "Custom Missed Poll Value in Seconds", range: "30..86400", required: false, defaultValue: 7200, submitOnChange: true) 
            				if(updNotifyWaitValCust) { atomicState.updNotifyWaitVal = updNotifyWaitValCust ? updNotifyWaitValCust.toInteger() : 7200 }
            			} 
                    } else { atomicState.updNotifyWaitVal = !updNotifyWaitVal ? 7200 : updNotifyWaitVal.toInteger() }
            	} else { atomicState.updNotif = false }
            }
            
        }
	}   
}

def devPrefPage() {
	dynamicPage(name: "devPrefPage", title: "Device Preferences", uninstall: false) {
    	section("") {
        	paragraph "\nDevice Preferences\n", image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/device_pref_icon.png")
        }
		if(atomicState?.protects) {
        	section("Protect Devices:") {
        		input "showProtAlarmStateEvts", "bool", title: "Disable Alarm State in Device Activity Feed?", required: false, defaultValue: false, submitOnChange: true, 
                		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/list_icon.png")
        	}
        }
        if(atomicState?.presDevice) {
        	section("Presence Device:") {
        		paragraph "Nothing to see here yet!!!"
            }
        }
    	if(atomicState?.thermostats) {
			section("Thermostat Devices:") {
        		paragraph "This will show 'Auto' while the location is 'Away'.\nFYI: Disabling will prevent Low/High Temp adjustments until the location returns to 'Home' again."
                input "showAwayAsAuto", "bool", title: "When Location is Away show Thermostat mode as Auto?", required: false, defaultValue: true, submitOnChange: true, 
                		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/list_icon.png")
            }        
        }
	}
}

def quietTimePage() {
	dynamicPage(name: "quietTimePage", title: "Quiet during certain times", uninstall: false) {
		section() {
			input "qStartInput", "enum", title: "Starting at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null,submitOnChange: true, required: false
            //atomicState?.qStartInput = qStartInput ? qStartInput : null
			if(qStartInput == "A specific time") { 
            	input "qStartTime", "time", title: "Start time", required: true } 
		}
		section() {
			input "qStopInput", "enum", title: "Stopping at", options: ["A specific time", "Sunrise", "Sunset"], defaultValue: null, submitOnChange: true, required: false
            //atomicState?.qStopInput = qStopInput ? qStopInput : null
			if(qStopInput == "A specific time") { 
            	input "qStopTime", "time", title: "Stop time", required: true } 
		}
        section() {
        	input "quietDays", "enum", title: "Only on certain days of the week", multiple: true, required: false,
			options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
        }
        section() {
        	input "quietModes", "mode", title: "Quiet when these Modes are Active", multiple: true, submitOnChange: true, required: false
			if(quietModes) { atomicState.quietModes = quietModes }
        }
	}
}

def modePresPage() {
	dynamicPage(name: "modePresPage", title: "Mode - Nest Home/Away Automation", uninstall: false) {
		section() {
			input "homeModes", "mode", title: "These modes set Nest to 'Home'", multiple: true, submitOnChange: true, required: false,
            		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/app_pres_home_Icon.png")
			if(homeModes) { atomicState.homeModes = homeModes }
		}
		section() {
			input "awayModes", "mode", title: "These modes set Nest to 'Away'", multiple: true, submitOnChange: true, required: false,
            		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/app_pres_away_Icon.png")
			if(awayModes) { atomicState.awayModes = awayModes }
		}
	}
}

def debugPrefPage() {
    dynamicPage(name: "debugPrefPage", install: false) {
        section ("Application Logs") {
            input (name: "appDebug", type: "bool", title: "Show App Logs in the IDE?", required: false, defaultValue: false, submitOnChange: true,
            		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/log.png"))
            if (appDebug) {
            	input (name: "advAppDebug", type: "bool", title: "Show Verbose Logs?", required: false, defaultValue: false, submitOnChange: true,
                	image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/list_icon.png"))
                atomicState.advAppDebug = advAppDebug ? true : false
            }
            if (appDebug && !atomicState?.appDebug) { LogAction("Debug Logs are Enabled...", "info", false) }
            else if (!appDebug && atomicState?.appDebug) { 
                LogAction("Debug Logs are Disabled...", "info", false)
            }
            atomicState.appDebug = appDebug ? true : false
        }
        
        section ("Child Device Logs") {
            input (name: "childDebug", type: "bool", title: "Show Device Logs in the IDE?", required: false, defaultValue: false, submitOnChange: true,
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/log.png"))
            if (childDebug) { //input (name: "advChildDebug", type: "bool", title: "Show verbose child device Debug Logs in the IDE?", required: false, defaultValue: false, submitOnChange: true)
            }
            if (childDebug && !atomicState?.childDebug) { LogAction("Device Debug Logs are Enabled...", "info", false) }
            else if (!childDebug && atomicState?.childDebug) { LogAction("Device Debug Logs are Disabled...", "info", false) }
            atomicState.childDebug = childDebug ? true : false
        }
    }
}

//Defines the Help Page
def infoPage () {
    dynamicPage(name: "infoPage", title: "App & License Info", install: false) {
        section("About this App:") {
            paragraph appInfoDesc(), image: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/thermostat_blue%401x.png"
        }
        section("Created by:") {
        	paragraph "Anthony S. (@tonesto7)\nBen W. (@desertblade)"
        }
        section("App Revision History:") {
            paragraph appVerInfo()
        }
        section("View the Readme:") {
        	href url:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/README.md", style:"embedded", required:false, title:"Readme File", 
            	description:"View the Projects Readme File..."
        }
        section("Licensing Info:") {
            paragraph "${textCopyright()}\n${textLicense()}"
        }
    }
}

def diagPage () {
    dynamicPage(name: "diagPage", install: false) {
       	section("") {
        	paragraph "This page will allow you to view/export diagnostic logs and state data to assist the developer in troubleshooting...",
            		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/diag_icon.png")
        }
        section("Export or View the Logs") {
           	
       		href url:"${apiServerUrl("/api/smartapps/installations/${app.id}/renderLogs?access_token=${atomicState.accessToken}")}", style:"embedded", required:false, 
               		title:"Diagnostic Logs", description:"Log Entries: (${getExLogSize()} Items)\n\nTap to view diagnostic logs...", image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/log.png")
            href url:"${apiServerUrl("/api/smartapps/installations/${app.id}/renderState?access_token=${atomicState.accessToken}")}", style:"embedded", required:false, 
               		title:"State Data", description:"Tap to view State Data...", image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/state_data_icon.png")
            href "resetDiagQueuePage", title: "Reset Diagnostic Logs", description: "Tap to Reset the Logs...",
            		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/reset_icon.png")
       	}
        section("Last Nest Command") {
        	def cmdTxt = atomicState.lastCmdSent ? atomicState?.lastCmdSent : "Nothing found..."
            def cmdDt = atomicState.lastCmdSentDt ? atomicState?.lastCmdSentDt : "Nothing found..."
        	paragraph "Command: ${cmdTxt}\nDateTime: ${cmdDt}"
        }
    }
}

def resetDiagQueuePage() {
	return dynamicPage(name: "resetDiagQueuePage", nextPage: diagPage, install: false) {
    	section ("Diagnostic Log Queue Reset..") {
            atomicState.exLogs = []
            paragraph "Diagnostic Logs have been reset...\nPress Done to return to previous page..." 
        }
    }
}

def nestLoginPrefPage () {
    dynamicPage(name: "nestLoginPrefPage", install: false) {
        section("Nest Login Preferences:") {
       	    href "nestTokenResetPage", title: "Log Out and Reset your Nest Token", description: "Tap to Reset the Token...",
            		image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/reset_icon.png")
       	}
    }
}

def nestTokenResetPage() {
	return dynamicPage(name: "nestTokenResetPage", install: false) {
    	section ("Resetting Nest Token..") {
	        revokeNestToken()
    	    atomicState.authToken = null
        	paragraph "Token has been reset...\nPress Done to return to Login page..." 
        }
    }
}

def nestInfoPage () {
    dynamicPage(name: "nestInfoPage", install: false) {
    	section("About this page:") {
        	paragraph "This Page will display the data returned from the API for each device that is selected..."
        }
        if(atomicState.structures) {	
        	section("Locations") {
        		href "structInfoPage", title: "Nest Location(s) Info...", description: "Tap to view Location info...", 
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_structure_icon.png")
        	}
        }
        if (atomicState.thermostats) {
        	section("Thermostats") {
            	href "tstatInfoPage", title: "Nest Thermostat(s) Info...", description: "Tap to view Thermostat info...", 
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_like.png")
        	}
        }
        if (atomicState.protects) {
        	section("Protects") {
        		href "protInfoPage", title: "Nest Protect(s) Info...", description: "Tap to view Protect info...", 
            			image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/protect_icon.png")
        	}
        }
    }
}

def structInfoPage () {
    dynamicPage(name: "structInfoPage", refreshInterval: 15, install: false) {
        section("") {
            paragraph "\nLocation Info:", image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_structure_icon.png")
        }
       	for(str in atomicState?.structData) {
            if (str.key == atomicState.structures) {
           		def strId = str.key
                def strData = str.value
        		section("Location Name: ${strData.name}") {
                	strData.each { item ->
    					switch (item.key) {
        					case [ "wheres" ]:
           						break
       						default: 
                				paragraph "${item.key.toString().capitalize()}: ${item.value}"
           						break
    					}
	    			}
                }
    		}        
        }
    }
}

def tstatInfoPage () {
    dynamicPage(name: "tstatInfoPage", refreshInterval: 15, install: false) {
        section("") {
            paragraph "\nThermostats:", image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_like.png")
        }
        for(tstat in atomicState.thermostats) { 
        	def devs = []
        	section("Thermostat Name: ${tstat.value}") {
            	atomicState.deviceData.thermostats[tstat.key].each { dev ->
                	switch (dev.key) {
       					case [ "where_id" ]:  //<< Excludes certain keys from being shown
       						break
    					default: 
               				devs << "${dev.key.toString().capitalize()}: ${dev.value}"
	   						break
    				}
                }
                devs.sort().each { item ->
                	paragraph "${item}"
                }
        	}
        }
    }
}

def protInfoPage () {
    dynamicPage(name: "protInfoPage", refreshInterval: 15, install: false) {
        section("") {
            paragraph "\nProtects:", image: appIcon("https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/protect_icon.png")
        }
        atomicState.protects.each { prot ->
        	def devs = []
        	section("Protect Name: ${prot.value}") {
            	atomicState.deviceData.smoke_co_alarms[prot.key].each { dev ->
                	log.debug "prot dev: $dev"
                	switch (dev.key) {
       					case [ "where_id" ]:  //<< Excludes certain keys from being shown
       						break
    					default: 
               				devs << "${dev.key.toString().capitalize()}: ${dev.value}"
	   						break
    				}
                }
                devs.sort().each { item ->
                	paragraph "${item}"
                }
        	}
        }
    }
}


/******************************************************************************  
*                Application Help and License Info Variables                  *
*******************************************************************************/
//Change This to rename the Default App Name
private def appName() 		{ "Nest Manager" }
private def appAuthor() 	{ "Anthony S." }
private def appNamespace() 	{ "tonesto7" }
private def appInfoDesc() 	{ 
	def cur = atomicState?.appData?.versions?.app?.ver.toString()
	def ver = (textVersion() != cur) ? "${textVersion()} (Lastest: v${cur})" : textVersion()
	return "Name: ${textAppName()}\n${ver}\n${textModified()}" 
}
private def textAppName()   { return "${appName()}" }    
private def textVersion()   { return "Version: ${appVersion()}" }
private def textModified()  { return "Updated: ${appVerDate()}" }
private def textAuthor()    { return "${appAuthor()}" }
private def textNamespace() { return "${appNamespace()}" }
private def textVerInfo()   { return "${appVerInfo()}" }
private def textCopyright() { return "Copyright© 2016 - Anthony S." }
private def textDesc()      { return "This this app adds, updates your Nest devices..." }
private def textHelp()      { return "" }
private def textLicense() { 
    return 	"This software if free for Private Use. You may use and modify the software without distributing it" +
 			"This software and derivatives may not be used for commercial purposes." +
 			"You may not modify, distribute or sublicense this software." +
			"You may not grant a sublicense to modify and distribute this software to third parties not included in the license.\n" +
			"Software is provided without warranty and the software author/license owner cannot be held liable for damages.\n"
}