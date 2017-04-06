'use strict';
var Alexa = require("alexa-sdk");
var https = require("https");
var capitalize = require('./');
// var firebase = require('firebase');
var firebaseHost = 'mas404-7d518.firebaseio.com';
var appId = 'amzn1.ask.skill.1d9c0a68-4703-42b2-ad2e-d2268bb6484e'; //'amzn1.echo-sdk-ams.app.your-skill-id';
var fs = require("fs");




function fbGet(key) {
    return new Promise((resolve, reject) => {
        var options = {
            hostname: firebaseHost,
            port: 443,
            path: key + ".json",
            method: 'GET'
        };
        var req = https.request(options, function(res) {
            res.setEncoding('utf8');
            var body = '';
            res.on('data', function(chunk) {
                body += chunk;
            });
            res.on('end', function() {
                resolve(JSON.parse(body))
            });
        });
        req.end();
        req.on('error', reject);
    });
}

function fbPut(key, value) {
    return new Promise((resolve, reject) => {
        var options = {
            hostname: firebaseHost,
            port: 443,
            path: key + ".json",
            method: 'PUT'
        };

        var req = https.request(options, function(res) {
            console.log("request made")
            res.setEncoding('utf8');
            var body = '';
            res.on('data', function(chunk) {
                body += chunk;
            });
            res.on('end', function() {
                resolve(body)
            });
        });
        req.end(JSON.stringify(value));
        req.on('error', reject);
    });
}


exports.handler = function(event, context, callback) {

    context.callbackWaitsForEmptyEventLoop = false; //<---Important

    var alexa = Alexa.handler(event, context);
    alexa.appId = appId;



    alexa.registerHandlers(newSessionHandlers);
    alexa.execute();
};


var newSessionHandlers = {
    'NewSession': function() {

        this.emit(':ask', 'I am Minibay, your smart inventory and nutrition helper.' + ' Try to ask me about your inventory.');
    },
    'AMAZON.HelpIntent': function() {
        this.emit(':ask', 'I am Minibay, your smart inventory and nutrition helper.' + ' Try ask me, if you have something.');
    },
    'AskInventoryIntent': function() {

        var today = new Date();

        var itemName = this.event.request.intent.slots.name.value;
        var tempItem = new Item(itemName);
        var msPerDay = 24 * 60 * 60 * 1000;
        // var bestBefore = tempItem.bestBefore.toDateString();

        fbGet("/inventory/" + this.event.request.intent.slots.name.value.toString().charAt(0).toUpperCase() + this.event.request.intent.slots.name.value.toString().slice(1)).then(res => {
            var putoutDate = res.putoutDate;
            var putinDate = new Date(res.putinDate);

            var freshness = 5 * (tempItem.durationTime - Math.round((today - putinDate) / msPerDay)) / tempItem.durationTime;
            var remain = Math.round(tempItem.durationTime - (today - putinDate) / msPerDay);
            var daysBefore = Math.round((today - putinDate) / msPerDay);
            var daysAfter = Math.round((today - putinDate - tempItem.durationTime) / msPerDay);
            // this.emit(':ask', 'Yes,  You bought ' + this.event.request.intent.slots.name.value+ 'on' + putinDate);


            if (putoutDate == "No") {

                if (freshness > 0) {
                    this.emit(':ask', 'Yes,  You bought ' + itemName + ' ' + remain + ' days ago' + '. What else can I help you?');
                } else {
                    this.emit(':ask', 'You bought ' + itemName + ' ' + daysBefore + ' days ago,' + ' It has already expired for ' + daysAfter + ' days and has turned bad. You had better get rid of it.');
                }
            } else {
                this.emit(':ask', 'No, You do not have ' + itemName + '. What else can I help you?');
            };

        });




        // if (tempItem.status === true) {
        //     var daysBefore = Math.round((today - tempItem.lastInTime) / msPerDay);
        //     var freshness = Math.round(5 * (tempItem.bestBefore - today) / (msPerDay * tempItem.durationTime));
        //     if (freshness > 0) {

        //         this.emit(':ask', 'You bought ' + tempItem.name + ' ' + daysBefore + ' days ago, ' + 'What else can I help you?');


        //     } else {
        //         var daysAfter = Math.round((tempItem.bestBefore - today) / msPerDay);
        //         this.emit(':ask', 'You bought ' + tempItem.name + ' ' + daysBefore + ' days ago,' + ' It has already expired for ' + daysAfter + ' days and has turned bad. You had better get rid of it.');


        //     }

        // } else {
        //     this.emit(':ask', 'You do not have' + tempItem.name + ' . What else can I help you?');
        // }
    },
    'AskWhenIntent': function() {

        var itemName = this.event.request.intent.slots.name.value;
        var tempItem = new Item(itemName);
        var today = new Date();
        var msPerDay = 24 * 60 * 60 * 1000;

        fbGet("/inventory/" + this.event.request.intent.slots.name.value.toString().charAt(0).toUpperCase() + this.event.request.intent.slots.name.value.toString().slice(1)).then(res => {
            var putoutDate = res.putoutDate;
            var putinDate = new Date(res.putinDate);

            var freshness = 5 * (tempItem.durationTime - Math.round((today - putinDate) / msPerDay)) / tempItem.durationTime;
            var bestBefore = tempItem.durationTime - Math.round((today - putinDate) / msPerDay);
            var daysBefore = Math.round((today - putinDate) / msPerDay);
            var daysAfter = Math.round((today - putinDate) / msPerDay) - tempItem.durationTime;
            // this.emit(':ask', 'Yes,  You bought ' + this.event.request.intent.slots.name.value+ 'on' + putinDate);


            if (putoutDate == "No") {
                
                if (freshness > 2) {

                    this.emit(':ask', 'You bought ' + itemName + ' ' + daysBefore + ' days ago,' + ' It is still very fresh. ' + 'What else can I help you?');
                } else if (freshness >= 0) {
                    this.emit(':ask', 'You bought ' + itemName + ' ' + daysBefore + ' days ago,' + ' It is turning bad. ' + 'You had better have it in ' + bestBefore + ' days. What else can I help you?');
                } else {
                    this.emit(':ask', 'You bought ' + itemName + ' ' + daysBefore + ' days ago,' + ' It has already expired for' + daysAfter + ' days and has turned bad. You had better get rid of it. ' + 'What else can I help you?');
                }
            } else {
                this.emit(':ask', 'You do not have ' + itemName + ". What else can I help you?");
            }

        });

    },

    'ListInvenotryIntent': function() {

        var contents = fs.readFileSync("test.json");
        var jsonObject = JSON.parse(contents);
        var array = " ";
        fbGet("/inventory").then(res => {

            var keys = Object.keys(res);
            for (var i = 0; i < keys.length; i++) {
                if (res[keys[i]].putoutDate == "No") {
                    var tempName = keys[i].toString().toLowerCase();
                    if (jsonObject[tempName].category == this.event.request.intent.slots.name.value) {
                        array = array.concat(keys[i], ",");
                    }
                }
               
            }
            this.emit(':ask', 'You have ' + array + ' What else can I help you with?');
        });


    },

    'WhatToEatIntent': function() {
        var today = new Date();
        var msPerDay = 3600 * 1000 * 24;
        var minTime = 20;
        var suggestedFood = "";
        var bestBefore;
        fbGet("/inventory").then(res => {
            var keys = Object.keys(res);
            for (var i = 0; i < keys.length; i++) {
                if (res[keys[i]].putoutDate == "No") {
                    var tempName = keys[i].toString().toLowerCase();
                    var tempItem = new Item(tempName);
                    var putinDate = new Date(res[keys[i]].putinDate);
                    var freshness = 5 * (tempItem.durationTime - Math.round((today - putinDate) / msPerDay)) / tempItem.durationTime;
                    var daysAfter = tempItem.durationTime - Math.round((today - putinDate) / msPerDay);
                    if (freshness > 0) {
                        if (daysAfter < minTime) {
                            minTime = daysAfter;
                            suggestedFood = tempName;
                           
                        }
                    }
                }

            }
             this.emit(':ask', 'I would suggest you have ' + suggestedFood + ' Because you need to have it in ' + minTime + ' days before it turns bad. What else can I help you with?');

        });
       




    },
    "AMAZON.StopIntent": function() {
        this.emit(':tell', "Ok. I will always be here. Goodbye!");
    },
    "AMAZON.CancelIntent": function() {
        this.emit(':tell', "Ok. I will always be here. Goodbye!");
    },

    'SessionEndedRequest': function() {
        console.log('session ended!');
        //this.attributes['endedSessionCount'] += 1;
        this.emit(":tell", "I will always be here. Goodbye!");
    }
};

var Item = function(name) {
   
    var durationTime;
    var putinDate;
    var putoutDate;
    var freshness;
    var remain;
    var daysBefore;
    var daysAfter;

    var contents = fs.readFileSync("test.json");
    var obj = JSON.parse(contents);
    this.durationTime = obj[name].expireDays;

}
