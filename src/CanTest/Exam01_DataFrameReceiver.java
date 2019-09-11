package CanTest;

import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Exam01_DataFrameReceiver extends Application{

	// 메시지 창 (받은 메시지를 보여주는 역할)
	TextArea textarea;
	
	// 연결 버튼 (COM 포트 연결 버튼) -> 메시지가 들어오는지 기다렸다가 데이터가 들어오면 받는다.
	Button connBtn;
	
	// 사용할 COM 포트를 지정하기 위해서 필요
	private CommPortIdentifier portIdentifier;
	
	// 만약 COM 포트를 사용할 수 있고, 해당 포트를 open 하면 COM 포트 객체를 획득
	private CommPort commPort;

	// COM 포트는 종류가 2가지 에요. ( Serial, Parallel 두가지 종류 )
	// CAN 통신은 Serial 통신을 해요. 따라서, COM 포트의 타입을 알아내서 type casting을 시켜요
	// socket과 같은 개념으로 생각 -> stream을 뽑아낼 수 있다. 
	private SerialPort serialPort;
	
	// Port 객체로부터 Stream을 얻어내서 입출력을 할 수 있다. 
	// Byte 계열로 입출력을 한다.
	private BufferedInputStream bis;
	private OutputStream out;
		
	public static void main(String[] args) {
		launch();
	}
	
	// inner Class 형식으로 event 처리하는 Listener Class를 작성
	// SerialPort에서 발생하는 이벤트를 처리하기 위한 클래스
	// -> 해당 객체는 SerialPort의 이벤트를 감지하고 처리한다.
	class MyPortListener implements SerialPortEventListener{

		// 이벤트가 발생하면 serialEvent() 가 호출
		// event 객체로 이벤트에 대한 세부 정보를 알 수 있다.
		@Override
		public void serialEvent(SerialPortEvent event) {
			// Serial Port에서 event가 발생하면 호출!
			// DATA_AVAILABLE : 데이터가 들어옴
			// 지금 발생한 이벤트가 데이터가 들어온거니?
			if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				// Port를 통해서 데이터가 들어왔다는 의미
				byte[] readBuffer = new byte[128];
				
				try {
					
					// Stream 안의 데이터를 반복해서 읽어온다.
					// bis.available()  > 0: 현재 InputStream에 데이터가 존재할 때까지
					// bis.available()  <= 0: Stream에 더이상 읽을 데이터가 없다.
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
	
	private void printMsg(String msg) {
		Platform.runLater(()->{
			textarea.appendText(msg + "\n");
		});
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
				// Port가 존재하고 내가 사용할 수 있다. 
				// Port를 열고 Port 객체를 획득
				// 첫번 째 인자 : Prot를 여는 프로그램의 이름 (문자열)
				// 두번 째 인자 : Port를 열 때 Blocking을 기다릴 수 있는 시간 (밀리세컨드) - "5초는 기다려봐"
				// CommPortIdentifier을 이용해서 CommPort 객체를 만든다.
				commPort = portIdentifier.open("MyApp", 5000);
				
				// Port 객체(commPort)를 얻은 후 Serial인지 Parallel인지를 확인한 후 적절하게 Type Casting
				if (commPort instanceof SerialPort) {
					// SerialPort이면 True
					// SerialPort로 Type Casting해서 Serial Port 객체를 얻어낼 수 있어요~
					serialPort = (SerialPort)commPort;
					
					// SerialPort에 대한 설정
					// 첫번 째 인자 : Serial Port 통신 속도
					// 두번 째 인자 : 데이터의 비트 ( 보내는 곳에서 데이터의 길이를 8로 보내면 8로 받아야 한다. )
					// 세번 째 인자 : Stop bit 설정
					// 네번 째 인자 : Parity bit는 사용하지 않음
					serialPort.setSerialPortParams(921600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					
					// Serial Port를 Open하고 설정까지 잡아 놓은 상태
					// 나에게 들어오는 Data Frame을 받아들일 수 있는 상태
					// 포트에 이벤트를 감지하는 리스너를 붙여 메시지가 들어왔을 때 알림을 준다.
					// - Data Frame이 전달되는 것을 감지하기 위해서 Event 처리 기법을 이용
					// - 데이터가 들어오는 걸 감지라고 처리하는 Listener 객채가 있어야 한다.
					// - 이런 Listener 객체를 만들어서 Port에 리스너로 등록해주면 되요!
					// - 당연히 Listener 객체를 만들기 위한 Class가 있어야 해요~
					serialPort.addEventListener(new MyPortListener());
					
					// Serial Port의 데이터가 유효할 때 (데이터가 들어옴) 알려주는 기능을 활성화
					serialPort.notifyOnDataAvailable(true);
					printMsg(portName + "에 리스너가 등록되었어요~");
					
					// 입출력을 하기 위해서 Stream을 열면 되요~
					// 데이터 받기
					bis = new BufferedInputStream(serialPort.getInputStream());
					// 데이터 보내기
					out = serialPort.getOutputStream();
					
					// CAN 데이터 수신 허용 설정
					// 이 작업은 어떻게 해야 하나요?
					// 프로토콜을 이용해서 정해진 형식대로 문자열을 만들어서 out stream을 통해서 출력
					
					// checksum : A9
					// 끝 문자 : \r
					// 이제부터 나의 CAN 장비가 수신을 시작할꺼여~
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
		
		connBtn = new Button("COM 포트 연결!!");
		connBtn.setPrefSize(150, 100);

		// 람다식을 사용해서 이벤트를 핸들링한다.
		connBtn.setOnAction(t->{
			String portName = "COM8";
			
			// portName 연결
			connectPort(portName);
		});
				
		root.setBottom(connBtn);
		
		// 화면에 띄우기
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("CAN Data Frame Receiver 예제");
		primaryStage.show();
		
	}

}
