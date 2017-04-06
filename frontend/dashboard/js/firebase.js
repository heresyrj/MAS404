// Initialize Firebase
var config = {
	apiKey: "AIzaSyBYQNdY_n-fX8Cu5V2assXqsMq720UxBoc",
	authDomain: "mas404-7d518.firebaseapp.com",
	databaseURL: "https://mas404-7d518.firebaseio.com",
	projectId: "mas404-7d518",
	storageBucket: "mas404-7d518.appspot.com",
	messagingSenderId: "908793150078"
};

firebase.initializeApp(config)

var database = firebase.database()

var inventory_ref = database.ref('inventory')

function parseTime(time) {
	var month = parseInt(time.substring(5, 7))
	var day = parseInt(time.substring(8, 10))
	var hour = parseInt(time.substring(10, 13))
	var min = parseInt(time.substring(14, 16))
	return [month, day, hour, min]
}


inventory_ref.once('value').then(function(dataSnapshot) {

	var list = dataSnapshot.val();

	var fruit_count = 0;
	var veg_count = 0;

	var fruit_category = [ "Apple", "Watermelon"]
	var veg_category = ["Broccoli", "Carrot", "Eggplant"]

	var fruit_orginal_list = []
	var veg_original_list = []

	var fruit_list = document.getElementById("fruit_list")
	for (var item in list) {

		var putinDate = list[item].putinDate
		var putoutDate = list[item].putoutDate

		var timeArray = parseTime(putinDate)
		console.log(timeArray[1])

		var newnode = document.createTextNode(item + "\n")
		fruit_list.appendChild(newnode)
		console.log(fruit_list)
	}

	for (var item in list) {
		if (fruit_category.includes(item) )  {
			fruit_count += 1 ;
			/* getElementById   set span of speicific paragraph */
			fruit_original_list.push(item);
			/* for fruit in fruit_original_list create element div, create element fruit_text, create corresponding_img, apeend text to div, append img to div, append div to fruit_list*/
		}
		else if (veg_category.includes(item)) {
			veg_count += 1 ;
			/* same for veg*/
		}	
	}

}


/*window.onload = function() {
	var chart = new CanvasJS.Chart("chartContainer", {
		title: {
			text: "",
			verticalAlign: 'top',
			horizontalAlign: 'center'
		},
		animationEnabled: true,
		data: [{
			type: "doughnut",
			startAngle: 0,
			toolTipContent: "{label}: {y} - <strong>#percent%</strong>",
			indexLabel: "{label} #percent%",
			dataPoints: [{
				y: 67,
				label: "Inbox"
			}, {
				y: 28,
				label: "Archives"
			}, {
				y: 10,
				label: "Labels"
			}, {
				y: 7,
				label: "Drafts"
			}, {
				y: 4,
				label: "Trash"
			}]
		}]
	});
	chart.render();
}*/