# 어머이건사야해(EOISA) - Spring Boot

#Spring Boot #Spring Security #Jsoup #Oracle #MyBatis #Thymeleaf #jQuery #Bootstrap #OAuth #PWA #AWS #Firebase Cloud Messaging

* https://eoisa.ml (현재는 운영되고 있지 않습니다)
* 온라인 커뮤니티 각지의 핫딜 정보를 수집(크롤링)하여 큐레이팅하는 웹 사이트 '**알구몬**'을 웹 개발 학습 목적으로 클론 코딩한 프로젝트입니다.
* 기본적인 UI/UX 및 기획 측면에서는 모방하되, 그 이외의 모든 기술적인 부분은 스스로 학습하며 직접 구현하였습니다.
* 다만, Mock data 구축을 위해 알구몬 페이지를 `Jsoup`으로 스크래핑하는 기능이 포함되어 있습니다.
* 기존의 프로젝트(https://github.com/isolet0722/EOISA) 를 `Spring Boot`로 마이그레이션 하였습니다.
* 자체 로그인 및 `OAuth` 소셜 로그인(`Naver`, `Kakao`) 기능을 제공합니다.
* `Spring Security`를 활용하여 기본적인 로그인, 비밀번호 암호화, 접근 권한 제어 기능을 구현하였습니다.
* `PWA` + `FCM`을 활용한 `Push notification`, `A2HS` 기능을 구현하였습니다.
