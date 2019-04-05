require('dotenv').config();
const { Issuer } = require('openid-client');
const express = require('express');
const session = require('express-session');

const app = express();
const port = 3000;

app.use(session({
  secret: '123456',
  resave: true,
  saveUninitialized: true
}));

const issuer = new Issuer({
  issuer: process.env.ISSUER,
  authorization_endpoint: process.env.AUTHORIZATION_ENDPOINT,
  token_endpoint: process.env.TOKEN_ENDPOINT,
  userinfo_endpoint: process.env.USERINFO_ENDPOINT,
  jwks_uri: process.env.JWKS_URI,
});
console.log('Issuer %s %O', issuer.issuer, issuer.metadata);

const client = new issuer.Client({
  client_id: process.env.CLIENT_ID,
  client_secret: process.env.CLIENT_SECRET
});

let authorizationUrl = client.authorizationUrl({
  redirect_uri: process.env.REDIRECT_URL_CALLBACK,
  scope: process.env.SCOPE
});
console.log(authorizationUrl)

app.get('/callback', (req, res) => {
  client.authorizationCallback(process.env.REDIRECT_URL_CALLBACK,
    req.query, { 'response_type': 'code' })
    .then(function (tokenSet) {

      client.userinfo(tokenSet.access_token)
        .then(function (userinfo) {
          res.redirect(process.env.REDIRECT_URL_WEB_APP +
            '?name=' + userinfo.name +
            '&email=' + userinfo.email +
            '&id_token=' + tokenSet.id_token
          );
        })
        .catch(function (error) {
          console.log(error);
          res.redirect(process.env.REDIRECT_URL_WEB_APP);
        })
    })
    .catch(function (error) {
      console.log(error);
      res.redirect(process.env.REDIRECT_URL_WEB_APP);
    })
})

app.get('/loginurl', (req, res) => {
  res.json({ loginUrl: authorizationUrl });
})

app.get('/login', (req, res) => {
  res.redirect(authorizationUrl);
})

app.get('/health', (req, res) => {
  res.send('up');
})
app.get('/', (req, res) => {
  res.send('up');
})

app.listen(3000, () => console.log('Listening on port 3000'))