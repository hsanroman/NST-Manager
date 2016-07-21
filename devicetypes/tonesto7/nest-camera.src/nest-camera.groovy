/**
 *  Nest Cam
 *	Authors: Anthony S. (@tonesto7), Ben W. (@desertblade), Eric S. (@E_Sch)
 *  A Big Thanks go out to Greg (@ghesp) for your help getting the video working.
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
//import org.apache.http.client.utils

preferences { }

def devVer() { return "0.0.5" }

metadata {
    definition (name: "${textDevName()}", author: "Anthony S.", namespace: "tonesto7") {
        capability "Sensor"
        capability "Switch"
        capability "Motion Sensor"
        capability "Sound Sensor"
        capability "Refresh"
        capability "Notification"
        //capability "Configuration"
        capability "Image Capture"
        //capability "Video Camera"
        //capability "Video Capture"

        command "refresh"
        command "poll"
        command "log", ["string","string"]
        command "streamingOn"
        command "streamingOff"
        command "changeStreaming"

        attribute "softwareVer", "string"
        attribute "lastConnection", "string"
        attribute "lastOnline", "string"
        attribute "lastUpdateDt", "string"
        attribute "activityZoneName", "string"
        attribute "isStreaming", "string"
        attribute "audioInputEnabled", "string"
        attribute "videoHistoryEnabled", "string"
        attribute "publicShareEnabled", "string"
        attribute "lastEventStart", "string"
        attribute "lastEventEnd", "string"
        attribute "apiStatus", "string"
        attribute "debugOn", "string"
        attribute "devTypeVer", "string"
        attribute "onlineStatus", "string"
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "videoPlayer", type: "videoPlayer", width: 6, height: 4) {
            tileAttribute("device.switch5", key: "CAMERA_STATUS") {
                attributeState("on", label: "Active", icon: "st.camera.dlink-indoor", action: "vidOff", backgroundColor: "#79b821", defaultState: true)
                attributeState("off", label: "Inactive", icon: "st.camera.dlink-indoor", action: "vidOn", backgroundColor: "#ffffff")
                attributeState("restarting", label: "Connecting", icon: "st.camera.dlink-indoor", backgroundColor: "#53a7c0")
                attributeState("unavailable", label: "Unavailable", icon: "st.camera.dlink-indoor", action: "refresh.refresh", backgroundColor: "#F22000")
            }
            tileAttribute("device.camera", key: "PRIMARY_CONTROL") {
                attributeState("on", label: "Active", icon: "st.camera.dlink-indoor", backgroundColor: "#79b821", defaultState: true)
                attributeState("off", label: "Inactive", icon: "st.camera.dlink-indoor", backgroundColor: "#ffffff")
                attributeState("restarting", label: "Connecting", icon: "st.camera.dlink-indoor", backgroundColor: "#53a7c0")
                attributeState("unavailable", label: "Unavailable", icon: "st.camera.dlink-indoor", backgroundColor: "#F22000")
            }
            tileAttribute("device.startLive", key: "START_LIVE") {
                attributeState("live", action: "start", defaultState: true)
            }

            tileAttribute("device.stream", key: "STREAM_URL") {
                attributeState("activeURL", defaultState: true)
            }
            tileAttribute("device.betaLogo", key: "BETA_LOGO") {
                attributeState("betaLogo", label: "", value: "", defaultState: true)
            }
        }
        carouselTile("cameraDetails", "device.image", width: 6, height: 2) { }

        standardTile("isStreamingStatus", "device.isStreaming", width: 2, height: 2, decoration: "flat") {
            state("on", label: "Streaming", action: "streamingOff", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_green_icon.png", backgroundColor: "#79b821")
            state("off", label: "Off", action: "streamingOn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_gray_icon.png", backgroundColor: "#ffffff")
            state("unavailable", label: "Unavailable", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_red_icon.png", backgroundColor: "#F22000")
        }
        standardTile("isStreaming", "device.isStreaming", width: 2, height: 2, decoration: "flat") {
            state("on", action: "streamingOff", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_stream_btn_icon.png")
            state("off", action: "streamingOn", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_off_btn_icon.png")
            state("unavailable", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_offline_btn_icon.png")
        }
        standardTile("take", "device.image", width: 1, height: 1, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state "take", label: "Take", action: "Image Capture.take", icon: "st.camera.camera", backgroundColor: "#FFFFFF", nextState:"taking"
            state "taking", label:'Taking', action: "", icon: "st.camera.take-photo", backgroundColor: "#53a7c0"
            state "image", label: "Take", action: "Image Capture.take", icon: "st.camera.camera", backgroundColor: "#FFFFFF", nextState:"taking"
        }
        standardTile("motion", "device.motion", width: 2, height: 2) {
            state "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0"
            state "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
        }
        standardTile("sound", "device.sound", width: 2, height: 2) {
            state "detected", label:'Noise', icon:"st.sound.sound.detected", backgroundColor:"#53a7c0"
            state "not detected", label:'Quiet', icon:"st.sound.sound.notdetected", backgroundColor:"#ffffff"
        }
        standardTile("filler", "device.filler", width: 2, height: 2){
            state("default", label:'')
        }
        valueTile("onlineStatus", "device.onlineStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
            state("default", label: 'Network Status:\n${currentValue}')
        }
        valueTile("softwareVer", "device.softwareVer", inactiveLabel: false, width: 2, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Firmware:\nv${currentValue}')
        }
        valueTile("lastConnection", "device.lastConnection", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Camera Last Checked-In:\n${currentValue}')
        }
        standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
            state "default", label: 'refresh', action:"refresh.refresh", icon:"st.secondary.refresh-icon"
        }
        valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
            state("default", label: 'Data Last Received:\n${currentValue}')
        }
        valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
            state("default", label: 'Device Type:\nv${currentValue}')
        }
        valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, decoration: "flat", wordWrap: true) {
            state "Ok", label: "API Status:\nOK"
            state "Issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
        }
        valueTile("debugOn", "device.debugOn", width: 2, height: 1, decoration: "flat") {
            state "true", 	label: 'Debug:\n${currentValue}'
            state "false", 	label: 'Debug:\n${currentValue}'
        }
        htmlTile(name:"devInfoHtml", action: "getInfoHtml", width: 6, height: 14)

    main "isStreamingStatus"
    details(["devInfoHtml", "isStreaming", "motion", "sound", "refresh"])
    //details(["alarmState", "filler", "batteryState", "filler", "devInfoHtml", "refresh"])
    }
}

mappings {
    path("/getInfoHtml") {action: [GET: "getInfoHtml"]}
}

def initialize() {
    log.info "Nest Camera ${textVersion()} ${textCopyright()}"
    poll()
}

def parse(String description) {
    log.debug "Parsing '${description}'"
}

def poll() {
    log.debug "polling parent..."
    parent.refresh(this)
}

def refresh() {
    log.debug "refreshing parent..."
    poll()
}

def generateEvent(Map eventData) {
    //log.trace("generateEvent parsing data ${eventData}")
    try {
        Logger("------------START OF API RESULTS DATA------------", "warn")
        if(eventData) {
            def results = eventData?.data
            //log.debug "results: $results"
            state?.useMilitaryTime = eventData?.mt ? true : false
            state.nestTimeZone = !location?.timeZone ? eventData?.tz : null
            isStreamingEvent(results?.is_streaming)
            videoHistEnabledEvent(results?.is_video_history_enabled?.toString())
            publicShareEnabledEvent(results?.is_public_share_enabled?.toString())
            if(!results?.last_is_online_change) { lastCheckinEvent(null) }
            else { lastCheckinEvent(results?.last_is_online_change?.toString()) }
            apiStatusEvent(eventData?.apiIssues)
            debugOnEvent(eventData?.debug ? true : false)
            onlineStatusEvent(results?.is_online?.toString())
            audioInputEnabledEvent(results?.is_audio_input_enabled?.toString())
            softwareVerEvent(results?.software_version?.toString())
            if(results?.activity_zones) { state?.activityZones = results?.activity_zones }
            if(results?.public_share_url) { state?.public_share_url = results?.public_share_url }
            if(results?.snapshot_url) { state?.snapshot_url = results?.snapshot_url?.toString() }
            if(results?.app_url) { state?.app_url = results?.app_url?.toString() }
            if(results?.web_url) { state?.web_url = results?.web_url?.toString() }
            if(results?.last_event) {
                //lastEventDataEvent(results?.last_event)
                //if(results?.last_event?.has_motion) { zoneMotionEvent(results?.last_event) }
                //if(results?.last_event?.has_sound) { zoneSoundEvent(results?.last_event) }
                //if(results?.last_event?.activity_zone_ids) { activityZoneEvent(results?.last_event?.activity_zone_ids) }
            }
            deviceVerEvent(eventData?.latestVer.toString())
            state?.cssUrl = eventData?.cssUrl
        }
        lastUpdatedEvent()
        //log.debug "Device State Data: ${getState()}" //This will return all of the devices state data to the logs.
        return null
    }
    catch (ex) {
        log.error "generateEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "generateEvent")
    }
}

def getDataByName(String name) {
    state[name] ?: device.getDataValue(name)
}

def getDeviceStateData() {
    return getState()
}

def getTimeZone() {
    def tz = null
    if (location?.timeZone) { tz = location?.timeZone }
    else { tz = state?.nestTimeZone ? TimeZone.getTimeZone(state?.nestTimeZone) : null }
    if(!tz) { log.warn "getTimeZone: Hub or Nest TimeZone is not found ..." }
    return tz
}

def isCodeUpdateAvailable(newVer, curVer) {
    try {
        def result = false
        def latestVer
        def versions = [newVer, curVer]
        if(newVer != curVer) {
            latestVer = versions?.max { a, b ->
                def verA = a?.tokenize('.')
                def verB = b?.tokenize('.')
                def commonIndices = Math.min(verA?.size(), verB?.size())
                for (int i = 0; i < commonIndices; ++i) {
                    //log.debug "comparing $numA and $numB"
                    if (verA[i]?.toInteger() != verB[i]?.toInteger()) {
                        return verA[i]?.toInteger() <=> verB[i]?.toInteger()
                    }
                }
                verA?.size() <=> verB?.size()
            }
            result = (latestVer == newVer) ? true : false
        }
        //log.debug "type: $type | newVer: $newVer | curVer: $curVer | newestVersion: ${latestVer} | result: $result"
        return result
    } catch (ex) {
        LogAction("isCodeUpdateAvailable Exception: ${ex}", "error", true)
        sendChildExceptionData("camera", devVer(), ex, "isCodeUpdateAvailable")
    }
}

def deviceVerEvent(ver) {
    try {
        def curData = device.currentState("devTypeVer")?.value.toString()
        def pubVer = ver ?: null
        def dVer = devVer() ?: null
        def newData = isCodeUpdateAvailable(pubVer, dVer) ? "${dVer}(New: v${pubVer})" : "${dVer}"
        state?.devTypeVer = newData
        state?.updateAvailable = isCodeUpdateAvailable(pubVer, dVer)
        if(!curData?.equals(newData)) {
            Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
            sendEvent(name: 'devTypeVer', value: newData, displayed: false)
        } else { Logger("Device Type Version is: (${newData}) | Original State: (${curData})") }
    }
    catch (ex) {
        log.error "deviceVerEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "deviceVerEvent")
    }
}

def lastCheckinEvent(checkin) {
    try {
        def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
        def tf = new SimpleDateFormat(formatVal)
        tf.setTimeZone(getTimeZone())
        def lastConn = checkin ? "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}" : "Not Available"
        def lastChk = device.currentState("lastConnection")?.value
        state?.lastConnection = lastConn?.toString()
        if(!lastChk.equals(lastConn?.toString())) {
            Logger("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
            sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: state?.showProtActEvts, isStateChange: true)
        } else { Logger("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
    }
    catch (ex) {
        log.error "lastCheckinEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "lastCheckinEvent")
    }
}

def lastOnlineEvent(dt) {
    try {
        def lastOnlVal = device.currentState("lastOnline")?.value
        def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
        def tf = new SimpleDateFormat(formatVal)
        tf.setTimeZone(getTimeZone())
        def lastOnl = !dt ? "Nothing To Show..." : "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dt))}"
        state?.lastOnl = lastOnl
        if(!lastOnlVal.equals(lastOnl?.toString())) {
            Logger("UPDATED | Last Online was: (${lastOnl}) | Original State: (${lastOnlVal})")
            sendEvent(name: 'lastOnline', value: lastOnl, displayed: true, isStateChange: true)
        } else { Logger("Last Manual Test was: (${lastOnl}) | Original State: (${lastOnlVal})") }
    }
    catch (ex) {
        log.error "lastOnlineEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "lastOnlineEvent")
    }
}

def isStreamingEvent(isStreaming) {
    //log.trace "isStreamingEvent($isStreaming)..."
    try {
        def isOn = device.currentState("isStreaming")?.value
        def isOnline = device.currentState("onlineStatus")?.value
        def val = (isStreaming.toBoolean() == true) ? "on" : (!isOnline == "Online" ? "unavailable" : "off")
        state?.isStreaming = val == "on" ? true : false
        if(!isOn.equals(val)) {
            log.debug("UPDATED | Streaming Video is: (${val}) | Original State: (${isOn})")
            sendEvent(name: "isStreaming", value: val, descriptionText: "Streaming Video is: ${val}", displayed: true, isStateChange: true, state: val)
        } else { Logger("Streaming Video Status is: (${val}) | Original State: (${isOn})") }
    }
    catch (ex) {
        log.error "isStreamingEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "isStreamingEvent")
    }
}

def audioInputEnabledEvent(on) {
    try {
        def isOn = device.currentState("audioInputEnabled")?.value
        def val = on ? "Enabled" : "Disabled"
        state?.audioInputEnabled = val
        if(!isOn.equals(val)) {
            log.debug("UPDATED | Audio Input Status is: (${val}) | Original State: (${isOn})")
            sendEvent(name: "audioInputEnabled", value: val, descriptionText: "Audio Input Status is: ${val}", displayed: true, isStateChange: true, state: val)
        } else { Logger("Audio Input Status is: (${val}) | Original State: (${isOn})") }
    }
    catch (ex) {
        log.error "audioInputEnabledEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "audioInputEnabledEvent")
    }
}

def videoHistEnabledEvent(on) {
    try {
        def isOn = device.currentState("videoHistoryEnabled")?.value
        def val = on ? "Enabled" : "Disabled"
        state?.videoHistoryEnabled = val
        if(!isOn.equals(val)) {
            log.debug("UPDATED | Video History Status is: (${val}) | Original State: (${isOn})")
            sendEvent(name: "videoHistoryEnabled", value: val, descriptionText: "Video History Status is: ${val}", displayed: true, isStateChange: true, state: val)
        } else { Logger("Video History Status is: (${val}) | Original State: (${isOn})") }
    }
    catch (ex) {
        log.error "videoHistEnabledEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "videoHistEnabledEvent")
    }
}

def publicShareEnabledEvent(on) {
    try {
        def isOn = device.currentState("publicShareEnabled")?.value
        def val = on ? "Enabled" : "Disabled"
        state?.publicShareEnabled = val
        if(!isOn.equals(val)) {
            log.debug("UPDATED | Public Sharing Status is: (${val}) | Original State: (${isOn})")
            sendEvent(name: "publicShareEnabled", value: val, descriptionText: "Public Sharing Status is: ${val}", displayed: true, isStateChange: true, state: val)
        } else { Logger("Public Sharing Status is: (${val}) | Original State: (${isOn})") }
    }
    catch (ex) {
        log.error "publicShareEnabledEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "publicShareEnabledEvent")
    }
}

def softwareVerEvent(ver) {
    try {
        def verVal = device.currentState("softwareVer")?.value
        state?.softwareVer = ver
        if(!verVal.equals(ver)) {
            log.debug("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
            sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now v${ver}", displayed: false)
        } else { Logger("Firmware Version: (${ver}) | Original State: (${verVal})") }
    }
    catch (ex) {
        log.error "softwareVerEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "softwareVerEvent")
    }
}

def lastEventDataEvent(data) {
    try {
        def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
        def tf = new SimpleDateFormat(formatVal)
            tf.setTimeZone(getTimeZone())

        def curStart = tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", device.currentState("lastEventStart")?.stringValue))
        def curEnd = tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", device.currentState("lastEventEnd")?.stringValue))

        def startDt = data?.start_time ? "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time))}" : "Not Available"
        def endDt = data?.end_time ? "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time))}" : "Not Available"

        state.lastEventStartDt = startDt
        state.lastEventEndDt = endDt
        state?.lastEventData = data

        def newStart = tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time))
        def newEnd = tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time))

        log.debug "curStart: ${curStart}"
        log.debug "newStart: ${newStart}"
        log.debug "curStop: ${curEnd}"
        log.debug "newStop: ${newEnd}"

        if(curStart != newStart || curEnd != newEnd) {
            log.debug("UPDATED | Last Event Start Time: (${newStart}) | Original State: (${curStart})")
            sendEvent(name: 'lastEventStart', value: newStart, descriptionText: "Last Event Start is now ${newStart}", displayed: false)
            log.debug("UPDATED | Last Event End Time: (${newEnd}) | Original State: (${curEnd})")
            sendEvent(name: 'lastEventEnd', value: newEnd, descriptionText: "Last Event End is now ${newEnd}", displayed: false)
        } else {
            log.debug("Last Event Start Time: (${newStart}) | Original State: (${curStart})")
            log.debug("Last Event End Time: (${newEnd}) | Original State: (${curEnd})")
        }
    }
    catch (ex) {
        log.error "lastEventDataEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", ex, "lastEventDataEvent")
    }
}

def zoneMotionEvent(data) {
    try {
        def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
        def tf = new SimpleDateFormat()
            //tf.setTimeZone(getTimeZone())
        def isMotion = device.currentState("motion")?.stringValue
        def startDt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(data?.start_time)
            startDt.setTimeZone(getTimeZone())
        def newDt = tf.format(startDt)
        log.debug "startDt: $newDt"
        def endDt = tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time))
        //def isBtwn = timeOfDayIsBetween(startDt, endDt, new Date(), getTimeZone())
        log.debug "hasMotion: ${data?.has_motion} | start: $startDt | end: $endDt | isBtwn: $isBtwn"
        def val = (data?.has_motion == "true" && isBtwn) ? "active" : "inactive"
        if(!isMotion.equals(val)) {
            log.debug("UPDATED | Motion Sensor is: (${val}) | Original State: (${isMotion})")
            sendEvent(name: "motion", value: val, descriptionText: "Motion Sensor is: ${val}", displayed: true, isStateChange: true, state: val)
        } else { Logger("Motion Sensor is: (${val}) | Original State: (${isMotion})") }
    }
    catch (ex) {
        log.error "zoneMotionEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "zoneMotionEvent")
    }
}

def zoneSoundEvent(sound) {
    try {
        def isSound = device.currentState("sound")?.stringValue
        def val = sound == "true" ? "detected" : "not detected"
        if(!isSound.equals(val)) {
            log.debug("UPDATED | Sound Sensor is now: (${val}) | Original State: (${isSound})")
            sendEvent(name: "sound", value: val, descriptionText: "Sound Sensor is: ${val}", displayed: true, isStateChange: true, state: val)
        } else { Logger("Sound Sensor is: (${val}) | Original State: (${isSound})") }
    }
    catch (ex) {
        log.error "zoneSoundEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "zoneSoundEvent")
    }
}

def activityZoneEvent(zones) {
    log.trace "activityZoneEvent($zones)..."
}

def debugOnEvent(debug) {
    try {
        def val = device.currentState("debugOn")?.value
        def dVal = debug ? "On" : "Off"
        state?.debugStatus = dVal
        state?.debug = debug.toBoolean() ? true : false
        if(!val.equals(dVal)) {
            log.debug("UPDATED | debugOn: (${dVal}) | Original State: (${val})")
            sendEvent(name: 'debugOn', value: dVal, displayed: false)
        } else { Logger("debugOn: (${dVal}) | Original State: (${val})") }
    }
    catch (ex) {
        log.error "debugOnEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", ex, "debugOnEvent")
    }
}

def apiStatusEvent(issue) {
    try {
        def curStat = device.currentState("apiStatus")?.value
        def newStat = issue ? "Issues" : "Ok"
        state?.apiStatus = newStat
        if(!curStat.equals(newStat)) {
            log.debug("UPDATED | API Status is: (${newStat}) | Original State: (${curStat})")
            sendEvent(name: "apiStatus", value: newStat, descriptionText: "API Status is: ${newStat}", displayed: true, isStateChange: true, state: newStat)
        } else { Logger("API Status is: (${newStat}) | Original State: (${curStat})") }
    }
    catch (ex) {
        log.error "apiStatusEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "apiStatusEvent")
    }
}

def lastUpdatedEvent() {
    try {
        def now = new Date()
        def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
        def tf = new SimpleDateFormat(formatVal)
        tf.setTimeZone(getTimeZone())
        def lastDt = "${tf?.format(now)}"
        def lastUpd = device.currentState("lastUpdatedDt")?.value
        state?.lastUpdatedDt = lastDt?.toString()
        if(!lastUpd.equals(lastDt?.toString())) {
            Logger("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
            sendEvent(name: 'lastUpdatedDt', value: lastDt?.toString(), displayed: false, isStateChange: true)
        }
    }
    catch (ex) {
        log.error "lastUpdatedEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "lastUpdatedEvent")
    }
}

def onlineStatusEvent(online) {
    try {
        def isOn = device.currentState("onlineStatus")?.value
        def val = online ? "Online" : "Offline"
        state?.onlineStatus = val
        if(!isOn.equals(val)) {
            log.debug("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
            sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: state?.showProtActEvts, isStateChange: true, state: val)
        } else { Logger("Online Status is: (${val}) | Original State: (${isOn})") }
    }
    catch (ex) {
        log.error "onlineStatusEvent Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "onlineStatusEvent")
    }
}

def getPublicVideoId() {
    if(state?.public_share_url) {
        def vidId = state?.public_share_url.tokenize('/')
        return vidId[3].toString()
    }
}

//this scrapes the public nest cam page for its unique id for using in render html tile
def getUUID(pubVidId) {
    def params = [
        uri: "https://opengraph.io/api/1.0/site/https://video.nest.com/live/${pubVidId}"
    ]
    try {
        httpGet(params) { resp ->
            def uuid = (resp?.data?.hybridGraph.image =~ /uuid=(\w*)/)[0][1]
            log.debug "uuid: $uuid"
            return uuid ?: null
        }
    } catch (ex) {
        log.error "getUUID Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "getUUID")
    }
}
/************************************************************************************************
|									DEVICE COMMANDS     										|
*************************************************************************************************/
def streamingOn() {
    try {
        log.trace "streamingOn..."
        parent?.setCamStreaming(this, "true")
        sendEvent(name: "isStreaming", value: "on", descriptionText: "Streaming Video is: on", displayed: true, isStateChange: true, state: "on")
    } catch (ex) {
        log.error "streamingOn Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "streamingOn")
    }
}

def streamingOff() {
    try {
        log.trace "streamingOff..."
        parent?.setCamStreaming(this, "false")
        sendEvent(name: "isStreaming", value: "off", descriptionText: "Streaming Video is: off", displayed: true, isStateChange: true, state: "off")
    } catch (ex) {
        log.error "streamingOff Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex, "streamingOff")
    }
}

def on() {
    streamingOn()
}

def off() {
    streamingOff()
}
/************************************************************************************************
|										LOGGING FUNCTIONS										|
*************************************************************************************************/

// Local Application Logging
def Logger(msg, logType = "debug") {
     if(state?.debug) {
        switch (logType) {
            case "trace":
                log.trace "${msg}"
                break
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
                break
        }
     }
 }
// Print log message from parent
def log(message, level = "trace") {
    switch (level) {
        case "trace":
            log.trace "PARENT_Log>> " + message
            break
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
            break
    }
    return null
}

def getImgBase64(url,type) {
    try {
        def params = [
            uri: url,
            contentType: 'image/$type'
        ]
        httpGet(params) { resp ->
            if(resp.data) {
                def respData = resp?.data
                ByteArrayOutputStream bos = new ByteArrayOutputStream()
                int len
                int size = 2048
                byte[] buf = new byte[size]
                while ((len = respData.read(buf, 0, size)) != -1)
                       bos.write(buf, 0, len)
                buf = bos.toByteArray()
                //log.debug "buf: $buf"
                String s = buf?.encodeBase64()
                //log.debug "resp: ${s}"
                return s ? "data:image/${type};base64,${s.toString()}" : null
            }
        }
    }
    catch (ex) {
        log.error "getImgBase64 Exception: $ex"
        parent?.sendChildExceptionData("camera", devVer(), ex.toString(), "getImgBase64")
    }
}

def getImg(imgName) {
    try {
        return imgName ? "https://cdn.rawgit.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : ""
    }
    catch (ex) {
        log.error "getImg Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex.toString(), "getImg")
    }
}

def getCSS(){
    try {
        def params = [
            uri: state?.cssUrl.toString(),
            contentType: 'text/css'
        ]
        httpGet(params)  { resp ->
            return resp?.data.text
        }
    }
    catch (ex) {
        log.error "Failed to load CSS - Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex.toString(), "getCSS")
    }
}

def getLiveStreamHost(camUUID) {
  try {
      def params = [
          uri: "https://www.dropcam.com/api/v1/cameras.get?id=${camUUID}",
      ]
      httpGet(params)  { resp ->
        def stream = (resp?.data?.items.live_stream_host)
        def stream1 = stream.toString().replaceAll("\\[|\\]", "")
        return stream1 ?: null
      }
  }
  catch (ex) {
      log.error "Failed to load camera server - Exception: ${ex}"
      parent?.sendChildExceptionData("camera", devVer(), ex.toString(), "getCSS")
  }
}

def getAPIServer(camUUID) {
  try {
      def params = [
          uri: "https://www.dropcam.com/api/v1/cameras.get?id=${camUUID}",
      ]
      httpGet(params)  { resp ->
        def apiServer = (resp?.data?.items.nexus_api_http_server)
        def apiServer1 = apiServer.toString().replaceAll("\\[|\\]", "")
        return apiServer1 ?: null
      }
  }
  catch (ex) {
      log.error "Failed to load API server - Exception: ${ex}"
      parent?.sendChildExceptionData("camera", devVer(), ex.toString(), "getCSS")
  }
}

def getInfoHtml() {
    try {
        def camUUID = getUUID(getPublicVideoId())
        def apiServer = getAPIServer(camUUID)
        def liveStreamURL = getLiveStreamHost(camUUID)
        def camImgUrl = "${apiServer}/get_image?uuid=${camUUID}&width=410"
        log.debug(camImgUrl)
        def camPlaylistUrl = "https://${liveStreamURL}/nexus_aac/${camUUID}/playlist.m3u8"

        def pubVidUrl = state?.public_share_url
        def pubVidId = getPublicVideoId()

        def pubSnapUrl = getImgBase64(state?.snapshot_url,'jpeg')

        def updateAvail = !state.updateAvailable ? "" : "<h3>Device Update Available!</h3>"

        def html = """
        <!DOCTYPE html>
        <html>
            <head>
                <meta http-equiv="cache-control" content="max-age=0"/>
                <meta http-equiv="cache-control" content="no-cache"/>
                <meta http-equiv="expires" content="0"/>
                <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
                <meta http-equiv="pragma" content="no-cache"/>
                <meta name="viewport" content="width = device-width, initial-scale=1.0">
            </head>
            <body>
                <style type="text/css">
                    ${getCSS()}
                </style>
                ${updateAvail}
                <script type="text/javascript">
                    <!--
                        function toggle_visibility(id) {

                        var e = document.getElementById(id);
                        if(e.style.display == 'block')
                            e.style.display = 'none';
                        else
                            e.style.display = 'block';
                        }
                    //-->
                </script>
                <div class="dual-header" id="primary">
                    <video width="410" controls
                        id="nest-video"
                        class="video-js vjs-default-skin"
                        poster="${camImgUrl}"
                        data-video-url="${pubVidUrl}"
                        data-video-title="">
                        <source src="${camPlaylistUrl}" type="application/x-mpegURL">
                    </video>
                </div>
                <div class="dual-header" id="secondary">
                    <img src="${pubSnapUrl}" width="100%"/>
                </div>
                <a href="#" onclick="toggle_visibility('primary');" class="button">Video</a>
                <a href="#" onclick="toggle_visibility('secondary');" class="button">Picture</a>
                <table>
                <col width="50%">
                <col width="50%">
                <thead>
                  <th>Last Event Start</th>
                  <th>Last Event End</th>
                </thead>
                  <tbody>
                     <tr>
                         <td>${state?.lastEventStartDt.toString()}</td>
                         <td>${state?.lastEventEndDt.toString()}</td>
                     </tr>
                  </tbody>
                </table>

                <table>
                <col width="33%">
                <col width="33%">
                <col width="33%">
                <thead>
                  <th>Public Video</th>
                  <th>Audio Input</th>
                  <th>Video History</th>
                </thead>
                  <tbody>
                     <tr>
                         <td>${state?.publicShareEnabled.toString()}</td>
                         <td>${state?.audioInputEnabled.toString()}</td>
                         <td>${state?.videoHistoryEnabled.toString()}</td>
                     </tr>
                  </tbody>
                </table>

                <table>
                <col width="50%">
                <col width="50%">
                <thead>
                  <th>Network Status</th>
                  <th>API Status</th>
                </thead>
                  <tbody>
                     <tr>
                         <td>${state?.onlineStatus.toString()}</td>
                         <td>${state?.apiStatus}</td>
                     </tr>
                  </tbody>
                </table>

                <p class="centerText">
                    <a href="#openModal" class="button">More info</a>
                </p>
                 <div id="openModal" class="topModal">
                        <div>
                            <a href="#close" title="Close" class="close">X</a>
                  <table>
                    <tr>
                        <th>Firmware Version</th>
                        <th>Debug</th>
                        <th>Device Type</th>
                    </tr>
                    <td>v${state?.softwareVer.toString()}</td>
                    <td>${state?.debugStatus}</td>
                    <td>${state?.devTypeVer.toString()}</td>
                </table>
                <table>
                <thead>
                    <th>Nest Last Checked-In</th>
                    <th>Data Last Received</th>
                </thead>
                <tbody>
                    <tr>
                    <td class="dateTimeText">${state?.lastConnection.toString()}</td>
                    <td class="dateTimeText">${state?.lastUpdatedDt.toString()}</td>
                    </tr>
                </tbody>
                </table>
                </div>
                    </div>
                </div>
            </body>
        </html>
        """
        render contentType: "text/html", data: html, status: 200
    }
    catch (ex) {
        log.error "getInfoHtml Exception: ${ex}"
        parent?.sendChildExceptionData("camera", devVer(), ex.toString(), "getInfoHtml")
    }
}

private def textDevName()   { return "Nest Camera${appDevName()}" }
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
