package guinguoi1;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
import javax.swing.JTextArea;

public class Nguoi11 extends JFrame {

	private JPanel contentPane;
	private JTextField textField_1;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Nguoi11 frame = new Nguoi11();
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
	public Nguoi11() throws Exception {
		
		setTitle("Ung dung chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 562, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField_1 = new JTextField();
		textField_1.setBounds(10, 333, 432, 32);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		textArea = new JTextArea();
		textArea.setBounds(10, 10, 527, 307);
		contentPane.add(textArea);
		textArea.setEditable(false);
		
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
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//gửi
				try {
					MessageProducer producer = session.createProducer(destination);
					Message msg=session.createTextMessage(textField_1.getText());
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
