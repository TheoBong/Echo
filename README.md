# Echo Spigot Plugin
Echo is a screenshare tool for catching cheaters in minecraft. This plugin allows you to more efficently run scans through echo directly through the server.

To download the plugin, visit: https://github.com/Cowings/Echo/releases
You must have an API Key to use this. Get an Echo API Key by purchasing an Echo License here: https://echo.ac/#pricing

Features:
* **Staff API Keys** - each staff member must use their own API key, this data is stored in a json file.
* **Automatic Screensharing** - run a simple /freeze command and it will automatically generate a download link and keep you updated on the status of the scan.
* **Freezing** - completely freezes a player and runs a variety of tasks that any regular freeze plugin would have.
* **Configurable** - almost every message and aspect of the plugin is configurable via a config.yml
* **Free** - available for download and usage at no cost, and permissively licensed so it can remain free forever.

## Frequently Asked Questions
* **Why do I have to specify a pin in freeze command?** - this is because you have a personal license, enterprise users can auto-generate a pin.
* **Where do I get my API key?** - https://github.com/Cowings/Echo/wiki#setup-process-each-moderator-must-do-this
* **I found a bug, what do I do?** - open a new issue at https://github.com/Cowings/Echo/issues

## Building
Echo Plugin uses Maven to handle dependencies & building.

#### Requirements
* Java 8 JDK or newer
* Git
* Spigot Build Tools installed (we're using 1.8.8)

#### Compiling from source
```sh
git clone https://github.com/Cowings/Echo
cd Echo/
mvn clean install
```

## Contributing
#### Pull Requests
If you make any changes or improvements to the plugin which you think would be beneficial to others, please consider making a pull request to merge your changes back into the upstream project. (especially if your changes are bug fixes!)

Echo Plugin loosely follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Generally, try to copy the style of code found in the class you're editing. 

Please make sure to have bug fixes/improvements in seperate pull requests from new features/changing how features work.

#### TODO
More spigot jar support
More testing
Make all API Requests Async

## License
Echo Plugin is licensed under the permissive MIT license. Please see [`LICENSE.txt`](https://github.com/Cowings/Echo/blob/master/LICENSE.txt) for more info.
