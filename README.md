<p align="center">
  <img src="http://www.shiftedit.org/img/logo128.png"/>
</p>

# Welcome to Shift!

Shift is a code editor for the modern web.

If you are interrested in using it and want to download binaries, then go to [http://www.shiftedit.com](http://www.shiftedit.com).

If your are interrested in building it from sources or contributing to the code, then you are at the right place!

## Why is Shift different?

The idea behind Shift is to create an hybrid tool between a code editor and an IDE to fit new web developers needs, such as tools to deal with mobile web and Responsive Web Design.

Building the most complete code editor with tons of shortcuts is not the goal here.

Here are the key features of the application (at the moment):

* **Live development on multiple devices**: see your webpage change as you modify your code: in a preview window or remotely on any HTML5 capable browser. No need to strike your refresh key at any change to see what's going on.
* **Bring your ideas to life more quickly**: generate your Bootstrap or HTML5 Boilerplate project in one click (others will come soon), no need to setup a webserver to test your pages.
* **Keep an eye on performances**: check rendering times on real devices while writing your pages and make sure your users will get the best experience.

<p align="center">
  <img src="http://www.shiftedit.org/img/slide2.png" width="70%"/>
</p>


## Building from sources

### Running the application

In order to run the application use:

```
mvn jfx:run
```

### Packaging the application

Packaging means : building the application and a native installer for it. The format depends on the operating system and additional tools may be installed prior to creating the package. 

Read [Building a Native Installer](http://zenjava.com/javafx/maven/native-bundle.html) for more information.

The command for packaging is:

```
mvn clean jfx:native
```

#### Linux

In order to build native packages as *.deb* and *.rpm* from Ubuntu / Debian, make sure all the required tools are installed.

To install required packages use:

```
sudo apt-get install build-essential
sudo apt-get install rpm
```

## Contributing

Shift is at an early stage, and there is still a lot of work to do on it, so any help is appreciated.

### How?

You can contribute on anything you like:

* Documentation or code: send me pull requests.
* New features and suggestions: use the project [issue tracker](https://github.com/zippy1978/shift/issues).

### Get ready to code!

In order to get familiar about the concepts and the code structure of the project, read the [developer guide](https://github.com/zippy1978/shift/blob/master/doc/developer/developer_guide.md) (not complete yet).



