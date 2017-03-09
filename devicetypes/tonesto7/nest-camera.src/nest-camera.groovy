/**
 *  Nest Cam
 *	Authors: Anthony S. (@tonesto7)
 *	Contributors: Ben W. (@desertblade), Eric S. (@E_Sch)
 *  A Huge thanks goes out to Greg (@ghesp) for all of your help getting this working.
 *
 *	Copyright (C) 2017 Anthony S.
 * 	Licensing Info: Located at https://raw.githubusercontent.com/tonesto7/nest-manager/master/LICENSE.md
 */

import java.text.SimpleDateFormat

preferences { }

def devVer() { return "2.6.1" }

metadata {
	definition (name: "${textDevName()}", author: "Anthony S.", namespace: "tonesto7") {
		capability "Sensor"
		capability "Switch"
		//capability "Motion Sensor"
		//capability "Sound Sensor"
		capability "Refresh"
		capability "Notification"
		capability "Image Capture"
		capability "Video Camera"
		//capability "Video Capture"
		capability "Health Check"

		command "refresh"
		command "poll"
		command "log", ["string","string"]
		command "streamingOn"
		command "streamingOff"
		command "chgStreaming"
		command "cltLiveStreamStart"

		attribute "devVer", "string"
		attribute "softwareVer", "string"
		attribute "lastConnection", "string"
		attribute "lastOnline", "string"
		attribute "lastUpdateDt", "string"
		attribute "activityZoneName", "string"
		attribute "isStreaming", "string"
		attribute "audioInputEnabled", "string"
		attribute "videoHistoryEnabled", "string"
		attribute "minVideoHistoryHours", "string"
		attribute "maxVideoHistoryHours", "string"
		attribute "publicShareEnabled", "string"
		attribute "lastEventStart", "string"
		attribute "lastEventEnd", "string"
		attribute "apiStatus", "string"
		attribute "debugOn", "string"
		attribute "devTypeVer", "string"
		attribute "onlineStatus", "string"
	}

	simulator { }

	tiles(scale: 2) {
		multiAttributeTile(name: "videoPlayer", type: "videoPlayer", width: 6, height: 4) {
			tileAttribute("device.switch", key: "CAMERA_STATUS") {
				attributeState("on", label: "Active", icon: "st.camera.dlink-indoor", action: "switch.off", backgroundColor: "#00A0DC", defaultState: true)
				attributeState("off", label: "Inactive", icon: "st.camera.dlink-indoor", action: "switch.on", backgroundColor: "#ffffff")
				attributeState("restarting", label: "Connecting", icon: "st.camera.dlink-indoor", backgroundColor: "#00A0DC")
				attributeState("unavailable", label: "Unavailable", icon: "st.camera.dlink-indoor", action: "refresh.refresh", backgroundColor: "#cccccc")
			}
			tileAttribute("device.errorMessage", key: "CAMERA_ERROR_MESSAGE") {
				attributeState("errorMessage", label: "", value: "", defaultState: true)
			}
			tileAttribute("device.camera", key: "PRIMARY_CONTROL") {
				attributeState("on", label: "Active", icon: "st.camera.dlink-indoor", backgroundColor: "#00A0DC", defaultState: true)
				attributeState("off", label: "Inactive", icon: "st.camera.dlink-indoor", backgroundColor: "#ffffff")
				attributeState("restarting", label: "Connecting", icon: "st.camera.dlink-indoor", backgroundColor: "#00A0DC")
				attributeState("unavailable", label: "Unavailable", icon: "st.camera.dlink-indoor", backgroundColor: "#cccccc")
			}
			tileAttribute("device.startLive", key: "START_LIVE") {
				attributeState("live", action: "cltLiveStreamStart", defaultState: true)
			}
			tileAttribute("device.stream", key: "STREAM_URL") {
				attributeState("activeURL", defaultState: true)
			}
			/*tileAttribute("device.betaLogo", key: "BETA_LOGO") {
				attributeState("betaLogo", label: "", value: "", defaultState: true)
			}*/
			/*tileAttribute("device.profile", key: "STREAM_QUALITY") {
				attributeState("1", label: "720p", action: "setProfileHD", defaultState: true)
				attributeState("2", label: "h360p", action: "setProfileSDH", defaultState: true)
				attributeState("3", label: "l360p", action: "setProfileSDL", defaultState: true)
			}*/
		}
		standardTile("isStreamingStatus", "device.isStreaming", width: 2, height: 2, decoration: "flat") {
			state("on", label: "Streaming", action: "chgStreaming", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_green_icon.png", backgroundColor: "#79b821")
			state("off", label: "Off", action: "chgStreaming", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_gray_icon.png", backgroundColor: "#ffffff")
			state("updating", label:"", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png")
			state("offline", label: "Offline", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_red_icon.png", backgroundColor: "#F22000")
			state("unavailable", label: "Unavailable", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_red_icon.png", backgroundColor: "#F22000")
		}
		standardTile("isStreaming", "device.isStreaming", width: 2, height: 2, decoration: "flat") {
			state("on", action: "chgStreaming", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_stream_btn_icon.png")
			state("off", action: "chgStreaming", nextState: "updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_off_btn_icon.png")
			state("updating", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png")
			state("offline", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/camera_offline_btn_icon.png")
			state("unavailable", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_offline_btn_icon.png")
		}
		carouselTile("cameraDetails", "device.image", width: 6, height: 4) { }
		standardTile("take", "device.image", width: 2, height: 2, canChangeIcon: false, decoration: "flat", canChangeBackground: false) {
			state "take", action: "Image Capture.take", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_snapshot_icon.png", nextState:"taking"
			state "taking", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png"
			state "image", action: "Image Capture.take", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/camera_snapshot_icon.png", nextState:"taking"
		}
		standardTile("motion", "device.motion", width: 2, height: 2) {
			state "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0"
			state "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
		}
		standardTile("sound", "device.sound", width: 2, height: 2) {
			state "detected", label:'Noise', icon:"st.sound.sound.detected", backgroundColor:"#53a7c0"
			state "not detected", label:'Quiet', icon:"st.sound.sound.notdetected", backgroundColor:"#ffffff"
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
			state "default", action:"refresh.refresh", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/refresh_icon.png"
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
		htmlTile(name:"devCamHtml", action: "getCamHtml", width: 6, height: 9, whitelist: ["raw.githubusercontent.com", "cdn.rawgit.com"])

		standardTile("test", "device.testBtn", width:2, height:2, decoration: "flat") {
			state "default", label: 'Test', action:"testBtn"
		}
		main "isStreamingStatus"
		//details(["devCamHtml", "isStreaming", "take", "refresh", "motion", "cameraDetails", "sound"])
		details(["videoPlayer", "isStreaming", "take", "refresh", "devCamHtml", "cameraDetails" ])
	}
}

mappings {
	path("/getInHomeURL") {action: [GET: "getInHomeURL"]}
	path("/getOutHomeURL") {action: [GET: "getOutHomeURL"]}

	path("/getCamHtml") {action: [GET: "getCamHtml"]}
}

def getInHomeURL() { return [InHomeURL: getCamPlaylistURL().toString()] }
def getOutHomeURL() { return [OutHomeURL: getCamPlaylistURL().toString()] }

def initialize() {
	Logger("initialized...")
	verifyHC()
	//poll()
}

void installed() {
	Logger("installed...")
	initialize()
	state?.isInstalled = true
}

void updated() {
	Logger("updated...")
	initialize()
}

def getHcTimeout() {
	def to = state?.hcTimeout
	return ((to instanceof Integer) ? to.toInteger() : 60)*60
}

void verifyHC() {
	def val = device.currentValue("checkInterval")
	def timeOut = getHcTimeout()
	if(!val || val.toInteger() != timeOut) {
		Logger("verifyHC: Updating Device Health Check Interval to $timeOut")
		sendEvent(name: "checkInterval", value: timeOut, data: [protocol: "cloud"], displayed: false)
	}
	sendEvent(name: "DeviceWatch-Enroll", value: "{\"protocol\": \"CLOUD\", \"scheme\":\"untracked\", \"hubHardwareId\": \"${hub?.hub?.hardwareID}\"}")
}

def ping() {
	Logger("ping...")
	keepAwakeEvent()
}

def parse(String description) {
	LogAction("Parsing '${description}'", "debug")
}

def poll() {
	Logger("polling parent...")
	parent.refresh(this)
}

def refresh() {
	//Logger("refreshing parent...")
	poll()
}

def cltLiveStreamStart() {
	//log.trace "video stream start()"
	def url = getCamPlaylistURL().toString()
	def imgUrl = "http://cdn.device-icons.smartthings.com/camera/dlink-indoor@2x.png"
	//def imgUrl = state?.snapshot_url
	def dataLiveVideo = [OutHomeURL: url, InHomeURL: url, ThumbnailURL: imgUrl, cookie: [key: "key", value: "value"]]
	def evtData = groovy.json.JsonOutput.toJson(dataLiveVideo)
	sendEvent(name: "stream", value: evtData.toString(), data: evtData, descriptionText: "Starting the livestream", eventType: "VIDEO", displayed: false, isStateChange: true)
}

// parent calls this method to queue data.
// goal is to return to parent asap to avoid execution timeouts

def generateEvent(Map eventData) {
	//log.trace("generateEvent Parsing data ${eventData}")
	state.eventData = eventData
	runIn(3, "processEvent", [overwrite: true] )
}

def processEvent() {
	if(state?.swVersion != devVer()) {
		initialize()
		state.swVersion = devVer()
	}
	def eventData = state?.eventData
	state.eventData = null
	//log.trace("processEvent Parsing data ${eventData}")
	try {
		LogAction("------------START OF API RESULTS DATA------------", "warn")
		if(eventData) {
			def results = eventData?.data
			//log.debug "results: $results"
			state.showLogNamePrefix = eventData?.logPrefix == true ? true : false
			state.enRemDiagLogging = eventData?.enRemDiagLogging == true ? true : false
			state.streamMsg = eventData?.streamNotify == true ? true : false
			state.healthMsg = eventData?.healthNotify == true ? true : false
			if(eventData.hcTimeout && (state?.hcTimeout != eventData?.hcTimeout || !state?.hcTimeout)) {
				state.hcTimeout = eventData?.hcTimeout
				verifyHC()
			}
			state?.useMilitaryTime = eventData?.mt ? true : false
			state.clientBl = eventData?.clientBl == true ? true : false
			state.mobileClientType = eventData?.mobileClientType
			state.nestTimeZone = eventData?.tz ?: null

			state?.devBannerMsgData = eventData?.devBannerData ?: null
			publicShareUrlEvent(results?.public_share_url)
			onlineStatusEvent(results?.is_online?.toString())
			isStreamingEvent(results?.is_streaming)
			publicShareEnabledEvent(results?.is_public_share_enabled?.toString())
			videoHistEnabledEvent(results?.is_video_history_enabled?.toString())
			if(!results?.last_is_online_change) { lastCheckinEvent(null) }
			else { lastCheckinEvent(results?.last_is_online_change?.toString()) }
			if(eventData?.htmlInfo) { state?.htmlInfo = eventData?.htmlInfo }
			if(eventData?.allowDbException) { state?.allowDbException = eventData?.allowDbException = false ? false : true }
			apiStatusEvent(eventData?.apiIssues)
			debugOnEvent(eventData?.debug ? true : false)
			audioInputEnabledEvent(results?.is_audio_input_enabled?.toString())
			softwareVerEvent(results?.software_version?.toString())
			if(results?.activity_zones) { state?.activityZones = results?.activity_zones }
			if(results?.snapshot_url) { state?.snapshot_url = results?.snapshot_url?.toString() }
			if(results?.app_url) { state?.app_url = results?.app_url?.toString() }
			if(results?.web_url) { state?.web_url = results?.web_url?.toString() }
			if(results?.last_event) {
				if(results?.last_event.start_time && results?.last_event.end_time) { lastEventDataEvent(results?.last_event) }
				//zoneMotionEvent(results?.last_event)
				//zoneSoundEvent(results?.last_event)
				if(results?.last_event?.activity_zone_ids) { activityZoneEvent(results?.last_event?.activity_zone_ids) }
				if(results?.last_event?.animated_image_url) { state?.animation_url = results?.last_event?.animated_image_url }
			}
			deviceVerEvent(eventData?.latestVer.toString())
			state?.devBannerMsgData = eventData?.devBannerData ?: null
			vidHistoryTimeEvent()
			checkHealth()
			// lastUpdatedEvent()
		}
		return null
	}
	catch (ex) {
		log.error "generateEvent Exception:", ex
		exceptionDataHandler(ex.message, "generateEvent")
	}
}

def getStateSize()      { return state?.toString().length() }
def getStateSizePerc()  { return (int) ((stateSize/100000)*100).toDouble().round(0) }

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
	if(!tz) { Logger("getTimeZone: Hub or Nest TimeZone is not found ...", "warn") }
	return tz
}

def isCodeUpdateAvailable(newVer, curVer) {
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
}

def deviceVerEvent(ver) {
	def curData = device.currentState("devTypeVer")?.value.toString()
	def pubVer = ver ?: null
	def dVer = devVer() ?: null
	def newData = isCodeUpdateAvailable(pubVer, dVer) ? "${dVer}(New: v${pubVer})" : "${dVer}" as String
	state?.devTypeVer = newData
	state?.updateAvailable = isCodeUpdateAvailable(pubVer, dVer)
	// if(!curData?.equals(newData)) {
	if(isStateChange(device, "devVer", dVer.toString())) {
		sendEvent(name: 'devVer', value: dVer, displayed: false)
	}
	if(isStateChange(device, "devTypeVer", newData?.toString())) {
		Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
		sendEvent(name: 'devTypeVer', value: newData, displayed: false)
	} else { LogAction("Device Type Version is: (${newData}) | Original State: (${curData})") }
}

def lastCheckinEvent(checkin) {
	def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
	def tf = new SimpleDateFormat(formatVal)
	tf.setTimeZone(getTimeZone())
	def lastConn = checkin ? tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin.toString())) : "Not Available"
	def lastChk = device.currentState("lastConnection")?.value
	state?.lastConnection = lastConn?.toString()
	//if(!lastChk.equals(lastConn?.toString())) {
	if(isStateChange(device, "lastConnection", lastConn?.toString())) {
		Logger("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
		sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: state?.showProtActEvts, isStateChange: true)
	} else { LogAction("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
}

def lastOnlineEvent(dt) {
	def lastOnlVal = device.currentState("lastOnline")?.value
	def formatVal = state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
	def tf = new SimpleDateFormat(formatVal)
	tf.setTimeZone(getTimeZone())
	def lastOnl = !dt ? "Nothing To Show..." : tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dt.toString()))
	state?.lastOnl = lastOnl
	if(isStateChange(device, "lastOnline", lastOnl?.toString())) {
		Logger("UPDATED | Last Online was: (${lastOnl}) | Original State: (${lastOnlVal})")
		sendEvent(name: 'lastOnline', value: lastOnl, displayed: false, isStateChange: true)
	} else { LogAction("Last Manual Test was: (${lastOnl}) | Original State: (${lastOnlVal})") }
}

def onlineStatusEvent(isOnline) {
	Logger("onlineStatusEvent($isOnline)")
	//if(state?.camApiServerData && state?.camApiServerData?.items?.is_online[0]) { online = state?.camApiServerData?.items?.is_online[0] }
	def prevOnlineStat = device.currentState("onlineStatus")?.value
	def onlineStat = isOnline.toString() == "true" ? "online" : "offline"
	state?.onlineStatus = onlineStat.toString().capitalize()
	state?.isOnline = (onlineStat == "online")
	if(onlineStat == "online") { lastUpdatedEvent(true) }
	//log.debug "onlineStatus: ${state?.isOnline} | onlineStat: $online"
	if(device?.getStatus().toString().toLowerCase() != onlineStat) {
		sendEvent(name: "DeviceWatch-DeviceStatusUpdate", value: onlineStat.toString(), displayed: false)
		sendEvent(name: "DeviceWatch-DeviceStatus", value: onlineStat.toString(), displayed: false)
		Logger("Device Health Status: ${device.getStatus()}")
	}
	if(isStateChange(device, "onlineStatus", onlineStat.toString().capitalize())) {
		Logger("UPDATED | Online Status is: (${onlineStat}) | Original State: (${prevOnlineStat})")
		sendEvent(name: "onlineStatus", value: onlineStat.toString(), descriptionText: "Online Status is: ${onlineStat}", displayed: true, isStateChange: true, state: onlineStat)
	} else { LogAction("Online Status is: (${onlineStat}) | Original State: (${prevOnlineStat})") }
}

def isStreamingEvent(isStreaming, override=false) {
	//log.trace "isStreamingEvent($isStreaming)..."
	def isOn = device.currentState("isStreaming")?.value
	def isOnline = device.currentState("onlineStatus")?.value
	//log.debug "isStreamingEvent: ${isStreaming} | CamData: ${state?.camApiServerData?.items?.is_streaming[0]}"
	if(override) { state?.camApiServerData = null }
	else { if(state?.camApiServerData && state?.camApiServerData?.items?.is_streaming[0]) { isStreaming = state?.camApiServerData?.items?.is_streaming[0] } }
	def val = (isStreaming.toString() == "true") ? "on" : (isOnline.toString() != "online" ? "offline" : "off")
	state?.isStreaming = (val == "on") ? true : false
	if(isStateChange(device, "isStreaming", val.toString())) {
		Logger("UPDATED | Camera Live Video Streaming is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "isStreaming", value: val, descriptionText: "Camera Live Video Streaming is: ${val}", displayed: true, isStateChange: true, state: val)
		sendEvent(name: "switch", value: (val == "on" ? val : "off"))
		cameraStreamNotify(state?.isStreaming)
	} else { LogAction("Camera Live Video Streaming is: (${val}) | Original State: (${isOn})") }
}

def audioInputEnabledEvent(on) {
	def isOn = device.currentState("audioInputEnabled")?.value
	def val = (on.toString() == "true") ? "Enabled" : "Disabled"
	state?.audioInputEnabled = val
	if(isStateChange(device, "audioInputEnabled", val.toString())) {
		Logger("UPDATED | Audio Input Status is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "audioInputEnabled", value: val, descriptionText: "Audio Input Status is: ${val}", displayed: true, isStateChange: true, state: val)
	} else { LogAction("Audio Input Status is: (${val}) | Original State: (${isOn})") }
}

def videoHistEnabledEvent(on) {
	def isOn = device.currentState("videoHistoryEnabled")?.value
	def val = (on.toString() == "true") ? "Enabled" : "Disabled"
	state?.videoHistoryEnabled = val
	if(isStateChange(device, "videoHistoryEnabled", val.toString())) {
		Logger("UPDATED | Video History Status is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "videoHistoryEnabled", value: val, descriptionText: "Video History Status is: ${val}", displayed: true, isStateChange: true, state: val)
	} else { LogAction("Video History Status is: (${val}) | Original State: (${isOn})") }
}

def publicShareEnabledEvent(on) {
	def isOn = device.currentState("publicShareEnabled")?.value
	def val = on ? "Enabled" : "Disabled"
	state?.publicShareEnabled = val
	if(isStateChange(device, "publicShareEnabled", val.toString())) {
		Logger("UPDATED | Public Sharing Status is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "publicShareEnabled", value: val, descriptionText: "Public Sharing Status is: ${val}", displayed: true, isStateChange: true, state: val)
	} else { LogAction("Public Sharing Status is: (${val}) | Original State: (${isOn})") }
}

def softwareVerEvent(ver) {
	def verVal = device.currentState("softwareVer")?.value
	state?.softwareVer = ver
	if(isStateChange(device, "softwareVer", ver.toString())) {
		Logger("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
		sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now v${ver}", displayed: false)
	} else { LogAction("Firmware Version: (${ver}) | Original State: (${verVal})") }
}

def lastEventDataEvent(data) {
	//log.trace "lastEventDataEvent($data)"
	def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
		tf.setTimeZone(getTimeZone())
	def curStartDt = device?.currentState("lastEventStart")?.value ? tf?.format(Date.parse("E MMM dd HH:mm:ss z yyyy", device?.currentState("lastEventStart")?.value.toString())) : null
	def curEndDt = device?.currentState("lastEventEnd")?.value ? tf?.format(Date.parse("E MMM dd HH:mm:ss z yyyy", device?.currentState("lastEventEnd")?.value.toString())) : null
	def newStartDt = data?.start_time ? tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time.toString())) : "Not Available"
	def newEndDt = data?.end_time ? tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time.toString())) : "Not Available"

	def hasPerson = data?.has_person ? data?.has_person?.toBoolean() : false
	def hasMotion = data?.has_motion ? data?.has_motion?.toBoolean() : false
	def hasSound = data?.has_sound ? data?.has_sound?.toBoolean() : false

	//log.debug "curStartDt: $curStartDt | curEndDt: $curEndDt || newStartDt: $newStartDt | newEndDt: $newEndDt"

	state.lastEventDate = formatDt2(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time.toString()), "MMMMM d, yyyy").toString()
	state.lastEventTime = "${formatDt2(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time.toString()), "h:mm:ssa")} to ${formatDt2(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time.toString()), "h:mm:ssa")}"
	if(state?.lastEventData) { state.lastEventData == null }

	if(!state?.lastCamEvtData || (curStartDt != newStartDt || curEndDt != newEndDt) && (hasPerson || hasMotion || hasSound)) {
		Logger("UPDATED | Last Event Start Time: (${newStartDt}) | Original State: (${curStartDt})")
		sendEvent(name: 'lastEventStart', value: newStartDt, descriptionText: "Last Event Start is ${newStartDt}", displayed: false)
		Logger("UPDATED | Last Event End Time: (${newEndDt}) | Original State: (${curEndDt})")
		sendEvent(name: 'lastEventEnd', value: newEndDt, descriptionText: "Last Event End is ${newEndDt}", displayed: false)
		state.lastCamEvtData = ["startDt":newStartDt, "endDt":newEndDt, "hasMotion":hasMotion, "hasSound":hasSound, "hasPerson":hasPerson, "actZones":(data?.activity_zone_ids ?: null)]
	} else {
		LogAction("Last Event Start Time: (${newStartDt}) | Original State: (${curStartDt})")
		LogAction("Last Event End Time: (${newEndDt}) | Original State: (${curEndDt})")
	}
}

def zoneMotionEvent(data) {
	def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
		tf.setTimeZone(getTimeZone())
	def nowDt = tf.format(new Date())
	def isMotion = device.currentState("motion")?.stringValue
	def isBtwn = false
	if(data?.start_time && data?.end_time) {
		def newStartDt = tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time.toString())) ?: "Not Available"
		def newEndDt = tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time.toString())) ?: "Not Available"
		isBtwn = (newStartDt && newEndDt) ? false :  isTimeBetween(newStartDt, newEndDt, nowDt, getTimeZone())
	}
	def val = ((data?.has_motion == "true") && isBtwn) ? "active" : "inactive"
	if(isStateChange(device, "motion", val.toString())) {
		Logger("UPDATED | Motion Sensor is: (${val}) | Original State: (${isMotion})")
		sendEvent(name: "motion", value: val, descriptionText: "Motion Sensor is: ${val}", displayed: true, isStateChange: true, state: val)
	} else { LogAction("Motion Sensor is: (${val}) | Original State: (${isMotion})") }
}

def zoneSoundEvent(data) {
	def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
		tf.setTimeZone(getTimeZone())
	def nowDt = tf.format(new Date())
	def isSound = device.currentState("sound")?.stringValue
	def isBtwn = false
	if(data?.start_time && data?.end_time) {
		def newStartDt = tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.start_time.toString())) ?: "Not Available"
		def newEndDt = tf.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", data?.end_time.toString())) ?: "Not Available"
		isBtwn = (newStartDt && newEndDt) ? false :  isTimeBetween(newStartDt, newEndDt, nowDt, getTimeZone())
	}
	def val = ((date?.has_sound == "true") && isBtwn) ? "detected" : "not detected"
	if(isStateChange(device, "sound", val.toString())) {
		Logger("UPDATED | Sound Sensor is: (${val}) | Original State: (${isSound})")
		sendEvent(name: "sound", value: val, descriptionText: "Sound Sensor is: ${val}", displayed: true, isStateChange: true, state: val)
	} else { LogAction("Sound Sensor is: (${val}) | Original State: (${isSound})") }
}

def activityZoneEvent(zones) {
	//log.trace "activityZoneEvent($zones)..."
}

def debugOnEvent(debug) {
	def val = device.currentState("debugOn")?.value
	def dVal = debug ? "On" : "Off"
	state?.debugStatus = dVal
	state?.debug = debug.toBoolean() ? true : false
	if(isStateChange(device, "debugOn", dVal.toString())) {
		Logger("UPDATED | Device Debug Logging is: (${dVal}) | Original State: (${val})")
		sendEvent(name: 'debugOn', value: dVal, displayed: false)
	} else { LogAction("Device Debug Logging is: (${dVal}) | Original State: (${val})") }
}

def apiStatusEvent(issue) {
	def curStat = device.currentState("apiStatus")?.value
	def newStat = issue ? "Issues" : "Ok"
	state?.apiStatus = newStat
	if(isStateChange(device, "apiStatus", newStat.toString())) {
		Logger("UPDATED | API Status is: (${newStat}) | Original State: (${curStat})")
		sendEvent(name: "apiStatus", value: newStat, descriptionText: "API Status is: ${newStat}", displayed: true, isStateChange: true, state: newStat)
	} else { LogAction("API Status is: (${newStat}) | Original State: (${curStat})") }
}

def lastUpdatedEvent(sendEvt=false) {
	def now = new Date()
	def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
	def tf = new SimpleDateFormat(formatVal)
		tf.setTimeZone(getTimeZone())
	def lastDt = "${tf?.format(now)}"
	state?.lastUpdatedDt = lastDt?.toString()
	state?.lastUpdatedDtFmt = formatDt(now)
	if(sendEvt && state?.isOnline) {
		LogAction("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
		sendEvent(name: 'lastUpdatedDt', value: formatDt(now)?.toString(), displayed: false, isStateChange: true)
	}
}

def keepAwakeEvent() {
	def lastDt = state?.lastUpdatedDtFmt
	if(lastDt) {
		def ldtSec = getTimeDiffSeconds(lastDt)
		log.debug "ldtSec: $ldtSec"
		if(ldtSec < 1900) {
			lastUpdatedEvent(true)
		} else { refresh() }
	} else { refresh() }
}

def vidHistoryTimeEvent() {
	if(!state?.camApiServerData) { return }
	def camData = state?.camApiServerData
	def newMin = (camData?.items?.hours_of_free_tier_history[0] > 3 ? camData?.items?.hours_of_free_tier_history[0] : 3)
	def newMax = (camData?.items?.hours_of_recording_max[0] > 3 ? camData?.items?.hours_of_recording_max[0] : 3)
	def curMin = device.currentState("minVideoHistoryHours")?.value
	def curMax = device.currentState("maxVideoHistoryHours")?.value
	state?.minVideoHistoryHours = newMin
	state?.maxVideoHistoryHours = newMax
	if(isStateChange(device, "minVideoHistoryHours", newMin.toString()) || isStateChange(device, "maxVideoHistoryHours", newMax.toString())) {
		Logger("UPDATED | Video Recording History Hours is Now: (Minimum: ${newMin} hours | Maximum: ${newMax} hours) | Original State: (Minimum: ${curMin} | Maximum: ${curMax})")
		sendEvent(name: "minVideoHistoryHours", value: newMin, descriptionText: "Minimum Video Recording History Hours is Now: (${newMin} hours)", displayed: false, isStateChange: true, state: newMin)
		sendEvent(name: "maxVideoHistoryHours", value: newMax, descriptionText: "Maximum Video Recording History Hours is Now: (${newMax} hours)", displayed: false, isStateChange: true, state: newMax)
	} else { LogAction("Video Recording History Hours is Now: (Minimum: ${newMin} hours | Maximum: ${newMax} hours) | Original State: (Minimum: ${curMin} | Maximum: ${curMax})") }
}

def publicShareUrlEvent(url) {
	//log.trace "publicShareUrlEvent($url)"
	if(url) {
		if(!state?.public_share_url || state?.public_share_url != url) { state?.public_share_url = url }
		def pubVidId = getPublicVidID()
		def lastVidId = state?.lastPubVidId
		//log.debug "Url: $url | Url(state): ${state?.public_share_url} | pubVidId: $pubVidId | lastVidId: $lastVidId | camUUID: ${state?.camUUID}"
		if(lastVidId == null || lastVidId.toString() != pubVidId.toString()) {
			state?.public_share_url = url
			state?.lastPubVidId = pubVidId
		}
		if(!state?.camUUID) {
			getCamUUID(pubVidId)
		} else {
			def camData = getCamApiServerData(state?.camUUID)
			if(camData && state?.lastCamApiServerData != camData) { state?.lastCamApiServerData = camData }
		}
	} else {
		if(state?.public_share_url || state?.pubVidId || state?.lastPubVidId || state?.camUUID || state?.camApiServerData || state?.animation_url || state?.snapshot_url) {
			state?.public_share_url = null
			state?.pubVidId = null
			state?.lastPubVidId = null
			state?.camUUID = null
			state?.camApiServerData = null
			state?.animation_url = null
			state?.snapshot_url = null
			Logger("Cleared Cached Camera State Info because Sharing has been disabled or the Public ID is no longer available...", "warn")
		}
	}
}

def getPublicVidID() {
	def id = null
	if(!state?.pubVidId && state?.public_share_url) {
		id = state?.public_share_url.tokenize('/')[3].toString()
		state?.pubVidId = id
	} else {
		id = state?.pubVidId
	}
	return id
}

def getRecTimeDesc(val) {
	def result = null
	if(val && val instanceof Integer) {
		if(val.toInteger() > 24) {
			def nVal = (val/24).toDouble().round(0)
			result = "${nVal.toInteger()} days"
		} else {
			result = "${val} hours"
		}
	}
	return result
}

def sendNofificationMsg(msg, msgType, recips = null, sms = null, push = null) {
	if(msg && msgType) { parent?.sendMsg(msg, msgType, recips, sms, push) }
}

def cameraStreamNotify(streaming) {
	if(streaming == null || state?.streamMsg != true) { return }
	parent?.cameraStreamNotify(this, streaming)
}

def getHealthStatus() {
	return device?.getStatus()
}

def checkHealth() {
	def isOnline = (getHealthStatus() == "ONLINE") ? true : false
	if(isOnline || state?.healthMsg != true) { return }
	parent?.deviceHealthNotify(this, isOnline)
}

/************************************************************************************************
|									DEVICE COMMANDS     										|
*************************************************************************************************/
void chgStreaming() {
	def cur = device?.currentState("isStreaming")?.value.toString()
	if(cur == "on" || cur == "unavailable" || !cur) {
		streamingOff(true)
	} else {
		streamingOn(true)
	}
}

void streamingOn(manChg=false) {
	try {
		log.trace "streamingOn..."
		if(parent?.setCamStreaming(this, "true")) {
			isStreamingEvent(true, true)
			if(manChg) { incManStreamChgCnt() }
			else { incProgStreamChgCnt() }
		}

	} catch (ex) {
		log.error "streamingOn Exception:", ex
		exceptionDataHandler(ex.message, "streamingOn")
	}
}

void streamingOff(manChg=false) {
	try {
		log.trace "streamingOff..."
		if(parent?.setCamStreaming(this, "false")) {
			isStreamingEvent(false, true)
			if(manChg) { incManStreamChgCnt() }
			else { incProgStreamChgCnt() }
		}
	} catch (ex) {
		log.error "streamingOff Exception:", ex
		exceptionDataHandler(ex.message, "streamingOff")
	}
}

void on() {
	streamingOn()
}

void off() {
	streamingOff()
}

void take() {
	takePicture()
}

private getPictureName() {
	def pictureUuid = java.util.UUID.randomUUID().toString().replaceAll('-', '')
	getCamUUID(getPublicVidID()) + "_$pictureUuid" + ".jpg"
}

private getImageWidth() {
	return 1280
}

private takePicture() {
	try {
		if(state?.isOnline) {
			def imageBytes
			def params = [
				uri: state?.snapshot_url,
				requestContentType: "application/x-www-form-urlencoded"
			]
			httpGet(params) { resp ->
				imageBytes = resp?.data
				if (imageBytes) {
					storeImage(getPictureName(), imageBytes)
					return true
				}
			}
		}
	} catch (ex) {
		log.error "takePicture Exception: $ex"
		exceptionDataHandler(ex.message, "takePicture")
	}
	return false
}

/************************************************************************************************
|							EXCEPTION HANDLING & LOGGING FUNCTIONS								|
*************************************************************************************************/

void Logger(msg, logType = "debug") {
	def smsg = state?.showLogNamePrefix ? "${device.displayName}: ${msg}" : "${msg}"
	switch (logType) {
		case "trace":
			log.trace "${smsg}"
			break
		case "debug":
			log.debug "${smsg}"
			break
		case "info":
			log.info "${smsg}"
			break
		case "warn":
			log.warn "${smsg}"
			break
		case "error":
			log.error "${smsg}"
			break
		default:
			log.debug "${smsg}"
			break
	}
	if(state?.enRemDiagLogging) {
		parent.saveLogtoRemDiagStore(smsg, logType, "Camera DTH")
	}
}

// Local Application Logging
void LogAction(msg, logType = "debug") {
	if(state?.debug) {
		Logger(msg, logType)
	}
}

 //This will Print logs from the parent app when added to parent method that the child calls
def log(message, level = "trace") {
	def smsg = "PARENT_Log>> " + message
	LogAction(smsg, level)
	return null // always child interface call with a return value
}

def exceptionDataHandler(msg, methodName) {
	if(state?.allowDbException == false) {
		return
	} else {
		if(msg && methodName) {
			def msgString = "${msg}"
			parent?.sendChildExceptionData("camera", devVer(), msgString, methodName)
		}
	}
}

def incHtmlLoadCnt() 		{ state?.htmlLoadCnt = (state?.htmlLoadCnt ? state?.htmlLoadCnt.toInteger()+1 : 1) }
def incManStreamChgCnt() 	{ state?.manStreamChgCnt = (state?.manStreamChgCnt ? state?.manStreamChgCnt.toInteger()+1 : 1) }
def incProgStreamChgCnt() 	{ state?.progStreamChgCnt = (state?.progStreamChgCnt ? state?.progStreamChgCnt.toInteger()+1 : 1) }
def incVideoBtnTapCnt()		{ state?.videoBtnTapCnt = (state?.videoBtnTapCnt ? state?.videoBtnTapCnt.toInteger()+1 : 1); return ""; }
def incImageBtnTapCnt()		{ state?.imageBtnTapCnt = (state?.imageBtnTapCnt ? state?.imageBtnTapCnt.toInteger()+1 : 1); return ""; }
def incEventBtnTapCnt()		{ state?.eventBtnTapCnt = (state?.eventBtnTapCnt ? state?.eventBtnTapCnt.toInteger()+1 : 1); return ""; }
def incInfoBtnTapCnt()		{ state?.infoBtnTapCnt = (state?.infoBtnTapCnt ? state?.infoBtnTapCnt.toInteger()+1 : 1); return ""; }

def getMetricCntData() {
	return 	[
		camManStrChgCnt:(state?.manStreamChgCnt ?: 0), camProgStrChgCnt:(state?.progStreamChgCnt ?: 0), camHtmlLoadedCnt:(state?.htmlLoadCnt ?: 0)//,
		//camVidBtnTapCnt:(state?.videoBtnTapCnt ?: 0), camImgBtnTapCnt:(state?.imageBtnTapCnt ?: 0), camEvtBtnTapCnt:(state?.eventBtnTapCnt ?: 0),
		//camInfoBtnCnt:(state?.infoBtnTapCnt ?: 0)
	]
}

/************************************************************************************************
|										OTHER METHODS     										|
*************************************************************************************************/

def getDtNow() {
	def now = new Date()
	return formatDt(now)
}

def formatDt(dt, mdy = false) {
	//log.trace "formatDt($dt, $mdy)..."
	def formatVal = mdy ? (state?.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a") : "E MMM dd HH:mm:ss z yyyy"
	def tf = new SimpleDateFormat(formatVal)
	if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
	else {
		Logger("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...")
	}
	return tf.format(dt)
}

def formatDt2(dt, fmt=null) {
	//log.trace "formatDt($dt, $mdy)..."
	def tf = new SimpleDateFormat(fmt)
	if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
	else {
		Logger("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...")
	}
	return tf.format(dt)
}

def epochToTime(tm) {
	def tf = new SimpleDateFormat("h:mm a")
		tf?.setTimeZone(getTimeZone())
	return tf.format(tm)
}

def isTimeBetween(start, end, now, tz) {
	def startDt = Date.parse("E MMM dd HH:mm:ss z yyyy", start).getTime()
	def endDt = Date.parse("E MMM dd HH:mm:ss z yyyy", end).getTime()
	def nowDt = Date.parse("E MMM dd HH:mm:ss z yyyy", now).getTime()
	def result = false
	if(nowDt > startDt && nowDt < endDt) {
		result = true
	}
	//def result = timeOfDayIsBetween(startDt, endDt, nowDt, tz) ? true : false
	return result
}

def getImgBase64(url,type) {
	def params = [
		uri: url,
		contentType: 'image/$type'
	]
	httpGet(params) { resp ->
		if(resp.data) {
			def respData = resp?.data
			ByteArrayOutputStream bos = new ByteArrayOutputStream()
			int len
			int size = 3072
			byte[] buf = new byte[size]
			while ((len = respData.read(buf, 0, size)) != -1)
				bos.write(buf, 0, len)
			buf = bos.toByteArray()
			String s = buf?.encodeBase64()
			return s ? "data:image/${type};base64,${s.toString()}" : null
		}
	}
}

def getFileBase64(url,preType,fileType) {
	def params = [
		uri: url,
		contentType: '$preType/$fileType'
	]
	httpGet(params) { resp ->
		if(resp.data) {
			def respData = resp?.data
			ByteArrayOutputStream bos = new ByteArrayOutputStream()
			int len
			int size = 4096
			byte[] buf = new byte[size]
			while ((len = respData.read(buf, 0, size)) != -1)
				bos.write(buf, 0, len)
			buf = bos.toByteArray()
			//log.debug "buf: $buf"
			String s = buf?.encodeBase64()
			//log.debug "resp: ${s}"
			return s ? "data:${preType}/${fileType};base64,${s.toString()}" : null
		}
	}
}

def getImg(imgName) {
	return imgName ? "https://cdn.rawgit.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : ""
}

def getCSS(url = null){
	def params = [
		uri: (!url ? cssUrl() : url?.toString()),
		contentType: "text/css"
	]
	httpGet(params)  { resp ->
		return resp?.data.text
	}
}

def getJS(url){
	def params = [
		uri: url.toString(),
		contentType: 'text/javascript'
	]
	httpGet(params)  { resp ->
		//log.debug "JS Resp: ${resp?.data}"
		return resp?.data.text
	}
}

def getCssData() {
	def cssData = null
	def htmlInfo = state?.htmlInfo
	state?.cssData = null
	if(htmlInfo?.cssUrl && htmlInfo?.cssVer) {
		//LogAction("getCssData: CSS Data is Missing | Loading Data from Source...")
		cssData = getFileBase64(htmlInfo.cssUrl, "text", "css")
		state?.cssData = cssData
		state?.cssVer = htmlInfo?.cssVer
	} else {
		//LogAction("getCssData: No Stored CSS Data Found for Device... Loading for Static URL...")
		cssData = getFileBase64(cssUrl(), "text", "css")
	}
	return cssData
}

def cssUrl() { return "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/css/ST-HTML.min.css" }

//this scrapes the public nest cam page for its unique id for using in render html tile
include 'asynchttp_v1' //<<<<<This is currently in Beta

def getCamUUID(pubVidId) {
	try {
		if(pubVidId) {
			if(!state?.isStreaming) {
				Logger("getCamUUID: Your Camera's currently not streaming so we are unable to get the required ID.  Once streaming is enabled the ID will be collected...", "warn")
				return null
			}
			else if(state?.isStreaming && (state?.lastPubVidId == null || state?.camUUID == null || state?.lastPubVidId != pubVidId)) {
				def params = [
					uri: "https://video.nest.com/live/${pubVidId}",
					requestContentType: 'text/html'
				]
				asynchttp_v1.get('camPageHtmlRespMethod', params)
			} else {
				return state?.camUUID
			}
		} else { Logger("getCamUUID: Your Camera's PublicVideoId was not found!!! Please make sure you have public video sharing enabled under your Cameras settings in the Nest Mobile App...", "warn") }
	} catch (ex) {
		log.error "getCamUUID Exception: $ex"
		exceptionDataHandler(ex.message, "getCamUUID")
	}
}

def camPageHtmlRespMethod(response, data) {
	//log.debug "camPageHtmlRespMethod: ${response?.status}, ${response.getData()}"
	if(response?.status != 408) {
		def rData = response.getData()
		log.debug
		def url = (rData =~ /<meta.*property="og:image".*content="(.*)".*/)[0][1]
		// log.debug "url: $url"
		def uuid = (url =~ /(\?|\&)([^=]+)\=([^&]+)/)[0][3]
		// log.debug "UUID: ${uuid}"
		state.camUUID = uuid
	} else { return }
}

def getCamApiServerData(camUUID) {
	try {
		if(camUUID) {
			def params = [
				uri: "https://www.dropcam.com/api/v1/cameras.get?id=${camUUID}"
			]
			httpGet(params)  { resp ->
				//log.debug "resp: (status: ${resp?.status}) | data: ${resp?.data}"
				state?.camApiServerData = resp?.data
				return resp?.data ?: null
			}
		} else { Logger("getCamApiServerData camUUID is missing....", "warn") }
	}
	catch (ex) {
		log.error "getCamApiServerData Exception:", ex
		exceptionDataHandler(ex.message, "getCamApiServerData")
	}
}

def getStreamHostUrl() {
	if(!state?.camApiServerData) { return null }
	def res = state?.camApiServerData?.items?.live_stream_host
	def data = res.toString().replaceAll("\\[|\\]", "")
	//log.debug "getStreamHostUrl: $data"
	return data ?: null
}

def getCamPlaylistURL() {
	def hUrl = getStreamHostUrl()
	if(hUrl && state?.camUUID) { return "https://${hUrl}/nexus_aac/${state?.camUUID}/playlist.m3u8" }
	return null
}

def getCamApiServer() {
	if(!state?.camApiServerData) { return null }
	def res = state?.camApiServerData?.items?.nexus_api_http_server
	def data = res.toString().replaceAll("\\[|\\]", "")
	//log.debug "getCamApiServer: $data"
	return data ?: null
}

def getCamHtml() {
	try {
		// These are used to determine the URL for the nest cam stream
		def updateAvail = !state.updateAvailable ? "" : """<div class="greenAlertBanner">Device Update Available!</div>"""
		def clientBl = state?.clientBl ? """<div class="brightRedAlertBanner">Your Manager client has been blacklisted!\nPlease contact the Nest Manager developer to get the issue resolved!!!</div>""" : ""
		def pubVidUrl = state?.public_share_url
		def camHtml = (pubVidUrl && state?.camUUID && state?.isStreaming && state?.isOnline) ? showCamHtml() : hideCamHtml()

		//def devBrdCastData = state?.brdcastData ?: null

		def mainHtml = """
		<!DOCTYPE html>
		<html>
			<head>
				<meta charset="utf-8"/>
				<meta http-equiv="cache-control" content="max-age=0"/>
				<meta http-equiv="cache-control" content="no-cache"/>
				<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
				<meta http-equiv="pragma" content="no-cache"/>
				<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0">
				<link rel="stylesheet" href="${getCssData()}"/>
				<link rel="stylesheet" href="${getFileBase64("https://cdnjs.cloudflare.com/ajax/libs/Swiper/3.4.1/css/swiper.min.css", "text", "css")}" />

				<script src="${getFileBase64("https://cdnjs.cloudflare.com/ajax/libs/Swiper/3.4.1/js/swiper.min.js", "text", "javascript")}"></script>
				<script src="${getFileBase64("https://cdnjs.cloudflare.com/ajax/libs/jquery/3.1.1/jquery.js", "text", "javascript")}"></script>
				<style>
				</style>
			</head>
			<body>
				${clientBl}
				${updateAvail}
				<div class="swiper-container">
					<!-- Additional required wrapper -->
					<div class="swiper-wrapper">
						${camHtml}
						<div class="swiper-slide">
						  <section class="sectionBg">
							<h3>Device Info</h3>
							<table class="devInfo">
							  <col width="40%">
							  <col width="40%">
							  <thead>
								<th>Network Status</th>
								<th>API Status</th>
							  </thead>
							  <tbody>
								<tr>
								  <td>${state?.onlineStatus.toString().capitalize()}</td>
								  <td>${state?.apiStatus}</td>
								</tr>
							  </tbody>
							</table>
						  </section>

						  <section class="sectionBg">
							<table class="devInfo">
							  <col width="50%">
							  <col width="50%">
								<thead>
								  <th>Video History (Min.)</th>
								  <th>Video History (Max.)</th>
								</thead>
								<tbody>
								  <tr>
									<td>${getRecTimeDesc(state?.minVideoHistoryHours) ?: "Not Available"}</td>
									<td>${getRecTimeDesc(state?.maxVideoHistoryHours) ?: "Not Available"}</td>
								  </tr>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBg">
							<table class="devInfo">
							  <col width="33%">
							  <col width="33%">
							  <col width="33%">
							  <thead>
								<th>Public Video</th>
								<th>Mic Status</th>
							  </thead>
							  <tbody>
								<tr>
								  <td>${state?.publicShareEnabled.toString()}</td>
								  <td>${state?.audioInputEnabled.toString()}</td>
								</tr>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBg">
							<table class="devInfo">
							  <col width="40%">
							  <col width="20%">
							  <col width="40%">
							  <thead>
								<th>FW Version</th>
								<th>Debug</th>
								<th>Device Type</th>
							  </thead>
							  <tbody>
								  <td>v${state?.softwareVer.toString()}</td>
								  <td>${state?.debugStatus}</td>
								  <td>${state?.devTypeVer.toString()}</td>
							  </tbody>
							</table>
						  </section>
						  <section class="sectionBg">
							<table class="devInfo">
							   <thead>
								 <th>Last Online Change</th>
								 <th>Data Last Received</th>
							   </thead>
							   <tbody>
								 <tr>
								   <td class="dateTimeText">${state?.lastConnection.toString()}</td>
								   <td class="dateTimeText">${state?.lastUpdatedDt.toString()}</td>
								 </tr>
							   </tbody>
							 </table>
						   </section>
						</div>
					</div>
					<div class="swiper-pagination"></div>
					<div style="text-align: center;">
						<p class="slideFooterMsg">Swipe/Tap to Change Slide</p>
					</div>
				</div>
				<script>
					var mySwiper = new Swiper ('.swiper-container', {
						direction: 'horizontal',
						lazyLoading: true,
						loop: true,
						slidesPerView: '1',
						centeredSlides: true,
						spaceBetween: 100,
						autoHeight: true,
						iOSEdgeSwipeDetection: true,
						parallax: true,
						slideToClickedSlide: true,

						effect: 'coverflow',
						coverflow: {
						  rotate: 50,
						  stretch: 0,
						  depth: 100,
						  modifier: 1,
						  slideShadows : true
						},
						onTap: (swiper, event) => {
							let element = event.target;
							swiper.slideNext()
						},
						pagination: '.swiper-pagination',
						paginationHide: false,
						paginationClickable: true
					})
					function reloadCamPage() {
					    var url = "https://" + window.location.host + "/api/devices/${device?.getId()}/getCamHtml"
					    window.location = url;
					}
				</script>
				<div class="pageFooterBtn">
				    <button type="button" class="btn btn-info pageFooterBtn" onclick="reloadCamPage()">
					  <span>&#10227;</span> Refresh
				    </button>
				</div>
			</body>
		</html>
		"""
		incHtmlLoadCnt()
		render contentType: "text/html", data: mainHtml, status: 200
	}
	catch (ex) {
		log.error "getCamHtml Exception:", ex
		exceptionDataHandler(ex.message, "getCamHtml")
	}
}

def showCamHtml() {
	def pubVidUrl = state?.public_share_url
	if(!state?.camUUID) { getCamUUID(getPublicVidID()) }
	def camUUID = state?.camUUID
	if(camUUID && state?.camApiServerData == null) { getCamApiServerData(camUUID) }

	def apiServer = getCamApiServer()

	def liveStreamURL = getStreamHostUrl()
	def camImgUrl = "${apiServer}/get_image?uuid=${camUUID}&width=410"
	def camPlaylistUrl = "https://${liveStreamURL}/nexus_aac/${camUUID}/playlist.m3u8"

	def animationUrl = state?.animation_url ? getImgBase64(state?.animation_url, 'gif') : null
	def pubSnapUrl = state?.snapshot_url ? getImgBase64(state?.snapshot_url,'jpeg') : null

	def vidBtn = (!state?.isStreaming || !liveStreamURL) ? "" : """<a href="#" onclick="toggle_visibility('liveStream');" class="button yellow">Live Video</a>"""
	def imgBtn = (!state?.isStreaming || !pubSnapUrl) ? "" : """<a href="#" onclick="toggle_visibility('still');" class="button blue">Still Image</a>"""
	def lastEvtBtn = (!state?.isStreaming || !animationUrl) ? "" : """<a href="#" onclick="toggle_visibility('animation');" class="button red">Last Event</a>"""

	def data = """
		<div class="swiper-slide">
			<div style="padding: 5px;">
				<section class="sectionBg">
					<h3>Last Camera Event</h3>
					<table class="devInfo">
					  <tbody>
						<tr>
						  <td>${state?.lastEventDate ?: "Not Available"}</td>
						  <td>${state?.lastEventTime ?: ""}</td>
						</tr>
					  </tbody>
					</table>
					<img src="${animationUrl}" width="100%"/>
					<table class="devInfo">
					  <col width="33%">
					  <col width="33%">
					  <col width="33%">
					  <thead>
						<th>Had Person?</th>
						<th>Had Motion?</th>
						<th>Had Sound?</th>
					  </thead>
					  <tbody>
						<tr>
						  <td>${state?.lastCamEvtData?.hasPerson.toString().capitalize() ?: "False"}</td>
						  <td>${state?.lastCamEvtData?.hasMotion.toString().capitalize() ?: "False"}</td>
						  <td>${state?.lastCamEvtData?.hasSound.toString().capitalize() ?: "False"}</td>
						</tr>
					  </tbody>
					</table>
				</section>
			</div>
		</div>
		<div class="swiper-slide">
			<section class="sectionBg">
				<h3>Still Image</h3>
				<table class="devInfo">
				  <tbody>
					<tr>
					  <td>
					  	<img src="${pubSnapUrl}" width="100%"/>
					  	<h4 style="background: #696969; color: #f5f5f5; padding: 4px;">FYI: This image is only refreshed when this window is generated...</h4>
					  </td>
					</tr>
				  </tbody>
				</table>
			</section>
		</div>
	"""
}

def hideCamHtml() {
	def tClass = 'style="background-color: #bd2828;"'
	def bClass = 'style="background-color: transparent; color: #bd2828;  text-shadow: 0px 0px 0px #bd2828;"'
	def d = """<div class="swiper-slide"><section class="sectionBg">"""
	if(!state?.isStreaming && state?.isOnline) {
		d += """<h3 ${tClass}>Live video streaming is Off</h3><br><h3 ${bClass}>Please Turn it back on and refresh this page...</h3>"""
	}
	else if(!state?.camUUID) {
		d += """<h3 ${tClass}>Camera ID Not Found...</h3><br><h3 ${bClass}>If this is your first time opening this device then try refreshing the page.</h3><br>
			<h3 ${bClass}>If this message is still shown after a few minutes then please verify public video streaming is enabled for this camera</h3>"""
	}
	else if(!state?.public_share_url) {SS
		d += """<h3 ${tClass}>Unable to Display Video Stream</h3><br><h3 ${bClass}>Please make sure that public video streaming is enabled at</h3><h3 ${bClass}>https://home.nest.com</h3>"""
	}
	else if(!state?.isOnline) {
		d += """<h3 ${tClass}>This Camera is Offline</h3><br><h3 ${bClass}>Please verify it has a Wi-Fi connection and refresh this page...</h3>"""
	}
	else {
		d += """<h3 ${tClass}>Unable to display the Live Video Stream</h3><br><br><br><h3 ${bClass}>An unknown issue has occurred...</h3><h3 ${bClass}>Please consult the Live Logs in the SmartThings IDE</h3>"""
	}
	d += """</section></div>"""
	return d
}

private def textDevName()   { return "Nest Camera${appDevName()}" }
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
