if (process.env.NODE_ENV == "development") {
    const config = require("@/constant/dev");
    module.exports = config;
} else {
    module.exports = require("@/constant/prod");
}