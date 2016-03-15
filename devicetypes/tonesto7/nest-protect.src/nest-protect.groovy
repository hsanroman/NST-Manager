/**
 *  Nest Protect
 *	Authors: Anthony S. (@tonesto7), Ben W. (@desertblade)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.text.SimpleDateFormat 

preferences {
    preferences {
   	input (description: "Setting Operational Mode allows you to test different Nest Protects states. Once saved hit refresh in Device Handler",
   	 title: "Testing Mode", displayDuringSetup: true, type: "paragraph", element: "paragraph")
              input("testMode", "enum", title: "Testing State", 
              default: "normal",
              options: [
                "testSmoke":"Smoke Alert",
                "testCO": "CO Alert",
                "testWarnSmoke": "Smoke Warning",
                "testWarnCO": "CO Warning"])
    }
}

def devVer() { return "0.3.7" }

metadata {
	definition (name: "Nest Protect", author: "Anthony S.", namespace: "tonesto7") {
		capability "Polling"
		capability "Battery"
		capability "Smoke Detector"
		capability "Carbon Monoxide Detector"
        capability "Refresh"
        
		command "refresh"
        command "poll"
        command "log", ["string","string"]
        
        attribute "alarmState", "string"
        attribute "batteryState", "string"
        attribute "uiColor", "string"
        attribute "softwareVer", "string"
        attribute "lastConnection", "string"
        attribute "lastTested", "string"
        attribute "isTesting", "string"
        attribute "apiStatus", "string"
        attribute "debugOn", "string"
        attribute "onlineStatus", "string"
    }
	
    simulator {
        // TODO: define status and reply messages here
    }
            
    tiles(scale: 2) {
    	multiAttributeTile(name:"alarmState", type:"generic", width:6, height:4) {
  			tileAttribute("device.alarmState", key: "PRIMARY_CONTROL") {
    			attributeState("default", label:'--', icon: "st.unknown.unknown.unknown")
				attributeState("ok", label:"clear", icon:"st.alarm.smoke.clear", backgroundColor:"#44B621")
				attributeState("smoke-warning", label:"SMOKE!\nWARNING", icon:"st.alarm.smoke.smoke", backgroundColor:"#e8d813")
                attributeState("smoke-emergency", label:"SMOKE!", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
				attributeState("co-warning", label:"CO!\nWARNING!", icon:"st.alarm.carbon-monoxide.carbon-monoxide", backgroundColor:"#e8d813")
  				attributeState("co-emergency", label:"CO!", icon:"st.alarm.carbon-monoxide.carbon-monoxide", backgroundColor:"#e86d13")
  			}
  			tileAttribute("device.batteryState", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'unknown', icon: "st.unknown.unknown.unknown")
				attributeState("ok", label: "Battery: OK", backgroundColor: "#44B621")
				attributeState("low", label: "Battery: REPLACE!", backgroundColor: "#e86d13")
  			}
        }
		standardTile("smoke", "device.smoke", width: 2, height: 2) {
			state("default", label:'unknown', icon: "st.unknown.unknown.unknown")
			//state("ok", label:"Clear", icon:"st.alarm.smoke.clear", backgroundColor:"#44B621")
            //state("warning", label:"WARN!", icon:"st.alarm.smoke.smoke", backgroundColor:"#e8d813")
			//state("emergency", label:"SMOKE!", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
            state("ok", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_clear.png")
            state("warning", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_warn.png")
			state("detected", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/smoke_emergency.png")
		}
		standardTile("carbonMonoxide", "device.carbonMonoxide", width: 2, height: 2){
			state("default", label:'unknown', icon: "st.unknown.unknown.unknown")
			//state("ok", label:"Clear", icon:"st.particulate.particulate.particulate", backgroundColor:"#44B621")
            //state("warning", label:"WARN!", icon:"st.particulate.particulate.particulate", backgroundColor:"#e8d813")
			//state("emergency", label:"CO!", icon:"st.particulate.particulate.particulate", backgroundColor:"#e86d13")
            state("ok", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/co_clear.png")
            state("warning", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/co_warn.png")
			state("detected", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/co_emergency.png")
		}
 		standardTile("batteryState", "device.batteryState", width: 2, height: 2){
			state("default", label:'unknown')
			state("ok", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/battery_ok.png")
			state("replace", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/battery_low.png")
        }
        valueTile("onlineStatus", "device.onlineStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Network Status:\n${currentValue}')
            //state("on", label: 'Network Status:\n\Online', backgroundColor:"#44b621")
            //state("off", label: 'Network Status:\nOffline', backgroundColor:"#bc2323")
		}
        valueTile("uiColor", "device.uiColor", inactiveLabel: false, width: 2, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'UI Color:\n${currentValue}')
		}
        valueTile("softwareVer", "device.softwareVer", inactiveLabel: false, width: 2, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Firmware:\nv${currentValue}')
		}
		valueTile("lastConnection", "device.lastConnection", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Protect Last Checked-In:\n${currentValue}')
	    }
        valueTile("lastTested", "device.lastTested", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Last Manual Test:\n${currentValue}')
	    }
        standardTile("refresh", "device.refresh", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false, decoration: "flat") {
			state("default", label: 'refresh', action: "refresh.refresh", icon:"st.secondary.refresh-icon")
		}
        valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Data Last Received:\n${currentValue}')
	    }
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
			state("default", label: '${currentValue}')
		}
        valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, decoration: "flat", wordWrap: true) {
        	state "ok", label: "API Status:\nOK"
            state "issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
		}
        valueTile("debugOn", "device.debugOn", width: 2, height: 1, decoration: "flat") {
			state "true", 	label: 'Debug:\n${currentValue}'
            state "false", 	label: 'Debug:\n${currentValue}'
		}
        
	main "alarmState"
	details(["alarmState", "smoke", "carbonMonoxide", "batteryState" , "lastConnection", "lastTested","lastUpdatedDt", "devTypeVer",  "onlineStatus",  "apiStatus", "refresh", "softwareVer","debugOn"])
   }
}

// handle commands
def initialize() {
	log.info "Nest Protect ${textVersion()} ${textCopyright()}"
	poll()
}

def poll() {
	log.debug "polling parent..."
    parent.refresh()
}

def refresh() {
	log.debug "refreshing parent..."
   	switch (testMode) {
    	case "testSmoke" :
        	alarmStateEvent("", "emergency")
        break;
        
        case "testCO":
        	alarmStateEvent("emergency", "")
        break;
        
        case "testWarnSmoke" :
        	alarmStateEvent("", "warning")
        break;
        
        case "testWarnCO":
        	alarmStateEvent("warning", "")
        break;
        default:
			parent.refresh()
            
        log.warn "Test mode is active: nest alarm state data will not be received until it is turned off"
    }
    
}

def generateEvent(Map results)
{	
	state.testMode = testMode ? testMode : null
    Logger("Gen Event parsing data ${results}")
	if(results)
	{
        deviceVerEvent()
    	lastCheckinEvent(results?.last_connection)
        lastTestedEvent(results?.last_manual_test_time)
        apiStatusEvent(parent?.apiIssues())
        debugOnEvent(parent.settings?.childDebug)
    	onlineStatusEvent(results?.is_online.toString())
        batteryStateEvent(results?.battery_health.toString())
        carbonStateEvent(results?.co_alarm_state.toString())
        smokeStateEvent(results?.smoke_alarm_state.toString())
        if (!state.testMode) { alarmStateEvent(results?.co_alarm_state.toString(), results?.smoke_alarm_state.toString()) }
        uiColorEvent(results?.ui_color_state.toString())
        testingStateEvent(results?.is_manual_test_active.toString())
        softwareVerEvent(results?.software_version.toString())
    }
    lastUpdatedEvent()
}

def deviceVerEvent() {
	if (devVer()) {
    	def cur = parent?.latestProtVer().ver.toString()
    	def ver = (cur != devVer().toString()) ? "Device Type:\nv${devVer()}(Latest: v${cur})" : "Device Type:\nv${devVer()}(Current)"
    	sendEvent(name: 'devTypeVer', value: ver, displayed: false, isStateChange: true)
    }
}

def lastCheckinEvent(checkin) {
    def tf = new SimpleDateFormat("MMM d, yyyy - h:mm:ss a")
    	tf.setTimeZone(location?.timeZone)
   	def lastConn = "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}"
	def lastChk = device.currentState("lastConnection")?.value
    if(!lastChk.equals(lastConn?.toString())) {
        Logger("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
    	sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: true, isStateChange: true)
    } else { Logger("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
}

def lastTestedEvent(dt) {
    def lastTstVal = device.currentState("lastTested")?.value
    def tf = new SimpleDateFormat("MMM d,yyyy - h:mm:ss a")
    	tf.setTimeZone(location?.timeZone)
    def lastTest = !dt ? "No Test Recorded" : "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dt))}"
    if(!lastTstVal.equals(lastTest?.toString())) {
    	Logger("UPDATED | Last Manual Test was: (${lastTest}) | Original State: (${lastTstVal})")
    	sendEvent(name: 'lastTested', value: lastTest, displayed: true, isStateChange: true)
    } else { Logger("Last Manual Test was: (${lastTest}) | Original State: (${lastTstVal})") }
}

def softwareVerEvent(ver) {
    def verVal = device.currentState("softwareVer")?.value
    if(!verVal.equals(ver)) {
    	log.debug("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
        sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now ${ver}", displayed: true, isStateChange: true)
    } else { Logger("Firmware Version: (${ver}) | Original State: (${verVal})") }
}

def debugOnEvent(debug) {
	def val = device.currentState("debugOn")?.value
    def stateVal = debug ? "On" : "Off"
	if(!val.equals(stateVal)) {
    	log.debug("UPDATED | debugOn: (${stateVal}) | Original State: (${val})")
        sendEvent(name: 'debugOn', value: stateVal, displayed: false, isStateChange: true, state: stateVal)
   	} else { Logger("debugOn: (${stateVal}) | Original State: (${val})") }
}

def apiStatusEvent(issue) {
	def appIs = device.currentState("apiStatus")?.value
    def val = issue ? "issue" : "ok"
	if(!appIs.equals(val)) { 
        log.debug("UPDATED | API Status is: (${val}) | Original State: (${appIs})")
   		sendEvent(name: "apiStatus", value: val, descriptionText: "API Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("API Status is: (${val}) | Original State: (${appIs})") }
}

def lastUpdatedEvent() {
    def now = new Date()
    def tf = new SimpleDateFormat("MMM d, yyyy - h:mm:ss a")
    	tf.setTimeZone(location?.timeZone)
   	def lastDt = "${tf?.format(now)}"
	def lastUpd = device.currentState("lastUpdatedDt")?.value
    if(!lastUpd.equals(lastDt?.toString())) {
        Logger("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
    	sendEvent(name: 'lastUpdatedDt', value: lastDt?.toString(), displayed: false, isStateChange: true)
    }
}

def uiColorEvent(color) {
	def colorVal = device.currentState("uiColor")?.value
    if(!colorVal.equals(color)) {
		log.debug("UI Color is: (${color}) | Original State: (${colorVal})")
    	sendEvent(name:'uiColor', value: color.toString(), displayed: false, isStateChange: true) 
    } else { Logger("UI Color: (${color}) | Original State: (${colorVal})") }
}

def carbonStateEvent(carbon) {
	def carbonVal = device.currentState("carbonMonoxide")?.value
    if(!carbonVal.equals(carbon)) {
    	log.debug("CO State is: (${carbon}) | Original State: (${carbonVal})")
    	sendEvent(name:'carbonMonoxide', value: carbon, descriptionText: "CO State is: ${carbon}",  displayed: true, isStateChange: true) 
    } else { Logger("CO State: (${carbon}) | Original State: (${carbonVal})") }
}

def onlineStatusEvent(online) {
	def isOn = device.currentState("onlineStatus")?.value
    def val = online ? "Online" : "Offline"
	if(!isOn.equals(val)) { 
        log.debug("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
   		sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("Online Status is: (${val}) | Original State: (${isOn})") }
}

def batteryStateEvent(batt) {
	def battVal = device.currentState("batteryState")?.value
    if(!battVal.equals(batt)) {
		log.debug("Battery is: ${batt} | Original State: (${battVal})")
		sendEvent(name:'batteryState', value: batt, descriptionText: "Battery is: ${batt}", displayed: true, isStateChange: true)
	} else { Logger("Battery State: (${batt}) | Original State: (${battVal})") }
}

def smokeStateEvent(smoke) {
	def smokeVal = device.currentState("smoke")?.value
    if(!smokeVal.equals(smoke)) {
	log.debug("Smoke State is: ${smoke} | Original State: (${smokeVal})")
	sendEvent(name:'smoke', value: smoke,  descriptionText: "Smoke State is: ${smoke}", displayed: true, isStateChange: true)
    } else { Logger("Smoke State: (${smoke}) | Original State: (${smokeVal})") }
}

def testingStateEvent(test) {
	def testVal = device.currentState("isTesting")?.value
    if(!testVal.equals(test)) {
		log.debug("Testing State: (${test}) | Original State: (${testVal})")
    	//Not displaying the results of this, not sure if it is truly needed
	 	sendEvent(name:'isTesting', value: test, descriptionText: "Manual test: ${test}", displayed: true, isStateChange: true) 
     } else { Logger("Testing State: (${test}) | Original State: (${testVal})") }
}

 def alarmStateEvent(coState, smokeState) {
 	def alarmState = ""
    def dispAct = true
       
	if ( smokeState == "emergency" ) {
    	alarmState = "smoke-emergency"
        sendEvent( name: 'smoke', value: "detected", descriptionText: "Smoke Alarm: ${smokeState}", type: "physical", displayed: dispAct, isStateChange: true )      
    } else if (coState == "emergency" ) {
    	alarmState = "co-emergency"
   		sendEvent( name: 'carbonMonoxide', value: "detected", descriptionText: "CO Alarm: ${coState}", type: "physical", displayed: dispAct, isStateChange: true ) 
   	} else if (smokeState == "warning" ) {
    	alarmState = "smoke-warning"
 		sendEvent( name: 'smoke', value: "warning", descriptionText: "Smoke Alarm: ${smokeState}", type: "physical", displayed: dispAct, isStateChange: true )    
	} else if (coState == "warning" ) {
    	alarmState = "co-warning"
        sendEvent( name: 'carbonMonoxide', value: "warning", descriptionText: "CO Alarm: ${coState}", type: "physical", displayed: dispAct, isStateChange: true ) 
    } else {
    	alarmState = "ok"
        dispAct = !parent?.showProtAlarmStateEvts ? true : false
    } 
    
 	log.info "alarmState: ${alarmState} (Smoke: ${smokeState.toString().capitalize()} | CarbonMonoxide: ${coState.toString().capitalize()})"
 	sendEvent( name: 'alarmState', value: alarmState, descriptionText: "Alarm: ${alarmState} (Smoke/CO: ${smokeState}/${coState})", type: "physical", displayed: dispAct, isStateChange: true )
}
 
// Local Application Logging
def Logger(msg, logType = "debug") {
 	if(parent.settings?.childDebug) { 
    	switch (logType) {
        	case "trace":
        		log.trace "${msg}"
            	break;
    		case "debug":
        		log.debug "${msg}"
            	break
    		case "warn":
        		log.warn "${msg}"
            	break
    		case "error":
        		log.error "${msg}"
            	break
        	default:
        		log.debug "${msg}"
            	break;
        }
     }
 } 
// Print log message from parent
def log(message, level = "trace") {
	switch (level) {
    	case "trace":
        	log.trace "PARENT_Log>> " + message
            break;
    	case "debug":
        	log.debug "PARENT_Log>> " + message
            break
    	case "warn":
        	log.warn "PARENT_Log>> " + message
            break
    	case "error":
        	log.error "PARENT_Log>> " + message
            break
        default:
        	log.error "PARENT_Log>> " + message
            break;
    }            
    return null
}