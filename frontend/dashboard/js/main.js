// Initialize Firebase
let config = {
    apiKey: "AIzaSyBYQNdY_n-fX8Cu5V2assXqsMq720UxBoc",
    authDomain: "mas404-7d518.firebaseapp.com",
    databaseURL: "https://mas404-7d518.firebaseio.com",
    projectId: "mas404-7d518",
    storageBucket: "mas404-7d518.appspot.com",
    messagingSenderId: "908793150078"
};

function loadJSON(path, callback) {

    var xobj = new XMLHttpRequest();
    xobj.overrideMimeType("application/json");
    xobj.open('GET', path, true); // Replace 'my_data' with the path to your file
    xobj.onreadystatechange = function () {
        if (xobj.readyState == 4 && xobj.status == "200") {
            // Required use of an anonymous callback as .open will NOT return a value but simply returns undefined in asynchronous mode
            callback(xobj.responseText);
        }
    };
    xobj.send(null);
}
let nutrtionData = null;
loadJSON("../nutrition.json", (responseText) => {
    nutrtionData = JSON.parse(responseText);
})

firebase.initializeApp(config);
let database = firebase.database();
let inventory_ref = database.ref("inventory");

function parseTime(time) {
    let month = parseInt(time.substring(5, 7));
    let day = parseInt(time.substring(8, 10));
    let hour = parseInt(time.substring(10, 13));
    let min = parseInt(time.substring(14, 16));
    return {
        month: month,
        day: day,
        hour: hour,
        min: min
    };
}

// category lists utility
const fruit_category = ["Apple", "Watermelon", "Banana"];
const vegie_category = ["Broccoli", "Carrot", "Eggplant"];
let vegie_count = 0;
let fruit_count = 0;
let fruitList = [];
let vegieList = [];
let isFruit = name => fruit_category.includes(name);
let isVegie = name => vegie_category.includes(name);
constructLists = (name, times) => {
    let putin = parseTime(times.putinDate);
    let putout = times.putoutDate !== "No" ? parseTime(times.putoutDate) : null;

    if (putout == null) {

    }

    let newItem = {
        name: name,
        putin: putin,
        putout: putout
    };
    if (isFruit(name)) {
        fruit_count++;
        fruitList.push(newItem);
    } else if (isVegie(name)) {
        vegie_count++;
        vegieList.push(newItem);
    }
};
constructNode = name => {
    name = name.toLowerCase();

    let newDiv = document.createElement("div");
    let newImg = document.createElement("img");
    newImg.src = "../img/" + name + ".png";
    newImg.style.width = "48px";
    newImg.style.height = "48px";
    newDiv.className = "inventory_item";
    newDiv.style.marginBottom = "8px";
    newDiv.appendChild(newImg);

    return newDiv;
};
let getChartWidth = () => {
    const containerWidth = document.getElementById("nutrition-report").offsetWidth - 64;
    console.log(containerWidth);
    return containerWidth / 2 * 0.8;
}

// render 1: Full Inventory List
addToListView = () => {
  fruitList.map(item => {
    document.getElementById("fruit_list").appendChild(constructNode(item.name));
    document.getElementById("fruitNum").innerHTML = fruit_count + " types";
  });
  vegieList.map(item => {
    document.getElementById("vegie_list").appendChild(constructNode(item.name));
    document.getElementById("vegieNum").innerHTML = vegie_count + " types";
  });
};

//render 2 : Suggest to eat soon
addToEatSoonView = () => {
	if(nutrtionData) {
		console.log(nutrtionData)
	}
}

//render 3: Recommended Dishes
addToRecDishView = () => {

}

//render 4: Nutrition Summary
addToNuritionView = () => {
    const data = [
        {
            name: "Energy",
             consumed: 1990,
            recommended: 2400
        },
        {
            name: "Fiber",
             consumed: 20,
            recommended: 30
        },
        {
            name: "Calcium",
             consumed: 400,
            recommended: 1000
        },
        {
            name: "Vitamin C",
             consumed: 75,
            recommended: 90
        }
    ];

    data.forEach(item => {
        let chartItem = document.createElement("div");
        const percentage = (item.consumed / item.recommended).toFixed(2) * 100;
        const innerEl = `<p class="chart-info"><span class="chart-name">${item.name}</span><br>${item.consumed}</p>`;
        const color = (percentage < 50) ? "#F67623" : "#20C164";
        chartItem.className = (percentage < 50) ? "chart red" : "chart";
        chartItem.setAttribute("data-percent", percentage);
        chartItem.innerHTML = innerEl;

        $(chartItem).easyPieChart({
            scaleColor: false,
            scaleLength: 0,
            barColor: color,
            lineWidth: 10,
            lineCap: "round",
            size: getChartWidth()
        });

        document.getElementById("daily-charts").appendChild(chartItem);
    });
}

//render 5: Nutrition Suggestion
addToNuritionSuggestionView = () => {
    const nutriSuggestions = ["apple","banana","broccoli"];

    nutriSuggestions.forEach(item => {
        let nutriItem = document.createElement("li");
        let nutriImg = document.createElement("img");
        nutriItem.className = "size-thumb";
        nutriImg.src = "../img/" + item + ".png";
        nutriItem.appendChild(nutriImg);
        document.getElementById("suggestion-list").appendChild(
            nutriItem
        );
    })
}

render = () => {
//   addToListView();
//   addToEatSoonView();
//   addToRecDishView();
  addToNuritionView();
  addToNuritionSuggestionView();
};




$(document).ready(function () {
    inventory_ref.once("value").then(dataSnapshot => {
        let list = dataSnapshot.val();
        let names = Object.keys(list);
        names.forEach(
            name => {
            constructLists(name, list[name]);
            },
            this
        );
        render();
    });
    $('.chart').easyPieChart({
        //your options goes here
        scaleColor: false,
        scaleLength: 0,
        barColor: "#20C164",
        lineWidth: 10,
        lineCap: "round",
        size: 160
    });
    $('.chart-red').easyPieChart({
        //your options goes here
        scaleColor: false,
        scaleLength: 0,
        barColor: "#F67623",
        lineWidth: 10,
        lineCap: "round",
        size: 160
    });
});