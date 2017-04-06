// Initialize Firebase
let config = {
  apiKey: "AIzaSyBYQNdY_n-fX8Cu5V2assXqsMq720UxBoc",
  authDomain: "mas404-7d518.firebaseapp.com",
  databaseURL: "https://mas404-7d518.firebaseio.com",
  projectId: "mas404-7d518",
  storageBucket: "mas404-7d518.appspot.com",
  messagingSenderId: "908793150078"
};

function loadJSON(path ,callback) {   

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

loadJSON("../nutrition.json", (responseText) => {
        data = JSON.parse(responseText);
		console.log(data)
})


// $.getJSON('../nutrition.json').done(json => {console.log(json)})


// let nutrition = JSON.parse();
// console.log(nutrition)

firebase.initializeApp(config);
let database = firebase.database();
let inventory_ref = database.ref("inventory");

function parseTime(time) {
  let month = parseInt(time.substring(5, 7));
  let day = parseInt(time.substring(8, 10));
  let hour = parseInt(time.substring(10, 13));
  let min = parseInt(time.substring(14, 16));
  return { month: month, day: day, hour: hour, min: min };
}

// category lists utility
const fruit_category = ["Apple", "Watermelon"];
const vegie_category = ["Broccoli", "Carrot", "Eggplant"];
let vegie_count = 0;
let fruit_count = 0;
let fruitList = [];
let vegieList = [];
isFruit = name => fruit_category.includes(name);
isVegie = name => vegie_category.includes(name);
constructLists = (name, times) => {
  let putin = parseTime(times.putinDate);
  let putout = times.putoutDate !== "No" ? parseTime(times.putoutDate) : null;
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
  newImg.src = "../pics/" + name + ".png";
  newImg.style.width = "48px";
  newImg.style.height = "48px";
  newDiv.className = "inventory_item";
  newDiv.style.marginBottom = "8px";
  newDiv.appendChild(newImg);

  return newDiv;
};

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

}

//render 3: Recommended Dishes
addToRecDishView = () => {

}

//render 4: Nutrition Summary
addToNuritionView = () => {

}

//render 5: Nutrition Suggestion
addToNuritionSuggestionView = () => {

}

render = () => {
  addToListView();
  addToEatSoonView();
  addToRecDishView();
  addToNuritionView();
  addToNuritionSuggestionView();
};

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