# Jaqumal

Jaqumal (Java + QML) is a library that provides Java bindings for Qt, allowing Java applications to use QML to make GUIs. 
It does this by providing generic model types that are then available to QML scripts, see [QAbstractItemModel](http://doc.qt.io/qt-5/qabstractitemmodel.html).  Events and callbacks are used for passing data back from QML to the Java layer.

# Dependencies

##### C++ Dependencies

* g++ 4.8.5 or newer
* Qt 5.11 or newer

##### Java Dependencies

Java 8 or newer and uses Maven to build

See pom.xml for details of the Java libraries used

* Apache Commons-IO
* Apache Commons-Lang
* Apache Commons-Math
* Guava
* JNA
* JUnit4
* Log4j2

# License

MIT

# Adding Jaqumal to your build - Linux

Jaqumal contains a C++ library so must it be built before it can be used.  Assuming a clean Cent OS 7 install, the
following must be installed/configured before building.

* sudo yum install java-11-openjdk-devel (Java 8, 9, or 10 can be installed instead)
* sudo yum install maven
* sudo alternatives --config java
     * Select java 8 or newer
* sudo alternatives --config javac
     * Select java 8 or newer
* sudo yum install gcc-c++
* sudo yum install mesa-libGL-devel
* Download Qt 5.11 or newer from https://www.qt.io/download and install it
* Add the path to the Qt binaries, such as qmake, to the PATH variable
     * May need to add the path to the Qt libraries to the LD_LIBRARY_PATH variable 

Change directories to the root of the Jaqumal git repository and run the following commands.

- mvn -DskipTests install
- mvn install

The first attempt to build must skip tests so that the C++ library, which is used in the unit tests, is available.

Then add the following to the pom.xml to be able to use Jaqumal.


```
<dependency>
	<groupId>com.github.sdankbar.jaqumal</groupId>
	<artifactId>library</artifactId>
	<version>${current.version}</version>
</dependency>
```
 
# Adding Jaqumal to your build - Windows

Jaqumal contains a C++ library so must it be built before it can be used.  The
following must be installed/configured before building.

* Install JDK 8 or newer
* Install maven
* Download Qt 5.11 or newer from https://www.qt.io/download and install the MinGW 64 bit version.  Also install MinGW 64 compiler as well.
* Add the path to the Qt binaries and libaries, such as qmake and Qt's dlls, to the Path variable
* Add the path to the MinGW64 bit binaries, such as g++, to the Path variable.

Change directories to the root of the Jaqumal git repository and run the following commands.

- mvn -DskipTests install
- mvn install

The first attempt to build must skip tests so that the C++ library, which is used in the unit tests, is available.

Then add the following to the pom.xml to be able to use Jaqumal.


```
<dependency>
	<groupId>com.github.sdankbar.jaqumal</groupId>
	<artifactId>library</artifactId>
	<version>${current.version}</version>
</dependency>
```
 

# Quick Start

A basic Java application that uses Jaqumal starts by creating a JQMLApplication instance.  This corresponds to the QApplication in the
Qt library.  Using the ModelFactory from the JQMLApplication, a model can be created to pass data to any QML scripts.  Models should be initialized with any starting data at this point.  Then any QML files should be loaded.  Once all initialization is done, execute()
must be called, which enters into the Qt Event Loop.  This function will only return once the event loop exits, i.e. the application has quit.

During development, is it also possible for the library to listen for changes to the loaded QML, causing the QML file to be reloaded automatically when it changes.  This allows for quick development of the QML since no re-compilation is necessary.

##### Models

There are 3 basic types of models available: singleton, list, and flat tree.  The singleton model provides a QAbstractItemModel with a single
item in it.  Since multiple roles can be defined, this can be thought of a map from roles to JVariant.  JVariant is the Java version of QVariant.  The list model provides a QAbstractItemModel with [0, n] items in it.  So it a list of maps from roles to JVariant.  The last model, flat tree, represents a tree structure.  The representation of the tree is a list of maps from roles to JVariant and each item in the list can have a child list which is also a list of maps from roles to JVariant.  Access to items in the tree is done via a TreePath which is a list of indices into each level of lists.  The reason it is referred to as a flat tree is that while the Java treats the model as a tree structure, the tree is flattened into a list when presented to the QML side.  This makes it easier to use inside of Repeaters and ListViews.

Role types must have toString() methods defined that return Strings that are valid QML identifiers since the String is used as the name of the role in the QAbstractItemModel.

##### Events

Events and callbacks are how Java receives data from QML.  The EventBuilder QML type is used to send events from QML to Java.  JQMLApplication requires an EventFactory to be passed in when it is created.  This EventFactory is called whenever the EventBuilder is used to send an Event.  The EventFactory implementation will need to take the data passed to it via the EventParser and create a specific Event instance.  To have Java code be called when the Event is created, use the EventDispatcher to register a callback.  Event listeners can be given priorities to determine their call order.  Events can also be consumed so that lower priority Event listeners won't receive the Event.

There are also built-in Event types provided by this library.  Code can register to receive a callback when those types of events are created using the EventDispatcher as well.

NOTE: Care should be taken when modifying a model from inside a model changed callback.  This may lead to undefined behavior.

##### Threading

The Jaqumal library is not thread safe and most functions should only be called from the Qt Thread.  The Qt Thread is the Java thread that has called QApplication.execute().  If a function that must be called from the Qt Thread is called from another thread, a QMLThreadingException will be thrown.  JQMLApplication provides a ScheduledExecutorService that allows Java code to be run asychronously on the Qt Thread.

# Examples

##### StopLight

Provides an example of a basic application that uses a singleton model and the JQMLApplication's ScheduledExecutorService to update the model after a delay.  Also provides examples of registering for callbacks. 

##### Color Editor

Provides an example of defining new Event types and sending and receiving those event to update a list model.

# Future Work

* Create additional specialized models for each QtQuick type.
* Improve support for setting values in a model on the QML side and listening for changes in Java.
* Improve performance of the list and flattree models.
* Expand Event functionality to allow Java to return a value to QML.
* Write additional unit tests.
