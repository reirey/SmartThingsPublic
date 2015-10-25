/**
 *  Sensibo
 *
 *  Copyright 2015 Eric Gosselin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient


preferences {
	//input("username", "text", title: "Username", description: "Your Sensibo username (usually an email address)")
	//input("password", "password", title: "Password", description: "Your Sensibo password")
	//input("apikey", "text", title: "Serial #", description: "The APIkey number of your Sensibo account")
}

metadata {
	definition (name: "SensiboPod", namespace: "EricG66", author: "Eric Gosselin", oauth: false) {
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
		capability "Polling"
        capability "Refresh"
        capability "Switch"
        capability "Thermostat"
        
		attribute "temperatureUnit", "string"
        
		command "getPodInfo"
		command "setFanLevel"
		command "setACMode"
		command "setPoint"
        command "getTemperature"
        command "getHumidity"
        command "generateEvent"
        command "levelUpDown"
        command "switchFanLevel"
        command "switchMode"
        command "raiseSetpoint"
        command "lowerSetpoint"

	}

	simulator {
		// TODO: define status and reply messages here
		
        // status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"
        status "temperature":"10"
        //status "humidity": "humidity"
        
        // reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
	}

	tiles(scale: 2) {
		// TODO: define your main and details tiles here
        
    	multiAttributeTile(name:"richcontact", type:"lighting", width:6, height:4) {
   			tileAttribute("device.targetTemperature", key: "PRIMARY_CONTROL") {
            	attributeState ("targetTemperature", label:'${currentValue} '+ TempUnit(), 
                backgroundColors:[
					[value: 15, color: "#153591"],
					[value: 18, color: "#1e9cbb"],
					[value: 21, color: "#90d2a7"],
					[value: 24, color: "#44b621"],
					[value: 27, color: "#f1d801"],
					[value: 30, color: "#d04e00"],
					[value: 33, color: "#bc2323"]
				])
       			//attributeState "on", label: '${temperature}',action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821"
      			//attributeState "off", label:'${name}',action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff"
    		}
            tileAttribute ("statusText", key: "SECONDARY_CONTROL") {
				attributeState "statusText", label:'${currentValue}'
			}
            //tileAttribute ("device.level", key: "VALUE_CONTROL") {
			//	attributeState "level", action: "levelUpDown"
	        //}
  		}
        
        standardTile("fanLevel", "device.fanLevel", width: 2, height: 2) {
            state "low", action:"switchFanLevel", backgroundColor:"#8C8C8D", icon:"http://i130.photobucket.com/albums/p242/brutalboy_photos/fan_low_2.png", nextState:"medium"
            state "medium", action:"switchFanLevel", backgroundColor:"#8C8C8D", icon:"http://i130.photobucket.com/albums/p242/brutalboy_photos/fan_medium_2.png", nextState:"high"
            state "high", action:"switchFanLevel", backgroundColor:"#8C8C8D", icon:"http://i130.photobucket.com/albums/p242/brutalboy_photos/fan_high_2.png", nextState:"auto"
            state "auto", action:"switchFanLevel", backgroundColor:"#8C8C8D", icon:"http://i130.photobucket.com/albums/p242/brutalboy_photos/fan_auto_2.png" , nextState:"low"
        }
        
        standardTile("upButtonControl", "device.targetTemperature", inactiveLabel: false, decoration: "flat") {
			state "setpoint", action:"raiseSetpoint", backgroundColor:"#d04e00", icon:"st.thermostat.thermostat-up"
		}
        
        standardTile("downButtonControl", "device.targetTemperature", inactiveLabel: false, decoration: "flat") {
			state "setpoint", action:"lowerSetpoint", backgroundColor:"#d04e00", icon:"st.thermostat.thermostat-down"
		}  
        
        standardTile("mode", "device.mode",  width: 2, height: 2) {
            state "cool", action:"switchMode", backgroundColor:"#0099FF", icon:"st.thermostat.cool", nextState:"heat"
            state "heat", action:"switchMode", backgroundColor:"#FF3300", icon:"st.thermostat.heat", nextState:"fan"
            state "fan", action:"switchMode", backgroundColor:"#e8e3d8", icon:"st.thermostat.fan-on", nextState:"cool"
            //e8e3d8
        }
        
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

        valueTile("targetTemperature", "device.targetTemperature", inactiveLabel: false, width: 2, height: 2) {
			state "targetTemperature", label:'${currentValue} ' + TempUnit(), backgroundColor:"#ffffff"
        }
        
        valueTile("statusText", "statusText", inactiveLabel: false, width: 2, height: 2) {
			state "statusText", label:'${currentValue}', backgroundColor:"#ffffff"
        }
        
		controlTile("coolSliderControl", "device.coolingSetpoint", "slider", height: 1, width: 2, inactiveLabel: false,range:"(16..30)") {
			state "setCoolingSetpoint", label:'Set temperature to', action:"thermostat.setCoolingSetpoint",
				backgroundColors:[
					[value: 15, color: "#153591"],
					[value: 18, color: "#1e9cbb"],
					[value: 21, color: "#90d2a7"],
					[value: 24, color: "#44b621"],
					[value: 27, color: "#f1d801"],
					[value: 30, color: "#d04e00"],
					[value: 33, color: "#bc2323"]
				]
		}

		controlTile("heatSliderControl", "device.heatingSetpoint", "slider", height: 1, width: 2, inactiveLabel: false, range:"(16..30)") {
			state "setHeatingSetpoint", label:'Set temperature to', action:"thermostat.setHeatingSetpoint",
            	backgroundColors:[
					[value: 15, color: "#153591"],
					[value: 18, color: "#1e9cbb"],
					[value: 21, color: "#90d2a7"],
					[value: 24, color: "#44b621"],
					[value: 27, color: "#f1d801"],
					[value: 30, color: "#d04e00"],
					[value: 33, color: "#bc2323"]
				]
		}
        
        valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'Temp: ${currentValue} ' + TempUnit(), unit:TempUnit(),
				backgroundColors:[
					[value: 15, color: "#153591"],
					[value: 18, color: "#1e9cbb"],
					[value: 21, color: "#90d2a7"],
					[value: 24, color: "#44b621"],
					[value: 27, color: "#f1d801"],
					[value: 30, color: "#d04e00"],
					[value: 33, color: "#bc2323"]
				]
			)
		}
        
        valueTile("humidity", "device.humidity", width: 2, height: 2) {
			state("humidity", label:'Humidity: ${currentValue} %', unit:"C",
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
        
        standardTile("on", "device.on", width: 2, height: 2, canChangeIcon: true) {
			state "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821"
			state "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff"
		}
        
        ////
        standardTile("thermostatMode", "device.thermostatMode", inactiveLabel: true, decoration: "flat") {
			state("cool", action:"thermostat.heat", icon: "st.thermostat.cool")
			state("heat", action:"thermostat.auto", icon: "st.thermostat.heat")
            state("auto", action:"thermostat.cool", icon: "st.thermostat.fan-on")
		}
        
        standardTile("thermostatFanMode", "device.thermostatFanMode", inactiveLabel: true, decoration: "flat") {
			state "auto", action:"thermostat.fanOn", icon: "http://i130.photobucket.com/albums/p242/brutalboy_photos/fan_auto_2.png" //auto
			state "on", action:"thermostat.fanCirculate", icon: "http://i130.photobucket.com/albums/p242/brutalboy_photos/fan_medium_2.png" //medium
			state "circulate", action:"thermostat.fanAuto", icon: "http://i130.photobucket.com/albums/p242/brutalboy_photos/fan_low_2.png" //low
		}
        
        valueTile("heatingSetpoint", "device.heatingSetpoint") {
			state "default", label:'${currentValue}°', unit:"Heat", backgroundColor:"#bc2323"
		}

		valueTile("coolingSetpoint", "device.coolingSetpoint") {
			state "default", label:'${currentValue}°', unit:"Cool", backgroundColor:"#1e9cbb"
		}
        ////
		       
		main (["on"])
		//main "switch"
        //"coolSliderControl","heatSliderControl"
		details (["richcontact","on","temperature","humidity", "fanLevel","mode","upButtonControl","downButtonControl","refresh"])    
        //details (["temperature","humidity","switch","statusText","targetTemperature","refresh", "fanLevel"])
	}
}

def fanLevelModes() {
	["low", "medium","high", "auto"]
}

def Modes() {
   ["cool","heat","fan"]
}

def levelUpDown(value) {
    //log.trace "levelUpDown called with value $value" // Values are 0 and 1
    //humidity = "10"
}


////
def heat() {
    device.currentState("mode").value = "heat"
    modeHeat()
}

def emergencyHeat() {
	device.currentState("mode").value = "heat"
    modeHeat()
}

def cool() {
	device.currentState("mode").value = "cool"
    modeCool()
}

def auto() {
	device.currentState("mode").value = "fan"
    modeFan()
}

def fanOn(){
	device.currentState("fanLevel").value = "medium"
    fanMedium()
}

def fanAuto(){
	device.currentState("fanLevel").value = "auto"
    fanAuto()
}

def fanCirculate(){
	device.currentState("fanLevel").value = "low"
    fanLow()
}

////

void lowerSetpoint() {
   	log.debug "Lower SetPoint"
    
	//def mode = device.currentValue("thermostatMode")
    def Setpoint = device.currentValue("targetTemperature").toInteger()
   
    log.debug "Current target temperature = ${Setpoint}"

    Setpoint--  
        
    if (Setpoint < 16)
    	Setpoint = 16

    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    def result = parent.setACStates(this, device.deviceNetworkId , "on", device.currentState("mode").value, Setpoint, device.currentState("fanLevel").value)
    
    sendEvent(name: 'coolingSetpoint', value: Setpoint)
    sendEvent(name: 'heatingSetpoint', value: Setpoint)
    generateSetTempEvent(Setpoint)
        
    log.debug "New target Temperature = ${Setpoint}"       

	generateStatusEvent()
}

void raiseSetpoint() {
   	log.debug "Lower SetPoint"
    
	//def mode = device.currentValue("thermostatMode")
    def Setpoint = device.currentValue("targetTemperature").toInteger()
   
    log.debug "Current target temperature = ${Setpoint}"

    Setpoint++  
        
    if (Setpoint > 30)
    	Setpoint = 30

    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    def result = parent.setACStates(this, device.deviceNetworkId , "on", device.currentState("mode").value, Setpoint, device.currentState("fanLevel").value)
    
    sendEvent(name: 'coolingSetpoint', value: Setpoint)
    sendEvent(name: 'heatingSetpoint', value: Setpoint)
    generateSetTempEvent(Setpoint)
        
    log.debug "New target Temperature = ${Setpoint}"       

	generateStatusEvent()
}

def TempUnit() { 
	return "C"
}

def refresh()
{
  log.debug "refresh called"
  poll()
   
  log.debug "refresh ended"
}

// Set Temperature
def setCoolingSetpoint(temp) {
	log.debug "setTemperature"
    
    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
  	generateModeEvent("cool")
    
    def result = parent.setACStates(this, device.deviceNetworkId , "on", "cool", temp, device.currentState("fanLevel").value)
    
    sendEvent(name: 'coolingSetpoint', value: temp)
    sendEvent(name: 'heatingSetpoint', value: temp)
    generateSetTempEvent(temp)
    
    generateStatusEvent() 
}

def setHeatingSetpoint(temp) {
	log.debug "setTemperature"
    
    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    generateModeEvent("heat")
    
    def result = parent.setACStates(this, device.deviceNetworkId , "on", "heat", temp, device.currentState("fanLevel").value)
    
    sendEvent(name: 'coolingSetpoint', value: temp)
    sendEvent(name: 'heatingSetpoint', value: temp)
    generateSetTempEvent(temp)
    
    generateStatusEvent() 
}

def generateSetTempEvent(temp) {
   sendEvent(name: "targetTemperature", value: temp, descriptionText: "$device.displayName set temperature is now ${temp}", displayed: true, isStateChange: true)
}

// Turn off or Turn on the AC
def on() {
	log.debug "on called"
    
    parent.setACStates(this, device.deviceNetworkId, "on", device.currentState("mode").value, device.currentState("targetTemperature").value, device.currentState("fanLevel").value)
    generateSwitchEvent("on")
    
    generateStatusEvent() 

}

def off() {
	log.debug "off called"
    parent.setACStates(this, device.deviceNetworkId, "off", device.currentState("mode").value, device.currentState("targetTemperature").value, device.currentState("fanLevel").value)
    generateSwitchEvent("off")
    
    generateStatusEvent() 
}

def generateSwitchEvent(mode) {
   sendEvent(name: "on", value: mode, descriptionText: "$device.displayName is now ${mode}", displayed: true, isStateChange: true)
}

// For Fan Level
def dfanLow() {
	log.debug "fanLow"

    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    def result = parent.setACStates(this, device.deviceNetworkId ,"on", device.currentState("mode").value, device.currentState("targetTemperature").value, "low")
    log.debug result
    
    sendEvent(name: 'thermostatFanMode', value: "circulate")
    generatefanLevelEvent("low")
    
    generateStatusEvent() 
}

def dfanMedium() {
	log.debug "fanMedium"

	if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    parent.setACStates(this, device.deviceNetworkId,"on", device.currentState("mode").value, device.currentState("targetTemperature").value, "medium")
   
    sendEvent(name: 'thermostatFanMode', value: "on")
    generatefanLevelEvent("medium")
    
    generateStatusEvent() 
}

def dfanHigh() {
	log.debug "fanHigh"
    
    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    parent.setACStates(this, device.deviceNetworkId,"on", device.currentState("mode").value, device.currentState("targetTemperature").value, "high")
    
    sendEvent(name: 'thermostatFanMode', value: "on")
    generatefanLevelEvent("high")
    
    generateStatusEvent() 
}

def dfanAuto() {
	log.debug "fanAuto"
    
    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    parent.setACStates(this, device.deviceNetworkId, "on", device.currentState("mode").value, device.currentState("targetTemperature").value, "auto")
    
    sendEvent(name: 'thermostatFanMode', value: "auto")
    generatefanLevelEvent("auto")
    
    generateStatusEvent() 
}

def generatefanLevelEvent(mode) {
   sendEvent(name: "fanLevel", value: mode, descriptionText: "$device.displayName fan level is now ${mode}", displayed: true, isStateChange: true)
}

def switchFanLevel() {
	log.debug "switchFanLevel"
	def currentFanMode = device.currentState("fanLevel")?.value
	log.debug "switching fan level from current mode: $currentFanMode"
	def returnCommand

	switch (currentFanMode) {
		case "low":
			returnCommand = dfanMedium()
			break
		case "medium":
			returnCommand = dfanHigh()
			break
		case "high":
			returnCommand = dfanAuto()
			break
        case "auto":
			returnCommand = dfanLow()
			break
	}
	//if(!currentFanMode) { returnCommand = switchToFanMode("fanOn") }
	returnCommand
}

// To change the AC mode

def switchMode() {
	log.debug "switchMode"
	def currentMode = device.currentState("mode")?.value
	log.debug "switching AC mode from current mode: $currentMode"
	def returnCommand

	switch (currentMode) {
		case "cool":
			returnCommand = modeHeat()
			break
		case "heat":
			returnCommand = modeFan()
			break
		case "fan":
			returnCommand = modeCool()
			break
	}
	//if(!currentFanMode) { returnCommand = switchToFanMode("fanOn") }
	returnCommand
}

def modeHeat() {
	log.debug "modeHeat"
    
    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    //parent.setACStates(this, device.deviceNetworkId, device.currentState("on").value, "heat", device.currentState("targetTemperature").value, device.currentState("fanLevel").value)
    parent.setACStates(this, device.deviceNetworkId, "on", "heat", device.currentState("targetTemperature").value, device.currentState("fanLevel").value)
    
    sendEvent(name: 'thermostatMode', value: "heat")
    generateModeEvent("heat")
    
    generateStatusEvent()
}

def modeCool() {
	log.debug "modeCool"
    
    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    //parent.setACStates(this, device.deviceNetworkId, device.currentState("on").value, "cool", device.currentState("targetTemperature").value, device.currentState("fanLevel").value)
    parent.setACStates(this, device.deviceNetworkId, "on", "cool", device.currentState("targetTemperature").value, device.currentState("fanLevel").value)
    
    sendEvent(name: 'thermostatMode', value: "cool")
    generateModeEvent("cool")

    generateStatusEvent() 
}

def modeFan() {
	log.debug "modeFan"
    
    // adapted to my AC model
    def Level = (device.currentState("fanLevel").value == "auto") ? "high" : device.currentState("fanLevel").value
    
     if (Level != device.currentState("fanLevel").value) {
      generatefanLevelEvent("high")
    }
    
    if (device.currentState("on").value == "off") { generateSwitchEvent("on") }
    //parent.setACStates(this, device.deviceNetworkId, device.currentState("on").value, "fan", device.currentState("targetTemperature").value, Level)
    parent.setACStates(this, device.deviceNetworkId, "on", "fan", device.currentState("targetTemperature").value, Level)
    
    sendEvent(name: 'thermostatMode', value: "auto")
    generateModeEvent("fan")
   
    generateStatusEvent()
}

def generateModeEvent(mode) {
   sendEvent(name: "mode", value: mode, descriptionText: "$device.displayName mode is now ${mode}", displayed: true, isStateChange: true)
   
}

void poll() {
	log.debug "Executing 'poll' using parent SmartApp"
	
	def results = parent.pollChild(this)
	parseEventData(results)
	//generateStatusEvent()
}


def parseEventData(Map results)
{
	log.debug "parsing data $results"
	if(results)
	{
		results.each { name, value -> 
 
			def linkText = getLinkText(device)
            def isChange = false
            def isDisplayed = true
            if (name=="thermostatMode") {
            	def mode = (value.toString() != "fan") ?: "auto"
               	
                isChange = true //isTemperatureStateChange(device, name, value.toString())
                isDisplayed = isChange
                   
				sendEvent(
					name: name,
					value: value,
					linkText: linkText,
					descriptionText: getThermostatDescriptionText(name, value, linkText),
					handlerName: name,
					isStateChange: isChange,
					displayed: isDisplayed)
            	}
            else if (name=="thermostatFanMode") {
            	def mode = (value.toString() == "high" || value.toString() == "medium") ? "on" : value.toString()
                mode = (mode == "low") ? "circulate" : mode
               	
                isChange = true //isTemperatureStateChange(device, name, value.toString())
                isDisplayed = isChange
                   
				sendEvent(
					name: name,
					value: value,
					linkText: linkText,
					descriptionText: getThermostatDescriptionText(name, value, linkText),
					handlerName: name,
					isStateChange: isChange,
					displayed: isDisplayed)
            	}
            else if (name=="temperature" || name=="targetTemperature") {
				isChange = true //isTemperatureStateChange(device, name, value.toString())
                isDisplayed = isChange
                   
				sendEvent(
					name: name,
					value: value,
					unit: "C",
					linkText: linkText,
					descriptionText: getThermostatDescriptionText(name, value, linkText),
					handlerName: name,
					isStateChange: isChange,
					displayed: isDisplayed)
                    
            }
            else {
            	isChange = true//isStateChange(device, name, value.toString())
                isDisplayed = isChange
                
                sendEvent(
					name: name,
					value: value.toString(),
					linkText: linkText,
					descriptionText: getThermostatDescriptionText(name, value, linkText),
					handlerName: name,
					isStateChange: isChange,
					displayed: isDisplayed)
                    
            }
		}                                   									
		//generateSetpointEvent ()  
        generateStatusEvent ()
	}
}

private getThermostatDescriptionText(name, value, linkText)
{
	if(name == "temperature")
	{
		return "$name was $value " + TempUnit()
	}
    else if(name == "humidity")
	{
		return "$name was $value %"
    }
    else if(name == "targetTemperature")
	{
		return "latest temperature setpoint was $value " + TempUnit()
	}
	else if(name == "fanLevel")
	{
		return "latest fan level was $value"
	}
	else if(name == "on")
	{
		return "latest switch was $value"
	}
    else if (name == "mode")
    {
        return "thermostat mode was ${value}"
    }
    else if (name == "thermostatMode")
    {
        return "thermostat mode was ${value}"
    }
    else
    {
        //return "${name} = ${value}"
    }
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
    
    def name = null
	def value = null
    def statusTextmsg = ""   
    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)

	if (description?.startsWith("on/off:")) {
		log.debug "Switch command"
		name = "switch"
		value = description?.endsWith(" 1") ? "on" : "off"
	}
   else if (description?.startsWith("temperature")) {
    	log.debug "Temperature"
        name = "temperature"
		value = device.currentValue("temperature")
    }
    else if (description?.startsWith("humidity")) {
    	log.debug "Humidity"
        name = "humidity"
		value = device.currentValue("humidity")
    }
    else if (description?.startsWith("targetTemperature")) {
    	log.debug "targetTemperature"
        name = "targetTemperature"
		value = device.currentValue("targetTemperature")
    }
    else if (description?.startsWith("fanLevel")) {
    	log.debug "fanLevel"
        name = "fanLevel"
		value = device.currentValue("fanLevel")
    }
    else if (description?.startsWith("mode")) {
    	log.debug "mode"
        name = "mode"
		value = device.currentValue("mode")
    }
    else if (description?.startsWith("on")) {
    	log.debug "on"
        name = "on"
        value = device.currentValue("on")
    }
	// TODO: handle 'humidity' attribute
	// TODO: handle 'temperature' attribute
	// TODO: handle 'temperatureUnit' attribute
	
    def result = createEvent(name: name, value: value)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

def setPoint() {
	log.debug "Executing 'setPoint'"
	// TODO: handle 'setPoint' command
}

def generateStatusEvent() {
    log.debug device
    def temperature = device.currentValue("temperature").toDouble()  
    def humidity = device.currentValue("humidity").toDouble() 
    def targetTemperature = device.currentValue("targetTemperature").toDouble()
    def fanLevel = device.currentValue("fanLevel")
    def mode = device.currentValue("mode")
    def on = device.currentValue("on")

    
    log.debug "Generate Status Event for Mode = ${mode}"
    log.debug "Temperature = ${temperature}"
    log.debug "Humidity = ${humidity}"
    log.debug "targetTemperature = ${targetTemperature}"
    log.debug "fanLevel = ${fanLevel}"
    log.debug "mode = ${mode}"
	log.debug "switch = ${on}"
            
    //} else {
    
    //    statusText = "?"
        
    //}
    
    def statusTextmsg = "Temp: ${temperature} ${TempUnit()} humidity ${humidity}%"
    //sendEvent("name":"statusText", "value":statusTextmsg)
    
    //log.debug "Generate Status Event = ${statusText}"
    sendEvent("name":"statusText", "value":statusTextmsg, "description":"", displayed: false, isStateChange: true)
}
