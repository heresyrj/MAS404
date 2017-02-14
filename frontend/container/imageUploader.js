let fs = require("fs")

const storage = require('@google-cloud/storage')

// Global Ref Basis
let gcs = storage({
  projectId: 'mas404-7d518',
  keyFilename: './meta/MAS404-0beb892b24b8.json'
});
// Reference an existing bucket.
let bucket = gcs.bucket('mas404-7d518.appspot.com')

module.exports.loadImage = (url) => {
    return new Promise( (resolve, reject) => { 
        bucket.upload(url, (err, file) => {
            if (!err) resolve([true,file])
            else reject(err)
        });
    })
}

loadImage('./fruits.jpg').then(res => { console.log(res[0])} )