package guinguoi1;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.BasicConfigurator;

import redis.clients.jedis.Jedis;

import javax.swing.JTextArea;

public class Nguoi1 extends JFrame {

	private JPanel contentPane;
	private JTextField textField_1;
	private JTextArea textArea;
	private JTextField txtNg1;
	private JFrame frame;
	private JButton send;
	private TextArea chatArea;

	/**
	 * Launch the application.
	 */
	
	private static final String Chat = "ChatNg1";
	public static void main(String[] args) {
		
		

        
        final JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setBounds(10, 10, 527, 307);
		chatArea.setEditable(false);
        
        final JTextField txtNg1 = new JTextField();
        txtNg1.setBounds(10, 333, 432, 32);
		txtNg1.setColumns(10);
        
        final Jedis jedis = new Jedis("redis://default:tAoljg4N4LvV7gxFTiEWsR053wKw1epJ@redis-10751.c299.asia-northeast1-1.gce.cloud.redislabs.com:10751");
        
        JButton send = new JButton("Gui");
        
        String chatMessages = jedis.get(Chat);
        if (chatMessages != null) {
            chatArea.setText(chatMessages);
        }
        
   

        send.addActionListener(new ActionListener() {
           
			@Override
            public void actionPerformed(ActionEvent e) {
                // Lấy tin nhắn từ TextField
                String message = txtNg1.getText();

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
                    txtNg1.setText("");
                } 
            }
        });
        
        
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Nguoi1 frame = new Nguoi1();
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
	public Nguoi1() throws Exception {
		
		setTitle("Ung dung chat 1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 562, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		
		
		
		
		//config environment for JMS
				BasicConfigurator.configure();
				//config environment for JNDI
				Properties settings=new Properties();
				settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
						"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
				settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
				//create context
				Context ctx=new InitialContext(settings);
				//lookup JMS connection factory
				ConnectionFactory factory=
						(ConnectionFactory)ctx.lookup("TopicConnectionFactory");
				//lookup destination. (If not exist-->ActiveMQ create once)
				final Destination destination=
						(Destination) ctx.lookup("dynamicTopics/sangnguyen");
				Destination destination1=
						(Destination) ctx.lookup("dynamicTopics/dauthi");
				//get connection using credential
				Connection con=factory.createConnection("admin","admin");
				//connect to MOM
				con.start();
				//create session
				final Session session=con.createSession(
						/*transaction*/false,
						/*ACK*/Session.AUTO_ACKNOWLEDGE
						);
				//create producer
				MessageConsumer receiver = session.createConsumer(destination1);
				//create text message
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
				frame.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);
				frame.getContentPane().add(txtNg1, BorderLayout.SOUTH);
				frame.getContentPane().add(send, BorderLayout.EAST);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//gửi
				try {
					MessageProducer producer = session.createProducer(destination);
					Message msg=session.createTextMessage(txtNg1.getText());
					producer.send(msg);
				} catch (JMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//shutdown connection
			}
		});
		btnNewButton.setBounds(452, 327, 85, 32);
		contentPane.add(btnNewButton);
		
		
	}

}
