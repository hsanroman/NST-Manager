/**
 *  Nest Presence
 *	Author: Ben W. (@desertBlade)
 *	Author: Anthony S. (@tonesto7)
 *  
 *
 
 * Copyright (C) 2016 Ben W, Anthony S.
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

// TODO: Need to update Copyright

import java.text.SimpleDateFormat

preferences {  }

def devVer() { return "1.0.1" }

// for the UI
metadata {
	definition (name: "Nest Presence", namespace: "tonesto7", author: "DesertBlade") {

        capability "Presence Sensor"
        capability "Sensor"
  		capability "Refresh"
        
		command "setAway"
        command "setHome"
        command "setPresMode"

		command "setPresence"
        command "refresh"
		command "log"
		command "updateData"
        command "updatePresence"
        
        attribute "lastConnection", "string"
        attribute "apiStatus", "string"
        attribute "debugOn", "string"
	}

	simulator {
		status "present": "presence: 1"
		status "not present": "presence: 0"
	}

	tiles(scale: 2) {
		standardTile("presence", "device.presence", width: 4, height: 4, canChangeBackground: true) {
			state("present", 	labelIcon:"st.presence.tile.mobile-present", 	backgroundColor:"#53a7c0", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/nest_dev_pres_icon.png")
			state("not present",labelIcon:"st.presence.tile.mobile-not-present",backgroundColor:"#ebeef2", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/nest_dev_away_icon.png")
		}
		standardTile("nestPresence", "device.nestPresence", width:2, height:2, decoration: "flat") {
			state "present",	action: "setPresMode",	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_home_icon.png"
            state "away", 		action: "setPresMode", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_away_icon.png"
            state "auto-away", 	action: "setPresMode", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_autoaway_icon.png"
        	state "unknown", action: "setPresMode", 	icon: "st.unknown.unknown.unknown"
		}
        valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Data Last Received:\n${currentValue}')
	    }
        valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, decoration: "flat", wordWrap: true) {
        	state "ok", label: "API Status:\nOK"
            state "issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
		}
        standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main ("presence")
		details ("presence", "nestPresence", "refresh")
	}
}

def initialize() {
	log.debug "initialize"
}

def parse(String description) {
	log.debug "Parsing '${description}'"
}

def configure() {
	
}

def poll() {
	log.debug "Polling parent..."
    parent.refresh()
}

def refresh() {
	parent.refresh()
}

def generateEvent(Map results) {
	//Logger("generateEvents Parsing data ${results}")
  	Logger("-------------------------------------------------------------------", "warn")
	if(!results) {
    	apiStatusEvent(parent?.apiIssues())
    	debugOnEvent(parent.settings?.childDebug)
    	presenceEvent(parent?.locationPresence())
    	lastUpdatedEvent()
   	}
 	return null
}

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def debugOnEvent(debug) {
	def val = device.currentState("debugOn")?.value
    def stateVal = debug ? "On" : "Off"
	if(!val.equals(stateVal)) {
    	log.debug("UPDATED | debugOn: (${stateVal}) | Original State: (${val})")
        sendEvent(name: 'debugOn', value: stateVal, displayed: false, isStateChange: true, state: stateVal)
   	} else { Logger("debugOn: (${stateVal}) | Original State: (${val})") }
}

def lastUpdatedEvent() {
    def now = new Date()
    def formatVal = state.use24Time ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
    def tf = new SimpleDateFormat(formatVal)
    	tf.setTimeZone(location?.timeZone)
   	def lastDt = "${tf?.format(now)}"
	def lastUpd = device.currentState("lastUpdatedDt")?.value
    if(!lastUpd.equals(lastDt?.toString())) {
        Logger("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
    	sendEvent(name: 'lastUpdatedDt', value: lastDt?.toString(), displayed: false, isStateChange: true)
    }
}

def presenceEvent(presence) {
	def val = getPresence()
	def pres = (presence == "home") ? "present" : "not present"
    def nestPres = (presence == "home") ? "present" : (presence == "auto-away") ? "auto-away" : "away" 
    if(!val.equals(pres)) {
        log.debug("UPDATED | Presence: ${pres} | Original State: ${val} | State Variable: ${state?.present}")
   		sendEvent(name: 'nestPresence', value: nestPres, descriptionText: "Nest Presence is: ${nestPres}", displayed: true, isStateChange: true )
		sendEvent(name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: true, isStateChange: true, state: pres )
   		state?.present = (pres == "present") ? true : false
    } else { Logger("Presence - Present: (${pres}) | Original State: (${val}) | State Variable: ${state?.present}") }
}

def apiStatusEvent(issue) {
	def appIs = device.currentState("apiStatus")?.value
    def val = issue ? "issue" : "ok"
	if(!appIs.equals(val)) { 
        log.debug("UPDATED | API Status is: (${val}) | Original State: (${appIs})")
   		sendEvent(name: "apiStatus", value: val, descriptionText: "API Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("API Status is: (${val}) | Original State: (${appIs})") }
}

def getHvacMode() { 
	try { return device.currentState("thermostatMode")?.value.toString() } 
	catch (e) { return "unknown" }
}

def getNestPresence() { 
	try { return device.currentValue("nestPresence").value.toString() } 
	catch (e) { return "present" }
}

def getPresence() { 
	try { return device.currentValue("presence").value.toString() } 
	catch (e) { return "present" }
}

/************************************************************************************************
|							Sends Commands to Manager Application								|
*************************************************************************************************/
def setPresMode() {
	log.trace "setAway()"
    def pres = getNestPresence()
    log.trace "${pres}"
    if(pres == "auto-away" || pres == "away") {
    	presenceEvent("home") 
		parent.setStructureAway(this, "false")
        presenceEvent("home") 
    }
    else if (pres == "present") {
    	presenceEvent("away") 
        parent.setStructureAway(this, "true")
        presenceEvent("away")
    }
}

// backward compatibility for previous nest thermostat (and rule machine)
def away() {
    log.trace "away()..."
    setAway()
}

// backward compatibility for previous nest thermostat (and rule machine)
def present() {
    log.trace "present()..."
    setHome()
}

def setAway() {
	Logger("setAway Command Received")
	presenceEvent("away") 
    parent.setStructureAway(this, "true")
    presenceEvent("away")
}

def setHome() {
	Logger("setHome Command Received")
	presenceEvent("home") 
    parent.setStructureAway(this, "false")
    presenceEvent("home")
}

// Local Device Logging
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
 
 //This will Print logs from the parent app when added to parent method that the child calls
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
    return null // always child interface call with a return value
}