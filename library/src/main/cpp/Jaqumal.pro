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

QT       += qml quick widgets

QT       += gui

TARGET = Jaqumal
TEMPLATE = lib
OBJECTS_DIR = objs
MOC_DIR = mocs
DESTDIR = libs
CONFIG += debug

DEFINES += JAQUMAL_LIBRARY

INCLUDEPATH += include

SOURCES += \
    impl/qmllibobject.cpp \
    impl/genericlistmodel.cpp \
    impl/eventbuilder.cpp \
    impl/genericobjectmodel.cpp \
    impl/genericflattreemodel.cpp \
    impl/qmllogging.cpp \
    impl/qmlinterface.cpp \
    impl/eventdispatcher.cpp \
    impl/userinputsimulator.cpp

HEADERS += \
    include/qmllibobject.h \
    include/genericlistmodel.h \
    include/eventbuilder.h \
    include/genericobjectmodel.h \
    include/genericflattreemodel.h \
    include/qmllogging.h \
    include/qmlinterface.h \
    include/eventdispatcher.h \
    include/userinputsimulator.h

DISTFILES += \
    resources/qmldir \
    resources/UtilFunc.js

RESOURCES += \
    resources/jaqumal.qrc

QMAKE_CXXFLAGS *= -Og
