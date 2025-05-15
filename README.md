# ![TestCoverage](https://img.shields.io/endpoint?url=https://todeveloperdo.github.io/TDD-be/badges/coverage-badge.json&cacheSeconds=30)

# 취준생 개발자를 위한 TodoList

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
- BACKEND : Spring Framework 2.7.13, Kotlin 1.9.24, Mysql, Spring Data Jpa, AWS ec2, Docker, Docker-compose, CI/CD, Nginx, FeignClient   <br>
- APIs : GitHub API, Apple API, OpenAI <br>
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
- 사용자가 입력한 기술스택, 학습기간, 능숙도, 기간에 맞는 커리큘럼을 추천해주어야 한다.
- 생성된 커리큘럼에서 사용자가 선택한 목표들만 할 일에 자동으로 등록을 해야한다.
---
## 참고사항 및 조건
- ReadME에는 오늘 할 일만 작성이 되어야한다.
- 친구가 아닌 사람의 할 일 목록은 볼 수 없다.
- 친구가 되는 조건은 한 쪽만 친추를 걸어서가 아닌 쌍방으로 친추가 되어야한다.
- 생성된 커리큘럼 중 사용자가 선택한 목표만 할 일에 등록되어야한다.
---

## 시연 영상
<details>
<summary>로그인 및 깃허브 연동</summary>

<table>
  <tr>
    <td align="center"><b>로그인 및 깃허브 연동</b></td>
    <td align="center"><b>Repository 생성</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/d0507240-e579-449b-a4dd-de4d64360886" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/191825a9-110d-4d88-a3ff-cc07c3ce26c6" width="180"/></td>
  </tr>
</table>

</details>

<details>
<summary>TODO 관련 기능</summary>

<table>
  <tr>
    <td align="center"><b>TODO 생성</b></td>
    <td align="center"><b>TODO 완료</b></td>
    <td align="center"><b>TODO 진행중</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/81de5c66-7795-419d-b531-7f67dc4ccdef" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/92eef94e-bf70-4ae3-9f87-ca2ddd0a8361" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/372883c3-8708-44e7-8914-3e80683a6089" width="180"/></td>
  </tr>
  <tr>
    <td align="center"><b>TODO 내용 변경</b></td>
    <td align="center"><b>TODO 날짜 변경</b></td>
    <td align="center"><b>TODO 삭제</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/1df9c1e1-10ea-4cb1-83f7-d9d7225ec1ea" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/738c916f-fc46-4500-bd2a-acce1caf821b" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/b49fe43c-e5aa-4e5a-b130-8c50ff2fe3e1" width="180"/></td>
  </tr>
</table>

</details>

<details>
<summary>친구 관련 기능</summary>

<table>
  <tr>
    <td align="center"><b>친구 탐색</b></td>
    <td align="center"><b>친구 프로필(팔로우)</b></td>
    <td align="center"><b>친구 프로필(언팔로우)</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/7aa4c1db-8654-49d7-a921-538f3cda69aa" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/e8a38a63-3d05-43f1-b6c8-ff5649a9da0c" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/9f813148-3d4b-4c76-9493-b952749b593b" width="180"/></td>
  </tr>
</table>

</details>

<details>
<summary>목표 관련 기능</summary>

<table>
  <tr>
    <td align="center"><b>목표 생성</b></td>
    <td align="center"><b>목표 리스트</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/e299da2d-ed91-4c02-8284-4239a348547e" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/d1ebac5d-3593-4a03-83f5-f8a90c6a42a8" width="180"/></td>
  </tr>
</table>

</details>

<details>
<summary>내 정보 및 계정 관리</summary>

<table>
  <tr>
    <td align="center"><b>내 정보</b></td>
    <td align="center"><b>탈퇴</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/300d2f4c-d7a5-41d0-949e-b24ad769e7f7" width="180"/></td>
    <td><img src="https://github.com/user-attachments/assets/61d865e7-6dce-4ef8-9264-e17b57ebe8e5" width="180"/></td>
  </tr>
</table>

</details>

---
## DB구조도
![image](https://github.com/user-attachments/assets/466c871d-1d60-4c96-815c-008dc3c7840c)




---

## System Architecture
<img width="1468" alt="image" src="https://github.com/user-attachments/assets/dabdd7f4-01ea-4193-8b00-4b57518acbed" />



---

## 기능정리
1. 할 일 등록, 조회, 수정, 삭제 기능
2. Git API연동을 통한 ReadME 작성 및 Issue생성
3. 친구 추가를 통한 서로 할 일 조회 기능
4. GitHub 소셜로그인
5. Apple 소셜로그인
6. Push 알림 전송
7. Webhook을 통한 Github Repository와 연동
8. OpenAI를 통한 사용자 맞춤 커리큘럼 생성
