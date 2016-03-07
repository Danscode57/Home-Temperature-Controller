Arduino code and electronic Circuit
===================================

Arduino module has 2 responsibilities: 

* 


The Elements that make the circuit for the Arduino controller contain:

* Digital Thermometer, I'm using (TODO: put a part number in here)
* 220V AC relay, I'm using (TODO:)
* A couple of resistors
* LED indicating when heating is switched on/off 

## The circuit


## The code

The code depends on 2 extra libraries that are included in this project: **OneWire** and **DallasTemperature**. 
Both libraries required by the Digital Thermometer.

In the code only Digital I/O pins are used for connecting all the elements.
I decided to use only 0.5 degree Celsius as a step in temperature reading hence reading normalization to the required precision.


Code for switching heating on/off is sending control signals to 2 digital outputs. 
The relay and configuration I'm using it in requires the Digital HIGH (5 V) to be present for the relay to remain open (open circuit). 
The second digital output is simply for the controll of the LED. Just a simple indication when heating is on. 


        void switchHeatingOn(){
          digitalWrite(HEATING_CONTROL_POWER, LOW);
          digitalWrite(HEATING_CONTROL_2, HIGH);
        }
        
        void switchHeatingOff(){
          digitalWrite(HEATING_CONTROL_POWER, HIGH);
          digitalWrite(HEATING_CONTROL_2, LOW);
        }

