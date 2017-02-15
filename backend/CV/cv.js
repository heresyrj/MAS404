const Clarifai = require("clarifai");
const Account = require("./meta/clarifaiaccount.json");
const Categories = require("./meta/categories.json");
const storage = require("@google-cloud/storage");

// Global Ref Basis
let gcs = storage({
  projectId: "mas404-7d518",
  keyFilename: "./meta/MAS404-0beb892b24b8.json"
});
// Reference an existing bucket.
let bucket = gcs.bucket("mas404-7d518.appspot.com");

// Init Clarifai API
const app = new Clarifai.App(`${Account.clientId}`, `${Account.clientSecret}`);

let retireveImagesMeta = () => {
  return new Promise((resolve, reject) => {
    bucket
      .getFiles()
      .then(data => {
        let files = data[0];
        // console.log(files[0][0].name)
        let filtered = files.map(file => {
          let meta = file.metadata;
          return obj = {
            name: meta.name,
            time: new Date(meta.timeCreated),
            link: (
              `https://storage.googleapis.com/mas404-7d518.appspot.com/${meta.name}`
            )
          };
        });
        resolve(filtered);
      })
      .catch(err => reject("err in retrieving imgs meta"));
  });
};

let getMostRecent = () => {
  return new Promise((resolve, reject) => 
    retireveImagesMeta().then(imgs => {
      let sorted = imgs.sort((a, b) => {
        return b.time.getTime() - a.time.getTime();
      });
      resolve(sorted[0])
    })).catch(err => console.log("err in getMostRecent"))
};

let categoryfilter = name => {
  for (let category in Categories) {
    return Categories[category].includes(name);
  }
};

//TODO:  next promises of getMostRecent and getPrediction to use the url of mostRecent
module.exports.getPrediction = url => {
  
  return new Promise((resolve, reject) => {
    //resolve
    app.models
      .predict(Clarifai.FOOD_MODEL, url)
      .then(response => {
        let concepts = response.outputs[0].data.concepts;
        let rawPredicts = concepts
          .map(item => obj = { name: item.name, value: item.value })
          .filter(item => categoryfilter(item.name));

        resolve(rawPredicts);
        //reject
      })
      .catch(err => reject("err in getPrediction"));
  });
};
