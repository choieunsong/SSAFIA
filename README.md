# <img src="https://user-images.githubusercontent.com/24693833/129665846-234af092-1003-47aa-82f0-edff976605ed.png" width="50" height="50"> SSAFIA
화상으로 즐기는 마피아 게임

<br>

## 👩‍👩‍👧‍👦 팀원 소개
* 유태규
* 김지훈
* 김용훈
* 이현정
* 최은송

<br>

## 📝 프로젝트 개요 
* <strong>진행 기간</strong>: 2021.07.12 ~ 2021.08.20
* <strong>목표</strong>
  * 플레이어는 음성과 영상을 통해 서로 소통하며, 오프라인으로만 즐기던 마피아 게임을 온라인으로 즐길 수 있습니다.
  * 어플리케이션을 설치하지 않고도 웹 브라우저로 간편하게 접속해 어디서든 SSAFIA를 이용할 수 있습니다.
  * 회원가입을 하지 않아도 구글 로그인만 하면 쉽게 방을 생성할 수 있습니다.
  * URL로 친구들을 초대하면 로그인의 번거로움 없이 게임을 즐길 수 있습니다. 
  * 재미있는 애니메이션과 사용자 친화적인 UI/UX를 설계해 게임의 몰입감을 높입니다.
<br>

## ✍️ 프로젝트 소개 
<strong>SSAFIA</strong>는 온라인으로 마피아 게임을 즐길 수 있는 화상 기반 마피아 웹 게임입니다.

코로나 19 바이러스로 인해 언택트 시대에 들어서며 랜선으로 할 수 있는 게임, 회식, 놀이, 수다 문화 등의 인기가 높아졌습니다. 현재까지는 ZOOM이나 웹엑스 등의 기존 화상채팅 도구를 이용하여 게임을 했습니다. 그러나 미팅 기능에 초점이 맞춰진 어플리케이션에서는 게임 진행에 한계가 있었습니다. 그래서 <strong>"SSAFIA"</strong>는 마피아 게임 기능에 초점을 맞춘 화상 채팅 서비스를 제공하고자 합니다. 

### 게임 규칙

### 게임 FLOW

<br>

## 💡 주요 기능 

<br>

## ⚙️ 기술 스택 

<br>

## 🏑 conventions
### commit message convention
- algular commit message convention 
  - type(scope): short summary
  - type 종류
    - feat : 새로운 기능 추가
    - fix : 버그 수정
    - docs : 문서 관련
    - style : 스타일 변경 (포매팅 수정, 들여쓰기 추가, …)
    - refactor : 코드 리팩토링
    - test : 테스트 관련 코드
    - build : 빌드 관련 파일 수정
    - ci : CI 설정 파일 수정
    - perf : 성능 개선
    - chore : 그 외 자잘한 수정
  - short summary
    - 마침표 쓰지 않기
    - 한글 영문, 모두 가능

### git branch convention

- master - develop - feature
- 개인 개발한 것들은 모두 feature/기능 브랜치에서 수행
- 프론트와 백에서 asignee로 지명된 이들이 심사후 develop으로 merge
- master에 merge할 경우 팀원 모두 모여서 회의 후 진행


<br>

## 배포 방법

WebRTC를 사용하기 때문에 STUN과 TURN 서버가 필요합니다.

오픈 소스 프로젝트인 [coturn](https://github.com/coturn/coturn)을 설치하는 방법은 다음과 같습니다
```sh
sudo apt-get update && sudo apt-get install --no-install-recommends --yes coturn
```

`/etc/default/coturn`의 내용을 다음과 같이 수정합니다.
```sh
TURNSERVER_ENABLED=1
```

`/etc/turnserver.conX`의 내용을 다음과 같이 수정합니다.
```sh
listening-port=3478
tls-listening-port=5349
listening-ip=<EC2의 프라이빗 IPv4 주소>
external-ip=<EC2의 퍼블릭 IPv4 주소>/<EC2의 프라이빗 IPv4 주소>
relay-ip=<EC2의 프라이빗 IPv4 주소>
fingerprint
lt-cred-mech
user=myuser:mypassword
realm=myrealm
log-file=/var/log/turn.log
simple-log
```

coturn을 재기동합니다.
```
sudo service coturn restart
```

다음 명령어를 통해 coturn의 상태를 확인할 수 있습니다.
```
sudo systemctl status coturn
```


프로젝트의 root 위치에서 docker-compose를 실행하면 빌드 및 배포까지 자동으로 이루어집니다.
```sh
docker-compose up -d
```
