Rem
Rem The MIT License
Rem Copyright © 2019 Stephen Dankbar
Rem
Rem Permission is hereby granted, free of charge, to any person obtaining a copy
Rem of this software and associated documentation files (the "Software"), to deal
Rem in the Software without restriction, including without limitation the rights
Rem to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
Rem copies of the Software, and to permit persons to whom the Software is
Rem furnished to do so, subject to the following conditions:
Rem
Rem The above copyright notice and this permission notice shall be included in
Rem all copies or substantial portions of the Software.
Rem
Rem THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
Rem IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
Rem FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
Rem AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
Rem LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
Rem OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
Rem THE SOFTWARE.
Rem

qmake NewType.pro -spec win32-g++
mingw32-make clean
del Makefile
