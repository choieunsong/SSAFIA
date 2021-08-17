# 화상 마피아

## 프로젝트 소개

## 구현 기능 목록

## conventions

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
