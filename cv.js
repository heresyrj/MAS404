var Clarifai = require('clarifai');
var account = require('./account.json');
var app = new Clarifai.App(
  `${account.clientId}`,
  `${account.clientSecret}`
);

// predict the contents of an image by passing in a url
app.models.predict(Clarifai.FOOD_MODEL, 'http://thescienceofeating.com/wp-content/uploads/2011/12/Book-Fruits-Header.jpg').then(
  (response) => { 
      status = response.status;
      rawData = response.rawData;
      outputs = response.outputs;

      recognizedItems = outputs[outputs.length-1].data.concepts;
      for(let i = 0; i < recognizedItems.length; i++) {
          let item = recognizedItems[i];
          console.log(item.name, item.value);
      }
      console.log();
    for(let item in response) {
        
    }
  },
  (err) => {console.error(err);}
);

