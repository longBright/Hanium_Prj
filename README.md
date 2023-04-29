# 마스크 착용 & 추천 서비스를 제공하는 이 시대에 딱 맞는 지능형 앱
얼굴형, 퍼스널 컬러, 날씨별 마스크를 추천하고, 가상착용 기술을 겸비한 개인 맞춤형 마스크 추천 및 관리 서비스

## 1. 개발환경 및 사용 라이브러리
OS : Window 10, Ubuntu 22.04 LTS (AWS EC2 Server)
Language : Kotlin, Python
Framework : Android
DBMS : MySQL

## 2. 구성도
![image](https://user-images.githubusercontent.com/74171272/235288327-4be73e4c-9b51-4e00-a607-0552cbb0825a.png)

## 3. 주요 기능
### 1) 얼굴형 분류 및 퍼스널 컬러 진단
- 얼굴형 분류 : MobileNetV3 를 기반으로 학습시킨 얼굴형 분류 딥러닝 모델을 통하여 사용자의 얼굴형을 자동으로 분류
- 퍼스널 컬러 진단 : K-Means 알고리즘을 통해 퍼스널 컬러를 진단하는 Flask 서버를 통하여 사용자의 퍼스널 컬러를 자동으로 진단

### 2) 마스크 유형 및 색상 추천
- 마스크 유형 추천 : MLKit 와 얼굴형 분류 딥러닝 모델을 사용하여 사용자 얼굴형에 가장 잘 어울리는 마스크 유형 추천
- 마스크 색상 추천 : 진단된 퍼스널 컬러를 바탕으로 각 퍼스널 컬러에 맞는 마스크 색상을 추천 (설명 포함)

![image](https://user-images.githubusercontent.com/74171272/235288649-1f211959-1938-4839-acd0-a755c1d9778f.png)

### 3) 마스크 등급 추천
- 기상 환경에 따른 마스크 추천 : 공공 데이터 포탈의 기상자료 API 를 활용하여 미세먼지 농도에 따른 마스크 등급 추천


![image](https://user-images.githubusercontent.com/74171272/235288644-39ca0fc0-4969-4365-b925-c6974c0cd77c.png)

### 4) 마스크 가상 착용
- AR Core 와 ML Kit 를 사용해 사용자가 선택한 마스크를 가상 착용


![image](https://user-images.githubusercontent.com/74171272/235288641-2cce8685-d6bc-43fb-ae94-bb750d3bb40b.png)

## 4. 결과
- 2022 한이음 ICT 공모전 입선 수상
- 한이음 공모전 수행 기간 동안 배포

## 상세 보고서 및 설계서
https://github.com/longBright/Hanium_Prj/tree/main/01_Documents/01_%EB%B3%B4%EA%B3%A0%EC%84%9C/05_%EA%B0%9C%EB%B0%9C%EB%B3%B4%EA%B3%A0(2%EC%B0%A8)
