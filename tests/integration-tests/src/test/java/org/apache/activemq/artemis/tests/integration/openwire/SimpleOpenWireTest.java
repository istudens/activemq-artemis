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
package org.apache.activemq.artemis.tests.integration.openwire;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SimpleOpenWireTest extends BasicOpenWireTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Override
   @Before
   public void setUp() throws Exception {
      this.realStore = true;
      super.setUp();
   }

   @Test
   public void testSimpleQueue() throws Exception {
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      System.out.println("creating queue: " + queueName);
      Destination dest = new ActiveMQQueue(queueName);

      System.out.println("creating producer...");
      MessageProducer producer = session.createProducer(dest);

      final int num = 1;
      final String msgBase = "MfromAMQ-";
      for (int i = 0; i < num; i++) {
         TextMessage msg = session.createTextMessage("MfromAMQ-" + i);
         producer.send(msg);
         System.out.println("sent: ");
      }

      //receive
      MessageConsumer consumer = session.createConsumer(dest);

      System.out.println("receiving messages...");
      for (int i = 0; i < num; i++) {
         TextMessage msg = (TextMessage) consumer.receive(5000);
         System.out.println("received: " + msg);
         String content = msg.getText();
         System.out.println("content: " + content);
         assertEquals(msgBase + i, content);
      }

      assertNull(consumer.receive(1000));

      session.close();
   }


   @Test
   public void testKeepAlive() throws Exception {
      connection.start();

      Thread.sleep(125000);

      connection.createSession(false, 1);
   }

   @Test
   public void testSimpleTopic() throws Exception {
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      System.out.println("creating queue: " + topicName);
      Destination dest = new ActiveMQTopic(topicName);

      MessageConsumer consumer1 = session.createConsumer(dest);
      MessageConsumer consumer2 = session.createConsumer(dest);

      MessageProducer producer = session.createProducer(dest);

      final int num = 1;
      final String msgBase = "MfromAMQ-";
      for (int i = 0; i < num; i++) {
         TextMessage msg = session.createTextMessage("MfromAMQ-" + i);
         producer.send(msg);
         System.out.println("Sent a message");
      }

      //receive
      System.out.println("receiving messages...");
      for (int i = 0; i < num; i++) {
         TextMessage msg = (TextMessage) consumer1.receive(5000);
         System.out.println("received: " + msg);
         String content = msg.getText();
         assertEquals(msgBase + i, content);
      }

      assertNull(consumer1.receive(500));

      System.out.println("receiving messages...");
      for (int i = 0; i < num; i++) {
         TextMessage msg = (TextMessage) consumer2.receive(5000);
         System.out.println("received: " + msg);
         String content = msg.getText();
         assertEquals(msgBase + i, content);
      }

      assertNull(consumer2.receive(500));
      session.close();
   }

   @Test
   public void testSimpleTempTopic() throws Exception {
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      System.out.println("creating temp topic");
      TemporaryTopic tempTopic = session.createTemporaryTopic();

      System.out.println("create consumer 1");
      MessageConsumer consumer1 = session.createConsumer(tempTopic);
      System.out.println("create consumer 2");
      MessageConsumer consumer2 = session.createConsumer(tempTopic);

      System.out.println("create producer");
      MessageProducer producer = session.createProducer(tempTopic);

      System.out.println("sending messages");
      final int num = 1;
      final String msgBase = "MfromAMQ-";
      for (int i = 0; i < num; i++) {
         TextMessage msg = session.createTextMessage("MfromAMQ-" + i);
         producer.send(msg);
         System.out.println("Sent a message");
      }

      //receive
      System.out.println("receiving messages...");
      for (int i = 0; i < num; i++) {
         TextMessage msg = (TextMessage) consumer1.receive(5000);
         System.out.println("received: " + msg);
         String content = msg.getText();
         assertEquals(msgBase + i, content);
      }

      assertNull(consumer1.receive(500));

      System.out.println("receiving messages...");
      for (int i = 0; i < num; i++) {
         TextMessage msg = (TextMessage) consumer2.receive(5000);
         System.out.println("received: " + msg);
         String content = msg.getText();
         assertEquals(msgBase + i, content);
      }

      assertNull(consumer2.receive(500));
      session.close();
   }

   @Test
   public void testSimpleTempQueue() throws Exception {
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      System.out.println("creating temp queue");
      TemporaryQueue tempQueue = session.createTemporaryQueue();

      System.out.println("create consumer 1");
      MessageConsumer consumer1 = session.createConsumer(tempQueue);

      System.out.println("create producer");
      MessageProducer producer = session.createProducer(tempQueue);

      System.out.println("sending messages");
      final int num = 1;
      final String msgBase = "MfromAMQ-";
      for (int i = 0; i < num; i++) {
         TextMessage msg = session.createTextMessage("MfromAMQ-" + i);
         producer.send(msg);
         System.out.println("Sent a message");
      }

      //receive
      System.out.println("receiving messages...");
      for (int i = 0; i < num; i++) {
         TextMessage msg = (TextMessage) consumer1.receive(5000);
         System.out.println("received: " + msg);
         String content = msg.getText();
         assertEquals(msgBase + i, content);
      }

      assertNull(consumer1.receive(500));
      session.close();
   }

   @Test
   public void testInvalidDestinationExceptionWhenNoQueueExistsOnCreateProducer() throws Exception {
      AddressSettings addressSetting = new AddressSettings();
      addressSetting.setAutoCreateJmsQueues(false);

      server.getAddressSettingsRepository().addMatch("jms.queue.foo", addressSetting);

      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Queue queue = session.createQueue("foo");

      thrown.expect(InvalidDestinationException.class);
      thrown.expect(JMSException.class);
      session.createProducer(queue);
      session.close();
   }

   @Test
   public void testAutoDestinationCreationOnProducerSend() throws JMSException {
      AddressSettings addressSetting = new AddressSettings();
      addressSetting.setAutoCreateJmsQueues(true);

      String address = "foo";
      server.getAddressSettingsRepository().addMatch("jms.queue." + address, addressSetting);

      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      TextMessage message = session.createTextMessage("bar");
      Queue queue = new ActiveMQQueue(address);

      MessageProducer producer = session.createProducer(null);
      producer.send(queue, message);

      MessageConsumer consumer = session.createConsumer(queue);
      TextMessage message1 = (TextMessage) consumer.receive(1000);
      assertTrue(message1.getText().equals(message.getText()));
   }

   @Test
   public void testAutoDestinationCreationOnConsumer() throws JMSException {
      AddressSettings addressSetting = new AddressSettings();
      addressSetting.setAutoCreateJmsQueues(true);

      String address = "foo";
      server.getAddressSettingsRepository().addMatch("jms.queue." + address, addressSetting);

      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      TextMessage message = session.createTextMessage("bar");
      Queue queue = new ActiveMQQueue(address);

      MessageConsumer consumer = session.createConsumer(queue);

      MessageProducer producer = session.createProducer(null);
      producer.send(queue, message);

      TextMessage message1 = (TextMessage) consumer.receive(1000);
      assertTrue(message1.getText().equals(message.getText()));
   }

   @Test
   public void testAutoDestinationNoCreationOnConsumer() throws JMSException {
      AddressSettings addressSetting = new AddressSettings();
      addressSetting.setAutoCreateJmsQueues(false);

      String address = "foo";
      server.getAddressSettingsRepository().addMatch("jms.queue." + address, addressSetting);

      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      TextMessage message = session.createTextMessage("bar");
      Queue queue = new ActiveMQQueue(address);

      try {
         MessageConsumer consumer = session.createConsumer(queue);
         fail("supposed to throw an exception here");
      }
      catch (JMSException e) {

      }
   }

   @Test
   public void testFailoverTransportReconnect() throws Exception {
      Connection exConn = null;

      try {
         String urlString = "failover:(tcp://" + OWHOST + ":" + OWPORT + ")";
         ActiveMQConnectionFactory exFact = new ActiveMQConnectionFactory(urlString);

         Queue queue = new ActiveMQQueue(durableQueueName);

         exConn = exFact.createConnection();
         exConn.start();

         Session session = exConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageProducer messageProducer = session.createProducer(queue);
         messageProducer.send(session.createTextMessage("Test"));

         MessageConsumer consumer = session.createConsumer(queue);
         assertNotNull(consumer.receive(5000));

         server.stop();
         Thread.sleep(3000);

         server.start();
         server.waitForActivation(10, TimeUnit.SECONDS);

         messageProducer.send(session.createTextMessage("Test2"));
         assertNotNull(consumer.receive(5000));
      }
      finally {
         if (exConn != null) {
            exConn.close();
         }
      }
   }

   /**
    * This is the example shipped with the distribution
    *
    * @throws Exception
    */
   @Test
   public void testOpenWireExample() throws Exception {
      Connection exConn = null;

      SimpleString durableQueue = new SimpleString("jms.queue.exampleQueue");
      this.server.createQueue(durableQueue, durableQueue, null, true, false);

      try {
         ActiveMQConnectionFactory exFact = new ActiveMQConnectionFactory();

         Queue queue = new ActiveMQQueue(durableQueueName);

         exConn = exFact.createConnection();

         exConn.start();

         Session session = exConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

         MessageProducer producer = session.createProducer(queue);

         TextMessage message = session.createTextMessage("This is a text message");

         producer.send(message);

         MessageConsumer messageConsumer = session.createConsumer(queue);

         TextMessage messageReceived = (TextMessage) messageConsumer.receive(5000);

         assertEquals("This is a text message", messageReceived.getText());
      }
      finally {
         if (exConn != null) {
            exConn.close();
         }
      }

   }


   /**
    * This is the example shipped with the distribution
    *
    * @throws Exception
    */
   @Test
   public void testMultipleConsumers() throws Exception {
      Connection exConn = null;

      SimpleString durableQueue = new SimpleString("jms.queue.exampleQueue");
      this.server.createQueue(durableQueue, durableQueue, null, true, false);

      try {
         ActiveMQConnectionFactory exFact = new ActiveMQConnectionFactory();

         Queue queue = new ActiveMQQueue(durableQueueName);

         exConn = exFact.createConnection();

         exConn.start();

         Session session = exConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

         MessageProducer producer = session.createProducer(queue);

         TextMessage message = session.createTextMessage("This is a text message");

         producer.send(message);

         MessageConsumer messageConsumer = session.createConsumer(queue);

         TextMessage messageReceived = (TextMessage) messageConsumer.receive(5000);

         assertEquals("This is a text message", messageReceived.getText());
      }
      finally {
         if (exConn != null) {
            exConn.close();
         }
      }

   }

   @Test
   public void testMixedOpenWireExample() throws Exception {
      Connection openConn = null;

      SimpleString durableQueue = new SimpleString("jms.queue.exampleQueue");
      this.server.createQueue(durableQueue, durableQueue, null, true, false);

      ActiveMQConnectionFactory openCF = new ActiveMQConnectionFactory();

      Queue queue = new ActiveMQQueue("exampleQueue");

      openConn = openCF.createConnection();

      openConn.start();

      Session openSession = openConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

      MessageProducer producer = openSession.createProducer(queue);

      TextMessage message = openSession.createTextMessage("This is a text message");

      producer.send(message);

      org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory artemisCF = new org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory();

      Connection artemisConn = artemisCF.createConnection();
      Session artemisSession = artemisConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
      artemisConn.start();
      MessageConsumer messageConsumer = artemisSession.createConsumer(artemisSession.createQueue("exampleQueue"));

      TextMessage messageReceived = (TextMessage) messageConsumer.receive(5000);

      assertEquals("This is a text message", messageReceived.getText());

      openConn.close();
      artemisConn.close();

   }


   // simple test sending openwire, consuming core
   @Test
   public void testMixedOpenWireExample2() throws Exception {
      Connection conn1 = null;

      SimpleString durableQueue = new SimpleString("jms.queue.exampleQueue");
      this.server.createQueue(durableQueue, durableQueue, null, true, false);

      Queue queue = ActiveMQJMSClient.createQueue("exampleQueue");

      org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory artemisCF = new org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory();

      conn1 = artemisCF.createConnection();

      conn1.start();

      Session session1 = conn1.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageProducer producer = session1.createProducer(queue);
      for (int i = 0; i < 10; i++) {
         TextMessage message = session1.createTextMessage("This is a text message");
         producer.send(message);
      }

      ActiveMQConnectionFactory openCF = new ActiveMQConnectionFactory();

      Connection conn2 = openCF.createConnection();
      Session sess2 = conn2.createSession(false, Session.AUTO_ACKNOWLEDGE);
      conn2.start();
      MessageConsumer messageConsumer = sess2.createConsumer(sess2.createQueue("exampleQueue"));

      for (int i = 0; i < 10; i++) {
         TextMessage messageReceived = (TextMessage) messageConsumer.receive(5000);
         assertEquals("This is a text message", messageReceived.getText());
      }

      conn1.close();
      conn2.close();
   }


}
