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