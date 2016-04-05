/**
 *  Nest Weather
 *	Author: Anthony S. (@tonesto7)
 *  Author: Ben W. (@desertBlade)
 *	
 *
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

def devVer() { return "1.0.0" }

// for the UI
metadata {
	definition (name: "Nest Weather", namespace: "tonesto7", author: "Anthony S.") {

        capability "Illuminance Measurement"
        capability "Sensor"
  		capability "Refresh"
        capability "Relative Humidity Measurement"
        capability "Temperature Measurement"
        
        command "refresh"
		command "log"
        
        attribute "lastConnection", "string"
        attribute "apiStatus", "string"
        attribute "debugOn", "string"
        attribute "devTypeVer", "string"
	}

	simulator {
		
	}

	tiles(scale: 2) {
		htmlTile(name:"weatherHtml", action: "getWeatherHtml", width: 6, height: 6)
        
        valueTile("temp2", "device.temperature", width: 2, height: 2, decoration: "flat") {
        	state("default", label:'${currentValue}°', 	icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_like.png", 
            		backgroundColors: [
						// Celsius Color Range
						[value: 0, color: "#153591"],
						[value: 7, color: "#1e9cbb"],
						[value: 15, color: "#90d2a7"],
						[value: 23, color: "#44b621"],
						[value: 29, color: "#f1d801"],
						[value: 33, color: "#d04e00"],
						[value: 36, color: "#bc2323"],
						// Fahrenheit Color Range
						[value: 40, color: "#153591"],
						[value: 44, color: "#1e9cbb"],
						[value: 59, color: "#90d2a7"],
						[value: 74, color: "#44b621"],
						[value: 84, color: "#f1d801"],
						[value: 92, color: "#d04e00"],
						[value: 96, color: "#bc2323"]
        	])
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
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
			state("default", label: 'Device Type:\nv${currentValue}')
		}
		main ("temp2")
		details ("weatherHtml", "refresh")
	}
}

mappings {
	path("/getInfoHtml") {action: [GET: "getInfoHtml"]}
	path("/getWeatherHtml") {action: [GET: "getWeatherHtml"]}
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
        state.tempUnit = getTemperatureScale()
        state?.useMilitaryTime = !parent?.settings?.useMilitaryTime ? false : true
    	debugOnEvent(parent?.settings?.childDebug)
        apiStatusEvent(parent?.apiIssues())
        deviceVerEvent()
   	}
    lastUpdatedEvent()
    getWeatherConditions()
 	return null
}

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def deviceVerEvent() {
    def curData = device.currentState("devTypeVer")?.value
    def pubVer = parent?.latestWeathVer().ver.toString()
	def dVer = devVer() ? devVer() : null
    def newData = (pubVer != dVer) ? "${dVer}(New: v${pubVer})" : "${dVer}(Current)"
    state?.devTypeVer = newData
    if(curData != newData) {
        Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
    	sendEvent(name: 'devTypeVer', value: newData, displayed: false)
    } else { Logger("Device Type Version is: (${newData}) | Original State: (${curData})") }
}

def debugOnEvent(debug) {
	def val = device.currentState("debugOn")?.value
    def dVal = debug ? "On" : "Off"
    state?.debugStatus = dVal
	if(!val.equals(dVal)) {
    	log.debug("UPDATED | debugOn: (${dVal}) | Original State: (${val})")
        sendEvent(name: 'debugOn', value: dVal, displayed: false)
   	} else { Logger("debugOn: (${dVal}) | Original State: (${val})") }
}

def lastUpdatedEvent() {
    def now = new Date()
    def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
    def tf = new SimpleDateFormat(formatVal)
    	tf.setTimeZone(location?.timeZone)
   	def lastDt = "${tf?.format(now)}"
	def lastUpd = device.currentState("lastUpdatedDt")?.value
    state?.lastUpdatedDt = lastDt?.toString()
    if(!lastUpd.equals(lastDt?.toString())) {
        Logger("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
    	sendEvent(name: 'lastUpdatedDt', value: lastDt?.toString(), displayed: false, isStateChange: true)
    }
}

def apiStatusEvent(issue) {
	def appStat = device.currentState("apiStatus")?.value
    def val = issue ? "issue" : "ok"
    state?.apiStatus = val
	if(!appStat.equals(val)) { 
        log.debug("UPDATED | API Status is: (${val}) | Original State: (${appStat})")
   		sendEvent(name: "apiStatus", value: val, descriptionText: "API Status is: ${val}", displayed: true, isStateChange: true, state: val)
    } else { Logger("API Status is: (${val}) | Original State: (${appStat})") }
}

def humidityEvent(humidity) {
	def hum = device.currentState("humidity")?.value
	if(!hum.equals(humidity)) {
        log.debug("UPDATED | Humidity is (${humidity}) | Original State: (${hum})")
		sendEvent(name:'humidity', value: humidity, unit: "%", descriptionText: "Humidity is ${humidity}" , displayed: false, isStateChange: true)
    } else { Logger("Humidity is (${humidity}) | Original State: (${hum})") }
}

def illuminanceEvent(illum) {
	def cur = device.currentState("humidity")?.value
	if(!cur.equals(illum)) {
        log.debug("UPDATED | Illuminance is (${illum}) | Original State: (${cur})")
		sendEvent(name:'illuminance', value: illum, unit: "lux", descriptionText: "Humidity is ${illum}" , displayed: false, isStateChange: true)
    } else { Logger("Illuminance is (${illum}) | Original State: (${cur})") }
}

def temperatureEvent(Double tempVal) {
	def temp = device.currentState("temperature")?.value.toString()
	def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
    if(!temp.equals(rTempVal.toString())) {
        log.debug("UPDATED | Temperature is (${rTempVal}) | Original Temp: (${temp})")
    	sendEvent(name:'temperature', value: rTempVal, unit: state?.tempUnit, descriptionText: "Ambient Temperature is ${rTempVal}" , displayed: true, isStateChange: true)
    } else { Logger("Temperature is (${rTempVal}) | Original Temp: (${temp})") }
}

def getTemp() { 
	try { return device.currentValue("temperature") } 
	catch (e) { return 0 }
}

def wantMetric() { return (device.currentValue('temperatureUnit') == "C") }
/************************************************************************************************
|									Weather Info for Tiles										|
*************************************************************************************************/

def getWeatherConditions() {
    def cur = parent?.getWData()
    if(cur) {
        state.curWeather = cur
        //log.debug "cur: $cur"
        state.curWeatherTemp_f = Math.round(cur?.current_observation?.temp_f)
        state.curWeatherTemp_c = Math.round(cur?.current_observation?.temp_c)
        state.curWeatherHum = cur?.current_observation?.relative_humidity?.toString().replaceAll("\\%", "")
        state.curWeatherLoc = cur?.current_observation?.display_location?.full.toString()
        state.curWeatherCond = cur?.current_observation?.weather.toString()

        def curWeatherTemp = (state?.tempUnit == "C") ? "${state?.curWeatherTemp_c}°C": "${state?.curWeatherTemp_f}°F"
        temperatureEvent(curWeatherTemp)
        humidityEvent(state?.curWeatherHum)
        
        def curCondVal = "Current Weather:\nT: ${state?.curWeatherTemp} (${state?.curWeatherHum}%)\n${state?.curWeatherCond}" 
		state.curWeathVal = "Temp: ${curWeatherTemp} (${state?.curWeatherHum}%)"

        Logger("${state?.curWeatherLoc} Weather | humidity: ${state?.curWeatherHum} | temp_f: ${state?.curWeatherTemp_f} | temp_c: ${state?.curWeatherTemp_c} | Current Conditions: ${state?.curWeatherCond}")
    }
}

/************************************************************************************************
|										LOGGING FUNCTIONS										|
*************************************************************************************************/
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

def getWeatherHtml() { 
	renderHTML {
    	head {
        	"""
            <style type="text/css">
            	#header { 
                  font-size: 1.5em; font-weight: bold;
                  text-align: center;
                }
                #weather { 
                  font-size: 1em; 
                  text-align: center;
                }
            </style>
           	"""
        }
        body {
        	"""
            	<div class="container">
                  <div id="header">Current Weather Conditions</div>
                  <div id="weather">
               	    Temp: ${state?.curWeatherTemp} </br> 
                    Humidity: ${state?.curWeatherHum}% </br>
            	    <img src="${state?.curWeather?.current_observation?.icon_url}">
               	  </div>
            	</div>
            """
        }
    }
}
