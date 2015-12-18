QTaste
======

QTaste framework (QSpin Tailored Automated System Test Environment) is a generic test environment customizable to test
different kind of systems. It can be used to test simple and complex hardware or software systems including a lot of different
technologies. For that reason, the test api has to be “tailored” in order to enable the kernel to communicate with your system.


The QTaste framework is mainly developed in java programming language and python. So by definition, it can be installed
on any platform running java VM 1.7. However, it has been only validated on Windows (Windows 8) and Linux platform.

Download
========

[https://github.com/qspin/qtaste/releases/download/v2.3.0/qtaste-izpack-2.3.0-installer.jar]

QTaste System Requirements
==========================

• Java Virtual Machine (JDK) >= 1.7 ( http://java.sun.com/javase/downloads/index.jsp )

• (optional) git command-line client accessible from PATH (“git”), for test script versioning

• At least 100 MB of disk space

• At least 256 MB of system memory (Running with less memory may cause disk swapping which has a severe effect on
performance. Very large programs may require more RAM for adequate performance.)

• On Linux system, Java VM requires some graphical gnome libraries. These libraries have to be installed. https://github.com/qspin/qtaste/issues/4

Installation of the QTaste
==========================

The QTaste framework is composed of:

• Test Engine kernel

• Simulators base classes

• Other tools

• Components Test API and Component Implementations

• Test Suites containing test scripts and test data

• Test Campaigns

• Testbeds configurations

The installer available on Github - Releases section  - [https://github.com/qspin/qtaste/releases] contains the QTaste Kernel (sources and
binaries), the demonstration (sources and binaries) and the documentation.
To start the installer, just click on the qtaste-izpack-2.3.0-installer.jar [https://github.com/qspin/qtaste/releases/download/v2.3.0/qtaste-izpack-2.3.0-installer.jar]
