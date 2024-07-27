# 취준 개발자를 위한 TodoList

## 팀원 구성
<div align="center">

<div align="center">

| **이준석** |
| :------: | 
| [<img src="https://github.com/CafeCheckin/CafeCheckin/assets/56196986/422a81d3-b0b7-4b85-af31-a42a3c23c771" height=150 width=150> <br/> @JunRock](https://github.com/JunRock) |

</div>
</div>
<br>

## 개발 환경
- BACKEND : Spring Framework, Kotlin 1.9.24, Mysql, Spring Data Jpa, AWS ec2, Docker, Docker-compose, CI/CD, Nginx, FeignClient   <br>
- APIs : GitHub API, Apple API <br>
- 버전 및 이슈관리 : Github, Github actions   <br>
---
## 주제
- 취준 개발자를 위한 TodoList서비스
---
## 요구사항
- 사용자가 원하는 요일에 할 일을 등록할 수 있어야한다.
- 등록된 할 일을 수정, 삭제 등을 할 수 있어야한다.
- 사용자가 본인의 깃에 Repository를 등록할 수 있어야한다.
- 할 일이 등록되면 연동된 사용자 Git Repository의 ReadME와 Issue에 할 일이 등록이 되야한다.
- 할 일을 완료시 ReadME파일을 수정하고 Issue는 Close가 되도록 한다.
- 친구 추가를 통해 친구의 할 일을 확인할 수 있어야한다.
- 매일 자정이 넘으면 ReadME파일을 해당 요일로 변경하고 해당요일에 할 일이 있다면 자동으로 작성되게 하여야한다.
- 할 일이 아직 남아있는 경우 사용자에게 푸시알림을 전송해야한다.

## 참고사항 및 조건
- ReadME에는 오늘 할 일만 작성이 되어야한다.
- 친구가 아닌 사람의 할 일 목록은 볼 수 없다.
- 친구가 되는 조건은 한 쪽만 친추를 걸어서가 아닌 쌍방으로 친추가 되어야한다.
---

## DB구조도


---

## System Architecture



---

## 기능정리
1. 할 일 등록, 조회, 수정, 삭제 기능
2. Git API연동을 통한 ReadME 작성 및 Issue생성
3. 친구 추가를 통한 서로 할 일 조회 기능
4. GitHub 소셜로그인
5. 푸시 알림 전송
