Temperature control sensor reading and REST Server
==================================================

This module hold Kotlin implementation of Temperature Reading from Arduino via a serial port and REST server exposing temperature reading and control to any UIs.

        
        Please note that this code is not designed to work on the public networks like Internet, as it is missing crucial security!

        
A list of technologies involved:

* JVM 8, and it has to be at least 8 as Vert.x 3.x doesn't work with anything before version 8
* RXTX Java - a Java library for communicating with serial ports (included in the libs folder)
* Vert.x 3.2.1 framework - framework for event driven applications, highly scalable and super fast. Love it!
* Kotlin programming language - because I wanted to learn another language for JVM :) It turned to be pretty good.
* H2 Database - for file based simple storage of the historical temperature data
* Gradle for building the lot

## Architecture and flow

Main entry point to the application is via **TemperatureApp** class. It starts all verticles that are responsible for specific event handling:

* SerialPortTemperatureVerticle - registers itself as a listener on specified serial port (Arduino USB connection). 
It receives temperature updates from Arduino and sends control message with temperature to set to it.

## Building the binaries

Project uses Gradle to build fat jar file. To build the project run:

        ./gradlew clean shadowJar

Build jar will be located in **build/libs/** folder.

There is also a task that packs binaries, scripts and config files into a single zip.

        ./gradlew clean prepareDist
        
Prepared zip file will be located in **build/dist/** folder.

## Running the app

[SerialPort verticle][1] is using RXTX Java Library which requires JNI library. On Mac, I'm using the one in [libs][./libs] folder.

On Raspberry Pi I installed the JNI with command:

        sudo apt-get -y install rxtx-jni


You should be able to run the [start_service.sh][1] script that starts the service.

Alternatively you can run the following from the command line:

        JAR_FILE=temperature-sensor-and-rest-1.0.0-SNAPSHOT-fat.jar
        JNI_LOCATION=/usr/lib/jni
        CONFIG_FILE=prod.json
        java -Djava.library.path=$JNI_LOCATION -jar $JAR_FILE  -conf $CONFIG_FILE


You can try to stop the service with [stop_service.sh][2] script :)

[1]: scripts/start_service.sh
[2]: scripts/stop_service.sh
