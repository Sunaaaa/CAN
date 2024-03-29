package CanTest;

import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import CanTest.Exam01_DataFrameReceiver.MyPortListener;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Exam02_DataFrameSender extends Application{

	// 메시지 창 (받은 메시지를 보여주는 역할)
	TextArea textarea;
	TextField tf;

	// 연결 버튼 (COM 포트 연결 버튼) -> 메시지가 들어오는지 기다렸다가 데이터가 들어오면 받는다.
	Button connBtn, sendBtn;

	// 사용할 COM 포트를 지정하기 위해서 필요
	private CommPortIdentifier portIdentifier;

	// 만약 COM 포트를 사용할 수 있고, 해당 포트를 open 하면 COM 포트 객체를 획득
	private CommPort commPort;

	private SerialPort serialPort;

	// Byte 계열로 입출력을 한다.
	private BufferedInputStream bis;
	private OutputStream out;

	public static void main(String[] args) {
		launch();
	}

	// SerialPort에서 발생하는 이벤트를 처리하기 위한 클래스
	class MyPortListener implements SerialPortEventListener{

		@Override
		public void serialEvent(SerialPortEvent event) {
			// Serial Port에서 event가 발생하면 호출!
			if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				// Port를 통해서 데이터가 들어왔다는 의미
				byte[] readBuffer = new byte[128];

				try {

					// Stream 안의 데이터를 반복해서 읽어온다.
					while(bis.available() > 0) {
						bis.read(readBuffer);						
					}

					String result = new String(readBuffer);
					printMsg("받은 메시지는 ___ " + result + "___");


				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e);
				}

			}

		}

	}

	public static String stringToHex(String s) {
		String result = "";

		for (int i = 0; i < s.length(); i++) {
			result += String.format("%02X", (int) s.charAt(i));
		}

		return result;
	}

	private void printMsg(String msg) {
		Platform.runLater(()->{
			textarea.appendText(msg + "\n");
		});
	}

	private void sendData(String portName) {
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

			printMsg(portName + "에 연결을 시도합니다.");

			if (portIdentifier.isCurrentlyOwned()) {
				printMsg(portName + "가 다른 프로그램에 의해서 사용되고 있어요 ㅠㅠ");				
			}else {
				commPort = portIdentifier.open("MyApp2", 5000);

				if (commPort instanceof SerialPort) {
					serialPort = (SerialPort)commPort;

					serialPort.setSerialPortParams(921600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

					serialPort.addEventListener(new MyPortListener());

					serialPort.notifyOnDataAvailable(true);
					printMsg(portName + "에 리스너가 등록되었어요~");

					// 데이터 받기
					bis = new BufferedInputStream(serialPort.getInputStream());
					// 데이터 보내기
					out = serialPort.getOutputStream();

					// CAN 데이터 수신 허용 설정
					//					String ckSum = getCheckSum();
//					String cksum = getCheckSum();
					String msg = getCheckSum();
					//					String msg = ":"+cksum+"\r";
					System.out.println(msg);


					// 문자열을 outputStream으로 바로 쏠수 없어서 try-catch
					try {
						byte[] inputData = msg.getBytes();
						out.write(inputData);
						printMsg(portName + "가 송신을 시작합니다.");
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
					}

				}
			}

		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	private String getCheckSum() {
		String data = stringToHex(tf.getText());
		String id = "00000007";
		String msg = "W28" + id + data;
		System.out.println(msg);


		int a = 0;
		for (char item : msg.toCharArray()) {
			a += item;
		}
		int result = a & 0xff;
		String b = Integer.toHexString(result).toUpperCase();
		System.out.println(result);
		System.out.println(Integer.toHexString(result).toUpperCase());

		return ":"+msg+b+"\r";
	}

	private void connectPort(String portName) {
		// portName을 이용해 Port에 접근해서 객체를 생성해요.
		try {
			// portName을 이용해서 CommPortIdentifier 객체를 만든다.
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

			printMsg(portName + "에 연결을 시도합니다.");

			// portIdentifier를 이용해서 현재 사용되고 있는지 확인한다.
			if (portIdentifier.isCurrentlyOwned()) {
				printMsg(portName + "가 다른 프로그램에 의해서 사용되고 있어요 ㅠㅠ");				
			}else {
				// Port를 열고 Port 객체를 획득
				commPort = portIdentifier.open("MyApp", 5000);

				// Port 객체(commPort)를 얻은 후 Serial인지 Parallel인지를 확인한 후 적절하게 Type Casting
				if (commPort instanceof SerialPort) {
					// SerialPort이면 True
					serialPort = (SerialPort)commPort;

					// SerialPort에 대한 설정
					serialPort.setSerialPortParams(921600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

					// 포트에 이벤트를 감지하는 리스너를 붙여 메시지가 들어왔을 때 알림을 준다.
					serialPort.addEventListener(new MyPortListener());

					// Serial Port의 데이터가 유효할 때 (데이터가 들어옴) 알려주는 기능을 활성화
					serialPort.notifyOnDataAvailable(true);
					printMsg(portName + "에 리스너가 등록되었어요~");

					// 데이터 받기
					bis = new BufferedInputStream(serialPort.getInputStream());
					// 데이터 보내기
					out = serialPort.getOutputStream();

					// CAN 데이터 수신 허용 설정
					// 프로토콜을 이용해서 정해진 형식대로 문자열을 만들어서 out stream을 통해서 출력
					String msg = ":G11A9\r";

					// 문자열을 outputStream으로 바로 쏠수 없어서 try-catch
					try {
						byte[] inputData = msg.getBytes();
						out.write(inputData);
						printMsg(portName + "가 수신을 시작합니다.");
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e);
					}

				}
			}

		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();

		root.setPrefSize(700, 500);

		textarea = new TextArea();
		root.setCenter(textarea);
		tf = new TextField();
		tf.setPrefSize(350, 100);

		connBtn = new Button("COM 포트 연결!!");
		connBtn.setPrefSize(100, 100);

		// 람다식을 사용해서 이벤트를 핸들링한다.
		connBtn.setOnAction(t->{
			String portName = "COM8";

			// portName 연결
			connectPort(portName);
		});

		sendBtn = new Button("긁적긁적");
		sendBtn.setPrefSize(100, 100);
		sendBtn.setOnAction(t->{
			String portName = "COM8";
			sendData(portName);
		});

		//		tf.setOnAction(t->{
		//			String portName = "COM8";
		//					
		//			sendData(portName);
		//		});

		// 긴 Panel 하나를 생성
		FlowPane flowpane = new FlowPane();
		flowpane.setPrefSize(700, 50);
		flowpane.getChildren().add(connBtn);
		flowpane.getChildren().add(tf);
		flowpane.getChildren().add(sendBtn);
		root.setBottom(flowpane);

		// 화면에 띄우기
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("CAN Data Frame Receiver 예제");
		primaryStage.show();

	}

}
