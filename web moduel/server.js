

var express = require('express');

var expressLayout = require('express-ejs-layouts');
var app=  express();
var port= 8081;


// app.use(express.bodyParser());

//setting layout
app.set('view engine', 'ejs');
app.use(expressLayout);


//route our app
var router = require('./app/routes');
app.use('/',router);





//setting static files
app.use(express.static(__dirname+'/public'));

app.listen(port, function(){
console.log('app started');



});
