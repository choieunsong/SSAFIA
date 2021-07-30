# Frontend

> 최초 생성: 2021.07.22
> 최종 수정: 2021.07.22
>
> 팀장: 유태규
> 팀원: 김영주, 최은송

<br>

### 🔧 개발 환경

- Google Chrome Browser
- Visual Studio Code
- HTML5, CSS3, JavaScript(ES6)
- [Bootstrap5](https://getbootstrap.com/docs/5.0/getting-started/introduction/), [Element Plus 1.0.2-beta.55](https://element-plus.org/#/en-US)
- [Animista](https://animista.net/)
- [Vue.js v3.1.5](https://v3.ko.vuejs.org/)

<br>

### 🔑 실행 방법

- 프로젝트 셋업

```
npm i
```

- 개발자 모드 실행

```
npm run serve_dev
```

- 사용자 모드 실행

```
npm run serve_prod
```

- 빌드 파일(dist) 개발자 환경 변수로 생성

```
npm run build_dev
```

- 빌드 파일(dist) 사용자 환경 변수로 생성

```
npm run build_prod
```

<br>

### 📈 프로젝트 구조

```
- App.vue

- assets
	- fonts
	- images

- router
	- router.js

- store
	- modules
	- index.js
	- mutation-types.js

- common
	- css
		- common.css
	- lib
		- 각종 3rd party library

- views
	- home (홈)
		- Home.vue
		- components
		  - Login.vue
	
	- main (게임)
		- Main.vue
		- components
			- Navbar.vue
			- Player.vue
			- Notification.vue
	
	- room-setting (방 세팅)
		- Room-setting.vue
	
	- nickname (닉네임 설정)
		- Nickname.vue
	
	- error (에러)
		- 404.vue
		- 500.vue
```

<br>

### 📘 관련 문서

- [와이어 프레임](https://www.figma.com/file/D946y8Vykt2jgYXgHfmGDM/%EB%A7%88%ED%94%BC%EC%95%84%EA%B2%8C%EC%9E%84---%EC%99%80%EC%9D%B4%EC%96%B4-%ED%94%84%EB%A0%88%EC%9E%84?node-id=0%3A1)
- [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript)
- [Vue JS Style Guide](https://kr.vuejs.org/v2/style-guide/index.html)

