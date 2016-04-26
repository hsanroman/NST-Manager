/********************************************************************************************
|    Application Name: Nest Automations                                                   |
|    Author: Anthony S. (@tonesto7), 														|
|	 Contributors: Ben W. (@desertblade) | Eric S. (@E_sch)                  				|
|                                                                                           |
|********************************************************************************************
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
    parent: "${appParent()}",
    author: "${textAuthor()}",
    description: "${textDesc()}",
    category: "My Apps",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/automation_icon.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/automation_icon.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/automation_icon.png",
    singleInstance: true)

def appVersion() { "1.0.3" }
def appVerDate() { "4-26-2016" }
def appVerInfo() {
    
    "V1.1.0 (Apr 26th, 2016)\n" +
    "Updated: standardized input and variable naming.\n" +
    "Updated: Select Custom Notification Recipients for Contacts, External Temps, and Mode Automations \n" + 
    "Fixed: Reworked the mode and contact event Logic...\n\n" +
    
    "V1.0.1 (Apr 22nd, 2016)\n" +
    "Updated: Cleaned up code \n" + 
    "Added: Links to in-app help pages. \n" +
    "Added: Ability to use presence to set Nest home/away...\n\n" +
    
    "V1.0.0 (Apr 21st, 2016)\n" +
    "Added... Everything is new...\n\n" +
    "------------------------------------------------"
}

preferences {
    page(name: "mainPage", title: "Nest Automations", content:"mainPage", uninstall: true, install: false, nextPage: "namePage")
    page(name: "remSensorPage")
    page(name: "remSenShowTempsPage")
    page(name: "contactWatchPage")
    page(name: "nestModePresPage")
    page(name: "extTempPage")
    page(name: "setRecipientsPage")
    page(name: "setDayModeTimePage")
    page(name: "namePage", install: true, uninstall: true)
}

def mainPage() {
    //log.trace "mainPage()"
    state?.tempUnit = getTemperatureScale().toString()
    return dynamicPage(name: "mainPage", title: "Automation Page...", uninstall: false) {
        section("Use Remote Temperature Sensor(s) to Control your Thermostat:") {
            def remSenDayDesc = (remSensorDay) ? ("Day Sensor${(remSensorDay?.size() > 1) ? " (average):" : ":"} ${getDeviceTempAvg(remSensorDay)}°${state?.tempUnit}") : ""
            def remSenNightDesc = (remSensorNight) ? ("\nNight Sensor${(remSensorNight?.size() > 1) ? " (average):" : ":"} ${getDeviceTempAvg(remSensorNight)}°${state?.tempUnit}") : ""
            def remSenEnableDesc = state?.remSenEnabled ? "" : "External Sensor Disabled...\n"
            def remSenMotInUse = extMotionSensors ? ("\nMotion Events: ${!remSenMotionModes ? "Active" : (isInMode(remSenMotionModes) ? "Active(Mode Ok)" : "Not Active(!Mode)")}") : ""
            def remSenModes = remSenModes ? "\nMode Filters Active" : ""
            def remSenSetTemps = (remSenHeatTemp && remSenCoolTemp) ? "\nSet Temps: (Heat/Cool: ${extSenHeatTemp}°${state?.tempUnit}/${extSenCoolTemp}°${state?.tempUnit})" : ""
            def remSenRuleType = remSenRuleType ? "\nRule-Type: ${remSenRuleName()}" : ""
            def remSenTstatStatus = remSenTstat ? "\nThermostat Mode: ${remSenTstat?.currentThermostatOperatingState.toString()}/${remSenTstat?.currentThermostatMode.toString()}" : ""
            def remSenTypeUsed = getUseNightSensor() ? remSenNightDesc : remSenDayDesc
            def remSenDesc = isRemSenConfigured() ? 
                "${remSenEnableDesc}Thermostat Temp: ${getDeviceTemp(remSenTstat)}°${state?.tempUnit}${remSenTstatStatus}\n${remSenTypeUsed}${remSenSetTemps}${remSenRuleType}${remSenMotInUse}${remSenModes}\nTap to Modify..." : "Tap to Configure..."
            href "remSensorPage", title: "Use Remote Sensors...", description: remSenDesc, state: remSenDesc, image: getAppImg("remote_sensor_icon.png")
        }
        section("Turn Off Thermostat when a Door Window is Opened:") {
            def qOpt = (settings?.conWatModes || settings?.conWatDays || (settings?.conWatStartTime && settings?.conWatStopTime)) ? "Schedule Options Selected...\n" : ""
            def desc = (conWatContacts && conWatTstat) ? "${conWatTstat.label}\nwith (${conWatContacts ? conWatContacts.size() : 0}) Contact(s)\n${qOpt}\nTap to Modify..." : "Tap to Configure..."
            href "contactWatchPage", title: "Configure Sensors to Watch...", description: desc, image: getAppImg("open_window.png")
        }
        section("Turn On/Off Thermostat based on Outside temps:") {
            def qOpt = (extTmpModes || extTmpDays || (extTmpStartTime && extTmpStopTime)) ? "Schedule Options Selected...\n" : ""
            def desc = (!extTmpUseWeather && extTmpTemp && extTmpTstat) ? ("${extTmpTstat?.label}\nwith External Temp Sensor\n${qOpt}\nTap to Modify...") : (extTmpUseWeather ? "${extTmpTstat?.label}\nwith External Weather\n${qOpt}\nTap to Modify..." : "Tap to Configure...")
            href "extTempPage", title: "Turn off based on External Temps...", description: desc, image: getAppImg("external_temp_icon.png")
        }
        section("Set Nest Presence Based on ST Modes:") {
            def nModeLocDesc = isNestModesConfigured() ? "Nest Location: ${getNestLocPres().toString().capitalize()}" : ""
            def nModeDesc = (!nModePresSensor && (nModeAwayModes || nModeHomeModes)) ? "${nModeHomeModes ? "Home Modes: $nModeHomeModes" : ""}${nModeAwayModes ? "\nAway Modes: $nModeAwayModes" : ""}\n\nTap to Modify..." : "\nTap to Configure..."
            def nPresDesc = nModePresSensor ? "Presence Sensor Active...\nPresence is: ${nModePresSensor?.currentPresence}" : ""
            def nPresDelayDesc = nModePresSensorDelay ? "Delay: ${getLongTimeEnumLabel(nModePresSensorDelayVal)}" : "" 
            href "nestModePresPage", title: "Mode Automations", description: "${nModeLocDesc}\n${(nModePresSensor ? "${nPresDesc}\n${nPresDelayDesc}" : nModeDesc)}", image: getAppImg("mode_automation_icon.png")
        }
        
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
     }
}

def namePage() {
    dynamicPage(name: "namePage") {
        section("Automation name") {
            label title: "Name this Automation", defaultValue: app.label, required: true
            paragraph "Make sure to name it something that will help easily recognize it later."
        } 
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has been installed...")
    //parent?.autoAppInst(true)
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
    //getAutomationsActive()
    //parent?.autoAppInst(true)
    sendNotificationEvent("${textAppName()} has updated settings...")
}

def uninstalled() {
    //sends notification of uninstall
    sendNotificationEvent("${textAppName()} is uninstalled...")
    //parent?.autoAppInst(false)
}

def initialize() {
    unschedule()
    unsubscribe()
    scheduler()
    subscribeToEvents()
    automationsInst()
    updateWeather()
}

def automationsInst() {
    state.isRemSenConfigured = isRemSenConfigured() ? true : false
    state.isWContConfigured = isWContConfigured() ? true : false
    state.isExtTmpConfigured = isExtTmpConfigured() ? true : false
    state.isNestModesConfigured = isNestModesConfigured() ? true : false
}

def getAutomationsActive() { 
    def remActive = ((remSensorDay || remSensorNight)  && extSenTstat && extSenHeatTemp && extSenCoolTemp)
    def conActive = (wContContacts && wContTstat)
    def nestModesActive = (nModeAwayModes && nModeHomeModes)
    def autoDesc = "${remActive ? "Remote Sensors Active..." : ""}${conActive ? "\nContact Watcher Active..." : ""}${nestModesActive ? "Mode Automation Active..." : ""}"
    parent?.automationsActive(((remActive || conActive || nestModesActive) ? true : false), autoDesc)
}

def subscribeToEvents() {
    //Remote Sensor Subscriptions 
    if(remSensorDay || remSensorNight || remSenTstat) {
        subscribe(location, remSenLocationEvt)
        if(remSensorDay) { subscribe(remSensorDay, "temperature", remSenTempEvt) }
        if(remSensorNight) { subscribe(remSensorNight, "temperature", remSenTempEvt) }
        if(remSenTstat) {
            subscribe(remSenTstat, "temperature", remSenTempEvt)
            subscribe(remSenTstat, "thermostatMode", remSenTempEvt) 
        }
        if(remSenMotion) { subscribe(remSenMotion, "motionSensor", remSenMotionEvt) }
        if(remSenSwitches) { subscribe(remSenSwitches, "switch", remSenSwitchEvt) }
        if(remSenUseSunAsMode) {
            subscribe(location, "sunset", remSenSunEvtHandler)
            subscribe(location, "sunrise", remSenSunEvtHandler)
            subscribe(location, "sunriseTime", remSenSunEvtHandler)
            subscribe(location, "sunsetTime", remSenSunEvtHandler)
        }
        remSenEvtEval()
    }

    //External Temp Subscriptions
    if(!extTmpUseWeather && extTmpTemp) { subscribe(extTmpTemp, "temperature", extTmpTempEvt, [filterEvents: false]) }
    
    //Nest Mode Subscriptions
    if (nModeHomeModes || nModeAwayModes) { subscribe(location, "mode", nModeEvt, [filterEvents: false]) }
    if (nModePresSensor) { subscribe(nModePresSensor, "presence", nModePresEvt) }
    if (conWatContacts && conWatTstat) { subscribe(conWatContacts, "contact", conWatContactEvt) }
}

def scheduler() {
    if(!extTmpUseWeather && extTmpTstat) { schedule("0 0/1 * * * ?", "updateData") } 
    if(extTmpUseWeather && extTmpTstat) { schedule("0 0/${getExtTmpWeatherRefreshVal()} * * * ?", "updateWeather") }
}

def updateData() {
    //exTempEvt(null) 
}

def updateWeather() {
    if(extTmpUseWeather) { 
        getExtConditions()
        extTmpEvt(null) 
    }
}

/******************************************************************************  
|                			EXTERNAL SENSOR AUTOMATION CODE	                  |
*******************************************************************************/
def remSensorPage() {
    def pName = "remSen"
    dynamicPage(name: "remSensorPage", title: "Remote Sensor Automation", uninstall: false) {
        if(state?.remSenEnabled == null) { state?.remSenEnabled = true }
        def req = (remSensorDay || remSensorNight || remSenTstat) ? true : false
        def dupTstat = remSenTstatDuplication()
        def tStatHeatSp = getTstatSetpoint(extSenTstat, "heat")
        def tStatCoolSp = getTstatSetpoint(extSenTstat, "cool")
        def tStatMode = remSenTstat ? remSenTstat?.currentThermostatMode : "unknown"
        def tStatTemp = "${getDeviceTemp(remSenTstat)}°${state?.tempUnit}"
        def locMode = location?.mode
        
        def coolTempsReq = (remSenRuleType in [ "Cool", "Heat_Cool", "Cool_Circ", "Heat_Cool_Circ" ]) ? true : false
        def heatTempsReq = (remSenRuleType in [ "Heat", "Heat_Cool", "Heat_Circ", "Heat_Cool_Circ" ]) ? true : false
        
        section("Select the Allowed (Rule) Action Type:") {
            if(!remSenRuleType) { 
                paragraph "(Rule) Actions will be used to determine what actions are taken when the temperature threshold is reached. Using combinations of Heat/Cool/Fan to help balance" + 
                          " out the temperatures in your home in an attempt to make it more comfortable...", image: getAppImg("instruct_icon.png")
            }
            input(name: "remSenRuleType", type: "enum", title: "(Rule) Action Type", options: remSenRuleEnum(), required: true, submitOnChange: true, image: getAppImg("rule_icon.png"))
        }
        if(remSenRuleType) {
            section("Choose a Thermostat... ") {
                input "remSenTstat", "capability.thermostat", title: "Which Thermostat?", submitOnChange: true, required: req, image: getAppImg("thermostat_icon.png")
                if(dupTstat) {
                    paragraph "Duplicate Primary Thermostat found in Mirror Thermostat List!!!.  Please Correct...", image: getAppImg("error_icon.png")
                }
                if(extSenTstat) { 
                    setTstatCapabilities()
                    paragraph "Current Temperature: ${tStatTemp}\nCool/Heat Setpoints: ${tStatCoolSp}°${state?.tempUnit}/${tStatHeatSp}°${state?.tempUnit}\nCurrent Mode: $tStatMode", image: getAppImg("instruct_icon.png")
                    input "remSenTstatsMir", "capability.thermostat", title: "Mirror Actions to these Thermostats", multiple: true, submitOnChange: true, required: false, image: getAppImg("thermostat_icon.png")
                    if(remSenTstatsMir && !dupTstat) { 
                        remSenTstatsMir?.each { t ->
                            paragraph "Thermostat Temp: ${getDeviceTemp(t)}${state?.tempUnit}", image: " "
                        }
                    }
                }
            }
            def dSenStr = !remSensorNight ? "Remote" : "Daytime"
            section("Choose $dSenStr Sensor(s) to use instead of the Thermostat's...") {
                def dSenReq = (((remSensorNight && !remSensorDay) || !remSensorNight) && remSenTstat) ? true : false
                input "remSensorDay", "capability.temperatureMeasurement", title: "$dSenStr Temp Sensors", submitOnChange: true, required: dSenReq,
                        multiple: true, image: getAppImg("temperature_icon.png")
                if(remSensorDay) {
                    def tempStr = !remSensorNight ? "" : "Day "
                    input "remSenHeatTempDay", "decimal", title: "Desired ${tempStr}Heat Temp (°${state?.tempUnit})", submitOnChange: true, required: heatTempsReq, image: getAppImg("heat_icon.png")
                    input "remSenCoolTempDay", "decimal", title: "Desired ${tempStr}Cool Temp (°${state?.tempUnit})", submitOnChange: true, required: coolTempsReq, image: getAppImg("cool_icon.png")
                    //paragraph " ", image: " "
                    def tmpVal = "$dSenStr Sensor Temp${(remSensorDay?.size() > 1) ? " (avg):" : ":"} ${getDeviceTempAvg(remSensorDay)}°${state?.tempUnit}"
                    if(remSensorDay.size() > 1) {
                        href "remSenShowTempsPage", title: "View $dSenStr Sensor Temps...", description: "${tmpVal}", image: getAppImg("blank_icon.png")
                        //paragraph "Multiple temp sensors will return the average of those sensors.", image: getAppImg("i_icon.png")
                    } else { paragraph "${tmpVal}", image: getAppImg("instruct_icon.png") }
                }
            }
            if(remSensorDay && (!setTempsReq || (setTempsReq && remSenDayHeatTemp && remSenDayCoolTemp))) {
                section("(Optional) Choose a second set of Temperature Sensor(s) to use in the Evening instead of the Thermostat's...") {
                    input "remSensorNight", "capability.temperatureMeasurement", title: "Evening Temp Sensors", submitOnChange: true, required: false, multiple: true, image: getAppImg("temperature_icon.png")
                    if(remSensorNight) {
                        input "remSenNightHeatTemp", "decimal", title: "Desired Evening Heat Temp (°${state?.tempUnit})", submitOnChange: true, required: ((remSensorNight && heatTempsReq) ? true : false), image: getAppImg("heat_icon.png")
                        input "remSenNightCoolTemp", "decimal", title: "Desired Evening Cool Temp (°${state?.tempUnit})", submitOnChange: true, required: ((remSensorNight && coolTempsReq) ? true : false), image: getAppImg("cool_icon.png")
                        //paragraph " ", image: " "
                        def tmpVal = "Evening Sensor Temp${(remSensorNight?.size() > 1) ? " (avg):" : ":"} ${getDeviceTempAvg(remSensorNight)}°${state?.tempUnit}"
                        if(remSensorNight.size() > 1) {
                            href "remSenShowTempsPage", title: "View Evening Sensor Temps...", description: "${tmpVal}", image: getAppImg("blank_icon.png")
                            //paragraph "Multiple temp sensors will return the average temp of those sensors.", image: getAppImg("i_icon.png")
                        } else { paragraph "${tmpVal}", image: getAppImg("instruct_icon.png") }
                    }
                }
            }
            if(remSensorDay && remSensorNight) {
                section("Day/Evening Detection Options:") {
                    input "remSenUseSunAsMode", "bool", title: "Use Sunrise/Sunset instead of Modes?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("sunrise_icon.png")
                    if(!remSenUseSunAsMode && !remSenModeDuplication()) {
                        def modesReq = (!remSenUseSunAsMode && (remSensorDay && remSensorNight)) ? true : false
                        input "remSensorDayModes", "mode", title: "Daytime Modes...", multiple: true, submitOnChange: true, required: modesReq, image: getAppImg("mode_icon.png")
                        input "remSensorNightModes", "mode", title: "Evening Modes...", multiple: true, submitOnChange: true, required: modesReq, image: getAppImg("mode_icon.png")
                    } else {
                        paragraph "Duplicate Mode(s) found under the Day or Evening Sensor!!!.  Please Correct...", image: getAppImg("error_icon.png")
                    }
                }
            }
            if(remSenTstat && (remSensorDay || remSensorNight)) {
                if(remSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) {
                    section("Fan Settings:") {
                        paragraph "The default fan runtime is 15 minutes.\nThis can be adjusted under your nest account.", image: getAppImg("instruct_icon.png")
                        input "remSenTimeBetweenRuns", "enum", title: "Delay Between Fan Runs?", required: true, defaultValue: 3600, metadata: [values:longTimeEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                    }
                }
                section("(Optional) Use Motion Sensors to Evaluate Temps:") {
                    input "remSenMotion", "capability.motionSensor", title: "Motion Sensors", required: false, multiple: true, submitOnChange: true, image: getAppImg("motion_icon.png")
                    if(remSenMotion) {
                        paragraph "Motion State: (${isMotionActive(remSenMotion) ? "Active" : "Not Active"})", image: " "
                        input "remSenMotionModes", "mode", title: "Only evaluate Motion in these Modes...", multiple: true, submitOnChange: true, required: false, image: getAppImg("mode_icon.png")
                        input "remSenMotionDelayVal", "enum", title: "Delay before evaluating?", required: true, defaultValue: 300, metadata: [values:longTimeEnum()], submitOnChange: true, image: getAppImg("delay_time_icon.png")
                    }
                }
                section("(Optional) Use Switch Event(s) to Evaluate Temps:") {
                    input "remSenSwitch", "capability.switch", title: "Select Switches", required: false, multiple: true, submitOnChange: true, image: getAppImg("wall_switch_icon.png")
                    if(remSenSwitch) { 
                        def swVals = [0:"Off", 1:"On", 2:"On/Off"]
                        input "remSenSwitchOpt", "enum", title: "Event Type to Trigger?", required: true, defaultValue: 2, metadata: [values:swVals], submitOnChange: true, image: getAppImg("settings_icon.png")
                    }
                    
                }
                section ("Optional Settings:") {
                    paragraph "The Action Threshold Temp is the temperature difference used to trigger a selected action.", image: getAppImg("instruct_icon.png")
                    input "remSenTempDiffDegrees", "decimal", title: "Action Threshold Temp (°${state?.tempUnit})", required: true, defaultValue: 1.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                    if(remSenRuleType != "Circ") {
                        paragraph "The Change Temp Increments are the amount the temp is adjusted +/- when an action requires a temp change.", image: getAppImg("instruct_icon.png")
                        input "remSenTempChgVal", "decimal", title: "Change Temp Increments (°${state?.tempUnit})", required: true, defaultValue: 2.0, submitOnChange: true, image: getAppImg("temp_icon.png")
                    }
                    input "remSenModes", "mode", title: "Only Evaluate Actions in these Modes?", multiple: true, required: false, submitOnChange: true, image: getAppImg("mode_icon.png")
                    input "remSenWaitVal", "number", title: "Wait Time between Evaluations (seconds)?", required: false, defaultValue: 60, submitOnChange: true, image: getAppImg("delay_time_icon.png")
                }
            }
        }
        if (isRemSenConfigured()) {
            section("Enable or Disable Remote Sensor Once Configured...") {
                input (name: "remSenEnabled", type: "bool", title: "Enable Remote Sensor Automation?", required: false, defaultValue: true, submitOnChange: true, image: getAppImg("switch_icon.png"))
                state?.remSenEnabled = remSenEnabled ? true : false
            }
        } else { state?.remSenEnabled = false }
        
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
    }
}

def isRemSenConfigured() {
    def devOk = ((remSensorDay || remSensorNight) && remSenTstat) ? true : false
    def nightOk = (!remSensorNight && remSensorDay) || (remSensorNight && (extSenRuleType == "Circ" && ((!extSenHeatTempNight || !extSenCoolTempNight) || (extSenHeatTempNight && extSenCoolTempNight)))) ? true : false
    def dayOk = (remSensorDay && (remSensorDay && !remSensorNight) || (remSensorDay && (extSenRuleType == "Circ" && ((!extSenHeatTempDay || !extSenCoolTempDay) || (extSenHeatTempDay && extSenCoolTempDay))))) ? true : false
    //log.debug "devOk: $devOk | nightOk: $nightOk | dayOk: $dayOk"
    return (devOk && nightOk && dayOk) ? true : false
}

def extSenMotionEvt(evt) {
    log.debug "extSenMotionEvt event: $evt.value"
    if(state?.extSenEnabled == false) { return }
    else if (extSenUseSunAsMode) { return}
    else {
        if(extMotionSensorModes) {
            if(isInMode(extMotionSensorModes)) {
                runIn(remSenMotionDelayVal.toInteger(), "remSenCheckMotion", [overwrite: true])
            }
        } else {
            runIn(remSenMotionDelayVal.toInteger(), "remSenCheckMotion", [overwrite: true])
        }
    }
}

def remSenSenTempEvt(evt) {
    if(state?.remSenEnabled == false) { return }
    else { remSenEvtEval() }
}

def remSenSunEvtHandler(evt) {
    if(remSenUseSunAsMode) { remSenEvtEval() }
    else { return }
}

def remSenSwitchEvt(evt) {
    def evtType = evt?.value.toString()
    if(remSenSwitch) {
        def opt = remSenSwitchOpt?.toInteger()
        switch(opt) {
            case 0:
                if(evtType == "off") { remSenEvtEval() }
                break
            case 1:
                if (evtType == "on") { remSenEvtEval() }
                break
            case 2:
                if(evtType in ["on", "off"]) { remSenEvtEval() }
            default:
                LogAction("remSenSwitchEvt: Invalid Option Received...", "warn", true)
            break
        }
    }
}

def remSenLocationEvt(evt) {
    log.debug "remSenLocationEvt mode: $evt.value, heat: $heat, cool: $cool"
    remSenEvtEval()
}

def coolingSetpointHandler(evt) { log.debug "coolingSetpointHandler()" }

def heatingSetpointHandler(evt) { log.debug "heatingSetpointHandler()" }

def isMotionActive(sensors) {
    return sensors?.currentState("motion")?.value.contains("active") ? true : false
}

def remSenCheckMotion() {
    if(isMotionActive(remSenMotion)) { remSenEvtEval() }
}

def remSenTstatDuplication() {
    def result = false
    if(remSenTstat && remSenTstatsMir) {
        def pTstat = remSenTstat?.deviceNetworkId.toString()
        def mTstatAr = []
        remSenTstatsMir?.each { ts ->
            mTstatAr << ts?.deviceNetworkId.toString()
        }
        if (pTstat in mTstatAr) { return true }
    }
    return result
}

def extSenModeDuplication() {
    def result = false
    if(remSensorDayModes && remSensorNightModes) {
         remSensorDayModes?.each { dm ->
            if(dm in remSensorNightModes) {
                result = true
            }
        }
    }
    return result
}

def getUseNightSensor() {
    def day = !remSensorDayModes ? false : isInMode(remSensorDayModes)
    def night = !remSensorNightModes ? false : isInMode(remSensorNightModes)
    if(remSenUseSunAsMode) { return getTimeAfterSunset() ? true : false }
    else if(night && !day) { return true }
    else if (day && !night) { return false }
    else { return null }
}

def getDeviceTempAvg(items) {
    def tmpAvg = []
    def tempVal = 0
    if(!items) { return tempVal }
    else if(items?.size() > 1) {
        tmpAvg = items*.currentTemperature
        if(tmpAvg && tmpAvg?.size() > 1) { tempVal = (tmpAvg?.sum().toDouble() / tmpAvg?.size().toDouble()).round(1) }
    } 
    else { tempVal = getDeviceTemp(items) }
    return tempVal.toDouble()
}

def remSenShowTempsPage() {
    dynamicPage(name: "remSenShowTempsPage", uninstall: false) {
        if(remSensorDay) { 
            def dSenStr = !remSensorNight ? "Remote" : "Daytime"
            section("$dSenStr Sensor Temps:") {
                remSensorDay?.each { t ->
                    paragraph "${t?.label}: ${getDeviceTemp(t)}°${state?.tempUnit}", image: getAppImg("temperature_icon.png")
                }
            }
            section("Average Temp of $dSenStr Sensors:") {
                paragraph "Sensor Temp (average): ${getDeviceTempAvg(remSensorDay)}°${state?.tempUnit}", image: getAppImg("instruct_icon.png")
            }
        }
        if(remSensorNight) { 
            section("Night Sensor Temps:") {
                remSensorNight?.each { t ->
                    paragraph "${t?.label}: ${getDeviceTemp(t)}°${state?.tempUnit}", image: getAppImg("temperature_icon.png")
                }
            }
            section("Average Temp of Night Sensors:") {
                paragraph "Sensor Temp (average): ${getDeviceTempAvg(remSensorNight)}°${state?.tempUnit}", image: getAppImg("instruct_icon.png")
            }
        }
    }
}

def getTimeAfterSunset() {
    def sR = (location?.currentValue("sunriseTime"))
    def sS = (location?.currentValue("sunsetTime"))
    def result = true
    if (sS && sR) {
        def timeNow = now()
        def start = timeToday(sS, location?.timeZone).time
        def stop = timeToday(sR, location?.timeZone).time
        result = (start < stop) ? ((timeNow >= start) && (timeNow <= stop)) : ((timeNow <= stop) || (timeNow >= start))
    }
    return result
}

def getLastRemSenEvalSec() { return !atomicState?.lastRemSenEval ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenEval).toInteger() }

// Based off of Keep Me Cozy II
private remSenEvtEval() {
    log.trace "remSenEvtEval....."
    //log.debug "Remote Sensor Enabled: ${state?.remSenEnabled} | remSenModesOk: ${modesOk(remSenModes)} | rem Sensor: ${(remSensorDay || remSensorNight)} | Thermostat: ${remSenTstat} | getRemModeOk(): ${getRemSenModeOk()}"
    if(getLastRemSenEvalSec() < (remSenWaitVal ? remSenWaitVal?.toInteger() : 60)) { 
        log.debug "Too Soon to Evaluate..."
        return 
    } 
    else { 
        atomicState?.lastRemSenEval = getDtNow()
        if (state?.remSenEnabled && modesOk(remSenModes) && (remSensorDay || remSensorNight) && remSenTstat && getRemSenModeOk()) {
            def threshold = !remTempDiffVal ? 0 : remSenTempDiffDegrees.toDouble()
            def chgTempVal = !remTempChgDegrees ? 0 : extTempChgDegrees.toDouble()
            def hvacMode = remSenTstat ? remSenTstat?.currentThermostatMode.toString() : null
            def curTstatTemp = getDeviceTemp(remSenTstat).toDouble()
            def curTstatOperState = remSenTstat?.currentThermostatOperatingState.toString()
            def curTstatFanMode = remSenTstat?.currentThermostatFanMode
            def curCoolSetpoint = getTstatSetpoint(remSenTstat, "cool")
            def curHeatSetpoint = getTstatSetpoint(remSenTstat, "heat")
            def remSenHtemp = getSenHeatSetpointTemp()
            def remSenCtemp = getSenCoolSetpointTemp()
            def curSenTemp = (remSensorDay || remSensorNight) ? getRemoteSenTemp().toDouble() : null
            
            log.trace "Remote Sensor Rule Type: ${remSenRuleType}"
            log.trace "Remote Sensor Temp: ${curSenTemp}"
            log.trace "Thermostat Info - ( Temperature: ($curTstatTemp) | HeatSetpoint: ($curHeatSetpoint) | CoolSetpoint: ($curCoolSetpoint) | HvacMode: ($hvacMode) | OperatingState: ($curTstatOperState) | FanMode: ($curTstatFanMode) )" 
            log.trace "Desired Temps - Heat: $extHtemp | Cool: $extCtemp"
            
            if(hvacMode == "off") { return }
            
            else if (hvacMode in ["cool","auto"]) {
                if ((curSenTemp - remSenCtemp) >= threshold) {
                    if(remSenRuleType in ["Cool", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "COOL - Setting CoolSetpoint to (${(curTstatTemp - chgTempVal)}°${state?.tempUnit})"
                        remSenTstat?.setCoolingSetpoint(curTstatTemp - chgTempVal)
                        if(remSenTstatsMirror) { remSenTstatsMir*.setCoolingSetpoint(curTstatTemp - chgTempVal) }
                        log.debug "remSenTstat.setCoolingSetpoint(${curTstatTemp - chgTempVal}), ON"
                    }
                }
                else if (((extCtemp - curSenTemp) >= threshold) && ((curTstatTemp - curCoolSetpoint) >= threshold)) {
                    if(remSenRuleType in ["Cool", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "COOL - Setting CoolSetpoint to (${(curTstatTemp + chgTempVal)}°${state?.tempUnit})"
                        remSenTstat?.setCoolingSetpoint(curTstatTemp + chgTempVal)
                        if(remSenTstatsMirror) { remSenTstatsMirror*.setCoolingSetpoint(curTstatTemp - chgTempVal) }
                        log.debug "remSenTstat.setCoolingSetpoint(${curTstatTemp + chgTempVal}), OFF"
                    }
                } else {
                    //log.debug "FAN(COOL): $remSenRuleType | RuleOk: (${remSenRuleType in ["Circ", "Cool_Circ", "Heat_Cool_Circ"]})"
                    //log.debug "FAN(COOL): DiffOK (${getFanTempOk(curSenTemp, remSenCtemp, curCoolSetpoint, threshold)})"
                    if(remSenRuleType in ["Circ", "Cool_Circ", "Heat_Cool_Circ"]) {
                        if( getFanTempOk(curSenTemp, remSenCtemp, curCoolSetpoint, threshold) && getFanRunOk(curTstatOperState, curTstatFanMode) ) {
                            log.debug "Running $remSenTstat Fan for COOL Circulation..."
                            remSenTstat?.fanOn()
                            if(remSenTstatsMir) { 
                                remSenTstatsMir.each { mt -> 
                                    log.debug "Mirroring $mt Fan Run for COOL Circulation..."
                                    mt?.fanOn() 
                                }
                            }
                            atomicState?.lastRemSenFanRunDt = getDtNow()
                        }
                    }
                }
            }
            //Heat Functions....
            else if (hvacMode in ["heat", "emergency heat", "auto"]) {
                if ((remSenHtemp - curSenTemp) >= threshold) {
                    if(remSenRuleType in ["Heat", "Heat_Cool", "Heat_Cool_Circ"]) { 
                        log.debug "HEAT - Setting HeatSetpoint to (${(curTstatTemp + chgTempVal)}°${state?.tempUnit})"
                        remSenTstat?.setHeatingSetpoint(curTstatTemp + chgTempVal)
                        if(remSenTstatsMirror) { remSenTstatsMir*.setHeatingSetpoint(curTstatTemp + chgTempVal) }
                        log.debug "remSenTstat.setHeatingSetpoint(${curTstatTemp + chgTempVal}), ON"
                    }
                }
                else if (((curSenTemp - remSenHtemp) >= threshold) && ((curHeatSetpoint - curTstatTemp) >= threshold)) {
                    if(remSenRuleType in ["Heat", "Heat_Cool", "Heat_Cool_Circ"]) {
                        log.debug "HEAT - Setting HeatSetpoint to (${(curTstatTemp - chgTempVal)}°${state?.tempUnit})"
                        remSenTstat?.setHeatingSetpoint(curTstatTemp - chgTempVal)
                        if(remSenTstatsMirror) { remSenTstatsMirror*.setHeatingSetpoint(curTstatTemp - chgTempVal) }
                        log.debug "remSenTstat.setHeatingSetpoint(${curTstatTemp - chgTempVal}), OFF"
                    }
                } else { 
                    //log.debug "FAN(HEAT): $remSenRuleType | RuleOk: (${remSenRuleType in ["Circ", "Heat_Circ", "Heat_Cool_Circ"]})"
                    //log.debug "FAN(HEAT): DiffOK (${getFanTempOk(curSenTemp, remSenHtemp, curHeatSetpoint, threshold)})"
                    if (remSenRuleType in ["Circ", "Heat_Circ", "Heat_Cool_Circ"]) {
                        if( getFanTempOk(curSenTemp, remSenHtemp, curHeatSetpoint, threshold) && getFanRunOk(curTstatOperState, curTstatFanMode) ) {
                            log.debug "Running $remSenTstat Fan for HEAT Circulation..."
                            remSenTstat?.fanOn()
                            if(remSenTstatsMir) { 
                                remSenTstatsMir.each { mt -> 
                                    log.debug "Mirroring $mt Fan Run for HEAT Circulation..."
                                    mt?.fanOn() 
                                }
                            }
                            atomicState?.lastRemSenFanRunDt = getDtNow()
                        }
                    }
                }
            } else { log.warn "remSenEvtEval: Did not receive a valid Thermostat Mode..." }
        }
        else {
            def remSenHtemp = getSenHeatSetpointTemp()
            def remSenCtemp = getSenCoolSetpointTemp()
            remSenTstat?.setHeatingSetpoint(remSenHtemp)
            remSenTstat?.setCoolingSetpoint(remSenCtemp)
            if(remSenTstatsMir) {
                remSenTstatsMir*.setHeatingSetpoint(remSenHtemp)
                remSenTstatsMirror*.setCoolingSetpoint(remSenCtemp)
            }
        }
    }
}

def getRemSenFanTempOk(senTemp, userTemp, curTemp, threshold) {
    def diff1 = (Math.abs(senTemp - userTemp)?.round(1) < threshold)
    def diff2 = (Math.abs(userTemp - curTemp)?.round(1) < threshold)
    log.debug "getRemSenFanTempOk: ( Sensor Temp - Set Temp: (${Math.abs(senTemp - userTemp).round(1)}) < Threshold Temp: (${threshold}) ) - ($diff1)"
    log.debug "getRemSenFanTempOk: ( Set Temp - Current Temp: (${Math.abs(userTemp - curTemp).round(1)}) < Threshold Temp: (${threshold}) ) - ($diff2)"
    return (diff1 && diff2) ? true : false
}

def getRemSenModeOk() {
    if (remSensorDay && (!remSensorDayModes && !remSensorNight && !remSensorNightModes)) {
        return true
    }
    else if (remSensorDayModes || remSensorNightModes) {
        return (isInMode(remSensorDayModes) || isInMode(remSensorNightModes)) ? true : false
    } 
    else {
        return false
    }
}

def getRemSenFanRunOk(operState, fanState) { 
    log.trace "getRemSenFanRunOk($operState, $fanState)"
    def val = remSenTimeBetweenRuns ? remSenTimeBetweenRuns.toInteger() : 3600
    def cond = ((remSenRuleType in ["Circ", "Heat_Circ", "Cool_Circ", "Heat_Cool_Circ"]) && operState == "idle" && fanState == "auto") ? true : false
    def timeSince = (getLastRemSenFanRunDtSec() > val)
    def result = (timeSince && cond) ? true : false
    log.debug "getRemSenFanRunOk(): cond: $cond | timeSince: $timeSince | val: $val | $result"
    return result
}

def getLastRemSenFanRunDtSec() { return !atomicState?.lastRemSenFanRunDt ? 100000 : GetTimeDiffSeconds(atomicState?.lastRemSenFanRunDt).toInteger() }

def getDeviceTemp(dev) {
    return dev ? dev?.currentValue("temperature").toString().replaceAll("\\[|\\]", "").toDouble() : null
}

def getTstatSetpoint(tstat, type) {
    if(tstat) {
        def coolSp = !tstat?.currentCoolingSetpoint ? 0 : tstat?.currentCoolingSetpoint.toDouble()
        def heatSp = !tstat?.currentHeatingSetpoint ? 0 : tstat?.currentHeatingSetpoint.toDouble()
        return (type == "cool") ? coolSp : heatSp
    }
    else { return 0 }
}

def getRemoteSenTemp() {
    if(!getUseNightSensor() && remSensorDay) {
        return getDeviceTempAvg(remSensorDay).toDouble()  
    }
    else if(getUseNightSensor() && remSensorNight) {
        return getDeviceTempAvg(remSensorNight).toDouble() 
    }
    else {
        return 0.0
    }
}

def getSenCoolSetpointTemp() {
    if(!getUseNightSensor() && remSenDayCoolTemp) {
        return remSenDayCoolTemp?.toDouble()  
    }
    else if(getUseNightSensor() && remSenNightCoolTemp) {
        return remSenNightCoolTemp?.toDouble()        
    }
    else {
        return remSenTstat ? getTstatSetpoint(remSenTstat, "cool") : 0
    }
}

def getSenHeatSetpointTemp() {
    if(!getUseNightSensor() && remSenDayHeatTemp) {
        return remSenDayHeatTemp?.toDouble()  
    }
    else if(getUseNightSensor() && remSenNightHeatTemp) {
        return remSenNightHeatTemp?.toDouble()        
    }
    else {
        return remSenTstat ? getTstatSetpoint(remSenTstat, "heat") : 0
    }
}

def setRemSenTstatCapabilities() {
    try {
        def canCool = true
        def canHeat = true
        def hasFan = true
        if(remSenTstat) { 
            canCool = (remSenTstat?.currentCanCool == "true") ? true : false
            canHeat = (remSenTstat?.currentCanHeat == "true") ? true : false
            hasFan = (remSenTstat?.currentHasFan == "true") ? true : false
        }
        state?.remSenTstatCanCool = canCool
        state?.remSenTstatCanHeat = canHeat
        state?.remSenTstatHasFan = hasFan
    } catch (e) { }
}

def remSenRuleEnum() {
    def canCool = state?.remSenTstatCanCool ? true : false
    def canHeat = state?.remSenTstatCanHeat ? true : false
    def hasFan = state?.remSenTstatHasFan ? true : false
    def vals = []
    
    //log.debug "remSenRuleEnum -- hasFan: $hasFan (${atomicState?.remSenTstatHasFan} | canCool: $canCool (${atomicState?.remSenTstatCanCool} | canHeat: $canHeat (${atomicState?.remSenTstatCanHeat}"
    
    if (canCool && !canHeat && hasFan) { vals = ["Cool":"Cool", "Circ":"Circulate(Fan)", "Cool_Circ":"Cool/Circulate(Fan)"] }
    else if (canCool && !canHeat && !hasFan) { vals = ["Cool":"Cool"] }
    else if (!canCool && canHeat && hasFan) { vals = ["Circ":"Circulate(Fan)", "Heat":"Heat", "Heat_Circ":"Heat/Circulate(Fan)"] }
    else if (!canCool && canHeat && !hasFan) { vals = ["Heat":"Heat"] }
    else if (!canCool && !canHeat && hasFan) { vals = ["Circ":"Circulate(Fan)"] }
    else if (canCool && canHeat && !hasFan) { vals = ["Heat_Cool":"Auto", "Heat":"Heat", "Cool":"Cool"] }
    else { vals = [ "Heat_Cool":"Auto", "Heat":"Heat", "Cool":"Cool", "Circ":"Circulate(Fan)", "Heat_Cool_Circ":"Auto/Circulate(Fan)", "Heat_Circ":"Heat/Circulate(Fan)", "Cool_Circ":"Cool/Circulate(Fan)" ] }
    //log.debug "remSenRuleEnum vals: $vals"
    return vals
}

def longTimeEnum() {
    def vals = [
        60:"1 Minute", 300:"5 Minutes", 600:"10 Minutes", 900:"15 Minutes", 1200:"20 Minutes", 1500:"25 Minutes", 1800:"30 Minutes", 
        3600:"1 Hour", 7200:"2 Hours", 14400:"4 Hours", 21600:"6 Hours", 43200:"12 Hours", 86400:"24 Hours"
    ]
    return vals
}

def shortTimeEnum() {
    def vals = [
        1:"1 Second", 2:"2 Seconds", 3:"3 Seconds", 4:"4 Seconds", 5:"5 Seconds", 6:"6 Seconds", 7:"7 Seconds",
        8:"8 Seconds", 9:"9 Seconds", 10:"10 Seconds", 15:"15 Seconds", 30:"30 Seconds"
    ]
    return vals
}

def smallTempEnum() {
    def tempUnit = state?.tempUnit
    def vals = [
        1:"1°${tempUnit}", 2:"2°${tempUnit}", 3:"3°${tempUnit}", 4:"4°${tempUnit}", 5:"5°${tempUnit}", 6:"6°${tempUnit}", 7:"7°${tempUnit}",
        8:"8°${tempUnit}", 9:"9°${tempUnit}", 10:"10°${tempUnit}"
    ]
    return vals
}

def remSenRuleName() {
    def result = "unknown"
    if(remSenRuleType) {
        remSenRuleEnum().each { item ->
            if(item?.key.toString() == remSenRuleType?.toString()) { 
                result = item?.value
            }
        }
    } 
    return result
}

/******************************************************************************  
|                			WATCH CONTACTS AUTOMATION CODE	                  |
*******************************************************************************/

def contactWatchPage() {
    def pName = "conWat"
    dynamicPage(name: "contactWatchPage", title: "Thermostat/Contact Automation", uninstall: false) {
        section("When These Contacts are open, Turn Off this Thermostat") {
            def req = (conWatContacts || wContTstat) ? true : false
            input name: "conWatContacts", type: "capability.contactSensor", title: "Which Contact(s)?", multiple: true, submitOnChange: true, required: req,
                    image: getAppImg("contact_icon.png")
            input name: "conWatTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req,
                    image: getAppImg("thermostat_icon.png")
            if(conWatTstat) {
                input name: "conWatTstatMir", type: "capability.thermostat", title: "Mirror commands to these Thermostats?", multiple: true, submitOnChange: true, required: false,
                    image: getAppImg("thermostat_icon.png")
            }
        }
        if(conWatContacts && conWatTstat) {
            section("Delay Values:") {
                input name: "conWatOffDelay", type: "enum", title: "Delay Off (in minutes)", defaultValue: 300, metadata: [values:longTimeEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")

                input "conWatRestModeOnClose", "bool", title: "Restore Previous mode after Closed?", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("restore_icon.png")
                if(conWatRestModeOnClose) {
                    input name: "conWatOnDelay", type: "enum", title: "Delay On (in minutes)", defaultValue: 300, metadata: [values:longTimeEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                }
            }
            
            section("Only During these Days, Times, or Modes:") {
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: getDayModeTimeDesc(pName), params: [pName: "${pName}"], image: getAppImg("notification_opt_icon.png")
            }
            section("Notifications:") {
                input "conWatPushMsOn", "bool", title: "Send Push Notifications on Changes?", required: false, defaultValue: true, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
                if(conWatPushMsgOn) {
                    def notifDesc = ((settings?."${pName}NotifRecips") || (settings?."${pName}NotifRecips" || settings?."${pName}NotifPhones")) ? 
                            "Custom Recipients are Set\nTap to Modify..." : "Tap to configure..."
                    paragraph "Custom Recipients are optional. If you choose not to set them the Nest Managers settings will be used.", image: getAppImg("instruct_icon.png")
                    href "setRecipientsPage", title: "Set Custom Recipients", description: notifDesc, params: [pName: "${pName}"], image: getAppImg("notification_opt_icon.png")
                }
            }
        }
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
    }
}

def isConWatConfigured() {
    def devOk = (conWatContacts && conWatTstat) ? true : false
    return devOk
}

def conWatTimeOk() {
    try {
        def strtTime = null
        def stopTime = null
        def now = new Date()
        if(settings?.conWatStartTime && settings?.conWatStopTime) { 
            if(settings?.conWatStartTime) { strtTime = settings?.conWatStartTime }
            if(settings?.conWatStopTime) { stopTime = settings?.conWatStopTime }
        } else { return true } 
        
        if (strtTime && stopTime) {
            return timeOfDayIsBetween(strtTime, stopTime, new Date(), location?.timeZone) ? false : true
        } else { return true }
    } catch (ex) { LogAction("conWatTimeOk Exception: ${ex}", "error", true, true) }
}

def getConWatContactsOk() { return conWatContacts?.currentState("contact")?.value.contains("open") ? false : true }
def conWatContactOk() { return (!conWatContacts && !conWatTstat) ? false : true }
def conWatScheduleOk() { return (modesOk(settings?.conWatModes) && daysOk(settings?.conWatDays) && conWatTimeOk()) ? true : false }
def getConWatOpenDtSec() { return !state?.conWatOpenDt ? 100000 : GetTimeDiffSeconds(state?.conWatOpenDt).toInteger() }
def getConWatCloseDtSec() { return !state?.conWatCloseDt ? 100000 : GetTimeDiffSeconds(state?.wContCloseDt).toInteger() }
def getConWatOffDelayVal() { return !conWatOffDelay ? 300 : (conWatOffDelay.toInteger()) }
def getConWatOnDelayVal() { return !wContOnDelay ? 300 : (conWatOnDelay.toInteger()) }
def getOpenCt = "${getOpenContacts(conWatContacts)?.join(", ")}"
def getClosedCt = "${getClosedContacts(conWatContacts)?.join(", ")}"

def conWatCheck() {
    //log.trace "conWatCheck..."
    def curMode = conWatTstat.currentState("thermostatMode").value.toString()
    def modeOff = (curMode == "off") ? true : false
    def lastMode = state?.conWatRestoreMode ? state?.conWatRestoreMode : curMode
    def openCtDesc = getOpenContacts(conWatContacts) ? " '${getOpenContacts(conWatContacts)?.join(", ")}' " : " a selected contact "
    //log.debug "curMode: $curMode | modeOff: $modeOff | conWatRestModeOnClose: $conWatRestModeOnClose | lastMode: $lastMode"
    //log.debug "state.conWatTurnedOff: $state?.conWatTurnedOff | getConWatCloseDtSec(): ${getConWatCloseDtSec()}"
    if(getConWatContactsOk()) {
        if(modeOff && conWatRestModeOnClose && state?.conWatTurnedOff) {
            if(getConWatCloseDtSec() >= (getConWatOnDelayVal()?.toInteger() - 5)) {
                if(lastMode && state?.conWatRestoreMode != curMode) {
                    if(setTstatMode(conWatTstat, lastMode)) {
                        state.conWatTurnedOff = false
                        if(conWatTstatMir) { 
                            conWatTstatMir.each { t ->
                                 setTstatMode(t, lastMode)
                                //log.debug("Restoring ${lastMode} to ${t}")
                            }
                        }
                        LogAction("The Mode on ${conWatTstat.label} has been restored to '${lastMode.toString().toUpperCase()}' because ALL contacts are now Closed again...", "info", true)
                        if(conWatPushMsgOn) {
                            sendNofificationMsg("The Mode on ${conWatTstat.label} has been restored to '${lastMode.toString().toUpperCase()}' because ALL contacts are now Closed again...", "Info", 
                                    conWatNotifRecips, conWatNotifPhones, conWatUsePush)
                        }
                    }
                    else { LogAction("conWatCheck() | lastMode was not found...", "error", true) }
                }
            }
        } 
    }
    
    if (!getConWatContactsOk()) {
        if(!modeOff) {
            if(getConWatOpenDtSec() >= (getConWatOffDelayVal().toInteger() - 2)) {
                if(conWatRestModeOnClose) { 
                    state.conWatRestoreMode = curMode
                    //log.debug "conWatRestoreMode Set to: ${state?.conWatRestoreMode}"
                }
                //log.debug("${openCtDesc} are Open: Turning off ${conWatTstat}")
                state.conWatTurnedOff = true
                conWatTstat?.off()
                if(conWatTstatMir) { 
                    conWatTstatMir.each { t ->
                        t.off()
                        log.debug("Mirrored Off to ${t}")
                    }
                }
                LogAction("conWatCheck: The ${conWatTstat.label} has been turned off because${openCtDesc}has been Opened...", "warning", true)
                if(sendPushOnWc) {
                    sendNofificationMsg("${conWatTstat.label} has been turned off because${openCtDesc}has been Opened...", "Info", 
                                    conWatNotifRecips, conWatNotifPhones, conWatUsePush)
                }
            }
        } else { LogAction("conWatCheck() | Skipping change because mode is already 'Off'", "info", true) }
    }
}

def conWatContactEvt(evt) {
    //log.debug "conWatContactEvt: ${evt?.value}"
    def curMode = conWatTstat?.currentThermostatMode.toString()
    def modeOff = (curMode == "off") ? true : false
    def conOpen = (evt?.value == "open") ? true : false
    if(conWatScheduleOk()) {
        if (conOpen) {
            state?.conWatOpenDt = getDtNow()
        }
        else if(!conOpen && getConWatContactsOk()) {
            state.conWatCloseDt = getDtNow()
        }
        log.debug "conWatContactEvt: A monitored contact is '${evt?.value.toString().toUpperCase()}' | Scheduling Evaluation in (${getConWatOffDelayVal()} seconds)..."
        runIn(getConWatOffDelayVal()?.toInteger(), "conWatCheck", [overwrite: true]) 
    }
}

/********************************************************************************  
|                			External Temp AUTOMATION CODE	     				|
*********************************************************************************/

def extTempPage() {
    def pName = "extTmp"
    dynamicPage(name: "extTempPage", title: "Thermostat/External Temps Automation", uninstall: false) {
        section("When External Temp reaches Turn Off this Thermostat when the Local Weather temp goes above a certain threshold.  ") {
            def req = ((extTmpUseWeather || (!extTmpUseWeather && exTmpTemp)) || extTmpTstat) ? true : false
            input "extTmpUseWeather", "bool", title: "Use Local Weather as External Sensor?", required: req, defaultValue: false, submitOnChange: true,
                    image: getAppImg("weather_icon.png")
            if(exUseWeather){
                getExtConditions()
                def tmpVal = (location?.temperatureScale == "C") ? state?.curWeatherTemp_c : state?.curWeatherTemp_f
                paragraph "Current Weather Temp: $tmpVal", image: " "
                input name: "extTmpZipcode", type: "number", title: "Custom ZipCode (Default is Hub Loction)?", submitOnChange: true, required: false,
                        image: getAppImg("location_icon.png")
                input name: "extTmpWeatherRfrshVal", type: "number", title: "Update Weather (in Minutes)?", default: 5, submitOnChange: true, required: false,
                        image: getAppImg("start_time_icon.png")
            }
            if(!exUseWeather) {
                input "extTmpTemp", "capability.temperatureMeasurement", title: "Which Temperature Sensors?", submitOnChange: true, multiple: false, required: req, 
                        image: getAppImg("temperature_icon.png")
                if(exTemp) {
                    def tmpVal = "${extTmpTemp?.currentValue("temperature").toString()}${location?.temperatureScale.toString()}"
                    paragraph "Current Sensor Temp: ${tmpVal}", image: " "
                }
            }
            input name: "extTmpTstat", type: "capability.thermostat", title: "Which Thermostat?", multiple: false, submitOnChange: true, required: req, image: getAppImg("thermostat_icon.png")
            if(exTstat) {
                def tmpVal = "${extTmpTstat?.currentValue("temperature").toString()}${location?.temperatureScale.toString()}"
                paragraph "Current Thermostat Temp: ${tmpVal}", image: " "
                input name: "extTmpDiffVal", type: "number", title: "When Inside Temp is within this many Degrees of External Temp?", range: "-30..30", default: 0, submitOnChange: true, required: false,
                        image: getAppImg("temp_icon.png")
            }
        }
        if((extTmpUseWeather || extTmpTemp) && extTmpTstat) {
            section("Only During these Days, Times, or Modes:") {
                href "setDayModeTimePage", title: "Configure Days, Times, or Modes", description: getDayModeTimeDesc(pName), params: [pName: "${pName}"], image: getAppImg("notification_opt_icon.png")
            }
            section("Delay Values:") {
                input name: "extTmpOffDelay", type: "enum", title: "Delay Off (in minutes)", defaultValue: 300, metadata: [values:longTimeEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")

                input "extTmpRestoreMode", "bool", title: "Restore Previous Mode when Temp goes below Threshold?", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("restore_icon.png")
                if(extTmpRestoreMode) {
                    input name: "extTmpOnDelay", type: "enum", title: "Delay On (in minutes)", defaultValue: 300, metadata: [values:longTimeEnum()], required: false, submitOnChange: true,
                        image: getAppImg("delay_time_icon.png")
                }
            }
            section("Notifications:") {
                input "extTmpPushMsgOn", "bool", title: "Send Push Notifications on Changes?", required: false, defaultValue: true, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
                if(extTmpPushMsgOn) {
                    def notifDesc = ((settings?."${pName}NotifRecips") || (settings?."${pName}NotifRecips" || settings?."${pName}NotifPhones")) ? 
                            "Custom Recipients are Set\nTap to Modify..." : "Tap to configure..."
                    paragraph "Custom Recipients are not required. I you don't set them the Nest Managers settings will be used.", image: getAppImg("instruct_icon.png")
                    href "setRecipientsPage", title: "Set Custom Recipients", description: notifDesc, params: [pName: "${pName}"], image: getAppImg("notification_opt_icon.png")
                }
            }
        }
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
    }
}

def isExtTmpConfigured() {
    def devOk = ((extTmpUseWeather || extTmpTemp) && extTmpTstat) ? true : false
    state.isExtTmpConfigured = devOk
    return devOk
}

def getExtConditions() {
    def cur = parent?.getWData()
    state?.curWeather = cur?.current_observation
    state?.curWeatherTemp_f = Math.round(cur?.current_observation?.temp_f).toInteger()
    state?.curWeatherTemp_c = Math.round(cur?.current_observation?.temp_c).toInteger()
    state?.curWeatherHum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "")
    state?.curWeatherLoc = cur?.current_observation?.display_location?.full.toString()
    log.debug "${state?.curWeatherLoc} Weather | humidity: ${state?.curWeatherHum} | temp_f: ${state?.curWeatherTemp_f} | temp_c: ${state?.curWeatherTemp_c}"
}

def getExtTmpOk() { 
    def intTemp = extTmpTstat ? Math.round(exTstat?.currentValue("temperature")).toInteger() : null
    def extTemp = null
    
    if(!extTmpUseWeather && extTmpTemp) { extTemp = Math.round(exTemp?.currentValue("temperature")).toInteger() }
    else {
        if(extTmpUseWeather && (state?.curWeatherTemp_f || state?.curWeatherTemp_c)) {
            if(location?.temperatureScale == "C" && state?.curWeatherTemp_c) { extTemp = state?.curWeatherTemp_c }
            else { extTemp = state?.curWeatherTemp_f }
        } else { return true }
    }
    log.debug "Inside Temp: $intTemp | Outside Temp: $extTemp | Temp Threshold: ${extTmpDiffVal}"
    if(intTemp && extTemp && extTmpDiffVal) { 
        def tempDiff = (extTemp < intTemp) ? -(extTemp - intTemp) : (extTemp - intTemp)
        log.debug "Inside Temp: $intTemp | Outside Temp: $extTemp | Temp Threshold: ${extTmpDiffVal} | Actual Difference: $tempDiff"
        if(extTmpDiffVal < 0 && extTmpDiffVal <= tempDiff) { return false }
        else if(extTmpDiffVal > 0 && tempDiff <= extTmpDiffVal) { return false }
        
        return true
    }
    LogAction("getExTempOk() | Failed to complete the temp check", "error", true)
    return null
}

def extTempTimeOk() {
    try {
        def strtTime = null
        def stopTime = null
        def now = new Date()
        if(settings?.exStartTime && settings?.exStopTime) { 
            if(settings?.exStartTime) { strtTime = settings?.exStartTime }
            if(settings?.exStopTime) { stopTime = settings?.exStopTime }
        } else { return true }  
        if (strtTime && stopTime) {
            return timeOfDayIsBetween(strtTime, stopTime, new Date(), location?.timeZone) ? false : true
        } else { return true }
    } catch (ex) { LogAction("exTimeOk Exception: ${ex}", "error", true, true) }
}

def extTmpScheduleOk() { return (modesOk(settings?.exModes) && daysOk(settings?.exDays) && extTmpTimeOk()) ? true : false }
def getExtTmpGoodDtSec() { return !state?.extTmpTempGoodDt ? 100000 : GetTimeDiffSeconds(state?.extTmpTempGoodDt).toInteger() }
def getExtTmpBadDtSec() { return !state?.extTmpTempBadDt ? 100000 : GetTimeDiffSeconds(state?.extTmpTempBadDt).toInteger() }
def getExtTmpOffDelayVal() { return !extTmpOffDelay ? 300 : (extTmpOffDelay.toInteger()) }
def getExtTmpOnDelayVal() { return !extTmpOnDelay ? 300 : (extTmpOnDelay.toInteger()) }
def getExtTmpWeatherRefreshVal() { return !extTmpWeatherRfrshVal ? 1 : (extTmpWeatherRfrshVal.toInteger()) }

def extTmpCheck() {
    log.trace "exCheck..."
    def curMode = exTstat.currentState("thermostatMode").value.toString()
    if(getExtTmpOk()) {
        if(curMode.equals("off") && extTmpRestoreMode && state?.extTmpTurnedOff == true) {
            if(getExtTmpGoodDtSec() >= (getExtTmpOnDelayVal().toInteger() - 2)) {
                def lastMode = state?.extTmpRestoreMode ?: curMode
                if(!state?.extTmpRestoreMode.equals(curMode)) {
                    if(lastMode) {
                        if(setTstatMode(extTmpTstat, lastMode)) {
                            state.extTmpTurnedOff = false
                            LogAction("${extTmpTstat?.label} has been restored to ${lastMode} Mode because External Temp is above Threshhold...", "info", true)
                            if(extTmpPushMsgOn) {
                                sendNofificationMsg("${extTmpTstat?.label} has been restored to ${lastMode} Mode because External Temp is above Threshhold...", "Info", 
                                        extTmpNotifRecips, extTmpNotifPhones, extTmpUsePush)
                            }
                        }
                    } else { LogAction("exCheck() | lastMode was not found...", "error", true) }
                }
            }
        } 
    }
    
    if (!getExtTmpOk()) {
        if(!curMode.equals("off")) {
            if(getExtTmpBadDtSec() >= (getExtTmpOffDelayVal().toInteger() - 2)) {
                log.debug "!getExtTmpsOk..."
                if(extTmpRestoreMode) { 
                    state.extTmpRestoreMode = curMode
                    log.debug "extTmpRestoreMode Saved as: ${state?.extTmpRestoreMode}"
                }
                log.debug("External Temp is at Threshhold turning off ${extTmpTstat}")
                extTmpTstat?.off()
                state.extTmpTurnedOff = true
                LogAction("${extTmpTstat.label} has been turned off because External Temp is at Threshhold", "info", true)
                if(extTmpPushMsgOn) {
                    sendNofificationMsg("${extTmpTstat.label} has been turned off because External Temp is at Threshhold", "Info", extTmpNotifRecips, extTmpNotifPhones, extTmpUsePush)
                }
            }
        } else { LogAction("extTmpCheck() | Skipping change because mode is already 'Off'", "info", true) }
    }
}

def extTmpEvt(evt) {
    log.debug "exTmpEvt: ${evt?.value}"
    def schedOff = false
    def schedOn = false
    def curMode = extTmpTstat?.currentState("thermostatMode").value.toString()
    def exOk = getExtTmpOk()
    log.debug "exOk: $exOk"
    if(extTmpScheduleOk()) {
        if (!exOk && !curMode.equals("off")) {
            state.extTmpGoodDt = getDtNow()
            log.debug "extTmpEvt() | Scheduling Thermostat OFF in (${getExtTmpOffDelayVal()} seconds)..."
            runIn(getExtTmpOffDelayVal().toInteger(), "extTmpCheck", [overwrite: true]) 
        }
        else if(exOk && (extTmpRestoreMode && state?.extTmpTurnedOff == true)) {
            state.extTmpBadDt = getDtNow()
            log.debug "extTmpEvt() | Scheduling Thermostat ON in (${getExtTmpOnDelayVal()} seconds)..."
            runIn(getExtTmpOnDelayVal().toInteger(), "extTmpCheck", [overwrite: true])
        }
    }
}


/********************************************************************************  
|                			MODE AUTOMATION CODE	     						|
*********************************************************************************/
def nestModePresPage() {
    def pName = "nMode"
    dynamicPage(name: "nestModePresPage", title: "Mode - Nest Home/Away Automation", uninstall: false) {
        if(!nModePresSensor) {
            def modeReq = (!nModePresSensor && (nModeHomeModes || nModeAwayModes))
            section("Set Nest Presence with ST Modes:") {
                input "nModeHomeModes", "mode", title: "Modes that set Nest 'Home'", multiple: true, submitOnChange: true, required: homeModes,
                        image: getAppImg("mode_home_icon.png")
                input "nModeAwayModes", "mode", title: "Modes that set Nest 'Away'", multiple: true, submitOnChange: true, required: homeModes,
                        image: getAppImg("mode_away_icon.png")
            }
        }
        section("Set Nest Presence Via Presence Sensor:") {
            paragraph "Choose a Presence Sensor(s) to use to set your Nest to Home/Away", image: getAppImg("instruct_icon")
            input "modePresSensor", "capability.presenceSensor", title: "Select a Presence Sensor", multiple: true, submitOnChange: true, required: false,
                    image: getAppImg("presence_icon.png")
            if(nModePresSensor) {
                if (nModePresSensor.size() > 1) {
                    paragraph "Nest will be set 'Away' when all Presence sensors leave and will return to 'Home' arrive", getAppImg("instruct_icon.png")
                }
                paragraph "Presence State: ${nModePresSensor.currentPresence}", image: " "
                input (name: "nModePresSensorDelay", type: "bool", title: "Delay Changes?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("switch_icon.png"))
                if(nModePresSensorDelay) {
                    input "nModePresSensorDelayVal", "enum", title: "Delay before Changing?", required: false, defaultValue: 60, metadata: [values:longTimeEnum()], 
                            submitOnChange: true, image: getAppImg("delay_time_icon.png")
                }
            }
        }
        if(((nModeHomeModes && nModeAwayModes) && !nModePresSensor) || nModePresSensor) {
            section("Notifications:") {
                input "nModePushMsgOn", "bool", title: "Send Push Notifications on Changes?", required: false, defaultValue: false, submitOnChange: true,
                        image: getAppImg("notification_icon.png")
                if(nModePushMsgOn) {
                    def notifDesc = ((settings?."${pName}NotifRecips") || (settings?."${pName}NotifRecips" || settings?."${pName}NotifPhones")) ? 
                            "Custom Recipients are Set\nTap to Modify..." : "Tap to configure..."
                    paragraph "Custom Recipients are not required. I you don't set them the Nest Managers settings will be used.", image: getAppImg("instruct_icon.png")
                    href "setRecipientsPage", title: "Set Custom Recipients", description: notifDesc, params: [pName: "${pName}"], image: getAppImg("notification_opt_icon.png")
                }
            }
        }
        
        section("Help and Instructions:") {
            href url:"https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html", style:"embedded", required:false, title:"Help Pages", 
                description:"View the Help and Instructions Page...", image: getAppImg("help_icon.png")
        }
    }
}

def isNestModesConfigured() {
    def devOk = ((!nModePresSensor && (nModeHomeModes && nModeAwayModes)) || nModePresSensor) ? true : false
    state.isNestModesConfigured = devOk
    return devOk
}

def nModeWatcher(evt) { 
    log.debug "modeWatcher: $evt"
    if(!modePresSensor) {
        checkNestMode()
    } 
}

def nModePresEvt(evt) {
    log.debug "nModePresEvt: [${evt?.displayName}] is (${evt?.value})"
    def curNestPres = (getNestLocPres() == "home") ? "present" : "not present"
    if((evt?.value.toString() != curNestPres) ? true : false) {
        if(nModePresSensorDelay) {
            runIn(nModePresSensorDelayVal.toInteger(), "checkNestMode", [overwrite: true])
        } else {
            checkNestMode()
        }
    }
}

def checkNestMode() {
    try {
        def curStMode = location?.mode?.toString()
        def nModePresAway = nModePresSensor?.find { it?.currentPresence == "present" }
        def nestModeAway = (getNestLocPres() == "home") ? false : true
        def away = ((nModePresSensor && nModePresAway) || (!nModePresSensor && (curStMode in nModeAwayModes))) ? true : false
        def home = ((nModePresSensor && !nModePresAway) || (!nModePresSensor && (curStMode in nModeHomeModes))) ? true : false
        def awayDesc = nModePresSensor ? "The monitored Presence device(s) is No Longer Present setting" : "The mode ($location.mode) has triggered"
        def homeDesc = nModePresSensor ? "The monitored Presence device(s) in Present setting" : "The mode ($location.mode) has triggered" 
        if((away && !nestModeAway) && !home) {
            LogAction("$desc Nest 'Away'", "info", true)
            parent?.setStructureAway(null, true) 
            sendNofificationMsg("$awayDesc Nest 'Away", "Info", nModeNotifRecips, nModeNotifPhones, nModeUsePush)
        }
        else if ((home && nestModeAway) && !away) {
            LogAction("$desc Nest 'Home'", "info", true)
            parent?.setStructureAway(null, false) 
            sendNofificationMsg("$homeDesc Nest 'Home", "Info", nModeNotifRecips, nModeNotifPhones, nModeUsePush)
        } 
        else {
            LogAction("checkNestMode: Conditions are not valid to change mode | current ST Mode: $curStMode | Nest Mode Away?: $nestModeAway | Away: $away | Home: $home", "info", true)
        }
    } catch (ex) { LogAction("checkNestMode Exception: ($ex)", "error", true) }
    
}

def getNestLocPres() {
    if(!parent?.locationPresence()) { return null }
    else {
        return parent?.locationPresence()
    }
}

/************************************************************************************************
|							              Dynamic Pages							                |
*************************************************************************************************/

def sendNofificationMsg(msg, msgType, recips = null, sms = null, push = null) {
    if(recips || sms || push) {
        parent?.extSendMsg(msg, msgType, recips, sms, push)
        //LogAction("Send Push Notification to $recips...", "info", true)
    } else {
        parent?.extSendMsg(msg, msgType)
    }
}

def setRecipientsPage(params) {
    if(params) { atomicState.curPName = params?.pName }
    def pName = atomicState?.curPName
    //log.debug "params:  ${state.curPName}"
    dynamicPage(name: "setRecipientsPage", title: "Set Push Notifications Recipients", uninstall: false) {
        def notifDesc = !location.contactBookEnabled ? "Enable push notifications below..." : "Select People or Devices to Receive Notifications..."
        section("${notifDesc}:") {
            if(!location.contactBookEnabled) {
                input "${pName}UsePush", "bool", title: "Send Push Notitifications", required: false, defaultValue: false, image: getAppImg("notification_icon.png")
            } else {
                input("${pName}NotifRecips", "contact", title: "Send notifications to", required: false, image: getAppImg("notification_icon.png")) {
                    input ("${pName}NotifPhones", "phone", title: "Phone Number to send SMS to...", description: "Phone Number", required: false)
                }
            }
        }
    }
}

def setDayModeTimePage(params) {
    if(params) { atomicState.curPName = params?.pName }
    def pName = atomicState?.curPName
    dynamicPage(name: "setDayModeTimePage", title: "Select Days, Times or Modes", uninstall: false) {
        section("Only During these Days, Times, or Modes:") {
            def timeReq = (settings?."${pName}StartTime" || settings."${pName}StopTime") ? true : false
            input "${pName}StartTime", "time", title: "Start time", required: timeReq, image: getAppImg("start_time_icon.png")
            input "${pName}StopTime", "time", title: "Stop time", required: timeReq, image: getAppImg("stop_time_icon.png")
            input "${pName}Modes", "mode", title: "Only with These Modes...", multiple: true, required: false, image: getAppImg("mode_icon.png")
            input "${pName}Days", "enum", title: "Only on certain days of the week", multiple: true, required: false,
                    options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], image: getAppImg("day_calendar_icon.png")
        }
    }
}

def getDayModeTimeDesc(var) {
	def pName = var
	def ss = (settings?."${pName}StartTime" && settings?."${pName}StopTime") ? "Start: ${time2Str(settings?."${pName}StartTime")} | Stop: ${time2Str(settings?."${pName}StopTime")}" : "" 
    def dys = settings?."${pName}Days".toString().replaceAll("\\[|\\]", "")
    def mds = settings?."${pName}Modes".toString().replaceAll("\\[|\\]", "")
    def md = settings?."${pName}Modes" ? "Modes: (${mds})" : ""
    def dy = settings?."${pName}Days" ? "Days: (${dys})" : ""
    def confDesc = ((settings?."${pName}StartTime" || settings?."${pName}StopTime") || settings?."${pName}Modes" || settings?."${pName}Days") ? 
    		"${ss ? "$ss\n" : ""}${md ? "$md\n" : ""}${dy ? "$dy" : ""}\n\nTap to Modify..." : "Tap to Configure..."
}

/************************************************************************************************
|							GLOBAL Code | Logging AND Diagnostic							    |
*************************************************************************************************/

def getClosedContacts(contacts) {
    if(contacts) {
        def cnts = contacts?.findAll { it?.currentContact == "closed" }
        return cnts ? cnts : null
    } 
    return null
}

def getOpenContacts(contacts) {
    if(contacts) {
        def cnts = contacts?.findAll { it?.currentContact == "open" }
        return cnts ? cnts : null
    } 
    return null
}

def setTstatMode(tstat, mode) {
    try {
        if(mode) {
            switch(mode) {
                case "auto":
                    tstat?.auto()
                    break
                case "heat":
                    tstat?.heat()
                    break
                case "cool":
                    tstat?.cool()
                    break
                case "off":
                    tstat?.off()
                    break
                default:
                    log.debug "setTstatMode() | Invalid LastMode received: ${mode}"
                    break
            }
            LogAction("${tstat.label} has been set to ${mode}...", "info", true)
            return true
        } else { 
            LogAction("setTstatMode() | mode was not found...", "error", true)
            return false
        }
    }
    catch (ex) { 
        LogAction("setTstatMode() Exception | ${ex}", "error", true) 
        return false
    }
}

def getLongTimeEnumLabel(val) {
    def result = "unknown"
    if(val) {
        longTimeEnum().each { item ->
            if(item?.key.toString() == val?.toString()) { 
                result = item?.value
            }
        }
    } 
    return result
}

def LogTrace(msg) { if(parent?.advAppDebug) { Logger(msg, "trace") } }

def LogAction(msg, type = "debug", showAlways = false) {
    try {
        if(showAlways) { Logger(msg, type) }
        else if (parent?.appDebug && !showAlways) { Logger(msg, type) }
    } catch (ex) { log.error("LogAction Exception: ${ex}") }
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


/******************************************************************************  
*                			Keep These Methods				                  *
*******************************************************************************/
def getAppImg(imgName, on = null) { return (!parent?.settings?.disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/App/$imgName" : "" }
                            
private debugStatus() { return state?.appDebug ? "On" : "Off" } //Keep this
private childDebugStatus() { return state?.childDebug ? "On" : "Off" } //Keep this
private isAppDebug() { return state?.appDebug ? true : false } //Keep This

def formatDt(dt) {
    def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
    if(location?.timeZone) { tf?.setTimeZone(location?.timeZone) }
    else {
        LogAction("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...", "warn", true, true)
    }
    return tf.format(dt)
}

//Returns time differences is seconds 
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

def daysOk(dayVals) {
    try {
        if(dayVals) {
            def day = new SimpleDateFormat("EEEE")
            if(location?.timeZone) { day.setTimeZone(location?.timeZone) }
            return dayVals.contains(day.format(new Date())) ? false : true
        } else { return true }
    } catch (ex) { LogAction("daysOk() Exception: ${ex}", "error", true, true) }
}

def modesOk(modeEntry) {
    def res = true
    if (modeEntry) {
        modeEntry?.each { m ->
            if(m.toString() == location?.mode.toString()) { res = false }
        }  
    }
    return res
}

def isInMode(modeList) {
    def res = false
    if (modeList) {
        modeList?.each { m ->
            if(m.toString() == location?.mode.toString()) { res = true }
        }  
    }
    return res
}

def time2Str(time) {
    if (time) {
        def t = timeToday(time, location?.timeZone)
        def f = new java.text.SimpleDateFormat("h:mm a")
        f?.setTimeZone(location.timeZone ?: timeZone(time))
        f?.format(t)
    }
}

def getDtNow() {
    def now = new Date()
    return formatDt(now)
}

/******************************************************************************  
*                Application Help and License Info Variables                  *
*******************************************************************************/
private def appName() 		{ "Nest Automations${appDevName()}" }
private def appAuthor() 	{ "Anthony S." }
private def appParent() 	{ "tonesto7:Nest Manager${appDevName()}" }
private def appNamespace() 	{ "tonesto7" }
private def gitBranch()     { "master" }
private def appDevType()    { false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
private def appInfoDesc() 	{ }
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
    return "Licensed under the Apache License, Version 2.0 (the 'License'); "+
        "you may not use this file except in compliance with the License. "+
        "You may obtain a copy of the License at"+
        "\n\n"+
        "    http://www.apache.org/licenses/LICENSE-2.0"+
        "\n\n"+
        "Unless required by applicable law or agreed to in writing, software "+
        "distributed under the License is distributed on an 'AS IS' BASIS, "+
        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. "+
        "See the License for the specific language governing permissions and "+
        "limitations under the License." 
}