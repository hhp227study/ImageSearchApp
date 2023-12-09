커머스 사전과제: 이미지 검색 앱
=================

사용기술: 
MDC Android
Retrofit2
Hilt
AAC ViewModel
DataBinding
Kotlinx Serialization

Api키: 78fef473a8c0e6578740fd761f29e197

Introduction
------------

앱 아키텍쳐는 여러 정형화된 앱 탬플릿들이 많지만 구글에서 권장하는 방식을 채택하여 설계하였습니다
[참고링크]: https://developer.android.com/topic/architecture
화면 구성은 최신 UIKit인 JetpackCompose를 고려하였지만 익숙하지않아, 기존방식의 MDC Android를 이용하여 작성하였습니다.
섬네일 이미지가 메인이므로 1행당 3개의 아이템을 가지는 그리드 형식의 화면을 구성하였으며 
하단 스크롤을 내리면 페이징 처리가 가능한 api이지만 요구사항에 따로 페이징 처리하라는점이 명시되어있지 않아 생략하였습니다.
이부분은 collections필드를 기준으로 필터가 적용되어 있어 아이템 갯수가 들쭉날쭉해지기때문에 하단 스크롤에 도달해 
페이징 처리하기가 애매해진 부분도 있습니다.
기본적으로 api를 호출하기때문에 네트워크 체크 하는 기능을 구현하였습니다.
처음 api요청 쿼리는 api설명서에 명시되어 있는 설현 키워드를 기본값으로 하였습니다.