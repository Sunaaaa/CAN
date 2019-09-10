# CAN ( Controller Area Network )

- 우리가 일반적으로 사용하는 LAN ( Local Area Network ) 환경이 아닌 구조적으로 다른 Network 환경이 아닌 구조적으로 다른 Network 환경

- 1986년도 Mercedes-Benz  사에서 로베르트 보쉬 사에게 의뢰를 해요 .

  -  3개의 ECU가 통신을 할 수 있는 네트워크 구조를 만들어봐!!

  - ECU ( 전자적 제어 장치, Electronic Control Unit )

    - 자동차에 들어있는 ECU를 간단하게 살펴보자

      - ACU ( Airbag Control Unit )
      - BCM ( Body Control Module )
      - ECU ( Engine Control Unit ) : 엔진 제어 
      - TCU ( Transmission Control Unit ) : 변속 기어
      - ABS ( Anti-Lock Braking System )

    - ECU에게 적절한 신호를 받기 위해 각 ECU별로 

    - ECU가 여러가지 신호를 받아서 제어를 해야 한다. 

      - 무수히 많은 메시지  중 우선순위 (중요도) 가 높은 것이 우선
        -  각 ECU 별로 지정한 아이디가 작은 것이 우선 순위가 높음

    - Genesis : 70 개의 ECU가 들어가 있다. 

    - Mercedes-Benz, BMW : 80 개의 ECU가 들어가 있다.

    - 렉서스 : 100개 이상의 ECU가 들어가 있다. 

      

    - 1986년에 보쉬 사가 만들어서 자동차 기술자 협회에서 발표 

    - 1991년 CAN 2.0이 발표

    - 1992년 Mercedes-Benz 사에서 CAN을 채택한 자동차 출시

    - 1993년 ISO 에 의해서 표준화



- ECU간의 데이터 통신

  <br>

  ​			![1568079492588](https://user-images.githubusercontent.com/39547788/64586838-7e7c9000-d3d8-11e9-85fb-92ef9545b9cd.png)

  - 모든 기계 장치에는 센서가 붙어 있다. 
    - 해당 장치의 상태 및 정보를 센싱하여 해당 데이터를 알아낸다.

  <br>

  <br>

  <br>

  ![1568077366949](https://user-images.githubusercontent.com/39547788/64586835-7e7c9000-d3d8-11e9-96fe-bcaf50c2a381.png)

  - ECU 하나가 여러 개의 센서로 부터 데이터를 받는다.
    - 같은 종류끼리의 센서들을 모아서 하나의 ECU에서 처리
    - 연료통의 ECU : 센싱해서 나온 값을 제공
    - 연료 게이지의 ECU : 데이터를 계속 받아서 화면에 제공

  <br>

  <br>

  <br>

  ![1568078670849](https://user-images.githubusercontent.com/39547788/64586836-7e7c9000-d3d8-11e9-8bc9-0264d8574ada.png)

  - 선으로 직접 연결하여 통신

    - 선으로 연결하여 데이터를 주고 받기 위해서는 IO 단자가 필요하다.

    - 연결되는 ECU마다 다수개의 IO 단자가 필요하다.

      <br>

  - 기본적으로 각 ECU를 선으로 연결하는 방식은 좋지 않다.

    - IO 단자가 많이 필요하고, 소형화하는데 문제가 발생한다.

    - 막대한 비용 발생한다.

      ==> 다른 방식으로 통신 : **<u>" Serial (직렬) 통신"</u>**

      

      

      <br><br><br>

      ​	

  ![1568078690909](https://user-images.githubusercontent.com/39547788/64586837-7e7c9000-d3d8-11e9-8c3a-6a8e1a0bb59a.png)

  - Serial (직렬) 통신 & BUS

    - 직렬 통신 VS 병렬 통신
      - 직렬 통신 
        - 선이 하나 이기 때문에 속도는 느리다.
        - 선이 하나 이기 때문에 저렴하고 소형화하기 좋다.
      - 병렬 통신 
        - 선이 다수 개이기 때문에 빠르지만 선마다 IO 단자가 여러 개 이기 때문에 비싸고 소형화가 어렵다.
  - 우리가 사용하는 대표적인 직렬 통신 방식의 기기 : USB, 컴퓨터의 COM 포트를 이용한 통신
    
  
  <br>
      
    - CAN BUS
      - 동축케이블로 만들어진 BUS
      - 각각의 ECU를 서로 직접 연결시키는게 아니라 BUS 개념을 도입
        - CAN은 CAN BUS에 대한 단일 입출력 인터페이스만 가지고 있는 것이 특징
        
          - 입출력 단자가 1개만 있으면 된다.
        
          <br>
        
          <br>
  
  - 각각의 ECU는 하나의 IO 단자를 갖는다.
  
  - IO 단자에 선이 2개인 이유 
  
    - 견고하게 데이터를 보낼 수 있다.
        - CAN BUS를 통해서 데이터를 보낼 때, 자동차의 환경이 열약하여 데이터가 깨질 위험이 있다.
      - 2개의 전압 차이를 이용하여 데이터를 표현하여 CAN BUS에 하고 흘린다.
        - 각각의 ECU가 흘러 다니는 데이터를 보고 필요한 데이터를 캐치하여 갖는다.
  
  <br><br>
  
  - 자동차는 기본적으로 네트워크 환경이 굉장히 열악한 환경이에요.
    - 온도, 충격, 진동이 많은 환경이라서 네트워크 통신할 때 오류가 발생할 여지가 많아요.
    - 자동차와 관련된 CAN 이외의 통신 방식이 존재하지만, CAN이 대표적으로 많이 사용되고 언급되는 이유 : **<u>"안정성"</u>**
      
      - 네트워크 속도는 느리다.
      
      <br><br>
    
  - CAN BUS에는 하나의 ECU만이 Message를 보낼 수 있다. 
  
    - 만약, 동시에 Message가 발생하면, 우선 순위가 높은 ECU의 Message를 먼저 보낸다.
    - 데이터를 보낼 때, CAN BUS가 Idle 상태인지 확인한다.



<br><br><br>



## CAN의 장점

- CAN은 Multi-Master 통신을 해요!
  - 서버와 클라이언트가 존재하지 않아요.
  - 누구든, 어떤 ECU든 해당 CAN BUS가 Idle하면 해당 BUS를 잡아서 스스로 데이터를 보낼 수 있다.

<br>

- 노이즈에 매우 강해요.
  - 차량 자체가 매우 열악한 환경인데, CAN은 2개의 가닥의 꼬인 전선을 이용하여 전선의 전압차를 통해 데이터를 전송하는 방식

<br>

- 표준 프로토콜

  - 사람들이 많이 만들어 사용할 수 밖에 없다.
    - **<u>"시장성 확보"</u>**

  <br>

- 하드웨어적으로 오류보정이 가능해요!

  - CRC를 하드웨어적으로 만들어서 전송한다.
  - 받는 측에서 CRC를 이용하여 데이터 프레임의 오류가 있는지를 확인한다.
    - 만약, 오류가 있으면 응답을 보낸다.
    - 해당 응답을 확인해서 재전송을 하게 된다. 
      - **<u>"오류 보정 가능"</u>**

<br>

- 다양한 통신 방식을 지원한다. 

  - BroadCast : 모두와 통신
  - MultiCast : 일부만 통신
  - UniCast : 1대1 통신
  - 통신 방식이 address를 기반이 아니다.
    - 누가 누구에서 제공 , 목적지를 갖고 해당 데이터 프레임을 핑하고 날리는 것이 아니다. 
    - BUC Cycle을 이용해 데이터 프레임을 살펴본 뒤, 판단하여 해당 데이터 프레임을 갖는다.
  - 수신 측에서 filter, mask를 이용해서 데이터를 받을지 판단한다.

  <br>

- ECU 간에는 우선순위가 존재한다.

  - 각 ECU마다 고유의 ID가 존재
    - ID 값이 작을수록 우선 순위가 높다.
    - 우선 순위를 이용하여 급한 Message를 먼저 처리할 수 있다. 

<br>

- CAN BUS 를 사용한다.
  - 비용 절감의 효과
    - 전선의 양을 줄일 수 있다. 
    - IO 단자의 수를 줄일 수 있다. 
  - 새로운 ECU가 추가되면 새로운 선을 깔지 않고 CAN BUS에 연결해주기만 하면 된다.
    - 확장성 Good





- RT는 접지, 사용 안함
- R과 L만 사용
  - Serial 통신이기 때문에  R은 R끼리, L은 L끼리 연결



- Mask가 모두 0 : 메시지를 거르지 않음, 모든 메시지를 수신함



## CAN 실습

- CAN 통신장비 연결 <br>

  ![1568092426282](https://user-images.githubusercontent.com/39547788/64586839-7f152680-d3d8-11e9-8b50-a346afaafa1e.png)

  <br>

  ![1568092433045](https://user-images.githubusercontent.com/39547788/64586841-7f152680-d3d8-11e9-885c-b5ab6ebdd9e4.png)

  <br>

  ![1568092441796](https://user-images.githubusercontent.com/39547788/64586843-7f152680-d3d8-11e9-84b9-3e6d4d76071e.png)

  <br><br>

- CAN Pro Analyzer

  ![1568092534528](https://user-images.githubusercontent.com/39547788/64586844-7f152680-d3d8-11e9-9362-7e137edf0eba.png)

  <br>

  ![1568092544009](https://user-images.githubusercontent.com/39547788/64586846-7fadbd00-d3d8-11e9-9936-7e1374d187ca.png)

  <br>

  ![1568092590580](https://user-images.githubusercontent.com/39547788/64586847-7fadbd00-d3d8-11e9-8b5c-5d7aef9ea7d9.png)

  <br><br>

  

  - 직렬 통신으로 연결되어 있는 com에서 해당 패킷을 받으면 연결 완료 

    - 직렬 연결

      ![1568093292529](https://user-images.githubusercontent.com/39547788/64586866-80deea00-d3d8-11e9-97fc-a807f1c5dfd5.png)

      <br>

    - 현재 나의 기기 (ECU)에 대한 환경을 설정

      ![1568092625988](https://user-images.githubusercontent.com/39547788/64586850-7fadbd00-d3d8-11e9-964b-4d22ae62fc22.png)

      <br>

      - Id 설정

        ![1568092725152](https://user-images.githubusercontent.com/39547788/64586851-7fadbd00-d3d8-11e9-873c-9147e63a6a7b.png)

        <br>

    - 설정 완료 

      ![1568092752255](https://user-images.githubusercontent.com/39547788/64586854-80465380-d3d8-11e9-8878-9c6b29635df7.png)

      <br>

    - CAN 데이터 수신 시작하기

      ![1568092796503](https://user-images.githubusercontent.com/39547788/64586856-80465380-d3d8-11e9-9deb-983450598eda.png)

      <br>

      - 수신 중

        ![1568092814238](https://user-images.githubusercontent.com/39547788/64586857-80465380-d3d8-11e9-86e7-ead715eab221.png)

        <br><br>

  - 데이터 보내기

    - 송신 데이터 생성

      ![1568092866729](https://user-images.githubusercontent.com/39547788/64586859-80465380-d3d8-11e9-839e-719d2dd251dd.png)

      <br>

    - Frame 타입

      - DataFrame

        - 데이터를 보냄

      - Remote Frame

        - 데이터를 보내라고 요청

        - 데이터를 보내지 않음

          ![1568092925249](https://user-images.githubusercontent.com/39547788/64586861-80deea00-d3d8-11e9-808b-b9b8117a925c.png)

          <br>

    - 데이터 송신

      ![1568092965353](https://user-images.githubusercontent.com/39547788/64586862-80deea00-d3d8-11e9-95ab-aa3684ae25d7.png)

      <br>

    - 연결된 com으로 부터 데이터 수신

      ![1568092979490](https://user-images.githubusercontent.com/39547788/64586865-80deea00-d3d8-11e9-84f9-5d7a788b26bc.png)

      <br>

    - 본인이 보낸 데이터 수신됨을 확인

      ![1568093482914](https://user-images.githubusercontent.com/39547788/64586867-81778080-d3d8-11e9-8a41-f0cb1691c286.png)

      <br>





## App을 이용하여 자동차 도어 오픈

![1568103407992](https://user-images.githubusercontent.com/39547788/64596593-27ce8080-d3ef-11e9-88e2-fee3c56d9821.png)





## App과 센서를 이용하여 자동차 도어 오픈

![1568103638490](https://user-images.githubusercontent.com/39547788/64596700-56e4f200-d3ef-11e9-9787-fb9f11ba8904.png)