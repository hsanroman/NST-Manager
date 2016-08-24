/********************************************************************************************
|    Application Name: Nest Web Dashboard                                         |
|    Author: Anthony S. (@tonesto7),                                                        |
|    Contributors: Ben W. (@desertblade) | Eric S. (@E_sch)                                 |
|                                                                                           |
|*******************************************************************************************|
|    There maybe portions of the code that may resemble code from other apps in the         |
|    community. I may have used some of it as a point of reference.                         |
|    Thanks go out to those Authors!!!                                                      |
|    I apologize if i've missed anyone.  Please let me know and I will add your credits     |
|                                                                                           |
|    ### I really hope that we don't have a ton or forks being released to the community,   |
|    ### I hope that we can collaborate and make app and device type that will accomodate   |
|    ### every use case                                                                     |
*********************************************************************************************/

import groovy.json.*
import groovy.time.*
import java.text.SimpleDateFormat
import java.security.MessageDigest

definition(
    name: "${textAppName()}",
    namespace: "${textNamespace()}",
    author: "${textAuthor()}",
    description: "${textDesc()}",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager%402x.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_manager%403x.png",
    singleInstance: true,
    oauth: true )

def appVersion() { "3.1.0" }
def appVerDate() { "8-24-2016" }
def appVerInfo() {
    def str = ""

    str += "V3.1.0 (August 24th, 2016):"
    str += "\n▔▔▔▔▔▔▔▔▔▔▔"
    str += "\n • ADDED: Initial Commit of Web Dashboard Child"

    return str
}

preferences {
    //startPage
    page(name: "startPage")

    //Manager Pages
    page(name: "mainPage")
    page(name: "webDashPage")
    page(name: "pageInitDashboard")
}

def webDashPrefix() { return "webDash" }

def startPage() {
    if (parent) {
        atomicState?.isParent = false
        selectAutoPage()
    } else {
        atomicState?.isParent = true
        authPage()
    }
}

//Web Dashboard EndPoints
mappings {
    path("/dashboard")        {action: [GET: "api_dashboard"]}
    path("/childAppData")                       {action: [GET: "api_childAppData"]}
    path("/childAppData/:autoType")             {action: [GET: "api_childAppData"]}
    path("/childAppData/:autoType/:dataType")             {action: [GET: "api_childAppData"]}
    path("/childAppData/:autoType/:dataType/:variable")   {action: [GET: "api_childAppData"]}
    path("/managerData")                        {action: [GET: "api_managerData"]}
    path("/managerData/:dataType")              {action: [GET: "api_managerData"]}
    path("/managerData/:dataType/:variable")    {action: [GET: "api_managerData"]}
    path("/deviceData")                 {action: [GET: "api_deviceData"]}
    path("/deviceData/:deviceType")     {action: [GET: "api_deviceData"]}
    path("/singleDeviceData/:deviceId")                    {action: [GET: "api_singleDeviceData"]}
    path("/singleDeviceData/:deviceId/:dataType")          {action: [GET: "api_singleDeviceData"]}
    path("/singleDeviceData/:deviceId/:dataType/:variable"){action: [GET: "api_singleDeviceData"]}
    path("/updateSetting/:setting/:setVal")     {action: [GET: "api_setSettingValue", POST: "api_setSettingValue"]}
    path("/executeCmd")                         {action: [POST: "api_executeCmd"]}
    path("/executeCmd/:cmd")                    {action: [GET: "api_executeCmd", POST: "api_executeCmd"]}
}

def mainAutoPage(params) {
    //log.trace "mainAutoPage()"
    if (!atomicState?.tempUnit) { atomicState?.tempUnit = getTemperatureScale()?.toString() }
    if (!atomicState?.disableAutomation) { atomicState.disableAutomation = false }
    atomicState?.showHelp = (parent?.getShowHelp() != null) ? parent?.getShowHelp() : true
    def autoType = null
    //If params.autoType is not null then save to atomicState.
    if (!params?.autoType) { autoType = atomicState?.automationType }
    else { atomicState.automationType = params?.autoType; autoType = params?.autoType }

    // If the selected automation has not been configured take directly to the config page.  Else show main page
    if (autoType == "remSen" && !isRemSenConfigured())          { return remSensorPage() }
    else if (autoType == "fanCtrl" && !isFanCtrlConfigured())   { return fanControlPage() }
    else if (autoType == "extTmp" && !isExtTmpConfigured())     { return extTempPage() }
    else if (autoType == "conWat" && !isConWatConfigured())     { return contactWatchPage() }
    else if (autoType == "nMode" && !isNestModesConfigured())   { return nestModePresPage() }
    else if (autoType == "tMode" && !isTstatModesConfigured())  { return tstatModePage() }
    else if (autoType == "leakWat" && !isLeakWatConfigured())   { return leakWatchPage() }
    else if (autoType == "watchDog" && !isWatchdogConfigured()) { return watchDogPage() }

    else {
        // Main Page Entries
        def nxtPage = (atomicState?.automationType) ? "nameAutoPage" : ""
        return dynamicPage(name: "mainAutoPage", title: "Automation Config Page...", uninstall: false, install: false, nextPage: "nameAutoPage" ) {
            if(disableAutomationreq) {
                section() {
                    paragraph "This Automation is currently disabled!!!\nTurn it back on to to make changes or resume operation...", required: true, state: null, image: getAppImg("instruct_icon.png")
                }
            }
            if(autoType == "webDash" && !atomicState?.disableAutomation) {
                section("Web Dashboard:") {
                    def webDashDesc = ""
                    webDashDesc += (settings["${getAutoType()}AllowSpeechNotif"] && (settings["${getAutoType()}SpeechDevices"] || settings["${getAutoType()}SpeechMediaPlayer"]) && getVoiceNotifConfigDesc()) ?
                            "\n\nVoice Notifications:${getVoiceNotifConfigDesc()}" : ""
                    def webDesc = isWebDashConfigured() ? "${webDashDesc}" : null
                    href "webDashPage", title: "Nest Web Dashboard...", description: webDesc ?: "Tap to Configure...", state: (webDesc ? "complete" : null), image: getAppImg("watchdog_icon.png")
                }
            }

            if (atomicState?.isInstalled && (isRemSenConfigured() || isExtTmpConfigured() || isConWatConfigured() || isNestModesConfigured() || isTstatModesConfigured() || isWatchdogConfigured())) {
                section("Enable/Disable this Automation") {
                    input "disableAutomationreq", "bool", title: "Disable this Automation?", required: false, defaultValue: disableAutomation, submitOnChange: true, image: getAppImg("switch_off_icon.png")
                    if(!atomicState?.disableAutomation && disableAutomationreq) {
                        LogAction("This Automation was Disabled at (${getDtNow()})", "info", true)
                        atomicState?.disableAutomationDt = getDtNow()
                    } else if (atomicState?.disableAutomation && !disableAutomationreq) {
                        LogAction("This Automation was Restored at (${getDtNow()})", "info", true)
                        atomicState?.disableAutomationDt = null
                    }
                    atomicState.disableAutomation = disableAutomationreq
                }
                section("Debug Options") {
                    input (name: "showDebug", type: "bool", title: "Show App Logs in the IDE?", required: false, defaultValue: false, submitOnChange: true, image: getAppImg("log.png"))
                    atomicState?.showDebug = showDebug
                }
            }
        }
    }
}

def webDashPage() {
    def pName = webDashPrefix()
    dynamicPage(name: "webDashPage", title: "Nest Web Dashboard", uninstall: true, install: true) {
        section("Notifications:") {
            href "setNotificationPage", title: "Configure Push/Voice\nNotifications...", description: getNotifConfigDesc(), params: ["pName":pName, "allowSpeech":true, "showSchedule":true, "allowAlarm":true],
                    state: (getNotificationOptionsConf() ? "complete" : null), image: getAppImg("notification_icon.png")
        }
    }
}

def nameAutoPage() {
    dynamicPage(name: "nameAutoPage", install: true, uninstall: false) {
        section("Automation name") {
            if(getAutoType() == "watchDog") {
                paragraph "${app?.label}"
            } else {
                label title: "Name this Automation:", defaultValue: "${getAutoTypeLabel()}", submitOnChange: true, required: true
                paragraph "New Name:\n${getAutoTypeLabel()}", required: true, state: null
                paragraph "FYI:\nMake sure to name it something that will help you easily identify the app later."
            }
        }
    }
}
def initAutoApp() {
    if(settings["webDashFlag"]) {
        atomicState?.automationType = "webDash"
        initNestManagerEndpoint()
    }
    unschedule()
    unsubscribe()
    automationsInst()
    subscribeToEvents()
    scheduler()
    app.updateLabel(getAutoTypeLabel())
    webDashAutomation()
}

def getAutoTypeLabel() {
    //LogAction("getAutoTypeLabel:","trace", true)
    def type = atomicState?.automationType
    def appLbl = app?.label?.toString()
    def newName = "${appName()}"
    def typeLabel = ""
    def newLbl
    def dis = atomicState?.disableAutomation ? "\n(Disabled)" : ""
    if (type == "webDash")     { typeLabel = "Nest Web Dashboard"}

    //if(appLbl != typeLabel && appLbl != "Nest Manager" && !appLbl?.contains("(Disabled)")) {
    if(appLbl != "Nest Manager") {
        if(appLbl.contains("\n(Disabled)")) {
            newLbl = appLbl.replaceAll('\\\n\\(Disabled\\)', '')
        } else {
            newLbl = appLbl
        }
    } else {
        newLbl = typeLabel
    }
    return "${newLbl}${dis}"
}

def subscribeToEvents() {
    //Remote Sensor Subscriptions
    def autoType = getAutoType()
    //webDash Subscriptions
    if (autoType == "webDash") {

    }
}

private pageInitDashboard() {
	def success = initNestManagerEndpoint()
	dynamicPage(name: "pageInitDashboard", title: "") {
		section() {
			if (success) {
				paragraph "Success! Your Nest dashboard is now enabled. Tap Done to continue", required: false
			}
		}
	}
}

def getWebEndpointUrl() {
    return atomicState?.endpoint ?: null
}

def isWebDashConfigured() {
    return (atomicState?.automationType == "webDash") ? true : false
}

def automationsInst() {
    atomicState.isWebDashConfigured = isWebDashConfigured() ? true : false
    atomicState?.isInstalled = true
}

def getSettingVal(var) {
    return settings[var] ?: null

}

def getStateVal(var) {
    return state[var] ?: null
}

def getAutomationType() {
    return atomicState?.automationType ?: null
}

def getIsAutomationDisabled() {
    return atomicState?.disableAutomation ? true : false
}

def webDashAutomation() {

}

def scheduler() {
    def random = new Random()
    def random_int = random.nextInt(60)
    def random_dint = random.nextInt(9)
}

def getAutomationStats() {
    return [
        "lastUpdatedDt":atomicState?.lastUpdatedDt,
        "lastEvalDt":atomicState?.lastEvalDt,
        "lastEvent":atomicState?.lastEventData,
        "lastActionData":getAutoActionData(),
        "lastSchedDt":atomicState?.lastAutomationSchedDt,
        "lastExecVal":atomicState?.lastExecutionTime,
        "execAvgVal":(atomicState?.evalExecutionHistory != [] ? getAverageValue(atomicState?.evalExecutionHistory) : null)
    ]
}

def storeLastAction(actionDesc, actionDt) {
    if(actionDesc && actionDt) {
        atomicState?.lastAutoActionData = ["actionDesc":actionDesc, "dt":actionDt]
    }
}

def getAutoActionData() {
    if(atomicState?.lastAutoActionData) {
        return atomicState?.lastAutoActionData
    }
}

private initNestManagerEndpoint() {
    if (!atomicState?.endpoint) {
		try {
			def accessToken = atomicState?.accessToken
			if (accessToken) {
				atomicState?.endpoint = apiServerUrl("/api/token/${accessToken}/smartapps/installations/${app.id}/")
			}
		} catch(e) {
			atomicState?.endpoint = null
		}
	}
	return atomicState?.endpoint
}

def api_deviceData() {
    if(parent) {
      return parent.api_deviceData(params)
    }
}

def api_singleDeviceData() {
    if(parent) {
      return parent.api_singleDeviceData(params)
    }
}

def api_managerData() {
    if(parent) {
      return parent.api_managerData(params)
    }
}

def api_childAppData() {
    if(parent) {
      return parent.api_childAppData(params)
    }
}


def api_dashboard() {
    def urlRoot = "https://st-nest-manager.firebaseapp.com"
    def htmlData = """
        <!DOCTYPE html>
            <head>
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <link rel="icon" href="${urlRoot}/resources/nest_manager.icon" type="image/x-icon" />
                <link rel="stylesheet prefetch" href="https://dl.dropboxusercontent.com/s/j3l3rmizag9skxx/dashboard_css.css"/>
                <script type="text/javascript" src="https://dl.dropboxusercontent.com/s/4xttynn712v9rlj/dashboard_js.js"></script>
    		    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    		    <title>Nest Manager Dashboard</title>

                <link href="${urlRoot}/css/bootstrap.min.css" rel="stylesheet">
                <link href="${urlRoot}/css/font-awesome.min.css" rel="stylesheet">
                <script src="${urlRoot}/js/jquery.js"></script>
                <script src="${urlRoot}/js/bootstrap.min.js"></script>
    	    </head>
          	  <body>
                <!-- Navigation -->
                <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
                    <div class="container">
                        <!-- Brand and toggle get grouped for better mobile display -->
                        <div class="navbar-header">
                            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                                <span class="sr-only">Toggle navigation</span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                            </button>
                            <a class="navbar-brand" href="#">
                                <img src="${urlRoot}/resources/nest_manager.png" style="width: 50px; height: 50px;" alt="">
                            </a>
                        </div>
                        <!-- Collect the nav links, forms, and other content for toggling -->
                        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                            <ul class="nav navbar-nav">
                                <li>
                                    <a href="#">About</a>
                                </li>
                                <li>
                                    <a href="#">Services</a>
                                </li>
                                <li>
                                    <a href="#">Contact</a>
                                </li>
                            </ul>
                            <ul class="nav navbar-nav navbar-right">
                              <li><a href="">SmartApp: v3.1.0</a></li>
                            </ul>
                        </div>
                        <!-- /.navbar-collapse -->
                    </div>
                    <!-- /.container -->
                </nav>
                <nav class="navbar navbar-inverse sidebar" role="navigation">
                    <div class="container-fluid">
                		<!-- Brand and toggle get grouped for better mobile display -->
                		<div class="navbar-header">
                			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-sidebar-navbar-collapse-1">
                				<span class="sr-only">Toggle navigation</span>
                				<span class="icon-bar"></span>
                				<span class="icon-bar"></span>
                				<span class="icon-bar"></span>
                			</button>
                		</div>
                		<!-- Collect the nav links, forms, and other content for toggling -->
                		<div class="collapse navbar-collapse" id="bs-sidebar-navbar-collapse-1">
                			<ul class="nav navbar-nav">
                				<li class="active"><a href="#">Home<span style="font-size:16px;" class="pull-right hidden-xs showopacity glyphicon glyphicon-home"></span></a></li>
                				<li ><a href="#">Profile<span style="font-size:16px;" class="pull-right hidden-xs showopacity glyphicon glyphicon-user"></span></a></li>
                				<li ><a href="#">Messages<span style="font-size:16px;" class="pull-right hidden-xs showopacity glyphicon glyphicon-envelope"></span></a></li>
                				<li class="dropdown">
                					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Settings <span class="caret"></span><span style="font-size:16px;" class="pull-right hidden-xs showopacity glyphicon glyphicon-cog"></span></a>
                					<ul class="dropdown-menu forAnimate" role="menu">
                						<li><a href="#">Action</a></li>
                						<li><a href="#">Another action</a></li>
                						<li><a href="#">Something else here</a></li>
                						<li class="divider"></li>
                						<li><a href="#">Separated link</a></li>
                						<li class="divider"></li>
                						<li><a href="#">One more separated link</a></li>
                					</ul>
                				</li>
                			</ul>
                		</div>
                	</div>
                </nav>

                <!-- Page Content -->
                <div class="container">
                    <div class="row">
                        <div class="col-lg-12">
                            <h1>Nest Manager Dashboard</h1>
                            <p>Note: Things need to go below this point :)</p>
                        </div>
                    </div>
                </div>
          	</body>
        </html>
    """
	render contentType: "text/html", data: htmlData
}

///////////////////////////////////////////////////////////////////////////////
/******************************************************************************
*                Application Help and License Info Variables                  *
*******************************************************************************/
///////////////////////////////////////////////////////////////////////////////
private def appName() 		{ return "Nest Web Dashboard${appDevName()}" }
private def appAuthor() 	{ return "Anthony S." }
private def appNamespace() 	{ return "tonesto7" }
private def gitBranch()     { return "develop" }
private def betaMarker()    { return false }
private def appDevType()    { return false }
private def appDevName()    { return appDevType() ? " (Dev)" : "" }
private def appInfoDesc() 	{
    def cur = atomicState?.appData?.updater?.versions?.app?.ver.toString()
    def beta = betaMarker() ? "" : ""
    def str = ""
    str += "${textAppName()}"
    str += isAppUpdateAvail() ? "\n• ${textVersion()} (Lastest: v${cur})${beta}" : "\n• ${textVersion()}${beta}"
    str += "\n• ${textModified()}"
    return str
}
private def textAppName()   { return "${appName()}" }
private def textVersion()   { return "Version: ${appVersion()}" }
private def textModified()  { return "Updated: ${appVerDate()}" }
private def textAuthor()    { return "${appAuthor()}" }
private def textNamespace() { return "${appNamespace()}" }
private def textVerInfo()   { return "${appVerInfo()}" }
private def textDonateLink(){ return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2CJEVN439EAWS" }
private def stIdeLink()     { return "https://graph.api.smartthings.com" }
private def textCopyright() { return "Copyright© 2016 - Anthony S." }
private def textDesc()      { return "This SmartApp is used to integrate you're Nest devices with SmartThings as well as allow you to create child automations triggered by user selected actions..." }
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
