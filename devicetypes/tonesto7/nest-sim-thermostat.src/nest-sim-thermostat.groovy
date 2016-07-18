/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
    
    definition (name: "Nest Sim Thermostat", namespace: "tonesto7", author: "A. Santilli") {
        capability "Thermostat"
        capability "Relative Humidity Measurement"
        
        command "tempUp"
        command "tempDown"
        command "heatUp"
        command "heatDown"
        command "coolUp"
        command "coolDown"
        command "setTemperature", ["number"]
        command "changePresence"
        command "safetyHumidityMaxUp"
        command "safetyHumidityMaxDown"
        command "safetyTempMinUp"
        command "safetyTempMinDown"
        command "safetyTempMaxUp"
        command "safetyTempMaxDown"
        command "lockedTempMinUp"
        command "lockedTempMinDown"
        command "lockedTempMaxUp"
        command "lockedTempMaxDown"
        command "changeTempLock"
        
        attribute "presence", "string"
        attribute "nestPresence", "string"
        attribute "safetyTempMin", "string"
        attribute "safetyTempMax", "string"
        attribute "safetyHumidityMax", "string"
        //attribute "safetyHumidityMin", "string"
        attribute "tempLockOn", "string"
        attribute "lockedTempMin", "string"
        attribute "lockedTempMax", "string"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"thermostatMulti", type:"thermostat", width:6, height:4) {
            tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState("default", label:'${currentValue}', unit:"dF")
            }
            tileAttribute("device.temperature", key: "VALUE_CONTROL") {
                attributeState("VALUE_UP", action: "tempUp")
                attributeState("VALUE_DOWN", action: "tempDown")
            }
            tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
                attributeState("default", label:'${currentValue}%', unit:"%")
            }
            tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
                attributeState("idle", backgroundColor:"#44b621")
                attributeState("heating", backgroundColor:"#ea5462")
                attributeState("cooling", backgroundColor:"#269bd2")
            }
            tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
                attributeState("off", label:'${name}')
                attributeState("heat", label:'${name}')
                attributeState("cool", label:'${name}')
                attributeState("auto", label:'${name}')
            }
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
                attributeState("default", label:'${currentValue}', unit:"dF")
            }
            tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
                attributeState("default", label:'${currentValue}', unit:"dF")
            }
        }

        valueTile("temperature", "device.temperature", width: 2, height: 2) {
            state("temperature", label:'Ambient:\n${currentValue}', unit:"dF",
                backgroundColors:[
                    [value: 31, color: "#153591"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 95, color: "#d04e00"],
                    [value: 96, color: "#bc2323"]
                ]
            )
        }
        standardTile("tempDown", "device.temperature", width: 2, height: 2, decoration: "flat") {
            state "default", action:"tempDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_down.png"
        }
        standardTile("tempUp", "device.temperature", width: 2, height: 2, decoration: "flat") {
            state "default", action:"tempUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_down.png"
        }

        valueTile("heatingSetpoint", "device.heatingSetpoint", width: 2, height: 2, decoration: "flat") {
            state "heat", label:'${currentValue}\nHeat Setpoint', unit: "F", backgroundColor:"#ffffff"
        }
        standardTile("heatDown", "device.temperature", width: 2, height: 2, decoration: "flat") {
            state "default", action:"heatDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_down.png"
        }
        standardTile("heatUp", "device.temperature", width: 2, height: 2, decoration: "flat") {
            state "default", action:"heatUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_up.png"
        }

        valueTile("coolingSetpoint", "device.coolingSetpoint", width: 2, height: 2, decoration: "flat") {
            state "cool", label:'${currentValue}\nCool Setpoint', unit:"F", backgroundColor:"#ffffff"
        }
        standardTile("coolDown", "device.temperature", width: 2, height: 2, decoration: "flat") {
            state "default", action:"coolDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_down.png"
        }
        standardTile("coolUp", "device.temperature", width: 2, height: 2, decoration: "flat") {
            state "default", action:"coolUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_up.png"
        }

        standardTile("mode", "device.thermostatMode", width: 2, height: 2, decoration: "flat") {
            state "off", action:"thermostat.heat", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/off_btn_icon.png"
            state "heat", action:"thermostat.cool", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_btn_icon.png"
            state "cool", action:"thermostat.auto", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_btn_icon.png"
            state "auto", action:"thermostat.off", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_cool_btn_icon.png"
        }
        
        standardTile("fanMode", "device.thermostatFanMode", width:2, height:2, decoration: "flat") {
            state "auto",	action:"fanOn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_auto_icon.png"
            state "on",		action:"fanAuto", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_on_icon.png"
            state "circulate",	action:"fanAuto", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_on_icon.png"
            state "disabled", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_disabled_icon.png"
        }
        
        standardTile("operatingState", "device.thermostatOperatingState", width: 2, height: 2) {
            state "idle", label:'${name}', backgroundColor:"#ffffff"
            state "heating", label:'${name}', backgroundColor:"#ffa81e"
            state "cooling", label:'${name}', backgroundColor:"#269bd2"
        }
        
        standardTile("nestPresence", "device.nestPresence", width:2, height:2, decoration: "flat") {
            state "home", 	    action: "changePresence",	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_home_icon.png"
            state "away", 		action: "changePresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_away_icon.png"
            state "auto-away", 	action: "changePresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_autoaway_icon.png"
            state "unknown",	action: "changePresence", 	icon: "st.unknown.unknown.unknown"
        }
        
        valueTile("safetyTempMin", "device.safetyTempMin", width: 2, height: 2, decoration: "flat") {
            state "default", label:'Safety Temp Min\n${currentValue}', unit: "F", backgroundColor:"#ffffff"
        }
        standardTile("safetyTempMinDown", "device.safetyTempMin", width: 2, height: 2, decoration: "flat") {
            state "default", action:"safetyTempMinDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_down.png"
        }
        standardTile("safetyTempMinUp", "device.safetyTempMin", width: 2, height: 2, decoration: "flat") {
            state "default", action:"safetyTempMinUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_up.png"
        }

        valueTile("safetyTempMax", "device.safetyTempMax", width: 2, height: 2, decoration: "flat") {
            state "default", label:'Safety Temp Max\n${currentValue}', unit: "F", backgroundColor:"#ffffff"
        }
        
        standardTile("safetyTempMaxDown", "device.safetyTempMax", width: 2, height: 2, decoration: "flat") {
            state "default", action:"safetyTempMaxDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_down.png"
        }
        standardTile("safetyTempMaxUp", "device.safetyTempMax", width: 2, height: 2, decoration: "flat") {
            state "default", action:"safetyTempMaxUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_up.png"
        }

        valueTile("safetyHumidityMax", "device.safetyHumidityMax", width: 2, height: 2, decoration: "flat") {
            state "default", label:'Hum Max\n${currentValue}', unit: "%", backgroundColor:"#ffffff"
        }
        standardTile("safetyHumidityMaxDown", "device.safetyHumidityMax", width: 2, height: 2, decoration: "flat") {
            state "default", action:"safetyTempMinDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_down.png"
        }
        standardTile("safetyHumidityMaxUp", "device.safetyHumidityMax", width: 2, height: 2, decoration: "flat") {
            state "default", action:"safetyHumidityMaxUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_up.png"
        }

        standardTile("tempLocked", "device.tempLockOn", width: 2, height: 2, decoration: "flat") {
            state "true", action:"changeTempLock", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/lock_icon.png"
            state "false", action:"changeTempLock", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/unlock_icon.png"
        }

        valueTile("lockedTempMin", "device.lockedTempMin", width: 2, height: 2, decoration: "flat") {
            state "default", label:'Temp Lock Min\n${currentValue}', unit: "F", backgroundColor:"#ffffff"
        }
        standardTile("lockedTempMinDown", "device.lockedTempMin", width: 2, height: 2, decoration: "flat") {
            state "default", action:"lockedTempMinDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_down.png"
        }
        standardTile("lockedTempMinUp", "device.lockedTempMin", width: 2, height: 2, decoration: "flat") {
            state "default", action:"lockedTempMinUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_up.png"
        }

        valueTile("lockedTempMax", "device.lockedTempMax", width: 2, height: 2, decoration: "flat") {
            state "default", label:'Temp Lock Max\n${currentValue}', unit: "F", backgroundColor:"#ffffff"
        }
        
        standardTile("lockedTempMaxDown", "device.lockedTempMax", width: 2, height: 2, decoration: "flat") {
            state "default", action:"lockedTempMaxDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_down.png"
        }
        standardTile("lockedTempMaxUp", "device.lockedTempMax", width: 2, height: 2, decoration: "flat") {
            state "default", action:"lockedTempMaxUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_up.png"
        }
        valueTile("filler", "device.filler", width: 2, height: 2, decoration: "flat") {
            state "default", label: ''
        }

        main("thermostatMulti")
        details([
            "thermostatMulti", "temperature","tempDown","tempUp",
            "mode", "fanMode", "operatingState",
            "heatingSetpoint", "heatDown", "heatUp",
            "coolingSetpoint", "coolDown", "coolUp", 
            "nestPresence", "filler", "filler",
            "safetyTempMin", "safetyTempMinDown", "safetyTempMinUp",
            "safetyTempMax", "safetyTempMaxDown", "safetyTempMaxUp",
            "safetyHumidityMax", "safetyHumidityMaxDown", "safetyHumidityMaxUp",
            "tempLocked", "filler", "filler",
            "lockedTempMin", "lockedTempMinDown", "lockedTempMinUp",
            "lockedTempMax", "lockedTempMaxDown", "lockedTempMaxUp"
            
        ])
    }
}

def installed() {
    sendEvent(name: "temperature", value: 72, unit: "F")
    sendEvent(name: "heatingSetpoint", value: 70, unit: "F")
    sendEvent(name: "thermostatSetpoint", value: 70, unit: "F")
    sendEvent(name: "coolingSetpoint", value: 76, unit: "F")
    sendEvent(name: "thermostatMode", value: "off")
    sendEvent(name: "thermostatFanMode", value: "auto")
    sendEvent(name: "thermostatOperatingState", value: "idle")
    sendEvent(name: "humidity", value: 53, unit: "%")
    sendEvent(name: "presence", value: "present")
    sendEvent(name: "nestPresence", value: "home")
    sendEvent(name: "safetyTempMin", value: 60, unit: "F")
    sendEvent(name: "safetyTempMax", value: 85, unit: "F")
    sendEvent(name: "safetyHumidityMax", value: 80, unit: "%")
    //sendEvent(name: "safetyHumidityMin", value: 15, unit: "%")
    sendEvent(name: "tempLockOn", value: false)
    sendEvent(name: "lockedTempMin", value: 60, unit: "F")
    sendEvent(name: "lockedTempMax", value: 80, unit: "F")
}

def parse(String description) {
}

def evaluate(temp, heatingSetpoint, coolingSetpoint) {
    log.debug "evaluate($temp, $heatingSetpoint, $coolingSetpoint"
    def threshold = 1.0
    def current = device.currentValue("thermostatOperatingState")
    def mode = device.currentValue("thermostatMode")

    def heating = false
    def cooling = false
    def idle = false
    if (mode in ["heat","emergency heat","auto"]) {
        if (heatingSetpoint - temp >= threshold) {
            heating = true
            sendEvent(name: "thermostatOperatingState", value: "heating")
        }
        else if (temp - heatingSetpoint >= threshold) {
            idle = true
        }
        sendEvent(name: "thermostatSetpoint", value: heatingSetpoint)
    }
    if (mode in ["cool","auto"]) {
        if (temp - coolingSetpoint >= threshold) {
            cooling = true
            sendEvent(name: "thermostatOperatingState", value: "cooling")
        }
        else if (coolingSetpoint - temp >= threshold && !heating) {
            idle = true
        }
        sendEvent(name: "thermostatSetpoint", value: coolingSetpoint)
    }
    else {
        sendEvent(name: "thermostatSetpoint", value: heatingSetpoint)
    }
    if (idle && !heating && !cooling) {
        sendEvent(name: "thermostatOperatingState", value: "idle")
    }
}

def setHeatingSetpoint(Double degreesF) {
    log.debug "setHeatingSetpoint($degreesF)"
    sendEvent(name: "heatingSetpoint", value: degreesF)
    evaluate(device.currentValue("temperature"), degreesF, device.currentValue("coolingSetpoint"))
}

def setCoolingSetpoint(Double degreesF) {
    log.debug "setCoolingSetpoint($degreesF)"
    sendEvent(name: "coolingSetpoint", value: degreesF)
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), degreesF)
}

def setThermostatMode(String value) {
    sendEvent(name: "thermostatMode", value: value)
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setThermostatFanMode(String value) {
    sendEvent(name: "thermostatFanMode", value: value)
}

def off() {
    sendEvent(name: "thermostatMode", value: "off")
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def heat() {
    sendEvent(name: "thermostatMode", value: "heat")
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def auto() {
    sendEvent(name: "thermostatMode", value: "auto")
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def emergencyHeat() {
    sendEvent(name: "thermostatMode", value: "emergency heat")
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def cool() {
    sendEvent(name: "thermostatMode", value: "cool")
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def fanOn() {
    sendEvent(name: "thermostatFanMode", value: "on")
}

def fanAuto() {
    sendEvent(name: "thermostatFanMode", value: "auto")
}

def fanCirculate() {
    sendEvent(name: "thermostatFanMode", value: "circulate")
}

def poll() {
    null
}

def tempUp() {
    def ts = device.currentState("temperature")
    def value = ts ? ts.integerValue + 1 : 72
    sendEvent(name:"temperature", value: value)
    log.debug "temperature is now: (${value})"
    evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def tempDown() {
    def ts = device.currentState("temperature")
    def value = ts ? ts.integerValue - 1 : 72
    sendEvent(name:"temperature", value: value)
    log.debug "temperature is now: (${value})"
    evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setTemperature(value) {
    def ts = device.currentState("temperature")
    sendEvent(name:"temperature", value: value)
    log.debug "temperature is now: (${value})"
    evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def heatUp() {
    def ts = device.currentState("heatingSetpoint")
    def value = ts ? ts.integerValue + 1 : 68
    sendEvent(name:"heatingSetpoint", value: value)
    log.debug "heatingSetpoint is now: (${value})"
    evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}

def heatDown() {
    def ts = device.currentState("heatingSetpoint")
    def value = ts ? ts.integerValue - 1 : 68
    sendEvent(name:"heatingSetpoint", value: value)
    log.debug "heatingSetpoint is now: (${value})"
    evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}


def coolUp() {
    def ts = device.currentState("coolingSetpoint")
    def value = ts ? ts.integerValue + 1 : 76
    sendEvent(name:"coolingSetpoint", value: value)
    log.debug "coolingSetpoint is now: (${value})"
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}

def coolDown() {
    def ts = device.currentState("coolingSetpoint")
    def value = ts ? ts.integerValue - 1 : 76
    sendEvent(name:"coolingSetpoint", value: value)
    log.debug "coolingSetpoint is now: (${value})"
    evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}

def changePresence() {
    def pres = device.currentState("presence")?.stringValue
    def nPres = device?.currentState("nestPresence")?.stringValue
    def newPres = (pres == "present") ? "not present" : "present"
    def newNestPres = (pres == "present") ? "away" : "home"
    log.debug "nestPresence is now: ${newNestPres}"
    log.debug "presence is now: ${newPres}"
    sendEvent(name: 'nestPresence', value: newNestPres, descriptionText: "Nest Presence is: ${newNestPres}", displayed: true, isStateChange: true )
    sendEvent(name: 'presence', value: newPres, descriptionText: "Device is: ${newPres}", displayed: false, isStateChange: true, state: newPres )
}

def changeTempLock() {
    def cur = device.currentState("tempLockOn")?.stringValue
    def newS = (cur.toString() == "true") ? false : true
    log.debug "tempLock is now: (${newS == true ? "On" : "Off"})"
    sendEvent(name: 'tempLockOn', value: newS, descriptionText: "Temp Lock is: ${newS}", displayed: false, isStateChange: true, state: newS )
}

def safetyTempMinUp() {
    def ts = device.currentState("safetyTempMin")
    def value = ts ? ts.integerValue + 1 : 72
    log.debug "safetyTempMin is now: (${value})"
    sendEvent(name: 'safetyTempMin', value: value, descriptionText: "Safety Temp Min is: (${value})", displayed: false, isStateChange: true)
}

def safetyTempMinDown() {
    def ts = device.currentState("safetyTempMin")
    def value = ts ? ts.integerValue - 1 : 72
    log.debug "safetyTempMin is now: (${value})"
    sendEvent(name:'safetyTempMin', value: value,descriptionText: "Safety Temp Min is: (${value})", displayed: false, isStateChange: true)
}

def safetyTempMaxUp() {
    def ts = device.currentState("safetyTempMax")
    def value = ts ? ts.integerValue + 1 : 72
    log.debug "safetyTempMax is now: (${value})"
    sendEvent(name:'safetyTempMax', value: value, descriptionText: "Safety Temp Max is: (${value})", displayed: false, isStateChange: true)
}

def safetyTempMaxDown() {
    def ts = device.currentState("safetyTempMin")
    def value = ts ? ts.integerValue - 1 : 72
    log.debug "safetyTempMax is now: (${value})"
    sendEvent(name: 'safetyTempMax', value: value, descriptionText: "Safety Temp Max is: (${value})", displayed: false, isStateChange: true)
}

def safetyHumidityMaxUp() {
    def ts = device.currentState("safetyHumidityMax")
    def value = ts ? ts.integerValue + 1 : 72
    log.debug "safetyHumidityMax is now: (${value})"
    sendEvent(name: 'safetyHumidityMax', value: value, descriptionText: "Safety Humidity Max is: (${value})", displayed: false, isStateChange: true)
}

def safetyHumidityMaxDown() {
    def ts = device.currentState("safetyHumidityMax")
    def value = ts ? ts.integerValue - 1 : 72
    log.debug "safetyHumidityMax is now: (${value})"
    sendEvent(name: 'safetyHumidityMax', value: value, descriptionText: "Safety Humidity Max is: (${value})", displayed: false, isStateChange: true)
}

def lockedTempMinUp() {
    def ts = device.currentState("lockedTempMin")
    def value = ts ? ts.integerValue + 1 : 72
    log.debug "lockedTempMin is now: (${value})"
    sendEvent(name: 'lockedTempMin', value: value, descriptionText: "Locked Temp Min is: (${value})", displayed: false, isStateChange: true)
}

def lockedTempMinDown() {
    def ts = device.currentState("lockedTempMin")
    def value = ts ? ts.integerValue - 1 : 72
    log.debug "lockedTempMin is now: (${value})"
    sendEvent(name:'lockedTempMin', value: value, descriptionText: "Locked Temp Min is: (${value})", displayed: false, isStateChange: true)
}

def lockedTempMaxUp() {
    def ts = device.currentState("lockedTempMax")
    def value = ts ? ts.integerValue + 1 : 72
    log.debug "lockedTempMax is now: (${value})"
    sendEvent(name: 'lockedTempMax', value: value, descriptionText: "Locked Temp Max is: (${value})", displayed: false, isStateChange: true)
}

def lockedTempMaxDown() {
    def ts = device.currentState("lockedTempMin")
    def value = ts ? ts.integerValue - 1 : 72
    log.debug "lockedTempMax is now: (${value})"
    sendEvent(name: 'lockedTempMax', value: value, descriptionText: "Locked Temp Max is: (${value})", displayed: false, isStateChange: true)
}