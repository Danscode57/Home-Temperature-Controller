Home Temperature Controller
===========================

This repository holds sources for a simple Home Temperature Controller I put together.

### Arduino

The **arduino** module contains source code and **libraries** that run controller of temperature for Arduion board.

The module is setup to send frequent updates of temperature reading via USB Serial port and receive a desired temperature setup via the same port.

!["Image the entire Circuit" ][1]

Look at the code and see more images in the [Arduino][11] folder.


### Temperature sensor reading and REST Server

The **temperature-sensor-and-rest** contains REST Server. 
It's responsibility is to:

* receive room temperature updates from Arduino board via USB Serial Port
* send desired temperature to Arduino board via USB Serial Port
* Store current and historical temperature reading in the simple File based DB
* Send temperature updates to Ardino according to prepared schedule

More details in the [Module Readme][12].

### Controller web application designed for smartphones and tablets

The **ui/mobile-web-ui** contains code for a very simple User Interface, designed with Smartphones in mind. 
It's a set of static HTML files communicating with REST API, presenting current temperature reading and providing way of changing the desired setup.

You can also setup a schedule of temperature sets for the week, that can be activated from UI.

More detailed description in the [UI][10] folder.

!["Image of Schedule"][3] !["Image of Home Screen"][2]

## Contributions

All welcome.

----


[1]: arduino/circuit-smaller.png
[2]: ui/screens/HomeScreen.png
[3]: ui/screens/Schedule.png

[10]: ui/mobile-web-ui/README.md
[11]: arduino/README.md
[12]: temperature-sensor-and-rest/README.md