/**
 *  Nest Thermostat
 *	Author: Anthony S. (@tonesto7)
 *  Author: Ben W. (@desertBlade)
 *	Contributor: Eric S. (@E_Sch)
 
 * Based off of the EcoBee thermostat under Templates in the IDE 
 * Copyright (C) 2016 Anthony S., Ben W.
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

def devVer() { return "1.0.3" }

// for the UI
metadata {
	definition (name: "Nest Thermostat", namespace: "tonesto7", author: "Anthony S.") {
		capability "Actuator"
        capability "Polling"
		capability "Relative Humidity Measurement"
        capability "Refresh"
        capability "Sensor"
		capability "Thermostat"
        capability "Thermostat Cooling Setpoint"
		capability "Thermostat Fan Mode"
		capability "Thermostat Heating Setpoint"
		capability "Thermostat Mode"
		capability "Thermostat Operating State"
		capability "Thermostat Setpoint"
		capability "Temperature Measurement"
		
        command "refresh"
        command "poll"
        
        command "away"
        command "present"
		command "setAway"
        command "setHome"
        command "setPresMode"
        command "setFanMode"
        command "setThermostatMode"
        command "setTemperature"
        command "temperatureUp"
        command "temperatureDown"
		command "log"
		command "heatingSetpointUp"
		command "heatingSetpointDown"
		command "coolingSetpointUp"
		command "coolingSetpointDown"

		attribute "temperatureUnit", "string"
        attribute "targetTemp", "string"
        attribute "thermostatMode", "string"
        attribute "softwareVer", "string"
        attribute "lastConnection", "string"
        attribute "apiStatus", "string"
        attribute "hasLeaf", "string"
        attribute "debugOn", "string"
        attribute "onlineStatus", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}
                    
    tiles(scale: 2) {
		multiAttributeTile(name:"temperature", type:"thermostat", width:6, height:4, canChangeIcon: true) {
  			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
    			attributeState("default", label:'${currentValue}°')
  			}
  			tileAttribute("device.temperature", key: "VALUE_CONTROL") {
    			//attributeState("default", action: "setTemperature")
                attributeState("VALUE_UP", action: "temperatureUp")
 				attributeState("VALUE_DOWN", action: "temperatureDown")
  			}
  			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'${currentValue}%', unit:"%")
  			}
  			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
    			attributeState("idle",backgroundColor:"#44b621")
    			attributeState("heating",backgroundColor:"#ffa81e")
    			attributeState("cooling", 		backgroundColor:"#269bd2")
                attributeState("fan-only",		backgroundColor:"#2ABBF0")
                attributeState("pending-heat",	backgroundColor:"#2ABBF0")
                attributeState("pending-cool",	backgroundColor:"#2ABBF0")
  			}
  			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
    			attributeState("off", label:'${name}')
    			attributeState("heat", label:'${name}')
    			attributeState("cool", label:'${name}')
    			attributeState("auto", label:'${name}')
                attributeState("emergencyHeat", label:'${name}')
  			}
            
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
    			attributeState("default", label:'${currentValue}')
  			}

  			tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
    			attributeState("default", label:'${currentValue}')
  			}
        }
        standardTile("temp2", "device.temperature", width: 2, height: 2, decoration: "flat") {
        	state("default", label:'${currentValue}°', 	icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_like.png")
        }
        standardTile("mode2", "device.thermostatMode", width: 2, height: 2, decoration: "flat") {
	        state("off",  icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_off_icon.png")
			state("heat", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_heat_icon.png")
            state("cool", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_cool_icon.png")
            state("auto", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_heat_cool_icon.png")
        }
        standardTile("thermostatMode", "device.thermostatMode", width:2, height:2, decoration: "flat") {
			//state("off", 	action:"thermostat.heat", 	nextState: "heat", 	icon: "st.thermostat.heating-cooling-off")
			//state("heat", 	action:"thermostat.cool", 	nextState: "cool", 	icon: "st.thermostat.heat")
            //state("cool", 	action:"thermostat.auto", 	nextState: "auto", 	icon: "st.thermostat.cool")
            //state("auto", 	action:"thermostat.off", 	nextState: "off", 	icon: "st.thermostat.auto")
            state("off", 	action:"thermostat.heat", 	nextState: "heat", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_off.png")
			state("heat", 	action:"thermostat.cool", 	nextState: "cool", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_heat.png")
            state("cool", 	action:"thermostat.auto", 	nextState: "auto", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_cool.png")
            state("auto", 	action:"thermostat.off", 	nextState: "off", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/hvac_auto.png")
            state("emergency heat", action:"thermostat.heat", nextState: "heat", icon: "st.thermostat.emergency")
		}
       standardTile("thermostatFanMode", "device.thermostatFanMode", width:2, height:2, decoration: "flat") {
       		//state "auto",action:"fanOn", icon: "st.thermostat.fan-auto"
            //state "on",action:"fanAuto", icon: "st.thermostat.fan-on"
			state "auto",	action:"fanOn", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_auto_icon.png"
            state "on",		action:"fanAuto", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_on_icon.png"
		}
		standardTile("nestPresence", "device.nestPresence", width:2, height:2, decoration: "flat") {
        	//state "present", 	label:'home', 		action: "setPresMode",	icon: "st.Home.home2"
			//state "away", 		label:'away', 		action: "setPresMode", 	icon: "st.Transportation.transportation5"
            //state "auto-away", 	label:'auto\naway', action: "setPresMode", 	icon: "st.Transportation.transportation5"
			state "present", 	action: "setPresMode",	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_home_icon.png"
			state "away", 		action: "setPresMode", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_away_icon.png"
            state "auto-away", 	action: "setPresMode", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_autoaway_icon.png"
        	state "unknown",	action: "setPresMode", 	icon: "st.unknown.unknown.unknown"
		}
		standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
        valueTile("softwareVer", "device.softwareVer", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Firmware:\nv${currentValue}')
		}
        
		valueTile("hasLeaf", "device.hasLeaf", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Leaf:\n${currentValue}')
		}
        valueTile("onlineStatus", "device.onlineStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Network Status:\n${currentValue}')
            //state("on", label: 'Network Status:\n\Online', backgroundColor:"#44b621")
            //state("off", label: 'Network Status:\nOffline', backgroundColor:"#bc2323")
		}
        valueTile("debugOn", "device.debugOn", width: 2, height: 1, decoration: "flat") {
			state "true", 	label: 'Debug:\n${currentValue}'
            state "false", 	label: 'Debug:\n${currentValue}'
		}
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
			state("default", label: '${currentValue}')
		}
        standardTile("heatingSetpointUp", "device.heatingSetpoint", width: 1, height: 1, canChangeIcon: false,  decoration: "flat") {
			state "heatingSetpointUp", label:'  ', action:"heatingSetpointUp", icon:"st.thermostat.thermostat-up", backgroundColor:"#bc2323"
		}
        
        valueTile("heatingSetpoint", "device.heatingSetpoint", width: 1, height: 1, canChangeIcon: false) {
			state "default", label:'${currentValue}', unit:"Heat", backgroundColor:"#bc2323"
		}
        
        valueTile("coolingSetpoint", "device.coolingSetpoint", width: 1, height: 1, canChangeIcon: false) {
			state "default", label:'${currentValue}', unit:"Cool", backgroundColor:"#1e9cbb"
		}

		standardTile("heatingSetpointDown", "device.heatingSetpoint",  width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
			state "heatingSetpointDown", label:'  ', action:"heatingSetpointDown", icon:"st.thermostat.thermostat-down", backgroundColor:"#bc2323"
		}
        
        standardTile("coolingSetpointUp", "device.coolingSetpoint", width: 1, height: 1,canChangeIcon: false, decoration: "flat") {
			state "coolingSetpointUp", label:'  ', action:"coolingSetpointUp", icon:"st.thermostat.thermostat-up", backgroundColor:"#1e9cbb"
		}

		standardTile("coolingSetpointDown", "device.coolingSetpoint", width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
			state "coolingSetpointDown", label:'  ', action:"coolingSetpointDown", icon:"st.thermostat.thermostat-down", backgroundColor:"#1e9cbb"
		}
        
        valueTile("lastConnection", "device.lastConnection", width: 4, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Nest Last Checked-In:\n${currentValue}')
	    }
        valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Data Last Received:\n${currentValue}')
	    }
        valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
        	state "ok", label: "API Status:\nOK"
            state "issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
		}
		main( tileMain() )
		details( tileSelect() )
	}
}

def tileMain() { 
    return ["temp2"]
}

def tileSelect() { 
	//log.debug "hvacMode: ${getHvacMode()}"
	if(getHvacMode() == "heat" || getHvacMode() == "cool") {
    	//log.debug "tileSelect if | hvacMode: ${getHvacMode()}"
        return ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "onlineStatus", "apiStatus", "softwareVer", "hasLeaf", "lastConnection", "refresh", 
        		"lastUpdatedDt", "softwareVer", "debugOn", "devTypeVer"] 
    } 
    else if(getHvacMode() == "auto" || getHvacMode() == "unknown") { 
    	//log.debug "tileSelect else if | hvacMode: ${getHvacMode()}"
    	return ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "heatingSetpointDown", "heatingSetpoint", "heatingSetpointUp", 
        		"coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "onlineStatus", "apiStatus", "hasLeaf", "lastConnection", "refresh", 
                "lastUpdatedDt", "softwareVer", "debugOn", "devTypeVer"]
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
	if(results)
	{
        //atomicState?.currentData = results
        state.use24Time = !parent?.settings?.use24Time ? false : true
        deviceVerEvent()
        apiStatusEvent(parent?.apiIssues())
        debugOnEvent(parent.settings?.childDebug)
        presenceEvent(parent?.locationPresence())
		tempUnitEvent(results?.temperature_scale)
		canHeatCool(results?.can_heat, results?.can_cool)
        hasFan(results?.has_fan.toString()) 
        hvacModeEvent(results?.hvac_mode.toString())
        state?.hvac_mode = results?.hvac_mode.toString()
        lastCheckinEvent(results?.last_connection)
        softwareVerEvent(results?.software_version.toString())
        onlineStatusEvent(results?.is_online.toString())
        hasLeafEvent(results?.has_leaf)
        humidityEvent(results?.humidity.toString())
        operatingStateEvent(results?.hvac_state.toString())
 		fanModeEvent(results?.fan_timer_active.toString())
       
        def heatingSetpoint = '--'
		def coolingSetpoint = '--'
		def hvacMode = results?.hvac_mode
		def tempUnit = device.latestValue('temperatureUnit')
		switch (tempUnit) {
			case "C":
				def temp = Math.round(results?.ambient_temperature_c)
				def targetTemp = Math.round(results?.target_temperature_c)

				if (hvacMode == "cool") { coolingSetpoint = targetTemp } 
                else if (hvacMode == "heat") { heatingSetpoint = targetTemp } 
                else if (hvacMode == "heat-cool") {
					coolingSetpoint = Math.round(results?.target_temperature_high_c)
					heatingSetpoint = Math.round(results?.target_temperature_low_c)
				}
				if (!state?.present) {
					if (results?.away_temperature_high_c) { coolingSetpoint = Math.round(results?.away_temperature_high_c) }
					if (results?.away_temperature_low_c) { heatingSetpoint = Math.round(results?.away_temperature_low_c) }
				}
                
                //Logger("heatingSetpointC: ${heatingSetpoint} | coolingSetpointC: ${coolingSetpoint}")
                temperatureEvent(temp)
                targetTempEvent(targetTemp)
				coolingSetpointEvent(coolingSetpoint)
				heatingSetpointEvent(heatingSetpoint)
				break
                
			default:
				def temp = Math.round(results?.ambient_temperature_f)
				def targetTemp = Math.round(results?.target_temperature_f)
				if (hvacMode == "cool") { coolingSetpoint = targetTemp } 
                else if (hvacMode == "heat") { heatingSetpoint = targetTemp } 
                else if (hvacMode == "heat-cool") {
					coolingSetpoint = Math.round(results?.target_temperature_high_f)
					heatingSetpoint = Math.round(results?.target_temperature_low_f)
				}
				if (!state?.present) {
					if (results?.away_temperature_high_f) 	{ coolingSetpoint = Math.round(results?.away_temperature_high_f) }
					if (results?.away_temperature_low_f)	{ heatingSetpoint = Math.round(results?.away_temperature_low_f) }
				}
				//Logger("heatingSetpointF: ${heatingSetpoint} | coolingSetpointF: ${coolingSetpoint}")
				temperatureEvent(temp)
                targetTempEvent(targetTemp)
				coolingSetpointEvent(coolingSetpoint)
				heatingSetpointEvent(heatingSetpoint)
				break
			}
	}
    lastUpdatedEvent()
    return null
}

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def deviceVerEvent() {
	if (devVer()) {
    	def cur = parent?.latestTstatVer().ver.toString()
    	def ver = (cur != devVer().toString()) ? "Device Type:\nv${devVer()}(Latest: v${cur})" : "Device Type:\nv${devVer()}(Current)"
    	sendEvent(name: 'devTypeVer', value: ver, displayed: false, isStateChange: true)
    }
}

def debugOnEvent(debug) {
	def val = device.currentState("debugOn")?.value
    def stateVal = debug ? "On" : "Off"
	if(!val.equals(stateVal)) {
    	log.debug("UPDATED | debugOn: (${stateVal}) | Original State: (${val})")
        sendEvent(name: 'debugOn', value: stateVal, displayed: false, isStateChange: true, state: stateVal)
   	} else { Logger("debugOn: (${stateVal}) | Original State: (${val})") }
}

def lastCheckinEvent(checkin) {
	//log.trace "lastCheckinEvent()..."
    def formatVal = state.use24Time ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
    def tf = new SimpleDateFormat(formatVal)
    	tf.setTimeZone(location?.timeZone)
   	def lastConn = "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}"
	def lastChk = device.currentState("lastConnection")?.value
    if(!lastChk.equals(lastConn?.toString())) {
        log.debug("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
    	sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: false, isStateChange: true)
    } else { Logger("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
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

def softwareVerEvent(ver) {
    def verVal = device.currentState("softwareVer")?.value
    if(!verVal.equals(ver)) {
    	log.debug("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
        sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now ${ver}", displayed: false, isStateChange: true)
    } else { Logger("Firmware Version: (${ver}) | Original State: (${verVal})") }
}

def tempUnitEvent(unit) {
	def tmpUnit = device.currentState("temperatureUnit")?.value
	if(!tmpUnit.equals(unit)) {   
    	log.debug("UPDATED | Temperature Unit: (${unit}) | Original State: (${tmpUnit})")
        sendEvent(name:'temperatureUnit', value: unit, descriptionText: "Temperature Unit is now: '${unit}'", displayed: false, isStateChange: true)
        state?.tempUnit = unit
    } else { Logger("Temperature Unit: (${unit}) | Original State: (${tmpUnit})") }
}

def targetTempEvent(targetTemp) {
	def temp = device.currentState("targetTemperature")?.value.toString()
	if(!temp.equals(targetTemp.toString())) {
		log.debug("UPDATED | Target Temperature is (${targetTemp}) | Original Temp: (${temp})")
		sendEvent(name:'targetTemperature', value: targetTemp, descriptionText: "Target Temperature is ${targetTemp}", displayed: true, isStateChange: true)
	} else { Logger("Target Temperature is (${targetTemp}) | Original Temp: (${temp})") }
}

def temperatureEvent(tempVal) {
	def temp = device.currentState("temperature")?.value.toString()
	if(!temp.equals(tempVal.toString())) {
        log.debug("UPDATED | Temperature is (${tempVal}) | Original Temp: (${temp})")
    	sendEvent(name:'temperature', value: tempVal,  descriptionText: "Ambient Temperature is ${tempVal}" , displayed: true, isStateChange: true)
    } else { Logger("Temperature is (${tempVal}) | Original Temp: (${temp})") }
}

def heatingSetpointEvent(tempVal) {
	def temp = device.currentState("heatingSetpoint")?.value.toString()
    if(!temp.equals(tempVal.toString())) {
        log.debug("UPDATED | HeatingSetpoint is (${tempVal}) | Original Temp: (${temp})")
    	sendEvent(name:'heatingSetpoint', value: tempVal, unit: state?.tempUnit, descriptionText: "Heat Setpoint is ${tempVal}" , displayed: true, isStateChange: true, state: "heat")
    } else { Logger("HeatingSetpoint is (${tempVal}) | Original Temp: (${temp})") }
}

def coolingSetpointEvent(tempVal) {
	def temp = device.currentState("coolingSetpoint")?.value.toString()
	if(!temp.equals(tempVal.toString())) {
        log.debug("UPDATED | CoolingSetpoint is (${tempVal}) | Original Temp: (${temp})")
    	sendEvent(name:'coolingSetpoint', value: tempVal, unit: state?.tempUnit, descriptionText: "Cool Setpoint is ${tempVal}" , displayed: true, isStateChange: true, state: "cool")
    } else { Logger("CoolingSetpoint is (${tempVal}) | Original Temp: (${temp})") }
}

def hasLeafEvent(Boolean hasLeaf) {
	def leaf = device.currentState("hasLeaf")?.value
    def lf = hasLeaf ? "On" : "Off"
	if(!leaf.equals(lf)) {
        log.debug("UPDATED | Leaf is set to (${lf}) | Original State: (${leaf})")
		sendEvent(name:'hasLeaf', value: lf,  descriptionText: "Leaf: ${lf}" , displayed: false, isStateChange: true, state: lf)
    } else { Logger("Leaf is set to (${lf}) | Original State: (${leaf})") }
}

def humidityEvent(humidity) {
	def hum = device.currentState("humidity")?.value
	if(!hum.equals(humidity)) {
        log.debug("UPDATED | Humidity is (${humidity}) | Original State: (${hum})")
		sendEvent(name:'humidity', value: humidity, unit: "%", descriptionText: "Humidity is ${humidity}" , displayed: true, isStateChange: true)
    } else { Logger("Humidity is (${humidity}) | Original State: (${hum})") }
}

def presenceEvent(presence) {
	def val = device.currentState("presence")?.value
	def pres = (presence == "home") ? "present" : "not present"
    def nestPres = (presence == "home") ? "present" : (presence == "auto-away") ? "auto-away" : "away" 
    if(!val.equals(pres)) {
        log.debug("UPDATED | Presence: ${pres} | Original State: ${val} | State Variable: ${state?.present}")
   		sendEvent(name: 'nestPresence', value: nestPres, descriptionText: "Nest Presence is: ${nestPres}", displayed: true, isStateChange: true )
		sendEvent(name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: true, isStateChange: true, state: pres )
   		state?.present = (pres == "present") ? true : false
    } else { Logger("Presence - Present: (${pres}) | Original State: (${val}) | State Variable: ${state?.present}") }
}

def hvacModeEvent(mode) {
	def pres = getNestPresence()
	def hvacMode = getHvacMode()
    def newMode = !parent?.showAwayAsAuto ? mode : (( mode == "heat-cool" || ((pres == "away" || pres == "auto-away") && (mode == "heat" || mode == "cool"))) ? "auto" : mode)
	if(!hvacMode.equals(newMode)) {
		log.debug("UPDATED | Hvac Mode is (${newMode}) | Original State: (${hvacMode})")
   		sendEvent(name: "thermostatMode", value: newMode, descriptionText: "HVAC mode is ${newMode} mode", displayed: true, isStateChange: true)
   	} else { Logger("Hvac Mode is (${newMode}) | Original State: (${hvacMode})") }
} 

def fanModeEvent(fanActive) {
	def val = (fanActive == "true") ? "on" : "auto"
	def fanMode = device.currentState("thermostatFanMode")?.value
	if(!fanMode.equals(val)) {
		log.debug("UPDATED | Fan Mode: (${val}) | Original State: (${fanMode})")
        if(state?.has_fan == "true") {
            sendEvent(name: "thermostatFanMode", value: val, descriptionText: "Fan Mode is: ${val}", displayed: true, isStateChange: true, state: val)
        }
    } else { Logger("Fan Active: (${val}) | Original State: (${fanMode})") }
}

def operatingStateEvent(operatingState) {
	def hvacState = device.currentState("thermostatOperatingState")?.value
	def operState = (operatingState == "off") ? "idle" : operatingState
    if(!hvacState.equals(operState)) {
    	log.debug("UPDATED | OperatingState is (${operState}) | Original State: (${hvacState})")
		sendEvent(name: 'thermostatOperatingState', value: operState, descriptionText: "Device is ${operState}", displayed: true, isStateChange: true)
    } else { Logger("OperatingState is (${operState}) | Original State: (${hvacState})") }
}

def onlineStatusEvent(online) {
	def isOn = device.currentState("onlineStatus")?.value
    def val = online ? "Online" : "Offline"
	if(!isOn.equals(val)) { 
        log.debug("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
   		sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("Online Status is: (${val}) | Original State: (${isOn})") }
}

def apiStatusEvent(issue) {
	def appIs = device.currentState("apiStatus")?.value
    def val = issue ? "issue" : "ok"
	if(!appIs.equals(val)) { 
        log.debug("UPDATED | API Status is: (${val}) | Original State: (${appIs})")
   		sendEvent(name: "apiStatus", value: val, descriptionText: "API Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("API Status is: (${val}) | Original State: (${appIs})") }
}

def canHeatCool(canHeat, canCool) {
    if(canHeat) { state?.can_heat = canHeat }
    if(canCool) { state?.can_cool = canCool }
}

def hasFan(hasFan) {
	if(hasFan) { state?.has_fan = hasFan }
}	

def isEmergencyHeat(val) {
	if(val) { state?.is_using_emergency_heat = val }
}

def clearHeatingSetpoint() {
    sendEvent(name:'heatingSetpoint', value: "",  descriptionText: "Clear Heating Setpoint" , display: false, displayed: false, isStateChange: true)
	state?.heating_setpoint = ""
}

def clearCoolingSetpoint() {
    sendEvent(name:'coolingSetpoint', value: "",  descriptionText: "Clear Cooling Setpoint" , display: false, displayed: false, isStateChange: true)
    state?.cooling_setpoint = ""
}

def getCoolTemp() { 
	try { return device.currentValue("coolingSetpoint").toInteger() } 
	catch (e) { return 0 }
}

def getHeatTemp() { 
	try { return device.currentValue("heatingSetpoint").toInteger() } 
	catch (e) { return 0 }
}

def getHvacMode() { 
	try { return device.currentState("thermostatMode")?.value.toString() } 
	catch (e) { return "unknown" }
}

def getNestPresence() { 
	try { return device.currentState("nestPresence").value.toString() } 
	catch (e) { return "present" }
}

def getPresence() { 
	try { return device.currentState("presence").value.toString() }
  	catch (e) { return "present" }
}

def getTargetTemp() { 
	try { return device.currentValue("targetTemperature").toInteger() } 
	catch (e) { return 0 }
}

def getTemp() { 
	try { return device.currentValue("temperature").toInteger() } 
	catch (e) { return 0 }
}

def tempWaitVal() {
	return parent?.getChildWaitVal() ? parent?.getChildWaitVal().toInteger() : 4
}
/************************************************************************************************
|					Below this are Temperature Setpoint Functions for Buttons					|
*************************************************************************************************/
def heatingSetpointUp() {
	def operMode = getHvacMode()
    def md = "heat"
	if ( operMode == "heat" || operMode == "auto" ) {
    	 def newTemp = getHeatTemp() + 1
         heatingSetpointEvent(newTemp)
         runIn(tempWaitVal(), "changeSetpoint", [data: [temp: newTemp, mode: md], overwrite: true])	
	}
}

def heatingSetpointDown() {
	def operMode = getHvacMode()
    def md = "heat"
	if ( operMode == "heat" || operMode == "auto" ) {
    	def newTemp = getHeatTemp() - 1
    	heatingSetpointEvent(newTemp)
    	runIn(tempWaitVal(), "changeSetpoint", [data: [temp: newTemp, mode: md], overwrite: true])		
	}
}

def coolingSetpointUp() {
	def operMode = getHvacMode()
    def md = "cool"
	if ( operMode == "cool" || operMode == "auto" ) {
    	def newTemp = getCoolTemp() + 1
    	coolingSetpointEvent(newTemp)
        runIn(tempWaitVal(), "changeSetpoint", [data: [temp: newTemp, mode: md], overwrite: true])		
	}
}

def coolingSetpointDown() {
	def operMode = getHvacMode()
    def md = "cool"
	if ( operMode == "cool" || operMode == "auto" ) {
    	def newTemp = getCoolTemp() - 1
    	coolingSetpointEvent(newTemp)
		runIn(tempWaitVal(), "changeSetpoint", [data: [temp: newTemp, mode: md], overwrite: true])		
	}
}

def setTemperature(temp) {
	log.trace "setTemperature()..."
	def hvacMode = getHvacMode()
	def curTemp = getTemp()
	def targetTemp = getTargetTemp()
	switch (hvacMode) {
    	case ["auto", "cool", "heat"]:
        	//log.debug "setTemperature: targetTemp: ${targetTemp} | mode: ${hvacMode}"
        	(temp < targetTemp) ? temperatureDown() : temperatureUp()
            break

		default:
        	log.error "setTemperature Error: mode wasn't properly detected"
        	break
    }
}

def temperatureUp() {
	def hvacMode = getHvacMode()
    def curTemp = getTemp().toInteger()
    def targetTemp = getTargetTemp().toInteger()
    def curHTemp = getHeatTemp().toInteger()
    def curCTemp = getCoolTemp().toInteger()
	def newTemp
	switch (hvacMode) {
    	case "heat":
        	newTemp = curHTemp + 1
    		Logger("Sending changeSetpoint(Temp: ${newTemp})") 
            heatingSetpointEvent(newTemp)
        	runIn( tempWaitVal(), "changeSetpoint", [data: [temp:newTemp], overwrite: true] )
            break

		case "cool":
        	newTemp = curHTemp + 1
    		Logger("Sending changeSetpoint(Temp: ${newTemp})") 
            coolingSetpointEvent(newTemp)
        	runIn( tempWaitVal(), "changeSetpoint", [data: [temp: newTemp], overwrite: true] )
            break
            
         case "auto":
          	log.warn "auto mode temp change is not supported yet."
        	break
        default:
        	break
    }
}

def temperatureDown() {
	def hvacMode = getHvacMode()
    def curTemp = getTemp().toInteger()
    def targetTemp = getTargetTemp().toInteger()
    def curHTemp = getHeatTemp().toInteger()
    def curCTemp = getCoolTemp().toInteger()
	def newTemp
	switch (hvacMode) {
    	case "heat":
        	newTemp = curHTemp - 1
    		Logger("Sending changeSetpoint(Temp: ${newTemp})") 
            heatingSetpointEvent(newTemp)
        	runIn( tempWaitVal(), "changeSetpoint", [data: [temp:newTemp], overwrite: true] )
            break

		case "cool":
        	newTemp = curHTemp - 1
    		Logger("Sending changeSetpoint(Temp: ${newTemp})") 
            coolingSetpointEvent(newTemp)
        	runIn( tempWaitVal(), "changeSetpoint", [data: [temp: newTemp], overwrite: true] )
            break
            
         case "auto":
          	log.warn "auto mode temp change is not supported yet."
        	break
        default:
        	break
    }
}

def changeSetpoint(val) {
	def hvacMode = getHvacMode()
    def temp = val?.temp?.value.toInteger()
    def md = !val?.mode.value ? null : val?.mode.value
    switch (hvacMode) {
    	case "heat":
           	setHeatingSetpoint(temp)
            break

		case "cool":
        	setCoolingSetpoint(temp)
            break
         case "auto":
         	if(md) {
	       		if("${md}" == "heat") { setHeatingSetpoint(temp) }
           		else if ("${md}" == "cool") { setCoolingSetpoint(temp) }
                else { log.warn "Invalid Temp Type received..." }
            }
        	break
        default:
        	break
    }
}

// handle commands
def setHeatingSetpoint(temp) {
	log.trace "setHeatingSetpoint()..."
	def hvacMode = getHvacMode()
    def pres = getNestPresence()
	def tempUnit = state?.tempUnit
    def canHeat = state?.can_heat.toBoolean()
	def result = false
    
    if (state?.present && canHeat) {
		switch (tempUnit) {
			case "C":
				if (temp) {
					if (temp < 9) { temp = 9 }
					if (temp > 32) { temp = 32 }
                    
                    log.debug "Heat Temp Received: ${temp} (${tempUnit})"
					if (hvacMode == 'auto') {
                    	heatingSetpointEvent(temp)
						parent.setTargetTempLow(this, tempUnit, temp)
                        heatingSetpointEvent(temp)
                    	
                    } else if (hvacMode == 'heat') {
                    	heatingSetpointEvent(temp)
						parent.setTargetTemp(this, tempUnit, temp)
                        heatingSetpointEvent(temp)
					}
				}
				break;
			default:
				if (temp) {
					if (temp < 51) { temp = 51 }
					if (temp > 89) { temp = 89 }
                    
					log.debug "Heat Temp Received: ${temp} (${tempUnit})"
					if (hvacMode == 'auto') {
						heatingSetpointEvent(temp)
                        parent.setTargetTempLow(this, tempUnit, temp) 
                        heatingSetpointEvent(temp)
                    	
					}  
                    if (hvacMode == 'heat') {
						heatingSetpointEvent(temp)
                        parent.setTargetTemp(this, tempUnit, temp) 
                        heatingSetpointEvent(temp)
                    }
				}
				break
            log.debug "Setting Heat Temp to: ${temp}"
            result = true
		}
	} else { 
    	log.debug "Skipping heat change" 
    	result = false
    }
    return result
}

def setCoolingSetpoint(temp) {
	log.trace "setCoolingSetpoint()..."
	def hvacMode = getHvacMode()
    def pres = getNestPresence()
	def tempUnit = state?.tempUnit
    def canCool = state?.can_cool.toBoolean()
	def result = false

    if (state?.present && canCool) {
		switch (tempUnit) {
			case "C":
				if (temp) {
					if (temp < 9) { temp = 9 }
					if (temp > 32) { temp = 32 }
                    
                    log.debug "Cool Temp Received: ${temp} (${tempUnit})"
					if (hvacMode == 'auto') {
                    	coolingSetpointEvent(temp)
						parent.setTargetTempHigh(this, tempUnit, temp) 
                        coolingSetpointEvent(temp)
                    	
					} 
                    else if (hvacMode == 'cool') {
						coolingSetpointEvent(temp)
                        parent.setTargetTemp(this, tempUnit, temp) 
                        coolingSetpointEvent(temp)
					}
                }
				break

			default:
				if (temp) {
					if (temp < 51) { temp = 51 }
					if (temp > 89) { temp = 89 }
                    
                    log.debug "Cool Temp Received: ${temp} (${tempUnit})"
					if (hvacMode == 'auto') {
						coolingSetpointEvent(temp)
                        parent.setTargetTempHigh(this, tempUnit, temp) 
                        coolingSetpointEvent(temp)
                    	
					} else if (hvacMode == 'cool') {
						coolingSetpointEvent(temp)
                        parent.setTargetTemp(this, tempUnit, temp) 
                        coolingSetpointEvent(temp)
					}
				}
				break
            result = true
		}
	} else {
		log.debug "Skipping cool change"
        result = false
	}
    return result
}


/************************************************************************************************
|							Sends Commands to Manager Application								|
*************************************************************************************************/
def setPresMode() {
	log.trace "setPresMode()"
    def pres = getNestPresence()
    log.trace "Current Pres: ${pres}"
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

def setThermostatMode(mode) {
	Logger("setThermostat Command Received")
	mode = mode == 'emergency heat'? 'heat' : mode
	switch(mode) {
    	case "auto":
        	hvacModeEvent("auto")
        	auto()
        	break
    	case "heat":
        	hvacModeEvent("heat")
        	heat()
        	break
       	case "cool":
        	hvacModeEvent("cool")
        	cool()
        	break
        case "off":
        	hvacModeEvent("off")
        	off()
        	break
    }
}

def off() {
	log.trace "off()..."
    def currentMode = getHvacMode()
	hvacModeEvent("off")     
    if (parent.setHvacMode(this, "off"))
        hvacModeEvent("off")
    else {
       	log.error "Error setting new mode." 
        hvacModeEvent(currentMode) // reset the tile back
    }
}

def heat() {
	log.trace "heat()..."
    def curPres = getNestPresence()
    def currentMode = getHvacMode()
	if (curPres != "away" || curPres != "auto-away") {
    	hvacModeEvent("heat")    
    	if (parent.setHvacMode(this, "heat")) { hvacModeEvent("heat") }
    	else {
       		log.error "Error setting new mode." 
        	hvacModeEvent(currentMode) // reset the tile back
    	}
    }
}

def emergencyHeat() {
    log.trace "emergencyHeat()..."
}

def cool() {
	log.trace "cool()..."
    def curPres = getNestPresence()
    def currentMode = getHvacMode()
	if (curPres != "away" || curPres != "auto-away") {
    	hvacModeEvent("cool")    
    	if (parent.setHvacMode(this, "cool")) { hvacModeEvent("cool") }
    	else {
       		log.error "Error setting new mode." 
        	hvacModeEvent(currentMode) // reset the tile back
    	}
    }
}

def auto() {
	log.trace "auto()..."
    def curPres = getNestPresence()
    def currentMode = getHvacMode()
	if (curPres != "away" || curPres != "auto-away") {
    	hvacModeEvent("auto")    
    	if (parent.setHvacMode(this, "heat-cool")) { hvacModeEvent("auto") }
    	else {
       		log.error "Error setting new mode." 
        	hvacModeEvent(currentMode) // reset the tile back
    	}
    }
}

def fanOn() {
	if(state?.has_fan.toBoolean()) {
    	fanModeEvent("true")
    	parent.setFanMode(this, true)
        fanModeEvent("true")
    }
}

def fanAuto() {
	if(state?.has_fan.toBoolean()) {
    	fanModeEvent("false")
   		parent.setFanMode(this,false)
        fanModeEvent("false")
    }
}

def setThermostatFanMode(str) {
    log.trace "setThermostatFanMode()..."
}

def fanOff() {
	if(state?.has_fan.toBoolean()) {
    	parent.setFanMode (this, "off")
    }
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
