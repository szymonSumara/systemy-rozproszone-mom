import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Supplier extends Thread{

    List<Item> items;
    String name;
    Integer deliveryCounter = 0;


    Supplier(String name, List<Item> items){
        this.items = items;
        this.name = name;
    }

    @Override
    public void run(){
        try {
            System.out.println("Dostawca start");
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

            channel.queueDeclare("supplier." + name, false, false, false, null);
            channel.queueBind( "supplier." + name, EXCHANGE_ADMIN,"#.supplier.#");

            List<String> queues = new LinkedList<>();

            for (Item item : this.items) {
                channel.queueDeclare("order." + item, false, false, false, null);
                channel.queueBind( "order." + item, EXCHANGE_ORDER,"order." + item);
                queues.add("order." + item);
            }

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                synchronized public void  handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    String[] data = message.split(";");
                    if(data.length == 1){
                        System.out.println( name + ": message from admin \"" + data[0] + "\"");
                    }else{
                        message = name + ";" + deliveryCounter + ";" + data[1];
                        System.out.println(name + ": Send " + data[1] +" to " + data[0]);
                        channel.basicPublish(EXCHANGE_DELIVERY,"delivery." + data[0], null, message.getBytes("UTF-8"));
                        deliveryCounter+=1;
                    }
                }
            };

            for(String queueName : queues){
                channel.basicConsume(queueName, true, consumer);
            }

            channel.basicConsume("supplier." + name, true, consumer);


        } catch (Exception exception) {

        }
    }

}
