#
# The MIT License
# Copyright © 2019 Stephen Dankbar
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#

QT       += qml quick widgets gui

TARGET = Jaqumal
TEMPLATE = lib
OBJECTS_DIR = objs
MOC_DIR = mocs
DESTDIR = libs
CONFIG += qmltestcase

DEFINES += JAQUMAL_LIBRARY

INCLUDEPATH += include

# TODO discover the include path
INCLUDEPATH += "C:\Program Files\Java\jdk-14.0.2\include" \
        "C:\Program Files\Java\jdk-14.0.2\include\win32"

SOURCES += \
    impl/applicationfunctions.cpp \
    impl/eventfunctions.cpp \
    impl/flattreemodelfunctions.cpp \
    impl/fontfunctions.cpp \
    impl/eventbuilder.cpp \
    impl/genericflattreemodel.cpp \
    impl/listmodelfunctions.cpp \
    impl/qmllogging.cpp \
    impl/eventdispatcher.cpp \
    impl/singletonmodelfunctions.cpp \
    impl/userinputsimulator.cpp \
    impl/qmlimageprovider.cpp \
    impl/jpolyline.cpp \
    impl/qmltest.cpp \
    impl/qmldatatransfer.cpp \
    impl/jniutilities.cpp

HEADERS += \
    include/applicationfunctions.h \
    include/eventfunctions.h \
    include/flattreemodelfunctions.h \
    include/fontfunctions.h \
    include/jniutilities.h \
    include/listmodelfunctions.h \
    include/qmldatatransfer.h \
    include/eventbuilder.h \
    include/genericflattreemodel.h \
    include/qmllogging.h \
    include/eventdispatcher.h \
    include/singletonmodelfunctions.h \
    include/userinputsimulator.h \
    include/qmlimageprovider.h \
    include/jpolyline.h \
    include/qmltest.h

DISTFILES += \
    resources/qmldir \
    resources/UtilFunc.js

RESOURCES += \
    resources/jaqumal.qrc
