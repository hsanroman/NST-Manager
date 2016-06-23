##Nest Manager Code Installation

The code for the SmartThings SmartApp is found on the GitHub site:

    https://github.com/tonesto7/nest-manager/blob/master/smartapps/tonesto7/nest-manager.src/nest-manager.groovy

While on the GitHub site, find the **Raw** button and click it. This will bring up a non-formatted page with just the code present. Select all of the code (typically CTRL+A) and copy It (typically CTRL+C).

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/git_raw_ss.png)


* Next, point your browser to you SmartThings IDE for your country (i.e. [http://ide.smartthings.com](http://ide.smartthings.com) or [https://graph-eu01-euwest1.api.smartthings.com](https://graph-eu01-euwest1.api.smartthings.com) and **Log In**.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-Loginscreen.jpg)

* Once you are logged in, find the **My SmartApps** link on the top of the page.  Clicking **My SmartApps** will allow you to produce a new SmartApp.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-MySmartApps.png)

* Find the button on this page labeled **+New SmartApp** and click it.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-%2BNewSmartApp.png)

* Since you already have the code in your computer’s clipboard, find the tab along the top section called **From Code**. In the area provided, paste (typically CTRL+V) the code you copied from GitHub. Click **Create** in the bottom left corner of the page.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-NewSmartAppCreate.png)

* This will bring up another page, with the code now formatted within the IDE. If the code was copied correctly, there are no other steps except to save and publish the code. In the upper right corner of the page, find and click **Save**. Now, click **Publish (For Me)**, and you should receive a confirmation that the code has been published successfully.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/SavePublish.png)

###Enabling OAuth
**Nest Manager** requires OAuth to operate correctly.

To enable OAuth, first find and click the **App Settings** button in the upper right corner of the page.

From here, find the **OAuth** section toward the bottom of the page. 

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-OAuthBtn.png)
 
Clicking the **OAuth** link will reveal a button labeled **Enable OAuth in Smart App**. Click this button. The screen will change, giving you a unique code for your **Client ID** and **Client Secret**. These are the foundations of the security of your app and should be kept secret. You do not need to memorize or write down these codes; nor do you need to add any other information to this page. OAuth simply needs to be enabled for **Cloud Interface** to generate your unique URLs.

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/700px-OAuth.jpg)

The final step is to press the **Update** button at the bottom left corner of the screen, or go back to your code by using the button in the upper-right region of the page, then **Save**, then **Publish** the SmartApp again. 

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/SavePublish.png)

###Advanced Installation (Using Git Integration)
For advanced users who have their SmartThings IDE integrated with GitHub, the installation and maintaining of code becomes very simple. This manual will not go into detail about setting up your IDE with GitHub; those instructions can be found on the SmartThings web site [[http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html?highlight=git]]

Please note that this process DOES NOT update any of the code on the Amazon developer site; you will need to follow the process above to copy/paste your code into proper areas.

Once you have integration, the four pieces of code you might need will be available to you to download and keep in sync with the latest versions. 
* First, find the **Settings** button at the top of your SmartThings IDE page (this will only appear after you integrate with GitHub)

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/IdeSettings.jpg)

* Clicking this button will open the GitHub Repository Integration page. To find the **Ask Alexa** SmartApp code, enter the information as you see it below:

**Owner:** tonesto7

**Name:** nest-manager

![](https://raw.githubusercontent.com/tonesto7/nest-manager/master/Documents/images/GithubIntegration.png)

* Close the GitHub Repository Integration page
*Next, click the **Update from Repo** button at the upper-right corner of the IDE
*On the right-hand column, scroll down to click the apps you want to install. This will typically be:

    `**SmartApp**: smartapps/tonesto7/nest-manager.src/nest-manager.groovy`
    
* Click the **Execute Update** in the bottom-right corner of the screen. When done syncing, the new apps should now appear in your IDE. If they ever change color, that indicates a new version is available.
