const CloudantSDK = require('@cloudant/cloudant');
const log4js = require('log4js');
const appName = require('./../package').name;
const logger = log4js.getLogger(appName);
logger.level = process.env.LOG_LEVEL || 'info';

const cloudant1 = 'https://e5417d38-080e-45be-b11a-c0c7a13b79a8-bluemix:2bb7d088fe6824ce2a4d6ace53c87c7ebfdf0a511f27b310b54188aae79d974e@e5417d38-080e-45be-b11a-c0c7a13b79a8-bluemix.cloudant.com';

if (process.env.CLOUDANT_URL) {
    var cloudant_db = require('@cloudant/cloudant')(process.env.CLOUDANT_URL); 
} else {
    var cloudant_db = require('@cloudant/cloudant')(cloudant1);
    logger.info("Using DB: " + cloudant1);
}

const authors = cloudant_db.use("authors");

// debug since db.create does not give a meaningful error message
// read hiscore db and print rows count to console.log
authors.view("authorview", "data_by_name", function (err, body, header) { 
    if (err) { 
        logger.error('Cloudant: ' + err ); } 
    else { 
        if ( JSON.stringify(body.rows.length) > 0 ) {  //do we have a result?
            logger.info("Test connect to authors_db successful -- rows count: " + JSON.stringify(body.total_rows)); }  
        else {
            logger.info("Test connect to authors_db successful -- no rows, authors db is empty"); 
        }
    } 
});

// Display details of single author
// Read a record
//
// GET w/ query
//
//  /getauthor?name=firstname%20lastname
//
exports.author_get = function(req, res, next) {  
    var name = req.query.name;
    logger.info('Query for: ' + name);
    authors.view("authorview", "data_by_name", {key: name}, function (err, body, header) { 
        if (err) { 
            res.writeHead(500, { "Content-Type": "application/json" }); 
            res.end('{"error": "' + err +  '"}');
            logger.error('Cloudant read failed ' + err); 
        } else {
            if ( JSON.stringify(body.rows.length) > 0 ) {  //do we have a result?
                var _n = body.rows[0].value.name;
                var _t = body.rows[0].value.twitter;
                var _b = body.rows[0].value.blog;
                var authorData = JSON.stringify({'name': _n, 'twitter': _t, 'blog': _b});
                res.writeHead(200, { "Content-Type": "application/json" }); 
                res.end(authorData); 
                logger.info('Cloudant read successful'); 
            } else {
                res.writeHead(204, { "Content-Type": "application/json" }); 
                res.end('{"msg": "no result"}');
                logger.info('Cloudant read successful but no results'); 
            }
        } 
    });       

};

// Display all entries -- not implemented 
exports.author_list = function(req, res, next) {  
    res.writeHead(501, { "Content-Type": "application/json" }); 
    res.end('{"error": "not implemented"}');
};