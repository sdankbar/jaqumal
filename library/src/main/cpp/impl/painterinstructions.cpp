/**
 * The MIT License
 * Copyright © 2020 Stephen Dankbar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
#include "painterinstructions.h"
#include <QPainter>

PainterInstructions::PainterInstructions() :
    m_length(0),
    m_instructions()
{
  // Empty Implementation
}

PainterInstructions::PainterInstructions(unsigned int length, unsigned char* instructions) :
    m_length(length),
    m_instructions(instructions)
{
    // Empty Implementation
}

void PainterInstructions::paint(QPainter& p)
{
    // TODO
}

jbyteArray PainterInstructions::cloneIntoJavaArray(JNIEnv* env) const
{
    jbyteArray javaObject = env->NewByteArray(m_length);
    jbyte* mem = env->GetByteArrayElements(javaObject, nullptr);
    memcpy(mem, m_instructions.get(), m_length);
    env->ReleaseByteArrayElements(javaObject, mem, 0);// Commit and release
    return javaObject;
}
