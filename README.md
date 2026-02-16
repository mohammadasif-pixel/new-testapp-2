# Cordova Plugin - Installed Apps

A Cordova plugin to get the list of all installed applications with their icons for both **Android** and **iOS**.

## Features

- ✅ Get list of all installed apps
- ✅ Retrieve app icons as base64 encoded images
- ✅ Get app metadata (name, package/bundle ID, version, etc.)
- ✅ Works on both Android and iOS
- ✅ Asynchronous operations for better performance

## Installation

### Install from local folder
```bash
cordova plugin add /path/to/cordova-plugin-installed-apps
```

### Install from git (after publishing)
```bash
cordova plugin add cordova-plugin-installed-apps
```

## Platform Support

- **Android**: ✅ Full support - Gets all installed apps
- **iOS**: ⚠️ Limited support - Due to iOS privacy restrictions, can only detect apps with registered URL schemes

## Usage

### Get All Installed Apps

```javascript
cordova.plugins.InstalledApps.getInstalledApps(
    function(apps) {
        console.log('Installed apps:', apps);
        // apps is an array of app objects
        apps.forEach(function(app) {
            console.log('App Name:', app.appName);
            console.log('Package/Bundle ID:', app.packageName || app.bundleId);
            console.log('Version:', app.versionName);
            console.log('Icon (base64):', app.icon);
            console.log('Is System App:', app.isSystemApp);
        });
    },
    function(error) {
        console.error('Error:', error);
    }
);
```

### Get Specific App Info

```javascript
// Android - use package name
var packageName = 'com.example.app';

// iOS - use bundle ID
var bundleId = 'com.example.app';

cordova.plugins.InstalledApps.getAppInfo(
    packageName, // or bundleId for iOS
    function(appInfo) {
        console.log('App Info:', appInfo);
    },
    function(error) {
        console.error('Error:', error);
    }
);
```

## Response Format

### Android Response

```json
[
    {
        "packageName": "com.example.app",
        "appName": "Example App",
        "versionName": "1.0.0",
        "versionCode": 1,
        "isSystemApp": false,
        "firstInstallTime": 1234567890000,
        "lastUpdateTime": 1234567890000,
        "icon": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
    }
]
```

### iOS Response

```json
[
    {
        "urlScheme": "fb://",
        "appName": "Facebook",
        "bundleId": "",
        "icon": "",
        "isSystemApp": false
    },
    {
        "bundleId": "com.yourapp.id",
        "appName": "Your App",
        "versionName": "1.0.0",
        "versionCode": "1",
        "isSystemApp": false,
        "icon": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
    }
]
```

## Android Permissions

The plugin automatically adds the required permission to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"/>
```

**Note**: For Android 11+ (API level 30+), you need to declare package visibility in your `AndroidManifest.xml`:

```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
    </intent>
</queries>
```

## iOS Limitations

Due to iOS privacy and security restrictions:

1. **Cannot get full list of installed apps** - iOS doesn't provide an API to enumerate all installed apps
2. **URL Scheme Detection** - The plugin can only detect apps that have registered URL schemes
3. **Current App Only** - Can get full details only for the current app
4. **No Icon Access** - Cannot retrieve icons for other apps

This is a platform limitation, not a plugin limitation.

## Example Integration

### HTML
```html
<!DOCTYPE html>
<html>
<head>
    <title>Installed Apps</title>
    <script src="cordova.js"></script>
</head>
<body>
    <h1>Installed Apps</h1>
    <button onclick="getApps()">Get Installed Apps</button>
    <div id="appList"></div>
    
    <script>
        function getApps() {
            cordova.plugins.InstalledApps.getInstalledApps(
                function(apps) {
                    var html = '<ul>';
                    apps.forEach(function(app) {
                        html += '<li>';
                        if (app.icon) {
                            html += '<img src="' + app.icon + '" width="48" height="48" /> ';
                        }
                        html += '<strong>' + app.appName + '</strong><br>';
                        html += 'Package: ' + (app.packageName || app.bundleId || 'N/A') + '<br>';
                        html += 'Version: ' + (app.versionName || 'N/A');
                        html += '</li>';
                    });
                    html += '</ul>';
                    document.getElementById('appList').innerHTML = html;
                },
                function(error) {
                    alert('Error: ' + error);
                }
            );
        }
    </script>
</body>
</html>
```

### React/Vue/Angular

```javascript
// After device ready
document.addEventListener('deviceready', function() {
    if (window.cordova && window.cordova.plugins.InstalledApps) {
        window.cordova.plugins.InstalledApps.getInstalledApps(
            (apps) => {
                console.log('Apps:', apps);
                // Update your state/data
            },
            (error) => {
                console.error('Error:', error);
            }
        );
    }
}, false);
```

## Performance Considerations

- **Android**: Getting all apps can take a few seconds on devices with many apps installed
- **Icons**: Base64 encoding icons increases response size
- **Background Thread**: All operations run on background threads to avoid blocking UI

## Troubleshooting

### Android

1. **Empty list returned**: Check if `QUERY_ALL_PACKAGES` permission is granted
2. **Android 11+ issues**: Add `<queries>` tag to AndroidManifest.xml
3. **Build errors**: Ensure you're using Cordova Android 9.0.0 or higher

### iOS

1. **Limited apps detected**: This is expected due to iOS restrictions
2. **No icons**: iOS doesn't allow accessing other apps' icons
3. **Only URL schemes work**: This is an iOS platform limitation

## License

MIT

## Contributing

Pull requests are welcome! Please ensure your code follows the existing style.

## Support

For issues and feature requests, please use the GitHub issue tracker.
