#include <dht11.h>

dht11 DHT11;

#define DHT11_PIN 2
#define LED_PIN 13
#define LDR_PIN 0

String readSerial = "";
String outputSerial = "";

int ldr = 0;
int chk = 0;
float humidity = 0.0;
float temperature = 0.0;

void setup()  {
  Serial.begin(9600);
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  if (Serial.available() > 0) {
    switch ((char)Serial.read()) {
      case 'l':
      case 'L':
        digitalWrite(LED_PIN, HIGH);
        break;
      
      case 'd':
      case 'D':
        digitalWrite(LED_PIN, LOW);
        break;
        
      case 'r':
      case 'R':
        chk = DHT11.read(DHT11_PIN);
        humidity = (float)DHT11.humidity;
        temperature = (float)DHT11.temperature;
        ldr = analogRead(ldr);
        
        outputSerial = "r;";
        outputSerial += (int)temperature;
        outputSerial += ";";
        outputSerial += (int)humidity;
        outputSerial += ";";
        outputSerial += ldr;
        outputSerial += ";";
        Serial.println(outputSerial);
        break;  
    }
  }
}

