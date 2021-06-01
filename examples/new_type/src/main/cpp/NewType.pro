#
# The MIT License
# Copyright © 2020 Stephen Dankbar
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

TARGET = NewType
TEMPLATE = lib
OBJECTS_DIR = objs
MOC_DIR = mocs
DESTDIR = libs

INCLUDEPATH += include

win32 {
    INCLUDEPATH += "$$getenv(JAVA_HOME)\include" \
                   "$$getenv(JAVA_HOME)\include\win32"

    # TODO figure out a better way to access the .a on Windows
    LIBS += "../../../../../library/src/main/cpp/libs/libJaqumal.a"
} else {
    INCLUDEPATH += "$$getenv(JAVA_HOME)/include" \
                   "$$getenv(JAVA_HOME)/include/unix" \
                   "$$getenv(JAVA_HOME)/include/linux"
}

SOURCES += \
    impl/registerNewType.cpp

HEADERS += \
    include/registerNewType.h
