var exec = require('cordova/exec');

var InstalledApps = {
    /**
     * Get list of all installed apps
     * @param {Function} successCallback - Success callback with apps array
     * @param {Function} errorCallback - Error callback
     */
    getInstalledApps: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'InstalledApps', 'getInstalledApps', []);
    },

    /**
     * Get app info by package name (Android) or bundle ID (iOS)
     * @param {String} packageName - Package name or bundle ID
     * @param {Function} successCallback - Success callback with app info
     * @param {Function} errorCallback - Error callback
     */
    getAppInfo: function (packageName, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'InstalledApps', 'getAppInfo', [packageName]);
    }
};

module.exports = InstalledApps;
