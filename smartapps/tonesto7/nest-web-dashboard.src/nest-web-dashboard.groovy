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
    namespace: "tonesto7",
    author: "${textAuthor()}",
    description: "${textDesc()}",
    category: "Convenience",
    parent: "${textParent()}",
    iconUrl: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/dashboard_icon.png",
    iconX2Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/dashboard_icon.png",
    iconX3Url: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/dashboard_icon.png",
    singleInstance: true,
    oauth: true )

def appVersion() { "1.0.0" }
def appVerDate() { "8-24-2016" }
def appVerInfo() {
    def str = ""

    str += "V1.0.0 (August 24th, 2016):"
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
}

def webDashPrefix() { return "webDash" }

def startPage() {
    mainPage()
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

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has been installed...")
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
    sendNotificationEvent("${textAppName()} has updated settings...")
    atomicState?.lastUpdatedDt = getDtNow()
}

def uninstalled() {
    log.debug "uninstalled..."
    sendNotificationEvent("${textAppName()} is uninstalled...")
    parent?.dashboardInstalled(false)
    parent?.setDashboardUrl(null)
}

def initialize() {
    log.trace "initialize nest web dashboard..."
    if(!atomicState?.endpoint) { atomicState?.endpoint = null }
    if(settings["webDashFlag"]) {
        initializeEndpoint()
        atomicState?.automationType = "webDash"
        atomicState?.webDashFlag = true
        parent?.dashboardInstalled(true)
        if(atomicState?.endpoint) {
            parent?.setDashboardUrl(atomicState?.endpoint)
        }
    }
}

def mainPage() {
    //log.trace "mainPage()"
    if (!atomicState?.tempUnit) { atomicState?.tempUnit = getTemperatureScale()?.toString() }
    atomicState?.showHelp = (parent?.getShowHelp() != null) ? parent?.getShowHelp() : true

    return dynamicPage(name: "mainPage", title: "Automation Config Page...", uninstall: false, install: false, nextPage: "nameAutoPage" ) {
        section("Web Dashboard:") {
            def webDashDesc = ""
            webDashDesc += atomicState?.endpoint ? "Dashboard is Enabled and Active" : ""
            def webDesc = isWebDashConfigured() ? "${webDashDesc}" : null
            href "webDashPage", title: "Nest Web Dashboard...", description: webDesc ?: "Tap to Configure...", state: (webDesc ? "complete" : null), image: getAppImg("dashboard_icon.png")
        }
    }
}

def webDashPage() {
    def pName = webDashPrefix()
    dynamicPage(name: "webDashPage", title: "Nest Web Dashboard", uninstall: true, install: true) {
        section("Notifications:") {
            paragraph "Nothing to Configure Yet"
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

def getSettingVal(var) { return settings[var] ?: null }

def getStateVal(var) { return state[var] ?: null }

def getAutomationType() { return atomicState?.automationType ?: null }

def getIsAutomationDisabled() { return atomicState?.disableAutomation ? true : false}

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

def storeLastEventData(evt) {
    if(evt) {
        atomicState?.lastEventData = ["name":evt.name, "displayName":evt.displayName, "value":evt.value, "date":evt.date, "unit":evt.unit]
        //log.debug "LastEvent: ${atomicState?.lastEventData}"
    }
}

def storeExecutionHistory(val, method = null) {
    //log.debug "storeExecutionHistory($val, $method)"
    try {
        if(method) {
            log.debug "${method} Execution Time: (${val} milliseconds)"
        }
        atomicState?.lastExecutionTime = val ?: null
        def list = atomicState?.evalExecutionHistory ?: []
        def listSize = 10
        if(list?.size() < listSize) {
            list.push(val)
        }
        else if (list?.size() > listSize) {
            def nSz = (list?.size()-listSize) + 1
            def nList = list?.drop(nSz)
            nList?.push(val)
            list = nList
        }
        else if (list?.size() == listSize) {
            def nList = list?.drop(1)
            nList?.push(val)
            list = nList
        }
        if(list) { atomicState?.evalExecutionHistory = list }
    } catch (ex) {
        log.error "storeExecutionHistory Exception:", ex
        parent?.sendExceptionData(ex.message, "storeExecutionHistory", true, getAutoType())
    }
}

def getAverageValue(items) {
    def tmpAvg = []
    def val = 0
    if(!items) { return val }
    else if(items?.size() > 1) {
        tmpAvg = items
        if(tmpAvg && tmpAvg?.size() > 1) { val = (tmpAvg?.sum().toDouble() / tmpAvg?.size().toDouble()).round(0) }
    } else { val = item }
    return val.toInteger()
}

def getAutoActionData() {
    if(atomicState?.lastAutoActionData) {
        return atomicState?.lastAutoActionData
    }
}

def initializeEndpoint() {
    if (!atomicState?.endpoint) {
		try {
			def accessToken = createAccessToken()
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
    def execTime = now()
    def data = parent.api_deviceData(params)
    storeExecutionHistory((now() - execTime), "api_deviceData")
    return data
}

def api_singleDeviceData() {
    def execTime = now()
    def data = parent.api_singleDeviceData(params)
    storeExecutionHistory((now() - execTime), "api_singleDeviceData")
    return data
}

def api_managerData() {
    def execTime = now()
    def data = parent.api_managerData(params)
    storeExecutionHistory((now() - execTime), "api_managerData")
    return data
}

def api_childAppData() {
    def execTime = now()
    def data = parent.api_childAppData(params)
    storeExecutionHistory((now() - execTime), "api_childAppData")
    return data
}

def api_dashboard() {
    def execTime = now()
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
    storeExecutionHistory((now() - execTime), "api_dashboard")
    render contentType: "text/html", data: htmlData
}

///////////////////////////////////////////////////////////////////////////////
/******************************************************************************
*                Application Help and License Info Variables                  *
*******************************************************************************/
///////////////////////////////////////////////////////////////////////////////
def appName() 		{ return "Nest Web Dashboard${appDevName()}" }
def appAuthor() 	{ return "Anthony S." }
def appNamespace() 	{ return "tonesto7" }
def gitBranch()     { return "develop" }
def betaMarker()    { return false }
def appDevType()    { return false }
def appDevName()    { return appDevType() ? " (Dev)" : "" }
def appInfoDesc() 	{
    def cur = parent?.isDashUpdateAvail()
    def beta = betaMarker() ? "" : ""
    def str = ""
    str += "${textAppName()}"
    str += isAppUpdateAvail() ? "\n• ${textVersion()} (Lastest: v${cur})${beta}" : "\n• ${textVersion()}${beta}"
    str += "\n• ${textModified()}"
    return str
}
def getServerUrl()          { return "https://graph.api.smartthings.com" }
def getShardUrl()           { return getApiServerUrl() }
def getCallbackUrl()		{ return "https://graph.api.smartthings.com/oauth/callback" }
def getBuildRedirectUrl()	{ return "${serverUrl}/oauth/initialize?appId=${app.id}&access_token=${atomicState?.accessToken}&apiServerUrl=${shardUrl}" }
def getNestApiUrl()			{ return "https://developer-api.nest.com" }
def getAppEndpointUrl(subPath) { return "${apiServerUrl("/api/smartapps/installations/${app.id}/${subPath}?access_token=${atomicState.accessToken}")}" }
def getHelpPageUrl()        { return "https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help-page.html" }
def getReadmePageUrl()        { return "https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/README.html" }
def getAutoHelpPageUrl()        { return "https://rawgit.com/tonesto7/nest-manager/${gitBranch()}/Documents/help/nest-automations.html" }
def getFirebaseAppUrl() 	{ return "https://st-nest-manager.firebaseio.com" }
def getAppImg(imgName, on = null) 	{ return (!disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/App/$imgName" : "" }
def getDevImg(imgName, on = null) 	{ return (!disAppIcons || on) ? "https://raw.githubusercontent.com/tonesto7/nest-manager/${gitBranch()}/Images/Devices/$imgName" : "" }
def textAppName()   { return "${appName()}" }
def textVersion()   { return "Version: ${appVersion()}" }
def textModified()  { return "Updated: ${appVerDate()}" }
def textParent()    { return "${textNamespace()}:Nest Manager${appDevName()}"}
def textAuthor()    { return "${appAuthor()}" }
def textNamespace() { return "${appNamespace()}" }
def textVerInfo()   { return "${appVerInfo()}" }
def textDonateLink(){ return "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2CJEVN439EAWS" }
def stIdeLink()     { return "https://graph.api.smartthings.com" }
def textCopyright() { return "Copyright© 2016 - Anthony S." }
def textDesc()      { return "This SmartApp is used to integrate you're Nest devices with SmartThings as well as allow you to create child automations triggered by user selected actions..." }
def textHelp()      { return "" }
def textLicense() {
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
