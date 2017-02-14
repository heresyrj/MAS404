let cv = require('./cv.js')

let url = "http://thescienceofeating.com/wp-content/uploads/2011/12/Book-Fruits-Header.jpg"

cv.getPrediction(url).then(res => console.log(res))