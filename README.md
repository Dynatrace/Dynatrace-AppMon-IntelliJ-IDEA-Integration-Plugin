<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>dynaTrace IntelliJ IDEA Integration Plugin</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8" />
    <meta content="Scroll Wiki Publisher" name="generator"/>
    <link type="text/css" rel="stylesheet" href="css/blueprint/liquid.css" media="screen, projection"/>
    <link type="text/css" rel="stylesheet" href="css/blueprint/print.css" media="print"/>
    <link type="text/css" rel="stylesheet" href="css/content-style.css" media="screen, projection, print"/>
    <link type="text/css" rel="stylesheet" href="css/screen.css" media="screen, projection"/>
    <link type="text/css" rel="stylesheet" href="css/print.css" media="print"/>
</head>
<body>
                <h1>dynaTrace IntelliJ IDEA Integration Plugin</h1>
    <p>
    </p>
    <div class="confbox admonition admonition-info">
    <p>
Special Thanks to Christian Grimm who contributed this plugin to the dynaTrace Community    </p>
    </div>
    <div class="section-2"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-Overview"  >
        <h2>Overview</h2>
    <p>
            <img src="images_community/download/attachments/68649064/icon.png" alt="images_community/download/attachments/68649064/icon.png" class="confluence-embedded-image" />
            </p>
    <p>
The IntelliJ IDEA Plugin integrates dynaTrace advanced profiling capabilities to the <a href="http://www.jetbrains.com/idea/">IntelliJ IDEA IDE</a>. The plugin uses the IDEA plugin architecture and integrates seamless. All configurations can be done by using the graphical user interface.    </p>
    <p>
The following dynaTrace integrations are supported:    </p>
<ul class=" "><li class=" ">    <p>
Agent Injection (Launcher)    </p>
</li><li class=" ">    <p>
Session Recording    </p>
</li><li class=" ">    <p>
Source Code Lookup (CodeLink)    </p>
</li></ul>    </div>
    <div class="section-2"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-PluginDetails"  >
        <h2>Plugin Details</h2>
    <div class="tablewrap">
        <table>
<thead class=" "></thead><tfoot class=" "></tfoot><tbody class=" ">    <tr>
            <td rowspan="1" colspan="1">
        <p>
Author    </p>
            </td>
                <td rowspan="1" colspan="1">
        <p>
Christian Grimm (christian.grimm@dynatrace.com)    </p>
            </td>
        </tr>
    <tr>
            <td rowspan="1" colspan="1">
        <p>
dynaTrace Versions    </p>
            </td>
                <td rowspan="1" colspan="1">
        <p>
dynaTrace 4.1    </p>
            </td>
        </tr>
    <tr>
            <td rowspan="1" colspan="1">
        <p>
IntelliJ IDEA Versions    </p>
            </td>
                <td rowspan="1" colspan="1">
        <p>
11.0 (Tested)<br/>10.0 (Tested)<br/>8.0 - 9.0 (Should be API compatible with 10.0 version, NOT tested)    </p>
            </td>
        </tr>
    <tr>
            <td rowspan="1" colspan="1">
        <p>
License    </p>
            </td>
                <td rowspan="1" colspan="1">
        <p>
<a href="attachments_5275722_2_dynaTraceBSD.txt">dynaTrace BSD</a>    </p>
            </td>
        </tr>
    <tr>
            <td rowspan="1" colspan="1">
        <p>
Support    </p>
            </td>
                <td rowspan="1" colspan="1">
        <p>
<a href="https://community/display/DL/Support+Levels">Not Supported</a>    </p>
            </td>
        </tr>
    <tr>
            <td rowspan="1" colspan="1">
        <p>
Known Problems    </p>
            </td>
                <td rowspan="1" colspan="1">
        <p>
    </p>
            </td>
        </tr>
    <tr>
            <td rowspan="1" colspan="1">
        <p>
Release History    </p>
            </td>
                <td rowspan="1" colspan="1">
        <p>
2012-01-10 Initial Release<br/>2012-03-15 Added support for older IDEA 10.0 version    </p>
            </td>
        </tr>
    <tr>
            <td rowspan="1" colspan="1">
        <p>
Download    </p>
            </td>
                <td rowspan="1" colspan="1">
        <p>
IDEA 11.0: <a href="attachments_74612923_1_dynaTraceIdea11Plugin-0.8.zip">dynaTraceIdea11Plugin-0.8.zip</a><br/>IDEA 10.0: <a href="attachments_74612922_1_dynaTraceIdea10Plugin-0.8.zip">dynaTraceIdea10Plugin-0.8.zip</a>    </p>
            </td>
        </tr>
</tbody>        </table>
            </div>
    </div>
    <div class="section-2"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-Installation"  >
        <h2>Installation</h2>
    <p>
The following process describes how to install the dynaTrace IntelliJ IDEA Plugin on a MacOS system. However, the process is identical on other operating systems.    </p>
    <div class="section-3"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-Step1-Download"  >
        <h3>Step 1 - Download</h3>
    <p>
Download the plugin (<a href="attachments_74612922_1_dynaTraceIdea10Plugin-0.8.zip">dynaTraceIdea10Plugin-0.8.zip</a> or <a href="attachments_74612923_1_dynaTraceIdea11Plugin-0.8.zip">dynaTraceIdea11Plugin-0.8.zip</a>). Do not unzip it, IDEA will do this for you.    </p>
    </div>
    <div class="section-3"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-Step2-Installation"  >
        <h3>Step 2 - Installation</h3>
    <p>
Open the IDEA general preferences dialog, navigate to &quot;Plugins&quot; and click on &quot;Install plugin from disk..&quot;:<br/>            <img src="images_community/download/attachments/68649064/PluginInstallation1.png" alt="images_community/download/attachments/68649064/PluginInstallation1.png" class="" />
            </p>
    <p>
Select the downloaded .zip file:<br/>            <img src="images_community/download/attachments/68649064/PluginInstallation2.png" alt="images_community/download/attachments/68649064/PluginInstallation2.png" class="" />
            </p>
    <p>
After the plugin has been successfully installed, a restart of IDEA is required:<br/>            <img src="images_community/download/attachments/68649064/PluginInstallation3.png" alt="images_community/download/attachments/68649064/PluginInstallation3.png" class="" />
            </p>
    </div>
    </div>
    <div class="section-2"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-Usage"  >
        <h2>Usage</h2>
    <p>
The dynaTrace IDEA plugin provides two configuration dialogs:    </p>
    <div class="section-3"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-IDESettings"  >
        <h3>IDE Settings</h3>
    <p>
The dynaTrace settings dialog can be access by opening the IDEA preferences dialog and navigating to the &quot;dynaTrace Integration&quot; section:<br/>            <img src="images_community/download/attachments/68649064/UsageIDESettings1.png" alt="images_community/download/attachments/68649064/UsageIDESettings1.png" class="" />
        <br/>There are two settings here:    </p>
<ul class=" "><li class=" ">    <p>
dynaTrace Client Web Service Port: This specifies the TCP port used to connect to the dynaTrace client's REST services.    </p>
</li><li class=" ">    <p>
CodeLink: If enabled, IDEA connects to the local dynaTrace client using the specified TCP port. This enables the context menu &quot;Source Lookup&quot; -&gt; &quot;Open in IDE&quot; in the dynaTrace client and opens the selected class/method in IDEA.    </p>
</li></ul>    </div>
    <div class="section-3"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-Launcher"  >
        <h3>Launcher</h3>
    <p>
This dynaTrace launcher is located in the IDEA main window right next to the standard 'Run' button:<br/>            <img src="images_community/download/attachments/68649064/Launcher1.png" alt="images_community/download/attachments/68649064/Launcher1.png" class="" />
            </p>
    <p>
Before the 'Run with dynaTrace' button can be used, the Run/Debug Configuration needs to be configured properly. This can be done by editing the Run/Debug Configuration:<br/>            <img src="images_community/download/attachments/68649064/Launcher2.png" alt="images_community/download/attachments/68649064/Launcher2.png" class="" />
        <br/>There are multiple settings:    </p>
<ul class=" "><li class=" ">    <p>
Agent Library: This specifies the path to the dynaTrace agent binary. On Windows system, this is the path to dtagent.dll.    </p>
</li><li class=" ">    <p>
Agent Name: This defines the name of the agent.    </p>
</li><li class=" ">    <p>
dynaTrace Server Address: Sets the address of the dynaTrace server. Use 'localhost' if the dynaTrace server runs on the same machine as IDEA runs.    </p>
</li><li class=" ">    <p>
dynaTrace Server Port: Default is 9998    </p>
</li><li class=" ">    <p>
Enable Session Recording: If enabled, session recording will be started and stopped automatically with the application.    </p>
</li><li class=" ">    <p>
dynaTrace Server Name: This sets the name of the dynaTrace server to use for session recording.    </p>
</li><li class=" ">    <p>
System Profile: Sets the system profile to use for session recording.    </p>
</li></ul>    <p>
If configuration is done properly it should look like this when the 'Run with dynaTrace' button is used:<br/>            <img src="images_community/download/attachments/68649064/Launcher3.png" alt="images_community/download/attachments/68649064/Launcher3.png" class="" />
            </p>
    </div>
    <div class="section-3"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-CodeLink"  >
        <h3>Code Link</h3>
    <p>
    <span style="color: #000000;">
The following shows an example of CodeLink.    </span>
    </p>
    <p>
    <span style="color: #000000;">
Using the 'Source Lookup' context menu from the dynaTrace Client:    </span>
<br/>            <img src="images_community/download/attachments/68649064/CodeLink1.png" alt="images_community/download/attachments/68649064/CodeLink1.png" class="" />
        <br/>    <span style="color: #000000;">
... opens the selected class and method in IDEA:    </span>
<br/>            <img src="images_community/download/attachments/68649064/CodeLink2.png" alt="images_community/download/attachments/68649064/CodeLink2.png" class="" />
            </p>
    </div>
    </div>
    <div class="section-2"  id="68649064_dynaTraceIntelliJIDEAIntegrationPlugin-Feedback"  >
        <h2>Feedback</h2>
    <p>
Please provide feedback on this plugin either by commenting on this page or by comments on the <a href="https://community/display/DTFORUM/Community+Plugins+and+Extensions">Community Plugins and Extensions</a> site.    </p>
    </div>
            </div>
        </div>
        <div class="footer">
        </div>
    </div>
</body>
</html>
