let cv = require('./cv.js')

let url = "http://thescienceofeating.com/wp-content/uploads/2011/12/Book-Fruits-Header.jpg"
let url2 = "https://storage.googleapis.com/mas404-7d518.appspot.com/fruits.jpg"



cv.getPrediction(url2).then(res => console.log(res)).catch(err => console.log(err))