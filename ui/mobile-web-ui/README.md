Mobile phone Temperature Controller
===================================

This UI is designed to control the heating. It is using the [Temperature Sensor and REST project][2] API.

UI is a single page JavaScript webapp.

## Technologies used to build it


* [ReactJS][10] - the very awesome UI Framework from Facebook
* [Redux][11] - state container for JavaScript apps
* [MaterialUI][12] - reusable React components build according to Material design
* [Babel, ES6][13] - new version of JavaScript
* [Ramda][14] - for some awesome functional programming
* [Webpack][15] - for building the lot

!["Mobile phone home screen"][1]


## How to run this?

First, grab some dependencies:

    npm install
    
Than run:

    npm start
    
You'll need to have a backend running.


**By default, the UI assumes that the REST server providing Temperature sensor reading is deployed on the same host and port 8080**

To change that, edit the lines in **Home.js** and **scheduleActions.js**.

And than rebuild!

## Building distribution

Make sure you for the dependencies firs and than run :

    npm run buildpack
    

It will build **site.tgz** in the root folder.


[1]: ../screens/HomeScreen.png
[2]: ../../temperature-sensor-and-rest/README.md


[10]: https://facebook.github.io/react/
[11]: https://github.com/reactjs/redux
[12]: http://www.material-ui.com/
[13]: https://babeljs.io/docs/learn-es2015/
[14]: http://ramdajs.com/0.19.1/index.html
[15]: https://webpack.github.io/