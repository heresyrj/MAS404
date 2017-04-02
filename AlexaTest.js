'use strict';
var Alexa = require("alexa-sdk");
// let firebase = require('firebase');
var appId = 'amzn1.ask.skill.1d9c0a68-4703-42b2-ad2e-d2268bb6484e'; //'amzn1.echo-sdk-ams.app.your-skill-id';

exports.handler = function(event, context, callback) {

    // context.callbackWaitsForEmptyEventLoop = false; //<---Important

    // var config = {

    //     apiKey: "AIzaSyCTuCRzvQCLfEk0VLAaEAy26nFWhSxXfn0",
    //     authDomain: "mas404-7d518.firebaseapp.com",
    //     databaseURL: "https://mas404-7d518.firebaseio.com",
    //     storageBucket: "mas404-7d518.appspot.com",
    //     messagingSenderId: "908793150078"

    // };

    // if (firebase.apps.length === 0) { // <---Important!!! In lambda, it will cause double initialization.
    //     firebase.initializeApp(config);
    // }
    var alexa = Alexa.handler(event, context);
    alexa.appId = appId;

    alexa.registerHandlers(newSessionHandlers);
    alexa.execute();
};


// let database = firebase.database();


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
        var bestBefore = tempItem.bestBefore.toDateString();
        if (tempItem.status === true) {
            var daysBefore = Math.round((today - tempItem.lastInTime) / msPerDay);
            var freshness = Math.round(5 * (tempItem.bestBefore - today) / (msPerDay * tempItem.durationTime));
            if (freshness > 0) {

                this.emit(':ask', 'You bought ' + tempItem.name + ' ' + daysBefore + ' days ago, ' + 'What else can I help you?');

                // let FACTS = [
                //     "A year on Mercury is just 88 days long.",
                //     "Despite being farther from the Sun, Venus experiences higher temperatures than Mercury.",
                //     "Venus rotates counter-clockwise, possibly because of a collision in the past with an asteroid."
                // ];

                // // Get a random space fact from the space facts list
                // let factIndex = Math.floor(Math.random() * FACTS.length);
                // let randomFact = FACTS[factIndex];

                // // Create speech output
                // let speechOutput = "Here's your fact: " + randomFact;

                // let today = new Date();
                // let time = today.toLocaleString();

                // database.ref().push({
                //     fact: randomFact,
                //     timestamp: time
                // }, () => {
                //     this.emit(':tellWithCard', speechOutput, "firebas", randomFact)
                // });
            } else {
                var daysAfter = Math.round((tempItem.bestBefore - today) / msPerDay);
                this.emit(':ask', 'You bought ' + tempItem.name + ' ' + daysBefore + ' days ago,' + ' It has already expired for ' + daysAfter + ' days and has turned bad. You had better get rid of it.');

            }

        } else {
            this.emit(':ask', 'You do not have' + tempItem.name + ' . What else can I help you?');
        }
    },
    'AskWhenIntent': function() {
        var itemName = this.event.request.intent.slots.name.value;
        var tempItem = new Item(itemName);
        var today = new Date();
        var msPerDay = 24 * 60 * 60 * 1000;


        if (tempItem.status === true) {
            var daysBefore = Math.round((today - tempItem.lastInTime) / msPerDay);
            var bestBefore = Math.round((tempItem.bestBefore - today) / msPerDay);
            var freshness = Math.round(5 * (tempItem.bestBefore - today) / (msPerDay * tempItem.durationTime));
            if (freshness > 2) {

                this.emit(':ask', 'You bought ' + tempItem.name + ' ' + daysBefore + ' days ago,' + ' It is still very fresh. ' + 'What else can I help you?');
            } else if (freshness >= 0) {
                this.emit(':ask', 'You bought ' + tempItem.name + ' ' + daysBefore + ' days ago,' + ' It is turning bad. ' + 'You had better have it in ' + bestBefore + ' days. What else can I help you?');
            } else {
                var daysAfter = Math.round((tempItem.bestBefore - today) / msPerDay);
                this.emit(':ask', 'You bought ' + tempItem.name + ' ' + daysBefore + ' days ago,' + ' It has already expired for' + daysAfter + ' days and has turned bad. You had better get rid of it. ' + 'What else can I help you?');

            }
        } else {
            this.emit(':ask', 'You do not have' + tempItem.name + ". What else can I help you?");
        }
    },

    'ListInvenotryIntent': function() {
        var itemName = this.event.request.intent.slots.name.value;
        if (itemName == 'fruit') {
            this.emit(':ask', 'You have apple, blueberry, banana and orange.' + ' What else can I help you with?');

        } else if (itemName == 'meat') {
            this.emit(':ask', 'You have beef, salmon and chickenbreast.' + ' What else can I help you with?');

        }


    },
    'WhatToEatIntent': function() {
        var tempItem = new Item("chickenbreast");
        var today = new Date();
        var msPerDay = 3600 * 1000 * 24;
        var bestBefore = Math.round((tempItem.bestBefore - today) / msPerDay);
        this.emit(':ask', 'I would suggest you have chicken breast. Because you need to have it in ' + bestBefore + ' days before it turns bad. You can have it with broccoli and mushroom. What else can I help you with?');

    },
    "AMAZON.StopIntent": function() {
        this.emit(':tell', "Ok. Goodbye!");
    },
    "AMAZON.CancelIntent": function() {
        this.emit(':tell', "Ok. Goodbye!");
    },

    'SessionEndedRequest': function() {
        console.log('session ended!');
        //this.attributes['endedSessionCount'] += 1;
        this.emit(":tell", "Goodbye!");
    }
};

var Item = function(name) {
    var time1 = new Date('7, March, 2017');
    var time4 = new Date('1, March, 2017');
    var time2 = new Date('1, march, 2017');
    var time3 = new Date('12, March, 2017');
    var durationTime;
    if (name == "apple") {

        this.durationTime = 10;

        this.name = name;
        this.status = true;
        this.lastInTime = time1;
        this.lastOutTime = time2;
        this.bestBefore = new Date(time1.getTime() + this.durationTime * 3600 * 24 * 1000);



    } else if (name == "chickenbreast") {

        this.durationTime = 7;
        this.name = name;
        this.status = true;
        this.lastInTime = time2;
        this.lastOutTime = time3;
        this.bestBefore = new Date(time1.getTime() + this.durationTime * 3600 * 24 * 1000);


    } else if (name == "orange") {

        this.durationTime = 4;
        this.name = name;
        this.status = true;
        this.lastInTime = time4;
        this.lastOutTime = time3;
        this.bestBefore = new Date(time4.getTime() + this.durationTime * 3600 * 24 * 1000);


    } else {

        this.name = name;
        this.status = false;

    }


}

