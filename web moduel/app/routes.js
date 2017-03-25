
var express = require('express');
var request = require('request');
var path =  require('path');
var config = require('./config.js');
var getGreetings = require('./greetings.js');
var builder = require('botbuilder');
var  recast = require('recastai');
var router = express.Router();


//export
module.exports = router;

var recastClient = new recast.Client(config.recast);
var connector = new builder.ChatConnector({
  appId: config.appId,
  appPassword: config.appPassword,
})


var bot = new builder.UniversalBot(connector);
// Event when Message received
bot.dialog('/', (session) => {
	recastClient.textRequest(session.message.text)
  .then(res => {
   var intent = res.intent()
   var entity = res.get('action');
   console.log(intent)
    session.send(`Intent: ${intent.slug}`)
    session.send(`Entity: ${getGreetings()}`)
    session.send(`Entity: ${entity}`)

   if (intent === 'greetings') {
  session.send(getGreetings());
  } else if (intent === 'infopokemon') {
 // session.send(getInfoPokemon(entity));
}
} )
  .catch(() => session.send('I need some sleep right now... Talk to me later!'))
  console.log(session.message.text)
})


router.post('/', connector.listen());

//route for homepage
router.get('/', function(req, res){
//res.send('hello! world asdfsadf');
res.render('pages/index');
//res.sendFile(path.join(__dirname,'../index.html'));
});

//route for homepage
router.get('/about', function(req, res){
//res.send('hello! world i am the about page');
res.render('pages/about');
});

router.get('/download', function(req, res){
//res.send('hello! world i am the about page');
res.render('pages/downloadTemplate');
});

//route for homepage
router.get('/uplolad', function(req, res){
res.render('pages/uplolad');
});




router.post('/contact');