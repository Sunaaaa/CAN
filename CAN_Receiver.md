# 데이터를 받기_Receiver

- (Thread로 수행)



- Serial 통신하기 위한 jar와 dll이 필요함

  ![1568160686911](https://user-images.githubusercontent.com/39547788/64692196-72272e80-d4cf-11e9-806e-f180001999f0.png)

  <br>

  - jar 파일을 프로젝트에 포함

    ![1568160838698](https://user-images.githubusercontent.com/39547788/64692197-72bfc500-d4cf-11e9-9653-a34acf6b4b4d.png)

    <br>

  - 아래의 경로에 dll 파일을 붙여넣기

    ![1568160998989](https://user-images.githubusercontent.com/39547788/64692198-72bfc500-d4cf-11e9-98e0-ef3c0784d538.png)

    <br>

  - JavaFX를 사용하기 위해 jar 파일 추가하기

    ![1568161367109](https://user-images.githubusercontent.com/39547788/64692200-72bfc500-d4cf-11e9-806f-3162dcbb2bd3.png)

  <br>

  <br>

  <br>

  - xxx.dll을 복사해야하는데 폴더가 없는 경우?
    - java 실행할 때 런타임 인자를 이용해서 dll 파일이 있는 폴더를 알려주면되요!
    - 값은 사용할 dll이 들어가 있는 폴더를 지칭
      - -Djava.library.path=여기는 dll 파일들의 경로

<br><br><br>

## Exam01_DataFrameReceiver

- Field 선언

  - 사용할 COM 포트를 지정

    ```java
    private CommPortIdentifier portIdentifier;
    ```

  - 만약 COM 포트를 사용할 수 있고, 해당 포트를 open 하면 COM 포트 객체를 획득

    ```java
    private CommPort commPort;
    ```

    

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

  - JavaFX

    ```java
    // 메시지 창 (받은 메시지를 보여주는 역할)
    TextArea textarea;
    
    // 연결 버튼 (COM 포트 연결 버튼) -> 메시지가 들어오는지 기다렸다가 데이터가 들어오면 받는다.
    Button connBtn;
    ```

  <br>

- connBtn 버튼을 누르면 COM 포트에 연결한다.

  ```java
  connBtn = new Button("COM 포트 연결!!");
  connBtn.setPrefSize(150, 100);
  
  // 람다식을 사용해서 이벤트를 핸들링한다.
  connBtn.setOnAction(t->{
      String portName = "COM8";
  
      // portName 연결
      connectPort(portName);
  });
  ```

  <br>

- COM 포트를 연결하는 coonectPort () 함수 

  - portName을 이용해 Port에 접근해서 객체를 생성한다.

  - portName을 이용해서 CommPortIdentifier 객체 생성

    ```
    portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    ```

  - portIdentifier를 이용해 현재 COM 포드가 사용되고 있는지 확인

    - portIdentifier.isCurrentlyOwned() : 이용 중인지 확인

      - 내가 사용할 수 있는 상태인 경우

        - Port를 열고 Port 객체를 획득

        - CommPortIdentifier을 이용해서 CommPort 객체 생성

          - 첫번 째 인자로 Port를 여는 프로그램의 이름을 String으로 제공한다.

          - 두번 째 인자로 Port를 열 때 Blocking을 기다릴 수 있는 시간을 밀리미터로 제공한다.

            ```java
            commPort = portIdentifier.open("MyApp", 5000);
            ```

          <br>

        - Port 객체(commPort)를 얻은 후 Serial인지 Parallel인지를 확인한 후 적절하게 Type Casting을 수행한다.

          - commPort가 Serial Port 이면 SerialPort로 Type Casting해서 Serial Port 객체를 얻는다.

            ```java
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort)commPort;
            }
            ```

          - 얻어낸 Serial Port 객체에 대한 설정

            - 첫번 째 인자로 Serial Port의 통신 속도를 제공한다.

            - 두번 째 인자로 데이터의 비트를 제공한다. 

              - 보내는 곳에서 데이터의 길이응 8로 보내면, 8로 받아야 한다.
              - 상수 값 SerialPort.DATABITS_8

            - 세번 째 인자로 Stop Bit를 제공한다.

              - 상수 값 SerialPort.STOPBITS_1

            - 네번 째 인자로 Parity Bit를 제공한다.

              - 상수 값 SerialPort.PARITY_NONE ( 사용하지 않음 )

              <br><br>

          #### 여기까지 Serial Port를 열고 설정까지 완료!!!

          #### 나에게 들어오는 Data Frame을 받아들이는 상태가 되었다.

          <br><br>

        - Port에 이벤트를 감지하는 리스너를 붙여 메시지가 들어왔을 때 알림

          - Data Frame이 전달되는 것을 감지하기 위해서 Event 처리 기법을 이용
          - 데이터가 들어오는 걸 감지라고 처리하는 Listener 객체가 있어야 한다.
          - Listener 객체를 만들기 위한 Class를 생성한다.

          <br>

        - MyPortListener Class 생성
          - SerialPort에서 발생하는 이벤트를 처리하기 위한 클래스

            - inner Class 형식으로 event 처리하는 Listener Class를 작성

            - 해당 객체는 SerialPort의 이벤트를 감지하고 처리한다.

            - SerialPortEventListener를 implements 한다.

              ```java
              class MyPortListener implements SerialPortEventListener{
              
                  @Override
                  public void serialEvent(SerialPortEvent arg0) {
                      // Serial Port에서 event가 발생하면 호출!
                  }
              }
              ```

          <br>

        - serial Port에 이벤트를 붙인다.

          ```java
          serialPort.addEventListener(new MyPortListener());
          ```

          <br>

        - Serial Port의 데이터가 유효할 때 (데이터가 들어옴) 알려주는 기능을 활성화한다.

          ```java
          serialPort.notifyOnDataAvailable(true);
          ```

          <br>

        - 입출력을 하기 위한 Stream 열기

          ```java
          // 데이터 받기
          bis = new BufferedInputStream(serialPort.getInputStream());
          // 데이터 보내기
          out = new ObjectOutputStream(serialPort.getOutputStream());
          ```

          <br>

        - 이벤트가 발생하면 serialEvent() 가 호출

          - event 객체로 이벤트에 대한 세부 정보를 알 수 있다.

          - Serial Port에서 event가 발생하면 호출된다. 

          - event.getEventType() == SerialPortEvent.DATA_AVAILABLE : 지금 발생한 이벤트가 데이터가 들어온거니?

            - DATA_AVAILABLE : 데이터가 들어옴

            - Stream 안의 데이터를 반복해서 읽어온다.

              - bis.available()  > 0: 현재 InputStream에 데이터가 존재할 때까지 읽는다.

              - bis.available()  <= 0: Stream에 더이상 읽을 데이터가 없다.

                ```java
                class MyPortListener implements SerialPortEventListener{
                
                    @Override
                    public void serialEvent(SerialPortEvent event) {
                        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                            // Port를 통해서 데이터가 들어왔다는 의미
                            byte[] readBuffer = new byte[128];
                
                            try {
                                while(bis.available() > 0) {
                                    bis.read(readBuffer);						
                                }
                                String result = new String(readBuffer);
                                printMsg("받은 메시지는 ___ " + result + "___");
                            } catch (Exception e) {
                                // TODO: handle exception
                                System.out.println(e.toString());
                            }
                        }
                    }
                }
                ```

          <br><br>

- 실행 화면

  ![1568168979487](https://user-images.githubusercontent.com/39547788/64692203-73585b80-d4cf-11e9-8fea-84b022a925b6.png)

  <br><br>

- CAN 데이터 수신 허용 설정

  - 프로토콜을 이용해 정해진 형식대로 문자열을 만들어서 outputstream을 통해 출력

    - 문자열 만들기

      ```java
      String msg = ":G11A9\r";
      ```

      - check sum : A9
      - 끝 문자 : \r

    - 문자열을 outputStream으로 바로 보낼 수 없기 때문에 byte[]의 형태로 변환하여 전송한다.

      ```java
      try {
          byte[] inputData = msg.getBytes();
          out.write(inputData);
          printMsg(portName + "가 수신을 시작합니다.");
      } catch (Exception e) {
          // TODO: handle exception
          System.out.println(e);
      }
      ```

  <br><br>

- 실행화면

  ![1568173624842](https://user-images.githubusercontent.com/39547788/64692204-73585b80-d4cf-11e9-9eeb-df50c47f854f.png)

  <br>

- CANPro Analyzer에서 DataFrame 생성하여 보내기

  ![1568174650271](https://user-images.githubusercontent.com/39547788/64692182-705d6b00-d4cf-11e9-887a-3d16ab1222e0.png)

  <br>

- 우리 프로그램에서 Data Frame 수신

  ![1568174696246](https://user-images.githubusercontent.com/39547788/64692184-705d6b00-d4cf-11e9-9984-d318e4dca4ab.png)

<br>

  - 자바 환경 확인하기

    ![1568168906206](https://user-images.githubusercontent.com/39547788/64692201-72bfc500-d4cf-11e9-8576-ef390fc546a7.png)

<br><br><br>



### MaskID 사용하기

- mask를 이용해 송신 측에서 필요한 데이터를 걸러간다.

- 송신 ID가 받는 쪽의 Mask

- 송신 ID와 수신 ID를 일치 & Mask를 모두 선택

  = 들어오는 송신 ID와 수신ID가 모두 같아야

  = Unicast

- 수신 Mask ID와 수신 ID를 비교한다. 

  - 상황 1

    ![1568179953956](https://user-images.githubusercontent.com/39547788/64692186-70f60180-d4cf-11e9-84f8-f84428cc3236.png)

    <br>

    - 수신 Mask ID에 ID4만 체크 되어있으므로, ID4만 수신 ID와 비교한다.

    - 수신 ID의 ID4가 체크 되어있기 때문에 

      - 아래의 송신 데이터는 수신할 수 있다. 

        ![1568180065889](https://user-images.githubusercontent.com/39547788/64692187-70f60180-d4cf-11e9-866e-e37eefea189e.png)

        <br>

      - 아래의 송신 데이터는 수신할 수 없다. 

        ![1568180139161](https://user-images.githubusercontent.com/39547788/64692188-70f60180-d4cf-11e9-8a78-2fd0a79dd49f.png)

        <br>

      - 수신 측 

        ![tyu](https://user-images.githubusercontent.com/39547788/64692195-72272e80-d4cf-11e9-98e9-4196d1d13a7a.png)

        <br>

  - 상황 2

    ![1568179851594](https://user-images.githubusercontent.com/39547788/64692185-70f60180-d4cf-11e9-8a8a-5568ead08635.png)

    <br>

    - 수신 Mask ID에 ID4만 체크 되어있으므로, ID4만 수신 ID와 비교한다.

    - 수신 ID의 ID4가 체크 되어있지 않기 때문에

      - 아래의 데이터는 수신할 수 없다.

        ![1568180065889](https://user-images.githubusercontent.com/39547788/64692187-70f60180-d4cf-11e9-866e-e37eefea189e.png)

        <br>

      - 아래의 데이터는 수신할 수 있다.

        ![1568180139161](https://user-images.githubusercontent.com/39547788/64692188-70f60180-d4cf-11e9-8a78-2fd0a79dd49f.png)

        <br>

      - 수신 측 

        ![1568199368931](https://user-images.githubusercontent.com/39547788/64692192-718e9800-d4cf-11e9-86fe-216101436aa0.png)

        <br>

  - 상황 3

    ![1568186312540](https://user-images.githubusercontent.com/39547788/64692189-718e9800-d4cf-11e9-9f43-89fd541c7751.png)

    <br>

    - 수신 Mask ID에 ID4와 ID0이 체크 되어있으므로, ID4와 ID0을 수신 ID와 비교한다.

    - 수신 ID의 ID4가 체크 되어있지 않기 때문에 

      - 수신 ID의 ID4는 반드시 0이어야 한다. 

      - 수신 ID의 ID0는 반드시 1이어야 한다. 

        - 아래의 데이터는 수신할 수 없다. 

          ![1568180065889](https://user-images.githubusercontent.com/39547788/64692187-70f60180-d4cf-11e9-866e-e37eefea189e.png)

          <br>

        - 아래의 데이터는 수신할 수 있다. 

          ![1568180139161](https://user-images.githubusercontent.com/39547788/64692188-70f60180-d4cf-11e9-8a78-2fd0a79dd49f.png)

          <br>

        - 수신 측

          ![1568199407446](https://user-images.githubusercontent.com/39547788/64692193-718e9800-d4cf-11e9-9289-5ee2de7db83b.png)

          <br>

  - 상황 4

    ![1568199210305](https://user-images.githubusercontent.com/39547788/64692191-718e9800-d4cf-11e9-9646-e8d4a06057d0.png)

    <br>

    - 수신 Mask ID에 ID4와 ID0이 체크 되어있으므로, ID4와 ID0을 수신 ID와 비교한다.

    - 수신 ID의 ID4와 ID0모두 체크 되어있기 때문에 

      - 수신 ID의 ID4와  ID0는 반드시 1이어야 한다. 

        - 아래의 데이터는 수신할 수 있다. 

          ![1568180065889](https://user-images.githubusercontent.com/39547788/64692187-70f60180-d4cf-11e9-866e-e37eefea189e.png)

          <br>

        - 아래의 데이터는 수신할 수 없다. 

          ![1568180139161](https://user-images.githubusercontent.com/39547788/64692188-70f60180-d4cf-11e9-8a78-2fd0a79dd49f.png)

        - 수신 측

          ![1568199450837](https://user-images.githubusercontent.com/39547788/64692194-72272e80-d4cf-11e9-8b0c-95a970f7c576.png)

  <br><br>

- 전체 코드

  ```java
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
  ```
