## RUNTIME VERIFICATION FOR SPATIO-TEMPORAL PROPERTIES OVER IOT NETWORKS [![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=oihanagarciaa_GBL-IoTMonitoringSTREL&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=oihanagarciaa_GBL-IoTMonitoringSTREL)

###### _Final degree project for taking the degree of_ : Degree in Computer engineering
___
### Middleware

<p>The Middleware connects different components or applications with each other.
This project uses SOA architecture.</p>
<img src="https://github.com/oihanagarciaa/GBL-IoTMonitoringSTREL/blob/main/Documents/Images/Middleware-Middleware%20diagram.drawio%20(2).png"/>
<p>These are the services you can find in this project: <br>
<strong><em>Runner service</em></strong><br>
The runner service receives the commands to <strong>set up the other services
</strong>. When the project is initialized, the runner subscribes to a broker
to listen to the client’s orders. The client can start the other services with
customized features and also stop them.
<br><strong><em>Moonlight service</em></strong><br>
This service <strong>stores</strong> the messages, is in charge of the <strong>
processing</strong> for Moonlight to be able to monitor and finally Moonlight
gives a <strong>feedback</strong>.
<br><strong><em>Sensor service</em></strong><br>
This service manages the <strong>data gathering</strong>. It gets the sensor's messages.
<br><strong><em>Thingsboard service</em></strong><br>
Sends the results and the sensors values to Thingsboard to <strong>present them
to the human operator</strong>

The services communicate using a unique Data Bus, a singleton class.
</p>

### Client
<p>The client sends a JSON to the Runner with the list of services and their 
properties. Here is an example:</p>

```json
{
	"services": [
		{
			"serviceId": "sensorService",
			"command": "start",
			"serviceType": "sensors",
			"connection": {
				"type": "mqtt",
				"settings": {
					"broker": "tcp://stefanschupp.de:1883",
					"topic": "institute/thingy/#",
					"username": "oihana",
					"password": "22oihana22"
				}
			}
		},
		{
			"serviceType": "moonlight",
			"serviceId": "moonlightService",
			"command": "stop",
			"formula":  "import dsl.*
			            val temp = \"temperature\" greaterThan 10 
			            val humidity = \"humidity\" lessThan 10 

			            val f1 = temp and (eventually(humidity) within interval(0, 1)) 
			            or somewhere(humidity) 

			            Specification.formula = f1"
		},
		{
			"serviceId": "thingsboardService",
			"command": "start",
			"serviceType": "thingsboard",
			"connection": {
				"type": "mqtt",
				"settings": {
				"broker": "tcp://thingsboard.stefanschupp.de:1884",
				"topic": "v1/devices/me/telemetry"
				}
			},
			"devices": [
				{
					"identifier": "1",
					"accessKey": "v8iK9AKNXuRZNhIrzROu"
				},
				{
					"identifier": "2",
			    	        "accessKey": "q1qbXmY3KR51xhD24iHP"
			        }
		        ]
	        }
        ]
}
```
