import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Admin extends Thread{


    @Override
    public void run(){
        System.out.println("Admin start");
        try {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            // exchange
            String EXCHANGE_ORDER = "order";
            String EXCHANGE_DELIVERY = "delivery";
            String EXCHANGE_ADMIN = "admin";

            channel.exchangeDeclare(EXCHANGE_ORDER, BuiltinExchangeType.TOPIC);
            channel.exchangeDeclare(EXCHANGE_DELIVERY, BuiltinExchangeType.TOPIC);
            channel.exchangeDeclare(EXCHANGE_ADMIN, BuiltinExchangeType.TOPIC);

            channel.queueDeclare("order.admin" , false, false, false, null);
            channel.queueBind( "order.admin" , EXCHANGE_ORDER,"order.#");

            channel.queueDeclare("delivery.admin" , false, false, false, null);
            channel.queueBind( "delivery.admin", EXCHANGE_DELIVERY,"delivery.#");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Admin" + ": get messsage \"" + message + "\"");
                }
            };

            channel.basicConsume("order.admin", true, consumer);
            channel.basicConsume("delivery.admin", true, consumer);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));

            // Reading data using readLine
            String recipients = "team.supplier";

            while (true) {
                String input = reader.readLine();
                System.out.println("get input:" + input);

                if(input.equals("A") || input.equals("T") || input.equals("S") ){
                    if(input.equals("A"))
                        recipients = "team.supplier";
                    if(input.equals("T"))
                        recipients = "team";
                    if(input.equals("S"))
                        recipients = "supplier";
                    System.out.println("Admin : now will send to " + recipients );
                    continue;
                }

                channel.basicPublish(EXCHANGE_ADMIN, recipients , null,  input.getBytes("UTF-8"));
            }

        } catch (Exception exception) {

        }
    }
}
