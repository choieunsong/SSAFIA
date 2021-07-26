if (process.env.NODE_ENV == "development") {
    const config = require("@/constant/dev");
    module.exports = config;
    console.log("development", config.apiBaseUri);
} else {
    module.exports = require("@/constant/prod");
}