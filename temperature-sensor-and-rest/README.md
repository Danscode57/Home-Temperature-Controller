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

## Running the Main class
Need to point to JNI library -Djava.library.path={LOCATION}