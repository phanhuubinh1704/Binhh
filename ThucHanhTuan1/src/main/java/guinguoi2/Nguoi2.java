package guinguoi2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

import redis.clients.jedis.Jedis;

import javax.swing.JTextField;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.BasicConfigurator;
import javax.swing.JTextArea;


public class Nguoi2 extends JFrame {

	private JPanel contentPane;
	private JTextField textField_1;
	private JTextField messNg2;
	private JFrame frame;
	private Component send;

	/**
	 * Launch the application.
	 */
	private static final String Chat = "ChatNg2";
	public static void main(String[] args) {
		
		

        
        final JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true); 
        
        final JTextField messageField = new JTextField();
        
        final Jedis jedis = new Jedis("redis://default:tAoljg4N4LvV7gxFTiEWsR053wKw1epJ@redis-10751.c299.asia-northeast1-1.gce.cloud.redislabs.com:10751");
        
        JButton send = new JButton("Lưu vào Redis");
        
        String chatMessages = jedis.get(Chat);
        if (chatMessages != null) {
            chatArea.setText(chatMessages);
        }
        
   

        send.addActionListener(new ActionListener() {
            
			@Override
            public void actionPerformed(ActionEvent e) {
                // Lấy tin nhắn từ TextField
                String message = messageField.getText();

                // Kiểm tra xem tin nhắn có giá trị hay không
                if (!message.isEmpty()) {
                    // Lấy thời gian hiện tại
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String timestamp = now.format(formatter);

                    // Tạo định dạng tin nhắn: [Thời gian] Tin nhắn
                    String formattedMessage = "[" + timestamp + "] " + message + "\n";

                    // Hiển thị tin nhắn lên TextArea
                    chatArea.append(formattedMessage);

                    // Lưu tin nhắn xuống Redis
                    jedis.append(Chat, formattedMessage);

                    // Xóa nội dung TextField
                    messageField.setText("");
                } 
            }
        });
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Nguoi2 frame = new Nguoi2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Nguoi2() throws Exception{
		
		setTitle("Ung dung chat 2");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 562, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 10, 527, 313);
		contentPane.add(textArea);
		
		messNg2 = new JTextField();
		messNg2.setBounds(10, 333, 432, 32);
		contentPane.add(messNg2);
		messNg2.setColumns(15);
		
		//thiết lập môi trường cho JMS
				BasicConfigurator.configure();
				//thiết lập môi trường cho JJNDI
				Properties settings=new Properties();
				settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
						"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
				settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
				//tạo context
				Context ctx=new InitialContext(settings);
				//lookup JMS connection factory
				Object obj=ctx.lookup("TopicConnectionFactory");
				ConnectionFactory factory=(ConnectionFactory)obj;
				//lookup destination
				Destination destination
				=(Destination) ctx.lookup("dynamicTopics/sangnguyen");
				final Destination destination1
				=(Destination) ctx.lookup("dynamicTopics/dauthi");
				//tạo connection
				Connection con=factory.createConnection("admin","admin");
				//nối đến MOM
				con.start();
				//tạo session
				final Session session=con.createSession(
						/*transaction*/false,
						/*ACK*/Session.CLIENT_ACKNOWLEDGE
						);
				//tạo consumer
				MessageConsumer receiver = session.createConsumer(destination);
				//blocked-method for receiving message - sync
				//receiver.receive();
				//Cho receiver lắng nghe trên queue, chừng có message thì notify - async
				receiver.setMessageListener(new MessageListener() {			
					public void onMessage(Message msg) {
						try {
							if (msg instanceof TextMessage) {
								TextMessage tm = (TextMessage) msg;
								String txt = tm.getText();						
								textArea.append(txt+"\n");
								msg.acknowledge();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				frame.getContentPane().setLayout(new BorderLayout());
				frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
				frame.getContentPane().add(messNg2, BorderLayout.SOUTH);
				frame.getContentPane().add(send, BorderLayout.EAST);
				
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					MessageProducer producer = session.createProducer(destination1);
					Message msg=session.createTextMessage(messNg2.getText());
					producer.send(msg);
				} catch (JMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(452, 327, 85, 32);
		contentPane.add(btnNewButton);
		
		
	}
}
