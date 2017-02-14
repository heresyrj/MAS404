let Clarifai = require("clarifai");
let Account = require("./account.json");

let app = new Clarifai.App(`${Account.clientId}`, `${Account.clientSecret}`);

// predict the contents of an image by passing in a path (url)

module.exports.getPrediction = url => {
  return new Promise((resolve, reject) => {
    //resolve
    app.models
      .predict(Clarifai.FOOD_MODEL, url)
      .then(response => {
        let concepts = response.outputs[0].data.concepts
        let rawPredicts = concepts.map(item => obj = {name: item.name, value: item.value} )
        
        resolve(rawPredicts);
        //reject
      })
      .catch(err => reject(err));
  });
};
