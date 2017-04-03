var fs = require("fs");
var contents = fs.readFileSync("test.json");
        var obj = JSON.parse(contents, function(key, value) {
                    if (key == "apple") {
                        // var tempDate = new Date(res.putinDate);
                        console.log("yes, apple")
                        // console.log("User Name:", );
                    } else {
                        
                        console.log("no")
                        
                    }
});

