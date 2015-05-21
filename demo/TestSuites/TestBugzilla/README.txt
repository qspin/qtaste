Demo: Bugzilla demo
===================

Introduction
============

This sample demonstrates how to test simple web application using selenium and check that data are inserted properly in a database. 

Next to this, this demo comes with a customized control_script in order to show possible integration with Sun VirtualBox.

This demo is provided as example as it is probably not executable in your own environment without adaptation as some requirement are really 

specific. However, it gives a good idea about how to perform automated tests. Please see the "Bugzilla video" in order to see it in action.

Pre-Requisites
==============
- A running bugzilla server running virtually using Sun VirtualBox (please note that no VBox image is provided). Tested with VirtualBox 2.2.4 and 3.0.8
- The VBoxManage command(provided by Sun Virtual Box) has to be in the PATH
- Depending on the configuration of the bugzilla environment, the testbed configuration file has to be adapter to fit the name of the db and 
  match the dbuser/dbpassword
- Internet Explorer (6.X or 7.X). Please notice that the selenium version used doesn't support well IE8
- JDK 1.7


Running The Client
==================

Start the GUI client from the demo directory using the "startUI.cmd' or "startUI.sh" commands

 * Select the "bugzilla_setup" as testbed configuration
 * Select the TestSuites called "TestBugzilla"
 * Start the test

Help
====
Please contact QSpin if you have any trouble running the sample.
