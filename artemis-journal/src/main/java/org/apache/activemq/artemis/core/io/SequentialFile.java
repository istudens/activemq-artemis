/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.core.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.core.journal.EncodingSupport;
import org.apache.activemq.artemis.core.io.buffer.TimedBuffer;

public interface SequentialFile {

   boolean isOpen();

   boolean exists();

   void open() throws Exception;

   /**
    * The maximum number of simultaneous writes accepted
    *
    * @param maxIO
    * @throws Exception
    */
   void open(int maxIO, boolean useExecutor) throws Exception;

   boolean fits(int size);

   int getAlignment() throws Exception;

   int calculateBlockStart(int position) throws Exception;

   String getFileName();

   void fill(int size) throws Exception;

   void delete() throws IOException, InterruptedException, ActiveMQException;

   void write(ActiveMQBuffer bytes, boolean sync, IOCallback callback) throws Exception;

   void write(ActiveMQBuffer bytes, boolean sync) throws Exception;

   void write(EncodingSupport bytes, boolean sync, IOCallback callback) throws Exception;

   void write(EncodingSupport bytes, boolean sync) throws Exception;

   /**
    * Write directly to the file without using any buffer
    *
    * @param bytes the ByteBuffer must be compatible with the SequentialFile implementation (AIO or
    *              NIO). To be safe, use a buffer from the corresponding
    *              {@link SequentialFileFactory#newBuffer(int)}.
    */
   void writeDirect(ByteBuffer bytes, boolean sync, IOCallback callback);

   /**
    * Write directly to the file without using intermediate any buffer
    *
    * @param bytes the ByteBuffer must be compatible with the SequentialFile implementation (AIO or
    *              NIO). To be safe, use a buffer from the corresponding
    *              {@link SequentialFileFactory#newBuffer(int)}.
    */
   void writeDirect(ByteBuffer bytes, boolean sync) throws Exception;

   /**
    * @param bytes the ByteBuffer must be compatible with the SequentialFile implementation (AIO or
    *              NIO). To be safe, use a buffer from the corresponding
    *              {@link SequentialFileFactory#newBuffer(int)}.
    */
   int read(ByteBuffer bytes, IOCallback callback) throws Exception;

   /**
    * @param bytes the ByteBuffer must be compatible with the SequentialFile implementation (AIO or
    *              NIO). To be safe, use a buffer from the corresponding
    *              {@link SequentialFileFactory#newBuffer(int)}.
    */
   int read(ByteBuffer bytes) throws Exception;

   void position(long pos) throws IOException;

   long position();

   void close() throws Exception;

   void sync() throws IOException;

   long size() throws Exception;

   void renameTo(String newFileName) throws Exception;

   SequentialFile cloneFile();

   void copyTo(SequentialFile newFileName) throws Exception;

   void setTimedBuffer(TimedBuffer buffer);

   /**
    * Returns a native File of the file underlying this sequential file.
    */
   File getJavaFile();
}
