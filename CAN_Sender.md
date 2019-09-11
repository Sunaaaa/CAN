## Exam02_Sender

- Field 선언

  - 사용할 COM 포트를 지정

    ```java
    private CommPortIdentifier portIdentifier;
    ```

    <br>

  - 만약 COM 포트를 사용할 수 있고, 해당 포트를 open 하면 COM 포트 객체를 획득

    ```java
    private CommPort commPort;
    ```

    <br>

  - COM 포트는 종류가 2가지

    - Serial, Parallel

    - CAN 통신은 Serial 통신

    - COM 포트의 타입을 알아내서 type casting을 시켜야 한다. 

      ```java
      private SerialPort serialPort;
      ```

    - Port 객체로부터 Stream을 얻어내서 입출력을 할 수 있다. 

      ```java
      //문자열이 아닌 Byte 계열로 입출력
      private BufferedInputStream bis;
      private OutputStream out;
      ```

    <br>

  - JavaFX

    ```java
    // 메시지 창 (받은 메시지를 보여주는 역할)
    TextArea textarea;
    TextField tf;
    
    // 연결 버튼 (COM 포트 연결 버튼) -> 메시지가 들어오는지 기다렸다가 데이터가 들어오면 받는다.
    Button connBtn, sendBtn;
    ```

  <br><br>

- MyPortListener Class 생성

  - Serial Port에서 발생하는 이벤트를 처리하기 위한 Class 생성

    ```java
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
    ```

  <br><br>

- connectPort 버튼을 누르면 COM8 포트에 연결한다.

  - Port를 열고, Serial Port 객체를 획득한다.

  - Serial Port의 이벤트를 감지하는 Listener를 붙여 메시지가 들어왔을 때 알림을 준다.

  - Serial Port의 데이터가 유효할 때 (데이터가 들어옴) 알려주는 기능을 활성화한다.

  - Serial Port에 대해 InputStream과 OutputStream을 열어 데이터를 주고 받을 준비를 한다.

  - CAN 데이터 수신 허용을 설정한다.

    - 프로토콜을 이용해서 정해진 형식대로 문자열을 만들어서 out stream을 통해서 출력한다.

  - OutputStream을 이용해 문자열을 보낸다.

  - connectPort() 함수 

    ```java
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
    ```

    <br><br>

- TextField에 데이터를 입력하고 Enter를 '빵'하면 sendDate()를 호출하여 데이터를 송신한다.

  ```java
  tf.setOnAction(t->{
      String portName = "COM8";
      sendData(portName);
  });
  ```

  <br><br>

- 숫자가 아닌 문자열을 송신하기 위해 string을 Hex로 변환하는 함수 ( stringToHex() )를 정의한다. 

  ```java
  public static String stringToHex(String s) {
      String result = "";
  
      for (int i = 0; i < s.length(); i++) {
          result += String.format("%02X", (int) s.charAt(i));
      }
      return result;
  }
  ```

  <br><br>

- sendData() 함수를 호출하여 데이터를 보낸다.

  - getCheckSum() 함수를 호출하여 CAN 송신 데이터를 받는다.

    ```java
    private void sendData(String portName) {
        String msg = getCheckSum();
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
    ```

    <br>

  - getCheckSum() 함수 

    - CheckSum 은 시작문자와 끝 문자를 제외한 송신 데이터의 합을 0xff와 AND 연산을 수행한 값의 HexString 값이다. 
  
      - "명령코드 + 송신 데이터 속성 코드 + CAN 송신 ID + CAN 송신 데이터" 의 ASCII연산을 수행한다.
    - 위의 연산 결과와 0xff의 AND 연산을 수행한다.
      - 위의 연산 결과의 Hex String 값이 Check Sum 값이다. 

    - 결과 값은 항상 10의 자리 수이다.

      - 예외) CAN 송신 데이터가 'zzzzzzzz'인 경우 결과 값이 한자리 수인 '8'이다.

        그래서 CheckSum 값이 오류 라는 '?W03'  통신 에러 응답코드가 발생한다.

        - if문을 이용해 위의 상황이 발생하면 CheckSum 값 앞에 0을 붙인다. 

    - 예 > W28000000076162636461626364

      - Check Sum 값 : 8C
  
      ```java
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
      
          if (b.length()==1) {
              return ":"+msg+"0"+b+"\r";			
          }else {
              return ":"+msg+b+"\r";
          }
    }
      ```

    <br>

  - 실행화면 

    - 초기화면 & Port 연결 

      ![1568197773692](https://user-images.githubusercontent.com/39547788/64692677-a9e2a600-d4d0-11e9-8625-43757012c373.png)

      <br>

    - 'abcdabcd'라는 데이터 보내기

      ![1568197847835](https://user-images.githubusercontent.com/39547788/64692676-a9e2a600-d4d0-11e9-8387-3bdf4c3a78c8.png)

      <br>
  
    - CheckSum 값 & 송신 Message 확인
  
      ![1568197898767](https://user-images.githubusercontent.com/39547788/64692674-a9e2a600-d4d0-11e9-9ea0-501dcc09b243.png)
      
      <br>
      
    - 수신 측 Message 수신 확인
  
      ![1568198338912](https://user-images.githubusercontent.com/39547788/64692673-a94a0f80-d4d0-11e9-8509-9de60baf24d8.png)
  
      <br>
  
    - 'zzzzzzzz'라는 데이터 보내기
  
      ![1568198398739](https://user-images.githubusercontent.com/39547788/64692671-a94a0f80-d4d0-11e9-8e86-ea1e2a344d1d.png)
  
      <br>
  
    - CheckSum 값 & 송신 Message 확인
  
      ![1568198421839](https://user-images.githubusercontent.com/39547788/64692669-a94a0f80-d4d0-11e9-9806-a00c9431c2f0.png)
  
      <br>
  
    - 수신 측 Message 수신 확인
  
      ![1568198479036](https://user-images.githubusercontent.com/39547788/64692666-a94a0f80-d4d0-11e9-9b6d-b09e67d426a0.png)
  
      <br>
  
  - 전체 코드
  
    ```java
    package CanTest;
    
    import java.io.BufferedInputStream;
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
    import javafx.scene.control.TextField;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.FlowPane;
    import javafx.stage.Stage;
    
    public class Exam02_Sender extends Application{
    
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
            String msg = getCheckSum();
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
    
            if (b.length()==1) {
                return ":"+msg+"0"+b+"\r";			
            }else {
                return ":"+msg+b+"\r";
    
            }
    
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
            });
    
            tf.setOnAction(t->{
                String portName = "COM8";
    
                sendData(portName);
            });
    
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
    
    ```
  
    
  
  
  
  <br><br><br><br>

### CAN 송신 데이터

- CAN 네트워크 상에 특정 CAN Message를 보내고자 할 때 사용하는 명령

- 예> 'abcdabcd'를 보내기

  ![1568196568411](https://user-images.githubusercontent.com/39547788/64692680-aa7b3c80-d4d0-11e9-9e63-25fc2732624a.png)



<br><br>

- 송신 데이터 특성 코드 값 구하기 

  ![1568196595412](https://user-images.githubusercontent.com/39547788/64692679-aa7b3c80-d4d0-11e9-9418-024c54d540b5.png)

  <br>

  - 데이터의 길이는 8, Frame type은 Data Frame, 프로토콜은 CAN2.0B 인 경우의 송신 데이터 특성 코드 값 = **<u>"28"</u>**
  
    ![1568196814903](https://user-images.githubusercontent.com/39547788/64692678-aa7b3c80-d4d0-11e9-8fdc-479f9e9fd7aa.png)



