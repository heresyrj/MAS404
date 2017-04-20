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
let reminder_ref = database.ref("reminder");

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
let getIndexFromList = (name, list) => {
    let index = -1;
    list.forEach((o, i)=> {
        console.log(o.name, name);
        if(o.name === name) index = i;
    });
    return index;
}
constructLists = (name, info) => {
    let count = info.count;
    let putin = parseTime(info.putinDate);
    let putout = info.putoutDate !== "No" ? parseTime(info.putoutDate) : null;

    let newItem = {
        name: name,
        count: count,
        putin: putin,
        putout: putout
    };

    if (isFruit(name)) {
        let index = getIndexFromList(name, fruitList);
        if (putout !== null && index !== -1) {
            fruitList.splice(index, 1);
            fruit_count -= count;
            nutriData[3].consumed += 10;
        }
        
        if (putout === null && index === -1) {
            fruit_count += count;
            fruitList.push(newItem);
        }        
    } else if (isVegie(name)) {
        let index = getIndexFromList(name, vegieList);
        if (putout !== null && index !== -1) {
            vegieList.splice(index, 1);
            vegie_count -= count;
        }
        
        if (putout === null && index === -1) {
            vegie_count += count;
            vegieList.push(newItem);
        }       
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
    return containerWidth / 2 * 0.5;
}

// render 1: Full Inventory List
addToListView = () => {

    const data = [
        {
            name: "Fruits",
            list: fruitList
        },
        {
            name: "Vegatables",
            list: vegieList
        }
    ];
    document.getElementById("inventory-list").innerHTML = "";
    data.forEach(category => {
        if (category.list.length > 0) {
            let categoryItem = document.createElement("div");
            let categoryInner = `
                <header>
                    <h4>${category.name}</h4>
                    <p><span class="number">${category.list.reduce((value, item) => {
                        return item.count + value;
                    },0)}</span> items</p>
                </header>
                <ul class="flex-wrap">
                    ${
                        category.list.map(item => {
                            return `
                                <li class="size-thumb relative">
                                    <img src="img/${item.name.toLowerCase()}.png">
                                    <span class="dot">${item.count}</span>
                                </li>
                            `;
                        }).join("")
                    }
                </ul>
            `;
            categoryItem.className = "category-list";
            categoryItem.innerHTML = categoryInner;

            document.getElementById("inventory-list").appendChild(categoryItem);
        }
        
    });
};

//render 2 : Suggest to eat soon
addToEatSoonView = () => {
	const data = [
        {
            name: "broccoli",
            days: 3
        },
        {
            name: "eggplant",
            days: 5
        }
    ];

    document.getElementById("eat-soon-list").innerHTML = "";
    data.forEach(item => {
        let soonItem = document.createElement("li");
        const innerEl = `
            <img class="enhance" src="img/${item.name.toLowerCase()}.png">
            <p class="center ${(item.days <=3)?"red":"grey"}">${item.days} days left</p>
        `;
        soonItem.className = "size-thumb large";
        soonItem.innerHTML = innerEl;

        document.getElementById("eat-soon-list").appendChild(soonItem);
    });
}

//render 3: Recommended Dishes
addToRecDishView = () => {

}
let nutriData = [
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
             consumed: 700,
            recommended: 1000
        },
        {
            name: "Vitamin C",
             consumed: 40,
            recommended: 90
        }
    ];
//render 4: Nutrition Summary
addToNutritionView = () => {
  
    document.getElementById("daily-charts").innerHTML = "";
    nutriData.forEach(item => {
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

    document.getElementById("suggestion-list").innerHTML = "";
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
  addToListView();
  addToEatSoonView();
//   addToRecDishView();
  addToNutritionView();
  addToNuritionSuggestionView();
};

getTime = () => {
    let m_names = ["Jan", "Feb", "Mar", 
"Apr", "May", "Jun", "Jul", "Aug", "Sep", 
"Oct", "Nov", "Dec"];
    let d_names = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    let curDate = new Date();
    let html = `<h4 class="main-text" id="cur-time">${(curDate.getHours()<10)?`0${curDate.getHours()}`:curDate.getHours()}:${(curDate.getMinutes()<10)?`0${curDate.getMinutes()}`:curDate.getMinutes()}</h4>
                <p class="sub-text" id="date-info">${m_names[curDate.getMonth()]} ${curDate.getDate()}<br> ${d_names[curDate.getDay()]}</p>`;
    
    $("#time").html(html);
}

getWeather = (location, woeid) => {
    let d_names = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    let curDate = new Date();
    $.simpleWeather({
        location: location,
        woeid: "",
        unit: 'f',
        success: (weather) => {
            html = `
            <i class="wicon wicon-${weather.code}"></i>
            <p class="sub-text" id="h-l-temps">${weather.high}&deg;${weather.units.temp} / ${weather.low}&deg;${weather.units.temp}</p>
                <h4 class="main-text" id="cur-temp">${weather.temp}&deg;${weather.units.temp}</h4>
                `;

            let forecastHtml = "";
            for (let i = 0; i < 2; i++) {
                forecastHtml += `
                <div class="weather-item">
                    <i class="wicon wicon-${weather.forecast[i+1].code}"></i>
                    <p class="sub-text">${weather.forecast[i+1].high}&deg;${weather.units.temp}/${weather.forecast[i+1].low}&deg;${weather.units.temp}<br>${d_names[curDate.getDay()+i+1]}</p>
                </div>
                `;
            }

            $("#temps").html(html);
            $("#weather-forecast").html(forecastHtml);
        },
        error: (error) => {
            $("#temps").html('<p>'+error+'</p>');
        }
    })
}

setReminder = (msg) => {
    let html = `<p><i class="icon-bell"></i>  ${msg}</p>`;
    $("#reminder").html(html);
}

$(document).ready(function () {
    inventory_ref.on("value", dataSnapshot => {
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

    reminder_ref.on("value", dataSnapshot => {
        let reminder = dataSnapshot.val();
        console.log(reminder.First);
        setReminder(reminder.First);
    });

    let location="Atlanta";
    getWeather(location,"");

    if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                location=`${position.coords.latitude},${position.coords.longitude}`;
                getWeather(location);
                setInterval(getWeather(location), 600000);
            }
        );
    } else {
        setInterval(getWeather(location,""), 600000);
    }
    
    getTime();
    setInterval(getTime, 60000);
  
});