const keys = require("@/constant/key");

const API_BASE_URL = keys.apiBaseUri;

const OAUTH2_REDIRECT_URI = keys.redirectUri;

const GOOGLE_AUTH_URL = API_BASE_URL + "/oauth2/authorize/google?redirect_uri=" + OAUTH2_REDIRECT_URI;

module.exports = {
    GOOGLE_AUTH_URL,
    API_BASE_URL
}
