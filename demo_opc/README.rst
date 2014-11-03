QTaste Demo Prosys OPC-UA 
=========================

This demo provides the ability to interface with Prosys OPC-UA to interact with components accesible from a SCADA Server.
For demonstration purposes the following TestSuite examples were implemented:

 - TemperatureComponents: Set the temperature of an Hardware component and check if this is reached after a defined delay.
 

System Requirements
===================

- Download OPC-UA-Java-SDK-Binary library. (The evaluation version will enough to use in this demo)
(please visit https://www.prosysopc.com/products/opc-ua-java-sdk/ and request the download)


Installation
============

1) Extract the downloaded tar file (e.g. Prosys-OPC-UA-Java-SDK-Client-Server-Evaluation-1.4.8-8731.tar.gz).
Using maven, please install the following items: 

* Prosys-OPC-UA-Java-SDK-Client-Server-Evaluation-XXX.jar
(E.g: mvn install:install-file -Dfile=./Prosys-OPC-UA-Java-SDK-Client-Binary-2.0.2-275.jar -DgroupId=Prosys -DartifactId=OPC-UA-Java-SDK-Binary -Dversion=2.0.2-275 -Dpackaging=jar -DgeneratePom=true) 

* Opc.Ua.Stack-XXX.jar
(E.g: mvn install:install-file -Dfile=./Opc.Ua.Stack-1.02.335.7.jar -DgroupId=unknown -DartifactId=Opc.Ua.Stack -Dversion=1.02.335.7 -Dpackaging=jar -DgeneratePom=true) 

* bcprov-jdkXXX.jar
(E.g: mvn install:install-file -Dfile=./bcprov-jdk15on-147.jar -DgroupId=unknown -DartifactId=bcprov-jdk15on-147 -Dversion=15on-147 -Dpackaging=jar -DgeneratePom=true)


For further information on maven installation procedure please visit:
http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html

2) Execute build script from /demo_opc directory.

Testbed Configuration
=====================

In order to connect QTaste to OPC-UA server, the a testbed xml file need to be created.
In this demo it was already created a configuration file named /Testbeds/ProsysOPC_UA.xml
The following parameters must be configured:

server_host: IP address of the SCADA server.
opc_url: the URL address to the SCADA server. (e.g: opc.tcp://${server_host}:4096/ignition_opc_ia_server)
opc_prefix: prefix given to the variables names that represents the accessible components from the server. (e.g: [AB1]Global.)






