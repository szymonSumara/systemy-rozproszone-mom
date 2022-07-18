import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;

public class Team extends Thread{

    List<Item> items;
    final String name;

    public Team(String teamName,  List<Item> items){
        this.items = items;
        this.name = teamName;
    }

    @Override
    public void run() {
        System.out.println("Team");

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

            channel.queueDeclare("team." + name, false, false, false, null);
            channel.queueBind( "team." + name, EXCHANGE_ADMIN,"#.team.#");

            for (Item item : items){
                String message =  name + ";" + item ;
                System.out.println(name + ": order " + item);
                channel.basicPublish(EXCHANGE_ORDER, "order." + item, null, message.getBytes("UTF-8"));
            }

            channel.queueDeclare("delivery." + name, false, false, false, null);
            channel.queueBind( "delivery." + name, EXCHANGE_DELIVERY,"delivery." + name);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                   // System.out.println("Received Team(" + name + "): " + message);
                    String[] data = message.split(";");
                    if(data.length == 1 ){
                        System.out.println( name + ": message from adnin \"" + data[0] + "\"");
                    }else{
                        System.out.println(name + ": get " + data[2] +"(" + data[1]  + ") from " + data[0] );
                    }
                }
            };

            channel.basicConsume("delivery." + name, true, consumer);
            channel.basicConsume("team." + name, true, consumer);

        }catch (Exception e){

        }
    }
}
