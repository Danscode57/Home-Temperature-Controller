#include <OneWire.h>
#include <DallasTemperature.h>

#define ONE_WIRE_BUS 7

#define HEATING_CONTROL_POWER 8 
#define HEATING_CONTROL_2 12 

#define TEMP_CORRECTION 0.5
#define MAX_REPETITIONS 10

// Some constants
const float baselineTemp = 20.0;
const float initialTemperature = 19.0;
const float tempStep = 0.5;

// Variables from now on
bool control = false;
float setTemperature = initialTemperature;
float currentTemperature = initialTemperature;

int repetitions = 0;

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

void setup() {
  pinMode(HEATING_CONTROL_POWER, OUTPUT);
  pinMode(HEATING_CONTROL_2, OUTPUT);
  
  digitalWrite(HEATING_CONTROL_POWER, HIGH);
  digitalWrite(HEATING_CONTROL_2, LOW);

  sensors.begin();
  
  Serial.begin(9600);
  Serial.setTimeout(100);

  // Intial Temperature reading
  sensors.requestTemperatures();
  currentTemperature = normalizeTemperatureToPrecision(sensors.getTempCByIndex(0), 0.5) - TEMP_CORRECTION;
}

void loop() {
  sensors.requestTemperatures();
  float temp = normalizeTemperatureToPrecision(sensors.getTempCByIndex(0), 0.5) - TEMP_CORRECTION;
  
  if (temp != currentTemperature) {
    if (repetitions > MAX_REPETITIONS){
      currentTemperature = temp;
      repetitions = 0;
    } else {
      repetitions++;
    }
  }
  
  bool heatingOn = shouldSwitchHeatingOn(currentTemperature, setTemperature);
    
  if (heatingOn){
    switchHeatingOn();
  } else {
    switchHeatingOff();
  }
   
  float setCommand = Serial.parseFloat();
  if (setCommand != 0){
    setTemperature = normalizeTemperatureToPrecision(setCommand, 0.5);
    Serial.println("SET:"+String(setCommand));
  }
  
  String forSerial = 
    "CURR:"+String(currentTemperature) +
    ";SET:"+String(setTemperature) + 
    ";HEATING:"+String(heatingOn);
    
  Serial.println(forSerial);
  
  control = false;
}

// Here be private functions
void switchHeatingOn(){
  digitalWrite(HEATING_CONTROL_POWER, LOW);
  digitalWrite(HEATING_CONTROL_2, HIGH);
}

void switchHeatingOff(){
  digitalWrite(HEATING_CONTROL_POWER, HIGH);
  digitalWrite(HEATING_CONTROL_2, LOW);
}

bool shouldSwitchHeatingOn(float currentTemp, float setTemp){
  if (currentTemp < setTemp){
    if (currentTemp > setTemp){
      return false;
    }
    return true;
  }
  return false;
}

float normalizeTemperatureToPrecision(float temp, float precission){
  return round(temp/0.5)*0.5;
}

