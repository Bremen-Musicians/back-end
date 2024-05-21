# **서비스 개요**

합주를 해보신 적 있으신가요? 합주를 해본 사람들은 다른 사람과의 연주가 잘 맞아떨어지는 순간에 엄청난 카타르시스를 느낀다고 합니다.

하지만 합주를 좋아하더라도 다양한 문제로 인해 합주를 하기 어려운 상황에 마주칠 수 있습니다. 

예를 들어, 악기를 가지고 이동하는 것이 어려워 장소에 있는 악기를 사용해야 하거나, 합주를 위한 인원을 구해야 하는 등의 어려움이 있습니다.

이러한 문제점을 해결하고자 브레멘 음악대처럼 서로의 음을 함께 쌓아올려 아름다운 연주를 만들어가자는 의미에서 **브레멘 음악대**라는 서비스를 개발하게 되었습니다.

# **주요 기능**

## 합주 동영상 생성

```markdown
- 찍은 동영상 파일을 합치기
- 서비스를 통해 동영상 촬영 + 촬영된 영상 업로드
- 메트로놈 제공
```

## **소셜 기능**

```markdown
- 로그인, 로그아웃, 회원가입, 회원 정보 수정, 회원탈퇴
- 동영상 좋아요
- 동영상 댓글
```

## 검색 기능

```markdown
- 악기 다중 선택 가능
- 곡 제목, 동영상 제목 / 아이디로 검색 가능
```

## 챌린지

```markdown
- 매주 한 곡을 미션곡으로 선정하여 관리자가 챌린지를 업로드
- 베스트 영상 선정 기준: 좋아요수와 조회수를 반영하여 선정 영상
```

# 화면소개

![image (1)](https://github.com/SSAFY-A104/back-end/assets/42714724/e1221733-a10c-48c2-b6bf-bbebd20f9a77)
![image (2)](https://github.com/SSAFY-A104/back-end/assets/42714724/6c9caf05-164c-4327-aad8-a637ba5dd849)

# 아키텍쳐

![image (3)](https://github.com/SSAFY-A104/back-end/assets/42714724/d1776d93-961b-40ec-8262-aae4c1ca8e14)


# 기술스택

| Infra | 버전 |
| --- | --- |
| AWS EC2 | Ubuntu 20.04.6 LTS |
| Docker | 25.0.4 |
| Nginx | 1.25.4 |
| Jenkins | 2.440.1 |

| Back End | 버전 |
| --- | --- |
| Java | 17 (Zulu) |
| Spring Boot | 3.2.3 (Gradle) |
| Spring Security | 6 |
| MariaDB | 10.11.6 |
| JPA | 3.1.0 |
| Redis | 7.2.4 |
| Lombok | 1.18.22 |
| Springdoc | 2.0.2 |

# 팀원

|[신온유]([https://github.com/tlsdhsdb](https://github.com/tlsdhsdb))|[전경향]([https://github.com/HappyHyang](https://github.com/HappyHyang))|
|---|---|
|<img style="width:100px" src = "https://avatars.githubusercontent.com/tlsdhsdb"/>|<img style="width:100px" src = "https://avatars.githubusercontent.com/HappyHyang"/>|
|유저,알림|영상 게시글,챌린지|
