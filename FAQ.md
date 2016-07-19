# FAQ / Troubleshooting Guide



## Problems? Questions? Suggestions?

Post any problems, questions or suggestions to the Dynatrace Community's [Application Monitoring & UEM Forum](https://answers.dynatrace.com/spaces/146/index.html).
 

## General

##### Dynatrace AppMon version compatibility - which version works with the plugin?
> 6.3 and newer

##### What is CodeLink?
> The ability to navigate from Dynatrace AppMon Client to code. E.g. this is available in the PurePaths view, from the PurePath Tree section: after right-clicking on a message call, selecting Source Lookup / Open in IDE. 

![img](img/source_lookup.png)

## Configuration Help

##### Where is the AppMon Server configuration?
> In the IntelliJ Idea global preferences, under Dynatrace AppMon ( "File \ Settings \ Build, Execution, Deployment \ Dynatrace AppMon"  )

##### Where is the AppMon Agent configuration?
> Under the "Run" tab [toolbar](img/launcher.png): select "Edit Configurations...", select a relevant configuration, [Dynatrace AppMon tab](img/run_configuration.png)

##### Can I have the Dynatrace Agent connect directly into the AppMon Server without a collector?
> Yes, input the server's host and port as the collector's host and port into IntelliJ Idea global Dynatrace AppMon configuration under "File \ Settings \ Build, Execution, Deployment \ Dynatrace AppMon".

##### Dynatrace AppMon Server port numbers -- where can I find them?
> Run Dynatrace AppMon Client, open Settings menu \ Dynatrace Server... \ Services \ Management tab \ Web Server section

##### Dynatrace AppMon server-as-collector port number -- where can I find it?
> Run Dynatrace AppMon Client, open Settings menu \ Dynatrace Server... \ Services \ General tab \ [Embedded Collector Settings section](img/collector_settings.png). The default is 9998.

##### Can I configure multiple servers/collectors?
> No. There is a single server and collector configuration which you would have to change every time.
> But you can configure multiple profiles and agent names, one per launch configuration.

##### Where can I enable per-launch session recording?
> Configured per launch configuration, under "Run" select "Edit Configurations...", select a relevant configuration, [Dynatrace AppMon tab](img/run_configuration.png)

## Runtime problems

##### JUnit executions don't appear in AppMon.
> 1. Execute the launch configuration from the "Run with Dynatrace AppMon" icon !["Run with Dynatrace AppMon" icon](img/dynatrace_run.png) vs the standard debug and run icons.
> 2. Check the IntelliJ Idea console for the agent output, confirm that it is able to connect to the collector.
> 3. Check the system profile and agent name configured in the Dynatrace AppMon launch configuration. Confirm AppMon Server contains this profile, and the agent name is mapped to this profile ( Run \ Edit Configurations... \ Dynatrace AppMon tab )
> 3. Check the IntelliJ Idea Event Log view (View \ Tool Windows \ EventLog) for errors
> 4. Confirm that you input (1) the dynatrace agent library (2) AppMon server, collector, client addresses into IntelliJ Idea configuration under "File \ Settings \ Build, Execution, Deployment \ Dynatrace AppMon"
> 5. Confirm that IntelliJ Idea has REST http/https connectivity to the server, collector and client.
> 6. Look for "bubbles" at the bottom of the window.

##### My Run configuration is not visible under "Run With AppMon Configurations"
> Only specific run configurations are supported. The currenlty supported run configurations are: Java application, JUnit test

## Error messages

##### Failed connecting to Dynatrace AppMon Client to poll for CodeLink jump requests. 
> Dynatrace AppMon Client is either not running or the IntelliJ Idea the client configuration is wrong ("File \ Settings \ Build, Execution, Deployment \ Dynatrace AppMon \ Codelink ). To prevent the message from appearing please disable CodeLink temporarily and reenable as needed ("File \ Settings \ Build, Execution, Deployment \ CodeLink \ Enable)

##### No test results available. Please verify agent and profile settings.
> The plugin polls the AppMon Server repeatadly waiting for the purePaths to be analized. Test results are processed on the server after a delay and don't arrive all at once. After 30 seconds the available results are presented. After 30 seconds the only way to view the test results is to open the AppMon Client.

#### Session recording is not allowed if continuous transaction storage is enabled
> In order to record session you have to disable Continuous Transaction on Dynatrace Server, you can do that in Dynatrace Client under "Settings \ Dynatrace Server... \ Storage \ Storage Settings section \ Enable continuous transaction storage".