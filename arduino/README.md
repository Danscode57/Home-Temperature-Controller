Arduino code and electronic Circuit
===================================


Arduino module has 2 responsibilities: 

* Read temperature from the Digital Thermometer and send updates via USB
* Switch the Relay on/off depending on the temperature set and temperature read.


## The circuit

!["Hand drawn circuit"][1]

In the circuit I'm using:

* Digital Thermometer **DS 18B20**, that uses OneWire communication protocol, [Datasheet][11]
* 230 V AC Relay, for switching the power ON/OFF to the central heating system. Relay is controlled with 5V voltage.
* LED, any would do, just for indication of the Heating being ON/OFF.
* resistors, 220 Ohm and 4.7 Kilo Ohm

## The code

The code depends on 2 external libraries that are included in this project: **OneWire** and **DallasTemperature**. 
Both libraries required by the Digital Thermometer.

In the code only Digital I/O pins are used for connecting all the elements.
I decided to use only 0.5 degree Celsius as a step in temperature reading hence reading normalization to the required precision.


Code for switching heating on/off is sending control signals to 2 digital outputs. 
The relay and configuration I'm using it in requires the Digital HIGH (5 V) to be present for the relay to remain open (open circuit). 
The second digital output is simply for the controll of the LED. Just a simple indication when heating is on. 

```c
void switchHeatingOn(){
  digitalWrite(HEATING_CONTROL_POWER, LOW);
  digitalWrite(HEATING_CONTROL_2, HIGH);
}

void switchHeatingOff(){
  digitalWrite(HEATING_CONTROL_POWER, HIGH);
  digitalWrite(HEATING_CONTROL_2, LOW);
}
        
```

Full code listing in [arduino C file][12].

[1]: circuit-smaller.png
[11]: https://datasheets.maximintegrated.com/en/ds/DS18B20.pdf
[12]: temperature/temperature.ino