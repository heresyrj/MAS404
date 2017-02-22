'use strict';
var Alexa = require("alexa-sdk");
var appId = ''; //'amzn1.echo-sdk-ams.app.your-skill-id';

exports.handler = function(event, context, callback) {
    var alexa = Alexa.handler(event, context);
    alexa.appId = appId;
    
    alexa.registerHandlers(newSessionHandlers);
    alexa.execute();
};

var newSessionHandlers = {
    
    'AMAZON.HelpIntent': function() {
        this.emit(':ask', 'I am Minibay, your smart inventory and nutrition helper.' + ' Try ask me, if you have something.');
    },
    'AskInventoryIntent': function() {
    	var itemName = this.event.request.intent.slots.name.value;
    	var tempItem = new Item(itemName);
    	if (tempItem.status === true) {
		 this.emit(':tell', 'Yes, you bought '+tempItem.name+' on '+tempItem.lastInTime);
		}
		else {
		this.emit(':tell', 'You do not have'+tempItem.name);
		}
    },
    'AskWhenIntent': function() {
    	var itemName = this.event.request.intent.slots.name.value;
    	var tempItem = new Item(itemName);
    	if (tempItem.status === true) {
		 this.emit(':tell', 'You bought '+tempItem.name+' on '+tempItem.lastInTime+ ','+'You had better have it before '+tempItem.bestBefore);
		}
		else {
		this.emit(':tell', 'You do not have'+tempItem.name);
		}
    },
    "AMAZON.StopIntent": function() {
      this.emit(':tell', "Goodbye!");  
    },
    "AMAZON.CancelIntent": function() {
      this.emit(':tell', "Goodbye!");  
    },
    'SessionEndedRequest': function () {
        console.log('session ended!');
        //this.attributes['endedSessionCount'] += 1;
        this.emit(":tell", "Goodbye!");
    }
};

var Item = function (name){
	var time1 = new Date('15, Feberuary, 2017');
	var time2 = new Date('20, Feberuary, 2017');
	var time3 = new Date('28, Feberuary, 2017');
	var durationTime;
	if (name == "apple") {

		durationTime = 5;

		this.name = name;
		this.status = true;
		this.lastInTime = time1.toDateString();
		this.lastOutTime = time2.toDateString();
		this.bestBefore = time3.toDateString();

	}
	else if (name == "cake") {

		durationTime = 10;
		this.name = name;
		this.status = true;
		this.lastInTime = time1.toDateString();
		this.lastOutTime = time2.toDateString();
		this.bestBefore = time3.toDateString();
	}
	else {

		this.name = name;
		this.status = false;

	}


}

   





